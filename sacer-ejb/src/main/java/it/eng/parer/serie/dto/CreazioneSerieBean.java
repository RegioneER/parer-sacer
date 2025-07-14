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

package it.eng.parer.serie.dto;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.form.SerieUDForm;
import it.eng.spagoCore.error.EMFError;

/**
 *
 * @author Bonora_L
 */
public final class CreazioneSerieBean {

    String ti_creazione;
    BigDecimal nm_tipo_serie;
    BigDecimal ni_anni_conserv;
    String cd_serie;
    String ds_serie;
    BigDecimal aa_serie;
    Date dt_inizio_serie;
    Date dt_fine_serie;
    Date dt_reg_unita_doc_da;
    Date dt_reg_unita_doc_a;
    Date dt_creazione_unita_doc_da;
    Date dt_creazione_unita_doc_a;
    String ds_azione;
    String ds_nota_azione;
    String cd_doc_file_input_ver_serie;
    String ds_doc_file_input_ver_serie;
    String fl_fornito_ente;
    String cd_serie_padre;
    String ds_serie_padre;
    String cd_serie_padre_da_creare;
    String ds_serie_padre_da_creare;
    BigDecimal id_serie_padre;
    String ti_periodo_sel_serie;
    BigDecimal ni_periodo_sel_serie;
    String ds_lista_anni_sel_serie;
    String cd_serie_normaliz;

    public CreazioneSerieBean(String ti_creazione, BigDecimal nm_tipo_serie,
	    BigDecimal ni_anni_conserv, String cd_serie, String ds_serie, BigDecimal aa_serie,
	    Date dt_inizio_serie, Date dt_fine_serie, String ds_azione, String ds_nota_azione,
	    String ti_periodo_sel_serie, BigDecimal ni_periodo_sel_serie) {
	this.ti_creazione = ti_creazione;
	this.nm_tipo_serie = nm_tipo_serie;
	this.ni_anni_conserv = ni_anni_conserv;
	this.cd_serie = cd_serie;
	this.ds_serie = ds_serie;
	this.aa_serie = aa_serie;
	setDt_inizio_serie(dt_inizio_serie);
	setDt_fine_serie(dt_fine_serie);
	this.ds_azione = ds_azione;
	this.ds_nota_azione = ds_nota_azione;
	this.ti_periodo_sel_serie = ti_periodo_sel_serie;
	this.ni_periodo_sel_serie = ni_periodo_sel_serie;
    }

    public CreazioneSerieBean(SerieUDForm.CreazioneSerie formFields)
	    throws EMFError, ParerUserError {
	this.ti_creazione = formFields.getTi_creazione().parse();
	this.nm_tipo_serie = formFields.getNm_tipo_serie().parse();
	this.cd_serie = formFields.getCd_serie().parse();
	this.ds_serie = formFields.getDs_serie().parse();
	this.aa_serie = formFields.getAa_serie().parse();
	setDt_inizio_serie(formFields.getDt_inizio_serie().parse());
	setDt_fine_serie(formFields.getDt_fine_serie().parse());
	this.ds_nota_azione = formFields.getDs_nota_azione().parse();
	this.cd_doc_file_input_ver_serie = formFields.getCd_doc_file_input_ver_serie().parse();
	this.ds_doc_file_input_ver_serie = formFields.getDs_doc_file_input_ver_serie().parse();
	this.fl_fornito_ente = formFields.getFl_fornito_ente().parse();
	this.cd_serie_padre = formFields.getCd_serie_padre().parse();
	this.ds_serie_padre = formFields.getDs_serie_padre().parse();
	this.cd_serie_padre_da_creare = formFields.getCd_serie_padre_da_creare().parse();
	this.ds_serie_padre_da_creare = formFields.getDs_serie_padre_da_creare().parse();
	setDt_reg_unita_doc_da(formFields.getDt_reg_unita_doc_da().parse());
	setDt_reg_unita_doc_a(formFields.getDt_reg_unita_doc_a().parse());
	setDt_creazione_unita_doc_da(formFields.getDt_creazione_unita_doc_da().parse());
	setDt_creazione_unita_doc_a(formFields.getDt_creazione_unita_doc_a().parse());
	this.ds_lista_anni_sel_serie = formFields.getDs_lista_anni_sel_serie().parse();
	BigDecimal anni = formFields.getNi_anni_conserv().parse();
	String unlimited = formFields.getConserv_unlimited().parse();
	if (anni == null) {
	    if (unlimited != null && unlimited.equals("1")) {
		this.ni_anni_conserv = new BigDecimal(9999);
	    } else {
		throw new ParerUserError(
			"Eccezione nella collezione dei parametri per la creazione della serie");
	    }
	} else {
	    this.ni_anni_conserv = anni;
	}
    }

