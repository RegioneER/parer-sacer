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
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.job.verificaCompRegistro.ejb;

import it.eng.parer.entity.DecAaRegistroUnitaDoc;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.job.verificaCompRegistro.helper.VerificaCompRegHelper;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fioravanti_f
 */
@Stateless(mappedName = "VerificaCompRegistroEjb")
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class VerificaCompRegistroEjb {

    Logger log = LoggerFactory.getLogger(VerificaCompRegistroEjb.class);
    @EJB
    private JobHelper jobHelper;
    @EJB
    private VerificaPeriodoRegistroEjb verificaPeriodoRegistro;
    @EJB
    private VerificaCompRegHelper verificaCompRegHelper;

    public void verificaCompRegistro() throws ParerInternalError {

        log.debug("Recupero lista elementi da elaborare");
        List<DecAaRegistroUnitaDoc> tmpList = verificaCompRegHelper.getAaRegistroUnitaDocDaElab();
        try {
            for (DecAaRegistroUnitaDoc tmpAaRegistroUnitaDoc : tmpList) {
                verificaPeriodoRegistro
                        .verificaPeriodo(tmpAaRegistroUnitaDoc.getIdAaRegistroUnitaDoc());
            }
        } catch (Exception ex) {
            // log.fatal("Creazione Indice AIP - Errore: " + ex);
            log.error("Creazione Indice AIP - Errore: " + ex);
            throw new ParerInternalError(ex);
        }
        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di verifica */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.VERIFICA_COMPATIBILITA_REGISTRO.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        log.debug("Verifica Compatibilità registro - fine");
    }
}
