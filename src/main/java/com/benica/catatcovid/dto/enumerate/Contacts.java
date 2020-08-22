package com.benica.catatcovid.dto.enumerate;

public enum Contacts {
    TODAY("Hari Ini"),
    YESTERDAY("Kemarin"),
    WEEKLY("Minggu Ini"),
    TWOWEEKS("Dua Minggu Terakhir");

    private String name;

    Contacts(String name){
        this.name = name;
    }

    public String getName() { return name; }
}