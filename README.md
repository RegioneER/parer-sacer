# SACER 

Fonte template redazione documento:  https://www.makeareadme.com/.


# Descrizione

SACER è il modulo centrale del Sistema di Conservazione e gestisce l'intero processo di conservazione di Unità documentarie e Fascicoli, a partire dall'acquisizione dei Pacchetti di versamento (SIP) via web-service fino alla generazione dei Pacchetti di Archiviazione (AIP) e la messa a disposizione dei Pacchetti di distribuzione (DIP). SACER mette a disposizione, sia tramite web service, sia  tramite interfaccia utente, funzionalità di annullamento degli oggetti versati. Inoltre, il sistema consente di organizzare Unità documentarie e Fascicoli in Serie, le quali sono a loro volta sottoposte. 


# Installazione

Requisiti minimi per installazione: 

- Sistema operativo : consigliato Linux server (in alternativa compatibilità con Windows server);
- Java versione 8 (OpenJDK / Oracle);
- JBoss 7 EAP;
- Oracle DB (versione consigliata 19c).

## Instalazione JDK 

Consigliata adozione della OpenJDK alla versione 8, guida all'installazione https://openjdk.org/install/.

## Setup application server (JBoss 7)

Richiesta l'esecuzione delle seguenti guide secondo l'ordine riportato di seguito: 

1. guida per la configurazione **base** di [guida 1](src/docs/JBoss7_configurazione_generale.md);
2. guida con le configurazioni **specifiche** per il contesto applicativo **SIAM**  di [guida 2](src/docs/JBoss7_configurazione_siam.md).

### Deploy su JBoss 7

Di seguito le indicazioni per il rilascio su application server JBoss7: 

1. generazione dell'artifact attraverso tool maven, eseguire il seguente comando: 

   ```bash
   mvn package
   ```
   
2. viene generato l'artifact .ear all'interno del modulo sacer-ear/target (e.g. sacer-9.3.1.ear)
3. deploy dell'ear generato allo step 1 su JBoss 7 (vedi configurazione [setup JBoss7](#setup-application-server-jboss-7))

## Predisposizione database

L'applicazione utilizza come DBMS di riferimento Oracle DB (https://www.oracle.com/it/database/) alla versione, consigliata, **19c**. Per l'installazione e la configurazione fare riferimento alle guide ufficiali.

