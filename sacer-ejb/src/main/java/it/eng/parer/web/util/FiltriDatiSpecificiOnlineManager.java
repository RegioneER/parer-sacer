package it.eng.parer.web.util;

import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecRowBean;
import it.eng.parer.web.dto.DecCriterioAttribBean;
import it.eng.parer.web.dto.DecCriterioDatiSpecBean;
import it.eng.parer.web.util.Constants.TipoEntitaSacer;
import it.eng.spagoIFace.Values;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Gilioli_P
 */
public class FiltriDatiSpecificiOnlineManager {
    // Metodo utilizzato per costruire l'interfaccia on-line sulla base
    // della lista di bean in memoria
    public static BaseTableInterface listBean2TableBeanPostAdd(List<DecCriterioDatiSpecBean> listaDatiSpec) {
        BaseTableInterface tabellaDatiSpec = new BaseTable();
        for (DecCriterioDatiSpecBean datoSpec : listaDatiSpec) {
            BaseRowInterface rigaDatoSpec = new BaseRow();
            rigaDatoSpec.setString("nm_attrib_dati_spec", datoSpec.getNmAttribDatiSpec());
            rigaDatoSpec.setString("ti_oper", datoSpec.getTiOper());
            rigaDatoSpec.setString("dl_valore", datoSpec.getDlValore());

            BaseRowInterface newRow = new BaseRow();
            if (rigaDatoSpec.getObject(Values.SUB_LIST) == null) {
                rigaDatoSpec.setObject(Values.SUB_LIST, new BaseTable());
            }

            List<DecCriterioAttribBean> definitoDa = datoSpec.getDecCriterioAttribs();
            for (DecCriterioAttribBean definitoRow : definitoDa) {
                String rigaDefinitoDa = "";
                if (definitoRow.getNmTipoUnitaDoc() != null && definitoRow.getNmSistemaMigraz() == null) {
                    rigaDefinitoDa = "Tipo unità doc.: " + definitoRow.getNmTipoUnitaDoc()
                            + (!definitoRow.getDsListaVersioniXsd().equals("")
                                    ? " (" + definitoRow.getDsListaVersioniXsd() + ")" : "");
                } else if (definitoRow.getNmTipoDoc() != null && definitoRow.getNmSistemaMigraz() == null) {
                    rigaDefinitoDa = "Tipo doc.: " + definitoRow.getNmTipoDoc()
                            + (!definitoRow.getDsListaVersioniXsd().equals("")
                                    ? " (" + definitoRow.getDsListaVersioniXsd() + ")" : "");
                } else if (definitoRow.getTiEntitaSacer().equals(TipoEntitaSacer.UNI_DOC.name())
                        && definitoRow.getNmSistemaMigraz() != null) {
                    rigaDefinitoDa = Constants.TI_SIS_MIGR_UD + ":" + " " + definitoRow.getNmSistemaMigraz()
                            + (!definitoRow.getDsListaVersioniXsd().equals("")
                                    ? " (" + definitoRow.getDsListaVersioniXsd() + ")" : "");
                } else if (definitoRow.getTiEntitaSacer().equals(TipoEntitaSacer.DOC.name())
                        && definitoRow.getNmSistemaMigraz() != null) {
                    rigaDefinitoDa = Constants.TI_SIS_MIGR_DOC + ":" + " " + definitoRow.getNmSistemaMigraz()
                            + (!definitoRow.getDsListaVersioniXsd().equals("")
                                    ? " (" + definitoRow.getDsListaVersioniXsd() + ")" : "");
                }
                newRow.setString("definito_da_record", rigaDefinitoDa);
                ((BaseTableInterface) rigaDatoSpec.getObject(Values.SUB_LIST)).add(newRow);
            }
            tabellaDatiSpec.add(rigaDatoSpec);
        }
        return tabellaDatiSpec;
    }

