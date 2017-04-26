/**
 * Created by caseyleemurphy on 4/11/17.
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsynchServer {
    public static Map<String, AsynchronousSocketChannel> clients = new HashMap();
    public static List<BulletPojo> bullets = new Vector<>();
    public static List<ObstaclePojo> obstacles = new Vector<>();
    public static List<PlayerPojo> players = new Vector<>();
    public static DimensionsPojo dimensions = new DimensionsPojo();
    public static double bulletSpeed = 20;
    public static final int updateInterval = 10; // updates per second
    public static final double maxBulletDistance = 100;
    private Integer portNum = 5000;

    public AsynchServer(Integer port) {
        portNum = port;
        System.out.println("Asynch Chat Server Started on port " + portNum);

        dimensions.height = 200.00;
        dimensions.width = 200.00;
        dimensions.tankRadius = 2.00;
        ObstaclePojo obstacle1 = new ObstaclePojo();
        obstacle1.radius = 3.00;
        obstacle1.x = 1.00;
        obstacle1.y = 56.00;
        obstacles.add(obstacle1);

        try(AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open()) {
            InetSocketAddress hostAddress = new InetSocketAddress(InetAddress.getLocalHost(), portNum);
            serverChannel.bind(hostAddress);

            System.out.println("Waiting for clients to connect...");
            Timer timer = new Timer();
            timer.schedule(new MovementCalculator(), 0, 100);

            while(true) {
                Future acceptResult = serverChannel.accept();
                AsynchronousSocketChannel client = (AsynchronousSocketChannel) acceptResult.get();
                new Thread(new ServersClientHandler(client)).start();
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayer(PlayerPojo newPlayer) {
        for (int i = 0; i < players.size(); i++){
            if (players.get(i).id.equalsIgnoreCase(newPlayer.id)){
                players.set(i, newPlayer);
                break;
            }
        }
    }

    public static void removePlayer(String playerID) {
        for (int i = 0; i < players.size(); i++){
            if (players.get(i).id.equalsIgnoreCase(playerID)){
                players.remove(i);
                System.out.println("Removed Player " + playerID);
                break;
            }
        }
    }
}
