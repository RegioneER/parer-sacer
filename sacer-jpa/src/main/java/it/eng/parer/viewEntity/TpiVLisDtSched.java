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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the TPI_V_LIS_DT_SCHED database table.
 * 
 */
@Entity
@Table(name = "TPI_V_LIS_DT_SCHED")
public class TpiVLisDtSched implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date dtSched;
    private String flAnomaliaArkPrim;
    private String flAnomaliaArkSecond;
    private String flAnomaliaBackupPrim;
    private String flAnomaliaBackupSecond;
    private String flAnomaliaCopiaPrim;
    private String flAnomaliaCopiaSecond;
    private String flAnomaliaMigratePrim;
    private String flAnomaliaMigrateSecond;
    private String flAnomaliaRiArkPrim;
    private String flAnomaliaRiArkSecond;
    private String flMigrazInCorso;
    private String flPresenzaSecondario;
    private BigDecimal idDtSched;
    private String tiStatoDtSched;

    public TpiVLisDtSched() {/* Hibernate */
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_SCHED")
    public Date getDtSched() {
        return this.dtSched;
    }

    public void setDtSched(Date dtSched) {
        this.dtSched = dtSched;
    }

    @Column(name = "FL_ANOMALIA_ARK_PRIM", columnDefinition = "char(1)")
    public String getFlAnomaliaArkPrim() {
        return this.flAnomaliaArkPrim;
    }

    public void setFlAnomaliaArkPrim(String flAnomaliaArkPrim) {
        this.flAnomaliaArkPrim = flAnomaliaArkPrim;
    }

    @Column(name = "FL_ANOMALIA_ARK_SECOND", columnDefinition = "char(1)")
    public String getFlAnomaliaArkSecond() {
        return this.flAnomaliaArkSecond;
    }

    public void setFlAnomaliaArkSecond(String flAnomaliaArkSecond) {
        this.flAnomaliaArkSecond = flAnomaliaArkSecond;
    }

    @Column(name = "FL_ANOMALIA_BACKUP_PRIM", columnDefinition = "char(1)")
    public String getFlAnomaliaBackupPrim() {
        return this.flAnomaliaBackupPrim;
    }

    public void setFlAnomaliaBackupPrim(String flAnomaliaBackupPrim) {
        this.flAnomaliaBackupPrim = flAnomaliaBackupPrim;
    }

    @Column(name = "FL_ANOMALIA_BACKUP_SECOND", columnDefinition = "char(1)")
    public String getFlAnomaliaBackupSecond() {
        return this.flAnomaliaBackupSecond;
    }

    public void setFlAnomaliaBackupSecond(String flAnomaliaBackupSecond) {
        this.flAnomaliaBackupSecond = flAnomaliaBackupSecond;
    }

    @Column(name = "FL_ANOMALIA_COPIA_PRIM", columnDefinition = "char(1)")
    public String getFlAnomaliaCopiaPrim() {
        return this.flAnomaliaCopiaPrim;
    }

    public void setFlAnomaliaCopiaPrim(String flAnomaliaCopiaPrim) {
        this.flAnomaliaCopiaPrim = flAnomaliaCopiaPrim;
    }

    @Column(name = "FL_ANOMALIA_COPIA_SECOND", columnDefinition = "char(1)")
    public String getFlAnomaliaCopiaSecond() {
        return this.flAnomaliaCopiaSecond;
    }

    public void setFlAnomaliaCopiaSecond(String flAnomaliaCopiaSecond) {
        this.flAnomaliaCopiaSecond = flAnomaliaCopiaSecond;
    }

    @Column(name = "FL_ANOMALIA_MIGRATE_PRIM", columnDefinition = "char(1)")
    public String getFlAnomaliaMigratePrim() {
        return this.flAnomaliaMigratePrim;
    }

    public void setFlAnomaliaMigratePrim(String flAnomaliaMigratePrim) {
        this.flAnomaliaMigratePrim = flAnomaliaMigratePrim;
    }

    @Column(name = "FL_ANOMALIA_MIGRATE_SECOND", columnDefinition = "char(1)")
    public String getFlAnomaliaMigrateSecond() {
        return this.flAnomaliaMigrateSecond;
    }

    public void setFlAnomaliaMigrateSecond(String flAnomaliaMigrateSecond) {
        this.flAnomaliaMigrateSecond = flAnomaliaMigrateSecond;
    }

    @Column(name = "FL_ANOMALIA_RI_ARK_PRIM", columnDefinition = "char(1)")
    public String getFlAnomaliaRiArkPrim() {
        return this.flAnomaliaRiArkPrim;
    }

    public void setFlAnomaliaRiArkPrim(String flAnomaliaRiArkPrim) {
        this.flAnomaliaRiArkPrim = flAnomaliaRiArkPrim;
    }

    @Column(name = "FL_ANOMALIA_RI_ARK_SECOND", columnDefinition = "char(1)")
    public String getFlAnomaliaRiArkSecond() {
        return this.flAnomaliaRiArkSecond;
    }

    public void setFlAnomaliaRiArkSecond(String flAnomaliaRiArkSecond) {
        this.flAnomaliaRiArkSecond = flAnomaliaRiArkSecond;
    }

    @Column(name = "FL_MIGRAZ_IN_CORSO", columnDefinition = "char(1)")
    public String getFlMigrazInCorso() {
        return this.flMigrazInCorso;
    }

    public void setFlMigrazInCorso(String flMigrazInCorso) {
        this.flMigrazInCorso = flMigrazInCorso;
    }

    @Column(name = "FL_PRESENZA_SECONDARIO", columnDefinition = "char(1)")
    public String getFlPresenzaSecondario() {
        return this.flPresenzaSecondario;
    }

    public void setFlPresenzaSecondario(String flPresenzaSecondario) {
        this.flPresenzaSecondario = flPresenzaSecondario;
    }

    @Id
    @Column(name = "ID_DT_SCHED")
    public BigDecimal getIdDtSched() {
        return this.idDtSched;
    }

    public void setIdDtSched(BigDecimal idDtSched) {
        this.idDtSched = idDtSched;
    }

    @Column(name = "TI_STATO_DT_SCHED")
    public String getTiStatoDtSched() {
        return this.tiStatoDtSched;
    }

    public void setTiStatoDtSched(String tiStatoDtSched) {
        this.tiStatoDtSched = tiStatoDtSched;
    }

}
