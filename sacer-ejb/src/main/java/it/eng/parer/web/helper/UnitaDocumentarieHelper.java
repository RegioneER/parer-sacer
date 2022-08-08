package it.eng.parer.web.helper;

import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompUrnCalc;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroFileVerIndiceAipUd;
import it.eng.parer.entity.AroNotaUnitaDoc;
import it.eng.parer.entity.AroRichAnnulVers;
import it.eng.parer.entity.AroStrutDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.AroUrnVerIndiceAipUd;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.AroWarnUpdUnitaDoc;
import it.eng.parer.entity.DecAttribDatiSpec;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoNotaUnitaDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.VrsXmlModelloSessioneVers;
import it.eng.parer.entity.constraint.AroCompUrnCalc.TiUrn;
import it.eng.parer.entity.constraint.AroUrnVerIndiceAipUd.TiUrnVerIxAipUd;
import it.eng.parer.entity.constraint.DecModelloXsdUd.TiModelloXsdUd;
import it.eng.parer.entity.constraint.SIOrgEnteSiam.TiEnteConvenz;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.grantedEntity.UsrUser;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice;
import it.eng.parer.slite.gen.tablebean.AroFileVerIndiceAipUdRowBean;
import it.eng.parer.slite.gen.tablebean.AroUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.AroUpdUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.AroVerIndiceAipUdRowBean;
import it.eng.parer.slite.gen.tablebean.AroVerIndiceAipUdTableBean;
import it.eng.parer.slite.gen.tablebean.AroWarnUpdUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.AroWarnUpdUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoNotaUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoNotaUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisArchivUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisCompDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisDatiSpecDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisDatiSpecTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisElvVerTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisFascTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisLinkUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisNotaUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdCompUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdDocUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdKoRisoltiTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisVolCorTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisVolNoValDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVVisDocIamRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisDocIamTableBean;
import it.eng.parer.slite.gen.viewbean.AroVVisNotaUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisUnitaDocIamRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisUpdUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisUpdUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVLisUpdUdRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVLisUpdUdTableBean;
import it.eng.parer.util.Utils;
import it.eng.parer.viewEntity.AroVDtVersMaxByUnitaDoc;
import it.eng.parer.viewEntity.AroVLisArchivUnitaDoc;
import it.eng.parer.viewEntity.AroVLisCompDoc;
import it.eng.parer.viewEntity.AroVLisDatiSpecDoc;
import it.eng.parer.viewEntity.AroVLisDoc;
import it.eng.parer.viewEntity.AroVLisElvVer;
import it.eng.parer.viewEntity.AroVLisFasc;
import it.eng.parer.viewEntity.AroVLisLinkUnitaDoc;
import it.eng.parer.viewEntity.AroVLisNotaUnitaDoc;
import it.eng.parer.viewEntity.AroVLisVolCor;
import it.eng.parer.viewEntity.AroVLisVolNoValDoc;
import it.eng.parer.viewEntity.AroVRicUnitaDoc;
import it.eng.parer.viewEntity.AroVVisDocIam;
import it.eng.parer.viewEntity.AroVVisUnitaDocIam;
import it.eng.parer.viewEntity.AroVVisUpdUnitaDoc;
import it.eng.parer.viewEntity.AroVLisUpdDocUnitaDoc;
import it.eng.parer.viewEntity.AroVLisUpdCompUnitaDoc;
import it.eng.parer.viewEntity.AroVLisUpdKoRisolti;
import it.eng.parer.viewEntity.ElvVLisUpdUd;
import it.eng.parer.viewEntity.AroVVisNotaUnitaDoc;
import it.eng.parer.volume.helper.VolumeHelper;
import it.eng.parer.volume.utils.DatiSpecQueryParams;
import it.eng.parer.volume.utils.ReturnParams;
import it.eng.parer.volume.utils.VolumeEnums.DocStatusEnum;
import it.eng.parer.web.dto.DecCriterioDatiSpecBean;
import it.eng.parer.web.dto.SerieAppartenenzaRowBean;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Constants.TipoEntitaSacer;
import it.eng.parer.web.util.StringPadding;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipoAplVGetValAppart;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.SingleValueField;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class unitaDocumetarieHelper Contiene i metodi (implementati di
 * unitaDocumentarieHelperLocal), per la gestione della persistenza su DB per le operarazioni CRUD su oggetti di
 * UnitaDocumentarieTableBean ed UnitaDocumentarieRowBean
 */
@Stateless
@LocalBean
public class UnitaDocumentarieHelper extends GenericHelper {

