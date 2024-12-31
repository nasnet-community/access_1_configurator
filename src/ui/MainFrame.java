package ui;

import ui.components.RoundedPanel;
import ui.components.StyledButton;
import utils.ConnectionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {
    private DashboardPanel dashboardPanel;
    private JPanel content;
    private AccountPanel accountPanel;


    public MainFrame() {
        setTitle("Access VPN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel sidebar = new RoundedPanel(20);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBackground(new Color(0, 255, 255));

        JLabel logoLabel = new JLabel("Access VPN");
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        logoLabel.setBorder(new EmptyBorder(16, 6, 16, 6));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator divider = new JSeparator(SwingConstants.HORIZONTAL);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));

        StyledButton dashboardBtn = new StyledButton("Dashboard");
        StyledButton accountsBtn = new StyledButton("Accounts");
        StyledButton addAccountBtn = new StyledButton("Add Account");

        JPanel sidebarBtnPanel = new JPanel();
        sidebarBtnPanel.setLayout(new BoxLayout(sidebarBtnPanel, BoxLayout.Y_AXIS));
        sidebarBtnPanel.setOpaque(true);
        sidebarBtnPanel.setBackground(new Color(0, 0, 0, 0));
        sidebarBtnPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        sidebarBtnPanel.add(divider);
        sidebarBtnPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarBtnPanel.add(dashboardBtn);
        dashboardBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(content.getComponent(2) + "okokokok" + accountPanel);
                if (content.getComponent(2) == accountPanel) {
                    content.remove(accountPanel);
                    content.add(dashboardPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                }
                super.mouseClicked(e);
            }
        });
        sidebarBtnPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarBtnPanel.add(accountsBtn);
        accountsBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(content.getComponent(2) + "okokokok" + dashboardPanel);
                if (content.getComponent(2) == dashboardPanel) {
                    content.remove(dashboardPanel);
                    content.add(accountPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                    if (!ConnectionService.isAccessPanelInited) {
                        ConnectionService.isAccessPanelInited = true;
                        SwingUtilities.invokeLater(() -> {
                            accountPanel.refreshList();
                        });
                    }
                }
                super.mouseClicked(e);
            }
        });
        sidebarBtnPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        //sidebarBtnPanel.add(addAccountBtn);
        sidebarBtnPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        sidebar.add(logoLabel);

        // Add glue to push buttons to the top
        sidebar.add(sidebarBtnPanel);

        content = new JPanel();
        content.setBorder(new EmptyBorder(5, 5, 5, 5));
        content.setLayout(new BorderLayout());
        content.setBackground(Color.WHITE);

        content.add(sidebar, BorderLayout.WEST);

        dashboardPanel = new DashboardPanel();


        JLabel placeholderLabel = new JLabel("Main Content");
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        content.add(placeholderLabel, BorderLayout.CENTER);
        content.add(dashboardPanel, BorderLayout.CENTER);

        accountPanel = new AccountPanel();

        setPreferredSize(new Dimension(1204, 768));
        setContentPane(content);
        pack();
        setLocationRelativeTo(null);

    }

    public DashboardPanel getDashboardPanel() {
        return dashboardPanel;
    }

}
