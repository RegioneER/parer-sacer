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

/**
 * The persistent class for the DM_UD_DEL_STATO_RICHIESTA database table. Storico dei passaggi di
 * stato interno della richiesta di cancellazione. Ogni riga rappresenta una transizione dello stato
 * tecnico, referenziato tramite FK ID_DECOD_STATO_INTERNO verso la tabella di decodifica
 * DM_UD_DEL_DECOD_STATO_INTERNO. Lo stato utente TI_STATO_RICHIESTA è gestito direttamente nella
 * tabella principale DM_UD_DEL_RICHIESTE.
 */
@Entity
@Table(name = "DM_UD_DEL_STATO_RICHIESTA")
public class DmUdDelStatoRichiesta implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idStatoUdDelRichiesta;
    private DmUdDelDecodStatoInterno decodStatoInterno;
    private Date dtRegStato;
    private BigDecimal pgStatoRich;
    private DmUdDelRichieste dmUdDelRichieste;

    public DmUdDelStatoRichiesta() {
        /* Hibernate */
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_STATO_UD_DEL_RICHIESTA")
    public Long getIdStatoUdDelRichiesta() {
        return this.idStatoUdDelRichiesta;
    }

    public void setIdStatoUdDelRichiesta(Long idStatoUdDelRichiesta) {
        this.idStatoUdDelRichiesta = idStatoUdDelRichiesta;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DECOD_STATO_INTERNO", nullable = true)
    public DmUdDelDecodStatoInterno getDecodStatoInterno() {
        return this.decodStatoInterno;
    }

    public void setDecodStatoInterno(DmUdDelDecodStatoInterno decodStatoInterno) {
        this.decodStatoInterno = decodStatoInterno;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_STATO", nullable = false)
    public Date getDtRegStato() {
        return this.dtRegStato;
    }

    public void setDtRegStato(Date dtRegStato) {
        this.dtRegStato = dtRegStato;
    }

    @Column(name = "PG_STATO_RICH", nullable = false, precision = 10, scale = 0)
    public BigDecimal getPgStatoRich() {
        return this.pgStatoRich;
    }

    public void setPgStatoRich(BigDecimal pgStatoRich) {
        this.pgStatoRich = pgStatoRich;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UD_DEL_RICHIESTA", nullable = false)
    public DmUdDelRichieste getDmUdDelRichieste() {
        return this.dmUdDelRichieste;
    }

    public void setDmUdDelRichieste(DmUdDelRichieste dmUdDelRichieste) {
        this.dmUdDelRichieste = dmUdDelRichieste;
    }
}
