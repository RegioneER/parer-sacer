package it.eng.parer.web.dto;

public class PairAuth<Appl, Auth> {

    private final Appl appl;
    private final Auth auth;

    public PairAuth(Appl appl, Auth auth) {
        this.appl = appl;
        this.auth = auth;
    }

    public Appl getAppl() {
        return appl;
    }

    public Auth getAuth() {
        return auth;
    }

    @Override
    public int hashCode() {
        return appl.hashCode() ^ auth.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof PairAuth)) {
            return false;
        }
        PairAuth pairo = (PairAuth) o;
        return this.appl.equals(pairo.getAppl()) && this.auth.equals(pairo.getAuth());
    }

    @Override
    public String toString() {
        return "PairAuth{" + "appl=" + appl + ", auth=" + auth + '}';
    }
}
