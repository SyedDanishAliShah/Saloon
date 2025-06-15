package com.example.saloon.Models;

public class Order {
    private String name, category, time, date, user_id;
    boolean accept;

    public Order() {
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory( String category ) {
        this.category = category;
    }

    public String getTime() {
        return time;
    }

    public void setTime( String time ) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate( String date ) {
        this.date = date;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id( String user_id ) {
        this.user_id = user_id;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept( boolean accept ) {
        this.accept = accept;
    }
}
