package net.lyrt.test;

/**
 * Created by nguonly on 6/25/17.
 */
public class TestMain {
    public static void main(String... args){
        TestObject o1 = new TestObject();
        TestObject o2 = new TestObject();

        o1.setName("lycog");
        o2.setName("ely");

        o1.display();
        o2.display();
    }
}
