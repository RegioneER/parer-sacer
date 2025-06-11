## 10.9.0 (11-06-2025)

### Bugfix: 4
- [#38184](https://parermine.regione.emilia-romagna.it/issues/38184) Correzione controllo gestione formati ammessi 
- [#38030](https://parermine.regione.emilia-romagna.it/issues/38030) Inserire i default corretti per le security policy
- [#38025](https://parermine.regione.emilia-romagna.it/issues/38025) Correzione del messaggio di errore relativo all'impossibilità di aggiungere un documento a una UD in stato IN_ARCHIVIO
- [#36875](https://parermine.regione.emilia-romagna.it/issues/36875) Correzione problema legato all'annullamento di una UD contenuta in un elenco con tutte UD annullate

### Novità: 10
- [#38027](https://parermine.regione.emilia-romagna.it/issues/38027) Rimozione all'interno del codice dei riferimenti di utilizzo della tabella VRS_CONENUTO_FILE sostituita dalla tabella VRS_CONTENUTO_FILE_KO
- [#38023](https://parermine.regione.emilia-romagna.it/issues/38023) Ottimizzazione ricerca UD derivanti da versamenti falliti con filtri su classe errore
- [#37955](https://parermine.regione.emilia-romagna.it/issues/37955) Sostituzione partizionamenti manuali per tabelle ELV_FILE_ELENCO_VERS e tabelle correlate by reference.
- [#37769](https://parermine.regione.emilia-romagna.it/issues/37769)  Ottimizzazioni e refactor codice per recupero debito tecnico (SonarQube)
- [#37218](https://parermine.regione.emilia-romagna.it/issues/37218) Aggiornamento librerie ESAPI (OWASP) e SAML + Spring Security 5
- [#35549](https://parermine.regione.emilia-romagna.it/issues/35549) Configurazione modulo jaxp-jdk per JBoss 7 per JDK 11
- [#34792](https://parermine.regione.emilia-romagna.it/issues/34792) Aggiunta nuova tabella nel disciplinare - Utenti automi versatori
- [#34195](https://parermine.regione.emilia-romagna.it/issues/34195) Funzione massiva per riportare indietro lo stato di un elenco per consentire la firma dell'AIP
- [#34194](https://parermine.regione.emilia-romagna.it/issues/34194) Modifiche alla pagina Ricerca elenchi di versamento unità documentarie per individuare gli elenchi da rimandare indietro per la firma dell'AIP
- [#34000](https://parermine.regione.emilia-romagna.it/issues/34000) Migrazione alle nuove dipendenze / pattern legate a xecers, xalan, jaxb, ecc
