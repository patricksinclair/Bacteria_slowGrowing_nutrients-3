import java.util.ArrayList;

public class Bacteria {

    //factors pertaining to the genotype and path length of the bacteria
    private int m;
    private int finalM;
    private final int initialM = 1;


    //migration, death and mutation rates of the bacteria
    private double b = 0.1;
    private double d = 0.;
    private double mu = 0.;

    private double K_prime = 33.;
    private double D = 0.32;

    public Bacteria(int m){this. m = m;}

    public int getM(){return m;}
    public int getFinalM(){return finalM;}
    public double getB(){return b;}
    public double getD(){return d;}
    public double getMu(){return mu;}


    public double beta(double s){

        double mu = s/(K_prime+s);
        return D*mu;
    }

    public double growthRate(double c, double s){

        double phi_c = 0.5*(1 + Math.sqrt(1 - c/beta(s)));

        if(phi_c < 0.) return 0.;
        else return phi_c;
    }

    public double replicationRate(double c, double s){

        //System.out.println("rep rate:\t"+growthRate(c, s, s_max, K) * s/(K + s));
        return growthRate(c, s) * (s/(K_prime + s));
    }



    public static ArrayList<Bacteria> initialPopulation(int K, int initM){

        ArrayList<Bacteria> initPop = new ArrayList<Bacteria>(K);

        for(int i = 0; i < K; i++){
            initPop.add(new Bacteria(initM));
        }
        return initPop;
    }

}
