package it.eng.parer.entity;

import it.eng.parer.entity.constraint.AroUpdDatiSpecUnitaDoc.TiEntitaAroUpdDatiSpecUnitaDoc;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ARO_UPD_DATI_SPEC_UD_OBJECT_STORAGE")
public class AroUpdDatiSpecUdObjectStorage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUpdDatiSpecUdObjectStorage;
    private AroUpdUnitaDoc aroUpdUnitaDoc;
    private AroUpdDocUnitaDoc aroUpdDocUnitaDoc;
    private AroUpdCompUnitaDoc aroUpdCompUnitaDoc;
    private TiEntitaAroUpdDatiSpecUnitaDoc tiEntitaSacer;
    private DecBackend decBackend;
    private String nmTenant;
    private String nmBucket;
    private String cdKeyFile;
    private BigDecimal idStrut;

    public AroUpdDatiSpecUdObjectStorage() {
        // hibernate constructor
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_UPD_DATI_SPEC_UD_OBJECT_STORAGE")
    public Long getIdUpdDatiSpecUdObjectStorage() {
        return idUpdDatiSpecUdObjectStorage;
    }

    public void setIdUpdDatiSpecUdObjectStorage(Long idUpdDatiSpecUdObjectStorage) {
        this.idUpdDatiSpecUdObjectStorage = idUpdDatiSpecUdObjectStorage;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UPD_UNITA_DOC")
    public AroUpdUnitaDoc getAroUpdUnitaDoc() {
        return aroUpdUnitaDoc;
    }

    public void setAroUpdUnitaDoc(AroUpdUnitaDoc aroUpdUnitaDoc) {
        this.aroUpdUnitaDoc = aroUpdUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UPD_DOC_UNITA_DOC")
    public AroUpdDocUnitaDoc getAroUpdDocUnitaDoc() {
        return aroUpdDocUnitaDoc;
    }

    public void setAroUpdDocUnitaDoc(AroUpdDocUnitaDoc aroUpdDocUnitaDoc) {
        this.aroUpdDocUnitaDoc = aroUpdDocUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UPD_COMP_UNITA_DOC")
    public AroUpdCompUnitaDoc getAroUpdCompUnitaDoc() {
        return aroUpdCompUnitaDoc;
    }

    public void setAroUpdCompUnitaDoc(AroUpdCompUnitaDoc aroUpdCompUnitaDoc) {
        this.aroUpdCompUnitaDoc = aroUpdCompUnitaDoc;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_ENTITA_SACER")
    public TiEntitaAroUpdDatiSpecUnitaDoc getTiEntitaSacer() {
        return this.tiEntitaSacer;
    }

    public void setTiEntitaSacer(TiEntitaAroUpdDatiSpecUnitaDoc tiEntitaSacer) {
        this.tiEntitaSacer = tiEntitaSacer;
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
