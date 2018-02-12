public class SlowBacMain {
    public static void main(String[] args){
        double specific_alpha = Math.log(11.5)/500.;
        BioSystem.spatialAndNutrientDistributions(specific_alpha);
    }
}
