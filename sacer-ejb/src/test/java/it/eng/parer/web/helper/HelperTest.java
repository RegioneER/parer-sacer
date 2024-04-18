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

package it.eng.parer.web.helper;

import it.eng.paginator.hibernate.OracleSqlInterceptor;
import it.eng.parer.entity.*;
import it.eng.parer.fascicoli.helper.FascicoliHelperTest;
import it.eng.parer.grantedViewEntity.UsrVAbilAmbEnteConvenz;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.TransactionLogContext;
import it.eng.parer.slite.gen.form.ComponentiForm;
import it.eng.parer.slite.gen.form.VolumiForm;
import it.eng.parer.slite.gen.tablebean.AroCompDocRowBean;
import it.eng.parer.slite.gen.tablebean.AroCompDocTableBean;
import it.eng.parer.web.dto.DecCriterioAttribBean;
import it.eng.parer.web.dto.DecCriterioDatiSpecBean;
import it.eng.parer.web.util.StringPadding;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.FrameElement;
import it.eng.spagoLite.FrameElementInterface;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import it.eng.spagoLite.form.Elements;
import it.eng.spagoLite.form.base.BaseElement;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.xmlbean.form.Field;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ejb.EJB;
import javax.persistence.Query;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.util.StringUtils;

import javax.ejb.EJB;
import javax.persistence.Query;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class HelperTest<T> {

    protected final int maxResult = 100;

    @EJB
    protected T helper;

    public static void saveArchiveTo(Archive<WebArchive> testArchive, String path) {
        testArchive.as(ZipExporter.class).exportTo(new File(path), true);
    }

    public static JavaArchive createPaginatorJavaArchive() {
        final JavaArchive paginatorArchive = ShrinkWrap.create(JavaArchive.class, "paginator.jar")
                .addPackages(true, "it.eng.paginator").addAsResource(
                        HelperTest.class.getClassLoader().getResource("ejb-jar-paginator.xml"), "META-INF/ejb-jar.xml");
        return paginatorArchive;
    }

    public static JavaArchive createSacerLogJar() {
        return ShrinkWrap.create(JavaArchive.class, "sacerlog.jar").addPackages(true, "it.eng.parer.sacerlog")
                .addAsResource(HelperTest.class.getClassLoader().getResource("ejb-jar-sacerlog.xml"),
                        "META-INF/ejb-jar.xml");
    }

    public static JavaArchive createSacerJavaArchive(List<String> packages, Class... classes) {
        JavaArchive sacerEjbArchive = ShrinkWrap.create(JavaArchive.class, "sacerEjb.jar");
        sacerEjbArchive.addPackage(GenericHelper.class.getPackage()).addPackage(AroCompDocRowBean.class.getPackage())
                .addPackage(AroCompDocTableBean.class.getPackage()).addPackage(VolumiForm.class.getPackage())
                .addPackage(JEEBaseRowInterface.class.getPackage()).addPackage(Fields.class.getPackage())
                .addPackage(Elements.class.getPackage()).addPackage(BaseElement.class.getPackage())
                .addClass(EMFError.class).addPackage(AbstractBaseTable.class.getPackage())
                .addPackage(BaseRow.class.getPackage()).addPackage(UsrVAbilAmbEnteConvenz.class.getPackage())
                .addPackage("it.eng.sequences.hibernate").addClass(AroCompDoc.class).addClass(AroStrutDoc.class)
                .addClass(HelperTest.class).addClass(Field.Type.class).addClass(StringPadding.class)
                .addClass(CostantiDB.class).addClass(FrameElement.class)
                .addClass(ComponentiForm.RicComponentiFiltri.class).addClass(FrameElementInterface.class)
                .addClass(DecCriterioDatiSpecBean.class).addClass(StringUtils.class)
                .addClass(OracleSqlInterceptor.class).addClass(FascicoliHelperTest.class)
                .addClass(org.springframework.util.Assert.class)
                .addClass(org.springframework.beans.PropertyAccessor.class)
                .addClass(com.sun.net.ssl.X509KeyManager.class)
                // with subpackages
                .addPackages(true, "it.eng.spagoLite.form", "it.eng.parer.jboss.timer.common", "it.eng.spagoLite.db",
                        "it.eng.parer.entity", "it.eng.parer.grantedEntity", "it.eng.parer.viewEntity",
                        "org.codehaus.jettison.json", "org.apache.commons.fileupload", "org.apache.xmlbeans",
                        "org.apache.tools.ant", "com.sun.javadoc", "org.apache.commons")
                .addAsResource(HelperTest.class.getClassLoader().getResource("persistence.xml"),
                        "META-INF/persistence.xml")
                .addAsResource(HelperTest.class.getClassLoader().getResource("ejb-jar.xml"), "META-INF/ejb-jar.xml")
                // NO subpackages
                .addPackages(false, "it.eng.parer.slite.gen.viewbean", "org.apache.commons.lang",
                        "org.apache.tools.ant.taskdefs", "it.eng.parer.aop", "it.eng.parer.exception",
                        "it.eng.parer.web.helper.dto");
        for (Class c : classes) {
            sacerEjbArchive.addClass(c);
        }
        packages.parallelStream().forEach(s -> sacerEjbArchive.addPackage(s));
        return sacerEjbArchive;
    }

    public static EnterpriseArchive createEnterpriseArchive(String archiveName, JavaArchive... modules) {
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, archiveName + ".ear")
                .addAsResource(EmptyAsset.INSTANCE, "beans.xml");
        for (JavaArchive m : modules) {
            ear.addAsModule(m);
        }
        return ear;
    }

    @Test
    public void ejbInject_ok() {
        Assert.assertNotNull(helper);
    }

    protected Date[] aDateArray(int n) {
        List list = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < n; i++) {
            Date date = calendar.getTime();
            list.add(date);
            calendar.add(Calendar.DATE, 1);
        }
        Date[] array = new Date[list.size()];
        list.toArray(array);
        return array;
    }

    protected Timestamp todayTs() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    protected Timestamp tomorrowTs() {
        return Timestamp.valueOf(LocalDateTime.now().plusDays(1L));
    }

    protected Set emptySet() {
        return new HashSet(0);
    }

    protected int aInt() {
        return new Random().ints(-100, -1).findFirst().getAsInt();
    }

    protected long aLong() {
        return 0L;
    }

    protected BigDecimal aBigDecimal() {
        return BigDecimal.ZERO;
    }

    protected Set<BigDecimal> aSetOfBigDecimal(int size) {
        Set set = new HashSet(size);
        IntStream.range(0, size).forEach(n -> set.add(aBigDecimal()));
        return set;
    }

    protected Set<String> aSetOfString(int size) {
        Set set = new HashSet(size);
        IntStream.range(0, size).forEach(n -> set.add(aRandomString()));
        return set;
    }

    protected List<BigDecimal> aListOfBigDecimal(int size) {
        return aSetOfBigDecimal(size).stream().collect(Collectors.toList());
    }

    protected List<Long> aListOfLong(int size) {
        List<Long> list = new ArrayList(size);
        IntStream.range(0, size).forEach(n -> list.add(aLong()));
        return list;
    }

    protected List<String> aListOfString(int size) {
        List list = new ArrayList(size);
        IntStream.range(0, size).forEach(n -> list.add(aRandomString()));
        return list;
    }

    protected String aRandomString() {
        final int zero = 48;
        final int zed = 122;
        Random random = new Random();

        return random.ints(zero, zed + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    protected String aString() {
        return "TEST_STRING";
    }

    protected void assertNoResultException(Exception e) {
        assertExceptionMessage(e, "No entity found", "NoResultException", "ParerNoResultException",
                "it.eng.parer.exception.errors", "java.lang.NullPointerException",
                "java.lang.IndexOutOfBoundsException", "javax.persistence.EntityNotFoundException");
    }

    protected void assertNoAutogeneratedSequence(Exception e) {
        assertExceptionMessage(e, "IdentifierGenerationException");
    }

    protected void assertMergeNullEntity(Exception e) {
        assertExceptionMessage(e, "attempt to create merge event with null entity");
    }

    static public void assertExceptionMessage(Exception e, String... messages) {
        boolean ok = false;
        final String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        for (String m : messages) {
            if (message.contains(m)) {
                ok = true;
                break;
            }
        }
        if (ok) {
            Assert.assertTrue(ok);
        } else {
            throw new RuntimeException(e);
        }
    }

    protected String[] aStringArray(int size) {
        String[] array = new String[size];
        List list = new ArrayList(size);
        IntStream.range(0, size).forEach(n -> list.add(aRandomString()));
        list.toArray(array);
        return array;
    }

    protected String aFlag() {
        return "1";
    }

    protected OrgStrut aOrgStrut() {
        OrgStrut struttura = new OrgStrut();
        struttura.setIdStrut(aLong());
        struttura.setNmStrut(aString());
        return struttura;
    }

    protected DecCriterioRaggr aDecCriterioRaggr() {
        DecCriterioRaggr criterio = new DecCriterioRaggr();
        criterio.setIdCriterioRaggr(aLong());
        criterio.setAaKeyUnitaDoc(aBigDecimal());
        criterio.setAaKeyUnitaDocDa(BigDecimal.ONE);
        criterio.setAaKeyUnitaDocA(BigDecimal.TEN);
        return criterio;
    }

    protected DecCriterioDatiSpecBean aDecCriterioDatiSpecBean() {
        final DecCriterioDatiSpecBean datiSpecBean = new DecCriterioDatiSpecBean();
        datiSpecBean.setTiOper(CostantiDB.TipoOperatoreDatiSpec.UGUALE.name());
        datiSpecBean.setDlValore(aString());
        datiSpecBean.setNmAttribDatiSpec(aString());
        DecCriterioAttribBean decCriterioAttribBean = new DecCriterioAttribBean();
        decCriterioAttribBean.setIdAttribDatiSpec(aBigDecimal());
        decCriterioAttribBean.setTiEntitaSacer(aString());
        decCriterioAttribBean.setNmTipoDoc(aString());
        decCriterioAttribBean.setNmTipoUnitaDoc(aString());
        decCriterioAttribBean.setNmSistemaMigraz(aString());
        datiSpecBean.setDecCriterioAttribs(new ArrayList<>());
        datiSpecBean.getDecCriterioAttribs().add(decCriterioAttribBean);
        return datiSpecBean;
    }

    protected LogJob aLogJob() {
        LogJob logJob = new LogJob();
        logJob.setDtRegLogJob(todayTs());
        return logJob;
    }

    protected AroDoc anAroDoc() {
        AroDoc doc = new AroDoc();
        doc.setIdDoc(aLong());
        doc.setPgDoc(aBigDecimal());
        doc.setTiDoc(aString());
        doc.setTiCreazione(aString());
        return doc;
    }

    protected AroUnitaDoc anAroUnitaDoc() {
        AroUnitaDoc ud = new AroUnitaDoc();
        ud.setIdUnitaDoc(aLong());
        ud.setCdRegistroKeyUnitaDoc(aString());
        ud.setAaKeyUnitaDoc(BigDecimal.valueOf(2020));
        ud.setCdKeyUnitaDoc(aString());
        ud.setAroDocs(new ArrayList<>());
        ud.getAroDocs().add(anAroDoc());
        return ud;
    }

    protected AroUpdUnitaDoc anAroUpdUnitaDoc() {
        AroUpdUnitaDoc upd = new AroUpdUnitaDoc();
        upd.setIdUpdUnitaDoc(aLong());
        upd.setPgUpdUnitaDoc(aBigDecimal());
        return upd;
    }

    protected ElvElencoVer aElvElencoVer() {
        ElvElencoVer elenco = new ElvElencoVer();
        elenco.setIdElencoVers(aLong());
        elenco.setNmElenco(aString());
        elenco.setAroUnitaDocs(new ArrayList<>());
        elenco.getAroUnitaDocs().add(anAroUnitaDoc());

        elenco.setAroDocs(new ArrayList<>());
        elenco.getAroUnitaDocs().add(anAroUnitaDoc());

        elenco.setAroUpdUnitaDocs(new ArrayList<>());
        elenco.getAroUpdUnitaDocs().add(anAroUpdUnitaDoc());
        elenco.setNiMaxComp(aBigDecimal());
        elenco.setNiCompVersElenco(aBigDecimal());
        elenco.setNiCompAggElenco(aBigDecimal());
        elenco.setNiUpdUnitaDoc(aBigDecimal());
        elenco.setOrgStrut(aOrgStrut());
        return elenco;
    }

    protected DecCriterioRaggrFasc aDecCriterioRaggrFasc() {
        DecCriterioRaggrFasc criterio = new DecCriterioRaggrFasc();
        criterio.setIdCriterioRaggrFasc(aLong());
        criterio.setAaFascicolo(BigDecimal.valueOf(2020));
        criterio.setAaFascicoloDa(BigDecimal.valueOf(2019));
        criterio.setAaFascicoloA(BigDecimal.valueOf(2021));
        return criterio;
    }

    protected FasFascicolo aFasFascicolo() {
        FasFascicolo ff = new FasFascicolo();
        ff.setIdFascicolo(aLong());
        return ff;
    }

    protected ElvElencoVersFasc aElvElencoVersFasc() {
        ElvElencoVersFasc elenco = new ElvElencoVersFasc();
        elenco.setNiMaxFascCrit(aBigDecimal());
        elenco.setNiIndiciAip(aBigDecimal());
        elenco.setNiFascVersElenco(aBigDecimal());
        elenco.setNiTempoScadChiusCrit(aBigDecimal());
        elenco.setNtIndiceElenco(aString());
        elenco.setFasFascicoli(new ArrayList<>());
        final FasFascicolo fasFascicolo = new FasFascicolo();
        elenco.getFasFascicoli().add(fasFascicolo);
        return elenco;
    }

    protected DecVoceTitol aDecVoceTitol() {
        DecVoceTitol voce = new DecVoceTitol();
        voce.setIdVoceTitol(aLong());
        return voce;
    }

    protected DecTipoFascicolo aDecTipoFascicolo() {
        DecTipoFascicolo tipo = new DecTipoFascicolo();
        tipo.setIdTipoFascicolo(aLong());
        return tipo;
    }

    protected VolVolumeConserv aVolVolumeConserv() {
        VolVolumeConserv volume = new VolVolumeConserv();
        volume.setIdVolumeConserv(aLong());
        return volume;
    }

    protected IamUser aIamUser() {
        IamUser user = new IamUser();
        user.setIdUserIam(-1L);
        user.setNmUserid(aString());
        user.setCdPsw(aString());
        user.setNmCognomeUser(aString());
        user.setNmNomeUser(aString());
        user.setFlAttivo(aFlag());
        user.setDtRegPsw(todayTs());
        user.setDtScadPsw(tomorrowTs());
        user.setFlUserAdmin(aFlag());
        user.setFlContrIp(aFlag());
        user.setTipoUser("PERSONA_FISICA");
        return user;
    }

    protected Boolean aBoolean() {
        return Boolean.TRUE;
    }

    protected LogParam aLogParam() {
        final LogParam logParam = new LogParam();
        logParam.setTransactionLogContext(new TransactionLogContext(BigDecimal.ZERO));
        logParam.setNomeApplicazione("sacer");
        logParam.setNomeUtente("test automatici");
        logParam.setNomeAzione("test");
        return logParam;
    }
}
