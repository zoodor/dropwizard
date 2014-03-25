package io.dropwizard.views.jade;

import io.dropwizard.views.View;

public class AbsoluteView extends View {
    private final String name;

    public AbsoluteView(String name) {
        super("/example.jade");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
