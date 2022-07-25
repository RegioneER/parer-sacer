package it.eng.parer.restWS;

import it.eng.parer.web.dto.CounterResultBean;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.spagoCore.error.EMFError;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Web Service
 *
 * @author Quaranta_M
 */
@Path("docounter")
@RequestScoped
public class DocCounter {

    private static final Logger LOG = LoggerFactory.getLogger(DocCounter.class);

    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;

    /**
     * Creates a new instance of DocCounter
     */
    public DocCounter() {
    }

    /**
     * Retrieves representation of an instance of it.eng.parer.restWS.DocCounter.
     *
     * @param context
     *            Oggetto iniettato contentente le informazioni dell'applicazione e della request.
     * 
     * @throws EMFError
     *             errore generico
     * 
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJson(@Context UriInfo context) throws EMFError {

        LOG.debug("Richiesta risorsa a " + context.getRequestUri());
        // try {
        // monitoraggioHelper = (MonitoraggioHelper) new
        // InitialContext().lookup("java:app/Parer-ejb/MonitoraggioHelper");
        // } catch (NamingException ex) {
        // LoggerFactory.getLogger(DocCounter.class.getName()).log(Level.SEVERE, null, ex);
        // }
        CounterResultBean result = monitoraggioHelper.getTotalMonTotSacer();

        // Nessuno deve effettuare il caching di questa risorsa
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);
        cc.setMaxAge(-1);
        cc.setMustRevalidate(true);

        return Response.ok(result).cacheControl(cc).build();
    }
}
