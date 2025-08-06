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

package it.eng.parer.job.allineamentoOrganizzazioni.utils;

/**
 *
 * @author Gilioli_P
 */
public class CostantiReplicaOrg {

    // COSTANTI DI ERRORE INSERIMENTO, MODIFICA, CANCELLAZIONE ORGANIZZAZIONE MANDATI IN RISPOSTA
    // DAL WS (SacerIAM)
    public static final String SERVIZI_ORG_001 = "SERVIZI-ORG-001";
    public static final String SERVIZI_ORG_002 = "SERVIZI-ORG-002";
    public static final String SERVIZI_ORG_003 = "SERVIZI-ORG-003";
    public static final String SERVIZI_ORG_004 = "SERVIZI-ORG-004";
    public static final String SERVIZI_ORG_005 = "SERVIZI-ORG-005";
    public static final String SERVIZI_ORG_006 = "SERVIZI-ORG-006";
    public static final String SERVIZI_ORG_007 = "SERVIZI-ORG-007";
    public static final String REPLICA_ORG_001 = "REPLICA-ORG-001";

    // Enum per tipo stato replica
    public enum TiOperReplic {

	INS, MOD, CANC
    }

    // Enum per tipo stato replica
    public enum TiStatoReplic {

	DA_REPLICARE, REPLICA_OK, REPLICA_IN_ERRORE, REPLICA_NON_POSSIBILE, REPLICA_IN_TIMEOUT
    }

    public enum EsitoServizio {

	OK, KO, NO_RISPOSTA
    }

}
