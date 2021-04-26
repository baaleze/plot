<template>
  <div v-if="people !== undefined && people.name !== ''">
    <h2>
      <b>{{ people.name }} ({{ getAge(people) }} {{ people.gender }})</b> of Nation
      {{ getNation(people.origin).name }}
    </h2>
    <h3>Living in {{ getCity(people.location).name }}<br /></h3>
    <b-table-simple dark responsive="true">
      <b-tr>
        <b-th>Stat</b-th>
        <b-th>Value</b-th>
      </b-tr>
      <b-tr v-for="s of stats" :key="s">
        <b-th>{{ s }}</b-th>
        <b-td>
          {{ people.stat(s) }}
        </b-td>
      </b-tr>
    </b-table-simple>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
import {
  allResources,
  ALL_STATS,
  City,
  Industry,
  IndustryName,
  Nation,
  People,
  Resource,
} from "@/game/model/models";
import { Utils } from "@/utils";
import { GameData } from "@/game/gameData";
@Component
export default class CityViewer extends Vue {
  @Prop() people!: People;
  stats = ALL_STATS;

  getCity(id: number): City {
    return GameData.getInstance().worldInstance.cities.get(id)!;
  }
  getNation(id: number): Nation {
    return GameData.getInstance().worldInstance.nations.get(id)!;
  }
  getAge(people: People): number {
    // get in weeks number
    const today = 550 * 12 * 4 + GameData.getInstance().worldInstance.week;
    const birthday = people.birthDay[2] * 12 * 4 + people.birthDay[1] * 4 + people.birthDay[0];
    // diff
    return Math.floor((today - birthday) / (12*4));
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
.resource {
  width: 25px;
}
</style>
