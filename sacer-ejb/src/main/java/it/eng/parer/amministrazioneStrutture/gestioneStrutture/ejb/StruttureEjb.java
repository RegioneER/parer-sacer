/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb;

import it.eng.integriam.client.ws.IAMSoapClients;
import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.ejb.DatiSpecificiEjb;
import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.helper.DatiSpecificiHelper;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.ejb.FormatoFileDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.helper.FormatoFileDocHelper;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.helper.FormatoFileStandardHelper;
import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneRegistro.helper.RegistroHelper;
import it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.ejb.SottoStruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.helper.SottoStruttureHelper;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.EntitaValida;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.SalvaStrutturaDto;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.AmbientiHelper;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.CorrispondenzePingHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb.TipoDocumentoEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.helper.TipoDocumentoHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.ejb.TipoFascicoloEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoRappresentazione.ejb.TipoRappresentazioneEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoRappresentazione.helper.TipoRappresentazioneHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.ejb.TipoStrutturaDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.helper.TipoStrutturaDocHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.helper.TipoUnitaDocHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.*;
import it.eng.parer.entity.constraint.AplValoreParamApplic.TiAppart;
import it.eng.parer.entity.constraint.SIOrgEnteSiam.TiEnteConvenz;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.grantedEntity.SIOrgEnteConvenzOrg;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.grantedEntity.SIUsrOrganizIam;
import it.eng.parer.grantedEntity.UsrUser;
import it.eng.parer.grantedViewEntity.OrgVRicEnteConvenzByEsterno;
import it.eng.parer.job.allineamentoEntiConvenzionati.ejb.AllineamentoEntiConvenzionatiEjb;
import it.eng.parer.job.allineamentoEntiConvenzionati.utils.CostantiAllineaEntiConv;
import it.eng.parer.job.allineamentoOrganizzazioni.ejb.AllineamentoOrganizzazioniEjb;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.serie.ejb.TipoSerieEjb;
import it.eng.parer.serie.helper.ModelliSerieHelper;
import it.eng.parer.slite.gen.tablebean.*;
import it.eng.parer.slite.gen.viewbean.*;
import it.eng.parer.slite.gen.viewbean.OrgVRicStrutRowBean;
import it.eng.parer.slite.gen.viewbean.OrgVRicStrutTableBean;
import it.eng.parer.viewEntity.DecVCalcTiServOnTipoUd;
import it.eng.parer.viewEntity.OrgVRicStrut;
import it.eng.parer.viewEntity.OrgVCorrPing;
import it.eng.parer.web.ejb.AmministrazioneEjb;
import it.eng.parer.web.ejb.StrutCache;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.CriteriRaggrHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.parer.web.util.XmlPrettyPrintFormatter;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import it.eng.integriam.client.ws.calcoloservizierogati.CalcoloServiziErogati;
import it.eng.parer.amministrazioneStrutture.gestioneSistemaMigrazione.helper.SistemaMigrazioneHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.helper.TipoFascicoloHelper;
import it.eng.parer.entity.AplSistemaMigraz;
import it.eng.parer.entity.OrgUsoSistemaMigraz;
import it.eng.parer.fascicoli.helper.CriteriRaggrFascicoliHelper;
import it.eng.parer.fascicoli.helper.ModelliFascicoliHelper;
import java.nio.charset.StandardCharsets;
import javax.xml.ws.BindingProvider;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class StruttureEjb {

    private static final Logger logger = LoggerFactory.getLogger(StruttureEjb.class);
    private static final String CALCOLO_SERVIZI_EROGATI_RISPOSTA_NEGATIVA_ENTE_CONVENZIONATO = "Calcolo servizi erogati - Risposta WS negativa per l'ente convenzionato";

    @Resource
    private SessionContext context;
    @EJB
    private StruttureHelper struttureHelper;
    @EJB
    private RegistroEjb registroEjb;
    @EJB
    private RegistroHelper registroHelper;
    @EJB
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB
    private TipoUnitaDocHelper tipoUnitaDocHelper;
    @EJB
    private TipoDocumentoEjb tipoDocEjb;
    @EJB
    private TipoDocumentoHelper tipoDocHelper;
    @EJB
    private TipoFascicoloEjb tipoFascicoloEjb;
    @EJB
    private CriteriRaggrHelper crHelper;
    @EJB
    private AmbientiHelper ambienteHelper;
    @EJB
    private AllineamentoEntiConvenzionatiEjb aecEjb;
    @EJB
    private StrutCache strutCache;
    @EJB
    private AllineamentoOrganizzazioniEjb allineamentoOrganizzazioniEjb;
    @EJB
    private TipoSerieEjb tipoSerieEjb;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private SacerLogEjb sacerLogEjb;
    @EJB
    private ModelliSerieHelper modelliSerieHelper;
    @EJB
    private FormatoFileDocEjb formatoFileDocEjb;
    @EJB
    private FormatoFileDocHelper formatoFileDocHelper;
    @EJB
    private FormatoFileStandardHelper formatoFileStandardHelper;
    @EJB
    private TipoStrutturaDocEjb tipoStrutDocEjb;
    @EJB
    private TipoStrutturaDocHelper tipoStrutDocHelper;
    @EJB
    private TipoRappresentazioneEjb tipoRapprEjb;
    @EJB
    private TipoRappresentazioneHelper tipoRapprHelper;
    @EJB
    private SottoStruttureEjb subStrutEjb;
    @EJB
    private SottoStruttureHelper subStrutHelper;
    @EJB
    private DatiSpecificiEjb datiSpecEjb;
    @EJB
    private DatiSpecificiHelper datiSpecHelper;
    @EJB
    private UnitaDocumentarieHelper unitaDocHelper;
    @EJB
    private AmministrazioneEjb amministrazioneEjb;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private CopiaStruttureEjb copiaStruttureEjb;
    @EJB
    private SistemaMigrazioneHelper sistemaMigrazioneHelper;
    @EJB
    private CorrispondenzePingHelper corrispondenzeHelper;
    @EJB
    private ModelliFascicoliHelper modelliFascicoliHelper;
    @EJB
    private TipoFascicoloHelper tipoFascicoloHelper;
    @EJB
    private CriteriRaggrFascicoliHelper crfHelper;

    public enum TiApparType {

        AMBIENTE, STRUT, TIPO_UNITA_DOC, PERIODO_TIPO_FASC
    }

    public enum XsdType {

        TIPO_DOC, TIPO_COMP_DOC, TIPO_UNITA_DOC
    }

    public enum TipoOper {
        INS, MOD, IMPORTA_NON_STANDARD, // IMPORTA NON STANDARD
        DUPLICA_STANDARD, // DUPLICA STANDARD
        IMPORTA_STANDARD, // IMPORTA STANDARD
        DUPLICA_NON_STANDARD, // DUPLICA NON STANDARD
        DUPLICA_REGISTRO // DUPLICA REGISTRO
    }

    private static final int NUM_CARATTERI_CODICE_STRUTTURA_NORMALIZZATO = 100;

    public byte[] getOrgStrutXml(BaseRowInterface orgStrutRowBean) throws IOException, JAXBException {

        OrgStrut struttura = struttureHelper.findById(OrgStrut.class, orgStrutRowBean.getBigDecimal("id_strut"));

        StringWriter sw = new StringWriter();

        JAXBContext jaxbCtx = JAXBContext.newInstance(OrgStrut.class);
        // Nota: al fine di evitare problemi di classloading e "override" del parser (vedi libreria Saxon-HE)
        // viene esplicitato a codice quale impementazione (xalan standard in questo caso) utilizzare
        TransformerFactory transFact = TransformerFactory
                .newInstance("org.apache.xalan.processor.TransformerFactoryImpl", null);
        Result outputResult = new StreamResult(sw);

        TransformerHandler handler = null;
        try {
            handler = ((SAXTransformerFactory) transFact).newTransformerHandler(
                    new StreamSource(this.getClass().getClassLoader().getResourceAsStream("/xsl/sortExportXml.xsl")));
            handler.setResult(outputResult);
        } catch (TransformerConfigurationException ex) {
            logger.error(ex.getMessage(), ex);
        }
        javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);

        try {
            marshaller.marshal(struttura, handler);

        } finally {
            sw.close();
        }

        XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
        String xmlFormatted = formatter.prettyPrintWithDOM3LS(sw.toString());

        return xmlFormatted.getBytes(StandardCharsets.UTF_8);
    }

    public UUID importXmlOrgStrut(String flussoXml) throws JAXBException {

        StringReader reader = new StringReader(flussoXml);

        return creaUUID(reader);

    }

    public UUID importXmlOrgStrut(File xmlFile) throws JAXBException, FileNotFoundException {

        FileInputStream fis = new FileInputStream(xmlFile);
        InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);

        return creaUUID(reader);

    }

    /* */
    private UUID creaUUID(Reader reader) throws JAXBException {

        JAXBContext jaxbCtx = JAXBContext.newInstance(OrgStrut.class);
        javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();

        OrgStrut struttura = (OrgStrut) unmarshaller.unmarshal(reader);

        UUID uuid = UUID.randomUUID();
        strutCache.setOrgStrut(uuid, struttura);

        return uuid;

    }

    public OrgStrutRowBean strutToRowBean(UUID uuid) {

        OrgStrut struttura = strutCache.getOrgStrut(uuid);
        OrgStrutRowBean strutRowBean = null;

        try {
            strutRowBean = (OrgStrutRowBean) Transform.entity2RowBean(struttura);
        } catch (Exception a) {
            logger.error(a.getMessage(), a);
        }
        return strutRowBean;
    }

    public OrgStrut getStrutFromCache(UUID uuid) {
        return strutCache.getOrgStrut(uuid);
    }

    /*
     *
     * Gestione AMBIENTI/ENTI/STRUTTURE
     *
     */
    public OrgAmbienteRowBean getOrgAmbienteRowBean(BigDecimal idAmbiente) {

        OrgAmbienteRowBean ambienteRowBean = new OrgAmbienteRowBean();
        OrgAmbiente ambiente = struttureHelper.findById(OrgAmbiente.class, idAmbiente);
        try {
            ambienteRowBean = (OrgAmbienteRowBean) Transform.entity2RowBean(ambiente);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return ambienteRowBean;
    }

    public OrgAmbienteRowBean getOrgAmbienteRowBeanByIdStrut(BigDecimal idStrut) {

        OrgAmbienteRowBean ambienteRowBean = new OrgAmbienteRowBean();
        OrgStrut struttura = struttureHelper.findById(OrgStrut.class, idStrut);
        try {
            ambienteRowBean = (OrgAmbienteRowBean) Transform.entity2RowBean(struttura.getOrgEnte().getOrgAmbiente());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return ambienteRowBean;
    }

    public OrgEnteRowBean getOrgEnteRowBean(BigDecimal idEnte) {

        OrgEnteRowBean enteRowBean = new OrgEnteRowBean();
        OrgEnte ente = struttureHelper.findById(OrgEnte.class, idEnte);
        try {
            // trasformo la lista di entity (risultante della query) in un tablebean
            enteRowBean = (OrgEnteRowBean) Transform.entity2RowBean(ente);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return enteRowBean;

    }

    private OrgStrutRowBean getOrgStrut(BigDecimal idStrut, String nmStrut, BigDecimal idEnte) {

        OrgStrutRowBean strutRowBean = null;
        OrgStrut struttura = null;
        Long idAmbienteEnteConvenz = null;
        if (idStrut == BigDecimal.ZERO && nmStrut != null) {
            struttura = struttureHelper.getOrgStrutByName(nmStrut, idEnte);
            idAmbienteEnteConvenz = struttureHelper.getIdAmbienteEnteSiamByStrut(idStrut);
        }
        if (nmStrut == null && idStrut != null && idStrut != BigDecimal.ZERO) {
            struttura = struttureHelper.findById(OrgStrut.class, idStrut);
            idAmbienteEnteConvenz = struttureHelper.getIdAmbienteEnteSiamByStrut(idStrut);
        }

        if (struttura != null) {
            try {
                // trasformo la lista di entity (risultante della query) in un tablebean
                strutRowBean = (OrgStrutRowBean) Transform.entity2RowBean(struttura);
                if (idAmbienteEnteConvenz != null) {
                    strutRowBean.setBigDecimal("id_ambiente_ente_convenz", new BigDecimal(idAmbienteEnteConvenz));
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return strutRowBean;
    }

    public OrgStrutRowBean getOrgStrutRowBean(String nmStrut, BigDecimal idEnte) {

        return getOrgStrut(BigDecimal.ZERO, nmStrut, idEnte);

    }

    public OrgStrutRowBean getOrgStrutRowBean(BigDecimal idStrut, BigDecimal idEnte) {

        return getOrgStrut(idStrut, null, idEnte);
    }

    public OrgStrutRowBean getOrgStrutRowBean(BigDecimal idStrut) {
        OrgStrut strut = struttureHelper.findById(OrgStrut.class, idStrut);
        OrgStrutRowBean strutRowBean = null;
        if (strut != null) {
            try {
                // trasformo la lista di entity (risultante della query) in un rowbean
                strutRowBean = (OrgStrutRowBean) Transform.entity2RowBean(strut);
                final OrgEnte orgEnte = strut.getOrgEnte();
                final OrgAmbiente orgAmbiente = orgEnte.getOrgAmbiente();
                strutRowBean.setBigDecimal("id_ente", new BigDecimal(orgEnte.getIdEnte()));
                strutRowBean.setString("nm_ente", orgEnte.getNmEnte());
                strutRowBean.setBigDecimal("id_ambiente", new BigDecimal(orgAmbiente.getIdAmbiente()));
                strutRowBean.setString("nm_ambiente", orgAmbiente.getNmAmbiente());
                // Se è presente l'ente convenzionato, allora ricavo anche il padre, vale a dire l'ambiente ente
                if (strutRowBean.getIdEnteConvenz() != null) {
                    strutRowBean.setBigDecimal("id_ambiente_ente_convenz",
                            new BigDecimal(struttureHelper.getIdAmbienteEnteSiamByStrut(idStrut)));
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new IllegalStateException("Impossibile recuperare la struttura richiesta");
            }
        }
        return strutRowBean;
    }

    public OrgStrutTableBean getOrgStrutTableBean(String nmStrut, BigDecimal idEnte, BigDecimal idAmbiente,
            Boolean isTemplate) {
        OrgStrutTableBean strutTableBean = new OrgStrutTableBean();
        List<OrgStrut> strutList = struttureHelper.retrieveOrgStrutList(nmStrut, idEnte, idAmbiente, isTemplate);

        try {
            if (!strutList.isEmpty()) {
                strutTableBean = (OrgStrutTableBean) Transform.entities2TableBean(strutList);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return strutTableBean;
    }

    public OrgStrutTableBean getOrgStrutTableBean(long idUtente, Set<BigDecimal> idEntiSet) {
        OrgStrutTableBean strutTableBean = new OrgStrutTableBean();
        List<OrgStrut> strutList = struttureHelper.retrieveOrgStrutList(idUtente, idEntiSet, null);
        try {
            if (!strutList.isEmpty()) {
                strutTableBean = (OrgStrutTableBean) Transform.entities2TableBean(strutList);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return strutTableBean;
    }

    public OrgStrutTableBean getOrgStrutTableBean(long idUtente, List<BigDecimal> idEntiList,
            List<BigDecimal> idCategStrutList) {
        OrgStrutTableBean strutTableBean = new OrgStrutTableBean();
        List<OrgStrut> strutList = struttureHelper.retrieveOrgStrutList(idUtente, idEntiList, idCategStrutList);
        try {
            if (!strutList.isEmpty()) {
                strutTableBean = (OrgStrutTableBean) Transform.entities2TableBean(strutList);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return strutTableBean;
    }

    public OrgStrutTableBean getOrgStrutTableBean(long idUtente, BigDecimal idEnte, Boolean filterValid) {
        OrgStrutTableBean strutTableBean = new OrgStrutTableBean();
        List<OrgStrut> strutList = struttureHelper.retrieveOrgStrutList(idUtente, idEnte, filterValid);
        try {
            if (!strutList.isEmpty()) {
                strutTableBean = (OrgStrutTableBean) Transform.entities2TableBean(strutList);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return strutTableBean;
    }

    //
    public OrgVRicStrutTableBean getOrgVRicStrutTableBean(String nmStrut, BigDecimal idEnte, BigDecimal idAmbiente,
            Boolean isTemplate, String partizionata, String nmSistemaVersante, BigDecimal idAmbitoTerrit,
            BigDecimal idCategEnte, BigDecimal idAmbienteEnteConvenz, BigDecimal idEnteConvenz,
            String flParametriSpecifici, long idUtente) {

        OrgVRicStrutTableBean strutTableBean = new OrgVRicStrutTableBean();

        List<OrgVRicStrut> strutList = struttureHelper.retrieveOrgVRicStrutList(nmStrut, idEnte, idAmbiente, isTemplate,
                partizionata, nmSistemaVersante, idAmbitoTerrit, idCategEnte, idAmbienteEnteConvenz, idEnteConvenz,
                flParametriSpecifici, idUtente);

        try {
            if (!strutList.isEmpty()) {
                for (OrgVRicStrut strut : strutList) {
                    OrgVRicStrutRowBean strutRow = (OrgVRicStrutRowBean) Transform.entity2RowBean(strut);
                    strutRow.setString("nm_ente", strut.getNmEnte() + " (" + (strut.getNmAmbiente()) + ")");
                    strutTableBean.add().copyFromBaseRow(strutRow);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return strutTableBean;
    }

    public CopiaStruttureEjb.OrgStrutCopyResult insertOrgStrutImp(LogParam param, OrgStrutRowBean strutRowBean,
            UUID uuid, SalvaStrutturaDto salva, AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura, AplParamApplicTableBean parametriGestioneStruttura)
            throws ParerUserError {
        StruttureEjb me = context.getBusinessObject(StruttureEjb.class);

        // IMPORTA NON STANDARD
        CopiaStruttureEjb.OrgStrutCopyResult result = me.otherTypeSaveStruttura(param, uuid, null, strutRowBean,
                TipoOper.IMPORTA_NON_STANDARD, salva, parametriAmministrazioneStruttura,
                parametriConservazioneStruttura, parametriGestioneStruttura);
        IamOrganizDaReplic replic = result.getIamOrganizDaReplic();
        String errorMessageTipiSerie = "TIPISERIE;";

        // Eseguo la creazione dei tipi serie standard, se possibile
        List<DecRegistroUnitaDoc> registri = registroHelper
                .retrieveDecRegistroUnitaDocList(replic.getIdOrganizApplic().longValue(), false);
        for (DecRegistroUnitaDoc registro : registri) {
            for (DecTipoUnitaDocAmmesso associazione : registro.getDecTipoUnitaDocAmmessos()) {
                try {
                    BigDecimal idRegistroUnitaDoc = new BigDecimal(registro.getIdRegistroUnitaDoc());
                    BigDecimal idTipoUnitaDoc = new BigDecimal(associazione.getDecTipoUnitaDoc().getIdTipoUnitaDoc());
                    tipoSerieEjb.createTipoSerieStandardDaRegistroOTipoUdNewTx(param, idRegistroUnitaDoc,
                            idTipoUnitaDoc);
                } catch (ParerUserError e) {
                    if (!errorMessageTipiSerie.contains(e.getDescription())) {
                        errorMessageTipiSerie = errorMessageTipiSerie + e.getDescription() + ";";
                    }
                }
            }
        }
        // Aggiorno i servizi di conservazione e attivazione presenti sui tipi ud della struttura considerata
        updateTipiServizioOnTipiUdInStruttura(replic.getIdOrganizApplic());
        struttureHelper.getEntityManager().flush();
        // Log applicativo
        sacerLogEjb.logInNewTx(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_STRUTTURA,
                new BigDecimal(replic.getIdOrganizApplic().longValueExact()), param.getNomePagina());
        me.replicateToIam(replic);
        if (!errorMessageTipiSerie.equals("TIPISERIE;")) {
            throw new ParerUserError(errorMessageTipiSerie);
        }
        return result;
    }

    public CopiaStruttureEjb.OrgStrutCopyResult copyOrgStrutRowBean(LogParam param, OrgStrutRowBean strutRowBean,
            BigDecimal idStrut, SalvaStrutturaDto salva, AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura, AplParamApplicTableBean parametriGestioneStruttura)
            throws ParerUserError {
        String errorMessageTipiSerie = "TIPISERIE;";
        StruttureEjb me = context.getBusinessObject(StruttureEjb.class);
        // DUPLICA NON STANDARD
        CopiaStruttureEjb.OrgStrutCopyResult result = me.otherTypeSaveStruttura(param, null, idStrut, strutRowBean,
                TipoOper.DUPLICA_NON_STANDARD, salva, parametriAmministrazioneStruttura,
                parametriConservazioneStruttura, parametriGestioneStruttura);
        IamOrganizDaReplic replic = result.getIamOrganizDaReplic();
        // Eseguo la creazione dei tipi serie standard, se possibile
        List<DecRegistroUnitaDoc> registri = registroHelper
                .retrieveDecRegistroUnitaDocList(replic.getIdOrganizApplic().longValue(), false);
        // Se viene lanciato il ParerUserError posso proseguire
        for (DecRegistroUnitaDoc registro : registri) {
            for (DecTipoUnitaDocAmmesso associazione : registro.getDecTipoUnitaDocAmmessos()) {
                try {
                    BigDecimal idRegistroUnitaDoc = new BigDecimal(registro.getIdRegistroUnitaDoc());
                    BigDecimal idTipoUnitaDoc = new BigDecimal(associazione.getDecTipoUnitaDoc().getIdTipoUnitaDoc());
                    tipoSerieEjb.createTipoSerieStandardDaRegistroOTipoUdNewTx(param, idRegistroUnitaDoc,
                            idTipoUnitaDoc);
                } catch (ParerUserError e) {
                    if (!errorMessageTipiSerie.contains(e.getDescription())) {
                        errorMessageTipiSerie = errorMessageTipiSerie + e.getDescription() + ";";
                    }
                }
            }
        }

        // Aggiorno i servizi di conservazione e attivazione presenti sui tipi ud della struttura considerata
        updateTipiServizioOnTipiUdInStruttura(replic.getIdOrganizApplic());

        // Log applicativo
        struttureHelper.getEntityManager().flush();
        sacerLogEjb.logInNewTx(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_STRUTTURA,
                new BigDecimal(replic.getIdOrganizApplic().longValueExact()), param.getNomePagina());
        me.replicateToIam(replic);
        if (!errorMessageTipiSerie.equals("TIPISERIE;")) {
            throw new ParerUserError(errorMessageTipiSerie);
        }
        return result;
    }

    public CopiaStruttureEjb.OrgStrutCopyResult overwriteOrgStrutImp(LogParam param, OrgStrutRowBean strutRowBean,
            UUID uuid, SalvaStrutturaDto salva, AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura, AplParamApplicTableBean parametriGestioneStruttura)
            throws ParerUserError {
        String errorMessageTipiSerie = "TIPISERIE;";
        StruttureEjb me = context.getBusinessObject(StruttureEjb.class);
        // IMPORTA STANDARD

        CopiaStruttureEjb.OrgStrutCopyResult result = me.otherTypeSaveStruttura(param, uuid, null, strutRowBean,
                TipoOper.IMPORTA_STANDARD, salva, parametriAmministrazioneStruttura, parametriConservazioneStruttura,
                parametriGestioneStruttura);
        IamOrganizDaReplic replic = result.getIamOrganizDaReplic();
        // Eseguo la creazione dei tipi serie standard, se possibile
        List<DecRegistroUnitaDoc> registri = registroHelper
                .retrieveDecRegistroUnitaDocList(replic.getIdOrganizApplic().longValue(), false);

        for (DecRegistroUnitaDoc registro : registri) {
            for (DecTipoUnitaDocAmmesso associazione : registro.getDecTipoUnitaDocAmmessos()) {
                try {
                    BigDecimal idRegistroUnitaDoc = new BigDecimal(registro.getIdRegistroUnitaDoc());
                    BigDecimal idTipoUnitaDoc = new BigDecimal(associazione.getDecTipoUnitaDoc().getIdTipoUnitaDoc());
                    tipoSerieEjb.createTipoSerieStandardDaRegistroOTipoUdNewTx(param, idRegistroUnitaDoc,
                            idTipoUnitaDoc);
                } catch (ParerUserError e) {
                    if (!errorMessageTipiSerie.contains(e.getDescription())) {
                        errorMessageTipiSerie = errorMessageTipiSerie + e.getDescription() + ";";
                    }
                }
            }
        }

        // Aggiorno i servizi di conservazione e attivazione presenti sui tipi ud della struttura considerata
        updateTipiServizioOnTipiUdInStruttura(replic.getIdOrganizApplic());

        // Log applicativo
        struttureHelper.getEntityManager().flush();
        sacerLogEjb.logInNewTx(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_STRUTTURA,
                new BigDecimal(replic.getIdOrganizApplic().longValueExact()), param.getNomePagina());

        me.replicateToIam(replic);

        if (!errorMessageTipiSerie.equals("TIPISERIE;")) {
            throw new ParerUserError(errorMessageTipiSerie);
        }
        return result;
    }

    public CopiaStruttureEjb.OrgStrutCopyResult overwriteStrut(LogParam param, BigDecimal idStrut,
            OrgStrutRowBean strutRowBean, SalvaStrutturaDto salva,
            AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura, AplParamApplicTableBean parametriGestioneStruttura)
            throws ParerUserError {
        String errorMessageTipiSerie = "TIPISERIE;";
        StruttureEjb me = context.getBusinessObject(StruttureEjb.class);
        // DUPLICA STANDARD
        CopiaStruttureEjb.OrgStrutCopyResult result = me.otherTypeSaveStruttura(param, null, idStrut, strutRowBean,
                TipoOper.DUPLICA_STANDARD, salva, parametriAmministrazioneStruttura, parametriConservazioneStruttura,
                parametriGestioneStruttura);
        IamOrganizDaReplic replic = result.getIamOrganizDaReplic();
        // Eseguo la creazione dei tipi serie standard, se possibile
        List<DecRegistroUnitaDoc> registri = registroHelper
                .retrieveDecRegistroUnitaDocList(replic.getIdOrganizApplic().longValue(), false);
        for (DecRegistroUnitaDoc registro : registri) {
            for (DecTipoUnitaDocAmmesso associazione : registro.getDecTipoUnitaDocAmmessos()) {
                try {
                    BigDecimal idRegistroUnitaDoc = new BigDecimal(registro.getIdRegistroUnitaDoc());
                    BigDecimal idTipoUnitaDoc = new BigDecimal(associazione.getDecTipoUnitaDoc().getIdTipoUnitaDoc());
                    tipoSerieEjb.createTipoSerieStandardDaRegistroOTipoUdNewTx(param, idRegistroUnitaDoc,
                            idTipoUnitaDoc);
                } catch (ParerUserError e) {
                    if (!errorMessageTipiSerie.contains(e.getDescription())) {
                        errorMessageTipiSerie = errorMessageTipiSerie + e.getDescription() + ";";
                    }
                }
            }
        }

        // Aggiorno i servizi di conservazione e attivazione presenti sui tipi ud della struttura considerata
        updateTipiServizioOnTipiUdInStruttura(replic.getIdOrganizApplic());

        // logga la strutttura verificare se corretto metterlo qui...
        struttureHelper.getEntityManager().flush();
        sacerLogEjb.logInNewTx(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_STRUTTURA,
                new BigDecimal(replic.getIdOrganizApplic().longValueExact()), param.getNomePagina());
        me.replicateToIam(replic);

        if (!errorMessageTipiSerie.equals("TIPISERIE;")) {
            throw new ParerUserError(errorMessageTipiSerie);
        }
        return result;
    }

    public void updateOrgStruttura(LogParam param, BigDecimal idStrut, OrgStrutRowBean strutRowBean)
            throws ParerUserError {
        StruttureEjb me = context.getBusinessObject(StruttureEjb.class);
        IamOrganizDaReplic replic = me.saveStruttura(param, idStrut, strutRowBean, TipoOper.MOD, false);
        if (replic != null) {
            me.replicateToIam(replic);
        }
    }

    public void insertOrgStruttura(LogParam param, OrgStrutRowBean orgStrutRowBean, boolean isStruttureTemplate)
            throws ParerUserError {
        StruttureEjb me = context.getBusinessObject(StruttureEjb.class);
        IamOrganizDaReplic replic = me.saveStruttura(param, null, orgStrutRowBean, TipoOper.INS, isStruttureTemplate);
        if (replic != null) {
            me.replicateToIam(replic);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic saveStruttura(LogParam param, BigDecimal idStrut, OrgStrutRowBean strutRowBean,
            TipoOper tipoOper, boolean isStruttureTemplate) throws ParerUserError {
        OrgStrut strut = new OrgStrut();
        OrgEnte ente;
        ApplEnum.TiOperReplic tiOper = null;
        boolean modificatiNomeDescrizioneEntePapi = false;
        if (tipoOper.name().equals(TipoOper.MOD.name())) {
            ente = struttureHelper.findById(OrgEnte.class, strutRowBean.getIdEnte());
            strut = struttureHelper.findById(OrgStrut.class, idStrut);

            BigDecimal idCategStrut = strutRowBean.getIdCategStrut();

            OrgCategStrut categStrut = null;

            // Se la struttura di un certo ente che sto modificando è già presente su DB (ovviamente controllo solo se
            // il nome è cambiato...)
            if (struttureHelper.getOrgStrutByName(strutRowBean.getNmStrut(), strutRowBean.getIdEnte()) != null
                    && !strut.getNmStrut().equals(strutRowBean.getNmStrut())) {
                throw new ParerUserError("Struttura gi\u00E0 associata a questo ente all'interno del database</br>");
            }

            // Se la struttura NON è template
            if (!strut.getFlTemplate().equals("1")) {
                if (strutRowBean.getCdStrutNormaliz() == null || struttureHelper
                        .existsCdStrutNormaliz(strutRowBean.getCdStrutNormaliz(), strutRowBean.getIdEnte(), idStrut)) {
                    throw new ParerUserError(
                            "Il nome normalizzato della struttura non è stato indicato o non è univoco</br>");
                }
            }

            /* Controllo se sono stati modificati nome e/o descrizione e/o ente padre */
            if (!strut.getNmStrut().equals(strutRowBean.getNmStrut())
                    || !strut.getDsStrut().equals(strutRowBean.getDsStrut())
                    || strut.getOrgEnte().getIdEnte() != strutRowBean.getIdEnte().longValue()) {
                modificatiNomeDescrizioneEntePapi = true;
            }

            strut = (OrgStrut) Transform.rowBean2Entity(strutRowBean);
            strut.setIdStrut(idStrut.longValue());
            strut.setOrgEnte(ente);
            if (idCategStrut != null) {
                categStrut = struttureHelper.findById(OrgCategStrut.class, idCategStrut);
                strut.setOrgCategStrut(categStrut);
            }

            strutRowBean.setIdStrut(BigDecimal.valueOf(strut.getIdStrut()));

            struttureHelper.updateOrgStrut(strut);

            if (!isStruttureTemplate) {
                struttureHelper.getEntityManager().flush();
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_STRUTTURA,
                        new BigDecimal(strut.getIdStrut()), param.getNomePagina());
            }

            tiOper = ApplEnum.TiOperReplic.MOD;
        } else if (tipoOper.name().equals(TipoOper.INS.name())) {
            if (struttureHelper.getOrgStrutByName(strutRowBean.getNmStrut(), strutRowBean.getIdEnte()) != null) {
                throw new ParerUserError("Struttura gi\u00E0 associata a questo ente all'interno del database</br>");
            }

            // Se la struttura NON è template
            if (!strutRowBean.getFlTemplate().equals("1")) {
                if (strutRowBean.getCdStrutNormaliz() == null || struttureHelper
                        .existsCdStrutNormaliz(strutRowBean.getCdStrutNormaliz(), strutRowBean.getIdEnte(), null)) {
                    throw new ParerUserError(
                            "Il nome normalizzato della struttura non è stato indicato o non è univoco</br>");
                }
            }

            ente = struttureHelper.findById(OrgEnte.class, strutRowBean.getIdEnte());
            if (ente.getOrgStruts() == null) {
                ente.setOrgStruts(new ArrayList<>());
            }

            strut = (OrgStrut) Transform.rowBean2Entity(strutRowBean);
            strut.setOrgEnte(ente);

            struttureHelper.insertEntity(strut, true);

            strutRowBean.setIdStrut(BigDecimal.valueOf(strut.getIdStrut()));
            ente.getOrgStruts().add(strut);
            tiOper = ApplEnum.TiOperReplic.INS;

            controlloAccordoEnteConvenzionatoSuInsStruttura(strutRowBean);
            /*
             * Creo un nuovo record sotto struttura di default
             */
            saveDefaultSubStrut(strut, "1".equals(strutRowBean.getFlTemplate()));
            modificatiNomeDescrizioneEntePapi = true;
            if (!isStruttureTemplate) {
                struttureHelper.getEntityManager().flush();
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_STRUTTURA,
                        new BigDecimal(strut.getIdStrut()), param.getNomePagina());
            }

            if (!strutRowBean.getFlTemplate().equals("1")) {
                // Aggiungo tutti i formati a livello di struttura
                formatoFileDocHelper.insertDecFormatoFileDocStrutturaSpecificaNative(strut.getIdStrut());
            }
        }

        IamOrganizDaReplic replic = null;
        if (modificatiNomeDescrizioneEntePapi) {
            replic = context.getBusinessObject(StruttureEjb.class).insertStrutIamOrganizDaReplic(strut, tiOper);
        }
        return replic;
    }

    /**
     * Controlli sul salvataggio associazione struttura/ente convenzionato in fase di inserimento/importa/duplica
     * struttura come riportato in MAC #26960 (primo punto riferito all'inserimento di una nuova struttura)
     *
     * @param strutRowBean
     * @param ente
     *
     * @throws ParerUserError
     */
    private void controlloAccordoEnteConvenzionatoSuInsStruttura(OrgStrutRowBean strutRowBean) throws ParerUserError {
        if (strutRowBean.getIdEnteConvenz() != null) {
            /* Controllo sull'ente convenzionato scelto */
            OrgVRicEnteConvenzByEsterno ricEnteConvenz = struttureHelper
                    .findOrgVRicEnteConvenzByEsternoByEnte(strutRowBean.getIdEnteConvenz());
            if (ricEnteConvenz.getIdEnteGestore() == null) {
                throw new ParerUserError("Controllare l'accordo dell'ente convenzionato</br>");
            }

            // Controllo l'intervallo dell'associazione rientri nell'intervallo di decorrenza accordo valido e fine
            // validità ente siam
            if (!struttureHelper.existsIntervalloValiditaPerAssociazione(strutRowBean.getIdEnteConvenz(),
                    strutRowBean.getDtIniVal(), strutRowBean.getDtFineVal())) {
                throw new ParerUserError(
                        "L’intervallo di validità dell'associazione non rientra nell'intervallo compreso tra "
                                + "la data di decorrenza accordo valido e la data di fine validità dell'ente");
            }
        }
    }

    // MEV#20462
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void cessazioneStruttura(LogParam param, long idStrut) throws ParerUserError {
        OrgStrut struttura = struttureHelper.findById(OrgStrut.class, idStrut);

        if (struttura.getIdEnteConvenz() != null) {
            // Controllo che l’ente siam cui la struttura appartiene alla data corrente NON abbia accordi validi alla
            // data corrente
            if (struttureHelper.checkEsistenzaAccordoValidoEnteConvenzStrutVers(struttura.getIdEnteConvenz())) {
                throw new ParerUserError(
                        "E’ possibile cessare la struttura solo se il suo ente di appartenenza non ha un accordo valido");
            }

            // Controllo che la struttura abbia fl_archivio_restituito = 1
            if ("0".equals(struttura.getFlArchivioRestituito())) {
                throw new ParerUserError(
                        "E’ possibile cessare la struttura solo dopo aver eseguito la restituzione dell’archivio</br>");
            }
        }

        Date dataCorrente = new Date();
        struttura.setDtFineValStrut(dataCorrente);
        struttura.setFlCessato("1");
        struttureHelper.getEntityManager().merge(struttura);
        struttureHelper.getEntityManager().flush();

        if (!struttureHelper.containsStrutEnteNonCessata(new BigDecimal(idStrut))) {
            struttura.getOrgEnte().setDtFineVal(dataCorrente);
            struttura.getOrgEnte().setFlCessato("1");
        }

        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_STRUTTURA, new BigDecimal(idStrut),
                param.getNomePagina());

        logger.info("Cessazione della struttura {} avvenuta con successo!", idStrut);
    }
    // end MEV#20462

    public void saveParametriStruttura(LogParam param, AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura, AplParamApplicTableBean parametriGestioneStruttura,
            BigDecimal idStrut) {
        OrgStrut strut = struttureHelper.findById(OrgStrut.class, idStrut);
        gestioneParametriStruttura(parametriAmministrazioneStruttura, parametriConservazioneStruttura,
                parametriGestioneStruttura, strut);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_STRUTTURA, new BigDecimal(strut.getIdStrut()),
                param.getNomePagina());
    }

    private void gestioneParametriStruttura(AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura, AplParamApplicTableBean parametriGestioneStruttura,
            OrgStrut strut) {
        // Gestione parametri amministrazione
        manageParametriPerStruttura(parametriAmministrazioneStruttura, "ds_valore_param_applic_strut_amm", strut);
        // Gestione parametri conservazione
        manageParametriPerStruttura(parametriConservazioneStruttura, "ds_valore_param_applic_strut_cons", strut);
        // Gestione parametri gestione
        manageParametriPerStruttura(parametriGestioneStruttura, "ds_valore_param_applic_strut_gest", strut);
    }

    private void manageParametriPerStruttura(AplParamApplicTableBean paramApplicTableBean,
            String nomeCampoValoreParamApplic, OrgStrut strut) {
        for (AplParamApplicRowBean paramApplicRowBean : paramApplicTableBean) {
            // Cancello il parametro se eliminato
            if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") != null
                    && (paramApplicRowBean.getString(nomeCampoValoreParamApplic) == null
                            || paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals(""))) {
                AplValoreParamApplic parametro = ambienteHelper.findById(AplValoreParamApplic.class,
                        paramApplicRowBean.getBigDecimal("id_valore_param_applic"));
                ambienteHelper.removeEntity(parametro, true);
            } // Modifico il parametro se modificato
            else if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") != null
                    && paramApplicRowBean.getString(nomeCampoValoreParamApplic) != null
                    && !paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals("")) {
                AplValoreParamApplic parametro = ambienteHelper.findById(AplValoreParamApplic.class,
                        paramApplicRowBean.getBigDecimal("id_valore_param_applic"));
                parametro.setDsValoreParamApplic(paramApplicRowBean.getString(nomeCampoValoreParamApplic));
            } // Inserisco il parametro se nuovo
            else if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") == null
                    && paramApplicRowBean.getString(nomeCampoValoreParamApplic) != null
                    && !paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals("")) {
                amministrazioneEjb.insertAplValoreParamApplic(null, strut, null, null,
                        paramApplicRowBean.getBigDecimal("id_param_applic"), "STRUT",
                        paramApplicRowBean.getString(nomeCampoValoreParamApplic));
            }
        }
    }

    public void saveDefaultSubStrut(OrgStrut strut, boolean isTemplate) {
        OrgSubStrut subStrut = new OrgSubStrut();
        subStrut.setOrgStrut(strut);
        subStrut.setNmSubStrut(CostantiDB.SubStruttura.DEFAULT_NAME);
        subStrut.setDsSubStrut(isTemplate ? CostantiDB.SubStruttura.DEFAULT_TEMPLATE_DESC
                : CostantiDB.SubStruttura.DEFAULT_DESC + strut.getNmStrut() + " - " + strut.getDsStrut());
        struttureHelper.insertEntity(subStrut, true);
        strut.getOrgSubStruts().add(subStrut);
    }

    public long insertOrgEnteConvezOrg(BigDecimal idStrut, BigDecimal idEnteConvenz, Date dtIniVal, Date dtFineVal) {
        SIOrgEnteConvenzOrg enteConvenzOrg = new SIOrgEnteConvenzOrg();
        SIOrgEnteSiam enteConvenz = ambienteHelper.findById(SIOrgEnteSiam.class, idEnteConvenz);
        SIUsrOrganizIam organizIam = ambienteHelper.getSIUsrOrganizIam(idStrut);
        enteConvenzOrg.setSiOrgEnteConvenz(enteConvenz);
        enteConvenzOrg.setSiUsrOrganizIam(organizIam);
        enteConvenzOrg.setDtIniVal(dtIniVal);
        enteConvenzOrg.setDtFineVal(dtFineVal);
        ambienteHelper.insertEntity(enteConvenzOrg, true);
        return enteConvenzOrg.getIdEnteConvenzOrg();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CopiaStruttureEjb.OrgStrutCopyResult otherTypeSaveStruttura(LogParam param, UUID uuid, BigDecimal idStrut,
            OrgStrutRowBean strutRowBean, TipoOper tipoOper, SalvaStrutturaDto salva,
            AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura, AplParamApplicTableBean parametriGestioneStruttura)
            throws ParerUserError {
        CopiaStruttureEjb.OrgStrutCopyResult result = null;
        salva.setDataAttuale(new Date());
        OrgStrut strut = null;
        ApplEnum.TiOperReplic tiOper = null;
        if (tipoOper.name().equals(TipoOper.DUPLICA_STANDARD.name())) {
            // <editor-fold defaultstate="collapsed" desc="DUPLICA STANDARD">
            OrgStrut strutturaDaCopiare = struttureHelper.findById(OrgStrut.class, idStrut);
            if (!subStrutEjb.checkDefaultOrgSubStrut(idStrut)) {
                throw new ParerUserError(
                        "La struttura da duplicare non presenta un'unica sottostruttura DEFAULT: \u00E8 necessario eseguire la creazione in modalit\u00E0 NON standard");
            }
            result = gestisciImpDupStandard(param, strutturaDaCopiare, strutRowBean, salva, false,
                    parametriAmministrazioneStruttura, parametriConservazioneStruttura, parametriGestioneStruttura);
            strut = result.getOrgStrut();
            tiOper = ApplEnum.TiOperReplic.MOD;

            // Calcolo servizi erogati
            boolean esitoOK = calcoloServiziErogati(strut.getIdEnteConvenz());
            if (!esitoOK) {
                throw new ParerUserError(
                        "Errore durante il calcolo dei servizi erogati a seguito di duplica standard</br>");
            }
            /*
             * Eseguo la copia dei record in orgCampoValSubStrut manualmente Per farlo devo eseguire le seguenti
             * operazioni nella copia xml: - scorrere i tipiUd - per ogni tipoUd, scorrermi ogni regola - per ogni
             * regola, scorrere i campi e copiarli, ricercando la regola in base ai suoi parametri su db
             *
             * Per copiare i campi, bisogna eseguire le seguenti operazioni: In base al tipo campo: + SUB_STRUTTURA:
             * riprendo dalla struttura nuova la sottostruttura creata (l'unica) e l'associo al nuovo campo + DATI_SPEC
             * : ricerco in base ai dati il nuovo record di DecAttribDatiSpec e glielo associo + DATO_PROFILO : non ha
             * bisogno di altre ricerche
             */
            OrgStrut duplicante = struttureHelper.findById(OrgStrut.class, idStrut);
            for (DecTipoUnitaDoc tipoUd : duplicante.getDecTipoUnitaDocs()) {
                for (OrgRegolaValSubStrut regola : tipoUd.getOrgRegolaValSubStruts()) {
                    OrgRegolaValSubStrut regolaDb = subStrutHelper.getOrgRegolaSubStrut(strut.getIdStrut(),
                            regola.getDecTipoUnitaDoc().getNmTipoUnitaDoc(), regola.getDecTipoDoc().getNmTipoDoc(),
                            regola.getDtIstituz(), regola.getDtSoppres());
                    if (regolaDb != null) {
                        for (OrgCampoValSubStrut campo : regola.getOrgCampoValSubStruts()) {
                            OrgCampoValSubStrut copiaCampo = new OrgCampoValSubStrut();
                            CostantiDB.TipoCampo campoEnum = CostantiDB.TipoCampo.valueOf(campo.getTiCampo());
                            copiaCampo.setNmCampo(campo.getNmCampo());
                            copiaCampo.setTiCampo(campo.getTiCampo());
                            copiaCampo.setOrgRegolaValSubStrut(regolaDb);
                            DecAttribDatiSpec attrib;
                            DecAttribDatiSpec attribDb;
                            switch (campoEnum) {
                            case DATO_PROFILO:
                                break;
                            case DATO_SPEC_DOC_PRINC:
                                attrib = campo.getDecAttribDatiSpec();
                                try {
                                    attribDb = datiSpecHelper.getDecAttribDatiSpecUniDocAndDoc(strut.getIdStrut(),
                                            attrib.getNmAttribDatiSpec(), attrib.getTiEntitaSacer(),
                                            attrib.getTiUsoAttrib(), null, attrib.getDecTipoDoc().getNmTipoDoc(), null);
                                    copiaCampo.setDecAttribDatiSpec(attribDb);
                                } catch (Exception e) {
                                    throw new ParerUserError(
                                            "Si \u00E8 verificato un errore nella procedura di copia. </br>");
                                }
                                break;
                            case DATO_SPEC_UNI_DOC:
                                attrib = campo.getDecAttribDatiSpec();
                                try {
                                    attribDb = datiSpecHelper.getDecAttribDatiSpecUniDocAndDoc(strut.getIdStrut(),
                                            attrib.getNmAttribDatiSpec(), attrib.getTiEntitaSacer(),
                                            attrib.getTiUsoAttrib(), attrib.getDecTipoUnitaDoc().getNmTipoUnitaDoc(),
                                            null, null);
                                    copiaCampo.setDecAttribDatiSpec(attribDb);
                                } catch (Exception e) {
                                    throw new ParerUserError(
                                            "Si \u00E8 verificato un errore nella procedura di copia. </br>");
                                }
                                break;
                            case SUB_STRUT:
                                List<OrgSubStrut> subStruts = subStrutHelper.getOrgSubStrut(
                                        campo.getOrgSubStrut().getNmSubStrut(), new BigDecimal(strut.getIdStrut()));
                                if (subStruts.isEmpty()) {
                                    throw new ParerUserError(
                                            "Si \u00E8 verificato un errore nella procedura di copia. </br>");
                                } else {
                                    copiaCampo.setOrgSubStrut(subStruts.get(0));
                                }
                                break;
                            }
                            regolaDb.getOrgCampoValSubStruts().add(copiaCampo);
                        }
                    }
                }
            }
            // </editor-fold>
        } else if (tipoOper.name().equals(TipoOper.IMPORTA_STANDARD.name())) {
            // <editor-fold defaultstate="collapsed" desc="IMPORTA STANDARD">
            OrgStrut struttura = strutCache.getOrgStrut(uuid);
            /* CONTROLLI PRELIMINARI */
            if (struttureHelper.getOrgStrutByName(strutRowBean.getNmStrut(),
                    strutRowBean.getBigDecimal("id_ente_rif")) != null) {
                throw new ParerUserError(
                        "Nome struttura gi\u00E0 presente all'interno del database. Operazione cancellata");
            }
            /* INIZIO SALVATAGGIO IMPORTA STRUTTURA STANDARD */
            BigDecimal idAmbiente = strutRowBean.getBigDecimal("id_ambiente_rif");
            BigDecimal idEnte = strutRowBean.getBigDecimal("id_ente_rif");
            OrgStrut template = new OrgStrut();
            // Ricavo l'ente selezionato ed eseguo le verifiche
            OrgEnte orgEnte = struttureHelper.findById(OrgEnte.class, idEnte);
            // Verifico se questo ente, che deve avere strutture template appartenenti ad esso, effettivamente ne ha
            if (orgEnte.getTipoDefTemplateEnte().equals(CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_ENTE.name())) {
                template = struttureHelper.getFirtsOrgStrutTemplatePerEntePartizionata(idEnte);
                if (template == null) {
                    throw new ParerUserError(
                            "Nell\u0027 ente " + orgEnte.getNmEnte() + " non sono presenti strutture template");
                }
            } // Questo ente non è un template, di conseguenza non può avere al suo cospetto strutture template.
              // Allora verifico se l'ambiente di cui fa parte, ha altri enti con strutture template
            else if (orgEnte.getTipoDefTemplateEnte().equals(CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name())) {
                template = struttureHelper.getFirstOrgStrutTemplatePerAmbienteAndTipoDefTemplateEntePartizionata(
                        idAmbiente, CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_AMBIENTE.name());
                if (template == null) {
                    String nmAmbiente = (struttureHelper.findById(OrgAmbiente.class, idAmbiente)).getNmAmbiente();
                    throw new ParerUserError(
                            "Nell\u0027 ambiente " + nmAmbiente + " non sono presenti strutture template");
                }
            } else // Controllo se il flag di "Struttura Template" è uguale a 0, in caso segnalo errore
            {
                if (strutRowBean.getFlTemplate().equals("0")) {
                    throw new ParerUserError("L'ente template può contenere solo strutture di tipo template");
                }
            }
            if (!subStrutEjb.checkDefaultOrgSubStrut(struttura)) {
                throw new ParerUserError(
                        "La struttura non contiene solamente la sottostruttura di default. \u00C8 necessario eseguire la creazione della nuova struttura in modalit\u00E0 NON STANDARD.");
            }
            // cancello tutte le tabelle associate al template se presenti
            struttureHelper.deleteOrgStrutRelations(template);
            struttureHelper.getEntityManager().flush();
            // Nuovo codice a fattor comune con l'importa standard/non standard
            result = impostaDatiStrutturaImpStd(struttura, template, orgEnte, strutRowBean, salva, true, true,
                    parametriAmministrazioneStruttura, parametriConservazioneStruttura, parametriGestioneStruttura);
            strut = result.getOrgStrut();
            strut.setFlTemplate("0");
            struttureHelper.getEntityManager().flush();
            strutRowBean.setIdStrut(new BigDecimal(strut.getIdStrut()));

            OrgStrutRowBean appo = getOrgStrut(new BigDecimal(strut.getIdStrut()), null,
                    new BigDecimal(orgEnte.getIdEnte()));
            strutRowBean.copyFromBaseRow(appo);

            /*
             * Eseguo la copia dei record in orgCampoValSubStrut manualmente Per farlo devo eseguire le seguenti
             * operazioni nella copia xml: - scorrere i tipiUd - per ogni tipoUd, scorrermi ogni regola - per ogni
             * regola, scorrere i campi e copiarli, ricercando la regola in base ai suoi parametri su db
             *
             * Per copiare i campi, bisogna eseguire le seguenti operazioni: In base al tipo campo: + SUB_STRUTTURA:
             * riprendo dalla struttura nuova la sottostruttura creata (l'unica) e l'associo al nuovo campo + DATI_SPEC
             * : ricerco in base ai dati il nuovo record di DecAttribDatiSpec e glielo associo + DATO_PROFILO : non ha
             * bisogno di altre ricerche
             */
            for (DecTipoUnitaDoc tipoUd : struttura.getDecTipoUnitaDocs()) {
                for (OrgRegolaValSubStrut regola : tipoUd.getOrgRegolaValSubStruts()) {
                    EntitaValida entita = new EntitaValida(salva.isCheckIncludiElementiDisattivi(),
                            salva.isCheckMantieniDateFineValidita(), regola.getDtIstituz(), regola.getDtSoppres(),
                            salva.getDataAttuale());
                    if (entita.isValida()) {
                        OrgRegolaValSubStrut regolaDb = subStrutHelper.getOrgRegolaSubStrut(strut.getIdStrut(),
                                regola.getDecTipoUnitaDoc().getNmTipoUnitaDoc(), regola.getDecTipoDoc().getNmTipoDoc(),
                                entita.getDataInizio(), entita.getDataFine());
                        if (regolaDb != null) {
                            for (OrgCampoValSubStrut campo : regola.getOrgCampoValSubStruts()) {
                                OrgCampoValSubStrut copiaCampo = new OrgCampoValSubStrut();
                                CostantiDB.TipoCampo campoEnum = CostantiDB.TipoCampo.valueOf(campo.getTiCampo());
                                copiaCampo.setNmCampo(campo.getNmCampo());
                                copiaCampo.setTiCampo(campo.getTiCampo());
                                copiaCampo.setOrgRegolaValSubStrut(regolaDb);
                                DecAttribDatiSpec attrib;
                                DecAttribDatiSpec attribDb;
                                switch (campoEnum) {
                                case DATO_PROFILO:
                                    break;
                                case DATO_SPEC_DOC_PRINC:
                                    attrib = campo.getDecAttribDatiSpec();
                                    try {
                                        attribDb = datiSpecHelper.getDecAttribDatiSpecUniDocAndDoc(strut.getIdStrut(),
                                                attrib.getNmAttribDatiSpec(), attrib.getTiEntitaSacer(),
                                                attrib.getTiUsoAttrib(), null, attrib.getDecTipoDoc().getNmTipoDoc(),
                                                null);
                                        copiaCampo.setDecAttribDatiSpec(attribDb);
                                    } catch (Exception e) {
                                        throw new ParerUserError(
                                                "Si \u00E8 verificato un errore nella procedura di copia. </br>");
                                    }
                                    break;
                                case DATO_SPEC_UNI_DOC:
                                    attrib = campo.getDecAttribDatiSpec();
                                    try {
                                        attribDb = datiSpecHelper.getDecAttribDatiSpecUniDocAndDoc(strut.getIdStrut(),
                                                attrib.getNmAttribDatiSpec(), attrib.getTiEntitaSacer(),
                                                attrib.getTiUsoAttrib(),
                                                attrib.getDecTipoUnitaDoc().getNmTipoUnitaDoc(), null, null);
                                        copiaCampo.setDecAttribDatiSpec(attribDb);
                                    } catch (Exception e) {
                                        throw new ParerUserError(
                                                "Si \u00E8 verificato un errore nella procedura di copia. </br>");
                                    }
                                    break;
                                case SUB_STRUT:
                                    List<OrgSubStrut> subStruts = subStrutHelper.getOrgSubStrut(
                                            campo.getOrgSubStrut().getNmSubStrut(), new BigDecimal(strut.getIdStrut()));
                                    if (subStruts.isEmpty()) {
                                        throw new ParerUserError(
                                                "Si \u00E8 verificato un errore nella procedura di copia. </br>");
                                    } else {
                                        copiaCampo.setOrgSubStrut(subStruts.get(0));
                                    }
                                    break;
                                }
                                regolaDb.getOrgCampoValSubStruts().add(copiaCampo);
                            }
                        }
                    }
                }
            }
            strutCache.removeOrgStrut(uuid);
            tiOper = ApplEnum.TiOperReplic.MOD;
            // </editor-fold>
        } else if (tipoOper.name().equals(TipoOper.DUPLICA_NON_STANDARD.name())) {
            // <editor-fold defaultstate="collapsed" desc="DUPLICA NON STANDARD">
            /* CONTROLLI PRELIMINARI */
            if (struttureHelper.getOrgStrutByName(strutRowBean.getNmStrut(), strutRowBean.getIdEnte()) != null) {
                throw new ParerUserError("Nome Struttura gi\u00E0 associato ad Ambiente</br>");
            }
            OrgStrut struttura = struttureHelper.findById(OrgStrut.class, idStrut);
            OrgEnte newOrgEnte = struttureHelper.findById(OrgEnte.class, strutRowBean.getIdEnte());

            // INIZIO COPIA della struttura
            result = impostaDatiStruttura(struttura, new OrgStrut(), newOrgEnte, strutRowBean, salva, false, false,
                    parametriAmministrazioneStruttura, parametriConservazioneStruttura, parametriGestioneStruttura,
                    struttura.getFlTemplate());
            strut = result.getOrgStrut();

            struttureHelper.getEntityManager().flush();

            // Calcolo servizi erogati
            boolean esitoOK = calcoloServiziErogati(strut.getIdEnteConvenz());
            if (!esitoOK) {
                throw new ParerUserError(
                        "Errore durante il calcolo dei servizi erogati a seguito di duplica non standard</br>");
            }

            long idNewStrut = strut.getIdStrut();
            strutRowBean.setIdStrut(new BigDecimal(idNewStrut));
            loggaOggettiStruttura(param, strut);
            tiOper = ApplEnum.TiOperReplic.INS;
            // </editor-fold>
        } else if (tipoOper.name().equals(TipoOper.IMPORTA_NON_STANDARD.name())) {
            // <editor-fold defaultstate="collapsed" desc="IMPORTA NON STANDARD">
            /* CONTROLLI PRELIMINARI */
            OrgStrut struttura = strutCache.getOrgStrut(uuid);
            if (struttureHelper.getOrgStrutByName(strutRowBean.getNmStrut(), strutRowBean.getIdEnte()) != null) {
                throw new ParerUserError("Struttura gi\u00E0 associata a questo ente all'interno del database</br>");
            }
            OrgEnte orgEnte = struttureHelper.findById(OrgEnte.class, strutRowBean.getIdEnte());
            // controllo che siano presenti nel DB tutte le categorie tipi unita doc che devo associare alla struttura
            // che importo
            if (struttura.getDecTipoUnitaDocs() != null) {
                for (DecTipoUnitaDoc tipoUd : struttura.getDecTipoUnitaDocs()) {
                    DecCategTipoUnitaDoc categ;
                    if ((categ = tipoUnitaDocHelper.getDecCategTipoUnitaDocByCode(
                            tipoUd.getDecCategTipoUnitaDoc().getCdCategTipoUnitaDoc())) != null) {
                        tipoUd.setDecCategTipoUnitaDoc(categ);
                    } else {
                        throw new ParerUserError("La categoria della tipologia unit\u00E0 documentaria "
                                + tipoUd.getDecCategTipoUnitaDoc().getCdCategTipoUnitaDoc()
                                + " non \u00E8 presente nel database.");
                    }
                }
            }
            // Nuovo codice a fattor comune con l'importa standard/non standard
            result = impostaDatiStruttura(struttura, new OrgStrut(), orgEnte, strutRowBean, salva, true, false,
                    parametriAmministrazioneStruttura, parametriConservazioneStruttura, parametriGestioneStruttura,
                    "0");
            strut = result.getOrgStrut();
            struttureHelper.getEntityManager().flush();

            loggaOggettiStruttura(param, strut);
            strutRowBean.setIdStrut(new BigDecimal(strut.getIdStrut()));

            strutCache.removeOrgStrut(uuid);
            tiOper = ApplEnum.TiOperReplic.INS;
            // </editor-fold>
        }

        /* SCRIVO NEL LOG E REPLICO */
        IamOrganizDaReplic replic = context.getBusinessObject(StruttureEjb.class).insertStrutIamOrganizDaReplic(strut,
                tiOper);
        result.setIamOrganizDaReplic(replic);
        return result;
    }

    private CopiaStruttureEjb.OrgStrutCopyResult gestisciImpDupStandard(LogParam param, OrgStrut strutturaDaCopiare,
            OrgStrutRowBean strutRowBean, SalvaStrutturaDto salva, boolean isImport,
            AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura, AplParamApplicTableBean parametriGestioneStruttura)
            throws ParerUserError {
        CopiaStruttureEjb.OrgStrutCopyResult result = null;
        /* CONTROLLI PRELIMINARI */
        if (struttureHelper.getOrgStrutByName(strutRowBean.getNmStrut(),
                strutRowBean.getBigDecimal("id_ente_rif")) != null) {
            throw new ParerUserError(
                    "Nome struttura gi\u00E0 presente all'interno del database. Operazione cancellata");
        }
        /* INIZIO SALVATAGGIO DUPLICA STRUTTURA STANDARD */
        BigDecimal idAmbiente = strutRowBean.getBigDecimal("id_ambiente_rif");
        BigDecimal idEnte = strutRowBean.getBigDecimal("id_ente_rif");
        OrgStrut template;

        // Ricavo l'ente selezionato ed eseguo le verifiche
        OrgEnte ente = struttureHelper.findById(OrgEnte.class, idEnte);
        // Verifico se questo ente, che deve avere strutture template appartenenti ad esso, effettivamente ne ha
        if (ente.getTipoDefTemplateEnte().equals(CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_ENTE.name())) {
            template = struttureHelper.getFirtsOrgStrutTemplatePerEntePartizionata(idEnte);
            if (template == null) {
                throw new ParerUserError(
                        "Nell\u0027 ente " + ente.getNmEnte() + " non sono presenti strutture template");
            }
        } // Questo ente non è un template, di conseguenza non può avere al suo cospetto strutture template.
          // Allora verifico se l'ambiente di cui fa parte, ha altri enti con strutture template
        else if (ente.getTipoDefTemplateEnte().equals(CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name())) {
            template = struttureHelper.getFirstOrgStrutTemplatePerAmbienteAndTipoDefTemplateEntePartizionata(idAmbiente,
                    CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_AMBIENTE.name());
            if (template == null) {
                String nmAmbiente = (struttureHelper.findById(OrgAmbiente.class, idAmbiente)).getNmAmbiente();
                throw new ParerUserError("Nell\u0027 ambiente " + nmAmbiente + " non sono presenti strutture template");
            }
        } else {
            template = struttureHelper.getFirtsOrgStrutTemplatePerEntePartizionata(idEnte);
            if (template == null) {
                throw new ParerUserError(
                        "Nell\u0027 ente " + ente.getNmEnte() + " non sono presenti strutture template");
            }
            // Controllo se il flag di "Struttura Template" è uguale a 0, in caso segnalo errore
            if (strutRowBean.getFlTemplate().equals("0")) {
                throw new ParerUserError("L'ente template può contenere solo strutture di tipo template");
            }
        }
        // cancello tutte le tabelle associate al template se presenti
        struttureHelper.deleteOrgStrutRelations(template);
        struttureHelper.getEntityManager().flush();
        result = impostaDatiStrutturaImpStd(strutturaDaCopiare, template, ente, strutRowBean, salva, isImport, true,
                parametriAmministrazioneStruttura, parametriConservazioneStruttura, parametriGestioneStruttura);
        OrgStrut strut = result.getOrgStrut();
        strut.setFlTemplate("0");
        struttureHelper.getEntityManager().flush();
        loggaOggettiStruttura(param, strut);
        strutRowBean.setIdStrut(new BigDecimal(strut.getIdStrut()));
        OrgStrutRowBean appo = getOrgStrut(new BigDecimal(strut.getIdStrut()), null, new BigDecimal(ente.getIdEnte()));
        if (appo != null) {
            strutRowBean.setIdEnte(appo.getIdEnte());
        }
        return result;
    }

    private CopiaStruttureEjb.OrgStrutCopyResult impostaDatiStruttura(OrgStrut oldStrut, OrgStrut newStrut,
            OrgEnte orgEnte, OrgStrutRowBean strutRowBean, SalvaStrutturaDto salva, boolean isImport,
            boolean isStandard, AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura, AplParamApplicTableBean parametriGestioneStruttura,
            String flTemplate) throws ParerUserError {
        // Se la struttura NON è template
        if (!strutRowBean.getFlTemplate().equals("1")) {
            if (strutRowBean.getCdStrutNormaliz() == null || struttureHelper
                    .existsCdStrutNormaliz(strutRowBean.getCdStrutNormaliz(), strutRowBean.getIdEnte(), null)) {
                throw new ParerUserError(
                        "Il nome normalizzato della struttura non è stato indicato o non è univoco</br>");
            }
        }

        // Capire se è giusto che il controllo stia qui!!!
        controlloAccordoEnteConvenzionatoSuInsStruttura(strutRowBean);

        // NUOVO MOTORE DI COPIA
        newStrut.setNmStrut(strutRowBean.getNmStrut());
        newStrut.setDsStrut(strutRowBean.getDsStrut());
        newStrut.setDtIniVal(strutRowBean.getDtIniVal());
        newStrut.setDtFineVal(strutRowBean.getDtFineVal());
        newStrut.setDtIniValStrut(strutRowBean.getDtIniValStrut());
        newStrut.setDtFineValStrut(strutRowBean.getDtFineValStrut());
        newStrut.setCdStrutNormaliz(strutRowBean.getCdStrutNormaliz());
        newStrut.setCdIpa(strutRowBean.getCdIpa());
        newStrut.setDlNoteStrut(strutRowBean.getDlNoteStrut());
        newStrut.setFlTemplate(flTemplate);
        newStrut.setOrgEnte(orgEnte);
        if (strutRowBean.getIdCategStrut() != null) {
            newStrut.setOrgCategStrut(struttureHelper.findById(OrgCategStrut.class, strutRowBean.getIdCategStrut()));
        } else {
            newStrut.setOrgCategStrut(null);
        }

        // Aggiungo i valori riguardanti l'ente convenzionato
        newStrut.setIdEnteConvenz(strutRowBean.getIdEnteConvenz());

        newStrut = formatoFileStandardHelper.getEntityManager().merge(newStrut);
        formatoFileStandardHelper.getEntityManager().flush();

        if (salva.isCheckIncludiTipiFascicolo()) {
            // Controllo i modelli xsd periodo tipi fascicolo in base all'ambiente di questa nuova struttura
            checkAndSetModelliXsdTipiFascicoloStruttura(oldStrut,
                    new BigDecimal(orgEnte.getOrgAmbiente().getIdAmbiente()));
        }

        CopiaStruttureEjb.OrgStrutCopyResult result = copiaStruttureEjb.getOrgStrutCopyFromStrut(oldStrut, newStrut,
                salva, isStandard);
        newStrut = result.getOrgStrut();

        /* Gestione Parametri */
        gestisciParametriStruttura(newStrut, parametriAmministrazioneStruttura, result);
        gestisciParametriStruttura(newStrut, parametriConservazioneStruttura, result);
        gestisciParametriStruttura(newStrut, parametriGestioneStruttura, result);
        /* Fine gestione parametri */

        // Controllo e associo i modelli in base all'ambiente di questa nuova struttura
        checkAndSetModelliTipiSerieStruttura(newStrut, new BigDecimal(orgEnte.getOrgAmbiente().getIdAmbiente()),
                (isImport ? "importa" : "duplica"));
        // inutile....
        result.setOrgStrut(newStrut);
        return result;
    }

    private CopiaStruttureEjb.OrgStrutCopyResult impostaDatiStrutturaImpStd(OrgStrut oldStrut, OrgStrut newStrut,
            OrgEnte orgEnte, OrgStrutRowBean strutRowBean, SalvaStrutturaDto salva, boolean isImport,
            boolean isStandard, AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura, AplParamApplicTableBean parametriGestioneStruttura)
            throws ParerUserError {
        // Se la struttura NON è template
        if (!strutRowBean.getFlTemplate().equals("1")) {
            if (strutRowBean.getCdStrutNormaliz() == null || struttureHelper
                    .existsCdStrutNormaliz(strutRowBean.getCdStrutNormaliz(), strutRowBean.getIdEnte(), null)) {
                throw new ParerUserError(
                        "Il nome normalizzato della struttura non è stato indicato o non è univoco</br>");
            }
        }

        // Capire se è giusto che il controllo stia qui!!!
        controlloAccordoEnteConvenzionatoSuInsStruttura(strutRowBean);

        // NUOVO MOTORE DI COPIA
        newStrut.setNmStrut(strutRowBean.getNmStrut());
        newStrut.setDsStrut(strutRowBean.getDsStrut());
        newStrut.setDtIniVal(strutRowBean.getDtIniVal());
        newStrut.setDtFineVal(strutRowBean.getDtFineVal());
        newStrut.setDtIniValStrut(strutRowBean.getDtIniValStrut());
        newStrut.setDtFineValStrut(strutRowBean.getDtFineValStrut());
        newStrut.setCdStrutNormaliz(strutRowBean.getCdStrutNormaliz());
        newStrut.setCdIpa(strutRowBean.getCdIpa());
        newStrut.setFlTemplate("0");
        newStrut.setDlNoteStrut(strutRowBean.getDlNoteStrut());
        newStrut.setOrgEnte(orgEnte);
        if (strutRowBean.getIdCategStrut() != null) {
            newStrut.setOrgCategStrut(struttureHelper.findById(OrgCategStrut.class, strutRowBean.getIdCategStrut()));
        } else {
            newStrut.setOrgCategStrut(null);
        }

        // Aggiungo i valori riguardanti l'ente convenzionato
        newStrut.setIdEnteConvenz(strutRowBean.getIdEnteConvenz());

        newStrut = formatoFileStandardHelper.getEntityManager().merge(newStrut);
        formatoFileStandardHelper.getEntityManager().flush();

        if (salva.isCheckIncludiTipiFascicolo()) {
            // Controllo i modelli xsd periodo tipi fascicolo in base all'ambiente di questa nuova struttura
            checkAndSetModelliXsdTipiFascicoloStruttura(oldStrut,
                    new BigDecimal(orgEnte.getOrgAmbiente().getIdAmbiente()));
        }

        CopiaStruttureEjb.OrgStrutCopyResult result = copiaStruttureEjb.getOrgStrutCopyFromStrutImpStd(oldStrut,
                newStrut, salva, isStandard);
        newStrut = result.getOrgStrut();

        /* Gestione Parametri */
        gestisciParametriStruttura(newStrut, parametriAmministrazioneStruttura, result);
        gestisciParametriStruttura(newStrut, parametriConservazioneStruttura, result);
        gestisciParametriStruttura(newStrut, parametriGestioneStruttura, result);
        /* Fine gestione parametri */

        // Controllo e associo i modelli in base all'ambiente di questa nuova struttura
        checkAndSetModelliTipiSerieStruttura(newStrut, new BigDecimal(orgEnte.getOrgAmbiente().getIdAmbiente()),
                (isImport ? "importa" : "duplica"));
        // inutile....
        result.setOrgStrut(newStrut);
        return result;
    }

    /*
     * Popola i valori param applic della struttura partendo da liste di rowBean separati per gestione, amministrazione
     * e conservazione
     */
    private void gestisciParametriStruttura(OrgStrut newStrut, AplParamApplicTableBean parametri,
            CopiaStruttureEjb.OrgStrutCopyResult result) {
        if (newStrut.getAplValoreParamApplics() == null) {
            newStrut.setAplValoreParamApplics(new ArrayList<>());
        }
        if (parametri != null && !parametri.isEmpty()) {
            Iterator<AplParamApplicRowBean> it = parametri.iterator();
            while (it.hasNext()) {
                AplParamApplicRowBean rb = it.next();
                String nmParamApplic = rb.getNmParamApplic();
                AplParamApplic aplParamApplic = configurationHelper.getParamApplic(nmParamApplic);
                // Se Applic non esiste lo crea e valorizza tutto il necessario
                if (aplParamApplic == null) {
                    result.addMessageIfNotExists("Il parametro [" + nmParamApplic
                            + "] non è presente nella struttura di destinazione e non è stato inserito");
                } else {
                    aplParamApplic.setFlAppartStrut("1");
                    // gestione del Valore param Applic
                    String valoreParamApplic = null;
                    if (rb.getTiGestioneParam().equals("amministrazione")) {
                        valoreParamApplic = rb.getString("ds_valore_param_applic_strut_amm");
                    } else if (rb.getTiGestioneParam().equals("gestione")) {
                        valoreParamApplic = rb.getString("ds_valore_param_applic_strut_gest");
                    } else if (rb.getTiGestioneParam().equals("conservazione")) {
                        valoreParamApplic = rb.getString("ds_valore_param_applic_strut_cons");
                    }
                    if (valoreParamApplic != null && !valoreParamApplic.trim().equals("")) {
                        AplValoreParamApplic aplValoreParamApplic = new AplValoreParamApplic();
                        aplValoreParamApplic.setAplParamApplic(aplParamApplic);
                        aplValoreParamApplic.setTiAppart("STRUT");
                        aplValoreParamApplic.setDsValoreParamApplic(valoreParamApplic);
                        aplValoreParamApplic.setAplParamApplic(aplParamApplic);
                        struttureHelper.getEntityManager().persist(aplValoreParamApplic);
                        newStrut.addAplValoreParamApplic(aplValoreParamApplic);
                    }
                }
            }
        }
    }

    /**
     * Controlla se registri e tipi Ud della nuova struttura hanno associati dei modelli. Nel qual caso verifica che gli
     * stessi siano presenti nel nuovo ambiente della nuova struttura. Se anche un solo modello non è presente per il
     * nuovo ambiente, viene segnalato errore, al contrario, con tutti i modelli presenti nella nuova struttura
     * (verificata l'uguaglianza tramite il nome) si procede al recupero da DB degli stessi ed associati a registri e
     * tipi ud
     *
     * @param strutturaNuova
     *            struttura
     * @param idAmbienteNuovo
     *            id ambiente nuovo
     * @param infisso
     *            fisso
     *
     * @throws ParerUserError
     *             errore generico
     */
    private void checkAndSetModelliTipiSerieStruttura(OrgStrut strutturaNuova, BigDecimal idAmbienteNuovo,
            String infisso) throws ParerUserError {
        List<DecRegistroUnitaDoc> registriNuovi = strutturaNuova.getDecRegistroUnitaDocs();
        List<DecTipoUnitaDoc> tipiUdNuovi = strutturaNuova.getDecTipoUnitaDocs();
        String articoloDeterminativo = infisso.equals("duplica") ? "la " : "l'";
        String errorMessage = "Nella struttura " + infisso + "ta "
                + "esiste almeno un registro o un tipo unità documentaria associato "
                + "a un modello che non è definito nell'ambiente di destinazione. " + "Prima di eseguire "
                + articoloDeterminativo + infisso + "zione occorre definire il modello";
        if (registriNuovi != null) {
            for (DecRegistroUnitaDoc registroNuovo : registriNuovi) {
                if (registroNuovo.getDecModelloTipoSerie() != null) {
                    DecModelloTipoSerie modelloTipoSerieNuovoAmbiente = modelliSerieHelper.getDecModelloTipoSerie(
                            registroNuovo.getDecModelloTipoSerie().getNmModelloTipoSerie(), idAmbienteNuovo);
                    if (modelloTipoSerieNuovoAmbiente != null) {
                        registroNuovo.setDecModelloTipoSerie(modelloTipoSerieNuovoAmbiente);
                    } else {
                        throw new ParerUserError(errorMessage);
                    }
                }
            }
        }

        if (tipiUdNuovi != null) {
            for (DecTipoUnitaDoc tipoUdNuovo : tipiUdNuovi) {
                if (tipoUdNuovo.getDecModelloTipoSerie() != null) {
                    DecModelloTipoSerie modelloTipoSerieNuovoAmbiente = modelliSerieHelper.getDecModelloTipoSerie(
                            tipoUdNuovo.getDecModelloTipoSerie().getNmModelloTipoSerie(), idAmbienteNuovo);
                    if (modelloTipoSerieNuovoAmbiente != null) {
                        tipoUdNuovo.setDecModelloTipoSerie(modelloTipoSerieNuovoAmbiente);
                    } else {
                        throw new ParerUserError(errorMessage);
                    }
                }
            }
        }

    }

    /**
     * Controlla se i periodi di validità dei tipi fascicolo della nuova struttura hanno associati dei modelli xsd tipo
     * fascicolo. Nel qual caso verifica che gli stessi siano presenti nel nuovo ambiente della nuova struttura. Se
     * anche un solo modello xsd non è presente per il nuovo ambiente, viene segnalato errore, al contrario, con tutti i
     * modelli presenti nella nuova struttura (verificata l'uguaglianza tramite il il tipo modello e la versione codice
     * xsd) si procede al recupero da DB degli stessi ed associati al periodo tipo fascicolo
     *
     * @param strutturaOld
     *            strutturaOld
     * @param idAmbienteNuovo
     *            id ambiente nuovo
     *
     * @throws ParerUserError
     *             errore generico
     */
    public void checkAndSetModelliXsdTipiFascicoloStruttura(OrgStrut strutturaOld, BigDecimal idAmbienteNuovo)
            throws ParerUserError {
        List<DecTipoFascicolo> tipiFascicoloOld = strutturaOld.getDecTipoFascicolos();
        boolean eccezione = false;

        List<String[]> listaDatiErroreModelli = new ArrayList<>();
        Set<String> setModelliErrore = new HashSet<>();
        if (tipiFascicoloOld != null) {
            for (DecTipoFascicolo tipoFascicoloNuovo : tipiFascicoloOld) {
                if (tipoFascicoloNuovo.getDecAaTipoFascicolos() != null) {
                    for (DecAaTipoFascicolo aaTipoFascicoloNuovo : tipoFascicoloNuovo.getDecAaTipoFascicolos()) {
                        if (aaTipoFascicoloNuovo.getDecUsoModelloXsdFascs() != null) {
                            for (DecUsoModelloXsdFasc usoModelloXsdFascNuovo : aaTipoFascicoloNuovo
                                    .getDecUsoModelloXsdFascs()) {
                                DecModelloXsdFascicolo modelloXsdFascicoloNuovo = usoModelloXsdFascNuovo
                                        .getDecModelloXsdFascicolo();
                                if (modelloXsdFascicoloNuovo != null) {
                                    DecModelloXsdFascicolo modelloXsdFascicoloNuovoAmbiente = modelliFascicoliHelper
                                            .getDecModelloXsdFascicolo(idAmbienteNuovo,
                                                    modelloXsdFascicoloNuovo.getTiModelloXsd().name(),
                                                    modelloXsdFascicoloNuovo.getTiUsoModelloXsd().name(),
                                                    modelloXsdFascicoloNuovo.getCdXsd());
                                    if (modelloXsdFascicoloNuovoAmbiente == null) {
                                        eccezione = true;
                                        String[] datiErroreModelli = new String[3];
                                        datiErroreModelli[0] = tipoFascicoloNuovo.getNmTipoFascicolo();
                                        datiErroreModelli[1] = modelloXsdFascicoloNuovo.getTiModelloXsd().name();
                                        datiErroreModelli[2] = modelloXsdFascicoloNuovo.getCdXsd();
                                        String modelloDaValutare = modelloXsdFascicoloNuovo.getTiModelloXsd().name()
                                                + "_v" + modelloXsdFascicoloNuovo.getCdXsd();
                                        if (setModelliErrore.add(modelloDaValutare)) {
                                            listaDatiErroreModelli.add(datiErroreModelli);
                                        }
                                    }
                                }

                            }
                        }
                    }

                }
            }
        }
        if (eccezione) {
            StringBuilder errorMessage = new StringBuilder();
            for (String[] datiErroreModelli : listaDatiErroreModelli) {
                errorMessage.append("Il Tipo fascicolo ").append(datiErroreModelli[0]).append(" referenzia il modello ")
                        .append(datiErroreModelli[1]).append("_v").append(datiErroreModelli[2]);
            }
            throw new ParerUserError(errorMessage.append("Impossibile proseguire").toString());
        }
    }

    public void checkAndSetModelliXsdTipiFascicoloStruttura(UUID uuid, BigDecimal idStrutCorrente)
            throws ParerUserError {
        // Ricavo la struttura presente nell'XML
        OrgStrut strutExp = strutCache.getOrgStrut(uuid);
        OrgStrut strutturaCorrente = struttureHelper.findById(OrgStrut.class, idStrutCorrente);

        checkAndSetModelliXsdTipiFascicoloStruttura(strutExp,
                new BigDecimal(strutturaCorrente.getOrgEnte().getOrgAmbiente().getIdAmbiente()));

    }

    public void checkAndSetModelliXsdTipiFascicoloStrutturaImpParam(OrgStrut strutturaOld, BigDecimal idAmbienteNuovo,
            String tipoFascicoloDaImp) throws ParerUserError {
        List<DecTipoFascicolo> tipiFascicoloOld = strutturaOld.getDecTipoFascicolos();
        boolean eccezione = false;

        List<String[]> listaDatiErroreModelli = new ArrayList<>();
        Set<String> setModelliErrore = new HashSet<>();
        if (tipiFascicoloOld != null) {
            for (DecTipoFascicolo tipoFascicoloNuovo : tipiFascicoloOld) {
                if (tipoFascicoloDaImp.equals(tipoFascicoloNuovo.getNmTipoFascicolo())) {
                    if (tipoFascicoloNuovo.getDecAaTipoFascicolos() != null) {
                        for (DecAaTipoFascicolo aaTipoFascicoloNuovo : tipoFascicoloNuovo.getDecAaTipoFascicolos()) {
                            if (aaTipoFascicoloNuovo.getDecUsoModelloXsdFascs() != null) {
                                for (DecUsoModelloXsdFasc usoModelloXsdFascNuovo : aaTipoFascicoloNuovo
                                        .getDecUsoModelloXsdFascs()) {
                                    DecModelloXsdFascicolo modelloXsdFascicoloNuovo = usoModelloXsdFascNuovo
                                            .getDecModelloXsdFascicolo();
                                    if (modelloXsdFascicoloNuovo != null) {
                                        DecModelloXsdFascicolo modelloXsdFascicoloNuovoAmbiente = modelliFascicoliHelper
                                                .getDecModelloXsdFascicolo(idAmbienteNuovo,
                                                        modelloXsdFascicoloNuovo.getTiModelloXsd().name(),
                                                        modelloXsdFascicoloNuovo.getTiUsoModelloXsd().name(),
                                                        modelloXsdFascicoloNuovo.getCdXsd());
                                        if (modelloXsdFascicoloNuovoAmbiente == null) {
                                            eccezione = true;
                                            String[] datiErroreModelli = new String[3];
                                            datiErroreModelli[0] = tipoFascicoloNuovo.getNmTipoFascicolo();
                                            datiErroreModelli[1] = modelloXsdFascicoloNuovo.getTiModelloXsd().name();
                                            datiErroreModelli[2] = modelloXsdFascicoloNuovo.getCdXsd();
                                            String modelloDaValutare = modelloXsdFascicoloNuovo.getTiModelloXsd().name()
                                                    + "_v" + modelloXsdFascicoloNuovo.getCdXsd();
                                            if (setModelliErrore.add(modelloDaValutare)) {
                                                listaDatiErroreModelli.add(datiErroreModelli);
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }

                }
            }
        }
        if (eccezione) {
            StringBuilder errorMessage = new StringBuilder();
            for (String[] datiErroreModelli : listaDatiErroreModelli) {
                errorMessage.append("Il Tipo fascicolo ").append(datiErroreModelli[0]).append(" referenzia il modello ")
                        .append(datiErroreModelli[1]).append("_v").append(datiErroreModelli[2])
                        .append(" che non è presente in questo ambiente;<br>");
            }
            throw new ParerUserError(errorMessage.append("Impossibile proseguire.").toString());
        }
    }

    public void checkAndSetModelliXsdTipiFascicoloStrutturaImpParam(UUID uuid, BigDecimal idStrutCorrente,
            String tipoFascicoloDaImp) throws ParerUserError {
        // Ricavo la struttura presente nell'XML
        OrgStrut strutExp = strutCache.getOrgStrut(uuid);
        OrgStrut strutturaCorrente = struttureHelper.findById(OrgStrut.class, idStrutCorrente);

        checkAndSetModelliXsdTipiFascicoloStrutturaImpParam(strutExp,
                new BigDecimal(strutturaCorrente.getOrgEnte().getOrgAmbiente().getIdAmbiente()), tipoFascicoloDaImp);

    }

    /**
     *
     * @param struttureDaElaborare
     *            le strutture da elaborare nell'importa parametri massivo
     *
     * @return la stringa con eventuale errore per presenza di strutture appartenenti ad ambienti diversi nell'import
     *         parametri massivo
     *
     * @throws ParerUserError
     *             errore generico
     */
    public String checkAppartenenzaAmbiente(Map<BigDecimal, String> struttureDaElaborare) throws ParerUserError {
        // Scorro le strutture coinvolte nell'importa parametri per verificare se appartengono tutte allo stesso
        // ambiente
        Set<Long> idAmbienteSet = new HashSet<>();
        String errore = "";
        for (Map.Entry<BigDecimal, String> entry : struttureDaElaborare.entrySet()) {
            BigDecimal idStrutCorrente = entry.getKey();
            OrgStrut strut = struttureHelper.findById(OrgStrut.class, idStrutCorrente);
            idAmbienteSet.add(strut.getOrgEnte().getOrgAmbiente().getIdAmbiente());
        }
        if (idAmbienteSet.size() > 1) {
            errore = "Per l'import dei tipi fascicolo le strutture individuate devono appartenere allo stesso AMBIENTE";
        }
        return errore;
    }

    /**
     * Come il metodo checkAndSetModelliTipiSerieStruttura, volendo si può creare un unico metodo
     *
     * @param idStrutturaCorrente
     *            id struttura corrente
     * @param tipoUnitaDocExp
     *            tipo unita doc
     * @param tipoUnitaDoc
     *            tipo unita doc
     * @param registriImportati
     *            registri
     *
     * @throws ParerUserError
     *             errore generico
     */
    private void checkAndSetModelliTipiSerieStrutturaPerImportaParametri(BigDecimal idStrutturaCorrente,
            DecTipoUnitaDoc tipoUnitaDocExp, DecTipoUnitaDoc tipoUnitaDoc, List<CoppiaRegistri> registriImportati)
            throws ParerUserError {
        // Ricavo la struttura corrente, quella dove mi trovo e sto eseguendo l'importazione dei parametri
        OrgStrut strutturaCorrente = struttureHelper.findById(OrgStrut.class, idStrutturaCorrente.longValue());
        // Da essa ricavo l'ambiente della struttura in cui mi trovo
        long idAmbienteNuovo = strutturaCorrente.getOrgEnte().getOrgAmbiente().getIdAmbiente();
        String errorMessage = "Il tipo di unit\u00E0 documentaria o i registri ad essa "
                + "associati sono associati a un modello che non \u00E8 definito nell'ambiente di destinazione. "
                + "Prima di eseguire l'importazione occorre definire il modello";

        // Verifico se sui registri o tipi di unità documentaria che sto importando ci siano dei tag DecModelloTipoSerie
        if (registriImportati != null) {
            for (CoppiaRegistri registroNuovo : registriImportati) {
                if (registroNuovo.getRegistroUnitaDocExp().getDecModelloTipoSerie() != null) {
                    DecModelloTipoSerie modelloTipoSerieNuovoAmbiente = modelliSerieHelper.getDecModelloTipoSerie(
                            registroNuovo.getRegistroUnitaDocExp().getDecModelloTipoSerie().getNmModelloTipoSerie(),
                            new BigDecimal(idAmbienteNuovo));
                    if (modelloTipoSerieNuovoAmbiente != null) {
                        registroNuovo.getRegistroUnitaDoc().setDecModelloTipoSerie(modelloTipoSerieNuovoAmbiente);
                    } else {
                        throw new ParerUserError(errorMessage);
                    }
                }
            }
        }

        if (tipoUnitaDocExp.getDecModelloTipoSerie() != null) {
            DecModelloTipoSerie modelloTipoSerieNuovoAmbiente = modelliSerieHelper.getDecModelloTipoSerie(
                    tipoUnitaDocExp.getDecModelloTipoSerie().getNmModelloTipoSerie(), new BigDecimal(idAmbienteNuovo));
            if (modelloTipoSerieNuovoAmbiente != null) {
                tipoUnitaDoc.setDecModelloTipoSerie(modelloTipoSerieNuovoAmbiente);
            } else {
                throw new ParerUserError(errorMessage);
            }
        }
    }

    public void deleteStruttura(LogParam param, long idStrut) throws ParerUserError {
        // Richiedo al EJB container una nuova istanza di StruttureEjb
        // N.B.: per avere un nuovo contesto transazionale devo avere
        // una nuova istanza di StruttureEjb
        StruttureEjb me = context.getBusinessObject(StruttureEjb.class);
        // Il metodo chiamato (executeDeleteStruttura) è in REQUIRES_NEW...
        // quindi devo richiamarlo tramite il businessObject altrimenti
        // il container non lo considererebbe come nuova transazione ma
        // facente parte del contesto transazionale già esistente
        IamOrganizDaReplic replic = me.executeDeleteStruttura(param, idStrut);
        me.replicateToIam(replic);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic executeDeleteStruttura(LogParam param, long idStrut) throws ParerUserError {
        OrgStrut struttura = struttureHelper.findById(OrgStrut.class, idStrut);

        try {

            // Loggo l'intera struttura che sto per cancellare
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_STRUTTURA, new BigDecimal(idStrut),
                    param.getNomePagina());

            if (struttureHelper.checkManyRelationsAreEmptyForStruttura(idStrut)) {
                throw new ParerUserError(
                        "Eliminazione della struttura non consentita. E' stato eseguito almeno un versamento di unità documentaria</br>");
            }

            if (corrispondenzeHelper.checkPingRelations(idStrut, 0)) {
                throw new ParerUserError(
                        "Eliminazione della struttura non consentita. Sono presenti corrispondenze con versatori di Ping</br>");
            }

            // Elimino tutti i registri
            for (DecRegistroUnitaDoc registroUnitaDoc : struttura.getDecRegistroUnitaDocs()) {
                registroEjb.deleteRegistroUnitaDocFromStruttura(param, registroUnitaDoc.getIdRegistroUnitaDoc());
            }

            // Elimino tutte le tipologie unità documentaria
            for (DecTipoUnitaDoc tipoUnitaDoc : struttura.getDecTipoUnitaDocs()) {
                tipoUnitaDocEjb.deleteTipoUnitaDocFromStruttura(param, tipoUnitaDoc.getIdTipoUnitaDoc());
            }

            // Elimino tutti i tipi documento
            for (DecTipoDoc tipoDoc : struttura.getDecTipoDocs()) {
                tipoDocEjb.deleteTipoDocFromStruttura(param, tipoDoc.getIdTipoDoc());
            }

            // Elimino tutti i tipi fascicolo
            for (DecTipoFascicolo tipoFascicolo : struttura.getDecTipoFascicolos()) {
                tipoFascicoloEjb.deleteDecTipoFascicolo(param, BigDecimal.valueOf(tipoFascicolo.getIdTipoFascicolo()));
            }

            // Elimino tutti i tipi struttura documento
            for (DecTipoStrutDoc tipoStrutDocDoc : struttura.getDecTipoStrutDocs()) {
                tipoStrutDocEjb.deleteDecTipoStrutDoc(param, tipoStrutDocDoc.getIdTipoStrutDoc());
            }

            // Elimino tutti i tipi di serie definiti sulla struttura
            for (DecTipoSerie tipoSerie : struttura.getDecTipoSeries()) {
                tipoSerieEjb.deleteDecTipoSerie(param, tipoSerie.getIdTipoSerie());
            }

            // Elimino tutti i tipi di rappresentazione componente
            for (DecTipoRapprComp tipoRapprComp : struttura.getDecTipoRapprComps()) {
                tipoRapprEjb.deleteDecTipoRapprComp(param, tipoRapprComp.getIdTipoRapprComp());
            }

            // // Elimino tutti i formati file doc ammessi
            formatoFileDocHelper.bulkDeleteDecFormatoFileDoc(idStrut);

            // Elimino tutti i criteri di raggruppamento
            for (DecCriterioRaggr criterioRaggr : struttura.getDecCriterioRaggrs()) {
                crHelper.deleteDecCriterioRaggr(param, new BigDecimal(criterioRaggr.getOrgStrut().getIdStrut()),
                        criterioRaggr.getNmCriterioRaggr());
            }

            // Elimino tutte le sottostrutture
            for (OrgSubStrut subStrut : struttura.getOrgSubStruts()) {
                subStrutEjb.deleteSubStruttura(param, subStrut.getIdSubStrut(), true);
            }

            // Elimino eventuali XSD di migrazione
            datiSpecEjb.deleteDecXsdDatiSpecMigraz(idStrut);

            // Elimino la struttura, i titolari e le partizioni esistenti per essa
            struttureHelper.removeEntity(struttura, true);

        } catch (ParerUserError e) {
            // Aggiungo un prefisso e rilancio l'eccezione
            throw new ParerUserError("Cancellazione struttura - " + e.getDescription());
        }

        // Inserisco il record di replica organizzazione
        IamOrganizDaReplic replic = context.getBusinessObject(StruttureEjb.class)
                .insertStrutIamOrganizDaReplic(struttura, ApplEnum.TiOperReplic.CANC);
        logger.info("Cancellazione della struttura {} avvenuta con successo!", idStrut);
        return replic;
    }

    /*
     * Funzioni di interrogazione
     */
    // -------------------------------------
    public boolean existAroUnitaDocByIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc, BigDecimal idStrut) {
        return unitaDocHelper.existAroUnitaDocsFromRegistro(idRegistroUnitaDoc, idStrut);
    }

    public boolean existAroUnitaDocByIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc, BigDecimal idStrut) {
        return unitaDocHelper.existAroUnitaDocsFromTipoUnita(idTipoUnitaDoc, idStrut);
    }

    public long getIdStrutFromTipoStrutUnitaDoc(BigDecimal idTipoStrutUnitaDoc) {
        return struttureHelper.getidStrutFromIdTipoStrutUnitaDoc(idTipoStrutUnitaDoc);
    }

    public boolean isStrutUsedForVers(BaseRowInterface orgStrutRowBean) {
        OrgStrut strut = struttureHelper.findById(OrgStrut.class, orgStrutRowBean.getBigDecimal("id_strut"));
        return !strut.getVrsSessioneVers().isEmpty();
    }

    /**
     * Metodo che ritorna il primo template, se presente, dalla lista dei template sul DB
     *
     * @return OrgStrut
     */
    public OrgStrut getFirstOrgStrutTemplate() {
        return struttureHelper.getFirstOrgStrutTemplate();
    }

    public String countOrgStrutTemplateRaggruppati(long idUserIam) {
        List<Object[]> numStrutTemplatePerAmbiente = struttureHelper.countOrgStrutTemplateRaggruppati(idUserIam);
        return getConteggioAmbientiPerStrutturaFormattati(numStrutTemplatePerAmbiente);
    }

    public String countOrgStrutTemplateWithCompletedPartitioningRaggruppati(long idUserIam) {
        List<Object[]> numStrutTemplatePartizionatePerAmbiente = struttureHelper
                .countOrgStrutTemplateWithCompletedPartitioningRaggruppati(idUserIam);
        return getConteggioAmbientiPerStrutturaFormattati(numStrutTemplatePartizionatePerAmbiente);
    }

    public long countOrgStrutTemplatePerAmbienteEnte(Long idAmbiente, Long idEnte, String tipoDefTemplateEnte) {
        return struttureHelper.countOrgStrutTemplatePerAmbienteEnte(idAmbiente, idEnte, tipoDefTemplateEnte);
    }

    private String getConteggioAmbientiPerStrutturaFormattati(List<Object[]> numStrutTemplatePerAmbiente) {
        StringBuilder result = new StringBuilder();
        int totStrutture = 0;
        String trattino = "";
        if (!numStrutTemplatePerAmbiente.isEmpty()) {
            int i = 0;
            result.append("(");
            do {
                result.append(trattino);
                Object[] ambiente = numStrutTemplatePerAmbiente.get(i);
                result.append(ambiente[1]).append(": <b>").append(ambiente[0]).append("</b>");
                totStrutture = totStrutture + ((Long) ambiente[0]).intValue();
                trattino = "   ";
                i++;
            } while (i < numStrutTemplatePerAmbiente.size());
            result.append(")");
            result.insert(0, "<b>" + totStrutture + "</b> ");
        } else {
            result.append("<b>0</b>");
        }
        return result.toString();
    }

    public OrgCategStrutTableBean getOrgCategStrutTableBean(OrgCategStrutRowBean categStrutRowBean) {
        OrgCategStrutTableBean orgCategStrutTableBean = new OrgCategStrutTableBean();
        List<OrgCategStrut> list = null;
        if (categStrutRowBean != null) {
            list = struttureHelper.getOrgCategStrutList(categStrutRowBean.getCdCategStrut(),
                    categStrutRowBean.getDsCategStrut());
        } else {
            list = struttureHelper.getOrgCategStrutList(null, null);
        }
        if (list != null) {
            try {
                orgCategStrutTableBean = (OrgCategStrutTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Eccezione", ex);
            }
        }

        return orgCategStrutTableBean;
    }

    public OrgCategStrutRowBean getOrgCategStrutRowBean(BigDecimal idCategStrut) {
        return (getOrgCategStrut(idCategStrut, null));
    }

    private OrgCategStrutRowBean getOrgCategStrut(BigDecimal idCategStrut, String cdCategStrut) {
        OrgCategStrutRowBean categStrutRowBean = new OrgCategStrutRowBean();
        OrgCategStrut categStrut = new OrgCategStrut();

        if (idCategStrut != BigDecimal.ZERO && cdCategStrut == null) {
            categStrut = struttureHelper.findById(OrgCategStrut.class, idCategStrut);
        }
        if (idCategStrut == BigDecimal.ZERO && cdCategStrut != null) {
            categStrut = struttureHelper.getOrgCategStrutByCd(cdCategStrut);
        }

        if (categStrut != null) {
            try {
                categStrutRowBean = (OrgCategStrutRowBean) Transform.entity2RowBean(categStrut);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Eccezione", ex);
            }
        }
        return categStrutRowBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertOrgCategStrut(OrgCategStrutRowBean categStrutRowBean) throws ParerUserError {
        OrgCategStrut categStrutDB = struttureHelper.getOrgCategStrutByCd(categStrutRowBean.getCdCategStrut());
        if (categStrutDB != null) {
            throw new ParerUserError("Codice categoria gi\u00E0 utilizzato nel database, scegliere altro codice ");
        }
        OrgCategStrut categStrut = (OrgCategStrut) Transform.rowBean2Entity(categStrutRowBean);
        struttureHelper.insertEntity(categStrut, true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateOrgCategStrut(BigDecimal idCategStrut, OrgCategStrutRowBean categStrutRowBean)
            throws ParerUserError {
        OrgCategStrut categStrutDB = struttureHelper.getOrgCategStrutByCd(categStrutRowBean.getCdCategStrut());

        if (categStrutDB != null && categStrutDB.getIdCategStrut() != idCategStrut.longValue()) {
            throw new ParerUserError("Codice categoria gi\u00E0 utilizzato nel database, scegliere altro codice ");
        }

        OrgCategStrut categStrut = struttureHelper.findById(OrgCategStrut.class, idCategStrut);
        categStrut.setCdCategStrut(categStrutRowBean.getCdCategStrut());
        categStrut.setDsCategStrut(categStrutRowBean.getDsCategStrut());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteOrgCategStrut(OrgCategStrutRowBean orgCategStrutRowBean) throws ParerUserError {
        OrgCategStrut categStrutDB = struttureHelper.findById(OrgCategStrut.class,
                orgCategStrutRowBean.getIdCategStrut());
        if (!categStrutDB.getOrgStruts().isEmpty()) {
            throw new ParerUserError("Categoria associata a strutture. Operazione cancellata.");
        }
        struttureHelper.removeEntity(categStrutDB, true);
    }

    public boolean hasAroUnitaDoc(BigDecimal idStrut) {
        return struttureHelper.hasAroUnitaDoc(idStrut);
    }

    /**
     * Metodo che esegue la chiamata di allineamento organizzazioni
     *
     * @param organizDaReplic
     *            array di record da replicare
     *
     * @throws ParerUserError
     *             Eccezione con rollback in caso di entity diversa dalle suddette o di errore imprevisto da parte
     *             dell'allineamento
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void replicateToIam(IamOrganizDaReplic... organizDaReplic) throws ParerUserError {
        List<IamOrganizDaReplic> orgDaReplic = (organizDaReplic != null ? Arrays.asList(organizDaReplic)
                : new ArrayList<IamOrganizDaReplic>());
        try {
            allineamentoOrganizzazioniEjb.allineamentoOrganizzazioni(orgDaReplic);
        } catch (Exception ex) {
            logger.error("Errore imprevisto del servizio di replica : " + ex.getMessage(), ex);
            jobHelper.writeAtomicLogJob(JobConstants.JobEnum.ALLINEAMENTO_ORGANIZZAZIONI.name(),
                    JobConstants.OpTypeEnum.ERRORE.name(), "Errore imprevisto del servizio di replica");
            throw new ParerUserError("Errore imprevisto del servizio di replica");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public IamOrganizDaReplic insertStrutIamOrganizDaReplic(OrgStrut orgStrut, ApplEnum.TiOperReplic tipoOperazione) {
        IamOrganizDaReplic replica = new IamOrganizDaReplic();
        replica.setIdOrganizApplic(new BigDecimal(orgStrut.getIdStrut()));
        replica.setNmTipoOrganiz(ApplEnum.NmOrganizReplic.STRUTTURA.name());
        replica.setNmOrganiz(orgStrut.getNmStrut());
        replica.setTiOperReplic(tipoOperazione.name());
        replica.setTiStatoReplic("DA_REPLICARE");
        replica.setDtLogOrganizDaReplic(new Date());
        struttureHelper.insertEntity(replica, true);
        return replica;
    }

    /**
     * Esegue il metodo dell'ejb per la chiamata al WS di allineamento enti convenzionati
     *
     * @param enteConvenzDaAllineaList
     *            lista IamEnteConvenzDaAllinea
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void alignsEnteConvenzToIam(List<IamEnteConvenzDaAllinea> enteConvenzDaAllineaList) throws ParerUserError {
        try {
            aecEjb.allineaEntiConvenzionati(enteConvenzDaAllineaList);
        } catch (Exception ex) {
            logger.error("Errore imprevisto del servizio di allineamento ente convenzionato : " + ex.getMessage(), ex);
            jobHelper.writeAtomicLogJob(JobConstants.JobEnum.ALLINEA_ENTI_CONVENZIONATI.name(),
                    JobConstants.OpTypeEnum.ERRORE.name(),
                    "Errore imprevisto del servizio di allineamento ente convenzionato");
            throw new ParerUserError("Errore imprevisto del servizio di allineamento ente convenzionato");
        }
    }

    /**
     * Metodo chiamato per il ricalcolo su IAM dei servizi erogati sull'ultimo accordo dell'ente convenzionato associato
     * alla struttura del tipo ud
     *
     * @param idEnteConvenz
     *            id ente convenzionato
     *
     * @return true/false
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean calcoloServiziErogati(BigDecimal idEnteConvenz) {
        boolean esito = true;

        /* Ricavo i dati per la chiamata del ws */
        String url = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.URL_CALCOLO_SERVIZI_EROGATI);
        String nmUserid = configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.USERID_REPLICA_ORG);
        String cdPsw = configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.PSW_REPLICA_ORG);
        String timeoutString = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.TIMEOUT_CALCOLO_SERVIZI_EROGATI);

        try {

            /* Recupero il client per chiamata al WS */
            CalcoloServiziErogati client = IAMSoapClients.calcoloServiziErogatiClient(nmUserid, cdPsw, url);
            // imposto il valore di timeout. vedi MEV #23814
            if (timeoutString != null && timeoutString.matches("^[0-9]+$")) {
                int timeoutCalcoloServiziErog = Integer.parseInt(timeoutString);
                IAMSoapClients.changeRequestTimeout((BindingProvider) client, timeoutCalcoloServiziErog);
            } else {
                logger.warn(
                        "Il valore personalizzato \"{}\" per il parametro TIMEOUT_CALCOLO_SERVIZI_EROGATI non è corretto. Utilizzo il valore predefinito",
                        timeoutString);
            }

            if (client != null) {
                logger.info("Calcolo servizi erogati - Preparazione attivazione servizio per l'ente convenzionato {}",
                        idEnteConvenz);

                client.calcoloServiziErogati(idEnteConvenz.intValue());

            } else {
                /* Se il client è null, ci sono stati problemi */
                esito = false;
                logger.error("{} {}", CALCOLO_SERVIZI_EROGATI_RISPOSTA_NEGATIVA_ENTE_CONVENZIONATO, idEnteConvenz);
            }

        } catch (SOAPFaultException e) {
            /* Errori di autenticazione */
            esito = false;
            logger.error("{} {} - Utente che attiva il servizio non riconosciuto o non abilitato",
                    CALCOLO_SERVIZI_EROGATI_RISPOSTA_NEGATIVA_ENTE_CONVENZIONATO, idEnteConvenz, e);

        } catch (WebServiceException e) {
            esito = false;
            logger.error("{} {} - Il servizio di Calcolo servizi erogati sull'ente convenzionato non risponde",
                    CALCOLO_SERVIZI_EROGATI_RISPOSTA_NEGATIVA_ENTE_CONVENZIONATO, idEnteConvenz);

        } catch (Exception e) {
            esito = false;
            logger.error("{} {}", CALCOLO_SERVIZI_EROGATI_RISPOSTA_NEGATIVA_ENTE_CONVENZIONATO, idEnteConvenz, e);

        }
        return esito;

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public IamEnteConvenzDaAllinea insertIamEnteConvenzDaAllinea(BigDecimal idEnteConvenz, String nmEnteConvenz) {
        IamEnteConvenzDaAllinea allinea = new IamEnteConvenzDaAllinea();
        allinea.setIdEnteConvenz(idEnteConvenz);
        allinea.setNmEnteConvenz(nmEnteConvenz);
        allinea.setTiOperAllinea(CostantiAllineaEntiConv.TiOperAllinea.ALLINEA.name());
        allinea.setTiStatoAllinea(CostantiAllineaEntiConv.TiStatoAllinea.DA_ALLINEARE.name());
        allinea.setDtLogEnteConvenzDaAllinea(new Date());
        struttureHelper.insertEntity(allinea, true);
        return allinea;
    }

    public String[] getAmbienteEnteStrutturaDesc(BigDecimal idStrut) {
        OrgStrut strut = struttureHelper.findById(OrgStrut.class, idStrut);
        String[] aes = new String[3];
        aes[0] = strut.getOrgEnte().getOrgAmbiente().getNmAmbiente();
        aes[1] = strut.getOrgEnte().getNmEnte();
        aes[2] = strut.getNmStrut();
        return aes;
    }

    /**
     * Data una struttura importata da XML, restituisce tutti i Tipi Unit\u00E0 Documentaria associati ad essa nell'XML
     * stesso
     *
     * @param uuid
     *            l'oggetto contenente la struttura importata da XML
     *
     * @return il table bean rappresentante i Tipi Unit\u00E0 Documentaria trovati
     */
    public DecTipoUnitaDocTableBean getTipiUdDaXmlImportato(UUID uuid) {
        OrgStrut strut = strutCache.getOrgStrut(uuid);
        List<DecTipoUnitaDoc> decTipoUnitaDocList = strut.getDecTipoUnitaDocs();
        DecTipoUnitaDocTableBean decTipoUnitaDocTableBean = new DecTipoUnitaDocTableBean();

        try {
            if (!decTipoUnitaDocList.isEmpty()) {
                decTipoUnitaDocTableBean = (DecTipoUnitaDocTableBean) Transform.entities2TableBean(decTipoUnitaDocList);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return decTipoUnitaDocTableBean;
    }

    /**
     * Data una struttura importata da XML e uno dei suoi Tipi Unit\u00E0 Documentaria selezionato tra quelli presenti,
     * restituisce tutti i Tipi Struttura Unit\u00E0 Documentaria associati al tipo ud dell'XML stesso
     *
     * @param uuid
     *            l'oggetto contenente la struttura importata da XML
     * @param nmTipoUnitaDoc
     *            il tipo unit\u00E0 documentaria scelto tra quelli presenti nell'XML
     *
     * @return il table bean rappresentante i Tipi Struttura Unit\u00E0 Documentaria trovati
     */
    public DecTipoStrutUnitaDocTableBean getTipiStrutUdDaXmlImportato(UUID uuid, String nmTipoUnitaDoc) {
        OrgStrut strut = strutCache.getOrgStrut(uuid);
        List<DecTipoUnitaDoc> decTipoUnitaDocList = strut.getDecTipoUnitaDocs();
        DecTipoStrutUnitaDocTableBean decTipoStrutUnitaDocTableBean = new DecTipoStrutUnitaDocTableBean();

        for (DecTipoUnitaDoc decTipoUnitaDoc : decTipoUnitaDocList) {
            if (decTipoUnitaDoc.getNmTipoUnitaDoc().equals(nmTipoUnitaDoc)) {
                try {
                    if (!decTipoUnitaDoc.getDecTipoStrutUnitaDocs().isEmpty()) {
                        decTipoStrutUnitaDocTableBean = (DecTipoStrutUnitaDocTableBean) Transform
                                .entities2TableBean(decTipoUnitaDoc.getDecTipoStrutUnitaDocs());
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                        | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return decTipoStrutUnitaDocTableBean;
    }

    /**
     * Data una struttura importata da XML, restituisce tutti i Tipi Fascicolo associati ad essa nell'XML stesso
     *
     * @param uuid
     *            l'oggetto contenente la struttura importata da XML
     *
     * @return il table bean rappresentante i Tipi Fascicolo trovati
     */
    public DecTipoFascicoloTableBean getTipiFascicoloDaXmlImportato(UUID uuid) {
        OrgStrut strut = strutCache.getOrgStrut(uuid);
        List<DecTipoFascicolo> tipoFascicoloList = strut.getDecTipoFascicolos();
        DecTipoFascicoloTableBean tipoFascicoloTableBean = new DecTipoFascicoloTableBean();

        try {
            if (!tipoFascicoloList.isEmpty()) {
                tipoFascicoloTableBean = (DecTipoFascicoloTableBean) Transform.entities2TableBean(tipoFascicoloList);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return tipoFascicoloTableBean;
    }

    /**
     * Data una struttura importata da XML e uno dei suoi Tipi Fascicolo selezionato tra quelli presenti, restituisce
     * tutti i Periodi tipo fascicolo associati al tipo fascicolo dell'XML stesso
     *
     * @param uuid
     *            l'oggetto contenente la struttura importata da XML
     * @param nmTipoFascicolo
     *            il tipo fascicolo scelto tra quelli presenti nell'XML
     *
     * @return il table bean rappresentante i Periodi tipo fascicolo trovati
     */
    public DecAaTipoFascicoloTableBean getAaTipoFascicoloDaXmlImportato(UUID uuid, String nmTipoFascicolo) {
        OrgStrut strut = strutCache.getOrgStrut(uuid);
        List<DecTipoFascicolo> tipoFascicoloList = strut.getDecTipoFascicolos();
        DecAaTipoFascicoloTableBean aaTipoFascicoloTableBean = new DecAaTipoFascicoloTableBean();

        for (DecTipoFascicolo tipoFascicolo : tipoFascicoloList) {
            if (tipoFascicolo.getNmTipoFascicolo().equals(nmTipoFascicolo)) {
                try {
                    for (DecAaTipoFascicolo aaTipoFascicolo : tipoFascicolo.getDecAaTipoFascicolos()) {
                        DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = (DecAaTipoFascicoloRowBean) Transform
                                .entity2RowBean(aaTipoFascicolo);
                        aaTipoFascicoloRowBean.setString("descrizione_periodo", aaTipoFascicolo.getAaIniTipoFascicolo()
                                + " - " + aaTipoFascicolo.getAaFinTipoFascicolo());
                        aaTipoFascicoloTableBean.add(aaTipoFascicoloRowBean);
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                        | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return aaTipoFascicoloTableBean;
    }

    /**
     * Esegue l'import del tipo unit\u00E0 documentaria preso dall'XML.
     *
     * @param param
     *            parametro
     * @param struttureDaElaborare
     *            l'insieme delle strutture su cui eseguire l'importa parametri
     * @param nmTipoUnitaDocSelezionataDaCombo
     *            il tipo unit\u00E0 documentaria selezionato nella combo
     * @param uuid
     *            l'oggetto contenente la struttura importata da XML
     * @param nmTipoStrutUnitaDocSelezionataDaCombo
     *            l'eventuale tipo struttura unit\u00E0 documentaria selezionato dalla combo
     * @param importareRegistri
     *            il flag che mi dice se devo importare anche i registri collegati
     * @param importareCriteri
     *            il flag che mi dice se devo importare anche i criteri di raggruppamento
     * @param importareSistemiMigraz
     *            flag 1/0
     * @param existRegistriDaImportareConFlTipoSerieMultAlzato
     *            esiste registro da importare
     *
     * @param importareFormatiComponente
     *            flag
     *
     *
     * @return report, array di oggetti contenente le liste/mappe con i diversi report
     *
     * @throws ParerUserError
     *             errore generico
     */
    public Object[] eseguiImportTipoUd(LogParam param, Map<BigDecimal, String> struttureDaElaborare, UUID uuid,
            String nmTipoUnitaDocSelezionataDaCombo, String nmTipoStrutUnitaDocSelezionataDaCombo,
            String importareRegistri, String importareCriteri, String importareSistemiMigraz,
            boolean existRegistriDaImportareConFlTipoSerieMultAlzato, String importareFormatiComponente)
            throws ParerUserError {

        // Preparo le strutture dati per il report
        Set<String> strutErrorGenerico = new HashSet<>();
        Set<String> strutErrorSuModello = new HashSet<>();
        Map<String, String> strutErrorTipiSerie = new HashMap<>();
        Set<String> strutErrorSisMigr = new HashSet<>();

        // Scorro le strutture coinvolte nell'importa parametri
        for (Map.Entry<BigDecimal, String> entry : struttureDaElaborare.entrySet()) {
            BigDecimal idStrutCorrente = entry.getKey();
            String nmStrutCorrente = entry.getValue();

            IamOrganizDaReplic organizDaReplic = null;
            StruttureEjb me = context.getBusinessObject(StruttureEjb.class);
            try {
                // L'import viene eseguito in una nuova transazione
                me.confermaImportazione(param, idStrutCorrente, uuid, nmTipoUnitaDocSelezionataDaCombo,
                        nmTipoStrutUnitaDocSelezionataDaCombo, importareRegistri, importareCriteri,
                        importareSistemiMigraz, importareFormatiComponente);
            } catch (ParerUserError e) {
                if (e.getDescription().contains(" iniziare o terminare con caratteri di spaziatura")) {
                    throw new ParerUserError(e.getDescription());
                } else {
                    strutErrorSuModello.add(nmStrutCorrente);
                    // passa alla struttura successiva
                    continue;
                }
            } catch (Exception e) {
                strutErrorGenerico.add(nmStrutCorrente);
                // passa alla struttura successiva
                continue;
            }

            DecTipoUnitaDoc tipoUnitaDoc = tipoUnitaDocHelper.getDecTipoUnitaDocByName(nmTipoUnitaDocSelezionataDaCombo,
                    idStrutCorrente);

            String errorMessageTipiSerie = "TIPISERIE;";
            if (!existRegistriDaImportareConFlTipoSerieMultAlzato) {
                // Eseguo, se esistono associazioni registro/tipo ud, la creazione dei tipi serie standard
                if (tipoUnitaDoc.getDecTipoUnitaDocAmmessos() != null) {
                    for (DecTipoUnitaDocAmmesso decTipoUnitaDocAmmesso : tipoUnitaDoc.getDecTipoUnitaDocAmmessos()) {
                        try {
                            BigDecimal idRegistroUnitaDoc = new BigDecimal(
                                    decTipoUnitaDocAmmesso.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc());
                            BigDecimal idTipoUnitaDoc = new BigDecimal(
                                    decTipoUnitaDocAmmesso.getDecTipoUnitaDoc().getIdTipoUnitaDoc());
                            tipoSerieEjb.createTipoSerieStandardDaRegistroOTipoUdNewTx(param, idRegistroUnitaDoc,
                                    idTipoUnitaDoc);
                        } catch (ParerUserError e) {
                            if (!errorMessageTipiSerie.contains(e.getDescription())) {
                                errorMessageTipiSerie = errorMessageTipiSerie + e.getDescription() + ";";
                            }
                        }
                    }
                }
            }

            if (!errorMessageTipiSerie.equals("TIPISERIE;")) {
                strutErrorTipiSerie.put(nmStrutCorrente, errorMessageTipiSerie);
            }

            // Controllo per fl_tipo_serie_mult
            List<DecTipoUnitaDocAmmesso> associazioniRegistroTipoUd = tipoUnitaDoc.getDecTipoUnitaDocAmmessos();
            for (DecTipoUnitaDocAmmesso associazione : associazioniRegistroTipoUd) {
                if (tipoSerieEjb.multipleDecRegistroUnitaDocInTipiSerie(
                        new BigDecimal(associazione.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()))) {
                    associazione.getDecRegistroUnitaDoc().setFlTipoSerieMult("1");
                } else {
                    associazione.getDecRegistroUnitaDoc().setFlTipoSerieMult("0");
                }
            }

            // Replica su IAM
            try {
                if (organizDaReplic != null) {
                    me.replicateToIam(organizDaReplic);
                }
            } catch (ParerUserError e) {
                logger.error("Errore durante la replica su IAM ", e);
            }
        }

        // Preparo il report
        Object[] report = new Object[4];
        report[0] = strutErrorGenerico;
        report[1] = strutErrorSuModello;
        report[2] = strutErrorTipiSerie;
        report[3] = strutErrorSisMigr;
        return report;
    }

    // TransactionAttribute
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Object[] confermaImportazione(LogParam param, BigDecimal idStrutCorrente, UUID uuid,
            String nmTipoUnitaDocSelezionataDaCombo, String nmTipoStrutUnitaDocSelezionataDaCombo,
            String importareRegistri, String importareCriteri, String importareSistemiMigraz,
            String importareFormatiComponente) throws Exception {
        List<CoppiaRegistri> registriImportati = new ArrayList<>();

        Set<BigDecimal> registriDaLoggare = new HashSet<>();
        Set<BigDecimal> tipiDocumentoDaLoggare = new HashSet<>();
        Set<BigDecimal> tipiStrutturaDocumentoDaLoggare = new HashSet<>();
        Set<BigDecimal> formatiDaLoggare = new HashSet<>();
        Set<BigDecimal> tipiRappresentazioneComponenteDaLoggare = new HashSet<>();
        Set<BigDecimal> criteriRaggruppamentoDaLoggare = new HashSet<>();

        // Ricavo la struttura presente nell'XML
        OrgStrut strutExp = strutCache.getOrgStrut(uuid);
        // Ricavo il tipoUnitaDoc presente nell'XML (quello selezionato)
        DecTipoUnitaDoc tipoUnitaDocExp = getDecTipoUnitaDocExpSelected(strutExp, nmTipoUnitaDocSelezionataDaCombo);

        /*
         * Dichiaro il tipo unità documentaria che verrà "decorato" e quindi importato a seguito dei vari controlli
         */
        /////////////////////////////
        // IMPORTA TIPO UNITA' DOC //
        /////////////////////////////
        Object[] obj = importaTipoUd(idStrutCorrente, tipoUnitaDocExp);
        DecTipoUnitaDoc tipoUnitaDoc = (DecTipoUnitaDoc) obj[0];

        //////////////////////
        // IMPORTA REGISTRI //
        //////////////////////
        if (importareRegistri != null && importareRegistri.equals("1")) {
            registriImportati = importaRegistro(idStrutCorrente, tipoUnitaDocExp, tipoUnitaDoc, registriDaLoggare);
        }

        // Controllo sui ModelliTipiSerie
        checkAndSetModelliTipiSerieStrutturaPerImportaParametri(idStrutCorrente, tipoUnitaDocExp, tipoUnitaDoc,
                registriImportati);

        /////////////////////////////////
        // IMPORTA FORMATI COMPONENTE ///
        /////////////////////////////////
        // Il sistema aggiorna i formati ammessi nel componente importato
        if (importareFormatiComponente != null && importareFormatiComponente.equals("1")) {
            OrgStrut strut = struttureHelper.findById(OrgStrut.class, idStrutCorrente);
            // punto 46 e 47: DEC_TIPO_STRUT_DOC e tipi comp doc
            strut.setDecTipoStrutDocs(copiaStruttureEjb.determinaDecTipoStrutDocsPerImportaParametri(strutExp,
                    new Date(), true, true, strut, true));

            // EVO 27925: Allineo nei tipi componente (flag gestiti, idonei, deprecati) eventualmente anche eventuali
            // formati che non erano presenti nella struttura dell'XML di import
            /* In base ai flag spuntati, inserisco i formati ammessi */
            List<DecTipoStrutDoc> tipoStrutDocList = tipoStrutDocHelper.getDecTipoStrutDocList(idStrutCorrente, true);

            if (tipoStrutDocList != null) {
                for (DecTipoStrutDoc tipoStrutDoc : tipoStrutDocList) {
                    if (tipoStrutDoc.getDecTipoCompDocs() != null) {
                        for (DecTipoCompDoc tipoCompDoc : tipoStrutDoc.getDecTipoCompDocs()) {
                            formatoFileDocEjb.gestisciFormatiAmmessi(BigDecimal.valueOf(tipoCompDoc.getIdTipoCompDoc()),
                                    tipoCompDoc.getFlGestiti(), tipoCompDoc.getFlIdonei(),
                                    tipoCompDoc.getFlDeprecati());
                        }
                    }
                }
            }

        }

        ///////////////////////////////////////
        // IMPORTA TIPO STRUTTURA UNITA' DOC //
        ///////////////////////////////////////
        // Se ne ho selezionato una in particolare, importo solo quella
        List<DecTipoStrutUnitaDoc> decTipoStrutUnitaDocExpList = new ArrayList<>();
        if (nmTipoStrutUnitaDocSelezionataDaCombo != null) {
            decTipoStrutUnitaDocExpList
                    .add(getDecTipoStrutUnitaDocExpSelected(tipoUnitaDocExp, nmTipoStrutUnitaDocSelezionataDaCombo));
        } // Altrimenti importo tutte quelle dell'XML
        else {
            decTipoStrutUnitaDocExpList = tipoUnitaDocExp.getDecTipoStrutUnitaDocs();
        }
        List<CoppiaTipiDoc> tipiDocImportati = importaTipoStrutUnitaDoc(idStrutCorrente, decTipoStrutUnitaDocExpList,
                tipoUnitaDoc, (boolean) obj[1], tipiDocumentoDaLoggare, tipiStrutturaDocumentoDaLoggare,
                formatiDaLoggare, tipiRappresentazioneComponenteDaLoggare, importareFormatiComponente);

        ////////////////////
        // IMPORTA REGOLE //
        ////////////////////
        importaRegole(idStrutCorrente, tipoUnitaDocExp, tipoUnitaDoc);

        ///////////////////////////////////////
        // IMPORTA CRITERI DI RAGGRUPPAMENTO //
        ///////////////////////////////////////
        // Criteri di raggruppamento contenenti il tipo ud importato,
        // i registri (se importati) e i tipi doc
        if (importareCriteri != null && importareCriteri.equals("1")) {
            importaCriteriTipoUd(idStrutCorrente, tipoUnitaDocExp, registriImportati, tipiDocImportati,
                    criteriRaggruppamentoDaLoggare);
        }

        ////////////////////////////////
        // IMPORTA SISTEMI MIGRAZIONE //
        ////////////////////////////////
        List<String> nmSisMigrNoImp = null;
        if (importareSistemiMigraz != null && importareSistemiMigraz.equals("1")) {
            nmSisMigrNoImp = importaSistemiMigraz(idStrutCorrente, strutExp);
        }

        /////////////////////
        // COMMITT TIPO UD //
        /////////////////////
        boolean tipoUdExists = (boolean) obj[1];
        if (!tipoUdExists) {
            struttureHelper.insertEntity(tipoUnitaDoc, true);
        } else {
            struttureHelper.getEntityManager().flush();
        }

        // Log di tutti i Registri
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_REGISTRO, registriDaLoggare,
                param.getNomePagina());
        // Log di tutti i Tipi documento
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO, tipiDocumentoDaLoggare,
                param.getNomePagina());
        // Log di tutti i Tipi Struttura Documento
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO,
                tipiStrutturaDocumentoDaLoggare, param.getNomePagina());
        // Logging del Tipo Unità  Doc
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA,
                new BigDecimal(tipoUnitaDoc.getIdTipoUnitaDoc()), param.getNomePagina());
        // Logging dei Formati File
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_FORMATO_AMMESSO, formatiDaLoggare,
                param.getNomePagina());
        // Logging dei Tipi Rappresentazioen Componente
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_RAPPRESENTAZIONE_COMPONENTE,
                tipiRappresentazioneComponenteDaLoggare, param.getNomePagina());
        // Logging dei Criteri di raggruppamento
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                criteriRaggruppamentoDaLoggare, param.getNomePagina());

        // Calcolo servizi erogati
        boolean esitoOK = calcoloServiziErogati(tipoUnitaDoc.getOrgStrut().getIdEnteConvenz());
        if (!esitoOK) {
            throw new ParerUserError(
                    "Errore durante il calcolo dei servizi erogati a seguito di importa parametri</br>");
        }

        /*
         * Eseguo la replica organizzazioni in ogni caso perché posso o meno aver inserito nuovi registri e/o tipiDoc ma
         * di sicuro ho inserito un nuovo tipo ud
         */
        IamOrganizDaReplic organizDaReplic = context.getBusinessObject(StruttureEjb.class)
                .insertStrutIamOrganizDaReplic(tipoUnitaDoc.getOrgStrut(), ApplEnum.TiOperReplic.MOD);

        Object[] result = new Object[2];
        result[0] = organizDaReplic;
        result[1] = nmSisMigrNoImp;

        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Object[] eseguiImportTipoFascicolo(LogParam param, Map<BigDecimal, String> struttureDaElaborare, UUID uuid,
            String nmTipoFascicoloSelezionatoDaMulti, BigDecimal aaTipoFascicoloSelezionatoDaCombo,
            String sovrascriviPeriodi) {

        // Preparo le strutture dati per il report
        Set<String> strutErrorGenerico = new HashSet<>();

        // Scorro le strutture coinvolte nell'importa parametri (parte relativa ai tipi fascicolo)
        for (Map.Entry<BigDecimal, String> entry : struttureDaElaborare.entrySet()) {
            BigDecimal idStrutCorrente = entry.getKey();
            String nmStrutCorrente = entry.getValue();

            StruttureEjb me = context.getBusinessObject(StruttureEjb.class);
            try {
                // L'import viene eseguito in una nuova transazione
                me.confermaImportazioneTipoFascicolo(param, idStrutCorrente, uuid, nmTipoFascicoloSelezionatoDaMulti,
                        aaTipoFascicoloSelezionatoDaCombo, sovrascriviPeriodi.equals("1"));

            } catch (Exception e) {
                strutErrorGenerico.add(nmStrutCorrente);
            }
        }

        // Preparo il report
        Object[] report = new Object[1];
        report[0] = strutErrorGenerico;
        return report;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Object[] confermaImportazioneTipoFascicolo(LogParam param, BigDecimal idStrutCorrente, UUID uuid,
            String nmTipoFascicoloSelezionatoDaMulti, BigDecimal aaTipoFascicoloSelezionatoDaCombo,
            boolean sovrascriviPeriodo) throws ParerUserError {

        Set<BigDecimal> criteriRaggruppamentoFascDaLoggare = new HashSet<>();

        // Ricavo la struttura presente nell'XML
        OrgStrut strutExp = strutCache.getOrgStrut(uuid);

        // Ricavo il tipoFascicolo presente nell'XML (quello selezionato)
        DecTipoFascicolo tipoFascicoloExp = getDecTipoFascicoloExpSelected(strutExp, nmTipoFascicoloSelezionatoDaMulti);
        Object[] result = new Object[1];

        // IN ATTESA DI SVILUPPO CONTROLLI SOVRASCRIVI PERIODI DI VALIDITA':
        // CONTROLLO TEMPORANEO PER VERIFICARE CHE IL TIPO FASCICOLO NON ESISTA:
        // SE ESISTE NON IMPORTO NULLA, AL MOMENTO, VISTO CHE HO TOLTO IL CHECK SOVRASCITI PERIODO

        // Cerco su DB il nmTipoFascicolo selezionato dalla combo
        DecTipoFascicolo tipoFascicoloDB = tipoFascicoloHelper
                .getDecTipoFascicoloByName(tipoFascicoloExp.getNmTipoFascicolo(), idStrutCorrente);

        if (tipoFascicoloDB == null) {

            /*
             * Dichiaro il tipo fascicolo che verrà "decorato" e quindi importato a seguito dei vari controlli
             */
            ////////////////////////////
            // IMPORTA TIPO FASCICOLO //
            ////////////////////////////
            Object[] obj = importaTipoFascicolo(idStrutCorrente, tipoFascicoloExp);
            DecTipoFascicolo tipoFascicolo = (DecTipoFascicolo) obj[0];

            ////////////////////////////////////
            // IMPORTA PERIODO TIPO FASCICOLO //
            ////////////////////////////////////
            // Se ne ho selezionato uno in particolare, importo solo quello
            List<DecAaTipoFascicolo> aaTipoFascicoloExpList = new ArrayList<>();
            if (aaTipoFascicoloSelezionatoDaCombo != null) {
                aaTipoFascicoloExpList
                        .add(getDecAaTipoFascicoloExpSelected(tipoFascicoloExp, aaTipoFascicoloSelezionatoDaCombo));
            } // Altrimenti importo tutti quelli dell'XML relativi al tipo fascicolo che sto considerando
            else {
                aaTipoFascicoloExpList = tipoFascicoloExp.getDecAaTipoFascicolos();
            }
            importaAaTipoFascicolo(idStrutCorrente, aaTipoFascicoloExpList, tipoFascicolo, (boolean) obj[1],
                    sovrascriviPeriodo);

            ////////////////////////////////////////////
            // IMPORTA CRITERI DI RAGGRUPPAMENTO FASC //
            ////////////////////////////////////////////
            importaCriteriRaggrFasc(idStrutCorrente, tipoFascicoloExp, tipoFascicolo,
                    criteriRaggruppamentoFascDaLoggare);

            //////////////////////
            // COMMITT TIPO FASC //
            //////////////////////
            boolean tipoFascExists = (boolean) obj[1];
            if (!tipoFascExists) {
                struttureHelper.insertEntity(tipoFascicolo, true);
            } else {
                struttureHelper.getEntityManager().flush();
            }

            /*
             * Eseguo la replica organizzazioni in ogni caso perché ho inserito un nuovo tipo fascicolo
             */
            IamOrganizDaReplic organizDaReplic = context.getBusinessObject(StruttureEjb.class)
                    .insertStrutIamOrganizDaReplic(tipoFascicolo.getOrgStrut(), ApplEnum.TiOperReplic.MOD);

            result[0] = organizDaReplic;

        }

        return result;
    }

    /**
     * Esegue l'importazione del solo Tipo fascicolo. Viene verificata l'esistenza dello stesso su DB. Infine viene
     * eseguito l'import dell'XSD relativo al tipoFascicolo
     *
     * @param idStrutCorrente
     *            la struttura in cui mi trovo e nella quale sto facendo l'import
     * @param tipoFascicoloExp
     *            il tipo fascicolo selezionato nella combo
     *
     * @return un array di object di lunghezza 2 contenente nel primo campo l'oggetto tipoFascicolo "decorato" dopo la
     *         fase di importaTipoFascicolo e nel secondo il valore booleano relativo al fatto che il tipoFascicolo
     *         fosse gi\u00E0 presente su DB
     *
     * @throws ParerUserError
     *             errore generico
     */
    public Object[] importaTipoFascicolo(BigDecimal idStrutCorrente, DecTipoFascicolo tipoFascicoloExp)
            throws ParerUserError {
        Object[] obj = new Object[2];
        // Informazione che mi dice se il tipo fascicolo \u00E8 gi\u00E0 presente nel DB
        obj[1] = true;

        // Cerco su DB il nmTipoFascicolo selezionato dalla combo
        DecTipoFascicolo tipoFascicolo = tipoFascicoloHelper
                .getDecTipoFascicoloByName(tipoFascicoloExp.getNmTipoFascicolo(), idStrutCorrente);

        // Se TIPO FASCICOLO NON \u00E8 gi\u00E0 PRESENTE nella struttura
        if (tipoFascicolo == null) {
            // Mi appunto l'informazione che il tipoFascicolo non era gi\u00E0 presente
            obj[1] = false;

            // Inserisco il tipo fascicolo "nuovo"
            tipoFascicolo = inserisciTipoFascicolo(idStrutCorrente, tipoFascicoloExp);
        }

        obj[0] = tipoFascicolo;
        return obj;
    }

    /**
     * Esegue l'importazione della sola tipologia unit\u00E0 documentaria. Viene verificata l'esistenza della stessa su
     * DB e della categoria associata. Infine viene eseguito l'import dell'XSD relativo al tipoUd
     *
     * @param idStrutCorrente
     *            la struttura in cui mi trovo e nella quale sto facendo l'import
     * @param tipoUnitaDocExp
     *            il tipo unit\u00E0 documentaria selezionato nella combo
     *
     * @return un array di object di lunghezza 2 contenente nel primo campo l'oggetto tipoUnitaDoc "decorato" dopo la
     *         fase di importaTipoUd e nel secondo il valore booleano relativo al fatto che il tipoUnitaDoc fosse
     *         gi\u00E0 presente su DB
     *
     * @throws ParerUserError
     *             errore generico
     */
    public Object[] importaTipoUd(BigDecimal idStrutCorrente, DecTipoUnitaDoc tipoUnitaDocExp) throws ParerUserError {
        Object[] obj = new Object[2];
        // Informazione che mi dice se il tipo ud \u00E8 gi\u00E0 presente nel DB
        obj[1] = true;

        // Cerco su DB il nmTipoUnitaDoc selezionato dalla combo
        DecTipoUnitaDoc tipoUnitaDoc = tipoUnitaDocHelper.getDecTipoUnitaDocByName(tipoUnitaDocExp.getNmTipoUnitaDoc(),
                idStrutCorrente);

        // Se TIPO UNITA' DOC NON \u00E8 gi\u00E0 PRESENTE nella struttura
        if (tipoUnitaDoc == null) {
            // Mi appunto l'informazione che il tipoUd non era gi\u00E0 presente
            obj[1] = false;

            // Inserisco o recupero la categoria
            DecCategTipoUnitaDoc categTipoUnitaDoc = gestisciCategoria(tipoUnitaDocExp);

            DecVCalcTiServOnTipoUd calcTiServOnTipoUd = tipoUnitaDocHelper.getDecVCalcTiServOnTipoUd(idStrutCorrente,
                    new BigDecimal(categTipoUnitaDoc.getIdCategTipoUnitaDoc()), "CLASSE_ENTE");

            DecVCalcTiServOnTipoUd calcTiServOnTipoUd2 = tipoUnitaDocHelper.getDecVCalcTiServOnTipoUd(idStrutCorrente,
                    new BigDecimal(categTipoUnitaDoc.getIdCategTipoUnitaDoc()), "NO_CLASSE_ENTE");
            // Inserisco il tipo ud "nuovo"
            tipoUnitaDoc = inserisciTipoUd(idStrutCorrente, tipoUnitaDocExp, categTipoUnitaDoc, calcTiServOnTipoUd,
                    calcTiServOnTipoUd2);
        }

        // Importa XSD
        importaXsdTipoUd(idStrutCorrente, tipoUnitaDocExp, tipoUnitaDoc);

        // Importa valori parametri
        importaValoreParamApplic(tipoUnitaDocExp, tipoUnitaDoc);

        obj[0] = tipoUnitaDoc;
        return obj;
    }

    public void importaValoreParamApplic(DecTipoUnitaDoc tipoUnitaDocExp, DecTipoUnitaDoc tipoUnitaDoc) {
        // Recupero la lista dei valori dei parametri sul tipo ud
        List<AplValoreParamApplic> valoreParamApplicExpList = tipoUnitaDocExp.getAplValoreParamApplics();
        // Per ogni valore APL_VALORE_PARAM_APPLIC
        for (AplValoreParamApplic valoreParamApplicExp : valoreParamApplicExpList) {

            // Controlla se esiste il valore parametro del tipo unit\u00E0 doc su DB
            String dsValoreParamApplic = configurationHelper.getAplValoreParamApplic(
                    valoreParamApplicExp.getAplParamApplic().getNmParamApplic(), TiApparType.TIPO_UNITA_DOC.name(),
                    null, null, BigDecimal.valueOf(tipoUnitaDoc.getIdTipoUnitaDoc()), null);

            // Se il valore NON \u00E8 presente su DB...
            if (dsValoreParamApplic == null) {
                ///////////////////////////////////
                // INSERISCI VALORE PARAM APPLIC //
                ///////////////////////////////////
                inserisciValoreParamApplic(valoreParamApplicExp.getAplParamApplic().getNmParamApplic(),
                        valoreParamApplicExp.getDsValoreParamApplic(), tipoUnitaDoc);
            }
        }
    }

    /**
     * Esegue l'importazione del registro. Viene verificata l'esistenza dello stesso su DB e della relazione con il tipo
     * UD (mediante la tabella DEC_TIPO_UNITA_DOC_AMMESSO)
     *
     * @param idStrutCorrente
     *            la struttura in cui mi trovo e nella quale sto facendo l'import
     * @param tipoUnitaDoc
     *            l'oggetto tipoUnitaDoc prima della fase di importaRegistro
     * @param tipoUnitaDocExp
     *            i tipiUnitaDoc preso da XML @return, l'elenco dei registri importati ex novo
     * @param registriDaLoggare
     *            i registri da loggare
     *
     * @return lista elementi di tipo CoppiaRegistri
     */
    public List<CoppiaRegistri> importaRegistro(BigDecimal idStrutCorrente, DecTipoUnitaDoc tipoUnitaDocExp,
            DecTipoUnitaDoc tipoUnitaDoc, Set<BigDecimal> registriDaLoggare) {
        List<DecTipoUnitaDocAmmesso> tipoUnitaDocAmmmessoExpList = tipoUnitaDocExp.getDecTipoUnitaDocAmmessos();
        List<CoppiaRegistri> registriImportati = new ArrayList<>();
        // Partendo dai DecTipoUnitaDocAmmesso presenti nell'XML...
        for (DecTipoUnitaDocAmmesso decTipoUnitaDocAmmessoExp : tipoUnitaDocAmmmessoExpList) {
            // Entity DecRegistroUnitaDoc da "decorare"
            DecRegistroUnitaDoc registroUnitaDoc;

            // ... recupero ogni registro presente nell'XML (tramite il tag cdRegistroKeyUnitaDoc)
            // e ne verifico l'esistenza su DB (tabella DEC_REGISTRO_UNITA_DOC)
            DecRegistroUnitaDoc registroUnitaDocExp = decTipoUnitaDocAmmessoExp.getDecRegistroUnitaDoc();
            String cdRegistroUnitaDocExp = registroUnitaDocExp.getCdRegistroUnitaDoc();

            boolean esisteRegistroUd = registroHelper.existsRegistroUnitaDoc(idStrutCorrente, cdRegistroUnitaDocExp);

            // Se il registro NON \u00E8 presente, lo creo e lo salvo su DB con relativi anni e parti numero
            if (!esisteRegistroUd) {
                registroUnitaDoc = inserisciRegistroUd(idStrutCorrente, registroUnitaDocExp);
                registriImportati.add(new CoppiaRegistri(registroUnitaDoc, registroUnitaDocExp));
            } // altrimenti mi limito a recuperarlo gestendo anni e parti
            else {
                registroUnitaDoc = recuperaRegistroUd(idStrutCorrente, registroUnitaDocExp);
            }
            // Aggiunge l'ID del registro da loggare alla fine
            registriDaLoggare.add(new BigDecimal(registroUnitaDoc.getIdRegistroUnitaDoc()));

            // Verifico se il registro \u00E8 inserito anche in DEC_TIPO_UNITA_DOC_AMMESSO (relazione tipoUd-registro)
            String nmTipoUnitaDocExp = tipoUnitaDoc.getNmTipoUnitaDoc();
            DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso = tipoUnitaDocHelper
                    .getDecTipoUnitaDocAmmessoByName(idStrutCorrente, nmTipoUnitaDocExp, cdRegistroUnitaDocExp);

            // Se il tipoUnitaDocAmmesso non \u00E8 presente, lo creo e lo salvo su DB
            if (tipoUnitaDocAmmesso == null) {
                inserisciTipoUdAmmesso(tipoUnitaDoc, registroUnitaDoc);
            }
        }
        return registriImportati;
    }

    public List<String> importaSistemiMigraz(BigDecimal idStrutCorrente, OrgStrut strutExp) {
        List<OrgUsoSistemaMigraz> usoSistemaMigrazExpList = strutExp.getOrgUsoSistemaMigrazs();
        List<String> nmSistemiMigrazNoImpList = new ArrayList<>();
        // Partendo dagli OrgUsoSistemaMigraz presenti nell'XML...
        for (OrgUsoSistemaMigraz usoSistemaMigrazExp : usoSistemaMigrazExpList) {
            // Entity DecRegistroUnitaDoc da "decorare"
            OrgUsoSistemaMigraz usoSis;

            // ... recupero ogni sistema di migrazione presente nell'XML (tramite il tag nmSistemaMigraz)
            // e ne verifico l'esistenza su DB (tabella APL_SISTEMA_MIGRAZ)
            AplSistemaMigraz sistemaMigrazExp = usoSistemaMigrazExp.getAplSistemaMigraz();
            String nmSistemaMigrazExp = sistemaMigrazExp.getNmSistemaMigraz();

            AplSistemaMigraz sistemaMigraz = sistemaMigrazioneHelper.getAplSistemaMigrazByName(nmSistemaMigrazExp);

            // Se il sistema di migrazione NON \u00E8 presente, non proseguo e riporto un messaggio
            if (sistemaMigraz == null) {
                nmSistemiMigrazNoImpList.add(nmSistemaMigrazExp);
            } // altrimenti inserisco la corrispondenza con la struttura in OrgUsoSistemaMigraz
            else {
                OrgStrut strutturaCorrente = tipoStrutDocHelper.findById(OrgStrut.class, idStrutCorrente);
                usoSis = copiaStruttureEjb.cercaOrgUsoSistemaMigraz(sistemaMigraz, strutturaCorrente);

                if (usoSis == null) {
                    usoSis = new OrgUsoSistemaMigraz();
                    usoSis.setAplSistemaMigraz(sistemaMigraz);
                    usoSis.setOrgStrut(strutturaCorrente);

                    // Se ho inserito un'associazione struttura-sistema di migrazione, inserisco i dati spec
                    // UD
                    List<DecXsdDatiSpec> listUd = datiSpecHelper.retrieveDecXsdDatiSpecList(
                            BigDecimal.valueOf(strutExp.getIdStrut()), CostantiDB.TipiUsoDatiSpec.MIGRAZ.name(),
                            CostantiDB.TipiEntitaSacer.UNI_DOC.name(), nmSistemaMigrazExp);

                    // DOC
                    List<DecXsdDatiSpec> listDoc = datiSpecHelper.retrieveDecXsdDatiSpecList(
                            BigDecimal.valueOf(strutExp.getIdStrut()), CostantiDB.TipiUsoDatiSpec.MIGRAZ.name(),
                            CostantiDB.TipiEntitaSacer.DOC.name(), nmSistemaMigrazExp);

                    // COMP
                    List<DecXsdDatiSpec> listComp = datiSpecHelper.retrieveDecXsdDatiSpecList(
                            BigDecimal.valueOf(strutExp.getIdStrut()), CostantiDB.TipiUsoDatiSpec.MIGRAZ.name(),
                            CostantiDB.TipiEntitaSacer.COMP.name(), nmSistemaMigrazExp);

                    Set<String> setStr = new HashSet<>();
                    for (DecXsdDatiSpec ds : listUd) {

                        DecXsdDatiSpec dsNew = copiaStruttureEjb.createDecXsdDatiSpec(ds, strutturaCorrente);
                        if (!setStr.add(dsNew.getCdVersioneXsd())) {
                            tipoUnitaDocHelper.getEntityManager().persist(dsNew);
                        }
                    }
                    for (DecXsdDatiSpec ds : listDoc) {
                        DecXsdDatiSpec dsNew = copiaStruttureEjb.createDecXsdDatiSpec(ds, strutturaCorrente);
                        if (!setStr.add(dsNew.getCdVersioneXsd())) {
                            tipoUnitaDocHelper.getEntityManager().persist(dsNew);
                        }
                    }
                    for (DecXsdDatiSpec ds : listComp) {
                        DecXsdDatiSpec dsNew = copiaStruttureEjb.createDecXsdDatiSpec(ds, strutturaCorrente);
                        if (!setStr.add(dsNew.getCdVersioneXsd())) {
                            tipoUnitaDocHelper.getEntityManager().persist(dsNew);
                        }
                    }

                    tipoUnitaDocHelper.getEntityManager().persist(usoSis);
                    tipoUnitaDocHelper.getEntityManager().flush();

                    strutturaCorrente.getOrgUsoSistemaMigrazs().add(usoSis);
                }

            }
        }
        return nmSistemiMigrazNoImpList;
    }

    private class CoppiaRegistri {

        DecRegistroUnitaDoc registroUnitaDoc;
        DecRegistroUnitaDoc registroUnitaDocExp;

        public CoppiaRegistri(DecRegistroUnitaDoc registroUnitaDoc, DecRegistroUnitaDoc registroUnitaDocExp) {
            this.registroUnitaDoc = registroUnitaDoc;
            this.registroUnitaDocExp = registroUnitaDocExp;
        }

        public DecRegistroUnitaDoc getRegistroUnitaDoc() {
            return registroUnitaDoc;
        }

        public void setRegistroUnitaDoc(DecRegistroUnitaDoc registroUnitaDoc) {
            this.registroUnitaDoc = registroUnitaDoc;
        }

        public DecRegistroUnitaDoc getRegistroUnitaDocExp() {
            return registroUnitaDocExp;
        }

        public void setRegistroUnitaDocExp(DecRegistroUnitaDoc registroUnitaDocExp) {
            this.registroUnitaDocExp = registroUnitaDocExp;
        }
    }

    /**
     * Esegue l'importazione del tipo struttura unit\u00E0 documentaria.
     *
     * @param idStrutCorrente
     *            la struttura in cui mi trovo e nella quale sto facendo l'import
     * @param tipoStrutUnitaDocExpList
     *            la lista di tipi struttura ud da importare sui quali effettuare i controlli
     * @param tipoUnitaDoc
     *            l'oggetto tipo ud in fase di decorazione
     * @param tipoUnitaDocGiaPresente
     *            l'informazione se il tipoUnitaDoc fosse gi\u00E0 presente su DB
     * @param tipiDocumetoDaLoggare
     *            tipo documento da loggare
     * @param tipiStrutturaDocumetoDaLoggare
     *            tipo struttura documento da loggare
     * @param formatiDaLoggare
     *            formati da loggare
     * @param tipiRappresentazioneComponenteDaLoggare
     *            rappresentazione componente da loggare
     *
     * @param includereFormatiComponente
     *            flag
     *
     * @return l'elenco dei tipi documento importati ex novo
     */
    public List<CoppiaTipiDoc> importaTipoStrutUnitaDoc(BigDecimal idStrutCorrente,
            List<DecTipoStrutUnitaDoc> tipoStrutUnitaDocExpList, DecTipoUnitaDoc tipoUnitaDoc,
            boolean tipoUnitaDocGiaPresente, Set<BigDecimal> tipiDocumetoDaLoggare,
            Set<BigDecimal> tipiStrutturaDocumetoDaLoggare, Set<BigDecimal> formatiDaLoggare,
            Set<BigDecimal> tipiRappresentazioneComponenteDaLoggare, String includereFormatiComponente) {
        List<CoppiaTipiDoc> tipiDocImportati = new ArrayList<>();
        // Per ogni TIPO STRUTTURA UNITA' DOCUMENTARIA
        for (DecTipoStrutUnitaDoc tipoStrutUnitaDocExp : tipoStrutUnitaDocExpList) {
            // Entity DecTipoStrutUnitaDoc da "decorare"
            DecTipoStrutUnitaDoc tipoStrutUnitaDoc;

            // Se il tipo unita doc da importare NON era presente nella struttura
            if (!tipoUnitaDocGiaPresente) {
                // Importo il tipo struttura unita doc
                tipoStrutUnitaDoc = inserisciTipoStrutUnitaDoc(tipoStrutUnitaDocExp, tipoUnitaDoc);
            } // Se invece il tipo unita doc NON \u00E8 stato importato (in quanto gi\u00E0 presente su DB)
              // ricavo, se presente, il relativo tipoStrutUnitaDoc
            else {
                tipoStrutUnitaDoc = tipoStrutDocHelper.getDecTipoStrutUnitaDocByName(idStrutCorrente,
                        tipoUnitaDoc.getNmTipoUnitaDoc(), tipoStrutUnitaDocExp.getNmTipoStrutUnitaDoc());
                // Se non presente, lo importo
                if (tipoStrutUnitaDoc == null) {
                    // Importo il tipo struttura unita doc
                    tipoStrutUnitaDoc = inserisciTipoStrutUnitaDoc(tipoStrutUnitaDocExp, tipoUnitaDoc);
                }
            }

            // Per ogni DEC_TIPO_DOC_AMMESSO dall'XML
            for (DecTipoDocAmmesso tipoDocAmmessoExp : tipoStrutUnitaDocExp.getDecTipoDocAmmessos()) {
                DecTipoDoc tipoDoc = gestisciTipoDoc(idStrutCorrente, tipoDocAmmessoExp, tipoStrutUnitaDoc,
                        tipiDocImportati);
                // Per ogni DEC_TIPO_STRUT_DOC_AMMESSO
                // Memorizzo tra i tipiDoc da loggare alla fine
                tipiDocumetoDaLoggare.add(new BigDecimal(tipoDoc.getIdTipoDoc()));
                for (DecTipoStrutDocAmmesso tipoStrutDocAmmessoExp : tipoDocAmmessoExp.getDecTipoDoc()
                        .getDecTipoStrutDocAmmessos()) {
                    gestisciTipoStrutDoc(idStrutCorrente, tipoStrutDocAmmessoExp, tipoDoc,
                            tipiStrutturaDocumetoDaLoggare, tipiRappresentazioneComponenteDaLoggare);
                }
            } // End For DecTipoDocAmmessoExp

            // Per ogni metadato specifico ammesso per il tipo struttura UD
            for (DecTipoStrutUdXsd tipoStrutUdXsdExp : tipoStrutUnitaDocExp.getDecTipoStrutUdXsds()) {

                DecXsdDatiSpec xsdDatiSpecExp = tipoStrutUdXsdExp.getDecXsdDatiSpec();

                // Controlla se esiste già un metadato per il tipo struttura UD su DB
                DecXsdDatiSpec xsdDatiSpec = datiSpecHelper.getDecXsdDatiSpec(idStrutCorrente,
                        xsdDatiSpecExp.getTiUsoXsd(), xsdDatiSpecExp.getTiEntitaSacer(),
                        tipoStrutUnitaDocExp.getDecTipoUnitaDoc().getNmTipoUnitaDoc(), null, null, null,
                        xsdDatiSpecExp.getNmSistemaMigraz(), xsdDatiSpecExp.getCdVersioneXsd());

                // Se il metadato NON è presente nella struttura: il record è importato.
                if (xsdDatiSpec == null) {
                    xsdDatiSpec = inserisciXsdDatiSpec(idStrutCorrente, xsdDatiSpecExp, tipoUnitaDoc, null, null);
                }

                // Controllo se esiste già il record nella tabella DEC_TIPO_STRT_UD_SIS_VERS, ed in caso lo inserisco
                String nmTipoStrutUnitaDoc = tipoStrutUdXsdExp.getDecTipoStrutUnitaDoc().getNmTipoStrutUnitaDoc();
                DecTipoStrutUdXsd tipoStrutUdXsd = datiSpecHelper.getDecTipoStrutUdXsdByName(idStrutCorrente,
                        nmTipoStrutUnitaDoc, xsdDatiSpecExp.getTiUsoXsd(), xsdDatiSpecExp.getTiEntitaSacer(),
                        tipoStrutUnitaDocExp.getDecTipoUnitaDoc().getNmTipoUnitaDoc(),
                        xsdDatiSpecExp.getNmSistemaMigraz(), xsdDatiSpecExp.getCdVersioneXsd());

                if (tipoStrutUdXsd == null) {
                    inserisciTipoStrutUdXsd(tipoStrutUnitaDoc, xsdDatiSpec);
                }

            } // End For DecTipoDocAmmessoExp

            // Per ogni sistema versante ammesso per il tipo struttura UD
            for (DecTipoStrutUdSisVer tipoStrutUdSisVersExp : tipoStrutUnitaDocExp.getDecTipoStrutUdSisVers()) {

                // Controlla se esiste già un sistema versante del tipo in questione
                AplSistemaVersante sistemaVersante = tipoUnitaDocHelper.getAplSistemaVersanteByName(
                        tipoStrutUdSisVersExp.getAplSistemaVersante().getNmSistemaVersante());

                // Se il sistema versante NON è presente nella struttura: il record è importato.
                if (sistemaVersante == null) {
                    sistemaVersante = inserisciSistemaVersante(tipoStrutUdSisVersExp.getAplSistemaVersante());
                }

                // Controllo se esiste già il record nella tabella DEC_TIPO_STRT_UD_SIS_VERS, ed in caso lo inserisco
                String nmTipoUnitaDoc = tipoStrutUdSisVersExp.getDecTipoStrutUnitaDoc().getDecTipoUnitaDoc()
                        .getNmTipoUnitaDoc();
                String nmTipoStrutUnitaDoc = tipoStrutUdSisVersExp.getDecTipoStrutUnitaDoc().getNmTipoStrutUnitaDoc();
                String nmSistemaVersante = tipoStrutUdSisVersExp.getAplSistemaVersante().getNmSistemaVersante();

                DecTipoStrutUdSisVer tipoStrutUdSisVers = tipoUnitaDocHelper.getDecTipoStrutUdSisVersByName(
                        idStrutCorrente, nmTipoUnitaDoc, nmTipoStrutUnitaDoc, nmSistemaVersante);

                if (tipoStrutUdSisVers == null) {
                    inserisciTipoStrutUdSisVer(tipoStrutUnitaDoc, sistemaVersante);
                }
            } // End For DecTipoStrutUdSisVers

            // Per ogni registro ammesso per il tipo struttura UD
            for (DecTipoStrutUdReg tipoStrutUdRegExp : tipoStrutUnitaDocExp.getDecTipoStrutUdRegs()) {

                // Controlla se esiste già un registro per la struttura in questione
                DecRegistroUnitaDoc registroUnitaDoc = registroHelper.getDecRegistroUnitaDocByName(
                        tipoStrutUdRegExp.getDecRegistroUnitaDoc().getCdRegistroUnitaDoc(), idStrutCorrente);

                // Se il sistema versante NON è presente nella struttura: il record è importato.
                if (registroUnitaDoc == null) {
                    registroUnitaDoc = inserisciRegistro(idStrutCorrente, tipoStrutUdRegExp.getDecRegistroUnitaDoc());
                }

                // Controllo se esiste già il record nella tabella, ed in caso lo inserisco
                String nmTipoUnitaDoc = tipoStrutUdRegExp.getDecTipoStrutUnitaDoc().getDecTipoUnitaDoc()
                        .getNmTipoUnitaDoc();
                String nmTipoStrutUnitaDoc = tipoStrutUdRegExp.getDecTipoStrutUnitaDoc().getNmTipoStrutUnitaDoc();
                String cdRegistroUnitaDoc = tipoStrutUdRegExp.getDecRegistroUnitaDoc().getCdRegistroUnitaDoc();

                DecTipoStrutUdReg tipoStrutUdReg = registroHelper.getDecTipoStrutUdRegByName(idStrutCorrente,
                        nmTipoUnitaDoc, nmTipoStrutUnitaDoc, cdRegistroUnitaDoc);

                if (tipoStrutUdReg == null) {
                    inserisciTipoStrutUdReg(tipoStrutUnitaDoc, registroUnitaDoc);
                }
            } // End For DecTipoStrutUdReg

        } // End For DecTipoStrutUnitaDocExp

        return tipiDocImportati;
    }

    /**
     * Esegue l'importazione del periodo tipo fascicolo.
     *
     * @param idStrutCorrente
     *            la struttura in cui mi trovo e nella quale sto facendo l'import
     * @param aaTipoFascicoloExpList
     *            la lista di periodi tipo fascicolo da importare dal file XML sui quali effettuare i controlli
     * @param tipoFascicolo
     *            l'oggetto tipo fascicolo in fase di decorazione
     * @param tipoFascicoloGiaPresente
     *            l'informazione se il tipoFascicolo sia gi\u00E0 presente su DB
     * @param sovrascriviPeriodo
     *            flag per richiedere la sovrascrittura dei periodi tipo fascicolo
     */
    public void importaAaTipoFascicolo(BigDecimal idStrutCorrente, List<DecAaTipoFascicolo> aaTipoFascicoloExpList,
            DecTipoFascicolo tipoFascicolo, boolean tipoFascicoloGiaPresente, boolean sovrascriviPeriodo) {

        // Se il tipoFascicolo era già presente (quindi non lo sto inserendo da zero) e ho spuntato sovrascriviPeriodo,
        // cancello i suoi eventuali periodi di validità
        if (tipoFascicoloGiaPresente && sovrascriviPeriodo) {
            List<Long> idAaTipoFascicoloList = new ArrayList<>();
            for (DecAaTipoFascicolo aaTipoFascicolo : tipoFascicolo.getDecAaTipoFascicolos()) {
                idAaTipoFascicoloList.add(aaTipoFascicolo.getIdAaTipoFascicolo());
            }
            tipoFascicoloHelper.deleteAaTipoFascicolo(idAaTipoFascicoloList);
            tipoFascicolo.setDecAaTipoFascicolos(new ArrayList<>());
        }

        // Per ogni PERIODO TIPO FASCICOLO
        for (DecAaTipoFascicolo aaTipoFascicoloExp : aaTipoFascicoloExpList) {
            // Entity DecAaTipoFascicolo da "decorare"
            DecAaTipoFascicolo aaTipoFascicolo;

            // Se il tipo fascicolo da importare NON era presente nella struttura, importo tutto RELATIVAMENTE AL
            // PERIODO DI VALIDITA':
            // periodo di validità stesso e poi partiNumero e Modelli Xsd periodo TipoFascicolo
            if (!tipoFascicoloGiaPresente) {
                // Importo il periodo tipo fascicolo
                aaTipoFascicolo = inserisciAaTipoFascicolo(aaTipoFascicoloExp, tipoFascicolo);
                // Importo le parti numero periodo tipo fascicolo
                for (DecParteNumeroFascicolo parteNumeroFascicolo : aaTipoFascicoloExp.getDecParteNumeroFascicolos()) {
                    inserisciParteNumeroFascicolo(parteNumeroFascicolo, aaTipoFascicolo);
                }
                // Importo i modelli XSD periodo tipo fascicolo
                for (DecUsoModelloXsdFasc uso : aaTipoFascicoloExp.getDecUsoModelloXsdFascs()) {
                    inserisciUsoModelloXsdFasc(uso, aaTipoFascicolo);
                }
            } // Se invece il tipo fascicolo NON \u00E8 stato importato (in quanto gi\u00E0 presente su DB) ricavo, se
              // presente, il relativo aaTipoFascicolo dall'XML E LO INSERISCO SOLO SE IL FLAG SOVRASCRIVI E' STATO
              // SETTATO A TRUE
            else {
                if (sovrascriviPeriodo) {
                    aaTipoFascicolo = inserisciAaTipoFascicolo(aaTipoFascicoloExp, tipoFascicolo);

                    // Importo le parti numero
                    for (DecParteNumeroFascicolo parteNumeroFascicolo : aaTipoFascicoloExp
                            .getDecParteNumeroFascicolos()) {
                        inserisciParteNumeroFascicolo(parteNumeroFascicolo, aaTipoFascicolo);
                    }
                    // Importo i modelli XSD periodo tipo fascicolo
                    for (DecUsoModelloXsdFasc uso : aaTipoFascicoloExp.getDecUsoModelloXsdFascs()) {
                        inserisciUsoModelloXsdFasc(uso, aaTipoFascicolo);
                    }
                }
            }
        }
    }

    public DecUsoModelloXsdFasc inserisciUsoModelloXsdFasc(DecUsoModelloXsdFasc usoModelloXsdFascExp,
            DecAaTipoFascicolo aaTipoFascicolo) {
        DecUsoModelloXsdFasc usoModelloXsdFasc = new DecUsoModelloXsdFasc();
        usoModelloXsdFasc.setDecAaTipoFascicolo(aaTipoFascicolo);
        usoModelloXsdFasc.setDecModelloXsdFascicolo(usoModelloXsdFascExp.getDecModelloXsdFascicolo());
        usoModelloXsdFasc.setDtIstituz(usoModelloXsdFascExp.getDtIstituz());
        usoModelloXsdFasc.setDtSoppres(usoModelloXsdFascExp.getDtSoppres());
        usoModelloXsdFasc.setFlStandard(usoModelloXsdFascExp.getFlStandard());

        struttureHelper.insertEntity(usoModelloXsdFasc, true);
        // e lo associo all'anno
        if (aaTipoFascicolo.getDecUsoModelloXsdFascs() == null) {
            aaTipoFascicolo.setDecUsoModelloXsdFascs(new ArrayList<>());
        }
        aaTipoFascicolo.getDecUsoModelloXsdFascs().add(usoModelloXsdFasc);
        return usoModelloXsdFasc;
    }

    public AplSistemaVersante inserisciSistemaVersante(AplSistemaVersante sistemaVersanteExp) {
        AplSistemaVersante sistemaVersante = new AplSistemaVersante();
        sistemaVersante.setCdVersione(sistemaVersanteExp.getCdVersione());
        sistemaVersante.setDsSistemaVersante(sistemaVersanteExp.getDsSistemaVersante());
        sistemaVersante.setNmSistemaVersante(sistemaVersanteExp.getNmSistemaVersante());
        struttureHelper.insertEntity(sistemaVersante, true);
        return sistemaVersante;
    }

    public DecTipoStrutUdReg inserisciTipoStrutUdReg(DecTipoStrutUnitaDoc tipoStrutUnitaDoc,
            DecRegistroUnitaDoc registroUnitaDoc) {
        DecTipoStrutUdReg tipoStrutUdReg = new DecTipoStrutUdReg();
        tipoStrutUdReg.setDecRegistroUnitaDoc(registroUnitaDoc);
        tipoStrutUdReg.setDecTipoStrutUnitaDoc(tipoStrutUnitaDoc);
        struttureHelper.insertEntity(tipoStrutUdReg, true);
        return tipoStrutUdReg;
    }

    public DecTipoStrutUdSisVer inserisciTipoStrutUdSisVer(DecTipoStrutUnitaDoc tipoStrutUd,
            AplSistemaVersante sistemaVersante) {
        DecTipoStrutUdSisVer tipoStrutUdSisVer = new DecTipoStrutUdSisVer();
        tipoStrutUdSisVer.setAplSistemaVersante(sistemaVersante);
        tipoStrutUdSisVer.setDecTipoStrutUnitaDoc(tipoStrutUd);
        struttureHelper.insertEntity(tipoStrutUdSisVer, true);
        return tipoStrutUdSisVer;
    }

    public DecTipoStrutUdXsd inserisciTipoStrutUdXsd(DecTipoStrutUnitaDoc tipoStrutUd, DecXsdDatiSpec xsdDatiSpec) {
        DecTipoStrutUdXsd tipoStrutUdXsd = new DecTipoStrutUdXsd();
        tipoStrutUdXsd.setDecXsdDatiSpec(xsdDatiSpec);
        tipoStrutUdXsd.setDecTipoStrutUnitaDoc(tipoStrutUd);
        struttureHelper.insertEntity(tipoStrutUdXsd, true);
        return tipoStrutUdXsd;
    }

    public boolean checkAnniSovrapposti(DecAaRegistroUnitaDoc decAaRegistroUnitaDocExp,
            DecAaRegistroUnitaDoc decAaRegistroUnitaDocDB) {
        int minExp = decAaRegistroUnitaDocExp.getAaMinRegistroUnitaDoc().intValue();
        int maxExp = decAaRegistroUnitaDocExp.getAaMaxRegistroUnitaDoc() != null
                ? decAaRegistroUnitaDocExp.getAaMaxRegistroUnitaDoc().intValue() : 2444;
        int minDB = decAaRegistroUnitaDocDB.getAaMinRegistroUnitaDoc().intValue();
        int maxDB = decAaRegistroUnitaDocDB.getAaMaxRegistroUnitaDoc() != null
                ? decAaRegistroUnitaDocDB.getAaMaxRegistroUnitaDoc().intValue() : 2444;
        return maxExp >= minDB && minExp <= maxDB;
    }

    public DecTipoUnitaDoc getDecTipoUnitaDocExpSelected(OrgStrut strutExp, String nmTipoUnitaDocSelezionataDaCombo) {
        List<DecTipoUnitaDoc> tipoUnitaDocExpList = strutExp.getDecTipoUnitaDocs();
        for (DecTipoUnitaDoc tipoUnitaDocExp : tipoUnitaDocExpList) {
            if (tipoUnitaDocExp.getNmTipoUnitaDoc().equals(nmTipoUnitaDocSelezionataDaCombo)) {
                return tipoUnitaDocExp;
            }
        }
        return null;
    }

    public DecTipoFascicolo getDecTipoFascicoloExpSelected(OrgStrut strutExp,
            String nmTipoFascicoloSelezionatoDaMulti) {
        List<DecTipoFascicolo> tipoFascicoloExpList = strutExp.getDecTipoFascicolos();
        for (DecTipoFascicolo tipoFascicoloExp : tipoFascicoloExpList) {
            if (tipoFascicoloExp.getNmTipoFascicolo().equals(nmTipoFascicoloSelezionatoDaMulti)) {
                return tipoFascicoloExp;
            }
        }
        return null;
    }

    public DecTipoStrutUnitaDoc getDecTipoStrutUnitaDocExpSelected(DecTipoUnitaDoc tipoUnitaDocExp,
            String nmTipoStrutUnitaDocSelezionataDaCombo) {
        List<DecTipoStrutUnitaDoc> tipoStrutUnitaDocExpList = tipoUnitaDocExp.getDecTipoStrutUnitaDocs();
        for (DecTipoStrutUnitaDoc tipoStrutUnitaDocExp : tipoStrutUnitaDocExpList) {
            if (tipoStrutUnitaDocExp.getNmTipoStrutUnitaDoc().equals(nmTipoStrutUnitaDocSelezionataDaCombo)) {
                return tipoStrutUnitaDocExp;
            }
        }
        return null;
    }

    public DecAaTipoFascicolo getDecAaTipoFascicoloExpSelected(DecTipoFascicolo tipoFascicoloExp,
            BigDecimal aaTipoFascicoloSelezionatoDaCombo) {
        List<DecAaTipoFascicolo> aaTipoFascicoloExpList = tipoFascicoloExp.getDecAaTipoFascicolos();
        for (DecAaTipoFascicolo aaTipoFascicoloExp : aaTipoFascicoloExpList) {
            if (aaTipoFascicoloExp.getAaIniTipoFascicolo().compareTo(aaTipoFascicoloSelezionatoDaCombo) == 0) {
                return aaTipoFascicoloExp;
            }
        }
        return null;
    }

    /**
     * Esegue il salvataggio su DB di DecAaRegistroUnitaDoc. Recupera l'anno dall'XML e crea il record su DB
     *
     * @param aaRegistroUnitaDocExp
     *            l'anno registro presente nell'XML
     * @param registroUnitaDoc
     *            il record del registro, gi\u00E0 presente su DB, nel quale associare l'anno @return, il record
     *            relativo all'anno appena creato
     *
     * @return DecAaRegistroUnitaDoc
     */
    public DecAaRegistroUnitaDoc inserisciAaRegistroUnitaDoc(DecAaRegistroUnitaDoc aaRegistroUnitaDocExp,
            DecRegistroUnitaDoc registroUnitaDoc) {
        DecAaRegistroUnitaDoc aaRegistroUnitaDoc = new DecAaRegistroUnitaDoc();
        aaRegistroUnitaDoc.setAaMinRegistroUnitaDoc(aaRegistroUnitaDocExp.getAaMinRegistroUnitaDoc());
        aaRegistroUnitaDoc.setAaMaxRegistroUnitaDoc(aaRegistroUnitaDocExp.getAaMaxRegistroUnitaDoc());
        aaRegistroUnitaDoc.setFlUpdFmtNumero(aaRegistroUnitaDocExp.getFlUpdFmtNumero());
        aaRegistroUnitaDoc.setDecRegistroUnitaDoc(registroUnitaDoc);
        struttureHelper.insertEntity(aaRegistroUnitaDoc, true);
        // e lo associo al registro
        if (registroUnitaDoc.getDecAaRegistroUnitaDocs() == null) {
            registroUnitaDoc.setDecAaRegistroUnitaDocs(new ArrayList<>());
        }
        registroUnitaDoc.getDecAaRegistroUnitaDocs().add(aaRegistroUnitaDoc);
        return aaRegistroUnitaDoc;
    }

    /**
     * Esegue il salvataggio su DB di DecParteNumeroRegistro. Recupera l'anno dall'XML e crea il record su DB
     *
     * @param parteNumeroRegistroExp
     *            la parte numero presente nell'XML
     * @param aaRegistroUnitaDoc
     *            il record dell'anno, gi\u00E0 presente su DB, nel quale associare la parte numero @return, il record
     *            relativo alla parte numero appena creata
     *
     * @return DecParteNumeroRegistro
     */
    public DecParteNumeroRegistro inserisciParteNumeroRegistro(DecParteNumeroRegistro parteNumeroRegistroExp,
            DecAaRegistroUnitaDoc aaRegistroUnitaDoc) {
        DecParteNumeroRegistro parteNumeroRegistro = new DecParteNumeroRegistro();
        parteNumeroRegistro.setNmParteNumeroRegistro(parteNumeroRegistroExp.getNmParteNumeroRegistro());
        parteNumeroRegistro.setDsParteNumeroRegistro(parteNumeroRegistroExp.getDsParteNumeroRegistro());
        parteNumeroRegistro.setNiParteNumeroRegistro(parteNumeroRegistroExp.getNiParteNumeroRegistro());
        parteNumeroRegistro.setTiCharParte(parteNumeroRegistroExp.getTiCharParte());
        parteNumeroRegistro.setNiMinCharParte(parteNumeroRegistroExp.getNiMinCharParte());
        parteNumeroRegistro.setNiMaxCharParte(parteNumeroRegistroExp.getNiMaxCharParte());
        parteNumeroRegistro.setTiPadSxParte(parteNumeroRegistroExp.getTiPadSxParte());
        parteNumeroRegistro.setTiParte(parteNumeroRegistroExp.getTiParte());
        parteNumeroRegistro.setTiCharSep(parteNumeroRegistroExp.getTiCharSep());
        parteNumeroRegistro.setDlValoriParte(parteNumeroRegistroExp.getDlValoriParte());
        parteNumeroRegistro.setDecAaRegistroUnitaDoc(aaRegistroUnitaDoc);
        struttureHelper.insertEntity(parteNumeroRegistro, true);
        // e lo associo all'anno
        if (aaRegistroUnitaDoc.getDecParteNumeroRegistros() == null) {
            aaRegistroUnitaDoc.setDecParteNumeroRegistros(new ArrayList<>());
        }
        aaRegistroUnitaDoc.getDecParteNumeroRegistros().add(parteNumeroRegistro);
        return parteNumeroRegistro;
    }

    /**
     * Esegue il salvataggio su DB di DecParteNumeroFascicolo. Recupera l'anno dall'XML e crea il record su DB
     *
     * @param parteNumeroFascicoloExp
     *            la parte numero presente nell'XML
     * @param aaTipoFascicolo
     *            il record dell'anno, gi\u00E0 presente su DB, nel quale associare la parte numero
     *
     * @return DecParteNumeroFascicolo, il record relativo alla parte numero appena creata
     */
    public DecParteNumeroFascicolo inserisciParteNumeroFascicolo(DecParteNumeroFascicolo parteNumeroFascicoloExp,
            DecAaTipoFascicolo aaTipoFascicolo) {
        DecParteNumeroFascicolo parteNumeroFascicolo = new DecParteNumeroFascicolo();
        parteNumeroFascicolo.setNmParteNumero(parteNumeroFascicoloExp.getNmParteNumero());
        parteNumeroFascicolo.setDlValoriParte(parteNumeroFascicoloExp.getDlValoriParte());
        parteNumeroFascicolo.setDsParteNumero(parteNumeroFascicoloExp.getDsParteNumero());
        parteNumeroFascicolo.setNiMinCharParte(parteNumeroFascicoloExp.getNiMinCharParte());
        parteNumeroFascicolo.setNiMaxCharParte(parteNumeroFascicoloExp.getNiMaxCharParte());
        parteNumeroFascicolo.setNiParteNumero(parteNumeroFascicoloExp.getNiParteNumero());
        parteNumeroFascicolo.setTiParte(parteNumeroFascicoloExp.getTiParte());
        parteNumeroFascicolo.setTiCharSep(parteNumeroFascicoloExp.getTiCharSep());
        parteNumeroFascicolo.setTiPadParte(parteNumeroFascicoloExp.getTiPadParte());
        parteNumeroFascicolo.setTiCharParte(parteNumeroFascicoloExp.getTiCharParte());
        parteNumeroFascicolo.setDecAaTipoFascicolo(aaTipoFascicolo);
        struttureHelper.insertEntity(parteNumeroFascicolo, true);
        // e lo associo all'anno
        if (aaTipoFascicolo.getDecParteNumeroFascicolos() == null) {
            aaTipoFascicolo.setDecParteNumeroFascicolos(new ArrayList<>());
        }
        aaTipoFascicolo.getDecParteNumeroFascicolos().add(parteNumeroFascicolo);
        return parteNumeroFascicolo;
    }

    /**
     * Inserisce su DB il record relativo al Tipo Struttura Unit\u00E0 Documentaria partendo da quella passata come
     * parametro dell'oggetto tipoStrutUd recuperato dall'XML
     *
     * @param tipoStrutUnitaDocExp
     *            il tipo struttura unit\u00E0 doc recuperato dall'XML
     * @param tipoUnitaDoc
     *            il tipo unità doc in fase di "decorazione" al quale associare il tipo struttura ud
     *
     * @return il tipo struttura unit\u00E0 documentaria inserito
     */
    public DecTipoStrutUnitaDoc inserisciTipoStrutUnitaDoc(DecTipoStrutUnitaDoc tipoStrutUnitaDocExp,
            DecTipoUnitaDoc tipoUnitaDoc) {
        DecTipoStrutUnitaDoc tipoStrutUnitaDoc = new DecTipoStrutUnitaDoc();
        tipoStrutUnitaDoc.setNmTipoStrutUnitaDoc(tipoStrutUnitaDocExp.getNmTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsTipoStrutUnitaDoc(tipoStrutUnitaDocExp.getDsTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDtIstituz(new Date());
        tipoStrutUnitaDoc.setDtSoppres(tipoStrutUnitaDocExp.getDtSoppres());
        tipoStrutUnitaDoc.setDecTipoUnitaDoc(tipoUnitaDoc);
        tipoStrutUnitaDoc.setAaMaxTipoStrutUnitaDoc(tipoStrutUnitaDocExp.getAaMaxTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setAaMinTipoStrutUnitaDoc(tipoStrutUnitaDocExp.getAaMinTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsAnnoTipoStrutUnitaDoc(tipoStrutUnitaDocExp.getDsAnnoTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsDataTipoStrutUnitaDoc(tipoStrutUnitaDocExp.getDsDataTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsNumeroTipoStrutUnitaDoc(tipoStrutUnitaDocExp.getDsNumeroTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsOggTipoStrutUnitaDoc(tipoStrutUnitaDocExp.getDsOggTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsRifTempTipoStrutUd(tipoStrutUnitaDocExp.getDsRifTempTipoStrutUd());
        tipoStrutUnitaDoc.setDsCollegamentiUd(tipoStrutUnitaDocExp.getDsCollegamentiUd());
        tipoStrutUnitaDoc.setDsPeriodicitaVers(tipoStrutUnitaDocExp.getDsPeriodicitaVers());
        tipoStrutUnitaDoc.setDsFirma(tipoStrutUnitaDocExp.getDsFirma());
        // Inserisco su DB il tipoStrutUnitaDoc
        struttureHelper.insertEntity(tipoStrutUnitaDoc, true);
        if (tipoUnitaDoc.getDecTipoStrutUnitaDocs() == null) {
            tipoUnitaDoc.setDecTipoStrutUnitaDocs(new ArrayList<>());
        }
        tipoUnitaDoc.getDecTipoStrutUnitaDocs().add(tipoStrutUnitaDoc);
        return tipoStrutUnitaDoc;
    }

    /**
     * Inserisce su DB il record relativo al Periodo Tipo Fascicolo partendo da quello passato come parametro
     * dell'oggetto aaTipoFascicolo recuperato dall'XML
     *
     * @param aaTipoFascicoloExp
     *            il periodo tipo fascicolo recuperato dall'XML
     * @param tipoFascicolo
     *            il tipo fascicolo in fase di "decorazione" al quale associare il periodo tipo fascicolo
     *
     * @return il periodo tipo fascicolo inserito
     */
    public DecAaTipoFascicolo inserisciAaTipoFascicolo(DecAaTipoFascicolo aaTipoFascicoloExp,
            DecTipoFascicolo tipoFascicolo) {
        DecAaTipoFascicolo aaTipoFascicolo = new DecAaTipoFascicolo();
        aaTipoFascicolo.setAaFinTipoFascicolo(aaTipoFascicoloExp.getAaFinTipoFascicolo());
        aaTipoFascicolo.setAaIniTipoFascicolo(aaTipoFascicoloExp.getAaIniTipoFascicolo());
        aaTipoFascicolo.setFlUpdFmtNumero(aaTipoFascicoloExp.getFlUpdFmtNumero());
        aaTipoFascicolo.setNiCharPadParteClassif(aaTipoFascicoloExp.getNiCharPadParteClassif());
        aaTipoFascicolo.setDecTipoFascicolo(tipoFascicolo);
        // Inserisco su DB il aaTipoFascicolo
        struttureHelper.insertEntity(aaTipoFascicolo, true);
        if (tipoFascicolo.getDecAaTipoFascicolos() == null) {
            tipoFascicolo.setDecAaTipoFascicolos(new ArrayList<>());
        }
        tipoFascicolo.getDecAaTipoFascicolos().add(aaTipoFascicolo);
        return aaTipoFascicolo;
    }

    /**
     * Inserisce su DB una nuova categoria tipo unit\u00E0 documentaria partendo da quella passata come parametro
     * dell'oggetto categoria recuperato dall'XML
     *
     * @param categTipoUnitaDocExp
     *            l'oggetto recuperato dall'XML
     *
     * @return la nuova categoria inserita su DB
     */
    public DecCategTipoUnitaDoc inserisciCategTipoUnitaDoc(DecCategTipoUnitaDoc categTipoUnitaDocExp) {
        DecCategTipoUnitaDoc categTipoUnitaDoc = new DecCategTipoUnitaDoc();
        categTipoUnitaDoc.setCdCategTipoUnitaDoc(categTipoUnitaDocExp.getCdCategTipoUnitaDoc());
        categTipoUnitaDoc.setDsCategTipoUnitaDoc(categTipoUnitaDocExp.getDsCategTipoUnitaDoc());
        categTipoUnitaDoc.setDecTipoUnitaDocs(new ArrayList<>());
        struttureHelper.insertEntity(categTipoUnitaDoc, true);
        return categTipoUnitaDoc;
    }

    /**
     * Inserisce su DB il tipo unit\u00E0 doc ammesso (relazione tipoUd-registro)
     *
     * @param tipoUnitaDoc
     *            tipo unita doc
     * @param registroUnitaDoc
     *            registro unita doc
     *
     * @return il record relativo al tipoUnitaDocAmmesso inserito
     */
    public DecTipoUnitaDocAmmesso inserisciTipoUdAmmesso(DecTipoUnitaDoc tipoUnitaDoc,
            DecRegistroUnitaDoc registroUnitaDoc) {
        DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso = new DecTipoUnitaDocAmmesso();
        tipoUnitaDocAmmesso.setDecTipoUnitaDoc(tipoUnitaDoc);
        tipoUnitaDocAmmesso.setDecRegistroUnitaDoc(registroUnitaDoc);
        struttureHelper.insertEntity(tipoUnitaDocAmmesso, true);
        if (tipoUnitaDoc.getDecTipoUnitaDocAmmessos() == null) {
            tipoUnitaDoc.setDecTipoUnitaDocAmmessos(new ArrayList<>());
        }
        tipoUnitaDoc.getDecTipoUnitaDocAmmessos().add(tipoUnitaDocAmmesso);
        if (registroUnitaDoc.getDecTipoUnitaDocAmmessos() == null) {
            registroUnitaDoc.setDecTipoUnitaDocAmmessos(new ArrayList<>());
        }
        registroUnitaDoc.getDecTipoUnitaDocAmmessos().add(tipoUnitaDocAmmesso);
        return tipoUnitaDocAmmesso;
    }

    public DecTipoDoc inserisciTipoDoc(BigDecimal idStrutCorrente, DecTipoDoc tipoDocExp) {
        DecTipoDoc tipoDoc = new DecTipoDoc();
        tipoDoc.setNmTipoDoc(tipoDocExp.getNmTipoDoc());
        tipoDoc.setDsTipoDoc(tipoDocExp.getDsTipoDoc());
        tipoDoc.setFlTipoDocPrincipale(tipoDocExp.getFlTipoDocPrincipale());
        tipoDoc.setDtSoppres(tipoDocExp.getDtSoppres());
        tipoDoc.setDtIstituz(new Date());
        tipoDoc.setDsPeriodicitaVers(tipoDocExp.getDsPeriodicitaVers());
        tipoDoc.setDlNoteTipoDoc(tipoDocExp.getDlNoteTipoDoc());
        OrgStrut strutCorrente = struttureHelper.findById(OrgStrut.class, idStrutCorrente);
        tipoDoc.setOrgStrut(strutCorrente);
        struttureHelper.insertEntity(tipoDoc, true);
        if (strutCorrente.getDecTipoDocs() == null) {
            strutCorrente.setDecTipoDocs(new ArrayList<>());
        }
        strutCorrente.getDecTipoDocs().add(tipoDoc);
        return tipoDoc;
    }

    public DecXsdDatiSpec inserisciXsdDatiSpec(BigDecimal idStrutCorrente, DecXsdDatiSpec xsdDatiSpecExp,
            DecTipoUnitaDoc tipoUnitaDoc, DecTipoDoc tipoDoc, DecTipoCompDoc tipoCompDoc) {
        DecXsdDatiSpec xsdDatiSpec = new DecXsdDatiSpec();
        xsdDatiSpec.setTiUsoXsd(xsdDatiSpecExp.getTiUsoXsd());
        xsdDatiSpec.setTiEntitaSacer(xsdDatiSpecExp.getTiEntitaSacer());
        xsdDatiSpec.setNmSistemaMigraz(xsdDatiSpecExp.getNmSistemaMigraz());
        xsdDatiSpec.setCdVersioneXsd(xsdDatiSpecExp.getCdVersioneXsd());
        xsdDatiSpec.setBlXsd(xsdDatiSpecExp.getBlXsd());
        xsdDatiSpec.setDtIstituz(xsdDatiSpecExp.getDtIstituz());
        xsdDatiSpec.setDtSoppres(xsdDatiSpecExp.getDtSoppres());
        xsdDatiSpec.setDsVersioneXsd(xsdDatiSpecExp.getDsVersioneXsd());
        if (tipoUnitaDoc != null) {
            xsdDatiSpec.setDecTipoUnitaDoc(tipoUnitaDoc);
        } else if (tipoDoc != null) {
            xsdDatiSpec.setDecTipoDoc(tipoDoc);
        } else if (tipoCompDoc != null) {
            xsdDatiSpec.setDecTipoCompDoc(tipoCompDoc);
        }
        OrgStrut strutCorrente = struttureHelper.findById(OrgStrut.class, idStrutCorrente);
        xsdDatiSpec.setOrgStrut(strutCorrente);
        struttureHelper.insertEntity(xsdDatiSpec, true);
        if (tipoUnitaDoc != null) {
            if (tipoUnitaDoc.getDecXsdDatiSpecs() == null) {
                tipoUnitaDoc.setDecXsdDatiSpecs(new ArrayList<>());
            }
            tipoUnitaDoc.getDecXsdDatiSpecs().add(xsdDatiSpec);
        } else if (tipoDoc != null) {
            if (tipoDoc.getDecXsdDatiSpecs() == null) {
                tipoDoc.setDecXsdDatiSpecs(new ArrayList<>());
            }
            tipoDoc.getDecXsdDatiSpecs().add(xsdDatiSpec);
        } else if (tipoCompDoc != null) {
            if (tipoCompDoc.getDecXsdDatiSpecs() == null) {
                tipoCompDoc.setDecXsdDatiSpecs(new ArrayList<>());
            }
            tipoCompDoc.getDecXsdDatiSpecs().add(xsdDatiSpec);
        }
        if (strutCorrente.getDecXsdDatiSpecs() == null) {
            strutCorrente.setDecXsdDatiSpecs(new ArrayList<>());
        }
        strutCorrente.getDecXsdDatiSpecs().add(xsdDatiSpec);
        return xsdDatiSpec;
    }

    public DecTipoDocAmmesso inserisciTipoDocAmmesso(DecTipoDocAmmesso tipoDocAmmessoExp,
            DecTipoStrutUnitaDoc tipoStrutUnitaDoc, DecTipoDoc tipoDoc) {
        DecTipoDocAmmesso tipoDocAmmesso = new DecTipoDocAmmesso();
        tipoDocAmmesso.setTiDoc(tipoDocAmmessoExp.getTiDoc());
        tipoDocAmmesso.setFlObbl(tipoDocAmmessoExp.getFlObbl());
        tipoDocAmmesso.setDecTipoDoc(tipoDoc);
        tipoDocAmmesso.setDecTipoStrutUnitaDoc(tipoStrutUnitaDoc);
        struttureHelper.insertEntity(tipoDocAmmesso, true);
        if (tipoStrutUnitaDoc.getDecTipoDocAmmessos() == null) {
            tipoStrutUnitaDoc.setDecTipoDocAmmessos(new ArrayList<>());
        }
        tipoStrutUnitaDoc.getDecTipoDocAmmessos().add(tipoDocAmmesso);
        if (tipoDoc.getDecTipoDocAmmessos() == null) {
            tipoDoc.setDecTipoDocAmmessos(new ArrayList<>());
        }
        tipoDoc.getDecTipoDocAmmessos().add(tipoDocAmmesso);
        return tipoDocAmmesso;
    }

    public DecTipoStrutDoc inserisciTipoStrutDoc(BigDecimal idStrutCorrente, DecTipoStrutDoc tipoStrutDocExp) {
        DecTipoStrutDoc tipoStrutDoc = new DecTipoStrutDoc();
        tipoStrutDoc.setNmTipoStrutDoc(tipoStrutDocExp.getNmTipoStrutDoc());
        tipoStrutDoc.setDsTipoStrutDoc(tipoStrutDocExp.getDsTipoStrutDoc());
        tipoStrutDoc.setDtSoppres(tipoStrutDocExp.getDtSoppres());
        tipoStrutDoc.setDtIstituz(new Date());
        OrgStrut strutCorrente = struttureHelper.findById(OrgStrut.class, idStrutCorrente);
        tipoStrutDoc.setOrgStrut(strutCorrente);
        struttureHelper.insertEntity(tipoStrutDoc, true);
        if (strutCorrente.getDecTipoStrutDocs() == null) {
            strutCorrente.setDecTipoStrutDocs(new ArrayList<>());
        }
        strutCorrente.getDecTipoStrutDocs().add(tipoStrutDoc);
        return tipoStrutDoc;
    }

    public DecTipoStrutDocAmmesso inserisciTipoStrutDocAmmesso(DecTipoDoc tipoDoc, DecTipoStrutDoc tipoStrutDoc) {
        DecTipoStrutDocAmmesso tipoStrutDocAmmesso = new DecTipoStrutDocAmmesso();
        tipoStrutDocAmmesso.setDecTipoDoc(tipoDoc);
        tipoStrutDocAmmesso.setDecTipoStrutDoc(tipoStrutDoc);
        struttureHelper.insertEntity(tipoStrutDocAmmesso, true);
        tipoDoc.getDecTipoStrutDocAmmessos().add(tipoStrutDocAmmesso);
        tipoStrutDoc.getDecTipoStrutDocAmmessos().add(tipoStrutDocAmmesso);
        return tipoStrutDocAmmesso;
    }

    public DecTipoCompDoc inserisciTipoCompDoc(BigDecimal idStrutCorrente, String nmTipoStrutDoc,
            DecTipoCompDoc tipoCompDocExp) {
        DecTipoCompDoc tipoCompDoc = new DecTipoCompDoc();
        tipoCompDoc.setNmTipoCompDoc(tipoCompDocExp.getNmTipoCompDoc());
        tipoCompDoc.setDsTipoCompDoc(tipoCompDocExp.getDsTipoCompDoc());
        tipoCompDoc.setTiUsoCompDoc(tipoCompDocExp.getTiUsoCompDoc());
        tipoCompDoc.setDtSoppres(tipoCompDocExp.getDtSoppres());
        tipoCompDoc.setDtIstituz(new Date());
        if (tipoCompDocExp.getFlGestiti() != null) {
            tipoCompDoc.setFlGestiti(tipoCompDocExp.getFlGestiti());
        } else {
            tipoCompDoc.setFlGestiti("0");
        }
        if (tipoCompDocExp.getFlIdonei() != null) {
            tipoCompDoc.setFlIdonei(tipoCompDocExp.getFlIdonei());
        } else {
            tipoCompDoc.setFlIdonei("0");
        }
        if (tipoCompDocExp.getFlDeprecati() != null) {
            tipoCompDoc.setFlDeprecati(tipoCompDocExp.getFlDeprecati());
        } else {
            tipoCompDoc.setFlDeprecati("0");
        }
        DecTipoStrutDoc tipoStrutDoc = tipoStrutDocHelper.getDecTipoStrutDocByName(nmTipoStrutDoc, idStrutCorrente);
        tipoCompDoc.setDecTipoStrutDoc(tipoStrutDoc);
        struttureHelper.insertEntity(tipoCompDoc, true);
        if (tipoStrutDoc.getDecTipoCompDocs() == null) {
            tipoStrutDoc.setDecTipoCompDocs(new ArrayList<>());
        }
        tipoStrutDoc.getDecTipoCompDocs().add(tipoCompDoc);
        return tipoCompDoc;
    }

    public DecFormatoFileDoc inserisciFormatoFileDoc(BigDecimal idStrutCorrente, DecFormatoFileDoc formatoFileDocExp) {
        DecFormatoFileDoc formatoFileDoc = new DecFormatoFileDoc();
        formatoFileDoc.setAroCompDocs(new ArrayList<>());
        formatoFileDoc.setCdVersione(formatoFileDocExp.getCdVersione());
        formatoFileDoc.setDecFormatoFileAmmessos(new ArrayList<>());
        formatoFileDoc.setDecTipoRapprCompConts(new ArrayList<>());
        formatoFileDoc.setDecTipoRapprCompConvs(new ArrayList<>());
        formatoFileDoc.setDecUsoFormatoFileStandards(new ArrayList<>());
        formatoFileDoc.setDsFormatoFileDoc(formatoFileDocExp.getDsFormatoFileDoc());
        formatoFileDoc.setDtIstituz(formatoFileDocExp.getDtIstituz());
        formatoFileDoc.setDtSoppres(formatoFileDocExp.getDtSoppres());
        formatoFileDoc.setNmFormatoFileDoc(formatoFileDocExp.getNmFormatoFileDoc());
        OrgStrut strutCorrente = struttureHelper.findById(OrgStrut.class, idStrutCorrente);
        formatoFileDoc.setOrgStrut(strutCorrente);
        struttureHelper.insertEntity(formatoFileDoc, true);
        if (strutCorrente.getDecFormatoFileDocs() == null) {
            strutCorrente.setDecFormatoFileDocs(new ArrayList<>());
        }
        strutCorrente.getDecFormatoFileDocs().add(formatoFileDoc);
        return formatoFileDoc;
    }

    public DecFormatoFileAmmesso inserisciFormatoFileAmmesso(DecTipoCompDoc tipoCompDoc,
            DecFormatoFileDoc formatoFileDoc) {
        DecFormatoFileAmmesso formatoFileAmmesso = new DecFormatoFileAmmesso();
        formatoFileAmmesso.setDecFormatoFileDoc(formatoFileDoc);
        formatoFileAmmesso.setDecTipoCompDoc(tipoCompDoc);
        struttureHelper.insertEntity(formatoFileAmmesso, true);
        if (tipoCompDoc.getDecFormatoFileAmmessos() == null) {
            tipoCompDoc.setDecFormatoFileAmmessos(new ArrayList<>());
        }
        tipoCompDoc.getDecFormatoFileAmmessos().add(formatoFileAmmesso);
        if (formatoFileDoc.getDecFormatoFileAmmessos() == null) {
            formatoFileDoc.setDecFormatoFileAmmessos(new ArrayList<>());
        }
        formatoFileDoc.getDecFormatoFileAmmessos().add(formatoFileAmmesso);
        return formatoFileAmmesso;
    }

    public DecTipoRapprComp inserisciTipoRapprComp(BigDecimal idStrutCorrente, DecTipoRapprComp tipoRapprCompExp) {
        DecTipoRapprComp tipoRapprComp = new DecTipoRapprComp();
        tipoRapprComp.setNmTipoRapprComp(tipoRapprCompExp.getNmTipoRapprComp());
        tipoRapprComp.setDsTipoRapprComp(tipoRapprCompExp.getDsTipoRapprComp());
        tipoRapprComp.setTiAlgoRappr(tipoRapprCompExp.getTiAlgoRappr());
        tipoRapprComp.setTiOutputRappr(tipoRapprCompExp.getTiOutputRappr());
        tipoRapprComp.setDtSoppres(tipoRapprCompExp.getDtSoppres());
        tipoRapprComp.setDtIstituz(new Date());
        tipoRapprComp.setDecTrasformTipoRapprs(new ArrayList<>());

        // Formato output: lo inserisco solo se è censito a sistema
        DecFormatoFileStandard formatoFileStandard = new DecFormatoFileStandard();
        if (tipoRapprCompExp.getDecFormatoFileStandard() != null) {
            if (copiaStruttureEjb.existsAllFormatiStandard(
                    tipoRapprCompExp.getDecFormatoFileStandard().getNmFormatoFileStandard())) {
                formatoFileStandard = gestisciFormatoFileStandard(tipoRapprCompExp.getDecFormatoFileStandard());
                tipoRapprComp.setDecFormatoFileStandard(formatoFileStandard);
            }
        }

        // Formato del file contenuto: lo inserisco solo se è censito a sistema
        if (tipoRapprCompExp.getDecFormatoFileDocCont() != null) {
            if (copiaStruttureEjb
                    .existsAllFormatiStandard(tipoRapprCompExp.getDecFormatoFileDocCont().getNmFormatoFileDoc())) {
                DecFormatoFileDoc formatoFileContenuto = gestisciFormatoFileDoc(idStrutCorrente,
                        tipoRapprCompExp.getDecFormatoFileDocCont());
                tipoRapprComp.setDecFormatoFileDocCont(formatoFileContenuto);
            }
        }

        // Formato del file convertitore
        if (tipoRapprCompExp.getDecFormatoFileDocConv() != null) {
            if (copiaStruttureEjb
                    .existsAllFormatiStandard(tipoRapprCompExp.getDecFormatoFileDocConv().getNmFormatoFileDoc())) {
                DecFormatoFileDoc formatoFileConvertitore = gestisciFormatoFileDoc(idStrutCorrente,
                        tipoRapprCompExp.getDecFormatoFileDocConv());
                tipoRapprComp.setDecFormatoFileDocConv(formatoFileConvertitore);
            }
        }

        OrgStrut strutCorrente = struttureHelper.findById(OrgStrut.class, idStrutCorrente);
        tipoRapprComp.setOrgStrut(strutCorrente);
        struttureHelper.insertEntity(tipoRapprComp, true);

        if (tipoRapprCompExp.getDecFormatoFileStandard() != null) {
            formatoFileStandard.getDecTipoRapprComps().add(tipoRapprComp);
        }
        if (strutCorrente.getDecTipoRapprComps() == null) {
            strutCorrente.setDecTipoRapprComps(new ArrayList<>());
        }
        strutCorrente.getDecTipoRapprComps().add(tipoRapprComp);

        // Inserisco i trasformatori
        if (tipoRapprCompExp.getDecTrasformTipoRapprs() != null) {
            for (DecTrasformTipoRappr trasformTipoRapprExp : tipoRapprCompExp.getDecTrasformTipoRapprs()) {
                DecTrasformTipoRappr trasformTipoRappr = new DecTrasformTipoRappr();
                trasformTipoRappr.setBlFileTrasform(trasformTipoRapprExp.getBlFileTrasform());
                trasformTipoRappr.setCdVersioneTrasform(trasformTipoRapprExp.getCdVersioneTrasform());
                trasformTipoRappr.setDecImageTrasforms(new ArrayList<>());
                trasformTipoRappr.setDecTipoRapprComp(tipoRapprComp);
                trasformTipoRappr.setDsHashFileTrasform(trasformTipoRapprExp.getDsHashFileTrasform());
                trasformTipoRappr.setDtInsTrasform(trasformTipoRapprExp.getDtInsTrasform());
                trasformTipoRappr.setDtLastModTrasform(trasformTipoRapprExp.getDtLastModTrasform());
                trasformTipoRappr.setIdCompDocTest(trasformTipoRapprExp.getIdCompDocTest());
                trasformTipoRappr.setIdTrasformTipoRappr(trasformTipoRapprExp.getIdTrasformTipoRappr());
                trasformTipoRappr.setNmTrasform(trasformTipoRapprExp.getNmTrasform());
                trasformTipoRappr.setTiStatoFileTrasform(trasformTipoRapprExp.getTiStatoFileTrasform());
                struttureHelper.insertEntity(trasformTipoRappr, true);
                tipoRapprComp.getDecTrasformTipoRapprs().add(trasformTipoRappr);
            }
        }

        return tipoRapprComp;
    }

    public DecFormatoFileDoc gestisciFormatoFileDoc(BigDecimal idStrutCorrente, DecFormatoFileDoc formatoFileDocExp) {
        DecFormatoFileDoc formatoFileDoc = formatoFileDocHelper
                .getDecFormatoFileDocByName(formatoFileDocExp.getNmFormatoFileDoc(), idStrutCorrente);
        // Se il formatoFileDoc non esiste, allora procedo all'inserimento del "ramo" relativi
        if (formatoFileDoc == null) {
            formatoFileDoc = inserisciFormatoFileDoc(idStrutCorrente, formatoFileDocExp);

            List<DecUsoFormatoFileStandard> usoFormatoFileStandardExpList = formatoFileDocExp
                    .getDecUsoFormatoFileStandards();
            for (DecUsoFormatoFileStandard usoFormatoFileStandardExp : usoFormatoFileStandardExpList) {

                String nmFormatoFileStandard = usoFormatoFileStandardExp.getDecFormatoFileStandard()
                        .getNmFormatoFileStandard();

                DecFormatoFileStandard formatoFileStandard = formatoFileStandardHelper
                        .getDecFormatoFileStandardByName(nmFormatoFileStandard);

                if (formatoFileStandard == null) {
                    formatoFileStandard = inserisciFormatoFileStandard(
                            usoFormatoFileStandardExp.getDecFormatoFileStandard());
                }

                // INSERISCO DEC_USO_FORMATO_FILE_STANDARD
                inserisciUsoFormatoFileStandard(usoFormatoFileStandardExp, formatoFileDoc, formatoFileStandard);
            }
        }
        return formatoFileDoc;
    }

    public DecFormatoFileStandard gestisciFormatoFileStandard(DecFormatoFileStandard formatoFileStandardExp) {
        DecFormatoFileStandard formatoFileStandard = formatoFileStandardHelper
                .getDecFormatoFileStandardByName(formatoFileStandardExp.getNmFormatoFileStandard());
        // Se il formatoFileStandard non esiste, allora lo inserisco
        if (formatoFileStandard == null) {
            formatoFileStandard = inserisciFormatoFileStandard(formatoFileStandardExp);
        }
        return formatoFileStandard;
    }

    public DecTipoRapprAmmesso inserisciTipoRapprAmmesso(DecTipoCompDoc tipoCompDoc, DecTipoRapprComp tipoRapprComp) {
        DecTipoRapprAmmesso tipoRapprAmmesso = new DecTipoRapprAmmesso();
        tipoRapprAmmesso.setDecTipoCompDoc(tipoCompDoc);
        tipoRapprAmmesso.setDecTipoRapprComp(tipoRapprComp);
        struttureHelper.insertEntity(tipoRapprAmmesso, true);
        tipoCompDoc.getDecTipoRapprAmmessos().add(tipoRapprAmmesso);
        tipoRapprComp.getDecTipoRapprAmmessos().add(tipoRapprAmmesso);
        return tipoRapprAmmesso;
    }

    public DecFormatoFileStandard inserisciFormatoFileStandard(DecFormatoFileStandard formatoFileStandardExp) {
        DecFormatoFileStandard formatoFileStandard = new DecFormatoFileStandard();
        formatoFileStandard.setNmFormatoFileStandard(formatoFileStandardExp.getNmFormatoFileStandard());
        formatoFileStandard.setDsFormatoFileStandard(formatoFileStandardExp.getDsFormatoFileStandard());
        formatoFileStandard.setCdVersione(formatoFileStandardExp.getCdVersione());
        formatoFileStandard.setDsCopyright(formatoFileStandardExp.getDsCopyright());
        formatoFileStandard.setNmMimetypeFile(formatoFileStandardExp.getNmMimetypeFile());
        formatoFileStandard.setTiEsitoContrFormato(formatoFileStandardExp.getTiEsitoContrFormato());
        formatoFileStandard.setDecEstensioneFiles(new ArrayList<>());
        formatoFileStandard.setDecTipoRapprComps(new ArrayList<>());
        formatoFileStandard.setFlFormatoConcat(formatoFileStandardExp.getFlFormatoConcat());
        struttureHelper.insertEntity(formatoFileStandard, true);

        for (DecEstensioneFile estensioneFileExp : formatoFileStandardExp.getDecEstensioneFiles()) {
            DecEstensioneFile estensioneFile = new DecEstensioneFile();
            estensioneFile.setCdEstensioneFile(estensioneFileExp.getCdEstensioneFile());
            estensioneFile.setDecFormatoFileStandard(formatoFileStandard);
            struttureHelper.insertEntity(estensioneFile, true);
            formatoFileStandard.getDecEstensioneFiles().add(estensioneFile);
        }

        return formatoFileStandard;
    }

    public void inserisciUsoFormatoFileStandard(DecUsoFormatoFileStandard usoFormatoFileStandardExp,
            DecFormatoFileDoc formatoFileDoc, DecFormatoFileStandard formatoFileStandard) {
        DecUsoFormatoFileStandard usoFormatoFileStandard = new DecUsoFormatoFileStandard();
        usoFormatoFileStandard.setDecFormatoFileDoc(formatoFileDoc);
        usoFormatoFileStandard.setDecFormatoFileStandard(formatoFileStandard);
        usoFormatoFileStandard.setNiOrdUso(usoFormatoFileStandardExp.getNiOrdUso());
        struttureHelper.insertEntity(usoFormatoFileStandard, true);
        if (formatoFileDoc.getDecUsoFormatoFileStandards() == null) {
            formatoFileDoc.setDecUsoFormatoFileStandards(new ArrayList<>());
        }
        formatoFileDoc.getDecUsoFormatoFileStandards().add(usoFormatoFileStandard);
        if (formatoFileStandard.getDecUsoFormatoFileStandards() == null) {
            formatoFileStandard.setDecUsoFormatoFileStandards(new ArrayList<>());
        }
        formatoFileStandard.getDecUsoFormatoFileStandards().add(usoFormatoFileStandard);
    }

    public void inserisciRegola(BigDecimal idStrutCorrente, OrgRegolaValSubStrut regolaValSubStrutExp,
            DecTipoUnitaDoc tipoUnitaDoc, DecTipoDoc tipoDoc) throws ParerUserError {
        Long idRegolaValSubStrut = subStrutEjb.saveRegolaSubStrut(null,
                new BigDecimal(tipoUnitaDoc.getIdTipoUnitaDoc()), new BigDecimal(tipoDoc.getIdTipoDoc()), new Date(),
                regolaValSubStrutExp.getDtSoppres());
        OrgRegolaValSubStrut regolaValSubStrut = struttureHelper.findById(OrgRegolaValSubStrut.class,
                idRegolaValSubStrut);
        // Inserisci i campi
        for (OrgCampoValSubStrut campoValSubStrutExp : regolaValSubStrutExp.getOrgCampoValSubStruts()) {

            BigDecimal idRecord = null;
            boolean skip = false;
            CostantiDB.TipoCampo campo = CostantiDB.TipoCampo.valueOf(campoValSubStrutExp.getTiCampo());
            switch (campo) {
            case DATO_SPEC_DOC_PRINC:
            case DATO_SPEC_UNI_DOC:
                DecAttribDatiSpec attribDatiSpec = datiSpecHelper.getDecAttribDatiSpecByName(idStrutCorrente,
                        campoValSubStrutExp.getDecAttribDatiSpec().getTiUsoAttrib(),
                        campoValSubStrutExp.getDecAttribDatiSpec().getTiEntitaSacer(),
                        campoValSubStrutExp.getDecAttribDatiSpec().getDecTipoUnitaDoc().getNmTipoUnitaDoc(), null, null,
                        null, campoValSubStrutExp.getDecAttribDatiSpec().getNmAttribDatiSpec());
                idRecord = new BigDecimal(attribDatiSpec.getIdAttribDatiSpec());
                break;
            case SUB_STRUT:
                List<OrgSubStrut> subStrut = subStrutHelper
                        .getOrgSubStrut(campoValSubStrutExp.getOrgSubStrut().getNmSubStrut(), idStrutCorrente);
                if (!subStrut.isEmpty()) {
                    idRecord = new BigDecimal(subStrut.get(0).getIdSubStrut());
                } else {
                    skip = true;
                }
                break;
            }
            if (!skip) {
                subStrutEjb.saveCampoSubStrut(null, regolaValSubStrut, campoValSubStrutExp.getTiCampo(),
                        campoValSubStrutExp.getNmCampo(), idRecord);
            }
        }
    }

    public DecCriterioRaggr inserisciCriterio(BigDecimal idStrutCorrente, DecCriterioRaggr criterioRaggrExp,
            Set<BigDecimal> criteriRaggruppamentoDaLoggare) {
        DecCriterioRaggr criterioRaggr = new DecCriterioRaggr();
        criterioRaggr.setNmCriterioRaggr(criterioRaggrExp.getNmCriterioRaggr());
        criterioRaggr.setDsCriterioRaggr(criterioRaggrExp.getDsCriterioRaggr());
        criterioRaggr.setNiMaxComp(criterioRaggrExp.getNiMaxComp());
        criterioRaggr.setTiScadChiusVolume(criterioRaggrExp.getTiScadChiusVolume());
        criterioRaggr.setTiTempoScadChius(criterioRaggrExp.getTiTempoScadChius());
        criterioRaggr.setNiTempoScadChius(criterioRaggrExp.getNiTempoScadChius());
        criterioRaggr.setFlFiltroTipoUnitaDoc(criterioRaggrExp.getFlFiltroTipoUnitaDoc());
        criterioRaggr.setFlFiltroRegistroKey(criterioRaggrExp.getFlFiltroRegistroKey());
        criterioRaggr.setAaKeyUnitaDoc(criterioRaggrExp.getAaKeyUnitaDoc());
        criterioRaggr.setCdKeyUnitaDoc(criterioRaggrExp.getCdKeyUnitaDoc());
        criterioRaggr.setFlFiltroRangeRegistroKey(criterioRaggrExp.getFlFiltroRangeRegistroKey());
        criterioRaggr.setAaKeyUnitaDocDa(criterioRaggrExp.getAaKeyUnitaDocDa());
        criterioRaggr.setAaKeyUnitaDocA(criterioRaggrExp.getAaKeyUnitaDocA());
        criterioRaggr.setCdKeyUnitaDocDa(criterioRaggrExp.getCdKeyUnitaDocDa());
        criterioRaggr.setCdKeyUnitaDocA(criterioRaggrExp.getCdKeyUnitaDocA());
        criterioRaggr.setFlFiltroTiEsitoVerifFirme(criterioRaggrExp.getFlFiltroTiEsitoVerifFirme());
        criterioRaggr.setDtCreazioneUnitaDocDa(criterioRaggrExp.getDtCreazioneUnitaDocDa());
        criterioRaggr.setDtCreazioneUnitaDocA(criterioRaggrExp.getDtCreazioneUnitaDocA());
        criterioRaggr.setTiConservazione(criterioRaggrExp.getTiConservazione());
        criterioRaggr.setFlFiltroSistemaMigraz(criterioRaggrExp.getFlFiltroSistemaMigraz());
        criterioRaggr.setFlForzaAccettazione(criterioRaggrExp.getFlForzaAccettazione());
        criterioRaggr.setFlForzaConservazione(criterioRaggrExp.getFlForzaConservazione());
        criterioRaggr.setDlOggettoUnitaDoc(criterioRaggrExp.getDlOggettoUnitaDoc());
        criterioRaggr.setDtRegUnitaDocDa(criterioRaggrExp.getDtRegUnitaDocDa());
        criterioRaggr.setDtRegUnitaDocA(criterioRaggrExp.getDtRegUnitaDocA());
        criterioRaggr.setFlFiltroTipoDoc(criterioRaggrExp.getFlFiltroTipoDoc());
        criterioRaggr.setDlDoc(criterioRaggrExp.getDlDoc());
        criterioRaggr.setDsAutoreDoc(criterioRaggrExp.getDsAutoreDoc());
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);
        criterioRaggr.setDtIstituz(c1.getTime());
        criterioRaggr.setDtSoppres(criterioRaggrExp.getDtSoppres());
        criterioRaggr.setFlCriterioRaggrStandard(criterioRaggrExp.getFlCriterioRaggrStandard());
        criterioRaggr.setFlCriterioRaggrFisc(criterioRaggrExp.getFlCriterioRaggrFisc());
        criterioRaggr.setTiGestElencoCriterio(criterioRaggrExp.getTiGestElencoCriterio());
        criterioRaggr.setTiValidElenco(criterioRaggrExp.getTiValidElenco());
        criterioRaggr.setTiModValidElenco(criterioRaggrExp.getTiModValidElenco());
        criterioRaggr.setNtCriterioRaggr(criterioRaggrExp.getNtCriterioRaggr());
        OrgStrut strutCorrente = struttureHelper.findById(OrgStrut.class, idStrutCorrente);
        criterioRaggr.setOrgStrut(strutCorrente);
        struttureHelper.insertEntity(criterioRaggr, true);
        criteriRaggruppamentoDaLoggare.add(new BigDecimal(criterioRaggr.getIdCriterioRaggr()));
        if (strutCorrente.getDecCriterioRaggrs() == null) {
            strutCorrente.setDecCriterioRaggrs(new ArrayList<>());
        }
        strutCorrente.getDecCriterioRaggrs().add(criterioRaggr);
        return criterioRaggr;
    }

    public DecCriterioRaggrFasc inserisciCriterioFasc(BigDecimal idStrutCorrente,
            DecCriterioRaggrFasc criterioRaggrFascExp, Set<BigDecimal> criteriRaggruppamentoFascDaLoggare) {
        DecCriterioRaggrFasc criterioRaggrFasc = new DecCriterioRaggrFasc();
        criterioRaggrFasc.setNmCriterioRaggr(criterioRaggrFascExp.getNmCriterioRaggr());
        criterioRaggrFasc.setDsCriterioRaggr(criterioRaggrFascExp.getDsCriterioRaggr());
        criterioRaggrFasc.setNiMaxFasc(criterioRaggrFascExp.getNiMaxFasc());
        criterioRaggrFasc.setTiScadChius(criterioRaggrFascExp.getTiScadChius());
        criterioRaggrFasc.setTiTempoScadChius(criterioRaggrFascExp.getTiTempoScadChius());
        criterioRaggrFasc.setNiTempoScadChius(criterioRaggrFascExp.getNiTempoScadChius());
        criterioRaggrFasc.setFlFiltroTipoFascicolo(criterioRaggrFascExp.getFlFiltroTipoFascicolo());
        criterioRaggrFasc.setAaFascicolo(criterioRaggrFascExp.getAaFascicolo());
        criterioRaggrFasc.setAaFascicoloDa(criterioRaggrFascExp.getAaFascicoloDa());
        criterioRaggrFasc.setAaFascicoloA(criterioRaggrFascExp.getAaFascicoloA());
        criterioRaggrFasc.setFlFiltroVoceTitol(criterioRaggrFascExp.getFlFiltroVoceTitol());
        criterioRaggrFasc.setDtApeFascicoloDa(criterioRaggrFascExp.getDtApeFascicoloDa());
        criterioRaggrFasc.setDtApeFascicoloA(criterioRaggrFascExp.getDtApeFascicoloA());
        criterioRaggrFasc.setTiConservazione(criterioRaggrFascExp.getTiConservazione());
        criterioRaggrFasc.setFlFiltroSistemaMigraz(criterioRaggrFascExp.getFlFiltroSistemaMigraz());
        criterioRaggrFasc.setDsOggettoFascicolo(criterioRaggrFascExp.getDsOggettoFascicolo());
        criterioRaggrFasc.setDtChiuFascicoloDa(criterioRaggrFascExp.getDtChiuFascicoloDa());
        criterioRaggrFasc.setDtChiuFascicoloA(criterioRaggrFascExp.getDtChiuFascicoloA());
        criterioRaggrFasc.setDtVersDa(criterioRaggrFascExp.getDtVersDa());
        criterioRaggrFasc.setDtVersA(criterioRaggrFascExp.getDtVersA());
        criterioRaggrFasc.setFlCriterioRaggrStandard(criterioRaggrFascExp.getFlCriterioRaggrStandard());
        criterioRaggrFasc.setNtCriterioRaggr(criterioRaggrFascExp.getNtCriterioRaggr());
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);
        criterioRaggrFasc.setDtIstituz(c1.getTime());
        criterioRaggrFasc.setDtSoppres(criterioRaggrFascExp.getDtSoppres());
        OrgStrut strutCorrente = struttureHelper.findById(OrgStrut.class, idStrutCorrente);
        criterioRaggrFasc.setOrgStrut(strutCorrente);
        struttureHelper.insertEntity(criterioRaggrFasc, true);
        criteriRaggruppamentoFascDaLoggare.add(new BigDecimal(criterioRaggrFasc.getIdCriterioRaggrFasc()));
        if (strutCorrente.getDecCriterioRaggrFascs() == null) {
            strutCorrente.setDecCriterioRaggrFascs(new ArrayList<>());
        }
        strutCorrente.getDecCriterioRaggrFascs().add(criterioRaggrFasc);
        return criterioRaggrFasc;
    }

    private void inserisciFiltroMultiploTipoUd(BigDecimal idStrutCorrente,
            DecCriterioFiltroMultiplo criterioFiltroMultiploExp, DecCriterioRaggr criterioRaggr) {
        DecCriterioFiltroMultiplo criterioFiltroMultiplo = new DecCriterioFiltroMultiplo();
        criterioFiltroMultiplo.setDecCriterioRaggr(criterioRaggr);
        criterioFiltroMultiplo.setTiFiltroMultiplo(criterioFiltroMultiploExp.getTiFiltroMultiplo());
        criterioFiltroMultiplo.setTiEsitoVerifFirme(criterioFiltroMultiploExp.getTiEsitoVerifFirme());
        DecTipoUnitaDoc tipoUnitaDoc = tipoUnitaDocHelper.getDecTipoUnitaDocByName(
                criterioFiltroMultiploExp.getDecTipoUnitaDoc().getNmTipoUnitaDoc(), idStrutCorrente);
        criterioFiltroMultiplo.setDecTipoUnitaDoc(tipoUnitaDoc);
        criterioFiltroMultiplo.setNmSistemaMigraz(criterioFiltroMultiploExp.getNmSistemaMigraz());
        struttureHelper.insertEntity(criterioFiltroMultiplo, true);
        if (criterioRaggr.getDecCriterioFiltroMultiplos() == null) {
            criterioRaggr.setDecCriterioFiltroMultiplos(new ArrayList<>());
        }
        criterioRaggr.getDecCriterioFiltroMultiplos().add(criterioFiltroMultiplo);
        if (tipoUnitaDoc.getDecCriterioFiltroMultiplos() == null) {
            tipoUnitaDoc.setDecCriterioFiltroMultiplos(new ArrayList<>());
        }
        tipoUnitaDoc.getDecCriterioFiltroMultiplos().add(criterioFiltroMultiplo);
    }

    private void inserisciFiltroMultiploTipoDoc(BigDecimal idStrutCorrente,
            DecCriterioFiltroMultiplo criterioFiltroMultiploExp, DecCriterioRaggr criterioRaggr) {
        DecCriterioFiltroMultiplo criterioFiltroMultiplo = new DecCriterioFiltroMultiplo();
        criterioFiltroMultiplo.setDecCriterioRaggr(criterioRaggr);
        criterioFiltroMultiplo.setTiFiltroMultiplo(criterioFiltroMultiploExp.getTiFiltroMultiplo());
        criterioFiltroMultiplo.setTiEsitoVerifFirme(criterioFiltroMultiploExp.getTiEsitoVerifFirme());
        DecTipoDoc tipoDoc = tipoDocHelper.getDecTipoDocByName(criterioFiltroMultiploExp.getDecTipoDoc().getNmTipoDoc(),
                idStrutCorrente);
        criterioFiltroMultiplo.setDecTipoDoc(tipoDoc);
        criterioFiltroMultiplo.setNmSistemaMigraz(criterioFiltroMultiploExp.getNmSistemaMigraz());
        struttureHelper.insertEntity(criterioFiltroMultiplo, true);
        if (criterioRaggr.getDecCriterioFiltroMultiplos() == null) {
            criterioRaggr.setDecCriterioFiltroMultiplos(new ArrayList<>());
        }
        criterioRaggr.getDecCriterioFiltroMultiplos().add(criterioFiltroMultiplo);
        if (tipoDoc.getDecCriterioFiltroMultiplos() == null) {
            tipoDoc.setDecCriterioFiltroMultiplos(new ArrayList<>());
        }
        tipoDoc.getDecCriterioFiltroMultiplos().add(criterioFiltroMultiplo);
    }

    private void inserisciFiltroMultiploRegistroUnitaDoc(BigDecimal idStrutCorrente,
            DecCriterioFiltroMultiplo criterioFiltroMultiploExp, DecCriterioRaggr criterioRaggr) {
        DecCriterioFiltroMultiplo criterioFiltroMultiplo = new DecCriterioFiltroMultiplo();
        criterioFiltroMultiplo.setDecCriterioRaggr(criterioRaggr);
        criterioFiltroMultiplo.setTiFiltroMultiplo(criterioFiltroMultiploExp.getTiFiltroMultiplo());
        criterioFiltroMultiplo.setTiEsitoVerifFirme(criterioFiltroMultiploExp.getTiEsitoVerifFirme());
        DecRegistroUnitaDoc registroUnitaDoc = registroHelper.getDecRegistroUnitaDocByName(
                criterioFiltroMultiploExp.getDecRegistroUnitaDoc().getCdRegistroUnitaDoc(), idStrutCorrente);
        criterioFiltroMultiplo.setDecRegistroUnitaDoc(registroUnitaDoc);
        criterioFiltroMultiplo.setNmSistemaMigraz(criterioFiltroMultiploExp.getNmSistemaMigraz());
        struttureHelper.insertEntity(criterioFiltroMultiplo, true);
        if (criterioRaggr.getDecCriterioFiltroMultiplos() == null) {
            criterioRaggr.setDecCriterioFiltroMultiplos(new ArrayList<>());
        }
        criterioRaggr.getDecCriterioFiltroMultiplos().add(criterioFiltroMultiplo);
        if (registroUnitaDoc.getDecCriterioFiltroMultiplos() == null) {
            registroUnitaDoc.setDecCriterioFiltroMultiplos(new ArrayList<>());
        }
        registroUnitaDoc.getDecCriterioFiltroMultiplos().add(criterioFiltroMultiplo);
    }

    public DecAttribDatiSpec inserisciAttribDatiSpec(BigDecimal idStrutCorrente, DecAttribDatiSpec attribDatiSpecExp,
            DecTipoUnitaDoc tipoUnitaDoc, DecTipoDoc tipoDoc, DecTipoCompDoc tipoCompDoc,
            Constants.TipoEntitaSacer tiEntitaSacer) {
        DecAttribDatiSpec attribDatiSpec = new DecAttribDatiSpec();
        attribDatiSpec.setDsAttribDatiSpec(attribDatiSpecExp.getDsAttribDatiSpec());
        attribDatiSpec.setNmAttribDatiSpec(attribDatiSpecExp.getNmAttribDatiSpec());
        attribDatiSpec.setNmSistemaMigraz(attribDatiSpecExp.getNmSistemaMigraz());
        attribDatiSpec.setTiEntitaSacer(attribDatiSpecExp.getTiEntitaSacer());
        attribDatiSpec.setTiUsoAttrib(attribDatiSpecExp.getTiUsoAttrib());
        if (tiEntitaSacer.equals(Constants.TipoEntitaSacer.UNI_DOC)) {
            attribDatiSpec.setDecTipoUnitaDoc(tipoUnitaDoc);
        }
        if (tiEntitaSacer.equals(Constants.TipoEntitaSacer.DOC)) {
            attribDatiSpec.setDecTipoDoc(tipoDoc);
        }
        if (tiEntitaSacer.equals(Constants.TipoEntitaSacer.COMP)) {
            attribDatiSpec.setDecTipoCompDoc(tipoCompDoc);
        }
        OrgStrut strutCorrente = struttureHelper.findById(OrgStrut.class, idStrutCorrente);
        attribDatiSpec.setOrgStrut(strutCorrente);
        struttureHelper.insertEntity(attribDatiSpec, true);
        if (strutCorrente.getDecAttribDatiSpecs() == null) {
            strutCorrente.setDecAttribDatiSpecs(new ArrayList<>());
        }
        strutCorrente.getDecAttribDatiSpecs().add(attribDatiSpec);
        if (tiEntitaSacer.equals(Constants.TipoEntitaSacer.UNI_DOC)) {
            if (tipoUnitaDoc.getDecAttribDatiSpecs() == null) {
                tipoUnitaDoc.setDecAttribDatiSpecs(new ArrayList<>());
            }
            tipoUnitaDoc.getDecAttribDatiSpecs().add(attribDatiSpec);
        }
        if (tiEntitaSacer.equals(Constants.TipoEntitaSacer.DOC)) {
            if (tipoDoc.getDecAttribDatiSpecs() == null) {
                tipoDoc.setDecAttribDatiSpecs(new ArrayList<>());
            }
            tipoDoc.getDecAttribDatiSpecs().add(attribDatiSpec);
        }
        if (tiEntitaSacer.equals(Constants.TipoEntitaSacer.COMP)) {
            if (tipoCompDoc.getDecAttribDatiSpecs() == null) {
                tipoCompDoc.setDecAttribDatiSpecs(new ArrayList<>());
            }
            tipoCompDoc.getDecAttribDatiSpecs().add(attribDatiSpec);
        }
        return attribDatiSpec;
    }

    public DecXsdAttribDatiSpec inserisciXsdAttribDatiSpec(DecXsdDatiSpec xsdDatiSpec, DecAttribDatiSpec attribDatiSpec,
            BigDecimal niOrdAttrib, String dsAttribDatiSpec) {
        DecXsdAttribDatiSpec xsdAttribDatiSpec = new DecXsdAttribDatiSpec();
        xsdAttribDatiSpec.setDecXsdDatiSpec(xsdDatiSpec);
        xsdAttribDatiSpec.setDecAttribDatiSpec(attribDatiSpec);
        xsdAttribDatiSpec.setNiOrdAttrib(niOrdAttrib);
        xsdAttribDatiSpec.setDsAttribDatiSpec(dsAttribDatiSpec);
        struttureHelper.insertEntity(xsdAttribDatiSpec, true);
        return xsdAttribDatiSpec;
    }

    /**
     * Creo un nuovo oggetto tipo unit\u00E0 documentaria partendo dallo stesso ricavato dall'XML e aggiungendo la
     * categoria e i valori sui parametri
     *
     * @param idStrutCorrente
     *            id struttura corrente
     * @param tipoUnitaDocExp
     *            tipo unita doc
     * @param categTipoUnitaDoc
     *            categoria tipo unita doc
     * @param calcTiServOnTipoUd
     *            calcolo tipo servizio
     * @param calcTiServOnTipoUd2
     *            calcolo tipo servizio
     *
     * @return DecTipoUnitaDoc decodifica tipo unita doc
     */
    public DecTipoUnitaDoc inserisciTipoUd(BigDecimal idStrutCorrente, DecTipoUnitaDoc tipoUnitaDocExp,
            DecCategTipoUnitaDoc categTipoUnitaDoc, DecVCalcTiServOnTipoUd calcTiServOnTipoUd,
            DecVCalcTiServOnTipoUd calcTiServOnTipoUd2) {
        DecTipoUnitaDoc tipoUnitaDoc = new DecTipoUnitaDoc();
        tipoUnitaDoc.setNmTipoUnitaDoc(tipoUnitaDocExp.getNmTipoUnitaDoc());
        tipoUnitaDoc.setDsTipoUnitaDoc(tipoUnitaDocExp.getDsTipoUnitaDoc());
        tipoUnitaDoc.setDtSoppres(tipoUnitaDocExp.getDtSoppres());
        tipoUnitaDoc.setTiSaveFile(tipoUnitaDocExp.getTiSaveFile());
        tipoUnitaDoc.setDtIstituz(new Date());
        tipoUnitaDoc.setDecCategTipoUnitaDoc(categTipoUnitaDoc);
        OrgStrut strutCorrente = struttureHelper.findById(OrgStrut.class, idStrutCorrente);
        tipoUnitaDoc.setOrgStrut(strutCorrente);
        struttureHelper.insertEntity(tipoUnitaDoc, true);
        if (categTipoUnitaDoc.getDecTipoUnitaDocs() == null) {
            categTipoUnitaDoc.setDecTipoUnitaDocs(new ArrayList<>());
        }
        categTipoUnitaDoc.getDecTipoUnitaDocs().add(tipoUnitaDoc);
        if (strutCorrente.getDecTipoUnitaDocs() == null) {
            strutCorrente.setDecTipoUnitaDocs(new ArrayList<>());
        }
        strutCorrente.getDecTipoUnitaDocs().add(tipoUnitaDoc);
        // Aggiungo i nuovi campi sulla Consigurazione Serie
        tipoUnitaDoc.setFlCreaTipoSerieStandard(tipoUnitaDocExp.getFlCreaTipoSerieStandard());
        tipoUnitaDoc.setNmTipoSerieDaCreare(tipoUnitaDocExp.getNmTipoSerieDaCreare());
        tipoUnitaDoc.setDsTipoSerieDaCreare(tipoUnitaDocExp.getDsTipoSerieDaCreare());
        tipoUnitaDoc.setCdSerieDaCreare(tipoUnitaDocExp.getCdSerieDaCreare());
        tipoUnitaDoc.setDsSerieDaCreare(tipoUnitaDocExp.getDsSerieDaCreare());
        tipoUnitaDoc.setNiAaConserv(tipoUnitaDocExp.getNiAaConserv());
        tipoUnitaDoc.setFlConservIllimitata(tipoUnitaDocExp.getFlConservIllimitata());
        tipoUnitaDoc.setFlConservUniforme(tipoUnitaDocExp.getFlConservUniforme());
        if (calcTiServOnTipoUd != null) {
            OrgTipoServizio tipoServizioConserv = calcTiServOnTipoUd.getIdTipoServizioConserv() != null
                    ? struttureHelper.findById(OrgTipoServizio.class, calcTiServOnTipoUd.getIdTipoServizioConserv())
                    : null;
            OrgTipoServizio tipoServizioAttiv = calcTiServOnTipoUd.getIdTipoServizioAttiv() != null
                    ? struttureHelper.findById(OrgTipoServizio.class, calcTiServOnTipoUd.getIdTipoServizioAttiv())
                    : null;
            tipoUnitaDoc.setOrgTipoServizio(tipoServizioConserv);
            tipoUnitaDoc.setOrgTipoServizioAttiv(tipoServizioAttiv);
        }
        if (calcTiServOnTipoUd2 != null) {
            OrgTipoServizio tipoServizioConserv = calcTiServOnTipoUd2.getIdTipoServizioConserv() != null
                    ? struttureHelper.findById(OrgTipoServizio.class, calcTiServOnTipoUd2.getIdTipoServizioConserv())
                    : null;
            OrgTipoServizio tipoServizioAttiv = calcTiServOnTipoUd2.getIdTipoServizioAttiv() != null
                    ? struttureHelper.findById(OrgTipoServizio.class, calcTiServOnTipoUd2.getIdTipoServizioAttiv())
                    : null;
            tipoUnitaDoc.setOrgTipoServConservTipoUd(tipoServizioConserv);
            tipoUnitaDoc.setOrgTipoServAttivTipoUd(tipoServizioAttiv);
        }
        return tipoUnitaDoc;
    }

    /**
     * Creo un nuovo oggetto tipo fascicolo partendo dallo stesso ricavato dall'XML
     *
     * @param idStrutCorrente
     *            id struttura corrente
     * @param tipoFascicoloExp
     *            tipo fascicolo
     *
     * @return DecTipoFascicolo decodifica tipo fascicolo
     */
    public DecTipoFascicolo inserisciTipoFascicolo(BigDecimal idStrutCorrente, DecTipoFascicolo tipoFascicoloExp) {
        DecTipoFascicolo tipoFascicolo = new DecTipoFascicolo();
        tipoFascicolo.setNmTipoFascicolo(tipoFascicoloExp.getNmTipoFascicolo());
        tipoFascicolo.setDsTipoFascicolo(tipoFascicoloExp.getDsTipoFascicolo());
        tipoFascicolo.setDtSoppres(tipoFascicoloExp.getDtSoppres());
        tipoFascicolo.setDtIstituz(new Date());
        OrgStrut strutCorrente = struttureHelper.findById(OrgStrut.class, idStrutCorrente);
        tipoFascicolo.setOrgStrut(strutCorrente);
        struttureHelper.insertEntity(tipoFascicolo, true);

        if (strutCorrente.getDecTipoUnitaDocs() == null) {
            strutCorrente.setDecTipoUnitaDocs(new ArrayList<>());
        }
        strutCorrente.getDecTipoFascicolos().add(tipoFascicolo);

        return tipoFascicolo;
    }

    public AplValoreParamApplic inserisciValoreParamApplic(String nmParamApplic, String dsValoreParamApplicExp,
            DecTipoUnitaDoc tipoUnitaDoc) {
        AplValoreParamApplic valoreParamApplic = new AplValoreParamApplic();
        valoreParamApplic.setAplParamApplic(configurationHelper.getParamApplic(nmParamApplic));
        valoreParamApplic.setDecAaTipoFascicolo(null);
        valoreParamApplic.setDecTipoUnitaDoc(tipoUnitaDoc);
        valoreParamApplic.setDsValoreParamApplic(dsValoreParamApplicExp);
        valoreParamApplic.setOrgAmbiente(null);
        valoreParamApplic.setOrgStrut(null);
        valoreParamApplic.setTiAppart(TiApparType.TIPO_UNITA_DOC.name());
        struttureHelper.insertEntity(valoreParamApplic, true);
        return valoreParamApplic;
    }

    /**
     * Creo un nuovo oggetto registro unit\u00E0 documentaria partendo dallo stesso ricavato dall'XML
     *
     * @param idStrutCorrente
     *            id struttura corrente
     * @param registroUnitaDocExp
     *            registro unita doc
     *
     * @return DecRegistroUnitaDoc registro unita doc
     */
    public DecRegistroUnitaDoc inserisciRegistro(BigDecimal idStrutCorrente, DecRegistroUnitaDoc registroUnitaDocExp) {
        DecRegistroUnitaDoc registroUnitaDoc = new DecRegistroUnitaDoc();
        registroUnitaDoc.setCdRegistroUnitaDoc(registroUnitaDocExp.getCdRegistroUnitaDoc());
        registroUnitaDoc.setCdRegistroNormaliz(registroUnitaDocExp.getCdRegistroNormaliz());
        registroUnitaDoc.setDsRegistroUnitaDoc(registroUnitaDocExp.getDsRegistroUnitaDoc());
        registroUnitaDoc.setFlRegistroFisc(registroUnitaDocExp.getFlRegistroFisc());
        registroUnitaDoc.setFlCreaSerie(registroUnitaDocExp.getFlCreaSerie());
        registroUnitaDoc.setFlTipoSerieMult(
                registroUnitaDocExp.getFlTipoSerieMult() != null ? registroUnitaDocExp.getFlTipoSerieMult() : "0");
        registroUnitaDoc.setDtSoppres(registroUnitaDocExp.getDtSoppres());
        registroUnitaDoc.setDtIstituz(new Date());
        registroUnitaDoc.setNiAnniConserv(registroUnitaDocExp.getNiAnniConserv());
        registroUnitaDoc.setOrgStrut(struttureHelper.findById(OrgStrut.class, idStrutCorrente));
        // Aggiungo i nuovi campi sulla Consigurazione Serie
        registroUnitaDoc.setFlCreaTipoSerieStandard(registroUnitaDocExp.getFlCreaTipoSerieStandard());
        registroUnitaDoc.setNmTipoSerieDaCreare(registroUnitaDocExp.getNmTipoSerieDaCreare());
        registroUnitaDoc.setDsTipoSerieDaCreare(registroUnitaDocExp.getDsTipoSerieDaCreare());
        registroUnitaDoc.setCdSerieDaCreare(registroUnitaDocExp.getCdSerieDaCreare());
        registroUnitaDoc.setDsSerieDaCreare(registroUnitaDocExp.getDsSerieDaCreare());
        registroUnitaDoc.setNiAaConserv(registroUnitaDocExp.getNiAaConserv());
        registroUnitaDoc.setFlConservIllimitata(registroUnitaDocExp.getFlConservIllimitata());
        registroUnitaDoc.setFlConservUniforme(registroUnitaDocExp.getFlConservUniforme());
        struttureHelper.insertEntity(registroUnitaDoc, true);
        return registroUnitaDoc;
    }

    public void importaRegole(BigDecimal idStrutCorrente, DecTipoUnitaDoc tipoUnitaDocExp, DecTipoUnitaDoc tipoUnitaDoc)
            throws Exception {
        List<OrgRegolaValSubStrut> regolaValSubStrutExpList = tipoUnitaDocExp.getOrgRegolaValSubStruts();

        for (OrgRegolaValSubStrut regolaValSubStrutExp : regolaValSubStrutExpList) {
            DecTipoDoc tipoDocExp = regolaValSubStrutExp.getDecTipoDoc();
            String nmTipoDocExp = tipoDocExp.getNmTipoDoc();

            DecTipoDoc tipoDoc = tipoDocHelper.getDecTipoDocByName(nmTipoDocExp, idStrutCorrente);

            // Se il tipo doc NON \u00E8 gi\u00E0 presente, lo inserisco
            if (tipoDoc != null) {

                // Verifico se il tipo doc \u00E8 inserito anche in ORG_REGOLA_VAL_SUB_STRUT (relazione tipoUd-tipoDoc)
                String nmTipoUnitaDocExp = regolaValSubStrutExp.getDecTipoUnitaDoc().getNmTipoUnitaDoc();

                boolean existsRegola = subStrutHelper.existOrgRegolaSubStrut(idStrutCorrente, nmTipoUnitaDocExp,
                        nmTipoDocExp);

                // Se la regola non \u00E8 presente, la creo e la salvo su DB, altrimenti non faccio nulla
                if (!existsRegola) {
                    try {
                        inserisciRegola(idStrutCorrente, regolaValSubStrutExp, tipoUnitaDoc, tipoDoc);
                    } catch (ParerUserError e) {
                        throw new Exception(e);
                    }
                }
            }
        }
    }

    public void importaCriteriTipoUd(BigDecimal idStrutCorrente, DecTipoUnitaDoc tipoUnitaDocExp,
            List<CoppiaRegistri> registriImportati, List<CoppiaTipiDoc> tipiDocImportati,
            Set<BigDecimal> criteriRaggruppamentoDaLoggare) {

        List<DecCriterioRaggr> criteriCoinvoltiExp = new ArrayList<>();

        // Ricavo i criteri di raggruppamento ATTIVI per il tipo unit\u00E0 documentaria da importare
        // e recupero i criteri coinvolti dal tipo unita doc
        for (DecCriterioFiltroMultiplo criterioFiltroMultiploExp : tipoUnitaDocExp.getDecCriterioFiltroMultiplos()) {
            if (criterioFiltroMultiploExp.getDecCriterioRaggr().getDtSoppres().after(new Date())) {
                criteriCoinvoltiExp.add(criterioFiltroMultiploExp.getDecCriterioRaggr());
            }
        }

        // Ricavo i criteri di raggruppamento ATTIVI per i registri importati e reupero i criteri coinvolti da ogni
        // registro importato
        for (CoppiaRegistri registroImportato : registriImportati) {
            for (DecCriterioFiltroMultiplo criterioFiltroMultiploExp : registroImportato.getRegistroUnitaDocExp()
                    .getDecCriterioFiltroMultiplos()) {
                if (criterioFiltroMultiploExp.getDecCriterioRaggr().getDtSoppres().after(new Date())) {
                    criteriCoinvoltiExp.add(criterioFiltroMultiploExp.getDecCriterioRaggr());
                }
            }
        }

        // Ricavo i criteri di raggruppamento ATTIVI per i tipi doc importati e reupero i criteri coinvolti da ogni tipo
        // doc importato
        for (CoppiaTipiDoc tipoDocImportato : tipiDocImportati) {
            for (DecCriterioFiltroMultiplo criterioFiltroMultiploExp : tipoDocImportato.getTipoDocExp()
                    .getDecCriterioFiltroMultiplos()) {
                if (criterioFiltroMultiploExp.getDecCriterioRaggr().getDtSoppres().after(new Date())) {
                    criteriCoinvoltiExp.add(criterioFiltroMultiploExp.getDecCriterioRaggr());
                }
            }
        }

        // Adesso che ho i criteri coinvolti ATTIVI, verifico se questi criteri fanno riferimento
        // anche ad altri tipi ud, registri o tipi doc e nel caso verifico che siano presenti per la struttura
        for (DecCriterioRaggr criterioCoinvoltoExp : criteriCoinvoltiExp) {
            boolean elementoMancante = false;
            // Controllo che il criterio esista su DB
            DecCriterioRaggr criterioRaggr = crHelper.getDecCriterioRaggrByStrutturaCorrenteAndCriterio(idStrutCorrente,
                    criterioCoinvoltoExp.getNmCriterioRaggr());

            // Se il criterio non esiste su DB, pu\u00E8 darsi che vada inserito... dipender\u00E8 dai tipiUd, tipiDoc,
            // registri coinvolti
            if (criterioRaggr == null) {
                // Ricavo la lista dei suoi filtri multipli (tipiUd, registri, tipiDoc)
                List<DecCriterioFiltroMultiplo> criteriFiltriMultipliCoinvolti = criterioCoinvoltoExp
                        .getDecCriterioFiltroMultiplos();
                // Per ognuno di essi, verifico se il tipoUd, il registro o il tipoDoc, \u00E8 presente per quella
                // struttura
                for (DecCriterioFiltroMultiplo criterioFiltroMultiploCoinvolto : criteriFiltriMultipliCoinvolti) {

                    if (criterioFiltroMultiploCoinvolto.getTiFiltroMultiplo().equals("TIPO_UNI_DOC")) {
                        String nmTipoUnitaDoc = criterioFiltroMultiploCoinvolto.getDecTipoUnitaDoc()
                                .getNmTipoUnitaDoc();
                        // Verifico se il tipoUd esiste per la struttura coinvolta
                        DecTipoUnitaDoc tipoUd = tipoUnitaDocHelper.getDecTipoUnitaDocByName(nmTipoUnitaDoc,
                                idStrutCorrente);
                        if (tipoUd == null) {
                            elementoMancante = true;
                            break;
                        }
                    } else if (criterioFiltroMultiploCoinvolto.getTiFiltroMultiplo().equals("TIPO_DOC")) {
                        String nmTipoDoc = criterioFiltroMultiploCoinvolto.getDecTipoDoc().getNmTipoDoc();
                        // Verifico se il tipoDoc esiste per la struttura coinvolta
                        DecTipoDoc tipoDoc = tipoDocHelper.getDecTipoDocByName(nmTipoDoc, idStrutCorrente);
                        if (tipoDoc == null) {
                            elementoMancante = true;
                            break;
                        }
                    } else if (criterioFiltroMultiploCoinvolto.getTiFiltroMultiplo().equals("REGISTRO_UNI_DOC")) {
                        String cdRegistroUnitaDoc = criterioFiltroMultiploCoinvolto.getDecRegistroUnitaDoc()
                                .getCdRegistroUnitaDoc();
                        // Verifico se il registroUd esiste per la struttura coinvolta
                        DecRegistroUnitaDoc registroUd = registroHelper.getDecRegistroUnitaDocByName(cdRegistroUnitaDoc,
                                idStrutCorrente);
                        if (registroUd == null) {
                            elementoMancante = true;
                            break;
                        }

                    }
                }
            } else {
                // Se esiste su DB, non devo fare nulla
                // (imposto elementoMancante a true cos\u00E8 da non fa nulla successivamente)
                elementoMancante = true;
            }

            // Se tutti i tipiUd, tipiDoc o registri sono presenti per quella struttura
            // allora importo il criterio
            if (!elementoMancante) {
                criterioRaggr = inserisciCriterio(idStrutCorrente, criterioCoinvoltoExp,
                        criteriRaggruppamentoDaLoggare);

                for (DecCriterioFiltroMultiplo criterioFiltroMultiploExp : criterioCoinvoltoExp
                        .getDecCriterioFiltroMultiplos()) {
                    if (criterioFiltroMultiploExp.getTiFiltroMultiplo().equals("TIPO_UNI_DOC")) {
                        inserisciFiltroMultiploTipoUd(idStrutCorrente, criterioFiltroMultiploExp, criterioRaggr);
                    } else if (criterioFiltroMultiploExp.getTiFiltroMultiplo().equals("REGISTRO_UNI_DOC")) {
                        inserisciFiltroMultiploRegistroUnitaDoc(idStrutCorrente, criterioFiltroMultiploExp,
                                criterioRaggr);
                    } else if (criterioFiltroMultiploExp.getTiFiltroMultiplo().equals("TIPO_DOC")) {
                        inserisciFiltroMultiploTipoDoc(idStrutCorrente, criterioFiltroMultiploExp, criterioRaggr);
                    }
                }
            }

        }

    }

    public void importaCriteriRaggrFasc(BigDecimal idStrutCorrente, DecTipoFascicolo tipoFascicoloExp,
            DecTipoFascicolo tipoFascicolo, Set<BigDecimal> criteriRaggruppamentoFascDaLoggare) {

        for (DecSelCriterioRaggrFasc selOld : tipoFascicoloExp.getDecSelCriterioRaggrFascicolos()) {

            if (selOld.getTiSel()
                    .equals(it.eng.parer.entity.constraint.DecSelCriterioRaggrFasc.TiSelFasc.TIPO_FASCICOLO.name())) {

                // Recupero il criterio raggruppamento fascicolo, se esiste
                DecCriterioRaggrFasc criterioFasc = crfHelper.getDecCriterioRaggrFascByStrutturaCorrenteAndCriterio(
                        idStrutCorrente, selOld.getDecCriterioRaggrFasc().getNmCriterioRaggr());

                // Se il criterio è nullo, lo inserisco
                if (criterioFasc == null) {
                    criterioFasc = inserisciCriterioFasc(idStrutCorrente, selOld.getDecCriterioRaggrFasc(),
                            criteriRaggruppamentoFascDaLoggare);
                    // Da fotografare in XML se inseriti ex-novo
                    criteriRaggruppamentoFascDaLoggare.add(new BigDecimal(criterioFasc.getIdCriterioRaggrFasc()));
                }

                // Inserisco la relazione tra tipoFascicolo e criterioRaggrFascicolo
                DecSelCriterioRaggrFasc selNew = tipoFascicoloHelper.getDecSelCriterioRaggrFasc(idStrutCorrente,
                        criterioFasc.getNmCriterioRaggr(), tipoFascicoloExp.getNmTipoFascicolo());
                if (selNew == null) {
                    selNew = new DecSelCriterioRaggrFasc();
                    selNew.setTiSel(selOld.getTiSel());
                    selNew.setDecTipoFascicolo(tipoFascicolo);

                    selNew.setDecCriterioRaggrFasc(criterioFasc);
                    tipoUnitaDocHelper.getEntityManager().persist(selNew);
                    if (tipoFascicolo.getDecSelCriterioRaggrFascicolos() == null) {
                        tipoFascicolo.setDecSelCriterioRaggrFascicolos(new ArrayList<>());
                    }
                    tipoFascicolo.getDecSelCriterioRaggrFascicolos().add(selNew);
                }
            }
        }
    }

    public DecXsdDatiSpec inserisciXsdTipoUd(BigDecimal idStrutCorrente, DecXsdDatiSpec xsdDatiSpecExp,
            DecTipoUnitaDoc tipoUnitaDoc) throws ParerUserError {

        datiSpecEjb.parseStringaXsd(xsdDatiSpecExp.getBlXsd());

        // Inserisco il record in DEC_XSD_DATI_SPEC
        DecXsdDatiSpec xsdDatiSpec = inserisciXsdDatiSpec(idStrutCorrente, xsdDatiSpecExp, tipoUnitaDoc, null, null);
        for (DecXsdAttribDatiSpec xsdAttribDatiSpecExp : xsdDatiSpecExp.getDecXsdAttribDatiSpecs()) {
            DecAttribDatiSpec attribDatiSpecExp = xsdAttribDatiSpecExp.getDecAttribDatiSpec();
            // Controllo se DEC_ATTRIB_DATI_SPEC esiste per la struttura interessata
            DecAttribDatiSpec attribDatiSpec = datiSpecHelper.getDecAttribDatiSpecByName(idStrutCorrente,
                    attribDatiSpecExp.getTiUsoAttrib(), attribDatiSpecExp.getTiEntitaSacer(),
                    attribDatiSpecExp.getDecTipoUnitaDoc().getNmTipoUnitaDoc(), null, null, null,
                    attribDatiSpecExp.getNmAttribDatiSpec());
            // Inserisco il record in DEC_ATTRIB_DATI_SPEC
            if (attribDatiSpec == null) {
                attribDatiSpec = inserisciAttribDatiSpec(idStrutCorrente, attribDatiSpecExp, tipoUnitaDoc, null, null,
                        Constants.TipoEntitaSacer.UNI_DOC);
            }

            // Inserisco la relazione xsdDatiSpec-attribDatiSpec
            inserisciXsdAttribDatiSpec(xsdDatiSpec, attribDatiSpec, xsdAttribDatiSpecExp.getNiOrdAttrib(),
                    xsdAttribDatiSpecExp.getDsAttribDatiSpec());
        }
        return xsdDatiSpec;
    }

    public DecXsdDatiSpec inserisciXsdTipoDoc(BigDecimal idStrutCorrente, DecXsdDatiSpec xsdDatiSpecExp,
            DecTipoDoc tipoDoc) {
        // Inserisco il record in DEC_XSD_DATI_SPEC
        DecXsdDatiSpec xsdDatiSpec = inserisciXsdDatiSpec(idStrutCorrente, xsdDatiSpecExp, null, tipoDoc, null);
        for (DecXsdAttribDatiSpec xsdAttribDatiSpecExp : xsdDatiSpecExp.getDecXsdAttribDatiSpecs()) {
            DecAttribDatiSpec attribDatiSpecExp = xsdAttribDatiSpecExp.getDecAttribDatiSpec();
            // Controllo se DEC_ATTRIB_DATI_SPEC esiste per la struttura interessata
            DecAttribDatiSpec attribDatiSpec = datiSpecHelper.getDecAttribDatiSpecByName(idStrutCorrente,
                    attribDatiSpecExp.getTiUsoAttrib(), attribDatiSpecExp.getTiEntitaSacer(), null,
                    attribDatiSpecExp.getDecTipoDoc().getNmTipoDoc(), null, null,
                    attribDatiSpecExp.getNmAttribDatiSpec());
            // Inserisco il record in DEC_ATTRIB_DATI_SPEC
            if (attribDatiSpec == null) {
                attribDatiSpec = inserisciAttribDatiSpec(idStrutCorrente, attribDatiSpecExp, null, tipoDoc, null,
                        Constants.TipoEntitaSacer.DOC);
            }

            // Inserisco la relazione xsdDatiSpec-attribDatiSpec
            inserisciXsdAttribDatiSpec(xsdDatiSpec, attribDatiSpec, xsdAttribDatiSpecExp.getNiOrdAttrib(),
                    xsdAttribDatiSpecExp.getDsAttribDatiSpec());
        }
        return xsdDatiSpec;
    }

    public DecXsdDatiSpec inserisciXsdTipoCompDoc(BigDecimal idStrutCorrente, DecXsdDatiSpec xsdDatiSpecExp,
            DecTipoCompDoc tipoCompDoc) {
        // Inserisco il record in DEC_XSD_DATI_SPEC
        DecXsdDatiSpec xsdDatiSpec = inserisciXsdDatiSpec(idStrutCorrente, xsdDatiSpecExp, null, null, tipoCompDoc);
        for (DecXsdAttribDatiSpec xsdAttribDatiSpecExp : xsdDatiSpecExp.getDecXsdAttribDatiSpecs()) {
            DecAttribDatiSpec attribDatiSpecExp = xsdAttribDatiSpecExp.getDecAttribDatiSpec();
            // Controllo se DEC_ATTRIB_DATI_SPEC esiste per la struttura interessata
            DecAttribDatiSpec attribDatiSpec = datiSpecHelper.getDecAttribDatiSpecByName(idStrutCorrente,
                    attribDatiSpecExp.getTiUsoAttrib(), attribDatiSpecExp.getTiEntitaSacer(), null, null,
                    attribDatiSpecExp.getDecTipoCompDoc().getNmTipoCompDoc(), null,
                    attribDatiSpecExp.getNmAttribDatiSpec());
            // Inserisco il record in DEC_ATTRIB_DATI_SPEC
            if (attribDatiSpec == null) {
                attribDatiSpec = inserisciAttribDatiSpec(idStrutCorrente, attribDatiSpecExp, null, null, tipoCompDoc,
                        Constants.TipoEntitaSacer.COMP);
            }

            // Inserisco la relazione xsdDatiSpec-attribDatiSpec
            inserisciXsdAttribDatiSpec(xsdDatiSpec, attribDatiSpec, xsdAttribDatiSpecExp.getNiOrdAttrib(),
                    xsdAttribDatiSpecExp.getDsAttribDatiSpec());
        }
        return xsdDatiSpec;
    }

    public void importaXsdTipoUd(BigDecimal idStrutCorrente, DecTipoUnitaDoc tipoUnitaDocExp,
            DecTipoUnitaDoc tipoUnitaDoc) throws ParerUserError {
        // Importa XSD
        List<DecXsdDatiSpec> xsdDatiSpecExpList = tipoUnitaDocExp.getDecXsdDatiSpecs();
        // Per ogni versione DEC_XSD_DATI_SPEC
        for (DecXsdDatiSpec xsdDatiSpecExp : xsdDatiSpecExpList) {

            // Controlla se esiste la versione xsd del tipo unit\u00E0 doc su DB
            DecXsdDatiSpec xsdDatiSpec = datiSpecHelper.getDecXsdDatiSpec(idStrutCorrente, xsdDatiSpecExp.getTiUsoXsd(),
                    xsdDatiSpecExp.getTiEntitaSacer(), xsdDatiSpecExp.getDecTipoUnitaDoc().getNmTipoUnitaDoc(), null,
                    null, null, xsdDatiSpecExp.getNmSistemaMigraz(), xsdDatiSpecExp.getCdVersioneXsd());

            // Se la versione XSD NON \u00E8 presente su DB...
            if (xsdDatiSpec == null) {
                ///////////////////
                // INSERISCI XSD //
                ///////////////////
                inserisciXsdTipoUd(idStrutCorrente, xsdDatiSpecExp, tipoUnitaDoc);
            }
        }
    }

    public void importaXsdTipoDoc(BigDecimal idStrutCorrente, DecTipoDoc tipoDocExp, DecTipoDoc tipoDoc) {
        // Verifico XSD associati al tipoDoc
        List<DecXsdDatiSpec> xsdDatiSpecExpList = tipoDocExp.getDecXsdDatiSpecs();
        for (DecXsdDatiSpec xsdDatiSpecExp : xsdDatiSpecExpList) {

            // Controlla se esiste la versione xsd del tipo doc su DB
            DecXsdDatiSpec xsdDatiSpec = datiSpecHelper.getDecXsdDatiSpec(idStrutCorrente, xsdDatiSpecExp.getTiUsoXsd(),
                    xsdDatiSpecExp.getTiEntitaSacer(), null, xsdDatiSpecExp.getDecTipoDoc().getNmTipoDoc(), null, null,
                    xsdDatiSpecExp.getNmSistemaMigraz(), xsdDatiSpecExp.getCdVersioneXsd());
            // Se la versione XSD NON \u00E8 presente su DB
            if (xsdDatiSpec == null) {
                ///////////////////
                // INSERISCI XSD //
                ///////////////////
                inserisciXsdTipoDoc(idStrutCorrente, xsdDatiSpecExp, tipoDoc);
            }
        }
    }

    public void importaXsdTipoCompDoc(BigDecimal idStrutCorrente, DecTipoCompDoc tipoCompDocExp,
            DecTipoCompDoc tipoCompDoc) {
        // Verifico XSD associati al tipoCompDoc
        List<DecXsdDatiSpec> xsdDatiSpecExpList = tipoCompDocExp.getDecXsdDatiSpecs();
        for (DecXsdDatiSpec xsdDatiSpecExp : xsdDatiSpecExpList) {

            // Controlla se esiste la versione xsd del tipo doc su DB
            DecXsdDatiSpec xsdDatiSpec = datiSpecHelper.getDecXsdDatiSpec(idStrutCorrente, xsdDatiSpecExp.getTiUsoXsd(),
                    xsdDatiSpecExp.getTiEntitaSacer(), null, null,
                    xsdDatiSpecExp.getDecTipoCompDoc().getNmTipoCompDoc(),
                    xsdDatiSpecExp.getDecTipoCompDoc().getDecTipoStrutDoc().getNmTipoStrutDoc(),
                    xsdDatiSpecExp.getNmSistemaMigraz(), xsdDatiSpecExp.getCdVersioneXsd());
            // Se la versione XSD NON \u00E8 presente su DB
            if (xsdDatiSpec == null) {
                ///////////////////
                // INSERISCI XSD //
                ///////////////////
                inserisciXsdTipoCompDoc(idStrutCorrente, xsdDatiSpecExp, tipoCompDoc);
            }
        }
    }

    public DecRegistroUnitaDoc inserisciRegistroUd(BigDecimal idStrutCorrente,
            DecRegistroUnitaDoc registroUnitaDocExp) {
        DecRegistroUnitaDoc registroUnitaDoc = inserisciRegistro(idStrutCorrente, registroUnitaDocExp);

        // Inserisco i record relativi agli "anni" ed a "parte numero registro"
        for (DecAaRegistroUnitaDoc aaRegistroUnitaDocExp : registroUnitaDocExp.getDecAaRegistroUnitaDocs()) {
            DecAaRegistroUnitaDoc decAaRegistroUnitaDoc = inserisciAaRegistroUnitaDoc(aaRegistroUnitaDocExp,
                    registroUnitaDoc);
            for (DecParteNumeroRegistro parteNumeroRegistro : aaRegistroUnitaDocExp.getDecParteNumeroRegistros()) {
                inserisciParteNumeroRegistro(parteNumeroRegistro, decAaRegistroUnitaDoc);
            }
        }

        return registroUnitaDoc;
    }

    public DecRegistroUnitaDoc recuperaRegistroUd(BigDecimal idStrutCorrente, DecRegistroUnitaDoc registroUnitaDocExp) {
        String cdRegistroUnitaDocExp = registroUnitaDocExp.getCdRegistroUnitaDoc();
        DecRegistroUnitaDoc registroUnitaDoc = registroHelper.getDecRegistroUnitaDocByName(cdRegistroUnitaDocExp,
                idStrutCorrente);

        // Se il registro esiste, verifico che gli anni dell'XML non si sovrappongano a quelli gi\u00E0 presenti
        for (DecAaRegistroUnitaDoc decAaRegistroUnitaDocExp : registroUnitaDocExp.getDecAaRegistroUnitaDocs()) {
            boolean anniSovrapposti = false;
            for (DecAaRegistroUnitaDoc decAaRegistroUnitaDocDB : registroUnitaDoc.getDecAaRegistroUnitaDocs()) {
                // anniSovrapposti(decExp, dec)
                if (anniSovrapposti = checkAnniSovrapposti(decAaRegistroUnitaDocExp, decAaRegistroUnitaDocDB)) {
                    break;
                }
            }

            // Se gli anni non sono sovrapposti, inserisco gli anni e le parti numero
            if (!anniSovrapposti) {
                DecAaRegistroUnitaDoc decAaRegistroUnitaDoc = inserisciAaRegistroUnitaDoc(decAaRegistroUnitaDocExp,
                        registroUnitaDoc);
                for (DecParteNumeroRegistro decParteNumeroRegistro : decAaRegistroUnitaDocExp
                        .getDecParteNumeroRegistros()) {
                    inserisciParteNumeroRegistro(decParteNumeroRegistro, decAaRegistroUnitaDoc);
                }
            }

        }
        return registroUnitaDoc;
    }

    public DecTipoDoc gestisciTipoDoc(BigDecimal idStrutCorrente, DecTipoDocAmmesso tipoDocAmmessoExp,
            DecTipoStrutUnitaDoc tipoStrutUnitaDoc, List<CoppiaTipiDoc> tipiDocImportati) {
        DecTipoDoc tipoDocExp = tipoDocAmmessoExp.getDecTipoDoc();
        String nmTipoDocExp = tipoDocExp.getNmTipoDoc();

        DecTipoDoc tipoDoc = tipoDocHelper.getDecTipoDocByName(nmTipoDocExp, idStrutCorrente);

        // Se il tipo doc NON \u00E8 gi\u00E0 presente, lo inserisco
        if (tipoDoc == null) {
            tipoDoc = inserisciTipoDoc(idStrutCorrente, tipoDocExp);
            tipiDocImportati.add(new CoppiaTipiDoc(tipoDoc, tipoDocExp));
        }

        // Importa Xsd per tipo doc
        importaXsdTipoDoc(idStrutCorrente, tipoDocExp, tipoDoc);

        // Inserisco la relazione tra tipoStrutUnitaDoc e tipoDoc
        DecTipoDocAmmesso tipoDocAmmesso = tipoUnitaDocHelper.getDecTipoDocAmmessoByName(idStrutCorrente,
                tipoDoc.getNmTipoDoc(), tipoStrutUnitaDoc.getNmTipoStrutUnitaDoc());
        if (tipoDocAmmesso == null) {
            inserisciTipoDocAmmesso(tipoDocAmmessoExp, tipoStrutUnitaDoc, tipoDoc);
        }

        return tipoDoc;
    }

    private class CoppiaTipiDoc {

        DecTipoDoc tipoDoc;
        DecTipoDoc tipoDocExp;

        public CoppiaTipiDoc(DecTipoDoc tipoDoc, DecTipoDoc tipoDocExp) {
            this.tipoDoc = tipoDoc;
            this.tipoDocExp = tipoDocExp;
        }

        public DecTipoDoc getTipoDoc() {
            return tipoDoc;
        }

        public void setTipoDoc(DecTipoDoc tipoDoc) {
            this.tipoDoc = tipoDoc;
        }

        public DecTipoDoc getTipoDocExp() {
            return tipoDocExp;
        }

        public void setTipoDocExp(DecTipoDoc tipoDocExp) {
            this.tipoDocExp = tipoDocExp;
        }
    }

    public DecTipoStrutDoc gestisciTipoStrutDoc(BigDecimal idStrutCorrente,
            DecTipoStrutDocAmmesso tipoStrutDocAmmessoExp, DecTipoDoc tipoDoc,
            Set<BigDecimal> tipiStrutturaDocumentoDaLoggare, Set<BigDecimal> tipiRappresentazioneComponenteDaLoggare) {
        DecTipoStrutDoc tipoStrutDocExp = tipoStrutDocAmmessoExp.getDecTipoStrutDoc();
        String nmTipoStrutDocExp = tipoStrutDocExp.getNmTipoStrutDoc();

        DecTipoStrutDoc tipoStrutDoc = tipoStrutDocHelper.getDecTipoStrutDocByName(nmTipoStrutDocExp, idStrutCorrente);

        // Se il Tipo Struttura Documento NON \u00E8 gi\u00E0 presente
        if (tipoStrutDoc == null) {
            tipoStrutDoc = inserisciTipoStrutDoc(idStrutCorrente, tipoStrutDocExp);
        }

        // Se la relazione tipoStrutDoc-tipoDoc manca, la inserisco
        DecTipoStrutDocAmmesso tipoStrutDocAmmesso = tipoDocHelper.getDecTipoStrutDocAmmessoByName(idStrutCorrente,
                nmTipoStrutDocExp, tipoDoc.getNmTipoDoc());
        if (tipoStrutDocAmmesso == null) {
            inserisciTipoStrutDocAmmesso(tipoDoc, tipoStrutDoc);
            tipiStrutturaDocumentoDaLoggare.add(new BigDecimal(tipoStrutDoc.getIdTipoStrutDoc()));
        }

        // Per ogni DEC_TIPO_COMP_DOC dell'XML
        for (DecTipoCompDoc tipoCompDocExp : tipoStrutDocExp.getDecTipoCompDocs()) {
            gestisciTipoCompDoc(idStrutCorrente, tipoCompDocExp, nmTipoStrutDocExp,
                    tipiRappresentazioneComponenteDaLoggare);
        }

        return tipoStrutDoc;
    }

    public DecTipoCompDoc gestisciTipoCompDoc(BigDecimal idStrutCorrente, DecTipoCompDoc tipoCompDocExp,
            String nmTipoStrutDoc, Set<BigDecimal> tipiRappresentazioneComponenteDaLoggare) {
        String nmTipoCompDocExp = tipoCompDocExp.getNmTipoCompDoc();

        // Verifico se esiste gi\u00E0 su DB il DecTipoCompDoc
        DecTipoCompDoc tipoCompDoc = tipoStrutDocHelper.getDecTipoCompDocByName(idStrutCorrente, nmTipoStrutDoc,
                nmTipoCompDocExp);

        // Se non \u00E8 presente, lo inserisco
        if (tipoCompDoc == null) {
            tipoCompDoc = inserisciTipoCompDoc(idStrutCorrente, nmTipoStrutDoc, tipoCompDocExp);
        }

        // Importa Xsd per tipo comp doc
        importaXsdTipoCompDoc(idStrutCorrente, tipoCompDocExp, tipoCompDoc);

        // "RAMO" DEC_TIPO_RAPPR_AMMESSO
        List<DecTipoRapprAmmesso> tipoRapprAmmessoExpList = tipoCompDocExp.getDecTipoRapprAmmessos();

        for (DecTipoRapprAmmesso tipoRapprAmmessoExp : tipoRapprAmmessoExpList) {
            DecTipoRapprComp tipoRapprCompExp = tipoRapprAmmessoExp.getDecTipoRapprComp();
            String nmTipoRapprComp = tipoRapprCompExp.getNmTipoRapprComp();

            DecTipoRapprComp tipoRapprComp = tipoRapprHelper.getDecTipoRapprCompByName(nmTipoRapprComp,
                    idStrutCorrente);

            if (tipoRapprComp == null) {
                tipoRapprComp = inserisciTipoRapprComp(idStrutCorrente, tipoRapprCompExp);
                // Da fotografare in XML se inseriti ex-novo
                tipiRappresentazioneComponenteDaLoggare.add(new BigDecimal(tipoRapprComp.getIdTipoRapprComp()));
            }

            // Verifico se il tipoRapprAmmesso \u00E8 inserito anche in DEC_TIPO_RAPPR_AMMESSO (relazione
            // tipoCompDoc-tipoRapprComp)
            DecTipoRapprAmmesso tipoRapprAmmesso = tipoStrutDocHelper.getDecTipoRapprAmmessoByParentId(
                    tipoCompDoc.getIdTipoCompDoc(), tipoRapprComp.getIdTipoRapprComp());

            // Se il tipoRapprAmmesso non \u00E8 presente, lo creo e lo salvo su DB
            if (tipoRapprAmmesso == null) {
                inserisciTipoRapprAmmesso(tipoCompDoc, tipoRapprComp);
            }
        }
        return tipoCompDoc;
    }

    public DecCategTipoUnitaDoc gestisciCategoria(DecTipoUnitaDoc tipoUnitaDocExp) {
        // Ricavo il categTipoUnitaDoc dal tipoUnitaDoc dell'Xml
        DecCategTipoUnitaDoc categTipoUnitaDocExp = tipoUnitaDocExp.getDecCategTipoUnitaDoc();
        // Verifico se su DB esiste la categoria appena ricavata
        DecCategTipoUnitaDoc categTipoUnitaDoc = tipoUnitaDocHelper
                .getDecCategTipoUnitaDocByCodeLike(categTipoUnitaDocExp.getCdCategTipoUnitaDoc());
        // Se non esiste lo creo e lo salvo
        if (categTipoUnitaDoc == null) {
            categTipoUnitaDoc = inserisciCategTipoUnitaDoc(categTipoUnitaDocExp);
        }
        return categTipoUnitaDoc;
    }

    public String partitionOK(BigDecimal idStrut) {
        return struttureHelper.partitionOK(idStrut);
    }

    /**
     * Restituisce il rowBean della prima struttura template disponibile per l'ambiente passato come parametro. Null in
     * caso non siano disponibili strutture template
     *
     * @param idAmbiente
     *            id ambiente
     *
     * @return OrgStrutRowBean bean struttura
     */
    public OrgStrutRowBean getFirstStrutturaTemplateDisponibilePerAmbienteSelezionato(BigDecimal idAmbiente) {
        OrgStrutRowBean strutRowBean = null;
        OrgStrut strut = null;
        try {
            strut = struttureHelper.getFirstOrgStrutTemplatePerAmbienteAndTipoDefTemplateEnte(idAmbiente, null);
            if (strut != null) {
                strutRowBean = (OrgStrutRowBean) Transform.entity2RowBean(strut);
            }
        } catch (Exception ex) {
            logger.error(
                    "Errore durante il recupero di una struttura tgemplate " + ExceptionUtils.getRootCauseMessage(ex),
                    ex);
        }
        return strutRowBean;
    }

    /**
     * Restituisce un array di interi contenente come valori gli indici da utilizzare per creare le strutture template
     *
     * @param numStruttureTemplateDaCreare
     *            il numero di strutture template
     *
     * @return indiciPerCreazioneTemplate: l'array, lungo quante strutture template devo creare, che mi dice con quali
     *         indici crearle
     */
    public int[] getProgressiviPerCreazioneStruttureTemplate(int numStruttureTemplateDaCreare) {
        // Preparo il mio array da restituire e lo fillo con valori 0
        int[] indiciPerCreazioneTemplate = new int[numStruttureTemplateDaCreare];
        Arrays.fill(indiciPerCreazioneTemplate, 0);
        // Ricavo i progressivi già presenti su DB
        List<Integer> listaProgressiviPresenti = struttureHelper.getProgressiviTemplatePresentiSuDB();
        // Se ho dei progressi (quindi delle strutture template) allora inizio a lavorarci su...
        if (!listaProgressiviPresenti.isEmpty()) {
            // Prendo l'ultimo progressivo su DB
            int ultimoProgressivoSuDB = listaProgressiviPresenti.get(listaProgressiviPresenti.size() - 1);
            // Creo un array "di appoggio" di lunghezza il progressivo più alto di strutture template disponibili
            int[] tmpArray = new int[ultimoProgressivoSuDB];
            // Fillo col valore "0"
            Arrays.fill(tmpArray, 0);
            // Inserisco nell'array gli indici trovati (li inserisco nelle loro posizioni-1)
            for (Integer progressivoPresente : listaProgressiviPresenti) {
                tmpArray[progressivoPresente - 1] = progressivoPresente;
            }
            // Bene... ora scorro l'array temporaneao e dove trovo "0"
            // ricavo l'indice per creare i nuovi template
            int count = 0;
            for (int i = 0; i < tmpArray.length; i++) {
                if (tmpArray[i] == 0) {
                    indiciPerCreazioneTemplate[count] = i + 1;
                    count++;
                }
                if (count == numStruttureTemplateDaCreare) {
                    break;
                }
            }
            // Se l'array con gli indiciPerCreazioneTemplate non è stato ancora
            // completamente riempito, i posti vuoti verranno colmati in progressione
            // partendo dall'ultima posizione inserita
            if (count < numStruttureTemplateDaCreare) {
                for (int j = count; j < numStruttureTemplateDaCreare; j++) {
                    indiciPerCreazioneTemplate[j] = ++ultimoProgressivoSuDB;
                }
            }
        } else {
            // ...altrimenti se non ho strutture template,
            // gli indici ripartono semplicemente da 0
            for (int i = 0; i < numStruttureTemplateDaCreare; i++) {
                indiciPerCreazioneTemplate[i] = i + 1;
            }
        }

        return indiciPerCreazioneTemplate;
    }

    private void loggaOggettiStruttura(LogParam param, OrgStrut strut) {
        /* Loggata di tutte le UD, Documenti e Registri */
        // TIPO UD
        if (strut.getDecTipoUnitaDocs() != null) {
            for (DecTipoUnitaDoc ogg : strut.getDecTipoUnitaDocs()) {
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA,
                        new BigDecimal(ogg.getIdTipoUnitaDoc()), param.getNomePagina());
            }
        }
        // TIPO DOC
        if (strut.getDecTipoDocs() != null) {
            for (DecTipoDoc ogg : strut.getDecTipoDocs()) {
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO,
                        new BigDecimal(ogg.getIdTipoDoc()), param.getNomePagina());
            }
        }
        // REGISTRI
        if (strut.getDecRegistroUnitaDocs() != null) {
            for (DecRegistroUnitaDoc ogg : strut.getDecRegistroUnitaDocs()) {
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_REGISTRO,
                        new BigDecimal(ogg.getIdRegistroUnitaDoc()), param.getNomePagina());
            }
        }
        // STRUTTURA DOCUMENTO
        if (strut.getDecTipoStrutDocs() != null) {
            for (DecTipoStrutDoc ogg : strut.getDecTipoStrutDocs()) {
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO,
                        new BigDecimal(ogg.getIdTipoStrutDoc()), param.getNomePagina());
            }
        }
        // Rappresentazione componenti
        if (strut.getDecTipoRapprComps() != null) {
            for (DecTipoRapprComp ogg : strut.getDecTipoRapprComps()) {
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_RAPPRESENTAZIONE_COMPONENTE,
                        new BigDecimal(ogg.getIdTipoRapprComp()), param.getNomePagina());
            }
        }
        // Criteri di ragguppamento
        if (strut.getDecCriterioRaggrs() != null) {
            for (DecCriterioRaggr ogg : strut.getDecCriterioRaggrs()) {
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                        new BigDecimal(ogg.getIdCriterioRaggr()), param.getNomePagina());
            }
        }
        // tipi Serie
        if (strut.getDecTipoSeries() != null) {
            for (DecTipoSerie ogg : strut.getDecTipoSeries()) {
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE,
                        new BigDecimal(ogg.getIdTipoSerie()), param.getNomePagina());
            }
        }
    }

    /**
     * Ricerca le strutture che rispondono ai parametri di ricerca impostati non associate al modello (non presenti
     * nella DEC_USO_MODELLO_TIPO_SERIE per l'id del modello da associare)
     *
     * @param idUtente
     *            id utente
     * @param nmStrut
     *            nome struttura
     * @param idEnte
     *            id ente
     * @param idAmbiente
     *            id ambiente
     * @param idModelloTipoSerie
     *            id modello tipo serie
     * @param filterValid
     *            true/false
     *
     * @return il tablebean contenente le strutture
     *
     * @throws ParerUserError
     *             errore generico
     */
    public OrgStrutTableBean getOrgStrutTableBean(long idUtente, String nmStrut, BigDecimal idEnte,
            BigDecimal idAmbiente, BigDecimal idModelloTipoSerie, Boolean filterValid) throws ParerUserError {
        OrgStrutTableBean table = new OrgStrutTableBean();
        List<OrgStrut> list = struttureHelper.retrieveOrgStrutList(idUtente, nmStrut, idEnte, idAmbiente,
                idModelloTipoSerie, Boolean.FALSE, filterValid);
        if (list != null && !list.isEmpty()) {
            try {
                for (OrgStrut strut : list) {
                    OrgStrutRowBean rowBean = (OrgStrutRowBean) Transform.entity2RowBean(strut);
                    rowBean.setString("nm_ente", strut.getOrgEnte().getNmEnte());
                    table.add(rowBean);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di strutture non associate al modello "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    public boolean registriDaImportareConTipoSerieMult(UUID uuid, List<String> nmTipoUnitaDocList) {
        // Ricavo la struttura presente nell'XML
        OrgStrut strutExp = strutCache.getOrgStrut(uuid);

        for (String nmTipoUnitaDoc : nmTipoUnitaDocList) {

            // Recupero il tipo Ud selezionato
            DecTipoUnitaDoc tipoUnitaDoxExp = getDecTipoUnitaDocExpSelected(strutExp, nmTipoUnitaDoc);

            // Controllo i registri associati al tipo ud selezionato
            if (tipoUnitaDoxExp.getDecTipoUnitaDocAmmessos() != null) {
                for (DecTipoUnitaDocAmmesso tipoUdAmmesso : tipoUnitaDoxExp.getDecTipoUnitaDocAmmessos()) {
                    if (tipoUdAmmesso.getDecRegistroUnitaDoc().getFlTipoSerieMult() != null
                            && tipoUdAmmesso.getDecRegistroUnitaDoc().getFlTipoSerieMult().equals("1")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Recupera le strutture da mostrare nella combo della pagina di scelta strutture dopo aver effettuato il login
     *
     * @param idUtente
     *            id utente
     *
     * @return IamAbilOrganizRowBean l'object array contenente i dati sulle strutture
     */
    public IamAbilOrganizRowBean getAmbEnteStrutDefault(long idUtente) {
        List<Object[]> abilStrut = struttureHelper.getAmbEnteStrutDefault(idUtente);
        IamAbilOrganizRowBean rowBean = null;
        try {
            // trasformo la lista di entity (risultante della query) in un tablebean
            if (abilStrut != null && !abilStrut.isEmpty()) {
                rowBean = (IamAbilOrganizRowBean) Transform.entity2RowBean(abilStrut.get(0)[0]);
                rowBean.setObject("idAmbiente", abilStrut.get(0)[1]);
                rowBean.setObject("idEnte", abilStrut.get(0)[2]);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Impossibile recuperare le strutture per l'utente");
        }
        return rowBean;
    }

    public boolean isEnteWithStruts(BigDecimal idEnte) {
        return struttureHelper.countOrgStrut(idEnte) > 0;
    }

    /**
     * Aggiorno i servizi di conservazione e attivazione presenti sui tipi ud della struttura considerata
     *
     * @param idStrut
     */
    private void updateTipiServizioOnTipiUdInStruttura(BigDecimal idStrut) {
        List<DecTipoUnitaDoc> tipiUd = tipoUnitaDocHelper.retrieveDecTipoUnitaDoc(idStrut);
        for (DecTipoUnitaDoc tipoUd : tipiUd) {
            DecVCalcTiServOnTipoUd calcTiServOnTipoUd = tipoUnitaDocHelper.getDecVCalcTiServOnTipoUd(idStrut,
                    new BigDecimal(tipoUd.getDecCategTipoUnitaDoc().getIdCategTipoUnitaDoc()), "CLASSE_ENTE");
            if (calcTiServOnTipoUd != null) {
                OrgTipoServizio tipoServizioConserv = calcTiServOnTipoUd.getIdTipoServizioConserv() != null
                        ? struttureHelper.findById(OrgTipoServizio.class, calcTiServOnTipoUd.getIdTipoServizioConserv())
                        : null;
                OrgTipoServizio tipoServizioAttiv = calcTiServOnTipoUd.getIdTipoServizioAttiv() != null
                        ? struttureHelper.findById(OrgTipoServizio.class, calcTiServOnTipoUd.getIdTipoServizioAttiv())
                        : null;
                tipoUd.setOrgTipoServizio(tipoServizioConserv);
                tipoUd.setOrgTipoServizioAttiv(tipoServizioAttiv);
            }
            DecVCalcTiServOnTipoUd calcTiServOnTipoUd2 = tipoUnitaDocHelper.getDecVCalcTiServOnTipoUd(idStrut,
                    new BigDecimal(tipoUd.getDecCategTipoUnitaDoc().getIdCategTipoUnitaDoc()), "NO_CLASSE_ENTE");
            if (calcTiServOnTipoUd != null) {
                OrgTipoServizio tipoServizioConserv = calcTiServOnTipoUd2.getIdTipoServizioConserv() != null
                        ? struttureHelper.findById(OrgTipoServizio.class,
                                calcTiServOnTipoUd2.getIdTipoServizioConserv())
                        : null;
                OrgTipoServizio tipoServizioAttiv = calcTiServOnTipoUd2.getIdTipoServizioAttiv() != null
                        ? struttureHelper.findById(OrgTipoServizio.class, calcTiServOnTipoUd2.getIdTipoServizioAttiv())
                        : null;
                tipoUd.setOrgTipoServConservTipoUd(tipoServizioConserv);
                tipoUd.setOrgTipoServAttivTipoUd(tipoServizioAttiv);
            }
        }
    }

    public List<BigDecimal> getIdAmbitoTerritorialePerRicerca(List<BigDecimal> regioniList,
            List<BigDecimal> provinceList, List<BigDecimal> formeAssociateList) {
        List<BigDecimal> idAmbitoTerritList = new ArrayList<>();
        // Metto tutte le forme associate presenti in online tra gli idAmbitoTerrit da ricercare
        if (!formeAssociateList.isEmpty()) {
            idAmbitoTerritList.addAll(formeAssociateList);
        }
        if (!provinceList.isEmpty()) {
            // Controllo se ci sono figli selezionati nell'online: se non ci sono piazzo la provincia e tutti i suoi
            // figli
            // salvati su DB, come ambiti territoriali da ricercare
            for (BigDecimal idProvincia : provinceList) {
                if (!struttureHelper.haFigliPresentiInSottoLivelloOnlineList(idProvincia, formeAssociateList)) {
                    idAmbitoTerritList.add(idProvincia);
                    idAmbitoTerritList.addAll(struttureHelper.getIdAmbitoTerritChildList(idProvincia));
                }
            }
        }
        if (!regioniList.isEmpty()) {
            for (BigDecimal idRegione : regioniList) {
                if (!struttureHelper.haFigliPresentiInSottoLivelloOnlineList(idRegione, provinceList)) {
                    idAmbitoTerritList.add(idRegione);
                    List<BigDecimal> figliRegione = struttureHelper.getIdAmbitoTerritChildList(idRegione);
                    for (BigDecimal provincia : figliRegione) {
                        idAmbitoTerritList.add(provincia);
                        idAmbitoTerritList.addAll(struttureHelper.getIdAmbitoTerritChildList(provincia));
                    }
                }
            }
        }
        return idAmbitoTerritList;
    }

    public BigDecimal getIdAmbitoTerritorialePerRicerca(BigDecimal regione, BigDecimal provincia,
            BigDecimal formaAssociata) {
        // Ricavo i valori da utilizzare per ricercare l'ambito territoriale
        List<BigDecimal> idAmbitoTerrit1Livello = new ArrayList<>();
        if (regione != null) {
            idAmbitoTerrit1Livello.add(regione);
        }
        List<BigDecimal> idAmbitoTerrit2Livello = new ArrayList<>();
        if (provincia != null) {
            idAmbitoTerrit2Livello.add(provincia);
        }
        List<BigDecimal> idAmbitoTerrit3Livello = new ArrayList<>();
        if (formaAssociata != null) {
            idAmbitoTerrit3Livello.add(formaAssociata);
        }
        List<BigDecimal> result = getIdAmbitoTerritorialePerRicerca(idAmbitoTerrit1Livello, idAmbitoTerrit2Livello,
                idAmbitoTerrit3Livello);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    public void insertValoriParametriStrutturaTemplate(BigDecimal idStrut) {
        OrgStrut strut = struttureHelper.findById(OrgStrut.class, idStrut);

        // Ricavo il parametro
        AplParamApplic tiTempoScadChius = configurationHelper
                .getParamApplic(CostantiDB.ParametroAppl.TI_TEMPO_SCAD_CHIUS);
        AplParamApplic niTempoScadChius = configurationHelper
                .getParamApplic(CostantiDB.ParametroAppl.NI_TEMPO_SCAD_CHIUS);
        AplParamApplic flGestFascicoli = configurationHelper.getParamApplic(CostantiDB.ParametroAppl.FL_GEST_FASCICOLI);
        AplParamApplic numMaxCompCriterioRaggr = configurationHelper
                .getParamApplic(CostantiDB.ParametroAppl.NUM_MAX_COMP_CRITERIO_RAGGR);
        String numMaxCompCriterioRaggrWarn = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NUM_MAX_COMP_CRITERIO_RAGGR_WARN);

        amministrazioneEjb.insertAplValoreParamApplic(null, strut, null, null,
                BigDecimal.valueOf(tiTempoScadChius.getIdParamApplic()), TiAppart.STRUT.name(), "GIORNI");
        amministrazioneEjb.insertAplValoreParamApplic(null, strut, null, null,
                BigDecimal.valueOf(niTempoScadChius.getIdParamApplic()), TiAppart.STRUT.name(), "30");
        amministrazioneEjb.insertAplValoreParamApplic(null, strut, null, null,
                BigDecimal.valueOf(flGestFascicoli.getIdParamApplic()), TiAppart.STRUT.name(), "0");
        amministrazioneEjb.insertAplValoreParamApplic(null, strut, null, null,
                BigDecimal.valueOf(numMaxCompCriterioRaggr.getIdParamApplic()), TiAppart.STRUT.name(),
                numMaxCompCriterioRaggrWarn);
    }

    public TiEnteConvenz getTiEnteConvenzUser(long idUser) {
        UsrUser user = struttureHelper.findById(UsrUser.class, idUser);
        return user.getSiOrgEnteSiam().getTiEnteConvenz();
    }

    /*
     * Verifica se il codice normalizzato passato è univoco sul db; se non è univoco tenta di aggiungere il carattere
     * "_" alla fine della stringa finche non lo trova univoco sul DB e lo torna indietro. Viene fatto fino al
     * raggiungimento del centesimo carattere della stringa dopodiché si ferma.
     */
    public String getCodStrutturaNormalizzatoUnivoco(String codice) {
        StringBuilder codiceNormalizzato = new StringBuilder(codice);
        int numUnderscoreAggiuntivi = NUM_CARATTERI_CODICE_STRUTTURA_NORMALIZZATO - codice.length();

        for (int t = 0; t < numUnderscoreAggiuntivi; t++) {
            if (struttureHelper.isCodStrutturaNormalizzatoUnivoco(codiceNormalizzato.toString())) {
                break;
            } else {
                codiceNormalizzato.append("_");
            }
        }
        return codiceNormalizzato.toString();
    }

    public BaseTable getFunzioneParametriTableBean() {
        BaseTable funzioneTB = new BaseTable();
        List<String> funzioni = struttureHelper.getFunzioneParametri();
        if (funzioni != null && !funzioni.isEmpty()) {
            for (String funzione : funzioni) {
                BaseRow funzioneRB = new BaseRow();
                funzioneRB.setString("funzione", funzione);
                funzioneTB.add(funzioneRB);
            }
        }
        return funzioneTB;
    }

    public OrgStrutTableBean getStruttureAssociateEnteConvenz(BigDecimal idEnteConvenz) {
        OrgStrutTableBean strutTableBean = new OrgStrutTableBean();
        List<OrgStrut> strutList = struttureHelper.retrieveOrgStrutByEnteConvenz(idEnteConvenz);
        try {
            if (!strutList.isEmpty()) {
                strutTableBean = (OrgStrutTableBean) Transform.entities2TableBean(strutList);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return strutTableBean;
    }

    public OrgVCorrPingTableBean retrieveOrgVCorrPingList(BigDecimal idStrut) {
        OrgVCorrPingTableBean corrPingTableBean = new OrgVCorrPingTableBean();
        OrgStrut strut = struttureHelper.findById(OrgStrut.class, idStrut);
        final OrgEnte orgEnte = strut.getOrgEnte();
        final OrgAmbiente orgAmbiente = orgEnte.getOrgAmbiente();
        List<OrgVCorrPing> lista;
        lista = corrispondenzeHelper.retrieveOrgVCorrPingList(idStrut, new BigDecimal(orgEnte.getIdEnte()),
                new BigDecimal(orgAmbiente.getIdAmbiente()));

        if (!lista.isEmpty()) {
            try {
                for (OrgVCorrPing orgVCorrPing : lista) {
                    OrgVCorrPingRowBean row = (OrgVCorrPingRowBean) Transform.entity2RowBean(orgVCorrPing);
                    switch (row.getTiDichVers()) {
                    case "STRUTTURA":
                        row.setObject("ti_corrispondenza", "Corrispondenza su struttura " + row.getNmEntita());
                        break;
                    case "ENTE":
                        row.setObject("ti_corrispondenza", "Corrispondenza su ente " + row.getNmEntita());
                        break;
                    case "AMBIENTE":
                        row.setObject("ti_corrispondenza", "Corrispondenza su ambiente " + row.getNmEntita());
                        break;
                    default:
                        break;
                    }
                    row.setObject("vers", row.getNmAmbienteVers() + " - " + row.getNmVers());
                    corrPingTableBean.add(row);
                }
            } catch (Exception e) {
                logger.error("Errore nel recupero delle corrispondenze di Ping per la struttura : "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new IllegalStateException(
                        "Errore inatteso nel recupero delle corrispondenze di Ping per la struttura");
            }
        }

        return corrPingTableBean;
    }

}
