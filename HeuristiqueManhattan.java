
/**
 * Heuristique Manhattan
 */

public class HeuristiqueManhattan extends Heuristique {

    public HeuristiqueManhattan(Urgence urgence) {
        super(urgence);
    }

    public double estimerCoutRestant(Etat etat, But but) {
        double coutTotal = 0.0D;

        for (int i = 0; i < etat.emplacementsPatients.length; i++) {
            // Le patient n'est pas arrivé à l'hopital
            if (etat.emplacementsPatients[i] != but.destinationsPatients[i]) {
                // Le patient est dans l'ambulance
                if (etat.patientsRecuperes[i]) {
                    coutTotal += etat.urgence.dureeDechargement
                            + distanceManhattan(etat.emplacementAmbulance, but.destinationsPatients[i]);
                } else {
                    // Distance Ambulance -> Patient + Patient -> Hopital
                    coutTotal += etat.urgence.dureeChargement
                            + distanceManhattan(etat.emplacementAmbulance, etat.emplacementsPatients[i]);
                    coutTotal += etat.urgence.dureeDechargement
                            + distanceManhattan(etat.emplacementsPatients[i], but.destinationsPatients[i]);
                }
            }
        }

        return coutTotal;
    }

    private double distanceManhattan(Emplacement courant, Emplacement but) {
        double x = Math.abs(courant.positionGeographique.getX() - but.positionGeographique.getX());
        double y = Math.abs(courant.positionGeographique.getY() - but.positionGeographique.getY());
        return x + y;
    }
}