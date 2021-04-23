import { ActionFactory } from "./actions";
import { Action, Goal, People, Project, World } from "./models";

export class GoalFactory {
    static bePowerful(owner: People): Goal {
        return new PowerGoal(owner);
    }
}

export class PowerGoal implements Goal {
    
    public project: Project | undefined;
    public need = 0;

    constructor(
        public owner: People
    ) {
        // TODO compute starting need depending on starting position
    }

    generateActions(world: World): [Action, number][] {
        const actions: [Action, number][] = [];
        
        return actions;
    }

    updateNeed(world: World): void {

    }
}