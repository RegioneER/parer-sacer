package it.eng.parer.web.servlet;

import it.eng.spagoCore.ConfigServlet;

import javax.servlet.annotation.WebServlet;

/**
 * Servlet di configurazione di SpagoLite che eredita dalla superclasse nel framework.
 *
 * Supporta i parametri di configurazione definiti dalle costanti sottostanti.
 */
@WebServlet(name = "ConfigServlet", loadOnStartup = 2)
public class SacerConfigServlet extends ConfigServlet {

}
