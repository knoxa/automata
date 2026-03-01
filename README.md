# Cellular automata

The simplest unit is the *Square* in the **cells** package.
Its only behaviour is to connect to, or disconnect from, a neighbouring square on any one of its four sides.
*Sense* objects mediate between a *Square* and its environment.
The *Compass* class is used to find reverse and orthogonal directions.

I want to connect a set of squares to form a *tile*.
I want the squares in a tile to be fully connected, so that connections between tiles are bidirectional,
and each square links to all its neighbours if laid out on a grid.

The operations that enforce these rules act on a source and target square:

* **ATTACH**: Link source to target and target to source. If the target is part of a tile, link to any other squares adjacent to the newly attached square.
* **DETACH**: Remove a square from a tile by clearing all of its neighbours.
* **CAPTURE**: This is equivalent to DETACH from target followed by ATTACH to source.
* **DEFECT**: This is equivalent to DETACH from source followed by ATTACH to target.
