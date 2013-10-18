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

import gurux.common.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.*;

/** 
 The GXNet component determines methods that make the communication possible using Internet. 

*/
public class GXNet implements IGXMedia
{    
    private NetworkType m_Protocol = NetworkType.TCP;
    private String m_HostName;
    private int m_Port;
    boolean m_Server;
    GXSynchronousMediaBase m_syncBase;
    java.io.Closeable m_Socket = null;
    public long m_BytesReceived = 0;
    private long m_BytesSend = 0;
    private int m_Synchronous = 0;
    private TraceLevel m_Trace = TraceLevel.OFF;
    private boolean privateUseIPv6;
    private int privateMaxClientCount;
    private Object privateEop;        
    private int ConfigurableSettings;
    private List<IGXMediaListener> MediaListeners = new ArrayList<IGXMediaListener>();
    private List<IGXNetListener> NetListeners = new ArrayList<IGXNetListener>();
    ReceiveThread Receiver = null;
    /** 
     Constructor.
    */
    public GXNet()
    {
        m_syncBase = new GXSynchronousMediaBase(1024);
        setConfigurableSettings(AvailableMediaSettings.All.getValue());
        setProtocol(NetworkType.TCP);        
    }

    /** 
     Client Constructor.

     @param protocol Used protocol.
     @param hostName Host name.
     @param port Client port.
    */
    public GXNet(NetworkType protocol, String hostName, int port)
    {
        this();
        setProtocol(protocol);
        setHostName(hostName);
        setPort(port);
    }
    
    /** 
        Constructor used when server is started.

        @param protocol Used protocol.
        @param port Server port.
*/
    public GXNet(NetworkType protocol, int port)
    {
        this();
        this.setServer(true);
        setProtocol(protocol);
        setPort(port);
    }

    /** 
     Destructor.
    */
    @Override
    @SuppressWarnings("FinalizeDeclaration")    
    protected void finalize() throws Throwable
    {
        super.finalize();
        if (isOpen())
        {
            close();
        }
    }

    /** 
     Is IPv6 used. Default is False (IPv4).
    */
    public final boolean getUseIPv6()
    {
        return privateUseIPv6;
    }
    public final void setUseIPv6(boolean value)
    {
        privateUseIPv6 = value;
    }

    /** 
     What level of tracing is used.
    */
    @Override
    public final TraceLevel getTrace()
    {
        return m_Trace;
    }
    @Override
    public final void setTrace(TraceLevel value)
    {
        m_Trace = m_syncBase.Trace = value;
    }
    private void NotifyPropertyChanged(String info)
    {
        for (IGXMediaListener listener : MediaListeners) 
        {
            listener.onPropertyChanged(this, new PropertyChangedEventArgs(info));
        }
    }
    
    
    void notifyClientConnected(ConnectionEventArgs e)
    {
        for (IGXNetListener listener : NetListeners) 
        {
            listener.onClientConnected(this, e);
        }
        if (m_Trace.ordinal() >= TraceLevel.INFO.ordinal())
        {
            for (IGXMediaListener listener : MediaListeners) 
            {
                listener.onTrace(this, new TraceEventArgs(TraceTypes.INFO, 
                        "Client connected."));
            }            
        }

    }
    void notifyClientDisconnected(ConnectionEventArgs e)
    {
        for (IGXNetListener listener : NetListeners) 
        {
            listener.onClientDisconnected(this, e);
        }
        if (m_Trace.ordinal() >= TraceLevel.INFO.ordinal())
        {
            for (IGXMediaListener listener : MediaListeners) 
            {
                listener.onTrace(this, new TraceEventArgs(TraceTypes.INFO, 
                        "Client disconnected."));
            }            
        }

    }


    void notifyError(RuntimeException ex)
    {
        for (IGXMediaListener listener : MediaListeners) 
        {
            listener.onError(this, ex);
            if (m_Trace.ordinal() >= TraceLevel.ERROR.ordinal())
            {
                listener.onTrace(this, new TraceEventArgs(TraceTypes.ERROR, ex));
            }
        }
    }
    
