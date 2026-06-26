<%--
 Engineering Ingegneria Informatica S.p.A.

 Copyright (C) 2023 Regione Emilia-Romagna
 <p/>
 This program is free software: you can redistribute it and/or modify it under the terms of
 the GNU Affero General Public License as published by the Free Software Foundation,
 either version 3 of the License, or (at your option) any later version.
 <p/>
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU Affero General Public License for more details.
 <p/>
 You should have received a copy of the GNU Affero General Public License along with this program.
 If not, see <https://www.gnu.org/licenses/>.
 --%>

<%@ page import="it.eng.parer.slite.gen.form.ScartoForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=ScartoForm.RichiestaAutPropScartoVers.DESCRIPTION%>">
        <script type="text/javascript">
            $(document).ready(function () {
                
                // Recupero i nomi esatti dei campi
                var nameFlagExt = 'Fl_registro_esterno_rich';
                var nameRegInt  = 'Cd_registro_rich_aut';
                var nameRegExt  = 'Cd_registro_rich_aut_esterno';

                // Seleziono l'input del flag
                var $chkFlagExt = $('[name="' + nameFlagExt + '"]');

                function toggleRegistroEsternoRich() {
                    
                    // Elementi per il Registro SACER (input/select, container div, e label)
                    var $inputRegInt = $('[name="' + nameRegInt + '"]');
                    var $containerRegInt = $inputRegInt.closest('.containerLeft');
                    var $labelRegInt = $('label[for="' + nameRegInt + '"]');

                    // Elementi per il Registro Esterno (input/select, container div, e label)
                    var $inputRegExt = $('[name="' + nameRegExt + '"]');
                    var $containerRegExt = $inputRegExt.closest('.containerLeft');
                    var $labelRegExt = $('label[for="' + nameRegExt + '"]');

                    // Verifico se la spunta è attiva
                    if ($chkFlagExt.is(':checked')) {
                        // Nascondo l'interno e mostro l'esterno
                        $containerRegInt.hide();
                        $labelRegInt.hide();
                        $inputRegInt.val(''); // Svuoto il valore nascosto
                        
                        $containerRegExt.show();
                        $labelRegExt.show();
                    } else {
                        // Mostro l'interno e nascondo l'esterno
                        $containerRegInt.show();
                        $labelRegInt.show();
                        
                        $containerRegExt.hide();
                        $labelRegExt.hide();
                        $inputRegExt.val(''); // Svuoto il valore nascosto
                    }
                }

                // Eseguo al caricamento della pagina
                toggleRegistroEsternoRich();
                
                // Associo l'evento al cambio della checkbox
                $chkFlagExt.on('change', toggleRegistroEsternoRich);
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="<%=ScartoForm.RichiestaAutPropScartoVers.DESCRIPTION%>" />
            <slf:fieldBarDetailTag name="<%=ScartoForm.RichiestaAutPropScartoVers.NAME%>" hideBackButton="true" />
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=ScartoForm.RichiestaAutorizzazioneSection.NAME%>" styleClass="importantContainer">
                
                <slf:lblField name="<%=ScartoForm.RichiestaAutPropScartoVers.CD_PROP_SCARTO_VERS%>" colSpan="2" />
                <sl:newLine />
                
                <slf:lblField name="<%=ScartoForm.RichiestaAutPropScartoVers.TI_STATO_PROP_SCARTO_VERS_COR%>" colSpan="2" />
                <sl:newLine skipLine="true"/>
                
                <slf:lblField name="<%=ScartoForm.RichiestaAutPropScartoVers.NT_AUTORITA%>" colSpan="4" />
                <sl:newLine />
                
                <slf:lblField name="<%=ScartoForm.RichiestaAutPropScartoVers.FL_REGISTRO_ESTERNO_RICH%>" colSpan="4" />
                <sl:newLine />
                
                <slf:lblField name="<%=ScartoForm.RichiestaAutPropScartoVers.CD_REGISTRO_RICH_AUT%>" colSpan="2" />
                <slf:lblField name="<%=ScartoForm.RichiestaAutPropScartoVers.CD_REGISTRO_RICH_AUT_ESTERNO%>" colSpan="2" />
                <sl:newLine />
                
                <slf:lblField name="<%=ScartoForm.RichiestaAutPropScartoVers.AA_RICH_AUT%>" colSpan="1" />
                <sl:newLine />
                
                <slf:lblField name="<%=ScartoForm.RichiestaAutPropScartoVers.CD_RICH_AUT%>" colSpan="2" />
                <sl:newLine skipLine="true"/>
                
            </slf:section>
            <sl:pulsantiera>
                <slf:lblField name="<%=ScartoForm.RichiestaAutPropScartoVers.SALVA_RICHIESTA_AUTORIZZAZIONE%>" colSpan="2" />
                <slf:lblField name="<%=ScartoForm.RichiestaAutPropScartoVers.ANNULLA_RICHIESTA_AUTORIZZAZIONE%>" colSpan="2" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>