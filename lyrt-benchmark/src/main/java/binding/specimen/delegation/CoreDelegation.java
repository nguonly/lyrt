package binding.specimen.delegation;

import binding.specimen.IBehavior;

/**
 * Created by nguonly on 7/4/17.
 */
public class CoreDelegation implements IBehavior{
    private IBehavior lifting;

    public String liftMethod(String args){
        if(lifting!=null){
            return lifting.method(args);
        }else return args;
    }

    public String method(String args){
        return args;
    }

    public void setLifting(IBehavior lifting){
        this.lifting = lifting;
    }
}
