package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;

public class EventScheduler {
    private static EventManager eventManager = new EventManager();

    public EventScheduler() {
        unit();
    }

    private void unit() {
        eventManager.loadEvents();
        frame();
        scheduleExistingEvents();
    }

    private void frame() {
        JFrame frame = new JFrame("Event calendar");
        frame.setSize(630, 420);
        frame.setLocale(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel descLabel = new JLabel("Description:");
        JTextField descField = new JTextField();
        descLabel.setBounds(80, 50, 150, 30);
        descField.setBounds(280, 50, 250, 30);

        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        JTextField dateField = new JTextField();
        dateLabel.setBounds(80, 90, 150, 30);
        dateField.setBounds(280, 90, 250, 30);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        JLabel timeLabel = new JLabel("Time (HH:mm):");
        JTextField timeField = new JTextField();
        timeLabel.setBounds(80, 130, 150, 30);
        timeField.setBounds(280, 130, 250, 30);
        timeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        JLabel soundLabel = new JLabel("Sound notifycation:");
        JCheckBox soundCheckBox = new JCheckBox("Event notifycation");
        soundLabel.setBounds(80, 170, 150, 30);
        soundCheckBox.setBounds(280, 170, 250, 30);

        JButton addButton = new JButton("Add");
        JButton refButton = new JButton("Refresh");
        JButton detButton = new JButton("Detail");
        JButton calButton = new JButton("Calendar");
        JTextArea eventListArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(eventListArea);
        addButton.setBounds(80, 210, 85, 30);
        refButton.setBounds(80, 250, 85, 30);
        detButton.setBounds(175, 210, 85, 30);
        calButton.setBounds(175, 250, 85, 30);
        scrollPane.setBounds(280, 210, 250, 130);
        eventListArea.setEditable(false);

        frame.add(descLabel);
        frame.add(descField);
        frame.add(dateLabel);
        frame.add(dateField);
        frame.add(timeLabel);
        frame.add(timeField);
        frame.add(soundLabel);
        frame.add(soundCheckBox);
        frame.add(addButton);
        frame.add(refButton);
        frame.add(detButton);
        frame.add(calButton);
        frame.add(scrollPane);

        JFrame f = new JFrame("Event calendar details");
        f.setSize(700, 420);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(null);

        String[] column = { "Description", "Date", "Sound"};
        String[][] datas = { {} };
        DefaultTableModel model = new DefaultTableModel(datas, column);
        JTable table = new JTable(model);
        JScrollPane scrollPane2 = new JScrollPane(table);
        scrollPane2.setBounds(10, 10, 400, 360);

        JLabel descrLabel = new JLabel("Description:");
        JTextField descrField = new JTextField();
        descrLabel.setBounds(420, 10, 100, 30);
        descrField.setBounds(500, 10, 150, 30);

        JLabel date = new JLabel("Date:");
        JTextField dateTextField = new JTextField();
        date.setBounds(420, 50, 100, 30);
        dateTextField.setBounds(500, 50, 150, 30);

        JLabel time = new JLabel("Time:");
        JTextField timeTextField = new JTextField();
        time.setBounds(420, 90, 100, 30);
        timeTextField.setBounds(500, 90, 150, 30);

        JLabel sound = new JLabel("Sound:");
        JCheckBox soundCheckBox2 = new JCheckBox("Event notifycation");
        sound.setBounds(420, 130, 100, 30);
        soundCheckBox2.setBounds(500, 130, 150, 30);

        JButton hideButton = new JButton("Hide");
        JButton unhiButton = new JButton("Unhide");
        JButton deleButton = new JButton("Delete");
        JButton updaButton = new JButton("Update");
        hideButton.setBounds(420, 300, 120, 30);
        unhiButton.setBounds(420, 340, 120, 30);
        deleButton.setBounds(550, 300, 120, 30);
        updaButton.setBounds(550, 340, 120, 30);

        f.add(descrLabel);
        f.add(descrField);
        f.add(date);
        f.add(dateTextField);
        f.add(time);
        f.add(timeTextField);
        f.add(sound);
        f.add(soundCheckBox2);
        f.add(scrollPane2);
        f.add(hideButton);
        f.add(unhiButton);
        f.add(deleButton);
        f.add(updaButton);

        f.setVisible(false);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String description = descField.getText();
                    String dateString = dateField.getText();
                    String timeString = timeField.getText();
                    boolean notification = soundCheckBox.isSelected();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date = dateFormat.parse(dateString + " " + timeString);
                    Event event = new Event(description, date, notification, true);
                    eventManager.addEvent(event);
                    scheduleEvent(event);
                    updateEventList(eventListArea);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid date format. Please use yyyy-MM-dd HH:mm.");
                }
            }
        });

        detButton.addActionListener(e -> {
            f.setVisible(true);
        });

        refButton.addActionListener(e -> {
            timeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        });

        detButton.addActionListener(e -> {
            model.setRowCount(0);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (Event event : eventManager.getEvents()) {
                Object[] rowData = {
                        event.getDescription(),
                        formatter.format(event.getDate()),
                        event.getNotifycationBySound() ? "Yes" : "No"
                };
                model.addRow(rowData);
            }
            f.setVisible(true);
        });

        updaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    try {
                        // Lấy thông tin hiện tại của sự kiện
                        String oldDescription = (String) table.getValueAt(selectedRow, 0);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date oldDate = formatter.parse((String) table.getValueAt(selectedRow, 1));
                        boolean oldNotification = table.getValueAt(selectedRow, 2).equals("Yes");
        
                        Event oldEvent = new Event(oldDescription, oldDate, oldNotification, true);
        
                        // Lấy thông tin mới từ các trường
                        String newDescription = descrField.getText();
                        String newDateString = dateTextField.getText();
                        String newTimeString = timeTextField.getText();
                        boolean newNotification = soundCheckBox2.isSelected();
                        Date newDate = formatter.parse(newDateString + " " + newTimeString);
        
                        Event newEvent = new Event(newDescription, newDate, newNotification, true);
        
                        // Cập nhật sự kiện
                        eventManager.updateEvent(oldEvent, newEvent);
        
                        // Cập nhật bảng
                        table.setValueAt(newDescription, selectedRow, 0);
                        table.setValueAt(formatter.format(newDate), selectedRow, 1);
                        table.setValueAt(newNotification ? "Yes" : "No", selectedRow, 2);
        
                        // Cập nhật danh sách sự kiện
                        updateEventList(eventListArea);
        
                        // Lên lịch lại sự kiện nếu ngày chưa qua
                        if (newDate.after(new Date())) {
                            scheduleEvent(newEvent);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(f, "Invalid date format. Please use yyyy-MM-dd HH:mm.");
                    }
                } else {
                    JOptionPane.showMessageDialog(f, "Please select an event to update.");
                }
            }
        });        

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    descrField.setText((String) table.getValueAt(selectedRow, 0));
                    dateTextField.setText((String) table.getValueAt(selectedRow, 1).toString().split(" ")[0]);
                    timeTextField.setText((String) table.getValueAt(selectedRow, 1).toString().split(" ")[1]);
                    soundCheckBox2.setSelected(table.getValueAt(selectedRow, 2).equals("Yes"));
                }
            }
        });

        frame.setVisible(true);
        updateEventList(eventListArea);
    }

    private static void scheduleEvent(Event event) {
        Date currentDate = new Date();
        if (event.getDate().before(currentDate)) {
            // System.out.println("The event date has already passed.");
            return;
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                showNotification(event.getDescription());
                if (event.getNotifycationBySound()) {
                    playSound("sound/sound.wav");
                }
            }
        }, event.getDate());
    }

    private static void playSound(String soundFile) {
        try {
            File file = new File(soundFile);
            if (file.exists()) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } else {
                System.out.println("Sound file not found: " + soundFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showNotification(String description) {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon/icon.png");
            TrayIcon trayIcon = new TrayIcon(image, "Event Scheduler");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip(description);
            tray.add(trayIcon);
            trayIcon.displayMessage("Event Reminder", description, TrayIcon.MessageType.INFO);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void updateEventList(JTextArea eventListArea) {
        StringBuilder eventListText = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Event event : eventManager.getEvents()) {
            eventListText
                    .append(dateFormat.format(event.getDate()))
                    .append(" ,")
                    .append(event.getNotifycationBySound() ? "Show" : "None")
                    .append(" ,")
                    .append(event.getDescription())
                    .append("\n");
        }
        eventListArea.setText(eventListText.toString());
    }

    private static void scheduleExistingEvents() {
        for (Event event : eventManager.getEvents()) {
            scheduleEvent(event);
        }
    }
}
