import {
  World,
  Resource,
  City,
  allResources,
  Industry,
  TileType,
  Message,
  ALL_INDUSTRIES,
} from "@/game/model/models";
import { Utils } from "@/utils";

const TICK_TIME = 1;

export class CityEvolution {
  static tick(world: World): Message {
    world.week = (world.week + TICK_TIME) % 70;
    // update cities every week
    if (world.week === 0) {
      CityEvolution.updateCities(world);
    }

    return { type: "tickEnd", world, progress: 100 };
  }

  static getRandomTradeRoute(
    world: World
  ): { resource: Resource; from: number; to: number } | undefined {
    // get all cities with enough access to spawn caravans and pick one
    const city = Utils.randomInArray(Array.from(world.cities.values()));

    // now to find which resource to trade
    const possibleResources = Array.from(city.production.keys()).filter((res) =>
      // filter resources that are needed by neighbors
      city.roads.some((r) => world.cities.get(r.to)?.needs.has(res))
    );
    let resourceToTrade: Resource;
    if (possibleResources.length > 0) {
      resourceToTrade = Utils.randomInArray(possibleResources);
    } else {
      // fuck it use random one
      resourceToTrade = Utils.randomInArray(Array.from(city.needs.keys()));
    }

    // choose a city to send the resource to
    let cityToTrade: number;
    const possibleCities = city.roads.filter((r) =>
      world.cities.get(r.to)?.needs.has(resourceToTrade)
    );
    if (possibleCities.length > 0) {
      cityToTrade = Utils.randomInArray(possibleCities).to;
    } else if (possibleCities.length === 0) {
      return undefined;
    } else {
      // pick random one
      cityToTrade = Utils.randomInArray(
        Array.from(world.cities.keys()).filter((id) => id !== city.id)
      );
    }

    // LETS GOOOOOOO
    return {
      resource: resourceToTrade,
      from: city.id,
      to: cityToTrade,
    };
  }

  static updateCities(world: World): void {
    const cityArray = Array.from(world.cities.values());
    // Update access for every city
    cityArray.forEach(
      (city) => (city.access = CityEvolution.computeAccess(city, world))
    );

    cityArray.forEach((city) => {
      // get best production for each resource
      const bestProd = CityEvolution.getBestProd(cityArray, city);

      // check for needs
      const deficits = new Map<Resource, number>();
      city.needs = CityEvolution.sumAllNeeds(city);
      city.needs.forEach((need, res) => {
        // is global demand met ?
        if (need > city.access || need > bestProd.get(res)!) {
          // Access or prod in the world are not enough
          deficits.set(res, need - Math.min(city.access, bestProd.get(res)!));
        } else {
          deficits.set(res, 0);
        }
      });

      city.deficits = deficits;

      // recompute stability
      city.stability = CityEvolution.computeStability(deficits);

      // compute growth
      city.growth = CityEvolution.computeGrowth(deficits, city, world);

      // update city population
      city.population = Math.floor(city.population * (1 + city.growth / 10));

      // update productions and fill stocks
      city.industries.forEach((ind) => {
        const industry = Industry.industries.get(ind)!;
        // get the biggest shortage for this industry
        let short = 0;
        industry.needs.forEach((need) => {
          if (deficits.get(need.resource)! > short) {
            short = deficits.get(need.resource)!;
          }
        });

        industry.produces.forEach((prod) => {
          // update production, removing shortage
          const newProd = Math.max(
            0,
            prod.mag(Utils.getMag(city.population)) - short
          );
          city.production.set(prod.resource, newProd);
          // update stocks
          city.resources.set(prod.resource, newProd * 100);
        });
      });
    });
  }

  static computeGrowth(
    deficits: Map<Resource, number>,
    city: City,
    world: World
  ): number {
    let g = 0;
    // apply consequences for lack of food
    g += deficits.get(Resource.FOOD)! > 0 ? -1 : 1;
    // bonus from access
    g += Math.max(0, city.access - 2);
    // bonus/malus from stability
    g += Math.floor((city.stability - 5) / 2);
    // biomes
    const scan = Utils.scanAround(
      world.map,
      city.position.x,
      city.position.y,
      2
    );
    if (scan.biomes.some((b) => b === TileType.SWAMP)) {
      g--;
    }
    if (scan.biomes.some((b) => b === TileType.SNOW)) {
      g--;
    }
    if (scan.biomes.some((b) => b === TileType.RIVER)) {
      g++;
    }
    return g;
  }

  static computeAccess(city: City, world: World): number {
    let a = 5; // base for city with market
    // size
    a += Math.max(0, Utils.getMag(city.population) - 4);
    // number of cities near
    const nbCloseCities = city.roads.filter((r) => r.path.length < 100).length;
    a += Math.min(5, nbCloseCities - 3);
    return a;
  }

  static computeStability(deficits: Map<Resource, number>): number {
    let stab = 5; // base
    if (deficits.get(Resource.GOODS) === 0) {
      stab++;
    }
    if (deficits.get(Resource.FOOD)) {
      stab--;
    }
    if (deficits.get(Resource.METAL)) {
      stab--;
    }
    if (deficits.get(Resource.WOOD)) {
      stab--;
    }
    if (deficits.get(Resource.TOOLS)) {
      stab--;
    }
    if (deficits.get(Resource.MACHINE)) {
      stab--;
    }
    // TODO add other bonus/malus from upgrades / raids
    return stab;
  }

  static getBestProd(cities: City[], city: City): Map<Resource, number> {
    const bestProd = new Map<Resource, number>();
    allResources.forEach((r) => {
      let best = 0;
      cities.forEach((c) => {
        // get prod for the city
        let prod = 0;
        // use resolved prod (includes shortage impact)
        if (c.production.has(r)) {
          prod = Math.min(c.access, c.production.get(r)!);
        } else {
          // if not get normal prod
          c.industries.forEach((i) => {
            Industry.industries.get(i)?.produces.forEach((p) => {
              if (p.resource === r) {
                // we're producing this
                prod = Math.min(c.access, p.mag(Utils.getMag(c.population)));
              }
            });
          });
        }
        // if same nation +3 bonus!
        if (c.nation === city.nation) {
          prod += 3;
        }
        if (prod > best) {
          best = prod;
        }
      });
      bestProd.set(r, best);
    });
    return bestProd;
  }

  static sumAllNeeds(city: City): Map<Resource, number> {
    const needs = new Map<Resource, number>();

    // base needs from population
    needs.set(Resource.FOOD, Utils.getMag(city.population));
    needs.set(Resource.GOODS, Math.max(0, Utils.getMag(city.population) - 3));

    city.industries.forEach((indus) => {
      Industry.industries.get(indus)?.needs.forEach((need) => {
        if (needs.has(need.resource)) {
          needs.set(
            need.resource,
            Math.max(
              needs.get(need.resource)!,
              need.mag(Utils.getMag(city.population))
            )
          );
        } else {
          needs.set(need.resource, need.mag(Utils.getMag(city.population)));
        }
      });
    });
    return needs;
  }
}
