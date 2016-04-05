package org.khmeracademy.Model;

/**
 * Created by Longdy on 2/10/2016.
 */
public class UniversityItem {
    private String uId;
    private String uName;

    public UniversityItem(String uId, String uName){
        this.uId = uId;
        this.uName = uName;
    }

    public String getuId() {
        return uId;
    }


    public String getuName() {
        return uName;
    }

}
