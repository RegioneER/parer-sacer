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

package it.eng.parer.grantedEntity;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The persistent class for the APL_PARAM_APPLIC_REPORT database table.
 */
@Entity
@Table(schema = "SACER_IAM", name = "APL_PARAM_APPLIC_REPORT")
@NamedQuery(name = "AplParamApplicReport.findByApplicReport", query = "SELECT o FROM AplParamApplicReport o WHERE o.nmApplic=:nmApplic AND o.nmReport=:nmReport")
public class AplParamApplicReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idParamApplicReport;

    private String nmApplic;

    private String nmReport;

    private String blFile1;

    private String blFile2;

    private String blFile3;

    public AplParamApplicReport() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_PARAM_APPLIC_REPORT")
    public Long getIdParamApplicReport() {
	return this.idParamApplicReport;
    }

    public void setIdParamApplicReport(Long idParamApplicReport) {
	this.idParamApplicReport = idParamApplicReport;
    }

    @Column(name = "NM_APPLIC")
    public String getNmApplic() {
	return nmApplic;
    }

    public void setNmApplic(String nmApplic) {
	this.nmApplic = nmApplic;
    }

    @Column(name = "NM_REPORT")
    public String getNmReport() {
	return nmReport;
    }

    public void setNmReport(String nmReport) {
	this.nmReport = nmReport;
    }

    @Column(name = "BL_FILE_1", columnDefinition = "clob")
    public String getBlFile1() {
	return blFile1;
    }

    public void setBlFile1(String blFile1) {
	this.blFile1 = blFile1;
    }

    @Column(name = "BL_FILE_2", columnDefinition = "clob")
    public String getBlFile2() {
	return blFile2;
    }

    public void setBlFile2(String blFile2) {
	this.blFile2 = blFile2;
    }

    @Column(name = "BL_FILE_3", columnDefinition = "clob")
    public String getBlFile3() {
	return blFile3;
    }

    public void setBlFile3(String blFile3) {
	this.blFile3 = blFile3;
    }
}
