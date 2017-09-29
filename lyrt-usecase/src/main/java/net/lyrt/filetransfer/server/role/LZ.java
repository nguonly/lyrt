package net.lyrt.filetransfer.server.role;

import net.lyrt.IRole;

public class LZ implements IRole{
    public String format(String data){
        String msg = invokePlayer("format", String.class, data);
        return "<LZ>" + msg + "<LZ>"; //perform compression algorithm
    }

    public int factor(){ return 0; }
}
