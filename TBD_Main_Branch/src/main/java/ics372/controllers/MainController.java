package ics372.controllers;

import ics372.model.Shipment;
import ics372.dto.ShipmentsWrapper;
import ics372.model.Warehouse;
import ics372.services.DataService;
import ics372.services.GsonService;

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

    public String processJsonInputFile(String file){
        Collection<Shipment> shipmentList = new ArrayList<>();
        String msg = "";

        shipmentList.addAll(gson.processJsonInputFile(file));


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
             * if the freight receipt in the warehouse is disabled, do not add any shipments
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
     * save the current state of application
     */
    public void saveCurrentState() {
        data.saveCurrentState(warehouseList, dataDirectory);
    }

    /**
     * retrieve the current state of application
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
