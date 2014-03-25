package io.dropwizard.views.jade;

import com.google.common.base.Throwables;
import de.neuland.jade4j.model.JadeModel;
import io.dropwizard.views.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class JadeModelFactory {

    public final String GET_METHOD_NAME_PREFIX = "get";

    public JadeModel createModel(final View view) {
        final JadeModel jadeModel =
                new JadeModel(new HashMap<String, Object>() {});
        addMethodsFromClassHierarchy(view, jadeModel);
        return jadeModel;
    }

    private void addMethodsFromClassHierarchy(final View view, final JadeModel jadeModel) {
        Class clazz = view.getClass();
        while (!clazz.equals(View.class)) {
            addMethodsFromClass(jadeModel, view, clazz);
            clazz = clazz.getSuperclass();
        }
    }

    private void addMethodsFromClass(final JadeModel jadeModel, final View view, final Class clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (!isGetterMethod(method)) {
                continue;
            }

            addMethod(jadeModel, view, method);
        }
    }

    private boolean isGetterMethod(final Method method) {
        return method.getName().startsWith(GET_METHOD_NAME_PREFIX)
                && method.getParameterTypes().length == 0;
    }

    private void addMethod(final JadeModel jadeModel, final View view, final Method method) {
        try {
            final String getterName = method.getName();
            final String getterNameWithoutGetPrefix = getterName.substring(3);
            final String nameWithLowerCasedFirstLetter = getterNameWithoutGetPrefix.substring(0, 1).toLowerCase() + getterNameWithoutGetPrefix.substring(1);
            jadeModel.put(nameWithLowerCasedFirstLetter, method.invoke(view));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Error: Failed to access public getter.");
        } catch (InvocationTargetException e) {
            Throwables.propagate(e.getCause());
        }
    }
}
