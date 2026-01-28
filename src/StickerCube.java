package rubikscube;


import java.io.*;


/**
 * Reads the 9x12 scramble net and converts it into a CubieCube
 * (cp/co/ep/eo). This class does NOT do moves; it just loads the
 * correct layout from the testcase scramble files 
 *
 * Net layout (rows 0..8, cols 0..11):
 *
 *                  U (0..2, 3..5)
 *   L (3..5,0..2)  F (3..5,3..5)  R (3..5,6..8)  B (3..5,9..11)
 *                  D (6..8, 3..5)
 *
 * Faces indices match CubieCube: U=0, R=1, F=2, D=3, L=4, B=5.
 */
public class StickerCube {


    private static final int U = 0;
    private static final int R = 1;
    private static final int F = 2;
    private static final int D = 3;
    private static final int L = 4;
    private static final int B = 5;


    // faces[face][row][col]
    private final char[][][] faces = new char[6][3][3];


    private StickerCube() {}



    // Reading the net from file
    
    public static StickerCube fromFile(String fileName) throws IOException {
        StickerCube sc = new StickerCube();
        char[][] net = new char[9][12];


        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            for (int i = 0; i < 9; i++) {
                String line = br.readLine();
                if (line == null) {
                    throw new IOException("Not enough lines in " + fileName);
                }
                if (line.length() < 12) {
                    line = String.format("%-12s", line); // pad with spaces
                }
                for (int j = 0; j < 12; j++) {
                    net[i][j] = line.charAt(j);
                }
            }
        }


        // U: rows 0..2, cols 3..5
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                sc.faces[U][r][c] = net[r][c + 3];


        // L: rows 3..5, cols 0..2
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                sc.faces[L][r][c] = net[r + 3][c];


        // F: rows 3..5, cols 3..5
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                sc.faces[F][r][c] = net[r + 3][c + 3];


        // R: rows 3..5, cols 6..8
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                sc.faces[R][r][c] = net[r + 3][c + 6];


        // B: rows 3..5, cols 9..11
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                sc.faces[B][r][c] = net[r + 3][c + 9];


        // D: rows 6..8, cols 3..5
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                sc.faces[D][r][c] = net[r + 6][c + 3];


        return sc;
    }


    
    // Debug: print faces 
    //Used during development, making sure every faces are read correctly
    
    public void debugPrint() {
        System.out.println("===== StickerCube Faces =====");
        char[] faceName = {'U','R','F','D','L','B'};
        for (int f = 0; f < 6; f++) {
            System.out.println("Face " + faceName[f] + ":");
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    System.out.print(faces[f][r][c]);
                }
                System.out.println();
            }
            System.out.println();
        }
        System.out.println("============================");
    }


    
    // Conversion to CubieCube
    


    // Corner positions (facelet indices) in URFDLB facelet order
    // Faces: U=0..8, R=9..17, F=18..26, D=27..35, L=36..44, B=45..53

    private static final int[][] CORNER_FACELETS = {
        { 8,  9, 20}, // URF
        { 6, 18, 38}, // UFL
        { 0, 36, 47}, // ULB
        { 2, 45, 11}, // UBR
        {29, 26, 15}, // DFR
        {27, 44, 24}, // DLF
        {33, 53, 42}, // DBL
        {35, 17, 51}  // DRB
    };


    // Which faces each corner type belongs to which orientation

    private static final int[][] CORNER_FACES = {
        {U, R, F}, // 0 URF
        {U, F, L}, // 1 UFL
        {U, L, B}, // 2 ULB
        {U, B, R}, // 3 UBR
        {D, F, R}, // 4 DFR
        {D, L, F}, // 5 DLF
        {D, B, L}, // 6 DBL
        {D, R, B}  // 7 DRB
    };


    // Edge positions (facelet indices)
    private static final int[][] EDGE_FACELETS = {
        { 5, 10}, // UR
        { 7, 19}, // UF
        { 3, 37}, // UL
        { 1, 46}, // UB
        {32, 16}, // DR
        {28, 25}, // DF
        {30, 43}, // DL
        {34, 52}, // DB
        {23, 12}, // FR
        {21, 41}, // FL
        {50, 39}, // BL
        {48, 14}  // BR
    };


    private static final int[][] EDGE_FACES = {
        {U, R}, // UR
        {U, F}, // UF
        {U, L}, // UL
        {U, B}, // UB
        {D, R}, // DR
        {D, F}, // DF
        {D, L}, // DL
        {D, B}, // DB
        {F, R}, // FR
        {F, L}, // FL
        {B, L}, // BL
        {B, R}  // BR
    };


    /**
     * Convert this sticker representation into a CubieCube.
     *We only use centers to represent the colors because they never change
     */
    public CubieCube toCubieCube() {
        CubieCube cc = new CubieCube();


        //  Center colors: which color is "U", "R", etc.
        char[] fc = new char[6];
        for (int f = 0; f < 6; f++) {
            fc[f] = faces[f][1][1];
        }


        //  Build a 54-length facelet array in URFDLB order
        char[] facelets = new char[54];
        for (int f = 0; f < 6; f++) {
            int base = f * 9;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    facelets[base + r * 3 + c] = faces[f][r][c];
                }
            }
        }


        // 3) Define correct colors for each cubie type using centers

        char[][] cornerColor = new char[8][3];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 3; j++) {
                cornerColor[i][j] = fc[ CORNER_FACES[i][j] ];
            }
        }


        char[][] edgeColor = new char[12][2];
        for (int i = 0; i < 12; i++) {
            edgeColor[i][0] = fc[ EDGE_FACES[i][0] ];
            edgeColor[i][1] = fc[ EDGE_FACES[i][1] ];
        }


        // 4) Corners: identify type & orientation at each position
        for (int pos = 0; pos < 8; pos++) {
            char c0 = facelets[ CORNER_FACELETS[pos][0] ];
            char c1 = facelets[ CORNER_FACELETS[pos][1] ];
            char c2 = facelets[ CORNER_FACELETS[pos][2] ];
            char[] cols = {c0, c1, c2};


            int foundType = -1;
            int ori = -1;


            outer:
            for (int type = 0; type < 8; type++) {
                char[] ref = cornerColor[type];


                // Try orientations ori = 0,1,2 such that
                // cols[(ori + k) % 3] == ref[k] for all k
                for (int o = 0; o < 3; o++) {
                    boolean match = true;
                    for (int k = 0; k < 3; k++) {
                        if (cols[(o + k) % 3] != ref[k]) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        foundType = type;
                        ori = o;
                        break outer;
                    }
                }
            }


            if (foundType == -1) {
                throw new IllegalStateException("Invalid corner colors at position " + pos);
            }


            cc.cp[pos] = foundType;
            cc.co[pos] = ori % 3;
        }


        // Edges: identify type & flip at each position
        
        for (int pos = 0; pos < 12; pos++) {
            char c0 = facelets[ EDGE_FACELETS[pos][0] ];
            char c1 = facelets[ EDGE_FACELETS[pos][1] ];


            int foundType = -1;
            int flip = 0;


            for (int type = 0; type < 12; type++) {
                char a0 = edgeColor[type][0];
                char a1 = edgeColor[type][1];


                if (c0 == a0 && c1 == a1) {
                    foundType = type;
                    flip = 0;
                    break;
                } else if (c0 == a1 && c1 == a0) {
                    foundType = type;
                    flip = 1;
                    break;
                }
            }


            if (foundType == -1) {
                throw new IllegalStateException("Invalid edge colors at position " + pos);
            }


            cc.ep[pos] = foundType;
            cc.eo[pos] = flip;
        }


        return cc;
    }
}


