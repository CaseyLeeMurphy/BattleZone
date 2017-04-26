/**
 * Created by caseyleemurphy on 2/16/17.
 */
public class ServerMain {
    public static void main(String[] args) {
        if (args.length == 1) {
            new AsynchServer(Integer.parseInt(args[0]));
        }
        else {
            new AsynchServer(5000);
        }
    }
}
