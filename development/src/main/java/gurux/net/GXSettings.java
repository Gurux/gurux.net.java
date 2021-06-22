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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;

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

    javax.swing.JLabel waittimeLbl = new javax.swing.JLabel();

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
    private JTextField WaittimeTB;

    /**
     * Creates new form GXSettings.
     * 
     * @param parent
     *            Parent frame.
     * @param modal
     *            Is Dialog shown as modal.
     * @param comp
     *            Media component where settings are get and set.
     * @param locale
     *            Used locale.
     */
    GXSettings(final java.awt.Frame parent, final boolean modal,
            final GXNet comp, final Locale locale) {
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
        WaittimeTB.setText(String.valueOf(target.getConnectionWaitTime()));

        // Localize strings.
        ResourceBundle bundle = ResourceBundle.getBundle("resources", locale);
        this.setTitle(bundle.getString("SettingsTxt"));
        ipAddressLbl.setText(bundle.getString("HostNameTxt"));
        portLbl.setText(bundle.getString("PortTxt"));
        serverCB.setText(bundle.getString("ServerTxt"));
        protocolLbl.setText(bundle.getString("ProtocolTxt"));
        // TODO: waittimeLbl.setText(bundle.getString("WaittimeTxt"));
        okBtn.setText(bundle.getString("OK"));
        cancelBtn.setText(bundle.getString("Cancel"));
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
                                .addGroup(portPanelLayout.createParallelGroup(
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

        JPanel WaittimePanel = new JPanel();
        WaittimePanel.setPreferredSize(new Dimension(298, 35));

        waittimeLbl.setText("Wait time:");

        WaittimeTB = new JTextField();
        WaittimeTB.setText("0");
        GroupLayout gl_WaittimePanel = new GroupLayout(WaittimePanel);
        gl_WaittimePanel.setHorizontalGroup(gl_WaittimePanel
                .createParallelGroup(Alignment.LEADING)
                .addGap(0, 341, Short.MAX_VALUE)
                .addGroup(gl_WaittimePanel.createSequentialGroup()
                        .addContainerGap().addComponent(waittimeLbl)
                        .addPreferredGap(ComponentPlacement.RELATED, 64,
                                Short.MAX_VALUE)
                        .addComponent(WaittimeTB, GroupLayout.PREFERRED_SIZE,
                                213, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap()));
        gl_WaittimePanel.setVerticalGroup(gl_WaittimePanel
                .createParallelGroup(Alignment.TRAILING)
                .addGap(0, 42, Short.MAX_VALUE)
                .addGroup(gl_WaittimePanel.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addGroup(gl_WaittimePanel
                                .createParallelGroup(Alignment.BASELINE)
                                .addComponent(waittimeLbl).addComponent(
                                        WaittimeTB, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE))
                        .addContainerGap()));
        WaittimePanel.setLayout(gl_WaittimePanel);

        javax.swing.GroupLayout jPanel1Layout =
                new javax.swing.GroupLayout(jPanel1);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(ipAddressPanel, GroupLayout.DEFAULT_SIZE,
                                341, Short.MAX_VALUE)
                        .addComponent(portPanel, GroupLayout.DEFAULT_SIZE, 341,
                                Short.MAX_VALUE)
                        .addComponent(serverPanel, GroupLayout.DEFAULT_SIZE,
                                341, Short.MAX_VALUE)
                        .addComponent(protocolPanel, GroupLayout.DEFAULT_SIZE,
                                341, Short.MAX_VALUE)
                        .addComponent(WaittimePanel, GroupLayout.DEFAULT_SIZE,
                                351, Short.MAX_VALUE));
        jPanel1Layout.setVerticalGroup(jPanel1Layout
                .createParallelGroup(Alignment.TRAILING)
                .addGroup(Alignment.LEADING, jPanel1Layout
                        .createSequentialGroup()
                        .addComponent(serverPanel, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(ipAddressPanel,
                                GroupLayout.PREFERRED_SIZE, 41,
                                GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(portPanel, GroupLayout.PREFERRED_SIZE, 42,
                                GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(protocolPanel, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(WaittimePanel, GroupLayout.PREFERRED_SIZE,
                                42, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(35, Short.MAX_VALUE)));
        jPanel1.setLayout(jPanel1Layout);

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
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap(220, Short.MAX_VALUE)
                        .addComponent(okBtn)
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addComponent(cancelBtn).addContainerGap())
                .addGroup(Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(11, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 234,
                                GroupLayout.PREFERRED_SIZE)
                        .addGap(26)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(cancelBtn).addComponent(okBtn))
                        .addContainerGap()));
        getContentPane().setLayout(layout);

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
            target.setConnectionWaitTime(
                    Integer.parseInt(WaittimeTB.getText()));
            target.validate();
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
