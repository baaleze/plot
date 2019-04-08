package plot.goal;

import plot.People;
import plot.World;
import plot.action.Action;

import java.util.Optional;

public class GetItem extends Goal {

    public GetItem() {
        this.type = GoalType.GET_ITEM;
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        // TODO
        return Optional.empty();
    }
}
