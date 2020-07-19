package sample;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class MarketBox extends VBox {

    private String marketID;
    private long selectionID;
    private long marketVersion;
    //private PriceHolder backPrices;
    private double lowestPrice;
    private boolean resetLowestPrice;
    private boolean resetLastTraded;
    private int forceUpdateCounter;
    private boolean control;
    private int counterLay;
    private int counterBack;
    private Label marketName;
    private Label lastTraded;
    private Label status;
    private HBox backBox;
    private CheckBox back;
    private Label backOdds;
    private HBox layBox;
    private CheckBox lay;
    private Label layOdds;
    private Label displayCount;
    private Label displayMin;

    //private Label close;

    public MarketBox(String marketName, String marketID, long selectionID) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(2);
        this.setPadding(new Insets(2,2,2,2));
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        /*this.close=new Label("x");
        this.close.setOnMouseClicked(e->{
            ObservableList<Node> boxes = ((GridPane) this.getParent()).getChildren();
            int index = boxes.indexOf(this);
            markets.remove(this);
            listIDs.remove(index);
            boxes.remove(this);
        });*/
        this.marketID=marketID;
        this.selectionID=selectionID;
        //this.backPrices=new PriceHolder();
        this.lowestPrice=0;
        this.resetLowestPrice=true;
        this.resetLastTraded=false;
        this.forceUpdateCounter=0;
        this.control=false;
        this.counterLay=0;
        this.counterBack=0;
        this.marketName = new Label(marketName);
        this.marketName.setFont(new Font( 15));
        this.displayMin=new Label();
        this.displayMin.setFont(Font.font("Verdana", FontWeight.BOLD,16));
        this.displayMin.setTextFill(Color.HOTPINK);
        this.lastTraded = new Label("0");
        this.lastTraded.setFont(Font.font("Verdana", FontWeight.BOLD,16));
        this.lastTraded.setTextFill(Color.ROYALBLUE);
        this.status = new Label();
        this.status.setFont((new Font(9)));
        this.backBox = new HBox(3);
        this.back=new CheckBox("Back");
        this.back.setTextFill(Color.ROYALBLUE);
        this.back.setOnAction(e->{
            if (!this.back.isSelected()){
                this.backOdds.setText("");
            }
        });
        this.backOdds=new Label();
        this.backBox.getChildren().addAll(this.back, this.backOdds);
        this.layBox = new HBox(3);
        this.lay=new CheckBox("Lay  ");
        this.lay.setTextFill(Color.HOTPINK);
        //this.lay.setSelected(true);
        this.lay.setOnAction(e->{
            if (!this.lay.isSelected()) this.layOdds.setText("");
        });
        this.layOdds=new Label();
        this.layBox.getChildren().addAll(this.lay, this.layOdds);

        this.displayCount=new Label();
        this.displayCount.setFont(new Font(8));

        this.getChildren().addAll(this.marketName,this.lastTraded,this.status,this.backBox,this.layBox,this.displayMin,this.displayCount);

    }

    public double getLastTraded() {
        return Double.parseDouble(lastTraded.getText());
    }

    public void setLastTraded(double lastTraded) {
        this.lastTraded.setText(Double.toString(lastTraded));
    }

    public String getStatus() {
        return status.getText();
    }

    public void setStatus(String status) {
        this.status.setText(status);
    }

    public void disable(){
            this.backBox.setDisable(true);
            this.back.setSelected(false);
            this.layBox.setDisable(true);
            this.lay.setSelected(false);

    }

    public boolean isEnabled(){
        return (!this.backBox.isDisable() || !this.layBox.isDisable());
    }

    public String getMarketID() {
        return marketID;
    }

    public String getMarketName() {
        return marketName.getText();
    }

    public long getSelectionID() {
        return selectionID;
    }

    public long getMarketVersion() {
        return marketVersion;
    }

    public void setMarketVersion(long marketVersion) {
        this.marketVersion = marketVersion;
    }

    public double getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(double lowestPrice) {
        this.lowestPrice = lowestPrice;
        this.displayMin.setText(Double.toString(this.lowestPrice));
    }

    public boolean isResetLowestPrice() {
        return resetLowestPrice;
    }

    public void setResetLowestPrice(boolean resetLowestPrice) {
        this.resetLowestPrice = resetLowestPrice;
    }

    public boolean isResetLastTraded() {
        return resetLastTraded;
    }

    public void setResetLastTraded(boolean resetLastTraded) {
        this.resetLastTraded = resetLastTraded;
    }

    public boolean isForceUpdate() {
        if (this.forceUpdateCounter==20) return false;
        else{
            this.forceUpdateCounter++;
            return true;
        }
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdateCounter = forceUpdate ? 0 : 20;
    }

    public void increaseCounterLay(){
        this.counterLay++;
        if (this.counterLay==10){
            this.counterLay=0;
            this.resetLowestPrice=true;
        }
        this.displayCount.setText(Integer.toString(this.counterLay));
    }

    public void resetCounterLay(){
        this.counterLay =0;
        this.displayCount.setText(Integer.toString(this.counterLay));
    }

    public void increaseCounterBack(){
        this.counterBack++;
        if (this.counterBack==5){
            this.counterBack=0;
            this.resetLastTraded=true;
        }
    }

    public void resetCounterBack(){
        this.counterBack=0;
    }

    /*public void updateCount(){
        this.displayCount.setText(Integer.toString(this.counter));
    }

    public void updateMin(){
        this.displayMin.setText(Double.toString(this.lowestPrice));
    }*/

    public boolean isBackSelected(){
        return this.back.isSelected();
    }

    public boolean isLaySelected(){
        return this.lay.isSelected();
    }

    public void unSelectLay(){
        this.lay.setSelected(false);
    }

    public double getBackOdds() {
        return Double.parseDouble(backOdds.getText());
    }

    public void setBackOdds(double backOdds) {
        this.backOdds.setText(Double.toString(backOdds));
    }

    public double getLayOdds() {
        return Double.parseDouble(layOdds.getText());
    }

    public void setLayOdds(double layOdds) {
        this.layOdds.setText(Double.toString(layOdds));
    }

    public boolean isControl(){
        return this.control;
    }

    public void setControl(boolean control){
        this.control=control;
    }

}
