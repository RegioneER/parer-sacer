package it.eng.parer.volume.helper;

import it.eng.parer.entity.VolFileVolumeConserv;
import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.volume.utils.DatiSpecQueryParams;
import it.eng.parer.volume.utils.ReturnParams;
import it.eng.parer.web.dto.DecCriterioAttribBean;
import it.eng.parer.web.dto.DecCriterioDatiSpecBean;
import it.eng.parer.web.dto.DefinitoDaBean;
import it.eng.parer.web.util.Constants.TipoEntitaSacer;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipoOperatoreDatiSpec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Agati_D
 */
@Stateless(mappedName = "VolumeHelper")
@LocalBean
public class VolumeHelper {

    Logger log = LoggerFactory.getLogger(VolumeHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager em;

    // METODI RIFATTI DA PAOLO DOPO MODIFICHE LOGICA DI SANDRO
    // FIXME: UTILIZZATO DA UNITADOC_ACTION - Da spostare in un posto più opportuno
    private String buildClauseExists(String conjunctionWord, int entityNameSuffix, int indiceidattribds,
            String operatore, String filtro, String initialBracket, String from, String where, String entitaSacer,
            String and1, String and2) {
        StringBuilder clauseExists = new StringBuilder();
        clauseExists.append(conjunctionWord)
                .append(initialBracket + " exists (select ric_dati_spec" + entityNameSuffix + " from " + from
                        + " ric_dati_spec" + entityNameSuffix + " WHERE ric_dati_spec" + entityNameSuffix + where
                        + " ");
        clauseExists.append("and ric_dati_spec" + entityNameSuffix + and1 + indiceidattribds + " ");
        if (!and2.isEmpty()) {
            clauseExists.append("and ric_dati_spec" + entityNameSuffix + and2 + indiceidattribds + " ");
        }
        clauseExists.append("and ric_dati_spec" + entityNameSuffix + ".tiEntitaSacer = " + entitaSacer + " ");
        clauseExists.append("and UPPER(ric_dati_spec" + entityNameSuffix + ".dlValore) ");
        clauseExists.append(operatore);
        clauseExists.append(filtro);
        clauseExists.append(") ");
        return clauseExists.toString();
    }

    public ReturnParams buildQueryForDatiSpec(List datiSpecList) {
        ReturnParams retParams = new ReturnParams();
        StringBuilder queryStr = new StringBuilder();
        // UTILIZZO DEI DATI SPECIFICI
        String operatore = null;
        String filtro = null;
        int entityNameSuffix = 0;
        int indiceidattribds = 0;
        List<DatiSpecQueryParams> mappone = new ArrayList<>();
        List<DefinitoDaBean> listaDefinitoDa = new ArrayList<>();
        Set<String> insiemeTipiUnitaDoc = new HashSet();
        Set<String> insiemeTipiDoc = new HashSet();
        Set<String> insiemeSistemiMigrazUniDoc = new HashSet();
        Set<String> insiemeSistemiMigrazDoc = new HashSet();

        // Per ogni dato specifico
        for (Object datiSpecObj : datiSpecList) {
            if (datiSpecObj instanceof DecCriterioDatiSpecBean) {
                DecCriterioDatiSpecBean datiSpec = (DecCriterioDatiSpecBean) datiSpecObj;
                /*
                 * Se il filtro è compilato, ricavo le informazioni che mi servono: aggiungo un elemento in
                 * ListaDefinitoDa e nel relativo insieme
                 */
                if ((StringUtils.isNotBlank(datiSpec.getTiOper()) && StringUtils.isNotBlank(datiSpec.getDlValore()))
                        || (datiSpec.getTiOper() != null
                                && datiSpec.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.NULLO.name())
                                && StringUtils.isWhitespace(datiSpec.getDlValore()))
                        || (datiSpec.getTiOper() != null
                                && datiSpec.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.NON_NULLO.name())
                                && StringUtils.isWhitespace(datiSpec.getDlValore()))) {

                    // Ricavo la listaDefinitoDa di quel preciso dato specifico
                    List<DecCriterioAttribBean> decCriterioAttribList = datiSpec.getDecCriterioAttribs();

                    /*
                     * Scorro questa lista per andare ad inserire l'elemento nella lista principale, ovvero
                     * ListaDefinitoDa
                     */
                    for (DecCriterioAttribBean decCriterioAttrib : decCriterioAttribList) {
                        DefinitoDaBean definitoDa = new DefinitoDaBean();
                        definitoDa.setIdAttribDatiSpec(decCriterioAttrib.getIdAttribDatiSpec());
                        definitoDa.setTiEntitaSacer(decCriterioAttrib.getTiEntitaSacer());
                        definitoDa.setNmTipoDoc(decCriterioAttrib.getNmTipoDoc());
                        definitoDa.setNmTipoUnitaDoc(decCriterioAttrib.getNmTipoUnitaDoc());
                        definitoDa.setNmSistemaMigraz(decCriterioAttrib.getNmSistemaMigraz());
                        definitoDa.setNmAttribDatiSpec(datiSpec.getNmAttribDatiSpec());
                        definitoDa.setTiOper(datiSpec.getTiOper());
                        definitoDa.setDlValore(datiSpec.getDlValore());
                        listaDefinitoDa.add(definitoDa);
                        // Annoto quale elemento sto trattando inserendolo nel relativo insieme
                        // Caso UNI_DOC
                        if (definitoDa.getNmTipoUnitaDoc() != null) {
                            insiemeTipiUnitaDoc.add(definitoDa.getNmTipoUnitaDoc());
                        } // Caso DOC
                        else if (definitoDa.getNmTipoDoc() != null) {
                            insiemeTipiDoc.add(definitoDa.getNmTipoDoc());
                        } // Caso Sistema Migrazione con entità Sacer UNI_DOC
                        else if (definitoDa.getTiEntitaSacer().equals(TipoEntitaSacer.UNI_DOC.name())) {
                            insiemeSistemiMigrazUniDoc.add(definitoDa.getNmSistemaMigraz());
                        } // Caso Sistema Migrazione con entità Sacer DOC
                        else if (definitoDa.getTiEntitaSacer().equals(TipoEntitaSacer.DOC.name())) {
                            insiemeSistemiMigrazDoc.add(definitoDa.getNmSistemaMigraz());
                        }
                    }
                }
            }
        }

