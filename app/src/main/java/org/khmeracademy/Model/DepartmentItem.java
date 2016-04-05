package org.khmeracademy.Model;

/**
 * Created by PC1 on 2/15/2016.
 */
public class DepartmentItem {
    private String id, name;

    public DepartmentItem (String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
