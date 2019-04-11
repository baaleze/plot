package plot.ui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import plot.Place;

public class ResidentsPane extends ScrollPane {
    private final ObjectProperty<Place> city;
    private TextArea text;

    public ResidentsPane(ObjectProperty<Place> citySelected) {
        this.city = citySelected;
        this.text = new TextArea();
        setFitToWidth(true);
        setContent(this.text);
        update(city.get());
        city.addListener((c, oldPlace, newPlace) -> {
            update(newPlace);
        });
    }

    public void update(Place p) {
        // TODO update text from residents
    }
}