        ///////////////////////
        // COSTRUZIONE QUERY //
        ///////////////////////

        /*
         * Comincio a costruire la query con i dati presenti nell'insieme tipi unità doc. appena creato
         */
        if (!insiemeTipiUnitaDoc.isEmpty()) {
            boolean firstTimeDefinitoDa = true;
            Iterator<String> it = insiemeTipiUnitaDoc.iterator();

            // Per ogni nm_tipo_unita_doc presente in insiemeTipiUnitaDoc
            while (it.hasNext()) {
                if (firstTimeDefinitoDa) {
                    queryStr.append("AND ((");
                    firstTimeDefinitoDa = false;
                } else {
                    queryStr.append("OR (");
                }

                String conjunctionWord = "";
                String nmTipoUnitaDoc = it.next();
                boolean firstTimeTipoUD = true;

                for (DefinitoDaBean definitoDa : listaDefinitoDa) {
                    if (definitoDa.getNmTipoUnitaDoc() != null
                            && definitoDa.getNmTipoUnitaDoc().equals(nmTipoUnitaDoc)) {
                        int j = mappone.size();
                        Object[] obj = translateFiltroToSql(definitoDa, j);
                        DatiSpecQueryParams datiSpecQueryParams = (DatiSpecQueryParams) obj[0];
                        operatore = (String) obj[1];
                        filtro = (String) obj[2];

                        if (firstTimeTipoUD) {
                            // (---1---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpec";
                            String where = ".idUnitaDoc = u.idUnitaDoc";
                            String entitaSacer = "'UNI_DOC'";
                            String and1 = ".idAttribDatiSpec = :idattribdatispecin";
                            String and2 = "";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            firstTimeTipoUD = false;
                            entityNameSuffix++;
                            indiceidattribds++;
                        } else {
                            // (---2---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpec";
                            String where = ".idUnitaDoc = u.idUnitaDoc";
                            String entitaSacer = "'UNI_DOC'";
                            String and1 = ".idAttribDatiSpec = :idattribdatispecin";
                            String and2 = "";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            entityNameSuffix++;
                            indiceidattribds++;
                        }
                        mappone.add(datiSpecQueryParams);
                    } // END IF
                } // END FOR di ListaDefinitoDa
                queryStr.append(") ");
            } // END WHILE sull'insieme dei TipiUnitàDoc
            queryStr.append(") ");
        }

