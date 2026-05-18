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

package it.eng.parer.fascicoli.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Moretti_Lu
 */
public class RicercaFascicoliBean {

    private BigDecimal aa_fascicolo;
    private BigDecimal aa_fascicolo_da;
    private BigDecimal aa_fascicolo_a;
    private String cd_key_fascicolo;
    private String cd_key_fascicolo_da;
    private String cd_key_fascicolo_a;
    private BigDecimal nm_tipo_fascicolo;
    private String ti_modello_xsd;
    private String cd_xsd;
    private String ds_oggetto_fascicolo;
    private Date dt_ape_fasciolo_da;
    private Date dt_ape_fasciolo_a;
    private Date dt_chiu_fasciolo_da;
    private Date dt_chiu_fasciolo_a;
    private String cd_proc_ammin;
    private String ds_proc_ammin;
    private BigDecimal ni_aa_conservazione;
    private String cd_livello_riserv;
    private String nm_sistema_versante;
    private String nm_userid;

    private String cd_composito_voce_titol;
    private BigDecimal aa_fascicolo_padre;
    private BigDecimal aa_fascicolo_padre_a;
    private BigDecimal aa_fascicolo_padre_da;
    private String cd_key_fascicolo_padre;
    private String cd_key_fascicolo_padre_da;
    private String cd_key_fascicolo_padre_a;
    private String ds_oggetto_fascicolo_padre;

    private String cd_registro_key_unita_doc;
    private BigDecimal aa_key_unita_doc;
    private BigDecimal aa_key_unita_doc_da;
    private BigDecimal aa_key_unita_doc_a;
    private String cd_key_unita_doc;
    private String cd_key_unita_doc_da;
    private String cd_key_unita_doc_a;

    private String ti_conservazione;
    private String fl_forza_contr_classif;
    private String fl_forza_contr_numero;
    private String fl_forza_contr_colleg;
    private String cd_versione_ws;

    private Date ts_vers_fascicolo_da;
    private Date ts_vers_fascicolo_a;
    private String ti_esito;
    private String ti_stato_conservazione;
    private String ti_stato_fasc_elenco_vers;

    private String fl_upd_annul_unita_doc;
    private String fl_upd_modif_unita_doc;

    public RicercaFascicoliBean() {
    }

    public BigDecimal getAa_fascicolo() {
        return aa_fascicolo;
    }

    public void setAa_fascicolo(BigDecimal aa_fascicolo) {
        this.aa_fascicolo = aa_fascicolo;
    }

    public BigDecimal getAa_fascicolo_da() {
        return aa_fascicolo_da;
    }

    public void setAa_fascicolo_da(BigDecimal aa_fascicolo_da) {
        this.aa_fascicolo_da = aa_fascicolo_da;
    }

    public BigDecimal getAa_fascicolo_a() {
        return aa_fascicolo_a;
    }

    public void setAa_fascicolo_a(BigDecimal aa_fascicolo_a) {
        this.aa_fascicolo_a = aa_fascicolo_a;
    }

    public String getCd_key_fascicolo() {
        return cd_key_fascicolo;
    }

    public void setCd_key_fascicolo(String cd_key_fascicolo) {
        this.cd_key_fascicolo = cd_key_fascicolo;
    }

    public String getCd_key_fascicolo_da() {
        return cd_key_fascicolo_da;
    }

    public void setCd_key_fascicolo_da(String cd_key_fascicolo_da) {
        this.cd_key_fascicolo_da = cd_key_fascicolo_da;
    }

    public String getCd_key_fascicolo_a() {
        return cd_key_fascicolo_a;
    }

    public void setCd_key_fascicolo_a(String cd_key_fascicolo_a) {
        this.cd_key_fascicolo_a = cd_key_fascicolo_a;
    }

    public BigDecimal getNm_tipo_fascicolo() {
        return nm_tipo_fascicolo;
    }

    public void setNm_tipo_fascicolo(BigDecimal nm_tipo_fascicolo) {
        this.nm_tipo_fascicolo = nm_tipo_fascicolo;
    }

    public String getTi_modello_xsd() {
        return ti_modello_xsd;
    }

    public void setTi_modello_xsd(String ti_modello_xsd) {
        this.ti_modello_xsd = ti_modello_xsd;
    }

    public String getCd_xsd() {
        return cd_xsd;
    }

    public void setCd_xsd(String cd_xsd) {
        this.cd_xsd = cd_xsd;
    }

    public String getDs_oggetto_fascicolo() {
        return ds_oggetto_fascicolo;
    }

