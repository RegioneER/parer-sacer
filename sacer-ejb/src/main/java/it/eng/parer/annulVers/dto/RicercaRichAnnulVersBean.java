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

package it.eng.parer.annulVers.dto;

import it.eng.parer.slite.gen.form.AnnulVersForm;
import it.eng.spagoCore.error.EMFError;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Bonora_L
 */
public class RicercaRichAnnulVersBean {

    private BigDecimal id_ambiente;
    private BigDecimal id_ente;
    private BigDecimal id_strut;
    private String cd_rich_annul_vers;
    private String ds_rich_annul_vers;
    private String nt_rich_annul_vers;
    private List<String> ti_stato_rich_annul_vers_cor;
    private Date dt_creazione_rich_annul_vers_da;
    private Date dt_creazione_rich_annul_vers_a;
    private String fl_immediata;
    private String fl_annul_ping;
    private String fl_non_annul;
    private String cd_registro_key_unita_doc;
    private BigDecimal aa_key_unita_doc;
    private String cd_key_unita_doc;
    private BigDecimal aa_fascicolo;
    private String cd_key_fascicolo;
    private String ti_rich_annul_vers;
    private String ti_annullamento;

    public RicercaRichAnnulVersBean() {

    }

    public RicercaRichAnnulVersBean(AnnulVersForm.FiltriRicercaRichAnnullVers filtri)
	    throws EMFError {
	this.id_ambiente = filtri.getId_ambiente().parse();
	this.id_ente = filtri.getId_ente().parse();
	this.id_strut = filtri.getId_strut().parse();
	this.cd_rich_annul_vers = filtri.getCd_rich_annul_vers().parse();
	this.ds_rich_annul_vers = filtri.getDs_rich_annul_vers().parse();
	this.nt_rich_annul_vers = filtri.getNt_rich_annul_vers().parse();
	this.ti_stato_rich_annul_vers_cor = filtri.getTi_stato_rich_annul_vers_cor().parse();
	this.dt_creazione_rich_annul_vers_da = filtri.getDt_creazione_rich_annul_vers_da().parse();
	this.dt_creazione_rich_annul_vers_a = filtri.getDt_creazione_rich_annul_vers_a().parse();
	this.fl_immediata = filtri.getFl_immediata().parse();
	this.fl_annul_ping = filtri.getFl_annul_ping().parse();
	this.fl_non_annul = filtri.getFl_non_annul().parse();
	this.cd_registro_key_unita_doc = filtri.getCd_registro_key_unita_doc().parse();
	this.aa_key_unita_doc = filtri.getAa_key_unita_doc().parse();
	this.cd_key_unita_doc = filtri.getCd_key_unita_doc().parse();
	this.aa_fascicolo = filtri.getAa_fascicolo().parse();
	this.cd_key_fascicolo = filtri.getCd_key_fascicolo().parse();
	this.ti_rich_annul_vers = filtri.getTi_rich_annul_vers().parse();
	this.ti_annullamento = filtri.getTi_annullamento().parse();
    }

    public String getTi_rich_annul_vers() {
	return ti_rich_annul_vers;
    }

    public void setTi_rich_annul_vers(String ti_rich_annul_vers) {
	this.ti_rich_annul_vers = ti_rich_annul_vers;
    }

    public BigDecimal getAa_fascicolo() {
	return aa_fascicolo;
    }

    public void setAa_fascicolo(BigDecimal aa_fascicolo) {
	this.aa_fascicolo = aa_fascicolo;
    }

    public String getCd_key_fascicolo() {
	return cd_key_fascicolo;
    }

    public void setCd_key_fascicolo(String cd_key_fascicolo) {
	this.cd_key_fascicolo = cd_key_fascicolo;
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

    public String getCd_rich_annul_vers() {
	return cd_rich_annul_vers;
    }

    public void setCd_rich_annul_vers(String cd_rich_annul_vers) {
	this.cd_rich_annul_vers = cd_rich_annul_vers;
    }

    public String getDs_rich_annul_vers() {
	return ds_rich_annul_vers;
    }

    public void setDs_rich_annul_vers(String ds_rich_annul_vers) {
	this.ds_rich_annul_vers = ds_rich_annul_vers;
    }

    public String getNt_rich_annul_vers() {
	return nt_rich_annul_vers;
    }

    public void setNt_rich_annul_vers(String nt_rich_annul_vers) {
	this.nt_rich_annul_vers = nt_rich_annul_vers;
    }

    public List<String> getTi_stato_rich_annul_vers_cor() {
	return ti_stato_rich_annul_vers_cor;
    }

    public void setTi_stato_rich_annul_vers_cor(List<String> ti_stato_rich_annul_vers_cor) {
	this.ti_stato_rich_annul_vers_cor = ti_stato_rich_annul_vers_cor;
    }

    public Date getDt_creazione_rich_annul_vers_da() {
	return dt_creazione_rich_annul_vers_da;
    }

    public void setDt_creazione_rich_annul_vers_da(Date dt_creazione_rich_annul_vers_da) {
	this.dt_creazione_rich_annul_vers_da = dt_creazione_rich_annul_vers_da;
    }

    public Date getDt_creazione_rich_annul_vers_a() {
	return dt_creazione_rich_annul_vers_a;
    }

    public void setDt_creazione_rich_annul_vers_a(Date dt_creazione_rich_annul_vers_a) {
	this.dt_creazione_rich_annul_vers_a = dt_creazione_rich_annul_vers_a;
    }

    public String getFl_immediata() {
	return fl_immediata;
    }

    public void setFl_immediata(String fl_immediata) {
	this.fl_immediata = fl_immediata;
    }

    public String getFl_annul_ping() {
	return fl_annul_ping;
    }

    public void setFl_annul_ping(String fl_annul_ping) {
	this.fl_annul_ping = fl_annul_ping;
    }

    public String getFl_non_annul() {
	return fl_non_annul;
    }

    public void setFl_non_annul(String fl_non_annul) {
	this.fl_non_annul = fl_non_annul;
    }

    public String getCd_registro_key_unita_doc() {
	return cd_registro_key_unita_doc;
    }

    public void setCd_registro_key_unita_doc(String cd_registro_key_unita_doc) {
	this.cd_registro_key_unita_doc = cd_registro_key_unita_doc;
    }

    public BigDecimal getAa_key_unita_doc() {
	return aa_key_unita_doc;
    }

    public void setAa_key_unita_doc(BigDecimal aa_key_unita_doc) {
	this.aa_key_unita_doc = aa_key_unita_doc;
    }

    public String getCd_key_unita_doc() {
	return cd_key_unita_doc;
    }

    public void setCd_key_unita_doc(String cd_key_unita_doc) {
	this.cd_key_unita_doc = cd_key_unita_doc;
    }

    public String getTi_annullamento() {
	return ti_annullamento;
    }

    public void setTi_annullamento(String ti_annullamento) {
	this.ti_annullamento = ti_annullamento;
    }

}
