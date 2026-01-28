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
- Corner Manhattan distance heuristic
- Edge Manhattan distance heuristic

- ## Future Improvements

- Pattern Database (PDB) heuristics
- Two-phase solving approach
- Additional pruning rules


