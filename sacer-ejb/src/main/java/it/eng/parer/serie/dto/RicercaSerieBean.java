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
import java.util.Date;
import java.util.List;

import it.eng.parer.slite.gen.form.SerieUDForm;
import it.eng.parer.slite.gen.form.SerieUdPerUtentiExtForm;
import it.eng.spagoCore.error.EMFError;

/**
 *
 * @author Bonora_L
 */
public class RicercaSerieBean {

    private String cd_composito_serie;
    private String ds_serie;
    private List<String> ti_stato_cor_serie;
    private List<String> ti_stato_conservazione;
    private BigDecimal id_tipo_serie;
    private BigDecimal aa_serie_da;
    private BigDecimal aa_serie_a;
    private Date dt_inizio_serie;
    private Date dt_fine_serie;
    private String fl_da_rigenera;
    private BigDecimal id_ambiente;
    private BigDecimal id_ente;
    private BigDecimal id_strut;
    private String ti_stato_contenuto_calc;
    private String fl_err_contenuto_calc;
    private String ti_stato_contenuto_acq;
    private String fl_err_contenuto_file;
    private String fl_err_contenuto_acq;
    private String ti_stato_contenuto_eff;
    private String fl_err_contenuto_eff;
    private String fl_elab_bloccata;
    private String fl_err_validazione;
    private String fl_presenza_consist_attesa;
    private String ti_crea_standard;
    private BigDecimal id_modello_tipo_serie;
    private BigDecimal id_registro_unita_doc;
    private BigDecimal id_tipo_unita_doc;

    public RicercaSerieBean(SerieUDForm.FiltriRicercaSerie filtri) throws EMFError {
	this.cd_composito_serie = filtri.getCd_composito_serie().parse();
	this.ds_serie = filtri.getDs_serie().parse();
	this.ti_stato_cor_serie = filtri.getTi_stato_cor_serie().parse();
	this.ti_stato_conservazione = filtri.getTi_stato_conservazione().parse();
	this.id_tipo_serie = filtri.getNm_tipo_serie().parse();
	this.aa_serie_da = filtri.getAa_serie_da().parse();
	this.aa_serie_a = filtri.getAa_serie_a().parse();
	this.dt_inizio_serie = filtri.getDt_inizio_serie().parse();
	this.dt_fine_serie = filtri.getDt_fine_serie().parse();
	this.fl_da_rigenera = filtri.getFl_da_rigenera().parse();

	this.id_ambiente = filtri.getId_ambiente().parse();
	this.id_ente = filtri.getId_ente().parse();
	this.id_strut = filtri.getId_strut().parse();

	this.ti_stato_contenuto_calc = filtri.getTi_stato_contenuto_calc().parse();
	this.fl_err_contenuto_calc = filtri.getFl_err_contenuto_calc().parse();
	this.ti_stato_contenuto_acq = filtri.getTi_stato_contenuto_acq().parse();
	this.fl_err_contenuto_file = filtri.getFl_err_contenuto_file().parse();
	this.fl_err_contenuto_acq = filtri.getFl_err_contenuto_acq().parse();
	this.ti_stato_contenuto_eff = filtri.getTi_stato_contenuto_eff().parse();
	this.fl_err_contenuto_eff = filtri.getFl_err_contenuto_eff().parse();
	this.fl_elab_bloccata = filtri.getFl_elab_bloccata().parse();
	this.fl_err_validazione = filtri.getFl_err_validazione().parse();
	this.fl_presenza_consist_attesa = filtri.getFl_presenza_consist_attesa().parse();

	this.ti_crea_standard = filtri.getTi_crea_standard().parse();
	this.id_modello_tipo_serie = filtri.getId_modello_tipo_serie().parse();
    }

    public RicercaSerieBean(SerieUDForm.FiltriComunicazioneConsistenzaSerieUD filtri)
	    throws EMFError {
	this.cd_composito_serie = filtri.getCd_composito_serie().parse();
	this.ds_serie = filtri.getDs_serie().parse();
	this.id_tipo_serie = filtri.getNm_tipo_serie().parse();
	this.aa_serie_da = filtri.getAa_serie_da().parse();
	this.aa_serie_a = filtri.getAa_serie_a().parse();

	this.id_ambiente = filtri.getId_ambiente().parse();
	this.id_ente = filtri.getId_ente().parse();
	this.id_strut = filtri.getId_strut().parse();

	this.fl_presenza_consist_attesa = filtri.getFl_presenza_consist_attesa().parse();

	this.id_tipo_unita_doc = filtri.getId_tipo_unita_doc().parse();
	this.id_registro_unita_doc = filtri.getId_registro_unita_doc().parse();
    }

    public RicercaSerieBean(SerieUdPerUtentiExtForm.FiltriRicercaSerie filtri) throws EMFError {
	this.cd_composito_serie = filtri.getCd_composito_serie().parse();
	this.ds_serie = filtri.getDs_serie().parse();
	if (filtri.getAa_serie().parse() != null) {
	    this.aa_serie_da = filtri.getAa_serie().parse();
	    this.aa_serie_a = filtri.getAa_serie().parse();
	} else {
	    this.aa_serie_da = filtri.getAa_serie_da().parse();
	    this.aa_serie_a = filtri.getAa_serie_a().parse();
	}

	this.id_ambiente = filtri.getId_ambiente().parse();
	this.id_ente = filtri.getId_ente().parse();
	this.id_strut = filtri.getId_strut().parse();

	this.id_tipo_unita_doc = filtri.getNm_tipo_unita_doc().parse();
	this.id_registro_unita_doc = filtri.getCd_registro_unita_doc().parse();
    }

    public RicercaSerieBean() {

    }

