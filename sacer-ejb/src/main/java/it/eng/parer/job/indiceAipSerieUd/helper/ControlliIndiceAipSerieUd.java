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

package it.eng.parer.job.indiceAipSerieUd.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroIndiceAipUd;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.constraint.SerUrnFileVerSerie.TiUrnFileVerSerie;
import it.eng.parer.entity.constraint.SerUrnIxVolVerSerie.TiUrnIxVolVerSerie;
import it.eng.parer.job.indiceAipSerieUd.dto.FileQuery_1_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.SelfDescriptionQuery_1_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_10_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_11_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_1_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_2_3_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_4_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_5_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_6_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_7_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_8_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_9_Bean;
import it.eng.parer.viewEntity.SerVCreaIxAipSerieUd;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.MessaggiWSBundle;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings({
        "unchecked" })
@Stateless(mappedName = "ControlliIndiceAipSerieUd")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliIndiceAipSerieUd {

    private static final Logger log = LoggerFactory.getLogger(ControlliIndiceAipSerieUd.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    public RispostaControlli getVersioneSacer() {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        String appVersion = it.eng.spagoCore.ConfigSingleton.getInstance().getAppVersion();
        rispostaControlli.setrString(appVersion);
        rispostaControlli.setrBoolean(true);
        return rispostaControlli;
    }

    public RispostaControlli getSelfDescriptionQuery1Data(Long idVerSerie) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<SelfDescriptionQuery_1_Bean> lstDati = new ArrayList<>();
        javax.persistence.Query query = null;
        try {
            String queryStr = "SELECT verSerieCor.idVerSerie, verSeriePrec.pgVerSerie, urnFileVerSeriePrec.dsUrn, fileVerSeriePrec.dsHashFile "
                    + "FROM SerUrnFileVerSerie urnFileVerSeriePrec JOIN urnFileVerSeriePrec.serFileVerSerie fileVerSeriePrec JOIN fileVerSeriePrec.serVerSerie verSeriePrec JOIN verSeriePrec.serVerSerie verSerieCor "
                    + "WHERE verSeriePrec.pgVerSerie < verSerieCor.pgVerSerie "
                    + "AND fileVerSeriePrec.tiFileVerSerie = 'IX_AIP_UNISINCRO' "
                    + "AND urnFileVerSeriePrec.tiUrn = :tiUrn "
                    + "AND verSerieCor.idVerSerie = :idVerSerie "
                    + "ORDER BY verSeriePrec.pgVerSerie ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idVerSerie", idVerSerie);
            query.setParameter("tiUrn", TiUrnFileVerSerie.NORMALIZZATO);
            List<Object[]> lstDatiObj = query.getResultList();
            for (Object[] dato : lstDatiObj) {
                SelfDescriptionQuery_1_Bean selfie = new SelfDescriptionQuery_1_Bean();
                selfie.setIdVerSerieCor((Long) dato[0]);
                selfie.setPgVerSeriePrec((BigDecimal) dato[1]);
                selfie.setDsUrnFilePrec((String) dato[2]);
                selfie.setDsHashFilePrec((String) dato[3]);
                lstDati.add(selfie);
            }
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliIndiceAipSerieUd.SelfDescriptionQuery_1_Bean "
                            + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei dati riguardanti il Self Description durante i controlli per la creazione dell'indice AIP versione serie UD ",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getVdCQuery1Data(Long idVerSerie) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VdCQuery_1_Bean> lstDati = new ArrayList<>();
        javax.persistence.Query query = null;
        try {
            String queryStr = "SELECT vdcQuery1 FROM SerVCreaIxAipSerieUd vdcQuery1 "
                    + "WHERE vdcQuery1.idVerSerie = :idVerSerie ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idVerSerie", BigDecimal.valueOf(idVerSerie));
            List<SerVCreaIxAipSerieUd> lstDatiObj = query.getResultList();
            for (SerVCreaIxAipSerieUd dato : lstDatiObj) {
                VdCQuery_1_Bean vdc = new VdCQuery_1_Bean();
                vdc.setAaSelezioneUd(dato.getAaSelezioneUd());
                vdc.setCdFirstUnitaDoc(dato.getCdFirstUnitaDoc());
                vdc.setCdLastUnitaDoc(dato.getCdLastUnitaDoc());
                vdc.setDsAutoreAcqInfo(dato.getDsAutoreAcqInfo());
                vdc.setDsAutoreAmbConten(dato.getDsAutoreAmbConten());
                vdc.setDsAutoreConserv(dato.getDsAutoreConserv());
                vdc.setDsAutoreCritOrd(dato.getDsAutoreCritOrd());
                vdc.setDsNotaAcqInfo(dato.getDsNotaAcqInfo());
                vdc.setDsNotaAmbConten(dato.getDsNotaAmbConten());
                vdc.setDsNotaConserv(dato.getDsNotaConserv());
                vdc.setDsNotaCritOrd(dato.getDsNotaCritOrd());
                vdc.setDtFineSelSerie(dato.getDtFineSelSerie());
                vdc.setDtFirstUnitaDoc(dato.getDtFirstUnitaDoc());
                vdc.setDtInizioSelSerie(dato.getDtInizioSelSerie());
                vdc.setDtLastUnitaDoc(dato.getDtLastUnitaDoc());
                vdc.setDtNotaAcqInfo(dato.getDtNotaAcqInfo());
                vdc.setDtNotaAmbConten(dato.getDtNotaAmbConten());
                vdc.setDtNotaConserv(dato.getDtNotaConserv());
                vdc.setDtNotaCritOrd(dato.getDtNotaCritOrd());
                vdc.setDtScarto(dato.getDtScarto());
                vdc.setIdVerSerie(dato.getIdVerSerie());
                vdc.setNiAnniConserv(dato.getNiAnniConserv());
                vdc.setNiPeriodoSelSerie(dato.getNiPeriodoSelSerie());
                vdc.setNiUnitaDoc(dato.getNiUnitaDoc());
                vdc.setTiConserv(dato.getTiConserv());
                vdc.setTiPeriodoSelSerie(dato.getTiPeriodoSelSerie());
                vdc.setTiSelezioneUd(dato.getTiSelezioneUd());
                lstDati.add(vdc);
            }
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliIndiceAipSerieUd.getVdCQuery_1_Data " + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei dati riguardanti il VdC durante i controlli per la creazione dell'indice AIP versione serie UD ",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getVdCQuery23Data(Long idVerSerie, String tiLacuna) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        List<VdCQuery_2_3_Bean> lstDati = new ArrayList<>();
        javax.persistence.Query query = null;
        try {
            String queryStr = "SELECT verSerieCor.idVerSerie, lacuna.tiModLacuna, lacuna.niIniLacuna, "
                    + "lacuna.niFinLacuna, lacuna.dlLacuna, lacuna.dlNotaLacuna "
                    + "FROM SerLacunaConsistVerSerie lacuna JOIN lacuna.serConsistVerSerie consist JOIN consist.serVerSerie verSerieCor "
                    + "WHERE verSerieCor.idVerSerie = :idVerSerie "
                    + "AND lacuna.tiLacuna = :tiLacuna "
                    + "ORDER BY lacuna.tiModLacuna, lacuna.dlLacuna, lacuna.niIniLacuna, lacuna.niFinLacuna ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idVerSerie", idVerSerie);
            query.setParameter("tiLacuna", tiLacuna);
            List<Object[]> lstDatiObj = query.getResultList();
            for (Object[] dato : lstDatiObj) {
                VdCQuery_2_3_Bean vdc = new VdCQuery_2_3_Bean();
                vdc.setIdVerSerieCor((Long) dato[0]);
                vdc.setTiModLacuna((String) dato[1]);
                vdc.setNiIniLacuna((BigDecimal) dato[2]);
                vdc.setNiFinLacuna((BigDecimal) dato[3]);
                vdc.setDlLacuna((String) dato[4]);
                vdc.setDlNotaLacuna((String) dato[5]);
                lstDati.add(vdc);
            }
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliIndiceAipSerieUd.getVdCQuery_2_3_Data " + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei dati riguardanti il VdC durante i controlli per la creazione dell'indice AIP versione serie UD ",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getVdCQuery4Data(Long idVerSerie) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VdCQuery_4_Bean> lstDati = new ArrayList<>();
        javax.persistence.Query query = null;
        try {
            String queryStr = "SELECT verSerieCor.idVerSerie, tiSerieUd.idTipoSerieUd, reg.cdRegistroUnitaDoc, tiUd.nmTipoUnitaDoc "
                    + "FROM DecTipoSerieUd tiSerieUd " + "JOIN tiSerieUd.decTipoUnitaDoc tiUd "
                    + "JOIN tiSerieUd.decRegistroUnitaDoc reg "
                    + "JOIN tiSerieUd.decTipoSerie tipoSerie " + "JOIN tipoSerie.serSeries serie "
                    + "JOIN serie.serVerSeries verSerieCor "
                    + "WHERE verSerieCor.idVerSerie = :idVerSerie "
                    + "ORDER BY tiUd.nmTipoUnitaDoc, reg.cdRegistroUnitaDoc ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idVerSerie", idVerSerie);
            List<Object[]> lstDatiObj = query.getResultList();
            for (Object[] dato : lstDatiObj) {
                VdCQuery_4_Bean vdc = new VdCQuery_4_Bean();
                vdc.setIdVerSerieCor((Long) dato[0]);
                vdc.setIdTipoSerieUd((Long) dato[1]);
                vdc.setCdRegistroUnitaDoc((String) dato[2]);
                vdc.setNmTipoUnitaDoc((String) dato[3]);
                lstDati.add(vdc);
            }
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliIndiceAipSerieUd.getVdCQuery_4_Data " + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei dati riguardanti il VdC durante i controlli per la creazione dell'indice AIP versione serie UD ",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getVdCQuery5Data(Long idTipoSerieUd) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VdCQuery_5_Bean> lstDati = new ArrayList<>();
        javax.persistence.Query query = null;
        try {
            String queryStr = "SELECT filtro.decTipoSerieUd.idTipoSerieUd, tiDoc.nmTipoDoc "
                    + "FROM DecFiltroSelUd filtro JOIN filtro.decTipoDoc tiDoc "
                    + "WHERE filtro.decTipoSerieUd.idTipoSerieUd = :idTipoSerieUd "
                    + "ORDER BY tiDoc.nmTipoDoc ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idTipoSerieUd", idTipoSerieUd);
            List<Object[]> lstDatiObj = query.getResultList();
            for (Object[] dato : lstDatiObj) {
                VdCQuery_5_Bean vdc = new VdCQuery_5_Bean();
                vdc.setIdTipoSerieUd((Long) dato[0]);
                vdc.setNmTipoDocPrinc((String) dato[1]);
                lstDati.add(vdc);
            }
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliIndiceAipSerieUd.getVdCQuery_5_Data " + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei dati riguardanti il VdC durante i controlli per la creazione dell'indice AIP versione serie UD ",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getVdCQuery6Data(Long idTipoSerieUd) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VdCQuery_6_Bean> lstDati = new ArrayList<>();
        javax.persistence.Query query = null;
        try {
            String queryStr = "SELECT filtroDatiSpec.decTipoSerieUd.idTipoSerieUd, filtroDatiSpec.idFiltroSelUdAttb, "
                    + "filtroDatiSpec.nmAttribDatiSpec, filtroDatiSpec.tiOper, filtroDatiSpec.dlValore "
                    + "FROM DecFiltroSelUdAttb filtroDatiSpec "
                    + "WHERE filtroDatiSpec.decTipoSerieUd.idTipoSerieUd = :idTipoSerieUd "
                    + "ORDER BY filtroDatiSpec.nmAttribDatiSpec ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idTipoSerieUd", idTipoSerieUd);
            List<Object[]> lstDatiObj = query.getResultList();
            for (Object[] dato : lstDatiObj) {
                VdCQuery_6_Bean vdc = new VdCQuery_6_Bean();
                vdc.setIdTipoSerieUd((Long) dato[0]);
                vdc.setIdFiltroSelUdAttb((Long) dato[1]);
                vdc.setNmAttribDatiSpec((String) dato[2]);
                vdc.setTiOper((String) dato[3]);
                vdc.setDlValore((String) dato[4]);
                lstDati.add(vdc);
            }
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliIndiceAipSerieUd.getVdCQuery_6_Data " + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei dati riguardanti il VdC durante i controlli per la creazione dell'indice AIP versione serie UD ",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getVdCQuery7Data(Long idFiltroSelUdAttb) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VdCQuery_7_Bean> lstDati = new ArrayList<>();
        javax.persistence.Query query = null;
        try {
            String queryStr = "SELECT defDatoSpec.decFiltroSelUdAttb.idFiltroSelUdAttb, defDatoSpec.tiEntitaSacer, "
                    + "defDatoSpec.nmTipoUnitaDoc, defDatoSpec.nmTipoDoc, defDatoSpec.dsListaVersioniXsd "
                    + "FROM DecFiltroSelUdDato defDatoSpec "
                    + "WHERE defDatoSpec.decFiltroSelUdAttb.idFiltroSelUdAttb = :idFiltroSelUdAttb "
                    + "ORDER BY defDatoSpec.tiEntitaSacer, defDatoSpec.nmTipoUnitaDoc, defDatoSpec.nmTipoDoc ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idFiltroSelUdAttb", idFiltroSelUdAttb);
            List<Object[]> lstDatiObj = query.getResultList();
            for (Object[] dato : lstDatiObj) {
                VdCQuery_7_Bean vdc = new VdCQuery_7_Bean();
                vdc.setIdFiltroSelUdAttb((Long) dato[0]);
                vdc.setTiEntitaSacer((String) dato[1]);
                vdc.setNmTipoUnitaDoc((String) dato[2]);
                vdc.setNmTipoDoc((String) dato[3]);
                vdc.setDsListaVersioniXsd((String) dato[4]);
                lstDati.add(vdc);
            }
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliIndiceAipSerieUd.getVdCQuery_7_Data " + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei dati riguardanti il VdC durante i controlli per la creazione dell'indice AIP versione serie UD ",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getVdCQuery8Data(BigDecimal idVerSerie) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VdCQuery_8_Bean> lstDati = new ArrayList<>();
        javax.persistence.Query query = null;
        try {
            String queryStr = "SELECT DISTINCT verSerieCor.idVerSerie, reg.cdRegistroUnitaDoc, reg.dsRegistroUnitaDoc "
                    + "FROM DecTipoSerieUd tiSerieUd " + "JOIN tiSerieUd.decRegistroUnitaDoc reg "
                    + "JOIN tiSerieUd.decTipoSerie tiSerie "
                    + "JOIN tiSerie.serSeries serie JOIN serie.serVerSeries verSerieCor "
                    + "WHERE verSerieCor.idVerSerie = :idVerSerie "
                    + "ORDER BY reg.cdRegistroUnitaDoc ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idVerSerie", longFromBigDecimal(idVerSerie));
            List<Object[]> lstDatiObj = query.getResultList();
            for (Object[] dato : lstDatiObj) {
                VdCQuery_8_Bean vdc = new VdCQuery_8_Bean();
                vdc.setIdVerSerie((Long) dato[0]);
                vdc.setCdRegistroUnitaDoc((String) dato[1]);
                vdc.setDsRegistroUnitaDoc((String) dato[2]);
                lstDati.add(vdc);
            }
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliIndiceAipSerieUd.getVdCQuery_8_Data " + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei dati riguardanti il VdC durante i controlli per la creazione dell'indice AIP versione serie UD ",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getVdCQuery9Data(BigDecimal idVerSerie) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VdCQuery_9_Bean> lstDati = new ArrayList<>();
        javax.persistence.Query query = null;
        try {
            String queryStr = "SELECT DISTINCT verSerieCor.idVerSerie, tiUd.nmTipoUnitaDoc, tiUd.dsTipoUnitaDoc "
                    + "FROM DecTipoSerieUd tiSerieUd " + "JOIN tiSerieUd.decTipoUnitaDoc tiUd "
                    + "JOIN tiSerieUd.decTipoSerie tiSerie " + "JOIN tiSerie.serSeries serie "
                    + "JOIN serie.serVerSeries verSerieCor "
                    + "WHERE verSerieCor.idVerSerie = :idVerSerie "
                    + "ORDER BY tiUd.nmTipoUnitaDoc ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idVerSerie", longFromBigDecimal(idVerSerie));
            List<Object[]> lstDatiObj = query.getResultList();
            for (Object[] dato : lstDatiObj) {
                VdCQuery_9_Bean vdc = new VdCQuery_9_Bean();
                vdc.setIdVerSerie((Long) dato[0]);
                vdc.setNmTipoUnitaDoc((String) dato[1]);
                vdc.setDsTipoUnitaDoc((String) dato[2]);
                lstDati.add(vdc);
            }
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliIndiceAipSerieUd.getVdCQuery_9_Data " + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei dati riguardanti il VdC durante i controlli per la creazione dell'indice AIP versione serie UD ",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getVdCQuery10Data(BigDecimal idVerSerie) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VdCQuery_10_Bean> lstDati = new ArrayList<>();
        javax.persistence.Query query = null;
        try {
            /*
             * Tu che stai leggendo... sì proprio tu che rompi sempre il... sul codice altrui, e
             * sappiamo bene a chi mi sto riferendo! Ti do un indizio: abiti a S.L. di S., sposato
             * con V.P. e hai un debole per... ecco proprio quella! Allora, ora che ci siamo capiti
             * su chi sei a scanso di equivoci... quando leggerai la seguente query non la troverai
             * di tuo gradimento in quanto vengono fatti i join sulle liste e non sulla singola
             * entity, e allora ti invito pure a riscriverla in diversa maniera senza join sulle
             * liste... Se ci riesci vieni pure a rinfacciarmelo, tanto sai dove trovarmi... fadc...
             */
            String queryStr = "SELECT DISTINCT verSerieCor.idVerSerie, tiDoc.nmTipoDoc, tiDoc.dsTipoDoc "
                    + "FROM SerVerSerie verSerieCor " + "JOIN verSerieCor.serSerie serie "
                    + "JOIN serie.decTipoSerie tiSerie " + "JOIN tiSerie.decTipoSerieUds tiSerieUd "
                    + "JOIN tiSerieUd.decTipoUnitaDoc tiUd "
                    + "JOIN tiUd.decTipoStrutUnitaDocs tiStrutUd "
                    + "JOIN tiStrutUd.decTipoDocAmmessos tiDocAmmesso "
                    + "JOIN tiDocAmmesso.decTipoDoc tiDoc "
                    + "WHERE verSerieCor.idVerSerie = :idVerSerie " + "ORDER BY tiDoc.nmTipoDoc ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idVerSerie", longFromBigDecimal(idVerSerie));
            List<Object[]> lstDatiObj = query.getResultList();
            for (Object[] dato : lstDatiObj) {
                VdCQuery_10_Bean vdc = new VdCQuery_10_Bean();
                vdc.setIdVerSerie((Long) dato[0]);
                vdc.setNmTipoDoc((String) dato[1]);
                vdc.setDsTipoDoc((String) dato[2]);
                lstDati.add(vdc);
            }
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliIndiceAipSerieUd.getVdCQuery_10_Data " + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei dati riguardanti il VdC durante i controlli per la creazione dell'indice AIP versione serie UD ",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getVdCQuery11Data(BigDecimal idVerSerie) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VdCQuery_11_Bean> lstDati = new ArrayList<>();
        javax.persistence.Query query = null;
        try {
            /* Se hai da rompere per la query sottostante, leggiti il commento per la query 10 */
            String queryStr = "SELECT verSerieCor.idVerSerie, tiNota.cdTipoNotaSerie, nota.pgNotaVerSerie, nota.dsNotaVerSerie, "
                    + "nota.dtNotaVerSerie, usr.nmUserid " + "FROM SerVerSerie verSerieCor "
                    + "LEFT JOIN verSerieCor.serNotaVerSeries nota "
                    + "LEFT JOIN nota.decTipoNotaSerie tiNota " + "LEFT JOIN nota.iamUser usr "
                    + "WHERE tiNota.cdTipoNotaSerie IN ('NOTE_CONSERVATORE', 'NOTE_PRODUTTORE') "
                    + "AND verSerieCor.idVerSerie = :idVerSerie "
                    + "ORDER BY tiNota.cdTipoNotaSerie, nota.pgNotaVerSerie ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idVerSerie", longFromBigDecimal(idVerSerie));
            List<Object[]> lstDatiObj = query.getResultList();
            for (Object[] dato : lstDatiObj) {
                VdCQuery_11_Bean vdc = new VdCQuery_11_Bean();
                vdc.setIdVerSerie((Long) dato[0]);
                vdc.setCdTipoNotaSerie((String) dato[1]);
                vdc.setPgNotaVerSerie((BigDecimal) dato[2]);
                vdc.setDsNota((String) dato[3]);
                vdc.setDtNota((Date) dato[4]);
                vdc.setDsAutore((String) dato[5]);
                lstDati.add(vdc);
            }
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliIndiceAipSerieUd.getVdCQuery_11_Data " + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei dati riguardanti il VdC durante i controlli per la creazione dell'indice AIP versione serie UD",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getFileQuery1Data(long idVerSerie) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<FileQuery_1_Bean> lstDati = new ArrayList<>();
        javax.persistence.Query query = null;
        try {
            String queryStr = "SELECT volSerie.idVerSerie, volSerie.pgVolVerSerie, volSerie.niUnitaDocVol, volSerie.cdFirstUnitaDocVol, "
                    + "volSerie.dtFirstUnitaDocVol, volSerie.cdLastUnitaDocVol, volSerie.dtLastUnitaDocVol, urnIxVolVerSerie.dsUrn, ixVolSerie.dsHashIxVol "
                    + "FROM SerVLisVolSerieUd volSerie, SerUrnIxVolVerSerie urnIxVolVerSerie, SerIxVolVerSerie ixVolSerie "
                    + "WHERE urnIxVolVerSerie.serIxVolVerSerie.idIxVolVerSerie = ixVolSerie.idIxVolVerSerie "
                    + "AND ixVolSerie.serVolVerSerie.idVolVerSerie = volSerie.idVolVerSerie "
                    + "AND urnIxVolVerSerie.tiUrn = :tiUrn "
                    + "AND volSerie.idVerSerie = :idVerSerie " + "ORDER BY volSerie.pgVolVerSerie ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idVerSerie", BigDecimal.valueOf(idVerSerie));
            query.setParameter("tiUrn", TiUrnIxVolVerSerie.NORMALIZZATO);
            List<Object[]> lstDatiObj = query.getResultList();
            for (Object[] dato : lstDatiObj) {
                FileQuery_1_Bean vdc = new FileQuery_1_Bean();
                vdc.setIdVerSerie(((BigDecimal) dato[0]).longValue());
                vdc.setPgVolVerSerie((BigDecimal) dato[1]);
                vdc.setNiUnitaDocVol((BigDecimal) dato[2]);
                vdc.setCdFirstUnitaDocVol((String) dato[3]);
                vdc.setDtFirstUnitaDocVol((Date) dato[4]);
                vdc.setCdLastUnitaDocVol((String) dato[5]);
                vdc.setDtLastUnitaDocVol((Date) dato[6]);
                vdc.setDsUrnIxVol((String) dato[7]);
                vdc.setDsHashIxVol((String) dato[8]);
                lstDati.add(vdc);
            }
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliIndiceAipSerieUd.getFileQuery_1_Data " + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei dati riguardanti il File durante i controlli per la creazione dell'indice AIP versione serie UD ",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getPrecedentiVersioniIndiceAip(Long idUnitaDoc) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroIndiceAipUd> lstIndice = null;
        List<AroVerIndiceAipUd> lstVerIndice;
        javax.persistence.Query query = null;

        try {
            String queryStr = "SELECT u FROM AroIndiceAipUd u "
                    + "WHERE u.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                    + "AND u.tiFormatoIndiceAip = 'UNISYNCRO' ";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            lstIndice = query.getResultList();
            if (!lstIndice.isEmpty()) {
                lstVerIndice = lstIndice.get(0).getAroVerIndiceAipUds();
                rispostaControlli.setrObject(lstVerIndice);
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
