import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by caseyleemurphy on 4/12/17.
 */
public class ServerToClientMessagePojo {
    @SerializedName("messageType")
    @Expose
    public String messageType;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("error")
    @Expose
    public String error;
    @SerializedName("map")
    @Expose
    public MapPojo map;
}
