import { Config } from "@/constants/conf";
import { ActionResolver } from "@/game/model/actions";
import {
  ALL_JOBS,
  ALL_STATS,
  City,
  Faction,
  Job,
  Nation,
  People,
  Stat,
  World,
} from "@/game/model/models";
import { Utils } from "@/utils";

export class PeopleGen {
  static generateFirstPeople(world: World): void {
    // init maps
    world.people = new Map<number, People>();
    world.factions = new Map<number, Faction>();
    // generate factions in each city with people in them
    world.cities.forEach((city) => {
      PeopleGen.generateCityFactions(
        world,
        city,
        world.nations.get(city.nation)!
      );
    });
  }

  static generateCityFactions(world: World, city: City, nation: Nation): void {
    // main
    city.builderGuild = PeopleGen.generateFaction(
      world,
      city,
      Utils.getMag(city.population),
      `Builder Guild of ${city.name}`,
      [
        ["Builder", "Noble", "Engineer"],
        ["Engineer", "Builder"],
        ["Builder", "Engineer"],
      ],
      nation
    );
    city.merchantGuild = PeopleGen.generateFaction(
      world,
      city,
      Utils.getMag(city.population),
      `Merchants Guild of ${city.name}`,
      [["Merchant", "Noble", "Soldier"], ["Merchant", "Noble"], ["Merchant"]],
      nation
    );
    city.mafia = PeopleGen.generateFaction(
      world,
      city,
      Utils.getMag(city.population) - 1,
      `Mafia of ${city.name}`,
      [[], [], []],
      nation
    );
    city.spies = PeopleGen.generateFaction(
      world,
      city,
      Utils.getMag(city.population) - 1,
      `Spies of ${city.name}`,
      [[], [], []],
      nation
    );
    city.council = PeopleGen.generateFaction(
      world,
      city,
      Utils.getMag(city.population),
      `Council of ${city.name}`,
      [
        ["Noble", "Merchant", "Engineer", "Soldier"],
        ["Noble", "Merchant", "Engineer", "Soldier"],
        ["Noble", "Merchant", "Engineer", "Soldier"],
      ],
      nation
    );
    city.guard = PeopleGen.generateFaction(
      world,
      city,
      Utils.getMag(city.population),
      `Guard of ${city.name}`,
      [
        ["Soldier", "Guard", "Noble"],
        ["Soldier", "Guard", "Noble"],
        ["Soldier", "Guard", "Thug"],
      ],
      nation
    );
    // syndicates
    city.syndicates.set(
      "Blacksmith",
      PeopleGen.generateFaction(
        world,
        city,
        Utils.getMag(city.population) - 1,
        `Blacksmiths Guild of ${city.name}`,
        [["Smith", "Noble", "Merchant"], ["Smith"], ["Smith", "Peasant"]],
        nation
      )
    );
    city.syndicates.set(
      "Cattle",
      PeopleGen.generateFaction(
        world,
        city,
        Utils.getMag(city.population) - 1,
        `Livestock Guild of ${city.name}`,
        [["Farmer", "Noble", "Merchant"], ["Farmer"], ["Farmer", "Peasant"]],
        nation
      )
    );
    city.syndicates.set(
      "Cotton",
      PeopleGen.generateFaction(
        world,
        city,
        Utils.getMag(city.population) - 1,
        `Cotton Guild of ${city.name}`,
        [["Farmer", "Noble", "Merchant"], ["Farmer"], ["Farmer", "Peasant"]],
        nation
      )
    );
    city.syndicates.set(
      "Farm",
      PeopleGen.generateFaction(
        world,
        city,
        Utils.getMag(city.population) - 1,
        `Farmers Guild of ${city.name}`,
        [["Farmer", "Noble", "Merchant"], ["Farmer"], ["Farmer", "Peasant"]],
        nation
      )
    );
    city.syndicates.set(
      "Goods",
      PeopleGen.generateFaction(
        world,
        city,
        Utils.getMag(city.population) - 1,
        `Crafters Guild of ${city.name}`,
        [
          ["Carpenter", "Engineer", "Noble", "Merchant"],
          ["Carpenter", "Engineer", "Merchant"],
          ["Carpenter", "Engineer"],
        ],
        nation
      )
    );
    city.syndicates.set(
      "Horse",
      PeopleGen.generateFaction(
        world,
        city,
        Utils.getMag(city.population) - 1,
        `Horse Guild of ${city.name}`,
        [
          ["Horse Caretaker", "Noble", "Merchant"],
          ["Horse Caretaker"],
          ["Horse Caretaker"],
        ],
        nation
      )
    );
    city.syndicates.set(
      "Machinery",
      PeopleGen.generateFaction(
        world,
        city,
        Utils.getMag(city.population) - 1,
        `Engineers Guild of ${city.name}`,
        [
          ["Engineer", "Noble", "Merchant"],
          ["Engineer", "Smith"],
          ["Engineer", "Smith", "Peasant"],
        ],
        nation
      )
    );
    city.syndicates.set(
      "Metal",
      PeopleGen.generateFaction(
        world,
        city,
        Utils.getMag(city.population) - 1,
        `Smelters Guild of ${city.name}`,
        [
          ["Smith", "Noble", "Merchant", "Miner"],
          ["Smith", "Miner"],
          ["Smith", "Miner", "Peasant"],
        ],
        nation
      )
    );
    city.syndicates.set(
      "Stone",
      PeopleGen.generateFaction(
        world,
        city,
        Utils.getMag(city.population) - 1,
        `Mining Guild of ${city.name}`,
        [
          ["Miner", "Mason", "Noble", "Merchant"],
          ["Mason", "Miner"],
          ["Mason", "Miner", "Peasant"],
        ],
        nation
      )
    );
    city.syndicates.set(
      "Woodcutting",
      PeopleGen.generateFaction(
        world,
        city,
        Utils.getMag(city.population) - 1,
        `Woodworkers Guild of ${city.name}`,
        [
          ["Carpenter", "Noble", "Merchant"],
          ["Carpenter"],
          ["Carpenter", "Peasant"],
        ],
        nation
      )
    );
    // mercenaries and gangs
    const nbMinorGroup = Math.max(1, Utils.getMag(city.population) - 2);
    for (let n = 0; n < nbMinorGroup; n++) {
      city.gangs.push(
        PeopleGen.generateFaction(
          world,
          city,
          Utils.getMag(city.population) - 2,
          `${nation.lang.generateName("river")}, Gang of ${city.name}`,
          [
            ["Peasant", "Soldier", "Merchant", "Poet", "Thug"],
            ["Peasant", "Soldier", "Merchant", "Poet", "Thug"],
            ["Peasant", "Soldier", "Thug"],
          ],
          nation
        )
      );
      city.gangs.push(
        PeopleGen.generateFaction(
          world,
          city,
          Utils.getMag(city.population) - 2,
          `${nation.lang.generateName("river")}, Mercenaries of ${city.name}`,
          [
            ["Peasant", "Soldier", "Merchant", "Thug"],
            ["Peasant", "Soldier", "Thug"],
            ["Peasant", "Soldier", "Thug"],
          ],
          nation
        )
      );
    }
  }

