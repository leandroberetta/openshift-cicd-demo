package com.redhat.gps.service;

import com.redhat.gps.model.BundleInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

@Path("/")
public class BundleService {

    Logger logger = LoggerFactory.getLogger(BundleService.class);

    @GET
    @Path("/bundleInfo")
    @Produces("application/json")
    public BundleInfo getBundleInfo() {
        BundleInfo bundleInfo = new BundleInfo();

        Properties properties = this.loadProperties("bundle.properties");

        bundleInfo.setVersion(properties.getProperty("bundle.version"));
        bundleInfo.setArtifactId(properties.getProperty("bundle.artifactId"));
        bundleInfo.setGroupId(properties.getProperty("bundle.groupId"));

        return bundleInfo;
    }

    private Properties loadProperties(String fileName) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        Properties properties = new Properties();

        if (is != null) {
            try {
                properties.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return properties;
    }
}
