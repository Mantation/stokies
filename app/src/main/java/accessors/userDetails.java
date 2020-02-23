package accessors;

import io.realm.RealmObject;

public class userDetails extends RealmObject {
    private String userid;
    private String userName;
    private String userSurname;
    private String userStokvel;
    private String userLocation;
    private String userPhone;
    private String userAltPhone;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getUserStokvel() {
        return userStokvel;
    }

    public void setUserStokvel(String userStokvel) {
        this.userStokvel = userStokvel;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAltPhone() {
        return userAltPhone;
    }

    public void setUserAltPhone(String userAltPhone) {
        this.userAltPhone = userAltPhone;
    }
}