    public void setDs_oggetto_fascicolo(String ds_oggetto_fascicolo) {
        this.ds_oggetto_fascicolo = ds_oggetto_fascicolo;
    }

    public Date getDt_ape_fasciolo_da() {
        return dt_ape_fasciolo_da;
    }

    public void setDt_ape_fasciolo_da(Date dt_ape_fasciolo_da) {
        this.dt_ape_fasciolo_da = dt_ape_fasciolo_da;
    }

    public Date getDt_ape_fasciolo_a() {
        return dt_ape_fasciolo_a;
    }

    public void setDt_ape_fasciolo_a(Date dt_ape_fasciolo_a) {
        this.dt_ape_fasciolo_a = dt_ape_fasciolo_a;
    }

    public Date getDt_chiu_fasciolo_da() {
        return dt_chiu_fasciolo_da;
    }

    public void setDt_chiu_fasciolo_da(Date dt_chiu_fasciolo_da) {
        this.dt_chiu_fasciolo_da = dt_chiu_fasciolo_da;
    }

    public Date getDt_chiu_fasciolo_a() {
        return dt_chiu_fasciolo_a;
    }

    public void setDt_chiu_fasciolo_a(Date dt_chiu_fasciolo_a) {
        this.dt_chiu_fasciolo_a = dt_chiu_fasciolo_a;
    }

    public String getCd_proc_ammin() {
        return cd_proc_ammin;
    }

    public void setCd_proc_ammin(String cd_proc_ammin) {
        this.cd_proc_ammin = cd_proc_ammin;
    }

    public String getDs_proc_ammin() {
        return ds_proc_ammin;
    }

    public void setDs_proc_ammin(String ds_proc_ammin) {
        this.ds_proc_ammin = ds_proc_ammin;
    }

    public BigDecimal getNi_aa_conservazione() {
        return ni_aa_conservazione;
    }

    public void setNi_aa_conservazione(BigDecimal ni_aa_conservazione) {
        this.ni_aa_conservazione = ni_aa_conservazione;
    }

    public String getCd_livello_riserv() {
        return cd_livello_riserv;
    }

    public void setCd_livello_riserv(String cd_livello_riserv) {
        this.cd_livello_riserv = cd_livello_riserv;
    }

    public String getNm_sistema_versante() {
        return nm_sistema_versante;
    }

    public void setNm_sistema_versante(String nm_sistema_versante) {
        this.nm_sistema_versante = nm_sistema_versante;
    }

    public String getNm_userid() {
        return nm_userid;
    }

    public void setNm_userid(String nm_userid) {
        this.nm_userid = nm_userid;
    }

    public String getCd_composito_voce_titol() {
        return cd_composito_voce_titol;
    }

    public void setCd_composito_voce_titol(String cd_composito_voce_titol) {
        this.cd_composito_voce_titol = cd_composito_voce_titol;
    }

    public BigDecimal getAa_fascicolo_padre() {
        return aa_fascicolo_padre;
    }

    public void setAa_fascicolo_padre(BigDecimal aa_fascicolo_padre) {
        this.aa_fascicolo_padre = aa_fascicolo_padre;
    }

    public BigDecimal getAa_fascicolo_padre_a() {
        return aa_fascicolo_padre_a;
    }

    public void setAa_fascicolo_padre_a(BigDecimal aa_fascicolo_padre_a) {
        this.aa_fascicolo_padre_a = aa_fascicolo_padre_a;
    }

    public BigDecimal getAa_fascicolo_padre_da() {
        return aa_fascicolo_padre_da;
    }

    public void setAa_fascicolo_padre_da(BigDecimal aa_fascicolo_padre_da) {
        this.aa_fascicolo_padre_da = aa_fascicolo_padre_da;
    }

    public String getCd_key_fascicolo_padre() {
        return cd_key_fascicolo_padre;
    }

    public void setCd_key_fascicolo_padre(String cd_key_fascicolo_padre) {
        this.cd_key_fascicolo_padre = cd_key_fascicolo_padre;
    }

    public String getCd_key_fascicolo_padre_da() {
        return cd_key_fascicolo_padre_da;
    }

    public void setCd_key_fascicolo_padre_da(String cd_key_fascicolo_padre_da) {
        this.cd_key_fascicolo_padre_da = cd_key_fascicolo_padre_da;
    }

    public String getCd_key_fascicolo_padre_a() {
        return cd_key_fascicolo_padre_a;
    }

    public void setCd_key_fascicolo_padre_a(String cd_key_fascicolo_padre_a) {
        this.cd_key_fascicolo_padre_a = cd_key_fascicolo_padre_a;
    }

