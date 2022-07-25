package it.eng.parer.firma.crypto.helper;

import it.eng.parer.exception.ParamApplicNotFoundException;
import it.eng.parer.retry.RestConfiguratorHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stateless bean che (auto) configura il comportamento della modalità "Retry" di un metodo. Questo helper include la
 * funzionalità di retry applicato a delle chiamate a endpoint RESTful.
 *
 * @author Snidero_L
 */
@Stateless
@LocalBean
public class CryptoRestConfiguratorHelper implements RestConfiguratorHelper {

    private final Logger log = LoggerFactory.getLogger(CryptoRestConfiguratorHelper.class);

    private static final String CRYPTO_CLIENT_TIMEOUT = "VERIFICA_FIRMA_TIMEOUT";

    /* CRYPTO */
    private static final String CRYPTO_RETRY_TIMEOUT = "CRYPTO_VERIFICA_FIRMA_RETRY_TIMEOUT";

    private static final String CRYPTO_MAX_TENTATIVI = "CRYPTO_VERIFICA_FIRMA_MAX_TENTATIVI";

    private static final String CRYPTO_CIRCUIT_BREAKER_OPEN_TIMEOUT = "CRYPTO_VERIFICA_FIRMA_CIRCUIT_BREAKER_OPEN_TIMEOUT";

    private static final String CRYPTO_CIRCUIT_BREAKER_RESET_TIMEOUT = "CRYPTO_VERIFICA_FIRMA_CIRCUIT_BREAKER_RESET_TIMEOUT";

    private static final String CRYPTO_PERIODO_BACKOFF = "CRYPTO_VERIFICA_FIRMA_PERIODO_BACKOFF";

    private static final String CRYPTO_ENDPOINT = "CRYPTO_VERIFICA_FIRMA_ENDPOINT";

    private static final String CRYPTO_COMPOSITE_POLICY_OPTIMISTIC = "CRYPTO_COMPOSITE_POLICY_OPTIMISTIC";

    private static final String PARAMETRO_NON_TROVATO = "Parametro {} non trovato. Utilizzo il valore predefinito.";

    private static final String NUMBER_REGEXP = "^[0-9]+$";

    private static final String ENDPOINT_SEPARATOR = "\\|";

    @EJB
    protected ConfigurationHelper configurationHelper;

    private Long getLongParameter(final String name) {
        Long paramValue = null;
        try {
            final String paramValueString = configurationHelper.getValoreParamApplicByApplic(name);
            if (paramValueString != null && paramValueString.matches(NUMBER_REGEXP)) {
                paramValue = Long.parseLong(paramValueString);
            }
        } catch (ParamApplicNotFoundException | NumberFormatException e) {
            log.warn(PARAMETRO_NON_TROVATO, name);

        }
        return paramValue;
    }

    private Integer getIntParameter(final String name) {
        Integer paramValue = null;
        try {
            final String paramValueString = configurationHelper.getValoreParamApplicByApplic(name);
            if (paramValueString != null && paramValueString.matches(NUMBER_REGEXP)) {
                paramValue = Integer.parseInt(paramValueString);
            }

        } catch (ParamApplicNotFoundException | NumberFormatException e) {
            log.warn(PARAMETRO_NON_TROVATO, name);
        }
        return paramValue;
    }

    private Boolean getBooleanParameter(final String name) {
        Boolean paramValue = true;
        try {
            final String boolParameterString = configurationHelper.getValoreParamApplicByApplic(name);
            paramValue = Boolean.parseBoolean(boolParameterString);

        } catch (ParamApplicNotFoundException e) {
            log.warn(PARAMETRO_NON_TROVATO, name);

        }
        return paramValue;
    }

    @Override
    public Long getRetryTimeoutParam() {
        Long value = getLongParameter(CRYPTO_RETRY_TIMEOUT);
        if (value != null && value < 2000L) {
            log.warn("Attenzione, il parametro {} è stato configurato con un valore inferiore a 2 secondi.",
                    CRYPTO_RETRY_TIMEOUT);
        }

        return value;
    }

    @Override
    public Integer getMaxRetryParam() {
        Integer value = getIntParameter(CRYPTO_MAX_TENTATIVI);
        if (value != null && value < 2) {
            log.warn("Attenzione, il parametro {} è stato configurato per un numero di tentativi inferiore a 2.",
                    CRYPTO_MAX_TENTATIVI);
        }
        return value;
    }

    @Override
    public Long getCircuitBreakerOpenTimeoutParam() {
        return getLongParameter(CRYPTO_CIRCUIT_BREAKER_OPEN_TIMEOUT);
    }

    @Override
    public Long getCircuitBreakerResetTimeoutParam() {
        return getLongParameter(CRYPTO_CIRCUIT_BREAKER_RESET_TIMEOUT);
    }

    @Override
    public Long getPeriodoBackOffParam() {
        return getLongParameter(CRYPTO_PERIODO_BACKOFF);
    }

    @Override
    public Long getClientTimeoutInMinutesParam() {
        Long value = getLongParameter(CRYPTO_CLIENT_TIMEOUT);
        if (value != null && value < 2L) {
            log.warn("Attenzione, il parametro {} è stato configurato con un valore inferiore a 2 minuti.",
                    CRYPTO_CLIENT_TIMEOUT);

        }
        return value;
    }

    @Override
    public Boolean isCompositePolicyOptimisticParam() {
        return getBooleanParameter(CRYPTO_COMPOSITE_POLICY_OPTIMISTIC);
    }

    /**
     * Lista degli endpoint per i servizi REST. Tendenzialmente questa verrà trattata come una lista circolare.
     *
     * @return lista di endpoint
     */
    @Override
    public List<String> endPoints() {
        final List<String> endPointCL = new LinkedList<>();
        final String endPointsString = configurationHelper.getValoreParamApplicByApplic(CRYPTO_ENDPOINT);
        Pattern.compile(ENDPOINT_SEPARATOR).splitAsStream(endPointsString).map(String::trim).forEach(endpoint -> {
            endPointCL.add(endpoint);
        });

        return endPointCL;
    }

    @Override
    public String preferredEndpoint() {
        return endPoints().get(0);
    }

}
