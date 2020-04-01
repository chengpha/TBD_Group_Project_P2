import ics372.model.Shipment;
import ics372.model.Warehouse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WarehouseTests {

    /**
     *  Test to verify that shipments can be added with freight receipt enabled
     */

    @Test
    public void addShipmentFreightEnabled_Test()
    {
        Warehouse warehouse = new Warehouse("11111", "Test1");
        List<Shipment> shipments = new ArrayList<Shipment>();
        shipments.add(new Shipment(warehouse.getWarehouseId(), "Test1", "1234a", "air", 50, 1515354694451L));
        shipments.add(new Shipment(warehouse.getWarehouseId(), "Test1", "1234b", "truck", 155, 1515354694451L));
        warehouse.enableFreightReceipt();

        for(Shipment s : shipments)
        {
            warehouse.addShipment(s);
        }

        assertEquals(2, warehouse.getShipments().size());
        assertTrue(warehouse.getShipments().contains(shipments.get(0)));
        assertTrue(warehouse.getShipments().contains(shipments.get(1)));
    }

    /**
     *  Test to verify that shipments can't be added with freight receipt disabled
     */

    @Test
    public void addShipmentFreightDisabled_Test()
    {
        Warehouse warehouse = new Warehouse("11111", "Test1");
        List<Shipment> shipments = new ArrayList<Shipment>();
        shipments.add(new Shipment(warehouse.getWarehouseId(), "Test1", "1234a", "air", 50, 1515354694451L));
        shipments.add(new Shipment(warehouse.getWarehouseId(), "Test1", "1234b", "truck", 155, 1515354694451L));
        warehouse.disableFreightReceipt();

        for(Shipment s : shipments)
        {
            warehouse.addShipment(s);
        }

        assertEquals(0, warehouse.getShipments().size());
    }

    /**
     * Test to verify that the status of freight receipt is enabled
     */

    @Test
    public void verifyFreightReceiptStatusEnabled_Test()
    {
        Warehouse warehouse = new Warehouse("11111", "Test1");

        warehouse.enableFreightReceipt();

        assertTrue((warehouse.isFreightReceiptEnabled()));
    }

    /**
     * Test to verify that the status of freight receipt is disabled
     */

    @Test
    public void verifyFreightReceiptStatusDisabled_Test()
    {
        Warehouse warehouse = new Warehouse("11111", "Test1");

        warehouse.disableFreightReceipt();

        assertFalse(warehouse.isFreightReceiptEnabled());
    }
}
