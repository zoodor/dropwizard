package io.dropwizard.views.jade;

import de.neuland.jade4j.model.JadeModel;
import io.dropwizard.views.View;

import java.util.HashMap;

public class AbsoluteView extends View implements JadeView {
    private JadeModel model;

    public AbsoluteView(String name) {
        super("/example.jade");

        this.model = new JadeModel(new HashMap<String, Object>());
        this.model.put("name", name);
    }

    @Override
    public JadeModel getModel() {
        return model;
    }
}
