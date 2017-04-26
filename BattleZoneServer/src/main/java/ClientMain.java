import com.google.gson.Gson;

import javax.swing.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

/**
 * Created by caseyleemurphy on 4/12/17.
 */
public class ClientMain {
    public static String username;
    public static String playerID;
    public static GameScreen gameScreen;
    private static Gson gson = new Gson();
    public static List<BulletPojo> bullets = new Vector<>();
    public static List<ObstaclePojo> obstacles = new Vector<>();
    public static List<PlayerPojo> players = new Vector<>();

    public static void main(String[] args) throws UnknownHostException {
        int port = 5000;
        String ipAddress = "127.0.0.1";
        String username = "default";

        if (args.length == 1){
            port =  Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            port = Integer.parseInt(args[0]);
            ipAddress = args[1].toString();
        } else if (args.length == 3) {
            port = Integer.parseInt(args[0]);
            ipAddress = args[1].toString();
            username = args[2].toString();
        }

        // Setup the Chat screen and start connection to chat server
        JFrame frame = new JFrame("test");
        gameScreen = new GameScreen();
        frame.setContentPane(gameScreen.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        new Thread(new ClientCommunicationHelper(ipAddress,port, username)).start();

        while (ClientCommunicationHelper.connectedToServer == false) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            if (ClientCommunicationHelper.connectedToServer == true) {
                ClientToServerMessagePojo quitMessage = new ClientToServerMessagePojo();
                quitMessage.action = "quit";
                ClientCommunicationHelper.sendVariableLengthMessage(gson.toJson(quitMessage));
            }
            System.exit(0);
            }
        });
    }

    public static String getCurrentLocation(String playerID) {
        for (int i = 0; i < players.size(); i++){
            if (players.get(i).id.equalsIgnoreCase(playerID)){
                return "x: " + players.get(i).x + " y: " + players.get(i).y + "      Heading " + players.get(i).heading;
            }
        }

        return "";
    }

    public static synchronized boolean PlayerKilled(String id) {
        for (int i = 0; i < players.size(); i++){
            if (players.get(i).id.equalsIgnoreCase(id) && players.get(i).alive == false) {
                return true;
            }
        }
        return false;
    }
}
