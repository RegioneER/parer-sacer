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

    function normalizeHtml(html) {
        if (!html) {
            return "";
        }

        return html.replace(/<script[\s\S]*?>[\s\S]*?<\/script>/gi, "")
                .replace(/ on\w+=(['"]).*?\1/gi, "");
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

    function initGestioneErroriRichText() {
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