import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class DummyClient {
    private static final int BUFFER_SIZE = 8192;

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Syntax: DummyClient <host> <port> <targetHost> <id>");
            return;
        }
 
        final String hostname = args[0];
        final int serverPort = Integer.parseInt(args[1]);
        final int id = Integer.parseInt(args[2]);
        final List<String> resources = new ArrayList<>(List.of(args)).subList(3, args.length);

        try {
            final InetAddress address = InetAddress.getByName(hostname);
            final DatagramSocket socket = new DatagramSocket(serverPort);
            if (id == 1) {
                firstPeer(socket, address, serverPort);
            } else if (id == 2) {
                secondPeer(socket, address, serverPort);
            } else if (id == 3) {
                registerPeer(socket, address, serverPort, resources);
            }
        } catch (Exception ex) {
            System.out.println("Timeout error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void firstPeer(final DatagramSocket socket, final InetAddress serverAddress, final int serverPort)
            throws IOException, SecurityException  {
        final String bookedResource = bookAnyResource(socket, serverAddress, serverPort);
        System.out.println(bookedResource);
    }

    private static void secondPeer(final DatagramSocket socket, final InetAddress serverAddress, final int serverPort) throws IOException {
        final String bookedResource = bookAnyResource(socket, serverAddress, serverPort);
        System.out.println("Booked resource " + bookedResource);
        if (bookedResource != null) {
            freeResource(socket, serverAddress, serverPort, bookedResource);
        }
    }

    private static void registerPeer(final DatagramSocket socket, final InetAddress address, final int port, final List<String> resources) throws IOException {
        registerResources(socket, address, port, resources);
    }

    private static String bookAnyResource(final DatagramSocket socket, final InetAddress address, final int port) throws IOException {
        sendMessage("BOOK_ANY", socket, address, port);
        final String response = receiveMessage(socket);
        System.out.println("POLL response= "+response);
        final String[] responseWords = response.split(" ");
        return responseWords.length > 1 ? responseWords[1].trim() : null;
    }

    private static void freeResource(final DatagramSocket socket, final InetAddress address, final int port, final String resource) throws IOException {
        sendMessage("FREE " + resource, socket, address, port);
    }

    private static String registerResources(final DatagramSocket socket, final InetAddress address, final int port, final List<String> resources) throws IOException {
        sendMessage("REGISTER " + mapStringsToString(resources), socket, address, port);
        return receiveMessage(socket);
    }

    private static void sendMessage(final String message, final DatagramSocket socket, final InetAddress address, final int port) throws IOException {
        final byte[] buffer = message.getBytes();
        DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(request);
    }

    private static String receiveMessage(final DatagramSocket socket) throws IOException {
        final DatagramPacket response = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
        socket.receive(response);
        return datagramMessage(response);
    }

    public static String mapStringsToString(final List<String> connectionRequests) {
        return connectionRequests.stream().map(inet -> inet + " ").reduce("", String::concat).trim();
    }

    private static String datagramMessage(final DatagramPacket response) {
        return new String(response.getData(), 0, response.getLength());
    }
}
