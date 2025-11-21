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
import org.springframework.context.annotation.Scope;
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
    @Scope("prototype")
    RedirectAction redirectAction() {
        return new RedirectAction();
    }

    @Bean(value = "/Login.html")
    @Scope("prototype")
    LoginAction loginAction() {
        return new LoginAction();
    }

    @Bean(value = "/Logout.html")
    @Scope("prototype")
    LogoutAction logoutAction() {
        return new LogoutAction();
    }

    /* Configurazione delle action specifiche del modulo web */
    @Bean(value = "/Home.html")
    @Scope("prototype")
    HomeAction homeAction() {
        return new HomeAction();
    }

    @Bean(value = "/SceltaOrganizzazione.html")
    @Scope("prototype")
    SceltaOrganizzazioneAction sceltaOrganizzazioneAction() {
        return new SceltaOrganizzazioneAction();
    }

    /* Action specifiche di SACER */

    @Bean(value = "/Volumi.html")
    @Scope("prototype")
    VolumiAction volumiAction() {
        return new VolumiAction();
    }

    @Bean(value = "/Componenti.html")
    @Scope("prototype")
    ComponentiAction componentiAction() {
        return new ComponentiAction();
    }

    @Bean(value = "/UnitaDocumentarie.html")
    @Scope("prototype")
    UnitaDocumentarieAction unitaDocumentarieAction() {
        return new UnitaDocumentarieAction();
    }

    @Bean(value = "/CriteriRaggruppamento.html")
    @Scope("prototype")
    CriteriRaggruppamentoAction criteriRaggruppamentoAction() {
        return new CriteriRaggruppamentoAction();
    }

    @Bean(value = "/Strutture.html")
    @Scope("prototype")
    StruttureAction struttureAction() {
        return new StruttureAction();
    }

    @Bean(value = "/StrutDatiSpec.html")
    @Scope("prototype")
    StrutDatiSpecAction strutDatiSpecAction() {
        return new StrutDatiSpecAction();
    }

    @Bean(value = "/StrutTipi.html")
    @Scope("prototype")
    StrutTipiAction strutTipiAction() {
        return new StrutTipiAction();
    }

    @Bean(value = "/Ambiente.html")
    @Scope("prototype")
    AmbienteAction AmbienteAction() {
        return new AmbienteAction();
    }

    @Bean(value = "/Amministrazione.html")
    @Scope("prototype")
    AmministrazioneAction amministrazioneAction() {
        return new AmministrazioneAction();
    }

    @Bean(value = "/Monitoraggio.html")
    @Scope("prototype")
    MonitoraggioAction monitoraggioAction() {
        return new MonitoraggioAction();
    }

    @Bean(value = "/MonitoraggioFascicoli.html")
    @Scope("prototype")
    MonitoraggioFascicoliAction monitoraggioFascicoliAction() {
        return new MonitoraggioFascicoliAction();
    }

    @Bean(value = "/ListaSessFascicoliErr.html")
    @Scope("prototype")
    MonitoraggioFascicoliAction listaSessFascicoliErr() {
        return new MonitoraggioFascicoliAction();
    }

    @Bean(value = "/MonitoraggioTpi.html")
    @Scope("prototype")
    MonitoraggioTpiAction monitoraggioTpiAction() {
        return new MonitoraggioTpiAction();
    }

    @Bean(value = "/GestioneJob.html")
    @Scope("prototype")
    GestioneJobAction gestioneJobAction() {
        return new GestioneJobAction();
    }

    @Bean(value = "/Formati.html")
    @Scope("prototype")
    FormatiAction formatiAction() {
        return new FormatiAction();
    }

    @Bean(value = "/StrutFormatoFile.html")
    @Scope("prototype")
    StrutFormatoFileAction strutFormatoFileAction() {
        return new StrutFormatoFileAction();
    }

    @Bean(value = "/StrutTipoStrut.html")
    @Scope("prototype")
    StrutTipoStrutAction strutTipoStrutAction() {
        return new StrutTipoStrutAction();
    }

    @Bean(value = "/StrutTitolari.html")
    @Scope("prototype")
    StrutTitolariAction strutTitolariAction() {
        return new StrutTitolariAction();
    }

    @Bean(value = "/MonitoraggioSintetico.html")
    @Scope("prototype")
    MonitoraggioSinteticoAction monitoraggioSinteticoAction() {
        return new MonitoraggioSinteticoAction();
    }

    @Bean(value = "/MonitoraggioAggMeta.html")
    @Scope("prototype")
    MonitoraggioAggMetaAction monitoraggioAggMetaAction() {
        return new MonitoraggioAggMetaAction();
    }

    @Bean(value = "/MonitoraggioIndiceAIP.html")
    @Scope("prototype")
    MonitoraggioIndiceAIPAction monitoraggioIndiceAIPAction() {
        return new MonitoraggioIndiceAIPAction();
    }

    @Bean(value = "/Trasformatori.html")
    @Scope("prototype")
    TrasformatoriAction trasformatoriAction() {
        return new TrasformatoriAction();
    }

    @Bean(value = "/SubStrutture.html")
    @Scope("prototype")
    SubStruttureAction subStruttureAction() {
        return new SubStruttureAction();
    }

    @Bean(value = "/StrutSerie.html")
    @Scope("prototype")
    StrutSerieAction strutSerieAction() {
        return new StrutSerieAction();
    }

    @Bean(value = "/SerieUD.html")
    @Scope("prototype")
    SerieUDAction serieUDAction() {
        return new SerieUDAction();
    }

    @Bean(value = "/SerieUdPerUtentiExt.html")
    @Scope("prototype")
    SerieUdPerUtentiExtAction serieUdPerUtentiExtAction() {
        return new SerieUdPerUtentiExtAction();
    }

    @Bean(value = "/ElenchiVersamento.html")
    @Scope("prototype")
    ElenchiVersamentoAction elenchiVersamentoAction() {
        return new ElenchiVersamentoAction();
    }

    @Bean(value = "/AnnulVers.html")
    @Scope("prototype")
    AnnulVersAction annulVersAction() {
        return new AnnulVersAction();
    }

    @Bean(value = "/ModelliSerie.html")
    @Scope("prototype")
    ModelliSerieAction modelliSerieAction() {
        return new ModelliSerieAction();
    }

    @Bean(value = "/EntiConvenzionati.html")
    @Scope("prototype")
    EntiConvenzionatiAction entiConvenzionatiAction() {
        return new EntiConvenzionatiAction();
    }

    @Bean(value = "/Fascicoli.html")
    @Scope("prototype")
    FascicoliAction fascicoliAction() {
        return new FascicoliAction();
    }

    @Bean(value = "/CriteriRaggrFascicoli.html")
    @Scope("prototype")
    CriteriRaggrFascicoliAction criteriRaggrFascicoliAction() {
        return new CriteriRaggrFascicoliAction();
    }

    @Bean(value = "/StrutTipiFascicolo.html")
    @Scope("prototype")
    StrutTipiFascicoloAction strutTipiFascicoloAction() {
        return new StrutTipiFascicoloAction();
    }

    @Bean(value = "/ElenchiVersFascicoli.html")
    @Scope("prototype")
    ElenchiVersFascicoliAction elenchiVersFascicoliAction() {
        return new ElenchiVersFascicoliAction();
    }

    @Bean(value = "/ModelliFascicoli.html")
    @Scope("prototype")
    ModelliFascicoliAction modelliFascicoliAction() {
        return new ModelliFascicoliAction();
    }

    @Bean(value = "/NoteRilascio.html")
    @Scope("prototype")
    NoteRilascioAction noteRilascioAction() {
        return new NoteRilascioAction();
    }

    @Bean(value = "/RestituzioneArchivio.html")
    @Scope("prototype")
    RestituzioneArchivioAction restituzioneArchivioAction() {
        return new RestituzioneArchivioAction();
    }

    @Bean(value = "/ModelliUD.html")
    @Scope("prototype")
    ModelliUDAction modelliUDAction() {
        return new ModelliUDAction();
    }

    @Bean(value = "/UtilizzoMicroservizi.html")
    @Scope("prototype")
    UtilizzoMicroserviziAction utilizzoMicroserviziAction() {
        return new UtilizzoMicroserviziAction();
    }

    @Bean(value = "/VerificaFirme")
    @Scope("prototype")
    VerificaFirme verificaFirme() {
        return new VerificaFirme();
    }

}
