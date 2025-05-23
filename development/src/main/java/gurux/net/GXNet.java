//
// --------------------------------------------------------------------------
//  Gurux Ltd
// 
//
//
// Filename:        $HeadURL$
//
// Version:         $Revision$,
//                  $Date$
//                  $Author$
//
// Copyright (c) Gurux Ltd
//
//---------------------------------------------------------------------------
//
//  DESCRIPTION
//
// This file is a part of Gurux Device Framework.
//
// Gurux Device Framework is Open Source software; you can redistribute it
// and/or modify it under the terms of the GNU General Public License 
// as published by the Free Software Foundation; version 2 of the License.
// Gurux Device Framework is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
// See the GNU General Public License for more details.
//
// More information of Gurux products: http://www.gurux.org
//
// This code is licensed under the GNU General Public License v2. 
// Full text may be retrieved at http://www.gnu.org/licenses/gpl-2.0.txt
//---------------------------------------------------------------------------

package gurux.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import gurux.common.GXSync;
import gurux.common.GXSynchronousMediaBase;
import gurux.common.IGXMedia2;
import gurux.common.IGXMediaListener;
import gurux.common.MediaStateEventArgs;
import gurux.common.PropertyChangedEventArgs;
import gurux.common.ReceiveEventArgs;
import gurux.common.ReceiveParameters;
import gurux.common.TraceEventArgs;
import gurux.common.enums.MediaState;
import gurux.common.enums.TraceLevel;
import gurux.common.enums.TraceTypes;
import gurux.net.enums.AvailableMediaSettings;
import gurux.net.enums.NetworkType;

/**
 * The GXNet component determines methods that make the communication possible
 * using Internet.
 * 
 */
public class GXNet implements IGXMedia2, AutoCloseable {
    private int receiveDelay;

    private int asyncWaitTime;
    /**
     * Used protocol.
     */
    private NetworkType protocol = NetworkType.TCP;
    /**
     * Host name.
     */
    private String hostName;
    /**
     * Used port.
     */
    private int port;
    /**
     * Is server or client.
     */
    private boolean server;
    /**
     * Synchronous data handler.
     */
    private GXSynchronousMediaBase syncBase;
    /**
     * Created socket.
     */
    private java.io.Closeable socket = null;

    /**
     * Connected TCP/IP clients.
     */
    private List<Socket> tcpIpClients = new ArrayList<Socket>();

    /**
     * Amount of sent bytes.
     */
    private long bytesSent = 0;
    /**
     * Synchronous counter.
     */
    private int synchronous = 0;
    /**
     * Trace level.
     */
    private TraceLevel trace = TraceLevel.OFF;
    /**
     * Maximum client count.
     */
    private int maxClientCount;
    /**
     * Used end of packet.
     */
    private Object eop;
    /**
     * Configurable settings.
     */
    private int configurableSettings;
    /**
     * Media listeners.
     */
    private List<IGXMediaListener> listeners =
            new ArrayList<IGXMediaListener>();

    /**
     * Network listeners.
     */
    private List<IGXNetListener> netListeners = new ArrayList<IGXNetListener>();
    /**
     * Received thread.
     */
    private ReceiveThread receiverThread = null;

    /**
     * Listener thread.
     */
    private ListenerThread listenerThread = null;

    /**
     * How long connection can take.
     */
    private int connectionWaitTime = 0;

    /**
     * Used locale
     */
    private Locale locale;

    /**
     * Constructor.
     */
    public GXNet() {
        syncBase =
                new GXSynchronousMediaBase(ReceiveThread.RECEIVE_BUFFER_SIZE);
        setConfigurableSettings(AvailableMediaSettings.ALL.getValue());
        setProtocol(NetworkType.TCP);
        locale = Locale.getDefault();
    }

    /**
     * Client Constructor.
     * 
     * @param networkType
     *            Used protocol.
     * @param name
     *            Host name.
     * @param portNo
     *            Client port number.
     */
    public GXNet(final NetworkType networkType, final String name,
            final int portNo) {
        this();
        setProtocol(networkType);
        setHostName(name);
        setPort(portNo);
    }

