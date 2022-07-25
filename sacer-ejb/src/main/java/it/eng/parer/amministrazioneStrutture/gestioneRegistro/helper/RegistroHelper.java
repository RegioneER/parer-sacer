package it.eng.parer.amministrazioneStrutture.gestioneRegistro.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import it.eng.parer.entity.DecAaRegistroUnitaDoc;
import it.eng.parer.entity.DecErrAaRegistroUnitaDoc;
import it.eng.parer.entity.DecParteNumeroRegistro;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoStrutUdReg;
import it.eng.parer.entity.DecTipoUnitaDocAmmesso;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.DecVLisTiUniDocAms;
import it.eng.parer.ws.dto.CSVersatore;

/**
 * Helper dei registri delle unit\u00E0 documentarie
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class RegistroHelper extends GenericHelper {

    /**
     * Verifica che esista un registro il cui nome campo <code>nmCampo</code> dato in input sia valorizzato con il
     * valore <code>valoreCampo</code> per la struttura data in input
     *
     * @param nmCampo
     *            Nome campo da verificare
     * @param valoreCampo
     *            Valore che assume il campo <code>nmCampo</code>
     * @param idStrut
     *            Struttura in input
     * @param idRegistroUnitaDoc
     *            registro da escludere dal controllo
     * 
     * @return true se esiste
     */
    public boolean checkRegistroUnitaDoc(String nmCampo, String valoreCampo, BigDecimal idStrut,
            BigDecimal idRegistroUnitaDoc) {
        Date now = Calendar.getInstance().getTime();
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(reg) FROM DecRegistroUnitaDoc reg WHERE reg.orgStrut.idStrut = :idStrut AND reg."
                        + nmCampo + " = :valoreCampo AND reg.dtIstituz <= :filterDate AND reg.dtSoppres >= :filterDate"
                        + (idRegistroUnitaDoc != null ? " AND reg.idRegistroUnitaDoc != :idRegistroUnitaDoc" : ""));
        query.setParameter("idStrut", idStrut);
        query.setParameter("valoreCampo", valoreCampo);
        query.setParameter("filterDate", now);
        if (idRegistroUnitaDoc != null) {
            query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        }
        Long count = (Long) query.getSingleResult();
        boolean regs = count > 0L;
        return regs;
    }

    /**
     * Ritorna l'oggetto DecRegistroUnitaDoc dato il codice registro e la struttura di riferimento
     *
     * @param cdRegistroUnitaDoc
     *            codice registro unita doc
     * @param idStrut
     *            struttura
     * 
     * @return l'oggetto DecRegistroUnitaDoc o null se inesistente
     */
    public DecRegistroUnitaDoc getDecRegistroUnitaDocByName(String cdRegistroUnitaDoc, BigDecimal idStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT registroUnitaDoc FROM DecRegistroUnitaDoc registroUnitaDoc WHERE registroUnitaDoc.cdRegistroUnitaDoc = :cdRegistroUnitaDoc AND registroUnitaDoc.orgStrut.idStrut=:idStrut");
        query.setParameter("cdRegistroUnitaDoc", cdRegistroUnitaDoc);
        query.setParameter("idStrut", idStrut);
        List<DecRegistroUnitaDoc> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * Ritorna la lista dei periodi di validità di un registro richiesto
     *
     * @param idRegistro
     *            registro
     * 
     * @return la lista dei periodi di validità di un registro
     */
    public List<DecAaRegistroUnitaDoc> getDecAARegistroUnitaDocList(BigDecimal idRegistro) {
        Query query = getEntityManager().createQuery(
                "SELECT aaReg FROM DecAaRegistroUnitaDoc aaReg WHERE aaReg.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistro ORDER BY aaReg.aaMinRegistroUnitaDoc");
        query.setParameter("idRegistro", idRegistro);
        List<DecAaRegistroUnitaDoc> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }

    /**
     * Ritorna la lista delle parti del periodo di validità di un registro richiesto
     *
     * @param idAaRegistroUnitaDoc
     *            id anno registro unita doc
     * 
     * @return lista oggetti di tipo {@link DecParteNumeroRegistro}
     */
    public List<DecParteNumeroRegistro> getDecParteNumeroRegistroList(Long idAaRegistroUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT decParte FROM DecParteNumeroRegistro decParte WHERE decParte.decAaRegistroUnitaDoc.idAaRegistroUnitaDoc = :idAaRegistroUnitaDoc ORDER BY decParte.niParteNumeroRegistro ASC");
        query.setParameter("idAaRegistroUnitaDoc", idAaRegistroUnitaDoc);
        List<DecParteNumeroRegistro> list = query.getResultList();
        return list;

    }

    /**
     * Recupera i tipi di registro per le chiavi unitï¿½ documentarie
     *
     * @param idUtente
     *            id utente
     * @param idStruttura
     *            id struttura
     * 
     * @return DecRegistroUnitaDocTableBean
     */
    public List<DecRegistroUnitaDoc> getRegistriUnitaDocAbilitati(long idUtente, BigDecimal idStruttura) {
        List<BigDecimal> idStrutList = new ArrayList<>();
        idStrutList.add(idStruttura);
        return getRegistriUnitaDocAbilitatiDaStrutturaList(idUtente, idStrutList);
    }

    public List<DecRegistroUnitaDoc> getRegistriUnitaDocAbilitatiDaStrutturaList(long idUtente,
            List<BigDecimal> idStrutturaList) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT u FROM DecRegistroUnitaDoc u , IamAbilTipoDato iatd WHERE iatd.idTipoDatoApplic = u.idRegistroUnitaDoc ");
        queryStr.append(
                " AND iatd.nmClasseTipoDato = 'REGISTRO' AND iatd.iamAbilOrganiz.iamUser.idUserIam = :idUtente ");
        if (!idStrutturaList.isEmpty()) {
            queryStr.append("AND u.orgStrut.idStrut IN :idStrutturaList ");
        }
        queryStr.append("ORDER BY u.cdRegistroUnitaDoc");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUtente", idUtente);
        if (!idStrutturaList.isEmpty()) {
            query.setParameter("idStrutturaList", idStrutturaList);
        }
        List<DecRegistroUnitaDoc> listaRegistri = query.getResultList();
        return listaRegistri;
    }

    public List<DecRegistroUnitaDoc> retrieveDecRegistroUnitaDocsFromTipoSerie(BigDecimal idStrut,
            BigDecimal idTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT DISTINCT tipoSerieUd.decRegistroUnitaDoc FROM DecTipoSerieUd tipoSerieUd JOIN tipoSerieUd.decTipoSerie tipoSerie WHERE tipoSerie.orgStrut.idStrut = :idStrut AND tipoSerie.dtIstituz <= :filterDate AND tipoSerie.dtSoppres >= :filterDate "
                        + (idTipoSerie != null ? " AND tipoSerie.idTipoSerie = :idTipoSerie " : ""));
        query.setParameter("idStrut", idStrut);
        query.setParameter("filterDate", Calendar.getInstance().getTime());
        if (idTipoSerie != null) {
            query.setParameter("idTipoSerie", idTipoSerie);
        }
        List<DecRegistroUnitaDoc> listaRegistri = query.getResultList();
        return listaRegistri;
    }

    public List<DecRegistroUnitaDoc> retrieveDecRegistroUnitaDocList(long idStrut, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT registroUnitaDoc FROM DecRegistroUnitaDoc registroUnitaDoc WHERE registroUnitaDoc.orgStrut.idStrut = :idStrut ");
        if (filterValid) {
            queryStr.append(" AND ").append(
                    " registroUnitaDoc.dtIstituz <= :filterDate AND registroUnitaDoc.dtSoppres >= :filterDate ");
        }

        queryStr.append("ORDER BY registroUnitaDoc.cdRegistroUnitaDoc ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", idStrut);
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }

        List<DecRegistroUnitaDoc> list = query.getResultList();
        return list;
    }

    public long countDecRegistroUnitaDoc(BigDecimal idModelloTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(registro) FROM DecRegistroUnitaDoc registro WHERE registro.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie");
        query.setParameter("idModelloTipoSerie", idModelloTipoSerie);
        Long count = (Long) query.getSingleResult();
        return count;
    }

    public String getDecVChkFmtNumeroForRegistro(long idRegistroUnitaDoc) {
        String queryStr = "SELECT COUNT(check), check.flFmtNumeroOk FROM DecVChkFmtNumero check "
                + "WHERE check.idRegistroUnitaDoc = :idRegistroUnitaDoc " + "GROUP BY check.flFmtNumeroOk ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        List<Object[]> list = query.getResultList();
        String risultato = "1";
        if (list.size() > 0) {
            for (Object[] obj : list) {
                if (obj[1] != null) {
                    if (((String) obj[1]).equals("0")) {
                        risultato = "0";
                        break;
                    }
                } else {
                    risultato = "2"; // corrispondente a null
                    break;
                }
            }
        } else {
            risultato = "2"; // corrispondente a null
        }
        return risultato;
    }

    public BigDecimal getMaxAnniConserv(BigDecimal idTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT MAX(tipoSerieUd.decRegistroUnitaDoc.niAnniConserv) FROM DecTipoSerieUd tipoSerieUd JOIN tipoSerieUd.decTipoSerie tipoSerie WHERE tipoSerie.idTipoSerie = :idTipoSerie",
                BigDecimal.class);
        query.setParameter("idTipoSerie", idTipoSerie);
        BigDecimal result = (BigDecimal) query.getSingleResult();
        return result;
    }

    public DecAaRegistroUnitaDoc getDecAARegistroUnitaDoc(BigDecimal idAaRegistroUnitaDoc) {
        Query query = getEntityManager().createQuery("SELECT aaReg " + "FROM DecAaRegistroUnitaDoc aaReg "
                + "WHERE aaReg.idAaRegistroUnitaDoc = :idAaRegistroUnitaDoc");
        query.setParameter("idAaRegistroUnitaDoc", idAaRegistroUnitaDoc);
        List<DecAaRegistroUnitaDoc> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public long getMonAaUdRegistroNumber(BigDecimal dataInizio, BigDecimal dataFine, BigDecimal idRegistro) {
        String queryStr = "SELECT COUNT (m) " + "FROM MonAaUnitaDocRegistro m "
                + "WHERE m.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc "
                + "AND m.aaUnitaDocRegistro BETWEEN :dataInizio AND :dataFine ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idRegistroUnitaDoc", idRegistro);
        query.setParameter("dataInizio", dataInizio);
        if (dataFine == null) {
            query.setParameter("dataFine", 9999);
        } else {
            query.setParameter("dataFine", dataFine);
        }
        return (long) query.getSingleResult();
    }

    public boolean checkRangeDecAaRegistroUnitaDoc(BigDecimal idAaRegistroUnitaDoc, BigDecimal idRegistroUnitaDoc,
            BigDecimal aaMinRegistroUnitaDoc, BigDecimal aaMaxRegistroUnitaDoc) {
        String queryStr = "SELECT COUNT(aaReg) FROM DecAaRegistroUnitaDoc aaReg WHERE aaReg.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc "
                + "AND aaReg.aaMinRegistroUnitaDoc <= :aaMaxRegistroUnitaDoc "
                + "AND (aaReg.aaMaxRegistroUnitaDoc IS NULL OR aaReg.aaMaxRegistroUnitaDoc >= :aaMinRegistroUnitaDoc)";
        if (idAaRegistroUnitaDoc != null) {
            queryStr += " AND aaReg.idAaRegistroUnitaDoc != :idAaRegistroUnitaDoc ";
        }
        Query query = getEntityManager().createQuery(queryStr);
        if (idAaRegistroUnitaDoc != null) {
            query.setParameter("idAaRegistroUnitaDoc", idAaRegistroUnitaDoc);
        }
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        query.setParameter("aaMinRegistroUnitaDoc", aaMinRegistroUnitaDoc);
        query.setParameter("aaMaxRegistroUnitaDoc",
                aaMaxRegistroUnitaDoc != null ? aaMaxRegistroUnitaDoc : new BigDecimal(9999));
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    public List<DecErrAaRegistroUnitaDoc> getDecErrAaRegistroUnitaDocList(BigDecimal idAaRegistroUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT err FROM DecErrAaRegistroUnitaDoc err WHERE err.decAaRegistroUnitaDoc.idAaRegistroUnitaDoc = :idAaRegistroUnitaDoc ORDER BY err.aaRegistroUnitaDoc");
        query.setParameter("idAaRegistroUnitaDoc", idAaRegistroUnitaDoc);

        List<DecErrAaRegistroUnitaDoc> list = query.getResultList();
        return list;
    }

    public boolean existsRegistroUnitaDoc(BigDecimal idStrut, String cdRegistroUnitaDoc) {
        String queryStr = "SELECT COUNT(u) FROM DecRegistroUnitaDoc u "
                + "WHERE u.cdRegistroUnitaDoc = :cdRegistroUnitaDoc " + "AND u.orgStrut.idStrut = :idStrut ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdRegistroUnitaDoc", cdRegistroUnitaDoc);
        query.setParameter("idStrut", idStrut);
        return (long) query.getSingleResult() > 0;
    }

    public boolean checkUnitaDocInDecAaRegUnitaDoc(BigDecimal idAaRegistroUnitaDoc, BigDecimal annoDa, BigDecimal annoA,
            Long idRegistroUnitaDoc, String cdRegistroKeyUnitaDoc, List<Long> subStruts) {
        Query query = getEntityManager().createQuery(
                "SELECT a FROM DecAaRegistroUnitaDoc a WHERE a.idAaRegistroUnitaDoc = :idAaRegistroUnitaDoc AND EXISTS ( SELECT ud FROM AroUnitaDoc ud WHERE ud.orgSubStrut.idSubStrut IN :subStruts AND ud.aaKeyUnitaDoc BETWEEN :annoDa AND :annoA AND ud.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc AND ud.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc )");
        query.setParameter("idAaRegistroUnitaDoc", idAaRegistroUnitaDoc);
        query.setParameter("subStruts", subStruts);
        query.setParameter("annoDa", annoDa);
        query.setParameter("annoA", annoA);
        query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);

        List<DecAaRegistroUnitaDoc> lista = query.getResultList();

        return !lista.isEmpty();
    }

    public Long countPeriodiValiditaConControlloConsec(BigDecimal idRegistroUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(DISTINCT aaReg.idAaRegistroUnitaDoc) FROM DecParteNumeroRegistro parte JOIN parte.decAaRegistroUnitaDoc aaReg WHERE aaReg.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc AND NOT EXISTS(SELECT noConsec FROM DecParteNumeroRegistro noConsec WHERE noConsec.tiParte='PROGR' AND noConsec.decAaRegistroUnitaDoc.idAaRegistroUnitaDoc = aaReg.idAaRegistroUnitaDoc )");
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        Long count = (Long) query.getSingleResult();
        return count;
    }

    public List<DecVLisTiUniDocAms> getDecVLisTiUniDocAmsByStrutByRegistriList(List<BigDecimal> idRegistri) {
        Query query = getEntityManager().createQuery("SELECT  tuda " + "FROM DecVLisTiUniDocAms tuda "
                + "WHERE tuda.idRegistroUnitaDoc IN :listaIdRegistri ");
        query.setParameter("listaIdRegistri", idRegistri);
        List<DecVLisTiUniDocAms> list = query.getResultList();
        return list;
    }

    public List<DecTipoUnitaDocAmmesso> getDecTipoUnitaDocAmmessoByRegistro(Long idRegistroUnitaDoc) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoUnitaDocAmmesso FROM DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso ");
        String whereWord = "WHERE ";

        if (idRegistroUnitaDoc != null) {
            queryStr.append(whereWord)
                    .append("tipoUnitaDocAmmesso.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc ");
            whereWord = "AND ";
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idRegistroUnitaDoc != null) {
            query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        }

        List<DecTipoUnitaDocAmmesso> list = query.getResultList();

        return list;
    }

    public boolean existRegistriNonFiscaliAssociati(long idTipoUnitaDoc, long idRegistroUnitaDocExcluded) {
        String queryStr = "SELECT COUNT(tipoUnitaDocAmmesso) FROM DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso "
                + "WHERE tipoUnitaDocAmmesso.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc "
                + "AND tipoUnitaDocAmmesso.decRegistroUnitaDoc.flRegistroFisc = '0' "
                + "AND tipoUnitaDocAmmesso.decRegistroUnitaDoc.idRegistroUnitaDoc != :idRegistroUnitaDocExcluded";

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        query.setParameter("idRegistroUnitaDocExcluded", idRegistroUnitaDocExcluded);

        return (Long) query.getSingleResult() > 0L;
    }

    public CSVersatore getVersatoreRegistroUd(BigDecimal idRegistroUnitaDoc) {
        CSVersatore csv = new CSVersatore();
        DecRegistroUnitaDoc drud = getEntityManager().find(DecRegistroUnitaDoc.class, idRegistroUnitaDoc.longValue());
        csv.setStruttura(drud.getOrgStrut().getNmStrut());
        csv.setEnte(drud.getOrgStrut().getOrgEnte().getNmEnte());
        csv.setAmbiente(drud.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());

        return csv;
    }

    public boolean existsCdRegistroNormaliz(String cdRegistroNormaliz, BigDecimal idStrut,
            BigDecimal idRegistroUnitaDocExcluded) {
        String queryStr = "SELECT registro FROM DecRegistroUnitaDoc registro "
                + "WHERE registro.cdRegistroNormaliz = :cdRegistroNormaliz "
                + "AND registro.orgStrut.idStrut = :idStrut ";
        if (idRegistroUnitaDocExcluded != null) {
            queryStr = queryStr + "AND registro.idRegistroUnitaDoc != :idRegistroUnitaDocExcluded ";
        }
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdRegistroNormaliz", cdRegistroNormaliz);
        query.setParameter("idStrut", idStrut);
        if (idRegistroUnitaDocExcluded != null) {
            query.setParameter("idRegistroUnitaDocExcluded", idRegistroUnitaDocExcluded);
        }
        return !query.getResultList().isEmpty();
    }

    public DecTipoStrutUdReg getDecTipoStrutUdRegByName(BigDecimal idStrutCorrente, String nmTipoUnitaDoc,
            String nmTipoStrutUnitaDoc, String cdRegistroUnitaDoc) {
        String queryStr = "SELECT tipoStrutUdReg FROM DecTipoStrutUdReg tipoStrutUdReg "
                + "WHERE tipoStrutUdReg.decTipoStrutUnitaDoc.decTipoUnitaDoc.orgStrut.idStrut = :idStrutCorrente "
                + "AND tipoStrutUdReg.decTipoStrutUnitaDoc.decTipoUnitaDoc.nmTipoUnitaDoc = :nmTipoUnitaDoc "
                + "AND tipoStrutUdReg.decTipoStrutUnitaDoc.nmTipoStrutUnitaDoc = :nmTipoStrutUnitaDoc "
                + "AND tipoStrutUdReg.decRegistroUnitaDoc.cdRegistroUnitaDoc = :cdRegistroUnitaDoc "
                + "AND tipoStrutUdReg.decRegistroUnitaDoc.orgStrut.idStrut = :idStrutCorrente ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrutCorrente", idStrutCorrente);
        query.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc);
        query.setParameter("nmTipoStrutUnitaDoc", nmTipoStrutUnitaDoc);
        query.setParameter("cdRegistroUnitaDoc", cdRegistroUnitaDoc);

        List<DecTipoStrutUdReg> list = query.getResultList();
        DecTipoStrutUdReg tipoStrutUdReg = null;
        if (!list.isEmpty()) {
            tipoStrutUdReg = list.get(0);
        }

        return tipoStrutUdReg;
    }

}
