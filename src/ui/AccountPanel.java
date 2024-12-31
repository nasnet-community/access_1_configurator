package ui;

import utils.ConnectionService;
import utils.Environments;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

class AccountPanel extends JPanel implements ListSelectionListener, ActionListener {

    private JList<String> list;
    private JTextField textFieldLastCon;
    private JTextField textFieldStaticIp;
    private JTextField textFieldAssignedIp;
    private JTextField textFieldSpeed;
    private JTextField textFieldBandwidth;
    private JTextField textFieldName;
    private JButton refreshListBtn;
    private JButton newBtn;
    private JButton saveBtn;
    private JButton deleteBtn;
    private JButton downloadBtn;
    public AccountPanel() {
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout(0, 20));

        refreshListBtn = new JButton("Refresh List");
        refreshListBtn.addActionListener(this);
        listPanel.add(refreshListBtn, BorderLayout.NORTH);

        DefaultListModel<String> l1 = new DefaultListModel<>();
        list = new JList<>(l1);
        list.setFixedCellWidth(80);
        list.setBorder(new EmptyBorder(20, 20, 20, 20));
        list.addListSelectionListener(this);
        listPanel.add(list, BorderLayout.CENTER);

        newBtn = new JButton("New Account");
        newBtn.addActionListener(this);
        listPanel.add(newBtn, BorderLayout.SOUTH);

        this.add(listPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1;
        GridBagConstraints gbc2;

        JLabel label;

        gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.anchor = GridBagConstraints.WEST;
        gbc1.insets = new Insets(0, 20, 10, 20);
        label = new JLabel("Name");
        rightPanel.add(label, gbc1);

        gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.insets = new Insets(0, 0, 20, 20);
        gbc2.weightx = 1.0;
        textFieldName = new JTextField(20);
        rightPanel.add(textFieldName, gbc2);

        gbc1.gridy = 1;
        label = new JLabel("Bandwidth limit (MB)");
        rightPanel.add(label, gbc1);

        gbc2.gridy = 1;
        textFieldBandwidth = new JTextField(20);
        rightPanel.add(textFieldBandwidth, gbc2);

        gbc1.gridy = 2;
        label = new JLabel("Used Bandwidth");
        rightPanel.add(label, gbc1);

        gbc2.gridy = 2;
        textFieldSpeed = new JTextField(20);
        textFieldSpeed.setEditable(false);
        rightPanel.add(textFieldSpeed, gbc2);

        gbc1.gridy = 3;
        label = new JLabel("Last connection");
        rightPanel.add(label, gbc1);

        gbc2.gridy = 3;
        textFieldLastCon = new JTextField(20);
        textFieldLastCon.setEditable(false);
        rightPanel.add(textFieldLastCon, gbc2);

        gbc1.gridy = 4;
        label = new JLabel("Client.ovpn");
        rightPanel.add(label, gbc1);

        gbc2.gridy = 4;
        gbc2.fill = GridBagConstraints.NONE;
        gbc2.anchor = GridBagConstraints.WEST;
        downloadBtn = new JButton("Download Ovpn");
        downloadBtn.setEnabled(false);
        downloadBtn.addActionListener(this);
        rightPanel.add(downloadBtn, gbc2);

        gbc1.gridy = 5;
        label = new JLabel("Static IP");
        rightPanel.add(label, gbc1);

        gbc2.gridy = 5;
        textFieldStaticIp = new JTextField(20);
        rightPanel.add(textFieldStaticIp, gbc2);

        gbc1.gridy = 6;
        label = new JLabel("Assigned IP");
        rightPanel.add(label, gbc1);

        gbc2.gridy = 6;
        textFieldAssignedIp = new JTextField(20);
        rightPanel.add(textFieldAssignedIp, gbc2);

        gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy =7;
        gbc1.anchor = GridBagConstraints.EAST;
        gbc1.insets = new Insets(10, 10, 10, 5);
        saveBtn = new JButton("Save Account");
        saveBtn.addActionListener(this);
        rightPanel.add(saveBtn, gbc1);

        gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 7;
        gbc2.anchor = GridBagConstraints.WEST;
        gbc2.insets = new Insets(10, 5, 10, 10);
        deleteBtn = new JButton("Delete Account");
        deleteBtn.addActionListener(this);
        rightPanel.add(deleteBtn, gbc2);

        this.add(rightPanel);

        //refreshList();
    }