    public static BaseTableInterface listBean2TableBeanPostRemove(List<DecCriterioDatiSpecBean> listaDatiSpec) {
        BaseTableInterface tabellaDatiSpec = new BaseTable();
        Iterator it = listaDatiSpec.iterator();
        while (it.hasNext()) {
            DecCriterioDatiSpecBean datoSpecRow = (DecCriterioDatiSpecBean) it.next();
            if (datoSpecRow.getDecCriterioAttribs().size() > 0) {
                BaseRowInterface rigaDatoSpec = new BaseRow();
                rigaDatoSpec.setString("nm_attrib_dati_spec", datoSpecRow.getNmAttribDatiSpec());
                rigaDatoSpec.setString("ti_oper", datoSpecRow.getTiOper());
                rigaDatoSpec.setString("dl_valore", datoSpecRow.getDlValore());

                BaseRowInterface newRow = new BaseRow();
                if (rigaDatoSpec.getObject(Values.SUB_LIST) == null) {
                    rigaDatoSpec.setObject(Values.SUB_LIST, new BaseTable());
                }

                List<DecCriterioAttribBean> definitoDa = datoSpecRow.getDecCriterioAttribs();
                for (DecCriterioAttribBean definitoRow : definitoDa) {
                    String rigaDefinitoDa = "";
                    if (definitoRow.getNmTipoUnitaDoc() != null && definitoRow.getNmSistemaMigraz() == null) {
                        rigaDefinitoDa = "Tipo unità doc.: " + definitoRow.getNmTipoUnitaDoc()
                                + (!definitoRow.getDsListaVersioniXsd().equals("")
                                        ? " (" + definitoRow.getDsListaVersioniXsd() + ")" : "");
                    } else if (definitoRow.getNmTipoDoc() != null && definitoRow.getNmSistemaMigraz() == null) {
                        rigaDefinitoDa = "Tipo doc.: " + definitoRow.getNmTipoDoc()
                                + (!definitoRow.getDsListaVersioniXsd().equals("")
                                        ? " (" + definitoRow.getDsListaVersioniXsd() + ")" : "");
                    } else if (definitoRow.getTiEntitaSacer().equals(TipoEntitaSacer.UNI_DOC.name())
                            && definitoRow.getNmSistemaMigraz() != null) {
                        rigaDefinitoDa = Constants.TI_SIS_MIGR_UD + ":" + " " + definitoRow.getNmSistemaMigraz()
                                + (!definitoRow.getDsListaVersioniXsd().equals("")
                                        ? " (" + definitoRow.getDsListaVersioniXsd() + ")" : "");
                    } else if (definitoRow.getTiEntitaSacer().equals(TipoEntitaSacer.DOC.name())
                            && definitoRow.getNmSistemaMigraz() != null) {
                        rigaDefinitoDa = Constants.TI_SIS_MIGR_DOC + ":" + " " + definitoRow.getNmSistemaMigraz()
                                + (!definitoRow.getDsListaVersioniXsd().equals("")
                                        ? " (" + definitoRow.getDsListaVersioniXsd() + ")" : "");
                    }
                    newRow.setString("definito_da_record", rigaDefinitoDa);
                    ((BaseTableInterface) rigaDatoSpec.getObject(Values.SUB_LIST)).add(newRow);
                }

                tabellaDatiSpec.add(rigaDatoSpec);
            } else {
                it.remove();
            }
        }
        return tabellaDatiSpec;
    }

    public static void insertFiltroDatoSpecifico(DecAttribDatiSpecRowBean rigaDatoSpecifico,
            List<DecCriterioDatiSpecBean> listaDatiSpecOnLine, DecCriterioAttribBean criterioAttrib) {
        boolean giaPresente = false;
        List<DecCriterioAttribBean> totaleDefinitoDaList = new ArrayList();
        // Controllo se il dato specifico che sto trattando è già stato inserito
        // nella Lista Dati Specifici presentata a video
        if (!listaDatiSpecOnLine.isEmpty()) {
            for (int j = 0; j < listaDatiSpecOnLine.size(); j++) {
                if (listaDatiSpecOnLine.get(j).getNmAttribDatiSpec().equals(rigaDatoSpecifico.getNmAttribDatiSpec())) {
                    giaPresente = true;
                    // Se il dato specifico è già presente, ricavo la lista dei suoi totaliDefinitoDa
                    // e vi aggiungo ad essa il TIPO UNITA' DOCUMENTARIA
                    totaleDefinitoDaList = listaDatiSpecOnLine.get(j).getDecCriterioAttribs();
                    totaleDefinitoDaList.add(criterioAttrib);
                    // FIXME: Controllare l'ordinamento (vedi voce Ordinamenti di liste con "delegate" sulla wiki)
                    Collections.sort(totaleDefinitoDaList);
                    break;
                }
            }
        }

        // Se invece il dato specifico non è già presente, lo inserisco
        // e aggiunto l'informazione su dove è "Definito da"
        if (!giaPresente) {
            DecCriterioDatiSpecBean datoSpec = new DecCriterioDatiSpecBean();
            datoSpec.setNmAttribDatiSpec(rigaDatoSpecifico.getString("nm_attrib_dati_spec"));
            datoSpec.setTiOper(rigaDatoSpecifico.getString("ti_oper"));
            datoSpec.setDlValore(rigaDatoSpecifico.getString("dl_valore"));
            totaleDefinitoDaList.add(criterioAttrib);
            datoSpec.setDecCriterioAttribs(totaleDefinitoDaList);
            listaDatiSpecOnLine.add(datoSpec);
        }
    }
}
