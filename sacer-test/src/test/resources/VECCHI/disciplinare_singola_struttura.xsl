<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" 
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://www.w3.org/1999/XSL/Format fop.xsd">
    
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
        <fo:root font-family="Verdana">
            <fo:layout-master-set>    
                <fo:simple-page-master master-name="PaginaA4"
                                       page-height="29.7cm" page-width="21cm" margin-top="1.5cm"
                                       margin-bottom="1.5cm" margin-left="1.25cm" margin-right="1.25cm" >
                    <fo:region-body margin-top="1cm" margin-bottom="1cm" />
                    <fo:region-before extent="1cm"/>
                    <fo:region-after extent="1cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>  
        
            <!-- <xsl:call-template name="stampaBookmark"/> -->
                                
            <fo:page-sequence master-reference="PaginaA4">
                
                <fo:title>DISCIPLINARE TECNICO</fo:title>
                
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block text-align="end" font-size="8pt" padding-top="1mm">
                        <fo:page-number/>
                    </fo:block>  
                </fo:static-content>       

                <fo:flow flow-name="xsl-region-body" >
                    <fo:wrapper font-size="10pt" text-align="justify">
                        <xsl:apply-templates select="capitolo[@tipo='testata']" />
<!--                        <xsl:call-template name="stampaTOC"/> -->
                        <xsl:apply-templates select="capitolo[@tipo='introduzione']" />
<!--                        <xsl:apply-templates select="capitolo[@tipo='utentiSistema']" />  -->
<!--                        <xsl:apply-templates select="capitolo[@tipo='sistemiVersanti']" /> -->
<!--                        <xsl:apply-templates select="capitolo[@tipo='modalitaTrasmissione']" /> -->
                        <xsl:apply-templates select="capitolo[@tipo='verificheControlli']" />
                        <xsl:apply-templates select="capitolo[@tipo='gestioneSipRifiutati']" />
                        <xsl:apply-templates select="capitolo[@tipo='modalitaAnnullamentoVersamenti']" />
                        <xsl:apply-templates select="capitolo[@tipo='modalitaRestituzioneOggettiVersati']" />
<!--                        <xsl:apply-templates select="capitolo[@tipo='tipologieUD']" /> -->
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
                Versione del <xsl:value-of select="versione" />
            </fo:block>  
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <fo:block text-align="center" font-size="18pt" font-weight="normal" >
                Ente produttore: <fo:inline font-weight="bold"><xsl:value-of select="translate(nomeEnte, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" /></fo:inline>
            </fo:block>
            <xsl:call-template name="aCapo" />
            <fo:block text-align="left" font-size="11pt" font-weight="normal" font-style="italic" >
                Estremi dell’Accordo/Convenzione tra Ente e l’Istituto per i Beni Artistici, Culturali e Naturali della Regione Emilia-Romagna (IBACN) per lo svolgimento della funzione di conservazione dei documenti informatici: 
            </fo:block>  
            <xsl:call-template name="aCapo" />
            <fo:block text-align="left" font-size="11pt" font-weight="normal" font-style="italic" >
                Data di decorrenza: <xsl:value-of select="dataDecorrenza" />
            </fo:block>  
            <fo:block text-align="left" font-size="11pt" font-weight="normal" font-style="italic" >
                Data di fine validità: <xsl:value-of select="dataFineValidita" />
            </fo:block>  
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <fo:block text-align="center" font-size="14pt" font-weight="normal" >
                Nome dell’Ente versante configurato nel Sistema di conservazione:
            </fo:block>
            <fo:block text-align="center" font-size="14pt" font-weight="normal" >
                <xsl:value-of select="translate(nomeEnte, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')" />
            </fo:block>
            <xsl:call-template name="aCapo" />
            <fo:block text-align="center" font-size="14pt" font-weight="normal" >
                Nome della Struttura versante configurata nel Sistema di conservazione:
            </fo:block>
            <fo:block text-align="center" font-size="14pt" font-weight="normal" >
                <xsl:value-of select="nomeStruttura" />
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
            <fo:block  text-align="left" font-size="14pt" font-weight="bold">
                Scopo, ambito e struttura del documento
            </fo:block>  
            <xsl:call-template name="aCapo" />
            <fo:block font-weight="normal" text-align="justify">
                Il presente documento costituisce il Disciplinare Tecnico (d’ora in poi Disciplinare), cioè il documento che definisce le specifiche operative e le modalità di descrizione e di versamento nel Sistema di conservazione delle Tipologie di unità documentaria oggetto di conservazione, redatto in esecuzione di quanto indicato nell’Accordo o Convenzione, che regola nei suoi profili generali il rapporto tra l’Ente produttore e IBACN per lo svolgimento della funzione di conservazione dei documenti informatici affidati dall'Ente produttore a IBACN e più specificatamente al suo Servizio Polo archivistico regionale (d'ora in poi ParER) in base al  “Manuale di conservazione” (d’ora in poi Manuale) redatto da ParER.
<!--
                <xsl:value-of select="dataRegistrazioneAccordo" /> tra
                <fo:inline font-weight="bold">
                    <xsl:value-of select="nomeEnte" />
                </fo:inline>
