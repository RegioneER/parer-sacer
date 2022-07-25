package it.eng.parer.amministrazioneStrutture.gestioneTitolario.dto;

import it.eng.parer.titolario.xml.ChiudiVoceType;
import it.eng.parer.titolario.xml.CreaVoceType;
import it.eng.parer.titolario.xml.LivelloType;
import it.eng.parer.titolario.xml.ModificaVoceType;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author Bonora_L
 */
public class Voce {

    private String codiceVoceComposito;
    private Integer numeroOrdine;
    private String descrizioneVoce;
    private Date dataInizioValidita;
    private Date dataFineValidita;
    private AttivoClass attivoPerClassificazione;
    private Integer tempoConservazione;
    private String noteVoceTitolario;

    private Operation operation;
    private LivelloType livello;

    private String codiceVoce;

    private final Map<String, Voce> figli;
    private final Set<Integer> numeroOrdineFigli;

    /**
     * The value of this constant is {@value}.
     */
    public static final String CODICE_VOCE_COMPOSITO = "CodiceVoceComposito";
    /**
     * The value of this constant is {@value}.
     */
    public static final String NUMERO_ORDINE = "NumeroOrdine";
    /**
     * The value of this constant is {@value}.
     */
    public static final String DESCRIZIONE_VOCE = "DescrizioneVoce";
    /**
     * The value of this constant is {@value}.
     */
    public static final String DATA_INIZIO_VALIDITA = "DataInizioValidita";
    /**
     * The value of this constant is {@value}.
     */
    public static final String DATA_FINE_VALIDITA = "DataFineValidita";
    /**
     * The value of this constant is {@value}.
     */
    public static final String ATTIVO_PER_CLASSIFICAZIONE = "AttivoPerClassificazione";
    /**
     * The value of this constant is {@value}.
     */
    public static final String TEMPO_CONSERVAZIONE = "TempoConservazione";
    /**
     * The value of this constant is {@value}.
     */
    public static final String NOTE_VOCE_TITOLARIO = "NoteVoceTitolario";

    /**
     * Enum per i tipi di operazioni possibili per le voci del titolario
     */
    public enum Operation {

        CREA(0), MODIFICA(1), CHIUDI(2);

        int comparatorValue;

        Operation(int comparatorValue) {
            this.comparatorValue = comparatorValue;
        }

        public int getComparatorValue() {
            return comparatorValue;
        }
    }

    public enum AttivoClass {

        SI("1"), NO("0");

        String val;

        private AttivoClass(String val) {
            this.val = val;
        }

        public static AttivoClass fromValue(String val) {
            if (val != null) {
                for (AttivoClass b : AttivoClass.values()) {
                    if (val.equalsIgnoreCase(b.val)) {
                        return b;
                    }
                }
            }
            return null;
        }

        public String getVal() {
            return this.val;
        }

    }

    public enum Fields {

        CODICE_VOCE_COMPOSITO(Voce.CODICE_VOCE_COMPOSITO), NUMERO_ORDINE(Voce.NUMERO_ORDINE),
        DESCRIZIONE_VOCE(Voce.DESCRIZIONE_VOCE), DATA_INIZIO_VALIDITA(Voce.DATA_INIZIO_VALIDITA),
        DATA_FINE_VALIDITA(Voce.DATA_FINE_VALIDITA), ATTIVO_PER_CLASSIFICAZIONE(Voce.ATTIVO_PER_CLASSIFICAZIONE),
        TEMPO_CONSERVAZIONE(Voce.TEMPO_CONSERVAZIONE), NOTE_VOCE_TITOLARIO(Voce.NOTE_VOCE_TITOLARIO);

        private final String nomeCampo;

        Fields(String nomeCampo) {
            this.nomeCampo = nomeCampo;
        }

        public String getNomeCampo() {
            return nomeCampo;
        }
    }

    public Voce(Operation operation) {
        this.operation = operation;
        figli = new HashMap<>();
        numeroOrdineFigli = new HashSet<>();
    }

