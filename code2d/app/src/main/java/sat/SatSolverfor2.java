package sat;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class SatSolverfor2 {

    private static int Vnum;
    static private int index = 0;

    static int[] defined2 = new int[]{};
    static int[] low_index2 = new int[]{};
    static ArrayList[] graph2;
    static int[] answer2 = new int[]{};
    static LinkedList<Integer> stack2 = new LinkedList<>();
    static ArrayList[] scc2;

    /**
     * Solve a 2 SAT problem using Tarjan's strongly connected components algorithm.
     * note that this implemenetation is slightly inefficient.
     * Otherwise, it does solve it at least in polynomial time
     * @param f
     * @param numV
     * @return
     */
    public static int[] solve(ArrayList<ArrayList<Integer>> f, int numV){
        Vnum = numV*2+1;
        defined2 = new int[Vnum];
        low_index2 = new int[Vnum];
        graph2 = new ArrayList[Vnum];
        answer2 = new int[Vnum];
        scc2 = new ArrayList[Vnum];
        make_graph(f);
        make_sccs();

        /**
         * once all SCCs have been made, check if the traversal of each SCC hits it's negation.
         * if it does, the formula is not satisfiable.
         * Otherwise, the answer will indicate the truth value of each vertice.
         * note: the answer might contain something like 5879 => 5233. The number above or below
         * the number of variables should indicate if it is true or false.
         * If a variable points to null, ignore it.
         */
        for (int l = 1; l < Vnum; l++){
            //System.out.println(l + "hellllllo");
            ArrayList<Integer> temp = scc2[l];
            if (temp != null) {
                for (int u : temp) {
                    int nu = negate(u);
                    if (answer2[nu] == l) {
                        System.out.println("FORMULA UNSATISFIABLE");
                        return null;
                    }
                    answer2[u] = l;
                }
            }
        }
        System.out.println("FORMULA SATISFIABLE");
        for (int i = 1; i <=numV; i++){
            if (answer2[i] <= numV){
                System.out.print("1 ");
            }
            else {
                System.out.print("0 ");
            }
        }
        return answer2;
    }

    /**
     * Going through all varibles, find all of the strongly connected components using
     * tarjan's algorithm.
     * If a vertice has already been visited, skip it.
     */

    private static void make_sccs() {
        for(int i = 1; i < Vnum; i++ ){
            if(defined2[i] == 0)dfs_tarjan(i);
        }


    }

    /**
     *Given the vertice, find all Strongly connected components of a vertice.
     * defined indicates if a vertice has been discovered and at when was it discovered.
     * low_index indicates the index of the vertice which was traversed eariler. This
     * low index will show that that that vertice can reach this vertice.
     * stack hold all found nodes in a LIFO order.
     *
     * Initially, when starting the first vertice, which is variable 1, the index is 0.
     * adding 1 starts the 'counter', so to speak. The index is then stored in the defined and low
     * index list, as well as the vertice is added to the stack.
     *
     * Then, the vertice's connected vertices are searched though.
     * If there is a vertice, it is checked that if it is traversed. If not, it will dfs through
     * the vertice until it hits vertice with no connected vertice or it hits a vertice that has already been
     * traversed.
     * if the vertice that was hit has been traversed, the defined index of that vertice is compared
     * with the current low index. If it is smaller, the current low-index is changed to it.
     *
     * Otherwise, once the call has been made, the travesed vertice's low_index is compared to the
     * current vertice's low index, and changed if the traversed one is lowered, which means
     * that the stack of vertices is strongly connected.
     *
     * if the vertice that has been traversed hits the beginning/head vertice, the recursion will stop
     * and the whole stack up till the head vertice will be added to the list of strongly connected
     * components.
     *
     * @param l
     */

    private static void dfs_tarjan( int l) {
        index++;
        defined2[l] = index;
        low_index2[l] = index;
        stack2.add(l);
        ArrayList<Integer> temp = graph2[l];
        if (temp != null){
            for (int u:temp) {
                if (defined2[u] == 0){
                    dfs_tarjan(u);
                    low_index2[l] = Math.min(low_index2[l],low_index2[u]);
                }
                else if(stack2.contains(u)){
                    low_index2[l] = Math.min(low_index2[l],defined2[u]);
                }
            }
        }
        if (defined2[l] == low_index2[l]){
            int u;
            ArrayList<Integer> new_scc2 = new ArrayList<>();
            while (true){
                u = stack2.removeLast();
                new_scc2.add(u);
                if(u == l)break;
            }
            scc2[l] = new_scc2;
        }
    }

    /**
     * Given a forumla in Nested arraylists, create a the adjacency list of vertices (directed graph)
     * The formula given is in conjunction normal form, where each clause each have at most 2 literals.
     * To convert it into an adjacency list of vertices, each clause is converted into 2 implication statements.
     * e.g. (a v ~b) becomes (~a -> ~b) and (b -> a).
     * for a single literal clause, like (a), it is the same as (a v a).
     * So an implied statement wlll be (~a -> a), which means a must be true
     * if one literal is false, the other must be true for the clause to be true.
     * For this solver, instead of using the inbuilt forumla class, a nested arraylist was given.
     * @param f nested arraylist of variables
     */
    public static void make_graph(ArrayList<ArrayList<Integer>> f){
        int first, second, nfirst, nsecond;

        for (ArrayList C:f) {
            if (C.size() == 1){
                first = (int) C.get(0);
                if (first < 0) first = (Vnum-1)/2 - first;
                second = negate(first);
                if (graph2[second] == null) graph2[second]=new ArrayList<Integer>(Arrays.asList(first));
                else graph2[second].add(first);
                continue;
            }
            first = (int)C.get(0);
            if (first < 0) first = (Vnum-1)/2 - first;
            nfirst = negate(first);
            second = (int)C.get(1);
            if (second < 0) second = (Vnum-1)/2 - second;
            nsecond = negate(second);
            if (graph2[nfirst] == null) { graph2[nfirst]=new ArrayList<Integer>(Arrays.asList(second)); }
            else graph2[nfirst].add(second);
            if (graph2[nsecond] == null) graph2[nsecond]=new ArrayList<Integer>(Arrays.asList(first));
            else graph2[nsecond].add(first);
        }

    }

    /**
     * This short method returns a 'negated' variable.
     * Using the number of variables as the counter, all negated variables are it value + (Vnum-1)/2
     * @param l
     * @return
     */

    public static int negate(int l){
        if (l <= (Vnum-1)/2) return l + (Vnum-1)/2;
        return l - (Vnum-1)/2;
    }
}
