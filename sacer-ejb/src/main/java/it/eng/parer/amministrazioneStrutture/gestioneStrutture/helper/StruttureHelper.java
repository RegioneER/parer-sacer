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

package it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper;

import it.eng.parer.entity.*;
import it.eng.parer.grantedEntity.OrgAmbitoTerrit;
import it.eng.parer.grantedEntity.SIOrgAccordoEnte;
import it.eng.parer.grantedEntity.SIOrgEnteConvenzOrg;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.grantedViewEntity.OrgVRicEnteConvenzByEsterno;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.job.allineamentoEntiConvenzionati.utils.CostantiAllineaEntiConv;
import it.eng.parer.viewEntity.*;
import it.eng.parer.web.util.Constants;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.*;

@Stateless
@LocalBean
public class StruttureHelper extends GenericHelper {

    /**
     * Metodo per eseguire la merge di un entity, da utilizzare <strong>solo e solamente</strong> in caso di entity
     * DETACHED già presente su db, e di cui si stanno aggiornando i dati
     *
     * @param <T>
     *            Classe di tipo entity
     * @param entity
     *            entita jpa
     * 
     * @return l'entity aggiornata
     */
    public <T> T updateEntity(T entity) {
        entity = getEntityManager().merge(entity);
        getEntityManager().flush();
        getEntityManager().refresh(entity);
        return entity;
    }

    /*
     * Metodi per query di ricerca Aggiunta per ejb
     */
    public List<OrgStrut> retrieveOrgStrutList(String nmStrut, BigDecimal idEnte, BigDecimal idAmbiente,
            Boolean isTemplate) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT strut FROM OrgStrut strut JOIN strut.orgEnte ente WHERE strut.orgEnte.idEnte = ente.idEnte");

        if (StringUtils.isNotBlank(nmStrut)) {
            queryStr.append(" AND UPPER(strut.nmStrut) LIKE :nmStrut");
        }

        if (idEnte != null && idEnte.compareTo(BigDecimal.ZERO) != 0) {
            queryStr.append(" AND strut.orgEnte.idEnte=:idEnte");
        }
        if (idAmbiente != null && idAmbiente.compareTo(BigDecimal.ZERO) != 0) {
            queryStr.append(" AND strut.orgEnte.orgAmbiente.idAmbiente=:idAmbiente");
        }
        if (isTemplate != null) {
            if (Boolean.TRUE.equals(isTemplate)) {
                queryStr.append(" AND ente.tipoDefTemplateEnte IN ('TEMPLATE_DEF_AMBIENTE','TEMPLATE_DEF_ENTE')");
            } else {
                queryStr.append(" AND ente.tipoDefTemplateEnte IN ('NO_TEMPLATE','TEMPLATE_DEF_ENTE')");
            }
        }

        // creazione query dalla stringa
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (StringUtils.isNotBlank(nmStrut)) {
            query.setParameter("nmStrut", "%" + nmStrut + "%");
        }

        if (idEnte != null && idEnte.compareTo(BigDecimal.ZERO) != 0) {
            query.setParameter("idEnte", longFromBigDecimal(idEnte));
        }
        if (idAmbiente != null && idAmbiente.compareTo(BigDecimal.ZERO) != 0) {
            query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        }

