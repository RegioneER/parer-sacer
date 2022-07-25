package it.eng.parer.job.indiceAipFascicoli.utils;

import it.eng.parer.aipFascicoli.xml.usprofascResp.AmministrazionePartecipanteType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.AmministrazioneTitolareType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.AmministrazioniPartecipantiType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.CamiciaFascicoloType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.ChiaveType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.ChiaveUDType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.ContenutoAnaliticoUDType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.ContenutoSinteticoType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.Fascicolo;
import it.eng.parer.aipFascicoli.xml.usprofascResp.FascicoloCollegatoType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.IntestazioneFascicoloType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.ProcedimentoAmministrativoType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.ProfiloArchivisticoClassificazioneType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.ProfiloArchivisticoFascicoloType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.ProfiloGeneraleFascicoloType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.ResponsabilitaType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.SCVersatoreType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.SegnaturaArchivisticaType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.SingolaResponsabilitaType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.SoggettiCoinvoltiType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.SoggettoCoinvoltoType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.UnitaOrganizzativeResponsabileType;
import it.eng.parer.aipFascicoli.xml.usprofascResp.VoceClassificazioneType;
import it.eng.parer.entity.DecValVoceTitol;
import it.eng.parer.entity.DecVoceTitol;
import it.eng.parer.entity.FasAmminPartec;
import it.eng.parer.entity.FasLinkFascicolo;
import it.eng.parer.entity.FasRespFascicolo;
import it.eng.parer.entity.FasSogFascicolo;
import it.eng.parer.entity.FasUniOrgRespFascicolo;
import it.eng.parer.job.indiceAipFascicoli.helper.CreazioneIndiceMetaFascicoliHelper;
import it.eng.parer.viewEntity.FasVLisUdInFasc;
import it.eng.parer.viewEntity.FasVVisFascicolo;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author DiLorenzo_F
 */
public class CreazioneIndiceMetaFascicoliUtil {

    private CreazioneIndiceMetaFascicoliHelper cimfHelper;

    public CreazioneIndiceMetaFascicoliUtil() throws NamingException {
        // Recupera l'ejb per la lettura di informazioni, se possibile
        cimfHelper = (CreazioneIndiceMetaFascicoliHelper) new InitialContext()
                .lookup("java:module/CreazioneIndiceMetaFascicoliHelper");
    }

