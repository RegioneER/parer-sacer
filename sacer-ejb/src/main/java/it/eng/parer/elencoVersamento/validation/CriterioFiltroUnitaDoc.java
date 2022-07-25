/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.elencoVersamento.validation;

import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecCriterioRaggr;
import javax.validation.constraints.NotNull;

/**
 *
 * @author DiLorenzo_F
 */
@ValidateFieldsUnitaDoc
@ValidateCdKeyUnitaDoc
@ValidateDtRegUnitaDoc
@ValidateFiltroMultiploUnitaDoc
public class CriterioFiltroUnitaDoc {

    @NotNull
    private DecCriterioRaggr criterioRaggr;

    @NotNull
    private AroUnitaDoc unitaDoc;

    public CriterioFiltroUnitaDoc(DecCriterioRaggr criterioRaggr, AroUnitaDoc unitaDoc) {
        this.criterioRaggr = criterioRaggr;
        this.unitaDoc = unitaDoc;
    }

    public DecCriterioRaggr getCriterioRaggr() {
        return criterioRaggr;
    }

    public void setCriterioRaggr(DecCriterioRaggr criterioRaggr) {
        this.criterioRaggr = criterioRaggr;
    }

    public AroUnitaDoc getUnitaDoc() {
        return unitaDoc;
    }

    public void setUnitaDoc(AroUnitaDoc unitaDoc) {
        this.unitaDoc = unitaDoc;
    }
}
