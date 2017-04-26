import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by caseyleemurphy on 4/12/17.
 */
public class ObstaclePojo {
    @SerializedName("x")
    @Expose
    public Double x;
    @SerializedName("y")
    @Expose
    public Double y;
    @SerializedName("radius")
    @Expose
    public Double radius;

    @Override
    public String toString() {
        return "Obstacle " + "\n\tRadius: " + radius + "\n\tPosition x: " + x +  "\n\tPosition y: " + y;
    }
}

