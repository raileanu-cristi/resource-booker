import java.net.SocketException;

public class ResourceBooker {
    final static int BUFFER_LEN = 8192;
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Port argument is required!");
            return;
        }
 
        int port = Integer.parseInt(args[0]);

        try {
            final Server server = new Server(port);
            server.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
