package it.eng.parer.elencoVersamento.helper;

import it.eng.parer.elencoVersamento.utils.ElencoEnums.*;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.viewEntity.VolVCntUdDocCompTipoUd;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Agati_D
 */
@Stateless(mappedName = "IndiceElencoVersHelper")
@LocalBean
public class IndiceElencoVersHelper {
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager em;

    public List<AroUnitaDoc> retrieveUnitaDocsInVolume(VolVolumeConserv volume) {
        Query q = em.createQuery("SELECT appUnitaDoc.aroUnitaDoc " + "FROM VolAppartUnitaDocVolume appUnitaDoc "
                + "WHERE appUnitaDoc.volVolumeConserv.idVolumeConserv = :idVolume");
        q.setParameter("idVolume", volume.getIdVolumeConserv());
        List<AroUnitaDoc> unitaDocs = q.getResultList();
        return unitaDocs;
    }

    // //TODO vedere perche non è stata eliminata
    // public List<String> getTipologieInVol(VolVolumeConserv volume) {
    // //SELECT distinct tipo_ud.NM_TIPO_UNITA_DOC
    // //FROM VOL_VOLUME_CONSERV vol, VOL_APPART_UNITA_DOC_VOLUME appart, ARO_UNITA_DOC ud, DEC_TIPO_UNITA_DOC tipo_ud
    // //where vol.ID_VOLUME_CONSERV = appart.ID_VOLUME_CONSERV
    // //and appart.ID_UNITA_DOC = ud.ID_UNITA_DOC
    // //and ud.ID_TIPO_UNITA_DOC = tipo_ud.ID_TIPO_UNITA_DOC
    // //and vol.ID_VOLUME_CONSERV = 1085
    //
    // String SELECT_DISTINCT_TIPO_UD_STM = "SELECT distinct(tipoUd.nmTipoUnitaDoc) "
    // + "FROM VolVolumeCOnserv vol "
    // + "JOIN vol.volAppartUnitaDocVolumes appartUd "
    // + "JOIN appartUd.aroUnitaDoc ud "
    // + "JOIN ud.decTipoUnitaDoc tipoUd "
    // + "WHERE tipoUd.idVolumeConserv = :idVolume";
    //
    // Query q = em.createQuery(SELECT_DISTINCT_TIPO_UD_STM);
    // q.setParameter("idVolume", volume.getIdVolumeConserv());
    // List<String> tipiUd = q.getResultList();
    // return tipiUd;
    // }

    public List<VolVCntUdDocCompTipoUd> getContenutoSinteticoElenco(VolVolumeConserv volume) {
        String SELECT_CONTENUTO_SINTETICO_ELENCO_STM = "SELECT contSint " + "FROM VolVCntUdDocCompTipoUd contSint "
                + "WHERE contSint.idVolumeConserv = :idVolume";

        Query q = em.createQuery(SELECT_CONTENUTO_SINTETICO_ELENCO_STM);
        q.setParameter("idVolume", volume.getIdVolumeConserv());
        List<VolVCntUdDocCompTipoUd> riepilogoContenutoSinteticoElenco = q.getResultList();
        return riepilogoContenutoSinteticoElenco;
    }

    public String getTipologieDocumentoPrincipaleElv(ElvElencoVer elenco) {
        String SELECT_TIPOLOGIE_DOC_PRINC_ELV_STM = "SELECT distinct tipoDoc.nmTipoDoc " + "FROM ElvElencoVer elenco "
                + "JOIN elenco.aroUnitaDocs uds " + "JOIN uds.aroDocs docs " + "JOIN docs.decTipoDoc tipoDoc "
                + "WHERE docs.tiDoc = :tipoDoc " + "AND elenco = :elenco";
        Query q = em.createQuery(SELECT_TIPOLOGIE_DOC_PRINC_ELV_STM);
        q.setParameter("tipoDoc", DocTypeEnum.PRINCIPALE.name());
        q.setParameter("elenco", elenco);
        List<String> tipologieDocPrinc = q.getResultList();
        return convertListToString(tipologieDocPrinc);
    }

