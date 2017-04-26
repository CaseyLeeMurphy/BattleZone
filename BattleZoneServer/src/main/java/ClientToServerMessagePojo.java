import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by caseyleemurphy on 4/12/17.
 */
public class ClientToServerMessagePojo {
    @SerializedName("action")
    @Expose
    public String action;

    @SerializedName("username")
    @Expose
    public String username;

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("drive")
    @Expose
    public Integer drive;

    @SerializedName("rotate")
    @Expose
    public Integer rotate;

}