    /**
     * Constructor used when server is started.
     * 
     * @param networkType
     *            Used protocol.
     * @param portNo
     *            Server port.
     */
    public GXNet(final NetworkType networkType, final int portNo) {
        this(networkType, null, portNo);
        this.setServer(true);
    }

    /**
     * 
     * Attach TCP/IP connection.
     * 
     * 
     * This can be used in server side if server want to start communicating
     * with client using synchronous communication. Close connection after use.
     * 
     * @param address
     *            cliend address to connect.
     * @return Attached connection.
     */
    public final GXNet attach(final String address) {
        if (protocol != NetworkType.TCP) {
            throw new IllegalArgumentException(
                    "Attach can be used only with TCP/IP connection.");
        }
        GXNet net = new GXNet();
        net.setConfigurableSettings(configurableSettings);
        net.setProtocol(protocol);
        net.setTrace(trace);
        synchronized (tcpIpClients) {
            for (Closeable it : tcpIpClients) {
                if (it instanceof Socket) {
                    Socket s = (Socket) it;
                    if (s.getRemoteSocketAddress().toString().equals(address)) {
                        net.socket = s;
                        net.hostName = hostName;
                        net.port = port;
                        tcpIpClients.remove(s);
                        net.receiverThread =
                                new ReceiveThread(net, (java.io.Closeable) s);
                        net.receiverThread.start();
                        break;
                    }
                }
            }
        }
        if (net.socket == null) {
            throw new IllegalArgumentException("Unknown address.");
        }
        return net;
    }

    /**
     * Destructor.
     */
    @Override
    protected final void finalize() throws Throwable {
        if (isOpen()) {
            close();
        }
    }

    /**
     * @return Gets connected TCP/IP clients.
     */
    final List<Socket> getTcpIpClients() {
        return tcpIpClients;
    }

    /**
     * Returns synchronous class used to communicate synchronously.
     * 
     * @return Synchronous class.
     */
    final GXSynchronousMediaBase getSyncBase() {
        return syncBase;
    }

    @Override
    public final TraceLevel getTrace() {
        return trace;
    }

    @Override
    public final void setTrace(final TraceLevel value) {
        trace = value;
        syncBase.setTrace(value);
    }

    /**
     * Notify that property has changed.
     * 
     * @param info
     *            Name of changed property.
     */
    private void notifyPropertyChanged(final String info) {
        for (IGXMediaListener it : listeners) {
            it.onPropertyChanged(this, new PropertyChangedEventArgs(info));
        }
    }

    /**
     * Notify that client has connected.
     * 
     * @param e
     *            Connection events argument.
     */
    final void notifyClientConnected(final ConnectionEventArgs e) {
        synchronized (netListeners) {
            for (IGXNetListener it : netListeners) {
                it.onClientConnected(this, e);
            }
            if (trace.ordinal() >= TraceLevel.INFO.ordinal()) {
                for (IGXMediaListener it : listeners) {
                    it.onTrace(this, new TraceEventArgs(TraceTypes.INFO,
                            "Client connected."));
                }
            }
        }
    }

    /**
     * Notifies clients that client is disconnected.
     * 
     * @param e
     *            Connection event argument.
     */
    final void notifyClientDisconnected(final ConnectionEventArgs e) {
        synchronized (netListeners) {
            for (IGXNetListener it : netListeners) {
                it.onClientDisconnected(this, e);
            }
            if (trace.ordinal() >= TraceLevel.INFO.ordinal()) {
                for (IGXMediaListener it : listeners) {
                    it.onTrace(this, new TraceEventArgs(TraceTypes.INFO,
                            "Client disconnected."));
                }
            }
        }
    }

    /**
     * Notify clients from error occurred.
     * 
     * @param ex
     *            Occurred error.
     */
    final void notifyError(final RuntimeException ex) {
        for (IGXMediaListener it : listeners) {
            it.onError(this, ex);
            if (trace.ordinal() >= TraceLevel.ERROR.ordinal()) {
                it.onTrace(this, new TraceEventArgs(TraceTypes.ERROR, ex));
            }
        }
    }

    /**
     * Notify clients from new data received.
     * 
     * @param e
     *            Received event argument.
     */
    final void notifyReceived(final ReceiveEventArgs e) {
        for (IGXMediaListener it : listeners) {
            it.onReceived(this, e);
        }
    }

