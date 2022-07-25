SELECT  XMLELEMENT("fotoOggetto",
          XMLELEMENT("versioneLogRecord",'1.0'),              
          XMLELEMENT("recordMaster",
            XMLELEMENT("tipoRecord",'Disciplinare tecnico'),
            XMLELEMENT("idRecord",'1'),
            XMLELEMENT("keyRecord",
                XMLELEMENT("datoKey",
                    XMLELEMENT("colonnaKey",'data_generazione'),
                    XMLELEMENT("labelKey",'Data generazione disciplinare tecnico'),
                    XMLELEMENT("valoreKey",TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS') )
                ),
                XMLELEMENT("datoKey",
                    XMLELEMENT("colonnaKey",'id_strut'),
                    XMLELEMENT("labelKey",'Id struttura'),
                    XMLELEMENT("valoreKey",':ID_OGGETTO')
                )
                
            )
          ), -- Fine recordMaster
          STRUTTURA,
                -- ----------------------------------------------
                -- FOTO DELL'ENTE CONVENZIONATO COME RECORD CHILD
                -- ----------------------------------------------
                (SELECT  XMLQUERY('for $a in /fotoOggetto
                                                          let $recordMaster := $a/recordMaster
                                                          let $tuttiChild := $a/recordChild
                                                          return  <recordChild>
                                                                    {$recordMaster/tipoRecord}
                                                                    <child>
                                                                      {$recordMaster/idRecord}
                                                                      {$recordMaster/keyRecord}
                                                                      {$recordMaster/datoRecord}
                                                                      {$tuttiChild}
                                                                    </child>
                                                                  </recordChild>' 
                                    PASSING FOTO RETURNING CONTENT) AS ENTE_CONVENZIONATO
                                    FROM
                            
                (
                    SELECT XMLELEMENT("fotoOggetto", 
                                    XMLELEMENT("versioneLogRecord",'1.0'), 
                                    XMLELEMENT
                                            ("recordMaster", 
                                                    XMLELEMENT("tipoRecord",'Ente convenzionato'), 
                                                    XMLELEMENT("idRecord",ente.id_ente_convenz), 

                                                    -- inizio keyRecord
                                                    XMLELEMENT
                                                            ("keyRecord", 
                                                                    XMLELEMENT
                                                                            ("datoKey", 
                                                                                    XMLELEMENT("colonnaKey",'nm_ente_convenz'),
                                                                                    XMLELEMENT("labelKey",'Denominazione'),
                                                                                    XMLELEMENT("valoreKey",ente.nm_ente_convenz) 
                                                                            ) 
                                                            ), 
                                                    -- fine keyRecord

                                                    -- inizio dati record master
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'dt_ini_val'), 
                                                                    XMLELEMENT("labelDato",'Data inizio validità'), 
                                                                    XMLELEMENT("valoreDato", to_char(ente.dt_ini_val, 'dd/mm/yyyy') ) 
                                                            ),
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'ds_via_sede_legale'), 
                                                                    XMLELEMENT("labelDato",'Via sede legale'), 
                                                                    XMLELEMENT("valoreDato", nvl(ente.ds_via_sede_legale, 'null') ) 
                                                            ),
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'cd_cap_sede_legale'), 
                                                                    XMLELEMENT("labelDato",'CAP sede legale'), 
                                                                    XMLELEMENT("valoreDato", nvl(ente.cd_cap_sede_legale, 'null') ) 
                                                            ),
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'ds_citta_sede_legale'), 
                                                                    XMLELEMENT("labelDato",'Città sede legale'), 
                                                                    XMLELEMENT("valoreDato", nvl(ente.ds_citta_sede_legale, 'null') ) 
                                                            ),
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'cd_prov_sede_legale'), 
                                                                    XMLELEMENT("labelDato",'Provincia sede legale'), 
                                                                    XMLELEMENT("valoreDato", nvl(prov.cd_ambito_territ, 'null') ) 
                                                            ),
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'cd_nazione_sede_legale'), 
                                                                    XMLELEMENT("labelDato",'Paese sede legale'), 
                                                                    XMLELEMENT("valoreDato", nvl(ente.cd_nazione_sede_legale, 'null') ) 
                                                            ),
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'cd_ente_convenz'), 
                                                                    XMLELEMENT("labelDato",'Codice ente'), 
                                                                    XMLELEMENT("valoreDato", ente.cd_ente_convenz) 
                                                            ),
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'ti_cd_ente_convenz'), 
                                                                    XMLELEMENT("labelDato",'Tipo codice'), 
                                                                    XMLELEMENT("valoreDato", ente.ti_cd_ente_convenz) 
                                                            ),
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'cd_fisc'), 
                                                                    XMLELEMENT("labelDato",'Codice fiscale'), 
                                                                    XMLELEMENT("valoreDato", nvl(ente.cd_fisc, 'null') ) 
                                                            ),
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'cd_cliente_sap'), 
                                                                    XMLELEMENT("labelDato",'Codice debitore SAP'), 
                                                                    XMLELEMENT("valoreDato", nvl(ente.cd_cliente_sap, 'null') ) 
                                                            ),
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'cd_categ_ente'), 
                                                                    XMLELEMENT("labelDato",'Categoria'), 
                                                                    XMLELEMENT("valoreDato", cat.cd_categ_ente ) 
                                                            ), 
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'cd_ambito_territ'), 
                                                                    XMLELEMENT("labelDato",'Ambito territoriale'), 
                                                                    XMLELEMENT("valoreDato", territ.cd_ambito_territ ) 
                                                            ),
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'fl_ente_regione'), 
                                                                    XMLELEMENT("labelDato",'In regione'), 
                                                                    XMLELEMENT("valoreDato", decode(ente.fl_ente_regione, '1', 'true', '0', 'false') ) 
                                                            ),
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'nm_ente_convenz_nuovo'), 
                                                                    XMLELEMENT("labelDato",'Denominazione nuovo ente'), 
                                                                    XMLELEMENT("valoreDato", nvl(ente_new.nm_ente_convenz, 'null') ) 
                                                            ),
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'nm_ambiente_ente_convenz'), 
                                                                    XMLELEMENT("labelDato",'Ambiente di appartenenza'), 
                                                                    XMLELEMENT("valoreDato",nvl(ambiente_ente_conv.nm_ambiente_ente_convenz, 'null') ) 
                                                            ),					
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'dt_atto_last_accordo'), 
                                                                    XMLELEMENT("labelDato",'Data atto di approvazione'), 
                                                                    XMLELEMENT("valoreDato", to_char(ente.dt_atto_last_accordo, 'dd/mm/yyyy') ) 
                                                            ),
                                                    XMLELEMENT
                                                            ("datoRecord", 
                                                                    XMLELEMENT("colonnaDato",'ds_atto_last_accordo'), 
                                                                    XMLELEMENT("labelDato",'Atto approvazione accordo'), 
                                                                    XMLELEMENT("valoreDato", ente.ds_atto_last_accordo ) 
                                                            )
                                                    -- Fine dati record Master	
                                            ),
                                            -- Fine record Master

                                            -- inizio Anagrafiche
                                            XMLELEMENT
                                            ("recordChild", 
                                                    XMLELEMENT("tipoRecord", 'Anagrafiche'),

                                                    (SELECT XMLAGG
                                                                    (XMLELEMENT
                                                                            ("child", 
                                                                                    XMLELEMENT("idRecord", ente_sto.id_sto_ente_convenz), 
                                                                                    XMLELEMENT
                                                                                            ("keyRecord", 
                                        XMLELEMENT
                                          ("datoKey", 
                                            XMLELEMENT("colonnaKey",'dt_ini_val'), 
                                            XMLELEMENT("labelKey",'Data inizio validità'),
                                            XMLELEMENT("valoreKey", to_char(ente_sto.dt_ini_val, 'dd/mm/yyyy') )
                                          )
                                                                                            ), 
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'dt_fine_val'), 
                                          XMLELEMENT("labelDato",'Data fine validità'), 
                                          XMLELEMENT("valoreDato", to_char(ente_sto.dt_fine_val, 'dd/mm/yyyy') ) 
                                        ),
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'nm_ente_convenz'), 
                                          XMLELEMENT("labelDato",'Nome ente convenzionato'), 
                                          XMLELEMENT("valoreDato", ente_sto.nm_ente_convenz ) 
                                        ),
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'cd_fisc'), 
                                          XMLELEMENT("labelDato",'Codice fiscale'), 
                                          XMLELEMENT("valoreDato", nvl(ente_sto.cd_fisc, 'null') ) 
                                        ),
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'cd_ente_convenz'), 
                                          XMLELEMENT("labelDato",'Codice ente'), 
                                          XMLELEMENT("valoreDato", ente_sto.cd_ente_convenz) 
                                        ),
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'ds_via_sede_legale'), 
                                          XMLELEMENT("labelDato",'Via sede legale'), 
                                          XMLELEMENT("valoreDato", nvl(ente_sto.ds_via_sede_legale, 'null') ) 
                                        ),
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'ds_citta_sede_legale'), 
                                          XMLELEMENT("labelDato",'Città sede legale'), 
                                          XMLELEMENT("valoreDato", nvl(ente_sto.ds_citta_sede_legale, 'null') ) 
                                        ),
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'cd_ambito_territ'), 
                                          XMLELEMENT("labelDato",'Ambito territoriale'), 
                                          XMLELEMENT("valoreDato", territ_sto.cd_ambito_territ ) 
                                        ),
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'cd_categ_ente'), 
                                          XMLELEMENT("labelDato",'Categoria'), 
                                          XMLELEMENT("valoreDato", cat_sto.cd_categ_ente ) 
                                        ), 
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'fl_ente_regione'), 
                                          XMLELEMENT("labelDato",'In regione'), 
                                          XMLELEMENT("valoreDato", decode(ente_sto.fl_ente_regione, '1', 'true', '0', 'false') ) 
                                        ),
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'cd_cliente_sap'), 
                                          XMLELEMENT("labelDato",'Codice debitore SAP'), 
                                          XMLELEMENT("valoreDato", nvl(ente_sto.cd_cliente_sap, 'null') ) 
                                        ),
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'dl_note'), 
                                          XMLELEMENT("labelDato",'Note'), 
                                          XMLELEMENT("valoreDato", nvl(ente_sto.dl_note, 'null') ) 
                                        ),
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'cd_prov_sede_legale'), 
                                          XMLELEMENT("labelDato",'Provincia sede legale'), 
                                          XMLELEMENT("valoreDato", nvl(prov_sto.cd_ambito_territ, 'null') ) 
                                        ),
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'cd_nazione_sede_legale'), 
                                          XMLELEMENT("labelDato",'Paese sede legale'), 
                                          XMLELEMENT("valoreDato", nvl(ente_sto.cd_nazione_sede_legale, 'null') ) 
                                        ),
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'ti_cd_ente_convenz'), 
                                          XMLELEMENT("labelDato",'Tipo codice'), 
                                          XMLELEMENT("valoreDato", ente_sto.ti_cd_ente_convenz) 
                                        ),
                                      XMLELEMENT
                                        ("datoRecord", 
                                          XMLELEMENT("colonnaDato",'cd_cap_sede_legale'), 
                                          XMLELEMENT("labelDato",'CAP sede legale'), 
                                          XMLELEMENT("valoreDato", nvl(ente_sto.cd_cap_sede_legale, 'null') ) 
                                        )
                                                                            )
                                                                    ORDER BY ente_sto.dt_ini_val DESC
                                                                    )
                              FROM SACER_IAM.ORG_STO_ENTE_CONVENZ ente_sto
                              left join SACER_IAM.ORG_AMBITO_TERRIT prov_sto
                               on (prov_sto.id_ambito_territ = ente_sto.id_prov_sede_legale)
                              join SACER_IAM.ORG_AMBITO_TERRIT territ_sto
                               on (territ_sto.id_ambito_territ = ente_sto.id_ambito_territ)
                              join sacer.ORG_CATEG_ENTE cat_sto
                               on (cat_sto.id_categ_ente = ente_sto.id_categ_ente)          
                              WHERE ente_sto.id_ente_convenz = ente.id_ente_convenz
                                                    )
                                            ), 
                                            -- Fine Anagrafiche

                                            -- inizio Strutture versanti
                                            XMLELEMENT
                                            ("recordChild", 
                                                    XMLELEMENT("tipoRecord", 'Struttura versante'),

                                                    (SELECT XMLAGG
                                                                    (XMLELEMENT
                                                                            ("child", 
                                                                                    XMLELEMENT("idRecord", org_ente.id_ente_convenz_org), 
                                                                                    XMLELEMENT
                                                                                            ("keyRecord", 
                                                                                            XMLELEMENT
                                                                                                    ("datoKey", 
                                                                                                            XMLELEMENT("colonnaKey",'dl_composito_organiz'), 
                                                                                                            XMLELEMENT("labelKey",'Struttura'),
                                                                                                            XMLELEMENT("valoreKey", substr(org.dl_composito_organiz, 1, instr(org.dl_composito_organiz, '(', -1) - 1) )
                                                                                                    )
                                                                                            ), 
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'ds_organiz'), 
                                                                                                    XMLELEMENT("labelDato",'Descrizione struttura'),
                                                                                                    XMLELEMENT("valoreDato", org.ds_organiz) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'dt_ini_val'), 
                                                                                                    XMLELEMENT("labelDato",'Data inizio validità'), 
                                                                                                    XMLELEMENT("valoreDato", to_char(org_ente.dt_ini_val, 'dd/mm/yyyy') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'dt_fine_val'), 
                                                                                                    XMLELEMENT("labelDato",'Data fine validità'), 
                                                                                                    XMLELEMENT("valoreDato", to_char(org_ente.dt_fine_val, 'dd/mm/yyyy') ) 
                                                                                            )
                                                                            )
                                                                    ORDER BY org.dl_composito_organiz
                                                                    )
                                                      FROM SACER_IAM.ORG_ENTE_CONVENZ_ORG org_ente
                                                      join SACER_IAM.USR_V_TREE_ORGANIZ_IAM org
                                                       on (org.id_organiz_iam = org_ente.id_organiz_iam)
                                                      WHERE org_ente.id_ente_convenz = ente.id_ente_convenz
                                                    )
                                            ), 
                                            -- Fine Strutture versanti

                                            -- inizio Archivista
                                            XMLELEMENT
                                            ("recordChild", 
                                                    XMLELEMENT("tipoRecord", 'Archivista'),

                                                    (SELECT XMLAGG
                                                                    (XMLELEMENT
                                                                            ("child", 
                                                                                    XMLELEMENT("idRecord", ark.id_ente_ark_rif), 
                                                                                    XMLELEMENT
                                                                                            ("keyRecord", 
                                                                                            XMLELEMENT
                                                                                                    ("datoKey", 
                                                                                                            XMLELEMENT("colonnaKey",'nm_userid'), 
                                                                                                            XMLELEMENT("labelKey",'Userid'),
                                                                                                            XMLELEMENT("valoreKey", usr.nm_userid) 
                                                                                                    ) 
                                                                                            )
                                                                            )
                                                                    ORDER BY usr.nm_userid
                                                                    )
                                                      FROM SACER_IAM.ORG_ENTE_ARK_RIF ark
                                                      join SACER_IAM.USR_USER usr
                                                       on (usr.id_user_iam = ark.id_user_iam)
                                                      WHERE ark.id_ente_convenz = ente.id_ente_convenz
                                                    )
                                            ),
                                            -- Fine Archivista

                                            -- inizio Accordo
                                            XMLELEMENT
                                            ("recordChild", 
                                                    XMLELEMENT("tipoRecord", 'Accordo'),

                                                    (SELECT XMLAGG
                                                                    (XMLELEMENT
                                                                            ("child", 
                                                                                    XMLELEMENT("idRecord", acc.id_accordo_ente), 
                                                                                    XMLELEMENT
                                                                                            ("keyRecord", 
                                                                                            XMLELEMENT
                                                                                                    ("datoKey", 
                                                                                                            XMLELEMENT("colonnaKey",'dt_dec_accordo'), 
                                                                                                            XMLELEMENT("labelKey",'Data decorrenza'),
                                                                                                            XMLELEMENT("valoreKey", to_char(acc.dt_dec_accordo, 'dd/mm/yyyy') ) 
                                                                                                    ) 
                                                                                            ), 
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'cd_registro_repertorio'), 
                                                                                                    XMLELEMENT("labelDato",'Registro repertorio'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(acc.cd_registro_repertorio, 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'aa_repertorio'), 
                                                                                                    XMLELEMENT("labelDato",'Anno repertorio'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(to_char(acc.aa_repertorio), 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'cd_key_repertorio'), 
                                                                                                    XMLELEMENT("labelDato",'Numero repertorio'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(acc.cd_key_repertorio, 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'cd_tipo_accordo'), 
                                                                                                    XMLELEMENT("labelDato",'Tipo accordo'), 
                                                                                                    XMLELEMENT("valoreDato", ti_acc.cd_tipo_accordo) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'dt_reg_accordo'), 
                                                                                                    XMLELEMENT("labelDato",'Data registrazione'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(to_char(acc.dt_reg_accordo, 'dd/mm/yyyy'), 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'dt_scad_accordo'), 
                                                                                                    XMLELEMENT("labelDato",'Data scadenza'), 
                                                                                                    XMLELEMENT("valoreDato", to_char(acc.dt_scad_accordo, 'dd/mm/yyyy') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'ds_firmatario_ente'), 
                                                                                                    XMLELEMENT("labelDato",'Firmatario'), 
                                                                                                    XMLELEMENT("valoreDato", acc.ds_firmatario_ente) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'fl_pagamento'), 
                                                                                                    XMLELEMENT("labelDato",'A pagamento'), 
                                                                                                    XMLELEMENT("valoreDato", decode(acc.fl_pagamento, '1', 'true', '0', 'false') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'nm_tariffario'), 
                                                                                                    XMLELEMENT("labelDato",'Tariffario'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(tariffario.nm_tariffario, 'null') ) 
                                                                                            ),	
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'cd_classe_ente_convenz'), 
                                                                                                    XMLELEMENT("labelDato",'Classe ente'), 
                                                                                                    XMLELEMENT("valoreDato", classe.cd_classe_ente_convenz ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'ni_abitanti'), 
                                                                                                    XMLELEMENT("labelDato",'Numero abitanti'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(to_char(acc.ni_abitanti, '999g999g999', 'NLS_NUMERIC_CHARACTERS = '',.''' ), 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'ds_note_accordo'), 
                                                                                                    XMLELEMENT("labelDato",'Note'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(acc.ds_note_accordo, 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'cd_ufe'), 
                                                                                                    XMLELEMENT("labelDato",'UFE'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(acc.cd_ufe, 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'ds_ufe'), 
                                                                                                    XMLELEMENT("labelDato",'Descrizione UFE'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(acc.ds_ufe, 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'cd_iva'), 
                                                                                                    XMLELEMENT("labelDato",'Codice IVA'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(iva.cd_iva, 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'cd_coge'), 
                                                                                                    XMLELEMENT("labelDato",'CoGe'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(acc.cd_coge, 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'cd_capitolo'), 
                                                                                                    XMLELEMENT("labelDato",'Capitolo'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(acc.cd_capitolo, 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'cd_cig'), 
                                                                                                    XMLELEMENT("labelDato",'CIG'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(acc.cd_cig, 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'cd_cup'), 
                                                                                                    XMLELEMENT("labelDato",'CUP'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(acc.cd_cup, 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'cd_rif_contab'), 
                                                                                                    XMLELEMENT("labelDato",'Riferimento contabile'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(acc.cd_rif_contab, 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'cd_ddt'), 
                                                                                                    XMLELEMENT("labelDato",'DdT'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(acc.cd_ddt, 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'cd_oda'), 
                                                                                                    XMLELEMENT("labelDato",'OdA'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(acc.cd_oda, 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'dt_rich_modulo_info'), 
                                                                                                    XMLELEMENT("labelDato",'Data richiesta modulo informazioni'), 
                                                                                                    XMLELEMENT("valoreDato", nvl(to_char(acc.dt_rich_modulo_info, 'dd/mm/yyyy'), 'null') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'dt_atto_accordo'), 
                                                                                                    XMLELEMENT("labelDato",'Data atto di approvazione'), 
                                                                                                    XMLELEMENT("valoreDato", to_char(acc.dt_atto_accordo, 'dd/mm/yyyy') ) 
                                                                                            ),
                                                                                    XMLELEMENT
                                                                                            ("datoRecord", 
                                                                                                    XMLELEMENT("colonnaDato",'ds_atto_accordo'), 
                                                                                                    XMLELEMENT("labelDato",'Atto approvazione accordo'), 
                                                                                                    XMLELEMENT("valoreDato", acc.ds_atto_accordo ) 
                                                                                            ),
                                                                                    -- inizio Servizio erogato
                                                                                    XMLELEMENT
                                                                                            ("recordChild", 
                                                                                                    XMLELEMENT("tipoRecord", 'Servizio erogato'),

                                                                                                    (SELECT XMLAGG
                                                                                                                    (XMLELEMENT
                                                                                                                            ("child", 
                                                                                                                                    XMLELEMENT("idRecord", serv.id_servizio_erogato), 
                                                                                                                                    XMLELEMENT
                                                                                                                                            ("keyRecord", 
                                                                                                                                            XMLELEMENT
                                                                                                                                                    ("datoKey", 
                                                                                                                                                            XMLELEMENT("colonnaKey",'nm_servizio_erogato'), 
                                                                                                                                                            XMLELEMENT("labelKey",'Servizio'),
                                                                                                                                                            XMLELEMENT("valoreKey", serv.nm_servizio_erogato)
                                                                                                                                                    )
                                                                                                                                            ),
                                                                                                                                    XMLELEMENT
                                                                                                                                            ("datoRecord", 
                                                                                                                                                    XMLELEMENT("colonnaDato",'cd_tipo_servizio'), 
                                                                                                                                                    XMLELEMENT("labelDato",'Tipo servizio'), 
                                                                                                                                                    XMLELEMENT("valoreDato", ti_serv.cd_tipo_servizio ) 
                                                                                                                                            ),
                                                                                                                                    XMLELEMENT
                                                                                                                                            ("datoRecord", 
                                                                                                                                                    XMLELEMENT("colonnaDato",'fl_pagamento'), 
                                                                                                                                                    XMLELEMENT("labelDato",'A pagamento'), 
                                                                                                                                                    XMLELEMENT("valoreDato", decode(serv.fl_pagamento, '1', 'true', '0', 'false') ) 
                                                                                                                                            ),
                                                                                                                                    XMLELEMENT
                                                                                                                                            ("datoRecord", 
                                                                                                                                                    XMLELEMENT("colonnaDato",'dt_erog'), 
                                                                                                                                                    XMLELEMENT("labelDato",'Data di erogazione'), 
                                                                                                                                                    XMLELEMENT("valoreDato", nvl(to_char(serv.dt_erog, 'dd/mm/yyyy'), 'null') ) 
                                                                                                                                            ),
                                                                                                                                    XMLELEMENT
                                                                                                                                            ("datoRecord", 
                                                                                                                                                    XMLELEMENT("colonnaDato",'nm_sistema_versante'), 
                                                                                                                                                    XMLELEMENT("labelDato",'Sistema versante'), 
                                                                                                                                                    XMLELEMENT("valoreDato", nvl(sist.nm_sistema_versante, 'null') ) 
                                                                                                                                            ),
                                                                                                                                    XMLELEMENT
                                                                                                                                            ("datoRecord", 
                                                                                                                                                    XMLELEMENT("colonnaDato",'nm_tariffa'), 
                                                                                                                                                    XMLELEMENT("labelDato",'Tariffa applicata'), 
                                                                                                                                                    XMLELEMENT("valoreDato", tariffa.nm_tariffa ) 
                                                                                                                                            ),		
                                                                                                                                    XMLELEMENT
                                                                                                                                            ("datoRecord", 
                                                                                                                                                    XMLELEMENT("colonnaDato",'im_valore_tariffa'), 
                                                                                                                                                    XMLELEMENT("labelDato",'Valore tariffa'), 
                                                                                                                                                    XMLELEMENT("valoreDato", serv.im_valore_tariffa ) 
                                                                                                                                            )
                                                                                                                            )
                                                                                                                            ORDER BY serv.nm_servizio_erogato
                                                                                                                    )
                                                                                                     FROM SACER_IAM.ORG_SERVIZIO_EROG serv
                                                                                                     join SACER_IAM.ORG_TIPO_SERVIZIO ti_serv
                                                                                                      on (ti_serv.id_tipo_servizio = serv.id_tipo_servizio)
                                                                                                     join SACER_IAM.ORG_TARIFFA tariffa
                                                                                                      on (tariffa.id_tariffa = serv.id_tariffa)
                                                                                                     left join SACER_IAM.APL_SISTEMA_VERSANTE sist
                                                                                                      on (sist.id_sistema_versante = serv.id_sistema_versante)

                                                                                                     WHERE serv.id_accordo_ente = acc.id_accordo_ente
                                                                                                    )
                                                                                            )
                                                                                            -- fine Servizio erogato
                                                                                        ,
                                                                                    -- inizio Modulo informazioni
                                                                                    XMLELEMENT
                                                                                            ("recordChild", 
                                                                                                    XMLELEMENT("tipoRecord", 'Modulo informazioni'),

                                                                                                    (SELECT XMLAGG
                                                                                                                    (XMLELEMENT
                                                                                                                            ("child", 
                                                                                                                                    XMLELEMENT("idRecord", modulo.id_modulo_info_accordo), 
                                                                                                                                    XMLELEMENT
                                                                                                                                            ("keyRecord", 
                                                                                                                                    XMLELEMENT
                                                                                                                                                    ("datoKey", 
                                                                                                                                                      XMLELEMENT("colonnaKey",'dt_ricev'), 
                                                                                                                                                      XMLELEMENT("labelKey",'Data ricezione modulo'), 
                                                                                                                                                      XMLELEMENT("valoreKey", nvl(to_char(modulo.dt_ricev, 'dd/mm/yyyy'), 'null') ) 
                                                                                                                                                    )
                                                                                                                                            ),
                                                                                                                                    XMLELEMENT
                                                                                                                                            ("datoRecord", 
                                                                                                                                                    XMLELEMENT("colonnaDato",'cd_registro_modulo_info'), 
                                                                                                                                                    XMLELEMENT("labelDato",'Registro'), 
                                                                                                                                                    XMLELEMENT("valoreDato", modulo.cd_registro_modulo_info ) 
                                                                                                                                            ),
                                                                                                                                    XMLELEMENT
                                                                                                                                            ("datoRecord", 
                                                                                                                                                    XMLELEMENT("colonnaDato",'aa_modulo_info'), 
                                                                                                                                                    XMLELEMENT("labelDato",'Anno'), 
                                                                                                                                                    XMLELEMENT("valoreDato", modulo.aa_modulo_info ) 
                                                                                                                                            ),
                                                                                                                                    XMLELEMENT
                                                                                                                                            ("datoRecord", 
                                                                                                                                                    XMLELEMENT("colonnaDato",'cd_key_modulo_info'), 
                                                                                                                                                    XMLELEMENT("labelDato",'Numero'), 
                                                                                                                                                    XMLELEMENT("valoreDato", modulo.cd_key_modulo_info ) 
                                                                                                                                            ),
                                                                                                                                    XMLELEMENT
                                                                                                                                            ("datoRecord", 
                                                                                                                                                    XMLELEMENT("colonnaDato",'cd_modulo_info'), 
                                                                                                                                                    XMLELEMENT("labelDato",'Identificativo modulo'), 
                                                                                                                                                    XMLELEMENT("valoreDato", modulo.cd_modulo_info ) 
                                                                                                                                            ),
                                                                                                                                    XMLELEMENT
                                                                                                                                            ("datoRecord", 
                                                                                                                                                    XMLELEMENT("colonnaDato",'nm_file_modulo_info'), 
                                                                                                                                                    XMLELEMENT("labelDato",'Nome file modulo'), 
                                                                                                                                                    XMLELEMENT("valoreDato", modulo.nm_file_modulo_info ) 
                                                                                                                                            )
                                                                                                                            )
                                                                                                                            ORDER BY modulo.cd_modulo_info
                                                                                                                    )
                                                                                                     FROM SACER_IAM.ORG_MODULO_INFO_ACCORDO modulo
                                                                                                     WHERE modulo.id_accordo_ente = acc.id_accordo_ente
                                                                                                    )
                                                                                            )
                                                                                            -- fine Modulo informazioni

                                                                            )
                                                                    ORDER BY acc.dt_dec_accordo
                                                                    )
                                                      FROM SACER_IAM.ORG_ACCORDO_ENTE acc
                                                      join SACER_IAM.ORG_TIPO_ACCORDO ti_acc
                                                       on (ti_acc.id_tipo_accordo = acc.id_tipo_accordo)
                                                      join SACER_IAM.ORG_CLASSE_ENTE_CONVENZ classe
                                                       on (classe.id_classe_ente_convenz = acc.id_classe_ente_convenz)
                                                      left join SACER_IAM.ORG_TARIFFARIO tariffario
                                                       on (tariffario.id_tariffario = acc.id_tariffario)
                                                      left join SACER_IAM.ORG_CD_IVA iva
                                                       on (iva.id_cd_iva = acc.id_cd_iva)
                                                      WHERE acc.id_ente_convenz = ente.id_ente_convenz
                                                      AND   SYSDATE BETWEEN acc.dt_dec_accordo AND acc.dt_scad_accordo
                                                    )
                                            ),
                                            -- Fine Accordo
                                            -- Inizio referenti
                                            XMLELEMENT
                                            ("recordChild", 
                                                    XMLELEMENT("tipoRecord", 'Referente'),

                                                    (SELECT XMLAGG
                                                                    (XMLELEMENT
                                                                            ("child", 
                                                                                    XMLELEMENT("idRecord", user_rif.ID_ENTE_USER_RIF), 
                                                                                    XMLELEMENT
                                                                                            ("keyRecord", 
                                              XMLELEMENT
                                                ("datoKey", 
                                                  XMLELEMENT("colonnaKey",'nm_userid'), 
                                                  XMLELEMENT("labelKey",'User id'),
                                                  XMLELEMENT("valoreKey", dati_utente.nm_userid )
                                                )
                                                ), 
                                              XMLELEMENT
                                                ("datoRecord", 
                                                  XMLELEMENT("colonnaDato",'nm_cognome_user'), 
                                                  XMLELEMENT("labelDato",'Cognome'), 
                                                  XMLELEMENT("valoreDato", dati_utente.nm_cognome_user ) 
                                                ),
                                              XMLELEMENT
                                                ("datoRecord", 
                                                  XMLELEMENT("colonnaDato",'nm_nome_user'), 
                                                  XMLELEMENT("labelDato",'Nome'), 
                                                  XMLELEMENT("valoreDato", dati_utente.nm_nome_user ) 
                                                ),
                                              XMLELEMENT
                                                ("datoRecord", 
                                                  XMLELEMENT("colonnaDato",'ds_email'), 
                                                  XMLELEMENT("labelDato",'Email'), 
                                                  XMLELEMENT("valoreDato", dati_utente.ds_email ) 
                                                )
                                            )
                                            ORDER BY dati_utente.NM_COGNOME_USER DESC, dati_utente.NM_NOME_USER DESC
                                        )
                                      FROM SACER_IAM.ORG_ENTE_USER_RIF user_rif
                                      join SACER_IAM.USR_USER dati_utente
                                       on (dati_utente.id_user_iam = user_rif.id_user_iam)
                                      WHERE user_rif.id_ente_convenz = ente.id_ente_convenz
                                                    )
                                            )
                                            -- Fine referenti
                      ) AS FOTO
                      -- Fine FotoOggetto
                    FROM SACER_IAM.ORG_ENTE_CONVENZ ente
                    left join SACER_IAM.ORG_AMBITO_TERRIT prov
                     on (prov.id_ambito_territ = ente.id_prov_sede_legale)
                    join SACER_IAM.ORG_AMBITO_TERRIT territ
                     on (territ.id_ambito_territ = ente.id_ambito_territ)
                    join sacer.ORG_CATEG_ENTE cat
                     on (cat.id_categ_ente = ente.id_categ_ente)
                    left join SACER_IAM.ORG_ENTE_CONVENZ ente_new
                     on (ente_new.id_ente_convenz = ente.id_ente_convenz_nuovo)
                    left join SACER_IAM.ORG_AMBIENTE_ENTE_CONVENZ ambiente_ente_conv
                     on (ambiente_ente_conv.id_ambiente_ente_convenz=ente.id_ambiente_ente_convenz) 
                    -- condizioni in più rispetto alla foto standard
                    join SACER_IAM.ORG_ENTE_CONVENZ_ORG org_ente_org
                     on (org_ente_org.ID_ENTE_CONVENZ = ente.ID_ENTE_CONVENZ)
                    join SACER_IAM.USR_ORGANIZ_IAM usr_org_iam
                     on (usr_org_iam.ID_ORGANIZ_IAM = org_ente_org.ID_ORGANIZ_IAM)
                    join SACER_IAM.APL_APPLIC appl
                     on (appl.ID_APPLIC = usr_org_iam.ID_APPLIC)
                    join SACER_IAM.APL_TIPO_ORGANIZ tipo_org
                     on (tipo_org.ID_TIPO_ORGANIZ = usr_org_iam.ID_TIPO_ORGANIZ)
                    WHERE   appl.NM_APPLIC= 'SACER'
                    AND     SYSDATE BETWEEN org_ente_org.DT_INI_VAL AND org_ente_org.DT_FINE_VAL
                    AND     tipo_org.NM_TIPO_ORGANIZ='STRUTTURA'
                    AND     usr_org_iam.ID_ORGANIZ_APPLIC = :ID_OGGETTO


                ) -- FINE FOTO ENTE CONVENZIONATO
            ), -- FINE FOTO ENTE CONVENZIONATO trasformata in child

            (   -- INIZIO FOTO CHILD dei formati ammessi
                SELECT XMLELEMENT
                            ("recordChild", 
                                    XMLELEMENT("tipoRecord", 'Formati ammessi'),

                                    (SELECT XMLAGG
                                                    (XMLELEMENT
                                                            ("child", 
                                                                    XMLELEMENT("idRecord", id_uso_formato_file_ammesso), 
                                                                    XMLELEMENT
                                                                            ("keyRecord", 
                                                                            XMLELEMENT
                                                                                    ("datoKey", 
                                                                                            XMLELEMENT("colonnaKey",'nm_strut'), 
                                                                                            XMLELEMENT("labelKey",'Nome struttura'),
                                                                                            XMLELEMENT("valoreKey", nm_strut) 
                                                                                    ),
                                                                            XMLELEMENT
                                                                                    ("datoKey", 
                                                                                            XMLELEMENT("colonnaKey",'nm_formato_file_doc'), 
                                                                                            XMLELEMENT("labelKey",'Nome formato file doc'),
                                                                                            XMLELEMENT("valoreKey", nm_formato_file_doc) 
                                                                                    ),
                                                                            XMLELEMENT
                                                                                    ("datoKey", 
                                                                                            XMLELEMENT("colonnaKey",'ni_ord_uso'), 
                                                                                            XMLELEMENT("labelKey",'Numero uso'),
                                                                                            XMLELEMENT("valoreKey", ni_ord_uso) 
                                                                                    ),
                                                                            XMLELEMENT
                                                                                    ("datoKey", 
                                                                                            XMLELEMENT("colonnaKey",'nm_formato_file_standard'), 
                                                                                            XMLELEMENT("labelKey",'Nome formato file standard'),
                                                                                            XMLELEMENT("valoreKey", nm_formato_file_standard) 
                                                                                    )
                                                                            ),
                                                                    XMLELEMENT
                                                                            ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'nm_mime_type'),
                                                                                    XMLELEMENT("labelDato",'Nome mime type'),
                                                                                    XMLELEMENT("valoreDato",nm_mime_type)
                                                                            ),
                                                                    XMLELEMENT
                                                                            ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'ti_esito_contr_formato'),
                                                                                    XMLELEMENT("labelDato",'Tipo esito controllo formato'),
                                                                                    XMLELEMENT("valoreDato",ti_esito_contr_formato)
                                                                            )
                                                            )
                                                    ORDER BY NM_FORMATO_FILE_DOC
                                                    )
                                    FROM                             
                                        (SELECT DISTINCT t1.id_uso_formato_file_ammesso, t0.ID_FORMATO_FILE_DOC, t0.CD_VERSIONE, t0.DS_FORMATO_FILE_DOC, 
                                                    t0.DT_ISTITUZ, t0.DT_SOPPRES, t0.NM_FORMATO_FILE_DOC AS NM_FORMATO_FILE_DOC, t0.ID_STRUT, t1.NI_ORD_USO AS NI_ORD_USO, 
                                                    t2.NM_MIMETYPE_FILE AS NM_MIME_TYPE, t2.TI_ESITO_CONTR_FORMATO, t2.NM_FORMATO_FILE_STANDARD AS NM_FORMATO_FILE_STANDARD,
                                                    str.NM_STRUT AS NM_STRUT
                                        FROM    SACER.DEC_FORMATO_FILE_DOC t0, 
                                                SACER.DEC_USO_FORMATO_FILE_STANDARD t1, 
                                                SACER.DEC_FORMATO_FILE_STANDARD t2,
                                                SACER.ORG_STRUT str
                                        WHERE   t0.ID_STRUT = :ID_OGGETTO 
                                        AND     t0.DT_ISTITUZ <= SYSDATE 
                                        AND     t0.DT_SOPPRES >= SYSDATE 
                                        AND     t0.ID_FORMATO_FILE_DOC = t1.ID_FORMATO_FILE_DOC
                                        AND     t1.NI_ORD_USO = 1
                                        AND     t1.ID_FORMATO_FILE_STANDARD = t2.ID_FORMATO_FILE_STANDARD
                                        AND     t0.id_strut = str.id_strut)
                                        )) FROM DUAL) -- FINE FOTO Record child dei formati ammessi
        ).getClobVal() -- Fine elemento fotoOggetto
            AS DISCIPLINARE_TECNICO
    FROM  
                    -- ------------------------------------------
                    -- FOTO DELLA STRUTTURA COME RECORD CHILD
                    -- ------------------------------------------
                    (SELECT  XMLQUERY('for $a in /fotoOggetto
                                          let $recordMaster := $a/recordMaster
                                          let $tuttiChild := $a/recordChild
                                          return  <recordChild>
                                                    {$recordMaster/tipoRecord}
                                                    <child>
                                                      {$recordMaster/idRecord}
                                                      {$recordMaster/keyRecord}
                                                      {$recordMaster/datoRecord}
                                                      {$tuttiChild}
                                                    </child>
                                                  </recordChild>' 
                    PASSING FOTO RETURNING CONTENT) AS STRUTTURA
                    FROM
                            (
                                SELECT    XMLELEMENT("fotoOggetto", 
                                            XMLELEMENT("versioneLogRecord",'1.0'),
                                            XMLELEMENT
                                                                ("recordMaster",
                                                                        XMLELEMENT("tipoRecord",'Struttura'),
                                                                        XMLELEMENT("idRecord",strut.id_strut),
                                                                        XMLELEMENT
                                                                                ("keyRecord",
                                                                                        XMLELEMENT
                                                                                                ("datoKey",
                                                                                                        XMLELEMENT("colonnaKey",'nm_ambiente'),
                                                                                                        XMLELEMENT("labelKey",'Ambiente'),
                                                                                                        XMLELEMENT("valoreKey",amb.nm_ambiente)
                                                                                                ),
                                                                                        XMLELEMENT
                                                                                                ("datoKey",
                                                                                                        XMLELEMENT("colonnaKey",'nm_ente'),
                                                                                                        XMLELEMENT("labelKey",'Ente'),
                                                                                                        XMLELEMENT("valoreKey",ente.nm_ente)
                                                                                                ),
                                                                                        XMLELEMENT
                                                                                                ("datoKey",
                                                                                                        XMLELEMENT("colonnaKey",'nm_strut'),
                                                                                                        XMLELEMENT("labelKey",'Struttura'),
                                                                                                        XMLELEMENT("valoreKey",strut.nm_strut)
                                                                                                )
                                                                                ),
                                                                                -- fine keyRecord

                                                                        -- inizio dati record master
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'ds_strut'),
                                                                                        XMLELEMENT("labelDato",'Descrizione struttura'),
                                                                                        XMLELEMENT("valoreDato",strut.ds_strut)
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'ds_ente'),
                                                                                        XMLELEMENT("labelDato",'Descrizione ente'),
                                                                                        XMLELEMENT("valoreDato",ente.ds_ente)
                                                                                ),
                                                                        XMLELEMENT ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'cd_categ_strut'),
                                                                                        XMLELEMENT("labelDato",'Categoria struttura'),
                                                                                        XMLELEMENT("valoreDato",cat.cd_categ_strut)
                                                                                ),
                                                                        XMLELEMENT ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'nm_ambiente_ente_convenz'),
                                                                                        XMLELEMENT("labelDato",'Ambiente ente convenzionato'),
                                                                                        XMLELEMENT("valoreDato",nvl(amb_convenz.nm_ambiente_ente_convenz, 'null') )
                                                                                ),
                                                                        XMLELEMENT ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'nm_ente_convenz'),
                                                                                        XMLELEMENT("labelDato",'Ente convenzionato'),
                                                                                        XMLELEMENT("valoreDato",nvl(ente_convenz.nm_ente_convenz, 'null') )
                                                                                ),
                                                                        XMLELEMENT ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'dt_ini_val'),
                                                                                        XMLELEMENT("labelDato",'Data inizio validità'),
                                                                                        XMLELEMENT("valoreDato",to_char(strut.dt_ini_val, 'dd/mm/yyyy') )
                                                                                ),
                                                                        XMLELEMENT ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'dt_fine_val'),
                                                                                        XMLELEMENT("labelDato",'Data fine validità'),
                                                                                        XMLELEMENT("valoreDato",to_char(strut.dt_fine_val, 'dd/mm/yyyy') )
                                                                                ),	
                                                                        XMLELEMENT ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'cd_ipa'),
                                                                                        XMLELEMENT("labelDato",'Codice IPA'),
                                                                                        XMLELEMENT("valoreDato",nvl(strut.cd_ipa, 'null') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'ti_scad_chius_volume'),
                                                                                        XMLELEMENT("labelDato",'Tipo scadenza chiusura volume / elenco'),
                                                                                        XMLELEMENT("valoreDato", nvl(strut.ti_scad_chius_volume, 'null') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'ti_tempo_scad_chius'),
                                                                                        XMLELEMENT("labelDato",'Tipo tempo scadenza chiusura'),
                                                                                        XMLELEMENT("valoreDato", nvl(strut.ti_tempo_scad_chius, 'null') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'ni_tempo_scad_chius'),
                                                                                        XMLELEMENT("labelDato",'Tempo scadenza chiusura'),
                                                                                        XMLELEMENT("valoreDato", nvl(to_char(strut.ni_tempo_scad_chius), 'null') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'ti_tempo_scad_chius_firme'),
                                                                                        XMLELEMENT("labelDato",'Tipo tempo scadenza chiusura firme'),
                                                                                        XMLELEMENT("valoreDato", strut.ti_tempo_scad_chius_firme )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'ni_tempo_scad_chius_firme'),
                                                                                        XMLELEMENT("labelDato",'Tempo scadenza chiusura firme'),
                                                                                        XMLELEMENT("valoreDato", to_char(strut.ni_tempo_scad_chius_firme))
                                                                                ),	
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'num_max_comp_criterio_raggr'),
                                                                                        XMLELEMENT("labelDato",'Numero massimo componenti'),
                                                                                        XMLELEMENT("valoreDato", nvl(to_char(strut.num_max_comp_criterio_raggr), 'null') )
                                                                                ),	
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_abilita_contr_crittog_vers'),
                                                                                        XMLELEMENT("labelDato",'Controllo crittografico'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_abilita_contr_crittog_vers, '1', 'true', '0', 'false') )
                                                                                ),	
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_abilita_contr_trust_vers'),
                                                                                        XMLELEMENT("labelDato",'Controllo catena trusted'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_abilita_contr_trust_vers, '1', 'true', '0', 'false') )
                                                                                ),	
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_abilita_contr_certif_vers'),
                                                                                        XMLELEMENT("labelDato",'Controllo certificato'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_abilita_contr_certif_vers, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_abilita_contr_crl_vers'),
                                                                                        XMLELEMENT("labelDato",'Controllo crl'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_abilita_contr_crl_vers, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_firma_noconos'),
                                                                                        XMLELEMENT("labelDato",'Accetta firma sconosciuta'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_firma_noconos, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_firma_giugno_2011'),
                                                                                        XMLELEMENT("labelDato",'Accetta firma no delibera 45'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_firma_giugno_2011, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_firma_noconf'),
                                                                                        XMLELEMENT("labelDato",'Accetta firma non conforme'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_firma_noconf, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_marca_noconos'),
                                                                                        XMLELEMENT("labelDato",'Accetta marca sconosciuta'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_marca_noconos, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_contr_trust_neg'),
                                                                                        XMLELEMENT("labelDato",'Accetta controllo catena trusted negativo'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_contr_trust_neg, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_contr_crittog_neg'),
                                                                                        XMLELEMENT("labelDato",'Accetta controllo crittografico negativo'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_contr_crittog_neg, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_contr_certif_scad'),
                                                                                        XMLELEMENT("labelDato",'Accetta controllo certificato scaduto'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_contr_certif_scad, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_contr_certif_noval'),
                                                                                        XMLELEMENT("labelDato",'Accetta controllo certificato non valido'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_contr_certif_noval, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_contr_certif_nocert'),
                                                                                        XMLELEMENT("labelDato",'Accetta controllo certificato non corretto'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_contr_certif_nocert, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_contr_crl_neg'),
                                                                                        XMLELEMENT("labelDato",'Accetta controllo crl negativo'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_contr_crl_neg, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_contr_crl_scad'),
                                                                                        XMLELEMENT("labelDato",'Accetta controllo crl scaduta'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_contr_crl_scad, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_contr_crl_noval'),
                                                                                        XMLELEMENT("labelDato",'Accetta controllo crl non valida'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_contr_crl_noval, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_contr_crl_noscar'),
                                                                                        XMLELEMENT("labelDato",'Accetta controllo crl non scaricabile'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_contr_crl_noscar, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_abilita_contr_fmt'),
                                                                                        XMLELEMENT("labelDato",'Abilita controllo formato file'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_abilita_contr_fmt, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_contr_fmt_neg'),
                                                                                        XMLELEMENT("labelDato",'Accetta controllo formato file negativo'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_contr_fmt_neg, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_forza_fmt'),
                                                                                        XMLELEMENT("labelDato",'Forza accettazione formato file'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_forza_fmt, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_abilita_contr_hash_vers'),
                                                                                        XMLELEMENT("labelDato",'Abilita controllo hash versato'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_abilita_contr_hash_vers, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_contr_hash_neg'),
                                                                                        XMLELEMENT("labelDato",'Accetta controllo hash versato negativo'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_contr_hash_neg, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_forza_hash_vers'),
                                                                                        XMLELEMENT("labelDato",'Forza accettazione hash versato'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_forza_hash_vers, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_abilita_contr_fmt_num'),
                                                                                        XMLELEMENT("labelDato",'Abilita controllo formato numero registro'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_abilita_contr_fmt_num, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_accetta_fmt_num_neg'),
                                                                                        XMLELEMENT("labelDato",'Accetta controllo formato numero registro negativo'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_accetta_fmt_num_neg, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_forza_fmt_num'),
                                                                                        XMLELEMENT("labelDato",'Forza accettazione formato numero registro'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_forza_fmt_num, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_obbl_oggetto'),
                                                                                        XMLELEMENT("labelDato",'Obbligatorietà campo oggetto dei dati di profilo dell''unità documentaria'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_obbl_oggetto, '1', 'true', '0', 'false') )
                                                                                ),
                                                                        XMLELEMENT
                                                                                ("datoRecord",
                                                                                        XMLELEMENT("colonnaDato",'fl_obbl_data'),
                                                                                        XMLELEMENT("labelDato",'Obbligatorietà campo data dei dati di profilo dell''unità documentaria'),
                                                                                        XMLELEMENT("valoreDato", decode(strut.fl_obbl_data, '1', 'true', '0', 'false') )
                                                                                )				
                                                                ),

                                                        -- fine recordMaster

                                                        -- inizio record child USR_V_ABIL_ORGANIZ Utenti abilitati (Persone fisiche)
                                                        XMLELEMENT
                                                                ("recordChild",
                                                                        XMLELEMENT("tipoRecord",'Utenti abilitati'),
                                                                        (SELECT XMLAGG
                                                                                        (XMLELEMENT
                                                                                                ("child",
                                                                                                        XMLELEMENT("idRecord", abil.ID_USER_IAM),
                                                                                                        XMLELEMENT
                                                                                                                ("keyRecord",
                                                                                                                        XMLELEMENT
                                                                                                                                ("datoKey",
                                                                                                                                        XMLELEMENT("colonnaKey",'nm_userid'),
                                                                                                                                        XMLELEMENT("labelKey",'Userid'),
                                                                                                                                        XMLELEMENT("valoreKey",abil.nm_userid)
                                                                                                                                )
                                                                                                                ),

                                                                                                        -- inizio dati record
                                                                                                        XMLELEMENT
                                                                                                                ("datoRecord",
                                                                                                                        XMLELEMENT("colonnaDato",'nm_cognome_user'),
                                                                                                                        XMLELEMENT("labelDato",'Cognome utente'),
                                                                                                                        XMLELEMENT("valoreDato",abil.nm_cognome_user)
                                                                                                                ),
                                                                                                        XMLELEMENT
                                                                                                                ("datoRecord",
                                                                                                                        XMLELEMENT("colonnaDato",'nm_nome_user'),
                                                                                                                        XMLELEMENT("labelDato",'Nome utente'),
                                                                                                                        XMLELEMENT("valoreDato",abil.nm_nome_user)
                                                                                                                ),
                                                                                                        XMLELEMENT
                                                                                                                ("datoRecord",
                                                                                                                        XMLELEMENT("colonnaDato",'ds_email'),
                                                                                                                        XMLELEMENT("labelDato",'Email utente'),
                                                                                                                        XMLELEMENT("valoreDato",abil.ds_email)
                                                                                                                ),
                                                                                                        XMLELEMENT
                                                                                                                ("datoRecord",
                                                                                                                        XMLELEMENT("colonnaDato",'lista_ruo'),
                                                                                                                        XMLELEMENT("labelDato",'Lista ruoli'),
                                                                                                                        XMLELEMENT("valoreDato",abil.lista_ruo)
                                                                                                                ),
                                                                                                        XMLELEMENT
                                                                                                                ("datoRecord",
                                                                                                                        XMLELEMENT("colonnaDato",'lista_tipo_dato_non_abil'),
                                                                                                                        XMLELEMENT("labelDato",'Lista tipi dato non abilitati'),
                                                                                                                        XMLELEMENT("valoreDato",nvl(to_char(abil.lista_tipo_dato_non_abil), 'null'))
                                                                                                                )	
                                                                                                )
                                                                                        ORDER BY abil.NM_COGNOME_USER, abil.NM_NOME_USER
                                                                                        )
                                                                        FROM    SACER.USR_V_LIS_USER_DISCIP_BY_STRUT abil
                                                                        WHERE   abil.ID_STRUT = strut.id_strut
                                                                        AND     abil.TI_USER_ARK_RIF = 'PERSONA_FISICA')
                                                                ), -- fine record child USR_V_LIS_USER_DISCIP_BY_STRUT (PERSONE FISICHE)

                                                        -- inizio record child USR_V_ABIL_ORGANIZ Rerenti
                                                        XMLELEMENT
                                                                ("recordChild",
                                                                        XMLELEMENT("tipoRecord",'Utenti referenti'),
                                                                        (SELECT XMLAGG
                                                                                        (XMLELEMENT
                                                                                                ("child",
                                                                                                        XMLELEMENT("idRecord", abil.ID_USER_IAM),
                                                                                                        XMLELEMENT
                                                                                                                ("keyRecord",
                                                                                                                        XMLELEMENT
                                                                                                                                ("datoKey",
                                                                                                                                        XMLELEMENT("colonnaKey",'nm_userid'),
                                                                                                                                        XMLELEMENT("labelKey",'Userid'),
                                                                                                                                        XMLELEMENT("valoreKey",abil.nm_userid)
                                                                                                                                )
                                                                                                                ),

                                                                                                        -- inizio dati record
                                                                                                        XMLELEMENT
                                                                                                                ("datoRecord",
                                                                                                                        XMLELEMENT("colonnaDato",'nm_cognome_user'),
                                                                                                                        XMLELEMENT("labelDato",'Cognome utente'),
                                                                                                                        XMLELEMENT("valoreDato",abil.nm_cognome_user)
                                                                                                                ),
                                                                                                        XMLELEMENT
                                                                                                                ("datoRecord",
                                                                                                                        XMLELEMENT("colonnaDato",'nm_nome_user'),
                                                                                                                        XMLELEMENT("labelDato",'Nome utente'),
                                                                                                                        XMLELEMENT("valoreDato",abil.nm_nome_user)
                                                                                                                ),
                                                                                                        XMLELEMENT
                                                                                                                ("datoRecord",
                                                                                                                        XMLELEMENT("colonnaDato",'ds_email'),
                                                                                                                        XMLELEMENT("labelDato",'Email utente'),
                                                                                                                        XMLELEMENT("valoreDato",abil.ds_email)
                                                                                                                ),
                                                                                                        XMLELEMENT
                                                                                                                ("datoRecord",
                                                                                                                        XMLELEMENT("colonnaDato",'ti_user_ark_rif'),
                                                                                                                        XMLELEMENT("labelDato",'Tipologia referente'),
                                                                                                                        XMLELEMENT("valoreDato",abil.ti_user_ark_rif)
                                                                                                                )	
                                                                                                )
                                                                                        ORDER BY abil.NM_COGNOME_USER, abil.NM_NOME_USER
                                                                                        )
                                                                        FROM    SACER.USR_V_LIS_USER_DISCIP_BY_STRUT abil
                                                                        WHERE   abil.ID_STRUT = strut.id_strut
                                                                        AND     abil.TI_USER_ARK_RIF <> 'PERSONA_FISICA')
                                                                ), -- fine record child USR_V_LIS_USER_DISCIP_BY_STRUT Referenti

                                                            -- inizio record child DEC_TIPO_SERIE
                                                            XMLELEMENT
                                                                    ("recordChild",
                                                                            XMLELEMENT("tipoRecord",'Tipi serie'),
                                                                            (SELECT XMLAGG
                                                                                            (XMLELEMENT
                                                                                                    ("child",
                                                                                                            XMLELEMENT("idRecord", tipo_serie.ID_TIPO_SERIE),
                                                                                                            XMLELEMENT
                                                                                                                    ("keyRecord",
                                                                                                                            XMLELEMENT
                                                                                                                                    ("datoKey",
                                                                                                                                            XMLELEMENT("colonnaKey",'nm_tipo_serie'),
                                                                                                                                            XMLELEMENT("labelKey",'Nome tipo serie'),
                                                                                                                                            XMLELEMENT("valoreKey",tipo_serie.nm_tipo_serie)
                                                                                                                                    )
                                                                                                                    ),

                                                                                                            XMLELEMENT
                                                                                                                    ("datoRecord",
                                                                                                                            XMLELEMENT("colonnaDato",'ds_tipo_serie'),
                                                                                                                            XMLELEMENT("labelDato",'Descrizione tipo serie'),
                                                                                                                            XMLELEMENT("valoreDato",nvl(tipo_serie.ds_tipo_serie, 'null'))
                                                                                                                    ),
                                                                                                            XMLELEMENT
                                                                                                                    ("datoRecord",
                                                                                                                            XMLELEMENT("colonnaDato",'ni_anni_conserv'),
                                                                                                                            XMLELEMENT("labelDato",'Numero anni conservazione'),
                                                                                                                            XMLELEMENT("valoreDato",nvl(to_char(tipo_serie.ni_anni_conserv),'null'))
                                                                                                                    )	
                                                                                                    )
                                                                                            ORDER BY tipo_serie.NM_TIPO_SERIE
                                                                                            )
                                                                            FROM    SACER.DEC_TIPO_SERIE tipo_serie
                                                                            WHERE   tipo_serie.ID_STRUT = strut.id_strut
                                                                            AND     tipo_serie.FL_TIPO_SERIE_PADRE = 0
                                                                            AND     tipo_serie.DT_ISTITUZ <= sysdate 
                                                                            AND     tipo_serie.DT_SOPPRES > sysdate)
                                                                    ), -- fine record child DEC_TIPO_SERIE


                                                            -- inizio record child TIPI OGGETTO DA TRASFORMARE
                                                            XMLELEMENT
                                                                    ("recordChild",
                                                                            XMLELEMENT("tipoRecord",'Tipi oggetto da trasformare'),
                                                                            (SELECT XMLAGG
                                                                                            (XMLELEMENT
                                                                                                    ("child",
                                                                                                            XMLELEMENT("idRecord", trasf.ID_TIPO_OBJECT_DA_TRASF),
                                                                                                            XMLELEMENT
                                                                                                                    ("keyRecord",
                                                                                                                            XMLELEMENT
                                                                                                                                    ("datoKey",
                                                                                                                                            XMLELEMENT("colonnaKey",'nm_tipo_object_da_trasf'),
                                                                                                                                            XMLELEMENT("labelKey",'Nome tipo oggetto da trasformare'),
                                                                                                                                            XMLELEMENT("valoreKey",trasf.nm_tipo_object_da_trasf)
                                                                                                                                    )
                                                                                                                    ),

                                                                                                            XMLELEMENT
                                                                                                                    ("datoRecord",
                                                                                                                            XMLELEMENT("colonnaDato",'ds_tipo_object_da_trasf'),
                                                                                                                            XMLELEMENT("labelDato",'Descrizione tipo oggetto da trasformare'),
                                                                                                                            XMLELEMENT("valoreDato",nvl(trasf.ds_tipo_object_da_trasf, 'null'))
                                                                                                                    ),
                                                                                                            XMLELEMENT
                                                                                                                    ("datoRecord",
                                                                                                                            XMLELEMENT("colonnaDato",'nm_vers_gen'),
                                                                                                                            XMLELEMENT("labelDato",'Nome versatore'),
                                                                                                                            XMLELEMENT("valoreDato",nvl(to_char(trasf.nm_vers_gen),'null'))
                                                                                                                    ),
                                                                                                            XMLELEMENT
                                                                                                                    ("datoRecord",
                                                                                                                            XMLELEMENT("colonnaDato",'ds_trasf'),
                                                                                                                            XMLELEMENT("labelDato",'Descrizione trasformazione'),
                                                                                                                            XMLELEMENT("valoreDato",nvl(to_char(trasf.ds_trasf),'null'))
                                                                                                                    )	
                                                                                                    )
                                                                                            ORDER BY trasf.NM_TIPO_OBJECT_DA_TRASF
                                                                                            )
                                                                            FROM    SACER_PING.PIG_V_LIS_TIOBJDATRASF_BYSTRUT trasf
                                                                            WHERE   strut.ID_STRUT = trasf.ID_ORGANIZ_APPLIC)
                                                                    ), -- fine record child TIPI OGGETTO DA TRASFORMARE

                                            (SELECT    XMLELEMENT("recordChild", 
                                                        XMLELEMENT("tipoRecord",'Tipo unità documentaria'),
                                                            (SELECT XMLAGG(
                                                                    XMLELEMENT
                                                                        ("child",
                                                                            XMLELEMENT("idRecord",tipo_ud.id_tipo_unita_doc),
                                                                            XMLELEMENT
                                                                                ("keyRecord",
                                                                                    XMLELEMENT
                                                                                        ("datoKey",
                                                                                            XMLELEMENT("colonnaKey",'nm_tipo_unita_doc'),
                                                                                            XMLELEMENT("labelKey",'Tipo unità documentaria'),
                                                                                            XMLELEMENT("valoreKey",tipo_ud.nm_tipo_unita_doc)
                                                                                        )
                                                                                ),
                                                                                -- fine keyRecord
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'ds_tipo_unita_doc'),
                                                                                    XMLELEMENT("labelDato",'Descrizione tipo unità documentaria'),
                                                                                    XMLELEMENT("valoreDato",tipo_ud.ds_tipo_unita_doc)
                                                                                ),
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'fl_forza_collegamento'),
                                                                                    XMLELEMENT("labelDato",'Forzatura collegamento'),
                                                                                    XMLELEMENT("valoreDato", DECODE(tipo_ud.FL_FORZA_COLLEGAMENTO,'1','true','0','false') )
                                                                                ),
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'ti_save_file'),
                                                                                    XMLELEMENT("labelDato",'Tipo salvataggio file'),
                                                                                    XMLELEMENT("valoreDato", tipo_ud.ti_save_file )
                                                                                ),
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'dt_istituz'),
                                                                                    XMLELEMENT("labelDato",'Data istituzione'),
                                                                                    XMLELEMENT("valoreDato", to_char(tipo_ud.dt_istituz, 'dd/mm/yyyy') )
                                                                                ),
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'dt_soppres'),
                                                                                    XMLELEMENT("labelDato",'Data soppressione'),
                                                                                    XMLELEMENT("valoreDato", to_char(tipo_ud.dt_soppres, 'dd/mm/yyyy') )
                                                                                ),
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'cd_subcateg_tipo_unita_doc'),
                                                                                    XMLELEMENT("labelDato",'Sottocategoria'),
                                                                                    XMLELEMENT("valoreDato", sotto_categ.cd_categ_tipo_unita_doc )
                                                                                ),
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'cd_categ_tipo_unita_doc'),
                                                                                    XMLELEMENT("labelDato",'Categoria'),
                                                                                    XMLELEMENT("valoreDato", categ.cd_categ_tipo_unita_doc )
                                                                                ),
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'fl_crea_tipo_serie_standard'),
                                                                                    XMLELEMENT("labelDato",'Crea tipo serie standard'),
                                                                                    XMLELEMENT("valoreDato", nvl(tipo_ud.fl_crea_tipo_serie_standard, 'null') )
                                                                                ),
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'nm_modello_tipo_serie'),
                                                                                    XMLELEMENT("labelDato",'Modello tipo serie'),
                                                                                    XMLELEMENT("valoreDato", nvl(modello.nm_modello_tipo_serie, 'null') )
                                                                                ),
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'nm_tipo_serie_da_creare'),
                                                                                    XMLELEMENT("labelDato",'Tipo serie da creare'),
                                                                                    XMLELEMENT("valoreDato", nvl(tipo_ud.nm_tipo_serie_da_creare, 'null') )
                                                                                ),
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'ds_tipo_serie_da_creare'),
                                                                                    XMLELEMENT("labelDato",'Descrizione tipo serie da creare'),
                                                                                    XMLELEMENT("valoreDato", nvl(tipo_ud.ds_tipo_serie_da_creare, 'null') )
                                                                                ),
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'cd_serie_da_creare'),
                                                                                    XMLELEMENT("labelDato",'Codice serie da creare'),
                                                                                    XMLELEMENT("valoreDato", nvl(tipo_ud.cd_serie_da_creare, 'null') )
                                                                                ),
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'ds_serie_da_creare'),
                                                                                    XMLELEMENT("labelDato",'Descrizione serie da creare'),
                                                                                    XMLELEMENT("valoreDato", nvl(tipo_ud.ds_serie_da_creare, 'null') )
                                                                                ),
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'ti_serv_conserv'),
                                                                                    XMLELEMENT("labelDato",'Tipo servizio per conservazione'),
                                                                                    XMLELEMENT("valoreDato", nvl(ti_serv_conserv.cd_tipo_servizio, 'null') )
                                                                                ),
                                                                            XMLELEMENT
                                                                                ("datoRecord",
                                                                                    XMLELEMENT("colonnaDato",'ti_serv_attiv'),
                                                                                    XMLELEMENT("labelDato",'Tipo servizio per attivazione sist. versante'),
                                                                                    XMLELEMENT("valoreDato", nvl(ti_serv_attiv.cd_tipo_servizio, 'null') )
                                                                                ),
                                                                            -- --------------------------------------    
                                                                            -- Tutti i record child della singola UD    
                                                                            -- --------------------------------------  
                                                                            -- inizio record child registri ammessi  
                                                                            XMLELEMENT
                                                                                ("recordChild",
                                                                                    XMLELEMENT("tipoRecord",'Registri ammessi'),
                                                                                    (SELECT XMLAGG
                                                                                            (XMLELEMENT
                                                                                                ("child",
                                                                                                    XMLELEMENT("idRecord", amm.id_tipo_unita_doc_ammesso),
                                                                                                    XMLELEMENT
                                                                                                        ("keyRecord",
                                                                                                            XMLELEMENT
                                                                                                                ("datoKey",
                                                                                                                    XMLELEMENT("colonnaKey",'cd_registro_unita_doc'),
                                                                                                                    XMLELEMENT("labelKey",'Registro'),
                                                                                                                    XMLELEMENT("valoreKey",reg.cd_registro_unita_doc)
                                                                                                                )
                                                                                                        ),
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'ds_registro_unita_doc'),
                                                                                                            XMLELEMENT("labelDato",'Descrizione registro'),
                                                                                                            XMLELEMENT("valoreDato", nvl(reg.ds_registro_unita_doc, 'null') )
                                                                                                        ),
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'dt_istituz'),
                                                                                                            XMLELEMENT("labelDato",'Data attivazione'),
                                                                                                            XMLELEMENT("valoreDato", nvl(to_char(dt_istituz, 'dd/mm/yyyy'), 'null') )
                                                                                                        ),
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'dt_soppres'),
                                                                                                            XMLELEMENT("labelDato",'Descrizione registro'),
                                                                                                            XMLELEMENT("valoreDato", nvl(to_char(dt_soppres, 'dd/mm/yyyy'), 'null') )
                                                                                                        )
                                                                                                        ,
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'fl_registro_fisc'),
                                                                                                            XMLELEMENT("labelDato",'Registro fiscale'),
                                                                                                            XMLELEMENT("valoreDato", nvl(decode(fl_registro_fisc, '1', 'true', '0', 'false'), 'null') )
                                                                                                        ),
                                                                                                    -- Inizio periodi di validità registro
                                                                                                    XMLELEMENT
                                                                                                        ("recordChild",
                                                                                                            XMLELEMENT("tipoRecord",'Periodi di validità'),
                                                                                                            (SELECT XMLAGG
                                                                                                                    (XMLELEMENT
                                                                                                                        ("child",
                                                                                                                            XMLELEMENT("idRecord", periodi.id_aa_registro_unita_doc),
                                                                                                                            XMLELEMENT
                                                                                                                                ("keyRecord",
                                                                                                                                    XMLELEMENT
                                                                                                                                        ("datoKey",
                                                                                                                                            XMLELEMENT("colonnaKey",'aa_min_registro_unita_doc'),
                                                                                                                                            XMLELEMENT("labelKey",'Anno minimo'),
                                                                                                                                            XMLELEMENT("valoreKey",periodi.aa_min_registro_unita_doc)
                                                                                                                                        )
                                                                                                                                ),
                                                                                                                            XMLELEMENT
                                                                                                                                ("datoRecord",
                                                                                                                                    XMLELEMENT("colonnaDato",'aa_max_registro_unita_doc'),
                                                                                                                                    XMLELEMENT("labelDato",'Anno massimo'),
--                                                                                                                                    XMLELEMENT("valoreDato", nvl(periodi.aa_max_registro_unita_doc, 'null') )
                                                                                                                                    XMLELEMENT("valoreDato", nvl(to_char(periodi.aa_max_registro_unita_doc),'null') )
                                                                                                                                ),
                                                                                                                            XMLELEMENT
                                                                                                                                ("datoRecord",
                                                                                                                                    XMLELEMENT("colonnaDato",'ds_formato_numero'),
                                                                                                                                    XMLELEMENT("labelDato",'Descrizione formato numero'),
                                                                                                                                    XMLELEMENT("valoreDato", nvl(periodi.ds_formato_numero,'null') )
                                                                                                                                )
                                                                                                                        )
                                                                                                                    ORDER BY periodi.aa_min_registro_unita_doc
                                                                                                                    )
                                                                                                                FROM  SACER.DEC_AA_REGISTRO_UNITA_DOC periodi
                                                                                                                WHERE periodi.id_registro_unita_doc = reg.id_registro_unita_doc
                                                                                                            )
                                                                                                    )
                                                                                                    -- FINE periodi di validità registro

                                                                                                )
                                                                                            ORDER BY reg.cd_registro_unita_doc
                                                                                            )
                                                                                    FROM  SACER.DEC_TIPO_UNITA_DOC_AMMESSO amm,
                                                                                          SACER.DEC_REGISTRO_UNITA_DOC reg
                                                                                    WHERE amm.id_tipo_unita_doc = tipo_ud.id_tipo_unita_doc
                                                                                    AND   reg.id_registro_unita_doc = amm.id_registro_unita_doc
                                                                                    )
                                                                                ),
                                                                                -- fine record child registri ammessi
                                                                            XMLELEMENT
                                                                                ("recordChild",
                                                                                    XMLELEMENT("tipoRecord",'Dati sistemi versanti'),
                                                                                    (SELECT XMLAGG
                                                                                            (XMLELEMENT
                                                                                                ("child",
                                                                                                    XMLELEMENT("idRecord", tipo_ud.ID_TIPO_UNITA_DOC),
                                                                                                    XMLELEMENT
                                                                                                        ("keyRecord",
                                                                                                            XMLELEMENT
                                                                                                                ("datoKey",
                                                                                                                    XMLELEMENT("colonnaKey",'id_record'),
                                                                                                                    XMLELEMENT("labelKey",'Id record'),
                                                                                                                    XMLELEMENT("valoreKey",tipo_ud.ID_TIPO_UNITA_DOC)
                                                                                                                )
                                                                                                        ),
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'dt_primo_versamento'),
                                                                                                            XMLELEMENT("labelDato",'Data primo versamento'),
                                                                                                            XMLELEMENT("valoreDato", (
                                                                                                                        SELECT  nvl(to_char(min(tot_tipo_ud.DT_RIF_CONTA),'DD/MM/YYYY'),'null') AS data_primo_versamento
                                                                                                                        FROM    SACER.mon_tipo_unita_doc_user_vers tot_tipo_ud
                                                                                                                        WHERE   tot_tipo_ud.ID_TIPO_UNITA_DOC=tipo_ud.ID_TIPO_UNITA_DOC
                                                                                                                ) -- Fine SELECT data_primo_versamento
                                                                                                            )
                                                                                                        ),
                                                                                                        -- INIZIO record child dei sistemi versanti    
                                                                                                    XMLELEMENT
                                                                                                        ("recordChild",
                                                                                                            XMLELEMENT("tipoRecord",'Sistemi versanti'),
                                                                                                            (SELECT XMLAGG
                                                                                                                    (XMLELEMENT
                                                                                                                        ("child",
                                                                                                                            XMLELEMENT("idRecord", tipo_ud.ID_TIPO_UNITA_DOC),
                                                                                                                            XMLELEMENT
                                                                                                                                ("keyRecord",
                                                                                                                                    XMLELEMENT
                                                                                                                                        ("datoKey",
                                                                                                                                            XMLELEMENT("colonnaKey",'id_record'),
                                                                                                                                            XMLELEMENT("labelKey",'Id record'),
                                                                                                                                            XMLELEMENT("valoreKey",tipo_ud.ID_TIPO_UNITA_DOC)
                                                                                                                                        )
                                                                                                                                ),
                                                                                                                            XMLELEMENT
                                                                                                                                ("datoRecord",
                                                                                                                                    XMLELEMENT("colonnaDato",'nm_sistema_versante'),
                                                                                                                                    XMLELEMENT("labelDato",'Nome sistema versante'),
                                                                                                                                    XMLELEMENT("valoreDato", nm_sistema_versante) 
                                                                                                                                    )
                                                                                                                                )
                                                                                                                ORDER BY nm_sistema_versante
                                                                                                                        )
                                                                                                            FROM (
                                                                                                                    SELECT  DISTINCT sist_vers.nm_sistema_versante AS nm_sistema_versante
                                                                                                                    FROM    SACER.mon_tipo_unita_doc_user_vers tot_tipo_ud
                                                                                                                    JOIN    sacer_iam.usr_user usr_sist_vers 
                                                                                                                      ON    (usr_sist_vers.id_user_iam = tot_tipo_ud.id_user_iam)
                                                                                                                    JOIN    sacer_iam.apl_sistema_versante sist_vers 
                                                                                                                      ON    (sist_vers.id_sistema_versante = usr_sist_vers.id_sistema_versante)
                                                                                                                    WHERE   tot_tipo_ud.ID_TIPO_UNITA_DOC=tipo_ud.ID_TIPO_UNITA_DOC
                                                                                                                )
                                                                                                            ) -- FINE record child dei sistemi versanti
                                                                                                        ), -- Fine elemento record child
                                                                                                    -- INIZIO record child degli utenti versatori    
                                                                                                    XMLELEMENT
                                                                                                        ("recordChild",
                                                                                                            XMLELEMENT("tipoRecord",'Utenti versatori'),
                                                                                                            (SELECT XMLAGG
                                                                                                                    (XMLELEMENT
                                                                                                                        ("child",
                                                                                                                            XMLELEMENT("idRecord", id_user_iam),
                                                                                                                            XMLELEMENT
                                                                                                                                ("keyRecord",
                                                                                                                                    XMLELEMENT
                                                                                                                                        ("datoKey",
                                                                                                                                            XMLELEMENT("colonnaKey",'nm_userid'),
                                                                                                                                            XMLELEMENT("labelKey",'Nome utente versatore'),
                                                                                                                                            XMLELEMENT("valoreKey",nm_userid)
                                                                                                                                        )
                                                                                                                                )
                                                                                                                        )
                                                                                                                ORDER BY nm_userid
                                                                                                                        )
                                                                                                            FROM (
                                                                                                                    SELECT  DISTINCT usr_sist_vers.nm_userid AS nm_userid,
                                                                                                                            usr_sist_vers.id_user_iam AS id_user_iam
                                                                                                                    FROM    SACER.mon_tipo_unita_doc_user_vers tot_tipo_ud
                                                                                                                    JOIN    sacer_iam.usr_user usr_sist_vers 
                                                                                                                      ON    (usr_sist_vers.id_user_iam = tot_tipo_ud.id_user_iam)
                                                                                                                    JOIN    sacer_iam.apl_sistema_versante sist_vers 
                                                                                                                      ON    (sist_vers.id_sistema_versante = usr_sist_vers.id_sistema_versante)
                                                                                                                    WHERE   tot_tipo_ud.ID_TIPO_UNITA_DOC=tipo_ud.ID_TIPO_UNITA_DOC
                                                                                                                )
                                                                                                        ) -- FINE record child degli utenti versatori
                                                                                                    ) -- Fine elemento record child

                                                                                                )
                                                                                            )
                                                                                        FROM DUAL
                                                                                    )
                                                                                ),
                                                                                -- fine record child Sistemi versanti                            
                                                                        
                                                                            -- inizio record child DEC_TIPO_STRUT_UNITA_DOC
                                                                            XMLELEMENT
                                                                                ("recordChild",
                                                                                    XMLELEMENT("tipoRecord",'Tipi struttura unità documentaria'),
                                                                                    (SELECT XMLAGG
                                                                                            (XMLELEMENT
                                                                                                ("child",
                                                                                                    XMLELEMENT("idRecord",strud.id_tipo_strut_unita_doc),
                                                                                                    XMLELEMENT
                                                                                                        ("keyRecord",
                                                                                                            XMLELEMENT
                                                                                                                ("datoKey",
                                                                                                                    XMLELEMENT("colonnaKey",'nm_tipo_strut_unita_doc'),
                                                                                                                    XMLELEMENT("labelKey",'Tipo struttura unità documentaria'),
                                                                                                                    XMLELEMENT("valoreKey",strud.nm_tipo_strut_unita_doc)
                                                                                                                )
                                                                                                        ),
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'ds_tipo_strut_unita_doc'),
                                                                                                            XMLELEMENT("labelDato",'Descrizione tipo struttura'),
                                                                                                            XMLELEMENT("valoreDato",strud.ds_tipo_strut_unita_doc)
                                                                                                        ),
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'dt_istituz'),
                                                                                                            XMLELEMENT("labelDato",'Data istituzione'),
                                                                                                            XMLELEMENT("valoreDato",to_char(strud.dt_istituz, 'dd/mm/yyyy') )
                                                                                                        ),
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'dt_soppres'),
                                                                                                            XMLELEMENT("labelDato",'Data soppressione'),
                                                                                                            XMLELEMENT("valoreDato",to_char(strud.dt_soppres, 'dd/mm/yyyy') )
                                                                                                        ),
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'ds_numero_tipo_strut_unita_doc'),
                                                                                                            XMLELEMENT("labelDato",'Numero tipo struttura ud'),
                                                                                                            XMLELEMENT("valoreDato",nvl(strud.ds_numero_tipo_strut_unita_doc, 'null') )
                                                                                                        ),
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'ds_anno_tipo_strut_unita_doc'),
                                                                                                            XMLELEMENT("labelDato",'Anno tipo struttura ud'),
                                                                                                            XMLELEMENT("valoreDato",nvl(strud.ds_anno_tipo_strut_unita_doc, 'null') )
                                                                                                        ),
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'ds_data_tipo_strut_unita_doc'),
                                                                                                            XMLELEMENT("labelDato",'Data tipo struttura ud'),
                                                                                                            XMLELEMENT("valoreDato",nvl(strud.ds_data_tipo_strut_unita_doc, 'null') )
                                                                                                        ),
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'ds_ogg_tipo_strut_unita_doc'),
                                                                                                            XMLELEMENT("labelDato",'Oggetto tipo struttura ud'),
                                                                                                            XMLELEMENT("valoreDato",nvl(strud.ds_ogg_tipo_strut_unita_doc, 'null') )
                                                                                                        ),
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'ds_rif_temp_tipo_strut_ud'),
                                                                                                            XMLELEMENT("labelDato",'Riferimento temporale tipo struttura ud'),
                                                                                                            XMLELEMENT("valoreDato",nvl(strud.ds_rif_temp_tipo_strut_ud, 'null') )
                                                                                                        ),
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'aa_min_tipo_strut_unita_doc'),
                                                                                                            XMLELEMENT("labelDato",'Anno inizio periodo di validità'),
                                                                                                            XMLELEMENT("valoreDato",nvl(to_char(strud.aa_min_tipo_strut_unita_doc), 'null') )
                                                                                                        ),
                                                                                                    XMLELEMENT
                                                                                                        ("datoRecord",
                                                                                                            XMLELEMENT("colonnaDato",'aa_max_tipo_strut_unita_doc'),
                                                                                                            XMLELEMENT("labelDato",'Anno fine periodo di validità'),
                                                                                                            XMLELEMENT("valoreDato",nvl(to_char(strud.aa_max_tipo_strut_unita_doc), 'null') )
                                                                                                        ),
                                                                                                    -- inizio record child DEC_TIPO_STRUT_UD_REG
                                                                                                    XMLELEMENT
                                                                                                        ("recordChild",
                                                                                                            XMLELEMENT("tipoRecord",'Registri tipo struttura ud'),
                                                                                                            (SELECT XMLAGG
                                                                                                                    (XMLELEMENT
                                                                                                                        ("child",
                                                                                                                            XMLELEMENT("idRecord",udReg.id_tipo_strut_ud_reg),
                                                                                                                            XMLELEMENT
                                                                                                                                ("keyRecord",
                                                                                                                                    XMLELEMENT
                                                                                                                                        ("datoKey",
                                                                                                                                            XMLELEMENT("colonnaKey",'cd_registro_unita_doc'),
                                                                                                                                            XMLELEMENT("labelKey",'Codice registro ud'),
                                                                                                                                            XMLELEMENT("valoreKey",regUd.cd_registro_unita_doc)
                                                                                                                                    )
                                                                                                                                ),
                                                                                                                            XMLELEMENT
                                                                                                                                ("datoRecord",
                                                                                                                                    XMLELEMENT("colonnaDato",'ds_registro_unita_doc'),
                                                                                                                                    XMLELEMENT("labelDato",'Descrizione registro'),
                                                                                                                                    XMLELEMENT("valoreDato", nvl(regUd.ds_registro_unita_doc, 'null') )
                                                                                                                                ),
                                                                                                                            XMLELEMENT
                                                                                                                                ("datoRecord",
                                                                                                                                    XMLELEMENT("colonnaDato",'dt_istituz'),
                                                                                                                                    XMLELEMENT("labelDato",'Data attivazione'),
                                                                                                                                    XMLELEMENT("valoreDato", nvl(to_char(regUd.dt_istituz, 'dd/mm/yyyy'), 'null') )
                                                                                                                                ),
                                                                                                                            XMLELEMENT
                                                                                                                                ("datoRecord",
                                                                                                                                    XMLELEMENT("colonnaDato",'dt_soppres'),
                                                                                                                                    XMLELEMENT("labelDato",'Descrizione registro'),
                                                                                                                                    XMLELEMENT("valoreDato", nvl(to_char(regUd.dt_soppres, 'dd/mm/yyyy'), 'null') )
                                                                                                                                )
                                                                                                                                ,
                                                                                                                            XMLELEMENT
                                                                                                                                ("datoRecord",
                                                                                                                                    XMLELEMENT("colonnaDato",'fl_registro_fisc'),
                                                                                                                                    XMLELEMENT("labelDato",'Registro fiscale'),
                                                                                                                                    XMLELEMENT("valoreDato", nvl(decode(regUd.fl_registro_fisc, '1', 'true', '0', 'false'), 'null') )
                                                                                                                                ),
                                                                                                                                -- Inizio periodi di validità registro
                                                                                                                                XMLELEMENT
                                                                                                                                    ("recordChild",
                                                                                                                                        XMLELEMENT("tipoRecord",'Periodi di validità'),
                                                                                                                                        (SELECT XMLAGG
                                                                                                                                                (XMLELEMENT
                                                                                                                                                    ("child",
                                                                                                                                                        XMLELEMENT("idRecord", periodi.id_aa_registro_unita_doc),
                                                                                                                                                        XMLELEMENT
                                                                                                                                                            ("keyRecord",
                                                                                                                                                                XMLELEMENT
                                                                                                                                                                    ("datoKey",
                                                                                                                                                                        XMLELEMENT("colonnaKey",'aa_min_registro_unita_doc'),
                                                                                                                                                                        XMLELEMENT("labelKey",'Anno minimo'),
                                                                                                                                                                        XMLELEMENT("valoreKey",periodi.aa_min_registro_unita_doc)
                                                                                                                                                                    )
                                                                                                                                                            ),
                                                                                                                                                        XMLELEMENT
                                                                                                                                                            ("datoRecord",
                                                                                                                                                                XMLELEMENT("colonnaDato",'aa_max_registro_unita_doc'),
                                                                                                                                                                XMLELEMENT("labelDato",'Anno massimo'),
                            --                                                                                                                                    XMLELEMENT("valoreDato", nvl(periodi.aa_max_registro_unita_doc, 'null') )
                                                                                                                                                                XMLELEMENT("valoreDato", nvl(to_char(periodi.aa_max_registro_unita_doc),'null') )
                                                                                                                                                            ),
                                                                                                                                                        XMLELEMENT
                                                                                                                                                            ("datoRecord",
                                                                                                                                                                XMLELEMENT("colonnaDato",'ds_formato_numero'),
                                                                                                                                                                XMLELEMENT("labelDato",'Descrizione formato numero'),
                                                                                                                                                                XMLELEMENT("valoreDato", nvl(periodi.ds_formato_numero,'null') )
                                                                                                                                                            )
                                                                                                                                                    )
                                                                                                                                                ORDER BY periodi.aa_min_registro_unita_doc
                                                                                                                                                )
                                                                                                                                            FROM  SACER.DEC_AA_REGISTRO_UNITA_DOC periodi
                                                                                                                                            WHERE periodi.id_registro_unita_doc = regUd.id_registro_unita_doc
                                                                                                                                        )
                                                                                                                                )
                                                                                                                                -- FINE periodi di validità registro

                                                                                                                        )
                                                                                                                    ORDER BY regUd.cd_registro_unita_doc
                                                                                                                    )
                                                                                                            FROM  SACER.DEC_TIPO_STRUT_UD_REG udReg, SACER.DEC_REGISTRO_UNITA_DOC regUd
                                                                                                            WHERE udReg.id_tipo_strut_unita_doc = strud.id_tipo_strut_unita_doc
                                                                                                            AND   udReg.id_registro_unita_doc = regUd.id_registro_unita_doc 
                                                                                                            )
                                                                                                        ), -- fine record child DEC_TIPO_STRUT_UD_REG                       
                                                                           
                                                                                                    -- inizio record child DEC_TIPO_DOC_AMMESSO
                                                                                                    XMLELEMENT
                                                                                                        ("recordChild",
                                                                                                            XMLELEMENT("tipoRecord",'Tipi documenti ammessi'),
                                                                                                            (SELECT XMLAGG
                                                                                                                    (XMLELEMENT
                                                                                                                        ("child",
                                                                                                                            XMLELEMENT("idRecord",tipo_doc_amm.id_tipo_doc_ammesso),
                                                                                                                            XMLELEMENT
                                                                                                                                ("keyRecord",
                                                                                                                                    XMLELEMENT
                                                                                                                                    ("datoKey",
                                                                                                                                        XMLELEMENT("colonnaKey",'nm_tipo_doc'),
                                                                                                                                        XMLELEMENT("labelKey",'Tipo documento'),
                                                                                                                                        XMLELEMENT("valoreKey",tipodoc.nm_tipo_doc)
                                                                                                                                    ),
                                                                                                                                    XMLELEMENT
                                                                                                                                        ("datoKey",
                                                                                                                                            XMLELEMENT("colonnaKey",'ti_doc'),
                                                                                                                                            XMLELEMENT("labelKey",'Elemento'),
                                                                                                                                            XMLELEMENT("valoreKey",tipo_doc_amm.ti_doc)
                                                                                                                                    )
                                                                                                                                ),
                                                                                                                            XMLELEMENT
                                                                                                                                ("datoRecord",
                                                                                                                                    XMLELEMENT("colonnaDato",'fl_obbl'),
                                                                                                                                    XMLELEMENT("labelDato",'Obbligatorio'),
                                                                                                                                    XMLELEMENT("valoreDato", DECODE(tipo_doc_amm.fl_obbl,'1','true','0','false') )
                                                                                                                                ),
                                                                                                                            XMLELEMENT
                                                                                                                                ("datoRecord",
                                                                                                                                    XMLELEMENT("colonnaDato",'ds_tipo_doc'),
                                                                                                                                    XMLELEMENT("labelDato",'Descrizione tipo documento'),
                                                                                                                                    XMLELEMENT("valoreDato", nvl(tipodoc.ds_tipo_doc, 'null') )
                                                                                                                                ),
                                                                                                                            XMLELEMENT
                                                                                                                                ("datoRecord",
                                                                                                                                    XMLELEMENT("colonnaDato",'dl_note_tipo_doc'),
                                                                                                                                    XMLELEMENT("labelDato",'Note tipo documento'),
                                                                                                                                    XMLELEMENT("valoreDato", nvl(tipodoc.dl_note_tipo_doc, 'null') )
                                                                                                                                ),
                                                                                                                                
                                                                                                                            -- Inizio record child DEC_XSD_DATI_SPEC tipo documento
                                                                                                                            XMLELEMENT
                                                                                                                                ("recordChild",
                                                                                                                                    XMLELEMENT("tipoRecord",'XSD dati specifici tipo documento'),
                                                                                                                                    (SELECT XMLAGG
                                                                                                                                            (XMLELEMENT
                                                                                                                                                ("child",
                                                                                                                                                    XMLELEMENT("idRecord",xsd.ID_XSD_DATI_SPEC),
                                                                                                                                                    XMLELEMENT
                                                                                                                                                        ("keyRecord",
                                                                                                                                                            XMLELEMENT
                                                                                                                                                                ("datoKey",
                                                                                                                                                                    XMLELEMENT("colonnaKey",'cd_versione_xsd'),
                                                                                                                                                                    XMLELEMENT("labelKey",'Versione XSD'),
                                                                                                                                                                    XMLELEMENT("valoreKey",xsd.cd_versione_xsd)
                                                                                                                                                                )
                                                                                                                                                        ),
                                                                                                                                                    XMLELEMENT
                                                                                                                                                        ("datoRecord",
                                                                                                                                                            XMLELEMENT("colonnaDato",'dt_istituz'),
                                                                                                                                                            XMLELEMENT("labelDato",'Data istituzione'),
                                                                                                                                                            XMLELEMENT("valoreDato",to_char(xsd.dt_istituz, 'dd/mm/yyyy') )
                                                                                                                                                        ),
                                                                                                                                                    XMLELEMENT
                                                                                                                                                        ("datoRecord",
                                                                                                                                                            XMLELEMENT("colonnaDato",'dt_soppres'),
                                                                                                                                                            XMLELEMENT("labelDato",'Data soppressione'),
                                                                                                                                                            XMLELEMENT("valoreDato",to_char(xsd.dt_soppres, 'dd/mm/yyyy') )
                                                                                                                                                        ),
                                                                                                                                                    XMLELEMENT
                                                                                                                                                        ("datoRecord",
                                                                                                                                                            XMLELEMENT("colonnaDato",'ds_versione_xsd'),
                                                                                                                                                            XMLELEMENT("labelDato",'Descrizione versione'),
                                                                                                                                                            XMLELEMENT("valoreDato",nvl(xsd.ds_versione_xsd, 'null'))
                                                                                                                                                        ),
                                                                                                                                                
                                                                                                                                                    -- Inizio record child DEC_ATTRIB_DATI_SPEC
                                                                                                                                                    XMLELEMENT
                                                                                                                                                        ("recordChild",
                                                                                                                                                            XMLELEMENT("tipoRecord",'Dati specifici tipo documento'),
                                                                                                                                                            (SELECT XMLAGG
                                                                                                                                                                    (XMLELEMENT
                                                                                                                                                                        ("child",
                                                                                                                                                                            XMLELEMENT("idRecord",xsdattrib.id_xsd_attrib_dati_spec),
                                                                                                                                                                            XMLELEMENT
                                                                                                                                                                                ("keyRecord",
                                                                                                                                                                                    XMLELEMENT
                                                                                                                                                                                        ("datoKey",
                                                                                                                                                                                            XMLELEMENT("colonnaKey",'nm_attrib_dati_spec'),
                                                                                                                                                                                            XMLELEMENT("labelKey",'Dato specifico'),
                                                                                                                                                                                            XMLELEMENT("valoreKey",attr.nm_attrib_dati_spec)
                                                                                                                                                                                        )
                                                                                                                                                                                ),
                                                                                                                                                                            XMLELEMENT
                                                                                                                                                                                ("datoRecord",
                                                                                                                                                                                    XMLELEMENT("colonnaDato",'ni_ord_attrib'),
                                                                                                                                                                                    XMLELEMENT("labelDato",'Numero ordine'),
                                                                                                                                                                                    XMLELEMENT("valoreDato",xsdattrib.ni_ord_attrib)
                                                                                                                                                                                ),
                                                                                                                                                                            XMLELEMENT
                                                                                                                                                                                ("datoRecord",
                                                                                                                                                                                    XMLELEMENT("colonnaDato",'ds_attrib_dati_spec'),
                                                                                                                                                                                    XMLELEMENT("labelDato",'Descrizione'),
                                                                                                                                                                                    XMLELEMENT("valoreDato",attr.ds_attrib_dati_spec)
                                                                                                                                                                                ),
                                                                                                                                                                            XMLELEMENT
                                                                                                                                                                                ("datoRecord",
                                                                                                                                                                                    XMLELEMENT("colonnaDato",'ti_attrib_dati_spec'),
                                                                                                                                                                                    XMLELEMENT("labelDato",'Tipo dato'),
                                                                                                                                                                                    XMLELEMENT("valoreDato",nvl(attr.ti_attrib_dati_spec, 'null'))
                                                                                                                                                                                )
                                                                                                                                                                        ) 
                                                                                                                                                                    ORDER BY xsdattrib.ni_ord_attrib
                                                                                                                                                                    )
                                                                                                                                                            FROM  SACER.DEC_XSD_ATTRIB_DATI_SPEC xsdattrib, SACER.DEC_ATTRIB_DATI_SPEC attr
                                                                                                                                                            WHERE xsdattrib.id_xsd_dati_spec = xsd.id_xsd_dati_spec
                                                                                                                                                            AND   attr.id_attrib_dati_spec = xsdattrib.id_attrib_dati_spec
                                                                                                                                                            AND   xsd.ti_uso_xsd = 'VERS'
                                                                                                                                                            AND   xsd.ti_entita_sacer = 'DOC'
                                                                                                                                                            )
                                                                                                                                                        )
                                                                                                                                                        -- Fine record child DEC_ATTRIB_DATI_SPEC
                                                                                                            
                                                                                                                                                ) 
                                                                                                                                            ORDER BY xsd.cd_versione_xsd
                                                                                                                                            )
                                                                                                                                    FROM  SACER.DEC_XSD_DATI_SPEC xsd
                                                                                                                                    WHERE xsd.id_tipo_doc = tipodoc.id_tipo_doc
                                                                                                                                    AND   xsd.ti_uso_xsd = 'VERS'
                                                                                                                                    AND   xsd.ti_entita_sacer = 'DOC'
                                                                                                                                    )
                                                                                                                                ) -- Fine record child DEC_XSD_DATI_SPEC
                                                                                                                                
                                                                                                                        )
                                                                                                                    ORDER BY tipo_doc_amm.ti_doc desc, tipodoc.nm_tipo_doc
                                                                                                                    )
                                                                                                            FROM  SACER.DEC_TIPO_DOC_AMMESSO tipo_doc_amm, SACER.DEC_TIPO_DOC tipodoc
                                                                                                            WHERE tipo_doc_amm.id_tipo_strut_unita_doc = strud.id_tipo_strut_unita_doc
                                                                                                            AND   tipo_doc_amm.id_tipo_doc = tipodoc.id_tipo_doc 
                                                                                                            )
                                                                                                        ) 
                                                                                                        -- fine record child DEC_TIPO_DOC_AMMESSO
                                                                                                )
                                                                                            ORDER BY strud.nm_tipo_strut_unita_doc
                                                                                            )
                                                                                    FROM  SACER.DEC_TIPO_STRUT_UNITA_DOC strud
                                                                                    WHERE strud.id_tipo_unita_doc = tipo_ud.id_tipo_unita_doc
