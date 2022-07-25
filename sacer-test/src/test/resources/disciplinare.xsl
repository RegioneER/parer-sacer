<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" 
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://www.w3.org/1999/XSL/Format fop.xsd">
    
    <xsl:variable name="lowercase" select="'abcdefghijklmnopqrstuvwxyz'" />
    <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />

    <xsl:output method="xml" indent="yes"/>
    
    <!-- Attributi del blocco elenco puntato -->
    <xsl:attribute-set name="bloccoElencoPuntato">
        <xsl:attribute name="provisional-distance-between-starts">0.5cm</xsl:attribute>
        <xsl:attribute name="start-indent">0.5cm</xsl:attribute>
    </xsl:attribute-set>

    <!-- ESEMPIO NON UTILIZZATO PER INDENTARE UN PARAGRAFO -->
    <xsl:attribute-set name="provaIndentazione">
        <xsl:attribute name="text-indent">0.5cm</xsl:attribute>
        <xsl:attribute name="start-indent">0.5cm</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="table.cell.attr">
        <xsl:attribute name="padding-left">4pt</xsl:attribute>
        <xsl:attribute name="padding-right">4pt</xsl:attribute>
        <xsl:attribute name="padding-top">4pt</xsl:attribute>
        <xsl:attribute name="padding-bottom">4pt</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-width">0.1mm</xsl:attribute>
        <xsl:attribute name="hyphenate">true</xsl:attribute>
        <xsl:attribute name="text-align">start</xsl:attribute> 
    </xsl:attribute-set>    
            
    <xsl:template match="/disciplinare">
        <!-- Font simile a Verdana ma free, da installare su linux server -->
        <!--        <fo:root font-family="Bitstream Vera Sans"> -->
        <fo:root font-family="DejaVu Sans">
            <fo:layout-master-set>    
                <fo:simple-page-master master-name="PaginaA4"
                                       page-height="29.7cm" page-width="21cm" margin-top="1.5cm"
                                       margin-bottom="1.5cm" margin-left="1.25cm" margin-right="1.25cm" >
                    <fo:region-body margin-top="1cm" margin-bottom="1cm" />
                    <fo:region-before extent="1cm"/>
                    <fo:region-after extent="1cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>  

            <fo:page-sequence master-reference="PaginaA4" initial-page-number="1">

                <fo:title>DISCIPLINARE TECNICO</fo:title>
                
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block text-align="end" font-size="8pt" padding-top="1mm">
                        <fo:page-number/>
                    </fo:block>  
                </fo:static-content>       

                <fo:flow flow-name="xsl-region-body" >
                    <fo:wrapper font-size="10pt" text-align="justify">
                        <xsl:apply-templates select="capitolo[@tipo='testata']" />
                        <xsl:apply-templates select="capitolo[@tipo='introduzione']" />
                        <xsl:apply-templates select="capitolo[@tipo='utentiReferenti']" />
                        <xsl:apply-templates select="capitolo[@tipo='utentiSistema']" />
                        <xsl:apply-templates select="capitolo[@tipo='utentiAbilitati']" />
                        <xsl:apply-templates select="capitolo[@tipo='tipologieUD']" /> 
                        <xsl:apply-templates select="capitolo[@tipo='verificheControlli']" />
                        <xsl:apply-templates select="capitolo[@tipo='oggettiDaTrasformare']" />
                        <xsl:apply-templates select="capitolo[@tipo='gestioneSipRifiutati']" />
                        <xsl:apply-templates select="capitolo[@tipo='generazioneSerie']" />
                        <xsl:apply-templates select="capitolo[@tipo='modalitaAnnullamentoVersamenti']" />
                        <xsl:apply-templates select="capitolo[@tipo='modalitaRestituzioneOggettiVersati']" />
                    </fo:wrapper>
                </fo:flow>
                
            </fo:page-sequence>
                  
        </fo:root>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='testata']">
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <fo:block text-align="center" font-size="28pt" font-weight="bold">
                DISCIPLINARE TECNICO
            </fo:block>  
            <xsl:call-template name="aCapo" />
            <fo:block text-align="center" font-size="25pt" font-weight="normal" >
                PER LO SVOLGIMENTO DELLA
            </fo:block>  
            <fo:block text-align="center" font-size="25pt" font-weight="normal" >
                FUNZIONE DI CONSERVAZIONE DEI
            </fo:block>  
            <fo:block text-align="center" font-size="25pt" font-weight="normal" >
                DOCUMENTI INFORMATICI
            </fo:block>
            <xsl:call-template name="aCapo" />
            <fo:block text-align="center" font-size="11pt" font-weight="normal" font-style="italic" >
                Versione del <xsl:value-of select="substring(versione,0,11)"/> (ora:<xsl:value-of select="substring(versione,12,8)"/>)
            </fo:block> 
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <fo:block text-align="center" font-size="18pt" font-weight="normal" >
                Ente convenzionato <fo:inline font-weight="bold">
                    <xsl:value-of select="translate(nomeEnte, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
                </fo:inline>
            </fo:block>
            <xsl:call-template name="aCapo" />
            <fo:block text-align="left" font-size="11pt" font-weight="normal" font-style="italic" >
                Estremi dell’Accordo/Convenzione tra Ente e l’Istituto per i Beni Artistici, Culturali e Naturali della Regione Emilia-Romagna (IBACN) per lo svolgimento della funzione di conservazione dei documenti informatici: 
            </fo:block>  
            <xsl:call-template name="aCapo" />
            <fo:block text-align="left" font-size="11pt" font-weight="normal" font-style="italic" >
                Data di decorrenza: 
                <xsl:call-template name="scriviSeNonNullo"><xsl:with-param name="campo" select="dataDecorrenza"/></xsl:call-template>
            </fo:block>  
            <fo:block text-align="left" font-size="11pt" font-weight="normal" font-style="italic" >
                Data di fine validità: 
                <xsl:call-template name="scriviData"><xsl:with-param name="campo" select="dataFineValidita"/></xsl:call-template>
            </fo:block>  
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <fo:block text-align="center" font-size="14pt" font-weight="normal" >
                Nome dell’Ente versante configurato nel Sistema di conservazione:
            </fo:block>
            <fo:block text-align="center" font-size="14pt" font-weight="normal" >
                <!--                <xsl:value-of select="translate(enteVersante, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')" /> -->
                <xsl:value-of select="enteVersante" />
            </fo:block>
            <fo:block text-align="center" font-size="14pt" font-weight="normal" >
                Descrizione dell’Ente versante:
            </fo:block>
            <fo:block text-align="center" font-size="14pt" font-weight="normal" >
                <!--                <xsl:value-of select="translate(enteVersante, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')" /> -->
                <xsl:value-of select="descEnteVersante" />
            </fo:block>

            <xsl:call-template name="aCapo" />
            <fo:block text-align="center" font-size="14pt" font-weight="normal" >
                Nome della Struttura versante configurata nel Sistema di conservazione:
            </fo:block>
            <fo:block text-align="center" font-size="14pt" font-weight="normal" >
                <xsl:value-of select="nomeStruttura" />
            </fo:block>
            <fo:block text-align="center" font-size="14pt" font-weight="normal" >
                Descrizione della Struttura versante:
            </fo:block>
            <fo:block text-align="center" font-size="14pt" font-weight="normal" >
                <xsl:value-of select="descStruttura" />
            </fo:block>
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <fo:leader leader-pattern="rule" color="green" leader-length="100%" padding-left="0%" rule-style="solid" rule-thickness="3pt" />
            <fo:table margin-left="0.5mm" margin-right="15mm">
                <fo:table-body >
                    <fo:table-row>
                        <fo:table-cell>
                            <fo:block font-weight="normal">
                                Soggetto conservatore
                            </fo:block>
                        </fo:table-cell> 
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell>
                            <fo:block font-weight="bold">
                                Istituto per i Beni Artistici, Culturali e Naturali (IBACN) 
                            </fo:block>
                        </fo:table-cell> 
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell>
                            <fo:block font-weight="bold">
                                Servizio Polo Archivistico Regionale (ParER)
                            </fo:block>
                        </fo:table-cell> 
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
        </fo:block> 
    </xsl:template>

    <xsl:template match="capitolo[@tipo='introduzione']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <fo:block text-align="left" font-size="14pt" font-weight="bold">
                INTRODUZIONE
            </fo:block>  
            <xsl:call-template name="aCapo" />
            <fo:block  text-align="left" font-size="14pt" font-weight="bold">
                Scopo, ambito e struttura del documento
            </fo:block>  
            <xsl:call-template name="aCapo" />
            <fo:block font-weight="normal" text-align="justify">
                Il presente documento costituisce il Disciplinare Tecnico (d’ora in poi Disciplinare), cioè il documento che definisce le specifiche operative e le modalità di descrizione e di versamento nel Sistema di conservazione delle Tipologie di unità documentarie oggetto di conservazione. Il Disciplinare è redatto in esecuzione di quanto indicato nell’Accordo o Convenzione, che regola nei suoi profili generali il rapporto tra l’Ente e IBACN per lo svolgimento della funzione di conservazione dei documenti informatici affidati dall'Ente a IBACN e più specificatamente al suo Servizio Polo archivistico regionale (d'ora in poi ParER) in base al Manuale di conservazione (d’ora in poi Manuale) redatto da ParER.
            </fo:block>  
            <fo:block>
                Per lo svolgimento delle funzioni di conservazione dei documenti informatici, ParER ha sviluppato un proprio Sistema di conservazione (d’ora in poi Sistema), descritto nel Manuale.
            </fo:block>  
            <fo:block>
                Le modalità generali di gestione delle funzioni di conservazione sono descritte nel Manuale a cui si rimanda anche per la terminologia utilizzata nel presente Disciplinare.
            </fo:block>  
            <fo:block>
                Il Disciplinare definisce l'articolazione in Strutture (corrispondenti normalmente alle Aree Organizzative Omogenee, ma non escludendo altre ripartizioni) con cui l'Ente si rapporta con ParER per il versamento dei documenti.
            </fo:block>  
            <fo:block>
                Inoltre dettaglia tutti gli aspetti direttamente desumibili dalle configurazioni della struttura versante (d’ora in poi Struttura) nel Sistema e non espressamente trattati dal Manuale. In particolare descrive le specificità e le modalità di versamento dei documenti informatici proprie dell’Ente.
            </fo:block>  
            <fo:block>
                Gli utenti espressamente autorizzati dall’Ente possono accedere al Sistema tramite credenziali personali rilasciate da ParER e comunicate al singolo utente. L’accesso al Sistema consente di consultare i documenti digitali versati nel Sistema e le configurazioni specifiche adottate.
            </fo:block>  
        </fo:block>  
    </xsl:template>

    <xsl:template match="capitolo[@tipo='utentiSistema']" >
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>In questo capitolo sono definiti i nominativi, i recapiti, il ruolo delle diverse persone dell’Ente abilitate all’accesso al Sistema.</fo:block> 
            <fo:block>A livello di regola generale, a ciascun utente viene assegnato all’interno del Sistema un userID con la sintassi “nome.cognome”.</fo:block>
            <!--            <fo:block>In questa sede, inoltre, sono elencati i Referenti ParER, ovvero le persone incaricate da ParER di seguire le attività di avvio dei servizi di conservazione, inclusi i test di versamento.</fo:block> -->
            <xsl:call-template name="aCapo" />
            <xsl:apply-templates select="capitolo[@tipo='profiliAccesso']" />
        </fo:block>  
    </xsl:template>

    <xsl:template match="capitolo[@tipo='profiliAccesso']" >
        <fo:block id="{generate-id(.)}">
            <!--
                        <xsl:call-template name="stampaCapitolo">
                            <xsl:with-param name="cap" select="."/>
                        </xsl:call-template>
                        <xsl:call-template name="aCapo" />
            -->            
            <fo:block>A ciascun utente del Sistema è possibile assegnare uno o più ruoli di accesso (d’ora in poi Ruoli), ognuno dei quali definisce le modalità di accesso ai documenti, alle informazioni conservate nel Sistema e alle operazioni che può effettuare.</fo:block>
            <fo:block>Tra i Ruoli gestiti dal Sistema e assegnabili agli utenti dell'Ente:</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                <fo:list-item >
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Responsabile: accede in visualizzazione a tutte le funzionalità. Può versare pacchetti standard e non standard, consistenza serie e annullare i versamenti. Inoltre accede in visualizzazione alle informazioni sugli utenti;</fo:block>
                        <xsl:call-template name="aCapo" />
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Supervisore: consente di accedere all'archivio e al monitoraggio dei versamenti senza limitazioni sul tipo di dato;</fo:block>
                        <xsl:call-template name="aCapo" />
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Operatore: consente di accedere al contenuto dell’archivio, anche limitatamente a determinate tipologie di contenuto.</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </fo:list-block>
            <xsl:call-template name="aCapo" />
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='utentiAbilitati']" >
        <fo:block id="{generate-id(.)}">
            <!--
                        <xsl:call-template name="stampaCapitolo">
                            <xsl:with-param name="cap" select="."/>
                        </xsl:call-template>
            -->            
            <xsl:call-template name="aCapo" />
            <fo:block>
                <xsl:text>La Tabella </xsl:text>
                <xsl:value-of select="@nome"/>
                <xsl:text> mostra l’elenco degli utenti dell’Ente abilitati.</xsl:text>
            </fo:block> 
            <fo:block>
                <xsl:text>L’utente che non ha effettuato l’accesso nel Sistema negli ultimi 90 giorni viene disattivato. L’utente disattivo può essere successivamente eliminato oppure riattivato, nel caso in cui non sia scattato il limite temporale per l’eliminazione. L’utente che è disattivo da almeno 180 giorni viene eliminato automaticamente dal Sistema e non sarà più visibile nella Tabella</xsl:text>
                <xsl:text> </xsl:text>
                <xsl:value-of select="@nome"/>.</fo:block>
            <xsl:call-template name="aCapo" />
            <xsl:if test="tabella[@tipo='utentiAbilitati']/riga">
                <fo:table>
                    <fo:table-column column-width="20%"/>
                    <fo:table-column column-width="20%"/>                        
                    <fo:table-column column-width="35%"/>
                    <fo:table-column column-width="25%"/>
                    <fo:table-header font-weight="bold">
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Nome e cognome</fo:block>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Ruolo</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Recapiti</fo:block>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Abilitazioni ai tipi di dato</fo:block>
                        </fo:table-cell>
                    </fo:table-header>
                    <fo:table-body>
                        <xsl:for-each select="tabella[@tipo='utentiAbilitati']/riga">
                            <fo:table-row>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>
                                        <xsl:value-of select="nome"/>
                                        <xsl:text> </xsl:text>
                                        <xsl:value-of select="cognome"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block-container overflow="hidden">
                                        <fo:block>
                                            <xsl:value-of select="ruoli"/>
                                        </fo:block>
                                    </fo:block-container>
                                </fo:table-cell>                        
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block-container overflow="hidden">
                                        <fo:block>
                                            <xsl:value-of select="recapiti"/>
                                        </fo:block>
                                    </fo:block-container>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr" width="40mm">
