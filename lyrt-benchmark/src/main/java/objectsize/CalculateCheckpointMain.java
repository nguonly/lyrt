package objectsize;

import binding.specimen.CoreObject;
import binding.specimen.R1;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.Relation;
import net.lyrt.block.InitBindingBlock;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayDeque;

public class CalculateCheckpointMain {
    public static void main(String... args){
        dumpSeralization(true,10);
        dumpSeralization(true,100);
        dumpSeralization(true,1000);
        dumpSeralization(true,5000);
        dumpSeralization(true,10000);

        dumpSeralization(false,10);
        dumpSeralization(false,100);
        dumpSeralization(false,1000);
        dumpSeralization(false,5000);
        dumpSeralization(false,10000);
    }

    private static ArrayDeque<Relation> makeRelation(int numRole) {
        Registry reg = Registry.getRegistry();

        Compartment comp = reg.newCompartment(Compartment.class);
        CoreObject core = reg.newCore(CoreObject.class);

        try (InitBindingBlock ib = comp.initBinding()) {
            for (int i = 0; i < numRole; i++) {
                core.bind(R1.class);
            }
        }

        return comp.getRelations();
    }

    private static void dumpSeralization(boolean isRelation, int numRole){
        Kryo kryo = new Kryo();
        Output output = null;
        try {
            String prefix = isRelation?"relation":"role";
            String fileName = String.format("%s%d.dump", prefix, numRole);
            output = new Output(new FileOutputStream(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(isRelation) {
            ArrayDeque<Relation> relations = makeRelation(numRole);
            kryo.writeObject(output, relations);
        }else{
            Object[] roles = makeRole(numRole);
            kryo.writeObject(output, roles);
        }

        output.close();
    }

    private static Object[] makeRole(int numRole){
        Object[] roles = new Object[numRole];
        for(int i=0; i<numRole; i++){
            roles[i] = new R1();
        }

        return roles;
    }
}
