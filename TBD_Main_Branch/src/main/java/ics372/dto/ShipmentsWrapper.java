package ics372.dto;

import ics372.model.Shipment;
import java.util.Collection;

/**
 * The class is used as a wrapper class to import/export shipments from/to JSON file.
 */
public class ShipmentsWrapper {
    private Collection<Shipment> warehouse_contents;
    public ShipmentsWrapper(Collection<Shipment> warehouse_contents){
        this.warehouse_contents = warehouse_contents;
    }
    public Collection<Shipment> getShipmentList(){
        return warehouse_contents;
    }
}
