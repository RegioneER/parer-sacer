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

package it.eng.parer.job.indiceAipFascicoli.helper;

import static it.eng.parer.util.Utils.bigDecimalFromLong;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import org.slf4j.Logger;
import it.eng.parer.entity.FasAmminPartec;
import it.eng.parer.entity.FasFileMetaVerAipFasc;
import it.eng.parer.entity.FasLinkFascicolo;
import it.eng.parer.entity.FasMetaVerAipFascicolo;
import it.eng.parer.entity.FasRespFascicolo;
import it.eng.parer.entity.FasSogFascicolo;
import it.eng.parer.entity.FasUniOrgRespFascicolo;
import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.entity.FasXmlFascicolo;
import it.eng.parer.entity.FasXsdMetaVerAipFasc;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiModelloXsd;
import it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiUsoModelloXsd;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.viewEntity.FasVLisUdInFasc;
import it.eng.parer.viewEntity.FasVVisFascicolo;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.MessaggiWSFormat;

/**
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings({ "unchecked" })
@Stateless(mappedName = "CreazioneIndiceMetaFascicoliHelper")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CreazioneIndiceMetaFascicoliHelper extends GenericHelper {

    public static final String JAVAX_PERSISTENCE_FETCHGRAPH = "javax.persistence.fetchgraph";

    @Resource
    private SessionContext context;

    Logger log = LoggerFactory.getLogger(CreazioneIndiceMetaFascicoliHelper.class);

    public FasVVisFascicolo getFasVVisFascicolo(Long idFascicolo) {
        Query query = getEntityManager()
                .createQuery("SELECT u FROM FasVVisFascicolo u WHERE u.idFascicolo = :idFascicolo ");
        query.setParameter("idFascicolo", bigDecimalFromLong(idFascicolo));
        return (FasVVisFascicolo) query.getSingleResult();
    }

    public List<FasVLisUdInFasc> getFasVLisUdInFasc(Long idFascicolo, Long idUserIamCorrente) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM FasVLisUdInFasc u WHERE u.id.idFascicolo = :idFascicolo AND u.idUserIamCorrente = :idUserIamCorrente ");
        query.setParameter("idFascicolo", bigDecimalFromLong(idFascicolo));
        query.setParameter("idUserIamCorrente", bigDecimalFromLong(idUserIamCorrente));
        return query.getResultList();
    }

    public List<FasAmminPartec> getFasAmminPartec(Long idFascicolo) {
        Query query = getEntityManager().createQuery("SELECT u FROM FasAmminPartec u "
                + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo " + "ORDER BY u.dsAmminPartec ");
        query.setParameter("idFascicolo", idFascicolo);
        return query.getResultList();
    }

    public List<FasSogFascicolo> getFasSogFascicolo(Long idFascicolo) {
        Query query = getEntityManager().createQuery("SELECT u FROM FasSogFascicolo u "
                + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo " + "ORDER BY u.dsDenomSog ");
        query.setParameter("idFascicolo", idFascicolo);
        return query.getResultList();
    }

    public List<FasRespFascicolo> getFasRespFascicolo(Long idFascicolo) {
        Query query = getEntityManager()
                .createQuery("SELECT u FROM FasRespFascicolo u " + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo");
        query.setParameter("idFascicolo", idFascicolo);
        return query.getResultList();
    }

    public List<FasUniOrgRespFascicolo> getFasUniOrgRespFascicolo(Long idFascicolo) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM FasUniOrgRespFascicolo u " + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo");
        query.setParameter("idFascicolo", idFascicolo);
        return query.getResultList();
    }

    public List<DecValVoceTitol> getDecValVoceTitol(Long idVoceTitol) {
        Query query = getEntityManager().createQuery("SELECT u FROM DecValVoceTitol u "
                + "WHERE u.decVoceTitol.idVoceTitol = :idVoceTitol " + "ORDER BY u.dsVoceTitol");
        query.setParameter("idVoceTitol", idVoceTitol);
        return query.getResultList();
    }

    public List<FasLinkFascicolo> getFasLinkFascicolo(Long idFascicolo) {
        Query query = getEntityManager().createQuery("SELECT u FROM FasLinkFascicolo u "
                + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo " + "ORDER BY u.dsLink ");
        query.setParameter("idFascicolo", idFascicolo);
        return query.getResultList();
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
            Date dtCreazione, BackendStorage backendMetadata, Map<String, String> indiceAipFascicoloBlob) {
        FasFileMetaVerAipFasc fileMetaVerAipFasc = new FasFileMetaVerAipFasc();
        FasMetaVerAipFascicolo fasMetaVerAipFascicolo = getEntityManager().find(FasMetaVerAipFascicolo.class,
                idMetaVerAipFascicolo);
        fileMetaVerAipFasc.setFasMetaVerAipFascicolo(fasMetaVerAipFascicolo);

        // MEV#30398
        if (backendMetadata.isDataBase()) {
            // clob contenente lo XML in input (canonicalizzato)
            fileMetaVerAipFasc.setBlFileVerIndiceAip(file);
        } else {
            indiceAipFascicoloBlob.put(it.eng.parer.entity.constraint.FasMetaVerAipFascicolo.TiMeta.FASCICOLO.name(),
                    file);
        }
        // end MEV#30398

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
        // calcolo parte urn ORIGINALE
        String tmpUrn = MessaggiWSFormat.formattaBaseUrnFascicolo(MessaggiWSFormat.formattaUrnPartVersatore(versatore),
                MessaggiWSFormat.formattaUrnPartFasc(chiaveFasc));
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnFascicolo(
                MessaggiWSFormat.formattaUrnPartVersatore(versatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                MessaggiWSFormat.formattaUrnPartFasc(chiaveFasc, true, Costanti.UrnFormatter.FASC_FMT_STRING));
        // salvo ORIGINALE
        fasMetaVerAipFascicolo
                .setDsUrnMetaFascicolo(MessaggiWSFormat.formattaUrnAipMetaFascicolo(tmpUrn, codiceVersione));
        // salvo NORMALIZZATO
        fasMetaVerAipFascicolo.setDsUrnNormalizMetaFascicolo(
                MessaggiWSFormat.formattaUrnAipMetaFascicolo(tmpUrnNorm, codiceVersione));
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
        return q.getResultList();
    }

    // MEV#26576
    /**
     * Ricava il modello xsd di tipo FASCICOLO attivo per l'ambiente di appartenenza della struttura a cui il fascicolo
     * appartiene e per la versione del modello xsd corrispondente a quella del servizio di versamento fascicolo
     *
     * @param idAmbiente
     *            id ambiente
     * @param cdVersioneXml
     *            versione del servizio di versamento fascicolo
     *
     * @return lista oggetti di tipo {@link DecModelloXsdFascicolo}
     */
    public List<DecModelloXsdFascicolo> retrieveIdModelliFascicoloDaElaborareV2(long idAmbiente, String cdVersioneXml) {
        String queryStr = "SELECT modello " + "FROM DecModelloXsdFascicolo modello "
                + "WHERE modello.orgAmbiente.idAmbiente = :idAmbiente " + "AND modello.tiModelloXsd = 'FASCICOLO' "
                + "AND modello.dtIstituz <= :filterDate AND modello.dtSoppres >= :filterDate "
                + "AND modello.cdXsd = :cdVersioneXml";

        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idAmbiente", idAmbiente);
        q.setParameter("filterDate", Calendar.getInstance().getTime());
        q.setParameter("cdVersioneXml", cdVersioneXml);
        return q.getResultList();
    }

    public List<FasXmlFascicolo> leggiXmlVersamentiModelloXsdFascicolo(TiUsoModelloXsd tiUsoModelloXsd,
            TiModelloXsd tiModelloXsdFasc, long idFascicolo) {

        List<FasXmlFascicolo> fasXmlFascicolo = null;

        try {

            String queryStr = "select xf from FasXmlFascicolo xf " + "join xf.decModelloXsdFascicolo modello_xsd "
                    + "where modello_xsd.tiUsoModelloXsd = :tiUsoModelloXsd "
                    + "and modello_xsd.tiModelloXsd = :tiModelloXsdFasc "
                    + "and xf.fasFascicolo.idFascicolo = :idFascicolo ";
            javax.persistence.Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("tiUsoModelloXsd", tiUsoModelloXsd);
            query.setParameter("tiModelloXsdFasc", tiModelloXsdFasc);
            query.setParameter("idFascicolo", idFascicolo);

            fasXmlFascicolo = query.getResultList();
        } catch (Exception e) {
            log.error("Eccezione nella lettura modello xsd fascicolo", e);
        }
        return fasXmlFascicolo;
    }
    // end MEV#26576
}
