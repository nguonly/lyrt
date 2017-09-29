package recovery;

//Recover Baseline Relation
public class RecoveryBLRelation {
    public Integer id;
    public Object role;

    public RecoveryBLRelation clone(){
        RecoveryBLRelation r = new RecoveryBLRelation();
        r.id = id;
        r.role = role;

        return r;
    }
}
