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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Classe di appoggio per gestire il risultato di preparazione query per la creazione di una serie
 *
 */
public class ResultVCalcoloSerieUd implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal idUnitaDoc;
    private Date dtCreazione;
    private String keyUdSerie;
    private Date dtUdSerie;
    private String infoUdSerie;
    private String dsKeyOrdUdSerie;
    private BigDecimal pgUdSerie;

    public ResultVCalcoloSerieUd() {/* Hibernate */
    }

    private enum cols {
        ID_UNITA_DOC, DT_CREAZIONE, KEY_UD_SERIE, DT_UD_SERIE, INFO_UD_SERIE, DS_KEY_ORD_UD_SERIE,
        PG_UD_SERIE
    }

    public ResultVCalcoloSerieUd(Map<String, Object> rec) {
        setIdUnitaDoc(BigDecimal.class.cast(rec.get(cols.ID_UNITA_DOC.name())));
        setDtCreazione(Date.class.cast(rec.get(cols.DT_CREAZIONE.name())));
        setKeyUdSerie(String.class.cast(rec.get(cols.KEY_UD_SERIE.name())));
        setDtUdSerie(Date.class.cast(rec.get(cols.DT_UD_SERIE.name())));
        setInfoUdSerie(String.class.cast(rec.get(cols.INFO_UD_SERIE.name())));
        setDsKeyOrdUdSerie(String.class.cast(rec.get(cols.DS_KEY_ORD_UD_SERIE.name())));
        setPgUdSerie(BigDecimal.class.cast(rec.get(cols.PG_UD_SERIE.name())));
    }

    public ResultVCalcoloSerieUd(Long idUnitaDoc, Date dtCreazione, String keyUdSerie,
            Date dtUdSerie, String infoUdSerie, String dsKeyOrdUdSerie, BigDecimal pgUdSerie) {
        this.idUnitaDoc = new BigDecimal(idUnitaDoc);
        this.dtCreazione = dtCreazione;
        this.keyUdSerie = keyUdSerie;
        this.dtUdSerie = dtUdSerie;
        this.infoUdSerie = infoUdSerie;
        this.dsKeyOrdUdSerie = dsKeyOrdUdSerie;
        this.pgUdSerie = pgUdSerie;
    }

    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    public Date getDtCreazione() {
        return dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    public String getKeyUdSerie() {
        return keyUdSerie;
    }

    public void setKeyUdSerie(String keyUdSerie) {
        this.keyUdSerie = keyUdSerie;
    }

    public Date getDtUdSerie() {
        return dtUdSerie;
    }

    public void setDtUdSerie(Date dtUdSerie) {
        this.dtUdSerie = dtUdSerie;
    }

    public String getInfoUdSerie() {
        return infoUdSerie;
    }

    public void setInfoUdSerie(String infoUdSerie) {
        this.infoUdSerie = infoUdSerie;
    }

    public String getDsKeyOrdUdSerie() {
        return dsKeyOrdUdSerie;
    }

    public void setDsKeyOrdUdSerie(String dsKeyOrdUdSerie) {
        this.dsKeyOrdUdSerie = dsKeyOrdUdSerie;
    }

    public BigDecimal getPgUdSerie() {
        return this.pgUdSerie;
    }

    public void setPgUdSerie(BigDecimal pgUdSerie) {
        this.pgUdSerie = pgUdSerie;
    }

}
