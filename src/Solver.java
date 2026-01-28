
package rubikscube;


import java.io.*;
import java.util.*;


public class Solver {


    // 18 moves
    private static final int[] MOVE_LIST = {
            0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17
    };


    private static final String[] MOVE_NAME = {
            "U","U2","U'",
            "R","R2","R'",
            "F","F2","F'",
            "D","D2","D'",
            "L","L2","L'",
            "B","B2","B'"
    };


    

   /*This function uses three different estimates of remaining distance:
 *
 *   H1 – Misplaced / Misoriented Cubies
 *        +1 for every misplaced corner
 *        +1 for every misoriented corner
 *        +1 for every misplaced edge
 *        +1 for every misoriented edge
 *        (sum divided by 4 to remain admissible)
 *
 *   H2 – Corner Manhattan Distance
 *        Each corner cubie has a 3D coordinate in the solved cube.
 *        We compute the Manhattan distance between its current position
 *        and its goal position. Sum over all corners, then divide by 4.
 *
 *   H3 – Edge Manhattan Distance
 *        Same idea as H2 but for the 12 edges, also divided by 4.
 *
 * The final heuristic is:
 *
 *          H = max(H1, H2, H3)
 *
 * Taking the maximum keeps the heuristic admissible while providing a
 * much stronger estimate than any single component alone. This greatly
 * improves pruning and makes IDA* significantly faster on deeper scrambles.
 */

private static int heuristic(CubieCube c) {
    int h1 = 0;             // original: misplaced + misoriented
    int h2 = 0;             // corner "distance"
    int h3 = 0;             // edge "distance"

    // H1:
    for (int i = 0; i < 8; i++) {
        if (c.cp[i] != i) h1++;
        if (c.co[i] != 0) h1++;
    }

    for (int i = 0; i < 12; i++) {
        if (c.ep[i] != i) h1++;
        if (c.eo[i] != 0) h1++;
    }   
        h1 /= 4;

    // H2:
    for (int i = 0; i < 8; i++) {
        int cubie = c.cp[i];  // which cubie is at position i
        int[] goal = CubieCube.CORNER_POS[cubie];
        int[] cur  = CubieCube.CORNER_POS[i];

        int md = Math.abs(goal[0] - cur[0])
               + Math.abs(goal[1] - cur[1])
               + Math.abs(goal[2] - cur[2]);
        h2 += md;
    }
        h2 /= 4;

    // H3: 

    for (int i = 0; i < 12; i++) {
        int cubie = c.ep[i];
        int[] goal = CubieCube.EDGE_POS[cubie];
        int[] cur  = CubieCube.EDGE_POS[i];

        int md = Math.abs(goal[0] - cur[0])
               + Math.abs(goal[1] - cur[1])
               + Math.abs(goal[2] - cur[2]);
        h3 += md;
    }
         h3 /= 4;

    // Combine: max of admissible heuristics is still admissible
    return Math.max(h1, Math.max(h2, h3));
}


   private static final int FOUND = -1;

    // Proper IDA* DFS
private static int dfsIda(CubieCube cube,
                          int g,
                          int bound,
                          int lastMove,
                          List<Integer> path) {

    int h = heuristic(cube);
    int f = g + h;

    if (f > bound) {
        return f;
    }

    if (cube.isSolved()) {
        return FOUND;
    }

    int min = Integer.MAX_VALUE;

    for (int m : MOVE_LIST) {

        if (lastMove != -1 && (m / 3) == (lastMove / 3))
            continue;

        CubieCube next = cube.clone();
        next.applyMove(m);
        path.add(m);

        int t = dfsIda(next, g + 1, bound, m, path);

        if (t == FOUND) return FOUND;

        if (t < min) min = t;

        path.remove(path.size() - 1);
    }

    return min;
}

private static List<Integer> idaStar(CubieCube start) {
    List<Integer> path = new ArrayList<>();
    int bound = heuristic(start);

    while (true) {
        int t = dfsIda(start, 0, bound, -1, path);

        if (t == FOUND) {
            return path;
        }

        if (t == Integer.MAX_VALUE) {
            return Collections.emptyList();
        }

        bound = t;  // jump to next bound
        if (bound > 35) {
            return Collections.emptyList();
        }
    }
}


  
//Expand our solution into expanded form 

    private static List<String> expandMoves(List<Integer> sol) {
    List<String> out = new ArrayList<>();

    for (int m : sol) {
        int face = m / 3;    // which face (0..5)
        int type = m % 3;    // 0=normal,1=2-turn,2=prime

        
        // e.g., U, R, F, D, L, B
        String base = MOVE_NAME[face * 3];

        if (type == 0) {
            out.add(base);
        } 
        else if (type == 1) {
            out.add(base);
            out.add(base);
        } 
        else { 
            out.add(base);
            out.add(base);
            out.add(base);
        }
    }

    return out;
}


    //Main function 

   public static void main(String[] args) throws Exception {

    if (args.length < 2) {
        return; 
    }

    String inFile = args[0];
    String outFile = args[1];

    StickerCube sc = StickerCube.fromFile(inFile);
    CubieCube cc = sc.toCubieCube();

    List<Integer> sol = idaStar(cc);

    // write ONLY the solution, nothing else
    try (PrintWriter pw = new PrintWriter(outFile)) {
        List<String> expanded = expandMoves(sol);
        for (String s : expanded) pw.print(s);
    }
}

}

