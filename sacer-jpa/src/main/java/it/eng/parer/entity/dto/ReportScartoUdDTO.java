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

package it.eng.parer.entity.dto;

import it.eng.spagoLite.db.base.table.BaseTable;
import java.util.HashMap;
import java.util.Map;

public class ReportScartoUdDTO {

    private BaseTable tableBean;
    private int totaleUd;
    private String anniRiferimento;
    private Map<String, Long> totaliPerColonna;

    // Costruttore
    public ReportScartoUdDTO() {
        this.tableBean = new BaseTable();
        this.totaleUd = 0;
        this.anniRiferimento = "";
        this.totaliPerColonna = new HashMap();
    }

    // Getters and Setters
    public BaseTable getTableBean() {
        return tableBean;
    }

    public void setTableBean(BaseTable tableBean) {
        this.tableBean = tableBean;
    }

    public int getTotaleUd() {
        return totaleUd;
    }

    public void setTotaleUd(int totaleUd) {
        this.totaleUd = totaleUd;
    }

    public String getAnniRiferimento() {
        return anniRiferimento;
    }

    public void setAnniRiferimento(String anniRiferimento) {
        this.anniRiferimento = anniRiferimento;
    }

    public Map<String, Long> getTotaliPerColonna() {
        return totaliPerColonna;
    }

    public void setTotaliPerColonna(Map<String, Long> totaliPerColonna) {
        this.totaliPerColonna = totaliPerColonna;
    }

}
