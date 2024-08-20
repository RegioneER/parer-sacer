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

package it.eng.parer.amministrazioneStrutture.gestioneTitolario.ejb;

import it.eng.parer.entity.DecLivelloTitol;
import it.eng.parer.entity.DecTitol;
import it.eng.parer.entity.DecValVoceTitol;
import it.eng.parer.entity.DecVoceTitol;
import it.eng.parer.entity.OrgOperTitol;
import it.eng.parer.entity.OrgOperVoceTitol;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.tablebean.DecLivelloTitolTableBean;
import it.eng.parer.slite.gen.tablebean.DecTitolRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.DecVLisValVoceTitolTableBean;
import it.eng.parer.slite.gen.viewbean.DecVTreeTitolRowBean;
import it.eng.parer.slite.gen.viewbean.DecVTreeTitolTableBean;
import it.eng.parer.titolario.xml.AttivoPerClassificazioneType;
import it.eng.parer.titolario.xml.CreaTitolario;
import it.eng.parer.titolario.xml.CreaTitolario.ListaOperazioniVoce;
import it.eng.parer.titolario.xml.CreaTitolario.Livelli;
import it.eng.parer.titolario.xml.CreaVoceType;
import it.eng.parer.titolario.xml.LivelloType;
import it.eng.parer.titolario.xml.OperazioneCreaType;
import it.eng.parer.titolario.xml.TipoFormatoLivelloType;
import it.eng.parer.titolario.xml.TitolarioType;
import it.eng.parer.viewEntity.AroVRicUnitaDoc;
import it.eng.parer.viewEntity.DecVLisValVoceTitol;
import it.eng.parer.viewEntity.DecVTreeTitol;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.amministrazioneStrutture.gestioneTitolario.dto.Voce;
import it.eng.parer.amministrazioneStrutture.gestioneTitolario.helper.StrutTitolariHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTitolario.utils.AttributesTitolario;
import it.eng.parer.slite.gen.tablebean.DecTitolTableBean;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.form.tree.Tree;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EJB di gestione dei titolari
 *
 *
 * {@link it.eng.parer.amministrazioneStrutture.gestioneTitolario}
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class StrutTitolariEjb {

    private static final Logger logger = LoggerFactory.getLogger(StrutTitolariEjb.class);

    @Resource
    private SessionContext context;

    @EJB
    private StrutTitolariHelper helper;

    @EJB
    private StrutTitolariCheck checker;

    /**
     * Eseguo il salvataggio dei dati del titolario creato tramite importazione xml
     *
     * @param titolario
     *            Intestazione del titolario
     * @param vociMap
     *            Albero delle voci ordinate
     * @param levelsList
     *            lista oggetti di tipo {@link LivelloType}
     * @param idStrut
     *            Id Struttura
     * @param cdRegistroKeyUnitaDoc
     *            Registro
     * @param aaKeyUnitaDoc
     *            Anno
     * @param cdKeyUnitaDoc
     *            Numero
     * @param dtDocInvio
     *            Data invio
     * @param dtIstituz
     *            Data istituzione del titolario
     * @param dtSoppres
     *            Data soppressione del titolario
     *
     * @return pk DecTitolario inserito
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long saveMap(TitolarioType titolario, Map<String, Voce> vociMap, List<LivelloType> levelsList,
            BigDecimal idStrut, String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc,
            Date dtDocInvio, Date dtIstituz, Date dtSoppres) {
        OrgStrut strut = helper.findById(OrgStrut.class, idStrut.longValue());

        // Creo l'oggetto titolario
        DecTitol titol = new DecTitol();
        titol.setNiLivelli(new BigDecimal(titolario.getNumeroLivelliUtilizzati()));
        titol.setDlNote(titolario.getNoteTitolario());
        titol.setDtIstituz(dtIstituz);
        titol.setDtSoppres(dtSoppres);
        titol.setNmTitol(titolario.getDenominazione());
        titol.setOrgStrut(strut);
        titol.setTiStatoTitol(CostantiDB.StatoTitolario.DA_VALIDARE.name());
        titol.setCdSepFascicolo(titolario.getSeparatoreVociTitolarioFascicoli());
        if (titol.getDecLivelloTitols() == null) {
            titol.setDecLivelloTitols(new ArrayList<DecLivelloTitol>());
        }
        if (titol.getDecVoceTitols() == null) {
            titol.setDecVoceTitols(new ArrayList<DecVoceTitol>());
        }
        if (titol.getOrgOperTitols() == null) {
            titol.setOrgOperTitols(new ArrayList<OrgOperTitol>());
        }

        OrgOperTitol orgOperTitol = context.getBusinessObject(StrutTitolariEjb.class).saveOrgOperTitol(
                Voce.Operation.CREA, strut, aaKeyUnitaDoc, cdKeyUnitaDoc, cdRegistroKeyUnitaDoc, dtDocInvio, dtIstituz,
                titol);

        // Crea l' "albero" di record ricorsivamente, partendo dal livello 0 (Padre nullo)
        Map<String, DecLivelloTitol> livelliMap = context.getBusinessObject(StrutTitolariEjb.class)
                .createLivelliMap(titol, levelsList);

        MutableInt counterVoci = new MutableInt(1);
        context.getBusinessObject(StrutTitolariEjb.class).alterVociRicorsivo(vociMap, livelliMap, null, titol,
                orgOperTitol, counterVoci, new ArrayList<String>(), new HashSet<Voce>(), false);

        helper.insertEntity(titol, true);

        return titol.getIdTitol();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveMap(BigDecimal idTitolario, TitolarioType titolarioType, Map<String, Voce> vociMap,
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, Date dtDocInvio,
            Date dtIstituz) {
        logger.info("Recupero titolario");
        DecTitol titolario = helper.findById(DecTitol.class, idTitolario.longValue());
        OrgStrut strut = titolario.getOrgStrut();

        if (titolarioType != null) {
            titolario.setNmTitol(titolarioType.getDenominazione());
            titolario.setNiLivelli(new BigDecimal(titolarioType.getNumeroLivelliUtilizzati()));
            titolario.setCdSepFascicolo(titolarioType.getSeparatoreVociTitolarioFascicoli());
            titolario.setDlNote(titolarioType.getNoteTitolario());
        }

        logger.info("Nuovo orgOperTitol con operazione di MODIFICA");
        OrgOperTitol orgOperTitol = context.getBusinessObject(StrutTitolariEjb.class).saveOrgOperTitol(
                Voce.Operation.MODIFICA, strut, aaKeyUnitaDoc, cdKeyUnitaDoc, cdRegistroKeyUnitaDoc, dtDocInvio,
                dtIstituz, titolario);
        Map<String, DecLivelloTitol> livelliMap = new HashMap<>();
        for (DecLivelloTitol livello : titolario.getDecLivelloTitols()) {
            livelliMap.put(livello.getNmLivelloTitol(), livello);
        }

        MutableInt counterVoci = new MutableInt(1);
        logger.info("Scorro ricorsivamente la mappa di voci per creare i record di voci e livelli");
        Set<Voce> vociDaChiudere = new HashSet<>();
        List<String> codiceVociChiuse = new ArrayList<>();
        context.getBusinessObject(StrutTitolariEjb.class).alterVociRicorsivo(vociMap, livelliMap, null, titolario,
                orgOperTitol, counterVoci, codiceVociChiuse, vociDaChiudere, false);
        logger.info("Eseguo la chiusura delle voci da chiudere");
        for (Voce voce : vociDaChiudere) {
            voce.setOperation(Voce.Operation.CHIUDI);
            context.getBusinessObject(StrutTitolariEjb.class).chiudiVoce(voce, titolario, orgOperTitol, counterVoci);
            counterVoci.increment();
        }

        logger.info("Titolario nuovamente DA_VALIDARE");
        titolario.setTiStatoTitol(CostantiDB.StatoTitolario.DA_VALIDARE.name());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Map<String, DecLivelloTitol> createLivelliMap(DecTitol titol, List<LivelloType> levelsList) {
        Map<String, DecLivelloTitol> livelliMap = new HashMap<>();
        logger.info("Eseguo la creazione dei livelli");
        for (LivelloType row : levelsList) {
            DecLivelloTitol livello = new DecLivelloTitol();
            livello.setNmLivelloTitol(row.getNomeLivello());
            livello.setNiLivello(new BigDecimal(row.getNumeroLivello()));
            livello.setDecTitol(titol);
            livello.setCdSepLivello(row.getCarattereSeparatoreLivello());
            livello.setTiFmtVoceTitol(row.getTipoFormatoLivello().name());

            titol.getDecLivelloTitols().add(livello);

            livelliMap.put(row.getNomeLivello(), livello);
        }
        return livelliMap;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public OrgOperTitol saveOrgOperTitol(Voce.Operation operation, OrgStrut strut, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc, String cdRegistroKeyUnitaDoc, Date dtDocInvio, Date dtIstituz, DecTitol titol) {
        OrgOperTitol orgOperTitol = new OrgOperTitol();
        orgOperTitol.setTiOperTitol(operation.name());
        orgOperTitol.setOrgStrut(strut);
        orgOperTitol.setAaDocInvio(aaKeyUnitaDoc);
        orgOperTitol.setCdDocInvio(cdKeyUnitaDoc);
        orgOperTitol.setCdRegistroDocInvio(cdRegistroKeyUnitaDoc);
        orgOperTitol.setDtDocInvio(dtDocInvio);
        orgOperTitol.setDtValOperTitol(dtIstituz);
        orgOperTitol.setDecTitol(titol);
        if (orgOperTitol.getOrgOperVoceTitols() == null) {
            orgOperTitol.setOrgOperVoceTitols(new ArrayList<OrgOperVoceTitol>());
        }
        titol.getOrgOperTitols().add(orgOperTitol);

        return orgOperTitol;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void alterVociRicorsivo(Map<String, Voce> vociMap, Map<String, DecLivelloTitol> livelliMap,
            DecVoceTitol vocePadre, DecTitol titol, OrgOperTitol orgOperTitol, MutableInt counterVoci,
            List<String> codiceVociChiuse, Set<Voce> vociDaChiudere, boolean toClose) {
        // Scorro ricorsivamente la mappa di voci per creare i record di voci e livelli
        Calendar calSoppres = Calendar.getInstance();
        calSoppres.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        calSoppres.set(Calendar.MILLISECOND, 0);

        for (Entry<String, Voce> entry : vociMap.entrySet()) {
            final Voce voceXml = entry.getValue();

            DecVoceTitol voce = null;
            if (voceXml.getOperation() != null) {
                logger.info("VOCE " + voceXml.getCodiceVoceComposito() + " - OPERAZIONE "
                        + voceXml.getOperation().toString());
                if (voceXml.getOperation() == Voce.Operation.CREA
                        && helper.existVoce(voceXml.getCodiceVoceComposito(), titol.getIdTitol())) {
                    logger.info("VOCE " + voceXml.getCodiceVoceComposito() + " preesistente - La metto in modifica");
                    voceXml.setOperation(Voce.Operation.MODIFICA);
                }

                if (voceXml.getOperation() == Voce.Operation.CREA) {
                    voce = context.getBusinessObject(StrutTitolariEjb.class).creaVoce(voceXml, entry.getKey(), titol,
                            vocePadre, livelliMap, orgOperTitol, counterVoci, calSoppres.getTime());
                    counterVoci.increment();
                    if (toClose) {
                        logger.info("----- DA CHIUDERE");
                        vociDaChiudere.add(voceXml);
                    }
                } else if (voceXml.getOperation() == Voce.Operation.MODIFICA) {
                    voce = context.getBusinessObject(StrutTitolariEjb.class).modificaVoce(voceXml, titol, orgOperTitol,
                            counterVoci, calSoppres.getTime());
                    counterVoci.increment();

                    if (voceXml.getDataFineValidita() != null
                            && !voceXml.getDataFineValidita().equals(calSoppres.getTime())
                            && voceXml.getDataFineValidita().getTime() != voce.getDtSoppres().getTime()) {
                        // Se la data fine validità è diversa da quella della voce, la voce è da chiudere.
                        codiceVociChiuse.add(voceXml.getCodiceVoceComposito());
                        toClose = true;
                    }

                    if (toClose) {
                        logger.info("----- DA CHIUDERE");
                        vociDaChiudere.add(voceXml);
                    }
                } else if (voceXml.getOperation() == Voce.Operation.CHIUDI) {
                    logger.info("----- DA CHIUDERE");
                    vociDaChiudere.add(voceXml);
                    codiceVociChiuse.add(voceXml.getCodiceVoceComposito());
                    toClose = true;
                }
            } else {
                logger.info("VOCE " + voceXml.getCodiceVoceComposito() + " - NESSUNA OPERAZIONE da xml");
                if (toClose) {
                    logger.info("----- DA CHIUDERE");
                    /*
                     * E' un nodo figlio di un nodo chiuso. Lo mantengo alla fine in una lista di nodi da chiudere, nel
                     * caso lo stesso nodo figlio abbia operazioni in sospeso. L'operazione viene eseguita, e poi viene
                     * fatta la chiusura
                     */
                    vociDaChiudere.add(voceXml);
                } else {
                    voce = helper.getVoce(titol.getIdTitol(), voceXml.getCodiceVoceComposito());
                }
            }

            if (voceXml.getNumeroFigli() > 0) {
                context.getBusinessObject(StrutTitolariEjb.class).alterVociRicorsivo(voceXml.getFigli(), livelliMap,
                        voce, titol, orgOperTitol, counterVoci, codiceVociChiuse, vociDaChiudere, toClose);
            }
            if (codiceVociChiuse.contains(voceXml.getCodiceVoceComposito())) {
                toClose = false;
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DecVoceTitol creaVoce(final Voce voceXml, String cdVoce, DecTitol titol, DecVoceTitol vocePadre,
            Map<String, DecLivelloTitol> livelliMap, OrgOperTitol orgOperTitol, MutableInt counterVoci,
            Date dtDefaultSoppres) {
        DecVoceTitol voce = new DecVoceTitol();
        voce.setCdCompositoVoceTitol(StringEscapeUtils.escapeEcmaScript(voceXml.getCodiceVoceComposito()));
        voce.setCdVoceTitol(StringEscapeUtils.escapeEcmaScript(cdVoce));
        voce.setDecTitol(titol);
        voce.setDecVoceTitol(vocePadre);
        voce.setDtIstituz(voceXml.getDataInizioValidita());
        voce.setDtSoppres(voceXml.getDataFineValidita() != null ? voceXml.getDataFineValidita() : dtDefaultSoppres);
        voce.setNiOrdVoceTitol(new BigDecimal(voceXml.getNumeroOrdine()));
        voce.setNiFascic(BigDecimal.ZERO);
        voce.setNiFascicVociFiglie(BigDecimal.ZERO);
        if (voce.getDecValVoceTitols() == null) {
            voce.setDecValVoceTitols(new ArrayList<>());
        }
        if (voce.getOrgOperVoceTitols() == null) {
            voce.setOrgOperVoceTitols(new ArrayList<>());
        }
        if (voce.getDecVoceTitols() == null) {
            voce.setDecVoceTitols(new ArrayList<>());
        }
        titol.getDecVoceTitols().add(voce);

        if (!livelliMap.containsKey(voceXml.getLivello().getNomeLivello())) {
            final LivelloType livelloXml = voceXml.getLivello();
            DecLivelloTitol livello = new DecLivelloTitol();
            livello.setDecTitol(titol);
            livello.setNiLivello(new BigDecimal(livelloXml.getNumeroLivello()));
            livello.setNmLivelloTitol(livelloXml.getNomeLivello());
            livello.setTiFmtVoceTitol(livelloXml.getTipoFormatoLivello().name());
            livello.setCdSepLivello(livelloXml.getCarattereSeparatoreLivello());

            titol.getDecLivelloTitols().add(livello);
            voce.setDecLivelloTitol(livello);

            livelliMap.put(voceXml.getLivello().getNomeLivello(), livello);
        } else {
            DecLivelloTitol livello = livelliMap.get(voceXml.getLivello().getNomeLivello());
            voce.setDecLivelloTitol(livello);
        }

        context.getBusinessObject(StrutTitolariEjb.class).saveDecValVoceTitol(voce, voceXml, voce.getDtIstituz(),
                voce.getDtSoppres());

        context.getBusinessObject(StrutTitolariEjb.class).saveOrgOperVoceTitol(orgOperTitol, voceXml, counterVoci, voce,
                voceXml.getDataInizioValidita());

        return voce;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DecVoceTitol modificaVoce(Voce voceXml, DecTitol titol, OrgOperTitol orgOperTitol, MutableInt counterVoci,
            Date dtDefaultSoppres) {
        DecVoceTitol voce = helper.getVoce(titol.getIdTitol(), voceXml.getCodiceVoceComposito());
        DecValVoceTitol valVoce = helper.getLastDecValVoceTitol(voce.getIdVoceTitol());

        Date lastDtSoppres = voce.getDtSoppres();
        boolean toClose = false;
        if (voceXml.getDataFineValidita() != null
                && voceXml.getDataFineValidita().getTime() != lastDtSoppres.getTime()) {
            if (!voceXml.getDataFineValidita().equals(dtDefaultSoppres)) {
                /*
                 * E' stata modificata la data fine validità del nodo, di conseguenza lascio invariate le date in fase
                 * di modifica, ma eseguo anche la chiusura del nodo
                 */
                toClose = true;
            } else {
                voce.setDtSoppres(dtDefaultSoppres);
            }
        }

        if (valVoce.getDtIniVal().compareTo(orgOperTitol.getDtValOperTitol()) == 0) {
            valVoce.setDsVoceTitol(StringEscapeUtils.escapeJava(voceXml.getDescrizioneVoce()));
            if (toClose) {
                valVoce.setDtFinVal(lastDtSoppres);
            } else {
                valVoce.setDtFinVal(
                        voceXml.getDataFineValidita() != null ? voceXml.getDataFineValidita() : dtDefaultSoppres);
            }
            valVoce.setFlUsoClassif(voceXml.getAttivoPerClassificazione().getVal());
            valVoce.setNiAnniConserv(new BigDecimal(voceXml.getTempoConservazione()));
            valVoce.setDlNote(voceXml.getNoteVoceTitolario());
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(orgOperTitol.getDtValOperTitol());
            cal.add(Calendar.DAY_OF_MONTH, -1);

            valVoce.setDtFinVal(cal.getTime());

            Date dataDefinitiva;
            if (toClose) {
                dataDefinitiva = lastDtSoppres;
            } else {
                dataDefinitiva = voceXml.getDataFineValidita() != null ? voceXml.getDataFineValidita()
                        : dtDefaultSoppres;
            }

            context.getBusinessObject(StrutTitolariEjb.class).saveDecValVoceTitol(voce, voceXml,
                    orgOperTitol.getDtValOperTitol(), dataDefinitiva);

        }
        context.getBusinessObject(StrutTitolariEjb.class).saveOrgOperVoceTitol(orgOperTitol, voceXml, counterVoci, voce,
                orgOperTitol.getDtValOperTitol());
        return voce;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DecVoceTitol chiudiVoce(Voce voceXml, DecTitol titol, OrgOperTitol orgOperTitol, MutableInt counterVoci) {
        Calendar calSoppres = Calendar.getInstance();
        calSoppres.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        calSoppres.set(Calendar.MILLISECOND, 0);

        DecVoceTitol voce = helper.getVoce(titol.getIdTitol(), voceXml.getCodiceVoceComposito());
        DecValVoceTitol valVoce = helper.getLastDecValVoceTitol(voce.getIdVoceTitol());

        valVoce.setDtFinVal(voceXml.getDataFineValidita().equals(calSoppres.getTime())
                ? orgOperTitol.getDtValOperTitol() : voceXml.getDataFineValidita());

        context.getBusinessObject(StrutTitolariEjb.class).saveOrgOperVoceTitol(orgOperTitol, voceXml, counterVoci, voce,
                (voceXml.getDataFineValidita().equals(calSoppres.getTime()) ? orgOperTitol.getDtValOperTitol()
                        : voceXml.getDataFineValidita()));

        voce.setDtSoppres(voceXml.getDataFineValidita().equals(calSoppres.getTime()) ? orgOperTitol.getDtValOperTitol()
                : voceXml.getDataFineValidita());

        return voce;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveOrgOperVoceTitol(OrgOperTitol orgOperTitol, final Voce voceXml, MutableInt counterVoci,
            DecVoceTitol voce, Date dtValidita) {
        OrgOperVoceTitol operVoce = new OrgOperVoceTitol();
        operVoce.setOrgOperTitol(orgOperTitol);
        operVoce.setTiOperVoceTitol(voceXml.getOperation().name());
        operVoce.setPgOperVoceTitol(new BigDecimal(counterVoci.intValue()));
        operVoce.setDecVoceTitol(voce);
        operVoce.setDtValOperVoceTitol(dtValidita);
        operVoce.setDlNote(voceXml.getNoteVoceTitolario());
        voce.getOrgOperVoceTitols().add(operVoce);
        orgOperTitol.getOrgOperVoceTitols().add(operVoce);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveDecValVoceTitol(DecVoceTitol voce, Voce voceXml, Date dtIniVal, Date dtFinVal) {
        DecValVoceTitol newValVoce = new DecValVoceTitol();
        newValVoce.setDecVoceTitol(voce);
        newValVoce.setDsVoceTitol(StringEscapeUtils.escapeJava(voceXml.getDescrizioneVoce()));
        newValVoce.setDtIniVal(dtIniVal);
        newValVoce.setDtFinVal(dtFinVal);
        newValVoce.setFlUsoClassif(voceXml.getAttivoPerClassificazione().getVal());
        newValVoce.setNiAnniConserv(new BigDecimal(voceXml.getTempoConservazione()));
        newValVoce.setDlNote(voceXml.getNoteVoceTitolario());
        voce.getDecValVoceTitols().add(newValVoce);
    }

    public boolean existChiaveUd(BigDecimal idStrut, String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc, Date dtDocInvio) {
        return helper.existChiaveUd(idStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc, dtDocInvio);
    }

    public boolean existTitolario(BigDecimal idStrut, String nmTitol) {
        return helper.existTitolario(idStrut, nmTitol, null);
    }

    public boolean existTitolario(BigDecimal idStrut, String nmTitol, BigDecimal idTitol) {
        return helper.existTitolario(idStrut, nmTitol, idTitol);
    }

    public boolean existTitolario(BigDecimal idStrut, Date dtIniVal, Date dtFinVal) {
        return helper.existTitolario(idStrut, dtIniVal, dtFinVal);
    }

    public DecVTreeTitolTableBean getDecVociTreeTableBean(BigDecimal idTitol, Date dtValidita, boolean addTitolRoot) {
        DecVTreeTitolTableBean treeTableBean = new DecVTreeTitolTableBean();
        List<DecVTreeTitol> treeList = helper.getVociTree(idTitol, dtValidita);
        DecTitol titolario = helper.findById(DecTitol.class, idTitol);
        if (treeList != null && !treeList.isEmpty()) {
            try {
                if (addTitolRoot) {
                    // La root diventa il titolario, da cui le voci con id padre nullo discenderanno
                    DecVTreeTitolRowBean root = new DecVTreeTitolRowBean();
                    root.setBigDecimal("id_voce_titol", BigDecimal.ZERO);
                    root.setString("nome_composito", titolario.getNmTitol());
                    root.setString("enum_icon", Tree.IconColours.BLACK.name());
                    treeTableBean.add(root);
                }
                for (DecVTreeTitol voce : treeList) {
                    DecVTreeTitolRowBean row = (DecVTreeTitolRowBean) Transform.entity2RowBean(voce);
                    row.setCdCompositoVoceTitol(StringEscapeUtils.unescapeJava(voce.getCdCompositoVoceTitol()));
                    row.setDsVoceTitol(StringEscapeUtils.unescapeJava(voce.getDsVoceTitol()));
                    if (row.getIdVoceTitolPadre() == null) {
                        row.setIdVoceTitolPadre(BigDecimal.ZERO);
                    }
                    row.setString("nome_composito", row.getCdCompositoVoceTitol()
                            + (StringUtils.isNotBlank(voce.getDsVoceTitol()) ? " - " + row.getDsVoceTitol() : ""));
                    String vocePadre = row.getString("cd_composito_voce_padre");
                    row.setString("cd_composito_voce_padre", StringEscapeUtils.unescapeJava(vocePadre));
                    if (voce.getNiFascicVociFiglie().intValue() > 0 || voce.getNiFascic().intValue() > 0) {
                        row.setString("enum_icon", Tree.IconColours.YELLOW.name());
                    } else {
                        row.setString("enum_icon", Tree.IconColours.BLUE.name());
                    }
                    treeTableBean.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei titolari " + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return treeTableBean;
    }

    public DecVTreeTitolTableBean getDecVociTreeTableBean(String nomeTitolario, Map<String, Voce> vociMap) {
        DecVTreeTitolTableBean treeTableBean = new DecVTreeTitolTableBean();
        // La root diventa il titolario, da cui le voci con id padre nullo discenderanno
        DecVTreeTitolRowBean root = new DecVTreeTitolRowBean();
        root.setBigDecimal("id_voce_titol", BigDecimal.ZERO);
        root.setString("nome_composito", nomeTitolario);
        root.setString("enum_icon", Tree.IconColours.BLACK.name());
        treeTableBean.add(root);

        vociMap = sortVociMap(vociMap);

        for (String key : vociMap.keySet()) {
            Voce voce = vociMap.get(key);
            getVoceRicorsiva(voce, treeTableBean, BigDecimal.ZERO);
        }
        return treeTableBean;
    }

    public Map<String, Voce> sortVociMap(Map<String, Voce> vociMap) {
        List<Map.Entry<String, Voce>> list = new LinkedList<Map.Entry<String, Voce>>(vociMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Voce>>() {
            @Override
            public int compare(Map.Entry<String, Voce> m1, Map.Entry<String, Voce> m2) {
                return (m1.getValue()).getNumeroOrdine().compareTo(m2.getValue().getNumeroOrdine());
            }
        });

        Map<String, Voce> result = new LinkedHashMap<String, Voce>();
        for (Map.Entry<String, Voce> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public void getVoceRicorsiva(Voce root, DecVTreeTitolTableBean treeTableBean, BigDecimal idRoot) {
        BigDecimal idVoce = new BigDecimal(treeTableBean.size());

        DecVTreeTitolRowBean row = new DecVTreeTitolRowBean();
        row.setIdVoceTitol(idVoce);
        row.setIdVoceTitolPadre(idRoot);
        row.setString("nome_composito", root.getCodiceVoce()
                + (StringUtils.isNotBlank(root.getDescrizioneVoce()) ? " - " + root.getDescrizioneVoce() : ""));
        row.setString("enum_icon", Tree.IconColours.BLUE.name());

        Timestamp timestamp = new Timestamp(root.getDataFineValidita().getTime());
        row.setDtSoppres(timestamp);
        treeTableBean.add(row);

        if (root.getNumeroFigli() > 0) {
            Map<String, Voce> figli = sortVociMap(root.getFigli());

            for (String key : figli.keySet()) {
                Voce son = figli.get(key);
                getVoceRicorsiva(son, treeTableBean, idVoce);
            }
        }
    }

    public void getVoceRicorsiva(Voce root, List<OperazioneCreaType> operazioni, BigDecimal idRoot)
            throws DatatypeConfigurationException {
        if (root.getNumeroFigli() > 0) {
            OperazioneCreaType operazione = getOperazioneCreaType(root);
            operazioni.add(operazione);

            Map<String, Voce> figli = sortVociMap(root.getFigli());

            for (String key : figli.keySet()) {
                Voce son = figli.get(key);
                getVoceRicorsiva(son, operazioni, idRoot.add(BigDecimal.ONE));
            }
        } else {
            OperazioneCreaType operazione = getOperazioneCreaType(root);
            operazioni.add(operazione);
        }
    }

    private OperazioneCreaType getOperazioneCreaType(Voce root) throws DatatypeConfigurationException {
        OperazioneCreaType operazione = new OperazioneCreaType();
        CreaVoceType creaVoce = new CreaVoceType();
        creaVoce.setCodiceVoceComposito(root.getCodiceVoceComposito());
        creaVoce.setDescrizioneVoce(root.getDescrizioneVoce());
        creaVoce.setAttivoPerClassificazione(
                AttivoPerClassificazioneType.fromValue(root.getAttivoPerClassificazione().name()));
        XMLGregorianCalendar dateInizio = null;
        XMLGregorianCalendar dateFine = null;
        if (root.getDataInizioValidita() != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(root.getDataInizioValidita());
            dateInizio = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        }
        if (root.getDataFineValidita() != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(root.getDataFineValidita());
            dateFine = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        }
        creaVoce.setDataInizioValidita(dateInizio);
        creaVoce.setDataFineValidita(dateFine);
        creaVoce.setNoteVoceTitolario(root.getNoteVoceTitolario());
        creaVoce.setTempoConservazione(new BigInteger(root.getTempoConservazione().toString()));
        creaVoce.setNumeroOrdine(new BigInteger(root.getNumeroOrdine().toString()));
        operazione.setCreaVoce(creaVoce);
        return operazione;
    }

    public BaseRowInterface getDocTrasmRowBean(BigDecimal idTitol, Date date) {
        BaseRow row = new BaseRow();

        OrgOperTitol oper = helper.getOperTitol(idTitol, date);
        if (oper != null) {
            row.setString("cd_registro_doc_invio", oper.getCdRegistroDocInvio());
            row.setBigDecimal("aa_doc_invio", oper.getAaDocInvio());
            row.setString("cd_doc_invio", oper.getCdDocInvio());
            row.setTimestamp("dt_doc_invio",
                    (oper.getDtDocInvio() != null ? new Timestamp(oper.getDtDocInvio().getTime()) : null));
        }
        return row;
    }

    public boolean isTitolarioChiuso(BigDecimal idTitol) {
        boolean result = false;
        OrgOperTitol oper = helper.getOperTitol(idTitol, null);
        if (oper.getTiOperTitol().equals(Voce.Operation.CHIUDI.name())) {
            result = true;
        }
        return result;
    }

    public DecVLisValVoceTitolTableBean getTracciaVociTableBean(BigDecimal idVoceTitol) {
        DecVLisValVoceTitolTableBean table = new DecVLisValVoceTitolTableBean();
        List<DecVLisValVoceTitol> list = helper.getTracciaList(idVoceTitol, null);
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecVLisValVoceTitolTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero della traccia delle voci di classificazione "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }

        return table;
    }

    public List<LivelloType> getLivelliForParsing(BigDecimal idTitol) {
        List<LivelloType> livelliList = new ArrayList<>();
        List<DecLivelloTitol> livelli = helper.getLivelliList(idTitol, null);
        for (DecLivelloTitol livello : livelli) {
            LivelloType tmpLiv = new LivelloType();
            tmpLiv.setCarattereSeparatoreLivello(livello.getCdSepLivello());
            tmpLiv.setNomeLivello(livello.getNmLivelloTitol());
            tmpLiv.setNumeroLivello(new BigInteger(livello.getNiLivello().toString()));
            tmpLiv.setTipoFormatoLivello(TipoFormatoLivelloType.fromValue(livello.getTiFmtVoceTitol()));

            livelliList.add(tmpLiv);
        }
        return livelliList;
    }

    public DecLivelloTitolTableBean getLivelliTableBean(BigDecimal idTitol) {
        DecLivelloTitolTableBean table = new DecLivelloTitolTableBean();
        List<DecLivelloTitol> livelli = helper.getLivelliList(idTitol, null);
        if (livelli != null && !livelli.isEmpty()) {
            try {
                table = (DecLivelloTitolTableBean) Transform.entities2TableBean(livelli);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero della unità documentaria di invio "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return table;
    }

    public Map<String, Voce> getVociMap(BigDecimal idTitol, List<LivelloType> livelli, AttributesTitolario attribs)
            throws ParerUserError {
        Date today = Calendar.getInstance().getTime();
        List<DecVTreeTitol> treeList = helper.getVociTree(idTitol, today);
        Pattern[] regExs = checker.getLivelliRegex(livelli);
        Map<String, Voce> vociMap = new HashMap<>();
        if (treeList != null && !treeList.isEmpty()) {
            for (DecVTreeTitol voce : treeList) {
                String codiceVoce = StringEscapeUtils.unescapeJava(voce.getCdCompositoVoceTitol());
                String descVoce = StringEscapeUtils.unescapeJava(voce.getDsVoceTitol());
                int indexPattern = 0;
                for (Pattern regEx : regExs) {
                    if (regEx.matcher(codiceVoce).matches()) {
                        Voce newVoce = new Voce(codiceVoce, descVoce, voce.getNiOrdVoceTitol().intValue(),
                                voce.getDtIniVal(), voce.getDtFinVal(),
                                Voce.AttivoClass.fromValue(voce.getFlUsoClassif()), voce.getNiAnniConserv().intValue(),
                                voce.getDlNote());
                        checker.addVociToMap(newVoce, livelli, indexPattern, vociMap);
                        if (attribs != null) {
                            LivelloType liv = livelli.get(indexPattern);
                            BaseRow row = new BaseRow();
                            row.setString("cd_composito_voce_titol", codiceVoce);
                            attribs.getLivelliVociMap().get(new BigDecimal(liv.getNumeroLivello())).add(row);
                        }
                        break;
                    }
                    indexPattern++;
                    if (indexPattern == regExs.length) {
                        throw new ParerUserError("Errore inaspettato nel recupero delle voci del titolario");
                    }
                }
            }
        }
        return vociMap;
    }

    public DecVTreeTitolTableBean getVociAllPadri(BigDecimal idTitol) {
        DecVTreeTitolTableBean result = new DecVTreeTitolTableBean();
        Date today = Calendar.getInstance().getTime();
        List<DecVTreeTitol> treeList = helper.getVociAllPadri(idTitol, today);

        if (treeList != null && !treeList.isEmpty()) {
            try {
                for (DecVTreeTitol t : treeList) {
                    BaseRowInterface rowbean = Transform.entity2RowBean(t);
                    rowbean.setString("ds_tot_voce_titol", t.getCdCompositoVoceTitol() + " - " + t.getDsVoceTitol());
                    result.add(rowbean);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero della traccia delle voci di classificazione "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }

        return result;
    }

    public void validaTitolario(BigDecimal idTitol) throws ParerUserError {
        try {
            DecTitol titolario = helper.findById(DecTitol.class, idTitol);
            titolario.setTiStatoTitol(CostantiDB.StatoTitolario.VALIDATO.name());
        } catch (Exception e) {
            logger.error(
                    "Errore inaspettato nella validazione del titolario : " + ExceptionUtils.getRootCauseMessage(e), e);
            throw new ParerUserError("Errore inaspettato nella validazione del titolario");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void chiudiTitolario(BigDecimal idTitolario, Map<String, Voce> vociMap, Date dataFineValidita, String note)
            throws ParerUserError {
        logger.info("Recupero titolario");
        DecTitol titolario = helper.findById(DecTitol.class, idTitolario.longValue());
        OrgStrut strut = titolario.getOrgStrut();

        logger.info("Nuovo orgOperTitol con operazione di CHIUSURA");
        OrgOperTitol orgOperTitol = context.getBusinessObject(StrutTitolariEjb.class)
                .saveOrgOperTitol(Voce.Operation.CHIUDI, strut, null, null, null, null, dataFineValidita, titolario);
        Map<String, DecLivelloTitol> livelliMap = new HashMap<>();
        for (DecLivelloTitol livello : titolario.getDecLivelloTitols()) {
            livelliMap.put(livello.getNmLivelloTitol(), livello);
        }

        MutableInt counterVoci = new MutableInt(1);
        logger.info("Scorro ricorsivamente la mappa di voci per chiudere le voci di primo livello");
        for (Entry<String, Voce> entry : vociMap.entrySet()) {
            final Voce voce = entry.getValue();
            voce.setOperation(Voce.Operation.CHIUDI);
        }
        Set<Voce> vociDaChiudere = new HashSet<>();
        List<String> codiceVociChiuse = new ArrayList<>();
        logger.info("Eseguo la chiusura logica di tutte le voci ricorsivamente");
        context.getBusinessObject(StrutTitolariEjb.class).alterVociRicorsivo(vociMap, livelliMap, null, titolario,
                orgOperTitol, counterVoci, codiceVociChiuse, vociDaChiudere, false);
        logger.info("Eseguo la chiusura su db di tutte le voci");
        for (Voce voce : vociDaChiudere) {
            voce.setOperation(Voce.Operation.CHIUDI);
            context.getBusinessObject(StrutTitolariEjb.class).chiudiVoce(voce, titolario, orgOperTitol, counterVoci);
            counterVoci.increment();
        }

        titolario.setDlNote(note);
        titolario.setDtSoppres(dataFineValidita);
    }

    public AroVRicUnitaDocTableBean getUnitaDocTableBean(String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc, BigDecimal idStrut) {
        AroVRicUnitaDocTableBean table = new AroVRicUnitaDocTableBean();
        AroVRicUnitaDoc unitaDoc = helper.getUnitaDoc(cdRegistroKeyUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc, idStrut);
        if (unitaDoc != null) {
            try {
                AroVRicUnitaDocRowBean row = (AroVRicUnitaDocRowBean) Transform.entity2RowBean(unitaDoc);
                table.add(row);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero della unità documentaria di invio "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return table;
    }

    public AttributesTitolario initAttributesTitolario(BigDecimal idTitol) throws ParerUserError {
        return initAttributesTitolario(idTitol, Calendar.getInstance().getTime());
    }

    public AttributesTitolario initAttributesTitolario(BigDecimal idTitol, Date dataValidita) throws ParerUserError {
        AttributesTitolario attrib = new AttributesTitolario();
        attrib.setLivelliTableBean(getLivelliTableBean(idTitol));
        attrib.setVociMap(getVociMap(idTitol, attrib.getLivelliParsing(), attrib));
        attrib.setVociTableBean(getDecVociTreeTableBean(idTitol, dataValidita, false));
        return attrib;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveLevels(BigDecimal idTitol, BaseTableInterface<?> table) throws ParerUserError {
        Set<BigDecimal> livelliSalvati = new HashSet<BigDecimal>();
        logger.info("Eseguo il salvataggio dei livelli");
        for (BaseRowInterface row : table) {
            BigDecimal idLivello = row.getBigDecimal("id_livello_titol");
            BigDecimal niLivello = row.getBigDecimal("ni_livello");
            String nmLivello = row.getString("nm_livello_titol");
            String tiFormato = row.getString("ti_fmt_voce_titol");
            String separatore = row.getString("cd_sep_livello");

            livelliSalvati.add(niLivello);

            DecLivelloTitol livelloSalvato;
            if (idLivello != null) {
                logger.info("Livello recuperato tramite ID - " + idLivello.toString());
                livelloSalvato = helper.findById(DecLivelloTitol.class, idLivello);
            } else {
                logger.info("Livello recuperato tramite niLivello - " + niLivello.toString());
                livelloSalvato = helper.getLivello(niLivello, idTitol);
            }
            if (livelloSalvato != null) {
                logger.info("Livello esistente - eseguo l'update dei campi");
                // Esiste già un livello con quel numero, eseguo l'update di quello
                livelloSalvato.setCdSepLivello(separatore);
                livelloSalvato.setNmLivelloTitol(nmLivello);
                livelloSalvato.setTiFmtVoceTitol(tiFormato);
            } else {
                logger.info("Livello non esistente - creo un nuovo livello");
                livelloSalvato = new DecLivelloTitol();
                livelloSalvato.setNmLivelloTitol(nmLivello);
                livelloSalvato.setNiLivello(niLivello);
                livelloSalvato.setDecTitol(helper.findById(DecTitol.class, idTitol));
                livelloSalvato.setCdSepLivello(separatore);
                livelloSalvato.setTiFmtVoceTitol(tiFormato);

                helper.insertEntity(livelloSalvato, true);
            }
        }
        // Elimino, se esistono, i livelli eliminati
        logger.info("Elimino, se esistono, i livelli presenti su db ma non nella lista creata dall'utente");
        List<DecLivelloTitol> livelliToDelete = helper.getLivelliList(idTitol, livelliSalvati);
        if (!livelliToDelete.isEmpty()) {
            for (DecLivelloTitol livelloToDelete : livelliToDelete) {
                if (!livelloToDelete.getDecVoceTitols().isEmpty()) {
                    throw new ParerUserError("Errore inaspettato nel salvataggio dei livelli: il livello "
                            + livelloToDelete.getNmLivelloTitol() + " presenta ancora delle voci associate ad esso");
                }
                helper.removeEntity(livelloToDelete, true);
            }
        }
    }

    public CreaTitolario generateXmlObject(DecTitolRowBean titol, Date dtValidita) throws ParerUserError {
        CreaTitolario titolario = new CreaTitolario();
        AttributesTitolario attrs = initAttributesTitolario(titol.getIdTitol(), dtValidita);

        TitolarioType intestazione = new TitolarioType();
        intestazione.setDenominazione(titol.getNmTitol());
        intestazione.setNoteTitolario(titol.getDlNote());
        intestazione.setNumeroLivelliUtilizzati(titol.getNiLivelli().toBigInteger());
        intestazione.setSeparatoreVociTitolarioFascicoli(titol.getCdSepFascicolo());
        titolario.setIntestazione(intestazione);

        Livelli livelli = new CreaTitolario.Livelli();
        livelli.getLivello().addAll(attrs.getLivelliParsing());
        titolario.setLivelli(livelli);

        try {
            List<OperazioneCreaType> vociTypes = generateOperazioniCreaType(attrs);

            ListaOperazioniVoce voci = new CreaTitolario.ListaOperazioniVoce();
            voci.getOperazioneVoce().addAll(vociTypes);
            titolario.setListaOperazioniVoce(voci);
        } catch (DatatypeConfigurationException ex) {
            logger.error("Errore nell'esportazione delle voci: " + ExceptionUtils.getRootCauseMessage(ex));
            throw new ParerUserError("Errore inaspettato nella generazione del file xml");
        }

        return titolario;
    }

    private List<OperazioneCreaType> generateOperazioniCreaType(AttributesTitolario attrs)
            throws DatatypeConfigurationException {
        List<OperazioneCreaType> operazioni = new ArrayList<>();
        Map<String, Voce> vociMap = sortVociMap(attrs.getVociMap());

        for (String key : vociMap.keySet()) {
            Voce voce = vociMap.get(key);
            getVoceRicorsiva(voce, operazioni, BigDecimal.ONE);
        }

        return operazioni;
    }

    public DecTitolTableBean getDecTitolTableBean(BigDecimal idStrut, boolean isFilterValid) {
        DecTitolTableBean tableBean = new DecTitolTableBean();
        List<DecTitol> listaDecTitol = helper.getDecTitol(idStrut, isFilterValid);
        if (listaDecTitol != null && !listaDecTitol.isEmpty()) {
            try {
                tableBean = (DecTitolTableBean) Transform.entities2TableBean(listaDecTitol);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei titolari " + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return tableBean;
    }

    public BaseTable getDecVoceTitolsTableBean(BigDecimal idCriterioRaggrFasc, String tiSel, boolean isFilterValid) {
        BaseTable tableBean = new BaseTable();
        BaseRow tableRow = null;

        List<DecVoceTitol> listaDecVoceTitol = helper.getDecVoceTitols(idCriterioRaggrFasc, tiSel, isFilterValid);
        if (listaDecVoceTitol != null && !listaDecVoceTitol.isEmpty()) {
            for (Iterator<DecVoceTitol> iterator = listaDecVoceTitol.iterator(); iterator.hasNext();) {
                DecVoceTitol ogg = iterator.next();
                tableRow = new BaseRow();
                tableRow.setBigDecimal("id_voce_titol", new BigDecimal(ogg.getIdVoceTitol()));
                tableRow.setString("cd_composito_voce_titol", ogg.getCdCompositoVoceTitol());
                tableRow.setString("nm_titol", ogg.getDecTitol().getNmTitol());
                tableRow.setString("ds_voce_titol", getDsVoceTitols(ogg.getDecValVoceTitols()));

                tableBean.add(tableRow);
            }
        }
        return tableBean;
    }

    public BaseRow getDecVoceTitolRowBean(BigDecimal idVoceTitol) {
        BaseRow rowBean = null;

        DecVoceTitol decVoceTitol = helper.getDecVoceTitol(idVoceTitol);
        if (decVoceTitol != null) {
            rowBean = new BaseRow();
            rowBean.setBigDecimal("id_titol", new BigDecimal(decVoceTitol.getDecTitol().getIdTitol()));
            rowBean.setBigDecimal("id_voce_titol", new BigDecimal(decVoceTitol.getIdVoceTitol()));
            rowBean.setString("cd_composito_voce_titol", decVoceTitol.getCdCompositoVoceTitol());
            rowBean.setString("cd_voce_titol", decVoceTitol.getCdVoceTitol());
            rowBean.setBigDecimal("id_livello_titol",
                    new BigDecimal(decVoceTitol.getDecLivelloTitol().getIdLivelloTitol()));
            rowBean.setBigDecimal("ni_ord_voce_titol", decVoceTitol.getNiOrdVoceTitol());
            rowBean.setTimestamp("dt_istituz", new Timestamp(decVoceTitol.getDtIstituz().getTime()));
            rowBean.setTimestamp("dt_soppres", new Timestamp(decVoceTitol.getDtSoppres().getTime()));
            if (decVoceTitol.getDecVoceTitol() != null)
                rowBean.setBigDecimal("id_voce_titol_padre",
                        new BigDecimal(decVoceTitol.getDecVoceTitol().getIdVoceTitol()));
            rowBean.setString("nm_titol", decVoceTitol.getDecTitol().getNmTitol());
            rowBean.setString("ds_voce_titol", getDsVoceTitols(decVoceTitol.getDecValVoceTitols()));
        }
        return rowBean;
    }

    private String getDsVoceTitols(List<DecValVoceTitol> decValVoceTitols) {
        String val = "";
        if (decValVoceTitols != null && !decValVoceTitols.isEmpty())
            val = (decValVoceTitols.size() > 1) ? "Diversi" : decValVoceTitols.get(0).getDsVoceTitol();

        return val;
    }
}
