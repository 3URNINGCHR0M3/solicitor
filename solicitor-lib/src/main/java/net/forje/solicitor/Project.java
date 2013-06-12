package net.forje.solicitor;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

class Project {


    /** The identifier of the project, expected to be unique in the Centrepoint storage. */
    private String id;

    /** A human readable name for the project. */
    private String _name;

    /** The current version of the project. */
    private String _version;

    /** A short description of the project. */
    private String _description;

    /** The URL to the source control viewer for the project (not the connection string). */
    private String _scmUrl;

    /** The URL to the project issue tracker. */
    private String _issueTrackerUrl;

    /** A URL to the project home page. */
    private String _url;

    /** A URL for the CI system in use. */
    private String _ciManagementUrl;

    /** A URL for the repository for releases. */
    private String _repositoryUrl;

    /** A URL for the repository for snapshots. */
    private String _snapshotRepositoryUrl;

    /** A set of models added to the project. */
    private Map<String, ExtensionModel> extensionModels = new LinkedHashMap<String, ExtensionModel>();

    public String getUrl()
    {
        return _url;
    }

    public void setUrl( String url )
    {
        _url = url;
    }

    public String getName()
    {
        return _name;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        id = id;
    }

    public String getDescription()
    {
        return _description;
    }

    public void setName( String name )
    {
        _name = name;
    }

    public void setDescription( String description )
    {
        _description = description;
    }

    public String getVersion()
    {
        return _version;
    }

    public void setVersion( String version )
    {
        _version = version;
    }

    public String getScmUrl()
    {
        return _scmUrl;
    }

    public void setScmUrl( String scmUrl )
    {
        _scmUrl = scmUrl;
    }

    public String getIssueTrackerUrl()
    {
        return _issueTrackerUrl;
    }

    public void setIssueTrackerUrl( String issueTrackerUrl )
    {
        _issueTrackerUrl = issueTrackerUrl;
    }

    /**
     * Retrieve a model by the given extension identifier for non-core project information.
     * @param extension the extension identifier
     * @return the model
     */
    public ExtensionModel getExtensionModel( String extension )
    {
        return extensionModels.get( extension );
    }

    /**
     * Retrieve all available models for non-core project information that have already been added to the project.
     * @return the models
     */
    public Collection<ExtensionModel> getExtensionModels()
    {
        return extensionModels.values();
    }

    /**
     * Add non-core project information from the given model.
     * @param model the extended model information
     */
    public void addExtensionModel( ExtensionModel model )
    {
        extensionModels.put( model.getId(), model );
    }

    public void setSnapshotRepositoryUrl( String snapshotRepositoryUrl )
    {
        _snapshotRepositoryUrl = snapshotRepositoryUrl;
    }

    public String getSnapshotRepositoryUrl()
    {
        return _snapshotRepositoryUrl;
    }

    public void setRepositoryUrl( String repositoryUrl )
    {
        _repositoryUrl = repositoryUrl;
    }

    public String getRepositoryUrl()
    {
        return _repositoryUrl;
    }

    public void setCiManagementUrl( String ciManagementUrl )
    {
        _ciManagementUrl = ciManagementUrl;
    }

    public String getCiManagementUrl()
    {
        return _ciManagementUrl;
    }

}
