import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * This program demonstrates how to implement PeerMessageAnalyzer
 *
 * @author criss.tmd@gmail.com
 */
public class PeerMessageAnalyzer extends Thread {
    private final ResourceRepository resourceRepository;
    private final ISendPacketToPeer peerSender;

    final DatagramPacket datagramPacket;

    public PeerMessageAnalyzer(ISendPacketToPeer peerSender, final DatagramPacket datagramPacket) {
        this.peerSender = peerSender;
        this.resourceRepository = new ResourceRepository();
        this.datagramPacket = datagramPacket;
    }

    public void run() {
        processMessage(datagramPacket);
    }

    private void processMessage(final DatagramPacket packet) {
        System.out.println("[PeerMessageAnalyzer] processMessage");
        if (packet == null) {
            return;
        }
        final InetAddress clientAddress = packet.getAddress();
        final int clientPort = packet.getPort();
        final String message = new String(packet.getData(), StandardCharsets.UTF_8);
        final String[] words = PeerMessageHelper.getNonEmptyWords(message);
        final String firstWord = words.length > 0 ? words[0].trim() : null;
        if (firstWord == null) {
            System.out.println("[PeerMessageAnalyzer] Error: no words in the message!");
            return;
        }

        switch (firstWord) {
            case "UPDATE_LIST" -> {
                System.out.println("[PeerMessageAnalyzer] UPDATE_LIST ");
                for (int i=1; i<words.length; i++) {
                    final String resource = words[i].trim();
                    resourceRepository.register(resource);
                }
                sendMessageToClient("UPDATE_LIST_DONE", clientAddress, clientPort);
            }
            case "BOOK_ANY" -> {
                System.out.println("[PeerMessageAnalyzer] POLL");
                final Optional<String> resourceResult = resourceRepository.bookResource();
                sendMessageToClient("BOOK_ANY_RESPONSE " + resourceResult.orElse(""), clientAddress, clientPort);
            }
            case "FREE" -> {
                System.out.println("[PeerMessageAnalyzer] FREE ");
                final String resource = words[1].trim();
                resourceRepository.freeResource(resource);
                sendMessageToClient("FREE_DONE " + resource, clientAddress, clientPort);
            }
        }
    }

    private void sendMessageToClient(final String message, final InetAddress address, final int port) {
        System.out.println("[PeerMessageAnalyzer] sent message \""+message + "\" to " + address.getHostAddress() + ":" + port);
        final DatagramPacket packetToClient = new DatagramPacket(message.getBytes(), message.length());
        packetToClient.setAddress(address);
        packetToClient.setPort(port);
        peerSender.sendPacketToPeer(packetToClient);
    }
}
