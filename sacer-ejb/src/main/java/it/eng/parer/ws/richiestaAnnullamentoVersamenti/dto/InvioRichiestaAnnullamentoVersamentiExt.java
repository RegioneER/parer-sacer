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

package it.eng.parer.ws.richiestaAnnullamentoVersamenti.dto;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;

import it.eng.parer.ws.dto.IWSDesc;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.dto.ParametriParser;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.xml.richAnnullVers.RichiestaAnnullamentoVersamenti;
import it.eng.spagoLite.security.User;

/**
 *
 * @author Gilioli_P
 */
public class InvioRichiestaAnnullamentoVersamentiExt {

    private IWSDesc descrizione;
    private RichiestaAnnullamentoVersamenti richiestaAnnullamentoVersamenti;
    private ParametriParser parametriParser;
    private String versioneCalc = null;
    private String nmUserid;
    private String cdPsw;
    private User user;
    private String xmlRichiesta;
    private Long idStrut;
    private Date dataElaborazione;
    //
    private HashMap<String, String> wsVersions;

    // private String versioneCalc;
    private EnumSet<Costanti.ModificatoriWS> modificatoriWS = EnumSet
	    .noneOf(Costanti.ModificatoriWS.class);

    public IWSDesc getDescrizione() {
	return descrizione;
    }

    public void setDescrizione(IWSDesc descrizione) {
	this.descrizione = descrizione;
    }

    public RichiestaAnnullamentoVersamenti getRichiestaAnnullamentoVersamenti() {
	return richiestaAnnullamentoVersamenti;
    }

    public void setRichiestaAnnullamentoVersamenti(
	    RichiestaAnnullamentoVersamenti richiestaAnnullamentoVersamenti) {
	this.richiestaAnnullamentoVersamenti = richiestaAnnullamentoVersamenti;
    }

    public String getXmlRichiesta() {
	return xmlRichiesta;
    }

    public void setXmlRichiesta(String xmlRichiesta) {
	this.xmlRichiesta = xmlRichiesta;
    }

    public String getVersioneCalc() {
	return versioneCalc;
    }

    public String getNmUserid() {
	return nmUserid;
    }

    public void setNmUserid(String nmUserid) {
	this.nmUserid = nmUserid;
    }

    public String getCdPsw() {
	return cdPsw;
    }

    public void setCdPsw(String cdPsw) {
	this.cdPsw = cdPsw;
    }

    public Long getIdStrut() {
	return idStrut;
    }

    public void setIdStrut(Long idStrut) {
	this.idStrut = idStrut;
    }

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

    public ParametriParser getParametriParser() {
	return parametriParser;
    }

    public void setParametriParser(ParametriParser parametriParser) {
	this.parametriParser = parametriParser;
    }

    public Date getDataElaborazione() {
	return dataElaborazione;
    }

    public void setDataElaborazione(Date dataElaborazione) {
	this.dataElaborazione = dataElaborazione;
    }

    public RispostaControlli checkVersioneRequest(String versione) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(true);

	versioneCalc = versione;
	modificatoriWS = EnumSet.noneOf(Costanti.ModificatoriWS.class);

	this.versioneCalc = versione;
	if (versione.equals("1.4")) {
	    this.modificatoriWS.add(Costanti.ModificatoriWS.TAG_ANNUL_TIPO_ANNUL);
	    this.modificatoriWS.add(Costanti.ModificatoriWS.TAG_ANNUL_FORZA_PING);
	    this.modificatoriWS.add(Costanti.ModificatoriWS.TAG_ANNUL_FASC);
	} else if (versione.equals("1.3")) {
	    this.modificatoriWS.add(Costanti.ModificatoriWS.TAG_ANNUL_FORZA_PING);
	    this.modificatoriWS.add(Costanti.ModificatoriWS.TAG_ANNUL_FASC);
	} else if (!versione.equals("1.0")) {
	    this.modificatoriWS.add(Costanti.ModificatoriWS.TAG_ANNUL_FORZA_PING);
	}

	// if (versione.equals("1.1")) {
	// this.versioneCalc = "1.1";
	// this.modificatoriWS.add(Costanti.ModificatoriWS.TAG_ANNUL_FORZA_PING);
	// } else {
	// this.versioneCalc = "1.0";
	// }

	return rispostaControlli;
    }

    public EnumSet<Costanti.ModificatoriWS> getModificatoriWS() {
	return modificatoriWS;
    }

    public HashMap<String, String> getWsVersions() {
	return wsVersions;
    }

    public void setWsVersions(HashMap<String, String> wsVersions) {
	this.wsVersions = wsVersions;
    }

}
