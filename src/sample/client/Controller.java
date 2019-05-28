package sample.client;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import sample.messages.Message;
import sample.messages.MessageType;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class Controller  implements Initializable {
    @FXML public Button a,ą,b,c,ć,d,e,ę,f,g,h,i,j,k,l,ł,m,n,ń,o,ó,p,r,s,ś,t,u,w,y,z,ż,ź;
    @FXML public FlowPane Gpane;
    @FXML public Button Buttonstart;
    @FXML public FlowPane Kategorie;
    @FXML public GridPane keyboard;
   // Thread x;
    private Listener listener;
    public boolean clickedStart=true;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
                String name = new String();
                TextInputDialog dialog = new TextInputDialog("Name");
                dialog.setHeaderText(null);
                dialog.setTitle("Zacznij gre");
                dialog.setContentText("Podaj swoja nazwe:");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()){
                    name = result.get();
                }

       if(!name.equals(null)) {

           listener = new Listener("localhost", 2004, name, this);
           Thread x = new Thread(listener);
           x.start();

       }
    }

    @FXML
    public void clickSign(Event e) {
        Message msg = new Message();
        msg.setType(MessageType.SIGN);
        Button x = (Button) e.getSource();
        msg.setSign(x);
        x.setDisable(true);
        String ClickedSignStr;
        ClickedSignStr = (x.getId()).toUpperCase();
        msg.setMsg(ClickedSignStr);

        try {
            listener.sendMessage(msg);
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }

    @FXML
    public void starting(Event e) {



        if(clickedStart) {
            Message msg = new Message();
            msg.setType(MessageType.START);
            msg.setMsg("START");
            try {
                listener.sendMessage(msg);

            } catch (IOException ex) {
                ex.printStackTrace();

            }

            clickedStart=false;
           // Buttonstart.setText("LOSUJ");
        }
        else {
            //TODO LOSOWANIE KWOT
        }
    }

    @FXML
    public void exit() {
        Message msg = new Message();
        msg.setType(MessageType.EXIT);
        msg.setMsg("EXIT");
        try {
            listener.sendMessage(msg);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        listener.exit();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Platform.exit();
            }
        });
    }

    public void createBoard() {
        listener.setPanels();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Gpane.getChildren().clear();
                Gpane.setAlignment(Pos.CENTER);
                Gpane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                Kategorie.setAlignment(Pos.CENTER);

                Text kategory = new Text();
                kategory.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR,20));
                kategory.setText("Kategoria: " + listener.getCategory());
                Kategorie.getChildren().add(kategory);

                for (int i = 0; i < listener.getWordsCounter()+1; i++) {
                    Gpane.getChildren().add(listener.words.get(i));
                }
            }
        });

    }

    public void changeBoard() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < listener.getWordLength(); i++) {
                    char x = listener.getHidden().charAt(i);
                    String clickedSign = Character.toString(x);
                    if(listener.getHidden().charAt(i)!='#' && listener.getHidden().charAt(i)!='_') {
                        listener.labels.get(i).setText(clickedSign);
                    }
                }
            }
        });
    }

    public void setInfo(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Text x = new Text("Oczekiwanie na innych graczy!");
                Gpane.getChildren().add(x);
            }
        });
    }

    public void disableButton(Button x)
    {
        System.out.println("disable button:"+x.getId());
        x.setDisable(true);
    }

    public void setClickedStart(boolean clickedStart) {
        this.clickedStart = clickedStart;
    }

    public boolean getClickedStart(){
        return this.clickedStart;
    }

    public void setButtonStart()
    {
        Buttonstart.setText("LOSUJ");
    }

}
