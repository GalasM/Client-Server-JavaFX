package sample.client;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
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
    @FXML public FlowPane Gpane;
    @FXML public Button Buttonstart;
    @FXML public Button FindPassword;
    @FXML public FlowPane Kategorie;
    @FXML public FlowPane Cash;
    @FXML public FlowPane Info;
    @FXML public VBox playersPanel;
    @FXML public GridPane keyboard;
    @FXML public AnchorPane pan;
    @FXML public Text OverallAccount;
    @FXML public Text CurrentAccont;
    @FXML public Text OverallAccount1;
    @FXML public Text CurrentAccont1;
    private Listener listener;
    private boolean clickedStart=true;


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
        if(listener.isYourTurn()){
        Message msg = new Message();
        msg.setType(MessageType.SIGN);
        Button x = (Button) e.getSource();
        x.setDisable(true);
        String ClickedSignStr;
        ClickedSignStr = (x.getId()).toUpperCase();
        msg.setMsg(ClickedSignStr);
        msg.setName(listener.getName());

        Message turn = new Message();
        turn.setName(listener.getName());
        turn.setTurn(false);

        try {
            listener.sendMessage(msg);
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }
        else{
            //TODO ZMIANA GRACZA
        }
    }

    @FXML
    public void starting(Event e) {
        Message msg = new Message();
        if(clickedStart) {
            Message msg1 = new Message();
            msg1.setName(listener.getName());
            msg1.setType(MessageType.PLAYER);
            msg.setMsg(listener.getName());
            try {
                listener.sendMessage(msg1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            FindPassword.setDisable(true);
            FindPassword.setVisible(true);
            msg.setType(MessageType.START);
            msg.setMsg("START");
            msg.setName(listener.getName());
            try {
                listener.sendMessage(msg);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            clickedStart=false;

        }
        else {
            msg.setType(MessageType.RANDOM);
            msg.setMsg("RANDOM");
            msg.setName(listener.getName());
            try {
                setButtonStart(true);
                setKeyBoard(true);
                setButtonPassword(false);
                listener.sendMessage(msg);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void find(){
        String password = new String();
        TextInputDialog dialog = new TextInputDialog("Podaj haslo");
        dialog.setHeaderText(null);
        dialog.setTitle("Zgadnij haslo!");
        dialog.setContentText("Podaj haslo");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            password = result.get();
        }
        Message msg = new Message();
        msg.setMsg(password.toUpperCase());
        msg.setName(listener.getName());
        msg.setType(MessageType.FIND);
        try {
            listener.sendMessage(msg);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @FXML
    public void exit() {
        Message msg = new Message();
        msg.setType(MessageType.EXIT);
        msg.setMsg("EXIT");
        msg.setName(listener.getName());
        try {
            listener.sendMessage(msg);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        listener.exit();
        Platform.runLater(() -> Platform.exit());
    }

    void createBoard() {
        listener.setPanels();
        Platform.runLater(() -> {
            Gpane.getChildren().clear();
            Gpane.setAlignment(Pos.CENTER);
            Gpane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
            Kategorie.setAlignment(Pos.CENTER);

            Kategorie.getChildren().clear();
            Text kategory = new Text();
            kategory.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR,20));
            kategory.setText("Kategoria: " + listener.getCategory());
            Kategorie.getChildren().add(kategory);

            for (int i = 0; i < listener.getWordsCounter()+1; i++) {
                Gpane.getChildren().add(listener.words.get(i));
            }
        });
    }

    void changeBoard() {
        Platform.runLater(() -> {
            for (int i = 0; i < listener.getWordLength(); i++) {
                char x = listener.getHidden().charAt(i);
                String clickedSign = Character.toString(x);
                if(listener.getHidden().charAt(i)!='#' && listener.getHidden().charAt(i)!='_') {
                    listener.labels.get(i).setText(clickedSign);
                }
            }
        });
    }

    void updatePlayersPanel(Message x){
        Platform.runLater(() -> {
            String name = x.getMsg();
            int account = x.getCountSign();
                if(x.getName().equals(listener.getOppenentName())) {
                    if (account == 0)
                        OverallAccount1.setText(listener.getOppenentName() + " Ogólny stan konta: 0");
                    else
                        OverallAccount1.setText(listener.getOppenentName() + " Ogólny stan konta: "+account);
                }
                else if(x.getName().equals(listener.getName())){
                    if(account==0)
                        OverallAccount.setText(listener.getName() + " Ogólny stan konta: 0");
                    else
                        OverallAccount.setText(listener.getName() + " Ogólny stan konta: "+account);
                }
        });
    }

    void updatePlayerPanel(){
        Platform.runLater(() -> {
                    OverallAccount.setText(listener.getName()+ " Ogólny stan konta: 0");
                    CurrentAccont.setText(listener.getName()+ " Obecny stan konta: 0");
                    OverallAccount1.setText(listener.getOppenentName()+" Ogólny stan konta: 0");
                    CurrentAccont1.setText(listener.getOppenentName()+" Obecny stan konta: 0");
        });
    }

    void updateCurrentAcount(Message x){
        Platform.runLater(() -> {
            int account = x.getCurrentAccount();
            if(x.getName().equals(listener.getOppenentName())) {
                if (account == 0) {
                    CurrentAccont1.setText(listener.getOppenentName() + " Obecny stan konta: 0");
                }
                else
                    CurrentAccont1.setText(listener.getOppenentName() + " Obecny stan konta: "+account);
            }
            else if(x.getName().equals(listener.getName())){
                if(account==0)
                    CurrentAccont.setText(listener.getName() + " Obecny stan konta: 0");
                else
                    CurrentAccont.setText(listener.getName() + " Obecny stan konta: "+account);
            }
        });
    }

    void setInfo(String info){
        Platform.runLater(() -> {
            Text x = new Text(info);
            Info.getChildren().add(x);
        });
    }

    void sequencing(boolean turn)
    {
        Platform.runLater(() -> {
            if(!turn) {
                Text info = new Text("Nie twoja tura");
                Info.getChildren().clear();
                Info.getChildren().add(info);
                setButtonStart(true);  //jesli nie jest twoja runda wysylasz true zeby wylaczyc przycisk
                setButtonPassword(true);
            }
            else {
                Text info = new Text("Twoja tura");
                Info.getChildren().clear();
                Info.getChildren().add(info);
                setButtonStart(false);//jesli jest twoja runda
                setButtonPassword(false);
            }
        });
    }

    synchronized void winnerBanner(){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wygrales!");
            alert.setHeaderText(null);
            alert.setContentText("Gratulacje wygrales: "+listener.getAccount()+"!");
            alert.showAndWait();

            Message account = new Message();
            account.setName(listener.getName());
            account.setType(MessageType.ACCOUNT);
            account.setMsg(Integer.toString(listener.getAccount()));
            Message reset = new Message();
            reset.setName(listener.getName());
            reset.setType(MessageType.RESET);
            reset.setMsg("RESET");
            try {
                listener.sendMessage(account);
                listener.sendMessage(reset);
                enableButtons();
            } catch (IOException e) {
                e.printStackTrace();
            }
            resetClient();
        });

    }
    synchronized void loseBanner(){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Bledne haslo!");
            alert.setHeaderText(null);
            alert.setContentText("Haslo jest inne!");
            alert.showAndWait();
        });

    }

    synchronized void endBanner(){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Przegrales!");
            alert.setHeaderText(null);
            alert.setContentText("Przegrales!");
            alert.showAndWait();

        });
        resetClient();
        enableButtons();

    }

    void setCash(String cash){
        Message newCurrent = new Message();
        newCurrent.setType(MessageType.CURRENTACCOUNT);
        newCurrent.setName(listener.getName());
        Platform.runLater(() -> {
            Text textCash;
            if(cash.equals("BANKRUT")) {
                listener.setSum(0);
                listener.setAccount(-1);
                textCash = new Text("Wylosowano: BANKRUT!"+" Konto: "+listener.getAccount());
            }
            else {
                textCash = new Text("Wylosowano: " + listener.getWin() + " Wygrana: " + listener.getSum() + " Konto: " + listener.getAccount());
            }
            Cash.getChildren().clear();
            Cash.getChildren().add(textCash);
            CurrentAccont.setText(listener.getName()+" Obecny stan konta: "+listener.getAccount());
        });

        newCurrent.setCurrentAccount(listener.getAccount());
        try {
            listener.sendMessage(newCurrent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void resetClient(){
        listener.words.clear();
        listener.labels.clear();
        listener.setWordsCounter(0);
        listener.setWordLength(0);
        listener.setHiddenWord(new StringBuilder(""));
        listener.setAccount(-1);
        Platform.runLater(() ->{
            CurrentAccont.setText(listener.getName()+" Obecny stan konta: 0");
            CurrentAccont1.setText(listener.getOppenentName()+" Obecny stan konta: 0");
        });
    }

    void enableButtons(){
       for(Node child:keyboard.getChildren()){
           if(child.isDisable())
               child.setDisable(false);
       }
    }

    void disableButton(String sign){
        for(Node child:keyboard.getChildren()){
            if(child.getId().equals(sign.toLowerCase())) {
                child.setDisable(true);
            }
        }
    }

    void setButtonStart(boolean x)
    {
        Platform.runLater(() -> {
                Buttonstart.setDisable(x);
                Buttonstart.setText("LOSUJ");
                });
    }

    void setButtonPassword(boolean x)
    {
        Platform.runLater(() -> FindPassword.setDisable(x));
    }

    void setKeyBoard(boolean x){
            Platform.runLater(() -> keyboard.setVisible(x));
    }

    void setClickedStart(boolean clickedStart) { this.clickedStart = clickedStart;}

}
