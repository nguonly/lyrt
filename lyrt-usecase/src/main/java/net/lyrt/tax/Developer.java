package net.lyrt.tax;

import net.lyrt.IPlayer;
import net.lyrt.IRole;

public class Developer implements IRole{
    private double salary=1500;

    public Developer(Double salary){ this.salary = salary;}
    public double getSalary(){
        return salary;
    }

    public void getPaid(){
        invokeCompartment("paySalary", salary);
        Person core = (Person)getCore();
        core.setSaving(salary);
    }

    public void work(){
        IPlayer person = (IPlayer)getCore();
        String name = person.invoke("getName", String.class);
        invokeCompartment("setRevenue", salary);

        System.out.println(name + " generates a revenue of " + salary + " Euro");
    }
}