    public void refreshList() {
        try {
            if (ConnectionService.isReachable(Environments.routerIp2)) {
                list.clearSelection();
                String[] com = new String[4];
                com[0] = "cd /etc/openvpn";
                com[1] = "mkdir -p accounts";
                com[2] = "cd accounts";
                com[3] = "ls";
                Vector<String> l = ConnectionService.executeCommandRemote(com);
                list.setListData(l);
            } else {
                list.removeAll();
            }

            textFieldName.setText("");
            textFieldBandwidth.setText("");
            textFieldSpeed.setText("");
            textFieldLastCon.setText("");
            textFieldStaticIp.setText("");
            textFieldAssignedIp.setText("");
            downloadBtn.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // List Select Event Listener
    @Override
    public void valueChanged(ListSelectionEvent e) {
        try {
            if (list.getSelectedIndex() == -1) return;
            String name = list.getSelectedValue();
            String[] com = new String[1];
            com[0] = "cat \"/etc/openvpn/accounts/" + name + "/" + name + "\"";
            Vector<String> res = ConnectionService.executeCommandRemote(com);

            com = new String[1];
            com[0] = "logread | grep 'User Connection:' | grep '" + res.get(3) + "' | tail -n 1";
            Vector<String> res1 = ConnectionService.executeCommandRemote(com);
            String lastConnection = (res1.size() > 0 && res1.get(0).indexOf("kern.warn") != -1) ? res1.get(0).substring(0, res1.get(0).indexOf("kern.warn") - 1) : "N/A";

            com = new String[1];
            com[0] = "iptables -L FORWARD -v | grep 'ACCEPT.*anywhere.*" + res.get(3) + "' | awk '{print $2}'";
            Vector<String> res2 = ConnectionService.executeCommandRemote(com);

            this.textFieldName.setText(name);
            this.textFieldBandwidth.setText(res.get(0));
            this.textFieldSpeed.setText(res2.get(0));
            this.textFieldLastCon.setText(lastConnection);
            this.textFieldStaticIp.setText(res.get(2));
            this.textFieldAssignedIp.setText(res.get(3));
            this.downloadBtn.setEnabled(true);
        } catch(Exception ev) {
            ev.printStackTrace();
        }
    }

    // Button Click Event Listeners
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(this.refreshListBtn)) {
            refreshList();
        }
        // Create Account
        else if (e.getSource().equals(this.newBtn)) {
            list.clearSelection();
            textFieldName.setText("");
            textFieldBandwidth.setText("");
            textFieldSpeed.setText("");
            textFieldLastCon.setText("");
            textFieldStaticIp.setText("");
            textFieldAssignedIp.setText("");
        }
        // Save Account
        else if (e.getSource().equals(this.saveBtn)) {

            if(list.getModel().getSize() == 0) {
                ConnectionService.configureBandWidthEnvironment();
            }

            String name = textFieldName.getText();
            String ip = textFieldStaticIp.getText();
            String vip = textFieldAssignedIp.getText();
            String lastConnection = "N/A";

            if (list.getSelectedValue() == null) { // New
                int newIp = ConnectionService.assignNewIp("" + Integer.parseInt(textFieldBandwidth.getText()) * 1024);

                if (newIp == 0) {
                    ConnectionService.logRemote("Ip assignment failed");
                    return;
                }
                vip = "192.168.9." + newIp;
                String[] com = new String[3];
                com[0] = "iptables -I FORWARD -s "+vip+" -j ACCEPT";
                com[1] = "iptables -I FORWARD -d "+vip+" -j ACCEPT";
                com[2] = "/etc/init.d/vpn-quota start \n";
                try {
                    ConnectionService.executeCommandRemote(com);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            } else {
                if (!ConnectionService.updateAssignedIp(vip, "" + Integer.parseInt(textFieldBandwidth.getText()) * 1024)) {
                    ConnectionService.logRemote("Ip assignment update failed");
                    return;
                }
            }
            String[] com = new String[8];
            com[0] = "cd /etc/openvpn/accounts";
            com[1] = "mkdir -p " + name;
            com[2] = "cd " + name;
            com[3] = "rm -f \"" + list.getSelectedValue() + "\"";
            com[4] = "echo \"" + textFieldBandwidth.getText() + "\" >> \"" + name + "\"";
//            com[5] = "echo \"" + textFieldSpeed.getText() + "\" >> \"" + name + "\"";
            com[5] = "echo \"" + lastConnection + "\" >> \"" + name + "\"";
            com[6] = "echo \"" + ip + "\" >> \"" + name + "\"";
            com[7] = "echo \"" + vip + "\" >> \"" + name + "\"";
            try {
                ConnectionService.executeCommandRemote(com);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            refreshList();
            createOpenVPNClient(name, ip, vip);
            ConnectionService.showAlert("Created user successfully.", "Create a new user");
        }
        // Delete Account
        else if (e.getSource().equals(this.deleteBtn)) {
            try {
                if (!ConnectionService.deleteAssignedIp(textFieldAssignedIp.getText())) {
                    ConnectionService.logRemote("Deleting Assigned Ip failed");
                    return;
                }
                String vip = textFieldAssignedIp.getText();

                String oldUsername = list.getSelectedValue();
                String[] com = new String[5];
                com[0] = "cd /etc/easy-rsa";
                com[1] = "easyrsa --batch revoke " + oldUsername;
                com[2] = "easyrsa gen-crl; echo 'success'1";
                com[3] = "cp /etc/easy-rsa/pki/crl.pem /etc/openvpn/crl.pem";
                com[4] = "/etc/init.d/openvpn restart";

                Map<Integer, Vector<String>> lastReps = new HashMap<>();
                Map<Integer, Map<String, String>> prompts = new HashMap<>();
                Map<Integer, Map<String, String>> passwords = new HashMap<>();

                Map<String, String> pwd = new HashMap<>();
                Vector<String> reps = new Vector<>();

                pwd.put("Enter pass phrase for /etc/easy-rsa/pki/private/ca.key:", "1234567890");
                passwords.put(2, pwd);
                reps.add("success1");
                lastReps.put(2, reps);
                boolean ret = ConnectionService.advancedRemoteControl(com, lastReps, prompts, passwords);

                com = new String[3];
                com[0] = "rm /etc/openvpn/ccd/" + oldUsername;
                com[1] = "rm -r /etc/openvpn/accounts/" + oldUsername;
                com[2] = "rm -r /etc/openvpn/clients/" + oldUsername;
                ConnectionService.executeCommandRemote(com);

                com[0] = "iptables -D FORWARD -s " + vip + " -j ACCEPT";
                com[1] = "iptables -D FORWARD -d " + vip + " -j ACCEPT";
                com[2] = "/etc/init.d/vpn-quota start \n";
                ConnectionService.executeCommandRemote(com);
                refreshList();
                ConnectionService.showAlert("Deleted user successfully.", "Delete User");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else if (e.getSource().equals(this.downloadBtn)) {
            DashboardPanel.downloadOVPN(textFieldName.getText());
        }
    }

    private void createOpenVPNClient(String username, String ip, String vip) {
        String remoteFilePath = "/etc/openvpn/clients/create-client-cert.sh";
//        ConnectionService.writeAndSend2Remote(remoteFilePath,CreateCert);
        String[] com = new String[5];
        com[0] = "mkdir -p /etc/openvpn/clients";
        com[1] = "mkdir -p /etc/openvpn/ccd";
        com[2] = "cd /etc/openvpn/ccd";
        com[3] = "echo \"ifconfig-push " + vip + " 255.255.255.0\" > " + username;
        com[4] = "chmod +x " + remoteFilePath + "\n";
        try {
            ConnectionService.executeCommandRemote(com);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // String localFilePath = "./temp.sh";
        Process sshProcess = null;
        BufferedWriter sshWriter = null;

        try {

            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder sshBuilder;
            if (os.contains("win")) {
                sshBuilder = new ProcessBuilder("cmd.exe", "/c", "ssh -p " + ConnectionService.getPort() + " -o StrictHostKeyChecking=no -T root@" + Environments.routerIp2);
            } else {
                sshBuilder = new ProcessBuilder("sh", "-c", "ssh -p " + ConnectionService.getPort() + " -o StrictHostKeyChecking=no -T root@" + Environments.routerIp2);
            }

            sshBuilder.redirectErrorStream(true);
            sshProcess = sshBuilder.start();

            sshWriter = new BufferedWriter(new OutputStreamWriter(sshProcess.getOutputStream()));

            // Make the script executable

            sshWriter.flush();

            sshWriter.close();

            sshProcess.waitFor();

            /*Map<String, String> prompts = new HashMap<>();
            prompts.put("Confirm key overwrite:", "yes");
            prompts.put("Common Name (eg: your user, host, or server name)", username);
            prompts.put("Confirm request details:", "yes");
            System.out.println("Before Create OpenVPN Client: " + remoteFilePath + " " + username);
            boolean ret = ConnectionService.interactRemoteShell(remoteFilePath + " " + username, prompts);*/

            com = new String[1];
            com[0] = remoteFilePath + " " + username + "; echo 'success'1";

            Map<Integer, Vector<String>> lastReps = new HashMap<>();
            Map<Integer, Map<String, String>> prompts = new HashMap<>();
            Map<Integer, Map<String, String>> passwords = new HashMap<>();

            Map<String, String> pwd = new HashMap<>();
            Vector<String> reps = new Vector<>();

            pwd.put("Enter pass phrase for /etc/easy-rsa/pki/private/ca.key:", "1234567890");
            passwords.put(0, pwd);
            //reps.add("have been generated and copied to /etc/openvpn/clients/");
            reps.add("success1");
            lastReps.put(0, reps);
            boolean ret = ConnectionService.advancedRemoteControl(com, lastReps, prompts, passwords);

            System.out.println("Create OpenVPN Client: " + remoteFilePath + " " + username + ":" + ret);
            ConnectionService.logRemote("Create OpenVPN Client: " + ret);
            ConnectionService.createOpenVPNClient(username, ip);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
