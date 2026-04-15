package it.eng.parer.xml.xsd;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Test semplice (non Arquillian) per verificare struttura classi
 */
public class XsdValidationServiceSimpleTest {

    @Test
    void testClassExists() {
        // Verifica che le classi siano compilabili
        assertNotNull(XsdValidationService.class);
        assertNotNull(it.eng.parer.xml.xsd.helper.XsdRepositoryHelper.class);
        assertNotNull(it.eng.parer.xml.xsd.DbXsdResourceResolver.class);
    }
}
