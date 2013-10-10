package net.forje.solicitor;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Means to acquire access to the Solicitor.
 */
public class SolicitorFactory {

    /**
     * Create a new instance of the Solicitor using the specified URLs for local and remote repositories
     *
     * @param localURL the file path to use as the local repository
     * @param remoteURL the URL to use for remote URL
     */
    public static Solicitor getInstance(final URL localURL,
                                         final URL remoteURL)
            throws SolicitorException {

        SolicitorImpl instance = new SolicitorImpl(localURL, remoteURL);

        instance.bootstrap();

        return instance;

    }

    public static Solicitor getInstance() throws SolicitorException {

        final URL remoteURL;
        final URL localURL;

        try {

            remoteURL = new URL("http://repo1.maven.org/maven2");

            String userHome = System.getProperty("user.home");
            File localRepo = new File(userHome, ".m2/repository");
            localURL = localRepo.toURI().toURL();
            System.out.println("localRepo = " + localRepo);

        } catch (Exception e) {
            throw new SolicitorException(e);
        }

        return getInstance(localURL, remoteURL);

    }

    // todo support multiple remote URLS, add second signature with remote param as an array or list
    // todo assuming ${user home}/.m2/repository, this may be naive.  can it be configured in settings.xml?

}
