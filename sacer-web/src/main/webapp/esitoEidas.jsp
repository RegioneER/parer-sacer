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
