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

package it.eng.parer.web.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.paginator.helper.LazyListHelper;
import it.eng.parer.crypto.model.CryptoEnums.TipoRifTemporale;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.FirCertifCa;
import it.eng.parer.entity.FirCrl;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.form.ComponentiForm.RicComponentiFiltri;
import it.eng.parer.slite.gen.form.ElenchiVersamentoForm;
import it.eng.parer.slite.gen.form.VolumiForm.ComponentiFiltri;
import it.eng.parer.slite.gen.tablebean.AroCompDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.OrgSubStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgSubStrutTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisCertifCaFirmaCompTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisCertifCaMarcaCompTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisControfirmaFirmaTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisFirmaCompRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisFirmaCompTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisMarcaCompTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicCompTableBean;
import it.eng.parer.slite.gen.viewbean.AroVVisCompRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisCompVolRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisFirmaCompRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisMarcaCompRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVListaCompElvRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVListaCompElvTableBean;
import it.eng.parer.slite.gen.viewbean.VolVListaCompVolRowBean;
import it.eng.parer.slite.gen.viewbean.VolVListaCompVolTableBean;
import it.eng.parer.viewEntity.AroVLisCertifCaFirmaComp;
import it.eng.parer.viewEntity.AroVLisCertifCaMarcaComp;
import it.eng.parer.viewEntity.AroVLisControfirmaFirma;
import it.eng.parer.viewEntity.AroVLisFirmaComp;
import it.eng.parer.viewEntity.AroVLisMarcaComp;
import it.eng.parer.viewEntity.AroVRicComp;
import it.eng.parer.viewEntity.AroVVisComp;
import it.eng.parer.viewEntity.AroVVisCompVol;
import it.eng.parer.viewEntity.AroVVisFirmaComp;
import it.eng.parer.viewEntity.AroVVisMarcaComp;
import it.eng.parer.viewEntity.VolVListaCompVol;
import it.eng.parer.web.util.BlobObject;
import it.eng.parer.web.util.StringPadding;
import it.eng.parer.web.util.Transform;
import it.eng.spagoCore.error.EMFError;

