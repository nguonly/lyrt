package unittest;

import net.lyrt.Compartment;
import net.lyrt.Player;
import net.lyrt.Role;
import net.lyrt.block.InitBindingBlock;
import net.lyrt.helper.DumpHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nguonly on 6/14/17.
 */
public class LoweringTest extends BaseTest {

    @Test
    public void testLoweringInvocation(){
        Compartment lzComp = _reg.newCompartment(Compartment.class);
        Compartment aesComp = _reg.newCompartment(Compartment.class);
        Channel channel = _reg.newCore(Channel.class);

        try(InitBindingBlock ib = lzComp.initBinding()){
            channel.bind(Lz.class).bind(Aes.class);
        }

        try(InitBindingBlock ib = aesComp.initBinding()){
            channel.bind(Aes.class);
        }


        Assert.assertEquals("DATA", channel.invoke("send", String.class, "DATA"));

        DumpHelper.dumpRelations(lzComp);
//        DumpHelper.dumpRelations(aesComp);

        lzComp.activate();
//        DumpHelper.dumpLifting(lzComp);
//        DumpHelper.dumpLowering(lzComp);
        Assert.assertEquals("<AES><LZ>DATA<LZ><AES>", channel.invoke("send", String.class, "DATA"));
        lzComp.deactivate();

        aesComp.activate();
        Assert.assertEquals("<AES>DATA<AES>", channel.invoke("send", String.class, "DATA"));
        aesComp.deactivate();
    }

    @Test
    public void testLoweringToCore(){
        Compartment lzxCompartment = _reg.newCompartment(Compartment.class);
        Compartment aesCompartment = _reg.newCompartment(Compartment.class);
        Channel channel = _reg.newCore(Channel.class);

        try(InitBindingBlock ib = lzxCompartment.initBinding()){
            channel.bind(Lzx.class);
        }

        try(InitBindingBlock ib = aesCompartment.initBinding()){
            channel.bind(Aes.class).bind(Lzx.class);
        }

        lzxCompartment.activate();
        Assert.assertEquals("<LZX>DATA<LZX>", channel.invoke("send", String.class, "DATA"));
        lzxCompartment.deactivate();

        aesCompartment.activate();
        Assert.assertEquals("<LZX>DATA<LZX>", channel.invoke("send", String.class, "DATA"));
        aesCompartment.deactivate();
    }

    public static class Channel extends Player {
        public String send(String data){
            return data;
        }
    }

    public static class Lz extends Role {
        public String send(String data){
//            System.out.println("lz");
            String f = invokePlayer("send", String.class, data);
            return "<LZ>" + f + "<LZ>";
        }
    }

    public static class Aes extends Role{
        public String send(String data){
//            System.out.println("aes");
            String f = invokePlayer("send", String.class, data);
            return "<AES>" + f + "<AES>";
        }
    }

    public static class Lzx extends Role{
        public String send(String data){
            String f = invokeCore("send", String.class, data);
            return "<LZX>" + f + "<LZX>";
        }
    }
}
