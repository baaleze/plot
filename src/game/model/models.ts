import { LanguageGenerator } from "@/generation/language/language.generator";
import { Utils } from "@/utils";

export class World {
  public cities!: Map<number, City>;
  public rivers!: string[];
  public people!: Map<number, People>;
  public factions!: Map<number, Faction>;
  public map!: Tile[][];
  public nations!: Map<number, Nation>;
  public neighbours = new Map<number, number[]>();
  public week = 0;
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
  // factions
  public council!: number;
  public army!: number;
  public spies!: number;
  public bandits: number[] = [];

  constructor(public color: number[]) {}

  public init(): void {
    this.lang.init();
    this.name = this.lang.generateName("city");
  }
}

export class City {
  static nextCityId = 0;
  // surroundings
  public port!: Position;
  public rivers: string[] = [];
  public roads: Road[] = [];
  // nation
  public nation!: number;
  // factions
  public guard!: number;
  public council!: number;
  public spies!: number;
  public mercenaries: number[] = [];
  public merchantGuild!: number;
  public builderGuild!: number;
  public mafia!: number;
  public gangs: number[] = [];
  public syndicates = new Map<IndustryName, number>();
  // stats
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
export const ALL_INDUSTRIES: IndustryName[] = [
  "Woodcutting",
  "Metal",
  "Stone",
  "Farm",
  "Cotton",
  "Cattle",
  "Horse",
  "Blacksmith",
  "Machinery",
  "Goods",
];

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
    public stats: Map<Stat, number>,
    public reputation: Map<number, number>, // reputation with factions
    public relations: Relation[],
    public location: number,
    public goals: Goal[],
    public currentAction: Action | undefined,
    public projects: Project[],
    public secrets: Secret[],
    public origin: number, // nation origin id
    public job: Job,
    public birthDay: [number, number, number],
    public gender: Gender,
    public willMarry: Gender[]
  ) {}

  stat(name: Stat): number {
    const s = this.stats.get(name);
    return s !== undefined ? s : 0;
  }
}

export type Gender = "M" | "F" | "NB";

export type Job =
  | "Carpenter"
  | "Smith"
  | "Noble"
  | "Farmer"
  | "Cleric"
  | "Butcher"
  | "Baker"
  | "Cook"
  | "Tanner"
  | "Builder"
  | "Mason"
  | "Guard"
  | "Soldier"
  | "Miner"
  | "Horse Caretaker"
  | "Peasant"
  | "Poet"
  | "Innkeeper"
  | "Engineer"
  | "Merchant"
  | "Thug";
export type BodyStat =
  | "Strengh"
  | "Dexterity"
  | "Intelligence"
  | "Charisma"
  | "Perception"
  | "Constitution";
export type CharacterStat =
  | "Jealous"
  | "Greedy"
  | "Angry"
  | "Violent"
  | "Paranoid"
  | "Sociopath"
  | "Unforgiving"
  | "Ambitious";
export type Stat = BodyStat | CharacterStat;
export const ALL_JOBS: Job[] = [
  "Carpenter",
  "Smith",
  "Noble",
  "Farmer",
  "Cleric",
  "Butcher",
  "Baker",
  "Cook",
  "Tanner",
  "Builder",
  "Mason",
  "Guard",
  "Soldier",
  "Miner",
  "Horse Caretaker",
  "Peasant",
  "Poet",
  "Innkeeper",
  "Engineer",
];
export const ALL_STATS: Stat[] = [
  "Strengh",
  "Dexterity",
  "Intelligence",
  "Charisma",
  "Perception",
  "Constitution",
  "Jealous",
  "Greedy",
  "Angry",
  "Violent",
  "Paranoid",
  "Sociopath",
  "Unforgiving",
];

/** Facts best kept hidden */
export interface Secret {
  isPublic: boolean;
  // tell someone
  revealTo(from: People, to: People): void;
  // tell everyone
  makePublic(world: World): void;
}

/** Long term projects */
export class Project {
  static nextProjectId = 0;
  public id = Project.nextProjectId++;
  constructor(
    public owner: number,
    public type: ProjectType,
    public forGoal: number,
    public totalSteps: number,
    public currentStep: number
  ) {}
}

/** Actions done each week */
export class Action {
  constructor(
    public owner: number,
    public type: ActionType,
    public targets: number[]
  ) {}
}

/** Goals and needs that drives the person */
export class Goal {
  static nextGoalId = 0;
  public id = Goal.nextGoalId++;
  // long term project for this goal
  public project: number | undefined;
  constructor(
    public owner: People,
    public type: GoalType,
    // need generated from the goal. If 0 the goal is satisfied
    public need: number,
    public targets: number[]
  ) {}
}

export type ActionType = "DO_NOTHING" | "START_PROJECT" | "CONTINUE_PROJECT";
export type GoalType = "GET_RICH" | "GET_REVENGE";
export type ProjectType = "INVEST_IN_INDUSTRY";

/** Relations between persons */
export interface Relation {
  owner: number;
  to: number;
  mag: number;
  reason: string;
  description: string;
}

export class Faction {
  static nextFactionId = 0;
  public id = Faction.nextFactionId++;
  public leaderSlots: number[];
  public leaders: [number[], number[], number[]] = [[], [], []];
  constructor(
    public name: string,
    public reputation: Map<number, number>, // reputation between factions
    public influence: number,
    public tier: number,
    public city: number
  ) {
    this.leaderSlots = [1, Math.floor(tier / 2) + 1, tier > 1 ? tier * 5 : 0];
  }

  reput(other: number): number {
    const r = this.reputation.get(other);
    return r !== undefined ? r : 0;
  }
}

export interface Position {
  x: number;
  y: number;
}

export interface Message {
  msg?: string;
  world?: World;
  progress: number;
  type: string;
}

export type Target = People | City | Faction;
