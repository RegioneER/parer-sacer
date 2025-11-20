package it.eng.parer.viewEntity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;

/**
 * The persistent class for the ARO_V_CHK_STATO_COR_RICH_SOFT_DELETE database table.
 *
 */
@Entity
@Table(name = "ARO_V_CHK_STATO_COR_RICH_SOFT_DELETE")
@NamedQuery(name = "AroVChkStatoCorRichSoftDelete.findAll", query = "SELECT a FROM AroVChkStatoCorRichSoftDelete a")
public class AroVChkStatoCorRichSoftDelete implements Serializable {
    private static final long serialVersionUID = 1L;
    private String flRichAcquisizioneKo;
    private String flRichErrore;
    private String flRichEvasaOk;
    private String flRichEvasaKoRecup;
    private String flRichInElaborazione;
    private BigDecimal idRichiestaSacer;
    private String tiItemRichSoftDelete;

    public AroVChkStatoCorRichSoftDelete() {
    }

    @Column(name = "FL_RICH_ACQUISIZIONE_KO", columnDefinition = "char")
    public String getFlRichAcquisizioneKo() {
	return this.flRichAcquisizioneKo;
    }

    public void setFlRichAcquisizioneKo(String flRichAcquisizioneKo) {
	this.flRichAcquisizioneKo = flRichAcquisizioneKo;
    }

    @Column(name = "FL_RICH_ERRORE", columnDefinition = "char")
    public String getFlRichErrore() {
	return this.flRichErrore;
    }

    public void setFlRichErrore(String flRichErrore) {
	this.flRichErrore = flRichErrore;
    }

    @Column(name = "FL_RICH_EVASA_OK", columnDefinition = "char")
    public String getFlRichEvasaOk() {
	return this.flRichEvasaOk;
    }

    public void setFlRichEvasaOk(String flRichEvasaOk) {
	this.flRichEvasaOk = flRichEvasaOk;
    }

    @Column(name = "FL_RICH_EVASA_KO_RECUP", columnDefinition = "char")
    public String getFlRichEvasaKoRecup() {
	return this.flRichEvasaKoRecup;
    }

    public void setFlRichEvasaKoRecup(String flRichEvasaKoRecup) {
	this.flRichEvasaKoRecup = flRichEvasaKoRecup;
    }

    @Column(name = "FL_RICH_IN_ELABORAZIONE", columnDefinition = "char")
    public String getFlRichInElaborazione() {
	return this.flRichInElaborazione;
    }

    public void setFlRichInElaborazione(String flRichInElaborazione) {
	this.flRichInElaborazione = flRichInElaborazione;
    }

    @Id
    @Column(name = "ID_RICHIESTA_SACER")
    public BigDecimal getIdRichiestaSacer() {
	return this.idRichiestaSacer;
    }

    public void setIdRichiestaSacer(BigDecimal idRichiestaSacer) {
	this.idRichiestaSacer = idRichiestaSacer;
    }

    @Column(name = "TI_ITEM_RICH_SOFT_DELETE")
    public String getTiItemRichSoftDelete() {
	return this.tiItemRichSoftDelete;
    }

    public void setTiItemRichSoftDelete(String tiItemRichSoftDelete) {
	this.tiItemRichSoftDelete = tiItemRichSoftDelete;
    }

}