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

<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%-- Questa pagina risiede nella root (e non in /jsp/) per "sfuggire" da spring-security ... --%>
<!--<link href="https://parer-svil.ente.regione.emr.it/dss-demo-webapp/css/bootstrap-theme.min.css" rel="stylesheet" />-->
<div class="container">
	<div class="tab-content" style="margin-top: 10px">
		<ul class="nav nav-tabs nav-justified hidden-print" id="tabsResult"
			style="margin-top: 25px;">
			<li role="presentation" class="active"><a href="#simple-report">Simple
					Report</a></li>
			<li role="presentation" class=""><a href="#detailed-report">Detailed
					Report</a></li>
			<li role="presentation" class=""><a href="#diagnostic-tree">Diagnostic
					tree</a></li>
		</ul>
		<%
            String[] esitoEidas = (String[]) session.getAttribute("eidas");

            String simpleReportHtml = esitoEidas[0];
            String detailedReportHtml = esitoEidas[1];
            String diagnosticData = StringEscapeUtils.escapeXml(esitoEidas[2]);

        %>


		<div role="tabpanel" class="tab-pane  in active" id="simple-report">
			<%=simpleReportHtml%>
		</div>
		<div role="tabpanel" class="tab-pane " id="detailed-report">
			<%=detailedReportHtml%>
		</div>
		<div role="tabpanel" class="tab-pane " id="diagnostic-tree">
			<pre class="prettyprint lang-xml">
                <%=diagnosticData%> 
            </pre>
		</div>
	</div>
</div>
<script type="text/javascript">
	$(function() {
		$(".tab-content").tabs({
			collapsible : false
		});
	});
</script>
<%
    session.removeAttribute("reportEidas");
%>
