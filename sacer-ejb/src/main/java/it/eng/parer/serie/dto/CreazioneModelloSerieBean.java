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

package it.eng.parer.serie.dto;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.slite.gen.form.ModelliSerieForm;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;

/**
 *
 * @author Bonora_L
 */
public class CreazioneModelloSerieBean {

    private BigDecimal id_ambiente;
    private String nm_modello_tipo_serie;
    private String ds_modello_tipo_serie;
    private Date dt_istituz;
    private Date dt_soppres;
    private String nm_tipo_serie_da_creare;
    private String ti_rgl_nm_tipo_serie;
    private String ds_tipo_serie_da_creare;
    private String ti_rgl_ds_tipo_serie;
    private String cd_serie_da_creare;
    private String ti_rgl_cd_serie;
    private String ds_serie_da_creare;
    private String ti_rgl_ds_serie;
    private String conserv_unlimited;
    private BigDecimal ni_anni_conserv;
    private String ti_rgl_anni_conserv;
    private String ti_conservazione_serie;
    private String ti_rgl_conservazione_serie;
    private String ti_sel_ud;
    private BigDecimal ni_aa_sel_ud;
    private BigDecimal ni_aa_sel_ud_suc;
    private BigDecimal ni_unita_doc_volume;
    private String fl_controllo_consist_obblig;
    private String fl_crea_autom;
    private String gg_crea_autom;
    private BigDecimal aa_ini_crea_autom;
    private BigDecimal aa_fin_crea_autom;
    private String ti_rgl_range_anni_crea_autom;
    private BigDecimal ni_mm_crea_autom;
    private BigDecimal id_tipo_unita_doc_dati_spec;
    private String nm_tipo_unita_doc_dati_spec;
    private BigDecimal id_tipo_doc_dati_spec;
    private String nm_tipo_doc_dati_spec;
    private String ti_rgl_filtro_ti_doc;
    private String ti_stato_ver_serie_autom;

