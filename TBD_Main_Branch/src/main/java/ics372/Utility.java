package ics372;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Utility class has been added for decoupling purposes. The use of Gson library in the application with all
 * its implementation logic has been moved here. That will allow for an easy replacement of Gson library in
 * the app if there was ever a need to do so in the future. Utility class is also responsible for all the
 * reading/writing file operations.
 */
public class Utility  {
    public static Collection<Shipment>  processJsonInputFile(String file) {
        Collection<Shipment> list = new ArrayList();

        try (Reader reader = new FileReader(file)) {
            list = new Gson().fromJson(reader, ShipmentsWrapper.class).getShipmentList();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String exportShipmentsToJsonString(Object o){
        return new Gson().toJson(o);
    }

    public static boolean exportShipmentsToJsonFile(String location, String fileString, Object o){
        if (Files.exists(Paths.get(location))) {
            try {
                Files.write(Paths.get(fileString), new Gson().toJson(o).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
