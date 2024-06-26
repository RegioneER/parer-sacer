#!/usr/bin/python
# -*- coding: utf-8 -*-
"""Genera la classe Java contenenete i messaggi dal file di properties."""

import re
import sys
import datetime
from xml.sax.saxutils import escape
from string import Template


def caricaTemplate():
    template = '''
/*
 * This class was automatically generated by
 * genJava.py (written by Francesco Fioravanti)
 *
 * File encoding: UTF-8
 *
 * THOU SHALT NOT EDIT THIS FILE!
 *
 */

 // Last update: ${lastupdate}

package it.eng.parer.ws.utils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class MessaggiWSBundle {

    // Definisce le costanti utilizzate per il bundle dei messaggi di errore
    private static final String BUNDLE_NAME = "messaggi_ws";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
    public static final String DEFAULT_LOCALE = "it";

    public static String getString(String key) {
        return RESOURCE_BUNDLE.getString(key);
    }

    public static String getString(String key, Object... params) {
        return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
    }

    // Le righe che seguono verranno mostrate raggruppate in Netbeans

    // <editor-fold defaultstate="collapsed" desc="COSTANTI DI ERRORE">
    ${messaggi}
    // </editor-fold>
}
    '''
    return template


pattRiga = re.compile(r'^([A-Z0-9\-]*)\s+=\s*(.+)$')
pattRigaErrNum = re.compile(r'^\d.+')
pattCommento = re.compile(r'^#\s*(.+)')

pattClasseJava = Template(caricaTemplate())
strList = []

for line in open(sys.argv[1]):
    outLinea = ""
    rigaprefix = ""
    jdoclinea = ""

    res = pattRiga.match(line)
    if res:
        riga = res.group(1)
        resErr = pattRigaErrNum.match(riga)
        if resErr:
            rigaprefix = "ERR_"
        jdoclinea = "\n\n    /**\n    * " \
            + escape(res.group(2).decode('unicode_escape')) + "\n    */"
        rigacnst = rigaprefix + riga.replace("-", "_")
        outLinea = jdoclinea + "\n    public static final String "\
            + rigacnst + " = \"" + riga + "\";"
    else:
        res2 = pattCommento.match(line)
        if res2:
            riga = res2.group(1)
            outLinea = "\n    // " + riga.rstrip().decode('unicode_escape')
    # strList.append(outLinea.encode('windows-1252'))
    strList.append(outLinea.encode('UTF-8'))

print (pattClasseJava.substitute(
  lastupdate=datetime.datetime.now(),
  messaggi=''.join(strList)))