    public String getTipologieDocumentoPrincipaleUd(AroUnitaDoc ud) {
        String SELECT_TIPOLOGIE_DOC_PRINC_Ud_STM = "SELECT tipoDoc.nmTipoDoc " + "FROM AroUnitaDoc ud "
                + "JOIN ud.aroDocs docs " + "JOIN docs.decTipoDoc tipoDoc " + "WHERE docs.tiDoc = :tipoDoc "
                + "AND ud.idUnitaDoc = :idUd";
        Query q = em.createQuery(SELECT_TIPOLOGIE_DOC_PRINC_Ud_STM);
        q.setParameter("tipoDoc", DocTypeEnum.PRINCIPALE.name());
        q.setParameter("idUd", ud.getIdUnitaDoc());
        List<String> tipologieDocPrinc = q.getResultList();
        return convertListToString(tipologieDocPrinc);
    }

    public String getTipologieUnitaDocumentaria(ElvElencoVer elenco) {
        String SELECT_TIPOLOGIE_UD_STM = "SELECT distinct tipoUnitaDoc.nmTipoUnitaDoc " + "FROM ElvElencoVer elenco "
                + "JOIN elenco.aroUnitaDocs uds " + "JOIN uds.decTipoUnitaDoc tipoUnitaDoc " + "WHERE elenco = :elenco";
        Query q = em.createQuery(SELECT_TIPOLOGIE_UD_STM);
        q.setParameter("elenco", elenco);
        List<String> tipologieUd = q.getResultList();
        return convertListToString(tipologieUd);
    }

    public String getTipologieRegistro(ElvElencoVer elenco) {
        String SELECT_TIPOLOGIE_REGISTRO_STM = "SELECT distinct uds.cdRegistroKeyUnitaDoc "
                + "FROM ElvElencoVer elenco " + "JOIN elenco.aroUnitaDocs uds " + "WHERE elenco = :elenco";
        Query q = em.createQuery(SELECT_TIPOLOGIE_REGISTRO_STM);
        q.setParameter("elenco", elenco);
        List<String> tipologieRegistro = q.getResultList();
        return convertListToString(tipologieRegistro);
    }

    public String getUtentiVersatori(ElvElencoVer elenco) {
        String SELECT_USR_STM = "SELECT usr.nmUserid " + "FROM ElvVSelUsrIndiceElenco usr "
                + "WHERE usr.idElencoVers = :idElencoVers";
        Query q = em.createQuery(SELECT_USR_STM);
        q.setParameter("idElencoVers", elenco.getIdElencoVers());
        List<String> utentiVersatori = q.getResultList();
        return convertListToString(utentiVersatori);
    }

    public String convertListToString(List<String> listToConvert) {
        return StringUtils.join(listToConvert.toArray(), ",");
    }

    public Map<String, Date> retrieveDateVersamento(ElvElencoVer elenco) {
        Object[] dateVersamentoUdVersate = (Object[]) retrieveDateVersamentoUdVersate(elenco);
        Object[] dateVersamentoDocAgg = (Object[]) retrieveDateVersamentoDocAgg(elenco);
        Object[] dateVersamentoUpd = (Object[]) retrieveDateVersamentoUpd(elenco);

        Date minUdVersate = (Date) dateVersamentoUdVersate[0];
        Date minDocAgg = (Date) dateVersamentoDocAgg[0];
        Date minUpd = (Date) dateVersamentoUpd[0];

        Date maxUdVersate = (Date) dateVersamentoUdVersate[1];
        Date maxDocAgg = (Date) dateVersamentoDocAgg[1];
        Date maxUpd = (Date) dateVersamentoUpd[1];

        Date dataVersamentoIniziale = least(calcolaDataVersamentoIniziale(minUdVersate, minDocAgg), minUpd);
        Date dataVersamentoFinale = most(calcolaDataVersamentoFinale(maxUdVersate, maxDocAgg), maxUpd);
        Map<String, Date> dateVersamento = new HashMap<>();
        dateVersamento.put("dataVersamentoIniziale", dataVersamentoIniziale);
        dateVersamento.put("dataVersamentoFinale", dataVersamentoFinale);
        return dateVersamento;
    }

