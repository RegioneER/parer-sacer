package it.eng.parer.job.indiceAipSerieUd.ejb;

import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.SerFileVerSerie;
import it.eng.parer.entity.SerStatoSerie;
import it.eng.parer.entity.SerStatoVerSerie;
import it.eng.parer.entity.SerVerSerieDaElab;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.indiceAipSerieUd.helper.CreazioneIndiceAipSerieUdHelper;
import it.eng.parer.job.indiceAipSerieUd.utils.CreazioneIndiceAipSerieUdUtil;
import it.eng.parer.serie.ejb.SerieEjb;
import it.eng.parer.serie.helper.SerieHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.utils.CostantiDB;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_FIRSTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_LASTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_TAXCODE;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_FORMALNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_TAXCODE;
import it.eng.parer.ws.xml.usmainResp.IdCType;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.naming.NamingException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gilioli_p
 */
@Stateless(mappedName = "ElaborazioneRigaIndiceAipVersioneSerieUd")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ElaborazioneRigaIndiceAipVersioneSerieUd {

    Logger log = LoggerFactory.getLogger(ElaborazioneRigaIndiceAipVersioneSerieUd.class);

    @EJB
    public CreazioneIndiceAipSerieUdHelper ciasudHelper;
    @EJB
    private SerieHelper serieHelper;
    @EJB
    private SerieEjb serieEjb;
    @EJB
    private ConfigurationHelper confHelper;
    @EJB
    private UserHelper userHelper;
    @EJB
    private XmlContextCache xmlContextCache;

    /* Ricavo i valori degli Agent dalla tabella APL_PARAM_APPLIC */
    private static final List<String> agentParam = Arrays.asList(AGENT_PRESERVER_FORMALNAME, AGENT_PRESERVER_TAXCODE,
            AGENT_PRESERVATION_MNGR_TAXCODE, AGENT_PRESERVATION_MNGR_LASTNAME, AGENT_PRESERVATION_MNGR_FIRSTNAME);

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creaIndiceAipSerieUd(long idVerSerieDaElab, String creatingApplicationProducer) throws NamingException,
            ValidationException, JAXBException, MarshalException, NoSuchAlgorithmException, IOException {
        SerVerSerieDaElab verSerieDaElab = serieHelper.findById(SerVerSerieDaElab.class, idVerSerieDaElab);
        String versioneSerie = verSerieDaElab.getSerVerSerie().getCdVerSerie();
        String codiceSerie = verSerieDaElab.getSerVerSerie().getSerSerie().getCdCompositoSerie();

        OrgStrut orgStrut = serieHelper.findById(OrgStrut.class, verSerieDaElab.getIdStrut().longValue());
        CSVersatore csv = new CSVersatore();
        csv.setStruttura(orgStrut.getNmStrut());
        csv.setEnte(orgStrut.getOrgEnte().getNmEnte());
        csv.setAmbiente(orgStrut.getOrgEnte().getOrgAmbiente().getNmAmbiente());
        // sistema (new URN)
        String sistemaConservazione = confHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE,
                null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);
        csv.setSistemaConservazione(sistemaConservazione);

        log.info(ElaborazioneRigaIndiceAipVersioneSerieUd.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- "
                + "Inizio creazione XML indice AIP per la versione serie "
                + verSerieDaElab.getSerVerSerie().getIdVerSerie());

        CreazioneIndiceAipSerieUdUtil indiceAipSerieUdUtil = new CreazioneIndiceAipSerieUdUtil();

        /* Data registrazione (sysdate) */
        Date dataRegistrazione = new GregorianCalendar().getTime();

        /**
         * GENERAZIONE INDICE AIP VERSIONE SERIE UD
         */
        OrgStrut strut = serieHelper.findById(OrgStrut.class, verSerieDaElab.getIdStrut());
        Map<String, String> mappaAgenti = confHelper.getParamApplicMapValue(agentParam,
                BigDecimal.valueOf(strut.getOrgEnte().getOrgAmbiente().getIdAmbiente()), verSerieDaElab.getIdStrut(),
                null, null, CostantiDB.TipoAplVGetValAppart.STRUT);
        IdCType idc = indiceAipSerieUdUtil.generaIndiceAIPSerieUd(verSerieDaElab, dataRegistrazione, mappaAgenti,
                creatingApplicationProducer);

        StringWriter tmpWriter = marshallIdC(idc);

        /* Persisto il file dell'indice AIP versione serie UD */
        SerFileVerSerie serFileVerSerie = serieHelper.storeFileIntoSerFileVerSerie(
                verSerieDaElab.getSerVerSerie().getIdVerSerie(), CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO.name(),
                tmpWriter.toString().getBytes(), "1.0", verSerieDaElab.getIdStrut(), dataRegistrazione);

        // EVO#16492
        /* Calcolo e persisto urn dell'indice AIP della serie */
        serieHelper.storeSerUrnFileVerSerie(serFileVerSerie, csv, codiceSerie, versioneSerie);
        // end EVO#16492

        log.info(ElaborazioneRigaIndiceAipVersioneSerieUd.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- "
                + "Fine creazione XML indice AIP per la versione serie "
                + verSerieDaElab.getSerVerSerie().getIdVerSerie());

        // Definisco la data di assunzione del nuovo stato pari a data corrente
        verSerieDaElab.setDtRegStatoVerSerie(dataRegistrazione);

        log.info(ElaborazioneRigaIndiceAipVersioneSerieUd.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- "
                + "Registro il nuovo STATO della VERSIONE della SERIE: DA_FIRMARE");
        BigDecimal pgStatoVerSerie = ciasudHelper
                .getUltimoProgressivoSerStatoVerSerie(verSerieDaElab.getSerVerSerie().getIdVerSerie());
        BigDecimal idAmbiente = BigDecimal.valueOf(strut.getOrgEnte().getOrgAmbiente().getIdAmbiente());
        Long idUserIam = userHelper
                .findIamUser(confHelper.getValoreParamApplic("USERID_CREAZIONE_IX_AIP_SERIE", idAmbiente,
                        BigDecimal.valueOf(strut.getIdStrut()), null, null, CostantiDB.TipoAplVGetValAppart.STRUT))
                .getIdUserIam();

        SerStatoVerSerie statoVerSerie = serieEjb.createSerStatoVerSerie(pgStatoVerSerie.add(BigDecimal.ONE),
                CostantiDB.StatoVersioneSerie.DA_FIRMARE.name(), "Creazione indice AIP serie", null, idUserIam,
                new Date(), verSerieDaElab.getSerVerSerie().getIdVerSerie());
        // Aggiorna l'identificatore dello stato corrente della versione
        // della serie assegnando l'identificatore dello stato inserito
        serieHelper.insertEntity(statoVerSerie, false);
        verSerieDaElab.getSerVerSerie().setIdStatoVerSerieCor(new BigDecimal(statoVerSerie.getIdStatoVerSerie()));

        log.info(ElaborazioneRigaIndiceAipVersioneSerieUd.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- "
                + "Registro il nuovo STATO della SERIE: AIP_GENERATO");
        Long idSerie = verSerieDaElab.getSerVerSerie().getSerSerie().getIdSerie();
        BigDecimal pgStatoSerie = ciasudHelper.getUltimoProgressivoSerStatoSerie(idSerie);
        SerStatoSerie statoSerie = serieEjb.createSerStatoSerie(pgStatoSerie.add(BigDecimal.ONE),
                CostantiDB.StatoConservazioneSerie.AIP_GENERATO.name(), "Creazione indice AIP serie", null, idUserIam,
                new Date(), idSerie);
        // Aggiorna l'identificatore dello stato corrente della serie
        // assegnando l'identificatore dello stato inserito
        serieHelper.insertEntity(statoSerie, false);
        verSerieDaElab.getSerVerSerie().getSerSerie().setIdStatoSerieCor(new BigDecimal(statoSerie.getIdStatoSerie()));

        log.info(ElaborazioneRigaIndiceAipVersioneSerieUd.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- "
                + "Aggiorno lo STATO della SERIE DA ELABORARE: DA_FIRMARE");
        // Aggiorno lo stato della versione serie da elaborare assegnando stato DA_FIRMARE
        verSerieDaElab.setTiStatoVerSerie(CostantiDB.StatoVersioneSerie.DA_FIRMARE.name());
    }

    private StringWriter marshallIdC(IdCType idc) throws ValidationException, JAXBException, MarshalException {
        it.eng.parer.ws.xml.usmainResp.ObjectFactory objFct_IdCType = new it.eng.parer.ws.xml.usmainResp.ObjectFactory();
        JAXBElement<IdCType> element_IdCType = objFct_IdCType.createIdC(idc);

        StringWriter tmpWriter = new StringWriter();
        Marshaller tmpMarshaller = xmlContextCache.getVersRespUniSincroCtx_IdC_Serie().createMarshaller();
        tmpMarshaller.setSchema(xmlContextCache.getSchemaOfVersRespUniSincro());
        tmpMarshaller.marshal(element_IdCType, tmpWriter);
        return tmpWriter;
    }
}
