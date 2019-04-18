package plot.people;

import plot.Place;
import plot.RelationType;
import plot.World;

public class SecretHireToKill extends SecretKill {

    public SecretHireToKill(People me, People target) {
        super(me, target);
    }

    @Override
    public void divulgate(World world, Place p) {
        // only hired someone, target is not dead yet maybe
        if (people.killer == null && !people.knownSecrets.contains(this)){
            // still alive
            world.updateReputation(owner, world.whereIs(owner), -1);
            world.updateRelation(people, owner, RelationType.ATTACKED_ME, 2, null, null);
            people.knownSecrets.add(this); // now he knows!!
            clearSecret(world);
        } else {
            // dead, same reaction as if I was the killer
            super.divulgate(world, p);
        }
    }

    @Override
    public void divulgateToConcernedPeople(World world) {
        // only hired someone, target is not dead yet maybe
        if (people.killer == null){
            // still alive
            world.updateReputation(owner, world.whereIs(owner), -1);
            world.updateRelation(people, owner, RelationType.ATTACKED_ME, 2, null, null);
            people.knownSecrets.add(this); // now he knows!!
        }
    }
}
