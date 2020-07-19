package sample;

import java.util.ArrayList;

public class PriceHolder {

    private ArrayList<Double> averagePrice;

    public PriceHolder() {
        this.averagePrice=new ArrayList<>();
    }

    public void update(double newPrice){

        if (this.averagePrice.size()==6) this.averagePrice.remove(0);
        this.averagePrice.add(newPrice);
    }

    public void flush(){
        averagePrice.clear();
    }

    public double getAverage(){
        double sum=0;
        for (double price : averagePrice){
            sum=sum+price;
        }
        return Validator.getValidOdd(sum/averagePrice.size());
    }

}
