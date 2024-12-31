package utils;

import ui.DashboardPanel;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JOptionPane;

public class ConnectionService {
    private static DashboardPanel dashboard;
    public static boolean isConnectedTPLink = false;
    public static boolean isConnectedStarLink = false;
    public static boolean isConnectedDSLLink = false;
    public static boolean isCompletedFirstStep = false;
    public static boolean isCompletedSecondStep = false;
    public static boolean isCompletedThirdStep = false;
    public static boolean isCompletedFourthStep = false;
    public static boolean isCompletedFifthStep = false;
    public static boolean isCompletedSixStep = false;
//    public static boolean isCompletedSevenStep = false;
    public static boolean isCompletedEightStep = false;
    public static boolean isConfiguringVPNServer = false;
    public static boolean installingPackages = false;
    public static boolean resortingBackupfile = false;
    public static  String realIpAddress = "";

    public static boolean isAccessPanelInited = false;
    public static boolean isSecureConnection = false;

    public static void setDashboard(DashboardPanel d) {
        dashboard = d;
    }

    public static void resortBackupFile() {
        //isCompletedSecondStep = false;
        resortingBackupfile = true;
        dashboard.updateSecondStepColor();

        try {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    boolean isResotreBackupFile = false;
                    try {
                        Boolean sendBackupFileRes = sendBackupFileFunc();
                        if (sendBackupFileRes) {
                            System.out.println("Start restoring \n");
                            String os = System.getProperty("os.name").toLowerCase();
                            ProcessBuilder processBuilder;
                            Process sshProcess = null;
                            BufferedReader sshReader = null;
                            BufferedWriter sshWriter = null;
                            if (os.contains("win")) {
                                processBuilder = new ProcessBuilder("cmd.exe", "/c", "ssh -o StrictHostKeyChecking=no -T root@" + Environments.routerIp1);
                            } else {
                                processBuilder = new ProcessBuilder("sh", "-c", "ssh -o StrictHostKeyChecking=no -T root@" + Environments.routerIp1);
                            }

                            processBuilder.redirectErrorStream(true);
                            sshProcess = processBuilder.start();

                            sshReader = new BufferedReader(new InputStreamReader(sshProcess.getInputStream()));
                            sshWriter = new BufferedWriter(new OutputStreamWriter(sshProcess.getOutputStream()));
                            String restoreCommand = "tar -xzvf /tmp/backup-openwrt.tar.gz -C / && reboot \n";
                            System.out.println("restoreCommand \n");
                            sshWriter.write(restoreCommand);
                            sshWriter.flush();

                            sshWriter.write("exit\n");
                            sshWriter.flush();

                            String sshLine;
                            while ((sshLine = sshReader.readLine()) != null) {
                                System.out.println("sshLine" + sshLine);
                                if (sshLine.equals("--------------------------------------------------")) {
                                    break;
                                }
                            }

                            sshProcess.waitFor();
                            System.out.println("Restore backupfile successfully");
                            //isResotreBackupFile = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("*************** Restore Backupfile Status ***************" + isResotreBackupFile);
                    logRemote("Restore Backupfile Status: " + isResotreBackupFile);
                    //isCompletedSecondStep = true;
                    resortingBackupfile = false;
                    dashboard.updateSecondStepColor();
                    showAlert("Router is configured successfully.", "Router configuration");
                }
            };
            thread.start();

        } catch (Exception e) {
            System.out.println("Error occurred during SSH operation: " + e.getMessage());
        }
    }

