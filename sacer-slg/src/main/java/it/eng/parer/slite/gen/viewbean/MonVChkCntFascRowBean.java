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

package it.eng.parer.slite.gen.viewbean;

import java.math.BigDecimal;

import it.eng.parer.viewEntity.MonVChkFascByAmb;
import it.eng.parer.viewEntity.MonVChkFascByEnte;
import it.eng.parer.viewEntity.MonVChkFascByStrut;
import it.eng.parer.viewEntity.MonVChkFascByTiFasc;
import it.eng.parer.viewEntity.MonVChkFascKoByAmb;
import it.eng.parer.viewEntity.MonVChkFascKoByEnte;
import it.eng.parer.viewEntity.MonVChkFascKoByStrut;
import it.eng.parer.viewEntity.MonVChkFascKoByTiFasc;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella
 *
 */
public class MonVChkCntFascRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    public static MonVChkCntFascTableDescriptor TABLE_DESCRIPTOR = new MonVChkCntFascTableDescriptor();

    public MonVChkCntFascRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // *** FASCICOLI VERSATI ***
    // fascicoli correnti
    public String getFlFascCorr() {
        return getString("fl_fasc_corr");
    }

    public void setFlFascCorr(String flFascCorr) {
        setObject("fl_fasc_corr", flFascCorr);
    }

    public BigDecimal getNiFascCorr() {
        return getBigDecimal("ni_fasc_corr");
    }

    public void setNiFascCorr(BigDecimal niFascCorr) {
        setObject("ni_fasc_corr", niFascCorr);
        setFlFascCorr(getNiFascCorr() != null && !getNiFascCorr().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // fascicoli 30gg
    public String getFlFasc30gg() {
        return getString("fl_fasc30gg");
    }

    public void setFlFasc30gg(String fl_fasc30gg) {
        setObject("fl_fasc30gg", fl_fasc30gg);
    }

    public BigDecimal getNiFasc30gg() {
        return getBigDecimal("ni_fasc30gg");
    }

    public void setNiFasc30gg(BigDecimal ni_fasc30gg) {
        setObject("ni_fasc30gg", ni_fasc30gg);
        setFlFasc30gg(getNiFasc30gg() != null && !getNiFasc30gg().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // fascicoli B30gg
    public String getFlFascB30gg() {
        return getString("fl_fasc_b30gg");
    }

    public void setFlFascB30gg(String fl_fasc_b30gg) {
        setObject("fl_fasc_b30gg", fl_fasc_b30gg);
    }

    public BigDecimal getNiFascB30gg() {
        return getBigDecimal("ni_fasc_b30gg");
    }

    public void setNiFascB30gg(BigDecimal ni_fasc_b30gg) {
        setObject("ni_fasc_b30gg", ni_fasc_b30gg);
        setFlFascB30gg(getNiFascB30gg() != null && !getNiFascB30gg().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // fascicoli attesa sched correnti
    public String getFlFascAttesaSchedCorr() {
        return getString("fl_fasc_attesa_sched_corr");
    }

    public void setFlFascAttesaSchedCorr(String flFascAttesaSchedCorr) {
        setObject("fl_fasc_attesa_sched_corr", flFascAttesaSchedCorr);
    }

    public BigDecimal getNiFascAttesaSchedCorr() {
        return getBigDecimal("ni_fasc_attesa_sched_corr");
    }

    public void setNiFascAttesaSchedCorr(BigDecimal niFascAttesaSchedCorr) {
        setObject("ni_fasc_attesa_sched_corr", niFascAttesaSchedCorr);
        setFlFascAttesaSchedCorr(
                getNiFascAttesaSchedCorr() != null && !getNiFascAttesaSchedCorr().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // fascicoli attesa sched 30gg
    public String getFlFascAttesaSched30gg() {
        return getString("fl_fasc_attesa_sched30gg");
    }

    public void setFlFascAttesaSched30gg(String fl_fasc_attesa_sched30gg) {
        setObject("fl_fasc_attesa_sched30gg", fl_fasc_attesa_sched30gg);
    }

    public BigDecimal getNiFascAttesaSched30gg() {
        return getBigDecimal("ni_fasc_attesa_sched30gg");
    }

    public void setNiFascAttesaSched30gg(BigDecimal ni_fasc_attesa_sched30gg) {
        setObject("ni_fasc_attesa_sched30gg", ni_fasc_attesa_sched30gg);
        setFlFascAttesaSched30gg(
                getNiFascAttesaSched30gg() != null && !getNiFascAttesaSched30gg().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // fascicoli attesa sched B30gg
    public String getFlFascAttesaSchedB30gg() {
        return getString("fl_fasc_attesa_sched_b30gg");
    }

    public void setFlFascAttesaSchedB30gg(String fl_fasc_attesa_sched_b30gg) {
        setObject("fl_fasc_attesa_sched_b30gg", fl_fasc_attesa_sched_b30gg);
    }

    public BigDecimal getNiFascAttesaSchedB30gg() {
        return getBigDecimal("ni_fasc_attesa_sched_b30gg");
    }

    public void setNiFascAttesaSchedB30gg(BigDecimal ni_fasc_attesa_sched_b30gg) {
        setObject("ni_fasc_attesa_sched_b30gg", ni_fasc_attesa_sched_b30gg);
        setFlFascAttesaSchedB30gg(
                getNiFascAttesaSchedB30gg() != null && !getNiFascAttesaSchedB30gg().equals(BigDecimal.ZERO) ? "1"
                        : "0");
    }

    // fascicoli attesa no sel sched correnti
    public String getFlFascNoselSchedCorr() {
        return getString("fl_fasc_nosel_sched_corr");
    }

    public void setFlFascNoselSchedCorr(String flFascNoselSchedCorr) {
        setObject("fl_fasc_nosel_sched_corr", flFascNoselSchedCorr);
    }

    public BigDecimal getNiFascNoselSchedCorr() {
        return getBigDecimal("ni_fasc_nosel_sched_corr");
    }

    public void setNiFascNoselSchedCorr(BigDecimal niFascNoselSchedCorr) {
        setObject("ni_fasc_nosel_sched_corr", niFascNoselSchedCorr);
        setFlFascNoselSchedCorr(
                getNiFascNoselSchedCorr() != null && !getNiFascNoselSchedCorr().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // fascicoli attesa no sel sched 30gg
    public String getFlFascNoselSched30gg() {
        return getString("fl_fasc_nosel_sched30gg");
    }

    public void setFlFascNoselSched30gg(String fl_fasc_nosel_sched30gg) {
        setObject("fl_fasc_nosel_sched30gg", fl_fasc_nosel_sched30gg);
    }

    public BigDecimal getNiFascNoselSched30gg() {
        return getBigDecimal("ni_fasc_nosel_sched30gg");
    }

    public void setNiFascNoselSched30gg(BigDecimal ni_fasc_nosel_sched30gg) {
        setObject("ni_fasc_nosel_sched30gg", ni_fasc_nosel_sched30gg);
        setFlFascNoselSched30gg(
                getNiFascNoselSched30gg() != null && !getNiFascNoselSched30gg().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // fascicoli attesa no sel sched B30gg
    public String getFlFascNoselSchedB30gg() {
        return getString("fl_fasc_nosel_sched_b30gg");
    }

    public void setFlFascNoselSchedB30gg(String fl_fasc_nosel_sched_b30gg) {
        setObject("fl_fasc_nosel_sched_b30gg", fl_fasc_nosel_sched_b30gg);
    }

    public BigDecimal getNiFascNoselSchedB30gg() {
        return getBigDecimal("ni_fasc_nosel_sched_b30gg");
    }

    public void setNiFascNoselSchedB30gg(BigDecimal ni_fasc_nosel_sched_b30gg) {
        setObject("ni_fasc_nosel_sched_b30gg", ni_fasc_nosel_sched_b30gg);
        setFlFascNoselSchedB30gg(
                getNiFascNoselSchedB30gg() != null && !getNiFascNoselSchedB30gg().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // *** FASCICOLI FALLITI ***
    // Falliti
    public String getFlFascKoFallCorr() {
        // return getString("fl_fasc_ko_fall_corr");
        return getNiFascKoFallCorr().equals(BigDecimal.ZERO) ? "0" : "1";
    }

    public void setFlFascKoFallCorr(String flFascKoFallCorr) {
        setObject("fl_fasc_ko_fall_corr", flFascKoFallCorr);
    }

    public BigDecimal getNiFascKoFallCorr() {
        return getBigDecimal("ni_fasc_ko_fall_corr");
    }

    public void setNiFascKoFallCorr(BigDecimal niFascKoFallCorr) {
        setObject("ni_fasc_ko_fall_corr", niFascKoFallCorr);
        setFlFascKoFallCorr(
                getNiFascKoFallCorr() != null && !getNiFascKoFallCorr().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // Falliti 30gg
    public String getFlFascKoFall30gg() {
        return getString("fl_fasc_ko_fall30gg");
    }

    public void setFlFascKoFall30gg(String fl_fasc_ko_fall30gg) {
        setObject("fl_fasc_ko_fall30gg", fl_fasc_ko_fall30gg);
    }

    public BigDecimal getNiFascKoFall30gg() {
        return getBigDecimal("ni_fasc_ko_fall30gg");
    }

    public void setNiFascKoFall30gg(BigDecimal ni_fasc_ko_fall30gg) {
        setObject("ni_fasc_ko_fall30gg", ni_fasc_ko_fall30gg);
        setFlFascKoFall30gg(
                getNiFascKoFall30gg() != null && !getNiFascKoFall30gg().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // Falliti B30gg
    public String getFlFascKoFallB30gg() {
        return getString("fl_fasc_ko_fallB30gg");
    }

    public void setFlFascKoFallB30gg(String fl_fasc_ko_fallB30gg) {
        setObject("fl_fasc_ko_fallB30gg", fl_fasc_ko_fallB30gg);
    }

    public BigDecimal getNiFascKoFallB30gg() {
        return getBigDecimal("ni_fasc_ko_fallB30gg");
    }

    public void setNiFascKoFallB30gg(BigDecimal ni_fasc_ko_fallB30gg) {
        setObject("ni_fasc_ko_fallB30gg", ni_fasc_ko_fallB30gg);
        setFlFascKoFallB30gg(
                getNiFascKoFallB30gg() != null && !getNiFascKoFallB30gg().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // Verificati
    public String getFlFascKoVerifCorr() {
        return getString("fl_fasc_ko_verif_corr");
    }

    public void setFlFascKoVerifCorr(String flFascKoVerifCorr) {
        setObject("fl_fasc_ko_verif_corr", flFascKoVerifCorr);
    }

    public BigDecimal getNiFascKoVerifCorr() {
        return getBigDecimal("ni_fasc_ko_verif_corr");
    }

    public void setNiFascKoVerifCorr(BigDecimal niFascKoVerifCorr) {
        setObject("ni_fasc_ko_verif_corr", niFascKoVerifCorr);
        setFlFascKoVerifCorr(
                getNiFascKoVerifCorr() != null && !getNiFascKoVerifCorr().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // Verificati 30gg
    public String getFlFascKoVerif30gg() {
        return getString("fl_fasc_ko_verif30gg");
    }

    public void setFlFascKoVerif30gg(String fl_fasc_ko_verif30gg) {
        setObject("fl_fasc_ko_verif30gg", fl_fasc_ko_verif30gg);
    }

    public BigDecimal getNiFascKoVerif30gg() {
        return getBigDecimal("ni_fasc_ko_verif30gg");
    }

    public void setNiFascKoVerif30gg(BigDecimal ni_fasc_ko_verif30gg) {
        setObject("ni_fasc_ko_verif30gg", ni_fasc_ko_verif30gg);
        setFlFascKoVerif30gg(
                getNiFascKoVerif30gg() != null && !getNiFascKoVerif30gg().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // Verificati B30gg
    public String getFlFascKoVerifB30gg() {
        return getString("fl_fasc_ko_verifB30gg");
    }

    public void setFlFascKoVerifB30gg(String fl_fasc_ko_verifB30gg) {
        setObject("fl_fasc_ko_verifB30gg", fl_fasc_ko_verifB30gg);
    }

    public BigDecimal getNiFascKoVerifB30gg() {
        return getBigDecimal("ni_fasc_ko_verifB30gg");
    }

    public void setNiFascKoVerifB30gg(BigDecimal ni_fasc_ko_verifB30gg) {
        setObject("ni_fasc_ko_verifB30gg", ni_fasc_ko_verifB30gg);
        setFlFascKoVerifB30gg(
                getNiFascKoVerifB30gg() != null && !getNiFascKoVerifB30gg().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // Non verificati
    public String getFlFascKoNonVerifCorr() {
        return getString("fl_fasc_ko_non_verif_corr");
    }

    public void setFlFascKoNonVerifCorr(String flFascKoNonVerifCorr) {
        setObject("fl_fasc_ko_non_verif_corr", flFascKoNonVerifCorr);
    }

    public BigDecimal getNiFascKoNonVerifCorr() {
        return getBigDecimal("ni_fasc_ko_non_verif_corr");
    }

    public void setNiFascKoNonVerifCorr(BigDecimal niFascKoNonVerifCorr) {
        setObject("ni_fasc_ko_non_verif_corr", niFascKoNonVerifCorr);
        setFlFascKoNonVerifCorr(
                getNiFascKoNonVerifCorr() != null && !getNiFascKoNonVerifCorr().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // Non verificati 30gg
    public String getFlFascKoNonVerif30gg() {
        return getString("fl_fasc_ko_non_verif30gg");
    }

    public void setFlFascKoNonVerif30gg(String fl_fasc_ko_non_verif30gg) {
        setObject("fl_fasc_ko_non_verif30gg", fl_fasc_ko_non_verif30gg);
    }

    public BigDecimal getNiFascKoNonVerif30gg() {
        return getBigDecimal("ni_fasc_ko_non_verif30gg");
    }

    public void setNiFascKoNonVerif30gg(BigDecimal ni_fasc_ko_non_verif30gg) {
        setObject("ni_fasc_ko_non_verif30gg", ni_fasc_ko_non_verif30gg);
        setFlFascKoNonVerif30gg(
                getNiFascKoNonVerif30gg() != null && !getNiFascKoNonVerif30gg().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // Non verificati B30gg
    public String getFlFascKoNonVerifB30gg() {
        return getString("fl_fasc_ko_non_verifB30gg");
    }

    public void setFlFascKoNonVerifB30gg(String fl_fasc_ko_non_verifB30gg) {
        setObject("fl_fasc_ko_non_verifB30gg", fl_fasc_ko_non_verifB30gg);
    }

    public BigDecimal getNiFascKoNonVerifB30gg() {
        return getBigDecimal("ni_fasc_ko_non_verifB30gg");
    }

    public void setNiFascKoNonVerifB30gg(BigDecimal ni_fasc_ko_non_verifB30gg) {
        setObject("ni_fasc_ko_non_verifB30gg", ni_fasc_ko_non_verifB30gg);
        setFlFascKoNonVerifB30gg(
                getNiFascKoNonVerifB30gg() != null && !getNiFascKoNonVerifB30gg().equals(BigDecimal.ZERO) ? "1" : "0");
    }

    // Non risolubili
    public String getFlFascKoNonRisolubCorr() {
        return getString("fl_fasc_ko_non_risolub_corr");
    }

    public void setFlFascKoNonRisolubCorr(String flFascKoNonRisolubCorr) {
        setObject("fl_fasc_ko_non_risolub_corr", flFascKoNonRisolubCorr);
    }

    public BigDecimal getNiFascKoNonRisolubCorr() {
        return getBigDecimal("ni_fasc_ko_non_risolub_corr");
    }

    public void setNiFascKoNonRisolubCorr(BigDecimal niFascKoNonRisolubCorr) {
        setObject("ni_fasc_ko_non_risolub_corr", niFascKoNonRisolubCorr);
        setFlFascKoNonRisolubCorr(
                getNiFascKoNonRisolubCorr() != null && !getNiFascKoNonRisolubCorr().equals(BigDecimal.ZERO) ? "1"
                        : "0");
    }

    // Non risolubili 30gg
    public String getFlFascKoNonRisolub30gg() {
        return getString("fl_fasc_ko_non_risolub30gg");
    }

    public void setFlFascKoNonRisolub30gg(String fl_fasc_ko_non_risolub30gg) {
        setObject("fl_fasc_ko_non_risolub30gg", fl_fasc_ko_non_risolub30gg);
    }

    public BigDecimal getNiFascKoNonRisolub30gg() {
        return getBigDecimal("ni_fasc_ko_non_risolub30gg");
    }

    public void setNiFascKoNonRisolub30gg(BigDecimal ni_fasc_ko_non_risolub30gg) {
        setObject("ni_fasc_ko_non_risolub30gg", ni_fasc_ko_non_risolub30gg);
        setFlFascKoNonRisolub30gg(
                getNiFascKoNonRisolub30gg() != null && !getNiFascKoNonRisolub30gg().equals(BigDecimal.ZERO) ? "1"
                        : "0");
    }

    // Non risolubili B30gg
    public String getFlFascKoNonRisolubB30gg() {
        return getString("fl_fasc_ko_non_risolubB30gg");
    }

    public void setFlFascKoNonRisolubB30gg(String fl_fasc_ko_non_risolubB30gg) {
        setObject("fl_fasc_ko_non_risolubB30gg", fl_fasc_ko_non_risolubB30gg);
    }

    public BigDecimal getNiFascKoNonRisolubB30gg() {
        return getBigDecimal("ni_fasc_ko_non_risolubB30gg");
    }

    public void setNiFascKoNonRisolubB30gg(BigDecimal ni_fasc_ko_non_risolubB30gg) {
        setObject("ni_fasc_ko_non_risolubB30gg", ni_fasc_ko_non_risolubB30gg);
        setFlFascKoNonRisolubB30gg(
                getNiFascKoNonRisolubB30gg() != null && !getNiFascKoNonRisolubB30gg().equals(BigDecimal.ZERO) ? "1"
                        : "0");
    }

    public void resetCounters() {
        resetCountersVersati();
        resetCountersFalliti();
    }

    public void resetCountersVersati() {
        // Fascicoli versati
        this.setNiFascCorr(BigDecimal.ZERO);
        this.setNiFasc30gg(BigDecimal.ZERO);
        this.setNiFascAttesaSchedCorr(BigDecimal.ZERO);
        this.setNiFascAttesaSched30gg(BigDecimal.ZERO);
        this.setNiFascNoselSchedCorr(BigDecimal.ZERO);
        this.setNiFascNoselSched30gg(BigDecimal.ZERO);
        this.setNiFascB30gg(BigDecimal.ZERO);
        this.setNiFascNoselSchedB30gg(BigDecimal.ZERO);
        this.setNiFascAttesaSchedB30gg(BigDecimal.ZERO);

    }

    public void resetCountersFalliti() {
        // Falliti
        this.setNiFascKoFallCorr(BigDecimal.ZERO);
        this.setNiFascKoFall30gg(BigDecimal.ZERO);
        this.setNiFascKoFallB30gg(BigDecimal.ZERO);
        this.setNiFascKoNonRisolubCorr(BigDecimal.ZERO);
        this.setNiFascKoNonRisolub30gg(BigDecimal.ZERO);
        this.setNiFascKoNonRisolubB30gg(BigDecimal.ZERO);
        this.setNiFascKoVerifCorr(BigDecimal.ZERO);
        this.setNiFascKoVerif30gg(BigDecimal.ZERO);
        this.setNiFascKoVerifB30gg(BigDecimal.ZERO);
        this.setNiFascKoNonVerifCorr(BigDecimal.ZERO);
        this.setNiFascKoNonVerif30gg(BigDecimal.ZERO);
        this.setNiFascKoNonVerifB30gg(BigDecimal.ZERO);
    }

    @Override
    public void entityToRowBean(Object obj) {
        MonVChkFasc entity = null;
        if (obj instanceof MonVChkFascByAmb) {
            entity = new MonVChkFasc((MonVChkFascByAmb) obj);
        } else if (obj instanceof MonVChkFascByStrut) {
            entity = new MonVChkFasc((MonVChkFascByStrut) obj);
        } else if (obj instanceof MonVChkFascByEnte) {
            entity = new MonVChkFasc((MonVChkFascByEnte) obj);
        } else if (obj instanceof MonVChkFascByTiFasc) {
            entity = new MonVChkFasc((MonVChkFascByTiFasc) obj);
        }
        if (entity != null) {
            this.setFlFascCorr(entity.getFlFascCorr());
            this.setFlFasc30gg(entity.getFlFasc30gg());
            this.setFlFascB30gg(entity.getFlFascB30gg());
            this.setFlFascAttesaSchedCorr(entity.getFlFascAttesaSchedCorr());
            this.setFlFascAttesaSched30gg(entity.getFlFascAttesaSched30gg());
            this.setFlFascAttesaSchedB30gg(entity.getFlFascAttesaSchedB30gg());
            this.setFlFascNoselSchedCorr(entity.getFlFascNoselSchedCorr());
            this.setFlFascNoselSched30gg(entity.getFlFascNoselSched30gg());
            this.setFlFascNoselSchedB30gg(entity.getFlFascNoselSchedB30gg());
        } else {
            MonVChkFascFalliti entityFalliti = null;
            if (obj instanceof MonVChkFascKoByAmb) {
                entityFalliti = new MonVChkFascFalliti((MonVChkFascKoByAmb) obj);
            } else if (obj instanceof MonVChkFascKoByStrut) {
                entityFalliti = new MonVChkFascFalliti((MonVChkFascKoByStrut) obj);
            } else if (obj instanceof MonVChkFascKoByEnte) {
                entityFalliti = new MonVChkFascFalliti((MonVChkFascKoByEnte) obj);
            } else if (obj instanceof MonVChkFascKoByTiFasc) {
                entityFalliti = new MonVChkFascFalliti((MonVChkFascKoByTiFasc) obj);
            } else {
                throw new IllegalArgumentException("Errore inaspettato nel casting dell'entity");
            }
            this.setFlFascKoVerifCorr(entityFalliti.getFlFascKoVerifCorr());
            this.setFlFascKoVerif30gg(entityFalliti.getFlFascKoVerif30gg());
            this.setFlFascKoVerifB30gg(entityFalliti.getFlFascKoVerifB30gg());
            this.setFlFascKoNonVerifCorr(entityFalliti.getFlFascKoNonVerifCorr());
            this.setFlFascKoNonVerif30gg(entityFalliti.getFlFascKoNonVerif30gg());
            this.setFlFascKoNonVerifB30gg(entityFalliti.getFlFascKoNonVerifB30gg());
            this.setFlFascKoNonRisolubCorr(entityFalliti.getFlFascKoNonRisolubCorr());
            this.setFlFascKoNonRisolub30gg(entityFalliti.getFlFascKoNonRisolub30gg());
            this.setFlFascKoNonRisolubB30gg(entityFalliti.getFlFascKoNonRisolubB30gg());
            // Falliti è calcolato come la somma delle altre righe
            if (this.getFlFascKoVerifCorr().equals("1") || this.getFlFascKoNonVerifCorr().equals("1")
                    || this.getFlFascKoNonRisolubCorr().equals("1")) {
                this.setFlFascKoFallCorr("1");
            } else {
                this.setFlFascKoFallCorr("0");
            }
            if (this.getFlFascKoVerif30gg().equals("1") || this.getFlFascKoNonVerif30gg().equals("1")
                    || this.getFlFascKoNonRisolub30gg().equals("1")) {
                this.setFlFascKoFall30gg("1");
            } else {
                this.setFlFascKoFall30gg("0");
            }
            if (this.getFlFascKoVerifB30gg().equals("1") || this.getFlFascKoNonVerifB30gg().equals("1")
                    || this.getFlFascKoNonRisolubB30gg().equals("1")) {
                this.setFlFascKoFallB30gg("1");
            } else {
                this.setFlFascKoFallB30gg("0");
            }
        }
    }

    public MonVChkFasc rowBeanToEntityMonVChkFasc() {
        MonVChkFasc entity = new MonVChkFasc();
        entity.setFlFascCorr(this.getFlFascCorr());
        entity.setFlFasc30gg(this.getFlFasc30gg());
        entity.setFlFascB30gg(this.getFlFascB30gg());

        entity.setFlFascAttesaSchedCorr(this.getFlFascAttesaSchedCorr());
        entity.setFlFascAttesaSched30gg(this.getFlFascAttesaSched30gg());
        entity.setFlFascAttesaSchedB30gg(this.getFlFascAttesaSchedB30gg());

        entity.setFlFascNoselSchedCorr(this.getFlFascNoselSchedCorr());
        entity.setFlFascNoselSched30gg(this.getFlFascNoselSched30gg());
        entity.setFlFascNoselSchedB30gg(this.getFlFascNoselSchedB30gg());
        return entity;
    }

    public MonVChkFascFalliti rowBeanToEntityMonVChkFascFalliti() {
        MonVChkFascFalliti entity = new MonVChkFascFalliti();
        entity.setFlFascKoVerifCorr(this.getFlFascKoVerifCorr());
        entity.setFlFascKoVerif30gg(this.getFlFascKoVerif30gg());
        entity.setFlFascKoVerifB30gg(this.getFlFascKoVerifB30gg());

        entity.setFlFascKoNonVerifCorr(this.getFlFascKoNonVerifCorr());
        entity.setFlFascKoNonVerif30gg(this.getFlFascKoNonVerif30gg());
        entity.setFlFascKoNonVerifB30gg(this.getFlFascKoNonVerifB30gg());

        entity.setFlFascKoNonRisolubCorr(this.getFlFascKoNonRisolubCorr());
        entity.setFlFascKoNonRisolub30gg(this.getFlFascKoNonRisolub30gg());
        entity.setFlFascKoNonRisolubB30gg(this.getFlFascKoNonRisolubB30gg());

        return entity;
    }

    // gestione della paginazione
    public void setRownum(Integer rownum) {
        setObject("rownum", rownum);
    }

    public Integer getRownum() {
        return Integer.parseInt(getObject("rownum").toString());
    }

    public void setRnum(Integer rnum) {
        setObject("rnum", rnum);
    }

    public Integer getRnum() {
        return Integer.parseInt(getObject("rnum").toString());
    }

    public void setNumrecords(Integer numRecords) {
        setObject("numrecords", numRecords);
    }

    public Integer getNumrecords() {
        return Integer.parseInt(getObject("numrecords").toString());
    }

    @Override
    public Object rowBeanToEntity() {
        return null;
    }

    public class MonVChkFasc {

        private String flFascCorr;
        private String flFasc30gg;
        private String flFascB30gg;
        private String flFascAttesaSchedCorr;
        private String flFascAttesaSched30gg;
        private String flFascAttesaSchedB30gg;
        private String flFascNoselSchedCorr;
        private String flFascNoselSched30gg;
        private String flFascNoselSchedB30gg;

        public MonVChkFasc() {
        }

        public MonVChkFasc(MonVChkFascByTiFasc entity) {
            this.flFascCorr = entity.getFlFascCorr();
            this.flFasc30gg = entity.getFlFasc30gg();
            this.flFascB30gg = entity.getFlFascB30gg();
            this.flFascAttesaSchedCorr = entity.getFlFascAttesaSchedCorr();
            this.flFascAttesaSched30gg = entity.getFlFascAttesaSched30gg();
            this.flFascAttesaSchedB30gg = entity.getFlFascAttesaSchedB30gg();
            this.flFascNoselSchedCorr = entity.getFlFascNoselSchedCorr();
            this.flFascNoselSched30gg = entity.getFlFascNoselSched30gg();
            this.flFascNoselSchedB30gg = entity.getFlFascNoselSchedB30gg();
        }

        public MonVChkFasc(MonVChkFascByAmb entity) {
            this.flFascCorr = entity.getFlFascCorr();
            this.flFasc30gg = entity.getFlFasc30gg();
            this.flFascB30gg = entity.getFlFascB30gg();
            this.flFascAttesaSchedCorr = entity.getFlFascAttesaSchedCorr();
            this.flFascAttesaSched30gg = entity.getFlFascAttesaSched30gg();
            this.flFascAttesaSchedB30gg = entity.getFlFascAttesaSchedB30gg();
            this.flFascNoselSchedCorr = entity.getFlFascNoselSchedCorr();
            this.flFascNoselSched30gg = entity.getFlFascNoselSched30gg();
            this.flFascNoselSchedB30gg = entity.getFlFascNoselSchedB30gg();
        }

        public MonVChkFasc(MonVChkFascByEnte entity) {
            this.flFascCorr = entity.getFlFascCorr();
            this.flFasc30gg = entity.getFlFasc30gg();
            this.flFascB30gg = entity.getFlFascB30gg();
            this.flFascAttesaSchedCorr = entity.getFlFascAttesaSchedCorr();
            this.flFascAttesaSched30gg = entity.getFlFascAttesaSched30gg();
            this.flFascAttesaSchedB30gg = entity.getFlFascAttesaSchedB30gg();
            this.flFascNoselSchedCorr = entity.getFlFascNoselSchedCorr();
            this.flFascNoselSched30gg = entity.getFlFascNoselSched30gg();
            this.flFascNoselSchedB30gg = entity.getFlFascNoselSchedB30gg();
        }

        public MonVChkFasc(MonVChkFascByStrut entity) {
            this.flFascCorr = entity.getFlFascCorr();
            this.flFasc30gg = entity.getFlFasc30gg();
            this.flFascB30gg = entity.getFlFascB30gg();
            this.flFascAttesaSchedCorr = entity.getFlFascAttesaSchedCorr();
            this.flFascAttesaSched30gg = entity.getFlFascAttesaSched30gg();
            this.flFascAttesaSchedB30gg = entity.getFlFascAttesaSchedB30gg();
            this.flFascNoselSchedCorr = entity.getFlFascNoselSchedCorr();
            this.flFascNoselSched30gg = entity.getFlFascNoselSched30gg();
            this.flFascNoselSchedB30gg = entity.getFlFascNoselSchedB30gg();
        }

        public String getFlFasc30gg() {
            return this.flFasc30gg;
        }

        public String getFlFascB30gg() {
            return this.flFascB30gg;
        }

        public String getFlFascAttesaSched30gg() {
            return this.flFascAttesaSched30gg;
        }

        public String getFlFascAttesaSchedB30gg() {
            return this.flFascAttesaSchedB30gg;
        }

        public String getFlFascAttesaSchedCorr() {
            return this.flFascAttesaSchedCorr;
        }

        public String getFlFascCorr() {
            return this.flFascCorr;
        }

        public String getFlFascNoselSched30gg() {
            return this.flFascNoselSched30gg;
        }

        public String getFlFascNoselSchedB30gg() {
            return this.flFascNoselSchedB30gg;
        }

        public String getFlFascNoselSchedCorr() {
            return this.flFascNoselSchedCorr;
        }

        public void setFlFasc30gg(String flFasc30gg) {
            this.flFasc30gg = flFasc30gg;
        }

        public void setFlFascB30gg(String flFascB30gg) {
            this.flFascB30gg = flFascB30gg;
        }

        public void setFlFascAttesaSched30gg(String flFascAttesaSched30gg) {
            this.flFascAttesaSched30gg = flFascAttesaSched30gg;
        }

        public void setFlFascAttesaSchedB30gg(String flFascAttesaSchedB30gg) {
            this.flFascAttesaSchedB30gg = flFascAttesaSchedB30gg;
        }

        public void setFlFascAttesaSchedCorr(String flFascAttesaSchedCorr) {
            this.flFascAttesaSchedCorr = flFascAttesaSchedCorr;
        }

        public void setFlFascCorr(String flFascCorr) {
            this.flFascCorr = flFascCorr;
        }

        public void setFlFascNoselSched30gg(String flFascNoselSched30gg) {
            this.flFascNoselSched30gg = flFascNoselSched30gg;
        }

        public void setFlFascNoselSchedB30gg(String flFascNoselSchedB30gg) {
            this.flFascNoselSchedB30gg = flFascNoselSchedB30gg;
        }

        public void setFlFascNoselSchedCorr(String flFascNoselSchedCorr) {
            this.flFascNoselSchedCorr = flFascNoselSchedCorr;
        }
    }

    public class MonVChkFascFalliti {

        private String flFascKoNonRisolub30gg;
        private String flFascKoNonRisolubB30gg;
        private String flFascKoNonRisolubCorr;
        private String flFascKoNonVerif30gg;
        private String flFascKoNonVerifB30gg;
        private String flFascKoNonVerifCorr;
        private String flFascKoVerif30gg;
        private String flFascKoVerifB30gg;
        private String flFascKoVerifCorr;

        public MonVChkFascFalliti() {
        }

        public MonVChkFascFalliti(MonVChkFascKoByAmb entity) {
            this.flFascKoNonRisolub30gg = entity.getFlFascKoNonRisolub30gg();
            this.flFascKoNonRisolubB30gg = entity.getFlFascKoNonRisolubB30gg();
            this.flFascKoNonRisolubCorr = entity.getFlFascKoNonRisolubCorr();
            this.flFascKoNonVerif30gg = entity.getFlFascKoNonVerif30gg();
            this.flFascKoNonVerifB30gg = entity.getFlFascKoNonVerifB30gg();
            this.flFascKoNonVerifCorr = entity.getFlFascKoNonVerifCorr();
            this.flFascKoVerif30gg = entity.getFlFascKoVerif30gg();
            this.flFascKoVerifB30gg = entity.getFlFascKoVerifB30gg();
            this.flFascKoVerifCorr = entity.getFlFascKoVerifCorr();
        }

        public MonVChkFascFalliti(MonVChkFascKoByEnte entity) {
            this.flFascKoNonRisolub30gg = entity.getFlFascKoNonRisolub30gg();
            this.flFascKoNonRisolubB30gg = entity.getFlFascKoNonRisolubB30gg();
            this.flFascKoNonRisolubCorr = entity.getFlFascKoNonRisolubCorr();
            this.flFascKoNonVerif30gg = entity.getFlFascKoNonVerif30gg();
            this.flFascKoNonVerifB30gg = entity.getFlFascKoNonVerifB30gg();
            this.flFascKoNonVerifCorr = entity.getFlFascKoNonVerifCorr();
            this.flFascKoVerif30gg = entity.getFlFascKoVerif30gg();
            this.flFascKoVerifB30gg = entity.getFlFascKoVerifB30gg();
            this.flFascKoVerifCorr = entity.getFlFascKoVerifCorr();
        }

        public MonVChkFascFalliti(MonVChkFascKoByStrut entity) {
            this.flFascKoNonRisolub30gg = entity.getFlFascKoNonRisolub30gg();
            this.flFascKoNonRisolubB30gg = entity.getFlFascKoNonRisolubB30gg();
            this.flFascKoNonRisolubCorr = entity.getFlFascKoNonRisolubCorr();
            this.flFascKoNonVerif30gg = entity.getFlFascKoNonVerif30gg();
            this.flFascKoNonVerifB30gg = entity.getFlFascKoNonVerifB30gg();
            this.flFascKoNonVerifCorr = entity.getFlFascKoNonVerifCorr();
            this.flFascKoVerif30gg = entity.getFlFascKoVerif30gg();
            this.flFascKoVerifB30gg = entity.getFlFascKoVerifB30gg();
            this.flFascKoVerifCorr = entity.getFlFascKoVerifCorr();
        }

        public MonVChkFascFalliti(MonVChkFascKoByTiFasc entity) {
            this.flFascKoNonRisolub30gg = entity.getFlFascKoNonRisolub30gg();
            this.flFascKoNonRisolubB30gg = entity.getFlFascKoNonRisolubB30gg();
            this.flFascKoNonRisolubCorr = entity.getFlFascKoNonRisolubCorr();
            this.flFascKoNonVerif30gg = entity.getFlFascKoNonVerif30gg();
            this.flFascKoNonVerifB30gg = entity.getFlFascKoNonVerifB30gg();
            this.flFascKoNonVerifCorr = entity.getFlFascKoNonVerifCorr();
            this.flFascKoVerif30gg = entity.getFlFascKoVerif30gg();
            this.flFascKoVerifB30gg = entity.getFlFascKoVerifB30gg();
            this.flFascKoVerifCorr = entity.getFlFascKoVerifCorr();
        }

        public String getFlFascKoNonRisolub30gg() {
            return flFascKoNonRisolub30gg;
        }

        public void setFlFascKoNonRisolub30gg(String flFascKoNonRisolub30gg) {
            this.flFascKoNonRisolub30gg = flFascKoNonRisolub30gg;
        }

        public String getFlFascKoNonRisolubB30gg() {
            return flFascKoNonRisolubB30gg;
        }

        public void setFlFascKoNonRisolubB30gg(String flFascKoNonRisolubB30gg) {
            this.flFascKoNonRisolubB30gg = flFascKoNonRisolubB30gg;
        }

        public String getFlFascKoNonRisolubCorr() {
            return flFascKoNonRisolubCorr;
        }

        public void setFlFascKoNonRisolubCorr(String flFascKoNonRisolubCorr) {
            this.flFascKoNonRisolubCorr = flFascKoNonRisolubCorr;
        }

        public String getFlFascKoNonVerif30gg() {
            return flFascKoNonVerif30gg;
        }

        public void setFlFascKoNonVerif30gg(String flFascKoNonVerif30gg) {
            this.flFascKoNonVerif30gg = flFascKoNonVerif30gg;
        }

        public String getFlFascKoNonVerifB30gg() {
            return flFascKoNonVerifB30gg;
        }

        public void setFlFascKoNonVerifB30gg(String flFascKoNonVerifB30gg) {
            this.flFascKoNonVerifB30gg = flFascKoNonVerifB30gg;
        }

        public String getFlFascKoNonVerifCorr() {
            return flFascKoNonVerifCorr;
        }

        public void setFlFascKoNonVerifCorr(String flFascKoNonVerifCorr) {
            this.flFascKoNonVerifCorr = flFascKoNonVerifCorr;
        }

        public String getFlFascKoVerif30gg() {
            return flFascKoVerif30gg;
        }

        public void setFlFascKoVerif30gg(String flFascKoVerif30gg) {
            this.flFascKoVerif30gg = flFascKoVerif30gg;
        }

        public String getFlFascKoVerifB30gg() {
            return flFascKoVerifB30gg;
        }

        public void setFlFascKoVerifB30gg(String flFascKoVerifB30gg) {
            this.flFascKoVerifB30gg = flFascKoVerifB30gg;
        }

        public String getFlFascKoVerifCorr() {
            return flFascKoVerifCorr;
        }

        public void setFlFascKoVerifCorr(String flFascKoVerifCorr) {
            this.flFascKoVerifCorr = flFascKoVerifCorr;
        }

    }

}
