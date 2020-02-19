package ics372;

import java.util.List;

/**
 * This class creates a shipment lists, which allows to get the list of shipments in a warehouse.
 */
public class Shipments {
    private List<Shipment> warehouse_contents;

    public Shipments(List<Shipment> warehouse_contents) {
        this.warehouse_contents = warehouse_contents;
    }

    public List<Shipment> getShipmentList() {
        return warehouse_contents;
    }
}
