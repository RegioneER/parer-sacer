package it.eng.parer.web.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Bonora_L
 */
public class CriterioRaggrStandardBean {

    private String nmCriterioRaggr;
    private BigDecimal aaKeyUnitaDoc;
    private BigDecimal aaKeyUnitaDocDa;
    private BigDecimal aaKeyUnitaDocA;
    private BigDecimal niTempoScadChius;
    private String tiTempoScadChius;
    private Set<String> reg;
    private Set<String> tipiUd;
    private Set<String> tipiDoc;
    private BigDecimal niMaxComp;
    private String dlOggettoUnitaDoc;
    private Date dtRegUnitaDocDa;
    private Date dtRegUnitaDocA;
    private String dlDoc;
    private String dsAutoreDoc;
    private Date dtCreazioneDa;
    private Date dtCreazioneA;
    private String tiConservazione;
    private String flUnitaDocFirmato;
    private String flForzaAccettazione;
    private String flForzaConservazione;
    private List<String> tiEsitoVerifFirme;
    private List<String> nmSistemaMigraz;

    public CriterioRaggrStandardBean(String nmCriterioRaggr, BigDecimal aaKeyUnitaDoc, BigDecimal aaKeyUnitaDocDa,
            BigDecimal aaKeyUnitaDocA, BigDecimal niTempoScadChius, String tiTempoScadChius, Set<String> reg,
            Set<String> tipiUd, Set<String> tipiDoc, BigDecimal niMaxComp, String dlOggettoUnitaDoc,
            Date dtRegUnitaDocDa, Date dtRegUnitaDocA, String dlDoc, String dsAutoreDoc, Date dtCreazioneDa,
            Date dtCreazioneA, String tiConservazione, String flUnitaDocFirmato, String flForzaAccettazione,
            String flForzaConservazione, List<String> tiEsitoVerifFirme, List<String> nmSistemaMigraz) {
        this.nmCriterioRaggr = nmCriterioRaggr;
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
        this.aaKeyUnitaDocDa = aaKeyUnitaDocDa;
        this.aaKeyUnitaDocA = aaKeyUnitaDocA;
        this.niTempoScadChius = niTempoScadChius;
        this.tiTempoScadChius = tiTempoScadChius;
        this.reg = reg;
        this.tipiUd = tipiUd;
        this.tipiDoc = tipiDoc;
        this.niMaxComp = niMaxComp;
        this.dlOggettoUnitaDoc = dlOggettoUnitaDoc;
        this.dtRegUnitaDocDa = dtRegUnitaDocDa;
        this.dtRegUnitaDocA = dtRegUnitaDocA;
        this.dlDoc = dlDoc;
        this.dsAutoreDoc = dsAutoreDoc;
        this.dtCreazioneDa = dtCreazioneDa;
        this.dtCreazioneA = dtCreazioneA;
        this.tiConservazione = tiConservazione;
        this.flUnitaDocFirmato = flUnitaDocFirmato;
        this.flForzaAccettazione = flForzaAccettazione;
        this.flForzaConservazione = flForzaConservazione;
        this.tiEsitoVerifFirme = tiEsitoVerifFirme;
        this.nmSistemaMigraz = nmSistemaMigraz;
    }

    /**
     * @return the nmCriterioRaggr
     */
    public String getNmCriterioRaggr() {
        return nmCriterioRaggr;
    }

    /**
     * @param nmCriterioRaggr
     *            the nmCriterioRaggr to set
     */
    public void setNmCriterioRaggr(String nmCriterioRaggr) {
        this.nmCriterioRaggr = nmCriterioRaggr;
    }

    /**
     * @return the aaKeyUnitaDoc
     */
    public BigDecimal getAaKeyUnitaDoc() {
        return aaKeyUnitaDoc;
    }

