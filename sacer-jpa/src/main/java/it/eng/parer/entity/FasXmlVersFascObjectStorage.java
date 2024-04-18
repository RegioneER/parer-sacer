package it.eng.parer.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "FAS_XML_VERS_FASC_OBJECT_STORAGE")
public class FasXmlVersFascObjectStorage implements Serializable {
    private static final long serialVersionUID = 1L;

    public FasXmlVersFascObjectStorage() {
        super();
    }

    private Long idFascicolo;
    private FasFascicolo fasFascicolo;
    private BigDecimal idStrut;
    private DecBackend decBackend;
    private String nmTenant;
    private String nmBucket;
    private String cdKeyFile;

    @Id
    @Column(name = "ID_FASCICOLO")
    public Long getIdFascicolo() {
        return idFascicolo;
    }

    public void setIdFascicolo(Long idFascicolo) {
        this.idFascicolo = idFascicolo;
    }

    @MapsId
    @OneToOne(mappedBy = "fasXmlVersFascObjectStorage")
    @JoinColumn(name = "ID_FASCICOLO")
    public FasFascicolo getFasFascicolo() {
        return fasFascicolo;
    }

    public void setFasFascicolo(FasFascicolo fasFascicolo) {
        this.fasFascicolo = fasFascicolo;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DEC_BACKEND")
    public DecBackend getDecBackend() {
        return decBackend;
    }

    public void setDecBackend(DecBackend decBackend) {
        this.decBackend = decBackend;
    }

    @Column(name = "NM_TENANT")
    public String getNmTenant() {
        return nmTenant;
    }

    public void setNmTenant(String nmTenant) {
        this.nmTenant = nmTenant;
    }

    @Column(name = "NM_BUCKET")
    public String getNmBucket() {
        return nmBucket;
    }

    public void setNmBucket(String nmBucket) {
        this.nmBucket = nmBucket;
    }

    @Column(name = "CD_KEY_FILE")
    public String getCdKeyFile() {
        return cdKeyFile;
    }

    public void setCdKeyFile(String cdKeyFile) {
        this.cdKeyFile = cdKeyFile;
    }
}
