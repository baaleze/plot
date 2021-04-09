<template>
  <div id="phaser-container"></div>
</template>

<script lang="ts">
import { AUTO, Game } from "phaser";
import { Component, Prop, Vue } from "vue-property-decorator";
import { LoadingScene } from "@/game/scene/loading";
import { MainScene } from "@/game/scene/main";
import { Config } from "@/constants/conf";
@Component
export default class HelloWorld extends Vue {
  private phaser!: Game;

  mounted(): void {
    this.phaser = new Game({
      parent: "phaser-container",
      type: AUTO,
      width: Config.WORLD_SIZE * Config.TILE_SIZE,
      height: Config.WORLD_SIZE * Config.TILE_SIZE,
      scene: [LoadingScene, MainScene],
    });
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss"></style>