        // getEntityManager().unwrap(JpagetEntityManager().class)
        return query.getResultList();
    }

    /**
     * Recupera le strutture in base all'ente (a sua volta recuperato in base alle abilitazioni)
     *
     * @param idUtente
     *            id utente
     * @param idEnte
     *            id ente
     * @param filterValid
     *            true/false
     * 
     * @return OrgStrutTableBean bean entity organizzazione struttura
     */
    public List<OrgStrut> retrieveOrgStrutList(long idUtente, BigDecimal idEnte, Boolean filterValid) {
        return retrieveOrgStrutList(idUtente, null, idEnte, null, null, null, filterValid);
    }

    public List<OrgVRicStrut> retrieveOrgVRicStrutList(String nmStrut, BigDecimal idEnte, BigDecimal idAmbiente,
            Boolean isTemplate, String partizionata, String nmSistemaVersante, BigDecimal idAmbitoTerrit,
            BigDecimal idCategEnte, BigDecimal idAmbienteEnteConvenz, BigDecimal idEnteConvenz, long idUserIamCor) {

        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT new it.eng.parer.viewEntity.OrgVRicStrut(ricStrut.idAmbiente, "
                        + "ricStrut.nmAmbiente, ricStrut.idEnte, ricStrut.nmEnte, ricStrut.id.idStrut, ricStrut.nmStrut, ricStrut.dsStrut, "
                        + "ricStrut.flTemplate, ricStrut.flPartOk, ricStrut.idAmbitoTerrit, ricStrut.tiAmbitoTerrit, ricStrut.dsTreeCdAmbitoTerrit, "
                        + "ricStrut.dsTreeIdAmbitoTerrit, ricStrut.idCategEnte, ricStrut.cdCategEnte) "
                        + "FROM OrgVRicStrut ricStrut " + "WHERE ricStrut.id.idUserIamCor = :idUserIamCor ");

        if (StringUtils.isNotBlank(nmStrut)) {
            queryStr.append(" AND UPPER(ricStrut.nmStrut) LIKE :nmStrut");
        }
        if (idEnte != null && idEnte.compareTo(BigDecimal.ZERO) != 0) {
            queryStr.append(" AND ricStrut.idEnte = :idEnte");
        }
        if (idAmbiente != null && idAmbiente.compareTo(BigDecimal.ZERO) != 0) {
            queryStr.append(" AND ricStrut.idAmbiente = :idAmbiente");
        }
        if (isTemplate != null) {
            queryStr.append(" AND ricStrut.flTemplate = :flTemplate");
        }
        if (StringUtils.isNotBlank(partizionata)) {
            queryStr.append(" AND ricStrut.flPartOk = :partizionata ");
        }
        if (StringUtils.isNotBlank(nmSistemaVersante)) {
            queryStr.append(" AND UPPER(ricStrut.nmSistemaVersante) LIKE :nmSistemaVersante ");
        }
        if (idAmbitoTerrit != null) {
            queryStr.append(" AND ricStrut.dsTreeIdAmbitoTerrit LIKE :idAmbitoTerrit ");
        }
        if (idCategEnte != null) {
            queryStr.append(" AND ricStrut.idCategEnte = :idCategEnte ");
        }
        if (idAmbienteEnteConvenz != null) {
            queryStr.append(" AND ricStrut.idAmbienteEnteConvenz = :idAmbienteEnteConvenz ");
        }
        if (idEnteConvenz != null) {
            queryStr.append(" AND ricStrut.idEnteConvenz = :idEnteConvenz ");
        }

        queryStr.append(" ORDER BY ricStrut.nmStrut ASC, ricStrut.nmEnte ASC ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUserIamCor", bigDecimalFromLong(idUserIamCor));

        if (StringUtils.isNotBlank(nmStrut)) {
            query.setParameter("nmStrut", "%" + nmStrut.toUpperCase() + "%");
        }
        if (idEnte != null && idEnte.compareTo(BigDecimal.ZERO) != 0) {
            query.setParameter("idEnte", idEnte);
        }
        if (idAmbiente != null && idAmbiente.compareTo(BigDecimal.ZERO) != 0) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (StringUtils.isNotBlank(partizionata)) {
            query.setParameter("partizionata", partizionata);
        }
        if (isTemplate != null) {
            query.setParameter("flTemplate", Boolean.TRUE.equals(isTemplate) ? "1" : "0");
        }
        if (StringUtils.isNotBlank(nmSistemaVersante)) {
            query.setParameter("nmSistemaVersante", "%" + nmSistemaVersante.toUpperCase() + "%");
        }
        if (idAmbitoTerrit != null) {
            query.setParameter("idAmbitoTerrit", "%/" + idAmbitoTerrit + "/%");
        }
        if (idCategEnte != null) {
            query.setParameter("idCategEnte", idCategEnte);
        }
        if (idAmbienteEnteConvenz != null) {
            query.setParameter("idAmbienteEnteConvenz", idAmbienteEnteConvenz);
        }
        if (idEnteConvenz != null) {
            query.setParameter("idEnteConvenz", idEnteConvenz);
        }

        return query.getResultList();
    }

    /**
     * Ricerca le strutture che rispondono ai parametri di ricerca impostati non associate al modello (non presenti
     * nella DEC_USO_MODELLO_TIPO_SERIE per l’id del modello da associare)
     *
     * @param nmStrut
     *            nome struttura
     * @param idEnte
     *            id ente
     * @param idAmbiente
     *            id ambiente
     * @param idModelloTipoSerie
     *            id modello tipo/serie
     * @param isTemplate
     *            true/false
     * @param idUtente
     *            id utente
     * @param filterValid
     *            true/false
     * 
     * @return lista oggetti di tipo {@link OrgStrut}
     */
    public List<OrgStrut> retrieveOrgStrutList(long idUtente, String nmStrut, BigDecimal idEnte, BigDecimal idAmbiente,
            BigDecimal idModelloTipoSerie, Boolean isTemplate, Boolean filterValid) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT strut " + "FROM IamAbilOrganiz iao, OrgStrut strut JOIN strut.orgEnte ente "
                        + "WHERE iao.idOrganizApplic = strut.idStrut ");
        queryStr.append(" and iao.iamUser.idUserIam = :idUtente");

        if (StringUtils.isNotBlank(nmStrut)) {
            queryStr.append(" AND UPPER(strut.nmStrut) LIKE :nmStrut");
        }

        if (idEnte != null && idEnte.compareTo(BigDecimal.ZERO) != 0) {
            queryStr.append(" AND strut.orgEnte.idEnte=:idEnte");
        }
        if (idAmbiente != null && idAmbiente.compareTo(BigDecimal.ZERO) != 0) {
            queryStr.append(" AND strut.orgEnte.orgAmbiente.idAmbiente=:idAmbiente");
        }
        if (isTemplate != null) {
            queryStr.append(" AND strut.flTemplate = :flTemplate");
        }

        if (idModelloTipoSerie != null) {
            queryStr.append(
                    " AND NOT EXISTS (SELECT d FROM DecUsoModelloTipoSerie d WHERE d.orgStrut.idStrut = strut.idStrut AND d.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie )");
        }
        if (Boolean.TRUE.equals(filterValid)) {
            queryStr.append(" AND strut.flCessato = '0' ");
        }
        queryStr.append(" ORDER BY ente.nmEnte, strut.nmStrut");

        // creazione query dalla stringa
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUtente", idUtente);

        if (StringUtils.isNotBlank(nmStrut)) {
            query.setParameter("nmStrut", "%" + nmStrut.toUpperCase() + "%");
        }

        if (idEnte != null && idEnte.compareTo(BigDecimal.ZERO) != 0) {
            query.setParameter("idEnte", longFromBigDecimal(idEnte));
        }
        if (idAmbiente != null && idAmbiente.compareTo(BigDecimal.ZERO) != 0) {
            query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        }
        if (idModelloTipoSerie != null) {
            query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
        }
        if (isTemplate != null) {
            query.setParameter("flTemplate", Boolean.TRUE.equals(isTemplate) ? "1" : "0");
        }

        return query.getResultList();
    }

    public List<OrgStrut> retrieveOrgStrutList(long idUtente, Collection<BigDecimal> idEntiSet,
            Collection<BigDecimal> idCategStrutList) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT strut FROM IamAbilOrganiz iao, OrgStrut strut JOIN strut.orgEnte ente ");
        if (idCategStrutList != null && !idCategStrutList.isEmpty()) {
            queryStr.append("JOIN strut.orgCategStrut categStrut ");
        }
        queryStr.append("WHERE iao.idOrganizApplic = strut.idStrut AND iao.iamUser.idUserIam = :idUtente ");
        if (idEntiSet != null && !idEntiSet.isEmpty()) {
            queryStr.append("AND strut.orgEnte.idEnte IN (:idEnte) ");
        }
        if (idCategStrutList != null && !idCategStrutList.isEmpty()) {
            queryStr.append("AND categStrut.idCategStrut IN (:idCategStrutList) ");
        }
        queryStr.append("ORDER BY ente.nmEnte, strut.nmStrut");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUtente", idUtente);
        if (idEntiSet != null && !idEntiSet.isEmpty()) {
            query.setParameter("idEnte", longListFrom(idEntiSet));
        }
        if (idCategStrutList != null && !idCategStrutList.isEmpty()) {
            query.setParameter("idCategStrutList", longListFrom(idCategStrutList));
        }
        return query.getResultList();
    }

    public OrgStrut updateOrgStrut(OrgStrut strut) {
        OrgStrut updStrut = findById(OrgStrut.class, strut.getIdStrut());

        String dsStrut = strut.getDsStrut();
        String dlNoteStrut = strut.getDlNoteStrut();
        String cdIpa = strut.getCdIpa();
        String cdStrutNormaliz = strut.getCdStrutNormaliz();

        String nmStrut = strut.getNmStrut();
        BigDecimal idEnteConvenz = strut.getIdEnteConvenz();
        Date dtIniVal = strut.getDtIniVal();
        Date dtFineVal = strut.getDtFineVal();
        Date dtIniValStrut = strut.getDtIniValStrut();
        Date dtFineValStrut = strut.getDtFineValStrut();
        String flArchivioRestituito = strut.getFlArchivioRestituito();
        OrgCategStrut orgCategStrut = strut.getOrgCategStrut();
        if (orgCategStrut != null) {
            long idCategStrut = orgCategStrut.getIdCategStrut();
            orgCategStrut = findById(OrgCategStrut.class, new BigDecimal(idCategStrut));
        }
        OrgEnte orgEnte = strut.getOrgEnte();
        if (orgEnte != null) {
            long idEnte = orgEnte.getIdEnte();
            orgEnte = findById(OrgEnte.class, idEnte);
        }
        updStrut.setDsStrut(dsStrut);
        updStrut.setDlNoteStrut(dlNoteStrut);
        updStrut.setCdIpa(cdIpa);
        updStrut.setNmStrut(nmStrut);
        updStrut.setCdStrutNormaliz(cdStrutNormaliz);

        updStrut.setIdEnteConvenz(idEnteConvenz);
        updStrut.setDtIniVal(dtIniVal);
        updStrut.setDtFineVal(dtFineVal);
        updStrut.setDtIniValStrut(dtIniValStrut);
        updStrut.setDtFineValStrut(dtFineValStrut);
        updStrut.setFlArchivioRestituito(flArchivioRestituito);
        updStrut.setOrgCategStrut(orgCategStrut);
        updStrut.setOrgEnte(orgEnte);
        getEntityManager().flush();
        return updStrut;
    }

    public Long getidStrutFromIdTipoStrutUnitaDoc(BigDecimal idTipoStrutUnitaDoc) {
        String queryStr = "SELECT tipoStrutUnitaDoc.decTipoUnitaDoc.orgStrut.idStrut FROM DecTipoStrutUnitaDoc tipoStrutUnitaDoc WHERE tipoStrutUnitaDoc.idTipoStrutUnitaDoc = :idTipoStrutUnitaDoc";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoStrutUnitaDoc", longFromBigDecimal(idTipoStrutUnitaDoc));

        List<Long> list = query.getResultList();

        return list.get(0);
    }

    public OrgStrut getOrgStrutByName(String nmStrut, BigDecimal idEnte) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT strut FROM OrgStrut strut " + "WHERE strut.nmStrut= :nmStrut ");
        if (idEnte != null) {
            queryStr.append(" AND strut.orgEnte.idEnte=:idEnte");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("nmStrut", nmStrut);
        if (idEnte != null) {
            query.setParameter("idEnte", longFromBigDecimal(idEnte));
        }
        List<OrgStrut> strutList = query.getResultList();
        if (strutList.isEmpty()) {
            return null;
        }
        return strutList.get(0);
    }

    public OrgSubStrut getOrgSubStrutByName(String nmSubStrut, OrgStrut orgStrut) {
        String queryStr = "SELECT subStrut FROM OrgSubStrut subStrut "
                + " WHERE subStrut.orgStrut=:orgStrut AND subStrut.nmSubStrut= :nmSubStrut ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("orgStrut", orgStrut);
        query.setParameter("nmSubStrut", nmSubStrut);
        List<OrgSubStrut> strutList = query.getResultList();
        if (strutList.isEmpty()) {
            return null;
        }
        return strutList.get(0);
    }

    public AplSistemaMigraz getAplSistemaMigrazByName(String nmSistemaMigraz) {
        String queryStr = "SELECT sistemaMigraz FROM AplSistemaMigraz sistemaMigraz "
                + " WHERE sistemaMigraz.nmSistemaMigraz = :nmSistemaMigraz ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmSistemaMigraz", nmSistemaMigraz);
        List<AplSistemaMigraz> strutList = query.getResultList();
        if (strutList.isEmpty()) {
            return null;
        }
        return strutList.get(0);
    }

    public void deleteOrgStrutRelations(OrgStrut oldStrut) {

        long idStrut = oldStrut.getIdStrut();
        // cancello registri
        StringBuilder queryStr = new StringBuilder(
                "DELETE FROM DecRegistroUnitaDoc registro " + "WHERE registro.orgStrut.idStrut = :idStrut");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", idStrut);

        query.executeUpdate();

        // cancello tipoUd
        queryStr = new StringBuilder(
                "DELETE FROM DecTipoUnitaDoc tipoUD " + "WHERE tipoUD.orgStrut.idStrut = :idStrut");

        query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", idStrut);
        query.executeUpdate();

        // cancello tipoDoc
        queryStr = new StringBuilder("DELETE FROM DecTipoDoc tipoDoc " + "WHERE tipoDoc.orgStrut.idStrut = :idStrut");

        query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", idStrut);
        query.executeUpdate();

        // cancello tipoStrutDoc
        queryStr = new StringBuilder(
                "DELETE FROM DecTipoStrutDoc tipoStrutDoc " + "WHERE tipoStrutDoc.orgStrut.idStrut = :idStrut");

        query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", idStrut);
        query.executeUpdate();

        // cancello formatoDoc
        queryStr = new StringBuilder(
                "DELETE FROM DecFormatoFileDoc formatoFileDoc " + "WHERE formatoFileDoc.orgStrut.idStrut = :idStrut");

        query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", idStrut);
        query.executeUpdate();

        // cancello tipoRappr
        queryStr = new StringBuilder(
                "DELETE FROM DecTipoRapprComp tipoRapprComp " + "WHERE tipoRapprComp.orgStrut.idStrut = :idStrut");

        query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", idStrut);
        query.executeUpdate();

    }

    public OrgStrut getFirstOrgStrutTemplate() {
        String queryStr = "SELECT strut FROM OrgStrut strut WHERE strut.flTemplate = '1'"
                + "ORDER BY strut.flTemplate DESC ";
        Query query = getEntityManager().createQuery(queryStr);
        List<OrgStrut> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * Restituisce la prima struttura template disponibile per l'ambiente passato come parametro. Null in caso non siano
     * disponibili strutture template
     *
     * @param idAmbiente
     *            id ambiente
     * @param tipoDefTemplateEnte
     *            tipo template ente
     * 
     * @return OrgStrut entity OrgStrut
     */
    public OrgStrut getFirstOrgStrutTemplatePerAmbienteAndTipoDefTemplateEnte(BigDecimal idAmbiente,
            String tipoDefTemplateEnte) {
        String queryStr = "SELECT strut FROM OrgStrut strut " + "WHERE strut.flTemplate = '1' ";

        if (idAmbiente != null) {
            queryStr = queryStr + "AND strut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ";
        }
        if (tipoDefTemplateEnte != null) {
            queryStr = queryStr + "AND strut.orgEnte.tipoDefTemplateEnte = :tipoDefTemplateEnte ";
        }
        Query query = getEntityManager().createQuery(queryStr);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        }
        if (tipoDefTemplateEnte != null) {
            query.setParameter("tipoDefTemplateEnte", tipoDefTemplateEnte);
        }
        List<OrgStrut> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * Restituisce la prima struttura template partizionata disponibile per l'ambiente passato come parametro. Se non ne
     * trova, restituisce la prima struttura template. Null in caso non siano disponibili strutture template
     *
     * @param idAmbiente
     *            id ambiente
     * @param tipoDefTemplateEnte
     *            tipo template
     * 
     * @return OrgStrut entity OrgStrut
     */
    public OrgStrut getFirstOrgStrutTemplatePerAmbienteAndTipoDefTemplateEntePartizionata(BigDecimal idAmbiente,
            String tipoDefTemplateEnte) {
        OrgStrut strut = null;
        if (idAmbiente != null && tipoDefTemplateEnte != null) {
            String queryStr = "SELECT strut FROM OrgStrut strut, OrgVChkStrutPartition chk "
                    + "WHERE strut.flTemplate = '1' " + "AND strut.idStrut = chk.idStrut " + "AND chk.flPartOk = '1' "
                    + "AND strut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente "
                    + "AND strut.orgEnte.tipoDefTemplateEnte = :tipoDefTemplateEnte ";
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
            query.setParameter("tipoDefTemplateEnte", tipoDefTemplateEnte);
            List<OrgStrut> list = query.getResultList();
            // Se non ho strutture template partizionate, provo con le non part
            if (list.isEmpty()) {
                String queryStr2 = "SELECT strut FROM OrgStrut strut " + "WHERE strut.flTemplate = '1' "
                        + "AND strut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente "
                        + "AND strut.orgEnte.tipoDefTemplateEnte = :tipoDefTemplateEnte ";
                Query query2 = getEntityManager().createQuery(queryStr2);
                query2.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
                query2.setParameter("tipoDefTemplateEnte", tipoDefTemplateEnte);
                List<OrgStrut> list2 = query2.getResultList();
                if (!list2.isEmpty()) {
                    strut = list2.get(0);
                }
            } else {
                strut = list.get(0);
            }
        }
        return strut;
    }

    /**
     * Restituisce la prima struttura template partizionata disponibile per l'ente passato come parametro. Se non ne
     * trova, restituisce la prima struttura template. Null in caso non siano disponibili strutture template
     *
     * @param idEnte
     *            id ente
     * 
     * @return OrgStrut entity OrgStrut
     */
    public OrgStrut getFirtsOrgStrutTemplatePerEntePartizionata(BigDecimal idEnte) {
        OrgStrut strut = null;
        if (idEnte != null) {
            String queryStr = "SELECT strut FROM OrgStrut strut, OrgVChkStrutPartition chk "
                    + "WHERE strut.flTemplate = '1' " + "AND strut.orgEnte.idEnte = :idEnte "
                    + "AND strut.idStrut = chk.idStrut " + "AND chk.flPartOk = '1' ";
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idEnte", longFromBigDecimal(idEnte));
            List<OrgStrut> list = query.getResultList();
            // Se non ho strutture template partizionate, provo con le non part
            if (list.isEmpty()) {
                String queryStr2 = "SELECT strut FROM OrgStrut strut " + "WHERE strut.flTemplate = '1' "
                        + "AND strut.orgEnte.idEnte = :idEnte ";
                Query query2 = getEntityManager().createQuery(queryStr2);
                query2.setParameter("idEnte", longFromBigDecimal(idEnte));
                List<OrgStrut> list2 = query2.getResultList();
                if (!list2.isEmpty()) {
                    strut = list2.get(0);
                }
            } else {
                strut = list.get(0);
            }
        }
        return strut;
    }

    /**
     * Conta il numero di strutture template definite su enti TEMPLATE_DEF_AMBIENTE per ogni ambiente e su enti
     * TEMPLATE_DEF_ENTE
     *
     * @param idUserIam
     *            id user IAM
     * 
     * @return l'object array con numeroStrutture e ambiente
     */
    public List<Object[]> countOrgStrutTemplateRaggruppati(long idUserIam) {
        String queryStr = "SELECT count(strut), ambiente.nmAmbiente " + "FROM OrgStrut strut "
                + "JOIN strut.orgEnte ente " + "JOIN ente.orgAmbiente ambiente," + "IamAbilOrganiz abilOrganiz "
                + "WHERE strut.flTemplate = '1' " + "AND strut.idStrut = abilOrganiz.idOrganizApplic "
                + "AND abilOrganiz.iamUser.idUserIam = :idUserIam "
                + "AND ente.tipoDefTemplateEnte = 'TEMPLATE_DEF_AMBIENTE' " + "GROUP BY ambiente.nmAmbiente "
                + "HAVING COUNT(strut) > 0";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUserIam", idUserIam);
        List<Object[]> strutTemplatePerAmbiente = query.getResultList();

        String queryStr2 = "SELECT count(strut), 'Enti con strutture template definite specificatamente per ente'  "
                + "FROM OrgStrut strut " + "JOIN strut.orgEnte ente, " + "IamAbilOrganiz abilOrganiz "
                + "WHERE strut.flTemplate = '1' " + "AND strut.idStrut = abilOrganiz.idOrganizApplic "
                + "AND abilOrganiz.iamUser.idUserIam = :idUserIam "
                + "AND ente.tipoDefTemplateEnte = 'TEMPLATE_DEF_ENTE' " + " GROUP BY 2 " + "HAVING COUNT(strut) > 0";
        Query query2 = getEntityManager().createQuery(queryStr2);
        query2.setParameter("idUserIam", idUserIam);
        List<Object[]> strutTemplatePerEntiDefEnte = query2.getResultList();

        strutTemplatePerAmbiente.addAll(strutTemplatePerEntiDefEnte);
        return strutTemplatePerAmbiente;
    }

    /**
     * Conta le strutture template filtrando in base ai parametri in ingresso
     *
     * @param idAmbiente
     *            id ambiente
     * @param idEnte
     *            id ente
     * @param tipoDefTemplateEnte
     *            tipo template
     * 
     * @return il numero di strutture template ricavate
     */
    public Long countOrgStrutTemplatePerAmbienteEnte(Long idAmbiente, Long idEnte, String tipoDefTemplateEnte) {
        StringBuilder queryStr = new StringBuilder("SELECT count(strut) " + "FROM OrgStrut strut "
                + "JOIN strut.orgEnte ente " + "JOIN ente.orgAmbiente ambiente " + "WHERE strut.flTemplate = '1' ");

        if (idAmbiente != null) {
            queryStr.append("AND ambiente.idAmbiente = :idAmbiente ");
        }
        if (idEnte != null) {
            queryStr.append("AND ente.idEnte = :idEnte ");
        }
        if (tipoDefTemplateEnte != null) {
            queryStr.append("AND ente.tipoDefTemplateEnte = :tipoDefTemplateEnte ");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }
        if (tipoDefTemplateEnte != null) {
            query.setParameter("tipoDefTemplateEnte", tipoDefTemplateEnte);
        }
        return (Long) query.getSingleResult();
    }

    /**
     * Conta il numero di strutture template partizionate definite su enti TEMPLATE_DEF_AMBIENTE per ogni ambiente e su
     * enti TEMPLATE_DEF_ENTE
     *
     * @param idUserIam
     *            id user IAM
     * 
     * @return l'object array con numeroStrutture e ambiente
     */
    public List<Object[]> countOrgStrutTemplateWithCompletedPartitioningRaggruppati(long idUserIam) {
        String queryStr = "SELECT count(strut), ambiente.nmAmbiente "
                + "FROM OrgStrut strut, OrgVChkStrutPartition part " + "JOIN strut.orgEnte ente "
                + "JOIN ente.orgAmbiente ambiente, " + "IamAbilOrganiz abilOrganiz "
                + "WHERE strut.idStrut = part.idStrut " + "AND strut.flTemplate = '1' " + "AND part.flPartOk = '1' "
                + "AND strut.idStrut = abilOrganiz.idOrganizApplic " + "AND abilOrganiz.iamUser.idUserIam = :idUserIam "
                + "AND ente.tipoDefTemplateEnte = 'TEMPLATE_DEF_AMBIENTE' " + "GROUP BY ambiente.nmAmbiente "
                + "HAVING COUNT(strut) > 0";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUserIam", idUserIam);
        List<Object[]> strutTemplatePerAmbiente = query.getResultList();

        String queryStr2 = "SELECT count(strut), 'Enti con strutture template definite specificatamente per ente'  "
                + "FROM OrgStrut strut, OrgVChkStrutPartition part " + "JOIN strut.orgEnte ente, "
                + "IamAbilOrganiz abilOrganiz " + "WHERE strut.idStrut = part.idStrut " + "AND strut.flTemplate = '1' "
                + "AND part.flPartOk = '1' " + "AND strut.idStrut = abilOrganiz.idOrganizApplic "
                + "AND abilOrganiz.iamUser.idUserIam = :idUserIam "
                + "AND ente.tipoDefTemplateEnte = 'TEMPLATE_DEF_ENTE' " + "GROUP BY 2 " + "HAVING COUNT(strut) > 0";
        Query query2 = getEntityManager().createQuery(queryStr2);
        query2.setParameter("idUserIam", idUserIam);
        List<Object[]> strutTemplatePerEntiDefEnte = query2.getResultList();

        strutTemplatePerAmbiente.addAll(strutTemplatePerEntiDefEnte);

        return strutTemplatePerAmbiente;
    }

    public List<OrgCategStrut> getOrgCategStrutList(String cdCategStrut, String dsCategStrut) {

        StringBuilder queryStr = new StringBuilder("SELECT orgCategStrut " + "FROM OrgCategStrut orgCategStrut");

        String whereWord = " WHERE ";
        if (cdCategStrut != null) {
            queryStr.append(whereWord).append(" UPPER(orgCategStrut.cdCategStrut) LIKE  :cdCategStrut ");
            whereWord = " AND ";
        }
        if (dsCategStrut != null) {
            queryStr.append(whereWord).append(" UPPER(orgCategStrut.dsCategStrut) LIKE  :dsCategStrut ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (cdCategStrut != null) {
            query.setParameter("cdCategStrut", "%" + cdCategStrut.toUpperCase() + "%");
        }
        if (dsCategStrut != null) {
            query.setParameter("dsCategStrut", "%" + dsCategStrut.toUpperCase() + "%");
        }

        List<OrgCategStrut> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list;

    }

    public OrgCategStrut getOrgCategStrutByCd(String cdCategStrut) {
        Query query = getEntityManager().createQuery("SELECT orgCategStrut " + "FROM OrgCategStrut orgCategStrut "
                + "WHERE orgCategStrut.cdCategStrut = :cdCategStrut");
        query.setParameter("cdCategStrut", cdCategStrut);
        List<OrgCategStrut> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public boolean hasAroUnitaDoc(BigDecimal idStrut) {
        String queryStr = "SELECT strut " + "FROM OrgStrut strut " + "WHERE EXISTS( SELECT ud " + "FROM AroUnitaDoc ud "
                + "WHERE ud.orgStrut.idStrut = :idStrut )";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        List<OrgStrut> list = query.getResultList();
        return !list.isEmpty();
    }

    public List<DecCriterioFiltroMultiplo> getRelationsWithCriteriRaggruppamento(long idTipoDato,
            Constants.TipoDato tipoDato) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT criterioFiltroMultiplo FROM DecCriterioFiltroMultiplo criterioFiltroMultiplo ");
        switch (tipoDato) {
        case REGISTRO:
            queryStr.append("WHERE criterioFiltroMultiplo.decRegistroUnitaDoc.idRegistroUnitaDoc = :idTipoDato "
                    + "OR criterioFiltroMultiplo.decRegistroRangeUnitaDoc.idRegistroUnitaDoc = :idTipoDato ");
            break;
        case TIPO_UNITA_DOC:
            queryStr.append("WHERE criterioFiltroMultiplo.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoDato ");
            break;
        case TIPO_DOC:
            queryStr.append("WHERE criterioFiltroMultiplo.decTipoDoc.idTipoDoc = :idTipoDato ");
            break;
        default:
            break;
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idTipoDato", idTipoDato);
        return query.getResultList();
    }

    /**
     * @param idTipoFascicolo
     *            id del tipo fasciolo
     * 
     * @return Lista di {@link DecSelCriterioRaggrFasc}
     */
    public List<DecSelCriterioRaggrFasc> getRelationsByIdTipoFascicolo(long idTipoFascicolo) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT selCriterioRaggrFasc FROM DecSelCriterioRaggrFasc selCriterioRaggrFasc ");
        queryStr.append("WHERE selCriterioRaggrFasc.decTipoFascicolo.idTipoFascicolo = :idTipoFascicolo ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idTipoFascicolo", idTipoFascicolo);
        return query.getResultList();
    }

    /**
     * @param idTipoDato
     *            id del tipo di dato
     * @param tipoDato
     *            costante Constants.TipoDato
     * 
     * @return Lista di {@link DecSelCriterioRaggrFasc}
     * 
     * @deprecated ormai gestisce solo il TIPO_FASCICOLO, quindi meglio usare
     *             {@link it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper#getRelationsByIdTipoFascicolo
     *             getRelationsByIdTipoFascicolo}
     */
    @Deprecated
    public List<DecSelCriterioRaggrFasc> getRelationsWithCriteriRaggrFascicolo(long idTipoDato,
            Constants.TipoDato tipoDato) {
        if (Constants.TipoDato.TIPO_FASCICOLO.equals(tipoDato)) {
            return getRelationsByIdTipoFascicolo(idTipoDato);
        }
        throw new IllegalStateException("TipoDato " + tipoDato.name() + " non gestito");
    }

    public boolean checkManyRelationsAreEmptyForStruttura(long idStrut) {
        String queryStr1 = "SELECT strut FROM OrgStrut strut " + "WHERE strut.idStrut = :idStrut "
                + "AND NOT EXISTS (SELECT mon FROM MonVRicContaUdDocComp mon WHERE mon.idStrut = strut.idStrut) ";
        Query query1 = getEntityManager().createQuery(queryStr1);
        query1.setParameter("idStrut", idStrut);
        List<Object[]> list1 = query1.getResultList();
        // Se ho record, vuol dire che non ho relazioni, quindi proseguo
        if (!list1.isEmpty()) {
            String queryStr2 = "SELECT strut FROM OrgStrut strut " + "WHERE strut.idStrut = :idStrut "
                    + "AND NOT EXISTS (SELECT unitaDoc FROM AroUnitaDoc unitaDoc WHERE unitaDoc.orgStrut.idStrut = strut.idStrut AND unitaDoc.dtAnnul = TRUNC( :dataOdierna)) ";
            Query query2 = getEntityManager().createQuery(queryStr2);
            query2.setParameter("idStrut", idStrut);
            query2.setParameter("dataOdierna", new Date());

            List<Object[]> list2 = query2.getResultList();
            // Se ho record, vuol dire che non ho relazioni, quindi proseguo
            if (!list2.isEmpty()) {
                String queryStr3 = "SELECT strut FROM OrgStrut strut " + "WHERE strut.idStrut = :idStrut "
                        + "AND NOT EXISTS (SELECT sessioneVers FROM VrsSessioneVersKo sessioneVers WHERE sessioneVers.orgStrut.idStrut = strut.idStrut AND sessioneVers.tiSessioneVers IN ('AGGIUNGI_DOCUMENTO', 'VERSAMENTO')) ";
                Query query3 = getEntityManager().createQuery(queryStr3);
                query3.setParameter("idStrut", idStrut);
                List<Object[]> list3 = query3.getResultList();
                // Se ho record, vuol dire che non ho relazioni
                return list3.isEmpty();
            }
        }
        return true;
    }

    public boolean existsRelationsWithElenchiVolumiForCriterioRaggruppamento(long idCriterioRaggr) {
        boolean result;
        String queryStr = "SELECT criterioRaggr FROM DecCriterioRaggr criterioRaggr "
                + "WHERE criterioRaggr.idCriterioRaggr = :idCriterioRaggr "
                + "AND NOT EXISTS (SELECT elencoVer FROM ElvElencoVer elencoVer WHERE elencoVer.decCriterioRaggr.idCriterioRaggr = criterioRaggr.idCriterioRaggr) "
                + "AND NOT EXISTS (SELECT volumeConserv FROM VolVolumeConserv volumeConserv WHERE volumeConserv.decCriterioRaggr.idCriterioRaggr = criterioRaggr.idCriterioRaggr) ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idCriterioRaggr", idCriterioRaggr);
        // Se NON trova nessun record, significa che ci sono relazioni
        List<Object[]> list = query.getResultList();
        result = list.isEmpty();
        return result;
    }

    public boolean existsRelationsWithElenchiForCriterioRaggrFasc(long idCriterioRaggrFasc) {
        boolean result;
        String queryStr = "SELECT criterioRaggrFasc FROM DecCriterioRaggrFasc criterioRaggrFasc "
                + "WHERE criterioRaggrFasc.idCriterioRaggrFasc = :idCriterioRaggrFasc "
                + "AND NOT EXISTS (SELECT elencoVersFasc FROM ElvElencoVersFasc elencoVersFasc WHERE elencoVersFasc.decCriterioRaggrFasc.idCriterioRaggrFasc = criterioRaggrFasc.idCriterioRaggrFasc)";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idCriterioRaggrFasc", idCriterioRaggrFasc);
        // Se NON trova nessun record, significa che ci sono relazioni
        List<Object[]> list = query.getResultList();
        result = list.isEmpty();
        return result;
    }

    public String partitionOK(BigDecimal idStrut) {
        return getEntityManager().find(OrgVChkStrutPartition.class, idStrut).getFlPartOk();
    }

    public String partitionFileEleVersFascOK(BigDecimal idStrut) {
        return getEntityManager().find(OrgVChkTimePartitionFasc.class, idStrut).getFlPartFileelevrsfascOk();
    }

    public String partitionFileEleVersFascDataOK(BigDecimal idStrut) {
        return getEntityManager().find(OrgVChkTimePartitionFasc.class, idStrut).getFlPartFileelevrsfascDataOk();
    }

    public List<Integer> getProgressiviTemplatePresentiSuDB() {
        List<Integer> progressiviList = new ArrayList<>();
        // Recupero le strutture template create in automatico (nome = Template + progressivo)
        Query query = getEntityManager()
                .createQuery("SELECT strut.nmStrut FROM OrgStrut strut " + "WHERE strut.flTemplate = '1' ");
        List<String> struttureTemplateList = query.getResultList();
        for (String nmStrutturaTemplate : struttureTemplateList) {
            String[] nomeAndProgressivo = nmStrutturaTemplate.split(" ", 2);
            // Controllo che ci siano 2 elementi, che il primo sia uguale a "Template" e che il secondo sia un
            // progressivo
            if (nomeAndProgressivo[0] != null && nomeAndProgressivo[0].equals("Template")
                    && nomeAndProgressivo[1] != null) {
                // returns defaultValue if the string cannot be parsed.
                int progressivo = org.apache.commons.lang3.math.NumberUtils.toInt(nomeAndProgressivo[1], 0);
                if (progressivo != 0) {
                    progressiviList.add(progressivo);
                }
            }
        }
        Collections.sort(progressiviList);
        return progressiviList;
    }

    public String getDecVChkFmtNumeroForPeriodo(long idAaRegistroUnitaDoc) {
        String queryStr = "SELECT check.flFmtNumeroOk FROM DecVChkFmtNumero check "
                + "WHERE check.idAaRegistroUnitaDoc = :idAaRegistroUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAaRegistroUnitaDoc", bigDecimalFromLong(idAaRegistroUnitaDoc));
        String risultato = "2";
        try {
            risultato = (String) query.getSingleResult();
            if (risultato != null) {
                return risultato;
            } else {
                return "2";
            }
        } catch (Exception e) {
            return "2";
        }
    }

    public String checkPartizioni(BigDecimal idStrut, Date data, String tiPartition) {
        /* Controllo se esistono partizioni di tipo passato in input per la struttura considerata */
        String query1Str = "SELECT COUNT(partitionStrut) FROM OrgPartitionStrut partitionStrut "
                + "JOIN partitionStrut.orgStrut strut2 " + "WHERE strut2.idStrut = :idStrut "
                + "AND partitionStrut.tiPartition = :tiPartition ";

        Query query1 = getEntityManager().createQuery(query1Str);
        query1.setParameter("idStrut", longFromBigDecimal(idStrut));
        query1.setParameter("tiPartition", tiPartition);
        Long result1 = (Long) query1.getSingleResult();

        // Se già non ho partizioni mi fermo, altrimenti proseguo
        if (result1 == 0L) {
            return String.valueOf(result1);
        } else {
            String query2NativeSQL = "SELECT CASE " + "WHEN NOT EXISTS (SELECT * FROM ORG_PARTITION_STRUT part_strut "
                    + "JOIN ORG_PARTITION part " + "ON (part.id_partition = part_strut.id_partition) "
                    + "JOIN ORG_SUB_PARTITION subpart " + "ON (subpart.id_partition = part.id_partition) "
                    + "JOIN ORG_VAL_SUB_PARTITION val_subpart "
                    + "ON (val_subpart.id_sub_partition = subpart.id_sub_partition "
                    + "AND val_subpart.id_partition = part.id_partition "
                    + "AND TO_DATE(val_subpart.cd_val_sub_partition, 'dd/mm/yyyy') > TRUNC(?1)) "
                    + "WHERE part_strut.id_strut = strut.id_strut " + "AND part_strut.ti_partition = ?2 )" + "THEN '0' "
                    + "ELSE '1' " + "END fl_part_file_ele_vers_data_ok " + "FROM ORG_STRUT strut "
                    + "WHERE strut.id_strut = ?3 ";

            Query query2 = getEntityManager().createNativeQuery(query2NativeSQL);
            query2.setParameter(1, data);
            query2.setParameter(2, tiPartition);
            query2.setParameter(3, idStrut);
            return query2.getSingleResult().toString();
        }
    }

    /**
     * @deprecated MAC#21555
     * 
     * @param idUserIam
     *            id dell'utente
     * @param idAmbientiSet
     *            identificativi degli ambienti
     * 
     * @return Lista delle strutture abilitate
     * 
     * @deprecated
     */
    @Deprecated
    public List<BigDecimal> getIdStrutAbilitatiFromAmbienteSet(long idUserIam,
            Set<? extends BigDecimal> idAmbientiSet) {
        Query query = getEntityManager()
                .createQuery("SELECT DISTINCT strut.idStrut " + " FROM IamAbilOrganiz iao, OrgStrut strut "
                        + " WHERE strut.orgEnte.orgAmbiente.idAmbiente IN (:idAmbiente) "
                        + " AND iao.iamUser.idUserIam = :idUtente " + " AND strut.idStrut = iao.idOrganizApplic "
                /* + " ORDER BY strut.nmStrut" */);
        query.setParameter("idUtente", idUserIam);
        query.setParameter("idAmbiente", longListFrom(idAmbientiSet));

        return query.getResultList();
    }

    /**
     * @deprecated MAC#21555
     *
     * @param idUserIam
     *            id dell'utente
     * @param idEntiSet
     *            gli id degli enti
     *
     * @return Lista degli id delle strutture
     *
     * @deprecated
     */
    @Deprecated
    public List<BigDecimal> getIdStrutAbilitatiFromEnteSet(long idUserIam, Set<? extends BigDecimal> idEntiSet) {
        Query query = getEntityManager().createQuery("SELECT DISTINCT strut.idStrut "
                + " FROM IamAbilOrganiz iao, OrgStrut strut " + " WHERE strut.orgEnte.idEnte IN (:idEnte) "
                + " AND iao.iamUser.idUserIam = :idUtente " + " AND strut.idStrut = iao.idOrganizApplic "
        /* + " ORDER BY strut.nmStrut" */
        );
        query.setParameter("idUtente", idUserIam);
        query.setParameter("idEnte", longListFrom(idEntiSet));

        return query.getResultList();
    }

    /**
     * Recupera le strutture da mostrare nella combo della pagina di scelta strutture dopo aver effettuato il login
     *
     * @param idUtente
     *            id utente
     * 
     * @return Object[], l'object array contenente i dati sulle strutture
     */
    public List<Object[]> getAmbEnteStrutDefault(long idUtente) {
        Query query = getEntityManager().createQuery(
                "SELECT iao, os.orgEnte.orgAmbiente.idAmbiente, os.orgEnte.idEnte  FROM IamAbilOrganiz iao, OrgStrut os  WHERE iao.iamUser.idUserIam = :idUtente  AND iao.flOrganizDefault = 1  AND os.idStrut = iao.idOrganizApplic");
        query.setParameter("idUtente", idUtente);
        return query.getResultList();
    }

    public Long countOrgStrut(BigDecimal idEnte) {
        Query query = getEntityManager()
                .createQuery("SELECT COUNT(strut) FROM OrgStrut strut WHERE strut.orgEnte.idEnte = :idEnte");
        query.setParameter("idEnte", longFromBigDecimal(idEnte));

        return (Long) query.getSingleResult();
    }

    public boolean haFigliPresentiInSottoLivelloOnlineList(BigDecimal idPadre,
            List<BigDecimal> figliQualunquePresentiInOnline) {
        String queryStr = "SELECT COUNT(a) " + "FROM OrgAmbitoTerrit a "
                + "WHERE a.orgAmbitoTerrit.idAmbitoTerrit = :idPadre "
                + "AND a.idAmbitoTerrit IN (:figliQualunquePresentiInOnline) ";
        // Se la lista di figli presenti nell'online è vuota, è ovvio che non avrò figli presenti nell'online
        if (figliQualunquePresentiInOnline.isEmpty()) {
            return false;
        } else {
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idPadre", longFromBigDecimal(idPadre));
            query.setParameter("figliQualunquePresentiInOnline", longListFrom(figliQualunquePresentiInOnline));
            return (Long) query.getSingleResult() > 0;
        }
    }

    public List<BigDecimal> getIdAmbitoTerritChildList(BigDecimal idAmbitoTerrit) {
        String queryStr = "SELECT a " + "FROM OrgAmbitoTerrit a "
                + "WHERE a.orgAmbitoTerrit.idAmbitoTerrit = :idAmbitoTerrit";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAmbitoTerrit", longFromBigDecimal(idAmbitoTerrit));
        List<OrgAmbitoTerrit> list = query.getResultList();
        List<BigDecimal> idAmbitoTerritList = new ArrayList<>();
        for (OrgAmbitoTerrit ambito : list) {
            idAmbitoTerritList.add(new BigDecimal(ambito.getIdAmbitoTerrit()));
        }
        return idAmbitoTerritList;
    }

    public Long getIdAmbienteEnteSiamByStrut(BigDecimal idStrut) {
        String queryStr = "SELECT enteSiam.siOrgAmbienteEnteConvenz.idAmbienteEnteConvenz FROM OrgStrut strut, SIOrgEnteSiam enteSiam "
                + "WHERE strut.idEnteConvenz = enteSiam.idEnteSiam " + "AND strut.idStrut = :idStrut ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        List<Long> strutList = query.getResultList();
        if (strutList.isEmpty()) {
            return null;
        }
        return strutList.get(0);
    }

    /**
     * Controlla che nel periodo compreso tra data di inizio e di fine validità la struttura non sia già associata ad un
     * altro ente convenzionato ricercando sulla tabella dell'associazione ovvero SIOrgEnteConvenzOrg
     *
     * @param nmApplic
     *            nome aplplicazione
     * @param idStrut
     *            id struttura
     * @param dtIniVal
     *            data inizio validita
     * @param dtFineVal
     *            data fine validita
     * @param idEnteConvenzOrg
     *            id ente convenzionato
     * 
     * @return true se esiste già l'associazione esiste gia
     */
    public boolean checkEsistenzaAssociazioneEnteConvenzStrutVers(String nmApplic, BigDecimal idStrut, Date dtIniVal,
            Date dtFineVal, BigDecimal idEnteConvenzOrg) {
        String queryStr = "SELECT enteConvenzOrg FROM SIOrgEnteConvenzOrg enteConvenzOrg "
                + "WHERE enteConvenzOrg.siUsrOrganizIam.idOrganizApplic = :idStrut "
                + "AND enteConvenzOrg.siUsrOrganizIam.siAplTipoOrganiz.nmTipoOrganiz = 'STRUTTURA' "
                + "AND enteConvenzOrg.siUsrOrganizIam.sIAplApplic.nmApplic = :nmApplic "
                // O è dentro l'intervallo la data di inizio
                + "AND ((enteConvenzOrg.dtIniVal <= :dtIniVal AND enteConvenzOrg.dtFineVal >= :dtIniVal) "
                // O è dentro l'intervallo la data di fine
                + "OR (enteConvenzOrg.dtIniVal <= :dtFineVal AND enteConvenzOrg.dtFineVal >= :dtFineVal) "
                // Oppure ancora entrambe le date sono esterne all'intervallo ma si sovrappongono ad un altro periodo
                + "OR (enteConvenzOrg.dtIniVal >= :dtIniVal AND enteConvenzOrg.dtFineVal <= :dtFineVal))";

        if (idEnteConvenzOrg != null) {
            queryStr = queryStr + "AND enteConvenzOrg.idEnteConvenzOrg != :idEnteConvenzOrgDaEscludere ";
        }
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("nmApplic", nmApplic);
        query.setParameter("dtIniVal", dtIniVal);
        query.setParameter("dtFineVal", dtFineVal);
        if (idEnteConvenzOrg != null) {
            query.setParameter("idEnteConvenzOrgDaEscludere", longFromBigDecimal(idEnteConvenzOrg));
        }
        List<SIOrgEnteConvenzOrg> list = query.getResultList();
        return !list.isEmpty();
    }

    /**
     * Controlla che nel periodo compreso tra data di inizio e di fine validità l'ente convenzionato associato alla
     * struttura abbia un accordo valido (compreso tra data di inizio e data di fine validità)
     *
     * @param idEnteConvenz
     *            id ente convenzionato
     * @param dtIniVal
     *            data inizio validita
     * @param dtFineVal
     *            data fine validita
     * 
     * @return true se esiste accordo valido
     */
    public boolean checkEsistenzaPeriodoValiditaAssociazioneEnteConvenzStrutVers(BigDecimal idEnteConvenz,
            Date dtIniVal, Date dtFineVal) {
        String queryStr = "SELECT enteSiam FROM SIOrgEnteSiam enteSiam " + "WHERE enteSiam.idEnteSiam = :idEnteConvenz "
                + "AND EXISTS (SELECT accordoEnte FROM SIOrgAccordoEnte accordoEnte "
                + "WHERE (accordoEnte.dtDecAccordo <= :dtIniVal AND :dtIniVal <= accordoEnte.dtFineValidAccordo) "
                + "AND (accordoEnte.dtDecAccordo <= :dtFineVal AND :dtFineVal <= accordoEnte.dtFineValidAccordo) "
                + "AND accordoEnte.siOrgEnteConvenz = enteSiam) ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idEnteConvenz", longFromBigDecimal(idEnteConvenz));
        query.setParameter("dtIniVal", dtIniVal);
        query.setParameter("dtFineVal", dtFineVal);
        List<SIOrgEnteSiam> list = query.getResultList();
        return !list.isEmpty();
    }

    /**
     * Controlla che l'ente convenzionato associato alla struttura abbia un accordo valido alla data corrente (compresa
     * tra data di inizio e data di fine validità accordo)
     *
     * @param idEnteConvenz
     *            id ente convenzionato
     * 
     * @return true se esiste accordo valido
     */
    public boolean checkEsistenzaAccordoValidoEnteConvenzStrutVers(BigDecimal idEnteConvenz) {
        String queryStr = "SELECT enteSiam FROM SIOrgEnteSiam enteSiam " + "WHERE enteSiam.idEnteSiam = :idEnteConvenz "
                + "AND EXISTS (SELECT accordoEnte FROM SIOrgAccordoEnte accordoEnte "
                + "WHERE (accordoEnte.dtDecAccordo <= :dataOdierna AND accordoEnte.dtFineValidAccordo >= :dataOdierna) "
                + "AND accordoEnte.siOrgEnteConvenz = enteSiam) ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idEnteConvenz", longFromBigDecimal(idEnteConvenz));
        query.setParameter("dataOdierna", new Date());
        List<SIOrgEnteSiam> list = query.getResultList();
        return !list.isEmpty();
    }

    /**
     * Controlla che data inizio validità sia maggiore o uguale data decorrenza più vecchia di tutti gli accordi
     * definiti sull'ente e data fine minore data fine validità ente siam
     *
     * @param idEnteConvenz
     *            l'ente convenzionato di cui controllare gli accordi
     * @param dtIniVal
     *            la data di inizio validità dell'associazione struttura/ente convenzionato
     * @param dtFineVal
     *            la data di fin validità dell'associazione struttura/ente convenzionato
     * 
     * @return true se viene soddisfatto il controllo
     */
    public boolean existsPeriodoValiditaAssociazioneEnteConvenzStrutVersAccordi(BigDecimal idEnteConvenz, Date dtIniVal,
            Date dtFineVal) {
        String queryStr = "SELECT accordo FROM SIOrgAccordoEnte accordo " + "JOIN accordo.siOrgEnteConvenz enteSiam "
                + "WHERE enteSiam.idEnteSiam = :idEnteConvenz "
                + "AND accordo.dtDecAccordo = (SELECT MIN(accordo_min.dtDecAccordo) FROM SIOrgAccordoEnte accordo_min "
                + "WHERE accordo_min.siOrgEnteConvenz = enteSiam) "
                + "AND accordo.dtDecAccordo <= :dtIniVal AND enteSiam.dtCessazione >= :dtFineVal ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idEnteConvenz", longFromBigDecimal(idEnteConvenz));
        query.setParameter("dtIniVal", dtIniVal);
        query.setParameter("dtFineVal", dtFineVal);
        List<SIOrgAccordoEnte> list = query.getResultList();
        return !list.isEmpty();
    }

    public boolean existsPeriodoValiditaAssociazioneEnteConvenzStrutVersAccordoValido(BigDecimal idEnteConvenz,
            Date dtIniVal, Date dtFineVal) {
        Query query = getEntityManager().createQuery("SELECT accordo FROM SIOrgAccordoEnte accordo "
                + "JOIN  accordo.siOrgEnteConvenz enteSiam " + "WHERE enteSiam.idEnteSiam = :idEnteConvenz "
                + "AND accordo.dtDecAccordo <= :dataOdierna AND accordo.dtFineValidAccordo >= :dataOdierna "
                + "AND accordo.dtDecAccordo <= :dtIniVal AND enteSiam.dtCessazione >= :dtFineVal ");
        query.setParameter("idEnteConvenz", longFromBigDecimal(idEnteConvenz));
        query.setParameter("dtIniVal", dtIniVal);
        query.setParameter("dtFineVal", dtFineVal);
        List<SIOrgAccordoEnte> accordoList = query.getResultList();
        return !accordoList.isEmpty();
    }

    /**
     * Verifica se per un accordo valido (data odierna compresa nell'intervallo di validità) le date dell'associazione
     * rientrano nell'intervallo tra la data di decorrenza accordo e la data di fine validità dell'ente siam
     * 
     * @param idEnteConvenz
     *            l'ente convenzionato su cui controllare se esiste un accordo valido
     * @param dtIniVal
     *            la data di inizio validità dell'associazione
     * @param dtFineVal
     *            la data di fine validità dell'associazione
     * 
     * @return vero o falso a seconda che l'intervallo sia valido
     */
    public boolean existsIntervalloValiditaPerAssociazione(BigDecimal idEnteConvenz, Date dtIniVal, Date dtFineVal) {
        Query query = getEntityManager().createQuery("SELECT accordo FROM SIOrgAccordoEnte accordo "
                + "JOIN  accordo.siOrgEnteConvenz enteSiam " + "WHERE enteSiam.idEnteSiam = :idEnteConvenz "
                + "AND accordo.dtDecAccordo <= :dtOdierna AND accordo.dtFineValidAccordo >= :dtOdierna "
                + "AND accordo.dtDecAccordo <= :dtIniVal AND enteSiam.dtCessazione >= :dtFineVal ");
        query.setParameter("idEnteConvenz", longFromBigDecimal(idEnteConvenz));
        query.setParameter("dtOdierna", new Date());
        query.setParameter("dtIniVal", dtIniVal);
        query.setParameter("dtFineVal", dtFineVal);
        List<SIOrgAccordoEnte> accordoList = query.getResultList();
        return !accordoList.isEmpty();
    }

    /**
     * Controlla che l'ente associato alla struttura abbia almeno una struttura non cessata
     *
     * @param idStrut
     *            id struttura
     * 
     * @return true se esiste almeno una struttura non cessata
     */
    public boolean containsStrutEnteNonCessata(BigDecimal idStrut) {
        String queryStr = "SELECT strut FROM OrgStrut strut " + "JOIN strut.orgEnte ente "
                + "WHERE strut.idStrut = :idStrut " + "AND EXISTS (SELECT strut_valida FROM OrgStrut strut_valida "
                + "WHERE strut_valida.orgEnte = ente " + "AND strut_valida.flCessato = '0') ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        List<OrgStrut> list = query.getResultList();
        return !list.isEmpty();
    }

    public List<IamEnteConvenzDaAllinea> getIamEnteConvenzDaAllinea() {
        String queryStr = "SELECT enteConvenzDaAllinea FROM IamEnteConvenzDaAllinea enteConvenzDaAllinea "
                + "WHERE enteConvenzDaAllinea.tiStatoAllinea "
                + "IN ('DA_ALLINEARE', 'ALLINEA_IN_TIMEOUT', 'ALLINEA_IN_ERRORE') "
                + "ORDER BY enteConvenzDaAllinea.dtLogEnteConvenzDaAllinea ";
        javax.persistence.Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void writeEsitoIamEnteConvenzDaAllinea(Long idEnteConvenzDaAllinea,
            CostantiAllineaEntiConv.EsitoServizio esitoServizio, String cdErr, String dsErr) {
        IamEnteConvenzDaAllinea enteConvenzDaAllinea = getEntityManager().find(IamEnteConvenzDaAllinea.class,
                idEnteConvenzDaAllinea);
        if (esitoServizio != null) {
            switch (esitoServizio) {
            case OK:
                enteConvenzDaAllinea.setTiStatoAllinea(CostantiAllineaEntiConv.TiStatoAllinea.ALLINEA_OK.name());
                enteConvenzDaAllinea.setCdErr(null);
                enteConvenzDaAllinea.setDsMsgErr(null);
                enteConvenzDaAllinea.setDtErr(null);
                break;
            case KO:
                switch (cdErr) {
                case CostantiAllineaEntiConv.SERVIZI_ENTE_001:
                case CostantiAllineaEntiConv.SERVIZI_ENTE_002:
                case CostantiAllineaEntiConv.ALLINEA_ENTE_001:
                case CostantiAllineaEntiConv.ERR_666:
                    enteConvenzDaAllinea
                            .setTiStatoAllinea(CostantiAllineaEntiConv.TiStatoAllinea.ALLINEA_IN_ERRORE.name());
                    enteConvenzDaAllinea.setCdErr(cdErr);
                    enteConvenzDaAllinea.setDsMsgErr(dsErr);
                    enteConvenzDaAllinea.setDtErr(new Date());
                    break;
                }
                break;
            case NO_RISPOSTA:
                enteConvenzDaAllinea
                        .setTiStatoAllinea(CostantiAllineaEntiConv.TiStatoAllinea.ALLINEA_IN_TIMEOUT.name());
                enteConvenzDaAllinea.setCdErr(cdErr);
                enteConvenzDaAllinea.setDsMsgErr(dsErr);
                enteConvenzDaAllinea.setDtErr(new Date());
                break;
            default:
                break;
            }
        }
    }

    public List<OrgStrut> retrieveOrgStrutList() {
        Query q = getEntityManager().createQuery("SELECT strut FROM OrgStrut strut ORDER BY strut.nmStrut");
        return q.getResultList();
    }

    public boolean existsCdStrutNormaliz(String cdStrutNormaliz, BigDecimal idEnte, BigDecimal idStrutExcluded) {
        String queryStr = "SELECT strut FROM OrgStrut strut " + "WHERE strut.cdStrutNormaliz = :cdStrutNormaliz "
                + "AND strut.orgEnte.idEnte = :idEnte ";
        if (idStrutExcluded != null) {
            queryStr = queryStr + "AND strut.idStrut != :idStrutExcluded ";
        }
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdStrutNormaliz", cdStrutNormaliz);
        query.setParameter("idEnte", longFromBigDecimal(idEnte));
        if (idStrutExcluded != null) {
            query.setParameter("idStrutExcluded", longFromBigDecimal(idStrutExcluded));
        }
        return !query.getResultList().isEmpty();
    }

    /**
     * Torna VERO se il codice Struttura normalizzato passato non esiste su db, quindi è univoco
     * 
     * @param cdStrutNormaliz
     *            il codice struttura normalizzato
     * 
     * @return true se il codice Struttura normalizzato passato non esiste su db
     */
    public boolean isCodStrutturaNormalizzatoUnivoco(String cdStrutNormaliz) {
        Query query = getEntityManager()
                .createQuery("SELECT s FROM OrgStrut s WHERE s.cdStrutNormaliz = :cdStrutNormaliz");
        query.setParameter("cdStrutNormaliz", cdStrutNormaliz);
        return query.getResultList().isEmpty();
    }

    public List<String> getFunzioneParametri() {
        Query query = getEntityManager().createQuery(
                "SELECT DISTINCT paramApplic.tiParamApplic FROM AplParamApplic paramApplic ORDER BY paramApplic.tiParamApplic");
        return query.getResultList();
    }

    public OrgVRicEnteConvenzByEsterno findOrgVRicEnteConvenzByEsternoByEnte(BigDecimal idEnteConvenz) {
        final String parmIdEnte = "idEnte";
        TypedQuery<OrgVRicEnteConvenzByEsterno> query = getEntityManager().createQuery(
                "SELECT o FROM OrgVRicEnteConvenzByEsterno o WHERE o.id.idEnteConvenz=:" + parmIdEnte,
                OrgVRicEnteConvenzByEsterno.class);
        query.setParameter(parmIdEnte, idEnteConvenz);
        query.setMaxResults(1);
        query.setFirstResult(0);
        return query.getSingleResult();
    }

    public List<OrgStrut> getOrgStrutList() {
        Query q = getEntityManager().createQuery("SELECT strut FROM OrgStrut strut");
        return q.getResultList();
    }

    public List<Long> getIdStrutList() {
        Query q = getEntityManager().createQuery("SELECT strut.idStrut FROM OrgStrut strut");
        return q.getResultList();
    }

    public List<Long> getIdTipoStrutDocList() {
        Query q = getEntityManager()
                .createQuery("SELECT tipoStrutDoc.idTipoStrutDoc FROM DecTipoStrutDoc tipoStrutDoc ");
        return q.getResultList();
    }

    public List<BigDecimal> getIdFormatoFileDocList(long idFormatoFileStandard) {
        Query q = getEntityManager().createNativeQuery("SELECT  doc.id_formato_file_doc "
                + "FROM    DEC_FORMATO_FILE_DOC doc "
                + "JOIN    dec_uso_formato_file_standard uso ON (uso.id_formato_file_doc=doc.id_formato_file_doc) "
                + "JOIN    dec_formato_file_standard std ON (uso.id_formato_file_standard=std.id_formato_file_standard) "
                + "WHERE   std.id_formato_file_standard = :idFormatoFileStandard ");
        q.setParameter("idFormatoFileStandard", idFormatoFileStandard);
        return q.getResultList();
    }

    public List<OrgStrut> retrieveOrgStrutByEnteConvenz(BigDecimal idEnteConvenz) {
        String queryStr = "SELECT strut FROM OrgStrut strut WHERE strut.idEnteConvenz = :idEnteConvenz ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idEnteConvenz", idEnteConvenz);
        return query.getResultList();
    }

}
