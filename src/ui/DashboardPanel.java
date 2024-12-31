package ui;

import ui.components.SwitchButton;
import utils.ConnectionService;
import utils.Environments;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class DashboardPanel extends JPanel implements ActionListener {
    private JPanel routerBottomPanel;
    private JPanel starlinkBottomPanel;
    private JPanel dslBottomPanel;
    private JLabel firstStepTxt1;
    private JLabel secondStepTxt1;

    private JLabel secondStepTxt2;
    private JLabel thirdStepTxt1;
    private JLabel fourthStepTxt1;
    private JLabel installStatTxt;
    private JLabel fifthStepTxt1;
    private JLabel sixStepTxt1;
    private JButton sendBackupFileBtn;
    private JButton restoreBackupFile;
    private JButton installPackageBtn;
    private JButton downloadVPNBtn;
    private JButton createOpenVPNServerBtn;
    private JButton createOpenVPNClientBtn;
    private JButton downloadOVPNBtn;
    private JButton refreshBtn;

    private JButton troubleShootBtn;
    private SwitchButton splitBlockBtn;

    public DashboardPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel stepFrame = new JPanel();
        stepFrame.setLayout(new GridLayout(4, 2));
        stepFrame.setBorder(new EmptyBorder(50, 50, 50, 50));

        // Add a flow pane in the main center section
        JPanel configureBtnPane = new JPanel(new FlowLayout());
        configureBtnPane.setMaximumSize(new Dimension(300, 180));
        configureBtnPane.setLayout(new BoxLayout(configureBtnPane, BoxLayout.Y_AXIS));

        sendBackupFileBtn = new JButton("Send Backup File");
        restoreBackupFile = new JButton("Configure Router");
        installPackageBtn = new JButton("Secure and update Router");
        downloadVPNBtn = new JButton("Download OVPN");
        createOpenVPNServerBtn = new JButton("Create OpenVPN Server");
        createOpenVPNClientBtn = new JButton("Create OpenVPN Client");
        downloadOVPNBtn = new JButton("Download OVPN File");
        refreshBtn = new JButton("Refresh");
        troubleShootBtn = new JButton("TroubleShoot");
        splitBlockBtn = new SwitchButton("Split", "Block");

        sendBackupFileBtn.addActionListener(this);
        createOpenVPNClientBtn.addActionListener(this);
        createOpenVPNServerBtn.addActionListener(this);
        restoreBackupFile.addActionListener(this);
        downloadVPNBtn.addActionListener(this);
        installPackageBtn.addActionListener(this);
        downloadOVPNBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        troubleShootBtn.addActionListener(this);
        splitBlockBtn.addActionListener(this);


        sendBackupFileBtn.setMaximumSize(new Dimension(200, 30));
        sendBackupFileBtn.setBackground(Color.WHITE);
        sendBackupFileBtn.setForeground(Color.BLACK);

        createOpenVPNServerBtn.setMaximumSize(new Dimension(200, 30));
        createOpenVPNServerBtn.setBackground(Color.WHITE);
        createOpenVPNServerBtn.setForeground(Color.BLACK);

        restoreBackupFile.setMaximumSize(new Dimension(200, 30));
        restoreBackupFile.setBackground(Color.WHITE);
        restoreBackupFile.setForeground(Color.BLACK);

        createOpenVPNClientBtn.setMaximumSize(new Dimension(200, 30));
        createOpenVPNClientBtn.setBackground(Color.WHITE);
        createOpenVPNClientBtn.setForeground(Color.BLACK);

        downloadVPNBtn.setMaximumSize(new Dimension(200, 30));
        downloadVPNBtn.setBackground(Color.WHITE);
        downloadVPNBtn.setForeground(Color.BLACK);

        installPackageBtn.setMaximumSize(new Dimension(200, 30));
        installPackageBtn.setBackground(Color.WHITE);
        installPackageBtn.setForeground(Color.BLACK);

        downloadOVPNBtn.setMaximumSize(new Dimension(200, 30));
        downloadOVPNBtn.setBackground(Color.WHITE);
        downloadOVPNBtn.setForeground(Color.BLACK);

        refreshBtn.setMaximumSize(new Dimension(200, 30));
        refreshBtn.setBackground(Color.WHITE);
        refreshBtn.setForeground(Color.BLACK);

        troubleShootBtn.setMaximumSize(new Dimension(200, 30));
        troubleShootBtn.setBackground(Color.WHITE);
        troubleShootBtn.setForeground(Color.BLACK);

        JPanel splitBlockPanel = new JPanel();
        splitBlockPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JLabel splitBlockLabel = new JLabel("Iranian Websites: ");
        splitBlockPanel.add(splitBlockLabel);
        splitBlockPanel.add(splitBlockBtn);

        configureBtnPane.add(Box.createRigidArea(new Dimension(0, 10)));
        configureBtnPane.add(Box.createRigidArea(new Dimension(0, 10)));
        configureBtnPane.add(Box.createRigidArea(new Dimension(0, 10)));
        configureBtnPane.add(Box.createRigidArea(new Dimension(0, 40)));
        configureBtnPane.add(splitBlockPanel);
        configureBtnPane.add(Box.createRigidArea(new Dimension(0, 10)));
        configureBtnPane.add(troubleShootBtn);

        JPanel firstStepPanel = new JPanel();
        firstStepPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        firstStepTxt1 = new JLabel("1");
        firstStepTxt1.setBorder(new EmptyBorder(10, 10, 10, 10));
        firstStepTxt1.setOpaque(true);
        updateFirstStepColor();
        JLabel firstStepTxt2 = new JLabel("Connect Openwrt Lan1 port to your computer");
        firstStepPanel.add(firstStepTxt1);
        firstStepPanel.add(firstStepTxt2);

        JPanel secondStepPanel = new JPanel();
        secondStepPanel.setLayout(new BoxLayout(secondStepPanel, BoxLayout.Y_AXIS));
        JPanel secondUpperPanel = new JPanel();
        JPanel secondLowerPanel = new JPanel();
        secondUpperPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        secondLowerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        secondStepTxt1 = new JLabel("2");
        secondStepTxt1.setBorder(new EmptyBorder(10, 10, 10, 10));
        secondStepTxt1.setBackground(Color.GRAY);
        secondStepTxt1.setOpaque(true);
        restoreBackupFile.setSize(100, 100);
        secondStepTxt2 = new JLabel("Configure Router" + " (N/A)");
        secondUpperPanel.add(secondStepTxt1);
        secondUpperPanel.add(secondStepTxt2);
        secondLowerPanel.add(restoreBackupFile);
        secondStepPanel.add(secondUpperPanel);
        secondStepPanel.add(secondLowerPanel);


        JPanel thirdStepPanel = new JPanel();
        thirdStepPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        thirdStepTxt1 = new JLabel("3");
        thirdStepTxt1.setBorder(new EmptyBorder(10, 10, 10, 10));
        thirdStepTxt1.setBackground(Color.GRAY);
        thirdStepTxt1.setOpaque(true);
        JLabel fifthStepTxt2 = new JLabel("Connect your Uncensored Internet Wi-Fi");
        thirdStepPanel.add(thirdStepTxt1);
        thirdStepPanel.add(fifthStepTxt2);

        JPanel fourthStepPanel = new JPanel();
        fourthStepPanel.setLayout(new BoxLayout(fourthStepPanel, BoxLayout.Y_AXIS));
        JPanel fourthUpperPanel = new JPanel();
        JPanel fourthLowerPanel = new JPanel();
        fourthUpperPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        fourthLowerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        //fourthStepPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        fourthStepTxt1 = new JLabel("4");
        fourthStepTxt1.setBorder(new EmptyBorder(10, 10, 10, 10));
        fourthStepTxt1.setBackground(Color.GRAY);
        fourthStepTxt1.setOpaque(true);
        JLabel fourthStepTxt2 = new JLabel("Install packages & Secure Router");
        fourthUpperPanel.add(fourthStepTxt1);
        fourthUpperPanel.add(fourthStepTxt2);
        installStatTxt = new JLabel("");
        fourthLowerPanel.add(installPackageBtn);
        fourthLowerPanel.add(installStatTxt);
        fourthStepPanel.add(fourthUpperPanel);
        fourthStepPanel.add(fourthLowerPanel);

        JPanel fifthStepPanel = new JPanel();
        fifthStepPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        fifthStepTxt1 = new JLabel("5");
        fifthStepTxt1.setBorder(new EmptyBorder(10, 10, 10, 10));
        fifthStepTxt1.setBackground(Color.GRAY);
        fifthStepTxt1.setOpaque(true);
        JLabel thirdStepTxt2 = new JLabel("Connect your DSL Router to Openwrt Lan2 port");
        fifthStepPanel.add(fifthStepTxt1);
        fifthStepPanel.add(thirdStepTxt2);

//        JPanel sixStepPanel = new JPanel();
//        sixStepPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel sixStepPanel = new JPanel();
        sixStepPanel.setLayout(new BoxLayout(sixStepPanel, BoxLayout.Y_AXIS));
        JPanel sixthUpperPanel = new JPanel();
        JPanel sixthLowerPanel = new JPanel();
        sixthUpperPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        sixthLowerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        //sevenStepPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        sixStepTxt1 = new JLabel("6");
        sixStepTxt1.setBorder(new EmptyBorder(10, 10, 10, 10));
        sixStepTxt1.setBackground(Color.GRAY);
        sixStepTxt1.setOpaque(true);
        JLabel sevenStepTxt2 = new JLabel("Creating VPN certificates");
        sixthUpperPanel.add(sixStepTxt1);
        sixthUpperPanel.add(sevenStepTxt2);
        sixthLowerPanel.add(createOpenVPNServerBtn);
        sixStepPanel.add(sixthUpperPanel);
        sixStepPanel.add(sixthLowerPanel);

        stepFrame.add(firstStepPanel);
        stepFrame.add(secondStepPanel);
        stepFrame.add(thirdStepPanel);
        stepFrame.add(fourthStepPanel);
        stepFrame.add(fifthStepPanel);
        stepFrame.add(sixStepPanel);

        add(stepFrame);

        /** +++++++++++++++++++++ Router ++++++++++++++++++++++ */
        JPanel routerPanel = new JPanel();
        routerPanel.setMaximumSize(new Dimension(200, 180));
        routerPanel.setLayout(new BoxLayout(routerPanel, BoxLayout.Y_AXIS));
        routerPanel.setBackground(SystemColor.WHITE);

        JLabel routerLabel = new JLabel("Router");

        try {
            BufferedImage dslImage = ImageIO.read(getClass().getResource("assets/tp_link.jpg"));
            int desiredWidth = 200;
            int desiredHeight = 150;

            ImageIcon icon = new ImageIcon(dslImage.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH));

            JLabel imageLabel = new JLabel(icon);

            routerBottomPanel = new JPanel();
            routerBottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
            updateRouterBottomPanelColor();

            // Add the image label to the routerPanel
            routerPanel.add(imageLabel);
            routerPanel.add(routerBottomPanel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        routerPanel.add(routerLabel);
        /** ---------------- Router ------------------ */

        /** +++++++++++++++++++++ Starlink ++++++++++++++++++++++ */
        JPanel starlinkPanel = new JPanel();
        starlinkPanel.setMaximumSize(new Dimension(200, 180));
        starlinkPanel.setLayout(new BoxLayout(starlinkPanel, BoxLayout.Y_AXIS));
        starlinkPanel.setBackground(SystemColor.WHITE);
        JLabel startlinkLabel = new JLabel("Star Link");
        startlinkLabel.setPreferredSize(new Dimension(100, startlinkLabel.getPreferredSize().height));

        try {
            BufferedImage dslImage = ImageIO.read(getClass().getResource("assets/starlink.png"));
            int desiredWidth = 200;
            int desiredHeight = 150;

            ImageIcon icon = new ImageIcon(dslImage.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH));

            JLabel imageLabel = new JLabel(icon);

            starlinkBottomPanel = new JPanel();
            starlinkBottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
            updateStarlinkBottomPanelColor();

            // Add the image label to the routerPanel
            starlinkPanel.add(imageLabel);
            starlinkPanel.add(starlinkBottomPanel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        starlinkPanel.add(startlinkLabel);
        /** ---------------- Starlink ------------------ */

        /** +++++++++++++++++++++ DSL ++++++++++++++++++++++ */
        JPanel dslPanel = new JPanel();
        dslPanel.setMaximumSize(new Dimension(200, 180));
        dslPanel.setLayout(new BoxLayout(dslPanel, BoxLayout.Y_AXIS));
        dslPanel.setBackground(SystemColor.WHITE);
        JLabel dslLabel = new JLabel("DSL");
        try {
            BufferedImage dslImage = ImageIO.read(getClass().getResource("assets/dsl.png"));
            int desiredWidth = 200;
            int desiredHeight = 150;

            ImageIcon icon = new ImageIcon(dslImage.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH));

            JLabel imageLabel = new JLabel(icon);

            dslBottomPanel = new JPanel();
            dslBottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
            updateDslBottomPanelColor();

            // Add the image label to the routerPanel
            dslPanel.add(imageLabel);
            dslPanel.add(dslBottomPanel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dslPanel.add(dslLabel);
        /** ---------------- DLS ------------------ */

        add(Box.createRigidArea(new Dimension(0, 20)));
        add(configureBtnPane);
        add(Box.createRigidArea(new Dimension(0, 20)));
    }

    public void updateFirstStepColor() {
        if (ConnectionService.isCompletedFirstStep) {
            firstStepTxt1.setBackground(Color.GREEN);
        } else {
            firstStepTxt1.setBackground(Color.GRAY);
        }

        firstStepTxt1.repaint();
    }

    public void updateSecondStepColor() {
        if (ConnectionService.resortingBackupfile) {
            secondStepTxt1.setBackground(Color.YELLOW);
            restoreBackupFile.setEnabled(false);
        } else if (ConnectionService.isCompletedFirstStep && ConnectionService.isCompletedSecondStep) {
            secondStepTxt1.setBackground(Color.GREEN);
            restoreBackupFile.setEnabled(true);
            secondStepTxt2.setText("Configure Router" + " (" + (ConnectionService.isReachable(Environments.routerIp2)? Environments.routerIp2: Environments.routerIp1) + ")");
        } else {
            secondStepTxt1.setBackground(Color.GRAY);
            restoreBackupFile.setEnabled(true);
            secondStepTxt2.setText("Configure Router" + " (N/A)");
        }

        secondStepTxt1.repaint();
    }

    public void updateThirdStepColor() {
        if (ConnectionService.isCompletedSecondStep && ConnectionService.isCompletedThirdStep) {
            thirdStepTxt1.setBackground(Color.GREEN);
        } else {
            thirdStepTxt1.setBackground(Color.GRAY);
        }

        thirdStepTxt1.repaint();
    }

    public void updateFourthStepColor() {
        if (!ConnectionService.isCompletedSecondStep && !ConnectionService.isCompletedFourthStep) {
            fourthStepTxt1.setBackground(Color.GRAY);
        } else if (ConnectionService.installingPackages && !ConnectionService.isCompletedFourthStep) {
            fourthStepTxt1.setBackground(Color.YELLOW);
            installPackageBtn.setEnabled(false);
        } else if (!ConnectionService.installingPackages && ConnectionService.isCompletedFourthStep) {
            fourthStepTxt1.setBackground(Color.GREEN);
            installPackageBtn.setEnabled(true);
        } else {
            installPackageBtn.setEnabled(true);
        }

        fourthStepTxt1.repaint();
    }

    public void setInstallStatTxt(String str) {
        installStatTxt.setText(str);
    }

    public String getInstallStatTxt() {
        return installStatTxt.getText();
    }

    public void updateFifthStepColor() {
        if (ConnectionService.isCompletedFifthStep) {
            fifthStepTxt1.setBackground(Color.GREEN);
        } else {
            fifthStepTxt1.setBackground(Color.GRAY);
        }

        fifthStepTxt1.repaint();
    }

    public void updateSixStepColor() {
        if (ConnectionService.isCompletedSixStep && !ConnectionService.isConfiguringVPNServer) {
            sixStepTxt1.setBackground(Color.GREEN);
        } else if (ConnectionService.isConfiguringVPNServer) {
            sixStepTxt1.setBackground(Color.YELLOW);
        } else {
            sixStepTxt1.setBackground(Color.GRAY);
        }

        sixStepTxt1.repaint();
    }


    public void updateRouterBottomPanelColor() {
        if (ConnectionService.isConnectedTPLink) {
            routerBottomPanel.setBackground(new Color(0, 255, 0)); // Green color when connected
        } else {
            routerBottomPanel.setBackground(new Color(255, 0, 0)); // Red color when not connected
        }

        // Repaint the panel to reflect the color change
        routerBottomPanel.repaint();
    }

    public void updateStarlinkBottomPanelColor() {
        if (ConnectionService.isConnectedStarLink) {
            starlinkBottomPanel.setBackground(new Color(0, 255, 0)); // Green color when connected
            ConnectionService.isCompletedSixStep = true;
        } else {
            starlinkBottomPanel.setBackground(new Color(255, 0, 0)); // Red color when not connected
            ConnectionService.isCompletedSixStep = false;
        }

        // Repaint the panel to reflect the color change
        starlinkBottomPanel.repaint();
    }

    public void updateDslBottomPanelColor() {
        if (ConnectionService.isConnectedDSLLink) {
            dslBottomPanel.setBackground(new Color(0, 255, 0)); // Green color when connected
        } else {
            dslBottomPanel.setBackground(new Color(255, 0, 0)); // Red color when not connected
        }

        // Repaint the panel to reflect the color change
        dslBottomPanel.repaint();
    }

    static void downloadOVPN(String username) {

        JFrame frame = new JFrame("Save File Dialog Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Ovpn files (*.ovpn)", "ovpn");
        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.setFileFilter(txtFilter);
        int returnValue = fileChooser.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String extension = "";

            if (fileChooser.getFileFilter() == txtFilter) {
                extension = ".ovpn";
            }

            if (!selectedFile.getAbsolutePath().endsWith(extension))
                selectedFile = new File(selectedFile + extension);
            // Main download part
            Process sshProcess = null;
            BufferedReader sshReader = null;
            BufferedWriter sshWriter = null;
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder sshBuilder;

            try {
                // Use the user's home directory for the file path
                String userHome = System.getProperty("user.home");
                File openvpnClientFile = new File(selectedFile.getAbsolutePath());

                // Create the file if it does not exist
                if (openvpnClientFile.createNewFile()) {
                    System.out.println("File created: " + openvpnClientFile.getName());
                } else {
                    System.out.println("File already exists.");
                }

                String cmd = "";
                if(username.equals("client")) {
                    cmd = "cat /etc/openvpn/client.ovpn";
                } else {
                    cmd = "cat /etc/openvpn/accounts/" + username + "/" + username + ".ovpn";
                }
                String[] sshCommand = {
                        "ssh",
                        "-p", ConnectionService.getPort(),
                        "root@" + Environments.routerIp2,
                        cmd
                };
                sshBuilder = new ProcessBuilder(sshCommand);
                sshBuilder.redirectErrorStream(true);
                sshProcess = sshBuilder.start();

                // Get input stream for SSH process
                sshReader = new BufferedReader(new InputStreamReader(sshProcess.getInputStream()));

                FileWriter openvpnFile = new FileWriter(openvpnClientFile);
                // Open file writer
                BufferedWriter ovpnFileWriter = new BufferedWriter(openvpnFile);

                String sshLine;

                while ((sshLine = sshReader.readLine()) != null) {
                    System.out.println("Read from SSH: " + sshLine);
                    ovpnFileWriter.write(sshLine);
                    ovpnFileWriter.newLine(); // Add newline character
                }
                sshReader.close();
                int exitCode = sshProcess.waitFor();
                System.out.println("Exited with error code: " + exitCode);

                ovpnFileWriter.close();
                System.out.println("Openvpn_3 configuration via SSH completed successfully.");
            } catch (Exception e) {
                System.out.println("Error occurred during SSH operation: " + e.getMessage());
            }

            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        } else {
            System.out.println("Save command cancelled by user.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(sendBackupFileBtn)) {
            ConnectionService.sendBackupFileFunc();
        }
        else if (e.getSource().equals(createOpenVPNClientBtn)) {
            ConnectionService.createOpenVPNClient("client","192.168.111.111");
        }
        else if (e.getSource().equals(createOpenVPNServerBtn)) {
            ConnectionService.configureOpenvpnSSH();
        }
        else if (e.getSource().equals(restoreBackupFile)) {
            ConnectionService.restoreBackup();
        }
        else if (e.getSource().equals(downloadOVPNBtn)) {
            downloadOVPN("client");
        }
        else if (e.getSource().equals(installPackageBtn)) {
            ConnectionService.installPackages();
        }
        else if(e.getSource().equals(refreshBtn)){
            ConnectionService.currentStatus();
        }
        else if(e.getSource().equals(troubleShootBtn)){
            ConnectionService.troubleShoot();
        }
        else if(e.getSource().equals(splitBlockBtn)){
            if (splitBlockBtn.isSelected())
                ConnectionService.split();
            else
                ConnectionService.block();
        }
    }
}