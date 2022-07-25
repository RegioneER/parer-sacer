package it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.ejb.DatiSpecificiEjb;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.helper.FormatoFileStandardHelper;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.EntitaValida;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.SalvaStrutturaDto;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.AmbientiHelper;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.helper.TipoUnitaDocHelper;
import java.util.Date;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.AplParamApplic;
import it.eng.parer.entity.AplSistemaVersante;
import it.eng.parer.entity.AplValoreParamApplic;
import it.eng.parer.entity.DecAaRegistroUnitaDoc;
import it.eng.parer.entity.DecAaTipoFascicolo;
import it.eng.parer.entity.DecAttribDatiSpec;
import it.eng.parer.entity.DecCategTipoUnitaDoc;
import it.eng.parer.entity.DecCriterioFiltroMultiplo;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.entity.DecEstensioneFile;
import it.eng.parer.entity.DecFormatoFileAmmesso;
import it.eng.parer.entity.DecFormatoFileBusta;
import it.eng.parer.entity.DecFormatoFileDoc;
import it.eng.parer.entity.DecFormatoFileStandard;
import it.eng.parer.entity.DecModelloTipoSerie;
import it.eng.parer.entity.DecParteNumeroFascicolo;
import it.eng.parer.entity.DecParteNumeroRegistro;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecSelCriterioRaggrFasc;
import it.eng.parer.entity.DecTipoCompDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoDocAmmesso;
import it.eng.parer.entity.DecTipoFascicolo;
import it.eng.parer.entity.DecTipoRapprAmmesso;
import it.eng.parer.entity.DecTipoRapprComp;
import it.eng.parer.entity.DecTipoStrutDoc;
import it.eng.parer.entity.DecTipoStrutDocAmmesso;
import it.eng.parer.entity.DecTipoStrutUdReg;
import it.eng.parer.entity.DecTipoStrutUdSisVer;
import it.eng.parer.entity.DecTipoStrutUdXsd;
import it.eng.parer.entity.DecTipoStrutUnitaDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.DecTipoUnitaDocAmmesso;
import it.eng.parer.entity.DecUsoFormatoFileStandard;
import it.eng.parer.entity.DecXsdAttribDatiSpec;
import it.eng.parer.entity.DecXsdDatiSpec;
import it.eng.parer.entity.IamOrganizDaReplic;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgCampoValSubStrut;
import it.eng.parer.entity.OrgRegolaValSubStrut;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.OrgSubStrut;
import it.eng.parer.entity.OrgTipoServizio;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.serie.helper.ModelliSerieHelper;
import it.eng.parer.viewEntity.DecVCalcTiServOnTipoUd;
import it.eng.parer.web.helper.ConfigurationHelper;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class CopiaStruttureEjb {

    private static final Logger logger = LoggerFactory.getLogger(CopiaStruttureEjb.class);

    @EJB
    private TipoUnitaDocHelper tipoUnitaDocHelper;
    @EJB
    private FormatoFileStandardHelper formatoFileStandardHelper;
    @EJB
    private StruttureHelper struttureHelper;
    @EJB
    private AmbientiHelper ambientiHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private DatiSpecificiEjb datiSpecificiEjb;
    @EJB
    private ModelliSerieHelper modelliSerieHelper;

    private static final String ERRORE_MODELLO_TIPO_SEIRE = "Nella struttura duplicata esiste almeno un registro o un tipo unità documentaria associato ad un modello che non è definito nell'ambiente di destinazione. Prima di eseguire la duplicazione occorre definire il modello";

    public OrgStrutCopyResult getOrgStrutCopyFromStrut(OrgStrut oldStrut, OrgStrut newStrut, SalvaStrutturaDto salva,
            boolean isStandard) throws ParerUserError {
        boolean includiCriteriDiRaggruppamento = salva.isCheckIncludiCriteri();
        boolean includiFormati = salva.isCheckIncludiFormati();
        boolean includiTipiFascicolo = salva.isCheckIncludiTipiFascicolo();
        boolean includiElementiDisattivi = salva.isCheckIncludiElementiDisattivi();
        boolean mantieniDateFineValidita = salva.isCheckMantieniDateFineValidita();
        // Inizia l'oggetto con i dati e messaggi di ritorno
        OrgStrutCopyResult result = new OrgStrutCopyResult();
        result.setOrgStrut(newStrut);
        Date dataAttuale = salva.getDataAttuale();

        // Punto 41/b: TIPI UD
        newStrut.setDecTipoUnitaDocs(determinaDecTipoUnitaDocs(oldStrut, dataAttuale, includiElementiDisattivi,
                newStrut, mantieniDateFineValidita, result));
        // Punto 42: REGISTRI
        newStrut.setDecRegistroUnitaDocs(determinaDecRegistroUnitaDocs(oldStrut, dataAttuale, includiElementiDisattivi,
                newStrut, mantieniDateFineValidita, result));
        // Punto 43: FORMATI
        if (includiFormati) {
            newStrut.setDecFormatoFileDocs(determinaDecFormatoFileDocs(newStrut, oldStrut.getDecFormatoFileDocs(),
                    dataAttuale, includiElementiDisattivi, mantieniDateFineValidita));
        }
        // AGGIUNTO DA ME rispetto all'analisi carica i tipi rappresentazione componente
        newStrut.setDecTipoRapprComps(determinaDecTipoRapprComps(oldStrut.getDecTipoRapprComps(), dataAttuale,
                includiElementiDisattivi, newStrut, mantieniDateFineValidita, includiFormati));
        // Punto 44: Tipi Struttura Unita Doc
        elaboraTipiStrutturaUnitaDoc(oldStrut, dataAttuale, includiElementiDisattivi, newStrut,
                mantieniDateFineValidita, result);
        // punto 45: DEC_TIPO_DOC
        newStrut.setDecTipoDocs(determinaDecTipoDocs(oldStrut, dataAttuale, includiElementiDisattivi, newStrut,
                mantieniDateFineValidita));
        // punto 46 e 47: DEC_TIPO_STRUT_DOC e tipi comp doc
        newStrut.setDecTipoStrutDocs(determinaDecTipoStrutDocs(oldStrut, dataAttuale, includiElementiDisattivi,
                includiFormati, newStrut, mantieniDateFineValidita));
        // Punto 48: tipi fascicolo
        if (includiTipiFascicolo) {
            newStrut.setDecTipoFascicolos(determinaDecTipoFascicolos(oldStrut, dataAttuale, includiElementiDisattivi,
                    newStrut, mantieniDateFineValidita, includiCriteriDiRaggruppamento));
        }
        // Punto 49: Importa RegoleValSubStrut
        determinaOrgRegoleValSubStruts(isStandard, oldStrut.getDecTipoUnitaDocs(), newStrut.getDecTipoUnitaDocs(),
                newStrut.getDecTipoDocs(), dataAttuale, includiElementiDisattivi, mantieniDateFineValidita, newStrut);
        // Punto 50: Criteri di raggruppamento
        if (includiCriteriDiRaggruppamento) {
            newStrut.setDecCriterioRaggrs(determinaDecCriterioRaggrs(newStrut, oldStrut.getDecCriterioRaggrs(),
                    dataAttuale, includiElementiDisattivi, mantieniDateFineValidita));
            // legati al registro, tipo ud, tipo doc e fascioli
        }
        // Punto 51: SottoStrutture
        if (isStandard == false) {
            newStrut.setOrgSubStruts(determinaOrgSubStruts(oldStrut, newStrut));
        }

        return result;
    }

    /*
     * Crea una lista di tipi ud da una lista tu tipi Ud di una struttura.
     */
    private List<DecCriterioRaggr> determinaDecCriterioRaggrs(OrgStrut newStrut, List<DecCriterioRaggr> oldList,
            Date dataAttuale, boolean includiElementiDisattivi, boolean mantieniDateSoppOriginali) {
        ArrayList<DecCriterioRaggr> al = null;
        if (oldList != null && (!oldList.isEmpty())) {
            al = new ArrayList();
            for (DecCriterioRaggr crOld : oldList) {
                EntitaValida entita = new EntitaValida(includiElementiDisattivi, mantieniDateSoppOriginali,
                        crOld.getDtIstituz(), crOld.getDtSoppres(), dataAttuale);
                if (entita.isValida()) {
                    DecCriterioRaggr c = new DecCriterioRaggr();
                    al.add(c);
                    c.setOrgStrut(newStrut);
                    c.setAaKeyUnitaDoc(crOld.getAaKeyUnitaDoc());
                    c.setAaKeyUnitaDocA(crOld.getAaKeyUnitaDocA());
                    c.setAaKeyUnitaDocDa(crOld.getAaKeyUnitaDocDa());
                    c.setBlFiltriDatiSpecDoc(crOld.getBlFiltriDatiSpecDoc());
                    c.setCdKeyUnitaDoc(crOld.getCdKeyUnitaDoc());
                    c.setCdKeyUnitaDocA(crOld.getCdKeyUnitaDocA());
                    c.setCdKeyUnitaDocDa(crOld.getCdKeyUnitaDocDa());
                    c.setDlDoc(crOld.getDlDoc());
                    c.setDlOggettoUnitaDoc(crOld.getDlOggettoUnitaDoc());
                    c.setDsAutoreDoc(crOld.getDsAutoreDoc());
                    c.setDsCriterioRaggr(crOld.getDsCriterioRaggr());
                    c.setDtCreazioneUnitaDocA(crOld.getDtCreazioneUnitaDocA());
                    c.setDtCreazioneUnitaDocDa(crOld.getDtCreazioneUnitaDocDa());
                    c.setDtIstituz(entita.getDataInizio());
                    c.setDtSoppres(entita.getDataFine());
                    c.setDtRegUnitaDocA(crOld.getDtRegUnitaDocA());
                    c.setDtRegUnitaDocDa(crOld.getDtRegUnitaDocDa());
                    c.setFlCriterioRaggrFisc(crOld.getFlCriterioRaggrFisc());
                    c.setFlCriterioRaggrStandard(crOld.getFlCriterioRaggrStandard());
                    c.setFlFiltroRangeRegistroKey(crOld.getFlFiltroRangeRegistroKey());
                    c.setFlFiltroRegistroKey(crOld.getFlFiltroRegistroKey());
                    c.setFlFiltroSistemaMigraz(crOld.getFlFiltroSistemaMigraz());
                    c.setFlFiltroTiEsitoVerifFirme(crOld.getFlFiltroTiEsitoVerifFirme());
                    c.setFlFiltroTipoDoc(crOld.getFlFiltroTipoDoc());
                    c.setFlFiltroTipoUnitaDoc(crOld.getFlFiltroTipoUnitaDoc());
                    c.setFlForzaAccettazione(crOld.getFlForzaAccettazione());
                    c.setFlForzaConservazione(crOld.getFlForzaConservazione());
                    c.setFlUnitaDocFirmato(crOld.getFlUnitaDocFirmato());
                    c.setNiMaxComp(crOld.getNiMaxComp());
                    c.setNiMaxElenchiByGg(crOld.getNiMaxElenchiByGg());
                    c.setNiTempoScadChius(crOld.getNiTempoScadChius());
                    c.setNtCriterioRaggr(crOld.getNtCriterioRaggr());
                    c.setNmCriterioRaggr(crOld.getNmCriterioRaggr());
                    c.setTiConservazione(crOld.getTiConservazione());
                    c.setTiGestElencoCriterio(crOld.getTiGestElencoCriterio());
                    c.setTiModValidElenco(crOld.getTiModValidElenco());
                    c.setTiScadChiusVolume(crOld.getTiScadChiusVolume());
                    c.setTiTempoScadChius(crOld.getTiTempoScadChius());
                    c.setTiValidElenco(crOld.getTiValidElenco());
                    c.setTiModValidElenco(crOld.getTiModValidElenco());
                    tipoUnitaDocHelper.getEntityManager().persist(c);
                    // Gestione dei criteri filtro multiplo associati alle ud, doc e registri
                    if (crOld.getDecCriterioFiltroMultiplos() != null
                            && (!crOld.getDecCriterioFiltroMultiplos().isEmpty())) {
                        ArrayList<DecCriterioFiltroMultiplo> alFiltro = new ArrayList();
                        for (DecCriterioFiltroMultiplo filtroOld : crOld.getDecCriterioFiltroMultiplos()) {
                            DecCriterioFiltroMultiplo filtroNew = new DecCriterioFiltroMultiplo();
                            filtroNew.setDecCriterioRaggr(c);
                            filtroNew.setNmSistemaMigraz(filtroOld.getNmSistemaMigraz());
                            filtroNew.setTiEsitoVerifFirme(filtroOld.getTiEsitoVerifFirme());
                            filtroNew.setTiFiltroMultiplo(filtroOld.getTiFiltroMultiplo());
                            if (filtroOld.getDecRegistroUnitaDoc() != null) {
                                filtroNew.setDecRegistroUnitaDoc(cercaDecRegistroUnitaDocPerNome(
                                        filtroOld.getDecRegistroUnitaDoc().getCdRegistroUnitaDoc(),
                                        newStrut.getDecRegistroUnitaDocs()));
                                alFiltro.add(filtroNew);
                                tipoUnitaDocHelper.getEntityManager().persist(filtroNew);
                            } else if (filtroOld.getDecTipoUnitaDoc() != null) {
                                filtroNew.setDecTipoUnitaDoc(
                                        cercaDecTipoUnitaDocPerNome(filtroOld.getDecTipoUnitaDoc().getNmTipoUnitaDoc(),
                                                newStrut.getDecTipoUnitaDocs()));
                                alFiltro.add(filtroNew);
                                tipoUnitaDocHelper.getEntityManager().persist(filtroNew);
                            } else if (filtroOld.getDecTipoDoc() != null
                                    && filtroOld.getDecTipoDoc().getFlTipoDocPrincipale().equals("1")) {
                                // Lo considera soltanto se sitratta di un tipo_doc principale
                                filtroNew.setDecTipoDoc(cercaDecTipoDocPerNome(filtroOld.getDecTipoDoc().getNmTipoDoc(),
                                        newStrut.getDecTipoDocs()));
                                alFiltro.add(filtroNew);
                                tipoUnitaDocHelper.getEntityManager().persist(filtroNew);
                            }
                        }
                        if (!alFiltro.isEmpty()) {
                            c.setDecCriterioFiltroMultiplos(alFiltro);
                        }
                    }
                }
            }
        }
        return al;
    }

    /*
     * Crea una lista di OrgSubStrut da una lista tu OrgSubStrut di una struttura. Se le sottoStrutture non esistono con
     * lo stesso nome le crea e le associa alla struttura nuova.
     */
    private List<OrgSubStrut> determinaOrgSubStruts(OrgStrut oldStrut, OrgStrut newStrut) {
        ArrayList<OrgSubStrut> al = null;
        if (oldStrut.getOrgSubStruts() != null && (!oldStrut.getOrgSubStruts().isEmpty())) {
            al = new ArrayList();
            for (OrgSubStrut subOld : oldStrut.getOrgSubStruts()) {
                OrgSubStrut subNew = struttureHelper.getOrgSubStrutByName(subOld.getNmSubStrut(), newStrut);
                if (subNew == null) {
                    subNew = new OrgSubStrut();
                    al.add(subNew);
                    subNew.setOrgStrut(newStrut);
                    subNew.setDsSubStrut(subOld.getDsSubStrut());
                    subNew.setNmSubStrut(subOld.getNmSubStrut());
                    tipoUnitaDocHelper.getEntityManager().persist(subNew);
                }
                al.add(subNew);
            }
        }
        return al;
    }

    /*
     * Crea una lista di tipi ud da una lista tu tipi Ud di una struttura.
     */
    private List<DecFormatoFileDoc> determinaDecFormatoFileDocs(OrgStrut newStrut, List<DecFormatoFileDoc> oldList,
            Date dataAttuale, boolean includiElementiDisattivi, boolean mantieniDateSoppOriginali) {
        ArrayList<DecFormatoFileDoc> al = null;
        if (oldList != null && (!oldList.isEmpty())) {
            al = new ArrayList();
            for (DecFormatoFileDoc crOld : oldList) {
                EntitaValida entita = new EntitaValida(includiElementiDisattivi, mantieniDateSoppOriginali,
                        crOld.getDtIstituz(), crOld.getDtSoppres(), dataAttuale);
                if (entita.isValida()) {
                    DecFormatoFileDoc fileNew = new DecFormatoFileDoc();
                    al.add(fileNew);
                    fileNew.setOrgStrut(newStrut);
                    fileNew.setCdVersione(crOld.getCdVersione());
                    fileNew.setDsFormatoFileDoc(crOld.getDsFormatoFileDoc());
                    fileNew.setNmFormatoFileDoc(crOld.getNmFormatoFileDoc());
                    fileNew.setDtIstituz(entita.getDataInizio());
                    fileNew.setDtSoppres(entita.getDataFine());
                    tipoUnitaDocHelper.getEntityManager().persist(fileNew);
                    if (crOld.getDecUsoFormatoFileStandards() != null
                            && (!crOld.getDecUsoFormatoFileStandards().isEmpty())) {
                        ArrayList<DecUsoFormatoFileStandard> alUso = new ArrayList();
                        for (DecUsoFormatoFileStandard usoOld : crOld.getDecUsoFormatoFileStandards()) {
                            DecFormatoFileStandard stdOld = usoOld.getDecFormatoFileStandard();
                            DecFormatoFileStandard stdNew = formatoFileStandardHelper
                                    .getDecFormatoFileStandardByName(stdOld.getNmFormatoFileStandard());
                            // Se non esiste giÃƒÂ  crea il formato file standard
                            if (stdNew == null) {
                                stdNew = new DecFormatoFileStandard();
                                stdNew.setCdVersione(stdOld.getCdVersione());
                                stdNew.setDsCopyright(stdOld.getDsCopyright());
                                stdNew.setDsFormatoFileStandard(stdOld.getDsFormatoFileStandard());
                                stdNew.setFlFormatoConcat(stdOld.getFlFormatoConcat());
                                stdNew.setNmFormatoFileStandard(stdOld.getNmFormatoFileStandard());
                                stdNew.setNmMimetypeFile(stdOld.getNmMimetypeFile());
                                stdNew.setTiEsitoContrFormato(stdOld.getTiEsitoContrFormato());
                                tipoUnitaDocHelper.getEntityManager().persist(stdNew);
                                // copia formato file busta
                                if (stdOld.getDecFormatoFileBustas() != null
                                        && (!stdOld.getDecFormatoFileBustas().isEmpty())) {
                                    ArrayList<DecFormatoFileBusta> alBusta = new ArrayList();
                                    for (DecFormatoFileBusta bustaOld : stdOld.getDecFormatoFileBustas()) {
                                        DecFormatoFileBusta bustaNew = new DecFormatoFileBusta();
                                        bustaNew.setDecFormatoFileStandard(stdNew);
                                        bustaNew.setTiFormatoFirmaMarca(bustaOld.getTiFormatoFirmaMarca());
                                        tipoUnitaDocHelper.getEntityManager().persist(bustaNew);
                                        alBusta.add(bustaNew);
                                    }
                                    stdNew.setDecFormatoFileBustas(alBusta);
                                }
                                // copia estensione file
                                if (stdOld.getDecEstensioneFiles() != null
                                        && (!stdOld.getDecEstensioneFiles().isEmpty())) {
                                    ArrayList<DecEstensioneFile> alEst = new ArrayList();
                                    for (DecEstensioneFile estOld : stdOld.getDecEstensioneFiles()) {
                                        if (!tipoUnitaDocHelper.existsEstensione(estOld.getCdEstensioneFile())) {
                                            // try {
                                            DecEstensioneFile estNew = new DecEstensioneFile();
                                            estNew.setDecFormatoFileStandard(stdNew);
                                            estNew.setCdEstensioneFile(estOld.getCdEstensioneFile());
                                            tipoUnitaDocHelper.getEntityManager().persist(estNew);
                                            // tipoUnitaDocHelper.getEntityManager().flush();
                                            alEst.add(estNew);
                                        }
                                        // } catch (Exception e) {
                                        // logger.warn("Estensione file impossibile da inserire");
                                        // }
                                    }
                                    stdNew.setDecEstensioneFiles(alEst);
                                }

                            }
                            DecUsoFormatoFileStandard usoNew = new DecUsoFormatoFileStandard();
                            usoNew.setDecFormatoFileDoc(fileNew);
                            usoNew.setDecFormatoFileStandard(stdNew);
                            usoNew.setNiOrdUso(usoOld.getNiOrdUso());
                            tipoUnitaDocHelper.getEntityManager().persist(usoNew);
                            alUso.add(usoNew);
                        }
                        fileNew.setDecUsoFormatoFileStandards(alUso);
                    }
                }
            }
        }
        return al;
    }

    /*
     * Crea una lista di tipi ud da una lista di tipi Ud di una struttura.
     */
    private List<DecTipoUnitaDoc> determinaDecTipoUnitaDocs(OrgStrut oldStrut, Date dataAttuale,
            boolean includiElementiDisattivi, OrgStrut newStrut, boolean mantieniDateFineValidita,
            OrgStrutCopyResult result) throws ParerUserError {
        ArrayList<DecTipoUnitaDoc> al = null;
        if (oldStrut.getDecTipoUnitaDocs() != null && (!oldStrut.getDecTipoUnitaDocs().isEmpty())) {
            al = new ArrayList();
            for (DecTipoUnitaDoc udOld : oldStrut.getDecTipoUnitaDocs()) {
                EntitaValida entita = new EntitaValida(includiElementiDisattivi, mantieniDateFineValidita,
                        udOld.getDtIstituz(), udOld.getDtSoppres(), dataAttuale);
                if (entita.isValida()) {
                    DecTipoUnitaDoc ud = new DecTipoUnitaDoc();
                    al.add(ud);
                    ud.setCdSerie(udOld.getCdSerie());
                    ud.setCdSerieDaCreare(udOld.getCdSerieDaCreare());
                    ud.setDlNoteTipoUd(udOld.getDlNoteTipoUd());
                    ud.setDsSerieDaCreare(udOld.getDsSerieDaCreare());
                    ud.setDsTipoSerieDaCreare(udOld.getDsTipoSerieDaCreare());
                    ud.setDsTipoUnitaDoc(udOld.getDsTipoUnitaDoc());
                    ud.setDtIstituz(entita.getDataInizio());
                    ud.setDtSoppres(entita.getDataFine());
                    ud.setFlCreaTipoSerieStandard(udOld.getFlCreaTipoSerieStandard());
                    ud.setNmTipoSerieDaCreare(udOld.getNmTipoSerieDaCreare());
                    ud.setFlVersManuale(udOld.getFlVersManuale());
                    ud.setNmTipoUnitaDoc(udOld.getNmTipoUnitaDoc());
                    ud.setTiSaveFile(udOld.getTiSaveFile());
                    ud.setOrgStrut(newStrut);
                    ud.setDecModelloTipoSerie(determinaModelloTipoSerie(result, udOld.getDecModelloTipoSerie()));
                    // b)
                    ud.setDecCategTipoUnitaDoc(determinaDecCategTipoUnitaDoc(udOld));
                    struttureHelper.getEntityManager().persist(ud);
                    // c) e d)
                    DecVCalcTiServOnTipoUd vistaClasseEnte = tipoUnitaDocHelper.getDecVCalcTiServOnTipoUd(
                            new BigDecimal(oldStrut.getIdStrut()),
                            new BigDecimal(ud.getDecCategTipoUnitaDoc().getIdCategTipoUnitaDoc()), "CLASSE_ENTE");
                    DecVCalcTiServOnTipoUd vistaNoClasseEnte = tipoUnitaDocHelper.getDecVCalcTiServOnTipoUd(
                            new BigDecimal(oldStrut.getIdStrut()),
                            new BigDecimal(ud.getDecCategTipoUnitaDoc().getIdCategTipoUnitaDoc()), "NO_CLASSE_ENTE");
                    if (vistaClasseEnte != null) {
                        if (vistaClasseEnte.getIdTipoServizioConserv() != null) {
                            ud.setOrgTipoServizio(tipoUnitaDocHelper.findById(OrgTipoServizio.class,
                                    vistaClasseEnte.getIdTipoServizioConserv()));
                        }
                        if (vistaClasseEnte.getIdTipoServizioAttiv() != null) {
                            ud.setOrgTipoServizioAttiv(tipoUnitaDocHelper.findById(OrgTipoServizio.class,
                                    vistaClasseEnte.getIdTipoServizioAttiv()));
                        }
                    }
                    if (vistaNoClasseEnte != null) {
                        if (vistaNoClasseEnte.getIdTipoServizioConserv() != null) {
                            ud.setOrgTipoServConservTipoUd(tipoUnitaDocHelper.findById(OrgTipoServizio.class,
                                    vistaNoClasseEnte.getIdTipoServizioConserv()));
                        }
                        if (vistaNoClasseEnte.getIdTipoServizioAttiv() != null) {
                            ud.setOrgTipoServAttivTipoUd(tipoUnitaDocHelper.findById(OrgTipoServizio.class,
                                    vistaNoClasseEnte.getIdTipoServizioAttiv()));
                        }
                    }
                    // DecVCalcTiServOnTipoUd vista = tipoUnitaDocHelper.getDecVCalcTiServOnTipoUd(
                    // new BigDecimal(newStrut.getIdStrut()),
                    // new BigDecimal(ud.getDecCategTipoUnitaDoc().getIdCategTipoUnitaDoc()));
                    // if (vista != null) {
                    // ud.setOrgTipoServizio(
                    // tipoUnitaDocHelper.findById(OrgTipoServizio.class, vista.getIdTipoServizioConserv()));
                    // ud.setOrgTipoServizioAttiv(
                    // tipoUnitaDocHelper.findById(OrgTipoServizio.class, vista.getIdTipoServizioAttiv()));
                    // }
                    // e)
                    ud.setDecAttribDatiSpecs(
                            determinaAttribDatiSpecs(udOld.getDecAttribDatiSpecs(), newStrut, ud, null, null));
                    ud.setDecXsdDatiSpecs(determinaXsdDatiSpecs(udOld.getDecXsdDatiSpecs(), newStrut, ud, null, null,
                            dataAttuale, includiElementiDisattivi, mantieniDateFineValidita));
                    tipoUnitaDocHelper.getEntityManager().flush();
                    // Aggiunto io
                    // creaAssociazioniXsdAttributiUD(udOld.getDecXsdDatiSpecs(), ud.getDecXsdDatiSpecs(),
                    // ud.getDecAttribDatiSpecs());
                    ud.setAplValoreParamApplics(determinaAplValoreParamApplics(udOld.getAplValoreParamApplics(), ud));
                }
            }
        }
        return al;
    }

    private List<AplValoreParamApplic> determinaAplValoreParamApplics(
            List<AplValoreParamApplic> aplValoreParamApplicsOld, DecTipoUnitaDoc decTipoUnitaDocNew)
            throws ParerUserError {
        ArrayList<AplValoreParamApplic> al = null;
        if (aplValoreParamApplicsOld != null && (!aplValoreParamApplicsOld.isEmpty())) {
            al = new ArrayList();
            for (AplValoreParamApplic aplValoreParamApplicOld : aplValoreParamApplicsOld) {
                AplValoreParamApplic aplValoreParamApplicNew = new AplValoreParamApplic();
                aplValoreParamApplicNew.setDecTipoUnitaDoc(decTipoUnitaDocNew);
                aplValoreParamApplicNew.setDsValoreParamApplic(aplValoreParamApplicOld.getDsValoreParamApplic());
                aplValoreParamApplicNew.setTiAppart(aplValoreParamApplicOld.getTiAppart());
                AplParamApplic aplParamApplicOld = aplValoreParamApplicOld.getAplParamApplic();
                AplParamApplic aplParamApplicNew = configurationHelper
                        .getParamApplic(aplParamApplicOld.getNmParamApplic());
                if (aplParamApplicNew == null) {
                    aplParamApplicNew = new AplParamApplic();
                    aplParamApplicNew.setDsListaValoriAmmessi(aplParamApplicOld.getDsListaValoriAmmessi());
                    aplParamApplicNew.setDsListaValoriAmmessi(aplParamApplicOld.getDsListaValoriAmmessi());
                    aplParamApplicNew.setFlAppartAaTipoFascicolo(aplParamApplicOld.getFlAppartAaTipoFascicolo());
                    aplParamApplicNew.setFlAppartAmbiente(aplParamApplicOld.getFlAppartAmbiente());
                    aplParamApplicNew.setFlAppartApplic(aplParamApplicOld.getFlAppartApplic());
                    aplParamApplicNew.setFlAppartStrut(aplParamApplicOld.getFlAppartStrut());
                    aplParamApplicNew.setFlAppartTipoUnitaDoc(aplParamApplicOld.getFlAppartTipoUnitaDoc());
                    aplParamApplicNew.setFlMulti(aplParamApplicOld.getFlMulti());
                    aplParamApplicNew.setNmParamApplic(aplParamApplicOld.getNmParamApplic());
                    aplParamApplicNew.setTiGestioneParam(aplParamApplicOld.getTiGestioneParam());
                    aplParamApplicNew.setTiParamApplic(aplParamApplicOld.getTiParamApplic());
                    tipoUnitaDocHelper.getEntityManager().persist(aplParamApplicNew);
                }
                aplValoreParamApplicNew.setAplParamApplic(aplParamApplicNew);
                tipoUnitaDocHelper.getEntityManager().persist(aplValoreParamApplicNew);
                al.add(aplValoreParamApplicNew);
            }
        }
        return al;
    }

    /*
     * Crea una lista di tipi ud da una lista di tipi Ud di una struttura.
     */
    private List<DecTipoStrutDoc> determinaDecTipoStrutDocs(OrgStrut oldStrut, Date dataAttuale,
            boolean includiElementiDisattivi, boolean includiFormati, OrgStrut newStrut,
            boolean mantieniDateFineValidita) throws ParerUserError {
        ArrayList<DecTipoStrutDoc> al = null;
        if (oldStrut.getDecTipoStrutDocs() != null && (!oldStrut.getDecTipoStrutDocs().isEmpty())) {
            al = new ArrayList();
            for (DecTipoStrutDoc tipoOld : oldStrut.getDecTipoStrutDocs()) {
                EntitaValida entita = new EntitaValida(includiElementiDisattivi, mantieniDateFineValidita,
                        tipoOld.getDtIstituz(), tipoOld.getDtSoppres(), dataAttuale);
                if (entita.isValida()) {
                    DecTipoStrutDoc tipoStrutDocNew = new DecTipoStrutDoc();
                    al.add(tipoStrutDocNew);
                    tipoStrutDocNew.setOrgStrut(newStrut);
                    tipoStrutDocNew.setDtIstituz(entita.getDataInizio());
                    tipoStrutDocNew.setDtSoppres(entita.getDataFine());
                    tipoStrutDocNew.setDsTipoStrutDoc(tipoOld.getDsTipoStrutDoc());
                    tipoStrutDocNew.setNmTipoStrutDoc(tipoOld.getNmTipoStrutDoc());
                    tipoUnitaDocHelper.getEntityManager().persist(tipoStrutDocNew);
                    if (tipoOld.getDecTipoCompDocs() != null && (!tipoOld.getDecTipoCompDocs().isEmpty())) {
                        ArrayList<DecTipoCompDoc> alCompNew = new ArrayList();
                        for (DecTipoCompDoc compDocOld : tipoOld.getDecTipoCompDocs()) {
                            EntitaValida entita2 = new EntitaValida(includiElementiDisattivi, mantieniDateFineValidita,
                                    compDocOld.getDtIstituz(), compDocOld.getDtSoppres(), dataAttuale);
                            if (entita2.isValida()) {
                                DecTipoCompDoc compDocNew = new DecTipoCompDoc();
                                alCompNew.add(compDocNew);
                                compDocNew.setDecTipoStrutDoc(tipoStrutDocNew);
                                compDocNew.setDtIstituz(entita2.getDataInizio());
                                compDocNew.setDtSoppres(entita2.getDataFine());
                                compDocNew.setDsTipoCompDoc(compDocOld.getDsTipoCompDoc());
                                compDocNew.setNmTipoCompDoc(compDocOld.getNmTipoCompDoc());
                                compDocNew.setTiUsoCompDoc(compDocOld.getTiUsoCompDoc());
                                tipoUnitaDocHelper.getEntityManager().persist(compDocNew);
                                // Gestisce associazioni XSD e attributi come per Tipo UD e tipo doc
                                compDocNew.setDecAttribDatiSpecs(determinaAttribDatiSpecs(
                                        compDocOld.getDecAttribDatiSpecs(), newStrut, null, null, compDocNew));
                                compDocNew.setDecXsdDatiSpecs(determinaXsdDatiSpecs(compDocOld.getDecXsdDatiSpecs(),
                                        newStrut, null, null, compDocNew, dataAttuale, includiElementiDisattivi,
                                        mantieniDateFineValidita));
                                // creaAssociazioniXsdAttributiUD(compDocOld.getDecXsdDatiSpecs(),
                                // compDocNew.getDecXsdDatiSpecs(), compDocNew.getDecAttribDatiSpecs());
                                // Gestione dei formati file, punto 47.i
                                if (includiFormati) {
                                    if (compDocOld.getDecFormatoFileAmmessos() != null
                                            && (!compDocOld.getDecFormatoFileAmmessos().isEmpty())) {
                                        ArrayList<DecFormatoFileAmmesso> alAmessoNew = new ArrayList();
                                        for (DecFormatoFileAmmesso fileAmmOld : compDocOld
                                                .getDecFormatoFileAmmessos()) {
                                            DecFormatoFileDoc dfd = cercaDecFormatoFileDocPerNome(
                                                    fileAmmOld.getDecFormatoFileDoc().getNmFormatoFileDoc(),
                                                    newStrut.getDecFormatoFileDocs());
                                            if (dfd != null) {
                                                DecFormatoFileAmmesso fileAmmNew = new DecFormatoFileAmmesso();
                                                alAmessoNew.add(fileAmmNew);
                                                fileAmmNew.setDecTipoCompDoc(compDocNew);
                                                fileAmmNew.setDecFormatoFileDoc(dfd);
                                                tipoUnitaDocHelper.getEntityManager().persist(fileAmmNew);
                                            }
                                        }
                                        compDocNew.setDecFormatoFileAmmessos(alAmessoNew);
                                    }
                                }
                                // Gestione tipo rappresentazione componente punto 47.D
                                if (compDocOld.getDecTipoRapprAmmessos() != null
                                        && (!compDocOld.getDecTipoRapprAmmessos().isEmpty())) {
                                    ArrayList<DecTipoRapprAmmesso> alRapprAmmNew = new ArrayList();
                                    for (DecTipoRapprAmmesso rapprAmmOld : compDocOld.getDecTipoRapprAmmessos()) {
                                        DecTipoRapprComp decTipoRapprComp = cercaDecTipoRapprCompPerNome(
                                                rapprAmmOld.getDecTipoRapprComp().getNmTipoRapprComp(),
                                                newStrut.getDecTipoRapprComps());
                                        if (decTipoRapprComp != null) {
                                            DecTipoRapprAmmesso decTipoRapprAmmessoNew = new DecTipoRapprAmmesso();
                                            alRapprAmmNew.add(decTipoRapprAmmessoNew);
                                            decTipoRapprAmmessoNew.setDecTipoCompDoc(compDocNew);
                                            decTipoRapprAmmessoNew.setDecTipoRapprComp(decTipoRapprComp);
                                            tipoUnitaDocHelper.getEntityManager().persist(decTipoRapprAmmessoNew);
                                        }
                                    }
                                    if (!alRapprAmmNew.isEmpty()) {
                                        compDocNew.setDecTipoRapprAmmessos(alRapprAmmNew);
                                    }
                                }
                            }
                        }
                        if (!alCompNew.isEmpty()) {
                            tipoStrutDocNew.setDecTipoCompDocs(alCompNew);
                        }
                    }
                    // Gestione tipi doc ammessi
                    if (tipoOld.getDecTipoStrutDocAmmessos() != null
                            && tipoOld.getDecTipoStrutDocAmmessos().size() > 1) {
                        ArrayList<DecTipoStrutDocAmmesso> alTipoDocNew = new ArrayList();
                        for (DecTipoStrutDocAmmesso decTipoStrutDocAmmessoOld : tipoOld.getDecTipoStrutDocAmmessos()) {
                            DecTipoDoc tipoDocOld = decTipoStrutDocAmmessoOld.getDecTipoDoc();
                            DecTipoDoc tipoDocNew = cercaDecTipoDocPerNome(tipoDocOld.getNmTipoDoc(),
                                    newStrut.getDecTipoDocs());
                            if (tipoDocNew != null) {
                                DecTipoStrutDocAmmesso decTipoStrutDocAmmessoNew = new DecTipoStrutDocAmmesso();
                                decTipoStrutDocAmmessoNew.setDecTipoDoc(tipoDocNew);
                                decTipoStrutDocAmmessoNew.setDecTipoStrutDoc(tipoStrutDocNew);
                                alTipoDocNew.add(decTipoStrutDocAmmessoNew);
                                tipoUnitaDocHelper.getEntityManager().persist(decTipoStrutDocAmmessoNew);
                            }
                        }
                        tipoStrutDocNew.setDecTipoStrutDocAmmessos(alTipoDocNew);
                    }
                }
            }
        }
        return al;
    }

    /*
     * Crea una lista di tipi doc da una lista di tipi doc di una struttura.
     */
    private List<DecTipoDoc> determinaDecTipoDocs(OrgStrut oldStrut, Date dataAttuale, boolean includiElementiDisattivi,
            OrgStrut newStrut, boolean mantieniDateFineValidita) throws ParerUserError {
        ArrayList<DecTipoDoc> al = null;
        if (oldStrut.getDecTipoDocs() != null && (!oldStrut.getDecTipoDocs().isEmpty())) {
            al = new ArrayList();
            for (DecTipoDoc docOld : oldStrut.getDecTipoDocs()) {
                EntitaValida entita = new EntitaValida(includiElementiDisattivi, mantieniDateFineValidita,
                        docOld.getDtIstituz(), docOld.getDtSoppres(), dataAttuale);
                if (entita.isValida()) {
                    DecTipoDoc doc = new DecTipoDoc();
                    al.add(doc);
                    doc.setDtIstituz(entita.getDataInizio());
                    doc.setDtSoppres(entita.getDataFine());
                    doc.setOrgStrut(newStrut);
                    doc.setDlNoteTipoDoc(docOld.getDlNoteTipoDoc());
                    doc.setDsTipoDoc(docOld.getDsTipoDoc());
                    doc.setFlTipoDocPrincipale(docOld.getFlTipoDocPrincipale());
                    doc.setNmTipoDoc(docOld.getNmTipoDoc());
                    doc.setDsPeriodicitaVers(docOld.getDsPeriodicitaVers());
                    tipoUnitaDocHelper.getEntityManager().persist(doc);
                    // g)
                    doc.setDecAttribDatiSpecs(
                            determinaAttribDatiSpecs(docOld.getDecAttribDatiSpecs(), newStrut, null, doc, null));
                    doc.setDecXsdDatiSpecs(determinaXsdDatiSpecs(docOld.getDecXsdDatiSpecs(), newStrut, null, doc, null,
                            dataAttuale, includiElementiDisattivi, mantieniDateFineValidita));
                    // creaAssociazioniXsdAttributiUD(docOld.getDecXsdDatiSpecs(), doc.getDecXsdDatiSpecs(),
                    // doc.getDecAttribDatiSpecs());
                    // h) Determina associaziojni tra tipo Doc e tipo struttura UD
                    doc.setDecTipoDocAmmessos(determinaAssociazioniTipoDocAmmesso(docOld.getDecTipoDocAmmessos(), doc,
                            newStrut.getDecTipoUnitaDocs()));
                    // 49)
                    // doc.setOrgRegolaValSubStruts(determinaOrgRegolaValSubStruts(doc.getOrgRegolaValSubStruts(),
                    // dataAttuale, includiElementiDisattivi, null, doc, mantieniDateFineValidita, newStrut));
                }
            }
        }
        return al;
    }

    /*
     * Crea una lista di tipi rappr comp da una lista di tipi rappr comp di una struttura.
     */
    private List<DecTipoRapprComp> determinaDecTipoRapprComps(List<DecTipoRapprComp> oldList, Date dataAttuale,
            boolean includiElementiDisattivi, OrgStrut newStrut, boolean mantieniDateFineValidita,
            boolean includiFormati) {
        ArrayList<DecTipoRapprComp> al = null;
        if (oldList != null && (!oldList.isEmpty())) {
            al = new ArrayList();
            for (DecTipoRapprComp rappOld : oldList) {
                EntitaValida entita = new EntitaValida(includiElementiDisattivi, mantieniDateFineValidita,
                        rappOld.getDtIstituz(), rappOld.getDtSoppres(), dataAttuale);
                if (entita.isValida()) {
                    DecTipoRapprComp rappNew = new DecTipoRapprComp();
                    al.add(rappNew);
                    rappNew.setDtIstituz(entita.getDataInizio());
                    rappNew.setDtSoppres(entita.getDataFine());
                    rappNew.setOrgStrut(newStrut);
                    rappNew.setDsTipoRapprComp(rappOld.getDsTipoRapprComp());
                    rappNew.setNmTipoRapprComp(rappOld.getNmTipoRapprComp());
                    rappNew.setTiAlgoRappr(rappOld.getTiAlgoRappr());
                    rappNew.setTiOutputRappr(rappOld.getTiOutputRappr());
                    if (includiFormati) {
                        if (rappOld.getDecFormatoFileDocCont() != null) {
                            DecFormatoFileDoc dfd = cercaDecFormatoFileDocPerNome(
                                    rappOld.getDecFormatoFileDocCont().getNmFormatoFileDoc(),
                                    newStrut.getDecFormatoFileDocs());
                            if (dfd != null) {
                                rappNew.setDecFormatoFileDocCont(dfd);
                                // tipoUnitaDocHelper.getEntityManager().persist(fileAmmNew);
                            }
                        }

                        if (rappOld.getDecFormatoFileDocConv() != null) {
                            DecFormatoFileDoc dfd2 = cercaDecFormatoFileDocPerNome(
                                    rappOld.getDecFormatoFileDocConv().getNmFormatoFileDoc(),
                                    newStrut.getDecFormatoFileDocs());
                            if (dfd2 != null) {
                                rappNew.setDecFormatoFileDocConv(dfd2);
                                // tipoUnitaDocHelper.getEntityManager().persist(fileAmmNew);
                            }
                        }

                        // rappNew.setDecFormatoFileDocCont(rappOld.getDecFormatoFileDocCont());
                        // rappNew.setDecFormatoFileDocConv(rappOld.getDecFormatoFileDocConv());
                        rappNew.setDecFormatoFileStandard(rappOld.getDecFormatoFileStandard());
                    }
                    tipoUnitaDocHelper.getEntityManager().persist(rappNew);
                }
            }
        }
        return al;
    }

    /*
     * Prende la categoria eventualmente presente nella vecchia ud e vede se esiste. Se esiste torna quella. Altrimenti
     * la inserisce nel db
     */
    private DecCategTipoUnitaDoc determinaDecCategTipoUnitaDoc(DecTipoUnitaDoc udOld) throws ParerUserError {
        DecCategTipoUnitaDoc decCategTipoUnitaDocOld = udOld.getDecCategTipoUnitaDoc();
        DecCategTipoUnitaDoc categTipoUnitaDocNew = null;
        if (decCategTipoUnitaDocOld != null) {
            String cdCategTipoUnitaDoc = decCategTipoUnitaDocOld.getCdCategTipoUnitaDoc();
            categTipoUnitaDocNew = tipoUnitaDocHelper.getDecCategTipoUnitaDocByCode(cdCategTipoUnitaDoc);
            // Se non esiste Genera eccezione!
            if (categTipoUnitaDocNew == null) {
                throw new ParerUserError(String.format("La categoria %s (%s) non esiste sulla base dati",
                        cdCategTipoUnitaDoc, decCategTipoUnitaDocOld.getDsCategTipoUnitaDoc()));
            }
            // Gestisce ricorsivamente tutti i padri della categoria
            // categTipoUnitaDocNew.setDecCategTipoUnitaDoc(determinaDecCategTipoUnitaDocPadre(decCategTipoUnitaDocOld.getDecCategTipoUnitaDoc()));
            // tipoUnitaDocHelper.getEntityManager().persist(categTipoUnitaDocNew);
        }
        return categTipoUnitaDocNew;
    }

    /*
     * Funzione ricorsiva che scandaglia tutti i padri vecchi della categoria tipo ud e li inserisce se non ci sono
     */
    /*
     * NON SI DEVONO GESTIRE I PADRI !!!!! private DecCategTipoUnitaDoc
     * determinaDecCategTipoUnitaDocPadre(DecCategTipoUnitaDoc decCategTipoUnitaDocPadreOld) { DecCategTipoUnitaDoc
     * categTipoUnitaDocPadre=null; if (decCategTipoUnitaDocPadreOld!=null) { String
     * cdCategTipoUnitaDoc=decCategTipoUnitaDocPadreOld.getCdCategTipoUnitaDoc();
     * categTipoUnitaDocPadre=tipoUnitaDocHelper.getDecCategTipoUnitaDocByCode(cdCategTipoUnitaDoc); // Se non esiste lo
     * devo inserire nel DB if (categTipoUnitaDocPadre==null) { categTipoUnitaDocPadre=new DecCategTipoUnitaDoc();
     * categTipoUnitaDocPadre.setCdCategTipoUnitaDoc(decCategTipoUnitaDocPadreOld.getCdCategTipoUnitaDoc());
     * categTipoUnitaDocPadre.setDsCategTipoUnitaDoc(decCategTipoUnitaDocPadreOld.getDsCategTipoUnitaDoc());
     * tipoUnitaDocHelper.getEntityManager().persist(categTipoUnitaDocPadre);
     * tipoUnitaDocHelper.getEntityManager().flush(); } // Richiama ricorsivamente la categoria padre...
     * categTipoUnitaDocPadre.setDecCategTipoUnitaDoc(determinaDecCategTipoUnitaDocPadre(decCategTipoUnitaDocPadreOld.
     * getDecCategTipoUnitaDoc())); } return categTipoUnitaDocPadre; }
     */
    /*
     * Crea una lista di DecXsdDatiSpec a partire da una lista prelevata da un DecTipoUnitaDoc o DecTipoDoc o Comp ...
     */
    private List<DecXsdDatiSpec> determinaXsdDatiSpecs(List<DecXsdDatiSpec> listaOld, OrgStrut newStrut,
            DecTipoUnitaDoc newUd, DecTipoDoc newDoc, DecTipoCompDoc newComp, Date dataAttuale,
            boolean includiElementiDisattivi, boolean mantieniDateFineValidita) throws ParerUserError {
        ArrayList<DecXsdDatiSpec> al = null;
        if (listaOld != null && (!listaOld.isEmpty())) {
            al = new ArrayList();
            for (DecXsdDatiSpec decXsdDatiSpecOld : listaOld) {
                // Se le date sono attuali considera tutto altrimenti bypassa
                EntitaValida entita = new EntitaValida(includiElementiDisattivi, mantieniDateFineValidita,
                        decXsdDatiSpecOld.getDtIstituz(), decXsdDatiSpecOld.getDtSoppres(), dataAttuale);
                if (entita.isValida()) {
                    /* DEVE controllare la validità  del formato dell'XSD e interrompere l'importazione/duplicazione */
                    validaXsd(decXsdDatiSpecOld.getBlXsd());
                    DecXsdDatiSpec decXsdDatiSpecNew = new DecXsdDatiSpec();
                    al.add(decXsdDatiSpecNew);
                    decXsdDatiSpecNew.setOrgStrut(newStrut);
                    decXsdDatiSpecNew.setTiUsoXsd(decXsdDatiSpecOld.getTiUsoXsd());
                    decXsdDatiSpecNew.setTiEntitaSacer(decXsdDatiSpecOld.getTiEntitaSacer());

                    if (newUd != null) {
                        decXsdDatiSpecNew.setDecTipoUnitaDoc(newUd);
                    } else if (newDoc != null) {
                        decXsdDatiSpecNew.setDecTipoDoc(newDoc);
                    } else if (newComp != null) {
                        decXsdDatiSpecNew.setDecTipoCompDoc(newComp);
                    }
                    decXsdDatiSpecNew.setNmSistemaMigraz(decXsdDatiSpecOld.getNmSistemaMigraz());
                    decXsdDatiSpecNew.setCdVersioneXsd(decXsdDatiSpecOld.getCdVersioneXsd());
                    decXsdDatiSpecNew.setBlXsd(decXsdDatiSpecOld.getBlXsd());
                    decXsdDatiSpecNew.setDsVersioneXsd(decXsdDatiSpecOld.getDsVersioneXsd());
                    decXsdDatiSpecNew.setDtIstituz(entita.getDataInizio());
                    decXsdDatiSpecNew.setDtSoppres(entita.getDataFine());

                    if (decXsdDatiSpecOld.getDecXsdAttribDatiSpecs() != null
                            && decXsdDatiSpecOld.getDecXsdAttribDatiSpecs().size() > 0) {
                        ArrayList<DecXsdAttribDatiSpec> alAtt = new ArrayList<>();
                        for (DecXsdAttribDatiSpec decXsdAttribDatiSpecOld : decXsdDatiSpecOld
                                .getDecXsdAttribDatiSpecs()) {
                            DecXsdAttribDatiSpec decXsdAttribDatiSpecNew = new DecXsdAttribDatiSpec();
                            decXsdAttribDatiSpecNew.setDecXsdDatiSpec(decXsdDatiSpecNew);
                            decXsdAttribDatiSpecNew.setNiOrdAttrib(decXsdAttribDatiSpecOld.getNiOrdAttrib());
                            List<DecAttribDatiSpec> listaAtt = null;
                            if (newUd != null) {
                                listaAtt = newUd.getDecAttribDatiSpecs();
                            } else if (newDoc != null) {
                                listaAtt = newDoc.getDecAttribDatiSpecs();
                            } else if (newComp != null) {
                                listaAtt = newComp.getDecAttribDatiSpecs();
                            }
                            decXsdAttribDatiSpecNew.setDecAttribDatiSpec(cercaDecAttribDatiSpecPerNome(
                                    decXsdAttribDatiSpecOld.getDecAttribDatiSpec().getNmAttribDatiSpec(), listaAtt));
                            if (decXsdAttribDatiSpecNew.getDecAttribDatiSpec() != null) {
                                alAtt.add(decXsdAttribDatiSpecNew);
                                struttureHelper.getEntityManager().persist(decXsdAttribDatiSpecNew);
                            }
                        }
                        decXsdDatiSpecNew.setDecXsdAttribDatiSpecs(alAtt);
                    }

                    struttureHelper.getEntityManager().persist(decXsdDatiSpecNew);
                }
            }
        }
        return al;
    }

    // Punto H: imposta associazionitra tipo doc e tipo strut unita doc
    private List<DecTipoDocAmmesso> determinaAssociazioniTipoDocAmmesso(List<DecTipoDocAmmesso> listaOld,
            DecTipoDoc tipoNew, List<DecTipoUnitaDoc> listaUdNew) {
        ArrayList<DecTipoDocAmmesso> al = null;
        if (listaOld != null && (!listaOld.isEmpty())) {
            al = new ArrayList();
            for (DecTipoDocAmmesso ammOld : listaOld) {
                DecTipoStrutUnitaDoc strutUDOld = ammOld.getDecTipoStrutUnitaDoc();
                DecTipoStrutUnitaDoc strutUDNew = cercaDecTipoStrutUnitaDocPerNome(strutUDOld.getNmTipoStrutUnitaDoc(),
                        listaUdNew);
                if (strutUDNew != null) {
                    DecTipoDocAmmesso ammNew = new DecTipoDocAmmesso();
                    ammNew.setFlObbl(ammOld.getFlObbl());
                    ammNew.setTiDoc(ammOld.getTiDoc());
                    ammNew.setDecTipoStrutUnitaDoc(strutUDNew);
                    ammNew.setDecTipoDoc(tipoNew);
                    tipoUnitaDocHelper.getEntityManager().persist(ammNew);
                    al.add(ammNew);
                }
            }
            if (al.isEmpty()) {
                al = null;
            }
        }
        return al;
    }

    /*
     * Crea una lista di DecAttribDatiSpec a partire da una lista prelevata da un DecTipoUnitaDoc
     */
    private List<DecAttribDatiSpec> determinaAttribDatiSpecs(List<DecAttribDatiSpec> listaOld, OrgStrut newStrut,
            DecTipoUnitaDoc newUd, DecTipoDoc newDoc, DecTipoCompDoc newComp) {
        ArrayList<DecAttribDatiSpec> al = null;
        if (listaOld != null && (!listaOld.isEmpty())) {
            al = new ArrayList();
            for (DecAttribDatiSpec decAttribDatiSpecOld : listaOld) {
                // Se le date sono attuali considera tutto altrimenti bypassa
                DecAttribDatiSpec decAttribDatiSpecNew = new DecAttribDatiSpec();
                decAttribDatiSpecNew.setOrgStrut(newStrut);
                decAttribDatiSpecNew.setTiUsoAttrib(decAttribDatiSpecOld.getTiUsoAttrib());
                decAttribDatiSpecNew.setTiEntitaSacer(decAttribDatiSpecOld.getTiEntitaSacer());
                decAttribDatiSpecNew.setTiAttribDatiSpec(decAttribDatiSpecOld.getTiAttribDatiSpec());
                if (newUd != null) {
                    decAttribDatiSpecNew.setDecTipoUnitaDoc(newUd);
                } else if (newDoc != null) {
                    decAttribDatiSpecNew.setDecTipoDoc(newDoc);
                } else if (newComp != null) {
                    decAttribDatiSpecNew.setDecTipoCompDoc(newComp);
                }
                decAttribDatiSpecNew.setNmSistemaMigraz(decAttribDatiSpecOld.getNmSistemaMigraz());
                decAttribDatiSpecNew.setNmAttribDatiSpec(decAttribDatiSpecOld.getNmAttribDatiSpec());
                decAttribDatiSpecNew.setDsAttribDatiSpec(decAttribDatiSpecOld.getDsAttribDatiSpec());
                al.add(decAttribDatiSpecNew);
                struttureHelper.getEntityManager().persist(decAttribDatiSpecNew);
                // tipoUnitaDocHelper.getEntityManager().flush();
            }
            // tipoUnitaDocHelper.getEntityManager().flush();
        }
        return al;
    }

    /*
     * Crea le associazioni tra le versioni degli xsd dei dati specifici e gli attributi
     */
    /*
     * private void creaAssociazioniXsdAttributiUD(List<DecXsdDatiSpec> listaOld, List<DecXsdDatiSpec> listaNew,
     * List<DecAttribDatiSpec> listaAttribNew) { if (listaNew != null) { for (DecXsdDatiSpec decXsdDatiSpecNew :
     * listaNew) { DecXsdDatiSpec decXsdDatiSpecOld =
     * cercaDecXsdDatiSpecPerVersione(decXsdDatiSpecNew.getCdVersioneXsd(), listaOld); if
     * (decXsdDatiSpecOld.getDecXsdAttribDatiSpecs() != null) { for (DecXsdAttribDatiSpec decXsdAttribDatiSpecOld :
     * decXsdDatiSpecOld.getDecXsdAttribDatiSpecs()) { DecAttribDatiSpec attribDatiSpecOld =
     * decXsdAttribDatiSpecOld.getDecAttribDatiSpec(); DecAttribDatiSpec attribDatiSpecNew =
     * cercaDecAttribDatiSpecPerNome(attribDatiSpecOld.getNmAttribDatiSpec(), listaAttribNew); DecXsdAttribDatiSpec
     * decXsdAttribDatiSpecNew = new DecXsdAttribDatiSpec();
     * decXsdAttribDatiSpecNew.setDecAttribDatiSpec(attribDatiSpecNew);
     * decXsdAttribDatiSpecNew.setDecXsdDatiSpec(decXsdDatiSpecNew);
     * decXsdAttribDatiSpecNew.setNiOrdAttrib(decXsdAttribDatiSpecOld.getNiOrdAttrib());
     * tipoUnitaDocHelper.getEntityManager().persist(decXsdAttribDatiSpecNew); } } } } }
     */
    /*
     * Cerca per versione un DecXsdDatiSpec da una lista
     */
    private DecXsdDatiSpec cercaDecXsdDatiSpecPerVersione(String nomeVersione, List<DecXsdDatiSpec> lista) {
        DecXsdDatiSpec ret = null;
        if (lista != null) {
            for (DecXsdDatiSpec decXsdDatiSpec : lista) {
                if (nomeVersione.equals(decXsdDatiSpec.getCdVersioneXsd())) {
                    ret = decXsdDatiSpec;
                    break;
                }
            }
        }
        return ret;
    }

    /*
     * Cerca per nome un attributo da una lista di DecAttribDatiSpec
     */
    private DecAttribDatiSpec cercaDecAttribDatiSpecPerNome(String nomeAttributo, List<DecAttribDatiSpec> lista) {
        DecAttribDatiSpec ret = null;
        if (lista != null) {
            for (DecAttribDatiSpec decAttribDatiSpec : lista) {
                if (nomeAttributo.equals(decAttribDatiSpec.getNmAttribDatiSpec())) {
                    ret = decAttribDatiSpec;
                    break;
                }
            }
        }
        return ret;
    }

    /*
     * Cerca per nome da una lista di DecTipoUnitaDoc
     */
    private DecTipoUnitaDoc cercaDecTipoUnitaDocPerNome(String nomeUd, List<DecTipoUnitaDoc> lista) {
        DecTipoUnitaDoc ret = null;
        if (lista != null) {
            for (DecTipoUnitaDoc decTipoUnitaDoc : lista) {
                if (nomeUd.equals(decTipoUnitaDoc.getNmTipoUnitaDoc())) {
                    ret = decTipoUnitaDoc;
                    break;
                }
            }
        }
        return ret;
    }

    /*
     * Cerca per nome una DEC_TIPO_STRUT_UNITA_DOC da una lista di DecTipoUnitaDoc
     */
    private DecTipoStrutUnitaDoc cercaDecTipoStrutUnitaDocPerNome(String nomeTipo, List<DecTipoUnitaDoc> lista) {
        DecTipoStrutUnitaDoc ret = null;
        if (lista != null) {
            for (DecTipoUnitaDoc decTipoUnitaDoc : lista) {
                if (decTipoUnitaDoc.getDecTipoStrutUnitaDocs() != null) {
                    for (DecTipoStrutUnitaDoc decTipoStrutUnitaDoc : decTipoUnitaDoc.getDecTipoStrutUnitaDocs()) {
                        if (nomeTipo.equals(decTipoStrutUnitaDoc.getNmTipoStrutUnitaDoc())) {
                            ret = decTipoStrutUnitaDoc;
                            break;
                        }
                    }
                }
            }
        }
        return ret;
    }

    /*
     * Cerca per nome una DEC_TIPO_STRUT_UNITA_DOC da una lista di DecTipoUnitaDoc
     */
    private DecTipoDoc cercaDecTipoDocPerNome(String nomeDoc, List<DecTipoDoc> lista) {
        DecTipoDoc ret = null;
        if (lista != null) {
            for (DecTipoDoc decTipoDoc : lista) {
                if (nomeDoc.equals(decTipoDoc.getNmTipoDoc())) {
                    ret = decTipoDoc;
                    break;
                }
            }
        }
        return ret;
    }

    /*
     * Cerca per nome una DecTipoRapprComp da una lista di DecTipoRapprComp
     */
    private DecTipoRapprComp cercaDecTipoRapprCompPerNome(String nomeRapp, List<DecTipoRapprComp> lista) {
        DecTipoRapprComp ret = null;
        if (lista != null) {
            for (DecTipoRapprComp rapp : lista) {
                if (nomeRapp.equals(rapp.getNmTipoRapprComp())) {
                    ret = rapp;
                    break;
                }
            }
        }
        return ret;
    }

    /*
     * Cerca per nome una DEC_TIPO_FILE_DOC da una lista di DecTipoFileDoc
     */
    private DecFormatoFileDoc cercaDecFormatoFileDocPerNome(String nomeFormato, List<DecFormatoFileDoc> lista) {
        DecFormatoFileDoc ret = null;
        if (lista != null) {
            for (DecFormatoFileDoc dec : lista) {
                if (nomeFormato.equals(dec.getNmFormatoFileDoc())) {
                    ret = dec;
                    break;
                }
            }
        }
        return ret;
    }

    /*
     * Cerca per nome da una lista di DecRegistroUnitaDoc
     */
    private DecRegistroUnitaDoc cercaDecRegistroUnitaDocPerNome(String nomeReg, List<DecRegistroUnitaDoc> lista) {
        DecRegistroUnitaDoc ret = null;
        if (lista != null) {
            for (DecRegistroUnitaDoc decRegistroUnitaDoc : lista) {
                if (nomeReg.equals(decRegistroUnitaDoc.getCdRegistroUnitaDoc())) {
                    ret = decRegistroUnitaDoc;
                    break;
                }
            }
        }
        return ret;
    }

    /*
     * Crea una lista di tipi ud da una lista di tipi Ud di una struttura.
     */
    private List<DecRegistroUnitaDoc> determinaDecRegistroUnitaDocs(OrgStrut oldStrut, Date dataAttuale,
            boolean includiElementiDisattivi, OrgStrut newStrut, boolean mantieniDateFineValidita,
            OrgStrutCopyResult result) throws ParerUserError {
        ArrayList<DecRegistroUnitaDoc> al = null;
        if (oldStrut.getDecRegistroUnitaDocs() != null && (!oldStrut.getDecRegistroUnitaDocs().isEmpty())) {
            al = new ArrayList();
            for (DecRegistroUnitaDoc regOld : oldStrut.getDecRegistroUnitaDocs()) {
                EntitaValida entita = new EntitaValida(includiElementiDisattivi, mantieniDateFineValidita,
                        regOld.getDtIstituz(), regOld.getDtSoppres(), dataAttuale);
                if (entita.isValida()) {
                    DecRegistroUnitaDoc reg = new DecRegistroUnitaDoc();
                    al.add(reg);
                    reg.setCdRegistroNormaliz(regOld.getCdRegistroNormaliz());
                    reg.setCdRegistroUnitaDoc(regOld.getCdRegistroUnitaDoc());
                    reg.setCdSerieDaCreare(regOld.getCdSerieDaCreare());
                    reg.setDsRegistroUnitaDoc(regOld.getDsRegistroUnitaDoc());
                    reg.setDsSerieDaCreare(regOld.getDsSerieDaCreare());
                    reg.setDsTipoSerieDaCreare(regOld.getDsTipoSerieDaCreare());
                    reg.setDtIstituz(entita.getDataInizio());
                    reg.setDtSoppres(entita.getDataFine());
                    reg.setFlCreaSerie(regOld.getFlCreaSerie());
                    reg.setFlCreaTipoSerieStandard(regOld.getFlCreaTipoSerieStandard());
                    reg.setFlRegistroFisc(regOld.getFlRegistroFisc());
                    reg.setFlTipoSerieMult(regOld.getFlTipoSerieMult());
                    reg.setNiAnniConserv(regOld.getNiAnniConserv());
                    reg.setNmTipoSerieDaCreare(regOld.getNmTipoSerieDaCreare());
                    reg.setOrgStrut(newStrut);
                    reg.setDecModelloTipoSerie(determinaModelloTipoSerie(result, regOld.getDecModelloTipoSerie()));
                    tipoUnitaDocHelper.getEntityManager().persist(reg);
                    reg.setDecAaRegistroUnitaDocs(determinaPeriodiValiditaRegistro(regOld, reg));
                    reg.setDecTipoUnitaDocAmmessos(determinaTipiUdAmmessi(regOld, reg, newStrut));
                }
            }
        }

        return al;
    }

    private DecModelloTipoSerie determinaModelloTipoSerie(OrgStrutCopyResult result,
            DecModelloTipoSerie decModelloTipoSerieOld) throws ParerUserError {
        DecModelloTipoSerie decModelloTipoSerie = null;
        if (decModelloTipoSerieOld != null) {
            OrgAmbiente orgAmbiente = ambientiHelper
                    .getOrgAmbienteByName(result.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
            if (orgAmbiente == null) {
                result.addMessageIfNotExists(ERRORE_MODELLO_TIPO_SEIRE);
                throw new ParerUserError(ERRORE_MODELLO_TIPO_SEIRE);
            } else {
                decModelloTipoSerie = modelliSerieHelper.getDecModelloTipoSerie(
                        decModelloTipoSerieOld.getNmModelloTipoSerie(), new BigDecimal(orgAmbiente.getIdAmbiente()));
                if (decModelloTipoSerie == null) {
                    result.addMessageIfNotExists(ERRORE_MODELLO_TIPO_SEIRE);
                    throw new ParerUserError(ERRORE_MODELLO_TIPO_SEIRE);
                }
            }
        }
        return decModelloTipoSerie;
    }

    /*
     * Crea una lista di tipi fascicolo da una lista di tipi fascicolo di una struttura.
     */
    private List<DecTipoFascicolo> determinaDecTipoFascicolos(OrgStrut oldStrut, Date dataAttuale,
            boolean includiElementiDisattivi, OrgStrut newStrut, boolean mantieniDateFineValidita,
            boolean includiCriteriDiRaggruppamento) {
        ArrayList<DecTipoFascicolo> al = null;
        if (oldStrut.getDecTipoFascicolos() != null && (!oldStrut.getDecTipoFascicolos().isEmpty())) {
            al = new ArrayList();
            // Contenitore Criteri raggr fascicolo
            HashMap<String, DecCriterioRaggrFasc> mappa = new HashMap();
            for (DecTipoFascicolo fasOld : oldStrut.getDecTipoFascicolos()) {
                EntitaValida entita = new EntitaValida(includiElementiDisattivi, mantieniDateFineValidita,
                        fasOld.getDtIstituz(), fasOld.getDtSoppres(), dataAttuale);
                if (entita.isValida()) {
                    DecTipoFascicolo fasNew = new DecTipoFascicolo();
                    al.add(fasNew);
                    fasNew.setDtIstituz(entita.getDataInizio());
                    fasNew.setDtSoppres(entita.getDataFine());
                    fasNew.setOrgStrut(newStrut);
                    fasNew.setDsTipoFascicolo(fasOld.getDsTipoFascicolo());
                    fasNew.setNmTipoFascicolo(fasOld.getNmTipoFascicolo());
                    fasNew.setDecAaTipoFascicolos(determinaPeriodiValiditaFascicolo(fasOld, fasNew));
                    tipoUnitaDocHelper.getEntityManager().persist(fasNew);
                    // Gestione criteri di raggruppamento del fascicolo
                    if (includiCriteriDiRaggruppamento == true && fasOld.getDecSelCriterioRaggrFascicolos() != null
                            && (!fasOld.getDecSelCriterioRaggrFascicolos().isEmpty())) {
                        for (DecSelCriterioRaggrFasc selOld : fasOld.getDecSelCriterioRaggrFascicolos()) {
                            /*
                             * Ricrea solo i Sel criteri di raggruppamento riguardanti il tuipo fascicolo escludendo gli
                             * altri
                             */
                            if (selOld.getTiSel().equals(
                                    it.eng.parer.entity.constraint.DecSelCriterioRaggrFasc.TiSelFasc.TIPO_FASCICOLO
                                            .name())) {
                                DecCriterioRaggrFasc raggOld = selOld.getDecCriterioRaggrFasc();
                                EntitaValida entita2 = new EntitaValida(includiElementiDisattivi,
                                        mantieniDateFineValidita, raggOld.getDtIstituz(), raggOld.getDtSoppres(),
                                        dataAttuale);
                                if (entita2.isValida()) {
                                    DecSelCriterioRaggrFasc selNew = new DecSelCriterioRaggrFasc();
                                    selNew.setTiSel(selOld.getTiSel());
                                    selNew.setDecTipoFascicolo(fasNew);
                                    DecCriterioRaggrFasc raggNew = mappa.get(raggOld.getNmCriterioRaggr());
                                    if (raggNew == null) {
                                        raggNew = new DecCriterioRaggrFasc();
                                        raggNew.setAaFascicolo(raggOld.getAaFascicolo());
                                        raggNew.setAaFascicoloA(raggOld.getAaFascicoloA());
                                        raggNew.setAaFascicoloDa(raggOld.getAaFascicoloDa());
                                        raggNew.setDsCriterioRaggr(raggOld.getDsCriterioRaggr());
                                        raggNew.setDsOggettoFascicolo(raggOld.getDsOggettoFascicolo());
                                        raggNew.setDtApeFascicoloA(raggOld.getDtApeFascicoloA());
                                        raggNew.setDtApeFascicoloDa(raggOld.getDtApeFascicoloDa());
                                        raggNew.setDtChiuFascicoloA(raggOld.getDtChiuFascicoloA());
                                        raggNew.setDtChiuFascicoloDa(raggOld.getDtChiuFascicoloDa());
                                        raggNew.setDtIstituz(entita2.getDataInizio());
                                        raggNew.setDtSoppres(entita2.getDataFine());
                                        raggNew.setDtVersA(raggOld.getDtVersA());
                                        raggNew.setDtVersDa(raggOld.getDtVersDa());
                                        raggNew.setFlCriterioRaggrStandard(raggOld.getFlCriterioRaggrStandard());
                                        raggNew.setFlFiltroSistemaMigraz(raggOld.getFlFiltroSistemaMigraz());
                                        raggNew.setFlFiltroTipoFascicolo(raggOld.getFlFiltroTipoFascicolo());
                                        raggNew.setFlFiltroVoceTitol(raggOld.getFlFiltroVoceTitol());
                                        raggNew.setNiMaxFasc(raggOld.getNiMaxFasc());
                                        raggNew.setNiTempoScadChius(raggOld.getNiTempoScadChius());
                                        raggNew.setNmCriterioRaggr(raggOld.getNmCriterioRaggr());
                                        raggNew.setNtCriterioRaggr(raggOld.getNtCriterioRaggr());
                                        raggNew.setOrgStrut(newStrut);
                                        raggNew.setTiConservazione(raggOld.getTiConservazione());
                                        raggNew.setTiScadChius(raggOld.getTiScadChius());
                                        raggNew.setTiTempoScadChius(raggOld.getTiTempoScadChius());
                                        mappa.put(raggNew.getNmCriterioRaggr(), raggNew);
                                        tipoUnitaDocHelper.getEntityManager().persist(raggNew);
                                    }
                                    selNew.setDecCriterioRaggrFasc(raggNew);
                                    tipoUnitaDocHelper.getEntityManager().persist(selNew);
                                }

                            }
                        }
                    }
                }
            }
        }
        return al;
    }

    /*
     * Determina Periodi di validita del registro e le sue parti COMPLETO!
     */
    private List<DecAaRegistroUnitaDoc> determinaPeriodiValiditaRegistro(DecRegistroUnitaDoc regOld,
            DecRegistroUnitaDoc regNew) {
        ArrayList<DecAaRegistroUnitaDoc> al = null;
        if (regOld.getDecAaRegistroUnitaDocs() != null && (!regOld.getDecAaRegistroUnitaDocs().isEmpty())) {
            al = new ArrayList();
            for (DecAaRegistroUnitaDoc decAaRegistroUnitaDocOld : regOld.getDecAaRegistroUnitaDocs()) {
                DecAaRegistroUnitaDoc aa = new DecAaRegistroUnitaDoc();
                aa.setAaMaxRegistroUnitaDoc(decAaRegistroUnitaDocOld.getAaMaxRegistroUnitaDoc());
                aa.setAaMinRegistroUnitaDoc(decAaRegistroUnitaDocOld.getAaMinRegistroUnitaDoc());
                aa.setCdFormatoNumero(decAaRegistroUnitaDocOld.getCdFormatoNumero());
                aa.setDecRegistroUnitaDoc(regNew);
                aa.setDsFormatoNumero(decAaRegistroUnitaDocOld.getDsFormatoNumero());
                aa.setFlUpdFmtNumero(decAaRegistroUnitaDocOld.getFlUpdFmtNumero());
                al.add(aa);
                tipoUnitaDocHelper.getEntityManager().persist(aa);
                if (decAaRegistroUnitaDocOld.getDecParteNumeroRegistros() != null
                        && (!decAaRegistroUnitaDocOld.getDecParteNumeroRegistros().isEmpty())) {
                    ArrayList<DecParteNumeroRegistro> alParti = new ArrayList();
                    aa.setDecParteNumeroRegistros(alParti);
                    for (DecParteNumeroRegistro pOld : decAaRegistroUnitaDocOld.getDecParteNumeroRegistros()) {
                        DecParteNumeroRegistro p = new DecParteNumeroRegistro();
                        alParti.add(p);
                        p.setDecAaRegistroUnitaDoc(aa);
                        p.setDlValoriParte(pOld.getDlValoriParte());
                        p.setDsParteNumeroRegistro(pOld.getDsParteNumeroRegistro());
                        p.setNiMaxCharParte(pOld.getNiMaxCharParte());
                        p.setNiMinCharParte(pOld.getNiMinCharParte());
                        p.setNiParteNumeroRegistro(pOld.getNiParteNumeroRegistro());
                        p.setNmParteNumeroRegistro(pOld.getNmParteNumeroRegistro());
                        p.setTiCharParte(pOld.getTiCharParte());
                        p.setTiCharSep(pOld.getTiCharSep());
                        p.setTiPadSxParte(pOld.getTiPadSxParte());
                        p.setTiParte(pOld.getTiParte());
                        tipoUnitaDocHelper.getEntityManager().persist(p);
                    }
                }
            }
        }
        return al;
    }

    /*
     * Determina Periodi di validita del fascicolo e le sue parti
     */
    private List<DecAaTipoFascicolo> determinaPeriodiValiditaFascicolo(DecTipoFascicolo fasOld,
            DecTipoFascicolo fasNew) {
        ArrayList<DecAaTipoFascicolo> al = null;
        if (fasOld.getDecAaTipoFascicolos() != null && (!fasOld.getDecAaTipoFascicolos().isEmpty())) {
            al = new ArrayList();
            for (DecAaTipoFascicolo decAaTipoFascicoloOld : fasOld.getDecAaTipoFascicolos()) {
                DecAaTipoFascicolo aa = new DecAaTipoFascicolo();
                aa.setDecTipoFascicolo(fasNew);
                aa.setAaFinTipoFascicolo(decAaTipoFascicoloOld.getAaFinTipoFascicolo());
                aa.setAaIniTipoFascicolo(decAaTipoFascicoloOld.getAaIniTipoFascicolo());
                aa.setFlUpdFmtNumero(decAaTipoFascicoloOld.getFlUpdFmtNumero());
                aa.setNiCharPadParteClassif(decAaTipoFascicoloOld.getNiCharPadParteClassif());
                al.add(aa);
                if (decAaTipoFascicoloOld.getDecParteNumeroFascicolos() != null
                        && (!decAaTipoFascicoloOld.getDecParteNumeroFascicolos().isEmpty())) {
                    ArrayList<DecParteNumeroFascicolo> alParti = new ArrayList();
                    aa.setDecParteNumeroFascicolos(alParti);
                    for (DecParteNumeroFascicolo pOld : decAaTipoFascicoloOld.getDecParteNumeroFascicolos()) {
                        DecParteNumeroFascicolo p = new DecParteNumeroFascicolo();
                        alParti.add(p);
                        p.setDecAaTipoFascicolo(aa);
                        p.setDlValoriParte(pOld.getDlValoriParte());
                        p.setDsParteNumero(pOld.getDsParteNumero());
                        p.setNiMaxCharParte(pOld.getNiMaxCharParte());
                        p.setNiMinCharParte(pOld.getNiMinCharParte());
                        p.setNiParteNumero(pOld.getNiParteNumero());
                        p.setNmParteNumero(pOld.getNmParteNumero());
                        p.setTiCharParte(pOld.getTiCharParte());
                        p.setTiPadParte(pOld.getTiPadParte());
                        p.setTiParte(pOld.getTiParte());
                        p.setTiCharSep(pOld.getTiCharSep());
                        // tipoUnitaDocHelper.getEntityManager().persist(p);
                    }
                }
            }
        }
        return al;
    }

    /*
     * Determina Tipi UD ammessi
     */
    private List<DecTipoUnitaDocAmmesso> determinaTipiUdAmmessi(DecRegistroUnitaDoc regOld, DecRegistroUnitaDoc regNew,
            OrgStrut newStrut) {
        ArrayList<DecTipoUnitaDocAmmesso> al = null;
        if (regOld.getDecTipoUnitaDocAmmessos() != null && (!regOld.getDecTipoUnitaDocAmmessos().isEmpty())) {
            al = new ArrayList();
            for (DecTipoUnitaDocAmmesso ammOld : regOld.getDecTipoUnitaDocAmmessos()) {
                String nomeUd = ammOld.getDecTipoUnitaDoc().getNmTipoUnitaDoc();
                DecTipoUnitaDoc tipo = cercaDecTipoUnitaDocPerNome(nomeUd, newStrut.getDecTipoUnitaDocs());
                if (tipo != null) {
                    DecTipoUnitaDocAmmesso ammNew = new DecTipoUnitaDocAmmesso();
                    ammNew.setDecRegistroUnitaDoc(regNew);
                    ammNew.setDecTipoUnitaDoc(cercaDecTipoUnitaDocPerNome(nomeUd, newStrut.getDecTipoUnitaDocs()));
                    al.add(ammNew);
                    tipoUnitaDocHelper.getEntityManager().persist(ammNew);
                }
            }
        }
        return al;
    }

    private void elaboraTipiStrutturaUnitaDoc(OrgStrut oldStrut, Date dataAttuale, boolean includiElementiDisattivi,
            OrgStrut newStrut, boolean mantieniDateFineValidita, OrgStrutCopyResult result) {
        if (oldStrut.getDecTipoUnitaDocs() != null && (!oldStrut.getDecTipoUnitaDocs().isEmpty())) {
            for (DecTipoUnitaDoc udOld : oldStrut.getDecTipoUnitaDocs()) {
                EntitaValida entita = new EntitaValida(includiElementiDisattivi, mantieniDateFineValidita,
                        udOld.getDtIstituz(), udOld.getDtSoppres(), dataAttuale);
                if (entita.isValida()) {
                    DecTipoUnitaDoc udNew = cercaDecTipoUnitaDocPerNome(udOld.getNmTipoUnitaDoc(),
                            newStrut.getDecTipoUnitaDocs());
                    udNew.setDecTipoStrutUnitaDocs(determinaDecTipoStrutUnitaDocs(udOld, udNew, dataAttuale,
                            includiElementiDisattivi, mantieniDateFineValidita, result));
                }
            }
        }
    }

    /*
     * Crea una lista di tipo strut unita doc da una lista di tipo strut unita doc di una UD.
     */
    private List<DecTipoStrutUnitaDoc> determinaDecTipoStrutUnitaDocs(DecTipoUnitaDoc oldUd, DecTipoUnitaDoc newUd,
            Date dataAttuale, boolean includiElementiDisattivi, boolean mantieniDateFineValidita,
            OrgStrutCopyResult result) {
        ArrayList<DecTipoStrutUnitaDoc> al = null;
        if (oldUd.getDecTipoStrutUnitaDocs() != null && (!oldUd.getDecTipoStrutUnitaDocs().isEmpty())) {
            al = new ArrayList();
            for (DecTipoStrutUnitaDoc tipoOld : oldUd.getDecTipoStrutUnitaDocs()) {
                EntitaValida entita = new EntitaValida(includiElementiDisattivi, mantieniDateFineValidita,
                        tipoOld.getDtIstituz(), tipoOld.getDtSoppres(), dataAttuale);
                if (entita.isValida()) {
                    DecTipoStrutUnitaDoc tipoNew = new DecTipoStrutUnitaDoc();
                    al.add(tipoNew);
                    tipoNew.setDecTipoUnitaDoc(newUd);
                    tipoNew.setDtIstituz(entita.getDataInizio());
                    tipoNew.setDtSoppres(entita.getDataFine());
                    tipoNew.setNmTipoStrutUnitaDoc(tipoOld.getNmTipoStrutUnitaDoc());
                    tipoNew.setDsTipoStrutUnitaDoc(tipoOld.getDsTipoStrutUnitaDoc());
                    tipoNew.setAaMaxTipoStrutUnitaDoc(tipoOld.getAaMaxTipoStrutUnitaDoc());
                    tipoNew.setAaMinTipoStrutUnitaDoc(tipoOld.getAaMinTipoStrutUnitaDoc());
                    tipoNew.setDsAnnoTipoStrutUnitaDoc(tipoOld.getDsAnnoTipoStrutUnitaDoc());
                    tipoNew.setDsDataTipoStrutUnitaDoc(tipoOld.getDsDataTipoStrutUnitaDoc());
                    tipoNew.setDsNumeroTipoStrutUnitaDoc(tipoOld.getDsNumeroTipoStrutUnitaDoc());
                    tipoNew.setDsOggTipoStrutUnitaDoc(tipoOld.getDsOggTipoStrutUnitaDoc());
                    tipoNew.setDsRifTempTipoStrutUd(tipoOld.getDsRifTempTipoStrutUd());
                    tipoNew.setDsCollegamentiUd(tipoOld.getDsCollegamentiUd());
                    tipoNew.setDsPeriodicitaVers(tipoOld.getDsPeriodicitaVers());
                    tipoNew.setDsFirma(tipoOld.getDsFirma());
                    // tipo.setDecTipoDocAmmessos(decTipoDocAmmessos);
                    tipoUnitaDocHelper.getEntityManager().persist(tipoNew);
                    /*
                     * Gestisce le associazioni tra il tipo struttura UD e i sistemi versanti solo se i sistemi ci sono,
                     * altrimenti oltrepassa
                     */
                    if (tipoOld.getDecTipoStrutUdSisVers() != null && (!tipoOld.getDecTipoStrutUdSisVers().isEmpty())) {
                        ArrayList<DecTipoStrutUdSisVer> alSis = new ArrayList();
                        for (DecTipoStrutUdSisVer sisOld : tipoOld.getDecTipoStrutUdSisVers()) {
                            AplSistemaVersante aplSistemaVersante = tipoUnitaDocHelper
                                    .getAplSistemaVersanteByName(sisOld.getAplSistemaVersante().getNmSistemaVersante());
                            // L'associazione viene fatta solo se il sistema versante ÃƒÂ¨ giÃƒÂ  presente nel sistema
                            // in cui si importa
                            if (aplSistemaVersante != null) {
                                DecTipoStrutUdSisVer sisNew = new DecTipoStrutUdSisVer();
                                sisNew.setAplSistemaVersante(aplSistemaVersante);
                                sisNew.setDecTipoStrutUnitaDoc(tipoNew);
                                alSis.add(sisNew);
                                tipoUnitaDocHelper.getEntityManager().persist(sisNew);
                            } else {
                                result.addMessageIfNotExists(MessageFormat.format(
                                        "Non è stato possibile associare il tipo struttura {0} al sistema versante {1}",
                                        tipoNew.getNmTipoStrutUnitaDoc(),
                                        sisOld.getAplSistemaVersante().getNmSistemaVersante()));
                            }
                        }
                    }
                    /* gestisce le associazioni tra il tipo struttura UD e gli XSD */
                    if (tipoOld.getDecTipoStrutUdXsds() != null && (!tipoOld.getDecTipoStrutUdXsds().isEmpty())) {
                        ArrayList<DecTipoStrutUdXsd> alUdXsd = new ArrayList();
                        for (DecTipoStrutUdXsd udXsdOld : tipoOld.getDecTipoStrutUdXsds()) {
                            DecXsdDatiSpec specOld = udXsdOld.getDecXsdDatiSpec();
                            if (specOld != null) {
                                DecXsdDatiSpec specNew = cercaDecXsdDatiSpecPerVersione(specOld.getCdVersioneXsd(),
                                        newUd.getDecXsdDatiSpecs());
                                if (specNew != null) {
                                    DecTipoStrutUdXsd udXsdnew = new DecTipoStrutUdXsd();
                                    udXsdnew.setDecTipoStrutUnitaDoc(tipoNew);
                                    udXsdnew.setDecXsdDatiSpec(specNew);
                                    tipoUnitaDocHelper.getEntityManager().persist(udXsdnew);
                                    alUdXsd.add(udXsdnew);
                                }
                            }
                        }
                        tipoNew.setDecTipoStrutUdXsds(alUdXsd);
                    }
                    // tipoUnitaDocHelper.getEntityManager().persist(tipoNew);
                    // Associa il tipo struttura UD al registro
                    if (tipoOld.getDecTipoStrutUdRegs() != null && (!tipoOld.getDecTipoStrutUdRegs().isEmpty())) {
                        ArrayList<DecTipoStrutUdReg> alUdRegNew = new ArrayList();
                        for (DecTipoStrutUdReg udRegOld : tipoOld.getDecTipoStrutUdRegs()) {
                            DecRegistroUnitaDoc regOld = udRegOld.getDecRegistroUnitaDoc();
                            if (regOld != null) {
                                DecRegistroUnitaDoc regNew = cercaDecRegistroUnitaDocPerNome(
                                        udRegOld.getDecRegistroUnitaDoc().getCdRegistroUnitaDoc(),
                                        newUd.getOrgStrut().getDecRegistroUnitaDocs());
                                if (regNew != null) {
                                    DecTipoStrutUdReg udRegNew = new DecTipoStrutUdReg();
                                    udRegNew.setDecRegistroUnitaDoc(regNew);
                                    udRegNew.setDecTipoStrutUnitaDoc(tipoNew);
                                    tipoUnitaDocHelper.getEntityManager().persist(udRegNew);
                                }
                            }
                        }
                        tipoNew.setDecTipoStrutUdRegs(alUdRegNew);
                    }
                }
            }
        }
        return al;
    }

    /* Metodo usato per le regole val sub strut per i tipi UD e i tipi doc */
    private List<OrgRegolaValSubStrut> determinaOrgRegoleValSubStruts(boolean isStandard, List<DecTipoUnitaDoc> oldUds,
            List<DecTipoUnitaDoc> newUds, List<DecTipoDoc> newDocs, Date dataAttuale, boolean includiElementiDisattivi,
            boolean mantieniDateFineValidita, OrgStrut newStrut) {
        ArrayList<OrgRegolaValSubStrut> al = null;
        if (oldUds != null && (!oldUds.isEmpty())) {
            al = new ArrayList();
            for (DecTipoUnitaDoc oldUd : oldUds) {
                String nomeVecchiaUd = oldUd.getNmTipoUnitaDoc();
                DecTipoUnitaDoc udDaAssociare = cercaDecTipoUnitaDocPerNome(nomeVecchiaUd, newUds);
                if (udDaAssociare != null) {
                    List<OrgRegolaValSubStrut> oldRegoleList = oldUd.getOrgRegolaValSubStruts();
                    if (oldRegoleList != null) {
                        for (OrgRegolaValSubStrut regolaOld : oldRegoleList) {
                            EntitaValida entita = new EntitaValida(includiElementiDisattivi, mantieniDateFineValidita,
                                    regolaOld.getDtIstituz(), regolaOld.getDtSoppres(), dataAttuale);
                            if (entita.isValida()) {
                                String nomeVecchioDoc = regolaOld.getDecTipoDoc().getNmTipoDoc();
                                DecTipoDoc docDaAssociare = cercaDecTipoDocPerNome(nomeVecchioDoc, newDocs);
                                if (docDaAssociare != null) {
                                    OrgRegolaValSubStrut regolaNew = new OrgRegolaValSubStrut();
                                    al.add(regolaNew);
                                    regolaNew.setDtIstituz(entita.getDataInizio());
                                    regolaNew.setDtSoppres(entita.getDataFine());
                                    regolaNew.setDecTipoUnitaDoc(udDaAssociare);
                                    regolaNew.setDecTipoDoc(docDaAssociare);
                                    tipoUnitaDocHelper.getEntityManager().persist(regolaNew);
                                    /*
                                     * Se la duplica/import è standard si importano i campi e se il tipo campo ==
                                     * SUB_STUT si assegna l'unica substruttura della Struttura.
                                     */
                                    if (isStandard) {
                                        // Aggiunti anche i Valori!
                                        if (regolaOld.getOrgCampoValSubStruts() != null
                                                && regolaOld.getOrgCampoValSubStruts().size() > 0) {
                                            ArrayList<OrgCampoValSubStrut> alVal = new ArrayList<>();
                                            for (OrgCampoValSubStrut orgCampoValSubStrutOld : regolaOld
                                                    .getOrgCampoValSubStruts()) {
                                                OrgCampoValSubStrut campoValSubStrutNew = new OrgCampoValSubStrut();
                                                campoValSubStrutNew.setNmCampo(orgCampoValSubStrutOld.getNmCampo());
                                                campoValSubStrutNew.setOrgRegolaValSubStrut(regolaNew);
                                                campoValSubStrutNew.setTiCampo(orgCampoValSubStrutOld.getTiCampo());
                                                if (orgCampoValSubStrutOld.getTiCampo().equals("SUB_STRUT")) {
                                                    campoValSubStrutNew
                                                            .setOrgSubStrut(newStrut.getOrgSubStruts().get(0));
                                                }
                                                tipoUnitaDocHelper.getEntityManager().persist(campoValSubStrutNew);
                                                alVal.add(campoValSubStrutNew);
                                            }
                                            regolaNew.setOrgCampoValSubStruts(alVal);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return al;
    }

    private void validaXsd(String xsd) throws ParerUserError {
        if (StringUtils.isNotBlank(xsd)) {
            try {
                datiSpecificiEjb.parseStringaXsd(xsd);
            } catch (ParerUserError e) {
                logger.debug("XSD ERRATO: " + xsd);
                throw e;
            }
            /*
             * SchemaFactory schemaFactoryValidazioneDatiSpec =
             * SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema"); try {
             * schemaFactoryValidazioneDatiSpec.newSchema(new StreamSource(new StringReader(xsd)));
             * datiSpecificiEjb.parseStringaXsd(xsd); } catch (SAXException e) {
             * logger.error("Eccezione nel parsing dello schema del file xsd", e); throw new
             * ParerUserError("l'XSD contiene errori formali"); }
             */
        }
    }

    public class OrgStrutCopyResult {

        private ArrayList<String> mex = new ArrayList();
        private OrgStrut orgStrut;
        private IamOrganizDaReplic iamOrganizDaReplic;

        public void addMessage(String messaggio) {
            mex.add(messaggio);
        }

        public void addMessageIfNotExists(String messaggio) {
            if (!mex.contains(messaggio)) {
                mex.add(messaggio);
            }
        }

        public ArrayList<String> getMex() {
            return mex;
        }

        public void setMex(ArrayList<String> mex) {
            this.mex = mex;
        }

        public OrgStrut getOrgStrut() {
            return orgStrut;
        }

        public void setOrgStrut(OrgStrut orgStrut) {
            this.orgStrut = orgStrut;
        }

        public IamOrganizDaReplic getIamOrganizDaReplic() {
            return iamOrganizDaReplic;
        }

        public void setIamOrganizDaReplic(IamOrganizDaReplic iamOrganizDaReplic) {
            this.iamOrganizDaReplic = iamOrganizDaReplic;
        }

    }
}
