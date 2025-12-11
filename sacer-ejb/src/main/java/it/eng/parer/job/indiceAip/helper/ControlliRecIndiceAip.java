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

package it.eng.parer.job.indiceAip.helper;

import com.rometools.utils.Strings;
import it.eng.paginator.util.HibernateUtils;
import it.eng.parer.entity.*;
import it.eng.parer.entity.constraint.AroUpdDatiSpecUnitaDoc;
import it.eng.parer.entity.constraint.AroUpdDatiSpecUnitaDoc.TiEntitaAroUpdDatiSpecUnitaDoc;
import it.eng.parer.entity.constraint.AroUpdDatiSpecUnitaDoc.TiUsoXsdAroUpdDatiSpecUnitaDoc;
import it.eng.parer.entity.constraint.AroVersIniDatiSpec.TiEntitaSacerAroVersIniDatiSpec;
import it.eng.parer.entity.constraint.AroVersIniDatiSpec.TiUsoXsdAroVersIniDatiSpec;
import it.eng.parer.entity.constraint.DecModelloXsdUd.TiModelloXsdUd;
import it.eng.parer.entity.constraint.VrsUrnXmlSessioneVers.TiUrnXmlSessioneVers;
import it.eng.parer.job.dto.SessioneVersamentoExt;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.viewEntity.AroVLisaipudSistemaMigraz;
import it.eng.parer.viewEntity.AroVVisCompAip;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

import static it.eng.parer.util.Utils.bigDecimalFromLong;

