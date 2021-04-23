import { Utils } from "@/utils";
import { ActionFactory } from "../model/actions";
import { Action, People, World } from "../model/models"

export class PeopleEvolution {

    public static evolution(world: World): void {
        // resolve what has been done last week
        world.people.forEach(p => p.currentAction.resolve(world));

        // generate some people from nothing
        // TODO

        // take all living people and look what they want to do this week
        world.people.forEach(p => PeopleEvolution.whatToDo(world, p));

    }

    public static whatToDo(world: World, people: People): void {
        // pile all the options with weights by checking each goal not satisfied and without project initiated
        let options: [Action, number][] = [];
        people.goals
            .filter(g => g.need > 0 && g.project === undefined)
            .forEach(goal => options.push(...goal.generateActions(world)));
        // also add current long term projects
        people.projects.forEach(project => options.push(
            [ActionFactory.continueProject(people, project), project.forGoal.need + (project.currentStep * 5 / project.totalSteps)]
        ));

        // choose one of them
        if (options.length > 0) {
            people.currentAction = Utils.randomInWeightedMap(new Map(options));
        } else {
            people.currentAction = ActionFactory.doNothing(people);
        }
    }

}