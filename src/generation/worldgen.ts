import {
  Tile,
  Position,
  World,
  City,
  Road,
  TileType,
  Nation,
  Industry,
  Node,
  IndustryName,
  Message,
} from "@/game/model/models";
import HeightMap from "@/generation/mapdisplacement";
import { Utils } from "@/utils";
import { PriorityQueue } from "@/generation/priorityqueue";
import { LanguageGenerator } from "@/generation/language/language.generator";
import { Astar } from "@/generation/astar.service";

const CITY_SCORE_RADIUS = 3;
const NB_RIVERS_BY_64_MAP = 8;
const NB_CITY_BY_64_MAP = 10;
const EMPTY_BORDER = 0.1;
const MAX_DIST_FOR_ROAD = 60;

const nationColors = [
  [30, 255, 30],
  [255, 255, 30],
  [30, 255, 255],
  [255, 30, 255],
  [255, 30, 30],
  [30, 30, 255],
];

const cityColors = [
  [30, 255, 30],
  [255, 255, 30],
  [30, 255, 255],
  [255, 30, 255],
  [255, 30, 30],
  [30, 30, 255],
  [30, 30, 30],
  [200, 200, 200],
];
export class WorldGen {
  public run(data: {
    seaLevel: number;
    mountainLevel: number;
    worldSize: number;
    nbNations: number;
  }): World {
    // build the world!!!
    const world = new World();
    world.nations = [];

    // generate nations
    for (let n = 0; n < data.nbNations; n++) {
      const nation = new Nation(nationColors[n]);
      world.nations.push(nation);
    }

    // terrain
    const nbRivers = Math.floor((data.worldSize * NB_RIVERS_BY_64_MAP) / 64);
    const nbCities = Math.floor((data.worldSize * NB_CITY_BY_64_MAP) / 64);
    this.generateTerrain(
      world,
      data.seaLevel,
      data.mountainLevel,
      data.worldSize,
      nbRivers
    );

    // cities
    this.spawnCities(nbCities, world);

    // roads
    this.buildRoads(world);

    // regions
    this.buildRegions(world);

    return world;
  }

  generateTerrain(
    world: World,
    seaLevel: number,
    mountainLevel: number,
    worldSize: number,
    nbRivers: number
  ): void {
    // get the nearest 2-power for the heighmap generation
    const worldGenSize = Utils.get2PowerAbove(worldSize);
    // first get a height map
    const height = Utils.crop(new HeightMap(worldGenSize).map, worldSize);
    // temperature and humidity maps
    const temp = Utils.crop(new HeightMap(worldGenSize).map, worldSize);
    const humid = Utils.crop(new HeightMap(worldGenSize).map, worldSize);

    const finalMap = height.map((line, x) =>
      line.map((t, y) => {
        const h = Math.floor(t * 255);
        return new Tile(
          this.getTileType(h, seaLevel, mountainLevel, temp, humid, x, y),
          new Position(x, y),
          h
        );
      })
    );

    // remove depressions (holes)
    this.handleHoles(finalMap, seaLevel);

    // filter only high enough tiles
    const mountainLevelTiles = finalMap
      .reduce((acc, val) => acc.concat(val), [])
      .filter((t) => t.altitude >= mountainLevel);

    // make rivers
    for (let r = 0; r < nbRivers; r++) {
      this.makeRiver(finalMap, mountainLevelTiles, `${r}`);
    }

    world.map = finalMap;
  }

  buildRoads(world: World): void {
    // land route
    this.buildRoadsRecursive(world.map, world.cities, false);
    // sea route
    this.buildRoadsRecursive(
      world.map,
      // only cities with SEA next to them
      world.cities.filter((c) => c.port !== undefined),
      true
    );
  }