    /**
     * Costruttore voce con operazione null. Utilizzato per le voci già esistenti su db
     * 
     * @param codiceVoceComposito
     *            codice composito
     * @param descrizioneVoce
     *            descrizione
     * @param numeroOrdine
     *            ordine
     * @param dataInizioValidita
     *            data inizio validita
     * @param dataFineValidita
     *            data fine validita
     * @param attivoPerClassificazione
     *            classificazione
     * @param tempoConservazione
     *            tempo conservazione
     * @param noteVoceTitolario
     *            note titolario
     */
    public Voce(String codiceVoceComposito, String descrizioneVoce, int numeroOrdine, Date dataInizioValidita,
            Date dataFineValidita, AttivoClass attivoPerClassificazione, Integer tempoConservazione,
            String noteVoceTitolario) {
        this.codiceVoceComposito = codiceVoceComposito;
        this.descrizioneVoce = descrizioneVoce;
        this.numeroOrdine = numeroOrdine;
        this.dataInizioValidita = dataInizioValidita;
        this.dataFineValidita = dataFineValidita;
        this.attivoPerClassificazione = attivoPerClassificazione;
        this.tempoConservazione = tempoConservazione;
        this.noteVoceTitolario = noteVoceTitolario;
        figli = new HashMap<>();
        numeroOrdineFigli = new HashSet<>();
    }

    /**
     * Costruttore voce per oggetti xml di creazione
     *
     * @param voce
     *            oggetto xml contenente i dati per la creazione della voce
     */
    public Voce(CreaVoceType voce) {
        this(Operation.CREA, voce.getCodiceVoceComposito(),
                voce.getNumeroOrdine() != null ? voce.getNumeroOrdine().intValue() : 1, voce.getDescrizioneVoce(),
                voce.getDataInizioValidita() != null ? voce.getDataInizioValidita().toGregorianCalendar().getTime()
                        : null,
                voce.getDataFineValidita() != null ? voce.getDataFineValidita().toGregorianCalendar().getTime() : null,
                AttivoClass.valueOf(voce.getAttivoPerClassificazione().name()), voce.getTempoConservazione().intValue(),
                voce.getNoteVoceTitolario());
    }

    /**
     * Costruttore voce per oggetti xml di modifica
     *
     * @param voce
     *            oggetto xml contenente i dati per la modifica della voce
     */
    public Voce(ModificaVoceType voce) {
        this(Operation.MODIFICA, voce.getCodiceVoceComposito(), null, voce.getDescrizioneVoce(), null,
                voce.getDataFineValidita() != null ? voce.getDataFineValidita().toGregorianCalendar().getTime() : null,
                AttivoClass.valueOf(voce.getAttivoPerClassificazione().name()), voce.getTempoConservazione().intValue(),
                voce.getNoteVoceTitolario());
    }

    /**
     * Costruttore voce per oggetti xml di chiusura
     *
     * @param voce
     *            oggetto xml contenente i dati per la chiusura della voce
     */
    public Voce(ChiudiVoceType voce) {
        this(Operation.CHIUDI, voce.getCodiceVoceComposito(), null, null, null,
                voce.getDataFineValidita() != null ? voce.getDataFineValidita().toGregorianCalendar().getTime() : null,
                null, null, voce.getNoteVoceTitolario());
    }

    /**
     * Costruttore voce generico
     *
     * @param operation
     *            Tipo di operazione
     * @param codiceVoceComposito
     *            codice voce
     * @param numeroOrdine
     *            numero d'ordine all'interno dell'albero
     * @param descrizioneVoce
     *            descrizione
     * @param dataInizioValidita
     *            data inizio validita
     * @param dataFineValidita
     *            data fine validita
     * @param attivoPerClassificazione
     *            classificazione
     * @param tempoConservazione
     *            tempo conservazione
     * @param noteVoceTitolario
     *            note titolario
     */
    public Voce(Operation operation, String codiceVoceComposito, Integer numeroOrdine, String descrizioneVoce,
            Date dataInizioValidita, Date dataFineValidita, AttivoClass attivoPerClassificazione,
            Integer tempoConservazione, String noteVoceTitolario) {
        figli = new HashMap<>();
        numeroOrdineFigli = new HashSet<>();
        this.operation = operation;
        this.codiceVoceComposito = codiceVoceComposito;
        this.numeroOrdine = numeroOrdine;
        this.descrizioneVoce = descrizioneVoce;
        this.dataInizioValidita = dataInizioValidita;
        this.dataFineValidita = dataFineValidita;
        this.attivoPerClassificazione = attivoPerClassificazione;
        this.tempoConservazione = tempoConservazione;
        this.noteVoceTitolario = noteVoceTitolario;
    }

