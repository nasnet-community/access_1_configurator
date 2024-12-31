import ui.MainFrame;
import ui.DashboardPanel;
import utils.ConnectionService;
import utils.Environments;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {

        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        DashboardPanel dashboard = mainFrame.getDashboardPanel();
        ConnectionService.setDashboard(dashboard);

        EventQueue.invokeLater(() -> {

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                     //   System.out.println("RUNNNINGGGG");
                        ConnectionService.checkFirstStep();
                        if (ConnectionService.isCompletedFirstStep) {
                            if (ConnectionService.isReachable(Environments.routerIp2)) {
                                ConnectionService.isCompletedSecondStep = true;
                                dashboard.updateSecondStepColor();
                            } else {
                                ConnectionService.isCompletedSecondStep = false;
                                dashboard.updateSecondStepColor();
                                ConnectionService.isCompletedThirdStep = false;
                                dashboard.updateThirdStepColor();
                                ConnectionService.isCompletedFourthStep = false;
                                dashboard.updateFourthStepColor();
                                ConnectionService.isCompletedFifthStep = false;
                                dashboard.updateFifthStepColor();
                                ConnectionService.isCompletedSixStep = false;
                                return;
                            }
                            if(ConnectionService.performReachableStarlink()) {
                                ConnectionService.isCompletedThirdStep = true;
                                dashboard.updateThirdStepColor();

                            } else {
                                ConnectionService.isCompletedThirdStep = false;
                                dashboard.updateThirdStepColor();
                            }

                            ConnectionService.checkPackageInstalled();
                            dashboard.updateFourthStepColor();
                            ConnectionService.checkOpenVPNCert();
                            dashboard.updateSixStepColor();
                            if(ConnectionService.performReachableDSL()){
                                ConnectionService.isCompletedFifthStep = true;
                            } else {
                                ConnectionService.isCompletedFifthStep = false;
                            }
                            dashboard.updateFifthStepColor();

                        }else{
                            ConnectionService.isCompletedSecondStep = false;
                            dashboard.updateSecondStepColor();
                            ConnectionService.isCompletedThirdStep = false;
                            dashboard.updateThirdStepColor();
                            ConnectionService.isCompletedFourthStep = false;
                            dashboard.updateFourthStepColor();
                            ConnectionService.isCompletedFifthStep = false;
                            dashboard.updateFifthStepColor();
                            ConnectionService.isCompletedSixStep = false;
                        }

                    } catch (Exception e) {
                        //   e.printStackTrace();
                    }
                }
            }, 0, 10000); // Check every 2 seconds


        });
    }
}
