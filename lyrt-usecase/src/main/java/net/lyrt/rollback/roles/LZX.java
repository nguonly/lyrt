package net.lyrt.rollback.roles;

import net.lyrt.Role;

/**
 * Created by nguonly on 6/27/17.
 */
public class LZX extends Role{
    String wrapper = "<LZX>";

    public String prepareChannelForReceiving(String data){
        int idx = data.indexOf(wrapper);
        int lastIdx = data.lastIndexOf(wrapper);

        return data.substring(idx+ wrapper.length(), lastIdx);
//        return data;
    }

    public String prepareChannelForSending(String data){
        String fMsg = invokePlayer("prepareChannelForSending", String.class, data);
        return wrapper + fMsg + wrapper;
    }

    public int factor(){return 1;}
}
