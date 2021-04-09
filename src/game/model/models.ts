import { LanguageGenerator } from "@/generation/language/language.generator";

export class World {
  public cities!: City[];
  public rivers!: string[];
  public people!: People[];
  public factions!: Faction[];
  public map!: Tile[][];
  public nations!: Nation[];
  public neighbours = new Map<City, City[]>();
  public day = 0;
  public refreshLayer = "";
  public caravans = new Map<number, Caravan>();
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
    switch(tileType) {
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
  public riverName!: string;
  public region!: City;
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
  public lang = new LanguageGenerator();
  public name: string;

  constructor(public color: number[]) {
    this.name = this.lang.generateName("city");
  }
}

export class City {
  public port!: Position;
  public rivers: string[] = [];
  public roads: Road[] = [];
  public nation!: Nation;
  public access = 5;
  public stability = 0;
  public growth = 0;
  public production = new Map<Resource, number>();
  public resources = new Map<Resource, number>();
  public deficits = new Map<Resource, number>();
  public needs = new Map<Resource, number>();
  public caravans: Caravan[] = [];

  constructor(
    public name: string,
    public population: number,
    public industries: IndustryName[],
    public position: Position,
    public color: number[]
  ) {}
}

export class Road {
  constructor(
    public from: City,
    public to: City,
    public path: Node[],
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

export class Caravan {
  static nextId = 0;
  public id: number;
  public progress = 0;
  public speed = 1;

  constructor(
    public name: string,
    public trade: number,
    public guard: number,
    public stealth: number,
    public nbPeople: number,
    public position: Position,
    public nation: Nation,
    public route: Road,
    public resourceGo: Resource,
    public resourceBack: Resource,
    public stock: number
  ) {
    this.id = Caravan.nextId++;
  }
}

export class Mission {
  constructor(public faction: Faction, public targets: Target[]) {}
}

export class People {
  constructor(
    public name: string,
    public trade: number,
    public guard: number,
    public stealth: number,
    public nbPeople: number,
    public reputation: Map<Faction, number>,
    public position: Position
  ) {}
}

export class Faction {
  constructor(
    public name: string,
    public reputation: Map<Faction, number>,
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
