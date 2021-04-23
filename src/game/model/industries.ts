import { Industry, IndustryName, Demand, Resource } from "./models";

export const INDUSTRIES = new Map<IndustryName, Industry>([
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