    private Date calcolaDataVersamentoIniziale(Date minUdVersate, Date minDocAgg) {
        Date dataVersamentoIniziale = null;
        if (minUdVersate != null && minDocAgg != null) {
            if (minUdVersate.after(minDocAgg)) {
                dataVersamentoIniziale = minDocAgg;
            } else {
                dataVersamentoIniziale = minUdVersate;
            }
        }
        if (minUdVersate == null && minDocAgg != null) {
            dataVersamentoIniziale = minDocAgg;
        } else if (minUdVersate != null && minDocAgg == null) {
            dataVersamentoIniziale = minUdVersate;
        }
        // TODO: controllare se funziona e cosa mettere in caso di null ad entrambi. Possibile?
        return dataVersamentoIniziale;
    }

    private Date calcolaDataVersamentoFinale(Date maxUdVersate, Date maxDocAgg) {
        Date dataVersamentoFinale = null;
        if (maxUdVersate != null && maxDocAgg != null) {
            if (maxUdVersate.after(maxDocAgg)) {
                dataVersamentoFinale = maxUdVersate;
            } else {
                dataVersamentoFinale = maxDocAgg;
            }
        }
        if (maxUdVersate == null && maxDocAgg != null) {
            dataVersamentoFinale = maxDocAgg;
        } else if (maxUdVersate != null && maxDocAgg == null) {
            dataVersamentoFinale = maxUdVersate;
        }
        // TODO: controllare se funziona e cosa mettere in caso di null ad entrambi. Possibile?
        return dataVersamentoFinale;
    }

    private Date least(Date a, Date b) {
        return a == null ? b : (b == null ? a : (a.before(b) ? a : b));
    }

    private Date most(Date a, Date b) {
        return a == null ? b : (b == null ? a : (a.after(b) ? a : b));
    }

    public Object retrieveDateVersamentoUdVersate(ElvElencoVer elenco) {
        String SELECT_STM = "SELECT MIN(ud.dtCreazione), MAX(ud.dtCreazione) " + "FROM AroUnitaDoc ud "
                + "WHERE ud.elvElencoVer.idElencoVers = :idElencoVers";
        Query q = em.createQuery(SELECT_STM);
        q.setParameter("idElencoVers", elenco.getIdElencoVers());
        Object dataVersamentoInizialeUdVersate = q.getSingleResult();
        return dataVersamentoInizialeUdVersate;
    }

    public Object retrieveDateVersamentoDocAgg(ElvElencoVer elenco) {
        String SELECT_STM = "SELECT MIN(doc.dtCreazione), MAX(doc.dtCreazione) " + "FROM AroDoc doc "
                + "WHERE doc.elvElencoVer.idElencoVers = :idElencoVers";
        Query q = em.createQuery(SELECT_STM);
        q.setParameter("idElencoVers", elenco.getIdElencoVers());
        Object dataVersamentoInizialeDocAgg = q.getSingleResult();
        return dataVersamentoInizialeDocAgg;
    }

    public Object retrieveDateVersamentoUpd(ElvElencoVer elenco) {
        String SELECT_STM = "SELECT MIN(upd.tsIniSes), MAX(upd.tsIniSes) " + "FROM AroUpdUnitaDoc upd "
                + "WHERE upd.elvElencoVer.idElencoVers = :idElencoVers";
        Query q = em.createQuery(SELECT_STM);
        q.setParameter("idElencoVers", elenco.getIdElencoVers());
        Object dataVersamentoInizialeUpd = q.getSingleResult();
        return dataVersamentoInizialeUpd;
    }

}

