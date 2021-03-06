package net.lyrt;

import com.esotericsoftware.kryo.Kryo;
import es.uniovi.reflection.invokedynamic.ProxyFactory;
import es.uniovi.reflection.invokedynamic.interfaces.Callable;
import es.uniovi.reflection.invokedynamic.util.Bootstrap;
import es.uniovi.reflection.invokedynamic.util.Cache;
import es.uniovi.reflection.invokedynamic.util.MethodSignature;
import net.lyrt.unanticipation.ClassReloader;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Created by nguonly on 5/12/17.
 */
public class Registry {

//    private ConcurrentHashMap<Integer, CallableMethod> coreCallable = new ConcurrentHashMap<>();

    private HashMap<Integer, Object> m_corePools = new HashMap<>();

    private ConcurrentHashMap<Long, Compartment> m_activeCompartments = new ConcurrentHashMap<>(); //CurrentThread, Compartment

    /**
     * Consistency Block List
     * Integer is a CompartmentId
     * SimpleEntry is a key value of ConsistencyId and Entering Timestamp
     */
    ConcurrentHashMap<Integer, SimpleEntry<Integer, LocalDateTime>> m_consistencies = new ConcurrentHashMap<>();

    /**
     * ControlUnit of a compartment adaptation configuration
     * Long is the threadId, Stack contain the list of a set of adaptation configuration
     */
    private HashMap<Integer, Stack<ArrayDeque<Relation>>> m_hashConfiguration = new HashMap<>();

    //Flag to set for the runtime to set whether Rollback should be supported.
    private boolean m_isRollbackEnabled = false;

    private boolean m_isDeepCopy = false;

    public boolean isUnanticipated = false; //a flag to check unanticipated adaptation support

    //Synchronized variable
    private static final ReentrantLock m_lock = new ReentrantLock();

    private static Registry registry;

    public static Registry getRegistry() {
        m_lock.lock();
        try {
            if (registry == null) {
                registry = new Registry();
            }
        }finally {
            m_lock.unlock();
        }
        return registry;
    }

//    public ArrayDeque<Relation> getRelations() {
//        return m_relations;
//    }
//
//    public void setRelations(ArrayDeque<Relation> relations) {
//        this.m_relations = relations;
//    }

    public Collection<Object> getCores(){ return m_corePools.values();}

    public HashMap<Integer, Stack<ArrayDeque<Relation>>> getRollbackStacks(){
        return m_hashConfiguration;
    }

    public void clearCheckpoint(){
        m_hashConfiguration.clear();
    }

    public boolean isRollbackEnabled(){return m_isRollbackEnabled;}

    public void enableRollback(boolean bool){m_isRollbackEnabled=bool;}

    public boolean isDeepCopy(){return m_isDeepCopy;}

    public void setCheckpointMode(boolean isDeepCopy){
        m_isDeepCopy = isDeepCopy;
    }

    public synchronized ConcurrentHashMap<Long, Compartment> getActiveCompartments(){return m_activeCompartments;}

    public <T extends IPlayer> T newCore(Class<T> coreType, Object... args){
        T core = ReflectionHelper.newInstance(coreType, args);

        registerCoreCallable(core.coreCallable, core, coreType);

        m_corePools.put(core.hashCode(), core);

        return core;
    }

    public <T extends Compartment> T newCompartment(Class<T> compartmentType, Object... args){
        T compartment = ReflectionHelper.newInstance(compartmentType, args);

        registerCompartment(compartment, compartmentType);
        return compartment;
    }

    public <T extends IRole> IRole bind(RelationEnum relationEnum, IPlayer player, Class<T> roleType, Object... args){
        Compartment compartment = getActiveCompartment();
        if (compartment == null) throw new RuntimeException("No active compartment was found");

        return bind(relationEnum, compartment, player, roleType, args);

//        T role = ReflectionHelper.newInstance(roleType, args);

//        int compartmentId = compartment.hashCode();
//        SimpleEntry<Integer, LocalDateTime> consistency = m_consistencies.get(compartmentId);
//
//        Relation relation;
//        int depth;
//        if(relationEnum == RelationEnum.PLAY){
//            if(consistency==null) {
////                System.out.println("consistency is not present");
//                //Register core's methods for invocation
//                registerLiftingCallable(compartment, player, player, player.getClass());
//                registerLoweringCallable(compartment, player, player.getClass());
//            }
//            depth = 1;
//            relation = Relation.newRelation(compartment, player, player, role, depth);
//        }else{
//            //find the depth of current bound role
//            depth = compartment.getDepth(player) + 1;
//            Object core = compartment.getCore(player);
//            relation = Relation.newRelation(compartment, core, player, role, depth);
//        }
//
//        //Register relation
//        compartment.addRelation(relation);
//
//        if(consistency==null) {
//            //Register methods for invocation
//            registerLiftingCallable(compartment, relation.core, role, roleType);
//            registerLoweringCallable(compartment, role, roleType);
//        }
//
//        return role;
    }

