/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.elencoVersamento.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.DecCriterioFiltroMultiplo;
import it.eng.parer.entity.DecCriterioRaggr;

/**
 *
 * @author DiLorenzo_F
 */
public class FiltroMultiploDocValidator
        implements ConstraintValidator<ValidateFiltroMultiploDoc, CriterioFiltroDoc> {

    @Override
    public void initialize(ValidateFiltroMultiploDoc constraintAnnotation) {
    }

    @Override
    public boolean isValid(CriterioFiltroDoc critFiltroMultDoc,
            ConstraintValidatorContext context) {

        DecCriterioRaggr criterio = critFiltroMultDoc.getCriterioRaggr();
        List<AroDoc> docs = critFiltroMultDoc.getDocs();
        List<DecCriterioFiltroMultiplo> critFiltroMultiploList = criterio
                .getDecCriterioFiltroMultiplos();

        // Raggruppo i filtri multipli sui Documenti che sono attivi sul criterio corrente

        // presenza filtro su tipo documento
        List<DecCriterioFiltroMultiplo> filtriDocList = (List<DecCriterioFiltroMultiplo>) CollectionUtils
                .select(critFiltroMultiploList, new Predicate() {
                    @Override
                    public boolean evaluate(final Object object) {
                        return ("1".equals(criterio.getFlFiltroTipoDoc()) && "TIPO_DOC".equals(
                                ((DecCriterioFiltroMultiplo) object).getTiFiltroMultiplo()));
                    }
                });

        // Controllo il criterio corrente in base ai filtri multipli attivi sui Documenti
        for (DecCriterioFiltroMultiplo filtroDoc : filtriDocList) {
            if ("TIPO_DOC".equals(filtroDoc.getTiFiltroMultiplo())) {
                for (AroDoc doc : docs) {
                    if (filtroDoc.getDecTipoDoc().getIdTipoDoc()
                            .compareTo(doc.getDecTipoDoc().getIdTipoDoc()) == 0) {
                        return true;
                    }
                }
                return false;
            }
        }

        return true;
    }
}
