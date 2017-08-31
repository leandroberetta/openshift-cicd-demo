package com.redhat.gps.service;

import com.redhat.gps.model.PodInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class PodInfoService {

    @GET
    @Path("/podInfo")
    @Produces("application/json")
    public PodInfo getPodInfo() {
        PodInfo podInfo = new PodInfo();

        podInfo.setName(System.getenv("POD_NAME"));
        podInfo.setNamespace(System.getenv("POD_NAMESPACE"));

        return podInfo;
    }
}
