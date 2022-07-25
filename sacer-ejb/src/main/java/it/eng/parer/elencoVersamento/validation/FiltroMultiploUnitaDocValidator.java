/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.elencoVersamento.validation;

import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecCriterioFiltroMultiplo;
import it.eng.parer.entity.DecCriterioRaggr;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 *
 * @author DiLorenzo_F
 */
public class FiltroMultiploUnitaDocValidator
        implements ConstraintValidator<ValidateFiltroMultiploUnitaDoc, CriterioFiltroUnitaDoc> {

    @Override
    public void initialize(ValidateFiltroMultiploUnitaDoc constraintAnnotation) {
    }

    @Override
    public boolean isValid(CriterioFiltroUnitaDoc critFiltroMultUnitaDoc, ConstraintValidatorContext context) {

        DecCriterioRaggr criterio = critFiltroMultUnitaDoc.getCriterioRaggr();
        AroUnitaDoc unitaDoc = critFiltroMultUnitaDoc.getUnitaDoc();
        List<DecCriterioFiltroMultiplo> critFiltroMultiploList = criterio.getDecCriterioFiltroMultiplos();

        // Raggruppo i filtri multipli sull'Unita Documentaria che sono attivi sul criterio corrente

        // presenza filtro sul tipo unita doc
        List<DecCriterioFiltroMultiplo> filtriUnitaDocList = (List<DecCriterioFiltroMultiplo>) CollectionUtils
                .select(critFiltroMultiploList, new Predicate() {
                    @Override
                    public boolean evaluate(final Object object) {
                        return ("1".equals(criterio.getFlFiltroTipoUnitaDoc())
                                && "TIPO_UNI_DOC".equals(((DecCriterioFiltroMultiplo) object).getTiFiltroMultiplo()));
                    }
                });
        // presenza filtro su registro chiave
        CollectionUtils.select(critFiltroMultiploList, new Predicate() {
            @Override
            public boolean evaluate(final Object object) {
                return ("1".equals(criterio.getFlFiltroRegistroKey())
                        && "REGISTRO_UNI_DOC".equals(((DecCriterioFiltroMultiplo) object).getTiFiltroMultiplo()));
            }
        }, filtriUnitaDocList);
        // presenza filtro su range registro chiave
        CollectionUtils.select(critFiltroMultiploList, new Predicate() {
            @Override
            public boolean evaluate(final Object object) {
                return ("1".equals(criterio.getFlFiltroRangeRegistroKey())
                        && "RANGE_REGISTRO_UNI_DOC".equals(((DecCriterioFiltroMultiplo) object).getTiFiltroMultiplo()));
            }
        }, filtriUnitaDocList);
        // presenza filtro su esito verifica firme
        CollectionUtils.select(critFiltroMultiploList, new Predicate() {
            @Override
            public boolean evaluate(final Object object) {
                return ("1".equals(criterio.getFlFiltroTiEsitoVerifFirme())
                        && "TIPO_ESITO_VERIF_FIRME".equals(((DecCriterioFiltroMultiplo) object).getTiFiltroMultiplo()));
            }
        }, filtriUnitaDocList);
        // presenza filtro su sistema di migrazione
        CollectionUtils.select(critFiltroMultiploList, new Predicate() {
            @Override
            public boolean evaluate(final Object object) {
                return ("1".equals(criterio.getFlFiltroSistemaMigraz())
                        && "SISTEMA_MIGRAZ".equals(((DecCriterioFiltroMultiplo) object).getTiFiltroMultiplo()));
            }
        }, filtriUnitaDocList);

        // Controllo il criterio corrente in base ai filtri multipli attivi sull'Unità Documentaria
        for (DecCriterioFiltroMultiplo filtroUnitaDoc : filtriUnitaDocList) {
            switch (filtroUnitaDoc.getTiFiltroMultiplo()) {
            case "TIPO_UNI_DOC":
                if (filtroUnitaDoc.getDecTipoUnitaDoc().getIdTipoUnitaDoc() != unitaDoc.getDecTipoUnitaDoc()
                        .getIdTipoUnitaDoc()) {
                    return false;
                }
                break;
            case "REGISTRO_UNI_DOC":
                if (filtroUnitaDoc.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc() != unitaDoc.getDecRegistroUnitaDoc()
                        .getIdRegistroUnitaDoc()) {
                    return false;
                }
                break;
            case "RANGE_REGISTRO_UNI_DOC":
                if (filtroUnitaDoc.getDecRegistroRangeUnitaDoc().getIdRegistroUnitaDoc() != unitaDoc
                        .getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()) {
                    return false;

                }
                break;
            case "TIPO_ESITO_VERIF_FIRME":
                if (!filtroUnitaDoc.getTiEsitoVerifFirme().equals(unitaDoc.getTiEsitoVerifFirme())) {
                    return false;

                }
                break;
            case "SISTEMA_MIGRAZ":
                if (!filtroUnitaDoc.getNmSistemaMigraz().equals(unitaDoc.getNmSistemaMigraz())) {
                    return false;

                }
                break;
            default:
                break;
            }
        }

        return true;
    }
}
