
/*
 * INF4230 - Intelligence artificielle
 * UQAM / Département d'informatique
 * 
 * Hiver 2017 / TP1
 * 
 */

import java.util.*;

public class AStar {

    // Démarche suggérée.
    // -- Commencez avec une heuristique minimaliste (toujours 0) et testez avec un
    // problème TRÈS simple
    // -- Tracez les itérations sur la liste open sur la console avec
    // System.out.println(..).
    // -- Pour chaque itération :
    // ---- Affichez le numéro d'itération.
    // ---- Affichez l'état e sélectionné par l'intération (les e.f affichés
    // devraient croître);
    // -- Vérifiez le bon fonctionnement de la génération des états.
    // -- Vérifiez que e.f soit non-décroissant (>=) tout au long.
    // Lorsque l'implémentation est terminée et validée, évaluez la 
    // ertinence d'un
    // PriorityQueue.
    // Élaborez des heuristiques plus réalistes
    public static int noeudsExplores = 0;
    public static int noeudsGeneres = 0;
    public static double coutFinal = 0;

    public static List<String> genererPlan(Etat etatInitial, But but, Heuristique heuristique) {

        TreeSet<Etat> open = new TreeSet<>(new AStar.FComparator());
        TreeSet<Etat> closed = new TreeSet<>();
        TreeSet<Etat> checkOpen = new TreeSet<>();
        LinkedList<String> plan = new LinkedList<String>();

        // Initialisation des valeurs de cout
        etatInitial.g = 0;
        etatInitial.h = heuristique.estimerCoutRestant(etatInitial, but);
        etatInitial.f = etatInitial.g + etatInitial.h;

        open.add(etatInitial);
        while (!open.isEmpty()) {
            Etat e = open.pollFirst();
            noeudsExplores++;
            // displayIteration(e, open);
            
            if (but.butEstStatisfait(e)) {
                coutFinal = e.g;
                while (e != null) {
                    if (e.actionFromParent != null) {
                        plan.addFirst(e.actionFromParent);
                    }
                    e = e.parent;
                }
                return plan;
            }

            closed.add(e);

            Collection<Successeur> successeurs = e.genererSuccesseurs();

            for (Successeur s : successeurs) {
                s.etat.g = e.g + s.cout;
                s.etat.h = heuristique.estimerCoutRestant(s.etat, but);
                s.etat.f = s.etat.g + s.etat.h;
                s.etat.parent = e;
                s.etat.actionFromParent = s.action;
                noeudsGeneres++;

                if (!closed.contains(s.etat)) {
                    if (!checkOpen.contains(s.etat)) {
                        checkOpen.add(s.etat);
                        open.add(s.etat);
                    } else {
                        Etat etatExistant = checkOpen.floor(s.etat);
                        if (s.etat.f < etatExistant.f) {
                            checkOpen.remove(etatExistant);
                            open.remove(etatExistant);
                            checkOpen.add(s.etat);
                            open.add(s.etat);
                        }
                    }
                }
            }
        }

        return plan;
    }

    // Tout à la fin, n'oubliez pas de commenter les affichages de traçage
    private static void displayIteration(Etat e, TreeSet<Etat> open) {
        System.out.print(e);
        System.out.println("Open: " + open.size() + "\n");
    }

    // LA classe comparatrice pour les états qui se base sur les valeurs de
    public static class FComparator implements Comparator<Etat> {
        @Override
        public int compare(Etat e1, Etat e2) {
            if (e1.f < e2.f)
                return -1;
            if (e1.f > e2.f)
                return +1;

            return e1.compareTo(e2);
        }
    }
}
