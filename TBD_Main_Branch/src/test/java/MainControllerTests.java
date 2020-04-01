import ics372.controllers.MainController;
import ics372.dto.ShipmentsWrapper;
import ics372.model.Shipment;
import ics372.model.Warehouse;
import ics372.services.DataService;
import ics372.services.GsonService;
import ics372.services.XmlService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.apache.commons.io.FileUtils.cleanDirectory;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MainControllerTests {

    @Mock
    GsonService gsonService;
    @Mock
    XmlService xmlService;
    @Mock
    DataService dataService;


    private String dataDirectory;
    private Warehouse warehouse1 = null;
    private Warehouse warehouse2 = null;

    @BeforeEach
    public void init() {
        /**
         * The files in the data directory must be removed prior to running the tests.
         * Otherwise they might interfere with other tests.
         */
        dataDirectory = System.getProperty("user.dir") + "/data/";
        try {
            cleanDirectory(new File(dataDirectory));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * Arrange
         */
        Shipment shipment1 = new Shipment("1111", "Warehouse 120", "id_1111","air", 87, 134234234232L);
        Shipment shipment2 = new Shipment("2222", "Warehouse 121", "id_2222", "truck", 87, 1231231123123L);
        List<Shipment> shipments = new ArrayList<Shipment>(){{
            add(shipment1);
            add(shipment2);
        }};
        Mockito.lenient().when(gsonService.processInputFile(any(String.class))).thenReturn(shipments);
    }

    /**
     * Test the ability of the MainController to create warehouses out of a list of shipments
     */

    @Test
    public void createWarehousesWithShipments_Test(){
        /**
         * Act
         */
        MainController mainController = new MainController(dataService, gsonService, xmlService);
        mainController.processInputFile("");
        for (Warehouse w : mainController.getWarehouseList()) {
            if(w.getWarehouseId().endsWith("1111"))
                warehouse1 = w;
            else
                warehouse2 = w;
        }

        /**
         * Assert
         *    Since only two shipments with two different warehouse are being passed, the size of
         *    the returned array is expected to be 2
         */
        assertEquals(2, mainController.getWarehouseList().size());
        /**
         * verify data in warehouse 1
         */
        assertNotNull(warehouse1);
        assertAll("warehouse 1111",
                () -> assertEquals("1111", warehouse1.getWarehouseId()),
                () -> assertEquals(1 , warehouse1.getShipments().size()),
                () -> {
                    List<Shipment> shipments = (List<Shipment>) warehouse1.getShipments();
                    assertEquals("id_1111", shipments.get(0).getShipmentId());
                }
        );
        /**
         * verify data in warehouse 2
         */
        assertNotNull(warehouse2);
        assertAll("warehouse 2222",
                () -> assertEquals("2222", warehouse2.getWarehouseId()),
                () -> assertEquals(1 , warehouse2.getShipments().size()),
                () -> {
                    List<Shipment> shipments = (List<Shipment>) warehouse2.getShipments();
                    assertEquals("id_2222", shipments.get(0).getShipmentId());
                }
        );

        verify(gsonService, times(1)).processInputFile("");
    }

    @Test
    public void printAllWarehousesWithShipments_Test(){
        /**
         * Act
         */
         MainController mainController = new MainController(dataService, gsonService, xmlService);
         mainController.processInputFile("");

        mainController.getWarehouseList()
                .forEach(w ->  Mockito.lenient().when(gsonService.exportShipmentsToJsonString(any(ShipmentsWrapper.class)))
                .thenReturn(String.format("Warehouse ID - %s:", w.getWarehouseId())));

         String msg = mainController.printAllWarehousesWithShipments();
        /**
         * Assert
         */
         assertTrue(msg.contains("Warehouse ID - 1111:"));
         assertTrue(msg.contains("Warehouse ID - 2222:"));
         verify(gsonService, times(2)).exportShipmentsToJsonString(any(ShipmentsWrapper.class));
    }

    /**
     * Clean up after the test.
     */
    @AfterEach
    public void FileCleanUp() {
        try {
            cleanDirectory(new File(dataDirectory));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
