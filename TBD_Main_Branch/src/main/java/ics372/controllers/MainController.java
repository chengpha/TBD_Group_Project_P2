package ics372.controllers;

import ics372.model.Shipment;
import ics372.dto.ShipmentsWrapper;
import ics372.model.Warehouse;
import ics372.services.DataService;
import ics372.services.GsonService;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * MainController provides the main features and functionality of the application.
 */
public class MainController {
    private final String dataDirectory = System.getProperty("user.dir")+"/data/";
    private List<Warehouse> warehouseList;
    private DataService data;
    private GsonService gson;

    public MainController(DataService dataService, GsonService gsonService){
        data = dataService;
        gson = gsonService;
        this.warehouseList = retrieveCurrentState();
    }

    public List<Warehouse>  getWarehouseList(){ return warehouseList;}

    public String processInputFile(String file){
        Collection<Shipment> shipmentList = new ArrayList<>();
        String msg = "";

        File f = new File(file);
        String ext = FilenameUtils.getExtension(f.getName());

        // Checks if the file has the xml ending, if it does parse it, otherwise assume its a json file.
        if(ext.equals("xml")) {
            try {
                // Turns the xml into usable data docs.
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(f);
                doc.getDocumentElement().normalize();

                // Gets all the shipment lists and puts them into a nodeList.
                NodeList shipmentNodeList = doc.getElementsByTagName("Shipment");

                // Loop through each shipment in the nodeList.
                for (int index = 0; index < shipmentNodeList.getLength(); index++) {
                    Node shipment = shipmentNodeList.item(index);

                    if (shipment.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) shipment;

                        // Gets all the values needed.
                        String warehouseID = ((Element) (shipment.getParentNode())).getAttribute("id"); // checks the shipment node parent, aka warehouse, and finds its ID
                        String shipmentID =  eElement.getAttribute("id"); // grabs the id attribute
                        String shipmentMethod = eElement.getAttribute("type"); // grabs the type attribute, aka air, rail, truck, etc.
                        Double weight = Double.parseDouble(doc.getElementsByTagName("Weight").item(index).getTextContent()); // gets the weight by index
                        Long receiptDate = Long.parseLong(doc.getElementsByTagName("ReceiptDate").item(index).getTextContent()); // gets the receipt date by index

                        // Creates a shipment and adds it to the list.
                        Shipment s = new Shipment(warehouseID, shipmentID, shipmentMethod, weight, receiptDate);
                        shipmentList.add(s);
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
            }

        } else {
            shipmentList.addAll(gson.processInputFile(file));
        }


        /**
         *  Create warehouses if they do not exist; add shipments to warehouses;
         *  Duplicate shipments are not allowed;
         */
        for (Shipment s : shipmentList) {
            Warehouse warehouse;
            if (warehouseList.stream().noneMatch(w -> w.getWarehouseId().equals(s.getWarehouseId()))) {
                warehouse = new Warehouse(s.getWarehouseId());
                warehouseList.add(warehouse);
            } else
                warehouse = warehouseList
                        .stream()
                        .filter(w -> w.getWarehouseId().equals(s.getWarehouseId()))
                        .findFirst()
                        .get();

            /**
             * If the freight receipt in the warehouse is disabled, do not add any shipments.
             */
            if(!warehouse.isFreightReceiptEnabled()){
                msg += String.format("Freight receipt is disabled for warehouse %s.Shipment %s won't be added.%n",
                                        warehouse.getWarehouseId(),
                                        s.getShipmentId());
                continue;
            }

            if(warehouse.addShipment(s))
                msg += String.format("Shipment %s has been added to warehouse %s.%n",
                        s.getShipmentId(),
                        warehouse.getWarehouseId());
            else
                msg += String.format(
                        "Duplicate shipment ID: %s for warehouse: %s. Shipment won't be added.%n",
                        s.getShipmentId(),
                        warehouse.getWarehouseId());
        }

        return  String.format("%s has been imported.\n%s", file, msg);
    }

    public boolean exportToJson(String location, String fileString, Object o){
        return gson.exportShipmentsToJsonFile(location, fileString, o);
    }

    /**
     * Save the current state of application.
     */
    public void saveCurrentState() {
        data.saveCurrentState(warehouseList, dataDirectory);
    }

    /**
     * Retrieve the current state of application.
     * @return
     */
    public List<Warehouse> retrieveCurrentState(){
        return data.retrieveCurrentState(dataDirectory);
    }

    public String printAllWarehousesWithShipments(){
        String msg = String.format("SHIPMENTS FOR ALL WAREHOUSES:%n");
        for (Warehouse w : warehouseList){
            String[] shipments = gson.exportShipmentsToJsonString(new ShipmentsWrapper(w.getShipments())).split("},");
            String temp = "";
            for(int i = 0; i<shipments.length; i++)
                temp += String.format("%s%n\t\t\t\t\t\t\t\t  ", shipments[i] + (i < shipments.length - 1 ? "}," : ""));
            msg += String.format("Warehouse ID - " + w.getWarehouseId()+":%n\t\t"+ temp +"%n");
        }
        return msg;
    }
}
