import java.util.ArrayList;
import java.util.Random;

public class BioSystem {

    //no. of habitats, "carrying capacity", no. of nutrients present initially in each habitat
    private int L, K, s, s_max;

    private double c, alpha, timeElapsed;

    private boolean populationDead = false;

    private Microhabitat[] microhabitats;

    Random rand = new Random();

    public BioSystem(int L, int S, double alpha){

        this.L = L;
        this.K = K;
        this.s = S;
        this.s_max = S;
        this.alpha = alpha;

        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0.;

        for(int i = 0; i < L; i++){

            double c_i = Math.exp(alpha*(double)i) - 1.;
            microhabitats[i] = new Microhabitat(c_i, S);
        }
        microhabitats[0].fillWithWildType();
    }


    public int getL(){
        return L;
    }

    public double getTimeElapsed(){
        return timeElapsed;
    }
    public void setTimeElapsed(double timeElapsed){
        this.timeElapsed = timeElapsed;
    }

    public boolean getPopulationDead(){
        return populationDead;
    }

    public void setC(double c){
        for(Microhabitat m : microhabitats) {
            m.setC(c);
        }
    }

    public int getCurrentPopulation(){
        int runningTotal = 0;

        for(Microhabitat m : microhabitats) {
            runningTotal += m.getN();
        }
        return runningTotal;
    }

    public int getCurrentNutrients(){
        int runningTotal = 0;

        for(Microhabitat m : microhabitats) {
            runningTotal += m.getS();
        }
        return runningTotal;
    }


    public ArrayList<Double> getSpatialDistribution(){
        ArrayList<Double> microhabPops = new ArrayList<>(L);
        for(int i = 0; i < L; i++){
            microhabPops.add((double)microhabitats[i].getN());
        }
        return microhabPops;
    }

    //method for getting the current growth rates in each microhabitat
    public ArrayList<Double> getGrowthRateDistributions(){
        ArrayList<Double> growthRates = new ArrayList<>(L);

        for (int i = 0; i < L; i++){
            growthRates.add(microhabitats[i].getGrowthRate());
        }
        return growthRates;
    }

    public ArrayList<Double> getNutrientDistribution(){
        ArrayList<Double> sVals = new ArrayList<>(L);

        for(int i = 0; i < L; i++){
            sVals.add((double)microhabitats[i].getS());
        }
        return sVals;
    }


    public Microhabitat getMicrohabitat(int i){
        return microhabitats[i];
    }

    public Bacteria getBacteria(int l, int k){
        return microhabitats[l].getBacteria(k);
    }

    public void migrate(int currentL, int bacteriumIndex){

        double direction = rand.nextDouble();

        if(direction < 0.5 && currentL < (L - 1)) {


            ArrayList<Bacteria> source = microhabitats[currentL].getPopulation();
            ArrayList<Bacteria> destination = microhabitats[currentL + 1].getPopulation();

            destination.add(source.remove(bacteriumIndex));

        }else if(direction > 0.5 && currentL > (0)){

            ArrayList<Bacteria> source = microhabitats[currentL].getPopulation();
            ArrayList<Bacteria> destination = microhabitats[currentL - 1].getPopulation();

            destination.add(source.remove(bacteriumIndex));
        }
    }

    public void die(int currentL, int bacteriumIndex){

        microhabitats[currentL].removeABacterium(bacteriumIndex);
        if(getCurrentPopulation() == 0) populationDead = true;
    }


    public void replicate(int currentL, int bacteriumIndex){
        //a nutrient unit is consumed for every replication
        microhabitats[currentL].consumeNutrients();
        //the bacterium which is going to be replicated and its associated properties
        Bacteria parentBac = microhabitats[currentL].getBacteria(bacteriumIndex);
        int m = parentBac.getM();

        Bacteria childBac = new Bacteria(m);
        microhabitats[currentL].addABacterium(childBac);
    }


    public void performAction(){

        //selects a random bacteria from the total population
        if(!populationDead) {

            int randomIndex = rand.nextInt(getCurrentPopulation());
            int indexCounter = 0;
            int microHabIndex = 0;
            int bacteriaIndex = 0;

            forloop:
            for(int i = 0; i < getL(); i++) {

                if((indexCounter + microhabitats[i].getN()) <= randomIndex) {

                    indexCounter += microhabitats[i].getN();
                    continue forloop;
                } else {
                    microHabIndex = i;
                    bacteriaIndex = randomIndex - indexCounter;
                    break forloop;
                }
            }

            Microhabitat randMicroHab = microhabitats[microHabIndex];

            int s = randMicroHab.getS(), s_max = randMicroHab.getS_max();
            double K_prime = randMicroHab.getK_prime(), c = randMicroHab.getC();
            Bacteria randBac = randMicroHab.getBacteria(bacteriaIndex);

            double migRate = randBac.getB();
            double deaRate = randBac.getD();
            double repliRate = randBac.replicationRate(c, s);
            double R_max = 1.2;
            double rando = rand.nextDouble()*R_max;

            if(rando < migRate) migrate(microHabIndex, bacteriaIndex);
            else if(rando >= migRate && rando < (migRate + deaRate)) die(microHabIndex, bacteriaIndex);
            else if(rando >= (migRate + deaRate) && rando < (migRate + deaRate + repliRate))
                replicate(microHabIndex, bacteriaIndex);

            timeElapsed += 1./((double) getCurrentPopulation()*R_max);
            //move this to the death() method

        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    //this was modified to also plot the growth rates and nutrient distributions over time
    public static void spatialAndNutrientDistributions(double input_alpha){

        int L = 500, nReps = 10;
        double interval = 100.;
        double duration = 2000.;
        double alpha = input_alpha;
        int S = 500;

        String filename = "realisticSlowGrowers-alpha-"+String.valueOf(alpha)+"-spatialDistribution-precise";
        String filenameGRates = "realisticSlowGrowers-alpha-"+String.valueOf(alpha)+"-gRateDistribution-precise";
        String filenameNutrients = "realisticSlowGrowers-alpha-"+String.valueOf(alpha)+"-nutrientDistribution-precise";
        boolean alreadyRecorded = false;

        ArrayList<Double> xVals = new ArrayList<>(L);
        for(double i = 0; i < L; i++){
            xVals.add(i);
        }


        BioSystem bs = new BioSystem(L, S, alpha);

        while(bs.getTimeElapsed() <= duration && !bs.getPopulationDead()){
            bs.performAction();

            if((bs.getTimeElapsed()%interval >= 0. && bs.getTimeElapsed()%interval <= 0.01) && !alreadyRecorded){

                System.out.println("Success "+(int)bs.getTimeElapsed());
                alreadyRecorded = true;

                ArrayList<Double> popVals = bs.getSpatialDistribution();
                ArrayList<Double> gRateVals = bs.getGrowthRateDistributions();
                //ArrayList<Double> nutrientVals = bs.getNutrientDistribution();

                String timeValue = "-"+String.valueOf((int)bs.getTimeElapsed());
                Toolbox.writeTwoArraylistsToFile(xVals, popVals, (filename+timeValue));
                Toolbox.writeTwoArraylistsToFile(xVals, gRateVals, (filenameGRates+timeValue));
                //Toolbox.writeTwoArraylistsToFile(xVals, nutrientVals, (filenameNutrients+timeValue));
            }

            if(bs.getTimeElapsed()%interval >= 0.1 && alreadyRecorded) alreadyRecorded = false;

        }
        System.out.println("duration "+bs.getTimeElapsed());

    }


}