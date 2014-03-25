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
    public void createModel_shouldIncludeFieldsInCustomView() throws Exception {
        View view = new CustomJadeView("");

        JadeModel model = factory.createModel(view);

        assertTrue(
                format("Model does not contain expected key: {0}", FIELD_NAME),
                model.containsKey(FIELD_NAME)
        );
        assertThat((String) model.get(FIELD_NAME), is(new CustomJadeView("").customStringField));
    }

    @Test
    public void createModel_shouldIncludeFieldsInheritedFromCustomViews() throws Exception {
        View view = new CustomJadeSubview("");

        JadeModel model = factory.createModel(view);

        assertTrue(
                format("Model does not contain expected key: {0}", FIELD_NAME),
                model.containsKey(FIELD_NAME)
        );
        assertThat((String) model.get(FIELD_NAME), is(new CustomJadeView("").customStringField));
    }

    @Test
    public void createModel_shouldNotIncludeFieldsInheritedFromDropwizardViewClass() throws Exception {
        final String VIEW_FIELD_NAME = "templateName";
        View view = new CustomJadeView("");

        JadeModel model = factory.createModel(view);

        assertFalse(
                format("Model contains unexpected key: {0}", VIEW_FIELD_NAME),
                model.containsKey(VIEW_FIELD_NAME)
        );
    }

    private static class CustomJadeView extends View {
        protected CustomJadeView(final String templateName) {
            super(templateName);
        }

        private final String customStringField = "some value";
    }

    private static class CustomJadeSubview extends CustomJadeView {
        protected CustomJadeSubview(final String templateName) {
            super(templateName);
        }
    }
}
