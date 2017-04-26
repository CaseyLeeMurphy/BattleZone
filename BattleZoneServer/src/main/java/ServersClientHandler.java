import com.google.gson.Gson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by caseyleemurphy on 2/15/17.
 */
public class ServersClientHandler implements Runnable{
    private final AsynchronousSocketChannel socketChannel;
    private Gson gson = new Gson();
    private PlayerPojo thisPlayer ;

    public ServersClientHandler(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void run() {
        System.out.println("Client Handler started for " + this.socketChannel);
        try {
            receiveVariableLengthMessage();
        } catch (InterruptedException | ExecutionException | IOException e) {
            AsynchServer.clients.remove(thisPlayer.id);
            try {
                socketChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Client " + thisPlayer.id + " disconnected");
        } catch(Exception e) {
            System.out.println("There was a general problem");
            AsynchServer.clients.remove(thisPlayer.id);
            e.printStackTrace();
        }
    }

    public void receiveVariableLengthMessage() throws InterruptedException, ExecutionException, IOException {
        if ((socketChannel != null) && (socketChannel.isOpen())) {
            ByteBuffer buffer = ByteBuffer.allocate(1000);
            String message = "";
            while (socketChannel.read(buffer).get() > 0 ){
                char byteRead = '\n';
                buffer.flip();
                while (buffer.hasRemaining()){
                    byteRead = (char) buffer.get();
                    if (byteRead == '\n'){
                        handleMessage(gson.fromJson(message, ClientToServerMessagePojo.class));
                        message = "";
                    } else {
                        message += byteRead;
                    }
                }
            }

            receiveVariableLengthMessage();
        }
    }

    public synchronized void  sendMessageToOriginalSender(String message) {
        ByteBuffer buffer = ByteBuffer.allocate(message.length() + 1);
        buffer.put(message.getBytes());
        buffer.put((byte) ('\n'));
        buffer.flip();
        while(buffer.hasRemaining()) {
            Future response = socketChannel.write(buffer);
            while(!response.isDone()){
                //wait
            }
        }

        System.out.println("printed the following message :" + message);
    }

    public static synchronized void sendMessageToEveryone(String message) {
        for (String key : AsynchServer.clients.keySet()) {
            ByteBuffer buffer = ByteBuffer.allocate(message.length() + 1);
            buffer.put(message.getBytes());
            buffer.put((byte) ('\n'));
            buffer.flip();
            while(buffer.hasRemaining()) {
                Future response = (AsynchServer.clients.get(key)).write(buffer);
                while(!response.isDone()){
                    //wait
                }
            }

            System.out.println("printed the following message to " + key +  " :" + message.toString());
        }
    }

    private void handleMessage(ClientToServerMessagePojo message) throws IOException {
        ServerToClientMessagePojo response = new ServerToClientMessagePojo();

        switch(message.action) {
            case "register":
                // Create new ID and send to user
                String newID = String.valueOf(System.currentTimeMillis());
                response.id = newID;
                response.messageType = "response";
                sendMessageToOriginalSender(gson.toJson(response));

                // Create new user object and add to global list
                thisPlayer = new PlayerPojo(newID, message.username);
                AsynchServer.players.add(thisPlayer);

                // Send initializing update to player
                ServerToClientMessagePojo firstUpdate = new ServerToClientMessagePojo();
                firstUpdate.messageType = "update";
                MapPojo firstUpdateMap = new MapPojo();
                firstUpdateMap.obstacles = AsynchServer.obstacles;
                firstUpdateMap.bullets = AsynchServer.bullets;
                firstUpdateMap.dimensions = AsynchServer.dimensions;
                firstUpdateMap.players = AsynchServer.players;
                firstUpdate.map = firstUpdateMap;
                sendMessageToOriginalSender(gson.toJson(firstUpdate));
                AsynchServer.clients.put(newID, socketChannel);

                // Send new player update to everyone
                ServerToClientMessagePojo newPlayerMessage = new ServerToClientMessagePojo();
                MapPojo updatedMap = new MapPojo();
                updatedMap.players = AsynchServer.players;
                newPlayerMessage.map = updatedMap;
                newPlayerMessage.messageType = "update";

                sendMessageToEveryone(gson.toJson(newPlayerMessage));
                break;
            case "fire":
                BulletPojo newBullet = new BulletPojo();
                newBullet.id = thisPlayer.id;
                newBullet.heading = thisPlayer.heading;
                newBullet.speed = AsynchServer.bulletSpeed;
                newBullet.x = thisPlayer.x;
                newBullet.y = thisPlayer.y;
                newBullet.xOrigin = thisPlayer.x;
                newBullet.yOrigin = thisPlayer.y;
                AsynchServer.bullets.add(newBullet);
                break;
            case "move":
                thisPlayer.movingDirection = message.drive;
                thisPlayer.rotateDirection = message.rotate;
                AsynchServer.updatePlayer(thisPlayer);
                break;
            case "continue":
                PlayerPojo respawnedPlayer = new PlayerPojo(thisPlayer.id, thisPlayer.username);
                thisPlayer = respawnedPlayer;
                AsynchServer.updatePlayer(respawnedPlayer);
            case "quit":
                socketChannel.close();
                AsynchServer.clients.remove(thisPlayer.id);
                AsynchServer.removePlayer(thisPlayer.id);
                break;
        }
    }

}
