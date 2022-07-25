package it.eng.parer.informazioni.noteRilascio.helper;

import it.eng.parer.grantedEntity.SIAplApplic;
import it.eng.parer.grantedEntity.SIAplNotaRilascio;
import it.eng.parer.slite.gen.form.NoteRilascioForm;
import it.eng.spagoCore.error.EMFError;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
public class NoteRilascioHelper {

    public NoteRilascioHelper() {
    }

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    public SIAplApplic getAplApplicByName(String nmApplic) {
        String queryStr = "SELECT applic FROM SIAplApplic applic " + "WHERE applic.nmApplic = :nmApplic ";
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
        SIAplApplic applic = (SIAplApplic) q.getSingleResult();
        return applic;
    }

    public List<SIAplNotaRilascio> getAplNoteRilascioList(BigDecimal idApplic) throws EMFError {
        String queryStr = "SELECT notaRilascio FROM SIAplNotaRilascio notaRilascio "
                + "WHERE notaRilascio.siAplApplic.idApplic = :idApplic " + "ORDER BY notaRilascio.dtVersione DESC";

        Query query = entityManager.createQuery(queryStr);

        if (idApplic != null) {
            query.setParameter("idApplic", idApplic);
        }

        List<SIAplNotaRilascio> list = query.getResultList();

        return list;
    }

    public SIAplNotaRilascio getAplNotaRilascioByVersione(String cdVersione) {

        StringBuilder queryStr = new StringBuilder("SELECT notaRilascio FROM SIAplNotaRilascio notaRilascio");
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

    public SIAplApplic getAplApplic(long idApplic) throws EMFError {
        SIAplApplic applic = entityManager.find(SIAplApplic.class, idApplic);
        return applic;
    }

    public List<SIAplNotaRilascio> getAplNoteRilascioPrecList(BigDecimal idApplic, BigDecimal idNotaRilascio,
            Date dtVersione) throws EMFError {
        String queryStr = "SELECT notaRilascio FROM SIAplNotaRilascio notaRilascio "
                + "JOIN notaRilascio.siAplApplic applic " + "WHERE notaRilascio.idNotaRilascio != :idNotaRilascio "
                + "AND applic.idApplic = :idApplic ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idNotaRilascio", idNotaRilascio);
        query.setParameter("idApplic", idApplic);
        List<SIAplNotaRilascio> list = query.getResultList();
        CollectionUtils.filter(list, new Predicate() {
            @Override
            public boolean evaluate(final Object object) {
                return ((SIAplNotaRilascio) object).getDtVersione().compareTo(dtVersione) < 0;
            }
        });
        return list;
    }
}
