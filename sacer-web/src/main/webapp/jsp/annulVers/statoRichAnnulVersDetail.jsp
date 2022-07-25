<%@ page import="it.eng.parer.slite.gen.form.AnnulVersForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=AnnulVersForm.StatoRichAnnulVersDetail.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="<%=AnnulVersForm.StatoRichAnnulVersDetail.DESCRIPTION%>"/>
            <slf:listNavBarDetail name="<%= AnnulVersForm.StatiList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=AnnulVersForm.InfoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=AnnulVersForm.StatoRichAnnulVersDetail.NM_AMBIENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.StatoRichAnnulVersDetail.NM_ENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.StatoRichAnnulVersDetail.NM_STRUT%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.StatoRichAnnulVersDetail.CD_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.StatoRichAnnulVersDetail.DS_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.StatoRichAnnulVersDetail.TI_STATO_RICH_ANNUL_VERS_COR%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
                <slf:section name="<%=AnnulVersForm.StatoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=AnnulVersForm.StatoRichAnnulVersDetail.PG_STATO_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.StatoRichAnnulVersDetail.TI_STATO_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.StatoRichAnnulVersDetail.DT_REG_STATO_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.StatoRichAnnulVersDetail.DS_NOTA_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.StatoRichAnnulVersDetail.NM_USERID_STATO%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>