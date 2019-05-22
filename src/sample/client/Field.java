package sample.client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


class Field extends Label{

    Field(char fromString) {
        super();
        this.setPadding(new Insets(5,5,5,5));
        int width = 50;
        int heigth = 50;
        this.setMinSize(width, heigth);
        this.setAlignment(Pos.CENTER);
        this.setFont(new Font("Arial",25));
        if(!(fromString=='_')) {
            this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
            this.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(1),
                    new Insets(0.0,0.0,0.0,0.0))));
        }
    }

}
