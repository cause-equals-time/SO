package sample;

import com.jbetfairng.BetfairClient;
import com.jbetfairng.entities.*;
import com.jbetfairng.entities.Event;
import com.jbetfairng.enums.*;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.*;
import java.util.List;

public class MarketLayout extends Stage {

    List <MarketBox> markets;
    List <String> listMarketIDs;
    List <ScheduledService<Void>> listServices;
    int fhMarkets;
    ControlBox controls;
    Map <String,List<String>> controlMarketsBetIDs;


    public void display(Event match, BetfairClient bfclient, Map<String, MarketLayout> openEvents){

        this.markets = new ArrayList<>();
        this.listMarketIDs = new ArrayList<>();
        this.listServices = new ArrayList<>();
        this.fhMarkets = 0;
        this.controlMarketsBetIDs = new HashMap<>();

        this.setTitle(match.getName());
        this.initStyle(StageStyle.UTILITY);

        HBox box = new HBox(2);
        box.setAlignment(Pos.CENTER);

        this.controls = new ControlBox();
        controls.setPlaceAllAction(e->placeBets(bfclient,true,true));
        /*controls.setPlaceBacksAction(e->placeBets(bfclient,true,false));
        controls.setPlaceLaysAction(e->placeBets(bfclient,false,true));*/

        controls.setCancelUnmatchedAction(e->{

            for (String m : listMarketIDs){
                if (controlMarketsBetIDs.containsKey(m)){
                    if (!controlMarketsBetIDs.get(m).isEmpty()) {
                        List<CancelInstruction> cancelIns = new ArrayList<>();
                        for (String bet : controlMarketsBetIDs.get(m)) {
                            CancelInstruction ins = new CancelInstruction();
                            ins.setBetId(bet);
                            cancelIns.add(ins);
                        }
                        new Thread(() -> bfclient.cancelOrders(m, cancelIns, null)).start();
                    }
                } else new Thread(()-> bfclient.cancelOrders(m, null, null)).start();
            }
            controlMarketsBetIDs.clear();
        });

        controls.setCancelAllRequestsAction(e->voidAllRequests());

        /*controls.setRefreshAction(e->{
            for (MarketBox m : markets){
                m.setResetLowestPrice(true);
            }
        });*/

        controls.setKillAction(e-> new Thread(()-> bfclient.cancelOrders(null, null, null)).start());

        controls.setAnchorAction(e->this.setAlwaysOnTop(!this.isAlwaysOnTop()));

        this.setAlwaysOnTop(controls.isAnchorSelected());

        //Layout for the markets of selected match
        GridPane layout = new GridPane();
        layout.setHgap(5);
        layout.setVgap(5);
        //layout.setGridLinesVisible(true);


        //List with MarketCatalogues of selected match, sorted by name, using comparator
        List<MarketCatalogue> listOUMarkets = bfclient.listMarketCatalogue(Filter.getOUFilter(Collections.singleton(match.getId())),Set.of(MarketProjection.RUNNER_DESCRIPTION), null,15).getResponse();
        listOUMarkets.sort(new Comparator<MarketCatalogue>() {
            @Override
            public int compare(MarketCatalogue o1, MarketCatalogue o2) {
                return o1.getMarketName().compareTo(o2.getMarketName());
            }
        });

        //List with selected match's markets' IDs
        //Marketboxes are created for each market and added to the layout
        int i = 0;
        for (MarketCatalogue catalogue : listOUMarkets){
            String marketName;
            if (catalogue.getMarketName().contains("F")){
                fhMarkets++;
                marketName=catalogue.getMarketName().replace("First Half Goals", "HT Under");
            } else marketName=catalogue.getMarketName().replace("Over/", "FT ").replace(" Goals", "");

            listMarketIDs.add(catalogue.getMarketId());
            markets.add(new MarketBox(marketName,catalogue.getMarketId(),catalogue.getRunners().get(0).getSelectionId()));
            layout.add(markets.get(i),i,0);
            i++;
        }

        //Disables the limit markets
        if (!markets.isEmpty()) {
            markets.get(0).disable();
            if (fhMarkets != 0) markets.get(fhMarkets).disable();
        }

        //Establishes the markets at which control stakes will be placed. Size>2 avoids exception when trying to open a match that has only 2 active markets, this fhmarkets+2 would be out of bounds.
        if (markets.size()>2) markets.get(fhMarkets+2).setControl(true);

        //half time control
        /*if (fhMarkets==3){
            markets.get(2).setControl(true);
        }*/

        for (MarketBox m : markets){
            if (m.isControl()) System.out.println(m.getMarketName() + " is control");
        }


        PriceProjection pp = new PriceProjection();
        pp.setPriceData(Set.of(PriceData.EX_BEST_OFFERS));
        //Service that runs continuously, requesting MarketBooks from betfair and updating the last traded price for each market
        ScheduledService<Void> svc = new ScheduledService<>() {
            protected Task<Void> createTask() {
                return new Task<>() {
                    protected Void call() {
                        long start = System.currentTimeMillis();


                        List<MarketBook> listOUMarketBooks = bfclient.listMarketBook(listMarketIDs, null, null, null).getResponse();

                        Platform.runLater(() -> {
                            for (MarketBook book : listOUMarketBooks){
                                    int marketIndex = listMarketIDs.indexOf(book.getMarketId());
                                    MarketBox currentMarket = markets.get(marketIndex);

                                    //If the betfair response returns a market version prior to the one on display, no point updating since the data is outdated.
                                    if (book.getVersion()>=currentMarket.getMarketVersion()) {
                                        Double newPrice = book.getRunners().get(0).getLastPriceTraded();
                                        //Double newPrice = book.getRunners().get(0).getEx().getAvailableToBack().size()!=0? book.getRunners().get(0).getEx().getAvailableToBack().get(0).getPrice() : null;

                                        // If a market status changes to closed, disables that market and also the next (the limit) as long as there is a next market that is not disabled already.
                                        if ((!currentMarket.getStatus().equals("CLOSED") && book.getStatus().equals("CLOSED"))){
                                            currentMarket.disable();
                                            currentMarket.setDisable(true);

                                            //Disables the limit market
                                            if (marketIndex+1<markets.size() && markets.get(marketIndex+1).isEnabled()){
                                                markets.get(marketIndex+1).disable();

                                                if (hasGoal(markets.get(marketIndex+1).getLowestPrice(),currentMarket.getLowestPrice())){
                                                    System.out.println("forcing market update");
                                                    markets.get(marketIndex+1).setForceUpdate(true);
                                                }

                                                if (marketIndex+2<markets.size() && markets.get(marketIndex+2).isEnabled()){
                                                    markets.get(marketIndex+2).setControl(false);
                                                    if (marketIndex+3<markets.size() && markets.get(marketIndex+3).isEnabled()){
                                                        markets.get(marketIndex+3).setControl(true);
                                                    }
                                                }

                                                /*//updates the control markets (limit+1 and limit+2)
                                                markets.get(marketIndex+1).updateControl();
                                                if (marketIndex+2<markets.size() && markets.get(marketIndex+2).isEnabled()){
                                                    markets.get(marketIndex+2).updateControl();
                                                    if (marketIndex+3<markets.size() && markets.get(marketIndex+3).isEnabled()) markets.get(marketIndex+3).updateControl();
                                                }*/
                                            }

                                            //removes closed markets
                                            layout.getChildren().remove(marketIndex);
                                            markets.remove(marketIndex);
                                            listMarketIDs.remove(marketIndex);

                                        }

                                        //Checks if the last traded price returned from betfair is not null or too many ticks lower than the current one on display.
                                        if (newPrice != null) {


                                            //Initializes the minimum price.
                                            if (currentMarket.isResetLowestPrice()){
                                                currentMarket.setLowestPrice(newPrice);
                                                currentMarket.setResetLowestPrice(false);
                                            }

                                            if (currentMarket.isResetLastTraded()){
                                                currentMarket.setLastTraded(newPrice);
                                                currentMarket.setResetLastTraded(false);
                                            }


                                            //Allows minimum price to be updated in case there is a goal.
                                            if (hasGoal(currentMarket.getLowestPrice(),newPrice) && (currentMarket.isEnabled() || currentMarket.isForceUpdate())){
                                                System.out.println(currentMarket.getMarketName() + " - Goal detected -- lowest price= " + currentMarket.getLowestPrice() + " newprice= " + newPrice);
                                                currentMarket.setLowestPrice(newPrice);
                                                currentMarket.setForceUpdate(false);
                                            }



                                            //If the new price is within the acceptable range for lay, it is updated on the GUI, otherwise it is discarded as it is just too delayed.
                                            if (isNewPriceValidLay(currentMarket.getLowestPrice(),newPrice)) {


                                                if (newPrice < currentMarket.getLowestPrice()){
                                                    currentMarket.setLowestPrice(newPrice);
                                                }

                                                //if odd difference to previous market is less than 0.05 then unchecks lay for the current market, as it is no longer a valid bet.
                                                if (currentMarket.isLaySelected() && currentMarket.getLowestPrice()<1.1 && (markets.get(marketIndex-1).getLowestPrice()-currentMarket.getLowestPrice())<0.05){
                                                    currentMarket.unSelectLay();
                                                }

                                                //if the previous odd market is lower than 1.08 and current market is a control market, sets it to false, as it is no longer worth planting control stakes on the current market. Lowestprice!=0 in the condition is to avoid the control market being set to false when the markets are being initialized and are set to 0 by default.
                                                if (currentMarket.isControl() && markets.get(marketIndex-1).getLowestPrice()<1.08 && markets.get(marketIndex-1).getLowestPrice()!=0){
                                                    currentMarket.setControl(false);
                                                }

                                                currentMarket.resetCounterLay();
                                            }
                                            else currentMarket.increaseCounterLay();

                                            //if the new price is acceptable for back it is updated, else discarded
                                            if (isNewPriceValidBack(currentMarket.getLastTraded(),newPrice)){
                                                currentMarket.setLastTraded(newPrice);
                                                currentMarket.resetCounterBack();
                                            }
                                            else currentMarket.increaseCounterBack();

                                        }

                                        if (currentMarket.isBackSelected()) {
                                            currentMarket.setBackOdds(Validator.increaseTicks(markets.get(marketIndex - 1).getLastTraded(),controls.getBackOffset()));
                                        }

                                        if (currentMarket.isLaySelected()){
                                            //current market lowest odds + 30% of the difference between previous market lowest odds and current market lowest odds

                                            //currentMarket.setLayOdds(generateLayOdds(currentMarket.getLowestPrice(), markets.get(marketIndex - 1).getLowestPrice(), controls.getLayOffset()));
                                            currentMarket.setLayOdds(Validator.getValidOdd(currentMarket.getLowestPrice()+((currentMarket.getLowestPrice()-1)*controls.getLayOffset()/100)));
                                        }

                                        currentMarket.setStatus(book.getStatus());
                                        currentMarket.setMarketVersion(book.getVersion());
                                        }
                                }
                            controls.updateFps(System.currentTimeMillis()-start);
                            });
                        return null;
                    }
                };
            }
        };
        svc.start();

        //On closing the window, stops refreshing the markets and removes the match from the list.
        this.setOnCloseRequest(e -> {
            svc.cancel();
            voidAllRequests();
            openEvents.remove(match.getName(),this);
        });

        //Adds all elements to the scene and shows stage
        box.getChildren().addAll(controls,layout);
        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(box);
        Scene scene = new Scene(pane);
        this.setScene(scene);
        //Set the stage to show only the controls
        /*this.setWidth(160);
        this.setHeight(180);*/
        this.show();

    }

