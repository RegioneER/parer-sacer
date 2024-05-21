package it.eng.parer.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "VRS_XML_SES_UPD_UD_KO_OBJECT_STORAGE")
public class VrsXmlSesUpdUdKoObjectStorage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idXmlSesUpdUdKoObjectStorage;
    private VrsSesUpdUnitaDocKo vrsSesUpdUnitaDocKo;
    private DecBackend decBackend;
    private String nmTenant;
    private String nmBucket;
    private String cdKeyFile;
    private BigDecimal idStrut;

    public VrsXmlSesUpdUdKoObjectStorage() {
        // hibernate constructor
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_XML_SES_UPD_UD_KO_OBJECT_STORAGE")
    public Long getIdXmlSesUpdUdKoObjectStorage() {
        return idXmlSesUpdUdKoObjectStorage;
    }

    public void setIdXmlSesUpdUdKoObjectStorage(Long idXmlSesUpdUdKoObjectStorage) {
        this.idXmlSesUpdUdKoObjectStorage = idXmlSesUpdUdKoObjectStorage;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SES_UPD_UNITA_DOC_KO")
    public VrsSesUpdUnitaDocKo getVrsSesUpdUnitaDocKo() {
        return vrsSesUpdUnitaDocKo;
    }

    public void setVrsSesUpdUnitaDocKo(VrsSesUpdUnitaDocKo vrsSesUpdUnitaDocKo) {
        this.vrsSesUpdUnitaDocKo = vrsSesUpdUnitaDocKo;
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

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }
}
