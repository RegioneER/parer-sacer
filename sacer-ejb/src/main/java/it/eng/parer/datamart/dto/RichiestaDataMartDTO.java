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

package it.eng.parer.datamart.dto;

import java.math.BigDecimal;
import java.util.Date;

public class RichiestaDataMartDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private Long idUdDelRichiesta;
    private BigDecimal idRichiesta;
    private String cdRichiesta;
    private String tiMotCancellazione;
    private String dsMotCancellazione; // Campo calcolato dal CASE
    private Date dtCreazione;
    private String tiStatoRichiesta;
    private Long totalUnitaDocumentarie; // Campo calcolato dal COUNT

    // È FONDAMENTALE avere un costruttore con tutti i campi nello stesso ordine della SELECT
    public RichiestaDataMartDTO(Long idUdDelRichiesta, BigDecimal idRichiesta, String cdRichiesta,
            String tiMotCancellazione, String dsMotCancellazione, Date dtCreazione,
            String tiStatoRichiesta, Long totalUnitaDocumentarie) {
        this.idUdDelRichiesta = idUdDelRichiesta;
        this.idRichiesta = idRichiesta;
        this.cdRichiesta = cdRichiesta;
        this.tiMotCancellazione = tiMotCancellazione;
        this.dsMotCancellazione = dsMotCancellazione;
        this.dtCreazione = dtCreazione;
        this.tiStatoRichiesta = tiStatoRichiesta;
        this.totalUnitaDocumentarie = totalUnitaDocumentarie;
    }

    public Long getIdUdDelRichiesta() {
        return idUdDelRichiesta;
    }

    public void setIdUdDelRichiesta(Long idUdDelRichiesta) {
        this.idUdDelRichiesta = idUdDelRichiesta;
    }

    public BigDecimal getIdRichiesta() {
        return idRichiesta;
    }

    public void setIdRichiesta(BigDecimal idRichiesta) {
        this.idRichiesta = idRichiesta;
    }

    public String getCdRichiesta() {
        return cdRichiesta;
    }

    public void setCdRichiesta(String cdRichiesta) {
        this.cdRichiesta = cdRichiesta;
    }

    public String getTiMotCancellazione() {
        return tiMotCancellazione;
    }

    public void setTiMotCancellazione(String tiMotCancellazione) {
        this.tiMotCancellazione = tiMotCancellazione;
    }

    public String getDsMotCancellazione() {
        return dsMotCancellazione;
    }

    public void setDsMotCancellazione(String dsMotCancellazione) {
        this.dsMotCancellazione = dsMotCancellazione;
    }

    public Date getDtCreazione() {
        return dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    public String getTiStatoRichiesta() {
        return tiStatoRichiesta;
    }

    public void setTiStatoRichiesta(String tiStatoRichiesta) {
        this.tiStatoRichiesta = tiStatoRichiesta;
    }

    public Long getTotalUnitaDocumentarie() {
        return totalUnitaDocumentarie;
    }

    public void setTotalUnitaDocumentarie(Long totalUnitaDocumentarie) {
        this.totalUnitaDocumentarie = totalUnitaDocumentarie;
    }

}
