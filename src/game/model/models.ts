import { LanguageGenerator } from "@/generation/language/language.generator";

export class World {
  public cities!: Map<number, City>;
  public rivers!: string[];
  public people!: Map<number, People>;
  public factions!: Map<number, Faction>;
  public map!: Tile[][];
  public nations!: Map<number, Nation>;
  public neighbours = new Map<number, number[]>();
  public day = 0;
}

export class TileType {
  public static DIRT = "dirt";
  public static ROCK = "rock";
  public static GRASS = "grass";
  public static FOREST = "forest";
  public static RIVER = "river";
  public static SWAMP = "swamp";
  public static SNOW = "snow";
  public static SAND = "sand";
  public static CITY = "city";
  public static SEA = "sea";

  public static ALL_TYPES = [
    TileType.DIRT,
    TileType.ROCK,
    TileType.GRASS,
    TileType.FOREST,
    TileType.RIVER,
    TileType.SWAMP,
    TileType.SNOW,
    TileType.SAND,
    TileType.CITY,
    TileType.SEA,
  ];

  public static getTileSetId(tileType: string): number {
    switch (tileType) {
      case TileType.CITY:
        return 0;
      case TileType.DIRT:
        return 1;
      case TileType.FOREST:
        return 2;
      case TileType.GRASS:
        return 3;
      case TileType.RIVER:
        return 4;
      case TileType.ROCK:
        return 5;
      case TileType.SAND:
        return 6;
      case TileType.SEA:
        return 7;
      case TileType.SNOW:
        return 8;
      case TileType.SWAMP:
        return 9;
    }
    return 7; // Default is SEA
  }
}

export class Tile {
  public wasHole = false;
  public isRoad = false;
  public isSeaRoad = false;
  public waterFlow = 0;
  public riverName = "";
  public cityId = -1;
  public isFrontier = false;

  constructor(
    public type: string,
    public position: Position,
    public altitude: number
  ) {}
}

export enum Resource {
  WOOD = 1,
  METAL = 2,
  TOOLS = 3,
  MACHINE = 4,
  GOODS = 5,
  FOOD = 6,
  CATTLE = 7,
  STONE = 8,
  HORSE = 9,
  COTTON = 10,
}
export const allResources = [
  Resource.CATTLE,
  Resource.COTTON,
  Resource.FOOD,
  Resource.GOODS,
  Resource.HORSE,
  Resource.MACHINE,
  Resource.METAL,
  Resource.STONE,
  Resource.TOOLS,
  Resource.WOOD,
];

export class Node {
  constructor(
    public x: number,
    public y: number,
    public f: number,
    public g: number,
    public h: number,
    public tile: Tile,
    public visited = false,
    public closed = false,
    public parent?: Node
  ) {}
}
export class Nation {
  private static nextNationId = 0;
  public id = Nation.nextNationId++;
  public lang = new LanguageGenerator();
  public name!: string;

  constructor(public color: number[]) {}

  public init(): void {
    this.lang.init();
    this.name = this.lang.generateName("city");
  }
}

export class City {
  static nextCityId = 0;
  public port!: Position;
  public rivers: string[] = [];
  public roads: Road[] = [];
  public nation!: number;
  public access = 5;
  public stability = 0;
  public growth = 0;
  public production = new Map<Resource, number>();
  public resources = new Map<Resource, number>();
  public deficits = new Map<Resource, number>();
  public needs = new Map<Resource, number>();
  public id = City.nextCityId++;

  constructor(
    public name: string,
    public population: number,
    public industries: IndustryName[],
    public position: Position,
    public color: number[]
  ) {}
}

export class Road {
  static nextRoadId = 0;
  public id = Road.nextRoadId++;
  constructor(
    public from: number,
    public to: number,
    public path: Position[],
    public cost: number
  ) {}
}

export class Demand {
  constructor(
    public resource: Resource,
    public mag: (size: number) => number
  ) {}
}

