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
package it.eng.parer.web.util;

/**
 *
 * @author Quaranta_M (40M o "FortyEm")
 */
public class Constants {

    public static final int PASSWORD_EXPIRATION_DAYS = 90;
    public static final String SACER = "SACER";
    // Constants for Transformer
    public static final String ENTITY_PACKAGE_NAME = "it.eng.parer.entity";
    public static final String GRANTED_ENTITY_PACKAGE_NAME = "it.eng.parer.grantedEntity";
    public static final String VIEWENTITY_PACKAGE_NAME = "it.eng.parer.viewEntity";
    public static final String GRANTED_VIEWENTITY_PACKAGE_NAME = "it.eng.parer.grantedViewEntity";
    public static final String ROWBEAN_PACKAGE_NAME = "it.eng.parer.slite.gen.tablebean";
    public static final String VIEWROWBEAN_PACKAGE_NAME = "it.eng.parer.slite.gen.viewbean";
    // Costanti per lista "Totale Definito Da" nella gestione dei dati specifici
    public static final String TI_UNI_DOC = "Tipo unità doc.";
    public static final String TI_DOC = "Tipo doc.";
    public static final String TI_SIS_MIGR_UD = "Migraz. unità doc.";
    public static final String TI_SIS_MIGR_DOC = "Migraz. doc.";
    // Costanti per dati specifici
    public static final String TI_USO_XSD_VERS = "VERS";
    public static final String TI_USO_XSD_MIGR = "MIGRAZ";

    // Formato data/ora
    public static final String DATE_FORMAT_TIMESTAMP_TYPE = "dd/MM/yyyy HH:mm:ss";
    public static final String DATE_FORMAT_DATE_TYPE = "dd/MM/yyyy";
    public static final String DATE_FORMAT_HOUR_MINUTE_TYPE = "dd/MM/yyyy HH:mm";
    public static final String DATE_FORMAT_DAY_MONTH_TYPE = "dd/MM";
    public static final String DATE_FORMAT_DATE_COMPACT_TYPE = "dd/MM/yy";

    // Start of the days
    public static final String START_DAYS_HH_MM_SS = "00:00:00";
    // Start of the years
    public static final String START_YEARS_YYYY_MM_DD = "2001/01/01";
    // Start of the millennium
    public static final String START_MILLENNIUM_YYYY = "2000";

    // Enum per tipo sessione
    public enum TipoSessione {

        VERSAMENTO, AGGIUNGI_DOCUMENTO
    }

    // Enum per tipo entità sacer
    public enum TipoEntitaSacer {

        UNI_DOC, DOC, UPD, COMP, FASC, SER
    }
    // Enum per tipo sistema migrazione

    public enum TipoSisMigr {

        ASC
    }

    // Enum per esito su calcoli nel monitoraggio
    public enum EsitoCalcolo {

        OK, OKNOUPDATE
    }

    // Enum per tipo dato
    public enum TipoDato {

        REGISTRO, TIPO_UNITA_DOC, TIPO_DOC, TIPO_DOC_PRINC, SUB_STRUTTURA, TIPO_FASCICOLO, TIPO_OBJECT
    }

    public enum TiOperReplic {

        INS, MOD, CANC
    }

    public enum TiStatoReplic {

        DA_REPLICARE, REPLICA_OK, REPLICA_NON_POSSIBILE, REPLICA_IN_ERRORE, REPLICA_IN_TIMEOUT
    }

    /*
     * NOTA PAOLO: Probabilmente da "fondere" in un unicop enum con gli altri presenti altrove ma non legati alla parte
     * web
     */
    public enum TiDoc {

        PRINCIPALE, ALLEGATO, ANNESSO, ANNOTAZIONE
    }

    // Enum per tipo entità sacer su object storage
    public enum TiEntitaSacerObjectStorage {

        COMP_DOC, REPORTVF, XML_VERS, INDICE_AIP
    }

    // MEV26587
    public enum ComboValueParamentersType {
        STRINGA, PASSWORD;
    }

    public static final String OBFUSCATED_STRING = "********";

    // MEV#27891
    // Enum per tipo sessione jms
    public enum TipoSessioneJMS {
        XA_TRANSACTED, SESSION_TRANSACTED, UNTRANSACTED, DISABLED
    }

    // Enum per modalità di delivery messaggio jms
    public enum DeliveryModeJMS {
        PERSISTENT, NON_PERSISTENT
    }
    // end MEV#27891

    // MAC#28020
    // Enum per modalità di validazione criteri
    public enum ValidationMode {
        JOB, MDB
    }
    // end MAC#28020
}
