import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestServer extends WebSocketServer {

    private final BlockingQueue<String> captured = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> messagesToSend = new LinkedBlockingQueue<>();
    private static final Logger log = LoggerFactory.getLogger(TestServer.class);

    public void loadMessages(List<String> messages) {
        messages.forEach(messagesToSend::offer);
    }

    public void clearMessageQueue() {
        messagesToSend.clear();
    }

    public TestServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        log.info(
                conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected to the test server");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        log.info(conn + " has disconnected");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        log.info(message);
        captured.offer(message);
        for (String path : messagesToSend) {
            try {
                String json = TestUtils.readFileFromPath(path);
                log.debug("Sending json: " + json);
                conn.send(json);
            } catch (Exception e) {
                throw new RuntimeException("Could not load json file: " + path);
            }
        }
    }

    public static void main(String[] args) {
        // 8887
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TestServer s = new TestServer(port);
        s.start();
        log.info("Test server started on port: " + s.getPort());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        log.info("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

}