    public <T extends IRole> IRole bind(RelationEnum relationEnum, Compartment compartment, IPlayer player, Class<T> roleType, Object... args) {
        T role = ReflectionHelper.newInstance(roleType, args);
        int compartmentId = compartment.hashCode();
        SimpleEntry<Integer, LocalDateTime> consistency = m_consistencies.get(compartmentId);

        Relation relation;
        int depth;
        if(relationEnum == RelationEnum.PLAY){
            if(consistency==null) {
//                System.out.println("consistency is not present");
                //Register core's methods for invocation
                registerLiftingCallable(compartment, player, player, player.getClass());
                registerLoweringCallable(compartment, player, player.getClass());
            }
            depth = 1;
            relation = Relation.newRelation(compartment, player, player, role, depth);
        }else{
            //find the depth of current bound role
            depth = compartment.getDepth(player) + 1;
            Object core = compartment.getCore(player);
            relation = Relation.newRelation(compartment, core, player, role, depth);
        }

        //Register relation
        compartment.addRelation(relation);

        if(consistency==null) {
            //Register methods for invocation
            registerLiftingCallable(compartment, relation.core, role, roleType);
            registerLoweringCallable(compartment, role, roleType);
        }

        return role;
    }


    public IRole bind(RelationEnum relationEnum, int compartmentId, int playerId, String roleType) {
        //Looking for a compartment by its Id
        Compartment compartment = m_activeCompartments.searchValues(1, value ->{
            if(value.hashCode() == compartmentId){
                return value;
            }
            return null;
        });

        Optional<Relation> optPlayer = compartment.getRelations().stream()
                .filter(p -> relationEnum == RelationEnum.PLAY?p.core.hashCode()==playerId:p.role.hashCode()==playerId)
                .findFirst();

        Object player;

        //TODO: the execution of the following code is very expensive
        if(!optPlayer.isPresent()){
//            Optional<Object> optCore = m_corePools.stream()
//                    .filter(p -> p.hashCode() == playerId).findFirst();
//
//            if(!optCore.isPresent()) throw new RuntimeException("No player in a given compartment was found.");
//
//            player = (IPlayer)optCore.get();

            player = m_corePools.get(playerId);
        }else {
            player = (relationEnum == RelationEnum.PLAY ? optPlayer.get().core : optPlayer.get().player);
        }

//        Class<T> roleClass = null;
//        try {
//            roleClass = (Class<T>)Class.forName(roleType);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return bind(relationEnum, compartment, player, roleClass, null);
        Class<?> roleClass = new ClassReloader().loadClass(roleType);
        Object role = null;
        try {
            role = roleClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
//        Object role = ReflectionHelper.newInstance(roleClass);
//        int compartmentId = compartment.hashCode();
        SimpleEntry<Integer, LocalDateTime> consistency = m_consistencies.get(compartmentId);

        Relation relation;
        int depth;
        if(relationEnum == RelationEnum.PLAY){
            if(consistency==null) {
//                System.out.println("consistency is not present");
                //Register core's methods for invocation
                registerLiftingCallable(compartment, player, player, player.getClass());
                registerLoweringCallable(compartment, player, player.getClass());
            }
            depth = 1;
            relation = Relation.newRelation(compartment, player, player, role, depth);
        }else{
            //find the depth of current bound role
            depth = compartment.getDepth(player) + 1;
            Object core = compartment.getCore(player);
            relation = Relation.newRelation(compartment, core, player, role, depth);
        }

        //Register relation
        compartment.addRelation(relation);

        if(consistency==null) {
            //Register methods for invocation
            registerLiftingCallable(compartment, relation.core, role, roleClass);
            registerLoweringCallable(compartment, role, roleClass);
        }

        return (IRole) role;
    }

    public IRole roleBindRole(RelationEnum relationEnum, IPlayer player, String roleType){
        Compartment compartment = getActiveCompartment();

        Class<?> roleClass = new ClassReloader().loadClass(roleType);
        Object role = null;
        try {
            role = roleClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
//        Object role = ReflectionHelper.newInstance(roleClass);
        int compartmentId = compartment.hashCode();
        SimpleEntry<Integer, LocalDateTime> consistency = m_consistencies.get(compartmentId);

        Relation relation;
        int depth;
        if(relationEnum == RelationEnum.PLAY){
            if(consistency==null) {
//                System.out.println("consistency is not present");
                //Register core's methods for invocation
                registerLiftingCallable(compartment, player, player, player.getClass());
                registerLoweringCallable(compartment, player, player.getClass());
            }
            depth = 1;
            relation = Relation.newRelation(compartment, player, player, role, depth);
        }else{
            //find the depth of current bound role
            depth = compartment.getDepth(player) + 1;
            Object core = compartment.getCore(player);
            relation = Relation.newRelation(compartment, core, player, role, depth);
        }

        //Register relation
        compartment.addRelation(relation);

        if(consistency==null) {
            //Register methods for invocation
            registerLiftingCallable(compartment, relation.core, role, roleClass);
            registerLoweringCallable(compartment, role, roleClass);
        }

        return (IRole) role;
    }

//    public <T extends IRole> T bind(RelationEnum relationEnum, int compartmentId, int playerId, String roleType) {
//        //Looking for a compartment by its Id
//        Compartment compartment = m_activeCompartments.searchValues(1, value ->{
//            if(value.hashCode() == compartmentId){
//                return value;
//            }
//            return null;
//        });
//
//        Optional<Relation> optPlayer = compartment.getRelations().stream()
//                .filter(p -> relationEnum == RelationEnum.PLAY?p.core.hashCode()==playerId:p.player.hashCode()==playerId)
//                .findFirst();
//
//        IPlayer player;
//
//        //TODO: the execution of the following code is very expensive
//        if(!optPlayer.isPresent()){
//            Optional<Object> optCore = m_corePools.stream()
//                    .filter(p -> p.hashCode() == playerId).findFirst();
//
//            if(!optCore.isPresent()) throw new RuntimeException("No player in a given compartment was found.");
//
//            player = (IPlayer)optCore.get();
//        }else {
//            player = (IPlayer) (relationEnum == RelationEnum.PLAY ? optPlayer.get().core : optPlayer.get().player);
//        }
//
//        Class<T> roleClass = null;
//        try {
//            roleClass = (Class<T>)Class.forName(roleType);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return bind(relationEnum, compartment, player, roleClass, null);
//    }

    public <T extends IRole> IRole rebind(RelationEnum relationEnum, IPlayer player, Class<T> roleType, Object... args){
        unbind(relationEnum, player, roleType);
        return bind(relationEnum, player, roleType, args);
    }

    public Object rebind(RelationEnum relationEnum, int compartmentId, int playerId, String roleType){
        unbind(relationEnum, compartmentId, playerId, roleType);
        return bind(relationEnum, compartmentId, playerId, roleType);
    }

    public void unbind(RelationEnum relationEnum, int compartmentId, int playerId, String roleType){
        //Looking for a compartment by its Id
        Compartment compartment = m_activeCompartments.searchValues(1, value ->{
            if(value.hashCode() == compartmentId){
                return value;
            }
            return null;
        });

        Optional<Relation> optPlayer = compartment.getRelations().stream()
                .filter(p -> relationEnum == RelationEnum.PLAY?p.core.hashCode()==playerId:p.player.hashCode()==playerId)
                .findFirst();

        if(!optPlayer.isPresent()) throw new RuntimeException("No player in a given compartment was found.");

        IPlayer player = (IPlayer)(relationEnum == RelationEnum.PLAY?optPlayer.get().core:optPlayer.get().player);


        Class<?> roleClass = null;
        try {
            roleClass = Class.forName(roleType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        unbind(relationEnum, player, roleClass);
    }

    public void unbind(RelationEnum relationEnum, IPlayer player, Class<?> roleType){
        Compartment compartment = getActiveCompartment();
        if (compartment == null) throw new RuntimeException("No active compartment was found");

        Optional<Relation> optRelation;
        if(relationEnum == RelationEnum.PLAY){
//            optRelation = compartment.getRelations().stream()
//                    .filter(p -> p.core.equals(player) && p.role.getClass().equals(roleType))
//                    .findFirst();
            //TODO: This's a tweak to work for unanticipated unbinding
            optRelation = compartment.getRelations().stream()
                    .filter(p -> p.core.equals(player) && p.role.getClass().getSimpleName().equals(roleType.getSimpleName()))
                    .findFirst();
        }else{
            optRelation = compartment.getRelations().stream()
                    .filter(p -> p.player == player && p.role.getClass().equals(roleType))
                    .findFirst();
        }

        if(!optRelation.isPresent()) return;
        Relation relation = optRelation.get();

        int compartmentId = compartment.hashCode();
        SimpleEntry<Integer, LocalDateTime> consistency = m_consistencies.get(compartmentId);

        List<Relation> deepRelations = traverseRelation(relation);

        if(consistency == null){
            for(Relation r : deepRelations){
                removeLoweringCallable(compartment, r.player, r.role.getClass());
                removeLiftingCallable(compartment, r.role, r.role.getClass());
                compartment.getRelations().removeIf(p -> p.equals(r));
            }
            removeLiftingCallable(compartment, player, roleType);
            removeLoweringCallable(compartment, relation.role, roleType);
            compartment.getRelations().removeIf(p -> p.equals(relation));

            //re-register all callables
            reRegisterCallable(compartment, relation.core);
        }else{
            LocalDateTime unboundTime = LocalDateTime.now();
            relation.unboundTime = unboundTime;
            for(Relation r : deepRelations){
                r.unboundTime = unboundTime;
            }
        }
    }

    public void unbindAll(IPlayer player){
        Compartment compartment = getActiveCompartment();
        if (compartment == null) throw new RuntimeException("No active compartment was found");

        Optional<Relation> optCoreRelation = compartment.getRelations().stream()
                .filter(p -> p.compartment.equals(compartment) && p.core.equals(player) && p.player.equals(player))
                .findFirst();

        if(!optCoreRelation.isPresent()) return;
        Relation coreRelation = optCoreRelation.get();
        List<Relation> rs = traverseRelation(coreRelation);

//        compartment.getRelations().removeIf(p -> p.equals(coreRelation));
//        for(Relation r : rs){
//            compartment.getRelations().removeIf(p -> p.equals(r));
//        }
//        reRegisterCallable(compartment, player);


//        int compartmentId = compartment.hashCode();
//        SimpleEntry<Integer, LocalDateTime> consistency = m_consistencies.get(compartmentId);
//
//        List<Relation> deepRelations = traverseRelation(coreRelation);
//
//        if(consistency == null){
//            for(Relation r : deepRelations){
//                removeLoweringCallable(compartment, r.player, r.role.getClass());
//                removeLiftingCallable(compartment, r.role, r.role.getClass());
//                compartment.getRelations().removeIf(p -> p.equals(r));
//            }
//            removeLiftingCallable(compartment, player, roleType);
//            removeLoweringCallable(compartment, relation.role, roleType);
//            compartment.getRelations().removeIf(p -> p.equals(relation));
//
//            //re-register all callables
//            reRegisterCallable(compartment, relation.core);
//        }else{
//            LocalDateTime unboundTime = LocalDateTime.now();
//            relation.unboundTime = unboundTime;
//            for(Relation r : deepRelations){
//                r.unboundTime = unboundTime;
//            }
//        }
    }

    public void transfer(IPlayer fromPlayer, IPlayer toPlayer, Class<?> roleType){
        Compartment compartment = getActiveCompartment();
        if (compartment == null) throw new RuntimeException("No active compartment was found");

        Optional<Relation> optFromCore;
        optFromCore = compartment.getRelations().stream()
                .filter(p -> p.core == fromPlayer && p.role.getClass().equals(roleType))
                .findFirst();

//        Optional<Relation> optToCore = compartment.getRelations().stream()
//                .filter(p -> p.role == toPlayer).findFirst();

        if(!optFromCore.isPresent()) return;
        Relation fromRelation = optFromCore.get();

        int compartmentId = compartment.hashCode();
        SimpleEntry<Integer, LocalDateTime> consistency = m_consistencies.get(compartmentId);

        List<Relation> deepRelations = traverseRelation(fromRelation);

        if(consistency == null){
            int depth = 1;
            Object player = toPlayer;

            Relation cloneRelation = fromRelation.clone();
            cloneRelation.core = toPlayer;
            cloneRelation.player = player;
            cloneRelation.depth = depth;
            compartment.getRelations().add(cloneRelation);
            player = cloneRelation.role;

            compartment.getRelations().removeIf(p -> p.equals(fromRelation));

            for(Relation r : deepRelations){
                removeLoweringCallable(compartment, r.player, r.role.getClass());
                removeLiftingCallable(compartment, r.role, r.role.getClass());

                cloneRelation = r.clone();
                compartment.getRelations().removeIf(p -> p.equals(r));

                cloneRelation.core = toPlayer;
                cloneRelation.player = player;
                cloneRelation.depth = depth++;
                compartment.getRelations().add(cloneRelation);
                player = cloneRelation.role;
            }

            reRegisterCallable(compartment, fromPlayer);
            reRegisterCallable(compartment, toPlayer);

        }else{
            //TODO: Add Transfer Time in the Relation
        }
    }

    public void transfer(int compartmentId, int fromCoreId, int toCoreId, String roleType){
        //Looking for a compartment by its Id
        Compartment compartment = m_activeCompartments.searchValues(1, value ->{
            if(value.hashCode() == compartmentId){
                return value;
            }
            return null;
        });

        Class<?> roleClass = null;
        try {
            roleClass = Class.forName(roleType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Class<?> finalRoleClass = roleClass;

        Optional<Relation> optPlayer = compartment.getRelations().stream()
                .filter(p -> p.core.hashCode()==fromCoreId && p.role.getClass().equals(finalRoleClass))
                .findFirst();

        if(!optPlayer.isPresent()) throw new RuntimeException("No player in a given compartment was found.");

        //TODO: an execution of the following code is very expensive
        IPlayer fromCore = (IPlayer)optPlayer.get().core;
//        Optional<Object> optToCore = m_corePools.stream()
//                .filter(p -> p.hashCode() == toCoreId).findFirst();
//
//        IPlayer toCore = (IPlayer)optToCore.get();
        IPlayer toCore = (IPlayer) m_corePools.get(toCoreId);
        transfer(fromCore, toCore, roleClass);
    }

    private List<Relation> traverseRelation(Relation relation){
        Compartment compartment = (Compartment)relation.compartment;
        List<Relation> list = new ArrayList<>();

        Optional<Relation> nextRelation = compartment.getRelations().stream()
                .filter(p -> p.compartment.equals(compartment) &&
                p.player.equals(relation.role) && p.depth>relation.depth
                && p.unboundTime==null)
                .findFirst();

        if(nextRelation.isPresent()) {
            list.add(nextRelation.get());
            list.addAll(traverseRelation(nextRelation.get()));
        }

        return list;
    }

    public void registerCoreCallable(ConcurrentHashMap<Integer, CallableMethod> hashCallable, Object object, Class<?> clazz){
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            Callable<?> c = getCallable(clazz, method);
            MethodSig ms = getMethodSig(method);
            String m = String.format("%d:%s", object.hashCode(), ms.toString());
            //CallableMethod cm = new CallableMethod(m, object, c);
            CallableMethod cm = new CallableMethod(m, object, c, method);
            hashCallable.put(m.hashCode(), cm);
        }
    }

    public void registerCompartment(Compartment compartment, Class<?> compartmentType){
        Method[] methods = compartmentType.getDeclaredMethods();

        for(Method method: methods){
            Callable<?> c = getCallable(compartmentType, method);
            MethodSig ms = getMethodSig(method);
            String m = String.format("%d:%s", compartment.hashCode(), ms.toString());
//            CallableMethod cm = new CallableMethod(m, compartment, c);
            CallableMethod cm = new CallableMethod(m, compartment, c, method);
            compartment.compartmentCallable.put(m.hashCode(), cm);
        }
    }

    public synchronized void registerLiftingCallable(Compartment compartment, Object core, Object role, Class<?> roleType){
//        int compId = compartment.hashCode();
//        int objectId = core.hashCode();

        Method[] methods = roleType.getDeclaredMethods();
//        int hashCode = (compId + ":" + objectId).hashCode();
//        HashMap<Integer, CallableMethod> hash = liftingCallable.get(hashCode); //get callable method on an object

        for (Method method : methods) {
            Callable<?> c = getCallable(roleType, method);
//            Callable<?> c = ProxyFactory.generateCallable(role, method.getName(), )
            //String m = String.format("%d:%d:%s", compId, role.hashCode(), getMethodForKey(method));
            MethodSig ms = getMethodSig(method);
            String m = String.format("%d:%s", core.hashCode(), ms.toString());

            //CallableMethod cm = new CallableMethod(m, role, c);
            CallableMethod cm = new CallableMethod(m, role, c, method);

            compartment.liftingCallable.put(m.hashCode(), cm); //put each method
        }
    }

    public void registerLoweringCallable(Compartment compartment, Object role, Class<?> roleType){
        Method[] methods = roleType.getDeclaredMethods();
//        int hashCode = (compId + ":" + objectId).hashCode();
//        HashMap<Integer, CallableMethod> hash = liftingCallable.get(hashCode); //get callable method on an object

        for (Method method : methods) {
            Callable<?> c = getCallable(roleType, method);
            MethodSig ms = getMethodSig(method);
            String m = String.format("%d:%s", role.hashCode(), ms.toString());

            //CallableMethod cm = new CallableMethod(m, role, c);
            CallableMethod cm = new CallableMethod(m, role, c, method);

            compartment.loweringCallable.put(m.hashCode(), cm); //put each method
        }
    }

    private void removeLiftingCallable(Compartment compartment, Object core, Class<?> roleType){
        Method[] methods = roleType.getDeclaredMethods();

        for(Method method : methods){
            MethodSig ms = getMethodSig(method);
            String m = String.format("%d:%s", core.hashCode(), ms.toString());
            compartment.liftingCallable.remove(m.hashCode());
        }
    }

    private void removeLoweringCallable(Compartment compartment, Object role, Class<?> roleType){
        Method[] methods = roleType.getDeclaredMethods();

        for(Method method : methods){
            MethodSig ms = getMethodSig(method);
            String m = String.format("%d:%s", role.hashCode(), ms.toString());
            compartment.loweringCallable.remove(m.hashCode());
        }
    }

    public void reRegisterCallable(Compartment compartment, Object core){
        List<Relation> relations = compartment.getRelations().stream()
                .filter(p -> p.compartment.equals(compartment) &&
                p.core.equals(core)
                ).sorted(Comparator.comparingInt(p -> p.depth))
                .collect(Collectors.toList());

        if(relations.isEmpty()){
            //Overide the callable of the core. Because it can happen if unbinding resulting in empty set
            registerLiftingCallable(compartment, core, core, core.getClass());
            registerLoweringCallable(compartment, core, core.getClass());
        }else {
            for (Relation r : relations) {
                //re-register core's method
                registerLiftingCallable(compartment, r.core, r.core, r.core.getClass());
                registerLoweringCallable(compartment, r.core, r.core.getClass());

                //re-register role's method
                registerLiftingCallable(compartment, r.core, r.role, r.role.getClass());
                registerLoweringCallable(compartment, r.role, r.role.getClass());
            }
        }
    }

    public Callable<?> getCallable(Class<?> clazz, Method method) {
        try {
            Bootstrap bootstrap = new Bootstrap(Cache.Save, "net.lyrt.IndyBootstrap", "callDispatch", clazz, method.getName());
            MethodSignature sig = new MethodSignature(method.getReturnType(), clazz, method.getParameterTypes());

            //Class rType = method.getReturnType();
            Callable<?> callable = ProxyFactory.generateInvokeDynamicCallable(bootstrap, sig);

            return callable;
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    /**
     * Get method for constructing as the key for method caching in composition
     * @param method
     * @return
     */
//    public String getMethodForKey(Method method) {
//        String name = method.getName();
//        Class[] paramTypes = method.getParameterTypes();
//
//        String retType = method.getReturnType().getTypeName();
//
//        String paramTypesStr = "";
//        for (Class param : paramTypes) {
//            paramTypesStr += param.getName() + ", ";
//        }
//        if (paramTypes.length > 0) {
//            paramTypesStr = paramTypesStr.substring(0, paramTypesStr.length() - 2);
//        }
//
//        String concat = String.format("%s %s(%s)", retType, name, paramTypesStr);
////        System.out.println("Reg: " + concat + " " + concat.hashCode());
//        return concat;
//        //return retType + " " + name + "(" + paramTypesStr + ")";
//    }

    public MethodSig getMethodSig(Method method){
        return new MethodSig(method.getReturnType(), method.getName(), method.getParameterTypes());
    }

    protected MethodSig getMethodSig(Class<?> retType, String methodName, Object... args){
        Class<?>[] paramTypes = new Class[args.length];
        for(int i=0; i<args.length; i++){
            paramTypes[i] = args[i].getClass();
        }

        return new MethodSig(retType, methodName, paramTypes);
    }

    public void activateCompartment(Compartment compartment) {
        long threadId = Thread.currentThread().getId();

        SimpleEntry<Integer, LocalDateTime> consistency = m_consistencies.get(threadId);
        if(consistency!=null) throw new RuntimeException("Compartment cannot be activated inside a transaction");

        //will override the existing compartment
        m_activeCompartments.put(threadId, compartment);
    }

    public void deactivateCompartment(Compartment compartment) {
        long threadId = Thread.currentThread().getId();

        SimpleEntry<Integer, LocalDateTime> consistency = m_consistencies.get(threadId);
        if(consistency!=null) throw new RuntimeException("Compartment cannot be deactivated inside a transaction");

        m_activeCompartments.remove(threadId);
    }

    public void destroyCompartment(Compartment compartment){
        long threadId = Thread.currentThread().getId();
        m_activeCompartments.remove(threadId);

        //remove all roles
        compartment.getRelations().clear();
        compartment = null;
    }

    public Compartment getActiveCompartment() {
//        m_lock.lock();
//        try {
            long threadId = Thread.currentThread().getId();

            Compartment compartment = m_activeCompartments.get(threadId);

            return compartment;
//        }finally {
//            m_lock.unlock();
//        }
    }

    public <T> T invoke(ConcurrentHashMap<Integer, CallableMethod> hash, ConcurrentHashMap<Integer, CallableMethod> coreCallable,
                        Class<T> retType, String method, Object... args){
        CallableMethod cm = hash.get(method.hashCode());
//        System.out.println(hash);
        if(cm==null) {
            cm = coreCallable.get(method.hashCode());
            if(cm==null) throw new RuntimeException("No method was found.");
        }
        Object objRet=null;
        if(isUnanticipated) {
            try {
                objRet = cm.reflectedMethod.invoke(cm.invokingObject, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException();
            }
        }else {
            objRet = cm.callable.invoke(cm.invokingObject, args);
        }
        if(retType!=null && !retType.isAssignableFrom(void.class) && !retType.isAssignableFrom(Void.class)) {
            return (retType.isPrimitive())?(T)objRet:retType.cast(objRet);
        }
        return null;
    }

//    public <T> T invoke(ConcurrentHashMap<Integer, CallableMethod> hash, Class<T> retType, String method, Object... args){
//        CallableMethod cm = hash.get(method.hashCode());
//
//        if(cm==null) {
//            cm = coreCallable.get(method.hashCode());
//            if(cm==null) throw new RuntimeException("No method was found.");
//        }
//        Object objRet = cm.callable.invoke(cm.invokingObject, args);
//        if(retType!=null && !retType.isAssignableFrom(void.class) && !retType.isAssignableFrom(Void.class)) {
//            return (retType.isPrimitive())?(T)objRet:retType.cast(objRet);
//        }
//        return null;
//    }

    public <T> T invoke(ConcurrentHashMap<Integer, CallableMethod> hash, Class<T> retType, String method, Object... args){
        return invoke(hash, null, retType, method, args);
    }

    public <T> T invoke(RelationEnum relationEnum, int compartmentId, int playerId, String method, Class<T> retType, Object... args){
        //Looking for a compartment by its Id
        Compartment compartment = m_activeCompartments.searchValues(1, value ->{
            if(value.hashCode() == compartmentId){
                return value;
            }
            return null;
        });

        Optional<Relation> optPlayer = compartment.getRelations().stream()
                .filter(p -> relationEnum == RelationEnum.PLAY?p.core.hashCode()==playerId:p.player.hashCode()==playerId)
                .findFirst();

        IPlayer player;

        //TODO: the execution of the following code is very expensive
        if(!optPlayer.isPresent()){
//            Optional<Object> optCore = m_corePools.stream()
//                    .filter(p -> p.hashCode() == playerId).findFirst();
//
//            if(!optCore.isPresent()) throw new RuntimeException("No player in a given compartment was found.");
//
//            player = (Player)optCore.get();
            player = (IPlayer)m_corePools.get(playerId);
        }else {
            player = (IPlayer) (relationEnum == RelationEnum.PLAY ? optPlayer.get().core : optPlayer.get().player);
        }

        MethodSig ms = getMethodSig(retType, method, args);
        String m = String.format("%d:%s", playerId, ms.toString());

        return invoke(compartment.liftingCallable, player.coreCallable, retType, m, args);
    }

    public void registerConsistencyBlock(Compartment compartment, int consistencyId, LocalDateTime time){
        m_lock.lock();
        try{
            int compartmentId = compartment.hashCode();
            SimpleEntry<Integer, LocalDateTime> consistency = m_consistencies.get(compartmentId);
            if(consistency == null) {
                m_consistencies.put(compartmentId, new SimpleEntry<>(consistencyId, time));
                List<Relation> relationList = compartment.getRelations().stream()
                        .filter(p -> p.compartment == compartment)
                        .collect(Collectors.toList());

                for(Relation r : relationList){
                    //ConcurrentHashMap<Integer, CallableMethod> o = compartment.la

                }
            }else{
                throw new RuntimeException("Two or more transaction are running in parallel in a single thread.");
            }
        }finally {
            m_lock.unlock();
        }
    }

    public void removeConsistencyBlock(Compartment compartment, int consistencyId){
        m_lock.lock();
        try{
            int compartmentId = compartment.hashCode();
            SimpleEntry<Integer, LocalDateTime> consistency = m_consistencies.get(compartmentId);
            if(consistency.getKey() == consistencyId){
                LocalDateTime startTime = consistency.getValue();

                //remove phantom role
                reAdjustCallable(startTime, compartment);

                //Re-adjust callable for liftingCallable and loweringCallable
//                List<Relation> relationList = compartment.getRelations().stream()
//                        .filter(p -> p.compartment == compartment && p.boundTime.isAfter(startTime))
//                        .collect(Collectors.toList());
//
//                for(Relation r: relationList){
//                    registerLiftingCallable(compartment, r.core, r.role, r.role.getClass());
//                    registerLoweringCallable(compartment, r.role, r.role.getClass());
//                }

                //remove from consistency object from HashMap
                m_consistencies.remove(compartmentId);
            }
        }finally {
            m_lock.unlock();
        }
    }

    private void reAdjustCallable(LocalDateTime consistencyTime, Compartment compartment){
//        List<Relation> relations = compartment.getRelations().stream()
//                .filter(p -> p.compartment.equals(compartment)
//                    && p.unboundTime!=null && p.unboundTime.isAfter(consistencyTime))
//                .collect(Collectors.toList());
//
//        for(Relation r : relations){
//            removeLiftingCallable(compartment, r.core, r.role.getClass());
//            removeLoweringCallable(compartment, r.role, r.role.getClass());
//            compartment.getRelations().removeIf(p -> p.equals(r));
//        }

        Map<Object, Long> list = compartment.getRelations().stream()
                .filter(p -> p.compartment.equals(compartment) &&
                        (p.unboundTime!=null || p.boundTime.isAfter(consistencyTime)))
                .collect(Collectors.groupingBy(Relation::getCore, Collectors.counting()));

        //Delete phantom roles
        compartment.getRelations().removeIf(p -> p.compartment.equals(compartment) && p.unboundTime!=null);

        list.forEach((k, v) -> reRegisterCallable(compartment, k));
    }

    // Setup ThreadLocal of Kryo instances
    public static final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            //First attempt to serialize object with zero args constructor. If not available,
            //StdInstantiatorStrategy is used as a fallback to serial object without zero args constructor.
            kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

            return kryo;
        }
    };

}
