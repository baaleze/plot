package plot.people;

import plot.Item;
import plot.Place;
import plot.RelationType;
import plot.World;
import plot.goal.KillSomebody;

public class SecretSteal extends Secret {

    public SecretSteal(People me, Item i, People p, Place pl, int money) {
        super(me, i, p, pl, money);
    }

    @Override
    public void divulgate(World world, Place p) {
        if (people != null) {
            world.updateReputation(owner, p, -1);
            if (!people.knownSecrets.contains(this)) {
                applyOnTarget(world);
            }
        } else {
            // stole place
            world.updateReputation(owner, place, -2);
        }
        // TODO discredit in organizations

        // clear secret
        clearSecret(world);
    }

    @Override
    public void divulgateToConcernedPeople(World world) {
        if (people != null) {
            applyOnTarget(world);
        }
    }

    private void applyOnTarget(World world) {
        world.updateRelation(people, owner, RelationType.STOLE, 2, item, null);
        // vengeance
        if (people.isMoreOfAPersonnality(people.personnality.vengeful) && people.isMoreOfAPersonnality(people.personnality.violent)) {
            people.newGoal(new KillSomebody(owner), null);
        }
        people.knownSecrets.add(this);
    }

}
