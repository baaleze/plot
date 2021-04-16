import { Config } from "@/constants/conf";
import { Utils } from "@/utils";
import { Scene, GameObjects, Tilemaps, BlendModes } from "phaser";
import { GameData } from "../gameData";
import { CityEvolution } from "../livingworld/city.evolution";
import { Road, Tile, TileType } from "../model/models";

export class MainScene extends Scene {
  gameData = GameData.getInstance();
  tileMap!: Tilemaps.Tilemap;

  constructor() {
    super({ key: "MainScene" });
  }

  create(): void {
    this.buildHeightMap();
    this.drawMap();
    this.drawRoads();
    CityEvolution.updateCities(this.gameData.worldInstance);
  }

  buildHeightMap(): void {
    // create a canvas
    const canvas = document.createElement('canvas');
    canvas.width = Config.WORLD_SIZE;
    canvas.height = Config.WORLD_SIZE;
    const cx = canvas.getContext('2d');
    if (cx) {
      this.gameData.worldInstance.map.forEach((col, x) => col.forEach((tile, y) => {
        const grey = tile.altitude / 2 + 127;
        cx.fillStyle = Utils.colorString([grey, grey, grey]);
        cx.fillRect(x, y, 1, 1);
      }));
      this.textures.addCanvas('heightmap', canvas);
    }
  }

  drawMap(): void {
    // build the tile map from scratch
    this.tileMap = this.make.tilemap({
      tileHeight: Config.TILE_SIZE,
      tileWidth: Config.TILE_SIZE,
      width: Config.WORLD_SIZE,
      height: Config.WORLD_SIZE,
    });
    // tileset
    const tileSet = this.tileMap.addTilesetImage("tileset");
    const mapLayer = this.tileMap.createBlankLayer("mapLayer", "tileset");
    // fill the tileMap
    this.gameData.worldInstance.map.forEach((tileCol, x) => {
      tileCol.forEach((tile, y) => {
        mapLayer.putTileAt(TileType.getTileSetId(tile.type), x, y);
      });
    });
    // add the heightmap
    const heightMap = this.add.image(Config.WORLD_SIZE * Config.TILE_SIZE / 2,  Config.WORLD_SIZE * Config.TILE_SIZE / 2,'heightmap');
    heightMap.scale = Config.TILE_SIZE;
    heightMap.blendMode = BlendModes.MULTIPLY;
  }

  drawRoads(): void {
    this.gameData.worldInstance.cities.forEach((city) => {
      city.roads.forEach((road) => {
        if (road.path.length > 0) {
          this.drawRoad(road);
        }
      });
      const text = this.add.text(
        city.position.x * Config.TILE_SIZE,
        (city.position.y - 2) * Config.TILE_SIZE,
        city.name,
        { align: "center", stroke: "#000", strokeThickness: 1 }
      );
      text.setOrigin(0.5, 0);
    });
  }

  drawRoad(road: Road): void {
    const rope = this.add.rope(
      0,
      0,
      "path",
      undefined,
      road.path
        .filter((n) => n.x !== 0 || n.y !== 0)
        .map((node) => ({
          x: node.x * Config.TILE_SIZE + Config.TILE_SIZE / 2,
          y: node.y * Config.TILE_SIZE + Config.TILE_SIZE / 2,
        }))
    );
  }
}