    /**
     * Costruttore di un modello di tipo serie
     *
     * @param detail
     *            form di dettaglio
     *
     * @throws it.eng.spagoCore.error.EMFError
     *             Errore di parsing
     */
    public CreazioneModelloSerieBean(ModelliSerieForm.ModelliTipiSerieDetail detail) throws EMFError {
        this.id_ambiente = detail.getId_ambiente().parse();
        this.nm_modello_tipo_serie = detail.getNm_modello_tipo_serie().parse();
        this.ds_modello_tipo_serie = detail.getDs_modello_tipo_serie().parse();
        this.dt_istituz = detail.getDt_istituz().parse();
        this.dt_soppres = detail.getDt_soppres().parse();
        this.nm_tipo_serie_da_creare = detail.getNm_tipo_serie_da_creare().parse();
        this.ti_rgl_nm_tipo_serie = detail.getTi_rgl_nm_tipo_serie().parse();
        this.ds_tipo_serie_da_creare = detail.getDs_tipo_serie_da_creare().parse();
        this.ti_rgl_ds_tipo_serie = detail.getTi_rgl_ds_tipo_serie().parse();
        this.cd_serie_da_creare = detail.getCd_serie_da_creare().parse();
        this.ti_rgl_cd_serie = detail.getTi_rgl_cd_serie().parse();
        this.ds_serie_da_creare = detail.getDs_serie_da_creare().parse();
        this.ti_rgl_ds_serie = detail.getTi_rgl_ds_serie().parse();
        this.conserv_unlimited = detail.getConserv_unlimited().parse();
        this.ni_anni_conserv = detail.getNi_anni_conserv().parse();
        this.ti_rgl_anni_conserv = detail.getTi_rgl_anni_conserv().parse();
        this.ti_conservazione_serie = detail.getTi_conservazione_serie().parse();
        this.ti_rgl_conservazione_serie = detail.getTi_rgl_conservazione_serie().parse();
        this.ti_sel_ud = detail.getTi_sel_ud().parse();
        this.ni_aa_sel_ud = detail.getNi_aa_sel_ud().parse();
        this.ni_unita_doc_volume = detail.getNi_unita_doc_volume().parse();
        this.fl_controllo_consist_obblig = detail.getFl_controllo_consist_obblig().parse();
        this.fl_crea_autom = detail.getFl_crea_autom().parse();
        this.gg_crea_autom = detail.getGg_crea_autom().parse();
        this.aa_ini_crea_autom = detail.getAa_ini_crea_autom().parse();
        this.aa_fin_crea_autom = detail.getAa_fin_crea_autom().parse();
        this.ti_rgl_range_anni_crea_autom = detail.getTi_rgl_range_anni_crea_autom().parse();
        this.ni_aa_sel_ud_suc = detail.getNi_aa_sel_ud_suc().parse();

        String niMmCreaAutomTranscode = detail.getNi_transcoded_mm_crea_autom().parse();
        if (StringUtils.isNotBlank(niMmCreaAutomTranscode)) {
            if (niMmCreaAutomTranscode.equals(CostantiDB.IntervalliMeseCreazioneSerie.DECADE.name())) {
                this.ni_mm_crea_autom = CostantiDB.IntervalliMeseCreazioneSerie.DECADE.getNumSerie();
            } else if (niMmCreaAutomTranscode.equals(CostantiDB.IntervalliMeseCreazioneSerie.QUINDICINA.name())) {
                this.ni_mm_crea_autom = CostantiDB.IntervalliMeseCreazioneSerie.QUINDICINA.getNumSerie();
            } else if (niMmCreaAutomTranscode.equals(CostantiDB.IntervalliMeseCreazioneSerie.MESE.name())) {
                this.ni_mm_crea_autom = CostantiDB.IntervalliMeseCreazioneSerie.MESE.getNumSerie();
            } else if (niMmCreaAutomTranscode.equals(CostantiDB.IntervalliMeseCreazioneSerie.BIMESTRE.name())) {
                this.ni_mm_crea_autom = CostantiDB.IntervalliMeseCreazioneSerie.BIMESTRE.getNumSerie();
            } else if (niMmCreaAutomTranscode.equals(CostantiDB.IntervalliMeseCreazioneSerie.TRIMESTRE.name())) {
                this.ni_mm_crea_autom = CostantiDB.IntervalliMeseCreazioneSerie.TRIMESTRE.getNumSerie();
            } else if (niMmCreaAutomTranscode.equals(CostantiDB.IntervalliMeseCreazioneSerie.QUADRIMESTRE.name())) {
                this.ni_mm_crea_autom = CostantiDB.IntervalliMeseCreazioneSerie.QUADRIMESTRE.getNumSerie();
            } else if (niMmCreaAutomTranscode.equals(CostantiDB.IntervalliMeseCreazioneSerie.SEMESTRE.name())) {
                this.ni_mm_crea_autom = CostantiDB.IntervalliMeseCreazioneSerie.SEMESTRE.getNumSerie();
            }
        } else {
            this.ni_mm_crea_autom = null;
        }

        this.id_tipo_unita_doc_dati_spec = detail.getId_tipo_unita_doc_dati_spec().parse();
        this.nm_tipo_unita_doc_dati_spec = detail.getNm_tipo_unita_doc_dati_spec().parse();
        this.id_tipo_doc_dati_spec = detail.getId_tipo_doc_dati_spec().parse();
        this.nm_tipo_doc_dati_spec = detail.getNm_tipo_doc_dati_spec().parse();
        this.ti_rgl_filtro_ti_doc = detail.getTi_rgl_filtro_ti_doc().parse();
        this.ti_stato_ver_serie_autom = detail.getTi_stato_ver_serie_autom().parse();
    }

    public BigDecimal getId_ambiente() {
        return id_ambiente;
    }

    public void setId_ambiente(BigDecimal id_ambiente) {
        this.id_ambiente = id_ambiente;
    }

    public String getNm_modello_tipo_serie() {
        return nm_modello_tipo_serie;
    }

    public void setNm_modello_tipo_serie(String nm_modello_tipo_serie) {
        this.nm_modello_tipo_serie = nm_modello_tipo_serie;
    }

    public String getDs_modello_tipo_serie() {
        return ds_modello_tipo_serie;
    }

    public void setDs_modello_tipo_serie(String ds_modello_tipo_serie) {
        this.ds_modello_tipo_serie = ds_modello_tipo_serie;
    }

    public Date getDt_istituz() {
        return dt_istituz;
    }

    public void setDt_istituz(Date dt_istituz) {
        this.dt_istituz = dt_istituz;
    }

