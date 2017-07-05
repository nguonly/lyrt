package binding.specimen.lowering;

import net.lyrt.Role;

/**
 * Created by nguonly on 7/3/17.
 */
public class LoweringRole02 extends Role {
    public String method(String args){
        return invokePlayer("method", String.class, args);
    }
}
