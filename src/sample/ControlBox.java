package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ControlBox extends AnchorPane {

    private Button placeAll;
    /*private Button placeBacks;
    private Button placeLays;*/
    private Button cancelAllRequests;
    private Button cancelUnmatched;
    private Spinner<Integer> backOffSet;
    private Spinner<Integer> layOffSet;
    //private ChoiceBox<Integer> backStake;
    private ChoiceBox<Integer> stake;
    //private Button refresh;
    private Button kill;
    private CheckBox control;
    private CheckBox liability;
    private CheckBox multiStake;
    private CheckBox stopLoss;
    private CheckBox anchor;
    private int maxLoss;
    private Label fps;

    public ControlBox() {
        this.setPrefSize(144,203);


        //Places both backs and lays on selected markets.
        this.placeAll = new Button("place all");
        placeAll.setPrefSize(134,35);
        placeAll.setLayoutX(5);
        placeAll.setLayoutY(0);


        //Buttons to place either backs or lays on selected markets.

        /*this.placeBacks = new Button("Back");
        placeBacks.setPrefSize(60,25);
        placeBacks.setLayoutX(5);
        placeBacks.setLayoutY(35);

        this.placeLays = new Button("Lay");
        placeLays.setPrefSize(60,25);
        placeLays.setLayoutX(78);
        placeLays.setLayoutY(35);*/



        //Buttons to cancel pending requests and cancel all unmatched bets on the current market.

        this.cancelAllRequests = new Button("#reqs");
        cancelAllRequests.setPrefSize(60,30);
        cancelAllRequests.setLayoutX(5);
        cancelAllRequests.setLayoutY(40);



        this.cancelUnmatched = new Button("#cancel");
        cancelUnmatched.setPrefSize(60,30);
        cancelUnmatched.setLayoutX(78);
        cancelUnmatched.setLayoutY(40);

        //Button that cancels all unmatched bets on all markets
        this.kill = new Button("#kill");
        kill.setPrefSize(60,30);
        kill.setLayoutX(5);
        kill.setLayoutY(75);




        Preferences pref = new Preferences();

        //ChoiceBoxes for choosing the stake
        /*this.backStake = new ChoiceBox<>();
        backStake.setPrefSize(60,25);
        backStake.setLayoutX(152);
        backStake.setLayoutY(65);
        backStake.setBorder(new Border(new BorderStroke(Color.ROYALBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        backStake.getItems().addAll(2,5,10,20,50,100,200);
        backStake.setValue(pref.getBackStake());*/


        this.stake = new ChoiceBox<>();
        stake.setPrefSize(60,30);
        stake.setLayoutX(78);
        stake.setLayoutY(75);
        stake.getItems().addAll(2,5,10,20,50,100,200,500,1000);
        stake.setValue(pref.getStake());

        //Spinners for selecting the desired offsets for backs and lays.

        //offSetBox.setBackground(new Background(new BackgroundFill(Color.HOTPINK, CornerRadii.EMPTY, Insets.EMPTY)));

        this.layOffSet = new Spinner<>();
        layOffSet.setPrefSize(60,25);
        layOffSet.setLayoutX(5);
        layOffSet.setLayoutY(110);
        layOffSet.setBorder(new Border(new BorderStroke(Color.HOTPINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        layOffSet.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100,pref.getLayOffSet()));

        this.backOffSet = new Spinner<>();
        backOffSet.setPrefSize(60,25);
        backOffSet.setLayoutX(78);
        backOffSet.setLayoutY(110);
        backOffSet.setBorder(new Border(new BorderStroke(Color.ROYALBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        backOffSet.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,200,pref.getBackOffSet()));


        //Button to refresh minimums
        /*this.refresh = new Button("Refresh");
        refresh.setPrefSize(60,25);
        refresh.setLayoutX(152);
        refresh.setLayoutY(5);*/

        //Checkbox and

        this.control = new CheckBox("control");
        control.setLayoutX(5);
        control.setLayoutY(143);
        control.setFont(new Font(9));
        control.setSelected(pref.isControl());

        this.liability = new CheckBox("liability");
        liability.setLayoutX(5);
        liability.setLayoutY(158);
        liability.setFont(new Font(9));
        liability.setSelected(pref.isLiability());

        this.anchor = new CheckBox("anchor");
        anchor.setLayoutX(5);
        anchor.setLayoutY(171);
        anchor.setFont(new Font(9));
        anchor.setSelected(pref.isAnchor());

        this.multiStake = new CheckBox(("multistake"));
        multiStake.setLayoutX(78);
        multiStake.setLayoutY(143);
        multiStake.setFont(new Font(9));
        multiStake.setSelected(pref.isMultiStake());

        //Spinner and Checkbox for stoploss options
        this.stopLoss = new CheckBox("stoploss");
        stopLoss.setLayoutX(78);
        stopLoss.setLayoutY(158);
        stopLoss.setFont(new Font(9));
        stopLoss.setSelected(pref.isStopLoss());

        this.maxLoss=pref.getLoss();


        //Label that shows the refresh rate
        this.fps=new Label();
        fps.setLayoutX(78);
        fps.setLayoutY(171);
        fps.setFont(new Font(9));


        this.getChildren().addAll(placeAll,cancelAllRequests,cancelUnmatched,backOffSet,layOffSet,stake,kill,control,liability,multiStake,stopLoss,anchor,fps);

    }

    public void setPlaceAllAction(EventHandler<ActionEvent> e){
        this.placeAll.setOnAction(e);
    }
    /*public void setPlaceBacksAction(EventHandler<ActionEvent> e){
        this.placeBacks.setOnAction(e);
    }
    public void setPlaceLaysAction(EventHandler<ActionEvent> e){
        this.placeLays.setOnAction(e);
    }*/
    public void setCancelAllRequestsAction(EventHandler<ActionEvent> e){
        this.cancelAllRequests.setOnAction(e);
    }
    public void setCancelUnmatchedAction(EventHandler<ActionEvent> e){
        this.cancelUnmatched.setOnAction(e);
    }
    /*public void setRefreshAction(EventHandler<ActionEvent> e){
        this.refresh.setOnAction(e);
    }*/
    public void setKillAction(EventHandler<ActionEvent> e){
        this.kill.setOnAction(e);
    }

    public void setAnchorAction(EventHandler<ActionEvent> e){
        this.anchor.setOnAction(e);
    }

    public double getStake(){
        return this.stake.getValue();
    }
    public int getBackOffset(){
        return this.backOffSet.getValue();
    }
    public int getLayOffset(){
        return this.layOffSet.getValue();
    }
    public boolean isControlSelected(){
        return this.control.isSelected();
    }
    public boolean isLiabilitySelected(){
        return this.liability.isSelected();
    }
    public boolean isMultiStakeSelected(){
        return this.multiStake.isSelected();
    }
    public boolean isStopLossSelected(){
        return this.stopLoss.isSelected();
    }
    public boolean isAnchorSelected(){
        return this.anchor.isSelected();
    }
    public int getMaxLoss(){
        return this.maxLoss;
    }
    public void updateFps(long fps){
        this.fps.setText(fps + " ms");
    }

}
