import ics372.model.Warehouse;
import ics372.services.DataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.apache.commons.io.FileUtils.cleanDirectory;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataServiceTests {

    private DataService dataService;
    private String directoryPath;
    private List<Warehouse> warehouseList;

    @BeforeEach
    public void initDataServiceTest() {
        /**
         * The files in the data directory must be removed prior to performing the tests.
         * Otherwise they might interfere with other tests.
         */
        directoryPath = System.getProperty("user.dir") + "/data/";
        File dataDirectory = new File(directoryPath);
        if(!dataDirectory.exists())
            dataDirectory.mkdir();
        else {
            try {
                cleanDirectory(new File(directoryPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataService = new DataService();
        //Arrange some data
        warehouseList = new ArrayList<Warehouse>() {
            {
                add(new Warehouse("12513", "Warehouse 12513"));
                add(new Warehouse("15566", "Warehouse 15566"));
            }
        };
    }

    /**
     *   Test the ability of DataService to save and retrieve the warehouse data
     */
    @Test
    public void saveAndRetrieveCurrentState_Test() {
        /**
         *  Act: save the contents of the array first, then retrieve it.
         */
        dataService.saveCurrentState(warehouseList, directoryPath);
        List<Warehouse> stu = dataService.retrieveCurrentState(directoryPath);
        List<String> wareHouseIds = new ArrayList<>();
        warehouseList.forEach(w -> wareHouseIds.add(w.getWarehouseId()));
        /**
         *  Assert the results
         */
        assertNotNull(stu);
        assertEquals(warehouseList.size(),stu.size());
        /**
         *  check if all the warehouses exist in the array
         */
        assertTrue(wareHouseIds.contains(stu.get(0).getWarehouseId()));
        assertTrue(wareHouseIds.contains(stu.get(1).getWarehouseId()));
    }

    /**
     * Clean the files up after the test.
     */
    @AfterEach
    public void FileCleanUp() {
        try {
            cleanDirectory(new File(directoryPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
