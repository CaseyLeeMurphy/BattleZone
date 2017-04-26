import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by caseyleemurphy on 4/12/17.
 */
public class BulletPojo {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("x")
    @Expose
    public Double x;
    @SerializedName("y")
    @Expose
    public Double y;
    @SerializedName("heading")
    @Expose
    public double heading;
    @SerializedName("speed")
    @Expose
    public double speed;

    public double xOrigin = 0;
    public double yOrigin = 0;
    public long timeCreated = System.currentTimeMillis();

    public double GetDistanceTraveled() {
        long currentTime = System.currentTimeMillis();

        return speed * (currentTime - timeCreated);
    }

    @Override
    public String toString() {
        return "Bullet " + "\n\tID: " + id + "\n\tPosition x: " + x +  "\n\tPosition y: " + y;
    }
}
