package it.eng.parer.crypto.test;

import it.eng.parer.crypto.model.ParerCRL;
import it.eng.parer.firma.crypto.verifica.CryptoInvoker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test per le funzionalità delle CRL su crypto. Sarebbe bene spostare queste classi su uno scope di test.
 *
 * @author Snidero_L
 */
public class GestioneCRL extends HttpServlet {

    Logger log = LoggerFactory.getLogger(GestioneCRL.class);
    @EJB
    private CryptoInvoker cryInv;

    @Resource
    private UserTransaction tx;

    private static final String basePath = System.getProperty("java.io.tmpdir");

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * 
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String operation = null;

        String crlDN = null;
        String crlKeyId = null;
        List<String> urls = null;
        ParerCRL parerCRL = null;
        byte[] blobFilePerFirma = null;

        String dettaglioErrore = "errore generico";
        if (ServletFileUpload.isMultipartContent(request)) {
            // Create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // Set factory constraints
            factory.setSizeThreshold(4096);

            factory.setRepository(new File(basePath));

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Set overall request size constraint
            upload.setSizeMax(1000 * 1000 * 300);
            try {

                // Parse the request
                List<FileItem> items = upload.parseRequest(request);

                for (FileItem item : items) {

                    if (item.isFormField()) {

                        if (item.getFieldName().equals("crlDN")) {
                            crlDN = item.getString();
                        }
                        if (item.getFieldName().equals("crlKeyId")) {
                            crlKeyId = item.getString();
                        }
                        if (item.getFieldName().equals("listaCRL")) {
                            String listaCRL = item.getString();
                            if (listaCRL != null) {
                                String[] split = listaCRL.split(";");
                                urls = Arrays.asList(split);
                            }

                        }

                        if (item.getFieldName().equals("crlFromFirmatario")) {
                            operation = "crlFromFirmatario";
                        }

                        if (item.getFieldName().equals("crlFromDnKeyId")) {
                            operation = "crlFromDnKeyId";
                        }
                        if (item.getFieldName().equals("addCrlFromUrls")) {
                            operation = "addCrlFromUrls";
                        }

                    } else {
                        if (item.getFieldName().equals("blobFirmatario")) {
                            blobFilePerFirma = IOUtils.toByteArray(item.getInputStream());
                        }
                    }

                }
                tx.begin();
                // Operazioni
                switch (operation) {
                case "crlFromFirmatario":
                    parerCRL = testCrlFromFirmatario(blobFilePerFirma);
                    break;

                case "crlFromDnKeyId":
                    parerCRL = testCrlFromDnKeyId(crlDN, crlKeyId);
                    break;

                case "addCrlFromUrls":
                    parerCRL = testAddCrlFromUrls(urls);
                    break;
                default:
                    break;

                }
                tx.commit();
            } catch (FileUploadException ex) {
                try {
                    dettaglioErrore = "Errore nell'upload. La transazione è in stato: "
                            + decodeTxStatus(tx.getStatus());
                    log.error(dettaglioErrore, ex);
                } catch (SystemException e) {
                    log.error("Eccezione di sistema", e);
                }
            } catch (Exception ex) {
                try {
                    dettaglioErrore = "Errore generico. La transazione è in stato: " + decodeTxStatus(tx.getStatus());
                    log.error(dettaglioErrore, ex);
                } catch (SystemException e) {
                    log.error("Eccezione di sistema", e);
                }
            } finally {
                if (parerCRL != null) {
                    request.setAttribute("message", parerCRL);
                } else {
                    request.setAttribute("errore", "Impossibile recuperare la CRL. " + dettaglioErrore);
                }

                request.getRequestDispatcher("GestioneCRL.jsp").forward(request, response);

            }
        }
    }

    private static String decodeTxStatus(int status) {
        String hrStatus;

        switch (status) {
        case Status.STATUS_ACTIVE:
            hrStatus = "attiva";
            break;
        case Status.STATUS_MARKED_ROLLBACK:
            hrStatus = "marcata per il rollback";
            break;
        case Status.STATUS_PREPARED:
            hrStatus = "preparata";
            break;
        case Status.STATUS_COMMITTED:
            hrStatus = "commit effettuato";
            break;
        case Status.STATUS_ROLLEDBACK:
            hrStatus = "effettuato il rollback";
            break;
        case Status.STATUS_UNKNOWN:
            hrStatus = "sconosciuta";
            break;
        case Status.STATUS_NO_TRANSACTION:
            hrStatus = "non esiste alcuna transazione";
            break;
        case Status.STATUS_PREPARING:
            hrStatus = "in preparazione";
            break;
        case Status.STATUS_COMMITTING:
            hrStatus = "commit in corso";
            break;
        case Status.STATUS_ROLLING_BACK:
            hrStatus = "rollback in corso";
            break;
        default:
            hrStatus = "indefinito";
        }
        return hrStatus;
    }

    private ParerCRL testCrlFromFirmatario(byte[] blobFilePerFirma) throws IOException, FileNotFoundException {
        ParerCRL result = null;

        if (blobFilePerFirma != null) {
            result = cryInv.retrieveCRL(blobFilePerFirma);
        }

        return result;
    }

    private ParerCRL testCrlFromDnKeyId(String crlDN, String crlKeyId) {
        ParerCRL result = null;

        if (crlDN != null && crlKeyId != null) {
            result = cryInv.retrieveCRL(crlDN, crlKeyId);
        }
        return result;
    }

    private ParerCRL testAddCrlFromUrls(List<String> urls) {
        ParerCRL result = null;
        if (urls != null) {
            result = cryInv.addCrlByURL(urls);
        }

        return result;
    }

    // <editor-fold defaultstate="collapsed" desc="STD">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * 
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * 
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Test delle invocazioni alle CRL";
    }// </editor-fold>
}
