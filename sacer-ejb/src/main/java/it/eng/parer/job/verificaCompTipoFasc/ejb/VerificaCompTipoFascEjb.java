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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.job.verificaCompTipoFasc.ejb;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.DecAaTipoFascicolo;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.job.verificaCompTipoFasc.helper.VerificaCompTipoFascHelper;

/**
 *
 * @author sinatti_s
 */
@Stateless(mappedName = "VerificaCompTipoFascEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class VerificaCompTipoFascEjb {

    Logger log = LoggerFactory.getLogger(VerificaCompTipoFascEjb.class);
    @EJB
    private JobHelper jobHelper;
    @EJB
    private VerificaPeriodoTipoFascEjb verificaPeriodoTipoFasc;
    @EJB
    private VerificaCompTipoFascHelper verificaCompTipoFascHelper;

    public void verificaCompTipoFasc() throws ParerInternalError {

        log.debug("Recupero lista elementi da elaborare");
        List<DecAaTipoFascicolo> tmpList = verificaCompTipoFascHelper.getAaTipoFascicoloDaElab();
        try {
            for (DecAaTipoFascicolo tmpAaTipoFasc : tmpList) {
                verificaPeriodoTipoFasc.verificaPeriodo(tmpAaTipoFasc.getIdAaTipoFascicolo());
            }
        } catch (Exception ex) {
            // log.fatal("Creazione Indice SIP - Errore: " + ex);
            log.error("Creazione Indice SIP - Errore: " + ex);
            throw new ParerInternalError(ex);
        }
        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di verifica */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.VERIFICA_COMPATIBILITA_TIPO_FASC.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        log.debug("Verifica Compatibilità tipo fascicolo - fine");
    }
}
