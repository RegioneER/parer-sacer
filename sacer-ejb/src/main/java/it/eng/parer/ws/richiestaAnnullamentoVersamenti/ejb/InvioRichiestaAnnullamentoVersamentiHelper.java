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

import static it.eng.parer.util.Utils.bigDecimalFromLong;
import static it.eng.parer.util.Utils.bigDecimalFromInteger;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import it.eng.parer.entity.AroRichAnnulVers;
import it.eng.parer.entity.IamUser;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.AroVLisItemRichAnnvrs;

/**
 *
 * @author Gilioli_P
 */
@Stateless
@LocalBean
// NOTA PG 27/05/2016: ho tolto l'extend verso GenericHelper e deciso di gestire questo EJB "alla
// vecchia"
// (utilizzando al suo interno l'getEntityManager()) in quanto estendendo GenericHelper la lookUp
// presente in GestioneRichiesteAnnullamentoVersamenti falliva
// NOTA LB 29/03/2017: Rimessa estensione a GenericHelper ed eliminata la classe
// GestioneRichiesteAnnullamentoVersamenti
public class InvioRichiestaAnnullamentoVersamentiHelper extends GenericHelper {

    /**
     * Se esiste un'altra richiesta appartenente alla struttura comunicata, con lo stesso codice
     * identificativo e con stato corrente pari a INVIO_FALLITO, la richiesta stessa viene
     * cancellata
     *
     * @param idStrut id struttura
     * @param codice  codice
     */
    @SuppressWarnings("unchecked")
    public void deleteRichiestaSePresente(Long idStrut, String codice) {
	String queryStr = "SELECT richAnnulVers FROM AroStatoRichAnnulVers statoRichAnnulVers "
		+ "JOIN statoRichAnnulVers.aroRichAnnulVers richAnnulVers "
		+ "WHERE richAnnulVers.orgStrut.idStrut = :idStrut "
		+ "AND richAnnulVers.cdRichAnnulVers = :codice "
		+ "AND richAnnulVers.idStatoRichAnnulVersCor = statoRichAnnulVers.idStatoRichAnnulVers "
		+ "AND statoRichAnnulVers.tiStatoRichAnnulVers = 'INVIO_FALLITO' ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idStrut", idStrut);
	query.setParameter("codice", codice);
	List<AroRichAnnulVers> richAnnulVersList = query.getResultList();
	if (!richAnnulVersList.isEmpty()) {
	    getEntityManager().remove(richAnnulVersList.get(0));
	    getEntityManager().flush();
	}
    }

    /**
     * Verifica se in ARO_ITEM_RICH_ANNUL_VERS della richiesta è già presente un item con la stessa
     * struttura, registro, anno, numero
     *
     * @param idStrut               id struttura
     * @param cdRegistroKeyUnitaDoc registro unita doc
     * @param aaKeyUnitaDoc         anno unita doc
     * @param cdKeyUnitaDoc         codice unita doc
     *
     * @return true o false a seconda che ci sia l'item
     */
    public boolean isItemPresente(Long idStrut, String cdRegistroKeyUnitaDoc, int aaKeyUnitaDoc,
	    String cdKeyUnitaDoc) {
	String queryStr = "SELECT COUNT(itemRichAnnulVers) FROM AroItemRichAnnulVers itemRichAnnulVers "
		+ "JOIN itemRichAnnulVers.aroRichAnnulVers richAnnulVers "
		+ "WHERE itemRichAnnulVers.idStrut = :idStrut "
		+ "AND itemRichAnnulVers.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc "
		+ "AND itemRichAnnulVers.aaKeyUnitaDoc = :aaKeyUnitaDoc "
		+ "AND itemRichAnnulVers.cdKeyUnitaDoc = :cdKeyUnitaDoc ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idStrut", bigDecimalFromLong(idStrut));
	query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
	query.setParameter("aaKeyUnitaDoc", bigDecimalFromInteger(aaKeyUnitaDoc));
	query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
	return (Long) query.getSingleResult() > 0;
    }

    /**
     * Restituisce l'entity relativa allo user id passato in ingresso
     *
     * @param nmUserid nome userid
     *
     * @return l'entity relativa all'utente, null se non esiste
     */
    @SuppressWarnings("unchecked")
    public IamUser getIamUserByNmUserid(String nmUserid) {
	Query q = getEntityManager()
		.createQuery("SELECT user FROM IamUser user WHERE user.nmUserid = :nmUserid ");
	q.setParameter("nmUserid", nmUserid);
	List<IamUser> userList = q.getResultList();
	if (!userList.isEmpty()) {
	    return userList.get(0);
	}
	return null;
    }

    /**
     * Restituisce la lista degli item della richiesta di annullamento versamenti fornita in input
     *
     * @param idRichAnnVers id richiesta di annullamento versamento
     *
     * @return lista oggetti di tipo {@link AroVLisItemRichAnnvrs}
     */
    @SuppressWarnings("unchecked")
    public List<AroVLisItemRichAnnvrs> getAroVLisItemRichAnnvrs(BigDecimal idRichAnnVers) {
	String queryStr = "SELECT itemRichAnnvrs FROM AroVLisItemRichAnnvrs itemRichAnnvrs "
		+ "WHERE itemRichAnnvrs.idRichAnnulVers = :idRichAnnVers ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idRichAnnVers", idRichAnnVers);
	return query.getResultList();
    }
}
