/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.util;

import it.eng.parer.web.helper.AmministrazioneHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoLite.actions.application.ApplicationBaseProperties;
import it.eng.spagoLite.actions.application.IApplicationBasePropertiesSevice;
import java.math.BigDecimal;
import javax.ejb.EJB;

/**
 *
 * @author Iacolucci_M
 *
 *         Implementazione che fornisce al framework SpagoLite i dati essenziali dell'applicazione per poter utilizzare
 *         l'Help on line da IAM
 */
public class ApplicationBasePropertiesSeviceImpl implements IApplicationBasePropertiesSevice {

    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    @Override
    public ApplicationBaseProperties getApplicationBaseProperties() {
        String nmApplic = configurationHelper.getValoreParamApplic("NM_APPLIC", null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC); // NM_APPLIC
        String user = configurationHelper.getValoreParamApplic("USERID_RECUP_INFO", null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        String password = configurationHelper.getValoreParamApplic("PSW_RECUP_INFO", null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        String url = configurationHelper.getValoreParamApplic("URL_RECUP_HELP", null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);

        ApplicationBaseProperties prop = new ApplicationBaseProperties(nmApplic, user, password, url);

        return prop;
    }

}
