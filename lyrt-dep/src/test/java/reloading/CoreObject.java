package reloading;

public class CoreObject {
    public String method(String args){
        System.out.println("The modified version of " + this.getClass().getName());
        return args;
    }
}
