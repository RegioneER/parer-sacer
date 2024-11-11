package it.eng.parer.entity;

import it.eng.parer.entity.inheritance.oop.AroXmlObjectStorage;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author gpiccioli
 */
@Entity
@Table(name = "SER_VER_SERIE_OBJECT_STORAGE")
public class SerVerSerieObjectStorage extends AroXmlObjectStorage {

    public SerVerSerieObjectStorage() {
        super();
    }

    private Long idVerSerieObjectStorage;
    private SerVerSerie serVerSerie;
    private BigDecimal idStrut;
    private String tiFileVerSerie;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VER_SERIE_OBJECT_STORAGE")
    public Long getIdVerSerieObjectStorage() {
        return idVerSerieObjectStorage;
    }

    public void setIdVerSerieObjectStorage(Long idVerSerieObjectStorage) {
        this.idVerSerieObjectStorage = idVerSerieObjectStorage;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_SERIE")
    public SerVerSerie getSerVerSerie() {
        return serVerSerie;
    }

    public void setSerVerSerie(SerVerSerie serVerSerie) {
        this.serVerSerie = serVerSerie;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "TI_FILE_VER_SERIE")
    public String getTiFileVerSerie() {
        return this.tiFileVerSerie;
    }

    public void setTiFileVerSerie(String tiFileVerSerie) {
        this.tiFileVerSerie = tiFileVerSerie;
    }
}
