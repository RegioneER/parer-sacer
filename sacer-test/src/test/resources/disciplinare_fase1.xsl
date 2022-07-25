<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:variable name="dataRiferimento" select="/fotoOggetto/recordMaster/keyRecord/datoKey[colonnaKey='data_generazione']/valoreKey"/>
    <xsl:variable name="dataRiferimentoPerConfronto" select="concat(substring-after(substring-after($dataRiferimento,'/'),'/'),substring-before(substring-after($dataRiferimento,'/'),'/'),substring-before($dataRiferimento,'/'))"/>
    <xsl:variable name="nomeEnteConvenzionato" select="/fotoOggetto/recordChild[tipoRecord='Ente convenzionato']/child/keyRecord/datoKey[colonnaKey='nm_ente_convenz']/valoreKey"/>
    <xsl:variable name="struttura" select="/fotoOggetto/recordChild[tipoRecord='Struttura']"/>
    <xsl:variable name="nomeAmbiente" select="$struttura/child/keyRecord/datoKey[colonnaKey='nm_ambiente']/valoreKey" />
    <xsl:variable name="nomeEnte" select="$struttura/child/keyRecord/datoKey[colonnaKey='nm_ente']/valoreKey"/>
    <xsl:variable name="descEnte" select="$struttura/child/datoRecord[colonnaDato='ds_ente']/valoreDato"/>
    <xsl:variable name="nomeStruttura" select="$struttura/child/keyRecord/datoKey[colonnaKey='nm_strut']/valoreKey"/>
    <xsl:variable name="descStruttura" select="$struttura/child/datoRecord[colonnaDato='ds_strut']/valoreDato"/>
    <xsl:variable name="organizzazione" select="concat($nomeAmbiente,' / ',$nomeEnte,' / ',$nomeStruttura)"/>
    <xsl:variable name="organizzazioneParziale" select="concat($nomeAmbiente,' / ',$nomeEnte)"/>
        
    <xsl:template match="/fotoOggetto">
        <disciplinare>
            <xsl:call-template name="testataDisciplinare" />
            <xsl:call-template name="introduzione" />
            <xsl:call-template name="descUtenti" />
            <xsl:call-template name="sistemiVersanti" />
            <xsl:call-template name="modalitaTrasmissione" />
            <xsl:call-template name="verificheControlli" />
            <xsl:call-template name="oggettiDaTrasformare" />
            <xsl:call-template name="gestioneSipRifiutati" />
            <xsl:call-template name="generazioneSerie" />
            <xsl:call-template name="modalitaAnnullamentoVersamenti" />
            <xsl:call-template name="modalitaRestituzioneOggettiVersati" />
            <xsl:call-template name="tipologieUD" />
        </disciplinare>
    </xsl:template>

    <xsl:template name="testataDisciplinare">
        <capitolo nome="DISCIPLINARE TECNICO" tipo="testata" livello="1" numerato="no">
            <versione>
                <xsl:value-of select="/fotoOggetto/recordMaster/keyRecord/datoKey[colonnaKey='data_generazione']/valoreKey"/>
            </versione>
            <nomeEnte>
                <xsl:value-of select="$nomeEnteConvenzionato" />
            </nomeEnte>
            <nomeStruttura>
                <xsl:value-of select="$nomeStruttura" />
            </nomeStruttura>
            <descStruttura>
                <xsl:value-of select="$descStruttura" />
            </descStruttura>
            <enteVersante>
                <xsl:value-of select="$nomeEnte" />
            </enteVersante>
            <descEnteVersante>
                <xsl:value-of select="$descEnte" />
            </descEnteVersante>
            <dataDecorrenza>
                <xsl:variable name="dataDecorrenza" select="/fotoOggetto/recordChild[tipoRecord='Ente convenzionato']/child/recordChild[tipoRecord='Accordo']/child/datoRecord[colonnaDato='dt_reg_accordo']/valoreDato"/>
                <xsl:value-of select="$dataDecorrenza"/>
            </dataDecorrenza>
            <dataFineValidita>
                <xsl:variable name="dataFineValidita" select="/fotoOggetto/recordChild[tipoRecord='Ente convenzionato']/child/recordChild[tipoRecord='Accordo']/child/datoRecord[colonnaDato='dt_scad_accordo']/valoreDato"/>
                <xsl:value-of select="$dataFineValidita" />
            </dataFineValidita>
        </capitolo>
    </xsl:template>

    <xsl:template name="introduzione">
        <capitolo nome="INTRODUZIONE" tipo="introduzione" livello="1" numerato="no">
            <nomeEnte>
                <xsl:value-of select="$nomeEnteConvenzionato" />
            </nomeEnte>
        </capitolo>
    </xsl:template>

    <xsl:template name="descUtenti">
        <capitolo nome="Utenti del sistema" tipo="utentiSistema" livello="1" numerato="no">
            <capitolo livello="2" numerato="no" nome="Profili di accesso degli utenti nel Sistema" tipo="profiliAccesso">
            </capitolo>
        </capitolo>    
        <capitolo nome="Utenti abilitati" tipo="utentiAbilitati" livello="1" numerato="no">
            <xsl:variable name="utentiAbilitati" select="/fotoOggetto/recordChild[tipoRecord='Struttura']/child/recordChild[tipoRecord='Utenti abilitati']/child"/>
            <xsl:if test="$utentiAbilitati"> 
                <xsl:element name="tabella">
                    <xsl:attribute name="tipo">utentiAbilitati</xsl:attribute>
                    <xsl:attribute name="numerato">no</xsl:attribute>
                    <xsl:for-each select="$utentiAbilitati">
                        <riga>
                            <nome>
                                <xsl:value-of select="datoRecord[colonnaDato='nm_nome_user']/valoreDato"/>
                            </nome>
                            <cognome>
                                <xsl:value-of select="datoRecord[colonnaDato='nm_cognome_user']/valoreDato"/>
                            </cognome>
                            <ruoli>
                                <xsl:value-of select="datoRecord[colonnaDato='lista_ruo']/valoreDato"/>
                            </ruoli>
                            <recapiti>
                                <xsl:value-of select="datoRecord[colonnaDato='ds_email']/valoreDato"/>
                            </recapiti>
                            <abilitazioniTipiUd><xsl:value-of select="datoRecord[colonnaDato='lista_tipo_ud_abil']/valoreDato"/></abilitazioniTipiUd>
                            <abilitazioniTipiDoc><xsl:value-of select="datoRecord[colonnaDato='lista_tipo_doc_abil']/valoreDato"/></abilitazioniTipiDoc>
                            <abilitazioniRegistri><xsl:value-of select="datoRecord[colonnaDato='lista_reg_abil']/valoreDato"/></abilitazioniRegistri>
                        </riga>
                    </xsl:for-each>
                </xsl:element>                  
            </xsl:if>
        </capitolo>
        <capitolo nome="Referenti" tipo="utentiReferenti" livello="1" numerato="no">
            <xsl:variable name="utentiReferenti" select="/fotoOggetto/recordChild[tipoRecord='Struttura']/child/recordChild[tipoRecord='Utenti referenti']/child"/>
            <xsl:if test="$utentiReferenti"> 
                <xsl:element name="tabella">
                    <xsl:attribute name="tipo">utentiReferenti</xsl:attribute>
                    <xsl:attribute name="numerato">no</xsl:attribute>
                    <xsl:for-each select="$utentiReferenti">
                        <riga>
                            <nome>
                                <xsl:value-of select="datoRecord[colonnaDato='nm_nome_user']/valoreDato"/>
                            </nome>
                            <cognome>
                                <xsl:value-of select="datoRecord[colonnaDato='nm_cognome_user']/valoreDato"/>
                            </cognome>
                            <recapiti>
                                <xsl:value-of select="datoRecord[colonnaDato='ds_email']/valoreDato"/>
                            </recapiti>
                            <tipoReferente>
                                <xsl:value-of select="datoRecord[colonnaDato='ti_user_ark_rif']/valoreDato"/>
                            </tipoReferente>
                            <note>
                                <xsl:value-of select="datoRecord[colonnaDato='dl_note']/valoreDato"/>
                            </note>
                        </riga>
                    </xsl:for-each>
                </xsl:element>                  
            </xsl:if>
        </capitolo>
    </xsl:template>

    <xsl:template name="sistemiVersanti">
        <capitolo livello="1" numerato="no" nome="Sistemi versanti" tipo="sistemiVersanti">
            <tabella numerato="no">
            </tabella>
        </capitolo>
    </xsl:template>

    <xsl:template name="modalitaTrasmissione">
        <capitolo livello="1" numerato="si" nome="Modalità di trasmissione dei pacchetti di versamento" tipo="modalitaTrasmissione">
            <tabella numerato="si">
            </tabella>
        </capitolo>
    </xsl:template>

    <xsl:template name="verificheControlli">
        <capitolo nome="Verifiche e controlli al versamento" tipo="verificheControlli" livello="1" numerato="si">
            <capitolo livello="2" numerato="si" nome="Parametri di accettazione formati" tipo="controlloFormati">
                <abilitaControlloFormato>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_abilita_contr_fmt']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </abilitaControlloFormato>
                <abilitaControlloFormatoNegativo>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_contr_fmt_neg']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </abilitaControlloFormatoNegativo>
                <forzaFormato>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_forza_fmt']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </forzaFormato>
                <tabella nome="Formati ammessi" tipo="formatiAmmessi" livello="2" numerato="no">
                    <xsl:for-each select="/fotoOggetto/recordChild[tipoRecord='Formati ammessi']/child">
                        <xsl:element name="riga">
                            <nomeFormato>
                                <xsl:value-of select="keyRecord/datoKey[colonnaKey='nm_formato_file_doc']/valoreKey" />
                            </nomeFormato>
                            <mimeType>
                                <xsl:value-of select="datoRecord[colonnaDato='nm_mime_type']/valoreDato" />
                            </mimeType>
                            <idoneitaConservazione>
                                <xsl:value-of select="datoRecord[colonnaDato='ti_esito_contr_formato']/valoreDato" />
                            </idoneitaConservazione>
                        </xsl:element>
                    </xsl:for-each>
                </tabella> 
            </capitolo>
            <capitolo livello="2" numerato="no" nome="Controlli sulla firma abilitati al versamento" tipo="controlloFirme">
                <controlloCrittografico>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_abilita_contr_crittog_vers']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </controlloCrittografico>
                <controlloCatenaTrusted>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_abilita_contr_trust_vers']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </controlloCatenaTrusted>
                <controlloCertificato>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_abilita_contr_certif_vers']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </controlloCertificato>
                <controlloCrl>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_abilita_contr_crl_vers']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </controlloCrl>
            </capitolo>
            <capitolo livello="2" numerato="no" nome="Parametri di accettazione firme" tipo="parametriAccettazioneFirme">
                <accettaFirmaSconosciuta>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_firma_noconos']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaFirmaSconosciuta>
                <accettaCatenaTrustedNegativo>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_contr_trust_neg']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaCatenaTrustedNegativo>
                <accettaControlloCertificatoNoCert>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_contr_certif_nocert']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaControlloCertificatoNoCert>
                <accettaControlloCrlNonScaricabile>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_contr_crl_noscar']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaControlloCrlNonScaricabile>
                <accettaFirmaNoDelibera45>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_firma_giugno_2011']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaFirmaNoDelibera45>
                <accettaControlloCrittograficoNegativo>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_contr_crittog_neg']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaControlloCrittograficoNegativo>
                <accettaControlloCrlNegativo>    
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_contr_crl_neg']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaControlloCrlNegativo>
                <accettaFirmaNonConforme>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_firma_noconf']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaFirmaNonConforme>
                <accettaControlloCertificatoScaduto>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_contr_certif_scad']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaControlloCertificatoScaduto>
                <accettaControlloCrlScaduto>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_contr_crl_scad']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaControlloCrlScaduto>
                <accettaMarcaSconosciuta>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_marca_noconos']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaMarcaSconosciuta>
                <accettaControlloCertificatoNonValido>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_contr_certif_noval']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaControlloCertificatoNonValido>
                <accettaControlloCrlNonValida>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_contr_crl_noval']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaControlloCrlNonValida>
            </capitolo>
            <capitolo livello="2" numerato="no" nome="Parametri di accettazione hash versato" tipo="controlloHash">
                <abilitaControlloHash>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_abilita_contr_hash_vers']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </abilitaControlloHash>
                <accettaControlloHashNegativo>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_contr_hash_neg']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaControlloHashNegativo>
                <forzaHash>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_forza_hash_vers']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </forzaHash>    
            </capitolo>
            <capitolo livello="2" numerato="no" nome="Parametri di controllo sulla conformità del registro" tipo="controlloConformitaRegistro">
                <abilitaControlloFormatoNumero>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_abilita_contr_fmt_num']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </abilitaControlloFormatoNumero>
                <accettaControlloFormatoNumeroNegativo>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_fmt_num_neg']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </accettaControlloFormatoNumeroNegativo>
                <forzaFormatoNumero>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_forza_fmt_num']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </forzaFormatoNumero>
            </capitolo>
            <capitolo livello="2" numerato="no" nome="Obbligatorietà dati di profilo dell’unità documentaria" tipo="controlloObblDatiProfiloUD">
                <obbligatorietaOggetto>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_obbl_oggetto']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </obbligatorietaOggetto>
                <obbligatorietaData>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_obbl_data']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </obbligatorietaData>
            </capitolo>
        </capitolo>
    </xsl:template>

    <xsl:template name="oggettiDaTrasformare">
        <capitolo nome="Tipi oggetto da trasformare" tipo="oggettiDaTrasformare" livello="1" numerato="no">
            <xsl:element name="tabella">
                <xsl:attribute name="tipo">tipiOggettoDaTrasformare</xsl:attribute>
                <xsl:attribute name="numerato">no</xsl:attribute>
                <xsl:attribute name="nome">Tipi oggetto da trasformare</xsl:attribute>

                <xsl:for-each select="/fotoOggetto/recordChild[tipoRecord='Struttura']/child/recordChild[tipoRecord='Tipi oggetto da trasformare']/child">
                    <riga>
                        <nome>
                            <xsl:value-of select="keyRecord/datoKey[colonnaKey='nm_tipo_object_da_trasf']/valoreKey"/>
                        </nome>
                        <descrizione>
                            <xsl:value-of select="datoRecord[colonnaDato='ds_tipo_object_da_trasf']/valoreDato"/>
                        </descrizione>
                        <versatore>
                            <xsl:value-of select="datoRecord[colonnaDato='nm_vers_gen']/valoreDato"/>
                        </versatore>
                        <trasformazione>
                            <xsl:value-of select="datoRecord[colonnaDato='ds_trasf']/valoreDato"/>
                        </trasformazione>
                    </riga>
                </xsl:for-each>
            </xsl:element>
        </capitolo> 
    </xsl:template>

    <xsl:template name="gestioneSipRifiutati">
        <capitolo nome="Gestione dei SIP rifiutati" tipo="gestioneSipRifiutati" livello="1" numerato="no">
        </capitolo> 
    </xsl:template>

    <xsl:template name="generazioneSerie">
        <capitolo nome="Generazione delle Serie documentarie" tipo="generazioneSerie" livello="1" numerato="no">
            <xsl:element name="tabella">
                <xsl:attribute name="tipo">tipiSerie</xsl:attribute>
                <xsl:attribute name="numerato">no</xsl:attribute>
                <xsl:attribute name="nome">Elenco tipi serie</xsl:attribute>

                <xsl:for-each select="/fotoOggetto/recordChild[tipoRecord='Struttura']/child/recordChild[tipoRecord='Tipi serie']/child">
                    <riga>
                        <nome>
                            <xsl:value-of select="keyRecord/datoKey[colonnaKey='nm_tipo_serie']/valoreKey"/>
                        </nome>
                        <descrizione>
                            <xsl:value-of select="datoRecord[colonnaDato='ds_tipo_serie']/valoreDato"/>
                        </descrizione>
                        <anni>
                            <xsl:value-of select="datoRecord[colonnaDato='ni_anni_conserv']/valoreDato"/>
                        </anni>
                    </riga>
                </xsl:for-each>
            </xsl:element>
        </capitolo> 
    </xsl:template>

    <xsl:template name="modalitaAnnullamentoVersamenti">
        <capitolo nome="Modalità di annullamento dei versamenti" tipo="modalitaAnnullamentoVersamenti" livello="1" numerato="no">
        </capitolo> 
    </xsl:template>

    <xsl:template name="modalitaRestituzioneOggettiVersati">
        <capitolo nome="Modalità di restituzione degli oggetti versati in conservazione in caso di recesso" tipo="modalitaRestituzioneOggettiVersati" livello="1" numerato="si">
        </capitolo> 
    </xsl:template>

    <xsl:template name="tipologieUD">
        <capitolo nome="Tipologie di unità documentarie" tipo="tipologieUD" livello="1" numerato="no">
            <xsl:for-each select="/fotoOggetto/recordChild[tipoRecord='Struttura']/child/recordChild[tipoRecord='Tipo unità documentaria']/child">
                <xsl:variable name="nomeTipoUD" select="keyRecord/datoKey[colonnaKey='nm_tipo_unita_doc']/valoreKey" />
                <xsl:element name="capitolo">
                    <xsl:attribute name="nome">Tipologia di unità documentaria <xsl:value-of select="keyRecord/datoKey[colonnaKey='nm_tipo_unita_doc']/valoreKey" /></xsl:attribute>
                    <xsl:attribute name="tipo">unitaDocumentaria</xsl:attribute>
                    <xsl:attribute name="livello">2</xsl:attribute>
                    <xsl:attribute name="numerato">no</xsl:attribute>
                    <nomeUD>
                        <xsl:value-of select="$nomeTipoUD" />
                    </nomeUD>
                    <descrizioneUD>
                        <xsl:value-of select="datoRecord[colonnaDato='ds_tipo_unita_doc']/valoreDato" />
                    </descrizioneUD>
                    <noteUD>
                        <xsl:value-of select="datoRecord[colonnaDato='dl_note_tipo_ud']/valoreDato" />
                    </noteUD>
                    <sistemiVersanti>
                        <xsl:for-each select="recordChild[tipoRecord='Dati sistemi versanti']/child/recordChild[tipoRecord='Sistemi versanti']/child/datoRecord[colonnaDato='nm_sistema_versante']/valoreDato">
                            <xsl:value-of select="." />
                            <xsl:if test="not(position()=last())">
                                <xsl:text>, </xsl:text>
                            </xsl:if>
                        </xsl:for-each>
                    </sistemiVersanti>
                    <utentiVersatori>
                        <xsl:variable name="utentiVersatori" select="recordChild[tipoRecord='Dati sistemi versanti']/child/recordChild[tipoRecord='Utenti versatori']/child/keyRecord/datoKey[colonnaKey='nm_userid']/valoreKey"/> 
                        <xsl:for-each select="$utentiVersatori">
                            <xsl:value-of select="." />
                            <xsl:if test="not(position()=last())">
                                <xsl:text>, </xsl:text>
                            </xsl:if>
                        </xsl:for-each>
                    </utentiVersatori>
                    <dataPrimoVersamento>
                        <xsl:variable name="dataPrimoVers" select="recordChild[tipoRecord='Dati sistemi versanti']/child/datoRecord[colonnaDato='dt_primo_versamento']/valoreDato" />
                        <xsl:choose>                                              
                            <xsl:when test="not($dataPrimoVers) or $dataPrimoVers='null'">
                                <xsl:text></xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$dataPrimoVers" />
                            </xsl:otherwise>
                        </xsl:choose>
                    </dataPrimoVersamento>

                    <xsl:element name="capitolo">
                        <xsl:attribute name="nome">Descrizione della tipologia di unità documentaria</xsl:attribute>
                        <xsl:attribute name="tipo">descrizioneUD</xsl:attribute>
                        <xsl:attribute name="livello">2</xsl:attribute>
                        <xsl:attribute name="numerato">no</xsl:attribute>
                        <xsl:attribute name="capitoloInterno">si</xsl:attribute>
                    </xsl:element>
                    
                    <xsl:if test="recordChild[tipoRecord='Tipi struttura unità documentaria']/child">
                        <xsl:element name="capitolo"> <!-- Capitolo tipi struttura -->
                            <xsl:attribute name="nome">Tipi struttura dell'unità documentaria</xsl:attribute>
                            <xsl:attribute name="tipo">struttureUD</xsl:attribute>
                            <xsl:attribute name="livello">2</xsl:attribute>
                            <xsl:attribute name="numerato">no</xsl:attribute>
                            <xsl:attribute name="capitoloInterno">si</xsl:attribute>
                            <xsl:element name="tabella">
                                <xsl:attribute name="tipo">struttureUD</xsl:attribute>
                                <xsl:attribute name="numerato">no</xsl:attribute>
                                <xsl:attribute name="nome">Tipi struttura unità documentaria</xsl:attribute>
                                <xsl:for-each select="recordChild[tipoRecord='Tipi struttura unità documentaria']/child">
                                    <riga>
                                        <nome>
                                            <xsl:value-of select="keyRecord/datoKey[colonnaKey='nm_tipo_strut_unita_doc']/valoreKey"/>
                                        </nome>
                                        <descrizione>
                                            <xsl:value-of select="datoRecord[colonnaDato='ds_tipo_strut_unita_doc']/valoreDato"/>
                                        </descrizione>
                                        <annoInizioValidita><xsl:value-of select="datoRecord[colonnaDato='aa_min_tipo_strut_unita_doc']/valoreDato"/></annoInizioValidita>
                                        <annoFineValidita><xsl:value-of select="datoRecord[colonnaDato='aa_max_tipo_strut_unita_doc']/valoreDato"/></annoFineValidita>
                                        <sistemiVersanti>
                                            <xsl:for-each select="recordChild[tipoRecord='Sistemi verasanti tipo struttura ud']/child">
                                                <sistema><xsl:value-of select="keyRecord/datoKey[colonnaKey='nm_sistema_versante']/valoreKey"/></sistema>
                                            </xsl:for-each>
                                        </sistemiVersanti>
                                    </riga>
                                </xsl:for-each>
                            </xsl:element> <!-- Fine tabella tipi struttura -->

                            <xsl:for-each select="recordChild[tipoRecord='Tipi struttura unità documentaria']/child">
                                <xsl:element name="capitolo"> <!-- Capitolo singolo tipo struttura -->
                                    <xsl:attribute name="nome">Tipo struttura <xsl:value-of select="keyRecord/datoKey[colonnaKey='nm_tipo_strut_unita_doc']/valoreKey" /></xsl:attribute>
                                    <xsl:attribute name="tipo">strutturaUD</xsl:attribute>
                                    <xsl:attribute name="livello">3</xsl:attribute>
                                    <xsl:attribute name="numerato">no</xsl:attribute>
                                    <xsl:attribute name="capitoloInterno">si</xsl:attribute>
                                    <xsl:element name="tabella">
                                        <xsl:attribute name="tipo">strutturaUD</xsl:attribute>
                                        <xsl:attribute name="numerato">no</xsl:attribute>
                                        <xsl:attribute name="nome">Tipo struttura unità documentaria <xsl:value-of select="keyRecord/datoKey[colonnaKey='nm_tipo_strut_unita_doc']/valoreKey" /></xsl:attribute>

                                        <xsl:for-each select="recordChild[tipoRecord='Tipi documenti ammessi']/child">
                                            <xsl:variable name="nomeTipoDoc" select="keyRecord/datoKey[colonnaKey='nm_tipo_doc']/valoreKey" />
                                            <riga>
                                                <elemento>
                                                    <xsl:value-of select="keyRecord/datoKey[colonnaKey='ti_doc']/valoreKey"/>
                                                </elemento>
                                                <tipoDocumento>
                                                    <xsl:value-of select="$nomeTipoDoc"/>
                                                </tipoDocumento>
                                                <descrizione>
                                                    <xsl:value-of select="datoRecord[colonnaDato='ds_tipo_doc']/valoreDato"/>
                                                </descrizione>
                                                <obbligatorio>
                                                    <xsl:choose>
                                                        <xsl:when test="datoRecord[colonnaDato='fl_obbl']/valoreDato='true'">
                                                            Si
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            No
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </obbligatorio>
                                                <note><xsl:value-of select="datoRecord[colonnaDato='dl_note_tipo_doc']/valoreDato"/></note>
                                            </riga>
                                        </xsl:for-each>
                                    </xsl:element>
                                    <xsl:element name="tabella">
                                        <xsl:attribute name="tipo">registriStrutturaUD</xsl:attribute>
                                        <xsl:attribute name="numerato">no</xsl:attribute>
                                        <xsl:attribute name="nome">Registri struttura ud</xsl:attribute>
                                        <xsl:for-each select="recordChild[tipoRecord='Registri tipo struttura ud']/child">
                                            <riga>
                                                <tipoRegistro>
                                                    <xsl:value-of select="keyRecord/datoKey[colonnaKey='cd_registro_unita_doc']/valoreKey"/>
                                                </tipoRegistro>
                                                <descRegistro>
                                                    <xsl:value-of select="datoRecord[colonnaDato='ds_registro_unita_doc']/valoreDato" />
                                                </descRegistro>
                                                <periodiValidita>
                                                    <xsl:for-each select="recordChild[tipoRecord='Periodi di validità']/child">
                                                        <periodo>
                                                            <dal><xsl:value-of select="keyRecord/datoKey[colonnaKey='aa_min_registro_unita_doc']/valoreKey" /></dal>
                                                            <al><xsl:value-of select="datoRecord[colonnaDato='aa_max_registro_unita_doc']/valoreDato" /></al>
                                                            <descFormatoNumero><xsl:value-of select="datoRecord[colonnaDato='ds_formato_numero']/valoreDato" /></descFormatoNumero>
                                                        </periodo>
                                                    </xsl:for-each>
                                                </periodiValidita>
                                                <fiscale>
                                                    <xsl:choose>
                                                        <xsl:when test="datoRecord[colonnaDato='fl_registro_fisc']/valoreDato='true'">
                                                            Si
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            No
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </fiscale>
                                                <dataAttivazione>
                                                    <xsl:value-of select="datoRecord[colonnaDato='dt_istituz']/valoreDato"/>
                                                </dataAttivazione>
                                                <dataDisattivazione>
                                                    <xsl:value-of select="datoRecord[colonnaDato='dt_soppres']/valoreDato"/>
                                                </dataDisattivazione>
                                            </riga>
                                        </xsl:for-each>
                                    </xsl:element>
                                    <anno><xsl:value-of select="datoRecord[colonnaDato='ds_anno_tipo_strut_unita_doc']/valoreDato"/></anno>
                                    <numero><xsl:value-of select="datoRecord[colonnaDato='ds_numero_tipo_strut_unita_doc']/valoreDato"/></numero>
                                    <data><xsl:value-of select="datoRecord[colonnaDato='ds_data_tipo_strut_unita_doc']/valoreDato"/></data>
                                    <oggetto><xsl:value-of select="datoRecord[colonnaDato='ds_ogg_tipo_strut_unita_doc']/valoreDato"/></oggetto>
                                    <riferimentoTemporale><xsl:value-of select="datoRecord[colonnaDato='ds_rif_temp_tipo_strut_ud']/valoreDato"/></riferimentoTemporale>
                                    <descrizione><xsl:value-of select="datoRecord[colonnaDato='ds_tipo_strut_unita_doc']/valoreDato"/></descrizione>
                                </xsl:element> <!-- Fine capitolo singolo tipo struttura -->
                            </xsl:for-each>
                        </xsl:element> <!-- Fine capitolo tipi struttura -->
                    </xsl:if>
