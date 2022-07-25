<%@ page import="it.eng.parer.slite.gen.form.ElenchiVersamentoForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="<%= ElenchiVersamentoForm.UpdDaRimuovereList.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="LISTA AGGIORNAMENTI DA ELIMINARE" />

            <h2>Vuoi rimuovere i seguenti aggiornamenti dall'elenco di versamento ${ fn:escapeXml(requestScope.elenco) }?</h2>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= ElenchiVersamentoForm.UpdDaRimuovereList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= ElenchiVersamentoForm.UpdDaRimuovereList.NAME%>" />
            <slf:listNavBar  name="<%= ElenchiVersamentoForm.UpdDaRimuovereList.NAME%>" />

            <div><input name="table" type="hidden" value="<%= ElenchiVersamentoForm.ElenchiVersamentoList.NAME%>" /></div>

            <sl:pulsantiera>
                <slf:buttonList name="<%= ElenchiVersamentoForm.ListaUpdDaRimuovereButtonList.NAME%>" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>