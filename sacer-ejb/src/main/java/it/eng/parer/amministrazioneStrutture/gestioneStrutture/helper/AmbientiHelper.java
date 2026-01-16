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

package it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper;

import static it.eng.parer.util.Utils.bigDecimalFromLong;
import static it.eng.parer.util.Utils.longFromBigDecimal;
import static it.eng.parer.util.Utils.longListFrom;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.entity.IamOrganizDaReplic;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgCategEnte;
import it.eng.parer.entity.OrgCategStrut;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStoricoEnteAmbiente;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.grantedEntity.OrgAmbitoTerrit;
import it.eng.parer.grantedEntity.SIOrgAccordoEnte;
import it.eng.parer.grantedEntity.SIOrgAmbienteEnteConvenz;
import it.eng.parer.grantedEntity.SIOrgEnteConvenzOrg;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.grantedEntity.SIUsrOrganizIam;
import it.eng.parer.grantedEntity.UsrUser;
import it.eng.parer.grantedViewEntity.OrgVRicEnteConvenzByEsterno;
import it.eng.parer.grantedViewEntity.UsrVAbilAmbEnteConvenz;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.job.allineamentoOrganizzazioni.utils.CostantiReplicaOrg;
import it.eng.parer.viewEntity.OrgVRicAmbiente;
import it.eng.parer.viewEntity.OrgVRicEnte;
import it.eng.parer.viewEntity.UsrVAbilAmbSacerXstrut;
import it.eng.parer.viewEntity.UsrVAbilAmbXente;
import it.eng.parer.viewEntity.UsrVAbilEnteSacerXstrut;
import it.eng.parer.viewEntity.UsrVChkCreaAmbSacer;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.Constants;

