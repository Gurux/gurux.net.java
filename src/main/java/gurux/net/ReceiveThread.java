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

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import gurux.common.GXSynchronousMediaBase;
import gurux.common.ReceiveEventArgs;
import gurux.common.TraceEventArgs;
import gurux.common.enums.TraceLevel;
import gurux.common.enums.TraceTypes;
import gurux.net.enums.NetworkType;

/**
 * Receive thread listens socket and sends received data to the listeners.
 * 
 * @author Gurux Ltd.
 *
 */
class ReceiveThread extends Thread {

    /**
     * Server socket.
     */
    private final ServerSocket serverSocket;
    /**
     * Parent component where notifies are send.
     */
    private GXNet parentMedia;
    /**
     * Client TCP/IP socket to listen.
     */
    private Socket tcpSocket = null;
    /**
     * Client UDP socket.
     */
    private final DatagramSocket udpSocket;
    /**
     * Buffer where received data is saved.
     */
    private byte[] buffer = null;
    /**
     * Buffer position.
     */
    private int bufferPosition = 0;

    /**
     * Amount of received bytes.
     */
    private long bytesReceived = 0;

    /**
     * Size of receive buffer.
     */
    public static final int RECEIVE_BUFFER_SIZE = 1024;

    /**
     * Constructor.
     * 
     * @param parent
     *            Parent media.
     * @param socket
     *            Socket to listen.
     */
    ReceiveThread(final GXNet parent, final java.io.Closeable socket) {
        super("GXNet " + socket.toString());
        parentMedia = parent;
        if (parent.getProtocol() == NetworkType.TCP) {
            udpSocket = null;
            buffer = new byte[RECEIVE_BUFFER_SIZE];
            if (parent.getServer()) {
                serverSocket = (ServerSocket) socket;
            } else {
                serverSocket = null;
                tcpSocket = (Socket) socket;
            }
        } else {
            serverSocket = null;
            udpSocket = (DatagramSocket) socket;
        }
        bufferPosition = 0;
    }

    /**
     * Gets client socket.
     * 
     * @return Client socket.
     */
    public Socket getClient() {
        return tcpSocket;
    }

    /**
     * Get amount of received bytes.
     * 
     * @return Amount of received bytes.
     */
    public final long getBytesReceived() {
        return bytesReceived;
    }

    /**
     * Reset amount of received bytes.
     */
    public final void resetBytesReceived() {
        bytesReceived = 0;
    }

    /**
     * Handle received data.
     * 
     * @param length
     *            Length of received data.
     * @param info
     *            Sender information.
     */
    private void handleReceivedData(final int length, final String info) {
        if (length == 0) {
            return;
        }
        Object eop = parentMedia.getEop();
        bytesReceived += length;
        int totalCount = 0;
        if (parentMedia.getIsSynchronous()) {
            TraceEventArgs arg = null;
            synchronized (parentMedia.getSyncBase().getSync()) {
                parentMedia.getSyncBase().appendData(buffer, bufferPosition,
                        length);
                // Search end of packet if it is given.
                if (eop != null) {
                    if (eop instanceof Array) {
                        for (Object it : (Object[]) eop) {
                            totalCount = GXSynchronousMediaBase.indexOf(buffer,
                                    GXSynchronousMediaBase.getAsByteArray(it),
                                    bufferPosition - length, bufferPosition);
                            if (totalCount != -1) {
                                break;
                            }
                        }
                    } else {
                        totalCount = GXSynchronousMediaBase.indexOf(buffer,
                                GXSynchronousMediaBase.getAsByteArray(eop),
                                bufferPosition - length, bufferPosition);
                    }
                }
                if (totalCount != -1) {
                    if (parentMedia.getTrace() == TraceLevel.VERBOSE) {
                        arg = new gurux.common.TraceEventArgs(
                                TraceTypes.RECEIVED, buffer, 0, totalCount + 1);
                    }
                    parentMedia.getSyncBase().setReceived();
                }
            }
            if (arg != null) {
                parentMedia.notifyTrace(arg);
            }
        } else {
            parentMedia.getSyncBase().resetReceivedSize();
            // Search end of packet if it is given.
            if (eop != null) {
                if (eop instanceof Array) {
                    for (Object it : (Object[]) eop) {

                        totalCount = GXSynchronousMediaBase.indexOf(buffer,
                                GXSynchronousMediaBase.getAsByteArray(it),
                                bufferPosition - length, bufferPosition);
                        if (totalCount != -1) {
                            break;
                        }
                    }
                } else {
                    totalCount = GXSynchronousMediaBase.indexOf(buffer,
                            GXSynchronousMediaBase.getAsByteArray(eop),
                            bufferPosition - length, bufferPosition);
                }
                if (totalCount != -1) {
                    byte[] data = new byte[length];
                    System.arraycopy(buffer, 0, data, 0, totalCount);
                    System.arraycopy(buffer, 0, buffer, totalCount,
                            bufferPosition - totalCount);
                    bufferPosition = 0;
                    ReceiveEventArgs e = new ReceiveEventArgs(data, info);
                    parentMedia.notifyReceived(e);
                    if (parentMedia.getTrace() == TraceLevel.VERBOSE) {
                        parentMedia.notifyTrace(new gurux.common.TraceEventArgs(
                                TraceTypes.RECEIVED, buffer, 0, length));
                    }
                }
            } else {
                byte[] data = new byte[length];
                System.arraycopy(buffer, 0, data, 0, length);
                if (parentMedia.getTrace() == TraceLevel.VERBOSE) {
                    parentMedia.notifyTrace(new gurux.common.TraceEventArgs(
                            TraceTypes.RECEIVED, data));
                }
                ReceiveEventArgs e = new ReceiveEventArgs(data, info);
                parentMedia.notifyReceived(e);
            }
        }
    }

