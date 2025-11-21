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

package it.eng.parer.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ELV_FILE_ELENCO_VERS_OBJECT_STORAGE")
public class ElvFileElencoVersObjectStorage implements Serializable {
    private static final long serialVersionUID = 1L;

    public ElvFileElencoVersObjectStorage() {
        super();
    }

    private Long idFileElencoVersObjectStorage;
    private BigDecimal idStrut;
    private DecBackend decBackend;
    private ElvFileElencoVer elvFileElencoVer;
    private String nmTenant;
    private String nmBucket;
    private String cdKeyFile;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_FILE_ELENCO_VERS_OBJECT_STORAGE")
    public Long getIdFileElencoVersObjectStorage() {
        return idFileElencoVersObjectStorage;
    }

    public void setIdFileElencoVersObjectStorage(Long idFileElencoVersObjectStorage) {
        this.idFileElencoVersObjectStorage = idFileElencoVersObjectStorage;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DEC_BACKEND")
    public DecBackend getDecBackend() {
        return decBackend;
    }

    public void setDecBackend(DecBackend decBackend) {
        this.decBackend = decBackend;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FILE_ELENCO_VERS")
    public ElvFileElencoVer getElvFileElencoVer() {
        return elvFileElencoVer;
    }

    public void setElvFileElencoVer(ElvFileElencoVer elvFileElencoVer) {
        this.elvFileElencoVer = elvFileElencoVer;
    }

    @Column(name = "NM_TENANT")
    public String getNmTenant() {
        return nmTenant;
    }

    public void setNmTenant(String nmTenant) {
        this.nmTenant = nmTenant;
    }

    @Column(name = "NM_BUCKET")
    public String getNmBucket() {
        return nmBucket;
    }

    public void setNmBucket(String nmBucket) {
        this.nmBucket = nmBucket;
    }

    @Column(name = "CD_KEY_FILE")
    public String getCdKeyFile() {
        return cdKeyFile;
    }

    public void setCdKeyFile(String cdKeyFile) {
        this.cdKeyFile = cdKeyFile;
    }
}
