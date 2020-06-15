import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class implements GUI for clients.
 * Using a instance of Room to handle logical and communicative works.
 *
 * @author Ziheng Ni, twist@bupt.edu.cn
 * @since 12 June, 2020
 */

public class ClientUI implements RoomConstants {
    private JButton powerOn;
    private JPanel panel1;
    private JTextField roomId;
    private JTextField targetTemp;
    private JButton changeFan;
    private JButton changeTemp;
    private JComboBox fan;
    private JButton powerOff;
    private JLabel id;
    private JLabel currTemp;
    private JLabel fee;
    private JTextField initTemp;
    private JButton checkOut;
    private JButton checkIn;
    private JCheckBox hotMode;
    private JCheckBox coldMode;
    private JLabel state;
    private Room room;
    private java.util.Timer feeTimer, screenTimer;
    private long updateDelay = 1500;
    private long refreshDelay = 500;
    private TimerTask feeUpdate, screenRefresh;

    public ClientUI(Room rm){
        room = rm;
        powerOn.setEnabled(false);
        powerOff.setEnabled(false);
        changeFan.setEnabled(false);
        changeTemp.setEnabled(false);
        checkOut.setEnabled(false);
        screenTimer = new Timer();
        hotMode.setSelected(true);

        screenRefresh = new ScreenUpdateTsk(this);
        screenTimer.schedule(screenRefresh, 0, refreshDelay);

        checkIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                room.roomId = Long.parseLong(roomId.getText());
                roomId.setEditable(false);
                initTemp.setEditable(false);
                room.currentTemperature = Float.parseFloat(initTemp.getText());
                room.inItTemp = Integer.parseInt(initTemp.getText());
                room.checkIn();
                id.setText(String.valueOf(room.id));
                targetTemp.setText(String.valueOf(room.defTemp));
                fan.setSelectedIndex(room.fanSpeed);
                checkOut.setEnabled(true);
                checkIn.setEnabled(false);
                powerOn.setEnabled(true);
                hotMode.setEnabled(false);
                coldMode.setEnabled(false);
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
                changeFan.setEnabled(false);
                changeTemp.setEnabled(false);
                initTemp.setEditable(true);
                hotMode.setEnabled(true);
                coldMode.setEnabled(true);

                room.roomState = SHUTDOWN;
                //feeTimer.cancel();
                //screenTimer.cancel();
            }
        });

        powerOn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                room.currentTemperature = Double.parseDouble(initTemp.getText());
                room.boot();
                powerOn.setEnabled(false);
                powerOff.setEnabled(true);
                changeTemp.setEnabled(true);
                changeFan.setEnabled(true);
                checkOut.setEnabled(false);
                feeTimer = new Timer();
                room.lastTimePoint = System.currentTimeMillis();
                feeUpdate = new FeeUpdateTsk(room);
                feeTimer.schedule(feeUpdate, 1000, updateDelay);
            }
        });

        powerOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                powerOn.setEnabled(true);
                powerOff.setEnabled(false);
                changeTemp.setEnabled(false);
                changeFan.setEnabled(false);
                checkOut.setEnabled(true);
                currTemp.setText(initTemp.getText());
                room.currentTemperature = Double.parseDouble(initTemp.getText());
                targetTemp.setText(String.valueOf(room.defTemp));
                room.targetTemperature = room.defTemp;
                room.fanSpeed = room.defFan;
                fan.setSelectedIndex(room.defFan);
                room.shutdown();
                feeTimer.cancel();
                room.roomState = SHUTDOWN;
            }
        });

        changeTemp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSONObject rs = room.cgTemp(targetTemp.getText());
                targetTemp.setText(String.valueOf(rs.getInteger("tarT")));
                room.targetTemperature = Integer.parseInt(targetTemp.getText());
            }
        });

        changeFan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                room.fanSpeed = fan.getSelectedIndex();
                room.cgFan();
            }
        });

        hotMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                coldMode.setSelected(false);
                room.mode = HOT;
            }
        });

        coldMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hotMode.setSelected(false);
                room.mode = COLD;
            }
        });
    }

    public void refresh() {
        String[] stateStr = { "-", "Waiting", "Recuperating", "Shutdown"};
        if (SERVED != room.roomState){
            state.setText(stateStr[room.roomState]);
        } else {
            String[] servedStr = { "Heating", "Refrigerating"};
            state.setText(servedStr[room.mode]);
        }
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

    public static void main(String[] args) {
        Room room = new Room();
        JFrame frame = new JFrame("ClientUI");
        frame.setContentPane(new ClientUI(room).panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
