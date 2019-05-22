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
    public String hostname;
    public int port;
    public Controller controller;
    private ObjectOutputStream out;
    private InputStream is;
    private ObjectInputStream in;
    private OutputStream os;

    private String password;
    private String category;
    private StringBuilder hiddenWord;
    private int wordLength;
    private int wordsCounter;
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

            while (socket.isConnected()) {
                try {
                    Message message = null;
                    message = (Message) in.readObject();
                  //  System.out.println(message.getMsg());

                    while(socket.isConnected()) {
                        message = (Message) in.readObject();
                        if (message != null) {

                            switch (message.getType()) {
                                case PASSWORD:
                                   // System.out.println(message.getMsg());
                                    this.password = message.getMsg();
                                    wordLength=password.length();
                                    break;

                                case CATEGORY:
                                   // System.out.println(message.getMsg());
                                    this.category = message.getMsg();
                                    this.controller.createBoard();
                                    break;

                                case SIGN:
                                    hiddenWord= new StringBuilder(message.getMsg());
                                    System.out.println(hiddenWord);
                                    this.controller.changeBoard();
                                    break;

                                case NOTIFICATION:
                                    System.out.println(message.getMsg());
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println("Blad przy odbiorze wiadomosci!");
                }

            }
        } catch (IOException e) {
            System.out.println("Nie mozna polaczyc sie z serwerem!");
        }

    }

    public void sendMessage(Message msg) throws IOException{
        try{
            Message newMessage = new Message();
            newMessage.setType(msg.getType());
            newMessage.setMsg(msg.getMsg());
            out.writeObject(newMessage);
            out.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    private void hideWord(String password) {
        hiddenWord = new StringBuilder(password);
        for (int i = 0; i < wordLength; i++)
            if (password.charAt(i) == ' ')
                hiddenWord.setCharAt(i, '_');
            else
                hiddenWord.setCharAt(i, '#');

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
        hideWord(password);
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
            Message exit = new Message();
            exit.setType(MessageType.EXIT);
            exit.setMsg("exit");
            sendMessage(exit);
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

    public String getPassword() {
        return password;
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
}
