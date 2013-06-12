package net.forje.solicitor;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.artifact.MavenMetadataSource;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;

class Repository {

    private final ArtifactMetadataSource _metadataSource;

    private final ArtifactFactory _artifactFactory;

    private final ArtifactRepository _repository;

    private final ArtifactRepository _localRepository;

    private final MavenProjectBuilder _projectBuilder;

    public Repository(RepositoryConfig config) {

        try {

            // Plexus is not able to resolve some of the Maven resources w/o this property.
            Map<? extends Object, ? extends Object> context =
                    Collections.singletonMap(PlexusConstants.IGNORE_CONTAINER_CONFIGURATION, Boolean.TRUE);

            // use Plexus to load the Maven components needed to retrieve _repository information
            final InputStreamReader configurationReader =
                    new InputStreamReader(getClass().getResourceAsStream("/custom-plexus.xml"));

            final PlexusContainer container = new DefaultPlexusContainer();

            _artifactFactory = (ArtifactFactory) container.lookup(ArtifactFactory.ROLE);

            _metadataSource =
                    (ArtifactMetadataSource) container.lookup(MavenMetadataSource.ROLE, MavenMetadataSource.ROLE_HINT);

            final ArtifactRepositoryFactory repositoryFactory =
                    (ArtifactRepositoryFactory) container.lookup(ArtifactRepositoryFactory.ROLE);

            final String localRepositoryUrl = new File(config.getLocalRepositoryPath()).toURL().toExternalForm();
            _localRepository =
                    repositoryFactory.createDeploymentArtifactRepository("local", localRepositoryUrl,
                            new DefaultRepositoryLayout(), false);

            _repository =
                    repositoryFactory.createArtifactRepository("central", config.getRepositoryUrl(),
                            new DefaultRepositoryLayout(),
                            new ArtifactRepositoryPolicy(),
                            new ArtifactRepositoryPolicy());

            _projectBuilder = (MavenProjectBuilder) container.lookup(MavenProjectBuilder.ROLE);


        } catch (Exception e) {
            throw new RuntimeException("Error starting Maven components for repository service", e);
        }



    }

}
