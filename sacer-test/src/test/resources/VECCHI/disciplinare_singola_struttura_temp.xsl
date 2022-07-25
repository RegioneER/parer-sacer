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
    <xsl:variable name="nomeStruttura" select="$struttura/child/keyRecord/datoKey[colonnaKey='nm_strut']/valoreKey"/>
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
            <xsl:call-template name="gestioneSipRifiutati" />
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
                <xsl:value-of select="$struttura/child/keyRecord/datoKey[colonnaKey='nm_strut']/valoreKey" />
            </nomeStruttura>
            <descrizioneStruttura>
                <xsl:value-of select="$struttura/child/datoRecord[colonnaDato='ds_strut']/valoreDato" />
            </descrizioneStruttura>
        </capitolo>
    </xsl:template>

    <xsl:template name="introduzione">
        <capitolo nome="INTRODUZIONE" tipo="introduzione" livello="1" numerato="no">
            <dataDecorrenza>
                <xsl:value-of select="/fotoOggetto/recordChild[tipoRecord='Ente convenzionato']/child/recordChild[tipoRecord='Accordo']/child/datoRecord[colonnaDato='dt_reg_accordo']/valoreDato" />
            </dataDecorrenza>
            <dataFineValidita>
                <xsl:value-of select="/fotoOggetto/recordChild[tipoRecord='Ente convenzionato']/child/recordChild[tipoRecord='Accordo']/child/datoRecord[colonnaDato='dt_scad_accordo']/valoreDato" />
            </dataFineValidita>
            <nomeEnte>
                <xsl:value-of select="$nomeEnteConvenzionato" />
            </nomeEnte>
        </capitolo>
    </xsl:template>

    <xsl:template name="descUtenti">
        <capitolo nome="Utenti del sistema" tipo="utentiSistema" livello="1" numerato="si">
            <capitolo livello="2" numerato="si" nome="Profili di accesso degli utenti nel Sistema" tipo="profiliAccesso">
            </capitolo>
            <capitolo livello="2" numerato="si" nome="Utenti abilitati" tipo="utentiAbilitati">
                <xsl:if test="/fotoOggetto/recordChild[tipoRecord='Utenti abilitati']/child">
                    <xsl:element name="tabella">
                        <xsl:attribute name="tipo">utentiAbilitati</xsl:attribute>
                        <xsl:attribute name="numerato">si</xsl:attribute>
                        <xsl:for-each select="/fotoOggetto/recordChild[tipoRecord='Utenti abilitati']/child">
                            <riga>
                                <nome>
                                    <xsl:value-of select="datoRecord[colonnaDato='nm_nome_user']/valoreDato"/>
                                </nome>
                                <cognome>
                                    <xsl:value-of select="datoRecord[colonnaDato='nm_cognome_user']/valoreDato"/>
                                </cognome>
                                <ruoli>
                                    <xsl:for-each select="recordChild[tipoRecord='Aplicazioni usate']/child[keyRecord/datoKey/colonnaKey='nm_applic' and keyRecord/datoKey/valoreKey='SACER']/recordChild[tipoRecord='Ruoli']/child">
                                        <ruolo>
                                            <xsl:value-of select="keyRecord/datoKey/valoreKey"/>
                                        </ruolo>
                                    </xsl:for-each>
                                </ruoli>
                                <recapiti>
                                    <xsl:value-of select="recordMaster/datoRecord[colonnaDato='ds_email']/valoreDato"/>
                                </recapiti>
                                <abilitazioni>
                                    <!-- Processo i registri -->
                                    <xsl:call-template name="cercaTipiDato">
                                        <xsl:with-param name="tipoDato" select="'REGISTRO'" />
                                    </xsl:call-template>
                                    <!-- Processo i tipi UD -->
                                    <xsl:call-template name="cercaTipiDato">
                                        <xsl:with-param name="tipoDato" select="'TIPO_UNITA_DOC'" />
                                    </xsl:call-template>
                                    <!-- Processo i tipi DOC -->
                                    <xsl:call-template name="cercaTipiDato">
                                        <xsl:with-param name="tipoDato" select="'TIPO_DOC'" />
                                    </xsl:call-template>
                                </abilitazioni>
                            </riga>
                        </xsl:for-each>
                    </xsl:element>                  
                </xsl:if>

            </capitolo>
        </capitolo> 
    </xsl:template>

    <xsl:template name="cercaTipiDato">
        <xsl:param name="tipoDato" />
        <xsl:variable name="abilitazioni" select="recordChild[tipoRecord='Aplicazioni usate']/child[keyRecord/datoKey/colonnaKey='nm_applic' and keyRecord/datoKey/valoreKey='SACER']/recordChild[tipoRecord='Abilitazioni ai tipi dato']/child"/>
        <!-- CERCO ALL_ORG -->
        <xsl:choose>
            <xsl:when test="$abilitazioni/keyRecord[datoKey/colonnaKey='nm_classe_tipo_dato' and datoKey/valoreKey=$tipoDato and datoKey/colonnaKey='ti_scopo_dich_abil_dati' and datoKey/valoreKey='ALL_ORG']">
                <xsl:element name="{concat($tipoDato,'s')}">tutti</xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <!-- CERCO ALL_ORG_CHILD -->
                <xsl:choose>
                    <xsl:when test="$abilitazioni/keyRecord[datoKey/colonnaKey='nm_classe_tipo_dato' and datoKey/valoreKey=$tipoDato and datoKey/colonnaKey='ti_scopo_dich_abil_dati' and datoKey/valoreKey='ALL_ORG_CHILD'  and datoKey/colonnaKey='dl_composito_organiz' and datoKey/valoreKey=$organizzazioneParziale]">
                        <xsl:element name="{concat($tipoDato,'s')}">tutti</xsl:element>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- CERCO UNA_ORG -->
                        <xsl:choose>
                            <xsl:when test="$abilitazioni/keyRecord[datoKey/colonnaKey='nm_classe_tipo_dato' and datoKey/valoreKey=$tipoDato and datoKey/colonnaKey='ti_scopo_dich_abil_dati' and datoKey/valoreKey='UNA_ORG'  and datoKey/colonnaKey='dl_composito_organiz' and datoKey/valoreKey=$organizzazione]">
                                <xsl:element name="{concat($tipoDato,'s')}">tutti</xsl:element>
                            </xsl:when>
                            <xsl:otherwise>
                                <!-- CERCO UNA_TIPO_DATO -->
                                <xsl:variable name="cercaTipoDato" select="$abilitazioni[keyRecord/datoKey/colonnaKey='nm_classe_tipo_dato' and keyRecord/datoKey/valoreKey=$tipoDato and keyRecord/datoKey/colonnaKey='ti_scopo_dich_abil_dati' and keyRecord/datoKey/valoreKey='UN_TIPO_DATO'  and keyRecord/datoKey/colonnaKey='dl_composito_organiz' and keyRecord/datoKey/valoreKey=$organizzazione]"/>
                                <xsl:choose>
                                    <xsl:when test="$cercaTipoDato">
                                        <xsl:element name="{concat($tipoDato,'s')}">
                                            <xsl:for-each select="$cercaTipoDato">
                                                <xsl:element name="{$tipoDato}">
                                                    <xsl:value-of select="keyRecord/datoKey[colonnaKey='nm_tipo_dato']/valoreKey"/>
                                                </xsl:element>
                                            </xsl:for-each>
                                        </xsl:element>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        ...
                                    </xsl:otherwise>
                                </xsl:choose> 
                            </xsl:otherwise>
                        </xsl:choose> 
                    </xsl:otherwise>
                </xsl:choose> 
            </xsl:otherwise>
        </xsl:choose> 
    </xsl:template>

    <xsl:template name="sistemiVersanti">
        <capitolo livello="1" numerato="si" nome="Sistemi versanti" tipo="sistemiVersanti">
            <tabella numerato="si">
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
            <capitolo livello="2" numerato="si" nome="Controllo sui formati" tipo="controlloFormati">
                <abilitaControlloFormato>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_abilita_contr_fmt_num']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </abilitaControlloFormato>
                <abilitaControlloFormatoNegativo>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_accetta_fmt_num_neg']/valoreDato='true'">si</xsl:when>
                        <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                </abilitaControlloFormatoNegativo>
                <forzaFormato>
                    <xsl:choose>
                        <xsl:when test="$struttura/child/datoRecord[colonnaDato='fl_forza_fmt_num']/valoreDato='true'">si</xsl:when>
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
            <capitolo livello="2" numerato="si" nome="Controlli sulle firme digitali" tipo="controlloFirme">
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
            <capitolo livello="2" numerato="si" nome="Controllo sull'hash" tipo="controlloHash">
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
<!--            
            <capitolo livello="2" numerato="si" nome="Controllo sulla conformità del registro" tipo="controlloConformitaRegistro">
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
            <capitolo livello="2" numerato="si" nome="Controllo sulla obbligatorietà dei dati di profilo dell'unità documentaria" tipo="controlloObblDatiProfiloUD">
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
-->            
        </capitolo> 
    </xsl:template>

    <xsl:template name="gestioneSipRifiutati">
        <capitolo nome="Gestione dei SIP rifiutati" tipo="gestioneSipRifiutati" livello="1" numerato="no">
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
        <capitolo nome="Tipologie di unità documentaria" tipo="tipologieUD" livello="1" numerato="si">
            <xsl:for-each select="/fotoOggetto/recordChild[tipoRecord='Tipo unità documentaria']/child">
                <xsl:variable name="nomeTipoUD" select="keyRecord/datoKey[colonnaKey='nm_tipo_unita_doc']/valoreKey" />
                <xsl:element name="capitolo">
                    <xsl:attribute name="nome">Tipologia di unità documentaria <xsl:value-of select="keyRecord/datoKey[colonnaKey='nm_tipo_unita_doc']/valoreKey" /></xsl:attribute>
                    <xsl:attribute name="tipo">unitaDocumentaria</xsl:attribute>
                    <xsl:attribute name="livello">2</xsl:attribute>
                    <xsl:attribute name="numerato">si</xsl:attribute>
                    <nomeUD>
                        <xsl:value-of select="$nomeTipoUD" />
                    </nomeUD>
                    <descrizioneUD>
                        <xsl:value-of select="datoRecord[colonnaDato='ds_tipo_unita_doc']/valoreDato" />
                    </descrizioneUD>

                    <xsl:element name="capitolo">
                        <xsl:attribute name="nome">Descrizione della tipologia di unità documentaria</xsl:attribute>
                        <xsl:attribute name="tipo">descrizioneUD</xsl:attribute>
                        <xsl:attribute name="livello">3</xsl:attribute>
                        <xsl:attribute name="numerato">si</xsl:attribute>
                        <xsl:attribute name="capitoloInterno">si</xsl:attribute>
                    </xsl:element>
                    
                    <xsl:if test="recordChild[tipoRecord='Tipi struttura unità documentaria']/child">
                        <xsl:element name="capitolo">
                            <xsl:attribute name="nome">Struttura dell'unità documentaria</xsl:attribute>
                            <xsl:attribute name="tipo">strutturaUD</xsl:attribute>
                            <xsl:attribute name="livello">3</xsl:attribute>
                            <xsl:attribute name="numerato">si</xsl:attribute>
                            <xsl:attribute name="capitoloInterno">si</xsl:attribute>
                            <xsl:element name="tabella">
                                <xsl:attribute name="tipo">strutturaUD</xsl:attribute>
                                <xsl:attribute name="numerato">no</xsl:attribute>
                                <xsl:attribute name="nome">Struttura unità documentaria <xsl:value-of select="$nomeTipoUD" /></xsl:attribute>
                                
                                <xsl:for-each select="recordChild[tipoRecord='Tipi struttura unità documentaria']/child/recordChild[tipoRecord='Tipi documenti ammessi']/child">
                                    <xsl:variable name="nomeTipoDoc" select="keyRecord/datoKey[colonnaKey='nm_tipo_doc']/valoreKey" />
                                    <riga>
                                        <elemento>
                                            <xsl:value-of select="keyRecord/datoKey[colonnaKey='ti_doc']/valoreKey"/>
                                        </elemento>
                                        <tipoDocumento>
                                            <xsl:value-of select="$nomeTipoDoc"/>
                                        </tipoDocumento>
                                        <descrizione>
                                            <xsl:value-of select="/fotoOggetto/recordChild[tipoRecord='Tipo documento']/child[keyRecord/datoKey/colonnaKey='nm_tipo_doc' and keyRecord/datoKey/valoreKey=$nomeTipoDoc]/datoRecord[colonnaDato='ds_tipo_doc']/valoreDato"/>
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
                                    </riga>
                                </xsl:for-each>
                            </xsl:element>
                        </xsl:element>
                        
                    </xsl:if>

                    <xsl:if test="recordChild[tipoRecord='Registri ammessi']/child">
                        
                        <xsl:element name="capitolo">
                            <xsl:attribute name="nome">Registri associati all’unità documentaria</xsl:attribute>
                            <xsl:attribute name="tipo">registriAmmessi</xsl:attribute>
                            <xsl:attribute name="livello">3</xsl:attribute>
                            <xsl:attribute name="capitoloInterno">si</xsl:attribute>
                            <xsl:attribute name="numerato">si</xsl:attribute>
                            <xsl:element name="tabella">
                                <xsl:attribute name="tipo">registriAmmessi</xsl:attribute>
                                <xsl:attribute name="numerato">no</xsl:attribute>
                                <xsl:attribute name="nome">Elenco registri <xsl:value-of select="$nomeTipoUD" /></xsl:attribute>
                                <xsl:for-each select="recordChild[tipoRecord='Registri ammessi']/child">
                                    <xsl:variable name="nomeRegistro" select="keyRecord/datoKey[colonnaKey='cd_registro_unita_doc']/valoreKey" />
                                    <xsl:variable name="childRegistro" select="/fotoOggetto/recordChild[tipoRecord='Registro']/child[keyRecord/datoKey/colonnaKey='cd_registro_unita_doc' and keyRecord/datoKey/valoreKey=$nomeRegistro]" />
                                    <riga>
                                        <tipoRegistro>
                                            <xsl:value-of select="$nomeRegistro" />
                                        </tipoRegistro>
                                        <descRegistro>
                                            <xsl:value-of select="$childRegistro/datoRecord[colonnaDato='ds_registro_unita_doc']/valoreDato" />
                                        </descRegistro>
                                        <periodiValidita>
                                            <xsl:for-each select="$childRegistro/recordChild[tipoRecord='Periodo validità registro']/child">
                                                <periodo>
                                                    <dal>
                                                        <xsl:value-of select="keyRecord/datoKey[colonnaKey='aa_min_registro_unita_doc']/valoreKey" />
                                                    </dal>
                                                    <al>
                                                        <xsl:if test="not(keyRecord/datoKey[colonnaKey='aa_max_registro_unita_doc']/valoreKey='null')">
                                                            <xsl:value-of select="keyRecord/datoKey[colonnaKey='aa_max_registro_unita_doc']/valoreKey" />    
                                                        </xsl:if> 
                                                    </al>
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
                                            <xsl:value-of select="$childRegistro/datoRecord[colonnaDato='dt_istituz']/valoreDato"/>
                                        </dataAttivazione>
                                        <dataDisattivazione>
                                            <xsl:value-of select="$childRegistro/datoRecord[colonnaDato='dt_soppres']/valoreDato"/>
                                        </dataDisattivazione>
                                    </riga>
                                </xsl:for-each>
                            </xsl:element>
                        </xsl:element>
                    </xsl:if>

                    <xsl:element name="capitolo">
                        <xsl:attribute name="nome">Metadati di identificazione e profilo</xsl:attribute>
                        <xsl:attribute name="tipo">metadatiProfilo</xsl:attribute>
                        <xsl:attribute name="livello">3</xsl:attribute>
                        <xsl:attribute name="capitoloInterno">si</xsl:attribute>
                        <xsl:attribute name="numerato">si</xsl:attribute>
                        <xsl:element name="tabella">
                            <xsl:attribute name="tipo">metadatiProfilo</xsl:attribute>
                            <xsl:attribute name="numerato">no</xsl:attribute>
                            <xsl:attribute name="nome">Metadati di identificazione e profilo <xsl:value-of select="$nomeTipoUD" /></xsl:attribute>
                                <riga>
                                    <denominazione>
                                        Numero
                                    </denominazione>
                                    <descrizione>
                                        Ad. Es. Coincide con il numero di registrazione nello specifico repertorio
                                    </descrizione>
                                </riga>
                                <riga>
                                    <denominazione>
                                        Anno
                                    </denominazione>
                                    <descrizione>
                                        Ad es. Coincide con l’anno della data di deliberazione
                                    </descrizione>
                                </riga>
                                <riga>
                                    <denominazione>
                                        Tipo registro
                                    </denominazione>
                                    <descrizione>
                                        Ad es. coincide con il codice del repertorio in cui sono registrate le delibere di consiglio e di giunta
                                    </descrizione>
                                </riga>
                                <riga>
                                    <denominazione>
                                        Oggetto
                                    </denominazione>
                                    <descrizione>
                                        Ad es. coincide  con l’oggetto della deliberazione
                                    </descrizione>
                                </riga>
                                <riga>
                                    <denominazione>
                                        Data
                                    </denominazione>
                                    <descrizione>
                                        Ad es. coincide con la data di seduta dell’Organo collegiale deliberante
                                    </descrizione>
                                </riga>
                        </xsl:element>
                    </xsl:element>

                    <!-- Gestisce la tabella dei metadati della UD -->
                    <xsl:if test="recordChild[tipoRecord='XSD dati specifici tipo unità documentaria']/child">
                        <xsl:element name="capitolo">
                            <xsl:attribute name="nome">Metadati specifici dell'unità documentaria <xsl:value-of select="$nomeTipoUD" /></xsl:attribute>
                            <xsl:attribute name="tipo">metadatiSpecificiUD</xsl:attribute>
                            <xsl:attribute name="livello">3</xsl:attribute>
                            <xsl:attribute name="capitoloInterno">si</xsl:attribute>
                            <xsl:attribute name="numerato">si</xsl:attribute>
                            <xsl:for-each select="recordChild[tipoRecord='XSD dati specifici tipo unità documentaria']/child">
                                <xsl:if test="recordChild[tipoRecord='Dati specifici tipo unità documentaria']/child">
                                    <xsl:element name="tabella">
                                        <xsl:attribute name="tipo">metadatiSpecificiUD</xsl:attribute>
                                        <xsl:attribute name="numerato">no</xsl:attribute>
                                        <xsl:attribute name="nome">Metadati specifici del tipo unità documentaria <xsl:value-of select="$nomeTipoUD" /></xsl:attribute>
                                        <versione><xsl:value-of select="keyRecord/datoKey[colonnaKey='cd_versione_xsd']/valoreKey"/></versione>
                                        <descrizione><xsl:value-of select="datoRecord[colonnaDato='ds_versione_xsd']/valoreDato"/></descrizione>
                                        <dataInizioValidita><xsl:value-of select="datoRecord[colonnaDato='dt_istituz']/valoreDato"/></dataInizioValidita>
                                        <dataFineValidita><xsl:value-of select="datoRecord[colonnaDato='dt_soppres']/valoreDato"/></dataFineValidita>
                                        <xsl:for-each select="recordChild[tipoRecord='Dati specifici tipo unità documentaria']/child">
                                            <riga>
                                                <denominazione><xsl:value-of select="keyRecord/datoKey[colonnaKey='nm_attrib_dati_spec']/valoreKey"/></denominazione>
                                                <descrizione><xsl:value-of select="keyRecord/datoKey[colonnaKey='ds_attrib_dati_spec']/valoreKey"/></descrizione>
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
                                <xsl:attribute name="numerato">si</xsl:attribute>
                                <!-- tabella dei metadati di profilo Tipo DOC -->
                                <xsl:element name="capitolo">
                                    <xsl:attribute name="nome">Metadati di profilo del tipo documento</xsl:attribute>
                                    <xsl:attribute name="tipo">metadatiProfiloTipoDoc</xsl:attribute>
                                    <xsl:attribute name="livello">4</xsl:attribute>
                                    <xsl:attribute name="capitoloInterno">si</xsl:attribute>
                                    <xsl:attribute name="numerato">si</xsl:attribute>
                                    <xsl:element name="tabella">
                                        <xsl:attribute name="tipo">metadatiProfiloTipoDoc</xsl:attribute>
                                        <xsl:attribute name="numerato">no</xsl:attribute>
                                            <riga>
                                                <denominazione>
                                                    Autore
                                                </denominazione>
                                                <descrizione>
                                                    Coincide con Nome e cognome del firmatario
                                                </descrizione>
                                            </riga>
                                            <riga>
                                                <denominazione>
                                                    Descrizione
                                                </denominazione>
                                                <descrizione>
                                                    contiene Eventuali informazioni aggiuntive relative al documento
                                                </descrizione>
                                            </riga>
                                    </xsl:element>
                                </xsl:element>

                                <!-- Gestisce la tabella dei metadati dei tipi Documento -->
                                <xsl:variable name="childDatiSpecTipoDoc" select="/fotoOggetto/recordChild[tipoRecord='Tipo documento']/child[keyRecord/datoKey/colonnaKey='nm_tipo_doc' and keyRecord/datoKey/valoreKey=$nomeDoc]/recordChild[tipoRecord='XSD dati specifici tipo documento']/child" />
                                <xsl:if test="$childDatiSpecTipoDoc">
                                    <xsl:element name="capitolo">
                                        <xsl:attribute name="nome">Metadati specifici tipo documento <xsl:value-of select="$nomeDoc" /></xsl:attribute>
                                        <xsl:attribute name="tipo">metadatiSpecificiTipoDoc</xsl:attribute>
                                        <xsl:attribute name="livello">4</xsl:attribute>
                                        <xsl:attribute name="capitoloInterno">si</xsl:attribute>
                                        <xsl:attribute name="numerato">si</xsl:attribute>
                                        <xsl:for-each select="$childDatiSpecTipoDoc">
                                            <xsl:if test="recordChild[tipoRecord='Dati specifici tipo documento']/child">
                                                <xsl:element name="tabella">
                                                    <xsl:attribute name="tipo">metadatiSpecificiTipoDoc</xsl:attribute>
                                                    <xsl:attribute name="numerato">no</xsl:attribute>
                                                    <xsl:attribute name="nome">Metadati specifici tipo documento <xsl:value-of select="$nomeDoc" /></xsl:attribute>
                                                    <versione><xsl:value-of select="keyRecord/datoKey[colonnaKey='cd_versione_xsd']/valoreKey"/></versione>
                                                    <descrizione><xsl:value-of select="datoRecord[colonnaDato='ds_versione_xsd']/valoreDato"/></descrizione>
                                                    <dataInizioValidita><xsl:value-of select="datoRecord[colonnaDato='dt_istituz']/valoreDato"/></dataInizioValidita>
                                                    <dataFineValidita><xsl:value-of select="datoRecord[colonnaDato='dt_soppres']/valoreDato"/></dataFineValidita>
                                                    <xsl:for-each select="recordChild[tipoRecord='Dati specifici tipo documento']/child">
                                                        <riga>
                                                            <denominazione><xsl:value-of select="keyRecord/datoKey[colonnaKey='nm_attrib_dati_spec']/valoreKey"/></denominazione>
                                                            <descrizione><xsl:value-of select="keyRecord/datoKey[colonnaKey='ds_attrib_dati_spec']/valoreKey"/></descrizione>
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
