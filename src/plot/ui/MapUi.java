package plot.ui;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import plot.Place;

public class MapUi extends StackPane {
    private final Place[][] map;

    public MapUi(Place[][] map) {
        this.map = map;
        GridPane gridLayout = new GridPane();

        // add each city
        for (int x = 0; x < map.length; x++){
            for (int y = 0; y < map[x].length; y++){
                gridLayout.add(new PlaceLabel(map[x][y]), x, y);
            }
        }

        getChildren().add(gridLayout);
    }

    public void update() {
        getChildren().forEach(node -> {
            ((PlaceLabel)node).update();
        });
    }

    private class PlaceLabel extends Label {
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
            this.setPrefSize(40,40);
        }

        public void update() {
            tooltip.update();
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
