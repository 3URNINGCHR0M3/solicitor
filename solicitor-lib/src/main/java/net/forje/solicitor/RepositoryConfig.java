package net.forje.solicitor;

class RepositoryConfig {

    private final String _repositoryUrl;
    private final String _localRepositoryPath;

    // todo change these to take URL objects
    public RepositoryConfig(final String repositoryUrl,
                            final String localRepositoryPath) {
        _repositoryUrl = repositoryUrl;
        _localRepositoryPath = localRepositoryPath;
    }

    public String getRepositoryUrl() {
        return _repositoryUrl;
    }

    public String getLocalRepositoryPath() {
        return _localRepositoryPath;
    }

}

