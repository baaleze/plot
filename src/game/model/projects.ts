import { People, Project } from "./models";

export class ProjectFactory {
    static investInIndustry(owner: People): Project {
        return {
            owner,
            currentStep: 0,
            totalSteps: 5, // TODO
            finished: w => {
                // TODO
            }
        };
    }
}