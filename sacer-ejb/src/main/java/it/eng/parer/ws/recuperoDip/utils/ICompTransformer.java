/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recuperoDip.utils;

import it.eng.parer.ws.dto.RispostaControlli;

/**
 *
 * @author Fioravanti_F
 */
public interface ICompTransformer {

    RispostaControlli convertiSuStream(ParametriTrasf parametri);

}
