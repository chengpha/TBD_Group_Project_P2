package ics372.services;

import com.google.gson.Gson;
import ics372.model.Shipment;
import ics372.dto.ShipmentsWrapper;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Gson controller implements Gson library to perform JSON read/write file operations across the application.
 */
public class GsonService {
    public Collection<Shipment>  processJsonInputFile(String file) {
        Collection<Shipment> list = new ArrayList<Shipment>();

        try (Reader reader = new FileReader(file)) {
            list = new Gson().fromJson(reader, ShipmentsWrapper.class).getShipmentList();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String exportShipmentsToJsonString(Object o){
        return new Gson().toJson(o);
    }

    public boolean exportShipmentsToJsonFile(String location, String fileString, Object o){
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
