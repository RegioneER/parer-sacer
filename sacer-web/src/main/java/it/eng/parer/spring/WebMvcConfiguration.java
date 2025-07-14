/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.spring;

import it.eng.paginator.ejb.PaginatorImpl;
import it.eng.parer.crypto.test.VerificaFirme;
import it.eng.parer.sacerlog.web.spring.SpagoliteWebMvcConfiguration;
import it.eng.parer.web.security.SacerAuthenticator;
import it.eng.parer.web.util.ApplicationBasePropertiesSeviceImpl;
import it.eng.parer.web.action.*;
import it.eng.spagoLite.actions.RedirectAction;
import it.eng.spagoLite.actions.security.LoginAction;
import it.eng.spagoLite.actions.security.LogoutAction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 *
 * @author Marco Iacolucci
 */
@EnableWebMvc
// DA SISTEMARE!!!
@ComponentScan(basePackages = {
	"it.eng.parer.web", "it.eng.parer.web.rest.controller", "it.eng.parer.ws",
	"it.eng.parer.spring", "it.eng.parer.web.action", "it.eng.parer.slite.gen.action",
	"it.eng.spagoCore", "it.eng.spagoLite" })
@Configuration
public class WebMvcConfiguration extends SpagoliteWebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
	registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	/*
	 * qui si dichiarano le risorse statiche
	 */
	registry.addResourceHandler("/css/**", "/images/**", "/img/**", "/js/**", "/webjars/**")
		.addResourceLocations("/css/", "/images/", "/img/", "/js/", "/webjars/")
		.setCachePeriod(0);
	registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    public InternalResourceViewResolver resolver() {
	InternalResourceViewResolver resolver = new InternalResourceViewResolver();
	resolver.setViewClass(JstlView.class);
	resolver.setPrefix("/jsp/");
	resolver.setSuffix(".jsp");
	// resolver.setExposedContextBeanNames("ricercheLoader");
	return resolver;
    }

    @Bean(name = "paginator")
    PaginatorImpl paginatorImpl() {
	return new PaginatorImpl();
    }

    /*
     * Classe che va a caricare le autorizzazioni da IAM
     */
    @Bean(name = "authenticator")
    SacerAuthenticator sacerAuthenticator() {
	return new SacerAuthenticator();
    }

    @Bean
    RestTemplate restTemplate() {
	SimpleClientHttpRequestFactory c = new SimpleClientHttpRequestFactory();
	c.setReadTimeout(10000);
	c.setConnectTimeout(10000);
	return new RestTemplate(c);
    }

    /*
     * Template da inserire nelle applicazioni che usano SpagoLite e che utilizzano l' Help On line.
     * Deve implementare l'interfaccia IApplicationBasePropertiesSevice
     *
     */
    @Bean
    ApplicationBasePropertiesSeviceImpl applicationBasePropertiesSeviceImpl() {
	return new ApplicationBasePropertiesSeviceImpl();
    }

    /*
     * Serve per parametrizzare l'applicazione specifica per esempio per caricare le variabili di
     * sistema che hanno come suffisso ad esempio "sacer".
     */
    @Bean
    String nomeApplicazione() {
	return "sacer";
    }

    /*
     * CONFIGURAZIONE DEI BEAN DELLE ACTION che prima erano nell'xml di springweb Configurazione
     * delle action ereditate dal framework
     */
    @Bean(value = "/View.html")
    RedirectAction redirectAction() {
	return new RedirectAction();
    }

    @Bean(value = "/Login.html")
    LoginAction loginAction() {
	return new LoginAction();
    }

    @Bean(value = "/Logout.html")
    LogoutAction logoutAction() {
	return new LogoutAction();
    }

    /* Configurazione delle action specifiche del modulo web */
    @Bean(value = "/Home.html")
    HomeAction homeAction() {
	return new HomeAction();
    }

    @Bean(value = "/SceltaOrganizzazione.html")
    SceltaOrganizzazioneAction sceltaOrganizzazioneAction() {
	return new SceltaOrganizzazioneAction();
    }

    /* Action specifiche di SACER */

    @Bean(value = "/Volumi.html")
    VolumiAction volumiAction() {
	return new VolumiAction();
    }

    @Bean(value = "/Componenti.html")
    ComponentiAction componentiAction() {
	return new ComponentiAction();
    }

    @Bean(value = "/UnitaDocumentarie.html")
    UnitaDocumentarieAction unitaDocumentarieAction() {
	return new UnitaDocumentarieAction();
    }

    @Bean(value = "/CriteriRaggruppamento.html")
    CriteriRaggruppamentoAction criteriRaggruppamentoAction() {
	return new CriteriRaggruppamentoAction();
    }

    @Bean(value = "/Strutture.html")
    StruttureAction struttureAction() {
	return new StruttureAction();
    }

    @Bean(value = "/StrutDatiSpec.html")
    StrutDatiSpecAction strutDatiSpecAction() {
	return new StrutDatiSpecAction();
    }

    @Bean(value = "/StrutTipi.html")
    StrutTipiAction strutTipiAction() {
	return new StrutTipiAction();
    }

    @Bean(value = "/Ambiente.html")
    AmbienteAction AmbienteAction() {
	return new AmbienteAction();
    }

    @Bean(value = "/Amministrazione.html")
    AmministrazioneAction amministrazioneAction() {
	return new AmministrazioneAction();
    }

    @Bean(value = "/Monitoraggio.html")
    MonitoraggioAction monitoraggioAction() {
	return new MonitoraggioAction();
    }

    @Bean(value = "/MonitoraggioFascicoli.html")
    MonitoraggioFascicoliAction monitoraggioFascicoliAction() {
	return new MonitoraggioFascicoliAction();
    }

    @Bean(value = "/ListaSessFascicoliErr.html")
    MonitoraggioFascicoliAction listaSessFascicoliErr() {
	return new MonitoraggioFascicoliAction();
    }

    @Bean(value = "/MonitoraggioTpi.html")
    MonitoraggioTpiAction monitoraggioTpiAction() {
	return new MonitoraggioTpiAction();
    }

    @Bean(value = "/GestioneJob.html")
    GestioneJobAction gestioneJobAction() {
	return new GestioneJobAction();
    }

    @Bean(value = "/Formati.html")
    FormatiAction formatiAction() {
	return new FormatiAction();
    }

    @Bean(value = "/StrutFormatoFile.html")
    StrutFormatoFileAction strutFormatoFileAction() {
	return new StrutFormatoFileAction();
    }

    @Bean(value = "/StrutTipoStrut.html")
    StrutTipoStrutAction strutTipoStrutAction() {
	return new StrutTipoStrutAction();
    }

    @Bean(value = "/StrutTitolari.html")
    StrutTitolariAction strutTitolariAction() {
	return new StrutTitolariAction();
    }

    @Bean(value = "/MonitoraggioSintetico.html")
    MonitoraggioSinteticoAction monitoraggioSinteticoAction() {
	return new MonitoraggioSinteticoAction();
    }

    @Bean(value = "/MonitoraggioAggMeta.html")
    MonitoraggioAggMetaAction monitoraggioAggMetaAction() {
	return new MonitoraggioAggMetaAction();
    }

    @Bean(value = "/MonitoraggioIndiceAIP.html")
    MonitoraggioIndiceAIPAction monitoraggioIndiceAIPAction() {
	return new MonitoraggioIndiceAIPAction();
    }

    @Bean(value = "/Trasformatori.html")
    TrasformatoriAction trasformatoriAction() {
	return new TrasformatoriAction();
    }

    @Bean(value = "/SubStrutture.html")
    SubStruttureAction subStruttureAction() {
	return new SubStruttureAction();
    }

    @Bean(value = "/StrutSerie.html")
    StrutSerieAction strutSerieAction() {
	return new StrutSerieAction();
    }

    @Bean(value = "/SerieUD.html")
    SerieUDAction serieUDAction() {
	return new SerieUDAction();
    }

    @Bean(value = "/SerieUdPerUtentiExt.html")
    SerieUdPerUtentiExtAction serieUdPerUtentiExtAction() {
	return new SerieUdPerUtentiExtAction();
    }

    @Bean(value = "/ElenchiVersamento.html")
    ElenchiVersamentoAction elenchiVersamentoAction() {
	return new ElenchiVersamentoAction();
    }

    @Bean(value = "/AnnulVers.html")
    AnnulVersAction annulVersAction() {
	return new AnnulVersAction();
    }

    @Bean(value = "/ModelliSerie.html")
    ModelliSerieAction modelliSerieAction() {
	return new ModelliSerieAction();
    }

    @Bean(value = "/EntiConvenzionati.html")
    EntiConvenzionatiAction entiConvenzionatiAction() {
	return new EntiConvenzionatiAction();
    }

    @Bean(value = "/Fascicoli.html")
    FascicoliAction fascicoliAction() {
	return new FascicoliAction();
    }

    @Bean(value = "/CriteriRaggrFascicoli.html")
    CriteriRaggrFascicoliAction criteriRaggrFascicoliAction() {
	return new CriteriRaggrFascicoliAction();
    }

    @Bean(value = "/StrutTipiFascicolo.html")
    StrutTipiFascicoloAction strutTipiFascicoloAction() {
	return new StrutTipiFascicoloAction();
    }

    @Bean(value = "/ElenchiVersFascicoli.html")
    ElenchiVersFascicoliAction elenchiVersFascicoliAction() {
	return new ElenchiVersFascicoliAction();
    }

    @Bean(value = "/ModelliFascicoli.html")
    ModelliFascicoliAction modelliFascicoliAction() {
	return new ModelliFascicoliAction();
    }

    @Bean(value = "/NoteRilascio.html")
    NoteRilascioAction noteRilascioAction() {
	return new NoteRilascioAction();
    }

    @Bean(value = "/RestituzioneArchivio.html")
    RestituzioneArchivioAction restituzioneArchivioAction() {
	return new RestituzioneArchivioAction();
    }

    @Bean(value = "/ModelliUD.html")
    ModelliUDAction modelliUDAction() {
	return new ModelliUDAction();
    }

    @Bean(value = "/UtilizzoMicroservizi.html")
    UtilizzoMicroserviziAction utilizzoMicroserviziAction() {
	return new UtilizzoMicroserviziAction();
    }

    @Bean(value = "/VerificaFirme")
    VerificaFirme verificaFirme() {
	return new VerificaFirme();
    }

}
