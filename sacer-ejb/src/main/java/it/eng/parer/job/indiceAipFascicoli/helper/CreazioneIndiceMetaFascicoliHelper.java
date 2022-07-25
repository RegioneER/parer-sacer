package it.eng.parer.job.indiceAipFascicoli.helper;

import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.DecValVoceTitol;
import it.eng.parer.entity.FasAmminPartec;
import it.eng.parer.entity.FasFileMetaVerAipFasc;
import it.eng.parer.entity.FasLinkFascicolo;
import it.eng.parer.entity.FasMetaVerAipFascicolo;
import it.eng.parer.entity.FasRespFascicolo;
import it.eng.parer.entity.FasSogFascicolo;
import it.eng.parer.entity.FasUniOrgRespFascicolo;
import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.entity.FasXsdMetaVerAipFasc;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.FasVLisUdInFasc;
import it.eng.parer.viewEntity.FasVVisFascicolo;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless(mappedName = "CreazioneIndiceMetaFascicoliHelper")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CreazioneIndiceMetaFascicoliHelper extends GenericHelper {

    @Resource
    private SessionContext context;
    private static final Logger log = LoggerFactory.getLogger(CreazioneIndiceMetaFascicoliHelper.class);

    public FasVVisFascicolo getFasVVisFascicolo(Long idFascicolo) {
        Query query = getEntityManager()
                .createQuery("SELECT u FROM FasVVisFascicolo u WHERE u.idFascicolo = :idFascicolo ");
        query.setParameter("idFascicolo", idFascicolo);
        return (FasVVisFascicolo) query.getSingleResult();
    }

    public List<FasVLisUdInFasc> getFasVLisUdInFasc(Long idFascicolo, Long idUserIamCorrente) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM FasVLisUdInFasc u WHERE u.idFascicolo = :idFascicolo AND u.idUserIamCorrente = :idUserIamCorrente ");
        query.setParameter("idFascicolo", idFascicolo);
        query.setParameter("idUserIamCorrente", idUserIamCorrente);
        return (List<FasVLisUdInFasc>) query.getResultList();
    }

    public List<FasAmminPartec> getFasAmminPartec(Long idFascicolo) {
        Query query = getEntityManager().createQuery("SELECT u FROM FasAmminPartec u "
                + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo " + "ORDER BY u.dsAmminPartec ");
        query.setParameter("idFascicolo", idFascicolo);
        return (List<FasAmminPartec>) query.getResultList();
    }

    public List<FasSogFascicolo> getFasSogFascicolo(Long idFascicolo) {
        Query query = getEntityManager().createQuery("SELECT u FROM FasSogFascicolo u "
                + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo " + "ORDER BY u.dsDenomSog ");
        query.setParameter("idFascicolo", idFascicolo);
        return (List<FasSogFascicolo>) query.getResultList();
    }

    public List<FasRespFascicolo> getFasRespFascicolo(Long idFascicolo) {
        Query query = getEntityManager()
                .createQuery("SELECT u FROM FasRespFascicolo u " + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo");
        query.setParameter("idFascicolo", idFascicolo);
        return (List<FasRespFascicolo>) query.getResultList();
    }

    public List<FasUniOrgRespFascicolo> getFasUniOrgRespFascicolo(Long idFascicolo) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM FasUniOrgRespFascicolo u " + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo");
        query.setParameter("idFascicolo", idFascicolo);
        return (List<FasUniOrgRespFascicolo>) query.getResultList();
    }

    public List<DecValVoceTitol> getDecValVoceTitol(Long idVoceTitol) {
        Query query = getEntityManager().createQuery("SELECT u FROM DecValVoceTitol u "
                + "WHERE u.decVoceTitol.idVoceTitol = :idVoceTitol " + "ORDER BY u.dsVoceTitol");
        query.setParameter("idVoceTitol", idVoceTitol);
        return (List<DecValVoceTitol>) query.getResultList();
    }

    public List<FasLinkFascicolo> getFasLinkFascicolo(Long idFascicolo) {
        Query query = getEntityManager().createQuery("SELECT u FROM FasLinkFascicolo u "
                + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo " + "ORDER BY u.dsLink ");
        query.setParameter("idFascicolo", idFascicolo);
        return (List<FasLinkFascicolo>) query.getResultList();
    }

    public FasXsdMetaVerAipFasc registraFasXsdMetaVerAipFasc(long idMetaVerAipFascicolo, long idModelloXsdFascicolo,
            String nmXsd) {
        FasXsdMetaVerAipFasc xsdMetaVerAipFasc = new FasXsdMetaVerAipFasc();
        FasMetaVerAipFascicolo fasMetaVerAipFascicolo = getEntityManager().find(FasMetaVerAipFascicolo.class,
                idMetaVerAipFascicolo);
        DecModelloXsdFascicolo decModelloXsdFascicolo = getEntityManager().find(DecModelloXsdFascicolo.class,
                idModelloXsdFascicolo);
        xsdMetaVerAipFasc.setFasMetaVerAipFascicolo(fasMetaVerAipFascicolo);
        xsdMetaVerAipFasc.setDecModelloXsdFascicolo(decModelloXsdFascicolo);
        xsdMetaVerAipFasc.setNmXsd(nmXsd);
        getEntityManager().persist(xsdMetaVerAipFasc);
        getEntityManager().flush();
        if (fasMetaVerAipFascicolo.getFasXsdMetaVerAipFascs() == null) {
            fasMetaVerAipFascicolo.setFasXsdMetaVerAipFascs(new ArrayList<>());
        }
        fasMetaVerAipFascicolo.getFasXsdMetaVerAipFascs().add(xsdMetaVerAipFasc);

        if (decModelloXsdFascicolo.getFasXsdMetaVerAipFascs() == null) {
            decModelloXsdFascicolo.setFasXsdMetaVerAipFascs(new ArrayList<>());
        }
        decModelloXsdFascicolo.getFasXsdMetaVerAipFascs().add(xsdMetaVerAipFasc);

        return xsdMetaVerAipFasc;
    }

    public FasFileMetaVerAipFasc registraFasFileMetaVerAipFasc(long idMetaVerAipFascicolo, String file, OrgStrut strut,
            Date dtCreazione) {
        FasFileMetaVerAipFasc fileMetaVerAipFasc = new FasFileMetaVerAipFasc();
        FasMetaVerAipFascicolo fasMetaVerAipFascicolo = getEntityManager().find(FasMetaVerAipFascicolo.class,
                idMetaVerAipFascicolo);
        fileMetaVerAipFasc.setFasMetaVerAipFascicolo(fasMetaVerAipFascicolo);
        fileMetaVerAipFasc.setBlFileVerIndiceAip(file);
        fileMetaVerAipFasc.setOrgStrut(strut);
        fileMetaVerAipFasc.setDtCreazione(dtCreazione);
        getEntityManager().persist(fileMetaVerAipFasc);
        getEntityManager().flush();
        if (fasMetaVerAipFascicolo.getFasFileMetaVerAipFascs() == null) {
            fasMetaVerAipFascicolo.setFasFileMetaVerAipFascs(new ArrayList<>());
        }
        fasMetaVerAipFascicolo.getFasFileMetaVerAipFascs().add(fileMetaVerAipFasc);
        return fileMetaVerAipFasc;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FasMetaVerAipFascicolo registraFasMetaVerAipFascicolo(long idVerAipFascicolo, String hash, String algoHash,
            String encodingHash, String codiceVersione, CSVersatore versatore, CSChiaveFasc chiaveFasc) {
        FasMetaVerAipFascicolo fasMetaVerAipFascicolo = new FasMetaVerAipFascicolo();
        FasVerAipFascicolo verAipFascicolo = getEntityManager().find(FasVerAipFascicolo.class, idVerAipFascicolo);
        fasMetaVerAipFascicolo.setFasVerAipFascicolo(verAipFascicolo);
        fasMetaVerAipFascicolo.setNmMeta("Fascicolo");
        fasMetaVerAipFascicolo.setTiMeta("FASCICOLO");
        fasMetaVerAipFascicolo.setDsHashFile(hash);
        fasMetaVerAipFascicolo.setDsAlgoHashFile(algoHash);
        fasMetaVerAipFascicolo.setCdEncodingHashFile(encodingHash);
        // salvo ORIGINALE
        fasMetaVerAipFascicolo.setDsUrnMetaFascicolo(
                MessaggiWSFormat.formattaBaseUrnFascicolo(MessaggiWSFormat.formattaUrnPartVersatore(versatore),
                        MessaggiWSFormat.formattaUrnPartFasc(chiaveFasc)));
        // salvo NORMALIZZATO
        fasMetaVerAipFascicolo.setDsUrnNormalizMetaFascicolo(MessaggiWSFormat.formattaBaseUrnFascicolo(
                MessaggiWSFormat.formattaUrnPartVersatore(versatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                MessaggiWSFormat.formattaUrnPartFasc(chiaveFasc, true, Costanti.UrnFormatter.FASC_FMT_STRING)));
        getEntityManager().persist(fasMetaVerAipFascicolo);
        getEntityManager().flush();
        if (verAipFascicolo.getFasMetaVerAipFascicolos() == null) {
            verAipFascicolo.setFasMetaVerAipFascicolos(new ArrayList<>());
        }
        verAipFascicolo.getFasMetaVerAipFascicolos().add(fasMetaVerAipFascicolo);
        return fasMetaVerAipFascicolo;
    }

    /**
     * Ricava il modello xsd attivo per l'ambiente di appartenenza della struttura a cui il fascicolo appartiene e per
     * il tipo FASCICOLO
     *
     * @param idAmbiente
     *            id ambiente
     * 
     * @return lista oggetti di tipo {@link DecModelloXsdFascicolo}
     */
    public List<DecModelloXsdFascicolo> retrieveIdModelliFascicoloDaElaborare(long idAmbiente) {
        String queryStr = "SELECT modello " + "FROM DecModelloXsdFascicolo modello "
                + "WHERE modello.orgAmbiente.idAmbiente = :idAmbiente " + "AND modello.tiModelloXsd = 'FASCICOLO' "
                + "AND modello.dtIstituz <= :filterDate AND modello.dtSoppres >= :filterDate";

        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idAmbiente", idAmbiente);
        q.setParameter("filterDate", Calendar.getInstance().getTime());
        List<DecModelloXsdFascicolo> modelli = (List<DecModelloXsdFascicolo>) q.getResultList();
        return modelli;
    }
}
