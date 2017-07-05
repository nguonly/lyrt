package binding;

import binding.specimen.CoreObject;
import binding.specimen.R1;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.unanticipation.UnanticipatedXMLParser;
import net.lyrt.unanticipation.XMLConstructor;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 7/1/17.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class UnanticipatedTransferBenchmark {
    @State(Scope.Thread)
    public static class MyState{
        public Compartment[] compartments;
        public CoreObject[][] fromCoreObjects;
        public CoreObject[][] toCoreObjects;
        public String[] adaptationXML;

        int NUM = 4;//10, 100, 1000, 10000

        Registry reg = Registry.getRegistry();
        @Setup(Level.Invocation)
        public void setup(){

            compartments = new Compartment[NUM];
            fromCoreObjects = new CoreObject[NUM][];
            toCoreObjects = new CoreObject[NUM][];
            adaptationXML = new String[NUM];

            for(int i=0; i<NUM; i++){
                compartments[i] = reg.newCompartment(Compartment.class);
                int amount = (int)Math.pow(10, i+1);
                fromCoreObjects[i] = new CoreObject[amount];

                BindingBenchmark.makeBinding(compartments[i], fromCoreObjects[i], amount);

                toCoreObjects[i] = new CoreObject[amount];
                for (int j = 0; j < amount; j++) {
                    toCoreObjects[i][j] = reg.newCore(CoreObject.class);
                }

                adaptationXML[i] = getXMLTransferOperation(compartments[i].hashCode(), fromCoreObjects[i], toCoreObjects[i], R1.class.getTypeName());
            }
        }

        public static String getXMLTransferOperation(int compartmentId, CoreObject[] fromCores, CoreObject[] toCores, String roleType){
            XMLConstructor xml = new XMLConstructor();

            xml.openClosure();
            xml.append("<compartment id=\"" + compartmentId + "\" >");
            for(int i=0; i<fromCores.length; i++) {
                xml.append("<transfer fromCoreId=\"" + fromCores[i].hashCode() + "\" toCoreId=\"" + toCores[i].hashCode() + "\" roleType=\"" + roleType + "\" />");
            }
            xml.append("</compartment>");
            xml.closeClosure();

            return xml.toString();
        }

        @TearDown(Level.Invocation)
        public void tearDown(){
            for(int i=0; i<NUM; i++) {
                compartments[i].deactivate(false);
            }
        }

    }

    @Benchmark
    public void unanticipatedTransfer10(MyState s){
        s.compartments[0].activate();
        UnanticipatedXMLParser.parse(s.adaptationXML[0]);
    }

    @Benchmark
    public void unanticipatedTransfer100(MyState s){
        s.compartments[1].activate();
        UnanticipatedXMLParser.parse(s.adaptationXML[1]);
    }

    @Benchmark
    public void unanticipatedTransfer1000(MyState s){
        s.compartments[2].activate();
        UnanticipatedXMLParser.parse(s.adaptationXML[2]);
    }

    @Benchmark
    public void unanticipatedTransfer10000(MyState s){
        s.compartments[3].activate();
        UnanticipatedXMLParser.parse(s.adaptationXML[3]);
    }
}
