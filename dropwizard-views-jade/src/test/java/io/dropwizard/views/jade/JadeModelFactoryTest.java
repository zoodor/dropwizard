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

    public final String FIELD_NAME = "customStringField";

    public JadeModelFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new JadeModelFactory();
    }

    @Test
    public void createModel_shouldIncludePublicGettersInCustomView() throws Exception {
        View view = new CustomJadeView("");

        JadeModel model = factory.createModel(view);

        assertTrue(
                format("Model does not contain expected key: {0}", FIELD_NAME),
                model.containsKey(FIELD_NAME)
        );
        assertThat((String) model.get(FIELD_NAME), is(new CustomJadeView("").getCustomStringField()));
    }

    @Test
    public void createModel_shouldExcludeGettersThatTakeParams() throws Exception {
        View view = new CustomJadeView("") {
            @SuppressWarnings("unused")
            public String getWithParam(final String someParam) { return ""; }
        };

        JadeModel model = factory.createModel(view);

        assertFalse(
                format("Model contains key: {0}", "withParam"),
                model.containsKey("withParam")
        );
    }

    @Test
    public void createModel_shouldExcludeMethodsWithoutGetPrefix() throws Exception {
        View view = new CustomJadeView("") {
            @SuppressWarnings("unused")
            public String notAGetMethod() { return "value to exclude"; }
        };

        JadeModel model = factory.createModel(view);

        assertFalse(
                format("Model contains key: {0}", "aGetMethod"),
                model.containsKey("aGetMethod")
        );
        assertFalse(
                format("Model contains value: {0}", "value to exclude"),
                model.containsValue("value to exclude")
        );
    }

    @Test
    public void createModel_shouldExcludeProtectedMethods() throws Exception {
        final String PROTECTED_FIELD_NAME = "protected";
        View view = new CustomJadeView("") {
            @SuppressWarnings("unused")
            protected String getProtected() { return ""; }
        };

        JadeModel model = factory.createModel(view);

        assertFalse(
                format("Model contains key: {0}", PROTECTED_FIELD_NAME),
                model.containsKey(PROTECTED_FIELD_NAME)
        );
    }

    @Test
    public void createModel_shouldIncludeInheritedGetters() throws Exception {
        View view = new CustomJadeSubview("");

        JadeModel model = factory.createModel(view);

        assertTrue(
                format("Model does not contain expected key: {0}", FIELD_NAME),
                model.containsKey(FIELD_NAME)
        );
        assertThat((String) model.get(FIELD_NAME), is(new CustomJadeView("").getCustomStringField()));
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
