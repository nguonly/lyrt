package binding.specimen.delegation;

import binding.specimen.IBehavior;

/**
 * Created by nguonly on 7/4/17.
 */
public class D5 implements IBehavior {
    private IBehavior lower;
    public String method(String args){
        if(lower!=null){
            return "<d5>" + lower.method(args) + "<d5>";
        }else {
            return "";
        }
    }

    public void setLower(IBehavior behavior){
        lower = behavior;
    }
}
