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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.web.ejb;

import java.util.HashMap;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import it.eng.parer.entity.OrgStrut;

/**
 *
 * @author Filippini_M
 */

@Singleton
@LocalBean
@Startup
public class StrutCache {

    HashMap<UUID, OrgStrut> struts;

    @PostConstruct
    protected void initSingleton() {
	struts = new HashMap<UUID, OrgStrut>();
    }

    public OrgStrut getOrgStrut(UUID key) {
	/*
	 * OrgStrut toRemove = struts.get(key); struts.remove(key); return toRemove;
	 */
	return struts.get(key);
    }

    public OrgStrut setOrgStrut(UUID key, OrgStrut strut) {
	return struts.put(key, strut);
    }

    public void removeOrgStrut(UUID key) {
	struts.remove(key);
    }

}
