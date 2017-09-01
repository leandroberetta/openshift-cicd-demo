package com.redhat.gps.service;

import com.redhat.gps.model.BundleInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;



@Path("/")
public class BundleService {

    Logger logger = LoggerFactory.getLogger(BundleService.class);

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
        Optional<InputStream> optInputStream = Optional.of(getClass().getClassLoader().getResourceAsStream(fileName));
        Properties properties = new Properties();

        if (optInputStream.isPresent()) {
            System.out.println("bundle.properties present");
            logger.info("bundle.properties present");
            try {
                properties.load(optInputStream.get());
                logger.info(properties.getProperty("version"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("bundle.properties not present");
            logger.error("bundle.properties not present");
        }

        return properties;
    }
}
