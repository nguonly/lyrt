package binding.specimen;

import net.lyrt.Player;

/**
 * Created by nguonly on 6/26/17.
 */
public class CoreObject  extends Player implements IBehavior{
    public String method(String args){
        return args;
    }
}
