---
title: "Configurazione Sacer"
---

# Configurazione Jboss EAP 7.4

## Versioni 

| Vers. doc | Vers. Sacer  | Modifiche  |
| -------- | ---------- | ---------- |
| 2.0.0 | 8.3.1.3 | Migrazione a JBoss EAP 7.4  |
| 2.0.1 | 8.4.0 | Aggiunta destinazioni JMS remote per la migrazione blob ordinaria |
| 3.0.0 | 8.5.0 | Modificate le configurazioni sulle code della elaborazione elenchi e thread pool |

## Datasource XA

### SacerJobDs

#### Console web

`Configuration > Connector > datasources`

#### JBoss CLI

```bash
xa-data-source add --name=SacerJobPool --jndi-name=java:jboss/datasources/SacerJobDs --xa-datasource-properties={"URL"=>"jdbc:oracle:thin:@parer-vora-b03.ente.regione.emr.it:1521/PARER19S.ente.regione.emr.it"} --user-name=SACER --password=<password> --driver-name=ojdbc11 --spy=true --xa-datasource-class=oracle.jdbc.xa.client.OracleXADataSource --validate-on-match=false --background-validation=false --same-rm-override=false --interleaving=false --no-tx-separate-pool=true --pad-xid=false --wrap-xa-resource=false --set-tx-query-timeout=false --blocking-timeout-wait-millis=0 --idle-timeout-minutes=0 --query-timeout=0 --use-try-lock=0 --allocation-retry=0 --allocation-retry-wait-millis=0 --xa-resource-timeout=0 --share-prepared-statements=false
```

### Transaction service 

Lo schema dell'applicazione ha bisogno delle seguenti grant su Oracle.

```sql
GRANT SELECT ON sys.dba_pending_transactions TO SACER;
GRANT SELECT ON sys.pending_trans$ TO SACER;
GRANT SELECT ON sys.dba_2pc_pending TO SACER;
GRANT EXECUTE ON sys.dbms_xa TO SACER;
```

La procedura è descritta nella documentazione standard di JBoss EAP 7.3

https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.3/html/configuration_guide/datasource_management#vendor_specific_xa_recovery

## Configurazione ActiveMQ 

```bash
/subsystem=messaging-activemq/server=default/pooled-connection-factory=sacer-activemq-ra:add(entries=["java:/SacerJmsXA"],connectors=["in-vm"],transaction="xa")
/subsystem=messaging-activemq/server=default/pooled-connection-factory=sacer-non-xa:add(entries=[java:/SacerJmsNonXA],connectors=[in-vm],block-on-acknowledge=false,block-on-non-durable-send=false,block-on-durable-send=false,pre-acknowledge=true,transaction=local,allow-local-transactions=true)
/subsystem=messaging-activemq/server=default/pooled-connection-factory=sacer-untransacted:add(entries=[java:/SacerJmsUntransacted],connectors=[in-vm],block-on-acknowledge=false,block-on-non-durable-send=false,block-on-durable-send=false,pre-acknowledge=true,transaction=none,allow-local-transactions=true)
```

## Configurazione Servizio JMS

### Configurazione Risorse JMS e Nomi JNDI

#### Configurazione tramite interfaccia web

`Configuration > Messaging > Destinations` 

Andare in `View` sul **default**  quindi 

`Queues/Topics > Queue`

Cliccare su 

`Add` 

e aggiungere le seguenti destinazioni 

Name | JNDI 
--- | --- 
VerificaFirmeDataVersQueue | java:/jms/queue/VerificaFirmeDataVersQueue |
IndiceAipUnitaDocQueue | java:/jms/queue/IndiceAipUnitaDocQueue |
OggettiDaMigrareQueue | java:/jms/queue/OggettiDaMigrareQueue java:/jboss/exported/jms/queue/OggettiDaMigrareQueue |
OggettiMigratiQueue | java:/jboss/exported/jms/queue/OggettiMigratiQueue
OggettiVerificatiQueue | java:/jms/queue/OggettiVerificatiQueue java:/jboss/exported/jms/queue/OggettiVerificatiQueue|
OggettiInErroreQueue | java:/jms/queue/OggettiInErroreQueue java:/jboss/exported/jms/queue/OggettiInErroreQueue |
IndiciAIPUDDaElabQueue | java:/jms/queue/IndiciAIPUDDaElabQueue |
ElenchiDaElabQueue | java:/jms/queue/ElenchiDaElabQueue |

