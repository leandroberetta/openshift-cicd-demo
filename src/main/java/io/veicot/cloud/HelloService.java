package io.veicot.cloud;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HelloService {

	@GET
	public HelloResponse get() {
		Properties properties = this.loadProperties("artifact.properties");

		HelloResponse helloResponse = new HelloResponse();

        helloResponse.setBuildNumber(properties.getProperty("artifact.buildNumber"));
        helloResponse.setGitCommit(properties.getProperty("artifact.gitCommit"));
        helloResponse.setVersion(properties.getProperty("artifact.version"));
        helloResponse.setMessage(System.getenv("HELLO_MESSAGE"));
        helloResponse.setEnvironment(System.getenv("HELLO_ENVIRONMENT"));
        helloResponse.setPodName(System.getenv("HELLO_POD_NAME"));
        helloResponse.setPodNamespace(System.getenv("HELLO_POD_NAMESPACE"));

        return helloResponse;
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