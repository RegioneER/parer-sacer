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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.DecAaTipoFascicolo;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.job.verificaCompTipoFasc.helper.VerificaCompTipoFascHelper;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.versFascicoli.dto.ConfigNumFasc;
import it.eng.parer.ws.versFascicoli.ejb.ControlliFascicoli;
import it.eng.parer.ws.versFascicoli.utils.KeyOrdFascUtility;
import it.eng.parer.ws.versFascicoli.utils.KeySizeFascUtility;

/**
 *
 * @author fioravanti_f
 */
@Stateless(mappedName = "VerificaPeriodoRegistroEjb")
@LocalBean
public class VerificaPeriodoTipoFascEjb {

    Logger log = LoggerFactory.getLogger(VerificaPeriodoTipoFascEjb.class);

    @Resource
    private SessionContext sessionContext;

    @Resource
    private EJBContext ejbContext;

    @EJB
    private ControlliFascicoli controlliFascicoli;
    @EJB
    private VerificaCompTipoFascHelper verificaCompTipoFascHelper;
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void verificaPeriodo(long idAaTipoFasc) {
        // recupero un proxy per invocare il metodo con una nuova transazione
        VerificaPeriodoTipoFascEjb me = sessionContext.getBusinessObject(VerificaPeriodoTipoFascEjb.class);
        //
        DecAaTipoFascicolo tmpDecAaTipoFascicolo = entityManager.find(DecAaTipoFascicolo.class, idAaTipoFasc);
        ConfigNumFasc configNumFasc = controlliFascicoli.caricaPartiAANumero(
                tmpDecAaTipoFascicolo.getIdAaTipoFascicolo(),
                tmpDecAaTipoFascicolo.getNiCharPadParteClassif().longValue());
        // inizializzo la verifica delle parti, dando come massima lunghezza, la massima possibile
        // per il numero; di fatto escludendo il test di lunghezza.
        // Fascicoli già versati, il registro non può cambiare nome e quindi il controllo
        // dello spazio residuo nella chiave ordinamento è inutile.
        KeyOrdFascUtility tmpKeyOrdFascUtility = new KeyOrdFascUtility(configNumFasc,
                KeySizeFascUtility.MAX_LEN_CHIAVEORD);
        long anno = tmpDecAaTipoFascicolo.getAaIniTipoFascicolo().longValue();
        long annoMax = tmpDecAaTipoFascicolo.getAaFinTipoFascicolo() != null
                ? tmpDecAaTipoFascicolo.getAaIniTipoFascicolo().longValue()
                : new GregorianCalendar().get(Calendar.YEAR);
        long idTipoFasc = tmpDecAaTipoFascicolo.getDecTipoFascicolo().getIdTipoFascicolo();
        long idOrgStrut = tmpDecAaTipoFascicolo.getDecTipoFascicolo().getOrgStrut().getIdStrut();
        //
        while (anno <= annoMax) {
            RispostaControlli rispostaControlli = me.verificaAnnoNewTrans(idTipoFasc, anno, idOrgStrut,
                    tmpKeyOrdFascUtility);
            if (!rispostaControlli.isrBoolean()) {
                // se ci sono errori, dopo aver fatto il rollback sui fascicoli elaborati,
                // pulisce gli eventuali errori e warning dell'anno e
                // scrive l'errore in una transazione autonoma
                verificaCompTipoFascHelper.scriviErrorePerAnnoNewTrans(idAaTipoFasc, anno, rispostaControlli.getrLong(),
                        rispostaControlli.getDsErr());
            } else {
                // pulisce gli eventuali errori e warning dell'anno, in una transazione autonoma
                verificaCompTipoFascHelper.pulisciErroriRegAnnoNewTrans(idAaTipoFasc, anno);
            }
            //
            anno++;
        }
        // azzero il flag di AARegistro modificato
        verificaCompTipoFascHelper.sbloccaAaTipoFascicolo(idAaTipoFasc);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public RispostaControlli verificaAnnoNewTrans(long idTipoFasc, long anno, long idOrgStrut,
            KeyOrdFascUtility tmpKeyOrdFascUtility) {
        RispostaControlli risposta = new RispostaControlli();
        risposta.setrBoolean(true);

        // elabora anno
        List<FasFascicolo> fasFascicolos = verificaCompTipoFascHelper.getListaFasFascicoloDaVerificare(idTipoFasc,
                idOrgStrut, anno);
        for (FasFascicolo tmpFasc : fasFascicolos) {
            CSChiaveFasc tmpCSChiaveFasc = new CSChiaveFasc();
            tmpCSChiaveFasc.setAnno(tmpFasc.getAaFascicolo().intValue());
            tmpCSChiaveFasc.setNumero(tmpFasc.getCdKeyFascicolo());
            RispostaControlli rispostaControlli = tmpKeyOrdFascUtility.verificaChiave(tmpCSChiaveFasc,
                    tmpFasc.getOrgStrut().getIdStrut(), tmpFasc.getDtApeFascicolo(), tmpFasc.getCdIndiceClassif());

            if (rispostaControlli.isrBoolean()) {
                // salvo la chiave per l'ordinamento
                KeyOrdFascUtility.KeyOrdResult keyOrdResult = (KeyOrdFascUtility.KeyOrdResult) rispostaControlli
                        .getrObject();
                // aggiorno tmpUd
                // aggiorno la chiave di ordinamento calcolata
                tmpFasc.setCdKeyOrd(keyOrdResult.getKeyOrdCalcolata());
                // TODO: aggiorno il progressivo calcolato, estratto dal numero della chiave.. potrebbe non esistere
                /*
                 * if (keyOrdResult.getProgressivoCalcolato() != null) { tmpFasc.setPgUnitaDoc(new
                 * BigDecimal(keyOrdResult.getProgressivoCalcolato())); } else { tmpFasc.setPgUnitaDoc(null); }
                 */
            } else {
                risposta.setCodErr(rispostaControlli.getCodErr());
                risposta.setDsErr(rispostaControlli.getDsErr());
                risposta.setrLong(tmpFasc.getIdFascicolo());
                risposta.setrBoolean(false);
                // in caso di problemi, effettuo il rollback delle modifiche fatte alle UD dell'anno
                ejbContext.setRollbackOnly();
                break;
            }
        }
        return risposta;
    }

}
