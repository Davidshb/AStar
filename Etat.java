
/*
 * INF4230 - Intelligence artificielle
 * UQAM / Département d'informatique
 * 
 * Hiver 2013 / TP1
 * 
 */

import java.util.Collection;
import java.util.LinkedList;

public class Etat implements Comparable<Etat> {

    // Référence sur la situation de l'urgence
    protected Urgence urgence;

    // Noyau de la représentation d'un état. Ici, on met tout ce qui rend l'état
    // unique.
    /* Emplacement de l'ambulance. */
    protected Emplacement emplacementAmbulance;
    /* Array indicant l'emplacement de chaque patient. */
    protected Emplacement emplacementsPatients[];
    /* Array indicant l'état de chargement de chaque patient par l'ambulance. */
    protected boolean patientsRecuperes[];
    /* Etat de chargement de l'ambulance */
    protected boolean patientCharge = false;

    // Variables pour l'algorithme A*.
    /* État précédent permettant d'atteindre cet état. */
    protected Etat parent;
    /* Action à partir de parent permettant d'atteindre cet état. */
    protected String actionFromParent;
    /* f=g+h. */
    protected double f;
    // Meilleur coût trouvé pour atteindre cet état à partir de l'état initial.
    protected double g;
    /* Estimation du coût restant pour atteindre le but. */
    protected double h;

    public Etat(Urgence urgence) {
        this.urgence = urgence;
    }

    /**
     * Fonction retournant les états successeurs à partir de cet état. Aussi ap
     * elé fonction de transition. Cela permet d'explorer l'espace d'état (le 
     * raphe de recherche).
     */

    public Collection<Successeur> genererSuccesseurs() {
        LinkedList<Successeur> successeurs = new LinkedList<Successeur>();

        // ----> emprunter une route de l'emplacement courant pour aller sur un
        // emplacement voisin,
        Successeur s;
        for (Route route : this.emplacementAmbulance.routes) {
            s = new Successeur();
            s.etat = this.clone();
            s.etat.emplacementAmbulance = route.destination;
            s.cout = 1.0D + this.getCoutDestination(route.destination);
            String direction = this.getDirection(route);
            s.action = this.getAction(route, direction);
            successeurs.add(s);
        }

        // ----> charger un patient lorsque l'ambulance : 1) est vide et 2) se trouve
        // sur un emplacement de client _pas encore transporté_
        if (!this.patientCharge) {
            for (int i = 0; i < this.emplacementsPatients.length; ++i) {
                if (!this.patientsRecuperes[i] && this.emplacementAmbulance == this.emplacementsPatients[i]) {
                    s = new Successeur();
                    s.etat = this.clone();
                    s.etat.patientCharge = true;
                    s.etat.patientsRecuperes[i] = true;
                    s.etat.emplacementsPatients[i] = null;
                    s.cout = this.urgence.dureeChargement;
                    s.action = "Charger()";
                    successeurs.add(s);
                    break;
                }
            }
        }
        // ----> décharger un patient
        for (int i = 0; i < this.emplacementsPatients.length; ++i) {
            if (this.patientsRecuperes[i] && this.emplacementAmbulance.type.equals("H")) {
                s = new Successeur();
                s.etat = this.clone();
                s.etat.patientCharge = false;
                s.etat.patientsRecuperes[i] = false;
                s.etat.emplacementsPatients[i] = this.emplacementAmbulance;
                s.cout = this.urgence.dureeDechargement;
                s.action = "Decharger()";
                successeurs.add(s);
                break;
            }
        }

        return successeurs;
    }

    // Crée un nouvel État en clonant le contenu pertinent de l'état actuel
    @Override
    public Etat clone() {
        Etat etat2 = new Etat(urgence);
        etat2.patientCharge = patientCharge;
        etat2.emplacementAmbulance = emplacementAmbulance;
        etat2.emplacementsPatients = new Emplacement[emplacementsPatients.length];
        for (int i = 0; i < emplacementsPatients.length; i++)
            etat2.emplacementsPatients[i] = emplacementsPatients[i];

        etat2.patientsRecuperes = new boolean[patientsRecuperes.length];
        for (int i = 0; i < patientsRecuperes.length; i++)
            etat2.patientsRecuperes[i] = patientsRecuperes[i];
        return etat2;
    }

    /* Relation d'ordre nécessaire pour le TreeSet checkOpen . */
    @Override
    public int compareTo(Etat o) {
        int c;
        c = this.emplacementAmbulance.compareTo(o.emplacementAmbulance);
        if (c != 0)
            return c;

        if (patientCharge == o.patientCharge)
            c = 0;
        else if (patientCharge == true)
            return 1;
        else
            return -1;

        for (int i = 0; i < emplacementsPatients.length; i++) {
            c = (patientsRecuperes[i] ? 1 : 0) - (o.patientsRecuperes[i] ? 1 : 0);
            if (c != 0)
                return c;
            if (!patientsRecuperes[i]) {
                c = emplacementsPatients[i].compareTo(o.emplacementsPatients[i]);
                if (c != 0)
                    return c;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        String s = "ETAT: f=" + f + "  g=" + g + "\n";
        s += "  PosAmbulance: " + emplacementAmbulance.nom + "";
        for (int i = 0; i < emplacementsPatients.length; i++) {
            s += "\n  PosPatient[" + i + "] : ";
            s += emplacementsPatients[i] == null ? "--" : emplacementsPatients[i].nom;
        }
        s += "\n";
        return s;
    }

    private String getDirection(Route route) {
        double originX = route.origine.positionGeographique.getX();
        double originY = route.origine.positionGeographique.getY();
        double destX = route.destination.positionGeographique.getX();
        double destY = route.destination.positionGeographique.getY();
        if (destX == originX - 1.0D)
            return "Nord";
        else if (destX == originX + 1.0D)
            return "Sud";
        else if (destY == originY + 1.0D)
            return "Est";
        else if (destY == originY - 1.0D)
            return "Ouest";
        return "";
    }

    private String getAction(Route route, String direction) {
        return direction + " = Lieu " + route.origine.nom + " -> Lieu " + route.destination.nom + ")";
    }

    private double getCoutDestination(Emplacement destination) {
        return destination.type.equals("#") ? 1 : 2;
    }
}