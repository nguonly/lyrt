package net.lyrt.unanticipation;

/**
 * Created by nguonly on 6/23/17.
 */
public class XMLConstructor {
    private StringBuffer buffer = new StringBuffer();

    public void openClosure(){
        buffer.append("<?xml version=\"1.0\"?>\n");
        buffer.append("<adaptation>\n");
    }

    public void closeClosure(){
        buffer.append("</adaptation>\n");
    }

    public void append(String text){
        buffer.append(text + "\n");
    }

    @Override
    public String toString(){
        return buffer.toString();
    }

    public static String getXMLBindingBaseOperation(int compartmentId, int playerId, boolean isCore, boolean isForBinding, String roleType){
        XMLConstructor xml = new XMLConstructor();

        String strOperation = isForBinding?"bind":"rebind";
        String strActor = isCore?"coreId":"roleId";

        xml.openClosure();
        xml.append("<compartment id=\"" + compartmentId + "\" >");
        xml.append("<" + strOperation + " " + strActor + "=\"" + playerId +"\" roleType=\"" + roleType + "\" />");
        xml.append("</compartment>");
        xml.closeClosure();

        return xml.toString();
    }

    public static String getXMLUnbindBaseOperation(int compartmentId, int playerId, boolean isCore, String roleType){
        XMLConstructor xml = new XMLConstructor();

        String strActor = isCore?"coreId":"roleId";

        xml.openClosure();
        xml.append("<compartment id=\"" + compartmentId + "\" >");
        xml.append("<unbind " + strActor + "=\"" + playerId +"\" roleType=\"" + roleType + "\" />");
        xml.append("</compartment>");
        xml.closeClosure();

        return xml.toString();
    }

    public static String getXMLTransferOperation(int compartmentId, int fromCoreId, int toCoreId, String roleType){
        XMLConstructor xml = new XMLConstructor();

        xml.openClosure();
        xml.append("<compartment id=\"" + compartmentId + "\" >");
        xml.append("<transfer fromCoreId=\"" + fromCoreId +"\" toCoreId=\"" + toCoreId + "\" roleType=\"" + roleType + "\" />");
        xml.append("</compartment>");
        xml.closeClosure();

        return xml.toString();
    }
}
