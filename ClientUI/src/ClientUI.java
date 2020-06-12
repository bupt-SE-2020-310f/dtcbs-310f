import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Timer;
import java.util.TimerTask;

public class ClientUI {
    private JButton powerOn;
    private JPanel panel1;
    private JTextField roomId;
    private JTextField targetTemp;
    private JButton changeFan;
    private JButton changeTemp;
    private JComboBox fan;
    private JButton powerOff;
    private JLabel id;
    private JLabel mode;
    private JLabel currTemp;
    private JLabel fee;
    private JTextField initTemp;
    private JButton checkOut;
    private JButton checkIn;
    private Room room;
    private java.util.Timer feeTimer, screenTimer;
    private long updateDelay = 1500;
    private long refreshDelay = 3000;
    private TimerTask feeUpdate, screenRefresh;

    public ClientUI(Room rm){
        room = rm;
        powerOn.setEnabled(false);
        powerOff.setEnabled(false);
        changeFan.setEnabled(false);
        changeTemp.setEnabled(false);
        checkOut.setEnabled(false);


        feeUpdate = new FeeUpdateTsk(room);
        screenRefresh = new ScreenUpdateTsk(this);

        checkIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                room.roomId = Long.parseLong(roomId.getText());
                roomId.setEditable(false);
                room.currentTemperature = Float.parseFloat(currTemp.getText());
                room.checkIn();
                id.setText(String.valueOf(room.id));
                targetTemp.setText(String.valueOf(room.defTemp));
                checkOut.setEnabled(true);
                checkIn.setEnabled(false);
                powerOn.setEnabled(true);
            }
        });

        checkOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                room.checkOut();
                fee.setText("0");
                checkOut.setEnabled(false);
                checkIn.setEnabled(true);
                powerOff.setEnabled(false);
                powerOn.setEnabled(false);
                roomId.setEditable(true);
                feeTimer.cancel();
                screenTimer.cancel();
            }
        });

        powerOn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                room.boot();
                powerOn.setEnabled(false);
                powerOff.setEnabled(true);
                feeTimer = new Timer();
                screenTimer = new Timer();
                feeTimer.schedule(feeUpdate, updateDelay);
                screenTimer.schedule(screenRefresh, refreshDelay);
            }
        });

        powerOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                powerOn.setEnabled(true);
                powerOff.setEnabled(false);
                room.shutdown();
                feeTimer.cancel();
                screenTimer.cancel();
            }
        });

        changeTemp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                room.targetTemperature = Integer.parseInt(targetTemp.getText());
                room.cgTemp();
            }
        });

        changeFan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                room.fanSpeed = fan.getSelectedIndex();
                room.cgFan();
            }
        });
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("ClientUI");
        Room room = new Room();
        frame.setContentPane(new ClientUI(room).panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void refresh() {
        currTemp.setText(String.valueOf(room.currentTemperature));
        fee.setText(String.valueOf(room.fee));
    }

    public void setId(String s){
        id.setText(s);
    }


    private class FeeUpdateTsk extends TimerTask{
        Room room;

        FeeUpdateTsk(Room rm){
            room = rm;
        }

        @Override
        public void run() {
            room.fee();
        }
    }

    private class ScreenUpdateTsk extends TimerTask{
        ClientUI ui;

        ScreenUpdateTsk(ClientUI theUI){
            ui = theUI;
        }

        @Override
        public void run() {
            ui.refresh();
        }
    }
}
