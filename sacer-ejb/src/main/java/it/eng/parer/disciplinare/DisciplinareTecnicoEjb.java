package it.eng.parer.disciplinare;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.AmbientiHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.grantedEntity.AplParamApplicReport;
import it.eng.parer.grantedEntity.OrgDiscipStrut;
import it.eng.parer.grantedEntity.SIUsrOrganizIam;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * @author Iacolucci_M
 */
@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class DisciplinareTecnicoEjb {
    @EJB
    private DisciplinareTecnicoHelper disciplinareTecnicoHelper;
    @EJB
    private AmbientiHelper ambientiHelper;
    @EJB
    private ConfigurationHelper configurationHelper;

    private static final Logger log = LoggerFactory.getLogger(DisciplinareTecnicoHelper.class);

    public byte[] generaDisciplinareTecnicoPDF(long idStrut) throws ParerUserError {
        byte[] arrayPDF = null;
        String idAccordo = null;
        String idEnteConvenzionato = null;
        AplParamApplicReport par = null;
        OrgDiscipStrut ods = null;
        log.debug("Inizio produzione disciplinare per la struttura {}", idStrut);
        try {
            String nmApplic = configurationHelper.getValoreParamApplic("NM_APPLIC", null, null, null, null,
                    CostantiDB.TipoAplVGetValAppart.APPLIC);
            par = disciplinareTecnicoHelper.getAplParamApplicReportByAppReport(nmApplic, "DISCIPLINARE_TECNICO");
            SIUsrOrganizIam oIam = ambientiHelper.getSIUsrOrganizIam(new BigDecimal(idStrut));
            if (par != null) {
                // ********** ESTRAE FOTO *****************
                Clob risultato = disciplinareTecnicoHelper.eseguiQuery(par.getBlFile1(), idStrut);
                // ********** ESTRAE XML INTERMEDIO *****************
                String xmlIntermedio = disciplinareTecnicoHelper.trasformaInXmlIntermedio(risultato, par.getBlFile2());
                // ********** ESTRAE XML FOP *****************
                String xmlFo = disciplinareTecnicoHelper.trasformaInFo(xmlIntermedio, par.getBlFile3());
                // ********** ESTRAE PDF *****************
                arrayPDF = disciplinareTecnicoHelper.trasformaInPDF(xmlFo);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                byte[] xmlIniziale = disciplinareTecnicoHelper.getClobAsByteArray(risultato);
                InputSource is = new InputSource(new StringReader(new String(xmlIniziale, "UTF-8")));
                Document doc = builder.parse(is);
                XPath xPath = XPathFactory.newInstance().newXPath();
                // Cerca l'ID_ACCORDO nell'XML
                String queryXml = "//fotoOggetto/recordChild[tipoRecord=\"Ente convenzionato\"]/child/recordChild[tipoRecord=\"Accordo\"]/child[ (datoRecord/colonnaDato=\"fl_valido\" and datoRecord/valoreDato='1') or (datoRecord/colonnaDato=\"fl_valido\" and datoRecord/valoreDato='0')][1]/idRecord";
                XPathExpression expr = xPath.compile(queryXml);
                idAccordo = expr.evaluate(doc);
                // Cerca l'ID_ENTE_CONVENZ nell'XML
                queryXml = "//fotoOggetto/recordChild[tipoRecord=\"Ente convenzionato\"]/child/idRecord";
                expr = xPath.compile(queryXml);
                idEnteConvenzionato = expr.evaluate(doc);
                if (idEnteConvenzionato != null || !idEnteConvenzionato.equals("")) {
                    queryXml = "//fotoOggetto/recordMaster/keyRecord/datoKey[colonnaKey=\"data_generazione\"]/valoreKey";
                    expr = xPath.compile(queryXml);
                    String dataGenerazioneDisciplinare = expr.evaluate(doc);
                    ods = new OrgDiscipStrut();
                    ods.setIdOrganizIam(oIam.getIdOrganizIam());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    ods.setDtDiscipStrut(sdf.parse(dataGenerazioneDisciplinare));
                }
            }
        } catch (Exception ex) {
            log.error("ERRORE nella produzione del disciplinare per la struttura {}", idStrut, ex);
            throw new RuntimeException("ERRORE nella produzione del disciplinare", ex);
        }
        if (idEnteConvenzionato == null || idEnteConvenzionato.equals("")) {
            throw new ParerUserError("Nel disciplinare non è presente alcun ente convenzionato!");
        }
        if (idAccordo == null || idAccordo.equals("")) {
            throw new ParerUserError("Nel disciplinare non è presente alcun accordo!");
        }
        try {
            if (par != null) {
                ods.setIdEnteConvenz(Long.parseLong(idEnteConvenzionato));
                ods.setIdAccordoEnte(Long.parseLong(idAccordo));
                ods.setBlDiscipStrut(arrayPDF);
                disciplinareTecnicoHelper.getEntityManager().persist(ods);
                log.debug("Fine produzione disciplinare per la struttura {}", idStrut);
            }
        } catch (Exception ex) {
            log.error("ERRORE nella produzione del disciplinare per la struttura {}", idStrut, ex);
            throw new RuntimeException("ERRORE nella produzione del disciplinare", ex);
        }
        return arrayPDF;
    }

}
