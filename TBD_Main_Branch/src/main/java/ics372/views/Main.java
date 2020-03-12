package ics372.views;

import ics372.dto.ShipmentsWrapper;
import ics372.model.Warehouse;
import ics372.controllers.MainController;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.text.MessageFormat;
import java.util.Date;


public class Main extends Application {

    Stage window;
    MainController controller = new MainController();
    ComboBox warehouseComboBox;
    Button addShipmentButton;
    Button disableEnableFreightButton;

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("GroupProject1");
        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(8);
        root.setPadding(new Insets(5));

        ColumnConstraints colConstraint1 = new ColumnConstraints();
        colConstraint1.setHgrow(Priority.NEVER);

        ColumnConstraints colConstraint2 = new ColumnConstraints();
        colConstraint2.setHgrow(Priority.ALWAYS);

        root.getColumnConstraints().addAll(colConstraint1, colConstraint2);

        RowConstraints rowConstraint1 = new RowConstraints();
        rowConstraint1.setVgrow(Priority.NEVER);

        RowConstraints rowConstraint2 = new RowConstraints();
        rowConstraint2.setVgrow(Priority.ALWAYS);

        root.getRowConstraints().addAll(rowConstraint1
                                        ,rowConstraint1
                                        ,rowConstraint1
                                        ,rowConstraint1
                                        ,rowConstraint1
                                        ,rowConstraint2);

        //buttons
        Button fileChooserButton = new Button("Choose File");
        Button printAllWarehouseShipmentsButton = new Button("Display All Shipments");
        Button closeButton = new Button("Close");
        Button exportToJsonButton = new Button("Export All");
        disableEnableFreightButton = new Button("Disable");
        addShipmentButton = new Button("Add");
        warehouseComboBox = new ComboBox();

        //text area
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setScrollTop(Double.MAX_VALUE);

        //labels
        Label warehouseLabel = new Label("Select Warehouse: ");
        Label disableLabel = new Label("Disable/Enable Freight: ");
        Label addShipmentLabel = new Label("Add Shipment: ");
        Label exportLabel = new Label("Export All Shipments To Json File: ");

        GridPane.setHalignment(printAllWarehouseShipmentsButton, HPos.RIGHT);

        root.add(fileChooserButton, 0, 0);
        root.add(warehouseLabel, 0, 1);
        root.add(disableLabel,0,2);
        root.add(addShipmentLabel,0,3);
        root.add(exportLabel,0,4);
        root.add(warehouseComboBox, 1, 1);
        root.add(disableEnableFreightButton,1,2);
        root.add(addShipmentButton,1,3);
        root.add(exportToJsonButton,1,4);
        root.add(textArea, 0, 5, 4, 2);
        root.add(printAllWarehouseShipmentsButton, 2, 7);
        root.add(closeButton, 3, 7);
        window.setScene(new Scene(root, 1000, 600));
        window.show();
        onLoad();

        //Button handlers
        fileChooserButton.setOnAction(a -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON files", "*.json"),
                    new FileChooser.ExtensionFilter("All files", "*"));
            File file = fileChooser.showOpenDialog(window);
            if(file != null){
                textArea.setText(controller.processJsonInputFile(file.getAbsolutePath()));
                onLoad();
                textArea.setScrollTop(Double.MAX_VALUE);
            }
        });

        warehouseComboBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue v, Object o, Object n) -> {
              if (warehouseComboBox.getValue() != null) {
                  Warehouse warehouse = controller
                          .getWarehouseList()
                          .stream()
                          .filter(w -> w.getWarehouseId().equals(((Warehouse) warehouseComboBox.getValue()).getWarehouseId()))
                          .findFirst()
                          .get();

                  enableDisableControlsOnFreightReceiptChange(warehouse);
              }
        });

        exportToJsonButton.setOnAction(a->{
            if(controller.getWarehouseList().isEmpty())
                return;
            String location = System.getProperty("user.dir");
            Alert alert = new Alert(Alert.AlertType.NONE);
            Warehouse warehouse = (Warehouse) warehouseComboBox.getValue();
            String fileString = MessageFormat.format("{0}/{1}_{2}.json",
                                                                location,
                                                                warehouse.getWarehouseId(),
                                                                new Date().getTime());
            if (controller.exportToJson(location, fileString, new ShipmentsWrapper(warehouse.getShipments()))) {
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setContentText(String.format("JSON extract %s has been generated for warehouse: %s",fileString,warehouse.getWarehouseId()));
            }
            else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText(String.format("Cannot access location: %s",fileString));
            }
            alert.show();
        });

        disableEnableFreightButton.setOnAction(a->{
            if(controller.getWarehouseList().isEmpty())
                return;

            Warehouse warehouse = controller
                    .getWarehouseList()
                    .stream()
                    .filter(w -> w.getWarehouseId().equals(((Warehouse) warehouseComboBox.getValue()).getWarehouseId()))
                    .findFirst()
                    .get();

            if(warehouse.isFreightReceiptEnabled())
                warehouse.disableFreightReceipt();
            else
                warehouse.enableFreightReceipt();

            enableDisableControlsOnFreightReceiptChange(warehouse);
        });

        printAllWarehouseShipmentsButton.setOnAction(a -> {
            textArea.setText(controller.printAllWarehousesWithShipments());
            textArea.setScrollTop(Double.MAX_VALUE);
        });

        addShipmentButton.setOnAction(a -> {
            if(controller.getWarehouseList().isEmpty())
                return;
            Stage window = new AddShipmentView((Warehouse)warehouseComboBox.getValue());
            window.show();
        });

        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });

        closeButton.setOnAction(e -> closeProgram());
    }
    public void onLoad(){
        if(controller.getWarehouseList().size() > 0){
            warehouseComboBox.setItems(FXCollections.observableArrayList(controller.getWarehouseList()));
            warehouseComboBox.getSelectionModel().selectFirst();
            enableDisableControlsOnFreightReceiptChange((Warehouse)warehouseComboBox.getValue());
        }
    }

    public void enableDisableControlsOnFreightReceiptChange(Warehouse w){
        if(w.isFreightReceiptEnabled()){
            disableEnableFreightButton.setText("Disable");
            addShipmentButton.setDisable(false);
        }
        else{
            disableEnableFreightButton.setText("Enable");
            addShipmentButton.setDisable(true);
        }
    }

    public void closeProgram(){
        window.close();
        controller.saveCurrentState();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}