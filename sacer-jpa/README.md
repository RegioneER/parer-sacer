# Sacer JPA

Fonte template redazione documento:  https://www.makeareadme.com/.


# Descrizione

Il seguente progetto è utilizzato come **dipendenza** interna da Sacer e Sacerws.
Lo scopo è quello di definire le entità, secondo standard JPA v.2.1, che modellano la struttura della base dati relazionale.

## Hibernate

Viene utilizzato Hibernate come ORM (Object-Relating Model) alla versione 5.x, che può essere quindi configurato come da [documentazione](https://hibernate.org/orm/documentation/5.0/) ufficiale. La configurazione base è definita su file [persistence.xml](src/main/resources/META-INF/persistence.xml): 

```xml
<properties>
    <!-- Hibernate properties -->
    <property name="hibernate.dialect" value="org.hibernate.dialect.Oracle12cDialect" />
    <property name="hibernate.format_sql" value="false" />
    <property name="hibernate.show_sql" value="${hibernate.show_sql}" />
    <property name="hibernate.hbm2ddl.auto" value="${hibernate.hbm2ddl.auto}" />
    <property name="hibernate.generate_statistics" value="true" />
    <property name="hibernate.id.new_generator_mappings" value="true" />
    <property name="hibernate.session.events.log" value="false" />
    <property name="hibernate.session_factory.session_scoped_interceptor"
        value="it.eng.paginator.hibernate.OracleSqlInterceptor" />
</properties>
```
nello specifico, alcuni valori sono definiti attraverso un maven profile su [pom.xml](pom.xml), ma ovviamente sarà possibile effettuare tutte le modifiche necessaria per poi compilare nuovamente l'artifact (maven package).

# Installazione

Come già specificato nel paragrafo precedente [Descrizione](# Descrizione) si tratta di un progetto di tipo "libreria", quindi un modulo applicativo utilizzato attraverso la definzione della dipendenza Maven secondo lo standard previsto (https://maven.apache.org/): 

```xml
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>sacer-jpa-el</artifactId>
    <version>$VERSIONE</version>
</dependency> 
```

# Utilizzo

La libreria Sacer-jpa definisce al suo interno il "sovra-insieme" di entità create su base dati, la quale è "condivisa" tra i due contesti applicativi Sacer e Sacerws, per cui entrambi faranno riferimento alle stesse entity JPA utilizzandone un sotto-insieme secondo logiche locali. Le entità descrivono quindi oltre le tabelle "standard" creati all'interno della base dati, anche viste e tabelle definite su altro schema (tabelle con "grant").

# Supporto

Mantainer del progetto è [Engineering Ingegneria Informatica S.p.A.](https://www.eng.it/).

# Contributi

Se interessati a crontribuire alla crescita del progetto potete scrivere all'indirizzo email <a href="mailto:areasviluppoparer@regione.emilia-romagna.it">areasviluppoparer@regione.emilia-romagna.it</a>.

# Credits

Progetto di proprietà di [Regione Emilia-Romagna](https://www.regione.emilia-romagna.it/) sviluppato a cura di [Engineering Ingegneria Informatica S.p.A.](https://www.eng.it/).

# Licenza

Questo progetto è rilasciato sotto licenza GNU Affero General Public License v3.0 or later ([LICENSE.txt](LICENSE.txt)).