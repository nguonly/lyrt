package net.lyrt.filetransfer.server;

import net.lyrt.IPlayer;

public class Channel implements IPlayer {

    public String format(String data){
        return data;
    }

    public String unformat(String data){
        return data;
    }

    public int factor(){ return 10;}
}
