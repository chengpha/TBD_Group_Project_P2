package sample;

import java.util.Collection;

/**
 * The class is used as a wrapper to extract the list of imported shipments from JSON file
 * and export all shipments for a given warehouse to a JSON file.
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