-->                
            </fo:block>  
            <fo:block>
                Per lo svolgimento delle funzioni di conservazione dei documenti informatici, ParER ha sviluppato un proprio Sistema di conservazione (d’ora in poi Sistema), descritto nella sezione 8 del Manuale.
            </fo:block>  
            <fo:block>
                Le modalità generali di gestione delle funzioni di conservazione sono descritte nel Manuale a cui si rimanda anche per la terminologia utilizzate nel presente Disciplinare.
            </fo:block>  
            <fo:block>
                Il Disciplinare definisce l'articolazione in Strutture (corrispondenti normalmente alle Aree Organizzative Omogenee, ma non escludendo altre ripartizioni) con cui l'Ente produttore si rapporta con ParER per il versamento dei documenti.
            </fo:block>  
            <fo:block>
                Inoltre dettaglia tutti gli aspetti direttamente desumibili dalle configurazioni della struttura versante (d’ora in poi Struttura) nel Sistema e non espressamente trattati dal Manuale. In particolare descrive le specificità e le modalità di versamento dei documenti informatici proprie del Produttore.
            </fo:block>  
            <fo:block>
                Gli utenti espressamente autorizzati dal Produttore possono accedere al Sistema tramite credenziali personali rilasciate da ParER e comunicate al singolo utente. L’accesso al Sistema consente di consultare i documenti digitali versati nel Sistema e le configurazioni specifiche adottate.
            </fo:block>  
        </fo:block>  
    </xsl:template>
<!--
    <xsl:template match="capitolo[@tipo='utentiSistema']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block >In questo capitolo sono definiti i nominativi, i recapiti, il ruolo e il Profilo delle diverse persone dell’Ente produttore abilitate all’accesso al Sistema.</fo:block> 
            <fo:block >A livello di regola generale, a ciascun utente viene assegnato all’interno del Sistema un userID con la sintassi "nome.cognome".</fo:block>
            <fo:block >In questa sede, inoltre, sono elencati anche i Referenti ParER, ovvero le persone incaricate da ParER di seguire le attività di avvio dei servizi di conservazione, inclusi i test di versamento.</fo:block>
            <xsl:call-template name="aCapo" />
            
            <xsl:apply-templates select="capitolo[@tipo='profiliAccesso']" />
            <xsl:apply-templates select="capitolo[@tipo='utentiAbilitati']" />
        
        </fo:block>  
    </xsl:template>
-->
    <xsl:template match="capitolo[@tipo='profiliAccesso']" >
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block >A ciascun utente del Sistema è possibile assegnare uno o più ruoli di accesso (d’ora in poi Ruoli), ognuno dei quali definisce le modalità di accesso ai documenti e alle informazioni conservate nel Sistema e alle operazioni che può effettuare.</fo:block>
            <fo:block >Tra i Ruoli gestiti dal Sistema, quelli assegnabili agli utenti del Produttore sono i seguenti:</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                <fo:list-item >
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Responsabile: Accede in visualizzazione a tutte le funzionalità. Può versare pacchetti standard e non standard, consistenza serie e annullare i versamenti. Inoltre accede in visualizzazione alle informazioni sugli utenti;</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Supervisore: Consente l’accesso in visualizzazione a tutte le funzionalità del sistema, incluse quelle che consentono di monitorare e annullare i versamenti. Inoltre ha la possibilità di versare unità documentarie, serie e SIP non standard;</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Operatore: Consente l’accesso in visualizzazione a tutte le funzionalità, ad esclusione di quelle di amministrazione del sistema e di quelle che non possono essere filtrate in base al tipo di dato (es.: monitoraggio). Consente di versare manualmente con VERSO.</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </fo:list-block>
            <xsl:call-template name="aCapo" />

        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='utentiAbilitati']" >
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block >L’elenco degli utenti dell’Ente abilitati e dei Referenti ParER che seguono o hanno seguito l’attività di avvio del servizio di conservazione è riportato nella Tabella 1.</fo:block>
            <xsl:call-template name="aCapo" />
            <xsl:if test="tabella[@tipo='utentiAbilitati']/riga">
                <fo:table>
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
                            <fo:block>Abilitazioni</fo:block>
                        </fo:table-cell>
                    </fo:table-header>
                    <fo:table-body>
                        <xsl:for-each select="tabella[@tipo='utentiAbilitati']/riga">
                            <fo:table-row>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>
                                        <xsl:value-of select="nome"/><xsl:text>&#160;</xsl:text>
                                        <xsl:value-of select="cognome"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>
                                        <xsl:for-each select="ruoli/ruolo">
                                            <xsl:value-of select="."/>
                                            <xsl:if test="not(position()=last())"><xsl:text>,&#160;</xsl:text></xsl:if>
                                        </xsl:for-each>
                                    </fo:block>
                                </fo:table-cell>                        
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>
                                        <xsl:value-of select="recapiti"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr" width="40mm">
                                    <fo:block>
                                        <xsl:for-each select="abilitazioni">
                                            <xsl:choose>
                                                <xsl:when test="REGISTROs='tutti' and TIPO_UNITA_DOCs='tutti' and TIPO_DOCs='tutti'">
                                                    Tutti i tipi di dato
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:if test="REGISTROs">
                                                        Registri:<xsl:text>&#160;</xsl:text>
                                                        <xsl:choose>
                                                            <xsl:when test="REGISTROs/REGISTRO">
                                                                <xsl:for-each select="REGISTROs/REGISTRO">
                                                                    <xsl:value-of select="." />
                                                                    <xsl:if test="not(position()=last())">,<xsl:text>&#160;</xsl:text></xsl:if>
                                                                </xsl:for-each>
                                                            </xsl:when>
                                                            <xsl:otherwise>
                                                                <xsl:value-of select="REGISTROs"/>
                                                            </xsl:otherwise>
                                                        </xsl:choose>
                                                        <xsl:call-template name="aCapo"/>
                                                    </xsl:if>
                                                    <xsl:if test="TIPO_UNITA_DOCs">
                                                        Tipo unità documentarie:<xsl:text>&#160;</xsl:text>
                                                        <xsl:choose>
                                                            <xsl:when test="TIPO_UNITA_DOCs/TIPO_UNITA_DOC">
                                                                <xsl:for-each select="TIPO_UNITA_DOCs/TIPO_UNITA_DOC">
                                                                    <xsl:value-of select="." />
                                                                    <xsl:if test="not(position()=last())"><xsl:text>,&#160;</xsl:text></xsl:if>
                                                                </xsl:for-each>
                                                            </xsl:when>
                                                            <xsl:otherwise>
                                                                <xsl:value-of select="TIPO_UNITA_DOCs"/>
                                                            </xsl:otherwise>
                                                        </xsl:choose>
                                                        <xsl:call-template name="aCapo"/>
                                                    </xsl:if>
                                                    <xsl:if test="TIPO_DOCs">
                                                        Tipo documento principale:<xsl:text>&#160;</xsl:text>
                                                        <xsl:choose>
                                                            <xsl:when test="TIPO_DOCs/TIPO_DOC">
                                                                <xsl:for-each select="TIPO_DOCs/TIPO_DOC">
                                                                    <xsl:value-of select="." />
                                                                    <xsl:if test="not(position()=last())"><xsl:text>,&#160;</xsl:text></xsl:if>
                                                                </xsl:for-each>
                                                            </xsl:when>
                                                            <xsl:otherwise>
                                                                <xsl:value-of select="TIPO_DOCs"/>
                                                            </xsl:otherwise>
                                                        </xsl:choose>
                                                        <xsl:call-template name="aCapo"/>
                                                    </xsl:if>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:for-each>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </xsl:for-each>
                    </fo:table-body>
                </fo:table>            
            </xsl:if>
        </fo:block>
    </xsl:template>
