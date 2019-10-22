package sat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Bool;
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
public class SATSolverfor3 {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     * 
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static HashMap<Variable, Bool> solve(Formula formula) {
        // TODO: implement this.
        HashMap<Variable, Bool> answer = new HashMap<Variable, Bool>();
        return solve(formula.getClauses(), answer);
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
    private static HashMap<Variable, Bool> solve(ImList<Clause> clauses, HashMap<Variable, Bool> env) {
        // TODO: implement this.
        if(clauses.isEmpty()) return env;
        Clause smallest = clauses.first();
        for (Clause C:clauses.rest()){
            if (C.isEmpty()) {
                //System.out.println("works");
                return null;
            }
            if (smallest.size()>C.size())smallest = C;
            if (smallest.isUnit()) break;
        }

        Literal randomL = smallest.chooseLiteral();
        Variable VrandomL = randomL.getVariable();
        ImList<Clause> second_clauses = substitute(clauses,randomL);
        if (second_clauses == null) return null;
        if (randomL instanceof PosLiteral) env.put(VrandomL,Bool.TRUE);
        else env.put(VrandomL, Bool.FALSE);
        if(smallest.isUnit()) return solve(second_clauses,env);
        else {
            HashMap<Variable,Bool> envtrue = solve(second_clauses,env);
            if (envtrue == null){
                second_clauses = substitute(clauses,randomL.getNegation());
                if(second_clauses == null)return null;
                if (randomL instanceof PosLiteral) env.put(VrandomL,Bool.FALSE);
                else env.put(VrandomL, Bool.TRUE);
                return solve(second_clauses,env);
            }
            return envtrue;
        }
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
        ImList<Clause> new_clauses = clauses;
        Clause tempclause;
        for(Clause C: clauses){
            if(C.contains(l) || C.contains(l.getNegation())){
                tempclause = C.reduce(l);
                if(tempclause != null){
                    if(tempclause.isEmpty())return null;
                    new_clauses = new_clauses.add(tempclause);
                }
                new_clauses = new_clauses.remove(C);

            }

        }
        return new_clauses;
    }

}
