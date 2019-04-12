package plot.ui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import plot.Place;
import plot.people.People;

public class ResidentsPane extends ScrollPane {
    private final ObjectProperty<Place> city;
    private final VBox vBox;
    private final ObjectProperty<People> peopleSelect;

    public ResidentsPane(ObjectProperty<Place> citySelected, ObjectProperty<People> peopleSelected) {
        this.city = citySelected;
        this.peopleSelect = peopleSelected;
        setFitToWidth(true);
        // setPrefHeight(400);
        this.vBox = new VBox();
        setContent(vBox);
        update(city.get());
        city.addListener((c, oldPlace, newPlace) -> {
            update(newPlace);
        });
    }

    public void update(Place p) {
        // update residents list
        vBox.getChildren().clear();
        // clear the people select
        peopleSelect.setValue(null);
        if (p != null) {
            p.residents.forEach(r -> {
                Label l = new Label(r.getDescription());
                l.setOnMouseClicked((mouseEvent) -> {
                    peopleSelect.setValue(r);
                });
                vBox.getChildren().add(l);
            });
        }
    }
}
