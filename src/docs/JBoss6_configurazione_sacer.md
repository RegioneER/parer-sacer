---
title: "Configurazione Sacer"
---

# Configurazione Jboss EAP 6.4

## Versioni 

| Vers. doc | Vers. Sacer  | Modifiche  |
| -------- | ---------- | ---------- |
| 1.0.0 | 6.7.7 | Versione iniziale del documento  |

## Datasource XA

Per configurare il datasource dell'applicativo dalla console web di amministrazione bisogna andare su

`Configuration > Connector > datasources`

Scegliere **XA DATASOURCES** e premere 

`Add`

Si apre un wizard in 3 passaggi
1. Aggiungere gli attributi del datasource: Nome=**SacerPool** e JNDI=**java:/jdbc/SacerDs**
2. Selezionare il driver **ojdbc8** (predisposto durante la configurazione generale di Jboss) e impostare **oracle.jdbc.xa.client.OracleXADataSource** come XA Data Source Class;
3. Impostare gli attributi della connessione, ad esempio *URL* 

### Configurazione del transaction service 

Lo schema dell'applicazione ha bisogno delle seguenti grant su Oracle.

```sql
GRANT SELECT ON sys.dba_pending_transactions TO SACER;
GRANT SELECT ON sys.pending_trans$ TO SACER;
GRANT SELECT ON sys.dba_2pc_pending TO SACER;
GRANT EXECUTE ON sys.dbms_xa TO SACER;
```

La procedura è descritta nella documentazione standard di JBoss EAP 6.4

https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/6.4/html/Administration_and_Configuration_Guide/sect-XA_Datasources.html#Create_an_XA_Datasource_with_the_Management_Interfaces

## Configurazione Servizio JMS

Per la configurazione del subsystem si rimanda alla documentazione generale di JBoss EAP 6.4 del ParER.  
Una volta fatto è necessario impostare le risorse JMS.

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
OggettiDaMigrareQueue | java:/jms/queue/OggettiDaMigrareQueue |
OggettiVerificatiQueue | java:/jms/queue/OggettiVerificatiQueue |
OggettiInErroreQueue | java:/jms/queue/OggettiInErroreQueue |
IndiciAIPUDDaElabQueue | java:/jms/queue/IndiciAIPUDDaElabQueue |

#### Configurazione tramite CLI

```bash
jms-queue --profile={my-profile} add --queue-address=VerificaFirmeDataVersQueue --entries=[java:/jms/queue/VerificaFirmeDataVersQueue]

jms-queue --profile={my-profile} add --queue-address=IndiceAipUnitaDocQueue --entries=[java:/jms/queue/IndiceAipUnitaDocQueue]

jms-queue --profile={my-profile} add --queue-address=OggettiDaMigrareQueue --entries=[java:/jms/queue/OggettiDaMigrareQueue]

jms-queue --profile={my-profile} add --queue-address=OggettiVerificatiQueue --entries=[java:/jms/queue/OggettiVerificatiQueue]

jms-queue --profile={my-profile} add --queue-address=OggettiInErroreQueue --entries=[java:/jms/queue/OggettiInErroreQueue]

# cd /profile={my-profile}/subsystem=messaging/hornetq-server=default
# ./jms-queue=IndiciAIPUDDaElabQueue:add(durable=true,entries=["java:/jms/queue/IndiciAIPUDDaElabQueue"])
jms-queue --profile={my-profile} add --queue-address=IndiciAIPUDDaElabQueue --entries=[java:/jms/queue/IndiciAIPUDDaElabQueue]
```

Sostiture {my-profile} con la keyword adeguata. 

### Configurazione JMS per migrazione blob su object storage

