package rubikscube;

//This class is to transform the sticker based net layout into cubie based representation

public class CubieCube {


    // Corner permutation and orientation
    public int[] cp = new int[8];
    public int[] co = new int[8];


    // Edge permutation and orientation
    public int[] ep = new int[12];
    public int[] eo = new int[12];

    static final int[][] CORNER_POS = {
        {0,2,2}, // 0
        {2,2,2}, // 1
        {2,0,2}, // 2
        {0,0,2}, // 3
        {0,2,0}, // 4
        {2,2,0}, // 5
        {2,0,0}, // 6
        {0,0,0}  // 7
};

static final int[][] EDGE_POS = {
        {1,2,2}, // 0
        {2,2,1}, // 1
        {1,2,0}, // 2
        {0,2,1}, // 3
        {1,0,2}, // 4
        {2,0,1}, // 5
        {1,0,0}, // 6
        {0,0,1}, // 7
        {2,1,2}, // 8
        {2,1,0}, // 9
        {0,1,0}, // 10
        {0,1,2}  // 11
};


    // Constructors

    //** Solved cube */

    public CubieCube() {
        for (int i = 0; i < 8; i++) {
            cp[i] = i;
            co[i] = 0;
        }
        for (int i = 0; i < 12; i++) {
            ep[i] = i;
            eo[i] = 0;
        }
    }


    /** Deep copy */
    public CubieCube(CubieCube c) {
        for (int i = 0; i < 8; i++) {
            cp[i] = c.cp[i];
            co[i] = c.co[i];
        }
        for (int i = 0; i < 12; i++) {
            ep[i] = c.ep[i];
            eo[i] = c.eo[i];
        }
    }


    @Override
    public CubieCube clone() { return new CubieCube(this); }


    
    // Orientation constraint checks
    
    public boolean orientationConstraintOK() {
        int s = 0;
        for (int x : co) {
            if (x < 0 || x > 2) return false;
            s += x;
        }
        if (s % 3 != 0) return false;


        int e = 0;
        for (int x : eo) {
            if (x < 0 || x > 1) return false;
            e += x;
        }
        return e % 2 == 0;
    }

    //Check if the cube is solved
    public boolean isSolved() {
        for (int i = 0; i < 8; i++)
            if (cp[i] != i || co[i] != 0) return false;
        for (int i = 0; i < 12; i++)
            if (ep[i] != i || eo[i] != 0) return false;
        return true;
    }



    // Debug print
    //We only called it for debugging use
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CORNERS:\n");
        for (int i = 0; i < 8; i++) {
            sb.append(" pos ").append(i)
              .append(": cubie=").append(cp[i])
              .append(" ori=").append(co[i]).append("\n");
        }
        sb.append("EDGES:\n");
        for (int i = 0; i < 12; i++) {
            sb.append(" pos ").append(i)
              .append(": cubie=").append(ep[i])
              .append(" ori=").append(eo[i]).append("\n");
        }
        return sb.toString();
    }


    // Cycle helpers

    private void cycleCorners(int a,int b,int c,int d,
                              int oa,int ob,int oc,int od)
    {
        int tc = cp[a], to = co[a];


        cp[a] = cp[d]; co[a] = (co[d] + oa) % 3;
        cp[d] = cp[c]; co[d] = (co[c] + od) % 3;
        cp[c] = cp[b]; co[c] = (co[b] + oc) % 3;
        cp[b] = tc;    co[b] = (to    + ob) % 3;
    }


    private void cycleEdges(int a,int b,int c,int d,
                            int fa,int fb,int fc,int fd)
    {
        int te = ep[a], to = eo[a];


        ep[a] = ep[d]; eo[a] = eo[d] ^ fa;
        ep[d] = ep[c]; eo[d] = eo[c] ^ fd;
        ep[c] = ep[b]; eo[c] = eo[b] ^ fc;
        ep[b] = te;    eo[b] = to    ^ fb;
    }


 
    // Moves

    private void moveU() {
        cycleCorners(0,1,2,3, 0,0,0,0);
        cycleEdges(0,1,2,3, 0,0,0,0);
    }


    private void moveR() {
        cycleCorners(0,3,7,4, 2,1,2,1);
        cycleEdges(0,11,4,8, 0,0,0,0);
    }


    private void moveF() {
        cycleCorners(0,1,5,4, 1,2,1,2);
        cycleEdges(1,9,5,8, 1,1,1,1);
    }


    private void moveD() {
        cycleCorners(4,7,6,5, 0,0,0,0);
        cycleEdges(4,7,6,5, 0,0,0,0);
    }


    private void moveL() {
        cycleCorners(1,2,6,5, 2,1,2,1);
        cycleEdges(2,10,6,9, 0,0,0,0);
    }


    private void moveB() {
        cycleCorners(2,3,7,6, 1,2,1,2);
        cycleEdges(3,11,7,10, 1,1,1,1);
    }


    
    // 18-move metric:
    //
    // 0: U   1: U2  2: U'
    // 3: R   4: R2  5: R'
    // 6: F   7: F2  8: F'
    // 9: D  10: D2 11: D'
    // 12: L 13: L2 14: L'
    // 15: B 16: B2 17: B'
    


    public void applyMove(int m) {
        switch (m) {
            case 0: moveU(); break;
            case 1: moveU(); moveU(); break;
            case 2: moveU(); moveU(); moveU(); break;


            case 3: moveR(); break;
            case 4: moveR(); moveR(); break;
            case 5: moveR(); moveR(); moveR(); break;


            case 6: moveF(); break;
            case 7: moveF(); moveF(); break;
            case 8: moveF(); moveF(); moveF(); break;


            case 9: moveD(); break;
            case 10: moveD(); moveD(); break;
            case 11: moveD(); moveD(); moveD(); break;


            case 12: moveL(); break;
            case 13: moveL(); moveL(); break;
            case 14: moveL(); moveL(); moveL(); break;


            case 15: moveB(); break;
            case 16: moveB(); moveB(); break;
            case 17: moveB(); moveB(); moveB(); break;


            default:
                throw new IllegalArgumentException("Bad move index " + m);
        }
    }


   

    public static boolean selfTest() {
        boolean ok = true;


        // Orientation constraint must hold for solved cube
        if (!new CubieCube().orientationConstraintOK()) {
            System.out.println("[FAIL] orientation constraint on solved cube");
            ok = false;
        }


        // Face^4 should return to solved cube
        int[] basic = {0,3,6,9,12,15};
        String[] names = {"U","R","F","D","L","B"};


        for (int i = 0; i < 6; i++) {
            CubieCube c = new CubieCube();
            for (int k = 0; k < 4; k++)
                c.applyMove(basic[i]);


            if (!c.isSolved()) {
                System.out.println("[FAIL] " + names[i] + "^4 != identity");
                ok = false;
            }
        }


        if (ok)
            System.out.println("[SELFTEST] CubieCube mapping OK.");
        else
            System.out.println("[SELFTEST] CubieCube mapping has ERRORS.");


        return ok;
    }

    //USed for debug

    public static void main(String[] args) {
        selfTest();
    }
}