    public Date getDt_soppres() {
        return dt_soppres;
    }

    public void setDt_soppres(Date dt_soppres) {
        this.dt_soppres = dt_soppres;
    }

    public String getNm_tipo_serie_da_creare() {
        return nm_tipo_serie_da_creare;
    }

    public void setNm_tipo_serie_da_creare(String nm_tipo_serie_da_creare) {
        this.nm_tipo_serie_da_creare = nm_tipo_serie_da_creare;
    }

    public String getTi_rgl_nm_tipo_serie() {
        return ti_rgl_nm_tipo_serie;
    }

    public void setTi_rgl_nm_tipo_serie(String ti_rgl_nm_tipo_serie) {
        this.ti_rgl_nm_tipo_serie = ti_rgl_nm_tipo_serie;
    }

    public String getDs_tipo_serie_da_creare() {
        return ds_tipo_serie_da_creare;
    }

    public void setDs_tipo_serie_da_creare(String ds_tipo_serie_da_creare) {
        this.ds_tipo_serie_da_creare = ds_tipo_serie_da_creare;
    }

    public String getTi_rgl_ds_tipo_serie() {
        return ti_rgl_ds_tipo_serie;
    }

    public void setTi_rgl_ds_tipo_serie(String ti_rgl_ds_tipo_serie) {
        this.ti_rgl_ds_tipo_serie = ti_rgl_ds_tipo_serie;
    }

    public String getCd_serie_da_creare() {
        return cd_serie_da_creare;
    }

    public void setCd_serie_da_creare(String cd_serie_da_creare) {
        this.cd_serie_da_creare = cd_serie_da_creare;
    }

    public String getTi_rgl_cd_serie() {
        return ti_rgl_cd_serie;
    }

    public void setTi_rgl_cd_serie(String ti_rgl_cd_serie) {
        this.ti_rgl_cd_serie = ti_rgl_cd_serie;
    }

    public String getDs_serie_da_creare() {
        return ds_serie_da_creare;
    }

    public void setDs_serie_da_creare(String ds_serie_da_creare) {
        this.ds_serie_da_creare = ds_serie_da_creare;
    }

    public String getTi_rgl_ds_serie() {
        return ti_rgl_ds_serie;
    }

    public void setTi_rgl_ds_serie(String ti_rgl_ds_serie) {
        this.ti_rgl_ds_serie = ti_rgl_ds_serie;
    }

    public String getConserv_unlimited() {
        return conserv_unlimited;
    }

    public void setConserv_unlimited(String conserv_unlimited) {
        this.conserv_unlimited = conserv_unlimited;
    }

    public BigDecimal getNi_anni_conserv() {
        return ni_anni_conserv;
    }

    public void setNi_anni_conserv(BigDecimal ni_anni_conserv) {
        this.ni_anni_conserv = ni_anni_conserv;
    }

    public String getTi_rgl_anni_conserv() {
        return ti_rgl_anni_conserv;
    }

    public void setTi_rgl_anni_conserv(String ti_rgl_anni_conserv) {
        this.ti_rgl_anni_conserv = ti_rgl_anni_conserv;
    }

    public String getTi_conservazione_serie() {
        return ti_conservazione_serie;
    }

    public void setTi_conservazione_serie(String ti_conservazione_serie) {
        this.ti_conservazione_serie = ti_conservazione_serie;
    }

    public String getTi_rgl_conservazione_serie() {
        return ti_rgl_conservazione_serie;
    }

    public void setTi_rgl_conservazione_serie(String ti_rgl_conservazione_serie) {
        this.ti_rgl_conservazione_serie = ti_rgl_conservazione_serie;
    }

    public String getTi_sel_ud() {
        return ti_sel_ud;
    }

    public void setTi_sel_ud(String ti_sel_ud) {
        this.ti_sel_ud = ti_sel_ud;
    }

    public BigDecimal getNi_aa_sel_ud() {
        return ni_aa_sel_ud;
    }

    public void setNi_aa_sel_ud(BigDecimal ni_aa_sel_ud) {
        this.ni_aa_sel_ud = ni_aa_sel_ud;
    }

    public BigDecimal getNi_aa_sel_ud_suc() {
        return ni_aa_sel_ud_suc;
    }

