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

import gurux.common.ReceiveEventArgs;
import gurux.common.TraceLevel;
import gurux.common.TraceTypes;
import java.io.*;
import java.lang.reflect.Array;
import java.net.*;

class ReceiveThread extends Thread
{
    
    ServerSocket serverSocket;
    private GXNet m_Parent;
    Socket m_tcpSocket;    
    DatagramSocket udpSocket;
    byte[] Buffer = null;
    int BufferPosition = 0;
    Object Eop = null;

    public ReceiveThread(GXNet parent, java.io.Closeable socket)
    {        
        super("GXNet " + socket.toString());
        m_Parent = parent;       
        Eop = m_Parent.getEop();
        if (parent.getProtocol() == NetworkType.TCP)
        {
            Buffer = new byte[1024];
            if (parent.getServer())  
            {
                serverSocket = (ServerSocket)socket;                
            }
            else
            {
                m_tcpSocket = (Socket)socket;                
            }
        }
        else
        {
            udpSocket = (DatagramSocket) socket;            
        }
        BufferPosition = 0;
    }
    
    private void handleReceivedData(int len, String info)
    {
        if (len == 0)
        {
            return;
        }
        m_Parent.m_BytesReceived += len;
        int totalCount = 0;
        if (m_Parent.getIsSynchronous())
        {
            gurux.common.TraceEventArgs arg = null;
            synchronized (m_Parent.m_syncBase.m_ReceivedSync)
            {                
                m_Parent.m_syncBase.appendData(Buffer, BufferPosition, len);                
                if (Eop != null) //Search Eop if given.
                {                    
                    if (Eop instanceof Array)
                    {                                
                        for(Object eop : (Object[]) Eop)
                        {
                            totalCount = GXSynchronousMediaBase.indexOf(Buffer, 
                                GXSynchronousMediaBase.getAsByteArray(eop), 
                                BufferPosition - len, BufferPosition);
                            if (totalCount != -1)
                            {
                                break;
                            }
                        }
                    }
                    else
                    {
                        totalCount = GXSynchronousMediaBase.indexOf(Buffer, 
                                GXSynchronousMediaBase.getAsByteArray(Eop), 
                                BufferPosition - len, BufferPosition);
                    }
                }
                if (totalCount != -1)
                {
                    if (m_Parent.getTrace() == TraceLevel.VERBOSE)
                    {
                        arg = new gurux.common.TraceEventArgs(TraceTypes.RECEIVED, Buffer, 0, totalCount + 1);
                    }
                    m_Parent.m_syncBase.m_ReceivedEvent.set();
                }
            }
            if (arg != null)
            {
                m_Parent.notifyTrace(arg);
            }
        }
        else
        {
            m_Parent.m_syncBase.m_ReceivedSize = 0;
            if (Eop != null) //Search Eop if given.
            {          
                if (Eop instanceof Array)
                {
                    for(Object eop : (Object[]) Eop)
                    {
                        
                        totalCount = GXSynchronousMediaBase.indexOf(Buffer, 
                                GXSynchronousMediaBase.getAsByteArray(eop), 
                                BufferPosition - len, BufferPosition);
                        if (totalCount != -1)
                        {
                            break;
                        }
                    }
                }
                else
                {                    
                    totalCount = GXSynchronousMediaBase.indexOf(Buffer, 
                            GXSynchronousMediaBase.getAsByteArray(Eop), 
                            BufferPosition - len, BufferPosition);
                }
                if (totalCount != -1)
                {
                    byte[] data = new byte[len];
                    System.arraycopy(Buffer, 0, data, 0, totalCount);
                    System.arraycopy(Buffer, 0, Buffer, totalCount, BufferPosition - totalCount);
                    BufferPosition = 0;
                    ReceiveEventArgs e = new ReceiveEventArgs(data, info);
                    m_Parent.notifyReceived(e);                
                    if (m_Parent.getTrace() == TraceLevel.VERBOSE)
                    {
                        m_Parent.notifyTrace(new gurux.common.TraceEventArgs(TraceTypes.RECEIVED, Buffer, 0, len));                    
                    }                    
                }
            }
            else
            {
                byte[] data = new byte[len];
                System.arraycopy(Buffer, 0, data, 0, len);                
                if (m_Parent.getTrace() == TraceLevel.VERBOSE)
                {
                    m_Parent.notifyTrace(new gurux.common.TraceEventArgs(TraceTypes.RECEIVED, data));                    
                }
                ReceiveEventArgs e = new ReceiveEventArgs(data, info);
                m_Parent.notifyReceived(e);                
            }
        }
    }
    
    private void handleTCP(Socket socket) throws IOException
    {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        int count = in.read(Buffer, BufferPosition, 1);
        if (count == -1)
        {
            throw new SocketException();
        }
        while (in.available() != 0)
        {
            int cnt = in.available();
            if (BufferPosition + cnt > 1024)
            {
                cnt = 1024 - BufferPosition - count;
            }
            count += in.read(Buffer, BufferPosition + count, cnt);                    
            if (count == 1024)
            {
                handleReceivedData(count, socket.getRemoteSocketAddress().toString());
                count = BufferPosition = 0;
            }
        }
        handleReceivedData(count, socket.getRemoteSocketAddress().toString());
    }
    
    /** 
     Receive data from the server using the established socket connection

     @return The data received from the server
    */
    @Override
    public final void run()
    {
        if (serverSocket != null)
        {
            while(!Thread.currentThread().isInterrupted())
            {
                try
                {                    
                    m_tcpSocket = serverSocket.accept();                    
                    String info = m_tcpSocket.getRemoteSocketAddress().toString();                    
                    m_Parent.notifyClientConnected(new ConnectionEventArgs(info));                    
                    while(!Thread.currentThread().isInterrupted())
                    {
                        handleTCP(m_tcpSocket);
                    }
                }
                //Client closed the connection.
                catch(SocketException ex)
                {              
                    String info = m_tcpSocket.getRemoteSocketAddress().toString();
                    m_tcpSocket = null;
                    m_Parent.notifyClientDisconnected(new ConnectionEventArgs(info));
                    continue;
                }
                catch(IOException ex)
                {              
                    m_tcpSocket = null;
                    m_Parent.notifyError(new RuntimeException(ex.getMessage()));
                    continue;
                }
            }
        }
        
        if (m_tcpSocket != null)
        {
            while(!Thread.currentThread().isInterrupted())
            {
                try
                {   
                    handleTCP(m_tcpSocket);
                }
                catch(IOException ex)
                {                
                    m_Parent.notifyError(new RuntimeException(ex.getMessage()));
                    continue;
                }
            }
        }
        else
        {
            DatagramPacket receivePacket = new DatagramPacket(Buffer, Buffer.length);
            while(!Thread.currentThread().isInterrupted())
            {
                try
                { 
                    udpSocket.receive(receivePacket);
                    handleReceivedData(receivePacket.getLength(), 
                            receivePacket.getSocketAddress().toString());
                }
                catch(IOException ex)
                {                
                    m_Parent.notifyError(new RuntimeException(ex.getMessage()));
                    continue;
                }
            }
        }
    }   
}