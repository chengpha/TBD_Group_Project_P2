package ics372.services;

import ics372.model.Warehouse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class DataService {
    /**
     * save the current state of application
     * @param warehouseList
     * @param filePath
     */
    public void saveCurrentState(List<Warehouse> warehouseList, String filePath){
        try {
            for(Warehouse w : warehouseList) {
                FileOutputStream file = new FileOutputStream(filePath + w.getWarehouseId());
                ObjectOutputStream out = new ObjectOutputStream(file);
                out.writeObject(w);
                out.close();
                file.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * retrieve the current state of application
     * @param filePath
     * @return
     */
    public List<Warehouse> retrieveCurrentState(String filePath){
        List<Warehouse> warehouseList = new ArrayList<>();
        try {
            File[] files = new File(filePath).listFiles();
            for (File file : files) {
                //Mac computers contain .DS_Store files in the newly created directories that may cause problems
                if(file.getName().contains(".DS_Store")) continue;

                FileInputStream fileInput = new FileInputStream(file.getAbsoluteFile());
                ObjectInputStream in = new ObjectInputStream(fileInput);
                warehouseList.add((Warehouse)in.readObject());
                in.close();
                fileInput.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return warehouseList;
    }
}