// public ListaCRL retrieveListaCRL(VolVolumeConserv volume) {
// String SELECT_LISTA_CRL =
// "select distinct " +
// "issuer.dl_dn_issuer_certif_ca issuerCRL, " +
// "crl.ni_serial_crl numSerialeCRL, " +
// "'CRL_' || to_char(crl.id_crl) nomeFileCRL " +
// "from VOL_VOLUME_CONSERV vol " +
// "join VOL_APPART_UNITA_DOC_VOLUME app_ud " +
// "on (app_ud.id_volume_conserv = vol.id_volume_conserv) " +
// "join VOL_APPART_DOC_VOLUME app_doc " +
// "on (app_doc.id_appart_unita_doc_volume = app_ud.id_appart_unita_doc_volume) " +
// "join VOL_APPART_COMP_VOLUME app_comp " +
// "on (app_comp.id_appart_doc_volume = app_doc.id_appart_doc_volume) " +
// "join ARO_FIRMA_COMP firma " +
// "on (firma.id_comp_doc = app_comp.id_comp_doc) " +
// "join ARO_CONTR_FIRMA_COMP contr_CRL " +
// "on (contr_CRL.id_firma_comp = firma.id_firma_comp " +
// "and contr_CRL.ti_contr = 'CRL') " +
// "join FIR_CRL crl " +
// "on (crl.id_crl = contr_CRL.id_crl_usata) " +
// "join FIR_CERTIF_CA cert " +
// "on (cert.id_certif_ca = crl.id_certif_ca) " +
// "join FIR_ISSUER issuer " +
// "on (issuer.id_issuer = cert.id_issuer) " +
// "where vol.id_volume_conserv = ? " +
// "UNION " +
// "select distinct " +
// "issuer.dl_dn_issuer_certif_ca issuerCRL, " +
// "crl.ni_serial_crl numSerialeCRL, " +
// "'CRL_' || to_char(crl.id_crl) nomeFileCRL " +
// "from VOL_VOLUME_CONSERV vol " +
// "join VOL_APPART_UNITA_DOC_VOLUME app_ud " +
// "on (app_ud.id_volume_conserv = vol.id_volume_conserv) " +
// "join VOL_APPART_DOC_VOLUME app_doc " +
// "on (app_doc.id_appart_unita_doc_volume = app_ud.id_appart_unita_doc_volume) " +
// "join VOL_APPART_COMP_VOLUME app_comp " +
// "on (app_comp.id_appart_doc_volume = app_doc.id_appart_doc_volume) " +
// "join ARO_FIRMA_COMP firma " +
// "on (firma.id_comp_doc = app_comp.id_comp_doc) " +
// "join ARO_CONTR_FIRMA_COMP contr_CATENA " +
// "on (contr_CATENA.id_firma_comp = firma.id_firma_comp " +
// "and contr_CATENA.ti_contr = 'CATENA_TRUSTED') " +
// "join ARO_USO_CERTIF_CA_CONTR_COMP uso_cert " +
// "on (uso_cert.id_contr_firma_comp = contr_CATENA.id_contr_firma_comp) " +
// "join FIR_CRL crl " +
// "on (crl.id_crl = uso_cert.id_crl_usata) " +
// "join FIR_CERTIF_CA cert " +
// "on (cert.id_certif_ca = crl.id_certif_ca) " +
// "join FIR_ISSUER issuer " +
// "on (issuer.id_issuer = cert.id_issuer) " +
// "where vol.id_volume_conserv = ? " +
// "order by 3";
// Query q = em.createNativeQuery(SELECT_LISTA_CRL);
// q.setParameter(1, volume.getIdVolumeConserv());
// q.setParameter(2, volume.getIdVolumeConserv());
// List<Object> crlObjectList = q.getResultList();
//
// ListaCRL listaCRL = null;
// if(crlObjectList.size() > 0) {
// listaCRL = new ListaCRL();
// for(Object crlObject : crlObjectList) {
// CRL crl = new CRL();
// Object[] arr = (Object[])crlObject;
// crl.setIssuerCRL((String)arr[0]);
// //TODO: controllare se questo if è necessario
// if(arr[1] != null) {
// crl.setNumSerialeCRL(((BigDecimal)arr[1]).longValue());
// }
// crl.setNomeFileCRL((String)arr[2]);
// listaCRL.addCRL(crl);
// }
// }
// return listaCRL;
// }

