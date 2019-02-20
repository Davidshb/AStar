/*
 * INF4230 - Intelligence artificielle
 * UQAM / Département d'informatique
 * 
 * Hiver 2019 / TP1
 * 
 */

public abstract class Heuristique {

    protected Urgence urgence;

    public Heuristique(Urgence urgence) {
        this.urgence = urgence;
    }

    /*
     * Estime et retourne le coût restant pour atteindre le but à partir de état.
     * Attention : pour être admissible, cette fonction heuristique ne doit pas
     * surestimer le coût restant.
     */
    public abstract double estimerCoutRestant(Etat etat, But but);
}
