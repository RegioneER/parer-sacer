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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecCriterioFiltroMultiplo;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.DecCriterioRaggr.TiModValidElencoCriterio;
import it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.form.CriteriRaggruppamentoForm;
import it.eng.parer.slite.gen.form.CriteriRaggruppamentoForm.CreaCriterioRaggr;
import it.eng.parer.slite.gen.tablebean.DecCriterioFiltroMultiploRowBean;
import it.eng.parer.slite.gen.tablebean.DecCriterioFiltroMultiploTableBean;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrRowBean;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.viewbean.DecVRicCriterioRaggrRowBean;
import it.eng.parer.slite.gen.viewbean.DecVRicCriterioRaggrTableBean;
import it.eng.parer.viewEntity.DecVCreaCritRaggrRegistro;
import it.eng.parer.viewEntity.DecVCreaCritRaggrTipoDoc;
import it.eng.parer.viewEntity.DecVCreaCritRaggrTipoUd;
import it.eng.parer.viewEntity.DecVRicCriterioRaggr;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.Constants.TipoDato;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;

@SuppressWarnings("unchecked")
@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class CriteriRaggrHelper extends GenericHelper {

    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    private static final Logger log = LoggerFactory.getLogger(CriteriRaggrHelper.class.getName());

    public Long saveCritRaggr(LogParam param, CreaCriterioRaggr filtri, Date[] dateCreazioneValidate,
            BigDecimal idStruttura, String nome, String criterioStandard) throws EMFError, ParerUserError {
        DecCriterioRaggr rec = new DecCriterioRaggr();
        if (nome != null) {
            rec = getDecCriterioRaggrByStrutturaAndCriterio(idStruttura, nome);
        }

        if (rec.getDecCriterioFiltroMultiplos() == null) {
            rec.setDecCriterioFiltroMultiplos(new ArrayList<>());
        }

        rec.setOrgStrut(getOrgStrutById(idStruttura));

        // Setto i filtri multipli a 0 come default
        rec.setFlFiltroTipoUnitaDoc("0");
        rec.setFlFiltroTipoDoc("0");
        rec.setFlFiltroSistemaMigraz("0");
        rec.setFlFiltroRegistroKey("0");
        rec.setFlFiltroRangeRegistroKey("0");
        rec.setFlFiltroTiEsitoVerifFirme("0");

        // Per ogni tipo unità doc creo un record filtro multiplo

        if (filtri.getNm_tipo_unita_doc().parse() != null && !filtri.getNm_tipo_unita_doc().parse().isEmpty()) {
            List<Long> asList = longListFrom(filtri.getNm_tipo_unita_doc().parse());
            List<DecTipoUnitaDoc> decTipoUnitaDocList = getDecTipoUnitaDocsByTipoUnitaDoc(asList);
            if (!decTipoUnitaDocList.isEmpty()) {
                rec.setFlFiltroTipoUnitaDoc("1");
                for (DecTipoUnitaDoc tipo : decTipoUnitaDocList) {
                    // Se siamo nel caso di modifica di un criterio, devo verificare se i filtri sono già presenti prima
                    // di salvarli
                    if (nome != null) {
                        final List<DecCriterioFiltroMultiplo> decCriterioFiltroMultiploList = getDecCriterioFiltroMultiploList(
                                rec, tipo);
                        if (decCriterioFiltroMultiploList.isEmpty()) {
                            saveCritRaggrFiltroMultiplo(rec, null, null, null, tipo, null,
                                    ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_UNI_DOC.name());
                        }
                    } else {
                        saveCritRaggrFiltroMultiplo(rec, null, null, null, tipo, null,
                                ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_UNI_DOC.name());
                    }
                }
                if (nome != null) {
                    // In caso di modifica, potrei aver eliminato qualche filtro dalle multiselect, che non
                    // risulterebbero più presenti nella lista
                    // Eseguo perciò una bulk delete sui record non presenti nella lista
                    deleteDecCriterioFiltroMultiploByDecCriterioNotInTipoUnitaDoc(rec, asList);
                }
            }
        } else {
            // Se sono in modifica, potrei avere eliminato tutti i filtri che avevo creato precedentemente
            // Eseguo perciò una bulk delete per eliminare quei record
            if (nome != null) {
                deleteDecCriterioFiltroMultiploTipoUniDocByDecCriterio(rec);
            }
        }

        // Per ogni tipo doc creo un record filtro multiplo

        if (filtri.getNm_tipo_doc().parse() != null && !filtri.getNm_tipo_doc().parse().isEmpty()) {
            List<Long> nmTipoDocList = longListFrom(filtri.getNm_tipo_doc().parse());
            List<DecTipoDoc> lista = getDecTipoDocsByTipoDocList(nmTipoDocList);
            if (!lista.isEmpty()) {
                rec.setFlFiltroTipoDoc("1");
                for (DecTipoDoc tipo : lista) {
                    // Se sto modificando il criterio, verifico se ho già inserito precedentemente il filtro,
                    // Altrimenti lo salvo direttamente
                    if (nome != null) {
                        final List<DecCriterioFiltroMultiplo> decCriterioFiltroMultiploList = getDecCriterioFiltroMultiplosByTipoDocAndDecCriterio(
                                rec, tipo);
                        if (decCriterioFiltroMultiploList.isEmpty()) {
                            saveCritRaggrFiltroMultiplo(rec, null, null, tipo, null, null,
                                    ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_DOC.name());
                        }
                    } else {
                        saveCritRaggrFiltroMultiplo(rec, null, null, tipo, null, null,
                                ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_DOC.name());
                    }
                }
                if (nome != null) {
                    // In caso di modifica, potrei aver eliminato qualche filtro dalle multiselect, che non
                    // risulterebbero più presenti nella lista
                    // Eseguo perciò una bulk delete sui record non presenti nella lista
                    deleteDecCriterioFiltroMultiploByDecCriterioNotInTipoDocs(rec, nmTipoDocList);
                }
            }
        } else {
            // Se sono in modifica, potrei avere eliminato tutti i filtri che avevo creato precedentemente
            // Eseguo perciò una bulk delete per eliminare quei record
            if (nome != null) {
                deleteDecCriterioFiltroMultiploTipoDocByDecCriterio(rec);
            }
        }

        // Per ogni sistema di migrazione creo un record filtro multiplo
        if (filtri.getNm_sistema_migraz().parse() != null && !filtri.getNm_sistema_migraz().parse().isEmpty()) {
            List<String> nmSistemaMigrazList = filtri.getNm_sistema_migraz().parse();
            List<String> orgUsoSistemaMigrazList = getOrgUsoSistemaMigrazByNmSistemaMigrazList(idStruttura,
                    nmSistemaMigrazList);
            if (!orgUsoSistemaMigrazList.isEmpty()) {
                rec.setFlFiltroSistemaMigraz("1");
                for (String tipo : orgUsoSistemaMigrazList) {
                    // Se sto modificando il criterio, verifico se ho già inserito precedentemente il filtro,
                    // Altrimenti lo salvo direttamente
                    if (nome != null) {
                        final List<DecCriterioFiltroMultiplo> decCriterioFiltroMultiploList = getDecCriterioFiltroMultiploByDecCriterioRaggr(
                                rec, tipo);
                        if (decCriterioFiltroMultiploList.isEmpty()) {
                            saveCritRaggrFiltroMultiploSisMigr(rec, tipo,
                                    ApplEnum.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name());
                        }
                    } else {
                        saveCritRaggrFiltroMultiploSisMigr(rec, tipo,
                                ApplEnum.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name());
                    }
                }
                if (nome != null) {
                    deleteDecCriterioRaggrByDecCriterioRaggrNotInNmSistemaMigraz(rec, nmSistemaMigrazList);
                }
            }
        } else {
            // Se sono in modifica, potrei avere eliminato tutti i filtri che avevo creato precedentemente
            // Eseguo perciò una bulk delete per eliminare quei record
            if (nome != null) {
                deleteDecCriterioFiltroMultiploTipoSistemaMigrazByDecCriterioRaggr(rec);
            }
        }

        // Per ogni registro creo un record filtro multiplo

        if (filtri.getCd_registro_key_unita_doc().parse() != null
                && !filtri.getCd_registro_key_unita_doc().parse().isEmpty()) {
            List<Long> cdRegistroKeyUnitaDocList = longListFrom(filtri.getCd_registro_key_unita_doc().parse());
            List<DecRegistroUnitaDoc> lista = getDecRegistroUnitaDocByIdRegistroUnitaDoc(cdRegistroKeyUnitaDocList);
            if (!lista.isEmpty()) {
                rec.setFlFiltroRegistroKey("1");
                for (DecRegistroUnitaDoc reg : lista) {
                    if (nome != null) {
                        final List<DecCriterioFiltroMultiplo> decCriterioFiltroMultiploList = getDecCriterioFiltroMultiploByDecRegistroUnitaDoc(
                                rec, reg);
                        if (decCriterioFiltroMultiploList.isEmpty()) {
                            saveCritRaggrFiltroMultiplo(rec, reg, null, null, null, null,
                                    ApplEnum.TipoFiltroMultiploCriteriRaggr.REGISTRO_UNI_DOC.name());
                        }
                    } else {
                        saveCritRaggrFiltroMultiplo(rec, reg, null, null, null, null,
                                ApplEnum.TipoFiltroMultiploCriteriRaggr.REGISTRO_UNI_DOC.name());
                    }
                }
                if (nome != null) {
                    // In caso di modifica, potrei aver eliminato qualche filtro dalle multiselect, che non
                    // risulterebbero più presenti nella lista
                    // Eseguo perciò una bulk delete sui record non presenti nella lista
                    deleteDecCriterioFiltroMultiploByDecCriterioRaggrNotInIdRegistroUnitaDoc(rec,
                            cdRegistroKeyUnitaDocList);
                }
            }
        } else {
            if (nome != null) {
                // Se sono in modifica, potrei avere eliminato tutti i filtri che avevo creato precedentemente
                // Eseguo perciò una bulk delete per eliminare quei record
                deleteDecCriterioFiltroMultiploTipoRegistroUniDocByDecCriterioRaggr(rec);
            }
        }

        // Per ogni tipo esito verifica firme creo un record filtro multiplo
        if (filtri.getTi_esito_verif_firme().parse() != null && !filtri.getTi_esito_verif_firme().parse().isEmpty()) {
            List<String> asList = filtri.getTi_esito_verif_firme().parse();
            if (!asList.isEmpty()) {
                rec.setFlFiltroTiEsitoVerifFirme("1");
                for (String tipo : asList) {
                    if (nome != null) {
                        final List<DecCriterioFiltroMultiplo> decCriterioFiltroMultiploList = getDecCriterioFiltroMultiploByDecCriterioRaggrAndTipoEsitoVerifFirme(
                                rec, tipo);
                        if (decCriterioFiltroMultiploList.isEmpty()) {
                            saveCritRaggrFiltroMultiplo(rec, null, null, null, null, tipo,
                                    ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_ESITO_VERIF_FIRME.name());
                        }
                    } else {
                        saveCritRaggrFiltroMultiplo(rec, null, null, null, null, tipo,
                                ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_ESITO_VERIF_FIRME.name());
                    }
                }
                // In caso di modifica, l'utente potrebbe aver eliminato qualche filtro dalle multiselect, che non
                // risulterebbero più presenti nella lista
                // Eseguo perciò una bulk delete sui record non presenti nella lista
                deleteDecCriterioFiltroMultiploByDecCriterioRaggrNotInEsitoVerifFirme(rec, asList);
            }
        } else {
            if (nome != null) {
                // Se sono in modifica, potrei avere eliminato tutti i filtri che avevo creato precedentemente
                // Eseguo perciò una bulk delete per eliminare quei record
                deleteDecCriterioFiltroMultiploTipoEsitoVerifFirmeByDecCriterioRaggr(rec);
            }
        }

        rec.setNmCriterioRaggr(filtri.getNm_criterio_raggr().parse());
        rec.setDsCriterioRaggr(filtri.getDs_criterio_raggr().parse());
        rec.setNiMaxComp(filtri.getNi_max_comp().parse());
        rec.setNiMaxElenchiByGg(filtri.getNi_max_elenchi_by_gg().parse());
        rec.setTiScadChiusVolume(filtri.getTi_scad_chius_volume().getValue());
        rec.setNiTempoScadChius(filtri.getNi_tempo_scad_chius().parse());
        rec.setTiTempoScadChius(filtri.getTi_tempo_scad_chius().getValue());
        rec.setDtIstituz(filtri.getDt_istituz().parse());
        rec.setDtSoppres(filtri.getDt_soppres().parse());
        rec.setCdKeyUnitaDoc(filtri.getCd_key_unita_doc().parse());
        rec.setCdKeyUnitaDocDa(filtri.getCd_key_unita_doc_da().parse());
        rec.setCdKeyUnitaDocA(filtri.getCd_key_unita_doc_a().parse());
        rec.setAaKeyUnitaDoc(filtri.getAa_key_unita_doc().parse());
        rec.setAaKeyUnitaDocDa(filtri.getAa_key_unita_doc_da().parse());
        rec.setAaKeyUnitaDocA(filtri.getAa_key_unita_doc_a().parse());
        rec.setFlUnitaDocFirmato(filtri.getFl_unita_doc_firmato().parse());
        Date dataDa = (dateCreazioneValidate != null ? dateCreazioneValidate[0] : null);
        Date dataA = (dateCreazioneValidate != null ? dateCreazioneValidate[1] : null);
        rec.setDtCreazioneUnitaDocDa(dataDa);
        rec.setDtCreazioneUnitaDocA(dataA);
        rec.setFlForzaAccettazione(filtri.getFl_forza_accettazione().parse());
        rec.setFlForzaConservazione(filtri.getFl_forza_conservazione().parse());
        rec.setTiConservazione(filtri.getTi_conservazione().parse());
        rec.setDtRegUnitaDocDa(filtri.getDt_reg_unita_doc_da().parse());
        rec.setDtRegUnitaDocA(filtri.getDt_reg_unita_doc_a().parse());
        rec.setDlOggettoUnitaDoc(filtri.getDl_oggetto_unita_doc().parse());
        rec.setDlDoc(filtri.getDl_doc().parse());
        rec.setDsAutoreDoc(filtri.getDs_autore_doc().parse());
        rec.setNtCriterioRaggr(filtri.getNt_criterio_raggr().parse());
        rec.setFlCriterioRaggrStandard(criterioStandard);
        // Salvo di default come non fiscale, poi i successivi controlli stabiliranno l'esatto valore
        rec.setFlCriterioRaggrFisc("0");
        rec.setTiGestElencoCriterio(filtri.getTi_gest_elenco_criterio().parse());
        rec.setTiValidElenco(TiValidElencoCriterio.valueOf(filtri.getTi_valid_elenco().parse()));
        rec.setTiModValidElenco(TiModValidElencoCriterio.valueOf(filtri.getTi_mod_valid_elenco().parse()));

        try {
            getEntityManager().persist(rec);
            getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                    new BigDecimal(rec.getIdCriterioRaggr()), param.getNomePagina());
        } catch (RuntimeException re) {
            log.error("Eccezione nella persistenza", re);
            throw new EMFError(EMFError.BLOCKING, re);
        }

        // Gestione del flag fiscale
        String flCriterioRaggrFiscMessage = getFlCriterioRaggrFiscMessage(new BigDecimal(rec.getIdCriterioRaggr()));
        if (flCriterioRaggrFiscMessage.equals(ApplEnum.FlagFiscaleMessage.FISCALE.getDescrizione())) {
            rec.setFlCriterioRaggrFisc("1");
        } else if (flCriterioRaggrFiscMessage.equals(ApplEnum.FlagFiscaleMessage.NON_FISCALE.getDescrizione())) {
            rec.setFlCriterioRaggrFisc("0");
        } else {
            throw new ParerUserError(
                    "Il criterio è errato perchè è standard ed il tipo di unità documentaria usato dal criterio "
                            + "è associata a registri fiscali ed a registri non fiscali");
        }

        return rec.getIdCriterioRaggr();
    }

    private void saveCritRaggrFiltroMultiploSisMigr(DecCriterioRaggr crit, String nmSistemaMigraz,
            String tiFiltroMultiplo) {
        DecCriterioFiltroMultiplo filtro = new DecCriterioFiltroMultiplo();
        filtro.setDecCriterioRaggr(crit);
        filtro.setNmSistemaMigraz(nmSistemaMigraz);
        filtro.setTiFiltroMultiplo(tiFiltroMultiplo);
        crit.getDecCriterioFiltroMultiplos().add(filtro);
    }

    public void saveCritRaggrFiltroMultiplo(DecCriterioRaggr crit, DecRegistroUnitaDoc reg,
            DecRegistroUnitaDoc regRange, DecTipoDoc tipoDoc, DecTipoUnitaDoc tipoUD, String tiEsitoVerifFirme,
            String tiFiltroMultiplo) {
        DecCriterioFiltroMultiplo filtro = new DecCriterioFiltroMultiplo();
        filtro.setDecCriterioRaggr(crit);
        filtro.setDecRegistroRangeUnitaDoc(regRange);
        filtro.setDecRegistroUnitaDoc(reg);
        filtro.setDecTipoDoc(tipoDoc);
        filtro.setDecTipoUnitaDoc(tipoUD);
        filtro.setTiEsitoVerifFirme(tiEsitoVerifFirme);
        filtro.setTiFiltroMultiplo(tiFiltroMultiplo);
        crit.getDecCriterioFiltroMultiplos().add(filtro);
    }

    public boolean existNomeCriterio(String nome, BigDecimal idStruttura) {
        String queryStr = "SELECT u FROM DecCriterioRaggr u WHERE u.orgStrut.idStrut = :idstrut and u.nmCriterioRaggr = :nomecrit";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idstrut", longFromBigDecimal(idStruttura));
        query.setParameter("nomecrit", nome);

        return !query.getResultList().isEmpty();
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggr(CriteriRaggruppamentoForm.FiltriCriteriRaggr filtriCriteri)
            throws EMFError {
        return getCriteriRaggr(filtriCriteri.getId_ambiente().parse(), filtriCriteri.getId_ente().parse(),
                filtriCriteri.getId_strut().parse(), filtriCriteri.getNm_criterio_raggr().parse(),
                filtriCriteri.getFl_criterio_raggr_standard().parse(),
                filtriCriteri.getFl_criterio_raggr_fisc().parse(), filtriCriteri.getTi_valid_elenco().parse(),
                filtriCriteri.getTi_mod_valid_elenco().parse(), filtriCriteri.getTi_gest_elenco_criterio().parse(),
                filtriCriteri.getId_registro_unita_doc().parse(), filtriCriteri.getId_tipo_unita_doc().parse(),
                filtriCriteri.getId_tipo_doc().parse(), filtriCriteri.getAa_key_unita_doc().parse(),
                filtriCriteri.getCriterio_attivo().parse());
    }

    public List<DecCriterioRaggr> retrieveDecCriterioRaggrList(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, String nmCriterioRaggr) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecCriterioRaggr u ");
        String whereWord = "WHERE ";
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.orgStrut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.orgStrut.orgEnte.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.orgStrut.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        if (nmCriterioRaggr != null) {
            queryStr.append(whereWord).append("u.nmCriterioRaggr = :nmCriterioRaggr");
        }
        queryStr.append(" ORDER BY u.nmCriterioRaggr ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        }
        if (idEnte != null) {
            query.setParameter("idEnte", longFromBigDecimal(idEnte));
        }
        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }
        if (nmCriterioRaggr != null) {
            query.setParameter("nmCriterioRaggr", nmCriterioRaggr);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        return query.getResultList();
    }

    public List<DecCriterioRaggr> retrieveDecCriterioRaggrList(BigDecimal idStruttura) {
        return retrieveDecCriterioRaggrList(null, null, idStruttura, null);
    }

    public boolean deleteDecCriterioRaggr(LogParam param, BigDecimal idStrut, String nmCriterioRaggr)
            throws ParerUserError {
        boolean result;

        DecCriterioRaggr row = getDecCriterioRaggrByStrutturaAndCriterio(idStrut, nmCriterioRaggr);
        if (row != null && row.getVolVolumeConservs().isEmpty() && row.getElvElencoVers().isEmpty()) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                    new BigDecimal(row.getIdCriterioRaggr()), param.getNomePagina());
            // Rimuovo il record
            getEntityManager().remove(row);
            getEntityManager().flush();
            log.info("Cancellazione criterio di raggruppamento {} della struttura {} avvenuta con successo!",
                    nmCriterioRaggr, idStrut);
            result = true;
        } else {
            if (row == null) {
                throw new ParerUserError("Errore nell'eliminazione del criterio " + nmCriterioRaggr
                        + ", non ci sono record per idStrut " + idStrut + " e nmCriterioRaggr " + nmCriterioRaggr);
            }
            throw new ParerUserError("Errore nell'eliminazione del criterio " + row.getNmCriterioRaggr()
                    + ", il criterio è collegato a dei volumi od a degli elenchi di versamento");
        }
        return result;
    }

    public DecCriterioFiltroMultiploTableBean getCriteriRaggrFiltri(BigDecimal idCriterioRaggr,
            String tiFiltroMultiplo) {
        String whereWord = " and ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT f FROM DecCriterioFiltroMultiplo f " + "WHERE f.decCriterioRaggr.idCriterioRaggr = :idcrit ");
        if (tiFiltroMultiplo != null) {
            queryStr.append(whereWord).append("f.tiFiltroMultiplo = :filtro ");
        }
        queryStr.append("ORDER BY f.tiFiltroMultiplo");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idcrit", longFromBigDecimal(idCriterioRaggr));
        if (tiFiltroMultiplo != null) {
            query.setParameter("filtro", tiFiltroMultiplo);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<DecCriterioFiltroMultiplo> listaFiltriCritRaggr = query.getResultList();
        DecCriterioFiltroMultiploTableBean listaFiltriTableBean = new DecCriterioFiltroMultiploTableBean();
        try {
            if (listaFiltriCritRaggr != null && !listaFiltriCritRaggr.isEmpty()) {
                listaFiltriTableBean = (DecCriterioFiltroMultiploTableBean) Transform
                        .entities2TableBean(listaFiltriCritRaggr);
                if (tiFiltroMultiplo.equals(ApplEnum.TipoFiltroMultiploCriteriRaggr.RANGE_REGISTRO_UNI_DOC.name())) {
                    for (DecCriterioFiltroMultiplo d : listaFiltriCritRaggr) {
                        for (DecCriterioFiltroMultiploRowBean row : listaFiltriTableBean) {
                            if (d.getIdCriterioFiltroMult() == row.getIdCriterioFiltroMult().longValue()) {
                                row.setIdRegistroRangeUnitaDoc(
                                        new BigDecimal(d.getDecRegistroRangeUnitaDoc().getIdRegistroUnitaDoc()));
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaFiltriTableBean;
    }

    public DecCriterioRaggrTableBean getCriteriRaggrbyId(BigDecimal id, BigDecimal idStruttura) {
        final String whereWord = " and ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT u FROM DecCriterioRaggr u WHERE u.orgStrut.idStrut = :idstrut");
        if (id != null) {
            queryStr.append(whereWord).append("u.idCriterioRaggr = :id");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idstrut", longFromBigDecimal(idStruttura));
        if (id != null) {
            query.setParameter("id", longFromBigDecimal(id));
        }
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<DecCriterioRaggr> listaCritRaggr = query.getResultList();
        DecCriterioRaggrTableBean listaCritRaggrTableBean = new DecCriterioRaggrTableBean();
        try {
            if (listaCritRaggr != null && !listaCritRaggr.isEmpty()) {
                listaCritRaggrTableBean = (DecCriterioRaggrTableBean) Transform.entities2TableBean(listaCritRaggr);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // riformatto la data da presentare a video
        Calendar c = Calendar.getInstance();

        for (int i = 0; i < listaCritRaggrTableBean.size(); i++) {
            DecCriterioRaggrRowBean row = listaCritRaggrTableBean.getRow(i);
            if (row.getDtCreazioneUnitaDocDa() != null) {
                Date dataDa = row.getDtCreazioneUnitaDocDa();
                c.setTime(dataDa);
                int oreDa = c.get(Calendar.HOUR_OF_DAY);
                int minutiDa = c.get(Calendar.MINUTE);
                row.setBigDecimal("ore_dt_creazione_unita_doc_da", new BigDecimal(oreDa));
                row.setBigDecimal("minuti_dt_creazione_unita_doc_da", new BigDecimal(minutiDa));
            }
            if (row.getDtCreazioneUnitaDocA() != null) {
                Date dataA = row.getDtCreazioneUnitaDocA();
                c.setTime(dataA);
                int oreA = c.get(Calendar.HOUR_OF_DAY);
                int minutiA = c.get(Calendar.MINUTE);

                row.setBigDecimal("ore_dt_creazione_unita_doc_a", new BigDecimal(oreA));
                row.setBigDecimal("minuti_dt_creazione_unita_doc_a", new BigDecimal(minutiA));
            }
        }

        return listaCritRaggrTableBean;
    }

    public OrgStrut getOrgStrutById(BigDecimal idStruttura) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM OrgStrut u WHERE u.idStrut = :idstrut");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idstrut", longFromBigDecimal(idStruttura));
        return (OrgStrut) query.getSingleResult();
    }

    public DecCriterioRaggrRowBean getDecCriterioRaggrById(BigDecimal idCriterioRaggr) {
        DecCriterioRaggrRowBean critRB = new DecCriterioRaggrRowBean();
        try {
            DecCriterioRaggr criterioRaggr = getEntityManager().find(DecCriterioRaggr.class,
                    idCriterioRaggr.longValue());
            if (criterioRaggr != null) {
                // Info criterio
                critRB = (DecCriterioRaggrRowBean) Transform.entity2RowBean(criterioRaggr);
                // Info ambiente
                String tiGestElencoNoStd = configurationHelper.getValoreParamApplicByStrut(
                        CostantiDB.ParametroAppl.TI_GEST_ELENCO_NOSTD,
                        BigDecimal.valueOf(criterioRaggr.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente()),
                        BigDecimal.valueOf(criterioRaggr.getOrgStrut().getIdStrut()));
                String tiGestElencoStdFisc = configurationHelper.getValoreParamApplicByStrut(
                        CostantiDB.ParametroAppl.TI_GEST_ELENCO_STD_FISC,
                        BigDecimal.valueOf(criterioRaggr.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente()),
                        BigDecimal.valueOf(criterioRaggr.getOrgStrut().getIdStrut()));
                String tiGestElencoStdNofisc = configurationHelper.getValoreParamApplicByStrut(
                        CostantiDB.ParametroAppl.TI_GEST_ELENCO_STD_NOFISC,
                        BigDecimal.valueOf(criterioRaggr.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente()),
                        BigDecimal.valueOf(criterioRaggr.getOrgStrut().getIdStrut()));
                critRB.setString("ti_gest_elenco_std_nofisc", tiGestElencoStdNofisc);
                critRB.setString("ti_gest_elenco_nostd", tiGestElencoNoStd);
                critRB.setString("ti_gest_elenco_std_fisc", tiGestElencoStdFisc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return critRB;
    }

    /**
     * Verifico se i tipi documento passati in ricerca sono tutti principali
     *
     * @param idTipiDocumentoList
     *            lista id tipi documento
     *
     * @return true o false
     */
    public boolean areAllTipiDocPrincipali(List<BigDecimal> idTipiDocumentoList) {
        String queryStr = "SELECT u FROM DecTipoDoc u " + "WHERE u.flTipoDocPrincipale = '1' ";

        if (idTipiDocumentoList != null && !idTipiDocumentoList.isEmpty()) {
            queryStr = queryStr + "AND u.idTipoDoc IN (:idTipiDocList) ";
        }
        Query query = getEntityManager().createQuery(queryStr);
        if (idTipiDocumentoList != null && !idTipiDocumentoList.isEmpty()) {
            query.setParameter("idTipiDocList", longListFrom(idTipiDocumentoList));
        }
        List<DecTipoDoc> listaTipiDoc = query.getResultList();
        return listaTipiDoc.size() == idTipiDocumentoList.size();
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggrFromAmministrazioneStruttura(BigDecimal idStrut,
            List<BigDecimal> idRegistroUnitaDocList, List<BigDecimal> idTipoUnitaDocList,
            List<BigDecimal> idTipoDocList) {
        return listaCriteriToTableBean(getDecVRicCriterioRaggrsByAmministrazioneStruttura(idStrut,
                idRegistroUnitaDocList, idTipoUnitaDocList, idTipoDocList));
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggrFromStruttura(BigDecimal idStrut, boolean filterValid) {
        return listaCriteriToTableBean(getDecVRicCriterioRaggrsByStruttura(idStrut, filterValid));
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggrFromRegistroNoRange(BigDecimal idStrut,
            BigDecimal idRegistroUnitaDoc) {

        return listaCriteriToTableBean(getDecVRicCriterioRaggrsByRegistroNoRange(idStrut, idRegistroUnitaDoc));
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggrFromTipoUnitaDoc(BigDecimal idStrut,
            BigDecimal idTipoUnitaDoc) {
        return listaCriteriToTableBean(getDecVRicCriterioRaggrsByTipoUnitaDoc(idStrut, idTipoUnitaDoc));
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggrFromTipoDoc(BigDecimal idStrut, BigDecimal idTipoDoc) {
        return listaCriteriToTableBean(getDecVRicCriterioRaggrsByTipoDoc(idStrut, idTipoDoc));
    }

    private DecVRicCriterioRaggrTableBean listaCriteriToTableBean(List<DecVRicCriterioRaggr> listaCritRaggr) {

        DecVRicCriterioRaggrTableBean critRaggrTableBean = new DecVRicCriterioRaggrTableBean();
        DecVRicCriterioRaggrRowBean critRaggrRowBean;

        try {
            if (listaCritRaggr != null && !listaCritRaggr.isEmpty()) {
                for (DecVRicCriterioRaggr scriteriato : listaCritRaggr) {
                    critRaggrRowBean = (DecVRicCriterioRaggrRowBean) Transform.entity2RowBean(scriteriato);
                    critRaggrRowBean.setString("nm_ente_nm_strut",
                            scriteriato.getNmEnte() + " - " + scriteriato.getNmStrut());
                    String cdRegistro = null;
                    String aaUnitaDoc = null;
                    if (scriteriato.getCdRegistroUnitaDoc() != null) {
                        cdRegistro = scriteriato.getCdRegistroUnitaDoc();
                    } else if (scriteriato.getCdRegistroRangeUnitaDoc() != null) {
                        cdRegistro = scriteriato.getCdRegistroRangeUnitaDoc();
                    }
                    critRaggrRowBean.setString("cd_registro", cdRegistro);
                    if (scriteriato.getAaKeyUnitaDoc() != null) {
                        aaUnitaDoc = "" + scriteriato.getAaKeyUnitaDoc();
                    } else if (scriteriato.getAaKeyUnitaDocDa() != null) {
                        aaUnitaDoc = scriteriato.getAaKeyUnitaDocDa() + " - " + scriteriato.getAaKeyUnitaDocA();
                    }

                    critRaggrRowBean.setString("aa_unita_doc", aaUnitaDoc);
                    critRaggrTableBean.add(critRaggrRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return critRaggrTableBean;

    }

    public boolean existElvElencoVersPerCriterioRaggr(BigDecimal idCriterioRaggr) {
        String queryStr = "SELECT elencoVers FROM ElvElencoVer elencoVers "
                + "WHERE EXISTS (SELECT criterioRaggr FROM DecCriterioRaggr criterioRaggr WHERE criterioRaggr.idCriterioRaggr = :idCriterioRaggr AND elencoVers.decCriterioRaggr = criterioRaggr)";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idCriterioRaggr", idCriterioRaggr.longValue());
        return !query.getResultList().isEmpty();
    }

    public void bulkDeleteCriteriRaggr(List<Long> idCriterioRaggrList) {
        if (!idCriterioRaggrList.isEmpty()) {
            String queryStr = "DELETE FROM DecCriterioRaggr criterioRaggr "
                    + "WHERE criterioRaggr.idCriterioRaggr IN :idCriterioRaggrList";
            Query q = getEntityManager().createQuery(queryStr);
            q.setParameter("idCriterioRaggrList", idCriterioRaggrList);
            q.executeUpdate();
            getEntityManager().flush();
        }
    }

    /**
     * Recupera i criteri di raggruppamento che hanno come campo: - l'anno passato come parametro e - il registro
     * passato come parametro oppure un tipo ud legato al registro passato come parametro
     *
     * @param idRegistroUnitaDoc
     *            id registro unita doc
     * @param aaKeyUnitaDoc
     *            anno unita doc
     *
     * @return lista oggetti di tipo {@link DecCriterioRaggr}
     */
    public List<DecCriterioRaggr> getDecCriterioRaggrRegistroOTipiUdAssociatiList(BigDecimal idRegistroUnitaDoc,
            int aaKeyUnitaDoc) {
        Query query = getEntityManager()
                .createQuery("SELECT criterioRaggr FROM DecCriterioFiltroMultiplo criterioFiltroMultiplo "
                        + "JOIN criterioFiltroMultiplo.decCriterioRaggr criterioRaggr "
                        + "JOIN criterioFiltroMultiplo.decRegistroUnitaDoc registro "
                        // Prendo o il registro passato come parametro o un tipo ud legato al registro passato come
                        // parametro
                        + "WHERE (registro.idRegistroUnitaDoc = :idRegistroUnitaDoc "
                        + "OR EXISTS (SELECT tipoUnitaDoc FROM DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso "
                        + "             JOIN tipoUnitaDocAmmesso.decTipoUnitaDoc tipoUnitaDoc "
                        + "             WHERE tipoUnitaDocAmmesso.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc)) "
                        //
                        + "AND criterioRaggr.aaKeyUnitaDoc = :aaKeyUnitaDoc "
                        + "AND criterioRaggr.flCriterioRaggrStandard = '1' ");
        query.setParameter("idRegistroUnitaDoc", longFromBigDecimal(idRegistroUnitaDoc));
        query.setParameter("aaKeyUnitaDoc", bigDecimalFromInteger(aaKeyUnitaDoc));
        return query.getResultList();
    }

    /**
     * Restituisce una Stringa con i seguenti possibili valori - 1 = ESISTE UN SOLO Criterio Stardard ATTIVO - 0 =
     * ESISTONO ASSOCIAZIONI ad almeno un criterio non standard - 2 = non è associata a criteri di raggruppamento (né
     * standard né non standard) o se tutti i criteri cui è associata sono disattivi alla data
     *
     * NB. Il valore 2 è utilizzato al posto di NULL perché il framework lo gestisce (per altri casi, correttamente)
     * come se fosse il valore FALSE
     *
     * @param idTipoDato
     *            id del tipo dato
     * @param tipoDato
     *            registro, tipo ud oppure tipo doc
     *
     * @return String criterio
     */
    public String getCriterioStandardPerTipoDatoAnno(long idTipoDato, TipoDato tipoDato) {
        String queryStr = "SELECT criterio FROM DecCriterioFiltroMultiplo multiplo "
                + "JOIN multiplo.decCriterioRaggr criterio " + "WHERE criterio.flCriterioRaggrStandard = :flagStandard "
                + "AND criterio.dtSoppres >= :data AND criterio.dtIstituz <= :data ";
        Date now = Calendar.getInstance().getTime();

        switch (tipoDato) {
        case REGISTRO:
            queryStr = queryStr
                    + "AND (multiplo.decRegistroUnitaDoc.idRegistroUnitaDoc = :idTipoDato OR multiplo.decRegistroRangeUnitaDoc.idRegistroUnitaDoc = :idTipoDato) ";
            break;
        case TIPO_UNITA_DOC:
            queryStr = queryStr + "AND multiplo.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoDato ";
            break;
        case TIPO_DOC:
            queryStr = queryStr + "AND multiplo.decTipoDoc.idTipoDoc = :idTipoDato ";
            break;
        }

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idTipoDato", idTipoDato);
        query.setParameter("flagStandard", "1");
        query.setParameter("data", now);

        List<DecCriterioRaggr> listStandard = query.getResultList();
        query.setParameter("idTipoDato", idTipoDato);
        query.setParameter("flagStandard", "0");
        query.setParameter("data", now);
        List<DecCriterioRaggr> listNotStandard = query.getResultList();

        String risultato = "2";
        if (!listStandard.isEmpty() && listStandard.size() == 1 && listNotStandard.isEmpty()) {
            risultato = "1";
        } else {
            if (!listStandard.isEmpty() && !listNotStandard.isEmpty()) {
                risultato = "0";
            }
        }
        return risultato;
    }

    public boolean existsCriterioPerTipoDato(Long idTipoDato, TipoDato tipoDato, String flCriterioRaggrStandard,
            String flCriterioRaggrFisc) {
        StringBuilder queryStr = new StringBuilder("SELECT COUNT(criterio) FROM DecCriterioFiltroMultiplo multiplo "
                + "JOIN multiplo.decCriterioRaggr criterio "
                + "WHERE criterio.dtSoppres >= :data AND criterio.dtIstituz <= :data ");
        String whereWord = " AND ";
        if (flCriterioRaggrStandard != null) {
            queryStr.append(whereWord).append("criterio.flCriterioRaggrStandard = :flCriterioRaggrStandard ");
            whereWord = " AND ";
        }
        if (flCriterioRaggrFisc != null) {
            queryStr.append(whereWord).append("criterio.flCriterioRaggrFisc = :flCriterioRaggrFisc ");
            whereWord = " AND ";
        }

        switch (tipoDato) {
        case REGISTRO:
            queryStr.append(whereWord).append(
                    " (multiplo.decRegistroUnitaDoc.idRegistroUnitaDoc = :idTipoDato OR multiplo.decRegistroRangeUnitaDoc.idRegistroUnitaDoc = :idTipoDato) ");
            break;
        case TIPO_UNITA_DOC:
            queryStr.append(whereWord).append(" multiplo.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoDato ");
            break;
        case TIPO_DOC:
            queryStr.append(whereWord).append(" multiplo.decTipoDoc.idTipoDoc = :idTipoDato ");
            break;
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (flCriterioRaggrStandard != null) {
            query.setParameter("flCriterioRaggrStandard", flCriterioRaggrStandard);
        }
        if (flCriterioRaggrFisc != null) {
            query.setParameter("flCriterioRaggrFisc", flCriterioRaggrFisc);
        }
        query.setParameter("idTipoDato", idTipoDato);
        Date now = Calendar.getInstance().getTime();
        query.setParameter("data", now);
        return (Long) query.getSingleResult() > 0;
    }

    public List<DecCriterioRaggr> getCriteriPerTipoDato(Long idTipoDato, TipoDato tipoDato,
            String flCriterioRaggrStandard, String flCriterioRaggrFisc) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT criterio FROM DecCriterioFiltroMultiplo multiplo " + "JOIN multiplo.decCriterioRaggr criterio "
                        + "WHERE criterio.dtSoppres >= :data AND criterio.dtIstituz <= :data ");
        String whereWord = " AND ";
        if (flCriterioRaggrStandard != null) {
            queryStr.append(whereWord).append("criterio.flCriterioRaggrStandard = :flCriterioRaggrStandard ");
            whereWord = " AND ";
        }
        if (flCriterioRaggrFisc != null) {
            queryStr.append(whereWord).append("criterio.flCriterioRaggrFisc = :flCriterioRaggrFisc ");
            whereWord = " AND ";
        }

        switch (tipoDato) {
        case REGISTRO:
            queryStr.append(whereWord).append(
                    " (multiplo.decRegistroUnitaDoc.idRegistroUnitaDoc = :idTipoDato OR multiplo.decRegistroRangeUnitaDoc.idRegistroUnitaDoc = :idTipoDato) ");
            break;
        case TIPO_UNITA_DOC:
            queryStr.append(whereWord).append(" multiplo.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoDato ");
            break;
        case TIPO_DOC:
            queryStr.append(whereWord).append(" multiplo.decTipoDoc.idTipoDoc = :idTipoDato ");
            break;
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (flCriterioRaggrStandard != null) {
            query.setParameter("flCriterioRaggrStandard", flCriterioRaggrStandard);
        }
        if (flCriterioRaggrFisc != null) {
            query.setParameter("flCriterioRaggrFisc", flCriterioRaggrFisc);
        }
        query.setParameter("idTipoDato", idTipoDato);
        Date now = Calendar.getInstance().getTime();
        query.setParameter("data", now);
        return query.getResultList();
    }

    public List<DecCriterioRaggr> getCriteriPerAssociazioneRegistroTipoUd(Long idRegistroUnitaDoc,
            Long idTipoUnitaDoc) {
        String queryStr = "SELECT criterio FROM DecCriterioFiltroMultiplo multiplo "
                + "JOIN multiplo.decCriterioRaggr criterio "
                + "WHERE (multiplo.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc OR multiplo.decRegistroRangeUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc) "
                + "AND multiplo.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        return query.getResultList();
    }

    public DecVCreaCritRaggrRegistro getDecVCreaCritRaggrRegistro(Long idRegistroUnitaDoc) {
        String queryStr = "SELECT u FROM DecVCreaCritRaggrRegistro u "
                + "WHERE u.idRegistroUnitaDoc = :idRegistroUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idRegistroUnitaDoc", bigDecimalFromLong(idRegistroUnitaDoc));
        List<DecVCreaCritRaggrRegistro> list = query.getResultList();
        return list.get(0);
    }

    public DecVCreaCritRaggrTipoUd getDecVCreaCritRaggrTipoUd(Long idTipoUnitaDoc) {
        String queryStr = "SELECT u FROM DecVCreaCritRaggrTipoUd u " + "WHERE u.idTipoUnitaDoc = :idTipoUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoUnitaDoc", bigDecimalFromLong(idTipoUnitaDoc));
        List<DecVCreaCritRaggrTipoUd> list = query.getResultList();
        return list.get(0);
    }

    public DecVCreaCritRaggrTipoDoc getDecVCreaCritRaggrTipoDoc(Long idTipoDoc) {
        String queryStr = "SELECT u FROM DecVCreaCritRaggrTipoDoc u " + "WHERE u.idTipoDoc = :idTipoDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoDoc", bigDecimalFromLong(idTipoDoc));
        List<DecVCreaCritRaggrTipoDoc> list = query.getResultList();
        return list.get(0);
    }

    /**
     * Verifico se i registri passati in input sono tutti fiscali
     *
     * @param idRegistroKeyUnitaDocList
     *            id registro chiave unita doc
     *
     * @return true/false
     *
     * @throws ParerUserError
     *             errore generico
     */
    public boolean areAllRegistriFiscali(List<BigDecimal> idRegistroKeyUnitaDocList) throws ParerUserError {
        if (!idRegistroKeyUnitaDocList.isEmpty()) {
            String queryStr = "SELECT COUNT(registroUnitaDoc) FROM DecRegistroUnitaDoc registroUnitaDoc "
                    + "WHERE registroUnitaDoc.flRegistroFisc = '1' "
                    + "AND registroUnitaDoc.idRegistroUnitaDoc IN :idRegistroKeyUnitaDocList ";

            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idRegistroKeyUnitaDocList", longListFrom(idRegistroKeyUnitaDocList));
            Long numRegistriFiscali = (Long) query.getSingleResult();
            return numRegistriFiscali == idRegistroKeyUnitaDocList.size();
        } else {
            throw new ParerUserError("Errore: lista registri fiscali da controllare vuota!");
        }
    }

    /**
     * Verifico se i tipi ud passati in input hanno associati solo registri fiscali
     *
     * @param idTipoUnitaDocList
     *            lista id tipo unita doc
     *
     * @return true/false
     *
     * @throws ParerUserError
     *             errore generico
     */
    public boolean areAllRegistriAssociatiFiscali(List<BigDecimal> idTipoUnitaDocList) throws ParerUserError {
        if (!idTipoUnitaDocList.isEmpty()) {
            String queryStr = "SELECT DISTINCT registroUnitaDoc FROM DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso "
                    + "JOIN tipoUnitaDocAmmesso.decTipoUnitaDoc tipoUnitaDoc "
                    + "JOIN tipoUnitaDocAmmesso.decRegistroUnitaDoc registroUnitaDoc "
                    + "WHERE tipoUnitaDoc.idTipoUnitaDoc IN :idTipoUnitaDocList ";
            // Una volta recuperati tutti i registri di tutti i tipi ud
            // (metto in DISTINCT per evitare di avere registri ripetuti associati a tipi ud diverse)
            // li scorro per verificare se sono tutti fiscali. Se la lista da controllare è vuota, è come se fosse false
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idTipoUnitaDocList", longListFrom(idTipoUnitaDocList));
            List<DecRegistroUnitaDoc> registroUnitaDocList = query.getResultList();
            int countRegAssFisc = 0;
            for (DecRegistroUnitaDoc registroUnitaDoc : registroUnitaDocList) {
                if (registroUnitaDoc.getFlRegistroFisc().equals("1")) {
                    countRegAssFisc++;
                }
            }
            if (!registroUnitaDocList.isEmpty()) {
                return countRegAssFisc == registroUnitaDocList.size();
            } else {
                return false;
            }
        } else {
            throw new ParerUserError("Errore: lista tipi ud da controllare vuota!");
        }
    }

    public String getFlCriterioRaggrFiscMessage(BigDecimal idCriterioRaggr) {
        String queryStr = "SELECT calcCriterioFisc.dsMessaggio FROM DecVCalcCriterioFisc calcCriterioFisc "
                + "WHERE calcCriterioFisc.idCriterioRaggr = :idCriterioRaggr ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idCriterioRaggr", idCriterioRaggr);
        List<String> list = query.getResultList();
        return list.get(0);
    }

    public List<String> getCriteriNonCoerenti(BigDecimal idTipoUnitaDoc) {
        String queryStr = "SELECT chkCriteriByTipoUd.dsCriterioNonCoerente FROM DecVChkCriteriByTipoUd chkCriteriByTipoUd "
                + "WHERE chkCriteriByTipoUd.decVChkCriteriByTipoUdId.idTipoUnitaDoc = :idTipoUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        return query.getResultList();
    }

    public OrgStrutRowBean getOrgStrutRowBeanById(BigDecimal idStrut) {
        OrgStrutRowBean strutRB = new OrgStrutRowBean();
        try {
            strutRB = (OrgStrutRowBean) Transform
                    .entity2RowBean(getEntityManager().find(OrgStrut.class, idStrut.longValue()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return strutRB;
    }

    public DecCriterioRaggr getDecCriterioRaggrByStrutturaCorrenteAndCriterio(BigDecimal idStrutCorrente,
            String nmCriterioRaggr) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecCriterioRaggr u ");
        String whereWord = "WHERE ";

        if (idStrutCorrente != null) {
            queryStr.append(whereWord).append("u.orgStrut.idStrut = :idStrutCorrente ");
            whereWord = "AND ";
        }

        if (nmCriterioRaggr != null) {
            queryStr.append(whereWord).append("u.nmCriterioRaggr = :nmCriterioRaggr ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrutCorrente != null) {
            query.setParameter("idStrutCorrente", longFromBigDecimal(idStrutCorrente));
        }

        if (nmCriterioRaggr != null) {
            query.setParameter("nmCriterioRaggr", nmCriterioRaggr);
        }

        List<DecCriterioRaggr> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public DecCriterioRaggr getDecCriterioRaggrByStrutturaAndCriterio(BigDecimal idStruttura, String nome) {
        DecCriterioRaggr decCriterioRaggr;// Se c'è il parametro nome, carico il criterio di raggruppamento
                                          // corrispondente
        String queryStr = "SELECT u FROM DecCriterioRaggr u WHERE u.orgStrut.idStrut = :idstrut and u.nmCriterioRaggr = :nomecrit";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idstrut", longFromBigDecimal(idStruttura));
        query.setParameter("nomecrit", nome);
        decCriterioRaggr = (DecCriterioRaggr) query.getSingleResult();
        return decCriterioRaggr;
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggr(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            String nmCriterioRaggr, String flCriterioRaggrStandard, String flCriterioRaggrFisc, String tiValidElenco,
            String tiModValidElenco, String tiGestElencoCriterio, BigDecimal idRegistroUnitaDoc,
            BigDecimal idTipoUnitaDoc, BigDecimal idTipoDoc, BigDecimal aaKeyUnitaDoc, String criterioAttivo) {
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT new it.eng.parer.viewEntity.DecVRicCriterioRaggr "
                + "(u.idAmbiente, u.nmAmbiente, u.idEnte, u.nmEnte, u.idStrut, u.nmStrut, u.id.idCriterioRaggr, u.nmCriterioRaggr, u.nmTipoUnitaDoc, "
                + "u.cdRegistroUnitaDoc, u.cdRegistroRangeUnitaDoc, u.nmTipoDoc, u.flCriterioRaggrStandard, u.flCriterioRaggrFisc, u.tiValidElenco, u.tiModValidElenco, "
                + "u.tiGestElencoCriterio, u.aaKeyUnitaDoc, u.aaKeyUnitaDocDa, u.aaKeyUnitaDocA, u.niMaxComp, u.dsScadChius, u.dtIstituz, u.dtSoppres) FROM DecVRicCriterioRaggr u ");
        String whereWord = "WHERE ";/* Inserimento nella query del filtro ID_AMBIENTE */
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        /* Inserimento nella query del filtro ID_ENTE */
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        /* Inserimento nella query del filtro ID_STRUT */
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        if (nmCriterioRaggr != null) {
            queryStr.append(whereWord).append("UPPER(u.nmCriterioRaggr) LIKE :nmCriterioRaggr ");
        }
        if (flCriterioRaggrStandard != null) {
            queryStr.append(whereWord).append("u.flCriterioRaggrStandard = :flCriterioRaggrStandard ");
        }
        if (flCriterioRaggrFisc != null) {
            queryStr.append(whereWord).append("u.flCriterioRaggrFisc = :flCriterioRaggrFisc ");
        }
        if (tiValidElenco != null) {
            queryStr.append(whereWord).append("u.tiValidElenco = :tiValidElenco ");
        }
        if (tiModValidElenco != null) {
            queryStr.append(whereWord).append("u.tiModValidElenco = :tiModValidElenco ");
        }
        if (tiGestElencoCriterio != null) {
            queryStr.append(whereWord).append("u.tiGestElencoCriterio = :tiGestElencoCriterio ");
        }
        if (idRegistroUnitaDoc != null) {
            queryStr.append(whereWord).append(
                    "(u.id.idRegistroUnitaDoc = :idRegistroUnitaDoc OR u.id.idRegistroRangeUnitaDoc = :idRegistroUnitaDoc) ");
        }
        if (idTipoUnitaDoc != null) {
            queryStr.append(whereWord).append("u.id.idTipoUnitaDoc = :idTipoUnitaDoc ");
        }
        if (idTipoDoc != null) {
            queryStr.append(whereWord).append("u.id.idTipoDoc = :idTipoDoc ");
        }
        if (aaKeyUnitaDoc != null) {
            queryStr.append(whereWord).append(
                    "(u.aaKeyUnitaDoc = :aaKeyUnitaDoc OR (u.aaKeyUnitaDocDa <= :aaKeyUnitaDoc AND u.aaKeyUnitaDocA >= :aaKeyUnitaDoc)) ");
        }
        if (criterioAttivo != null) {
            if (criterioAttivo.equals("1")) {
                queryStr.append(whereWord).append("u.dtSoppres >= :data AND u.dtIstituz <= :data ");
            } else {
                queryStr.append(whereWord).append("u.dtSoppres < :data OR u.dtIstituz > :data ");
            }
        }

        queryStr.append("ORDER BY u.nmCriterioRaggr ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }
        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (nmCriterioRaggr != null) {
            query.setParameter("nmCriterioRaggr", "%" + nmCriterioRaggr.toUpperCase() + "%");
        }
        if (flCriterioRaggrStandard != null) {
            query.setParameter("flCriterioRaggrStandard", flCriterioRaggrStandard);
        }
        if (flCriterioRaggrFisc != null) {
            query.setParameter("flCriterioRaggrFisc", flCriterioRaggrFisc);
        }
        if (tiValidElenco != null) {
            query.setParameter("tiValidElenco", tiValidElenco);
        }
        if (tiModValidElenco != null) {
            query.setParameter("tiModValidElenco", tiModValidElenco);
        }
        if (tiGestElencoCriterio != null) {
            query.setParameter("tiGestElencoCriterio", tiGestElencoCriterio);
        }
        if (idRegistroUnitaDoc != null) {
            query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        }
        if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        }
        if (idTipoDoc != null) {
            query.setParameter("idTipoDoc", idTipoDoc);
        }
        if (aaKeyUnitaDoc != null) {
            query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        }
        if (criterioAttivo != null) {
            Calendar dataOdierna = Calendar.getInstance();
            dataOdierna.set(Calendar.HOUR_OF_DAY, 0);
            dataOdierna.set(Calendar.MINUTE, 0);
            dataOdierna.set(Calendar.SECOND, 0);
            dataOdierna.set(Calendar.MILLISECOND, 0);
            query.setParameter("data", dataOdierna.getTime());
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<DecVRicCriterioRaggr> listaCritRaggr = query.getResultList();
        DecVRicCriterioRaggrTableBean critRaggrTableBean = new DecVRicCriterioRaggrTableBean();
        DecVRicCriterioRaggrRowBean critRaggrRowBean;

        try {
            if (listaCritRaggr != null && !listaCritRaggr.isEmpty()) {
                for (DecVRicCriterioRaggr scriteriato : listaCritRaggr) {
                    critRaggrRowBean = (DecVRicCriterioRaggrRowBean) Transform.entity2RowBean(scriteriato);
                    critRaggrRowBean.setString("nm_ente_nm_strut",
                            scriteriato.getNmEnte() + " - " + scriteriato.getNmStrut());
                    String cdRegistro = null;
                    String aaUnitaDoc = null;
                    if (scriteriato.getCdRegistroUnitaDoc() != null) {
                        cdRegistro = scriteriato.getCdRegistroUnitaDoc();
                    } else if (scriteriato.getCdRegistroRangeUnitaDoc() != null) {
                        cdRegistro = scriteriato.getCdRegistroRangeUnitaDoc();
                    }
                    critRaggrRowBean.setString("cd_registro", cdRegistro);
                    if (scriteriato.getAaKeyUnitaDoc() != null) {
                        aaUnitaDoc = "" + scriteriato.getAaKeyUnitaDoc();
                    } else if (scriteriato.getAaKeyUnitaDocDa() != null) {
                        aaUnitaDoc = scriteriato.getAaKeyUnitaDocDa() + " - " + scriteriato.getAaKeyUnitaDocA();
                    }
                    critRaggrRowBean.setString("aa_unita_doc", aaUnitaDoc);
                    critRaggrTableBean.add(critRaggrRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return critRaggrTableBean;
    }

    public List<DecVRicCriterioRaggr> getDecVRicCriterioRaggrsByAmministrazioneStruttura(BigDecimal idStrut,
            List<BigDecimal> idRegistroUnitaDocList, List<BigDecimal> idTipoUnitaDocList,
            List<BigDecimal> idTipoDocList) {
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT new it.eng.parer.viewEntity.DecVRicCriterioRaggr "
                + "(u.idAmbiente, u.nmAmbiente, u.idEnte, u.nmEnte, u.idStrut, u.nmStrut, u.id.idCriterioRaggr, u.nmCriterioRaggr, u.nmTipoUnitaDoc, "
                + "u.cdRegistroUnitaDoc, u.cdRegistroRangeUnitaDoc, u.nmTipoDoc, u.flCriterioRaggrStandard, u.flCriterioRaggrFisc, u.tiValidElenco, u.tiModValidElenco, "
                + "u.tiGestElencoCriterio, u.aaKeyUnitaDoc, u.aaKeyUnitaDocDa, u.aaKeyUnitaDocA, u.niMaxComp, u.dsScadChius, u.dtIstituz, u.dtSoppres) FROM DecVRicCriterioRaggr u "
                + "WHERE u.idStrut = :idStrut ");

        if (!idRegistroUnitaDocList.isEmpty()) {
            queryStr = queryStr.append("AND u.id.idRegistroUnitaDoc IN (:idRegistroUnitaDocList) ");
        }
        if (!idTipoUnitaDocList.isEmpty()) {
            queryStr = queryStr.append("AND u.id.idTipoUnitaDoc IN (:idTipoUnitaDocList) ");
        }
        if (!idTipoDocList.isEmpty()) {
            queryStr = queryStr.append("AND u.id.idTipoDoc IN (:idTipoDocList) ");
        }

        queryStr.append("ORDER BY u.nmCriterioRaggr ASC ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", idStrut);

        if (!idRegistroUnitaDocList.isEmpty()) {
            query.setParameter("idRegistroUnitaDocList", idRegistroUnitaDocList);
        }
        if (!idTipoUnitaDocList.isEmpty()) {
            query.setParameter("idTipoUnitaDocList", idTipoUnitaDocList);
        }
        if (!idTipoDocList.isEmpty()) {
            query.setParameter("idTipoDocList", idTipoDocList);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        return query.getResultList();
    }

    public List<DecVRicCriterioRaggr> getDecVRicCriterioRaggrsByStruttura(BigDecimal idStrut, boolean filterValid) {
        String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.DecVRicCriterioRaggr "
                + "(u.idAmbiente, u.nmAmbiente, u.idEnte, u.nmEnte, u.idStrut, u.nmStrut, "
                + "u.decVRicCriterioRaggrId.idCriterioRaggr, u.nmCriterioRaggr, u.nmTipoUnitaDoc, "
                + "u.cdRegistroUnitaDoc, u.cdRegistroRangeUnitaDoc, u.nmTipoDoc, u.flCriterioRaggrStandard, "
                + "u.flCriterioRaggrFisc, u.tiValidElenco, u.tiModValidElenco, "
                + "u.tiGestElencoCriterio, u.aaKeyUnitaDoc, u.aaKeyUnitaDocDa, u.aaKeyUnitaDocA, u.niMaxComp, u.dsScadChius, "
                + "u.dtIstituz, u.dtSoppres) FROM DecVRicCriterioRaggr u " + "WHERE u.idStrut = :idStrut ";

        if (filterValid) {
            queryStr += " AND u.dtIstituz <= :filterDate AND u.dtSoppres >= :filterDate ";
        }
        queryStr += "ORDER BY u.nmCriterioRaggr ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        return query.getResultList();
    }

    public List<DecVRicCriterioRaggr> getDecVRicCriterioRaggrsByRegistroNoRange(BigDecimal idStrut,
            BigDecimal idRegistroUnitaDoc) {
        String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.DecVRicCriterioRaggr "
                + "(u.idAmbiente, u.nmAmbiente, u.idEnte, u.nmEnte, u.idStrut, u.nmStrut, u.id.idCriterioRaggr, u.nmCriterioRaggr, u.nmTipoUnitaDoc, "
                + "u.cdRegistroUnitaDoc, u.cdRegistroRangeUnitaDoc, u.nmTipoDoc, u.flCriterioRaggrStandard, u.flCriterioRaggrFisc, u.tiValidElenco, u.tiModValidElenco, "
                + "u.tiGestElencoCriterio, u.aaKeyUnitaDoc, u.aaKeyUnitaDocDa, u.aaKeyUnitaDocA, u.niMaxComp, u.dsScadChius, u.dtIstituz, u.dtSoppres) FROM DecVRicCriterioRaggr u "
                + "WHERE u.idStrut = :idStrut AND u.id.idRegistroUnitaDoc = :idRegistroUnitaDoc "
                + "ORDER BY u.nmCriterioRaggr ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        return query.getResultList();
    }

    public List<DecVRicCriterioRaggr> getDecVRicCriterioRaggrsByTipoUnitaDoc(BigDecimal idStrut,
            BigDecimal idTipoUnitaDoc) {
        String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.DecVRicCriterioRaggr "
                + "(u.idAmbiente, u.nmAmbiente, u.idEnte, u.nmEnte, u.idStrut, u.nmStrut, u.id.idCriterioRaggr, u.nmCriterioRaggr, u.nmTipoUnitaDoc, "
                + "u.cdRegistroUnitaDoc, u.cdRegistroRangeUnitaDoc, u.nmTipoDoc, u.flCriterioRaggrStandard, u.flCriterioRaggrFisc, u.tiValidElenco, u.tiModValidElenco, "
                + "u.tiGestElencoCriterio, u.aaKeyUnitaDoc, u.aaKeyUnitaDocDa, u.aaKeyUnitaDocA, u.niMaxComp, u.dsScadChius, u.dtIstituz, u.dtSoppres) FROM DecVRicCriterioRaggr u "
                + "WHERE u.idStrut = :idStrut AND u.id.idTipoUnitaDoc = :idTipoUnitaDoc "
                + "ORDER BY u.nmCriterioRaggr ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        return query.getResultList();
    }

    public List<DecVRicCriterioRaggr> getDecVRicCriterioRaggrsByTipoDoc(BigDecimal idStrut, BigDecimal idTipoDoc) {
        String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.DecVRicCriterioRaggr "
                + "(u.idAmbiente, u.nmAmbiente, u.idEnte, u.nmEnte, u.idStrut, u.nmStrut, u.id.idCriterioRaggr, u.nmCriterioRaggr, u.nmTipoUnitaDoc, "
                + "u.cdRegistroUnitaDoc, u.cdRegistroRangeUnitaDoc, u.nmTipoDoc, u.flCriterioRaggrStandard, u.flCriterioRaggrFisc, u.tiValidElenco, u.tiModValidElenco, "
                + "u.tiGestElencoCriterio, u.aaKeyUnitaDoc, u.aaKeyUnitaDocDa, u.aaKeyUnitaDocA, u.niMaxComp, u.dsScadChius, u.dtIstituz, u.dtSoppres) FROM DecVRicCriterioRaggr u "
                + "WHERE u.idStrut = :idStrut AND u.id.idTipoDoc = :idTipoDoc " + "ORDER BY u.nmCriterioRaggr ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("idTipoDoc", idTipoDoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        return query.getResultList();
    }

    public void deleteDecCriterioFiltroMultiploTipoEsitoVerifFirmeByDecCriterioRaggr(
            DecCriterioRaggr decCriterioRaggr) {
        Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                + "WHERE u.tiFiltroMultiplo = :filtro and u.decCriterioRaggr.idCriterioRaggr = :crit");
        q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_ESITO_VERIF_FIRME.name());
        q.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        q.executeUpdate();
        getEntityManager().flush();
    }

    public void deleteDecCriterioFiltroMultiploByDecCriterioRaggrNotInEsitoVerifFirme(DecCriterioRaggr decCriterioRaggr,
            List<String> esitiVerifFirme) {
        Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                + "WHERE u.tiFiltroMultiplo = :filtro and u.decCriterioRaggr.idCriterioRaggr = :crit and u.tiEsitoVerifFirme NOT IN (:esiti)");
        q.setParameter("esiti", esitiVerifFirme);
        q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_ESITO_VERIF_FIRME.name());
        q.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        q.executeUpdate();
        getEntityManager().flush();
    }

    public List<DecCriterioFiltroMultiplo> getDecCriterioFiltroMultiploByDecCriterioRaggrAndTipoEsitoVerifFirme(
            DecCriterioRaggr decCriterioRaggr, String tipo) {
        Query query = getEntityManager()
                .createQuery("SELECT u FROM DecCriterioFiltroMultiplo u " + "WHERE u.tiEsitoVerifFirme = :tipo "
                        + "and u.tiFiltroMultiplo = :filtro " + "and u.decCriterioRaggr.idCriterioRaggr = :crit");
        query.setParameter("tipo", tipo);
        query.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_ESITO_VERIF_FIRME.name());
        query.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        return query.getResultList();
    }

    public void deleteDecCriterioFiltroMultiploTipoRegistroUniDocByDecCriterioRaggr(DecCriterioRaggr decCriterioRaggr) {
        Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                + "WHERE u.tiFiltroMultiplo = :filtro and u.decCriterioRaggr.idCriterioRaggr = :crit");
        q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.REGISTRO_UNI_DOC.name());
        q.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        q.executeUpdate();
        getEntityManager().flush();
    }

    public void deleteDecCriterioFiltroMultiploByDecCriterioRaggrNotInIdRegistroUnitaDoc(
            DecCriterioRaggr decCriterioRaggr, List<Long> cdRegistroKeyUnitaDocList) {
        Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                + "WHERE u.tiFiltroMultiplo = :filtro " + "and u.decCriterioRaggr.idCriterioRaggr = :crit "
                + "and u.decRegistroUnitaDoc.idRegistroUnitaDoc NOT IN (:regs)");
        q.setParameter("regs", cdRegistroKeyUnitaDocList);
        q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.REGISTRO_UNI_DOC.name());
        q.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        q.executeUpdate();
        getEntityManager().flush();
    }

    public List<DecCriterioFiltroMultiplo> getDecCriterioFiltroMultiploByDecRegistroUnitaDoc(
            DecCriterioRaggr decCriterioRaggr, DecRegistroUnitaDoc reg) {
        Query query = getEntityManager()
                .createQuery("SELECT u FROM DecCriterioFiltroMultiplo u " + "WHERE u.decRegistroUnitaDoc = :reg "
                        + "and u.tiFiltroMultiplo = :filtro " + "and u.decCriterioRaggr.idCriterioRaggr = :crit");
        query.setParameter("reg", reg);
        query.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.REGISTRO_UNI_DOC.name());
        query.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        return query.getResultList();
    }

    public List<DecRegistroUnitaDoc> getDecRegistroUnitaDocByIdRegistroUnitaDoc(List<Long> cdRegistroKeyUnitaDocList) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecRegistroUnitaDoc u ");
        queryStr.append("WHERE u.idRegistroUnitaDoc in :idreg");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idreg", cdRegistroKeyUnitaDocList);
        return query.getResultList();
    }

    public void deleteDecCriterioFiltroMultiploTipoSistemaMigrazByDecCriterioRaggr(DecCriterioRaggr decCriterioRaggr) {
        Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                + "WHERE u.tiFiltroMultiplo = :filtro and u.decCriterioRaggr.idCriterioRaggr = :crit");
        q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name());
        q.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        q.executeUpdate();
    }

    public void deleteDecCriterioRaggrByDecCriterioRaggrNotInNmSistemaMigraz(DecCriterioRaggr decCriterioRaggr,
            List<String> nmSistemaMigrazList) {
        // In caso di modifica, potrei aver eliminato qualche filtro dalle multiselect, che non
        // risulterebbero più presenti nella lista
        // Eseguo perciò una bulk delete sui record non presenti nella lista
        Query q = getEntityManager()
                .createQuery("DELETE FROM DecCriterioFiltroMultiplo u " + "WHERE u.tiFiltroMultiplo = :filtro "
                        + "and u.decCriterioRaggr.idCriterioRaggr = :crit " + "and u.nmSistemaMigraz NOT IN (:tipi)");
        q.setParameter("tipi", nmSistemaMigrazList);
        q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name());
        q.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        q.executeUpdate();
        getEntityManager().flush();
    }

    public List<DecCriterioFiltroMultiplo> getDecCriterioFiltroMultiploByDecCriterioRaggr(
            DecCriterioRaggr decCriterioRaggr, String tipo) {
        Query query = getEntityManager()
                .createQuery("SELECT u FROM DecCriterioFiltroMultiplo u " + "WHERE u.nmSistemaMigraz = :tipo "
                        + "and u.tiFiltroMultiplo = :filtro " + "and u.decCriterioRaggr.idCriterioRaggr = :crit");
        query.setParameter("tipo", tipo);
        query.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name());
        query.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        return query.getResultList();
    }

    public List<String> getOrgUsoSistemaMigrazByNmSistemaMigrazList(BigDecimal idStruttura,
            List<String> nmSistemaMigrazList) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT v.nmSistemaMigraz FROM OrgUsoSistemaMigraz u JOIN u.aplSistemaMigraz v "
                        + "WHERE u.orgStrut.idStrut = :idStrutturain " + "AND v.nmSistemaMigraz is not null ");
        queryStr.append("AND v.nmSistemaMigraz in :nmsistemamigraz");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("nmsistemamigraz", nmSistemaMigrazList);
        query.setParameter("idStrutturain", longFromBigDecimal(idStruttura));
        return query.getResultList();
    }

    public void deleteDecCriterioFiltroMultiploByDecCriterioNotInTipoDocs(DecCriterioRaggr decCriterioRaggr,
            List<Long> nmTipoDocList) {
        Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                + "WHERE u.tiFiltroMultiplo = :filtro " + "and u.decCriterioRaggr.idCriterioRaggr = :crit "
                + "and u.decTipoDoc.idTipoDoc NOT IN (:tipi)");
        q.setParameter("tipi", nmTipoDocList);
        q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_DOC.name());
        q.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        q.executeUpdate();
        getEntityManager().flush();
    }

    public List<DecCriterioFiltroMultiplo> getDecCriterioFiltroMultiplosByTipoDocAndDecCriterio(
            DecCriterioRaggr decCriterioRaggr, DecTipoDoc tipo) {
        Query query = getEntityManager().createQuery("SELECT u FROM DecCriterioFiltroMultiplo u "
                + "WHERE u.decTipoDoc = :tipo and u.tiFiltroMultiplo = :filtro "
                + "and u.decCriterioRaggr.idCriterioRaggr = :crit");
        query.setParameter("tipo", tipo);
        query.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_DOC.name());
        query.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        return query.getResultList();
    }

    public List<DecTipoDoc> getDecTipoDocsByTipoDocList(List<Long> nmTipoDocList) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecTipoDoc u ");
        queryStr.append("WHERE u.idTipoDoc in :idtipodoc");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idtipodoc", nmTipoDocList);
        return query.getResultList();
    }

    public void deleteDecCriterioFiltroMultiploTipoUniDocByDecCriterio(DecCriterioRaggr decCriterioRaggr) {
        Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                + "WHERE u.tiFiltroMultiplo = :filtro and u.decCriterioRaggr.idCriterioRaggr = :crit");
        q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_UNI_DOC.name());
        q.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        q.executeUpdate();
        getEntityManager().flush();
    }

    public void deleteDecCriterioFiltroMultiploTipoDocByDecCriterio(DecCriterioRaggr decCriterioRaggr) {
        Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                + "WHERE u.tiFiltroMultiplo = :filtro and u.decCriterioRaggr.idCriterioRaggr = :crit");
        q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_DOC.name());
        q.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        q.executeUpdate();

        getEntityManager().flush();
    }

    public void deleteDecCriterioFiltroMultiploByDecCriterioNotInTipoUnitaDoc(DecCriterioRaggr decCriterioRaggr,
            List<Long> asList) {
        Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                + "WHERE u.tiFiltroMultiplo = :filtro " + "and u.decCriterioRaggr.idCriterioRaggr = :crit "
                + "and u.decTipoUnitaDoc.idTipoUnitaDoc NOT IN (:tipi)");
        q.setParameter("tipi", asList);
        q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_UNI_DOC.name());
        q.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        q.executeUpdate();
        getEntityManager().flush();
    }

    public List<DecCriterioFiltroMultiplo> getDecCriterioFiltroMultiploList(DecCriterioRaggr decCriterioRaggr,
            DecTipoUnitaDoc tipo) {
        Query query = getEntityManager().createQuery("SELECT u FROM DecCriterioFiltroMultiplo u "
                + "WHERE u.decTipoUnitaDoc = :tipo and u.tiFiltroMultiplo = :filtro "
                + "and u.decCriterioRaggr.idCriterioRaggr = :crit");
        query.setParameter("tipo", tipo);
        query.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_UNI_DOC.name());
        query.setParameter("crit", decCriterioRaggr.getIdCriterioRaggr());
        return query.getResultList();
    }

    public List<DecTipoUnitaDoc> getDecTipoUnitaDocsByTipoUnitaDoc(List<Long> asList) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecTipoUnitaDoc u ");
        queryStr.append("WHERE u.idTipoUnitaDoc in :idtipoud");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idtipoud", asList);
        return query.getResultList();
    }

}
