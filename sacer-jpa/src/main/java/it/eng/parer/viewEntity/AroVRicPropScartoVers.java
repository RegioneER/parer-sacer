package it.eng.parer.viewEntity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the ARO_V_RIC_PROP_SCARTO_VERS database table.
 *
 */
@Entity
@Table(name = "ARO_V_RIC_PROP_SCARTO_VERS")
@NamedQuery(name = "AroVRicPropScartoVers.findAll", query = "SELECT a FROM AroVRicPropScartoVers a")
public class AroVRicPropScartoVers implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdPropScartoVers;
    private String dsPropScartoVers;
    private Date dtCreazionePropScartoVers;
    private Date dtUltimaModPropScartoVers;
    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idStrut;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String ntPropScartoVers;
    private String tiStatoPropScartoVersCor;

    public AroVRicPropScartoVers() {
    }

    @Column(name = "CD_PROP_SCARTO_VERS")
    public String getCdPropScartoVers() {
        return this.cdPropScartoVers;
    }

    public void setCdPropScartoVers(String cdPropScartoVers) {
        this.cdPropScartoVers = cdPropScartoVers;
    }

    @Column(name = "DS_PROP_SCARTO_VERS")
    public String getDsPropScartoVers() {
        return this.dsPropScartoVers;
    }

    public void setDsPropScartoVers(String dsPropScartoVers) {
        this.dsPropScartoVers = dsPropScartoVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_PROP_SCARTO_VERS")
    public Date getDtCreazionePropScartoVers() {
        return this.dtCreazionePropScartoVers;
    }

    public void setDtCreazionePropScartoVers(Date dtCreazionePropScartoVers) {
        this.dtCreazionePropScartoVers = dtCreazionePropScartoVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ULTIMA_MOD_PROP_SCARTO_VERS")
    public Date getDtUltimaModPropScartoVers() {
        return this.dtUltimaModPropScartoVers;
    }

    public void setDtUltimaModPropScartoVers(Date dtUltimaModPropScartoVers) {
        this.dtUltimaModPropScartoVers = dtUltimaModPropScartoVers;
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

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
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

    @Column(name = "NT_PROP_SCARTO_VERS")
    public String getNtPropScartoVers() {
        return this.ntPropScartoVers;
    }

    public void setNtPropScartoVers(String ntPropScartoVers) {
        this.ntPropScartoVers = ntPropScartoVers;
    }

    @Column(name = "TI_STATO_PROP_SCARTO_VERS_COR")
    public String getTiStatoPropScartoVersCor() {
        return this.tiStatoPropScartoVersCor;
    }

    public void setTiStatoPropScartoVersCor(String tiStatoPropScartoVersCor) {
        this.tiStatoPropScartoVersCor = tiStatoPropScartoVersCor;
    }

    private AroVRicPropScartoVersId aroVRicPropScartoVersId;

    @EmbeddedId()
    public AroVRicPropScartoVersId getAroVRicPropScartoVersId() {
        return aroVRicPropScartoVersId;
    }

    public void setAroVRicPropScartoVersId(AroVRicPropScartoVersId aroVRicPropScartoVersId) {
        this.aroVRicPropScartoVersId = aroVRicPropScartoVersId;
    }

}