--                                                                                    FETCH FIRST 1 ROWS ONLY -- Estrae solo il primo record che trova, di solito ce n'è sempre e solo uno.
                                                                                    )  --- fine TIPI struttura doc
                                                                                ),
                                                                                -- Fine record child DEC_TIPO_STRUT_UNITA_DOC
                                                                              
                                                                                -- Inizio record child DEC_XSD_DATI_SPEC
                                                                                XMLELEMENT
                                                                                    ("recordChild",
                                                                                        XMLELEMENT("tipoRecord",'XSD dati specifici tipo unità documentaria'),
                                                                                        (SELECT XMLAGG
                                                                                                (XMLELEMENT
                                                                                                    ("child",
                                                                                                        XMLELEMENT("idRecord",xsd.ID_XSD_DATI_SPEC),
                                                                                                        XMLELEMENT
                                                                                                            ("keyRecord",
                                                                                                                XMLELEMENT
                                                                                                                    ("datoKey",
                                                                                                                        XMLELEMENT("colonnaKey",'cd_versione_xsd'),
                                                                                                                        XMLELEMENT("labelKey",'Versione XSD'),
                                                                                                                        XMLELEMENT("valoreKey",xsd.cd_versione_xsd)
                                                                                                                    )
                                                                                                            ),
                                                                                                        XMLELEMENT
                                                                                                            ("datoRecord",
                                                                                                                XMLELEMENT("colonnaDato",'dt_istituz'),
                                                                                                                XMLELEMENT("labelDato",'Data istituzione'),
                                                                                                                XMLELEMENT("valoreDato",to_char(xsd.dt_istituz, 'dd/mm/yyyy') )
                                                                                                            ),
                                                                                                        XMLELEMENT
                                                                                                            ("datoRecord",
                                                                                                                XMLELEMENT("colonnaDato",'dt_soppres'),
                                                                                                                XMLELEMENT("labelDato",'Data soppressione'),
                                                                                                                XMLELEMENT("valoreDato",to_char(xsd.dt_soppres, 'dd/mm/yyyy') )
                                                                                                            ),
                                                                                                        XMLELEMENT
                                                                                                            ("datoRecord",
                                                                                                                XMLELEMENT("colonnaDato",'ds_versione_xsd'),
                                                                                                                XMLELEMENT("labelDato",'Descrizione versione'),
                                                                                                                XMLELEMENT("valoreDato", nvl(xsd.ds_versione_xsd, 'null' ))
                                                                                                            ),
                                                                                                    
                                                                                                        -- Inizio record child DEC_ATTRIB_DATI_SPEC
                                                                                                        XMLELEMENT
                                                                                                            ("recordChild",
                                                                                                                XMLELEMENT("tipoRecord",'Dati specifici tipo unità documentaria'),
                                                                                                                (SELECT XMLAGG
                                                                                                                        (XMLELEMENT
                                                                                                                            ("child",
                                                                                                                                XMLELEMENT("idRecord",xsdattrib.id_xsd_attrib_dati_spec),
                                                                                                                                XMLELEMENT
                                                                                                                                    ("keyRecord",
                                                                                                                                        XMLELEMENT
                                                                                                                                            ("datoKey",
                                                                                                                                                XMLELEMENT("colonnaKey",'nm_attrib_dati_spec'),
                                                                                                                                                XMLELEMENT("labelKey",'Dato specifico'),
                                                                                                                                                XMLELEMENT("valoreKey",attr.nm_attrib_dati_spec)
                                                                                                                                            )
                                                                                                                                    ),
                                                                                                                                XMLELEMENT
                                                                                                                                    ("datoRecord",
                                                                                                                                        XMLELEMENT("colonnaDato",'ni_ord_attrib'),
                                                                                                                                        XMLELEMENT("labelDato",'Numero ordine'),
                                                                                                                                        XMLELEMENT("valoreDato",xsdattrib.ni_ord_attrib)
                                                                                                                                    ),
                                                                                                                                XMLELEMENT
                                                                                                                                    ("datoRecord",
                                                                                                                                        XMLELEMENT("colonnaDato",'ds_attrib_dati_spec'),
                                                                                                                                        XMLELEMENT("labelDato",'Descrizione'),
                                                                                                                                        XMLELEMENT("valoreDato",attr.ds_attrib_dati_spec)
                                                                                                                                    ),
                                                                                                                                XMLELEMENT
                                                                                                                                    ("datoRecord",
                                                                                                                                        XMLELEMENT("colonnaDato",'ti_attrib_dati_spec'),
                                                                                                                                        XMLELEMENT("labelDato",'Tipo dato'),
                                                                                                                                        XMLELEMENT("valoreDato",nvl(attr.ti_attrib_dati_spec, 'null'))
                                                                                                                                    )
                                                                                                                            ) 
                                                                                                                        ORDER BY xsdattrib.ni_ord_attrib
                                                                                                                        )
                                                                                                                FROM  SACER.DEC_XSD_ATTRIB_DATI_SPEC xsdattrib, SACER.DEC_ATTRIB_DATI_SPEC attr
                                                                                                                WHERE xsdattrib.id_xsd_dati_spec = xsd.id_xsd_dati_spec
                                                                                                                AND   attr.id_attrib_dati_spec = xsdattrib.id_attrib_dati_spec
                                                                                                                AND   xsd.ti_uso_xsd = 'VERS'
                                                                                                                AND   xsd.ti_entita_sacer = 'UNI_DOC'
                                                                                                                )
                                                                                                            )
                                                                                                            -- Fine record child DEC_ATTRIB_DATI_SPEC
                                                                
                                                                                                    ) 
                                                                                                ORDER BY xsd.cd_versione_xsd
                                                                                                )
                                                                                        FROM  SACER.DEC_XSD_DATI_SPEC xsd
                                                                                        WHERE xsd.id_tipo_unita_doc = tipo_ud.id_tipo_unita_doc
                                                                                        AND   xsd.ti_uso_xsd = 'VERS'
                                                                                        AND   xsd.ti_entita_sacer = 'UNI_DOC'
                                                                                        )
                                                                                    ) 
                                                                                    -- Fine record child DEC_XSD_DATI_SPEC
                                                                        )
                                                                    ORDER BY tipo_ud.NM_TIPO_UNITA_DOC
                                                            )
                                                            FROM SACER.DEC_TIPO_UNITA_DOC tipo_ud
                                                            left join SACER.DEC_MODELLO_TIPO_SERIE modello
                                                             on (modello.id_modello_tipo_serie = tipo_ud.id_modello_tipo_serie)
                                                            
                                                            join SACER.DEC_CATEG_TIPO_UNITA_DOC sotto_categ
                                                             on (sotto_categ.id_categ_tipo_unita_doc = tipo_ud.id_categ_tipo_unita_doc)
                                                            join SACER.DEC_CATEG_TIPO_UNITA_DOC categ
                                                             on (categ.id_categ_tipo_unita_doc = sotto_categ.id_categ_tipo_unita_doc_padre)
                                                             
                                                            left join sacer_iam.org_tipo_servizio ti_serv_conserv
                                                             on (ti_serv_conserv.id_tipo_servizio = tipo_ud.id_tipo_servizio)
                                                            left join sacer_iam.org_tipo_servizio ti_serv_attiv
                                                             on (ti_serv_attiv.id_tipo_servizio = tipo_ud.id_tipo_servizio_attiv)
                                                            
                                                            WHERE       tipo_ud.ID_STRUT = strut.id_strut  
--                                                            ORDER BY    tipo_ud.nm_tipo_unita_doc
                                                        ) -- FINE XMLAGG per ogni child di Tipo UD
                                                    )
                                            FROM DUAL)

                                                ) AS FOTO 
                                FROM SACER.ORG_STRUT strut
                                join SACER.ORG_ENTE ente
                                 on (ente.id_ente = strut.id_ente)
                                join SACER.ORG_AMBIENTE amb
                                 on (amb.id_ambiente = ente.id_ambiente)
                                left join SACER.ORG_CATEG_STRUT cat
                                 on (cat.id_categ_strut = strut.id_categ_strut)
                                left join sacer_iam.ORG_ENTE_CONVENZ ente_convenz
                                 on (ente_convenz.id_ente_convenz = strut.id_ente_convenz)
                                left join sacer_iam.ORG_AMBIENTE_ENTE_CONVENZ amb_convenz
                                 on (amb_convenz.id_ambiente_ente_convenz = ente_convenz.id_ambiente_ente_convenz) 
                                WHERE   strut.id_strut = :ID_OGGETTO
                            ) -- foto STRUTTURA
                    ) -- fine foto STRUTTURA trasformata in recordChild
            