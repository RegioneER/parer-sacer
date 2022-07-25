/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recupero.ejb;

import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.grantedEntity.SIDecColQueryModelloComunic;
import it.eng.parer.grantedEntity.SIDecModelloComunic;
import it.eng.parer.grantedEntity.SIDecQueryModelloComunic;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.grantedEntity.SIUsrOrganizIam;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fioravanti_f
 */
@Stateless(mappedName = "ProduzioneDipEsibizione")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ProduzioneDipEsibizione {

    public enum TipiUsoModello {
        ESIBIZIONE, NOTIFICA
    }

    public enum TipiOggQryModello {
        COMP, DOC, RICH_GEST_USER, UNITA_DOC, UTENTE
    }

    public enum TipiUsoTNotifica {
        AMBIENTE_ENTE_CONVENZ, ENTE_CONVENZ
    }

    public enum TipiUsoQuery {
        MITTENTE, OGGETTO, TESTO
    }

    public enum TipiResultQuery {
        MULTIPLE_ROW, SINGLE_ROW
    }

    private static final Logger log = LoggerFactory.getLogger(ProduzioneDipEsibizione.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    // nome query, nome colonna resa della query diventano il nome della variabile da sostiuire nel template
    private static final String FMT_KEY_TEMPLATE = "<{0}>.<{1}>";

    public RispostaControlli caricaModello(long idUnitaDoc, TipiUsoModello usoModello,
            TipiOggQryModello oggQryModello) {
        RispostaControlli rc = new RispostaControlli();

        try {
            // lettura di UsrOrgIam
            SIUsrOrganizIam tmpOrganizIam = this.leggiUsrOrgIam(idUnitaDoc);
            if (tmpOrganizIam == null) {
                rc.setrBoolean(false);
                rc.setDsErr("(Impossibile determinare l'Organizzazione in IAM - UsrOrganizIam)");
                return rc;
            }

            // lettura di Ente convenzionato
            SIOrgEnteSiam tmpEnteConvenz = this.leggiEnteSiam(tmpOrganizIam);
            if (tmpEnteConvenz == null) {
                rc.setrBoolean(false);
                rc.setDsErr("(Impossibile determinare l'Ente convenzionato in IAM - OrgEnteConvenz)");
                return rc;
            }

            // lettura modello
            SIDecModelloComunic tmpModelloComunic = this.leggiModelloComunic(tmpEnteConvenz, usoModello, oggQryModello);
            if (tmpModelloComunic == null) {
                rc.setrBoolean(false);
                rc.setDsErr("");
                return rc;
            }
            rc.setrLong(tmpModelloComunic.getIdModelloComunic());
            rc.setrString(tmpModelloComunic.getBlTestoComunic());
            rc.setrBoolean(true);
        } catch (Exception e) {
            rc.setrBoolean(false);
            rc.setCodErr(MessaggiWSBundle.ERR_666);
            rc.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ProduzioneDipEsibizione.caricaModello " + e.getMessage()));
            log.error("Eccezione nella lettura del modello per DIP/esibizione ", e);
        }

        return rc;
    }

    public RispostaControlli caricaDatiDaQuery(long idModello, TipiUsoQuery tuq, long idEntitaSacer) {
        RispostaControlli rc = new RispostaControlli();
        Map<String, String> tmpMap = new HashMap<>();

        try {
            List<SIDecQueryModelloComunic> lstQry = this.leggiQueryModello(idModello, tuq);
            for (SIDecQueryModelloComunic sidqmc : lstQry) {
                this.leggiDatiQueryInMappa(tmpMap, sidqmc, idEntitaSacer);
            }
            // restituisce la mappa valori, eventualmente vuota o con valori a null
            rc.setrObject(tmpMap);
            rc.setrBoolean(true);
        } catch (Exception e) {
            rc.setrBoolean(false);
            rc.setCodErr(MessaggiWSBundle.ERR_666);
            rc.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ProduzioneDipEsibizione.caricaDatiDaQuery " + e.getMessage()));
            log.error("Eccezione nella lettura dei dati per il modello per DIP/esibizione ", e);
        }

        return rc;
    }

    private SIUsrOrganizIam leggiUsrOrgIam(long idUnitaDoc) {
        List<SIUsrOrganizIam> siuoi;

        AroUnitaDoc aud = entityManager.find(AroUnitaDoc.class, idUnitaDoc);

        String queryStr = "select t from SIUsrOrganizIam t " + "where t.sIAplApplic.nmApplic = :nmApplic "
                + "and t.siAplTipoOrganiz.nmTipoOrganiz = :nmTipoOrganiz "
                + "and t.idOrganizApplic = :idOrganizApplic ";
        javax.persistence.Query query = entityManager.createQuery(queryStr, SIUsrOrganizIam.class);
        query.setParameter("nmApplic", Constants.SACER);
        query.setParameter("nmTipoOrganiz", "STRUTTURA");
        query.setParameter("idOrganizApplic", aud.getOrgStrut().getIdStrut());
        siuoi = query.getResultList();
        if (siuoi.size() != 1) {
            return null;
        } else {
            return siuoi.get(0);
        }
    }

    private SIOrgEnteSiam leggiEnteSiam(SIUsrOrganizIam organizIam) {
        List<SIOrgEnteSiam> rEnteConvenz;

        String queryStr = "select t from SIOrgEnteSiam t " + "join t.orgEnteConvenzOrgs to "
                + "where to.siUsrOrganizIam = :siUsrOrganizIam ";
        javax.persistence.Query query = entityManager.createQuery(queryStr, SIOrgEnteSiam.class);
        query.setParameter("siUsrOrganizIam", organizIam);
        rEnteConvenz = query.getResultList();
        if (rEnteConvenz.size() != 1) {
            return null;
        } else {
            return rEnteConvenz.get(0);
        }
    }

    private SIDecModelloComunic leggiModelloComunic(SIOrgEnteSiam enteConvenz, TipiUsoModello usoModello,
            TipiOggQryModello oggQryModello) {
        List<SIDecModelloComunic> modelli;

        String queryStr = "select t from SIDecModelloComunic t " + "join t.decUsoModelloComunics tu "
                + "where t.tiComunic = :tiComunic " + "and t.tiOggettoQuery = :tiOggettoQuery "
                + "and t.dtIstituz <= :dataOggi " + "and t.dtSoppres > :dataOggi "
                + "and ((tu.tipoUso = :tipoUsoAmb and tu.siOrgAmbienteEnteConvenz = :siOrgAmbienteEnteConvenz) "
                + "or (tu.tipoUso = :tipoUsoEnte and tu.siOrgEnteConvenz = :siOrgEnteConvenz)) ";
        javax.persistence.Query query = entityManager.createQuery(queryStr, SIDecModelloComunic.class);
        query.setParameter("tiComunic", usoModello.name());
        query.setParameter("tiOggettoQuery", oggQryModello.name());
        query.setParameter("dataOggi", new Date());
        query.setParameter("tipoUsoAmb", TipiUsoTNotifica.AMBIENTE_ENTE_CONVENZ.name());
        query.setParameter("siOrgAmbienteEnteConvenz", enteConvenz.getSiOrgAmbienteEnteConvenz());
        query.setParameter("tipoUsoEnte", TipiUsoTNotifica.ENTE_CONVENZ.name());
        query.setParameter("siOrgEnteConvenz", enteConvenz);
        modelli = query.getResultList();
        if (modelli.size() != 1) {
            return null;
        } else {
            return modelli.get(0);
        }
    }

    private List<SIDecQueryModelloComunic> leggiQueryModello(long idModello, TipiUsoQuery tuq) {
        List<SIDecQueryModelloComunic> tmpList = null;

        String queryStr = "select t from SIDecQueryModelloComunic t "
                + "where t.siDecModelloComunic.idModelloComunic = :idModelloComunic "
                + "and t.tiUsoQuery = :tiUsoQuery ";

        javax.persistence.Query query = entityManager.createQuery(queryStr, SIDecQueryModelloComunic.class);
        query.setParameter("idModelloComunic", idModello);
        query.setParameter("tiUsoQuery", tuq.name());
        tmpList = query.getResultList();
        return tmpList;
    }

    private void leggiDatiQueryInMappa(Map<String, String> tmpMainMap, SIDecQueryModelloComunic sidqmc,
            long idOggetto) {
        TipiResultQuery trq = TipiResultQuery.valueOf(sidqmc.getTiResultSet());
        String prefisso = sidqmc.getNmQuery();

        // creo una mappa per tradurre le posizioni di colonna in nomi di variabili
        // inoltre precarico nella mappa principale i valori dei placeholder (con valore vuoto).
        // In questo modo se una query non rende risultati. il placegolder esiste, ha valore vuoto
        // e può essere usato nel template/modello di comunicazione.
        Map<Long, String> tmpMappaCampi = new HashMap<>();
        for (SIDecColQueryModelloComunic tmpPos : sidqmc.getDecColQueryModelloComunics()) {
            String tmpNomePlaceHolder = MessageFormat.format(FMT_KEY_TEMPLATE, prefisso, tmpPos.getNmCol());
            tmpMappaCampi.put(tmpPos.getPgPosizCol(), tmpNomePlaceHolder);
            tmpMainMap.put(tmpNomePlaceHolder, "");
        }
        // eseguo la query
        javax.persistence.Query query = entityManager.createNativeQuery(sidqmc.getBlQuery());
        query.setParameter(1, idOggetto);
        List<Object> tmpList = query.getResultList();
        //
        for (Object tmpRigaRaw : tmpList) {
            Long colonna = 1L;
            Object[] tmpRiga;
            // il resultset è composto da più colonne, viene resa una lista di Object[].
            // se è composto da una sola colonna, viene resa una lista di valori.
            // in questo caso, per coerenza, la traduco in un Object[] di un solo elemento.
            if (tmpRigaRaw instanceof Object[]) {
                tmpRiga = (Object[]) tmpRigaRaw;
            } else {
                tmpRiga = new Object[] { tmpRigaRaw };
            }

            for (Object tmpColonna : tmpRiga) {
                if (tmpMappaCampi.containsKey(colonna) && tmpColonna != null) {
                    String tmpNomePlaceHolder = tmpMappaCampi.get(colonna);
                    if (trq == TipiResultQuery.SINGLE_ROW) {
                        tmpMainMap.put(tmpNomePlaceHolder, tmpColonna.toString());
                    } else {
                        String tmpOldVal = tmpMainMap.get(tmpNomePlaceHolder);
                        if (tmpOldVal != null && !tmpOldVal.isEmpty()) {
                            tmpMainMap.put(tmpNomePlaceHolder, tmpOldVal + "\n" + tmpColonna.toString());
                        } else {
                            tmpMainMap.put(tmpNomePlaceHolder, tmpColonna.toString());
                        }
                    }
                }
                colonna++;
            }
        }
    }

}
