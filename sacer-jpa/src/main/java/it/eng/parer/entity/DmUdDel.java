package it.eng.parer.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the DM_UD_DEL database table.
 *
 */
@Entity
@Table(name = "DM_UD_DEL")
@NamedQuery(name = "DmUdDel.findAll", query = "SELECT d FROM DmUdDel d")
public class DmUdDel implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idUnitaDoc;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private Date dtStatoUdCancellate;
    private Date dtVersamento;
    private BigDecimal idEnte;
    private BigDecimal idStrut;
    private String nmEnte;
    private String nmStrut;
    private String tiStatoUdCancellate;
    private DmUdDelRichieste dmUdDelRichieste;

    public DmUdDel() {
	/* Hibernate */
    }

    @Id
    @Column(name = "ID_UNITA_DOC")
    public Long getIdUnitaDoc() {
	return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(Long idUnitaDoc) {
	this.idUnitaDoc = idUnitaDoc;
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

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_STATO_UD_CANCELLATE")
    public Date getDtStatoUdCancellate() {
	return this.dtStatoUdCancellate;
    }

    public void setDtStatoUdCancellate(Date dtStatoUdCancellate) {
	this.dtStatoUdCancellate = dtStatoUdCancellate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_VERSAMENTO")
    public Date getDtVersamento() {
	return this.dtVersamento;
    }

    public void setDtVersamento(Date dtVersamento) {
	this.dtVersamento = dtVersamento;
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

    @Column(name = "TI_STATO_UD_CANCELLATE")
    public String getTiStatoUdCancellate() {
	return this.tiStatoUdCancellate;
    }

    public void setTiStatoUdCancellate(String tiStatoUdCancellate) {
	this.tiStatoUdCancellate = tiStatoUdCancellate;
    }

    // bi-directional many-to-one association to DmUdDelRichieste
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UD_DEL_RICHIESTA")
    public DmUdDelRichieste getDmUdDelRichieste() {
	return this.dmUdDelRichieste;
    }

    public void setDmUdDelRichieste(DmUdDelRichieste dmUdDelRichieste) {
	this.dmUdDelRichieste = dmUdDelRichieste;
    }

}