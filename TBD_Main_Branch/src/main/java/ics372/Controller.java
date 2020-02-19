package ics372;

import com.google.gson.Gson;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class manages reading a JSON file and creating warehouses based on shipments.
 * Allows to retrieve the warehouse list and printing the list.
 */
public class Controller {
    private Gson gson = new Gson();
    private List<Warehouse> warehouseList = new ArrayList<>();

    public Controller() {
    }

    public List<Warehouse> getWarehouseList() {
        return warehouseList;
    }

    public String processJsonInputFile(File f) {
        List<Shipment> shipmentList = new ArrayList<>();
        String msg = "";

        try (Reader reader = new FileReader(String.valueOf(f))) {
            List<Shipment> list = gson.fromJson(reader, Shipments.class).getShipmentList();
            shipmentList.addAll(list);
        } catch (FileNotFoundException e) {
            msg = "File not found";
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            if (!warehouse.isFreightReceiptEnabled()) {
                msg += String.format("Freight receipt is disabled for warehouse %s.%nShipment %s won't be added.%n",
                        warehouse.getWarehouseId(),
                        s.getShipmentId());
                continue;
            }

            if (warehouse.addShipment(s))
                msg += String.format("Shipment %s has been added to warehouse %s.%n",
                        s.getShipmentId(),
                        warehouse.getWarehouseId());
            else
                msg += String.format(
                        "Duplicate shipment ID: %s for warehouse: %s.%nShipment won't be added.%n",
                        s.getShipmentId(),
                        warehouse.getWarehouseId());
        }


        String fileHasBeenRead = f.getName()+ " has been imported.\n";
        return fileHasBeenRead + msg;
    }

    public String printAllWarehousesWithShipments() {
        String msg = String.format("SHIPMENTS FOR ALL WAREHOUSES:%n");
        for (Warehouse w : warehouseList) {
            String[] shipments = w.exportAllShipmentsToJsonString().split("},");
            String temp = "";
            for (int i = 0; i < shipments.length; i++) {
                temp += String.format("%s%n\t\t\t\t\t\t\t\t  ", shipments[i] + (i < shipments.length - 1 ? "}," : ""));
            }
            msg += String.format("Warehouse ID - " + w.getWarehouseId() + ":%n\t\t" + temp + "%n");
        }
        return msg;
    }
}
