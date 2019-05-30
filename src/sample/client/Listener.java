package sample.client;


import javafx.application.Platform;
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
    public String hostname;
    public int port;
    public Controller controller;
    private ObjectOutputStream out;
    private InputStream is;
    private ObjectInputStream in;
    private OutputStream os;

    private String category;
    private StringBuilder hiddenWord;
    private int wordLength;
    private int wordsCounter;
    private boolean yourTurn= false;
    private int win;
    private int sum;
    List<Field> labels = new ArrayList<>();
    List<FlowPane> words = new ArrayList<>();

    public Listener(String hostname, int port, String name, Controller controller) {
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

            System.out.println("Stworzono "+name);
            System.out.println("Polaczono sie z serwerem!");

            Message firstMessage = new Message();
            firstMessage.setMsg("Pierwsza wiadomosc do serwera!");
            firstMessage.setType(MessageType.NOTIFICATION);
            sendMessage(firstMessage);


                    while(socket.isConnected()) {
                        Message message = null;
                        message = (Message) in.readObject();
                        if (message != null) {

                            switch (message.getType()) {
                                case PASSWORD:
                                    this.hiddenWord = new StringBuilder(message.getMsg());
                                    wordLength=hiddenWord.length();
                                    break;

                                case CATEGORY:
                                        this.category = message.getMsg();
                                        this.controller.createBoard();

                                    break;

                                case SIGN:
                                    System.out.println(message.getName()+">ilosc trafien:"+message.getCountSign());
                                    setSum(win*message.getCountSign());
                                    controller.setCash(Integer.toString(sum));
                                    //zrobic disable przycisku litery
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
                                    controller.setCash(message.getMsg());
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

    public void sendMessage(Message msg) throws IOException{
        try{

            out.writeObject(msg);
            out.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
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

    public void setPanels() {
        addLabels();
        for (int i = 0; i < wordLength; i++) {
            String ClickedSign = String.valueOf(hiddenWord.charAt(i));
            if ((hiddenWord.charAt(i) != '#') && (hiddenWord.charAt(i) != '_')) {
                labels.get(i).setText(ClickedSign);
            }
        }
    }

    public void exit() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e)
        {
            System.out.println("Nie udalo sie zamknac polaczenia!");
        }
    }


    public int getWordLength() {
        return wordLength;
    }


    public String getHidden() {
        return hiddenWord.toString();
    }

    public int getWordsCounter() {
        return wordsCounter;
    }

    public String getCategory() {
        return category;
    }

    public String getName() { return name; }

    public void setYourTurn(boolean yourTurn) { this.yourTurn = yourTurn; }

    public boolean isYourTurn() { return yourTurn; }

    public void setWin(int win) { this.win = win; }

    public int getWin() { return win; }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
