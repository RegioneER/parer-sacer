package it.eng.parer.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;

/**
 * The persistent class for the DM_UD_DEL_OBJECT_STORAGE database table.
 *
 */
@Entity
@Table(name = "DM_UD_DEL_OBJECT_STORAGE")
@NamedQuery(name = "DmUdDelObjectStorage.findAll", query = "SELECT d FROM DmUdDelObjectStorage d")
public class DmUdDelObjectStorage implements Serializable {
    private static final long serialVersionUID = 1L;
    private long idUdDelObjSto;
    private String cdKeyFile;
    private BigDecimal idDecBackend;
    private BigDecimal idUnitaDoc;
    private String nmBucket;
    private String nmTenant;
    private String tiFile;

    public DmUdDelObjectStorage() {
	/* Hibernate */
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_UD_DEL_OBJ_STO")
    public long getIdUdDelObjSto() {
	return this.idUdDelObjSto;
    }

    public void setIdUdDelObjSto(long idUdDelObjSto) {
	this.idUdDelObjSto = idUdDelObjSto;
    }

    @Column(name = "CD_KEY_FILE")
    public String getCdKeyFile() {
	return this.cdKeyFile;
    }

    public void setCdKeyFile(String cdKeyFile) {
	this.cdKeyFile = cdKeyFile;
    }

    @Column(name = "ID_DEC_BACKEND")
    public BigDecimal getIdDecBackend() {
	return this.idDecBackend;
    }

    public void setIdDecBackend(BigDecimal idDecBackend) {
	this.idDecBackend = idDecBackend;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
	return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
	this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "NM_BUCKET")
    public String getNmBucket() {
	return this.nmBucket;
    }

    public void setNmBucket(String nmBucket) {
	this.nmBucket = nmBucket;
    }

    @Column(name = "NM_TENANT")
    public String getNmTenant() {
	return this.nmTenant;
    }

    public void setNmTenant(String nmTenant) {
	this.nmTenant = nmTenant;
    }

    @Column(name = "TI_FILE")
    public String getTiFile() {
	return this.tiFile;
    }

    public void setTiFile(String tiFile) {
	this.tiFile = tiFile;
    }

}