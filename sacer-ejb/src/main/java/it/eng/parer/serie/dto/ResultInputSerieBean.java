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

package it.eng.parer.serie.dto;

import java.math.BigDecimal;
import java.util.List;

import it.eng.parer.viewEntity.ResultVCalcoloSerieUd;

/**
 *
 * @author Bonora_L
 */
public class ResultInputSerieBean {

    String record;
    BigDecimal indexRecord;
    List<ResultVCalcoloSerieUd> resultQueryList;

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public List<ResultVCalcoloSerieUd> getResultQueryList() {
        return resultQueryList;
    }

    public void setResultQueryList(List<ResultVCalcoloSerieUd> resultQueryList) {
        this.resultQueryList = resultQueryList;
    }

    public BigDecimal getIndexRecord() {
        return indexRecord;
    }

    public void setIndexRecord(BigDecimal indexRecord) {
        this.indexRecord = indexRecord;
    }

}
