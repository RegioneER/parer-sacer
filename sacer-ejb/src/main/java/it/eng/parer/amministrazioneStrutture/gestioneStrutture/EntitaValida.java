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

package it.eng.parer.amministrazioneStrutture.gestioneStrutture;

import it.eng.parer.util.Utils;
import java.util.Date;

/**
 * @author Iacolucci_M
 * 
 *         Classe che determina se un'entità deve essere considerata (flag valida==true) e quale deve essere la
 *         risultante data istituzione (dataInizio) e data soppressione (dataFine). per ogni entità che si intende
 *         processare si passano al costruttore i due flag che dicono come si deve comportare l'oggetto, la data
 *         istituzione e soppressione originali del vecchio oggetto e la data attuale di riferimento. Questa classe
 *         determina poi se l'oggetto è valido e quali devono essere le date giuste da mettere nel nuovo oggetto che si
 *         andrà a creare come importazione/duplicazione del vecchio.
 * 
 */
public class EntitaValida {

    private boolean valida; // Entità NON VALIDA di DEFALUT!
    private Date dataInizio;
    private Date dataFine;

    public EntitaValida(boolean includiEntitaDisattiva, boolean mantieniDateFineValiditaOriginale, Date dataIstituzione,
            Date dataSoppressione, Date dataAttuale) {
        // Se dataIstituzione dell'entità è antecedente o uguale alla data attuale
        // e la data soppressione è uguale o maggiore di quella attuale
        dataInizio = dataAttuale; // data inizio sempre uguale alla data odierna in tutti i casi
        if (dataIstituzione == null) {
            dataIstituzione = dataAttuale;
        }
        if (dataSoppressione == null) {
            dataSoppressione = Utils.getDataInfinita();
        }
        /*
         * Se la data inizio è uguale o antecedente ad adesso e la data fine è uguale o successiva ad adesso l'oggetto è
         * ancora attivo
         */
        if ((dataIstituzione.before(dataAttuale) || dataIstituzione.equals(dataAttuale))
                && (dataSoppressione.after(dataAttuale) || dataSoppressione.equals(dataAttuale))) {
            valida = true;
            if (mantieniDateFineValiditaOriginale) {
                dataFine = dataSoppressione;
            } else {
                dataFine = Utils.getDataInfinita();
            }
        } else {
            /*
             * Se invece l'oggetto non è attivo:
             * 
             * Se la data inizio è successiva a adesso e la data fine è successiva ad adesso:
             */
            if (dataIstituzione.after(dataAttuale) && dataSoppressione.after(dataAttuale)) {
                // Entità attiva nel futuro
                if (includiEntitaDisattiva) {
                    valida = true;
                    if (mantieniDateFineValiditaOriginale) {
                        dataFine = dataSoppressione;
                    } else {
                        dataFine = Utils.getDataInfinita();
                    }
                }
                /*
                 * Se invece data inizio è prima di adesso e anche la data fine:
                 */
            } else if (dataIstituzione.before(dataAttuale) && dataSoppressione.before(dataAttuale)) {
                // Entità attiva nel passato
                if (includiEntitaDisattiva) {
                    valida = true;
                    dataFine = Utils.getDataInfinita();
                }
            }
        }
    }

    public boolean isValida() {
        return valida;
    }

    public void setValida(boolean valida) {
        this.valida = valida;
    }

    public Date getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(Date dataInizio) {
        this.dataInizio = dataInizio;
    }

    public Date getDataFine() {
        return dataFine;
    }

    public void setDataFine(Date dataFine) {
        this.dataFine = dataFine;
    }
}
