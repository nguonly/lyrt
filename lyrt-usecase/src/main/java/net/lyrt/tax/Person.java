package net.lyrt.tax;

import net.lyrt.IPlayer;

public class Person implements IPlayer{
    private String name;
    private double saving;

    public Person(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSaving() {
        return saving;
    }

    public void setSaving(double saving) {
        this.saving += saving;
    }
}