    /**
     * Read data from TCP/IP stream.
     * 
     * @param socket
     *            socket to read.
     * @throws IOException
     *             occurred exception.
     */
    private void handleTCP(final Socket socket) throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        int count = in.read(buffer, bufferPosition, 1);
        if (count == -1) {
            throw new SocketException();
        }
        while (in.available() != 0) {
            int cnt = in.available();
            if (bufferPosition + cnt > buffer.length) {
                cnt = buffer.length - bufferPosition - count;
            }
            count += in.read(buffer, bufferPosition + count, cnt);
            // If buffer is full.
            if (count == buffer.length) {
                handleReceivedData(count,
                        socket.getRemoteSocketAddress().toString());
                count = 0;
                bufferPosition = 0;
            }
        }
        handleReceivedData(count, socket.getRemoteSocketAddress().toString());
    }

    /**
     * Receive data from the server using the established socket connection.
     * 
     * @return The data received from the server
     */
    @Override
    public final void run() {
        if (serverSocket != null) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    tcpSocket = serverSocket.accept();
                    String info =
                            String.valueOf(tcpSocket.getRemoteSocketAddress());
                    parentMedia.notifyClientConnected(
                            new ConnectionEventArgs(info));
                    while (!Thread.currentThread().isInterrupted()) {
                        handleTCP(tcpSocket);
                    }
                } catch (SocketException ex) {
                    // Client closed the connection.
                    if (tcpSocket != null) {
                        String info = String
                                .valueOf(tcpSocket.getRemoteSocketAddress());
                        tcpSocket = null;
                        parentMedia.notifyClientDisconnected(
                                new ConnectionEventArgs(info));
                    }
                    continue;
                } catch (IOException ex) {
                    tcpSocket = null;
                    parentMedia
                            .notifyError(new RuntimeException(ex.getMessage()));
                    continue;
                }
            }
        }

        if (tcpSocket != null) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    handleTCP(tcpSocket);
                } catch (IOException ex) {
                    parentMedia
                            .notifyError(new RuntimeException(ex.getMessage()));
                    continue;
                }
            }
        } else {
            DatagramPacket receivePacket =
                    new DatagramPacket(buffer, buffer.length);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    udpSocket.receive(receivePacket);
                    handleReceivedData(receivePacket.getLength(),
                            receivePacket.getSocketAddress().toString());
                } catch (IOException ex) {
                    parentMedia
                            .notifyError(new RuntimeException(ex.getMessage()));
                    continue;
                }
            }
        }
    }
}