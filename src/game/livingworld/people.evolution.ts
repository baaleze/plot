import { People, World } from "../model/models"

export class PeopleEvolution {

    public static evolution(world: World): void {
        // resolve what has been done last week
        world.people.forEach(p => PeopleEvolution.resolve(world, p));

        // generate some people from nothing
        // TODO

        // take all living people and look what they want to do this week
        world.people.forEach(p => PeopleEvolution.whatToDo(world, p));

    }

    public static resolve(world: World, people: People): void {
        // TODO
    }

    public static whatToDo(world: World, people: People): void {
        // pile all the options with weights


        // choose one of them
    }

}