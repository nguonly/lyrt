package rop;

import java.util.List;

/**
 * Created by nguonly on 6/29/17.
 */
public interface Component {
    void addRole(ComponentRole spec);

    List<ComponentRole> getRole(String spec);

    boolean hasRole(String spec);
}
