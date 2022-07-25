package it.eng.parer.job.indiceAipSerieUd.utils;

import it.eng.parer.entity.SerVolVerSerie;
import it.eng.parer.job.indiceAipSerieUd.helper.CreazioneIndiceVolumeSerieUdHelper;
import it.eng.parer.serie.xml.indiceVolumeSerie.ContenutoAnaliticoVolumeType;
import it.eng.parer.serie.xml.indiceVolumeSerie.ContenutoSinteticoVolumeType;
import it.eng.parer.serie.xml.indiceVolumeSerie.CriterioOrdinamentoType;
import it.eng.parer.serie.xml.indiceVolumeSerie.IndiceVolumeSerie;
import it.eng.parer.serie.xml.indiceVolumeSerie.NotaType;
import it.eng.parer.serie.xml.indiceVolumeSerie.SerieType;
import it.eng.parer.serie.xml.indiceVolumeSerie.UnitaDocumentariaType;
import it.eng.parer.viewEntity.SerVCreaIxVolSerieUd;
import it.eng.parer.viewEntity.SerVLisUdAppartVolSerie;
import it.eng.parer.ws.recupero.utils.XmlDateUtility;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.datatype.DatatypeConfigurationException;

/**
 *
 * @author gilioli_p
 */
public class CreazioneIndiceVolumeSerieUdUtil {

    private CreazioneIndiceVolumeSerieUdHelper civsudHelper;

    public CreazioneIndiceVolumeSerieUdUtil() throws NamingException {
        // Recupera l'ejb per la lettura di informazioni, se possibile
        civsudHelper = (CreazioneIndiceVolumeSerieUdHelper) new InitialContext()
                .lookup("java:module/CreazioneIndiceVolumeSerieUdHelper");
    }

    public IndiceVolumeSerie generaIndiceVolumeSerie(SerVCreaIxVolSerieUd creaVol, BigDecimal numeroTotaleVolumi)
            throws DatatypeConfigurationException {
        IndiceVolumeSerie indiceVolumeSerie = new IndiceVolumeSerie();
        popolaIndiceVolumeSerie(indiceVolumeSerie, creaVol, numeroTotaleVolumi);
        return indiceVolumeSerie;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void popolaIndiceVolumeSerie(IndiceVolumeSerie indiceVolumeSerie, SerVCreaIxVolSerieUd creaVol,
            BigDecimal numeroTotVolumi) throws DatatypeConfigurationException {

        // Versione XSD indice volume serie
        indiceVolumeSerie.setVersioneXSDIndiceVolumeSerie("1.1");
        // Serie
        SerieType serie = new SerieType();
        serie.setCodice(creaVol.getDsIdSerie());
        serie.setDenominazione(creaVol.getDsSerie());
        serie.setVersione(creaVol.getCdVerSerie());
        indiceVolumeSerie.setSerie(serie);
        // Progressivo volume
        indiceVolumeSerie.setProgressivoVolume((civsudHelper.findById(SerVolVerSerie.class, creaVol.getIdVolVerSerie()))
                .getPgVolVerSerie().toBigInteger());
        // Numero totale volumi
        indiceVolumeSerie.setNumeroTotaleVolumi(numeroTotVolumi.toBigInteger());
        // Contenuto sintetico volume
        ContenutoSinteticoVolumeType contenutoSintetito = new ContenutoSinteticoVolumeType();
        contenutoSintetito.setNumeroUnitaDocumentarie(creaVol.getNiUnitaDocVol().toBigInteger());
        ContenutoSinteticoVolumeType.PrimaUnitaDocumentaria prima = new ContenutoSinteticoVolumeType.PrimaUnitaDocumentaria();
        prima.setCodice(creaVol.getCdFirstUnitaDocVol());
        prima.setData(XmlDateUtility.dateToXMLGregorianCalendar(creaVol.getDtFirstUnitaDocVol()));
        contenutoSintetito.setPrimaUnitaDocumentaria(prima);
        ContenutoSinteticoVolumeType.UltimaUnitaDocumentaria ultima = new ContenutoSinteticoVolumeType.UltimaUnitaDocumentaria();
        ultima.setCodice(creaVol.getCdLastUnitaDocVol());
        ultima.setData(XmlDateUtility.dateToXMLGregorianCalendar(creaVol.getDtLastUnitaDocVol()));
        contenutoSintetito.setUltimaUnitaDocumentaria(ultima);
        indiceVolumeSerie.setContenutoSinteticoVolume(contenutoSintetito);
        // Criterio ordinamento (opzionale)
        if (creaVol.getDsAutoreCriterioOrdinamento() != null) {
            CriterioOrdinamentoType cot = new CriterioOrdinamentoType();
            NotaType nota = new NotaType();
            nota.setAutore(creaVol.getDsAutoreCriterioOrdinamento());
            nota.setData(XmlDateUtility.dateToXMLGregorianCalendar(creaVol.getDtCriterioOrdinamento()));
            nota.setValue(creaVol.getDsCriterioOrdinamento());
            cot.setNota(nota);
            indiceVolumeSerie.setCriterioOrdinamento(cot);
        }

        // Unità Documentarie
        List<SerVLisUdAppartVolSerie> udAppartVolList = civsudHelper
                .getSerVLisUdAppartVolSerie(creaVol.getIdVolVerSerie().longValue());
        ContenutoAnaliticoVolumeType contAnalitico = new ContenutoAnaliticoVolumeType();
        for (SerVLisUdAppartVolSerie udAppart : udAppartVolList) {
            UnitaDocumentariaType ud = new UnitaDocumentariaType();
            ud.setCodice(udAppart.getCdUdSerie());
            ud.setChiave(udAppart.getCdKeyUnitaDoc());
            ud.setData(XmlDateUtility.dateToXMLGregorianCalendar(udAppart.getDtUdSerie()));
            ud.setHashIndiceAIP(udAppart.getDsHash());
            ud.setUrnIndiceAIP(udAppart.getDsUrn());
            contAnalitico.getUnitaDocumentaria().add(ud);
        }
        indiceVolumeSerie.setContenutoAnaliticoVolume(contAnalitico);
    }
}
