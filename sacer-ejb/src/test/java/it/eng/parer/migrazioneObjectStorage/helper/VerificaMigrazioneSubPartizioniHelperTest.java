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

package it.eng.parer.migrazioneObjectStorage.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aInt;
import static it.eng.ArquillianUtils.aListOfString;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.todayTs;
import static it.eng.ArquillianUtils.tomorrowTs;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import it.eng.parer.entity.OrgSubPartition;
import it.eng.parer.entity.OstMigrazSubPart;
import it.eng.parer.web.helper.HelperTest;

@ArquillianTest
public class VerificaMigrazioneSubPartizioniHelperTest {
    @EJB
    private VerificaMigrazioneSubPartizioniHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(
                VerificaMigrazioneSubPartizioniHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""),
                        VerificaMigrazioneSubPartizioniHelperTest.class,
                        VerificaMigrazioneSubPartizioniHelper.class));
    }

    @Test
    void getOstMigrazSubPartList_queryIsOk() {
        List<String> tiStato = aListOfString(2);
        String niFile = "niFileMigrazInCorso";
        helper.getOstMigrazSubPartList(tiStato, niFile);
        assertTrue(true);
    }

    @Test
    void getOstMigrazSubPartListByStateOrdered_queryIsOk() {
        List<String> tiStato = aListOfString(2);
        int numeroJob = aInt();
        helper.getOstMigrazSubPartListByStateOrdered(tiStato, numeroJob);
        assertTrue(true);
    }

    @Test
    void getOstMigrazSubPartDaEliminareListOrdered_queryIsOk() {
        helper.getOstMigrazSubPartDaEliminareListOrdered();
        assertTrue(true);
    }

    @Test
    void getOstMigrazFileBeforeNumGiorni_queryIsOk() {
        long idMigrazSubPart = aLong();
        String tiStatoCor = aString();
        BigDecimal numGgMigrazInCorso = aBigDecimal();
        helper.getOstMigrazFileBeforeNumGiorni(idMigrazSubPart, tiStatoCor, numGgMigrazInCorso);
        assertTrue(true);
    }

    @Test
    void getOstMigrazFileByStateAndSubPartitionWithLimit_queryIsOk() {
        String stato = aString();
        OstMigrazSubPart ostMigrazSubPart = aOstMigrazSubPart();
        long maxRecords = 10L;
        helper.getOstMigrazFileByStateAndSubPartitionWithLimit(stato, ostMigrazSubPart, maxRecords);
        assertTrue(true);
    }

    private OstMigrazSubPart aOstMigrazSubPart() {
        OstMigrazSubPart ostMigrazSubPart = new OstMigrazSubPart();
        ostMigrazSubPart.setIdMigrazSubPart(aLong());
        ostMigrazSubPart.setIdStatoMigrazSubPartCor(aBigDecimal());
        ostMigrazSubPart.setMmMax(aBigDecimal());
        ostMigrazSubPart.setNiByteSize(aBigDecimal());
        ostMigrazSubPart.setNiFileDaMigrare(aBigDecimal());
        ostMigrazSubPart.setNiFileErroreNormaliz(aBigDecimal());
        ostMigrazSubPart.setNiFileMigrati(aBigDecimal());
        ostMigrazSubPart.setNiFileMigrazInCorso(aBigDecimal());
        ostMigrazSubPart.setNiFileMigrazInErrore(aBigDecimal());
        ostMigrazSubPart.setNiFileSubPart(aBigDecimal());
        ostMigrazSubPart.setNiIstanzaJobPrepara(aBigDecimal());
        ostMigrazSubPart.setNiIstanzaJobProducer(aBigDecimal());
        ostMigrazSubPart.setNmColonnaBlobFile(aString());
        ostMigrazSubPart.setNmColonnaIdFile(aString());
        ostMigrazSubPart.setNmTabellaFile(aString());
        ostMigrazSubPart.setNmTablespace(aString());
        final OrgSubPartition orgSubPartition = new OrgSubPartition();
        orgSubPartition.setCdSubPartition(aString());
        orgSubPartition.setIdSubPartition(aLong());
        return ostMigrazSubPart;
    }

    @Test
    void getOstMigrazFilePerNumErrori_queryIsOk() {
        long idMigrazSubPart = aLong();
        String tiStatoCor = aString();
        BigDecimal numMaxErr = aBigDecimal();
        helper.getOstMigrazFilePerNumErrori(idMigrazSubPart, tiStatoCor, numMaxErr);
        assertTrue(true);
    }

    @Test
    void getOstMigrazSubPartPerPartizStatoCorrList_queryIsOk() {
        String tipoPartizione = aString();
        String tiStatoCorrente = aString();
        int numeroJob = aInt();
        helper.getOstMigrazSubPartPerPartizStatoCorrList(tipoPartizione, tiStatoCorrente,
                numeroJob);
        assertTrue(true);
    }

    @Test
    void getTableSpaceList_queryIsOk() {
        String tiStato = aString();
        helper.getTableSpaceList(tiStato);
        assertTrue(true);
    }

    @Test
    void checkAllSubPartitionsWithTableSpaceAndState_queryIsOk() {
        String nmTablespace = aString();
        String tiStato = aString();
        helper.checkAllSubPartitionsWithTableSpaceAndState(nmTablespace, tiStato);
        assertTrue(true);
    }

    @Test
    void getOstMigrazSubPartByTablespace_queryIsOk() {
        String nmTablespace = aString();
        helper.getOstMigrazSubPartByTablespace(nmTablespace);
        assertTrue(true);
    }

    @Test
    void getOstVLisFileBlobIdByStrutMeseBetweenDate_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        Date data1 = todayTs();
        Date data2 = tomorrowTs();
        helper.getOstVLisFileBlobIdByStrutMeseBetweenDate(idStrut, data1, data2);
        assertTrue(true);
    }

    @Test
    void getOstMigrazStrutMeseByOrgSubPartitionFlag_queryIsOk() {
        OstMigrazSubPart ostMigrazSubPart = aOstMigrazSubPart();;
        boolean flagFileAggiunti = false;
        helper.getOstMigrazStrutMeseByOrgSubPartitionFlag(ostMigrazSubPart, flagFileAggiunti);
        assertTrue(true);
    }

    @Test
    void existsNoMigrazFileWithTiCausaleNoMigraz_queryIsOk() {
        OstMigrazSubPart ostMigrazSubPart = aOstMigrazSubPart();;
        String tiCausaleNoMigraz = aString();
        helper.existsNoMigrazFileWithTiCausaleNoMigraz(ostMigrazSubPart, tiCausaleNoMigraz);
        assertTrue(true);
    }

    @Test
    void getSubPartitionByMesiAntecedenti_queryIsOk() {
        int numeroIstanzaJob = aInt();
        helper.getSubPartitionByMesiAntecedenti(numeroIstanzaJob);
        assertTrue(true);
    }

    @Test
    void getCountFileAllPartitionWithState_queryIsOk() {
        String statoFile = aString();
        helper.getCountFileAllPartitionWithState(statoFile);
        assertTrue(true);
    }

    @Test
    void getStrutturaMesePerSubPartizione_queryIsOk() {
        BigDecimal idSubPartition = aBigDecimal();
        helper.getStrutturaMesePerSubPartizione(idSubPartition);
        assertTrue(true);
    }

    @Test
    void getOstMigrazSubPartBySubPartition_queryIsOk() {
        OrgSubPartition orgSubPartition = new OrgSubPartition();
        orgSubPartition.setIdSubPartition(aLong());
        helper.getOstMigrazSubPartBySubPartition(orgSubPartition);
        assertTrue(true);
    }

    @Test
    void usoTbsOK_queryIsOk() {
        String nmTablespace = aString();
        helper.usoTbsOK(nmTablespace);
        assertTrue(true);
    }

    @Test
    void getSommeFileMigrazConStati_queryIsOk() {
        List<String> listaStatiSubPartizioni = aListOfString(2);
        helper.getSommeFileMigrazConStati(listaStatiSubPartizioni);
        assertTrue(true);
    }

    @Test
    void getSubPartitionsMigrazInCorsoErroreMoreThanZero_queryIsOk() {
        helper.getSubPartitionsMigrazInCorsoErroreMoreThanZero();
        assertTrue(true);
    }

    @Test
    void getCountComponentsSubPartAndState_OstMigrazSubPart_String_queryIsOk() {
        OstMigrazSubPart ostMigrazSubPart = aOstMigrazSubPart();;
        String tiStato = aString();
        helper.getCountComponentsSubPartAndState(ostMigrazSubPart, tiStato);
        assertTrue(true);
    }

    @Test
    void getCountComponentsSubPartAndState_OstMigrazSubPart_List_queryIsOk() {
        OstMigrazSubPart ostMigrazSubPart = aOstMigrazSubPart();;
        List<String> tiStato = aListOfString(2);
        helper.getCountComponentsSubPartAndState(ostMigrazSubPart, tiStato);
        assertTrue(true);
    }

    @Test
    void eliminaMigrazioneFileInNewTransaction_queryIsOk() {
        long id = aLong();
        helper.eliminaMigrazioneFileInNewTransaction(id);
        assertTrue(true);
    }

    @Test
    void eliminaNoMigrazioneFileInNewTransaction_queryIsOk() {
        long id = aLong();
        helper.eliminaNoMigrazioneFileInNewTransaction(id);
        assertTrue(true);
    }

    @Test
    void findOstMigrazFileIdByOstMigrazSubPart_queryIsOk() {
        OstMigrazSubPart ostMigrazSubPart = aOstMigrazSubPart();;
        helper.findOstMigrazFileIdByOstMigrazSubPart(ostMigrazSubPart);
        assertTrue(true);
    }

    @Test
    void findOstNoMigrazFileIdByOstMigrazSubPart_queryIsOk() {
        OstMigrazSubPart ostMigrazSubPart = aOstMigrazSubPart();;
        helper.findOstNoMigrazFileIdByOstMigrazSubPart(ostMigrazSubPart);
        assertTrue(true);
    }

    @Test
    @Disabled("Qual è un nome valido di partizione ?")
    void findIdAroContenutoCompByCodiceIdentificativo_queryIsOk() {
        String codiceIdentificativo = "STRUTDEF";
        helper.findIdAroContenutoCompByCodiceIdentificativo(codiceIdentificativo);
        assertTrue(true);
    }

    @Test
    @Disabled("Qual è un nome valido di partizione ?")
    void countAroContenutoCompByCodiceIdentificativo_queryIsOk() {
        String codiceIdentificativoPartizione = "STRUTDEF";
        helper.countAroContenutoCompByCodiceIdentificativo(codiceIdentificativoPartizione);
        assertTrue(true);
    }

    @Test
    void findOstMigrazSubPartByOrgSubPartitionWithLock_queryIsOk() {
        BigDecimal idSubPartition = aBigDecimal();
        helper.findOstMigrazSubPartByOrgSubPartitionWithLock(idSubPartition);
        assertTrue(true);
    }
}
