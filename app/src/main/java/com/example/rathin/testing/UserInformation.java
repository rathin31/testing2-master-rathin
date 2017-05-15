package com.example.rathin.testing;


public class UserInformation
{
    public String name;
    public String email;
    public String password;
    public String number;
    public String joiningdate;
    public String birthdate;


    public UserInformation(String name,String email,String password, String number,String joiningdate, String birthdate) {
        this.name = name;
        this.number = number;
        this.email=email;
        this.joiningdate=joiningdate;
        this.password=password;
        this.birthdate= birthdate;
    }
}
