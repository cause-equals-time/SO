package sample;

import java.io.FileReader;
import java.util.Scanner;

public class Preferences {
    private int stake;
    private int backOffSet;
    private int layOffSet;
    private boolean control;
    private boolean liability;
    private boolean multiStake;
    private boolean stopLoss;
    private boolean anchor;
    private int loss;


    public Preferences() {
        try{
            Scanner sc = new Scanner(new FileReader("preferences"));
            while (sc.hasNextLine()) {
                String[] line = sc.nextLine().split("=");
                switch (line[0]) {
                    case "stake" : this.stake=Integer.parseInt(line[1]); break;
                    case "backOffSet" : this.backOffSet=Integer.parseInt(line[1]); break;
                    case "layOffSet" : this.layOffSet=Integer.parseInt(line[1]); break;
                    case "control" : this.control=line[1].equals("true"); break;
                    case "liability" : this.liability=line[1].equals("true"); break;
                    case "multiStake" : this.multiStake=line[1].equals("true"); break;
                    case "stopLoss" : this.stopLoss=line[1].equals("true"); break;
                    case "anchor" : this.anchor=line[1].equals("true"); break;
                    case "loss" : this.loss=Integer.parseInt(line[1]); break;
                }
            }
            sc.close();
        }
        catch(Exception ignored){
            this.stake=2;
            this.backOffSet=5;
            this.layOffSet=10;
            this.control=false;
            this.liability=false;
            this.multiStake=false;
            this.stopLoss=false;
            this.anchor=false;
            this.loss=4;
        }
    }

    public int getStake() {
        return stake;
    }

    public int getBackOffSet() {
        return backOffSet;
    }

    public int getLayOffSet() {
        return layOffSet;
    }

    public boolean isControl() {
        return control;
    }

    public boolean isLiability() {
        return liability;
    }

    public boolean isMultiStake() {
        return multiStake;
    }

    public boolean isStopLoss() {
        return stopLoss;
    }

    public boolean isAnchor(){
        return anchor;
    }

    public int getLoss() {
        return loss;
    }
}