    public Voce(String codiceVoceComposito, Integer numeroOrdine, String descrizioneVoce, Date dataInizioValidita,
            Date dataFineValidita, AttivoClass attivoPerClassificazione, Integer tempoConservazione,
            String noteVoceTitolario) {
        this(Operation.CREA, codiceVoceComposito, numeroOrdine, descrizioneVoce, dataInizioValidita, dataFineValidita,
                attivoPerClassificazione, tempoConservazione, noteVoceTitolario);
    }

    public Voce(String codiceVoceComposito, Date dataFineValidita, String noteVoceTitolario) {
        this(Operation.CHIUDI, codiceVoceComposito, null, null, null, dataFineValidita, null, null, noteVoceTitolario);
    }

    public String getCodiceVoceComposito() {
        return StringEscapeUtils.unescapeJava(codiceVoceComposito);
    }

    public void setCodiceVoceComposito(String codiceVoceComposito) {
        this.codiceVoceComposito = codiceVoceComposito;
    }

    public Integer getNumeroOrdine() {
        return numeroOrdine;
    }

    public void setNumeroOrdine(Integer numeroOrdine) {
        this.numeroOrdine = numeroOrdine;
    }

    public String getDescrizioneVoce() {
        return descrizioneVoce;
    }

    public void setDescrizioneVoce(String descrizioneVoce) {
        this.descrizioneVoce = descrizioneVoce;
    }

    public Date getDataInizioValidita() {
        return dataInizioValidita;
    }

    public void setDataInizioValidita(Date dataInizioValidita) {
        this.dataInizioValidita = dataInizioValidita;
    }

    public Date getDataFineValidita() {
        return dataFineValidita;
    }

    public void setDataFineValidita(Date dataFineValidita) {
        this.dataFineValidita = dataFineValidita;
    }

    public AttivoClass getAttivoPerClassificazione() {
        return attivoPerClassificazione;
    }

    public void setAttivoPerClassificazione(AttivoClass attivoPerClassificazione) {
        this.attivoPerClassificazione = attivoPerClassificazione;
    }

    public Integer getTempoConservazione() {
        return tempoConservazione;
    }

    public void setTempoConservazione(Integer tempoConservazione) {
        this.tempoConservazione = tempoConservazione;
    }

    public String getNoteVoceTitolario() {
        return noteVoceTitolario;
    }

    public void setNoteVoceTitolario(String noteVoceTitolario) {
        this.noteVoceTitolario = noteVoceTitolario;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public void putFiglio(String codiceVoceComposito, Voce figlio) {
        figli.put(codiceVoceComposito, figlio);
    }

    public Voce getFiglio(String codiceVoceSingolo) {
        if (figli.containsKey(codiceVoceSingolo)) {
            return figli.get(codiceVoceSingolo);
        }
        return null;
    }

    public Voce removeFiglio(String codiceVoceSingolo) {
        if (figli.containsKey(codiceVoceSingolo)) {
            Voce son = figli.remove(codiceVoceSingolo);
            numeroOrdineFigli.remove(son.getNumeroOrdine());
            return son;
        }
        return null;
    }

    public Map<String, Voce> getFigli() {
        return figli;
    }

    public boolean putNumeroOrdineFiglio(Integer numeroOrdine) {
        return numeroOrdineFigli.add(numeroOrdine);
    }

    public int getNumeroFigli() {
        return figli.size();
    }

    public LivelloType getLivello() {
        return livello;
    }

    public void setLivello(LivelloType livello) {
        this.livello = livello;

        if (livello.getNumeroLivello().equals(BigInteger.ONE)) {
            this.codiceVoce = this.codiceVoceComposito;
        } else {
            int index = this.codiceVoceComposito.lastIndexOf(livello.getCarattereSeparatoreLivello());
            this.codiceVoce = this.codiceVoceComposito.substring(index + 1);
        }
    }

    public String getCodiceVoce() {
        return codiceVoce;
    }

    public void setCodiceVoce(String codiceVoce) {
        this.codiceVoce = codiceVoce;
    }
}