    void notifyReceived(ReceiveEventArgs e)
    {
        for (IGXMediaListener listener : MediaListeners) 
        {
            listener.onReceived(this, e);
        }
    }
    
    void notifyTrace(TraceEventArgs e)
    {
        for (IGXMediaListener listener : MediaListeners) 
        {                
            listener.onTrace(this, e);
        }
    }
       
    /** <inheritdoc cref="IGXMedia.ConfigurableSettings"/>
    */
    @Override
    public final int getConfigurableSettings()
    {            
        return ConfigurableSettings;
    }
    @Override
    public final void setConfigurableSettings(int value)
    {
        this.ConfigurableSettings = value;
    }

    /**    
     Displays the copyright of the control, user license, and version information, in a dialog box. 
    */
    public final void AboutBox()
    {
        throw new UnsupportedOperationException();
    }

    /** 
     Sends data asynchronously. <br/>
     No reply from the receiver, whether or not the operation was successful, is expected.

     @param data Data to send to the device.
     @param receiver IP address of the receiver (optional).
     Reply data is received through OnReceived event.<br/>     		
     @see OnReceived OnReceived
     @see Open Open
     @see Close Close 
    */
    @Override
    public final void send(Object data, String receiver) throws Exception
    {
        if (m_Socket == null)
        {
            throw new RuntimeException("Invalid connection.");
        }
        if (m_Trace == TraceLevel.VERBOSE)
        {
            notifyTrace(new TraceEventArgs(TraceTypes.SENT, data));
        }
        //Reset last position if Eop is used.
        synchronized (m_syncBase.m_ReceivedSync)
        {
            m_syncBase.m_LastPosition = 0;
        }
        byte[] buff = GXSynchronousMediaBase.getAsByteArray(data);
        if (buff == null)
        {
            throw new IllegalArgumentException("Data send failed. Invalid data.");
        }
        if (this.getServer())  
        {
            if (getProtocol() == NetworkType.TCP)
            {                
                Receiver.m_tcpSocket.getOutputStream().write(buff);
            }
            else if (getProtocol() == NetworkType.UDP)
            {
                InetAddress addr = InetAddress.getByName(getHostName());
                DatagramPacket p = new DatagramPacket(buff, buff.length, 
                        addr, getPort());
               ((DatagramSocket)m_Socket).send(p);
            }
        }
        else
        {
            if (getProtocol() == NetworkType.TCP)
            {
                ((Socket)m_Socket).getOutputStream().write(buff);
            }
            else if (getProtocol() == NetworkType.UDP)
            {
                InetAddress addr = InetAddress.getByName(getHostName());
                DatagramPacket p = new DatagramPacket(buff, buff.length, 
                        addr, getPort());
               ((DatagramSocket)m_Socket).send(p);
            }
        }
        this.m_BytesSend += buff.length;
    }

    private void NotifyMediaStateChange(MediaState state)
    {
        for (IGXMediaListener listener : MediaListeners) 
        {                
            if (m_Trace.ordinal() >= TraceLevel.ERROR.ordinal())
            {
                listener.onTrace(this, new TraceEventArgs(TraceTypes.INFO, state));
            }
            listener.onMediaStateChange(this, new MediaStateEventArgs(state));
        }
    }
  
