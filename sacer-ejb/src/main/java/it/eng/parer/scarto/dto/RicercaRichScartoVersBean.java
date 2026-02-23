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

package it.eng.parer.scarto.dto;

import it.eng.parer.slite.gen.form.ScartoForm;
import it.eng.spagoCore.error.EMFError;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RicercaRichScartoVersBean {

    private BigDecimal id_ambiente;
    private BigDecimal id_ente;
    private BigDecimal id_strut;
    private String cd_rich_scarto_vers;
    private String ds_rich_scarto_vers;
    private String nt_rich_scarto_vers;
    private List<String> ti_stato_rich_scarto_vers_cor;
    private Date dt_creazione_rich_scarto_vers_da;
    private Date dt_creazione_rich_scarto_vers_a;
    private String fl_non_scartabile;
    private String cd_registro_key_unita_doc;
    private BigDecimal aa_key_unita_doc;
    private String cd_key_unita_doc;

    public RicercaRichScartoVersBean() {

    }

    public RicercaRichScartoVersBean(ScartoForm.FiltriRicercaRichScartoVers filtri)
            throws EMFError {
        this.id_ambiente = filtri.getId_ambiente().parse();
        this.id_ente = filtri.getId_ente().parse();
        this.id_strut = filtri.getId_strut().parse();
        this.cd_rich_scarto_vers = filtri.getCd_rich_scarto_vers().parse();
        this.ds_rich_scarto_vers = filtri.getDs_rich_scarto_vers().parse();
        this.nt_rich_scarto_vers = filtri.getNt_rich_scarto_vers().parse();
        this.ti_stato_rich_scarto_vers_cor = filtri.getTi_stato_rich_scarto_vers_cor().parse();
        this.dt_creazione_rich_scarto_vers_da = filtri.getDt_creazione_rich_scarto_vers_da()
                .parse();
        this.dt_creazione_rich_scarto_vers_a = filtri.getDt_creazione_rich_scarto_vers_a().parse();
        this.fl_non_scartabile = filtri.getFl_non_scartabile().parse();
        this.cd_registro_key_unita_doc = filtri.getCd_registro_key_unita_doc().parse();
        this.aa_key_unita_doc = filtri.getAa_key_unita_doc().parse();
        this.cd_key_unita_doc = filtri.getCd_key_unita_doc().parse();
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

    public String getCd_rich_scarto_vers() {
        return cd_rich_scarto_vers;
    }

    public void setCd_rich_scarto_vers(String cd_rich_scarto_vers) {
        this.cd_rich_scarto_vers = cd_rich_scarto_vers;
    }

    public String getDs_rich_scarto_vers() {
        return ds_rich_scarto_vers;
    }

    public void setDs_rich_scarto_vers(String ds_rich_scarto_vers) {
        this.ds_rich_scarto_vers = ds_rich_scarto_vers;
    }

    public String getNt_rich_scarto_vers() {
        return nt_rich_scarto_vers;
    }

    public void setNt_rich_scarto_vers(String nt_rich_scarto_vers) {
        this.nt_rich_scarto_vers = nt_rich_scarto_vers;
    }

    public List<String> getTi_stato_rich_scarto_vers_cor() {
        return ti_stato_rich_scarto_vers_cor;
    }

    public void setTi_stato_rich_scarto_vers_cor(List<String> ti_stato_rich_scarto_vers_cor) {
        this.ti_stato_rich_scarto_vers_cor = ti_stato_rich_scarto_vers_cor;
    }

    public Date getDt_creazione_rich_scarto_vers_da() {
        return dt_creazione_rich_scarto_vers_da;
    }

    public void setDt_creazione_rich_scarto_vers_da(Date dt_creazione_rich_scarto_vers_da) {
        this.dt_creazione_rich_scarto_vers_da = dt_creazione_rich_scarto_vers_da;
    }

    public Date getDt_creazione_rich_scarto_vers_a() {
        return dt_creazione_rich_scarto_vers_a;
    }

    public void setDt_creazione_rich_scarto_vers_a(Date dt_creazione_rich_scarto_vers_a) {
        this.dt_creazione_rich_scarto_vers_a = dt_creazione_rich_scarto_vers_a;
    }

    public String getFl_non_scartabile() {
        return fl_non_scartabile;
    }

    public void setFl_non_scartabile(String fl_non_scartabile) {
        this.fl_non_scartabile = fl_non_scartabile;
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

}
