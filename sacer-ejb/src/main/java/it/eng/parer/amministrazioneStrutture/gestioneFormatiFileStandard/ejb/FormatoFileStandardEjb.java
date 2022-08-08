package it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.helper.FormatoFileStandardHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecEstensioneFile;
import it.eng.parer.entity.DecFormatoFileBusta;
import it.eng.parer.entity.DecFormatoFileStandard;
import it.eng.parer.entity.DecFormatoGruppoProprieta;
import it.eng.parer.entity.DecFormatoProprieta;
import it.eng.parer.entity.DecFormatoValutazione;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.form.FormatiForm;
import it.eng.parer.slite.gen.tablebean.DecEstensioneFileRowBean;
import it.eng.parer.slite.gen.tablebean.DecEstensioneFileTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileBustaRowBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileBustaTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileStandardRowBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileStandardTableBean;
import it.eng.parer.web.util.Transform;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.form.base.BaseElements;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EJB di gestione dei formati file standard
 *
 * {@link it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard}
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class FormatoFileStandardEjb {

    private static final Logger logger = LoggerFactory.getLogger(FormatoFileStandardEjb.class);

    @Resource
    private SessionContext context;
    @EJB
    private FormatoFileStandardHelper helper;

    public DecFormatoFileStandardTableBean getDecFormatoFileStandardInList(Set<String> list, BaseElements.Status status,
            BigDecimal idStrut) {
        DecFormatoFileStandardTableBean formatoTableBean = new DecFormatoFileStandardTableBean();
        List<Object[]> formati = helper.getDecFormatoFileStandardInList(list, status, idStrut);
        try {
            if (!formati.isEmpty()) {
                DecFormatoFileStandardRowBean formatoRB;
                for (Object[] formato : formati) {
                    formatoRB = (DecFormatoFileStandardRowBean) Transform.entity2RowBean(formato[0]);
                    formatoRB.setString("cd_estensione_file", ((String) formato[1]).toUpperCase());
                    Date dtIstituz = ((Date) formato[2]);
                    Date dtSoppres = ((Date) formato[3]);
                    if (dtIstituz.before(new Date()) && dtSoppres.after(new Date())) {
                        formatoRB.setObject("fl_attivo", "1");
                    } else {
                        formatoRB.setObject("fl_attivo", "0");
                    }
                    formatoTableBean.add(formatoRB);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Errore inatteso nel recupero dei formati file standard");
        }
        return formatoTableBean;
    }

    public DecFormatoFileStandardTableBean getDecFormatoFileStandardInListByName(Collection<String> list) {

        DecFormatoFileStandardTableBean formatoTableBean = new DecFormatoFileStandardTableBean();
        List<Object[]> formati = helper.getDecFormatoFileStandardInListByName(list);
        try {
            if (!formati.isEmpty()) {
                DecFormatoFileStandardRowBean formatoRB;
                for (Object[] formato : formati) {
                    formatoRB = (DecFormatoFileStandardRowBean) Transform.entity2RowBean(formato[0]);
                    formatoRB.setString("cd_estensione_file", ((String) formato[1]).toUpperCase());
                    formatoRB.setString("cd_estensione_file_busta", ((String) formato[1]).toUpperCase());
                    formatoTableBean.add(formatoRB);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return formatoTableBean;
    }

    public DecEstensioneFileTableBean getDecEstensioneFileListByName(Collection<String> list) {

        DecEstensioneFileTableBean formatoTableBean = new DecEstensioneFileTableBean();
        List<Object[]> formati = helper.getDecFormatoFileStandardInListByName(list);
        try {
            if (!formati.isEmpty()) {
                DecEstensioneFileRowBean formatoRB;
                for (Object[] formato : formati) {
                    formatoRB = (DecEstensioneFileRowBean) Transform.entity2RowBean(formato[0]);
                    formatoRB.setString("cd_estensione_file", ((String) formato[1]).toUpperCase());
                    formatoRB.setString("cd_estensione_file_busta", ((String) formato[1]).toUpperCase());
                    formatoTableBean.add(formatoRB);

                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return formatoTableBean;
    }

    public DecFormatoFileStandardTableBean getDecFormatoFileStandardNotInList(Collection<String> list,
            BaseElements.Status status) {

        DecFormatoFileStandardTableBean formatoTableBean = new DecFormatoFileStandardTableBean();
        List<Object[]> formati = helper.getDecFormatoFileStandardNotInList(list, status);

        try {
            if (!formati.isEmpty()) {
                DecFormatoFileStandardRowBean formatoRB;
                Map<DecFormatoFileStandard, SortedSet<String>> formatiMap = new HashMap<>();
                for (Object[] formato : formati) {
                    if (formatiMap.containsKey((DecFormatoFileStandard) formato[0])) {
                        SortedSet<String> estensioni = formatiMap.get((DecFormatoFileStandard) formato[0]);
                        estensioni.add((String) formato[1]);
                        formatiMap.put((DecFormatoFileStandard) formato[0], estensioni);
                    } else {
                        SortedSet<String> estensione = new TreeSet<>();
                        estensione.add((String) formato[1]);
                        formatiMap.put((DecFormatoFileStandard) formato[0], estensione);
                    }
                }

                for (Map.Entry<DecFormatoFileStandard, SortedSet<String>> entry : formatiMap.entrySet()) {
                    formatoRB = (DecFormatoFileStandardRowBean) Transform.entity2RowBean(entry.getKey());
                    SortedSet<String> estensioni = entry.getValue();
                    String estensioniString = "";
                    for (String estensione : estensioni) {
                        if (estensioniString.equals("")) {
                            estensioniString = estensione;
                        } else {
                            estensioniString = estensioniString + "; " + estensione;
                        }
                    }
                    formatoRB.setString("cd_estensione_file", estensioniString);
                    formatoTableBean.add(formatoRB);
                }

                formatoTableBean.addSortingRule("nm_mimetype_file");
                formatoTableBean.sort();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return formatoTableBean;
    }

    public String getDecFormatoFileStandardFromEstensioneFile(String cdEstensioneFile) {
        return helper.getDecFormatoFileStandardNameFromEstensioneFile(cdEstensioneFile);
    }

    public List<String> getDecFormatoFileStandardNameList(BigDecimal idFormatoFileDoc) {
        return helper.getDecFormatoFileStandardNameList(idFormatoFileDoc);
    }

    public DecEstensioneFileRowBean getDecEstensioneFileRowBean(BigDecimal idEstensioneFile,
            BigDecimal idFormatoFileStandard) {
        return getDecEstensioneFile(idEstensioneFile, null, idFormatoFileStandard);
    }

    public DecEstensioneFileRowBean getDecEstensioneFileRowBean(String cdEstensioneFile,
            BigDecimal idFormatoFileStandard) {
        return getDecEstensioneFile(BigDecimal.ZERO, cdEstensioneFile, idFormatoFileStandard);
    }

    public DecEstensioneFileRowBean getDecEstensioneFile(BigDecimal idEstensioneFile, String nmEstensioneFile,
            BigDecimal idFormatoFileStandard) {
        DecEstensioneFileRowBean estensioneFileRowBean = new DecEstensioneFileRowBean();
        DecEstensioneFile estensioneFile = null;

        // FIXMEPLEASE Non capisco l'utilità di questi controlli
        if (idEstensioneFile == BigDecimal.ZERO && nmEstensioneFile != null) {
            estensioneFile = helper.getDecEstensioneFileByName(nmEstensioneFile, idFormatoFileStandard);
        }
        if (nmEstensioneFile == null && idEstensioneFile != BigDecimal.ZERO) {
            estensioneFile = helper.findById(DecEstensioneFile.class, idEstensioneFile);
        }
        if (estensioneFile != null) {
            try {
                estensioneFileRowBean = (DecEstensioneFileRowBean) Transform.entity2RowBean(estensioneFile);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new IllegalStateException("Errore inatteso nel recupero dell'estensione file");
            }
        }
        return estensioneFileRowBean;
    }

    public DecFormatoFileStandardRowBean getDecFormatoFileStandardRowBean(BigDecimal idFormatoFileStandard) {
        return getDecFormatoFileStandard(idFormatoFileStandard, null);
    }

    public DecFormatoFileStandardRowBean getDecFormatoFileStandardRowBean(String nmFormatoFileStandard) {
        return getDecFormatoFileStandard(BigDecimal.ZERO, nmFormatoFileStandard);
    }

    public DecFormatoFileStandardRowBean getDecFormatoFileStandard(BigDecimal idFormatoFileStandard,
            String nmFormatoFileStandard) {
        DecFormatoFileStandard formatoFileStandard = null;
        DecFormatoFileStandardRowBean formatoFileStandardRowBean = null;
        if (idFormatoFileStandard != BigDecimal.ZERO && nmFormatoFileStandard == null) {
            formatoFileStandard = helper.findById(DecFormatoFileStandard.class, idFormatoFileStandard);
        }
        if (idFormatoFileStandard == BigDecimal.ZERO && nmFormatoFileStandard != null) {
            formatoFileStandard = helper.getDecFormatoFileStandardByName(nmFormatoFileStandard);
        }

        if (formatoFileStandard != null) {
            try {
                formatoFileStandardRowBean = (DecFormatoFileStandardRowBean) Transform
                        .entity2RowBean(formatoFileStandard);
                formatoFileStandardRowBean.setObject(FormatiForm.FormatoFileStandard.ni_punteggio_totale,
                        helper.calcolaValutazione(formatoFileStandard));
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new IllegalStateException("Errore inatteso nel recupero del formato file standard");
            }
        }

        return formatoFileStandardRowBean;
    }

    public DecFormatoFileStandardTableBean getDecFormatoFileStandardTableBean(DecFormatoFileStandardRowBean fRowBean) {
        DecFormatoFileStandardTableBean formatoTableBean = new DecFormatoFileStandardTableBean();
        List<DecFormatoFileStandard> list = helper.retrieveDecFormatoFileStandardList(
                fRowBean != null ? fRowBean.getNmFormatoFileStandard() : null,
                fRowBean != null ? fRowBean.getNmMimetypeFile() : null);
        try {
            if (!list.isEmpty()) {
                for (DecFormatoFileStandard entity : list) {
                    DecFormatoFileStandardRowBean row = new DecFormatoFileStandardRowBean();
                    row.entityToRowBean(entity);
                    List<DecEstensioneFile> estensioneFileList = helper
                            .retrieveDecEstensioneFileList(row.getIdFormatoFileStandard());
                    String cdEstensioneFile = "";
                    for (DecEstensioneFile estensioneFile : estensioneFileList) {
                        cdEstensioneFile = cdEstensioneFile + estensioneFile.getCdEstensioneFile() + ";";
                    }
                    row.setString("cd_estensione_file", cdEstensioneFile);
                    row.setObject(FormatiForm.FormatoFileStandardList.ni_punteggio_totale,
                            helper.calcolaValutazione(entity));
                    formatoTableBean.add(row);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return formatoTableBean;
    }

    public DecEstensioneFileTableBean getDecEstensioneFileTableBean(DecEstensioneFileRowBean estensioneFileRowBean) {
        DecEstensioneFileTableBean estensioneFileTableBean = new DecEstensioneFileTableBean();
        List<DecEstensioneFile> list = helper
                .retrieveDecEstensioneFileList(estensioneFileRowBean.getIdFormatoFileStandard());
        try {
            if (!list.isEmpty()) {
                estensioneFileTableBean = (DecEstensioneFileTableBean) Transform.entities2TableBean(list);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return estensioneFileTableBean;
    }

    public DecFormatoFileBustaRowBean getDecFormatoFileBustaRowBean(BigDecimal idFormatoFileBusta) {
        DecFormatoFileBustaRowBean formatoFileBustaRowBean = null;
        DecFormatoFileBusta formatoFileBusta = helper.findById(DecFormatoFileBusta.class, idFormatoFileBusta);
        if (formatoFileBusta != null) {
            try {
                formatoFileBustaRowBean = (DecFormatoFileBustaRowBean) Transform.entity2RowBean(formatoFileBusta);
            } catch (Exception e) {
                logger.error(e.getMessage());
                // Correggere l'errore gestito
            }
        }
        return formatoFileBustaRowBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertDecFormatoFileStandard(DecFormatoFileStandardRowBean fRowBean) {
        DecFormatoFileStandard formatoFileStandard = (DecFormatoFileStandard) Transform.rowBean2Entity(fRowBean);
        helper.insertEntity(formatoFileStandard, true);

        fRowBean.setIdFormatoFileStandard(new BigDecimal(formatoFileStandard.getIdFormatoFileStandard()));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long insertDecEstensioneFile(DecEstensioneFileRowBean estensioneFileRowBean) throws ParerUserError {
        DecFormatoFileStandard formatoFileStandard = helper.findById(DecFormatoFileStandard.class,
                estensioneFileRowBean.getIdFormatoFileStandard());
        if (formatoFileStandard.getDecEstensioneFiles() == null) {
            formatoFileStandard.setDecEstensioneFiles(new ArrayList<>());
        }

        if (helper.getDecEstensioneFileByName(estensioneFileRowBean.getCdEstensioneFile(), null) != null) {
            throw new ParerUserError("Estensione gi\u00E0 associata al formato specificato");
        }

        DecEstensioneFile estensioneFile = (DecEstensioneFile) Transform.rowBean2Entity(estensioneFileRowBean);
        estensioneFile.setDecFormatoFileStandard(formatoFileStandard);

        helper.insertEntity(estensioneFile, true);
        formatoFileStandard.getDecEstensioneFiles().add(estensioneFile);
        return estensioneFile.getIdEstensioneFile();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecEstensioneFile(BigDecimal idFormatoFileStandard, BigDecimal idEstensioneFile,
            String cdEstensioneFileOld, String cdEstensioneFile) throws ParerUserError {
        DecFormatoFileStandard formatoFileStandard = helper.findById(DecFormatoFileStandard.class,
                idFormatoFileStandard);
        if (formatoFileStandard.getDecEstensioneFiles() == null) {
            formatoFileStandard.setDecEstensioneFiles(new ArrayList<>());
        }

        DecEstensioneFile estensioneFileDB = helper.findById(DecEstensioneFile.class, idEstensioneFile);

        DecEstensioneFile estensioneFileNewDB = helper.getDecEstensioneFileByName(cdEstensioneFile, null);

        if (estensioneFileDB != null && estensioneFileNewDB != null
                && estensioneFileDB.getIdEstensioneFile() != estensioneFileNewDB.getIdEstensioneFile()) {
            throw new ParerUserError("Estensione gi\u00E0 associata ad un formato");
        }

        estensioneFileDB.setCdEstensioneFile(cdEstensioneFile);

        formatoFileStandard.getDecEstensioneFiles().add(estensioneFileDB);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecFormatoFileStandard(BigDecimal idFormato, DecFormatoFileStandardRowBean fRowBean) {
        DecFormatoFileStandard formato = helper.findById(DecFormatoFileStandard.class, idFormato);
        formato.setCdVersione(fRowBean.getCdVersione());
        formato.setDsCopyright(fRowBean.getDsCopyright());
        formato.setDsFormatoFileStandard(fRowBean.getDsFormatoFileStandard());
        formato.setFlFormatoConcat(fRowBean.getFlFormatoConcat());
        formato.setNmMimetypeFile(fRowBean.getNmMimetypeFile());
        formato.setNmFormatoFileStandard(fRowBean.getNmFormatoFileStandard());
        formato.setTiEsitoContrFormato(fRowBean.getTiEsitoContrFormato());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertDecFormatoFileBusta(DecFormatoFileBustaRowBean formatoFileBustaRowBean) throws ParerUserError {
        DecFormatoFileStandard formatoFileStandard = helper.findById(DecFormatoFileStandard.class,
                formatoFileBustaRowBean.getIdFormatoFileStandard());
        if (formatoFileStandard.getDecFormatoFileBustas() == null) {
            formatoFileStandard.setDecFormatoFileBustas(new ArrayList<>());
        }
        if (helper.getDecFormatoFileBustaByName(formatoFileBustaRowBean.getTiFormatoFirmaMarca(),
                formatoFileBustaRowBean.getIdFormatoFileStandard()) != null) {
            throw new ParerUserError("Formato file busta gi\u00E0 associata al formato specificato");
        }
        DecFormatoFileBusta formatoFileBusta = (DecFormatoFileBusta) Transform.rowBean2Entity(formatoFileBustaRowBean);
        formatoFileBusta.setDecFormatoFileStandard(formatoFileStandard);

        helper.insertEntity(formatoFileBusta, true);
        formatoFileStandard.getDecFormatoFileBustas().add(formatoFileBusta);
    }

    public DecFormatoFileBustaTableBean getDecFormatoFileBustaTableBean(
            DecFormatoFileBustaRowBean formatoFileBustaRowBean) {
        DecFormatoFileBustaTableBean formatoFileBustaTableBean = new DecFormatoFileBustaTableBean();
        List<DecFormatoFileBusta> list = helper
                .getDecFormatoFileBustaList(formatoFileBustaRowBean.getIdFormatoFileStandard());

        try {
            if (!list.isEmpty()) {
                formatoFileBustaTableBean = (DecFormatoFileBustaTableBean) Transform.entities2TableBean(list);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new IllegalStateException("Errore inatteso nel recupero dei formati file busta");
        }

        return formatoFileBustaTableBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecEstensioneFile(DecEstensioneFileRowBean estensioneFileRowBean) {
        DecEstensioneFile estensioneFile = helper.findById(DecEstensioneFile.class,
                estensioneFileRowBean.getIdEstensioneFile());
        helper.removeEntity(estensioneFile, true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecFormatoFileBusta(DecFormatoFileBustaRowBean formatoFileBustaRowBean) {
        DecFormatoFileBusta formatoFileBusta = helper.findById(DecFormatoFileBusta.class,
                formatoFileBustaRowBean.getIdFormatoFileBusta());
        helper.removeEntity(formatoFileBusta, true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecFormatoFileStandard(DecFormatoFileStandardRowBean formatoFileStandardRowBean)
            throws ParerUserError {
        DecFormatoFileStandard formatoFileStandard = helper.findById(DecFormatoFileStandard.class,
                formatoFileStandardRowBean.getIdFormatoFileStandard());
        if (!formatoFileStandard.getAroCompDocs().isEmpty() || !formatoFileStandard.getAroBustaCrittogs().isEmpty()
                || !formatoFileStandard.getDecEstensioneFiles().isEmpty()
                || !formatoFileStandard.getDecFormatoFileBustas().isEmpty()
                || !formatoFileStandard.getDecUsoFormatoFileStandards().isEmpty()) {
            throw new ParerUserError(
                    "Impossibile eliminare il formato file standard: esiste almeno un elemento associato ad esso");
        }
        helper.removeEntity(formatoFileStandard, true);
    }

    @TransactionAttribute
    public BaseTable getValutazioneFormatiTableBean(BigDecimal idFormato) {
        Asserts.notNull(idFormato, "Impossibile ottenere le valutazioni senza un id formato");
        BaseTable baseTable = new BaseTable();
        List<DecFormatoGruppoProprieta> proprietaValutazione = helper.getAllDecFormatoGruppoProprieta();
        List<DecFormatoValutazione> valutazioni = helper.getValutazioniPerFormato(idFormato.longValue());

        for (DecFormatoGruppoProprieta g : proprietaValutazione) {
            boolean valutazioneVuota = true;
            for (DecFormatoValutazione v : valutazioni) {
                if (v.getDecFormatoProprieta().getDecFormatoGruppoProprieta().getIdFormatoGruppoProprieta()
                        .equals(g.getIdFormatoGruppoProprieta())) {
                    valutazioneVuota = false;
                    baseTable.add(componiRigaValutazioneFormato(v));
                    break;
                }
            }
            if (valutazioneVuota) {
                baseTable.add(componiRigaValutazioneFormato(g));
            }
        }

        return baseTable;
    }

    public static final String VALUTAZIONE_ID_GRP_PROP = "id_gruppo_proprieta";
    public static final String VALUTAZIONE_ID = "id_valutazione";
    public static final String VALUTAZIONE_NM_PROPRIETA = "nm_proprieta";

    private BaseRow componiRigaValutazioneFormato(DecFormatoGruppoProprieta gruppoProprieta) {
        BaseRow row = new BaseRow();
        row.setObject(VALUTAZIONE_ID_GRP_PROP, gruppoProprieta.getIdFormatoGruppoProprieta());
        row.setObject(VALUTAZIONE_ID, null);
        row.setObject(FormatiForm.ParametroValutazione.id_proprieta, null);
        row.setString(FormatiForm.FormatoFileParametriValutazioneList.nm_formato_gruppo_proprieta,
                gruppoProprieta.getNmFormatoGruppoProprieta());
        row.setString(FormatiForm.FormatoFileParametriValutazioneList.nm_formato_proprieta, " - non impostato -");
        row.setBigDecimal(FormatiForm.FormatoFileParametriValutazioneList.ni_punteggio, null);
        return row;
    }

    private BaseRow componiRigaValutazioneFormato(DecFormatoValutazione valutazione) {
        BaseRow row = new BaseRow();
        row.setObject(VALUTAZIONE_ID_GRP_PROP,
                valutazione.getDecFormatoProprieta().getDecFormatoGruppoProprieta().getIdFormatoGruppoProprieta());
        row.setObject(VALUTAZIONE_ID, valutazione.getIdFormatoValutazione());
        row.setObject(FormatiForm.ParametroValutazione.id_proprieta,
                valutazione.getDecFormatoProprieta().getIdFormatoProprieta());
        row.setString(FormatiForm.FormatoFileParametriValutazioneList.nm_formato_gruppo_proprieta,
                valutazione.getDecFormatoProprieta().getDecFormatoGruppoProprieta().getNmFormatoGruppoProprieta());
        row.setString(FormatiForm.FormatoFileParametriValutazioneList.nm_formato_proprieta,
                valutazione.getDecFormatoProprieta().getNmFormatoProprieta());
        row.setBigDecimal(FormatiForm.FormatoFileParametriValutazioneList.ni_punteggio, valutazione.getNiPunteggio());
        return row;
    }

    public BaseRow getParametroValutazioneRowBean(BaseRow parametroValutazioneCorrente) {
        BaseRow row = new BaseRow();
        row.setString(FormatiForm.ParametroValutazione.nm_formato_gruppo_proprieta, parametroValutazioneCorrente
                .getString(FormatiForm.FormatoFileParametriValutazioneList.nm_formato_gruppo_proprieta));
        row.setBigDecimal(FormatiForm.ParametroValutazione.ni_punteggio, parametroValutazioneCorrente
                .getBigDecimal(FormatiForm.FormatoFileParametriValutazioneList.ni_punteggio));
        return row;
    }

    public BaseTable getProprietaPerGruppo(Long idGruppoProprieta) {
        List<DecFormatoProprieta> proprieta = helper.getDecFormatoProprietaByGruppo(idGruppoProprieta);
        BaseTable baseTable = new BaseTable();
        for (DecFormatoProprieta p : proprieta) {
            BaseRow baseRow = new BaseRow();
            baseRow.setBigDecimal(FormatiForm.ParametroValutazione.id_proprieta,
                    BigDecimal.valueOf(p.getIdFormatoProprieta()));
            baseRow.setString(VALUTAZIONE_NM_PROPRIETA, p.getNmFormatoProprieta());
            baseTable.add(baseRow);
        }
        return baseTable;
    }

    public DecFormatoValutazione updateDecFormatoValutazione(Long idValutazione, Long idProprieta,
            BigDecimal punteggio) {
        DecFormatoValutazione decFormatoValutazione = helper.findById(DecFormatoValutazione.class, idValutazione);
        Asserts.notNull(decFormatoValutazione,
                "Impossibile trovare un " + DecFormatoValutazione.class.getSimpleName() + " con id " + idValutazione);
        DecFormatoProprieta decFormatoProprieta = helper.findById(DecFormatoProprieta.class, idProprieta);
        Asserts.notNull(decFormatoProprieta,
                "Impossibile trovare un " + DecFormatoProprieta.class.getSimpleName() + " con id " + idProprieta);

        decFormatoValutazione.setNiPunteggio(punteggio);
        decFormatoValutazione.setDecFormatoProprieta(decFormatoProprieta);
        return helper.mergeEntity(decFormatoValutazione);
    }

    public void insertDecFormatoValutazione(BigDecimal idFormatoFileStandard, Long idFormatoProprieta,
            BigDecimal punteggio) {
        Asserts.notNull(idFormatoFileStandard,
                "idFormatoFileStandard obbligatorio per inserire un nuovo DecFormatoValutazione");
        Asserts.notNull(idFormatoProprieta,
                "idFormatoProprieta obbligatorio per inserire un nuovo DecFormatoValutazione");
        Asserts.notNull(punteggio, "punteggio obbligatorio per inserire un nuovo DecFormatoValutazione");

        DecFormatoFileStandard formato = helper.findById(DecFormatoFileStandard.class,
                idFormatoFileStandard.longValue());
        Asserts.notNull(formato, "Non esiste nessun DecFormatoFileStandard con id " + idFormatoFileStandard);

        DecFormatoProprieta proprieta = helper.findById(DecFormatoProprieta.class, idFormatoProprieta);
        Asserts.notNull(proprieta, "Non esiste nessuna DecFormatoProprieta con id " + idFormatoProprieta);

        DecFormatoValutazione valutazione = new DecFormatoValutazione();
        valutazione.setDecFormatoFileStandard(formato);
        valutazione.setDecFormatoProprieta(proprieta);
        valutazione.setNiPunteggio(punteggio);
        helper.insertEntity(valutazione, false);

    }

    public void deleteDecFormatoValutazione(Long idFormatoValutazione) {
        Asserts.notNull(idFormatoValutazione, "idFormatoValutazione obbligatorio per cancellare DecFormatoValutazione");
        DecFormatoValutazione daCancellare = helper.findById(DecFormatoValutazione.class, idFormatoValutazione);
        Asserts.notNull(daCancellare, "Non c'è nessun DecFormatoValutazione con id  " + idFormatoValutazione);
        helper.removeEntity(daCancellare, false);
    }

    public BigDecimal getPunteggioDefault(Long idFormatoProprieta) {
        Asserts.notNull(idFormatoProprieta, "idFormatoProprieta obbligatorio per recuperare la DecFormatoProprieta");
        DecFormatoProprieta proprieta = helper.findById(DecFormatoProprieta.class, idFormatoProprieta);
        Asserts.notNull(proprieta, "Non esiste nessuna DecFormatoProprieta con id " + idFormatoProprieta);
        return proprieta.getNiPunteggioDefault() == null ? null : BigDecimal.valueOf(proprieta.getNiPunteggioDefault());
    }

    public BigDecimal calcolaPunteggioInteroperabilita(BigDecimal idFormatoFileStandard) {
        Asserts.notNull(idFormatoFileStandard,
                "idFormatoFileStandard obbligatorio per poter calcolare il punteggio di interoperabilita");
        DecFormatoFileStandard formatoFileStandard = helper.findById(DecFormatoFileStandard.class,
                idFormatoFileStandard.longValue());
        Asserts.notNull(formatoFileStandard,
                "Non esiste nessun DecFormatoFileStandard con id " + idFormatoFileStandard);
        return helper.calcolaValutazione(formatoFileStandard);
    }

    public BaseTable getMimetypeTableBean() {
        BaseTable tabella = new BaseTable();
        List<String> mimetypeList = helper.getMimetypeList();

        for (String mimetype : mimetypeList) {
            BaseRow riga = new BaseRow();
            riga.setString("nm_mimetype_file", mimetype);
            tabella.add(riga);
        }

        return tabella;

    }
}
