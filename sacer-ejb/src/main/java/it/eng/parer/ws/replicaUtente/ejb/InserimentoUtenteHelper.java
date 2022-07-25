package it.eng.parer.ws.replicaUtente.ejb;

import it.eng.integriam.server.ws.reputente.ListaIndIp;
import it.eng.parer.entity.IamAbilOrganiz;
import it.eng.parer.entity.IamAbilTipoDato;
import it.eng.parer.entity.IamAutorServ;
import it.eng.parer.entity.IamIndIpUser;
import it.eng.parer.entity.IamUser;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.replicaUtente.dto.InserimentoUtenteExt;
import it.eng.integriam.server.ws.reputente.ListaOrganizAbil;
import it.eng.integriam.server.ws.reputente.ListaServiziAutor;
import it.eng.integriam.server.ws.reputente.ListaTipiDatoAbil;
import it.eng.integriam.server.ws.reputente.OrganizAbil;
import it.eng.integriam.server.ws.reputente.TipoDatoAbil;
import it.eng.parer.ws.replicaUtente.dto.RispostaWSInserimentoUtente;
import it.eng.integriam.server.ws.reputente.Utente;
import it.eng.parer.exception.ParerErrorSeverity;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "InserimentoUtenteHelper")
@LocalBean
public class InserimentoUtenteHelper {

