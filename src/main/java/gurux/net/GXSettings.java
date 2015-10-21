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
// This code is licensed under the GNU General Public License v2. 
// Full text may be retrieved at http://www.gnu.org/licenses/gpl-2.0.txt
//---------------------------------------------------------------------------

package gurux.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import gurux.net.enums.NetworkType;

/**
 * SMS settings dialog.
 */
class GXSettings extends javax.swing.JDialog implements ActionListener {
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Has user accept changes.
     */
    private boolean accepted;
    /**
     * Parent media component.
     */
    private GXNet target;

    /**
     * Cancel button.
     */
    private javax.swing.JButton cancelBtn;
    /**
     * IP Address label.
     */
    private javax.swing.JLabel ipAddressLbl;
    /**
     * IP Address panel includes label and text box.
     */
    private javax.swing.JPanel ipAddressPanel;
    /**
     * IP address text box.
     */
    private javax.swing.JTextField ipAddressTB;
    /**
     * OK button.
     */
    private javax.swing.JButton okBtn;
    /**
     * Port label.
     */
    private javax.swing.JLabel portLbl;
    /**
     * Port panel includes label and text box.
     */
    private javax.swing.JPanel portPanel;
    /**
     * Port text box.
     */
    private javax.swing.JTextField portTB;
    /**
     * Available protocols.
     */
    private javax.swing.JComboBox<String> protocol;
    /**
     * Protocol label.
     */
    private javax.swing.JLabel protocolLbl;
    /**
     * Protocol panel includes label and text box.
     */
    private javax.swing.JPanel protocolPanel;
    /**
     * Server combo box.
     */
    private javax.swing.JCheckBox serverCB;
    /**
     * Server panel includes server combo box.
     */
    private javax.swing.JPanel serverPanel;
    /**
     * Main panel.
     */
    private javax.swing.JPanel jPanel1;

    /**
     * Creates new form GXSettings.
     * 
     * @param parent
     *            Parent frame.
     * @param modal
     *            Is Dialog shown as modal.
     * @param comp
     *            Media component where settings are get and set.
     */
    public GXSettings(final java.awt.Frame parent, final boolean modal,
            final GXNet comp) {
        super(parent, modal);
        super.setLocationRelativeTo(parent);
        initComponents();
        target = comp;

        String[] protocols = new String[] { "TCP/IP", "UDP" };
        protocol.setModel(new DefaultComboBoxModel<String>(protocols));
        ipAddressTB.setText(target.getHostName());
        portTB.setText(String.valueOf(target.getPort()));
        this.serverCB.setSelected(target.getServer());
        this.protocol.setSelectedItem(getProtocol(target.getProtocol()));
    }

    /**
     * Has user accept changes.
     * 
     * @return True, if user has accept changes.
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Get protocol as string.
     * 
     * @param value
     *            Enumeration value of NetworkType.
     * @return String value of NetworkType.
     */
    final String getProtocol(final NetworkType value) {
        if (value == NetworkType.UDP) {
            return "UDP";
        }
        return "TCP/IP";
    }

    /**
     * Parse protocol value from string.
     * 
     * @param value
     *            Protocol string.
     * @return Enumeration value of NetworkType.
     */
    final NetworkType getProtocol(final String value) {
        if (value.equalsIgnoreCase("UDP")) {
            return NetworkType.UDP;
        }
        return NetworkType.TCP;
    }

    @Override
    public final void actionPerformed(final ActionEvent e) {
        this.dispose();
    }

    /**
     * Initialize components.
     */
    /// CHECKSTYLE:OFF
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        ipAddressPanel = new javax.swing.JPanel();
        ipAddressLbl = new javax.swing.JLabel();
        ipAddressTB = new javax.swing.JTextField();
        portPanel = new javax.swing.JPanel();
        portLbl = new javax.swing.JLabel();
        portTB = new javax.swing.JTextField();
        serverPanel = new javax.swing.JPanel();
        serverCB = new javax.swing.JCheckBox();
        protocolPanel = new javax.swing.JPanel();
        protocolLbl = new javax.swing.JLabel();
        protocol = new javax.swing.JComboBox<String>();
        okBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        ipAddressPanel.setPreferredSize(new java.awt.Dimension(298, 33));
        ipAddressLbl.setText("IP Address:");