/**
 * Session Bean implementation class ComponentiHelper Contiene i metodi (implementati di ComponentiHelperLocal), per la
 * gestione della persistenza su DB per le operazioni CRUD su oggetti di ComponentiTableBean e ComponentiRowBean
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Stateless
@LocalBean
public class ComponentiHelper extends GenericHelper {
    @EJB(mappedName = "java:app/paginator/LazyListHelper")
    private LazyListHelper lazyListHelper;

    private static final Logger log = LoggerFactory.getLogger(ComponentiHelper.class.getName());

    public AroCompDocRowBean getAroCompDocRowBean(BigDecimal idComp, BigDecimal idStrut) {
        String queryStr = "SELECT u FROM AroCompDoc u where u.idCompDoc = :idcomp and u.idStrut = :idstrut";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idcomp", longFromBigDecimal(idComp));
        query.setParameter("idstrut", idStrut);

        // ESEGUO LA QUERY E INSERISCO I RISULTATI IN UNA LISTA DI "Componenti"
        List<AroCompDoc> componentiList = query.getResultList();

        AroCompDocRowBean componenteRb = new AroCompDocRowBean();

        try {
            if (componentiList != null && componentiList.size() == 1) {
                componenteRb = (AroCompDocRowBean) Transform.entity2RowBean(componentiList.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return componenteRb;
    }

    // Metodo che restituisce un viewbean con i record dei componenti trovati in base
    // ai filtri di ricerca passati in ingresso
    public AroVRicCompTableBean getAroVRicCompViewBean(BigDecimal idStrut, RicComponentiFiltri filtri,
            Date[] dateAcquisizioneValidate, DecTipoUnitaDocTableBean tmpTableBeanTipoUD,
            DecTipoDocTableBean tmpTableBeanTipoDoc, int maxResults) throws EMFError {

        // Inserimento nella query dei tipi unità documentaria abilitati
        Set<BigDecimal> idTipoUnitaDocSet = new HashSet<>();
        for (DecTipoUnitaDocRowBean row : tmpTableBeanTipoUD) {
            idTipoUnitaDocSet.add(row.getIdTipoUnitaDoc());
        }

        Set<BigDecimal> idTipoDocSet = new HashSet<>();
        for (DecTipoDocRowBean row : tmpTableBeanTipoDoc) {
            idTipoDocSet.add(row.getIdTipoDoc());
        }

        return getAroVRicCompViewBeanSimpleTypeParameters(idStrut, dateAcquisizioneValidate, maxResults,
                filtri.getCd_registro_key_unita_doc().parse(), filtri.getAa_key_unita_doc().parse(),
                filtri.getCd_key_unita_doc().parse(), filtri.getAa_key_unita_doc_da().parse(),
                filtri.getAa_key_unita_doc_a().parse(), filtri.getCd_key_unita_doc_da().parse(),
                filtri.getCd_key_unita_doc_a().parse(), filtri.getDs_id_comp_vers().parse(),
                filtri.getNm_tipo_strut_doc().parse(), filtri.getNm_tipo_comp_doc().parse(),
                filtri.getNm_formato_file_vers().parse(), filtri.getNi_size_file_da().parse(),
                filtri.getNi_size_file_a().parse(), filtri.getFl_comp_firmato().parse(),
                filtri.getTi_esito_contr_conforme().parse(), filtri.getTi_esito_verif_firme().parse(),
                filtri.getDt_scad_firma_comp_da().parse(), filtri.getDt_scad_firma_comp_a().parse(),
                filtri.getFl_rif_temp_vers().parse(), filtri.getDs_rif_temp_vers().parse(),
                filtri.getDs_nome_comp_vers().parse(), filtri.getFl_hash_vers().parse(),
                filtri.getDs_hash_file_vers().parse(), filtri.getNm_mimetype_file().parse(),
                filtri.getDl_urn_comp_vers().parse(), filtri.getDs_formato_rappr_calc().parse(),
                filtri.getDs_formato_rappr_esteso_calc().parse(), filtri.getFl_forza_accettazione().parse(),
                filtri.getFl_forza_conservazione().parse(), filtri.getTi_esito_contr_formato_file().parse(),
                filtri.getDs_hash_file_calc().parse(), filtri.getDs_algo_hash_file_calc().parse(),
                filtri.getCd_encoding_hash_file_calc().parse(), filtri.getDs_urn_comp_calc().parse(),
                filtri.getTi_supporto_comp().parse(), filtri.getNm_tipo_rappr_comp().parse(),
                filtri.getNm_sub_strut().parse(), filtri.getCd_registro_key_unita_doc().getDecodeMap().keySet(),
                idTipoUnitaDocSet, idTipoDocSet);
    }

    // Metodo che restituisce un viewbean con i record dei componenti trovati in base
    // ai filtri di ricerca passati in ingresso
    public AroVRicCompTableBean getAroVRicCompViewBeanSimpleTypeParameters(BigDecimal idStrut,
            Date[] dateAcquisizioneValidate, int maxResults, final String registro, final BigDecimal anno,
            final String codice, final BigDecimal annoRangeDa, final BigDecimal annoRangeA, String codiceRangeDa,
            String codiceRangeA, final String idComp, final BigDecimal tipoStrutDoc, final BigDecimal tipoCompDoc,
            final String formato, BigDecimal fileSizeDa, final BigDecimal fileSizeA, final String presenza,
            final String conformita, final String esitoFirme, final Timestamp dtScadFirmaCompDa,
            final Timestamp dtScadFirmaCompA, final String flRifTempVers, final String dsRifTempVers,
            final String dsNomeCompVers, final String flHashVers, final String dsHashFileVers,
            final String nmMimetypeFile, final String dlUrnCompVers, final String dsFormatoRapprCalc,
            final String dsFormatoRapprEstesoCalc, final String forzaAcc, final String forzaConserva,
            final String tiEsitoContrFormatoFile, final String dsHashFileCalc, final String dsAlgoHashFileCalc,
            final String cdEncodingHashFileCalc, final String dsUrnCompCalc, final String tiSupportoComp,
            final String nmTipoRapprComp, final List<BigDecimal> subStruts, final Set<Object> keySet,
            Set<BigDecimal> idTipoUnitaDocSet, Set<BigDecimal> idTipoDocSet) {
        String whereWord = "and ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT new it.eng.parer.viewEntity.AroVRicComp(u.id.idCompDoc,u.id.idFirmaComp, u.dsUrnCompCalc, u.dsNomeCompVers, u.tiSupportoComp, u.nmTipoCompDoc, u.nmFormatoFileDocVers, u.dtCreazioneDoc, u.niSizeFileCalc, u.flCompFirmato, u.tiEsitoVerifFirmeVers, u.tiStatoElencoVers, u.tiStatoConservazione,u.dsOrdComp) FROM AroVRicComp u WHERE u.idStrutUnitaDoc = :idstrut ");
        // Inserimento nella query dei tipi unità documentaria abilitati
        if (idTipoUnitaDocSet.isEmpty()) {
            idTipoUnitaDocSet.add(new BigDecimal("0"));
        }
        queryStr.append(whereWord).append("u.idTipoUnitaDoc IN (:idtipounitadocin) ");
        // Inserimento nella query dei tipi documento abilitati
        if (idTipoDocSet.isEmpty()) {
            idTipoDocSet.add(new BigDecimal("0"));
        }
        queryStr.append(whereWord).append("u.idTipoDoc IN (:idtipodocin) ");
        // Inserimento nella query del filtro CHIAVE DOCUMENTO
        if (idComp != null) {
            queryStr.append(whereWord).append("UPPER(u.dsIdCompVers) LIKE :idcomp ");
            whereWord = "and ";
        }
        if (StringUtils.isNotBlank(registro)) {
            queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc = :registroin ");
            whereWord = "AND ";
        } else {
            queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc IN (:registroin) ");
            whereWord = "AND ";
        }
        if (anno != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :annoin ");
            whereWord = "and ";
        }
        if (codice != null) {
            queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :codicein ");
            whereWord = "and ";
        }
        if (annoRangeDa != null && annoRangeA != null) {
            queryStr.append(whereWord).append("(u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }
        if (codiceRangeDa != null && codiceRangeA != null) {
            codiceRangeDa = StringPadding.padString(codiceRangeDa, "0", 12, StringPadding.PADDING_LEFT);
            codiceRangeA = StringPadding.padString(codiceRangeA, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord).append("LPAD( u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }
        // Inserimento nella query del filtro DATA CREAZIONE DA - A
        Date dataDa = (dateAcquisizioneValidate != null ? dateAcquisizioneValidate[0] : null);
        Date dataA = (dateAcquisizioneValidate != null ? dateAcquisizioneValidate[1] : null);
        if ((dataDa != null) && (dataA != null)) {
            queryStr.append(whereWord).append("(u.dtCreazioneDoc between :datada AND :dataa) ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro Tipo Struttura Documento
        if (tipoStrutDoc != null) {
            queryStr.append(whereWord).append("u.idTipoStrutDoc = :tipostrutdocin ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro Tipo Componente Documento
        if (tipoCompDoc != null) {
            queryStr.append(whereWord).append("u.idTipoCompDoc = :tipocompdocin ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro Formato
        if (formato != null) {
            queryStr.append(whereWord).append("u.nmFormatoFileDocVers = :formatoin ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro file size
        if (fileSizeDa == null && fileSizeA != null) {
            fileSizeDa = BigDecimal.ZERO;
        }
        if (fileSizeDa != null && fileSizeA != null) {
            queryStr.append(whereWord).append("u.niSizeFileCalc between :filesizedain AND :filesizeain ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro PRESENZA FIRME
        if (presenza != null) {
            queryStr.append(whereWord).append("u.flCompFirmato = :presenzain ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro Conformita FIRME
        if (conformita != null) {
            queryStr.append(whereWord).append("u.tiEsitoContrConforme = :conformitain ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro Esito Firme
        if (esitoFirme != null) {
            queryStr.append(whereWord).append("u.tiEsitoVerifFirmeVers = :esitofirmein ");
            whereWord = "and ";
        }

        Date dataValDa = null;
        Date dataValA = null;
        // Inserimento nella query del filtro DATA SCADENZA DA - A
        if (dtScadFirmaCompDa != null) {
            dataValDa = new Date(dtScadFirmaCompDa.getTime());
            if (dtScadFirmaCompA != null) {
                dataValA = new Date(dtScadFirmaCompA.getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dataValA);
                calendar.add(Calendar.DATE, 1);
                dataValA = calendar.getTime();
            } else {
                dataValA = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dataValA);
                calendar.add(Calendar.DATE, 1);
                dataValA = calendar.getTime();
            }
        }
        if ((dataValDa != null) && (dataValA != null)) {
            queryStr.append(whereWord).append("(u.dtScadCertifFirmatario between :datavalda AND :datavala) ");
            whereWord = "and ";
        }
        if (StringUtils.isNotBlank(flRifTempVers)) {
            queryStr.append(whereWord).append("u.flRifTempVers = :flRifTempVers ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dsRifTempVers)) {
            queryStr.append(whereWord).append("UPPER(u.dsRifTempVers) LIKE :dsRifTempVers ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dsNomeCompVers)) {
            queryStr.append(whereWord).append("UPPER(u.dsNomeCompVers) LIKE :nomeCompVersIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(flHashVers)) {
            queryStr.append(whereWord).append("u.flHashVers = :flHashVers ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dsHashFileVers)) {
            queryStr.append(whereWord).append("u.dsHashFileVers = :hashFileVersIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(nmMimetypeFile)) {
            queryStr.append(whereWord).append("UPPER(u.nmMimetypeFile) LIKE :mimetypeIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dlUrnCompVers)) {
            queryStr.append(whereWord).append("UPPER(u.dlUrnCompVers) LIKE :urnCompVersIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dsFormatoRapprCalc)) {
            queryStr.append(whereWord).append("u.dsFormatoRapprCalc = :formatoRapprCalcIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dsFormatoRapprEstesoCalc)) {
            queryStr.append(whereWord).append("u.dsFormatoRapprEstesoCalc = :formatoRapprEstesoCalcIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(forzaAcc)) {
            queryStr.append(whereWord).append("u.flForzaAccettazione = :forzaaccin ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(forzaConserva)) {
            queryStr.append(whereWord).append("u.flForzaConservazione = :forzaconservain ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(tiEsitoContrFormatoFile)) {
            queryStr.append(whereWord).append("u.tiEsitoContrFormatoFile = :esitoContrFormatoFileIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dsHashFileCalc)) {
            queryStr.append(whereWord).append("u.dsHashFileCalc = :hashFileCalcIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dsAlgoHashFileCalc)) {
            queryStr.append(whereWord).append("u.dsAlgoHashFileCalc = :algoHashFileCalcIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(cdEncodingHashFileCalc)) {
            queryStr.append(whereWord).append("u.cdEncodingHashFileCalc = :encodingHashFileCalcIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dsUrnCompCalc)) {
            queryStr.append(whereWord).append("UPPER(u.dsUrnCompCalc) LIKE :urnCompCalcIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(tiSupportoComp)) {
            queryStr.append(whereWord).append("u.tiSupportoComp = :supportoCompIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(nmTipoRapprComp)) {
            queryStr.append(whereWord).append("u.nmTipoRapprComp = :tipoRapprCompIn ");
            whereWord = " AND ";
        }

        if (!subStruts.isEmpty()) {
            queryStr.append(whereWord).append(" u.idSubStrut IN (:subStruts) ");
        }
        // ordina per data creazione decrescente
        queryStr.append("ORDER BY u.dsOrdComp");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idstrut", idStrut);
        query.setParameter("idtipounitadocin", idTipoUnitaDocSet);
        query.setParameter("idtipodocin", idTipoDocSet);
        if (idComp != null) {
            query.setParameter("idcomp", "%" + idComp.toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(registro)) {
            query.setParameter("registroin", registro);
        } else {
            query.setParameter("registroin", keySet);
        }
        if (anno != null) {
            query.setParameter("annoin", anno);
        }
        if (codice != null) {
            query.setParameter("codicein", codice);
        }
        if (annoRangeDa != null && annoRangeA != null) {
            query.setParameter("annoin_da", annoRangeDa);
            query.setParameter("annoin_a", annoRangeA);
        }
        if (codiceRangeDa != null && codiceRangeA != null) {
            query.setParameter("codicein_da", codiceRangeDa);
            query.setParameter("codicein_a", codiceRangeA);
        }
        if (dataDa != null && dataA != null) {
            query.setParameter("datada", dataDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataA, TemporalType.TIMESTAMP);
        }
        if (tipoStrutDoc != null) {
            query.setParameter("tipostrutdocin", tipoStrutDoc);
        }
        if (tipoCompDoc != null) {
            query.setParameter("tipocompdocin", tipoCompDoc);
        }
        if (formato != null) {
            query.setParameter("formatoin", formato);
        }
        if (fileSizeDa != null && fileSizeA != null) {
            query.setParameter("filesizedain", fileSizeDa);
            query.setParameter("filesizeain", fileSizeA);
        }
        if (presenza != null) {
            query.setParameter("presenzain", presenza);
        }
        if (conformita != null) {
            query.setParameter("conformitain", conformita);
        }
        if (esitoFirme != null) {
            query.setParameter("esitofirmein", esitoFirme);
        }

        if (dataValDa != null && dataValA != null) {
            query.setParameter("datavalda", dataValDa, TemporalType.DATE);
            query.setParameter("datavala", dataValA, TemporalType.DATE);
        }
        if (StringUtils.isNotBlank(flRifTempVers)) {
            query.setParameter("flRifTempVers", flRifTempVers);
        }
        if (StringUtils.isNotBlank(dsRifTempVers)) {
            query.setParameter("dsRifTempVers", "%" + dsRifTempVers.toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(dsNomeCompVers)) {
            query.setParameter("nomeCompVersIn", "%" + dsNomeCompVers.toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(flHashVers)) {
            query.setParameter("flHashVers", flHashVers);
        }
        if (StringUtils.isNotBlank(dsHashFileVers)) {
            query.setParameter("hashFileVersIn", dsHashFileVers);
        }
        if (StringUtils.isNotBlank(nmMimetypeFile)) {
            query.setParameter("mimetypeIn", "%" + nmMimetypeFile.toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(dlUrnCompVers)) {
            query.setParameter("urnCompVersIn", "%" + dlUrnCompVers.toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(dsFormatoRapprCalc)) {
            query.setParameter("formatoRapprCalcIn", dsFormatoRapprCalc);
        }
        if (StringUtils.isNotBlank(dsFormatoRapprEstesoCalc)) {
            query.setParameter("formatoRapprEstesoCalcIn", dsFormatoRapprEstesoCalc);
        }
        if (StringUtils.isNotBlank(forzaAcc)) {
            query.setParameter("forzaaccin", forzaAcc);
        }
        if (StringUtils.isNotBlank(forzaConserva)) {
            query.setParameter("forzaconservain", forzaConserva);
        }
        if (StringUtils.isNotBlank(tiEsitoContrFormatoFile)) {
            query.setParameter("esitoContrFormatoFileIn", tiEsitoContrFormatoFile);
        }
        if (StringUtils.isNotBlank(dsHashFileCalc)) {
            query.setParameter("hashFileCalcIn", dsHashFileCalc);
        }
        if (StringUtils.isNotBlank(dsAlgoHashFileCalc)) {
            query.setParameter("algoHashFileCalcIn", dsAlgoHashFileCalc);
        }
        if (StringUtils.isNotBlank(cdEncodingHashFileCalc)) {
            query.setParameter("encodingHashFileCalcIn", cdEncodingHashFileCalc);
        }
        if (StringUtils.isNotBlank(dsUrnCompCalc)) {
            query.setParameter("urnCompCalcIn", "%" + dsUrnCompCalc.toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(tiSupportoComp)) {
            query.setParameter("supportoCompIn", tiSupportoComp);
        }
        if (StringUtils.isNotBlank(nmTipoRapprComp)) {
            query.setParameter("tipoRapprCompIn", nmTipoRapprComp);
        }

        if (!subStruts.isEmpty()) {
            query.setParameter("subStruts", subStruts);
        }
        query.setMaxResults(maxResults);
        return lazyListHelper.getTableBean(query, list -> getAroVRicCompTableBeanFromResultList(list),
                "u.id.idCompDoc");

    }

    private AroVRicCompTableBean getAroVRicCompTableBeanFromResultList(List<AroVRicComp> listaComponenti) {
        AroVRicCompTableBean componentiTableBean = new AroVRicCompTableBean();
        try {
            if (listaComponenti != null && !listaComponenti.isEmpty()) {
                componentiTableBean = (AroVRicCompTableBean) Transform.entities2TableBean(listaComponenti);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return componentiTableBean;
    }

    // Metodo che restituisce un viewbean con i record dei componenti trovati in base
    // ai filtri di ricerca passati in ingresso e all'id del volume
    public it.eng.parer.slite.gen.viewbean.VolVListaCompVolTableBean getVolVListaCompVolViewBean(BigDecimal idVolume,
            ComponentiFiltri filtri, Date[] dateValidate, DecTipoUnitaDocTableBean tmpTableBeanTipoUD,
            DecTipoDocTableBean tmpTableBeanTipoDoc, OrgSubStrutTableBean tmpSubStrutsTableBean, int maxResults)
            throws it.eng.spagoCore.error.EMFError {

        Set<BigDecimal> idTipoUnitaDocSet = new HashSet<>();
        for (DecTipoUnitaDocRowBean row : tmpTableBeanTipoUD) {
            idTipoUnitaDocSet.add(row.getIdTipoUnitaDoc());
        }

        Set<BigDecimal> idTipoDocSet = new HashSet<>();
        for (DecTipoDocRowBean row : tmpTableBeanTipoDoc) {
            idTipoDocSet.add(row.getIdTipoDoc());
        }
        Set<BigDecimal> idSubStrutSet = new HashSet<>();
        for (OrgSubStrutRowBean row : tmpSubStrutsTableBean) {
            idSubStrutSet.add(row.getIdSubStrut());
        }
        return getVolVListaCompVolViewBeanSimpleTypeParameters(idVolume, dateValidate, maxResults,
                filtri.getCd_registro_key_unita_doc().parse(), filtri.getAa_key_unita_doc().parse(),
                filtri.getCd_key_unita_doc().parse(), filtri.getAa_key_unita_doc_da().parse(),
                filtri.getAa_key_unita_doc_a().parse(), filtri.getCd_key_unita_doc_da().parse(),
                filtri.getCd_key_unita_doc_a().parse(), filtri.getNm_tipo_strut_doc().parse(),
                filtri.getNm_tipo_comp_doc().parse(), filtri.getNm_formato_file_vers().parse(),
                filtri.getNi_size_file_da().parse(), filtri.getNi_size_file_a().parse(),
                filtri.getFl_comp_firmato().parse(), filtri.getTi_esito_contr_conforme().parse(),
                filtri.getTi_esito_verif_firme_vers().parse(), filtri.getTi_esito_verif_firme_chius().parse(),
                filtri.getDt_scad_firma_comp_da().parse(), filtri.getDt_scad_firma_comp_a().parse(),
                filtri.getDs_nome_comp_vers().parse(), filtri.getDs_hash_file_vers().parse(),
                filtri.getNm_mimetype_file().parse(), filtri.getDl_urn_comp_vers().parse(),
                filtri.getDs_formato_rappr_calc().parse(), filtri.getDs_formato_rappr_esteso_calc().parse(),
                filtri.getFl_forza_accettazione().parse(), filtri.getFl_forza_conservazione().parse(),
                filtri.getTi_esito_contr_formato_file().parse(), filtri.getDs_hash_file_calc().parse(),
                filtri.getDs_algo_hash_file_calc().parse(), filtri.getCd_encoding_hash_file_calc().parse(),
                filtri.getDs_urn_comp_calc().parse(), idTipoUnitaDocSet, idTipoDocSet, idSubStrutSet);
    }

    // Metodo che restituisce un viewbean con i record dei componenti trovati in base
    // ai filtri di ricerca passati in ingresso e all'id del volume
    VolVListaCompVolTableBean getVolVListaCompVolViewBeanSimpleTypeParameters(BigDecimal idVolume, Date[] dateValidate,
            int maxResults, final String registro, final BigDecimal anno, final String codice,
            final BigDecimal annoRangeDa, final BigDecimal annoRangeA, String codiceRangeDa, String codiceRangeA,
            final BigDecimal tipoStrutDoc, final BigDecimal tipoCompDoc, final String formato, BigDecimal fileSizeDa,
            final BigDecimal fileSizeA, final String presenza, final String conformita, final String esitoFirme,
            final String esitoFirmeChius, final Timestamp dtScadFirmaCompDa, final Timestamp dtScadFirmaCompA,
            final String dsNomeCompVers, final String dsHashFileVers, final String nmMimetypeFile,
            final String dlUrnCompVers, final String dsFormatoRapprCalc, final String dsFormatoRapprEstesoCalc,
            final String flForzaAccettazione, final String flForzaConservazione, final String tiEsitoContrFormatoFile,
            final String dsHashFileCalc, final String dsAlgoHashFileCalc, final String cdEncodingHashFileCalc,
            final String dsUrnCompCalc, Set<BigDecimal> idTipoUnitaDocSet, Set<BigDecimal> idTipoDocSet,
            Set<BigDecimal> idSubStrutSet) {
        String whereWord = "and ";

        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT new it.eng.parer.viewEntity.VolVListaCompVol(u.idVolumeConserv, u.idCompDoc, "
                        + "u.dsUrnCompCalc, u.dsUrnCompCalcShort, u.dsNomeCompVers, u.dsOrdComp, u.tiSupportoComp, "
                        + "u.nmTipoStrutDoc, u.nmTipoCompDoc, u.nmFormatoFileDocVers, u.dtCreazioneDoc, u.niSizeFileCalc, "
                        + "u.flCompFirmato, u.tiEsitoVerifFirmeVers, u.tiEsitoVerifFirmeChius, u.idUnitaDoc, u.idDoc, "
                        + "u.cdRegistroKeyUnitaDoc, u.idTipoUnitaDoc) "
                        + "FROM VolVListaCompVol u WHERE u.idVolumeConserv = :idvol ");
        // Inserimento nella query dei tipi unità documentaria abilitati
        if (idTipoUnitaDocSet.isEmpty()) {
            idTipoUnitaDocSet.add(new BigDecimal("0"));
        }
        queryStr.append(whereWord).append("u.idTipoUnitaDoc IN (:idtipounitadocin) ");
        // Inserimento nella query dei tipi documento abilitati
        if (idTipoDocSet.isEmpty()) {
            idTipoDocSet.add(new BigDecimal("0"));
        }
        queryStr.append(whereWord).append("u.idTipoDoc IN (:idtipodocin) ");
        // Inserimento nella query delle sottostrutture abilitate
        if (idSubStrutSet.isEmpty()) {
            idSubStrutSet.add(new BigDecimal("0"));
        }
        queryStr.append(whereWord).append("u.idSubStrut IN (:idsubstrutin) ");
        // Inserimento nella query del filtro CHIAVE DOCUMENTO
        if (registro != null) {
            queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc = :registroin ");
            whereWord = "and ";
        }
        if (anno != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :annoin ");
            whereWord = "and ";
        }
        if (codice != null) {
            queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :codicein ");
            whereWord = "and ";
        }
        if (annoRangeDa != null && annoRangeA != null) {
            queryStr.append(whereWord).append("(u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }
        if (codiceRangeDa != null && codiceRangeA != null) {
            codiceRangeDa = StringPadding.padString(codiceRangeDa, "0", 12, StringPadding.PADDING_LEFT);
            codiceRangeA = StringPadding.padString(codiceRangeA, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord).append("LPAD(u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }
        // Inserimento nella query del filtro DATA CREAZIONE DA - A
        Date dataDa = (dateValidate != null ? dateValidate[0] : null);
        Date dataA = (dateValidate != null ? dateValidate[1] : null);
        if ((dataDa != null) && (dataA != null)) {
            queryStr.append(whereWord).append("(u.dtCreazioneDoc between :datada AND :dataa) ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro Tipo Struttura Documento
        if (tipoStrutDoc != null) {
            queryStr.append(whereWord).append("u.idTipoStrutDoc = :tipostrutdocin ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro Tipo Componente Documento
        if (tipoCompDoc != null) {
            queryStr.append(whereWord).append("u.idTipoCompDoc = :tipocompdocin ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro Formato
        if (formato != null) {
            queryStr.append(whereWord).append("u.nmFormatoFileDocVers = :formatoin ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro file size
        if (fileSizeDa == null && fileSizeA != null) {
            fileSizeDa = BigDecimal.ZERO;
        }
        if (fileSizeDa != null && fileSizeA != null) {
            queryStr.append(whereWord).append("u.niSizeFileCalc between :filesizedain AND :filesizeain ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro PRESENZA FIRME
        if (presenza != null) {
            queryStr.append(whereWord).append("u.flCompFirmato = :presenzain ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro Conformita FIRME
        if (conformita != null) {
            queryStr.append(whereWord).append("u.tiEsitoContrConforme = :conformitain ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro Esito Firme
        if (esitoFirme != null) {
            queryStr.append(whereWord).append("u.tiEsitoVerifFirmeVers = :esitofirmein ");
            whereWord = "and ";
        }
        // Inserimento nella query del filtro Esito Firme Chiusura Volume
        if (esitoFirmeChius != null) {
            queryStr.append(whereWord).append("u.tiEsitoVerifFirmeChius = :esitofirmechiusin ");
            whereWord = "and ";
        }
        Date dataValDa = null;
        Date dataValA = null;
        // Inserimento nella query del filtro DATA SCADENZA FIRMA DA - A
        if (dtScadFirmaCompDa != null) {
            dataValDa = new Date(dtScadFirmaCompDa.getTime());
            if (dtScadFirmaCompA != null) {
                dataValA = new Date(dtScadFirmaCompA.getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dataValA);
                calendar.add(Calendar.DATE, 1);
                dataValA = calendar.getTime();
            } else {
                dataValA = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dataValA);
                calendar.add(Calendar.DATE, 1);
                dataValA = calendar.getTime();
            }
        }
        if ((dataValDa != null) && (dataValA != null)) {
            queryStr.append(whereWord).append("(u.dtScadCertifFirmatario between :datavalda AND :datavala) ");
            whereWord = "and ";
        }
        if (StringUtils.isNotBlank(dsNomeCompVers)) {
            queryStr.append(whereWord).append("UPPER(u.dsNomeCompVers) LIKE :nomeCompVersIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dsHashFileVers)) {
            queryStr.append(whereWord).append("u.dsHashFileVers = :hashFileVersIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(nmMimetypeFile)) {
            queryStr.append(whereWord).append("UPPER(u.nmMimetypeFile) LIKE :mimetypeIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dlUrnCompVers)) {
            queryStr.append(whereWord).append("UPPER(u.dlUrnCompVers) LIKE :urnCompVersIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dsFormatoRapprCalc)) {
            queryStr.append(whereWord).append("u.dsFormatoRapprCalc = :formatoRapprCalcIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dsFormatoRapprEstesoCalc)) {
            queryStr.append(whereWord).append("u.dsFormatoRapprEstesoCalc = :formatoRapprEstesoCalcIn ");
            whereWord = " AND ";
        }
        String forzaAcc = flForzaAccettazione;
        if (StringUtils.isNotBlank(forzaAcc)) {
            queryStr.append(whereWord).append("u.flForzaAccettazione = :forzaaccin ");
            whereWord = " AND ";
        }
        String forzaConserva = flForzaConservazione;
        if (StringUtils.isNotBlank(forzaConserva)) {
            queryStr.append(whereWord).append("u.flForzaConservazione = :forzaconservain ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(tiEsitoContrFormatoFile)) {
            queryStr.append(whereWord).append("u.tiEsitoContrFormatoFile = :esitoContrFormatoFileIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dsHashFileCalc)) {
            queryStr.append(whereWord).append("u.dsHashFileCalc = :hashFileCalcIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dsAlgoHashFileCalc)) {
            queryStr.append(whereWord).append("u.dsAlgoHashFileCalc = :algoHashFileCalcIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(cdEncodingHashFileCalc)) {
            queryStr.append(whereWord).append("u.cdEncodingHashFileCalc = :encodingHashFileCalcIn ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(dsUrnCompCalc)) {
            queryStr.append(whereWord).append("UPPER(u.dsUrnCompCalc) LIKE :urnCompCalcIn ");
        }
        queryStr.append("ORDER BY u.dsOrdComp ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idvol", idVolume);
        query.setParameter("idtipounitadocin", idTipoUnitaDocSet);
        query.setParameter("idtipodocin", idTipoDocSet);
        query.setParameter("idsubstrutin", idSubStrutSet);
        if (registro != null) {
            query.setParameter("registroin", registro);
        }
        if (anno != null) {
            query.setParameter("annoin", anno);
        }
        if (codice != null) {
            query.setParameter("codicein", codice);
        }
        if (annoRangeDa != null && annoRangeA != null) {
            query.setParameter("annoin_da", annoRangeDa);
            query.setParameter("annoin_a", annoRangeA);
        }
        if (codiceRangeDa != null && codiceRangeA != null) {
            query.setParameter("codicein_da", codiceRangeDa);
            query.setParameter("codicein_a", codiceRangeA);
        }
        if (dataDa != null && dataA != null) {
            query.setParameter("datada", dataDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataA, TemporalType.TIMESTAMP);
        }
        if (tipoStrutDoc != null) {
            query.setParameter("tipostrutdocin", tipoStrutDoc);
        }
        if (tipoCompDoc != null) {
            query.setParameter("tipocompdocin", tipoCompDoc);
        }
        if (formato != null) {
            query.setParameter("formatoin", formato);
        }
        if (fileSizeDa != null && fileSizeA != null) {
            query.setParameter("filesizedain", fileSizeDa);
            query.setParameter("filesizeain", fileSizeA);
        }
        if (presenza != null) {
            query.setParameter("presenzain", presenza);
        }
        if (conformita != null) {
            query.setParameter("conformitain", conformita);
        }
        if (esitoFirme != null) {
            query.setParameter("esitofirmein", esitoFirme);
        }
        if (esitoFirmeChius != null) {
            query.setParameter("esitofirmechiusin", esitoFirmeChius);
        }
        if (dataValDa != null && dataValA != null) {
            query.setParameter("datavalda", dataValDa, TemporalType.DATE);
            query.setParameter("datavala", dataValA, TemporalType.DATE);
        }
        if (StringUtils.isNotBlank(dsNomeCompVers)) {
            query.setParameter("nomeCompVersIn", "'%" + dsNomeCompVers.toUpperCase() + "%'");
        }
        if (StringUtils.isNotBlank(dsHashFileVers)) {
            query.setParameter("hashFileVersIn", dsHashFileVers);
        }
        if (StringUtils.isNotBlank(nmMimetypeFile)) {
            query.setParameter("mimetypeIn", "'%" + nmMimetypeFile.toUpperCase() + "%'");
        }
        if (StringUtils.isNotBlank(dlUrnCompVers)) {
            query.setParameter("urnCompVersIn", "%" + dlUrnCompVers.toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(dsFormatoRapprCalc)) {
            query.setParameter("formatoRapprCalcIn", dsFormatoRapprCalc);
        }
        if (StringUtils.isNotBlank(dsFormatoRapprEstesoCalc)) {
            query.setParameter("formatoRapprEstesoCalcIn", dsFormatoRapprEstesoCalc);
        }
        if (StringUtils.isNotBlank(forzaAcc)) {
            query.setParameter("forzaaccin", forzaAcc);
        }
        if (StringUtils.isNotBlank(forzaConserva)) {
            query.setParameter("forzaconservain", forzaConserva);
        }
        if (StringUtils.isNotBlank(tiEsitoContrFormatoFile)) {
            query.setParameter("esitoContrFormatoFileIn", tiEsitoContrFormatoFile);
        }
        if (StringUtils.isNotBlank(dsHashFileCalc)) {
            query.setParameter("hashFileCalcIn", dsHashFileCalc);
        }
        if (StringUtils.isNotBlank(dsAlgoHashFileCalc)) {
            query.setParameter("algoHashFileCalcIn", dsAlgoHashFileCalc);
        }
        if (StringUtils.isNotBlank(cdEncodingHashFileCalc)) {
            query.setParameter("encodingHashFileCalcIn", cdEncodingHashFileCalc);
        }
        if (StringUtils.isNotBlank(dsUrnCompCalc)) {
            query.setParameter("urnCompCalcIn", "%" + dsUrnCompCalc.toUpperCase() + "%");
        }
        query.setMaxResults(maxResults);
        return lazyListHelper.getTableBean(query, list -> getVolVListaCompVolTableBeanFromResultList(list),
                "id.idAppartCompVolume");
    }

    private VolVListaCompVolTableBean getVolVListaCompVolTableBeanFromResultList(
            List<VolVListaCompVol> listaComponenti) {
        VolVListaCompVolTableBean componentiTableBean = new VolVListaCompVolTableBean();
        try {
            if (listaComponenti != null && !listaComponenti.isEmpty()) {
                componentiTableBean = (VolVListaCompVolTableBean) Transform.entities2TableBean(listaComponenti);
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        // setta il campo relativo alla checkbox select_comp non ceccato
        for (int i = 0; i < componentiTableBean.size(); i++) {
            VolVListaCompVolRowBean row = componentiTableBean.getRow(i);
            row.setString("select_comp", "0");
        }
        return componentiTableBean;
    }

    public List[] getBlobboByteList(BigDecimal idVolume) {
        // creo le lista che conterranno i blobbi dei file da restituire
        List<BlobObject> listaBlobbiCRL = new ArrayList<>();
        List<BlobObject> listaBlobbiCertif = new ArrayList<>();
        // ArrayList da restituire
        List[] blobbi = new ArrayList[2];

        List<BigDecimal> crlNameList = retrieveListaCRL(idVolume);
        List<BigDecimal> certifCaNameList = retrieveListaCertificatiCA(idVolume);

        // mi cucco i blobbi delle CRL
        if (crlNameList != null) {
            for (int i = 0; i < crlNameList.size(); i++) {
                BigDecimal idCRL = new BigDecimal(crlNameList.get(i).toString());
                FirCrl fircrl = getEntityManager().find(FirCrl.class, idCRL.longValue());
                if (fircrl != null && fircrl.getFirFilePerFirma() != null) {
                    // recupero finalmente il file della crl
                    BlobObject bocrl = new BlobObject(idCRL.longValue(),
                            fircrl.getFirFilePerFirma().getBlFilePerFirma());
                    listaBlobbiCRL.add(bocrl);
                }
            }
        }

        // mi cucco i blobbi dei certificati
        if (certifCaNameList != null) {
            for (int i = 0; i < certifCaNameList.size(); i++) {
                BigDecimal idCertif = new BigDecimal(certifCaNameList.get(i).toString());
                FirCertifCa fircertif = getEntityManager().find(FirCertifCa.class, idCertif.longValue());
                // corretto controllo mancante su !=null con MAC 35187
                if (fircertif != null && fircertif.getFirFilePerFirma() != null) {
                    // recupero finalmente il file del certificato
                    BlobObject bocertif = new BlobObject(idCertif.longValue(),
                            fircertif.getFirFilePerFirma().getBlFilePerFirma());
                    listaBlobbiCertif.add(bocertif);
                }
            }
        }
        blobbi[0] = listaBlobbiCRL;
        blobbi[1] = listaBlobbiCertif;
        return blobbi;
    }

    public List<BigDecimal> retrieveListaCRL(BigDecimal idVolume) {
        String SELECT_LISTA_CRL = "select distinct " + "crl.id_crl nomeFileCRL " + "from VOL_VOLUME_CONSERV vol "
                + "join VOL_APPART_UNITA_DOC_VOLUME app_ud " + "on (app_ud.id_volume_conserv = vol.id_volume_conserv) "
                + "join VOL_APPART_DOC_VOLUME app_doc "
                + "on (app_doc.id_appart_unita_doc_volume = app_ud.id_appart_unita_doc_volume) "
                + "join VOL_APPART_COMP_VOLUME app_comp "
                + "on (app_comp.id_appart_doc_volume = app_doc.id_appart_doc_volume) " + "join ARO_FIRMA_COMP firma "
                + "on (firma.id_comp_doc = app_comp.id_comp_doc) " + "join ARO_CONTR_FIRMA_COMP contr_CRL "
                + "on (contr_CRL.id_firma_comp = firma.id_firma_comp " + "and contr_CRL.ti_contr = 'CRL') "
                + "join FIR_CRL crl " + "on (crl.id_crl = contr_CRL.id_crl_usata) " + "join FIR_CERTIF_CA cert "
                + "on (cert.id_certif_ca = crl.id_certif_ca) " + "join FIR_ISSUER issuer "
                + "on (issuer.id_issuer = cert.id_issuer) " + "where vol.id_volume_conserv = ? " + "UNION "
                + "select distinct " + "crl.id_crl nomeFileCRL " + "from VOL_VOLUME_CONSERV vol "
                + "join VOL_APPART_UNITA_DOC_VOLUME app_ud " + "on (app_ud.id_volume_conserv = vol.id_volume_conserv) "
                + "join VOL_APPART_DOC_VOLUME app_doc "
                + "on (app_doc.id_appart_unita_doc_volume = app_ud.id_appart_unita_doc_volume) "
                + "join VOL_APPART_COMP_VOLUME app_comp "
                + "on (app_comp.id_appart_doc_volume = app_doc.id_appart_doc_volume) " + "join ARO_FIRMA_COMP firma "
                + "on (firma.id_comp_doc = app_comp.id_comp_doc) " + "join ARO_CONTR_FIRMA_COMP contr_CATENA "
                + "on (contr_CATENA.id_firma_comp = firma.id_firma_comp "
                + "and contr_CATENA.ti_contr = 'CATENA_TRUSTED') " + "join ARO_USO_CERTIF_CA_CONTR_COMP uso_cert "
                + "on (uso_cert.id_contr_firma_comp = contr_CATENA.id_contr_firma_comp) " + "join FIR_CRL crl "
                + "on (crl.id_crl = uso_cert.id_crl_usata) " + "join FIR_CERTIF_CA cert "
                + "on (cert.id_certif_ca = crl.id_certif_ca) " + "join FIR_ISSUER issuer "
                + "on (issuer.id_issuer = cert.id_issuer) " + "where vol.id_volume_conserv = ? ";

        Query q = getEntityManager().createNativeQuery(SELECT_LISTA_CRL);
        q.setParameter(1, idVolume);
        q.setParameter(2, idVolume);
        List<Object> crlObjectList = q.getResultList();
        List<BigDecimal> crlName = new ArrayList<>();

        if (!crlObjectList.isEmpty()) {
            for (Object crlObject : crlObjectList) {
                Object obj = crlObject;
                crlName.add((BigDecimal) obj);
            }
        }
        return crlName;
    }

    public List<BigDecimal> retrieveListaCertificatiCA(BigDecimal idVolume) {
        String SELECT_LISTA_CERTIFICATI_CA = "select distinct " + "cert.id_certif_ca nomeFileCertificatoCA "
                + "from VOL_VOLUME_CONSERV vol " + "join VOL_APPART_UNITA_DOC_VOLUME app_ud "
                + "on (app_ud.id_volume_conserv = vol.id_volume_conserv) " + "join VOL_APPART_DOC_VOLUME app_doc "
                + "on (app_doc.id_appart_unita_doc_volume = app_ud.id_appart_unita_doc_volume) "
                + "join VOL_APPART_COMP_VOLUME app_comp "
                + "on (app_comp.id_appart_doc_volume = app_doc.id_appart_doc_volume) " + "join ARO_FIRMA_COMP firma "
                + "on (firma.id_comp_doc = app_comp.id_comp_doc) " + "join ARO_CONTR_FIRMA_COMP contr_CATENA "
                + "on (contr_CATENA.id_firma_comp = firma.id_firma_comp "
                + "and contr_CATENA.ti_contr = 'CATENA_TRUSTED') " + "join ARO_USO_CERTIF_CA_CONTR_COMP uso_cert "
                + "on (uso_cert.id_contr_firma_comp = contr_CATENA.id_contr_firma_comp) " + "join FIR_CERTIF_CA cert "
                + "on (cert.id_certif_ca = uso_cert.id_certif_ca) " + "join FIR_ISSUER issuer "
                + "on (issuer.id_issuer = cert.id_issuer) " + "where vol.id_volume_conserv = ? ";

        Query q = getEntityManager().createNativeQuery(SELECT_LISTA_CERTIFICATI_CA);
        q.setParameter(1, idVolume);
        List<Object> certificatoObjectCAList = q.getResultList();
        List<BigDecimal> certifCaName = new ArrayList<>();

        if (!certificatoObjectCAList.isEmpty()) {
            for (Object certificatoObjectCA : certificatoObjectCAList) {
                Object obj = certificatoObjectCA;
                certifCaName.add((BigDecimal) obj);
            }
        }
        return certifCaName;
    }

    // Metodi per la visualizzazione/ricerca su COMPONENTI
    public AroVVisCompRowBean getAroVVisCompRowBean(BigDecimal idcompdoc) {
        String queryStr = "SELECT u FROM AroVVisComp u WHERE u.idCompDoc = :idcomp";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idcomp", idcompdoc);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisComp> comp = query.getResultList();

        AroVVisCompRowBean compRowBean = new AroVVisCompRowBean();
        try {
            if (comp != null && !comp.isEmpty()) {
                compRowBean = (AroVVisCompRowBean) Transform.entity2RowBean(comp.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return compRowBean;
    }

    public AroVLisFirmaCompTableBean getAroVLisFirmaCompTableBean(BigDecimal idcompdoc, int maxResults) {
        String queryStr = "SELECT u FROM AroVLisFirmaComp u WHERE u.idCompDoc = :idcomp ORDER BY u.pgFirma";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idcomp", idcompdoc);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisFirmaComp> listaFirmaComp = query.getResultList();

        AroVLisFirmaCompTableBean listaFirmaCompTableBean = new AroVLisFirmaCompTableBean();
        try {
            if (listaFirmaComp != null && !listaFirmaComp.isEmpty()) {
                for (AroVLisFirmaComp row : listaFirmaComp) {
                    AroVLisFirmaCompRowBean rowBean = (AroVLisFirmaCompRowBean) Transform.entity2RowBean(row);
                    if (row.getTiRifTempUsato().equals(TipoRifTemporale.RIF_TEMP_VERS.name())) {
                        rowBean.setTiRifTempUsato("Riferimento temporale versato");
                    } else if (row.getTiRifTempUsato().equals(TipoRifTemporale.DATA_VERS.name())) {
                        rowBean.setTiRifTempUsato("Data versamento");
                    }
                    listaFirmaCompTableBean.add(rowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return listaFirmaCompTableBean;
    }

    public AroVLisMarcaCompTableBean getAroVLisMarcaCompTableBean(BigDecimal idcompdoc, int maxResults) {
        String queryStr = "SELECT u FROM AroVLisMarcaComp u WHERE u.idCompDoc = :idcomp ORDER BY u.pgMarca";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idcomp", idcompdoc);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisMarcaComp> listaMarcaComp = query.getResultList();

        AroVLisMarcaCompTableBean listaMarcaCompTableBean = new AroVLisMarcaCompTableBean();
        try {
            if (listaMarcaComp != null && !listaMarcaComp.isEmpty()) {
                listaMarcaCompTableBean = (AroVLisMarcaCompTableBean) Transform.entities2TableBean(listaMarcaComp);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaMarcaCompTableBean;
    }

    // Metodi per la visualizzazione/ricerca su MARCHE
    public AroVVisMarcaCompRowBean getAroVVisMarcaCompRowBean(BigDecimal idmarca) {
        String queryStr = "SELECT u FROM AroVVisMarcaComp u WHERE u.idMarcaComp = :idmarca";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idmarca", idmarca);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisMarcaComp> compMarca = query.getResultList();

        AroVVisMarcaCompRowBean compMarcaRowBean = new AroVVisMarcaCompRowBean();
        try {
            if (compMarca != null && !compMarca.isEmpty()) {
                compMarcaRowBean = (AroVVisMarcaCompRowBean) Transform.entity2RowBean(compMarca.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return compMarcaRowBean;
    }

    public AroVLisCertifCaMarcaCompTableBean getAroVLisCertifCaMarcaCompTableBean(BigDecimal idmarca, String tiContr) {
        String queryStr = "SELECT u FROM AroVLisCertifCaMarcaComp u WHERE u.idMarcaComp = :idmarca AND u.tiContr = :tiContr ORDER BY u.dlDnIssuerCertifCa";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idmarca", idmarca);
        query.setParameter("tiContr", tiContr);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisCertifCaMarcaComp> listaCertifCaMarcaComp = query.getResultList();

        AroVLisCertifCaMarcaCompTableBean listaCertifCaMarcaCompTableBean = new AroVLisCertifCaMarcaCompTableBean();
        try {
            if (listaCertifCaMarcaComp != null && !listaCertifCaMarcaComp.isEmpty()) {
                listaCertifCaMarcaCompTableBean = (AroVLisCertifCaMarcaCompTableBean) Transform
                        .entities2TableBean(listaCertifCaMarcaComp);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaCertifCaMarcaCompTableBean;
    }

    // Metodi per la visualizzazione/ricerca su FIRME
    public AroVVisFirmaCompRowBean getAroVVisFirmaCompRowBean(BigDecimal idfirma) {
        String queryStr = "SELECT u FROM AroVVisFirmaComp u WHERE u.idFirmaComp = :idfirma";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idfirma", idfirma);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisFirmaComp> compFirma = query.getResultList();

        AroVVisFirmaCompRowBean compFirmaRowBean = new AroVVisFirmaCompRowBean();
        try {
            if (compFirma != null && !compFirma.isEmpty()) {
                compFirmaRowBean = (AroVVisFirmaCompRowBean) Transform.entity2RowBean(compFirma.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return compFirmaRowBean;
    }

    public AroVLisCertifCaFirmaCompTableBean getAroVLisCertifCaFirmaCompTableBean(BigDecimal idfirma, String tiContr) {
        String queryStr = "SELECT u FROM AroVLisCertifCaFirmaComp u WHERE u.idFirmaComp = :idfirma AND u.tiContr = :tiContr ORDER BY u.dlDnIssuerCertifCa";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idfirma", idfirma);
        query.setParameter("tiContr", tiContr);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisCertifCaFirmaComp> listaCertifCaFirmaComp = query.getResultList();

        AroVLisCertifCaFirmaCompTableBean listaCertifCaFirmaCompTableBean = new AroVLisCertifCaFirmaCompTableBean();
        try {
            if (listaCertifCaFirmaComp != null && !listaCertifCaFirmaComp.isEmpty()) {
                listaCertifCaFirmaCompTableBean = (AroVLisCertifCaFirmaCompTableBean) Transform
                        .entities2TableBean(listaCertifCaFirmaComp);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaCertifCaFirmaCompTableBean;
    }

    public AroVLisControfirmaFirmaTableBean getAroVLisControfirmaFirmaTableBean(BigDecimal idfirma) {
        String queryStr = "SELECT u FROM AroVLisControfirmaFirma u WHERE u.idFirmaComp = :idfirma ORDER BY u.nmCognomeFirmatario";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idfirma", idfirma);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisControfirmaFirma> listaControfirmaFirma = query.getResultList();

        AroVLisControfirmaFirmaTableBean listaControfirmaFirmaTableBean = new AroVLisControfirmaFirmaTableBean();
        try {
            if (listaControfirmaFirma != null && !listaControfirmaFirma.isEmpty()) {
                listaControfirmaFirmaTableBean = (AroVLisControfirmaFirmaTableBean) Transform
                        .entities2TableBean(listaControfirmaFirma);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaControfirmaFirmaTableBean;
    }

    public it.eng.parer.slite.gen.viewbean.ElvVListaCompElvTableBean getElvVListaCompElvViewBean(
            BigDecimal idElencoVers, ElenchiVersamentoForm.ComponentiFiltri filtri, Date[] dateValidate, long idUtente,
            BigDecimal idStrut) throws it.eng.spagoCore.error.EMFError {

        return getElvVListaCompElvViewBean(idElencoVers, idUtente, idStrut);
    }

    public ElvVListaCompElvTableBean getElvVListaCompElvViewBean(BigDecimal idElencoVers, long idUtente,
            BigDecimal idStrut) {
        String queryStr = "SELECT u.* FROM TABLE(Elv_Vp_Lista_Comp_Elv(:idElencoVers, :idStrut, :idUserIam)) u ";
        Query query = getEntityManager().createNativeQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("idUserIam", idUtente);
        query.setParameter("idStrut", idStrut);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI COMPONENTI
        ElvVListaCompElvTableBean componentiTableBean = new ElvVListaCompElvTableBean();
        List<Object[]> listaComponentiObj = (List<Object[]>) query.getResultList();
        for (Object[] componenteObj : listaComponentiObj) {
            ElvVListaCompElvRowBean componentiRowBean = new ElvVListaCompElvRowBean();

            // Applica l'operatore ternario a tutti i campi per gestire potenziali null e cast
            componentiRowBean.setIdElencoVers(componenteObj[0] != null ? (BigDecimal) componenteObj[0] : null);
            componentiRowBean.setIdCompDoc(componenteObj[1] != null ? (BigDecimal) componenteObj[1] : null);
            componentiRowBean.setDsUrnCompCalc(componenteObj[2] != null ? (String) componenteObj[2] : null);
            componentiRowBean.setDsUrnCompCalcShort(componenteObj[3] != null ? (String) componenteObj[3] : null);
            componentiRowBean.setDsNomeCompVers(componenteObj[4] != null ? (String) componenteObj[4] : null);
            componentiRowBean.setDsOrdComp(componenteObj[5] != null ? (String) componenteObj[5] : null);
            componentiRowBean.setTiSupportoComp(componenteObj[6] != null ? (String) componenteObj[6] : null);
            componentiRowBean.setNmTipoStrutDoc(componenteObj[7] != null ? (String) componenteObj[7] : null);
            componentiRowBean.setNmTipoCompDoc(componenteObj[8] != null ? (String) componenteObj[8] : null);
            componentiRowBean.setNmFormatoFileDocVers(componenteObj[9] != null ? (String) componenteObj[9] : null);
            componentiRowBean.setDtCreazioneDoc(componenteObj[10] != null ? (Timestamp) componenteObj[10] : null);
            componentiRowBean.setNiSizeFileCalc(componenteObj[11] != null ? (BigDecimal) componenteObj[11] : null);
            // Caso speciale per Character -> String
            componentiRowBean
                    .setFlCompFirmato(componenteObj[12] != null ? ((Character) componenteObj[12]).toString() : null);
            componentiRowBean.setTiEsitoVerifFirmeVers(componenteObj[13] != null ? (String) componenteObj[13] : null);
            componentiRowBean.setIdUnitaDoc(componenteObj[14] != null ? (BigDecimal) componenteObj[14] : null);
            componentiRowBean.setIdDoc(componenteObj[15] != null ? (BigDecimal) componenteObj[15] : null);
            componentiRowBean.setCdRegistroKeyUnitaDoc(componenteObj[16] != null ? (String) componenteObj[16] : null);
            componentiRowBean.setIdTipoUnitaDoc(componenteObj[17] != null ? (BigDecimal) componenteObj[17] : null);
            componentiRowBean.setIdTipoDoc(componenteObj[18] != null ? (BigDecimal) componenteObj[18] : null);
            componentiRowBean.setIdSubStrut(componenteObj[19] != null ? (BigDecimal) componenteObj[19] : null);
            componentiRowBean.setTiStatoElencoVers(componenteObj[20] != null ? (String) componenteObj[20] : null);
            componentiRowBean.setTiStatoConservazione(componenteObj[21] != null ? (String) componenteObj[21] : null);

            // setta il campo relativo alla checkbox select_comp non ceccato
            // Questo campo è impostato manualmente, non proviene dalla query, quindi rimane invariato
            componentiRowBean.setString("select_comp", "0");

            componentiTableBean.add(componentiRowBean);
        }

        return componentiTableBean;

    }

    public boolean isComponenteInElenco(BigDecimal idCompDoc) {
        String queryStr = "SELECT COUNT(u) FROM ElvVListaCompElv u " + "WHERE u.id.idCompDoc = :idCompDoc";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idCompDoc", idCompDoc);
        Long conta = (Long) query.getSingleResult();
        return conta > 0;
    }

    public boolean isComponenteInVolume(BigDecimal idCompDoc) {
        String queryStr = "SELECT COUNT(u) FROM VolVListaCompVol u " + "WHERE u.idCompDoc = :idCompDoc";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idCompDoc", idCompDoc);
        Long conta = (Long) query.getSingleResult();
        return conta > 0;
    }

    // Metodi per la visualizzazione/ricerca delle informazioni su volumi riferite al componente
    public AroVVisCompVolRowBean getAroVVisCompVolRowBean(BigDecimal idCompDoc) {
        String queryStr = "SELECT u FROM AroVVisCompVol u WHERE u.idCompDoc = :idCompDoc";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idCompDoc", idCompDoc);
        List<AroVVisCompVol> compVolList = query.getResultList();
        AroVVisCompVolRowBean compVolRowBean = new AroVVisCompVolRowBean();
        try {
            if (compVolList != null && !compVolList.isEmpty()) {
                compVolRowBean = (AroVVisCompVolRowBean) Transform.entity2RowBean(compVolList.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return compVolRowBean;
    }

    public List<AroCompDoc> getAroCompDocsByIdUnitaDoc(long idUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT comp FROM AroCompDoc comp JOIN comp.aroStrutDoc strutDoc JOIN strutDoc.aroDoc doc JOIN doc.aroUnitaDoc ud WHERE ud.idUnitaDoc = :idUnitaDoc");
        query.setParameter("idUnitaDoc", idUnitaDoc);
        return query.getResultList();
    }

    public boolean isAroUnitaDocReferredByOtherAroCompDocs(Long idUnitaDoc, BigDecimal idStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(comp) FROM AroCompDoc comp WHERE comp.aroUnitaDoc.idUnitaDoc = :idUnitaDoc AND comp.idStrut = :idStrut");
        query.setParameter("idUnitaDoc", idUnitaDoc);
        query.setParameter("idStrut", idStrut);
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    public BigDecimal[] retrieveStrutUnitaDoc(BigDecimal idCompDoc) {
        BigDecimal[] ids = new BigDecimal[2];
        AroCompDoc compDoc = getEntityManager().find(AroCompDoc.class, idCompDoc.longValue());
        ids[0] = BigDecimal.valueOf(compDoc.getAroStrutDoc().getAroDoc().getAroUnitaDoc().getIdUnitaDoc());
        ids[1] = compDoc.getIdStrut();
        return ids;
    }
}
