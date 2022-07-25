/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recupero.dto;

import it.eng.parer.ws.dto.IWSDesc;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recuperoTpi.dto.DatiSessioneRecupero;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.Costanti.ModificatoriWS;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipoSalvataggioFile;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import java.util.EnumSet;
import java.util.HashMap;

/**
 *
 * @author Fioravanti_F
 */
public class RecuperoExt implements IRecuperoExt {

    private static final long serialVersionUID = 5261426459498072293L;
    private String datiXml;
    private Recupero strutturaRecupero;
    private ParametriRecupero parametriRecupero;
    private ParametriParser parametriParser;
    private DatiSessioneRecupero datiSessioneRecupero;
    private String versioneWsChiamata;
    private String loginName;
    private long idStruttura;
    private CostantiDB.TipoSalvataggioFile tipoSalvataggioFile;
    private IWSDesc descrizione;
    //
    private boolean tpiAbilitato = false;
    private String tpiRootTpi;
    private String tpiRootTpiDaSacer;
    private String tpiRootRecup;
    private String tpiListaFile;
    //
    private String subPathVersatoreArk;
    private String subPathUnitaDocArk;
    private String fileLogRetrieve;
    //
    private String dipRootImg;
    //
    private String versioneCalc = null;
    private EnumSet<ModificatoriWS> modificatoriWS = EnumSet.noneOf(Costanti.ModificatoriWS.class);
    //
    private HashMap<String, String> wsVersions;

    @Override
    public String getDatiXml() {
        return datiXml;
    }

    @Override
    public void setDatiXml(String datiXml) {
        this.datiXml = datiXml;
    }

    @Override
    public Recupero getStrutturaRecupero() {
        return strutturaRecupero;
    }

    @Override
    public void setStrutturaRecupero(Recupero strutturaRecupero) {
        this.strutturaRecupero = strutturaRecupero;
    }

    public ParametriRecupero getParametriRecupero() {
        return parametriRecupero;
    }

    public void setParametriRecupero(ParametriRecupero parametriRecupero) {
        this.parametriRecupero = parametriRecupero;
    }

    public ParametriParser getParametriParser() {
        return parametriParser;
    }

    public void setParametriParser(ParametriParser parametriParser) {
        this.parametriParser = parametriParser;
    }

    public DatiSessioneRecupero getDatiSessioneRecupero() {
        return datiSessioneRecupero;
    }

    public void setDatiSessioneRecupero(DatiSessioneRecupero datiSessioneRecupero) {
        this.datiSessioneRecupero = datiSessioneRecupero;
    }

    @Override
    public IWSDesc getDescrizione() {
        return descrizione;
    }

    @Override
    public void setDescrizione(IWSDesc descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public String getLoginName() {
        return loginName;
    }

    @Override
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String getVersioneWsChiamata() {
        return versioneWsChiamata;
    }

    @Override
    public void setVersioneWsChiamata(String versioneWsChiamata) {
        this.versioneWsChiamata = versioneWsChiamata;
    }

    public long getIdStruttura() {
        return idStruttura;
    }

    public void setIdStruttura(long idStruttura) {
        this.idStruttura = idStruttura;
    }

    public TipoSalvataggioFile getTipoSalvataggioFile() {
        return tipoSalvataggioFile;
    }

    public void setTipoSalvataggioFile(TipoSalvataggioFile tipoSalvataggioFile) {
        this.tipoSalvataggioFile = tipoSalvataggioFile;
    }

    //
    @Override
    public RispostaControlli checkVersioneRequest(String versione) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        versioneCalc = versione;
        modificatoriWS = EnumSet.noneOf(Costanti.ModificatoriWS.class);
        if (versione.equals("1.2")) {
            this.versioneCalc = "1.2";
            this.modificatoriWS.add(ModificatoriWS.TAG_REC_USR_DOC_COMP);
        } else if (versione.equals("1.1")) {
            this.versioneCalc = "1.1";
        } else {
            this.versioneCalc = "1.1";
        }

        return rispostaControlli;
    }

    @Override
    public EnumSet<ModificatoriWS> getModificatoriWSCalc() {
        return this.modificatoriWS;
    }

    @Override
    public String getVersioneCalc() {
        return this.versioneCalc;
    }

    //
    public boolean isTpiAbilitato() {
        return tpiAbilitato;
    }

    public void setTpiAbilitato(boolean tpiAbilitato) {
        this.tpiAbilitato = tpiAbilitato;
    }

    public String getTpiRootTpi() {
        return tpiRootTpi;
    }

    public void setTpiRootTpi(String tpiRootTpi) {
        this.tpiRootTpi = tpiRootTpi;
    }

    public String getTpiRootTpiDaSacer() {
        return tpiRootTpiDaSacer;
    }

    public void setTpiRootTpiDaSacer(String tpiRootTpiDaSacer) {
        this.tpiRootTpiDaSacer = tpiRootTpiDaSacer;
    }

    public String getTpiRootRecup() {
        return tpiRootRecup;
    }

    public void setTpiRootRecup(String tpiRootRecup) {
        this.tpiRootRecup = tpiRootRecup;
    }

    public String getTpiListaFile() {
        return tpiListaFile;
    }

    public void setTpiListaFile(String tpiListaFile) {
        this.tpiListaFile = tpiListaFile;
    }

    public String getSubPathVersatoreArk() {
        return subPathVersatoreArk;
    }

    public void setSubPathVersatoreArk(String subPathVersatoreArk) {
        this.subPathVersatoreArk = subPathVersatoreArk;
    }

    public String getSubPathUnitaDocArk() {
        return subPathUnitaDocArk;
    }

    public void setSubPathUnitaDocArk(String subPathUnitaDocArk) {
        this.subPathUnitaDocArk = subPathUnitaDocArk;
    }

    public String getFileLogRetrieve() {
        return fileLogRetrieve;
    }

    public void setFileLogRetrieve(String fileLogRetrieve) {
        this.fileLogRetrieve = fileLogRetrieve;
    }

    public String getDipRootImg() {
        return dipRootImg;
    }

    public void setDipRootImg(String dipRootImg) {
        this.dipRootImg = dipRootImg;
    }

    public HashMap<String, String> getWsVersions() {
        return wsVersions;
    }

    public void setWsVersions(HashMap<String, String> wsVersions) {
        this.wsVersions = wsVersions;
    }

}
