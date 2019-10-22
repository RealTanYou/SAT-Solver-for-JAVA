package sat;

/*
import static org.junit.Assert.*;

import org.junit.Test;
*/

import sat.env.*;
import sat.formula.*;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();


    public static void main(String[] args){
        /**
         * The following code reads a CNF file and creates a forumla in both 'forumla' form and in nested arraylists.
         * This is done for both 2-sat solver and 3 or higher sat solvers.
         */

        String filename = "D:\\Documents\\SUTD important files\\Sophomore Year\\Project-2D\\project-2d-starting\\sampleCNF\\largeSat.CNF";
        String regexpresion = "\\s{1,10}";
        Formula f = new Formula();
        Clause tempClause = new Clause();
        String templine;
        String[] templist;
        templist = new String[]{};
        templine = "";
        int longest_clause = 1;
        boolean p_check = false;
        boolean clause_check = false;
        int count_p = 3;
        int num_of_variables = 0;
        int variableValue = 0;
        ArrayList<Literal> literalList = new ArrayList<Literal>();
        ArrayList<ArrayList<Integer>> forumlar2 = new ArrayList<>();
        ArrayList<Integer> clause2 = new ArrayList<>();
        System.out.println("Creating formula from " + filename);
        long started = System.nanoTime();
        try{
            File file = new File(filename);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            templine = new String(data,"UTF-8");
            templist = templine.split(regexpresion);
            for (String S: templist) {
                if (!S.equals("p") && !p_check && !clause_check) continue;
                if (S.equals("p")) {
                    p_check = true;
                    continue;
                }
                if (p_check) {
                    if (count_p == 3) {
                        count_p--;
                        continue;
                    } else if (count_p == 2) {
                        num_of_variables = Integer.parseInt(S);
                        count_p--;
                        continue;
                    } else {
                        p_check = false;
                        clause_check = true;
                        continue;
                    }
                } else {
                    variableValue = Integer.parseInt(S);
                    if (variableValue == 0){
                        for (Literal l: literalList) {
                            tempClause =tempClause.add(l);
                        }
                        if (longest_clause < tempClause.size()) longest_clause = tempClause.size();
                        f = f.addClause(tempClause);
                        tempClause = new Clause();
                        literalList = new ArrayList<Literal>();
                        forumlar2.add(clause2);
                        clause2 = new ArrayList<>();
                    }
                    else if (variableValue < 0){
                        clause2.add(variableValue);
                        literalList.add(NegLiteral.make(S.substring(1)));
                    }
                    else{
                        clause2.add(variableValue);
                        literalList.add(PosLiteral.make(S));
                    }
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("File read failed.");
        }

        long time = System.nanoTime();
        long timeTaken= time - started;
        System.out.println("Time:" + timeTaken/1000000.0 + "ms");

        int[] e1 = null;
        HashMap<Variable,Bool> e2 = null;

        System.out.println("SAT solver starts!!!");
        long started2 = System.nanoTime();
        if (longest_clause <=2) e1 = SatSolverfor2.solve(forumlar2,num_of_variables);
        else e2 = SATSolverfor3.solve(f);
        long time2 = System.nanoTime();
        long timeTaken2= time2 - started2;
        System.out.println("Time:" + timeTaken2/1000000.0 + "ms");

        //if (e1 != null) System.out.println(Arrays.toString(e1));
        if (e2 != null) System.out.println(e2.toString());

    }

    public void testSATSolver1(){
    	// (a v b)
    	Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);
/*
    	assertTrue( "one of the literals should be set to true",
    			Bool.TRUE == e.get(a.getVariable())  
    			|| Bool.TRUE == e.get(b.getVariable())	);
    	
*/    	
    }
    
    
    public void testSATSolver2(){
    	// (~a)
    	Environment e = SATSolver.solve(makeFm(makeCl(na)));
/*
    	assertEquals( Bool.FALSE, e.get(na.getVariable()));
*/    	
    }
    
    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }
    
    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }

    public static boolean isInteger(String S){
        if (S == null){ return false; }
        if (S.isEmpty()){return false;}
        int i = 0;
        if (S.charAt(i) == '-'){
            if (S.length() == 1){return false;}
            i++;
        }
        char temp_char;
        for (;i< S.length();i++){
            temp_char = S.charAt(i);
            if (i < '0' || i > '9'){return false;}
        }
        return true;
    }

    
}

