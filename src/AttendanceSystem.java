import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class AttendanceSystem {
    private JFrame frame;
    private JTextField userField;
    private String currentUser;
    private JPasswordField passwordField;
    private HashMap<String, String> teachers;
    private HashMap<String, Boolean> students;
    private JTextField fileNameField;
    private HashMap<String, JCheckBox> checkBoxes;



    public AttendanceSystem() {
        teachers = new HashMap<>();
        loadTeachers();

        students = new HashMap<>();
        loadStudents();

        checkBoxes = new HashMap<>();

        frame = new JFrame("Attendance System");
        frame.setLocationRelativeTo(null);
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);
        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);
        JLabel userLabel = new JLabel("User");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        userField = new JTextField(20);
        userField.setBounds(100, 20, 165, 25);
        panel.add(userField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 165, 25);
        panel.add(passwordField);


        JButton loginButton = new JButton("login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String user = userField.getText();
                String password = new String(passwordField.getPassword());
                if (teachers.containsKey(user) && teachers.get(user).equals(password)) {
                    currentUser = user;
                    frame.setVisible(false);
                    JOptionPane.showMessageDialog(frame, "Login successful.");
                    showAttendanceWindow();
                } else {
                    JOptionPane.showMessageDialog(frame, "Login failed.");
                }
            }
        });
    }

    private void loadTeachers() {
        try (BufferedReader br = new BufferedReader(new FileReader("user.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    teachers.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStudents() {
        try (BufferedReader br = new BufferedReader(new FileReader("student.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                students.put(line, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAttendanceWindow() {
        JFrame attendanceFrame = new JFrame("Attendance");
        attendanceFrame.setLocationRelativeTo(null);
        attendanceFrame.setSize(300, 200);
        attendanceFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        attendanceFrame.add(panel);
        placeAttendanceComponents(panel,attendanceFrame);
        attendanceFrame.pack();
        attendanceFrame.setVisible(true);
    }

    private void placeAttendanceComponents(JPanel panel, JFrame attendanceFrame) {
        panel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel userLabel = new JLabel("Logged in as: " + currentUser);
        topPanel.add(userLabel,BorderLayout.WEST);
        JButton logoutButton = new JButton("Logout");
        topPanel.add(logoutButton, BorderLayout.EAST);
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                attendanceFrame.setVisible(false);
                logout();
            }
        });
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        JLabel student_list = new JLabel("Student List:");
        centerPanel.add(student_list);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        for (Map.Entry<String, Boolean> entry : students.entrySet()) {
            JCheckBox checkBox = new JCheckBox(entry.getKey());
            checkBox.setSelected(entry.getValue());
            checkBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    students.put(entry.getKey(), checkBox.isSelected());
                }
            });
            checkBoxes.put(entry.getKey(), checkBox);
            centerPanel.add(checkBox);
        }

        JLabel fileNameLabel = new JLabel("File Name:");
        centerPanel.add(fileNameLabel);

        fileNameField = new JTextField(20);
        centerPanel.add(fileNameField);

        JButton saveButton = new JButton("Save");
        centerPanel.add(saveButton);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAttendance(fileNameField.getText());
            }
        });

        panel.add(centerPanel, BorderLayout.CENTER);
    }



    private void logout() {
        userField.setText("");
        passwordField.setText("");
        currentUser = null;
        frame.setVisible(true);
    }

    private void saveAttendance(String fileName) {
        File directory = new File("records/"+currentUser);
        if (!directory.exists()) {
            directory.mkdir();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(new File(directory, fileName+".txt")))) {
            for (Map.Entry<String, Boolean> entry : students.entrySet()) {
                pw.println(entry.getKey() + "," + (entry.getValue() ? "Present" : "Absent"));
            }
            JOptionPane.showMessageDialog(frame, "Attendance records saved successfully.");
            resetAttendance();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to save attendance records.");
            e.printStackTrace();
        }
    }

    private void resetAttendance() {
        for (Map.Entry<String, Boolean> entry : students.entrySet()) {
            students.put(entry.getKey(), false);
            checkBoxes.get(entry.getKey()).setSelected(false);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        new AttendanceSystem();
    }
}
