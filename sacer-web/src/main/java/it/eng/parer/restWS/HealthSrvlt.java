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
package it.eng.parer.restWS;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.eng.parer.web.dto.HealthActuatorBean;

public class HealthSrvlt extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(HealthSrvlt.class);

    public HealthSrvlt() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.reset();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json; charset=\"utf-8\"");
        ServletOutputStream out = response.getOutputStream();
        OutputStreamWriter tmpStreamWriter = new OutputStreamWriter(out, "UTF-8");
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(tmpStreamWriter, HealthActuatorBean.statusUp());
        } catch (Exception e) {
            log.error("Eccezione nella servlet actuator", e);
        } finally {
            try {
                tmpStreamWriter.close();
            } catch (Exception ei) {
                log.error("Eccezione nella servlet actuator", ei);
            }
            try {
                out.flush();
                out.close();
            } catch (Exception ei) {
                log.error("Eccezione nella servlet actuator", ei);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
