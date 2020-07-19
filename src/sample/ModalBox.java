package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;

public class ModalBox {

    static CertAppKeyData data;

    public static CertAppKeyData display(String title, double h, double w){

        Stage boxWindow = new Stage();

        boxWindow.initModality(Modality.APPLICATION_MODAL);
        boxWindow.setTitle(title);
        boxWindow.setHeight(h);
        boxWindow.setMaxWidth(w);
        boxWindow.setResizable(false);

        Label certDesc = new Label("Select certificate to load");
        HBox box = new HBox(5);
        TextField path = new TextField();
        Button load = new Button("Select");
        load.setOnAction(e->{
            FileChooser fc = new FileChooser();
            fc.setTitle("Select certificate File");
            path.setText(fc.showOpenDialog(boxWindow).getPath()); //.getAbsolutePath());
        });
        box.getChildren().addAll(path,load);

        Label passDesc = new Label("Certificate password");
        PasswordField passfield = new PasswordField();

        Label appKeyDesc = new Label("App Key");
        TextField appkey = new TextField();

        Button okButton = new Button("OK");
        okButton.setDefaultButton(true);
        okButton.setOnAction(actionEvent -> {
            boxWindow.close();
            data=new CertAppKeyData(path.getText(),passfield.getText(),appkey.getText());
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(certDesc, box, passDesc, passfield, appKeyDesc, appkey, okButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        boxWindow.setScene(scene);
        boxWindow.showAndWait();
        return data;

    }

}
