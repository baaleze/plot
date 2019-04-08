package plot.goal;

import plot.Entity;
import plot.People;
import plot.World;
import plot.action.Action;

import java.util.Optional;

public class GetInfo extends Goal {

    private final InfoType infoType;
    private final Entity target;

    public GetInfo(InfoType type, Entity target) {
        this.infoType = type;
        this.target = target;
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        // TODO
        return Optional.empty();
    }
}
