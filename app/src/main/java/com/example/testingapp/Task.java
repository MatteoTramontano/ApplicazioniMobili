package com.example.testingapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {
    String name;
    String category;
    String   date;
    String ora;
    String state;

    public Task(String name, String category, String date, String ora, String state){
            this.name = name;
            this.category=category;
            this.date=date;
            this.ora =ora;
            this.state=state;

    }
    public  String getName(){
        return name;
    }
    public  String getCategory(){
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getState() {
        return state;
    }

   /* public String getState () {return state;}
    public void setState(String s){
        this.state = s;
    }
*/


}
