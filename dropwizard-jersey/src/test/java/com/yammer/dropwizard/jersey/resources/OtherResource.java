package com.yammer.dropwizard.jersey.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/two")
public class OtherResource {
    @GET
    public String yay() {
        return "yay";
    }
}
