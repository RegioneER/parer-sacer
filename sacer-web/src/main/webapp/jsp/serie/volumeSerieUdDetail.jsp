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

<%@ page import="it.eng.parer.slite.gen.form.SerieUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=SerieUDForm.VolumeDetail.DESCRIPTION%>">
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="<%=SerieUDForm.VolumeDetail.DESCRIPTION%>"/>
            <slf:listNavBarDetail name="<%= SerieUDForm.VolumiList.NAME%>" />  
            <sl:newLine skipLine="true"/>
            <slf:tab  name="<%= SerieUDForm.VolumeDetailTabs.NAME%>" tabElement="<%= SerieUDForm.VolumeDetailTabs.info_principali_volume%>">
                <slf:fieldSet borderHidden="false">
                    <slf:section name="<%=SerieUDForm.InfoSerieSection.NAME%>" styleClass="importantContainer w100">
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.NM_AMBIENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.NM_ENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.NM_STRUT%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />

                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.CD_COMPOSITO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.AA_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.DS_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.NM_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.CD_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.DT_INIZIO_SEL_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.DT_FINE_SEL_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.TI_STATO_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    </slf:section>
                    <slf:section name="<%=SerieUDForm.VolumeSection.NAME%>" styleClass="importantContainer w100">
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.NI_UNITA_DOC_VOL%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.CD_FIRST_UNITA_DOC_VOL%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.DT_FIRST_UNITA_DOC_VOL%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.CD_LAST_UNITA_DOC_VOL%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.VolumeDetail.DT_LAST_UNITA_DOC_VOL%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    </slf:section>
                    <slf:section name="<%=SerieUDForm.FiltriContenutoSection.NAME%>" styleClass="importantContainer w100">
                        <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.CD_UD_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.DT_UD_SERIE_DA%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.DT_UD_SERIE_A%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.INFO_UD_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.PG_UD_SERIE_DA%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.PG_UD_SERIE_A%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <sl:pulsantiera>
                            <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.RICERCA_CONTENUTO_SU_VOLUME%>"/>
                        </sl:pulsantiera>
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>
            <slf:tab  name="<%= SerieUDForm.VolumeDetailTabs.NAME%>" tabElement="<%= SerieUDForm.VolumeDetailTabs.indice_volume%>">
                <slf:lblField name="<%=SerieUDForm.VolumeDetail.CD_VER_XSD_IX_VOL%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=SerieUDForm.VolumeDetail.DT_CREAZIONE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <!--EVO#16486--> 
                <%--<slf:lblField name="<%=SerieUDForm.VolumeDetail.DS_URN_IX_VOL%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />--%>
                <slf:lblField name="<%=SerieUDForm.VolumeDetail.DS_URN_AIP_VOL%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=SerieUDForm.VolumeDetail.DS_URN_NORMALIZ_AIP_VOL%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <!--end EVO#16486-->
                <slf:lblField name="<%=SerieUDForm.VolumeDetail.DS_HASH_IX_VOL%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=SerieUDForm.VolumeDetail.DS_ALGO_HASH_IX_VOL%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=SerieUDForm.VolumeDetail.CD_ENCODING_HASH_IX_VOL%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=SerieUDForm.VolumeDetail.BL_IX_VOL%>" width="w100" controlWidth="w80" labelWidth="w20" /><sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=SerieUDForm.VolumeDetail.DOWNLOAD_IX_VOL%>" width="w30" position="right" />
                </sl:pulsantiera>
            </slf:tab>
            <slf:listNavBar name="<%= SerieUDForm.UdVolumeList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= SerieUDForm.UdVolumeList.NAME%>" />
            <slf:listNavBar  name="<%= SerieUDForm.UdVolumeList.NAME%>" />
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
