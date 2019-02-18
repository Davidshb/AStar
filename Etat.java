
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
    /* Meilleur coût trouvé pour atteindre cet état à partir de l'état initial. */
    protected double g;
    /* Estimation du coût restant pour atteindre le but. */
    protected double h;

    public Etat(Urgence urgence) {
        this.urgence = urgence;
        this.parent = null;
    }

    /**
     * Fonction retournant les états successeurs à partir de cet état. Aussi appelé
     * fonction de transition. Cela permet d'explorer l'espace d'état (le graphe de
     * recherche).
     */

    public Collection<Successeur> genererSuccesseurs() {
        LinkedList<Successeur> successeurs = new LinkedList<Successeur>();

        // À compléter.
        //
        // - Les actions possibles sont :
        // ----> emprunter une route de l'emplacement courant pour aller sur un
        // emplacement voisin,
        // ----> charger un patient lorsque l'ambulance : 1) est vide et 2) se trouve
        // sur un emplacement de client _pas encore transporté_
        // ----> décharger un patient

        // - Pour toute action possible
        // --- Instancier un objet Successeur s;
        // --- Cloner l'état courant dans la variable état du successeur (s.etat =
        // clone()).
        // --- Créer la chaîne de caractère représentant l'action dans s.action (voir
        // plans fournis).
        // -----> ex. d'un déplacement à l'ouest "Ouest = Lieu " + route.origine.nom + "
        // -> Lieu " + route.destination.nom + ")"
        // --- Calculer le coût de cette action dans s.cout.
        // -----> ex. pour un déplacement, le cout est 1 + le cout de l'emplacement
        // --- Modifier la valeur des variables appropriées dans s.etat pour refléter
        // l'effet de l'action (qu'est-ce qui change?)
        // --- Ajouter s dans la liste successeurs.

        for (Route route : emplacementAmbulance.routes) {
            if (route.destination.type == " ")
                continue;

            Successeur s = new Successeur();
            s.etat = clone();
            s.etat.emplacementAmbulance = route.destination;
            s.action = route.getDirection() + " = Lieu " + route.origine.nom + " -> Lieu " + route.destination.nom
                    + ")";
            s.etat.actionFromParent = s.action;
            s.cout = 1 + route.destination.getCout();
            s.etat.parent = this;
            successeurs.add(s);
        }

        // Déchargement du patient à l'hopital. patientRecuperes est mis à true
        if (emplacementAmbulance.type.equals("H") && patientCharge) {
            for (int i = 0; i < patientsRecuperes.length; i++)
                if (emplacementAmbulance.compareTo(emplacementsPatients[i]) == 0 && !patientsRecuperes[i]) {
                    Successeur s = new Successeur();
                    s.etat = clone();
                    s.etat.patientCharge = false;
                    s.etat.patientsRecuperes[i] = true;
                    s.action = "Decharger()";
                    s.etat.actionFromParent = s.action;
                    successeurs.add(s);
                    break;
                }
        }

        // Chargement d'un patient lorsque l'ambulance se trouve sur lui et que
        // l'ambulance n'est pas chargé et q'il n'est pas récupéré
        for (int i = 0; i < patientsRecuperes.length; i++) {
            if (patientsRecuperes[i])
                continue;

            if (!patientCharge && emplacementAmbulance.compareTo(emplacementsPatients[i]) == 0) {
                Successeur s = new Successeur();
                s.etat = clone();
                s.etat.patientCharge = true;
                s.action = "Charger()";
                s.etat.actionFromParent = s.action;
                s.cout = urgence.dureeChargement;
                successeurs.add(s);
                break;
            }
        }

        return successeurs;
    }

    /* Crée un nouvel État en clonant le contenu pertinent de l'état actuel */
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
        s += "  Pos=" + emplacementAmbulance.nom + "";
        for (int i = 0; i < emplacementsPatients.length; i++) {
            s += "\n  PosColis[i]=";
            s += emplacementsPatients[i] == null ? "--" : emplacementsPatients[i].nom;
        }
        s += "\n";
        return s;
    }
}
