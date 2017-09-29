package binding.specimen;

import net.lyrt.IPlayer;
import net.lyrt.Player;

/**
 * Created by nguonly on 6/26/17.
 */
public class CoreObject  implements IPlayer, IBehavior{
    public String method(String args){
        return args;
    }
}
