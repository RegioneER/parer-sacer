(function ($) {
    function ensureQuillStyles() {
        if (!document.getElementById("gestione-errori-richtext-style")) {
            var style = document.createElement("style");
            style.id = "gestione-errori-richtext-style";
            style.textContent = ""
                    + ".ge-quill-shell{max-width:960px;}"
                    + ".ge-quill-toolbar{background:#fafafa;}"
                    + ".ge-quill-editor{min-height:260px;background:#fff;}"
                    + ".ge-quill-preview{max-width:960px;min-height:120px;padding:12px;border:1px solid #d5d5d5;background:#fff;line-height:1.5;}";
            document.head.appendChild(style);
        }
    }

    function isRichTextField(textArea) {
        var fieldId = (textArea.id || "").toLowerCase();
        var fieldName = (textArea.name || "").toLowerCase();
        return fieldId.indexOf("soluzione_sugg") !== -1
                || fieldName.indexOf("soluzione_sugg") !== -1
                || fieldId.indexOf("casistica") !== -1
                || fieldName.indexOf("casistica") !== -1;
    }

    function findRichTextFields() {
        return $("textarea").filter(function () {
            return isRichTextField(this);
        });
    }

    // Whitelist coerente con i pulsanti disponibili nella toolbar Quill configurata in
    // buildEditor: bold/italic/underline/strike, liste, blockquote, link.
    var DOMPURIFY_CONFIG = {
        ALLOWED_TAGS: ["p", "br", "b", "strong", "i", "em", "u", "s", "strike", "ul", "ol", "li",
            "blockquote", "a", "span"],
        ALLOWED_ATTR: ["href", "target", "rel", "class"],
        ALLOW_DATA_ATTR: false
    };

    /**
     * Sanitizza un frammento HTML con DOMPurify prima che venga assegnato a innerHTML (via
     * jQuery .html()/.append() o direttamente su quill.root.innerHTML). Usa una whitelist di
     * tag/attributi (nessun tag script/iframe/svg, nessun attributo on*, nessun protocollo
     * pericoloso su href) per prevenire XSS anche in presenza di markup malformato o payload che
     * bypassano i controlli basati su semplici espressioni regolari.
     */
    function normalizeHtml(html) {
        if (!html) {
            return "";
        }

        if (window.DOMPurify && typeof window.DOMPurify.sanitize === "function") {
            return window.DOMPurify.sanitize(html, DOMPURIFY_CONFIG);
        }

        // Fallback prudenziale nel caso DOMPurify non sia disponibile (es. errore di
        // caricamento): niente HTML, solo testo plain per evitare comunque l'XSS.
        return $("<div/>").text($("<div/>").html(html).text()).html();
    }

    function plainTextToHtml(value) {
        if (!value) {
            return "";
        }

        if (/<[a-z][\s\S]*>/i.test(value)) {
            return normalizeHtml(value);
        }

        return $("<div/>").text(value).html().replace(/\r?\n/g, "<br>");
    }

    function syncQuillToTextarea(textArea, quill) {
        textArea.val(normalizeHtml(quill.root.innerHTML));
    }

    function buildEditor(textArea, readOnly) {
        ensureQuillStyles();

        var shell = $("<div class='ge-quill-shell'></div>");
        var baseId = textArea.attr("id") || textArea.attr("name") || "rich_text_field";
        var safeId = baseId.replace(/[^A-Za-z0-9_:-]/g, "_");
        var editorId = safeId + "_editor";
        var editor = $(readOnly
                ? "<div id='" + editorId + "' class='ge-quill-preview'></div>"
                : "<div id='" + editorId + "' class='ge-quill-editor'></div>");
        var initialHtml = plainTextToHtml(textArea.val());

        shell.append(editor);
        textArea.hide().after(shell);

        if (readOnly || !window.Quill) {
            editor.html(initialHtml);
            return;
        }

        var quill = new Quill("#" + editorId, {
            theme: "snow",
            modules: {
                toolbar: [
                    ["bold", "italic", "underline", "strike"],
                    [{list: "ordered"}, {list: "bullet"}, "blockquote"],
                    ["link", "clean"]
                ]
            }
        });
        quill.root.innerHTML = initialHtml;
        textArea.data("quillInstance", quill);
        quill.on("text-change", function () {
            syncQuillToTextarea(textArea, quill);
        });
        syncQuillToTextarea(textArea, quill);

        var form = textArea.closest("form");
        form.off("submit.gestioneErroriRichText").on("submit.gestioneErroriRichText", function () {
            syncQuillToTextarea(textArea, quill);
        });
    }

    /**
     * Le pagine in sola visualizzazione (status 'view') non hanno una textarea editabile: il
     * contenuto HTML-encoded viene emesso dalla JSP dentro una <textarea> nascosta (contesto
     * innocuo lato server, non interpretato come markup) e qui viene letto, sanitizzato con
     * DOMPurify e iniettato nel div di anteprima. In questo modo il markup non viene mai reso
     * "vivo" dal browser prima di essere ripulito.
     */
    function initReadOnlyPreviews() {
        $("textarea.ge-quill-raw-source[data-richtext-source]").each(function () {
            var source = $(this);
            var key = source.data("richtextSource");
            var preview = $("[data-richtext-preview='" + key + "']");
            if (preview.length === 0) {
                return;
            }

            ensureQuillStyles();
            preview.html(normalizeHtml(source.val()));
        });
    }

    function initGestioneErroriRichText() {
        initReadOnlyPreviews();

        var textAreas = findRichTextFields();
        if (textAreas.length === 0) {
            return;
        }

        textAreas.each(function () {
            var textArea = $(this);
            if (textArea.data("richtextInitialized")) {
                return;
            }

            buildEditor(textArea, textArea.is(":disabled") || textArea.is("[readonly]"));
            textArea.data("richtextInitialized", true);
        });
    }

    $(document).ready(initGestioneErroriRichText);
})(jQuery);