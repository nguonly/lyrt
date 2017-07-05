package net.lyrt.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguonly on 6/25/17.
 */
public interface ITest {
    List<String> _names = new ArrayList<>();

    default List<String> getNames(){return _names;}

    default void setName(String name){
        _names.add(name);
    }

    default void display(){
        _names.forEach(k -> System.out.println(k));
    }
}
