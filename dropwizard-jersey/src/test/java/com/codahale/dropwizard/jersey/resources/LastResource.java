package com.codahale.dropwizard.jersey.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/three")
public class LastResource {
    @GET
    public String yay() {
        return "yay";
    }
}
