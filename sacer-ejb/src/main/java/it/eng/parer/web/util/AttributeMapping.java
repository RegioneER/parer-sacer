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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.web.util;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Quaranta_M
 */
/**
 * Classe che contiene le liste di attributi esclusi o id inclusi nel copyGroup relativo alla
 * duplicazione/importazione/esportazione di una struttura Tali attributi vengono controllati ed
 * esclusi per le entity accedute in cascade a partire da OrgStrut. Gli attributi relativi a
 * OrgStrut vengono invece esclusi direttamente all'interno dell'entity tramite annotazione
 * XmlTransient
 */
public class AttributeMapping {

    private static final Set<String> list = new HashSet<String>();
    private static final Set<String> idList = new HashSet<String>();

    private AttributeMapping() {
    }

    ;

    // list of attribute not to be copied
    static {
        list.add("aroDocs");
        list.add("aroCompDocs");
        list.add("aroRichAnnulVers");
        list.add("aroStrutDocs");
        list.add("aroUsoXsdDatiSpecs");
        // list.add("aroUnitaDocAnnuls");
        list.add("aroUnitaDocs");
        list.add("aroValoreAttribDatiSpecs");
        list.add("decCampoInpUds");
        list.add("decCampoOutSelUds");
        list.add("decErrAaRegistroUnitaDocs");
        list.add("decFiltroSelUdDatos");
        list.add("decFiltroSelUds");
        list.add("decModelloTipoSeries");
        list.add("decTipoFascicolos");
        list.add("decTipoSeries");
        list.add("decTipoSerieUds");
        list.add("decTitols");
        // list.add("decUsoModelloTipoSeries");
        list.add("decWarnAaRegistroUds");
        list.add("elvElencoVers");
        list.add("elvElencoVersFasc");
        list.add("elvLogElencoVers");
        list.add("fasFascicolos");
        list.add("logOpers");
        list.add("logJobs");
        list.add("logLockElabs");
        list.add("monAaUnitaDocRegistros");
        list.add("monContaUdDocComps");
        list.add("orgCampoValSubStruts");
        list.add("orgEnte");
        // list.add("orgStrut");
        list.add("orgOperTitols");
        list.add("orgPartitionStruts");
        list.add("orgPartitionSubStruts");
        list.add("orgSubStruts");
        list.add("orgUsoSistemaMigrazs");
        list.add("serSeries");
        list.add("usrAppartUserStruts");
        list.add("usrFiltroAttribs");
        list.add("volVolumeConservs");
        list.add("vrsDocNonVers");
        list.add("vrsSessioneVers");
        list.add("vrsUnitaDocNonVers");
        list.add("monTipoUnitaDocUserVers");

    }
    // id list to be copied
    static {
        idList.add("idFormatoFileStandard");
        idList.add("idEstensioneFile");
        idList.add("idFormatoFileBusta");
        idList.add("idAmbitoTerrit");
        idList.add("idCategStrut");
        idList.add("idCategTipoUnitaDoc");
        // idList.add("idTipoRapprComp");
        idList.add("idModelloTipoSerie");
        idList.add("idSistemaVersante");
        idList.add("idTipoServizio");
    }

    public static boolean contains(String s, boolean duplicaOrgSub) {
        boolean isContained = false;
        if (duplicaOrgSub) {
            if (!s.equals("orgSubStruts") && !s.equals("orgRegolaValSubStruts")
                    && !s.equals("orgCampoValSubStruts")) {
                isContained = list.contains(s);
            }
        } else {
            isContained = list.contains(s);
        }
        return isContained;
    }

    public static boolean idListContains(String s) {
        return idList.contains(s);
    }
}