Per la creazione del modello E-R consultare il seguente [README.md](https://github.com/RegioneER/parer-db-init/blob/master/README.md) (progetto di riferimento https://github.com/RegioneER/parer-db-init).

## Configurazione bucket S3 Object Storage

L'applicazione può essere integrata sulla base delle configurazioni impostate, vedi paragrafo [Setup application server (Jboss 7)](#setup-application-server-jboss-7), ad uno storage grid o object storage attraverso lo standard [AWS S3](https://aws.amazon.com/it/s3/).

In questa sezione si vuole proporre un possibile scenario di configurazione dei bucket e, in particolar modo, di eventuali [lifecycle](https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lifecycle-mgmt.html) legati ad alcuni bucket, essenziali per gestire in modo corretto ed opportuno il ciclo di vita degli oggetti all'interno di determinati bucket; su quest'ultimo aspetto, si sottolinea, che alcuni flussi applicativi/servizi prevedono la creazione di oggetto "temporanei" ossia la cui esistenza è prevista per un determinato periodo temporale dopo il quale possono essere eliminati dal bucket stesso.

### Configurazione dei bucket

Una possibile configurazione dei bucket, proposta, è la seguente: 

- aggiornamenti-metadati : oggetti prodotti dal servizio di Aggiornamento Metadati (vedi README.md progetto "sacerws");
- aip : oggetti prodotti dai processi di creazione dell'AIP;
- componenti : oggetti prodotti dai servizi di Versamento Unità Documentarie/Aggiunta documenti (vedi README.md progetto "sacerws");
- indici-aip : oggetti prodotti dai processi di creazione degli indici AIP;
- reportvf : oggetti prodotti dalla creazione di report verifica firma su documenti processati dai servizi di Versamento Unità Documentarie/Aggiunta documenti (vedi README.md progetto "sacerws");
- sessioni-agg-md-err-ko  : oggetti prodotti dal servizio di Aggiornamento Metadati (vedi README.md progetto "sacerws");
- vrs-staging : oggetti prodotti dai servizi di Versamento Unità Documentarie/Aggiunta documenti (vedi README.md progetto "sacerws").

I bucket possono essere creati con / senza versioning, alcuni dei bucket prevedono l'applicazione di lifecycle policy (consigliato), nello specifico: 
- vrs-staging;
- sessioni-agg-md-err-ko.

#### Lifecyle policy : casi d'uso

Bucket : vrs-staging

Esempio di lifecycle applicata (con filtri per tag)

```json
{
    "Rules": [
        {
            "Expiration": {
                "Days": 183
            },
            "ID": "default_no_tag",
            "Filter": {
                "Prefix": ""
            },
            "Status": "Enabled"
        },
        {
            "Expiration": {
                "Days": 92
            },
            "ID": "file_componente_uddoc",
            "Filter": {
                "And": {
                    "Prefix": "",
                    "Tags": [
                        {
                            "Key": "vrs-object-type",
                            "Value": "file_componente_uddoc"
                        }
                    ]
                }
            },
            "Status": "Enabled"
        },
        {
            "Expiration": {
                "Days": 183
            },
            "ID": "xml_metadati_uddoc",
            "Filter": {
                "And": {
                    "Prefix": "",
                    "Tags": [
                        {
                            "Key": "vrs-object-type",
                            "Value": "xml_metadati_uddoc"
                        }
                    ]
                }
            },
            "Status": "Enabled"
        },
        {
            "Expiration": {
                "Days": 30
            },
            "ID": "orphan_objects",
            "Filter": {
                "And": {
                    "Prefix": "",
                    "Tags": [
                        {
                            "Key": "orphan",
                            "Value": "true"
                        }
                    ]
                }
            },
            "Status": "Enabled"
        },
        {
            "Expiration": {
                "Days": 2
            },
            "ID": "temporany_object",
            "Filter": {
                "And": {
                    "Prefix": "",
                    "Tags": [
                        {
                            "Key": "vrs-object-type",
                            "Value": "temporany"
                        }
                    ]
                }
            },
            "Status": "Enabled"
        }
    ]
}
```


# Utilizzo

SACER è il modulo centrale del Sistema di Conservazione e gestisce l'intero processo di conservazione di Unità documentarie e Fascicoli, a partire dall'acquisizione dei Pacchetti di versamento (SIP) via web-service fino alla generazione dei Pacchetti di Archiviazione (AIP) e la messa a disposizione dei Pacchetti di distribuzione (DIP). SACER mette a disposizione, sia tramite web service, sia  tramite interfaccia utente, funzionalità di annullamento degli oggetti versati. Inoltre, il sistema consente di organizzare Unità documentarie e Fascicoli in Serie, le quali sono a loro volta sottoposte. 

Le funzionalità implementate da Sacer coprono le varie fasi del processo di conservazione previste dallo standard OAIS, ovvero:  

* Acquisizione;
* Gestione dati;
* Archiviazione;
* Amministrazione;
* Accesso.   

**Le funzionalità di Acquisizione di SacER** consentono la gestione delle varie fasi del processo di Acquisizione e presa in carico. Operativamente si compongono delle seguenti attività:  

* acquisizione del SIP trasmesso attraverso i Servizi di versamento;  

* **memorizzazione** del SIP in un’area temporanea logicamente distinta dall’archivio vero e proprio per effettuare le verifiche previste;  

* verifica del SIP in relazione alla struttura dati, ai metadati degli oggetti sottoposti a conservazione, alle eventuali firme apposte sui file (**Oggetti-dati**) associati ai **Componenti**, ai formati dei file stessi e generazione del Rapporto di versamento in caso di verifica positiva;  

* restituzione dell’**Esito versamento**, comprensivo, in caso di esito positivo, del Rapporto di versamento;  

* creazione degli **Elenchi di versamento**: attraverso vari job eseguiti sugli Application server automaticamente e in modo ricorrente secondo intervalli di tempo stabiliti nel modulo Amministrazione i SIP sono raggruppati in Elenchi di versamento, validati dal Responsabile della funzione ed elaborati per produrre i relativi AIP; 

* eventuale migrazione di **formato** degli **Oggetti-dati** contenuti nei SIP sia per contrastarne l’obsolescenza tecnologica, sia per esigenze di miglioramento della fruibilità degli oggetti conservati;  

* estrazione dei metadati dal SIP (ed eventuale loro normalizzazione) e dal Sistema da utilizzare per completare le informazioni necessarie a generare l’AIP (**Informazioni sulla rappresentazione, Informazioni sulla conservazione, Informazioni sull’impacchettamento, Informazioni descrittive**);  

* generazione dell’**Indice dell’AIP**, utilizzando i metadati estratti dal SIP e quelli generati dal Sistema nel corso del processo di conservazione. SacER produce report di eccezioni a fronte di situazioni anomale nella creazione dell’**Indice dell’AIP**. Tutte le azioni vengono registrate sul sistema in apposite tabelle di log;  

* generazione dell’AIP, che avviene impacchettando in un oggetto auto-consistente l’**Indice dell’AIP**, le evidenze informatiche prodotte nel corso del processo di conservazione e gli **Oggetti-dati**.  


Le funzionalità di **Gestione Dati di SacER** sono finalizzate principalmente a gestire le Informazioni descrittive degli AIP generate durante il processo di acquisizione e includono:  

* la memorizzazione dei metadati estratti dal SIP o generati dal Sistema nel corso del processo di Acquisizione dei SIP;  

* la gestione degli aggiornamenti dei dati generati dalle funzionalità di Amministrazione e nel corso del processo di conservazione;  

* l’esecuzione delle ricerche e la sua restituzione alle funzionalità di Accesso, che avvengono mediante l’utilizzo di funzionalità da interfaccia web del Sistema o mediante chiamata a Servizi specifici.  


Le funzionalità di **Archiviazione di SacER** gestiscono la parte del processo di Gestione dell’AIP relativa alla memorizzazione e verifica degli Oggetti-dati su Data Base Oracle e file system (vedi paragrafo 7.5) e comprendono:  

* la **memorizzazione** degli AIP e l’organizzazione gerarchica dei supporti di memorizzazione. In particolare, gli Oggetti-dati degli AIP, costituiti dagli Indici degli AIP e dei SIP, dagli Esiti versamento, dai Rapporti di versamento e dai file associati ai Componenti, sono memorizzati su supporti di diverso tipo in ragione della loro dimensione e della frequenza con cui vengono ricercati:  

* gli **Oggetti-dati** vengono memorizzati in modo permanente nell’object storage; 

* gli **Oggetti-dati** di grande dimensione e di accesso meno frequente vengono salvati temporaneamente su file system in cartelle opportunamente strutturate, per poi essere memorizzati in modo permanente su supporti a cassette, utilizzando a questo scopo un apposito componente software chiamato TPI, descritto più avanti;  

* il controllo dell’integrità degli oggetti conservati, comprensivo della copia degli archivi, del controllo degli errori e delle procedure di refreshing dei supporti, in conformità con il Piano della Sicurezza del servizio di conservazione;  

* la restituzione dei pacchetti alle funzioni di Accesso, mediante opportune funzionalità dell’interfaccia web del Sistema o mediante l’utilizzo di Servizi di recupero esposti da Sacer e descritti nel documento Specifiche tecniche dei servizi di recupero;  

* l’annullamento dei versamenti effettuati, mediante opportune funzionalità dell’interfaccia web del Sistema o mediante l’utilizzo di Servizi di annullamento esposti da Sacer e descritti nel documento Specifiche tecniche dei servizi di annullamento; 

* la cancellazione degli AIP sottoposti a scarto o a seguito di restituzione dei documenti conservati ai Produttore alla cessazione del servizio. 


Le **funzionalità di Amministrazione di SacER** consentono di gestire configurazioni e parametrazioni in grado di determinare il funzionamento del Sistema in funzione degli specifici accordi intercorsi con i Produttori. Inoltre, consentono di monitorare tutta l’attività svolta da SacER. In particolare, in SacER è possibile configurare tutte le entità significative: Enti, Strutture, operatori e relativi profili, tipologie documentarie, formati accettati, logiche di controllo dei versamenti, logiche di creazione delle Serie, regole di accesso e di esibizione, politiche di monitoraggio del sistema. Anche l’interfaccia web di SacER è configurata automaticamente in ragione del profilo dei singoli operatori che vi accedono.  

Le funzionalità di Amministrazione sono costituite da transazioni eseguibili tramite l’interfaccia web del sistema e riservate agli operatori di ParER, ma visibili negli esiti anche agli operatori dei Produttori.

Le **funzionalità di Accesso di SacER** consentono di restituire in forma di DIP gli oggetti conservati. A tal fine SacER mette a disposizione un’interfaccia web per le ricerche e per l’estrazione manuale dei documenti e dei Servizi di recupero per l’estrazione automatica.  

Gli AIP forniti sono trasformati in DIP sulla base delle caratteristiche dell’oggetto e degli utilizzi cui è destinato. In molti casi la trasformazione dell’AIP in DIP può richiedere specifici passi elaborativi e trasformazioni complesse, che, necessitando di elaborazioni onerose, vengono normalmente eseguite da opportuni job batch e mantenute in modo permanente sul Data Base. Secondo la natura dei DIP, l’esibizione può avvenire on-line con un download, oppure tramite il trasferimento in un’area di transito, da cui il successivo recupero viene effettuato dal sistema richiedente con chiamata FTP. In molti casi, per comodità di trasferimento e recupero, i vari elementi che costituiscono il DIP vengono compressi in un archivio di tipo ZIP.   

Il **modulo di Accesso**, oltre a verificare tramite i servizi di Autenticazione l’abilitazione dell’Utente al recupero del documento, traccia in apposite tabelle di log tutte le richieste prevenute, qualunque ne sia stato l’esito.     

### Ricerche nell'archivio

Il sistema consente di effettuare ricerche su Unità documentarie, fascicoli e serie. 

<img src="src/docs/img/amm_archivio.png"> 


È possibile effettuare ricerche usando come filtri i metadati dell'oggetto versato, secondo i parametri di versamento (ad esempio secondo la versione del servizio di versamento utilizzato) o secondo i controlli eseguiti al momento del versamento. 

<img src="src/docs/img/ricerca_ud.png"> 

### Elenchi di versamento

<img src="src/docs/img/amm_elenchi_vers.png"> 

Questa sezione consente di gestire il processo di conservazione fino alla produzione dell'indice del pacchetto di archiviazione AIP. 

<img src="src/docs/img/ricerca_elenchi_vers.png"> 

### Monitoraggio

<img src="src/docs/img/monitoraggio.png"> 

La sezione di monitoraggio consente la verifica delle funzioni vitali del sistema  e del contenuto dell'archivio, consentendo ad esempio di monitorare i flussi di versamento e lo stato del processo di conservazione. 

<img src="src/docs/img/riepilogo_monitoraggio.png"> 


### Strutture versanti

<img src="src/docs/img/amm_strutture.png"> 

Questa sezione consente la creazione di nuove strutture di archivio offrendo funzioni di replica, importazione. 

<img src="src/docs/img/gestione_strutture.png"> 

### Registro formati

Anagrafica del sistema nel quale i formati, accettati a seguito di una procedura di valutazione, vengono censiti. Ogni formato viene descritto e accompagnati da attributi di qualità. 

Sulle strutture versanti sono poi configurati in modo specifico i formati che il produttore vuole ammette sulla struttura. 

<img src="src/docs/img/registro_formati.png"> 


### Annullamento versamenti

<img src="src/docs/img/amm_annull_vers.png"> 

Sezione che consente la gestione dell'annullamento di unità documentarie e fascicoli. 


<img src="src/docs/img/ricerca_annull_vers.png"> 

### Restituzione archivio

<img src="src/docs/img/amm_rest_arch.png"> 

Sezione che consente la gestione del processo di restituzione archvio di una struttura cessata, dalla richiesta all'estrazione del contenuto.  

<img src="src/docs/img/gestione_rich_rest_arch.png"> 


# Librerie utilizzate

|  GroupId | ArtifactId  | Version  | Type   |  Licenses |
|---|---|---|---|---|
|antlr|antlr|2.7.7.redhat-7|jar|BSD License|
|avalon-framework|avalon-framework-api|4.2.0|jar|-|
|avalon-framework|avalon-framework-impl|4.2.0|jar|-|
|ch.qos.logback|logback-classic|1.2.1|jar|Eclipse Public License - v 1.0, GNU Lesser General Public License|
|com.codeborne|phantomjsdriver|1.4.4|jar|The BSD 2-Clause License|
|com.fasterxml|classmate|1.5.1.redhat-00001|jar|Apache License, Version 2.0|
|com.fasterxml.jackson.core|jackson-annotations|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.jackson.core|jackson-core|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.jackson.core|jackson-databind|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.jackson.datatype|jackson-datatype-jdk8|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.jackson.datatype|jackson-datatype-jsr310|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.jackson.jaxrs|jackson-jaxrs-base|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.jackson.jaxrs|jackson-jaxrs-json-provider|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.jackson.module|jackson-module-jaxb-annotations|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.woodstox|woodstox-core|6.4.0.redhat-00001|jar|The Apache License, Version 2.0|
|com.github.ben-manes.caffeine|caffeine|2.8.8.redhat-00001|jar|Apache License, Version 2.0|
|com.github.fge|json-patch|1.9.0.redhat-00002|jar|The Apache License, Version 2.0, Lesser General Public License, version 3 or greater|
|com.google.code.gson|gson|2.8.9.redhat-00001|jar|Apache-2.0|
|com.google.guava|failureaccess|1.0.1.redhat-00002|jar|Apache License, Version 2.0|
|com.google.guava|guava|30.1.0.redhat-00001|jar|Apache License, Version 2.0|
|com.google.inject|guice|4.2.1|no_aop|jar|The Apache Software License, Version 2.0|
|com.h2database|h2|1.4.197.redhat-00004|jar|MPL 2.0, EPL 1.0|
|com.oracle|ojdbc7|12.1.0.1|jar|-|
|com.sun.activation|jakarta.activation|1.2.2.redhat-00001|jar|EDL 1.0|
|com.sun.istack|istack-commons-runtime|3.0.10.redhat-00001|jar|Eclipse Distribution License - v 1.0|
|com.sun.mail|jakarta.mail|1.6.7.redhat-00001|jar|EPL 2.0, GPL2 w/ CPE, EDL 1.0|
|commons-beanutils|commons-beanutils|1.9.4|jar|Apache License, Version 2.0|
|commons-cli|commons-cli|1.4|jar|Apache License, Version 2.0|
|commons-codec|commons-codec|1.15|jar|Apache License, Version 2.0|
|commons-collections|commons-collections|3.2.2|jar|Apache License, Version 2.0|
|commons-fileupload|commons-fileupload|1.5|jar|Apache-2.0|
|commons-io|commons-io|2.12.0|jar|Apache-2.0|
|commons-jxpath|commons-jxpath|1.3|jar|The Apache Software License, Version 2.0|
|commons-logging|commons-logging|1.2|jar|The Apache Software License, Version 2.0|
|commons-net|commons-net|3.9.0|jar|Apache License, Version 2.0|
|directory-naming|naming-java|0.8|jar|-|
|io.netty|netty-handler|4.1.86.Final-redhat-00001|jar|Apache License, Version 2.0|
|io.netty|netty-transport-native-epoll|4.1.86.Final-redhat-00001|linux-x86_64|jar|Apache License, Version 2.0|
|io.reactivex.rxjava3|rxjava|3.0.9.redhat-00001|jar|-|
|io.undertow|undertow-core|2.2.24.SP1-redhat-00001|jar|Apache License Version 2.0|
|it.eng.parer|blob-info|1.0.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3||
|it.eng.parer|client-hsm|1.0.2|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|idp-jaas-rdbms|0.0.9|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|parer-retry|2.1.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|sacer-jboss-ear|9.2.0|ear|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|sacer-jboss-ejb|9.2.0|ejb|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|sacer-jboss-jpa|9.2.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|sacer-jboss-slg|9.2.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|sacer-jboss-web|9.2.0|war|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|sacer-xml|2.8.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-core|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-middle|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-paginator-ejb|5.12.0|ejb|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-paginator-gf|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-si-client|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-si-server|5.12.0|classes|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-si-util|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-sl-ejb|5.12.0|ejb|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-sl-jpa|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-sl-slg|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-sl-web|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-sl-web|5.12.0|war|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-timer-wrapper-common|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-timer-wrapper-ejb|5.12.0|ejb|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|verificafirma-crypto-beans|1.3.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|verificafirma-eidas-beans|1.9.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|jakarta.enterprise|jakarta.enterprise.cdi-api|2.0.2.redhat-00002|jar|Apache License 2.0|
|jakarta.inject|jakarta.inject-api|1.0.3.redhat-00001|jar|The Apache Software License, Version 2.0|
|jakarta.json|jakarta.json-api|1.1.6.redhat-00001|jar|Eclipse Public License 2.0, GNU General Public License, version 2 with the GNU Classpath Exception|
|jakarta.json.bind|jakarta.json.bind-api|1.0.2.redhat-00001|jar|Eclipse Public License 2.0, GNU General Public License, version 2 with the GNU Classpath Exception|
|jakarta.persistence|jakarta.persistence-api|2.2.3.redhat-00001|jar|Eclipse Public License v. 2.0, Eclipse Distribution License v. 1.0|
|jakarta.security.enterprise|jakarta.security.enterprise-api|1.0.2.redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|jakarta.validation|jakarta.validation-api|2.0.2.redhat-00001|jar|Apache License 2.0|
|javax.annotation|jsr250-api|1.0|jar|COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0|
|javax.inject|javax.inject|1|jar|The Apache Software License, Version 2.0|
|javax.jws|jsr181-api|1.0.0.MR1-redhat-8|jar|CDDL+GPLv2|
|javax.servlet|javax.servlet-api|4.0.1|jar|CDDL + GPLv2 with classpath exception|
|joda-time|joda-time|2.12.5|jar|Apache License, Version 2.0|
|net.bytebuddy|byte-buddy|1.11.12.redhat-00002|jar|Apache License, Version 2.0|
|net.java.xadisk|xadisk|1.2.2.5|jar|-|
|net.sf.saxon|Saxon-HE|9.6.0-5|jar|Mozilla Public License Version 2.0|
|net.sourceforge.javacsv|javacsv|2.0|jar|GNU Library or Lesser General Public License|
|org.apache.commons|commons-collections4|4.4|jar|Apache License, Version 2.0|
|org.apache.commons|commons-compress|1.23.0|jar|Apache-2.0|
|org.apache.commons|commons-lang3|3.12.0|jar|Apache License, Version 2.0|
|org.apache.commons|commons-text|1.10.0|jar|Apache License, Version 2.0|
|org.apache.cxf|cxf-rt-frontend-jaxws|3.3.9.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.cxf|cxf-rt-transports-http|3.3.9.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.cxf|cxf-rt-ws-security|3.3.9.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.httpcomponents|httpclient|4.5.14|jar|Apache License, Version 2.0|
|org.apache.httpcomponents|httpcore|4.4.16|jar|Apache License, Version 2.0|
|org.apache.httpcomponents|httpmime|4.5.14|jar|Apache License, Version 2.0|
|org.apache.james|apache-mime4j-dom|0.8.9.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.james|apache-mime4j-storage|0.8.9.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.lucene|lucene-analyzers-common|5.5.5.redhat-2|jar|Apache 2|
|org.apache.lucene|lucene-core|5.5.5.redhat-2|jar|Apache 2|
|org.apache.lucene|lucene-facet|5.5.5.redhat-2|jar|Apache 2|
|org.apache.lucene|lucene-misc|5.5.5.redhat-2|jar|Apache 2|
|org.apache.lucene|lucene-queries|5.5.5.redhat-2|jar|Apache 2|
|org.apache.lucene|lucene-queryparser|5.5.5.redhat-2|jar|Apache 2|
|org.apache.maven|maven-artifact|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-builder-support|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-compat|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-core|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-embedder|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-model|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-model-builder|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-plugin-api|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-repository-metadata|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-resolver-provider|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-settings|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-settings-builder|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-slf4j-provider|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven.resolver|maven-resolver-api|1.4.1|jar|Apache License, Version 2.0|
|org.apache.maven.resolver|maven-resolver-connector-basic|1.4.1|jar|Apache License, Version 2.0|
|org.apache.maven.resolver|maven-resolver-impl|1.4.1|jar|Apache License, Version 2.0|
|org.apache.maven.resolver|maven-resolver-spi|1.4.1|jar|Apache License, Version 2.0|
|org.apache.maven.resolver|maven-resolver-transport-wagon|1.4.1|jar|Apache License, Version 2.0|
|org.apache.maven.resolver|maven-resolver-util|1.4.1|jar|Apache License, Version 2.0|
|org.apache.maven.shared|maven-shared-utils|3.2.1|jar|Apache License, Version 2.0|
|org.apache.maven.wagon|wagon-file|3.3.4|jar|Apache License, Version 2.0|
|org.apache.maven.wagon|wagon-http|3.3.4|shaded|jar|Apache License, Version 2.0|
|org.apache.maven.wagon|wagon-provider-api|3.3.4|jar|Apache License, Version 2.0|
|org.apache.poi|ooxml-schemas|1.4|jar|The Apache Software License, Version 2.0|
|org.apache.poi|poi|4.1.2|jar|Apache License, Version 2.0|
|org.apache.poi|poi-ooxml|4.1.2|jar|Apache License, Version 2.0|
|org.apache.santuario|xmlsec|2.2.3.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.taglibs|taglibs-standard-impl|1.2.6.RC1-redhat-1|jar|Apache License, Version 2.0|
|org.apache.taglibs|taglibs-standard-spec|1.2.6.RC1-redhat-1|jar|Apache License, Version 2.0|
|org.apache.tika|tika-core|2.8.0|jar|Apache-2.0|
|org.apache.tika|tika-parsers-standard-package|2.8.0|jar|Apache-2.0|
|org.apache.wss4j|wss4j-ws-security-common|2.2.4|jar|Apache License, Version 2.0|
|org.apache.xmlbeans|xmlbeans|3.1.0|jar|The Apache Software License, Version 2.0|
|org.apache.xmlgraphics|fop|1.1|jar|The Apache Software License, Version 2.0|
|org.codehaus.jackson|jackson-core-asl|1.9.13.redhat-00007|jar|The Apache Software License, Version 2.0|
|org.codehaus.jackson|jackson-jaxrs|1.9.13.redhat-00007|jar|The Apache Software License, Version 2.0, GNU Lesser General Public License (LGPL), Version 2.1|
|org.codehaus.jackson|jackson-mapper-asl|1.9.13.redhat-00007|jar|The Apache Software License, Version 2.0|
|org.codehaus.jackson|jackson-xc|1.9.13.redhat-00007|jar|The Apache Software License, Version 2.0, GNU Lesser General Public License (LGPL), Version 2.1|
|org.codehaus.jettison|jettison|1.5.4|jar|Apache License, Version 2.0|
|org.codehaus.plexus|plexus-classworlds|2.6.0|jar|Apache License, Version 2.0|
|org.codehaus.plexus|plexus-component-annotations|2.1.0|jar|Apache License, Version 2.0|
|org.codehaus.plexus|plexus-interpolation|1.25|jar|Apache License, Version 2.0|
|org.codehaus.plexus|plexus-utils|3.2.1|jar|Apache License, Version 2.0|
|org.codehaus.woodstox|stax2-api|4.2.1.redhat-00001|jar|The BSD License|
|org.eclipse.sisu|org.eclipse.sisu.inject|0.3.4|jar|Eclipse Public License, Version 1.0|
|org.eclipse.sisu|org.eclipse.sisu.plexus|0.3.4|jar|Eclipse Public License, Version 1.0|
|org.fusesource.jansi|jansi|1.17.1|jar|The Apache Software License, Version 2.0|
|org.glassfish.jaxb|txw2|2.3.3.b02-redhat-00002|jar|Eclipse Distribution License - v 1.0|
|org.hibernate|hibernate-envers|5.3.29.Final-redhat-00001|jar|GNU Library General Public License v2.1 or later|
|org.hibernate|hibernate-search-engine|5.10.13.Final-redhat-00001|jar|GNU Lesser General Public License v2.1 or later|
|org.hibernate|hibernate-search-orm|5.10.13.Final-redhat-00001|jar|GNU Lesser General Public License v2.1 or later|
|org.hibernate.common|hibernate-commons-annotations|5.0.5.Final-redhat-00002|jar|GNU Lesser General Public License v2.1 or later|
|org.hibernate.validator|hibernate-validator-annotation-processor|6.0.23.Final-redhat-00001|jar|Apache License 2.0|
|org.infinispan|infinispan-client-hotrod|11.0.17.Final-redhat-00001|jar|Apache License 2.0|
|org.infinispan|infinispan-commons|11.0.17.Final-redhat-00001|jar|Apache License 2.0|
|org.infinispan|infinispan-core|11.0.17.Final-redhat-00001|jar|Apache License 2.0|
|org.infinispan|infinispan-directory-provider|10.1.8.Final-redhat-00001|jar|Apache License 2.0|
|org.infinispan.protostream|protostream|4.3.5.Final-redhat-00001|jar|Apache License 2.0|
|org.javassist|javassist|3.27.0.GA-redhat-00001|jar|MPL 1.1, LGPL 2.1, Apache License 2.0|
|org.jboss|jandex|2.4.2.Final-redhat-00001|jar|Apache License, Version 2.0|
|org.jboss.arquillian.config|arquillian-config-api|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.config|arquillian-config-impl-base|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.config|arquillian-config-spi|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.container|arquillian-container-impl-base|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.container|arquillian-container-spi|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.container|arquillian-container-test-api|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.container|arquillian-container-test-impl-base|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.container|arquillian-container-test-spi|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.core|arquillian-core-api|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.core|arquillian-core-impl-base|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.core|arquillian-core-spi|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.extension|arquillian-drone-api|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-appium-extension|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-browserstack-extension|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-configuration|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-impl|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-saucelabs-extension|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-spi|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-webdriver|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-webdriver-depchain|2.5.2|pom|Public Domain|
|org.jboss.arquillian.graphene|graphene-webdriver|2.3.2|pom|GNU Lesser General Public License, Version 2.1|
|org.jboss.arquillian.junit|arquillian-junit-container|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.junit|arquillian-junit-core|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.junit|arquillian-junit-standalone|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.protocol|arquillian-protocol-jmx|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.protocol|arquillian-protocol-servlet|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.test|arquillian-test-api|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.test|arquillian-test-impl-base|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.test|arquillian-test-spi|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.testenricher|arquillian-testenricher-cdi|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.testenricher|arquillian-testenricher-ejb|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.testenricher|arquillian-testenricher-initialcontext|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.testenricher|arquillian-testenricher-resource|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.testng|arquillian-testng-container|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.testng|arquillian-testng-core|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.testng|arquillian-testng-standalone|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.eap|wildfly-clustering-api|7.4.11.GA-redhat-00002|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.eap|wildfly-clustering-service|7.4.11.GA-redhat-00002|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.eap|wildfly-clustering-singleton-api|7.4.11.GA-redhat-00002|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.eap|wildfly-ejb-client-bom|7.4.11.GA|pom|Apache License 2.0|
|org.jboss.eap|wildfly-jaxws-client-bom|7.4.11.GA|pom|Apache License 2.0|
|org.jboss.eap|wildfly-jms-client-bom|7.4.11.GA|pom|Apache License 2.0|
|org.jboss.eap|wildfly-security-api|7.4.11.GA-redhat-00002|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.ejb3|jboss-ejb3-ext-api|2.3.0.Final-redhat-00001|jar|Public Domain|
|org.jboss.logging|commons-logging-jboss-logging|1.0.0.Final-redhat-1|jar|Apache License 2.0|
|org.jboss.logging|jboss-logging|3.4.1.Final-redhat-00001|jar|Apache License, version 2.0|
|org.jboss.msc|jboss-msc|1.4.12.Final-redhat-00001|jar|GNU Lesser General Public License v2.1 only|
|org.jboss.narayana.xts|jbossxts|5.11.4.Final-redhat-00001|api|jar|LGPL 2.1|
|org.jboss.resteasy|resteasy-atom-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-client|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-jackson-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-jackson2-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-jaxb-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-jaxrs|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-jettison-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-jsapi|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-json-p-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-multipart-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-spring|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-validator-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.security|jboss-negotiation-common|3.0.6.Final-redhat-00001|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.security|jboss-negotiation-extras|3.0.6.Final-redhat-00001|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.security|jboss-negotiation-ntlm|3.0.6.Final-redhat-00001|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.security|jboss-negotiation-spnego|3.0.6.Final-redhat-00001|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.security|jbossxacml|2.0.8.Final-redhat-8|jar|lgpl|
|org.jboss.shrinkwrap|shrinkwrap-api|1.2.6|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap|shrinkwrap-api-nio2|1.2.6|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap|shrinkwrap-depchain|1.2.6|pom|Apache License, Version 2.0|
|org.jboss.shrinkwrap|shrinkwrap-depchain-java7|1.2.6|pom|Apache License, Version 2.0|
|org.jboss.shrinkwrap|shrinkwrap-impl-base|1.2.6|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap|shrinkwrap-impl-nio2|1.2.6|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap|shrinkwrap-spi|1.2.6|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-api-base|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-api-javaee|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-api-jboss|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-depchain|2.0.0|pom|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-gen|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-impl-base|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-impl-javaee|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-impl-jboss|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-spi|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-api|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-api-gradle-embedded-archive|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-api-maven|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-api-maven-archive|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-api-maven-embedded|3.1.4|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-depchain|2.2.7|pom|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-gradle-depchain|2.2.7|pom|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-impl-gradle-embedded-archive|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-impl-maven|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-impl-maven-archive|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-impl-maven-embedded|3.1.4|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-spi|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-spi-maven|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.spec|jboss-jakartaee-8.0|1.0.1.Final-redhat-00007|pom|GNU Lesser General Public License, Version 2.1|
|org.jboss.spec.javax.annotation|jboss-annotations-api_1.3_spec|2.0.1.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.batch|jboss-batch-api_1.0_spec|2.0.0.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.spec.javax.ejb|jboss-ejb-api_3.2_spec|2.0.0.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.el|jboss-el-api_3.0_spec|2.0.1.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE, Apache 2.0, LGPL 2.1 or later|
|org.jboss.spec.javax.enterprise.concurrent|jboss-concurrency-api_1.0_spec|2.0.0.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.faces|jboss-jsf-api_2.3_spec|3.0.0.SP07-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.interceptor|jboss-interceptors-api_1.2_spec|2.0.0.Final-redhat-00002|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.jms|jboss-jms-api_2.0_spec|2.0.0.Final-redhat-00001|jar|Eclipse Public License 2.0, GNU General Public License, version 2 with the GNU Classpath Exception|
|org.jboss.spec.javax.management.j2ee|jboss-j2eemgmt-api_1.1_spec|2.0.0.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.resource|jboss-connector-api_1.7_spec|2.0.0.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.security.auth.message|jboss-jaspi-api_1.1_spec|2.0.1.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.security.jacc|jboss-jacc-api_1.5_spec|2.0.0.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.servlet|jboss-servlet-api_4.0_spec|2.0.0.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.servlet.jsp|jboss-jsp-api_2.3_spec|2.0.0.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.transaction|jboss-transaction-api_1.3_spec|2.0.0.Final-redhat-00005|jar|Eclipse Public License 2.0, GNU General Public License, Version 2 with the Classpath Exception|
|org.jboss.spec.javax.websocket|jboss-websocket-api_1.1_spec|2.0.0.Final-redhat-00001|jar|Eclipse Public License 2.0, GNU General Public License, version 2 with the GNU Classpath Exception|
|org.jboss.spec.javax.ws.rs|jboss-jaxrs-api_2.1_spec|2.0.1.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.xml.bind|jboss-jaxb-api_2.3_spec|2.0.1.Final-redhat-00001|jar|Eclipse Distribution License - v 1.0|
|org.jboss.spec.javax.xml.soap|jboss-saaj-api_1.4_spec|1.0.2.Final-redhat-00002|jar|Eclipse Distribution License, Version 1.0|
|org.jboss.spec.javax.xml.ws|jboss-jaxws-api_2.3_spec|2.0.0.Final-redhat-00001|jar|Eclipse Distribution License - v 1.0|
|org.jboss.threads|jboss-threads|2.4.0.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.xnio|xnio-api|3.8.9.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.xnio|xnio-nio|3.8.9.Final-redhat-00001|jar|Apache License 2.0|
|org.jgroups|jgroups|4.2.15.Final-redhat-00001|jar|Apache License 2.0|
|org.jsoup|jsoup|1.12.1|jar|The MIT License|
|org.keycloak|keycloak-servlet-filter-adapter|21.1.1|jar|Apache License, Version 2.0|
|org.ow2.asm|asm-debug-all|4.1|jar|BSD|
|org.owasp.esapi|esapi|2.2.0.0|jar|BSD, Creative Commons 3.0 BY-SA|
|org.picketbox|picketbox|5.0.3.Final-redhat-00009|jar|GNU Lesser General Public License v2.1 only|
|org.picketbox|picketbox-commons|1.0.0.final-redhat-5|jar|lgpl|
|org.picketlink|picketlink-api|2.5.5.SP12-redhat-00012|jar|Apache License 2.0|
|org.picketlink|picketlink-common|2.5.5.SP12-redhat-00012|jar|Apache License 2.0|
|org.picketlink|picketlink-config|2.5.5.SP12-redhat-00012|jar|Apache License 2.0|
|org.picketlink|picketlink-federation|2.5.5.SP12-redhat-00012|jar|Apache License, Version 2.0|
|org.picketlink|picketlink-idm-api|2.5.5.SP12-redhat-00012|jar|Apache License 2.0|
|org.picketlink|picketlink-idm-impl|2.5.5.SP12-redhat-00012|jar|Apache License 2.0|
|org.picketlink|picketlink-impl|2.5.5.SP12-redhat-00012|jar|Apache License 2.0|
|org.powermock|powermock-reflect|1.7.4|jar|The Apache Software License, Version 2.0|
|org.reactivestreams|reactive-streams|1.0.3.redhat-00003|jar|-|
|org.seleniumhq.selenium|htmlunit-driver|2.28|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|lift|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-api|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-chrome-driver|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-edge-driver|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-firefox-driver|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-ie-driver|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-java|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-leg-rc|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-opera-driver|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-remote-driver|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-safari-driver|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-server|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-support|3.11.0|jar|The Apache Software License, Version 2.0|
|org.slf4j|jcl-over-slf4j|2.0.7|jar|Apache License, Version 2.0|
|org.slf4j|log4j-over-slf4j|2.0.7|jar|Apache Software Licenses|
|org.slf4j|slf4j-api|2.0.7|jar|MIT License|
|org.slf4j|slf4j-log4j12|2.0.7|jar|MIT License|
|org.slf4j|slf4j-simple|2.0.7|jar|MIT License|
|org.sonatype.plexus|plexus-cipher|1.7|jar|Apache Public License 2.0|
|org.sonatype.plexus|plexus-sec-dispatcher|1.4|jar|Apache Public License 2.0|
|org.springframework|spring-aop|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-context|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-context-support|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-core|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-jdbc|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-orm|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-test|5.0.15.RELEASE|jar|Apache License, Version 2.0|
|org.springframework|spring-tx|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-web|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-webmvc|5.3.30|jar|Apache License, Version 2.0|
|org.springframework.security|spring-security-config|5.8.8|jar|Apache License, Version 2.0|
|org.springframework.security|spring-security-core|5.8.8|jar|Apache License, Version 2.0|
|org.springframework.security|spring-security-web|5.8.8|jar|Apache License, Version 2.0|
|org.springframework.security.extensions|spring-security-saml2-core|1.0.10.RELEASE|jar|The Apache Software License, Version 2.0|
|org.testng|testng|6.11|jar|Apache 2.0|
|org.webjars|font-awesome|6.4.0|jar|CC BY 3.0|
|org.webjars|highlightjs|11.5.0|jar|BSD|
|org.webjars|jquery|3.6.4|jar|MIT License|
|org.webjars|jquery-ui|1.13.2|jar|MIT License|
|org.webjars|jstree|3.3.8|jar|MIT License, GPL|
|org.webjars|select2|4.0.13|jar|MIT|
|org.webjars.bower|chosen|1.8.7|jar|MIT|
|org.webjars.bowergithub.wcoder|highlightjs-line-numbers.js|2.7.0|jar|MIT|
|org.webjars.npm|highlightjs-badgejs|0.0.5|jar|MIT|
|org.wildfly.arquillian|wildfly-arquillian-common|3.0.1.Final|jar|Apache License Version 2.0|
|org.wildfly.arquillian|wildfly-arquillian-container-managed|3.0.1.Final|jar|Apache License Version 2.0|
|org.wildfly.arquillian|wildfly-arquillian-container-remote|3.0.1.Final|jar|Apache License Version 2.0|
|org.wildfly.client|wildfly-client-config|1.0.1.Final-redhat-00001|jar|Apache License 2.0|
|org.wildfly.common|wildfly-common|1.5.4.Final-redhat-00001|jar|Apache License 2.0|
|org.wildfly.discovery|wildfly-discovery-client|1.2.1.Final-redhat-00001|jar|Apache License 2.0|
|org.wildfly.security|wildfly-elytron|1.15.16.Final-redhat-00001|jar|Apache License 2.0|
|software.amazon.awssdk|accessanalyzer|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|account|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|acm|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|acmpca|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|alexaforbusiness|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|amp|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|amplify|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|amplifybackend|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|amplifyuibuilder|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|annotations|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|apache-client|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|apigateway|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|apigatewaymanagementapi|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|apigatewayv2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|appconfig|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|appconfigdata|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|appflow|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|appintegrations|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|applicationautoscaling|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|applicationcostprofiler|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|applicationdiscovery|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|applicationinsights|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|appmesh|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|apprunner|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|appstream|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|appsync|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|arczonalshift|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|arns|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|athena|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|auditmanager|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|auth|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|autoscaling|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|autoscalingplans|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|aws-cbor-protocol|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|aws-core|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|aws-crt-client|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|aws-json-protocol|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|aws-query-protocol|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|aws-xml-protocol|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|backup|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|backupgateway|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|backupstorage|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|batch|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|billingconductor|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|braket|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|budgets|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|chime|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|chimesdkidentity|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|chimesdkmediapipelines|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|chimesdkmeetings|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|chimesdkmessaging|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|chimesdkvoice|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cleanrooms|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloud9|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudcontrol|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|clouddirectory|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudformation|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudfront|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudhsm|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudhsmv2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudsearch|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudsearchdomain|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudtrail|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudtraildata|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudwatch|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudwatch-metric-publisher|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudwatchevents|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudwatchlogs|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codeartifact|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codebuild|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codecatalyst|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codecommit|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codedeploy|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codegen|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codegen-lite|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codegen-lite-maven-plugin|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codegen-maven-plugin|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codeguruprofiler|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codegurureviewer|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codegurusecurity|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codepipeline|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codestar|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codestarconnections|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codestarnotifications|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cognitoidentity|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cognitoidentityprovider|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cognitosync|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|comprehend|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|comprehendmedical|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|computeoptimizer|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|config|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|connect|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|connectcampaigns|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|connectcases|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|connectcontactlens|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|connectparticipant|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|controltower|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|costandusagereport|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|costexplorer|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|customerprofiles|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|databasemigration|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|databrew|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|dataexchange|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|datapipeline|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|datasync|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|dax|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|detective|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|devicefarm|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|devopsguru|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|directconnect|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|directory|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|dlm|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|docdb|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|docdbelastic|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|drs|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|dynamodb|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|dynamodb-enhanced|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ebs|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ec2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ec2instanceconnect|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ecr|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ecrpublic|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ecs|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|efs|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|eks|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|elasticache|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|elasticbeanstalk|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|elasticinference|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|elasticloadbalancing|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|elasticloadbalancingv2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|elasticsearch|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|elastictranscoder|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|emr|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|emrcontainers|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|emrserverless|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|eventbridge|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|evidently|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|finspace|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|finspacedata|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|firehose|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|fis|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|fms|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|forecast|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|forecastquery|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|frauddetector|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|fsx|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|gamelift|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|gamesparks|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|glacier|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|globalaccelerator|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|glue|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|grafana|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|greengrass|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|greengrassv2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|groundstation|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|guardduty|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|health|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|healthlake|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|honeycode|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|http-client-spi|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iam|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|identitystore|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|imagebuilder|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|imds|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|inspector|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|inspector2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|internetmonitor|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iot|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iot1clickdevices|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iot1clickprojects|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotanalytics|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotdataplane|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotdeviceadvisor|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotevents|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ioteventsdata|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotfleethub|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotfleetwise|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotjobsdataplane|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotroborunner|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotsecuretunneling|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotsitewise|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotthingsgraph|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iottwinmaker|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotwireless|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ivs|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ivschat|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ivsrealtime|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|json-utils|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kafka|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kafkaconnect|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kendra|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kendraranking|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|keyspaces|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesis|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesisanalytics|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesisanalyticsv2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesisvideo|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesisvideoarchivedmedia|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesisvideomedia|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesisvideosignaling|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesisvideowebrtcstorage|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kms|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lakeformation|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lambda|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lexmodelbuilding|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lexmodelsv2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lexruntime|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lexruntimev2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|licensemanager|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|licensemanagerlinuxsubscriptions|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|licensemanagerusersubscriptions|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lightsail|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|location|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lookoutequipment|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lookoutmetrics|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lookoutvision|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|m2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|machinelearning|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|macie|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|macie2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|managedblockchain|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|marketplacecatalog|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|marketplacecommerceanalytics|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|marketplaceentitlement|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|marketplacemetering|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediaconnect|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediaconvert|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|medialive|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediapackage|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediapackagev2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediapackagevod|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediastore|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediastoredata|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediatailor|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|memorydb|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|metrics-spi|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mgn|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|migrationhub|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|migrationhubconfig|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|migrationhuborchestrator|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|migrationhubrefactorspaces|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|migrationhubstrategy|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mobile|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mq|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mturk|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mwaa|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|neptune|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|netty-nio-client|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|networkfirewall|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|networkmanager|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|nimble|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|oam|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|omics|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|opensearch|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|opensearchserverless|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|opsworks|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|opsworkscm|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|organizations|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|osis|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|outposts|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|panorama|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|paymentcryptography|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|paymentcryptographydata|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|personalize|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|personalizeevents|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|personalizeruntime|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|pi|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|pinpoint|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|pinpointemail|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|pinpointsmsvoice|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|pinpointsmsvoicev2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|pipes|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|polly|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|pricing|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|privatenetworks|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|profiles|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|protocol-core|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|proton|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|qldb|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|qldbsession|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|quicksight|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ram|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|rbin|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|rds|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|rdsdata|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|redshift|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|redshiftdata|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|redshiftserverless|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|regions|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|rekognition|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|resiliencehub|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|resourceexplorer2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|resourcegroups|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|resourcegroupstaggingapi|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|robomaker|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|rolesanywhere|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|route53|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|route53domains|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|route53recoverycluster|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|route53recoverycontrolconfig|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|route53recoveryreadiness|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|route53resolver|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|rum|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|s3|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|s3-transfer-manager|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|s3control|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|s3outposts|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sagemaker|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sagemakera2iruntime|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sagemakeredge|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sagemakerfeaturestoreruntime|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sagemakergeospatial|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sagemakermetrics|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sagemakerruntime|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|savingsplans|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|scheduler|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|schemas|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sdk-core|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|secretsmanager|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|securityhub|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|securitylake|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|serverlessapplicationrepository|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|servicecatalog|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|servicecatalogappregistry|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|servicediscovery|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|servicequotas|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ses|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sesv2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sfn|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|shield|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|signer|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|simspaceweaver|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sms|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|snowball|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|snowdevicemanagement|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sns|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sqs|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ssm|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ssmcontacts|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ssmincidents|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ssmsap|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sso|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ssoadmin|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ssooidc|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|storagegateway|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sts|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|support|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|supportapp|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|swf|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|synthetics|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|textract|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|third-party-jackson-core|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|third-party-jackson-dataformat-cbor|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|timestreamquery|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|timestreamwrite|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|tnb|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|transcribe|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|transcribestreaming|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|transfer|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|translate|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|url-connection-client|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|utils|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|verifiedpermissions|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|voiceid|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|vpclattice|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|waf|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|wafv2|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|wellarchitected|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|wisdom|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|workdocs|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|worklink|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|workmail|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|workmailmessageflow|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|workspaces|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|workspacesweb|2.20.87|jar|Apache License, Version 2.0|
|software.amazon.awssdk|xray|2.20.87|jar|Apache License, Version 2.0|
|xalan|xalan|2.7.2|jar|The Apache Software License, Version 2.0|

# Supporto

Mantainer del progetto è [Engineering Ingegneria Informatica S.p.A.](https://www.eng.it/).

# Contributi

Se interessati a crontribuire alla crescita del progetto potete scrivere all'indirizzo email <a href="mailto:areasviluppoparer@regione.emilia-romagna.it">areasviluppoparer@regione.emilia-romagna.it</a>.

# Credits

Progetto di proprietà di [Regione Emilia-Romagna](https://www.regione.emilia-romagna.it/) sviluppato a cura di [Engineering Ingegneria Informatica S.p.A.](https://www.eng.it/).

# Licenza

Questo progetto è rilasciato sotto licenza GNU Affero General Public License v3.0 or later ([LICENSE.txt](LICENSE.txt)).

# Appendice

## Documentazione aggiuntiva

Alcuni riferimenti:

* Sito ParER: https://poloarchivistico.regione.emilia-romagna.it/
* Manuale di conservazione: https://poloarchivistico.regione.emilia-romagna.it/documentazione/documenti_open/manualeconservazione_v5-0.pdf/@@download/file/ManualeConservazione_v2.0.pdf 