<!--
                    <xsl:if test="recordChild[tipoRecord='Registri ammessi']/child">
                        
                        <xsl:element name="capitolo">
                            <xsl:attribute name="nome">Registri</xsl:attribute>
                            <xsl:attribute name="tipo">registriAmmessi</xsl:attribute>
                            <xsl:attribute name="livello">3</xsl:attribute>
                            <xsl:attribute name="capitoloInterno">si</xsl:attribute>
                            <xsl:attribute name="numerato">no</xsl:attribute>
                            <xsl:element name="tabella">
                                <xsl:attribute name="tipo">registriAmmessi</xsl:attribute>
                                <xsl:attribute name="numerato">no</xsl:attribute>
                                <xsl:attribute name="nome">Elenco registri <xsl:value-of select="$nomeTipoUD" /></xsl:attribute>
                                <xsl:for-each select="recordChild[tipoRecord='Registri ammessi']/child">
                                    <xsl:variable name="nomeRegistro" select="keyRecord/datoKey[colonnaKey='cd_registro_unita_doc']/valoreKey" />
                                    <riga>
                                        <tipoRegistro>
                                            <xsl:value-of select="$nomeRegistro" />
                                        </tipoRegistro>
                                        <descRegistro>
                                            <xsl:value-of select="datoRecord[colonnaDato='ds_registro_unita_doc']/valoreDato" />
                                        </descRegistro>
                                        <periodiValidita>
                                            <xsl:for-each select="recordChild[tipoRecord='Periodi di validità']/child">
                                                <periodo>
                                                    <dal><xsl:value-of select="keyRecord/datoKey[colonnaKey='aa_min_registro_unita_doc']/valoreKey" /></dal>
                                                    <al><xsl:value-of select="datoRecord[colonnaDato='aa_max_registro_unita_doc']/valoreDato" /></al>
                                                    <descFormatoNumero><xsl:value-of select="datoRecord[colonnaDato='ds_formato_numero']/valoreDato" /></descFormatoNumero>
                                                </periodo>
                                            </xsl:for-each>
                                        </periodiValidita>
                                        <fiscale>
                                            <xsl:choose>
                                                <xsl:when test="datoRecord[colonnaDato='fl_registro_fisc']/valoreDato='true'">
                                                    Si
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    No
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </fiscale>
                                        <dataAttivazione>
                                            <xsl:value-of select="datoRecord[colonnaDato='dt_istituz']/valoreDato"/>
                                        </dataAttivazione>
                                        <dataDisattivazione>
                                            <xsl:value-of select="datoRecord[colonnaDato='dt_soppres']/valoreDato"/>
                                        </dataDisattivazione>
                                    </riga>
                                </xsl:for-each>
                            </xsl:element>
                        </xsl:element>
                    </xsl:if>
