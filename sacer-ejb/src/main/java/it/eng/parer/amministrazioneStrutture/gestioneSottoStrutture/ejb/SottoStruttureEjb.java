/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.ejb;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.helper.SottoStruttureHelper;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecAttribDatiSpec;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.IamOrganizDaReplic;
import it.eng.parer.entity.OrgCampoValSubStrut;
import it.eng.parer.entity.OrgRegolaValSubStrut;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.OrgSubStrut;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.tablebean.OrgCampoValSubStrutTableBean;
import it.eng.parer.slite.gen.tablebean.OrgRegolaValSubStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgRegolaValSubStrutTableBean;
import it.eng.parer.slite.gen.tablebean.OrgSubStrutTableBean;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;

/**
 * EJB di gestione dei dati delle sottostrutture
 *
 * {@link it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture}
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({
        TransactionInterceptor.class })
public class SottoStruttureEjb {

    private static final Logger logger = LoggerFactory.getLogger(SottoStruttureEjb.class);

    @Resource
    private SessionContext context;
    @EJB
    private SottoStruttureHelper helper;
    @EJB
    private SacerLogEjb sacerLogEjb;
    @EJB
    private StruttureEjb struttureEjb;

    /**
     * Verifica che la struttura con id idStrut contenga solo la sotto struttura di default
     *
     * @param idStrut id struttura
     *
     * @return true/false
     *
     * @throws ParerUserError errore generico
     */
    public boolean checkDefaultOrgSubStrut(BigDecimal idStrut) throws ParerUserError {
        OrgStrut strut = helper.findById(OrgStrut.class, idStrut);
        return checkDefaultOrgSubStrut(strut);
    }

    public boolean checkDefaultOrgSubStrut(OrgStrut strut) throws ParerUserError {
        boolean isContained;
        if (strut != null) {
            if (strut.getOrgSubStruts() != null) {
                if (strut.getOrgSubStruts().isEmpty()) {
                    isContained = false;
                } else if (strut.getOrgSubStruts().size() > 1) {
                    isContained = false;
                } else if (!strut.getOrgSubStruts().get(0).getNmSubStrut()
                        .equals(CostantiDB.SubStruttura.DEFAULT_NAME)) {
                    isContained = false;
                } else {
                    isContained = true;
                }
            } else {
                isContained = false;
            }
        } else {
            throw new ParerUserError(
                    "Errore inaspettato durante il controllo delle sottostrutture di default. Si prega di ritentare l'operazione.");
        }
        return isContained;
    }

    public boolean existOrgSubStrut(String name, BigDecimal idStrut) {
        List<OrgSubStrut> listaSubStrut = helper.getOrgSubStrut(name, idStrut);
        return !listaSubStrut.isEmpty();
    }

    public OrgSubStrutTableBean getOrgSubStrutTableBean(BigDecimal idStrut) {
        OrgSubStrutTableBean tableBean = new OrgSubStrutTableBean();
        List<OrgSubStrut> listaSubStrut = helper.getOrgSubStrut(null, idStrut);
        if (listaSubStrut != null && !listaSubStrut.isEmpty()) {
            try {
                tableBean = (OrgSubStrutTableBean) Transform.entities2TableBean(listaSubStrut);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero delle sottostrutture "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return tableBean;
    }

    public OrgSubStrutTableBean getOrgSubStrutTableBeanAbilitate(long idUtente,
            BigDecimal idStrut) {
        OrgSubStrutTableBean tableBean = new OrgSubStrutTableBean();
        List<OrgSubStrut> listaSubStrut = helper.retrieveOrgSubStrutListAbilitate(idUtente,
                idStrut);
        if (listaSubStrut != null && !listaSubStrut.isEmpty()) {
            try {
                tableBean = (OrgSubStrutTableBean) Transform.entities2TableBean(listaSubStrut);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero delle sottostrutture "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return tableBean;
    }

    public boolean existUdInSubStrut(BigDecimal idSubStrut) {
        return helper.countUdInSubStrut(idSubStrut) > 0;
    }

    public OrgRegolaValSubStrutTableBean getOrgRegolaValSubStrutTableBean(BigDecimal idStrut,
            BigDecimal idSubStrut) {
        List<OrgRegolaValSubStrut> regoleList = helper.getOrgRegolaValSubStrut(idStrut, idSubStrut);
        OrgRegolaValSubStrutTableBean tableBean = new OrgRegolaValSubStrutTableBean();
        try {
            if (regoleList != null && !regoleList.isEmpty()) {
                for (OrgRegolaValSubStrut rule : regoleList) {
                    // 0 : OrgRegolaValSubStrut
                    OrgRegolaValSubStrutRowBean row = (OrgRegolaValSubStrutRowBean) Transform
                            .entity2RowBean(rule);
                    StringBuilder regolaComposita = new StringBuilder();
                    for (OrgCampoValSubStrut tmpCampo : rule.getOrgCampoValSubStruts()) {
                        // 1 : OrgCampoValSubStrut
                        regolaComposita
                                .append(tmpCampo.getTiCampo() + "." + tmpCampo.getNmCampo() + ";");
                    }
                    row.setString("regola_composita", StringUtils.chop(regolaComposita.toString()));
                    Date now = Calendar.getInstance().getTime();
                    if (rule.getDtSoppres().after(now)) {
                        row.setString("fl_attivo", "1");
                    } else {
                        row.setString("fl_attivo", "0");
                    }
                    row.setString("nm_tipo_unita_doc",
                            rule.getDecTipoUnitaDoc().getNmTipoUnitaDoc());
                    row.setString("nm_tipo_doc", rule.getDecTipoDoc().getNmTipoDoc());
                    tableBean.add(row);
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero delle regole "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tableBean;
    }

    public OrgRegolaValSubStrutTableBean getOrgRegolaValSubStrutTableBean(BigDecimal id,
            Constants.TipoDato tipoDato, boolean isFilterValid) {
        List<OrgRegolaValSubStrut> regoleList = helper.getOrgRegolaSubStrut(id, tipoDato,
                isFilterValid);
        OrgRegolaValSubStrutTableBean tableBean = new OrgRegolaValSubStrutTableBean();
        try {
            if (regoleList != null && !regoleList.isEmpty()) {
                for (OrgRegolaValSubStrut rule : regoleList) {
                    // 0 : OrgRegolaValSubStrut
                    OrgRegolaValSubStrutRowBean row = (OrgRegolaValSubStrutRowBean) Transform
                            .entity2RowBean(rule);
                    StringBuilder regolaComposita = new StringBuilder();
                    for (OrgCampoValSubStrut tmpCampo : rule.getOrgCampoValSubStruts()) {
                        // 1 : OrgCampoValSubStrut
                        regolaComposita
                                .append(tmpCampo.getTiCampo() + "." + tmpCampo.getNmCampo() + ";");
                    }
                    row.setString("regola_composita", StringUtils.chop(regolaComposita.toString()));
                    Date now = Calendar.getInstance().getTime();
                    if (rule.getDtSoppres().after(now)) {
                        row.setString("fl_attivo", "1");
                    } else {
                        row.setString("fl_attivo", "0");
                    }
                    row.setString("nm_tipo_unita_doc",
                            rule.getDecTipoUnitaDoc().getNmTipoUnitaDoc());
                    row.setString("nm_tipo_doc", rule.getDecTipoDoc().getNmTipoDoc());
                    tableBean.add(row);
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero delle regole "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tableBean;
    }

    public OrgCampoValSubStrutTableBean getOrgCampoValSubStrutTableBean(
            BigDecimal idOrgRegolaValSubStrut) {
        List<OrgCampoValSubStrut> campiList = helper.getOrgCampoValSubStrut(idOrgRegolaValSubStrut);
        OrgCampoValSubStrutTableBean tableBean = new OrgCampoValSubStrutTableBean();
        try {
            if (campiList != null && !campiList.isEmpty()) {
                tableBean = (OrgCampoValSubStrutTableBean) Transform.entities2TableBean(campiList);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei campi "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tableBean;
    }

    public boolean existOrgRegolaSubStrut(BigDecimal idRegolaValSubStrut, BigDecimal idTipoUnitaDoc,
            BigDecimal idTipoDoc, Date dtIstituz, Date dtSoppres) {
        return helper.existOrgRegolaSubStrut(idRegolaValSubStrut, idTipoUnitaDoc, idTipoDoc,
                dtIstituz, dtSoppres);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Long saveRegolaSubStrut(LogParam param, BigDecimal idTipoUnitaDoc, BigDecimal idTipoDoc,
            Date dtIstituz, Date dtSoppres) throws ParerUserError {
        DecTipoUnitaDoc tipoUnitaDoc = helper.findById(DecTipoUnitaDoc.class, idTipoUnitaDoc);
        DecTipoDoc tipoDoc = helper.findById(DecTipoDoc.class, idTipoDoc);
        if (tipoUnitaDoc == null || tipoDoc == null) {
            throw new ParerUserError(
                    "Errore inaspettato - impossibile inserire la regola con tipologia unit\u00E0 documentaria o tipo documento NULL");
        }
        OrgRegolaValSubStrut regola = new OrgRegolaValSubStrut();
        regola.setDecTipoDoc(tipoDoc);
        regola.setDecTipoUnitaDoc(tipoUnitaDoc);
        regola.setDtIstituz(dtIstituz);
        regola.setDtSoppres(dtSoppres);

        if (regola.getOrgCampoValSubStruts() == null) {
            regola.setOrgCampoValSubStruts(new ArrayList<OrgCampoValSubStrut>());
        }

        helper.insertEntity(regola, true);
        /*
         * Questo metodo può essere invocato dall'importazione parametri che non deve loggare il
         * TIPO UD
         */
        if (param != null) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                    param.getNomeUtente(), param.getNomeAzione(), param.getNomeTipoOggetto(),
                    param.getNomeTipoOggetto().equals(
                            SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA) ? idTipoUnitaDoc
                                    : idTipoDoc,
                    param.getNomePagina());
        }
        if (tipoDoc.getOrgRegolaValSubStruts() == null) {
            tipoDoc.setOrgRegolaValSubStruts(new ArrayList<OrgRegolaValSubStrut>());
        }
        if (tipoUnitaDoc.getOrgRegolaValSubStruts() == null) {
            tipoUnitaDoc.setOrgRegolaValSubStruts(new ArrayList<>());
        }
        tipoDoc.getOrgRegolaValSubStruts().add(regola);
        tipoUnitaDoc.getOrgRegolaValSubStruts().add(regola);

        return regola.getIdRegolaValSubStrut();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRegolaSubStrut(LogParam param, BigDecimal idRegolaValSubStrut, Date dtIstituz,
            Date dtSoppres, BigDecimal idTipoDoc) throws ParerUserError {
        OrgRegolaValSubStrut regola = helper.findById(OrgRegolaValSubStrut.class,
                idRegolaValSubStrut);
        if (regola == null) {
            throw new ParerUserError(
                    "Errore inaspettato - impossibile recuperare la regola indicata per la modifica");
        }

        if (regola.getDecTipoDoc().getIdTipoDoc() != idTipoDoc.longValue()) {
            DecTipoDoc tipoDoc = helper.findById(DecTipoDoc.class, idTipoDoc);
            if (tipoDoc == null) {
                throw new ParerUserError(
                        "Errore inaspettato - impossibile inserire la regola con tipo documento NULL");
            }
            regola.setDecTipoDoc(tipoDoc);
        }
        regola.setDtIstituz(dtIstituz);
        regola.setDtSoppres(dtSoppres);

        helper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                param.getNomeUtente(), param.getNomeAzione(), param.getNomeTipoOggetto(),
                param.getIdOggetto(), param.getNomePagina());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRegolaSubStrutFromTipoDoc(LogParam param, BigDecimal idRegolaValSubStrut,
            Date dtIstituz, Date dtSoppres, BigDecimal idTipoUnitaDoc) throws ParerUserError {
        OrgRegolaValSubStrut regola = helper.findById(OrgRegolaValSubStrut.class,
                idRegolaValSubStrut);
        if (regola == null) {
            throw new ParerUserError(
                    "Errore inaspettato - impossibile recuperare la regola indicata per la modifica");
        }

        if (regola.getDecTipoUnitaDoc().getIdTipoUnitaDoc() != idTipoUnitaDoc.longValue()) {
            DecTipoUnitaDoc tipoUnitaDoc = helper.findById(DecTipoUnitaDoc.class, idTipoUnitaDoc);
            if (tipoUnitaDoc == null) {
                throw new ParerUserError(
                        "Errore inaspettato - impossibile inserire la regola con tipo unita' documentaria NULL");
            }
            regola.setDecTipoUnitaDoc(tipoUnitaDoc);
        }
        regola.setDtIstituz(dtIstituz);
        regola.setDtSoppres(dtSoppres);

        helper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                param.getNomeUtente(), param.getNomeAzione(), param.getNomeTipoOggetto(),
                param.getIdOggetto(), param.getNomePagina());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteRegolaSubStrut(LogParam param, BigDecimal idRegolaValSubStrut)
            throws ParerUserError {
        OrgRegolaValSubStrut regola = helper.findById(OrgRegolaValSubStrut.class,
                idRegolaValSubStrut);
        if (regola == null) {
            throw new ParerUserError(
                    "Errore inaspettato - impossibile recuperare la regola indicata per la rimozione");
        }
        helper.removeEntity(regola, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                param.getNomeUtente(), param.getNomeAzione(), param.getNomeTipoOggetto(),
                param.getIdOggetto(), param.getNomePagina());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteCampoSubStrut(LogParam param, BigDecimal idCampoValSubStrut)
            throws ParerUserError {
        OrgCampoValSubStrut campo = helper.findById(OrgCampoValSubStrut.class, idCampoValSubStrut);
        if (campo == null) {
            throw new ParerUserError(
                    "Errore inaspettato - impossibile recuperare il campo indicato per la rimozione");
        }
        helper.removeEntity(campo, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                param.getNomeUtente(), param.getNomeAzione(), param.getNomeTipoOggetto(),
                param.getIdOggetto(), param.getNomePagina());
    }

    public boolean existOrgCampoSubStrut(BigDecimal idCampoValSubStrut,
            BigDecimal idRegolaValSubStrut, String tiCampo, String nmCampo, BigDecimal idRecord) {
        return helper.existOrgCampoSubStrut(idCampoValSubStrut, idRegolaValSubStrut, tiCampo,
                nmCampo, idRecord);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long saveCampoSubStrut(LogParam param, BigDecimal idRegolaValSubStrut, String tiCampo,
            String nmCampo, BigDecimal idRecord) throws ParerUserError {
        OrgRegolaValSubStrut regola = helper.findById(OrgRegolaValSubStrut.class,
                idRegolaValSubStrut);
        return saveCampoSubStrut(param, regola, tiCampo, nmCampo, idRecord);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Long saveCampoSubStrut(LogParam param, OrgRegolaValSubStrut regola, String tiCampo,
            String nmCampo, BigDecimal idRecord) throws ParerUserError {
        if (regola == null) {
            throw new ParerUserError(
                    "Errore inaspettato - impossibile recuperare la regola indicata per la modifica");
        }
        OrgCampoValSubStrut campo = new OrgCampoValSubStrut();
        campo.setOrgRegolaValSubStrut(regola);
        campo.setTiCampo(tiCampo);
        campo.setNmCampo(nmCampo);
        CostantiDB.TipoCampo campoEnum = CostantiDB.TipoCampo.valueOf(tiCampo);
        switch (campoEnum) {
        case DATO_PROFILO:
            break;
        case DATO_SPEC_DOC_PRINC:
        case DATO_SPEC_UNI_DOC:
            DecAttribDatiSpec attrib = helper.findById(DecAttribDatiSpec.class, idRecord);
            campo.setDecAttribDatiSpec(attrib);
            break;
        case SUB_STRUT:
            OrgSubStrut subStrut = helper.findById(OrgSubStrut.class, idRecord);
            campo.setOrgSubStrut(subStrut);
            break;
        }

        helper.insertEntity(campo, true);
        /*
         * Questo metodo può essere invocato dall'importazione parametri che non deve loggare il
         * TIPO UD
         */
        if (param != null) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                    param.getNomeUtente(), param.getNomeAzione(), param.getNomeTipoOggetto(),
                    param.getIdOggetto(), param.getNomePagina());
        }

        if (regola.getOrgCampoValSubStruts() == null) {
            regola.setOrgCampoValSubStruts(new ArrayList<>());
        }
        regola.getOrgCampoValSubStruts().add(campo);

        return campo.getIdCampoValSubStrut();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveCampoSubStrut(LogParam param, String tiCampo, String nmCampo,
            BigDecimal idRecord, BigDecimal idCampoValSubStrut) throws ParerUserError {
        OrgCampoValSubStrut campo = helper.findById(OrgCampoValSubStrut.class, idCampoValSubStrut);
        if (campo == null) {
            throw new ParerUserError(
                    "Errore inaspettato - impossibile recuperare il campo indicato per la modifica");
        }
        campo.setTiCampo(tiCampo);
        campo.setNmCampo(nmCampo);
        CostantiDB.TipoCampo campoEnum = CostantiDB.TipoCampo.valueOf(tiCampo);
        switch (campoEnum) {
        case DATO_PROFILO:
            campo.setDecAttribDatiSpec(null);
            campo.setOrgSubStrut(null);
            break;
        case DATO_SPEC_DOC_PRINC:
        case DATO_SPEC_UNI_DOC:
            DecAttribDatiSpec attrib = helper.findById(DecAttribDatiSpec.class, idRecord);
            campo.setDecAttribDatiSpec(attrib);
            campo.setOrgSubStrut(null);
            break;
        case SUB_STRUT:
            OrgSubStrut subStrut = helper.findById(OrgSubStrut.class, idRecord);
            campo.setOrgSubStrut(subStrut);
            campo.setDecAttribDatiSpec(null);
            break;
        }
        helper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                param.getNomeUtente(), param.getNomeAzione(), param.getNomeTipoOggetto(),
                param.getIdOggetto(), param.getNomePagina());
    }

    public void updateOrgSubStrut(LogParam param, String name, String desc, BigDecimal idSubStrut)
            throws ParerUserError {
        SottoStruttureEjb me = context.getBusinessObject(SottoStruttureEjb.class);
        Object[] datiRitorno = me.saveOrgSubStrut(param, name, desc, idSubStrut,
                StruttureEjb.TipoOper.MOD);
        if (datiRitorno[0] != null) {
            struttureEjb.replicateToIam((IamOrganizDaReplic) datiRitorno[0]);
        }
    }

    public Long insertOrgSubStrut(LogParam param, String name, String desc, BigDecimal idStrut)
            throws ParerUserError {
        SottoStruttureEjb me = context.getBusinessObject(SottoStruttureEjb.class);
        Object[] datiRitorno = me.saveOrgSubStrut(param, name, desc, idStrut,
                StruttureEjb.TipoOper.INS);
        if (datiRitorno[0] != null) {
            struttureEjb.replicateToIam((IamOrganizDaReplic) datiRitorno[0]);
        }
        return (Long) datiRitorno[1];
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Object[] saveOrgSubStrut(LogParam param, String name, String desc, BigDecimal id,
            StruttureEjb.TipoOper tipoOper) throws ParerUserError {
        ApplEnum.TiOperReplic tiOper = null;
        Object[] datiRitorno = new Object[2];
        OrgStrut struttura = new OrgStrut();
        boolean modificatiNomeDescrizione = false;

        /* INSERIMENTO SOTTO STRUTTURA */
        if (tipoOper.name().equals((StruttureEjb.TipoOper.INS.name()))) {
            struttura = helper.findById(OrgStrut.class, id);
            if (struttura == null) {
                throw new ParerUserError(
                        "Errore inaspettato - impossibile inserire la sottostruttura con struttura NULL");
            }
            OrgSubStrut subStrut = new OrgSubStrut();
            subStrut.setOrgStrut(struttura);
            subStrut.setNmSubStrut(name);
            subStrut.setDsSubStrut(desc);
            helper.insertEntity(subStrut, true);
            // Log applicativo della struttura
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                    param.getNomeUtente(), param.getNomeAzione(),
                    SacerLogConstants.TIPO_OGGETTO_STRUTTURA,
                    new BigDecimal(struttura.getIdStrut()), param.getNomePagina());
            struttura.getOrgSubStruts().add(subStrut);

            datiRitorno[1] = subStrut.getIdSubStrut();
            tiOper = ApplEnum.TiOperReplic.MOD;
            modificatiNomeDescrizione = true;
        }

        /* MODIFICA SOTTO STRUTTURA */
        if (tipoOper.name().equals((StruttureEjb.TipoOper.MOD.name()))) {
            OrgSubStrut subStrut = helper.findById(OrgSubStrut.class, id);

            if (subStrut == null) {
                throw new ParerUserError(
                        "Errore inaspettato - impossibile recuperare la sottostruttura indicata per la modifica");
            }

            /* Controllo se sono stati modificati nome e/o descrizione */
            if (!subStrut.getNmSubStrut().equals(name) || !subStrut.getDsSubStrut().equals(desc)) {
                modificatiNomeDescrizione = true;
            }

            if (name != null) {
                subStrut.setNmSubStrut(name);
            }
            subStrut.setDsSubStrut(desc);
            struttura = subStrut.getOrgStrut();
            tiOper = ApplEnum.TiOperReplic.MOD;
            // Log applicativo della struttura
            helper.getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                    param.getNomeUtente(), param.getNomeAzione(),
                    SacerLogConstants.TIPO_OGGETTO_STRUTTURA,
                    new BigDecimal(struttura.getIdStrut()), param.getNomePagina());
        }
        /*
         * La replica va fatta solo se la sottostruttura appartiene ad una struttura non
         * appartenente ad un ente di tipo template e se sono stati modificati nome e/o descrizione
         */
        IamOrganizDaReplic replic = null;
        if (modificatiNomeDescrizione && (struttura.getOrgEnte().getTipoDefTemplateEnte()
                .equals(CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_ENTE.name())
                || struttura.getOrgEnte().getTipoDefTemplateEnte()
                        .equals(CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name()))) {
            replic = struttureEjb.insertStrutIamOrganizDaReplic(struttura, tiOper);
        }
        datiRitorno[0] = replic;
        return datiRitorno;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteOrgSubStrut(LogParam param, long idSubStrut, boolean isFromDeleteStruttura)
            throws ParerUserError {
        SottoStruttureEjb me = context.getBusinessObject(SottoStruttureEjb.class);
        IamOrganizDaReplic replic = me.deleteSubStruttura(param, idSubStrut, isFromDeleteStruttura);
        if (replic != null) {
            struttureEjb.replicateToIam(replic);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public IamOrganizDaReplic deleteSubStruttura(LogParam param, long idSubStrut,
            boolean isFromDeleteStruttura) throws ParerUserError {
        OrgSubStrut subStrut = helper.findById(OrgSubStrut.class, idSubStrut);
        if (subStrut == null) {
            throw new ParerUserError(
                    "Errore inaspettato - impossibile recuperare la sottostruttura indicata per la rimozione");
        }
        String nmSubStrut = subStrut.getNmSubStrut();
        long idStrut = subStrut.getOrgStrut().getIdStrut();
        BigDecimal idStrutDaLoggare = new BigDecimal(subStrut.getOrgStrut().getIdStrut());
        helper.removeEntity(subStrut, true);
        // Log applicativo della struttura solo se non si proviene dalla cancellazione struttura
        // altrimenti logga anche se stessa!
        if (!isFromDeleteStruttura) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                    param.getNomeUtente(), param.getNomeAzione(),
                    SacerLogConstants.TIPO_OGGETTO_STRUTTURA, idStrutDaLoggare,
                    param.getNomePagina());
        }
        OrgStrut struttura = helper.findById(OrgStrut.class, idStrut);
        IamOrganizDaReplic replic = null;
        /*
         * La replica va fatta solo se la sottostruttura appartiene ad una struttura non
         * appartenente ad un ente di tipo template
         */
        if ((struttura.getOrgEnte().getTipoDefTemplateEnte()
                .equals(CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_ENTE.name())
                || struttura.getOrgEnte().getTipoDefTemplateEnte()
                        .equals(CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name()))
                && !isFromDeleteStruttura) {
            replic = struttureEjb.insertStrutIamOrganizDaReplic(struttura,
                    ApplEnum.TiOperReplic.MOD);
        }
        logger.info("Cancellazione sotto struttura " + nmSubStrut + " della struttura "
                + struttura.getIdStrut() + " avvenuta con successo!");
        return replic;
    }
}
