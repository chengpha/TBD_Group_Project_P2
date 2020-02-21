package sample;

public class Shipment {
    private String warehouse_id;
    private String shipment_id;
    private String shipment_method;
    private double weight;
    private long receipt_date;

    public Shipment(String warehouse_id, String shipment_id, String shipment_method, double weight, long receipt_date){
        this.warehouse_id = warehouse_id;
        this.shipment_id = shipment_id;
        this.shipment_method = shipment_method;
        this.weight = weight;
        this.receipt_date = receipt_date;
    }

    //getters
    public String getShipmentId(){ return shipment_id; }

    public String getWarehouseId(){ return warehouse_id; }
}
