package net.lyrt.tax;

import net.lyrt.IRole;

public class Accountant implements IRole{
    private double salary = 1600;

//    public double getSalary(){
//        return salary;
//    }

    public void getPaid(){
        invokeCompartment("paySalary", salary);
        Person core = (Person)getCore();
        core.setSaving(salary);
    }

    /**
     * Pay salary to all employee working in this company
     */
    public void paySalary() {
        invokeRel(Developer.class, "getPaid");
        invokeRel(Accountant.class, "getPaid");
    }
}