    /** 
     Opens the connection.
     Protocol, Port and HostName must be set, before calling the Open method.

     <example>
     <code lang="vbscript">
     'This example shows how to start client connection.
     'Set Protocol
     GXNet1.Protocol = GX_NW_UDP  
     'Set client port
     GXNet1.Port = 1234
     'Set client name
     GXNet1.HostName = "localhost"
     'Make connection
     GXNet1.Open
     'Send data
     dim dataToSend
     dataToSend = "Hello"
     GXNet1.Send dataToSend, ""
     'The response is received after this through the IGXNetEvents.OnReceived method.
     </code>
     </example>
     @see Port Port
     @see HostName HostName
     @see Protocol Protocol
     @see Server Server
     @see Close Close
    */
    @Override
    public final void open() throws Exception
    {
        close();
        try
        {
            synchronized (m_syncBase.m_ReceivedSync)
            {
                m_syncBase.m_LastPosition = 0;
            }
            NotifyMediaStateChange(MediaState.OPENING);
            if (this.getServer())  
            {
                if (getProtocol() == NetworkType.TCP)
                {
                    m_Socket = (Closeable) new ServerSocket(getPort());
                }
                else if (getProtocol() == NetworkType.UDP)
                {
                   m_Socket = (Closeable) new DatagramSocket();
                }
                if (m_Trace.ordinal() >= TraceLevel.INFO.ordinal())
                {
                    notifyTrace(new TraceEventArgs(TraceTypes.INFO, 
                            "Server settings: Protocol: " + this.getProtocol().toString() + 
                            " Port: " + (new Integer(getPort())).toString()));
                }
            }
            else
            {           
                // Create a stream-based, TCP socket using the InterNetwork Address Family. 
                if (getProtocol() == NetworkType.TCP)
                {
                    m_Socket = (Closeable) new Socket(getHostName(), getPort());
                }
                else if (getProtocol() == NetworkType.UDP)
                {
                   m_Socket = (Closeable) new DatagramSocket();
                }
                else
                {
                    throw new IllegalArgumentException("Protocol");
                }
                if (m_Trace.ordinal() >= TraceLevel.INFO.ordinal())
                {
                    notifyTrace(new TraceEventArgs(TraceTypes.INFO, 
                            "Client settings: Protocol: " + 
                            this.getProtocol().toString() + " Host: " + 
                            getHostName() + " Port: " + (new Integer(getPort())).toString()));
                }
            }
            Receiver = new ReceiveThread(this, m_Socket);
            Receiver.start();
            NotifyMediaStateChange(MediaState.OPEN);
        }
        catch (IOException e)
        {
            close();
            throw e;
        }
    }

    /** 
     * <inheritdoc cref="IGXMedia.Close"/>        
    */
    @Override       
    public final void close()
    {
        if (m_Socket != null)
        {         
            if (Receiver != null)
            {
                Receiver.interrupt();                    
                Receiver = null;
            }            
            try
            {
                NotifyMediaStateChange(MediaState.CLOSING);
            }
            catch (RuntimeException ex)
            {
                notifyError(ex);
                throw ex;
            }
            finally
            {
                try
                {
                    m_Socket.close();
                }
                catch (java.lang.Exception e)
                {
                    //Ignore all errors on close.                    
                }
                m_Socket = null;
                NotifyMediaStateChange(MediaState.CLOSED);
                m_BytesSend = m_BytesReceived = 0;
                m_syncBase.m_ReceivedSize = 0;
            }
        }
    }

    /** <inheritdoc cref="IGXMedia.IsOpen"/>
     <seealso char="Connect">Open
     <seealso char="StartServer">StartServer
     <seealso char="Close">Close
    */
    @Override
    public final boolean isOpen()
    {
        return m_Socket != null;
    }

    /** 
     Retrieves or sets the protocol.


     Defaut protocol is UDP.

     <value>
     Protocol
     </value>
    */
    public final NetworkType getProtocol()
    {
        return m_Protocol;
    }

    public final void setProtocol(NetworkType value)
    {
        if (m_Protocol != value)
        {
            m_Protocol = value;
            NotifyPropertyChanged("Protocol");
        }
    }

    /** 
     Retrieves or sets the name or IP address of the host.

     <value>
     The name of the host.
     </value>
     @see Open Open
     @see Port Port
     @see Protocol Protocol
    */
    public final String getHostName()
    {
        return m_HostName;
    }
    public final void setHostName(String value)
    {
        if (m_HostName == null || !m_HostName.equals(value))
        {
            m_HostName = value;
            NotifyPropertyChanged("HostName");
        }
    }

    /** 
     Retrieves or sets the host or server port number.

     <value>
     Host or server port number.
     </value>
     @see Open Open
     @see HostName HostName  	
     @see Protocol Protocol
    */
    public final int getPort()
    {
        return m_Port;
    }
    public final void setPort(int value)
    {
        if (m_Port != value)
        {
            m_Port = value;
            NotifyPropertyChanged("Port");
        }
    }   

