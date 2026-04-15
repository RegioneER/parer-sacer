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

package it.eng.parer.slite.gen.tablebean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import it.eng.parer.entity.DecModelloXsdFascRif;
import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.constraint.DecModelloXsdFascRif.TiRiferimento;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Dec_Modello_Xsd_Fasc_Rif
 *
 */
public class DecModelloXsdFascRifRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    public static DecModelloXsdFascRifTableDescriptor TABLE_DESCRIPTOR = new DecModelloXsdFascRifTableDescriptor();

    public DecModelloXsdFascRifRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdModelloXsdFascRif() {
        return getBigDecimal("id_modello_xsd_fasc_rif");
    }

    public void setIdModelloXsdFascRif(BigDecimal idModelloXsdFascRif) {
        setObject("id_modello_xsd_fasc_rif", idModelloXsdFascRif);
    }

    public BigDecimal getIdModelloXsdFascicoloPadre() {
        return getBigDecimal("id_modello_xsd_fascicolo_padre");
    }

    public void setIdModelloXsdFascicoloPadre(BigDecimal idModelloXsdFascicoloPadre) {
        setObject("id_modello_xsd_fascicolo_padre", idModelloXsdFascicoloPadre);
    }

    public BigDecimal getIdModelloXsdFascicoloTarget() {
        return getBigDecimal("id_modello_xsd_fascicolo_target");
    }

    public void setIdModelloXsdFascicoloTarget(BigDecimal idModelloXsdFascicoloTarget) {
        setObject("id_modello_xsd_fascicolo_target", idModelloXsdFascicoloTarget);
    }

    public String getTiRiferimento() {
        return getString("ti_riferimento");
    }

    public void setTiRiferimento(String tiRiferimento) {
        setObject("ti_riferimento", tiRiferimento);
    }

    public String getNamespaceUri() {
        return getString("namespace_uri");
    }

    public void setNamespaceUri(String namespaceUri) {
        setObject("namespace_uri", namespaceUri);
    }

    public String getSchemaLocation() {
        return getString("schema_location");
    }

    public void setSchemaLocation(String schemaLocation) {
        setObject("schema_location", schemaLocation);
    }

    public Timestamp getDtIstituz() {
        return getTimestamp("dt_istituz");
    }

    public void setDtIstituz(Timestamp dtIstituz) {
        setObject("dt_istituz", dtIstituz);
    }

    public Timestamp getDtSoppres() {
        return getTimestamp("dt_soppres");
    }

    public void setDtSoppres(Timestamp dtSoppres) {
        setObject("dt_soppres", dtSoppres);
    }

    // Campo aggiuntivo per mostrare il codice XSD del target nella lista
    public String getCdXsdTarget() {
        return getString("cd_xsd_target");
    }

    public void setCdXsdTarget(String cdXsdTarget) {
        setObject("cd_xsd_target", cdXsdTarget);
    }

    @Override
    public void entityToRowBean(Object obj) {
        DecModelloXsdFascRif entity = (DecModelloXsdFascRif) obj;

        this.setIdModelloXsdFascRif(entity.getIdModelloXsdFascRif() == null ? null
                : BigDecimal.valueOf(entity.getIdModelloXsdFascRif()));

        if (entity.getDecModelloXsdFascicoloPadre() != null) {
            this.setIdModelloXsdFascicoloPadre(BigDecimal
                    .valueOf(entity.getDecModelloXsdFascicoloPadre().getIdModelloXsdFascicolo()));
        }

        if (entity.getDecModelloXsdFascicoloTarget() != null) {
            this.setIdModelloXsdFascicoloTarget(BigDecimal
                    .valueOf(entity.getDecModelloXsdFascicoloTarget().getIdModelloXsdFascicolo()));
            this.setCdXsdTarget(entity.getDecModelloXsdFascicoloTarget().getCdXsd());
        }

        if (entity.getTiRiferimento() != null) {
            this.setTiRiferimento(entity.getTiRiferimento().name());
        }
        this.setNamespaceUri(entity.getNamespaceUri());
        this.setSchemaLocation(entity.getSchemaLocation());

        if (entity.getDtIstituz() != null) {
            this.setDtIstituz(new Timestamp(entity.getDtIstituz().getTime()));
        }
        if (entity.getDtSoppres() != null) {
            this.setDtSoppres(new Timestamp(entity.getDtSoppres().getTime()));
        }
    }

    @Override
    public DecModelloXsdFascRif rowBeanToEntity() {
        DecModelloXsdFascRif entity = new DecModelloXsdFascRif();
        if (this.getIdModelloXsdFascRif() != null) {
            entity.setIdModelloXsdFascRif(this.getIdModelloXsdFascRif().longValue());
        }
        if (this.getIdModelloXsdFascicoloPadre() != null) {
            DecModelloXsdFascicolo padre = new DecModelloXsdFascicolo();
            padre.setIdModelloXsdFascicolo(this.getIdModelloXsdFascicoloPadre().longValue());
            entity.setDecModelloXsdFascicoloPadre(padre);
        }
        if (this.getIdModelloXsdFascicoloTarget() != null) {
            DecModelloXsdFascicolo target = new DecModelloXsdFascicolo();
            target.setIdModelloXsdFascicolo(this.getIdModelloXsdFascicoloTarget().longValue());
            entity.setDecModelloXsdFascicoloTarget(target);
        }
        if (this.getTiRiferimento() != null) {
            entity.setTiRiferimento(TiRiferimento.valueOf(this.getTiRiferimento()));
        }
        entity.setNamespaceUri(this.getNamespaceUri());
        entity.setSchemaLocation(this.getSchemaLocation());
        entity.setDtIstituz(this.getDtIstituz());
        entity.setDtSoppres(this.getDtSoppres());
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

}
