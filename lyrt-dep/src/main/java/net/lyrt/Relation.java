package net.lyrt;

import java.time.LocalDateTime;

/**
 * Created by nguonly on 5/12/17.
 */
public class Relation {
    public Object compartment;
    public Object core;
    public Object player;
    public Object role;

    public int depth; //Depth of the deep role relation

    public LocalDateTime boundTime;
    public LocalDateTime unboundTime;

    public static Relation newRelation(Compartment compartment, Object core, Object player, Object role, int depth){
        Relation relation = new Relation();

        relation.compartment = compartment;
        relation.core = core;
        relation.player = player;
        relation.role = role;
        relation.depth = depth;
        relation.boundTime = LocalDateTime.now();

        return relation;
    }

    public Object getCore(){return core;}

    public Relation clone(){
        Relation r = new Relation();
        r.compartment = this.compartment;
        r.core = this.core;
        r.player = this.player;
        r.role = this.role;
        r.depth = this.depth;
        r.boundTime = this.boundTime;
        r.unboundTime = this.unboundTime;

        return r;
    }

    public String toString(){
//        System.out.format("%30s %12s %12s %30s %30s %30s %5s\n",
//                "Compartment", "BoundTime", "UnBoundTime", "Core", "Player", "Role", "Lvl");
        String strBoundTime = boundTime!=null?boundTime.toLocalTime().toString():"";
        String strUnBoundTime = unboundTime!=null?unboundTime.toLocalTime().toString():"";
        String msg = String.format("%30s %12s %12s %30s %30s %30s %5s",
                compartment.hashCode() + ":" + compartment.getClass().getSimpleName(),
                strBoundTime, strUnBoundTime,
                core.hashCode() + ":" + core.getClass().getSimpleName(),
                player.hashCode() + ":" + player.getClass().getSimpleName(),
                role.hashCode() + ":" + role.getClass().getSimpleName(),
                depth
                );

        return msg;
    }
}
