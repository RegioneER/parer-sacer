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

public class ConteggioStatoUdDto {
    private Long idUdDelRichiesta;
    private BigDecimal idRichiesta;
    private String tiMotCancellazione;
    private BigDecimal idEnte;
    private String nmEnte;
    private BigDecimal idStrut;
    private String nmStrut;
    private String tiStatoUdCancellate;
    private long conteggio; // COUNT restituisce un Long in JPA

    public ConteggioStatoUdDto(Long idUdDelRichiesta, BigDecimal idRichiesta,
            String tiMotCancellazione, BigDecimal idEnte, String nmEnte, BigDecimal idStrut,
            String nmStrut, String tiStatoUdCancellate, long conteggio) {
        this.idUdDelRichiesta = idUdDelRichiesta;
        this.idRichiesta = idRichiesta;
        this.tiMotCancellazione = tiMotCancellazione;
        this.idEnte = idEnte;
        this.nmEnte = nmEnte;
        this.idStrut = idStrut;
        this.nmStrut = nmStrut;
        this.tiStatoUdCancellate = tiStatoUdCancellate;
        this.conteggio = conteggio;
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

    public String getTiMotCancellazione() {
        return tiMotCancellazione;
    }

    public void setTiMotCancellazione(String tiMotCancellazione) {
        this.tiMotCancellazione = tiMotCancellazione;
    }

    public BigDecimal getIdStrut() {
        return idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    public String getTiStatoUdCancellate() {
        return tiStatoUdCancellate;
    }

    public void setTiStatoUdCancellate(String tiStatoUdCancellate) {
        this.tiStatoUdCancellate = tiStatoUdCancellate;
    }

    public long getConteggio() {
        return conteggio;
    }

    public void setConteggio(long conteggio) {
        this.conteggio = conteggio;
    }

    public BigDecimal getIdEnte() {
        return idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    public String getNmEnte() {
        return nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    public String getNmStrut() {
        return nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

}