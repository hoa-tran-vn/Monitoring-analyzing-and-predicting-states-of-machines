package com.example.myfinalappsproject;

public class UserObject {

    private int ID;
    private String IdentityName;
    private String PassWord;
    private String UserName;
    private String Email;
    private int GroupId;
    private String GroupName;


    public UserObject() {
    }

    public int getGroupId() {
        return GroupId;
    }

    public String getGroupName() {
        return GroupName;
    }

    public String getEmail() {
        return Email;
    }

    public String getUserName() {
        return UserName;
    }

    public int getID() {
        return ID;
    }

    public String getIdentityName() {
        return IdentityName;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setIdentityName(String identityName) {
        IdentityName = identityName;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setGroupId(int groupId) {
        GroupId = groupId;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }
}