    private static final Logger log = LoggerFactory.getLogger(InserimentoUtenteHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    @EJB
    private ModificaUtenteHelper muHelper;

    /**
     * Inserimento su DB dell'applicazione (SACER, SACER_PING...) di un nuovo utente
     *
     * @param iuExt
     *            contenente i valori da inserire
     * @param rispostaWs
     *            la risposta in cui tenere traccia dell'esito dell'operazione
     * 
     * @throws ParerInternalError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void insertIamUser(InserimentoUtenteExt iuExt, RispostaWSInserimentoUtente rispostaWs)
            throws ParerInternalError {
        Utente utente = iuExt.getInserimentoUtenteInput();
        log.debug("Ricevuta chiamata insert " + utente.getNmUserid());
        IamUser iamUser = new IamUser();
        try {
            iamUser.setIdUserIam(utente.getIdUserIam());
            iamUser.setNmUserid(utente.getNmUserid());
            iamUser.setCdPsw(utente.getCdPsw());
            iamUser.setCdSalt(utente.getCdSalt());
            iamUser.setNmCognomeUser(utente.getNmCognomeUser());
            iamUser.setNmNomeUser(utente.getNmNomeUser());
            iamUser.setFlAttivo(utente.getFlAttivo());
            iamUser.setDtRegPsw(utente.getDtRegPsw());
            iamUser.setDtScadPsw(utente.getDtScadPsw());
            iamUser.setCdFisc(utente.getCdFisc());
            iamUser.setDsEmail(utente.getDsEmail());
            iamUser.setFlUserAdmin(utente.getFlUserAdmin());
            iamUser.setFlContrIp(utente.getFlContrIp());
            iamUser.setTipoUser(utente.getTipoUser());
            iamUser.setTipoAuth(utente.getTipoAuth());
            /* Inserisco gli IP a cui l'utente è abilitato */
            iamUser.setIamIndIpUsers(new ArrayList());
            ListaIndIp listaIndIp = utente.getListaIndIp();
            if (listaIndIp.getIndIp() != null) {
                log.debug("Necessario persistere " + listaIndIp.getIndIp().size() + " record di IamIndIpUser");
                for (String ip : listaIndIp.getIndIp()) {
                    IamIndIpUser iamIp = new IamIndIpUser();
                    iamIp.setIamUser(iamUser);
                    iamIp.setCdIndIpUser(ip);
                    iamUser.getIamIndIpUsers().add(iamIp);
                }
                log.debug("Eseguita persist di nuove IamIndIpUser");
            }

            /* Inserisco le organizzazioni abilitate */
            ListaOrganizAbil listaOrganizAbil = utente.getListaOrganizAbil();
            List<IamAbilOrganiz> iamAbilOrganizList = new ArrayList();
            if (listaOrganizAbil != null) {
                if (listaOrganizAbil.getOrganizAbilList() != null) {
                    log.debug("Necessario persistere " + listaOrganizAbil.getOrganizAbilList().size()
                            + " record di IamAbilOrganiz");
                    for (OrganizAbil organizAbil : listaOrganizAbil) {
                        /* Inserisco il record in IAM_ABIL_ORGANIZ */
                        IamAbilOrganiz iamAbilOrganiz = new IamAbilOrganiz();
                        iamAbilOrganiz.setIdOrganizApplic(new BigDecimal(organizAbil.getIdOrganizApplicAbil()));
                        iamAbilOrganiz.setFlOrganizDefault(organizAbil.isFlOrganizDefault() ? "1" : "0");
                        iamAbilOrganiz.setIamUser(iamUser);
                        List<IamAutorServ> iamAutorServList = new ArrayList();
                        ListaServiziAutor lsa = organizAbil.getListaServiziAutor();
                        if (lsa.getNmServizioAutor() != null) {
                            /* Inserisco i record in IAM_AUTOR_SERV */
                            log.debug("Necessario persistere " + lsa.getNmServizioAutor().size()
                                    + " record di servizi autor");
                            for (String nmServizioAutor : organizAbil.getListaServiziAutor()) {
                                IamAutorServ iamAutorServ = new IamAutorServ();
                                iamAutorServ.setIamAbilOrganiz(iamAbilOrganiz);
                                iamAutorServ.setNmServizioWeb(nmServizioAutor);
                                iamAutorServList.add(iamAutorServ);
                            }
                            log.debug("Eseguita persist di nuovi servizi autor");
                        }
                        iamAbilOrganiz.setIamAutorServs(iamAutorServList);
                        List<IamAbilTipoDato> iamAbilTipoDatoList = new ArrayList();
                        ListaTipiDatoAbil ltda = organizAbil.getListaTipiDatoAbil();
                        if (ltda.getTipoDatoAbilList() != null) {
                            if (ltda.getTipoDatoAbilList() != null) {
                                log.debug("Necessario persistere " + ltda.getTipoDatoAbilList().size()
                                        + " record di IamAbilTipoDato");
                                /* Inserisco i record in IAM_ABIL_TIPO_DATO */
                                for (TipoDatoAbil tipiDatoApplic : organizAbil.getListaTipiDatoAbil()) {
                                    IamAbilTipoDato iamAbilTipoDato = new IamAbilTipoDato();
                                    iamAbilTipoDato
                                            .setIdTipoDatoApplic(new BigDecimal(tipiDatoApplic.getIdTipoDatoApplic()));
                                    iamAbilTipoDato.setNmClasseTipoDato(tipiDatoApplic.getNmClasseTipoDato());
                                    iamAbilTipoDato.setIamAbilOrganiz(iamAbilOrganiz);
                                    iamAbilTipoDatoList.add(iamAbilTipoDato);
                                }
                                log.debug("Eseguita persist di nuovi IamAbilTipoDato");
                            }
                        }
                        iamAbilOrganiz.setIamAbilTipoDatos(iamAbilTipoDatoList);
                        iamAbilOrganiz.setIamUser(iamUser);
                        iamAbilOrganizList.add(iamAbilOrganiz);
                        log.debug("Eseguita persist di nuove IamAbilOrganiz");
                    }
                }
            }
            iamUser.setIamAbilOrganizs(iamAbilOrganizList);
            entityManager.persist(iamUser);
        } catch (Exception ex) {
            log.error(ExceptionUtils.getRootCauseMessage(ex), ex);
            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
            rispostaWs.setErrorCode(MessaggiWSBundle.SERVIZI_USR_001);
            rispostaWs.setErrorMessage("Errore nel salvataggio dell'utente " + ExceptionUtils.getRootCauseMessage(ex));
            throw new ParerInternalError(ParerErrorSeverity.ERROR,
                    "Errore nel salvataggio dell'utente " + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
    }

    public boolean existsUtente(long idUserIam) {
        return entityManager.find(IamUser.class, idUserIam) != null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateFromInserimentoIamUser(InserimentoUtenteExt iuExt, RispostaWSInserimentoUtente rispostaWs)
            throws ParerInternalError {
        Utente utente = iuExt.getInserimentoUtenteInput();
        log.debug("Ricevuta chiamata di modifica per l'utente " + utente.getNmUserid()
                + " in quanto già presente ma NON attivo ");
        try {
            muHelper.eseguiModificaUtente(utente);
        } catch (Exception ex) {
            log.error(ExceptionUtils.getRootCauseMessage(ex), ex);
            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
            rispostaWs.setErrorCode(MessaggiWSBundle.SERVIZI_USR_001);
            rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.SERVIZI_USR_001,
                    ExceptionUtils.getRootCauseMessage(ex)));
            throw new ParerInternalError(ParerErrorSeverity.ERROR,
                    "Errore nel salvataggio dell'utente " + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
    }
}
