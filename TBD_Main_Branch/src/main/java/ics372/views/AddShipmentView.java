package ics372.views;

import ics372.model.Shipment;
import ics372.model.Warehouse;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.util.stream.Stream;

/**
 * Allows to add a new shipment to a warehouse.
 */
public class AddShipmentView extends Stage {

    Warehouse warehouse;

    public AddShipmentView(Warehouse warehouse){
        this.warehouse = warehouse;
        setTitle("Add Shipment for Warehouse " + warehouse.getWarehouseId());
        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(8);
        root.setPadding(new Insets(5));

        //labels
        Label shipmentIdLabel = new Label("shipment_id: ");
        Label shipmentMethodLabel = new Label("shipment_method: ");
        Label weightLabel = new Label("weight: ");
        Label receiptDateLabel = new Label("receipt_date: ");

        //input
        TextField shipmentIdText = new TextField();
        shipmentIdText.setMaxWidth(170);
        TextField shipmentMethodText  = new TextField();
        shipmentMethodText.setMaxWidth(170);
        TextField weightText = new TextField();
        weightText.setMaxWidth(170);
        TextField receiptDateText = new TextField();
        receiptDateText.setMaxWidth(170);

        //buttons
        Button addButton = new Button("Add");
        Button cancelButton = new Button("Cancel");

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(10,10,10,60));
        hbox.setSpacing(10);
        hbox.getChildren().addAll(addButton,cancelButton);

        root.add(shipmentIdLabel, 0, 0);
        root.add(shipmentMethodLabel, 0, 1);
        root.add(weightLabel, 0, 2);
        root.add(receiptDateLabel, 0, 3);
        root.add(shipmentIdText, 1, 0);
        root.add(shipmentMethodText, 1, 1);
        root.add(weightText, 1, 2);
        root.add(receiptDateText, 1, 3);
        root.add(hbox, 1, 4);

        setScene(new Scene(root, 310,200));
        show();


        //add a shipment
        addButton.setOnAction(a -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            if(shipmentIdText.getText().trim().isEmpty() | shipmentMethodText.getText().trim().isEmpty() | weightText.getText().trim().isEmpty()| receiptDateText.getText().trim().isEmpty()){
                alert.setContentText("Fill out all required fields");
                alert.show();
                return;
            }
            double weight;
            long receiptDate;
            try{
                weight = Double.parseDouble(weightText.getText());
            }catch(NumberFormatException e){
                alert.setContentText("'weight' must be a numeric value");
                alert.show();
                return;
            }
            try{
                receiptDate = Long.parseLong(receiptDateText.getText());
            } catch(NumberFormatException e){
                alert.setContentText("'receipt_date' must be a numeric value");
                alert.show();
                return;
            }
            /**
             * verify the right shipment method
             */
            if (Stream.of("AIR", "RAIL", "SHIP", "TRUCK")
                    .noneMatch(s -> shipmentMethodText.getText().toUpperCase().trim().equals(s))
            ) {
                alert.setContentText("'shipment_method' must be AIR, RAIL, SHIP, or TRUCK");
                alert.show();
                return;
            }
            Shipment shipment = new Shipment(warehouse.getWarehouseId(),
                    warehouse.getWarehouseName(),
                    shipmentIdText.getText(),
                    shipmentMethodText.getText(),
                    weight,
                    receiptDate
                    );
            warehouse.addShipment(shipment);
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setContentText(String.format("Shipment %s has been added to warehouse %s"
                    ,shipment.getShipmentId()
                    ,warehouse.getWarehouseId()));
            alert.show();
            close();
        });

        //close the form
        cancelButton.setOnAction(a -> {
            close();
        });
    }


}
