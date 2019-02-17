/*
 * INF4230 - Intelligence artificielle
 * UQAM / Département d'informatique
 * 
 * Hiver 2019 / TP1
 * 
 */

public class Route {

    protected Emplacement  origine;
    protected Emplacement  destination;
    /**
     * La direction : Nord, Sud, Ouest, Est
     * {@link #getDirection()}
     */
    protected String direction;

    public Route(Emplacement origine, Emplacement destination,String direction){
        this.origine = origine;
        this.destination = destination;
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

}
