// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.navpanel;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

public class StartPanelEmailNB extends JPanel implements FocusListener, ActionListener
{
	private static String hint = RB.getString("gui.navpanel.NBStartEmailPanel.hint");

    public StartPanelEmailNB()
	{
		initComponents();
		setOpaque(false);

		emailText.setText(Prefs.miscEmail);
		institutionText.setText(Prefs.miscInstitution);

		RB.setText(label, "gui.navpanel.NBStartEmailPanel.label");
		RB.setText(emailLabel, "gui.navpanel.NBStartEmailPanel.email");
		RB.setText(institutionLabel, "gui.navpanel.NBStartEmailPanel.institution");
		RB.setText(bSubscribe, "gui.navpanel.NBStartEmailPanel.subscribe");
		RB.setText(bUnsubscribe, "gui.navpanel.NBStartEmailPanel.unsubscribe");

		setStatus();

		bSubscribe.addActionListener(this);
		bUnsubscribe.addActionListener(this);

		institutionText.addFocusListener(this);
		focusLost(null);
    }

	// Updates the institution hint depending on the status of the text box
    public void focusGained(FocusEvent e)
	{
		if (institutionText.getText().trim().equals(hint))
		{
			institutionText.setText("");
			institutionText.setForeground(Color.black);
		}
	}

	public void focusLost(FocusEvent e)
	{
		if (institutionText.getText().trim().length() == 0)
		{
			institutionText.setText(hint);
			institutionText.setForeground(Color.lightGray);
		}
		else
			institutionText.setForeground(Color.black);
	}

    private void setStatus()
    {
    	if (Prefs.miscSubscribed)
    	{
			bSubscribe.setEnabled(false);
			bUnsubscribe.setEnabled(true);
    	}
		else
		{
			bSubscribe.setEnabled(true);
			bUnsubscribe.setEnabled(false);
		}
    }

    private void subscribe()
    {
		Prefs.miscEmail = emailText.getText().trim();

		// Only track the institution if a real value has been entered
		if (institutionText.getText().trim().equals(hint) == false)
			Prefs.miscInstitution = institutionText.getText().trim();

		// Quit if the email doesn't look valid
		if (Prefs.miscEmail.length() == 0 || Prefs.miscEmail.indexOf("@") == -1)
			return;

		if (connectToServer("add"))
		{
			Prefs.miscSubscribed = true;
			setStatus();
		}
    }

    private void unsubscribe()
    {
    	if (connectToServer("del"))
    	{
			Prefs.miscSubscribed = false;
			setStatus();
    	}
    }

	// Connects to a remote cgi script to log (add or delete) email addresses
    private boolean connectToServer(String cmd)
    {
    	try
		{
			String id = URLEncoder.encode(Prefs.flapjackID, "UTF-8");
			String email = URLEncoder.encode(Prefs.miscEmail, "UTF-8");
			String institution = URLEncoder.encode(Prefs.miscInstitution, "UTF-8");

			String addr = "http://bioinf.hutton.ac.uk/flapjack/logs/email.pl"
				+ "?cmd=" + cmd
				+ "&id=" + id
				+ "&email=" + email
				+ "&institution=" + institution;

			URL url = new URL(addr);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();

			int code = c.getResponseCode();
			c.disconnect();

			return code == 200;
		}
		catch (Exception e) {}

		return false;
    }

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == bSubscribe)
		{
			subscribe();
		}

		if(e.getSource() == bUnsubscribe)
		{
			unsubscribe();
		}
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        institutionLabel = new javax.swing.JLabel();
        emailText = new javax.swing.JTextField();
        institutionText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        bSubscribe = new scri.commons.gui.matisse.HyperLinkLabel();
        bUnsubscribe = new scri.commons.gui.matisse.HyperLinkLabel();

        label.setText("<html>Subscribe to the Flapjack mailing list and we'll keep you informed when new releases are available.");

        emailLabel.setText("Email address:");

        institutionLabel.setText("Institution (optional): ");

        institutionText.setForeground(java.awt.Color.lightGray);

        jLabel4.setText("|");

        bSubscribe.setForeground(new java.awt.Color(68, 106, 156));
        bSubscribe.setText("subscribe");

        bUnsubscribe.setForeground(new java.awt.Color(68, 106, 156));
        bUnsubscribe.setText("unsubscribe");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(institutionLabel)
                            .addComponent(emailLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(institutionText, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                            .addComponent(emailText, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bSubscribe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bUnsubscribe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailLabel)
                    .addComponent(emailText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(bSubscribe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bUnsubscribe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(institutionLabel)
                    .addComponent(institutionText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private scri.commons.gui.matisse.HyperLinkLabel bSubscribe;
    private scri.commons.gui.matisse.HyperLinkLabel bUnsubscribe;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JTextField emailText;
    private javax.swing.JLabel institutionLabel;
    private javax.swing.JTextField institutionText;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel label;
    // End of variables declaration//GEN-END:variables


}