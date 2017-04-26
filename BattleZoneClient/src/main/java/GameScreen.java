import com.google.gson.Gson;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by caseyleemurphy on 4/12/17.
 */
public class GameScreen {

    private JTextArea txtPlayers;
    private JTextArea txtBullets;
    private JButton btnFire;
    private JButton btnReverse;
    private JButton btnLeft;
    private JButton btnForward;
    private JButton btnRight;
    private JTextArea txtObstacles;
    public JPanel panelMain;
    private JTextField txtCurrentLocation;
    private Gson gson = new Gson();
    private int rotateDirection = 0;
    private int driveDirection = 0;

    public GameScreen() {
        btnFire.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientToServerMessagePojo fireMessage = new ClientToServerMessagePojo();
                fireMessage.action = "fire";
                fireMessage.id = ClientMain.playerID;

                ClientCommunicationHelper.sendVariableLengthMessage(gson.toJson(fireMessage));
            }
        });
        btnLeft.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rotateDirection >= 0 ) rotateDirection--;

                sendMoveMessage();
            }
        });
        btnRight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rotateDirection <= 0 ) rotateDirection++;

                sendMoveMessage();
            }
        });
        btnForward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (driveDirection <= 0 ) driveDirection++;

                sendMoveMessage();
            }
        });
        btnReverse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (driveDirection >= 0 ) driveDirection--;

                sendMoveMessage();
            }
        });
    }

    private void sendMoveMessage() {
        ClientToServerMessagePojo moveMessage = new ClientToServerMessagePojo();
        moveMessage.action = "move";
        moveMessage.drive = driveDirection;
        moveMessage.rotate = rotateDirection;
        moveMessage.id = ClientMain.playerID;

        ClientCommunicationHelper.sendVariableLengthMessage(gson.toJson(moveMessage));
    }

    public synchronized void UpdateAllScreens(String currentLocation)
    {
        StringBuilder playersString = new StringBuilder();
        StringBuilder bulletsString = new StringBuilder();
        StringBuilder obstaclesString = new StringBuilder();

        for (int i = 0; i < ClientMain.players.size(); i++){
            if (ClientMain.players.get(i).alive == false) continue;

            playersString.append(ClientMain.players.get(i).toString());
            playersString.append('\n');
        }

        for (int i = 0; i < ClientMain.bullets.size(); i++){
            bulletsString.append(ClientMain.bullets.get(i).toString());
            bulletsString.append('\n');
        }

        for (int i = 0; i < ClientMain.obstacles.size(); i++){
            obstaclesString.append(ClientMain.obstacles.get(i).toString());
            obstaclesString.append('\n');
        }

        txtPlayers.setText(playersString.toString());
        txtBullets.setText(bulletsString.toString());
        txtObstacles.setText(obstaclesString.toString());
        txtCurrentLocation.setText(currentLocation);
    }

    public synchronized void ShowKilledScreen(String id) {
        int dialogResult = JOptionPane.showConfirmDialog (null, "You have been killed. Bummer. Would you like to continue?");
        if(dialogResult == JOptionPane.YES_OPTION){
            ClientCommunicationHelper.ReviveMe(id);
        } else {
            ClientCommunicationHelper.connectedToServer = false;
        }
    }
}
