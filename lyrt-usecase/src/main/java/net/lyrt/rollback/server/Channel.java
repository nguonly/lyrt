package net.lyrt.rollback.server;

import net.lyrt.Player;

/**
 * Created by nguonly on 6/27/17.
 */
public class Channel extends Player {
    public String prepareChannelForSending(String data){
        return data;
    }

    public String prepareChannelForReceiving(String data){ return data;}

    public int factor(){
        return 10;
    }
}
