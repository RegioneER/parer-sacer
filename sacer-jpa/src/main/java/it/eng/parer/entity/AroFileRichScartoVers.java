package it.eng.parer.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the ARO_FILE_RICH_SCARTO_VERS database table.
 *
 */
@Entity
@Table(name = "ARO_FILE_RICH_SCARTO_VERS")
@NamedQuery(name = "AroFileRichScartoVers.findAll", query = "SELECT a FROM AroFileRichScartoVers a")
public class AroFileRichScartoVers implements Serializable {
    private static final long serialVersionUID = 1L;
    private long idFileRichScartoVers;
    private String blFile;
    private String tiFile;
    private AroRichScartoVers aroRichScartoVers;

    public AroFileRichScartoVers() {
        /* Hibernate */
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_FILE_RICH_SCARTO_VERS")
    public long getIdFileRichScartoVers() {
        return this.idFileRichScartoVers;
    }

    public void setIdFileRichScartoVers(long idFileRichScartoVers) {
        this.idFileRichScartoVers = idFileRichScartoVers;
    }

    @Lob
    @Column(name = "BL_FILE")
    public String getBlFile() {
        return this.blFile;
    }

    public void setBlFile(String blFile) {
        this.blFile = blFile;
    }

    @Column(name = "TI_FILE")
    public String getTiFile() {
        return this.tiFile;
    }

    public void setTiFile(String tiFile) {
        this.tiFile = tiFile;
    }

    // bi-directional many-to-one association to AroRichScartoVers
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RICH_SCARTO_VERS")
    public AroRichScartoVers getAroRichScartoVers() {
        return this.aroRichScartoVers;
    }

    public void setAroRichScartoVers(AroRichScartoVers aroRichScartoVers) {
        this.aroRichScartoVers = aroRichScartoVers;
    }

}