    public String getTi_creazione() {
	return ti_creazione;
    }

    public void setTi_creazione(String ti_creazione) {
	this.ti_creazione = ti_creazione;
    }

    public BigDecimal getNm_tipo_serie() {
	return nm_tipo_serie;
    }

    public void setNm_tipo_serie(BigDecimal nm_tipo_serie) {
	this.nm_tipo_serie = nm_tipo_serie;
    }

    public BigDecimal getNi_anni_conserv() {
	return ni_anni_conserv;
    }

    public void setNi_anni_conserv(BigDecimal ni_anni_conserv) {
	this.ni_anni_conserv = ni_anni_conserv;
    }

    public String getCd_serie() {
	return cd_serie;
    }

    public void setCd_serie(String cd_serie) {
	this.cd_serie = cd_serie;
    }

    public String getDs_serie() {
	return ds_serie;
    }

    public void setDs_serie(String ds_serie) {
	this.ds_serie = ds_serie;
    }

    public BigDecimal getAa_serie() {
	return aa_serie;
    }

    public void setAa_serie(BigDecimal aa_serie) {
	this.aa_serie = aa_serie;
    }

    public Date getDt_inizio_serie() {
	return dt_inizio_serie;
    }

    public void setDt_inizio_serie(Date dt_inizio_serie) {
	if (dt_inizio_serie != null) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(dt_inizio_serie);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    this.dt_inizio_serie = cal.getTime();
	} else {
	    this.dt_inizio_serie = null;
	}
    }

    public Date getDt_fine_serie() {
	return dt_fine_serie;
    }

    public void setDt_fine_serie(Date dt_fine_serie) {
	if (dt_fine_serie != null) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(dt_fine_serie);
	    cal.set(Calendar.HOUR_OF_DAY, 23);
	    cal.set(Calendar.MINUTE, 59);
	    cal.set(Calendar.SECOND, 59);
	    cal.set(Calendar.MILLISECOND, 999);
	    this.dt_fine_serie = cal.getTime();
	} else {
	    this.dt_fine_serie = null;
	}
    }

    public String getCd_serie_padre() {
	return cd_serie_padre;
    }

    public void setCd_serie_padre(String cd_serie_padre) {
	this.cd_serie_padre = cd_serie_padre;
    }

    public String getDs_serie_padre() {
	return ds_serie_padre;
    }

    public void setDs_serie_padre(String ds_serie_padre) {
	this.ds_serie_padre = ds_serie_padre;
    }

    public String getCd_serie_padre_da_creare() {
	return cd_serie_padre_da_creare;
    }

    public void setCd_serie_padre_da_creare(String cd_serie_padre_da_creare) {
	this.cd_serie_padre_da_creare = cd_serie_padre_da_creare;
    }

    public String getDs_serie_padre_da_creare() {
	return ds_serie_padre_da_creare;
    }

    public void setDs_serie_padre_da_creare(String ds_serie_padre_da_creare) {
	this.ds_serie_padre_da_creare = ds_serie_padre_da_creare;
    }

    public BigDecimal getId_serie_padre() {
	return id_serie_padre;
    }

    public void setId_serie_padre(BigDecimal id_serie_padre) {
	this.id_serie_padre = id_serie_padre;
    }

    public String getDs_azione() {
	return ds_azione;
    }

    public void setDs_azione(String ds_azione) {
	this.ds_azione = ds_azione;
    }

    public String getDs_nota_azione() {
	return ds_nota_azione;
    }

    public void setDs_nota_azione(String ds_nota_azione) {
	this.ds_nota_azione = ds_nota_azione;
    }

    public String getCd_doc_file_input_ver_serie() {
	return cd_doc_file_input_ver_serie;
    }

    public void setCd_doc_file_input_ver_serie(String cd_doc_file_input_ver_serie) {
	this.cd_doc_file_input_ver_serie = cd_doc_file_input_ver_serie;
    }

    public String getDs_doc_file_input_ver_serie() {
	return ds_doc_file_input_ver_serie;
    }

    public void setDs_doc_file_input_ver_serie(String ds_doc_file_input_ver_serie) {
	this.ds_doc_file_input_ver_serie = ds_doc_file_input_ver_serie;
    }

    public String getFl_fornito_ente() {
	return fl_fornito_ente;
    }

    public void setFl_fornito_ente(String fl_fornito_ente) {
	this.fl_fornito_ente = fl_fornito_ente;
    }

    public Date getDt_reg_unita_doc_da() {
	return dt_reg_unita_doc_da;
    }

    public void setDt_reg_unita_doc_da(Date dt_reg_unita_doc_da) {
	if (dt_reg_unita_doc_da != null) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(dt_reg_unita_doc_da);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    this.dt_reg_unita_doc_da = cal.getTime();
	} else {
	    this.dt_reg_unita_doc_da = null;
	}
    }

    public Date getDt_reg_unita_doc_a() {
	return dt_reg_unita_doc_a;
    }

    public void setDt_reg_unita_doc_a(Date dt_reg_unita_doc_a) {
	if (dt_reg_unita_doc_a != null) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(dt_reg_unita_doc_a);
	    cal.set(Calendar.HOUR_OF_DAY, 23);
	    cal.set(Calendar.MINUTE, 59);
	    cal.set(Calendar.SECOND, 59);
	    cal.set(Calendar.MILLISECOND, 999);
	    this.dt_reg_unita_doc_a = cal.getTime();
	} else {
	    this.dt_reg_unita_doc_a = null;
	}
    }

    public Date getDt_creazione_unita_doc_da() {
	return dt_creazione_unita_doc_da;
    }

    public void setDt_creazione_unita_doc_da(Date dt_creazione_unita_doc_da) {
	if (dt_creazione_unita_doc_da != null) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(dt_creazione_unita_doc_da);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    this.dt_creazione_unita_doc_da = cal.getTime();
	} else {
	    this.dt_creazione_unita_doc_da = null;
	}
    }

    public Date getDt_creazione_unita_doc_a() {
	return dt_creazione_unita_doc_a;
    }

    public void setDt_creazione_unita_doc_a(Date dt_creazione_unita_doc_a) {
	if (dt_creazione_unita_doc_a != null) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(dt_creazione_unita_doc_a);
	    cal.set(Calendar.HOUR_OF_DAY, 23);
	    cal.set(Calendar.MINUTE, 59);
	    cal.set(Calendar.SECOND, 59);
	    cal.set(Calendar.MILLISECOND, 999);
	    this.dt_creazione_unita_doc_a = cal.getTime();
	} else {
	    this.dt_creazione_unita_doc_a = null;
	}
    }

    public String getTi_periodo_sel_serie() {
	return ti_periodo_sel_serie;
    }

    public void setTi_periodo_sel_serie(String ti_periodo_sel_serie) {
	this.ti_periodo_sel_serie = ti_periodo_sel_serie;
    }

    public BigDecimal getNi_periodo_sel_serie() {
	return ni_periodo_sel_serie;
    }

    public void setNi_periodo_sel_serie(BigDecimal ni_periodo_sel_serie) {
	this.ni_periodo_sel_serie = ni_periodo_sel_serie;
    }

    public String getDs_lista_anni_sel_serie() {
	return ds_lista_anni_sel_serie;
    }

    public void setDs_lista_anni_sel_serie(String ds_lista_anni_sel_serie) {
	this.ds_lista_anni_sel_serie = ds_lista_anni_sel_serie;
    }

    public String getCdCompositoSerie() {
	String codiceSeriePadre = null;
	if (StringUtils.isNotBlank(this.getCd_serie_padre())) {
	    codiceSeriePadre = this.getCd_serie_padre();
	} else if (StringUtils.isNotBlank(this.getCd_serie_padre_da_creare())) {
	    codiceSeriePadre = this.getCd_serie_padre_da_creare();
	}
	String codiceSerie = StringUtils.isNotBlank(codiceSeriePadre)
		? codiceSeriePadre + "/" + this.getCd_serie()
		: this.getCd_serie();
	return codiceSerie;
    }

    public String getCd_serie_normaliz() {
	return cd_serie_normaliz;
    }

    public void setCd_serie_normaliz(String cd_serie_normaliz) {
	this.cd_serie_normaliz = cd_serie_normaliz;
    }

}
