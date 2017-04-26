import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by caseyleemurphy on 4/12/17.
 */
public class MapPojo {
    @SerializedName("obstacles")
    @Expose
    public List<ObstaclePojo> obstacles = null;
    @SerializedName("dimensions")
    @Expose
    public DimensionsPojo dimensions;
    @SerializedName("players")
    @Expose
    public List<PlayerPojo> players = null;
    @SerializedName("bullets")
    @Expose
    public List<BulletPojo> bullets = null;
}