  static generateFaction(
    world: World,
    city: City,
    maxTier: number,
    name: string,
    rankJobs: [Job[], Job[], Job[]],
    nation: Nation
  ): number {
    // If city is too small make it tier 1
    const tier = maxTier < 1 ? 1 : Utils.randomIntBetween(1, maxTier);
    const influence = Math.floor(Math.pow(10, tier) * (1 + Math.random()));
    const faction = new Faction(
      name,
      new Map<number, number>(),
      influence,
      tier,
      city.id
    );
    world.factions.set(faction.id, faction);
    // populate it
    PeopleGen.generateFactionMembers(world, rankJobs, faction, city, nation);
    return faction.id;
  }

  static generateFactionMembers(
    world: World,
    rankJobs: [Job[], Job[], Job[]],
    faction: Faction,
    city: City,
    nation: Nation
  ): void {
    // for each rank
    for (let rank = 0; rank < 3; rank++) {
      // fill random number of slots
      const nbToGenerate = Utils.randomIntBetween(
        Math.ceil(faction.leaderSlots[rank] / 3),
        faction.leaderSlots[rank]
      );
      for (let i = 0; i < nbToGenerate; i++) {
        const p = PeopleGen.generatePeople(world, rankJobs[rank], city, nation);
        // add to faction
        faction.leaders[rank].push(p.id);
        // add to world
        world.people.set(p.id, p);
      }
    }
  }