        /*
         * Comincio a costruire la query con i dati presenti nell'insieme tipi doc. appena creato
         */
        if (!insiemeTipiDoc.isEmpty()) {
            boolean firstTimeDefinitoDa = true;
            Iterator<String> it = insiemeTipiDoc.iterator();

            // Per ogni nm_tipo_doc presente in insiemeTipiDoc
            while (it.hasNext()) {
                if (firstTimeDefinitoDa) {
                    queryStr.append("AND ((");
                    firstTimeDefinitoDa = false;
                } else {
                    queryStr.append("OR (");
                }

                String conjunctionWord = "";
                String nmTipoDoc = it.next();
                boolean firstTimeTipoDoc = true;

                for (DefinitoDaBean definitoDa : listaDefinitoDa) {
                    if (definitoDa.getNmTipoDoc() != null && definitoDa.getNmTipoDoc().equals(nmTipoDoc)) {
                        int j = mappone.size();
                        Object[] obj = translateFiltroToSql(definitoDa, j);
                        DatiSpecQueryParams datiSpecQueryParams = (DatiSpecQueryParams) obj[0];
                        operatore = (String) obj[1];
                        filtro = (String) obj[2];

                        if (firstTimeTipoDoc) {
                            // (---3---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpec";
                            String where = ".idDoc = u.idDoc";
                            String entitaSacer = "'DOC'";
                            String and1 = ".idAttribDatiSpec = :idattribdatispecin";
                            String and2 = "";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            firstTimeTipoDoc = false;
                            entityNameSuffix++;
                            indiceidattribds++;
                        } else {
                            // (---4---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpec";
                            String where = ".idDoc = u.idDoc";
                            String entitaSacer = "'DOC'";
                            String and1 = ".idAttribDatiSpec = :idattribdatispecin";
                            String and2 = "";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            entityNameSuffix++;
                            indiceidattribds++;
                        }
                        mappone.add(datiSpecQueryParams);
                    } // END IF
                } // END FOR di ListaDefinitoDa
                queryStr.append(") ");
            } // END WHILE sull'insieme dei TipiDoc
            queryStr.append(") ");
        }

