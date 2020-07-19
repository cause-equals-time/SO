package sample;
public class Validator {


    private static int getIncrement(int iodd){

        if (iodd >= 101 && iodd < 200) {
            return 1;
        } else if (iodd >= 200 && iodd < 300) {
            return 2;
        } else if (iodd >= 300 && iodd < 400) {
            return 5;
        } else if (iodd >= 400 && iodd < 600) {
            return 10;
        } else if (iodd >= 600 && iodd < 1000) {
            return 20;
        } else if (iodd >= 1000 && iodd < 2000) {
            return 50;
        } else if (iodd >= 2000 && iodd < 3000) {
            return 100;
        } else if (iodd >= 3000 && iodd < 5000) {
            return 200;
        } else if (iodd >= 5000 && iodd < 10000) {
            return 500;
        } else if (iodd >= 10000 && iodd < 100000) {
            return 1000;
        }
        return 0;
    }

    public static double getValidOdd(double odd){

        int iodd = (int) Math.round(odd*100);
        if (iodd<101) return 0;

        int increment = getIncrement(iodd);
        if (iodd%increment!=0){
            double newOdd = iodd - (iodd)%(increment) + increment;
            return newOdd/100;
        }

        return ((double)iodd/100);
    }

    public static double increaseTicks(double odd, int ticks){

        if (ticks<1) return odd;
        int iodd = (int) Math.round(odd*100);
        for (int i=0; i<ticks; i++){
            iodd+=getIncrement(iodd);
        }

        return((double)iodd/100);
    }

}
