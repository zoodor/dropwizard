package com.yammer.dropwizard.jersey.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/one")
public class ExampleResource {
    @GET
    public String yay() {
        return "yay";
    }
}
