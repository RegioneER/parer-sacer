<%@ page import="it.eng.parer.slite.gen.form.ElenchiVersamentoForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="<%= ElenchiVersamentoForm.DocDaRimuovereList.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="LISTA DOCUMENTI DA ELIMINARE" />

            <h2>Vuoi rimuovere i seguenti documenti dall'elenco di versamento ${ fn:escapeXml(requestScope.elenco) }?</h2>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= ElenchiVersamentoForm.DocDaRimuovereList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= ElenchiVersamentoForm.DocDaRimuovereList.NAME%>" />
            <slf:listNavBar  name="<%= ElenchiVersamentoForm.DocDaRimuovereList.NAME%>" />

            <div><input name="table" type="hidden" value="<%= ElenchiVersamentoForm.ElenchiVersamentoList.NAME%>" /></div>

            <sl:pulsantiera>
                <slf:buttonList name="<%= ElenchiVersamentoForm.ListaDocDaRimuovereButtonList.NAME%>" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>