<!--                                    <xsl:variable name="abilitazioniTipiUd" select="abilitazioniTipiUd" />
                                    <xsl:variable name="abilitazioniTipiDoc" select="abilitazioniTipiDoc" />
                                    <xsl:variable name="abilitazioniRegistri" select="abilitazioniRegistri" /> -->
                                    <xsl:variable name="abilitazioniTipiUd">
                                        <xsl:choose>
                                            <xsl:when test="abilitazioniTipiUd='ALL'">
                                                Abilitato a tutti i tipi ud
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:value-of select="abilitazioniTipiUd"/>                                                
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:variable>
                                    <xsl:variable name="abilitazioniTipiDoc">
                                        <xsl:choose>
                                            <xsl:when test="abilitazioniTipiDoc='ALL'">
                                                Abilitato a tutti i tipi documento
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:value-of select="abilitazioniTipiDoc"/>                                                
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:variable>
                                    <xsl:variable name="abilitazioniRegistri">
                                        <xsl:choose>
                                            <xsl:when test="abilitazioniRegistri='ALL'">
                                                Abilitato a tutti i registri
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:value-of select="abilitazioniRegistri"/>                                                
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:variable>
                                    <fo:block>
                                        <xsl:choose>
                                            <xsl:when test="abilitazioniTipiUd='ALL' and abilitazioniTipiDoc='ALL' and abilitazioniRegistri='ALL'">
                                                <xsl:text>Abilitato a tutti i tipi di dato</xsl:text>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:if test="not($abilitazioniRegistri='null')">
                                                    <fo:block>                                                    
                                                        <xsl:value-of select="$abilitazioniRegistri"/>
                                                        <xsl:if test="not($abilitazioniTipiUd='null')">, </xsl:if>
                                                    </fo:block>
                                                </xsl:if>
                                                <xsl:if test="not($abilitazioniTipiUd='null')">
                                                    <fo:block>                                                    
                                                        <xsl:value-of select="$abilitazioniTipiUd"/>
                                                        <xsl:if test="not($abilitazioniTipiDoc='null')">, </xsl:if>
                                                    </fo:block>
                                                </xsl:if>
                                                <xsl:if test="not($abilitazioniTipiDoc='null')">
                                                    <fo:block>                                                    
                                                        <xsl:value-of select="$abilitazioniTipiDoc"/>
                                                    </fo:block>
                                                </xsl:if>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </xsl:for-each>
                    </fo:table-body>
                </fo:table>            
                <fo:block font-weight="bold">
                    <xsl:text>Tabella</xsl:text>
                    <xsl:text> - </xsl:text> 
                    <xsl:value-of select="@nome"/>
                </fo:block>
            </xsl:if>
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='utentiReferenti']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>
                <xsl:text>In questo capitolo sono definiti i nominativi e i recapiti dei Referenti ParER e dei Referenti dell'Ente che seguono o hanno seguito i test di versamento e l'attività di avvio del servizio di conservazione.</xsl:text>
            </fo:block>
            <xsl:call-template name="aCapo" />
            <xsl:if test="tabella[@tipo='utentiReferenti']/riga">
                <fo:table>
                    <fo:table-column column-width="20%"/>
                    <fo:table-column column-width="35%"/>                        
                    <fo:table-column column-width="20%"/>
                    <fo:table-column column-width="25%"/>
                    <fo:table-header font-weight="bold">
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Nome e cognome</fo:block>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Recapiti</fo:block>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Archivista di riferimento (ParER) / Referente Ente</fo:block>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Note</fo:block>
                        </fo:table-cell>
                    </fo:table-header>
                    <fo:table-body>
                        <xsl:for-each select="tabella[@tipo='utentiReferenti']/riga">
                            <xsl:sort select="tipoReferente"/>
                            <fo:table-row>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>
                                        <xsl:value-of select="nome"/>
                                        <xsl:text> </xsl:text>
                                        <xsl:value-of select="cognome"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block-container overflow="hidden">
                                        <fo:block>
                                            <xsl:value-of select="recapiti"/>
                                        </fo:block>
                                    </fo:block-container>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr" width="40mm">
                                    <fo:block>
                                        <xsl:choose>
                                            <xsl:when test="tipoReferente='ARK_RIF'">
                                                <xsl:text>Archivista di riferimento (ParER)</xsl:text>
                                            </xsl:when>
                                            <xsl:when test="tipoReferente='REF_ENTE'">
                                                <xsl:text>Referente Ente</xsl:text>
                                            </xsl:when>
                                        </xsl:choose>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block-container overflow="hidden">
                                        <fo:block>
                                            <xsl:value-of select="note"/>
                                        </fo:block>
                                    </fo:block-container>
                                </fo:table-cell>
                            </fo:table-row>
                        </xsl:for-each>
                    </fo:table-body>
                </fo:table>            
                <fo:block font-weight="bold">
                    <xsl:text>Tabella</xsl:text>
                    <xsl:text> - </xsl:text> 
                    <xsl:value-of select="@nome"/>
                </fo:block>
            </xsl:if>
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='verificheControlli']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>Nella fase di acquisizione del pacchetto di versamento (SIP) il Sistema effettua una serie di verifiche automatiche finalizzate ad individuare eventuali anomalie.</fo:block>
            <fo:block>In questo capitolo è descritta l’impostazione dell’intera Struttura in relazione ai controlli operati dal Sistema.</fo:block>
            <fo:block>Per ciascun parametro sono fornite le seguenti informazioni:</fo:block>
            <fo:block>- sintetica descrizione;</fo:block>
            <fo:block>- indicazione dello stato di attivazione previsto dalla configurazione “standard” definita da ParER;</fo:block>
            <fo:block>- indicazione dello stato di attivazione effettivo.</fo:block>
            <fo:block>Si precisa che l’eventuale difformità tra lo stato di attivazione definito da ParER e lo stato effettivo dipende da un’esplicita richiesta di modifica del parametro “standard” da parte dell’Ente. Le verifiche automatiche a cui sono sottoposti i SIP nonché i parametri di accettazione sono infatti regolabili a discrezione dell’Ente.</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:block>L’esito del versamento è condizionato dai parametri impostati a livello di Struttura dettagliati nelle tabelle sottostanti e dai parametri definiti dall’Ente a livello di Indice SIP (file XML che contiene i metadati nonché i riferimenti ai file dei Componenti del SIP dell’unità documentaria).</fo:block>
            <fo:block>I parametri definiti nell’Indice SIP sono tre:</fo:block>
            <fo:block>- Forza Accettazione: definisce il comportamento del Sistema in relazione agli esiti delle verifiche di firma e/o formato dei file contenuti nel SIP. Assume valori “False” o “True”:</fo:block>
            <fo:block>
                <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">False: il Sistema accetta il versamento solo se tutti i controlli relativi alla firma e al formato hanno esito positivo</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">True: il Sistema accetta il versamento anche nel caso in cui almeno uno dei controlli relativi alla firma e al formato hanno esito negativo</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                </fo:list-block>
            </fo:block>

            <fo:block>- Forza Conservazione: definisce il comportamento del Sistema in relazione al versamento di SIP contenenti file non firmati. Assume valori “False” o “True”:</fo:block>
            <fo:block>
                <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">False: il Sistema accetta il versamento solo se e` presente almeno un file firmato</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">True: il Sistema accetta il versamento anche nel caso in cui nessuno dei file sia firmato</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                </fo:list-block>
            </fo:block>

            <fo:block>- Forza Collegamento: definisce il comportamento del Sistema in funzione della presenza o meno nel Sistema stesso del SIP oggetto di collegamento. Assume valori “False” o “True”:</fo:block>
            <fo:block>
                <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">False: il Sistema accetta il versamento di SIP i cui eventuali Collegamenti siano rivolti a SIP gia` presenti nel Sistema</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">True: il Sistema accetta il versamento di SIP anche nel caso in cui gli eventuali Collegamenti siano rivolti a SIP non presenti nel Sistema.</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                </fo:list-block>
            </fo:block>
            <xsl:call-template name="aCapo" />
            <fo:block>L’impostazione “standard” riguarda unicamente i parametri configurati a livello di Struttura e  prevede l’attivazione di tutti i controlli sul formato del file e sulla validità della firma e di tutti i relativi parametri di accettazione dei SIP per i quali il Sistema abbia restituito un esito negativo del controllo.</fo:block>
            <fo:block>Si precisa che i controlli sulle eventuali firme digitali è effettuata alla data indicata nell’Indice SIP (che puo` essere quella contenuta nella firma, in una marca temporale o un riferimento temporale dichiarato nell’Indice SIP) o, in assenza di questa, alla data del versamento.</fo:block>
            <fo:block>Secondo l’impostazione “standard”, il SIP con esito negativo del controllo sulla validità della firma viene accettato in conservazione solo se la trasmissione dello stesso viene “forzata” dal versatore in sede di versamento. La forzatura consiste nel valorizzare con “True” il parametro “Forza Accettazione” presente nell’Indice SIP.</fo:block>
            <fo:block>A titolo di esempio si riporta l’esito del versamento di un SIP con certificato di firma scaduto in relazione all’impostazione del parametro “Forza Accettazione” e all’impostazione “standard” dei parametri di Struttura:</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:block>
                <fo:inline font-weight="bold">CASO A </fo:inline>
                <fo:inline font-style="italic">– Parametro “Forza Accettazione” attivo</fo:inline>
                <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">Controllo certificato: SI (configurazione standard)</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">Accetta controllo certificato scaduto: SI (configurazione standard)</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">Forza Accettazione: SI (definito nell’IndiceSIP)</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                </fo:list-block>
            </fo:block>
            <fo:block>In questo caso il SIP viene acquisito in conservazione e l’esito del versamento è di tipo WARNING.</fo:block>
            <fo:block>
                <fo:inline font-weight="bold">CASO B </fo:inline>
                <fo:inline font-style="italic">– Parametro “Forza Accettazione” NON attivo</fo:inline>
                <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">Controllo certificato: SI (configurazione standard)</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">Accetta controllo certificato scaduto: SI (configurazione standard)</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">Forza Accettazione: NO (definito nell’IndiceSIP)</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                </fo:list-block>
            </fo:block>
            <fo:block>In questo caso il SIP viene rifiutato e l’esito del versamento è NEGATIVO.</fo:block>
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <xsl:apply-templates select="capitolo[@tipo='controlloFirme']" />
            <xsl:apply-templates select="capitolo[@tipo='parametriAccettazioneFirme']" />
            <xsl:apply-templates select="capitolo[@tipo='controlloFormati']" />
            <xsl:apply-templates select="capitolo[@tipo='controlloHash']" />
            <xsl:apply-templates select="capitolo[@tipo='controlloConformitaRegistro']" />
            <xsl:apply-templates select="capitolo[@tipo='controlloObblDatiProfiloUD']" />
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='controlloFormati']" >
        <xsl:call-template name="aCapo" />
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:table>
                <fo:table-column column-width="40%"/>
                <fo:table-column column-width="30%"/>                        
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="15%"/>
                <fo:table-header font-weight="bold">
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Parametro</fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Descrizione</fo:block>
                    </fo:table-cell>                        
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Valore standard</fo:block>
                    </fo:table-cell>                        
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Valore effettivo (Richiesto dall'Ente)</fo:block>
                    </fo:table-cell>                        
                </fo:table-header>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Abilita controllo formati</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Il parametro indica se effettuare il controllo formati al versamento. Il controllo viene effettuato anche se il parametro disattivo, ma gli esiti del controllo non influiscono sull’acquisizione nel Sistema</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(abilitaControlloFormato, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Accetta controllo formato negativo</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Il parametro indica se il parametro di versamento Forza accettazione opera anche sul controllo formati, consentendo di forzare il versamento quando il formato versato non corrisponde al formato calcolato</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(abilitaControlloFormatoNegativo, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Forza formato</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Il parametro consente di forzare tutti i versamenti nal caso in cui il formato versato non corrisponde al formato calcolato</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(forzaFormato, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                </fo:table-body>
            </fo:table>  
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <fo:block>
                <xsl:text>Nella Tabella Formati sono elencati i formati configurati nella struttura. In base all’impostazione “standard” tutti i formati classificati come IDONEI e GESTITI ai fini della conservazione a lungo termini sono inclusi nella struttura mentre i formati classificati come DEPRECATI possono essere ammessi solo su richiesta dell'Ente.</xsl:text>
            </fo:block>
            <fo:block>In base all’idoneità in relazione alla conservazione a lungo termine i formati sono classificati come segue:</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">
                            <fo:inline text-decoration="underline">Formati idonei</fo:inline>: sono i formati che per le loro caratteristiche di standardizzazione, di apertura, di sicurezza, di portabilità, di immodificabilità, di staticità e di diffusione reputati idonei alla conservazione a lungo termine, quali ad esempio quelli elencati al punto 5 dell’Allegato 2 alle Regole tecniche;</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">
                            <fo:inline text-decoration="underline">Formati gestiti</fo:inline>: sono i formati non ritenuti idonei per la conservazione a lungo termine ma che possono essere opportunamente migrati in Formati idonei, con le procedure di cui al comma 1, lettera j, dell’art. 9 delle Regole tecniche per la produzione delle Copie informatiche di documento informatico;</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">
                            <fo:inline text-decoration="underline">Formati deprecati</fo:inline>: sono formati ritenuti non idonei per la conservazione a lungo termine e che al contempo non possono essere migrati in Formati idonei, per i quali, quindi, non è possibile assicurare la conservazione a lungo termine.</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </fo:list-block>
            <xsl:call-template name="aCapo" />
            <xsl:if test="tabella[@tipo='formatiAmmessi']/riga">
                <fo:table>
                    <fo:table-column column-width="40%"/>
                    <fo:table-column column-width="40%"/>                        
                    <fo:table-column column-width="20%"/>

                    <fo:table-header font-weight="bold">
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Formato versato ammesso</fo:block>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Mimetype</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Idoneità alla conservazione</fo:block>
                        </fo:table-cell>                        
                    </fo:table-header>
                    <fo:table-body>
                        <!--                    <xsl:for-each select="tabella/riga[@tipo='formatiAmmessi']" > -->
                        <xsl:for-each select="tabella[@tipo='formatiAmmessi']/riga" >
                            <fo:table-row>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block-container  overflow="hidden">
                                        <fo:block>
                                            <xsl:value-of select="nomeFormato"/>
                                        </fo:block>
                                    </fo:block-container>                                
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block-container overflow="hidden">
                                        <fo:block font-weight="normal">
                                            <xsl:value-of select="mimeType"/>
                                        </fo:block>
                                    </fo:block-container>                                
                                </fo:table-cell>                        
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block font-weight="normal">
                                        <xsl:value-of select="idoneitaConservazione"/>
                                    </fo:block>
                                </fo:table-cell>                        
                            </fo:table-row>
                        </xsl:for-each>
                    </fo:table-body>
                </fo:table>  
                <fo:block font-weight="bold">
                    <xsl:text>Tabella Formati</xsl:text>
                </fo:block>
            </xsl:if>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="capitolo[@tipo='controlloFirme']" >
        <xsl:call-template name="aCapo" />
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:table>
                <fo:table-column column-width="40%"/>
                <fo:table-column column-width="30%"/>                        
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="15%"/>

                <fo:table-header font-weight="bold">
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Parametro</fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Descrizione</fo:block>
                    </fo:table-cell>                        
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Valore standard</fo:block>
                    </fo:table-cell>                        
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Valore effettivo (Richiesto dall'Ente)</fo:block>
                    </fo:table-cell>                        
                </fo:table-header>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Controllo crittografico</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Verifica che l’hash del documento firmato corrisponde all’hash sui cui è stata apposta la firma</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(controlloCrittografico, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Controllo catena trusted</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Verifica che il certificatore che ha emesso il certificato di firma sia accreditato da AGID</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(controlloCatenaTrusted, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Controllo certificato</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Verifica che la data di validità della firma, intesa come riferimento temporale, sia compresa nel periodo di validità del certificato di firma</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(controlloCertificato, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Controllo crl</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Verifica l’eventuale revoca del certificato di firma</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(controlloCrl, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                </fo:table-body>
            </fo:table>  
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='parametriAccettazioneFirme']" >
        <xsl:call-template name="aCapo" />
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:table>
                <fo:table-column column-width="40%"/>
                <fo:table-column column-width="30%"/>                        
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="15%"/>

                <fo:table-header font-weight="bold">
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Parametro</fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Descrizione</fo:block>
                    </fo:table-cell>                        
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Valore standard</fo:block>
                    </fo:table-cell>                        
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Valore effettivo (Richiesto dall'Ente)</fo:block>
                    </fo:table-cell>                        
                </fo:table-header>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Firma sconosciuta</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Accetta l’acquisizione di SIP contenenti documenti la cui firma sia in un formato non determinabile</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaFirmaSconosciuta, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Controllo catena trusted negativo</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Accetta l’acquisizione di SIP contenenti documenti la cui firma sia emessa da un certificatore non accreditato</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaCatenaTrustedNegativo, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Controllo certificato no cert</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Verifica che il certificato sia un certificato di firma</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaControlloCertificatoNoCert, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Controllo crl non scaricabile</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Accetta l’acquisizione di SIP contenenti documenti firmati digitalmente il cui certificato di firma non sia verificabile al momento del versamento per l’impossibilità di interrogare le liste di revoca</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaControlloCrlNonScaricabile, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Firma no delibera 45</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Accetta l’acquisizione di SIP contenenti documenti firmati digitalmente utilizzando un certificato il cui formato non sia fra quelli ammessi dalla deliberazione n.45/2009, emanata dal CNIPA</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaFirmaNoDelibera45, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Controllo crittografico negativo</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Accetta l’acquisizione di SIP contenenti documenti in cui non vi è corrispondenza tra firma e contenuto firmato</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaControlloCrittograficoNegativo, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Controllo crl negativo</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Accetta l’acquisizione di SIP contenenti documenti firmati digitalmente utilizzando un certificato di firma revocato o scaduto alla data del riferimento temporale</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaControlloCrlNegativo, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Firma non conforme</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Accetta l’acquisizione di SIP contenenti documenti firmati digitalmente utilizzando una firma la cui struttura non sia conforme con il suo formato</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaFirmaNonConforme, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Controllo certificato scaduto</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Accetta l’acquisizione di SIP contenenti documenti firmati digitalmente utilizzando una firma basata su un certificato scaduto alla data del riferimento temporale</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaControlloCertificatoScaduto, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Controllo crl scaduta</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Accetta l’acquisizione di SIP nel caso in cui la CRL scaricata sia scaduta</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaControlloCrlScaduto, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Marca sconosciuta</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Accetta formati di marca non conformi</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaMarcaSconosciuta, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Controllo certificato non valido</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Accetta l’acquisizione di SIP contenenti documenti firmati digitalmente utilizzando un certificato di firma non ancora valido alla data del riferimento temporale</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaControlloCertificatoNonValido, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Controllo crl non valida</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Accetta l’acquisizione di SIP nel caso in cui la CRL scaricata non sia valida ad esempio per mancanza della data di scadenza</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaControlloCrlNonValida, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                </fo:table-body>
            </fo:table>  
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='controlloHash']" >
        <xsl:call-template name="aCapo" />
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:table>
                <fo:table-column column-width="40%"/>
                <fo:table-column column-width="30%"/>                        
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="15%"/>

                <fo:table-header font-weight="bold">
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Parametro</fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Descrizione</fo:block>
                    </fo:table-cell>                        
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Valore standard</fo:block>
                    </fo:table-cell>                        
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Valore effettivo (Richiesto dall'Ente)</fo:block>
                    </fo:table-cell>                        
                </fo:table-header>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Abilita controllo hash</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Verifica la corrispondenza tra hash del file dichiarato nell’Indice SIP e l’hash calcolato dal Sistema. L’attivazione del controllo implica l’obbligatorietà nell’Indice SIP dell’informazione relativa all’hash per ciascun file presente nel SIP</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">NO</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(abilitaControlloHash, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Accetta controllo hash negativo</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Accetta l’acquisizione di SIP in cui non vi sia corrispondenza tra hash dichiarato e hash calcolato dal Sistema. In caso di esito negativo l’accettazione è legata all’impostazione del parametro successivo</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaControlloHashNegativo, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Forza hash</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Il parametro regola l’accettazione del SIP in caso di esito negativo del controllo sull’hash</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(forzaHash, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                </fo:table-body>
            </fo:table>  
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='controlloConformitaRegistro']" >
        <xsl:call-template name="aCapo" />
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>Il registro viene definito dall’Ente o concordato con ParER e in generale corrisponde al nome del repertorio in cui sono registrati in ordine progressivo i documenti ad esso afferenti (ex art. 53, D.P.R. 28/12/2000 n. 445) oppure al contesto applicativo/documentale nell’ambito del quale viene attribuito all’unità documentaria l’identificativo progressivo e univoco.</fo:block>
            <fo:block>In relazione a ciascun registro associato alle tipologie documentarie oggetto di versamento (vedere Tabella Elenco registri), è definito il formato del metadato “Numero” della chiave del SIP dell’unità documentaria afferente al registro medesimo. Il metadato “Numero” può assumere caratteri diversi ad esempio numerici, alfabetici, alfanumerici, caratteri speciali o numeri romani.</fo:block>
            <fo:block>Il Sistema verifica che il valore inserito nel metadato “Numero” risponda al formato definito.</fo:block>
            <fo:block>In assenza di indicazioni concordate con l’Ente, il numero della chiave viene accettato come se fosse una stringa (calcolo di ordinamento del numero di tipo “GENERICO”).</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:table>
                <fo:table-column column-width="40%"/>
                <fo:table-column column-width="30%"/>                        
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="15%"/>

                <fo:table-header font-weight="bold">
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Parametro</fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Descrizione</fo:block>
                    </fo:table-cell>                        
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Valore standard</fo:block>
                    </fo:table-cell>                        
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Valore effettivo (Richiesto dall'Ente)</fo:block>
                    </fo:table-cell>                        
                </fo:table-header>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Abilita controllo formato numero</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Verifica la conformità del formato Numero della chiave dell’unità documentaria</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(abilitaControlloFormatoNumero, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Accetta controllo formato numero negativo</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Accetta l’acquisizione di SIP in cui il formato numero non sia conforme con la configurazione del registro. In caso di esito negativo l’accettazione è legata all’impostazione del parametro successivo</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">NO</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(accettaControlloFormatoNumeroNegativo, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Forza formato numero</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">Il parametro regola l’accettazione del SIP in caso di esito negativo del controllo sul formato Numero della chiave dell’unità documentaria</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">NO</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(forzaFormatoNumero, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                </fo:table-body>
            </fo:table>  
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='controlloObblDatiProfiloUD']" >
        <xsl:call-template name="aCapo" />
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:table>
                <fo:table-column column-width="40%"/>
                <fo:table-column column-width="30%"/>                        
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="15%"/>

                <fo:table-header font-weight="bold">
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Parametro</fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Descrizione</fo:block>
                    </fo:table-cell>                        
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Valore standard</fo:block>
                    </fo:table-cell>                        
                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                        <fo:block>Valore effettivo (Richiesto dall'Ente)</fo:block>
                    </fo:table-cell>                        
                </fo:table-header>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Oggetto</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">L’attivazione del controllo implica l’obbligatorietà nell’Indice SIP dell’informazione relativa dell’oggetto del documento</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(obbligatorietaOggetto, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block>Data</fo:block>
                            </fo:block-container>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block-container overflow="hidden">
                                <fo:block font-weight="normal">L’attivazione del controllo implica l’obbligatorietà nell’Indice SIP dell’informazione relativa alla data del documento</fo:block>
                            </fo:block-container>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">SI</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="translate(obbligatorietaData, $lowercase, $uppercase)"/>
                            </fo:block>
                        </fo:table-cell>                        
                    </fo:table-row>
                </fo:table-body>
            </fo:table>  
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='oggettiDaTrasformare']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>Per oggetto da trasformare si intende un insieme di uno o più documenti che per ragioni di natura tecnica e organizzativa non è possibile produrre nel formato di SIP standard da trasmettere direttamente al Sistema di conservazione.</fo:block>
            <fo:block>In tali casi il processo di conservazione prevede una fase di Preacquisizione che ha in input un oggetto da trasformare dall’Ente e in output uno o più SIP standard da versare a Sacer.</fo:block>
            <fo:block>
                <xsl:text>La tabella </xsl:text>
                <xsl:value-of select="@nome"/>
                <xsl:text> elenca i tipi oggetto da trasformare restituendo per ciascuno l’informazione relativa al Versatore dell’oggetto, alla Trasformazione utilizzata e il tipo oggetto contenente i SIP standard da versare a Sacer.</xsl:text>
            </fo:block>
            <xsl:call-template name="aCapo" />
            <xsl:variable name="righe" select="tabella[@tipo='tipiOggettoDaTrasformare']/riga" />
            <xsl:if test="$righe">
                <fo:table>
                    <fo:table-header font-weight="bold">
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Tipo oggetto da trasformare</fo:block>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Descrizione</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Versatore</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Trasformazione</fo:block>
                        </fo:table-cell>                        
                    </fo:table-header>
                    <fo:table-body>
                        <xsl:for-each select="$righe">
                            <fo:table-row>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>
                                        <xsl:value-of select="nome"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>
                                        <xsl:value-of select="descrizione"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>
                                        <xsl:value-of select="versatore"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>
                                        <xsl:value-of select="trasformazione"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </xsl:for-each>
                    </fo:table-body>
                </fo:table>  
                <fo:block font-weight="bold">
                    <xsl:text>Tabella</xsl:text>
                    <xsl:text> </xsl:text> 
                    <xsl:value-of select="@nome"/>
                </fo:block>            
            </xsl:if>
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='gestioneSipRifiutati']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>Il Sistema registra i SIP il cui versamento è fallito in un’area temporanea del Sistema. Tali SIP sono conservati nel Sistema secondo le seguenti politiche:</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">i SIP relativi a versamenti falliti e successivamente andati a buon fine (c.d. errori risolti), sono conservati nel Sistema per un anno dal loro tentato versamento;</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">i SIP relativi a versamenti falliti che non saranno più ritentati (c.d. errori non risolubili), sono conservati nel Sistema per un anno dal loro tentato versamento;</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">i SIP relativi a versamenti falliti non risolti e non indicati come non risolubili, per i quali è identificata la Struttura versante, la chiave e la tipologia di unità documentaria, sono conservati nel Sistema per un anno dal momento in cui tutte le aggregazioni documentali previste per quella specifica tipologia di unità documentaria per l’anno indicato nella chiave del SIP sono state chiuse e prese in custodia;</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">i SIP relativi a versamenti falliti non risolti e non indicati come non risolubili, per i quali non è possibile identificare l’anno e/o la tipologia di unità documentaria e/o la Struttura versante, sono conservati per un anno dal tentato versamento.</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </fo:list-block>
            <xsl:call-template name="aCapo" />
            <fo:block>Per ulteriori informazioni in merito alle modalità di rifiuto o accettazione dei SIP trasmessi, si rinvia al Manuale,  al documento Specifiche tecniche dei servizi di versamento e al documento Codifiche errori in cui sono descritti gli errori restituiti dal Sistema a fronte delle operazioni di versamento, recupero e annullamento effettuate utilizzando i web service.</fo:block>
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='generazioneSerie']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>Le unità documentarie acquisite nel Sistema di conservazione sono selezionabili per essere aggregate in Serie di unità documentarie. Il criterio “standard” di generazione delle serie definito da ParER è basato su tre elementi:</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Anno di produzione/repertoriazione dell’unità documentaria;</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Tipologia di unità documentaria;</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Registro</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </fo:list-block>
            <xsl:call-template name="aCapo" />
            <fo:block>In accordo con l’Ente è prevista la possibilità di configurare criteri specifici per la generazione di serie di unità documentarie che non rientrano nei criteri standard, utilizzando, ad esempio, un metadato specifico o un particolare tipo di documento.</fo:block>
            <fo:block>Inoltre, è prevista per l’Ente la possibilità di comunicare la consistenza della serie specificando ad esempio il numero complessivo delle unità documentarie che appartengono alla serie o l’eventuale presenza di lacune. L’Ente può comunicare tali informazioni utilizzando apposite funzionalità del Sistema.</fo:block>
            <fo:block>Il processo di generazione della serie si conclude con la sua validazione definitiva da parte del Responsabile della funzione archivistica di conservazione di ParER e la generazione del relativo pacchetto di archiviazione (d’ora in poi AIP) di livello serie.</fo:block>
            <fo:block>
                <xsl:text>I tipi serie eventualmente configurati sono indicati nella Tabella </xsl:text>
                <xsl:value-of select="tabella[@tipo='tipiSerie']/@nome"/>
                <xsl:text>.</xsl:text>
            </fo:block>
            <xsl:call-template name="aCapo" />
            <xsl:variable name="righe" select="tabella[@tipo='tipiSerie']/riga" />
            <xsl:if test="$righe">
                <fo:table>
                    <fo:table-column column-width="40%"/>
                    <fo:table-column column-width="40%"/>                        
                    <fo:table-column column-width="20%"/>
                    <fo:table-header font-weight="bold">
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Tipo serie</fo:block>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Descrizione</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Anni di conservazione</fo:block>
                        </fo:table-cell>                        
                    </fo:table-header>
                    <fo:table-body>
                        <xsl:for-each select="$righe">
                            <fo:table-row>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>
                                        <xsl:value-of select="nome"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>
                                        <xsl:value-of select="descrizione"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>
                                        <xsl:value-of select="anni"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </xsl:for-each>
                    </fo:table-body>
                </fo:table>  
                <fo:block font-weight="bold">
                    <xsl:text>Tabella</xsl:text>
                    <xsl:text> - </xsl:text> 
                    <xsl:value-of select="tabella[@tipo='tipiSerie']/@nome"/>
                </fo:block>            
            </xsl:if>
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='modalitaAnnullamentoVersamenti']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>Nel caso in cui un versamento andato a buon fine sia stato effettuato per errore o contenga degli errori non correggibili altrimenti, l’Ente provvede ad annullarlo utilizzando apposite funzionalità del Sistema oppure inviando al personale di ParER una richiesta formale completa degli estremi dei documenti da annullare e relativa motivazione.</fo:block>
            <fo:block>Gli oggetti non sono cancellati dal Sistema ma marcati come Annullati e sono comunque sempre consultabili tramite l’apposita sezione di ricerca.</fo:block>
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='modalitaRestituzioneOggettiVersati']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>ParER garantisce il mantenimento nel proprio Sistema dei Documenti informatici e delle Aggregazioni documentali informatiche conservati, con i metadati a essi associati e le evidenze informatiche generate nel corso del processo di conservazione fino alla comunicazione da parte dell’Ente dell’effettiva messa a disposizione del Sistema in cui effettuare il riversamento.</fo:block>
            <fo:block>ParER provvederà all’eliminazione dal proprio Sistema di tutti gli oggetti riversati e di tutti gli elementi riferiti all’Ente solo al termine del riversamento e solo dopo le opportune verifiche - effettuate da entrambe le Parti e svolte di concerto tra le stesse – di corretto svolgimento del riversamento stesso. In tal caso viene garantita la completa cancellazione e non leggibilità dei dati. L’intera operazione dovrà comunque avvenire con l’autorizzazione e la vigilanza delle competenti autorità, in particolare delle strutture del MIBACT.</fo:block>
            <fo:block>In caso di chiusura del servizio da parte della Regione Emilia-Romagna, con interventi di modifica alla normativa regionale, si provvederà a trasferire quanto conservato al Sistema individuato per proseguire le attività svolte da IBACN. Per quanto riguarda gli aspetti operativi per il trasferimento di archivi ad altri sistemi di conservazione, ParER adotta lo standard Uni Sincro, e provvederà a trasferire secondo canali sicuri concordati con l’Ente o con il nuovo Conservatore le informazioni. Analogamente il Sistema è predisposto per la ricezione di archivi in formato Uni Sincro; qualora il precedente sistema di conservazione non sia in grado di produrre l’archivio in formato Uni Sincro, ParER, a seguito di specifici accordi, può mettere a disposizione dell’Ente consulenza e strumenti per facilitare il trasferimento dell’archivio.</fo:block>
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='tipologieUD']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>
                <xsl:text>La Tabella</xsl:text>
                <xsl:text> </xsl:text>
                <xsl:value-of select="@nome"/>
                <xsl:text> </xsl:text>
                <xsl:text>elenca le tipologie di unità documentarie oggetto di versamento e restituisce per ciascuna l’informazione relativa al sistema versante e alla data di primo versamento.</xsl:text>
            </fo:block>
            <xsl:call-template name="aCapo" />
            <fo:block >Per unità documentaria si intende un aggregato logico costituito da uno più documenti che sono considerati come un tutto unico. I documenti sono gli elementi dell’unità documentaria e sono identificati in base alla funzione che svolgono nel contesto dell’unità documentaria stessa, ovvero:</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Documento principale: documento che deve essere obbligatoriamente presente nell’unità documentaria, della quale definisce il contenuto primario;</fo:block>
                        <xsl:call-template name="aCapo" />
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Allegato: documento che compone l’unità documentaria per integrare le informazioni contenute nel documento principale. È redatto contestualmente o precedentemente al documento principale;</fo:block>
                        <xsl:call-template name="aCapo" />
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Annesso: documento che compone l’unità documentaria, generalmente prodotto e inserito nell’unità documentaria in un momento successivo a quello di creazione dell’unità documentaria, per fornire ulteriori notizie e informazioni a corredo del documento principale;</fo:block>
                        <xsl:call-template name="aCapo" />
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Annotazione: documento che compone l’unità documentaria riportante gli elementi identificativi del documento e del suo iter documentale (un tipico esempio di Annotazione è rappresentato dalla segnatura di protocollo);</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </fo:list-block>
            <xsl:call-template name="aCapo" />
            <fo:block>Per sistema versante si intende il sistema che cura, sotto forma di pacchetto di versamento (SIP), la trasmissione dei documenti al Sistema, direttamente o tramite sistemi intermedi, esterni all’Ente. Al sistema versante è associato l’utente versatore indicato nel SIP. In caso di utilizzo del client on line di versamento messo a disposizione da ParER o del servizio di versamento asincrono, la colonna Sistema versante sarà compilata rispettivamente con “SACER_VERSO” e “SACER_PREINGEST”. Si precisa che il sistema versante e la modalità di versamento possono variare nel tempo e che il versamento della stessa tipologia di unità documentaria può essere contestualmente curato da più sistemi. In tali casi la tabella riporterà l’indicazione di tutti i sistemi versanti e utenti versatori utilizzati per il versamento.</fo:block>
            <fo:block>
                <xsl:text>Nel caso in cui alla data di estrazione del Disciplinare non siano ancora stati effettuati versamenti risolti, le informazioni relative al nome del sistema versante, utente versatore e data di primo versamento non sono riportate nella Tabella </xsl:text>
                <xsl:value-of select="@nome"/>.</fo:block>
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <!-- TABELLA CON ELENCO SINTETICO DELLE UD -->
            <xsl:if test="capitolo[@tipo='unitaDocumentaria']">
                <fo:table>
                    <fo:table-column column-width="35%"/>
                    <fo:table-column column-width="30%"/>                        
                    <fo:table-column column-width="20%"/>                        
                    <fo:table-column column-width="15%"/>

                    <fo:table-header font-weight="bold">
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Tipologia di unità documentaria</fo:block>
                        </fo:table-cell>
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Sistema versante</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Utente versatore</fo:block>
                        </fo:table-cell>                        
                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                            <fo:block>Data di primo versamento</fo:block>
                        </fo:table-cell>                        
                    </fo:table-header>
                    <fo:table-body>
                        <xsl:for-each select="capitolo[@tipo='unitaDocumentaria']" >
                            <fo:table-row>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block-container overflow="hidden">
                                        <fo:block>
                                            <xsl:value-of select="nomeUD"/>
                                        </fo:block>
                                    </fo:block-container>                                
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block font-weight="normal">
                                        <xsl:value-of select="sistemiVersanti"/>
                                    </fo:block>
                                </fo:table-cell>                        
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block font-weight="normal">
                                        <!--                                        <xsl:value-of select="utentiVersatori"/> -->
                                        <xsl:call-template name="intersperse-with-zero-spaces">
                                            <xsl:with-param name="str" select="utentiVersatori"/>
                                        </xsl:call-template>                                        
                                    </fo:block>
                                </fo:table-cell>                        
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block font-weight="normal">
                                        <xsl:value-of select="dataPrimoVersamento"/>
                                    </fo:block>
                                </fo:table-cell>                        
                            </fo:table-row>
                        </xsl:for-each>
                    </fo:table-body>
                </fo:table>  
                <fo:block font-weight="bold">
                    <xsl:text>Tabella</xsl:text>
                    <xsl:text> - </xsl:text>
                    <xsl:value-of select="@nome"/>
                </fo:block>
            </xsl:if>
            <xsl:call-template name="aCapo" />
            <fo:block >Nei paragrafi seguenti sono descritte in dettaglio le singole tipologie di unità documentarie in termini di struttura e metadati.</fo:block>
            <xsl:call-template name="aCapo" />
            <xsl:apply-templates select="capitolo[@tipo='unitaDocumentaria']" />
        </fo:block>
    </xsl:template>

    <!-- Dettaglio del capitolo della singola UD -->
    <xsl:template match="capitolo[@tipo='unitaDocumentaria']" >
        <fo:block id="{generate-id(.)}" page-break-after="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />

            <xsl:variable name="capDescrizioneUD" select="capitolo[@tipo='descrizioneUD']" />
            <xsl:if test="$capDescrizioneUD">
                <fo:block id="{generate-id($capDescrizioneUD)}" font-weight="bold">
                    <!--
                                        <xsl:call-template name="stampaCapitoloInterno">
                                            <xsl:with-param name="cap" select="$capDescrizioneUD"/>
                                        </xsl:call-template>
                    -->                    
                    <fo:block>
                        <fo:inline font-weight="bold">
                            <xsl:value-of select="$capDescrizioneUD/@nome" />:
                        </fo:inline>
                        <fo:inline font-weight="normal">
                            <xsl:if test="descrizioneUD">
                                <xsl:value-of select="descrizioneUD" />
                            </xsl:if>
                        </fo:inline>
                    </fo:block>  
                    <xsl:call-template name="aCapo" />
                    <xsl:if test="not(noteUD='null')">
                        <fo:block> 
                            <fo:inline font-weight="bold">Note Tipologia di unità documentaria: </fo:inline>
                            <fo:inline font-weight="normal"><xsl:value-of select="noteUD" /></fo:inline>
                        </fo:block>
                        <xsl:call-template name="aCapo" />
                    </xsl:if>
                </fo:block>
            </xsl:if>
            <xsl:variable name="capStruttureUD" select="capitolo[@tipo='struttureUD']" />
            <xsl:if test="$capStruttureUD">
                <fo:block id="{generate-id($capStruttureUD)}" font-weight="bold">
                    <!--                    
                    <xsl:call-template name="stampaCapitoloInterno">
                        <xsl:with-param name="cap" select="$capStrutturaUD"/>
                    </xsl:call-template>
                    -->
                    
                    <fo:block>
                        <fo:inline font-weight="bold">
                            <xsl:value-of select="$capStruttureUD/@nome" />
                            <xsl:text>:</xsl:text>
                        </fo:inline>
                        <fo:inline font-weight="normal">
                            <xsl:text> nella Tabella Tipi struttura unità documentaria sono rappresentate le varie strutture che può assumere il tipo di unità documentaria, ovvero la sua articolazione in documento principale ed eventuali allegati, annessi e annotazioni, le informazioni relative alla chiave, ai metadati di profilo e altro ancora.</xsl:text>
                        </fo:inline>
                    </fo:block>  
                    <fo:block>
                        <fo:inline font-weight="normal">
                            <xsl:text>Per ogni Tipo struttura è indicato il periodo di validità, ovvero per quali annualità delle unità documentarie è valido il tipo struttura e i sistemi versanti utilizzati.</xsl:text>
                        </fo:inline>
                    </fo:block>  
                    <xsl:call-template name="aCapo" />
                    <fo:table>
                        <fo:table-column column-width="25%"/>
                        <fo:table-column column-width="35%"/>
                        <fo:table-column column-width="20%"/>                        
                        <fo:table-column column-width="20%"/>                        
                        <fo:table-header font-weight="bold">
                            <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                <fo:block>Denominazione</fo:block>
                            </fo:table-cell>
                            <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                <fo:block>Descrizione</fo:block>
                            </fo:table-cell>
                            <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                <fo:block>Periodi di validità</fo:block>
                            </fo:table-cell>                        
                            <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                <fo:block>Sistemi versanti</fo:block>
                            </fo:table-cell>                        
                        </fo:table-header>
                        <fo:table-body>
                            <xsl:for-each select="$capStruttureUD/tabella[@tipo='struttureUD']/riga">
                                <fo:table-row>
                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                        <fo:block font-weight="normal">
                                            <xsl:value-of select="nome"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                        <fo:block font-weight="normal">
                                            <xsl:value-of select="descrizione"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                        <fo:block font-weight="normal">
                                            <xsl:call-template name="scriviSeNonNullo"><xsl:with-param name="campo" select="dal"/></xsl:call-template>
                                            <xsl:call-template name="scriviSeNonNullo"><xsl:with-param name="campo" select="annoInizioValidita"/></xsl:call-template>
<!--
                                            <xsl:if test="not(annoInizioValidita='null')">
                                                <xsl:value-of select="annoInizioValidita"/> 
                                            </xsl:if>
-->                                                            
                                            <xsl:if test="not(annoFineValidita='null')">
                                                - <xsl:value-of select="annoFineValidita"/>    
                                            </xsl:if>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                        <fo:block font-weight="normal">
                                            <xsl:for-each select="sistemiVersanti/sistema">
                                                <xsl:value-of select="." />
                                                <xsl:if test="not(position()=last())"> 
                                                    <xsl:text>, </xsl:text>
                                                </xsl:if>
                                            </xsl:for-each>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>  
                    <fo:block font-weight="bold">Tabella - Tipi struttura unità documentaria</fo:block>
                    <xsl:call-template name="aCapo" />
                    <fo:block>
                        <fo:inline font-weight="normal">
                            <xsl:text>Nei paragrafi successivi sono riportate le informazioni di dettaglio su ogni singolo Tipo struttura dell’unità documentaria</xsl:text>
                        </fo:inline>
                    </fo:block>  
                    
                    <xsl:call-template name="aCapo" />
                </fo:block>
                
                <xsl:variable name="capStrutturaUD" select="$capStruttureUD/capitolo[@tipo='strutturaUD']" />
                <xsl:if test="$capStrutturaUD">
                    <xsl:for-each select="$capStrutturaUD">                    
                        <fo:block id="{generate-id(.)}" font-weight="bold">
                            <!--                    
                            <xsl:call-template name="stampaCapitoloInterno">
                                <xsl:with-param name="cap" select="$capStrutturaUD"/>
                            </xsl:call-template>
                            -->

                            <fo:block>
                                <fo:inline font-weight="bold">
                                    <xsl:value-of select="./@nome" />
                                </fo:inline>
                                <fo:inline font-weight="normal">
                                    <xsl:text>Nella tabella è riportata l'articolazione dell’unità documentaria in documento principale ed eventuali allegati, annessi e annotazioni.</xsl:text>
                                </fo:inline>
                            </fo:block>  
                            <xsl:if test="not(descrizione='null')">
                                <fo:block>
                                    <fo:inline font-weight="normal">
                                        <xsl:text>Descrizione:</xsl:text>
                                    </fo:inline>
                                    <fo:inline font-weight="normal">
                                        <xsl:value-of select="descrizione" />
                                    </fo:inline>
                                </fo:block>  
                            </xsl:if>
                            <xsl:call-template name="aCapo" />
                            <xsl:if test="./tabella[@tipo='strutturaUD']/riga">
                                <fo:table>
                                    <fo:table-column column-width="20%"/>
                                    <fo:table-column column-width="25%"/>                        
                                    <fo:table-column column-width="25%"/>
                                    <fo:table-column column-width="15%"/>                        
                                    <fo:table-column column-width="15%"/>                        
                                    <fo:table-header font-weight="bold">
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block>Elemento</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block>Tipo documento</fo:block>
                                        </fo:table-cell>                        
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block>Descrizione</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block>Obbligatorio</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block>Note</fo:block>
                                        </fo:table-cell>
                                    </fo:table-header>
                                    <fo:table-body>
                                        <xsl:for-each select="./tabella[@tipo='strutturaUD']/riga">
                                            <fo:table-row>
                                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                    <fo:block font-weight="normal">
                                                        <xsl:value-of select="elemento"/>
                                                    </fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                    <fo:block font-weight="normal">
                                                        <xsl:value-of select="tipoDocumento"/>
                                                    </fo:block>
                                                </fo:table-cell>                        
                                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                    <fo:block font-weight="normal">
                                                        <xsl:value-of select="descrizione"/>
                                                    </fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                    <fo:block font-weight="normal">
                                                        <xsl:value-of select="obbligatorio"/>
                                                    </fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                    <fo:block font-weight="normal">
                                                        <xsl:call-template name="scriviSeNonNullo"><xsl:with-param name="campo" select="note"/></xsl:call-template>
                                                    </fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </xsl:for-each>
                                    </fo:table-body>
                                </fo:table>  
                                <fo:block font-weight="bold">Tabella - <xsl:value-of select="@nome"/></fo:block>
                                <xsl:call-template name="aCapo" />
                                <xsl:call-template name="aCapo" />
                            </xsl:if>
                        </fo:block>
                        
                        <xsl:variable name="tabRegistriStrutturaUD" select="./tabella[@tipo='registriStrutturaUD']" />
                        <xsl:if test="$tabRegistriStrutturaUD/riga">
                            <fo:block>
                                <fo:inline font-weight="bold">
                                    <xsl:text>Chiave identificativa del tipo struttura dell’unità documentaria (Tipo registro - Anno - Numero)</xsl:text>
                                </fo:inline>
                            </fo:block>  
                            <xsl:call-template name="aCapo" />
                            <fo:block>
                                <fo:inline font-weight="bold">
                                    <xsl:text>Registri</xsl:text>
                                </fo:inline>
                                <fo:inline font-weight="normal">
                                    <xsl:text>- Nella Tabella Elenco registri è rappresentata la lista dei registri associati all’unità documentaria. Il nome del registro viene definito dall’Ente o concordato con ParER e in generale corrisponde al nome del repertorio in cui sono registrati in ordine progressivo i documenti ad esso afferenti (ex art. 53, D.P.R. 28/12/2000 n. 445) oppure al contesto applicativo/documentale nell’ambito del quale avviene l’assegnazione dell’identificativo progressivo e univoco.</xsl:text>
                                </fo:inline>
                            </fo:block>  
                            <fo:block font-weight="normal">Il parametro “Fiscale” indica che le unità documentarie associate al Registro sono soggette alla conservazione di tipo fiscale, finalizzata alla conservazione a norma dei documenti rilevanti ai fini tributari in conformità con quanto previsto dalla normativa di settore vigente (DM del 17 giugno 2014 del Ministero dell’economia e delle finanze)</fo:block>    
                            <xsl:call-template name="aCapo" />                            
                                                        
                            <fo:table width="100%">
                                <fo:table-column column-width="18%"/>
                                <fo:table-column column-width="19%"/>                        
                                <fo:table-column column-width="10%"/>
                                <fo:table-column column-width="15%"/>                        
                                <fo:table-column column-width="9%"/>                        
                                <fo:table-column column-width="13%"/>
                                <fo:table-column column-width="16%"/>
                                <fo:table-header font-weight="bold">
                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                        <fo:block>Tipo registro</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                        <fo:block>Descrizione</fo:block>
                                    </fo:table-cell>                        
                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                        <fo:block>Periodi di validità</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                        <fo:block>Descrizione formato numero</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                        <fo:block>Fiscale</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                        <fo:block>Data attivazione</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                        <fo:block>Data disattivazione</fo:block>
                                    </fo:table-cell>
                                </fo:table-header>
                                <fo:table-body>

                                    <xsl:for-each select="$tabRegistriStrutturaUD/riga">
                                        <fo:table-row>
                                            <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                <fo:block font-weight="normal">
                                                    <xsl:value-of select="tipoRegistro"/>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                <fo:block font-weight="normal">
                                                    <xsl:value-of select="descRegistro"/>
                                                </fo:block>
                                            </fo:table-cell>                        
                                            <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                <fo:block font-weight="normal">
                                                    <xsl:for-each select="periodiValidita/periodo">
                                                        <xsl:value-of select="dal" />
                                                        <xsl:if test="not(al='null')">
                                                            - <xsl:value-of select="al" />    
                                                        </xsl:if>
                                                        <xsl:if test="not(position()=last())">, 
                                                            <xsl:call-template name="aCapo" />
                                                        </xsl:if>
                                                    </xsl:for-each>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                <fo:block font-weight="normal">
                                                    <xsl:call-template name="scriviSeNonNullo"><xsl:with-param name="campo" select="periodiValidita/periodo/descFormatoNumero"/></xsl:call-template>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                <fo:block font-weight="normal">
                                                    <xsl:value-of select="fiscale"/>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                <fo:block font-weight="normal">
                                                    <xsl:value-of select="dataAttivazione"/>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                <fo:block font-weight="normal">
                                                    <xsl:call-template name="scriviData"><xsl:with-param name="campo" select="dataDisattivazione"/></xsl:call-template>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:for-each>
                                </fo:table-body>
                            </fo:table>  
                            <fo:block font-weight="bold">Tabella - Elenco registri</fo:block>
                            <xsl:call-template name="aCapo" />
                            <xsl:call-template name="aCapo" />
                        
                            <fo:block>
                                <fo:inline font-weight="bold">
                                    <xsl:text>Anno</xsl:text>
                                </fo:inline>
                                <fo:inline font-weight="normal">
                                    <xsl:text>- </xsl:text>
                                    <xsl:call-template name="scriviSeNonNullo"><xsl:with-param name="campo" select="anno"/></xsl:call-template>
                                </fo:inline>
                            </fo:block>  
                            <xsl:call-template name="aCapo" />
                            <fo:block>
                                <fo:inline font-weight="bold">
                                    <xsl:text>Numero</xsl:text>
                                </fo:inline>
                                <fo:inline font-weight="normal">
                                    <xsl:text>- </xsl:text>
                                    <xsl:call-template name="scriviSeNonNullo"><xsl:with-param name="campo" select="numero"/></xsl:call-template>                                
                                </fo:inline>
                            </fo:block>  
                            <xsl:call-template name="aCapo" />
                            <fo:block>
                                <fo:inline font-weight="bold">
                                    <xsl:text>Riferimento temporale</xsl:text>
                                </fo:inline>
                                <fo:inline font-weight="normal">
                                    <xsl:text>- </xsl:text>
                                    <xsl:call-template name="scriviSeNonNullo"><xsl:with-param name="campo" select="riferimentoTemporale"/></xsl:call-template>  
                                </fo:inline>
                            </fo:block>  
                            <xsl:call-template name="aCapo" />
                            <xsl:call-template name="aCapo" />
                        </xsl:if>
                        
                        <fo:block>
                            <fo:block>
                                <fo:inline font-weight="bold">
                                    <xsl:text>Metadati di profilo del tipo unità documentaria</xsl:text>
                                </fo:inline>
                                <fo:inline font-weight="normal">
                                    <xsl:text>– Nella Tabella Metadati di profilo dell’unità documentaria sono descritti i metadati “Data” e “Oggetto” associati all’unità documentaria</xsl:text>
                                </fo:inline>
                            </fo:block>  
                            <xsl:call-template name="aCapo" />
                            <fo:table width="100%">
                                <fo:table-column column-width="50%"/>
                                <fo:table-column column-width="50%"/>                        
                                <fo:table-header font-weight="bold">
                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                        <fo:block>Denominazione</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                        <fo:block>Descrizione</fo:block>
                                    </fo:table-cell>                        
                                </fo:table-header>
                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">Data</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">
                                                <xsl:call-template name="scriviSeNonNullo"><xsl:with-param name="campo" select="data"/></xsl:call-template>                                                
                                            </fo:block>
                                        </fo:table-cell>                        
                                    </fo:table-row>
                                    <fo:table-row>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">Oggetto</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">
                                                <xsl:call-template name="scriviSeNonNullo"><xsl:with-param name="campo" select="oggetto"/></xsl:call-template>
                                            </fo:block>
                                        </fo:table-cell>                        
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>  
                        </fo:block>
                        <fo:block font-weight="bold">Tabella - Metadati di profilo dell'unità documentaria</fo:block>
                        <xsl:call-template name="aCapo" />
                        <xsl:call-template name="aCapo" />
                    </xsl:for-each>
                </xsl:if>
                
            </xsl:if>
<!--
            <xsl:variable name="capRegistriAmmessi" select="capitolo[@tipo='registriAmmessi']" />
            <xsl:if test="$capRegistriAmmessi">
                <fo:block id="{generate-id($capRegistriAmmessi)}" font-weight="bold">
                    <fo:block>
                        <fo:inline font-weight="bold">
                            <xsl:value-of select="$capRegistriAmmessi/@nome" />
                        </fo:inline>
                        <fo:inline font-weight="normal">
                            <xsl:text>- Nella Tabella Elenco registri è rappresentata la lista dei registri associati all’unità documentaria. Il nome del registro viene definito dall’Ente o concordato con ParER e in generale corrisponde al nome del repertorio in cui sono registrati in ordine progressivo i documenti ad esso afferenti (ex art. 53, D.P.R. 28/12/2000 n. 445) oppure al contesto applicativo/documentale nell’ambito del quale avviene l’assegnazione dell’identificativo progressivo e univoco.</xsl:text>
                        </fo:inline>
                    </fo:block>  
                    <fo:block font-weight="normal">Il parametro “Fiscale” indica che le unità documentarie associate al Registro sono soggette alla conservazione di tipo fiscale, finalizzata alla conservazione a norma dei documenti rilevanti ai fini tributari in conformità con quanto previsto dalla normativa di settore vigente (DM del 17 giugno 2014 del Ministero dell’economia e delle finanze)</fo:block>    
                    <xsl:call-template name="aCapo" />
                    <xsl:if test="$capRegistriAmmessi/tabella[@tipo='registriAmmessi']/riga">
                        <fo:table width="100%">
                            <fo:table-column column-width="18%"/>
                            <fo:table-column column-width="19%"/>                        
                            <fo:table-column column-width="10%"/>
                            <fo:table-column column-width="15%"/>                        
                            <fo:table-column column-width="9%"/>                        
                            <fo:table-column column-width="13%"/>
                            <fo:table-column column-width="16%"/>
                            <fo:table-header font-weight="bold">
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>Tipo registro</fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>Descrizione</fo:block>
                                </fo:table-cell>                        
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>Periodi di validità</fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>Descrizione formato numero</fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>Fiscale</fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>Data attivazione</fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>Data disattivazione</fo:block>
                                </fo:table-cell>
                            </fo:table-header>
                            <fo:table-body>
                                <xsl:for-each select="$capRegistriAmmessi/tabella[@tipo='registriAmmessi']/riga">
                                    <fo:table-row>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">
                                                <xsl:value-of select="tipoRegistro"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">
                                                <xsl:value-of select="descRegistro"/>
                                            </fo:block>
                                        </fo:table-cell>                        
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">
                                                <xsl:for-each select="periodiValidita/periodo">
                                                    <xsl:value-of select="dal" />
                                                    <xsl:if test="not(al='null')">
                                                        - <xsl:value-of select="al" />    
                                                    </xsl:if>
                                                    <xsl:if test="not(position()=last())">, 
                                                        <xsl:call-template name="aCapo" />
                                                    </xsl:if>
                                                </xsl:for-each>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">
                                                <xsl:if test="not(periodiValidita/periodo/descFormatoNumero='null')">
                                                    <xsl:value-of select="periodiValidita/periodo/descFormatoNumero"/>
                                                </xsl:if>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">
                                                <xsl:value-of select="fiscale"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">
                                                <xsl:value-of select="dataAttivazione"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">
                                                <xsl:if test="not(dataDisattivazione='31/12/2444')">
                                                    <xsl:value-of select="dataDisattivazione"/>
                                                </xsl:if>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </xsl:for-each>
                            </fo:table-body>
                        </fo:table>  
                        <fo:block font-weight="bold">Tabella - Elenco registri</fo:block>
                        <xsl:call-template name="aCapo" />
                        <xsl:call-template name="aCapo" />
                    </xsl:if>
                    <xsl:call-template name="aCapo" />
                    <xsl:call-template name="aCapo" />
                </fo:block>
            </xsl:if>
-->
            <xsl:variable name="capMetadatiSpecificiUD" select="capitolo[@tipo='metadatiSpecificiUD']" />
            <xsl:if test="$capMetadatiSpecificiUD">
                <fo:block id="{generate-id($capMetadatiSpecificiUD)}" font-weight="bold">
                    <!--                    
                                        <xsl:call-template name="stampaCapitoloInterno">
                                            <xsl:with-param name="cap" select="$capMetadatiSpecificiUD"/>
                                        </xsl:call-template>
                    -->                    
                    <fo:block>
                        <fo:inline font-weight="bold">
                            <xsl:value-of select="$capMetadatiSpecificiUD/@nome" />
                        </fo:inline>
                        <fo:inline font-weight="normal">
                            <xsl:text>- Nelle Tabelle Metadati specifici dell’unità documentaria è presentato il set di metadati associato all’unità documentaria nelle diverse versioni. La presenza di versioni diverse è legata alle eventuali modifiche/integrazioni/aggiornamenti nel set di metadati specifici o nelle obbligatorietà concordate con l’Ente.</xsl:text>
                        </fo:inline>
                    </fo:block>  
                    <xsl:call-template name="aCapo" />
                    <xsl:for-each select="$capMetadatiSpecificiUD/tabella[@tipo='metadatiSpecificiUD']" >
                        <fo:block font-weight="normal" font-style="italic">Versione metadati specifici: <xsl:value-of select="versione"/></fo:block>
                        <fo:block font-weight="normal" font-style="italic">Descrizione versione:
                            <xsl:call-template name="scriviSeNonNullo"><xsl:with-param name="campo" select="descrizione"/></xsl:call-template>
                        </fo:block>
                        <fo:block font-weight="normal" font-style="italic">Data inizio validità: <xsl:value-of select="dataInizioValidita"/></fo:block>
                        <fo:block font-weight="normal" font-style="italic">Data fine validità: 
                            <xsl:call-template name="scriviData"><xsl:with-param name="campo" select="dataFineValidita"/></xsl:call-template>
                        </fo:block>
                        <xsl:call-template name="aCapo" />
                        <fo:table>
                            <fo:table-header font-weight="bold">
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>Denominazione</fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>Descrizione</fo:block>
                                </fo:table-cell>
                            </fo:table-header>
                            <fo:table-body>
                                <xsl:for-each select="riga" >
                                    <fo:table-row>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">
                                                <!--                                                <xsl:value-of select="denominazione"/> -->
                                                <xsl:call-template name="intersperse-with-zero-spaces">
                                                    <xsl:with-param name="str" select="denominazione"/>
                                                </xsl:call-template>                                        
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">
                                                <!--                                                <xsl:value-of select="descrizione"/> -->
                                                <xsl:call-template name="intersperse-with-zero-spaces">
                                                    <xsl:with-param name="str" select="descrizione"/>
                                                </xsl:call-template>                                        
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </xsl:for-each>
                            </fo:table-body>
                        </fo:table> 
                        <fo:block font-weight="bold">Tabella - Metadati specifici dell'unità documentaria</fo:block>
                        <xsl:call-template name="aCapo" />
                        <xsl:call-template name="aCapo" />
                    </xsl:for-each>
                </fo:block>
            </xsl:if>
            <xsl:variable name="capTipiDocumento" select="capitolo[@tipo='tipoDocumentoUD']" />
            <xsl:call-template name="aCapo" />
            <xsl:if test="$capTipiDocumento">
                <xsl:for-each select="$capTipiDocumento">
                    <fo:block id="{generate-id(.)}" font-weight="bold" >
                        <xsl:variable name="capMetadatiSpecificiTipoDoc" select="capitolo[@tipo='metadatiSpecificiTipoDoc']" />
                        <xsl:if test="$capMetadatiSpecificiTipoDoc">
                            <fo:block id="{generate-id($capMetadatiSpecificiTipoDoc)}" font-weight="bold"> 
                                <!--                                
                                                                <xsl:call-template name="stampaCapitoloInterno">
                                                                    <xsl:with-param name="cap" select="$capMetadatiSpecificiTipoDoc"/>
                                                                </xsl:call-template>
                                -->
                                <fo:block>
                                    <fo:inline font-weight="bold">
                                        <xsl:value-of select="$capMetadatiSpecificiTipoDoc/@nome" />
                                    </fo:inline>
                                    <fo:inline font-weight="normal">
                                        <xsl:text>- Nelle tabelle “Metadati specifici del documento” è presentato il set di metadati specifici associato al documento nelle varie versioni. La presenza di versioni diverse è legata alle eventuali modifiche/integrazioni/aggiornamenti nel set di metadati specifici e nella obbligatorietà concordate con l'Ente.</xsl:text>
                                    </fo:inline>
                                </fo:block>  
                                <xsl:call-template name="aCapo" />
                                <xsl:for-each select="$capMetadatiSpecificiTipoDoc/tabella[@tipo='metadatiSpecificiTipoDoc']" >
                                    <fo:block font-weight="normal" font-style="italic">Versione metadati specifici: <xsl:value-of select="versione"/></fo:block>
                                    <fo:block font-weight="normal" font-style="italic">Descrizione versione: 
                                        <xsl:call-template name="scriviSeNonNullo"><xsl:with-param name="campo" select="descrizione"/></xsl:call-template>
                                    </fo:block>
                                    <fo:block font-weight="normal" font-style="italic">Data inizio validità: <xsl:value-of select="dataInizioValidita"/></fo:block>
                                    <fo:block font-weight="normal" font-style="italic">Data fine validità: 
                                        <xsl:call-template name="scriviData"><xsl:with-param name="campo" select="dataFineValidita"/></xsl:call-template>
                                    </fo:block>
                                    <xsl:call-template name="aCapo" />
                                    <fo:table>
                                        <fo:table-header font-weight="bold">
                                            <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                <fo:block>Denominazione</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                <fo:block>Descrizione</fo:block>
                                            </fo:table-cell>
                                        </fo:table-header>
                                        <fo:table-body>
                                            <xsl:for-each select="riga" >
                                                <fo:table-row>
                                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                        <fo:block font-weight="normal">
                                                            <xsl:call-template name="intersperse-with-zero-spaces">
                                                                <xsl:with-param name="str" select="denominazione"/>
                                                            </xsl:call-template>                                        
                                                        </fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                        <fo:block font-weight="normal">
                                                            <xsl:call-template name="intersperse-with-zero-spaces">
                                                                <xsl:with-param name="str" select="descrizione"/>
                                                            </xsl:call-template>                                        
                                                        </fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                            </xsl:for-each>
                                        </fo:table-body>
                                    </fo:table>  
                                    <fo:block font-weight="bold">Tabella - Metadati specifici del documento</fo:block>
                                    <xsl:call-template name="aCapo" />
                                </xsl:for-each>
                            </fo:block>
                        </xsl:if>
                    </fo:block>
                    
                </xsl:for-each> 
            </xsl:if>
        </fo:block>
    </xsl:template>

    <!-- ************************************************************************* -->
    <!-- ************************************************************************* -->
    <!-- ************************************************************************* -->
    <!-- Stampa un 'a capo' -->
    <xsl:template name="aCapo">
        <fo:block linefeed-treatment="preserve" white-space-collapse="false" white-space-treatment="preserve">
            <xsl:text>&#9;</xsl:text>
        </fo:block>
    </xsl:template>

    <xsl:template name="stampaCapitolo">
        <xsl:param name="cap" />
        <xsl:param name="descCap" />
        <fo:block font-size="12pt">
            <fo:inline font-weight="bold">
                <xsl:value-of select="$cap/@nome" />
            </fo:inline>
            <fo:inline font-weight="normal">
                <xsl:if test="$descCap">
                    <xsl:text>:&#160;</xsl:text>
                    <xsl:value-of select="$descCap" />
                </xsl:if>
            </fo:inline>
        </fo:block>  
    </xsl:template>

    <xsl:template name="stampaCapitoloInterno">
        <xsl:param name="cap" />
        <xsl:param name="descCap" />
        <fo:block>
            <fo:inline font-weight="bold">
                <xsl:value-of select="$cap/@nome" />
            </fo:inline>
            <fo:inline font-weight="normal">
                <xsl:if test="$descCap">
                    <xsl:value-of select="$descCap" />
                </xsl:if>
            </fo:inline>
        </fo:block>  
    </xsl:template>

    <xsl:template name="scriviSeNonNullo">
        <xsl:param name="campo" />
        <xsl:if test="not($campo='null')">
            <xsl:value-of select="$campo" />
        </xsl:if>
    </xsl:template>

    <xsl:template name="scriviData">
        <xsl:param name="campo" />
        <xsl:if test="not($campo='31/12/2444')">
            <xsl:value-of select="$campo" />
        </xsl:if>
    </xsl:template>
                
    <xsl:template name="intersperse-with-zero-spaces">
        <xsl:param name="str"/>
        <xsl:variable name="spacechars">
            &#x9;&#xA;
            &#x2000;&#x2001;&#x2002;&#x2003;&#x2004;&#x2005;
            &#x2006;&#x2007;&#x2008;&#x2009;&#x200A;&#x200B;
        </xsl:variable>

        <xsl:if test="string-length($str) &gt; 0">
            <xsl:variable name="c1" select="substring($str, 1, 1)"/>
            <xsl:variable name="c2" select="substring($str, 2, 1)"/>

            <xsl:value-of select="$c1"/>
            <xsl:if test="$c2 != '' and
                not(contains($spacechars, $c1) or
                contains($spacechars, $c2))">
                <xsl:text>&#x200B;</xsl:text>
            </xsl:if>

            <xsl:call-template name="intersperse-with-zero-spaces">
                <xsl:with-param name="str" select="substring($str, 2)"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>                 
                        
</xsl:stylesheet>
