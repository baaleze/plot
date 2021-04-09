<template>
  <div v-if="city !== undefined">
    <h3>
      <b>{{ city.name }}</b> of Nation {{ city.nation.name }} [{{ city.position.x }},{{
        city.position.y
      }}]</h3>
    <h2>
      Population {{ city.population }} [{{ getMag() }}] (growth = {{ city.growth * 10 }}%)<br>
      Access {{ city.access }}<br>
      Stability {{ city.stability }}<br>
      <span v-if="city.port">
        Has a port [{{ city.port.x }},{{ city.port.y }}]
      </span>
    </h2>
    <div>
      Roads to <span v-for="r of city.roads" :key="r.id">{{ r.to.name }}//</span>
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
        <table>
          <tr>
            <th>Resource</th>
            <th v-for="r of res" :key="r">
              {{ getResourceName(r) }}
            </th>
          </tr>
          <tr>
            <th>Stock</th>
            <td v-for="r of res" :key="r">
              {{ city.resources.get(r) }}
            </td>
          </tr>
          <tr>
            <th>Production</th>
            <td v-for="r of res" :key="r">
              {{ city.production.get(r) }}
            </td>
          </tr>
          <tr>
            <th>Demand</th>
            <td v-for="r of res" :key="r">
              {{ city.needs.get(r) }}
            </td>
          </tr>
          <tr>
            <th>Provided</th>
            <td
              v-for="r of res"
              :key="r"
              :class="{ danger: city.deficits.get(r) > 0 }"
            >
              <span v-if="city.needs.get(r) !== undefined">{{
                city.needs.get(r) - city.deficits.get(r)
              }}</span>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
import { allResources, City, Industry, IndustryName, Resource } from "@/game/model/models";
import { Utils } from "@/utils";
@Component
export default class CityViewer extends Vue {
  @Prop() city!: City;
  res = allResources;
  getIndustry(name: IndustryName): Industry {
    return Industry.industries.get(name)!;
  }

  getResourceName(r: Resource): string {
    return Resource[r];
  }

  getMag(): number {
    return Utils.getMag(this.city.population);
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss"></style>
