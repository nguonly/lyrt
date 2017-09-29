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
public class UnanticipatedBindingBenchmark {

    public static void main(String... args){
        MyState s = new MyState();

        s.setup();
        s.compartments[0].activate();
        System.out.println(s.adaptationXML[0]);
        System.out.println(s.adaptationXML[1]);
        UnanticipatedXMLParser.parse(s.adaptationXML[0]);
    }

    @Param({"0", "1", "2", "3", "4"})
    private int ARGS;

    private static int[] params = new int[]{10, 100, 1000, 5000, 10000};

    @State(Scope.Thread)
    public static class MyState{
        public Compartment[] compartments;
        public CoreObject[][] coreObjects;
        public String[] adaptationXML;

        int NUM = params.length;//10, 100, 1000, 10000

        Registry reg = Registry.getRegistry();
        @Setup(Level.Invocation)
        public void setup(){
            compartments = new Compartment[NUM];
            coreObjects = new CoreObject[NUM][];
            adaptationXML = new String[NUM];

            for(int i=0; i<NUM; i++){
                compartments[i] = reg.newCompartment(Compartment.class);
//                int amount = (int)Math.pow(10, i+1);
                int amount = params[i];
                coreObjects[i] = new CoreObject[amount];

                for(int j=0; j<amount; j++){
                    coreObjects[i][j] = reg.newCore(CoreObject.class);
                }

                //initial binding
//                BindingBenchmark.makeBinding(compartments[i], coreObjects[i], amount);

                adaptationXML[i] = getXMLBindingBaseOperation(compartments[i].hashCode(), coreObjects[i], true, true, R1.class.getTypeName());
            }
        }

        @TearDown(Level.Invocation)
        public void tearDown(){
            for(int i=0; i<NUM; i++) {
                compartments[i].deactivate(false);
            }
        }

    }

    public static String getXMLBindingBaseOperation(int compartmentId, CoreObject[] cores, boolean isCore, boolean isForBinding, String roleType){
        XMLConstructor xml = new XMLConstructor();

        String strOperation = isForBinding?"bind":"rebind";
        String strActor = isCore?"coreId":"roleId";

        xml.openClosure();
        xml.append("<compartment id=\"" + compartmentId + "\" >");
        for(int i=0; i<cores.length; i++) {
            xml.append("<" + strOperation + " " + strActor + "=\"" + cores[i].hashCode() + "\" roleType=\"" + roleType + "\" />");
        }
        xml.append("</compartment>");
        xml.closeClosure();

        return xml.toString();
    }

    @Benchmark
    public void unanticipatedBinding(MyState s){
        s.compartments[ARGS].activate();
        UnanticipatedXMLParser.parse(s.adaptationXML[ARGS]);
    }

//    @Benchmark
//    public void unanticipatedBinding10(MyState s){
//        s.compartments[0].activate();
//        UnanticipatedXMLParser.parse(s.adaptationXML[0]);
//    }
//
//    @Benchmark
//    public void unanticipatedBinding100(MyState s){
//        s.compartments[1].activate();
//        UnanticipatedXMLParser.parse(s.adaptationXML[1]);
//    }
//
//    @Benchmark
//    public void unanticipatedBinding1000(MyState s){
//        s.compartments[2].activate();
//        UnanticipatedXMLParser.parse(s.adaptationXML[2]);
//    }
//
//    @Benchmark
//    public void unanticipatedBinding10000(MyState s){
//        s.compartments[3].activate();
//        UnanticipatedXMLParser.parse(s.adaptationXML[3]);
//    }
}