export type IndustryName =
  | "Woodcutting"
  | "Metal"
  | "Stone"
  | "Farm"
  | "Cotton"
  | "Cattle"
  | "Horse"
  | "Blacksmith"
  | "Machinery"
  | "Goods";

export class Industry {
  constructor(
    public name: string,
    public needs: Demand[],
    public produces: Demand[]
  ) {}

  static industries = new Map<IndustryName, Industry>([
    [
      "Woodcutting",
      new Industry(
        "Woodcutting",
        [new Demand(Resource.TOOLS, (s) => Math.max(0, s - 1))],
        [new Demand(Resource.WOOD, (s) => s + 1)]
      ),
    ],
    [
      "Metal",
      new Industry(
        "Metal",
        [new Demand(Resource.TOOLS, (s) => s)],
        [new Demand(Resource.METAL, (s) => s)]
      ),
    ],
    [
      "Stone",
      new Industry(
        "Stone",
        [new Demand(Resource.TOOLS, (s) => Math.max(0, s - 1))],
        [new Demand(Resource.STONE, (s) => s)]
      ),
    ],
    [
      "Farm",
      new Industry(
        "Farm",
        [
          new Demand(Resource.MACHINE, (s) => Math.max(0, s - 1)),
          new Demand(Resource.CATTLE, (s) => Math.max(0, s - 1)),
        ],
        [new Demand(Resource.FOOD, (s) => s + 1)]
      ),
    ],
    [
      "Cotton",
      new Industry(
        "Cotton",
        [new Demand(Resource.TOOLS, (s) => Math.max(0, s - 1))],
        [new Demand(Resource.COTTON, (s) => s + 1)]
      ),
    ],
    [
      "Cattle",
      new Industry(
        "Cattle",
        [new Demand(Resource.FOOD, (s) => Math.max(0, s - 1))],
        [new Demand(Resource.CATTLE, (s) => s)]
      ),
    ],
    [
      "Horse",
      new Industry(
        "Horse",
        [new Demand(Resource.FOOD, (s) => s)],
        [new Demand(Resource.HORSE, (s) => s)]
      ),
    ],
    [
      "Goods",
      new Industry(
        "Goods",
        [
          new Demand(Resource.COTTON, (s) => s),
          new Demand(Resource.MACHINE, (s) => Math.max(0, s - 1)),
        ],
        [new Demand(Resource.GOODS, (s) => s + 1)]
      ),
    ],
    [
      "Blacksmith",
      new Industry(
        "Blacksmith",
        [
          new Demand(Resource.MACHINE, (s) => Math.max(0, s - 1)),
          new Demand(Resource.METAL, (s) => s),
        ],
        [new Demand(Resource.TOOLS, (s) => s + 1)]
      ),
    ],
    [
      "Machinery",
      new Industry(
        "Machinery",
        [
          new Demand(Resource.TOOLS, (s) => Math.max(0, s - 1)),
          new Demand(Resource.WOOD, (s) => s + 1),
        ],
        [new Demand(Resource.MACHINE, (s) => s)]
      ),
    ],
  ]);
}

export class People {
  static nextPeopleId = 0;
  public id = People.nextPeopleId++;
  constructor(
    public name: string,
    public stats: Map<string, number>,
    public reputation: Map<number, number>,
    public relations: Relation[],
    public location: number
  ) {}
}

export class Relation {
  constructor(
    public to: number,
    public mag: number,
    public reason: string,
    public description: string
  ) {}
}

export class Faction {
  static nextFactionId = 0;
  public id = Faction.nextFactionId++;
  constructor(
    public name: string,
    public reputation: Map<number, number>,
    public wealth: number,
    public members: number
  ) {}
}

export class Position {
  constructor(public x: number, public y: number) {}
}

export interface Message {
  msg?: string;
  world?: World;
  progress: number;
  type: string;
}

export type Target = People | City | Faction;
