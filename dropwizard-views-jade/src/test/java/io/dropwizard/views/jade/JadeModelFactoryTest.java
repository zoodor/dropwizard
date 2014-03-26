package io.dropwizard.views.jade;

import de.neuland.jade4j.model.JadeModel;
import io.dropwizard.views.View;
import org.junit.Before;
import org.junit.Test;

import static java.text.MessageFormat.format;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class JadeModelFactoryTest {

    public final String EXPECTED_KEY = "customStringField";

    public JadeModelFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new JadeModelFactory();
    }

    @Test
    public void createModel_shouldIncludePublicGettersInCustomView() throws Exception {
        CustomJadeView view = new CustomJadeView("");

        JadeModel model = factory.createModel(view);

        assertTrue(
                format("Model does not contain expected key: {0}", EXPECTED_KEY),
                model.containsKey(EXPECTED_KEY)
        );
        assertThat(
                (String) model.get(EXPECTED_KEY),
                is(view.getCustomStringField())
        );
    }

    @Test
    public void createModel_shouldIncludeInheritedGetters() throws Exception {
        CustomJadeSubview view = new CustomJadeSubview("");

        JadeModel model = factory.createModel(view);

        assertTrue(
                format("Model does not contain expected key: {0}", EXPECTED_KEY),
                model.containsKey(EXPECTED_KEY)
        );
        assertThat(
                (String) model.get(EXPECTED_KEY),
                is(view.getCustomStringField())
        );
    }

    @Test
    public void createModel_shouldExcludeGettersThatTakeParams() throws Exception {
        final String excludedKey = "withParam";
        final String excludedValue = "value to exclude";
        View view = new CustomJadeView("") {
            @SuppressWarnings("unused")
            public String getWithParam(final String someParam) {
                return excludedValue;
            }
        };

        JadeModel model = factory.createModel(view);

        assertFalse(
                format("Model contains key: {0}", excludedKey),
                model.containsKey(excludedKey)
        );
        assertFalse(
                format("Model contains expectedValue: {0}", excludedValue),
                model.containsValue(excludedValue)
        );
    }

    @Test
    public void createModel_shouldExcludeMethodsWithoutGetPrefix() throws Exception {
        final String excludedKey = "aGetMethod";
        final String excludedValue = "expectedValue to exclude";
        View view = new CustomJadeView("") {
            @SuppressWarnings("unused")
            public String notAGetMethod() {
                return excludedValue;
            }
        };

        JadeModel model = factory.createModel(view);

        assertFalse(
                format("Model contains key: {0}", excludedKey),
                model.containsKey(excludedKey)
        );
        assertFalse(
                format("Model contains expectedValue: {0}", excludedValue),
                model.containsValue(excludedValue)
        );
    }

    @Test
    public void createModel_shouldExcludeProtectedMethods() throws Exception {
        final String exludedKey = "protected";
        final String excludedValue = "excluded value";
        View view = new CustomJadeView("") {
            @SuppressWarnings("unused")
            protected String getProtected() {
                return excludedValue;
            }
        };

        JadeModel model = factory.createModel(view);

        assertFalse(
                format("Model contains key: {0}", exludedKey),
                model.containsKey(exludedKey)
        );
        assertFalse(
                format("Model contains expectedValue: {0}", excludedValue),
                model.containsValue(excludedValue)
        );
    }

    private static class CustomJadeView extends View {
        protected CustomJadeView(final String templateName) {
            super(templateName);
        }

        public String getCustomStringField() {
            return "some value";
        }
    }

    private static class CustomJadeSubview extends CustomJadeView {
        protected CustomJadeSubview(final String templateName) {
            super(templateName);
        }
    }
}
