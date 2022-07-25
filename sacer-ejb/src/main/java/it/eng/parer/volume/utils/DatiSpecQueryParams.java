package it.eng.parer.volume.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Agati_D
 */
public final class DatiSpecQueryParams {

    private String tiOper;
    private String dlValore;
    private List<BigDecimal> idAttribDatiSpec;
    private List<String> nmSistemaMigraz;

    public DatiSpecQueryParams(String tiOper, String dlValore) {
        this.tiOper = tiOper;
        this.dlValore = dlValore;
        idAttribDatiSpec = new ArrayList();
        nmSistemaMigraz = new ArrayList();
    }

    public boolean add(BigDecimal e) {
        return idAttribDatiSpec.add(e);
    }

    public boolean addSM(String e) {
        return nmSistemaMigraz.add(e);
    }

    public String getDlValore() {
        return dlValore;
    }

    public void setDlValore(String dlValore) {
        this.dlValore = dlValore;
    }

    public List<BigDecimal> getIdAttribDatiSpec() {
        return idAttribDatiSpec;
    }

    public void setIdAttribDatiSpec(List<BigDecimal> idAttribDatiSpec) {
        this.idAttribDatiSpec = idAttribDatiSpec;
    }

    public List<String> getNmSistemaMigraz() {
        return nmSistemaMigraz;
    }

    public void setNmSistemaMigraz(List<String> nmSistemaMigraz) {
        this.nmSistemaMigraz = nmSistemaMigraz;
    }

    public String getTiOper() {
        return tiOper;
    }

    public void setTiOper(String tiOper) {
        this.tiOper = tiOper;
    }
}