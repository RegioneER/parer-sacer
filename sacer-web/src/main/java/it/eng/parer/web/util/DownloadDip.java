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

package it.eng.parer.web.util;

import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.dto.WSDescRecUDPdf;
import it.eng.parer.ws.recupero.ejb.RecuperoSync;
import it.eng.parer.ws.recuperoDip.dto.CompRecDip;
import it.eng.parer.ws.recuperoDip.ejb.RecuperoDip;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.security.User;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadDip {

    private static Logger logger = LoggerFactory.getLogger(DownloadDip.class.getName());

    private final User user;
    private final BigDecimal idUnitaDoc;

    private CostantiDB.TipiEntitaRecupero tipoEntitaSacer;
    private BigDecimal idCompDoc;
    private BigDecimal idDoc;
    RispostaWSRecupero rispostaWs;
    RecuperoExt recuperoExt;

    RecuperoDip recuperoDip;
    RecuperoSync recuperoSync;

    public enum TIPO_DOWNLOAD {

	SCARICA_ZIP, SCARICA_COMP_CONV
    }

    public DownloadDip(User user, BigDecimal idUnitaDoc) throws NamingException {
	this.user = user;
	this.idUnitaDoc = idUnitaDoc;

	this.rispostaWs = new RispostaWSRecupero();
	this.recuperoExt = new RecuperoExt();
	recuperoExt.setDescrizione(new WSDescRecUDPdf());

	AvanzamentoWs tmpAvanzamento = AvanzamentoWs.nuovoAvanzamentoWS("prova",
		AvanzamentoWs.Funzioni.RecuperoWeb);
	tmpAvanzamento.logAvanzamento();

	recuperoDip = (RecuperoDip) new InitialContext().lookup("java:app/Parer-ejb/RecuperoDip");
	recuperoSync = (RecuperoSync) new InitialContext()
		.lookup("java:app/Parer-ejb/RecuperoSync");
	tmpAvanzamento.setFase("EJB recuperato").logAvanzamento();

	rispostaWs = new RispostaWSRecupero();
	recuperoSync.initRispostaWs(rispostaWs, tmpAvanzamento, recuperoExt);

	// verifica se l'unità documentaria richiesta contiene file convertibili
	recuperoExt.getParametriRecupero()
		.setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.UNI_DOC_DIP);
	recuperoExt.getParametriRecupero().setUtente(this.user);
	recuperoExt.getParametriRecupero().setIdUnitaDoc(idUnitaDoc.longValue());
    }

    public DownloadDip(RecuperoExt myRecuperoExt, RispostaWSRecupero rispostaWs)
	    throws NamingException {
	this.recuperoExt = myRecuperoExt;
	this.rispostaWs = rispostaWs;
	this.user = myRecuperoExt.getParametriRecupero().getUtente();
	this.idUnitaDoc = new BigDecimal(myRecuperoExt.getParametriRecupero().getIdUnitaDoc());
	this.tipoEntitaSacer = myRecuperoExt.getParametriRecupero().getTipoEntitaSacer();
	this.idCompDoc = myRecuperoExt.getParametriRecupero().getIdComponente() != null
		? new BigDecimal(myRecuperoExt.getParametriRecupero().getIdComponente())
		: null;
	this.idDoc = myRecuperoExt.getParametriRecupero().getIdDocumento() != null
		? new BigDecimal(myRecuperoExt.getParametriRecupero().getIdDocumento())
		: null;

	recuperoDip = (RecuperoDip) new InitialContext().lookup("java:app/Parer-ejb/RecuperoDip");
	recuperoSync = (RecuperoSync) new InitialContext()
		.lookup("java:app/Parer-ejb/RecuperoSync");
    }

    public BaseTable populateComponentiDipTable() throws EMFError {
	BaseTable baseTable = new BaseTable();
	baseTable.setPageSize(10);
	recuperoDip.listaComponenti(getRispostaWs(), getRecuperoExt());
	if (getRispostaWs().getSeverity() == IRispostaWS.SeverityEnum.OK) {
	    if (!getRispostaWs().getDatiRecuperoDip().getElementiTrovati().isEmpty()) {
		baseTable = DownloadDip.generaTableBean(
			getRispostaWs().getDatiRecuperoDip().getElementiTrovati().values());
	    }
	}
	return baseTable;
    }

    public void scaricaDipZip(TIPO_DOWNLOAD tipoDownload) throws EMFError {
	this.recuperoExt.getParametriRecupero()
		.setTipoRichiedente(JobConstants.TipoSessioniRecupEnum.DOWNLOAD);
	switch (tipoDownload) {
	case SCARICA_COMP_CONV:
	    recuperoDip.recuperaCompConvertito(getRispostaWs(), this.recuperoExt,
		    System.getProperty("java.io.tmpdir"));
	    break;
	case SCARICA_ZIP:
	    recuperoDip.recuperaUnitaDocumentaria(getRispostaWs(), this.recuperoExt,
		    System.getProperty("java.io.tmpdir"));
	    break;
	}
    }

    public CostantiDB.TipiEntitaRecupero getTipoEntitaSacer() {
	return tipoEntitaSacer;
    }

    public void setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero tipoEntitaSacer) {
	this.tipoEntitaSacer = tipoEntitaSacer;
	recuperoExt.getParametriRecupero().setTipoEntitaSacer(tipoEntitaSacer);
    }

    public BigDecimal getIdCompDoc() {
	return idCompDoc;
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
	this.idCompDoc = idCompDoc;
	recuperoExt.getParametriRecupero().setIdComponente(idCompDoc.longValue());
    }

    public BigDecimal getIdDoc() {
	return idDoc;
    }

    public void setIdDoc(BigDecimal idDoc) {
	this.idDoc = idDoc;
	recuperoExt.getParametriRecupero().setIdDocumento(idDoc.longValue());
    }

    public RispostaWSRecupero getRispostaWs() {
	return rispostaWs;
    }

    public void setRispostaWs(RispostaWSRecupero rispostaWs) {
	this.rispostaWs = rispostaWs;
    }

    public RecuperoExt getRecuperoExt() {
	return recuperoExt;
    }

    public void setRecuperoExt(RecuperoExt recuperoExt) {
	this.recuperoExt = recuperoExt;
    }

    public BigDecimal getIdUnitaDoc() {
	return idUnitaDoc;
    }

    /*
     ******************
     */

    public static BaseTable generaTableBean(Collection<CompRecDip> collection) {
	BaseTable baseTable = new BaseTable();
	for (CompRecDip compRecDip : collection) {
	    BaseRow row = new BaseRow();
	    row.setBigDecimal("id_comp", new BigDecimal(compRecDip.getIdCompDoc()));
	    row.setObject("nm_comp_doc", compRecDip.getNomeFileBreve());
	    row.setObject("nm_formato_rappr", compRecDip.getNomeFormatoRappresentazione());
	    row.setObject("ds_algo",
		    (compRecDip.getTipoAlgoritmoRappresentazione() != null
			    ? compRecDip.getTipoAlgoritmoRappresentazione().name()
			    : CostantiDB.TipoAlgoritmoRappr.ALTRO.name()));
	    row.setObject("nm_conv", compRecDip.getNomeConvertitore());
	    row.setObject("vrs_conv", compRecDip.getVersioneConvertitore());
	    row.setObject("ti_stato_conv",
		    (compRecDip.getStatoFileTrasform() != null
			    ? compRecDip.getStatoFileTrasform().name()
			    : "---"));
	    row.setTimestamp("dt_comp",
		    (compRecDip.getDataUltimoAggiornamento() != null
			    ? new Timestamp(compRecDip.getDataUltimoAggiornamento().getTime())
			    : new Timestamp(new Date().getTime())));
	    row.setObject("cd_err", compRecDip.getSeverity().name());
	    row.setObject("msg_err", compRecDip.getErrorMessage());
	    baseTable.add(row);
	}
	baseTable.setPageSize(10);
	return baseTable;
    }
}
