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

package it.eng.parer.migrazioneObjectStorage.helper;

import it.eng.parer.entity.OrgSubPartition;
import it.eng.parer.entity.OstMigrazSubPart;
import it.eng.parer.serie.helper.ModelliSerieHelperTest;
import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class VerificaMigrazioneSubPartizioniHelperTest {
    @EJB
    private VerificaMigrazioneSubPartizioniHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(VerificaMigrazioneSubPartizioniHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), VerificaMigrazioneSubPartizioniHelperTest.class,
                        VerificaMigrazioneSubPartizioniHelper.class));
    }

    @Test
    public void getOstMigrazSubPartList_queryIsOk() {
        List<String> tiStato = aListOfString(2);
        String niFile = "niFileMigrazInCorso";
        helper.getOstMigrazSubPartList(tiStato, niFile);
        assertTrue(true);
    }

    @Test
    public void getOstMigrazSubPartListByStateOrdered_queryIsOk() {
        List<String> tiStato = aListOfString(2);
        int numeroJob = aInt();
        helper.getOstMigrazSubPartListByStateOrdered(tiStato, numeroJob);
        assertTrue(true);
    }

    @Test
    public void getOstMigrazSubPartDaEliminareListOrdered_queryIsOk() {
        helper.getOstMigrazSubPartDaEliminareListOrdered();
        assertTrue(true);
    }

    @Test
    public void getOstMigrazFileBeforeNumGiorni_queryIsOk() {
        long idMigrazSubPart = aLong();
        String tiStatoCor = aString();
        BigDecimal numGgMigrazInCorso = aBigDecimal();
        helper.getOstMigrazFileBeforeNumGiorni(idMigrazSubPart, tiStatoCor, numGgMigrazInCorso);
        assertTrue(true);
    }

    @Test
    public void getOstMigrazFileByStateAndSubPartitionWithLimit_queryIsOk() {
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
    public void getOstMigrazFilePerNumErrori_queryIsOk() {
        long idMigrazSubPart = aLong();
        String tiStatoCor = aString();
        BigDecimal numMaxErr = aBigDecimal();
        helper.getOstMigrazFilePerNumErrori(idMigrazSubPart, tiStatoCor, numMaxErr);
        assertTrue(true);
    }

    @Test
    public void getOstMigrazSubPartPerPartizStatoCorrList_queryIsOk() {
        String tipoPartizione = aString();
        String tiStatoCorrente = aString();
        int numeroJob = aInt();
        helper.getOstMigrazSubPartPerPartizStatoCorrList(tipoPartizione, tiStatoCorrente, numeroJob);
        assertTrue(true);
    }

    @Test
    public void getTableSpaceList_queryIsOk() {
        String tiStato = aString();
        helper.getTableSpaceList(tiStato);
        assertTrue(true);
    }

    @Test
    public void checkAllSubPartitionsWithTableSpaceAndState_queryIsOk() {
        String nmTablespace = aString();
        String tiStato = aString();
        helper.checkAllSubPartitionsWithTableSpaceAndState(nmTablespace, tiStato);
        assertTrue(true);
    }

    @Test
    public void getOstMigrazSubPartByTablespace_queryIsOk() {
        String nmTablespace = aString();
        helper.getOstMigrazSubPartByTablespace(nmTablespace);
        assertTrue(true);
    }

    @Test
    public void getOstVLisFileBlobIdByStrutMeseBetweenDate_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        Date data1 = todayTs();
        Date data2 = tomorrowTs();
        helper.getOstVLisFileBlobIdByStrutMeseBetweenDate(idStrut, data1, data2);
        assertTrue(true);
    }

    @Test
    public void getOstMigrazStrutMeseByOrgSubPartitionFlag_queryIsOk() {
        OstMigrazSubPart ostMigrazSubPart = aOstMigrazSubPart();
        ;
        boolean flagFileAggiunti = false;
        helper.getOstMigrazStrutMeseByOrgSubPartitionFlag(ostMigrazSubPart, flagFileAggiunti);
        assertTrue(true);
    }

    @Test
    public void existsNoMigrazFileWithTiCausaleNoMigraz_queryIsOk() {
        OstMigrazSubPart ostMigrazSubPart = aOstMigrazSubPart();
        ;
        String tiCausaleNoMigraz = aString();
        helper.existsNoMigrazFileWithTiCausaleNoMigraz(ostMigrazSubPart, tiCausaleNoMigraz);
        assertTrue(true);
    }

    @Test
    public void getSubPartitionByMesiAntecedenti_queryIsOk() {
        int numeroIstanzaJob = aInt();
        helper.getSubPartitionByMesiAntecedenti(numeroIstanzaJob);
        assertTrue(true);
    }

    @Test
    public void getCountFileAllPartitionWithState_queryIsOk() {
        String statoFile = aString();
        helper.getCountFileAllPartitionWithState(statoFile);
        assertTrue(true);
    }

    @Test
    public void getStrutturaMesePerSubPartizione_queryIsOk() {
        BigDecimal idSubPartition = aBigDecimal();
        helper.getStrutturaMesePerSubPartizione(idSubPartition);
        assertTrue(true);
    }

    @Test
    public void getOstMigrazSubPartBySubPartition_queryIsOk() {
        OrgSubPartition orgSubPartition = new OrgSubPartition();
        orgSubPartition.setIdSubPartition(aLong());
        helper.getOstMigrazSubPartBySubPartition(orgSubPartition);
        assertTrue(true);
    }

    @Test
    public void usoTbsOK_queryIsOk() {
        String nmTablespace = aString();
        helper.usoTbsOK(nmTablespace);
        assertTrue(true);
    }

    @Test
    public void getSommeFileMigrazConStati_queryIsOk() {
        List<String> listaStatiSubPartizioni = aListOfString(2);
        helper.getSommeFileMigrazConStati(listaStatiSubPartizioni);
        assertTrue(true);
    }

    @Test
    public void getSubPartitionsMigrazInCorsoErroreMoreThanZero_queryIsOk() {
        helper.getSubPartitionsMigrazInCorsoErroreMoreThanZero();
        assertTrue(true);
    }

    @Test
    public void getCountComponentsSubPartAndState_OstMigrazSubPart_String_queryIsOk() {
        OstMigrazSubPart ostMigrazSubPart = aOstMigrazSubPart();
        ;
        String tiStato = aString();
        helper.getCountComponentsSubPartAndState(ostMigrazSubPart, tiStato);
        assertTrue(true);
    }

    @Test
    public void getCountComponentsSubPartAndState_OstMigrazSubPart_List_queryIsOk() {
        OstMigrazSubPart ostMigrazSubPart = aOstMigrazSubPart();
        ;
        List<String> tiStato = aListOfString(2);
        helper.getCountComponentsSubPartAndState(ostMigrazSubPart, tiStato);
        assertTrue(true);
    }

    @Test
    public void eliminaMigrazioneFileInNewTransaction_queryIsOk() {
        long id = aLong();
        helper.eliminaMigrazioneFileInNewTransaction(id);
        assertTrue(true);
    }

    @Test
    public void eliminaNoMigrazioneFileInNewTransaction_queryIsOk() {
        long id = aLong();
        helper.eliminaNoMigrazioneFileInNewTransaction(id);
        assertTrue(true);
    }

    @Test
    public void findOstMigrazFileIdByOstMigrazSubPart_queryIsOk() {
        OstMigrazSubPart ostMigrazSubPart = aOstMigrazSubPart();
        ;
        helper.findOstMigrazFileIdByOstMigrazSubPart(ostMigrazSubPart);
        assertTrue(true);
    }

    @Test
    public void findOstNoMigrazFileIdByOstMigrazSubPart_queryIsOk() {
        OstMigrazSubPart ostMigrazSubPart = aOstMigrazSubPart();
        ;
        helper.findOstNoMigrazFileIdByOstMigrazSubPart(ostMigrazSubPart);
        assertTrue(true);
    }

    @Test
    @Ignore("Qual è un nome valido di partizione ?")
    public void findIdAroContenutoCompByCodiceIdentificativo_queryIsOk() {
        String codiceIdentificativo = "STRUTDEF";
        helper.findIdAroContenutoCompByCodiceIdentificativo(codiceIdentificativo);
        assertTrue(true);
    }

    @Test
    @Ignore("Qual è un nome valido di partizione ?")
    public void countAroContenutoCompByCodiceIdentificativo_queryIsOk() {
        String codiceIdentificativoPartizione = "STRUTDEF";
        helper.countAroContenutoCompByCodiceIdentificativo(codiceIdentificativoPartizione);
        assertTrue(true);
    }

    @Test
    public void findOstMigrazSubPartByOrgSubPartitionWithLock_queryIsOk() {
        BigDecimal idSubPartition = aBigDecimal();
        helper.findOstMigrazSubPartByOrgSubPartitionWithLock(idSubPartition);
        assertTrue(true);
    }
}