    /** 
    Determines if the component is in server, or in client, mode.

    <value>
    True, if component is a server. False, if component is a client.
    </value>
    @see Open Open 	
*/
    public final boolean getServer()
    {
        return m_Server;
    }
    public final void setServer(boolean value)
    {
        if (m_Server != value)
        {
            m_Server = value;
            NotifyPropertyChanged("Server");
        }
    }
    
    @Override
    public final <T> boolean receive(ReceiveParameters<T> args)
    {
        return m_syncBase.receive(args);
    }

    /** 
     Sent byte count.

     @see BytesReceived BytesReceived
     @see ResetByteCounters ResetByteCounters
    */
    @Override
    public final long getBytesSent()
    {
        return m_BytesSend;
    }

    /** 
     Received byte count.

     @see BytesSent BytesSent
     @see ResetByteCounters ResetByteCounters
    */
    @Override
    public final long getBytesReceived()
    {
        return m_BytesReceived;
    }

    /** 
     Resets BytesReceived and BytesSent counters.

     @see BytesSent BytesSent
     @see BytesReceived BytesReceived
    */
    @Override
    public final void resetByteCounters()
    {
        m_BytesSend = m_BytesReceived = 0;
    }

    /** 
     Retrieves or sets maximum count of connected clients.
    */	
    public final int getMaxClientCount()
    {
        return privateMaxClientCount;
    }
    public final void setMaxClientCount(int value)
    {
        privateMaxClientCount = value;
    }

    /** 
     Media settings as a XML string.
    */
    @Override
    public final String getSettings()
    {        
        return null;
        //TODO:
    }
    
    @Override
    public final void setSettings(String value)
    {   
        //TODO:
    }
    
    @Override
    public final void copy(Object target)
    {
        GXNet tmp = (GXNet)target;
        setPort(tmp.getPort());
        setHostName(tmp.getHostName());
        setProtocol(tmp.getProtocol());
    }

    @Override
    public String getName()
    {
        String tmp;
        tmp = getHostName() + " " + getPort();
        if (getProtocol() == NetworkType.UDP)
        {
            tmp += "UDP";
        }
        else
        {
            tmp += "TCP/IP";
        }
        return tmp;
    }

    @Override
    public String getMediaType()
    {
        return "Net";
    }

    /** <inheritdoc cref="IGXMedia.Synchronous"/>
    */
    @Override
    public final Object getSynchronous()
    {
        synchronized (this)
        {
            int[] tmp = new int[]{m_Synchronous};
            GXSync obj = new GXSync(tmp);
            m_Synchronous = tmp[0];
            return obj;
        }
    }

    /** <inheritdoc cref="IGXMedia.IsSynchronous"/>
    */
    @Override
    public final boolean getIsSynchronous()
    {
        synchronized (this)
        {
            return m_Synchronous != 0;
        }
    }

    /** <inheritdoc cref="IGXMedia.ResetSynchronousBuffer"/>
    */
    @Override
    public final void resetSynchronousBuffer()
    {
        synchronized (m_syncBase.m_ReceivedSync)
        {
            m_syncBase.m_ReceivedSize = 0;
        }
    }

    /** <inheritdoc cref="IGXMedia.Validate"/>
    */
    @Override
    public final void validate()
    {
        if (getPort() == 0)
        {
            throw new RuntimeException("Invalid port name.");
        }
        if (getHostName() == null || "".equals(getHostName()))
        {
            throw new RuntimeException("Invalid host name.");
        }
    }

    /** <inheritdoc cref="IGXMedia.Eop"/>
    */
    @Override
    public final Object getEop()
    {
        return privateEop;
    }
    @Override
    public final void setEop(Object value)
    {
        privateEop = value;
    }

    @Override
    public void addListener(IGXMediaListener listener) 
    {        
        MediaListeners.add(listener);
        if (listener instanceof IGXNetListener)
        {
            NetListeners.add((IGXNetListener) listener);
        }
    }

    @Override
    public void removeListener(IGXMediaListener listener) 
    {
        MediaListeners.remove(listener);
        if (listener instanceof IGXNetListener)
        {
            NetListeners.remove((IGXNetListener) listener);
        }
    }          
}