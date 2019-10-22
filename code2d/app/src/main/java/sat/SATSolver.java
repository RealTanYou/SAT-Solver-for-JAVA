package sat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.NegLiteral;
import sat.formula.PosLiteral;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     * 
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */

    public static Environment solve(Formula formula) {
        // TODO: implement this.
        Environment answer = new Environment();
        return solve(formula.getClauses(),answer);
        //throw new RuntimeException("not yet implemented.");
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     * 
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
        // TODO: implement this.
        if(clauses.isEmpty()) return env;
        Clause smallest = null;
        for (Clause C:clauses){
            if (C.isEmpty()) {
                System.out.println("works");
                return null;
            }
            if (smallest==null||smallest.size()>C.size())smallest = C;
            if (smallest.isUnit()) break;
        }

        Literal randomL = smallest.chooseLiteral();
        Variable VrandomL = randomL.getVariable();
        //System.out.println(VrandomL.toString());
        if (smallest.isUnit()){
            if (randomL.equals(PosLiteral.make(VrandomL)))env = env.putTrue(VrandomL);
            else env = env.putFalse(VrandomL);
            //System.out.println(smallest.toString());
            return solve(substitute(clauses,randomL),env);
        }
        else {
            if (randomL.equals(NegLiteral.make(VrandomL))) randomL = randomL.getNegation();
            Environment envtrue = solve(substitute(clauses,randomL),env.putTrue(VrandomL));
            //System.out.println(smallest.toString());
            if (envtrue == null) return solve(substitute(clauses,randomL.getNegation()),env.putFalse(VrandomL));
            return envtrue;
        }


        //throw new RuntimeException("not yet implemented.");
    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     * 
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses, Literal l) {
        // TODO: implement this.

        Iterator<Clause> Citer = clauses.iterator();
        ImList<Clause> new_clauses = new EmptyImList<Clause>();
        Clause tempclause;
        while(Citer.hasNext()){
            tempclause = Citer.next();
            tempclause = tempclause.reduce(l);
            if (tempclause!=null)
                new_clauses = new_clauses.add(tempclause);
        }
        return new_clauses;
        //throw new RuntimeException("not yet implemented.");
    }

}