    public void setNi_aa_sel_ud_suc(BigDecimal ni_aa_sel_ud_suc) {
        this.ni_aa_sel_ud_suc = ni_aa_sel_ud_suc;
    }

    public BigDecimal getNi_unita_doc_volume() {
        return ni_unita_doc_volume;
    }

    public void setNi_unita_doc_volume(BigDecimal ni_unita_doc_volume) {
        this.ni_unita_doc_volume = ni_unita_doc_volume;
    }

    public String getFl_controllo_consist_obblig() {
        return fl_controllo_consist_obblig;
    }

    public void setFl_controllo_consist_obblig(String fl_controllo_consist_obblig) {
        this.fl_controllo_consist_obblig = fl_controllo_consist_obblig;
    }

    public String getFl_crea_autom() {
        return fl_crea_autom;
    }

    public void setFl_crea_autom(String fl_crea_autom) {
        this.fl_crea_autom = fl_crea_autom;
    }

    public String getGg_crea_autom() {
        return gg_crea_autom;
    }

    public void setGg_crea_autom(String gg_crea_autom) {
        this.gg_crea_autom = gg_crea_autom;
    }

    public BigDecimal getAa_ini_crea_autom() {
        return aa_ini_crea_autom;
    }

    public void setAa_ini_crea_autom(BigDecimal aa_ini_crea_autom) {
        this.aa_ini_crea_autom = aa_ini_crea_autom;
    }

    public BigDecimal getAa_fin_crea_autom() {
        return aa_fin_crea_autom;
    }

    public void setAa_fin_crea_autom(BigDecimal aa_fin_crea_autom) {
        this.aa_fin_crea_autom = aa_fin_crea_autom;
    }

    public String getTi_rgl_range_anni_crea_autom() {
        return ti_rgl_range_anni_crea_autom;
    }

    public void setTi_rgl_range_anni_crea_autom(String ti_rgl_range_anni_crea_autom) {
        this.ti_rgl_range_anni_crea_autom = ti_rgl_range_anni_crea_autom;
    }

    public BigDecimal getNi_mm_crea_autom() {
        return ni_mm_crea_autom;
    }

    public void setNi_mm_crea_autom(BigDecimal ni_mm_crea_autom) {
        this.ni_mm_crea_autom = ni_mm_crea_autom;
    }

    public BigDecimal getId_tipo_unita_doc_dati_spec() {
        return id_tipo_unita_doc_dati_spec;
    }

    public void setId_tipo_unita_doc_dati_spec(BigDecimal id_tipo_unita_doc_dati_spec) {
        this.id_tipo_unita_doc_dati_spec = id_tipo_unita_doc_dati_spec;
    }

    public String getNm_tipo_unita_doc_dati_spec() {
        return nm_tipo_unita_doc_dati_spec;
    }

    public void setNm_tipo_unita_doc_dati_spec(String nm_tipo_unita_doc_dati_spec) {
        this.nm_tipo_unita_doc_dati_spec = nm_tipo_unita_doc_dati_spec;
    }

    public BigDecimal getId_tipo_doc_dati_spec() {
        return id_tipo_doc_dati_spec;
    }

    public void setId_tipo_doc_dati_spec(BigDecimal id_tipo_doc_dati_spec) {
        this.id_tipo_doc_dati_spec = id_tipo_doc_dati_spec;
    }

    public String getNm_tipo_doc_dati_spec() {
        return nm_tipo_doc_dati_spec;
    }

    public void setNm_tipo_doc_dati_spec(String nm_tipo_doc_dati_spec) {
        this.nm_tipo_doc_dati_spec = nm_tipo_doc_dati_spec;
    }

    public String getTi_rgl_filtro_ti_doc() {
        return ti_rgl_filtro_ti_doc;
    }

    public void setTi_rgl_filtro_ti_doc(String ti_rgl_filtro_ti_doc) {
        this.ti_rgl_filtro_ti_doc = ti_rgl_filtro_ti_doc;
    }

    public String getTi_stato_ver_serie_autom() {
        return ti_stato_ver_serie_autom;
    }

    public void setTi_stato_ver_serie_autom(String ti_stato_ver_serie_autom) {
        this.ti_stato_ver_serie_autom = ti_stato_ver_serie_autom;
    }

}