    /**
     * @param aaKeyUnitaDoc
     *            the aaKeyUnitaDoc to set
     */
    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    /**
     * @return the aaKeyUnitaDocDa
     */
    public BigDecimal getAaKeyUnitaDocDa() {
        return aaKeyUnitaDocDa;
    }

    /**
     * @param aaKeyUnitaDocDa
     *            the aaKeyUnitaDocDa to set
     */
    public void setAaKeyUnitaDocDa(BigDecimal aaKeyUnitaDocDa) {
        this.aaKeyUnitaDocDa = aaKeyUnitaDocDa;
    }

    /**
     * @return the aaKeyUnitaDocA
     */
    public BigDecimal getAaKeyUnitaDocA() {
        return aaKeyUnitaDocA;
    }

    /**
     * @param aaKeyUnitaDocA
     *            the aaKeyUnitaDocA to set
     */
    public void setAaKeyUnitaDocA(BigDecimal aaKeyUnitaDocA) {
        this.aaKeyUnitaDocA = aaKeyUnitaDocA;
    }

    /**
     * @return the niTempoScadChius
     */
    public BigDecimal getNiTempoScadChius() {
        return niTempoScadChius;
    }

    /**
     * @param niTempoScadChius
     *            the niTempoScadChius to set
     */
    public void setNiTempoScadChius(BigDecimal niTempoScadChius) {
        this.niTempoScadChius = niTempoScadChius;
    }

    /**
     * @return the tiTempoScadChius
     */
    public String getTiTempoScadChius() {
        return tiTempoScadChius;
    }

    /**
     * @param tiTempoScadChius
     *            the tiTempoScadChius to set
     */
    public void setTiTempoScadChius(String tiTempoScadChius) {
        this.tiTempoScadChius = tiTempoScadChius;
    }

    /**
     * @return the reg
     */
    public Set<String> getReg() {
        return reg;
    }

    /**
     * @param reg
     *            the reg to set
     */
    public void setReg(Set<String> reg) {
        this.reg = reg;
    }

    /**
     * @return the tipiUd
     */
    public Set<String> getTipiUd() {
        return tipiUd;
    }

    /**
     * @param tipiUd
     *            the tipiUd to set
     */
    public void setTipiUd(Set<String> tipiUd) {
        this.tipiUd = tipiUd;
    }

    /**
     * @return the tipiDoc
     */
    public Set<String> getTipiDoc() {
        return tipiDoc;
    }

    /**
     * @param tipiDoc
     *            the tipiDoc to set
     */
    public void setTipiDoc(Set<String> tipiDoc) {
        this.tipiDoc = tipiDoc;
    }

    /**
     * @return the niMaxComp
     */
    public BigDecimal getNiMaxComp() {
        return niMaxComp;
    }

    /**
     * @param niMaxComp
     *            the niMaxComp to set
     */
    public void setNiMaxComp(BigDecimal niMaxComp) {
        this.niMaxComp = niMaxComp;
    }

    /**
     * @return the dlOggettoUnitaDoc
     */
    public String getDlOggettoUnitaDoc() {
        return dlOggettoUnitaDoc;
    }

    /**
     * @param dlOggettoUnitaDoc
     *            the dlOggettoUnitaDoc to set
     */
    public void setDlOggettoUnitaDoc(String dlOggettoUnitaDoc) {
        this.dlOggettoUnitaDoc = dlOggettoUnitaDoc;
    }

    /**
     * @return the dtRegUnitaDocDa
     */
    public Date getDtRegUnitaDocDa() {
        return dtRegUnitaDocDa;
    }

    /**
     * @param dtRegUnitaDocDa
     *            the dtRegUnitaDocDa to set
     */
    public void setDtRegUnitaDocDa(Date dtRegUnitaDocDa) {
        this.dtRegUnitaDocDa = dtRegUnitaDocDa;
    }

    /**
     * @return the dtRegUnitaDocA
     */
    public Date getDtRegUnitaDocA() {
        return dtRegUnitaDocA;
    }

