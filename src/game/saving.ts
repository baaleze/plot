import { LanguageGenerator } from "@/generation/language/language.generator";
import {
  Action,
  City,
  Faction,
  Gender,
  Goal,
  IndustryName,
  Job,
  Nation,
  Node,
  People,
  Position,
  Project,
  Relation,
  Road,
  Secret,
  Stat,
  Tile,
  World,
} from "./model/models";

export class Saving {
  public static simplifyMap<K, V>(map: Map<K, V>): [K, V][] {
    return map ? Array.from(map.entries()) : [];
  }

  public static save(world: World): string {
    return JSON.stringify(WorldSave.fromWorld(world));
  }

  public static load(json: string): World {
    return WorldSave.toWorld(JSON.parse(json));
  }
}

class WorldSave {
  constructor(
    public cities: [number, CitySave][],
    public rivers: string[],
    public people: [number, PeopleSave][],
    public factions: [number, FactionSave][],
    public map: string[][],
    public nations: [number, NationSave][],
    public neighbours: [number, number[]][],
    public day: number
  ) {}
  public static fromWorld(world: World): WorldSave {
    return new WorldSave(
      Saving.simplifyMap(world.cities).map(([id, city]) => [
        id,
        CitySave.fromCity(city),
      ]),
      world.rivers,
      Saving.simplifyMap(world.people).map(([id, people]) => [
        id,
        PeopleSave.fromPeople(people),
      ]),
      Saving.simplifyMap(world.factions).map(([id, faction]) => [
        id,
        FactionSave.fromFaction(faction),
      ]),
      WorldSave.compressMap(world.map),
      Saving.simplifyMap(world.nations).map(([id, nation]) => [
        id,
        NationSave.fromNation(nation),
      ]),
      Saving.simplifyMap(world.neighbours),
      world.week
    );
  }
  public static toWorld(save: WorldSave): World {
    const world = new World();
    world.week = save.day;
    world.map = WorldSave.extractMap(save.map);
    world.rivers = save.rivers;
    world.cities = new Map(
      save.cities.map(([id, city]) => [id, CitySave.toCity(city)])
    );
    world.people = new Map(
      save.people.map(([id, people]) => [id, PeopleSave.toPeople(people)])
    );
    world.factions = new Map(
      save.factions.map(([id, faction]) => [id, FactionSave.toFaction(faction)])
    );
    world.nations = new Map(
      save.nations.map(([id, nation]) => [id, NationSave.toNation(nation)])
    );
    world.neighbours = new Map(save.neighbours);
    return world;
  }
  public static compressMap(map: Tile[][]): string[][] {
    return map.map((col) =>
      col.map(
        (tile) =>
          // compress tile info into a single string
          `${tile.isFrontier ? 1 : 0}|${tile.isRoad ? 1 : 0}|${
            tile.isSeaRoad ? 1 : 0
          }|${tile.wasHole ? 1 : 0}|${tile.altitude}|${tile.cityId}\
            |${tile.position.x}/${tile.position.y}|${tile.type}|${
            tile.riverName
          }|${tile.waterFlow}`
      )
    );
  }
  public static extractMap(map: string[][]): Tile[][] {
    return map.map((col) =>
      col.map((s) => {
        // read tile info from string
        const split = s.split("|");
        const pos = split[6].split("/");
        const tile = new Tile(
          split[7],
          { x: Number(pos[0]), y: Number(pos[1]) },
          Number(split[4])
        );
        tile.isFrontier = split[0] === "1";
        tile.isRoad = split[1] === "1";
        tile.isSeaRoad = split[2] === "1";
        tile.wasHole = split[3] === "1";
        tile.cityId = Number(split[5]);
        tile.riverName = split[8];
        tile.waterFlow = Number(split[9]);
        return tile;
      })
    );
  }
}
class CitySave {
  constructor(
    public port: Position,
    public rivers: string[],
    public roads: RoadSave[],
    public nation: number,
    public access: number,
    public stability: number,
    public growth: number,
    public production: [number, number][],
    public resources: [number, number][],
    public deficits: [number, number][],
    public needs: [number, number][],
    public id: number,
    public name: string,
    public population: number,
    public industries: IndustryName[],
    public position: Position,
    public color: number[]
  ) {}

