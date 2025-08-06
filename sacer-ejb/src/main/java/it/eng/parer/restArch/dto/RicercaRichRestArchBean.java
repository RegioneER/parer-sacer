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

package it.eng.parer.restArch.dto;

import java.math.BigDecimal;
import java.util.List;

import it.eng.parer.slite.gen.form.RestituzioneArchivioForm;
import it.eng.spagoCore.error.EMFError;

/**
 *
 * @author DiLorenzo_F
 */
public class RicercaRichRestArchBean {

    private BigDecimal id_ambiente;
    private BigDecimal id_ente;
    private BigDecimal id_strut;
    private List<String> ti_stato_rich_rest_arch_cor;
    private String ti_rich_rest_arch;

    public RicercaRichRestArchBean(RestituzioneArchivioForm.FiltriRicercaRichRestArch filtri)
	    throws EMFError {
	this.id_ambiente = filtri.getId_ambiente().parse();
	this.id_ente = filtri.getId_ente().parse();
	this.id_strut = filtri.getId_strut().parse();
	this.ti_stato_rich_rest_arch_cor = filtri.getTi_stato_rich_rest_arch_cor().parse();
	this.ti_rich_rest_arch = filtri.getTi_rich_rest_arch().parse();
    }

    public RicercaRichRestArchBean() {

    }

    public String getTi_rich_rest_arch() {
	return ti_rich_rest_arch;
    }

    public void setTi_rich_rest_arch(String ti_rich_rest_arch) {
	this.ti_rich_rest_arch = ti_rich_rest_arch;
    }

    public BigDecimal getId_ambiente() {
	return id_ambiente;
    }

    public void setId_ambiente(BigDecimal id_ambiente) {
	this.id_ambiente = id_ambiente;
    }

    public BigDecimal getId_ente() {
	return id_ente;
    }

    public void setId_ente(BigDecimal id_ente) {
	this.id_ente = id_ente;
    }

    public BigDecimal getId_strut() {
	return id_strut;
    }

    public void setId_strut(BigDecimal id_strut) {
	this.id_strut = id_strut;
    }

    public List<String> getTi_stato_rich_rest_arch_cor() {
	return ti_stato_rich_rest_arch_cor;
    }

    public void setTi_stato_rich_rest_arch_cor(List<String> ti_stato_rich_rest_arch_cor) {
	this.ti_stato_rich_rest_arch_cor = ti_stato_rich_rest_arch_cor;
    }

}
