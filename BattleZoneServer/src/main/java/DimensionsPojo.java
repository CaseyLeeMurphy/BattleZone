import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by caseyleemurphy on 4/12/17.
 */
public class DimensionsPojo {
    @SerializedName("height")
    @Expose
    public Double height;
    @SerializedName("width")
    @Expose
    public Double width;

    @SerializedName("tankRadius")
    @Expose
    public Double tankRadius;
}
