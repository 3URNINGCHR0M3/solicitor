package net.forje.solicitor;

import java.net.URL;


// todo think the set / add methods should be on the factory, not the interface.

/**
 * This is the public interface to the system.  Everything else will be package level access.
 */
public interface Solicitor {

    /**
     * Adds a URL to the list of remote repositories to query for updates
     */
    public void addRemoteRepo(URL repoURL);

    /**
     * defines the path on the local file system where files are to be written
     */
    public void setLocalRepoPath(URL localRepo);

    /**
     * Initiates the process of looking up an artifact in the list of provided repositories and pulls any updates to
     * the local repo
     */
    public void update (String groupId, String artifactId);

}
