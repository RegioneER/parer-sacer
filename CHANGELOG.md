## 10.9.1 (24-06-2025)

### Bugfix: 2
- [#38323](https://parermine.regione.emilia-romagna.it/issues/38323) Correzione join di allineamento organizzazioni
- [#38296](https://parermine.regione.emilia-romagna.it/issues/38296) Correzione dell'errore critico quando di effettuano ricerche sui criteri di raggruppamento

## 10.9.0 (11-06-2025)

### Bugfix: 4
- [#38184](https://parermine.regione.emilia-romagna.it/issues/38184) Correzione controllo gestione formati ammessi 
- [#38030](https://parermine.regione.emilia-romagna.it/issues/38030) Inserire i default corretti per le security policy
- [#38025](https://parermine.regione.emilia-romagna.it/issues/38025) Correzione del messaggio di errore relativo all'impossibilità di aggiungere un documento a una UD in stato IN_ARCHIVIO
- [#36875](https://parermine.regione.emilia-romagna.it/issues/36875) Correzione problema legato all'annullamento di una UD contenuta in un elenco con tutte UD annullate

### Novità: 10
- [#38027](https://parermine.regione.emilia-romagna.it/issues/38027) Rimozione all'interno del codice dei riferimenti di utilizzo della tabella VRS_CONENUTO_FILE sostituita dalla tabella VRS_CONTENUTO_FILE_KO
- [#38023](https://parermine.regione.emilia-romagna.it/issues/38023) Ottimizzazione ricerca UD derivanti da versamenti falliti con filtri su classe errore
- [#37955](https://parermine.regione.emilia-romagna.it/issues/37955) Sostituzione partizionamenti manuali per  tabelle ELV_FILE_ELENCO_VERS e tabelle correlate by reference.
- [#37769](https://parermine.regione.emilia-romagna.it/issues/37769)  Ottimizzazioni e refactor codice per recupero debito tecnico (SonarQube)
- [#37218](https://parermine.regione.emilia-romagna.it/issues/37218) Aggiornamento librerie ESAPI (OWASP) e SAML + Spring Security 5
- [#35549](https://parermine.regione.emilia-romagna.it/issues/35549) Configurazione modulo jaxp-jdk per JBoss 7 per JDK 11
- [#34792](https://parermine.regione.emilia-romagna.it/issues/34792) Aggiunta nuova tabella nel disciplinare - Utenti automi versatori
- [#34195](https://parermine.regione.emilia-romagna.it/issues/34195) Funzione massiva per riportare indietro lo stato di un elenco per consentire la firma dell'AIP
- [#34194](https://parermine.regione.emilia-romagna.it/issues/34194) Modifiche alla pagina Ricerca elenchi di versamento unità documentarie per individuare gli elenchi da rimandare indietro per la firma dell'AIP
- [#34000](https://parermine.regione.emilia-romagna.it/issues/34000) Migrazione alle nuove dipendenze / pattern legate a xecers, xalan, jaxb, ecc

## 10.8.1 (22-05-2025)

### Bugfix: 8
- [#38165](https://parermine.regione.emilia-romagna.it/issues/38165) Correzione visualizzazione data contenuto dell'archivio nella home page
- [#37919](https://parermine.regione.emilia-romagna.it/issues/37919) Fascicolo annullato visualizzabile dal dettaglio UD
- [#37730](https://parermine.regione.emilia-romagna.it/issues/37730) Correzione salvataggio calcolo UD, DOC e COMPONENTI annullati in calcolo contenuto Sacer
- [#37703](https://parermine.regione.emilia-romagna.it/issues/37703) Correzione dell'utilizzo del carattere underscore nel filtro Numero contiene
- [#35396](https://parermine.regione.emilia-romagna.it/issues/35396) Correzione ricerca criteri raggruppamento UD 
- [#35254](https://parermine.regione.emilia-romagna.it/issues/35254) Correzione delle anomalie nella fase di marcatura temporale embedded negli elenchi indici aip UD
- [#34849](https://parermine.regione.emilia-romagna.it/issues/34849) Correzione funzionamento parametro tipo firma
- [#33816](https://parermine.regione.emilia-romagna.it/issues/33816) Monitoraggio: integrazione menù filtro "Sottoclasse errore" con UD-014

## 10.8.0 (07-05-2025)

### Bugfix: 3
- [#37373](https://parermine.regione.emilia-romagna.it/issues/37373) Adeguamento procedura di calcolo contenuto sacer e calcolo consistenza
- [#37354](https://parermine.regione.emilia-romagna.it/issues/37354) Rimozione controlli su partizionamenti logici per le sessioni di versamento errate a livello di fascicoli
- [#37222](https://parermine.regione.emilia-romagna.it/issues/37222) Correzione Chiave Object Storage su SACER

### Novità: 5
- [#37737](https://parermine.regione.emilia-romagna.it/issues/37737) Ottimizzazione performance ricerca elenchi di versamento per chiave UD
- [#37576](https://parermine.regione.emilia-romagna.it/issues/37576) SACER - Refactor migrazione a GCP mantenendo S3
- [#37041](https://parermine.regione.emilia-romagna.it/issues/37041) Implementazione recupero indici elenchi di versamento firmati da object storage
- [#35333](https://parermine.regione.emilia-romagna.it/issues/35333) Ottimizzazione navigazione dei risultati di ricerca degli elenchi di versamento e apertura del dettaglio degli stessi
- [#34839](https://parermine.regione.emilia-romagna.it/issues/34839) Migliorie al log del processo di conservazione delle unità documentarie

## 10.7.0 (31-03-2025)

### Bugfix: 1
- [#37610](https://parermine.regione.emilia-romagna.it/issues/37610) Correzione per la mancata chiusura dei file e protocollo NFS

### Novità: 1
- [#37619](https://parermine.regione.emilia-romagna.it/issues/37619) Sostituzione partizionamenti manuale con partizionamento automatico tabella ARO_FILE_VER_INDICE_AIP_UD

## 10.6.1 (24-03-2025)

### Bugfix: 5
- [#37512](https://parermine.regione.emilia-romagna.it/issues/37512) Correzione query estrazione formati ammessi tipo componete 
- [#37172](https://parermine.regione.emilia-romagna.it/issues/37172) Modifica modello di partizionamento tabella "prototipo"per la ricerca delle UD tramite dati specifici
- [#35548](https://parermine.regione.emilia-romagna.it/issues/35548) Correzione dell'errore sulla fine validità dei profili nel dettaglio fascicolo
- [#35187](https://parermine.regione.emilia-romagna.it/issues/35187) Correzione generazione indice AIP da validazione fascicolo con UD in volume di conservazione
- [#34606](https://parermine.regione.emilia-romagna.it/issues/34606) Correzione nel naming dei file negli AIP Unisincro 1.0

## 10.6.0 (06-02-2025)

### Bugfix: 3
- [#35782](https://parermine.regione.emilia-romagna.it/issues/35782) Correzione gestione transazione cancellazione elenco di versamento durante l'annullamento UD 
- [#35555](https://parermine.regione.emilia-romagna.it/issues/35555) Impostazione data corretta nella home della struttura in "Contenuto dell'archivio al" 
- [#34748](https://parermine.regione.emilia-romagna.it/issues/34748) Correzione warning: Illegal reflective access

### Novità: 1
- [#32643](https://parermine.regione.emilia-romagna.it/issues/32643) Realizzazione pagina di ricerca ud tramite dati specifici - prototipo per valutazione performance di ricerca

## 10.5.0 (20-01-2025)

### Bugfix: 4
- [#34880](https://parermine.regione.emilia-romagna.it/issues/34880) Correzione scarico file sbustati
- [#34838](https://parermine.regione.emilia-romagna.it/issues/34838) Correzione composizione AIP Unisincro 1 in caso di documenti aggiunti successivamente (caso evidenze di conservazione)
- [#34796](https://parermine.regione.emilia-romagna.it/issues/34796) Correzione con eliminazione gestione partizionamenti logici su gestione serie e fascicoli
- [#34601](https://parermine.regione.emilia-romagna.it/issues/34601) Correzione problema collisione urn Documenti

### Novità: 3
- [#34698](https://parermine.regione.emilia-romagna.it/issues/34698) Rimozione dipendenza diretta a modelli EIDAS (libreria DSS)
- [#33126](https://parermine.regione.emilia-romagna.it/issues/33126) Aggiornamento alle ultimi versioni librerie jakarata-ee8 per jboss 7.4
- [#32991](https://parermine.regione.emilia-romagna.it/issues/32991) Versamento fascicolo: eliminazione delle retrocompatibilità nel codice e nella configurazione

## 10.4.0 (09-12-2024)

### Bugfix: 6
- [#34696](https://parermine.regione.emilia-romagna.it/issues/34696) Correzione funzione di eliminazione xsd sistema di migrazione  - Errore 500 
- [#34605](https://parermine.regione.emilia-romagna.it/issues/34605) Correzione composizione AIP Unisincro 1 in caso di documenti aggiunti successivamente (caso componenti)
- [#34600](https://parermine.regione.emilia-romagna.it/issues/34600) Correzione stato restituzione archivio
- [#34492](https://parermine.regione.emilia-romagna.it/issues/34492) Fix recupero da object storage degli Indici Aip Unisincro 1.0 nell'AIP
- [#34038](https://parermine.regione.emilia-romagna.it/issues/34038) Correzione data ultimo versamento estratta per tipologia unità documentaria versata da più automi
- [#34027](https://parermine.regione.emilia-romagna.it/issues/34027) Correzione dell'errore nella modifica del formato numero nel periodo di validità di un tipo fascicolo

### Novità: 4
- [#34435](https://parermine.regione.emilia-romagna.it/issues/34435) Estensione servizio di recupero stato di conservazione per includere il log del processo di conservazione
- [#34239](https://parermine.regione.emilia-romagna.it/issues/34239) Estensione servizio per recupero file sbustati
- [#34199](https://parermine.regione.emilia-romagna.it/issues/34199) Nuova gestione per rapprentazione completa su base dati del seriale del certificato (CA, CRL e OCSP)
- [#31162](https://parermine.regione.emilia-romagna.it/issues/31162) Log del processo di conservazione delle unità documentarie

## 10.3.1 (28-11-2024)

### Bugfix: 1
- [#34747](https://parermine.regione.emilia-romagna.it/issues/34747) Introduzione log su parametro header per servizi di recupero con certificato

## 10.3.0 (11-11-2024)

### Bugfix: 2
- [#34366](https://parermine.regione.emilia-romagna.it/issues/34366) Correzione controllo esistenza password nulla nel servizi di recupero
- [#34301](https://parermine.regione.emilia-romagna.it/issues/34301) Correzione della chiamata al parametro TIMEOUT_CALCOLO_SERVIZI_EROGATI

### Novità: 12
- [#34489](https://parermine.regione.emilia-romagna.it/issues/34489) Sostituzione partizionamenti by_list e subpartizionamenti  by_list con partizionamenti automatici - tabelle ARO_CNTENUTO_COMP
- [#34433](https://parermine.regione.emilia-romagna.it/issues/34433) Sostituzione partizionamenti  by_range in sacer_LOG con partizionamenti automatici con interval
- [#34321](https://parermine.regione.emilia-romagna.it/issues/34321) Sostituzione partizionamenti by_list/by_range in partizionamenti automatici - tabelle SERIE
- [#33897](https://parermine.regione.emilia-romagna.it/issues/33897) Eliminazione controllo LOGINNAME/PASSWORD nella chiamata ai servizi di recupero con certificato
- [#32722](https://parermine.regione.emilia-romagna.it/issues/32722) Modifiche al monitoraggio Riepilogo versamenti fascicoli 
- [#32535](https://parermine.regione.emilia-romagna.it/issues/32535) Aggiornamento delle logiche di creazione della richiesta di restituzione archivio.
- [#32490](https://parermine.regione.emilia-romagna.it/issues/32490) Riduzione tempi di apertura della pagina di dettaglio della richiesta di estrazione
- [#31668](https://parermine.regione.emilia-romagna.it/issues/31668) Visualizzazione dei dati relativi alle sessioni errate e fallite cancellate
- [#30400](https://parermine.regione.emilia-romagna.it/issues/30400) Salvataggio diretto su object storage dell'indice AIP delle serie 
- [#27019](https://parermine.regione.emilia-romagna.it/issues/27019) Ridimensionamento di tutte le colonne che contengono esiti di errore.
- [#23686](https://parermine.regione.emilia-romagna.it/issues/23686) Visualizzazione chiave normalizzata fascicolo
- [#15967](https://parermine.regione.emilia-romagna.it/issues/15967) Attivazione della firma Xades e XadesT

## 10.2.1 (18-09-2024)

### Bugfix: 4
- [#34039](https://parermine.regione.emilia-romagna.it/issues/34039) Correzione job evasione richieste restituzione archivio
- [#34023](https://parermine.regione.emilia-romagna.it/issues/34023) Correzione dell'errore nel salvataggio del periodo di validità di un tipo fascicolo quando si modifica un parametro
- [#33957](https://parermine.regione.emilia-romagna.it/issues/33957) Correzione in fase di eliminazione file e/o directory temporenee nei casi di servizi recupero e monitoraggio
- [#33851](https://parermine.regione.emilia-romagna.it/issues/33851) Importazione parametri struttura: Import tipo fascicolo errore su ambiente diverso dall'export

## 10.2.0 (12-09-2024)

### Bugfix: 9
- [#33854](https://parermine.regione.emilia-romagna.it/issues/33854) Modifica condizione di controllo su modifica/cancellazione periodo di validità registro
- [#33801](https://parermine.regione.emilia-romagna.it/issues/33801) Correzione casting sul parametro aa_unita_doc_registro.
- [#33782](https://parermine.regione.emilia-romagna.it/issues/33782) Correzione descrizione struttura mostrata nell'intestazione a sinistra all'apertura di organizzazione da Siam
- [#33298](https://parermine.regione.emilia-romagna.it/issues/33298) Precompilazione di ambiente, ente e struttura nella pagina RIEPILOGO AGGIORNAMENTO METADATI
- [#33252](https://parermine.regione.emilia-romagna.it/issues/33252) Correzione dei valori su RIEPILOGO AGGIORNAMENTO METADATI
- [#33157](https://parermine.regione.emilia-romagna.it/issues/33157) Correzione dell'errore nella creazione di un criterio di raggruppamento
- [#33057](https://parermine.regione.emilia-romagna.it/issues/33057) Importazione parametri struttura: correzione bug che impedisce di importare il profilo normativo
- [#32638](https://parermine.regione.emilia-romagna.it/issues/32638) Correzione alla ricerca fascicoli per la selezione del modello xsd di fascicolo
- [#32589](https://parermine.regione.emilia-romagna.it/issues/32589) Restituzione archivio: Correzione contatore dimensione file estratti

### Novità: 3
- [#33156](https://parermine.regione.emilia-romagna.it/issues/33156) Aumento della capacità di upload di lab
- [#32249](https://parermine.regione.emilia-romagna.it/issues/32249) Funzione per riportare indietro lo stato di un elenco per consentire la firma dell'AIP
- [#31352](https://parermine.regione.emilia-romagna.it/issues/31352) Modifiche alla pagina Gestione richieste di restituzione archivio

## 10.1.0 (20-08-2024)

### Bugfix: 1
- [#33297](https://parermine.regione.emilia-romagna.it/issues/33297) Recupero da object storage degli Indici Aip Unisincro 1.0 nell'AIP

### Novità: 1
- [#33069](https://parermine.regione.emilia-romagna.it/issues/33069) Aggiornamento librerie obsolete 2024

## 10.0.0 (13-08-2024)

### Novità: 1
- [#30802](https://parermine.regione.emilia-romagna.it/issues/30802) Aggiornamento a Java 11

## 9.4.0 (17-07-2024)

### Bugfix: 3
- [#32985](https://parermine.regione.emilia-romagna.it/issues/32985) Recupero dei profili del fascicolo da object storage nella creazione dell'indici AIP dei fascicoli
- [#32546](https://parermine.regione.emilia-romagna.it/issues/32546) Introduzione constraint inter schema SACER (aro_unita_doc) e SACER_IAM(usr_user
- [#32341](https://parermine.regione.emilia-romagna.it/issues/32341) Risoluzione problema relativo agli indici aip delle aggregazioni nell'AIP dell'UD

### Novità: 14
- [#32930](https://parermine.regione.emilia-romagna.it/issues/32930) Ottimizzazione performance monitoraggio riepilogo versamenti sintetico
- [#32818](https://parermine.regione.emilia-romagna.it/issues/32818) Modifica modello CRYPTO response
- [#32648](https://parermine.regione.emilia-romagna.it/issues/32648) Associazione tra parametri e versione dell'applicazione
- [#32529](https://parermine.regione.emilia-romagna.it/issues/32529) Aggiunta controllo per date non contigue durante la fase di salvataggio associazione  ente convenzionato-struttura.
- [#32322](https://parermine.regione.emilia-romagna.it/issues/32322) Realizzazione della funzionalità per comunicare key&secret per client api pubblicate
- [#31947](https://parermine.regione.emilia-romagna.it/issues/31947) Eliminare il salvataggio degli elenchi di versamento UD
- [#31945](https://parermine.regione.emilia-romagna.it/issues/31945) Eliminare validazione elenco UD con firma
- [#31922](https://parermine.regione.emilia-romagna.it/issues/31922) Introduzione modalità NO FIRMA nella validazione degli elenchi di versamento dei fascicoli
- [#31034](https://parermine.regione.emilia-romagna.it/issues/31034) Gestione descrizione dei dati specifici per singola versione 
- [#30752](https://parermine.regione.emilia-romagna.it/issues/30752) Indicazione degli elenchi non validati nel messaggio di avvenuta validazione
- [#30399](https://parermine.regione.emilia-romagna.it/issues/30399) Salvataggio diretto su object storage dell'elenco indici AIP dei fascicoli
- [#30398](https://parermine.regione.emilia-romagna.it/issues/30398) Salvataggio diretto su object storage dell'indice AIP dei fascicoli
- [#30397](https://parermine.regione.emilia-romagna.it/issues/30397) Salvataggio diretto su object storage dell'elenco indici AIP delle UD
- [#9833](https://parermine.regione.emilia-romagna.it/issues/9833) Cancellazione periodica dei versamenti non andati a buon fine

## 9.3.0 (06-05-2024)

### Bugfix: 8
- [#31701](https://parermine.regione.emilia-romagna.it/issues/31701) Risoluzione errore riscontrato nella verifica formato di file MPP
- [#31359](https://parermine.regione.emilia-romagna.it/issues/31359) Correzione errore pagina grigia dopo il download dell'elenco di versamento
- [#31064](https://parermine.regione.emilia-romagna.it/issues/31064) Correzione bug in fase di salvataggio RICHIESTA di annullamento UD
- [#31062](https://parermine.regione.emilia-romagna.it/issues/31062) Integrazione ai dati presentati per il fascicolo nella sezione Profilo archivistico
- [#31060](https://parermine.regione.emilia-romagna.it/issues/31060) Rimozione record duplicati nella ricerca annullamenti UD
- [#30429](https://parermine.regione.emilia-romagna.it/issues/30429) Correzione dell'errore critico nella ricerca dei criteri di raggruppametno
- [#27520](https://parermine.regione.emilia-romagna.it/issues/27520) Gestione dell'errore critico aprendo tipo serie da Dettaglio serie di unità documentarie
- [#25168](https://parermine.regione.emilia-romagna.it/issues/25168) Correzione della gestione dei nomi file e degli urn nel servizio di recupero 

### Novità: 15
- [#31671](https://parermine.regione.emilia-romagna.it/issues/31671) Inserimento pagina "informativa privacy"
- [#31449](https://parermine.regione.emilia-romagna.it/issues/31449) Adeguamento del transaction timeout sul Consumer Indice Aip
- [#31298](https://parermine.regione.emilia-romagna.it/issues/31298) ottimizzaione recupero lista componenti di un elenco di versamenti
- [#31079](https://parermine.regione.emilia-romagna.it/issues/31079) Restituzione archivio: modifica controlli alla creazione della richiesta
- [#31047](https://parermine.regione.emilia-romagna.it/issues/31047) Servizi di annullamento versamento UD: aggiunta campo Utente
- [#30846](https://parermine.regione.emilia-romagna.it/issues/30846) Modifiche alla pagina di dettaglio della richiesta restituzione archivio
- [#30823](https://parermine.regione.emilia-romagna.it/issues/30823) Aggiunta logica di ricalcolo consistenza SACER a seguito di cambio stato delle US/componenti
- [#30723](https://parermine.regione.emilia-romagna.it/issues/30723) Gestione del Tipo annullamento nell'interfaccia di Sacer
- [#30721](https://parermine.regione.emilia-romagna.it/issues/30721) Introduzione parametro Tipo annullamento nella chiamata del servizio di annullamento UD
- [#30536](https://parermine.regione.emilia-romagna.it/issues/30536) Gestione strutture: introdurre la ricerca per parametro con valore non standard
- [#30394](https://parermine.regione.emilia-romagna.it/issues/30394) Eliminazione dall'annullamento versamento UD della modalità di richiesta che prevede l'iter di approvazione
- [#29089](https://parermine.regione.emilia-romagna.it/issues/29089) Accesso ai dati dell'aggiornamento metadati UD salvati su Object Storage
- [#28935](https://parermine.regione.emilia-romagna.it/issues/28935) Aggiunta della procedura automatica eliminazione sessioni di versamento errate e fallite
- [#28081](https://parermine.regione.emilia-romagna.it/issues/28081) Riepilogo processo generazione indice aip: modifiche alla pagina
- [#25865](https://parermine.regione.emilia-romagna.it/issues/25865) Inserimento tempo di conservazione su tipo registro e tipo ud

## 9.2.0 (04-04-2024)

### Bugfix: 2
- [#31282](https://parermine.regione.emilia-romagna.it/issues/31282) Correzione problema encoding in fase di recupero dell'indice-aip
- [#31274](https://parermine.regione.emilia-romagna.it/issues/31274) Correzione dell'errore di dowload di zip vuoto di una sessione di versamento 

### Novità: 3
- [#31281](https://parermine.regione.emilia-romagna.it/issues/31281) ottimizzazione DB per Gestione lifecycle
- [#29090](https://parermine.regione.emilia-romagna.it/issues/29090) Accesso ai metadati su ojbect storage del fascicolo
- [#27869](https://parermine.regione.emilia-romagna.it/issues/27869) Eliminazione partizionamento logico

## 9.1.0 (21-03-2024)

### Novità: 1
- [#29588](https://parermine.regione.emilia-romagna.it/issues/29588) Rimozione della libreria esterna sacer-jpa

## 9.0.0 (14-03-2024)

### Bugfix: 1
- [#30978](https://parermine.regione.emilia-romagna.it/issues/30978) Correzione chiamata al ws Recupero stato oggetto da lab.jsp

### Novità: 1
- [#30799](https://parermine.regione.emilia-romagna.it/issues/30799) Aggiornamento a Spring 5 

## 8.20.0 (29-01-2024)

### Bugfix: 1
- [#30960](https://parermine.regione.emilia-romagna.it/issues/30960) Correzione bug su Job Sigillo

### Novità: 9
- [#31012](https://parermine.regione.emilia-romagna.it/issues/31012) Aggiornamento libreria DSS 5.13
- [#30763](https://parermine.regione.emilia-romagna.it/issues/30763) Rimozione elementi del partizionamento logico 
- [#30395](https://parermine.regione.emilia-romagna.it/issues/30395) Salvataggio diretto su object storage dell'indice AIP delle UD
- [#30206](https://parermine.regione.emilia-romagna.it/issues/30206) Elenchi di versamento: introdotta la persistenza dei parametri gestionali
- [#29287](https://parermine.regione.emilia-romagna.it/issues/29287) Aggiunta la ricerca dei formati nella pagina di aggiunta dei formati a livello di componente
- [#28215](https://parermine.regione.emilia-romagna.it/issues/28215) Adeguamento dell'Indicatore di struttura partizionata in assenza di partizionamenti logici
- [#28013](https://parermine.regione.emilia-romagna.it/issues/28013) Realizzata la parte di interfaccia per la visualizzazione dei profili del fascicolo
- [#26039](https://parermine.regione.emilia-romagna.it/issues/26039) Miglioramenti della  Valutazione idoneità formati alla conservazione
- [#14129](https://parermine.regione.emilia-romagna.it/issues/14129) Miglioramento performance del monitoraggio versamenti unità documentarie

## 8.19.0 (05-12-2023)

### Bugfix: 3
- [#30890](https://parermine.regione.emilia-romagna.it/issues/30890) Correzione anomalie nella composizione dell'AIP
- [#30845](https://parermine.regione.emilia-romagna.it/issues/30845) Correzione tempi anomali nell'apertura della pagina gestione richieste
- [#29079](https://parermine.regione.emilia-romagna.it/issues/29079) Correzione log di errore 

### Novità: 11
- [#30848](https://parermine.regione.emilia-romagna.it/issues/30848) Creazione endpoint con informazioni sulla versione
- [#30719](https://parermine.regione.emilia-romagna.it/issues/30719) Modifiche alla pagina Dettaglio firme
- [#30204](https://parermine.regione.emilia-romagna.it/issues/30204) Ricerca elenchi di versamento: introduzione nuovi filtri di ricerca
- [#30190](https://parermine.regione.emilia-romagna.it/issues/30190) Rimozione del tab note all'interno del dettaglio dell'unità documentaria e del dettaglio documento
- [#30081](https://parermine.regione.emilia-romagna.it/issues/30081) Gestione parametro FL_EIDAS_INCLUDI_FILEBASE64
- [#30057](https://parermine.regione.emilia-romagna.it/issues/30057) Modifica alle ricerche nella pagina ESAME CONTENUTO SACER
- [#29429](https://parermine.regione.emilia-romagna.it/issues/29429) Integrazione con SPID professionale
- [#29328](https://parermine.regione.emilia-romagna.it/issues/29328) Modifica dati dettaglio struttura
- [#23698](https://parermine.regione.emilia-romagna.it/issues/23698) Servizi di recupero: correzione problema con urn definito nel nome componente
- [#16859](https://parermine.regione.emilia-romagna.it/issues/16859) Sovrascrittura XSD consentita anche in presenza di nuovi metadati
- [#8930](https://parermine.regione.emilia-romagna.it/issues/8930) Trasformatori: implementato il trasformatore con origine formati XML.P7M

## 8.18.0 (23-11-2023)

### Bugfix: 12
- [#30878](https://parermine.regione.emilia-romagna.it/issues/30878) Ripristino filtri su stato sessione nelle tabelle delle sessioni andate a buon fine
- [#30809](https://parermine.regione.emilia-romagna.it/issues/30809) Correzione errore recupero SIP sessione fallita
- [#30690](https://parermine.regione.emilia-romagna.it/issues/30690) Correzione problema di performance della ricerca fascicoli
- [#30655](https://parermine.regione.emilia-romagna.it/issues/30655) Aggiornamento dell'ultimo ente convenzionato associato all'organizzazione Sacer
- [#30566](https://parermine.regione.emilia-romagna.it/issues/30566) Correzione errore del calcolo struttura con SIP salvato su object storage
- [#30430](https://parermine.regione.emilia-romagna.it/issues/30430) Correzione errore in fase di cancellazione di un  criterio di raggruppamento dalla lista del dettaglio tipologia ud
- [#30106](https://parermine.regione.emilia-romagna.it/issues/30106) Tipo fascicolo: correzione errore di pagina indietro su periodo di validità
- [#30054](https://parermine.regione.emilia-romagna.it/issues/30054) AIP fascicolo: correzione del nome file dell'xsd di validazione del profilo
- [#30051](https://parermine.regione.emilia-romagna.it/issues/30051) Correzione della riposta nel recupero di un fascicolo non presente sul sistema
- [#30050](https://parermine.regione.emilia-romagna.it/issues/30050) Correzione della riposta nel recupero di un fascicolo per cui non è stato ancora prodotto l'aip
- [#30004](https://parermine.regione.emilia-romagna.it/issues/30004) Correzione della richiesta di annullamento immediata che rimane in stato chiusa
- [#29702](https://parermine.regione.emilia-romagna.it/issues/29702) Correzioni alla pagina Lista unità documentarie derivanti da versamenti falliti

### Novità: 6
- [#30819](https://parermine.regione.emilia-romagna.it/issues/30819) Miglioramento performance job calcolo contenuto sacer
- [#30055](https://parermine.regione.emilia-romagna.it/issues/30055) Introduzione filtro di ricerca UD e Fascicolo sulla versione utilizzata
- [#29971](https://parermine.regione.emilia-romagna.it/issues/29971) Modifica job Calcola struttura per controllare che l'UD non sia già stata versata correttamente
- [#29968](https://parermine.regione.emilia-romagna.it/issues/29968) Implementazione nuovo parametro per massimo di elenchi da gestire col Sigillo
- [#27483](https://parermine.regione.emilia-romagna.it/issues/27483) Introduzione filtro di ricerca UD sulla presenza di aggiornamenti metadati
- [#15879](https://parermine.regione.emilia-romagna.it/issues/15879) Ricerca unità documentarie: aggiungere il filtro Firmatario

## 8.17.0 (03-10-2023)

### Bugfix: 1
- [#27333](https://parermine.regione.emilia-romagna.it/issues/27333) Correzione della mancata visualizzazione di uno stato elenco nei risultati di una ricerca

### Novità: 7
- [#30415](https://parermine.regione.emilia-romagna.it/issues/30415) Monitoraggio delle sessioni cancellate delle strutture cessate
- [#30369](https://parermine.regione.emilia-romagna.it/issues/30369) Gestione dei file e degli xml dei versamenti falliti 
- [#30235](https://parermine.regione.emilia-romagna.it/issues/30235) Ripristino della versione 1.11.1 di Eidas
- [#30195](https://parermine.regione.emilia-romagna.it/issues/30195) Aumento del transaction timeout per MDB di Creazione Indice Aip
- [#29833](https://parermine.regione.emilia-romagna.it/issues/29833) Attività per la realizzazione della separazione delle sessioni: parte database
- [#29639](https://parermine.regione.emilia-romagna.it/issues/29639) Attività per la realizzazione della separazione delle sessioni: parte monitoraggio
- [#28749](https://parermine.regione.emilia-romagna.it/issues/28749) Separazione tra sessioni versamenti in errore e sessioni versamenti andati a buon fine

## 8.16.1 (13-09-2023)

### Bugfix: 1
- [#30210](https://parermine.regione.emilia-romagna.it/issues/30210) Downgrade alla versione 1.10.0 di Eidas

## 8.16.0 (30-08-2023)

### Bugfix: 4
- [#30077](https://parermine.regione.emilia-romagna.it/issues/30077) Correzione visualizzazione doppia fascicoli
- [#30072](https://parermine.regione.emilia-romagna.it/issues/30072) Gestione job: correzione dell'errore all'apertura di Amministrazione job
- [#29898](https://parermine.regione.emilia-romagna.it/issues/29898) Correzione della mancata visualizzazione SIP di Documenti aggiunti e Aggiornamento metadati per modifica della Data unità documentaria
- [#28947](https://parermine.regione.emilia-romagna.it/issues/28947) Correzione errore mancata generazione del report di consistenza archivio

### Novità: 2
- [#29734](https://parermine.regione.emilia-romagna.it/issues/29734) Modifiche all'Indice AIP delle serie di unità documentarie
- [#29536](https://parermine.regione.emilia-romagna.it/issues/29536) Estensione import/export struttura e parametri di configurazione ai tipi fascicolo

## 8.15.0 (16-08-2023)

### Novità: 1
- [#29725](https://parermine.regione.emilia-romagna.it/issues/29725) Gestione Indice AIP Unisincro fascicolo

### Bugfix: 4
- [#29740](https://parermine.regione.emilia-romagna.it/issues/29740) Salvataggio attributi profilo specifico fascicolo
- [#29385](https://parermine.regione.emilia-romagna.it/issues/29385) Correzione errore pagina grigia dopo il download
- [#28366](https://parermine.regione.emilia-romagna.it/issues/28366) Correzione del funzionamento del link diretto alla pagina gestione JOB
- [#14377](https://parermine.regione.emilia-romagna.it/issues/14377) Esame contenuto Sacer: errore nel calcolo delle ud versate e annullate

### Novità: 3
- [#29936](https://parermine.regione.emilia-romagna.it/issues/29936) Adeguamento del timeout per MDB di Creazione Indice Aip
- [#29880](https://parermine.regione.emilia-romagna.it/issues/29880) Aggiornamento alle versione 5.12 della libreria DSS
- [#29726](https://parermine.regione.emilia-romagna.it/issues/29726) Modifica del recupero AIP fascicolo

## 8.14.0 (03-08-2023)

### Novità: 1
- [#29327](https://parermine.regione.emilia-romagna.it/issues/29327) Aggiornamento librerie obsolete 2023

## 8.13.0 (13-07-2023)

### Novità: 1
- [#25611](https://parermine.regione.emilia-romagna.it/issues/25611) Adeguamento indice sip fascicolo a nuove linee guida e nuovo profilo normativo

### Bugfix: 3
- [#29751](https://parermine.regione.emilia-romagna.it/issues/29751) Correzione inserimento/modifica associazione struttura-ente convenzionato
- [#29602](https://parermine.regione.emilia-romagna.it/issues/29602) Correzione recupero con automa abilitato solo ad alcuni tipi di dato
- [#27958](https://parermine.regione.emilia-romagna.it/issues/27958) Correzione elenco dei parametri visualizzati e modificabili in "Gestione ambienti / Dettaglio ambiente"

### Novità: 6
- [#29589](https://parermine.regione.emilia-romagna.it/issues/29589) Gestione Indice AIP Unisincro indipendentemente dalla versione del servizio di versamento fascicolo
- [#29443](https://parermine.regione.emilia-romagna.it/issues/29443) Servizio di Aggiornamento UD: aggiungere profilo normativo
- [#29329](https://parermine.regione.emilia-romagna.it/issues/29329) Ottimizzazione job calcolo consistenza nel caso di numerosi annullamenti
- [#28154](https://parermine.regione.emilia-romagna.it/issues/28154) Aggiunto il pulsante "Scarica xml" alla pagina di visualizzazione di una foto del log eventi 
- [#27509](https://parermine.regione.emilia-romagna.it/issues/27509) Inserimento nuovo stato di "Annullamento" alla richiesta di annullamento versamenti
- [#14981](https://parermine.regione.emilia-romagna.it/issues/14981) Dettaglio fascicolo: visualizzazione metadati di profilo in funzione degli xsd utilizzati

## 8.12.0 (15-06-2023)

### Novità: 3
- [#27655](https://parermine.regione.emilia-romagna.it/issues/27655) Modifiche all'interfaccia per la gestione del profilo normativo
- [#26658](https://parermine.regione.emilia-romagna.it/issues/26658) Ottimizzate performance gestione elenchi di versamento
- [#26470](https://parermine.regione.emilia-romagna.it/issues/26470) Completo l'adeguamento Linee guida Serie - Adeguamento Indice AIP Unisincro 2

### LAB: 1
- [#29341](https://parermine.regione.emilia-romagna.it/issues/29341) Valutazione dell'introduzione OAUTH 2 su servizio Annullamento fascicolo

### Bugfix: 10
- [#29705](https://parermine.regione.emilia-romagna.it/issues/29705) Miglioramento performance ricerca sessioni versamento errate
- [#29631](https://parermine.regione.emilia-romagna.it/issues/29631) Correzione estrazione nome utente durante l'autenticazione SPID
- [#29539](https://parermine.regione.emilia-romagna.it/issues/29539) Correzione query di estrazione nella Restituzione archivio
- [#29512](https://parermine.regione.emilia-romagna.it/issues/29512)  Correzione JPQL su viste in fase di validazione utente
- [#29444](https://parermine.regione.emilia-romagna.it/issues/29444) Correzione problema snippet
- [#28260](https://parermine.regione.emilia-romagna.it/issues/28260) Correzione della restituzione archivio nel caso in cui l'ente abbia più strutture sacer 
- [#28014](https://parermine.regione.emilia-romagna.it/issues/28014) Integrate le voci mancanti nel menù "Classe errore" del monitoraggio versamenti falliti
- [#28003](https://parermine.regione.emilia-romagna.it/issues/28003) Correzione errore download  AIP UD
- [#27229](https://parermine.regione.emilia-romagna.it/issues/27229) Correzione del messaggio di errore nella creazione di una richiesta di restituzione archivio
- [#26192](https://parermine.regione.emilia-romagna.it/issues/26192) Corretti i nomi file serie senza codice nell'AIP UD

### Novità: 7
- [#28515](https://parermine.regione.emilia-romagna.it/issues/28515) Aggiunta la nuova modalità di autenticazione tramite OAUTH2 anche per il servizio di annullamento fascicoli 
- [#27915](https://parermine.regione.emilia-romagna.it/issues/27915) Aggiunto accesso diretto alla pagina di ricerca dei versamenti (attuale "Lista documenti")
- [#27411](https://parermine.regione.emilia-romagna.it/issues/27411) Modifiche alla pagina Lista unità documentarie derivante da versamenti falliti
- [#27402](https://parermine.regione.emilia-romagna.it/issues/27402)  Aggiunto logging accessi SPID non autorizzati
- [#27080](https://parermine.regione.emilia-romagna.it/issues/27080) Sviluppato nuovo indice AIP della serie conforme ad Uni Sincro 2020
- [#25457](https://parermine.regione.emilia-romagna.it/issues/25457) Adeguati i moduli informazioni e Disciplinari tecnici svincolati dall'accordo
- [#18999](https://parermine.regione.emilia-romagna.it/issues/18999) Gestione corrispondenze sacer - ping

## 8.11.0 (17-05-2023)

### Bugfix: 7
- [#29607](https://parermine.regione.emilia-romagna.it/issues/29607) Corretta la procedura di migrazione per indicare esplicitamente il dec_backend 
- [#29317](https://parermine.regione.emilia-romagna.it/issues/29317) Correzione della mancata importazione del Tipo registro normalizzato - valorizzazione mancante
- [#29275](https://parermine.regione.emilia-romagna.it/issues/29275) Correzione errore nel recupero se l'utente è abilitato a tipi di dato 
- [#29073](https://parermine.regione.emilia-romagna.it/issues/29073) Correzione della mancata presenza del campo Archivio Restituito in dettaglio struttura
- [#28509](https://parermine.regione.emilia-romagna.it/issues/28509) Correzione chiusura automatica elenchi fiscali
- [#28505](https://parermine.regione.emilia-romagna.it/issues/28505) Correzione della mancata visualizzazione SIP di Documenti aggiunti  tramite il servizio "aggiunta documento" quando i SIP sono Object storage.
- [#27786](https://parermine.regione.emilia-romagna.it/issues/27786) Correzione del mancato aggiornamento stato di conservazione di una UD

### Novità: 2
- [#29083](https://parermine.regione.emilia-romagna.it/issues/29083) Portate le informazioni di memorizzazione sull'UD e sul Doc
- [#28937](https://parermine.regione.emilia-romagna.it/issues/28937) Storicizzazione dei log dei job

## 8.10.1 (08-05-2023)

### Bugfix: 1
- [#29492](https://parermine.regione.emilia-romagna.it/issues/29492) Correzione disallineamento DB tra test, pre-produzione e produzione - jpa/hibernate

## 8.10.0 (27-04-2023)

### Novità: 1
- [#27925](https://parermine.regione.emilia-romagna.it/issues/27925) Evoluzione registro formati per acquisizione automatica delle categorie dei formati nei componenti

### Novità: 1
- [#28543](https://parermine.regione.emilia-romagna.it/issues/28543)  Creazione pacchetto unico per sacer

## 8.9.1 (26-04-2023)

### Bugfix: 1
- [#28034](https://parermine.regione.emilia-romagna.it/issues/28034)  Correzione su gestione caratteri non validi nelle risposte alle chiamate dei servizi

## 8.9.0 (06-04-2023)

### Bugfix: 12
- [#29103](https://parermine.regione.emilia-romagna.it/issues/29103) Risoluzione problema con parametro "AGENT_AUTHORIZED_SIGNER_ROLE_LEGAL_PERSON"
- [#29102](https://parermine.regione.emilia-romagna.it/issues/29102) Correzione malfunzionamento sezione monitoraggio Sacer - impossibile utilizzare i filtri di ricerca
- [#29025](https://parermine.regione.emilia-romagna.it/issues/29025) Correzione controllo su duplicazione struttura su strutture non pre-esistenti.
- [#28618](https://parermine.regione.emilia-romagna.it/issues/28618) Correzione errore durante inserimento associazione registro tipo UD
- [#28573](https://parermine.regione.emilia-romagna.it/issues/28573) Correzione dell'importa struttura: mancata importazione dei formati specifici
- [#28567](https://parermine.regione.emilia-romagna.it/issues/28567) Importazione formati specifici anche nel caso in cui il tipo componente sia presente
- [#28547](https://parermine.regione.emilia-romagna.it/issues/28547) Correzione del campo numero contiene
- [#28072](https://parermine.regione.emilia-romagna.it/issues/28072) Correzione errore critico in Esame job schedulati (Calcola estrazione)
- [#28064](https://parermine.regione.emilia-romagna.it/issues/28064) Correzione errore critico di funzionalità nell'accedere al dettaglio in "RIEPILOGO PROCESSO GENERAZIONE INDICE AIP"
- [#28040](https://parermine.regione.emilia-romagna.it/issues/28040) Correzione errore presente in fase di cancellazione di un tipo fascicolo
- [#27992](https://parermine.regione.emilia-romagna.it/issues/27992) Correzione problema importazione/duplicazione registro e mancanza di un controllo in inserimento registro
- [#27879](https://parermine.regione.emilia-romagna.it/issues/27879) Correzione della cancellazione di una struttura con il profilo normativo configurato

### Novità: 1
- [#28217](https://parermine.regione.emilia-romagna.it/issues/28217) Registro formati: introduzione gestione dinamica dei formati delle buste crittografiche

## 8.8.0 (28-02-2023)

### Novità: 1
- [#15749](https://parermine.regione.emilia-romagna.it/issues/15749) Gestione del Sigillo elettronico (stessa tecnologia di firma automatica)

### Bugfix: 13
- [#28346](https://parermine.regione.emilia-romagna.it/issues/28346)  Disabilitata gestione delle CRL nel job verifica firme
- [#28219](https://parermine.regione.emilia-romagna.it/issues/28219) Errore critico nella cessazione di una struttura
- [#28173](https://parermine.regione.emilia-romagna.it/issues/28173) correzione della mancata compilazione del campo mimetype sul tipo componente
- [#28167](https://parermine.regione.emilia-romagna.it/issues/28167) Correzione dell'eccezione nel metodo eliminaFormatiAmmessiGestiti
- [#28165](https://parermine.regione.emilia-romagna.it/issues/28165) Correzione di errore nell'importa parametri di configurazione 
- [#28137](https://parermine.regione.emilia-romagna.it/issues/28137) Correzione errore nel servizio di recupero RecDIPEsibizioneSync
- [#28130](https://parermine.regione.emilia-romagna.it/issues/28130) Modificando il periodo di validità di un Tipo fascicolo ottengo errore
- [#28129](https://parermine.regione.emilia-romagna.it/issues/28129) Errore nel salvataggio di un nuovo Tipo Fascicolo
- [#28128](https://parermine.regione.emilia-romagna.it/issues/28128) Correzione dell'Errore generico nell'export di alcune strutture
- [#28090](https://parermine.regione.emilia-romagna.it/issues/28090) Correzione visualizzazione lista RIEPILOGO DOCUMENTI DERIVANTI DA AGGIUNTE FALLITE
- [#28042](https://parermine.regione.emilia-romagna.it/issues/28042) Correzione  visualizzazione dettaglio su monitoraggio aggiornamento metadati
- [#25884](https://parermine.regione.emilia-romagna.it/issues/25884) Correzione errori di visualizzazione su monitoraggio aggiornamento metadati
- [#25598](https://parermine.regione.emilia-romagna.it/issues/25598) Correzione del campo mimetype vuoto nella lista formati ammessi del dettaglio del componente

### Novità: 4
- [#28502](https://parermine.regione.emilia-romagna.it/issues/28502) Gestione dei formati specifici
- [#28357](https://parermine.regione.emilia-romagna.it/issues/28357) adeguamento SAML per regione PUGLIA
- [#28166](https://parermine.regione.emilia-romagna.it/issues/28166) Modifiche alla gestione dell'importazione parametri di configurazione
- [#28006](https://parermine.regione.emilia-romagna.it/issues/28006) Modifica servizio di recupero da Object Storage

## 8.7.0 (22-12-2022)

### Novità: 1
- [#13993](https://parermine.regione.emilia-romagna.it/issues/13993) Implementati i servizi di recupero dei fascicoli

### Bugfix: 3
- [#28060](https://parermine.regione.emilia-romagna.it/issues/28060) Correzione errore nel caricamento di un modello di fascicolo
- [#28051](https://parermine.regione.emilia-romagna.it/issues/28051) Correzione errori AIP fascicolo
- [#28020](https://parermine.regione.emilia-romagna.it/issues/28020) Correzione problema di deadlock sulla cancellazione elenchi vuoti tramite mdb

### Novità: 9
- [#28038](https://parermine.regione.emilia-romagna.it/issues/28038) Realizzazione recupero sincrono dell'aip del fascicolo (ONLINE e WS).
- [#27930](https://parermine.regione.emilia-romagna.it/issues/27930) Aggiornato l' Import/Export della struttura con la nuova modalità di gestione dei formati
- [#27929](https://parermine.regione.emilia-romagna.it/issues/27929) Verifica del disciplinare tecnico a seguito della cancellazione del registro formati dalla struttura
- [#27928](https://parermine.regione.emilia-romagna.it/issues/27928) Aggiunta della concatenazione a livello di componente
- [#27927](https://parermine.regione.emilia-romagna.it/issues/27927) Aggiunta la possibilità di definire la scelta della categoria di formato  a livello di componente
- [#27926](https://parermine.regione.emilia-romagna.it/issues/27926) Eliminazione del controllo dei formati a livello di struttura
- [#27831](https://parermine.regione.emilia-romagna.it/issues/27831) Modifica creazione indice AIP in presenza di SIGILLO
- [#27247](https://parermine.regione.emilia-romagna.it/issues/27247) Modifiche alla configurazione tipo fascicolo
- [#26576](https://parermine.regione.emilia-romagna.it/issues/26576) Gestione AIP del fascicolo secondo il nuovo Uni Sincro

## 8.6.0 (19-12-2022)

### Bugfix: 8
- [#27878](https://parermine.regione.emilia-romagna.it/issues/27878) correzione visualizzazione numero elenchi in monitoraggio -> riepilogo processo di generazione indice aip
- [#27816](https://parermine.regione.emilia-romagna.it/issues/27816)  Correzione del job Evasione richieste di restituzione archivio (EVASIONE_RICH_REST_ARCH)  
- [#27714](https://parermine.regione.emilia-romagna.it/issues/27714) Correzione errore in fase di cancellazione di un xsd
- [#27327](https://parermine.regione.emilia-romagna.it/issues/27327) Correzione errore sul pulsante "Duplica"  del criterio di raggruppamento
- [#27171](https://parermine.regione.emilia-romagna.it/issues/27171) Correzione dell'eccezione non prevista durante l'eliminazione di un modello di fascicolo
- [#26668](https://parermine.regione.emilia-romagna.it/issues/26668) Gestione caratteri non validi nelle risposte alle chiamate dei servizi 
- [#26530](https://parermine.regione.emilia-romagna.it/issues/26530) Aggiunta tra i servizi di recupero il ws Recupero stato oggetto
- [#26510](https://parermine.regione.emilia-romagna.it/issues/26510) Modifica al campo data nella pagina dettaglio ente

### Novità: 17
- [#27990](https://parermine.regione.emilia-romagna.it/issues/27990) Modifica alla pagina "Ricerca elenchi di versamento unità documentarie"
- [#27884](https://parermine.regione.emilia-romagna.it/issues/27884) Aggiunta possibilità di start e stop dei job direttamente dal monitoraggio esame  job schedulati
- [#27827](https://parermine.regione.emilia-romagna.it/issues/27827) Modifiche alla pagina e al funzionamento di "ELENCHI INDICI AIP UNITÀ DOCUMENTARIE DA FIRMARE"
- [#27826](https://parermine.regione.emilia-romagna.it/issues/27826) Modifica alla pagina di inserimento dei nuovi criteri di raggruppamento UD
- [#27825](https://parermine.regione.emilia-romagna.it/issues/27825) Modifica alla pagina di ricerca dei criteri di raggruppamento
- [#27824](https://parermine.regione.emilia-romagna.it/issues/27824) Introduzione del JOB per l'apposizione del sigillo elettronico
- [#27823](https://parermine.regione.emilia-romagna.it/issues/27823) Gestione dei nuovi parametri necessari al funzionamento del SIGILLO
- [#27517](https://parermine.regione.emilia-romagna.it/issues/27517) Aggiunta del campo URN nelle informazioni principali del Dettaglio serie di unità documentarie
- [#27512](https://parermine.regione.emilia-romagna.it/issues/27512) Aggiunta del campo URN nelle informazioni principali del dettaglio fascicolo
- [#27511](https://parermine.regione.emilia-romagna.it/issues/27511) Gestione Enti: preimpostazione filtro 'Tipologia di ente' con Ente non template
- [#27414](https://parermine.regione.emilia-romagna.it/issues/27414) Aggiornamento della tabella degli utenti disciplinare tecnico
- [#27329](https://parermine.regione.emilia-romagna.it/issues/27329) Introduzione della gestione LOG EVENTI in registro parametri
- [#27301](https://parermine.regione.emilia-romagna.it/issues/27301) Aggiunta selezione di più ambienti per l'elaborazione "Esame Consistenza Sacer"
- [#27259](https://parermine.regione.emilia-romagna.it/issues/27259) Spostamento funzionalità Gestione modelli unità documentaria e Gestione modelli di tipo fascicolo
- [#27141](https://parermine.regione.emilia-romagna.it/issues/27141) Estrazione dati RIEPILOGO UNITÀ DOCUMENTARIE DERIVANTI DA VERSAMENTI FALLITI
- [#26396](https://parermine.regione.emilia-romagna.it/issues/26396) Miglioramenti al monitoraggio del processo generazione indice AIP
- [#25453](https://parermine.regione.emilia-romagna.it/issues/25453) Modifica chiamate in GET a rimozione oggetti, vulnerabili al cross site request forgery (CSRF)

## 8.5.1 (25-11-2022)

### Bugfix: 1
- [#27994](https://parermine.regione.emilia-romagna.it/issues/27994) disabilitazione multi-threading nel job Creazione elenchi batch

## 8.5.0 (23-11-2022)

### Novità: 1
- [#27535](https://parermine.regione.emilia-romagna.it/issues/27535) aggiornamento libreria dss 5.10

## 8.3.1.6 (29-09-2022)

### Bugfix: 1
- [#27764](https://parermine.regione.emilia-romagna.it/issues/27764) Correzione mancata cancellazione record dalla aro_indice_aip_ud_da_elab 

## 8.3.1.5 (27-09-2022)

### Bugfix: 1
- [#27730](https://parermine.regione.emilia-romagna.it/issues/27730) Correzione di ulteriori errori nella creazione degli indici AIP

## 8.3.1.4 (23-09-2022)

### Bugfix: 1
- [#27719](https://parermine.regione.emilia-romagna.it/issues/27719) Correzione di un errore nella creazione degli indici AIP

## 8.3.1.3 (16-09-2022)

### Bugfix: 1
- [#27699](https://parermine.regione.emilia-romagna.it/issues/27699) Aggiornamento loghi

## 8.3.1.2 (15-09-2022)

### Bugfix: 4
- [#27666](https://parermine.regione.emilia-romagna.it/issues/27666) Correzione dell'ERRORE FILESYS-003-00 su versamento DPI
- [#27665](https://parermine.regione.emilia-romagna.it/issues/27665) Correzione dell'errore critico quando si elimina un elenco di versamento unità documentaria o un  elenco da validare
- [#27664](https://parermine.regione.emilia-romagna.it/issues/27664) Gestione dell'errore nell'eliminazione da lista
- [#27654](https://parermine.regione.emilia-romagna.it/issues/27654) Correzione dell'errore in Job Creazione indici aip serie UD

## 8.3.1.1 (09-09-2022)

### Bugfix: 24
- [#27650](https://parermine.regione.emilia-romagna.it/issues/27650) Correzione dell'errore sulla validazione delle serie
- [#27644](https://parermine.regione.emilia-romagna.it/issues/27644) Correzione dell'anomalia sul job Verifica massiva versamenti falliti
- [#27643](https://parermine.regione.emilia-romagna.it/issues/27643) Correzione dell'errore sul job di calcolo contenuto Sacer
- [#27641](https://parermine.regione.emilia-romagna.it/issues/27641) Correzione del oroblema con il JOB CREAZIONE_INDICI_ELENCHI_VERS_FASC
- [#27638](https://parermine.regione.emilia-romagna.it/issues/27638) Correzione dell'errore nel processo di restituzione archivio
- [#27634](https://parermine.regione.emilia-romagna.it/issues/27634) Correzione dell'errore JOB Creazione Indice AIP FASC
- [#27628](https://parermine.regione.emilia-romagna.it/issues/27628) Correzione dei problemi con il JOB Validazione fascicoli
- [#27573](https://parermine.regione.emilia-romagna.it/issues/27573) Correzione dell'errore nel JOB validazione fascicoli
- [#27567](https://parermine.regione.emilia-romagna.it/issues/27567) Correzione errore nel servizio di recupero: RecDIPComponenteTrasformatoSync
- [#27560](https://parermine.regione.emilia-romagna.it/issues/27560) Correzione del mancato recupero UD in stato IN_ARCHIVIO
- [#27556](https://parermine.regione.emilia-romagna.it/issues/27556) Correzione dell'errrore critico in RIEPILOGO AGGIORNAMENTO METADATI
- [#27554](https://parermine.regione.emilia-romagna.it/issues/27554) Correzione dell'errore critico sfogliando RICERCA REPLICHE ORGANIZZAZIONI
- [#27553](https://parermine.regione.emilia-romagna.it/issues/27553) Correzione dell'ERRORE CRITICO avviando la ricerca ESAME CONTENUTO SACER
- [#27551](https://parermine.regione.emilia-romagna.it/issues/27551) Correzione dell'errore critico aprendo il dettaglio da Elenco Metadati delle versioni xsd del tipo fascicolo
- [#27546](https://parermine.regione.emilia-romagna.it/issues/27546) Correzione dell'errore in DETTAGLIO STRUTTURA nell'eliminazione dalle liste delle sezioni
- [#27526](https://parermine.regione.emilia-romagna.it/issues/27526) Correzione della gestione delle dipendenze per il WS /ReplicaUtente
- [#27518](https://parermine.regione.emilia-romagna.it/issues/27518) Correzione dell'errore critico scaricando il contenuto di una serie
- [#27505](https://parermine.regione.emilia-romagna.it/issues/27505) Correzione errore causato da eliminazione di un parametro in gestione parametri 
- [#27497](https://parermine.regione.emilia-romagna.it/issues/27497) Correzione errore nell'accesso alla pagina Versamenti annullati unità documentarie
- [#27478](https://parermine.regione.emilia-romagna.it/issues/27478) Correzione errore nell'accesso al dettaglio differenza in "Esame Consistenza Sacer"
- [#27476](https://parermine.regione.emilia-romagna.it/issues/27476) Correzione errore presente in fase di cancellazione di una tipologia UD
- [#27475](https://parermine.regione.emilia-romagna.it/issues/27475) Correzione errore nella modifica delle data dell'ente convenzionato
- [#27268](https://parermine.regione.emilia-romagna.it/issues/27268) Dettaglio periodo di validità del tipo fascicolo: correzione errore nell'accedere al dettaglio di una versione XSD dei metadati di profilo 
- [#27127](https://parermine.regione.emilia-romagna.it/issues/27127) Errore nel servizio di recupero RecDIPProveConservSync

### Novità: 1
- [#27439](https://parermine.regione.emilia-romagna.it/issues/27439) Aggiornamento ad Hibernate

## 7.3.1 (04-08-2022)

### Bugfix: 3
- [#27500](https://parermine.regione.emilia-romagna.it/issues/27500) Modifica alla pagina gestione job - se il job è schedulato il pulsante esecuzione singola massiva non deve risultare attivo
- [#27499](https://parermine.regione.emilia-romagna.it/issues/27499) Correzione problema di concorrenza nella creazione degli elenchi
- [#27493](https://parermine.regione.emilia-romagna.it/issues/27493) Risoluzione casi non gestiti nella creazione degli elenchi dopo il versamento delle UD (a seguito dell'ottimizzazione con la coda JMS)

### Novità: 2
- [#27544](https://parermine.regione.emilia-romagna.it/issues/27544) Adeguamento monitoraggio DLQ per messaggi proveniente da SacerWs
- [#27216](https://parermine.regione.emilia-romagna.it/issues/27216) Completamento di rimozione delle logiche di gestione verifica firme attraverso webservice SOAP

## 7.3.0 (01-08-2022)

### Bugfix: 8
- [#27279](https://parermine.regione.emilia-romagna.it/issues/27279) Correzione visualizzazione dettaglio differenze negative in Esame Consistenza Sacer
- [#27277](https://parermine.regione.emilia-romagna.it/issues/27277) Correzione problema in associazione utente SPID con anagrafica utenti per le applicazioni sacer eidas
- [#27261](https://parermine.regione.emilia-romagna.it/issues/27261) Abilitazione inserimento nuovo ente Sacer dopo l'esecuzione di una ricerca in "Gestione enti"
- [#27248](https://parermine.regione.emilia-romagna.it/issues/27248) Correzione al mancato salvataggio della modfica dell'estensione di un formato
- [#27234](https://parermine.regione.emilia-romagna.it/issues/27234) Correzione messaggio di errore associazione struttura-ente convenzionato
- [#27233](https://parermine.regione.emilia-romagna.it/issues/27233) Realizzazione report analitico per monitoraggio consistenza
- [#27203](https://parermine.regione.emilia-romagna.it/issues/27203) Campi non valorizzati nell'importa parametri configurazione
- [#26977](https://parermine.regione.emilia-romagna.it/issues/26977) Correzione errore nell'export della struttura

### Novità: 9
- [#27389](https://parermine.regione.emilia-romagna.it/issues/27389) Integrazione della gestione di SPID della Puglia nell'attuale gestione SPID in RER
- [#27153](https://parermine.regione.emilia-romagna.it/issues/27153) realizzazione di un report sintetico per tutti gli ambienti relativo al monitoraggio della consistenza
- [#27126](https://parermine.regione.emilia-romagna.it/issues/27126) Nuova pagina gestione JOB - esecuzione singola "massiva"
- [#27122](https://parermine.regione.emilia-romagna.it/issues/27122) Migliorie alla sezione ricerca della pagina gestione job new
- [#27042](https://parermine.regione.emilia-romagna.it/issues/27042) Migliorie alla gestione della pagina gestione job - refresh della lista
- [#27041](https://parermine.regione.emilia-romagna.it/issues/27041) Migliorie alla gestione del filtro nella pagina gestione job
- [#26487](https://parermine.regione.emilia-romagna.it/issues/26487) Visualizzazione profilo normativo in Dettaglio UD
- [#26486](https://parermine.regione.emilia-romagna.it/issues/26486) Introduzione filtro ricerca UD con Profilo normativo
- [#26085](https://parermine.regione.emilia-romagna.it/issues/26085) Impostazione campi in inserimento/modifica Tipologia UD 

## 7.2.1 (31-05-2022)

### Bugfix: 1
- [#27298](https://parermine.regione.emilia-romagna.it/issues/27298) correzione configurazione coda jms
## 7.2.0 (31-05-2022)

### Novità: 3
- [#27296](https://parermine.regione.emilia-romagna.it/issues/27296) Aggiornamento libreria DSS 5.9
- [#27169](https://parermine.regione.emilia-romagna.it/issues/27169) Ottimizzazione job di Creazione elenchi di versamento - Consumer coda IN_ATTESA_SCHED
- [#26288](https://parermine.regione.emilia-romagna.it/issues/26288) Scorporo del job creazione indice aip

## 7.1.0 (10-05-2022)

### Bugfix: 6
- [#27172](https://parermine.regione.emilia-romagna.it/issues/27172) Correzione funzionalità di disattivazione job in "amministrazione job" 
- [#27068](https://parermine.regione.emilia-romagna.it/issues/27068) Correzione errore critico di funzionalità accedendo al dettaglio delle UD di un elenco
- [#26996](https://parermine.regione.emilia-romagna.it/issues/26996) Correzione comportamento della pagina registro parametri dopo l'eliminazione di un parametro
- [#26960](https://parermine.regione.emilia-romagna.it/issues/26960) Modifica controllo in inserimento/modifica struttura
- [#26395](https://parermine.regione.emilia-romagna.it/issues/26395) Correzioni al riepilogo processo generazione indice AIP
- [#26038](https://parermine.regione.emilia-romagna.it/issues/26038) Errore nell'inserimento della valutazione idoneità formati alla conservazione

### Novità: 11
- [#27152](https://parermine.regione.emilia-romagna.it/issues/27152) Realizzazione report analitico per monitoraggio consistenza
- [#27130](https://parermine.regione.emilia-romagna.it/issues/27130) adeguamento e ottimizzazione calcolo consistenza per intervallo di data versamento/riferimento
- [#27072](https://parermine.regione.emilia-romagna.it/issues/27072) Gestione condizioni per Inserimento nuovo ente Sacer e Inserimento nuova struttura Sacer
- [#27044](https://parermine.regione.emilia-romagna.it/issues/27044) Modifica al colore utilizzato per lo stato "in esecuzione" nella lista dei job
- [#27035](https://parermine.regione.emilia-romagna.it/issues/27035) Cambio nome del file Indice AIP
- [#27008](https://parermine.regione.emilia-romagna.it/issues/27008) Miglioramenti al registro dei formati - Filtri di ricerca
- [#27001](https://parermine.regione.emilia-romagna.it/issues/27001) Utilizzo del SubjectDN dalla CA sul job di verifica firme a data versamento
- [#26987](https://parermine.regione.emilia-romagna.it/issues/26987) Sostituzione caratteri non accettati nel nome delle cartelle nella funzione "Restituzione archivio"
- [#26985](https://parermine.regione.emilia-romagna.it/issues/26985) Creazione automatica in FTP della cartella creazione archivio
- [#24976](https://parermine.regione.emilia-romagna.it/issues/24976) Introduzione parametro per cessare struttura senza richieste di restituzione archivio
- [#24554](https://parermine.regione.emilia-romagna.it/issues/24554) Correzione tabella Tipi oggetto da trasformare

## 7.0.12 (09-05-2022)

### Bugfix: 1
- [#27185](https://parermine.regione.emilia-romagna.it/issues/27185) Correzione gestione profilo indice aip del fascicolo

## 7.0.11 (27-04-2022)

### Novità: 1
- [#14664](https://parermine.regione.emilia-romagna.it/issues/14664) Visualizzazione attributi degli xsd di profilo generale ed archivistico del fascicolo

## 8.0.10.1 (11-04-2022)

### Bugfix: 1
- [#27092](https://parermine.regione.emilia-romagna.it/issues/27092) Correzione nuova pagine di gestione job

## 8.0.10 (28-03-2022)

### Bugfix: 9
- [#26955](https://parermine.regione.emilia-romagna.it/issues/26955) Errore in fase salvataggio struttura (hibernate)
- [#26940](https://parermine.regione.emilia-romagna.it/issues/26940) PING TEST - servizio annullamento non risponde
- [#26932](https://parermine.regione.emilia-romagna.it/issues/26932) Job di creazione degli elenchi di versamento: salvataggio dati incompleto
- [#26931](https://parermine.regione.emilia-romagna.it/issues/26931) Correzione di errore critico nel download di un aip di ud
- [#26929](https://parermine.regione.emilia-romagna.it/issues/26929) Errore nel servizio di recupero RecDIPProveConservSync
- [#26928](https://parermine.regione.emilia-romagna.it/issues/26928) Errore nel servizio di recupero RecAIPUnitaDocumentariaSync
- [#26913](https://parermine.regione.emilia-romagna.it/issues/26913) Errore critico modificando l'ambiente di un ente
- [#26912](https://parermine.regione.emilia-romagna.it/issues/26912) Errore critico cliccando su un dettaglio della lista nella pagina ESAME CONSISTENZA SACER
- [#26883](https://parermine.regione.emilia-romagna.it/issues/26883) correzione connessione db di test hibernate

## 7.0.10 (23-03-2022)

### Bugfix: 7
- [#26884](https://parermine.regione.emilia-romagna.it/issues/26884) Errore nella configurazione di un tipo documento
- [#26747](https://parermine.regione.emilia-romagna.it/issues/26747) Errore nell'xsd di validazione della response
- [#26737](https://parermine.regione.emilia-romagna.it/issues/26737) Adeguamento del monitoraggio sul job di Creazione elenchi di versamento
- [#26723](https://parermine.regione.emilia-romagna.it/issues/26723) Correzione errore di visualizzazione parametri di tipo password
- [#26623](https://parermine.regione.emilia-romagna.it/issues/26623) Correzione su controllo di verifica profilo per tipo documento
- [#26595](https://parermine.regione.emilia-romagna.it/issues/26595) Correzione funzionalità di importazione strutture
- [#23470](https://parermine.regione.emilia-romagna.it/issues/23470) Errore nella gestione della controfirma

### Novità: 10
- [#26895](https://parermine.regione.emilia-romagna.it/issues/26895) Gestione Strutture: preimpostazione filtro 'Struttura Template' con NO
- [#26798](https://parermine.regione.emilia-romagna.it/issues/26798) Modifica alla gestione del registro parametri
- [#26517](https://parermine.regione.emilia-romagna.it/issues/26517) Inserimento di un tooltip sul flag "controllo formato numero"
- [#26424](https://parermine.regione.emilia-romagna.it/issues/26424) Nuova pagina gestione JOB 
- [#26349](https://parermine.regione.emilia-romagna.it/issues/26349) Elenchi UD da validare: modificare valore campo Tipo validazione elenco
- [#26275](https://parermine.regione.emilia-romagna.it/issues/26275) Miglioramenti al registro dei formati - Ricerca per mimetype
- [#25997](https://parermine.regione.emilia-romagna.it/issues/25997) Spostamento voci di menu relative a sessioni errate in Logging
- [#25675](https://parermine.regione.emilia-romagna.it/issues/25675) Aggiunta campi di ricerca ID elenco in alcune pagine
- [#25656](https://parermine.regione.emilia-romagna.it/issues/25656) Monitoraggio sessioni di recupero: aggiunta filtri di ricerca e informazioni nei risultati
- [#25597](https://parermine.regione.emilia-romagna.it/issues/25597) Modifica ordinamento lista formati ammessi

## 8.0.9.1 (09-03-2022)

### Bugfix: 4
- [#26901](https://parermine.regione.emilia-romagna.it/issues/26901) Errore in fase di aggiornamento di un criterio di raggruppamento
- [#26896](https://parermine.regione.emilia-romagna.it/issues/26896) Ambiente di test: Errore critico sulla funzionalita' in "Duplica struttura selezionata"
- [#26860](https://parermine.regione.emilia-romagna.it/issues/26860) Correzione dell'errore sconosciuto nella procedura di firma di Elenchi indici AIP
- [#26858](https://parermine.regione.emilia-romagna.it/issues/26858) Correzione dell'errore su nuovo Modello XSD in Gestione modelli unità documentaria

## 8.0.9 (25-02-2022)

### Bugfix: 6
- [#26893](https://parermine.regione.emilia-romagna.it/issues/26893) Errore annullamento UD
- [#26881](https://parermine.regione.emilia-romagna.it/issues/26881) Errore nel Job di generazione elenco di versamento
- [#26863](https://parermine.regione.emilia-romagna.it/issues/26863) Correzione dell'errore nella chiusura manuale di un elenco DI VERSAMENTO FASCICOLI
- [#26862](https://parermine.regione.emilia-romagna.it/issues/26862) Correzione di errore nell'apertura del dettaglio di un volume di conservazione
- [#26861](https://parermine.regione.emilia-romagna.it/issues/26861) Correzione dell'errore nell'eliminazione da lista
- [#26847](https://parermine.regione.emilia-romagna.it/issues/26847) Errore nel Job di generazione elenco di versamento

### Novità: 1
- [#26873](https://parermine.regione.emilia-romagna.it/issues/26873)  Versione hibernate corrispondente alla 7.0.9

## 7.0.9 (02-03-2022)

### Bugfix: 1
- [#26866](https://parermine.regione.emilia-romagna.it/issues/26866) aggiunta conteggio componenti annullati direttamente in fase di conteggio dei versamenti

## 8.0.8

### Novità: 1
- [#26842](https://parermine.regione.emilia-romagna.it/issues/26842) Versione  hibernate corrispondente alla 7.0.8 

## 7.0.8 (24-02-2022)

### Bugfix: 1
- [#26841](https://parermine.regione.emilia-romagna.it/issues/26841) correzione problema di performance con creazione viste DB

## 7.0.7 (18-02-2022)

### Bugfix: 1
- [#26814](https://parermine.regione.emilia-romagna.it/issues/26814) correzione recupero stato per registrazione stato elenco in verifica firme

## 7.0.6 (18-02-2022)

### Novità: 1
- [#26810](https://parermine.regione.emilia-romagna.it/issues/26810) problema di performance nel conteggio della consistenza

## 8.0.5

### Bugfix: 1
- [#26783](https://parermine.regione.emilia-romagna.it/issues/26783) Correzione errore nel download di un AIP

### Novità: 1
- [#26788](https://parermine.regione.emilia-romagna.it/issues/26788) Versione  hibernate corrispondente alla 7.0.5 
## 7.0.5 (15-02-2022)

### Bugfix: 1
- [#26787](https://parermine.regione.emilia-romagna.it/issues/26787) ottimizzazione query per prima esecuzione

## 8.0.4.1 (10-02-2022)

### Novità: 1
- [#26764](https://parermine.regione.emilia-romagna.it/issues/26764) Rimozione delle tabelle obsolete/non più utilizzate

## 8.0.4 (09-02-2022)

### Novità: 1
- [#26263](https://parermine.regione.emilia-romagna.it/issues/26263) Allineamento della versione Hibernate all'ultima versione Eclipselink

## 7.0.4

### Novità: 3
- [#26734](https://parermine.regione.emilia-romagna.it/issues/26734) Gestione della chiusura anticipata degli elenchi fiscali a seconda del tipo di validazione
- [#26731](https://parermine.regione.emilia-romagna.it/issues/26731) Gestione degli elenchi con tipo validazione = NO_INDICE e modalità di validazione = AUTOMATICA.
- [#25672](https://parermine.regione.emilia-romagna.it/issues/25672) Miglioramento della modalità di ricerca di alcuni campi

## 7.0.3 (31-01-2022)

### Bugfix: 2
- [#26609](https://parermine.regione.emilia-romagna.it/issues/26609) Correzione salvataggio stato elenchi di versamento (stato IN_CODA_JMS_VERIFICA_FIRME_DT_VERS)
- [#26521](https://parermine.regione.emilia-romagna.it/issues/26521) Correzione errore nella ricerca elenchi: alcuni filtri non funzionano

### Novità: 5
- [#26659](https://parermine.regione.emilia-romagna.it/issues/26659) aggiornamento librerie obsolete primo quadrimestre 2021
- [#26587](https://parermine.regione.emilia-romagna.it/issues/26587) Offuscamento campo parametro di tipo password solo in visualizzazione
- [#26446](https://parermine.regione.emilia-romagna.it/issues/26446) Predisporre un servizio che consenta l'annullamento dei fascicoli
- [#26419](https://parermine.regione.emilia-romagna.it/issues/26419) Gestione del profilo normativo all'interno dell'AIP delle Unità Documentarie
- [#18740](https://parermine.regione.emilia-romagna.it/issues/18740) Home page struttura: calcolare la data effettiva di aggiornamento del contenuto dell'archivio

## 7.0.2 (13-01-2022)

### Bugfix: 2
- [#26592](https://parermine.regione.emilia-romagna.it/issues/26592) Correzione salvataggio stato degli elenchi di versamento
- [#26580](https://parermine.regione.emilia-romagna.it/issues/26580) Aggiunta controllo in fase di modifica xsd del profilo ud

## 7.0.1 (30-12-2021)

### Bugfix: 1
- [#26514](https://parermine.regione.emilia-romagna.it/issues/26514) Correzione rapporto di versamento indirizzo ip sistema versante

## 7.0.0 (20-12-2021)

### Novità: 1
- [#26168](https://parermine.regione.emilia-romagna.it/issues/26168) Introduzione gestione del nuovo profilo normativo

## 6.9.3

### Novità: 2
- [#26338](https://parermine.regione.emilia-romagna.it/issues/26338) ottimizzazione estrazione differenze tra "contenuto sacer" e "calcolo consistenza" e popolamento temporanea delle struttre
- [#24597](https://parermine.regione.emilia-romagna.it/issues/24597) Gestione delle note sull'Unità documentaria

## 6.9.2 (09-12-2021)

### Bugfix: 2
- [#26394](https://parermine.regione.emilia-romagna.it/issues/26394) Correzione reintegro branch MEV 24517
- [#26391](https://parermine.regione.emilia-romagna.it/issues/26391) Correzione errore vista su partizionamento aggiornamento metadati

## 6.9.1

### Bugfix: 4
- [#26377](https://parermine.regione.emilia-romagna.it/issues/26377) Correzione passaggio parametro utente hsm per firma elenchi indici aip
- [#26366](https://parermine.regione.emilia-romagna.it/issues/26366) Correzione parametro che definisce l'annualità di chiusura degli elenchi fiscali in stato aperto
- [#26365](https://parermine.regione.emilia-romagna.it/issues/26365) Correzione errore job creazione indice aip (regressione causata dal reintegro dei branch)
- [#26320](https://parermine.regione.emilia-romagna.it/issues/26320) Correzione errore gestione CRL mancanti nel job verifica firme
## 6.9.0

### EVO: 1
- [#19691](https://parermine.regione.emilia-romagna.it/issues/19691) Autenticazione sui web services di recupero con certificato client

### Bugfix: 2
- [#24734](https://parermine.regione.emilia-romagna.it/issues/24734) Correzione Job di Validazione Fascicoli: errore NullPointerException.
- [#22156](https://parermine.regione.emilia-romagna.it/issues/22156) Annullamento fascicolo: non viene aggiornato lo stato delle UD 

### Novità: 6
- [#24558](https://parermine.regione.emilia-romagna.it/issues/24558) Inserimento campo Utente nelle Informazioni di versamento del Dettaglio UD
- [#24517](https://parermine.regione.emilia-romagna.it/issues/24517) Visualizzazione dati riguardanti la verifica firma con ocsp
- [#22934](https://parermine.regione.emilia-romagna.it/issues/22934) Scorporo del job di verifica firme
- [#22248](https://parermine.regione.emilia-romagna.it/issues/22248) Elenchi di versamento fascicoli: impedire l'accesso ai fascicoli annullati
- [#15631](https://parermine.regione.emilia-romagna.it/issues/15631) Gestione data scadenza per chiusura elenchi fiscali
- [#7788](https://parermine.regione.emilia-romagna.it/issues/7788) Gestire più di una marca temporale su un singolo componente

## 6.8.6.1

### Bugfix: 1
- [#26327](https://parermine.regione.emilia-romagna.it/issues/26327) Correzione errore gestione CRL mancanti nel job verifica firme

## 6.8.6

### Bugfix: 1
- [#26282](https://parermine.regione.emilia-romagna.it/issues/26282) Adeguamenti nel job di creazione elenchi di versamento 

## 6.8.5

### Bugfix: 1
- [#26215](https://parermine.regione.emilia-romagna.it/issues/26215) Cambio modalità di valorizzazione dell'attributo AgentID nell'indice AIP

### Novità: 1
- [#26219](https://parermine.regione.emilia-romagna.it/issues/26219) Adeguamento della logica di gestione degli urn del pregresso

## 6.8.4 (04-11-2021)

### EVO: 1
- [#15369](https://parermine.regione.emilia-romagna.it/issues/15369) Monitoraggio processo di conservazione unità documentarie

### Bugfix: 2
- [#26173](https://parermine.regione.emilia-romagna.it/issues/26173) Correzione gestione proprietà di sistema relativa al livello di autenticazione SPID 
- [#26115](https://parermine.regione.emilia-romagna.it/issues/26115) Correzione procedure di restituzione archivio

## 6.8.3 (21-10-2021)

### Bugfix: 9
- [#25927](https://parermine.regione.emilia-romagna.it/issues/25927) Eliminazione dei parametri previa verifica
- [#25922](https://parermine.regione.emilia-romagna.it/issues/25922) Errore di valorizzazione nella tabella del valore precedenti indici AIP
- [#25915](https://parermine.regione.emilia-romagna.it/issues/25915) Modificare urn Aggiornamento metadati UD
- [#25904](https://parermine.regione.emilia-romagna.it/issues/25904) Modifica del parametro AGENT_AUTHORIZED_SIGNER_ROLE per personalizzare il relevant document all'interno dell'Indice AIP
- [#25864](https://parermine.regione.emilia-romagna.it/issues/25864) modificare il recupero dell'elenco indice aip firmato
- [#25856](https://parermine.regione.emilia-romagna.it/issues/25856) Errata valorizzazione dell'elemento previous hash
- [#25707](https://parermine.regione.emilia-romagna.it/issues/25707) Correzione errore nella generazione dell'xml della struttura
- [#25654](https://parermine.regione.emilia-romagna.it/issues/25654) Aggiornamento degli hash nell'aggiornamento AIP
- [#16791](https://parermine.regione.emilia-romagna.it/issues/16791) AIP UD: nomi file serie senza codice

### Novità: 9
- [#25921](https://parermine.regione.emilia-romagna.it/issues/25921) Inserire l'xsd di Unisincro nell'AIP
- [#25918](https://parermine.regione.emilia-romagna.it/issues/25918) Miglioramento delle informazioni nel dettaglio indice AIP utilizzato
- [#25903](https://parermine.regione.emilia-romagna.it/issues/25903) Introduzione dei parametri per impostare il relevant document per il submitter e holder
- [#25872](https://parermine.regione.emilia-romagna.it/issues/25872) Modifica valorizzazione attributo PVolume/ID
- [#25871](https://parermine.regione.emilia-romagna.it/issues/25871) Modifica al nome dell'indice AIP
- [#25772](https://parermine.regione.emilia-romagna.it/issues/25772) Gestione di diversi livelli di accesso con credenziali SPID 
- [#25653](https://parermine.regione.emilia-romagna.it/issues/25653) Modificare label HASH nell'interfaccia della sezione INDICE AIP
- [#25640](https://parermine.regione.emilia-romagna.it/issues/25640) Ottimizzazione meccanismo di retry 
- [#25348](https://parermine.regione.emilia-romagna.it/issues/25348) Accesso ai report di verifica firma su object storage

## 6.8.2

### Bugfix: 4
- [#25689](https://parermine.regione.emilia-romagna.it/issues/25689) Correzione errore critico sulla funzionalità in inserimento valore parametro struttura  lungo
- [#25403](https://parermine.regione.emilia-romagna.it/issues/25403) Errore critico sulla funzionalita' quando si tenta di di settare gli errori come VERIFICATI e NON RISOLUBILI.
- [#25197](https://parermine.regione.emilia-romagna.it/issues/25197) Gestione strutture: risoluzione problemi su pagine dettaglio regola e dettaglio campo
- [#24363](https://parermine.regione.emilia-romagna.it/issues/24363) Errore critico nell'accesso al dettaglio di una categoria ente

### Novità: 1
- [#24534](https://parermine.regione.emilia-romagna.it/issues/24534) Indici elenchi di versamento: creazione automatica solo con tipo di validazione pari a FIRMA

## 6.8.1 (10-08-2021)

### EVO: 1
- [#20972](https://parermine.regione.emilia-romagna.it/issues/20972) Adeguamento alle nuove linee guida

### Bugfix: 2
- [#25428](https://parermine.regione.emilia-romagna.it/issues/25428) Correzione procedura di import configurazione strutture
- [#25224](https://parermine.regione.emilia-romagna.it/issues/25224) Correzione gestione hash indici aip precedenti 

### Novità: 4
- [#22152](https://parermine.regione.emilia-romagna.it/issues/22152) Monitoraggio sessioni di recupero: estendere le informazioni monitorate
- [#20980](https://parermine.regione.emilia-romagna.it/issues/20980) Valutazione idoneità formati alla conservazione
- [#20971](https://parermine.regione.emilia-romagna.it/issues/20971) Gestione precedenti Indici AIP
- [#20420](https://parermine.regione.emilia-romagna.it/issues/20420) Gestire il cambio ambiente di una struttura da interfaccia

### SUE: 1
- [#24992](https://parermine.regione.emilia-romagna.it/issues/24992) Verifica gestione hash indici aip 

## 6.8.0.4

### Bugfix: 1
- [#25482](https://parermine.regione.emilia-romagna.it/issues/25482) Correzione trasformazioni xslt (librerie)

## 6.8.0.3 (13-07-2021)

### Bugfix: 1
- [#25435](https://parermine.regione.emilia-romagna.it/issues/25435) Correzione dipendenze librerie JAXB e JAVAX ACTIVATION

## 6.8.0.2 (07-07-2021)

### Bugfix: 1
- [#25418](https://parermine.regione.emilia-romagna.it/issues/25418) Correzione gestione errori nel job verifica firma

## 6.8.0.1 (02-07-2021)

### Bugfix: 1
- [#25380](https://parermine.regione.emilia-romagna.it/issues/25380) Correzione gestione errori nel job verifica firma

## 6.8.0 (03-06-2021)

### Bugfix: 1
- [#20154](https://parermine.regione.emilia-romagna.it/issues/20154) Errore nella costruzione dello zip di scarico di un elenco di versamento ex volume di conservazione

### Novità: 10
- [#25142](https://parermine.regione.emilia-romagna.it/issues/25142) Allineamento Accesso SPID per SACER versione eidas
- [#23546](https://parermine.regione.emilia-romagna.it/issues/23546)  Controllo librerie obsolete terzo quadrimestre 2020
- [#22785](https://parermine.regione.emilia-romagna.it/issues/22785) Recepimento aggiornamenti librerie Secondo quadrimestre 2020 - SACER (versione Eidas)
- [#22407](https://parermine.regione.emilia-romagna.it/issues/22407) Aggiunta nuovi campi sul dettaglio unità documentaria
- [#22152](https://parermine.regione.emilia-romagna.it/issues/22152) Monitoraggio sessioni di recupero: estendere le informazioni monitorate
- [#22110](https://parermine.regione.emilia-romagna.it/issues/22110) Libreria obsoleta log4j-1.2.17.jar
- [#21254](https://parermine.regione.emilia-romagna.it/issues/21254)  Aggiornamento JQUERY
- [#20439](https://parermine.regione.emilia-romagna.it/issues/20439) Nuova reportistica di verifica firma
- [#18697](https://parermine.regione.emilia-romagna.it/issues/18697) Rimozione cryptolibrary
- [#11170](https://parermine.regione.emilia-romagna.it/issues/11170) Aggiunta nuovo formato di file per firma

## 6.7.9 (18-05-2021)

### Bugfix: 6
- [#24837](https://parermine.regione.emilia-romagna.it/issues/24837) Correzione della gestione dei nomi file e degli urn nel servizio di recupero 
- [#24530](https://parermine.regione.emilia-romagna.it/issues/24530) Correzione anomalia alla chiusura della pagina in "dettaglio parametri struttura"
- [#24279](https://parermine.regione.emilia-romagna.it/issues/24279) Correzione errore nella verifica delle date di associazione della struttura all'ente convenzionato
- [#22972](https://parermine.regione.emilia-romagna.it/issues/22972) Indice elenco di versamento UD: non sono indicati tutti gli aggiornamenti UD
- [#22942](https://parermine.regione.emilia-romagna.it/issues/22942) Aggiornamento metadati: la cancellazione di elenchi di versamento aperti non resetta lo stato del'aggiornamento UD
- [#22735](https://parermine.regione.emilia-romagna.it/issues/22735) Correzione anomalia in fase di creazione elenchi vuoti

### Novità: 6
- [#24239](https://parermine.regione.emilia-romagna.it/issues/24239) Visualizzazione indirizzo IP e sistema versante dell'utente versatore
- [#23512](https://parermine.regione.emilia-romagna.it/issues/23512) Inserimento attributo/selettore messaggio JMS con indicazione dell'applicazione
- [#23374](https://parermine.regione.emilia-romagna.it/issues/23374) Eliminazione della logica per gestire il recupero dei file versamenti falliti da altro db
- [#22920](https://parermine.regione.emilia-romagna.it/issues/22920) Revisione check struttura partizionata SI/NO in base agli aggiornamenti metadati
- [#22438](https://parermine.regione.emilia-romagna.it/issues/22438) Monitoraggio aggiornamento UD: aggiungere UD derivanti da aggiornamenti falliti
- [#16516](https://parermine.regione.emilia-romagna.it/issues/16516) Gestione indice aip per ud in volume di conservazione

## 6.7.8 (14-04-2021)

### Bugfix: 1
- [#24670](https://parermine.regione.emilia-romagna.it//issues/24670) Correzione errore su procedura di preparazione migrazione verso Object Storage

## 6.7.7 (10-03-2021)

### Bugfix: 4
- [#24114](https://parermine.regione.emilia-romagna.it//issues/24114) Correzione job di preparazione migrazione verso object storage relativamente al nuovo calcolo degli URN
- [#24088](https://parermine.regione.emilia-romagna.it//issues/24088) Generazione javadoc
- [#23996](https://parermine.regione.emilia-romagna.it//issues/23996) Indice AIP: calcolo degli urn delle UD collegate
- [#23757](https://parermine.regione.emilia-romagna.it//issues/23757) Impossibile cancellare il tipo UD

### Novità: 2
- [#23602](https://parermine.regione.emilia-romagna.it//issues/23602) Dettaglio parametri in Dettaglio struttura/Tipo UD: rimanere nella stessa pagina dopo il salvataggio
- [#17709](https://parermine.regione.emilia-romagna.it//issues/17709) Calcolo collegamenti risolti nel job creazione indice AIP

## 6.7.6 (15-02-2021)

### Bugfix: 1
- [#24214](https://parermine.regione.emilia-romagna.it//issues/24214) Calcolo canoni in base alla sola data di scadenza

## 6.7.5 (19-02-2021)

### Bugfix: 4
- [#23881](https://parermine.regione.emilia-romagna.it//issues/23881) Creazione Indice AIP: urn Indice AIP delle ud collegate non viene ricalcolato
- [#23806](https://parermine.regione.emilia-romagna.it//issues/23806) Errore in fase di recupero di componenti da trasformare
- [#22849](https://parermine.regione.emilia-romagna.it//issues/22849) Visualizzazione aggiornamento metadati
- [#22475](https://parermine.regione.emilia-romagna.it//issues/22475) Trasformazione componente

### Novità: 2
- [#23814](https://parermine.regione.emilia-romagna.it//issues/23814) Gestire timeout mediante parametro
- [#22963](https://parermine.regione.emilia-romagna.it//issues/22963) Salvare la firma in formato CLOB

## 6.7.4

### Bugfix: 4
- [#23742](https://parermine.regione.emilia-romagna.it//issues/23742) ElencoIndiciAIP: formattazione urn nel tag descrizione elenco indici aip non corretta
- [#23736](https://parermine.regione.emilia-romagna.it//issues/23736) Urn normalizzati non corretti su elenchi versamento e elenchi indici aip
- [#23731](https://parermine.regione.emilia-romagna.it//issues/23731) Dettaglio UD: problema nell'accesso a UD collegate annullate
- [#23726](https://parermine.regione.emilia-romagna.it//issues/23726) Errore job Validazione fascicoli

### SUE: 1
- [#22917](https://parermine.regione.emilia-romagna.it//issues/22917) Rilascio SACER 6.7.2 versione urn + nuova fatturazione (include 6.6.0, 6.7.0 e 6.7.1)

## 6.7.3

### Bugfix: 2
- [#23706](https://parermine.regione.emilia-romagna.it//issues/23706) Job creazione indice AIP: gestire il caso dei collegamenti non risolti
- [#23680](https://parermine.regione.emilia-romagna.it//issues/23680) Errore nell'accedere al Dettaglio documento

### Novità: 2
- [#23596](https://parermine.regione.emilia-romagna.it//issues/23596) Rimozione logo IBACN per passaggio a RER
- [#17625](https://parermine.regione.emilia-romagna.it//issues/17625) Annullamento UD: gestione collegamenti

## 6.7.2

### Bugfix: 1
- [#23503](https://parermine.regione.emilia-romagna.it//issues/23503) Indice AIP: errore valorizzazione tag Versione XSD Indice AIP

### Novità: 2
- [#23548](https://parermine.regione.emilia-romagna.it//issues/23548) Introdurre campo Note su Ambiente
- [#23501](https://parermine.regione.emilia-romagna.it//issues/23501) Aggiunta lettura parametro da DB

## 6.7.1

### Bugfix: 17
- [#23345](https://parermine.regione.emilia-romagna.it//issues/23345) Crypto library: correzioni per la corretta gestione degli XADES
- [#23323](https://parermine.regione.emilia-romagna.it//issues/23323) Importa Tipo UD: gestire i nuovi tipi servizio 
- [#23287](https://parermine.regione.emilia-romagna.it//issues/23287) Campi non valorizzati nell'importa/duplica struttura
- [#23241](https://parermine.regione.emilia-romagna.it//issues/23241) Errore nel recupero rapporto di versamento
- [#23240](https://parermine.regione.emilia-romagna.it//issues/23240) Errore nel job Calcolo contenuto Sacer
- [#23237](https://parermine.regione.emilia-romagna.it//issues/23237) Errore critico in salvataggio Tipo UD
- [#23214](https://parermine.regione.emilia-romagna.it//issues/23214) Urn errato nel Dettaglio elenco di versamento
- [#23192](https://parermine.regione.emilia-romagna.it//issues/23192) Indice Elenco indici AIP: urn non corretto
- [#23187](https://parermine.regione.emilia-romagna.it//issues/23187) Dettaglio Indice AIP precedente in errore
- [#23174](https://parermine.regione.emilia-romagna.it//issues/23174) Firma elenco di versamento: errore sconosciuto
- [#23172](https://parermine.regione.emilia-romagna.it//issues/23172) Dettaglio aggiornamento UD: urn errati e mancanti
- [#23171](https://parermine.regione.emilia-romagna.it//issues/23171) Dettaglio documento: problemi di visualizzazione
- [#23170](https://parermine.regione.emilia-romagna.it//issues/23170) Correzione urn dell'Esito versamento dell'Aggiunta documento
- [#23168](https://parermine.regione.emilia-romagna.it//issues/23168) Ricerca Componenti: componente duplicato in visualizzazione
- [#22898](https://parermine.regione.emilia-romagna.it//issues/22898) Monitoraggio aggiornamento metadati - filtro documento
- [#22419](https://parermine.regione.emilia-romagna.it//issues/22419) Mancato salvataggio valore normalizzato del registro 
- [#22191](https://parermine.regione.emilia-romagna.it//issues/22191) Dettaglio sottostruttura: filtrare le regole per substruttura

## 6.7.0

### Bugfix: 7
- [#23238](https://parermine.regione.emilia-romagna.it//issues/23238) Errore critico in fase di importazione struttura
- [#23188](https://parermine.regione.emilia-romagna.it//issues/23188) Creazione Indice AIP: urn UD collegate non vengono aggiornati
- [#23146](https://parermine.regione.emilia-romagna.it//issues/23146) Correzione urn Sottocomponente
- [#23145](https://parermine.regione.emilia-romagna.it//issues/23145) Correzione urn dell'Elenco di versamento
- [#23108](https://parermine.regione.emilia-romagna.it//issues/23108) Errata valorizzazione dell'urn del Documento
- [#23105](https://parermine.regione.emilia-romagna.it//issues/23105) Errata valorizzazione dell'urn del Componente nel Dettaglio elenco di versamento
- [#23104](https://parermine.regione.emilia-romagna.it//issues/23104) Errore in chiusura manuale di elenco di versamento

### Novità: 2
- [#22921](https://parermine.regione.emilia-romagna.it//issues/22921) Parametrizzazione servizi di recupero
- [#20026](https://parermine.regione.emilia-romagna.it//issues/20026) Revisione amministrazione per nuova fatturazione

## 6.6.0 (15-09-2020)

### EVO: 1
- [#16486](https://parermine.regione.emilia-romagna.it//issues/16486) Implementazione nuovo naming

### Bugfix: 5
- [#23688](https://parermine.regione.emilia-romagna.it//issues/23688) Errore calcolo urn  nel job di creazione elenco dei versamenti del fascicolo
- [#22754](https://parermine.regione.emilia-romagna.it//issues/22754) Correzione information leakage
- [#22178](https://parermine.regione.emilia-romagna.it//issues/22178) Correzione nomi di pagine
- [#21837](https://parermine.regione.emilia-romagna.it//issues/21837) Censimento servizio di recupero sui servizi da abilitare
- [#16840](https://parermine.regione.emilia-romagna.it//issues/16840) Calcolo nuovi URN nell'ambito della gestione fascicoli

### Novità: 2
- [#16494](https://parermine.regione.emilia-romagna.it//issues/16494) Processi gestiti dal Sistema: calcolare i nuovi URN se le entità ne sono sprovviste
- [#16492](https://parermine.regione.emilia-romagna.it//issues/16492) Calcolo dei nuovi URN per le entità generate dal Sistema

## 6.5.2 (22-07-2020)

### Bugfix: 1
- [#22659](https://parermine.regione.emilia-romagna.it//issues/22659) Problema nella visualizzazione ambito territoriale

### SUE: 1
- [#22962](https://parermine.regione.emilia-romagna.it//issues/22962) Esecuzione test di non regressione SACERWS 2.2.4-EIDAS

## 6.5.1 (21-07-2020)

### Bugfix: 2
- [#22555](https://parermine.regione.emilia-romagna.it//issues/22555) Esportazione titolario in errore
- [#22437](https://parermine.regione.emilia-romagna.it//issues/22437) Aggiornamento metadati UD: urn errato

### Novità: 2
- [#22155](https://parermine.regione.emilia-romagna.it//issues/22155) Estensione campo DS_FIRMA_BASE64 su SACER.ARO_FIRMA_COMP
- [#20767](https://parermine.regione.emilia-romagna.it//issues/20767) Strutture versanti: calcolare l'ambito territoriale

### SUE: 1
- [#22382](https://parermine.regione.emilia-romagna.it//issues/22382) Rilascio  in preproduzione e produzione SACER 6.5.2 (versione Ambito territoriale)

## 6.5.0 (12-05-2020)

### Bugfix: 1
- [#17337](https://parermine.regione.emilia-romagna.it//issues/17337) Dettaglio registro: spostare controllo caratteri non ammessi sul Tipo registro normalizzato

### Novità: 1
- [#21353](https://parermine.regione.emilia-romagna.it//issues/21353) Interventi per consentire la cancellazione logica

### SUE: 1
- [#21936](https://parermine.regione.emilia-romagna.it//issues/21936) Rilascio SACER in preproduzione e produzione (versione 6.5.0 - Cancellazione logica)

## 6.4.4 (07-05-2020)

### Novità: 1
- [#21799](https://parermine.regione.emilia-romagna.it//issues/21799) Recupero ud tramite automa

### SUE: 1
- [#21953](https://parermine.regione.emilia-romagna.it//issues/21953) Rilascio SACER in preproduzione e produzione (versione 6.4.4)

## 6.4.3 (22-04-2020)

### Bugfix: 1
- [#21668](https://parermine.regione.emilia-romagna.it//issues/21668) Correzione collegamento al data source SacerJobDs

## 6.4.2 (30-03-2020)

### Bugfix: 3
- [#21555](https://parermine.regione.emilia-romagna.it//issues/21555) Ricerche elenchi in errore 
- [#21537](https://parermine.regione.emilia-romagna.it//issues/21537)  Rimozione username in URL nella pagina di cambio password
- [#20641](https://parermine.regione.emilia-romagna.it//issues/20641) Restituzione archivio: correzione nell'organizzazione delle cartelle

### Novità: 3
- [#21624](https://parermine.regione.emilia-romagna.it//issues/21624) Predisposizione per l'utilizzo di un nuovo service name per il data source
- [#20831](https://parermine.regione.emilia-romagna.it//issues/20831) Ricerca unità documentarie: introdurre la ricerca like sul numero
- [#20463](https://parermine.regione.emilia-romagna.it//issues/20463) Gestione delle date dell'accordo e riflessi sulla gestione degli enti

## 6.4.1 (13-01-2020)

### Bugfix: 3
- [#20789](https://parermine.regione.emilia-romagna.it//issues/20789) Importa tipi UD/strutture: includere anche i parametri 
- [#20716](https://parermine.regione.emilia-romagna.it//issues/20716) Aggiornare entity per campi non utilizzati
- [#18863](https://parermine.regione.emilia-romagna.it//issues/18863) Gestione ambienti: presentare la lista di tutti gli ambienti all'apertura della pagina

### Novità: 3
- [#20281](https://parermine.regione.emilia-romagna.it//issues/20281) Importa parametri di configurazione: consentire l'importazione di più tipi UD
- [#20269](https://parermine.regione.emilia-romagna.it//issues/20269) Ricerca UD annullate: creare pagina di ricerca ad hoc
- [#19112](https://parermine.regione.emilia-romagna.it//issues/19112) Modifiche gestione parametri

## 6.4.0 (13-12-2019)

### EVO: 1
- [#16509](https://parermine.regione.emilia-romagna.it//issues/16509) Monitoraggio aggiornamento metadati

### Bugfix: 5
- [#20688](https://parermine.regione.emilia-romagna.it//issues/20688) Gestione accordi non validi nella produzione del disciplinare tecnico
- [#20682](https://parermine.regione.emilia-romagna.it//issues/20682) Disciplinare tecnico: sistemare la visualizzazione dei parametri
- [#20577](https://parermine.regione.emilia-romagna.it//issues/20577) Non è possibile inserire un nuovo sistema di migrazione 
- [#20541](https://parermine.regione.emilia-romagna.it//issues/20541) Dettaglio formato ammesso: modificare elenco Formati ammissibili
- [#19281](https://parermine.regione.emilia-romagna.it//issues/19281) Dettaglio registro: non funziona comando Indietro

## 6.3.4 (07-11-2019)

### Bugfix: 1
- [#20360](https://parermine.regione.emilia-romagna.it//issues/20360)  Sonda Zabbix per job controllo archiviazione tivoli - FIX

## 6.3.3 (05-11-2019)

### Novità: 2
- [#20060](https://parermine.regione.emilia-romagna.it//issues/20060) Sonda Zabbix per job controllo archiviazione tivoli
- [#20059](https://parermine.regione.emilia-romagna.it//issues/20059) Controllo archiviazione su nastro

## 6.3.2

### Bugfix: 3
- [#20231](https://parermine.regione.emilia-romagna.it//issues/20231) Produzione disciplinare tecnico con accordo errato
- [#19353](https://parermine.regione.emilia-romagna.it//issues/19353) Amministrazione enti e strutture: il conservatore deve poter operare come gestore
- [#18392](https://parermine.regione.emilia-romagna.it//issues/18392) Errore nel job di verifica versamenti falliti

### Novità: 1
- [#20039](https://parermine.regione.emilia-romagna.it//issues/20039) Aggiornare entity ORG_V_RIC_ENTE_CONVENZ

### SUE: 1
- [#19194](https://parermine.regione.emilia-romagna.it//issues/19194) Modificare la data di inizio validità delle strutture versanti

## 6.3.1 (09-10-2019)

### Novità: 1
- [#19954](https://parermine.regione.emilia-romagna.it//issues/19954) Gestire il recupero dei file versamenti falliti da altro db

## 6.3.0 (24-09-2019)

### Bugfix: 3
- [#19684](https://parermine.regione.emilia-romagna.it//issues/19684) Monitoraggio JMS message su DLQ (coda morta)
- [#19217](https://parermine.regione.emilia-romagna.it//issues/19217) Errore in fase di inserimento/modifica ambiente sacer
- [#19120](https://parermine.regione.emilia-romagna.it//issues/19120) Elenchi di versamento: completare elenco degli stati ricercabili

### Novità: 3
- [#20187](https://parermine.regione.emilia-romagna.it//issues/20187) Modifiche a disciplinare tecnico per gestione nuovi campi su Strutture versanti
- [#19254](https://parermine.regione.emilia-romagna.it//issues/19254) Integrazione Sacer con release notes
- [#18834](https://parermine.regione.emilia-romagna.it//issues/18834) Strutture versanti: nuovi campi

## 6.2.3

### Bugfix: 1
- [#19896](https://parermine.regione.emilia-romagna.it//issues/19896) Correzione collocamento Indice Aip in restituzione archivio

## 6.2.2

### Bugfix: 1
- [#19756](https://parermine.regione.emilia-romagna.it//issues/19756) Correzione collocamento Indice Aip in restituzione archivio

### Novità: 1
- [#19757](https://parermine.regione.emilia-romagna.it//issues/19757) ricerca Restituzione Archivio

## 6.2.1 (30-08-2019)

### Novità: 2
- [#19084](https://parermine.regione.emilia-romagna.it//issues/19084) Servizio di recupero dei file dato il tipo documento
- [#16855](https://parermine.regione.emilia-romagna.it//issues/16855) Modifica estensione dei file dell'UD recuperati/scaricati

## 6.1.1

### Novità: 1
- [#18161](https://parermine.regione.emilia-romagna.it//issues/18161) Scarico AIP su area FTP

## 6.0.11

### Bugfix: 1
- [#19427](https://parermine.regione.emilia-romagna.it//issues/19427) Importazione struttura: duplicazione dei parametri 

## 6.0.10

### Bugfix: 1
- [#18341](https://parermine.regione.emilia-romagna.it//issues/18341) Consumer VerificaFirmeDataVersamento: escludere i componenti della unità doc già verificati correttamente

## 6.0.9 (15-07-2019)

### Bugfix: 1
- [#19349](https://parermine.regione.emilia-romagna.it//issues/19349) Importa struttura: errore critico sulla funzionalità

## 6.0.8

### Bugfix: 1
- [#19316](https://parermine.regione.emilia-romagna.it//issues/19316) Accesso a ricerca strutture in errore con utente di ente non convenzionato

## 6.0.7 (04-07-2019)

### Novità: 2
- [#19050](https://parermine.regione.emilia-romagna.it//issues/19050) Disciplinare tecnico: nuovi campi e nuova gestione parametri
- [#18304](https://parermine.regione.emilia-romagna.it//issues/18304) Disciplinare tecnico: consentirne la generazione anche con accordo scaduto

## 6.0.6

### Bugfix: 1
- [#19215](https://parermine.regione.emilia-romagna.it//issues/19215) Importa parametri di configurazione: mancata importazione associazione registri - tipi ud

## 6.0.5

### Novità: 1
- [#18132](https://parermine.regione.emilia-romagna.it//issues/18132) Spostare i parametri di configurazione su una pagina dedicata

## 6.0.4 (13-06-2019)

### Bugfix: 2
- [#18958](https://parermine.regione.emilia-romagna.it//issues/18958) Produzione disciplinare tecnico
- [#18786](https://parermine.regione.emilia-romagna.it//issues/18786) Errore nella duplica / importa struttura

### Novità: 1
- [#18845](https://parermine.regione.emilia-romagna.it//issues/18845) Aggiornamento disciplinare tecnico

## 6.0.3 (31-05-2019)

### Bugfix: 5
- [#18865](https://parermine.regione.emilia-romagna.it//issues/18865) Parametro numero elenchi da validare automaticamente: gestione del valore zero
- [#18861](https://parermine.regione.emilia-romagna.it//issues/18861) Dettaglio struttura: errore critico dopo annulla
- [#18835](https://parermine.regione.emilia-romagna.it//issues/18835) Gestione intervallo di date di validità ente sacer
- [#18826](https://parermine.regione.emilia-romagna.it//issues/18826) Problemi nel calcolo Impronta SHA 256 sugli oggetti conservati
- [#18205](https://parermine.regione.emilia-romagna.it//issues/18205) Importa parametri: mancata importazione nuovi campi tipo struttura UD

## 6.0.2 (22-05-2019)

### Bugfix: 1
- [#18730](https://parermine.regione.emilia-romagna.it//issues/18730) Problemi nell'allineamento tra sacer e iam

### Novità: 1
- [#18696](https://parermine.regione.emilia-romagna.it//issues/18696) Rimozione codice applet di firma

## 6.0.1 (09-05-2019)

### Bugfix: 1
- [#18636](https://parermine.regione.emilia-romagna.it//issues/18636) Errore nell'importa duplica struttura

## 6.0.0 (06-05-2019)

### EVO: 2
- [#17086](https://parermine.regione.emilia-romagna.it//issues/17086) Gestione parametri per multiconservatore SACER
- [#14940](https://parermine.regione.emilia-romagna.it//issues/14940) Sostituzione strumento importa / duplica struttura - CONSIP (WBS 04 - Import export  - GG PE 40)

### Bugfix: 3
- [#18533](https://parermine.regione.emilia-romagna.it//issues/18533) Errore servizio di recupero
- [#18238](https://parermine.regione.emilia-romagna.it//issues/18238) Formati file busta: valori mancanti nella combo
- [#17530](https://parermine.regione.emilia-romagna.it//issues/17530) Errore azione "Indietro" in pagina Dettaglio tipo rappresentazione componente

### Novità: 5
- [#18203](https://parermine.regione.emilia-romagna.it//issues/18203) Gestione date validità struttura
- [#18056](https://parermine.regione.emilia-romagna.it//issues/18056) Gestione versioni dei servizi
- [#17494](https://parermine.regione.emilia-romagna.it//issues/17494) Impronta calcolata sugli oggetti conservati: passare a SHA-256
- [#17430](https://parermine.regione.emilia-romagna.it//issues/17430) Modifiche al modulo di amministrazione Sacer per introduzione multiconservatore
- [#16707](https://parermine.regione.emilia-romagna.it//issues/16707) Aggiungere i parametri di forzatura dell'aggiunta documento

## 5.6.12 (16-04-2019)

### Novità: 1
- [#18420](https://parermine.regione.emilia-romagna.it//issues/18420) Modifica job ControllaMigrazioneSubPartizione

## 5.6.11 (04-04-2019)

### Bugfix: 1
- [#18167](https://parermine.regione.emilia-romagna.it//issues/18167) Job VERIFICA_FIRME_DT_VERS: set timestamp su elenchi sono se serve

### Novità: 1
- [#18188](https://parermine.regione.emilia-romagna.it//issues/18188) Reset stato contenuto elenco versamento

## 5.6.10

### Bugfix: 1
- [#18134](https://parermine.regione.emilia-romagna.it//issues/18134) Export / import struttura: problema nel codice normalizzato della struttura

## 5.6.9 (13-03-2019)

### Novità: 1
- [#18002](https://parermine.regione.emilia-romagna.it//issues/18002) Multiistanza per il job PRODUCER_CODA_DA_MIGRARE

## 5.6.8 (11-03-2019)

### Bugfix: 1
- [#17997](https://parermine.regione.emilia-romagna.it//issues/17997) Errore sul pulsante ricerca degli elenchi indici aip da firmare

### Novità: 1
- [#17991](https://parermine.regione.emilia-romagna.it//issues/17991) Ottimizzazione fase di produzione dei messaggi relativi alla migrazione

## 5.6.7 (06-03-2019)

### Bugfix: 1
- [#17912](https://parermine.regione.emilia-romagna.it//issues/17912) Job Verifica migrazione partizione: errore nel settare stato = MIGRATA

### Novità: 3
- [#17953](https://parermine.regione.emilia-romagna.it//issues/17953) Abilitazione log delle query SQL
- [#17920](https://parermine.regione.emilia-romagna.it//issues/17920) Riepilogo per struttura: introdurre indicazione obbligatoria dell'Ambiente
- [#17917](https://parermine.regione.emilia-romagna.it//issues/17917) Elenchi di versamento: eliminare indicatore "Versamenti annullati"

## 5.6.6.1 (07-03-2019)

### Novità: 1
- [#17957](https://parermine.regione.emilia-romagna.it//issues/17957) Abilitazione log delle query SQL (backport di MEV #17953)

## 5.6.6 (28-02-2019)

### Bugfix: 1
- [#17873](https://parermine.regione.emilia-romagna.it//issues/17873) ClassCastException in produzione nel job di migrazione

## 5.6.5 (26-02-2019)

### Bugfix: 3
- [#17843](https://parermine.regione.emilia-romagna.it//issues/17843) Memory leak derivato da job di migrazione
- [#17796](https://parermine.regione.emilia-romagna.it//issues/17796) Ricerca elenchi fascicolo per stato
- [#17718](https://parermine.regione.emilia-romagna.it//issues/17718) Verifica firma in presenza di CRL non scaricabili

## 5.6.4 (21-02-2019)

### Bugfix: 2
- [#17705](https://parermine.regione.emilia-romagna.it//issues/17705) Consumer VerificaFirmeDataVersamento: non viene settato lo stato che consente di rielaborare l'unità doc
- [#17635](https://parermine.regione.emilia-romagna.it//issues/17635) Firma Indice AIP fascicolo: le UD non assumono stato IN ARCHIVIO

### Novità: 4
- [#17827](https://parermine.regione.emilia-romagna.it//issues/17827) Monitoraggio: eliminare dall'interfaccia menu Riepilogo per struttura e pulsante Calcola chiave UD...
- [#17686](https://parermine.regione.emilia-romagna.it//issues/17686) Elenchi di versamento da validare: introdurre filtro  Modalità validazione elenco
- [#17663](https://parermine.regione.emilia-romagna.it//issues/17663) Multiistanza per job PREPARA_PARTIZIONE_DA_MIGRARE
- [#17605](https://parermine.regione.emilia-romagna.it//issues/17605) Migrazione: Job per controllo contenuto partizione

## 5.6.3 (12-02-2019)

### Novità: 1
- [#17594](https://parermine.regione.emilia-romagna.it//issues/17594) Eliminazione jks dal sorgente dell'applicazione.

## 5.6.2

### Bugfix: 2
- [#17630](https://parermine.regione.emilia-romagna.it//issues/17630) Problema creazione indice AIP ud da validazione fascicolo
- [#17626](https://parermine.regione.emilia-romagna.it//issues/17626) Problema su Messa in coda del contenuto degli elenchi

## 5.6.1 (07-02-2019)

### Bugfix: 3
- [#17532](https://parermine.regione.emilia-romagna.it//issues/17532) Stati di conservazione del fascicolo non gestiti correttamente
- [#17527](https://parermine.regione.emilia-romagna.it//issues/17527) Job validazione fascicoli in presenza di ud in volume di conservazione
- [#17524](https://parermine.regione.emilia-romagna.it//issues/17524) Problema su ricerca elenchi versamento fascicoli

### Novità: 6
- [#17408](https://parermine.regione.emilia-romagna.it//issues/17408) Modifica al servizio di recupero ud per eseguirne recupero da object storage se ud migrata
- [#17324](https://parermine.regione.emilia-romagna.it//issues/17324) Divisione job di migrazione 
- [#17237](https://parermine.regione.emilia-romagna.it//issues/17237) Mettere in coda non gli elenchi ma il contenuto degli elenchi
- [#17186](https://parermine.regione.emilia-romagna.it//issues/17186) Validazione elenchi e Firma Indici AIP: introdurre filtro di ricerca su range di date
- [#16706](https://parermine.regione.emilia-romagna.it//issues/16706) Visualizzare flag su aggregazioni per aggiornamento e annullamento unità documentarie
- [#16241](https://parermine.regione.emilia-romagna.it//issues/16241) Abilitazione token (csrf) su tag libray di spago-lite utilizzate per generare apposite form (HTTP POST)

## 5.6.0 (15-01-2019)

### EVO: 1
- [#16221](https://parermine.regione.emilia-romagna.it//issues/16221) Modifiche per introduzione servizio aggiornamento metadati

### Bugfix: 5
- [#17233](https://parermine.regione.emilia-romagna.it//issues/17233) Problema di timeout nel job di annullamento versamenti
- [#17063](https://parermine.regione.emilia-romagna.it//issues/17063) Errori sul Tipo rappresentazione componente
- [#16820](https://parermine.regione.emilia-romagna.it//issues/16820) Problema in creazione indice AIP fascicoli
- [#16802](https://parermine.regione.emilia-romagna.it//issues/16802) Job validazione fascicoli in presenza di ud in volume di conservazione
- [#16413](https://parermine.regione.emilia-romagna.it//issues/16413) Lentezza nel caricamento pagina elenchi versamento firmare

### Novità: 4
- [#17166](https://parermine.regione.emilia-romagna.it//issues/17166) Parametro per numero massimo di elenchi di versamento da creare al giorno per criterio
- [#16412](https://parermine.regione.emilia-romagna.it//issues/16412) Aggiornamento disciplinare tecnico
- [#15889](https://parermine.regione.emilia-romagna.it//issues/15889) Strutture versanti: nuovi campi e modifiche a quelli esistenti
- [#15116](https://parermine.regione.emilia-romagna.it//issues/15116) Estensione richieste di annullamento 

## 5.5.4 (17-01-2019)

### Novità: 1
- [#17330](https://parermine.regione.emilia-romagna.it//issues/17330) Modifica job di verifica firme per elaborare prima elenchi fiscali

## 5.5.3 (15-01-2019)

### Bugfix: 1
- [#17307](https://parermine.regione.emilia-romagna.it//issues/17307) Miglioramento della chiusura delle risorse durante la spedizioni di messaggi JMS

## 5.5.2 (14-12-2018)

### Bugfix: 1
- [#17068](https://parermine.regione.emilia-romagna.it//issues/17068) Modifiche a ConsumerCodaIndiceAipUnitaDoc

## 5.5.1 (13-12-2018)

### Bugfix: 2
- [#17040](https://parermine.regione.emilia-romagna.it//issues/17040) Job di creazione indici aip - errore nell'accodamento dei record 
- [#16991](https://parermine.regione.emilia-romagna.it//issues/16991) errore nel job di evasione richieste annullamento

## 5.5.0 (26-11-2018)

### Bugfix: 3
- [#16726](https://parermine.regione.emilia-romagna.it//issues/16726) Verifica firme in presenza di ud annullate
- [#16687](https://parermine.regione.emilia-romagna.it//issues/16687) AIP ud: problema nei documenti inseriti nello zip
- [#16686](https://parermine.regione.emilia-romagna.it//issues/16686) Job validazione fascicoli in presenza di ud annullate

### Novità: 3
- [#16489](https://parermine.regione.emilia-romagna.it//issues/16489) Calcolo nuovi URN nell'ambito della gestione fascicoli
- [#16317](https://parermine.regione.emilia-romagna.it//issues/16317) Dettaglio unità documentaria: aggiungere dati
- [#16316](https://parermine.regione.emilia-romagna.it//issues/16316) Configurazione strutture: inserire valori normalizzati

## 5.4.1 (05-11-2018)

### Bugfix: 1
- [#16528](https://parermine.regione.emilia-romagna.it//issues/16528) Inserire nella lab di sacer la voce per testare servizio di aggiornamento metadati

## 5.4.0 (05-11-2018)

### EVO: 1
- [#9558](https://parermine.regione.emilia-romagna.it//issues/9558) FASCICOLI Fase 2 - Pacchetto di archiviazione (AIP)

### Bugfix: 5
- [#16069](https://parermine.regione.emilia-romagna.it//issues/16069) Aumento tempo di deploy - timeout
- [#16030](https://parermine.regione.emilia-romagna.it//issues/16030) Contromisure alla vulnerabilità session termination
- [#15991](https://parermine.regione.emilia-romagna.it//issues/15991) Ricerca elenchi di versamento in errore impostando il registro
- [#15934](https://parermine.regione.emilia-romagna.it//issues/15934) Eliminazione associazione tra modello di serie e tipo unità documentaria
- [#15428](https://parermine.regione.emilia-romagna.it//issues/15428) Elenchi di versamento da firmare: anomalie dell'interfaccia

### Novità: 7
- [#16055](https://parermine.regione.emilia-romagna.it//issues/16055) Modifiche a ricerca elenchi versamento per recepire gestione code
- [#15989](https://parermine.regione.emilia-romagna.it//issues/15989) Link alla struttura nella colonna di sinistra
- [#15883](https://parermine.regione.emilia-romagna.it//issues/15883) Validazione elenchi: consentire modifiche su elenchi in stato CHIUSO
- [#15771](https://parermine.regione.emilia-romagna.it//issues/15771) Importa tipo unità documentaria: non importare i formati
- [#15388](https://parermine.regione.emilia-romagna.it//issues/15388) Visualizzazione fascicolo: inserire stato presa in carico
- [#13351](https://parermine.regione.emilia-romagna.it//issues/13351) AIP di unità documentaria: prevedere pagina di visualizzazione dettaglio e includere nell'AIP le evidenze di firma dell'Indice
- [#11008](https://parermine.regione.emilia-romagna.it//issues/11008) Configurazione strutture: aggiungere regole su Dettaglio sottostruttura

## 5.3.8 (30-10-2018)

### Bugfix: 1
- [#16424](https://parermine.regione.emilia-romagna.it//issues/16424) Modifiche a ConsumerCodaIndiceAipUnitaDoc

## 5.3.7 (23-10-2018)

### Bugfix: 1
- [#16385](https://parermine.regione.emilia-romagna.it//issues/16385) Job creazione indici AIP - stato elenco non coerente con stati UD

## 5.3.6 (22-10-2018)

### Bugfix: 1
- [#16379](https://parermine.regione.emilia-romagna.it//issues/16379) Stato ud non coerente con stato elenco indice AIP

## 5.3.5 (11-10-2018)

### Bugfix: 3
- [#16275](https://parermine.regione.emilia-romagna.it//issues/16275) Job creazione elenchi indici aip ud: gestione controllo partizionamento
- [#16269](https://parermine.regione.emilia-romagna.it//issues/16269) Modifica ai parametri per coda verifica firme
- [#16265](https://parermine.regione.emilia-romagna.it//issues/16265) Verifica firme: gestire lock per gestione code

## 5.3.4 (13-09-2018)

### Bugfix: 1
- [#16060](https://parermine.regione.emilia-romagna.it//issues/16060) Errore nella gestione delle code

## 5.3.3 (12-09-2018)

### Bugfix: 1
- [#15632](https://parermine.regione.emilia-romagna.it//issues/15632) Impostazione flag HttpOnly e secure sul cookie JSESSIONID

### Novità: 1
- [#15295](https://parermine.regione.emilia-romagna.it//issues/15295) Elaborazione elenchi di versamento - operatività su tutti i nodi

## 5.3.2 (04-09-2018)

### Bugfix: 1
- [#15964](https://parermine.regione.emilia-romagna.it//issues/15964) Modifica XSD UniSiNCRO

### Novità: 2
- [#15841](https://parermine.regione.emilia-romagna.it//issues/15841) Parametrizzazione delle informazioni di validazione degli elenchi
- [#15751](https://parermine.regione.emilia-romagna.it//issues/15751) Elenchi di versamento: validazione manuale e senza firma

## 5.3.1 (10-08-2018)

### Bugfix: 3
- [#15847](https://parermine.regione.emilia-romagna.it//issues/15847) Cambiare valori di default su validazione elenco
- [#15840](https://parermine.regione.emilia-romagna.it//issues/15840) Richiesta di annullamento versamento
- [#15784](https://parermine.regione.emilia-romagna.it//issues/15784) Problema nel calcolo contenuto Sacer

## 5.3.0 (30-07-2018)

### Bugfix: 4
- [#15716](https://parermine.regione.emilia-romagna.it//issues/15716) Elaborazione indici AIP in presenza di elenco con tutte le ud annullate
- [#15654](https://parermine.regione.emilia-romagna.it//issues/15654) Problema nella creazione richiesta di annullamento immediata
- [#15545](https://parermine.regione.emilia-romagna.it//issues/15545) rischio di IP spoofing legato all'utilizzo dell'header http X-Forwarded-For.
- [#15488](https://parermine.regione.emilia-romagna.it//issues/15488) Modifica nome tabelle ELV_STATO_CONSERV_FASCICOLO e ELV_STATO_FASCICOLO_ELENCO

### Novità: 7
- [#15456](https://parermine.regione.emilia-romagna.it//issues/15456) Elenchi di versamento: validazione automatica e senza firma
- [#15004](https://parermine.regione.emilia-romagna.it//issues/15004) Fascicolo: informazione elenco di versamento di appartenenza
- [#14939](https://parermine.regione.emilia-romagna.it//issues/14939) Visualizzazione ud: lista fascicoli e serie di appartenenza
- [#14812](https://parermine.regione.emilia-romagna.it//issues/14812) Sostituzione di Castor
- [#14714](https://parermine.regione.emilia-romagna.it//issues/14714) Gestione periodo di validità su utilizzo del modello di xsd nel tipo fascicolo
- [#14705](https://parermine.regione.emilia-romagna.it//issues/14705) Gestione modelli tipo fascicolo
- [#14098](https://parermine.regione.emilia-romagna.it//issues/14098) Gestione elenchi: modifiche alla gestione

## 5.2.1 (27-06-2018)

### Bugfix: 1
- [#15506](https://parermine.regione.emilia-romagna.it//issues/15506) Risposta evasione annullamento con tutti gli item non annullabili

## 5.2.0 (22-06-2018)

### EVO: 1
- [#8248](https://parermine.regione.emilia-romagna.it//issues/8248) Produzione disciplinare tecnico da Sistema

### Novità: 1
- [#15358](https://parermine.regione.emilia-romagna.it//issues/15358) Creazione struttura solo se c'è un accordo in corso di validità

## 5.1.2 (18-06-2018)

### Bugfix: 1
- [#15387](https://parermine.regione.emilia-romagna.it//issues/15387) Errore nell'aggiunta dei fascicoli all'elenco dei fascicoli da firmare

## 5.1.1 (13-06-2018)

### Bugfix: 3
- [#15235](https://parermine.regione.emilia-romagna.it//issues/15235) Anomalie nella gestione degli elenchi fascicoli
- [#15197](https://parermine.regione.emilia-romagna.it//issues/15197) Servizi di annullamento: controllo in fase di chiusura ed evasione sull'utente che ha creatola richiesta
- [#14757](https://parermine.regione.emilia-romagna.it//issues/14757) Modifiche al monitoraggio dei fascicoli

### Novità: 2
- [#15191](https://parermine.regione.emilia-romagna.it//issues/15191) Serie / Ricerca serie: aggiunta pulsanti per scaricare pacchetto archiviazione
- [#14938](https://parermine.regione.emilia-romagna.it//issues/14938) Gestione firma elenchi di versamento fascicoli

## 5.1.0 (10-05-2018)

### EVO: 1
- [#13991](https://parermine.regione.emilia-romagna.it//issues/13991) Fascicoli fase 2 - Gestione elenchi di versamento

### Bugfix: 3
- [#14982](https://parermine.regione.emilia-romagna.it//issues/14982) Eliminazione metodo inutilizzato nel Monitoraggio UD
- [#14753](https://parermine.regione.emilia-romagna.it//issues/14753) Amministrazione fascicoli: impedire la cancellazione di periodi di validità usati nei versamenti
- [#13675](https://parermine.regione.emilia-romagna.it//issues/13675) Problema java.lang.NoClassDefFoundError su JBoss in ambiente di TEST

### Novità: 5
- [#14922](https://parermine.regione.emilia-romagna.it//issues/14922) Configurazione job "registra schedulazioni job TPI"
- [#13755](https://parermine.regione.emilia-romagna.it//issues/13755) Servizi di annullamento: unità documentarie versate da PING e controllo sui tipi di dato
- [#13740](https://parermine.regione.emilia-romagna.it//issues/13740) Gestione elenchi contenenti versamenti annullati
- [#13455](https://parermine.regione.emilia-romagna.it//issues/13455) Unità documentaria: gestione stato AIP FIRMATO e aggiornamento AIP
- [#13439](https://parermine.regione.emilia-romagna.it//issues/13439) Tipo gestione elenco indici AIP: riportare informazione su criterio e renderla modificabile

## 5.0.3 (16-04-2018)

### Novità: 1
- [#14764](https://parermine.regione.emilia-romagna.it//issues/14764) Job per verifica formato numero fascicoli

## 5.0.2.1

### Novità: 1
- [#15331](https://parermine.regione.emilia-romagna.it//issues/15331) Errore VerificaFirma.ControlloCRL su GIORNALE DI CASSA

## 5.0.2 (30-03-2018)

### Bugfix: 1
- [#14740](https://parermine.regione.emilia-romagna.it//issues/14740) Cancellazione informazione ente convenzionato sulla struttura

## 5.0.1 (29-03-2018)

### Novità: 2
- [#14594](https://parermine.regione.emilia-romagna.it//issues/14594) Modifiche gestione fascicoli
- [#14576](https://parermine.regione.emilia-romagna.it//issues/14576) Visualizzazione xsd metadati fascicolo

## 5.0.0 (14-03-2018)

### DOC: 1
- [#18298](https://parermine.regione.emilia-romagna.it//issues/18298) Manuale di Conservazione

### EVO: 3
- [#13384](https://parermine.regione.emilia-romagna.it//issues/13384) FASCICOLI - Gestione on line ricerca e visualizzazione fascicolo
- [#13383](https://parermine.regione.emilia-romagna.it//issues/13383) FASCICOLI - Gestione on line monitoraggio
- [#9557](https://parermine.regione.emilia-romagna.it//issues/9557) FASCICOLI - Gestione on line configurazione

### Bugfix: 2
- [#13920](https://parermine.regione.emilia-romagna.it//issues/13920) Scelta Organizzazione per utente non ancora replicato
- [#13467](https://parermine.regione.emilia-romagna.it//issues/13467) Duplicazioni formati disattivati: riattivazione al salvataggio

### Novità: 1
- [#9473](https://parermine.regione.emilia-romagna.it//issues/9473) Controlli sulla compilazione della parte di numero di un registro coincidente con il tipo registro

## 4.13.1 (07-03-2018)

### Bugfix: 3
- [#14465](https://parermine.regione.emilia-romagna.it//issues/14465) Inibizione dei pulsanti nella pagina di gestione job
- [#14444](https://parermine.regione.emilia-romagna.it//issues/14444) Servizio richiesta di annullamento: aumentare dimensione del codice che identifica la richiesta
- [#14442](https://parermine.regione.emilia-romagna.it//issues/14442) Errato controllo sovrapposizione periodo associazione struttura-ente convenzionato

## 4.13.0 (26-02-2018)

### Novità: 2
- [#13875](https://parermine.regione.emilia-romagna.it//issues/13875) Integrazione nuova versione libreria SD-DSS per il ws di verifica EIDAS
- [#13473](https://parermine.regione.emilia-romagna.it//issues/13473) Controllo su associazione ente convenzionato / struttura

## 4.12.5 (16-02-2018)

### Novità: 1
- [#14318](https://parermine.regione.emilia-romagna.it//issues/14318) Modifica pop up della firma con HSM

## 4.12.3 (14-02-2018)

### Bugfix: 2
- [#14074](https://parermine.regione.emilia-romagna.it//issues/14074) Messaggio di errore al termine della sessione di firma
- [#14043](https://parermine.regione.emilia-romagna.it//issues/14043) Gestione sessioni di apertura chiusura sessione di firma HSM

### Novità: 1
- [#14080](https://parermine.regione.emilia-romagna.it//issues/14080) Aggiungere la gestione dell'utente bloccato HSM

## 4.12.1 (23-01-2018)

### Bugfix: 1
- [#13964](https://parermine.regione.emilia-romagna.it//issues/13964) Firma HSM di Elenchi indici AIP

## 4.12.0 (08-01-2018)

### Bugfix: 2
- [#13840](https://parermine.regione.emilia-romagna.it//issues/13840) Elenchi di versamento da firmare
- [#13586](https://parermine.regione.emilia-romagna.it//issues/13586) Problema di navigazione nel monitoraggio

### Novità: 2
- [#13775](https://parermine.regione.emilia-romagna.it//issues/13775) Dettaglio strutture: prevedere il pulsante Mostra record non attivi su tutte le tabelle
- [#13109](https://parermine.regione.emilia-romagna.it//issues/13109) Disattivazione automatica degli utenti automa in caso di ripetuti fallimenti del login

## 4.11.5 (12-12-2017)

### Bugfix: 1
- [#13738](https://parermine.regione.emilia-romagna.it//issues/13738) Creazione elenco indice AIP

## 4.11.4 (04-12-2017)

### Bugfix: 1
- [#13681](https://parermine.regione.emilia-romagna.it//issues/13681) Importa parametri di configurazione - Criteri di raggruppamento non fiscali

## 4.11.3 (28-11-2017)

### Novità: 2
- [#13601](https://parermine.regione.emilia-romagna.it//issues/13601) Indicazione ip client nel log
- [#13588](https://parermine.regione.emilia-romagna.it//issues/13588) Tracciamento del timestamp di inizio / fine sessione versamento

## 4.11.2 (13-11-2017)

### Bugfix: 2
- [#13475](https://parermine.regione.emilia-romagna.it//issues/13475) Lista operazioni elenco di versamento: data non visualizzata e operazione loggata due volte
- [#13444](https://parermine.regione.emilia-romagna.it//issues/13444) Cancellazione nodo ambito territoriale restituisce errore critico

## 4.11.1 (08-11-2017)

### Bugfix: 1
- [#13260](https://parermine.regione.emilia-romagna.it//issues/13260)  Problema sulla duplicazione di alcuni formati 

### Novità: 2
- [#13419](https://parermine.regione.emilia-romagna.it//issues/13419) Tipo gestione elenco indici AIP: riportare informazione su elenco di versamento
- [#13249](https://parermine.regione.emilia-romagna.it//issues/13249) Ricerca unità documentarie: consentire di scaricare il risultato della ricerca indipendentemente dal numero dei risultati

## 4.11.0 (23-10-2017)

### EVO: 1
- [#9636](https://parermine.regione.emilia-romagna.it//issues/9636) Revisione processo di conservazione fiscale

### Bugfix: 1
- [#13048](https://parermine.regione.emilia-romagna.it//issues/13048) Creazione criterio di raggruppamento in una struttura senza ente convenzionato

### Novità: 3
- [#10502](https://parermine.regione.emilia-romagna.it//issues/10502) Modificare denominazione "Stato dell'unità documentaria nell'elenco di versamento"
- [#9720](https://parermine.regione.emilia-romagna.it//issues/9720) Aggiungere ai filtri di ricerca UD ID documento
- [#8527](https://parermine.regione.emilia-romagna.it//issues/8527) Controlli semantici al versamento: aggiungere controllo obbligatorietà dei dati di profilo UD

## 4.10.1 (21-09-2017)

### Bugfix: 7
- [#12249](https://parermine.regione.emilia-romagna.it//issues/12249) Modifica ordinamento tabella Regole su sottostrutture
- [#12157](https://parermine.regione.emilia-romagna.it//issues/12157) Eliminazione di un xsd con un attributo legato a un tipo serie
- [#12105](https://parermine.regione.emilia-romagna.it//issues/12105) Caricamento xsd di un tipo ud
- [#12025](https://parermine.regione.emilia-romagna.it//issues/12025) Duplica / Importa struttura - problema nella registrazione dei log
- [#11959](https://parermine.regione.emilia-romagna.it//issues/11959) Ambito territoriale - controllo su eliminazione nodo
- [#11267](https://parermine.regione.emilia-romagna.it//issues/11267) Assenza comandi di navigazione e azioni su pagina Dettaglio elenco di versamento dopo la chiusura manuale
- [#10694](https://parermine.regione.emilia-romagna.it//issues/10694) Errore nella modifica del formato ammesso

### Novità: 12
- [#12935](https://parermine.regione.emilia-romagna.it//issues/12935) Introduzione parametro per firmatario su HSM
- [#12682](https://parermine.regione.emilia-romagna.it//issues/12682) Gestione dell'errore: spazi presenti nel nome del metadato nell'XSD dei metadati specifici
- [#12501](https://parermine.regione.emilia-romagna.it//issues/12501) Rimozione Job di migrazione blob Telecom -> Tivoli
- [#12191](https://parermine.regione.emilia-romagna.it//issues/12191) Ordinamento alfabetico degli enti convenzionati da selezionare in Dettaglio struttura
- [#12027](https://parermine.regione.emilia-romagna.it//issues/12027) Prevedere lista associazioni con enti convenzionati in Dettaglio struttura
- [#11822](https://parermine.regione.emilia-romagna.it//issues/11822) Flag criterio standard nella pagina di dettaglio struttura
- [#11638](https://parermine.regione.emilia-romagna.it//issues/11638) Tipizzazione o gestione errore di certificato duplicato
- [#11165](https://parermine.regione.emilia-romagna.it//issues/11165) Eliminare controllo su numerazione della versione degli XSD dei dati specifici
- [#11004](https://parermine.regione.emilia-romagna.it//issues/11004) Gestione strutture versanti: sistemazione gestione sistema di migrazione
- [#10747](https://parermine.regione.emilia-romagna.it//issues/10747) Gestire lunghezza massima del numero della chiave UD in modo flessibile
- [#10693](https://parermine.regione.emilia-romagna.it//issues/10693) Eliminare possibilità di modificare alcune tipologie di record
- [#7520](https://parermine.regione.emilia-romagna.it//issues/7520) WS VERSAMENTO: controllo sui metadati dei Collegamenti (registro, anno, numero)

## 4.10.0 (07-09-2017)

### EVO: 1
- [#8245](https://parermine.regione.emilia-romagna.it//issues/8245) Firma remota

## 4.9.6 (20-07-2017)

### Novità: 1
- [#12446](https://parermine.regione.emilia-romagna.it//issues/12446) Firma con nuovi certificati 2017

## 4.9.5

### LAB: 1
- [#8240](https://parermine.regione.emilia-romagna.it//issues/8240) Migrazione a JBOSS - Fase 1

## 4.9.3 (05-06-2017)

### Bugfix: 6
- [#11961](https://parermine.regione.emilia-romagna.it//issues/11961) Integrazione con versione 1.0.9 Sacer log
- [#11957](https://parermine.regione.emilia-romagna.it//issues/11957) Creazione serie - flag FL_UPD_ANNUL_UNITA_DOC
- [#11949](https://parermine.regione.emilia-romagna.it//issues/11949) Visualizzazione albero categoria tipo ud e ambito territoriale
- [#11925](https://parermine.regione.emilia-romagna.it//issues/11925) Job verifica firme
- [#11916](https://parermine.regione.emilia-romagna.it//issues/11916) Modifica messaggio di errore per upload CSV con formato errato
- [#11915](https://parermine.regione.emilia-romagna.it//issues/11915) Versionamento del servizio di annullamento

### Novità: 2
- [#11668](https://parermine.regione.emilia-romagna.it//issues/11668) Gestire nuovo tipo uso componente FIRMA_ELETTRONICA
- [#10407](https://parermine.regione.emilia-romagna.it//issues/10407) Configurazione strutture: prevedere ente convenzionato come dato obbligatorio

## 4.9.2 (23-05-2017)

### Bugfix: 2
- [#11871](https://parermine.regione.emilia-romagna.it//issues/11871) Integrazione con framework 1.0.17
- [#11868](https://parermine.regione.emilia-romagna.it//issues/11868) Recepire versione 1.0.8 Log

## 4.9.1 (22-05-2017)

### Bugfix: 2
- [#11816](https://parermine.regione.emilia-romagna.it//issues/11816) Denominazione file nella Dichiarazione DIP per l'esibizione
- [#11804](https://parermine.regione.emilia-romagna.it//issues/11804) Eliminazione vista APL_V_TIPO_OGGETTO da Sacer Log e Package per USR_V_ABIL_ORGANIZ

## 4.9.0 (17-05-2017)

### EVO: 1
- [#8596](https://parermine.regione.emilia-romagna.it//issues/8596) DIP per l'esibizione (pacchetto di distribuzione finalizzato all'esibizione)

### Bugfix: 6
- [#11507](https://parermine.regione.emilia-romagna.it//issues/11507) In Dettaglio struttura mostrare XSD più recente solo se attivo
- [#11492](https://parermine.regione.emilia-romagna.it//issues/11492) Periodo validità registro
- [#11171](https://parermine.regione.emilia-romagna.it//issues/11171) Pulsante Creazione Criterio Raggruppamento
- [#10916](https://parermine.regione.emilia-romagna.it//issues/10916) Modelli tipi serie - Modifica a regole di rappresentazione, regole di acquisizione, filtri su dati specifici
- [#10667](https://parermine.regione.emilia-romagna.it//issues/10667) Controlli alla configurazione dei criteri automatici e alla loro duplicazione via job
- [#9568](https://parermine.regione.emilia-romagna.it//issues/9568) Tag "Annesso" vuoto in response versamento per alcune tipologie di errore

### Novità: 11
- [#11419](https://parermine.regione.emilia-romagna.it//issues/11419) Integrazione con libreria sacer log 1.0.4
- [#11049](https://parermine.regione.emilia-romagna.it//issues/11049) Gestione tipo servizio di conservazione e di attivazione in importa e duplica struttura e importa tipo ud
- [#10823](https://parermine.regione.emilia-romagna.it//issues/10823) Importa parametri di configurazione in modo massivo
- [#10118](https://parermine.regione.emilia-romagna.it//issues/10118) Annullamento UD: consentire l'annullamento del versamento indipendentemente dallo stato di conservazione 
- [#10115](https://parermine.regione.emilia-romagna.it//issues/10115) Annullamento versamenti UD: consentire l'annullamento lo stesso giorno del versamento
- [#10080](https://parermine.regione.emilia-romagna.it//issues/10080) Log Sacer su componenti di amministrazione escluse da primo rilascio
- [#9831](https://parermine.regione.emilia-romagna.it//issues/9831) Note degli elenchi di versamento contenenti ud annullate
- [#9544](https://parermine.regione.emilia-romagna.it//issues/9544) Gestire più collegamenti a una stessa unità documentaria se questi hanno descrizioni diverse
- [#9016](https://parermine.regione.emilia-romagna.it//issues/9016) Estensione funzionalità di annullamento: modalità sincrona
- [#8877](https://parermine.regione.emilia-romagna.it//issues/8877) Annullamento versamento UD: prevedere upload file anche dopo che la richiesta è stata creata
- [#8214](https://parermine.regione.emilia-romagna.it//issues/8214) Pulizia database e classi Sacer

## 4.8.6 (10-05-2017)

### Bugfix: 1
- [#11728](https://parermine.regione.emilia-romagna.it//issues/11728) Revisione job VerificaFirme alla data di versamento

## 4.8.5

### Bugfix: 1
- [#11321](https://parermine.regione.emilia-romagna.it//issues/11321) Revisione job VerificaFirme alla data di versamento

## 4.8.4 (29-03-2017)

### Bugfix: 1
- [#11289](https://parermine.regione.emilia-romagna.it//issues/11289) Recepire le modifiche eseguite sulle versioni 4.7.4 e 4.7.5 

## 4.8.3

### Novità: 1
- [#11176](https://parermine.regione.emilia-romagna.it//issues/11176) Job Scarico CA - nuovo link pagina CA

## 4.8.2 (08-03-2017)

### Bugfix: 2
- [#11030](https://parermine.regione.emilia-romagna.it//issues/11030) Ambito territoriale: errore critico in fase di creazione
- [#11029](https://parermine.regione.emilia-romagna.it//issues/11029) Elenchi di versamento: contatori documenti aggiunti

### Novità: 2
- [#11016](https://parermine.regione.emilia-romagna.it//issues/11016) Disattivazione dell'xsd del tipo componente
- [#10948](https://parermine.regione.emilia-romagna.it//issues/10948) Modifica pagina Dettaglio serie

## 4.8.1 (08-02-2017)

### Bugfix: 1
- [#10706](https://parermine.regione.emilia-romagna.it//issues/10706) Inserimento ricerca serie nel pacchetto sacer 4.8.1

## 4.7.5

### Bugfix: 1
- [#11265](https://parermine.regione.emilia-romagna.it//issues/11265) Job Creazione Indice AIP: interventi per migliorare le prestazioni

## 4.7.4 (22-03-2017)

### Bugfix: 1
- [#11245](https://parermine.regione.emilia-romagna.it//issues/11245) Miglioramento prestazioni Job Creazione Indice AIP