    public String getDs_oggetto_fascicolo_padre() {
        return ds_oggetto_fascicolo_padre;
    }

    public void setDs_oggetto_fascicolo_padre(String ds_oggetto_fascicolo_padre) {
        this.ds_oggetto_fascicolo_padre = ds_oggetto_fascicolo_padre;
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

    public BigDecimal getAa_key_unita_doc_da() {
        return aa_key_unita_doc_da;
    }

    public void setAa_key_unita_doc_da(BigDecimal aa_key_unita_doc_da) {
        this.aa_key_unita_doc_da = aa_key_unita_doc_da;
    }

    public BigDecimal getAa_key_unita_doc_a() {
        return aa_key_unita_doc_a;
    }

    public void setAa_key_unita_doc_a(BigDecimal aa_key_unita_doc_a) {
        this.aa_key_unita_doc_a = aa_key_unita_doc_a;
    }

    public String getCd_key_unita_doc() {
        return cd_key_unita_doc;
    }

    public void setCd_key_unita_doc(String cd_key_unita_doc) {
        this.cd_key_unita_doc = cd_key_unita_doc;
    }

    public String getCd_key_unita_doc_da() {
        return cd_key_unita_doc_da;
    }

    public void setCd_key_unita_doc_da(String cd_key_unita_doc_da) {
        this.cd_key_unita_doc_da = cd_key_unita_doc_da;
    }

    public String getCd_key_unita_doc_a() {
        return cd_key_unita_doc_a;
    }

    public void setCd_key_unita_doc_a(String cd_key_unita_doc_a) {
        this.cd_key_unita_doc_a = cd_key_unita_doc_a;
    }

    public String getTi_conservazione() {
        return ti_conservazione;
    }

    public void setTi_conservazione(String ti_conservazione) {
        this.ti_conservazione = ti_conservazione;
    }

    public String getFl_forza_contr_classif() {
        return fl_forza_contr_classif;
    }

    public void setFl_forza_contr_classif(String fl_forza_contr_classif) {
        this.fl_forza_contr_classif = fl_forza_contr_classif;
    }

    public String getFl_forza_contr_numero() {
        return fl_forza_contr_numero;
    }

    public void setFl_forza_contr_numero(String fl_forza_contr_numero) {
        this.fl_forza_contr_numero = fl_forza_contr_numero;
    }

    public String getFl_forza_contr_colleg() {
        return fl_forza_contr_colleg;
    }

    public void setFl_forza_contr_colleg(String fl_forza_contr_colleg) {
        this.fl_forza_contr_colleg = fl_forza_contr_colleg;
    }

    public Date getTs_vers_fascicolo_da() {
        return ts_vers_fascicolo_da;
    }

    public void setTs_vers_fascicolo_da(Date ts_vers_fascicolo_da) {
        this.ts_vers_fascicolo_da = ts_vers_fascicolo_da;
    }

    public Date getTs_vers_fascicolo_a() {
        return ts_vers_fascicolo_a;
    }

    public void setTs_vers_fascicolo_a(Date ts_vers_fascicolo_a) {
        this.ts_vers_fascicolo_a = ts_vers_fascicolo_a;
    }

    public String getTi_esito() {
        return ti_esito;
    }

    public void setTi_esito(String ti_esito) {
        this.ti_esito = ti_esito;
    }

    public String getTi_stato_conservazione() {
        return ti_stato_conservazione;
    }

    public void setTi_stato_conservazione(String ti_stato_conservazione) {
        this.ti_stato_conservazione = ti_stato_conservazione;
    }

    public String getTi_stato_fasc_elenco_vers() {
        return ti_stato_fasc_elenco_vers;
    }

    public void setTi_stato_fasc_elenco_vers(String ti_stato_fasc_elenco_vers) {
        this.ti_stato_fasc_elenco_vers = ti_stato_fasc_elenco_vers;
    }

    public String getCd_versione_ws() {
        return cd_versione_ws;
    }

    public void setCd_versione_ws(String cd_versione_ws) {
        this.cd_versione_ws = cd_versione_ws;
    }

    public String getFlUpdAnnulUnitaDoc() {
        return fl_upd_annul_unita_doc;
    }

    public void setFlUpdAnnulUnitaDoc(String fl_upd_annul_unita_doc) {
        this.fl_upd_annul_unita_doc = fl_upd_annul_unita_doc;
    }

    public String getFlUpdModifUnitaDoc() {
        return fl_upd_modif_unita_doc;
    }

    public void setFlUpdModifUnitaDoc(String fl_upd_modif_unita_doc) {
        this.fl_upd_modif_unita_doc = fl_upd_modif_unita_doc;
    }
}
