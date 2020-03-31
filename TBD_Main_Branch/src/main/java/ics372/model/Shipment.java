package ics372.model;

import java.io.Serializable;

public class Shipment implements Serializable {
    private String warehouse_id;
    private String warehouse_name;
    private String shipment_id;
    private String shipment_method;
    private double weight;
    private long receipt_date;

    public Shipment(String warehouse_id, String warehouse_name, String shipment_id, String shipment_method, double weight, long receipt_date){
        this.warehouse_id = warehouse_id;
        this.warehouse_name = warehouse_name;
        this.shipment_id = shipment_id;
        this.shipment_method = shipment_method;
        this.weight = weight;
        this.receipt_date = receipt_date;
    }

    //getters
    public String getShipmentId(){ return shipment_id; }

    public String getWarehouseId(){ return warehouse_id; }

    public String getWarehouseName(){return warehouse_name;}
}
