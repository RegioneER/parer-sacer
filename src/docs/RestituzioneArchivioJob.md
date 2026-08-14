# Job Restituzione Archivio

## Scopo

Il job di Restituzione Archivio ha il compito di evadere le richieste di restituzione degli AIP, recuperando i pacchetti richiesti, organizzandoli nell'area filesystem di destinazione e aggiornando lo stato sia delle richieste sia dei singoli elementi estratti.

La logica principale e' implementata nel job di orchestrazione, con il supporto dei componenti helper per l'accesso ai dati, la lettura dei parametri applicativi e il trasporto dei DTO di lavorazione.

## Responsabilita funzionali

Il job gestisce due macro-attivita:

1. presa in carico delle richieste gia' restituite, con eventuale svuotamento dell'area FTP quando tutte le strutture coinvolte risultano abilitate alla chiusura del processo;
2. evasione delle richieste in stato `IN_ATTESA_ESTRAZIONE` oppure `ESTRAZIONE_IN_CORSO`, tramite recupero dell'AIP e copia del pacchetto nella directory di output.

L'area di destinazione e' costruita sotto la root configurata dal parametro applicativo `ROOT_FOLDER_EC_RA` e viene organizzata per:

- ente convenzionato;
- ente;
- struttura;
- tipologia oggetto;
- anno della unita documentaria.

## Componenti coinvolti

- Job di orchestrazione: gestisce dispatch delle richieste, stato del processo e pipeline di estrazione.
- Helper di calcolo estrazione: esegue l'accesso ai dati di richieste, AIP, UD, strutture e query di selezione.
- Helper di configurazione: legge i parametri applicativi, inclusi quelli opzionali usati per il parallelismo.
- DTO di lavorazione: rappresenta gli oggetti da estrarre e, per le UD, mantiene anche l'anno `aaKeyUnitaDoc`.
- Costanti applicative: definiscono i parametri utilizzati dal job.

## Flusso di esecuzione

### 1. Inizializzazione

All'avvio il job:

- legge la root di output `ROOT_FOLDER_EC_RA`;
- legge il limite massimo globale di AIP processabili per esecuzione tramite `MAX_UD2PROC_RA`;
- legge il limite massimo di file per cartella tramite `NUM_MAX_FILE_FOLDER_RA`;
- verifica l'esistenza e la disponibilita della directory radice.

Se la directory non e' accessibile, l'elaborazione viene interrotta con errore.

### 2. Chiusura richieste gia' restituite

Il job recupera le richieste in stato `RESTITUITO` con flag `flSvuotaFtp = 1` e, per ognuna, verifica che tutte le strutture coinvolte abbiano `FlArchivioRestituito = 1`.

Solo in questo caso:

- svuota le cartelle dell'area FTP relative alla richiesta;
- azzera il flag `flSvuotaFtp`;
- produce, dove previsto, i dati per il DataMart.

### 3. Selezione richieste da elaborare

Se il budget massimo di AIP per esecuzione non e' stato esaurito, il job recupera le richieste in stato `IN_ATTESA_ESTRAZIONE` o `ESTRAZIONE_IN_CORSO` e le passa al dispatcher interno.

La lavorazione vera e propria parte da `processRichiesteRaDaElab(...)`.

## Strategia di parallelismo

L'ottimizzazione principale introdotta sul job e' il passaggio da una esecuzione prevalentemente seriale a un parallelismo controllato su piu livelli.

### Parallelismo tra richieste

Il primo livello di parallelismo e' tra richieste RA diverse.

Il metodo `buildRichiesteBatch(...)` costruisce batch di richieste indipendenti, con due regole:

- il numero massimo di richieste concorrenti e' limitato dal parametro `MAX_PARALLEL_RICHIESTE_RA`;
- nello stesso batch non possono comparire due richieste della stessa struttura.

Questa seconda regola evita contese su dati e cartelle condivise della stessa struttura.

Ogni richiesta del batch viene poi eseguita in modo asincrono tramite `manageRichiestaEstrazioneJobAsync(...)`.