#### Configurazione tramite CLI

```bash
jms-queue add --queue-address=VerificaFirmeDataVersQueue --entries=[java:/jms/queue/VerificaFirmeDataVersQueue]

jms-queue add --queue-address=IndiceAipUnitaDocQueue --entries=[java:/jms/queue/IndiceAipUnitaDocQueue]

jms-queue add --queue-address=OggettiDaMigrareQueue --entries=[java:/jms/queue/OggettiDaMigrareQueue /jboss/exported/jms/queue/OggettiDaMigrareQueue] 

jms-queue add --queue-address=OggettiMigratiQueue --entries=[/jboss/exported/jms/queue/OggettiMigratiQueue]

jms-queue add --queue-address=OggettiVerificatiQueue --entries=[java:/jms/queue/OggettiVerificatiQueue /jboss/exported/jms/queue/OggettiVerificatiQueue]

jms-queue add --queue-address=OggettiInErroreQueue --entries=[java:/jms/queue/OggettiInErroreQueue /jboss/exported/jms/queue/OggettiInErroreQueue]

jms-queue add --queue-address=IndiciAIPUDDaElabQueue --entries=[java:/jms/queue/IndiciAIPUDDaElabQueue]

jms-queue add --queue-address=ElenchiDaElabQueue --entries=[java:/jms/queue/ElenchiDaElabQueue]
```


### Bean pool per gli MDB

#### Configurazione tramite interfaccia web

`Configuration > Container > EJB 3 > BEAN POOLS`

Aggiungere i seguenti Bean Pools  

Name | Max Pool Size | Timeout | Timeout unit |
--- | --- | --- | --- |
coda-verifica-firme-pool | 5 | 5 | MINUTES |
coda-indice-aip-ud-pool | 2 | 5 | MINUTES |
coda-oggetti-verificati-pool | 5 | 5 | MINUTES |
coda-oggetti-errati-pool | 5 | 5 | MINUTES |
coda-indici-aip-da-elab-pool | 3 | 5 | MINUTES |
coda-elenchi-da-elab-pool | 5 | 5 | MINUTES |

#### Configurazione tramite CLI

```bash
/subsystem=ejb3/strict-max-bean-instance-pool=coda-verifica-firme-pool:add(max-pool-size=5)

/subsystem=ejb3/strict-max-bean-instance-pool=coda-indice-aip-ud-pool:add(max-pool-size=2)

/subsystem=ejb3/strict-max-bean-instance-pool=coda-oggetti-verificati-pool:add(max-pool-size=5)

/subsystem=ejb3/strict-max-bean-instance-pool=coda-oggetti-errati-pool:add(max-pool-size=5)

/subsystem=ejb3/strict-max-bean-instance-pool=coda-indici-aip-da-elab-pool:add(max-pool-size=3, timeout=5, timeout-unit="MINUTES")

/subsystem=ejb3/strict-max-bean-instance-pool=coda-elenchi-da-elab-pool:add(max-pool-size=5, timeout=5, timeout-unit="MINUTES")
``` 
### Thread pool

Aggiungere un thread pool async-default.

#### Configurazione tramite CLI

```bash
/subsystem=ejb3/thread-pool=async-default:add(max-threads=30,keepalive-time={time=100,unit=MILLISECONDS})
/subsystem=ejb3/service=async:write-attribute(name=thread-pool-name,value=async-default)
```

## Key Store 

È necessario mettere il keystore in formato JKS in una cartella accessibile all'IDP e poi configurare la system properties sacer-jks-path con il path al file.

## System properties

### Console web

`Configuration > System properties`

impostare le seguenti properties

