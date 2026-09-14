/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import it.eng.parer.entity.constraint.AroAipRestituzioneArchivio.AroAipRaTipologiaOggetto;
import it.eng.parer.entity.constraint.AroAipRestituzioneArchivio.TiStatoAroAipRa;

/**
 * The persistent class for the ARO_AIP_RESTITUZIONE_ARCHIVIO database table.
 *
 * <p>
 * Rappresenta una richiesta di restituzione (estrazione) di un pacchetto AIP dall'archivio, con il
 * relativo stato di elaborazione, la tipologia di oggetto restituito e gli eventuali dati di
 * errore.
 * </p>
 */
@Entity
@Table(name = "ARO_AIP_RESTITUZIONE_ARCHIVIO")
public class AroAipRestituzioneArchivio implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idAipRestArchivio;

    private AroIndiceAipUd aroIndiceAipUd;

    private AroRichiestaRa aroRichiestaRa;

    private TiStatoAroAipRa tiStato;

    private AroAipRaTipologiaOggetto tiTipologiaOggetto;

    private Date dtEstrazione;

    private BigDecimal dim;

    private String cdErrore;

    private String flAipControllato;

    public AroAipRestituzioneArchivio() {/* Hibernate */
    }

    /**
     * Identificativo univoco della richiesta di restituzione dell'AIP dall'archivio (chiave
     * primaria).
     *
     * @return id restituzione AIP archivio
     */
    @Id
    @Column(name = "ID_AIP_REST_ARCHIVIO")
    @GenericGenerator(name = "SARO_AIP_RESTITUZIONE_ARCHIVIO_ID_AIP_REST_ARCHIVIO_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SARO_AIP_RESTITUZIONE_ARCHIVIO"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SARO_AIP_RESTITUZIONE_ARCHIVIO_ID_AIP_REST_ARCHIVIO_GENERATOR")
    public Long getIdAipRestArchivio() {
        return this.idAipRestArchivio;
    }

    public void setIdAipRestArchivio(Long idAipRestArchivio) {
        this.idAipRestArchivio = idAipRestArchivio;
    }

    /**
     * Data e ora in cui il pacchetto AIP e' stato estratto dall'archivio.
     *
     * @return data estrazione
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ESTRAZIONE")
    public Date getDtEstrazione() {
        return this.dtEstrazione;
    }

    public void setDtEstrazione(Date dtEstrazione) {
        this.dtEstrazione = dtEstrazione;
    }

    /**
     * Dimensione, in byte, del pacchetto AIP restituito.
     *
     * @return dimensione AIP
     */
    @Column(name = "DIM")
    public BigDecimal getDim() {
        return this.dim;
    }

    public void setDim(BigDecimal dim) {
        this.dim = dim;
    }

    /**
     * Stato di elaborazione della richiesta di restituzione dell'AIP (es. DA_ELABORARE, ESTRATTO,
     * ERRORE).
     *
     * @return stato elaborazione
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TI_STATO")
    public TiStatoAroAipRa getTiStato() {
        return this.tiStato;
    }

    public void setTiStato(TiStatoAroAipRa tiStato) {
        this.tiStato = tiStato;
    }

    /**
     * Tipologia dell'oggetto (fascicolo, oggetto documentale, serie o unita' documentaria) a cui si
     * riferisce il pacchetto AIP restituito.
     *
     * @return tipologia oggetto
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TI_TIPOLOGIA_OGGETTO")
    public AroAipRaTipologiaOggetto getTiTipologiaOggetto() {
        return this.tiTipologiaOggetto;
    }

    public void setTiTipologiaOggetto(AroAipRaTipologiaOggetto tiTipologiaOggetto) {
        this.tiTipologiaOggetto = tiTipologiaOggetto;
    }

    /**
     * Codice dell'eventuale errore riscontrato durante l'estrazione/restituzione del pacchetto AIP.
     *
     * @return codice errore
     */
    @Column(name = "CD_ERRORE")
    public String getCdErrore() {
        return this.cdErrore;
    }

    public void setCdErrore(String cdErrore) {
        this.cdErrore = cdErrore;
    }

    /**
     * Flag che indica se il pacchetto AIP e' gia' stato sottoposto con successo al controllo a
     * campione (esistenza dell'indice AIP, validita' XSD, presenza di tutti i file referenziati).
     *
     * @return flag AIP controllato
     */
    @Column(name = "FL_AIP_CONTROLLATO", columnDefinition = "char(1)")
    public String getFlAipControllato() {
        return this.flAipControllato;
    }

    public void setFlAipControllato(String flAipControllato) {
        this.flAipControllato = flAipControllato;
    }

    /**
     * Richiesta di restituzione dell'AIP (ARO_RICHIESTA_RA) a cui appartiene questa estrazione.
     *
     * @return richiesta RA
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RICHIESTA_RA")
    public AroRichiestaRa getAroRichiestaRa() {
        return this.aroRichiestaRa;
    }

    public void setAroRichiestaRa(AroRichiestaRa aroRichiestaRa) {
        this.aroRichiestaRa = aroRichiestaRa;
    }

    /**
     * Indice AIP (ARO_INDICE_AIP_UD) del pacchetto a cui si riferisce la restituzione
     * dall'archivio.
     *
     * @return indice AIP UD
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_INDICE_AIP")
    public AroIndiceAipUd getAroIndiceAipUd() {
        return this.aroIndiceAipUd;
    }

    public void setAroIndiceAipUd(AroIndiceAipUd aroIndiceAipUd) {
        this.aroIndiceAipUd = aroIndiceAipUd;
    }
}
