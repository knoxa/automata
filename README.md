# Cellular automata

Playing around with self-organization and evolutionary algorithms.

## Cells

The simplest unit is the *Square* in the **cells** package.
Its only behaviour is to connect to, or disconnect from, a neighbouring square on any one of its four sides.
*Sense* objects mediate between a *Square* and its environment.

The **worlds** package provides an environment.
The *Compass* class is used to find reverse and orthogonal directions.
The *Board* class places squares on a grid.

## Tiles

I want to connect a set of squares to form a *tile*.
A tile is simply a set of connected squares rather than an explicit construct.
The *Partioner.partion()* method divides a set of squares into tiles based on their neighbour relationships.

I want the squares in a tile to be fully connected, so that connections between tiles are bidirectional
and each square links to all its neighbours if laid out on a grid.

The operations that enforce these rules act on a source and target squares:

* **ATTACH**: Link source to target and target to source. If the target is part of a tile, then other squares in the two tiles are linked if they touch.
This operation fails if it would cause tiles to overlap.
* **DETACH**: Remove a square from a tile by clearing all of its neighbours.
* **CAPTURE**: This is equivalent to DETACH from target followed by ATTACH to source.
* **DEFECT**: This is equivalent to DETACH from source followed by ATTACH to target.

## Pentominoes
