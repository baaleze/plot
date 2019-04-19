package plot.ui;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import plot.Place;

public class MapUi extends StackPane {
    private final Place[][] map;
    private final ObjectProperty<Place> city;
    private final GridPane gridLayout;

    public MapUi(Place[][] map, ObjectProperty<Place> citySelected) {
        this.map = map;
        this.city = citySelected;
        this.gridLayout = new GridPane();

        // add each city
        for (int x = 0; x < map.length; x++){
            for (int y = 0; y < map[x].length; y++){
                Place p = map[x][y];
                PlaceLabel placeLabel = new PlaceLabel(p);
                if (p != null){
                    placeLabel.setOnMouseClicked(event -> this.city.setValue(p));
                }
                gridLayout.add(placeLabel, x, y);
            }
        }

        getChildren().add(gridLayout);
    }

    public void update() {
        this.gridLayout.getChildren().forEach(node -> {
            ((PlaceLabel)node).update();
        });
    }

    private class PlaceLabel extends Label {
        public static final int DIM = 50;
        private Place place;
        private PlaceToolTip tooltip;

        public PlaceLabel(Place p) {
            if (p != null){
                this.place = p;
                this.setText(p.name);
                tooltip = new PlaceToolTip(p);
                this.setTooltip(tooltip);
            } else {
                this.setText(".");
            }
            GridPane.setHalignment(this, HPos.CENTER);
            GridPane.setValignment(this, VPos.CENTER);
            this.setWrapText(true);
            this.setPrefSize(DIM, DIM);
        }

        public void update() {
            if (tooltip != null) {
                tooltip.update();
            }
        }
    }

    private class PlaceToolTip extends Tooltip {
        private final Place place;

        public PlaceToolTip(Place p) {
            this.place = p;
            this.update();
        }

        private void update() {
            if (place != null) {
                setText("Population : " + place.population + "\nWealth : " + place.wealth);
            } else {
                setText("");
            }
        }


    }
}
