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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prova;

import it.eng.parer.disciplinare.DisciplinareTecnicoHelper;
import it.eng.parer.entity.AplParamApplic;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.sql.Clob;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * @author Iacolucci_M
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDisciplinareTecnico {

    public static final String NOME_FILE = "disciplinare";
//    public static final long ID_STRUTTURA = 181; // SVILUPPO
//    public static final long ID_STRUTTURA = 524; // SVILUPPO BonnyStrut
    
    public static final long ID_STRUTTURA = 3323; // SVILUPPO TEST SERIE
//    public static final long ID_STRUTTURA = 4708; // SVILUPPO TEST FIRMA
//    public static final long ID_STRUTTURA = 80151230; // PRODUZIONE COMUNE DI VIGEVANO
//    public static final long ID_STRUTTURA = 3323; // PRODUZIONE COMUNE DI VIGEVANO
//    public static final long ID_STRUTTURA = 78122235; // PRODUZIONE COMUNE DI LUSERNA
    
    public static final String RISORSE = "src/test/resources";
    public static final String OUTPUT = "OUTPUT";
//    public static final String DATA_RIFERIMENTO = "31/07/2017 23:59:59";
    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
//    public static Date DATA_RIF_QUERY = null;
/*
    static {
        try {
            DATA_RIF_QUERY = sdf.parse(DATA_RIFERIMENTO);
        } catch (ParseException ex) {
        }
    }
*/
    private EJBContainer ejbContainer = null;

    @Resource
    private UserTransaction userTransaction;
    @EJB
    private DisciplinareTecnicoHelper disciplinareTecnicoHelper;
    
    
//    @EJB
//    private DisciplinareTecnicoEjb disciplinareTecnicoEjb;
    @PersistenceContext
    public EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(TestDisciplinareTecnico.class);

    public TestDisciplinareTecnico() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws NamingException {
        FileInputStream in = null;
        try {

            Properties props = new Properties();
            String locationPath = TestDisciplinareTecnico.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            String fileProperties = locationPath + "jndi.properties";

            in = new FileInputStream(fileProperties);
            props.load(in);
            in.close();
//            String driver = props.getProperty("saceriamDs.JdbcDriver");
//            if (driver != null) {
//                Class.forName(driver);
//            }
//             String url = props.getProperty("saceriamDs.JdbcUrl");
//             String username = props.getProperty("saceriamDs.UserName");
//             String password = props.getProperty("saceriamDs.Password");
//             con = DriverManager.getConnection(url, username, password);
//             eseguiBatch(fileCaricamento);
            // EJB

            ejbContainer = EJBContainer.createEJBContainer(props);
            ejbContainer.getContext().bind("inject", this);

//          DECOMMENTARE PER PULIRE TUTTE LE TABELLE DEL LOG CON LE FOTO

            /*
            userTransaction.begin();
            Query q = entityManager.createNamedQuery("LogFotoOggettoEvento.deleteAll", LogFotoOggettoEvento.class);
            q.executeUpdate();
            userTransaction.commit();
             */
        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug(null, ex);
        } finally {
            /*            
             try {
             in.close();
             } catch (IOException ex) {
             log.debug(null, ex);
             }
             */
        }

    }

    @After
    public void tearDown() {
        if (ejbContainer != null) {
            ejbContainer.close();
        }
    }

    
    @Test
    public void entityTest() throws Exception {
        userTransaction.begin();
        String queryStr = "SELECT a FROM AplParamApplic a ";
//                + "WHERE a.nmParamApplic = 'sdfjshdkfjhsfjkhsdkfj' ";
        TypedQuery<AplParamApplic> query = entityManager.createQuery(queryStr, AplParamApplic.class);
        query.setMaxResults(1);
        List<AplParamApplic> l=query.getResultList();
        
//        long risultato=query.getFirstResult();

        System.out.println("****************** RISULTATO *************************: "+ (l.size() >0 ? true : false) );
        queryStr="SELECT o FROM OrgStrut o";

//        TypedQuery<OrgStrut> query2 = entityManager.createQuery(queryStr, OrgStrut.class);
//        List<OrgStrut> l2=query2.getResultList();
//        for (OrgStrut orgStrut : l2) {
//            System.out.println(String.format("Struttura [%d] nome:%s ", orgStrut.getIdStrut(), orgStrut.getNmStrut()) );
//        }
        
        userTransaction.commit();
    }


/*
    @Test
    public void entityTest() throws Exception {
        userTransaction.begin();
        AplParamApplicReport ap = disciplinareTecnicoHelper.getAplParamApplicReportByAppReport("SACER", "DISCIPLINARE_TECNICO");
        if (ap != null) {
            log.info("Record estratto {}-{}", ap.getNmApplic(), ap.getNmReport());
        }
        userTransaction.commit();
    }
*/
    /*
    @Test
    public void ejbTest() throws Exception {
        userTransaction.begin();
        generaDisciplinarePerIdStrut(ID_STRUTTURA);
        userTransaction.commit();
    }
*/

    @Test
    public void serverTest() throws UnknownHostException {
//        long[] idStrut={36};
//        long[] idStrut={3323};
//        long[] idStrut={24123973};
//        long[] idStrut={36};
//        long[] idStrut={7871494,80151230, 78122235, 11952633, 36, 7871494, 177, 24123973, 69734492, 21683529};
        long[] idStrut={ID_STRUTTURA};
        
//        long[] idStrut={24123973, 69734492, 21683529};

        for (long l : idStrut) {
            try {
                generaDisciplinarePerIdStrut(l);
            } catch (NotSupportedException ex) {
                java.util.logging.LoggerFactory.getLogger(TestDisciplinareTecnico.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SystemException ex) {
                java.util.logging.LoggerFactory.getLogger(TestDisciplinareTecnico.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void generaDisciplinarePerIdStrut(long idStrut) throws UnknownHostException, NotSupportedException, SystemException {
        userTransaction.begin();

        stampaParametriDiLancio();
        String memoria0 = getMemory();
        String query = leggiFile(NOME_FILE + ".sql");
        Date dataStart1 = new Date();
        double inizio1 = (double) dataStart1.getTime();
        try {
            //********** ESTRAE FOTO *****************
            Clob risultato = disciplinareTecnicoHelper.eseguiQuery(query, idStrut);
            byte[] xmlIniziale = disciplinareTecnicoHelper.getClobAsByteArray(risultato);
            scriviFile(NOME_FILE + ".xml", xmlIniziale);
            //****************************************
            
            /** Estrai id Accordo Ente e Data generazione Disciplinare
             */
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                // XXE: This is the PRIMARY defense. If DTDs (doctypes) are disallowed,
                // almost all XML entity attacks are prevented
                final String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
                dbf.setFeature(FEATURE, true);
                dbf.setFeature("http://xml.org/sax/features/external-general-entities",false);

                dbf.setFeature("http://xml.org/sax/features/external-parameter-entities",false);
                // ... and these as well, per Timothy Morgan's 2014 paper:
                // "XML Schema, DTD, and Entity Attacks" (see reference below)
                dbf.setXIncludeAware(false);
                dbf.setExpandEntityReferences(false);
                // As stated in the documentation, "Feature for Secure Processing (FSP)" is the central mechanism that will
                // help you safeguard XML processing. It instructs XML processors, such as parsers, validators, 
                // and transformers, to try and process XML securely, and the FSP can be used as an alternative to
                // dbf.setExpandEntityReferences(false); to allow some safe level of Entity Expansion
                // Exists from JDK6.
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                // ... and, per Timothy Morgan:
                // "If for some reason support for inline DOCTYPEs are a requirement, then
                // ensure the entity settings are disabled (as shown above) and beware that SSRF
                // attacks
                // (http://cwe.mitre.org/data/definitions/918.html) and denial
                // of service attacks (such as billion laughs or decompression bombs via "jar:")
                // are a risk."
                DocumentBuilder builder = dbf.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(new String(xmlIniziale,"UTF-8")));
                Document doc=builder.parse(is);
                XPath xPath = XPathFactory.newInstance().newXPath();
                String queryXml="//fotoOggetto/recordChild[tipoRecord=\"Ente convenzionato\"]/child/recordChild[tipoRecord=\"Accordo\"]/child/idRecord";
                XPathExpression expr = xPath.compile(queryXml);
                String idAccordo=expr.evaluate(doc);
                queryXml="//fotoOggetto/recordMaster/keyRecord/datoKey[colonnaKey=\"data_generazione\"]/valoreKey";
                expr = xPath.compile(queryXml);
                String dataDisc=expr.evaluate(doc);
                SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                System.out.println("idAccordo:"+idAccordo+" data generazione:"+sdf.parse(dataDisc));
                dataDisc=null;
            } catch (Exception ex) {
                log.error("Errore nel parsing XML", ex);
            }            
            
            Date dataEnd1 = new Date();
            double fine1 = (double) dataEnd1.getTime();
            Date dataStart2 = new Date();
            String memoria1 = getMemory();

            //********** ESTRAE XML INTERMEDIO *****************
            String xmlIntermedio = trasformaInXmlIntermedio(risultato);
            scriviFile(NOME_FILE + "_fase1.xml", xmlIntermedio.getBytes("UTF-8"));
            //****************************************

            //********** ESTRAE XML FOP *****************
            String xmlFo = trasformaInFo2(xmlIntermedio);
            scriviFile(NOME_FILE + ".fo", xmlFo.getBytes("UTF-8"));
            //****************************************

            //********** ESTRAE PDF *****************
            byte[] stringaPDF = disciplinareTecnicoHelper.trasformaInPDF(xmlFo);
            scriviFile(NOME_FILE+"-"+idStrut+".pdf", stringaPDF);
            //****************************************

            String memoria2 = getMemory();
            Date dataEnd2 = new Date();
            double inizio2 = (double) dataStart2.getTime();
            double fine2 = (double) dataEnd2.getTime();
            StringBuilder report = new StringBuilder();
            report.append("===============================================================================\n")
                    .append("- TEMPO IMPIEGATO per la query [" + (fine1 - inizio1) / 1000 + "] secondi.\n")
                    .append("- TEMPO IMPIEGATO per la trasformazione [" + (fine2 - inizio2) / 1000 + "] secondi.\n")
                    .append("---- memoria -------------------------------------------------------------------\n")
                    .append("Prima di tutto:\n")
                    .append("-------------------------------------------------------------------\n")
                    .append(memoria0).append("\n")
                    .append("Prima della trasformazione:\n")
                    .append("-------------------------------------------------------------------\n")
                    .append(memoria1).append("\n")
                    .append("Dopo la trasformazione:\n")
                    .append("-------------------------------------------------------------------\n")
                    .append(memoria2).append("\n")
                    .append("===============================================================================\n");
            scriviFile(NOME_FILE + ".txt", report.toString().getBytes());
            System.out.println(report);
            userTransaction.commit();

        } catch (Exception ex) {
            log.error(null, ex);
        }

    }

    
    public static String leggiFile(String nomeFile) {
        String locationPath = TestDisciplinareTecnico.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        byte[] b = null;
        StringBuffer strBuf = new StringBuffer();
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(locationPath + nomeFile), "UTF8"));
            String str = null;
            while ((str = in.readLine()) != null) {
                strBuf.append(str + "\n");
            }
            in.close();
        } catch (Exception ex) {
            System.out.println("leggiFile:" + ex.getMessage());
        }
//        return (b != null) ? new String(b) : null;
//        System.out.println("SQL LETTO:"+strBuf);

        return strBuf.toString();
    }

    public static void scriviFile(String nomeFile, byte[] contenuto) {
        String locationPath = TestDisciplinareTecnico.class.getProtectionDomain().getCodeSource().getLocation().getFile() + "../../src/test/resources/" + OUTPUT + "/";

        File file = new File(locationPath + nomeFile);
        file.delete();

        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile(locationPath + nomeFile, "rw");
            f.write(contenuto);
        } catch (Exception ex) {
            System.out.println("scriviFile:" + ex.getMessage());
        } finally {
            try {
                f.close();
            } catch (Exception ex) {
                System.out.println("Errore chiusura file:" + ex.getMessage());
            }
        }
    }

    private static String getMemory() {
        Runtime runtime = Runtime.getRuntime();
        NumberFormat format = NumberFormat.getInstance();
        StringBuilder sbPrima = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        sbPrima.append("free memory: ").append(format.format(freeMemory / 1024)).append("\n");
        sbPrima.append("allocated memory: ").append(format.format(allocatedMemory / 1024) + "\n");
        sbPrima.append("max memory: " + format.format(maxMemory / 1024) + "\n");
        sbPrima.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");
        return sbPrima.toString();
    }

    private static void stampaParametriDiLancio() {
        List<String> lista = ManagementFactory.getRuntimeMXBean().getInputArguments();

        System.out.println("Numero di parametri di lancio: " + lista.size());
        for (String str : lista) {
            System.out.println("Par: " + str);
        }
    }

    public String trasformaInFo2(String fotoXml) {
        String ret = null;
        try {

            String xslPerIntermedio = leggiFile(NOME_FILE + ".xsl");
            ret = disciplinareTecnicoHelper.trasformaXmlInXml(fotoXml, xslPerIntermedio);
        } catch (Exception ex) {
            log.error(null, ex);
        }
        return ret;
    }

    public String trasformaInXmlIntermedio(Clob fotoXml) {
        String ret = null;
        try {

            String xslPerIntermedio = leggiFile(NOME_FILE + "_fase1.xsl");
            ret = disciplinareTecnicoHelper.trasformaXmlInXml(new String(disciplinareTecnicoHelper.getClobAsByteArray(fotoXml), "UTF-8"), xslPerIntermedio);
        } catch (Exception ex) {
            log.error(null, ex);
        }
        return ret;
    }

}
