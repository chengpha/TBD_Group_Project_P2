import ics372.dto.ShipmentsWrapper;
import ics372.model.Shipment;
import ics372.services.GsonService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GsonServiceTests {
    private GsonService stu;
    private List<Shipment> shipments;

    @BeforeEach
    public void initGsonServiceTests(){
        stu = new GsonService();
        shipments = new ArrayList<>();
        shipments.add(new Shipment("1111", "Warehouse 120", "id_1111","air", 87, 134234234232L));
        shipments.add(new Shipment("2222", "Warehouse 121", "id_2222", "truck", 87, 1231231123123L));
    }

    @Test
    public void processInputFile_Test() throws Exception {
        /**
         * Act
         */
        String content = "{\n" +
                "  \"warehouse_contents\": [\n" +
                "    {\n" +
                "      \"warehouse_id\": \"11111\",\n" +
                "      \"shipment_method\": \"air\",\n" +
                "      \"shipment_id\": \"48934j\",\n" +
                "      \"weight\": 84,\n" +
                "      \"receipt_date\": 1515354694451\n" +
                "    },\n" +
                "    {\n" +
                "      \"warehouse_id\": \"22222\",\n" +
                "      \"shipment_method\": \"truck\",\n" +
                "      \"shipment_id\": \"1adf4\",\n" +
                "      \"weight\": 354,\n" +
                "      \"receipt_date\": 1515354694451\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        String file = System.getProperty("user.dir")+"test.json";

        try {
            FileUtils.writeStringToFile(new File(file), content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> shipmentIds = new ArrayList<String>(){{
            add("11111");
            add("22222");
        }};
        shipments = (List<Shipment>) stu.processInputFile(file);
        shipments.forEach(s -> shipmentIds.add(s.getShipmentId()));

        /**
         * Assert
         */
        assertNotNull(shipments);
        assertEquals(2, shipments.size());
        assertTrue(shipmentIds.contains(shipmentIds.get(0)));
        assertTrue(shipmentIds.contains(shipmentIds.get(1)));
    }

    @Test
    public void exportShipmentsToJsonString_Test(){
        /**
         * Act
         */
        String str = stu.exportShipmentsToJsonString(new ShipmentsWrapper(shipments));
        /**
         * Assert
         */
        assertTrue(str.contains("1111"));
        assertTrue(str.contains("2222"));
    }

    @Test
    public void exportShipmentsToJsonFile_Test(){
        /**
         * Act
         */
        String location = System.getProperty("user.dir");
        String fileString = MessageFormat.format("{0}/{1}_{2}.json",
                location,
                "12345",
                new Date().getTime());
        stu.exportShipmentsToJsonFile(location, fileString, new ShipmentsWrapper(shipments));
        File testFile = new File(fileString);

        /**
         * Assert
         */
        assertTrue(testFile.exists());

        /* cleanup */
        if(testFile.exists())
            testFile.delete();
    }
}