### Mutua esclusione per struttura

Quando una richiesta viene presa in carico, il job mantiene la serializzazione solo all'interno della stessa struttura.

Il metodo `elaboraEstrazioniInCorsoStessaStruttura(...)` intercetta eventuali altre richieste gia' in `ESTRAZIONE_IN_CORSO` sulla stessa struttura e le riporta in `IN_ATTESA_ESTRAZIONE`.

Questo consente di:

- far avanzare in parallelo richieste di strutture diverse;
- continuare a proteggere i casi realmente conflittuali sulla stessa struttura.

### Parallelismo interno alla richiesta

All'interno della singola richiesta, gli oggetti da elaborare vengono recuperati tramite `retrieveAipUdSerFascByRichiesta(...)`.

Per le unita documentarie viene portato nel DTO anche il campo `aaKeyUnitaDoc`, che rappresenta l'anno e che viene usato come chiave di partizionamento.

Il metodo `buildUdSerFascObjGroups(...)` suddivide quindi il carico in gruppi logici. Per le UD, il raggruppamento e' fatto per anno, in modo che ogni gruppo corrisponda a una cartella di output distinta.

I gruppi vengono eseguiti in parallelo fino al limite definito da `MAX_PARALLEL_ANNI_RA`.

### Chunking intra-anno

Esiste un caso degenerato in cui quasi tutte le UD di una richiesta appartengono allo stesso anno. In questo scenario il solo partizionamento per anno non produce benefici apprezzabili, perche' il lavoro ricade quasi tutto in un unico gruppo.

Per questo motivo il job introduce anche un parallelismo intra-anno:

- se il batch corrente contiene un solo gruppo anno e il limite di parallelismo lo consente;
- il gruppo viene ulteriormente suddiviso in chunk tramite `buildUdSerFascObjChunks(...)`;
- i chunk vengono eseguiti in parallelo usando worker asincroni distinti.

Questa strategia consente di riutilizzare il budget di parallelismo anche quando il carico e' fortemente concentrato su una sola annualita.

## Coordinamento sul filesystem

Il principale punto critico del parallelismo intra-richiesta e' la scelta della cartella di destinazione.

Poiche' piu worker possono scrivere contemporaneamente nell'area relativa allo stesso anno, il job utilizza la classe interna `ProcessingContext`, che centralizza due informazioni:

- i progressivi delle cartelle generate per uno stesso anno;
- il numero di file gia' prenotati per ogni cartella.

Il metodo sincronizzato `reserveDestinationFolder(...)` garantisce che:

- due worker concorrenti non prenotino la stessa cartella in modo incoerente;
- il limite `NUM_MAX_FILE_FOLDER_RA` venga rispettato;
- la creazione delle cartelle progressive sia consistente anche in presenza di chunk paralleli.

In questo modo il parallelismo non introduce collisioni sulla struttura fisica di output.

## Pipeline di elaborazione della singola UD

Per ogni elemento di tipo `UNI_DOC`, il job esegue i seguenti passi:

1. recupero della richiesta, del record `ARO_AIP_RESTITUZIONE_ARCHIVIO`, dell'indice AIP e della UD;
2. acquisizione del lock pessimista su UD e indice AIP;
3. calcolo della directory di output in base a ente, struttura, tipologia e anno;
4. prenotazione atomica della cartella di destinazione;
5. recupero del pacchetto AIP tramite `RecuperoWeb`;
6. copia del file nell'area di output;
7. aggiornamento dello stato del record AIP in base all'esito.

L'aggiornamento dello stato del singolo elemento avviene tramite:

- `setStatoAipRestArchivio(...)` in caso di esito positivo;
- `setErrore(...)` in caso di warning o errore.

## Gestione dello stato della richiesta

Lo stato della richiesta viene aggiornato in piu punti del flusso.

Quando inizia la lavorazione, la richiesta viene portata in `ESTRAZIONE_IN_CORSO` tramite `setStatoRichiestaRaAtomic(...)`.

A fine elaborazione, se la richiesta non e' stata annullata, il job ricalcola lo stato finale usando la vista `AroVChkAipRestArchUd`:

- `ESTRATTO` se tutti gli elementi risultano estratti;
- `ESTRAZIONE_IN_CORSO` se rimangono elementi da elaborare;
- `ERRORE` se almeno un elemento e' andato in errore.

## Gestione annullamento richiesta

Durante l'elaborazione dei gruppi o dei chunk, il job controlla periodicamente se la richiesta e' stata annullata.

Il polling e' regolato dalla costante `ANNULLAMENTO_CHECK_INTERVAL`, che nel codice attuale vale `100`.

Questo approccio riduce il costo del controllo continuo, ma mantiene comunque una finestra di reazione accettabile. Se una richiesta risulta annullata, il worker interrompe la propria esecuzione e la richiesta non viene marcata come completata.

## Parametri applicativi

I parametri applicativi utilizzati dal job sono:

- `ROOT_FOLDER_EC_RA`: root dell'area filesystem di restituzione;
- `MAX_UD2PROC_RA`: numero massimo di AIP processabili in una singola esecuzione del job;
- `NUM_MAX_FILE_FOLDER_RA`: numero massimo di file ammessi in una cartella fisica;
- `MAX_PARALLEL_RICHIESTE_RA`: numero massimo di richieste RA eseguibili in parallelo su strutture diverse;
- `MAX_PARALLEL_ANNI_RA`: numero massimo di gruppi o chunk paralleli all'interno della stessa richiesta.

I parametri di parallelismo vengono letti tramite `getValoreParamApplicByApplicIfPresent(...)`, in modo da non provocare rollback se non presenti.

Se il parametro e' assente o non valido, vengono applicati fallback interni:

- `DEFAULT_MAX_PARALLEL_RICHIESTE_RA = 1`;
- `DEFAULT_MAX_PARALLEL_ANNI_RA = 1`.

## Gestione errori e resilienza

Il recupero dell'AIP produce tre possibili severita:

- `OK`: il file viene copiato e l'AIP viene marcato `ESTRATTO`;
- `WARNING`: l'AIP viene marcato `ERRORE` con il messaggio restituito;
- `ERROR`: l'AIP viene marcato `ERRORE` con logging a livello error.

Le eccezioni generate dai worker asincroni vengono intercettate dal chiamante tramite `ExecutionException`. In questo caso il job registra l'anomalia e passa alla richiesta successiva, evitando che un errore locale comprometta l'intera schedulazione.

## Benefici introdotti dalle ottimizzazioni

Le modifiche introdotte sul job producono i seguenti benefici tecnici:

- aumento del throughput complessivo, grazie alla lavorazione concorrente di richieste su strutture diverse;
- riduzione dei tempi di evasione delle singole richieste grandi, grazie al parallelismo interno per anno;
- miglior comportamento nei casi con una sola annualita dominante, grazie al chunking intra-anno;
- prevenzione delle collisioni sul filesystem, grazie alla prenotazione sincronizzata delle cartelle;
- maggiore robustezza operativa, grazie alla lettura opzionale dei parametri di tuning;
- mantenimento della coerenza applicativa, grazie alla serializzazione limitata ai soli casi realmente conflittuali.

## Considerazioni finali

Il job Restituzione Archivio e' oggi strutturato come una pipeline concorrente controllata. Il parallelismo introdotto non e' indiscriminato, ma e' progettato per rispettare i vincoli del dominio applicativo, i limiti del filesystem di output e la coerenza degli stati su database.

L'efficacia reale dell'ottimizzazione dipende comunque anche da fattori infrastrutturali, in particolare:

- dimensionamento del pool EJB asincrono del container;
- prestazioni del filesystem di destinazione;
- tempi del servizio di recupero AIP;
- livello di contesa sui lock applicati alle entita coinvolte.

In termini architetturali, il risultato ottenuto e' il passaggio da una esecuzione quasi seriale a un modello di parallelismo controllato su tre livelli: tra richieste, tra gruppi anno e, nei casi piu pesanti, all'interno dello stesso gruppo anno.