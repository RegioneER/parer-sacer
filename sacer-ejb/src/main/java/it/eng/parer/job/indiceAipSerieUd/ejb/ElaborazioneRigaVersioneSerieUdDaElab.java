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

package it.eng.parer.job.indiceAipSerieUd.ejb;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.SerVerSerieDaElab;
import it.eng.parer.job.indiceAipSerieUd.helper.CreazioneIndiceAipSerieUdHelper;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "ElaborazioneRigaVersioneSerieUdDaElab")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ElaborazioneRigaVersioneSerieUdDaElab {

    Logger log = LoggerFactory.getLogger(ElaborazioneRigaVersioneSerieUdDaElab.class);
    @EJB
    private CreazioneIndiceAipSerieUdHelper ciasudHelper;
    @EJB
    private ElaborazioneRigaIndiceVolumeSerieUd elaborazioneVolume;
    @EJB
    private ElaborazioneRigaIndiceAipVersioneSerieUd elaborazioneAip;

    public void gestisciIndiceAipSerieUdDaElab(SerVerSerieDaElab verSerieDaElab, String creatingApplicationProducer)
            throws Exception {
        Long idVerSerie = verSerieDaElab.getSerVerSerie().getIdVerSerie();
        // Numero di unità documentarie con cui creare i volumi definito dal tipo di serie della versione della serie
        BigDecimal num = verSerieDaElab.getSerVerSerie().getSerSerie().getDecTipoSerie().getNiUnitaDocVolume();
        BigDecimal numUdPerVolume = num != null ? num : new BigDecimal("999999999999");
        if (numUdPerVolume.compareTo(BigDecimal.ZERO) == 0) {
            numUdPerVolume = new BigDecimal("999999999999");
        }

        // Numero di unità documentarie appartenenti al contenuto di tipo EFFETTIVO della versione serie
        // per le quali la foreign key al volume sia nulla (cioè non fanno ancora parte di un volume)
        BigDecimal numUdEffettive = new BigDecimal(ciasudHelper.getNumUdEffettiveSenzaVolume(idVerSerie));
        // Numero volumi da creare, arrotondando all'intero superiore
        BigDecimal numVolumiDaCreare = (numUdEffettive.divide(numUdPerVolume, RoundingMode.CEILING));

        log.info(ElaborazioneRigaVersioneSerieUdDaElab.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- " + "Per la versione serie " + idVerSerie
                + " verranno creati " + numVolumiDaCreare + " volumi, con " + numUdPerVolume
                + " unità documentarie in ogni volume");

        /* CREA I VOLUMI ED I RELATIVI INDICI */
        // (una transazione per ogni volume)
        log.info(ElaborazioneRigaVersioneSerieUdDaElab.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- " + "Inizio creazione volumi e relativi indici ");
        for (int i = 0; i < numVolumiDaCreare.intValue(); i++) {
            elaborazioneVolume.creaVolumeVerSerie(idVerSerie, numUdPerVolume, numVolumiDaCreare);
        }
        log.info(ElaborazioneRigaVersioneSerieUdDaElab.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- " + "Termine creazione volumi e relativi indici ");

        /* CREA INDICE AIP VERSIONE SERIE */
        log.info(ElaborazioneRigaVersioneSerieUdDaElab.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- " + "Inizio creazione indice AIP versione serie ");
        elaborazioneAip.creaIndiceAipSerieUd(verSerieDaElab.getIdVerSerieDaElab(), creatingApplicationProducer);
        log.info(ElaborazioneRigaVersioneSerieUdDaElab.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- " + "Termine creazione indice AIP versione serie ");
    }
}