    /**
     * Notify clients from trace events.
     * 
     * @param e
     *            Trace event argument.
     */
    final void notifyTrace(final TraceEventArgs e) {
        for (IGXMediaListener it : listeners) {
            it.onTrace(this, e);
        }
    }

    @Override
    public final int getConfigurableSettings() {
        return configurableSettings;
    }

    @Override
    public final void setConfigurableSettings(final int value) {
        this.configurableSettings = value;
    }

    @Override
    public final boolean properties(final javax.swing.JFrame parent) {
        GXSettings dlg = new GXSettings(parent, true, this, locale);
        dlg.pack();
        dlg.setVisible(true);
        return dlg.isAccepted();
    }

    /**
     * Displays the copyright of the control, user license, and version
     * information, in a dialog box.
     */
    public final void aboutBox() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param target
     *            IP address of the receiver (optional). Reply data is received
     *            through OnReceived event.
     */
    @Override
    public final void send(final Object data, final String target)
            throws Exception {
        if (socket == null) {
            throw new RuntimeException("Invalid connection.");
        }
        if (trace == TraceLevel.VERBOSE) {
            notifyTrace(new TraceEventArgs(TraceTypes.SENT, data));
        }
        // Reset last position if end of packet is used.
        synchronized (syncBase.getSync()) {
            syncBase.resetLastPosition();
        }
        byte[] buff = GXSynchronousMediaBase.getAsByteArray(data);
        if (buff == null) {
            throw new IllegalArgumentException(
                    "Data send failed. Invalid data.");
        }
        if (getServer()) {
            if (getProtocol() == NetworkType.TCP) {
                synchronized (tcpIpClients) {
                    for (Closeable it : tcpIpClients) {
                        if (it instanceof Socket) {
                            if (((Socket) it).getRemoteSocketAddress()
                                    .toString().equals(target)) {
                                ((Socket) it).getOutputStream().write(buff);
                                break;
                            }
                        }
                    }
                }
            } else {
                String info;
                if (target.startsWith("/")) {
                    info = target.substring(1);
                } else {
                    info = target;
                }

                int index = info.lastIndexOf(":");
                if (index == -1) {
                    throw new RuntimeException("Port is missing.");
                }
                InetAddress addr =
                        InetAddress.getByName(info.substring(0, index));
                DatagramPacket p = new DatagramPacket(buff, buff.length, addr,
                        Integer.parseInt(info.substring(1 + index)));
                ((DatagramSocket) socket).send(p);
            }
        } else {
            if (getProtocol() == NetworkType.TCP) {
                ((Socket) socket).getOutputStream().write(buff);
            } else if (getProtocol() == NetworkType.UDP) {
                InetAddress addr = InetAddress.getByName(getHostName());
                DatagramPacket p =
                        new DatagramPacket(buff, buff.length, addr, getPort());
                ((DatagramSocket) socket).send(p);
            }
        }
        this.bytesSent += buff.length;

    }

    /**
     * Notify client from media state change.
     * 
     * @param state
     *            New media state.
     */
    void notifyMediaStateChange(final MediaState state) {
        for (IGXMediaListener it : listeners) {
            if (trace.ordinal() >= TraceLevel.ERROR.ordinal()) {
                it.onTrace(this, new TraceEventArgs(TraceTypes.INFO, state));
            }
            it.onMediaStateChange(this, new MediaStateEventArgs(state));
        }
    }