<!--
    <xsl:template match="capitolo[@tipo='sistemiVersanti']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block >In questo capitolo sono elencati i sistemi versanti del Produttore con riferimento alle diverse tipologie documentarietipologie di unità documentaria.</fo:block>
            <fo:block >Per sistema versante si intende il sistema che cura, sotto forma di pacchetti di versamento (SIP), la trasmissione dei documenti al Sistema, direttamente o tramite sistemi intermedi, esterni al Produttore. Al sistema versante è associato l’utente versatore indicato nel SIP. In caso di utilizzo del client on line di versamento messo a disposizione da ParER o del servizio di versamento asincrono, la colonna Sistema versante sarà compilata rispettivamente con "SACER_VERSO" e  "SACER_PREINGEST".</fo:block>
            <fo:block >Nella Tabella 2 sono riportati, per ogni tipologia documentariatipologia di unità documentaria (ed eventuali estremi cronologici), i sistemi utilizzati dal Produttore per il versamento in conservazione.</fo:block>
            <fo:block >[TABELLA]</fo:block>
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='modalitaTrasmissione']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block >Le modalità di trasmissione del pacchetto di versamento sono:</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Servizi di versamento in modalità sincrona: Il versamento avviene utilizzando i Servizi di versamento in modalità sincrona descritti nel documento Specifiche tecniche dei servizi di versamento;</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Servizi di versamento in modalità asincrona: Il versamento avviene utilizzando i Servizi di versamento in modalità asincrona descritti nel documento Specifiche tecniche dei servizi di versamento;</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Client on line: Il versamento avviene manualmente, utilizzando il client di versamento on line messo a disposizione da ParER.</fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </fo:list-block>
            <xsl:call-template name="aCapo" />
            <fo:block >Nella Tabella 3, per ogni tipologia di unità documentaria (ed eventuali estremi cronologici), sono riportate le informazioni essenziali relative alle procedure di versamento. Nella colonna "Parametri di versamento" viene indicata la valorizzazione di default dei Parametri di versamento inclusa nell’Indice del SIP. Eventuali ulteriori dettagli sull’utilizzo dei parametri di versamento sono riportati nella descrizione delle singole tipologie di unità documentaria.</fo:block>
            
                <fo:block >[TABELLA]</fo:block>
            </fo:block>
        </xsl:template>
    -->
    <xsl:template match="capitolo[@tipo='verificheControlli']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>In questo capitolo è descritta l’impostazione “standard” dell’intera struttura in relazione ai controlli operati in fase di acquisizione del pacchetto di versamento (SIP) e ai parametri di accettazione nel caso in cui il controllo abbia avuto un esito negativo. L’impostazione “standard” prevede l’attivazione di tutti i controlli definiti nel Sistema e di tutti i parametri di accettazione. In base all’impostazione “standard” il Sistema accetta l’acquisizione dei SIP a fronte di un esito negativo del controllo solo se i parametri di forzatura definiti nell’Indice SIP sono attivi.</fo:block>
            <fo:block>A titolo di esempio si riporta la configurazione standard relativa al controllo sulla firma e l’esito del versamento in relazione al parametro di forzatura indicato nel SIP (cd.  “Forza Accettazione”):</fo:block>

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
                            <fo:block border-width="0mm" border-style="hidden">Controllo crittografico: SI</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">Accetta controllo crittografico negativo: SI</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">Forza accettazione: SI</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                </fo:list-block>
            </fo:block>
            <fo:block>In questo caso il SIP viene acquisito in conservazione con un esito di tipo WARNING sul controllo crittografico.</fo:block>
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
                            <fo:block border-width="0mm" border-style="hidden">Controllo crittografico: SI</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">Accetta controllo crittografico negativo: SI</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                    <fo:list-item>
                        <fo:list-item-label end-indent="label-end()">
                            <fo:block>
                                <fo:inline>&#8226;</fo:inline>
                            </fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block border-width="0mm" border-style="hidden">Forza accettazione: NO</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                </fo:list-block>
            </fo:block>
            <fo:block>In questo caso il SIP viene rifiutato e l’esito del versamento è NEGATIVO.</fo:block>
            <fo:block>Per informazioni di dettaglio sui singoli parametri vedere il documento Specifiche tecniche dei servizi di versamento.</fo:block>

            <xsl:apply-templates select="capitolo[@tipo='controlloFirme']" />
            <xsl:apply-templates select="capitolo[@tipo='controlloHash']" />
            <xsl:apply-templates select="capitolo[@tipo='controlloConformitaRegistro']" />
            <xsl:apply-templates select="capitolo[@tipo='controlloObblDatiProfiloUD']" />
            <xsl:apply-templates select="capitolo[@tipo='controlloFormati']" />
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='controlloFormati']" >
        <xsl:call-template name="aCapo" />
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>L’attivazione del parametro “Forza formato” comporta l’acquisizione del SIP in caso di controllo negativo indipendentemente dall’impostazione del parametro di accettazione del SIP medesimo. In base alla configurazione standard il parametro è attivabile su richiesta del Produttore.</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Abilita controllo formati (Il parametro indica se effettuare il controllo formati al versamento. Il controllo viene effettuato anche se il parametro è valorizzato false, ma gli esiti del controllo non influiscono sugli esiti del versamento): <xsl:value-of select="abilitaControlloFormato"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta controllo formato negativo: <xsl:value-of select="abilitaControlloFormatoNegativo"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Forza formato: <xsl:value-of select="forzaFormato"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </fo:list-block>
            <xsl:call-template name="aCapo" />
            <xsl:call-template name="aCapo" />
            <fo:block>Nella Tabella 3 sono elencati i formati configurati nella struttura. In base all’impostazione “standard” tutti i formati classificati come IDONEI e GESTITI ai fini della conservazione a lungo termini sono inclusi nella struttura mentre i formati classificati come DEPRECATI possono essere ammessi solo su richiesta del Produttore.</fo:block>
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
            <fo:block font-weight="bold">Tabella 3</fo:block>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="capitolo[@tipo='controlloFirme']" >
        <xsl:call-template name="aCapo" />
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>Il “SI” indica che il parametro di controllo o di accettazione è attivo secondo quanto previsto dall’impostazione “standard” mentre il “NO” indica che il parametro è disattivo. La disattivazione del parametro avviene su richiesta del Produttore.</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Controllo crittografico (Verifica che l’hash del documento firmato corrisponde all’hash sui cui è stata apposta la firma): <xsl:value-of select="controlloCrittografico"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Controllo catena trusted (Verifica che il certificatore che ha emesso il certificato di firma sia accreditato da AGID): <xsl:value-of select="controlloCatenaTrusted"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Controllo certificato (Verifica che la data di validità della firma, intesa come riferimento temporale indicato nell’Indice SIP, sia compresa nel periodo di validità del certificato di firma): <xsl:value-of select="controlloCertificato"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Controllo crl (Verifica l’eventuale revoca del certificato di firma): <xsl:value-of select="controlloCrl"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </fo:list-block>
            <xsl:call-template name="aCapo" />
            <fo:block>In relazione ai seguenti parametri si precisa che, in base alla configurazione “standard”, l’accettazione o il rifiuto del SIP dipende dall’impostazione del parametro di accettazione del SIP: Verifica che il certificato sia un certificato di firma</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta firma sconosciuta (Accetta l’acquisizione di SIP contenenti documenti la cui firma sia in un formato non determinabile): <xsl:value-of select="accettaFirmaSconosciuta"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta controllo catena trusted negativo (Accetta l’acquisizione di SIP contenenti documenti la cui firma sia emessa da un certificatore non accreditato): <xsl:value-of select="accettaCatenaTrustedNegativo"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta controllo certificato no cert (Verifica che il certificato sia un certificato di firma): <xsl:value-of select="accettaControlloCertificatoNoCert"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta controllo crl non scaricabile (Accetta l’acquisizione di SIP contenenti documenti firmati digitalmente il cui certificato di firma non sia verificabile al momento del versamento per l’impossibilità di interrogare le liste di revoca): <xsl:value-of select="accettaControlloCrlNonScaricabile"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta firma no delibera 45 (Accetta l’acquisizione di SIP contenenti documenti firmati digitalmente utilizzando un certificato il cui formato non sia fra quelli ammessi dalla deliberazione n.45/2009, emanata dal CNIPA): <xsl:value-of select="accettaFirmaNoDelibera45"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta controllo crittografico negativo (Accetta l’acquisizione di SIP contenenti documenti in cui non vi è corrispondenza tra firma e contenuto firmato): <xsl:value-of select="accettaControlloCrittograficoNegativo"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta controllo crl negativo (Accetta l’acquisizione di SIP contenenti documenti firmati digitalmente utilizzando un certificato di firma revocato o scaduto alla data del riferimento temporale indicato nell’Indice SIP): <xsl:value-of select="accettaControlloCrlNegativo"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta firma non conforme (Accetta l’acquisizione di SIP contenenti documenti firmati digitalmente utilizzando una firma la cui struttura non sia conforme con il suo formato): <xsl:value-of select="accettaFirmaNonConforme"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta controllo certificato scaduto (Accetta l’acquisizione di SIP contenenti documenti firmati digitalmente utilizzando una firma basata su un certificato scaduto alla data del riferimento temporale indicata nell’Indice SIP): <xsl:value-of select="accettaControlloCertificatoScaduto"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta controllo crl scaduta (Accetta l’acquisizione di SIP nel caso in cui la CRL scaricata non sia scaduta): <xsl:value-of select="accettaControlloCrlScaduto"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta marca sconosciuta: <xsl:value-of select="accettaMarcaSconosciuta"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta controllo certificato non valido (Accetta l’acquisizione di SIP contenenti documenti firmati digitalmente utilizzando un certificato di firma non ancora valido alla data del riferimento temporale indicato nell’Indice SIP): <xsl:value-of select="accettaControlloCertificatoNonValido"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta controllo crl non valida (Accetta l’acquisizione di SIP nel caso in cui la CRL scaricata non sia valida ad esempio per mancanza della data di scadenza): <xsl:value-of select="accettaControlloCrlNonValida"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </fo:list-block>
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='controlloHash']" >
        <xsl:call-template name="aCapo" />
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>Il “SI” indica che il parametro di controllo o di accettazione è attivo secondo quanto previsto dall’impostazione “standard” mentre il “NO” indica che il parametro è disattivo. La disattivazione del parametro avviene su richiesta del Produttore. L’attivazione del parametro “Forza hash” comporta l’acuisizione del SIP in caso di controllo negativo indipendentemente dall’impostazione del parametro di accettazione del SIP medesimo. In base alla configurazione standard il parametro è attivabile su richiesta del Produttore.</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Abilita controllo hash (Verifica la corrispondenza tra hash dichiarato nel SIP e hash calcolato dal Sistema): <xsl:value-of select="abilitaControlloHash"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta controllo hash negativo (Accetta l’acquisizione di SIP in cui non vi sia corrispondenza tra hash dichiarato e hash calcolato dal Sistema. In caso di esito negativo l’accettazione è legata all’impostazione del parametro successivo): <xsl:value-of select="accettaControlloHashNegativo"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Forza hash (Il parametro regola l’accettazione del SIP in caso di esito negativo del controllo sull’hash): <xsl:value-of select="forzaHash"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </fo:list-block>
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='controlloConformitaRegistro']" >
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>Il “SI” indica che il parametro di controllo o di accettazione è attivo secondo quanto previsto dall’impostazione “standard” mentre il “NO” indica che il parametro è disattivo. La disattivazione del parametro avviene su richiesta del Produttore.</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Abilita controllo formato numero (Verifica la conformità del formato Numero della chiave dell’unità documentaria): <xsl:value-of select="abilitaControlloFormatoNumero"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Accetta controllo formato numero negativo (Accetta l’acquisizione di SIP in cui il formato numero non sia conforme con la configurazione del registro. In caso di esito negativo l’accettazione è legata all’impostazione del parametro successivo): <xsl:value-of select="accettaControlloFormatoNumeroNegativo"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Forza formato numero (Il parametro regola l’accettazione del SIP in caso di esito negativo del controllo sul formato Numero della chiave dell’unità documentaria. In base alla configurazione standard il parametro è attivabile su richiesta del Produttore): <xsl:value-of select="forzaFormatoNumero"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </fo:list-block>
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='controlloObblDatiProfiloUD']" >
        <fo:block id="{generate-id(.)}">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>Il “SI” indica che il parametro di controllo o di accettazione è attivo secondo quanto previsto dall’impostazione “standard” mentre il “NO” indica che il parametro è disattivo. La disattivazione del parametro avviene su richiesta del Produttore.</fo:block>
            <xsl:call-template name="aCapo" />
            <fo:list-block xsl:use-attribute-sets="bloccoElencoPuntato">
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Oggetto (L’attivazione del controllo implica l’obbligatorietà nel SIP dell’informazione relativa dell’oggetto del documento): <xsl:value-of select="obbligatorietaOggetto"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <fo:inline>&#8226;</fo:inline>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block border-width="0mm" border-style="hidden">Data (L’attivazione del controllo implica l’obbligatorietà nel SIP dell’informazione relativa alla data del documento): <xsl:value-of select="obbligatorietaData"/></fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </fo:list-block>
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
            <fo:block>Per ulteriori informazioni in merito alle modalità di rifiuto o accettazione dei SIP trasmessi, si rinvia al Manuale di conservazione e al documento Specifiche tecniche dei servizi di versamento.</fo:block>
        </fo:block>
    </xsl:template>


    <xsl:template match="capitolo[@tipo='modalitaAnnullamentoVersamenti']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>Nel caso in cui un versamento andato a buon fine sia stato effettuato per errore o contenga degli errori non correggibili altrimenti, il Produttore provvede ad annullarlo utilizzando apposite funzionalità del Sistema oppure inviando al personale di ParER una richiesta formale completa degli estremi dei documenti da annullare e relativa motivazione.</fo:block>
            <fo:block>Gli oggetti non sono cancellati dal Sistema ma marcati come Annullati e sono comunque sempre consultabili tramite l’apposita sezione di ricerca.</fo:block>
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='modalitaRestituzioneOggettiVersati']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block>La Convenzione prevede che, in caso di recesso o a scadenza di contratto, l’IBACN, tramite il ParER, è tenuto a riversare i Documenti informatici e le Aggregazioni documentali informatiche conservate, i metadati a essi associati e le evidenze informatiche generate nel corso del processo di conservazione nel sistema indicato dal Produttore, secondo modalità e tempi indicati nel Disciplinare Tecnico.</fo:block>
            <fo:block>ParER garantisce comunque il mantenimento nel proprio Sistema di conservazione dei Documenti informatici e delle Aggregazioni documentali informatiche conservati, con i metadati a essi associati e le evidenze informatiche generate nel corso del processo di conservazione fino alla comunicazione da parte del Produttore dell’effettiva messa a disposizione del Sistema di conservazione in cui effettuare il riversamento.</fo:block>
            <fo:block>ParER provvederà all’eliminazione dal proprio Sistema di conservazione di tutti gli oggetti riversati e di tutti gli elementi riferiti al Produttore solo al termine del riversamento e solo dopo le opportune verifiche - effettuate da entrambe le Parti e svolte di concerto tra le stesse – di corretto svolgimento del riversamento stesso. In tal caso viene garantita la completa cancellazione e non leggibilità dei dati. L’intera operazione dovrà comunque avvenire con l’autorizzazione e la vigilanza delle competenti autorità, in particolare delle strutture del MIBACT.</fo:block>
            <fo:block>In caso di chiusura del servizio da parte della Regione Emilia-Romagna, con interventi di modifica alla normativa regionale, si provvederà a trasferire quanto conservato al Sistema di conservazione individuato per proseguire le attività svolte da IBACN. Per quanto riguarda gli aspetti operativi per il trasferimento di archivi ad altri sistemi di conservazione, ParER adotta lo standard Uni Sincro, e provvederà a trasferire secondo canali sicuri concordati con il Produttore o con il nuovo Conservatore le informazioni. Analogamente il Sistema è predisposto per la ricezione di archivi in formato Uni Sincro; qualora il precedente non sia in grado di produrre l’archivio in formato Uni Sincro, ParER, a seguito di specifici accordi, può mettere a disposizione del Produttore consulenza e strumenti per facilitare il trasferimento dell’archivio.</fo:block>
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='tipologieUD']" >
        <fo:block id="{generate-id(.)}" page-break-before="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <fo:block >In questo capitolo sono descritte le tipologie di unità documentaria oggetto di versamento in termini di struttura dell’unità documentaria e relativi metadati. Inoltre sono dettagliati particolari casi d’uso che il Produttore può eventualmente gestire nel Sistema come annullamenti, aggiunte di documenti a unità documentarie già presenti nel sistema.</fo:block>
            <xsl:call-template name="aCapo" />
            <xsl:apply-templates select="capitolo[@tipo='unitaDocumentaria']" />
        </fo:block>
    </xsl:template>

    <xsl:template match="capitolo[@tipo='unitaDocumentaria']" >
        <fo:block id="{generate-id(.)}" page-break-after="always">
            <xsl:call-template name="stampaCapitolo">
                <xsl:with-param name="cap" select="."/>
            </xsl:call-template>
            <xsl:call-template name="aCapo" />
            <xsl:variable name="capDescrizioneUD" select="capitolo[@tipo='descrizioneUD']" />
            <xsl:if test="$capDescrizioneUD">
                <fo:block id="{generate-id($capDescrizioneUD)}" font-weight="bold">
                    <xsl:call-template name="stampaCapitoloInterno">
                        <xsl:with-param name="cap" select="$capDescrizioneUD"/>
                    </xsl:call-template>
                    <fo:block font-weight="normal">
                        <xsl:value-of select="descrizioneUD" />
                    </fo:block>
                    <xsl:call-template name="aCapo" />
                </fo:block>
            </xsl:if>
            <xsl:variable name="capStrutturaUD" select="capitolo[@tipo='strutturaUD']" />
            <xsl:if test="$capStrutturaUD">
                <fo:block id="{generate-id($capStrutturaUD)}" font-weight="bold">
                    <xsl:call-template name="stampaCapitoloInterno">
                        <xsl:with-param name="cap" select="$capStrutturaUD"/>
                    </xsl:call-template>
                    <fo:block font-weight="normal">Nella tabella denominata "Struttura unità documentaria <xsl:value-of select="$capStrutturaUD/tabella[@tipo='struttureUD']/@nome" />" è rappresentata la struttura dell’unità documentaria ovvero la sua articolazione in documento principale ed eventuali allegati, annessi e annotazioni.</fo:block>
                    <xsl:call-template name="aCapo" />
                    <xsl:if test="$capStrutturaUD/tabella[@tipo='strutturaUD']/riga">
                        <fo:table>
                            <fo:table-column column-width="25%"/>
                            <fo:table-column column-width="30%"/>                        
                            <fo:table-column column-width="30%"/>
                            <fo:table-column column-width="15%"/>                        
                            <fo:table-header font-weight="bold">
                                <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                    <fo:block>Elemento dell'UD</fo:block>
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
                            </fo:table-header>
                            <fo:table-body>
                                <xsl:for-each select="$capStrutturaUD/tabella[@tipo='strutturaUD']/riga">
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
                                    </fo:table-row>
                                </xsl:for-each>
                            </fo:table-body>
                        </fo:table>  
                        <xsl:call-template name="aCapo" />
                    </xsl:if>
                </fo:block>
            </xsl:if>
            <xsl:variable name="capRegistriAmmessi" select="capitolo[@tipo='registriAmmessi']" />
            <xsl:if test="$capRegistriAmmessi">
                <fo:block id="{generate-id($capRegistriAmmessi)}" font-weight="bold">
                    <xsl:call-template name="stampaCapitoloInterno">
                        <xsl:with-param name="cap" select="$capRegistriAmmessi"/>
                    </xsl:call-template>
                    <fo:block font-weight="normal">L’elenco dei registri associati all’unità documentaria è riportato nella Tabella denominata "<xsl:value-of select="$capRegistriAmmessi/tabella[@tipo='registriAmmessi']/@nome" />".</fo:block>    
                    <xsl:call-template name="aCapo" />
                    <xsl:if test="$capRegistriAmmessi/tabella[@tipo='registriAmmessi']/riga">
                        <fo:table width="100%">
                            <fo:table-column column-width="16%"/>
                            <fo:table-column column-width="26%"/>                        
                            <fo:table-column column-width="20%"/>
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
                                                    dal <xsl:value-of select="dal" />
                                                    <xsl:if test="not(al)">
                                                        al <xsl:value-of select="al" />    
                                                    </xsl:if>
                                                    <xsl:if test="not(position()=last())">, 
                                                    </xsl:if>
                                                    <xsl:if test="not(position()=last())">, 
                                                        <xsl:call-template name="aCapo" />
                                                    </xsl:if>
                                                </xsl:for-each>
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
                                                <xsl:value-of select="dataDisattivazione"/>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </xsl:for-each>
                            </fo:table-body>
                        </fo:table>  
                        <xsl:call-template name="aCapo" />
                    </xsl:if>

                </fo:block>
            </xsl:if>
            <xsl:variable name="capMetadatiProfilo" select="capitolo[@tipo='metadatiProfilo']" />
            <xsl:if test="$capMetadatiProfilo">
                <fo:block id="{generate-id($capMetadatiProfilo)}" font-weight="bold">
                    <xsl:call-template name="stampaCapitoloInterno">
                        <xsl:with-param name="cap" select="$capMetadatiProfilo"/>
                    </xsl:call-template>
                    <fo:block font-weight="normal">Nella tabella denominata "Metadati di identificazione <xsl:value-of select="$capMetadatiProfilo/tabella[@tipo='metadatiProfilo']/@nome" />" (NOTA: ATTUALMENTE NON DISPONIBILE) sono presentati i metadati di identificazione e di profilo generale dell’unità documentaria.</fo:block>    
                    <xsl:call-template name="aCapo" />
                    <xsl:if test="$capMetadatiProfilo/tabella[@tipo='metadatiProfilo']/riga">
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
                                <xsl:for-each select="$capMetadatiProfilo/tabella[@tipo='metadatiProfilo']/riga">
                                    <fo:table-row>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block>
                                                <xsl:value-of select="denominazione"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">
                                                <xsl:value-of select="descrizione"/>
                                            </fo:block>
                                        </fo:table-cell>                        
                                    </fo:table-row>
                                </xsl:for-each>
                            </fo:table-body>
                        </fo:table>    
                        <xsl:call-template name="aCapo" />
                    </xsl:if>
                </fo:block>
            </xsl:if>
            <xsl:variable name="capMetadatiSpecificiUD" select="capitolo[@tipo='metadatiSpecificiUD']" />
            <xsl:if test="$capMetadatiSpecificiUD">
                <fo:block id="{generate-id($capMetadatiSpecificiUD)}" font-weight="bold">
                    <xsl:call-template name="stampaCapitoloInterno">
                        <xsl:with-param name="cap" select="$capMetadatiSpecificiUD"/>
                    </xsl:call-template>
                    <fo:block font-weight="normal">Nelle tabelle denominate "<xsl:value-of select="$capMetadatiSpecificiUD/tabella[@tipo='metadatiSpecificiUD']/@nome" />" sono presentate le varie versioni dei metadati specifici associate all’unità documentaria.</fo:block>    
                    <xsl:call-template name="aCapo" />
                    <xsl:for-each select="$capMetadatiSpecificiUD/tabella[@tipo='metadatiSpecificiUD']" >
                        <fo:block font-weight="normal">Versione metadati specifici: <xsl:value-of select="versione"/></fo:block>
                        <fo:block font-weight="normal">Descrizione versione: <xsl:if test="not(descrizione='null')">
                                <xsl:value-of select="descrizione"/>
                            </xsl:if>
                        </fo:block>
                        <fo:block font-weight="normal">Data inizio validità: <xsl:value-of select="dataInizioValidita"/></fo:block>
                        <fo:block font-weight="normal">Data fine validità: 
                            <xsl:if test="not(dataFineValidita='31/12/2444')">
                                <xsl:value-of select="dataFineValidita"/>
                            </xsl:if>
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
                                            <fo:block>
                                                <xsl:value-of select="denominazione"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                            <fo:block font-weight="normal">
                                                <xsl:value-of select="descrizione"/>
                                            </fo:block>
                                        </fo:table-cell>                        
                                    </fo:table-row>
                                </xsl:for-each>
                            </fo:table-body>
                        </fo:table> 
                        <xsl:call-template name="aCapo" />
                    </xsl:for-each>
                </fo:block>
            </xsl:if>
            <xsl:variable name="capTipiDocumento" select="capitolo[@tipo='tipoDocumentoUD']" />
            <xsl:call-template name="aCapo" />
            <xsl:if test="$capTipiDocumento">
                <xsl:for-each select="$capTipiDocumento">
                    <fo:block id="{generate-id(.)}" font-weight="bold" >
                        <xsl:call-template name="stampaCapitolo">
                            <xsl:with-param name="cap" select="."/>
                        </xsl:call-template>
                        <xsl:call-template name="aCapo" />
                        <xsl:variable name="capMetadatiProfiloTipoDoc" select="capitolo[@tipo='metadatiProfiloTipoDoc']" />
                        <xsl:if test="$capMetadatiProfiloTipoDoc">
                            <fo:block id="{generate-id($capMetadatiProfiloTipoDoc)}" font-weight="bold"> 
                                <xsl:call-template name="stampaCapitoloInterno">
                                    <xsl:with-param name="cap" select="$capMetadatiProfiloTipoDoc"/>
                                </xsl:call-template>
                                <fo:block font-weight="normal">"Metadati di profilo del documento" contiene la descrizione dei criteri per la valorizzazione dei metadati di profilo per ciascun Documento, qualora definiti.</fo:block>
                                <xsl:call-template name="aCapo" />
                                <xsl:if test="$capMetadatiProfiloTipoDoc/tabella[@tipo='metadatiProfiloTipoDoc']/riga">
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
                                            <xsl:for-each select="$capMetadatiProfiloTipoDoc/tabella[@tipo='metadatiProfiloTipoDoc']/riga">
                                                <fo:table-row>
                                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                        <fo:block>
                                                            <xsl:value-of select="denominazione"/>
                                                        </fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                        <fo:block font-weight="normal">
                                                            <xsl:value-of select="descrizione"/>
                                                        </fo:block>
                                                    </fo:table-cell>                        
                                                </fo:table-row>
                                            </xsl:for-each>
                                        </fo:table-body>
                                    </fo:table>            
                                    <xsl:call-template name="aCapo" />
                                </xsl:if>
                        
                            </fo:block>
                        </xsl:if>
                        <xsl:variable name="capMetadatiSpecificiTipoDoc" select="capitolo[@tipo='metadatiSpecificiTipoDoc']" />
                        <xsl:if test="$capMetadatiSpecificiTipoDoc">
                            <fo:block id="{generate-id($capMetadatiSpecificiTipoDoc)}" font-weight="bold"> 
                                <xsl:call-template name="stampaCapitoloInterno">
                                    <xsl:with-param name="cap" select="$capMetadatiSpecificiTipoDoc"/>
                                </xsl:call-template>
                                <fo:block font-weight="normal">Nelle tabelle "Metadati specifici del documento" sono presentati le varie versioni di metadati specifici associati al tipo documento.</fo:block>
                                <xsl:call-template name="aCapo" />
                                <xsl:for-each select="$capMetadatiSpecificiTipoDoc/tabella[@tipo='metadatiSpecificiTipoDoc']" >
                                    <fo:block font-weight="normal">Versione metadati specifici: <xsl:value-of select="versione"/></fo:block>
                                    <fo:block font-weight="normal">Descrizione versione: <xsl:if test="not(descrizione='null')">
                                            <xsl:value-of select="descrizione"/>
                                        </xsl:if>
                                    </fo:block>
                                    <fo:block font-weight="normal">Data inizio validità: <xsl:value-of select="dataInizioValidita"/></fo:block>
                                    <fo:block font-weight="normal">Data fine validità: 
                                        <xsl:if test="not(dataFineValidita='31/12/2444')">
                                            <xsl:value-of select="dataFineValidita"/>
                                        </xsl:if>
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
                                                        <fo:block>
                                                            <xsl:value-of select="denominazione"/>
                                                        </fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell xsl:use-attribute-sets="table.cell.attr">
                                                        <fo:block font-weight="normal">
                                                            <xsl:value-of select="descrizione"/>
                                                        </fo:block>
                                                    </fo:table-cell>                        
                                                </fo:table-row>
                                            </xsl:for-each>
                                        </fo:table-body>
                                    </fo:table>  
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
    <!-- VECCHIO
        <xsl:template name="stampaCapitolo">
            <xsl:param name="cap" />
            <xsl:variable name="conteggio" select="count($cap/ancestor-or-self::*[name()='capitolo'])" />
            <fo:block font-size="12pt" font-weight="bold" margin-left="{($conteggio*0.5)}cm">
                <fo:inline font-weight="bold">
                    <xsl:for-each select="$cap/ancestor-or-self::*[name()='capitolo']">
                        <xsl:value-of select="concat(count(preceding-sibling::*[@numerato='si' and not(@capitoloInterno) ])+1,'.')"/>
                    </xsl:for-each>&#160;
                    <xsl:value-of select="$cap/@nome" />
                </fo:inline>
            </fo:block>  
        </xsl:template>
    -->

    <xsl:template name="stampaCapitolo">
        <xsl:param name="cap" />
        <fo:block font-size="12pt" font-weight="bold" >
            <fo:inline font-weight="bold">
                <xsl:value-of select="$cap/@nome" />
            </fo:inline>
        </fo:block>  
    </xsl:template>

    <xsl:template name="stampaCapitoloInterno">
        <xsl:param name="cap" />
        <fo:block font-weight="bold" >
            <fo:inline font-weight="bold">
                <xsl:value-of select="concat(count($cap/preceding-sibling::*[@capitoloInterno='si'])+1,'.')"/>&#160;
                <xsl:value-of select="$cap/@nome" />
            </fo:inline>
        </fo:block>  
    </xsl:template>

    <xsl:template name="stampaTOC">
        <fo:block page-break-before="always">
            <xsl:for-each select="//capitolo">
                <xsl:element name="fo:block">
                    <xsl:choose>
                        <xsl:when test="@livello='1'">
                            <xsl:attribute name="font-weight">bold</xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="font-weight">normal</xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:attribute name="text-align-last">
                        justify
                    </xsl:attribute>
                    <xsl:choose>
                        <xsl:when test="@capitoloInterno">
                            &#160;&#160;&#160;
                            <xsl:value-of select="concat(count(preceding-sibling::*[@capitoloInterno='si'])+1,'.')"/>&#160;
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:for-each select="ancestor-or-self::*[name()='capitolo']">
                                <xsl:if test="@numerato='si'">
                                    <xsl:value-of select="concat(count(preceding-sibling::*[@numerato='si' and not(@capitoloInterno) ])+1,'.')"/>
                                </xsl:if>
                            </xsl:for-each>&#160;
                        </xsl:otherwise>
                    </xsl:choose>
                    <fo:basic-link internal-destination="{generate-id(.)}">
                        <xsl:value-of select="@nome"/>
                        <fo:leader leader-pattern="dots" />
                        <fo:page-number-citation ref-id="{generate-id(.)}" />
                    </fo:basic-link>
                </xsl:element>
            </xsl:for-each>
        </fo:block>
    </xsl:template>

    <xsl:template name="stampaBookmark">
        <fo:bookmark-tree>
            <fo:bookmark internal-destination="s-1" starting-state="show">
                <fo:bookmark-title>Tabella dei contenuti</fo:bookmark-title>
                <xsl:call-template name="stampaBookmarkRamo">
                    <xsl:with-param name="capitoli" select="capitolo"/>
                </xsl:call-template>
            </fo:bookmark>
        </fo:bookmark-tree>            
    </xsl:template>

    <xsl:template name="stampaBookmarkRamo">
        <xsl:param name="capitoli" />
        <xsl:for-each select="$capitoli">
            <xsl:element name="fo:bookmark">
                <xsl:attribute name="internal-destination">
                    <xsl:value-of select="generate-id(.)"/>
                </xsl:attribute>
                <xsl:element name="fo:bookmark-title">
                    <xsl:value-of select="@nome"/>
                </xsl:element>
                <xsl:call-template name="stampaBookmarkRamo">
                    <xsl:with-param name="capitoli" select="capitolo"/>
                </xsl:call-template>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
        
</xsl:stylesheet>
