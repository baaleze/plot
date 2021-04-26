import { ActionResolver } from "./actions";
import { Action, Goal, People, Project, World } from "./models";

export class GoalResolver {
  public static generateActions(world: World, goal: Goal): [Action, number][] {
    return [];
  }
}
