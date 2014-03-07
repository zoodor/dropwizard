package io.dropwizard.views.jade;

import de.neuland.jade4j.model.JadeModel;
import io.dropwizard.views.View;

import java.util.HashMap;

public class BadView extends View implements JadeView {
    public BadView() {
        super("/woo-oo-ahh.txt.jade");
    }

    @Override
    public JadeModel getModel() {
        return new JadeModel(new HashMap<String, Object>());
    }
}