    public String getCd_composito_serie() {
	return cd_composito_serie;
    }

    public void setCd_composito_serie(String cd_composito_serie) {
	this.cd_composito_serie = cd_composito_serie;
    }

    public String getDs_serie() {
	return ds_serie;
    }

    public void setDs_serie(String ds_serie) {
	this.ds_serie = ds_serie;
    }

    public List<String> getTi_stato_cor_serie() {
	return ti_stato_cor_serie;
    }

    public void setTi_stato_cor_serie(List<String> ti_stato_cor_serie) {
	this.ti_stato_cor_serie = ti_stato_cor_serie;
    }

    public List<String> getTi_stato_conservazione() {
	return ti_stato_conservazione;
    }

    public void setTi_stato_conservazione(List<String> ti_stato_conservazione) {
	this.ti_stato_conservazione = ti_stato_conservazione;
    }

    public BigDecimal getId_tipo_serie() {
	return id_tipo_serie;
    }

    public void setId_tipo_serie(BigDecimal id_tipo_serie) {
	this.id_tipo_serie = id_tipo_serie;
    }

    public BigDecimal getAa_serie_da() {
	return aa_serie_da;
    }

    public void setAa_serie_da(BigDecimal aa_serie_da) {
	this.aa_serie_da = aa_serie_da;
    }

    public BigDecimal getAa_serie_a() {
	return aa_serie_a;
    }

    public void setAa_serie_a(BigDecimal aa_serie_a) {
	this.aa_serie_a = aa_serie_a;
    }

    public Date getDt_inizio_serie() {
	return dt_inizio_serie;
    }

    public void setDt_inizio_serie(Date dt_inizio_serie) {
	this.dt_inizio_serie = dt_inizio_serie;
    }

    public Date getDt_fine_serie() {
	return dt_fine_serie;
    }

    public void setDt_fine_serie(Date dt_fine_serie) {
	this.dt_fine_serie = dt_fine_serie;
    }

    public String getFl_da_rigenera() {
	return fl_da_rigenera;
    }

    public void setFl_da_rigenera(String fl_da_rigenera) {
	this.fl_da_rigenera = fl_da_rigenera;
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

    public String getTi_stato_contenuto_calc() {
	return ti_stato_contenuto_calc;
    }

    public void setTi_stato_contenuto_calc(String ti_stato_contenuto_calc) {
	this.ti_stato_contenuto_calc = ti_stato_contenuto_calc;
    }

    public String getFl_err_contenuto_calc() {
	return fl_err_contenuto_calc;
    }

    public void setFl_err_contenuto_calc(String fl_err_contenuto_calc) {
	this.fl_err_contenuto_calc = fl_err_contenuto_calc;
    }

    public String getTi_stato_contenuto_acq() {
	return ti_stato_contenuto_acq;
    }

    public void setTi_stato_contenuto_acq(String ti_stato_contenuto_acq) {
	this.ti_stato_contenuto_acq = ti_stato_contenuto_acq;
    }

    public String getFl_err_contenuto_file() {
	return fl_err_contenuto_file;
    }

    public void setFl_err_contenuto_file(String fl_err_contenuto_file) {
	this.fl_err_contenuto_file = fl_err_contenuto_file;
    }

    public String getFl_err_contenuto_acq() {
	return fl_err_contenuto_acq;
    }

    public void setFl_err_contenuto_acq(String fl_err_contenuto_acq) {
	this.fl_err_contenuto_acq = fl_err_contenuto_acq;
    }

    public String getTi_stato_contenuto_eff() {
	return ti_stato_contenuto_eff;
    }

    public void setTi_stato_contenuto_eff(String ti_stato_contenuto_eff) {
	this.ti_stato_contenuto_eff = ti_stato_contenuto_eff;
    }

    public String getFl_err_contenuto_eff() {
	return fl_err_contenuto_eff;
    }

    public void setFl_err_contenuto_eff(String fl_err_contenuto_eff) {
	this.fl_err_contenuto_eff = fl_err_contenuto_eff;
    }

    public String getFl_elab_bloccata() {
	return fl_elab_bloccata;
    }

    public void setFl_elab_bloccata(String fl_elab_bloccata) {
	this.fl_elab_bloccata = fl_elab_bloccata;
    }

    public String getFl_presenza_consist_attesa() {
	return fl_presenza_consist_attesa;
    }

    public void setFl_presenza_consist_attesa(String fl_presenza_consist_attesa) {
	this.fl_presenza_consist_attesa = fl_presenza_consist_attesa;
    }

    public String getTi_crea_standard() {
	return ti_crea_standard;
    }

    public void setTi_crea_standard(String ti_crea_standard) {
	this.ti_crea_standard = ti_crea_standard;
    }

    public BigDecimal getId_modello_tipo_serie() {
	return id_modello_tipo_serie;
    }

    public void setId_modello_tipo_serie(BigDecimal id_modello_tipo_serie) {
	this.id_modello_tipo_serie = id_modello_tipo_serie;
    }

    public BigDecimal getId_registro_unita_doc() {
	return id_registro_unita_doc;
    }

    public void setId_registro_unita_doc(BigDecimal id_registro_unita_doc) {
	this.id_registro_unita_doc = id_registro_unita_doc;
    }

    public BigDecimal getId_tipo_unita_doc() {
	return id_tipo_unita_doc;
    }

    public void setId_tipo_unita_doc(BigDecimal id_tipo_unita_doc) {
	this.id_tipo_unita_doc = id_tipo_unita_doc;
    }

    public String getFl_err_validazione() {
	return fl_err_validazione;
    }

    public void setFl_err_validazione(String fl_err_validazione) {
	this.fl_err_validazione = fl_err_validazione;
    }

}
