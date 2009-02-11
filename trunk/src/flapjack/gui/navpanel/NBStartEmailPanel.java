package flapjack.gui.navpanel;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;

import flapjack.gui.*;

public class NBStartEmailPanel extends JPanel implements FocusListener
{
	private static String hint = RB.getString("gui.navpanel.NBStartEmailPanel.hint");

    public NBStartEmailPanel()
	{
		initComponents();
		setBackground(Color.white);
		panel.setBackground(Color.white);

		emailText.setText(Prefs.miscEmail);
		institutionText.setText(Prefs.miscInstitution);

		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.navpanel.NBStartEmailPanel.panel.title")));
		RB.setText(statusLabel1, "gui.navpanel.NBStartEmailPanel.statusLabel1");
		RB.setText(emailLabel, "gui.navpanel.NBStartEmailPanel.email");
		RB.setText(institutionLabel, "gui.navpanel.NBStartEmailPanel.institution");
		RB.setText(bSubscribe, "gui.navpanel.NBStartEmailPanel.subscribe");
		RB.setText(bUnsubscribe, "gui.navpanel.NBStartEmailPanel.unsubscribe");

		setStatus();

		bSubscribe.setCursor(new Cursor(Cursor.HAND_CURSOR));
		bSubscribe.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				subscribe();
			}
		});

		bUnsubscribe.setCursor(new Cursor(Cursor.HAND_CURSOR));
		bUnsubscribe.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				unsubscribe();
			}
		});

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
	}

    private void setStatus()
    {
    	String s1 = RB.getString("gui.navpanel.NBStartEmailPanel.statusSubscribed");
    	String s2 = RB.getString("gui.navpanel.NBStartEmailPanel.statusUnsubscribed");

    	if (Prefs.miscSubscribed)
    	{
			statusLabel2.setText(s1);
			bSubscribe.setEnabled(false);
			bUnsubscribe.setEnabled(true);
    	}
		else
		{
			statusLabel2.setText(s2);
			bSubscribe.setEnabled(true);
			bUnsubscribe.setEnabled(false);
		}
    }

    private void subscribe()
    {
		Prefs.miscEmail = emailText.getText().trim();
		Prefs.miscInstitution = institutionText.getText().trim();

		if (Prefs.miscEmail.length() == 0 || Prefs.miscEmail.indexOf("@") == -1)
			return;

		if (connectToServer("add"))
		{
			Prefs.miscSubscribed = true;
			setStatus();
		}
		else
			statusLabel2.setText(RB.getString("gui.navpanel.NBStartEmailPanel.error"));
    }

    private void unsubscribe()
    {
    	if (connectToServer("del"))
    	{
			Prefs.miscSubscribed = false;
			setStatus();
    	}
		else
			statusLabel2.setText(RB.getString("gui.navpanel.NBStartEmailPanel.error"));
    }

	// Connects to a remote cgi script to log (add or delete) email addresses
    private boolean connectToServer(String cmd)
    {
    	statusLabel2.setText("...");

    	try
		{
			String id = URLEncoder.encode(Prefs.flapjackID, "UTF-8");
			String email = URLEncoder.encode(Prefs.miscEmail, "UTF-8");
			String institution = URLEncoder.encode(Prefs.miscInstitution, "UTF-8");

			String addr = "http://bioinf.scri.ac.uk/cgi-bin/flapjack/email.cgi"
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        label = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        emailText = new javax.swing.JTextField();
        institutionLabel = new javax.swing.JLabel();
        institutionText = new javax.swing.JTextField();
        bSubscribe = new javax.swing.JLabel();
        statusLabel1 = new javax.swing.JLabel();
        statusLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        bUnsubscribe = new javax.swing.JLabel();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Email updates:"));

        label.setText("<html>Subscribe to the Flapjack mailing list and we'll keep you informed when new releases are available.");

        emailLabel.setText("Email address:");

        institutionLabel.setText("Institution (optional): ");

        institutionText.setForeground(java.awt.Color.lightGray);

        bSubscribe.setForeground(new java.awt.Color(68, 106, 156));
        bSubscribe.setText("subscribe");

        statusLabel1.setText("Current status:");

        statusLabel2.setText("Status");

        jLabel4.setText("|");

        bUnsubscribe.setForeground(new java.awt.Color(68, 106, 156));
        bUnsubscribe.setText("unsubscribe");

        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                    .add(panelLayout.createSequentialGroup()
                        .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(institutionLabel)
                            .add(emailLabel)
                            .add(statusLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(statusLabel2)
                            .add(panelLayout.createSequentialGroup()
                                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, institutionText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, emailText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 250, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(bSubscribe)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel4)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(bUnsubscribe)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 26, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusLabel1)
                    .add(statusLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(emailLabel)
                    .add(emailText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4)
                    .add(bUnsubscribe)
                    .add(bSubscribe))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(institutionLabel)
                    .add(institutionText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bSubscribe;
    private javax.swing.JLabel bUnsubscribe;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JTextField emailText;
    private javax.swing.JLabel institutionLabel;
    private javax.swing.JTextField institutionText;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel label;
    private javax.swing.JPanel panel;
    private javax.swing.JLabel statusLabel1;
    private javax.swing.JLabel statusLabel2;
    // End of variables declaration//GEN-END:variables

}
