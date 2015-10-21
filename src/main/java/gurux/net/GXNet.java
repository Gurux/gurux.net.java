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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import gurux.common.GXSync;
import gurux.common.GXSynchronousMediaBase;
import gurux.common.IGXMedia;
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
public class GXNet implements IGXMedia, AutoCloseable {
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
     * Is IP6 used.
     */
    private boolean useIPv6;
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
    private ReceiveThread receiver = null;

    /**
     * Constructor.
     */
    public GXNet() {
        syncBase =
                new GXSynchronousMediaBase(ReceiveThread.RECEIVE_BUFFER_SIZE);
        setConfigurableSettings(AvailableMediaSettings.ALL.getValue());
        setProtocol(NetworkType.TCP);
    }

    /**
     * Client Constructor.
     * 
     * @param networkType
     *            Used protocol.
     * @param name
     *            Host name.
     * @param port
     *            Client port.
     */
    public GXNet(final NetworkType networkType, final String name,
            final int port) {
        this();
        setProtocol(networkType);
        setHostName(name);
        setPort(port);
    }

    /**
     * Constructor used when server is started.
     * 
     * @param networkType
     *            Used protocol.
     * @param port
     *            Server port.
     */
    public GXNet(final NetworkType networkType, final int port) {
        this(networkType, null, port);
        this.setServer(true);
    }

    /**
     * Destructor.
     */
    @Override
    protected final void finalize() throws Throwable {
        super.finalize();
        if (isOpen()) {
            close();
        }
    }

    /**
     * Returns synchronous class used to communicate synchronously.
     * 
     * @return Synchronous class.
     */
    final GXSynchronousMediaBase getSyncBase() {
        return syncBase;
    }

    /**
     * Is IPv6 used. Default is False (IPv4).
     * 
     * @return Is IPv6 used.
     */
    public final boolean getUseIPv6() {
        return useIPv6;
    }

    /**
     * Is IPv6 used. Default is False (IPv4).
     * 
     * @param value
     *            Is IPv6 used.
     */
    public final void setUseIPv6(final boolean value) {
        useIPv6 = value;
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
        for (IGXMediaListener listener : listeners) {
            listener.onPropertyChanged(this,
                    new PropertyChangedEventArgs(info));
        }
    }

