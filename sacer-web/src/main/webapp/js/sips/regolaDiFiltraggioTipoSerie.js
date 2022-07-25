            $(document).ready(function(){
                var $ti_filtro = $('[name=Ti_filtro]');
                $ti_filtro.change(function() {

                    if ($ti_filtro.val() === 'TIPO_DOC_PRINC') {
                        $("#Id_tipo_doc_princ").removeAttr('disabled');
                    }else{
                        $("#Id_tipo_doc_princ").val("");
                        $("#Id_tipo_doc_princ").attr("disabled", true);
                    }
                }).trigger('change'); // added trigger to calculate initial state                
            });
