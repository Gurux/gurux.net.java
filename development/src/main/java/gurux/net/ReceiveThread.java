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
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

import gurux.common.GXSynchronousMediaBase;
import gurux.common.ReceiveEventArgs;
import gurux.common.TraceEventArgs;
import gurux.common.enums.TraceLevel;
import gurux.common.enums.TraceTypes;

/**
 * Receive thread listens socket and sends received data to the listeners.
 * 
 * @author Gurux Ltd.
 *
 */
class ReceiveThread extends Thread {

    /**
     * Socket.
     */
    private final Closeable socket;
    /**
     * Parent component where notifies are send.
     */
    private GXNet parentMedia;
    /**
     * Buffer where received data is saved.
     */
    private byte[] buffer = null;

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
     * @param s
     *            Socket to listen.
     */
    ReceiveThread(final GXNet parent, final Closeable s) {
        super("GXNet " + s.toString());
        parentMedia = parent;
        socket = s;
        buffer = new byte[RECEIVE_BUFFER_SIZE];
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
     * Wait until thread is started.
     * 
     * @return true, if thread started.
     */
    public boolean waitUntilRun() {
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
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
                parentMedia.getSyncBase().appendData(buffer, 0, length);
                // Search end of packet if it is given.
                if (eop != null) {
                    if (eop instanceof Array) {
                        for (Object it : (Object[]) eop) {
                            totalCount = GXSynchronousMediaBase.indexOf(buffer,
                                    GXSynchronousMediaBase.getAsByteArray(it),
                                    0, length);
                            if (totalCount != -1) {
                                break;
                            }
                        }
                    } else {
                        totalCount = GXSynchronousMediaBase.indexOf(buffer,
                                GXSynchronousMediaBase.getAsByteArray(eop), 0,
                                length);
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

    /**
     * Read data from TCP/IP stream.
     * 
     * @param s
     *            socket to read.
     * @throws IOException
     *             occurred exception.
     */
    private void handleTCP(final Socket s) throws IOException {
        DataInputStream in = new DataInputStream(s.getInputStream());
        int count = in.read(buffer, 0, 1);
        if (count == -1) {
            throw new SocketException();
        }
        while (in.available() != 0) {
            int cnt = in.available();
            if (count + cnt > buffer.length) {
                cnt = buffer.length - count;
            }
            count += in.read(buffer, count, cnt);
            // If buffer is full.
            if (count == buffer.length) {
                handleReceivedData(count,
                        s.getRemoteSocketAddress().toString());
                count = 0;
            }
        }
        handleReceivedData(count, s.getRemoteSocketAddress().toString());
    }

    /**
     * Receive data from the server using the established socket connection.
     * 
     * @return The data received from the server
     */
    @Override
    public final void run() {
        // Notify caller that thread is started.
        synchronized (this) {
            notifyAll();
        }
        // If TCP/IP
        if (socket instanceof Socket) {
            Socket s = (Socket) socket;
            String info = String.valueOf(s.getRemoteSocketAddress());
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    handleTCP(s);
                } catch (java.net.SocketException e) {
                    // Client has close the connection.
                    parentMedia.getTcpIpClients().remove(s);
                    parentMedia.notifyClientDisconnected(
                            new ConnectionEventArgs(info));
                    try {
                        s.close();
                    } catch (IOException e1) {
                        // It's OK if this fails.
                    }
                    break;
                } catch (IOException e) {
                    parentMedia
                            .notifyError(new RuntimeException(e.getMessage()));
                    break;
                }
            }
        } else {
            // If UDP
            DatagramPacket receivePacket =
                    new DatagramPacket(buffer, buffer.length);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ((DatagramSocket) socket).receive(receivePacket);
                    handleReceivedData(receivePacket.getLength(),
                            receivePacket.getSocketAddress().toString());
                } catch (java.net.SocketException e) {
                    break;
                } catch (IOException ex) {
                    parentMedia
                            .notifyError(new RuntimeException(ex.getMessage()));
                    continue;
                }
            }
        }
    }
}