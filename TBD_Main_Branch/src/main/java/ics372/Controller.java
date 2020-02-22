package ics372;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Controller {
    private List<Warehouse> warehouseList = new ArrayList<>();
    public Controller(){}

    public List<Warehouse>  getWarehouseList(){ return warehouseList;}

    public String processJsonInputFile(String file){
        Collection<Shipment> shipmentList = new ArrayList<>();
        String msg = "";

        shipmentList.addAll(Utility.processJsonInputFile(file));

        /*
        create warehouses if they do not exist; add shipments to warehouses;
        duplicate shipments are not allowed;
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

            //if the freight receipt in the warehouse is disabled, do not add any shipments
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
        return Utility.exportShipmentsToJsonFile(location, fileString, o);
    }

    public String printAllWarehousesWithShipments(){
        String msg = String.format("SHIPMENTS FOR ALL WAREHOUSES:%n");
        for (Warehouse w : warehouseList){
            String[] shipments = Utility.exportShipmentsToJsonString(new ShipmentsWrapper(w.getShipments())).split("},");
            String temp = "";
            for(int i = 0; i<shipments.length; i++)
                temp += String.format("%s%n\t\t\t\t\t\t\t\t  ", shipments[i] + (i < shipments.length - 1 ? "}," : ""));
            msg += String.format("Warehouse ID - " + w.getWarehouseId()+":%n\t\t"+ temp +"%n");
        }
        return msg;
    }
}
