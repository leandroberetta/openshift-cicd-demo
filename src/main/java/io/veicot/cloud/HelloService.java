package io.veicot.cloud;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@Path("/")
public class HelloService extends Application {

	@GET
	@Produces("text/plain")
	public Response doGet() {
		Properties properties = this.loadProperties("artifact.properties");

		return Response.ok(String.format("%s (Version: %s - Build Number: %s - Git Commit: %s - Environment: %s)", 
										 System.getenv("HELLO_STRING"), 
										 properties.get("artifact.version"),
										 properties.get("artifact.buildNumber"),
										 properties.get("artifact.gitCommit"),
										 System.getenv("HELLO_ENVIRONMENT"))).build();
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
