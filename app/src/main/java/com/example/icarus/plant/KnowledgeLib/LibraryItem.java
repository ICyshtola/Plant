package com.example.icarus.plant.KnowledgeLib;

public class LibraryItem {
    private String id;
    private String name;
    private String dec;

    public LibraryItem(String id, String name, String dec) {
        this.id = id;
        this.name = name;
        this.dec = dec;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }
}
