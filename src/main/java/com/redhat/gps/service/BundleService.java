package com.redhat.gps.service;

import com.redhat.gps.model.BundleInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

@Path("/")
public class BundleService {

    @GET
    @Path("/bundleInfo")
    @Produces("application/json")
    public BundleInfo getBundleInfo() {
        BundleInfo bundleInfo = new BundleInfo();
        Properties properties = this.loadProperties("bundle.properties");

        bundleInfo.setVersion(properties.getProperty("version"));
        bundleInfo.setArtifactId(properties.getProperty("artifactId"));
        bundleInfo.setGroupId(properties.getProperty("groupId"));

        return bundleInfo;
    }

    private Properties loadProperties(String fileName) {
        Optional<InputStream> optInputStream = Optional.of(BundleService.class.getClassLoader().getResourceAsStream(fileName));
        Properties properties = new Properties();

        if (optInputStream.isPresent()) {
            try {
                properties.load(optInputStream.get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return properties;
    }
}
