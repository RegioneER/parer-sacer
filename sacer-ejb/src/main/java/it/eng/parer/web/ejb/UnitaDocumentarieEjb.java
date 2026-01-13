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

package it.eng.parer.web.ejb;

import it.eng.parer.entity.AroLogStatoConservUd;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.web.helper.ConfigurationHelper;
import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "UnitaDocumentarieEjb")
@LocalBean
public class UnitaDocumentarieEjb {

    @Resource
    SessionContext ctx;

    @EJB(mappedName = "java:app/Parer-ejb/UnitaDocumentarieHelper")
    private UnitaDocumentarieHelper unitaDocumentarieHelper;

    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    public UnitaDocumentarieEjb() {
        //
    }

    private static final Logger log = LoggerFactory.getLogger(UnitaDocumentarieEjb.class);

    // MEV #31162
    /**
     *
     * @param idUnitaDoc           id unità documentaria di cui registrare lo stato di conservazione
     *                             ud
     * @param nmAgente             nome dell'automatismo o dell'utente che ha portato allo stato di
     *                             conservazione ud da registrare
     * @param tiEvento             tipo di evento che ha portato allo stato di conservazione ud da
     *                             registrare
     * @param tiStatoConservazione stato di conservazione ud da registrare
     * @param tiMod                tipo modalità
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void insertLogStatoConservUd(long idUnitaDoc, String nmAgente, String tiEvento,
            String tiStatoConservazione, String tiMod) {
        AroUnitaDoc unitaDoc = unitaDocumentarieHelper.findById(AroUnitaDoc.class, idUnitaDoc);
        BigDecimal idAmbiente = BigDecimal
                .valueOf(unitaDoc.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente());
        BigDecimal idStrut = BigDecimal.valueOf(unitaDoc.getOrgStrut().getIdStrut());
        BigDecimal idTipoUnitaDoc = BigDecimal
                .valueOf(unitaDoc.getDecTipoUnitaDoc().getIdTipoUnitaDoc());
        // Recupero il parametro per verificare se procedere o meno al log
        boolean flAbilitaLogStatoConserv = Boolean.parseBoolean(
                configurationHelper.getValoreParamApplicByTipoUd("FL_ABILITA_LOG_STATO_CONSERV",
                        idAmbiente, idStrut, idTipoUnitaDoc));
        if (flAbilitaLogStatoConserv) {
            AroLogStatoConservUd logStatoConservUd = new AroLogStatoConservUd();
            logStatoConservUd.setAroUnitaDoc(unitaDoc);
            logStatoConservUd.setOrgSubStrut(unitaDoc.getOrgSubStrut());
            // Ottieni l'istante corrente
            Instant now = Instant.now();
            // Crea un Timestamp dall'Instant
            Timestamp istante = Timestamp.from(now);
            logStatoConservUd.setDtStato(istante);
            logStatoConservUd.setAaKeyUnitaDoc(unitaDoc.getAaKeyUnitaDoc());
            logStatoConservUd.setNmAgente(nmAgente);
            logStatoConservUd.setTiEvento(tiEvento);
            logStatoConservUd.setTiMod(tiMod);
            logStatoConservUd.setAaKeyUnitaDoc(unitaDoc.getAaKeyUnitaDoc());
            logStatoConservUd.setTiStatoConservazione(tiStatoConservazione);
            if (unitaDoc.getAroLogStatoConservUds() == null) {
                unitaDoc.setAroLogStatoConservUds(new ArrayList<>());
            }
            unitaDoc.getAroLogStatoConservUds().add(logStatoConservUd);
            unitaDocumentarieHelper.getEntityManager().persist(logStatoConservUd);
            unitaDocumentarieHelper.getEntityManager().flush();
        }
    }
    // end MEV #31162

}
