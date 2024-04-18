/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.elencoVersamento.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.DecCriterioRaggr;

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