Chiave | Valore di esempio | Descrizione
--- | --- | ---
sacer-key-manager-pass | <password_jks_sacer> | Chiave del Java Key Store utilizzato per ottenere la chiave privata del circolo di fiducia dell’IDP.
sacer-timeout-metadata | 10000 | Timeout in secondi per la ricezione dei metadati dall’IDP.
sacer-temp-file | /var/tmp/tmp-sacer-federation.xml | Percorso assoluto del file xml che rappresenta l’applicazione all’interno del circolo di fiducia.
sacer-sp-identity-id | https://parer-svil.ente.regione.emr.it/sacer | Identità dell’applicazione all’interno del circolo di fiducia.
sacer-refresh-check-interval | 600000 | Intervallo di tempo in secondi utilizzato per ricontattare l’IDP per eventuali variazioni sulla configurazione del circolo di fiducia.
sacer-jks-path | /opt/jboss-eap/certs/sacer.jks | Percorso assoluto del Java Key Store dell’applicazione.
sacer-store-key-name | sacer | Alias del certificato dell’applicazione all’interno del Java Key Store.
aws.accessKeyId | <accessKeyId_object_storage> | Access Key id delle credenziali S3 per l’accesso all’object storage per il servizio di migrazione.
aws.secretKey | <secretKey_object_storage> | Secret Key delle credenziali S3 per l’accesso all’object storage per il servizio di migrazione.

### JBoss CLI

```bash 
/system-property=sacer-key-manager-pass:add(value="<password_jks_sacer>")
/system-property=sacer-timeout-metadata:add(value="10000")
/system-property=sacer-temp-file:add(value="/var/tmp/tmp-sacer-federation.xml")
/system-property=sacer-sp-identity-id:add(value="https://parer-svil.ente.regione.emr.it/sacer")
/system-property=sacer-refresh-check-interval:add(value="600000")
/system-property=sacer-store-key-name:add(value="sacer")
/system-property=sacer-jks-path:add(value="/opt/jboss-eap/certs/sacer.jks")
/system-property=aws.accessKeyId:add(value="<accessKeyId_object_storage>")
/system-property=aws.secretKey:add(value="<secretKey_object_storage>")
```


## Logging profile


### Hibernate custom handler
Assicurarsi di aver installato il modulo ApplicationLogCustomHandler (Vedi documentazione di configurazione di Jboss EAP 7.3).

Configurare un custom handler nel subsystem **jboss:domain:logging:1.5**.

```xml
<subsystem xmlns="urn:jboss:domain:logging:1.5">
    <!-- ... --> 
    <custom-handler name="sacer_jdbc_handler" class="it.eng.tools.jboss.module.logger.ApplicationLogCustomHandler" module="it.eng.tools.jboss.module.logger">
        <level name="INFO"/>
        <formatter>
            <named-formatter name="PATTERN"/>
        </formatter>
        <properties>
            <property name="fileName" value="sacer_jdbc.log"/>
            <property name="deployment" value="sacer"/>
        </properties>
    </custom-handler>
    <!-- ... -->
</subsystem>
```
I comandi CLI 

```bash 
/subsystem=logging/custom-handler=sacer_jdbc_handler:add(class=it.eng.tools.jboss.module.logger.ApplicationLogCustomHandler,module=it.eng.tools.jboss.module.logger,level=INFO)

/subsystem=logging/custom-handler=sacer_jdbc_handler:write-attribute(name=named-formatter,value=PATTERN)

/subsystem=logging/custom-handler=sacer_jdbc_handler:write-attribute(name=properties,value={fileName=>"sacer_jdbc.log", deployment=>"sacer"})
```

Associare l'handler ai logger **jboss.jdbc.spy** e **org.hibernate**, sempre nel subsystem **jboss:domain:logging:1.5**. 


```xml
<subsystem xmlns="urn:jboss:domain:logging:1.5">
    <!-- ... -->
    <logger category="jboss.jdbc.spy" use-parent-handlers="false">
        <level name="DEBUG"/>
        <filter-spec value="match(&quot;Statement|prepareStatement&quot;)"/>
        <handlers>
            <handler name="sacer_jdbc_handler"/>
        </handlers>
    </logger>
    <logger category="org.hibernate" use-parent-handlers="false">
        <level name="WARNING"/>
        <handlers>
            <handler name="sacer_jdbc_handler"/>
        </handlers>
    </logger>
    <!-- ... -->
</subsystem>
```

