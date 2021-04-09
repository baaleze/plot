import { Config } from "@/constants/conf";
import { Scene } from "phaser";
import { TileType } from "../model/models";

export class LoadingScene extends Scene {
  constructor() {
    super({ key: "LoadingScene" });
  }

  preload(): void {
    // load assets
    TileType.ALL_TYPES.forEach((name) => this.loadTexture(name));
    this.loadTexture("path");
    this.load.image("tileset", `img/${Config.TILE_SIZE}/tileset.png`)
  }

  loadTexture(name: string): void {
    this.load.image(name, `img/${Config.TILE_SIZE}/${name}.jpg`);
  }

  create(): void {
    // start the main scene
    console.log("LOADED");
    this.scene.start("MainScene");
  }
}