        /*
         * Comincio a costruire la query con i dati presenti nell'insieme sistemi di migrazione doc. UNI_DOC appena
         * creato
         */
        if (!insiemeSistemiMigrazUniDoc.isEmpty()) {
            boolean firstTimeDefinitoDa = true;
            Iterator<String> it = insiemeSistemiMigrazUniDoc.iterator();

            // Per ogni nm_sistema_migraz presente in insiemeSistemiMigrazUniDoc
            while (it.hasNext()) {
                if (firstTimeDefinitoDa) {
                    queryStr.append("AND ((");
                    firstTimeDefinitoDa = false;
                } else {
                    queryStr.append("OR (");
                }

                String conjunctionWord = "";
                String nmSisMigr = it.next();
                boolean firstTimeSisMigrTipoUD = true;

                for (DefinitoDaBean definitoDa : listaDefinitoDa) {
                    if (definitoDa.getNmSistemaMigraz() != null && definitoDa.getNmSistemaMigraz().equals(nmSisMigr)
                            && definitoDa.getTiEntitaSacer().equals("UNI_DOC")) {
                        int j = mappone.size();
                        Object[] obj = translateFiltroToSql(definitoDa, j);
                        DatiSpecQueryParams datiSpecQueryParams = (DatiSpecQueryParams) obj[0];
                        operatore = (String) obj[1];
                        filtro = (String) obj[2];

                        if (firstTimeSisMigrTipoUD) {
                            // (---5---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpecMigraz";
                            String where = ".idUnitaDoc = u.idUnitaDoc";
                            String entitaSacer = "'UNI_DOC'";
                            String and1 = ".nmSistemaMigraz = :nmsistemamigrazin";
                            String and2 = ".idAttribDatiSpec = :idattribdatispecin";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            firstTimeSisMigrTipoUD = false;
                            entityNameSuffix++;
                            indiceidattribds++;
                        } else {
                            // (---6---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpecMigraz";
                            String where = ".idUnitaDoc = u.idUnitaDoc";
                            String entitaSacer = "'UNI_DOC'";
                            String and1 = ".nmSistemaMigraz = :nmsistemamigrazin";
                            String and2 = ".idAttribDatiSpec = :idattribdatispecin";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            entityNameSuffix++;
                            indiceidattribds++;
                        }
                        mappone.add(datiSpecQueryParams);
                    } // END IF
                } // END FOR di ListaDefinitoDa
                queryStr.append(") ");
            } // END WHILE sull'insieme dei SistemiMigrazUniDoc
            queryStr.append(") ");
        }

        /*
         * Comincio a costruire la query con i dati presenti nell'insieme sistemi di migrazione doc. DOC appena creato
         */
        if (!insiemeSistemiMigrazDoc.isEmpty()) {
            boolean firstTimeDefinitoDa = true;
            Iterator<String> it = insiemeSistemiMigrazDoc.iterator();

            // Per ogni nm_sistema_migraz presente in insiemeSistemiMigrazUniDoc
            while (it.hasNext()) {
                if (firstTimeDefinitoDa) {
                    queryStr.append("AND ((");
                    firstTimeDefinitoDa = false;
                } else {
                    queryStr.append("OR (");
                }

                String conjunctionWord = "";
                String nmSisMigr = it.next();
                boolean firstTimeSisMigrTipoDoc = true;

                for (DefinitoDaBean definitoDa : listaDefinitoDa) {
                    if (definitoDa.getNmSistemaMigraz() != null && definitoDa.getNmSistemaMigraz().equals(nmSisMigr)
                            && definitoDa.getTiEntitaSacer().equals("DOC")) {
                        int j = mappone.size();
                        Object[] obj = translateFiltroToSql(definitoDa, j);
                        DatiSpecQueryParams datiSpecQueryParams = (DatiSpecQueryParams) obj[0];
                        operatore = (String) obj[1];
                        filtro = (String) obj[2];

                        if (firstTimeSisMigrTipoDoc) {
                            // (---7---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpecMigraz";
                            String where = ".idDoc = u.idDoc";
                            String entitaSacer = "'DOC'";
                            String and1 = ".nmSistemaMigraz = :nmsistemamigrazin";
                            String and2 = ".idAttribDatiSpec = :idattribdatispecin";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            firstTimeSisMigrTipoDoc = false;
                            entityNameSuffix++;
                            indiceidattribds++;
                        } else {
                            // (---8---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpecMigraz";
                            String where = ".idDoc = u.idDoc";
                            String entitaSacer = "'DOC'";
                            String and1 = ".nmSistemaMigraz = :nmsistemamigrazin";
                            String and2 = ".idAttribDatiSpec = :idattribdatispecin";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            entityNameSuffix++;
                            indiceidattribds++;
                        }
                        mappone.add(datiSpecQueryParams);
                    } // END IF
                } // END FOR di ListaDefinitoDa
                queryStr.append(") ");
            } // END WHILE sull'insieme dei SistemiMigrazUniDoc
            queryStr.append(") ");
        }

