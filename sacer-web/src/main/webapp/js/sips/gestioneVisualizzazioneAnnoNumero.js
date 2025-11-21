/**
 * Oggetto per gestire la logica di visualizzazione (abilitazione/disabilitazione)
 * dei campi di ricerca Anno e Numero per le Unità Documentarie.
 */
const GestioneRicercaUD = {

    init: function() {
        // Selezioniamo gli elementi del form con gli ID corretti
        const annoSingolo = document.getElementById('Aa_key_unita_doc');
        const annoDa = document.getElementById('Aa_key_unita_doc_da');
        const annoA = document.getElementById('Aa_key_unita_doc_a');
        const numeroSingolo = document.getElementById('Cd_key_unita_doc');
        const numeroDa = document.getElementById('Cd_key_unita_doc_da');
        const numeroA = document.getElementById('Cd_key_unita_doc_a');
        const numeroContiene = document.getElementById('Cd_key_unita_doc_contiene');

        const fields = {
            annoSingolo, annoDa, annoA,
            numeroSingolo, numeroDa, numeroA, numeroContiene
        };

        // Aggiungiamo i listener per la disabilitazione dinamica (evento 'input')
        Object.values(fields).forEach(field => {
            if (field) {
                field.addEventListener('input', () => this.updateFieldStates(fields));
            }
        });

        // Eseguiamo la logica al caricamento della pagina per gestire form pre-compilati
        this.updateFieldStates(fields);
    },

    /**
     * Aggiorna lo stato (abilitato/disabilitato) dei campi in base ai valori inseriti.
     */
    updateFieldStates: function(fields) {
        const { annoSingolo, annoDa, annoA, numeroSingolo, numeroDa, numeroA, numeroContiene } = fields;

        // --- Logica per il gruppo ANNO ---
        const annoSingoloHasValue = annoSingolo && annoSingolo.value.trim() !== '';
        const annoRangeHasValue = (annoDa && annoDa.value.trim() !== '') || (annoA && annoA.value.trim() !== '');
        
        if (annoSingolo) annoSingolo.disabled = annoRangeHasValue;
        if (annoDa) annoDa.disabled = annoSingoloHasValue;
        if (annoA) annoA.disabled = annoSingoloHasValue;

        // --- Logica per il gruppo NUMERO ---
        const numeroSingoloHasValue = numeroSingolo && numeroSingolo.value.trim() !== '';
        const numeroRangeHasValue = (numeroDa && numeroDa.value.trim() !== '') || (numeroA && numeroA.value.trim() !== '');
        const numeroContieneHasValue = numeroContiene && numeroContiene.value.trim() !== '';
        
        if (numeroSingolo) numeroSingolo.disabled = numeroRangeHasValue || numeroContieneHasValue;
        if (numeroDa) numeroDa.disabled = numeroSingoloHasValue || numeroContieneHasValue;
        if (numeroA) numeroA.disabled = numeroSingoloHasValue || numeroContieneHasValue;
        if (numeroContiene) numeroContiene.disabled = numeroSingoloHasValue || numeroRangeHasValue;
    }
};

// Avviamo la logica solo quando il DOM è pronto
document.addEventListener('DOMContentLoaded', function() {
    GestioneRicercaUD.init();
});