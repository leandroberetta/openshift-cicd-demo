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

        bundleInfo.setVersion(properties.getProperty("version"));
        bundleInfo.setArtifactId(properties.getProperty("artifactId"));
        bundleInfo.setGroupId(properties.getProperty("groupId"));

        return bundleInfo;
    }

    private Properties loadProperties(String fileName) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        Properties properties = new Properties();


        String text = null;
        try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();

            logger.info(text);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (is != null) {
            System.out.println("bundle.properties present");
            logger.info("bundle.properties present");

            try {
                properties.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info(properties.getProperty("version"));

        } else {
            System.out.println("bundle.properties not present");
            logger.error("bundle.properties not present");
        }

        return properties;
    }
}
