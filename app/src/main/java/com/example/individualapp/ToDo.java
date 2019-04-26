package com.example.individualapp;

public class ToDo {
    private long id = -1;
    private String name = "";
    private String createdAt = "";
    private int isCompleted = -1;

    public final long getId() {
        return this.id;
    }
    public final void setId(long var) {
        this.id = var;
    }
    public final String getName() {
        return this.name;
    }
    public final void setName( String name) {
        this.name = name;
    }
    public final int getCompleted() { return this.isCompleted; }
    public final void setCompleted(int var) { this.isCompleted = var; }
}