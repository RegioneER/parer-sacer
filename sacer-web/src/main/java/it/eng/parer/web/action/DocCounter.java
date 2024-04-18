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

package it.eng.parer.web.action;

import it.eng.parer.web.dto.CounterResultBean;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.spagoCore.error.EMFError;
import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Bertuzzi_M
 */

@RestController
@RequestMapping(path = "/rest")
public class DocCounter {

    private MonitoraggioHelper monitoraggioHelper;

    private static final Logger logger = LoggerFactory.getLogger(DocCounter.class);

    @PostConstruct
    public void init() {
        try {
            monitoraggioHelper = (MonitoraggioHelper) new InitialContext()
                    .lookup("java:app/Parer-ejb/MonitoraggioHelper");
        } catch (NamingException ex) {
            logger.error("Errore nel recupero dell'EJB ConfigurationHelper ", ex);
            throw new IllegalStateException(ex);
        }
    }

    @RequestMapping(value = "/docounter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CounterResultBean> countDocs() throws EMFError {
        CounterResultBean result = monitoraggioHelper.getTotalMonTotSacer();
        ResponseEntity<CounterResultBean> response = new ResponseEntity<CounterResultBean>(result, HttpStatus.OK);
        return response;
    }

}
