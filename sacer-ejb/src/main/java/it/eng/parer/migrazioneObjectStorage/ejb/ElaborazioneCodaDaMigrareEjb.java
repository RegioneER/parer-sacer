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

package it.eng.parer.migrazioneObjectStorage.ejb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.blob.info.BlobInfo;
import it.eng.parer.blob.info.ChiaveDiRecupero;
import it.eng.parer.blob.info.PayLoad;
import it.eng.parer.blob.info.Urn;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompHashCalc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.OstMigrazFile;
import it.eng.parer.entity.OstMigrazFileErr;
import it.eng.parer.entity.OstMigrazStrutMese;
import it.eng.parer.entity.OstMigrazSubPart;
import it.eng.parer.entity.OstNoMigrazFile;
import it.eng.parer.entity.OstStatoMigrazSubPart;
import it.eng.parer.entity.constraint.OrgPartition;
import it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato;
import it.eng.parer.migrazioneObjectStorage.helper.ConsumerCodaHelper;
import it.eng.parer.migrazioneObjectStorage.helper.VerificaMigrazioneSubPartizioniHelper;
import it.eng.parer.migrazioneObjectStorage.utils.MsgUtil;
import it.eng.parer.util.Utils;
import it.eng.parer.util.ejb.JmsProducerUtilEjb;
import it.eng.parer.util.helper.UniformResourceNameUtilHelper;
import it.eng.parer.viewEntity.OstVLisFileBlobBystrumese;
import it.eng.parer.viewEntity.OstVLisStrutMmBlob;
import it.eng.parer.viewEntity.OstVLisSubpartBlobByIstz;
import it.eng.parer.viewEntity.OstVPayloadMigrazFileBlob;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.CostantiDB.TipoCausaleNoMigraz;

/**
 *
 * @author Iacolucci_M
 */
