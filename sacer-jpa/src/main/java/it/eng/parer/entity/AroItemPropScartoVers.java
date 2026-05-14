package it.eng.parer.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;

/**
 * The persistent class for the ARO_ITEM_PROP_SCARTO_VERS database table.
 *
 */
@Entity
@Table(name = "ARO_ITEM_PROP_SCARTO_VERS")
@NamedQuery(name = "AroItemPropScartoVers.findAll", query = "SELECT a FROM AroItemPropScartoVers a")
public class AroItemPropScartoVers implements Serializable {
    private static final long serialVersionUID = 1L;
    private long idItemPropScartoVers;
    private FasFascicolo fasFascicolo;
    // private BigDecimal idSerie;
    private AroUnitaDoc aroUnitaDoc;
    private BigDecimal pgItem;
    private String tiItemPropScartoVers;
    private String tiStatoItem;
    private AroPropScartoVers aroPropScartoVers;

    public AroItemPropScartoVers() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ITEM_PROP_SCARTO_VERS")
    public long getIdItemPropScartoVers() {
        return this.idItemPropScartoVers;
    }

    public void setIdItemPropScartoVers(long idItemPropScartoVers) {
        this.idItemPropScartoVers = idItemPropScartoVers;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FASCICOLO")
    public FasFascicolo getFasFascicolo() {
        return this.fasFascicolo;
    }

    public void setFasFascicolo(FasFascicolo fasFascicolo) {
        this.fasFascicolo = fasFascicolo;
    }

    // @Column(name="ID_SERIE")
    // public BigDecimal getIdSerie() {
    // return this.idSerie;
    // }
    //
    // public void setIdSerie(BigDecimal idSerie) {
    // this.idSerie = idSerie;
    // }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNITA_DOC")
    public AroUnitaDoc getAroUnitaDoc() {
        return this.aroUnitaDoc;
    }

    public void setAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
        this.aroUnitaDoc = aroUnitaDoc;
    }

    @Column(name = "PG_ITEM")
    public BigDecimal getPgItem() {
        return this.pgItem;
    }

    public void setPgItem(BigDecimal pgItem) {
        this.pgItem = pgItem;
    }

    @Column(name = "TI_ITEM_PROP_SCARTO_VERS")
    public String getTiItemPropScartoVers() {
        return this.tiItemPropScartoVers;
    }

    public void setTiItemPropScartoVers(String tiItemPropScartoVers) {
        this.tiItemPropScartoVers = tiItemPropScartoVers;
    }

    @Column(name = "TI_STATO_ITEM")
    public String getTiStatoItem() {
        return this.tiStatoItem;
    }

    public void setTiStatoItem(String tiStatoItem) {
        this.tiStatoItem = tiStatoItem;
    }

    // bi-directional many-to-one association to AroPropScartoVer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PROP_SCARTO_VERS")
    public AroPropScartoVers getAroPropScartoVers() {
        return this.aroPropScartoVers;
    }

    public void setAroPropScartoVers(AroPropScartoVers aroPropScartoVers) {
        this.aroPropScartoVers = aroPropScartoVers;
    }

}