  buildRoadsRecursive(map: Tile[][], cities: City[], seaRoute: boolean): void {
    // build a road to each city (in a limit of distance)
    const city = cities[0];
    const citiesLeft = cities.slice(1);
    if (citiesLeft.length > 0) {
      citiesLeft.forEach((c) => {
        if (
          Utils.dist(
            city.position.x,
            city.position.y,
            c.position.x,
            c.position.y
          ) < MAX_DIST_FOR_ROAD
        ) {
          // is sea route, start and end point must be on sea
          if (seaRoute) {
            const path = this.findPath(map, city.port, c.port, seaRoute);
            if (path && path.length > 0) {
              path.forEach((p) => (map[p.x][p.y].isSeaRoad = true));
              city.roads.push(
                new Road(city, c, path, path ? path[path.length - 1].g : -1)
              );
              c.roads.push(
                new Road(
                  c,
                  city,
                  path.reverse(),
                  path ? path[path.length - 1].g : -1
                )
              );
            }
          } else {
            const path = this.findPath(
              map,
              city.position,
              c.position,
              seaRoute
            );
            if (path && path.length > 0) {
              path.forEach((p) => (map[p.x][p.y].isRoad = true));
              city.roads.push(new Road(city, c, path, path[path.length - 1].g));
              c.roads.push(
                new Road(c, city, path.reverse(), path[path.length - 1].g)
              );
            }
          }
        }
      });
      // do the same for next city
      this.buildRoadsRecursive(map, citiesLeft, seaRoute);
    }
  }

  findPath(
    map: Tile[][],
    start: Position,
    end: Position,
    seaRoute: boolean
  ): Node[] {
    const astar = new Astar(map);
    return astar.findPath(start, end, seaRoute);
  }

  spawnCities(nbCities: number, world: World): void {
    const map = world.map;
    // spawn cities
    const cities: City[] = [];
    for (let r = 0; r < nbCities; r++) {
      const pos = this.getCitySpot(map, cities);
      map[pos.x][pos.y].type = TileType.CITY;
      cities.push(this.createCityFromSpot(map, pos, "" + r));
    }
    world.cities = cities;
  }

  createCityFromSpot(map: Tile[][], pos: Position, name: string): City {
    // generate industries from what's available
    const scan = Utils.scanAround(map, pos.x, pos.y, 5);

    // population is what is needed plus a little more
    const mag = Utils.randomIntBetween(2, 5);
    let pop = Math.pow(10, mag);
    pop = Math.floor(pop + pop * Math.random());
    const industries = this.generateIndustries(
      scan.biomes,
      Math.floor(Math.log10(pop))
    );
    const c = new City(
      name,
      pop,
      industries,
      pos,
      Utils.randomInArray(cityColors)
    );

    // add port if sea is close
    c.port = Utils.getClosestTileOfType(map, pos.x, pos.y, TileType.SEA, 3);
    // add rivers
    c.rivers = scan.rivers;

    return c;
  }

  buildRegions(world: World): void {
    const queue = new PriorityQueue<{ t: Tile; score: number }>(
      (a, b) => a.score < b.score
    );
    // put the neighbors of all cities in the queue
    world.cities.forEach((city) => {
      // init neighbours
      world.neighbours.set(city, []);
      Utils.getNeighbors(world.map, city.position).forEach((n) => {
        n.region = city;
        queue.push({
          t: n,
          score: this.getMovementScore(
            world.map[city.position.x][city.position.y],
            n
          ),
        });
      });
    });
    // until everything has been put in one region
    while (!queue.isEmpty()) {
      // next is one with lowest score
      const next = queue.pop();
      Utils.getNeighbors(world.map, next.t.position).forEach((n) => {
        if (!n.region) {
          // not yet any region
          n.region = next.t.region;
          queue.push({
            t: n,
            score: next.score + this.getMovementScore(next.t, n),
          });
        } else if (n.region !== next.t.region) {
          next.t.isFrontier = true;
          n.isFrontier = true;
          // mark the regions as neighbours
          if (world.neighbours.get(next.t.region)?.indexOf(n.region) === -1) {
            world.neighbours.get(next.t.region)?.push(n.region);
          }
          if (world.neighbours.get(n.region)?.indexOf(next.t.region) === -1) {
            world.neighbours.get(n.region)?.push(next.t.region);
          }
        }
      });
    }
    // now choose nations for each
    const cityQueue = new PriorityQueue<City>(
      (a, b) => a.population > b.population
    );
    world.nations.forEach((nation) => {
      const city = Utils.randomInArray(
        world.cities.filter((c) => c.nation === undefined)
      );
      if (city) {
        city.nation = nation;
      }
    });
    world.cities
      .filter((c) => c.nation !== undefined)
      .forEach((city) => {
        world.neighbours.get(city)?.forEach((neigh) => {
          if (!neigh.nation) {
            neigh.nation = city.nation;
            cityQueue.push(neigh);
          }
        });
      });
    while (!cityQueue.isEmpty()) {
      const nextCity = cityQueue.pop();
      world.neighbours.get(nextCity)?.forEach((neigh) => {
        if (!neigh.nation) {
          neigh.nation = nextCity.nation;
          cityQueue.push(neigh);
        }
      });
    }
    world.cities.forEach((c) => {
      c.name = c.nation.lang.generateName("city");
      c.rivers = c.rivers.map((r) =>
        this.nameRiver(r, c.nation.lang, world.map)
      );
    });
  }

