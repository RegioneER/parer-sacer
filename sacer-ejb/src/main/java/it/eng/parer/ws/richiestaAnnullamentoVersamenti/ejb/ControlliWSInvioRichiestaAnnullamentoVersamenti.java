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

package it.eng.parer.ws.richiestaAnnullamentoVersamenti.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroRichAnnulVers;
import it.eng.parer.entity.AroStatoRichAnnulVers;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "ControlliWSInvioRichiestaAnnullamentoVersamenti")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliWSInvioRichiestaAnnullamentoVersamenti {

    private static final Logger log = LoggerFactory
	    .getLogger(ControlliWSInvioRichiestaAnnullamentoVersamenti.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    /**
     * Controlla che non esista un'altra richiesta appartenente alla struttura comunicata, con lo
     * stesso codice identificativo e con stato corrente diverso da INVIO_FALLITO
     *
     * @param idStrut id struttura
     * @param codice  code
     *
     * @return RispostaControlli risposta con esisto operazione eseguita
     */
    @SuppressWarnings("unchecked")
    public RispostaControlli checkRichiestaEsistenteInvioFallito(Long idStrut, String codice) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	try {
	    String queryStr = "SELECT richAnnulVers FROM AroRichAnnulVers richAnnulVers "
		    + "WHERE richAnnulVers.orgStrut.idStrut = :idStrut "
		    + "AND richAnnulVers.cdRichAnnulVers = :codice "
		    + "AND EXISTS (SELECT statoRichAnnulVers FROM AroStatoRichAnnulVers statoRichAnnulVers "
		    + "WHERE richAnnulVers.idStatoRichAnnulVersCor = statoRichAnnulVers.idStatoRichAnnulVers "
		    + "AND statoRichAnnulVers.tiStatoRichAnnulVers != 'INVIO_FALLITO') ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idStrut", idStrut);
	    query.setParameter("codice", codice);

	    List<AroRichAnnulVers> richAnnulVersList = query.getResultList();
	    if (richAnnulVersList.isEmpty()) {
		// OK, non esiste
		rispostaControlli.setrBoolean(true);
	    } else {
		AroRichAnnulVers rich = richAnnulVersList.get(0);
		AroStatoRichAnnulVers stato = entityManager.find(AroStatoRichAnnulVers.class,
			rich.getIdStatoRichAnnulVersCor().longValue());
		if (stato.getTiStatoRichAnnulVers()
			.equals(CostantiDB.StatoRichAnnulVers.CHIUSA.name())) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.RICH_ANN_VERS_014);
		    rispostaControlli.setDsErr(
			    MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_014));
		} else if (stato.getTiStatoRichAnnulVers()
			.equals(CostantiDB.StatoRichAnnulVers.EVASA.name())) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.RICH_ANN_VERS_015);
		    rispostaControlli.setDsErr(
			    MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_015));
		} else {
		    rispostaControlli.setCodErr(MessaggiWSBundle.RICH_ANN_VERS_009);
		    rispostaControlli.setDsErr(
			    MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_009));
		}
	    }
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli
		    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, e.getMessage()));
	    log.error(
		    "Eccezione nella lettura della tabella delle richieste di annullamento versamento ",
		    e);
	}
	return rispostaControlli;
    }
}
