import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class ServerUI {
    private JPanel panel;
    private JLabel managerLb;
    private JLabel adminLb;
    private JLabel fontLb;
    private JButton powerBtn;
    private JButton monoitorBtn;
    private JButton RDRBtn;
    private JButton InvoiceBtn;
    private JButton ReportBtn;
    private Server server;

    public ServerUI(Server s, JFrame f) {
        server = s;
        powerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPowerOnDialog(f, f);
            }
        });
        monoitorBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMonitorDialog(f, f);
            }
        });
        RDRBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRDRDialog(f, f);
            }
        });
        InvoiceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInvoiceDialog(f, f);
            }
        });
        ReportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showReportDialog(f, f);
            }
        });
    }

    private void showReportDialog(JFrame owner, Component parentComp) {
        JDialog dialog = new JDialog(owner, "日报", false);
        dialog.setLocationRelativeTo(parentComp);
        dialog.setSize(800, 500);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Box vBox = Box.createVerticalBox();
        JLabel lb1 = new JLabel("房间号");
        JTextField tx1 = new JTextField();
        Box hBox1 = Box.createHorizontalBox();
        hBox1.add(lb1);
        hBox1.add(tx1);
        vBox.add(hBox1);
        JLabel lb2 = new JLabel("查询起始日期");
        JTextField tx2 = new JTextField(Server.TIME_FORMAT);
        Box hBox2 = Box.createHorizontalBox();
        hBox2.add(lb2);
        hBox2.add(tx2);
        vBox.add(hBox2);
        JLabel lb3 = new JLabel("查询截止日期");
        JTextField tx3 = new JTextField(Server.TIME_FORMAT);
        Box hBox3 = Box.createHorizontalBox();
        hBox3.add(lb3);
        hBox3.add(tx3);
        vBox.add(hBox3);
        JButton btn = new JButton("查询");
        vBox.add(btn);

        AbstractTableModel tableModel;
        String[] title = {"编号","房间号","总费用","总服务时长","详单记录数","启动次数","调风次数"};
        Vector<Vector<Object>> vectors = new Vector<>();
        tableModel = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return vectors.size();
            }

            @Override
            public int getColumnCount() {
                return title.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (!vectors.isEmpty()) {
                    return ((Vector<?>)vectors.elementAt(rowIndex)).elementAt(columnIndex);
                } else {
                    return null;
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

            @Override
            public String getColumnName(int column) {
                return title[column];
            }
        };
        JTable table = new JTable(tableModel);
        table.setToolTipText("显示房间日报信息");
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);// 设置表格调整尺寸模式
        table.setCellSelectionEnabled(false);// 设置单元格选择方式
        table.setShowVerticalLines(true);// 设置是否显示单元格间的分割线
        table.setShowHorizontalLines(true);
        JScrollPane scrollPane = new JScrollPane(table);
        vBox.add(scrollPane);


        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSONObject j = server.QueryReport(tx1.getText(), tx2.getText(), tx3.getText());
                if (j == null) {
                    JOptionPane.showMessageDialog(
                            dialog,
                            "查询失败!",
                            "WarnMsg",
                            JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    vectors.removeAllElements();
                    tableModel.fireTableStructureChanged();
                    JSONObject jd = j.getJSONObject("data");
                    int id = jd.getInteger("id");
                    String rmId = jd.getString("rmId");
                    Float totalFee = jd.getFloat("totalFee");
                    long duration = jd.getLong("duration")/1000;
                    int nRDR = jd.getInteger("nRDR");
                    int nOnOff = jd.getInteger("nOnOff");
                    int nChangeF = jd.getInteger("nChangeF");
                    Vector<Object> v = new Vector<>();
                    v.add(id);
                    v.add(rmId);
                    v.add(totalFee);
                    v.add(duration+"s");
                    v.add(nRDR);
                    v.add(nOnOff);
                    v.add(nChangeF);
                    vectors.add(v);
                    tableModel.fireTableStructureChanged();
                }
            }
        });

        dialog.setContentPane(vBox);
        dialog.setVisible(true);
    }

    private void showInvoiceDialog(JFrame owner, Component parentComp) {
        JDialog dialog = new JDialog(owner, "详单", false);
        dialog.setLocationRelativeTo(parentComp);
        dialog.setSize(400, 400);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel jPanel = new JPanel(null);

        JLabel lb1 = new JLabel("房间号");
        lb1.setBounds(10, 10, 100, 50);
        JTextField tx1 = new JTextField();
        tx1.setBounds(150, 10, 200, 50);
        JLabel lb2 = new JLabel("查询起始日期");
        lb2.setBounds(10, 70, 100, 50);
        JTextField tx2 = new JTextField(Server.TIME_FORMAT);
        tx2.setBounds(150, 70, 200, 50);
        JLabel lb3 = new JLabel("查询截止日期");
        lb3.setBounds(10, 130, 100, 50);
        JTextField tx3 = new JTextField(Server.TIME_FORMAT);
        tx3.setBounds(150, 130, 200, 50);
        JLabel tf = new JLabel("$totalFee");
        tf.setBounds(100, 200, 100, 50);
        JButton btn = new JButton("查询");
        btn.setBounds(250, 200, 100, 50);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSONObject j = server.QueryInvoice(tx1.getText(), tx2.getText(), tx3.getText());
                if (j == null) {
                    JOptionPane.showMessageDialog(
                            dialog,
                            "查询失败!",
                            "WarnMsg",
                            JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    String totalFee = j.getString("totalFee");
                    tf.setText(totalFee);
                }
            }
        });

        jPanel.add(lb1);
        jPanel.add(tx1);
        jPanel.add(lb2);
        jPanel.add(tx2);
        jPanel.add(lb3);
        jPanel.add(tx3);
        jPanel.add(btn);
        jPanel.add(tf);

        dialog.setContentPane(jPanel);
        dialog.setVisible(true);
    }

    private void showRDRDialog(JFrame owner, Component parentComp) {
        JDialog dialog = new JDialog(owner, "详单", false);
        dialog.setLocationRelativeTo(parentComp);
        dialog.setSize(800, 500);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Box vBox = Box.createVerticalBox();
        JLabel lb1 = new JLabel("房间号");
        JTextField tx1 = new JTextField();
        Box hBox1 = Box.createHorizontalBox();
        hBox1.add(lb1);
        hBox1.add(tx1);
        vBox.add(hBox1);
        JLabel lb2 = new JLabel("查询起始日期");
        JTextField tx2 = new JTextField(Server.TIME_FORMAT);
        Box hBox2 = Box.createHorizontalBox();
        hBox2.add(lb2);
        hBox2.add(tx2);
        vBox.add(hBox2);
        JLabel lb3 = new JLabel("查询截止日期");
        JTextField tx3 = new JTextField(Server.TIME_FORMAT);
        Box hBox3 = Box.createHorizontalBox();
        hBox3.add(lb3);
        hBox3.add(tx3);
        vBox.add(hBox3);
        JButton btn = new JButton("查询");
        vBox.add(btn);

        AbstractTableModel tableModel;
        String[] title = {"房间号","请求时间","服务时长","风速","费率","单次费用"};
        Vector<Vector<Object>> vectors = new Vector<>();
        tableModel = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return vectors.size();
            }

            @Override
            public int getColumnCount() {
                return title.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (!vectors.isEmpty()) {
                    return ((Vector<?>)vectors.elementAt(rowIndex)).elementAt(columnIndex);
                } else {
                    return null;
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

            @Override
            public String getColumnName(int column) {
                return title[column];
            }
        };
        JTable table = new JTable(tableModel);
        table.setToolTipText("显示房间详单记录");
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);// 设置表格调整尺寸模式
        table.setCellSelectionEnabled(false);// 设置单元格选择方式
        table.setShowVerticalLines(true);// 设置是否显示单元格间的分割线
        table.setShowHorizontalLines(true);
        JScrollPane scrollPane = new JScrollPane(table);
        vBox.add(scrollPane);


        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSONObject j = server.QueryRDR(tx1.getText(), tx2.getText(), tx3.getText());
                if (j == null) {
                    JOptionPane.showMessageDialog(
                            dialog,
                            "查询失败!",
                            "WarnMsg",
                            JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    vectors.removeAllElements();
                    tableModel.fireTableStructureChanged();
                    JSONArray datas = j.getJSONArray("data");
                    String[] fanSpdStr = new String[]{"低","中","高"};
                    for (int i = 0; i < datas.size(); i++) {
                        JSONObject jd = datas.getJSONObject(i);
                        String rmId = jd.getString("rmId");
                        Server.DATE.setTime(jd.getLong("requestTime"));
                        String requestTime = Server.DATE_FORMAT.format(Server.DATE);
                        long duration = jd.getLong("duration")/1000;
                        String fanSpd = fanSpdStr[jd.getInteger("fanSpd")];
                        Float feeRate = jd.getFloat("feeRate");
                        Float fee = jd.getFloat("fee");
                        Vector<Object> v = new Vector<>();
                        v.add(rmId);
                        v.add(requestTime);
                        v.add(duration+"s");
                        v.add(fanSpd);
                        v.add(feeRate);
                        v.add(fee);
                        vectors.add(v);
                        tableModel.fireTableStructureChanged();
                    }
                }
            }
        });

        dialog.setContentPane(vBox);
        dialog.setVisible(true);
    }

    private void showMonitorDialog(JFrame owner, Component parentComp) {
        JDialog dialog = new JDialog(owner, "Monitor", false);
        dialog.setLocationRelativeTo(parentComp);
        dialog.setSize(800, 500);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        AbstractTableModel tableModel;
        String[] title = {"房间号","空调状态","当前温度","目标温度","当前风速","当前费用"};
        Vector<Vector<Object>> vectors = new Vector<>();
        tableModel = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return vectors.size();
            }

            @Override
            public int getColumnCount() {
                return title.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (!vectors.isEmpty()) {
                    return ((Vector<?>)vectors.elementAt(rowIndex)).elementAt(columnIndex);
                } else {
                    return null;
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

            @Override
            public String getColumnName(int column) {
                return title[column];
            }
        };
        JTable table = new JTable(tableModel);
        table.setToolTipText("显示入住房间空调信息");
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);// 设置表格调整尺寸模式
        table.setCellSelectionEnabled(false);// 设置单元格选择方式
        table.setShowVerticalLines(true);// 设置是否显示单元格间的分割线
        table.setShowHorizontalLines(true);

        final boolean[] running = {true};
        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                while(running[0]) {
                    JSONObject j = server.CheckRoomState();
                    if (j == null) {
                        JOptionPane.showMessageDialog(
                                dialog,
                                "查询失败!",
                                "WarnMsg",
                                JOptionPane.WARNING_MESSAGE
                        );
                        break;
                    }
                    try {
                        vectors.removeAllElements();
                        tableModel.fireTableStructureChanged();
                        JSONArray datas = j.getJSONArray("data");
                        String[] onStr = new String[]{"服务中","等待响应","回温待机","关机"};
                        String[] fanSpdStr = new String[]{"低","中","高"};
                        for (int i = 0; i < datas.size(); i++) {
                            JSONObject jd = datas.getJSONObject(i);
                            String rmId = jd.getString("rmId");
                            int state = jd.getInteger("state");
                            String stateStr = onStr[state];
                            long waitTime = jd.getLong("waitTime");
                            if (state == 1) {
                                if (waitTime > 0) {
                                    stateStr += "，RR计时:" + (waitTime/1000) + "s";
                                } else {
                                    stateStr += "，RR取消";
                                }
                            }
                            Float currT = jd.getFloat("currT");
                            int targetT = jd.getInteger("targetT");
                            String fanSpd = fanSpdStr[jd.getInteger("fanSpd")];
                            Float fee = jd.getFloat("fee");
                            Vector<Object> v = new Vector<>();
                            v.add(rmId);
                            v.add(stateStr);
                            v.add(currT);
                            v.add(targetT);
                            v.add(fanSpd);
                            v.add(fee);
                            vectors.add(v);
                            tableModel.fireTableStructureChanged();
                        }
                        Thread.sleep(1500);
                    } catch (Exception ignored) {
                        ;
                    }
                }
            }
        });
        timer.start();
        dialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                running[0] = false;
                dialog.dispose();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);

        dialog.setContentPane(scrollPane);
        dialog.setVisible(true);
    }

    private void showPowerOnDialog(JFrame owner, Component parentComp) {
        JDialog dialog = new JDialog(owner, "PowerOn", true);
        dialog.setLocationRelativeTo(parentComp);
        dialog.setSize(200, 200);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(null);
        JLabel lb1 = new JLabel("空调模式");
        lb1.setBounds(10, 10, 50, 50);
        String[] modes = new String[]{"制热","制冷"};
        JComboBox<String> comboBox = new JComboBox<>(modes);
        comboBox.setBounds(100, 20, 60, 20);
        comboBox.setSelectedIndex(0);
        final int[] mode = {0};
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    mode[0] = comboBox.getSelectedIndex();
                }
            }
        });
        JButton btn = new JButton("确认");
        btn.setBounds(50, 100, 100, 40);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean status = server.PowerOn(mode[0]);
                if (!status) {
                    JOptionPane.showMessageDialog(
                            dialog,
                            "请求失败!",
                            "WarnMsg",
                            JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    monoitorBtn.setEnabled(true);
                    RDRBtn.setEnabled(true);
                    InvoiceBtn.setEnabled(true);
                    ReportBtn.setEnabled(true);
                    powerBtn.setEnabled(false);
                }
                dialog.dispose();

            }
        });
        panel.add(lb1);
        panel.add(comboBox);
        panel.add(btn);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ServerUI");
        Server server = new Server();
        frame.setContentPane(new ServerUI(server, frame).panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                boolean off = server.PowerOff();
                if (!off && Server.on) {
                    Server.on = false;
                    JOptionPane.showMessageDialog(
                            frame,
                            "关闭远程服务器出错，请人工核查!",
                            "WarnMsg",
                            JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                            frame,
                            "即将退出!",
                            "okMsg",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
