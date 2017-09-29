package net.lyrt.tax;

import net.lyrt.Compartment;

public class Company extends Compartment{
    private double revenue = 0;
    private String name;
    private double taxRate = 0.2; //20%

    public Company(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setRevenue(double amount){
        revenue += amount;
    }

    public double getRevenue(){
        return revenue;
    }

    public double taxToBePaid(){
        double tax = revenue*taxRate;
        this.revenue -= tax;

        return tax;
    }

    /**
     * Pay salary to individual contract (position)
     * @param amount
     */
    public void paySalary(double amount){
        revenue -= amount;
    }
}
