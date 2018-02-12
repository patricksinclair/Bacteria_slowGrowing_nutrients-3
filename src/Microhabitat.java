
import java.util.ArrayList;

public class Microhabitat {

    //K = karrying kapacity, K_prime is the constant used in growth rates.
    private int K = 100, s, s_max;
    private double c;

    private final double K_prime = 33.;
    private final double D = 0.32;

    private ArrayList<Bacteria> population;


    public Microhabitat(double c, int S){
        this.c = c;
        this.s = S;
        this.s_max = S;
        this.population = new ArrayList<Bacteria>(K);
    }


    public int getK(){return K;}
    public void setK(int K){
        this.K = K;
    }

    public double getC(){return c;}
    public void setC(double c){
        this.c = c;
    }

    public double getK_prime(){return K_prime;}

    public int getS(){return s;}
    public void setS(int S){this.s = S;}

    public int getS_max(){return s_max;}

    public int getN(){
        return population.size();
    }

    public ArrayList<Bacteria> getPopulation(){
        return population;
    }
    public Bacteria getBacteria(int i){
        return population.get(i);
    }

    public boolean fullOfMutants(){

        int mutantCounter = 0;
        for(int i = 0; i < population.size(); i++){
            if(population.get(i).getM() > 1) mutantCounter++;
        }
        if(mutantCounter >= K) return true;
        else return false;
    }

    public void innoculateWithABActeria(){
        int initGentotype = 1;
        population.add(new Bacteria(initGentotype));
    }

    public void fillWithWildType(){

        int initGenotype = 1;

        for(int i = 0; i < K; i++){
            population.add(new Bacteria(initGenotype));
        }
    }

    public double getGrowthRate(){

        double beta = D*(s/(K_prime+s));
        double phi_c = Math.max(0., 0.5*(1 + Math.sqrt(1 - c/beta)));
        return phi_c*(s/(K_prime+s));
    }

    public void consumeNutrients(){
        if(s > 0) s--;
    }

    public void removeABacterium(int i){
        population.remove(i);
    }

    public void addABacterium(Bacteria newBact){
        population.add(newBact);
    }



}
