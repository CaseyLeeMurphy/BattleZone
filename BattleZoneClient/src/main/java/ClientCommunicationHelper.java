import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by caseyleemurphy on 4/12/17.
 */
public class ClientCommunicationHelper implements Runnable{
    public static AsynchronousSocketChannel clientChannel;
    public static boolean connectedToServer = false;
    private static Gson gson = new Gson();
    private String ipAddress;
    private Integer port;
    private String playerID = "";
    private String username = "";

    public ClientCommunicationHelper(String ipAddress, Integer port, String username){
        this.ipAddress = ipAddress;
        this.port = port;
        this.username = username;
    }

    public static synchronized void ReviveMe(String id) {
        ClientToServerMessagePojo registerMessage = new ClientToServerMessagePojo();
        registerMessage.action = "continue";
        registerMessage.id = id;

        sendVariableLengthMessage(gson.toJson(registerMessage));
    }

    public synchronized void receiveVariableLengthMessage() {
        if (connectedToServer) {
            ByteBuffer buffer = ByteBuffer.allocate(1000);
            String message = "";
            try {
                while (clientChannel.read(buffer).get() > 0 ){
                    char byteRead = '\n';
                    buffer.flip();
                    while (buffer.hasRemaining()){
                        byteRead = (char) buffer.get();
                        if (byteRead == '\n'){
                            handleMessage(message);
                            message = "";
                        } else {
                            message += byteRead;
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            receiveVariableLengthMessage();
        }
    }

    public static synchronized void sendVariableLengthMessage(String message){
        ByteBuffer buffer = ByteBuffer.allocate(message.length() + 1);
        buffer.put(message.getBytes());
        buffer.put((byte) ('\n'));
        buffer.flip();

        while(buffer.hasRemaining()){
            Future response = clientChannel.write(buffer);
            while(!response.isDone()){
                //wait
            }
        }
        System.out.println("Sent: " + message);
    }

    public void run() {
        try {
            clientChannel = AsynchronousSocketChannel.open();
            InetSocketAddress hostAddress = new InetSocketAddress(InetAddress.getByName(ipAddress), port);
            Future future = clientChannel.connect(hostAddress);
            future.get();
            connectedToServer = true;

            connectedToServer = true;

            ClientToServerMessagePojo registerMessage = new ClientToServerMessagePojo();
            registerMessage.action = "register";
            registerMessage.username = username;
            sendVariableLengthMessage(gson.toJson(registerMessage));

            receiveVariableLengthMessage();
        } catch (IOException | InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
    }

    private void handleMessage(String message) {
        ServerToClientMessagePojo updateMessage;
        try {
            updateMessage = gson.fromJson(message, ServerToClientMessagePojo.class);
            switch(updateMessage.messageType) {
                case "response":
                    playerID = updateMessage.id;
                    ClientMain.playerID = updateMessage.id;
                    break;
                case "update":
                    if (updateMessage.map.players != null) ClientMain.players = updateMessage.map.players;
                    else ClientMain.players = new Vector<>();

                    if (updateMessage.map.bullets != null) ClientMain.bullets = updateMessage.map.bullets;
                    else ClientMain.bullets = new Vector<>();

                    if (updateMessage.map.obstacles != null) ClientMain.obstacles = updateMessage.map.obstacles;

                    if (ClientMain.PlayerKilled(playerID)) {
                        ClientMain.gameScreen.ShowKilledScreen(playerID);
                    } else {
                        ClientMain.gameScreen.UpdateAllScreens(ClientMain.getCurrentLocation(playerID));
                    }

                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
