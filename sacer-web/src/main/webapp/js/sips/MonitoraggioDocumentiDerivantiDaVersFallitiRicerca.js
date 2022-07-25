            $(document).ready(function(){
                var $ti_filtro = $('[name=Id_strut]');
                var verificaButton = $('[name=operation__verificaVersamentiFalliti]');
                $ti_filtro.change(function() {


                    if ($ti_filtro.val() === '') {
                        verificaButton.hide();

                    } else {
                        verificaButton.show();

                    }
                }).trigger('change'); // added trigger to calculate initial state                
            });
