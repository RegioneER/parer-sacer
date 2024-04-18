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
package it.eng.parer.ws.versamento.dto;

import java.util.HashMap;
import java.util.List;

import it.eng.parer.elenco.xml.indice.DocumentoType;
import it.eng.parer.ws.utils.Costanti.CategoriaDocumento;

/**
 *
 * @author Fioravanti_F
 */
public class DocumentoVers implements java.io.Serializable, IDatiSpecEntity {

    private static final long serialVersionUID = 1L;
    private int progressivo;
    private CategoriaDocumento categoriaDoc;
    private String urnPartDocumento = "";
    private DocumentoType rifDocumento;
    private HashMap<String, DatoSpecifico> datiSpecifici;
    private HashMap<String, DatoSpecifico> datiSpecificiMigrazione;
    private long idRecXsdDatiSpec;
    private long idRecXsdDatiSpecMigrazione;
    private List<ComponenteVers> fileAttesi;
    // id salvataggio di se stesso su db
    private long idRecDocumentoDB;
    // id salvataggio della strutturaDoc (rappresentazione)
    private long idRecStrutturaDB;
    // id: <TipoDocumento>
    private long idTipoDocumentoDB;
    // id: <StrutturaOriginale><TipoStruttura>
    private long idTipoStrutturaDB;
    private String tiEsitoVerifFirme;
    private String dsMsgEsitoVerifica;
    private String flFileFirmato;

    /**
     * @return the progressivo
     */
    public int getProgressivo() {
        return progressivo;
    }

    /**
     * @param progressivo
     *            the progressivo to set
     */
    public void setProgressivo(int progressivo) {
        this.progressivo = progressivo;
    }

    /**
     * @return the categoriaDoc
     */
    public CategoriaDocumento getCategoriaDoc() {
        return categoriaDoc;
    }

    /**
     * @param categoriaDoc
     *            the categoriaDoc to set
     */
    public void setCategoriaDoc(CategoriaDocumento categoriaDoc) {
        this.categoriaDoc = categoriaDoc;
    }

    public String getUrnPartDocumento() {
        return urnPartDocumento;
    }

    public void setUrnPartDocumento(String urnPartDocumento) {
        this.urnPartDocumento = urnPartDocumento;
    }

    /**
     * @return the rifDocumento
     */
    public DocumentoType getRifDocumento() {
        return rifDocumento;
    }

    /**
     * @param rifDocumento
     *            the rifDocumento to set
     */
    public void setRifDocumento(DocumentoType rifDocumento) {
        this.rifDocumento = rifDocumento;
    }

    /**
     * @return the idRecDocumentoDB
     */
    public long getIdRecDocumentoDB() {
        return idRecDocumentoDB;
    }

    /**
     * @param idRecDocumentoDB
     *            the idRecDocumentoDB to set
     */
    public void setIdRecDocumentoDB(long idRecDocumentoDB) {
        this.idRecDocumentoDB = idRecDocumentoDB;
    }

    /**
     * @return the idRecStrutturaDB
     */
    public long getIdRecStrutturaDB() {
        return idRecStrutturaDB;
    }

    /**
     * @param idRecStrutturaDB
     *            the idRecStrutturaDB to set
     */
    public void setIdRecStrutturaDB(long idRecStrutturaDB) {
        this.idRecStrutturaDB = idRecStrutturaDB;
    }

    /**
     * @return the datiSpecDocumento
     */
    @Override
    public HashMap<String, DatoSpecifico> getDatiSpecifici() {
        return datiSpecifici;
    }

    /**
     * @param datiSpecifici
     *            the datiSpecDocumento to set
     */
    @Override
    public void setDatiSpecifici(HashMap<String, DatoSpecifico> datiSpecifici) {
        this.datiSpecifici = datiSpecifici;
    }

    @Override
    public HashMap<String, DatoSpecifico> getDatiSpecificiMigrazione() {
        return datiSpecificiMigrazione;
    }

    @Override
    public void setDatiSpecificiMigrazione(HashMap<String, DatoSpecifico> datiSpecificiMigrazione) {
        this.datiSpecificiMigrazione = datiSpecificiMigrazione;
    }

    @Override
    public long getIdRecXsdDatiSpec() {
        return idRecXsdDatiSpec;
    }

    @Override
    public long getIdRecXsdDatiSpecMigrazione() {
        return idRecXsdDatiSpecMigrazione;
    }

    @Override
    public void setIdRecXsdDatiSpec(long idRecXsdDatiSpec) {
        this.idRecXsdDatiSpec = idRecXsdDatiSpec;
    }

    @Override
    public void setIdRecXsdDatiSpecMigrazione(long idRecXsdDatiSpec) {
        this.idRecXsdDatiSpecMigrazione = idRecXsdDatiSpec;
    }

    /**
     * @return the fileAttesi
     */
    public List<ComponenteVers> getFileAttesi() {
        return fileAttesi;
    }

    /**
     * @param fileAttesi
     *            the fileAttesi to set
     */
    public void setFileAttesi(List<ComponenteVers> fileAttesi) {
        this.fileAttesi = fileAttesi;
    }

    /**
     * @return the idTipoDocumentoDB
     */
    public long getIdTipoDocumentoDB() {
        return idTipoDocumentoDB;
    }

    /**
     * @param idTipoDocumentoDB
     *            the idTipoDocumentoDB to set
     */
    public void setIdTipoDocumentoDB(long idTipoDocumentoDB) {
        this.idTipoDocumentoDB = idTipoDocumentoDB;
    }

    /**
     * @return the idTipoStrutturaDB
     */
    public long getIdTipoStrutturaDB() {
        return idTipoStrutturaDB;
    }

    /**
     * @param idTipoStrutturaDB
     *            the idTipoStrutturaDB to set
     */
    public void setIdTipoStrutturaDB(long idTipoStrutturaDB) {
        this.idTipoStrutturaDB = idTipoStrutturaDB;
    }

    /**
     * @return the dsMsgEsitoVerifica
     */
    public String getDsMsgEsitoVerifica() {
        return dsMsgEsitoVerifica;
    }

    /**
     * @param dsMsgEsitoVerifica
     *            the dsMsgEsitoVerifica to set
     */
    public void setDsMsgEsitoVerifica(String dsMsgEsitoVerifica) {
        this.dsMsgEsitoVerifica = dsMsgEsitoVerifica;
    }

    /**
     * @return the flFileFirmato
     */
    public String getFlFileFirmato() {
        return flFileFirmato;
    }

    /**
     * @param flFileFirmato
     *            the flFileFirmato to set
     */
    public void setFlFileFirmato(String flFileFirmato) {
        this.flFileFirmato = flFileFirmato;
    }

    /**
     * @return the tiEsitoVerifFirme
     */
    public String getTiEsitoVerifFirme() {
        return tiEsitoVerifFirme;
    }

    /**
     * @param tiEsitoVerifFirme
     *            the tiEsitoVerifFirme to set
     */
    public void setTiEsitoVerifFirme(String tiEsitoVerifFirme) {
        this.tiEsitoVerifFirme = tiEsitoVerifFirme;
    }
}
