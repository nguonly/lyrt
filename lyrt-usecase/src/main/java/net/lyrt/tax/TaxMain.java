package net.lyrt.tax;

import net.lyrt.Registry;
import net.lyrt.block.InitBindingBlock;

public class TaxMain {
    public static void main(String... args){
        Registry reg = Registry.getRegistry();

        Company abc = reg.newCompartment(Company.class, "ABC");

        Person alice = reg.newCore(Person.class, "Alice");
        Person bob = reg.newCore(Person.class, "Bob");
        Person ely = reg.newCore(Person.class, "Ely");

        try(InitBindingBlock ib = abc.initBinding()){
            alice.bind(Accountant.class);

            bob.bind(Developer.class, 1500.00);
            ely.bind(Developer.class, 2000.00);
        }

        abc.activate();

        bob.invoke("work");
        bob.invoke("work");
        ely.invoke("work");
        ely.invoke("work");

        System.out.println("====== Before Salary Payment ======");
        System.out.println("Company revue is " + abc.getRevenue() + " Euro");
        System.out.println("Bob saving is " + bob.getSaving() + " Euro");
        System.out.println("Ely saving is " + ely.getSaving() + " Euro");
        System.out.println("Alice saving is " + alice.getSaving() + " Euro");

        alice.invoke("paySalary"); //Pay salary

        System.out.println("====== After Salary Payment ======");
        System.out.println("Company revenue is " + abc.getRevenue() + " Euro");
        System.out.println("Bob saving is " + bob.getSaving() + " Euro");
        System.out.println("Ely saving is " + ely.getSaving() + " Euro");
        System.out.println("Alice saving is " + alice.getSaving() + " Euro");

        abc.deactivate();

        TaxDepartment tax = reg.newCompartment(TaxDepartment.class, "Tax Dept.");
        Person ana = reg.newCore(Person.class, "Ana");

        try(InitBindingBlock ib = tax.initBinding()){
            ana.bind(TaxCollector.class);
            ely.bind(Freelancer.class).bind(TaxPayer.class);
            abc.bind(TaxPayer.class);
        }

        tax.activate();

        ely.invoke("earn", 2000.0); //ely earns
        ana.invoke("collectTax");
//        ely.invoke("pay");

//        DumpHelper.dumpRelations(tax);
//        DumpHelper.dumpLifting(tax);

//        abc.invoke("pay");

        System.out.println("====== After Tax Payment ======");
        System.out.println("Collected tax is " + tax.getRevenue() + " Euro");
        System.out.println("Company revenue is " + abc.getRevenue() + " Euro");
        System.out.println("Ely gets " + ely.getSaving() + " Euro");
    }
}
