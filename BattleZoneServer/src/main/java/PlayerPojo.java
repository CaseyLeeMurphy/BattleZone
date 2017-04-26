import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by caseyleemurphy on 4/12/17.
 */
public class PlayerPojo {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("username")
    @Expose
    public String username;

    @SerializedName("x")
    @Expose
    public Double x;
    @SerializedName("y")
    @Expose
    public Double y;
    @SerializedName("heading")
    @Expose
    public double heading;
    @SerializedName("driveSpeed")
    @Expose
    public double driveSpeed;
    @SerializedName("rotateSpeed")
    @Expose
    public double rotateSpeed;

    public int movingDirection;

    public int rotateDirection;

    public boolean alive;

    public PlayerPojo(String id, String username) {
        this.id = id;
        this.username = username;
        x = (double)ThreadLocalRandom.current().nextInt(-AsynchServer.dimensions.height.intValue(), AsynchServer.dimensions.height.intValue() + 1);
        y = (double)ThreadLocalRandom.current().nextInt(-AsynchServer.dimensions.width.intValue(), AsynchServer.dimensions.width.intValue() + 1);
        heading = 0;
        driveSpeed = 5;
        rotateSpeed = 30;
        movingDirection = 0;
        alive = true;
    }

    @Override
    public String toString() {
        return "Player " + id + "\n\tPosition x: " + x +  "\n\tPosition y: " + y;
    }
}
