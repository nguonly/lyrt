package net.lyrt.rollback.roles;

import net.lyrt.Role;

/**
 * Created by nguonly on 6/27/17.
 */
public class LZ extends Role {
    String wrapper = "<LZ>";

    public String prepareChannelForReceiving(String data){
        int idx = data.indexOf(wrapper);
        int lastIdx = data.lastIndexOf(wrapper);

        return data.substring(idx+ wrapper.length(), lastIdx);
//        return data;
    }

    public String prepareChannelForSending(String data){
        String fMsg = invokePlayer("prepareChannelForSending", String.class, data);
        int factor = invokePlayer("factor", int.class);
        double i = 1/Math.pow(2, factor); //there is no chance of divide-by-zero error
        return wrapper + fMsg + wrapper;
    }

    public int factor(){
        return 0;
    }
}
