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

package it.eng.parer.web.action;

import it.eng.parer.datamart.dto.StatoAvanzamentoCancellazioneFisicaDTO;
import it.eng.parer.datamart.dto.StatoAvanzamentoCancellazioneLogicaDTO;
import it.eng.parer.web.ejb.DataMartEjb;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
// Usiamo un path pulito, non conflittuale
@RequestMapping("/monitoraggio-datamart")
public class DataMartMonitoraggioController {

    private static final Logger logger = LoggerFactory
            .getLogger(DataMartMonitoraggioController.class);
    private DataMartEjb dataMartEjb;

    @PostConstruct
    public void init() {
        try {
            // Usa il lookup manuale che ha funzionato nel test
            this.dataMartEjb = (DataMartEjb) new InitialContext()
                    .lookup("java:app/Parer-ejb/DataMartEjb");
            logger.info(
                    "DataMartMonitoraggioController inizializzato e EJB recuperato con successo.");
        } catch (NamingException ex) {
            logger.error(
                    "ERRORE FATALE durante l'inizializzazione di DataMartMonitoraggioController",
                    ex);
            throw new IllegalStateException("Impossibile recuperare EJB DataMartEjb", ex);
        }
    }

    @GetMapping("/cancellazione-logica-status")
    public ResponseEntity<StatoAvanzamentoCancellazioneLogicaDTO> getCancellazioneLogicaStatus(
            @RequestParam("idUdDelRichiesta") BigDecimal idUdDelRichiesta,
            @RequestParam("idRichiesta") BigDecimal idRichiesta,
            @RequestParam("tiMotivo") String tiMotivo) {

        String tiItemRichSoftDelete = dataMartEjb.getTiItemRichSoftDelete(tiMotivo);

        // Chiama il nuovo metodo di sincronizzazione
        StatoAvanzamentoCancellazioneLogicaDTO dto = dataMartEjb.sincronizzaEcalcolaStatoLogico(
                idUdDelRichiesta, idRichiesta, tiItemRichSoftDelete);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/cancellazione-fisica-status")
    public ResponseEntity<StatoAvanzamentoCancellazioneFisicaDTO> getCancellazioneFisicaStatus(
            @RequestParam("idUdDelRichiesta") BigDecimal idUdDelRichiesta) {
        return ResponseEntity
                .ok(dataMartEjb.calcolaStatoAvanzamentoCancellazioneFisica(idUdDelRichiesta));
    }
}