  getMovementScore(from: Tile, to: Tile): number {
    return (
      1 +
      (to.riverName ? 2 : 0) +
      (from.altitude - to.altitude) +
      (to.type === TileType.SEA ? 5 : 0)
    );
  }

  nameRiver(riverId: string, lang: LanguageGenerator, map: Tile[][]): string {
    if (!isNaN(Number(riverId))) {
      // river is a number, it needs a name
      const name = lang.generateName("river");
      // change the name everywhere
      map.forEach((line) =>
        line.forEach((t) => {
          if (t.riverName === riverId) {
            t.riverName = name;
          }
        })
      );
      return name;
    } else {
      return riverId;
    }
  }

  generateIndustries(biomes: string[], nbIndustry: number): IndustryName[] {
    let availableIndustries: IndustryName[] = [];
    biomes.forEach((b) => {
      switch (b) {
        case TileType.FOREST:
          availableIndustries.push("Woodcutting");
          break;
        case TileType.ROCK:
          availableIndustries.push("Stone");
          availableIndustries.push("Metal");
          break;
        case TileType.GRASS:
          availableIndustries.push("Farm");
          availableIndustries.push("Cattle");
          availableIndustries.push("Horse");
          availableIndustries.push("Cotton");
          break;
      }
    });
    availableIndustries.push("Blacksmith");
    availableIndustries.push("Machinery");
    availableIndustries.push("Goods");

    // get random industries from the available list
    const ind: IndustryName[] = [];
    while (ind.length < nbIndustry && availableIndustries.length > 0) {
      const random = Utils.randomInArray(availableIndustries);
      availableIndustries = availableIndustries.filter((i) => i !== random);
      ind.push(random);
    }

    return ind;
  }

  getCitySpot(map: Tile[][], cities: City[]): Position {
    const pos = new Position(0, 0);
    let maxScore = -10000;
    map.forEach((line, x) =>
      line.forEach((t, y) => {
        const score = this.computeCityScore(map, x, y, cities);
        if (score > maxScore) {
          maxScore = score;
          pos.x = x;
          pos.y = y;
        }
      })
    );
    return pos;
  }

  computeCityScore(
    map: Tile[][],
    x: number,
    y: number,
    cities: City[]
  ): number {
    // no city on already city, on the sea or on the border of the map
    if (
      map[x][y].type === TileType.CITY ||
      map[x][y].type === TileType.SEA ||
      x < map.length * EMPTY_BORDER ||
      x > map.length * (1 - EMPTY_BORDER) ||
      y < map[0].length * EMPTY_BORDER ||
      y > map[0].length * (1 - EMPTY_BORDER)
    ) {
      return 0;
    }
    // get distance to nearest city
    let minDist = 100000;
    cities.forEach((c) => {
      const d = Utils.dist(x, y, c.position.x, c.position.y);
      if (d < minDist) {
        minDist = d;
      }
    });
    // get water flux in here
    const scan = Utils.scanAround(map, x, y, CITY_SCORE_RADIUS);
    // different biomes around
    // combine
    return minDist * 5 + scan.rivers.length * 10 + scan.biomes.length * 10;
  }