/**
 *
 * @author Fioravanti_F
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ControlliRecIndiceAip")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliRecIndiceAip {

    private static final Logger log = LoggerFactory.getLogger(ControlliRecIndiceAip.class);
    public static final String JAVAX_PERSISTENCE_FETCHGRAPH = "javax.persistence.fetchgraph";

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    @EJB
    private ObjectStorageService objectStorageService;

    public RispostaControlli leggiXmlVersamentiAip(long idAroIndiceAipUdDaElab) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<SessioneVersamentoExt> tmpSveList = new ArrayList<>();

        log.debug("Lettura xml di versamento per AIP - INIZIO");
        AroIndiceAipUdDaElab udDaElab = entityManager.find(AroIndiceAipUdDaElab.class,
                idAroIndiceAipUdDaElab);

        /*
         * recupero la sessione relativa al versamento originale dell'UD. per ora non ho bisogno di
         * conoscerne l'elenco dei documenti
         */
        log.debug("Ricavo la sessione di versamento per l'UD id={}",
                udDaElab.getAroUnitaDoc().getIdUnitaDoc());
        String queryStr = "select t from VrsSessioneVers t "
                + "where t.aroUnitaDoc.idUnitaDoc = :idUnitaDoc " + "and t.aroDoc is null "
                + "and t.tiStatoSessioneVers = 'CHIUSA_OK' "
                + "and t.tiSessioneVers = 'VERSAMENTO' ";

        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idUnitaDoc", udDaElab.getAroUnitaDoc().getIdUnitaDoc());

        List<VrsSessioneVers> vsv = query.getResultList();
        if (!vsv.isEmpty()) {
            SessioneVersamentoExt sveVersamentoOrig = new SessioneVersamentoExt();
            sveVersamentoOrig.setIdUnitaDoc(vsv.get(0).getAroUnitaDoc().getIdUnitaDoc());
            sveVersamentoOrig.setIdSessioneVers(vsv.get(0).getIdSessioneVers());
            sveVersamentoOrig.setDataSessioneVers(vsv.get(0).getDtChiusura());
            sveVersamentoOrig.setTipoSessione(
                    Constants.TipoSessione.valueOf(vsv.get(0).getTiSessioneVers()));
            tmpSveList.add(sveVersamentoOrig);
            //
            /*
             * ricavo la lista dei documenti versati ed aggiunti riferiti da questo indice AIP da
             * elaborare. La lista dovrebbe essere sempre non-vuota poiché deve contenere almeno i
             * documenti versati nel versamento iniziale, che vengono sempre riportati.
             */
            queryStr = "SELECT DISTINCT ad from AroDoc ad " + "join ad.aroStrutDocs asd "
                    + "join asd.aroCompDocs acd " + "join acd.aroCompIndiceAipDaElabs acdi "
                    + "where acdi.aroIndiceAipUdDaElab.idIndiceAipDaElab = :idIndiceAipDaElabIn "
                    + "order by ad.dtCreazione, ad.niOrdDoc, ad.pgDoc ";
            EntityGraph<AroDoc> entityGraph = entityManager.createEntityGraph(AroDoc.class);
            entityGraph.addAttributeNodes("decTipoDoc");
            query = entityManager.createQuery(queryStr);
            query.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);
            query.setParameter("idIndiceAipDaElabIn", udDaElab.getIdIndiceAipDaElab());
            List<AroDoc> ads = query.getResultList();
            for (AroDoc tmpAd : ads) {
                if (tmpAd.getTiCreazione()
                        .equals(CostantiDB.TipoCreazioneDoc.AGGIUNTA_DOCUMENTO.name())) {
                    log.debug("Ricavo la sessione di versamento del documento aggiunto id={}",
                            tmpAd.getIdDoc());
                    /*
                     * per ogni doc trovato, ricavo la sessione di versamento e la accodo alla
                     * collection
                     */
                    queryStr = "select t from VrsSessioneVers t " + "where t.aroDoc.idDoc = :idDoc "
                            + "and t.tiStatoSessioneVers = 'CHIUSA_OK' ";
                    query = entityManager.createQuery(queryStr);
                    query.setParameter("idDoc", tmpAd.getIdDoc());
                    vsv = query.getResultList();
                    if (!vsv.isEmpty()) {
                        SessioneVersamentoExt sveAggiunta = new SessioneVersamentoExt();
                        sveAggiunta.setIdDoc(vsv.get(0).getAroDoc().getIdDoc());
                        sveAggiunta.setIdSessioneVers(vsv.get(0).getIdSessioneVers());
                        sveAggiunta.setDataSessioneVers(vsv.get(0).getDtChiusura());
                        sveAggiunta.setTipoSessione(
                                Constants.TipoSessione.valueOf(vsv.get(0).getTiSessioneVers()));
                        sveAggiunta.getDocumentiVersati().add(tmpAd);
                        tmpSveList.add(sveAggiunta);
                    }
                } else {
                    /*
                     * aggiungo alla lista dei documenti versati nella sessione di versamento
                     * originale, ricavata all'inizio del metodo
                     */
                    sveVersamentoOrig.getDocumentiVersati().add(tmpAd);
                }
            }

            /*
             * ricavo i documenti XML relativi ad ogni sessione di versamento individuata
             */
            for (SessioneVersamentoExt tmpsvExt : tmpSveList) {
                queryStr = "select xml from VrsXmlDatiSessioneVers xml "
                        + "where xml.vrsDatiSessioneVers.vrsSessioneVers.idSessioneVers = :idSessioneVers "
                        + "and xml.vrsDatiSessioneVers.tiDatiSessioneVers = 'XML_DOC' ";

                query = entityManager.createQuery(queryStr);
                query.setParameter("idSessioneVers", tmpsvExt.getIdSessioneVers());

                // xml from O.S.
                Map<String, String> xmlVersamentoOs = Collections.emptyMap();
                if (tmpsvExt.getTipoSessione().equals(Constants.TipoSessione.VERSAMENTO)
                        && !Objects.isNull(tmpsvExt.getIdUnitaDoc())) {
                    xmlVersamentoOs = objectStorageService
                            .getObjectSipUnitaDoc(tmpsvExt.getIdUnitaDoc());
                }
                if (tmpsvExt.getTipoSessione().equals(Constants.TipoSessione.AGGIUNGI_DOCUMENTO)
                        && !Objects.isNull(tmpsvExt.getIdDoc())) {
                    xmlVersamentoOs = objectStorageService.getObjectSipDoc(tmpsvExt.getIdDoc());
                }

                List<VrsXmlDatiSessioneVers> vxdsv = query.getResultList();

                for (VrsXmlDatiSessioneVers xml : vxdsv) {
                    SessioneVersamentoExt.DatiXml tmpDatiXml = new SessioneVersamentoExt().new DatiXml();
                    tmpDatiXml.setTipoXmlDati(xml.getTiXmlDati());
                    tmpDatiXml.setVersione(xml.getCdVersioneXml());
                    // Il backend ORA è Object storage ma l'xml è già stato migrato? il controllo
                    // sul null serve a
                    // questo
                    if (!xmlVersamentoOs.isEmpty() && Strings.isNull(xml.getBlXml())) {
                        tmpDatiXml.setXml(xmlVersamentoOs.get(xml.getTiXmlDati()));
                    } else {
                        tmpDatiXml.setXml(xml.getBlXml());
                    }
                    VrsUrnXmlSessioneVers urnXmlSessioneVers = IterableUtils.find(
                            xml.getVrsUrnXmlSessioneVers(),
                            object -> (object).getTiUrn().equals(TiUrnXmlSessioneVers.ORIGINALE));
                    if (urnXmlSessioneVers != null) {
                        tmpDatiXml.setUrn(urnXmlSessioneVers.getDsUrn());
                    } else {
                        tmpDatiXml.setUrn(xml.getDsUrnXmlVers());
                    }
                    tmpDatiXml.setHash(xml.getDsHashXmlVers());
                    tmpDatiXml.setAlgoritmo(xml.getDsAlgoHashXmlVers());
                    tmpDatiXml.setEncoding(xml.getCdEncodingHashXmlVers());
                    tmpsvExt.getXmlDatiSessioneVers().add(tmpDatiXml);
                }
            }

            rispostaControlli.setrObject(tmpSveList);
            rispostaControlli.setrBoolean(true);
        } else {
            rispostaControlli.setCodErr("666");
            rispostaControlli
                    .setDsErr("Errore interno: non sono stati eseguiti versamenti per l'UD "
                            + udDaElab.getAroUnitaDoc().getIdUnitaDoc());
        }

        log.debug("Lettura xml di versamento per AIP - FINE");
        return rispostaControlli;
    }

    public RispostaControlli leggiXmlVersamentiAipDaUnitaDoc(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<SessioneVersamentoExt> tmpSveList = new ArrayList<>();

        log.debug("Lettura xml di versamento per AIP - INIZIO");
        /*
         * recupero la sessione relativa al versamento originale dell'UD. per ora non ho bisogno di
         * conoscerne l'elenco dei documenti
         */
        log.debug("Ricavo la sessione di versamento per l'UD id={}", idUnitaDoc);
        String queryStr = "select t from VrsSessioneVers t "
                + "where t.aroUnitaDoc.idUnitaDoc = :idUnitaDoc " + "and t.aroDoc is null "
                + "and t.tiStatoSessioneVers = 'CHIUSA_OK' "
                + "and t.tiSessioneVers = 'VERSAMENTO' ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);

        List<VrsSessioneVers> vsv = query.getResultList();
        if (!vsv.isEmpty()) {
            SessioneVersamentoExt sveVersamentoOrig = new SessioneVersamentoExt();
            sveVersamentoOrig.setIdUnitaDoc(vsv.get(0).getAroUnitaDoc().getIdUnitaDoc());
            sveVersamentoOrig.setIdSessioneVers(vsv.get(0).getIdSessioneVers());
            sveVersamentoOrig.setDataSessioneVers(vsv.get(0).getDtChiusura());
            sveVersamentoOrig.setTipoSessione(
                    Constants.TipoSessione.valueOf(vsv.get(0).getTiSessioneVers()));
            tmpSveList.add(sveVersamentoOrig);
            //
            /*
             * ricavo la lista dei documenti versati ed aggiunti riferiti all'unita doc da
             * elaborare. La lista dovrebbe essere sempre non-vuota poiché deve contenere almeno i
             * documenti versati nel versamento iniziale, che vengono sempre riportati.
             */
            query = entityManager.createQuery(
                    "SELECT DISTINCT ad FROM AroDoc ad where ad.aroUnitaDoc.idUnitaDoc = :idUnitaDoc order by ad.dtCreazione, ad.niOrdDoc, ad.pgDoc ");
            query.setParameter("idUnitaDoc", idUnitaDoc);
            EntityGraph<AroDoc> entityGraph = entityManager.createEntityGraph(AroDoc.class);
            entityGraph.addAttributeNodes("decTipoDoc");
            query.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);
            List<AroDoc> ads = query.getResultList();
            for (AroDoc tmpAd : ads) {
                if (tmpAd.getTiCreazione()
                        .equals(CostantiDB.TipoCreazioneDoc.AGGIUNTA_DOCUMENTO.name())) {
                    log.debug("Ricavo la sessione di versamento del documento aggiunto id={}",
                            tmpAd.getIdDoc());
                    /*
                     * per ogni doc trovato, ricavo la sessione di versamento e la accodo alla
                     * collection
                     */
                    queryStr = "select t from VrsSessioneVers t " + "where t.aroDoc.idDoc = :idDoc "
                            + "and t.tiStatoSessioneVers = 'CHIUSA_OK' ";

                    query = entityManager.createQuery(queryStr);
                    query.setParameter("idDoc", tmpAd.getIdDoc());
                    vsv = query.getResultList();
                    if (!vsv.isEmpty()) {
                        SessioneVersamentoExt sveAggiunta = new SessioneVersamentoExt();
                        sveAggiunta.setIdDoc(vsv.get(0).getAroDoc().getIdDoc());
                        sveAggiunta.setIdSessioneVers(vsv.get(0).getIdSessioneVers());
                        sveAggiunta.setDataSessioneVers(vsv.get(0).getDtChiusura());
                        sveAggiunta.setTipoSessione(
                                Constants.TipoSessione.valueOf(vsv.get(0).getTiSessioneVers()));
                        sveAggiunta.getDocumentiVersati().add(tmpAd);
                        tmpSveList.add(sveAggiunta);
                    }
                } else {
                    /*
                     * aggiungo alla lista dei documenti versati nella sessione di versamento
                     * originale, ricavata all'inizio del metodo
                     */
                    sveVersamentoOrig.getDocumentiVersati().add(tmpAd);
                }
            }

            /*
             * ricavo i documenti XML relativi ad ogni sessione di versamento individuata
             */
            for (SessioneVersamentoExt tmpsvExt : tmpSveList) {
                queryStr = "select xml from VrsXmlDatiSessioneVers xml "
                        + "where xml.vrsDatiSessioneVers.vrsSessioneVers.idSessioneVers = :idSessioneVers "
                        + "and xml.vrsDatiSessioneVers.tiDatiSessioneVers = 'XML_DOC' ";

                query = entityManager.createQuery(queryStr);
                query.setParameter("idSessioneVers", tmpsvExt.getIdSessioneVers());

                // xml from O.S.
                Map<String, String> xmlVersamentoOs = Collections.emptyMap();
                if (tmpsvExt.getTipoSessione().equals(Constants.TipoSessione.VERSAMENTO)
                        && !Objects.isNull(tmpsvExt.getIdUnitaDoc())) {
                    xmlVersamentoOs = objectStorageService
                            .getObjectSipUnitaDoc(tmpsvExt.getIdUnitaDoc());
                }
                if (tmpsvExt.getTipoSessione().equals(Constants.TipoSessione.AGGIUNGI_DOCUMENTO)
                        && !Objects.isNull(tmpsvExt.getIdDoc())) {
                    xmlVersamentoOs = objectStorageService.getObjectSipDoc(tmpsvExt.getIdDoc());
                }

                List<VrsXmlDatiSessioneVers> vxdsv = query.getResultList();
                for (VrsXmlDatiSessioneVers xml : vxdsv) {
                    SessioneVersamentoExt.DatiXml tmpDatiXml = new SessioneVersamentoExt().new DatiXml();
                    tmpDatiXml.setTipoXmlDati(xml.getTiXmlDati());
                    tmpDatiXml.setVersione(xml.getCdVersioneXml());
                    // Il backend ORA è Object storage ma l'xml è già stato migrato? il controllo
                    // sul null serve a
                    // questo
                    if (!xmlVersamentoOs.isEmpty() && Strings.isNull(xml.getBlXml())) {
                        tmpDatiXml.setXml(xmlVersamentoOs.get(xml.getTiXmlDati()));
                    } else {
                        tmpDatiXml.setXml(xml.getBlXml());
                    }

                    VrsUrnXmlSessioneVers urnXmlSessioneVers = IterableUtils.find(
                            xml.getVrsUrnXmlSessioneVers(),
                            object -> (object).getTiUrn().equals(TiUrnXmlSessioneVers.ORIGINALE));
                    if (urnXmlSessioneVers != null) {
                        tmpDatiXml.setUrn(urnXmlSessioneVers.getDsUrn());
                    } else {
                        tmpDatiXml.setUrn(xml.getDsUrnXmlVers());
                    }
                    tmpDatiXml.setHash(xml.getDsHashXmlVers());
                    tmpDatiXml.setAlgoritmo(xml.getDsAlgoHashXmlVers());
                    tmpDatiXml.setEncoding(xml.getCdEncodingHashXmlVers());
                    tmpsvExt.getXmlDatiSessioneVers().add(tmpDatiXml);
                }
            }

            rispostaControlli.setrObject(tmpSveList);
            rispostaControlli.setrBoolean(true);
        } else {
            rispostaControlli.setCodErr("666");
            rispostaControlli.setDsErr(
                    "Errore interno: non sono stati eseguiti versamenti per l'UD " + idUnitaDoc);
        }

        log.debug("Lettura xml di versamento per AIP - FINE");
        return rispostaControlli;
    }

    // MEV#26419
    public RispostaControlli leggiXmlVersamentiModelloXsdUnitaDoc(String tiUsoModelloXsd,
            TiModelloXsdUd tiModelloXsdUd, long idUnitaDoc) {

        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VrsXmlModelloSessioneVers> lstXmlModelloSessioneVers = null;

        try {

            String queryStr = "select xms from VrsXmlModelloSessioneVers xms "
                    + "join xms.decUsoModelloXsdUniDoc uso_modello "
                    + "join uso_modello.decModelloXsdUd modello_xsd "
                    + "join xms.vrsDatiSessioneVers dati_ses "
                    + "join dati_ses.vrsSessioneVers ses "
                    + "where modello_xsd.tiUsoModelloXsd = :tiUsoModelloXsd "
                    + "and modello_xsd.tiModelloXsd = :tiModelloXsdUd "
                    + "and dati_ses.tiDatiSessioneVers = 'XML_DOC' "
                    + "and ses.aroUnitaDoc.idUnitaDoc = :idUnitaDoc " + "and ses.aroDoc is null "
                    + "and ses.tiStatoSessioneVers = 'CHIUSA_OK' "
                    + "and ses.tiSessioneVers = 'VERSAMENTO' ";
            EntityGraph<VrsXmlModelloSessioneVers> entityGraph = entityManager
                    .createEntityGraph(VrsXmlModelloSessioneVers.class);
            entityGraph.addSubgraph("decUsoModelloXsdUniDoc").addAttributeNodes("decModelloXsdUd");
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);
            query.setParameter("tiUsoModelloXsd", tiUsoModelloXsd);
            query.setParameter("tiModelloXsdUd", tiModelloXsdUd);
            query.setParameter("idUnitaDoc", idUnitaDoc);

            lstXmlModelloSessioneVers = query.getResultList();
            rispostaControlli.setrObject(lstXmlModelloSessioneVers);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiXmlVersamentiModelloXsdUnitaDoc "
                            + e.getMessage()));
            log.error("Eccezione nella lettura modello xsd unità documentaria", e);
        }
        return rispostaControlli;
    }
    // end MEV#26419

    public RispostaControlli leggiXmlVersamentiAipUpdDaUnitaDoc(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroXmlUpdUnitaDoc> xmlupds = new ArrayList<>();

        try {
            /*
             * ricavo la lista degli aggiornamenti versati riferiti all'unita doc da elaborare.
             */
            String queryStr = "SELECT DISTINCT upd from AroUpdUdIndiceAipDaElab updi "
                    + "join updi.aroUpdUnitaDoc upd " + "join upd.aroUnitaDoc ud "
                    + "where ud.idUnitaDoc = :idUnitaDoc " + "order by upd.tsIniSes ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            List<AroUpdUnitaDoc> upds = query.getResultList();
            for (AroUpdUnitaDoc tmpUpd : upds) {
                queryStr = "select xml from AroXmlUpdUnitaDoc xml "
                        + "where xml.aroUpdUnitaDoc.idUpdUnitaDoc = :idUpdUnitaDoc ";

                query = entityManager.createQuery(queryStr);
                query.setParameter("idUpdUnitaDoc", tmpUpd.getIdUpdUnitaDoc());

                xmlupds.addAll(query.getResultList());
            }
            rispostaControlli.setrObject(xmlupds);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiXmlVersamentiAipUpdDaUnitaDoc "
                            + e.getMessage()));
            log.error("Eccezione nella lettura xml di versamento aggiornamento metadati ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiFascicoliSec(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroArchivSec> lstArchivSec;

        try {
            String queryStr = "select ud.aroArchivSecs " + "from AroUnitaDoc ud "
                    + "where ud.idUnitaDoc = :idUnitaDoc ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            lstArchivSec = query.getResultList();
            rispostaControlli.setrObject(lstArchivSec);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiFascicoliSec " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella dei fascicoli ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiAroVersIniUnitaDoc(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroVersIniUnitaDoc> lstVersIniUnitaDoc = null;

        try {
            String queryStr = "select viud " + "from AroVersIniUnitaDoc viud "
                    + "where viud.aroUnitaDoc.idUnitaDoc = :idUnitaDoc ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            lstVersIniUnitaDoc = query.getResultList();
            AroVersIniUnitaDoc viud;
            if (!lstVersIniUnitaDoc.isEmpty()) {
                viud = lstVersIniUnitaDoc.get(0);
                rispostaControlli.setrObject(viud);
            }
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiAroVersIniUnitaDoc " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella AroVersIniArchivSec ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiFascicoliSecVersIniUnitaDoc(long idVersIniUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroVersIniArchivSec> lstVersIniArchivSec;

        try {
            String queryStr = "select viud.aroVersIniArchivSecs " + "from AroVersIniUnitaDoc viud "
                    + "where viud.idVersIniUnitaDoc = :idVersIniUnitaDoc ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idVersIniUnitaDoc", idVersIniUnitaDoc);
            lstVersIniArchivSec = query.getResultList();
            rispostaControlli.setrObject(lstVersIniArchivSec);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiFascicoliSecVersIniUnitaDoc "
                            + e.getMessage()));
            log.error("Eccezione nella lettura della tabella AroVersIniArchivSec ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiVersamentiAipUpdPgMaxInCoda(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroUpdUnitaDoc> lstUpd = null;

        try {
            /*
             * ricavo l'aggiornamento in coda avente progressivo maggiore riferito all'unita doc da
             * elaborare.
             */
            String queryStr = "SELECT DISTINCT upd from AroUpdUdIndiceAipDaElab updi "
                    + "join updi.aroUpdUnitaDoc upd " + "join upd.aroUnitaDoc ud "
                    + "where ud.idUnitaDoc = :idUnitaDoc "
                    + "and upd.pgUpdUnitaDoc = (SELECT MAX(updInCoda.pgUpdUnitaDoc) FROM AroUpdUdIndiceAipDaElab updDaElab "
                    + "JOIN updDaElab.aroUpdUnitaDoc updInCoda "
                    + "WHERE updInCoda.aroUnitaDoc.idUnitaDoc = ud.idUnitaDoc) ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            lstUpd = query.getResultList();
            AroUpdUnitaDoc upd;
            if (!lstUpd.isEmpty()) {
                upd = lstUpd.get(0);
                rispostaControlli.setrObject(upd);
            }
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiVersamentiAipUpdPgMaxInCoda "
                            + e.getMessage()));
            log.error("Eccezione nella lettura aggiornamento metadati ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiFascicoliSecUpd(long idUpdUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroUpdArchivSec> lstUpdArchivSec;

        try {
            String queryStr = "select upd.aroUpdArchivSecs " + "from AroUpdUnitaDoc upd "
                    + "where upd.idUpdUnitaDoc = :idUpdUnitaDoc ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUpdUnitaDoc", idUpdUnitaDoc);
            lstUpdArchivSec = query.getResultList();
            rispostaControlli.setrObject(lstUpdArchivSec);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiFascicoliSecUpd " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella AroUpdArchivSec ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiUDocColleg(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroLinkUnitaDoc> lstLinkUD;
        try {
            String queryStr = "select ud " + "from AroLinkUnitaDoc ud "
                    + "where ud.aroUnitaDoc.idUnitaDoc = :idUnitaDoc ";
            EntityGraph<AroLinkUnitaDoc> entityGraph = entityManager
                    .createEntityGraph(AroLinkUnitaDoc.class);
            entityGraph.addSubgraph("aroUnitaDoc").addSubgraph("orgStrut").addSubgraph("orgEnte")
                    .addAttributeNodes("orgAmbiente");
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            query.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);
            lstLinkUD = query.getResultList();
            rispostaControlli.setrObject(lstLinkUD);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiUDocColleg " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella dei link unità doc ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiUDocCollegVersIniUpd(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroVersIniLinkUnitaDoc> lstLinkUDVersIniUpd;

        try {
            String queryStr = "select updLink " + "from AroVersIniLinkUnitaDoc updLink "
                    + "join updLink.aroVersIniUnitaDoc viud "
                    + "where viud.aroUnitaDoc.idUnitaDoc = :idUnitaDoc ";
            EntityGraph<AroVersIniLinkUnitaDoc> entityGraph = entityManager
                    .createEntityGraph(AroVersIniLinkUnitaDoc.class);
            entityGraph.addSubgraph("aroVersIniUnitaDoc").addSubgraph("aroUnitaDoc")
                    .addSubgraph("orgStrut").addSubgraph("orgEnte")
                    .addAttributeNodes("orgAmbiente");
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            query.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);
            lstLinkUDVersIniUpd = query.getResultList();
            rispostaControlli.setrObject(lstLinkUDVersIniUpd);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiUDocCollegVersIniUpd " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella degli aggiornamenti link unità doc ",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiUDocCollegUpd(long idUpdUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroUpdLinkUnitaDoc> lstLinkUDUpd;

        try {
            String queryStr = "select updLink " + "from AroUpdLinkUnitaDoc updLink "
                    + "where updLink.aroUpdUnitaDoc.idUpdUnitaDoc = :idUpdUnitaDoc ";
            EntityGraph<AroUpdLinkUnitaDoc> entityGraph = entityManager
                    .createEntityGraph(AroUpdLinkUnitaDoc.class);
            entityGraph.addSubgraph("aroUpdUnitaDoc").addSubgraph("orgStrut").addSubgraph("orgEnte")
                    .addAttributeNodes("orgAmbiente");
            Query query = entityManager.createQuery(queryStr);
            query.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);
            query.setParameter("idUpdUnitaDoc", idUpdUnitaDoc);
            lstLinkUDUpd = query.getResultList();
            rispostaControlli.setrObject(lstLinkUDUpd);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiUDocCollegUpd " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella degli aggiornamenti link unità doc ",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiDatiSpecEntity(CostantiDB.TipiUsoDatiSpec tipoUsoAttr,
            CostantiDB.TipiEntitaSacer tipoEntitySacer, long idEntitySacer) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<Object[]> lstdati;

        try {
            String tmpTipoEntita = null;
            switch (tipoEntitySacer) {
            case UNI_DOC:
                tmpTipoEntita = "uxsd.aroUnitaDoc.idUnitaDoc";
                break;
            case DOC:
                tmpTipoEntita = "uxsd.aroDoc.idDoc";
                break;
            case COMP:
            case SUB_COMP:
                tmpTipoEntita = "uxsd.aroCompDoc.idCompDoc";
                break;
            default:
                throw new IllegalArgumentException(
                        "Tipo entità " + tipoEntitySacer + " non prevista");
            }

            String queryStr = String.format(
                    "select uxsd.decXsdDatiSpec.cdVersioneXsd, datd.nmAttribDatiSpec ,avad.dlValore "
                            + "from AroValoreAttribDatiSpec avad "
                            + "join avad.decAttribDatiSpec datd "
                            + "join avad.aroUsoXsdDatiSpec uxsd "
                            + "join datd.decXsdAttribDatiSpecs dxads " + "where "
                            + "dxads.decXsdDatiSpec = uxsd.decXsdDatiSpec "
                            + "and uxsd.tiUsoXsd = :tipoUsoAttr "
                            + "and uxsd.tiEntitaSacer = :tipoEntitySacer "
                            + "and %s = :idEntitySacer " + "order by dxads.niOrdAttrib ",
                    tmpTipoEntita);

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("tipoUsoAttr", tipoUsoAttr.name());
            query.setParameter("tipoEntitySacer", tipoEntitySacer.name());
            query.setParameter("idEntitySacer", idEntitySacer);
            lstdati = query.getResultList();
            rispostaControlli.setrObject(lstdati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiDatiSpecEntity " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei dati specifici ", e);
        }

        return rispostaControlli;
    }

    public RispostaControlli leggiDatiSpecEntityVersIniUpd(TiUsoXsdAroVersIniDatiSpec tipoUsoAttr,
            TiEntitaSacerAroVersIniDatiSpec tipoEntitySacer, long idEntitySacer) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroVersIniDatiSpec> lstVersIniDatiSpec = null;

        try {

            String tmpTipoEntita = null;
            switch (tipoEntitySacer) {
            case UNI_DOC:
                tmpTipoEntita = "vids.aroVersIniUnitaDoc.idVersIniUnitaDoc";
                break;
            case DOC:
                tmpTipoEntita = "vids.aroVersIniDoc.idVersIniDoc";
                break;
            case COMP:
                tmpTipoEntita = "vids.aroVersIniComp.idVersIniComp";
                break;
            }

            String queryStr = String.format("select vids " + "from AroVersIniDatiSpec vids "
                    + "where vids.tiUsoXsd = :tipoUsoAttr "
                    + "and vids.tiEntitaSacer = :tipoEntitySacer " + "and %s = :idEntitySacer ",
                    tmpTipoEntita);

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("tipoUsoAttr", tipoUsoAttr);
            query.setParameter("tipoEntitySacer", tipoEntitySacer);
            query.setParameter("idEntitySacer", idEntitySacer);
            lstVersIniDatiSpec = query.getResultList();
            rispostaControlli.setrObject(lstVersIniDatiSpec);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiDatiSpecEntityVersIniUpd "
                            + e.getMessage()));
            log.error("Eccezione nella lettura dati specifici UniSincro", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiDatiSpecEntityUpd(TiUsoXsdAroUpdDatiSpecUnitaDoc tipoUsoAttr,
            TiEntitaAroUpdDatiSpecUnitaDoc tipoEntitySacer, long idEntitySacerUpd) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroUpdDatiSpecUnitaDoc> lstUpdDatiSpecUnitaDoc = null;

        try {

            String tmpTipoEntita = null;
            switch (tipoEntitySacer) {
            case UPD_UNI_DOC:
                tmpTipoEntita = "udsud.aroUpdUnitaDoc.idUpdUnitaDoc";
                break;
            case UPD_DOC:
                tmpTipoEntita = "udsud.aroUpdDocUnitaDoc.idUpdDocUnitaDoc";
                break;
            case UPD_COMP:
                tmpTipoEntita = "udsud.aroUpdCompUnitaDoc.idUpdCompUnitaDoc";
                break;
            }

            String queryStr = String.format("select udsud " + "from AroUpdDatiSpecUnitaDoc udsud "
                    + "where udsud.tiUsoXsd = :tipoUsoAttr "
                    + "and udsud.tiEntitaSacer = :tipoEntitySacer " + "and %s = :idEntitySacerUpd ",
                    tmpTipoEntita);

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("tipoUsoAttr", tipoUsoAttr);
            query.setParameter("tipoEntitySacer", tipoEntitySacer);
            query.setParameter("idEntitySacerUpd", idEntitySacerUpd);
            lstUpdDatiSpecUnitaDoc = query.getResultList();
            rispostaControlli.setrObject(lstUpdDatiSpecUnitaDoc);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiDatiSpecEntityUpd " + e.getMessage()));
            log.error("Eccezione nella lettura dati specifici UniSincro", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiSottoComponenti(AroCompDoc compPadre) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroCompDoc> lstDatiF;
        javax.persistence.Query query;

        try {
            String queryStr = "select acd from AroCompDoc acd "
                    + "where acd.aroCompDoc = :aroCompDoc";
            EntityGraph<AroCompDoc> entityGraph = entityManager.createEntityGraph(AroCompDoc.class);
            entityGraph.addAttributeNodes("aroCompDoc"); // carico anche il padre
            query = entityManager.createQuery(queryStr);
            query.setParameter("aroCompDoc", compPadre);
            query.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);
            lstDatiF = query.getResultList();
            rispostaControlli.setrObject(lstDatiF);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiSottoComponenti " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella dei sottocomponenti doc", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getVersioneSacer() {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        String appVersion = it.eng.spagoCore.ConfigSingleton.getInstance().getAppVersion();
        rispostaControlli.setrString(appVersion);
        rispostaControlli.setrBoolean(true);
        return rispostaControlli;
    }

    public RispostaControlli getVersioniPrecedentiAIP(Long idUnitaDoc) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            // Ricavo tutte le versioni precedenti dell'AIP
            String queryStr = "SELECT u FROM AroVerIndiceAipUd u "
                    + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                    + "ORDER BY u.pgVerIndiceAip DESC ";
            Query query = entityManager.createQuery(queryStr);
            EntityGraph<AroVerIndiceAipUd> entityGraph = entityManager
                    .createEntityGraph(AroVerIndiceAipUd.class);
            entityGraph.addAttributeNodes("aroUrnVerIndiceAipUds");
            query.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            List<AroVerIndiceAipUd> versioniPrecedenti = query.getResultList();
            rispostaControlli.setrObject(versioniPrecedenti);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione durante il recupero delle versioni precedenti dell'AIP "
                            + e.getMessage()));
            log.error("Eccezione durante il recupero delle versioni precedenti dell'AIP", e);
        }
        return rispostaControlli;
    }

    // EVO#20972:MEV#20971
    public RispostaControlli getVersioniPrecedentiAIPExternal(Long idUnitaDoc) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            // Ricavo tutte le versioni precedenti dell'AIP provenienti da altri
            // conservatori
            String queryStr = "SELECT u FROM AroVLisaipudSistemaMigraz u "
                    + "WHERE u.idUnitaDoc = :idUnitaDoc ";
            Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", bigDecimalFromLong(idUnitaDoc));
            List<AroVLisaipudSistemaMigraz> versioniPrecedentiExternal = query.getResultList();
            rispostaControlli.setrObject(versioniPrecedentiExternal);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione durante il recupero delle versioni precedenti dell'AIP provenienti da altri conservatori "
                            + e.getMessage()));
            log.error(
                    "Eccezione durante il recupero delle versioni precedenti dell'AIP provenienti da altri conservatori ",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getVolumiUnitaDocList(Long idUnitaDoc) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            /*
             * Determina i volumi di conservazione a cui appartiene l’unita doc.
             */
            String queryStr = "select audv from VolAppartUnitaDocVolume audv "
                    + "where audv.aroUnitaDoc.idUnitaDoc = :idUnitaDoc ";
            EntityGraph<VolAppartUnitaDocVolume> entityGraph = entityManager
                    .createEntityGraph(VolAppartUnitaDocVolume.class);
            entityGraph.addSubgraph("volVolumeConserv").addSubgraph("orgStrut")
                    .addSubgraph("orgEnte").addAttributeNodes("orgAmbiente");
            Query query = entityManager.createQuery(queryStr);
            query.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            List<VolAppartUnitaDocVolume> listaVolumiConserv = query.getResultList();
            rispostaControlli.setrObject(
                    listaVolumiConserv.stream().map(VolAppartUnitaDocVolume::getVolVolumeConserv)
                            .collect(Collectors.toList()));
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione durante il recupero dei volumi a cui appartiene l’unita documentaria "
                            + e.getMessage()));
            log.error(
                    "Eccezione durante il recupero dei volumi a cui appartiene l’unita documentaria ",
                    e);
        }
        return rispostaControlli;
    }
    // end EVO#20972:MEV#20971

    public RispostaControlli leggiComponentiDocumento(AroDoc aroDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroCompDoc> lstDatiF;
        javax.persistence.Query query;

        try {
            String queryStr = "select acd from AroCompDoc acd "
                    + "where acd.aroStrutDoc.aroDoc.idDoc = :idDoc " + "and acd.aroCompDoc is null";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idDoc", aroDoc.getIdDoc());
            lstDatiF = query.getResultList();
            rispostaControlli.setrObject(lstDatiF);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiComponentiDocumento AroDoc "
                            + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella dei componenti doc", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiComponenteDaVista(Long idCompDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroVVisCompAip> lstDati;
        javax.persistence.Query query;

        try {
            String queryStr = "SELECT u FROM AroVVisCompAip u " + "WHERE u.idCompDoc = :idCompDoc ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idCompDoc", HibernateUtils.bigDecimalFrom(idCompDoc));
            lstDati = query.getResultList();
            AroVVisCompAip comp;
            if (!lstDati.isEmpty()) {
                comp = lstDati.get(0);
                rispostaControlli.setrObject(comp);
                rispostaControlli.setrBoolean(true);
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiComponentiDaVista " + e.getMessage()));
            log.error("Eccezione nella lettura della vista dei componenti ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiComponenteDaVersIniUpd(Long idVersIniUnitaDoc, Long idCompDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroVersIniComp> lstDati;
        javax.persistence.Query query;

        try {
            String queryStr = "SELECT vic FROM AroVersIniComp vic " + "JOIN vic.aroVersIniDoc vid "
                    + "JOIN vid.aroVersIniUnitaDoc viud "
                    + "WHERE viud.idVersIniUnitaDoc = :idVersIniUnitaDoc "
                    + "AND vic.aroCompDoc.idCompDoc = :idCompDoc ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idVersIniUnitaDoc", idVersIniUnitaDoc);
            query.setParameter("idCompDoc", idCompDoc);
            lstDati = query.getResultList();
            AroVersIniComp comp;
            if (!lstDati.isEmpty()) {
                comp = lstDati.get(0);
                rispostaControlli.setrObject(comp);
            }
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiComponenteDaVersIniUpd "
                            + e.getMessage()));
            log.error("Eccezione nella lettura della tabella AroVersIniComp ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiComponenteDaUpd(Long idUpdUnitaDoc, Long idCompDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroUpdCompUnitaDoc> lstDati;
        javax.persistence.Query query;

        try {
            String queryStr = "SELECT ucud FROM AroUpdCompUnitaDoc ucud "
                    + "JOIN ucud.aroUpdDocUnitaDoc udud " + "JOIN udud.aroUpdUnitaDoc upd "
                    + "WHERE upd.idUpdUnitaDoc = :idUpdUnitaDoc "
                    + "AND ucud.aroCompDoc.idCompDoc = :idCompDoc ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idUpdUnitaDoc", idUpdUnitaDoc);
            query.setParameter("idCompDoc", idCompDoc);
            lstDati = query.getResultList();
            AroUpdCompUnitaDoc comp;
            if (!lstDati.isEmpty()) {
                comp = lstDati.get(0);
                rispostaControlli.setrObject(comp);
            }
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiComponenteDaUpd " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella AroUpdCompUnitaDoc ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiDocumentoDaVersIniUpd(Long idVersIniUnitaDoc, Long idDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroVersIniDoc> lstDati;
        javax.persistence.Query query;

        try {
            String queryStr = "SELECT vid FROM AroVersIniDoc vid "
                    + "JOIN vid.aroVersIniUnitaDoc viud "
                    + "WHERE viud.idVersIniUnitaDoc = :idVersIniUnitaDoc "
                    + "AND vid.aroDoc.idDoc = :idDoc ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idVersIniUnitaDoc", idVersIniUnitaDoc);
            query.setParameter("idDoc", idDoc);
            lstDati = query.getResultList();
            AroVersIniDoc doc;
            if (!lstDati.isEmpty()) {
                doc = lstDati.get(0);
                rispostaControlli.setrObject(doc);
            }
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiDocumentoDaVersIniUpd "
                            + e.getMessage()));
            log.error("Eccezione nella lettura della tabella AroVersIniDoc ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiDocumentoDaUpd(Long idUpdUnitaDoc, Long idDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroUpdDocUnitaDoc> lstDati;
        javax.persistence.Query query;

        try {
            String queryStr = "SELECT udud FROM AroUpdDocUnitaDoc udud "
                    + "JOIN udud.aroUpdUnitaDoc upd " + "WHERE upd.idUpdUnitaDoc = :idUpdUnitaDoc "
                    + "AND udud.aroDoc.idDoc = :idDoc ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idUpdUnitaDoc", idUpdUnitaDoc);
            query.setParameter("idDoc", idDoc);
            lstDati = query.getResultList();
            AroUpdDocUnitaDoc doc;
            if (!lstDati.isEmpty()) {
                doc = lstDati.get(0);
                rispostaControlli.setrObject(doc);
            }
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.leggiDocumentoDaUpd " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella AroUpdDocUnitaDoc ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getPrecedentiVersioniIndiceAip(Long idUnitaDoc) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroIndiceAipUd> lstIndice;
        javax.persistence.Query query;

        try {
            String queryStr = "SELECT u FROM AroIndiceAipUd u "
                    + "WHERE u.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                    + "AND u.tiFormatoIndiceAip = 'UNISYNCRO' ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            lstIndice = query.getResultList();
            if (!lstIndice.isEmpty()) {
                queryStr = "SELECT u FROM AroVerIndiceAipUd u "
                        + "WHERE u.aroIndiceAipUd = :aroIndiceAipUd ";
                query = entityManager.createQuery(queryStr);
                EntityGraph<AroVerIndiceAipUd> entityGraph = entityManager
                        .createEntityGraph(AroVerIndiceAipUd.class);
                entityGraph.addAttributeNodes("aroUrnVerIndiceAipUds");
                query.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);
                query.setParameter("aroIndiceAipUd", lstIndice.get(0));
                rispostaControlli.setrObject(query.getResultList());
            }
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAip.getPrecedentiVersioniIndiciAip "
                            + e.getMessage()));
            log.error("Eccezione nella lettura delle precedenti versioni indici AIP ", e);
        }
        return rispostaControlli;
    }

}
