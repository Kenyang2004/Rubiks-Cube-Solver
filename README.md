# Rubiks-Cube-Solver

This is an efficient Rubik’s Cube solver implemented in Java using a cubie-based representation and IDA* search with admissible heuristics.

This project was developed as part of CMPT 225 to explore state-space search, heuristics, and efficient representations for solving combinatorial puzzles.

Initially, a sticker-based representation was implemented, but updating 54 facelets per move resulted in a large branching factor and high memory usage. The final solver uses a cubie-based representation, tracking corner and edge permutations and orientations, significantly improving performance and scalability.

## Core Classes

- **StickerCube** – 54-sticker representation for input parsing and visualization
- **CubieCube** – Corner and edge permutations/orientations; main internal representation
- **Solver** – Implements IDA* search and admissible heuristics

## Algorithms

- Iterative Deepening A* (IDA*)
- Depth-first search with heuristic pruning

## Heuristics

- Cubie misplacement and misorientation heuristic (admissible)
    1 = (misplaced corners + misoriented corners + misplaced edges + misoriented edges) / 4
  
- Corner Manhattan distance heuristic
    Each corner has a 3D coordinate in the solved cube (from CORNER_POS)
    Computes Manhattan distance between current and goal positions for each corner
    Sum all distances, divide by 4 (quarter-turn metric)
  
- Edge Manhattan distance heuristic
    Same concept as H2 but for edges using EDGE_POS
    Sum distances, divide by 4

## Current Limitations

- Currently solves 14 of 40 test cases
- Solver is optimized for moderate scrambles; very complex scrambles may exceed time constraints


- ## Future Improvements

- Pattern Database (PDB) heuristics
- Two-phase solving approach
- Additional pruning rules




