package plot.ui;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import plot.Place;
import plot.World;
import plot.people.People;

import java.awt.*;

public class Plot extends Application {

    private World world;

    private ObjectProperty<Place> citySelected = new SimpleObjectProperty<>();
    private ObjectProperty<People> peopleSelected = new SimpleObjectProperty<>();
    private MapUi mapUi;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.world = new World();
        this.world.create();
        buildUI(primaryStage);
    }

    private void buildUI(Stage primaryStage) {
        VBox root = new VBox();
        // upper is multiple node horizontal
        HBox upper = new HBox();

        // 1st left is map up and residents list down
        VBox left = new VBox();
        this.mapUi = new MapUi(world.map, citySelected);
        ResidentsPane residents = new ResidentsPane(citySelected, peopleSelected);
        left.getChildren().addAll(mapUi, residents);

        // 2nd is people info
        PeoplePane peoplePane = new PeoplePane(peopleSelected);

        // 3rd is people relation

        // 4th is people knowledge

        upper.getChildren().addAll(left, peoplePane);
        // lower is log
        LogTextArea log = new LogTextArea();
        world.setLog(log);
        root.getChildren().addAll(upper, log);

        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(1000);
        primaryStage.setHeight(800);
        primaryStage.show();
    }

    public void update() {
        citySelected.setValue(null);
        peopleSelected.setValue(null);
        mapUi.update();
    }
}