    /**
     * Opens the connection. Protocol, Port and HostName must be set, before
     * calling the Open method.
     * 
     * @see #port
     * @see #hostName
     * @see #protocol
     * @see #server
     * @see #close
     */
    @Override
    public final void open() throws Exception {
        close();
        try {
            synchronized (syncBase.getSync()) {
                syncBase.resetLastPosition();
            }
            notifyMediaStateChange(MediaState.OPENING);
            if (this.getServer()) {
                if (trace.ordinal() >= TraceLevel.INFO.ordinal()) {
                    notifyTrace(new TraceEventArgs(TraceTypes.INFO,
                            "Server settings: Protocol: "
                                    + this.getProtocol().toString() + " Port: "
                                    + String.valueOf(getPort())));
                }
                if (getProtocol() == NetworkType.TCP) {
                    socket = new ServerSocket(getPort());
                    listenerThread = new ListenerThread(this, socket);
                    listenerThread.start();
                    listenerThread.waitUntilRun();
                } else if (getProtocol() == NetworkType.UDP) {
                    socket = new DatagramSocket(getPort());
                    receiverThread = new ReceiveThread(this, socket);
                    receiverThread.start();
                    receiverThread.waitUntilRun();
                }

            } else {
                // Create a stream-based, TCP socket using the InterNetwork
                // Address Family.
                if (getProtocol() == NetworkType.TCP) {
                    // If value is zero OS wait time is used.
                    if (connectionWaitTime == 0) {
                        socket = new Socket(getHostName(), getPort());
                    } else {
                        socket = new Socket();
                        ((Socket) socket).connect(
                                new InetSocketAddress(getHostName(), getPort()),
                                connectionWaitTime);
                    }
                } else if (getProtocol() == NetworkType.UDP) {
                    socket = new DatagramSocket();
                } else {
                    throw new IllegalArgumentException("Protocol");
                }
                if (trace.ordinal() >= TraceLevel.INFO.ordinal()) {
                    notifyTrace(new TraceEventArgs(TraceTypes.INFO,
                            "Client settings: Protocol: "
                                    + this.getProtocol().toString() + " Host: "
                                    + getHostName() + " Port: "
                                    + String.valueOf(getPort())));
                }
                receiverThread = new ReceiveThread(this, socket);
                receiverThread.start();
                receiverThread.waitUntilRun();
            }
            notifyMediaStateChange(MediaState.OPEN);
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    @Override
    public final void close() {
        if (socket != null) {
            if (getServer() && listenerThread != null) {
                // Close all active sockets.
                synchronized (tcpIpClients) {
                    for (Closeable it : tcpIpClients) {
                        if (it instanceof Socket) {
                            try {
                                it.close();
                            } catch (IOException e) {
                                // It's OK if this fails.
                            }
                        }
                    }
                }
                listenerThread.interrupt();
                listenerThread = null;
            } else if (receiverThread != null) {
                try {
                    receiverThread.interrupt();
                    if (socket instanceof Socket) {
                        ((Socket) socket).shutdownOutput();
                        // Wait until the client has received server's ACK.
                        receiverThread.join(10000);
                    }
                } catch (InterruptedException | IOException e) {
                }
                receiverThread = null;
            }
            try {
                notifyMediaStateChange(MediaState.CLOSING);
            } catch (RuntimeException ex) {
                notifyError(ex);
                throw ex;
            } finally {
                try {
                    socket.close();
                } catch (java.lang.Exception e) {
                    // Ignore all errors on close.
                }
                socket = null;
                notifyMediaStateChange(MediaState.CLOSED);
                bytesSent = 0;
                if (receiverThread != null) {
                    receiverThread.resetBytesReceived();
                }
                syncBase.resetReceivedSize();
            }
        }
    }

    @Override
    public final boolean isOpen() {
        return socket != null;
    }

    /**
     * Retrieves the used protocol.
     * 
     * @return Protocol in use.
     */
    public final NetworkType getProtocol() {
        return protocol;
    }

    /**
     * Sets the used protocol.
     * 
     * @param value
     *            Used protocol.
     */
    public final void setProtocol(final NetworkType value) {
        if (protocol != value) {
            protocol = value;
            notifyPropertyChanged("Protocol");
        }
    }

    /**
     * Retrieves the name or IP address of the host.
     * 
     * @return The name of the host.
     * 
     * @see #open
     * @see #port
     * @see #protocol
     */
    public final String getHostName() {
        return hostName;
    }

    /**
     * Sets the name or IP address of the host.
     * 
     * @param value
     *            The name of the host.
     */
    public final void setHostName(final String value) {
        if (hostName == null || !hostName.equals(value)) {
            hostName = value;
            notifyPropertyChanged("HostName");
        }
    }

    /**
     * Retrieves or sets the host or server port number.
     * 
     * @return Host or server port number.
     * 
     * @see #open
     * @see #hostName
     * @see #protocol
     */
    public final int getPort() {
        return port;
    }

    /**
     * Retrieves or sets the host or server port number.
     * 
     * @param value
     *            Host or server port number
     * @see #open
     * @see #hostName
     * @see #protocol
     */
    public final void setPort(final int value) {
        if (port != value) {
            port = value;
            notifyPropertyChanged("Port");
        }
    }

    /**
     * 
     * Is server mode used.
     * 
     * @see #open
     * @return Is server mode used.
     */
    public final boolean getServer() {
        return server;
    }

    /**
     * 
     * Is server mode used.
     * 
     * @param value
     *            Is server mode used.
     * @see #open
     */
    public final void setServer(final boolean value) {
        if (server != value) {
            server = value;
            notifyPropertyChanged("Server");
        }
    }

    /**
     * Gets how long (milliseconds) server answer is waited when connection is
     * made. Operating system time is used if value is zero.
     * 
     * @return Wait time in milliseconds.
     */
    public final int getConnectionWaitTime() {
        return connectionWaitTime;
    }

    /**
     * Sets how long (milliseconds) server answer is waited when connection is
     * made. Operating system time is used if value is zero.
     * 
     * @param value
     *            Wait time in milliseconds.
     */
    public final void setConnectionWaitTime(final int value) {
        boolean change = connectionWaitTime != value;
        connectionWaitTime = value;
        if (change) {
            notifyPropertyChanged("ConnectionWaitTime");
        }
    }

    @Override
    public final <T> boolean receive(final ReceiveParameters<T> args) {
        return syncBase.receive(args);
    }

    /**
     * Sent byte count.
     * 
     * @see #getBytesReceived
     * @see #resetByteCounters
     */
    @Override
    public final long getBytesSent() {
        return bytesSent;
    }

    /**
     * Received byte count.
     * 
     * @see #bytesSent
     * @see #resetByteCounters
     */
    @Override
    public final long getBytesReceived() {
        if (receiverThread == null) {
            return 0;
        }
        return receiverThread.getBytesReceived();
    }

    /**
     * Resets BytesReceived and BytesSent counters.
     * 
     * @see #bytesSent
     * @see #getBytesReceived
     */
    @Override
    public final void resetByteCounters() {
        bytesSent = 0;
        if (receiverThread != null) {
            receiverThread.resetBytesReceived();
        }
    }

    /**
     * Retrieves maximum count of connected clients.
     * 
     * @return Maximum count of connected clients.
     */
    public final int getMaxClientCount() {
        return maxClientCount;
    }

    /**
     * Sets maximum count of connected clients.
     * 
     * @param value
     *            Maximum count of connected clients.
     */
    public final void setMaxClientCount(final int value) {
        maxClientCount = value;
    }

    /**
     * Media settings as a XML string.
     */
    @Override
    public final String getSettings() {
        StringBuilder sb = new StringBuilder();
        String nl = System.getProperty("line.separator");
        if (server) {
            sb.append("<Server>");
            if (server) {
                sb.append("1");
            } else {
                sb.append("0");
            }
            sb.append("</Server>");
            sb.append(nl);
        }
        if (hostName != null && !hostName.isEmpty()) {
            sb.append("<IP>");
            sb.append(hostName);
            sb.append("</IP>");
            sb.append(nl);
        }
        if (port != 0) {
            sb.append("<Port>");
            sb.append(String.valueOf(port));
            sb.append("</Port>");
            sb.append(nl);
        }
        if (protocol != NetworkType.TCP) {
            sb.append("<Protocol>");
            sb.append(protocol.ordinal());
            sb.append("</Protocol>");
            sb.append(nl);
        }
        return sb.toString();
    }

    @Override
    public final void setSettings(final String value) {
        // Reset to default values.
        server = false;
        hostName = "";
        port = 0;
        protocol = NetworkType.TCP;
        if (value != null && !value.isEmpty()) {
            try {
                DocumentBuilderFactory factory =
                        DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                StringBuilder sb = new StringBuilder();
                if (value.startsWith("<?xml version=\"1.0\"?>")) {
                    sb.append(value);
                } else {
                    String nl = System.getProperty("line.separator");
                    sb.append("<?xml version=\"1.0\"?>\r\n");
                    sb.append(nl);
                    sb.append("<Net>");
                    sb.append(value);
                    sb.append(nl);
                    sb.append("</Net>");
                }
                InputSource is =
                        new InputSource(new StringReader(sb.toString()));
                Document doc = builder.parse(is);
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getChildNodes();
                if (nList.getLength() != 1) {
                    throw new IllegalArgumentException(
                            "Invalid XML root node.");
                }
                nList = nList.item(0).getChildNodes();
                for (int pos = 0; pos < nList.getLength(); ++pos) {
                    Node it = nList.item(pos);
                    if (it.getNodeType() == Node.ELEMENT_NODE) {
                        if ("Server".equalsIgnoreCase(it.getNodeName())) {
                            setServer(true);
                        } else if ("IP".equalsIgnoreCase(it.getNodeName())) {
                            setHostName(it.getFirstChild().getNodeValue());
                        } else if ("Port".equalsIgnoreCase(it.getNodeName())) {
                            setPort(Integer.parseInt(
                                    it.getFirstChild().getNodeValue()));
                        } else if ("Protocol"
                                .equalsIgnoreCase(it.getNodeName())) {
                            setProtocol(NetworkType.values()[Integer.parseInt(
                                    it.getFirstChild().getNodeValue())]);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public final void copy(final Object target) {
        GXNet tmp = (GXNet) target;
        setPort(tmp.getPort());
        setHostName(tmp.getHostName());
        setProtocol(tmp.getProtocol());
    }

    @Override
    public final String getName() {
        String tmp;
        tmp = getHostName() + " " + getPort();
        if (getProtocol() == NetworkType.UDP) {
            tmp += "UDP";
        } else {
            tmp += "TCP/IP";
        }
        return tmp;
    }

    @Override
    public final String getMediaType() {
        return "Net";
    }

    @Override
    public final Object getSynchronous() {
        synchronized (this) {
            int[] tmp = new int[] { synchronous };
            GXSync obj = new GXSync(tmp);
            synchronous = tmp[0];
            return obj;
        }
    }

    @Override
    public final boolean getIsSynchronous() {
        synchronized (this) {
            return synchronous != 0;
        }
    }

    @Override
    public final void resetSynchronousBuffer() {
        synchronized (syncBase.getSync()) {
            syncBase.resetReceivedSize();
        }
    }

    @Override
    public final void validate() {
        if (getPort() == 0) {
            // Localize strings.
            ResourceBundle bundle =
                    ResourceBundle.getBundle("resources", locale);
            throw new RuntimeException(bundle.getString("InvalidPortName"));
        }
        if (getHostName() == null || "".equals(getHostName())) {
            ResourceBundle bundle =
                    ResourceBundle.getBundle("resources", locale);
            throw new RuntimeException(bundle.getString("InvalidHostName"));
        }
    }

    @Override
    public final Object getEop() {
        return eop;
    }

    @Override
    public final void setEop(final Object value) {
        eop = value;
    }

    @Override
    public final void addListener(final IGXMediaListener listener) {
        synchronized (netListeners) {
            listeners.add(listener);
            if (listener instanceof IGXNetListener) {
                netListeners.add((IGXNetListener) listener);
            }
        }
    }

    @Override
    public final void removeListener(final IGXMediaListener listener) {
        synchronized (netListeners) {
            listeners.remove(listener);
            if (listener instanceof IGXNetListener) {
                netListeners.remove((IGXNetListener) listener);
            }
        }
    }

    @Override
    public int getReceiveDelay() {
        return receiveDelay;
    }

    @Override
    public void setReceiveDelay(final int value) {
        receiveDelay = value;
    }

    @Override
    public int getAsyncWaitTime() {
        return asyncWaitTime;
    }

    @Override
    public void setAsyncWaitTime(final int value) {
        asyncWaitTime = value;
    }

    @Override
    public Object getAsyncWaitHandle() {
        return null;
    }

    /**
     * @return Used locale.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * 
     * @param value
     *            Used locale.
     */
    public void setLocale(final Locale value) {
        locale = value;
    }

    @Override
    public String toString() {
        return "GXNet " + getName();
    }
}