package plot.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

public class LogTextArea extends Label implements Log {

    private StringBuffer sb = new StringBuffer();

    public LogTextArea() {
        this.setWrapText(true);

        final ContextMenu menu = new ContextMenu();
        menu.getItems().add(createItem("Clear console", e -> {
            this.setText("");
        }));
        this.setContextMenu(menu);
        this.setPrefHeight(200);
    }

    private MenuItem createItem(String name, EventHandler<ActionEvent> a) {
        final MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(a);
        return menuItem;
    }

    public void log(String s) {
        sb.append(s).append("\n");
        setText(sb.toString());
    }

}
