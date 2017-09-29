package net.lyrt.unanticipation;

import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.RelationEnum;
import net.lyrt.helper.BoxingHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * Created by nguonly on 6/23/17.
 */
public class UnanticipatedXMLParser {
    public static void parseFile(String filePath){
        File inputFile = new File(filePath);
        try {
            Reader reader = new InputStreamReader(new FileInputStream(inputFile));
            parse(reader);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void parse(Reader reader) {
        try {
            InputSource is = new InputSource(reader);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            Registry registry = Registry.getRegistry();

            ////////////////////////
            // compartment tag
            ////////////////////////
            NodeList nodeList = doc.getElementsByTagName("compartment");
            for (int i = 0; i < nodeList.getLength(); i++) {
                //component tag
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String sCompId = element.getAttribute("id");
                    int compartmentId = sCompId.isEmpty() ? 0 : Integer.parseInt(sCompId);

                    //Create compartment if it doesn't have one
                    Compartment compartment;
                    if (compartmentId <= 0) {
                        //compartment = Compartment.initialize(Compartment.class);
                        compartment = registry.newCompartment(Compartment.class);
                        compartment.activate();
                        compartmentId = compartment.hashCode();
                    }

                    processBind(element, compartmentId);

                    processRebindOperation(element, compartmentId);

                    processUnbind(element, compartmentId);

                    processTransfer(element, compartmentId);

                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void parse(String xml){
        Reader reader = new StringReader(xml);
        parse(reader);
    }

    private static void processBind(Element element, int compartmentId) throws Throwable{
        Registry registry = Registry.getRegistry();

        NodeList bindNodes = element.getElementsByTagName("bind");
        for (int iBind = 0; iBind < bindNodes.getLength(); iBind++) {
            Node bindNode = bindNodes.item(iBind);
            Element bindElement = (Element) bindNode;
//            int coreId = Integer.parseInt(bindElement.getAttribute("coreId"));

            String strCoreId = bindElement.getAttribute("coreId");
            String strRoleId = bindElement.getAttribute("roleId");
            int bindCoreId = strCoreId.equals("")?0:Integer.parseInt(strCoreId);
            int bindRoleId = strRoleId.equals("")?0:Integer.parseInt(strRoleId);

            String roleClass = bindElement.getAttribute("roleType");
//                        boolean reload = StringValueConverter.convert(bindElement.getAttribute("reload"), boolean.class);
//            System.out.format("%10s %10s %s\n", compartmentId, coreId, roleClass);

            if(!strCoreId.isEmpty()) {
                //Bind role to core object
                registry.bind(RelationEnum.PLAY, compartmentId, bindCoreId, roleClass);
            }
            if(!strRoleId.isEmpty()){
                registry.bind(RelationEnum.DEEP_PLAY, compartmentId, bindRoleId, roleClass);
            }

            ///////////////////
            //invoke tag
            ///////////////////
            processInvocation(bindElement, compartmentId, bindCoreId, bindRoleId);
        }
    }

    private static void processRebindOperation(Element element, int compartmentId) throws Throwable{
        Registry registry = Registry.getRegistry();
        NodeList bindNodes = element.getElementsByTagName("rebind");
        for(int iBind=0; iBind<bindNodes.getLength(); iBind++){
            Node bindNode = bindNodes.item(iBind);
            Element bindElement = (Element) bindNode;

            String strCoreId = bindElement.getAttribute("coreId");
            String strRoleId = bindElement.getAttribute("roleId");
            int bindCoreId = strCoreId.equals("")?0:Integer.parseInt(strCoreId);
            int bindRoleId = strRoleId.equals("")?0:Integer.parseInt(strRoleId);

            String roleClass = bindElement.getAttribute("roleType");

            if(!strCoreId.isEmpty()) {
                //Bind role to core object
                registry.rebind(RelationEnum.PLAY, compartmentId, bindCoreId, roleClass);
            }
            if(!strRoleId.isEmpty()){
                registry.rebind(RelationEnum.DEEP_PLAY, compartmentId, bindRoleId, roleClass);
            }

            ///////////////////
            //invoke tag
            ///////////////////
            NodeList invokeNodes = bindElement.getElementsByTagName("invoke");
            //System.out.println("::: invoke :::: " + invokeNodes.getLength());
            for(int iInvoke=0; iInvoke<invokeNodes.getLength(); iInvoke++){
                Node invokeNode = invokeNodes.item(iInvoke);
                Element invokeNodeElement = (Element)invokeNode;
                String method = invokeNodeElement.getAttribute("method");
                System.out.println(method);

                //param nodes
                NodeList paramNodes = invokeNodeElement.getElementsByTagName("param");
                Object[] paramValues = new Object[paramNodes.getLength()];
                Class[] paramClasses = new Class[paramNodes.getLength()];
                for(int iParam=0; iParam<paramNodes.getLength(); iParam++){
                    Node paramNode = paramNodes.item(iParam);
                    Element paramNodeElement = (Element)paramNode;
                    paramClasses[iParam] = Class.forName(paramNodeElement.getAttribute("type"));
                    paramValues[iParam] = StringValueConverter.convert(paramNodeElement.getAttribute("value"), paramClasses[iParam]);
                }

                //Return type
                Class returnType = Class.forName(invokeNodeElement.getAttribute("returnType"));
                //Prepare method definition to invoke
//                            Object core = registry.getCoreObjectMap().get(coreId);
                //core.invoke(method, paramClasses, paramValues);
//                            registry.invokeRole(null, core, method, returnType, paramClasses, paramValues);
                if(!strCoreId.isEmpty()) {
                    registry.invoke(RelationEnum.PLAY, compartmentId, bindCoreId, method, returnType);
                }
                if(!strRoleId.isEmpty()){
                    registry.invoke(RelationEnum.DEEP_PLAY, compartmentId, bindRoleId, method, returnType);
                }
            }
        }
    }

    private static void processInvocation(Element bindElement, int compartmentId, int bindCoreId, int bindRoleId) throws ClassNotFoundException {
        NodeList invokeNodes = bindElement.getElementsByTagName("invoke");
        //System.out.println("::: invoke :::: " + invokeNodes.getLength());
        for(int iInvoke=0; iInvoke<invokeNodes.getLength(); iInvoke++){
            Node invokeNode = invokeNodes.item(iInvoke);
            Element invokeNodeElement = (Element)invokeNode;
            String method = invokeNodeElement.getAttribute("method");
            System.out.println(" >>> invoke " + method);

            //param nodes
            NodeList paramNodes = invokeNodeElement.getElementsByTagName("param");
            Object[] paramValues = new Object[paramNodes.getLength()];
            Class[] paramClasses = new Class[paramNodes.getLength()];
            for(int iParam=0; iParam<paramNodes.getLength(); iParam++){
                Node paramNode = paramNodes.item(iParam);
                Element paramNodeElement = (Element)paramNode;
                paramClasses[iParam] = Class.forName(paramNodeElement.getAttribute("type"));
                paramValues[iParam] = StringValueConverter.convert(paramNodeElement.getAttribute("value"), paramClasses[iParam]);
            }

            //Return type
            Class returnType = BoxingHelper.descriptorPrimitive.get(invokeNodeElement.getAttribute("returnType"));
//            System.out.println(returnType);
            //Prepare method definition to invoke
//                            Object core = registry.getCoreObjectMap().get(coreId);
            //core.invoke(method, paramClasses, paramValues);
//                            registry.invokeRole(null, core, method, returnType, paramClasses, paramValues);
            Registry registry = Registry.getRegistry();
            if(bindCoreId!=0) {
                registry.invoke(RelationEnum.PLAY, compartmentId, bindCoreId, method, returnType);
            }
            if(bindRoleId!=0){
                registry.invoke(RelationEnum.DEEP_PLAY, compartmentId, bindRoleId, method, returnType);
            }
        }
    }

    private static void processUnbind(Element element, int compartmentId) throws Throwable{
        Registry registry = Registry.getRegistry();
        NodeList unbindNodes = element.getElementsByTagName("unbind");
        //System.out.println("::: unbind :::");
        for(int iUnbind=0; iUnbind<unbindNodes.getLength(); iUnbind++){
            Node unbindNode = unbindNodes.item(iUnbind);
            Element unbindElement = (Element)unbindNode;
            String strCoreId = unbindElement.getAttribute("coreId");
            String strRoleId = unbindElement.getAttribute("roleId");
            int unbindCoreId = Integer.parseInt(strCoreId.equals("")?"0":strCoreId);
            int unbindRoleId = Integer.parseInt(strRoleId.equals("")?"0":strRoleId);
            String unbindRole = unbindElement.getAttribute("roleType");

            //Prepare unbind operation
            //unbind role from core
            if(unbindCoreId>0 && !unbindRole.isEmpty()) {
//                System.out.println("::: unbind coreId = " + unbindCoreId + " :::");
                registry.unbind(RelationEnum.PLAY, compartmentId, unbindCoreId, unbindRole);
            }

            //unbind role from role
            if(unbindRoleId>0 && !unbindRole.isEmpty()) {
//                System.out.println("::: unbind coreId = " + unbindCoreId + " :::");
                registry.unbind(RelationEnum.DEEP_PLAY, compartmentId, unbindRoleId, unbindRole);
            }
        }
    }

    private static void processTransfer(Element element, int compartmentId) throws Throwable{
        Registry registry = Registry.getRegistry();
        NodeList transferNodes = element.getElementsByTagName("transfer");

        for(int iTransfer=0; iTransfer<transferNodes.getLength(); iTransfer++){
            Node transferNode = transferNodes.item(iTransfer);
            Element transferElement = (Element)transferNode;
            String strFromCoreId = transferElement.getAttribute("fromCoreId");
            String strToCoreId = transferElement.getAttribute("toCoreId");
            String strRoleType = transferElement.getAttribute("roleType");

            int fromCoreId = Integer.parseInt(strFromCoreId.equals("")?"0":strFromCoreId);
            int toCoreId = Integer.parseInt(strToCoreId.equals("")?"0":strToCoreId);

            //prepare transferring process
            if(fromCoreId > 0 && toCoreId >0 && !strRoleType.isEmpty()){
                registry.transfer(compartmentId, fromCoreId, toCoreId, strRoleType);
            }
        }
    }
}
