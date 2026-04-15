/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.xml.xsd;

/**
 * Esempi di utilizzo della validazione XML con XSD modulari.
 *
 * Questa classe contiene esempi pratici di come utilizzare il nuovo meccanismo di validazione
 * multi-namespace con import/include.
 */
public class XsdValidationExamples {

    // ========================================================================
    // ESEMPIO 1: Validazione Semplice con Service
    // ========================================================================

    /**
     * Esempio base: validazione di un XML con XSD modulare usando il service.
     *
     * @EJB private XsdValidationService validationService;
     * @EJB private XsdRepositoryHelper xsdRepositoryHelper;
     *
     *      public void esempioValidazioneSemplice() { try { // Recupera l'XSD root (ad esempio
     *      luoghi-model-1.xsd) Long idXsdRoot = 1001L; // ID dell'XSD padre nel database
     *
     *      // XML da validare String xml = """ <?xml version="1.0" encoding="UTF-8"?>
     *      <luoghi:Fascicolo xmlns:luoghi="urn:rer_parer:luoghi:modello:1" xmlns:catasto=
     *      "urn:rer_parer:catasto:modello:1" xmlns:clv="https://w3id.org/italia/onto/CLV/">
     *      <luoghi:identificativo>FASC-001</luoghi:identificativo>
     *      <catasto:particella>123</catasto:particella> <clv:indirizzo>Via Roma 1</clv:indirizzo>
     *      </luoghi:Fascicolo> """;
     *
     *      // Valida (il service gestisce automaticamente import/include)
     *      validationService.validateXmlWithModularXsd(idXsdRoot, xml);
     *
     *      System.out.println("Validazione completata con successo!");
     *
     *      } catch (SAXException e) { System.err.println("Errore validazione: " + e.getMessage());
     *      } catch (IOException e) { System.err.println("Errore I/O: " + e.getMessage()); } }
     */

    // ========================================================================
    // ESEMPIO 2: Validazione con Recupero Manuale XSD
    // ========================================================================

    /**
     * Esempio intermedio: recupero manuale dell'XSD e validazione.
     *
     * @EJB private XsdValidationService validationService;
     * @EJB private XsdRepositoryHelper xsdRepositoryHelper;
     *
     *      public void esempioValidazioneManuale() { try { // Recupera il modello XSD dal database
     *      DecModelloXsdFascicolo xsdRoot = xsdRepositoryHelper.getModelloXsdFascicolo(1001L,
     *      true);
     *
     *      if (xsdRoot == null) { throw new IllegalStateException("XSD non trovato"); }
     *
     *      // Leggi XML da file o altra sorgente String xml = leggiXmlDaFile("fascicolo.xml");
     *
     *      // Valida validationService.validateXmlWithModularXsd(xsdRoot, xml);
     *
     *      System.out.println("Validazione OK per: " + xsdRoot.getCdXsd());
     *
     *      } catch (Exception e) { System.err.println("Errore: " + e.getMessage()); } }
     *
     *      private String leggiXmlDaFile(String filename) throws IOException { // Implementazione
     *      lettura file return Files.readString(Path.of(filename)); }
     */

    // ========================================================================
    // ESEMPIO 3: Validazione Batch con Schema Riutilizzabile
    // ========================================================================

    /**
     * Esempio avanzato: validazione batch ottimizzata con compilazione schema una volta.
     *
     * @EJB private XsdRepositoryHelper xsdRepositoryHelper;
     *
     *      public void esempioValidazioneBatch(List<String> xmlList) { try { // Recupera XSD root
     *      DecModelloXsdFascicolo xsdRoot = xsdRepositoryHelper.getModelloXsdFascicolo(1001L,
     *      true);
     *
     *      // Crea resolver DbXsdResourceResolver resolver = new
     *      DbXsdResourceResolver(xsdRepositoryHelper);
     *
     *      // Compila schema UNA VOLTA (performance!) Schema schema =
     *      XmlUtils.getSchemaValidationWithResolver( new StreamSource(new
     *      StringReader(xsdRoot.getBlXsd())), resolver);
     *
     *      // Crea validator riutilizzabile Validator validator = schema.newValidator();
     *      validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
     *      validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
     *
     *      // Valida tutti gli XML int validati = 0; int errori = 0;
     *
     *      for (String xml : xmlList) { try { validator.validate(new StreamSource(new
     *      StringReader(xml))); validator.reset(); validati++; } catch (SAXException e) {
     *      System.err.println("Errore validazione: " + e.getMessage()); errori++; } }
     *
     *      System.out.println("Batch completato: " + validati + " OK, " + errori + " KO");
     *
     *      } catch (Exception e) { System.err.println("Errore batch: " + e.getMessage()); } }
     */

    // ========================================================================
    // ESEMPIO 4: Validazione con Gestione Errori Dettagliata
    // ========================================================================

