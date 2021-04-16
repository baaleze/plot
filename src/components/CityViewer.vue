<template>
  <div v-if="city !== undefined && city.name !== ''">
    <h2>
      <b>{{ city.name }}</b> of Nation {{ getNation(city.nation).name }} [{{ city.position.x }},{{
        city.position.y
      }}]</h2>
    <h3>
      Population {{ city.population }} [{{ getMag() }}] (growth = {{ city.growth * 10 }}%)<br>
      Access {{ city.access }} 
      Stability {{ city.stability }}
      <span v-if="city.port">
        Has a port [{{ city.port.x }},{{ city.port.y }}]
      </span>
    </h3>
    <div>
      Roads to <span v-for="r of city.roads" :key="r.id">{{ getCity(r.to).name }}//</span>
    </div>
    <div v-if="city.industries">
      <div>
        Industries:<br />
        <table>
          <tr>
            <td v-for="i of city.industries" :key="i">
              {{ getIndustry(i).name }}
            </td>
          </tr>
        </table>
      </div>
      <div>
        Resources:<br />
        <b-table-simple dark responsive="true">
          <b-tr>
            <b-th>Resource</b-th>
            <b-th v-for="r of res" :key="r">
              <img v-b-tooltip :title="getResourceName(r)" class="resource" :src="'/icons/'+getResourceName(r)+'.svg'" >
            </b-th>
          </b-tr>
          <b-tr>
            <b-th>Stock</b-th>
            <b-td v-for="r of res" :key="r">
              {{ city.resources.get(r) }}
            </b-td>
          </b-tr>
          <b-tr>
            <b-th>Production</b-th>
            <b-td v-for="r of res" :key="r">
              {{ city.production.get(r) }}
            </b-td>
          </b-tr>
          <b-tr>
            <b-th>Demand</b-th>
            <b-td v-for="r of res" :key="r">
              {{ city.needs.get(r) }}
            </b-td>
          </b-tr>
          <b-tr>
            <b-th>Provided</b-th>
            <b-td
              v-for="r of res"
              :key="r"
              :variant="city.deficits.get(r) > 0 ? 'danger' : ''"
              :title="city.deficits.get(r) > 0 ? 'demand not fulfilled' : ''"
              v-b-tooltip
            >
              <span v-if="city.needs.get(r) !== undefined">{{
                city.needs.get(r) - city.deficits.get(r)
              }}</span>
            </b-td>
          </b-tr>
        </b-table-simple>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
import { allResources, City, Industry, IndustryName, Nation, Resource } from "@/game/model/models";
import { Utils } from "@/utils";
import { GameData } from "@/game/gameData";
@Component
export default class CityViewer extends Vue {
  @Prop() city!: City;
  res = allResources;
  getIndustry(name: IndustryName): Industry {
    return Industry.industries.get(name)!;
  }

  getResourceName(r: Resource): string {
    return Resource[r].toLowerCase();
  }

  getMag(): number {
    return Utils.getMag(this.city.population);
  }
  getCity(id: number): City {
    return GameData.getInstance().worldInstance.cities.get(id)!;
  }
  getNation(id: number): Nation {
    return GameData.getInstance().worldInstance.nations.get(id)!;
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
.resource {
  width: 25px;
}
</style>
