<%@ page import="it.eng.parer.slite.gen.form.AnnulVersForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=AnnulVersForm.CreazioneRichAnnulVers.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content multipartForm="true">
            <slf:messageBox />
            <sl:contentTitle title="<%=AnnulVersForm.CreazioneRichAnnulVers.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldBarDetailTag name="<%=AnnulVersForm.CreazioneRichAnnulVers.NAME%>" hideBackButton="${!((fn:length(sessionScope['###_NAVHIS_CONTAINER'])) gt 1 )}" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:lblField  name="<%=AnnulVersForm.CreazioneRichAnnulVers.TI_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=AnnulVersForm.CreazioneRichAnnulVers.CD_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=AnnulVersForm.CreazioneRichAnnulVers.DS_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=AnnulVersForm.CreazioneRichAnnulVers.NT_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=AnnulVersForm.CreazioneRichAnnulVers.FL_IMMEDIATA%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=AnnulVersForm.CreazioneRichAnnulVers.FL_FORZA_ANNUL%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=AnnulVersForm.CreazioneRichAnnulVers.BL_FILE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField  name="<%=AnnulVersForm.CreazioneRichAnnulVers.CREA_RICH_ANNUL_VERS%>"  width="w50" />
            </sl:pulsantiera> 
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
