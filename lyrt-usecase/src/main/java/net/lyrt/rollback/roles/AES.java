package net.lyrt.rollback.roles;

import net.lyrt.Role;

/**
 * Created by nguonly on 6/27/17.
 */
public class AES extends Role {
    String wrapper = "<AES>";

    public String prepareChannelForReceiving(String data){
        int idx = data.indexOf(wrapper);
        int lastIdx = data.lastIndexOf(wrapper);

        return data.substring(idx+ wrapper.length(), lastIdx);
//        return data;
    }

    public String prepareChannelForSending(String data){
        String fMsg = invokePlayer("prepareChannelForSending", String.class, data);
//        int i =1/invokePlayer("factor", int.class); // the probability of an error
        return wrapper + fMsg + wrapper;
    }

    public int factor(){
        return 5;
    }
}
