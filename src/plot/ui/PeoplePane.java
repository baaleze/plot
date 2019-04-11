package plot.ui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import plot.people.People;

public class PeoplePane extends StackPane {
    private final Label label;

    public PeoplePane(ObjectProperty<People> peopleSelected) {
        this.label = new Label();
        this.label.setWrapText(true);
        this.getChildren().add(this.label);
        peopleSelected.addListener((observable, oldValue, newValue) -> update(newValue));
        this.update(peopleSelected.get());
    }

    private void update(People people) {
        if (people != null) {
            this.label.setText(people.getFullDescription());
        } else {
            this.label.setText("CLICK ON PEOPLE");
        }
    }
}
