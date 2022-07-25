/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.ejb;

import it.eng.parer.entity.OrgStrut;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import java.util.UUID;

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
