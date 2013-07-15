package co.zoyi.carryu.Application.Datas.ValueObjects;

public class UserLoginData extends CUValueObject {
    private String userID, userPassword;

    public String getUserID() {
        return userID;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public UserLoginData(String userID, String userPassword) {
        this.userID = userID;
        this.userPassword = userPassword;
    }
}
