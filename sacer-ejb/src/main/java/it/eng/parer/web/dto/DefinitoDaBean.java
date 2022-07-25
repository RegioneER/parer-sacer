package it.eng.parer.web.dto;

import java.math.BigDecimal;

/**
 *
 * @author Gilioli_P
 */
public class DefinitoDaBean {
    private BigDecimal idAttribDatiSpec;
    private String tiEntitaSacer;
    private String nmTipoDoc;
    private String nmTipoUnitaDoc;
    private String nmSistemaMigraz;
    private String nmAttribDatiSpec;
    private String tiOper;
    private String dlValore;

    public BigDecimal getIdAttribDatiSpec() {
        return idAttribDatiSpec;
    }

    public void setIdAttribDatiSpec(BigDecimal idAttribDatiSpec) {
        this.idAttribDatiSpec = idAttribDatiSpec;
    }

    public String getTiEntitaSacer() {
        return tiEntitaSacer;
    }

    public void setTiEntitaSacer(String tiEntitaSacer) {
        this.tiEntitaSacer = tiEntitaSacer;
    }

    public String getNmTipoDoc() {
        return nmTipoDoc;
    }

    public void setNmTipoDoc(String nmTipoDoc) {
        this.nmTipoDoc = nmTipoDoc;
    }

    public String getNmTipoUnitaDoc() {
        return nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    public String getNmSistemaMigraz() {
        return nmSistemaMigraz;
    }

    public void setNmSistemaMigraz(String nmSistemaMigraz) {
        this.nmSistemaMigraz = nmSistemaMigraz;
    }

    public String getNmAttribDatiSpec() {
        return nmAttribDatiSpec;
    }

    public void setNmAttribDatiSpec(String nmAttribDatiSpec) {
        this.nmAttribDatiSpec = nmAttribDatiSpec;
    }

    public String getTiOper() {
        return tiOper;
    }

    public void setTiOper(String tiOper) {
        this.tiOper = tiOper;
    }

    public String getDlValore() {
        return dlValore;
    }

    public void setDlValore(String dlValore) {
        this.dlValore = dlValore;
    }
}
