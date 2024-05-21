package it.eng.parer.entity;

import it.eng.parer.entity.constraint.AroVersIniDatiSpec.TiEntitaSacerAroVersIniDatiSpec;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ARO_VERS_INI_DATI_SPEC_OBJECT_STORAGE")
public class AroVersIniDatiSpecObjectStorage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idVersIniDatiSpecObjectStorage;
    private AroVersIniUnitaDoc aroVersIniUnitaDoc;
    private AroVersIniDoc aroVersIniDoc;
    private AroVersIniComp aroVersIniComp;
    private TiEntitaSacerAroVersIniDatiSpec tiEntitaSacer;
    private DecBackend decBackend;
    private String nmTenant;
    private String nmBucket;
    private String cdKeyFile;
    private BigDecimal idStrut;

    public AroVersIniDatiSpecObjectStorage() {
        // hibernate constructor
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VERS_INI_DATI_SPEC_OBJECT_STORAGE")
    public Long getIdVersIniDatiSpecObjectStorage() {
        return idVersIniDatiSpecObjectStorage;
    }

    public void setIdVersIniDatiSpecObjectStorage(Long idVersIniDatiSpecObjectStorage) {
        this.idVersIniDatiSpecObjectStorage = idVersIniDatiSpecObjectStorage;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS_INI_UNITA_DOC")
    public AroVersIniUnitaDoc getAroVersIniUnitaDoc() {
        return aroVersIniUnitaDoc;
    }

    public void setAroVersIniUnitaDoc(AroVersIniUnitaDoc aroVersIniUnitaDoc) {
        this.aroVersIniUnitaDoc = aroVersIniUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS_INI_DOC")
    public AroVersIniDoc getAroVersIniDoc() {
        return aroVersIniDoc;
    }

    public void setAroVersIniDoc(AroVersIniDoc aroVersIniDoc) {
        this.aroVersIniDoc = aroVersIniDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS_INI_COMP")
    public AroVersIniComp getAroVersIniComp() {
        return aroVersIniComp;
    }

    public void setAroVersIniComp(AroVersIniComp aroVersIniComp) {
        this.aroVersIniComp = aroVersIniComp;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_ENTITA_SACER")
    public TiEntitaSacerAroVersIniDatiSpec getTiEntitaSacer() {
        return this.tiEntitaSacer;
    }

    public void setTiEntitaSacer(TiEntitaSacerAroVersIniDatiSpec tiEntitaSacer) {
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
