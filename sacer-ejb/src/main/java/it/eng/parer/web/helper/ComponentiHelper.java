package it.eng.parer.web.helper;

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
import it.eng.parer.viewEntity.ElvVListaCompElv;
import it.eng.parer.viewEntity.VolVListaCompVol;
import it.eng.parer.web.util.BlobObject;
import it.eng.parer.web.util.StringPadding;
import it.eng.parer.web.util.Transform;
import it.eng.spagoCore.error.EMFError;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class ComponentiHelper Contiene i metodi (implementati di ComponentiHelperLocal), per la
 * gestione della persistenza su DB per le operazioni CRUD su oggetti di ComponentiTableBean e ComponentiRowBean
 */
@Stateless
@LocalBean
public class ComponentiHelper extends GenericHelper {

    private static final Logger log = LoggerFactory.getLogger(ComponentiHelper.class.getName());

    public AroCompDocRowBean getAroCompDocRowBean(BigDecimal idComp, BigDecimal idStrut) {
        String queryStr = "SELECT u FROM AroCompDoc u where u.idCompDoc = :idcomp and u.idStrut = :idstrut";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idcomp", idComp);
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
        String whereWord = "and ";
        // StringBuilder queryStr = new StringBuilder(
        // "SELECT DISTINCT new it.eng.parer.viewEntity.AroVRicComp(u.idCompDoc, u.idFirmaComp, u.dsUrnCompCalc,
        // u.dsUrnCompCalcShort, u.dsNomeCompVers, u.dsOrdComp, u.tiSupportoComp, u.nmTipoStrutDoc, u.nmTipoCompDoc,
        // u.nmFormatoFileDocVers, u.dtCreazioneDoc, u.niSizeFileCalc, u.flCompFirmato, u.tiEsitoVerifFirmeVers,
        // u.idUnitaDoc, u.idStrutUnitaDoc, u.tiStatoElencoVers, u.tiStatoConservazione) FROM AroVRicComp u WHERE
        // u.idStrutUnitaDoc = :idstrut ");
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT new it.eng.parer.viewEntity.AroVRicComp"
                + "(u.idCompDoc, u.dsUrnCompCalc, u.dsNomeCompVers, u.tiSupportoComp, u.nmTipoCompDoc, u.nmFormatoFileDocVers, u.dtCreazioneDoc, u.niSizeFileCalc, u.flCompFirmato, u.tiEsitoVerifFirmeVers, u.tiStatoElencoVers, u.tiStatoConservazione) FROM AroVRicComp u WHERE u.idStrutUnitaDoc = :idstrut ");

        // Inserimento nella query dei tipi unità documentaria abilitati
        Set<BigDecimal> idTipoUnitaDocSet = new HashSet<>();
        for (DecTipoUnitaDocRowBean row : tmpTableBeanTipoUD) {
            idTipoUnitaDocSet.add(row.getIdTipoUnitaDoc());
        }
        if (idTipoUnitaDocSet.isEmpty()) {
            idTipoUnitaDocSet.add(new BigDecimal("0"));
        }
        queryStr.append(whereWord).append("u.idTipoUnitaDoc IN :idtipounitadocin ");

        // Inserimento nella query dei tipi documento abilitati
        Set<BigDecimal> idTipoDocSet = new HashSet<>();
        for (DecTipoDocRowBean row : tmpTableBeanTipoDoc) {
            idTipoDocSet.add(row.getIdTipoDoc());
        }
        if (idTipoDocSet.isEmpty()) {
            idTipoDocSet.add(new BigDecimal("0"));
        }

        queryStr.append(whereWord).append("u.idTipoDoc IN :idtipodocin ");

        // Inserimento nella query del filtro CHIAVE DOCUMENTO
        String registro = filtri.getCd_registro_key_unita_doc().parse();
        BigDecimal anno = filtri.getAa_key_unita_doc().parse();
        String codice = filtri.getCd_key_unita_doc().parse();
        BigDecimal anno_range_da = filtri.getAa_key_unita_doc_da().parse();
        BigDecimal anno_range_a = filtri.getAa_key_unita_doc_a().parse();
        String codice_range_da = filtri.getCd_key_unita_doc_da().parse();
        String codice_range_a = filtri.getCd_key_unita_doc_a().parse();
        String idComp = filtri.getDs_id_comp_vers().parse();

        if (idComp != null) {
            queryStr.append(whereWord).append("UPPER(u.dsIdCompVers) LIKE :idcomp ");
            whereWord = "and ";
        }

