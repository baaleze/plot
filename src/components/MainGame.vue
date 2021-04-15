<template>
  <div>
    <div id="phaser-container"></div>
    <div id="ui">
      <div id="action-menu"></div>
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
import { LoadingScene } from "@/game/scene/loading";
import { MainScene } from "@/game/scene/main";
import { Config } from "@/constants/conf";
import { GameData } from "@/game/gameData";
import { City, Position } from "@/game/model/models";
import CityViewer from "./CityViewer.vue";
import { Utils } from "@/utils";
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
    this.cities = this.gameData.worldInstance.cities.map(c => c).sort((a, b) => {
      return a.nation.name > b.nation.name ? 1 : -1
    });
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
    return Utils.colorString(city.nation.color.map(c => c / 2));
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