    private static final Logger log = LoggerFactory.getLogger(UnitaDocumentarieHelper.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/VolumeHelper")
    private VolumeHelper volumeHelper;
    @EJB
    private UnitaDocumentarieHelper me;
    @EJB
    private ConfigurationHelper configurationHelper;

    // Metodo che restituisce un viewbean con il record trovato in base
    // ai parametri
    public AroVRicUnitaDocRowBean getAroVRicUnitaDocRowBean(BigDecimal idUnitaDoc, BigDecimal idStruttura,
            String statoDoc) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM AroVRicUnitaDoc u ");
        if (idUnitaDoc != null) {
            queryStr.append(whereWord).append("u.idUnitaDoc = :idud");
            whereWord = " AND ";
        }
        if (idStruttura != null) {
            queryStr.append(whereWord).append("u.idStrutUnitaDoc = :idstrutud");
            whereWord = " AND ";
        }
        if (statoDoc != null) {
            queryStr.append(whereWord).append("u.tiStatoDoc = :statodoc");
            whereWord = " AND ";
        }

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr.toString());
        // Passo i parametri di ricerca
        if (idUnitaDoc != null) {
            query.setParameter("idud", idUnitaDoc);
        }
        if (idStruttura != null) {
            query.setParameter("idstrutud", idStruttura);
        }
        if (statoDoc != null) {
            query.setParameter("statodoc", statoDoc);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVRicUnitaDoc> listaUD = query.getResultList();

        AroVRicUnitaDocRowBean rowBean = null;

        try {
            if (listaUD != null && !listaUD.isEmpty()) {
                rowBean = (AroVRicUnitaDocRowBean) Transform.entity2RowBean(listaUD.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return rowBean;
    }

    /*
     * Metodo che wrappa quello seguente in quanto bypassa l'intercettazione fatta nell'ejb-jar.xml per il metodo
     * getAroVRicUnitaDocRicSempliceViewBean() che viene limitato ad un numero massimo di records.
     */
    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicSempliceViewBeanNoLimit(FiltriUnitaDocumentarieSemplice filtri,
            List<BigDecimal> idTipoUnitaDocList, List<String> cdRegistroUnitaDocList, List<BigDecimal> idTipoDocList,
            Date[] dateAcquisizioneValidate, Date[] dateUnitaDocValidate, BigDecimal idStruttura, BigDecimal idUser)
            throws EMFError {

        return getAroVRicUnitaDocRicSempliceViewBean(filtri, idTipoUnitaDocList, cdRegistroUnitaDocList, idTipoDocList,
                dateAcquisizioneValidate, dateUnitaDocValidate, idStruttura, idUser, -1);
    }

    // Metodo che restituisce un viewbean con i record trovati in base
    // ai filtri di ricerca passati in ingresso
    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicSempliceViewBean(FiltriUnitaDocumentarieSemplice filtri,
            List<BigDecimal> idTipoUnitaDocList, List<String> cdRegistroUnitaDocList, List<BigDecimal> idTipoDocList,
            Date[] dateAcquisizioneValidate, Date[] dateUnitaDocValidate, BigDecimal idStruttura, BigDecimal idUser,
            int maxResults) throws EMFError {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT new it.eng.parer.viewEntity.AroVRicUnitaDoc "
                + "(u.idUnitaDoc, u.aaKeyUnitaDoc, u.cdKeyUnitaDoc, u.cdRegistroKeyUnitaDoc, u.dtCreazione, u.dtRegUnitaDoc, "
                + "u.flUnitaDocFirmato, u.tiEsitoVerifFirme, u.dsMsgEsitoVerifFirme, u.nmTipoUnitaDoc, u.flForzaAccettazione, "
                + "u.flForzaConservazione, u.dsKeyOrd, u.niAlleg, u.niAnnessi, u.niAnnot, u.nmTipoDocPrinc, u.dsListaStatiElencoVers, u.tiStatoConservazione) FROM AroVRicUnitaDoc u ");

        // Inserimento nella query del filtro Tipo Unità Doc
        if (idTipoUnitaDocList.size() > 0) {
            queryStr.append(whereWord).append("u.idTipoUnitaDoc IN :tipoudin ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro CD_VERSIONE_XSD_UD
        String cdVersioneXsdUd = filtri.getCd_versione_xsd_ud().parse();
        if (cdVersioneXsdUd != null) {
            queryStr.append(whereWord).append("u.cdVersioneXsdUd = :cdVersioneXsdUd ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro Registro
        if (cdRegistroUnitaDocList.size() > 0) {
            queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc IN :registrokeyunitadocin ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro Tipo Doc
        if (idTipoDocList.size() > 0) {
            queryStr.append(whereWord).append("u.idTipoDoc IN :tipodocin ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro CD_VERSIONE_XSD_DOC
        String cdVersioneXsdDoc = filtri.getCd_versione_xsd_doc().parse();
        if (cdVersioneXsdDoc != null) {
            queryStr.append(whereWord).append("u.cdVersioneXsdDoc = :cdVersioneXsdDoc ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro CHIAVE UNITA DOC
        // String registro = filtri.getCd_registro_key_unita_doc().getDecodedValue();
        BigDecimal anno = filtri.getAa_key_unita_doc().parse();
        String codice = filtri.getCd_key_unita_doc().parse();

        if (anno != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :annoin ");
            whereWord = "AND ";
        }

        if (codice != null) {
            queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :codicein ");
            whereWord = "AND ";
        }

        String cdKeyDocVers = filtri.getCd_key_doc_vers().parse();
        if (cdKeyDocVers != null) {
            queryStr.append(whereWord).append("UPPER(u.cdKeyDocVers) LIKE :cdKeyDocVers ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro CHIAVE UNITA DOC PER RANGE
        BigDecimal anno_range_da = filtri.getAa_key_unita_doc_da().parse();
        BigDecimal anno_range_a = filtri.getAa_key_unita_doc_a().parse();
        String codice_range_da = filtri.getCd_key_unita_doc_da().parse();
        String codice_range_a = filtri.getCd_key_unita_doc_a().parse();

        if (anno_range_da != null && anno_range_a != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a ");
            whereWord = "AND ";
        }

        if (codice_range_da != null && codice_range_a != null) {
            codice_range_da = StringPadding.padString(codice_range_da, "0", 12, StringPadding.PADDING_LEFT);
            codice_range_a = StringPadding.padString(codice_range_a, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord)
                    .append("FUNC('lpad', u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = "AND ";
        }

        String codice_num_contiene = filtri.getCd_key_unita_doc_contiene().parse();

        if (codice_num_contiene != null) {
            queryStr.append(whereWord).append("UPPER(u.cdKeyUnitaDoc) LIKE :codicein_contiene ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro PRESENZA FIRME
        String presenza = filtri.getFl_unita_doc_firmato().parse();
        if (presenza != null) {
            queryStr.append(whereWord).append("u.flUnitaDocFirmato = :presenzain ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro ESITO FIRME VERS
        String esito = filtri.getTi_esito_verif_firme().parse();
        if (esito != null) {
            queryStr.append(whereWord).append("u.tiEsitoVerifFirme = :esitoin ");
            whereWord = "AND ";
        }

        Date data_da = (dateAcquisizioneValidate != null ? dateAcquisizioneValidate[0] : null);
        Date data_a = (dateAcquisizioneValidate != null ? dateAcquisizioneValidate[1] : null);

        if ((data_da != null) && (data_a != null)) {
            queryStr.append(whereWord).append("(u.dtCreazione >= :datada AND u.dtCreazione <= :dataa) ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro ESISTE PROFILO NORMATIVO
        String profiloNorm = filtri.getFl_profilo_normativo().parse();
        if (profiloNorm != null) {
            queryStr.append(whereWord).append("u.flEsisteProfiloNormativo = :profiloNorm ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro FORZA ACCETTAZIONE
        String forzaAcc = filtri.getFl_forza_accettazione().parse();
        if (forzaAcc != null) {
            queryStr.append(whereWord).append("u.flForzaAccettazione = :forzaaccin ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro FORZA CONSERVAZIONE
        String forzaConserva = filtri.getFl_forza_conservazione().parse();
        if (forzaConserva != null) {
            queryStr.append(whereWord).append("u.flForzaConservazione = :forzaconservain ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro FORZA COLLEGAMENTO
        String forzaColleg = filtri.getFl_forza_collegamento().parse();
        if (forzaColleg != null) {
            queryStr.append(whereWord).append("u.flForzaCollegamento = :forzacollegin ");
            whereWord = "AND ";
        }

        String unitaDocAnnul = filtri.getFl_unita_doc_annul().parse();
        if (unitaDocAnnul != null) {
            queryStr.append(whereWord).append("u.flUnitaDocAnnul = :unitaDocAnnul ");
            whereWord = "AND ";
        }
        String docAggiunti = filtri.getFl_doc_aggiunti().parse();
        if (docAggiunti != null) {
            queryStr.append(whereWord).append("u.flDocAggiunti = :docAggiunti ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro FL_HASH_VERS
        String flHashVers = filtri.getFl_hash_vers().parse();
        if (flHashVers != null) {
            queryStr.append(whereWord).append("u.flHashVers = :flHashVers ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro STATO CONSERVAZIONE
        String statoConserva = filtri.getTi_stato_conservazione().parse();
        if (statoConserva != null) {
            queryStr.append(whereWord).append("u.tiStatoConservazione = :statoconservain ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro TI_STATO_UD_ELENCO_VERS
        String tiStatoUdElencoVers = filtri.getTi_stato_ud_elenco_vers().parse();
        if (tiStatoUdElencoVers != null) {
            queryStr.append(whereWord).append(
                    "(u.tiStatoUdElencoVers = :tiStatoUdElencoVers OR u.tiStatoDocElencoVers = :tiStatoUdElencoVers) ");
            whereWord = "AND ";
        }

        Date data_meta_da = (dateUnitaDocValidate != null ? dateUnitaDocValidate[0] : null);
        Date data_meta_a = (dateUnitaDocValidate != null ? dateUnitaDocValidate[1] : null);

        if ((data_meta_da != null) && (data_meta_a != null)) {
            queryStr.append(whereWord).append("(u.dtRegUnitaDoc between :datametada AND :datametaa) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro TI_DOC ("Elemento")
        String tiDoc = filtri.getTi_doc().parse();
        if (tiDoc != null) {
            queryStr.append(whereWord).append("u.tiDoc = :tiDoc ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro metadati Oggetto
        String oggettoMeta = filtri.getDl_oggetto_unita_doc().parse();
        if (oggettoMeta != null) {
            queryStr.append(whereWord).append("UPPER(u.dlOggettoUnitaDoc) LIKE :dloggettounitadocin ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro metadati Autore documento
        String autoreDocMeta = filtri.getDs_autore_doc().parse();
        if (autoreDocMeta != null) {
            queryStr.append(whereWord).append("UPPER(u.dsAutoreDoc) LIKE :dldocmetain ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro metadati Descrizione documento
        String descrizioneDocMeta = filtri.getDl_doc().parse();
        if (descrizioneDocMeta != null) {
            queryStr.append(whereWord).append("UPPER(u.dlDoc) LIKE :dsautoredocin ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro Tipo Conservazione
        String tipoConservazione = filtri.getTi_conservazione().parse();
        if (tipoConservazione != null) {
            queryStr.append(whereWord).append("u.tiConservazione = :ticonservazionein ");
            whereWord = "AND ";
        }

        queryStr.append(whereWord).append("u.idStrutUnitaDoc = :idstrutin ");
        whereWord = "AND ";

        List<BigDecimal> subStruts = filtri.getNm_sub_strut().parse();
        if (!subStruts.isEmpty()) {
            queryStr.append(whereWord).append(" u.idSubStrut IN :subStruts ");
            whereWord = "AND ";
        }

        // ordina per dsKeyDoc crescente
        queryStr.append("ORDER BY u.dsKeyOrd");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr.toString());

        // non avendo passato alla query i parametri di ricerca, devo passarli ora
        if (idTipoUnitaDocList.size() > 0) {
            query.setParameter("tipoudin", idTipoUnitaDocList);
        }

        if (cdVersioneXsdUd != null) {
            query.setParameter("cdVersioneXsdUd", cdVersioneXsdUd);
        }

        if (cdRegistroUnitaDocList.size() > 0) {
            query.setParameter("registrokeyunitadocin", cdRegistroUnitaDocList);
        }

        if (anno != null) {
            query.setParameter("annoin", anno);
        }

        if (codice != null) {
            query.setParameter("codicein", codice);
        }

        if (cdKeyDocVers != null) {
            query.setParameter("cdKeyDocVers", "%" + cdKeyDocVers.toUpperCase() + "%");
        }

        if (anno_range_da != null && anno_range_a != null) {
            query.setParameter("annoin_da", anno_range_da);
            query.setParameter("annoin_a", anno_range_a);
        }

        if (codice_range_da != null && codice_range_a != null) {
            query.setParameter("codicein_da", codice_range_da);
            query.setParameter("codicein_a", codice_range_a);
        }

        if (codice_num_contiene != null) {
            query.setParameter("codicein_contiene", "%" + codice_num_contiene + "%");
        }

        if (presenza != null) {
            query.setParameter("presenzain", presenza);
        }

        if (esito != null) {
            query.setParameter("esitoin", esito);
        }

        if (data_da != null && data_a != null) {
            query.setParameter("datada", data_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_a, TemporalType.TIMESTAMP);
        }

        if (profiloNorm != null) {
            query.setParameter("profiloNorm", profiloNorm);
        }

        if (forzaAcc != null) {
            query.setParameter("forzaaccin", forzaAcc);
        }

        if (forzaConserva != null) {
            query.setParameter("forzaconservain", forzaConserva);
        }

        if (forzaColleg != null) {
            query.setParameter("forzacollegin", forzaColleg);
        }

        if (unitaDocAnnul != null) {
            query.setParameter("unitaDocAnnul", unitaDocAnnul);
        }

        if (docAggiunti != null) {
            query.setParameter("docAggiunti", docAggiunti);
        }

        if (flHashVers != null) {
            query.setParameter("flHashVers", flHashVers);
        }

        if (statoConserva != null) {
            query.setParameter("statoconservain", statoConserva);
        }

        if (tiStatoUdElencoVers != null) {
            query.setParameter("tiStatoUdElencoVers", tiStatoUdElencoVers);
        }

        if (idTipoDocList.size() > 0) {
            query.setParameter("tipodocin", idTipoDocList);
        }

        if (cdVersioneXsdDoc != null) {
            query.setParameter("cdVersioneXsdDoc", cdVersioneXsdDoc);
        }

        if (data_meta_da != null && data_meta_a != null) {
            query.setParameter("datametada", data_meta_da, TemporalType.DATE);
            query.setParameter("datametaa", data_meta_a, TemporalType.DATE);
        }

        if (tiDoc != null) {
            query.setParameter("tiDoc", tiDoc);
        }

        if (oggettoMeta != null) {
            query.setParameter("dloggettounitadocin", "%" + oggettoMeta.toUpperCase() + "%");
        }

        if (autoreDocMeta != null) {
            query.setParameter("dldocmetain", "%" + autoreDocMeta.toUpperCase() + "%");
        }

        if (descrizioneDocMeta != null) {
            query.setParameter("dsautoredocin", "%" + descrizioneDocMeta.toUpperCase() + "%");
        }

        if (idStruttura != null) {
            query.setParameter("idstrutin", idStruttura);
        }

        if (tipoConservazione != null) {
            query.setParameter("ticonservazionein", tipoConservazione);
        }

        if (!subStruts.isEmpty()) {
            query.setParameter("subStruts", subStruts);
        }

        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "UNITA' DOCUMENTARIE"
        List<AroVRicUnitaDoc> listaUD = query.getResultList();

        AroVRicUnitaDocTableBean udTableBean = new AroVRicUnitaDocTableBean();

        try {
            if (listaUD != null && !listaUD.isEmpty()) {
                udTableBean = (AroVRicUnitaDocTableBean) Transform.entities2TableBean(listaUD);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return udTableBean;
    }

    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicAvanzataViewBeanNoLimit(FiltriUnitaDocumentarieAvanzata filtri,
            List<BigDecimal> idTipoUnitaDocList, Set<String> cdRegistroUnitaDocSet, List<BigDecimal> idTipoDocList,
            List<DecCriterioDatiSpecBean> listaDatiSpecOnLine,
            UnitaDocumentarieForm.FiltriCollegamentiUnitaDocumentarie filtriCollegamenti,
            UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie filtriComponenti,
            UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie filtriFascicoli, Date[] dateAcquisizioneValidate,
            Date[] dateUnitaDocValidate, Date[] dateCreazioneCompValidate, BigDecimal idStruttura, BigDecimal idUser,
            boolean addButton) throws EMFError {
        return getAroVRicUnitaDocRicAvanzataViewBean(filtri, idTipoUnitaDocList, cdRegistroUnitaDocSet, idTipoDocList,
                listaDatiSpecOnLine, filtriCollegamenti, filtriComponenti, filtriFascicoli, dateAcquisizioneValidate,
                dateUnitaDocValidate, dateCreazioneCompValidate, idStruttura, idUser, addButton);
    }

    // Metodo che restituisce un viewbean con i record trovati in base
    // ai filtri di ricerca passati in ingresso
    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicAvanzataViewBean(FiltriUnitaDocumentarieAvanzata filtri,
            List<BigDecimal> idTipoUnitaDocList, Set<String> cdRegistroUnitaDocSet, List<BigDecimal> idTipoDocList,
            List<DecCriterioDatiSpecBean> listaDatiSpecOnLine,
            UnitaDocumentarieForm.FiltriCollegamentiUnitaDocumentarie filtriCollegamenti,
            UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie filtriComponenti,
            UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie filtriFascicoli, Date[] dateAcquisizioneValidate,
            Date[] dateUnitaDocValidate, Date[] dateCreazioneCompValidate, BigDecimal idStruttura, BigDecimal idUser,
            boolean addButton) throws EMFError {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT new it.eng.parer.viewEntity.AroVRicUnitaDoc "
                + "(u.idUnitaDoc, u.aaKeyUnitaDoc, u.cdKeyUnitaDoc, u.cdRegistroKeyUnitaDoc,"
                + " u.dtCreazione, u.dtRegUnitaDoc, u.flUnitaDocFirmato, u.tiEsitoVerifFirme,"
                + " u.dsMsgEsitoVerifFirme, u.nmTipoUnitaDoc, u.flForzaAccettazione,"
                + " u.flForzaConservazione, u.dsKeyOrd,"
                + " u.niAlleg, u.niAnnessi, u.niAnnot, u.nmTipoDocPrinc, u.dsListaStatiElencoVers, u.tiStatoConservazione)"
                + " FROM AroVRicUnitaDoc u ");

        // GESTIONE FILTRI COMPONENTI
        // Da eseguire prima di tutto per la costruzione dei join
        boolean joinFormato = false;
        boolean joinAppart = false;
        boolean joinFirma = false;
        boolean joinCertif = false;
        StringBuilder whereCompClause = new StringBuilder();
        if (filtriComponenti.getNm_tipo_strut_doc().parse() != null) {
            whereCompClause.append(" AND strutDoc.decTipoStrutDoc.idTipoStrutDoc = :tipoStrutDocIn");
        }
        if (filtriComponenti.getNm_tipo_comp_doc().parse() != null) {
            whereCompClause.append(" AND comp.decTipoCompDoc.idTipoCompDoc = :tipoCompDocIn");
        }
        if (filtriComponenti.getDs_nome_comp_vers().parse() != null) {
            whereCompClause.append(" AND UPPER(comp.dsNomeCompVers) LIKE :nomeCompVersIn");
        }
        if (filtriComponenti.getDl_urn_comp_vers().parse() != null) {
            whereCompClause.append(" AND UPPER(comp.dlUrnCompVers) LIKE :urnCompVersIn");
        }
        if (filtriComponenti.getDs_hash_file_vers().parse() != null) {
            whereCompClause.append(" AND comp.dsHashFileVers = :hashFileVersIn");
        }
        if (filtriComponenti.getNm_mimetype_file().parse() != null) {
            joinFormato = true;
            whereCompClause.append(" AND UPPER(fileStandard.nmMimetypeFile) LIKE :mimeTypeIn");
        }
        if (filtriComponenti.getNm_formato_file_vers().parse() != null) {
            whereCompClause.append(" AND comp.decFormatoFileDoc.idFormatoFileDoc = :formatoFileDocIn");
        }

        // Inserimento nella query del filtro file size
        BigDecimal fileSizeDa = filtriComponenti.getNi_size_file_da().parse();
        BigDecimal fileSizeA = filtriComponenti.getNi_size_file_a().parse();
        if (fileSizeDa == null && fileSizeA != null) {
            fileSizeDa = BigDecimal.ZERO;
        }
        if (fileSizeDa != null && fileSizeA != null) {
            whereCompClause.append(" AND comp.niSizeFileCalc between :filesizedain AND :filesizeain ");
        }
        if (filtriComponenti.getDs_formato_rappr_calc().parse() != null) {
            whereCompClause.append(" AND comp.dsFormatoRapprCalc = :formatoRapprCalcIn");
        }
        if (filtriComponenti.getDs_formato_rappr_esteso_calc().parse() != null) {
            whereCompClause.append(" AND comp.dsFormatoRapprEstesoCalc = :formatoRapprCalcEstesoIn");
        }

        if (filtriComponenti.getFl_comp_firmato().parse() != null) {
            if (filtriComponenti.getFl_comp_firmato().parse().equals("1")) {
                whereCompClause.append(" AND comp.flCompFirmato = :compFirmatoIn ");
            } else {
                whereCompClause.append(
                        " AND NOT EXISTS (SELECT comp2 FROM AroCompDoc comp2 JOIN comp2.aroStrutDoc strutDoc2 WHERE strutDoc2.aroDoc.aroUnitaDoc.idUnitaDoc = u.idUnitaDoc AND comp2.flCompFirmato = :compFirmatoIn ) ");
            }
        }

        if (filtriComponenti.getFl_rif_temp_vers().parse() != null) {
            if (filtriComponenti.getFl_rif_temp_vers().parse().equals("1")) {
                whereCompClause.append(" AND comp.tmRifTempVers IS NOT NULL ");
            } else {
                whereCompClause.append(
                        " AND NOT EXISTS (SELECT comp3 FROM AroCompDoc comp3 JOIN comp3.aroStrutDoc strutDoc3 WHERE strutDoc3.aroDoc.aroUnitaDoc.idUnitaDoc = u.idUnitaDoc AND comp3.tmRifTempVers IS NOT NULL ) ");
            }
        }
        if (filtriComponenti.getDs_rif_temp_vers().parse() != null) {
            whereCompClause.append(" AND UPPER(comp.dsRifTempVers) LIKE :dsRifTempVers ");
        }

        if (filtriComponenti.getTi_esito_contr_conforme().parse() != null) {
            joinFirma = true;
            whereCompClause.append(" AND firma.tiEsitoContrConforme = :esitoContrConformeIn");
        }

        Date data_val_da = null;
        Date data_val_a = null;
        // Inserimento nella query del filtro DATA SCADENZA DA - A
        if (filtriComponenti.getDt_scad_firma_comp_da().parse() != null) {
            data_val_da = new Date(filtriComponenti.getDt_scad_firma_comp_da().parse().getTime());
            if (filtriComponenti.getDt_scad_firma_comp_a().parse() != null) {
                data_val_a = new Date(filtriComponenti.getDt_scad_firma_comp_a().parse().getTime());
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
            joinFirma = true;
            joinCertif = true;
            whereCompClause.append(" AND certif.dtFinValCertifFirmatario between :datavalda AND :datavala ");
        }

        if (filtriComponenti.getTi_esito_contr_formato_file().parse() != null) {
            whereCompClause.append(" AND comp.tiEsitoContrFormatoFile = :esitoContrFormatoFileIn");
        }
        if (filtriComponenti.getTi_esito_verif_firma().parse() != null) {
            whereCompClause.append(" AND firma.tiEsitoVerifFirma = :esitoVerifFirmeIn");
            joinFirma = true;
        }
        if (filtriComponenti.getTi_esito_verif_firme_chius().parse() != null) {
            whereCompClause.append(" AND appartComp.tiEsitoVerifFirmeChius = :esitoVerifFirmeChiusIn");
            joinAppart = true;
        }
        if (filtriComponenti.getDs_hash_file_calc().parse() != null) {
            whereCompClause.append(" AND comp.dsHashFileCalc = :hashFileCalcIn");
        }
        if (filtriComponenti.getDs_algo_hash_file_calc().parse() != null) {
            whereCompClause.append(" AND comp.dsAlgoHashFileCalc = :algoHashFileCalcIn");
        }
        if (filtriComponenti.getCd_encoding_hash_file_calc().parse() != null) {
            whereCompClause.append(" AND comp.cdEncodingHashFileCalc = :encodingHashFileCalcIn");
        }
        if (filtriComponenti.getDs_urn_comp_calc().parse() != null) {
            whereCompClause.append(" AND UPPER(comp.dsUrnCompCalc) LIKE :urnCompCalcIn");
        }

        Date data_comp_da = (dateCreazioneCompValidate != null ? dateCreazioneCompValidate[0] : null);
        Date data_comp_a = (dateCreazioneCompValidate != null ? dateCreazioneCompValidate[1] : null);

        if ((data_comp_da != null) && (data_comp_a != null)) {
            whereCompClause.append(" AND strutDoc.aroDoc.dtCreazione between :dataCompDaIn AND :dataCompAIn");
        }
        //
        if (filtriComponenti.getFl_forza_accettazione_comp().parse() != null) {
            whereCompClause.append(" AND strutDoc.aroDoc.flForzaAccettazione = :flForzaAccIn");
        }

        if (filtriComponenti.getFl_forza_conservazione_comp().parse() != null) {
            whereCompClause.append(" AND strutDoc.aroDoc.flForzaConservazione = :flForzaConsIn");
        }

        if (filtriComponenti.getTi_supporto_comp().parse() != null) {
            whereCompClause.append(" AND comp.tiSupportoComp = :tiSupportoCompIn");
        }

        if (filtriComponenti.getTi_supporto_comp().parse() != null) {
            whereCompClause.append(" AND comp.tiSupportoComp = :tiSupportoCompIn");
        }

        if (filtriComponenti.getNm_tipo_rappr_comp().parse() != null) {
            whereCompClause.append(" AND comp.decTipoRapprComp.idTipoRapprComp = :idTipoRapprCompIn");
        }

        if (filtriComponenti.getDs_id_comp_vers().parse() != null) {
            whereCompClause.append(" AND UPPER(comp.dsIdCompVers) LIKE :idCompDocIn");
        }

        if (StringUtils.isNotBlank(whereCompClause.toString())) {
            queryStr.append(appendJoin(joinFormato, joinFirma, joinCertif, joinAppart));
            queryStr.append(whereCompClause.toString());
            whereWord = " AND ";
        }
        // FINE GESTIONE FILTRI COMPONENTI

        // Inserimento nella query del filtro Tipo Unità Doc versione multiselect
        if (!idTipoUnitaDocList.isEmpty()) {
            queryStr.append(whereWord).append("(u.idTipoUnitaDoc IN :listtipoud)");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro CD_VERSIONE_XSD_UD
        String cdVersioneXsdUd = filtri.getCd_versione_xsd_ud().parse();
        if (cdVersioneXsdUd != null) {
            queryStr.append(whereWord).append("u.cdVersioneXsdUd = :cdVersioneXsdUd ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro Registro
        if (!cdRegistroUnitaDocSet.isEmpty()) {
            queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc IN :setregistro ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro Tipo Documento
        if (!idTipoDocList.isEmpty()) {
            queryStr.append(whereWord).append("u.idTipoDoc IN :listtipodoc ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro CD_VERSIONE_XSD_DOC
        String cdVersioneXsdDoc = filtri.getCd_versione_xsd_doc().parse();
        if (cdVersioneXsdDoc != null) {
            queryStr.append(whereWord).append("u.cdVersioneXsdDoc = :cdVersioneXsdDoc ");
            whereWord = "AND ";
        }

        BigDecimal anno = filtri.getAa_key_unita_doc().parse();
        String codice = filtri.getCd_key_unita_doc().parse();

        if (anno != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :annoin ");
            whereWord = " AND ";
        }

        if (codice != null) {
            queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :codicein ");
            whereWord = " AND ";
        }

        String cdKeyDocVers = filtri.getCd_key_doc_vers().parse();
        if (cdKeyDocVers != null) {
            queryStr.append(whereWord).append("UPPER(u.cdKeyDocVers) LIKE :cdKeyDocVers ");
            whereWord = "AND ";
        }

        BigDecimal anno_range_da = filtri.getAa_key_unita_doc_da().parse();
        BigDecimal anno_range_a = filtri.getAa_key_unita_doc_a().parse();
        String codice_range_da = filtri.getCd_key_unita_doc_da().parse();
        String codice_range_a = filtri.getCd_key_unita_doc_a().parse();

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

        String codice_num_contiene = filtri.getCd_key_unita_doc_contiene().parse();

        if (codice_num_contiene != null) {
            queryStr.append(whereWord).append("UPPER(u.cdKeyUnitaDoc) LIKE :codicein_contiene ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro PRESENZA FIRME
        String presenza = filtri.getFl_unita_doc_firmato().parse();
        if (presenza != null) {
            queryStr.append(whereWord).append("u.flUnitaDocFirmato = :presenzain ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro ESITO FIRME VERS in versione multiselect
        List<String> esitoveriffirmeList = filtri.getTi_esito_verif_firme().parse();
        if (!esitoveriffirmeList.isEmpty()) {
            queryStr.append(whereWord).append("(u.tiEsitoVerifFirme IN :listaesitoveriffirme)");
            whereWord = " AND ";
        }

        Date data_da = (dateAcquisizioneValidate != null ? dateAcquisizioneValidate[0] : null);
        Date data_a = (dateAcquisizioneValidate != null ? dateAcquisizioneValidate[1] : null);

        if ((data_da != null) && (data_a != null)) {
            queryStr.append(whereWord).append("(u.dtCreazione between :datada AND :dataa) ");
            whereWord = " AND ";
        }
        // Inserimento nella query del filtro ESISTE PROFILO NORMATIVO
        String profiloNorm = filtri.getFl_profilo_normativo().parse();
        if (profiloNorm != null) {
            queryStr.append(whereWord).append("u.flEsisteProfiloNormativo = :profiloNorm ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro FORZA ACCETTAZIONE
        String forzaAcc = filtri.getFl_forza_accettazione().parse();
        if (forzaAcc != null) {
            queryStr.append(whereWord).append("u.flForzaAccettazione = :forzaaccin ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro FORZA CONSERVAZIONE
        String forzaConserva = filtri.getFl_forza_conservazione().parse();
        if (forzaConserva != null) {
            queryStr.append(whereWord).append("u.flForzaConservazione = :forzaconservain ");
            whereWord = " AND ";
        }
        // Inserimento nella query del filtro FORZA COLLEGAMENTO
        String forzaColleg = filtri.getFl_forza_collegamento().parse();
        if (forzaColleg != null) {
            queryStr.append(whereWord).append("u.flForzaCollegamento = :forzacollegin ");
            whereWord = "AND ";
        }

        String unitaDocAnnul = filtri.getFl_unita_doc_annul().parse();
        if (unitaDocAnnul != null) {
            queryStr.append(whereWord).append("u.flUnitaDocAnnul = :unitaDocAnnul ");
            whereWord = "AND ";
        }
        String docAggiunti = filtri.getFl_doc_aggiunti().parse();
        if (docAggiunti != null) {
            queryStr.append(whereWord).append("u.flDocAggiunti = :docAggiunti ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro FL_HASH_VERS
        String flHashVers = filtri.getFl_hash_vers().parse();
        if (flHashVers != null) {
            queryStr.append(whereWord).append("u.flHashVers = :flHashVers ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro STATO CONSERVAZIONE
        String tiStatoConservazione = filtri.getTi_stato_conservazione().parse();
        if (tiStatoConservazione != null) {
            queryStr.append(whereWord).append("u.tiStatoConservazione = :tiStatoConservazione ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro TI_STATO_UD_ELENCO_VERS
        String tiStatoUdElencoVers = filtri.getTi_stato_ud_elenco_vers().parse();
        if (tiStatoUdElencoVers != null) {
            queryStr.append(whereWord).append(
                    "(u.tiStatoUdElencoVers = :tiStatoUdElencoVers OR u.tiStatoDocElencoVers = :tiStatoUdElencoVers) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro NM_SISTEMA_MIGRAZ in versione multiselect
        List<String> sisMigrazList = filtri.getNm_sistema_migraz().parse();
        if (!sisMigrazList.isEmpty()) {
            queryStr.append(whereWord).append("(u.nmSistemaMigraz IN :listasismigraz)");
            whereWord = " AND ";
        }

        Date data_meta_da = (dateUnitaDocValidate != null ? dateUnitaDocValidate[0] : null);
        Date data_meta_a = (dateUnitaDocValidate != null ? dateUnitaDocValidate[1] : null);

        if ((data_meta_da != null) && (data_meta_a != null)) {
            queryStr.append(whereWord).append("(u.dtRegUnitaDoc between :datametada AND :datametaa) ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro TI_DOC ("Elemento")
        String tiDoc = filtri.getTi_doc().parse();
        if (tiDoc != null) {
            queryStr.append(whereWord).append("u.tiDoc = :tiDoc ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro metadati Oggetto
        String oggettoMeta = filtri.getDl_oggetto_unita_doc().parse();
        if (oggettoMeta != null) {
            queryStr.append(whereWord).append("UPPER(u.dlOggettoUnitaDoc) LIKE :dloggettounitadocin ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro metadati Autore documento
        String autoreDocMeta = filtri.getDs_autore_doc().parse();
        if (autoreDocMeta != null) {
            queryStr.append(whereWord).append("UPPER(u.dsAutoreDoc) LIKE :dldocmetain ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro metadati Descrizione documento
        String descrizioneDocMeta = filtri.getDl_doc().parse();
        if (descrizioneDocMeta != null) {
            queryStr.append(whereWord).append("UPPER(u.dlDoc) LIKE :dsautoredocin ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro Tipo Conservazione
        String tipoConservazione = filtri.getTi_conservazione().parse();
        if (tipoConservazione != null) {
            queryStr.append(whereWord).append("u.tiConservazione = :ticonservazionein ");
            whereWord = " AND ";
        }

        queryStr.append(whereWord).append("u.idStrutUnitaDoc = :idstrutin ");
        whereWord = " AND ";

        // UTILIZZO DEI DATI SPECIFICI
        ReturnParams params = volumeHelper.buildQueryForDatiSpec(listaDatiSpecOnLine);
        queryStr.append(params.getQuery());
        List<DatiSpecQueryParams> mappone = params.getMappone();

        String valoreClassif = null;
        BigDecimal aaFascicolo = null;
        String cdKeyFascicolo = null;
        BigDecimal aaFascRangeDa = null;
        BigDecimal aaFascRangeA = null;
        String cdFascRangeDa = null;
        String cdFascRangeA = null;
        BigDecimal tipoFascIn = null;
        Date dtApeFascRangeDa = null;
        Date dtApeFascRangeA = null;
        Date dtChiuFascRangeDa = null;
        Date dtChiuFascRangeA = null;
        String oggettoFasc = null;

        // FILTRI SU FASCICOLI
        // TODO: verificare, Verifico se almeno un filtro sul fascicolo di appartenenza è stato definito
        boolean fascfilterDefined = false;
        for (Field field : filtriFascicoli.getComponentList()) {
            if (((SingleValueField) field).getValue() != null && ((SingleValueField) field).getValue().length() > 0) {
                fascfilterDefined = true;
                break;
            }
        }

        if (fascfilterDefined) {
            queryStr.append(whereWord)
                    .append("exists (select fasc_ud from AroVLisFasc fasc_ud where fasc_ud.idUnitaDoc = u.idUnitaDoc ");

            // Se il filtro "Indice cassificazione" è valorizzato
            valoreClassif = filtriFascicoli.getCd_composito_voce_titol().parse();
            if (valoreClassif != null) {
                queryStr.append(whereWord).append("UPPER(fasc_ud.cdCompositoVoceTitol) LIKE :valoreclassif ");
                whereWord = " AND ";
            }

            aaFascicolo = filtriFascicoli.getAa_fascicolo().parse();
            if (aaFascicolo != null) {
                queryStr.append(whereWord).append("fasc_ud.aaFascicolo = :aafasc ");
                whereWord = " AND ";
            }

            cdKeyFascicolo = filtriFascicoli.getCd_key_fascicolo().parse();
            if (cdKeyFascicolo != null) {
                queryStr.append(whereWord).append("fasc_ud.cdKeyFascicolo = :cdfasc ");
                whereWord = " AND ";
            }

            aaFascRangeDa = filtriFascicoli.getAa_fascicolo_da().parse();
            aaFascRangeA = filtriFascicoli.getAa_fascicolo_a().parse();
            if (aaFascRangeDa != null && aaFascRangeA != null) {
                queryStr.append(whereWord).append("(fasc_ud.aaFascicolo BETWEEN :aafasc_da AND :aafasc_a) ");
                whereWord = " AND ";
            }

            cdFascRangeDa = filtriFascicoli.getCd_key_fascicolo_da().parse();
            cdFascRangeA = filtriFascicoli.getCd_key_fascicolo_a().parse();
            if (cdFascRangeDa != null && cdFascRangeA != null) {
                cdFascRangeDa = StringPadding.padString(cdFascRangeDa, "0", 12, StringPadding.PADDING_LEFT);
                cdFascRangeA = StringPadding.padString(cdFascRangeA, "0", 12, StringPadding.PADDING_LEFT);
                queryStr.append(whereWord)
                        .append("FUNC('lpad', fasc_ud.cdKeyFascicolo, 12, '0') BETWEEN :cdfasc_da AND :cdfasc_a ");
                whereWord = " AND ";
            }

            tipoFascIn = filtriFascicoli.getNm_tipo_fascicolo().parse();
            if (filtriFascicoli.getNm_tipo_fascicolo().parse() != null) {
                queryStr.append(whereWord).append("fasc_ud.idTipoFascicolo = :tipofascin ");
                whereWord = " AND ";
            }

            dtApeFascRangeDa = filtriFascicoli.getDt_ape_fascicolo_da().parse();
            dtApeFascRangeA = filtriFascicoli.getDt_ape_fascicolo_a().parse();
            if (dtApeFascRangeDa != null && dtApeFascRangeA != null) {
                queryStr.append(whereWord).append("(fasc_ud.dtApeFascicolo BETWEEN :dtapefasc_da AND :dtapefasc_a) ");
                whereWord = " AND ";
            }

            dtChiuFascRangeDa = filtriFascicoli.getDt_chiu_fascicolo_da().parse();
            dtChiuFascRangeA = filtriFascicoli.getDt_chiu_fascicolo_a().parse();
            if (dtChiuFascRangeDa != null && dtChiuFascRangeA != null) {
                queryStr.append(whereWord)
                        .append("(fasc_ud.dtChiuFascicolo BETWEEN :dtchiufasc_da AND :dtchiufasc_a) ");
                whereWord = " AND ";
            }

            oggettoFasc = filtriFascicoli.getDs_oggetto_fascicolo().parse();
            if (oggettoFasc != null) {
                queryStr.append(whereWord).append("UPPER(fasc_ud.dsOggettoFascicolo) LIKE :dsoggfasc ");
                whereWord = " AND ";
            }

            queryStr.append(") ");
        }

        String valoreColl = null;
        String valoreColl2 = null;
        String valorereg = null;
        BigDecimal valoreanno = null;
        String valorenumero = null;

        // FILTRI SU COLLEGAMENTI
        // Se il filtro "Con collegamento" vale "Sì"
        if (filtriCollegamenti.getCon_collegamento().parse() != null
                && filtriCollegamenti.getCon_collegamento().parse().equals("1")) {
            queryStr.append(whereWord).append(
                    "exists (select link from AroLinkUnitaDoc link where link.aroUnitaDoc.idUnitaDoc = u.idUnitaDoc ");

            // Se il filtro "Collegamento risolto" vale "Sì"
            if (filtriCollegamenti.getCollegamento_risolto().parse() != null
                    && filtriCollegamenti.getCollegamento_risolto().parse().equals("1")) {
                queryStr.append(whereWord).append("link.aroUnitaDocLink is not null ");
            }
            // Se il filtro "Collegamento risolto" vale "No"
            if (filtriCollegamenti.getCollegamento_risolto().parse() != null
                    && filtriCollegamenti.getCollegamento_risolto().parse().equals("0")) {
                queryStr.append(whereWord).append("link.aroUnitaDocLink is null ");
            }
            // Se il filtro "Descr. collegamento" è valorizzato
            valoreColl = filtriCollegamenti.getDs_link_unita_doc().parse();
            if (valoreColl != null) {
                queryStr.append(whereWord).append("UPPER(link.dsLinkUnitaDoc) LIKE :valorecoll ");
            }
            // Se il filtro "Registro" è valorizzato
            valorereg = filtriCollegamenti.getCd_registro_key_unita_doc_link().getDecodedValue();
            if (valorereg != null && !valorereg.equals("")) {
                queryStr.append(whereWord).append("link.cdRegistroKeyUnitaDocLink = :valorereg ");
            }
            // Se il filtro "Anno" è valorizzato
            valoreanno = filtriCollegamenti.getAa_key_unita_doc_link().parse();
            if (valoreanno != null) {
                queryStr.append(whereWord).append("link.aaKeyUnitaDocLink = :valoreanno ");
            }
            // Se il filtro "Numero" è valorizzato
            valorenumero = filtriCollegamenti.getCd_key_unita_doc_link().parse();
            if (valorenumero != null) {
                queryStr.append(whereWord).append("link.cdKeyUnitaDocLink = :valorenumero ");
            }
            queryStr.append(") ");
        }
        // Se il filtro "Con collegamento" vale "No"
        if (filtriCollegamenti.getCon_collegamento().parse() != null
                && filtriCollegamenti.getCon_collegamento().parse().equals("0")) {
            queryStr.append(whereWord).append(
                    "not exists (select link1 from AroLinkUnitaDoc link1 where link1.aroUnitaDoc.idUnitaDoc = u.idUnitaDoc ");
            queryStr.append(") ");
        }
        // Se il filtro "E' oggetto di collegamento" vale "Sì"
        if (filtriCollegamenti.getIs_oggetto_collegamento().parse() != null
                && filtriCollegamenti.getIs_oggetto_collegamento().parse().equals("1")) {
            queryStr.append(whereWord).append(
                    "exists (select link2 from AroLinkUnitaDoc link2 where link2.aroUnitaDocLink.idUnitaDoc = u.idUnitaDoc ");

            // Se il filtro "Descr. collegamento oggetto" è valorizzato
            valoreColl2 = filtriCollegamenti.getDs_link_unita_doc_oggetto().parse();
            if (valoreColl2 != null) {
                queryStr.append(whereWord).append("UPPER(link2.dsLinkUnitaDoc) LIKE :valorecoll2 ");
            }
            queryStr.append(") ");
        }
        // Se il filtro "Con collegamento" vale "No"
        if (filtriCollegamenti.getIs_oggetto_collegamento().parse() != null
                && filtriCollegamenti.getIs_oggetto_collegamento().parse().equals("0")) {
            queryStr.append(whereWord).append(
                    "not exists (select link3 from AroLinkUnitaDoc link3 where link3.aroUnitaDocLink.idUnitaDoc = u.idUnitaDoc ");
            queryStr.append(") ");
        }

        if (filtri.getDs_classif().parse() != null || filtri.getCd_fascic().parse() != null
                || filtri.getDs_oggetto_fascic().parse() != null || filtri.getCd_sottofascic().parse() != null
                || filtri.getDs_oggetto_sottofascic().parse() != null) {
            queryStr.append(whereWord)
                    .append(" (exists (select ud from AroUnitaDoc ud where ud.idUnitaDoc = u.idUnitaDoc ");
            if (filtri.getDs_classif().parse() != null) {
                queryStr.append(" and UPPER(ud.dsClassifPrinc) like :classif ");
            }
            if (filtri.getCd_fascic().parse() != null) {
                queryStr.append(" and UPPER(ud.cdFascicPrinc) like :fascic ");
            }
            if (filtri.getDs_oggetto_fascic().parse() != null) {
                queryStr.append(" and UPPER(ud.dsOggettoFascicPrinc) like :oggFascic ");
            }
            if (filtri.getCd_sottofascic().parse() != null) {
                queryStr.append(" and UPPER(ud.cdSottofascicPrinc) like :sottoFascic ");
            }
            if (filtri.getDs_oggetto_sottofascic().parse() != null) {
                queryStr.append(" and UPPER(ud.dsOggettoSottofascicPrinc) like :oggSottoFascic ");
            }

            queryStr.append(
                    ") or exists (select arch_sec from AroArchivSec arch_sec where arch_sec.aroUnitaDoc.idUnitaDoc = u.idUnitaDoc ");
            if (filtri.getDs_classif().parse() != null) {
                queryStr.append(" and UPPER(arch_sec.dsClassif) like :classif ");
            }
            if (filtri.getCd_fascic().parse() != null) {
                queryStr.append(" and UPPER(arch_sec.cdFascic) like :fascic ");
            }
            if (filtri.getDs_oggetto_fascic().parse() != null) {
                queryStr.append(" and UPPER(arch_sec.dsOggettoFascic) like :oggFascic ");
            }
            if (filtri.getCd_sottofascic().parse() != null) {
                queryStr.append(" and UPPER(arch_sec.cdSottofascic) like :sottoFascic ");
            }
            if (filtri.getDs_oggetto_sottofascic().parse() != null) {
                queryStr.append(" and UPPER(arch_sec.dsOggettoSottofascic) like :oggSottoFascic ");
            }
            queryStr.append(")) ");
            whereWord = " AND ";
        }

        List<BigDecimal> subStruts = filtri.getNm_sub_strut().parse();
        if (!subStruts.isEmpty()) {
            queryStr.append(whereWord).append(" u.idSubStrut IN :subStruts ");
            whereWord = "AND ";
        }
        // ordina per dsKeyDoc crescente
        queryStr.append("ORDER BY u.dsKeyOrd");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr.toString());
        // non avendo passato alla query i parametri di ricerca, devo passarli ora

        if (filtriComponenti.getNm_tipo_strut_doc().parse() != null) {
            query.setParameter("tipoStrutDocIn", filtriComponenti.getNm_tipo_strut_doc().parse());
        }

        if (!idTipoUnitaDocList.isEmpty()) {
            query.setParameter("listtipoud", idTipoUnitaDocList);
        }

        if (cdVersioneXsdUd != null) {
            query.setParameter("cdVersioneXsdUd", cdVersioneXsdUd);
        }

        if (cdVersioneXsdDoc != null) {
            query.setParameter("cdVersioneXsdDoc", cdVersioneXsdDoc);
        }

        if (!cdRegistroUnitaDocSet.isEmpty()) {
            query.setParameter("setregistro", cdRegistroUnitaDocSet);
        }

        if (anno != null) {
            query.setParameter("annoin", anno);
        }

        if (codice != null) {
            query.setParameter("codicein", codice);
        }

        if (cdKeyDocVers != null) {
            query.setParameter("cdKeyDocVers", "%" + cdKeyDocVers.toUpperCase() + "%");
        }

        if (anno_range_da != null && anno_range_a != null) {
            query.setParameter("annoin_da", anno_range_da);
            query.setParameter("annoin_a", anno_range_a);
        }

        if (codice_range_da != null && codice_range_a != null) {
            query.setParameter("codicein_da", codice_range_da);
            query.setParameter("codicein_a", codice_range_a);
        }

        if (codice_num_contiene != null) {
            query.setParameter("codicein_contiene", "%" + codice_num_contiene + "%");
        }

        if (presenza != null) {
            query.setParameter("presenzain", presenza);
        }

        if (!esitoveriffirmeList.isEmpty()) {
            query.setParameter("listaesitoveriffirme", esitoveriffirmeList);
        }

        if (data_da != null && data_a != null) {
            query.setParameter("datada", data_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_a, TemporalType.TIMESTAMP);
        }
        if (profiloNorm != null) {
            query.setParameter("profiloNorm", profiloNorm);
        }
        if (forzaAcc != null) {
            query.setParameter("forzaaccin", forzaAcc);
        }

        if (forzaConserva != null) {
            query.setParameter("forzaconservain", forzaConserva);
        }

        if (forzaColleg != null) {
            query.setParameter("forzacollegin", forzaColleg);
        }

        if (unitaDocAnnul != null) {
            query.setParameter("unitaDocAnnul", unitaDocAnnul);
        }

        if (docAggiunti != null) {
            query.setParameter("docAggiunti", docAggiunti);
        }

        if (flHashVers != null) {
            query.setParameter("flHashVers", flHashVers);
        }

        if (tiStatoConservazione != null) {
            query.setParameter("tiStatoConservazione", tiStatoConservazione);
        }

        if (tiStatoUdElencoVers != null) {
            query.setParameter("tiStatoUdElencoVers", tiStatoUdElencoVers);
        }

        if (!idTipoDocList.isEmpty()) {
            query.setParameter("listtipodoc", idTipoDocList);
        }

        if (cdVersioneXsdUd != null) {
            query.setParameter("cdVersioneXsdUd", cdVersioneXsdUd);
        }

        if (!sisMigrazList.isEmpty()) {
            query.setParameter("listasismigraz", sisMigrazList);
        }

        if (data_meta_da != null && data_meta_a != null) {
            query.setParameter("datametada", data_meta_da, TemporalType.DATE);
            query.setParameter("datametaa", data_meta_a, TemporalType.DATE);
        }

        if (tiDoc != null) {
            query.setParameter("tiDoc", tiDoc);
        }

        if (oggettoMeta != null) {
            query.setParameter("dloggettounitadocin", "%" + oggettoMeta.toUpperCase() + "%");
        }

        if (autoreDocMeta != null) {
            query.setParameter("dldocmetain", "%" + autoreDocMeta.toUpperCase() + "%");
        }

        if (descrizioneDocMeta != null) {
            query.setParameter("dsautoredocin", "%" + descrizioneDocMeta.toUpperCase() + "%");
        }

        if (idStruttura != null) {
            query.setParameter("idstrutin", idStruttura);
        }

        if (tipoConservazione != null) {
            query.setParameter("ticonservazionein", tipoConservazione);
        }

        int contaIdAttrib = 0;
        for (int i = 0; i < mappone.size(); i++) {
            DatiSpecQueryParams dsqp = mappone.get(i);

            if (!dsqp.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.E_UNO_FRA.name())) {
                if (StringUtils.isNotBlank(dsqp.getDlValore())) {
                    query.setParameter("valorein" + i, dsqp.getDlValore().toUpperCase());
                }
            } else {
                String[] inParams = dsqp.getDlValore().toUpperCase().trim().replaceAll("\\s*,\\s*", ",").split(",");
                query.setParameter("valorein" + i, Arrays.asList(inParams));
            }
            for (int j = 0; j < dsqp.getIdAttribDatiSpec().size(); j++) {
                query.setParameter("idattribdatispecin" + contaIdAttrib, dsqp.getIdAttribDatiSpec().get(j));
                if (dsqp.getNmSistemaMigraz().get(j) != null) {
                    query.setParameter("nmsistemamigrazin" + contaIdAttrib, dsqp.getNmSistemaMigraz().get(j));
                }
                contaIdAttrib++;
            }
        }

        if (valoreClassif != null) {
            query.setParameter("valoreclassif", "%" + valoreClassif.toUpperCase() + "%");
        }

        if (aaFascicolo != null) {
            query.setParameter("aafasc", aaFascicolo);
        }

        if (cdKeyFascicolo != null) {
            query.setParameter("cdfasc", cdKeyFascicolo);
        }

        if (aaFascRangeDa != null && aaFascRangeA != null) {
            query.setParameter("aafasc_da", aaFascRangeDa);
            query.setParameter("aafasc_a", aaFascRangeA);
        }

        if (cdFascRangeDa != null && cdFascRangeA != null) {
            query.setParameter("cdfasc_da", cdFascRangeDa);
            query.setParameter("cdfasc_a", cdFascRangeA);
        }

        if (tipoFascIn != null) {
            query.setParameter("tipofascin", tipoFascIn);
        }

        if (dtApeFascRangeDa != null && dtApeFascRangeA != null) {
            query.setParameter("dtapefasc_da", dtApeFascRangeDa);
            query.setParameter("dtapefasc_a", dtApeFascRangeA);
        }

        if (dtChiuFascRangeDa != null && dtChiuFascRangeA != null) {
            query.setParameter("dtchiufasc_da", dtChiuFascRangeDa);
            query.setParameter("dtchiufasc_a", dtChiuFascRangeA);
        }

        if (oggettoFasc != null) {
            query.setParameter("dsoggfasc", "%" + oggettoFasc.toUpperCase() + "%");
        }

        if (valoreColl != null) {
            query.setParameter("valorecoll", "%" + valoreColl.toUpperCase() + "%");
        }
        if (valoreColl2 != null) {
            query.setParameter("valorecoll2", "%" + valoreColl2.toUpperCase() + "%");
        }

        if (valorereg != null && !valorereg.equals("")) {
            query.setParameter("valorereg", valorereg);
        }
        if (valoreanno != null) {
            query.setParameter("valoreanno", valoreanno);
        }
        if (valorenumero != null) {
            query.setParameter("valorenumero", valorenumero);
        }

        if (filtri.getDs_classif().parse() != null) {
            query.setParameter("classif", "%" + filtri.getDs_classif().parse().toUpperCase() + "%");
        }
        if (filtri.getCd_fascic().parse() != null) {
            query.setParameter("fascic", "%" + filtri.getCd_fascic().parse().toUpperCase() + "%");
        }
        if (filtri.getDs_oggetto_fascic().parse() != null) {
            query.setParameter("oggFascic", "%" + filtri.getDs_oggetto_fascic().parse().toUpperCase() + "%");
        }
        if (filtri.getCd_sottofascic().parse() != null) {
            query.setParameter("sottoFascic", "%" + filtri.getCd_sottofascic().parse().toUpperCase() + "%");
        }
        if (filtri.getDs_oggetto_sottofascic().parse() != null) {
            query.setParameter("oggSottoFascic", "%" + filtri.getDs_oggetto_sottofascic().parse().toUpperCase() + "%");
        }

        // Parametri Filtri Componente
        if (filtriComponenti.getNm_tipo_strut_doc().parse() != null) {
            query.setParameter("tipoStrutDocIn", filtriComponenti.getNm_tipo_strut_doc().parse());
        }
        if (filtriComponenti.getNm_tipo_comp_doc().parse() != null) {
            query.setParameter("tipoCompDocIn", filtriComponenti.getNm_tipo_comp_doc().parse());
        }
        if (filtriComponenti.getDs_nome_comp_vers().parse() != null) {
            query.setParameter("nomeCompVersIn",
                    "%" + filtriComponenti.getDs_nome_comp_vers().parse().toUpperCase() + "%");
        }
        if (filtriComponenti.getDl_urn_comp_vers().parse() != null) {
            query.setParameter("urnCompVersIn",
                    "%" + filtriComponenti.getDl_urn_comp_vers().parse().toUpperCase() + "%");
        }
        if (filtriComponenti.getDs_hash_file_vers().parse() != null) {
            query.setParameter("hashFileVersIn", filtriComponenti.getDs_hash_file_vers().parse());
        }
        if (filtriComponenti.getNm_mimetype_file().parse() != null) {
            query.setParameter("mimeTypeIn", "%" + filtriComponenti.getNm_mimetype_file().parse().toUpperCase() + "%");
        }
        if (filtriComponenti.getNm_formato_file_vers().parse() != null) {
            query.setParameter("formatoFileDocIn", filtriComponenti.getNm_formato_file_vers().parse());
        }
        if (fileSizeDa != null && fileSizeA != null) {
            query.setParameter("filesizedain", fileSizeDa);
            query.setParameter("filesizeain", fileSizeA);
        }
        if (filtriComponenti.getDs_formato_rappr_calc().parse() != null) {
            query.setParameter("formatoRapprCalcIn", filtriComponenti.getDs_formato_rappr_calc().parse());
        }
        if (filtriComponenti.getDs_formato_rappr_esteso_calc().parse() != null) {
            query.setParameter("formatoRapprCalcEstesoIn", filtriComponenti.getDs_formato_rappr_esteso_calc().parse());
        }
        if (filtriComponenti.getFl_comp_firmato().parse() != null) {
            // Gli devo sempre passare 1
            query.setParameter("compFirmatoIn", "1");
        }
        if (filtriComponenti.getTi_esito_contr_conforme().parse() != null) {
            query.setParameter("esitoContrConformeIn", filtriComponenti.getTi_esito_contr_conforme().parse());
        }
        if (filtriComponenti.getDs_rif_temp_vers().parse() != null) {
            query.setParameter("dsRifTempVers",
                    "%" + filtriComponenti.getDs_rif_temp_vers().parse().toUpperCase() + "%");
        }

        if ((data_val_da != null) && (data_val_a != null)) {
            query.setParameter("datavalda", data_val_da);
            query.setParameter("datavala", data_val_a);
        }

        if (filtriComponenti.getTi_esito_contr_formato_file().parse() != null) {
            query.setParameter("esitoContrFormatoFileIn", filtriComponenti.getTi_esito_contr_formato_file().parse());
        }
        if (filtriComponenti.getTi_esito_verif_firma().parse() != null) {
            query.setParameter("esitoVerifFirmeIn", filtriComponenti.getTi_esito_verif_firma().parse());
        }
        if (filtriComponenti.getTi_esito_verif_firme_chius().parse() != null) {
            query.setParameter("esitoVerifFirmeChiusIn", filtriComponenti.getTi_esito_verif_firme_chius().parse());
        }
        if (filtriComponenti.getDs_hash_file_calc().parse() != null) {
            query.setParameter("hashFileCalcIn", filtriComponenti.getDs_hash_file_calc().parse());
        }
        if (filtriComponenti.getDs_algo_hash_file_calc().parse() != null) {
            query.setParameter("algoHashFileCalcIn", filtriComponenti.getDs_algo_hash_file_calc().parse());
        }
        if (filtriComponenti.getCd_encoding_hash_file_calc().parse() != null) {
            query.setParameter("encodingHashFileCalcIn", filtriComponenti.getCd_encoding_hash_file_calc().parse());
        }
        if (filtriComponenti.getDs_urn_comp_calc().parse() != null) {
            query.setParameter("urnCompCalcIn",
                    "%" + filtriComponenti.getDs_urn_comp_calc().parse().toUpperCase() + "%");
        }

        if ((data_comp_da != null) && (data_comp_a != null)) {
            query.setParameter("dataCompDaIn", data_comp_da);
            query.setParameter("dataCompAIn", data_comp_a);
        }

        if (filtriComponenti.getFl_forza_accettazione_comp().parse() != null) {
            query.setParameter("flForzaAccIn", filtriComponenti.getFl_forza_accettazione_comp().parse());
        }

        if (filtriComponenti.getFl_forza_conservazione_comp().parse() != null) {
            query.setParameter("flForzaConsIn", filtriComponenti.getFl_forza_conservazione_comp().parse());
        }

        if (filtriComponenti.getTi_supporto_comp().parse() != null) {
            query.setParameter("tiSupportoCompIn", filtriComponenti.getTi_supporto_comp().parse());
        }

        if (filtriComponenti.getNm_tipo_rappr_comp().parse() != null) {
            query.setParameter("idTipoRapprCompIn", filtriComponenti.getNm_tipo_rappr_comp().parse());
        }

        if (filtriComponenti.getDs_id_comp_vers().parse() != null) {
            query.setParameter("idCompDocIn", "%" + filtriComponenti.getDs_id_comp_vers().parse().toUpperCase() + "%");
        }

        if (!subStruts.isEmpty()) {
            query.setParameter("subStruts", subStruts);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI UNITA' DOCUMENTARIE
        List<AroVRicUnitaDoc> listaUD = query.getResultList();
        AroVRicUnitaDocTableBean udTableBean = new AroVRicUnitaDocTableBean();

        try {
            if (listaUD != null && !listaUD.isEmpty()) {
                for (AroVRicUnitaDoc ud : listaUD) {
                    AroVRicUnitaDocRowBean rowBean = (AroVRicUnitaDocRowBean) Transform.entity2RowBean(ud);
                    // Se sto gestendo l'aggiunta di ud a una serie, aggiungo questo campo per inserire il bottone "+"
                    // sulla selectList
                    if (addButton) {
                        rowBean.setString("addSerie", "1");
                    }
                    udTableBean.add(rowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return udTableBean;
    }

    private String appendJoin(boolean formato, boolean firma, boolean certif, boolean appart) {
        StringBuilder join = new StringBuilder(", AroCompDoc comp JOIN comp.aroStrutDoc strutDoc ");
        StringBuilder where = new StringBuilder("WHERE strutDoc.aroDoc.idDoc = u.idDoc ");
        if (formato) {
            join.append(" JOIN comp.decFormatoFileStandard fileStandard ");
        }
        if (appart) {
            join.append(" JOIN comp.volAppartCompVolumes appartComp JOIN appartComp.volAppartDocVolume appartDoc ");
            where.append(" AND appartDoc.aroDoc.idDoc = u.idDoc AND appartDoc.flValida = '1' ");
        }
        if (firma) {
            join.append(" JOIN comp.aroFirmaComps firma ");
        }
        if (certif) {
            join.append(" JOIN firma.firCertifFirmatario certif ");
        }
        return join.append(where.toString()).toString();
    }

    public AroVRicUnitaDocTableBean getAroVRicUnitaDocsById(List<BigDecimal> ids) {
        String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.AroVRicUnitaDoc (u.idUnitaDoc, u.aaKeyUnitaDoc, u.cdKeyUnitaDoc, u.cdRegistroKeyUnitaDoc, u.dtCreazione, u.dtRegUnitaDoc, u.flUnitaDocFirmato, u.tiEsitoVerifFirme, u.dsMsgEsitoVerifFirme, u.nmTipoUnitaDoc, u.flForzaAccettazione, u.flForzaConservazione, u.dsKeyOrd, u.niAlleg, u.niAnnessi, u.niAnnot, u.nmTipoDocPrinc, u.dsListaStatiElencoVers, u.tiStatoConservazione) "
                + "FROM AroVRicUnitaDoc u " + "WHERE u.idUnitaDoc IN :ids " + "ORDER BY u.dsKeyOrd";

        Query query = getEntityManager().createQuery(queryStr);
        if (!ids.isEmpty()) {
            query.setParameter("ids", ids);
        }
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVRicUnitaDoc> listaUd = query.getResultList();

        AroVRicUnitaDocTableBean listaUdTB = new AroVRicUnitaDocTableBean();
        try {
            if (listaUd != null && !listaUd.isEmpty()) {
                listaUdTB = (AroVRicUnitaDocTableBean) Transform.entities2TableBean(listaUd);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaUdTB;
    }

    public AroVLisDocTableBean getAroVLisDocTableBeanByIdDoc(Set<BigDecimal> idDocSet) {
        String queryStr = "SELECT u FROM AroVLisDoc u " + "WHERE u.idDoc IN :idDocSet ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idDocSet", idDocSet);
        List<AroVLisDoc> listaDoc = query.getResultList();
        AroVLisDocTableBean docTB = new AroVLisDocTableBean();
        try {
            if (listaDoc != null && !listaDoc.isEmpty()) {
                for (AroVLisDoc row : listaDoc) {
                    AroVLisDocRowBean rowBean = (AroVLisDocRowBean) Transform.entity2RowBean(row);
                    if (!rowBean.getTiDoc().equals("PRINCIPALE")) {
                        rowBean.setTiDoc(row.getTiDoc() + " " + row.getPgDoc());
                    }
                    docTB.add(rowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return docTB;
    }

    public ElvVLisUpdUdTableBean getElvVLisUpdUdTableBeanByIdUpd(Set<BigDecimal> idUpdUnitaDocSet) {
        String queryStr = "SELECT u FROM ElvVLisUpdUd u " + "WHERE u.idUpdUnitaDoc IN :idUpdUnitaDocSet ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUpdUnitaDocSet", idUpdUnitaDocSet);
        List<ElvVLisUpdUd> listaUpd = query.getResultList();
        ElvVLisUpdUdTableBean updTB = new ElvVLisUpdUdTableBean();
        try {
            if (listaUpd != null && !listaUpd.isEmpty()) {
                for (ElvVLisUpdUd row : listaUpd) {
                    ElvVLisUpdUdRowBean rowBean = (ElvVLisUpdUdRowBean) Transform.entity2RowBean(row);
                    updTB.add(rowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return updTB;
    }

    // Metodo che restituisce un rowbean con il record trovato in base
    // al''id unità documentaria
    public AroVVisUnitaDocIamRowBean getAroVVisUnitaDocIamRowBean(BigDecimal idud) throws EMFError {
        AroVVisUnitaDocIam record = getEntityManager().find(AroVVisUnitaDocIam.class, idud);
        AroVVisUnitaDocIamRowBean dettaglioUD = new AroVVisUnitaDocIamRowBean();
        try {
            if (record != null) {
                dettaglioUD = (AroVVisUnitaDocIamRowBean) Transform.entity2RowBean(record);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio dell'unita' documentaria" + e.getMessage(), e);
        }

        // Calcolo il campo stato conservazione
        setStatiDocumentari(dettaglioUD);

        return dettaglioUD;
    }

    public AroVLisDocTableBean getAroVLisDocTableBean(BigDecimal idud, String tipoDoc) throws EMFError {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT new it.eng.parer.viewEntity.AroVLisDoc(u.cdKeyDocVers, u.dlDoc, u.dsAutoreDoc, u.dtCreazione, u.flDocFirmato, u.idDoc, u.idUnitaDoc, u.nmTipoDoc, u.pgDoc, u.tiDoc, u.tiDocOrd, u.tiStatoElencoVers, u.tiEsitoVerifFirme) "
                        + " FROM AroVLisDoc u " + "WHERE u.idUnitaDoc = :idud ");

        if (tipoDoc != null) {
            queryStr.append(" and u.tiDoc = :tipodoc ");
        }

        queryStr.append("ORDER BY u.tiDocOrd, u.pgDoc");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr.toString());

        query.setParameter("idud", idud);
        if (tipoDoc != null) {
            query.setParameter("tipodoc", tipoDoc);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisDoc> listaDoc = query.getResultList();

        AroVLisDocTableBean listaDocTableBean = new AroVLisDocTableBean();
        try {
            if (listaDoc != null && !listaDoc.isEmpty()) {
                for (AroVLisDoc row : listaDoc) {
                    AroVLisDocRowBean rowBean = (AroVLisDocRowBean) Transform.entity2RowBean(row);
                    if (!rowBean.getTiDoc().equals("PRINCIPALE")) {
                        rowBean.setTiDoc(row.getTiDoc() + " " + row.getPgDoc());
                    }
                    listaDocTableBean.add(rowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return listaDocTableBean;
    }

    public AroVVisDocIamTableBean getAroVVisDocAggIamTableBean(BigDecimal idud, Date dataCreazioneUd) throws EMFError {
        String queryStr = "SELECT u FROM AroVVisDocIam u " + "WHERE u.idUnitaDoc = :idud "
                + "AND u.dtCreazione > :datacreazioneud ";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idud", idud);
        query.setParameter("datacreazioneud", dataCreazioneUd);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisDocIam> listaDoc = query.getResultList();

        AroVVisDocIamTableBean listaDocTableBean = new AroVVisDocIamTableBean();
        try {
            if (listaDoc != null && !listaDoc.isEmpty()) {
                listaDocTableBean = (AroVVisDocIamTableBean) Transform.entities2TableBean(listaDoc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaDocTableBean;
    }

    public AroVLisLinkUnitaDocTableBean getAroVLisLinkUnitaDocTableBean(BigDecimal idud, int maxResults)
            throws EMFError {
        String queryStr = "SELECT u FROM AroVLisLinkUnitaDoc u WHERE u.idUnitaDoc = :idud ORDER BY u.cdKeyUnitaDocLink";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idud", idud);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisLinkUnitaDoc> listaLink = query.getResultList();

        AroVLisLinkUnitaDocTableBean listaLinkTableBean = new AroVLisLinkUnitaDocTableBean();
        try {
            if (listaLink != null && !listaLink.isEmpty()) {
                listaLinkTableBean = (AroVLisLinkUnitaDocTableBean) Transform.entities2TableBean(listaLink);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaLinkTableBean;
    }

    public AroVLisArchivUnitaDocTableBean getAroVLisArchivUnitaDocTableBean(BigDecimal idud, int maxResults)
            throws EMFError {
        String queryStr = "SELECT u FROM AroVLisArchivUnitaDoc u WHERE u.idUnitaDoc = :idud ORDER BY u.dsClassif, u.cdFascic";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idud", idud);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisArchivUnitaDoc> listaArchiv = query.getResultList();

        AroVLisArchivUnitaDocTableBean listaArchivTableBean = new AroVLisArchivUnitaDocTableBean();
        try {
            if (listaArchiv != null && !listaArchiv.isEmpty()) {
                listaArchivTableBean = (AroVLisArchivUnitaDocTableBean) Transform.entities2TableBean(listaArchiv);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaArchivTableBean;
    }

    // Metodi per la visualizzazione/ricerca su DOCUMENTI
    public AroVVisDocIamRowBean getAroVVisDocIamRowBean(BigDecimal iddoc) throws EMFError {
        String queryStr = "SELECT u FROM AroVVisDocIam u WHERE u.idDoc = :iddoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("iddoc", iddoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisDocIam> doc = query.getResultList();

        AroVVisDocIamRowBean docRowBean = new AroVVisDocIamRowBean();
        try {
            if (doc != null && !doc.isEmpty()) {
                docRowBean = (AroVVisDocIamRowBean) Transform.entity2RowBean(doc.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return docRowBean;
    }

    // Metodi per la visualizzazione/ricerca su DOCUMENTI
    public AroVVisDocIamTableBean getAroVVisDocIamTableBean(BigDecimal iddoc) throws EMFError {
        String queryStr = "SELECT u FROM AroVVisDocIam u WHERE u.idDoc = :iddoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("iddoc", iddoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisDocIam> doc = query.getResultList();

        AroVVisDocIamTableBean docTableBean = new AroVVisDocIamTableBean();
        try {
            if (doc != null && !doc.isEmpty()) {
                docTableBean = (AroVVisDocIamTableBean) Transform.entities2TableBean(doc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return docTableBean;
    }

    // Metodi per la visualizzazione/ricerca su AGGIORNAMENTI METADATI
    public AroVVisUpdUnitaDocRowBean getAroVVisUpdUnitaDocRowBean(BigDecimal idupdunitadoc) throws EMFError {
        String queryStr = "SELECT u FROM AroVVisUpdUnitaDoc u WHERE u.idUpdUnitaDoc = :idupdunitadoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idupdunitadoc", idupdunitadoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisUpdUnitaDoc> upd = query.getResultList();

        AroVVisUpdUnitaDocRowBean updRowBean = new AroVVisUpdUnitaDocRowBean();
        try {
            if (upd != null && !upd.isEmpty()) {
                updRowBean = (AroVVisUpdUnitaDocRowBean) Transform.entity2RowBean(upd.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return updRowBean;
    }

    // Metodi per la visualizzazione/ricerca su AGGIORNAMENTI METADATI
    public AroVVisUpdUnitaDocTableBean getAroVVisUpdUnitaDocTableBean(BigDecimal idupdunitadoc) throws EMFError {
        String queryStr = "SELECT u FROM AroVVisUpdUnitaDoc u WHERE u.idUpdUnitaDoc = :idupdunitadoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idupdunitadoc", idupdunitadoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisUpdUnitaDoc> upd = query.getResultList();

        AroVVisUpdUnitaDocTableBean updTableBean = new AroVVisUpdUnitaDocTableBean();
        try {
            if (upd != null && !upd.isEmpty()) {
                updTableBean = (AroVVisUpdUnitaDocTableBean) Transform.entities2TableBean(upd);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return updTableBean;
    }

    public AroVLisCompDocTableBean getAroVLisCompDocTableBean(BigDecimal iddoc, int maxResults) throws EMFError {
        String queryStr = "SELECT u FROM AroVLisCompDoc u WHERE u.idDoc = :iddoc ORDER BY u.niOrdCompDoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("iddoc", iddoc);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisCompDoc> listaCompDoc = query.getResultList();

        AroVLisCompDocTableBean listaCompDocTableBean = new AroVLisCompDocTableBean();
        try {
            if (listaCompDoc != null && !listaCompDoc.isEmpty()) {
                listaCompDocTableBean = (AroVLisCompDocTableBean) Transform.entities2TableBean(listaCompDoc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return listaCompDocTableBean;
    }

    public AroVLisUpdDocUnitaDocTableBean getAroVLisUpdDocUnitaDocTableBean(BigDecimal idupd, int maxResults)
            throws EMFError {
        String queryStr = "SELECT u FROM AroVLisUpdDocUnitaDoc u WHERE u.idUpdUnitaDoc = :idupd ORDER BY u.tiDocOrd, u.pgDoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idupd", idupd);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisUpdDocUnitaDoc> listaUpdDoc = query.getResultList();

        AroVLisUpdDocUnitaDocTableBean listaUpdDocUnitaDocTableBean = new AroVLisUpdDocUnitaDocTableBean();
        try {
            if (listaUpdDoc != null && !listaUpdDoc.isEmpty()) {
                listaUpdDocUnitaDocTableBean = (AroVLisUpdDocUnitaDocTableBean) Transform
                        .entities2TableBean(listaUpdDoc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return listaUpdDocUnitaDocTableBean;
    }

    public AroVLisDatiSpecDocTableBean getAroVLisDatiSpecDocTableBean(BigDecimal iddoc, int maxResults)
            throws EMFError {
        // String queryStr = "SELECT u FROM AroVLisDatiSpecDoc u WHERE u.idDoc = :iddoc ORDER BY u.niOrdAttrib";

        String queryStr = "SELECT u FROM AroVLisDatiSpecDoc u WHERE u.idDoc = :iddoc ORDER BY u.niOrdAttrib";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("iddoc", iddoc);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisDatiSpecDoc> listaDatiSpecDoc = query.getResultList();

        AroVLisDatiSpecDocTableBean listaDatiSpecDocTableBean = new AroVLisDatiSpecDocTableBean();
        try {
            if (listaDatiSpecDoc != null && !listaDatiSpecDoc.isEmpty()) {
                listaDatiSpecDocTableBean = (AroVLisDatiSpecDocTableBean) Transform
                        .entities2TableBean(listaDatiSpecDoc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaDatiSpecDocTableBean;
    }

    public AroVLisVolNoValDocTableBean getAroVLisVolNoValDocTableBean(BigDecimal iddoc, int maxResults)
            throws EMFError {
        String queryStr = "SELECT u FROM AroVLisVolNoValDoc u WHERE u.idDoc = :iddoc ORDER BY u.dtCreazione DESC";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("iddoc", iddoc);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisVolNoValDoc> listaVolNoValDoc = query.getResultList();

        AroVLisVolNoValDocTableBean listaVolNoValDocTableBean = new AroVLisVolNoValDocTableBean();
        try {
            if (listaVolNoValDoc != null && !listaVolNoValDoc.isEmpty()) {
                listaVolNoValDocTableBean = (AroVLisVolNoValDocTableBean) Transform
                        .entities2TableBean(listaVolNoValDoc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaVolNoValDocTableBean;
    }

    public AroVLisUpdCompUnitaDocTableBean getAroVLisUpdCompUnitaDocTableBean(BigDecimal idupd, int maxResults)
            throws EMFError {
        String queryStr = "SELECT u FROM AroVLisUpdCompUnitaDoc u WHERE u.idUpdUnitaDoc = :idupd ORDER BY u.tiDocOrd, u.pgDoc, u.niOrdCompDoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idupd", idupd);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisUpdCompUnitaDoc> listaUpdCompUnitaDoc = query.getResultList();

        AroVLisUpdCompUnitaDocTableBean listaUpdCompUnitaDocTableBean = new AroVLisUpdCompUnitaDocTableBean();
        try {
            if (listaUpdCompUnitaDoc != null && !listaUpdCompUnitaDoc.isEmpty()) {
                listaUpdCompUnitaDocTableBean = (AroVLisUpdCompUnitaDocTableBean) Transform
                        .entities2TableBean(listaUpdCompUnitaDoc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaUpdCompUnitaDocTableBean;
    }

    public AroVLisUpdKoRisoltiTableBean getAroVLisUpdKoRisoltiTableBean(BigDecimal idupd, int maxResults)
            throws EMFError {
        String queryStr = "SELECT u FROM AroVLisUpdKoRisolti u WHERE u.idUpdUnitaDoc = :idupd ORDER BY u.tsIniSes";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idupd", idupd);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisUpdKoRisolti> listaUpdKoRisolti = query.getResultList();

        AroVLisUpdKoRisoltiTableBean listaUpdKoRisoltiTableBean = new AroVLisUpdKoRisoltiTableBean();
        try {
            if (listaUpdKoRisolti != null && !listaUpdKoRisolti.isEmpty()) {
                listaUpdKoRisoltiTableBean = (AroVLisUpdKoRisoltiTableBean) Transform
                        .entities2TableBean(listaUpdKoRisolti);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaUpdKoRisoltiTableBean;
    }

    public AroWarnUpdUnitaDocTableBean getAroWarnUpdUnitaDocTableBean(BigDecimal idupd, int maxResults)
            throws EMFError {
        String queryStr = "SELECT u FROM AroWarnUpdUnitaDoc u WHERE u.aroUpdUnitaDoc.idUpdUnitaDoc = :idupd ORDER BY u.pgWarn";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idupd", idupd);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroWarnUpdUnitaDoc> listaUpdWarnUnitaDoc = query.getResultList();

        AroWarnUpdUnitaDocTableBean listaUpdWarnUnitaDocTableBean = new AroWarnUpdUnitaDocTableBean();
        try {
            if (listaUpdWarnUnitaDoc != null && !listaUpdWarnUnitaDoc.isEmpty()) {
                for (AroWarnUpdUnitaDoc updWarnUD : listaUpdWarnUnitaDoc) {
                    AroWarnUpdUnitaDocRowBean listaUpdWarnUnitaDocRowBean = (AroWarnUpdUnitaDocRowBean) Transform
                            .entity2RowBean(updWarnUD);
                    listaUpdWarnUnitaDocRowBean.setString("cd_err", updWarnUD.getDecErrSacer().getCdErr());
                    listaUpdWarnUnitaDocTableBean.add(listaUpdWarnUnitaDocRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaUpdWarnUnitaDocTableBean;
    }

    public String getVersioniXsd(BigDecimal idAttribDatiSpec, BigDecimal id, TipoEntitaSacer tipoEntitaSacer) {
        String tmpTipoEntita = null;
        switch (tipoEntitaSacer) {
        case UNI_DOC:
            tmpTipoEntita = "dec.decTipoUnitaDoc.idTipoUnitaDoc";
            break;
        case DOC:
            tmpTipoEntita = "dec.decTipoDoc.idTipoDoc";
            break;
        case COMP:
            tmpTipoEntita = "dec.decTipoCompDoc.idTipoCompDoc";
            break;
        }

        String queryStr = String.format("SELECT DISTINCT dec.cdVersioneXsd FROM DecXsdAttribDatiSpec decXsd "
                + " JOIN decXsd.decXsdDatiSpec dec " + " JOIN decXsd.decAttribDatiSpec decAttrib "
                + " WHERE decAttrib.idAttribDatiSpec = :idattribdatispec " + " AND %s = :id", tmpTipoEntita);

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idattribdatispec", idAttribDatiSpec);
        query.setParameter("id", id);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<String> listaVersioni = query.getResultList();

        String versioni = "";
        if (!listaVersioni.isEmpty()) {
            versioni = "ver.";
            int count = 0;
            for (String valore : listaVersioni) {
                versioni = versioni + " " + valore;
                if (count < listaVersioni.size() - 1) {
                    versioni = versioni + ", ";
                    count++;
                }
            }
        }
        return versioni;
    }

    public String getVersioniXsdSisMigr(BigDecimal idAttribDatiSpec, String tipoEntitaSacer, String nmSistemaMigraz) {

        String queryStr = "SELECT DISTINCT dec.cdVersioneXsd FROM DecXsdAttribDatiSpec decXsd JOIN decXsd.decXsdDatiSpec dec "
                + " JOIN decXsd.decAttribDatiSpec decAttrib " + " WHERE decAttrib.idAttribDatiSpec = :idattribdatispec"
                + " AND dec.nmSistemaMigraz = :nmsistemamigrazin" + " AND dec.tiEntitaSacer = :tipoentitasacerin";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idattribdatispec", idAttribDatiSpec);
        query.setParameter("nmsistemamigrazin", nmSistemaMigraz);
        query.setParameter("tipoentitasacerin", tipoEntitaSacer);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<String> listaVersioni = query.getResultList();

        String versioni = "";
        if (!listaVersioni.isEmpty()) {
            versioni = "ver.";
            int count = 0;
            for (String valore : listaVersioni) {
                versioni = versioni + " " + valore;
                if (count < listaVersioni.size() - 1) {
                    versioni = versioni + ", ";
                    count++;
                }
            }
        }
        return versioni;
    }

    public DecAttribDatiSpecTableBean getDecAttribDatiSpecTableBean(BigDecimal id, TipoEntitaSacer tipoEntitaSacer)
            throws EMFError {
        String tmpTipoEntita = null;

        switch (tipoEntitaSacer) {
        case UNI_DOC:
            tmpTipoEntita = "u.decTipoUnitaDoc.idTipoUnitaDoc";
            break;
        case DOC:
            tmpTipoEntita = "u.decTipoDoc.idTipoDoc";
            break;
        case COMP:
            tmpTipoEntita = "u.decTipoCompDoc.idTipoCompDoc";
            break;
        }

        String queryStr = String.format("SELECT DISTINCT u FROM DecXsdAttribDatiSpec v JOIN v.decAttribDatiSpec u "
                + "WHERE %s = :id " + "ORDER BY v.niOrdAttrib", tmpTipoEntita);

        Query query = this.getEntityManager().createQuery(queryStr);
        query.setParameter("id", id);

        List<DecAttribDatiSpec> listaDatiSpec = query.getResultList();

        DecAttribDatiSpecTableBean listaDatiSpecTableBean = new DecAttribDatiSpecTableBean();
        try {
            if (listaDatiSpec != null && !listaDatiSpec.isEmpty()) {
                listaDatiSpecTableBean = (DecAttribDatiSpecTableBean) Transform.entities2TableBean(listaDatiSpec);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaDatiSpecTableBean;
    }

    public DecAttribDatiSpecTableBean getDecAttribDatiSpecSisMigrTableBean(String tipoSistemaMigrazione,
            BigDecimal idStrut) throws EMFError {

        String queryStr = "SELECT u FROM DecXsdAttribDatiSpec v JOIN v.decAttribDatiSpec u "
                + "WHERE u.nmSistemaMigraz = :nmsistemamigrazin " + "AND u.orgStrut.idStrut = :idstrutin "
                + "AND (u.tiEntitaSacer = :unidocin " + "OR u.tiEntitaSacer = :docin) " + "ORDER BY v.niOrdAttrib";

        Query query = this.getEntityManager().createQuery(queryStr);

        query.setParameter("nmsistemamigrazin", tipoSistemaMigrazione);
        query.setParameter("idstrutin", idStrut);
        query.setParameter("unidocin", TipoEntitaSacer.UNI_DOC.name());
        query.setParameter("docin", TipoEntitaSacer.DOC.name());

        List<DecAttribDatiSpec> listaDatiSpec = query.getResultList();

        DecAttribDatiSpecTableBean listaDatiSpecTableBean = new DecAttribDatiSpecTableBean();
        try {
            if (listaDatiSpec != null && !listaDatiSpec.isEmpty()) {
                listaDatiSpecTableBean = (DecAttribDatiSpecTableBean) Transform.entities2TableBean(listaDatiSpec);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaDatiSpecTableBean;
    }

    // Ricavo i componenti di tipo file di un determinato documento
    public AroVLisCompDocTableBean getAroVLisCompDocFileTableBean(BigDecimal iddoc) throws EMFError {
        String queryStr = "SELECT u FROM AroVLisCompDoc u WHERE u.idDoc = :iddoc and UPPER(u.tiSupportoComp) LIKE 'FILE' ORDER BY u.niOrdCompDoc";

        Query query = this.getEntityManager().createQuery(queryStr);
        query.setParameter("iddoc", iddoc);

        List listaCompDoc = query.getResultList();

        AroVLisCompDocTableBean listaCompDocTableBean = new AroVLisCompDocTableBean();
        try {
            if (listaCompDoc != null && !listaCompDoc.isEmpty()) {
                listaCompDocTableBean = (AroVLisCompDocTableBean) Transform.entities2TableBean(listaCompDoc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaCompDocTableBean;
    }

    // Ricavo i componenti di tipo file di un determinato documento
    public AroVLisDatiSpecTableBean getAroVLisDatiSpecTableBean(BigDecimal identificativo,
            TipoEntitaSacer tipoEntitaSacer, String tipoDatiSpec, int maxResults) throws EMFError {
        String tmpTipoEntita = null;
        switch (tipoEntitaSacer) {
        case UNI_DOC:
            tmpTipoEntita = "u.idUnitaDoc";
            break;
        case DOC:
            tmpTipoEntita = "u.idDoc";
            break;
        case COMP:
            tmpTipoEntita = "u.idCompDoc";
            break;
        }

        String queryStr = String
                .format("SELECT u FROM AroVLisDatiSpec u " + "WHERE %s = :id " + "AND u.tiUsoXsd = :tipoDatiSpecIn "
                        + "AND u.tiEntitaSacer = :tipoEntitaSacerIn " + "ORDER BY u.niOrdAttrib", tmpTipoEntita);

        Query query = this.getEntityManager().createQuery(queryStr);

        query.setParameter("id", identificativo);
        query.setParameter("tipoDatiSpecIn", tipoDatiSpec);
        query.setParameter("tipoEntitaSacerIn", tipoEntitaSacer.name());

        query.setMaxResults(maxResults);
        List listaDatiSpec = query.getResultList();

        AroVLisDatiSpecTableBean listaTableBean = new AroVLisDatiSpecTableBean();
        try {
            if (listaDatiSpec != null && !listaDatiSpec.isEmpty()) {
                listaTableBean = (AroVLisDatiSpecTableBean) Transform.entities2TableBean(listaDatiSpec);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaTableBean;
    }

    public String getUrnCalc(long idud) {
        AroUnitaDoc aud = getEntityManager().find(AroUnitaDoc.class, idud);
        return "urn_" + aud.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente() + "_"
                + aud.getOrgStrut().getOrgEnte().getNmEnte() + "_" + aud.getOrgStrut().getNmStrut() + "_"
                + aud.getCdRegistroKeyUnitaDoc() + "-" + aud.getAaKeyUnitaDoc().toString() + "-"
                + aud.getCdKeyUnitaDoc();
    }

    public List<String> getStatiDoc(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT DISTINCT (u.tiStatoDoc) FROM AroDoc u WHERE u.aroUnitaDoc.idUnitaDoc = :idunitadoc";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idunitadoc", idUnitaDoc);

        List<String> stati = query.getResultList();
        return stati;
    }

    private void setStatiDocumentari(BaseRow riga) {
        BigDecimal idUnitaDoc = riga.getBigDecimal("id_unita_doc");
        riga.setObject("fl_doc_in_volume_aperto", "0");
        riga.setObject("fl_doc_in_volume_chiuso", "0");
        riga.setObject("fl_doc_in_volume_in_errore", "0");
        riga.setObject("fl_doc_in_volume_da_chiudere", "0");
        StringBuilder stati = new StringBuilder();
        for (String stato : this.getStatiDoc(idUnitaDoc)) {
            if (StringUtils.isNotBlank(stato)) {
                stati.append(stato).append(", ");
                if (stato.equals(DocStatusEnum.IN_VOLUME_APERTO.name())) {
                    riga.setObject("fl_doc_in_volume_aperto", "1");
                } else if (stato.equals(DocStatusEnum.IN_VOLUME_CHIUSO.name())) {
                    riga.setObject("fl_doc_in_volume_chiuso", "1");
                } else if (stato.equals(DocStatusEnum.IN_VOLUME_IN_ERRORE.name())) {
                    riga.setObject("fl_doc_in_volume_in_errore", "1");
                } else if (stato.equals(DocStatusEnum.IN_VOLUME_DA_CHIUDERE.name())) {
                    riga.setObject("fl_doc_in_volume_da_chiudere", "1");
                }
            }
        }
        String stato = StringUtils.chomp(stati.toString(), ", ");
        riga.setObject("ds_stato_unita_doc", stato);
    }

    public DecTipoDocRowBean getDecTipoDocRowBean(BigDecimal idTipoDoc) {
        DecTipoDoc tipoDoc = getEntityManager().find(DecTipoDoc.class, idTipoDoc.longValue());
        DecTipoDocRowBean row = new DecTipoDocRowBean();
        try {
            if (tipoDoc != null) {
                row = (DecTipoDocRowBean) Transform.entity2RowBean(tipoDoc);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del tipo documento: " + e.getMessage(), e);
        }

        return row;
    }

    public DecTipoUnitaDocRowBean getDecTipoUnitaDocRowBean(BigDecimal idTipoUnitaDoc) {
        DecTipoUnitaDoc tipoUnitaDoc = getEntityManager().find(DecTipoUnitaDoc.class, idTipoUnitaDoc.longValue());
        DecTipoUnitaDocRowBean row = new DecTipoUnitaDocRowBean();

        try {
            if (tipoUnitaDoc != null) {
                row = (DecTipoUnitaDocRowBean) Transform.entity2RowBean(tipoUnitaDoc);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del tipo unità documentaria: " + e.getMessage(), e);
        }
        return row;
    }

    public String getTipoSaveFile(BigDecimal idTipoUnitaDoc) {
        DecTipoUnitaDoc tipoUnitaDoc = getEntityManager().find(DecTipoUnitaDoc.class, idTipoUnitaDoc.longValue());
        return tipoUnitaDoc.getTiSaveFile();
    }

    public boolean isDocumentoAggiunto(BigDecimal idDoc) {
        AroDoc doc = getEntityManager().find(AroDoc.class, idDoc.longValue());
        return doc.getTiCreazione().equals(CostantiDB.TipoCreazioneDoc.AGGIUNTA_DOCUMENTO.name());
    }

    public AroVerIndiceAipUdTableBean getAroVerIndiceAipUdTableBean(BigDecimal idUnitaDoc, int maxResults)
            throws EMFError {
        String queryStr = "SELECT u FROM AroVerIndiceAipUd u "
                + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc " + "ORDER BY u.pgVerIndiceAip DESC";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        query.setMaxResults(maxResults);
        List<AroVerIndiceAipUd> listaIndici = query.getResultList();
        AroVerIndiceAipUdTableBean listaIndiciTableBean = new AroVerIndiceAipUdTableBean();
        try {
            if (listaIndici != null && !listaIndici.isEmpty()) {
                listaIndiciTableBean = (AroVerIndiceAipUdTableBean) Transform.entities2TableBean(listaIndici);
                for (AroVerIndiceAipUdRowBean indiceRowBean : listaIndiciTableBean) {
                    // MEV#25918
                    // UNI_DOC_UNISYNCRO (versioni 0.X) o UNI_DOC_UNISYNCRO_V2 (versioni 1.X)
                    // Scompatto il campo cdVerIndiceAip
                    String[] numbers = indiceRowBean.getCdVerIndiceAip().split("[.]");
                    int majorNumber = Integer.parseInt(numbers[0]);
                    String tiFormatoIndiceAip = (majorNumber > 0) ? "UNI SInCRO 2.0 (UNI 11386:2020)"
                            : "UNI SInCRO 1.0 (UNI 11386:2010)";
                    indiceRowBean.setString("ti_formato_indice_aip", tiFormatoIndiceAip);
                    // end MEV#25918
                    if (indiceRowBean.getBigDecimal("id_elenco_vers") != null) {
                        ElvElencoVer elenco = findById(ElvElencoVer.class,
                                indiceRowBean.getBigDecimal("id_elenco_vers"));
                        indiceRowBean.setString("ti_stato_elenco_vers", elenco.getTiStatoElenco());
                    }
                    // EVO#16486
                    if (indiceRowBean.getBigDecimal("id_ver_indice_aip") != null) {
                        AroVerIndiceAipUd indice = findById(AroVerIndiceAipUd.class,
                                indiceRowBean.getBigDecimal("id_ver_indice_aip"));
                        if (indice.getAroUrnVerIndiceAipUds() != null && !indice.getAroUrnVerIndiceAipUds().isEmpty()) {
                            // Recupero lo urn ORIGINALE
                            AroUrnVerIndiceAipUd urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils
                                    .find(indice.getAroUrnVerIndiceAipUds(), new Predicate() {
                                        @Override
                                        public boolean evaluate(final Object object) {
                                            return ((AroUrnVerIndiceAipUd) object).getTiUrn()
                                                    .equals(TiUrnVerIxAipUd.ORIGINALE);
                                        }
                                    });
                            if (urnVerIndiceAipUd != null) {
                                indiceRowBean.setString("urn", urnVerIndiceAipUd.getDsUrn());
                            }
                            // Recupero lo urn NORMALIZZATO
                            urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils
                                    .find(indice.getAroUrnVerIndiceAipUds(), new Predicate() {
                                        @Override
                                        public boolean evaluate(final Object object) {
                                            return ((AroUrnVerIndiceAipUd) object).getTiUrn()
                                                    .equals(TiUrnVerIxAipUd.NORMALIZZATO);
                                        }
                                    });
                            if (urnVerIndiceAipUd != null) {
                                indiceRowBean.setString("urn_normalizzato", urnVerIndiceAipUd.getDsUrn());
                            }
                            // Recupero lo urn INZIALE
                            urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils
                                    .find(indice.getAroUrnVerIndiceAipUds(), new Predicate() {
                                        @Override
                                        public boolean evaluate(final Object object) {
                                            return ((AroUrnVerIndiceAipUd) object).getTiUrn()
                                                    .equals(TiUrnVerIxAipUd.INIZIALE);
                                        }
                                    });
                            if (urnVerIndiceAipUd != null) {
                                indiceRowBean.setString("urn_iniziale", urnVerIndiceAipUd.getDsUrn());
                            }
                        } else {
                            indiceRowBean.setString("urn", indice.getDsUrn());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaIndiciTableBean;
    }

    public AroFileVerIndiceAipUdRowBean getAroFileVerIndiceAipUdRowBean(BigDecimal idVerIndiceAip, int maxResults) {
        String queryStr = "SELECT u FROM AroFileVerIndiceAipUd u "
                + "WHERE u.aroVerIndiceAipUd.idVerIndiceAip = :idVerIndiceAip ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVerIndiceAip", idVerIndiceAip);
        query.setMaxResults(maxResults);
        List<AroFileVerIndiceAipUd> listaFileIndici = query.getResultList();
        AroFileVerIndiceAipUdRowBean fileIndiceRowBean = new AroFileVerIndiceAipUdRowBean();
        try {
            if (listaFileIndici != null && !listaFileIndici.isEmpty()) {
                fileIndiceRowBean = (AroFileVerIndiceAipUdRowBean) Transform.entity2RowBean(listaFileIndici.get(0));
                if (fileIndiceRowBean.getBigDecimal("id_ver_indice_aip") != null) {
                    AroVerIndiceAipUd indice = findById(AroVerIndiceAipUd.class,
                            fileIndiceRowBean.getBigDecimal("id_ver_indice_aip"));
                    if (indice.getAroUrnVerIndiceAipUds() != null && !indice.getAroUrnVerIndiceAipUds().isEmpty()) {
                        // Recupero lo urn ORIGINALE
                        AroUrnVerIndiceAipUd urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils
                                .find(indice.getAroUrnVerIndiceAipUds(), new Predicate() {
                                    @Override
                                    public boolean evaluate(final Object object) {
                                        return ((AroUrnVerIndiceAipUd) object).getTiUrn()
                                                .equals(TiUrnVerIxAipUd.ORIGINALE);
                                    }
                                });
                        if (urnVerIndiceAipUd != null) {
                            fileIndiceRowBean.setString("urn", urnVerIndiceAipUd.getDsUrn());
                        }
                        // Recupero lo urn NORMALIZZATO
                        urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils
                                .find(indice.getAroUrnVerIndiceAipUds(), new Predicate() {
                                    @Override
                                    public boolean evaluate(final Object object) {
                                        return ((AroUrnVerIndiceAipUd) object).getTiUrn()
                                                .equals(TiUrnVerIxAipUd.NORMALIZZATO);
                                    }
                                });
                        if (urnVerIndiceAipUd != null) {
                            fileIndiceRowBean.setString("urn_normalizzato", urnVerIndiceAipUd.getDsUrn());
                        }
                        // Recupero lo urn INZIALE
                        urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils
                                .find(indice.getAroUrnVerIndiceAipUds(), new Predicate() {
                                    @Override
                                    public boolean evaluate(final Object object) {
                                        return ((AroUrnVerIndiceAipUd) object).getTiUrn()
                                                .equals(TiUrnVerIxAipUd.INIZIALE);
                                    }
                                });
                        if (urnVerIndiceAipUd != null) {
                            fileIndiceRowBean.setString("urn_iniziale", urnVerIndiceAipUd.getDsUrn());
                        }
                    } else {
                        fileIndiceRowBean.setString("urn", indice.getDsUrn());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fileIndiceRowBean;
    }

    public AroVLisVolCorTableBean getAroVLisVolCorViewBean(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT u FROM AroVLisVolCor u " + "WHERE u.idUnitaDoc = :idUnitaDoc "
                + "ORDER BY u.dtCreazione DESC ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVLisVolCor> listaVolumi = query.getResultList();
        AroVLisVolCorTableBean volumiTableBean = new AroVLisVolCorTableBean();
        try {
            if (listaVolumi != null && !listaVolumi.isEmpty()) {
                volumiTableBean = (AroVLisVolCorTableBean) Transform.entities2TableBean(listaVolumi);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return volumiTableBean;
    }

    public AroVLisElvVerTableBean getAroVLisElvVerViewBean(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT u FROM AroVLisElvVer u " + "WHERE u.idUnitaDoc = :idUnitaDoc "
                + "ORDER BY u.dtCreazioneElenco DESC ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVLisElvVer> listaElenchi = query.getResultList();
        AroVLisElvVerTableBean elenchiTableBean = new AroVLisElvVerTableBean();
        try {
            if (listaElenchi != null && !listaElenchi.isEmpty()) {
                elenchiTableBean = (AroVLisElvVerTableBean) Transform.entities2TableBean(listaElenchi);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return elenchiTableBean;
    }

    public Long getLastIdUnitaDocAnnullata(BigDecimal idStrut, String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc) {
        String queryStr = "SELECT u.idUnitaDoc FROM AroUnitaDoc u " + "WHERE u.orgStrut.idStrut = :idStrut "
                + "AND u.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc " + "AND u.aaKeyUnitaDoc = :aaKeyUnitaDoc "
                + "AND u.cdKeyUnitaDoc = :cdKeyUnitaDoc " + "AND u.dtAnnul != :dtAnnul " + "ORDER BY u.dtAnnul DESC ";
        List<Long> listaUdAnnullate = new ArrayList<>();
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        query.setParameter("dtAnnul", cal.getTime());
        listaUdAnnullate = (List<Long>) query.getResultList();
        // Posso avere più di una ud annullata, dunque prendo l'ultima
        if (listaUdAnnullate != null && !listaUdAnnullate.isEmpty()) {
            return listaUdAnnullate.get(0);
        } else {
            return null;
        }
    }

    public Long getIdUnitaDocVersataNoAnnul(BigDecimal idStrut, String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc) {
        String queryStr = "SELECT u.idUnitaDoc FROM AroUnitaDoc u " + "WHERE u.orgStrut.idStrut = :idStrut "
                + "AND u.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc " + "AND u.aaKeyUnitaDoc = :aaKeyUnitaDoc "
                + "AND u.cdKeyUnitaDoc = :cdKeyUnitaDoc " + "AND u.dtAnnul = :dtAnnul ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        query.setParameter("dtAnnul", cal.getTime());
        List<Long> listaUdVersate = (List<Long>) query.getResultList();
        if (listaUdVersate != null && !listaUdVersate.isEmpty()) {
            return listaUdVersate.get(0);
        } else {
            return null;
        }
    }

    /**
     * Controlla che l’unità documentaria identificata dalla struttura versante, registro, anno e numero esista
     *
     * (NOTA: una unità doc può essere annullata più di una volta, per questo il conteggio può essere superiore a 1)
     *
     * @param idStrut
     *            id struttura
     * @param cdRegistroKeyUnitaDoc
     *            chiave registro unita doc
     * @param aaKeyUnitaDoc
     *            anno unita doc
     * @param cdKeyUnitaDoc
     *            numero unita doc
     * 
     * @return true/false
     */
    public boolean existAroUnitaDoc(BigDecimal idStrut, String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc) {
        String queryStr = "SELECT COUNT(u) FROM AroUnitaDoc u " + "WHERE u.orgStrut.idStrut = :idStrut "
                + "AND u.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc " + "AND u.aaKeyUnitaDoc = :aaKeyUnitaDoc "
                + "AND u.cdKeyUnitaDoc = :cdKeyUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        Long numUd = (Long) query.getSingleResult();
        return numUd > 0;
    }

    public BaseTable getSerieAppartenenzaList(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT verSerie.idVerSerie, serie.cdCompositoSerie, serie.aaSerie, serie.dsSerie, verSerie.cdVerSerie, statoVerSerie.tiStatoVerSerie, statoSerie.tiStatoSerie "
                + "FROM AroUdAppartVerSerie udAppartVerSerie "
                + "JOIN udAppartVerSerie.serContenutoVerSerie contenutoVerSerie "
                + "JOIN contenutoVerSerie.serVerSerie verSerie " + "JOIN verSerie.serSerie serie "
                + "JOIN verSerie.serStatoVerSeries statoVerSerie " + "JOIN serie.serStatoSeries statoSerie "
                + "WHERE udAppartVerSerie.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND contenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' "
                + "AND verSerie.pgVerSerie = (SELECT MAX(ver.pgVerSerie) FROM SerVerSerie ver WHERE ver.idVerSerie = verSerie.idVerSerie) "
                + "AND verSerie.idStatoVerSerieCor = statoVerSerie.idStatoVerSerie "
                + "AND serie.idStatoSerieCor = statoSerie.idStatoSerie " + "ORDER BY serie.aaSerie ASC ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<Object[]> listaObj = (List<Object[]>) query.getResultList();
        BaseTable listaSerieAppartenenza = new BaseTable();

        try {
            if (listaObj != null && !listaObj.isEmpty()) {
                for (Object[] obj : listaObj) {
                    SerieAppartenenzaRowBean bean = new SerieAppartenenzaRowBean();
                    bean.setIdVerSerie(new BigDecimal(obj[0].toString()));
                    bean.setCdCompositoSerie((String) obj[1]);
                    bean.setAaSerie((BigDecimal) obj[2]);
                    bean.setDsSerie((String) obj[3]);
                    bean.setCdVerSerie((String) obj[4]);
                    bean.setTiStatoVerSerie((String) obj[5]);
                    bean.setTiStatoSerie((String) obj[6]);
                    listaSerieAppartenenza.add(bean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaSerieAppartenenza;
    }

    public AroVLisFascTableBean getFascicoliAppartenenzaViewBean(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.AroVLisFasc(a.idFascicolo, a.aaFascicolo, a.cdKeyFascicolo, a.nmTipoFascicolo, "
                + "a.dtApeFascicolo, a.dtChiuFascicolo, a.cdCompositoVoceTitol, a.niUnitaDoc, a.tsIniSes, "
                + "a.tiEsito, a.tiStatoFascElencoVers, a.tiStatoConservazione) " + "FROM AroVLisFasc a "
                + "WHERE a.idUnitaDoc = :idUnitaDoc " + "ORDER BY a.aaFascicolo, a.cdKeyFascicolo ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVLisFasc> listaFascAppartenenza = query.getResultList();
        AroVLisFascTableBean fascicoliTableBean = new AroVLisFascTableBean();
        try {
            if (listaFascAppartenenza != null && !listaFascAppartenenza.isEmpty()) {
                fascicoliTableBean = (AroVLisFascTableBean) Transform.entities2TableBean(listaFascAppartenenza);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fascicoliTableBean;
    }

    public AroUpdUnitaDocTableBean getAggiornamentiMetadatiTableBean(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT u FROM AroUpdUnitaDoc u " + "WHERE u.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "ORDER BY u.tsIniSes ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroUpdUnitaDoc> listaUpd = query.getResultList();
        AroUpdUnitaDocTableBean updTableBean = new AroUpdUnitaDocTableBean();
        try {
            if (listaUpd != null && !listaUpd.isEmpty()) {
                updTableBean = (AroUpdUnitaDocTableBean) Transform.entities2TableBean(listaUpd);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return updTableBean;
    }

    // MEV#24597
    public AroVLisNotaUnitaDocTableBean getListaNoteUnitaDocumentarieTableBean(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT u FROM AroVLisNotaUnitaDoc u WHERE u.idUnitaDoc = :idUnitaDoc ORDER BY u.niOrd, u.pgNotaUnitaDoc";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVLisNotaUnitaDoc> listaNote = query.getResultList();
        AroVLisNotaUnitaDocTableBean noteTableBean = new AroVLisNotaUnitaDocTableBean();
        try {
            if (listaNote != null && !listaNote.isEmpty()) {
                noteTableBean = (AroVLisNotaUnitaDocTableBean) Transform.entities2TableBean(listaNote);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return noteTableBean;
    }

    public List<AroVLisNotaUnitaDoc> getAroVLisNotaUnitaDoc(BigDecimal idUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM AroVLisNotaUnitaDoc u WHERE u.idUnitaDoc = :idUnitaDoc ORDER BY u.niOrd, u.pgNotaUnitaDoc");
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVLisNotaUnitaDoc> list = query.getResultList();
        return list;
    }

    public BigDecimal getMaxPgNota(BigDecimal idUnitaDoc, BigDecimal idTipoNotaUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT MAX(u.pgNotaUnitaDoc) FROM AroVLisNotaUnitaDoc u WHERE u.idUnitaDoc = :idUnitaDoc AND u.idTipoNotaUnitaDoc = :idTipoNotaUnitaDoc ");
        query.setParameter("idUnitaDoc", idUnitaDoc);
        query.setParameter("idTipoNotaUnitaDoc", idTipoNotaUnitaDoc);
        BigDecimal pg = (BigDecimal) query.getSingleResult();
        return pg != null ? pg : BigDecimal.ZERO;
    }

    public List<DecTipoNotaUnitaDoc> getDecTipoNotaUnitaDocList() {

        List<DecTipoNotaUnitaDoc> result = null;
        Query query = getEntityManager().createQuery(
                "Select tnud from DecTipoNotaUnitaDoc tnud where tnud.flMolt='1' order by tnud.cdTipoNotaUnitaDoc");
        result = query.getResultList();
        return result;
    }

    public List<DecTipoNotaUnitaDoc> getDecTipoNotaUnitaDocListByIdUserIam(long idUtente) {
        UsrUser user = this.findById(UsrUser.class, idUtente);

        List<DecTipoNotaUnitaDoc> decTipoNotaUnitaDocList = null;
        Query query = getEntityManager().createQuery(
                "Select tnud from DecTipoNotaUnitaDoc tnud where tnud.flMolt='1' order by tnud.cdTipoNotaUnitaDoc");
        decTipoNotaUnitaDocList = query.getResultList();

        if (decTipoNotaUnitaDocList != null && !decTipoNotaUnitaDocList.isEmpty()) {
            if (TiEnteConvenz.PRODUTTORE.equals(user.getSiOrgEnteSiam().getTiEnteConvenz())
                    || TiEnteConvenz.GESTORE.equals(user.getSiOrgEnteSiam().getTiEnteConvenz())) {
                CollectionUtils.filter(decTipoNotaUnitaDocList, new Predicate() {
                    @Override
                    public boolean evaluate(final Object object) {
                        return ((DecTipoNotaUnitaDoc) object).getCdTipoNotaUnitaDoc().equals("NOTE_PRODUTTORE");
                    }
                });
            }
        }

        return decTipoNotaUnitaDocList;
    }

    public DecTipoNotaUnitaDocTableBean getDecTipoNotaUnitaDocNotInUnitaDocTableBean(long idUtente,
            BigDecimal idUnitaDoc) {
        DecTipoNotaUnitaDocTableBean table = new DecTipoNotaUnitaDocTableBean();
        table.addSortingRule("ni_ord", SortingRule.ASC);
        List<DecTipoNotaUnitaDoc> decTipoNotaUnitaDocList = this.getDecTipoNotaUnitaDocListByIdUserIam(idUtente);
        List<DecTipoNotaUnitaDoc> listNotInUnitaDoc = this.getDecTipoNotaUnitaDocNotInUnitaDocByIdUserIam(idUtente,
                idUnitaDoc);
        try {
            if (decTipoNotaUnitaDocList != null && !decTipoNotaUnitaDocList.isEmpty()) {
                for (DecTipoNotaUnitaDoc row : decTipoNotaUnitaDocList) {
                    DecTipoNotaUnitaDocRowBean rowBean = (DecTipoNotaUnitaDocRowBean) Transform.entity2RowBean(row);
                    if (rowBean.getFlObblig().equals("1")) {
                        rowBean.setDsTipoNotaUnitaDoc(rowBean.getDsTipoNotaUnitaDoc() + " (OBBLIGATORIO)");
                    }
                    table.add(rowBean);
                }
            }
            if (listNotInUnitaDoc != null && !listNotInUnitaDoc.isEmpty()) {
                for (DecTipoNotaUnitaDoc row : listNotInUnitaDoc) {
                    DecTipoNotaUnitaDocRowBean rowBean = (DecTipoNotaUnitaDocRowBean) Transform.entity2RowBean(row);
                    if (rowBean.getFlObblig().equals("1")) {
                        rowBean.setDsTipoNotaUnitaDoc(rowBean.getDsTipoNotaUnitaDoc() + " (OBBLIGATORIO)");
                    }
                    table.add(rowBean);
                }
            }
            table.sort();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero della lista tipi di nota delle unit\u00e0 documentarie "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return table;
    }

    public List<DecTipoNotaUnitaDoc> getDecTipoNotaUnitaDocNotInUnitaDoc(BigDecimal idUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "Select tnud from DecTipoNotaUnitaDoc tnud where tnud.flMolt='0' AND NOT EXISTS ( select n from AroNotaUnitaDoc n where n.decTipoNotaUnitaDoc.idTipoNotaUnitaDoc=tnud.idTipoNotaUnitaDoc and n.aroUnitaDoc.idUnitaDoc = :idUnitaDoc)");
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<DecTipoNotaUnitaDoc> result = query.getResultList();
        return result;
    }

    public List<DecTipoNotaUnitaDoc> getDecTipoNotaUnitaDocNotInUnitaDocByIdUserIam(long idUtente,
            BigDecimal idUnitaDoc) {
        UsrUser user = this.findById(UsrUser.class, idUtente);

        Query query = getEntityManager().createQuery(
                "Select tnud from DecTipoNotaUnitaDoc tnud where tnud.flMolt='0' AND NOT EXISTS ( select n from AroNotaUnitaDoc n where n.decTipoNotaUnitaDoc.idTipoNotaUnitaDoc=tnud.idTipoNotaUnitaDoc and n.aroUnitaDoc.idUnitaDoc = :idUnitaDoc)");
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<DecTipoNotaUnitaDoc> listNotInUnitaDoc = query.getResultList();

        if (listNotInUnitaDoc != null && !listNotInUnitaDoc.isEmpty()) {
            if (TiEnteConvenz.PRODUTTORE.equals(user.getSiOrgEnteSiam().getTiEnteConvenz())
                    || TiEnteConvenz.GESTORE.equals(user.getSiOrgEnteSiam().getTiEnteConvenz())) {
                CollectionUtils.filter(listNotInUnitaDoc, new Predicate() {
                    @Override
                    public boolean evaluate(final Object object) {
                        return ((DecTipoNotaUnitaDoc) object).getCdTipoNotaUnitaDoc().equals("NOTE_PRODUTTORE");
                    }
                });
            }
        }

        return listNotInUnitaDoc;
    }

    public DecTipoNotaUnitaDocTableBean getDecTipoNotaUnitaDocNotInVerIndiceAipTableBean(BigDecimal idVerIndiceAip) {
        DecTipoNotaUnitaDocTableBean table = new DecTipoNotaUnitaDocTableBean();
        table.addSortingRule("ni_ord", SortingRule.ASC);
        List<DecTipoNotaUnitaDoc> decTipoNotaUnitaDocList = this.getDecTipoNotaUnitaDocList();
        List<DecTipoNotaUnitaDoc> listNotInVerIndiceAip = this.getDecTipoNotaUnitaDocNotInVerIndiceAip(idVerIndiceAip);
        try {
            if (decTipoNotaUnitaDocList != null && !decTipoNotaUnitaDocList.isEmpty()) {
                for (DecTipoNotaUnitaDoc row : decTipoNotaUnitaDocList) {
                    DecTipoNotaUnitaDocRowBean rowBean = (DecTipoNotaUnitaDocRowBean) Transform.entity2RowBean(row);
                    if (rowBean.getFlObblig().equals("1")) {
                        rowBean.setDsTipoNotaUnitaDoc(rowBean.getDsTipoNotaUnitaDoc() + " (OBBLIGATORIO)");
                    }
                    table.add(rowBean);
                }
            }
            if (listNotInVerIndiceAip != null && !listNotInVerIndiceAip.isEmpty()) {
                for (DecTipoNotaUnitaDoc row : listNotInVerIndiceAip) {
                    DecTipoNotaUnitaDocRowBean rowBean = (DecTipoNotaUnitaDocRowBean) Transform.entity2RowBean(row);
                    if (rowBean.getFlObblig().equals("1")) {
                        rowBean.setDsTipoNotaUnitaDoc(rowBean.getDsTipoNotaUnitaDoc() + " (OBBLIGATORIO)");
                    }
                    table.add(rowBean);
                }
            }
            table.sort();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero della lista tipi di nota delle unit\u00e0 documentarie "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return table;
    }

    public List<DecTipoNotaUnitaDoc> getDecTipoNotaUnitaDocNotInVerIndiceAip(BigDecimal idVerIndiceAip) {
        Query query = getEntityManager().createQuery(
                "Select tnud from DecTipoNotaUnitaDoc tnud where tnud.flMolt='0' AND NOT EXISTS ( select n from AroNotaUnitaDoc n where n.decTipoNotaUnitaDoc.idTipoNotaUnitaDoc=tnud.idTipoNotaUnitaDoc and n.aroVerIndiceAipUd.idVerIndiceAip = :idVerIndiceAip)");
        query.setParameter("idVerIndiceAip", idVerIndiceAip);
        List<DecTipoNotaUnitaDoc> result = query.getResultList();
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal saveNota(long idUtente, BigDecimal idUnitaDoc, BigDecimal idTipoNotaUnitaDoc, BigDecimal pgNota,
            String dsNota, Date dtNota) throws ParerUserError {
        log.debug("Eseguo il salvataggio della nota");
        BigDecimal id = null;
        try {
            AroUnitaDoc ud = this.findById(AroUnitaDoc.class, idUnitaDoc);
            if (ud.getAroNotaUnitaDocs() == null) {
                ud.setAroNotaUnitaDocs(new ArrayList<>());
            }
            AroNotaUnitaDoc nota = new AroNotaUnitaDoc();
            nota.setDecTipoNotaUnitaDoc(this.findById(DecTipoNotaUnitaDoc.class, idTipoNotaUnitaDoc));
            nota.setIamUser(this.findById(IamUser.class, idUtente));
            nota.setPgNotaUnitaDoc(pgNota);
            nota.setDsNotaUnitaDoc(dsNota);
            nota.setDtNotaUnitaDoc(dtNota);

            ud.addAroNotaUnitaDoc(nota);
            this.insertEntity(nota, true);
            id = new BigDecimal(nota.getIdNotaUnitaDoc());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio della nota ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            log.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
        return id;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveNota(BigDecimal idNota, String dsNota, long idUtente, Date dtNota) throws ParerUserError {
        log.debug("Eseguo il salvataggio della nota");
        try {
            AroNotaUnitaDoc nota = this.findById(AroNotaUnitaDoc.class, idNota);
            nota.setDsNotaUnitaDoc(dsNota);
            nota.setIamUser(this.findById(IamUser.class, idUtente));
            nota.setDtNotaUnitaDoc(dtNota);
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio della nota ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            log.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteNota(BigDecimal idNotaUnitaDoc) throws ParerUserError {
        log.debug("Eseguo l'eliminazione della nota");
        try {
            AroNotaUnitaDoc nota = this.findById(AroNotaUnitaDoc.class, idNotaUnitaDoc);
            this.removeEntity(nota, true);
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nell'eliminazione della nota ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            log.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    public AroVLisNotaUnitaDocTableBean getAroVLisNotaUnitaDocTableBean(BigDecimal idUnitaDoc) {
        AroVLisNotaUnitaDocTableBean table = new AroVLisNotaUnitaDocTableBean();
        List<AroVLisNotaUnitaDoc> list = this.getAroVLisNotaUnitaDoc(idUnitaDoc);
        if (list != null && !list.isEmpty()) {
            try {
                table = (AroVLisNotaUnitaDocTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Errore durante il recupero della lista note delle unit\u00e0 documentarie "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return table;
    }

    public DecTipoNotaUnitaDocTableBean getSingleDecTipoNotaUnitaDocTableBean(BigDecimal idTipoNotaUnitaDoc) {
        DecTipoNotaUnitaDocTableBean table = new DecTipoNotaUnitaDocTableBean();
        DecTipoNotaUnitaDoc nota = this.findById(DecTipoNotaUnitaDoc.class, idTipoNotaUnitaDoc);
        try {
            DecTipoNotaUnitaDocRowBean rowBean = (DecTipoNotaUnitaDocRowBean) Transform.entity2RowBean(nota);
            table.add(rowBean);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero della lista tipi di nota delle unit\u00e0 documentarie "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return table;
    }

    public AroVVisNotaUnitaDocRowBean getAroVVisNotaUnitaDocRowBean(BigDecimal idNotaUnitaDoc) throws EMFError {
        String queryStr = "SELECT u FROM AroVVisNotaUnitaDoc u WHERE u.idNotaUnitaDoc = :idNotaUnitaDoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idNotaUnitaDoc", idNotaUnitaDoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisNotaUnitaDoc> nota = query.getResultList();

        AroVVisNotaUnitaDocRowBean notaRowBean = new AroVVisNotaUnitaDocRowBean();
        try {
            if (nota != null && !nota.isEmpty()) {
                notaRowBean = (AroVVisNotaUnitaDocRowBean) Transform.entity2RowBean(nota.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return notaRowBean;
    }

    public AroVerIndiceAipUdRowBean retrieveVersioneIndiceAipUdById(long idVerIndiceAip) {
        AroVerIndiceAipUd verIndiceAipUd = getEntityManager().find(AroVerIndiceAipUd.class, idVerIndiceAip);

        AroVerIndiceAipUdRowBean verIndiceAipUdRowBean = new AroVerIndiceAipUdRowBean();
        try {
            if (verIndiceAipUd != null) {
                verIndiceAipUdRowBean = (AroVerIndiceAipUdRowBean) Transform.entity2RowBean(verIndiceAipUd);

                // UNI_DOC_UNISYNCRO (versioni 0.X) o UNI_DOC_UNISYNCRO_V2 (versioni 1.X)
                // Scompatto il campo cdVerIndiceAip
                String[] numbers = verIndiceAipUdRowBean.getCdVerIndiceAip().split("[.]");
                int majorNumber = Integer.parseInt(numbers[0]);
                String tiFormatoIndiceAip = (majorNumber > 0) ? "UNI SInCRO 2.0 (UNI 11386:2020)"
                        : "UNI SInCRO 1.0 (UNI 11386:2010)";
                verIndiceAipUdRowBean.setString("ti_formato_indice_aip", tiFormatoIndiceAip);
                // end MEV#25918
                if (verIndiceAipUdRowBean.getBigDecimal("id_elenco_vers") != null) {
                    ElvElencoVer elenco = findById(ElvElencoVer.class,
                            verIndiceAipUdRowBean.getBigDecimal("id_elenco_vers"));
                    verIndiceAipUdRowBean.setString("ti_stato_elenco_vers", elenco.getTiStatoElenco());
                }

                if (verIndiceAipUdRowBean.getBigDecimal("id_ver_indice_aip") != null) {
                    AroVerIndiceAipUd indice = findById(AroVerIndiceAipUd.class,
                            verIndiceAipUdRowBean.getBigDecimal("id_ver_indice_aip"));
                    if (indice.getAroUrnVerIndiceAipUds() != null && !indice.getAroUrnVerIndiceAipUds().isEmpty()) {
                        // Recupero lo urn ORIGINALE
                        AroUrnVerIndiceAipUd urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils
                                .find(indice.getAroUrnVerIndiceAipUds(), new Predicate() {
                                    @Override
                                    public boolean evaluate(final Object object) {
                                        return ((AroUrnVerIndiceAipUd) object).getTiUrn()
                                                .equals(TiUrnVerIxAipUd.ORIGINALE);
                                    }
                                });
                        if (urnVerIndiceAipUd != null) {
                            verIndiceAipUdRowBean.setString("urn", urnVerIndiceAipUd.getDsUrn());
                        }
                        // Recupero lo urn NORMALIZZATO
                        urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils
                                .find(indice.getAroUrnVerIndiceAipUds(), new Predicate() {
                                    @Override
                                    public boolean evaluate(final Object object) {
                                        return ((AroUrnVerIndiceAipUd) object).getTiUrn()
                                                .equals(TiUrnVerIxAipUd.NORMALIZZATO);
                                    }
                                });
                        if (urnVerIndiceAipUd != null) {
                            verIndiceAipUdRowBean.setString("urn_normalizzato", urnVerIndiceAipUd.getDsUrn());
                        }
                        // Recupero lo urn INZIALE
                        urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils
                                .find(indice.getAroUrnVerIndiceAipUds(), new Predicate() {
                                    @Override
                                    public boolean evaluate(final Object object) {
                                        return ((AroUrnVerIndiceAipUd) object).getTiUrn()
                                                .equals(TiUrnVerIxAipUd.INIZIALE);
                                    }
                                });
                        if (urnVerIndiceAipUd != null) {
                            verIndiceAipUdRowBean.setString("urn_iniziale", urnVerIndiceAipUd.getDsUrn());
                        }
                    } else {
                        verIndiceAipUdRowBean.setString("urn", indice.getDsUrn());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return verIndiceAipUdRowBean;
    }

    public boolean isUserAppartAllOk(long idUtente) {
        boolean isOk = false;

        UsrUser user = getEntityManager().find(UsrUser.class, idUtente);
        TiEnteConvenz tiEnteConvenz = user.getSiOrgEnteSiam().getTiEnteConvenz();
        if (TiEnteConvenz.AMMINISTRATORE.equals(tiEnteConvenz) || TiEnteConvenz.CONSERVATORE.equals(tiEnteConvenz)) {
            isOk = true;
        }

        return isOk;
    }
    // end MEV#24597

    public List<Object[]> progressivoEsistenteUnitaDocList(BigDecimal idStrut, BigDecimal idUnitaDoc,
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, BigDecimal nuovoProgressivo) {
        String queryStr = "SELECT unitaDoc FROM AroUnitaDoc unitaDoc "
                + "WHERE unitaDoc.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc "
                + "AND unitaDoc.aaKeyUnitaDoc = :aaKeyUnitaDoc " + "AND unitaDoc.pgUnitaDoc = :nuovoProgressivo "
                + "AND unitaDoc.orgStrut.idStrut = :idStrut " + "AND unitaDoc.idUnitaDoc != :idUnitaDoc "
                + "AND unitaDoc.dtAnnul = :dtAnnul ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc.longValue());
        query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        query.setParameter("idStrut", idStrut.longValue());
        query.setParameter("nuovoProgressivo", nuovoProgressivo);
        Calendar deadline = Calendar.getInstance();
        deadline.set(2444, 11, 31, 0, 0, 0);
        query.setParameter("dtAnnul", deadline.getTime());
        List<AroUnitaDoc> unitaDocList = (List<AroUnitaDoc>) query.getResultList();
        List<Object[]> regAnnoNumeroObjList = new ArrayList<>();
        if (!unitaDocList.isEmpty()) {
            for (AroUnitaDoc unitaDoc : unitaDocList) {
                regAnnoNumeroObjList.add(new Object[] { unitaDoc.getCdRegistroKeyUnitaDoc(),
                        unitaDoc.getAaKeyUnitaDoc(), unitaDoc.getCdKeyUnitaDoc() });
            }
        }
        return regAnnoNumeroObjList;
    }

    public void salvaAssegnaProgressivo(BigDecimal idUnitaDoc, BigDecimal pgUnitaDoc) {
        String queryStr = "UPDATE AroUnitaDoc unitaDoc SET unitaDoc.pgUnitaDoc = :pgUnitaDoc "
                + "WHERE unitaDoc.idUnitaDoc = :idUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc.longValue());
        query.setParameter("pgUnitaDoc", pgUnitaDoc);
        query.executeUpdate();
    }

    public BigDecimal getPgUnitaDoc(BigDecimal idUnitaDoc) {
        return ((AroUnitaDoc) getEntityManager().find(AroUnitaDoc.class, idUnitaDoc.longValue())).getPgUnitaDoc();
    }

    public boolean existAroUnitaDocsFromRegistro(BigDecimal idRegistroUnitaDoc, BigDecimal idStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(aroUnitaDoc) FROM AroUnitaDoc aroUnitaDoc WHERE aroUnitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc AND aroUnitaDoc.decRegistroUnitaDoc.orgStrut.idStrut = :idStrut ");
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        query.setParameter("idStrut", idStrut);
        Long result = (Long) query.getSingleResult();
        return (result != null) ? result > 0L : false;
    }

    public boolean existAroUnitaDocsFromTipoUnita(BigDecimal idTipoUnitaDoc, BigDecimal idStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(aroUnitaDoc) FROM AroUnitaDoc aroUnitaDoc WHERE aroUnitaDoc.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc AND aroUnitaDoc.decTipoUnitaDoc.orgStrut.idStrut = :idStrut");
        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        query.setParameter("idStrut", idStrut);
        Long result = (Long) query.getSingleResult();
        return (result != null) ? result > 0L : false;
    }

    public boolean existsRelationsWithUnitaDoc(long idTipoDato, Constants.TipoDato tipoDato) {
        StringBuilder queryStr = new StringBuilder("SELECT COUNT(unitaDoc) FROM AroUnitaDoc unitaDoc ");
        switch (tipoDato) {
        case REGISTRO:
            queryStr.append("WHERE unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc = :idTipoDato ");
            break;
        case TIPO_UNITA_DOC:
            queryStr.append("WHERE unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoDato ");
            break;
        case TIPO_DOC:
            queryStr = new StringBuilder("SELECT COUNT(unitaDoc) FROM AroDoc doc " + "JOIN doc.aroUnitaDoc unitaDoc "
                    + "WHERE doc.decTipoDoc.idTipoDoc = :idTipoDato ");
            break;
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idTipoDato", idTipoDato);
        return (Long) query.getSingleResult() > 0;
    }

    /*
     * // Restituisce l'UD e se non è definito il numero normalizzato lo calcola e lo registra public AroUnitaDoc
     * getAndCalculateUDNumber(BigDecimal id) { AroUnitaDoc ud=findById(AroUnitaDoc.class, id); if (ud!=null &&
     * ud.getCdKeyUnitaDocNormaliz()==null) { String
     * numeroNormalizzato=Utils.getNormalizedUDCode(ud.getCdKeyUnitaDoc());
     * ud.setCdKeyUnitaDocNormaliz(numeroNormalizzato); } return ud; }
     */

    /*
     * Controlla se esieste un numeroNormalizzato nell'ambito della stessa struttura/anno/numero registro.
     */
    public boolean existsUdWithSameNormalizedNumber(long idRegistroUnitaDoc, BigDecimal annoRegistro,
            String numeroNormalizzato, String numeroNonNormalizzato) {
        Query query = getEntityManager().createQuery("SELECT  COUNT(aroUnitaDoc) FROM AroUnitaDoc aroUnitaDoc " + // "WHERE
                                                                                                                  // aroUnitaDoc.orgStrut=:orgStrut
                                                                                                                  // " +
                "WHERE   aroUnitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc=:idRegistroUnitaDoc "
                + "AND     aroUnitaDoc.aaKeyUnitaDoc=:annoRegistro " + // "AND
                                                                       // aroUnitaDoc.cdRegistroKeyUnitaDoc=:codiceRegistro
                                                                       // " +
                "AND     aroUnitaDoc.cdKeyUnitaDocNormaliz=:numeroNormalizzato "
                + "AND     aroUnitaDoc.cdKeyUnitaDoc!=:numeroNonNormalizzato ");
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        query.setParameter("annoRegistro", annoRegistro);
        query.setParameter("numeroNormalizzato", numeroNormalizzato);
        query.setParameter("numeroNonNormalizzato", numeroNonNormalizzato);
        Long result = (Long) query.getSingleResult();
        return (result != null) ? result > 1L : false;
    }

    /*
     * Data una UD torna la lista dei suoi AroDoc ordinati per: - Tipo documento principale e poi tutti gli altri -
     * Tutti gli altri ordinati per Data Creazione e nella stessa data per tipo documento e nello stesso tipo per
     * progressivo
     */
    public List<AroDoc> getAndSetAroDocOrderedByTypeAndDateProg(AroUnitaDoc aroUnitaDoc) {
        List<AroDoc> listaDoc = aroUnitaDoc.getAroDocs();
        ArrayList<AroDoc> alDef = null;
        if (listaDoc != null) {

            AroDoc aroDocPrinc = null;
            ArrayList<AroDoc> alNew = new ArrayList();
            for (AroDoc aroDoc : listaDoc) {
                if (aroDoc.getTiDoc().equals("PRINCIPALE")) {
                    aroDocPrinc = aroDoc; // memorizza per dopo il doc PRINCIPALE
                } else {
                    alNew.add(aroDoc);
                }
            }
            // Ordina gli elementi tranne il PRINCIPALE...
            Collections.sort(alNew, new Comparator<AroDoc>() {
                @Override
                public int compare(AroDoc doc1, AroDoc doc2) {
                    int comparazionePerData = doc1.getDtCreazione().compareTo(doc2.getDtCreazione());
                    if (comparazionePerData == 0) {
                        int comparazionePerTipo = doc1.getTiDoc().compareTo(doc2.getTiDoc());
                        if (comparazionePerTipo == 0) {
                            return doc1.getPgDoc().compareTo(doc2.getPgDoc());
                        } else {
                            return comparazionePerTipo;
                        }
                    } else {
                        return comparazionePerData;
                    }
                }
            });
            // Mette il PRINCIPALE come primo...
            alDef = new ArrayList();
            if (aroDocPrinc != null) {
                alDef.add(aroDocPrinc);
            }
            for (AroDoc aroDocZ : alNew) {
                alDef.add(aroDocZ);
            }
            // E poi tutti gli altri già ordinati di seguito
            BigDecimal prog = BigDecimal.ONE;
            for (AroDoc aroDocx : alDef) {
                // assegno solo se non presente
                if (aroDocx.getNiOrdDoc() == null) {
                    aroDocx.setNiOrdDoc(prog);

                    getEntityManager().merge(aroDocx);
                }
                // incremento
                prog = prog.add(BigDecimal.ONE);
            }

        }
        return alDef;
    }

    /*
     * restituisce AroCompUrnCalc del tipo passato per il componente passato
     */
    public AroCompUrnCalc findAroCompUrnCalcByType(AroCompDoc aroCompDoc, TiUrn tiUrn) {
        Query query = getEntityManager()
                .createQuery("SELECT  a FROM AroCompUrnCalc a WHERE   a.aroCompDoc = :aroCompDoc AND a.tiUrn = :tiUrn");
        query.setParameter("aroCompDoc", aroCompDoc);
        query.setParameter("tiUrn", tiUrn);
        List<AroCompUrnCalc> l = query.getResultList();
        return (l != null && l.size() > 0) ? l.get(0) : null;
    }

    private String calcAndcheckCdKeyNormalized(AroUnitaDoc aroUnitaDoc, String cdKeyUnitaDocNormaliz) {
        if (this.existsUdWithSameNormalizedNumber(aroUnitaDoc.getIdDecRegistroUnitaDoc(),
                aroUnitaDoc.getAaKeyUnitaDoc(), cdKeyUnitaDocNormaliz, aroUnitaDoc.getCdKeyUnitaDoc())) {
            // retry with another one
            // aggiungere _ infondo e richiamare ricorsivamente lo stesso metodo fino a che la condizione di uscita
            // restituisce l'urn part normalizzato corretto
            cdKeyUnitaDocNormaliz = cdKeyUnitaDocNormaliz.concat("_");
            return this.calcAndcheckCdKeyNormalized(aroUnitaDoc, cdKeyUnitaDocNormaliz);
        }
        // return
        return cdKeyUnitaDocNormaliz;
    }

    public AroCompUrnCalc verifyAndCreateCompUrnCalc(AroCompDoc aroCompDoc, String nomeStruttura,
            String nomeStrutturaNorm, String nomeEnte, String nomeEnteNorm, String nomeSistema, TiUrn tiUrn) {
        AroDoc aroDoc = aroCompDoc.getAroStrutDoc().getAroDoc();
        AroUnitaDoc aroUnitaDoc = aroDoc.getAroUnitaDoc();
        AroCompDoc aroCompDocPadre = aroCompDoc.getAroCompDoc();
        boolean needCheckSottoComp = true;
        // Calcola URN originale
        AroCompUrnCalc aroCompUrnCalc = this.findAroCompUrnCalcByType(aroCompDoc, tiUrn);
        if (aroCompUrnCalc == null) {
            aroCompUrnCalc = new AroCompUrnCalc();
            aroCompUrnCalc.setAroCompDoc(aroCompDoc);
            aroCompUrnCalc.setTiUrn(tiUrn);
            String tmpUrn = null;
            switch (tiUrn) {
            case ORIGINALE:
                tmpUrn = String.format("urn:%s:%s:%s:%s-%s-%s:", nomeSistema, nomeEnte, nomeStruttura,
                        aroUnitaDoc.getCdRegistroKeyUnitaDoc(), aroUnitaDoc.getAaKeyUnitaDoc().longValueExact(),
                        aroUnitaDoc.getCdKeyUnitaDoc());
                break;
            case NORMALIZZATO:
                tmpUrn = String.format("urn:%s:%s:%s:%s-%s-%s:", nomeSistema, nomeEnteNorm, nomeStrutturaNorm,
                        Utils.getNormalizedUDCode(aroUnitaDoc.getCdRegistroKeyUnitaDoc()),
                        aroUnitaDoc.getAaKeyUnitaDoc().longValueExact(),
                        StringUtils.isBlank(aroUnitaDoc.getCdKeyUnitaDocNormaliz())
                                ? Utils.getNormalizedUDCode(aroUnitaDoc.getCdKeyUnitaDoc())
                                : aroUnitaDoc.getCdKeyUnitaDocNormaliz());
                break;
            default:
                tmpUrn = aroCompDoc.getDsUrnCompCalc();
                needCheckSottoComp = false;
                break;
            }
            // more
            if (needCheckSottoComp) {
                if (aroCompDocPadre != null) {
                    // E' UN SOTTOCOMPONENTE
                    tmpUrn += String.format("%05d", aroDoc.getNiOrdDoc().longValueExact()) + ":"
                            + String.format("%05d", aroCompDocPadre.getNiOrdCompDoc().longValueExact()) + ":"
                            + String.format("%02d", aroCompDoc.getNiOrdCompDoc().longValueExact());
                } else {
                    // E' UN COMPONENTE
                    tmpUrn += "DOC" + String.format("%05d", aroDoc.getNiOrdDoc().longValueExact()) + ":"
                            + String.format("%05d", aroCompDoc.getNiOrdCompDoc().longValueExact());
                }
            }
            if (StringUtils.isNotBlank(tmpUrn)) {
                // finally set urn
                aroCompUrnCalc.setDsUrn(tmpUrn);
            }
        }

        return aroCompUrnCalc;
    }

    /**
     * Verifica e calcolo chiave UD Normalizzata e URN Componenti
     * 
     * @param idAroUnitaDoc
     *            id unita doc
     * @param tiUrnToCalculate
     *            tipo urn da calcolare
     */
    public void normalizzaUDAndCalcUrnOrigNormalizComp(long idAroUnitaDoc, List<TiUrn> tiUrnToCalculate) {
        // lock UD
        AroUnitaDoc tmpAroUnitaDoc = me.findByIdWithLock(AroUnitaDoc.class, idAroUnitaDoc);

        OrgStrut tmpOrgStrut = tmpAroUnitaDoc.getOrgStrut();
        OrgEnte tmpOrgEnte = tmpOrgStrut.getOrgEnte();
        String nomeSistema = configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE,
                null, null, null, null, TipoAplVGetValAppart.APPLIC);
        // verifico cd_key
        // calcola e verifica la chiave normalizzata (se non trovato in precedenza)
        if (StringUtils.isBlank(tmpAroUnitaDoc.getCdKeyUnitaDocNormaliz())) {
            String cdKeyNormalized = me.calcAndcheckCdKeyNormalized(tmpAroUnitaDoc,
                    Utils.getNormalizedUDCode(tmpAroUnitaDoc.getCdKeyUnitaDoc()));
            tmpAroUnitaDoc.setCdKeyUnitaDocNormaliz(cdKeyNormalized);
        }
        // per ogni documento/componente se non esiste:
        // calcolo ni_ord_doc
        me.getAndSetAroDocOrderedByTypeAndDateProg(tmpAroUnitaDoc);
        // urn componente ORIGINALE/NORMALIZZATO
        // per ogni documento
        for (AroDoc tmpAroDoc : tmpAroUnitaDoc.getAroDocs()) {
            // update ud on doc
            tmpAroDoc.setAroUnitaDoc(tmpAroUnitaDoc);
            // per ogni componente
            for (Iterator<AroStrutDoc> it = tmpAroDoc.getAroStrutDocs().iterator(); it.hasNext();) {
                AroStrutDoc tmpAroStrutDoc = it.next();
                for (AroCompDoc tmpAroCompDoc : tmpAroStrutDoc.getAroCompDocs()) {
                    // per ogni TiUrn (tranne originale)
                    for (TiUrn tiUrn : tiUrnToCalculate) {
                        // calcolo URN
                        AroCompUrnCalc tmpAroCompUrnCalc = me.verifyAndCreateCompUrnCalc(tmpAroCompDoc,
                                tmpOrgStrut.getNmStrut(), tmpOrgStrut.getCdStrutNormaliz(), tmpOrgEnte.getNmEnte(),
                                tmpOrgEnte.getCdEnteNormaliz(), nomeSistema, tiUrn);
                        // new one
                        if (tmpAroCompDoc.getAroAroCompUrnCalcs() == null) {
                            tmpAroCompDoc.setAroAroCompUrnCalcs(new ArrayList<AroCompUrnCalc>());
                        }
                        // add element
                        tmpAroCompDoc.getAroAroCompUrnCalcs().add(tmpAroCompUrnCalc);
                    } // TiUrn
                }
            }
        }
    }

    // Metodo che restituisce un viewbean con i record trovati in base
    // ai filtri di ricerca passati in ingresso
    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicAnnullateTableBean(
            UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate filtri, List<BigDecimal> idTipoUnitaDocList,
            Set<String> cdRegistroUnitaDocSet, List<BigDecimal> idTipoDocList, Date[] dateAcquisizioneValidate,
            Date[] dateUnitaDocValidate, Date[] dateAnnulValidate, BigDecimal idUser) throws EMFError {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT u FROM AroDoc doc JOIN doc.aroUnitaDoc u ");

        // Inserimento nella query del filtro Tipo Unità Doc versione multiselect
        if (!idTipoUnitaDocList.isEmpty()) {
            queryStr.append(whereWord).append("(u.decTipoUnitaDoc.idTipoUnitaDoc IN :listtipoud)");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro Registro
        if (!cdRegistroUnitaDocSet.isEmpty()) {
            queryStr.append(whereWord).append("u.decRegistroUnitaDoc.cdRegistroUnitaDoc IN :setregistro ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro Tipo Documento
        if (!idTipoDocList.isEmpty()) {
            queryStr.append(whereWord).append("doc.decTipoDoc.idTipoDoc IN :listtipodoc ");
            whereWord = "AND ";
        }

        BigDecimal anno = filtri.getAa_key_unita_doc().parse();
        String codice = filtri.getCd_key_unita_doc().parse();

        if (anno != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :annoin ");
            whereWord = " AND ";
        }

        if (codice != null) {
            queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :codicein ");
            whereWord = " AND ";
        }

        BigDecimal anno_range_da = filtri.getAa_key_unita_doc_da().parse();
        BigDecimal anno_range_a = filtri.getAa_key_unita_doc_a().parse();
        String codice_range_da = filtri.getCd_key_unita_doc_da().parse();
        String codice_range_a = filtri.getCd_key_unita_doc_a().parse();

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

        Date data_da = (dateAcquisizioneValidate != null ? dateAcquisizioneValidate[0] : null);
        Date data_a = (dateAcquisizioneValidate != null ? dateAcquisizioneValidate[1] : null);

        if ((data_da != null) && (data_a != null)) {
            queryStr.append(whereWord).append("(u.dtCreazione between :datada AND :dataa) ");
            whereWord = " AND ";
        }

        Date data_reg_ud_da = (dateUnitaDocValidate != null ? dateUnitaDocValidate[0] : null);
        Date data_reg_ud_a = (dateUnitaDocValidate != null ? dateUnitaDocValidate[1] : null);

        if ((data_reg_ud_da != null) && (data_reg_ud_a != null)) {
            queryStr.append(whereWord).append("(u.dtRegUnitaDoc between :dataregudda AND :datareguda) ");
            whereWord = " AND ";
        }

        Date data_annul_da = (dateAnnulValidate != null ? dateAnnulValidate[0] : null);
        Date data_annul_a = (dateAnnulValidate != null ? dateAnnulValidate[1] : null);

        if ((data_annul_da != null) && (data_annul_a != null)) {
            queryStr.append(whereWord).append("(u.dtAnnul between :dataannulda AND :dataannula) ");
            whereWord = " AND ";
        }

        queryStr.append(whereWord).append("u.tiAnnul = 'ANNULLAMENTO' ORDER BY u.dsKeyOrd ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr.toString());
        // non avendo passato alla query i parametri di ricerca, devo passarli ora

        if (!idTipoUnitaDocList.isEmpty()) {
            query.setParameter("listtipoud", idTipoUnitaDocList);
        }

        if (!cdRegistroUnitaDocSet.isEmpty()) {
            query.setParameter("setregistro", cdRegistroUnitaDocSet);
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

        if (!idTipoDocList.isEmpty()) {
            query.setParameter("listtipodoc", idTipoDocList);
        }

        if (data_reg_ud_da != null && data_reg_ud_a != null) {
            query.setParameter("dataregudda", data_reg_ud_da, TemporalType.DATE);
            query.setParameter("datareguda", data_reg_ud_a, TemporalType.DATE);
        }

        if ((data_annul_da != null) && (data_annul_a != null)) {
            query.setParameter("dataannulda", data_annul_da);
            query.setParameter("dataannula", data_annul_a);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI UNITA' DOCUMENTARIE
        List<AroUnitaDoc> listaUD = query.getResultList();
        AroVRicUnitaDocTableBean udTableBean = new AroVRicUnitaDocTableBean();

        try {
            if (listaUD != null && !listaUD.isEmpty()) {
                for (AroUnitaDoc ud : listaUD) {
                    AroUnitaDocRowBean udRowBean = (AroUnitaDocRowBean) Transform.entity2RowBean(ud);
                    AroVRicUnitaDocRowBean rowBean = new AroVRicUnitaDocRowBean();
                    rowBean.copyFromBaseRow(udRowBean);
                    rowBean.setString("nm_tipo_unita_doc", ud.getDecTipoUnitaDoc().getNmTipoUnitaDoc());
                    // Ricavo il campo tipo doc principale
                    for (AroDoc doc : ud.getAroDocs()) {
                        if (doc.getTiDoc().equals("PRINCIPALE")) {
                            rowBean.setString("nm_tipo_doc_princ", doc.getDecTipoDoc().getNmTipoDoc());
                            break;
                        }
                    }
                    // Ricavo il campo ds lista stati elenco vers
                    rowBean.setString("ds_lista_stati_elenco_vers", getDsListaStatiElencoVers(ud.getIdUnitaDoc()));

                    // Ricavo l'identificativo e il tipo della richiesta di annullamento con stato EVASA cui
                    // l'annullamento si riferisce
                    AroRichAnnulVers aroRichAnnulVers = getAroRichAnnulVers(ud.getIdUnitaDoc());
                    if (aroRichAnnulVers != null) {
                        rowBean.setBigDecimal("id_rich_annul_vers",
                                BigDecimal.valueOf(aroRichAnnulVers.getIdRichAnnulVers()));
                        rowBean.setString("ti_creazione_rich_annul_vers",
                                aroRichAnnulVers.getTiCreazioneRichAnnulVers());
                    }

                    udTableBean.add(rowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return udTableBean;
    }

    private String getDsListaStatiElencoVers(long idUnitaDoc) {
        Query query = getEntityManager().createNativeQuery(
                "select " + "listagg (tmp.ti_stato_elenco_vers, '; ') within group (order by tmp.ti_stato_elenco_vers) "
                        + " from " + "	(select ud_elenco.ti_stato_ud_elenco_vers ti_stato_elenco_vers "
                        + "	from ARO_UNITA_DOC ud_elenco " + "	where ud_elenco.id_unita_doc = ?1 " + "	" + "	UNION "
                        + "	select distinct doc_elenco.ti_stato_doc_elenco_vers ti_stato_elenco_vers "
                        + "	from ARO_DOC doc_elenco " + "	where doc_elenco.id_unita_doc = ?1 "
                        + "	and doc_elenco.ti_stato_doc_elenco_vers is not null " + "	)	tmp ");

        query.setParameter(1, idUnitaDoc);
        Object obj = query.getSingleResult();
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    private AroRichAnnulVers getAroRichAnnulVers(long idUnitaDoc) {
        String queryStr = "SELECT rich FROM AroItemRichAnnulVers item "
                + "JOIN item.aroRichAnnulVers rich JOIN rich.aroStatoRichAnnulVers stati "
                + "WHERE item.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND stati.pgStatoRichAnnulVers = (SELECT MAX(maxStati.pgStatoRichAnnulVers) FROM AroStatoRichAnnulVers maxStati WHERE maxStati.aroRichAnnulVers.idRichAnnulVers = rich.idRichAnnulVers) "
                + "AND stati.tiStatoRichAnnulVers = 'EVASA' ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroRichAnnulVers> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * Ricavo il progressivo più alto della versione indice AIP
     *
     * @param idUnitaDoc
     *            id unita doc
     * 
     * @return entity AroVerIndiceAipUd
     */
    public AroVerIndiceAipUd getUltimaVersioneIndiceAip(long idUnitaDoc) {
        Query q = getEntityManager().createQuery(
                "SELECT u FROM AroVerIndiceAipUd u " + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' " + "ORDER BY u.pgVerIndiceAip DESC ");
        q.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVerIndiceAipUd> lista = (List<AroVerIndiceAipUd>) q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public AroVDtVersMaxByUnitaDoc getAroVDtVersMaxByUd(long idUnitaDoc) {
        String queryStr = "SELECT aro FROM AroVDtVersMaxByUnitaDoc aro WHERE aro.idUnitaDoc = :idUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVDtVersMaxByUnitaDoc> lista = (List<AroVDtVersMaxByUnitaDoc>) query.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public String getProfiloNormativo(String tiUsoModelloXsd, TiModelloXsdUd tiModelloXsdUd, long idUnitaDoc) {

        List<VrsXmlModelloSessioneVers> lstXmlModelloSessioneVers = null;

        try {

            String queryStr = "select xms from VrsXmlModelloSessioneVers xms "
                    + "join xms.decUsoModelloXsdUniDoc.decModelloXsdUd modello_xsd "
                    + "join xms.vrsDatiSessioneVers dati_ses " + "join dati_ses.vrsSessioneVers ses "
                    + "where modello_xsd.tiUsoModelloXsd = :tiUsoModelloXsd "
                    + "and modello_xsd.tiModelloXsd = :tiModelloXsdUd " + "and dati_ses.tiDatiSessioneVers = 'XML_DOC' "
                    + "and ses.aroUnitaDoc.idUnitaDoc = :idUnitaDoc " + "and ses.aroDoc is null "
                    + "and ses.tiStatoSessioneVers = 'CHIUSA_OK' " + "and ses.tiSessioneVers = 'VERSAMENTO' ";

            javax.persistence.Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("tiUsoModelloXsd", tiUsoModelloXsd);
            query.setParameter("tiModelloXsdUd", tiModelloXsdUd);
            query.setParameter("idUnitaDoc", idUnitaDoc);

            lstXmlModelloSessioneVers = query.getResultList();
            if (lstXmlModelloSessioneVers != null && !lstXmlModelloSessioneVers.isEmpty()) {
                return lstXmlModelloSessioneVers.get(0).getBlXml();
            }

        } catch (Exception e) {
            // "Eccezione durante il recupero del profilo normativo ud " + e.getMessage()));
            log.error("Eccezione durante il recupero del profilo normativo ud " + e.getMessage(), e);
        }
        return null;
    }
}
