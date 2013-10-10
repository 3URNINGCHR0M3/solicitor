package net.forje.solicitor;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.MavenMetadataSource;
import org.apache.maven.wagon.Streams;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class SolicitorImpl implements Solicitor {

    private final URL _localURL;
    private final URL _remoteURL;

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private ArtifactMetadataSource _metadataSource;
    private ArtifactFactory _artifactFactory;

    private ArtifactRepository _remoteRepository;
    private ArtifactRepository _localRepository;

    private MavenProjectBuilder _projectBuilder;

    public SolicitorImpl(final URL localURL,
                         final URL remoteURL) {
        _localURL = localURL;
        _remoteURL = remoteURL;
    }

    /**
     * makes sure all information is valid and then bootstraps the configuration
     */
    void bootstrap() throws SolicitorException {

        // todo check local is a directory
        // todo check can write to local url

        // todo check can connect to remote URL

        // jetty:run friendly setting
        Map<? extends Object, ? extends Object> context =
                Collections.singletonMap(PlexusConstants.IGNORE_CONTAINER_CONFIGURATION, Boolean.TRUE);

        // use Plexus to load the Maven components needed to retrieve repository information
        InputStream inputStream = getClass().getResourceAsStream("/custom-plexus.xml");

        if (inputStream == null) {
            throw new SolicitorException("Could not load plexus configuration");
        }

        InputStreamReader configurationReader = new InputStreamReader(inputStream);

        try {

            // A chain of errors occurred when initialing the container with the parameters
            // todo revisit when core functionality is complete.
            // PlexusContainer container = new DefaultPlexusContainer(configurationReader, context);

            PlexusContainer container = new DefaultPlexusContainer();

            _artifactFactory = (ArtifactFactory) container.lookup(ArtifactFactory.ROLE);

            _metadataSource =
                    (ArtifactMetadataSource) container.lookup(MavenMetadataSource.ROLE, MavenMetadataSource.ROLE_HINT);

            ArtifactRepositoryFactory repositoryFactory =
                    (ArtifactRepositoryFactory) container.lookup(ArtifactRepositoryFactory.ROLE);

            String localRepositoryUrl = new File(_localURL.toURI()).getAbsolutePath();

            _localRepository =
                    repositoryFactory.createDeploymentArtifactRepository("local", localRepositoryUrl,
                            new DefaultRepositoryLayout(), false);

            _remoteRepository =
                    repositoryFactory.createArtifactRepository("central", _remoteURL.toExternalForm(),
                            new DefaultRepositoryLayout(),
                            new ArtifactRepositoryPolicy(),
                            new ArtifactRepositoryPolicy());

            _projectBuilder = (MavenProjectBuilder) container.lookup(MavenProjectBuilder.ROLE);

        } catch (Exception e) {
            throw new RepositoryException("Error initializing Maven engine", e);
        }

    }

    /**
     * Makes sure the latest version of the specified artifact is in the local repository
     *
     * @param groupId    the group id for the artifact
     * @param artifactId the artifact id for the artifact
     *
     * @throws RepositoryException if any errors occur
     */
    public void update(final String groupId,
                       final String artifactId)
            throws RepositoryException {

        List<String> availableVersions = getAvailableVersions(groupId, artifactId);
        int size = availableVersions.size();

        if (size == 0) {
            throw new RepositoryException("Couldn't find artifact [" + groupId + ":" + artifactId + "]");
        }

        String maxVersion = availableVersions.get(size - 1);
        System.out.println("maxVersion = " + maxVersion);

        MavenProject project = retrieveProject(groupId, artifactId, maxVersion);
        Artifact artifact = project.getArtifact();

        String remotePathToArtifact = _remoteRepository.pathOf(artifact);
        System.out.println("remotePathToArtifact = " + remotePathToArtifact);

        String localPathToArtifact = _localRepository.pathOf(artifact);
        System.out.println("localPathToArtifact = " + localPathToArtifact);

        String basedir = _remoteRepository.getBasedir();
        System.out.println("basedir = " + basedir);
        String remoteRepositoryUrl = _remoteRepository.getUrl();
        System.out.println("remoteRepositoryUrl = " + remoteRepositoryUrl);

        String downloadUrl = artifact.getDownloadUrl();
        System.out.println("downloadUrl = " + downloadUrl);

        StringBuilder artifactURL = new StringBuilder();
        artifactURL.append(remoteRepositoryUrl);
        artifactURL.append("/");
        artifactURL.append(remotePathToArtifact);

        URL remoteArtifactUrl;

        try {
            remoteArtifactUrl = new URL(artifactURL.toString());
            System.out.println("artifactURL = " + remoteArtifactUrl);
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        System.out.println("localPath = " + localPathToArtifact);
        String localRepositoryUrl = _localRepository.getUrl();
        System.out.println("localRepositoryUrl = " + localRepositoryUrl);

        StringBuilder localArtifactPath = new StringBuilder();
        localArtifactPath.append(localRepositoryUrl);
        localArtifactPath.append(FILE_SEPARATOR);
        localArtifactPath.append(localPathToArtifact);

        final String fullLocalPath = localArtifactPath.toString();
        System.out.println("fullLocalPath = " + fullLocalPath);

        File file = new File(fullLocalPath);
        System.out.println("file [" + file.getAbsolutePath() + "] exists : " + file.exists());

        if (!file.exists()) {
            downloadArtifact(remoteArtifactUrl, file);
        }

    }

    private void downloadArtifact(final URL remoteArtifactUrl,
                                  final File file)
            throws RepositoryException {

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {

            File parentFile = file.getParentFile();

            if (!parentFile.exists()) {

                boolean b = parentFile.mkdirs();

                if (!b) {
                    throw new Exception("could not create directory [" + parentFile.getAbsolutePath() + "]");
                }
            }

            inputStream = remoteArtifactUrl.openStream();
            outputStream = new FileOutputStream(file);

            int i = -1;
            while ((i = inputStream.read()) > -1) {
                outputStream.write(i);
            }

        } catch (Exception e) {
            throw new RepositoryException(e.getMessage() + "[" + remoteArtifactUrl + ", " + file.getAbsolutePath() + "]", e);
        } finally {

            try {

                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ArtifactWrapper {

        private final Artifact _artifact;
        private final ArtifactHandler _artifactHandler;

        private ArtifactWrapper(final Artifact artifact) {
            _artifact = artifact;
            _artifactHandler = _artifact.getArtifactHandler();
        }

    }


    public MavenProject retrieveProject(String groupId, String artifactId, String version)
            throws RepositoryException {

        MavenProject mavenProject;

        // get the project from the repository
        Artifact artifact = _artifactFactory.createProjectArtifact(groupId, artifactId, version);

        try {
            mavenProject =
                    _projectBuilder.buildFromRepository(
                            artifact,
                            Collections.singletonList(_remoteRepository),
                            _localRepository);
        } catch (ProjectBuildingException e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        return mavenProject;

    }

    private List<String> getAvailableVersions(String groupId, String artifactId)
            throws RepositoryException {
        // the version supplied is arbitrary, but must not be null
        Artifact artifact = _artifactFactory.createProjectArtifact(groupId, artifactId, "");

        List<ArtifactVersion> versions = null;
        try {
            versions =
                    _metadataSource.retrieveAvailableVersions(
                            artifact,
                            _localRepository,
                            Collections.singletonList(_remoteRepository));
        } catch (ArtifactMetadataRetrievalException e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        // sort the versions as required by the spec
        Collections.sort(versions);

        // convert the list into one of strings to return
        List<String> availableVersions = new ArrayList<String>(versions.size());
        for (ArtifactVersion version : versions) {
            availableVersions.add(version.toString());
        }

        return availableVersions;

    }

}