        retParams.setMappone(mappone);
        retParams.setQuery(queryStr);
        return retParams;
    }

    public Object[] translateFiltroToSql(DefinitoDaBean definitoDa, int j) {
        String perc1 = "";
        String perc2 = "";
        String filtro = ":valorein" + j;
        String operatore = null;
        // Verifico in quale caso ricado
        if (definitoDa.getTiOper().equals(TipoOperatoreDatiSpec.CONTIENE.name())) {
            operatore = " like ";
            perc1 = "%";
            perc2 = "%";
        } else if (definitoDa.getTiOper().equals(TipoOperatoreDatiSpec.INIZIA_PER.name())) {
            operatore = " like ";
            perc2 = "%";
        } else if (definitoDa.getTiOper().equals(TipoOperatoreDatiSpec.DIVERSO.name())) {
            operatore = " != ";
        } else if (definitoDa.getTiOper().equals(TipoOperatoreDatiSpec.MAGGIORE.name())) {
            operatore = " > ";
        } else if (definitoDa.getTiOper().equals(TipoOperatoreDatiSpec.MAGGIORE_UGUALE.name())) {
            operatore = " >= ";
        } else if (definitoDa.getTiOper().equals(TipoOperatoreDatiSpec.MINORE.name())) {
            operatore = " < ";
        } else if (definitoDa.getTiOper().equals(TipoOperatoreDatiSpec.MINORE_UGUALE.name())) {
            operatore = " <= ";
        } else if (definitoDa.getTiOper().equals(TipoOperatoreDatiSpec.NON_CONTIENE.name())) {
            operatore = " not like ";
            perc1 = "%";
            perc2 = "%";
        } else if (definitoDa.getTiOper().equals(TipoOperatoreDatiSpec.NULLO.name())) {
            operatore = " is null ";
            filtro = "";
        } else if (definitoDa.getTiOper().equals(TipoOperatoreDatiSpec.UGUALE.name())) {
            operatore = " = ";
        } else if (definitoDa.getTiOper().equals(TipoOperatoreDatiSpec.NON_NULLO.name())) {
            operatore = " is not null ";
            filtro = "";
        } else if (definitoDa.getTiOper().equals(TipoOperatoreDatiSpec.E_UNO_FRA.name())) {
            operatore = " IN ";
        }
        DatiSpecQueryParams datiSpecQueryParams = new DatiSpecQueryParams(definitoDa.getTiOper(),
                perc1 + definitoDa.getDlValore() + perc2);
        datiSpecQueryParams.add(definitoDa.getIdAttribDatiSpec());
        datiSpecQueryParams.addSM(definitoDa.getNmSistemaMigraz());
        Object[] obj = new Object[3];
        obj[0] = datiSpecQueryParams;
        obj[1] = operatore;
        obj[2] = filtro;
        return obj;
    }

    public byte[] retrieveFile(VolVolumeConserv volume, String fileType) {
        byte[] file = null;
        List<VolFileVolumeConserv> volFiles = volume.getVolFileVolumeConservs();
        for (VolFileVolumeConserv volFile : volFiles) {
            if (fileType.equals(volFile.getTiFileVolumeConserv())) {
                file = volFile.getBlFileVolumeConserv();
            }
        }
        return file;
    }

    public VolVolumeConserv retrieveVolumeById(Long idVolume) {
        VolVolumeConserv volume = em.find(VolVolumeConserv.class, idVolume);
        return volume;
    }

    public VolVolumeConserv getVolInfo(Long idUnitaDoc) {
        Query query = em.createQuery(
                "SELECT app.volVolumeConserv FROM VolAppartUnitaDocVolume app WHERE app.aroUnitaDoc.idUnitaDoc = :idUnitaDoc AND app.volVolumeConserv.dtCreazione = (SELECT MAX(volMax.dtCreazione) FROM VolAppartUnitaDocVolume appMax JOIN appMax.volVolumeConserv volMax WHERE appMax.aroUnitaDoc = app.aroUnitaDoc)");
        query.setParameter("idUnitaDoc", idUnitaDoc);
        VolVolumeConserv vol = (VolVolumeConserv) query.getSingleResult();
        return vol;
    }

}
