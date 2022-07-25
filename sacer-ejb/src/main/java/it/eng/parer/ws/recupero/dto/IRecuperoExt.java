/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recupero.dto;

import it.eng.parer.ws.dto.IRestWSBase;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import java.io.Serializable;

/**
 *
 * @author Fioravanti_F
 */
public interface IRecuperoExt extends Serializable, IRestWSBase {

    Recupero getStrutturaRecupero();

    void setStrutturaRecupero(Recupero strutturaRecupero);
}
