package com.yammer.dropwizard.jetty;

import com.google.common.collect.Lists;
import org.eclipse.jetty.servlet.FilterHolder;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class FilterBuilder {
    private final FilterHolder holder;
    private final EnumSet<DispatcherType> dispatchers;
    private final List<String> paths;

    FilterBuilder(String pathSpec, String... otherPaths) {
        this.holder = new FilterHolder();
        this.paths = Lists.newArrayList();
        paths.add(pathSpec);
        Collections.addAll(paths, otherPaths);
        this.dispatchers = EnumSet.of(DispatcherType.REQUEST);
    }

    public FilterBuilder through(Class<? extends Filter> filter) {
        holder.setHeldClass(filter);
        return this;
    }

    public FilterBuilder through(Filter filter) {
        holder.setFilter(filter);
        return this;
    }

    public FilterBuilder dispatcher(DispatcherType... types) {
        dispatchers.addAll(Arrays.asList(types));
        return this;
    }

    public FilterBuilder initParam(String name, String value) {
        holder.setInitParameter(name, value);
        return this;
    }

    FilterHolder getHolder() {
        return holder;
    }

    EnumSet<DispatcherType> getDispatchers() {
        return dispatchers;
    }

    List<String> getPaths() {
        return paths;
    }
}
