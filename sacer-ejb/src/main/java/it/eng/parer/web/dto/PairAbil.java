package it.eng.parer.web.dto;

public class PairAbil<Classe, IdTipoDato> {

    private final Classe classe;
    private final IdTipoDato idTipoDato;

    public PairAbil(Classe appl, IdTipoDato idTipoDato) {
        this.classe = appl;
        this.idTipoDato = idTipoDato;
    }

    public Classe getClasse() {
        return classe;
    }

    public IdTipoDato getIdTipoDato() {
        return idTipoDato;
    }

    @Override
    public int hashCode() {
        return classe.hashCode() ^ idTipoDato.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof PairAbil)) {
            return false;
        }
        PairAbil pairo = (PairAbil) o;
        return this.classe.equals(pairo.getClasse()) && this.idTipoDato.equals(pairo.getIdTipoDato());
    }

    @Override
    public String toString() {
        return "PairAbil{" + "classe=" + classe + ", idTipoDato=" + idTipoDato + '}';
    }
}
