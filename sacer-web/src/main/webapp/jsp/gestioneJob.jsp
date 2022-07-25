<%@ page import="it.eng.parer.slite.gen.form.GestioneJobForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="Gestione Job">
        <script type="text/javascript" src="<c:url value='/js/sips/gestioneJobBox.js'/>" ></script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="GESTIONE JOB SACER"/>
            <slf:fieldSet legend="Creazione elenchi" >
                <slf:lblField name="<%=GestioneJobForm.CreazioneElenchi.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CreazioneElenchi.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CreazioneElenchi.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CreazioneElenchi.START_CREAZIONE_ELENCHI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneElenchi.STOP_CREAZIONE_ELENCHI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneElenchi.START_ONCE_CREAZIONE_ELENCHI%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CreazioneElenchi.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Creazione indici" >
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndici.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndici.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndici.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndici.START_CREAZIONE_INDICI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndici.STOP_CREAZIONE_INDICI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndici.START_ONCE_CREAZIONE_INDICI%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CreazioneIndici.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Verifica firme" >
                <slf:lblField name="<%=GestioneJobForm.VerificaFirme.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.VerificaFirme.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.VerificaFirme.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.VerificaFirme.START_VERIFICA_FIRME%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.VerificaFirme.STOP_VERIFICA_FIRME%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.VerificaFirme.START_ONCE_VERIFICA_FIRME%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.VerificaFirme.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Coda indici aip da elaborare" >
                <slf:lblField name="<%=GestioneJobForm.CodaIndiciAipDaElab.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CodaIndiciAipDaElab.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CodaIndiciAipDaElab.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CodaIndiciAipDaElab.START_CODA_INDICI_AIP_DA_ELAB%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CodaIndiciAipDaElab.STOP_CODA_INDICI_AIP_DA_ELAB%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CodaIndiciAipDaElab.START_ONCE_CODA_INDICI_AIP_DA_ELAB%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CodaIndiciAipDaElab.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Calcolo contenuto sacer" >
                <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoSacer.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoSacer.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoSacer.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoSacer.START_CALCOLO_CONTENUTO_SACER%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoSacer.STOP_CALCOLO_CONTENUTO_SACER%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoSacer.START_ONCE_CALCOLO_CONTENUTO_SACER%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CalcoloContenutoSacer.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Allineamento organizzazioni" >
                <slf:lblField name="<%=GestioneJobForm.AllineamentoOrganizzazioni.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.AllineamentoOrganizzazioni.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.AllineamentoOrganizzazioni.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.AllineamentoOrganizzazioni.START_ALLINEAMENTO_ORGANIZZAZIONI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.AllineamentoOrganizzazioni.STOP_ALLINEAMENTO_ORGANIZZAZIONI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.AllineamentoOrganizzazioni.START_ONCE_ALLINEAMENTO_ORGANIZZAZIONI%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.AllineamentoOrganizzazioni.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <slf:fieldSet legend="Aggiorna stato archiviazione" >
                <slf:lblField name="<%=GestioneJobForm.AggiornamentoStatoArk.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.AggiornamentoStatoArk.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.AggiornamentoStatoArk.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.AggiornamentoStatoArk.START_AGGIORNAMENTO_STATO_ARK%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.AggiornamentoStatoArk.STOP_AGGIORNAMENTO_STATO_ARK%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.AggiornamentoStatoArk.START_ONCE_AGGIORNAMENTO_STATO_ARK%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.AggiornamentoStatoArk.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Elabora sessioni recupero archiviazione" >
                <slf:lblField name="<%=GestioneJobForm.ElaboraSessioniRecupero.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.ElaboraSessioniRecupero.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.ElaboraSessioniRecupero.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.ElaboraSessioniRecupero.START_ELABORA_SESSIONI_RECUPERO%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ElaboraSessioniRecupero.STOP_ELABORA_SESSIONI_RECUPERO%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ElaboraSessioniRecupero.START_ONCE_ELABORA_SESSIONI_RECUPERO%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.ElaboraSessioniRecupero.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Registra schedulazioni Job TPI" >
                <slf:lblField name="<%=GestioneJobForm.RegistraSchedTpi.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.RegistraSchedTpi.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.RegistraSchedTpi.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.RegistraSchedTpi.START_REGISTRA_SCHED_TPI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.RegistraSchedTpi.STOP_REGISTRA_SCHED_TPI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.RegistraSchedTpi.START_ONCE_REGISTRA_SCHED_TPI%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.RegistraSchedTpi.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Creazione Indici AIP" >
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAip.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAip.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAip.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAip.START_CREAZIONE_INDICI_AIP%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAip.STOP_CREAZIONE_INDICI_AIP%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAip.START_ONCE_CREAZIONE_INDICI_AIP%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CreazioneIndiciAip.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Creazione Elenchi Indici AIP" >
                <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiIndiciAip.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiIndiciAip.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiIndiciAip.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiIndiciAip.START_CREAZIONE_ELENCHI_INDICI_AIP%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiIndiciAip.STOP_CREAZIONE_ELENCHI_INDICI_AIP%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiIndiciAip.START_ONCE_CREAZIONE_ELENCHI_INDICI_AIP%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CreazioneElenchiIndiciAip.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Verifica massiva versamenti falliti" >
                <slf:lblField name="<%=GestioneJobForm.VerificaMassivaVersamentiFalliti.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.VerificaMassivaVersamentiFalliti.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.VerificaMassivaVersamentiFalliti.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.VerificaMassivaVersamentiFalliti.START_VERIFICA_MASSIVA_VERSAMENTI_FALLITI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.VerificaMassivaVersamentiFalliti.STOP_VERIFICA_MASSIVA_VERSAMENTI_FALLITI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.VerificaMassivaVersamentiFalliti.START_ONCE_VERIFICA_MASSIVA_VERSAMENTI_FALLITI%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.VerificaMassivaVersamentiFalliti.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Verifica periodo registro" >
                <slf:lblField name="<%=GestioneJobForm.VerificaPeriodoRegistro.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.VerificaPeriodoRegistro.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.VerificaPeriodoRegistro.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.VerificaPeriodoRegistro.START_VERIFICA_PERIODO_REGISTRO%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.VerificaPeriodoRegistro.STOP_VERIFICA_PERIODO_REGISTRO%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.VerificaPeriodoRegistro.START_ONCE_VERIFICA_PERIODO_REGISTRO%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.VerificaPeriodoRegistro.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
             <slf:fieldSet legend="Verifica periodo tipo fascicolo" >
                <slf:lblField name="<%=GestioneJobForm.VerificaPeriodoTipoFascicolo.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.VerificaPeriodoTipoFascicolo.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.VerificaPeriodoTipoFascicolo.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.VerificaPeriodoTipoFascicolo.START_VERIFICA_PERIODO_TIPO_FASC%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.VerificaPeriodoTipoFascicolo.STOP_VERIFICA_PERIODO_TIPO_FASC%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.VerificaPeriodoTipoFascicolo.START_ONCE_VERIFICA_PERIODO_TIPO_FASC%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.VerificaPeriodoTipoFascicolo.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Creazione automatica serie" >
                <slf:lblField name="<%=GestioneJobForm.CreazioneAutomaticaSerie.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CreazioneAutomaticaSerie.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CreazioneAutomaticaSerie.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CreazioneAutomaticaSerie.START_CREAZIONE_AUTOMATICA_SERIE%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneAutomaticaSerie.STOP_CREAZIONE_AUTOMATICA_SERIE%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneAutomaticaSerie.START_ONCE_CREAZIONE_AUTOMATICA_SERIE%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CreazioneAutomaticaSerie.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Creazione indice AIP serie ud" >
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAipSerieUd.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAipSerieUd.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAipSerieUd.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAipSerieUd.START_CREAZIONE_INDICI_AIP_SERIE_UD%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAipSerieUd.STOP_CREAZIONE_INDICI_AIP_SERIE_UD%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAipSerieUd.START_ONCE_CREAZIONE_INDICI_AIP_SERIE_UD%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CreazioneIndiciAipSerieUd.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Generazione automatica contenuto effettivo serie" >
                <slf:lblField name="<%=GestioneJobForm.GenerazioneAutomaticaEffettivoSerie.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.GenerazioneAutomaticaEffettivoSerie.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.GenerazioneAutomaticaEffettivoSerie.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.GenerazioneAutomaticaEffettivoSerie.START_GENERAZIONE_AUTOMATICA_EFFETTIVO%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.GenerazioneAutomaticaEffettivoSerie.STOP_GENERAZIONE_AUTOMATICA_EFFETTIVO%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.GenerazioneAutomaticaEffettivoSerie.START_ONCE_GENERAZIONE_AUTOMATICA_EFFETTIVO%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.GenerazioneAutomaticaEffettivoSerie.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Controllo automatico contenuto effettivo serie" >
                <slf:lblField name="<%=GestioneJobForm.ControlloAutomaticoEffettivoSerie.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.ControlloAutomaticoEffettivoSerie.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.ControlloAutomaticoEffettivoSerie.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.ControlloAutomaticoEffettivoSerie.START_CONTROLLO_AUTOMATICO_EFFETTIVO%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ControlloAutomaticoEffettivoSerie.STOP_CONTROLLO_AUTOMATICO_EFFETTIVO%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ControlloAutomaticoEffettivoSerie.START_ONCE_CONTROLLO_AUTOMATICO_EFFETTIVO%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.ControlloAutomaticoEffettivoSerie.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Evasione richieste di annullamento versamenti" >
                <slf:lblField name="<%=GestioneJobForm.EvasioneRichiesteAnnullamentoVersamenti.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.EvasioneRichiesteAnnullamentoVersamenti.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.EvasioneRichiesteAnnullamentoVersamenti.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.EvasioneRichiesteAnnullamentoVersamenti.START_EVASIONE_RICHIESTE_ANNUL_VERS%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.EvasioneRichiesteAnnullamentoVersamenti.STOP_EVASIONE_RICHIESTE_ANNUL_VERS%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.EvasioneRichiesteAnnullamentoVersamenti.START_ONCE_EVASIONE_RICHIESTE_ANNUL_VERS%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.EvasioneRichiesteAnnullamentoVersamenti.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="<%=GestioneJobForm.InizializzazioneLog.DESCRIPTION%>" >
                <slf:lblField name="<%=GestioneJobForm.InizializzazioneLog.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.InizializzazioneLog.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.InizializzazioneLog.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.InizializzazioneLog.START_ONCE_INIZIALIZZAZIONE_LOG%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.InizializzazioneLog.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="<%=GestioneJobForm.AllineamentoLog.DESCRIPTION%>" >
                <slf:lblField name="<%=GestioneJobForm.AllineamentoLog.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.AllineamentoLog.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.AllineamentoLog.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.AllineamentoLog.START_ALLINEAMENTO_LOG%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.AllineamentoLog.STOP_ALLINEAMENTO_LOG%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.AllineamentoLog.START_ONCE_ALLINEAMENTO_LOG%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.AllineamentoLog.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Calcolo struttura" >
                <slf:lblField name="<%=GestioneJobForm.CalcoloStruttura.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CalcoloStruttura.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CalcoloStruttura.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CalcoloStruttura.START_CALCOLO_STRUTTURA%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CalcoloStruttura.STOP_CALCOLO_STRUTTURA%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CalcoloStruttura.START_ONCE_CALCOLO_STRUTTURA%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CalcoloStruttura.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Evasione richieste di restituzione archivio" >
                <slf:lblField name="<%=GestioneJobForm.EvasioneRichiesteRestituzioneArchivio.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.EvasioneRichiesteRestituzioneArchivio.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.EvasioneRichiesteRestituzioneArchivio.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.EvasioneRichiesteRestituzioneArchivio.START_EVASIONE_RICHIESTE_REST_ARCH%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.EvasioneRichiesteRestituzioneArchivio.STOP_EVASIONE_RICHIESTE_REST_ARCH%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.EvasioneRichiesteRestituzioneArchivio.START_ONCE_EVASIONE_RICHIESTE_REST_ARCH%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.EvasioneRichiesteRestituzioneArchivio.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Calcolo contenuto fascicoli" >
                <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoFascicoli.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoFascicoli.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoFascicoli.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoFascicoli.START_CALCOLO_CONTENUTO_FASCICOLI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoFascicoli.STOP_CALCOLO_CONTENUTO_FASCICOLI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoFascicoli.START_ONCE_CALCOLO_CONTENUTO_FASCICOLI%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CalcoloContenutoFascicoli.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Allineamento enti convenzionati" >
                <slf:lblField name="<%=GestioneJobForm.AllineaEntiConvenzionati.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.AllineaEntiConvenzionati.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.AllineaEntiConvenzionati.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.AllineaEntiConvenzionati.START_ALLINEA_ENTI_CONVENZIONATI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.AllineaEntiConvenzionati.STOP_ALLINEA_ENTI_CONVENZIONATI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.AllineaEntiConvenzionati.START_ONCE_ALLINEA_ENTI_CONVENZIONATI%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.AllineaEntiConvenzionati.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Creazione elenchi versamento fascicoli" >
                <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiVersFascicoli.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiVersFascicoli.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiVersFascicoli.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiVersFascicoli.START_CREAZIONE_ELENCHI_VERS_FASCICOLI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiVersFascicoli.STOP_CREAZIONE_ELENCHI_VERS_FASCICOLI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiVersFascicoli.START_ONCE_CREAZIONE_ELENCHI_VERS_FASCICOLI%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CreazioneElenchiVersFascicoli.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Creazione indici versamento fascicoli" >
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciVersFascicoli.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciVersFascicoli.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciVersFascicoli.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciVersFascicoli.START_CREAZIONE_INDICI_VERS_FASCICOLI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciVersFascicoli.STOP_CREAZIONE_INDICI_VERS_FASCICOLI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciVersFascicoli.START_ONCE_CREAZIONE_INDICI_VERS_FASCICOLI%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CreazioneIndiciVersFascicoli.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Validazione fascicoli" >
                <slf:lblField name="<%=GestioneJobForm.ValidazioneFascicoli.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.ValidazioneFascicoli.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.ValidazioneFascicoli.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.ValidazioneFascicoli.START_VALIDAZIONE_FASCICOLI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ValidazioneFascicoli.STOP_VALIDAZIONE_FASCICOLI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ValidazioneFascicoli.START_ONCE_VALIDAZIONE_FASCICOLI%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.ValidazioneFascicoli.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Creazione indici aip fascicoli" >
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAipFascicoli.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAipFascicoli.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAipFascicoli.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAipFascicoli.START_CREAZIONE_INDICI_AIP_FASC%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAipFascicoli.STOP_CREAZIONE_INDICI_AIP_FASC%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneIndiciAipFascicoli.START_ONCE_CREAZIONE_INDICI_AIP_FASC%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CreazioneIndiciAipFascicoli.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Creazione elenchi indici aip fascicoli" >
                <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiIndiciAipFascicoli.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiIndiciAipFascicoli.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiIndiciAipFascicoli.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiIndiciAipFascicoli.START_CREAZIONE_ELENCHI_INDICI_AIP_FASC%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiIndiciAipFascicoli.STOP_CREAZIONE_ELENCHI_INDICI_AIP_FASC%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CreazioneElenchiIndiciAipFascicoli.START_ONCE_CREAZIONE_ELENCHI_INDICI_AIP_FASC%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CreazioneElenchiIndiciAipFascicoli.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Preparazione partizione da migrare 1" >
                <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare1.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare1.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare1.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare1.START_PREPARAZIONE_PARTIZIONE_DA_MIGRARE1%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare1.STOP_PREPARAZIONE_PARTIZIONE_DA_MIGRARE1%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare1.START_ONCE_PREPARAZIONE_PARTIZIONE_DA_MIGRARE1%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.PreparazionePartizioneDaMigrare1.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Preparazione partizione da migrare 2" >
                <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare2.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare2.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare2.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare2.START_PREPARAZIONE_PARTIZIONE_DA_MIGRARE2%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare2.STOP_PREPARAZIONE_PARTIZIONE_DA_MIGRARE2%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare2.START_ONCE_PREPARAZIONE_PARTIZIONE_DA_MIGRARE2%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.PreparazionePartizioneDaMigrare2.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Preparazione partizione da migrare 3" >
                <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare3.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare3.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare3.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare3.START_PREPARAZIONE_PARTIZIONE_DA_MIGRARE3%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare3.STOP_PREPARAZIONE_PARTIZIONE_DA_MIGRARE3%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare3.START_ONCE_PREPARAZIONE_PARTIZIONE_DA_MIGRARE3%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.PreparazionePartizioneDaMigrare3.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Preparazione partizione da migrare 4" >
                <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare4.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare4.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare4.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare4.START_PREPARAZIONE_PARTIZIONE_DA_MIGRARE4%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare4.STOP_PREPARAZIONE_PARTIZIONE_DA_MIGRARE4%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.PreparazionePartizioneDaMigrare4.START_ONCE_PREPARAZIONE_PARTIZIONE_DA_MIGRARE4%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.PreparazionePartizioneDaMigrare4.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Producer coda da migrare 1" >
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare1.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare1.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare1.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare1.START_PRODUCER_CODA_DA_MIGRARE1%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare1.STOP_PRODUCER_CODA_DA_MIGRARE1%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare1.START_ONCE_PRODUCER_CODA_DA_MIGRARE1%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.ProducerCodaDaMigrare1.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Producer coda da migrare 2" >
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare2.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare2.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare2.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare2.START_PRODUCER_CODA_DA_MIGRARE2%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare2.STOP_PRODUCER_CODA_DA_MIGRARE2%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare2.START_ONCE_PRODUCER_CODA_DA_MIGRARE2%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.ProducerCodaDaMigrare2.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Producer coda da migrare 3" >
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare3.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare3.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare3.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare3.START_PRODUCER_CODA_DA_MIGRARE3%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare3.STOP_PRODUCER_CODA_DA_MIGRARE3%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare3.START_ONCE_PRODUCER_CODA_DA_MIGRARE3%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.ProducerCodaDaMigrare3.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Producer coda da migrare 4" >
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare4.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare4.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare4.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare4.START_PRODUCER_CODA_DA_MIGRARE4%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare4.STOP_PRODUCER_CODA_DA_MIGRARE4%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaDaMigrare4.START_ONCE_PRODUCER_CODA_DA_MIGRARE4%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.ProducerCodaDaMigrare4.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Verifica migrazione subpartizione" >
                <slf:lblField name="<%=GestioneJobForm.VerificaMigrazioneSubpartizione.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.VerificaMigrazioneSubpartizione.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.VerificaMigrazioneSubpartizione.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.VerificaMigrazioneSubpartizione.START_VERIFICA_MIGRAZIONE_SUBPARTIZIONE%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.VerificaMigrazioneSubpartizione.STOP_VERIFICA_MIGRAZIONE_SUBPARTIZIONE%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.VerificaMigrazioneSubpartizione.START_ONCE_VERIFICA_MIGRAZIONE_SUBPARTIZIONE%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.VerificaMigrazioneSubpartizione.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Controlla migrazione subpartizione" >
                <slf:lblField name="<%=GestioneJobForm.ControllaMigrazioneSubpartizione.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.ControllaMigrazioneSubpartizione.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.ControllaMigrazioneSubpartizione.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.ControllaMigrazioneSubpartizione.START_CONTROLLA_MIGRAZIONE_SUBPARTIZIONE%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ControllaMigrazioneSubpartizione.STOP_CONTROLLA_MIGRAZIONE_SUBPARTIZIONE%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ControllaMigrazioneSubpartizione.START_ONCE_CONTROLLA_MIGRAZIONE_SUBPARTIZIONE%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.ControllaMigrazioneSubpartizione.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Calcolo contenuto aggiornamenti metadati" >
                <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoAggiornamentiMetadati.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoAggiornamentiMetadati.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoAggiornamentiMetadati.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoAggiornamentiMetadati.START_CALCOLO_CONTENUTO_AGG%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoAggiornamentiMetadati.STOP_CALCOLO_CONTENUTO_AGG%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CalcoloContenutoAggiornamentiMetadati.START_ONCE_CALCOLO_CONTENUTO_AGG%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CalcoloContenutoAggiornamentiMetadati.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Calcolo consistenza" >
                <slf:lblField name="<%=GestioneJobForm.CalcoloConsistenza.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.CalcoloConsistenza.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.CalcoloConsistenza.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.CalcoloConsistenza.START_CALCOLO_CONSISTENZA%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CalcoloConsistenza.STOP_CALCOLO_CONSISTENZA%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.CalcoloConsistenza.START_ONCE_CALCOLO_CONSISTENZA%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.CalcoloConsistenza.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
