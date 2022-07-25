<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%-- Questa pagina risiede nella root (e non in /jsp/) per "sfuggire" da spring-security ... --%>
<style type="text/css">
body {
	font-size: 13px;
}
</style>
<!--<link href="https://parer-svil.ente.regione.emr.it/dss-demo-webapp/css/bootstrap-theme.min.css" rel="stylesheet" />-->
<div class="container">
	<div class="tab-content" style="margin-top: 10px">
		<ul class="nav nav-tabs nav-justified hidden-print" id="tabsResult"
			style="margin-top: 25px;">
			<li role="presentation" class="active"><a href="#report">Crypto
					Report</a></li>
		</ul>
		<%
            String esitoCrypto = StringEscapeUtils.escapeXml(((String) session.getAttribute("crypto")));
        %>


		<div role="tabpanel" class="tab-pane  in active" id="report">
			<pre class="prettyprint lang-xml">
				<%=esitoCrypto%>
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
    session.removeAttribute("reportCrypto");
%>
