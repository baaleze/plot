package plot.ui;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import plot.Event;
import plot.World;
import plot.people.People;

import java.util.List;
import java.util.stream.Collectors;

public class LogTextArea extends Label implements Log {

    private final World world;
    private final ObjectProperty<People> people;
    private StringBuffer sb = new StringBuffer();
    private static int LOG_LIMIT = 20;

    public LogTextArea(ObjectProperty<People> peopleSelected, World w) {
        this.world = w;
        this.people = peopleSelected;
        this.setWrapText(true);

        final ContextMenu menu = new ContextMenu();
        menu.getItems().add(createItem("Clear console", e -> {
            this.setText("");
        }));
        this.setContextMenu(menu);
        this.setPrefHeight(200);

        peopleSelected.addListener((observable, oldValue, newValue) -> {
            updateLog(world.getEventsFor(newValue).stream().map(Event::toString).collect(Collectors.toList()));
        });
    }

    private MenuItem createItem(String name, EventHandler<ActionEvent> a) {
        final MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(a);
        return menuItem;
    }

    public void updateLog(List<String> events) {
        sb.setLength(0);
        if (events != null) {
            events.forEach(s -> sb.append(s).append("\n"));
        }
        setText(sb.toString());
    }

    public void update() {
        updateLog(world.getEventsFor(people.get()).stream().map(Event::toString).collect(Collectors.toList()));
    }

}