        if (StringUtils.isNotBlank(registro)) {
            queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc = :registroin ");
            whereWord = "AND ";
        } else {
            queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc IN :registroin ");
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

        if (anno_range_da != null && anno_range_a != null) {
            queryStr.append(whereWord).append("(u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }

        if (codice_range_da != null && codice_range_a != null) {
            codice_range_da = StringPadding.padString(codice_range_da, "0", 12, StringPadding.PADDING_LEFT);
            codice_range_a = StringPadding.padString(codice_range_a, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord)
                    .append("FUNC('lpad', u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro DATA CREAZIONE DA - A
        Date data_da = (dateAcquisizioneValidate != null ? dateAcquisizioneValidate[0] : null);
        Date data_a = (dateAcquisizioneValidate != null ? dateAcquisizioneValidate[1] : null);

        if ((data_da != null) && (data_a != null)) {
            queryStr.append(whereWord).append("(u.dtCreazioneDoc between :datada AND :dataa) ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Tipo Struttura Documento
        BigDecimal tipoStrutDoc = filtri.getNm_tipo_strut_doc().parse();
        if (tipoStrutDoc != null) {
            queryStr.append(whereWord).append("u.idTipoStrutDoc = :tipostrutdocin ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Tipo Componente Documento
        BigDecimal tipoCompDoc = filtri.getNm_tipo_comp_doc().parse();
        if (tipoCompDoc != null) {
            queryStr.append(whereWord).append("u.idTipoCompDoc = :tipocompdocin ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Formato
        String formato = filtri.getNm_formato_file_vers().parse();
        if (formato != null) {
            queryStr.append(whereWord).append("u.nmFormatoFileDocVers = :formatoin ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro file size
        BigDecimal fileSizeDa = filtri.getNi_size_file_da().parse();
        BigDecimal fileSizeA = filtri.getNi_size_file_a().parse();
        if (fileSizeDa == null && fileSizeA != null) {
            fileSizeDa = BigDecimal.ZERO;
        }
        if (fileSizeDa != null && fileSizeA != null) {
            queryStr.append(whereWord).append("u.niSizeFileCalc between :filesizedain AND :filesizeain ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro PRESENZA FIRME
        String presenza = filtri.getFl_comp_firmato().parse();
        if (presenza != null) {
            queryStr.append(whereWord).append("u.flCompFirmato = :presenzain ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Conformita FIRME
        String conformita = filtri.getTi_esito_contr_conforme().parse();
        if (conformita != null) {
            queryStr.append(whereWord).append("u.tiEsitoContrConforme = :conformitain ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Esito Firme
        String esitoFirme = filtri.getTi_esito_verif_firme().parse();
        if (esitoFirme != null) {
            queryStr.append(whereWord).append("u.tiEsitoVerifFirmeVers = :esitofirmein ");
            whereWord = "and ";
        }

        // // Inserimento nella query del filtro Esito Firme Chiusura Volume
        // String esitoFirmeChius = filtri.getTi_esito_verif_firme_chius().parse();
        // if (esitoFirmeChius != null) {
        // queryStr.append(whereWord).append("u.tiEsitoVerifFirmeChius = :esitofirmechiusin ");
        // whereWord = "and ";
        // }
        Date data_val_da = null;
        Date data_val_a = null;

        // Inserimento nella query del filtro DATA SCADENZA DA - A
        if (filtri.getDt_scad_firma_comp_da().parse() != null) {
            data_val_da = new Date(filtri.getDt_scad_firma_comp_da().parse().getTime());
            if (filtri.getDt_scad_firma_comp_a().parse() != null) {
                data_val_a = new Date(filtri.getDt_scad_firma_comp_a().parse().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(data_val_a);
                calendar.add(Calendar.DATE, 1);
                data_val_a = calendar.getTime();
            } else {
                data_val_a = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(data_val_a);
                calendar.add(Calendar.DATE, 1);
                data_val_a = calendar.getTime();
            }
        }

        if ((data_val_da != null) && (data_val_a != null)) {
            queryStr.append(whereWord).append("(u.dtScadCertifFirmatario between :datavalda AND :datavala) ");
            whereWord = "and ";
        }

        if (StringUtils.isNotBlank(filtri.getFl_rif_temp_vers().parse())) {
            queryStr.append(whereWord).append("u.flRifTempVers = :flRifTempVers ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_rif_temp_vers().parse())) {
            queryStr.append(whereWord).append("UPPER(u.dsRifTempVers) LIKE :dsRifTempVers ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_nome_comp_vers().parse())) {
            queryStr.append(whereWord).append("UPPER(u.dsNomeCompVers) LIKE :nomeCompVersIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getFl_hash_vers().parse())) {
            queryStr.append(whereWord).append("u.flHashVers = :flHashVers ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_hash_file_vers().parse())) {
            queryStr.append(whereWord).append("u.dsHashFileVers = :hashFileVersIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getNm_mimetype_file().parse())) {
            queryStr.append(whereWord).append("UPPER(u.nmMimetypeFile) LIKE :mimetypeIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDl_urn_comp_vers().parse())) {
            queryStr.append(whereWord).append("UPPER(u.dlUrnCompVers) LIKE :urnCompVersIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_formato_rappr_calc().parse())) {
            queryStr.append(whereWord).append("u.dsFormatoRapprCalc = :formatoRapprCalcIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_formato_rappr_esteso_calc().parse())) {
            queryStr.append(whereWord).append("u.dsFormatoRapprEstesoCalc = :formatoRapprEstesoCalcIn ");
            whereWord = " AND ";
        }

        String forzaAcc = filtri.getFl_forza_accettazione().parse();
        if (StringUtils.isNotBlank(forzaAcc)) {
            queryStr.append(whereWord).append("u.flForzaAccettazione = :forzaaccin ");
            whereWord = " AND ";
        }

        String forzaConserva = filtri.getFl_forza_conservazione().parse();
        if (StringUtils.isNotBlank(forzaConserva)) {
            queryStr.append(whereWord).append("u.flForzaConservazione = :forzaconservain ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getTi_esito_contr_formato_file().parse())) {
            queryStr.append(whereWord).append("u.tiEsitoContrFormatoFile = :esitoContrFormatoFileIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_hash_file_calc().parse())) {
            queryStr.append(whereWord).append("u.dsHashFileCalc = :hashFileCalcIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_algo_hash_file_calc().parse())) {
            queryStr.append(whereWord).append("u.dsAlgoHashFileCalc = :algoHashFileCalcIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getCd_encoding_hash_file_calc().parse())) {
            queryStr.append(whereWord).append("u.cdEncodingHashFileCalc = :encodingHashFileCalcIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_urn_comp_calc().parse())) {
            queryStr.append(whereWord).append("UPPER(u.dsUrnCompCalc) LIKE :urnCompCalcIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getTi_supporto_comp().parse())) {
            queryStr.append(whereWord).append("u.tiSupportoComp = :supportoCompIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getNm_tipo_rappr_comp().parse())) {
            queryStr.append(whereWord).append("u.nmTipoRapprComp = :tipoRapprCompIn ");
            whereWord = " AND ";
        }

        // if (StringUtils.isNotBlank(filtri.getFl_doc_annul().parse())) {
        // queryStr.append(whereWord).append("u.flDocAnnul = :flDocAnnulIn ");
        // whereWord = " AND ";
        // }
        List<BigDecimal> subStruts = filtri.getNm_sub_strut().parse();
        if (!subStruts.isEmpty()) {
            queryStr.append(whereWord).append(" u.idSubStrut IN :subStruts ");
            whereWord = "AND ";
        }

        // ordina per data creazione decrescente
        queryStr.append("ORDER BY u.dsOrdComp");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
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
            Set<Object> keySet = (Set<Object>) filtri.getCd_registro_key_unita_doc().getDecodeMap().keySet();
            query.setParameter("registroin", keySet);
        }

        if (anno != null) {
            query.setParameter("annoin", anno);
        }

        if (codice != null) {
            query.setParameter("codicein", codice);
        }

        if (anno_range_da != null && anno_range_a != null) {
            query.setParameter("annoin_da", anno_range_da);
            query.setParameter("annoin_a", anno_range_a);
        }

        if (codice_range_da != null && codice_range_a != null) {
            query.setParameter("codicein_da", codice_range_da);
            query.setParameter("codicein_a", codice_range_a);
        }

        if (data_da != null && data_a != null) {
            query.setParameter("datada", data_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_a, TemporalType.TIMESTAMP);
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
        // if (esitoFirmeChius != null) {
        // query.setParameter("esitofirmechiusin", esitoFirmeChius);
        // }
        if (data_val_da != null && data_val_a != null) {
            query.setParameter("datavalda", data_val_da, TemporalType.DATE);
            query.setParameter("datavala", data_val_a, TemporalType.DATE);
        }

        if (StringUtils.isNotBlank(filtri.getFl_rif_temp_vers().parse())) {
            query.setParameter("flRifTempVers", filtri.getFl_rif_temp_vers().parse());
        }

        if (StringUtils.isNotBlank(filtri.getDs_rif_temp_vers().parse())) {
            query.setParameter("dsRifTempVers", "%" + filtri.getDs_rif_temp_vers().parse().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getDs_nome_comp_vers().parse())) {
            query.setParameter("nomeCompVersIn", "%" + filtri.getDs_nome_comp_vers().parse().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getFl_hash_vers().parse())) {
            query.setParameter("flHashVers", filtri.getFl_hash_vers().parse());
        }

        if (StringUtils.isNotBlank(filtri.getDs_hash_file_vers().parse())) {
            query.setParameter("hashFileVersIn", filtri.getDs_hash_file_vers().parse());
        }

        if (StringUtils.isNotBlank(filtri.getNm_mimetype_file().parse())) {
            query.setParameter("mimetypeIn", "%" + filtri.getNm_mimetype_file().parse().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getDl_urn_comp_vers().parse())) {
            query.setParameter("urnCompVersIn", "%" + filtri.getDl_urn_comp_vers().parse().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getDs_formato_rappr_calc().parse())) {
            query.setParameter("formatoRapprCalcIn", filtri.getDs_formato_rappr_calc().parse());
        }

        if (StringUtils.isNotBlank(filtri.getDs_formato_rappr_esteso_calc().parse())) {
            query.setParameter("formatoRapprEstesoCalcIn", filtri.getDs_formato_rappr_esteso_calc().parse());
        }

        if (StringUtils.isNotBlank(forzaAcc)) {
            query.setParameter("forzaaccin", forzaAcc);
        }

        if (StringUtils.isNotBlank(forzaConserva)) {
            query.setParameter("forzaconservain", forzaConserva);
        }

        if (StringUtils.isNotBlank(filtri.getTi_esito_contr_formato_file().parse())) {
            query.setParameter("esitoContrFormatoFileIn", filtri.getTi_esito_contr_formato_file().parse());
        }

        if (StringUtils.isNotBlank(filtri.getDs_hash_file_calc().parse())) {
            query.setParameter("hashFileCalcIn", filtri.getDs_hash_file_calc().parse());
        }

        if (StringUtils.isNotBlank(filtri.getDs_algo_hash_file_calc().parse())) {
            query.setParameter("algoHashFileCalcIn", filtri.getDs_algo_hash_file_calc().parse());
        }

        if (StringUtils.isNotBlank(filtri.getCd_encoding_hash_file_calc().parse())) {
            query.setParameter("encodingHashFileCalcIn", filtri.getCd_encoding_hash_file_calc().parse());
        }

        if (StringUtils.isNotBlank(filtri.getDs_urn_comp_calc().parse())) {
            query.setParameter("urnCompCalcIn", "%" + filtri.getDs_urn_comp_calc().parse().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getTi_supporto_comp().parse())) {
            query.setParameter("supportoCompIn", filtri.getTi_supporto_comp().parse());
        }

        if (StringUtils.isNotBlank(filtri.getNm_tipo_rappr_comp().parse())) {
            query.setParameter("tipoRapprCompIn", filtri.getNm_tipo_rappr_comp().parse());
        }

        // if (StringUtils.isNotBlank(filtri.getFl_doc_annul().parse())) {
        // query.setParameter("flDocAnnulIn", filtri.getFl_doc_annul().parse());
        // }
        if (!subStruts.isEmpty()) {
            query.setParameter("subStruts", subStruts);
        }

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "COMPONENTI"
        List<AroVRicComp> listaComponenti = query.getResultList();

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
    public VolVListaCompVolTableBean getVolVListaCompVolViewBean(BigDecimal idVolume, ComponentiFiltri filtri,
            Date[] dateValidate, DecTipoUnitaDocTableBean tmpTableBeanTipoUD, DecTipoDocTableBean tmpTableBeanTipoDoc,
            OrgSubStrutTableBean tmpSubStrutsTableBean, int maxResults) throws EMFError {
        String whereWord = "and ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT new it.eng.parer.viewEntity.VolVListaCompVol(u.idVolumeConserv, u.idCompDoc, "
                        + "u.dsUrnCompCalc, u.dsUrnCompCalcShort, u.dsNomeCompVers, u.dsOrdComp, u.tiSupportoComp, "
                        + "u.nmTipoStrutDoc, u.nmTipoCompDoc, u.nmFormatoFileDocVers, u.dtCreazioneDoc, u.niSizeFileCalc, "
                        + "u.flCompFirmato, u.tiEsitoVerifFirmeVers, u.tiEsitoVerifFirmeChius, u.idUnitaDoc, u.idDoc, "
                        + "u.cdRegistroKeyUnitaDoc, u.idTipoUnitaDoc) "
                        + "FROM VolVListaCompVol u WHERE u.idVolumeConserv = :idvol ");

        // Inserimento nella query dei tipi unità documentaria abilitati
        Set<BigDecimal> idTipoUnitaDocSet = new HashSet<>();
        for (DecTipoUnitaDocRowBean row : tmpTableBeanTipoUD) {
            idTipoUnitaDocSet.add(row.getIdTipoUnitaDoc());
        }
        if (idTipoUnitaDocSet.isEmpty()) {
            idTipoUnitaDocSet.add(new BigDecimal("0"));
        }
        queryStr.append(whereWord).append("u.idTipoUnitaDoc IN :idtipounitadocin ");

        // Inserimento nella query dei tipi documento abilitati
        Set<BigDecimal> idTipoDocSet = new HashSet<>();
        for (DecTipoDocRowBean row : tmpTableBeanTipoDoc) {
            idTipoDocSet.add(row.getIdTipoDoc());
        }
        if (idTipoDocSet.isEmpty()) {
            idTipoDocSet.add(new BigDecimal("0"));
        }
        queryStr.append(whereWord).append("u.idTipoDoc IN :idtipodocin ");

        // Inserimento nella query delle sottostrutture abilitate
        Set<BigDecimal> idSubStrutSet = new HashSet<>();
        for (OrgSubStrutRowBean row : tmpSubStrutsTableBean) {
            idSubStrutSet.add(row.getIdSubStrut());
        }
        if (idTipoDocSet.isEmpty()) {
            idSubStrutSet.add(new BigDecimal("0"));
        }
        queryStr.append(whereWord).append("u.idSubStrut IN :idsubstrutin ");

        // Inserimento nella query del filtro CHIAVE DOCUMENTO
        String registro = filtri.getCd_registro_key_unita_doc().parse();
        BigDecimal anno = filtri.getAa_key_unita_doc().parse();
        String codice = filtri.getCd_key_unita_doc().parse();
        BigDecimal anno_range_da = filtri.getAa_key_unita_doc_da().parse();
        BigDecimal anno_range_a = filtri.getAa_key_unita_doc_a().parse();
        String codice_range_da = filtri.getCd_key_unita_doc_da().parse();
        String codice_range_a = filtri.getCd_key_unita_doc_a().parse();

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

        if (anno_range_da != null && anno_range_a != null) {
            queryStr.append(whereWord).append("(u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }

        if (codice_range_da != null && codice_range_a != null) {
            codice_range_da = StringPadding.padString(codice_range_da, "0", 12, StringPadding.PADDING_LEFT);
            codice_range_a = StringPadding.padString(codice_range_a, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord)
                    .append("FUNC('lpad', u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro DATA CREAZIONE DA - A
        Date data_da = (dateValidate != null ? dateValidate[0] : null);
        Date data_a = (dateValidate != null ? dateValidate[1] : null);

        if ((data_da != null) && (data_a != null)) {
            queryStr.append(whereWord).append("(u.dtCreazioneDoc between :datada AND :dataa) ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Tipo Struttura Documento
        BigDecimal tipoStrutDoc = filtri.getNm_tipo_strut_doc().parse();
        if (tipoStrutDoc != null) {
            queryStr.append(whereWord).append("u.idTipoStrutDoc = :tipostrutdocin ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Tipo Componente Documento
        BigDecimal tipoCompDoc = filtri.getNm_tipo_comp_doc().parse();
        if (tipoCompDoc != null) {
            queryStr.append(whereWord).append("u.idTipoCompDoc = :tipocompdocin ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Formato
        String formato = filtri.getNm_formato_file_vers().parse();
        if (formato != null) {
            queryStr.append(whereWord).append("u.nmFormatoFileDocVers = :formatoin ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro file size
        BigDecimal fileSizeDa = filtri.getNi_size_file_da().parse();
        BigDecimal fileSizeA = filtri.getNi_size_file_a().parse();
        if (fileSizeDa == null && fileSizeA != null) {
            fileSizeDa = BigDecimal.ZERO;
        }
        if (fileSizeDa != null && fileSizeA != null) {
            queryStr.append(whereWord).append("u.niSizeFileCalc between :filesizedain AND :filesizeain ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro PRESENZA FIRME
        String presenza = filtri.getFl_comp_firmato().parse();
        if (presenza != null) {
            queryStr.append(whereWord).append("u.flCompFirmato = :presenzain ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Conformita FIRME
        String conformita = filtri.getTi_esito_contr_conforme().parse();
        if (conformita != null) {
            queryStr.append(whereWord).append("u.tiEsitoContrConforme = :conformitain ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Esito Firme
        String esitoFirme = filtri.getTi_esito_verif_firme_vers().parse();
        if (esitoFirme != null) {
            queryStr.append(whereWord).append("u.tiEsitoVerifFirmeVers = :esitofirmein ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Esito Firme Chiusura Volume
        String esitoFirmeChius = filtri.getTi_esito_verif_firme_chius().parse();
        if (esitoFirmeChius != null) {
            queryStr.append(whereWord).append("u.tiEsitoVerifFirmeChius = :esitofirmechiusin ");
            whereWord = "and ";
        }

        Date data_val_da = null;
        Date data_val_a = null;

        // Inserimento nella query del filtro DATA SCADENZA FIRMA DA - A
        if (filtri.getDt_scad_firma_comp_da().parse() != null) {
            data_val_da = new Date(filtri.getDt_scad_firma_comp_da().parse().getTime());
            if (filtri.getDt_scad_firma_comp_a().parse() != null) {
                data_val_a = new Date(filtri.getDt_scad_firma_comp_a().parse().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(data_val_a);
                calendar.add(Calendar.DATE, 1);
                data_val_a = calendar.getTime();
            } else {
                data_val_a = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(data_val_a);
                calendar.add(Calendar.DATE, 1);
                data_val_a = calendar.getTime();
            }
        }

        if ((data_val_da != null) && (data_val_a != null)) {
            queryStr.append(whereWord).append("(u.dtScadCertifFirmatario between :datavalda AND :datavala) ");
            whereWord = "and ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_nome_comp_vers().parse())) {
            queryStr.append(whereWord).append("UPPER(u.dsNomeCompVers) LIKE :nomeCompVersIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_hash_file_vers().parse())) {
            queryStr.append(whereWord).append("u.dsHashFileVers = :hashFileVersIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getNm_mimetype_file().parse())) {
            queryStr.append(whereWord).append("UPPER(u.nmMimetypeFile) LIKE :mimetypeIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDl_urn_comp_vers().parse())) {
            queryStr.append(whereWord).append("UPPER(u.dlUrnCompVers) LIKE :urnCompVersIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_formato_rappr_calc().parse())) {
            queryStr.append(whereWord).append("u.dsFormatoRapprCalc = :formatoRapprCalcIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_formato_rappr_esteso_calc().parse())) {
            queryStr.append(whereWord).append("u.dsFormatoRapprEstesoCalc = :formatoRapprEstesoCalcIn ");
            whereWord = " AND ";
        }

        String forzaAcc = filtri.getFl_forza_accettazione().parse();
        if (StringUtils.isNotBlank(forzaAcc)) {
            queryStr.append(whereWord).append("u.flForzaAccettazione = :forzaaccin ");
            whereWord = " AND ";
        }

        String forzaConserva = filtri.getFl_forza_conservazione().parse();
        if (StringUtils.isNotBlank(forzaConserva)) {
            queryStr.append(whereWord).append("u.flForzaConservazione = :forzaconservain ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getTi_esito_contr_formato_file().parse())) {
            queryStr.append(whereWord).append("u.tiEsitoContrFormatoFile = :esitoContrFormatoFileIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_hash_file_calc().parse())) {
            queryStr.append(whereWord).append("u.dsHashFileCalc = :hashFileCalcIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_algo_hash_file_calc().parse())) {
            queryStr.append(whereWord).append("u.dsAlgoHashFileCalc = :algoHashFileCalcIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getCd_encoding_hash_file_calc().parse())) {
            queryStr.append(whereWord).append("u.cdEncodingHashFileCalc = :encodingHashFileCalcIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_urn_comp_calc().parse())) {
            queryStr.append(whereWord).append("UPPER(u.dsUrnCompCalc) LIKE :urnCompCalcIn ");
            // whereWord = " AND ";
        }

        queryStr.append("ORDER BY u.dsOrdComp ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
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

        if (anno_range_da != null && anno_range_a != null) {
            query.setParameter("annoin_da", anno_range_da);
            query.setParameter("annoin_a", anno_range_a);
        }

        if (codice_range_da != null && codice_range_a != null) {
            query.setParameter("codicein_da", codice_range_da);
            query.setParameter("codicein_a", codice_range_a);
        }

        if (data_da != null && data_a != null) {
            query.setParameter("datada", data_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_a, TemporalType.TIMESTAMP);
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
        if (data_val_da != null && data_val_a != null) {
            query.setParameter("datavalda", data_val_da, TemporalType.DATE);
            query.setParameter("datavala", data_val_a, TemporalType.DATE);
        }

        if (StringUtils.isNotBlank(filtri.getDs_nome_comp_vers().parse())) {
            query.setParameter("nomeCompVersIn", "%" + filtri.getDs_nome_comp_vers().parse().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getDs_hash_file_vers().parse())) {
            query.setParameter("hashFileVersIn", filtri.getDs_hash_file_vers().parse());
        }

        if (StringUtils.isNotBlank(filtri.getNm_mimetype_file().parse())) {
            query.setParameter("mimetypeIn", "%" + filtri.getNm_mimetype_file().parse().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getDl_urn_comp_vers().parse())) {
            query.setParameter("urnCompVersIn", "%" + filtri.getDl_urn_comp_vers().parse().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getDs_formato_rappr_calc().parse())) {
            query.setParameter("formatoRapprCalcIn", filtri.getDs_formato_rappr_calc().parse());
        }

        if (StringUtils.isNotBlank(filtri.getDs_formato_rappr_esteso_calc().parse())) {
            query.setParameter("formatoRapprEstesoCalcIn", filtri.getDs_formato_rappr_esteso_calc().parse());
        }

        if (StringUtils.isNotBlank(forzaAcc)) {
            query.setParameter("forzaaccin", forzaAcc);
        }

        if (StringUtils.isNotBlank(forzaConserva)) {
            query.setParameter("forzaconservain", forzaConserva);
        }

        if (StringUtils.isNotBlank(filtri.getTi_esito_contr_formato_file().parse())) {
            query.setParameter("esitoContrFormatoFileIn", filtri.getTi_esito_contr_formato_file().parse());
        }

        if (StringUtils.isNotBlank(filtri.getDs_hash_file_calc().parse())) {
            query.setParameter("hashFileCalcIn", filtri.getDs_hash_file_calc().parse());
        }

        if (StringUtils.isNotBlank(filtri.getDs_algo_hash_file_calc().parse())) {
            query.setParameter("algoHashFileCalcIn", filtri.getDs_algo_hash_file_calc().parse());
        }

        if (StringUtils.isNotBlank(filtri.getCd_encoding_hash_file_calc().parse())) {
            query.setParameter("encodingHashFileCalcIn", filtri.getCd_encoding_hash_file_calc().parse());
        }

        if (StringUtils.isNotBlank(filtri.getDs_urn_comp_calc().parse())) {
            query.setParameter("urnCompCalcIn", "%" + filtri.getDs_urn_comp_calc().parse().toUpperCase() + "%");
        }

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "VOLUMI"
        List<VolVListaCompVol> listaComponenti = query.getResultList();

        VolVListaCompVolTableBean componentiTableBean = new VolVListaCompVolTableBean();
        try {
            if (listaComponenti != null && !listaComponenti.isEmpty()) {
                componentiTableBean = (VolVListaCompVolTableBean) Transform.entities2TableBean(listaComponenti);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // setta il campo relativo alla checkbox select_comp non ceccato
        for (int i = 0; i < componentiTableBean.size(); i++) {
            VolVListaCompVolRowBean row = componentiTableBean.getRow(i);
            row.setString("select_comp", "0");
        }

        return componentiTableBean;
    }

    public List[] getBlobboByteList(BigDecimal idVolume) throws EMFError {
        // creo le lista che conterranno i blobbi dei file da restituire
        List<BlobObject> listaBlobbiCRL = new ArrayList<BlobObject>();
        List<BlobObject> listaBlobbiCertif = new ArrayList<BlobObject>();
        // ArrayList da restituire
        List[] blobbi = new ArrayList[2];

        List<BigDecimal> CRLNameList = retrieveListaCRL(idVolume);
        List<BigDecimal> CertifCaNameList = retrieveListaCertificatiCA(idVolume);

        // mi cucco i blobbi delle CRL
        if (CRLNameList != null) {
            for (int i = 0; i < CRLNameList.size(); i++) {
                BigDecimal idCRL = new BigDecimal(CRLNameList.get(i).toString());
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
        if (CertifCaNameList != null) {
            for (int i = 0; i < CertifCaNameList.size(); i++) {
                BigDecimal idCertif = new BigDecimal(CertifCaNameList.get(i).toString());
                FirCertifCa fircertif = getEntityManager().find(FirCertifCa.class, idCertif.longValue());
                if (fircertif != null && fircertif.getFirFilePerFirma() != null) {
                    // recupero finalmente il file della certif
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
        List<BigDecimal> crlName = new ArrayList<BigDecimal>();

        if (crlObjectList.size() > 0) {
            for (Object crlObject : crlObjectList) {
                Object obj = (Object) crlObject;
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
        List<BigDecimal> certifCaName = new ArrayList<BigDecimal>();

        if (certificatoObjectCAList.size() > 0) {
            for (Object certificatoObjectCA : certificatoObjectCAList) {
                Object obj = (Object) certificatoObjectCA;
                certifCaName.add((BigDecimal) obj);
            }
        }
        return certifCaName;
    }

    // Metodi per la visualizzazione/ricerca su COMPONENTI
    public AroVVisCompRowBean getAroVVisCompRowBean(BigDecimal idcompdoc) throws EMFError {
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

    public AroVLisFirmaCompTableBean getAroVLisFirmaCompTableBean(BigDecimal idcompdoc, int maxResults)
            throws EMFError {
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

    public AroVLisMarcaCompTableBean getAroVLisMarcaCompTableBean(BigDecimal idcompdoc, int maxResults)
            throws EMFError {
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
    public AroVVisMarcaCompRowBean getAroVVisMarcaCompRowBean(BigDecimal idmarca) throws EMFError {
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

    public AroVLisCertifCaMarcaCompTableBean getAroVLisCertifCaMarcaCompTableBean(BigDecimal idmarca, String tiContr)
            throws EMFError {
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
    public AroVVisFirmaCompRowBean getAroVVisFirmaCompRowBean(BigDecimal idfirma) throws EMFError {
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

    public AroVLisCertifCaFirmaCompTableBean getAroVLisCertifCaFirmaCompTableBean(BigDecimal idfirma, String tiContr)
            throws EMFError {
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

    public AroVLisControfirmaFirmaTableBean getAroVLisControfirmaFirmaTableBean(BigDecimal idfirma) throws EMFError {
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

    public ElvVListaCompElvTableBean getElvVListaCompElvViewBean(BigDecimal idElencoVers,
            ElenchiVersamentoForm.ComponentiFiltri filtri, Date[] dateValidate,
            DecTipoUnitaDocTableBean tmpTableBeanTipoUD, DecTipoDocTableBean tmpTableBeanTipoDoc,
            OrgSubStrutTableBean tmpSubStrutsTableBean) throws EMFError {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT new it.eng.parer.viewEntity.ElvVListaCompElv"
                + "(u.idElencoVers, u.idCompDoc, "
                + "u.dsUrnCompCalc, u.dsUrnCompCalcShort, u.dsNomeCompVers, u.dsOrdComp, u.tiSupportoComp, "
                + "u.nmTipoStrutDoc, u.nmTipoCompDoc, u.nmFormatoFileDocVers, u.dtCreazioneDoc, u.niSizeFileCalc, "
                + "u.flCompFirmato, u.tiEsitoVerifFirmeVers, u.idUnitaDoc, u.idDoc, "
                + "u.cdRegistroKeyUnitaDoc, u.idTipoUnitaDoc, u.idTipoDoc, u.idSubStrut, u.tiStatoElencoVers, u.tiStatoConservazione) "
                + "FROM ElvVListaCompElv u WHERE u.idElencoVers = :idElencoVers ");

        // Inserimento nella query dei tipi unità documentaria abilitati
        Set<BigDecimal> idTipoUnitaDocSet = new HashSet<>();
        for (DecTipoUnitaDocRowBean row : tmpTableBeanTipoUD) {
            idTipoUnitaDocSet.add(row.getIdTipoUnitaDoc());
        }
        if (idTipoUnitaDocSet.isEmpty()) {
            idTipoUnitaDocSet.add(new BigDecimal("0"));
        }
        queryStr.append(whereWord).append("u.idTipoUnitaDoc IN :idtipounitadocin ");

        // Inserimento nella query dei tipi documento abilitati
        Set<BigDecimal> idTipoDocSet = new HashSet<>();
        for (DecTipoDocRowBean row : tmpTableBeanTipoDoc) {
            idTipoDocSet.add(row.getIdTipoDoc());
        }
        if (idTipoDocSet.isEmpty()) {
            idTipoDocSet.add(new BigDecimal("0"));
        }
        queryStr.append(whereWord).append("u.idTipoDoc IN :idtipodocin ");

        // Inserimento nella query delle sottostrutture abilitate
        Set<BigDecimal> idSubStrutSet = new HashSet<>();
        for (OrgSubStrutRowBean row : tmpSubStrutsTableBean) {
            idSubStrutSet.add(row.getIdSubStrut());
        }
        if (idTipoDocSet.isEmpty()) {
            idSubStrutSet.add(new BigDecimal("0"));
        }
        queryStr.append(whereWord).append("u.idSubStrut IN :idsubstrutin ");

        // Inserimento nella query del filtro CHIAVE DOCUMENTO
        String registro = filtri.getCd_registro_key_unita_doc().parse();
        BigDecimal anno = filtri.getAa_key_unita_doc().parse();
        String codice = filtri.getCd_key_unita_doc().parse();
        BigDecimal anno_range_da = filtri.getAa_key_unita_doc_da().parse();
        BigDecimal anno_range_a = filtri.getAa_key_unita_doc_a().parse();
        String codice_range_da = filtri.getCd_key_unita_doc_da().parse();
        String codice_range_a = filtri.getCd_key_unita_doc_a().parse();

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

        if (anno_range_da != null && anno_range_a != null) {
            queryStr.append(whereWord).append("(u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }

        if (codice_range_da != null && codice_range_a != null) {
            codice_range_da = StringPadding.padString(codice_range_da, "0", 12, StringPadding.PADDING_LEFT);
            codice_range_a = StringPadding.padString(codice_range_a, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord)
                    .append("FUNC('lpad', u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro DATA CREAZIONE DA - A
        Date data_da = (dateValidate != null ? dateValidate[0] : null);
        Date data_a = (dateValidate != null ? dateValidate[1] : null);

        if ((data_da != null) && (data_a != null)) {
            queryStr.append(whereWord).append("(u.dtCreazioneDoc between :datada AND :dataa) ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Tipo Struttura Documento
        BigDecimal tipoStrutDoc = filtri.getNm_tipo_strut_doc().parse();
        if (tipoStrutDoc != null) {
            queryStr.append(whereWord).append("u.idTipoStrutDoc = :tipostrutdocin ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Tipo Componente Documento
        BigDecimal tipoCompDoc = filtri.getNm_tipo_comp_doc().parse();
        if (tipoCompDoc != null) {
            queryStr.append(whereWord).append("u.idTipoCompDoc = :tipocompdocin ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Formato
        String formato = filtri.getNm_formato_file_vers().parse();
        if (formato != null) {
            queryStr.append(whereWord).append("u.nmFormatoFileDocVers = :formatoin ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro file size
        BigDecimal fileSizeDa = filtri.getNi_size_file_da().parse();
        BigDecimal fileSizeA = filtri.getNi_size_file_a().parse();
        if (fileSizeDa == null && fileSizeA != null) {
            fileSizeDa = BigDecimal.ZERO;
        }
        if (fileSizeDa != null && fileSizeA != null) {
            queryStr.append(whereWord).append("u.niSizeFileCalc between :filesizedain AND :filesizeain ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro PRESENZA FIRME
        String presenza = filtri.getFl_comp_firmato().parse();
        if (presenza != null) {
            queryStr.append(whereWord).append("u.flCompFirmato = :presenzain ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Conformita FIRME
        String conformita = filtri.getTi_esito_contr_conforme().parse();
        if (conformita != null) {
            queryStr.append(whereWord).append("u.tiEsitoContrConforme = :conformitain ");
            whereWord = "and ";
        }

        // Inserimento nella query del filtro Esito Firme
        String esitoFirme = filtri.getTi_esito_verif_firme_vers().parse();
        if (esitoFirme != null) {
            queryStr.append(whereWord).append("u.tiEsitoVerifFirmeVers = :esitofirmein ");
            whereWord = "and ";
        }

        Date data_val_da = null;
        Date data_val_a = null;

        // Inserimento nella query del filtro DATA SCADENZA FIRMA DA - A
        if (filtri.getDt_scad_firma_comp_da().parse() != null) {
            data_val_da = new Date(filtri.getDt_scad_firma_comp_da().parse().getTime());
            if (filtri.getDt_scad_firma_comp_a().parse() != null) {
                data_val_a = new Date(filtri.getDt_scad_firma_comp_a().parse().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(data_val_a);
                calendar.add(Calendar.DATE, 1);
                data_val_a = calendar.getTime();
            } else {
                data_val_a = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(data_val_a);
                calendar.add(Calendar.DATE, 1);
                data_val_a = calendar.getTime();
            }
        }

        if ((data_val_da != null) && (data_val_a != null)) {
            queryStr.append(whereWord).append("(u.dtScadCertifFirmatario between :datavalda AND :datavala) ");
            whereWord = "and ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_nome_comp_vers().parse())) {
            queryStr.append(whereWord).append("UPPER(u.dsNomeCompVers) LIKE :nomeCompVersIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_hash_file_vers().parse())) {
            queryStr.append(whereWord).append("u.dsHashFileVers = :hashFileVersIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getNm_mimetype_file().parse())) {
            queryStr.append(whereWord).append("UPPER(u.nmMimetypeFile) LIKE :mimetypeIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDl_urn_comp_vers().parse())) {
            queryStr.append(whereWord).append("UPPER(u.dlUrnCompVers) LIKE :urnCompVersIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_formato_rappr_calc().parse())) {
            queryStr.append(whereWord).append("u.dsFormatoRapprCalc = :formatoRapprCalcIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_formato_rappr_esteso_calc().parse())) {
            queryStr.append(whereWord).append("u.dsFormatoRapprEstesoCalc = :formatoRapprEstesoCalcIn ");
            whereWord = " AND ";
        }

        String forzaAcc = filtri.getFl_forza_accettazione().parse();
        if (StringUtils.isNotBlank(forzaAcc)) {
            queryStr.append(whereWord).append("u.flForzaAccettazione = :forzaaccin ");
            whereWord = " AND ";
        }

        String forzaConserva = filtri.getFl_forza_conservazione().parse();
        if (StringUtils.isNotBlank(forzaConserva)) {
            queryStr.append(whereWord).append("u.flForzaConservazione = :forzaconservain ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getTi_esito_contr_formato_file().parse())) {
            queryStr.append(whereWord).append("u.tiEsitoContrFormatoFile = :esitoContrFormatoFileIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_hash_file_calc().parse())) {
            queryStr.append(whereWord).append("u.dsHashFileCalc = :hashFileCalcIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_algo_hash_file_calc().parse())) {
            queryStr.append(whereWord).append("u.dsAlgoHashFileCalc = :algoHashFileCalcIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getCd_encoding_hash_file_calc().parse())) {
            queryStr.append(whereWord).append("u.cdEncodingHashFileCalc = :encodingHashFileCalcIn ");
            whereWord = " AND ";
        }

        if (StringUtils.isNotBlank(filtri.getDs_urn_comp_calc().parse())) {
            queryStr.append(whereWord).append("UPPER(u.dsUrnCompCalc) LIKE :urnCompCalcIn ");
            // whereWord = " AND ";
        }

        queryStr.append("ORDER BY u.dsOrdComp ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idElencoVers", idElencoVers);

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

        if (anno_range_da != null && anno_range_a != null) {
            query.setParameter("annoin_da", anno_range_da);
            query.setParameter("annoin_a", anno_range_a);
        }

        if (codice_range_da != null && codice_range_a != null) {
            query.setParameter("codicein_da", codice_range_da);
            query.setParameter("codicein_a", codice_range_a);
        }

        if (data_da != null && data_a != null) {
            query.setParameter("datada", data_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_a, TemporalType.TIMESTAMP);
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
        if (data_val_da != null && data_val_a != null) {
            query.setParameter("datavalda", data_val_da, TemporalType.DATE);
            query.setParameter("datavala", data_val_a, TemporalType.DATE);
        }

        if (StringUtils.isNotBlank(filtri.getDs_nome_comp_vers().parse())) {
            query.setParameter("nomeCompVersIn", "%" + filtri.getDs_nome_comp_vers().parse().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getDs_hash_file_vers().parse())) {
            query.setParameter("hashFileVersIn", filtri.getDs_hash_file_vers().parse());
        }

        if (StringUtils.isNotBlank(filtri.getNm_mimetype_file().parse())) {
            query.setParameter("mimetypeIn", "%" + filtri.getNm_mimetype_file().parse().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getDl_urn_comp_vers().parse())) {
            query.setParameter("urnCompVersIn", "%" + filtri.getDl_urn_comp_vers().parse().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getDs_formato_rappr_calc().parse())) {
            query.setParameter("formatoRapprCalcIn", filtri.getDs_formato_rappr_calc().parse());
        }

        if (StringUtils.isNotBlank(filtri.getDs_formato_rappr_esteso_calc().parse())) {
            query.setParameter("formatoRapprEstesoCalcIn", filtri.getDs_formato_rappr_esteso_calc().parse());
        }

        if (StringUtils.isNotBlank(forzaAcc)) {
            query.setParameter("forzaaccin", forzaAcc);
        }

        if (StringUtils.isNotBlank(forzaConserva)) {
            query.setParameter("forzaconservain", forzaConserva);
        }

        if (StringUtils.isNotBlank(filtri.getTi_esito_contr_formato_file().parse())) {
            query.setParameter("esitoContrFormatoFileIn", filtri.getTi_esito_contr_formato_file().parse());
        }

        if (StringUtils.isNotBlank(filtri.getDs_hash_file_calc().parse())) {
            query.setParameter("hashFileCalcIn", filtri.getDs_hash_file_calc().parse());
        }

        if (StringUtils.isNotBlank(filtri.getDs_algo_hash_file_calc().parse())) {
            query.setParameter("algoHashFileCalcIn", filtri.getDs_algo_hash_file_calc().parse());
        }

        if (StringUtils.isNotBlank(filtri.getCd_encoding_hash_file_calc().parse())) {
            query.setParameter("encodingHashFileCalcIn", filtri.getCd_encoding_hash_file_calc().parse());
        }

        if (StringUtils.isNotBlank(filtri.getDs_urn_comp_calc().parse())) {
            query.setParameter("urnCompCalcIn", "%" + filtri.getDs_urn_comp_calc().parse().toUpperCase() + "%");
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<ElvVListaCompElv> listaComponenti = query.getResultList();

        ElvVListaCompElvTableBean componentiTableBean = new ElvVListaCompElvTableBean();
        try {
            if (listaComponenti != null && !listaComponenti.isEmpty()) {
                componentiTableBean = (ElvVListaCompElvTableBean) Transform.entities2TableBean(listaComponenti);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // setta il campo relativo alla checkbox select_comp non ceccato
        for (int i = 0; i < componentiTableBean.size(); i++) {
            ElvVListaCompElvRowBean row = componentiTableBean.getRow(i);
            row.setString("select_comp", "0");
        }

        return componentiTableBean;
    }

    public boolean isComponenteInElenco(BigDecimal idCompDoc) throws EMFError {
        String queryStr = "SELECT COUNT(u) FROM ElvVListaCompElv u " + "WHERE u.idCompDoc = :idCompDoc";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idCompDoc", idCompDoc.longValue());
        Long conta = (Long) query.getSingleResult();
        return conta > 0;
    }

    public boolean isComponenteInVolume(BigDecimal idCompDoc) throws EMFError {
        String queryStr = "SELECT COUNT(u) FROM VolVListaCompVol u " + "WHERE u.idCompDoc = :idCompDoc";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idCompDoc", idCompDoc.longValue());
        Long conta = (Long) query.getSingleResult();
        return conta > 0;
    }

    // Metodi per la visualizzazione/ricerca delle informazioni su volumi riferite al componente
    public AroVVisCompVolRowBean getAroVVisCompVolRowBean(BigDecimal idCompDoc) throws EMFError {
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
        List<AroCompDoc> list = query.getResultList();
        return list;
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
