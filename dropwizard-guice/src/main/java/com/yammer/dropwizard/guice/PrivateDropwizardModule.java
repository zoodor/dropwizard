package com.yammer.dropwizard.guice;

import com.google.inject.PrivateModule;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedElementBuilder;
import com.google.inject.binder.LinkedBindingBuilder;

import java.lang.annotation.Annotation;

public abstract class PrivateDropwizardModule extends PrivateModule {
    private final Annotation annotation;
    private final Class<? extends Annotation> annotationType;

    protected PrivateDropwizardModule(Annotation annotation,
                                      Class<? extends Annotation> annotationType) {
        this.annotation = annotation;
        this.annotationType = annotationType;
    }

    /**
     * Binds and exposes the given type to the annotation scope, if any.
     *
     * @param literal a literal of the type
     * @param <T>     the type
     * @return a binding builder for the type
     */
    protected final <T> LinkedBindingBuilder<T> bindAndExpose(TypeLiteral<T> literal) {
        final AnnotatedBindingBuilder<T> binder = bind(literal);
        final AnnotatedElementBuilder expose = expose(literal);
        if (annotation == null && annotationType == null) {
            return binder;
        } else if (annotation != null) {
            expose.annotatedWith(annotation);
            return binder.annotatedWith(annotation);
        } else {
            expose.annotatedWith(annotationType);
            return binder.annotatedWith(annotationType);
        }
    }

    /**
     * Binds and exposes the given type to the annotation scope, if any.
     *
     * @param klass the type's class
     * @param <T>   the type
     * @return a binding builder for the type
     */
    protected final <T> LinkedBindingBuilder<T> bindAndExpose(Class<T> klass) {
        return bindAndExpose(TypeLiteral.get(klass));
    }
}
