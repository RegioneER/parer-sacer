/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.elencoVersamento.validation;

import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecCriterioFiltroMultiplo;
import it.eng.parer.entity.DecCriterioRaggr;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author DiLorenzo_F
 */
public class FiltroMultiploDocValidator implements ConstraintValidator<ValidateFiltroMultiploDoc, CriterioFiltroDoc> {

    @Override
    public void initialize(ValidateFiltroMultiploDoc constraintAnnotation) {
    }

    @Override
    public boolean isValid(CriterioFiltroDoc critFiltroMultDoc, ConstraintValidatorContext context) {

        DecCriterioRaggr criterio = critFiltroMultDoc.getCriterioRaggr();
        List<AroDoc> docs = critFiltroMultDoc.getDocs();
        List<DecCriterioFiltroMultiplo> critFiltroMultiploList = criterio.getDecCriterioFiltroMultiplos();

        // Raggruppo i filtri multipli sui Documenti che sono attivi sul criterio corrente

        // presenza filtro su tipo documento
        List<DecCriterioFiltroMultiplo> filtriDocList = (List<DecCriterioFiltroMultiplo>) CollectionUtils
                .select(critFiltroMultiploList, new Predicate() {
                    @Override
                    public boolean evaluate(final Object object) {
                        return ("1".equals(criterio.getFlFiltroTipoDoc())
                                && "TIPO_DOC".equals(((DecCriterioFiltroMultiplo) object).getTiFiltroMultiplo()));
                    }
                });

        // Controllo il criterio corrente in base ai filtri multipli attivi sui Documenti
        for (DecCriterioFiltroMultiplo filtroDoc : filtriDocList) {
            if ("TIPO_DOC".equals(filtroDoc.getTiFiltroMultiplo())) {
                for (AroDoc doc : docs) {
                    if (filtroDoc.getDecTipoDoc().getIdTipoDoc() == doc.getDecTipoDoc().getIdTipoDoc()) {
                        return true;
                    }
                }
                return false;
            }
        }

        return true;
    }
}