  public static fromCity(city: City): CitySave {
    return new CitySave(
      city.port,
      city.rivers,
      city.roads.map((road) => RoadSave.fromRoad(road)),
      city.nation,
      city.access,
      city.stability,
      city.growth,
      Saving.simplifyMap(city.production),
      Saving.simplifyMap(city.resources),
      Saving.simplifyMap(city.deficits),
      Saving.simplifyMap(city.needs),
      city.id,
      city.name,
      city.population,
      city.industries,
      city.position,
      city.color
    );
  }
  public static toCity(save: CitySave): City {
    const city = new City(
      save.name,
      save.population,
      save.industries,
      save.position,
      save.color
    );
    city.id = save.id;
    city.port = save.port;
    city.rivers = save.rivers;
    city.nation = save.nation;
    city.access = save.access;
    city.stability = save.stability;
    city.growth = save.growth;
    city.roads = save.roads.map((r) => RoadSave.toRoad(r));
    city.production = new Map(save.production);
    city.resources = new Map(save.resources);
    city.deficits = new Map(save.deficits);
    city.needs = new Map(save.needs);
    return city;
  }
}
class RoadSave {
  constructor(
    public id: number,
    public from: number,
    public to: number,
    public path: Position[],
    public cost: number
  ) {}
  public static fromRoad(road: Road): RoadSave {
    return new RoadSave(road.id, road.from, road.to, road.path, road.cost);
  }
  public static toRoad(save: RoadSave): Road {
    const road = new Road(save.from, save.to, save.path, save.cost);
    road.id = save.id;
    return road;
  }
}
class FactionSave {
  constructor(
    public id: number,
    public name: string,
    public reputation: [number, number][],
    public influence: number,
    public tier: number,
    public city: number,
    public leaderSlots: number[],
    public leaders: [number[], number[], number[]]
  ) {}
  public static fromFaction(faction: Faction): FactionSave {
    return new FactionSave(
      faction.id,
      faction.name,
      Saving.simplifyMap(faction.reputation),
      faction.influence,
      faction.tier,
      faction.city,
      faction.leaderSlots,
      faction.leaders
    );
  }
  public static toFaction(save: FactionSave): Faction {
    const faction = new Faction(
      save.name,
      new Map(save.reputation),
      save.influence,
      save.tier,
      save.city
    );
    faction.id = save.id;
    faction.leaderSlots = save.leaderSlots;
    faction.leaders = save.leaders;
    return faction;
  }
}
class PeopleSave {
  constructor(
    public id: number,
    public name: string,
    public stats: [Stat, number][],
    public reputation: [number, number][],
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
  public static fromPeople(people: People): PeopleSave {
    return new PeopleSave(
      people.id,
      people.name,
      Saving.simplifyMap(people.stats),
      Saving.simplifyMap(people.reputation),
      people.relations,
      people.location,
      people.goals,
      people.currentAction,
      people.projects,
      people.secrets,
      people.origin,
      people.job,
      people.birthDay,
      people.gender,
      people.willMarry
    );
  }
  public static toPeople(save: PeopleSave): People {
    const people = new People(
      save.name,
      new Map(save.stats),
      new Map(save.reputation),
      save.relations,
      save.location,
      save.goals,
      save.currentAction,
      save.projects,
      save.secrets,
      save.origin,
      save.job,
      save.birthDay,
      save.gender,
      save.willMarry
    );
    people.id = save.id;
    return people;
  }
}
class NationSave {
  constructor(
    public id: number,
    public name: string,
    public lang: LanguageSave,
    public color: number[]
  ) {}
  public static fromNation(nation: Nation): NationSave {
    return new NationSave(
      nation.id,
      nation.name,
      LanguageSave.fromLanguage(nation.lang),
      nation.color
    );
  }
  public static toNation(save: NationSave): Nation {
    const nation = new Nation(save.color);
    nation.id = save.id;
    nation.name = save.name;
    nation.lang = LanguageSave.toLanguage(save.lang);
    return nation;
  }
}
class LanguageSave {
  constructor(
    public consonantProbas: [string, number][],
    public vowelProbas: [string, number][],
    public syllabes: [string, number][][],
    public wordPatterns: string[][]
  ) {}
  public static fromLanguage(lang: LanguageGenerator): LanguageSave {
    return new LanguageSave(
      Saving.simplifyMap(lang.consonantProbas),
      Saving.simplifyMap(lang.vowelProbas),
      lang.syllabes.map((syll) => Saving.simplifyMap(syll)),
      lang.wordPatterns
    );
  }
  public static toLanguage(save: LanguageSave): LanguageGenerator {
    const lang = new LanguageGenerator();
    lang.consonantProbas = new Map(save.consonantProbas);
    lang.vowelProbas = new Map(save.vowelProbas);
    lang.syllabes = save.syllabes.map((syll) => new Map(syll));
    lang.wordPatterns = save.wordPatterns;
    return lang;
  }
}