// public ListaCertificatiCA retrieveListaCertificatiCA(VolVolumeConserv volume) {
// String SELECT_LISTA_CERTIFICATI_CA =
// "select distinct " +
// "issuer.dl_dn_issuer_certif_ca issuerCertificatoCA, " +
// "cert.ni_serial_certif_ca numSerialeCertificatoCA, " +
// "'CERTIF_CA_' || to_char(cert.id_certif_ca) nomeFileCertificatoCA " +
// "from VOL_VOLUME_CONSERV vol " +
// "join VOL_APPART_UNITA_DOC_VOLUME app_ud " +
// "on (app_ud.id_volume_conserv = vol.id_volume_conserv) " +
// "join VOL_APPART_DOC_VOLUME app_doc " +
// "on (app_doc.id_appart_unita_doc_volume = app_ud.id_appart_unita_doc_volume) " +
// "join VOL_APPART_COMP_VOLUME app_comp " +
// "on (app_comp.id_appart_doc_volume = app_doc.id_appart_doc_volume) " +
// "join ARO_FIRMA_COMP firma " +
// "on (firma.id_comp_doc = app_comp.id_comp_doc) " +
// "join ARO_CONTR_FIRMA_COMP contr_CATENA " +
// "on (contr_CATENA.id_firma_comp = firma.id_firma_comp " +
// "and contr_CATENA.ti_contr = 'CATENA_TRUSTED') " +
// "join ARO_USO_CERTIF_CA_CONTR_COMP uso_cert " +
// "on (uso_cert.id_contr_firma_comp = contr_CATENA.id_contr_firma_comp) " +
// "join FIR_CERTIF_CA cert " +
// "on (cert.id_certif_ca = uso_cert.id_certif_ca) " +
// "join FIR_ISSUER issuer " +
// "on (issuer.id_issuer = cert.id_issuer) " +
// "where vol.id_volume_conserv = ? " +
// "order by 3";
// Query q = em.createNativeQuery(SELECT_LISTA_CERTIFICATI_CA);
// q.setParameter(1, volume.getIdVolumeConserv());
// List<Object> certificatoObjectCAList = q.getResultList();
//
// ListaCertificatiCA listaCertificatiCA = null;
// if(certificatoObjectCAList.size() > 0) {
// listaCertificatiCA = new ListaCertificatiCA();
// for(Object certificatoObjectCA : certificatoObjectCAList) {
// CertificatoCAListaCertCA certificatoCA = new CertificatoCAListaCertCA();
// Object[] arr = (Object[])certificatoObjectCA;
// certificatoCA.setIssuerCerificatoCA((String)arr[0]);
// certificatoCA.setNumSerialeCertificatoCA(((BigDecimal)arr[1]).longValue());
// certificatoCA.setNomeFileCertificatoCA((String)arr[2]);
// listaCertificatiCA.addCertificatoCAListaCertCA(certificatoCA);
// }
// }
// return listaCertificatiCA;
// }

// //DA SPOSTARE IN VOLUME HELPER
// public List<AroUnitaDoc> retrieveUdInVolumePerTipologia(VolVolumeConserv volume, String tipologia) {
//
// String SELECT_UNITA_DOC_IN_VOL_PER_TIPOLOGIA_STM = "SELECT appartUd.aroUnitaDoc "
// + "FROM VolVolumeCOnserv vol "
// + "JOIN vol.volAppartUnitaDocVolumes appartUd "
// + "JOIN appartUd.aroUnitaDoc ud "
// + "JOIN ud.decTipoUnitaDoc tipoUd "
// + "WHERE vol.idVolumeConserv = :idVolume "
// + "AND tipoUd.nmTipoUnitaDoc = :tipologia";
//
//// String SELECT_NUM_UNITA_DOC_IN_VOL_PER_TIPOLOGIA_STM = "SELECT count(appartUd) "
//// + "FROM VolVolumeCOnserv vol "
//// + "JOIN vol.volAppartUnitaDocVolumes appartUd "
//// + "JOIN appartUd.aroUnitaDoc ud "
//// + "JOIN ud.decTipoUnitaDoc tipoUd "
//// + "WHERE vol.idVolumeConserv = :idVolume "
//// + "AND tipoUd.nmTipoUnitaDoc = :tipologia";
//
// Query q = em.createQuery(SELECT_UNITA_DOC_IN_VOL_PER_TIPOLOGIA_STM);
// q.setParameter("idVolume", volume.getIdVolumeConserv());
// q.setParameter("tipologia", tipologia);
// List<AroUnitaDoc> udInVolumePerTipo = q.getResultList();
// return udInVolumePerTipo;
// }
//
// //DA SPOSTARE IN VOLUME HELPER
// public long countNumUdInVolumePerTipologia(VolVolumeConserv volume, String tipologia) {
// List<AroUnitaDoc> UdInVolPerTipo = retrieveUdInVolumePerTipologia(volume, tipologia);
// return UdInVolPerTipo.size();
// }
//
// public long countNumDocInVolumePerTipologia(VolVolumeConserv volume, String tipologia) {
// long docCount = 0;
// List<AroUnitaDoc> UdInVolPerTipo = retrieveUdInVolumePerTipologia(volume, tipologia);
// for(AroUnitaDoc ud : UdInVolPerTipo) {
// docCount = docCount + ud.getAroDocs().size();
// }
// return docCount;
// }
//
// public long countNumCompInVolumePerTipologia(VolVolumeConserv volume, String tipologia) {
// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools |
// Templates.
// }

