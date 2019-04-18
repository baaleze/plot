package plot.people;

import plot.Place;
import plot.RelationType;
import plot.Util;
import plot.World;
import plot.goal.KillSomebody;

public class SecretKill extends Secret {

    public SecretKill(People me, People target) {
        super(me, null, target, null, 0);
    }

    @Override
    public void divulgate(World world, Place p) {
        // city rep
        world.updateReputation(owner, p, -2);
        world.places.forEach(city -> world.updateReputation(owner, city, -1));
        // relatives too
        for(People relative: world.getAllRelatives(people)) {
            // add or amplify relation
            world.updateRelation(relative, owner, RelationType.KILLED_RELATIVE, 4, null, people);
            // maybe vengeance ?
            if (relative.isMoreOfAPersonnality(relative.personnality.vengeful) && Util.testStat(relative.personnality.vengeful)) {
                relative.newGoal(new KillSomebody(owner), null);
            }
        }
        // TODO discredit in organizations

        // clear secret
        clearSecret(world);
    }

    @Override
    public void divulgateToConcernedPeople(World world) {
        super.divulgateToConcernedPeople(world);// nothing happens the concerned people is dead
    }
}
