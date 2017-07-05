package net.lyrt;

/**
 * Created by nguonly on 6/14/17.
 */
public enum RelationEnum {
    PLAY (1),      //core plays role
    DEEP_PLAY (2); //role plays role

    private final int relationCode;

    RelationEnum(int relationCode){
        this.relationCode = relationCode;
    }

    public int getRelationCode(){
        return this.relationCode;
    }
}
