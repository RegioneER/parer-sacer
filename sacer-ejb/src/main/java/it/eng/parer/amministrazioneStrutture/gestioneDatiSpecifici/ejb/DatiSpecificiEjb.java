package it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.helper.DatiSpecificiHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecAttribDatiSpec;
import it.eng.parer.entity.DecTipoCompDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.DecXsdAttribDatiSpec;
import it.eng.parer.entity.DecXsdDatiSpec;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecRowBean;
import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecTableBean;
import it.eng.parer.slite.gen.tablebean.DecXsdAttribDatiSpecRowBean;
import it.eng.parer.slite.gen.tablebean.DecXsdDatiSpecRowBean;
import it.eng.parer.slite.gen.tablebean.DecXsdDatiSpecTableBean;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EJB di gestione dei dati specifici
 *
 * {@link it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici}
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class DatiSpecificiEjb {

    private static final Logger logger = LoggerFactory.getLogger(DatiSpecificiEjb.class);

    @Resource
    private SessionContext context;
    @EJB
    private DatiSpecificiHelper helper;
    @EJB
    private SacerLogEjb sacerLogEjb;

    /**
     * Ritorna il tablebean contenente la lista degli xsd dati i parametri in input
     *
     * @param idStrut
     *            struttura
     * @param tiUsoXsd
     *            usoXsd
     * @param tiEntitaSacer
     *            entita
     * @param nmSistemaMigraz
     *            sistema migrazione
     * 
     * @return il tableBean contenente la lista
     */
    public DecXsdDatiSpecTableBean getDecXsdDatiSpecTableBean(BigDecimal idStrut, String tiUsoXsd, String tiEntitaSacer,
            String nmSistemaMigraz) {
        DecXsdDatiSpecTableBean table = new DecXsdDatiSpecTableBean();
        List<DecXsdDatiSpec> list = helper.retrieveDecXsdDatiSpecList(idStrut, tiUsoXsd, tiEntitaSacer,
                nmSistemaMigraz);
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecXsdDatiSpecTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero della lista di xsd per la struttura " + idStrut + " : "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException("Errore durante il recupero della lista di xsd");
            }
        }
        return table;
    }

    public DecAttribDatiSpecTableBean getDecAttribDatiSpecTableBeanFromXsd(BigDecimal idXsdDatiSpec) {
        DecAttribDatiSpecTableBean attribDatiSpecTableBean = new DecAttribDatiSpecTableBean();

        List<DecAttribDatiSpec> list = helper.retrieveDecAttribDatiSpecList(idXsdDatiSpec);
        try {
            if (!list.isEmpty()) {
                for (DecAttribDatiSpec attrib : list) {
                    DecAttribDatiSpecRowBean attribRow = (DecAttribDatiSpecRowBean) Transform.entity2RowBean(attrib);

                    attribDatiSpecTableBean.add(attribRow);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return attribDatiSpecTableBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insNewXsdDatiSpec(LogParam param, String cdVersione, String file,
            DecXsdDatiSpecRowBean xsdDatiSpecRowBean) throws ParerUserError {
        BigDecimal idXsdDatiSpec = insertDecXsdDatiSpec(xsdDatiSpecRowBean);
        saveXsdAttribList(file, idXsdDatiSpec, xsdDatiSpecRowBean);
        // Spostata qui la loggata altrimenti non fotografava bene i dati specifici
        String tipoEntitaSacer = xsdDatiSpecRowBean.getTiEntitaSacer();
        if (tipoEntitaSacer == null) {
            tipoEntitaSacer = "";
        }
        if (xsdDatiSpecRowBean.getIdTipoDoc() != null || xsdDatiSpecRowBean.getIdTipoUnitaDoc() != null
                || xsdDatiSpecRowBean.getIdTipoCompDoc() != null) {
            if (tipoEntitaSacer.equals("DOC")) {
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO,
                        xsdDatiSpecRowBean.getIdTipoDoc(), param.getNomePagina());
            } else if (tipoEntitaSacer.equals("UNI_DOC")) {
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA,
                        xsdDatiSpecRowBean.getIdTipoUnitaDoc(), param.getNomePagina());
            } else if (tipoEntitaSacer.contains("COMP")) {
                DecTipoCompDoc tipoCompDoc = (xsdDatiSpecRowBean.getIdTipoCompDoc() != null
                        ? helper.findById(DecTipoCompDoc.class, xsdDatiSpecRowBean.getIdTipoCompDoc()) : null);
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO,
                        new BigDecimal(tipoCompDoc.getDecTipoStrutDoc().getIdTipoStrutDoc()), param.getNomePagina());
            }
        }
    }

    private BigDecimal insertDecXsdDatiSpec(DecXsdDatiSpecRowBean xsdDatiSpecRowBean) throws ParerUserError {

        DecTipoDoc tipoDoc = (xsdDatiSpecRowBean.getIdTipoDoc() != null
                ? helper.findById(DecTipoDoc.class, xsdDatiSpecRowBean.getIdTipoDoc()) : null);
        DecTipoUnitaDoc tipoUnitaDoc = (xsdDatiSpecRowBean.getIdTipoUnitaDoc() != null
                ? helper.findById(DecTipoUnitaDoc.class, xsdDatiSpecRowBean.getIdTipoUnitaDoc()) : null);
        DecTipoCompDoc tipoCompDoc = (xsdDatiSpecRowBean.getIdTipoCompDoc() != null
                ? helper.findById(DecTipoCompDoc.class, xsdDatiSpecRowBean.getIdTipoCompDoc()) : null);
        String type = "";
        if (tipoDoc != null) {
            if (tipoDoc.getDecXsdDatiSpecs() == null) {
                tipoDoc.setDecXsdDatiSpecs(new ArrayList<DecXsdDatiSpec>());
            }
            xsdDatiSpecRowBean.setTiEntitaSacer("DOC");
            type = "documento";
        }

        if (tipoUnitaDoc != null) {
            if (tipoUnitaDoc.getDecXsdDatiSpecs() == null) {
                tipoUnitaDoc.setDecXsdDatiSpecs(new ArrayList<DecXsdDatiSpec>());
            }
            xsdDatiSpecRowBean.setTiEntitaSacer("UNI_DOC");
            type = "unit\u00E0 documentaria";
        }

        if (tipoCompDoc != null) {
            if (tipoCompDoc.getDecXsdDatiSpecs() == null) {
                tipoCompDoc.setDecXsdDatiSpecs(new ArrayList<DecXsdDatiSpec>());
            }
            if (tipoCompDoc.getTiUsoCompDoc().equals("CONTENUTO")) {
                xsdDatiSpecRowBean.setTiEntitaSacer("COMP");
            } else {
                xsdDatiSpecRowBean.setTiEntitaSacer("SUB_COMP");
            }
            type = "componente";
        }

        /*
         * Controllo se l'xsd \u00E8 per una migrazione all'interno della query i dati che lo indicano sono gi\u00E0
         * contenuti in XsdDatiSpec
         */
        if (helper.getDecXsdDatiSpecByVersion(xsdDatiSpecRowBean) != null) {
            throw new ParerUserError("Versione Xsd gi\u00E0 presente in memoria per il tipo " + type);
        }
        OrgStrut strut = helper.findById(OrgStrut.class, xsdDatiSpecRowBean.getIdStrut());

        DecXsdDatiSpec xsdDatiSpec = (DecXsdDatiSpec) Transform.rowBean2Entity(xsdDatiSpecRowBean);
        xsdDatiSpec.setDecTipoDoc(tipoDoc);
        xsdDatiSpec.setDecTipoUnitaDoc(tipoUnitaDoc);
        xsdDatiSpec.setDecTipoCompDoc(tipoCompDoc);
        xsdDatiSpec.setOrgStrut(strut);

        helper.insertEntity(xsdDatiSpec, true);
        BigDecimal idXsdDatiSpec = new BigDecimal(xsdDatiSpec.getIdXsdDatiSpec());

        if (tipoDoc != null && tipoDoc.getDecAttribDatiSpecs() == null) {
            tipoDoc.getDecXsdDatiSpecs().add(xsdDatiSpec);
        }

        if (tipoUnitaDoc != null && tipoUnitaDoc.getDecAttribDatiSpecs() == null) {
            tipoUnitaDoc.getDecXsdDatiSpecs().add(xsdDatiSpec);
        }
        if (tipoDoc != null && tipoDoc.getDecAttribDatiSpecs() == null) {
            tipoCompDoc.getDecXsdDatiSpecs().add(xsdDatiSpec);
        }

        return idXsdDatiSpec;
    }

    private void saveXsdAttribList(String blXsdTipoDoc, BigDecimal idXsdDatiSpec,
            DecXsdDatiSpecRowBean xsdDatiSpecRowBean) throws ParerUserError {

        DecAttribDatiSpecRowBean attribDatiSpecRowBean = new DecAttribDatiSpecRowBean();

        List<String> attributes = parseStringaXsd(blXsdTipoDoc);
        if (attributes.isEmpty()) {
            throw new ParerUserError("File Xsd non contenente attributi.</br>");
        }

        attribDatiSpecRowBean.setIdStrut(xsdDatiSpecRowBean.getIdStrut());
        attribDatiSpecRowBean.setTiEntitaSacer(xsdDatiSpecRowBean.getTiEntitaSacer());
        // se \u00E8 un attributo di xsd di migrazione
        if (xsdDatiSpecRowBean.getNmSistemaMigraz() != null) {

            attribDatiSpecRowBean.setTiUsoAttrib("MIGRAZ");
            attribDatiSpecRowBean.setNmSistemaMigraz(xsdDatiSpecRowBean.getNmSistemaMigraz());

        } else {
            if (xsdDatiSpecRowBean.getIdTipoDoc() != null) {
                attribDatiSpecRowBean.setIdTipoDoc(xsdDatiSpecRowBean.getIdTipoDoc());
            } else if (xsdDatiSpecRowBean.getIdTipoUnitaDoc() != null) {
                attribDatiSpecRowBean.setIdTipoUnitaDoc(xsdDatiSpecRowBean.getIdTipoUnitaDoc());
            } else if (xsdDatiSpecRowBean.getIdTipoCompDoc() != null) {
                attribDatiSpecRowBean.setIdTipoCompDoc(xsdDatiSpecRowBean.getIdTipoCompDoc());
            }
            attribDatiSpecRowBean.setTiUsoAttrib("VERS");
        }
        int order = 1;
        List<String> dbAttributes = helper.getNmAttribDatiSpecList(xsdDatiSpecRowBean.getIdStrut(),
                xsdDatiSpecRowBean.getIdTipoUnitaDoc(), xsdDatiSpecRowBean.getIdTipoDoc(),
                xsdDatiSpecRowBean.getIdTipoCompDoc(), xsdDatiSpecRowBean.getTiEntitaSacer(),
                xsdDatiSpecRowBean.getNmSistemaMigraz());
        /*
         * Salvo la lista degli attributi uno a uno Controllo che l'attributo non ci sia gi\u00E0, se c'\u00E8 non
         * importa inserirlo, devo solo inserire il nuovo riferimento in DecXsdAttribDatiSpec
         */
        List<String> controlList = new ArrayList<String>();

        for (String attr : attributes) {

            // controllo sui duplicati
            if (controlList.contains(attr)) {
                throw new ParerUserError("Xsd con attributi duplicati. Impossibile salvare");
            }
            BigDecimal idAttribDatiSpec = null;
            if (!dbAttributes.contains(attr)) {
                idAttribDatiSpec = salvaAttribDatiSpec(attr, attribDatiSpecRowBean);
            } else {
                // se l'attributo era gi\u00E0 presente in precedenti versioni ne prendo l'id
                attribDatiSpecRowBean.setNmAttribDatiSpec(attr);
                idAttribDatiSpec = (getDecAttribDatiSpecRowBean(attribDatiSpecRowBean).getIdAttribDatiSpec());
                dbAttributes.remove(attr);
            }

            // inserisco nella lista dei riferimenti
            if (idAttribDatiSpec != null) {
                // DecXsdAttribDatiSpecRowBean xsdAttribDatiSpecRowBean = new DecXsdAttribDatiSpecRowBean();
                insertDecXsdAttribDatiSpec(idXsdDatiSpec, idAttribDatiSpec, order);
            }
            controlList.add(attr);
            order = order + 5;
        }

    }

    private void insertDecXsdAttribDatiSpec(BigDecimal idXsdDatiSpec, BigDecimal idAttribDatiSpec, int order) {

        DecXsdAttribDatiSpec xsdAttribDatiSpec = new DecXsdAttribDatiSpec();

        DecXsdDatiSpec xsdDatiSpec = helper.findById(DecXsdDatiSpec.class, idXsdDatiSpec);
        DecAttribDatiSpec attribDatiSpec = helper.findById(DecAttribDatiSpec.class, idAttribDatiSpec);

        xsdAttribDatiSpec.setDecXsdDatiSpec(xsdDatiSpec);
        xsdAttribDatiSpec.setDecAttribDatiSpec(attribDatiSpec);
        xsdAttribDatiSpec.setNiOrdAttrib(new BigDecimal(order));

        helper.insertEntity(xsdAttribDatiSpec, true);
    }

    public DecAttribDatiSpecRowBean getDecAttribDatiSpecRowBean(BigDecimal idAttribDatiSpec) {
        DecAttribDatiSpec attrib = helper.findById(DecAttribDatiSpec.class, idAttribDatiSpec);
        DecAttribDatiSpecRowBean row = new DecAttribDatiSpecRowBean();
        if (attrib != null) {
            try {
                row = (DecAttribDatiSpecRowBean) Transform.entity2RowBean(attrib);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dell'attributo dati specifici "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException("Errore durante il recupero dell'attributo dati specifici");
            }
        }
        return row;
    }

    public DecAttribDatiSpecRowBean getDecAttribDatiSpecRowBean(BigDecimal idStrut, String nmAttribDatiSpec,
            BigDecimal idTipoDoc, BigDecimal idTipoUnitaDoc, BigDecimal idTipoCompDoc) {
        DecAttribDatiSpecRowBean attribDatiSpecRowBean = null;
        String tiEntitaSacer = null;
        if (idTipoUnitaDoc != null) {
            tiEntitaSacer = CostantiDB.TipiEntitaSacer.UNI_DOC.name();
        } else if (idTipoDoc != null) {
            tiEntitaSacer = CostantiDB.TipiEntitaSacer.DOC.name();
        } else if (idTipoCompDoc != null) {
            tiEntitaSacer = CostantiDB.TipiEntitaSacer.COMP.name();
        }
        DecAttribDatiSpec attribDatiSpec = helper.getDecAttribDatiSpecById(idStrut, nmAttribDatiSpec, tiEntitaSacer,
                CostantiDB.TipiUsoDatiSpec.VERS.name(), idTipoUnitaDoc, idTipoDoc, idTipoCompDoc);

        if (attribDatiSpec != null) {
            try {
                attribDatiSpecRowBean = (DecAttribDatiSpecRowBean) Transform.entity2RowBean(attribDatiSpec);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return attribDatiSpecRowBean;
    }

    public DecAttribDatiSpecRowBean getDecAttribDatiSpecRowBean(DecAttribDatiSpecRowBean attribDatiSpecRowBean) {
        final BigDecimal idTipoUnitaDoc = attribDatiSpecRowBean.getIdTipoUnitaDoc();
        final BigDecimal idTipoDoc = attribDatiSpecRowBean.getIdTipoDoc();
        final BigDecimal idTipoCompDoc = attribDatiSpecRowBean.getIdTipoCompDoc();
        DecAttribDatiSpec result = helper.getDecAttribDatiSpecById(attribDatiSpecRowBean.getIdStrut().longValue(),
                attribDatiSpecRowBean.getNmAttribDatiSpec(), attribDatiSpecRowBean.getTiEntitaSacer(),
                attribDatiSpecRowBean.getTiUsoAttrib(), (idTipoUnitaDoc != null ? idTipoUnitaDoc.longValue() : null),
                (idTipoDoc != null ? idTipoDoc.longValue() : null),
                (idTipoCompDoc != null ? idTipoCompDoc.longValue() : null), attribDatiSpecRowBean.getNmSistemaMigraz());

        try {
            attribDatiSpecRowBean = (DecAttribDatiSpecRowBean) Transform.entity2RowBean(result);
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }

        return attribDatiSpecRowBean;

    }

    /**
     * Metodo che inserisce un nuovo attributo per Tipo Documento
     *
     * @param attribute
     * @param order
     * 
     * @throws EMFError
     *
     */
    private BigDecimal salvaAttribDatiSpec(String attribute, DecAttribDatiSpecRowBean attribDatiSpecRowBean) {

        BigDecimal idAttribDatiSpec;

        attribDatiSpecRowBean.setNmAttribDatiSpec(attribute);
        attribDatiSpecRowBean.setDsAttribDatiSpec(attribute);

        idAttribDatiSpec = insertDecAttribDatiSpec(attribDatiSpecRowBean);

        return idAttribDatiSpec;
    }

    public BigDecimal insertDecAttribDatiSpec(DecAttribDatiSpecRowBean attribDatiSpecRowBean) {

        DecAttribDatiSpec attribDatiSpec;
        DecTipoDoc tipoDoc = (attribDatiSpecRowBean.getIdTipoDoc() != null
                ? helper.findById(DecTipoDoc.class, attribDatiSpecRowBean.getIdTipoDoc()) : null);
        DecTipoUnitaDoc tipoUnitaDoc = (attribDatiSpecRowBean.getIdTipoUnitaDoc() != null
                ? helper.findById(DecTipoUnitaDoc.class, attribDatiSpecRowBean.getIdTipoUnitaDoc()) : null);
        DecTipoCompDoc tipoCompDoc = (attribDatiSpecRowBean.getIdTipoCompDoc() != null
                ? helper.findById(DecTipoCompDoc.class, attribDatiSpecRowBean.getIdTipoCompDoc()) : null);

        if (tipoUnitaDoc != null) {
            if (tipoUnitaDoc.getDecAttribDatiSpecs() == null) {
                tipoUnitaDoc.setDecAttribDatiSpecs(new ArrayList<DecAttribDatiSpec>());
            }
            attribDatiSpecRowBean.setTiEntitaSacer("UNI_DOC");
        }
        if (tipoDoc != null) {
            if (tipoDoc.getDecAttribDatiSpecs() == null) {
                tipoDoc.setDecAttribDatiSpecs(new ArrayList<DecAttribDatiSpec>());
            }
            attribDatiSpecRowBean.setTiEntitaSacer("DOC");
        }
        if (tipoCompDoc != null) {
            if (tipoCompDoc.getDecAttribDatiSpecs() == null) {
                tipoCompDoc.setDecAttribDatiSpecs(new ArrayList<DecAttribDatiSpec>());
            }
            if (tipoCompDoc.getTiUsoCompDoc().equals("CONTENUTO")) {
                attribDatiSpecRowBean.setTiEntitaSacer("COMP");
            } else {
                attribDatiSpecRowBean.setTiEntitaSacer("SUB_COMP");
            }
        }

        OrgStrut strut = helper.findById(OrgStrut.class, attribDatiSpecRowBean.getIdStrut());
        BigDecimal idAttribDatiSpec = null;
        attribDatiSpec = (DecAttribDatiSpec) Transform.rowBean2Entity(attribDatiSpecRowBean);
        attribDatiSpec.setDecTipoUnitaDoc(tipoUnitaDoc);
        attribDatiSpec.setDecTipoDoc(tipoDoc);
        attribDatiSpec.setDecTipoCompDoc(tipoCompDoc);
        attribDatiSpec.setOrgStrut(strut);

        helper.insertEntity(attribDatiSpec, true);
        idAttribDatiSpec = new BigDecimal(attribDatiSpec.getIdAttribDatiSpec());
        if (tipoDoc != null && tipoDoc.getDecAttribDatiSpecs() == null) {
            tipoDoc.getDecAttribDatiSpecs().add(attribDatiSpec);
        }

        if (tipoUnitaDoc != null && tipoUnitaDoc.getDecAttribDatiSpecs() == null) {
            tipoUnitaDoc.getDecAttribDatiSpecs().add(attribDatiSpec);
        }
        if (tipoDoc != null && tipoDoc.getDecAttribDatiSpecs() == null) {
            tipoDoc.getDecAttribDatiSpecs().add(attribDatiSpec);
        }

        return idAttribDatiSpec;

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void delXsdDatiSpec(LogParam param, DecXsdDatiSpecRowBean xsdDatiSpecRowBean) throws ParerUserError {

        // Lista di tutti gli attributi che hanno stessi riferimenti di questo XSD
        List<DecAttribDatiSpec> listAll = helper.getPreviousVersionAttributesList(xsdDatiSpecRowBean.getIdXsdDatiSpec(),
                xsdDatiSpecRowBean.getIdTipoUnitaDoc(), xsdDatiSpecRowBean.getIdTipoDoc(),
                xsdDatiSpecRowBean.getIdTipoCompDoc());
        List<DecAttribDatiSpec> listXsd = helper.retrieveDecAttribDatiSpecList(xsdDatiSpecRowBean.getIdXsdDatiSpec());
        for (DecAttribDatiSpec attr : listXsd) {
            if (!listAll.contains(attr)) {
                // cancello solo l'attributo, il riferimento all'xsd verr\u00E0 cancellato in cascata
                DecAttribDatiSpec toRemove = helper.findById(DecAttribDatiSpec.class, attr.getIdAttribDatiSpec());
                helper.removeEntity(toRemove, true);
            }
        }
        deleteDecXsdDatiSpec(param, xsdDatiSpecRowBean);
        /*
         * Aggiunto per il logging...
         */
        DecTipoCompDoc tipoCompDoc = (xsdDatiSpecRowBean.getIdTipoCompDoc() != null
                ? helper.findById(DecTipoCompDoc.class, xsdDatiSpecRowBean.getIdTipoCompDoc()) : null);
        BigDecimal idTipoDoc = xsdDatiSpecRowBean.getIdTipoDoc();
        BigDecimal idTipoUnitaDoc = xsdDatiSpecRowBean.getIdTipoUnitaDoc();
        if (tipoCompDoc != null) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO,
                    new BigDecimal(tipoCompDoc.getDecTipoStrutDoc().getIdTipoStrutDoc()), param.getNomePagina());
        } else if (idTipoDoc != null) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO, idTipoDoc,
                    param.getNomePagina());
        } else if (idTipoUnitaDoc != null) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA, idTipoUnitaDoc,
                    param.getNomePagina());
        }
    }

    public void deleteDecXsdDatiSpec(LogParam param, DecXsdDatiSpecRowBean xsdDatiSpecDatiSpecRowBean)
            throws ParerUserError {
        DecXsdDatiSpec xsdDatiSpec = helper.findById(DecXsdDatiSpec.class,
                xsdDatiSpecDatiSpecRowBean.getIdXsdDatiSpec());
        long idXsdDatiSpec = xsdDatiSpec.getIdXsdDatiSpec();
        boolean notIsDecXsdDatiSpecCancellabile = helper.checkRelationsAreEmptyForDecXsdDatiSpec(idXsdDatiSpec);
        if (notIsDecXsdDatiSpecCancellabile) {
            throw new ParerUserError(
                    "Impossibile eliminare la versione del xsd: esiste almeno un elemento associato ad esso</br>");
        }
        helper.removeEntity(xsdDatiSpec, true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateXsdDatiSpec(LogParam param, String file, BigDecimal idXsdDatiSpec,
            DecXsdDatiSpecRowBean xsdDatiSpecRowBean) throws ParerUserError {
        updateDecXsdDatiSpec(idXsdDatiSpec, xsdDatiSpecRowBean);
        boolean xsdInUse = isXsdDatiSpecInUse(xsdDatiSpecRowBean);
        boolean campiRegoleInUso = helper.campiRegoleInUso(idXsdDatiSpec);
        if (StringUtils.isNotBlank(file)) {
            updateXsdAttribList(file, xsdInUse, campiRegoleInUso, idXsdDatiSpec, xsdDatiSpecRowBean);
        }
        // Parte di logging
        DecTipoCompDoc tipoCompDoc = (xsdDatiSpecRowBean.getIdTipoCompDoc() != null
                ? helper.findById(DecTipoCompDoc.class, xsdDatiSpecRowBean.getIdTipoCompDoc()) : null);
        BigDecimal idTipoDoc = xsdDatiSpecRowBean.getIdTipoDoc();
        BigDecimal idTipoUnitaDoc = xsdDatiSpecRowBean.getIdTipoUnitaDoc();

        if (tipoCompDoc != null) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO,
                    new BigDecimal(tipoCompDoc.getDecTipoStrutDoc().getIdTipoStrutDoc()), param.getNomePagina());
        } else if (idTipoDoc != null) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO, idTipoDoc,
                    param.getNomePagina());
        } else if (idTipoUnitaDoc != null) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA, idTipoUnitaDoc,
                    param.getNomePagina());
        }
    }

    public boolean isXsdDatiSpecInUse(DecXsdDatiSpecRowBean xsdDatiSpecRowBean) {

        boolean isInUse = false;

        if (xsdDatiSpecRowBean != null && xsdDatiSpecRowBean.getIdXsdDatiSpec() != null) {
            isInUse = helper.getUseOfXsdDatiSpec(xsdDatiSpecRowBean.getIdXsdDatiSpec().longValue());
        }

        return isInUse;
    }

    public boolean isXsdDatiSpecInUseInTipiSerie(BigDecimal idXsdDatiSpec) {
        Long count = helper.countXsdDatiSpecInUseInTipiSerie(idXsdDatiSpec);
        return count > 0;
    }

    private void updateDecXsdDatiSpec(BigDecimal idXsdDatiSpec, DecXsdDatiSpecRowBean xsdDatiSpecRowBean) {
        DecXsdDatiSpec xsdDatiSpec = helper.findById(DecXsdDatiSpec.class, idXsdDatiSpec);
        if (StringUtils.isNotBlank(xsdDatiSpecRowBean.getBlXsd())) {
            xsdDatiSpec.setBlXsd(xsdDatiSpecRowBean.getBlXsd());
        }
        xsdDatiSpec.setDtSoppres(xsdDatiSpecRowBean.getDtSoppres());
        xsdDatiSpec.setDsVersioneXsd(xsdDatiSpecRowBean.getDsVersioneXsd());
    }

    private void updateXsdAttribList(String blXsdTipoDoc, boolean xsdInUse, boolean campiInUso,
            BigDecimal idXsdDatiSpec, DecXsdDatiSpecRowBean xsdDatiSpecRowBean) throws ParerUserError {
        DecAttribDatiSpecRowBean attribDatiSpecRowBean = new DecAttribDatiSpecRowBean();
        List<String> attributes = parseStringaXsd(blXsdTipoDoc);
        if (attributes.isEmpty()) {
            throw new ParerUserError("File Xsd non contenente attributi.</br>");
        }
        List<String> dbAttributes = new ArrayList<String>();

        attribDatiSpecRowBean.setIdStrut(xsdDatiSpecRowBean.getIdStrut());
        // se \u00E8 un attributo di xsd di migrazione
        if (xsdDatiSpecRowBean.getNmSistemaMigraz() != null) {
            attribDatiSpecRowBean.setTiUsoAttrib("MIGRAZ");
            attribDatiSpecRowBean.setTiEntitaSacer(xsdDatiSpecRowBean.getTiEntitaSacer());
            attribDatiSpecRowBean.setNmSistemaMigraz(xsdDatiSpecRowBean.getNmSistemaMigraz());
        } else {
            if (xsdDatiSpecRowBean.getIdTipoDoc() != null) {
                attribDatiSpecRowBean.setIdTipoDoc(xsdDatiSpecRowBean.getIdTipoDoc());
            } else if (xsdDatiSpecRowBean.getIdTipoUnitaDoc() != null) {
                attribDatiSpecRowBean.setIdTipoUnitaDoc(xsdDatiSpecRowBean.getIdTipoUnitaDoc());
            } else if (xsdDatiSpecRowBean.getIdTipoCompDoc() != null) {
                attribDatiSpecRowBean.setIdTipoCompDoc(xsdDatiSpecRowBean.getIdTipoCompDoc());
            }
            attribDatiSpecRowBean.setTiUsoAttrib("VERS");
        }
        // Devo prendere la lista di tutti gli attributi di questa versione
        List<DecAttribDatiSpec> listDB = helper.getWhichAttributesList(idXsdDatiSpec,
                xsdDatiSpecRowBean.getIdTipoUnitaDoc(), xsdDatiSpecRowBean.getIdTipoDoc(),
                xsdDatiSpecRowBean.getIdTipoCompDoc(), false);
        for (DecAttribDatiSpec row : listDB) {
            dbAttributes.add(row.getNmAttribDatiSpec());
        }

        if (xsdInUse && !CollectionUtils.disjunction(attributes, dbAttributes).isEmpty()) {
            throw new ParerUserError("La lista di attributi non corrisponde con la lista presente.");
        }

        // se non in uso, cancello la lista associata a questo XSD
        if (!xsdInUse && !campiInUso) {

            List<DecAttribDatiSpec> listXsd = helper
                    .retrieveDecAttribDatiSpecList(xsdDatiSpecRowBean.getIdXsdDatiSpec());
            for (DecAttribDatiSpec attr : listXsd) {
                BigDecimal idAttribDatiSpec = new BigDecimal(attr.getIdAttribDatiSpec());
                if (!listDB.contains(attr)) {

                    // cancello solo l'attributo, il riferimento all'xsd verr\u00E0 cancellato in cascata
                    // DecAttribDatiSpec toRemove = struttureHelper.getDecAttribDatiSpecById(idAttribDatiSpec);
                    DecAttribDatiSpec toRemove = helper.findById(DecAttribDatiSpec.class, idAttribDatiSpec);
                    helper.removeEntity(toRemove, true);
                } else {
                    // cancello solo il riferimento
                    DecXsdAttribDatiSpec xsdAttribDatiSpec = helper.getDecXsdAttribDatiSpecByAttrib(idAttribDatiSpec,
                            xsdDatiSpecRowBean.getIdXsdDatiSpec());
                    helper.removeEntity(xsdAttribDatiSpec, true);
                }
            }
        }

        // TODO: cancellare i controlli sugli attributi soppressi
        List<String> controlList = new ArrayList<String>();
        int order = 1;

        for (String attr : attributes) {
            // controllo sui duplicati
            if (controlList.contains(attr)) {
                throw new ParerUserError("Xsd con attributi duplicati. Impossibile salvare");
            }
            // se Xsd non in uso, allora la lista \u00E8 vuota, devo inserire sicuramente il nuovo riferimento
            // se l'attributo \u00E8 nuovo, inserisco anche l'attributo
            if (!xsdInUse) {

                BigDecimal idAttribDatiSpec = null;
                // se \u00E8 nuovo
                if (!dbAttributes.contains(attr)) {
                    // inserisco e ricavo id per inserire anche il riferimento
                    idAttribDatiSpec = salvaAttribDatiSpec(attr, attribDatiSpecRowBean);
                    // se non nuovo, ricavo id
                } else {
                    idAttribDatiSpec = (getDecAttribDatiSpecRowBean(xsdDatiSpecRowBean.getIdStrut(), attr,
                            attribDatiSpecRowBean.getIdTipoDoc(), attribDatiSpecRowBean.getIdTipoUnitaDoc(),
                            attribDatiSpecRowBean.getIdTipoCompDoc())).getIdAttribDatiSpec();
                }
                // inserisco riferimento a Xsd
                if (idAttribDatiSpec != null) {
                    insertDecXsdAttribDatiSpec(idXsdDatiSpec, idAttribDatiSpec, order);
                }
                // se Xsd in uso, allora devo semplicemente aggiornare il numero ordine (unica modifica consentita)
            } else {

                attribDatiSpecRowBean.setNmAttribDatiSpec(attr);
                updateDecAttribDatiSpec(attribDatiSpecRowBean, idXsdDatiSpec, order);
                // rimuovo dalla lista degli attributi nel DB

            }
            dbAttributes.remove(attr);
            controlList.add(attr);
            order += 5;
        }

    }

    public void updateDecAttribDatiSpec(DecAttribDatiSpecRowBean attribDatiSpecRowBean, BigDecimal idXsdDatiSpec,
            int nrOrd) {
        DecXsdAttribDatiSpec xsdAttribDatiSpec;

        if (attribDatiSpecRowBean.getNmSistemaMigraz() != null) {
            BigDecimal idStrut = attribDatiSpecRowBean.getIdStrut();
            String nmSistemaMigraz = attribDatiSpecRowBean.getNmSistemaMigraz();
            String tiSacerType = attribDatiSpecRowBean.getTiEntitaSacer();
            xsdAttribDatiSpec = helper.getMigrazDecXsdAttribDatiSpecByNameAndXsdId(
                    attribDatiSpecRowBean.getNmAttribDatiSpec(), idStrut, nmSistemaMigraz, tiSacerType, idXsdDatiSpec);
        } else {
            BigDecimal idTipoDoc = attribDatiSpecRowBean.getIdTipoDoc();
            BigDecimal idTipoUnitaDoc = attribDatiSpecRowBean.getIdTipoUnitaDoc();
            BigDecimal idTipoCompDoc = attribDatiSpecRowBean.getIdTipoCompDoc();

            xsdAttribDatiSpec = helper.getDecXsdAttribDatiSpecByNameAndXsdId(
                    attribDatiSpecRowBean.getNmAttribDatiSpec(), idTipoDoc, idTipoUnitaDoc, idTipoCompDoc,
                    idXsdDatiSpec);
        }
        if (xsdAttribDatiSpec != null && nrOrd != 0) {
            xsdAttribDatiSpec.setNiOrdAttrib(new BigDecimal(nrOrd));
            helper.getEntityManager().flush();
        }
    }

    public List<String> parseStringaXsd(String stringaFile) throws ParerUserError {

        List<String> attributes = new ArrayList<String>();

        ByteArrayInputStream bais = null;

        if (!stringaFile.isEmpty()) {
            bais = new ByteArrayInputStream(stringaFile.getBytes());

        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();

            Document doc;

            doc = db.parse(bais);
            NodeList nl = doc.getElementsByTagNameNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
            for (int it = 0; it < nl.getLength(); it++) {
                Node n = nl.item(it);
                NamedNodeMap map = n.getAttributes();

                if (!(map.getNamedItem("name").getNodeValue().equals("VersioneDatiSpecifici"))
                        && !(map.getNamedItem("name").getNodeValue().equals("DatiSpecifici"))
                        && !(map.getNamedItem("name").getNodeValue().equals("DatiSpecificiMigrazione"))) {
                    // viene verificato che il tag che si intende registrare non inizi o termini con
                    // il carattere <spazio>. Dal punto di vista XSD questo sarebbe corretto e
                    // definisce un tag XML senza gli spazi all'inizio e alla fine,
                    // ma la tabella di decodifica verrebbe a riportare il tag "con lo spazio".
                    // in sede di versamento questo crea un'incongruenza che blocca il versamento.
                    String tmpAttrname = map.getNamedItem("name").getNodeValue();
                    if (tmpAttrname.trim().equals(tmpAttrname)) {
                        attributes.add(tmpAttrname);
                    } else {
                        throw new ParerUserError("Operazione non effettuata: il tag [" + tmpAttrname
                                + "] del documento XSD che si sta cercando di caricare "
                                + "non può iniziare o terminare con caratteri di spaziatura</br>");
                    }
                }
            }
        } catch (SAXException e) {
            throw new ParerUserError("Operazione non effettuata: file non ben formato " + e.toString() + "</br>");
        } catch (IOException e) {
            throw new ParerUserError("Errore IO - Operazione non effettuata: " + e.toString() + "</br>");
        } catch (ParserConfigurationException e) {
            throw new ParerUserError(
                    "Errore ParserConfiguration - Operazione non effettuata: " + e.toString() + "</br>");
        }

        return attributes;
    }

    public DecXsdAttribDatiSpecRowBean getDecXsdAttribDatiSpecRowBeanByAttrib(BigDecimal idAttribDatiSpec,
            BigDecimal idXsdDatiSpec) {
        DecXsdAttribDatiSpecRowBean xsdAttribDatiSpecRowBean = new DecXsdAttribDatiSpecRowBean();
        DecXsdAttribDatiSpec xsdAttribDatiSpec = helper.getDecXsdAttribDatiSpecByAttrib(idAttribDatiSpec,
                idXsdDatiSpec);

        try {
            xsdAttribDatiSpecRowBean = (DecXsdAttribDatiSpecRowBean) Transform.entity2RowBean(xsdAttribDatiSpec);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return xsdAttribDatiSpecRowBean;
    }

    public BigDecimal getDecAttribNrOrd(DecAttribDatiSpecRowBean row, DecXsdDatiSpecRowBean xsdDatiSpecRowBean) {

        DecXsdAttribDatiSpec attrib = new DecXsdAttribDatiSpec();
        attrib = helper.getDecXsdAttribDatiSpecByAttrib(row.getIdAttribDatiSpec(),
                xsdDatiSpecRowBean.getIdXsdDatiSpec());

        return attrib.getNiOrdAttrib();
    }

    public DecXsdDatiSpecTableBean getDecXsdDatiSpecTableBean(DecXsdDatiSpecRowBean xsdDatiSpecRowBean) {
        DecXsdDatiSpecTableBean xsdDatiSpecTableBean = new DecXsdDatiSpecTableBean();
        List<DecXsdDatiSpec> list = helper.retrieveDecXsdDatiSpecList(xsdDatiSpecRowBean.getIdStrut(),
                xsdDatiSpecRowBean.getTiUsoXsd(), xsdDatiSpecRowBean.getTiEntitaSacer(),
                xsdDatiSpecRowBean.getIdTipoUnitaDoc(), xsdDatiSpecRowBean.getIdTipoDoc(),
                xsdDatiSpecRowBean.getIdTipoCompDoc());
        try {
            if (!list.isEmpty()) {
                xsdDatiSpecTableBean = (DecXsdDatiSpecTableBean) Transform.entities2TableBean(list);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return xsdDatiSpecTableBean;

    }

    public DecXsdDatiSpecRowBean getLastDecXsdDatiSpecRowBean(BigDecimal idStrut, BigDecimal idTipoUnitaDoc,
            BigDecimal idTipoDoc, BigDecimal idTipoCompDoc) {
        DecXsdDatiSpec row = null;
        DecXsdDatiSpecRowBean rowBean = null;
        if (idTipoUnitaDoc != null) {
            row = helper.getLastDecXsdDatiSpecForTipoUnitaDoc(idStrut.longValue(), idTipoUnitaDoc.longValue());
        } else if (idTipoDoc != null) {
            row = helper.getLastDecXsdDatiSpecForTipoDoc(idStrut.longValue(), idTipoDoc.longValue());
        } else if (idTipoCompDoc != null) {
            row = helper.getLastDecXsdDatiSpecForTipoCompDoc(idStrut.longValue(), idTipoCompDoc.longValue());
        }
        try {
            if (row != null) {
                rowBean = (DecXsdDatiSpecRowBean) Transform.entity2RowBean(row);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Errore nel recupero del xsd dei dati specifici");
        }
        return rowBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDsDecAttribDatiSpec(LogParam param, BigDecimal idAttribDatiSpec, String dsAttribDatiSpec)
            throws EMFError {
        DecAttribDatiSpec attribDatiSpec = helper.findById(DecAttribDatiSpec.class, idAttribDatiSpec);
        attribDatiSpec.setDsAttribDatiSpec(dsAttribDatiSpec);
        helper.getEntityManager().flush();

        if (attribDatiSpec.getDecTipoUnitaDoc() != null) {
            BigDecimal idTipoUnitaDoc = new BigDecimal(attribDatiSpec.getDecTipoUnitaDoc().getIdTipoUnitaDoc());
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA, idTipoUnitaDoc,
                    param.getNomePagina());
        } else if (attribDatiSpec.getDecTipoDoc() != null) {
            BigDecimal idTipoDoc = new BigDecimal(attribDatiSpec.getDecTipoDoc().getIdTipoDoc());
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO, idTipoDoc,
                    param.getNomePagina());
        } else {
            DecTipoCompDoc compDoc = helper.findById(DecTipoCompDoc.class,
                    attribDatiSpec.getDecTipoCompDoc().getIdTipoCompDoc());
            BigDecimal idTipoStrutDoc = new BigDecimal(compDoc.getDecTipoStrutDoc().getIdTipoStrutDoc());
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO, idTipoStrutDoc,
                    param.getNomePagina());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteDecXsdDatiSpecMigraz(long idStrut) throws ParerUserError {
        helper.deleteDecXsdDatiSpecMigraz(idStrut);
        logger.info("Cancellazione XSD dati specifici di migrazione della struttura " + idStrut
                + " avvenuta con successo!");
    }

    public DecAttribDatiSpecTableBean getDecAttribDatiSpecCombo(BigDecimal idStrut, String tiEntitaSacer,
            BigDecimal idEntity) {
        List<DecAttribDatiSpec> attributi = helper.getDecAttribDatiSpecList(idStrut, tiEntitaSacer, idEntity);
        DecAttribDatiSpecTableBean tableBean = new DecAttribDatiSpecTableBean();
        try {
            if (attributi != null && !attributi.isEmpty()) {
                tableBean = (DecAttribDatiSpecTableBean) Transform.entities2TableBean(attributi);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei documenti " + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tableBean;
    }

    public boolean isXsdDatiSpecInUseOnCampi(BigDecimal idXsdDatiSpec, String... tipiCampo) {
        return helper.existsCampoSuXsdDatiSpec(idXsdDatiSpec, tipiCampo);
    }

    public DecXsdDatiSpecTableBean getXsdDatiSpecTableBeanByTipoEntita(long idTipoDato,
            Constants.TipoEntitaSacer tiEntitaSacer) {
        DecXsdDatiSpecTableBean xsdDatiSpecTableBean = new DecXsdDatiSpecTableBean();
        try {
            switch (tiEntitaSacer) {
            case UNI_DOC:
                DecTipoUnitaDoc tipoUnitaDoc = helper.findById(DecTipoUnitaDoc.class, idTipoDato);
                if (tipoUnitaDoc.getDecXsdDatiSpecs() != null && !tipoUnitaDoc.getDecXsdDatiSpecs().isEmpty()) {
                    xsdDatiSpecTableBean = (DecXsdDatiSpecTableBean) Transform
                            .entities2TableBean(tipoUnitaDoc.getDecXsdDatiSpecs());
                }
                break;
            case DOC:
                DecTipoDoc tipoDoc = helper.findById(DecTipoDoc.class, idTipoDato);
                if (tipoDoc.getDecXsdDatiSpecs() != null && !tipoDoc.getDecXsdDatiSpecs().isEmpty()) {
                    xsdDatiSpecTableBean = (DecXsdDatiSpecTableBean) Transform
                            .entities2TableBean(tipoDoc.getDecXsdDatiSpecs());
                }
                break;
            case COMP:
                DecTipoCompDoc tipoCompDoc = helper.findById(DecTipoCompDoc.class, idTipoDato);
                if (tipoCompDoc.getDecXsdDatiSpecs() != null && !tipoCompDoc.getDecXsdDatiSpecs().isEmpty()) {
                    xsdDatiSpecTableBean = (DecXsdDatiSpecTableBean) Transform
                            .entities2TableBean(tipoCompDoc.getDecXsdDatiSpecs());
                }
                break;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return xsdDatiSpecTableBean;
    }

    public void deactivateXsdAndLog(LogParam param, BigDecimal idXsdDatiSpec) {
        context.getBusinessObject(DatiSpecificiEjb.class).deactivateXsd(idXsdDatiSpec);
        DecXsdDatiSpec xsdDatiSpec = helper.findById(DecXsdDatiSpec.class, idXsdDatiSpec);
        // Parte di logging
        DecTipoCompDoc tipoCompDoc = xsdDatiSpec.getDecTipoCompDoc();
        BigDecimal idTipoDoc = xsdDatiSpec.getDecTipoDoc() != null
                ? new BigDecimal(xsdDatiSpec.getDecTipoDoc().getIdTipoDoc()) : null;
        BigDecimal idTipoUnitaDoc = xsdDatiSpec.getDecTipoUnitaDoc() != null
                ? new BigDecimal(xsdDatiSpec.getDecTipoUnitaDoc().getIdTipoUnitaDoc()) : null;

        if (tipoCompDoc != null) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO,
                    new BigDecimal(tipoCompDoc.getDecTipoStrutDoc().getIdTipoStrutDoc()), param.getNomePagina());
        } else if (idTipoDoc != null) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO, idTipoDoc,
                    param.getNomePagina());
        } else if (idTipoUnitaDoc != null) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA, idTipoUnitaDoc,
                    param.getNomePagina());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deactivateXsd(BigDecimal idXsdDatiSpec) {
        DecXsdDatiSpec xsdDatiSpec = helper.findById(DecXsdDatiSpec.class, idXsdDatiSpec);
        xsdDatiSpec.setDtSoppres(Calendar.getInstance().getTime());

        helper.getEntityManager().flush();
    }
}
