import { Action, People, Project } from "./models";

export class ActionFactory {
    static doNothing(owner: People): Action {
        return { 
            owner,
            resolve: _ => {}
        };
    }

    static startProject(owner: People, project: Project): Action {
        return {
            owner,
            resolve: w => {
                project.currentStep++;
                project.owner = owner;
                owner.projects.push(project);
            }
        };
    }

    static continueProject(owner: People, project: Project): Action {
        return {
            owner,
            resolve: w => {
                project.currentStep++;
                if (project.currentStep === project.totalSteps) {
                    // project is done
                    project.finished(w);
                    // remove it
                    owner.projects.splice(owner.projects.indexOf(project), 1);
                }
            }
        };
    }
}