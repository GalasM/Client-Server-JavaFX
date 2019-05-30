package sample.server;

import sample.messages.Message;
import sample.messages.MessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Server implements Runnable{

    private static final int PORT = 2004;
    private ServerSocket socket;
    private static String[][] hasla = {{"Lew czarownika i stara szafa","Literatura"},{"Ala ma", "Powiedzenia"}, {"Krol Karol kupil krolowej Karolinie korale koloru koralowego", "aaa"}, {"Stol z powylamywanymi nogami", "bbb"}};
    private static final int haslaCount=4;
    private static String[] kwoty ={"500","1000","1500","2000","BANKRUT"};
    private static final int kwotyCount=5;
    private static final int generatedNumber = randomValue();
    private static String password;
    private static StringBuilder passwordCompleted = new StringBuilder();
    private static String category;
    private static int length;
    private static int playersCount;
    private static int startedPlayers=0;
    private boolean started = false;
    private boolean activeServer = true;
    private ArrayList<Socket> clients;
    private ArrayList<Thread> clientThreads;
    private static ArrayList<Handler> players;

    //CONSTRUCTOR
    public Server() throws IOException {
        players = new ArrayList<>();
        clients = new ArrayList<>();
        socket = new ServerSocket(PORT);
    }

    //RUN
    @Override
    public void run()
    {
        clientThreads = new ArrayList<>();
        while(activeServer){
            try {
                if (!socket.isClosed()) {
                    final Socket clientSocket = socket.accept();
                    clients.add(clientSocket);
                    Handler clientThred = new Handler(clientSocket, this);
                    players.add(clientThred);
                    Thread x = new Thread(clientThred);
                    clientThreads.add(x);
                    playersCount++;
                    x.start();

                    System.out.println("ilosc graczy:" + playersCount);

                }
                } catch(IOException ex){
                   System.out.println("Zamknieto socket i wylaczono serwer!");
                }

        }
    }


    public void writeToAllSockets(Message input) {
        for (Handler clientThread : players) {
            clientThread.sendMessage(input);
        }
    }

    public void showAllSockets() {
        for (Handler clientThread : players) {
          System.out.println(clientThread+" ilosc graczy:"+playersCount);
        }
    }

    public void disconnectClient(Handler client)
    {
        System.out.println(client.getThread());
        clients.remove(client.getSocket());
        clientThreads.remove(client.getThread());
        players.remove(client);
        playersCount--;
        showAllSockets();
    }

    private static int randomValue() {
        Random generator = new Random();
        return generator.nextInt(haslaCount);   //ilosc hasel i kategorii
    }

    private static String randomCash(){
        Random rand = new Random();
       return  kwoty[rand.nextInt(kwotyCount)];
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

   static int checkSign(String sign) {
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
        return countSigns;
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

    public void setActiveServer(boolean activeServer) {
        this.activeServer = activeServer;
    }

    public void setTurns(){
        boolean randTurn=true;
        Message msg = new Message();
        msg.setType(MessageType.TURN);
        for (Handler clientThread : players) {

            if(randTurn) {
                clientThread.setYourTurn(randTurn);
                msg.setTurn(randTurn);
                clientThread.sendMessage(msg);
                randTurn = false;
            }
            else {
                clientThread.setYourTurn(randTurn);
                msg.setTurn(randTurn);
                clientThread.sendMessage(msg);
            }
        }
    }

    ///////MAIN///////////
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.run();

    }
    /////////////////////
    private class Handler implements Runnable {

        private Socket socket;
        private Server baseServer;
        private ObjectInputStream in;
        private OutputStream os;
        private ObjectOutputStream out;
        private InputStream is;
        private boolean active=true;
        private boolean yourTurn=false;

        Handler(Socket socket, Server baseServer)
        {
            this.baseServer=baseServer;
            this.socket=socket;
            try {
                is = socket.getInputStream();
                in = new ObjectInputStream(is);
                os = socket.getOutputStream();
                out = new ObjectOutputStream(os);
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {

                System.out.println("Uruchomiono Handler");

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
                    System.out.println(inputmsg.getName()+">"+inputmsg.getMsg());

                    if (inputmsg != null) {
                        switch (inputmsg.getType()) {
                            case START:
                                if(started){
                                    Message info = new Message();
                                    info.setType(MessageType.INFO);
                                    info.setMsg("Gra sie rozpoczela!");
                                    sendMessage(info);
                                }
                                else {
                                    startedPlayers++;
                                    Message StartWord = new Message();
                                    Message StartCategory = new Message();
                                    Message Start = new Message();
                                    Start.setType(MessageType.START);
                                    Start.setMsg("Start");
                                    Start.setName("Serwer");
                                    StartWord.setType(MessageType.PASSWORD);
                                    StartWord.setName("Serwer");
                                    password = randomWord();
                                    hideWord();
                                    String x = new String(passwordCompleted);
                                    StartWord.setMsg(x);
                                    category = randomCategory();
                                    StartCategory.setMsg(category);
                                    StartCategory.setType(MessageType.CATEGORY);
                                    StartCategory.setName("Serwer");
                                    if (startedPlayers == playersCount) {
                                        baseServer.setTurns();
                                        baseServer.writeToAllSockets(Start);
                                        baseServer.writeToAllSockets(StartWord);
                                        baseServer.writeToAllSockets(StartCategory);
                                        started = true;
                                    } else {
                                        Message info = new Message();
                                        info.setType(MessageType.INFO);
                                        info.setMsg("Oczekiwanie na innych graczy!");
                                        sendMessage(info);
                                    }
                                }
                                    break;

                            case SIGN:
                                    Message Pass = new Message();
                                    Message counter = new Message();
                                    int count = checkSign(inputmsg.getMsg());
                                    counter.setCountSign(count);
                                    counter.setType(MessageType.SIGN);
                                    counter.setName("Serwer");
                                    String msg = new String(passwordCompleted);
                                    Pass.setMsg(msg);
                                    Pass.setType(MessageType.INCOMPLETEPASSWORD);
                                    Pass.setName("Serwer");
                                    baseServer.writeToAllSockets(Pass);
                                    sendMessage(counter);
                                    break;

                            case RANDOM:
                                    Message rand = new Message();
                                    rand.setType(MessageType.RANDOM);
                                    rand.setMsg(randomCash());
                                    rand.setTurn(true);
                                    rand.setName("Serwer");
                                    sendMessage(rand);
                                    break;

                            case EXIT:
                                baseServer.disconnectClient(this);
                                startedPlayers--;
                                if(playersCount==0) {
                                    active = false;
                                    baseServer.setActiveServer(false);
                                    baseServer.socket.close();
                                }
                                closeConnection();
                                    break;

                            case NOTIFICATION:
                                System.out.println(inputmsg.getMsg());
                                break;
                        }
                    }


            }}catch (ClassNotFoundException e) {
               // closeConnection();
                System.out.println("Blad przy odczytaniu wiadomosci przez serwer. "+Thread.currentThread());

            }  catch (IOException e) {
              //  closeConnection();
                System.out.println("Blad przy odczytaniu wiadomosci przez serwer."+Thread.currentThread());
            } finally {

              //  closeConnection();
            }

        }

        private Message sendNotification(Message firstMessage) throws IOException {
            Message msg = new Message();
            msg.setMsg("Klient polaczony z serwerem.");
            msg.setType(MessageType.NOTIFICATION);
            msg.setName("Serwer");
            sendMessage(msg);
            return msg;
        }

        public void sendMessage(Message msg) {
            try{
                out.writeObject(msg);
                out.flush();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
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

        public Socket getSocket(){
            return this.socket;
        }

        public Thread getThread(){
            return Thread.currentThread();
        }

        public void setYourTurn(boolean yourTurn) { this.yourTurn = yourTurn; }

        public boolean getYourTurn(){ return yourTurn;}
    }
}


