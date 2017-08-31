package io.veicot.examples.model;

/**
 * Created by lberetta on 7/15/17.
 */
public class BundleInfo {

    private String version;
    private String artifactId;
    private String groupId;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
