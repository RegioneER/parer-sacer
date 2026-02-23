package it.eng.parer.viewEntity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the ARO_V_RIC_RICH_SCARTO database table.
 *
 */
@Entity
@Table(name = "ARO_V_RIC_RICH_SCARTO")
@NamedQuery(name = "AroVRicRichScarto.findAll", query = "SELECT a FROM AroVRicRichScarto a")
public class AroVRicRichScarto implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String cdRichScartoVers;
    private String dsRichScartoVers;
    private Date dtCreazioneRichScartoVers;
    private String flNonScartabile;
    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idItemRichScartoVers;
    private BigDecimal idStrut;
    private BigDecimal niItem;
    private BigDecimal niItemNonScartati;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String ntRichScartoVers;
    private String tiCreazioneRichScartoVers;
    private String tiStatoRichScartoVersCor;

    public AroVRicRichScarto() {
        /* Hibernate */
    }

    public AroVRicRichScarto(String cdRichScartoVers, String dsRichScartoVers,
            Date dtCreazioneRichScartoVers, String flNonScartabile, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idRichScartoVers, BigDecimal idStrut,
            BigDecimal idUserIam, BigDecimal niItem, BigDecimal niItemNonScartati,
            String nmAmbiente, String nmEnte, String nmStrut, String ntRichScartoVers,
            String tiCreazioneRichScartoVers, String tiStatoRichScartoVersCor) {
        this.cdRichScartoVers = cdRichScartoVers;
        this.dsRichScartoVers = dsRichScartoVers;
        this.dtCreazioneRichScartoVers = dtCreazioneRichScartoVers;
        this.flNonScartabile = flNonScartabile;
        this.idAmbiente = idAmbiente;
        this.idEnte = idEnte;
        this.aroVRicRichScartoId = new AroVRicRichScartoId();
        this.aroVRicRichScartoId.setIdRichScartoVers(idRichScartoVers);
        this.aroVRicRichScartoId.setIdUserIam(idUserIam);
        this.idStrut = idStrut;
        this.niItem = niItem;
        this.niItemNonScartati = niItemNonScartati;
        this.nmAmbiente = nmAmbiente;
        this.nmEnte = nmEnte;
        this.nmStrut = nmStrut;
        this.ntRichScartoVers = ntRichScartoVers;
        this.tiCreazioneRichScartoVers = tiCreazioneRichScartoVers;
        this.tiStatoRichScartoVersCor = tiStatoRichScartoVersCor;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "CD_KEY_UNITA_DOC")
    public String getCdKeyUnitaDoc() {
        return this.cdKeyUnitaDoc;
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    public String getCdRegistroKeyUnitaDoc() {
        return this.cdRegistroKeyUnitaDoc;
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    @Column(name = "CD_RICH_SCARTO_VERS")
    public String getCdRichScartoVers() {
        return this.cdRichScartoVers;
    }

    public void setCdRichScartoVers(String cdRichScartoVers) {
        this.cdRichScartoVers = cdRichScartoVers;
    }

    @Column(name = "DS_RICH_SCARTO_VERS")
    public String getDsRichScartoVers() {
        return this.dsRichScartoVers;
    }

    public void setDsRichScartoVers(String dsRichScartoVers) {
        this.dsRichScartoVers = dsRichScartoVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_RICH_SCARTO_VERS")
    public Date getDtCreazioneRichScartoVers() {
        return this.dtCreazioneRichScartoVers;
    }

    @Column(name = "FL_NON_SCARTABILE", columnDefinition = "char(1)")
    public String getFlNonScartabile() {
        return this.flNonScartabile;
    }

    public void setFlNonScartabile(String flNonScartabile) {
        this.flNonScartabile = flNonScartabile;
    }

    public void setDtCreazioneRichScartoVers(Date dtCreazioneRichScartoVers) {
        this.dtCreazioneRichScartoVers = dtCreazioneRichScartoVers;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    @Column(name = "ID_ITEM_RICH_SCARTO_VERS")
    public BigDecimal getIdItemRichScartoVers() {
        return this.idItemRichScartoVers;
    }

    public void setIdItemRichScartoVers(BigDecimal idItemRichScartoVers) {
        this.idItemRichScartoVers = idItemRichScartoVers;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "NI_ITEM")
    public BigDecimal getNiItem() {
        return this.niItem;
    }

    public void setNiItem(BigDecimal niItem) {
        this.niItem = niItem;
    }

    @Column(name = "NI_ITEM_NON_SCARTATI")
    public BigDecimal getNiItemNonScartati() {
        return this.niItemNonScartati;
    }

    public void setNiItemNonScartati(BigDecimal niItemNonScartati) {
        this.niItemNonScartati = niItemNonScartati;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
        return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "NT_RICH_SCARTO_VERS")
    public String getNtRichScartoVers() {
        return this.ntRichScartoVers;
    }

    public void setNtRichScartoVers(String ntRichScartoVers) {
        this.ntRichScartoVers = ntRichScartoVers;
    }

    @Column(name = "TI_CREAZIONE_RICH_SCARTO_VERS")
    public String getTiCreazioneRichScartoVers() {
        return this.tiCreazioneRichScartoVers;
    }

    public void setTiCreazioneRichScartoVers(String tiCreazioneRichScartoVers) {
        this.tiCreazioneRichScartoVers = tiCreazioneRichScartoVers;
    }

    @Column(name = "TI_STATO_RICH_SCARTO_VERS_COR")
    public String getTiStatoRichScartoVersCor() {
        return this.tiStatoRichScartoVersCor;
    }

    public void setTiStatoRichScartoVersCor(String tiStatoRichScartoVersCor) {
        this.tiStatoRichScartoVersCor = tiStatoRichScartoVersCor;
    }

    private AroVRicRichScartoId aroVRicRichScartoId;

    @EmbeddedId()
    public AroVRicRichScartoId getAroVRicRichScartoId() {
        return aroVRicRichScartoId;
    }

    public void setAroVRicRichScartoId(AroVRicRichScartoId aroVRicRichScartoId) {
        this.aroVRicRichScartoId = aroVRicRichScartoId;
    }

}