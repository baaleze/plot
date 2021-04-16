import { Config } from "@/constants/conf";
import { WorldGen } from "@/generation/worldgen";
import { World } from "./model/models";

export class GameData {
  private static instance: GameData;
  public worldInstance!: World;

  public static getInstance(): GameData {
    if (!GameData.instance) {
      GameData.instance = new GameData();
    }
    return GameData.instance;
  }

  public generate(): void {
    const world = new WorldGen().run({
      seaLevel: Config.SEA_LEVEL,
      mountainLevel: Config.MOUNTAIN_LEVEL,
      worldSize: Config.WORLD_SIZE,
      nbNations: 4,
    });
    this.worldInstance = world;
  }
}
