import { Tile, Position, TileType, Node } from "@/game/model/models";
import { PriorityQueue } from "./priorityqueue";

export class Astar {
  grid: Node[][];

  constructor(map: Tile[][]) {
    this.grid = map.map((line, x) =>
      line.map((t, y) => new Node(x, y, 0, 0, 0, t, false, false, undefined))
    );
  }
  getCost(tile: Tile, seaRoute: boolean): number {
    if (tile.isRoad && !seaRoute) {
      return 1;
    } else if (seaRoute) {
      if (tile.isSeaRoad) {
        return 1;
      } else if (tile.type === TileType.SEA) {
        return 3;
      } else {
        return 10000;
      }
    } else {
      switch (tile.type) {
        case TileType.CITY:
          return 10;
        case TileType.SAND:
        case TileType.SNOW:
          return 100;
        case TileType.FOREST:
          return 70;
        case TileType.GRASS:
          return 30;
        case TileType.ROCK:
        case TileType.SWAMP:
          return 150;
        case TileType.RIVER:
          return 300;
        case TileType.SEA:
          return 2000;
      }
    }
    return 10000;
  }
  heap(): PriorityQueue<Node> {
    return new PriorityQueue<Node>((a, b) => a.f > b.f);
  }

  findPath(start: Position, end: Position, seaRoute: boolean): Node[] {
    const openSet = new Set<Node>();
    const closedSet = new Set<Node>();
    const startNode = this.grid[start.x][start.y];
    const endNode = this.grid[end.x][end.y];
    let currentNode = startNode; // the currentNode, defaults to start node for now
    let tempArray: Node[];
    let newMovementCost; // the new movement cost to neighbor

    openSet.add(currentNode);
    while (openSet.size > 0) {
      tempArray = Array.from(openSet);

      currentNode = tempArray[0];

      for (let i = 1; i < tempArray.length; i++) {
        // this if statement is solely to build the starting walls.
        if (
          tempArray[i].f < currentNode.f ||
          (tempArray[i].f === currentNode.f && tempArray[i].h < currentNode.h)
        ) {
          currentNode = tempArray[i]; // sets the currentNode to openSetI if it has a lower F value, or an = F value with a lower HCost.
        }
      }

      // exits for loop with either lowest F value or combined H value and F value
      openSet.delete(currentNode);
      closedSet.add(currentNode);

      // might need to put this after getNighbors.... then replace closedSet.hasIn(neighborNode with currentNode
      if (currentNode === endNode) {
        if ((!seaRoute || currentNode.g < 100000) && currentNode.parent) {
          return this.retracePath(startNode, endNode);
        } else {
          return [];
        }
      }
      this.neighbors(currentNode).forEach((neighborNode) => {
        const neighborH = neighborNode.h;
        const neighborG = neighborNode.g;
        const currentG = currentNode.g;

        if (
          !closedSet.has(neighborNode) &&
          ((seaRoute && neighborNode.tile.type === TileType.SEA) ||
            (!seaRoute && neighborNode.tile.type !== TileType.SEA))
        ) {
          newMovementCost =
            currentG +
            this.getCost(neighborNode.tile, seaRoute) +
            (seaRoute
              ? 0
              : Math.abs(
                  neighborNode.tile.altitude - currentNode.tile.altitude
                ) * 100) +
            this.manhattan(
              [currentNode.x, currentNode.y],
              [neighborNode.x, neighborNode.y]
            );

          if (newMovementCost < neighborG || !openSet.has(neighborNode)) {
            neighborNode.g = newMovementCost;
            neighborNode.h = neighborH;
            neighborNode.parent = currentNode;

            if (!openSet.has(neighborNode)) {
              // push the neighborNode to the openSet, to check against other open values
              openSet.add(neighborNode);
            }
          }
        }
      });
    }
    return [];
  }

  retracePath(startNode: Node, endNode: Node): Node[] {
    let currentNode = endNode;
    const reverseArray = [];
    while (currentNode !== startNode) {
      reverseArray.push(currentNode);
      if (currentNode.parent) {
        currentNode = currentNode.parent;
      } else {
        return [];
      }
    }
    return reverseArray.reverse();
  }

  manhattan(pos0: [number, number], pos1: [number, number]): number {
    return Math.abs(pos1[0] - pos0[0]) + Math.abs(pos1[1] - pos0[1]);
  }

  neighbors(node: Node): Node[] {
    const ret: Node[] = [];
    const x = node.x;
    const y = node.y;

    // West
    if (this.grid[x - 1] && this.grid[x - 1][y]) {
      ret.push(this.grid[x - 1][y]);
    }

    // East
    if (this.grid[x + 1] && this.grid[x + 1][y]) {
      ret.push(this.grid[x + 1][y]);
    }

    // South
    if (this.grid[x] && this.grid[x][y - 1]) {
      ret.push(this.grid[x][y - 1]);
    }

    // North
    if (this.grid[x] && this.grid[x][y + 1]) {
      ret.push(this.grid[x][y + 1]);
    }
    // Southwest
    if (this.grid[x - 1] && this.grid[x - 1][y - 1]) {
      ret.push(this.grid[x - 1][y - 1]);
    }

    // Southeast
    if (this.grid[x + 1] && this.grid[x + 1][y - 1]) {
      ret.push(this.grid[x + 1][y - 1]);
    }

    // Northwest
    if (this.grid[x - 1] && this.grid[x - 1][y + 1]) {
      ret.push(this.grid[x - 1][y + 1]);
    }

    // Northeast
    if (this.grid[x + 1] && this.grid[x + 1][y + 1]) {
      ret.push(this.grid[x + 1][y + 1]);
    }

    return ret;
  }
}
