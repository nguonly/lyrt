package unittest;

import net.lyrt.Compartment;
import net.lyrt.Player;
import net.lyrt.Role;
import net.lyrt.block.InitBindingBlock;
import net.lyrt.helper.DumpHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nguonly on 6/19/17.
 */
public class CompartmentInvocationTest extends BaseTest {

    @Test
    public void accessCompartmentMethodTest(){
        Company abc = _reg.newCompartment(Company.class);
        Person ely = _reg.newCore(Person.class, "ely");

        abc.invoke("setRevenue", 10000);

        try(InitBindingBlock ib = abc.initBinding()){
            ely.bind(Developer.class);
        }

        abc.activate();
        ely.invoke("getPaid", void.class);
        int elySaving = ely.invoke("getSaving", int.class);
        Assert.assertEquals(1500, elySaving);

        int abcRevenue = abc.invoke("getRevenue", int.class);
        Assert.assertEquals(10000 - 1500, abcRevenue);
        abc.deactivate();
    }

    @Test
    public void invokeRelationshipTest(){
        Company abc = _reg.newCompartment(Company.class);
        Person ely = _reg.newCore(Person.class, "ely");
        Person lycog = _reg.newCore(Person.class, "lycog");
        Person nut = _reg.newCore(Person.class, "nut");

        abc.invoke("setRevenue", 10000);

        try(InitBindingBlock ib = abc.initBinding()){
            lycog.bind(Developer.class);
            ely.bind(Developer.class);
            nut.bind(Accountant.class);
        }

        abc.activate();
        DumpHelper.dumpRelations(abc);
        lycog.invoke("setSalary", 2000);

        System.out.println("ely salary : " + ely.invoke("getSalary", int.class));
        System.out.println("lycog salary : " + lycog.invoke("getSalary", int.class));

        nut.invoke("paySalary");

        int elySaving = ely.invoke("getSaving", int.class);
        int lycogSaving = lycog.invoke("getSaving", int.class);
        int nutSaving = nut.invoke("getSaving", int.class);

        Assert.assertEquals(1500, elySaving);
        Assert.assertEquals(2000, lycogSaving);
        Assert.assertEquals(1600, nutSaving);

        int abcRevenue = abc.invoke("getRevenue", int.class);

        Assert.assertEquals(10000 - 1500-2000-1600, abcRevenue);

        abc.deactivate();
    }

    public static class Company extends Compartment{
        private int revenue = 0;

        public void setRevenue(int amount){
            revenue = amount;
        }

        public int getRevenue(){
            return revenue;
        }

        public void deductForSalary(int amount){
            revenue -= amount;
        }
    }

    public static class Person extends Player{
        private int saving = 0;
        private String name;

        public Person(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }

        public void setSaving(int amount){
            saving += amount;
        }

        public int getSaving(){
            return saving;
        }
    }

    public static class Developer extends Role {
        private int salary = 1500;

        public void setSalary(int amount){
            this.salary = amount;
        }

        public int getSalary(){
            return salary;
        }

        public void getPaid(){

            invokeCompartment("deductForSalary", salary);
            invokeCore("setSaving", salary);
        }
    }

    public static class Accountant extends Role{
        private int salary = 1600;

        public void getPaid(){
            invokeCompartment("deductForSalary", salary);
            invokeCore("setSaving", salary);
        }

        public void paySalary(){
            invokeRel(Developer.class, "getPaid");
            invokeRel(Accountant.class, "getPaid");
        }
    }
}
