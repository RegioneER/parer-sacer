<%@ page import="it.eng.parer.slite.gen.form.SerieUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=SerieUDForm.ContenutoEffettivoButtonList.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="<%=SerieUDForm.ContenutoEffettivoButtonList.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldBarDetailTag name="<%=SerieUDForm.ContenutoEffettivoButtonList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <div id="container1" class="w100">
                    <slf:section name="<%=SerieUDForm.ContenutoCalcolatoSection.NAME%>" styleClass="importantContainer containerLeft w50">
                        <slf:lblField name="<%=SerieUDForm.SerieDetail.TI_STATO_CONTENUTO_CALC%>" width="w100" controlWidth="w40" labelWidth="w50"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.SerieDetail.FL_ERR_CONTENUTO_CALC%>" width="w100" controlWidth="w40" labelWidth="w50"/>
                        <sl:newLine />
                    </slf:section>
                    <slf:section name="<%=SerieUDForm.ContenutoAcquisitoSection.NAME%>" styleClass="importantContainer containerRight w50">
                        <slf:lblField name="<%=SerieUDForm.SerieDetail.TI_STATO_CONTENUTO_ACQ%>" width="w100" controlWidth="w60" labelWidth="w40"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.SerieDetail.FL_ERR_CONTENUTO_FILE%>" width="w100" controlWidth="w60" labelWidth="w40"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.SerieDetail.FL_ERR_CONTENUTO_ACQ%>" width="w100" controlWidth="w60" labelWidth="w40"/>
                        <sl:newLine />
                    </slf:section>
                </div>
                <slf:lblField name="<%=SerieUDForm.SerieDetail.TI_CONTENUTO_GENERAZIONE%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%=SerieUDForm.ContenutoEffettivoButtonList.CONFERMA_GENERAZIONE_EFFETTIVO%>" />
                </sl:pulsantiera>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>