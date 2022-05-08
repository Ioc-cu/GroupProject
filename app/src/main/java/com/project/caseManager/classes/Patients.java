package com.project.caseManager.classes;


//Entity Class of Patients, exact same fields as SQLite(Local) and MySQL(Server) "patients" table fields

public class Patients {
    public int id;
    public String fullname;
    public String address;
    public String date;

    public Patients(int id, String fullname, String address, String date)
    {
        this.id = id;
        this.fullname =fullname;
        this.address=address;
        this.date=date;
    }

    public Patients(String fullname, String address, String date)
    {
        this.id = 0;
        this.fullname =fullname;
        this.address=address;
        this.date=date;
    }


    public Patients()
    {
        this.id = 1;
        this.fullname ="";
        this.address="";
        this.date="";
    }

}
