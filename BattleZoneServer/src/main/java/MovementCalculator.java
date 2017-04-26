import com.google.gson.Gson;

import java.util.List;
import java.util.TimerTask;

/**
 * Created by caseyleemurphy on 4/12/17.
 */
public class MovementCalculator extends TimerTask {
    public void run() {
        Gson gson = new Gson();
        RecalculatePlayers();
        RecalculateBullets();

        ServerToClientMessagePojo updateMessage = new ServerToClientMessagePojo();
        MapPojo updateMap = new MapPojo();
        updateMessage.messageType = "update";
        if (AsynchServer.players.size() > 0) updateMap.players = AsynchServer.players;
        if (AsynchServer.bullets.size() > 0) updateMap.bullets = AsynchServer.bullets;

        updateMessage.map = updateMap;
        ServersClientHandler.sendMessageToEveryone(gson.toJson(updateMessage));
    }

    private void RecalculatePlayers() {
        for (int i = 0; i < AsynchServer.players.size(); i++) {
            PlayerPojo newPlayer = AsynchServer.players.get(i);
            newPlayer.x += newPlayer.movingDirection * (newPlayer.driveSpeed / AsynchServer.updateInterval * Math.cos(Math.toRadians(newPlayer.heading)));
            newPlayer.y += newPlayer.movingDirection * (newPlayer.driveSpeed / AsynchServer.updateInterval * Math.sin(Math.toRadians(newPlayer.heading)));

            if (PlayerHitObstacle(AsynchServer.obstacles, newPlayer)) {
                newPlayer.x -= newPlayer.movingDirection * (newPlayer.driveSpeed / AsynchServer.updateInterval * Math.cos(Math.toRadians(newPlayer.heading)));
                newPlayer.y -= newPlayer.movingDirection * (newPlayer.driveSpeed / AsynchServer.updateInterval * Math.sin(Math.toRadians(newPlayer.heading)));
            }

            newPlayer.heading += -newPlayer.rotateDirection * newPlayer.rotateSpeed / AsynchServer.updateInterval;

            // Check if new value is out of the dimension. If so, reset value to be at the limit.
            if (newPlayer.x < -AsynchServer.dimensions.width) newPlayer.x = -AsynchServer.dimensions.width;
            if (newPlayer.x > AsynchServer.dimensions.width) newPlayer.x = AsynchServer.dimensions.width;

            if (newPlayer.y < -AsynchServer.dimensions.height) newPlayer.y = -AsynchServer.dimensions.height;
            if (newPlayer.y > AsynchServer.dimensions.height) newPlayer.y = AsynchServer.dimensions.height;

            AsynchServer.players.set(i, newPlayer);
        }
    }

    private void RecalculateBullets() {
        for (int i = 0; i < AsynchServer.bullets.size(); i++) {
            BulletPojo newBullet = AsynchServer.bullets.get(i);
            newBullet.x += (newBullet.speed / AsynchServer.updateInterval * Math.cos(Math.toRadians(newBullet.heading)));
            newBullet.y += (newBullet.speed / AsynchServer.updateInterval * Math.sin(Math.toRadians(newBullet.heading)));

            // Check if new value is out of the dimension. If so, reset value to be at the limit.
            if (newBullet.x > -AsynchServer.dimensions.width
                && newBullet.x < AsynchServer.dimensions.width
                && newBullet.y > -AsynchServer.dimensions.height
                && newBullet.y < AsynchServer.dimensions.height
                && !BulletExceededMaximumDistance(newBullet)
                && !BulletHitObstacle(AsynchServer.obstacles, newBullet)
                && !BulletHitPlayer(newBullet)) {
                AsynchServer.bullets.set(i, newBullet);
            } else {
                AsynchServer.bullets.remove(i);
            }
        }
    }

    private boolean BulletExceededMaximumDistance(BulletPojo theBullet) {
        return Math.sqrt(Math.abs(theBullet.x - theBullet.xOrigin) * Math.abs(theBullet.x - theBullet.xOrigin)
                         +  Math.abs(theBullet.y - theBullet.yOrigin) * Math.abs(theBullet.y - theBullet.yOrigin)
            ) > AsynchServer.maxBulletDistance;
    }

    private boolean PlayerHitObstacle(List<ObstaclePojo> obstructions, PlayerPojo thePlayer) {
        for (int i = 0; i < obstructions.size(); i++){
            double distanceBetweenPoints = Math.sqrt(Math.pow((thePlayer.x - obstructions.get(i).x), 2) + Math.pow((thePlayer.y - obstructions.get(i).y),2));
            if (distanceBetweenPoints < obstructions.get(i).radius) {
                return true;
            }
        }

        return false;
    }

    private boolean BulletHitObstacle(List<ObstaclePojo> obstructions, BulletPojo theBullet) {
        for (int i = 0; i < obstructions.size(); i++){
            double distanceBetweenPoints = Math.sqrt(Math.pow((theBullet.x - obstructions.get(i).x), 2) + Math.pow((theBullet.y - obstructions.get(i).y),2));
            if (distanceBetweenPoints < obstructions.get(i).radius) {
                return true;
            }
        }

        return false;
    }

    private boolean BulletHitPlayer(BulletPojo theBullet) {
        for (int i = 0; i < AsynchServer.players.size(); i++) {
            if (theBullet.id.equalsIgnoreCase(AsynchServer.players.get(i).id)){
                continue;
            }

            double distanceBetweenPoints = Math.sqrt(Math.pow((theBullet.x - AsynchServer.players.get(i).x), 2) + Math.pow((theBullet.y - AsynchServer.players.get(i).y),2));
            if (distanceBetweenPoints < AsynchServer.dimensions.tankRadius && AsynchServer.players.get(i).alive != false) {
                AsynchServer.players.get(i).alive = false;
                return true;
            }
        }

        return false;
    }
}