@Stateless(mappedName = "ElaborazioneCodaDaMigrareEjb")
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class ElaborazioneCodaDaMigrareEjb {

    private final Logger log = LoggerFactory.getLogger(ElaborazioneCodaDaMigrareEjb.class);

    private static final String ARO_COMP_DOC = "ARO_COMP_DOC";
    private static final String OST_001 = "OST-001";

    @Resource(mappedName = "jms/ProducerConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/queue/OggettiDaMigrareQueue")
    private Queue queue;
    @EJB
    private VerificaMigrazioneSubPartizioniHelper verificaMigrazioneSubPartizioneHelper;
    @EJB
    private ElaborazioneCodaDaMigrareEjb me;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private UnitaDocumentarieHelper unitaDocumentarieHelper;
    @EJB
    private JmsProducerUtilEjb jmsProducerUtilEjb;
    @EJB
    private ConsumerCodaHelper ccHelper;
    @EJB
    private UniformResourceNameUtilHelper calcoloURNHelper;

    public void completaSubpartizioniBlob(int numeroJob) {
        List<OstMigrazSubPart> lista = verificaMigrazioneSubPartizioneHelper
                .getOstMigrazSubPartPerPartizStatoCorrList(OrgPartition.TiPartition.BLOB.name(),
                        it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.FILE_DA_SCARICARE
                                .name(),
                        numeroJob);
        log.info("COMPLETA_SUBPARTIZIONI_BLOB elementi estratti[" + lista.size() + "]");
        for (OstMigrazSubPart ostMigrazSubPart : lista) {
            aggiungiStrutturaMeseASubPartizioneBlob(ostMigrazSubPart);
            me.aggiornaStatoSubPartizione(ostMigrazSubPart);
        }
    }

    public void aggiungiSubpartizioniBlob(int numeroJob) {
        List<OstVLisSubpartBlobByIstz> lista = verificaMigrazioneSubPartizioneHelper
                .getSubPartitionByMesiAntecedenti(numeroJob);
        log.info("AGGIUNGI_SUBPARTIZIONI_BLOB elementi estratti[" + lista.size() + "]");
        int numMaxFileDaElab = Integer.parseInt(configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NUM_MAX_FILE_DA_ELAB));
        // Per ogni subpartizione determinata fa i vari conteggi per determinare se aggiungere
        // OstMigrazSubPart...
        for (OstVLisSubpartBlobByIstz ostVLisSubpartBlobByIstz : lista) {
            List<String> alStati = new ArrayList<>();
            alStati.add(
                    it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.DA_MIGRARE.name());
            alStati.add(it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_IN_CORSO
                    .name());
            alStati.add(
                    it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_IN_ERRORE
                            .name());
            List<Object[]> listaContatori = verificaMigrazioneSubPartizioneHelper
                    .getSommeFileMigrazConStati(alStati);
            long numeroTotale = 0;
            if (listaContatori != null && !listaContatori.isEmpty()) {
                Object[] arr = listaContatori.get(0);
                if (arr != null && arr[0] != null && arr[1] != null && arr[2] != null) {
                    numeroTotale = ((BigDecimal) arr[0]).longValueExact()
                            + ((BigDecimal) arr[1]).longValueExact()
                            + ((BigDecimal) arr[2]).longValueExact();
                }
            }
            if (numeroTotale < numMaxFileDaElab) {
                long idMigrazSubPart = me
                        .registraStatoMigrazSubPartizione(ostVLisSubpartBlobByIstz);
                OstMigrazSubPart ostMigrazSubPart = verificaMigrazioneSubPartizioneHelper
                        .findById(OstMigrazSubPart.class, idMigrazSubPart);
                aggiungiStrutturaMeseASubPartizioneBlob(ostMigrazSubPart);
                me.aggiornaStatoSubPartizione(ostMigrazSubPart);
            }
        }
    }

    public ContatoriPerMigrazioni aggiungiInCodaDaMigrare(int numeroJob) {
        long numMaxMessaggiInCodaDaMigrare = Long
                .parseLong(configurationHelper.getValoreParamApplicByApplic(
                        CostantiDB.ParametroAppl.NUM_MAX_MESSAGGI_CODA_DA_MIGRARE));
        long numFileDaMigrare = Long.parseLong(configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NUM_FILE_DA_MIGRARE));
        ContatoriPerMigrazioni totalizzatore = new ContatoriPerMigrazioni();
        long totFileConStatoMigrazioneInCorso = verificaMigrazioneSubPartizioneHelper
                .getCountFileAllPartitionWithState(
                        it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_CORSO
                                .name());

        if (totFileConStatoMigrazioneInCorso < numMaxMessaggiInCodaDaMigrare) {
            List<String> al = new ArrayList<>();
            al.add(it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.DA_MIGRARE.name());
            al.add(it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_IN_CORSO
                    .name());
            al.add(it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_IN_ERRORE
                    .name());
            List<OstMigrazSubPart> lista = verificaMigrazioneSubPartizioneHelper
                    .getOstMigrazSubPartListByStateOrdered(al, numeroJob);
            // se risultato non è vuoto...
            if (lista.size() > 0) {
                // determino il minimo tra i numeri file da migrare...
                long diff = numMaxMessaggiInCodaDaMigrare - totFileConStatoMigrazioneInCorso;
                if (numFileDaMigrare < diff) {
                    totalizzatore.setFileDaMigrareDefinitivo(numFileDaMigrare);
                } else {
                    totalizzatore.setFileDaMigrareDefinitivo(diff);
                }
                // Per ogni sub partizione...
                for (OstMigrazSubPart ostMigrazSubPart : lista) {
                    totalizzatore.setNumFileSelezionatiPerLaPartizione(0);
                    if (totalizzatore.getFileDaMigrareDefinitivo() > 0) {
                        List<OstMigrazFile> listaFile = verificaMigrazioneSubPartizioneHelper
                                .getOstMigrazFileByStateAndSubPartitionWithLimit(
                                        it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.DA_MIGRARE
                                                .name(),
                                        ostMigrazSubPart,
                                        totalizzatore.getFileDaMigrareDefinitivo());
                        // per ogni file da migrare...
                        for (OstMigrazFile ostMigrazFile : listaFile) {
                            // determino ARO_COMP_HASH_CALC (more than one -> error)
                            AroCompHashCalc compHashCalc = ccHelper
                                    .getAroCompHashCalcByIdOggetto(ostMigrazFile.getIdOggetto());
                            me.aggiungiInCodaDaMigrareInNewTransaction(ostMigrazFile, totalizzatore,
                                    compHashCalc);
                        }
                        if (totalizzatore.getNumFileSelezionatiPerLaPartizione() > 0) {
                            totalizzatore.setIsAddedInQueue(true);
                        }
                        totalizzatore.setFileDaMigrareDefinitivo(
                                totalizzatore.getFileDaMigrareDefinitivo()
                                        - totalizzatore.getNumFileSelezionatiPerLaPartizione());
                    }
                }
            } else {
                totalizzatore.setIsNotFileToMigrate(true);
            }
        } else {
            totalizzatore.setIsCodaPiena(true);
        }
        return totalizzatore;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiInCodaDaMigrareInNewTransaction(OstMigrazFile ostMigrazFilePar,
            ContatoriPerMigrazioni totalizzatore, AroCompHashCalc compHashCalc) {
        OstMigrazSubPart ostMigrazSubPart = verificaMigrazioneSubPartizioneHelper.findByIdWithLock(
                OstMigrazSubPart.class,
                ostMigrazFilePar.getOstMigrazSubPart().getIdMigrazSubPart());
        OstMigrazFile ostMigrazFile = verificaMigrazioneSubPartizioneHelper
                .findByIdWithLock(OstMigrazFile.class, ostMigrazFilePar.getIdMigrazFile());
        totalizzatore.incrementaNumFileSelezionatiPerLaPartizione();
        // Nuove aggiunte MEV#16016
        if (totalizzatore.getNumFileSelezionatiPerLaPartizione() == 1) {
            OstStatoMigrazSubPart ostStatoMigrazSubPart = verificaMigrazioneSubPartizioneHelper
                    .findById(OstStatoMigrazSubPart.class,
                            ostMigrazSubPart.getIdStatoMigrazSubPartCor());
            if (ostStatoMigrazSubPart != null && ostStatoMigrazSubPart.getTiStato()
                    .equals(it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.DA_MIGRARE
                            .name())) {
                OstStatoMigrazSubPart newOstStatoMigrazSubPart = new OstStatoMigrazSubPart();
                ostMigrazSubPart.addOstStatoMigrazSubPart(newOstStatoMigrazSubPart);
                newOstStatoMigrazSubPart.setTsRegStato(new Date());
                newOstStatoMigrazSubPart.setTiStato(
                        it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_IN_CORSO
                                .name());
                verificaMigrazioneSubPartizioneHelper.getEntityManager()
                        .persist(newOstStatoMigrazSubPart);
                verificaMigrazioneSubPartizioneHelper.getEntityManager().flush();
                ostMigrazSubPart.setIdStatoMigrazSubPartCor(
                        new BigDecimal(newOstStatoMigrazSubPart.getIdStatoMigrazSubPart()));
            }
        }
        // Fine nuove modifiche MEV#16016
        ostMigrazFile.setTiStatoCor(
                it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_CORSO.name());
        ostMigrazFile.setTsRegStatoCor(new Date());
        ostMigrazSubPart.setNiFileMigrazInCorso(
                new BigDecimal(ostMigrazSubPart.getNiFileMigrazInCorso().longValueExact() + 1));
        ostMigrazSubPart.setNiFileDaMigrare(
                new BigDecimal(ostMigrazSubPart.getNiFileDaMigrare().longValueExact() - 1));
        PayLoad pl = new PayLoad();
        BlobInfo blobInfo = new BlobInfo();
        pl.setBlobInfo(blobInfo);
        ChiaveDiRecupero chiaveDiRecupero = new ChiaveDiRecupero();
        OstVPayloadMigrazFileBlob ostVPayloadMigrazFileBlob = verificaMigrazioneSubPartizioneHelper
                .findViewById(OstVPayloadMigrazFileBlob.class, ostMigrazFile.getIdOggetto());
        chiaveDiRecupero.setIdBlob(ostMigrazFile.getIdOggetto().longValueExact());
        chiaveDiRecupero.setNomeColonnaBlob(ostMigrazSubPart.getNmColonnaBlobFile());
        chiaveDiRecupero.setNomeColonnaId(ostMigrazSubPart.getNmColonnaIdFile());
        chiaveDiRecupero.setNomeTabella(ostMigrazSubPart.getNmTabellaFile());
        blobInfo.setChiave(chiaveDiRecupero);
        blobInfo.setTipoHash(compHashCalc.getDsAlgoHashFile());
        blobInfo.setHash(compHashCalc.getDsHashFile());
        blobInfo.setHashEncoding(compHashCalc.getCdEncodingHashFile());
        blobInfo.setTenant(ostMigrazFile.getNmTenant());
        blobInfo.setBucket(ostMigrazFile.getNmBucket());
        blobInfo.setKey(ostMigrazFile.getCdKeyFile());
        if (ostVPayloadMigrazFileBlob.getNiSizeFileCalc() != null) {
            blobInfo.setDimensione(ostVPayloadMigrazFileBlob.getNiSizeFileCalc().longValueExact());
        }
        blobInfo.setTimeStamp((new Date().getTime()));
        blobInfo.setTipoHashDaCalcolare(
                compHashCalc.getDsAlgoHashFile().equals(TipiHash.SHA_1.descrivi())
                        ? TipiHash.SHA_256.descrivi()
                        : null);
        blobInfo.setDataVersamento(ostVPayloadMigrazFileBlob.getDtCreazione().getTime());
        Urn urn = new Urn();
        urn.setNormalizzato(ostVPayloadMigrazFileBlob.getDsUrnNormaliz());
        urn.setOriginale(ostVPayloadMigrazFileBlob.getDsUrnOriginale());
        // MAC #24114, può essere null
        urn.setIniziale(ostVPayloadMigrazFileBlob.getDsUrnIniziale());
        blobInfo.setUrn(urn);
        jmsProducerUtilEjb.inviaMessaggioInFormatoJson(connectionFactory, queue, pl,
                "CodaOggettiDaMigrare");
    }

    private void aggiungiStrutturaMeseASubPartizioneBlob(OstMigrazSubPart ostMigrazSubPart) {
        List<OstMigrazStrutMese> listaMese = verificaMigrazioneSubPartizioneHelper
                .getOstMigrazStrutMeseByOrgSubPartitionFlag(ostMigrazSubPart, false);
        for (OstMigrazStrutMese ostMigrazStrutMese : listaMese) {
            me.aggiungiStrutturaMeseASubPartizione(ostMigrazStrutMese);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStrutturaMeseASubPartizione(OstMigrazStrutMese ostMigrazStrutMese) {
        OstMigrazStrutMese orgMese = verificaMigrazioneSubPartizioneHelper.findByIdWithLock(
                OstMigrazStrutMese.class, ostMigrazStrutMese.getIdMigrazStrutMese());
        aggiungiFileASubPartizioneBlob(ostMigrazStrutMese);
        orgMese.setFlFileAggiunti("1");
    }

    private void aggiungiFileASubPartizioneBlob(OstMigrazStrutMese ostMigrazStrutMese) {
        // determina nome ente e struttura normalizzati e non
        OrgStrut orgStrut = verificaMigrazioneSubPartizioneHelper.findById(OrgStrut.class,
                ostMigrazStrutMese.getIdStrut());
        OrgEnte orgEnte = orgStrut.getOrgEnte();
        OrgAmbiente orgAmbiente = orgEnte.getOrgAmbiente();
        String nomeStruttura = orgStrut.getNmStrut();
        String nomeStrutturaNorm = orgStrut.getCdStrutNormaliz();
        String nomeEnte = orgEnte.getNmEnte();
        String nomeEnteNorm = orgEnte.getCdEnteNormaliz();
        String nomeAmbiente = orgAmbiente.getNmAmbiente();
        // usa prima gli id perché potrebbero esserci centinaia di migliaia di oggetti!
        List<BigDecimal> lista = verificaMigrazioneSubPartizioneHelper
                .getOstVLisFileBlobIdByStrutMeseBetweenDate(ostMigrazStrutMese.getIdStrut(),
                        ostMigrazStrutMese.getDtVersIni(), ostMigrazStrutMese.getDtVersFine());
        log.debug("COMPONENTI ESTRATTI [" + lista.size() + "}");
        for (BigDecimal idComp : lista) {
            me.gestisciComponentePerAggiungiFileASubPartizione(idComp,
                    ostMigrazStrutMese.getOstMigrazSubPart(), nomeStruttura, nomeStrutturaNorm,
                    nomeEnte, nomeEnteNorm, nomeAmbiente);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestisciComponentePerAggiungiFileASubPartizione(BigDecimal idComp,
            OstMigrazSubPart ostMigrazSubPartPar, String nomeStruttura, String nomeStrutturaNorm,
            String nomeEnte, String nomeEnteNorm, String nomeAmbiente) {
        OstVLisFileBlobBystrumese ostVLisFileBlobBystrumese = verificaMigrazioneSubPartizioneHelper
                .findViewById(OstVLisFileBlobBystrumese.class, idComp);
        AroUnitaDoc ud = verificaMigrazioneSubPartizioneHelper.findByIdWithLock(AroUnitaDoc.class,
                ostVLisFileBlobBystrumese.getIdUnitaDoc());
        AroCompDoc comp = verificaMigrazioneSubPartizioneHelper.findByIdWithLock(AroCompDoc.class,
                ostVLisFileBlobBystrumese.getIdCompDoc());
        OstMigrazSubPart ostMigrazSubPart = verificaMigrazioneSubPartizioneHelper
                .findByIdWithLock(OstMigrazSubPart.class, ostMigrazSubPartPar.getIdMigrazSubPart());
        // calcola numero normalizzato
        calcolaNumeroNormalizzato(ud);

        String codErr = controllaNumeroNormalizzato(ud);

        String nomeSistemaConservazione = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);

        // Se non ci sono errori...
        if (codErr == null) {
            // MAC #24114, utilizzo il metodo standard per calcolare l'URN
            CSVersatore versatore = new CSVersatore();
            versatore.setAmbiente(nomeAmbiente);
            versatore.setEnte(nomeEnte);
            versatore.setStruttura(nomeStruttura);
            versatore.setSistemaConservazione(nomeSistemaConservazione);

            CSChiave chiave = new CSChiave();
            chiave.setAnno(ud.getAaKeyUnitaDoc().longValue());
            chiave.setNumero(ud.getCdKeyUnitaDoc());
            chiave.setTipoRegistro(ud.getCdRegistroKeyUnitaDoc());

            calcoloURNHelper.scriviUrnCompPreg(ud, versatore, chiave);

            popolaAroCompHashCalc(comp);

            // se componente ha tipo supporto = FILE e salvataggio BLOB...
            if (comp.getTiSupportoComp()
                    .equals(it.eng.parer.entity.constraint.AroCompDoc.TiSupportoComp.FILE.name())
                    && ud.getDecTipoUnitaDoc().getTiSaveFile()
                            .equals(CostantiDB.TipoSalvataggioFile.BLOB.name())) {
                registraFileDaMigrare(ostMigrazSubPart.getIdMigrazSubPart(), comp.getIdCompDoc(),
                        nomeEnteNorm, nomeStrutturaNorm,
                        ostVLisFileBlobBystrumese.getCdRegistroNormaliz(),
                        ostVLisFileBlobBystrumese.getAaKeyUnitaDoc(),
                        ostVLisFileBlobBystrumese.getIdStrut(),
                        ostVLisFileBlobBystrumese.getDtCreazione());
            } // MEV#17324: Altrimenti qui dovrebbe esserci il punto in cui registriamo il file da
              // non migrare
            else {
                // Determina la causale della non migrazione (punto 3.1.2.1.1.6 registra file da non
                // migrare)
                String tiCausaleNoMigraz = null;
                if (!comp.getTiSupportoComp().equals(
                        it.eng.parer.entity.constraint.AroCompDoc.TiSupportoComp.FILE.name())) {
                    tiCausaleNoMigraz = TipoCausaleNoMigraz.SUPPORTO_NON_FILE.name();
                } else if (ud.getDecTipoUnitaDoc().getTiSaveFile()
                        .equals(CostantiDB.TipoSalvataggioFile.FILE.name())) {
                    tiCausaleNoMigraz = TipoCausaleNoMigraz.FILE_NON_BLOB.name();
                }
                registraFileDaNonMigrare(ostVLisFileBlobBystrumese, ostMigrazSubPart,
                        tiCausaleNoMigraz);
            }
        } else {
            registraFileInErrore(ostMigrazSubPart.getIdMigrazSubPart(), comp.getIdCompDoc(),
                    nomeEnteNorm, nomeStrutturaNorm,
                    ostVLisFileBlobBystrumese.getCdRegistroNormaliz(),
                    ostVLisFileBlobBystrumese.getAaKeyUnitaDoc(),
                    ostVLisFileBlobBystrumese.getIdStrut(),
                    ostVLisFileBlobBystrumese.getDtCreazione());
        }
    }

    private void calcolaNumeroNormalizzato(AroUnitaDoc ud) {
        if (ud.getCdKeyUnitaDocNormaliz() == null) {
            String numeroNormalizzato = Utils.getNormalizedUDCode(ud.getCdKeyUnitaDoc());
            ud.setCdKeyUnitaDocNormaliz(numeroNormalizzato);
        }
    }

    private String controllaNumeroNormalizzato(AroUnitaDoc ud) {
        String codErrore = null;
        if (unitaDocumentarieHelper.existsUdWithSameNormalizedNumber(
                ud.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(), ud.getAaKeyUnitaDoc(),
                ud.getCdKeyUnitaDocNormaliz(), ud.getCdKeyUnitaDoc())) {
            codErrore = OST_001;
        }
        return codErrore;
    }

    private void popolaAroCompHashCalc(AroCompDoc aroCompDoc) {
        // Registro in ARO_COMP_HASH_CALC se l'hash per l'algoritmo non è già definito
        String hashFileCalc = aroCompDoc.getDsHashFileCalc();
        if (hashFileCalc != null && aroCompDoc.getDsAlgoHashFileCalc() != null
                && aroCompDoc.getCdEncodingHashFileCalc() != null) {
            AroCompHashCalc compHashCalc = ccHelper.getAroCompHashCalc(aroCompDoc.getIdCompDoc(),
                    aroCompDoc.getDsAlgoHashFileCalc());
            if (compHashCalc == null) {
                AroCompHashCalc aroCompHashCalc = new AroCompHashCalc();
                aroCompHashCalc.setAroCompDoc(aroCompDoc);
                aroCompHashCalc.setDsAlgoHashFile(aroCompDoc.getDsAlgoHashFileCalc());
                aroCompHashCalc.setDsHashFile(hashFileCalc);
                aroCompHashCalc.setCdEncodingHashFile(aroCompDoc.getCdEncodingHashFileCalc());
                verificaMigrazioneSubPartizioneHelper.getEntityManager().persist(aroCompHashCalc);
            }
        } else {
            log.warn(
                    "Attenzione, impossibile compilare AroCompHashCalc durante la preparazione della migrazione per il componente con id_comp_doc pari a "
                            + aroCompDoc.getIdCompDoc());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiornaStatoSubPartizione(OstMigrazSubPart ostMigrazSubPartPar) {
        Enum<TiStato> nuovoStatoSubPartizione = null;
        OstMigrazSubPart ostMigrazSubPart = verificaMigrazioneSubPartizioneHelper
                .findByIdWithLock(OstMigrazSubPart.class, ostMigrazSubPartPar.getIdMigrazSubPart());
        if (ostMigrazSubPart.getNiFileErroreNormaliz().longValueExact() == 0) {
            if (ostMigrazSubPart.getNiFileDaMigrare().longValueExact() != 0) {
                nuovoStatoSubPartizione = it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.DA_MIGRARE;
            } else {
                nuovoStatoSubPartizione = it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.DA_NON_MIGRARE;
            }
        } else {
            nuovoStatoSubPartizione = it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.ERRORE_NORMALIZ;
        }
        OstStatoMigrazSubPart stato = new OstStatoMigrazSubPart();
        stato.setOstMigrazSubPart(ostMigrazSubPart);
        stato.setTsRegStato(new Date());
        stato.setTiStato(nuovoStatoSubPartizione.name());
        verificaMigrazioneSubPartizioneHelper.getEntityManager().persist(stato);
        verificaMigrazioneSubPartizioneHelper.getEntityManager().flush();
        ostMigrazSubPart
                .setIdStatoMigrazSubPartCor(BigDecimal.valueOf(stato.getIdStatoMigrazSubPart()));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long registraStatoMigrazSubPartizione(OstVLisSubpartBlobByIstz ostVLisSubpartBlob) {
        OstMigrazSubPart mig = verificaMigrazioneSubPartizioneHelper
                .findOstMigrazSubPartByOrgSubPartitionWithLock(
                        ostVLisSubpartBlob.getIdSubPartition());
        // Registra lo stato della migrazione della subpartizione
        OstStatoMigrazSubPart stato = new OstStatoMigrazSubPart();
        stato.setOstMigrazSubPart(mig);
        stato.setTsRegStato(new Date());
        stato.setTiStato(
                it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.FILE_DA_SCARICARE
                        .name());
        verificaMigrazioneSubPartizioneHelper.getEntityManager().persist(stato);
        verificaMigrazioneSubPartizioneHelper.getEntityManager().flush();
        mig.setIdStatoMigrazSubPartCor(new BigDecimal(stato.getIdStatoMigrazSubPart()));

        // Determina la lista delle coppie struttura, mese...
        List<OstVLisStrutMmBlob> lista = verificaMigrazioneSubPartizioneHelper
                .getStrutturaMesePerSubPartizione(ostVLisSubpartBlob.getIdSubPartition());
        for (OstVLisStrutMmBlob ostVLisStrutMmBlob : lista) {
            OstMigrazStrutMese mese = new OstMigrazStrutMese();
            mese.setOstMigrazSubPart(mig);
            mese.setDtVersIni(ostVLisStrutMmBlob.getDtVersIni());
            mese.setDtVersFine(ostVLisStrutMmBlob.getDtVersFine());
            mese.setMmVers(ostVLisStrutMmBlob.getOstVLisStrutMmBlobId().getMmVers());
            mese.setIdStrut(ostVLisStrutMmBlob.getOstVLisStrutMmBlobId().getIdStrut());
            mese.setFlFileAggiunti(ostVLisStrutMmBlob.getFlFileAggiunti());
            verificaMigrazioneSubPartizioneHelper.getEntityManager().persist(mese);
        }
        return mig.getIdMigrazSubPart();
    }

    /*
     * Classe di comodo per memorizzare contatori relativi alle migrazioni
     */
    public class ContatoriPerMigrazioni {

        private long numFileSelezionatiPerLaPartizione;
        private long fileDaMigrareDefinitivo;
        private boolean isCodaPiena;
        private boolean isAddedInQueue;
        private boolean isNotFileToMigrate;

        public long getNumFileSelezionatiPerLaPartizione() {
            return numFileSelezionatiPerLaPartizione;
        }

        public void setNumFileSelezionatiPerLaPartizione(long numFileSelezionatiPerLaPartizione) {
            this.numFileSelezionatiPerLaPartizione = numFileSelezionatiPerLaPartizione;
        }

        public void incrementaNumFileSelezionatiPerLaPartizione() {
            numFileSelezionatiPerLaPartizione++;
        }

        public long getFileDaMigrareDefinitivo() {
            return fileDaMigrareDefinitivo;
        }

        public void setFileDaMigrareDefinitivo(long fileDaMigrareDefinitivo) {
            this.fileDaMigrareDefinitivo = fileDaMigrareDefinitivo;
        }

        public boolean isIsCodaPiena() {
            return isCodaPiena;
        }

        public void setIsCodaPiena(boolean isCodaPiena) {
            this.isCodaPiena = isCodaPiena;
        }

        public boolean isIsAddedInQueue() {
            return isAddedInQueue;
        }

        public void setIsAddedInQueue(boolean isAddedInQueue) {
            this.isAddedInQueue = isAddedInQueue;
        }

        public boolean isIsNotFileToMigrate() {
            return isNotFileToMigrate;
        }

        public void setIsNotFileToMigrate(boolean isNotFileToMigrate) {
            this.isNotFileToMigrate = isNotFileToMigrate;
        }

    }

    private void registraFileDaMigrare(long idMigrazSubPart, long idCompDoc, String nmEnteNorm,
            String nmStrutNorm, String cdRegistroNorm, BigDecimal anno, BigDecimal idStrut,
            Date dtCreazione) {
        OstMigrazSubPart migrazSubPart = verificaMigrazioneSubPartizioneHelper
                .findById(OstMigrazSubPart.class, idMigrazSubPart);
        AroCompDoc compDoc = verificaMigrazioneSubPartizioneHelper.findById(AroCompDoc.class,
                idCompDoc);
        migrazSubPart.setNiFileDaMigrare(migrazSubPart.getNiFileDaMigrare().add(BigDecimal.ONE));
        BigDecimal byteSizeMigraz = migrazSubPart.getNiByteSize();
        if (byteSizeMigraz == null) {
            byteSizeMigraz = new BigDecimal(0);
        }
        BigDecimal byteSizeComp = compDoc.getNiSizeFileCalc();
        if (byteSizeComp == null) {
            byteSizeComp = new BigDecimal(0);
        }
        migrazSubPart.setNiByteSize(byteSizeMigraz.add(byteSizeComp));
        OstMigrazFile migrazFile = new OstMigrazFile();
        migrazFile.setOstMigrazSubPart(migrazSubPart);
        migrazFile.setNmTabellaIdOggetto(ARO_COMP_DOC);
        migrazFile.setIdOggetto(BigDecimal.valueOf(idCompDoc));
        migrazFile.setTiStatoCor(
                it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.DA_MIGRARE.name());
        migrazFile.setTsRegStatoCor(new Date());
        String nmTenant = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.TENANT_OBJECT_STORAGE);
        migrazFile.setNmTenant(nmTenant);
        String nmBucket = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.BUCKET_OBJECT_STORAGE_COMP);
        migrazFile.setNmBucket(nmBucket);
        String cdKeyFile = nmTenant + "/" + nmEnteNorm + "/" + nmStrutNorm + "/" + cdRegistroNorm
                + "/" + anno + "/" + idCompDoc;
        migrazFile.setCdKeyFile(cdKeyFile);
        migrazFile.setNiMigrazErr(BigDecimal.ZERO);
        migrazFile.setIdStrut(idStrut);
        migrazFile.setMmFile(Utils.getNumericAnnoMeseFromDate(dtCreazione));
        verificaMigrazioneSubPartizioneHelper.insertEntity(migrazFile, true);
    }

    /*
     * MEV#17324 - DivisioneJobMigrazione
     */
    private void registraFileDaNonMigrare(OstVLisFileBlobBystrumese ostVLisFileBlobBystrumese,
            OstMigrazSubPart ostMigrazSubPart, String tiCausaleNoMigraz) {
        OstNoMigrazFile noMigraz = new OstNoMigrazFile();
        noMigraz.setIdOggetto(ostVLisFileBlobBystrumese.getIdCompDoc());
        noMigraz.setIdStrut(ostVLisFileBlobBystrumese.getIdStrut());
        noMigraz.setMmFile(
                Utils.getNumericAnnoMeseFromDate(ostVLisFileBlobBystrumese.getDtCreazione()));
        noMigraz.setNmTabellaIdOggetto(ARO_COMP_DOC);
        noMigraz.setOstMigrazSubPart(ostMigrazSubPart);
        noMigraz.setTiSupportoComp(ostVLisFileBlobBystrumese.getTiSupportoComp());
        noMigraz.setTiSaveFile(ostVLisFileBlobBystrumese.getTiSaveFile());
        noMigraz.setTiCausaleNoMigraz(tiCausaleNoMigraz);
        verificaMigrazioneSubPartizioneHelper.insertEntity(noMigraz, true);
    }

    private void registraFileInErrore(long idMigrazSubPart, long idCompDoc, String nmEnteNorm,
            String nmStrutNorm, String cdRegistroNorm, BigDecimal anno, BigDecimal idStrut,
            Date dtCreazione) {
        OstMigrazSubPart migrazSubPart = verificaMigrazioneSubPartizioneHelper
                .findById(OstMigrazSubPart.class, idMigrazSubPart);
        AroCompDoc compDoc = verificaMigrazioneSubPartizioneHelper.findById(AroCompDoc.class,
                idCompDoc);
        migrazSubPart.setNiFileErroreNormaliz(
                migrazSubPart.getNiFileErroreNormaliz().add(BigDecimal.ONE));
        migrazSubPart.setNiByteSize(migrazSubPart.getNiByteSize()
                .add(compDoc.getNiSizeFileCalc() != null ? compDoc.getNiSizeFileCalc()
                        : BigDecimal.ZERO));

        OstMigrazFile migrazFile = new OstMigrazFile();
        migrazFile.setOstMigrazSubPart(migrazSubPart);
        migrazFile.setNmTabellaIdOggetto(ARO_COMP_DOC);
        migrazFile.setIdOggetto(BigDecimal.valueOf(idCompDoc));
        migrazFile.setTiStatoCor(
                it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.ERRORE_NORMALIZ.name());
        migrazFile.setTsRegStatoCor(new Date());
        String nmTenant = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.TENANT_OBJECT_STORAGE);
        migrazFile.setNmTenant(nmTenant);
        String nmBucket = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.BUCKET_OBJECT_STORAGE_COMP);
        migrazFile.setNmBucket(nmBucket);
        String cdKeyFile = nmTenant + "/" + nmEnteNorm + "/" + nmStrutNorm + "/" + cdRegistroNorm
                + "/" + anno + "/" + idCompDoc;
        migrazFile.setCdKeyFile(cdKeyFile);
        migrazFile.setNiMigrazErr(BigDecimal.ONE);
        migrazFile.setIdStrut(idStrut);
        migrazFile.setMmFile(Utils.getNumericAnnoMeseFromDate(dtCreazione));
        verificaMigrazioneSubPartizioneHelper.insertEntity(migrazFile, true);
        OstMigrazFileErr migrazFileErr = new OstMigrazFileErr();
        migrazFileErr.setOstMigrazFile(migrazFile);
        migrazFileErr.setTsErr(new Date());
        migrazFileErr.setCdErr(OST_001);
        migrazFileErr.setDsErr(MsgUtil.getMessage(OST_001));
        migrazFileErr.setTiErr("NORMALIZZAZIONE");
        verificaMigrazioneSubPartizioneHelper.insertEntity(migrazFileErr, true);
    }

}