    /**
     * Notify that client has connected.
     * 
     * @param e
     *            Connection events argument.
     */
    final void notifyClientConnected(final ConnectionEventArgs e) {
        for (IGXNetListener listener : netListeners) {
            listener.onClientConnected(this, e);
        }
        if (trace.ordinal() >= TraceLevel.INFO.ordinal()) {
            for (IGXMediaListener listener : listeners) {
                listener.onTrace(this, new TraceEventArgs(TraceTypes.INFO,
                        "Client connected."));
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
        for (IGXNetListener listener : netListeners) {
            listener.onClientDisconnected(this, e);
        }
        if (trace.ordinal() >= TraceLevel.INFO.ordinal()) {
            for (IGXMediaListener listener : listeners) {
                listener.onTrace(this, new TraceEventArgs(TraceTypes.INFO,
                        "Client disconnected."));
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
        for (IGXMediaListener listener : listeners) {
            listener.onError(this, ex);
            if (trace.ordinal() >= TraceLevel.ERROR.ordinal()) {
                listener.onTrace(this,
                        new TraceEventArgs(TraceTypes.ERROR, ex));
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
        for (IGXMediaListener listener : listeners) {
            listener.onReceived(this, e);
        }
    }

    /**
     * Notify clients from trace events.
     * 
     * @param e
     *            Trace event argument.
     */
    final void notifyTrace(final TraceEventArgs e) {
        for (IGXMediaListener listener : listeners) {
            listener.onTrace(this, e);
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
        GXSettings dlg = new GXSettings(parent, true, this);
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
        if (this.getServer()) {
            if (getProtocol() == NetworkType.TCP) {
                ((Socket) socket).getOutputStream().write(buff);
            } else if (getProtocol() == NetworkType.UDP) {
                InetAddress addr = InetAddress.getByName(getHostName());
                DatagramPacket p =
                        new DatagramPacket(buff, buff.length, addr, getPort());
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
    private void notifyMediaStateChange(final MediaState state) {
        for (IGXMediaListener listener : listeners) {
            if (trace.ordinal() >= TraceLevel.ERROR.ordinal()) {
                listener.onTrace(this,
                        new TraceEventArgs(TraceTypes.INFO, state));
            }
            listener.onMediaStateChange(this, new MediaStateEventArgs(state));
        }
    }

    /**
     * Opens the connection. Protocol, Port and HostName must be set, before
     * calling the Open method.
     * 
     * @see port port
     * @see hostName hostName
     * @see protocol protocol
     * @see server server
     * @see close close
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
                if (getProtocol() == NetworkType.TCP) {
                    socket = (Closeable) new ServerSocket(getPort());
                } else if (getProtocol() == NetworkType.UDP) {
                    socket = (Closeable) new DatagramSocket();
                }
                if (trace.ordinal() >= TraceLevel.INFO.ordinal()) {
                    notifyTrace(new TraceEventArgs(TraceTypes.INFO,
                            "Server settings: Protocol: "
                                    + this.getProtocol().toString() + " Port: "
                                    + (new Integer(getPort())).toString()));
                }
            } else {
                // Create a stream-based, TCP socket using the InterNetwork
                // Address Family.
                if (getProtocol() == NetworkType.TCP) {
                    socket = (Closeable) new Socket(getHostName(), getPort());
                } else if (getProtocol() == NetworkType.UDP) {
                    socket = (Closeable) new DatagramSocket();
                } else {
                    throw new IllegalArgumentException("Protocol");
                }
                if (trace.ordinal() >= TraceLevel.INFO.ordinal()) {
                    notifyTrace(new TraceEventArgs(TraceTypes.INFO,
                            "Client settings: Protocol: "
                                    + this.getProtocol().toString() + " Host: "
                                    + getHostName() + " Port: "
                                    + (new Integer(getPort())).toString()));
                }
            }
            receiver = new ReceiveThread(this, socket);
            receiver.start();
            notifyMediaStateChange(MediaState.OPEN);
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    @Override
    public final void close() {
        if (socket != null) {
            if (receiver != null) {
                receiver.interrupt();
                receiver = null;
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
                if (receiver != null) {
                    receiver.resetBytesReceived();
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
     * @see open open
     * @see port port
     * @see protocol protocol
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
     * @see open open
     * @see hostName hostName
     * @see protocol protocol
     */
    public final int getPort() {
        return port;
    }

    /**
     * Retrieves or sets the host or server port number.
     * 
     * @param value
     *            Host or server port number
     * @see open open
     * @see hostName hostName
     * @see protocol protocol
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
     * @see open open
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
     * @see open open
     */
    public final void setServer(final boolean value) {
        if (server != value) {
            server = value;
            notifyPropertyChanged("Server");
        }
    }

    @Override
    public final <T> boolean receive(final ReceiveParameters<T> args) {
        return syncBase.receive(args);
    }

    /**
     * Sent byte count.
     * 
     * @see getBytesReceived getBytesReceived
     * @see resetByteCounters resetByteCounters
     */
    @Override
    public final long getBytesSent() {
        return bytesSent;
    }

    /**
     * Received byte count.
     * 
     * @see bytesSent bytesSent
     * @see resetByteCounters resetByteCounters
     */
    @Override
    public final long getBytesReceived() {
        if (receiver == null) {
            return 0;
        }
        return receiver.getBytesReceived();
    }

    /**
     * Resets BytesReceived and BytesSent counters.
     * 
     * @see bytesSent bytesSent
     * @see getBytesReceived getBytesReceived
     */
    @Override
    public final void resetByteCounters() {
        bytesSent = 0;
        if (receiver != null) {
            receiver.resetBytesReceived();
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
        // TODO:
        return null;
    }

    @Override
    public final void setSettings(final String value) {
        // TODO:
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
            throw new RuntimeException("Invalid port name.");
        }
        if (getHostName() == null || "".equals(getHostName())) {
            throw new RuntimeException("Invalid host name.");
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
        listeners.add(listener);
        if (listener instanceof IGXNetListener) {
            netListeners.add((IGXNetListener) listener);
        }
    }

    @Override
    public final void removeListener(final IGXMediaListener listener) {
        listeners.remove(listener);
        if (listener instanceof IGXNetListener) {
            netListeners.remove((IGXNetListener) listener);
        }
    }
}