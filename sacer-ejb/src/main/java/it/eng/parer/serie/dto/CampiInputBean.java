package it.eng.parer.serie.dto;

import it.eng.parer.ws.utils.CostantiDB;
import java.math.BigDecimal;

/**
 *
 * @author Bonora_L
 */
public class CampiInputBean {

    BigDecimal pgOrdCampo;
    String nmCampo;
    CostantiDB.TipoCampo tipoCampo;
    String tiTransformCampo;
    String vlCampoRecord;
    String vlCampoTransform;

    public BigDecimal getPgOrdCampo() {
        return pgOrdCampo;
    }

    public void setPgOrdCampo(BigDecimal pgOrdCampo) {
        this.pgOrdCampo = pgOrdCampo;
    }

    public String getNmCampo() {
        return nmCampo;
    }

    public void setNmCampo(String nmCampo) {
        this.nmCampo = nmCampo;
    }

    public String getTiTransformCampo() {
        return tiTransformCampo;
    }

    public void setTiTransformCampo(String tiTransformCampo) {
        this.tiTransformCampo = tiTransformCampo;
    }

    public String getVlCampoRecord() {
        return vlCampoRecord;
    }

    public void setVlCampoRecord(String vlCampoRecord) {
        this.vlCampoRecord = vlCampoRecord;
    }

    public String getVlCampoTransform() {
        return vlCampoTransform;
    }

    public void setVlCampoTransform(String vlCampoTransform) {
        this.vlCampoTransform = vlCampoTransform;
    }

    public CostantiDB.TipoCampo getTipoCampo() {
        return tipoCampo;
    }

    public void setTipoCampo(CostantiDB.TipoCampo tipoCampo) {
        this.tipoCampo = tipoCampo;
    }
}
