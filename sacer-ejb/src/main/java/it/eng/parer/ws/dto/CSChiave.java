/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.dto;

/**
 *
 * @author Fioravanti_F
 */
public class CSChiave implements java.io.Serializable {

    private String numero;
    private Long anno;
    private String tipoRegistro;

    public Long getAnno() {
        return anno;
    }

    public void setAnno(Long anno) {
        this.anno = anno;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    @Override
    public String toString() {

        return tipoRegistro + "-" + anno + "-" + numero;

    }
}