    public static void showAlert(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    public static void createOpenVPNClient(String username, String staticIp) {
        Process sshProcess = null;
        BufferedReader sshReader = null;
        BufferedWriter sshWriter = null;
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder sshBuilder;
        try {
            if (os.contains("win")) {
                sshBuilder = new ProcessBuilder("cmd.exe", "/c", "ssh -p " + getPort() + " -o StrictHostKeyChecking=no -T root@" + Environments.routerIp2);
            } else {
                sshBuilder = new ProcessBuilder("sh", "-c", "ssh -p " + getPort() + " -o StrictHostKeyChecking=no -T root@" + Environments.routerIp2);
            }
            sshBuilder.redirectErrorStream(true);
            sshProcess = sshBuilder.start();

            // Get input and output streams for SSH process
            sshReader = new BufferedReader(new InputStreamReader(sshProcess.getInputStream()));
            sshWriter = new BufferedWriter(new OutputStreamWriter(sshProcess.getOutputStream()));

            // +++++++++++ Create Client OVPN File ++++++++++++++++++++++++++a
            sshWriter.write("umask go=\n");
            if(username == "client") {
                sshWriter.write("VPN_DH=\"$(cat /etc/easy-rsa/pki/dh.pem)\"\n");
                sshWriter.write("VPN_CA=\"$(openssl x509 -in /etc/easy-rsa/pki/ca.crt)\"\n");
                sshWriter.write("VPN_TC=\"$(cat /etc/easy-rsa/pki/private/client.pem)\"\n");
                sshWriter.write("VPN_KEY=\"$(cat /etc/easy-rsa/pki/private/client.key)\"\n");
                sshWriter.write("VPN_CERT=\"$(openssl x509 -in /etc/easy-rsa/pki/issued/client.crt)\"\n");
                sshWriter.write("VPN_CONF=\"/etc/openvpn/client.ovpn\"\n");
            } else {
                sshWriter.write("VPN_DH=\"$(cat /etc/easy-rsa/pki/dh.pem)\"\n");
                sshWriter.write("VPN_CA=\"$(openssl x509 -in /etc/easy-rsa/pki/ca.crt)\"\n");
                sshWriter.write("VPN_TC=\"$(cat /etc/easy-rsa/pki/private/client.pem)\"\n");
                sshWriter.write("VPN_KEY=\"$(cat /etc/openvpn/clients/" + username + "/" + username + ".key)\"\n");
                sshWriter.write("VPN_CERT=\"$(openssl x509 -in /etc/openvpn/clients/" + username +  "/" + username + ".crt)\"\n");
                sshWriter.write("VPN_CONF=\"/etc/openvpn/accounts/" + username + "/" + username + ".ovpn\"\n");
            }
            sshWriter.write("cat << EOF > ${VPN_CONF} \n");
            sshWriter.write("user nobody\n");
            sshWriter.write("group nogroup\n");
            sshWriter.write("dev tun\n");
            sshWriter.write("nobind\n");
            sshWriter.write("client\n");
            sshWriter.write("remote " + staticIp + " 1194 tcp\n");
            sshWriter.write("auth-nocache\n");
            sshWriter.write("remote-cert-tls server\n");
            sshWriter.write("cipher AES-256-GCM\n");
           sshWriter.write("auth SHA256\n");

            sshWriter.write("<tls-crypt-v2>\n");
            sshWriter.write("${VPN_TC}\n");
            sshWriter.write("</tls-crypt-v2>\n");
            sshWriter.write("<key>\n");
            sshWriter.write("${VPN_KEY}\n");
            sshWriter.write("</key>\n");
            sshWriter.write("<cert>\n");
            sshWriter.write("${VPN_CERT}\n");
            sshWriter.write("</cert>\n");
            sshWriter.write("<ca>\n");
            sshWriter.write("${VPN_CA}\n");
            sshWriter.write("</ca>\n");
            sshWriter.write("EOF\n");
            sshWriter.write("service openvpn restart\n");

            sshWriter.flush();

            sshWriter.close();

            String line;
            while ((line = sshReader.readLine()) != null) {
                System.out.println(line);
            }
            sshReader.close();

            int exitStatus = sshProcess.waitFor();
            if (exitStatus == 0) {
                System.out.println("Script executed successfully.");
                logRemote("Create OpenVPN Client: Script executed successfully.");
            } else {
                System.out.println("Script execution failed with exit status: " + exitStatus);
                logRemote("Create OpenVPN Client: Script execution failed with exit status: " + exitStatus);
            }
            System.out.println("Openvpn_4 configuration via SSH completed successfully.");
        } catch (Exception e) {
            System.out.println("Error occurred during SSH operation: " + e.getMessage());
            logRemote("Create OpenVPN Client: " + e.getMessage());
        }
    }

    public static boolean isReachable(String ipAddress) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            boolean ret = address.isReachable(3000);
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void checkFirstStep() {
        if (isReachable(Environments.routerIp2) || isReachable(Environments.routerIp1)) {
           // System.out.println("First step found" + Environments.routerIp1 + "or" + Environments.routerIp2);
            isCompletedFirstStep = true;
          //  System.out.println("true");

        } else if (!isReachable(Environments.routerIp2) && !isReachable(Environments.routerIp1)) {
            System.out.println("First step not found" + Environments.routerIp1 + "or" + Environments.routerIp2);
            isCompletedFirstStep = false;
            isCompletedSecondStep = false;
            isCompletedThirdStep = false;
            isCompletedFourthStep = false;
            isCompletedFifthStep = false;
            isCompletedSixStep = false;

            dashboard.updateFirstStepColor();
            dashboard.updateSecondStepColor();
            dashboard.updateThirdStepColor();
            dashboard.updateFourthStepColor();
            dashboard.updateFifthStepColor();
            dashboard.updateSixStepColor();
        }

        dashboard.updateFirstStepColor();
    }

    public static boolean performReachableDSL() {
        try {
            String[] com = new String[1];
            com[0] = "if [ $(cat /sys/class/net/lan2/carrier) -eq 1 ]; then echo \"true\"; else echo \"false\"; fi";
            Vector<String> res = executeCommandRemote(com);

            if (res.contains("true"))
                return true;
            else
                return false;
        } catch (Exception e) {
           System.out.println("Error occurred during SSH operation: " + e.getMessage());
            return false;
        }
    }
    public static boolean performReachableStarlink() {
        try {
            String[] com = new String[1];
            com[0] = "WDEVICE=$(uci get network.wan3.device);if [ $(cat /sys/class/net/$WDEVICE/carrier) -eq 1 ]; then echo \"true\"; else echo \"false\"; fi";
            Vector<String> res = executeCommandRemote(com);
            if (res.get(0).contains("true"))
                return true;
            else
                return false;
        } catch (Exception e) {
            // System.out.println("Error occurred during SSH operation: " + e.getMessage());
            return false;
        }

    }

    public static void checkPackageInstalled() {
        if(getPort() == "2222")
            isCompletedFourthStep= true;
        else
            isCompletedFourthStep= false;
    }

    public static void checkOpenVPNCert() {
        try {
            String[] com = new String[1];
            com[0] = "ls /etc/easy-rsa/pki/issued";
            Vector<String> res = executeCommandRemote(com);
            if (res.get(0).contains("No such file or directory")) {
                isCompletedSixStep = false;
            } else {
                isCompletedSixStep = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void installPackages() {

        try {
            installingPackages  = true;
            isCompletedFourthStep = false;
            dashboard.updateFourthStepColor();
            dashboard.setInstallStatTxt("Installing started.");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {

                        String os = System.getProperty("os.name").toLowerCase();
                        ProcessBuilder processBuilder;
                        Process sshProcess = null;
                        BufferedReader sshReader = null;

                        if (os.contains("win")) {
                            processBuilder = new ProcessBuilder("cmd.exe", "/c", "ssh -p " + getPort() + " -o StrictHostKeyChecking=no -T root@" + Environments.routerIp2);
                        } else {
                            processBuilder = new ProcessBuilder("sh", "-c", "ssh -p " + getPort() + " -o StrictHostKeyChecking=no -T root@" + Environments.routerIp2);
                        }

                        processBuilder.redirectErrorStream(true);

                        String sshLine;

                        String initCom[] = new String[3];
                        initCom[0] = "/etc/init.d/sysntpd stop";
                        initCom[1] = "ntpd -q -p pool.ntp.org";
                        initCom[2] = "/etc/init.d/sysntpd start";
                        executeCommandRemote(initCom);

                        String com[] = new String[1];

                        dashboard.setInstallStatTxt("Updating opkg");
                        com[0] = "opkg update";
                        executeCommandRemote(com);

                        dashboard.setInstallStatTxt("Installing: openvpn-openssl openvpn-easy-rsa");
                        com[0] = "opkg install openvpn-openssl openvpn-easy-rsa";
                        executeCommandRemote(com);

                        dashboard.setInstallStatTxt("Installing: pbr luci-app-pbr");
                        com[0] = "opkg install luci-app-pbr";
                        executeCommandRemote(com);

                        dashboard.setInstallStatTxt("Installing: libatomic");
                        com = new String[1];
                        com[0] = "opkg install libatomic";
                        executeCommandRemote(com);

                        dashboard.setInstallStatTxt("Installing: libcurl ca-certificates git-http git");
                        com[0] = "opkg install libcurl ca-certificates git-http git";
                        executeCommandRemote(com);

                        dashboard.setInstallStatTxt("Installing: xl2tpd");
                        com[0] = "opkg install xl2tpd";
                        executeCommandRemote(com);

                        dashboard.setInstallStatTxt("Installing: vnstat iptables jq");
                        com[0] = "opkg install vnstat iptables jq";
                        executeCommandRemote(com);

                        dashboard.setInstallStatTxt("Installing: tc-full");
                        com[0] = "opkg install tc-full";
                        executeCommandRemote(com);

                        dashboard.setInstallStatTxt("Installing: kmod-wireguard...");
                        com[0] = "opkg install kmod-wireguard wireguard-tools  luci-proto-wireguard";
                        executeCommandRemote(com);

                        com[0] = "/etc/init.d/troubleshoot enable";
                        executeCommandRemote(com);
                        com = new String[3];
                        dashboard.setInstallStatTxt("Installing: kmod-dummy");
                        com[0] = "opkg install kmod-dummy";
                        com[1] = "modprobe dummy";
                        com[2] = "echo \"dummy\" >> /etc/modules.d/dummy";
                        executeCommandRemote(com);


                        com = new String[2];

                        dashboard.setInstallStatTxt("Installing: https-dns-proxy");
                        com[0] = "opkg install https-dns-proxy";
                        com[1] = "/etc/init.d/https-dns-proxy enable";
                        executeCommandRemote(com);


                        com = new String[1];

                        dashboard.setInstallStatTxt("Installing: logrotate");
                        com[0] = "opkg install logrotate";
                        executeCommandRemote(com);

                        dashboard.setInstallStatTxt("Adding logrotate.conf file");
                        com[0] = "echo \\\"/var/log/*.log {\\n\"+\n" +
                                "                        \"size 1M\\n\"+\n" +
                                "                        \"rotate 5\\n\"+\n" +
                                "                        \"missingok\\n\"+\n" +
                                "                        \"compress\\n\"+\n" +
                                "                        \"delaycompress\\n\"+\n" +
                                "                        \"notifempty\\n\"+\n" +
                                "                        \"create 640 root root\\n\"+\n" +
                                "                        \"}\\\" > /etc/logrotate.conf";
                        executeCommandRemote(com);

                        String localpath;
                        if (os.contains("win")) {
                            processBuilder = new ProcessBuilder("cmd.exe", "/c", "echo %username%");
                            sshProcess = processBuilder.start();

                            sshReader = new BufferedReader(new InputStreamReader(sshProcess.getInputStream()));

                            String username = "";

                            while ((sshLine = sshReader.readLine()) != null) {
                                username = sshLine;
                            }

                            sshProcess.waitFor();
                            sshReader.close();

                            localpath = "C:\\Users\\" + username + "\\.ssh\\id_ed25519";
                        } else {
                            localpath = "~/.ssh/id_ed25519";
                        }

                        dashboard.setInstallStatTxt("Checking if there is ssh public keys.");
                        File keyFile = new File(localpath);
                        if (keyFile.exists()) {
                            System.out.println("Existing SSH key found. Deleting...");
                            if (!keyFile.delete()) {
                                System.err.println("Failed to delete the existing SSH key file: " + localpath);
                                return; // Exit if the file could not be deleted
                            }
                        }

                        dashboard.setInstallStatTxt("Generating ssh public keys.");
                        if (os.contains("win")) {
                            processBuilder = new ProcessBuilder("cmd.exe", "/c", "ssh-keygen -t ed25519 -o -a 100 -f \"" + localpath + "\" -N \"\"");
                        } else {
                            processBuilder = new ProcessBuilder("sh", "-c", "rm " + localpath + " -N \"\"");
                            sshProcess = processBuilder.start();
                            processBuilder = new ProcessBuilder("sh", "-c", "ssh-keygen -t ed25519 -o -a 100 -f " + localpath + " -N \"\"");
                        }

                        sshProcess = processBuilder.start();

                        sshProcess.waitFor();

                        dashboard.setInstallStatTxt("Transferring ssh public key.");
                        localpath = localpath + ".pub";
                        String remotepath = "/etc/dropbear/authorized_keys";
                        sendFile2Remote(localpath, remotepath);

                        System.out.println("Successfully transferred ===========>>>>");
                        dashboard.setInstallStatTxt("Setting up dropbear.");

                        String com2[] = new String[7];
                        com2[0] = "uci set dropbear.@dropbear[0].PasswordAuth='off'";
                        com2[1] = "uci set dropbear.@dropbear[0].RootPasswordAuth='off'";
                        com2[2] = "uci set dropbear.@dropbear[0].Port='2222'";
                        com2[3] = "uci set dropbear.@dropbear[0].IdleTimeout='300'";
                        com2[4] = "uci set dropbear.@dropbear[0].Interface='lan'";
                        com2[5] = "uci commit dropbear";
                        com2[6] = "reboot";
                        executeCommandRemote(com2);

                        dashboard.setInstallStatTxt("");
                        showAlert("Package installation is done and your router will be restarted.", "Package Installation");

                        System.out.println("Packages installed  successfully.");
                        logRemote("Packages install: true");
                        isCompletedFourthStep = true;
                    } catch (Exception e) {
                        dashboard.setInstallStatTxt("Failed: " + dashboard.getInstallStatTxt()+e.getMessage());
                        showAlert("Package installation is not completed.", "Package Installation");

                        System.out.println("Error occurred during SSH operation: " + e.getMessage());
                        logRemote("Packages install: " + e.getMessage());
                        isCompletedFourthStep = false;
                    }
                    installingPackages = false;
                    dashboard.updateFourthStepColor();
                }
            };
            thread.start();


        } catch (Exception e) {
            System.out.println("Error occurred during SSH operation: " + e.getMessage());
            logRemote("Packages install: " + e.getMessage());
        }
    }

    public static boolean configureOpenvpnSSH_1() {
        Process sshProcess = null;
        BufferedReader sshReader = null;
        BufferedWriter sshWriter = null;
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder sshBuilder;

        isConfiguringVPNServer = true;
        isCompletedSixStep = false;
        dashboard.updateSixStepColor();

        try {
            if (os.contains("win")) {
                sshBuilder = new ProcessBuilder("cmd.exe", "/c", "ssh -p " + getPort() + " -o StrictHostKeyChecking=no -T root@" + Environments.routerIp2);
            } else {
                sshBuilder = new ProcessBuilder("sh", "-c", "ssh -p " + getPort() + " -o StrictHostKeyChecking=no -T root@" + Environments.routerIp2);
            }
            sshBuilder.redirectErrorStream(true);
            sshProcess = sshBuilder.start();

            // Get input and output streams for SSH process
            sshReader = new BufferedReader(new InputStreamReader(sshProcess.getInputStream()));
            sshWriter = new BufferedWriter(new OutputStreamWriter(sshProcess.getOutputStream()));

            // Read the output of SSH command
            String sshLine;

            while ((sshLine = sshReader.readLine()) != null) {
                System.out.println("configuaring openvpn server..." + sshLine);
                if (sshLine.contains("--------------------------------------------------")) {
                    break; // Assuming "Last login" indicates that SSH connection is ready for further commands
                }
            }

            String[] com = new String[10];
            com[0] = "easyrsa init-pki";
            com[1] = "openssl dhparam -out dh1024.pem 1024";
            com[2] = "mv /root/dh1024.pem /etc/easy-rsa/pki/dh.pem";
            com[3] = "easyrsa build-ca nopass; echo 'success'1";
            com[4] = "easyrsa build-server-full server nopass; echo 'success'1";
            com[5] = "openvpn --genkey tls-crypt-v2-server /etc/easy-rsa/pki/private/server.pem";
            com[6] = "easyrsa build-client-full client nopass; echo 'success'1";
            com[7] = "easyrsa gen-crl; echo 'success'1";
            com[8] = "cp /etc/easy-rsa/pki/crl.pem /etc/openvpn/crl.pem";
            com[9] = "vnstat -u -i lan2";

            Map<Integer, Vector<String>> lastReps = new HashMap<>();
            Map<Integer, Map<String, String>> prompts = new HashMap<>();
            Map<Integer, Map<String, String>> passwords = new HashMap<>();

            Map<String, String> pwd = new HashMap<>();
            pwd.put("Enter PEM pass phrase:", "1234567890");
            pwd.put("Verifying - Enter PEM pass phrase:", "1234567890");
            passwords.put(3, pwd);
            Vector<String> reps = new Vector<>();
            reps.add("success1");
            lastReps.put(3, reps);

            pwd = new HashMap<>();
            pwd.put("Enter pass phrase for /etc/easy-rsa/pki/private/ca.key:", "1234567890");
            passwords.put(4, pwd);
            reps = new Vector<>();
            reps.add("success1");
            lastReps.put(4, reps);

            pwd = new HashMap<>();
            pwd.put("Enter pass phrase for /etc/easy-rsa/pki/private/ca.key:", "1234567890");
            passwords.put(6, pwd);
            reps = new Vector<>();
            reps.add("success1");
            lastReps.put(6, reps);

            pwd = new HashMap<>();
            pwd.put("Enter pass phrase for /etc/easy-rsa/pki/private/ca.key:", "1234567890");
            passwords.put(7, pwd);
            reps = new Vector<>();
            reps.add("success1");
            lastReps.put(7, reps);

            advancedRemoteControl(com, lastReps, prompts, passwords);

            com = new String[1];
            com[0] = "openvpn --tls-crypt-v2 /etc/easy-rsa/pki/private/server.pem --genkey tls-crypt-v2-client /etc/easy-rsa/pki/private/client.pem";
            try {
                executeCommandRemote(com);
            } catch (Exception e) {
                e.printStackTrace();
            }
/** Openvpn server.conf generating */
/*
            sshWriter.flush();

            Thread.sleep(1000 * 20);*/
            sshWriter.write("umask go=\n");
            sshWriter.write("VPN_DH=\"$(cat /etc/easy-rsa/pki/dh.pem)\"\n");
            sshWriter.write("VPN_CA=\"$(openssl x509 -in /etc/easy-rsa/pki/ca.crt)\"\n");
            sshWriter.write("VPN_TC=\"$(cat /etc/easy-rsa/pki/private/server.pem)\"\n");
            sshWriter.write("VPN_KEY=\"$(cat /etc/easy-rsa/pki/private/server.key)\"\n");
            sshWriter.write("VPN_CERT=\"$(openssl x509 -in /etc/easy-rsa/pki/issued/server.crt)\"\n");
            sshWriter.write("VPN_CONF=\"/etc/openvpn/server.conf\"\n");
            sshWriter.write("cat << EOF > ${VPN_CONF} \n");
            sshWriter.write("user nobody\n");
            sshWriter.write("group nogroup\n");
            sshWriter.write("dev tun\n");
         //   sshWriter.write("cipher AES-128-CBC\n");
            sshWriter.write("port 1194\n");
            sshWriter.write("proto tcp\n");
            sshWriter.write("server 192.168.9.0 255.255.255.0\n");
            sshWriter.write("topology subnet\n");
            sshWriter.write("client-to-client\n");
            sshWriter.write("keepalive 10 60\n");
            sshWriter.write("persist-tun\n");
            sshWriter.write("persist-key\n");
            sshWriter.write("push \"dhcp-option DNS 192.168.9.1\"\n");
            sshWriter.write("push \"dhcp-option DOMAIN lan\"\n");
            sshWriter.write("push \"redirect-gateway def1\"\n");
            sshWriter.write("push \"persist-tun\"\n");
            sshWriter.write("push \"persist-key\"\n");
            sshWriter.write("crl-verify /etc/openvpn/crl.pem\n");
            sshWriter.write("client-config-dir /etc/openvpn/ccd\n");
            sshWriter.write("cipher AES-256-GCM\n");
            sshWriter.write("auth SHA256\n");
            sshWriter.write("tls-version-min 1.2\n");
          //  sshWriter.write("tls-cipher TLS-ECDHE-ECDSA-WITH-AES-256-GCM-SHA384: TLS-ECDHE-RSA-WITH-AES-256-GCM-SHA384\n");
            sshWriter.write("<dh>\n");
            sshWriter.write("${VPN_DH}\n");
            sshWriter.write("</dh>\n");
            sshWriter.write("<tls-crypt-v2>\n");
            sshWriter.write("${VPN_TC}\n");
            sshWriter.write("</tls-crypt-v2>\n");
            sshWriter.write("<key>\n");
            sshWriter.write("${VPN_KEY}\n");
            sshWriter.write("</key>\n");
            sshWriter.write("<cert>\n");
            sshWriter.write("${VPN_CERT}\n");
            sshWriter.write("</cert>\n");
            sshWriter.write("<ca>\n");
            sshWriter.write("${VPN_CA}\n");
            sshWriter.write("</ca>\n");

            // Close SSH streams
            sshWriter.close();
            sshReader.close();

            // Wait for SSH process to finish
            try {
                sshProcess.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }

            isCompletedSixStep = true;
            isConfiguringVPNServer = false;
            dashboard.updateSixStepColor();

            logRemote("Create OpenVPN Server: true");
            com = new String[2];
            com[0] = "/etc/init.d/troubleshoot start";
            com[1] = "reboot";
            try {
                executeCommandRemote(com);
            }catch (Exception e) {
            e.printStackTrace();
        }
            System.out.println("*************** Openvpn server is created successfully *******************");
            showAlert("Openvpn server is created successfully. Router will reboot.", "Create OpenVPN Server");
            return true;
        } catch (IOException e) {
            logRemote("Create OpenVPN Server: " + e.getMessage());
            System.out.println("Error occurred during SSH operation: " + e.getMessage());
            return false;
        }
    }
    public static void configureOpenvpnSSH() {
        isConfiguringVPNServer = true;
        isCompletedSixStep = false;
        dashboard.updateSixStepColor();

        boolean result = configureOpenvpnSSH_1();
        System.out.println("____________________ OPEN VPN SERVER IS CREATED SUCCESSFULLY! ___________________");
        isCompletedSixStep = result;
        dashboard.updateSixStepColor();
    }

    public static String getLocalFile(String filename){
        try {
            File jarFile = new File(ConnectionService.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File appBundle = jarFile.getParentFile();
            File resourcesDir= new File(appBundle, "AccessVPN");
            String os = System.getProperty("os.name").toLowerCase();
            String path;
            if (os.contains("win")) {
                path = "./AccessVPN/"+filename;
            } else {
                path = new File(resourcesDir, filename).getAbsolutePath();
            }
            return path;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean sendBackupFileFunc() {
        String localFile = null;
        try {
            // Get the path to the running .app bundle
            localFile= getLocalFile("backup-openwrt.tar.gz");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String remoteFile = "/tmp/backup-openwrt.tar.gz";

        String command = "ssh -o StrictHostKeyChecking=no root@" + Environments.routerIp1 +  " \"tee /tmp/backup-openwrt.tar.gz\" < "+localFile;

        try {
            ProcessBuilder pb;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                pb = new ProcessBuilder("sh", "-c", command);
            }

            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("File transferred successfully to " + remoteFile);
                return true;
            } else {
                System.out.println("Failed to transfer file. Exit code: " + exitCode);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void restoreBackup() {
        if (isCompletedFirstStep) {
            resortBackupFile();
        }
    }

    public static boolean logRemote(String message) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String datetime = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(new Date());
        String[] com = new String[2];
        com[0] = "mkdir -m 0777 -p /etc/openvpn/logs";
        com[1] = "echo \"" + datetime + ":" + message + "\" >> \"/etc/openvpn/logs/" + date + ".log\"";
        try {
            ConnectionService.executeCommandRemote(com);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String getPort() {
        return isSecureConnection ? "2222" : "22";
    }

    // Remote Command Execution
    public static Vector<String> executeCommandRemote(String[] commands) throws Exception {

        Vector<String> reslist = new Vector<>();
        int exitCode = 0;
        try {
            String[] sshCommand = {
                    "ssh",
                    "-p", getPort(),
                    "-o StrictHostKeyChecking=no",
                    "root@" + Environments.routerIp2,
                    String.join(";", commands)
            };
            ProcessBuilder sshBuilder = new ProcessBuilder(sshCommand);
            sshBuilder.redirectErrorStream(true);
            Process sshProcess = sshBuilder.start();
            BufferedReader sshReader = new BufferedReader(new InputStreamReader(sshProcess.getInputStream()));

            String sshLine;
            while ((sshLine = sshReader.readLine()) != null) {
               // System.out.println("Read from SSH: " + sshLine);
                reslist.add(sshLine);
            }

            exitCode = sshProcess.waitFor();
         //   System.out.println("Exited with error code: " + exitCode);

            sshReader.close();

            System.out.println("SSH command execution end with code " + exitCode + ": " + commands[0]);
            if (commands.length > 1)
                System.out.println("SSH command execution end with code " + exitCode + ": " + commands[1]);

            if (exitCode == 255) isSecureConnection = !isSecureConnection;

        } catch (Exception e) {
            System.out.println("Error occurred during SSH operation: " + e.getMessage());
            isSecureConnection = !isSecureConnection;
        }
        if (exitCode != 0)
            throw new Exception("SSH command execution end with code " + exitCode + ": " + commands[0]);
        return reslist;
    }

    public  static void currentStatus() {
        System.out.println("Connect Openwrt Lan1 port to your computer: " + isCompletedFirstStep);
        if(!isCompletedFirstStep) {
            if(isReachable(Environments.routerIp2)){
                isCompletedFirstStep = true;
                dashboard.updateFirstStepColor();
            }
        } else {
            if(!isReachable(Environments.routerIp2)){
                isCompletedFirstStep = false;
                dashboard.updateFirstStepColor();
            } else {
                isCompletedFirstStep = true;
                dashboard.updateFirstStepColor();
            }
        }
        System.out.println("Configure Router: " + isCompletedSecondStep);
        if(!isCompletedSecondStep) {
            if(isReachable(Environments.routerIp2)) {
                isCompletedSecondStep = true;
                dashboard.updateSecondStepColor();
            }
        } else {
            if(!isReachable(Environments.routerIp2)) {
                isCompletedSecondStep = false;
                dashboard.updateSecondStepColor();
            } else {
                isCompletedSecondStep = true;
                dashboard.updateSecondStepColor();
            }
        }

        System.out.println("Connect Your Starlink: " + isCompletedThirdStep);

        if(!isCompletedThirdStep) {
            if(performReachableStarlink()) {
                isCompletedThirdStep = true;
                dashboard.updateThirdStepColor();
            }
        } else {
                isCompletedThirdStep = false;
                dashboard.updateThirdStepColor();
        }

        System.out.println("Install Packages: " + isCompletedFourthStep);

        if(isCompletedFirstStep && !isCompletedFourthStep) {
            try {
                String[] com = new String[1];
                com[0] = "easyrsa";
                Vector<String> res = executeCommandRemote(com);
                if (res.get(0).contains("not found")) {
                    isCompletedFourthStep = false;
                    dashboard.updateFourthStepColor();
                } else {
                    isCompletedFourthStep = true;
                    dashboard.updateFourthStepColor();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!isCompletedFirstStep && isCompletedFourthStep) {
            isCompletedFourthStep = false;
            dashboard.updateFourthStepColor();
        } else if (!isCompletedFirstStep && !isCompletedFourthStep) {
            isCompletedFourthStep = false;
            dashboard.updateFourthStepColor();
        }

//        System.out.println(isCompletedFifthStep);
 //       System.out.println("Connect StarLink: " + isCompletedSixStep);
        System.out.println("Connect Your DSL router to Openwrt Lan2: " + isCompletedFifthStep);
        if(!isCompletedSixStep) {
            if(performReachableDSL()) {
                isCompletedFifthStep = true;
                dashboard.updateFifthStepColor();
            } else {
                isCompletedSixStep = false;
                dashboard.updateFifthStepColor();
            }
        } else {
            if(!performReachableDSL()) {
                isCompletedFifthStep = false;
                dashboard.updateFifthStepColor();
            } else {
                isCompletedFifthStep = true;
                dashboard.updateFifthStepColor();
            }
        }
   //     System.out.println("Create OpenVpn server: " + isCompletedSixStep);
    }

    private static String vpnQuota(String ipbws) {
        return "#!/bin/sh /etc/rc.common\n" +
                "START=99\n" +
                "start() {\n" +
                "    # Define user IPs and quotas (in bytes)\n" +
                "    USERS=\"" + ipbws + "\"\n" +
                "    for USER in $USERS; do\n" +
                "        IP=$(echo $USER | cut -d: -f1)\n" +
                "        QUOTA=$(echo $USER | cut -d: -f2)\n" +
                "        # Get total usage for the IP\n" +
                "        RX_BYTES=$(iptables -L FORWARD -v -x -n | awk -v ip=$IP '$9 == ip {sum += $2} END {print sum}')\n" +
                "        TX_BYTES=$(iptables -L FORWARD -v -x -n | awk -v ip=$IP '$8 == ip {sum += $2} END {print sum}')\n" +
                "        USAGE=$(((RX_BYTES + TX_BYTES)/1024))\n" +
                "        # Check if REJECT rules are already in place\n" +
                "        iptables -C FORWARD -s $IP -j REJECT 2>/dev/null\n" +
                "        REJECT_SRC_EXIST=$?\n" +
                "        iptables -C FORWARD -d $IP -j REJECT 2>/dev/null\n" +
                "        REJECT_DST_EXIST=$?\n" +
                "        if [ \"$USAGE\" -ge \"$QUOTA\" ]; then\n" +
                "            # Block user traffic if quota is exceeded\n" +
                "            iptables -C FORWARD -s $IP -j REJECT 2>/dev/null\n" +
                "            REJECT_SRC_EXIST=$?\n" +
                "            iptables -C FORWARD -d $IP -j REJECT 2>/dev/null\n" +
                "            REJECT_DST_EXIST=$?\n" +
                "            if [ $REJECT_SRC_EXIST -ne 0 ]; then\n" +
                "                iptables -I FORWARD -s $IP -j REJECT\n" +
                "            fi\n" +
                "            if [ $REJECT_DST_EXIST -ne 0 ]; then\n" +
                "                iptables -I FORWARD -d $IP -j REJECT\n" +
                "            fi\n" +
                "            logger -t vpn-quota \"User $IP has reached the quota limit. Blocking traffic.\"\n" +
                "        else\n" +
                "            # Remove existing REJECT rules if quota is not exceeded\n" +
                "            iptables -C FORWARD -s $IP -j REJECT 2>/dev/null\n" +
                "            if [ $? -eq 0 ]; then\n" +
                "                iptables -D FORWARD -s $IP -j REJECT\n" +
                "            fi\n" +
                "            iptables -C FORWARD -d $IP -j REJECT 2>/dev/null\n" +
                "            if [ $? -eq 0 ]; then\n" +
                "                iptables -D FORWARD -d $IP -j REJECT\n" +
                "            fi\n" +
                "            logger -t vpn-quota \"User $IP has not reached the quota limit. Allowing traffic.\"\n" +
                "            iptables-save > /etc/openvpn/iptables/rules.v4\n"+
                "        fi\n" +
                "    done\n" +
                "}\n";
    }

    public static void configureBandWidthEnvironment () {
        try {

            // Send Script
            String localFilePath2 = getLocalFile("vpnquota.sh");
            String remoteFilePath2 = "/etc/init.d/vpn-quota";
            
            writeAndSend2Remote(localFilePath2, remoteFilePath2, vpnQuota(""));

           //  Run Script
            String[] com1 = new String[3];
            com1[0] = "chmod +x " + remoteFilePath2;
            com1[1] = "cat /dev/null >> /etc/openvpn/iplist";
            com1[2] = "chmod 0777 /etc/openvpn/iplist";
            executeCommandRemote(com1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static int assignNewIp(String bandwidth) {
        try {
            String remoteIptableFilePath = "/etc/openvpn/iplist";

            String[] com = new String[1];
            com[0] = "cat " + remoteIptableFilePath;
            Vector<String> iplist = executeCommandRemote(com);
            if (iplist.size() > 255) return 0;
            int i = 0;
            int orgcnt = iplist.size();
            for (i = 0; i < orgcnt; i++) {
                int lastip = Integer.parseInt(iplist.get(i).split(":")[0].split("\\.")[3]);
                if (i + 2 < lastip) {
                    iplist.insertElementAt("192.168.9." + (i + 2) + ":" + bandwidth, i);
                    break;
                }
            }
            if (i == orgcnt) {
                iplist.insertElementAt("192.168.9." + (i + 2) + ":" + bandwidth, i);
            }
            String localIptableFilePath = getLocalFile("iptable");
            StringBuffer newIps = new StringBuffer(iplist.get(0));
            for (int j = 1; j < iplist.size(); j++) {
                newIps.append("\n" + iplist.get(j));
            }

            if (writeAndSend2Remote(localIptableFilePath, remoteIptableFilePath, newIps.toString())) {
                updateNetworkEnv();
                return i + 2;
            }
            else
                return 0;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public static boolean updateAssignedIp(String ip, String bandwidth) {
        try {
            String remoteIptableFilePath = "/etc/openvpn/iplist";

            String[] com = new String[1];
            com[0] = "cat " + remoteIptableFilePath;
            Vector<String> iplist = executeCommandRemote(com);
            int orgcnt = iplist.size();
            for (int i = 0; i < orgcnt; i++) {
                if (iplist.get(i).split(":")[0].equals(ip)) {
                    iplist.set(i, ip + ":" + bandwidth);
                    break;
                }
            }

            String localIptableFilePath = getLocalFile("iptable");
            StringBuffer newIps = new StringBuffer(iplist.get(0));
            for (int i = 1; i < iplist.size(); i++) {
                newIps.append("\n" + iplist.get(i));
            }

            if (writeAndSend2Remote(localIptableFilePath, remoteIptableFilePath, newIps.toString())) {
                updateNetworkEnv();
                return true;
            }
            else
                return false;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean deleteAssignedIp(String ip) {
        try {
            String remoteIptableFilePath = "/etc/openvpn/iplist";

            String[] com = new String[1];
            com[0] = "cat " + remoteIptableFilePath;

            Vector<String> iplist = executeCommandRemote(com);
            int orgcnt = iplist.size();
            for (int i = 0; i < orgcnt; i++) {
                if (iplist.get(i).split(":")[0].equals(ip)) {
                    iplist.remove(i);
                    break;
                }
            }

            String localIptableFilePath = getLocalFile("iptable");
            StringBuffer newIps;
            if (iplist.size() > 0) {
                newIps = new StringBuffer(iplist.get(0));
                for (int i = 1; i < iplist.size(); i++) {
                    newIps.append("\n" + iplist.get(i));
                }
            } else {
                newIps = new StringBuffer();
            }

            if (writeAndSend2Remote(localIptableFilePath, remoteIptableFilePath, newIps.toString())) {
                updateNetworkEnv();
                return true;
            }
            else
                return false;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void updateNetworkEnv() {
        try {
            String remoteIptableFilePath = "/etc/openvpn/iplist";
            String localFilePath2 = getLocalFile("vpnquota.sh");
            String remoteFilePath2 = "/etc/init.d/vpn-quota";

            String[] com = new String[1];
            com[0] = "cat " + remoteIptableFilePath;
            Vector<String> iplist = executeCommandRemote(com);

            StringBuffer newIps = new StringBuffer(iplist.get(0).split(":")[0]);
            for (int i = 1; i < iplist.size(); i++) {
                newIps.append(" " + iplist.get(i).split(":")[0]);
            }

            newIps = new StringBuffer(iplist.get(0));
            for (int i = 1; i < iplist.size(); i++) {
                newIps.append(" " + iplist.get(i));
            }
            System.out.println(vpnQuota(newIps.toString()));
            writeAndSend2Remote(localFilePath2, remoteFilePath2, vpnQuota(newIps.toString()));

            // Run Script
            String[] com1 = new String[2];
            com1[0] = "chmod +x " + remoteFilePath2;
            //com1[1] = "echo * * * * * " + remoteFilePath2 + " start";
            com1[1] = remoteFilePath2 + " start";
            executeCommandRemote(com1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /*public static boolean writeAndSend2Remote(String remoteFilePath, String content) {

        try {
            // Transfer script to remote
           content = content.replace("\"", "\\\"");

            String[] com = new String[1];
            com[0] = " echo \"" + content + "\" > "+remoteFilePath;
            System.out.println(com[0]);
            ConnectionService.executeCommandRemote(com);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }*/

    public static boolean sendFile2Remote(String localFilePath, String remoteFilePath) {
        try {
            // Transfer script to remote
            String command = "ssh -p " + getPort() + " -o StrictHostKeyChecking=no root@" + Environments.routerIp2 +  " \"tee " + remoteFilePath + "\" < "+localFilePath;

            ProcessBuilder pb;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                pb = new ProcessBuilder("sh", "-c", command);
            }

            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("File transferred successfully to " + remoteFilePath);
                return true;
            } else {
                System.out.println("Failed to transfer file. Exit code: " + exitCode);
                return false;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean writeAndSend2Remote(String localFilePath, String remoteFilePath, String content) {
        try {
            FileWriter fw = new FileWriter(localFilePath);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            return false;
        }

        boolean ret = sendFile2Remote(localFilePath, remoteFilePath);
        File file = new File(localFilePath);
        file.delete();
        return ret;
    }

    public static boolean interactRemoteShell(String command, Map<String, String> prompts) {
        ProcessBuilder processBuilder = new ProcessBuilder("ssh", "-p", getPort(), "-tt", "root@" + Environments.routerIp2);

        // Prepared answers for the prompts
        boolean flag = false;
        try {
            Process process = processBuilder.start();

            // Get input and output streams of the process
            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader processErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            BufferedWriter processInputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            // Thread to handle error stream
            Thread errorThread = new Thread(() -> {
                try {
                    String errorLine;
                    while ((errorLine = processErrorReader.readLine()) != null) {
                        System.out.println("Process Error: " + errorLine);
                        // Terminate the process if any error occurs
                        process.destroy();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            errorThread.start();

            // Write the initial command to the process
            processInputWriter.write(command + "\n");
            processInputWriter.flush();

            // Read the process output
            StringBuilder outputBuffer = new StringBuilder();
            int ch;
            while (true) {
                ch = processOutputReader.read();
                System.out.print((char) ch); // Print the character to standard output
                outputBuffer.append((char) ch);

                // Check if the output contains any prompts
                for (Map.Entry<String, String> entry : prompts.entrySet()) {
                    if (outputBuffer.toString().contains(entry.getKey())) {
                        System.out.println(">>Prompted to " + entry.getKey());
                        String response = entry.getValue();
                        processInputWriter.write(response + "\n");
                        processInputWriter.flush();
                        System.out.println("Responded to " + entry.getKey() + " with " + response);
                        outputBuffer.setLength(0); // Clear the buffer after responding
                    }
                }
                if (outputBuffer.toString().contains("Enter pass phrase for /etc/easy-rsa/pki/private/ca.key:")) {
                    System.out.println(">>Enter pass ");
                    processInputWriter.write((int) '1');
                    processInputWriter.flush();
                    processInputWriter.write((int) '2');
                    processInputWriter.flush();
                    processInputWriter.write((int) '3');
                    processInputWriter.flush();
                    processInputWriter.write((int) '4');
                    processInputWriter.flush();
                    processInputWriter.write((int) '5');
                    processInputWriter.flush();
                    processInputWriter.write((int) '6');
                    processInputWriter.flush();
                    processInputWriter.write((int) '7');
                    processInputWriter.flush();
                    processInputWriter.write((int) '8');
                    processInputWriter.flush();
                    processInputWriter.write((int) '9');
                    processInputWriter.flush();
                    processInputWriter.write((int) '0');
                    processInputWriter.flush();
                    processInputWriter.write((int) '!');
                    processInputWriter.flush();
                    processInputWriter.write((int) '@');
                    processInputWriter.flush();
                    processInputWriter.write((int) '#');
                    processInputWriter.flush();
                    processInputWriter.write((int) '$');
                    processInputWriter.flush();
                    processInputWriter.write((int) '%');
                    processInputWriter.flush();
                    processInputWriter.write((int) '^');
                    processInputWriter.flush();
                    processInputWriter.write((int) '&');
                    processInputWriter.flush();
                    processInputWriter.write((int) '*');
                    processInputWriter.flush();
                    processInputWriter.write((int) '(');
                    processInputWriter.flush();
                    processInputWriter.write((int) ')');
                    processInputWriter.flush();
                    processInputWriter.write((int) '\n');
                    processInputWriter.flush();
                    System.out.println("Responded to Enter pass with " + "1234...\n");
                    outputBuffer.setLength(0); // Clear the buffer after responding
                }
                if (outputBuffer.toString().contains("have been generated and copied to /etc/openvpn/clients")) {
                    flag = true;
                    break;
                }
            }
            System.out.println("Process completed");

            process.destroy();
            System.out.println("Process exited with code: " + flag);

            // Close streams
            processOutputReader.close();
            processErrorReader.close();
            processInputWriter.close();

        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    public static void troubleShoot() {
        try {
            if(isCompletedFifthStep){
                String[] com = new String[4];
                com[0] = "uci set network.wan3.defaultroute='0'";
                com[1] = "uci set network.WAN2.defaultroute='1'";
                com[2] = "uci commit network";
                com[3] = "reboot";
                executeCommandRemote(com);
            }


        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void split() {
        try {
                String[] com = new String[3];
                com[0] = "uci set pbr.@policy[0].interface='WAN2'";
                com[1] = "uci commit pbr";
                com[2] = "reboot";
                executeCommandRemote(com);
                showAlert("Rebooting now. Please wait a moment.", "Choose Split");
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void block() {
        try {
                String[] com = new String[3];
                com[0] = "uci set pbr.@policy[0].interface='blackhole'";
                com[1] = "uci commit pbr";
                com[2] = "reboot";
                executeCommandRemote(com);
                showAlert("Rebooting now. Please wait a moment.", "Choose Block");
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static boolean advancedRemoteControl(String[] commands, Map<Integer, Vector<String>> lastResps, Map<Integer, Map<String, String>> prompts, Map<Integer, Map<String, String>> passwords) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("ssh", "-p", getPort(), "-tt", "root@" + Environments.routerIp2);

        // Prepared answers for the prompts
        boolean flag = false;
        try {
            Process process = processBuilder.start();

            // Get input and output streams of the process
            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader processErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            BufferedWriter processInputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            // Thread to handle error stream
            Thread errorThread = new Thread(() -> {
                try {
                    String errorLine;
                    while ((errorLine = processErrorReader.readLine()) != null) {
                        System.out.println("Process Error: " + errorLine);
                        // Terminate the process if any error occurs
                        process.destroy();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            errorThread.start();

            StringBuilder outputBuffer = new StringBuilder();

            for (int i = 0; i < commands.length; i++) {
                String command = commands[i];
                // Write the initial command to the process
                System.out.println(">>Wrote command: " + command);
                processInputWriter.write(command + "\n");
                processInputWriter.flush();

                if (prompts.containsKey(i) || passwords.containsKey(i)) {
                    // Read the process output
                    int ch;
                    while (true) {
                        ch = processOutputReader.read();
                        System.out.print((char) ch); // Print the character to standard output
                        outputBuffer.append((char) ch);

                        // Check if the output contains any prompts
                        if (prompts.containsKey(i)) {
                            for (Map.Entry<String, String> entry : prompts.get(i).entrySet()) {
                                if (outputBuffer.toString().contains(entry.getKey())) {
                                    System.out.println(">>Prompted to " + entry.getKey());
                                    String response = entry.getValue();
                                    processInputWriter.write(response + "\n");
                                    processInputWriter.flush();
                                    System.out.println("Responded to " + entry.getKey() + " with " + response);
                                    outputBuffer.setLength(0); // Clear the buffer after responding
                                }
                            }
                        }
                        // Check if the output contains any password prompts
                        if (passwords.containsKey(i)) {
                            boolean flag3 = false;
                            for (Map.Entry<String, String> entry : passwords.get(i).entrySet()) {
                                if (outputBuffer.toString().contains(entry.getKey())) {
                                    System.out.println(">>Enter pass to " + entry.getKey());
                                    String response = entry.getValue();
                                    for (char c : response.toCharArray()) {
                                        processInputWriter.write((int) c);
                                        processInputWriter.flush();
                                    }
                                    processInputWriter.write("\n");
                                    processInputWriter.flush();
                                    System.out.println("Entered pass to " + entry.getKey() + " with " + response);
                                    outputBuffer.setLength(0); // Clear the buffer after responding

                                    if (lastResps.get(i).get(0).isEmpty()) {
                                        System.out.println("Entered last line: EMPTY-EOF");
                                        flag3 = true;
                                        break;
                                    }
                                }
                            }
                            if (flag3)
                                break;
                        }
                        boolean flag2 = false;
                        // Check if the output contains any end lines
                        for (String entry : lastResps.get(i)) {
                            if (!entry.isEmpty() && outputBuffer.toString().contains(entry)) {
                                System.out.println("Entered last line: " + entry);
                                flag = true;
                                flag2 = true;
                                break;
                            }
                        }
                        if (flag2)
                            break;
                    }
                }
                outputBuffer.setLength(0);
                System.out.println("Process completed");
            }

            process.destroy();
            System.out.println("Process exited with code: " + flag);

            // Close streams
            processOutputReader.close();
            processErrorReader.close();
            processInputWriter.close();

        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
            throw e;
        }
        return flag;
    }
}