    private void voidAllRequests(){
        for (ScheduledService<Void> s : listServices) {
            s.cancel();
        }
        listServices.clear();
    }

    private boolean isNewPriceValidLay(double storedPrice, double newPrice){

        if (newPrice==storedPrice) return true;
        int ticks=10;
        if (storedPrice>=1.01 && storedPrice<1.1) ticks=3;
        if (storedPrice>=1.1 && storedPrice<1.2) ticks=5;
        if (storedPrice>=1.2 && storedPrice<1.3) ticks=7;
        if (newPrice<storedPrice) return (Validator.increaseTicks(newPrice,ticks)>=storedPrice);
        return (Validator.increaseTicks(storedPrice,ticks)>=newPrice);

    }

    private boolean isNewPriceValidBack(double storedPrice, double newPrice){
        if (newPrice>=storedPrice) return true;
        return Validator.increaseTicks(newPrice,10)>=storedPrice;
    }

    /*private boolean hasMarketNotUpdated(double oddMarket1, double oddMarket2){
        System.out.println("odd1: " + oddMarket1 + "odd2: " + oddMarket2 + "result: " + (oddMarket1-1)/(oddMarket2-1));
        return (oddMarket1-1)/(oddMarket2-1)>1.5;
    }*/

    private boolean hasGoal(double storedPrice, double newPrice){

        if ((newPrice-1)/(storedPrice-1)>7) return false;
        if (storedPrice==1.01) return ((newPrice-1)/(storedPrice-1))>=5;
        if (storedPrice==1.02) return ((newPrice-1)/(storedPrice-1))>=4;
        if (storedPrice==1.03) return ((newPrice-1)/(storedPrice-1))>=3;
        if (storedPrice==1.04) return ((newPrice-1)/(storedPrice-1))>2.5;
        return ((newPrice-1)/(storedPrice-1))>2;
    }

