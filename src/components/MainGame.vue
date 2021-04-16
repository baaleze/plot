<template>
  <div>
    <div id="phaser-container"></div>
    <div id="ui">
      <div id="action-menu">
        <b-button @click="save()">SAVE</b-button>
        <b-button @click="load()">LOAD</b-button>
      </div>
      <div id="intel">
        <b-tabs>
          <b-tab title="Cities" active>
            <b-row id="intel">
              <b-col id="city-list" class="col-3">
                <b-list-group>
                  <b-list-group-item
                    :style="{ color: getNationColor(city) }"
                    :active="selectedCity.id === city.id"
                    @click="selectCity(city)"
                    button
                    v-for="city in cities"
                    :key="city.id"
                    >{{ city.name }}</b-list-group-item
                  >
                </b-list-group>
              </b-col>
              <b-col id="city-info" class="col-9">
                <city-viewer :city="selectedCity" />
              </b-col>
            </b-row>
          </b-tab>
          <b-tab title="People"><p>I'm the second tab</p></b-tab>
        </b-tabs>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { AUTO, Game } from "phaser";
import { Component, Prop, Vue } from "vue-property-decorator";
import { LoadingScene } from "@/game/scene/loading.scene";
import { MainScene } from "@/game/scene/main.scene";
import { Config } from "@/constants/conf";
import { GameData } from "@/game/gameData";
import { City, Position } from "@/game/model/models";
import CityViewer from "./CityViewer.vue";
import { Utils } from "@/utils";
import { Saving } from "@/game/saving";
@Component({
  components: {
    CityViewer
  }
})
export default class MainGame extends Vue {
  private phaser!: Game;
  private gameData = GameData.getInstance();

  public cities: City[] = [];
  public selectedCity = new City('', 0, [], new Position(0,0), []);

  mounted(): void {
    this.resetGame();
  }

  resetGame(): void {
    this.cities = Array.from(this.gameData.worldInstance.cities.values()).sort((a, b) => {
      return a.nation > b.nation ? 1 : -1
    });
    if (this.phaser) {
      this.phaser.destroy(true);
    }
    this.phaser = new Game({
      parent: "phaser-container",
      type: AUTO,
      width: Config.WORLD_SIZE * Config.TILE_SIZE,
      height: Config.WORLD_SIZE * Config.TILE_SIZE,
      scene: [LoadingScene, MainScene],
    });
  }

  selectCity(city: City): void {
    this.selectedCity = city;
  }

  getNationColor(city: City): string {
    const nations = this.gameData.worldInstance.nations;
    const nation = nations.get(city.nation)!;
    return Utils.colorString(nation.color.map(c => c / 2));
  }

  save(): void {
    localStorage.setItem("worldsave", Saving.save(this.gameData.worldInstance));
  }
  load(): void {
    this.gameData.worldInstance = Saving.load(localStorage.getItem("worldsave")!);
    this.resetGame();
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
#phaser-container, #ui {
  display: inline-block;
  width: 50%;
}
#phaser-container {
  float: left;
}
#ui {
  float: right;
}
#intel {
  width: 95%;
}
</style>
