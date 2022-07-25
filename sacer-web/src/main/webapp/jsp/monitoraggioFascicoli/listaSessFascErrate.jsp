<%@page import="it.eng.parer.slite.gen.form.MonitoraggioFascicoliForm" pageEncoding="UTF-8"%>
<%@include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista sessioni fascicoli errate" >
        <script type='text/javascript'>
            $(document).ready(function () {
                // Accende o spegne il pulsante in base al contenuto della combo
                $('#Ti_stato_ses').change(function () {
                    var input = $(this).val();
                    if (input === 'NON_VERIFICATO') {
                        $('input[name=operation__trasfSessErrateInVersFallitiButton]').show();
                    } else {
                        $('input[name=operation__trasfSessErrateInVersFallitiButton]').hide();
                    }
                });
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="LISTA SESSIONI FASCICOLI ERRATE"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriSessioniFascicoli.GIORNO_VERS_DA%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioFascicoliForm.FiltriSessioniFascicoli.ORE_VERS_DA%>" name2="<%=MonitoraggioFascicoliForm.FiltriSessioniFascicoli.MINUTI_VERS_DA%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriSessioniFascicoli.GIORNO_VERS_A%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioFascicoliForm.FiltriSessioniFascicoli.ORE_VERS_A%>" name2="<%=MonitoraggioFascicoliForm.FiltriSessioniFascicoli.MINUTI_VERS_A%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriSessioniFascicoli.TI_STATO_SES%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriSessioniFascicoli.CD_CLASSE_ERR%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriSessioniFascicoli.CD_ERR%>" colSpan="4" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriSessioniFascicoli.RICERCA_SESS_FASCICOLI_ERRATE_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioFascicoliForm.SesFascicoliErrList.NAME%>" pageSizeRelated="true" />
                <slf:list name="<%= MonitoraggioFascicoliForm.SesFascicoliErrList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioFascicoliForm.SesFascicoliErrList.NAME%>" />
            
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>