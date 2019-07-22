package com.krishna.schoolbustracker.models;

import java.io.Serializable;

public class StudentModels implements Serializable {
    String name,id;
    boolean flag=false;
    public  StudentModels(){
    }
    public StudentModels(String name,String id,boolean flag){
        this.name=name;
        this.id=id;
        this.flag=false;
    }
    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public  void setCheck(boolean flag){this.flag=flag;}

    public boolean getCheck(){return flag;}
}