I comandi CLI

```bash
/subsystem=logging/logger=org.hibernate:add-handler(name=sacer_jdbc_handler)

/subsystem=logging/logger=jboss.jdbc.spy:add-handler(name=sacer_jdbc_handler)
```
###  Profilo SACER

#### JBoss CLI 

```bash
/subsystem=logging/logging-profile=SACER:add()
/subsystem=logging/logging-profile=SACER/periodic-rotating-file-handler=sacer_handler:add(level=INFO,formatter="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n",file={path="sacer.log",relative-to="jboss.server.log.dir"},suffix=".yyyy-MM-dd",append=true)
/subsystem=logging/logging-profile=SACER/size-rotating-file-handler=sacer_tx_connection_handler:add(level=DEBUG,formatter="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n",file={path="sacer_conn_handler.log",relative-to="jboss.server.log.dir"},append=true,max-backup-index=1,rotate-size="256m")
/subsystem=logging/logging-profile=SACER/root-logger=ROOT:add(level=INFO,handlers=[sacer_handler])
/subsystem=logging/logging-profile=SACER/logger=org.springframework:add(level=ERROR,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=org.opensaml:add(level=ERROR,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=es.mityc:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=it.eng.crypto:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=it.eng.parer.crypto:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=it.eng.parer.volume:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=it.eng.parer.ws:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=it.eng.parer.restWS:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=it.eng.parer.admin:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=it.eng.parer.web:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=it.eng.spagoLite:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=iit.eng.parer.ws.utils.AvanzamentoWs:add(level=OFF,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=org.exolab.castor.xml.NamespacesStack:add(level=OFF,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=org.exolab.castor.xml.EndElementProcessor:add(level=ERROR,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=org.jboss.jca.core.connectionmanager.listener.TxConnectionListener:add(level=DEBUG,handlers=[sacer_tx_connection_handler])
/subsystem=logging/logging-profile=SACER/logger=it.eng.parer.job.indiceAip:add(level=DEBUG,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=stdout:add(level=OFF,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=org.hibernate:add(level=ERROR,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACER/logger=jboss.jdbc.spy:add(level=ERROR,use-parent-handlers=true)

```


```xml
<logging-profiles>
    <!-- ... -->
    <logging-profile name="SACER">
        <periodic-rotating-file-handler name="sacer_handler" autoflush="true">
            <level name="INFO"/>
            <formatter>
                <pattern-formatter pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n"/>
            </formatter>
            <file relative-to="jboss.server.log.dir" path="sacer.log"/>
            <suffix value=".yyyy-MM-dd"/>
            <append value="true"/>
        </periodic-rotating-file-handler>
        <size-rotating-file-handler name="sacer_tx_connection_handler" autoflush="true">
            <level name="DEBUG"/>
            <formatter>
                <pattern-formatter pattern="%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n"/>
            </formatter>
            <file relative-to="jboss.server.log.dir" path="sacer_conn_handler.log"/>
            <append value="true"/>
            <max-backup-index value="1">
            <rotate-size value="256m"/>
        </size-rotating-file-handler>
        <logger category="org.springframework" use-parent-handlers="true">
            <level name="ERROR"/>
        </logger>
        <logger category="org.opensaml" use-parent-handlers="true">
            <level name="ERROR"/>
        </logger>
        <logger category="org.hibernate" use-parent-handlers="true">
            <level name="ERROR"/>
        </logger>
        <logger category="jboss.jdbc.spy" use-parent-handlers="true">
            <level name="ERROR"/>
        </logger>
        <logger category="es.mityc" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="it.eng.crypto" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="it.eng.parer.crypto" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="it.eng.parer.volume" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="it.eng.parer.ws" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="it.eng.parer.restWS" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="it.eng.parer.admin" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="it.eng.parer.web" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="it.eng.spagoLite" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="it.eng.parer.ws.utils.AvanzamentoWs"use-parent-handlers="true">
            <level name="OFF"/>
        </logger>
        <logger category="org.exolab.castor.xml.NamespacesStack" use-parent-handlers="true">
            <level name="OFF"/>
        </logger>
        <logger category="org.exolab.castor.xml.EndElementProcessor" use-parent-handlers="true">
            <level name="ERROR"/>
        </logger>
        <logger category="org.jboss.jca.core.connectionmanager.listener.TxConnectionListener" use-parent-handlers="true">
            <level name="DEBUG"/>
            <handlers>
                <handler name="sacer_tx_connection_handler"/>
            </handlers>
        </logger>
        <logger category="it.eng.parer.job.indiceAip" use-parent-handlers="true">
            <level name="DEBUG"/>
        </logger>
        <logger category="stdout" use-parent-handlers="true">
            <level name="OFF"/>
        </logger>
        <root-logger>
            <level name="INFO"/>
            <handlers>
                <handler name="sacer_handler"/>
            </handlers>
        </root-logger>
    </logging-profile>
    <!-- ... -->
</logging-profiles>
```

