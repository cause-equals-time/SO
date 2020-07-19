package sample;

import com.jbetfairng.BetfairClient;
import com.jbetfairng.entities.*;
import com.jbetfairng.enums.Wallet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.*;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ControllerMain implements Initializable {

    private BetfairClient bfclient;
    private Map<String, MarketLayout> openEvents;

    @FXML
    Label labelFunds;
    @FXML
    Button buttonFetchMatches;

    public void passClient(BetfairClient bfclient) {
        this.bfclient = bfclient;
        this.openEvents = new HashMap<>();
        AccountFundsResponse response = bfclient.getAccountFunds(Wallet.UK).getResponse();
        labelFunds.setText("Funds: " + response.getAvailableToBetBalance());
    }

    @FXML
    TableView <Event> tableViewMatches;
    @FXML
    TableColumn <Event, String> columnName;
    @FXML
    TableColumn <Event, String> columnCountry;
    @FXML
    TableColumn <Event, String> columnDate;

    @FXML public void onFetchClicked(){

        List<EventResult> listE = bfclient.listEvents(Filter.getSoccerFilter()).getResponse();
        ObservableList <Event> listDailyMatches = FXCollections.observableArrayList();
        for (EventResult m:listE){
            listDailyMatches.add(m.getEvent());
        }
        //TableColumn <Event, String> nameColumn = new TableColumn<>("Name");
        //nameColumn.setMinWidth(200);
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        //TableColumn <Event, String> countryColumn = new TableColumn<>("Country");
        //countryColumn.setMinWidth(40);
        columnCountry.setCellValueFactory(new PropertyValueFactory<>("countryCode"));

        //TableColumn <Event, Date> timeColumn = new TableColumn<>("Time");
        //timeColumn.setMinWidth(100);
        columnDate.setCellValueFactory(new PropertyValueFactory<>("openDate"));

        tableViewMatches.setItems(listDailyMatches);
        //tableViewMatches.getColumns().addAll(columnName, countryColumn, timeColumn);
        tableViewMatches.getSelectionModel().getSelectedItem();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    public void loadMarket(){

        Event selectedMatch = tableViewMatches.getSelectionModel().getSelectedItem();
        if (selectedMatch!=null) {
            if (openEvents.containsKey(selectedMatch.getName())) {
                openEvents.get(selectedMatch.getName()).requestFocus();
            } else {
                openEvents.put(selectedMatch.getName(), new MarketLayout());
                openEvents.get(selectedMatch.getName()).display(selectedMatch, bfclient, openEvents);
            }
        }
    }

}
