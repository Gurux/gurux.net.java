package gurux.net.java;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import gurux.common.IGXMediaListener;
import gurux.common.MediaStateEventArgs;
import gurux.common.PropertyChangedEventArgs;
import gurux.common.ReceiveEventArgs;
import gurux.common.ReceiveParameters;
import gurux.common.TraceEventArgs;
import gurux.net.ConnectionEventArgs;
import gurux.net.GXNet;
import gurux.net.IGXNetListener;
import gurux.net.enums.NetworkType;

/**
 * Unit test for serial port media.
 */
/**
 * @author Gurux Ltd
 */
public class GXNetTest implements IGXMediaListener, IGXNetListener {
    /**
     * Used TCP/IP port.
     * 
     */
    private static final int TCP_IP_PORT = 1001;
    /**
     * Used UDP port.
     */
    private static final int UDP_PORT = 1002;

    /**
     * Wait time.
     */
    private static final int WAIT_TIME = 50000;

    /**
     * TCP/IP test.
     * 
     * @throws Exception
     *             Occurred exception.
     */
    @Test
    public final void tcpIpTest() throws Exception {
        GXNet server = new GXNet(NetworkType.TCP, TCP_IP_PORT);
        GXNet client = new GXNet(NetworkType.TCP, "localhost", TCP_IP_PORT);
        test(server, client);
    }

    /**
     * UDP test.
     * 
     * @throws Exception
     *             Occurred exception.
     */
    @Test
    public final void udpTest() throws Exception {
        GXNet server = new GXNet(NetworkType.UDP, UDP_PORT);
        GXNet client = new GXNet(NetworkType.UDP, "localhost", UDP_PORT);
        test(server, client);
    }

    /**
     * Executed tests.
     * 
     * @param server
     *            server.
     * @param client
     *            client.
     * @throws Exception
     *             Occurred exception.
     */
    private void test(final GXNet server, final GXNet client) throws Exception {
        server.addListener(this);
        client.addListener(this);
        server.open();
        client.open();
        synchronized (client.getSynchronous()) {
            String expected = "Hello World!";
            client.send(expected, null);
            ReceiveParameters<String> p =
                    new ReceiveParameters<String>(String.class);
            p.setWaitTime(WAIT_TIME);
            p.setCount(expected.length());
            client.receive(p);
            assertEquals(expected, p.getReply());
        }
        client.close();
        server.close();
    }

    @Override
    public final void onClientConnected(final Object sender,
            final ConnectionEventArgs e) {
        // TODO Auto-generated method stub

    }

    @Override
    public final void onClientDisconnected(final Object sender,
            final ConnectionEventArgs e) {
        // TODO Auto-generated method stub

    }

    @Override
    public final void onError(final Object sender, final Exception ex) {
        throw new RuntimeException(ex.getMessage());
    }

    /**
     * Echo received data.
     */
    @Override
    public final void onReceived(final Object sender,
            final ReceiveEventArgs e) {
        try {
            ((GXNet) sender).send(e.getData(), e.getSenderInfo());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public final void onMediaStateChange(final Object sender,
            final MediaStateEventArgs e) {
        // TODO Auto-generated method stub

    }

    @Override
    public final void onTrace(final Object sender, final TraceEventArgs e) {
        // TODO Auto-generated method stub

    }

    @Override
    public final void onPropertyChanged(final Object sender,
            final PropertyChangedEventArgs e) {
        // TODO Auto-generated method stub
    }

    /**
     * Settings test.
     * 
     */
    @Test
    public final void settingsTest() {
        String nl = System.getProperty("line.separator");
        try (GXNet client = new GXNet(NetworkType.UDP, "localhost", UDP_PORT)) {

            String expected = "<IP>localhost</IP>" + nl + "<Port>1002</Port>"
                    + nl + "<Protocol>0</Protocol>" + nl;
            String actual = client.getSettings();
            assertEquals(expected, actual);
            client.setSettings(actual);
        }
    }
}
