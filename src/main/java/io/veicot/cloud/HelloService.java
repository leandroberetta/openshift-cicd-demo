package io.veicot.cloud;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Path("/")
public class HelloService {

	@GET
    @Produces(MediaType.APPLICATION_JSON)
	public Response get() {
		Properties properties = this.loadProperties("artifact.properties");

		HelloResponse helloResponse = new HelloResponse();

        helloResponse.setBuildNumber(properties.getProperty("artifact.buildNumber"));
        helloResponse.setGitCommit(properties.getProperty("artifact.gitCommit"));
        helloResponse.setVersion(properties.getProperty("artifact.version"));
        helloResponse.setMessage(System.getenv("HELLO_MESSAGE"));
        helloResponse.setEnvironment(System.getenv("HELLO_ENVIRONMENT"));
        helloResponse.setPodName(System.getenv("HELLO_POD_NAME"));
        helloResponse.setPodNamespace(System.getenv("HELLO_POD_NAMESPACE"));

        return Response.ok().entity(helloResponse).build();
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