package it.eng.tpi.dto;

public class RichiestaTpi {

    private TipoRichiesta tipoRichiesta;
    public static final String NM_USER = "nmUser";
    public static final String CD_PSW = "cdPsw";
    public static final String FL_CARTELLA_MIGRAZ = "flCartellaMigraz";
    public static final String DT_VERS = "dtVers";
    public static final String ROOT_DT_VERS = "rootDtVers";
    public static final String TI_RI_ARK = "tiRiArk";
    public static final String DIR_STRUTTURA = "dirStruttura";
    public static final String DIR_UNITA_DOC = "dirUnitaDoc";
    public static final String DT_SCHED = "dtSched";

    public enum TipoRichiesta {

        STATO_ARK_CARTELLA, ELIMINA_CARTELLA_ARK, RETRIEVE_FILE_UNITA_DOC, REGISTRA_CARTELLA_RI_ARK,
        SCHEDULAZIONI_JOB_TPI;
    }

    public enum TipoRiArk {

        LOCALE, SECONDARIO, ENTRAMBI;

        public static TipoRiArk getTipoRiArk(String tipoRiArk) {
            for (TipoRiArk tipo : TipoRiArk.values()) {
                if (tipo.toString().equals(tipoRiArk)) {
                    return tipo;
                }
            }
            return null;
        }
    }

    public RichiestaTpi(TipoRichiesta tipoRichiesta) {
        this.tipoRichiesta = tipoRichiesta;
    }

    public TipoRichiesta getTipoRichiesta() {
        return tipoRichiesta;
    }

    public void setTipoRichiesta(TipoRichiesta tipoRichiesta) {
        this.tipoRichiesta = tipoRichiesta;
    }
}
