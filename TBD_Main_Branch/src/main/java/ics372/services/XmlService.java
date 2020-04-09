package ics372.services;

import ics372.interfaces.IFileService;
import ics372.model.Shipment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Collection;

public class XmlService implements IFileService {
    public Collection<Shipment> processInputFile(String file){
        Collection<Shipment> list = new ArrayList<>();
        try {
            // turns the xml into usable data docs
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            // Gets all the shipment lists and puts them into a nodelist
            NodeList shipmentNodeList = doc.getElementsByTagName("Shipment");

            // Loop through each shipment in the nodelist
            for (int index = 0; index < shipmentNodeList.getLength(); index++) {
                Node shipment = shipmentNodeList.item(index);

                if (shipment.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) shipment;

                    // Gets all the values needed
                    String warehouseID = ((Element) (shipment.getParentNode())).getAttribute("id"); // checks the shipment node parent, aka warehouse, and finds its ID
                    String warehouseName = ((Element) (shipment.getParentNode())).getAttribute("name"); // checks the shipment node parent, aka warehouse, and finds its name
                    String shipmentID =  eElement.getAttribute("id"); // grabs the id attribute
                    String shipmentMethod = eElement.getAttribute("type"); // grabs the type attribute, aka air, rail, truck, etc.
                    Double weight = Double.parseDouble(doc.getElementsByTagName("Weight").item(index).getTextContent()); // gets the weight by index
                    Long receiptDate = Long.parseLong(doc.getElementsByTagName("ReceiptDate").item(index).getTextContent()); // gets the receipt date by index

                    // Creates a shipment and adds it to the list
                    Shipment s = new Shipment(warehouseID, warehouseName, shipmentID, shipmentMethod, weight, receiptDate);
                    list.add(s);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