@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class AmbientiHelper extends GenericHelper {

    /**
     * Metodo che ritorna l'entità OrgEnte corrispondente al nome e all'idAmbiente passato come
     * parametro
     *
     * @param nmEnte nome dell'ente
     * @param idAmb  idAmbiente associato
     *
     * @return OrgEnte istanza
     */
    public OrgEnte getOrgEnteByName(String nmEnte, BigDecimal idAmb) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT ente FROM OrgEnte ente  WHERE ente.nmEnte= :nmEnte ");

        if (idAmb != null) {
            queryStr.append(" AND ente.orgAmbiente.idAmbiente = :idAmb");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("nmEnte", nmEnte);

        if (idAmb != null) {
            query.setParameter("idAmb", longFromBigDecimal(idAmb));
        }

        List<OrgEnte> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    /**
     * Metodo che ritorna l'entità OrgAmbiente corrispondente al nome passato come parametro
     *
     * @param nmAmbiente nome ambiente
     *
     * @return OrgAmbiente istanza
     */
    public OrgAmbiente getOrgAmbienteByName(String nmAmbiente) {
        String queryStr = "SELECT amb FROM OrgAmbiente amb WHERE amb.nmAmbiente= :nmAmbiente";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmAmbiente", nmAmbiente);
        List<OrgAmbiente> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    /**
     * Metodo che ritorna una lista di OrgAmbiente ordinati per nome
     *
     * @return List
     */
    public List<OrgAmbiente> findOrgAmbienteList() {
        String queryStr = "SELECT amb FROM OrgAmbiente amb ORDER BY amb.nmAmbiente";
        Query query = getEntityManager().createQuery(queryStr);
        List<OrgAmbiente> list = query.getResultList();
        return list;
    }

    public List<OrgAmbitoTerrit> getOrgAmbitoTerritList(String tipo) {
        // Query nativa
        StringBuilder queryStr = new StringBuilder(
                "SELECT * " + " FROM SACER_IAM.ORG_AMBITO_TERRIT a");

        if (tipo != null) {
            queryStr.append(" WHERE a.ti_ambito_territ LIKE '").append(tipo).append("'");
        }

        queryStr.append(" CONNECT BY PRIOR a.id_ambito_territ =  a.id_ambito_territ_padre"
                + " START WITH a.id_ambito_territ_padre is null "
                + " ORDER SIBLINGS BY a.cd_ambito_territ ASC");

        Query query = getEntityManager().createNativeQuery(queryStr.toString(),
                OrgAmbitoTerrit.class);

        List<OrgAmbitoTerrit> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list;
    }

    public OrgAmbitoTerrit getOrgAmbitoTerritByCode(String cdAmbitoTerritoriale) {
        String queryStr = "SELECT a " + "FROM OrgAmbitoTerrit a "
                + "WHERE a.cdAmbitoTerrit = :cdAmbitoTerritoriale";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdAmbitoTerritoriale", cdAmbitoTerritoriale);
        List<OrgAmbitoTerrit> list = query.getResultList();

        if (list.isEmpty()) {

            return null;
        }
        return list.get(0);

    }

    public List<OrgAmbitoTerrit> getOrgAmbitoTerritChildList(BigDecimal idAmbitoTerritoriale) {

        String queryStr = "SELECT a " + "FROM OrgAmbitoTerrit a "
                + "WHERE a.orgAmbitoTerrit.idAmbitoTerrit = :idAmbitoTerritoriale";

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idAmbitoTerritoriale", longFromBigDecimal(idAmbitoTerritoriale));

        List<OrgAmbitoTerrit> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return list;

    }

    public List<OrgAmbitoTerrit> getOrgAmbitoTerritChildList(List<BigDecimal> idAmbitoTerrit) {
        List<OrgAmbitoTerrit> list = null;
        if (!idAmbitoTerrit.isEmpty()) {
            String queryStr = "SELECT a " + "FROM OrgAmbitoTerrit a "
                    + "WHERE a.orgAmbitoTerrit.idAmbitoTerrit IN (:idAmbitoTerrit)";
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idAmbitoTerrit", longListFrom(idAmbitoTerrit));
            list = query.getResultList();
            if (list.isEmpty()) {
                return null;
            }
        }
        return list;
    }

    public List<OrgCategEnte> getOrgCategEnteList(String cdCategEnte, String dsCategEnte) {

        StringBuilder queryStr = new StringBuilder(
                "SELECT orgCategEnte " + "FROM OrgCategEnte orgCategEnte");

        String whereWord = " WHERE ";
        if (cdCategEnte != null) {
            queryStr.append(whereWord)
                    .append(" UPPER(orgCategEnte.cdCategEnte) LIKE  :cdCategEnte ");
            whereWord = " AND ";
        }
        if (dsCategEnte != null) {
            queryStr.append(whereWord)
                    .append(" UPPER(orgCategEnte.dsCategEnte) LIKE  :dsCategEnte ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (cdCategEnte != null) {
            query.setParameter("cdCategEnte", "%" + cdCategEnte.toUpperCase() + "%");
        }
        if (dsCategEnte != null) {
            query.setParameter("dsCategEnte", "%" + dsCategEnte.toUpperCase() + "%");
        }

        List<OrgCategEnte> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list;

    }

    public OrgCategEnte getOrgCategEnteByCd(String cdCategEnte) {
        String queryStr = "SELECT orgCategEnte " + "FROM OrgCategEnte orgCategEnte "
                + "WHERE orgCategEnte.cdCategEnte LIKE :cdCategEnte";

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("cdCategEnte", cdCategEnte);

        List<OrgCategEnte> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    // MAC#24363
    public OrgCategEnte getOrgCategEnteByDesc(String dsCategEnte) {
        String queryStr = "SELECT orgCategEnte " + "FROM OrgCategEnte orgCategEnte "
                + "WHERE orgCategEnte.dsCategEnte LIKE :dsCategEnte";

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("dsCategEnte", dsCategEnte);

        List<OrgCategEnte> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }
    // end MAC#24363

    public <T extends Serializable> IamOrganizDaReplic insertEntityIamOrganizDaReplic(T entity,
            ApplEnum.TiOperReplic tipoOperazione) throws ParerUserError {
        String tipoOrganiz = null;
        String nomeOrganiz = null;
        BigDecimal idOrganiz = null;
        if (entity instanceof OrgAmbiente) {
            OrgAmbiente amb = (OrgAmbiente) entity;
            tipoOrganiz = ApplEnum.NmOrganizReplic.AMBIENTE.name();
            nomeOrganiz = amb.getNmAmbiente();
            idOrganiz = new BigDecimal(amb.getIdAmbiente());
        } else if (entity instanceof OrgEnte) {
            OrgEnte ente = (OrgEnte) entity;
            tipoOrganiz = ApplEnum.NmOrganizReplic.ENTE.name();
            nomeOrganiz = ente.getNmEnte();
            idOrganiz = new BigDecimal(ente.getIdEnte());
        }

        if (tipoOrganiz != null && nomeOrganiz != null && idOrganiz != null) {
            IamOrganizDaReplic replica = new IamOrganizDaReplic();
            replica.setIdOrganizApplic(idOrganiz);
            replica.setNmTipoOrganiz(tipoOrganiz);
            replica.setNmOrganiz(nomeOrganiz);
            replica.setTiOperReplic(tipoOperazione.name());
            replica.setTiStatoReplic(CostantiReplicaOrg.TiStatoReplic.DA_REPLICARE.name());
            replica.setDtLogOrganizDaReplic(new Date());
            getEntityManager().persist(replica);
            getEntityManager().flush();
            return replica;
        } else {
            throw new ParerUserError(
                    "Errore imprevisto in fase di inserimento di un record di replica di AMBIENTE o ENTE");
        }
    }

    public List<OrgCategStrut> getOrgCategStrutList() {
        Query q = getEntityManager().createQuery("SELECT categStrut FROM OrgCategStrut categStrut");
        return q.getResultList();
    }

    public UsrVChkCreaAmbSacer getUsrVChkCreaAmbSacer(long idUser, String nmApplic) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM UsrVChkCreaAmbSacer u WHERE u.id.idUserIam = :idUserIam AND u.nmApplic = :nmApplic");
        query.setParameter("idUserIam", bigDecimalFromLong(idUser));
        query.setParameter("nmApplic", nmApplic);
        return (UsrVChkCreaAmbSacer) query.getSingleResult();
    }

    public List<UsrVAbilAmbXente> getAmbientiAbilitatiPerEnte(long idUser, String nmApplic) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM UsrVAbilAmbXente u WHERE u.id.idUserIam = :idUserIam AND u.nmApplic = :nmApplic AND u.dtIniVal <= :dtCorrente AND u.dtFinVal >= :dtCorrente ORDER BY u.nmOrganiz");
        query.setParameter("idUserIam", bigDecimalFromLong(idUser));
        query.setParameter("nmApplic", nmApplic);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        query.setParameter("dtCorrente", c.getTime());
        return query.getResultList();
    }

    public List<UsrVAbilAmbSacerXstrut> getAmbientiAbilitatiPerStrut(long idUser, String nmApplic) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM UsrVAbilAmbSacerXstrut u WHERE u.usrVAbilAmbSacerXstrutId.idUserIam = :idUserIam AND u.nmApplic = :nmApplic ORDER BY u.nmOrganiz");
        query.setParameter("idUserIam", bigDecimalFromLong(idUser));
        query.setParameter("nmApplic", nmApplic);
        return query.getResultList();
    }

    public List<UsrVAbilEnteSacerXstrut> getEntiAbilitatiPerStrut(long idUser, String nmApplic,
            String nmEnte, BigDecimal idAmbiente, List<String> tipoDefTemplateEnte) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT u FROM UsrVAbilEnteSacerXstrut u WHERE u.id.idUserIam = :idUserIam AND u.nmApplic = :nmApplic ");
        if (StringUtils.isNotBlank(nmEnte)) {
            queryStr.append(" AND UPPER(u.nmOrganiz) = :nmEnte");
        }
        if (idAmbiente != null) {
            queryStr.append(" AND u.idOrganizApplicPadre = :idAmbiente");
        }
        if (tipoDefTemplateEnte != null && !tipoDefTemplateEnte.isEmpty()) {
            queryStr.append(
                    " AND EXISTS (SELECT orgEnte FROM OrgEnte orgEnte WHERE orgEnte.idEnte = u.id.idOrganizApplic AND orgEnte.tipoDefTemplateEnte IN (:tipoDefTemplateEnte)) ");
        }
        queryStr.append(" ORDER BY u.nmOrganiz");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUserIam", bigDecimalFromLong(idUser));
        query.setParameter("nmApplic", nmApplic);
        if (StringUtils.isNotBlank(nmEnte)) {
            query.setParameter("nmEnte", nmEnte.toUpperCase());
        }
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (tipoDefTemplateEnte != null && !tipoDefTemplateEnte.isEmpty()) {
            query.setParameter("tipoDefTemplateEnte", tipoDefTemplateEnte);
        }
        return query.getResultList();
    }

    public List<OrgVRicAmbiente> getAmbientiAbilitatiRicerca(long idUser, String nmAmbiente) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT amb FROM OrgVRicAmbiente amb WHERE amb.idUserIam = :idUserIam");
        if (StringUtils.isNotBlank(nmAmbiente)) {
            queryStr.append(" AND UPPER(amb.nmAmbiente) = :nmAmbiente");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUserIam", bigDecimalFromLong(idUser));
        if (StringUtils.isNotBlank(nmAmbiente)) {
            query.setParameter("nmAmbiente", nmAmbiente.toUpperCase());
        }
        return query.getResultList();
    }

    /**
     *
     * Metodo che restituisce una lista di OrgEnte corrispondente ai criteri di ricerca passati come
     * parametro
     *
     * @param idUtente            id utente
     * @param idAmbiente          id ambiente
     * @param nmEnte              nome ente
     * @param tipoDefTemplateEnte tipo definizione template ente
     *
     * @return lista di entità OrgEnte
     */
    public List<OrgVRicEnte> getEntiAbilitatiRicerca(long idUtente, BigDecimal idAmbiente,
            String nmEnte, String tipoDefTemplateEnte) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT ente FROM OrgVRicEnte ente WHERE ente.id.idUserIam = :idUtente");
        String cond = " AND ";
        if (idAmbiente != null) {
            queryStr.append(cond).append("ente.idAmbiente= :idAmb ");
            cond = " AND ";
        }

        if (StringUtils.isNotBlank(nmEnte)) {
            queryStr.append(cond).append("UPPER(ente.nmEnte) LIKE :nmEnte ");
            cond = " AND ";
        }

        if (StringUtils.isNotBlank(tipoDefTemplateEnte)) {
            queryStr.append(cond).append("ente.tipoDefTemplateEnte = :tipoDefTemplateEnte ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUtente", bigDecimalFromLong(idUtente));

        if (idAmbiente != null) {
            query.setParameter("idAmb", idAmbiente);
        }
        if (StringUtils.isNotBlank(nmEnte)) {
            query.setParameter("nmEnte", "%" + nmEnte.toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(tipoDefTemplateEnte)) {
            query.setParameter("tipoDefTemplateEnte", tipoDefTemplateEnte);
        }

        return query.getResultList();
    }

    /**
     *
     * Metodo che restituisce una lista di enti validi alla data corrente associati all'ambiente
     *
     * @param idAmbiente id ambiente
     *
     * @return lista di entità OrgEnte
     */
    public List<OrgEnte> getEntiValidiAmbiente(BigDecimal idAmbiente) {
        String queryStr = "SELECT ente FROM OrgEnte ente "
                + "WHERE ente.orgAmbiente.idAmbiente= :idAmbiente "
                + "AND ente.dtIniVal < :dtCorrente AND ente.dtFineVal > :dtCorrente ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        query.setParameter("dtCorrente", new Date());

        return query.getResultList();
    }

    public List<UsrUser> getUtentiAttiviAbilitatiAdAmbiente(BigDecimal idAmbiente) {
        String queryStr = "SELECT DISTINCT user FROM UsrDichAbilOrganiz dichAbilOrganiz "
                + "JOIN dichAbilOrganiz.usrUsoUserApplic usoUserApplic "
                + "JOIN usoUserApplic.usrUser user "
                + "JOIN dichAbilOrganiz.usrOrganizIam organizIam "
                + "JOIN organizIam.siAplTipoOrganiz tipoOrganiz "
                + "WHERE dichAbilOrganiz.tiScopoDichAbilOrganiz = 'ALL_ORG_CHILD' "
                + "AND tipoOrganiz.nmTipoOrganiz = 'AMBIENTE' " + "AND user.flAttivo = '1' "
                + "AND usoUserApplic.aplApplic.nmApplic = 'SACER' "
                + "AND organizIam.idOrganizApplic = :idAmbiente ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        return query.getResultList();
    }

    /* INIZIO DA TESTARE */

    public List<UsrUser> getUtentiAttiviAbilitatiAdAmbiente2(BigDecimal idAmbiente) {
        String queryStr = "SELECT DISTINCT user FROM UsrVAbilAmbSacerXente abilAmbSacerXente, UsrUser user "
                + "WHERE user.flAttivo = '1' " + "AND abilAmbSacerXente.nmApplic = 'SACER' "
                + "AND abilAmbSacerXente.id.idOrganizApplic = :idAmbiente "
                + "AND user.id.idUserIam = abilAmbSacerXente.id.idUserIam ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAmbiente", idAmbiente);
        return query.getResultList();
    }

    public List<UsrUser> getUtentiAttiviAbilitatiAdEnte(BigDecimal idEnte) {
        String queryStr = "SELECT DISTINCT user FROM UsrVAbilEnteSacerXstrut abilEnteSacerXstrut, UsrUser user "
                + "WHERE user.flAttivo = '1' " + "AND abilEnteSacerXstrut.nmApplic = 'SACER' "
                + "AND abilEnteSacerXstrut.id.idOrganizApplic = :idEnte "
                + "AND user.idUserIam = abilEnteSacerXstrut.id.idUserIam ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idEnte", idEnte);
        return query.getResultList();
    }

    /* FINE DA TESTARE */

    /**
     * Recupera dalla tabella di SACER_IAM il nome ente convenzionato per la struttura passata in
     * input
     *
     * @param nmApplic        nome applicazione
     * @param nmTipoOrganiz   nome tipo organizzazione
     * @param idOrganizApplic id organizzazione applicazione
     *
     * @return String nome
     */
    public String getNmEnteConvenz(String nmApplic, String nmTipoOrganiz,
            BigDecimal idOrganizApplic) {
        String nmEnteConvenz = null;
        Query q = getEntityManager().createQuery(
                "SELECT enteConvenzByOrganiz.nmEnteConvenz FROM OrgVEnteConvenzByOrganiz enteConvenzByOrganiz "
                        + "WHERE enteConvenzByOrganiz.idOrganizApplic = :idOrganizApplic "
                        + "AND enteConvenzByOrganiz.nmTipoOrganiz = :nmTipoOrganiz "
                        + "AND enteConvenzByOrganiz.nmApplic = :nmApplic ");
        q.setParameter("idOrganizApplic", idOrganizApplic);
        q.setParameter("nmApplic", nmApplic);
        q.setParameter("nmTipoOrganiz", nmTipoOrganiz);
        List<String> nmEnteConvenzList = q.getResultList();
        if (!nmEnteConvenzList.isEmpty()) {
            nmEnteConvenz = nmEnteConvenzList.get(0);
        }
        return nmEnteConvenz;
    }

    /**
     * Recupera l'ambiente in base alle abilitazioni
     *
     * @param idUtente id utente
     *
     * @return lista oggetti di tipo {@link OrgAmbiente}
     */
    public List<OrgAmbiente> retrieveOrgAmbienteFromAbil(long idUtente) {
        Query query = getEntityManager().createQuery(
                "SELECT DISTINCT strut.orgEnte.orgAmbiente FROM IamAbilOrganiz iao, OrgStrut strut WHERE iao.idOrganizApplic = strut.idStrut AND  iao.iamUser.idUserIam = :idutente ORDER BY strut.orgEnte.orgAmbiente.nmAmbiente");
        query.setParameter("idutente", idUtente);

        return query.getResultList();
    }

    public List<OrgEnte> retrieveOrgEnteAbilNoTemplate(long idUtente, Long idAmbiente,
            Boolean filterValid) {
        return retrieveOrgEnteAbil(idUtente, idAmbiente, null, null, filterValid, "NO_TEMPLATE",
                "TEMPLATE_DEF_ENTE");
    }

    public List<OrgEnte> retrieveOrgEnteAbil(long idUtente, Long idAmbiente,
            List<BigDecimal> idAmbitoTerritList, List<BigDecimal> idCategEnteList,
            Boolean filterValid) {
        return retrieveOrgEnteAbil(idUtente, idAmbiente, idAmbitoTerritList, idCategEnteList,
                filterValid, "NO_TEMPLATE", "TEMPLATE_DEF_ENTE");
    }

    public List<OrgEnte> retrieveOrgEnteAbil(long idUtente, List<BigDecimal> idAmbiente,
            List<BigDecimal> idAmbitoTerritList, List<BigDecimal> idCategEnteList,
            Boolean filterValid) {
        return retrieveOrgEnteAbil(idUtente, idAmbiente, idAmbitoTerritList, idCategEnteList,
                filterValid, "NO_TEMPLATE", "TEMPLATE_DEF_ENTE");
    }

    public List<OrgEnte> retrieveOrgEnteAbil(long idUtente, Long idAmbiente,
            List<BigDecimal> idAmbitoTerritList, List<BigDecimal> idCategEnteList,
            Boolean filterValid, String... tipoDefTemplateEnte) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT ente FROM IamAbilOrganiz iao, OrgStrut strut ");
        if (idAmbitoTerritList != null && !idAmbitoTerritList.isEmpty()) {
            queryStr.append(", SIOrgEnteSiam enteConvenz ");
        }
        queryStr.append("JOIN strut.orgEnte ente JOIN ente.orgAmbiente ambiente ");
        if (idCategEnteList != null && !idCategEnteList.isEmpty()) {
            queryStr.append("JOIN ente.orgCategEnte categEnte ");
        }
        queryStr.append(
                " WHERE strut.idStrut = iao.idOrganizApplic AND iao.iamUser.idUserIam = :idutente ");
        if (idAmbiente != null) {
            queryStr.append("AND ambiente.idAmbiente = :idAmbiente ");
        }
        if (idAmbitoTerritList != null && !idAmbitoTerritList.isEmpty()) {
            queryStr.append("AND strut.idEnteConvenz = enteConvenz.idEnteSiam ");
            queryStr.append("AND enteConvenz.idAmbitoTerrit IN (:idAmbitoTerritList) ");
        }
        if (idCategEnteList != null && !idCategEnteList.isEmpty()) {
            queryStr.append("AND categEnte.idCategEnte IN (:idCategEnteList) ");
        }
        if (tipoDefTemplateEnte != null && tipoDefTemplateEnte.length > 0) {
            queryStr.append("AND ente.tipoDefTemplateEnte");
            if (tipoDefTemplateEnte.length == 1) {
                queryStr.append(" = ");
            } else {
                queryStr.append(" IN ");
            }
            queryStr.append(":tipoDefTemplateEnte");
        }
        if (Boolean.TRUE.equals(filterValid)) {
            queryStr.append(" AND ente.flCessato = '0' ");
        }
        queryStr.append(" ORDER BY ente.nmEnte");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idutente", idUtente);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idAmbitoTerritList != null && !idAmbitoTerritList.isEmpty()) {
            query.setParameter("idAmbitoTerritList", idAmbitoTerritList);
        }
        if (idCategEnteList != null && !idCategEnteList.isEmpty()) {
            query.setParameter("idCategEnteList", longListFrom(idCategEnteList));
        }
        if (tipoDefTemplateEnte != null && tipoDefTemplateEnte.length > 0) {
            if (tipoDefTemplateEnte.length == 1) {
                query.setParameter("tipoDefTemplateEnte", tipoDefTemplateEnte[0]);
            } else {
                query.setParameter("tipoDefTemplateEnte", Arrays.asList(tipoDefTemplateEnte));
            }
        }
        return query.getResultList();
    }

    public List<OrgEnte> retrieveOrgEnteAbil(long idUtente, List<BigDecimal> idAmbienteList,
            List<BigDecimal> idAmbitoTerritList, List<BigDecimal> idCategEnteList,
            Boolean filterValid, String... tipoDefTemplateEnte) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT ente FROM IamAbilOrganiz iao, OrgStrut strut ");
        if (idAmbitoTerritList != null && !idAmbitoTerritList.isEmpty()) {
            queryStr.append(", SIOrgEnteSiam enteConvenz ");
        }
        queryStr.append("JOIN strut.orgEnte ente JOIN ente.orgAmbiente ambiente ");
        if (idCategEnteList != null && !idCategEnteList.isEmpty()) {
            queryStr.append("JOIN ente.orgCategEnte categEnte ");
        }
        queryStr.append(
                " WHERE strut.idStrut = iao.idOrganizApplic AND iao.iamUser.idUserIam = :idutente ");
        if (idAmbienteList != null && !idAmbienteList.isEmpty()) {
            queryStr.append("AND ambiente.idAmbiente IN :idAmbiente ");
        }
        if (idAmbitoTerritList != null && !idAmbitoTerritList.isEmpty()) {
            queryStr.append("AND strut.idEnteConvenz = enteConvenz.idEnteSiam ");
            queryStr.append("AND enteConvenz.idAmbitoTerrit IN :idAmbitoTerritList ");
        }
        if (idCategEnteList != null && !idCategEnteList.isEmpty()) {
            queryStr.append("AND categEnte.idCategEnte IN :idCategEnteList ");
        }
        if (tipoDefTemplateEnte != null && tipoDefTemplateEnte.length > 0) {
            queryStr.append("AND ente.tipoDefTemplateEnte");
            if (tipoDefTemplateEnte.length == 1) {
                queryStr.append(" = ");
            } else {
                queryStr.append(" IN ");
            }
            queryStr.append(":tipoDefTemplateEnte");
        }
        if (filterValid) {
            queryStr.append(" AND ente.flCessato = '0' ");
        }
        queryStr.append(" ORDER BY ente.nmEnte");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idutente", idUtente);
        if (idAmbienteList != null && !idAmbienteList.isEmpty()) {
            query.setParameter("idAmbiente", longListFrom(idAmbienteList));
        }
        if (idAmbitoTerritList != null && !idAmbitoTerritList.isEmpty()) {
            query.setParameter("idAmbitoTerritList", longListFrom(idAmbitoTerritList));
        }
        if (idCategEnteList != null && !idCategEnteList.isEmpty()) {
            query.setParameter("idCategEnteList", longListFrom(idCategEnteList));
        }
        if (tipoDefTemplateEnte != null && tipoDefTemplateEnte.length > 0) {
            if (tipoDefTemplateEnte.length == 1) {
                query.setParameter("tipoDefTemplateEnte", tipoDefTemplateEnte);
            } else {
                query.setParameter("tipoDefTemplateEnte", Arrays.asList(tipoDefTemplateEnte));
            }
        }
        List<OrgEnte> enteList = query.getResultList();
        return enteList;
    }

    public List<SIOrgEnteSiam> retrieveSiOrgEnteConvenz(BigDecimal idAmbienteEnteConvenz) {
        String queryStr = "SELECT enteSiam FROM SIOrgEnteSiam enteSiam "
                + "WHERE enteSiam.siOrgAmbienteEnteConvenz.idAmbienteEnteConvenz = :idAmbienteEnteConvenz ORDER BY enteSiam.nmEnteSiam";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAmbienteEnteConvenz", longFromBigDecimal(idAmbienteEnteConvenz));
        return query.getResultList();
    }

    public List<SIOrgEnteSiam> retrieveSiOrgEnteConvenzAccordoValido(
            BigDecimal idAmbienteEnteConvenz) {
        String queryStr = "SELECT enteSiam FROM SIOrgEnteSiam enteSiam "
                + "WHERE enteSiam.siOrgAmbienteEnteConvenz.idAmbienteEnteConvenz = :idAmbienteEnteConvenz "
                + "AND EXISTS (SELECT accordoEnte FROM SIOrgAccordoEnte accordoEnte "
                + "WHERE accordoEnte.dtDecAccordo <= :dataOdierna AND accordoEnte.dtScadAccordo >= :dataOdierna "
                + "AND accordoEnte.siOrgEnteConvenz = enteSiam) " + "ORDER BY enteSiam.nmEnteSiam";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAmbienteEnteConvenz", longFromBigDecimal(idAmbienteEnteConvenz));
        query.setParameter("dataOdierna", new Date());
        return query.getResultList();
    }

    public List<SIOrgEnteSiam> getEntiConvenzionatiAbilitati(long idUserIamCor,
            BigDecimal idAmbienteEnteConvenz) {
        List<SIOrgEnteSiam> entiSiamList = new ArrayList<>();
        String queryStr = "SELECT ricEnteConvenz FROM OrgVRicEnteConvenzByEsterno ricEnteConvenz "
                + "WHERE ricEnteConvenz.id.idUserIamCor = :idUserIamCor "
                + "AND ricEnteConvenz.idAmbienteEnteConvenz = :idAmbienteEnteConvenz "
                + "ORDER BY ricEnteConvenz.nmEnteConvenz ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAmbienteEnteConvenz", idAmbienteEnteConvenz);
        query.setParameter("idUserIamCor", bigDecimalFromLong(idUserIamCor));
        List<OrgVRicEnteConvenzByEsterno> ricEnteConvenzList = query.getResultList();

        for (OrgVRicEnteConvenzByEsterno ricEnteConvenz : ricEnteConvenzList) {
            entiSiamList.add(
                    this.findById(SIOrgEnteSiam.class, ricEnteConvenz.getId().getIdEnteConvenz()));
        }
        return entiSiamList;
    }

    public List<SIOrgEnteSiam> getEntiConvenzionatiValidiAbilitati(long idUserIamCor,
            BigDecimal idAmbienteEnteConvenz) {
        List<SIOrgEnteSiam> entiSiamList = new ArrayList<>();
        String queryStr = "SELECT ricEnteConvenz FROM OrgVRicEnteConvenzByEsterno ricEnteConvenz "
                + "WHERE ricEnteConvenz.id.idUserIamCor = :idUserIamCor "
                + "AND ricEnteConvenz.idAmbienteEnteConvenz = :idAmbienteEnteConvenz "
                + "AND ricEnteConvenz.flNonConvenz = '0' "
                + "ORDER BY ricEnteConvenz.nmEnteConvenz ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAmbienteEnteConvenz", idAmbienteEnteConvenz);
        query.setParameter("idUserIamCor", bigDecimalFromLong(idUserIamCor));
        List<OrgVRicEnteConvenzByEsterno> ricEnteConvenzList = query.getResultList();

        for (OrgVRicEnteConvenzByEsterno ricEnteConvenz : ricEnteConvenzList) {
            entiSiamList.add(
                    this.findById(SIOrgEnteSiam.class, ricEnteConvenz.getId().getIdEnteConvenz()));
        }
        return entiSiamList;
    }

    /**
     * Ritorna l'accordo valido alla data corrente per l'ente convenzionato dato in input
     *
     * @param idEnteConvenz id ente convenzionato
     *
     * @return l'accordo valido, o null
     */
    public SIOrgAccordoEnte retrieveOrgAccordoValidoEnteConvenz(BigDecimal idEnteConvenz) {
        Query query = getEntityManager().createQuery("SELECT accordo FROM SIOrgAccordoEnte accordo "
                + "WHERE accordo.siOrgEnteConvenz.idEnteSiam = :idEnteConvenz "
                + "AND accordo.dtDecAccordo <= :dataOdierna AND accordo.dtFineValidAccordo >= :dataOdierna ");
        query.setParameter("idEnteConvenz", longFromBigDecimal(idEnteConvenz));
        query.setParameter("dataOdierna", new Date());
        List<SIOrgAccordoEnte> accordoList = query.getResultList();
        if (!accordoList.isEmpty() && accordoList.get(0) != null) {
            return accordoList.get(0);
        }
        return null;
    }

    public Long countOrgEnteConvenzByAmbitoTerrit(BigDecimal idAmbitoTerrit) {
        String queryStr = "SELECT COUNT(enteSiam) FROM SIOrgEnteSiam enteSiam WHERE enteSiam.idAmbitoTerrit = :idAmbitoTerrit";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAmbitoTerrit", idAmbitoTerrit);
        return (Long) query.getSingleResult();
    }

    public Long countOrgStoEnteConvenzByAmbitoTerrit(BigDecimal idAmbitoTerrit) {
        String queryStr = "SELECT COUNT(stoEnteConvenz) FROM OrgStoEnteConvenz stoEnteConvenz WHERE stoEnteConvenz.idAmbitoTerrit = :idAmbitoTerrit";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAmbitoTerrit", idAmbitoTerrit);
        return (Long) query.getSingleResult();
    }

    public List<UsrVAbilAmbEnteConvenz> retrieveAmbientiEntiConvenzAbilitati(BigDecimal idUserIam) {
        Query query = getEntityManager().createQuery(
                "SELECT abilAmbEnteConvenz FROM UsrVAbilAmbEnteConvenz abilAmbEnteConvenz "
                        + "WHERE abilAmbEnteConvenz.id.idUserIam = :idUserIam "
                        + "ORDER BY abilAmbEnteConvenz.nmAmbienteEnteConvenz ");
        query.setParameter("idUserIam", idUserIam);
        return query.getResultList();
    }

    public List<SIOrgEnteConvenzOrg> retrieveSIOrgEnteConvenzOrg(BigDecimal idStrut) {
        Query query = getEntityManager()
                .createQuery("SELECT enteOrg FROM SIOrgEnteConvenzOrg enteOrg "
                        + "JOIN FETCH enteOrg.siOrgEnteConvenz e "
                        + "JOIN FETCH enteOrg.siUsrOrganizIam organizIam "
                        + "WHERE organizIam.idOrganizApplic = :idStrut "
                        + "AND organizIam.siAplTipoOrganiz.nmTipoOrganiz = 'STRUTTURA' "
                        + "AND organizIam.sIAplApplic.nmApplic = 'SACER' ORDER BY enteOrg.dtIniVal DESC ");
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        return (List<SIOrgEnteConvenzOrg>) query.getResultList();
    }

    public SIOrgEnteConvenzOrg getSIOrgEnteConvenzOrg(BigDecimal idStrut, BigDecimal idEnteConvenz,
            Date dtIniVal) {
        Query query = getEntityManager()
                .createQuery("SELECT enteOrg FROM SIOrgEnteConvenzOrg enteOrg "
                        + "WHERE enteOrg.siUsrOrganizIam.idOrganizApplic = :idStrut "
                        + "AND enteOrg.dtIniVal = :dtIniVal "
                        + "AND enteOrg.siOrgEnteConvenz.idEnteSiam = :idEnteConvenz");
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("idEnteConvenz", longFromBigDecimal(idEnteConvenz));
        query.setParameter("dtIniVal", dtIniVal);
        return (SIOrgEnteConvenzOrg) query.getSingleResult();
    }

    public SIOrgAmbienteEnteConvenz getSIOrgAmbienteEnteConvenzByEnteConvenz(
            BigDecimal idEnteConvenz) {
        Query query = getEntityManager()
                .createQuery("SELECT ambienteEnteConvenz FROM SIOrgEnteSiam enteSiam "
                        + "JOIN enteSiam.siOrgAmbienteEnteConvenz ambienteEnteConvenz "
                        + "WHERE enteSiam.idEnteSiam = :idEnteConvenz ");
        query.setParameter("idEnteConvenz", longFromBigDecimal(idEnteConvenz));
        return (SIOrgAmbienteEnteConvenz) query.getSingleResult();
    }

    public SIUsrOrganizIam getSIUsrOrganizIam(BigDecimal idStrut) {
        List<SIUsrOrganizIam> siuoi;

        String queryStr = "select t from SIUsrOrganizIam t "
                + "where t.sIAplApplic.nmApplic = :nmApplic "
                + "and t.siAplTipoOrganiz.nmTipoOrganiz = :nmTipoOrganiz "
                + "and t.idOrganizApplic = :idOrganizApplic ";
        javax.persistence.Query query = getEntityManager().createQuery(queryStr,
                SIUsrOrganizIam.class);
        query.setParameter("nmApplic", Constants.SACER);
        query.setParameter("nmTipoOrganiz", "STRUTTURA");
        query.setParameter("idOrganizApplic", longFromBigDecimal(idStrut));
        siuoi = query.getResultList();
        if (siuoi.size() != 1) {
            return null;
        } else {
            return siuoi.get(0);
        }
    }

    public String checkOrgVChkServFattByStrut(long idEnteConvenz, long idStrut, Date dtIniVal) {
        Query query = getEntityManager().createQuery(
                "SELECT vChk.flEliminaEnteStrut FROM OrgVChkServFattByStrut vChk WHERE vChk.id.idEnteConvenz = :idEnteConvenz AND vChk.id.idStrut = :idStrut AND vChk.id.dtIniVal = :dtIniVal");
        query.setParameter("idStrut", bigDecimalFromLong(idStrut));
        query.setParameter("idEnteConvenz", bigDecimalFromLong(idEnteConvenz));
        query.setParameter("dtIniVal", dtIniVal);
        return (String) query.getSingleResult();
    }

    public boolean existsCdEnteNormaliz(String cdEnteNormaliz, BigDecimal idEnteExcluded) {
        String queryStr = "SELECT ente FROM OrgEnte ente "
                + "WHERE ente.cdEnteNormaliz = :cdEnteNormaliz ";

        if (idEnteExcluded != null) {
            queryStr = queryStr + "AND ente.idEnte != :idEnteExcluded ";
        }
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdEnteNormaliz", cdEnteNormaliz);
        if (idEnteExcluded != null) {
            query.setParameter("idEnteExcluded", longFromBigDecimal(idEnteExcluded));
        }
        return !query.getResultList().isEmpty();
    }

    public List<OrgVRicEnteConvenzByEsterno> getOrgVRicEnteConvenzByEstList(BigDecimal idUserIamCor,
            BigDecimal idAmbienteEnteConvenz, String tiEnteConvenz) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT ricEnteConvenz FROM OrgVRicEnteConvenzByEsterno ricEnteConvenz "
                        + "WHERE ricEnteConvenz.id.idUserIamCor = :idUserIamCor "
                        + "AND ricEnteConvenz.idAmbienteEnteConvenz = :idAmbienteEnteConvenz ");
        if (tiEnteConvenz != null) {
            queryStr.append("AND ricEnteConvenz.tiEnteConvenz != :tiEnteConvenz ");
        }
        queryStr.append("AND ricEnteConvenz.flNonConvenz = '0' ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUserIamCor", idUserIamCor);
        query.setParameter("idAmbienteEnteConvenz", idAmbienteEnteConvenz);
        if (tiEnteConvenz != null) {
            query.setParameter("tiEnteConvenz", tiEnteConvenz);
        }
        return query.getResultList();
    }

    public List<SIOrgEnteSiam> getEnteConvenzConservList(long idUserIamCor,
            BigDecimal idEnteSiamGestore) {
        Query query = getEntityManager().createQuery(
                "SELECT DISTINCT enteConvenzConserv FROM OrgVRicEnteConvenzByEsterno ricEnteConvenzByEst, SIOrgAccordoEnte accordoEnte "
                        + "JOIN accordoEnte.siOrgEnteConvenz enteConvenz "
                        + "JOIN accordoEnte.orgEnteSiamByIdEnteConvenzGestore enteConvenzGestore "
                        + "JOIN accordoEnte.orgEnteSiamByIdEnteConvenzConserv enteConvenzConserv "
                        + "WHERE ricEnteConvenzByEst.id.idEnteConvenz = enteConvenz.idEnteSiam "
                        + "AND ricEnteConvenzByEst.id.idUserIamCor = :idUserIamCor "
                        + "AND enteConvenzGestore.idEnteSiam = :idEnteSiamGestore "
                        + "AND :dtCorrente BETWEEN accordoEnte.dtDecAccordo AND accordoEnte.dtFineValidAccordo ");
        query.setParameter("idUserIamCor", bigDecimalFromLong(idUserIamCor));
        query.setParameter("idEnteSiamGestore", longFromBigDecimal(idEnteSiamGestore));
        query.setParameter("dtCorrente", new Date());

        return query.getResultList();
    }

    public boolean checkDateAmbiente(BigDecimal idAmbiente, Date dtIniVal, Date dtFinVal) {
        Query query = getEntityManager().createQuery("SELECT ambiente FROM OrgAmbiente ambiente "
                + "WHERE ambiente.idAmbiente = :idAmbiente "
                + "AND ambiente.dtIniVal <= :dtIniVal AND ambiente.dtFinVal >= :dtIniVal "
                + "AND ambiente.dtIniVal <= :dtFinVal AND ambiente.dtFinVal >= :dtFinVal ");
        query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        query.setParameter("dtIniVal", dtIniVal);
        query.setParameter("dtFinVal", dtFinVal);
        List<OrgAmbiente> lista = query.getResultList();
        return !lista.isEmpty();
    }

    public boolean checkIntervalloSuStorico(BigDecimal idEnte, Date dtIniVal, Date dtFinVal) {
        Query query = getEntityManager().createQuery(
                "SELECT storicoEnteAmbiente FROM OrgStoricoEnteAmbiente storicoEnteAmbiente "
                        + "WHERE storicoEnteAmbiente.orgEnte.idEnte = :idEnte "
                        + "AND ((storicoEnteAmbiente.dtIniVal <= :dtIniVal AND storicoEnteAmbiente.dtFinVal >= :dtIniVal) "
                        + "OR (storicoEnteAmbiente.dtIniVal <= :dtFinVal AND storicoEnteAmbiente.dtFinVal >= :dtFinVal)) ");
        query.setParameter("idEnte", longFromBigDecimal(idEnte));
        query.setParameter("dtIniVal", dtIniVal);
        query.setParameter("dtFinVal", dtFinVal);
        List<OrgStoricoEnteAmbiente> lista = query.getResultList();
        return !lista.isEmpty();
    }

    public List<OrgStoricoEnteAmbiente> getOrgStoricoEnteAmbienteList(BigDecimal idEnte) {
        Query query = getEntityManager().createQuery(
                "SELECT storicoEnteAmbiente FROM OrgStoricoEnteAmbiente storicoEnteAmbiente "
                        + "WHERE storicoEnteAmbiente.orgEnte.idEnte = :idEnte ");
        query.setParameter("idEnte", longFromBigDecimal(idEnte));
        return query.getResultList();
    }

    public OrgVRicEnteConvenzByEsterno findOrgVRicEnteConvenzByEsterno(BigDecimal idEnteConvenz) {
        final String idEnteConvenzParam = "idEnteConvenz";
        final TypedQuery<OrgVRicEnteConvenzByEsterno> query = getEntityManager().createQuery(
                "SELECT e FROM OrgVRicEnteConvenzByEsterno e WHERE e.id.idEnteConvenz = :"
                        + idEnteConvenzParam,
                OrgVRicEnteConvenzByEsterno.class);
        query.setParameter(idEnteConvenzParam, idEnteConvenz);
        query.setFirstResult(0);
        query.setMaxResults(1);
        // ritorna il primo risultato, questa vista fornisce record tutti uguali fatto salvo
        // l'idUserCor che in questo
        // caso non ci interessa
        return query.getSingleResult();
    }

    public OrgStrut findOrgStrutById(Long id) {
        TypedQuery<OrgStrut> query = getEntityManager().createQuery(
                "SELECT o FROM OrgStrut o LEFT JOIN FETCH o.orgEnte e LEFT JOIN FETCH e.orgAmbiente WHERE o.idStrut = :idStrut",
                OrgStrut.class);
        query.setParameter("idStrut", id);
        return query.getSingleResult();
    }
}
