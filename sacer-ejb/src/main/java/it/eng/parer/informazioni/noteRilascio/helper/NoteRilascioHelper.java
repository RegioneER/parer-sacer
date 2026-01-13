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

package it.eng.parer.informazioni.noteRilascio.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections4.CollectionUtils;

import it.eng.parer.grantedEntity.SIAplApplic;
import it.eng.parer.grantedEntity.SIAplNotaRilascio;

/**
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings({
        "unchecked" })
@Stateless
@LocalBean
public class NoteRilascioHelper {

    public NoteRilascioHelper() {
        /* default */
    }

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    /**
     * @deprecated non più utilizzato
     *
     * @param nmApplic nome dell'applicazione
     *
     * @return {@link SIAplApplic} o null se non trova record
     */
    @Deprecated
    public SIAplApplic getAplApplicByName(String nmApplic) {
        String queryStr = "SELECT applic FROM SIAplApplic applic WHERE applic.nmApplic = :nmApplic ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("nmApplic", nmApplic);
        List<SIAplApplic> applic = query.getResultList();
        if (applic != null && applic.size() == 1) {
            return applic.get(0);
        } else {
            return null;
        }
    }

    public SIAplApplic getAplApplic(String name) {
        String queryStr = "SELECT applic FROM SIAplApplic applic WHERE applic.nmApplic = :nomeappl";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter("nomeappl", name);
        return (SIAplApplic) q.getSingleResult();
    }

    public List<SIAplNotaRilascio> getAplNoteRilascioList(BigDecimal idApplic) {
        String queryStr = "SELECT notaRilascio FROM SIAplNotaRilascio notaRilascio "
                + "WHERE notaRilascio.siAplApplic.idApplic = :idApplic "
                + "ORDER BY notaRilascio.dtVersione DESC";

        Query query = entityManager.createQuery(queryStr);

        if (idApplic != null) {
            query.setParameter("idApplic", longFromBigDecimal(idApplic));
        }

        return query.getResultList();
    }

    /**
     * @deprecated non più utilizzato
     *
     * @param cdVersione codice della versione
     *
     * @return SIAplNotaRilascio
     */
    @Deprecated
    public SIAplNotaRilascio getAplNotaRilascioByVersione(String cdVersione) {

        StringBuilder queryStr = new StringBuilder(
                "SELECT notaRilascio FROM SIAplNotaRilascio notaRilascio");
        if (cdVersione != null) {
            queryStr.append(" WHERE notaRilascio.cdVersione = :cdVersione");
        }

        Query query = entityManager.createQuery(queryStr.toString());

        if (cdVersione != null) {
            query.setParameter("cdVersione", cdVersione);
        }

        List<SIAplNotaRilascio> lista = query.getResultList();

        if (lista.isEmpty()) {
            return null;
        }

        return lista.get(0);
    }

    public SIAplNotaRilascio getAplNotaRilascioById(BigDecimal idNotaRilascio) {
        return entityManager.find(SIAplNotaRilascio.class, idNotaRilascio.longValue());
    }

    public SIAplApplic getAplApplicById(BigDecimal idApplic) {
        return entityManager.find(SIAplApplic.class, idApplic.longValue());
    }

    public void insert(Object o) {
        entityManager.persist(o);
        entityManager.flush();
    }

    public void update(Object o) {
        o = entityManager.merge(o);
        entityManager.flush();
        entityManager.refresh(o);
    }

    public void remove(Object o) {
        entityManager.remove(o);
        entityManager.flush();
    }

    public List<SIAplNotaRilascio> getAplNoteRilascioPrecList(BigDecimal idApplic,
            BigDecimal idNotaRilascio, Date dtVersione) {
        String queryStr = "SELECT notaRilascio FROM SIAplNotaRilascio notaRilascio "
                + "JOIN notaRilascio.siAplApplic applic "
                + "WHERE notaRilascio.idNotaRilascio != :idNotaRilascio "
                + "AND applic.idApplic = :idApplic ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idNotaRilascio", longFromBigDecimal(idNotaRilascio));
        query.setParameter("idApplic", longFromBigDecimal(idApplic));
        List<SIAplNotaRilascio> list = query.getResultList();
        CollectionUtils.filter(list,
                object -> ((SIAplNotaRilascio) object).getDtVersione().compareTo(dtVersione) < 0);
        return list;
    }
}