-->                    
                    <!-- Gestisce la tabella dei metadati della UD -->
                    <xsl:if test="recordChild[tipoRecord='XSD dati specifici tipo unità documentaria']/child">
                        <xsl:element name="capitolo">
                            <xsl:attribute name="nome">Metadati specifici associati al tipo unità documentaria</xsl:attribute>
                            <xsl:attribute name="tipo">metadatiSpecificiUD</xsl:attribute>
                            <xsl:attribute name="livello">3</xsl:attribute>
                            <xsl:attribute name="capitoloInterno">si</xsl:attribute>
                            <xsl:attribute name="numerato">no</xsl:attribute>
                            <xsl:for-each select="recordChild[tipoRecord='XSD dati specifici tipo unità documentaria']/child">
                                <xsl:if test="recordChild[tipoRecord='Dati specifici tipo unità documentaria']/child">
                                    <xsl:element name="tabella">
                                        <xsl:attribute name="tipo">metadatiSpecificiUD</xsl:attribute>
                                        <xsl:attribute name="numerato">no</xsl:attribute>
                                        <xsl:attribute name="nome">Metadati specifici associati al tipo unità documentaria <xsl:value-of select="$nomeTipoUD" /></xsl:attribute>
                                        <versione>
                                            <xsl:value-of select="keyRecord/datoKey[colonnaKey='cd_versione_xsd']/valoreKey"/>
                                        </versione>
                                        <descrizione>
                                            <xsl:value-of select="datoRecord[colonnaDato='ds_versione_xsd']/valoreDato"/>
                                        </descrizione>
                                        <dataInizioValidita>
                                            <xsl:value-of select="datoRecord[colonnaDato='dt_istituz']/valoreDato"/>
                                        </dataInizioValidita>
                                        <dataFineValidita>
                                            <xsl:value-of select="datoRecord[colonnaDato='dt_soppres']/valoreDato"/>
                                        </dataFineValidita>
                                        <xsl:for-each select="recordChild[tipoRecord='Dati specifici tipo unità documentaria']/child">
                                            <riga>
                                                <denominazione>
                                                    <xsl:value-of select="keyRecord/datoKey[colonnaKey='nm_attrib_dati_spec']/valoreKey"/>
                                                </denominazione>
                                                <descrizione>
                                                    <xsl:value-of select="datoRecord[colonnaDato='ds_attrib_dati_spec']/valoreDato"/>
                                                </descrizione>
                                            </riga>
                                        </xsl:for-each>
                                    </xsl:element>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:element>
                    </xsl:if>

                    <!-- Gestisce i tipi documento della UD -->
                    <xsl:variable name="docAmmessiUD" select="recordChild[tipoRecord='Tipi struttura unità documentaria']/child/recordChild[tipoRecord='Tipi documenti ammessi']/child"/>
                    <xsl:if test="$docAmmessiUD">
                        <xsl:for-each select="$docAmmessiUD">
                            <xsl:variable name="nomeDoc" select="keyRecord/datoKey[colonnaKey='nm_tipo_doc']/valoreKey" />
                            <xsl:element name="capitolo">
                                <xsl:attribute name="nome">Tipo documento: <xsl:value-of select="$nomeDoc" /></xsl:attribute>
                                <xsl:attribute name="tipo">tipoDocumentoUD</xsl:attribute>
                                <xsl:attribute name="livello">3</xsl:attribute>
                                <xsl:attribute name="numerato">no</xsl:attribute>
                                <!-- Gestisce la tabella dei metadati dei tipi Documento -->
                                <xsl:variable name="childDatiSpecTipoDoc" select="recordChild[tipoRecord='XSD dati specifici tipo documento']/child" />
                                <xsl:if test="$childDatiSpecTipoDoc">
                                    <xsl:element name="capitolo">
                                        <xsl:attribute name="nome">Metadati specifici tipo documento <xsl:value-of select="$nomeDoc" /></xsl:attribute>
                                        <xsl:attribute name="tipo">metadatiSpecificiTipoDoc</xsl:attribute>
                                        <xsl:attribute name="livello">4</xsl:attribute>
                                        <xsl:attribute name="capitoloInterno">si</xsl:attribute>
                                        <xsl:attribute name="numerato">no</xsl:attribute>
                                        <xsl:for-each select="$childDatiSpecTipoDoc">
                                            <xsl:if test="recordChild[tipoRecord='Dati specifici tipo documento']/child">
                                                <xsl:element name="tabella">
                                                    <xsl:attribute name="tipo">metadatiSpecificiTipoDoc</xsl:attribute>
                                                    <xsl:attribute name="numerato">no</xsl:attribute>
                                                    <xsl:attribute name="nome">Metadati specifici tipo documento <xsl:value-of select="$nomeDoc" /></xsl:attribute>
                                                    <versione>
                                                        <xsl:value-of select="keyRecord/datoKey[colonnaKey='cd_versione_xsd']/valoreKey"/>
                                                    </versione>
                                                    <descrizione>
                                                        <xsl:value-of select="datoRecord[colonnaDato='ds_versione_xsd']/valoreDato"/>
                                                    </descrizione>
                                                    <dataInizioValidita>
                                                        <xsl:value-of select="datoRecord[colonnaDato='dt_istituz']/valoreDato"/>
                                                    </dataInizioValidita>
                                                    <dataFineValidita>
                                                        <xsl:value-of select="datoRecord[colonnaDato='dt_soppres']/valoreDato"/>
                                                    </dataFineValidita>
                                                    <xsl:for-each select="recordChild[tipoRecord='Dati specifici tipo documento']/child">
                                                        <riga>
                                                            <denominazione>
                                                                <xsl:value-of select="keyRecord/datoKey[colonnaKey='nm_attrib_dati_spec']/valoreKey"/>
                                                            </denominazione>
                                                            <descrizione>
                                                                <xsl:value-of select="datoRecord[colonnaDato='ds_attrib_dati_spec']/valoreDato"/>
                                                            </descrizione>
                                                        </riga>
                                                    </xsl:for-each>
                                                </xsl:element>
                                            </xsl:if>
                                        </xsl:for-each>
                                    </xsl:element>
                                </xsl:if>
                            </xsl:element>
                        </xsl:for-each>
                    </xsl:if>
                </xsl:element>
            </xsl:for-each>
        </capitolo> 
    </xsl:template>

</xsl:stylesheet>
