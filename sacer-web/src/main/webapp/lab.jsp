<!--
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
-->

<%--
    Document   : index
    Created on : 30-mag-2011, 14.48.16
    Author     : Quaranta_M
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="java.util.Properties" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    Properties prop = new Properties();
    prop.load(getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF"));
    String appVersion = prop.getProperty("App-Version");
    String appBuildDate = prop.getProperty("App-BuildDate");

    prop.load(getServletContext().getResourceAsStream("/WEB-INF/classes/git.properties"));
    String gitProptags = String.valueOf(prop.get("git.tags"));
    String gitPropbranch = String.valueOf(prop.get("git.branch"));
    String gitPropdirty = String.valueOf(prop.get("git.dirty"));
    String gitPropremoteOriginUrl = String.valueOf(prop.get("git.remote.origin.url"));

    String gitPropcommitId = String.valueOf(prop.get("git.commit.id"));
    String gitPropcommitIdAbbrev = String.valueOf(prop.get("git.commit.id.abbrev"));
    String gitPropdescribe = String.valueOf(prop.get("git.commit.id.describe"));
    String gitPropdescribeShort = String.valueOf(prop.get("git.commit.id.describe-short"));
    String gitPropcommitUserName = String.valueOf(prop.get("git.commit.user.name"));
    String gitPropcommitUserEmail = String.valueOf(prop.get("git.commit.user.email"));
    String gitPropcommitMessageFull = String.valueOf(prop.get("git.commit.message.full"));
    String gitPropcommitMessageShort = String.valueOf(prop.get("git.commit.message.short"));
    String gitPropcommitTime = String.valueOf(prop.get("git.commit.time"));
    String gitPropclosestTagName = String.valueOf(prop.get("git.closest.tag.name"));
    String gitPropclosestTagCommitCount = String.valueOf(prop.get("git.closest.tag.commit.count"));

    String gitPropbuildUserName = String.valueOf(prop.get("git.build.user.name"));
    String gitPropbuildUserEmail = String.valueOf(prop.get("git.build.user.email"));
    String gitPropbuildTime = String.valueOf(prop.get("git.build.time"));
    String gitPropbuildHost = String.valueOf(prop.get("git.build.host"));
    String gitPropbuildVersion = String.valueOf(prop.get("git.build.version"));
%>

<!DOCTYPE html>
<html lang="it" xml:lang="it">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Sacer Lab</title>

        <style>
            h1 {
                font: 25px verdana,verdana, arial;
                margin: 20px 0px 40px 10px;
                padding: 0;
                border-collapse: collapse;
                text-align: left;
                color: #333;
                line-height: 19px;
            }
            h2 {
                font: 17px verdana,verdana, arial;
                margin: 20px 0px 40px 10px;
                padding: 0;
                border-collapse: collapse;
                text-align: left;
                color: #333;
                line-height: 19px;
            }
            .green{
                background: green;
                padding:0px 6px;
                border:1px solid #3b6e22;
                height:24px;
                line-height:24px;
                color:#FFFFFF;
                font-size:12px;
                margin:20px 10px 0 20px;
                display:inline-block;
                text-decoration:none;
            }
            div.box .input-text{
                border:1px solid #3b6e22;
                color:#666666;
            }

            div.box label{
                display:block;
                margin-bottom:10px;
                color:#555555;
            }

            div.box label span{
                font: 11px verdana,verdana, arial;
                display:block;
                float:left;
                padding-right:6px;
                width:150px;
                text-align:left;
                font-weight:bold;
            }
            table {
                font: 11px verdana,verdana, arial;
                margin: 0;
                padding: 0;
                border-collapse: collapse;
                text-align: left;
                color: #333;
                line-height: 19px;
            }

            caption {
                font-size: 14px;
                font-weight: bold;
                margin-bottom: 20px;
                text-align: left;
                text-transform: uppercase;
            }

            td {
                margin: 0;
                padding: 10px 10px;
                border: 1px dotted #f5f5f5;
            }


            td.label {
                text-align: right;
                font-weight: bold;
                vertical-align: top;
            }

            td.titolo {
                font-weight: bold;
                font-size: 13px;
            }

            th {
                font-weight: normal;
                text-transform: uppercase;
            }

            thead tr th {
                background-color: #575757;
                padding:  20px 10px;
                color: #fff;
                font-weight: bold;
                border-right: 2px solid #333;
                text-transform: uppercase;
                text-align:center;
            }

            tfoot tr th, tfoot tr td {
                background-color: transparent;
                padding:  20px 10px;
                color: #ccc;
                border-top: 1px solid #ccc;
            }

            tbody tr th {
                padding: 10px 10px;
                border-bottom: 1px dotted #fafafa;
            }

            tr {
                background-color: #FBFDF6;
            }
            tr.odd {
                background-color: #EDF7DC;
            }

            tr:hover {
            }

            tr:hover td, tr:hover td a, tr:hover th a {
                color: #a10000;
            }

            td:hover {
            }

            tr:hover th a:hover {
                background-color: #F7FBEF;
                border-bottom: 2px solid #86C200;
            }

            table a {
                color: #608117;
                background-image: none;
                text-decoration: none;
                border-bottom: 1px dotted #8A8F95;
                padding: 2px;
                padding-right: 12px;
            }

            table a:hover {
                color: #BBC4CD;
                background-image: none;
                text-decoration: none;
                border-bottom: 3px solid #333;
                padding: 2px;
                padding-right: 12px; color: #A2A2A2;
            }

            table a:visited {
                text-decoration: none;
                border-bottom: 1px dotted #333;
                text-decoration: none;
                padding-right: 12px; color: #A2A2A2;
            }

            table a:visited:hover {
                background-image: none;
                text-decoration: none;
                border-bottom: 3px solid #333;
                padding: 2px;
                padding-right: 12px; color: #A2A2A2;
            }



            div.tabbed {
                position: absolute;
                top: 150px;
                left: 40px;
            }

            div.tabs a {
                font-family: Verdana, Arial, Helvetica, sans-serif;
                font-size: smaller;
                color: #e0e0e0;
                background-color: #404040;
                border: thin solid black;
                margin-right: 2px;
                padding: 0px 2px;
                -moz-border-radius: 5px 5px 0px 0px;
            }

            div.tab {
                display: none;
                height: 500px;
                text-align: left;
            }

            div.tab:target {
                display: block;
            }

            :target div.tab {
                display: block;
            }
            :target div.tab + div.tab{
                display: none;
            }


        </style>
        <script type="text/javascript" src='<c:url value="/webjars/jquery/3.6.4/jquery.min.js" />'></script>
        <script type="text/javascript" src='<c:url value="/js/jQuery/snowfall.min.jquery.js" />'></script>
    </head>
    <body>
        <h1>Sacer Lab</h1>
        <h3>Versione: <% out.println(appVersion);%>
            Build date: <% out.println(appBuildDate);%>
        </h3>

        <div class="tabbed">
            <div class="tabs">
                <a href="#info">Informazioni</a>
                <a href="#util">Utility</a>
                <a href="#vers">Servizi di Versamento</a>
                <a href="#rec">Servizi di Recupero</a>
                <a href="#others">Il resto</a>
            </div>

            <div id="info" class="tab">
                <table>
                    <tr>
                        <td colspan="2" class="titolo" >Branch</td>
                    </tr>
                    <tr>
                        <td class="label">Nome:</td>
                        <td><% out.println(gitPropbranch);%></td>
                    </tr>
                    <tr>
                        <td colspan="2" class="titolo" >Commit</td>
                    </tr>
                    <tr>
                        <td class="label">ID:</td>
                        <td><% out.println(gitPropcommitId);%></td>
                    </tr>
                    <tr>
                        <td class="label">Describe:</td>
                        <td><% out.println(gitPropdescribe);%></td>
                    </tr>
                    <tr>
                        <td class="label">Autore:</td>
                        <td><% out.println(gitPropcommitUserName);%></td>
                    </tr>
                    <tr>
                        <td class="label">Data:</td>
                        <td><% out.println(gitPropcommitTime);%></td>
                    </tr>
                    <tr>
                        <td class="label">Messaggio:</td>
                        <td><% out.println(gitPropcommitMessageShort);%></td>
                    </tr>
                    <tr>
                        <td colspan="2" class="titolo" >Build</td>
                    </tr>
                    <tr>
                        <td class="label">Autore:</td>
                        <td><% out.println(gitPropbuildUserName);%></td>
                    </tr>
                    <tr>
                        <td class="label">Data:</td>
                        <td><% out.println(gitPropbuildTime);%></td>
                    </tr>
                </table>

            </div>

            <div id="util" class="tab">
                <a href="JobMarche.jsp">Test marcatura e mimetype formato</a>
                <br/>
                <a href="VerificaFirme">Lancia la verifica firme...</a>
                <br/>
                <a href="GestioneCRL.jsp">Gestione CRL</a>
                <br/>
            </div>

            <div id="vers" class="tab">
                <a href="ProvaUp.jsp">Lancia la pagina di versamento sincrono (max 15 file)...</a>
                <br/>
                <a href="ProvaAggAll.jsp">Lancia la pagina di aggiunta documenti (1 documento, max 15 file)...</a>
                <br/>
                <a href="ProvaUpMultiMedia.jsp">Lancia la pagina di versamento multimedia...</a>
                <br/>
                <a href="ProvaUpFascicolo.jsp">Lancia la pagina di versamento fascicolo...</a>
                <br/>
                <a href="ProvaAggMetadati.jsp">Lancia la pagina di versamento aggiornamento metadati Unit&agrave; documentaria...</a>
            </div>

            <div id="rec" class="tab">
                <a href="ProvaRecStatoC.jsp">Lancia la pagina di recupero stato conservazione...</a>
                <br/>
                <a href="ProvaRecUnitaDoc.jsp">Lancia la pagina di recupero unità documentaria...</a>
                <br/>
                <a href="ProvaRecAIPUnitaDoc.jsp">Lancia la pagina di recupero AIP unità documentaria...</a>
                <br/>
                <a href="ProvaRecProveCons.jsp">Lancia la pagina di recupero prove conservazione...</a>
                <br/>
                <a href="ProvaRecRapportiVers.jsp">Lancia la pagina di recupero rapporti di versamento...</a>
                <br/>
                <a href="ProvaRecUDMultiMedia.jsp">Lancia la pagina di Recupero Unità Documentaria Multimedia...</a>
                <br/>
                <a href="ProvaRecPCUDMultiMedia.jsp">Lancia la pagina di Recupero Prove Conservazione Multimedia...</a>
                <br/>
                <a href="ProvaRecUDConvPdf.jsp">Lancia la pagina di Recupero Sincrono componente con conversione PDF...</a>
                <br/>
                <a href="ProvaRecDipEsibizione.jsp">Lancia la pagina di Recupero Sincrono DIP per Esibizione...</a>
                <br/>
                <a href="ProvaRecStatoOggetto.jsp">Lancia la pagina di Recupero Stato Oggetto di PING...</a>
                <br/>
                <a href="ProvaRecAIPFascicolo.jsp">Lancia la pagina di recupero AIP del fascicolo...</a>
            </div>

            <div id="others" class="tab">
                <a href="ProvaStatusMon.jsp">Lancia il recupero dello status dell'applicazione..</a>
                <br/>
                <a href="ProvaSecFile.jsp">Lancia prova secure file...</a>
                <br/>
                <a href="InvalidaCache.jsp">Lancia la pagina di invalidazione della cache (solo amministratori)...</a>
                <br/>
                <a href="PuliziaDB.jsp">Pulisci DB per test creazione volumi</a>
                <br/>
                <a href="ProvaInvioRichiestaAnnullamentoVersamenti.jsp">Lancia la pagina di invio richiesta annullamento versamenti...</a>
            </div>

        </div>

    </body>
</html>
