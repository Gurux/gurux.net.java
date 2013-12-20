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

/**
 * SMS settings dialog.
 */
class GXSettings extends javax.swing.JDialog implements ActionListener {
    boolean Accepted;
    GXNet Target;
    /**
     * Creates new form GXSettings
     */
    @SuppressWarnings("unchecked")
    public GXSettings(java.awt.Frame parent, boolean modal, GXNet comp) {
        super(parent, modal);        
        super.setLocationRelativeTo(parent); 
        initComponents();                 
        Target = comp;
        
        String[] protocols = new String[]{"TCP/IP", "UDP"};
        Protocol.setModel(new DefaultComboBoxModel(protocols));                
        IPAddressTB.setText(Target.getHostName());
        PortTB.setText(String.valueOf(Target.getPort()));
        this.ServerCB.setSelected(Target.getServer());
        this.Protocol.setSelectedItem(getProtocol(Target.getProtocol()));              
    }
    
    final String getProtocol(NetworkType protocol)
    {
        if (protocol == NetworkType.UDP)
        {
            return "UDP";
        }
        return "TCP/IP";
    }
    
    final NetworkType getProtocol(String protocol)
    {
        if (protocol.equalsIgnoreCase("UDP"))
        {
            return NetworkType.UDP;
        }
        return NetworkType.TCP;
    }
    
    /*
     * If user press ESC.
     */
    @Override
    public void actionPerformed(ActionEvent e) {        
        this.dispose();
    }
        

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        IPAddressPanel = new javax.swing.JPanel();
        IPAddressLbl = new javax.swing.JLabel();
        IPAddressTB = new javax.swing.JTextField();
        PortPanel = new javax.swing.JPanel();
        PortLbl = new javax.swing.JLabel();
        PortTB = new javax.swing.JTextField();
        ServerPanel = new javax.swing.JPanel();
        ServerCB = new javax.swing.JCheckBox();
        ProtocolPanel = new javax.swing.JPanel();
        ProtocolLbl = new javax.swing.JLabel();
        Protocol = new javax.swing.JComboBox();
        OKBtn = new javax.swing.JButton();
        CancelBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        IPAddressPanel.setPreferredSize(new java.awt.Dimension(298, 33));

        IPAddressLbl.setText("IP Address:");

        javax.swing.GroupLayout IPAddressPanelLayout = new javax.swing.GroupLayout(IPAddressPanel);
        IPAddressPanel.setLayout(IPAddressPanelLayout);
        IPAddressPanelLayout.setHorizontalGroup(
            IPAddressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(IPAddressPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(IPAddressLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(IPAddressTB, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        IPAddressPanelLayout.setVerticalGroup(
            IPAddressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, IPAddressPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(IPAddressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IPAddressLbl)
                    .addComponent(IPAddressTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(367, 367, 367))
        );

        PortPanel.setPreferredSize(new java.awt.Dimension(298, 35));

        PortLbl.setText("Port:");

        javax.swing.GroupLayout PortPanelLayout = new javax.swing.GroupLayout(PortPanel);
        PortPanel.setLayout(PortPanelLayout);
        PortPanelLayout.setHorizontalGroup(
            PortPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PortPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PortLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(PortTB, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PortPanelLayout.setVerticalGroup(
            PortPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PortPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PortPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PortLbl)
                    .addComponent(PortTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        ServerPanel.setPreferredSize(new java.awt.Dimension(298, 35));

        ServerCB.setText("Server");

        javax.swing.GroupLayout ServerPanelLayout = new javax.swing.GroupLayout(ServerPanel);
        ServerPanel.setLayout(ServerPanelLayout);
        ServerPanelLayout.setHorizontalGroup(
            ServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ServerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ServerCB)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ServerPanelLayout.setVerticalGroup(
            ServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ServerPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ServerCB)
                .addContainerGap())
        );

        ProtocolPanel.setPreferredSize(new java.awt.Dimension(218, 35));

        ProtocolLbl.setText("Protocol");

        Protocol.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout ProtocolPanelLayout = new javax.swing.GroupLayout(ProtocolPanel);
        ProtocolPanel.setLayout(ProtocolPanelLayout);
        ProtocolPanelLayout.setHorizontalGroup(
            ProtocolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProtocolPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ProtocolLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Protocol, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        ProtocolPanelLayout.setVerticalGroup(
            ProtocolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ProtocolPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ProtocolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ProtocolLbl)
                    .addComponent(Protocol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(IPAddressPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
            .addComponent(PortPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
            .addComponent(ServerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
            .addComponent(ProtocolPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(ServerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(IPAddressPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PortPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ProtocolPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        OKBtn.setText("OK");
        OKBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKBtnActionPerformed(evt);
            }
        });

        CancelBtn.setText("Cancel");
        CancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(OKBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CancelBtn)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CancelBtn)
                    .addComponent(OKBtn))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    @Override
    protected JRootPane createRootPane() {
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JRootPane rootPane = new JRootPane();
        rootPane.registerKeyboardAction(this, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }
    
    /*
     * Accept changes.
     */            
    private void OKBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKBtnActionPerformed
        try
        {
            //e.getStateChange() == ItemEvent.DESELECTED
            Target.setHostName(IPAddressTB.getText());
            Target.setPort(Integer.parseInt(PortTB.getText()));
            Target.setServer(this.ServerCB.isSelected());
            Target.setProtocol(getProtocol(this.Protocol.getSelectedItem().toString()));
            Accepted = true;
            this.dispose();
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_OKBtnActionPerformed

    /*
     * Discard changes.
     */
    private void CancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelBtnActionPerformed
        this.dispose();
    }//GEN-LAST:event_CancelBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelBtn;
    private javax.swing.JLabel IPAddressLbl;
    private javax.swing.JPanel IPAddressPanel;
    private javax.swing.JTextField IPAddressTB;
    private javax.swing.JButton OKBtn;
    private javax.swing.JLabel PortLbl;
    private javax.swing.JPanel PortPanel;
    private javax.swing.JTextField PortTB;
    private javax.swing.JComboBox Protocol;
    private javax.swing.JLabel ProtocolLbl;
    private javax.swing.JPanel ProtocolPanel;
    private javax.swing.JCheckBox ServerCB;
    private javax.swing.JPanel ServerPanel;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
