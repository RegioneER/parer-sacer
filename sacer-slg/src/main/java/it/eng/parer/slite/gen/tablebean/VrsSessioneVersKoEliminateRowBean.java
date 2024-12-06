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

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.eng.parer.slite.gen.tablebean;

import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.VrsSessioneVersKoEliminate;
import static it.eng.parer.slite.gen.tablebean.VrsSessioneVersKoEliminateRowBean.TABLE_DESCRIPTOR;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author gpiccioli
 */
public class VrsSessioneVersKoEliminateRowBean extends BaseRow implements JEEBaseRowInterface {

    public static VrsSessioneVersKoEliminateTableDescriptor TABLE_DESCRIPTOR = new VrsSessioneVersKoEliminateTableDescriptor();

    public VrsSessioneVersKoEliminateRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    public BigDecimal getIdSessioneVersKoEliminate() {
        return getBigDecimal("id_sessione_vers_ko_eliminate");
    }

    public void setIdSessioneVersKoEliminate(BigDecimal idSessioneVersKoEliminate) {
        setObject("id_sessione_vers_ko_eliminate", idSessioneVersKoEliminate);
    }

    public BigDecimal getIdStrut() {
        return getBigDecimal("id_strut");
    }

    public void setIdStrut(BigDecimal idStrut) {
        setObject("id_strut", idStrut);
    }

    public String getDsStrut() {
        return getString("ds_strut");
    }

    public void setDsStrut(String dsStrut) {
        setObject("ds_strut", dsStrut);
    }

    public String getNmStrut() {
        return getString("nm_strut");
    }

    public void setNmStrut(String nmStrut) {
        setObject("nm_strut", nmStrut);
    }

    public String getNmEnte() {
        return getString("nm_ente");
    }

    public void setNmEnte(String nmEnte) {
        setObject("nm_ente", nmEnte);
    }

    public String getNmAmbiente() {
        return getString("nm_ambiente");
    }

    public void setNmAmbiente(String nmAmbiente) {
        setObject("nm_ambiente", nmAmbiente);
    }

    public BigDecimal getNiSesEliminate() {
        return getBigDecimal("ni_ses_eliminate");
    }

    public void setNiSesEliminate(BigDecimal niSesEliminate) {
        setObject("ni_ses_eliminate", niSesEliminate);
    }

    public Timestamp getDtElab() {
        return getTimestamp("dt_elab");
    }

    public void setDtElab(Timestamp dtElab) {
        setObject("dt_elab", dtElab);
    }

    public Timestamp getDtRif() {
        return getTimestamp("dt_rif");
    }

    public void setDtRif(Timestamp dtRif) {
        setObject("dt_rif", dtRif);
    }

    @Override
    public void entityToRowBean(Object obj) {
        VrsSessioneVersKoEliminate entity = (VrsSessioneVersKoEliminate) obj;

        this.setIdSessioneVersKoEliminate(entity.getIdSessioneVersKoEliminata() == null ? null
                : BigDecimal.valueOf(entity.getIdSessioneVersKoEliminata()));

        if (entity.getOrgStrut() != null) {
            OrgStrut strut = entity.getOrgStrut();
            OrgEnte ente = strut.getOrgEnte();
            OrgAmbiente ambiente = ente.getOrgAmbiente();
            this.setIdStrut(new BigDecimal(strut.getIdStrut()));
            this.setNmEnte(ente.getNmEnte());
            this.setNmAmbiente(ambiente.getNmAmbiente());
        }

        if (entity.getDsStrut() != null) {
            this.setDsStrut(entity.getDsStrut());
        }

        if (entity.getNmStrut() != null) {
            this.setNmStrut(entity.getNmStrut());
        }

        if (entity.getNiSesEliminate() != null) {
            this.setNiSesEliminate(entity.getNiSesEliminate());
        }

        if (entity.getDtElab() != null) {
            this.setDtElab(new Timestamp(entity.getDtElab().getTime()));
        }

        if (entity.getDtRif() != null) {
            this.setDtRif(new Timestamp(entity.getDtRif().getTime()));
        }
    }

    @Override
    public VrsSessioneVersKoEliminate rowBeanToEntity() {
        VrsSessioneVersKoEliminate entity = new VrsSessioneVersKoEliminate();

        if (this.getIdSessioneVersKoEliminate() != null) {
            entity.setIdSessioneVersKoEliminata(this.getIdSessioneVersKoEliminate().longValue());
        }

        if (this.getIdStrut() != null) {
            if (entity.getOrgStrut() == null) {
                entity.setOrgStrut(new OrgStrut());
            }

            entity.getOrgStrut().setIdStrut(this.getIdStrut().longValue());
        }

        if (this.getNmStrut() != null) {
            entity.setNmStrut(this.getDsStrut());
        }

        if (this.getDsStrut() != null) {
            entity.setDsStrut(this.getDsStrut());
        }

        if (this.getNiSesEliminate() != null) {
            entity.setNiSesEliminate(this.getNiSesEliminate());
        }

        if (this.getDtElab() != null) {
            entity.setDtElab(this.getDtElab());
        }

        if (this.getDtRif() != null) {
            entity.setDtRif(this.getDtRif());
        }

        return entity;
    }
}
