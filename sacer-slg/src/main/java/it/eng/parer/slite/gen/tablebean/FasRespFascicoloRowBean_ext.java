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

import it.eng.parer.entity.FasRespFascicolo;

/**
 *
 * @author Moretti_Lu
 */
public class FasRespFascicoloRowBean_ext extends FasRespFascicoloRowBean {

    private static final long serialVersionUID = 1L;
    private final String STR_FASCICOLO = "Gestione fascicoli";
    private final String STR_PROC_AMMIN = "Gestione procedimento amministrativo";

    @Override
    public void entityToRowBean(Object obj) {
        super.entityToRowBean(obj);

        final String s = this.getTiResp();

        if (s.equalsIgnoreCase("FASCICOLO")) {
            this.setTiResp(STR_FASCICOLO);
        } else if (s.equalsIgnoreCase("PROC_AMMIN")) {
            this.setTiResp(STR_PROC_AMMIN);
        }
    }

    @Override
    public FasRespFascicolo rowBeanToEntity() {
        FasRespFascicolo result = super.rowBeanToEntity();

        switch (this.getTiResp()) {
        case STR_FASCICOLO:
            result.setTiResp("FASCICOLO");
            break;
        case STR_PROC_AMMIN:
            result.setTiResp("PROC_AMMIN");
            break;
        }

        return result;
    }
}
