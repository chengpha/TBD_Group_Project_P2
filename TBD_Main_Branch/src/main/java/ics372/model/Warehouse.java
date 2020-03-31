package ics372.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Warehouse implements Serializable {
    private String warehouse_id;
    private String warehouse_name;
    private Collection<Shipment> shipments = new ArrayList<Shipment>();
    boolean freightReceiptEnable = true;

    public Warehouse(String warehouse_id, String warehouse_name){
        this.warehouse_id = warehouse_id;
        this.warehouse_name = warehouse_name;
    }

    public boolean addShipment(Shipment shipment){
        if (freightReceiptEnable)
            if (shipments.stream().noneMatch(s -> s.getShipmentId().equals(shipment.getShipmentId()))) {
                shipments.add(shipment);
                return true;
            }
        return false;
    }

    public String getWarehouseId (){ return warehouse_id;}

    public String getWarehouseName(){ return warehouse_name;}

    public void enableFreightReceipt(){
        freightReceiptEnable = true;
    }

    public void disableFreightReceipt(){
        freightReceiptEnable = false;
    }

    public boolean isFreightReceiptEnabled(){ return freightReceiptEnable; }

    public Collection<Shipment> getShipments(){ return shipments;}

    @Override
    public String toString(){ return warehouse_id;}
}
