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

/**
 * Argument class for IGXMedia connection and disconnection events.
 */
public class ConnectionEventArgs {
    /**
     * Connection info.
     */
    private String info;
    /**
     * Is server accepting client connection.
     */
    private boolean accept;

    /**
     * Constructor.
     */
    public ConnectionEventArgs() {
        setAccept(true);
    }

    /**
     * Constructor.
     * 
     * @param information
     *            Client's IP address and port number.
     */
    public ConnectionEventArgs(final String information) {
        setAccept(true);
        setInfo(information);
    }

    /**
     * Get connection TCP/IP and port number information.
     * 
     * @return Client's IP address and port number.
     */
    public final String getInfo() {
        return info;
    }

    /**
     * Set connection info.
     * 
     * @param value
     *            Client's IP address and port number.
     */
    final void setInfo(final String value) {
        info = value;
    }

    /**
     * Get is server accepting client connection.
     * 
     * @return Is connection accepted.
     */
    public final boolean getAccept() {
        return accept;
    }

    /**
     * Set is server accepting client connection.
     * 
     * @param value
     *            Is connection accepted.
     */
    public final void setAccept(final boolean value) {
        accept = value;
    }
}
