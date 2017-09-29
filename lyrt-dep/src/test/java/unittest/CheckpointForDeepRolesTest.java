package unittest;

import net.lyrt.Compartment;
import net.lyrt.IPlayer;
import net.lyrt.IRole;
import net.lyrt.block.AdaptationBlock;
import net.lyrt.block.InitBindingBlock;
import net.lyrt.helper.DumpHelper;
import net.lyrt.rollback.ControlUnit;
import org.junit.Assert;
import org.junit.Test;

public class CheckpointForDeepRolesTest extends BaseTest {

    @Test
    public void deepRoleTest(){
        Channel ch = _reg.newCore(Channel.class);
        Compartment comp = _reg.newCompartment(Compartment.class);

        try(InitBindingBlock ib = comp.initBinding()){
            ch.bind(Logger.class).bind(Lz.class);
        }

        comp.activate();
        DumpHelper.dumpLowering(comp);
        String msg = ch.invoke("send", String.class, "DATA");
        Assert.assertEquals("<Z><L>DATA<L><Z>", msg);

        try(AdaptationBlock ab = new AdaptationBlock()){
//            ch.unbind(Logger.class);
            ch.bind(Lz.class);
        }

//        DumpHelper.dumpRelations(comp);

        msg = ch.invoke("send", String.class, "DATA");
        Assert.assertEquals("<Z>DATA<Z>", msg);

        ControlUnit cu = new ControlUnit();
        cu.rollback(comp);

//        DumpHelper.dumpRelations(comp);
//        DumpHelper.dumpLowering(comp);
        msg = ch.invoke("send", String.class, "DATA");
        Assert.assertEquals("<Z><L>DATA<L><Z>", msg);
    }

    public static class Channel implements IPlayer{
        public String send(String msg){
            return msg;
        }
    }

    public static class Logger implements IRole {
        public String send(String msg){
            String fMsg = invokePlayer("send", String.class, msg);
            return "<L>" + fMsg + "<L>";
        }
    }

    public static class Lz implements IRole{
        public String send(String msg){
            String fMsg = invokePlayer("send", String.class, msg);
            return "<Z>" + fMsg + "<Z>";
        }
    }
}