Nell'ambito dell'introduzione dell'infrastruttura OpenShift in regione Emilia-Romagna sono stati creati alcuni micro-servizi che dialogano con SACER tramite un bridge JMS.  
Un bridge JMS è un sotto-sistema dell'application server ed è composto da:

 1. una o più destinazioni JMS locali, vedi Configurazione [Risorse JMS / Nomi JNDI](#configurazione-risorse-jms-e-nomi-jndi);
 2. un broker di messaggi remoto; 
 3. un modulo JBoss contenente le librerie necessarie per decodificare i messaggi del broker remoto (nel nostro caso il modulo è `org.apache.artemismq`);
 4. uno o più componenti (*bridge*) che redirigono i messaggi tra le destinazioni locali e le destinazioni esposte dal broker remoto (solo se necessaria la funzionalità di migrazione su Object Storage).

**Le configurazioni dei punti 2, 3 e 4 sono obbligatorie solo se necessaria la funzionalità di migrazione su Object Storage.**  
Il compito del bridge JMS è quello di gestire in maniera automatica la consegna dei messaggi tra una destinazione locale ed una remota utilizzando le transazioni XA. In caso di problematiche di rete il bridge si occupa di gestire in autonomia la riconnessione alla controparte remota.  

Di seguito le configurazioni da applicare al file standalone.xml oppure, in ambiente domain, al file domain.xml.

#### Creazione Bridge

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- … -->
<subsystem xmlns="urn:jboss:domain:messaging:1.4">
  <!-- … -->
  <jms-bridge name="hornetq-artemismq-da-migrare" module="org.apache.artemismq">
    <source>
      <connection-factory name="java:/ConnectionFactoryXA"/>
      <destination name="java:/jms/queue/OggettiDaMigrareQueue"/>
    </source>
    <target>
      <connection-factory name="ConnectionFactory"/>
      <destination name="oggetti-da-migrare"/>
      <user>jms-user</user>
      <password>password</password>
      <context>
        <property key="java.naming.factory.initial" value="org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory"/>
        <property key="java.naming.provider.url" value="tcp://<ARTEMIS_SERVER_HOST>:<ARTEMIS_SERVER_PORT>?type=XA_CF&sslEnabled=true"/>
        <property key="queue.oggetti-da-migrare" value="oggetti-da-migrare"/>
        <property key="connectionFactory.ConnectionFactory" value="tcp://<ARTEMIS_SERVER_HOST>:<ARTEMIS_SERVER_PORT>?type=XA_CF&sslEnabled=true"/>
      </context>
    </target>
    <quality-of-service>ONCE_AND_ONLY_ONCE</quality-of-service>
    <failure-retry-interval>2000</failure-retry-interval>
    <max-retries>10</max-retries>
    <max-batch-size>500</max-batch-size>
    <max-batch-time>500</max-batch-time>
    <add-messageID-in-header>true</add-messageID-in-header>
  </jms-bridge>
  <jms-bridge name="artemismq-hornetq-verificati" module="org.apache.artemismq">
    <source>
      <connection-factory name="ConnectionFactory"/>
      <destination name="oggetti-verificati"/>
      <user>jms-user</user>
      <password>password</password>
      <context>
        <property key="java.naming.factory.initial" value="org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory"/>
        <property key="java.naming.provider.url" value="tcp://<ARTEMIS_SERVER_HOST>:<ARTEMIS_SERVER_PORT>?type=XA_CF&sslEnabled=true"/>
        <property key="queue.oggetti-verificati" value="oggetti-verificati"/>
        <property key="connectionFactory.ConnectionFactory" value="tcp://<ARTEMIS_SERVER_HOST>:<ARTEMIS_SERVER_PORT>?type=XA_CF&sslEnabled=true"/>
      </context>
    </source>
    <target>
      <connection-factory name="java:/ConnectionFactoryXA"/>
      <destination name="java:/jms/queue/OggettiVerificatiQueue"/>
    </target>
    <quality-of-service>ONCE_AND_ONLY_ONCE</quality-of-service>
    <failure-retry-interval>2000</failure-retry-interval>
    <max-retries>10</max-retries>
    <max-batch-size>500</max-batch-size>
    <max-batch-time>500</max-batch-time>
    <add-messageID-in-header>true</add-messageID-in-header>
  </jms-bridge>
  <jms-bridge name="artemismq-hornetq-in-errore" module="org.apache.artemismq">
    <source>
      <connection-factory name="ConnectionFactory"/>
      <destination name="oggetti-in-errore"/>
      <user>{jms-user}</user>
      <password>{password}</password>
      <context>
        <property key="java.naming.factory.initial" value="org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory"/>
        <property key="java.naming.provider.url" value="tcp://{ARTEMIS_SERVER_HOST}:{ARTEMIS_SERVER_PORT}?type=XA_CF&sslEnabled=true"/>
        <property key="queue.oggetti-in-errore" value="oggetti-in-errore"/>
        <property key="connectionFactory.ConnectionFactory" value="tcp://{ARTEMIS_SERVER_HOST}:{ARTEMIS_SERVER_PORT}?type=XA_CF&sslEnabled=true"/>
      </context>
    </source>
    <target>
      <connection-factory name="java:/ConnectionFactoryXA"/>
      <destination name="java:/jms/queue/OggettiInErroreQueue"/>
    </target>
    <quality-of-service>ONCE_AND_ONLY_ONCE</quality-of-service>
    <failure-retry-interval>2000</failure-retry-interval>
    <max-retries>10</max-retries>
    <max-batch-size>500</max-batch-size>
    <max-batch-time>500</max-batch-time>
    <add-messageID-in-header>true</add-messageID-in-header>
  </jms-bridge>
</subsystem>
```
I parametri tra {} indicano le configurazioni specifiche del broker remoto. 

#### Modulo ActiveMQ/Artemis

ActiveMQ/Artemis è l’implementazione delle code JMS che utilizziamo come broker remoto. Per poter configurare correttamente il bridge è necessario che sia installato il modulo JBoss `org.apache.artemismq`.  
La configurazione del modulo di ActiveMQ/Artemis è definita nel file
`${JBOSS_HOME}/modules/system/layers/base/org/apache/artemismq/main/module.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.1" name="org.apache.artemismq" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <resources>
    <resource-root path="."/>
    <resource-root path="artemis-commons-1.5.5.jar"/>
    <resource-root path="artemis-core-client-1.5.5.jar"/>
    <resource-root path="artemis-jdbc-store-1.5.5.jar"/>
    <resource-root path="artemis-jms-client-1.5.5.jar"/>
    <resource-root path="artemis-jms-server-1.5.5.jar"/>
    <resource-root path="artemis-journal-1.5.5.jar"/>
    <resource-root path="artemis-native-1.5.5.jar"/>
    <resource-root path="artemis-ra-1.5.5.jar"/>
    <resource-root path="artemis-selector-1.5.5.jar"/>
    <resource-root path="artemis-server-1.5.5.jar"/>
    <resource-root path="artemis-service-extensions-1.5.5.jar"/>
    <resource-root path="commons-beanutils-1.9.2.jar"/>
    <resource-root path="commons-collections-3.2.2.jar"/>
    <resource-root path="commons-logging-1.2.jar"/>
    <resource-root path="geronimo-ejb_3.0_spec-1.0.1.jar"/>
    <resource-root path="geronimo-jms_2.0_spec-1.0-alpha-2.jar"/>
    <resource-root path="geronimo-json_1.0_spec-1.0-alpha-1.jar"/>
    <resource-root path="geronimo-jta_1.1_spec-1.1.1.jar"/>
    <resource-root path="guava-19.0.jar"/>
    <resource-root path="jboss-logging-3.3.0.Final.jar"/>
    <resource-root path="jgroups-3.6.9.Final.jar"/>
    <resource-root path="johnzon-core-0.9.5.jar"/>
    <resource-root path="netty-all-4.1.5.Final.jar"/>
  </resources>
  <exports>
    <exclude path="org/springframework/**"/>
    <exclude path="org/apache/xbean/**"/>
    <exclude path="org/apache/commons/**"/>
    <exclude path="org/aopalliance/**"/>
    <exclude path="org/fusesource/**"/>
  </exports>l
  <dependencies>
    <module name="javax.api"/>
    <module name="sun.jdk"/>
    <!--<module name="org.jboss.netty" />-->
    <module name="org.slf4j"/>
    <module name="javax.resource.api"/>
    <module name="javax.jms.api"/>
    <module name="javax.management.j2ee.api"/>
  </dependencies>
</module>
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

#### Configurazione tramite CLI

Sostituire la keyword *{my-profile}* con la keyword adeguata.

```bash
/profile={my-profile}/subsystem=ejb3/strict-max-bean-instance-pool=coda-verifica-firme-pool:add(max-pool-size=5)

/profile={my-profile}/subsystem=ejb3/strict-max-bean-instance-pool=coda-indice-aip-ud-pool:add(max-pool-size=2)

/profile={my-profile}/subsystem=ejb3/strict-max-bean-instance-pool=coda-oggetti-verificati-pool:add(max-pool-size=5)

/profile={my-profile}/subsystem=ejb3/strict-max-bean-instance-pool=coda-oggetti-errati-pool:add(max-pool-size=5)

/profile={my-profile}/subsystem=ejb3/strict-max-bean-instance-pool=coda-indici-aip-da-elab-pool:add(max-pool-size=3, timeout=5, timeout-unit="MINUTES")
``` 

### Impostazione del message grouping

Questa impostazione è necessaria perché venga utilizzata il Group ID impostato dal producer.
Bisogna impostare come "LOCAL" un nodo che si occuperà di decidere l'associazione Gruop ID <=> host, mentre gli altri saranno definiti "REMOTE". 

#### Configurazione tramite CLI

```bash
/profile={my-profile}/subsystem=messaging/hornetq-server=default/grouping-handler=my-grouping-handler:add(group-timeout=5000,grouping-handler-address=jms,type="${grouping-handler.type}")
/host={nome-host-1}/system-property=grouping-handler.type:add(value=LOCAL)
/host={nome-host-2}/system-property=grouping-handler.type:add(value=REMOTE)
/host={nome-host-n}/system-property=grouping-handler.type:add(value=REMOTE)
``` 
Sostiture {my-profile} con il profilo corretto.

## Key Store 

È necessario mettere il keystore in formato JKS in una cartella accessibile all'IDP e poi configurare la system properties sacer-jks-path con il path al file.

## System properties

Dalla console web di amministrazione 

`Configuration > System properties`

impostare le seguenti properties

Chiave | Valore di esempio | Descrizione
--- | --- | ---
sacer-key-manager-pass | <password_jks_sacer> | Chiave del Java Key Store utilizzato per ottenere la chiave privata del circolo di fiducia dell’IDP.
sacer-timeout-metadata | 10000 | Timeout in secondi per la ricezione dei metadati dall’IDP.
sacer-temp-file | /var/tmp/tmp-sacer-federation.xml | Percorso assoluto del file xml che rappresenta l’applicazione all’interno del circolo di fiducia.
sacer-sp-identity-id | https://parer.regione.emilia-romagna.it/sacer | Identità dell’applicazione all’interno del circolo di fiducia.
sacer-refresh-check-interval | 600000 | Intervallo di tempo in secondi utilizzato per ricontattare l’IDP per eventuali variazioni sulla configurazione del circolo di fiducia.
sacer-jks-path | /opt/jboss-eap/certs/sacer.jks | Percorso assoluto del Java Key Store dell’applicazione.
sacer-store-key-name | sacer | Alias del certificato dell’applicazione all’interno del Java Key Store.
aws.accessKeyId | <accessKeyId_object_storage> | Access Key id delle credenziali S3 per l’accesso all’object storage per il servizio di migrazione.
aws.secretKey | <secretKey_object_storage> | Secret Key delle credenziali S3 per l’accesso all’object storage per il servizio di migrazione.

## Logging profile

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
        <periodic-size-rotating-file-handler name="sacer_tx_connection_handler" autoflush="true">
            <level name="DEBUG"/>
            <formatter>
                <pattern-formatter pattern="%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n"/>
            </formatter>
            <file relative-to="jboss.server.log.dir" path="sacer_conn_handler.log"/>
            <append value="true"/>
            <max-backup-index value="1">
            <rotate-size value="256m"/>
        </periodic-size-rotating-file-handler>
        <logger category="org.springframework" use-parent-handlers="true">
            <level name="ERROR"/>
        </logger>
        <logger category="org.opensaml" use-parent-handlers="true">
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
