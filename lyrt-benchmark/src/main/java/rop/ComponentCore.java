package rop;

import java.util.HashMap;
import java.util.List;

/**
 * Created by nguonly on 6/29/17.
 */
public class ComponentCore implements Component {
    private HashMap<Integer, ComponentRole> roles = new HashMap<>();

    @Override
    public void addRole(ComponentRole spec) {
        if(!roles.containsKey(spec.hashCode())){
            roles.put(spec.hashCode(), spec);
        }
    }

    @Override
    public List<ComponentRole> getRole(String spec) {
        return null;
    }

    @Override
    public boolean hasRole(String spec) {
        return false;
    }

    public HashMap<Integer, ComponentRole> getRoles(){return roles;}
}
