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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_TIPO_DOC_PROCESSO_CONSERV database table.
 */
@Entity
@Cacheable(true)
@Table(schema = "SACER_IAM", name = "DEC_TIPO_DOC_PROCESSO_CONSERV")
public class DecTipoDocProcessoConserv implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idTipoDocProcessoConserv;
    private String nmTipoDoc;
    private String dsTipoDoc;
    private Date dtIstituz;
    private Date dtSoppres;

    // Relazioni
    private OrgStrut orgStrut;
    private List<DecDocProcessoConserv> decDocProcessoConservs = new ArrayList<>();

    public DecTipoDocProcessoConserv() {
        // Costruttore di default
    }

    @Id
    @Column(name = "ID_TIPO_DOC_PROCESSO_CONSERV")
    @GenericGenerator(name = "SORG_TIPO_DOC_PROCESSO_CONSERV", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SORG_TIPO_DOC_PROCESSO_CONSERV"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SORG_TIPO_DOC_PROCESSO_CONSERV")
    public Long getIdTipoDocProcessoConserv() {
        return this.idTipoDocProcessoConserv;
    }

    public void setIdTipoDocProcessoConserv(Long idTipoDocProcessoConserv) {
        this.idTipoDocProcessoConserv = idTipoDocProcessoConserv;
    }

    @Column(name = "NM_TIPO_DOC")
    public String getNmTipoDoc() {
        return this.nmTipoDoc;
    }

    public void setNmTipoDoc(String nmTipoDoc) {
        this.nmTipoDoc = nmTipoDoc;
    }

    @Column(name = "DS_TIPO_DOC")
    public String getDsTipoDoc() {
        return this.dsTipoDoc;
    }

    public void setDsTipoDoc(String dsTipoDoc) {
        this.dsTipoDoc = dsTipoDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ISTITUZ")
    public Date getDtIstituz() {
        return this.dtIstituz;
    }

    public void setDtIstituz(Date dtIstituz) {
        this.dtIstituz = dtIstituz;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SOPPRES")
    public Date getDtSoppres() {
        return this.dtSoppres;
    }

    public void setDtSoppres(Date dtSoppres) {
        this.dtSoppres = dtSoppres;
    }

    // Relazione many-to-one con OrgStrut
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    // Relazione one-to-many con DecDocProcessoConserv
    @OneToMany(mappedBy = "decTipoDocProcessoConserv", cascade = CascadeType.PERSIST)
    public List<DecDocProcessoConserv> getDecDocProcessoConservs() {
        return this.decDocProcessoConservs;
    }

    public void setDecDocProcessoConservs(List<DecDocProcessoConserv> decDocProcessoConservs) {
        this.decDocProcessoConservs = decDocProcessoConservs;
    }

    public DecDocProcessoConserv addDecDocProcessoConserv(
            DecDocProcessoConserv decDocProcessoConserv) {
        getDecDocProcessoConservs().add(decDocProcessoConserv);
        decDocProcessoConserv.setDecTipoDocProcessoConserv(this);
        return decDocProcessoConserv;
    }

    public DecDocProcessoConserv removeDecDocProcessoConserv(
            DecDocProcessoConserv decDocProcessoConserv) {
        getDecDocProcessoConservs().remove(decDocProcessoConserv);
        decDocProcessoConserv.setDecTipoDocProcessoConserv(null);
        return decDocProcessoConserv;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash
                + (this.idTipoDocProcessoConserv != null ? this.idTipoDocProcessoConserv.hashCode()
                        : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final DecTipoDocProcessoConserv other = (DecTipoDocProcessoConserv) obj;
        if (this.idTipoDocProcessoConserv != other.idTipoDocProcessoConserv
                && (this.idTipoDocProcessoConserv == null
                        || !this.idTipoDocProcessoConserv.equals(other.idTipoDocProcessoConserv))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DecTipoDocProcessoConserv{" + "idTipoDocProcessoConserv=" + idTipoDocProcessoConserv
                + ", nmTipoDoc='" + nmTipoDoc + '\'' + ", dsTipoDoc='" + dsTipoDoc + '\''
                + ", dtIstituz=" + dtIstituz + ", dtSoppres=" + dtSoppres + '}';
    }
}
