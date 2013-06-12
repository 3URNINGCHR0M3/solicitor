package net.forje.solicitor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MavenCoordinates  implements ExtensionModel {

    /** The Maven group ID of the project. */
    private String _groupId;

    /** The Maven artifact ID of the project. */
    private String _artifactId;

    public String getId()
    {
        return "maven";
    }

    public void setGroupId( String groupId )
    {
        this._groupId = groupId;
    }

    public String getGroupId()
    {
        return _groupId;
    }

    public void setArtifactId( String artifactId )
    {
        this._artifactId = artifactId;
    }

    public String getArtifactId()
    {
        return _artifactId;
    }

    public static String constructProjectId( String groupId, String artifactId )
    {
        return groupId + ":" + artifactId;
    }

    public Map<String, String> getValuesAsMap()
    {
        Map<String, String> values = new HashMap<String, String>();
        values.put( "groupId", _groupId);
        values.put( "artifactId", _artifactId);
        return values;
    }

    public void setValuesFromMap( Map<String, String> values )
    {
        _groupId = values.get( "groupId" );
        _artifactId = values.get( "artifactId" );
    }

    public List<String> getKeys()
    {
        return Arrays.asList("groupId", "artifactId");
    }

}
