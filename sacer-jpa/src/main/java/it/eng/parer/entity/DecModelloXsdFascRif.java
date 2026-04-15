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
import java.util.Date;
import javax.persistence.*;

import it.eng.parer.entity.constraint.DecModelloXsdFascRif.TiRiferimento;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_MODELLO_XSD_FASC_RIF database table. * Rappresenta le dipendenze
 * tra XSD (root/parent) e gli schemi importati/inclusi *
 *
 * Rappresenta le dipendenze tra XSD (root/parent) e gli schemi importati/inclusi tramite xs:import
 * e xs:include per supportare la validazione multi-namespace.
 */
@Entity
@Table(name = "DEC_MODELLO_XSD_FASC_RIF")
public class DecModelloXsdFascRif implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idModelloXsdFascRif;
    private TiRiferimento tiRiferimento;
    private String namespaceUri;
    private String schemaLocation;
    private Date dtIstituz;
    private Date dtSoppres;

    private DecModelloXsdFascicolo decModelloXsdFascicoloPadre;
    private DecModelloXsdFascicolo decModelloXsdFascicoloTarget;

    public DecModelloXsdFascRif() {
        /* Hibernate */
    }

    @Id
    @Column(name = "ID_MODELLO_XSD_FASC_RIF")
    @GenericGenerator(name = "SDEC_MODELLO_XSD_FASC_RIF_ID_MODELLO_XSD_FASC_RIF_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_MODELLO_XSD_FASC_RIF"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_MODELLO_XSD_FASC_RIF_ID_MODELLO_XSD_FASC_RIF_GENERATOR")
    public Long getIdModelloXsdFascRif() {
        return this.idModelloXsdFascRif;
    }

    public void setIdModelloXsdFascRif(Long idModelloXsdFascRif) {
        this.idModelloXsdFascRif = idModelloXsdFascRif;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_RIFERIMENTO")
    public TiRiferimento getTiRiferimento() {
        return this.tiRiferimento;
    }

    public void setTiRiferimento(TiRiferimento tiRiferimento) {
        this.tiRiferimento = tiRiferimento;
    }

    @Column(name = "NAMESPACE_URI")
    public String getNamespaceUri() {
        return this.namespaceUri;
    }

    public void setNamespaceUri(String namespaceUri) {
        this.namespaceUri = namespaceUri;
    }

    @Column(name = "SCHEMA_LOCATION")
    public String getSchemaLocation() {
        return this.schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
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

    // bi-directional many-to-one association to DecModelloXsdFascicolo (parent/root)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MODELLO_XSD_FASCICOLO_PADRE")
    public DecModelloXsdFascicolo getDecModelloXsdFascicoloPadre() {
        return this.decModelloXsdFascicoloPadre;
    }

    public void setDecModelloXsdFascicoloPadre(DecModelloXsdFascicolo decModelloXsdFascicoloPadre) {
        this.decModelloXsdFascicoloPadre = decModelloXsdFascicoloPadre;
    }

    // bi-directional many-to-one association to DecModelloXsdFascicolo (target/imported)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MODELLO_XSD_FASCICOLO_TARGET")
    public DecModelloXsdFascicolo getDecModelloXsdFascicoloTarget() {
        return this.decModelloXsdFascicoloTarget;
    }

    public void setDecModelloXsdFascicoloTarget(
            DecModelloXsdFascicolo decModelloXsdFascicoloTarget) {
        this.decModelloXsdFascicoloTarget = decModelloXsdFascicoloTarget;
    }
}
