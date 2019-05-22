package sample.server;

import sample.client.Listener;
import sample.messages.Message;
import sample.messages.MessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Server {

    private static final int PORT = 2004;
    private static String[][] hasla = {{"Ala ma", "Powiedzenia"}, {"Krol Karol kupil krolowej Karolinie korale koloru koralowego", "aaa"}, {"Stol z powylamywanymi nogami", "bbb"}};
    private static final int generatedNumber = randomValue();
    private static String password;
    private static StringBuilder passwordCompleted = new StringBuilder();
    private static String category;
    private static int length;
    private static ArrayList<Handler> players ;

    private static int randomValue() {
        Random generator = new Random();
        return generator.nextInt(3);   //ilosc hasel i kategorii
    }

   static String randomWord()
    {
        return hasla[generatedNumber][0].toUpperCase();
    }

   static String  randomCategory()
    {
        return hasla[generatedNumber][1].toUpperCase();
    }

    static void setHiddenWord(int i, char ClickedSign) {
        passwordCompleted.setCharAt(i, ClickedSign);
    }

   static void checkSign(String sign) {
        int countSigns=0;
        for (int i = 0; i < length; i++) {
            char ClickedSign;
            ClickedSign = sign.charAt(0);
            char CurrSign = password.charAt(i);
            if (CurrSign == ClickedSign) {
                countSigns++;
                setHiddenWord(i, ClickedSign);
            }
        }
    }

    private static void hideWord() {
        length=password.length();
        passwordCompleted = new StringBuilder(password);
        for (int i = 0; i < length; i++)
            if (password.charAt(i) == ' ')
                passwordCompleted.setCharAt(i, '_');
            else
                passwordCompleted.setCharAt(i, '#');
    }

    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(PORT);

        try {
            players = new ArrayList<>();
            int i =0;
            while (!listener.isClosed()) {
              // Handler connection = new Handler(listener.accept()).start();
                    System.out.println("Uruchomiono serwer");
                    Handler connection = new Handler(listener.accept());
                    players.add(connection);
                    players.get(i).start();
                    i++;
                    if (!connection.getActive()) {
                        listener.close();
                    }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            listener.close();
        }

    }

    private static class Handler extends Thread {

        private Socket socket;
        private ObjectInputStream in;
        private OutputStream os;
        private ObjectOutputStream out;
        private InputStream is;
        private boolean active=true;

        Handler(Socket socket)
        {
            this.socket=socket;
        }

        @Override
        public void run() {
            try {
                System.out.println("Uruchomiono Handler");
                is = socket.getInputStream();
                in = new ObjectInputStream(is);
                os = socket.getOutputStream();
                out = new ObjectOutputStream(os);

            Message firstMessage = (Message) in.readObject();
            firstMessage.setMsg("Pierwsza wiadomosc");
            sendNotification(firstMessage);
            } catch (IOException e) {
                e.printStackTrace();
            } catch(ClassNotFoundException e)
            {
                System.out.println("Nie odebrano wiadomosci!");
            }
            try {
            while(active) {

                    Message inputmsg = (Message) in.readObject();
                    if (inputmsg != null) {
                        switch (inputmsg.getType()) {
                            case START:
                                    Message StartWord = new Message();
                                    Message StartCategory = new Message();

                                    StartWord.setType(MessageType.PASSWORD);
                                    password = randomWord();
                                    hideWord();
                                    StartWord.setMsg(password);
                                    System.out.println(StartWord.getMsg());
                                    category = randomCategory();
                                    StartCategory.setMsg(category);
                                    StartCategory.setType(MessageType.CATEGORY);
                                    sendMessage(StartWord);
                                    sendMessage(StartCategory);
                                    break;

                            case SIGN:
                                    System.out.println(inputmsg.getMsg());
                                    checkSign(inputmsg.getMsg());
                                    String msg = new String(passwordCompleted);
                                    System.out.println(passwordCompleted);
                                    Message Pass = new Message();
                                    Pass.setMsg(msg);
                                    Pass.setType(MessageType.SIGN);
                                    sendMessage(Pass);
                                    break;

                            case EXIT:
                                    active=false;
                                    closeConnection();
                                    break;
                        }
                    }


            }}catch (ClassNotFoundException e) {
                closeConnection();
                System.out.println("Blad przy odczytaniu wiadomosci przez serwer. "+Thread.currentThread());

            } catch (IOException e) {
                closeConnection();
                System.out.println("Blad przy odczytaniu wiadomosci przez serwer."+Thread.currentThread());
            }
            finally {

                closeConnection();
            }
        }

        private Message sendNotification(Message firstMessage) throws IOException {
            Message msg = new Message();
            msg.setMsg("Klient polaczony z serwerem.");
            msg.setType(MessageType.NOTIFICATION);
            sendMessage(msg);
            return msg;
        }

        void sendMessage(Message msg) {
            try{
                out.writeObject(msg);
                out.flush();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }

        public boolean getActive()
        {
            return this.active;
        }

        void closeConnection() {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e)
            {
                System.out.println("Nie udalo sie zamknac polaczenia!");
            }
        }
    }
}


