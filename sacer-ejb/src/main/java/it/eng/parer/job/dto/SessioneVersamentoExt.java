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
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.job.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.eng.parer.entity.AroDoc;
import it.eng.parer.web.util.Constants;

/**
 *
 * @author Fioravanti_F
 */
public class SessioneVersamentoExt {

    public class DatiXml {

        private String tipoXmlDati;
        private String versione;
        private String xml;
        private String urn;
        private String hash;
        private String algoritmo;
        private String encoding;

        /**
         * vedi CostantiDB.TipiXmlDati
         *
         * @return String
         */
        public String getTipoXmlDati() {
            return tipoXmlDati;
        }

        public void setTipoXmlDati(String tipoXmlDati) {
            this.tipoXmlDati = tipoXmlDati;
        }

        public String getVersione() {
            return versione;
        }

        public void setVersione(String versione) {
            this.versione = versione;
        }

        public String getXml() {
            return xml;
        }

        public void setXml(String xml) {
            this.xml = xml;
        }

        public String getUrn() {
            return urn;
        }

        public void setUrn(String urn) {
            this.urn = urn;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getAlgoritmo() {
            return algoritmo;
        }

        public void setAlgoritmo(String algoritmo) {
            this.algoritmo = algoritmo;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

    }

    long idSessioneVers;
    private Constants.TipoSessione tipoSessione;
    private Date dataSessioneVers;
    private List<DatiXml> xmlDatiSessioneVers;
    private List<AroDoc> documentiVersati;
    private Long idUnitaDoc;
    private Long idDoc;

    public SessioneVersamentoExt() {
        xmlDatiSessioneVers = new ArrayList<>();
        documentiVersati = new ArrayList<>();
    }

    public long getIdSessioneVers() {
        return idSessioneVers;
    }

    public void setIdSessioneVers(long idSessioneVers) {
        this.idSessioneVers = idSessioneVers;
    }

    public Constants.TipoSessione getTipoSessione() {
        return tipoSessione;
    }

    public void setTipoSessione(Constants.TipoSessione tipoSessione) {
        this.tipoSessione = tipoSessione;
    }

    public Date getDataSessioneVers() {
        return dataSessioneVers;
    }

    public void setDataSessioneVers(Date dataSessioneVers) {
        this.dataSessioneVers = dataSessioneVers;
    }

    public List<DatiXml> getXmlDatiSessioneVers() {
        return xmlDatiSessioneVers;
    }

    public void setXmlDatiSessioneVers(List<DatiXml> xmlDatiSessioneVers) {
        this.xmlDatiSessioneVers = xmlDatiSessioneVers;
    }

    public List<AroDoc> getDocumentiVersati() {
        return documentiVersati;
    }

    public void setDocumentiVersati(List<AroDoc> documentiVersati) {
        this.documentiVersati = documentiVersati;
    }

    /**
     * @return the idUnitaDoc
     */
    public Long getIdUnitaDoc() {
        return idUnitaDoc;
    }

    /**
     * @param idUnitaDoc the idUnitaDoc to set
     */
    public void setIdUnitaDoc(Long idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    /**
     * @return the idDoc
     */
    public Long getIdDoc() {
        return idDoc;
    }

    /**
     * @param idDoc the idDoc to set
     */
    public void setIdDoc(Long idDoc) {
        this.idDoc = idDoc;
    }

}