    /**
     * Esempio con error handler personalizzato per raccogliere tutti gli errori.
     *
     * @EJB private XsdRepositoryHelper xsdRepositoryHelper;
     *
     *      public List<String> esempioValidazioneConErrorHandler(String xml) { List<String> errori
     *      = new ArrayList<>();
     *
     *      try { DecModelloXsdFascicolo xsdRoot = xsdRepositoryHelper.getModelloXsdFascicolo(1001L,
     *      true); DbXsdResourceResolver resolver = new DbXsdResourceResolver(xsdRepositoryHelper);
     *
     *      Schema schema = XmlUtils.getSchemaValidationWithResolver( new StreamSource(new
     *      StringReader(xsdRoot.getBlXsd())), resolver);
     *
     *      Validator validator = schema.newValidator();
     *
     *      // Error handler personalizzato validator.setErrorHandler(new ErrorHandler() {
     * @Override public void warning(SAXParseException e) { errori.add("WARNING: " + e.getMessage()
     *           + " (line " + e.getLineNumber() + ")"); }
     *
     * @Override public void error(SAXParseException e) { errori.add("ERROR: " + e.getMessage() + "
     *           (line " + e.getLineNumber() + ")"); }
     *
     * @Override public void fatalError(SAXParseException e) throws SAXException {
     *           errori.add("FATAL: " + e.getMessage() + " (line " + e.getLineNumber() + ")"); throw
     *           e; } });
     *
     *           validator.validate(new StreamSource(new StringReader(xml)));
     *
     *           } catch (Exception e) { errori.add("Errore generale: " + e.getMessage()); }
     *
     *           return errori; }
     */

    // ========================================================================
    // ESEMPIO 5: Migrazione da Validazione Tradizionale
    // ========================================================================

    /**
     * Esempio di migrazione da codice esistente a validazione modulare.
     *
     * // PRIMA - Validazione tradizionale (solo per XSD singoli) public void
     * validazioneVecchia(DecModelloXsdFascicolo xsd, String xml) throws SAXException, IOException {
     * XmlUtils.validateXml(xsd.getBlXsd(), xml); }
     *
     * // DOPO - Validazione modulare (supporta import/include)
     *
     * @EJB private XsdRepositoryHelper xsdRepositoryHelper;
     *
     *      public void validazioneNuova(DecModelloXsdFascicolo xsd, String xml) throws
     *      SAXException, IOException { DbXsdResourceResolver resolver = new
     *      DbXsdResourceResolver(xsdRepositoryHelper);
     *      XmlUtils.validateXmlWithResolver(xsd.getBlXsd(), xml, resolver); }
     *
     *      // NOTA: Se l'XSD non ha import/include, entrambe le versioni funzionano. // Il resolver
     *      non interferisce con XSD semplici.
     */

    // ========================================================================
    // ESEMPIO 6: Test Risoluzione XSD
    // ========================================================================

    /**
     * Esempio per testare la risoluzione degli XSD importati.
     *
     * @EJB private XsdRepositoryHelper xsdRepositoryHelper;
     *
     *      public void testRisoluzioneXsd() { // Test risoluzione per namespace XsdBlob xsd1 =
     *      xsdRepositoryHelper.findImportedXsd( "IMPORT", "urn:rer_parer:catasto:modello:1",
     *      "catasto-model-1.xsd");
     *
     *      if (xsd1 != null) { System.out.println("XSD trovato: " + xsd1.cdXsd());
     *      System.out.println("Dimensione: " + xsd1.blXsd().length() + " chars"); } else {
     *      System.err.println("XSD non trovato nel repository!"); }
     *
     *      // Test risoluzione solo per namespace (fallback) XsdBlob xsd2 =
     *      xsdRepositoryHelper.findImportedXsd( "IMPORT", "https://w3id.org/italia/onto/CLV/",
     *      null);
     *
     *      if (xsd2 != null) { System.out.println("XSD CLV trovato: " + xsd2.cdXsd()); } }
     */

    // ========================================================================
    // ESEMPIO 7: Utilizzo in un Job/Elaborazione
    // ========================================================================

    /**
     * Esempio di integrazione in un job di elaborazione fascicoli.
     *
     * @EJB private XsdValidationService validationService;
     * @EJB private FascicoloHelper fascicoloHelper;
     *
     *      public void elaboraFascicoli(List<Long> idFascicoli) { for (Long idFascicolo :
     *      idFascicoli) { try { // Recupera fascicolo FasFascicolo fascicolo =
     *      fascicoloHelper.findById(idFascicolo);
     *
     *      // Recupera XML del fascicolo String xmlContent = fascicolo.getXmlContent();
     *
     *      // Recupera ID XSD da configurazione/metadati Long idXsd = fascicolo.getIdModelloXsd();
     *
     *      // Valida con XSD modulare validationService.validateXmlWithModularXsd(idXsd,
     *      xmlContent);
     *
     *      // Aggiorna stato fascicolo fascicolo.setStatoValidazione("VALIDATO");
     *
     *      } catch (SAXException e) { // Gestisci errore validazione System.err.println("Fascicolo
     *      " + idFascicolo + " non valido: " + e.getMessage()); // ... registra errore nel DB
     *
     *      } catch (Exception e) { System.err.println("Errore elaborazione fascicolo " +
     *      idFascicolo + ": " + e.getMessage()); } } }
     */
}
