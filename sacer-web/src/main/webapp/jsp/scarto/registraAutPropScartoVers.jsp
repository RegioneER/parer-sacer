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
    <sl:head title="<%=ScartoForm.RegistraAutPropScartoVers.DESCRIPTION%>">
        <script type="text/javascript">
            $(document).ready(function () {

                // ---- Toggle registro risposta autorità ----
                var nameFlRisp     = 'Fl_registro_esterno_risp_aut';
                var nameRegIntRisp = 'Cd_registro_risp_aut';
                var nameRegExtRisp = 'Cd_registro_risp_aut_esterno';
                var $chkRisp = $('[name="' + nameFlRisp + '"]');

                function toggleRegistroEsternoRisp() {
                    var $inputRegInt = $('[name="' + nameRegIntRisp + '"]');
                    var $containerRegInt = $inputRegInt.closest('.containerLeft');
                    var $labelRegInt = $('label[for="' + nameRegIntRisp + '"]');

                    var $inputRegExt = $('[name="' + nameRegExtRisp + '"]');
                    var $containerRegExt = $inputRegExt.closest('.containerLeft');
                    var $labelRegExt = $('label[for="' + nameRegExtRisp + '"]');

                    if ($chkRisp.is(':checked')) {
                        $containerRegInt.hide();
                        $labelRegInt.hide();
                        $inputRegInt.val('');
                        $containerRegExt.show();
                        $labelRegExt.show();
                    } else {
                        $containerRegInt.show();
                        $labelRegInt.show();
                        $containerRegExt.hide();
                        $labelRegExt.hide();
                        $inputRegExt.val('');
                    }
                }

                toggleRegistroEsternoRisp();
                $chkRisp.on('change', toggleRegistroEsternoRisp);

                // ---- Toggle registro provvedimento di scarto ----
                var nameFlProvv     = 'Fl_registro_esterno_provv_scarto';
                var nameRegIntProvv = 'Cd_registro_provv_scarto';
                var nameRegExtProvv = 'Cd_registro_provv_scarto_esterno';
                var $chkProvv = $('[name="' + nameFlProvv + '"]');

                function toggleRegistroEsternoProvv() {
                    var $inputRegInt = $('[name="' + nameRegIntProvv + '"]');
                    var $containerRegInt = $inputRegInt.closest('.containerLeft');
                    var $labelRegInt = $('label[for="' + nameRegIntProvv + '"]');

                    var $inputRegExt = $('[name="' + nameRegExtProvv + '"]');
                    var $containerRegExt = $inputRegExt.closest('.containerLeft');
                    var $labelRegExt = $('label[for="' + nameRegExtProvv + '"]');

                    if ($chkProvv.is(':checked')) {
                        $containerRegInt.hide();
                        $labelRegInt.hide();
                        $inputRegInt.val('');
                        $containerRegExt.show();
                        $labelRegExt.show();
                    } else {
                        $containerRegInt.show();
                        $labelRegInt.show();
                        $containerRegExt.hide();
                        $labelRegExt.hide();
                        $inputRegExt.val('');
                    }
                }

                toggleRegistroEsternoProvv();
                $chkProvv.on('change', toggleRegistroEsternoProvv);
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="<%=ScartoForm.RegistraAutPropScartoVers.DESCRIPTION%>" />
            <slf:fieldBarDetailTag name="<%=ScartoForm.RegistraAutPropScartoVers.NAME%>" hideBackButton="true" />
            <sl:newLine skipLine="true"/>

            <%-- Sezione: Dati proposta --%>
            <slf:section name="<%=ScartoForm.DatiPropostaSection.NAME%>" styleClass="importantContainer">
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.CD_PROP_SCARTO_VERS%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.TI_STATO_PROP_SCARTO_VERS_COR%>" colSpan="2" />
                <sl:newLine skipLine="true"/>
            </slf:section>

            <%-- Sezione: Dati risposta autorizzazione --%>
            <slf:section name="<%=ScartoForm.RispostaAutorizzazioneSection.NAME%>" styleClass="importantContainer">
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.FL_REGISTRO_ESTERNO_RISP_AUT%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.CD_REGISTRO_RISP_AUT%>" colSpan="2" />
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.CD_REGISTRO_RISP_AUT_ESTERNO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.AA_RISP_AUT%>" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.CD_RISP_AUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.TI_AUTORIZZAZIONE%>" colSpan="2" />
                <sl:newLine skipLine="true"/>
            </slf:section>

            <%-- Sezione: Provvedimento di scarto --%>
            <slf:section name="<%=ScartoForm.ProvvedimentoScartoSection.NAME%>" styleClass="importantContainer">
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.FL_REGISTRO_ESTERNO_PROVV_SCARTO%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.CD_REGISTRO_PROVV_SCARTO%>" colSpan="2" />
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.CD_REGISTRO_PROVV_SCARTO_ESTERNO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.AA_PROVV_SCARTO%>" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.CD_PROVV_SCARTO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.DS_FIRMATO_DA%>" colSpan="2" />
                <sl:newLine skipLine="true"/>
            </slf:section>

            <sl:pulsantiera>
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.SALVA_REGISTRA_AUTORIZZAZIONE%>" colSpan="1" />
                <slf:lblField name="<%=ScartoForm.RegistraAutPropScartoVers.ANNULLA_REGISTRA_AUTORIZZAZIONE%>" colSpan="1" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
