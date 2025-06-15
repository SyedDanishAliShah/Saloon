package com.example.saloon.Models;

public class SaloonModel {

    // Real-time database admin and users table data
    String id, name, email, password, address, latitude, longitude, male_services, female_services, gender;

    public SaloonModel() {
    }

    // Constructor for admin table

//    public SaloonModel( String id, String name , String email , String password , String address , String latitude , String longitude , String male_services , String female_services , String gender ) {
//        this.id = id;
//        this.name = name;
//        this.email = email;
//        this.password = password;
//        this.address = address;
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.male_services = male_services;
//        this.female_services = female_services;
//    }

    // Constructor for users table

//    public SaloonModel( String name , String email , String password , String gender ) {
//        this.name = name;
//        this.email = email;
//        this.password = password;
//        this.gender = gender;
//    }

    // Getter and Setter for admin and users table

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude( String latitude ) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude( String longitude ) {
        this.longitude = longitude;
    }

    public String getMale_services() {
        return male_services;
    }

    public void setMale_services( String male_services ) {
        this.male_services = male_services;
    }

    public String getFemale_services() {
        return female_services;
    }

    public void setFemale_services( String female_services ) {
        this.female_services = female_services;
    }

    public String getGender() {
        return gender;
    }

    public void setGender( String gender ) {
        this.gender = gender;
    }
}
