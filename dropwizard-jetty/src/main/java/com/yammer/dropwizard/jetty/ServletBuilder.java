package com.yammer.dropwizard.jetty;

import com.google.common.collect.Lists;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;
import java.util.Collections;
import java.util.List;

public class ServletBuilder {
    private final ServletHolder holder;
    private final List<String> paths;

    ServletBuilder(String pathSpec, String... otherPaths) {
        this.holder = new ServletHolder();
        this.paths = Lists.newArrayList();
        paths.add(pathSpec);
        Collections.addAll(paths, otherPaths);
    }

    public ServletBuilder with(Class<? extends Servlet> servlet) {
        holder.setHeldClass(servlet);
        return this;
    }

    public ServletBuilder with(Servlet servlet) {
        holder.setServlet(servlet);
        return this;
    }

    public ServletBuilder initOrder(int order) {
        holder.setInitOrder(order);
        return this;
    }

    public ServletBuilder initParam(String name, String value) {
        holder.setInitParameter(name, value);
        return this;
    }

    public ServletBuilder runAsRole(String role) {
        holder.setRunAsRole(role);
        return this;
    }

    public ServletBuilder userLink(String name, String link) {
        holder.setUserRoleLink(name, link);
        return this;
    }

    ServletHolder getHolder() {
        return holder;
    }

    List<String> getPaths() {
        return paths;
    }
}
