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

package it.eng.parer.entity.constraint;

/**
 *
 * @author Iacolucci_M
 */
public final class OrgPartition {

    private OrgPartition() {
    }

    public enum TiPartition {
        AIP_UD, BLOB, FILE_SES, SES, UNI_DOC, UNI_DOC_SUB_STRUT, FILE, XML_SES, FASCICOLO, FASCICOLO_ERR, FASCICOLO_KO,
        SES_FASCICOLO_KO, XML_FASCICOLO, XML_VERS_FASCICOLO, XML_SES_FASCICOLO_KO, FILE_META_VER_AIP_FASC,
        FILE_ELENCHI_VERS, FILE_ELENCO_VERS_FASC, FILE_SER, FILE_VOL_SER, VERS_INI_DATI_SPEC, UPD_DATI_SPEC_UNITA_DOC,
        XML_UPD_UNITA_DOC, UPD_UNITA_DOC_KO, SES_UPD_UNITA_DOC_KO, XML_SES_UPD_UNITA_DOC_KO, SES_UPD_UNITA_DOC_ERR
    }

}
