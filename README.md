See An [Gurux](http://www.gurux.org/ "Gurux") for an overview.

Join the Gurux Community or follow [@Gurux](https://twitter.com/guruxorg "@Gurux") for project updates.

With gurux.net component you can send data easily syncronously or asyncronously using TCP or UDP connection.

Open Source GXNet media component, made by Gurux Ltd, is a part of GXMedias set of media components, which programming interfaces help you implement communication by chosen connection type. Gurux media components also support the following connection types: serial port.

For more info check out [Gurux](http://www.gurux.org/ "Gurux").

We are updating documentation on Gurux web page. 

If you have problems you can ask your questions in Gurux [Forum](http://www.gurux.org/forum).

You can get source codes from http://www.github.com/gurux or if you use Maven add this to your POM-file:

<dependency>
  <groupId>org.gurux</groupId>
  <artifactId>gurux.net</artifactId>
  <version>1.0.4</version>
</dependency>

Simple example
=========================== 
Before use you must set following settings:
* HostName
* Port
* Protocol

It is also good to add listener to listen following events.
* onError
* onReceived
* onMediaStateChange

and if in server mode following events might be important.
* onClientConnected
* onClientDisconnected                

```java

GXNet cl = new GXNet();
cl.setHostName("localhost");
cl.setPort(1000);
cl.setProtocol(NetworkType.TCP);
cl.open();

```

Data is send with send command:

```java
cl.send("Hello World!", null);
```
In default mode received data is coming as asynchronously from OnReceived event.
Event listener is added like this:
1. Ads class that you want to use to listen media events and derive class from IGXMediaListener.

```java
*/
 Media listener.
*/
public class GXNetListener implements IGXMediaListener, gurux.net.IGXNetListener
{
	/** 
        Represents the method that will handle the error event of a Gurux component.

        @param sender The source of the event.
        @param ex An Exception object that contains the event data.
    */
    @Override
    void onError(Object sender, RuntimeException ex)
    {
    }

    /** 
     Media component sends received data through this method.

     @param sender The source of the event.
     @param e Event arguments.
    */
    @Override
    void onReceived(Object sender, ReceiveEventArgs e)
    {
    
    }

    /** 
     Media component sends notification, when its state changes.

     @param sender The source of the event.    
     @param e Event arguments.
    */
    @Override
    void onMediaStateChange(Object sender, MediaStateEventArgs e)
    {
    
    }

    /** 
     Called when the Media is sending or receiving data.

     @param sender
     @param e
     @see IGXMedia.Trace Traceseealso>
    */
    @Override
    void onTrace(Object sender, TraceEventArgs e)
    {
    
    }
    
    // Summary:
    //     Represents the method that will handle the System.ComponentModel.INotifyPropertyChanged.PropertyChanged
    //     event raised when a property is changed on a component.
    //
    // Parameters:
    //   sender:
    //     The source of the event.
    //
    //   e:
    //     A System.ComponentModel.PropertyChangedEventArgs that contains the event
    //     data.
    @Override
    void onPropertyChanged(Object sender, PropertyChangedEventArgs e)
    {
		
    }
    
    /*
     * Client is made connection.
     */
    @Override
    public void onClientConnected(Object sender, gurux.net.ConnectionEventArgs e) 
    {
        System.out.println("Client Connected.");
    }

    /*
     * Client is closed connection.
     */
    @Override
    public void onClientDisconnected(Object sender, gurux.net.ConnectionEventArgs e) 
    {
        System.out.println("Client Disconnected.");
    }
}

```

Listener is registered calling addListener method.
```java
cl.addListener(this);

```

Data can be also send as syncronous if needed.

```java
synchronized (cl.getSynchronous())
{
    String reply = "";    
    ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>(byte[].class);    
    //End of Packet.
    p.setEop('\n'); 
    //How long reply is waited.   
    p.setWaitTime(1000);          
    cl.send("Hello World!", null);
    if (!cl.receive(p))
    {
        throw new RuntimeException("Failed to receive response..");
    }
}
```
