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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.DecCriterioRaggr;

/**
 *
 * @author DiLorenzo_F
 */
@ValidateAaKeyUnitaDoc
@ValidateDtCreazione
public class CriterioRaggrValidation<T> {

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

    public CriterioRaggrValidation(DecCriterioRaggr criterioRaggr, T udDocUpd,
	    BigDecimal aaKeyUnitaDoc, Date dtCreazione) {
	this.criterioRaggr = criterioRaggr;
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
	this.dtCreazione = dtCreazione;

	if (udDocUpd instanceof AroUnitaDoc) {
	    this.criterioFiltroUnitaDoc = new CriterioFiltroUnitaDoc(criterioRaggr,
		    (AroUnitaDoc) udDocUpd);
	    this.criterioFiltroDoc = new CriterioFiltroDoc(criterioRaggr,
		    ((AroUnitaDoc) udDocUpd).getAroDocs());
	} else if (udDocUpd instanceof AroDoc) {
	    this.criterioFiltroUnitaDoc = new CriterioFiltroUnitaDoc(criterioRaggr,
		    ((AroDoc) udDocUpd).getAroUnitaDoc());
	    this.criterioFiltroDoc = new CriterioFiltroDoc(criterioRaggr,
		    Arrays.asList(((AroDoc) udDocUpd)));
	} else if (udDocUpd instanceof AroUpdUnitaDoc) {
	    this.criterioFiltroUnitaDoc = new CriterioFiltroUnitaDoc(criterioRaggr,
		    ((AroUpdUnitaDoc) udDocUpd).getAroUnitaDoc());
	    this.criterioFiltroDoc = new CriterioFiltroDoc(criterioRaggr,
		    ((AroUpdUnitaDoc) udDocUpd).getAroUnitaDoc().getAroDocs());
	}
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
