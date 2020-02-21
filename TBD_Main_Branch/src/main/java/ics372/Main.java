package sample;

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

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        Controller controller = new Controller();
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
        Button disableEnableFreightButton = new Button("Disable");
        disableEnableFreightButton.setDisable(true);
        Button addShipmentButton = new Button("Add");
        addShipmentButton.setDisable(true);
        Button exportToJsonButton = new Button("Export All");
        exportToJsonButton.setDisable(true);
        //combo box
        ComboBox warehouseComboBox = new ComboBox();
        warehouseComboBox.setDisable(true);
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

        //Button handlers
        fileChooserButton.setOnAction(a -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON files", "*.json"),
                    new FileChooser.ExtensionFilter("All files", "*"));
            File file = fileChooser.showOpenDialog(window);
            if(file != null){
                textArea.setText(controller.processJsonInputFile(file.getName()));

                if (warehouseComboBox.getItems().size() > 0) {
                    warehouseComboBox.getSelectionModel().clearSelection();
                    warehouseComboBox.getItems().clear();
                }
                warehouseComboBox.setItems(FXCollections.observableArrayList(controller.getWarehouseList()));
                warehouseComboBox.getSelectionModel().selectFirst();

                //enable the controls
                warehouseComboBox.setDisable(false);
                disableEnableFreightButton.setDisable(false);
                exportToJsonButton.setDisable(false);
                addShipmentButton.setDisable(false);
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

                  if(warehouse.isFreightReceiptEnabled()){
                      disableEnableFreightButton.setText("Disable");
                      addShipmentButton.setDisable(false);
                  }

                  else{
                      disableEnableFreightButton.setText("Enable");
                      addShipmentButton.setDisable(true);
                  }

              }
        });

        exportToJsonButton.setOnAction(a->{
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
            else{
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText(String.format("Cannot access location: %s",fileString));
            }
            alert.show();
        });

        disableEnableFreightButton.setOnAction(a->{
            Warehouse warehouse = controller
                    .getWarehouseList()
                    .stream()
                    .filter(w -> w.getWarehouseId().equals(((Warehouse) warehouseComboBox.getValue()).getWarehouseId()))
                    .findFirst()
                    .get();

            if(warehouse.isFreightReceiptEnabled()) {
                warehouse.disableFreightReceipt();
                disableEnableFreightButton.setText("Enable");
                addShipmentButton.setDisable(true);
            } else {
                warehouse.enableFreightReceipt();
                disableEnableFreightButton.setText("Disable");
                addShipmentButton.setDisable(false);
            }
        });

        printAllWarehouseShipmentsButton.setOnAction(a -> {
            textArea.setText(controller.printAllWarehousesWithShipments());
            textArea.setScrollTop(Double.MAX_VALUE);
        });

        addShipmentButton.setOnAction(a -> {
            Stage window = new AddShipmentForm((Warehouse)warehouseComboBox.getValue());
            window.show();
        });

        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });

        closeButton.setOnAction(e -> closeProgram());
    }

    public void closeProgram(){
        window.close();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
