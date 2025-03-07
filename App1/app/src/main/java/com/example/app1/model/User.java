package com.example.app1.model;

public class User {
    private String id;
    private String userName;
    private String email;
    private String password;
    private String gender;
    private String birthDate;
    private String imagePath;

    public User() {
    }

    public User(String id, String userName, String email, String password, String gender, String birthDate, String imagePath) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.birthDate = birthDate;
        this.imagePath = imagePath;
    }

    // Getter ve Setter metodları

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getImagePath(){
        return imagePath;
    }
    public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }
}