    private double getStakeLiability(double stake, double odd){
        if (odd<2){
            return ((double) Math.round(100*stake/(odd-1)))/100;
        }
        return stake;
    }
    private double getLiability(double stake, double odd){
        return stake*(odd-1);
    }

    private double generateLayOdds(double currentLowestPrice, double previousLowestPrice, double pc){
        double lay = Validator.getValidOdd(currentLowestPrice + ((previousLowestPrice-currentLowestPrice)*(pc/100)));
        if (lay<=1.3) return Validator.getValidOdd((lay+currentLowestPrice)*0.5);
        return lay;
    }


    private void placeBets(BetfairClient bfclient, boolean back, boolean lay){

        long start = System.currentTimeMillis();
        controlMarketsBetIDs.clear();
        for (MarketBox m : markets){

            if (m.isBackSelected() || m.isLaySelected() || m.isControl()){
                List<PlaceInstruction> listBets = new ArrayList<>();

                if (back && m.isBackSelected()) {
                    listBets.add(createInstruction(m.getSelectionID(), Side.BACK, controls.getStake(), m.getBackOdds()));
                    //System.out.println("trying to place a " + controls.getBackStake() + " euro bet at odd " + m.getBackOdds());

                }
                if (lay && m.isLaySelected()) {

                    //If stoploss mode is on
                    if (controls.isStopLossSelected() && getLiability(controls.getStake(),m.getLayOdds())>controls.getMaxLoss()){
                        System.out.println("Bet not placed due to stop loss limitations");
                    }else{

                        //if liability mode is on
                        double stake = controls.isLiabilitySelected() ? getStakeLiability(controls.getStake(),m.getLayOdds()) : controls.getStake();
                        listBets.add(createInstruction(m.getSelectionID(),Side.LAY,stake,m.getLayOdds()));

                        //if multi-stakes mode is on
                        if (controls.isMultiStakeSelected()){
                            stake = controls.getStake();
                            for (int i=1;i<=3;i++){
                                double layOdd=generateLayOdds(m.getLowestPrice(),m.getLayOdds(),25*i);
                                if (controls.isLiabilitySelected()) stake = getStakeLiability(controls.getStake(),layOdd);
                                listBets.add(createInstruction(m.getSelectionID(),Side.LAY,stake,layOdd));
                            }

                        }
                        //System.out.println("trying to place a " + controls.getLayStake() + " euro liability lay bet at odd " + m.getLayOdds());
                    }

                }

                if (m.isControl() && controls.isControlSelected()){
                        if (controls.isLiabilitySelected()){
                            listBets.add(createInstruction(m.getSelectionID(), Side.LAY, getStakeLiability(controls.getStake()+0.1,1.01), 1.01));
                            listBets.add(createInstruction(m.getSelectionID(), Side.LAY, getStakeLiability(controls.getStake()+0.1,1.02), 1.02));
                            listBets.add(createInstruction(m.getSelectionID(), Side.LAY, getStakeLiability(controls.getStake()+0.1,1.03), 1.03));
                        } else{
                            double ctrlStake = (double) Math.round(controls.getStake()*100+10)/100;
                            listBets.add(createInstruction(m.getSelectionID(), Side.LAY, ctrlStake, 1.01));
                            listBets.add(createInstruction(m.getSelectionID(), Side.LAY, ctrlStake, 1.02));
                            listBets.add(createInstruction(m.getSelectionID(), Side.LAY, ctrlStake, 1.03));
                        }
                    }

                    /*if (m.isControl1Selected()) {
                        double stake = controls.getLayStake();

                        for (int i = 1; i <= controls.getControl1nStakes(); i++) {
                            double layOdd = Validator.increaseTicks(controls.getControl1Odd(), i - 1);
                            if (controls.isLiabilitySelected()) stake = getStakeLiability(controls.getLayStake(),layOdd);
                            listBets.add(createInstruction(m.getSelectionID(), Side.LAY, stake, layOdd, PersistenceType.PERSIST));
                        }
                        //System.out.println("trying to place control stakes of " + controls.getLayStake() + " on limit+1 from " + controls.getControl1Odd());
                    }
                    if (m.isControl2Selected()){
                        double stake = controls.getLayStake();

                        for (int i = 1; i <= controls.getControl2nStakes(); i++) {
                            double layOdd = Validator.increaseTicks(controls.getControl2Odd(), i - 1);
                            if (controls.isLiabilitySelected()) stake = getStakeLiability(controls.getLayStake(),layOdd);
                            listBets.add(createInstruction(m.getSelectionID(), Side.LAY, stake, layOdd, PersistenceType.PERSIST));
                        }
                        //System.out.println("trying to place control stakes of " + controls.getLayStake() + " on limit+2 from " + controls.getControl2Odd());
                    }*/

                if (!listBets.isEmpty()) {
                    ScheduledService<Void> s = new ScheduledService<>() {
                        protected Task<Void> createTask() {
                            return new Task<>() {
                                protected Void call() {
                                    long start = System.currentTimeMillis();
                                    PlaceExecutionReport report = bfclient.placeOrders(m.getMarketID(), listBets, null, null).getResponse();
                                    System.out.println("Time: " + (System.currentTimeMillis() - start) + " | Status: " + report.getStatus() + " | Error: " + report.getErrorCode());
                                    if (!report.getStatus().equals(ExecutionReportStatus.FAILURE)) {

                                        if (m.isControl() && controls.isControlSelected()){
                                            List<String> betIDs = new ArrayList<>();
                                            for (PlaceInstructionReport ins : report.getInstructionReports()) {
                                                if ((!controls.isLiabilitySelected() && ins.getInstruction().getLimitOrder().getSize()==controls.getStake())
                                                    || controls.isLiabilitySelected() && getLiability(ins.getInstruction().getLimitOrder().getSize(),ins.getInstruction().getLimitOrder().getSize())<2.05) {
                                                    betIDs.add(ins.getBetId());
                                                }
                                            }
                                            controlMarketsBetIDs.put(m.getMarketID(),betIDs);
                                        }

                                        this.cancel();
                                    }
                                    return null;
                                }
                            };
                        }
                    };
                    listServices.add(s);
                    s.start();
                }
            }
        }
        System.out.println(System.currentTimeMillis()-start);
    }

    private PlaceInstruction createInstruction(long selectionID, Side side, double size, double price){
        PlaceInstruction instruction = new PlaceInstruction();
        instruction.setOrderType(OrderType.LIMIT);
        instruction.setSelectionId(selectionID);
        instruction.setSide(side);
        LimitOrder order = new LimitOrder();
        order.setSize(size);
        order.setPrice(price);
        order.setPersistenceType(PersistenceType.LAPSE);
        instruction.setLimitOrder(order);
        return instruction;
    }



}
