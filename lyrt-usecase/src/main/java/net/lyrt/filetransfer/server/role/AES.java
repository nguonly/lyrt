package net.lyrt.filetransfer.server.role;

import net.lyrt.IRole;

public class AES implements IRole{
    public String format(String data){
        String msg = invokePlayer("format", String.class, data);
        int factor = invokePlayer("factor", int.class);
        int errorInjection = 1/factor; //simulating an error in certain composition
        return "<AES>" + msg + "<AES>"; //perform encryption algorithm
    }
}
