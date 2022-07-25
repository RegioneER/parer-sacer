/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.elencoVersamento.validation;

import com.beust.jcommander.internal.Lists;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.DecCriterioRaggr;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 *
 * @author DiLorenzo_F
 */
@ValidateAaKeyUnitaDoc
@ValidateDtCreazione
public class CriterioRaggrValidation {

    @NotNull
    private DecCriterioRaggr criterioRaggr;

    @NotNull
    BigDecimal aaKeyUnitaDoc;

    @NotNull
    Date dtCreazione;

    @Valid
    @NotNull
    private CriterioFiltroUnitaDoc criterioFiltroUnitaDoc;

    @Valid
    @NotNull
    private CriterioFiltroDoc criterioFiltroDoc;

    public CriterioRaggrValidation(DecCriterioRaggr criterioRaggr, AroUnitaDoc unitaDoc, BigDecimal aaKeyUnitaDoc,
            Date dtCreazione) {
        this.criterioRaggr = criterioRaggr;
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
        this.dtCreazione = dtCreazione;
        this.criterioFiltroUnitaDoc = new CriterioFiltroUnitaDoc(criterioRaggr, unitaDoc);
        this.criterioFiltroDoc = new CriterioFiltroDoc(criterioRaggr, unitaDoc.getAroDocs());
    }

    public CriterioRaggrValidation(DecCriterioRaggr criterioRaggr, AroDoc docAgg, BigDecimal aaKeyUnitaDoc,
            Date dtCreazione) {
        this.criterioRaggr = criterioRaggr;
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
        this.dtCreazione = dtCreazione;
        this.criterioFiltroUnitaDoc = new CriterioFiltroUnitaDoc(criterioRaggr, docAgg.getAroUnitaDoc());
        this.criterioFiltroDoc = new CriterioFiltroDoc(criterioRaggr, Lists.newArrayList(docAgg));
    }

    public CriterioRaggrValidation(DecCriterioRaggr criterioRaggr, AroUpdUnitaDoc aggMtd, BigDecimal aaKeyUnitaDoc,
            Date dtCreazione) {
        this.criterioRaggr = criterioRaggr;
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
        this.dtCreazione = dtCreazione;
        this.criterioFiltroUnitaDoc = new CriterioFiltroUnitaDoc(criterioRaggr, aggMtd.getAroUnitaDoc());
        this.criterioFiltroDoc = new CriterioFiltroDoc(criterioRaggr, aggMtd.getAroUnitaDoc().getAroDocs());
    }

    public DecCriterioRaggr getCriterioRaggr() {
        return criterioRaggr;
    }

    public void setCriterioRaggr(DecCriterioRaggr criterioRaggr) {
        this.criterioRaggr = criterioRaggr;
    }

    public BigDecimal getAaKeyUnitaDoc() {
        return aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    public Date getDtCreazione() {
        return dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    public CriterioFiltroUnitaDoc getCriterioFiltroUnitaDoc() {
        return criterioFiltroUnitaDoc;
    }

    public void setCriterioFiltroUnitaDoc(CriterioFiltroUnitaDoc criterioFiltroUnitaDoc) {
        this.criterioFiltroUnitaDoc = criterioFiltroUnitaDoc;
    }

    public CriterioFiltroDoc getCriterioFiltroDoc() {
        return criterioFiltroDoc;
    }

    public void setCriterioFiltroDoc(CriterioFiltroDoc criterioFiltroDoc) {
        this.criterioFiltroDoc = criterioFiltroDoc;
    }
}
