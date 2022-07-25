/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.elencoVersamento.validation;

import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.DecCriterioRaggr;
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 *
 * @author DiLorenzo_F
 */
@ValidateFieldsDoc
@ValidateFiltroMultiploDoc
public class CriterioFiltroDoc {

    @NotNull
    private DecCriterioRaggr criterioRaggr;

    @NotNull
    private List<AroDoc> docs;

    public CriterioFiltroDoc(DecCriterioRaggr criterioRaggr, List<AroDoc> docs) {
        this.criterioRaggr = criterioRaggr;
        this.docs = docs;
    }

    public DecCriterioRaggr getCriterioRaggr() {
        return criterioRaggr;
    }

    public void setCriterioRaggr(DecCriterioRaggr criterioRaggr) {
        this.criterioRaggr = criterioRaggr;
    }

    public List<AroDoc> getDocs() {
        return docs;
    }

    public void setDocs(List<AroDoc> docs) {
        this.docs = docs;
    }

}
