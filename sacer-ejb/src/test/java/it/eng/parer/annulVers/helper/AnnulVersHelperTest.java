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

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.annulVers.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aFlag;
import static it.eng.ArquillianUtils.aListOfLong;
import static it.eng.ArquillianUtils.aListOfString;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.aStringArray;
import static it.eng.ArquillianUtils.assertNoResultException;
import static it.eng.ArquillianUtils.todayTs;
import static it.eng.ArquillianUtils.tomorrowTs;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.annulVers.dto.RicercaRichAnnulVersBean;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.FasFascicolo;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.utils.CostantiDB;

@ArquillianTest
public class AnnulVersHelperTest {
    @EJB
    private AnnulVersHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(AnnulVersHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), AnnulVersHelperTest.class,
                        AnnulVersHelper.class, RicercaRichAnnulVersBean.class));
    }

    @Test
    void isRichAnnulVersExisting_queryIsOk() {
        String cdRichAnnulVers = aString();
        BigDecimal idStrut = aBigDecimal();
        helper.isRichAnnulVersExisting(cdRichAnnulVers, idStrut);
        assertTrue(true);
    }

    @Test
    void getAroRichAnnulVersContainingUd_queryIsOk() {
        Long idUnitaDoc = aLong();
        Long idRichAnnulVers = aLong();
        helper.getAroRichAnnulVersContainingUd(idUnitaDoc, idRichAnnulVers);
        assertTrue(true);
    }

    @Test
    void getAroRichAnnulVersContainingFasc_queryIsOk() {
        Long idFascicolo = aLong();
        Long idRichAnnulVers = aLong();
        helper.getAroRichAnnulVersContainingFasc(idFascicolo, idRichAnnulVers);
        assertTrue(true);
    }

    @Test
    void countDocAggListOnDtVers_queryIsOk() {
        Long idUnitaDoc = aLong();
        String tiCreazione = aString();
        Date dtVers = todayTs();
        helper.countDocAggListOnDtVers(idUnitaDoc, tiCreazione, dtVers);
        assertTrue(true);
    }

    @Test
    void isUdFromPreIngest_queryIsOk() {
        Long idUnitaDoc = aLong();
        helper.isUdFromPreIngest(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getRichiesteAnnullamentoVersamentoDaElab_queryIsOk() {
        helper.getRichiesteAnnullamentoVersamentoDaElab();
        assertTrue(true);
    }

    @Test
    void getUltimoProgressivoStatoRichiesta_queryIsOk() {
        long idRichAnnulVers = aLong();
        helper.getUltimoProgressivoStatoRichiesta(idRichAnnulVers);
        assertTrue(true);
    }

    @Test
    void getUltimoProgressivoItemRichiesta_queryIsOk() {
        long idRichAnnulVers = aLong();
        helper.getUltimoProgressivoItemRichiesta(idRichAnnulVers);
        assertTrue(true);
    }

    @Test
    void getIdUserIamStatoCorrenteRichiesta_queryIsOk() {
        long idStatoRichAnnulVersCorr = aLong();
        try {
            helper.getIdUserIamStatoCorrenteRichiesta(idStatoRichAnnulVersCorr);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void retrieveVolVLisVolumeUdAnnul_queryIsOk() {
        long idRichAnnulVers = aLong();
        helper.retrieveVolVLisVolumeUdAnnul(idRichAnnulVers);
        assertTrue(true);
    }

    @Test
    void retrieveVolVLisUdAnnulByVolume_queryIsOk() {
        long idRichAnnulVers = aLong();
        long idVolumeConserv = aLong();
        helper.retrieveVolVLisUdAnnulByVolume(idRichAnnulVers, idVolumeConserv);
        assertTrue(true);
    }

    @Test
    void getItem_queryIsOk() {
        long idRichAnnulVers = aLong();
        String[] tiStatoUdElencoVers = aStringArray(2);
        helper.getItem(idRichAnnulVers, tiStatoUdElencoVers);
        assertTrue(true);
    }

    @Test
    public void getUpdItem_queryIsOk() {
        long idRichAnnulVers = aLong();
        AroUpdUDTiStatoUpdElencoVers[] tiStatoUdElencoVers = {
                AroUpdUDTiStatoUpdElencoVers.IN_ATTESA_SCHED,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_APERTO,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CHIUSO,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_COMPLETATO,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_INDICI_AIP_GENERATI,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_DA_CHIUDERE,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_VALIDATO,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_IN_CODA_INDICE_AIP,
                AroUpdUDTiStatoUpdElencoVers.NON_SELEZ_SCHED,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO,
                AroUpdUDTiStatoUpdElencoVers.IN_CODA_JMS_INDICE_AIP_DA_ELAB };
        helper.getUpdItem(idRichAnnulVers, tiStatoUdElencoVers);
        assertTrue(true);
    }

    @Test
    void getItemFasc_queryIsOk() {
        long idRichAnnulVers = aLong();
        TiStatoFascElencoVers[] tiStatoFascElencoVers = {
                TiStatoFascElencoVers.IN_ATTESA_SCHED, TiStatoFascElencoVers.IN_ELENCO_APERTO,
                TiStatoFascElencoVers.IN_ELENCO_CHIUSO, TiStatoFascElencoVers.IN_ELENCO_COMPLETATO,
                TiStatoFascElencoVers.IN_ELENCO_CON_AIP_CREATO,
                TiStatoFascElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO,
                TiStatoFascElencoVers.IN_ELENCO_DA_CHIUDERE,
                TiStatoFascElencoVers.IN_ELENCO_FIRMATO,
                TiStatoFascElencoVers.IN_ELENCO_IN_CODA_CREAZIONE_AIP,
                TiStatoFascElencoVers.NON_SELEZ_SCHED };
        helper.getItemFasc(idRichAnnulVers, tiStatoFascElencoVers);
        assertTrue(true);
    }

    @Test
    void getDocAggiunti_queryIsOk() {
        long idRichAnnulVers = aLong();
        String[] tiStatoDocElencoVers = aStringArray(2);
        helper.getDocAggiunti(idRichAnnulVers, tiStatoDocElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveElvVLisElencoUdAnnuls_queryIsOk() {
        long idRichAnnulVers = aLong();
        helper.retrieveElvVLisElencoUdAnnuls(idRichAnnulVers);
        assertTrue(true);
    }

    @Test
    void retrieveElvVLisUdAnnulByElenco_queryIsOk() {
        long idRichAnnulVers = aLong();
        long idElencoVers = aLong();
        helper.retrieveElvVLisUdAnnulByElenco(idRichAnnulVers, idElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveAroVRicRichAnnvrs_queryIsOk() {
        long idUser = aLong();
        RicercaRichAnnulVersBean filtri = new RicercaRichAnnulVersBean();
        filtri.setAa_fascicolo(BigDecimal.valueOf(2020));
        filtri.setAa_key_unita_doc(aBigDecimal());
        filtri.setCd_key_fascicolo(aString());
        filtri.setCd_key_unita_doc(aString());
        filtri.setCd_registro_key_unita_doc(aString());
        filtri.setCd_rich_annul_vers(aString());
        filtri.setDs_rich_annul_vers(aString());
        filtri.setDt_creazione_rich_annul_vers_da(todayTs());
        filtri.setDt_creazione_rich_annul_vers_a(tomorrowTs());
        filtri.setFl_annul_ping(aFlag());
        filtri.setFl_annul_ping(aFlag());
        filtri.setFl_immediata(aFlag());
        filtri.setFl_non_annul(aFlag());
        filtri.setId_ambiente(aBigDecimal());
        filtri.setId_ente(aBigDecimal());
        filtri.setId_strut(aBigDecimal());
        filtri.setNt_rich_annul_vers(aString());
        filtri.setTi_rich_annul_vers(aString());
        filtri.setTi_stato_rich_annul_vers_cor(aListOfString(2));
        helper.retrieveAroVRicRichAnnvrs(idUser, filtri);
        assertTrue(true);
    }

    @Test
    void getAroVLisItemRichAnnvrs_queryIsOk() {
        BigDecimal idRichAnnulVers = aBigDecimal();
        helper.getAroVLisItemRichAnnvrs(idRichAnnulVers);
        assertTrue(true);
    }

    @Test
    void getAroVLisStatoRichAnnvrs_queryIsOk() {
        BigDecimal idRichAnnulVers = aBigDecimal();
        helper.getAroVLisStatoRichAnnvrs(idRichAnnulVers);
        assertTrue(true);
    }

    @Test
    void countAroItemRichAnnulVers_queryIsOk() {
        BigDecimal idRichAnnulVers = aBigDecimal();
        String[] tiStato = aStringArray(2);
        helper.countAroItemRichAnnulVers(idRichAnnulVers, tiStato);
        assertTrue(true);
    }

    @Test
    void isUserAbilitatoToTipoDato_queryIsOk() {
        long idUserIam = aLong();
        long idTipoDatoApplic = aLong();
        String nmClasseTipoDato = aString();
        helper.isUserAbilitatoToTipoDato(idUserIam, idTipoDatoApplic, nmClasseTipoDato);
        assertTrue(true);
    }

    @Test
    void getAroErrRichAnnulVersByGravity_queryIsOk() {
        long idItemRichAnnulVers = aLong();
        String tiGravita = aString();
        helper.getAroErrRichAnnulVersByGravity(idItemRichAnnulVers, tiGravita);
        assertTrue(true);
    }

    @Test
    void isUdAnnullata_queryIsOk() {
        long idUnitaDoc = aLong();
        helper.isUdAnnullata(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    void isFascicoloAnnullato_queryIsOk() {
        long idFascicolo = aLong();
        helper.isFascicoloAnnullato(idFascicolo);
        assertTrue(true);
    }

    @Test
    void getDecTipoDocPrincipale_queryIsOk() {
        long idUnitaDoc = aLong();
        try {
            helper.getDecTipoDocPrincipale(idUnitaDoc);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void getIdUserIamFirstStateRich_queryIsOk() {
        BigDecimal idRichAnnulVers = aBigDecimal();
        try {
            helper.getIdUserIamFirstStateRich(idRichAnnulVers);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void getStatoRichiestaRecenteAnnulUd_queryIsOk() {
        List<Long> idUnitaDocList = aListOfLong(2);
        try {
            helper.getStatoRichiestaRecenteAnnulUd(idUnitaDocList);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void retrieveElvVLisElencoFascAnnul_long_queryIsOk() {
        long idRichAnnulVers = aLong();
        helper.retrieveElvVLisElencoFascAnnul(idRichAnnulVers);
        assertTrue(true);
    }

    @Test
    void retrieveElvVLisElencoFascAnnul_long_long_queryIsOk() {
        long idRichAnnulVers = aLong();
        long idElencoVersFasc = aLong();
        helper.retrieveElvVLisElencoFascAnnul(idRichAnnulVers, idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    void retrieveElvVLisFascAnnulByElenco_queryIsOk() {
        long idRichAnnulVers = aLong();
        long idElencoVersFasc = aLong();
        helper.retrieveElvVLisFascAnnulByElenco(idRichAnnulVers, idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    void getXmlRichAnnulVersByTipo_queryIsOk() {
        final long idRichAnnulVers = 0L;
        for (CostantiDB.TiXmlRichAnnulVers tiXmlRichAnnulVers : CostantiDB.TiXmlRichAnnulVers
                .values()) {
            try {
                helper.getXmlRichAnnulVersByTipo(idRichAnnulVers, tiXmlRichAnnulVers);
                fail("non deve trovare nulla con id 0");
            } catch (Exception e) {
                assertNoResultException(e);
            }
        }
        assertTrue(true);
    }

    @Test
    void updateFascicoliItem_queryIsOk() {
        final long idRichAnnulVers = 0L;
        final Timestamp dtAnnull = tomorrowTs();
        final String ntAnnul = AnnulVersHelperTest.class.getSimpleName();
        for (FasFascicolo.TiStatoConservazione tiStatoConservazione : FasFascicolo.TiStatoConservazione
                .values()) {
            helper.updateFascicoliItem(idRichAnnulVers, dtAnnull, tiStatoConservazione, ntAnnul);
        }
        assertTrue(true);
    }

    @Test
    void updateUnitaDocumentarieItemTiStatoConservazione_queryIsOk() {
        final String ntAnnul = AnnulVersHelperTest.class.getSimpleName();
        final long idRichAnnulVers = 0L;
        final String tiAnnull = "tiAnnull";
        final String tiStatoConservazione = "tiStatoConservazione";
        final Timestamp dtAnnul = tomorrowTs();
        helper.updateUnitaDocumentarieItem(idRichAnnulVers, dtAnnul, tiAnnull, tiStatoConservazione,
                ntAnnul);
        assertTrue(true);
    }

    @Test
    void updateUnitaDocumentarieItem_queryIsOk() {
        final String ntAnnul = AnnulVersHelperTest.class.getSimpleName();
        final long idRichAnnulVers = 0L;
        final Timestamp dtAnnul = tomorrowTs();
        helper.updateUpdUnitaDocumentarieItem(idRichAnnulVers, dtAnnul, ntAnnul);
        assertTrue(true);
    }

    @Test
    void updateCollegamentiUd_queryIsOk() {
        helper.updateCollegamentiUd(0L);
        assertTrue(true);
    }

    @Test
    void updateDocumentiUdItem_queryIsOk() {
        final long idRichAnnulVers = 0L;
        final Timestamp dtAnnul = tomorrowTs();
        final String tiAnnull = "tiAnnull";
        final String ntAnnul = AnnulVersHelperTest.class.getSimpleName();
        helper.updateDocumentiUdItem(idRichAnnulVers, dtAnnul, tiAnnull, ntAnnul);
        assertTrue(true);
    }

    @Test
    void updateStatoItemList_queryIsOk() {
        final long idRichAnnulVers = 0L;
        final String tiStatoItem = "tiStatoItem";
        helper.updateStatoItemList(idRichAnnulVers, tiStatoItem);
        assertTrue(true);
    }

    @Test
    void updateCollegamentiFasc_queryIsOk() {
        helper.updateCollegamentiFasc(0L);
        assertTrue(true);
    }

    @Test
    void deleteElvUdVersDaElabElenco_queryIsOk() {
        helper.deleteElvUdVersDaElabElenco(0L);
        assertTrue(true);
    }

    @Test
    void deleteElvUpdUdDaElabElenco_queryIsOk() {
        helper.deleteElvUpdUdDaElabElenco(0L);
        assertTrue(true);
    }

    @Test
    void deleteElvDocAggDaElabElenco_queryIsOk() {
        helper.deleteElvDocAggDaElabElenco(0L);
        assertTrue(true);
    }

    @Test
    void deleteElvFascDaElabElenco_queryIsOk() {
        helper.deleteElvFascDaElabElenco(0L);
        assertTrue(true);
    }

    @Test
    void deleteAroErrRichAnnulVers_queryIsOk() {
        helper.deleteAroErrRichAnnulVers(0L, "tiErrRichAnnulVers");
        assertTrue(true);
    }

    @Test
    void deleteAroErrRichAnnulVersTiErrList_queryIsOk() {
        helper.deleteAroErrRichAnnulVers(0L, "tiErrRichAnnulVers", "tipo2");
        assertTrue(true);
    }
}
