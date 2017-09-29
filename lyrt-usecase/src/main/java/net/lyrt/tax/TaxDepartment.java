package net.lyrt.tax;

import net.lyrt.Compartment;

public class TaxDepartment extends Compartment{
    private double revenue;
    private String name;

    public TaxDepartment(String name){
        this.name = name;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double amount) {
        this.revenue += amount;
    }
}
