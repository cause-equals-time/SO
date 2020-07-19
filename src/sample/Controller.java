package sample;

import com.jbetfairng.BetfairClient;
import com.jbetfairng.enums.Exchange;
import com.jbetfairng.exceptions.LoginException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.Scanner;

public class Controller {


    @FXML
    MenuItem menuBoxOptionsItem1;
    @FXML
    AnchorPane loginPane;
    @FXML
    Label labelStatus;

    private CertAppKeyData certificate;

    @FXML
    public void initialize() {
        certificate=new CertAppKeyData();
        try{
            Scanner sc = new Scanner(new FileReader("config"));
            certificate.setCertificate(sc.nextLine());
            certificate.setPassword(sc.nextLine());
            certificate.setAppKey(sc.nextLine());
            sc.close();
            labelStatus.setText("Certificate and password loaded\n" + "AppKey: " + certificate.getAppKey());
        }
        catch(Exception ignored){
            labelStatus.setText("Config file corrupt or not found");
        }
    }

    @FXML
    public void onOptionChosen() {

        certificate=ModalBox.display("Settings",250,300);

        try{
            FileWriter config = new FileWriter("config");
            config.write(certificate.getCertificate() +"\n");
            config.write(certificate.getPassword() +"\n");
            config.write(certificate.getAppKey());
            config.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    Button buttonLogin;
    @FXML
    TextField textFieldUsername;
    @FXML
    PasswordField passFieldPassword;


    @FXML
    public void onLoginClicked(){

        if (certificate.isComplete() && !textFieldUsername.getText().isEmpty() && !passFieldPassword.getText().isEmpty()){
            BetfairClient cl = new BetfairClient(Exchange.UK, certificate.getAppKey());
            boolean loginSuccess =false;
            try {
                cl.login(certificate.getCertificate(), certificate.getPassword(), textFieldUsername.getText(), passFieldPassword.getText());
                loginSuccess =true;
                //cl.keepAlive();
            } catch (LoginException e){
                labelStatus.setText("Login failed");
                e.printStackTrace();
            }
            if (loginSuccess) {
                try {
                    Stage mainStage = (Stage) loginPane.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
                    Scene mainScene = new Scene(loader.load(), 650, 700);
                    ControllerMain controllerMain = loader.getController();
                    controllerMain.passClient(cl);
                    mainStage.setScene(mainScene);
                    mainStage.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
