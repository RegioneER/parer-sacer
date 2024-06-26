<%@ page import="it.eng.parer.slite.gen.form.SerieUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=SerieUDForm.LacunaDetail.DESCRIPTION%>" >
        <script type="text/javascript">
            $(document).ready(function () {
                $("#Ti_mod_lacuna").change(function () {
                    var divTipo = $("div#Ti_mod_lacuna").text();
                    var tipo = $("#Ti_mod_lacuna").val();
                    
                    if (divTipo){
                        tipo = divTipo;
                    }

                    $("#Ni_ini_lacuna").attr("disabled", false);
                    $("#Ni_fin_lacuna").attr("disabled", false);
                    $("#Dl_lacuna").attr("disabled", false);
                    if (tipo) {
                        if (tipo === 'RANGE_PROGRESSIVI') {
                            $("#Dl_lacuna").attr("disabled", true);
                        } else {
                            $("#Ni_ini_lacuna").attr("disabled", true);
                            $("#Ni_fin_lacuna").attr("disabled", true);
                        }
                    }
                });

                $("#Ti_mod_lacuna").trigger('change');
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="<%=SerieUDForm.LacunaDetail.DESCRIPTION%>"/>
            <c:if test="${sessionScope['###_FORM_CONTAINER']['lacuneList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= SerieUDForm.LacunaDetail.NAME%>" hideBackButton="${sessionScope['###_FORM_CONTAINER']['lacuneList'].status eq 'insert'}"/> 
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['lacuneList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= SerieUDForm.LacuneList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=SerieUDForm.LacunaDetail.ID_LACUNA_CONSIST_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=SerieUDForm.LacunaDetail.PG_LACUNA%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=SerieUDForm.LacunaDetail.TI_LACUNA%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=SerieUDForm.LacunaDetail.TI_MOD_LACUNA%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=SerieUDForm.LacunaDetail.NI_INI_LACUNA%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=SerieUDForm.LacunaDetail.NI_FIN_LACUNA%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=SerieUDForm.LacunaDetail.DL_LACUNA%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=SerieUDForm.LacunaDetail.DL_NOTA_LACUNA%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <sl:newLine />
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>