// //DA SPOSTARE IN VOLUME HELPER
// public List<String> countNumDocInUdTipologia(VolVolumeConserv volume, String tipologia) {
//
// String SELECT_UNITA_DOC_IN_VOL_PER_TIPOLOGIA_STM = "SELECT appartUd.aroUnitaDoc "
// + "FROM VolVolumeCOnserv vol "
// + "JOIN vol.volAppartUnitaDocVolumes appartUd "
// + "JOIN appartUd.aroUnitaDoc ud "
// + "JOIN ud.decTipoUnitaDoc tipoUd "
// + "WHERE vol.idVolumeConserv = :idVolume "
// + "AND tipoUd.nmTipoUnitaDoc = :tipologia";
//
//// String SELECT_NUM_UNITA_DOC_IN_VOL_PER_TIPOLOGIA_STM = "SELECT count(appartUd) "
//// + "FROM VolVolumeCOnserv vol "
//// + "JOIN vol.volAppartUnitaDocVolumes appartUd "
//// + "JOIN appartUd.aroUnitaDoc ud "
//// + "JOIN ud.decTipoUnitaDoc tipoUd "
//// + "WHERE vol.idVolumeConserv = :idVolume "
//// + "AND tipoUd.nmTipoUnitaDoc = :tipologia";
//
// Query q = em.createQuery(SELECT_UNITA_DOC_IN_VOL_PER_TIPOLOGIA_STM);
// q.setParameter("idVolume", volume.getIdVolumeConserv());
// q.setParameter("tipologia", tipologia);
// List<String> udInVolumePerTipo = q.getResultList();
// return udInVolumePerTipo;
// }

// -------------------------------------------------

// //DA SPOSTARE IN VOLUME HELPER
// public long countNumUdInVolume(VolVolumeConserv volume) {
// String SELECT_NUM_UNITA_DOC_IN_VOL_STM = "SELECT count(appart) "
// + "FROM VolVolumeCOnserv vol JOIN vol.volAppartUnitaDocVolumes appart "
// + "WHERE vol.idVolumeConserv = :idVolume";
//
// Query q = em.createQuery(SELECT_NUM_UNITA_DOC_IN_VOL_STM);
// q.setParameter("idVolume", volume.getIdVolumeConserv());
// long numUdInVolume = ((Long) q.getSingleResult()).longValue();
// return numUdInVolume;
// }
//
// //DA SPOSTARE IN VOLUME HELPER
// public long countNumDocInVolume(VolVolumeConserv volume) {
// String SELECT_NUM_DOC_IN_VOL_STM = "SELECT count(appartDoc) "
// + "FROM VolVolumeCOnserv vol JOIN vol.volAppartUnitaDocVolumes appartUd JOIN appartUd.volAppartDocVolumes appartDoc "
// + "WHERE vol.idVolumeConserv = :idVolume";
//
// Query q = em.createQuery(SELECT_NUM_DOC_IN_VOL_STM);
// q.setParameter("idVolume", volume.getIdVolumeConserv());
// long numDocInVolume = ((Long) q.getSingleResult()).longValue();
// return numDocInVolume;
// }
//
// //DA SPOSTARE IN VOLUME HELPER
// public long countNumCompInVolume(VolVolumeConserv volume) {
// String SELECT_NUM_COMP_IN_VOL_STM = "SELECT count(appartComp) "
// + "FROM VolVolumeCOnserv vol "
// + "JOIN vol.volAppartUnitaDocVolumes appartUd "
// + "JOIN appartUd.volAppartDocVolumes appartDoc "
// + "JOIN appartDoc.volAppartCompVolumes appartComp "
// + "WHERE vol.idVolumeConserv = :idVolume";
//
// Query q = em.createQuery(SELECT_NUM_COMP_IN_VOL_STM);
// q.setParameter("idVolume", volume.getIdVolumeConserv());
// long numCompInVolume = ((Long) q.getSingleResult()).longValue();
// return numCompInVolume;
// }
//