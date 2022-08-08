package it.eng.parer.web.security;

import it.eng.parer.web.helper.UserHelper;
import it.eng.spagoLite.security.saml.SliteSAMLUserDetail;
import java.util.List;
import javax.ejb.EJB;
import org.slf4j.Logger;
import it.eng.parer.grantedEntity.UsrUser;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;

/**
 *
 * @author MIacolucci
 */
public class SacerSAMLUserDetail extends SliteSAMLUserDetail {

    @EJB(mappedName = "java:app/Parer-ejb/UserHelper")
    private UserHelper userHelper;

    private static final Logger logger = LoggerFactory.getLogger(SacerSAMLUserDetail.class);

    @Override
    protected List<UtenteDb> findUtentiPerCodiceFiscale(String codiceFiscale) {
        ArrayList al = new ArrayList<UtenteDb>();
        List<UsrUser> l = userHelper.findByCodiceFiscale(codiceFiscale);
        for (UsrUser usrUser : l) {
            UtenteDb u = new UtenteDb();
            u.setId(usrUser.getIdUserIam());
            u.setCodiceFiscale(usrUser.getCdFisc());
            u.setDataScadenzaPassword(u.getDataScadenzaPassword());
            u.setUsername(usrUser.getNmUserid());
            al.add(u);
        }
        return al;
    }

    @Override
    protected UtenteDb getUtentePerUsername(String username) {
        UsrUser usrUser = userHelper.findUsrUser(username);
        UtenteDb u = new UtenteDb();
        u.setId(usrUser.getIdUserIam());
        u.setCodiceFiscale(usrUser.getCdFisc());
        u.setDataScadenzaPassword(u.getDataScadenzaPassword());
        u.setUsername(usrUser.getNmUserid());
        return u;
    }

    @Override
    protected List<UtenteDb> findUtentiPerUsernameCaseInsensitive(String username) {
        List<UtenteDb> al = new ArrayList<>();
        List<UsrUser> l = userHelper.findUtentiPerUsernameCaseInsensitive(username);
        for (UsrUser usrUser : l) {
            UtenteDb u = new UtenteDb();
            u.setId(usrUser.getIdUserIam());
            u.setCodiceFiscale(usrUser.getCdFisc());
            u.setDataScadenzaPassword(u.getDataScadenzaPassword());
            u.setUsername(usrUser.getNmUserid());
            al.add(u);
        }
        return al;
    }

}
