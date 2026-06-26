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

import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecAaRegistroUnitaDoc;
import it.eng.parer.entity.OrgSubStrut;
import it.eng.parer.job.verificaCompRegistro.helper.VerificaCompRegHelper;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliSemantici;
import it.eng.parer.ws.utils.KeyOrdUtility;
import it.eng.parer.ws.utils.KeySizeUtility;
import it.eng.parer.ws.versamento.dto.ConfigRegAnno;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
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

/**
 *
 * @author fioravanti_f
 */
@Stateless(mappedName = "VerificaPeriodoRegistroEjb")
@LocalBean
public class VerificaPeriodoRegistroEjb {

    Logger log = LoggerFactory.getLogger(VerificaPeriodoRegistroEjb.class);

    @Resource
    private SessionContext sessionContext;

    @Resource
    private EJBContext ejbContext;

    @EJB
    private ControlliSemantici controlliSemantici;
    @EJB
    private VerificaCompRegHelper verificaCompRegHelper;
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void verificaPeriodo(long idAaRegistroUnitaDoc) {
        // recupero un proxy per invocare il metodo con una nuova transazione
        VerificaPeriodoRegistroEjb me = sessionContext
                .getBusinessObject(VerificaPeriodoRegistroEjb.class);
        //
        ConfigRegAnno configRegAnno = controlliSemantici
                .caricaPartiAARegistro(idAaRegistroUnitaDoc);
        // inizializzo la verifica delle parti, dando come massima lunghezza, la massima possibile
        // per il numero; di fatto escludendo il test di lunghezza.
        // Queste UD sono già entrate, il registro non può cambiare nome e quindi il controllo
        // dello spazio residuo nella chiave ordinamento è inutile.
        KeyOrdUtility tmpKeyOrdUtility = new KeyOrdUtility(configRegAnno,
                KeySizeUtility.MAX_LEN_CHIAVEORD);
        DecAaRegistroUnitaDoc tmpAaRegistroUnitaDoc = entityManager
                .find(DecAaRegistroUnitaDoc.class, idAaRegistroUnitaDoc);
        long anno = tmpAaRegistroUnitaDoc.getAaMinRegistroUnitaDoc().longValue();
        long annoMax = tmpAaRegistroUnitaDoc.getAaMaxRegistroUnitaDoc() != null
                ? tmpAaRegistroUnitaDoc.getAaMaxRegistroUnitaDoc().longValue()
                : new GregorianCalendar().get(Calendar.YEAR);
        Long idRegistroUnitaDoc = tmpAaRegistroUnitaDoc.getDecRegistroUnitaDoc()
                .getIdRegistroUnitaDoc();
        List<OrgSubStrut> orgSubStruts = tmpAaRegistroUnitaDoc.getDecRegistroUnitaDoc()
                .getOrgStrut().getOrgSubStruts();
        //
        List<Long> idSubStruts = new ArrayList<>();
        for (OrgSubStrut oss : orgSubStruts) {
            idSubStruts.add(oss.getIdSubStrut());
        }
        //
        while (anno <= annoMax) {
            RispostaControlli rispostaControlli = me.verificaAnnoNewTrans(idRegistroUnitaDoc, anno,
                    idSubStruts, tmpKeyOrdUtility);
            if (!rispostaControlli.isrBoolean()) {
                // se ci sono errori, dopo aver fatto il rollback sulle UD elaborate,
                // pulisce gli eventuali errori e warning dell'anno e
                // scrive l'errore in una transazione autonoma
                verificaCompRegHelper.scriviErrorePerAnnoNewTrans(idAaRegistroUnitaDoc, anno,
                        rispostaControlli.getrLong(), rispostaControlli.getDsErr());
            } else {
                // pulisce gli eventuali errori e warning dell'anno, in una transazione autonoma
                verificaCompRegHelper.pulisciErroriRegAnnoNewTrans(idAaRegistroUnitaDoc, anno);
            }
            //
            anno++;
        }
        // azzero il flag di AARegistro modificato
        verificaCompRegHelper.sbloccaAaRegistroUnitaDoc(idAaRegistroUnitaDoc);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public RispostaControlli verificaAnnoNewTrans(Long idRegistroUnitaDoc, long anno,
            List<Long> idSubStruts, KeyOrdUtility tmpKeyOrdUtility) {

        RispostaControlli risposta = new RispostaControlli();
        risposta.setrBoolean(true);
        int count = 0;

        // Utilizzo del try-with-resources per garantire la chiusura dello stream e del cursore DB
        try (Stream<AroUnitaDoc> aroUnitaDocStream = verificaCompRegHelper
                .getStreamUdDaVerificare(idRegistroUnitaDoc, idSubStruts, anno)) {

            log.info("JOB VERIFICA_COMPATIBILITA_REGISTRO: Verifica registro {} per anno {}",
                    idRegistroUnitaDoc, anno);

            // L'uso dell' Iterator permette di usare il return immediato, cosa che non avremmo
            // potuto fare con un semplice .forEach() lambda
            Iterator<AroUnitaDoc> iterator = aroUnitaDocStream.iterator();

            while (iterator.hasNext()) {
                AroUnitaDoc tmpUd = iterator.next();

                // 1. Esecuzione controlli
                CSChiave tmpCSChiave = new CSChiave();
                tmpCSChiave.setAnno(tmpUd.getAaKeyUnitaDoc().longValue());
                tmpCSChiave.setTipoRegistro(tmpUd.getCdRegistroKeyUnitaDoc());
                tmpCSChiave.setNumero(tmpUd.getCdKeyUnitaDoc());

                RispostaControlli rispostaControlli = tmpKeyOrdUtility.verificaChiave(tmpCSChiave);

                if (rispostaControlli.isrBoolean()) {
                    // Successo: aggiorno l'oggetto managed
                    KeyOrdUtility.KeyOrdResult keyOrdResult = (KeyOrdUtility.KeyOrdResult) rispostaControlli
                            .getrObject();
                    tmpUd.setDsKeyOrd(keyOrdResult.getKeyOrdCalcolata());

                    if (keyOrdResult.getProgressivoCalcolato() != null) {
                        tmpUd.setPgUnitaDoc(new BigDecimal(keyOrdResult.getProgressivoCalcolato()));
                    } else {
                        tmpUd.setPgUnitaDoc(null);
                    }
                } else {
                    // Errore di validazione: Rollback totale dell'anno e uscita immediata
                    risposta.setCodErr(rispostaControlli.getCodErr());
                    risposta.setDsErr(rispostaControlli.getDsErr());
                    risposta.setrLong(tmpUd.getIdUnitaDoc());
                    risposta.setrBoolean(false);
                    log.info(
                            "JOB VERIFICA_COMPATIBILITA_REGISTRO: Verifica terminata con errore di validazione");

                    ejbContext.setRollbackOnly(); // Annulla tutti i flush fatti finora per questo
                                                  // anno
                    return risposta; // col return, abbinato al try-with-resource, esco dal ciclo
                                     // immediatamente, chiudo lo stream e il cursore sul DB
                }

                // Gestione Memoria: ogni 1000 record svuoto il Persistence Context
                if (++count % 1000 == 0) {
                    entityManager.flush(); // Invia gli update al DB (ma senza commit)
                    entityManager.clear(); // Libera la RAM eliminando gli oggetti processati
                }
            }
            log.info(
                    "JOB VERIFICA_COMPATIBILITA_REGISTRO: Verifica dell'anno {} terminata senza errorei di validazione",
                    anno);
        } catch (Exception e) {
            // Gestione errori tecnici imprevisti durante lo streaming
            log.error(
                    "JOB VERIFICA_COMPATIBILITA_REGISTRO: Errore nel processamento dello stream per anno "
                            + anno,
                    e);
            risposta.setrBoolean(false);
            risposta.setDsErr(
                    "JOB VERIFICA_COMPATIBILITA_REGISTRO: Errore nel processamento dello stream per anno: "
                            + e.getMessage());
            ejbContext.setRollbackOnly();
            return risposta;
        }

        return risposta;
    }

}
