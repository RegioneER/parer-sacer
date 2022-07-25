package it.eng.parer.async.helper;

import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.VrsContenutoFile;
import it.eng.parer.entity.VrsDatiSessioneVers;
import it.eng.parer.entity.VrsDocNonVer;
import it.eng.parer.entity.VrsFileSessione;
import it.eng.parer.entity.VrsSessioneVers;
import it.eng.parer.entity.VrsUnitaDocNonVer;
import it.eng.parer.viewEntity.LogVLisIniSchedJob;
import it.eng.parer.viewEntity.LogVLisIniSchedJobStrut;
import it.eng.parer.viewEntity.VrsVAggFallitiRisolto;
import it.eng.parer.viewEntity.VrsVVersFallitiDaNorisol;
import it.eng.parer.viewEntity.VrsVVersFallitiDaVerif;
import it.eng.parer.viewEntity.VrsVVersFallitiRisolto;
import it.eng.parer.web.dto.MonitoraggioFiltriListaVersFallitiBean;
import it.eng.parer.web.util.Constants;
import it.eng.spagoCore.error.EMFError;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "CalcoloMonitoraggioHelper")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CalcoloMonitoraggioHelper {

    Logger log = LoggerFactory.getLogger(CalcoloMonitoraggioHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    @EJB
    private CalcoloMonitoraggioHelper me;

    public List<Long> getListaSessioniVersByUsr(boolean checkUsr, String flVerificato) {
        /*
         * Ricavo le entità di tipo VrsSessioneVers di tipo chiusa in errore dove la struttura è nulla
         */
        StringBuilder queryStr = new StringBuilder("SELECT ses.idSessioneVers FROM VrsSessioneVers ses "
                + "WHERE ses.tiStatoSessioneVers = 'CHIUSA_ERR' " + "AND ses.orgStrut is null ");
        if (checkUsr) {
            /* cerco di recuperare la struttura attraverso User */
            queryStr.append("AND (SELECT COUNT(iao) FROM IamAbilOrganiz iao "
                    + "WHERE iao.iamUser.nmUserid = ses.nmUseridWs) = 1 ");
        }

        if (flVerificato != null) {
            queryStr.append(" AND ses.flSessioneErrVerif = :flVerificato ");
        }
        queryStr.append(" ORDER BY ses.dtApertura ");

        Query query = entityManager.createQuery(queryStr.toString());
        if (flVerificato != null) {
            query.setParameter("flVerificato", flVerificato);
        }

        return (List<Long>) query.getResultList();
    }

    public List<VrsVVersFallitiDaVerif> getListaVersFallitiDaVerif(Long idStrut, Date ultimaRegistrazione) {
        String queryStr = "SELECT u FROM VrsVVersFallitiDaVerif u " + "WHERE u.idStrut = :idStrut "
                + "AND u.dtApertura > :ultimaRegistrazione ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("ultimaRegistrazione", ultimaRegistrazione);
        return (List<VrsVVersFallitiDaVerif>) query.getResultList();
    }

    public List<VrsVVersFallitiDaNorisol> getListaVersFallitiDaNorisol(Long idStrut, Date ultimaRegistrazione) {
        String queryStr = "SELECT u FROM VrsVVersFallitiDaNorisol u " + "WHERE u.idStrut = :idStrut "
                + "AND u.dtApertura > :ultimaRegistrazione ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("ultimaRegistrazione", ultimaRegistrazione);
        return (List<VrsVVersFallitiDaNorisol>) query.getResultList();
    }

    public Date getUltimaRegistrazione(String nmJob, Long idStrut) {
        String queryStr = "SELECT u FROM LogVLisIniSchedJobStrut u " + "WHERE u.nmJob = :nmJob "
                + "AND u.idStrut = :idStrut " + "ORDER BY u.dtRegLogJobIni DESC ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("nmJob", nmJob);
        query.setParameter("idStrut", idStrut);

        List<LogVLisIniSchedJobStrut> lastSched = (List<LogVLisIniSchedJobStrut>) query.getResultList();

        /*
         * Imposto la sezione relativa alla data all'1 gennaio 2011 relativa all'ora alle 0:00:00.000
         */
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2011);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date ultimaData = cal.getTime();

        if (!lastSched.isEmpty()) {
            ultimaData = lastSched.get(0).getDtRegLogJobIni();
        }

        return ultimaData;
    }

    public Date getUltimaRegistrazione(String nmJob) {
        String queryStr = "SELECT u FROM LogVLisIniSchedJob u " + "WHERE u.nmJob = :nmJob "
                + "ORDER BY u.dtRegLogJobIni DESC ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("nmJob", nmJob);

        List<LogVLisIniSchedJob> lastSched = (List<LogVLisIniSchedJob>) query.getResultList();

        /*
         * Imposto la sezione relativa alla data all'1 gennaio 2011 relativa all'ora alle 0:00:00.000
         */
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2011);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date ultimaData = cal.getTime();

        // Devo prendere il penultimo, perchè l'ultimo l'ho appena scritto!
        if (!lastSched.isEmpty() && lastSched.size() > 1) {
            ultimaData = lastSched.get(1).getDtRegLogJobIni();
        }

        return ultimaData;
    }

    public List<OrgStrut> getStruttureVersanti() {
        String queryStr = "SELECT u FROM OrgStrut u ";
        Query query = entityManager.createQuery(queryStr);
        return (List<OrgStrut>) query.getResultList();
    }

    public String getXmlRichiestaSessione(Long idSessione) {
        Query query = entityManager.createQuery("SELECT xmlRich.blXml " + "FROM VrsSessioneVers ses "
                + "JOIN ses.vrsDatiSessioneVers datiSes " + "JOIN datiSes.vrsXmlDatiSessioneVers xmlRich "
                + "WHERE xmlRich.tiXmlDati = 'RICHIESTA' AND ses.idSessioneVers = :idSessione");
        query.setParameter("idSessione", idSessione);
        return (String) query.getSingleResult();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void impostaVersamentoFallitoVerif(Long idVersFallito) {
        VrsSessioneVers ses = entityManager.find(VrsSessioneVers.class, idVersFallito);
        ses.setFlSessioneErrVerif("1");
        ses.setFlSessioneErrNonRisolub("0");
        log.info("Il versamento fallito " + idVersFallito + " &#232; stato impostato verificato");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void impostaVersamentoFallitoNorisol(Long idVersFallito) {
        VrsSessioneVers ses = entityManager.find(VrsSessioneVers.class, idVersFallito);
        ses.setFlSessioneErrNonRisolub("1");
        log.info("Il versamento fallito " + idVersFallito + " &#232; stato impostato non risolubile");
    }

    public void calcolaChiave(String xmlRich, VrsSessioneVers ses) {
        /* Se il file XML non è vuoto... */
        if (xmlRich != null) {
            /*
             * ... e se uno dei campi della chiave unità documentaria (registro/anno/numero) sono vuoti, provo a
             * procedere al calcolo della chiave unità documentaria
             */
            if (ses.getCdKeyUnitaDoc() == null || ses.getCdRegistroKeyUnitaDoc() == null
                    || ses.getAaKeyUnitaDoc() == null) {

                String numero = null;
                String anno = null;
                String tipoReg = null;

                int chiaveStart = xmlRich.indexOf("<Chiave>");
                int chiaveStop = xmlRich.indexOf("</Chiave>");

                // Ricavo, se riesco, i valori dei tag che mi interessano
                if (chiaveStart != -1 && chiaveStop != -1) {
                    String chiave = xmlRich.substring(chiaveStart + ("<Chiave>".length()), chiaveStop);
                    int numeroStart = chiave.indexOf("<Numero>");
                    int numeroStop = chiave.indexOf("</Numero>");
                    int annoStart = chiave.indexOf("<Anno>");
                    int annoStop = chiave.indexOf("</Anno>");
                    int tipoRegStart = chiave.indexOf("<TipoRegistro>");
                    int tipoRegStop = chiave.indexOf("</TipoRegistro>");

                    if (numeroStart != -1 && numeroStop != -1) {
                        numero = chiave.substring(numeroStart + 8, numeroStop);
                    }
                    if (annoStart != -1 && annoStop != -1) {
                        anno = chiave.substring(annoStart + 6, annoStop);
                    }
                    if (tipoRegStart != -1 && tipoRegStop != -1) {
                        tipoReg = chiave.substring(tipoRegStart + 14, tipoRegStop);
                    }

                    // Trimmo le stringhe
                    if (numero != null) {
                        numero = numero.trim();
                    }
                    if (anno != null) {
                        anno = anno.trim();
                    }
                    if (tipoReg != null) {
                        tipoReg = tipoReg.trim();
                    }

                    // Controllo la che l'anno sia effettivamenteo un numero
                    if (ses.getCdKeyUnitaDoc() == null && ses.getAaKeyUnitaDoc() == null
                            && ses.getCdRegistroKeyUnitaDoc() == null) {
                        if (numero != null && !numero.equals("")) {
                            // Controllo che cdKeyUnitaDoc abbia una lunghezza inferiore o uguale ai 100 byte previsti
                            // su DB, altrimenti tronco
                            ses.setCdKeyUnitaDoc(numero.length() > 100 ? numero.substring(0, 100) : numero);
                        }
                        if (tipoReg != null && !tipoReg.equals("")) {
                            // Controllo che cdRegistroKeyUnitaDoc abbia una lunghezza inferiore o uguale ai 100 byte
                            // previsti su DB, altrimenti tronco
                            ses.setCdRegistroKeyUnitaDoc(tipoReg.length() > 100 ? tipoReg.substring(0, 100) : tipoReg);
                        }
                        if (anno != null && !anno.equals("")) {
                            try {
                                if (anno.length() <= 4) {
                                    ses.setAaKeyUnitaDoc(new BigDecimal(anno));
                                } else {
                                    ses.setAaKeyUnitaDoc(new BigDecimal(0));
                                }
                            } catch (NumberFormatException e) {
                                ses.setAaKeyUnitaDoc(new BigDecimal(0));
                            }
                        }
                    }

                    // Ricerca unita doc con idStrut e questi dati, se esiste setto l'id nella sessione
                    if (ses.getCdKeyUnitaDoc() != null && ses.getAaKeyUnitaDoc() != null
                            && ses.getCdRegistroKeyUnitaDoc() != null) {
                        AroUnitaDoc ud = me.getUnitaDocIfExists(ses.getOrgStrut().getIdStrut(), ses.getCdKeyUnitaDoc(),
                                ses.getAaKeyUnitaDoc(), ses.getCdRegistroKeyUnitaDoc());
                        if (ud != null) {
                            ses.setAroUnitaDoc(ud);
                        }
                    }
                }
            }

            // Calcolo della chiave documento per i versamenti di tipo "AGGIUNGI_DOCUMENTO"
            if (ses.getTiSessioneVers() != null
                    && ses.getTiSessioneVers().equals(Constants.TipoSessione.AGGIUNGI_DOCUMENTO.name())) {
                String idDoc = null;
                int idDocStart = xmlRich.indexOf("<IDDocumento>");
                int idDocStop = xmlRich.indexOf("</IDDocumento>");

                if (idDocStart != -1 && idDocStop != -1) {
                    idDoc = xmlRich.substring(idDocStart + 13, idDocStop);
                }

                if (idDoc != null) {
                    idDoc = idDoc.trim();
                }

                if (idDoc != null && !idDoc.equals("")) {
                    // Controllo che cdKeyDocVers abbia una lunghezza inferiore o uguale ai 100 byte previsti su DB,
                    // altrimenti tronco
                    ses.setCdKeyDocVers(idDoc.length() > 100 ? idDoc.substring(0, 100) : idDoc);
                }
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void calcolaStrutturaByXml(Long idSessione) {
        VrsSessioneVers ses = entityManager.find(VrsSessioneVers.class, idSessione);

        String xml = me.getXmlRichiestaSessione(ses.getIdSessioneVers());
        me.calcolaStrutturaXml(xml, ses);
    }

    public void calcolaStrutturaXml(String xmlRich, VrsSessioneVers ses) {
        String nmAmbiente = null;
        String nmEnte = null;
        String nmStrut = null;
        boolean setSessioneVerif = false;
        if (xmlRich != null) {
            int ambStart = xmlRich.indexOf("<Ambiente>");
            int ambStop = xmlRich.indexOf("</Ambiente>");
            int enteStart = xmlRich.indexOf("<Ente>");
            int enteStop = xmlRich.indexOf("</Ente>");
            int strutStart = xmlRich.indexOf("<Struttura>");
            int strutStop = xmlRich.indexOf("</Struttura>");

            if (ambStart != -1 && ambStop != -1) {
                nmAmbiente = xmlRich.substring(ambStart + 10, ambStop);
            }
            if (enteStart != -1 && enteStop != -1) {
                nmEnte = xmlRich.substring(enteStart + 6, enteStop);
            }
            if (strutStart != -1 && strutStop != -1) {
                nmStrut = xmlRich.substring(strutStart + 11, strutStop);
            }

            if (nmAmbiente != null && nmEnte != null && nmStrut != null) {
                OrgStrut strut = me.getStrutturaIfExist(nmAmbiente, nmEnte, nmStrut);
                if (strut != null) {
                    // Setto i valori ricavati nell'entity
                    ses.setNmAmbiente(nmAmbiente);
                    ses.setNmEnte(nmEnte);
                    ses.setNmStrut(nmStrut);
                    ses.setOrgStrut(strut);

                    /*
                     * Eseguo l'update della struttura anche nei record di VrsContenutoFile
                     */
                    log.debug("Eseguo l'update della struttura anche nei record di VrsContenutoFile");
                    me.updateVrsContenutoFile(ses.getIdSessioneVers(), strut.getIdStrut());

                    // a questo punto calcolo anche la chiave unità documentaria
                    me.calcolaChiave(xmlRich, ses);

                    me.checkSessioneRisolta(ses);
                } else {
                    setSessioneVerif = true;
                }
            } else {
                setSessioneVerif = true;
            }
        } else {
            setSessioneVerif = true;
        }

        if (setSessioneVerif) {
            ses.setFlSessioneErrVerif("1");
        }
    }

    public OrgStrut getStrutturaIfExist(String nmAmbiente, String nmEnte, String nmStruttura) {
        Query query = entityManager
                .createQuery("SELECT strut FROM OrgStrut strut JOIN strut.orgEnte ente JOIN ente.orgAmbiente ambiente "
                        + "WHERE ambiente.nmAmbiente = :ambiente AND ente.nmEnte = :ente AND strut.nmStrut = :strut");
        query.setParameter("ambiente", nmAmbiente);
        query.setParameter("ente", nmEnte);
        query.setParameter("strut", nmStruttura);
        List<OrgStrut> resultList = (List<OrgStrut>) query.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }

    public AroUnitaDoc getUnitaDocIfExists(Long idStrut, String cdKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdRegistroKeyUnitaDoc) {
        Query query = entityManager.createQuery("SELECT ud FROM AroUnitaDoc ud "
                + "WHERE ud.orgStrut.idStrut = :idStrut AND ud.cdKeyUnitaDoc = :cdKeyUnitaDoc "
                + "AND ud.aaKeyUnitaDoc = :aaKeyUnitaDoc AND ud.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc");
        query.setParameter("idStrut", idStrut);
        query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        List<AroUnitaDoc> resultList = (List<AroUnitaDoc>) query.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }

    /**
     * Ricavo la lista dei "Versamenti unità documentarie falliti" Di questi ricavo quelli con chiave nulla
     *
     * @param filtriSes
     *            filtro monitoraggio
     * 
     * @return listaVersErr in errore
     * 
     * @throws EMFError
     *             errore generico
     */
    public List<Object[]> getSessioniSenzaChiave(MonitoraggioFiltriListaVersFallitiBean filtriSes) throws EMFError {

        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT u.idSessioneVers, v.blXml FROM MonVLisVersErrIam u, VrsXmlDatiSessioneVers v JOIN v.vrsDatiSessioneVers dv "
                        + "WHERE u.idSessioneVers = dv.vrsSessioneVers.idSessioneVers ");

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtriSes.getIdAmbiente();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtriSes.getIdEnte();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtriSes.getIdStrut();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        String tipoSes = filtriSes.getTipoSes();
        if (tipoSes != null) {
            queryStr.append(whereWord).append("u.tiSessioneVers = :tipoSes ");
            whereWord = "AND ";
        }

        String flRisolto = filtriSes.getRisolto();
        if (flRisolto != null) {
            queryStr.append(whereWord).append("u.flRisolto = :flRisolto ");
            whereWord = "AND ";
        }

        // GESTIONE PERIODO - GIORNO
        Calendar dataDBa = Calendar.getInstance();
        Calendar dataDBda = Calendar.getInstance();
        dataDBda.set(Calendar.HOUR_OF_DAY, 0);
        dataDBda.set(Calendar.MINUTE, 0);
        dataDBda.set(Calendar.SECOND, 0);
        dataDBda.set(Calendar.MILLISECOND, 0);
        dataDBa.set(Calendar.HOUR_OF_DAY, 23);
        dataDBa.set(Calendar.MINUTE, 59);
        dataDBa.set(Calendar.SECOND, 59);
        dataDBa.set(Calendar.MILLISECOND, 999);

        // Inserimento nella query del filtro periodo versamento
        String periodoVers = filtriSes.getPeriodoVers();
        if (periodoVers != null) {
            if (periodoVers.equals("ULTIMI7")) {
                dataDBda.add(Calendar.DATE, -6);
                queryStr.append(whereWord).append("u.dtChiusura between :datada AND :dataa ");
            } else if (periodoVers.equals("OGGI")) {
                queryStr.append(whereWord).append("u.dtChiusura between :datada AND :dataa ");
            } else {
                queryStr.append(whereWord).append("u.dtChiusura <= :dataa ");
            }
            whereWord = "AND ";
        }

        Date data_orario_da = (filtriSes.getGiornoVersDaValidato() != null ? filtriSes.getGiornoVersDaValidato()
                : null);
        Date data_orario_a = (filtriSes.getGiornoVersAValidato() != null ? filtriSes.getGiornoVersAValidato() : null);

        if (data_orario_da != null && data_orario_a != null) {
            queryStr.append(whereWord).append("u.dtChiusura between :datada AND :dataa ");
        } else if (data_orario_da != null) {
            queryStr.append(whereWord).append("u.dtChiusura >= :datada ");
        } else if (data_orario_a != null) {
            queryStr.append(whereWord).append("u.dtChiusura <= :dataa ");
        }

        String flSessioneErrVerif = filtriSes.getVerificato();
        if (flSessioneErrVerif != null) {
            queryStr.append(whereWord).append("u.flSessioneErrVerif = :flSessioneErrVerif ");
            whereWord = "AND ";
        }

        String flNonRisolub = filtriSes.getNonRisolubile();
        if (flNonRisolub != null) {
            queryStr.append(whereWord).append("u.flSessioneErrNonRisolub = :flSessioneErrNonRisolub ");
            whereWord = "AND ";
        }

        // Gestione filtri codice errore
        String classeErrore = filtriSes.getClasseErrore();
        String sottoClasseErrore = filtriSes.getSottoClasseErrore();
        String codiceErrore = filtriSes.getCodiceErrore();

        if (codiceErrore != null) {
            queryStr.append(whereWord).append("u.cdErr = :cdErr ");
            whereWord = "AND ";
        } else if (sottoClasseErrore != null || classeErrore != null) {
            queryStr.append(whereWord).append("u.cdErr LIKE :cdErr ");
            whereWord = "AND ";
        }

        BigDecimal idUsrIam = filtriSes.getIdUserIam();
        if (idUsrIam != null) {
            queryStr.append(whereWord).append("u.idUserIam = :idUsrIam ");
            whereWord = "AND ";
        }

        queryStr.append(whereWord).append("v.tiXmlDati = 'RICHIESTA' ");

        if (tipoSes != null) {
            if (tipoSes.equals(Constants.TipoSessione.VERSAMENTO.name())) {
                queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc is null " + "AND u.cdKeyUnitaDoc is null "
                        + "AND u.aaKeyUnitaDoc is null ");
            } else if (tipoSes.equals(Constants.TipoSessione.AGGIUNGI_DOCUMENTO.name())) {
                queryStr.append(whereWord).append("((u.cdRegistroKeyUnitaDoc is null " + "AND u.cdKeyUnitaDoc is null "
                        + "AND u.aaKeyUnitaDoc is null) " + "OR u.cdKeyDocVers is null) ");
            }
        }

        // ordina per data chiusura descrescente
        queryStr.append("ORDER BY u.dtApertura");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (tipoSes != null) {
            query.setParameter("tipoSes", tipoSes);
        }

        if (flRisolto != null) {
            query.setParameter("flRisolto", flRisolto);
        }

        if (flNonRisolub != null) {
            query.setParameter("flSessioneErrNonRisolub", flNonRisolub);
        }

        if (periodoVers != null) {
            if (!periodoVers.equals("TUTTI")) {
                query.setParameter("datada", dataDBda.getTime(), TemporalType.TIMESTAMP);
            }
            query.setParameter("dataa", dataDBa.getTime(), TemporalType.TIMESTAMP);
        }

        if (data_orario_da != null && data_orario_a != null) {
            query.setParameter("datada", data_orario_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_orario_a, TemporalType.TIMESTAMP);
        } else if (data_orario_da != null) {
            query.setParameter("datada", data_orario_da, TemporalType.TIMESTAMP);
        } else if (data_orario_a != null) {
            query.setParameter("dataa", data_orario_a, TemporalType.TIMESTAMP);
        }

        if (flSessioneErrVerif != null) {
            query.setParameter("flSessioneErrVerif", flSessioneErrVerif);
        }

        if (codiceErrore != null) {
            query.setParameter("cdErr", codiceErrore);
        } else if (sottoClasseErrore != null) {
            query.setParameter("cdErr", sottoClasseErrore + '%');
        } else if (classeErrore != null) {
            query.setParameter("cdErr", classeErrore + '%');
        }

        if (idUsrIam != null) {
            query.setParameter("idUsrIam", idUsrIam);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<Object[]> listaVersErr = query.getResultList();

        return listaVersErr;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void calcolaChiaveUdDoc(Object[] objTotSacer) {
        VrsSessioneVers ses = entityManager.find(VrsSessioneVers.class, ((BigDecimal) objTotSacer[0]).longValue());
        String xmlrich = (String) objTotSacer[1];

        // aggiorno l'entity con i valori della chiave
        // contaAggiornati = contaAggiornati +
        me.calcolaChiave(xmlrich, ses);

        me.checkSessioneRisolta(ses);
    }

    /*
     * Eseguo l'update dell'idStruttura trovato nei record della sessione relativi a VrsContenutoFile
     */
    public void updateVrsContenutoFile(Long idSessione, Long idStrut) {
        VrsSessioneVers ses = entityManager.find(VrsSessioneVers.class, idSessione);
        for (VrsDatiSessioneVers datiSessioneVers : ses.getVrsDatiSessioneVers()) {
            for (VrsFileSessione fileSessione : datiSessioneVers.getVrsFileSessiones()) {
                for (VrsContenutoFile contenutoFile : fileSessione.getVrsContenutoFiles()) {
                    contenutoFile.setIdStrut(new BigDecimal(idStrut));
                }
            }
        }
    }

    public void checkSessioneRisolta(VrsSessioneVers ses) {
        log.debug("Verifico se la sessione è risolta o no");
        if (ses.getTiSessioneVers().equals(Constants.TipoSessione.VERSAMENTO.name())) {
            if (ses.getOrgStrut() != null && ses.getCdRegistroKeyUnitaDoc() != null && ses.getAaKeyUnitaDoc() != null
                    && ses.getCdKeyUnitaDoc() != null) {
                if (!me.isSessioneRisolta(ses.getIdSessioneVers(), ses.getTiSessioneVers())) {
                    log.debug("Sessione non risolta - creo un record in VrsUnitaDocNonVers, se non esiste già");
                    VrsUnitaDocNonVer udNonVer;
                    if ((udNonVer = me.getVrsUnitaDocNonVers(ses.getOrgStrut().getIdStrut(),
                            ses.getCdRegistroKeyUnitaDoc(), ses.getAaKeyUnitaDoc(), ses.getCdKeyUnitaDoc())) == null) {
                        VrsUnitaDocNonVer v = new VrsUnitaDocNonVer();
                        v.setOrgStrut(ses.getOrgStrut());
                        v.setCdRegistroKeyUnitaDoc(ses.getCdRegistroKeyUnitaDoc());
                        v.setAaKeyUnitaDoc(ses.getAaKeyUnitaDoc());
                        v.setCdKeyUnitaDoc(ses.getCdKeyUnitaDoc());
                        v.setDtFirstSesErr(ses.getDtApertura());
                        v.setDtLastSesErr(ses.getDtApertura());
                        v.setDsErrPrinc(ses.getDsErrPrinc());
                        v.setCdErrPrinc(ses.getCdErrPrinc());
                        entityManager.persist(v);
                        entityManager.flush();
                    } else {
                        log.debug(
                                "Sessione non risolta - esiste già un record, aggiorno la descrizione errore e la dtLastSessErr");
                        if (ses.getDtApertura().before(udNonVer.getDtFirstSesErr())) {
                            udNonVer.setDtFirstSesErr(ses.getDtApertura());
                        }
                        if (ses.getDtApertura().after(udNonVer.getDtLastSesErr())) {
                            udNonVer.setDtLastSesErr(ses.getDtApertura());
                        }
                        if (ses.getDsErrPrinc() != null && !ses.getDsErrPrinc().equals(udNonVer.getDsErrPrinc())) {
                            udNonVer.setDsErrPrinc("Diversi");
                        }
                        if (ses.getCdErrPrinc() != null && !ses.getCdErrPrinc().equals(udNonVer.getCdErrPrinc())) {
                            udNonVer.setCdErrPrinc("Diversi");
                        }
                    }
                }
            }
        } else if (ses.getTiSessioneVers().equals(Constants.TipoSessione.AGGIUNGI_DOCUMENTO.name())) {
            if (ses.getOrgStrut() != null && ses.getCdRegistroKeyUnitaDoc() != null && ses.getAaKeyUnitaDoc() != null
                    && ses.getCdKeyUnitaDoc() != null && ses.getCdKeyDocVers() != null) {
                if (!me.isSessioneRisolta(ses.getIdSessioneVers(), ses.getTiSessioneVers())) {
                    log.debug("Sessione non risolta - creo un record in VrsDocNonVers, se non esiste già");
                    VrsDocNonVer docNonVer;
                    if ((docNonVer = me.existVrsDocNonVers(ses.getOrgStrut().getIdStrut(),
                            ses.getCdRegistroKeyUnitaDoc(), ses.getAaKeyUnitaDoc(), ses.getCdKeyUnitaDoc(),
                            ses.getCdKeyDocVers())) == null) {
                        VrsDocNonVer v = new VrsDocNonVer();
                        v.setOrgStrut(ses.getOrgStrut());
                        v.setCdRegistroKeyUnitaDoc(ses.getCdRegistroKeyUnitaDoc());
                        v.setAaKeyUnitaDoc(ses.getAaKeyUnitaDoc());
                        v.setCdKeyUnitaDoc(ses.getCdKeyUnitaDoc());
                        v.setCdKeyDocVers(ses.getCdKeyDocVers());
                        v.setDtFirstSesErr(ses.getDtApertura());
                        v.setDtLastSesErr(ses.getDtApertura());
                        v.setDsErrPrinc(ses.getDsErrPrinc());
                        v.setCdErrPrinc(ses.getCdErrPrinc());
                        entityManager.persist(v);
                        entityManager.flush();
                    } else {
                        if (ses.getDtApertura().before(docNonVer.getDtFirstSesErr())) {
                            docNonVer.setDtFirstSesErr(ses.getDtApertura());
                        }
                        if (ses.getDtApertura().after(docNonVer.getDtLastSesErr())) {
                            docNonVer.setDtLastSesErr(ses.getDtApertura());
                        }
                        if (ses.getDsErrPrinc() != null && !ses.getDsErrPrinc().equals(docNonVer.getDsErrPrinc())) {
                            docNonVer.setDsErrPrinc("Diversi");
                        }
                        if (ses.getCdErrPrinc() != null && !ses.getCdErrPrinc().equals(docNonVer.getCdErrPrinc())) {
                            docNonVer.setCdErrPrinc("Diversi");
                        }
                    }
                }
            }
        }
    }

    public boolean isSessioneRisolta(long idSessioneVers, String tiSessioneVers) {
        boolean risolto = false;
        String table = null;
        if (tiSessioneVers.equals(Constants.TipoSessione.VERSAMENTO.name())) {
            table = "VrsVVersFallitiRisolto";
        } else if (tiSessioneVers.equals(Constants.TipoSessione.AGGIUNGI_DOCUMENTO.name())) {
            table = "VrsVAggFallitiRisolto";
        }
        String queryStr = "SELECT v FROM " + table + " v WHERE v.idSessioneVers = :idSessioneVers";
        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idSessioneVers", idSessioneVers);

        List vrsList = query.getResultList();
        if (vrsList != null && !vrsList.isEmpty()) {
            Object o = vrsList.get(0);
            if (o instanceof VrsVVersFallitiRisolto) {
                VrsVVersFallitiRisolto v = (VrsVVersFallitiRisolto) o;
                risolto = v.getFlRisolto().equals("1");
            } else if (o instanceof VrsVAggFallitiRisolto) {
                VrsVAggFallitiRisolto v = (VrsVAggFallitiRisolto) o;
                risolto = v.getFlRisolto().equals("1");
            }
        }
        return risolto;
    }

    public VrsUnitaDocNonVer getVrsUnitaDocNonVers(long idStrut, String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc) {
        Query query = entityManager.createQuery("SELECT v FROM VrsUnitaDocNonVer v "
                + "WHERE v.orgStrut.idStrut = :idStrut " + "AND v.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc "
                + "AND v.aaKeyUnitaDoc = :aaKeyUnitaDoc " + "AND v.cdKeyUnitaDoc = :cdKeyUnitaDoc");
        query.setParameter("idStrut", idStrut);
        query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        return (VrsUnitaDocNonVer) (query.getResultList().size() > 0 ? query.getResultList().get(0) : null);
    }

    public VrsDocNonVer existVrsDocNonVers(long idStrut, String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc, String cdKeyDocVers) {

        Query query = entityManager.createQuery("SELECT v FROM VrsDocNonVer v " + "WHERE v.orgStrut.idStrut = :idStrut "
                + "AND v.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc " + "AND v.aaKeyUnitaDoc = :aaKeyUnitaDoc "
                + "AND v.cdKeyUnitaDoc = :cdKeyUnitaDoc " + "AND v.cdKeyDocVers = :cdKeyDocVers");
        query.setParameter("idStrut", idStrut);
        query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        query.setParameter("cdKeyDocVers", cdKeyDocVers);
        return (VrsDocNonVer) (query.getResultList().size() > 0 ? query.getResultList().get(0) : null);

    }
}