## Regole di Rewrite

Per il corretto funzionamento dei versamenti dopo l'introduzione di SacerWS è necessario applicare le seguenti regole di rewrite. In ParER queste regole sono state impostate nel bilanciatore LBL.

URL chiamato | Redirect verso
--- | --- 
https://parer.regione.emilia-romagna.it/sacer/VersamentoSync | https://parer.regione.emilia-romagna.it/sacerws/VersamentoSync |
https://parer.regione.emilia-romagna.it/sacer/AggiuntaAllegatiSync | https://parer.regione.emilia-romagna.it/sacerws/AggiuntaAllegatiSync |
https://parer.regione.emilia-romagna.it/sacer/VersamentoMultiMedia | https://parer.regione.emilia-romagna.it/sacerws/VersamentoMultiMedia |
https://parer.regione.emilia-romagna.it/sacer/VersamentoFascicoloSync | https://parer.regione.emilia-romagna.it/sacerws/VersamentoFascicoloSync  |


## Object storage: configurazione AWS Access Key ID e  Secret Access Key  

Attraverso opportune configurazioni, è possibile attivare l'integrazione con uno o più cloud storage (o object storage), secondo determinati processi (e.g. versamento unità documentaria sincrona), Questo è possibile attraerso system property che permettono all'applicazione di recuperare in modalità chiave/valore le credenziali di accesso necessarie per l'interazione con l'object storage secondo lo standard AWS S3 (https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html).

La property configurata viene mappata su base dati (e.g. vedere table **DEC_CONFIG_OBJECT_STORAGE**), il client quindi non farà altro che recuparare, sulla base del flusso applicativo / funzionalità abilitata all'integrazione, la coppia accessKeyId / secretKey, attraverso le quali sarà possibile effettuare l'invocazione dell'API S3.

L'applicazione supporta tutti gli object storage che aderiscono (anche solo parzialmente) allo standard AWS S3.

### Esempio 

```bash
batch

/system-property=sip-r.aws.accessKeyId:add(value="$accessKeyId")
/system-property=sip-r.aws.secretKey:add(value="$secretKey")

run-batch
```

nel caso specifico dello script sopra riportato, le chiavi interessate sono : **sip-r.aws.accessKeyId** e **sip-r.aws.secretKey**; rispettivamente configurate nella tabella citata in precedenza.

### Esempio di configuazione su database 


|  ID_DEC_CONFIG_OBJECT_STORAGE | ID_DEC_BACKEND   |  DS_VALORE_CONFIG_OBJECT_STORAGE | TI_USO_CONFIG_OBJECT_STORAGE  | NM_CONFIG_OBJECT_STORAGE  |  DS_DESCRIZIONE_CONFIG_OBJECT_STORAGE |
|---|---|---|---|---|---|
|  1 |  2 | ACCESS_KEY_ID_SYS_PROP  | sip-r.aws.accessKeyId  | READ_SIP   |   Nome della system property utilizzata per l'access key id per il bucket dei sip in sola lettura |
|  2 | 2  | SECRET_KEY_SYS_PROP  | sip-r.aws.secretKey  | READ_SIP  | Nome della system property utilizzata per la secret key per il bucket dei sip in sola lettura |

Nota: la FK (chiave esterna) legata al valore presente su colonna ID_DEC_BACKEND, dipende dalla configurazione presente su DEC_BACKEND.