    public Fascicolo generaIndiceMetaFascicolo(FasVVisFascicolo creaMeta, String cdXsd)
            throws DatatypeConfigurationException {
        Fascicolo indiceMetaFascicolo = new Fascicolo();
        popolaIndiceMetaFascicolo(indiceMetaFascicolo, creaMeta, cdXsd);
        return indiceMetaFascicolo;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void popolaIndiceMetaFascicolo(Fascicolo indiceMetaFascicolo, FasVVisFascicolo creaMeta, String cdXsd)
            throws DatatypeConfigurationException {
        /***************************************
         * VERSIONE PROFILO COMPLETO FASCICOLO *
         ***************************************/

        indiceMetaFascicolo.setVersioneProfiloCompletoFascicolo(cdXsd);

        /****************
         * INTESTAZIONE *
         ****************/

        // Versatore
        SCVersatoreType versatore = new SCVersatoreType();
        versatore.setAmbiente(creaMeta.getNmAmbiente());
        versatore.setEnte(creaMeta.getNmEnte());
        versatore.setStruttura(creaMeta.getNmStrut());
        versatore.setUserID(creaMeta.getNmUserid());
        // Chiave
        ChiaveType chiave = new ChiaveType();
        chiave.setAnno(creaMeta.getAaFascicolo().intValue());
        chiave.setNumero(creaMeta.getCdKeyFascicolo());
        // Tipo Fascicolo
        String nmTipoFascicolo = creaMeta.getNmTipoFascicolo();

        IntestazioneFascicoloType intestazione = new IntestazioneFascicoloType();
        intestazione.setVersatore(versatore);
        intestazione.setChiave(chiave);
        intestazione.setTipoFascicolo(nmTipoFascicolo);

        indiceMetaFascicolo.setIntestazione(intestazione);

        /******************************
         * PROFILO GENERALE FASCICOLO *
         ******************************/

        ProfiloGeneraleFascicoloType profiloGeneraleFascicolo = new ProfiloGeneraleFascicoloType();

        // DATA APERTURA
        XMLGregorianCalendar dataApeFascicolo = null;
        if (creaMeta.getDtApeFascicolo() != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(creaMeta.getDtApeFascicolo());
            dataApeFascicolo = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        }
        profiloGeneraleFascicolo.setDataApertura(dataApeFascicolo);

        // DATA CHIUSURA
        XMLGregorianCalendar dataChiuFascicolo = null;
        if (creaMeta.getDtChiuFascicolo() != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(creaMeta.getDtChiuFascicolo());
            dataChiuFascicolo = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        }
        profiloGeneraleFascicolo.setDataChiusura(dataChiuFascicolo);

        // OGGETTO
        String dsOggettoFascicolo = creaMeta.getDsOggettoFascicolo();
        profiloGeneraleFascicolo.setOggetto(dsOggettoFascicolo);

        /*
         * PRIMO DOCUMENTO NEL FASCICOLO
         */
        // ChiaveUD Primo Doc
        ChiaveUDType chiaveUdPrimoDocNelFascicolo = new ChiaveUDType();
        if (creaMeta.getCdRegKeyUnitaDocFirst() != null && creaMeta.getAaKeyUnitaDocFirst() != null
                && creaMeta.getCdKeyUnitaDocFirst() != null) {
            chiaveUdPrimoDocNelFascicolo.setRegistro(creaMeta.getCdRegKeyUnitaDocFirst());
            chiaveUdPrimoDocNelFascicolo.setAnno(creaMeta.getAaKeyUnitaDocFirst().intValue());
            chiaveUdPrimoDocNelFascicolo.setNumero(creaMeta.getCdKeyUnitaDocFirst());

            profiloGeneraleFascicolo.setPrimoDocumentoNelFascicolo(chiaveUdPrimoDocNelFascicolo);
        }

        /*
         * ULTIMO DOCUMENTO NEL FASCICOLO
         */
        // ChiaveUD Ultimo Doc
        ChiaveUDType chiaveUdUltimoDocNelFascicolo = new ChiaveUDType();
        if (creaMeta.getCdRegKeyUnitaDocLast() != null && creaMeta.getAaKeyUnitaDocLast() != null
                && creaMeta.getCdKeyUnitaDocLast() != null) {
            chiaveUdUltimoDocNelFascicolo.setRegistro(creaMeta.getCdRegKeyUnitaDocLast());
            chiaveUdUltimoDocNelFascicolo.setAnno(creaMeta.getAaKeyUnitaDocLast().intValue());
            chiaveUdUltimoDocNelFascicolo.setNumero(creaMeta.getCdKeyUnitaDocLast());

            profiloGeneraleFascicolo.setUltimoDocumentoNelFascicolo(chiaveUdUltimoDocNelFascicolo);
        }

        // TEMPO CONSERVAZIONE
        long niAaConserzione = creaMeta.getNiAaConservazione().longValue();
        profiloGeneraleFascicolo.setTempoConservazione(niAaConserzione);

        // LIVELLO RISERVATEZZA
        String cdLivelloRiserv = Objects.toString(creaMeta.getCdLivelloRiserv(), "");
        profiloGeneraleFascicolo.setLivelloRiservatezza(cdLivelloRiserv);

        /*
         * AMMINISTRAZIONE TITOLARE
         */
        AmministrazioneTitolareType amministrazioneTitolare = new AmministrazioneTitolareType();
        if (creaMeta.getDsAmminTitol() != null && creaMeta.getCdIpaAmminTitol() != null
                && creaMeta.getTiCodiceAmminTitol() != null) {
            amministrazioneTitolare.setDenominazione(creaMeta.getDsAmminTitol());
            amministrazioneTitolare.setCodice(creaMeta.getCdIpaAmminTitol());
            amministrazioneTitolare.setTipoCodice(creaMeta.getTiCodiceAmminTitol());

            profiloGeneraleFascicolo.setAmministrazioneTitolare(amministrazioneTitolare);
        }

        /*
         * AMMINISTRAZIONI PARTECIPANTI
         */
        List<FasAmminPartec> fasAmminPartecList = cimfHelper.getFasAmminPartec(creaMeta.getIdFascicolo().longValue());
        AmministrazioniPartecipantiType amministrazioniPartecipamenti = new AmministrazioniPartecipantiType();
        for (FasAmminPartec fasAmminPartec : fasAmminPartecList) {
            AmministrazionePartecipanteType amminPartec = new AmministrazionePartecipanteType();
            if (fasAmminPartec.getDsAmminPartec() != null && fasAmminPartec.getCdAmminPartec() != null
                    && fasAmminPartec.getTiCodiceAmminPartec() != null) {
                amminPartec.setDenominazione(fasAmminPartec.getDsAmminPartec());
                amminPartec.setCodice(fasAmminPartec.getCdAmminPartec());
                amminPartec.setTipoCodice(fasAmminPartec.getTiCodiceAmminPartec());
                amministrazioniPartecipamenti.getAmministrazionePartecipante().add(amminPartec);
            }
        }
        if (!amministrazioniPartecipamenti.getAmministrazionePartecipante().isEmpty()) {
            profiloGeneraleFascicolo.setAmministrazioniPartecipanti(amministrazioniPartecipamenti);
        }

        /*
         * SOGGETTI COINVOLTI
         */
        List<FasSogFascicolo> fasSogFascicoloList = cimfHelper
                .getFasSogFascicolo(creaMeta.getIdFascicolo().longValue());
        SoggettiCoinvoltiType soggettiCoinvolti = new SoggettiCoinvoltiType();
        for (FasSogFascicolo fasSogFascicolo : fasSogFascicoloList) {
            SoggettoCoinvoltoType soggettoCoinvolto = new SoggettoCoinvoltoType();
            if (fasSogFascicolo.getTiRapp() != null) {
                soggettoCoinvolto.setNome(fasSogFascicolo.getNmNomeSog());
                soggettoCoinvolto.setCognome(fasSogFascicolo.getNmCognSog());
                soggettoCoinvolto.setDenominazione(fasSogFascicolo.getDsDenomSog());
                soggettoCoinvolto.setIdentificativo(fasSogFascicolo.getCdSog());
                soggettoCoinvolto.setTipoIdentificativo(fasSogFascicolo.getTiCdSog());
                soggettoCoinvolto.setTipoRapporto(fasSogFascicolo.getTiRapp());
                soggettiCoinvolti.getSoggettoCoinvolto().add(soggettoCoinvolto);
            }
        }
        if (!soggettiCoinvolti.getSoggettoCoinvolto().isEmpty()) {
            profiloGeneraleFascicolo.setSoggettiCoinvolti(soggettiCoinvolti);
        }

        /*
         * PROCEDIMENTO AMMINISTRATIVO
         */
        ProcedimentoAmministrativoType procAmmin = new ProcedimentoAmministrativoType();
        if (creaMeta.getCdProcAmmin() != null && creaMeta.getDsProcAmmin() != null) {
            procAmmin.setCodiceProcedimento(creaMeta.getCdProcAmmin());
            procAmmin.setDenominazioneProcedimento(creaMeta.getDsProcAmmin());

            profiloGeneraleFascicolo.setProcedimentoAmministrativo(procAmmin);
        }

        /*
         * RESPONSABILI
         */
        List<FasRespFascicolo> fasRespFascicoloList = cimfHelper
                .getFasRespFascicolo(creaMeta.getIdFascicolo().longValue());
        ResponsabilitaType responsabili = new ResponsabilitaType();
        for (FasRespFascicolo fasRespFascicolo : fasRespFascicoloList) {
            SingolaResponsabilitaType responsabile = new SingolaResponsabilitaType();
            if (fasRespFascicolo.getTiResp() != null) {
                responsabile.setNome(fasRespFascicolo.getNmNomeResp());
                responsabile.setCognome(fasRespFascicolo.getNmCognResp());
                responsabile.setIdentificativo(fasRespFascicolo.getCdResp());
                responsabile.setTipoIdentificativo(fasRespFascicolo.getTiCdResp());
                responsabile.setResponsabilita(fasRespFascicolo.getTiResp());
                responsabili.getResponsabile().add(responsabile);
            }
        }
        if (!responsabili.getResponsabile().isEmpty()) {
            profiloGeneraleFascicolo.setResponsabili(responsabili);
        }

        /*
         * UNITA ORGANIZZATIVE RESPONSABILI
         */
        List<FasUniOrgRespFascicolo> FasUniOrgRespFascicoloList = cimfHelper
                .getFasUniOrgRespFascicolo(creaMeta.getIdFascicolo().longValue());
        UnitaOrganizzativeResponsabileType uniOrgResponsabili = new UnitaOrganizzativeResponsabileType();
        for (FasUniOrgRespFascicolo fasUniOrgRespFascicolo : FasUniOrgRespFascicoloList) {
            if (fasUniOrgRespFascicolo.getCdUniOrgResp() != null) {
                uniOrgResponsabili.getUnitaOrganizzativaResponsabile().add(fasUniOrgRespFascicolo.getCdUniOrgResp());
            }
        }
        if (!uniOrgResponsabili.getUnitaOrganizzativaResponsabile().isEmpty()) {
            profiloGeneraleFascicolo.setUnitaOrganizzativeResponsabili(uniOrgResponsabili);
        }

        // NOTE
        if (creaMeta.getDsNota() != null) {
            String dsNota = creaMeta.getDsNota();
            profiloGeneraleFascicolo.setNote(dsNota);
        }

        indiceMetaFascicolo.setProfiloGeneraleFascicolo(profiloGeneraleFascicolo);

        /**********************************
         * PROFILO ARCHIVISTICO FASCICOLO *
         **********************************/

        /*
         * |----> CLASSIFICAZIONE ----> [VOCI CLASSIFICAZIONE] SEGNATURA ARCHIVISTICA ---| |----> CHIAVE FASCICOLO DI
         * APPARTENENZA
         */
        SegnaturaArchivisticaType segnaturaArchivistica = new SegnaturaArchivisticaType();

        // Voce Classificazione
        ProfiloArchivisticoClassificazioneType.DescrizioneIndiceClassificazione descrizioneIndiceClassificazione = new ProfiloArchivisticoClassificazioneType.DescrizioneIndiceClassificazione();
        if (creaMeta.getIdVoceTitol() != null && creaMeta.getCdCompositoVoceTitol() != null) {
            List<DecValVoceTitol> DecValVoceTitolList = cimfHelper
                    .getDecValVoceTitol(creaMeta.getIdVoceTitol().longValue());
            for (DecValVoceTitol decValVoceTitol : DecValVoceTitolList) {
                VoceClassificazioneType voceClassificazione = new VoceClassificazioneType();
                voceClassificazione.setCodiceVoce(
                        cimfHelper.findById(DecVoceTitol.class, creaMeta.getIdVoceTitol()).getCdVoceTitol());
                voceClassificazione.setDescrizioneVoce(decValVoceTitol.getDsVoceTitol());
                descrizioneIndiceClassificazione.getVoceClassificazione().add(voceClassificazione);
            }

            // Classificazione
            ProfiloArchivisticoClassificazioneType classificazione = new ProfiloArchivisticoClassificazioneType();
            classificazione.setIndiceClassificazione(creaMeta.getCdCompositoVoceTitol());
            classificazione.setDescrizioneIndiceClassificazione(descrizioneIndiceClassificazione);

            segnaturaArchivistica.setClassificazione(classificazione);
        }

        // Chiave fascicolo di appartenenza
        CamiciaFascicoloType chiaveFascicoloDiAppartenenza = new CamiciaFascicoloType();
        if (creaMeta.getAaFascicoloPadre() != null && creaMeta.getCdKeyFascicoloPadre() != null
                && creaMeta.getDsOggettoFascicoloPadre() != null) {
            chiaveFascicoloDiAppartenenza.setAnno(creaMeta.getAaFascicoloPadre().intValue());
            chiaveFascicoloDiAppartenenza.setNumero(creaMeta.getCdKeyFascicoloPadre());
            chiaveFascicoloDiAppartenenza.setOggetto(creaMeta.getDsOggettoFascicoloPadre());

            segnaturaArchivistica.setChiaveFascicoloDiAppartenenza(chiaveFascicoloDiAppartenenza);
        }

        /*
         * COLLEGAMENTI
         */
        List<FasLinkFascicolo> fasLinkFascicoloList = cimfHelper
                .getFasLinkFascicolo(creaMeta.getIdFascicolo().longValue());
        FascicoloCollegatoType collegamenti = new FascicoloCollegatoType();
        for (FasLinkFascicolo fasLinkFascicolo : fasLinkFascicoloList) {
            // Chiave collegamento
            ChiaveType chiaveCollegamento = new ChiaveType();
            chiaveCollegamento.setAnno(fasLinkFascicolo.getAaFascicoloLink().intValue());
            chiaveCollegamento.setNumero(fasLinkFascicolo.getCdKeyFascicoloLink());
            // Fascicolo Collegato
            FascicoloCollegatoType.FascicoloCollegato fascicoloCollegato = new FascicoloCollegatoType.FascicoloCollegato();
            fascicoloCollegato.setChiaveCollegamento(chiaveCollegamento);
            fascicoloCollegato.setDescrizioneCollegamento(fasLinkFascicolo.getDsLink());
            collegamenti.getFascicoloCollegato().add(fascicoloCollegato);
        }

        ProfiloArchivisticoFascicoloType profiloArchivisticoFascicolo = new ProfiloArchivisticoFascicoloType();
        profiloArchivisticoFascicolo.setSegnaturaArchivistica(segnaturaArchivistica);
        if (fasLinkFascicoloList != null && !fasLinkFascicoloList.isEmpty()) {
            profiloArchivisticoFascicolo.setCollegamenti(collegamenti);
        }

        /*******************************************
         * CONTENUTO SINTETICO UNITA' DOCUMENTARIE *
         *******************************************/

        ContenutoSinteticoType contenutoSintetico = new ContenutoSinteticoType();
        if (creaMeta.getNiUnitaDoc() != null) {
            contenutoSintetico.setNumeroUnitaDocumentarie(creaMeta.getNiUnitaDoc().intValue());

            indiceMetaFascicolo.setContenutoSinteticoUnitaDocumentarie(contenutoSintetico);
        }

        /*******************************************
         * CONTENUTO ANALITICO UNITA' DOCUMENTARIE *
         *******************************************/

        /*
         * UNITA' DOCUMENTARIA
         */
        List<FasVLisUdInFasc> fasVLisUdInFascList = cimfHelper.getFasVLisUdInFasc(creaMeta.getIdFascicolo().longValue(),
                creaMeta.getIdUserIamVers().longValue());
        ContenutoAnaliticoUDType contenutoAnalitico = new ContenutoAnaliticoUDType();
        for (FasVLisUdInFasc fasVLisUdInFasc : fasVLisUdInFascList) {
            ChiaveUDType unitaDocumentaria = new ChiaveUDType();
            if (fasVLisUdInFasc.getCdRegistroKeyUnitaDoc() != null && fasVLisUdInFasc.getAaKeyUnitaDoc() != null
                    && fasVLisUdInFasc.getCdKeyUnitaDoc() != null) {
                unitaDocumentaria.setRegistro(fasVLisUdInFasc.getCdRegistroKeyUnitaDoc());
                unitaDocumentaria.setAnno(fasVLisUdInFasc.getAaKeyUnitaDoc().intValue());
                unitaDocumentaria.setNumero(fasVLisUdInFasc.getCdKeyUnitaDoc());
                contenutoAnalitico.getUnitaDocumentaria().add(unitaDocumentaria);
            }
        }
        if (!contenutoAnalitico.getUnitaDocumentaria().isEmpty()) {
            indiceMetaFascicolo.setContenutoAnaliticoUnitaDocumentarie(contenutoAnalitico);
        }

        indiceMetaFascicolo.setProfiloArchivisticoFascicolo(profiloArchivisticoFascicolo);
    }
}
