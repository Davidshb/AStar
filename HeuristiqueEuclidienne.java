
/**
 * Heuristique Euclidienne
 * Cette heuristique est moins efficace.
 */

public class HeuristiqueEuclidienne extends Heuristique {

    public HeuristiqueEuclidienne(Urgence urgence) {
        super(urgence);
    }

    public double estimerCoutRestant(Etat etat, But but) {
        double coutTotal = 0.0D;

        for (int i = 0; i < etat.emplacementsPatients.length; i++) {
            // Le patient n'est pas arrivé à l'hopital
            if (etat.emplacementsPatients[i] != but.destinationsPatients[i]) {
                // Le patient est dans l'ambulance
                if (etat.patientsRecuperes[i]) {
                    coutTotal += 30D + distanceEuclidienne(etat.emplacementAmbulance, but.destinationsPatients[i]);
                } else {
                    // Distance Ambulance -> Patient + Patient -> Hopital
                    coutTotal += 30D + distanceEuclidienne(etat.emplacementAmbulance, etat.emplacementsPatients[i]);
                    coutTotal += 30D + distanceEuclidienne(etat.emplacementsPatients[i], but.destinationsPatients[i]);
                }
            }
        }

        return coutTotal;
    }

    private double distanceEuclidienne(Emplacement courant, Emplacement but) {
        double x = courant.positionGeographique.getX() - but.positionGeographique.getX();
        double y = courant.positionGeographique.getY() - but.positionGeographique.getY();
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}