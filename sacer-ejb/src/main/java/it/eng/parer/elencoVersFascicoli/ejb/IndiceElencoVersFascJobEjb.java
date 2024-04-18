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

package it.eng.parer.elencoVersFascicoli.ejb;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.elencoVersFascicoli.helper.ElencoVersFascicoliHelper;
import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.ElvElencoVersFascDaElab;
import it.eng.parer.entity.ElvStatoElencoVersFasc;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab.TiStatoElencoFascDaElab;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.entity.constraint.FasStatoFascicoloElenco.TiStatoFascElenco;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.viewEntity.ElvVCreaIxElencoFasc;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.MessaggiWSFormat;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class IndiceElencoVersFascJobEjb {

    Logger log = LoggerFactory.getLogger(IndiceElencoVersFascJobEjb.class);

    @EJB
    private IndiceElencoVersFascXsdEjb indiceEjb;
    @EJB
    private StruttureEjb struttureEjb;
    @EJB
    private ElencoVersFascicoliHelper elencoHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @Resource
    private SessionContext context;

    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");

    public IndiceElencoVersFascJobEjb() {
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void buildIndex(LogJob logJob) throws Exception {
        log.info("Indice Elenco Versamento Fascicoli - Creazione automatica indici elenchi fascicoli...");
        List<OrgStrut> strutture = elencoHelper.retrieveStrutture();
        for (OrgStrut struttura : strutture) {
            manageStrut(struttura.getIdStrut(), logJob.getIdLogJob());
        }
        jobHelper.writeLogJob(JobConstants.JobEnum.CREAZIONE_INDICI_ELENCHI_VERS_FASC.name(),
                ElencoEnums.OpTypeEnum.FINE_SCHEDULAZIONE.name());
    }

    public void manageStrut(long idStruttura, long idLogJob) throws Exception {
        log.debug("Indice Elenco Versamento Fascicoli - manageStrut");
        BigDecimal idStrut = new BigDecimal(idStruttura);
        /*
         * Determino gli elenchi appartenenti alla struttura corrente, con stato DA_CHIUDERE (tabella
         * ELV_ELENCO_VERS_FASC_DA_ELAB)
         */
        List<Long> elenchiDaChiudere = elencoHelper.retrieveIdElenchiDaElaborare(idStrut,
                TiStatoElencoFascDaElab.DA_CHIUDERE);
        log.info("Indice Elenco Versamento Fascicoli - struttura id {}: trovati {} elenchi DA_CHIUDERE da processare",
                idStrut, elenchiDaChiudere.size());

        IndiceElencoVersFascJobEjb indiceElencoVersFascEjbRef1 = context
                .getBusinessObject(IndiceElencoVersFascJobEjb.class);
        for (Long idElenchi : elenchiDaChiudere) {
            indiceElencoVersFascEjbRef1.manageIndexAtomic(idElenchi, idStruttura, idLogJob);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageIndexAtomic(long idElenco, long idStruttura, long idLogJob) throws Exception {
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        ElvElencoVersFasc elenco = elencoHelper.retrieveElencoById(idElenco);
        elencoHelper.lockElenco(elenco);
        manageIndex(elenco, struttura);
    }

    public void manageIndex(ElvElencoVersFasc elenco, OrgStrut struttura) throws Exception {
        OrgEnte ente = struttura.getOrgEnte();
        String nomeStruttura = struttura.getNmStrut();
        String nomeStrutturaNorm = struttura.getCdStrutNormaliz();
        String nomeEnte = ente.getNmEnte();
        String nomeEnteNorm = ente.getCdEnteNormaliz();

        buildIndexFile(elenco, nomeStruttura, nomeStrutturaNorm, nomeEnte, nomeEnteNorm);
        // registro un nuovo stato = CHIUSO e lo lascio nella coda degli elenchi da elaborare assegnando stato = CHIUSO
        ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
        statoElencoVersFasc.setElvElencoVersFasc(elenco);
        statoElencoVersFasc.setTsStato(new Date());
        statoElencoVersFasc.setTiStato(TiStatoElencoFasc.CHIUSO);

        elenco.getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);

        // aggiorno l’elenco da elaborare assegnando stato = CHIUSO
        ElvElencoVersFascDaElab elencoVersFascDaElab = elencoHelper.retrieveElencoInQueue(elenco);
        elencoVersFascDaElab.setTiStato(TiStatoElencoFascDaElab.CHIUSO);
        // il sistema aggiorna l’elenco specificando l’identificatore dello stato corrente
        Long idStatoElencoVersFasc = elencoHelper
                .retrieveStatoElencoByIdElencoVersFascStato(elenco.getIdElencoVersFasc(), TiStatoElencoFasc.CHIUSO)
                .getIdStatoElencoVersFasc();
        elenco.setIdStatoElencoVersFascCor(new BigDecimal(idStatoElencoVersFasc));
        // registro un nuovo stato pari a IN_ELENCO_CHIUSO per ogni fascicolo appartenente all’elenco
        elencoHelper.setStatoFascicoloElenco(elenco, TiStatoFascElenco.IN_ELENCO_CHIUSO);
        // assegno ad ogni fascicolo appartenente all'elenco stato = IN_ELENCO_CHIUSO
        elencoHelper.setFasFascicoliStatus(elenco, TiStatoFascElencoVers.IN_ELENCO_CHIUSO);
    }

    public void buildIndexFile(ElvElencoVersFasc elenco, String nomeStruttura, String nomeStrutturaNorm,
            String nomeEnte, String nomeEnteNorm) throws Exception {
        calcolaUrnElenco(elenco, nomeStruttura, nomeStrutturaNorm, nomeEnte, nomeEnteNorm);
        byte[] indexFile = null;
        // creo il file .xml
        log.info(
                "Indice Elenco Versamento Fascicoli - creazione indice per elenco id '{}' appartenente alla struttura '{}'",
                elenco.getIdElencoVersFasc(), elenco.getOrgStrut().getIdStrut());
        indexFile = indiceEjb.createIndex(elenco, false);
        // calcolo l'hash SHA-256 del file .xml
        String hashXmlIndice = new HashCalculator().calculateHashSHAX(indexFile, TipiHash.SHA_256).toHexBinary();
        // costruisco l'urn dell'indice
        ElvVCreaIxElencoFasc creaIxElencoFasc = elencoHelper.findViewById(ElvVCreaIxElencoFasc.class,
                BigDecimal.valueOf(elenco.getIdElencoVersFasc()));
        // URN originale
        String urnXmlIndice = creaIxElencoFasc.getDsUrnIndiceElenco();
        // URN normalizzato
        String urnXmlIndiceNormaliz = creaIxElencoFasc.getDsUrnIndiceElencoNormaliz();
        // Registro il file Indice.xml (in ELV_FILE_ELENCO_VERS_FASC)
        // definendo l'hash dell'indice, l'algoritmo usato per il calcolo hash (=SHA-256),
        // l'encoding del hash (=hexBinary), la versione del XSD (=1.0) con cui è creato l'indice dell'elenco e
        // l'urn dell’indice “urn:<sistemaconservazione>:<ente>:<struttura>:ElencoVers-FA-<id elenco>:Indice”
        elencoHelper.storeFileIntoElenco(elenco, indexFile, ElencoEnums.FileTypeEnum.INDICE_ELENCO.name(), new Date(),
                hashXmlIndice, TipiHash.SHA_256.descrivi(), TipiEncBinari.HEX_BINARY.descrivi(), urnXmlIndice,
                urnXmlIndiceNormaliz, ElencoEnums.ElencoInfo.VERSIONE_ELENCO.message());
    }

    public void calcolaUrnElenco(ElvElencoVersFasc elenco, String nomeStruttura, String nomeStrutturaNorm,
            String nomeEnte, String nomeEnteNorm) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String nomeSistema = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        // Calcola URN originale
        String urnOriginale = MessaggiWSFormat.formattaUrnElencoVersFascicoli(nomeSistema, nomeEnte, nomeStruttura,
                sdf.format(elenco.getTsCreazioneElenco()), String.format("%d", elenco.getIdElencoVersFasc()));
        elenco.setDsUrnElenco(urnOriginale);
        // Calcola URN normalizzato
        String urnNormalizzato = MessaggiWSFormat.formattaUrnElencoVersFascicoli(nomeSistema, nomeEnteNorm,
                nomeStrutturaNorm, sdf.format(elenco.getTsCreazioneElenco()),
                String.format("%d", elenco.getIdElencoVersFasc()));
        elenco.setDsUrnNormalizElenco(urnNormalizzato);
    }

}
