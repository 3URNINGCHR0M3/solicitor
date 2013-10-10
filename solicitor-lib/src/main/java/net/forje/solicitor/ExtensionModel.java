package net.forje.solicitor;

import java.util.List;
import java.util.Map;

interface ExtensionModel {

    /**
     * The identifier for the model within the project.
     *
     * @return the identifier
     */
    public String getId();

    /**
     * Retrieve all values of the model as a map of strings for storing
     *
     * @return the map of values
     */
    public Map<String, String> getValuesAsMap();

    /**
     * Store to the model the values from a map of strings which was retrieved
     *
     * @param values the values to store
     */
    public void setValuesFromMap( Map<String, String> values );

    /**
     * A list of all possible keys that can be used in the map for {@link #setValuesFromMap(Map)} or
     * {@link #getValuesAsMap()}.
     *
     * @return the list of keys
     */
    public List<String> getKeys();

}
