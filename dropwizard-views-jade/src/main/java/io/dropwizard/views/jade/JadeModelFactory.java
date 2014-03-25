package io.dropwizard.views.jade;

import de.neuland.jade4j.model.JadeModel;
import io.dropwizard.views.View;

import java.lang.reflect.Field;
import java.util.HashMap;

public class JadeModelFactory {
    public JadeModel createModel(final View view) {
        final JadeModel jadeModel =
                new JadeModel(new HashMap<String, Object>() {});
        addFieldsFromClassHierarchy(view, jadeModel);
        return jadeModel;
    }

    private void addFieldsFromClassHierarchy(final View view, final JadeModel jadeModel) {
        Class clazz = view.getClass();
        while (!clazz.equals(View.class)) {
            addFieldsFromClass(jadeModel, view, clazz);

            clazz = clazz.getSuperclass();
        }
    }

    private void addFieldsFromClass(final JadeModel jadeModel, final View view, final Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            addField(jadeModel, view, field);
        }
    }

    private void addField(final JadeModel jadeModel, final View view, final Field field) {
        field.setAccessible(true);
        try {
            jadeModel.put(field.getName(), field.get(view));
        } catch (IllegalAccessException e) {
            // Accessing the field's value should never cause this exception,
            // since we are calling setAccessible(true) beforehand
            throw new IllegalStateException("Error: Failed to access field, even though setAccessible(true) has been called.");
        }
    }
}
