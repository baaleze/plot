import { Position, Tile } from "@/game/model/models";

export class Utils {
  static samePosition(p1: Position, p2: Position): boolean {
    return p1.x === p2.x && p1.y === p2.y;
  }
  static randomInArray<T>(arr: T[]): T {
    return arr[Math.floor(Math.random() * arr.length)];
  }

  static randomIntBetween(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }

  static dist(x1: number, y1: number, x2: number, y2: number): number {
    return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
  }

  static randomInWeightedMap<T>(map: Map<T, number>): T {
    // get sum
    let sum = 0;
    map.forEach((v) => (sum += v));
    const random = Math.floor(Math.random() * sum);
    let cumul = 0;
    let res!: T;
    map.forEach((v, k) => {
      cumul += v;
      if (!res && cumul > random) {
        res = k;
      }
    });
    return res;
  }

  static getClosestTileOfType(
    map: Tile[][],
    x: number,
    y: number,
    type: string,
    radius: number
  ): Position {
    let pos!: Position;
    let minDist = 1000;
    for (let i = x - radius; i < x + radius; i++) {
      for (let j = y - radius; j < y + radius; j++) {
        if (i >= 0 && i < map.length && j >= 0 && j < map[x].length) {
          // bounds
          // get tile
          if (map[i][j].type === type && Utils.dist(x, y, i, j) < minDist) {
            pos = {x: i, y: j};
            minDist = Utils.dist(x, y, i, j);
          }
        }
      }
    }
    return pos;
  }

  static scanAround(
    map: Tile[][],
    x: number,
    y: number,
    radius: number
  ): { rivers: string[]; biomes: string[] } {
    const biomes: string[] = [];
    const rivers: string[] = [];
    for (let i = x - radius; i < x + radius; i++) {
      for (let j = y - radius; j < y + radius; j++) {
        if (i >= 0 && i < map.length && j >= 0 && j < map[x].length) {
          // bounds
          // get tile
          const tile = map[i][j];
          if (biomes.indexOf(tile.type) === -1) {
            biomes.push(tile.type);
          }
          if (rivers.indexOf(tile.riverName) === -1 && tile.riverName) {
            rivers.push(tile.riverName);
          }
        }
      }
    }
    return { rivers, biomes };
  }

  static getNeighbors(map: Tile[][], pos: Position): Tile[] {
    const neigh: Tile[] = [];
    for (let x = pos.x - 1; x <= pos.x + 1; x++) {
      for (let y = pos.y - 1; y <= pos.y + 1; y++) {
        if (
          x >= 0 &&
          x < map.length &&
          y >= 0 &&
          y < map[x].length && // bounds
          !(x === pos.x && y === pos.y)
        ) {
          // do not include self
          neigh.push(map[x][y]);
        }
      }
    }
    return neigh;
  }

  /**
   * Scale or magnitude of the city. Equal to the nearest power of 10 of the population.
   * (1530 is 4, 23 is 2, 554984 is 6...)
   */
  static getMag(amount: number): number {
    return Math.floor(Math.log10(amount));
  }

  static colorString(color: number[]): string {
    return color.length === 3
      ? `rgb(${color[0]}, ${color[1]}, ${color[2]})`
      : `rgb(${color[0]}, ${color[1]}, ${color[2]}, ${color[3]})`;
  }

  static rgbToHex(rgb: number[]) {
    return (1 << 24) + (rgb[0] << 16) + (rgb[1] << 8) + rgb[2];
  }

  static copyPosition(pos: Position): Position {
    return {x: pos.x, y: pos.y};
  }

  static get2PowerAbove(x: number): number {
    let pow = 2;
    while (pow < x) {
      pow *= 2;
    }
    return pow;
  }

  static crop<T>(matrix: T[][], size: number): T[][] {
    if (matrix.length > size) {
      // remove elements from each column then remove columns
      return matrix.map((col) => col.slice(0, size)).slice(0, size);
    } else {
      return matrix;
    }
  }

  static check(baseDifficulty: number, mod: number): boolean {
    const percent = baseDifficulty + mod;
    if (percent >= 10) {
      return true;
    } else if (percent <= 0) {
      return false;
    } else {
      return Utils.randomIntBetween(0, 10) <= percent;
    }
  }
}
