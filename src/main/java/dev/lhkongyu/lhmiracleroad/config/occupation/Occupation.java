package dev.lhkongyu.lhmiracleroad.config.occupation;

import java.util.List;

public class Occupation {
    String id;
    String name;
    String description;
    List<OccupationAttribute> occupationAttributes;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<OccupationAttribute> getOccupationAttributes() {
        return occupationAttributes;
    }
}
