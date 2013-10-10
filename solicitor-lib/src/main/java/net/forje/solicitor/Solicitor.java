package net.forje.solicitor;

import java.net.URL;


// todo think the set / add methods should be on the factory, not the interface.

/**
 * This is the public interface to the system.  Most everything else will be package level access.
 */
public interface Solicitor {

    /**
     * Initiates the process of looking up an artifact in the list of provided repositories and pulls any updates to
     * the local repo.
     *
     * @param groupId the group id for the project to update
     * @param artifactId the artifact id for the project to update
     */
    public void update (String groupId,
                        String artifactId) throws RepositoryException;

}
