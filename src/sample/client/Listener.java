package sample.client;

import javafx.geometry.Orientation;
import javafx.scene.layout.FlowPane;
import sample.messages.Message;
import sample.messages.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Listener implements Runnable{

    private String name;
    private Socket socket;
    private String hostname;
    private int port;
    private Controller controller;
    private ObjectOutputStream out;
    private InputStream is;
    private ObjectInputStream in;
    private OutputStream os;

    private String category;
    private StringBuilder hiddenWord;
    private int wordLength;
    private int wordsCounter;
    private boolean yourTurn= false;
    private String oppenentName;
    private int opponentNumber;
    private int win;
    private int sum;
    private int account;
    List<Field> labels = new ArrayList<>();
    List<FlowPane> words = new ArrayList<>();

    Listener(String hostname, int port, String name, Controller controller) {
        this.hostname=hostname;
        this.port=port;
        this.name=name;
        this.controller=controller;
    }
    @Override
    public void run() {
        try {
            socket = new Socket(hostname, port);
            os = socket.getOutputStream();
            out = new ObjectOutputStream(os);
            is = socket.getInputStream();
            in = new ObjectInputStream(is);
            boolean info = false;
            System.out.println("Stworzono "+name);
            System.out.println("Polaczono sie z serwerem!");

            Message firstMessage = new Message();
            firstMessage.setMsg("Pierwsza wiadomosc do serwera!");
            firstMessage.setType(MessageType.NOTIFICATION);
            sendMessage(firstMessage);


                    while(socket.isConnected()) {
                        Message message;
                        message = (Message) in.readObject();
                        if (message != null) {

                            switch (message.getType()) {
                                case PASSWORD:
                                    this.hiddenWord = new StringBuilder(message.getMsg());
                                    wordLength=hiddenWord.length();
                                    if(!info){
                                        controller.updatePlayerPanel();
                                        info = true;
                                    }
                                    break;

                                case CATEGORY:
                                        this.category = message.getMsg();
                                        this.controller.createBoard();
                                    break;

                                case SIGN:
                                    System.out.println(message.getName()+">ilosc trafien:"+message.getCountSign());
                                    setSum(win*message.getCountSign());
                                    setAccount(win*message.getCountSign());
                                    controller.setCash(Integer.toString(sum));
                                    controller.setKeyBoard(false);
                                    controller.setButtonStart(false);
                                    if(message.getCountSign()==0) {
                                        changePlayer();
                                    }
                                        break;

                                case NOTIFICATION:
                                    System.out.println(message.getName()+">"+message.getMsg());
                                    break;
                                case START:
                                        controller.setClickedStart(false);
                                    break;

                                case INCOMPLETEPASSWORD:
                                    hiddenWord= new StringBuilder(message.getMsg());
                                    System.out.println(message.getName()+">"+hiddenWord);
                                    this.controller.changeBoard();
                                    break;

                                case INFO:
                                    controller.setInfo(message.getMsg());
                                    break;

                                case TURN:
                                    setYourTurn(message.isTurn());
                                    controller.sequencing(yourTurn);
                                    break;

                                case RANDOM:


                                    if(!message.getMsg().equals("BANKRUT")) {
                                        setWin(Integer.parseInt(message.getMsg()));
                                        controller.setCash(message.getMsg());
                                    }
                                    else {
                                        changePlayer();
                                        setWin(0);
                                        controller.setCash(message.getMsg());
                                        controller.setKeyBoard(false);
                                    }


                                    break;

                                case WIN:
                                    if(message.getMsg().equals("win")) {
                                        controller.winnerBanner();

                                    }
                                    else if(message.getMsg().equals("lose")) {
                                        controller.loseBanner();
                                        changePlayer();
                                    }
                                    else
                                        controller.endBanner();
                                    break;

                                case RESET:
                                    Message newGame = new Message();
                                    newGame.setName(getName());
                                    newGame.setType(MessageType.START);
                                    newGame.setMsg("START");
                                    try{
                                        sendMessage(newGame);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;

                                case BUTTON:
                                    controller.disableButton(message.getMsg());
                                    break;

                                case PLAYER:
                                    oppenentName = message.getMsg();
                                    opponentNumber = message.getCountSign();
                                    break;

                                case ACCOUNT:
                                    controller.updatePlayersPanel(message);
                                    break;

                                case CURRENTACCOUNT:
                                    controller.updateCurrentAcount(message);
                                    break;
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println("Blad przy odbiorze wiadomosci!");
                }
         catch (IOException e) {
            System.out.println("Nie mozna polaczyc sie z serwerem!");
        }
    }

   synchronized void sendMessage(Message msg) throws IOException{
        try{
            out.writeObject(msg);
            out.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    private void changePlayer(){
        Message turn = new Message();
        turn.setName(getName());
        turn.setMsg("TURN");
        turn.setTurn(false);
        turn.setType(MessageType.TURN);
        try {
            sendMessage(turn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setYourTurn(false);
        controller.setInfo("To nie twoja tura!");
    }


    private void addLabels() {
        words.add(new FlowPane(Orientation.VERTICAL,2,2));
        words.get(wordsCounter).setPrefWrapLength(100);
        words.get(wordsCounter).setMaxSize(800,50);
        for (int i = 0; i < wordLength; i++) {
            labels.add(new Field(hiddenWord.charAt(i)));
            if(hiddenWord.charAt(i)=='_') {
                words.add(new FlowPane(Orientation.VERTICAL,2,2));
                wordsCounter++;
                words.get(wordsCounter).setPrefWrapLength(100);
                words.get(wordsCounter).setMaxSize(800,50);
            }
            else
                words.get(wordsCounter).getChildren().add(labels.get(i));
        }
    }

    void setPanels() {
        addLabels();
        for (int i = 0; i < wordLength; i++) {
            String ClickedSign = String.valueOf(hiddenWord.charAt(i));
            if ((hiddenWord.charAt(i) != '#') && (hiddenWord.charAt(i) != '_')) {
                labels.get(i).setText(ClickedSign);
            }
        }
    }

    void exit() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e)
        {
            System.out.println("Nie udalo sie zamknac polaczenia!");
        }
    }


    int getWordLength() {
        return wordLength;
    }


    String getHidden() {
        return hiddenWord.toString();
    }

    public void setHiddenWord(StringBuilder hiddenWord) {
        this.hiddenWord = hiddenWord;
    }

    int getWordsCounter() {
        return wordsCounter;
    }

    public void setWordsCounter(int wordsCounter) {
        this.wordsCounter = wordsCounter;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }

    String getCategory() {
        return category;
    }

    String getName() { return name; }

    private void setYourTurn(boolean yourTurn) { this.yourTurn = yourTurn; }

    boolean isYourTurn() { return yourTurn; }

    private void setWin(int win) { this.win = win; }

    int getWin() { return win; }

    void setSum(int sum) { this.sum = sum; }

    int getSum() { return sum; }

    int getAccount() { return account; }

    void setAccount(int account) {
        if(account==-1)
            this.account=0;
        else
        this.account = this.account + account; }

    public int getOpponentNumber() {
        return opponentNumber;
    }

    public String getOppenentName() {
        return oppenentName;
    }
}
