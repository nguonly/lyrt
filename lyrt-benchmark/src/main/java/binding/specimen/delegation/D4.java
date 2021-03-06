package binding.specimen.delegation;

import binding.specimen.IBehavior;

/**
 * Created by nguonly on 7/4/17.
 */
public class D4 implements IBehavior {
    private IBehavior lower;
    public String method(String args){
        if(lower != null){
            return "<d4>" + lower.method(args) + "<d4>";
        }else return "";
    }

    public void setLower(IBehavior behavior){
        lower = behavior;
    }
}
