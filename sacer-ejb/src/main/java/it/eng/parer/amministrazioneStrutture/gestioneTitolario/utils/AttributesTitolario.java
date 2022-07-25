package it.eng.parer.amministrazioneStrutture.gestioneTitolario.utils;

import it.eng.parer.slite.gen.tablebean.DecLivelloTitolRowBean;
import it.eng.parer.slite.gen.tablebean.DecLivelloTitolTableBean;
import it.eng.parer.slite.gen.viewbean.DecVTreeTitolTableBean;
import it.eng.parer.titolario.xml.LivelloType;
import it.eng.parer.titolario.xml.TipoFormatoLivelloType;
import it.eng.parer.amministrazioneStrutture.gestioneTitolario.dto.Voce;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.table.BaseTable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Bonora_L
 */
public class AttributesTitolario {

    Set<Integer> livelli;
    Set<String> nomeLivelli;
    List<LivelloType> livelliParsing;
    Set<Integer> numeroOrdinePrimoLivelloSet;
    Map<String, Voce> vociMap;
    Map<BigDecimal, BaseTableInterface<?>> livelliVociMap;

    DecLivelloTitolTableBean livelliTableBean;
    DecVTreeTitolTableBean vociTableBean;

    public AttributesTitolario() {
        this.livelli = new HashSet<>();
        this.nomeLivelli = new HashSet<>();
        this.livelliParsing = new ArrayList<>();
        this.numeroOrdinePrimoLivelloSet = new HashSet<>();
        this.vociMap = new HashMap<>();
        this.livelliVociMap = new HashMap<>();
    }

    public Set<Integer> getLivelli() {
        return livelli;
    }

    public void setLivelli(Set<Integer> livelli) {
        this.livelli = livelli;
    }

    public Set<String> getNomeLivelli() {
        return nomeLivelli;
    }

    public void setNomeLivelli(Set<String> nomeLivelli) {
        this.nomeLivelli = nomeLivelli;
    }

    public List<LivelloType> getLivelliParsing() {
        return livelliParsing;
    }

    public void setLivelliParsing(List<LivelloType> livelliParsing) {
        this.livelliParsing = livelliParsing;
    }

    public Set<Integer> getNumeroOrdinePrimoLivelloSet() {
        return numeroOrdinePrimoLivelloSet;
    }

    public void setNumeroOrdinePrimoLivelloSet(Set<Integer> numeroOrdinePrimoLivelloSet) {
        this.numeroOrdinePrimoLivelloSet = numeroOrdinePrimoLivelloSet;
    }

    public Map<String, Voce> getVociMap() {
        return vociMap;
    }

    public void setVociMap(Map<String, Voce> vociMap) {
        this.vociMap = vociMap;
        for (String key : vociMap.keySet()) {
            Voce voce = vociMap.get(key);
            getNumeroOrdinePrimoLivelloSet().add(voce.getNumeroOrdine());
        }
    }

    public Map<BigDecimal, BaseTableInterface<?>> getLivelliVociMap() {
        return livelliVociMap;
    }

    public void setLivelliVociMap(Map<BigDecimal, BaseTableInterface<?>> livelliVociMap) {
        this.livelliVociMap = livelliVociMap;
    }

    public DecLivelloTitolTableBean getLivelliTableBean() {
        return livelliTableBean;
    }

    public void setLivelliTableBean(DecLivelloTitolTableBean livelliTableBean) {
        this.livelliTableBean = livelliTableBean;
        for (DecLivelloTitolRowBean livello : livelliTableBean) {
            getLivelli().add(livello.getNiLivello().intValue());
            getNomeLivelli().add(livello.getNmLivelloTitol());

            LivelloType tmpLiv = new LivelloType();
            tmpLiv.setCarattereSeparatoreLivello(livello.getCdSepLivello() != null ? livello.getCdSepLivello() : "");
            tmpLiv.setNomeLivello(livello.getNmLivelloTitol());
            tmpLiv.setNumeroLivello(new BigInteger(livello.getNiLivello().toString()));
            tmpLiv.setTipoFormatoLivello(TipoFormatoLivelloType.fromValue(livello.getTiFmtVoceTitol()));

            getLivelliParsing().add(tmpLiv);

            getLivelliVociMap().put(livello.getNiLivello(), new BaseTable());
        }
    }

    public DecVTreeTitolTableBean getVociTableBean() {
        return vociTableBean;
    }

    public void setVociTableBean(DecVTreeTitolTableBean vociTableBean) {
        this.vociTableBean = vociTableBean;
    }
}