        javax.swing.GroupLayout ipAddressPanelLayout =
                new javax.swing.GroupLayout(ipAddressPanel);
        ipAddressPanel.setLayout(ipAddressPanelLayout);
        ipAddressPanelLayout.setHorizontalGroup(ipAddressPanelLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ipAddressPanelLayout.createSequentialGroup()
                        .addContainerGap().addComponent(ipAddressLbl)
                        .addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                34, Short.MAX_VALUE)
                        .addComponent(ipAddressTB,
                                javax.swing.GroupLayout.PREFERRED_SIZE, 211,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap()));
        ipAddressPanelLayout.setVerticalGroup(ipAddressPanelLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                        ipAddressPanelLayout.createSequentialGroup()
                                .addContainerGap(
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                .addGroup(ipAddressPanelLayout
                                        .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(ipAddressLbl)
                                        .addComponent(ipAddressTB,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(367, 367, 367)));

        portPanel.setPreferredSize(new java.awt.Dimension(298, 35));

        portLbl.setText("Port:");

        javax.swing.GroupLayout portPanelLayout =
                new javax.swing.GroupLayout(portPanel);
        portPanel.setLayout(portPanelLayout);
        portPanelLayout.setHorizontalGroup(portPanelLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(portPanelLayout.createSequentialGroup()
                        .addContainerGap().addComponent(portLbl)
                        .addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(portTB,
                                javax.swing.GroupLayout.PREFERRED_SIZE, 213,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap()));
        portPanelLayout.setVerticalGroup(portPanelLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                        portPanelLayout.createSequentialGroup()
                                .addContainerGap(
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                .addGroup(portPanelLayout
                                        .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(portLbl)
                                        .addComponent(portTB,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap()));

        serverPanel.setPreferredSize(new java.awt.Dimension(298, 35));

        serverCB.setText("Server");

        javax.swing.GroupLayout serverPanelLayout =
                new javax.swing.GroupLayout(serverPanel);
        serverPanel.setLayout(serverPanelLayout);
        serverPanelLayout.setHorizontalGroup(serverPanelLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(serverPanelLayout.createSequentialGroup()
                        .addContainerGap().addComponent(serverCB)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)));
        serverPanelLayout.setVerticalGroup(serverPanelLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                        serverPanelLayout.createSequentialGroup()
                                .addContainerGap(
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                .addComponent(serverCB).addContainerGap()));

        protocolPanel.setPreferredSize(new java.awt.Dimension(218, 35));

        protocolLbl.setText("Protocol");

        protocol.setModel(
                new javax.swing.DefaultComboBoxModel<String>(new String[0]));

        javax.swing.GroupLayout protocolPanelLayout =
                new javax.swing.GroupLayout(protocolPanel);
        protocolPanel.setLayout(protocolPanelLayout);
        protocolPanelLayout.setHorizontalGroup(protocolPanelLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(protocolPanelLayout.createSequentialGroup()
                        .addContainerGap().addComponent(protocolLbl)
                        .addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(protocol,
                                javax.swing.GroupLayout.PREFERRED_SIZE, 208,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap()));
        protocolPanelLayout.setVerticalGroup(protocolPanelLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                        protocolPanelLayout.createSequentialGroup()
                                .addContainerGap(
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                .addGroup(protocolPanelLayout
                                        .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(protocolLbl)
                                        .addComponent(protocol,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap()));

        javax.swing.GroupLayout jPanel1Layout =
                new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(ipAddressPanel,
                        javax.swing.GroupLayout.DEFAULT_SIZE, 321,
                        Short.MAX_VALUE)
                .addComponent(portPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                        321, Short.MAX_VALUE)
                .addComponent(serverPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                        321, Short.MAX_VALUE)
                .addComponent(protocolPanel,
                        javax.swing.GroupLayout.DEFAULT_SIZE, 321,
                        Short.MAX_VALUE));
        jPanel1Layout.setVerticalGroup(jPanel1Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                        jPanel1Layout.createSequentialGroup()
                                .addComponent(serverPanel,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ipAddressPanel,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        41,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(portPanel,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        42,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(protocolPanel,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)));

        okBtn.setText("OK");
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public final void
                    actionPerformed(final java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        cancelBtn.setText("Cancel");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public final void
                    actionPerformed(final java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout =
                new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 11, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout
                        .createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(okBtn)
                        .addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelBtn).addContainerGap()));
        layout.setVerticalGroup(layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout
                                .createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cancelBtn).addComponent(okBtn))
                        .addContainerGap()));

        pack();
    }
    // CHECKSTYLE:ON

    @Override
    protected JRootPane createRootPane() {
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JRootPane rootPane = new JRootPane();
        rootPane.registerKeyboardAction(this, stroke,
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }

    /**
     * Accept changes.
     * 
     * @param evt
     *            Action events.
     */
    private void okBtnActionPerformed(final ActionEvent evt) {
        try {
            target.setHostName(ipAddressTB.getText());
            target.setPort(Integer.parseInt(portTB.getText()));
            target.setServer(this.serverCB.isSelected());
            target.setProtocol(
                    getProtocol(this.protocol.getSelectedItem().toString()));
            accepted = true;
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    /**
     * Discard changes.
     * 
     * @param evt
     *            Action event.
     */
    private void cancelBtnActionPerformed(final ActionEvent evt) {
        this.dispose();
    }
}
