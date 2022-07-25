/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.elencoVersamento.validation;

import it.eng.parer.entity.AroDoc;
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
public class FieldsDocValidator implements ConstraintValidator<ValidateFieldsDoc, CriterioFiltroDoc> {

    @Override
    public void initialize(ValidateFieldsDoc constraintAnnotation) {
    }

    @Override
    public boolean isValid(CriterioFiltroDoc critFiltroDoc, ConstraintValidatorContext context) {

        DecCriterioRaggr criterio = critFiltroDoc.getCriterioRaggr();
        List<AroDoc> docs = critFiltroDoc.getDocs();

        boolean ret = true;

        // valida filtro su descrizione doc
        if (criterio.getDlDoc() != null) {
            ret = CollectionUtils.exists(docs, new Predicate() {
                @Override
                public boolean evaluate(final Object object) {
                    return (((AroDoc) object).getDlDoc().contains(criterio.getDlDoc()));
                }
            });
        }

        // valida filtro su autore doc
        if (ret && criterio.getDsAutoreDoc() != null) {
            ret = CollectionUtils.exists(docs, new Predicate() {
                @Override
                public boolean evaluate(final Object object) {
                    return (((AroDoc) object).getDsAutoreDoc().contains(criterio.getDsAutoreDoc()));
                }
            });
        }

        return ret;
    }
}