  getTileType(
    h: number,
    seaLevel: number,
    mountainLevel: number,
    temp: number[][],
    humid: number[][],
    x: number,
    y: number
  ): string {
    let type = TileType.GRASS;
    if (h > mountainLevel) {
      type = TileType.ROCK;
    } else if (h < seaLevel) {
      type = TileType.SEA;
    } else if (h < seaLevel + 6) {
      type = TileType.SAND;
    } else if (temp[x][y] > 0.7 && humid[x][y] > 0.5) {
      type = TileType.SWAMP;
    } else if (temp[x][y] > 0.7 && humid[x][y] <= 0.5) {
      type = TileType.SAND;
    } else if (temp[x][y] <= 0.7 && humid[x][y] > 0.5) {
      type = TileType.FOREST;
    } else if (temp[x][y] <= 0.3) {
      type = TileType.SNOW;
    }
    return type;
  }

  makeRiver(map: Tile[][], mountainLevelTiles: Tile[], name: string): void {
    // take one tile at random in mountainlevel tiles
    const t = Utils.randomInArray(mountainLevelTiles);
    // flow until touching water level
    let finished = false;
    let current = t;
    let waterFlow = 1;
    let steps = 0;
    while (!finished && steps < 10000) {
      steps++;
      current.type = TileType.RIVER;
      if (!current.riverName) {
        current.riverName = name;
      }
      current.waterFlow += waterFlow++; // add flow to point's existing flow and increment flow
      const next = this.getRandomLowerNeighbor(map, current);
      if (!next) {
        break;
      }
      current = next;
      finished = current.type === TileType.SEA;
    }
  }

  handleHoles(map: Tile[][], seaLevel: number): void {
    // algorithm to remove holes from the map (but kkep track of them to put them in water for LAKES)
    const open = new PriorityQueue<Tile>((a, b) => a.altitude < b.altitude);
    const pit: Tile[] = [];
    const closed = map.map((line, x) => line.map((t, y) => false));

    // init the edges
    for (let i = 0; i < map.length; i++) {
      open.push(map[i][0]);
      closed[i][0] = true;
      open.push(map[i][map[i].length - 1]);
      closed[i][map[i].length - 1] = true;
    } // lines
    for (let j = 0; j < map.length; j++) {
      open.push(map[0][j]);
      closed[0][j] = true;
      open.push(map[map.length - 1][j]);
      closed[map.length - 1][j] = true;
    } // columns
    // DO IT
    while (!open.isEmpty() || pit.length > 0) {
      let current: Tile;
      if (pit.length > 0) {
        current = pit.shift()!;
      } else {
        current = open.pop();
      }
      Utils.getNeighbors(map, current.position).forEach((n) => {
        if (!closed[n.position.x][n.position.y]) {
          // mark as done
          closed[n.position.x][n.position.y] = true;
          if (n.altitude <= current.altitude && current.altitude > seaLevel) {
            n.altitude = current.altitude;
            pit.push(n);
            // mark pit
            n.wasHole = true;
          } else {
            open.push(n);
          }
        } // else already done, continue...
      });
    }
  }

  getRandomLowerNeighbor(map: Tile[][], tile: Tile): Tile {
    const neigh = Utils.getNeighbors(map, tile.position);
    // check in strictly lower
    const lower = neigh.filter((ti) => ti.altitude < tile.altitude);
    let t: Tile;
    if (lower.length > 0) {
      // take in priority the one aligned with the river current direction
      t = Utils.randomInArray(lower);
    } else {
      // else check in same alt
      const same = neigh.filter((n) => n.altitude === tile.altitude);
      t = Utils.randomInArray(same);
    }
    return t;
  }
}
