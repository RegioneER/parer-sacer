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