  static generatePeople(
    world: World,
    possibleJobs: Job[],
    city: City,
    nation: Nation
  ): People {
    // 10% chance to use already existing people to have some double responsabilities
    if (Utils.randomIntBetween(0, 100) < Config.USE_EXISTING_PEOPLE_CHANCE) {
      // find someone good for the job, in the same city
      const existing = Utils.randomInArray(
        Array.from(world.people.values()).filter(
          (p) => possibleJobs.includes(p.job) && p.location === city.id
        )
      );
      if (existing) {
        return existing;
      } // else no one found create someone
    }

    // 5% chance other nation that where the people is currently
    const origin =
      Utils.randomIntBetween(0, 100) < Config.STRANGER_IN_CITY_CHANCE
        ? Utils.randomInArray(Array.from(world.nations.values()))
        : nation;
    const job =
      possibleJobs.length > 0
        ? Utils.randomInArray(possibleJobs)
        : Utils.randomInArray(ALL_JOBS);
    // Make a new people!!
    const p = new People(
      `${origin.lang.generateName("firstName")} ${origin.lang.generateName(
        "lastName"
      )}`,
      PeopleGen.generateStats(job),
      new Map<number, number>(),
      [],
      city.id,
      [],
      undefined,
      [],
      [],
      origin.id,
      job,
      // starting date is 1w 1m 550y
      [Utils.randomIntBetween(1, 4), Utils.randomIntBetween(1, 12), Utils.randomIntBetween(450, 535)],
      Utils.randomInArray(["M", "F", "NB"]),
      Utils.randomInArray([["M", "F", "NB"], ["M", "F"], ["M", "NB"], ["F", "NB"],["M"], ["F"], ["NB"], []])
    );
    return p;
  }

  static generateStats(job: Job): Map<Stat, number> {
    const stats = new Map<Stat, number>();
    // put random stats in all, in range -2~2
    ALL_STATS.forEach((stat) => stats.set(stat, Utils.randomIntBetween(-2, 2)));

    // bonus depending on jobs
    switch (job) {
      case "Carpenter":
        Utils.addInMap(stats, "Strengh", 1);
        Utils.addInMap(stats, "Dexterity", 1);
        break;
      case "Cleric":
        Utils.addInMap(stats, "Intelligence", 1);
        Utils.addInMap(stats, "Perception", 1);
        break;
      case "Cook":
        Utils.addInMap(stats, "Constitution", 1);
        break;
      case "Engineer":
        Utils.addInMap(stats, "Intelligence", 1);
        break;
      case "Farmer":
        Utils.addInMap(stats, "Strengh", 1);
        Utils.addInMap(stats, "Constitution", 1);
        break;
      case "Guard":
        Utils.addInMap(stats, "Strengh", 1);
        Utils.addInMap(stats, "Constitution", 1);
        break;
      case "Horse Caretaker":
        Utils.addInMap(stats, "Perception", 1);
        Utils.addInMap(stats, "Dexterity", 1);
        break;
      case "Innkeeper":
        Utils.addInMap(stats, "Charisma", 1);
        break;
      case "Mason":
        Utils.addInMap(stats, "Strengh", 1);
        break;
      case "Miner":
        Utils.addInMap(stats, "Strengh", 1);
        Utils.addInMap(stats, "Constitution", 1);
        break;
      case "Noble":
        Utils.addInMap(stats, "Charisma", 1);
        Utils.addInMap(stats, "Ambitious", 1);
        break;
      case "Peasant":
        break;
      case "Poet":
        Utils.addInMap(stats, "Charisma", 1);
        Utils.addInMap(stats, "Jealous", 1);
        break;
      case "Smith":
        Utils.addInMap(stats, "Strengh", 1);
        break;
      case "Soldier":
        Utils.addInMap(stats, "Strengh", 1);
        Utils.addInMap(stats, "Constitution", 1);
        break;
      case "Tanner":
        Utils.addInMap(stats, "Dexterity", 1);
        break;
      case "Baker":
        break;
      case "Builder":
        Utils.addInMap(stats, "Strengh", 1);
        break;
      case "Butcher":
        Utils.addInMap(stats, "Violent", 1);
        break;
      case "Merchant":
        Utils.addInMap(stats, "Charisma", 1);
        Utils.addInMap(stats, "Greedy", 1);
    }
    return stats;
  }
}
