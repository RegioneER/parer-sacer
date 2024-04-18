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

package it.eng.parer.web.validator;

import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.message.MessageBox;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioAggMetaValidator extends TypeValidator {

    public MonitoraggioAggMetaValidator(MessageBox messageBox) {
        super(messageBox);
    }

    public Date[] validaDate(Date data_da, BigDecimal ore_da, BigDecimal minuti_da, Date data_a, BigDecimal ore_a,
            BigDecimal minuti_a, String nm_data_da, String nm_data_a) throws EMFError {
        Date[] dateValidate = super.validaDate(data_da, ore_da, minuti_da, data_a, ore_a, minuti_a, nm_data_da,
                nm_data_a);

        if (dateValidate != null) {
            Calendar dataDa = Calendar.getInstance();
            dataDa.setTime(dateValidate[0]);
            dataDa.set(Calendar.MILLISECOND, 0);
            dateValidate[0] = dataDa.getTime();
            Calendar dataA = Calendar.getInstance();
            dataA.setTime(dateValidate[1]);
            dataA.set(Calendar.MILLISECOND, 999);
            dateValidate[1] = dataA.getTime();
        }
        // Ritorno le date validate
        return dateValidate;
    }

}
