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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.crypto.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.crypto.model.ParerTSD;
import it.eng.parer.crypto.model.ParerTST;
import it.eng.parer.firma.crypto.verifica.CryptoInvoker;
import it.eng.parer.firma.crypto.verifica.SpringTikaSingleton;

/**
 *
 * @author Quaranta_M
 */
@WebServlet(urlPatterns = { "/TestMarcatura" }, asyncSupported = true)
public class TestMarcatura extends HttpServlet {

    private static final long serialVersionUID = 1L;

    Logger log = LoggerFactory.getLogger(TestMarcatura.class);
    @EJB
    private CryptoInvoker cryInv;
    @EJB
    private SpringTikaSingleton ejbFormati;

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
        // PrintWriter out = response.getWriter();
        File contDaMarcare = null;
        File contTika = null;
        Boolean tsd = false;
        try {
            if (ServletFileUpload.isMultipartContent(request)) {
                // Create a factory for disk-based file items
                DiskFileItemFactory factory = new DiskFileItemFactory();

                // Set factory constraints
                factory.setSizeThreshold(4096);
                String basePath = System.getProperty("java.io.tmpdir");
                factory.setRepository(new File(basePath));

                // Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);

                // Set overall request size constraint
                upload.setSizeMax(1000 * 1000 * 300);
                try {
                    // Parse the request
                    List<FileItem> items = upload.parseRequest(request);
                    Iterator iter = items.iterator();
                    while (iter.hasNext()) {
                        FileItem item = (FileItem) iter.next();
                        if (item.getFieldName().equals("contDaMarcare") && !"".equals(item.getName())) {
                            contDaMarcare = new File(basePath + "/" + FilenameUtils.getName(item.getName()));
                            item.write(contDaMarcare);
                            request.setAttribute("contDaMarcare", item.getName());
                        } else if (item.getFieldName().equals("contFormato") && !"".equals(item.getName())) {
                            contTika = new File(basePath + "/" + FilenameUtils.getName(item.getName()));
                            item.write(contTika);
                            request.setAttribute("contFormato", item.getName());
                        } else if (item.getFieldName().equals("startMarcaturaTSD") && !"".equals(item.getName())) {
                            tsd = true;
                        }

                    }
                } catch (Exception ex) {
                    log.error("Errore nell'upload :", ex);
                }

            }

            if (contDaMarcare != null) {
                // CMSTimeStampedData tsdData = null;
                // TimeStampToken tst = null;
                ParerTSD tsdData = null;
                ParerTST tst = null;
                if (tsd) {
                    try {
                        tsdData = cryInv.generateTSD(FileUtils.readFileToByteArray(contDaMarcare));
                        request.setAttribute("tstTime",
                                tsdData.getTimeStampTokens()[0].getTimeStampInfo().getGenTime());
                    } catch (Exception ex) {
                        log.error("Errore nell'acquisizione della marca temporale", ex);
                    }
                } else {
                    try {
                        tst = cryInv.requestTST(FileUtils.readFileToByteArray(contDaMarcare));
                        request.setAttribute("tstTime", tst.getTimeStampInfo().getGenTime());
                    } catch (Exception ex) {
                        log.error("Errore nell'acquisizione della marca temporale", ex);
                    }
                }

                String filename = FilenameUtils.getName(contDaMarcare.getName()) + (tsd ? ".tsd" : ".tsr");

                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;filename=" + filename);

                ByteArrayInputStream is = new ByteArrayInputStream(
                        tst == null ? tsdData.getEncoded() : tst.getEncoded());
                int read;

                byte[] bytes = new byte[1024];
                OutputStream os = response.getOutputStream();
                while ((read = is.read(bytes)) != -1) {
                    os.write(bytes, 0, read);
                }

                os.flush();
                os.close();
            } else if (contTika != null) {
                String detectMimeType = ejbFormati.detectMimeType(Files.readAllBytes(contTika.toPath()));
                request.setAttribute("mimeType", detectMimeType);
                // Tika tika = ejbFormati.getTikaInstance();
                // Metadata metadata = new Metadata();
                // try (InputStream stream = TikaInputStream.get(contTika.toURI().toURL(), metadata)) {
                // metadata.set(Metadata.RESOURCE_NAME_KEY, null);
                // String text = tika.detect(stream, metadata);
                // request.setAttribute("mimeType", text);
                // }

                request.getRequestDispatcher("JobMarche.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("JobMarche.jsp").forward(request, response);
            }

        } finally {
            if (contDaMarcare != null && contDaMarcare.exists()) {
                Files.delete(contDaMarcare.toPath());
            }
            if (contTika != null && contTika.exists()) {
                Files.delete(contTika.toPath());
            }
        }
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
        return "Short description";
    }// </editor-fold>
}
