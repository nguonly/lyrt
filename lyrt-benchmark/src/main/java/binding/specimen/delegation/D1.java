package binding.specimen.delegation;

import binding.specimen.IBehavior;

/**
 * Created by nguonly on 7/4/17.
 */
public class D1 implements IBehavior{
    private IBehavior lower;
    public String method(String args){
        if(lower != null){
            return "<d1>" + lower.method(args) + "<d1>";
        }else return "";
    }

    public void setLower(IBehavior behavior){
        lower = behavior;
    }
}
