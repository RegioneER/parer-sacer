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
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DM_UD_DEL_RICHIESTE database table.
 *
 */
@Entity
@Table(name = "DM_UD_DEL_RICHIESTE")
@NamedQuery(name = "DmUdDelRichieste.findAll", query = "SELECT d FROM DmUdDelRichieste d")
public class DmUdDelRichieste implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idUdDelRichiesta;
    private String cdRichiesta;
    private String dsMessaggioErrore;
    private Date dtCreazione;
    private Date dtUltimoAggiornamento;
    private BigDecimal idRichiesta;
    private String tiModDel;
    private String tiMotCancellazione;
    private String tiStatoInternoRich;
    private String tiStatoRichiesta;
    private List<DmUdDel> dmUdDels;

    public DmUdDelRichieste() {
        /* Hibernate */
    }

    @Id
    @GenericGenerator(name = "DM_UD_DEL_RICHIESTE_ID_DM_UD_DEL_RICHIESTA_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDM_UD_DEL_RICHIESTE"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DM_UD_DEL_RICHIESTE_ID_DM_UD_DEL_RICHIESTA_GENERATOR")
    @Column(name = "ID_UD_DEL_RICHIESTA")
    public Long getIdUdDelRichiesta() {
        return this.idUdDelRichiesta;
    }

    public void setIdUdDelRichiesta(Long idUdDelRichiesta) {
        this.idUdDelRichiesta = idUdDelRichiesta;
    }

    @Column(name = "CD_RICHIESTA")
    public String getCdRichiesta() {
        return this.cdRichiesta;
    }

    public void setCdRichiesta(String cdRichiesta) {
        this.cdRichiesta = cdRichiesta;
    }

    @Column(name = "DS_MESSAGGIO_ERRORE")
    public String getDsMessaggioErrore() {
        return this.dsMessaggioErrore;
    }

    public void setDsMessaggioErrore(String dsMessaggioErrore) {
        this.dsMessaggioErrore = dsMessaggioErrore;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
        return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_ULTIMO_AGGIORNAMENTO")
    public Date getDtUltimoAggiornamento() {
        return this.dtUltimoAggiornamento;
    }

    public void setDtUltimoAggiornamento(Date dtUltimoAggiornamento) {
        this.dtUltimoAggiornamento = dtUltimoAggiornamento;
    }

    @Column(name = "ID_RICHIESTA")
    public BigDecimal getIdRichiesta() {
        return this.idRichiesta;
    }

    public void setIdRichiesta(BigDecimal idRichiesta) {
        this.idRichiesta = idRichiesta;
    }

    @Column(name = "TI_MOD_DEL")
    public String getTiModDel() {
        return this.tiModDel;
    }

    public void setTiModDel(String tiModDel) {
        this.tiModDel = tiModDel;
    }

    @Column(name = "TI_MOT_CANCELLAZIONE")
    public String getTiMotCancellazione() {
        return this.tiMotCancellazione;
    }

    public void setTiMotCancellazione(String tiMotCancellazione) {
        this.tiMotCancellazione = tiMotCancellazione;
    }

    @Column(name = "TI_STATO_INTERNO_RICH")
    public String getTiStatoInternoRich() {
        return this.tiStatoInternoRich;
    }

    public void setTiStatoInternoRich(String tiStatoInternoRich) {
        this.tiStatoInternoRich = tiStatoInternoRich;
    }

    @Column(name = "TI_STATO_RICHIESTA")
    public String getTiStatoRichiesta() {
        return this.tiStatoRichiesta;
    }

    public void setTiStatoRichiesta(String tiStatoRichiesta) {
        this.tiStatoRichiesta = tiStatoRichiesta;
    }

    // bi-directional many-to-one association to DmUdDel
    @OneToMany(mappedBy = "dmUdDelRichieste")
    public List<DmUdDel> getDmUdDels() {
        return this.dmUdDels;
    }

    public void setDmUdDels(List<DmUdDel> dmUdDels) {
        this.dmUdDels = dmUdDels;
    }

}
