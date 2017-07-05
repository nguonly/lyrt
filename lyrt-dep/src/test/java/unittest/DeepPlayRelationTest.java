package unittest;

import net.lyrt.Compartment;
import net.lyrt.Player;
import net.lyrt.Role;
import net.lyrt.block.InitBindingBlock;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nguonly on 6/14/17.
 */
public class DeepPlayRelationTest extends BaseTest {

    @Test
    public void testDeepBinding(){
        Compartment lzComp = _reg.newCompartment(Compartment.class);
        Compartment aesComp = _reg.newCompartment(Compartment.class);
        Channel channel = _reg.newCore(Channel.class);

        try(InitBindingBlock ib = lzComp.initBinding()){
            channel.bind(Lz.class);
        }

        try(InitBindingBlock ib = aesComp.initBinding()){
            channel.bind(Aes.class);
        }

        Assert.assertEquals("DATA", channel.invoke("send", String.class, "DATA"));

        aesComp.activate();
        Assert.assertEquals("<AES>DATA<AES>", channel.invoke("send", String.class, "DATA"));
        aesComp.deactivate();

        lzComp.activate();
        Assert.assertEquals("<LZ>DATA<LZ>", channel.invoke("send", String.class, "DATA"));
        lzComp.deactivate();
    }

    public static class Channel extends Player{
        public String send(String data){
            return data;
        }
    }

    public static class Lz extends Role {
        public String send(String data){
            return "<LZ>" + data + "<LZ>";
        }
    }

    public static class Aes extends Role{
        public String send(String data){
            return "<AES>" + data + "<AES>";
        }
    }
}