    /**
     * @param dtRegUnitaDocA
     *            the dtRegUnitaDocA to set
     */
    public void setDtRegUnitaDocA(Date dtRegUnitaDocA) {
        this.dtRegUnitaDocA = dtRegUnitaDocA;
    }

    /**
     * @return the dlDoc
     */
    public String getDlDoc() {
        return dlDoc;
    }

    /**
     * @param dlDoc
     *            the dlDoc to set
     */
    public void setDlDoc(String dlDoc) {
        this.dlDoc = dlDoc;
    }

    /**
     * @return the dsAutoreDoc
     */
    public String getDsAutoreDoc() {
        return dsAutoreDoc;
    }

    /**
     * @param dsAutoreDoc
     *            the dsAutoreDoc to set
     */
    public void setDsAutoreDoc(String dsAutoreDoc) {
        this.dsAutoreDoc = dsAutoreDoc;
    }

    /**
     * @return the dtCreazioneDa
     */
    public Date getDtCreazioneDa() {
        return dtCreazioneDa;
    }

    /**
     * @param dtCreazioneDa
     *            the dtCreazioneDa to set
     */
    public void setDtCreazioneDa(Date dtCreazioneDa) {
        this.dtCreazioneDa = dtCreazioneDa;
    }

    /**
     * @return the dtCreazioneA
     */
    public Date getDtCreazioneA() {
        return dtCreazioneA;
    }

    /**
     * @param dtCreazioneA
     *            the dtCreazioneA to set
     */
    public void setDtCreazioneA(Date dtCreazioneA) {
        this.dtCreazioneA = dtCreazioneA;
    }

    /**
     * @return the tiConservazione
     */
    public String getTiConservazione() {
        return tiConservazione;
    }

    /**
     * @param tiConservazione
     *            the tiConservazione to set
     */
    public void setTiConservazione(String tiConservazione) {
        this.tiConservazione = tiConservazione;
    }

    /**
     * @return the flUnitaDocFirmato
     */
    public String getFlUnitaDocFirmato() {
        return flUnitaDocFirmato;
    }

    /**
     * @param flUnitaDocFirmato
     *            the flUnitaDocFirmato to set
     */
    public void setFlUnitaDocFirmato(String flUnitaDocFirmato) {
        this.flUnitaDocFirmato = flUnitaDocFirmato;
    }

    /**
     * @return the flForzaAccettazione
     */
    public String getFlForzaAccettazione() {
        return flForzaAccettazione;
    }

    /**
     * @param flForzaAccettazione
     *            the flForzaAccettazione to set
     */
    public void setFlForzaAccettazione(String flForzaAccettazione) {
        this.flForzaAccettazione = flForzaAccettazione;
    }

    /**
     * @return the flForzaConservazione
     */
    public String getFlForzaConservazione() {
        return flForzaConservazione;
    }

    /**
     * @param flForzaConservazione
     *            the flForzaConservazione to set
     */
    public void setFlForzaConservazione(String flForzaConservazione) {
        this.flForzaConservazione = flForzaConservazione;
    }

    /**
     * @return the tiEsitoVerifFirme
     */
    public List<String> getTiEsitoVerifFirme() {
        return tiEsitoVerifFirme;
    }

    /**
     * @param tiEsitoVerifFirme
     *            the tiEsitoVerifFirme to set
     */
    public void setTiEsitoVerifFirme(List<String> tiEsitoVerifFirme) {
        this.tiEsitoVerifFirme = tiEsitoVerifFirme;
    }

    /**
     * @return the nmSistemaMigraz
     */
    public List<String> getNmSistemaMigraz() {
        return nmSistemaMigraz;
    }

    /**
     * @param nmSistemaMigraz
     *            the nmSistemaMigraz to set
     */
    public void setNmSistemaMigraz(List<String> nmSistemaMigraz) {
        this.nmSistemaMigraz = nmSistemaMigraz;
    }

}
