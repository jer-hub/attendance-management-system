import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class AttendanceSystem {
    private JFrame frame;
    private JTextField userField;
    private String currentUser;

    DefaultTableModel model = new DefaultTableModel();
    JTable table = new JTable(model);
    private JPasswordField passwordField;
    private HashMap<String, String> teachers;
    private HashMap<String, Boolean> students;

    private List<Student> student_objects = new ArrayList<>();
    private Object[][] student_objects2;
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
        loginButton.setBounds(150, 80, 80, 25);
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
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    student_objects.add(new Student(Integer.parseInt(parts[0]), parts[1], parts[2],  false));
                    students.put(parts[0], false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAttendanceWindow() {
        JFrame attendanceFrame = new JFrame("Attendance");
        attendanceFrame.setLocationRelativeTo(null);
        attendanceFrame.setSize(600, 500);
        attendanceFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        attendanceFrame.add(panel);
        placeAttendanceComponents(panel,attendanceFrame);
        attendanceFrame.setVisible(true);
    }

    private void placeAttendanceComponents(JPanel panel, JFrame attendanceFrame) {
        panel.setLayout(new BorderLayout());

        //Sub Panels
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel  addStudentPanel = new JPanel(new FlowLayout(2));

        //Top Panel WEST
        JLabel userLabel = new JLabel("List of Students");
        userLabel.setBorder(new EmptyBorder(10,10,10,0));
        userLabel.setFont(new Font("Arial",Font.BOLD,30));
        topPanel.add(userLabel, BorderLayout.SOUTH);

        //Add student Panel
        JButton addPersonButton = new JButton("Add new Student");
        JButton logoutButton = new JButton("Logout");

        addPersonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = JOptionPane.showInputDialog(attendanceFrame, "Enter First Name:");
                String lastName = JOptionPane.showInputDialog(attendanceFrame, "Enter Last Name:");

                if (firstName != null && lastName != null) {
                    addNewStudent(firstName, lastName);
                }
            }
        });

        JButton removeSelectedButton = new JButton("Remove Selected Students");

        removeSelectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(attendanceFrame, "Are you sure you want to remove selected students?", "Confirmation", JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    removeSelectedStudents();
                }
            }
        });

        JButton updateButton = new JButton("Update Selected Student");

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();

                if (selectedRow != -1) {
                    int studentId = (int) table.getValueAt(selectedRow, 0);

                    String newFirstName = JOptionPane.showInputDialog(attendanceFrame, "Enter new First Name:");
                    String newLastName = JOptionPane.showInputDialog(attendanceFrame, "Enter new Last Name:");

                    updateStudent(studentId, newFirstName, newLastName);
                } else {
                    JOptionPane.showMessageDialog(attendanceFrame, "Please select a student to update.");
                }
            }
        });

        addStudentPanel.add(updateButton);

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                attendanceFrame.setVisible(false);
                logout();
            }
        });
        addStudentPanel.add(addPersonButton);
        addStudentPanel.add(removeSelectedButton);
        addStudentPanel.add(logoutButton);


        topPanel.add(addStudentPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        //Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout());

        String[] columns = {"id", "First Name", "Lastname", " "};
        model.setColumnIdentifiers(columns);

        table = new JTable(model);

        for (Student student : student_objects) {
            model.addRow(new Object[]{
                    student.getId(),
                    student.getFirstname(),
                    student.getLastname(),
                    student.isStatus()
            });
        }

        table.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 3) {
                    int rowIndex = e.getFirstRow();
                    boolean isChecked = (boolean) table.getValueAt(rowIndex, 3);
                    student_objects.get(rowIndex).setStatus(isChecked);
                }
            }
        });

        table.getColumnModel().getColumn(3).setCellRenderer(new CheckBoxRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());

        JLabel fileNameLabel = new JLabel("File Name:");
        southPanel.add(fileNameLabel,BorderLayout.WEST);

        JTextField fileNameField = new JTextField();
        southPanel.add(fileNameField,BorderLayout.CENTER);

        JButton saveButton = new JButton("Save");
        southPanel.add(saveButton, BorderLayout.SOUTH);


        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAttendance(fileNameField.getText());
            }
        });


        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);
    }

    private void addNewStudent(String firstName, String lastName) {

        int newStudentId = student_objects.size() + 1;
        Student newStudent = new Student(newStudentId, firstName, lastName, false);
        student_objects.add(newStudent);
        students.put(String.valueOf(newStudentId), false);


        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{newStudent.getId(), newStudent.getFirstname(), newStudent.getLastname(), false});

        saveStudentList();
    }

    private void updateStudent(int studentId, String newFirstName, String newLastName) {

        Optional<Student> optionalStudent = student_objects.stream()
                .filter(student -> student.getId() == studentId)
                .findFirst();

        if (optionalStudent.isPresent()) {

            Student student = optionalStudent.get();
            student.setFirstname(newFirstName);
            student.setLastname(newLastName);

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            int rowIndex = student_objects.indexOf(student);
            if (rowIndex != -1) {
                model.setValueAt(newFirstName, rowIndex, 1); // Update First Name column
                model.setValueAt(newLastName, rowIndex, 2);  // Update Last Name column
            }

            saveStudentList();
        } else {
            JOptionPane.showMessageDialog(frame, "Student with ID " + studentId + " not found.");
        }
    }

    private void removeSelectedStudents() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowCount = model.getRowCount();

        for (int i = rowCount - 1; i >= 0; i--) {
            boolean isSelected = (boolean) model.getValueAt(i, 3);
            if (isSelected) {
                student_objects.remove(i);
                model.removeRow(i);
            }
        }
        saveStudentList();
    }
    private void saveStudentList() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("student.txt"))) {
            for (Student student : student_objects) {
                pw.println(student.getId() + "," + student.getFirstname() + "," + student.getLastname() + "," + student.isStatus());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to save student list.");
            e.printStackTrace();
        }
    }

    private class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setSelected((value != null && ((Boolean) value)));
            return this;
        }
    }


    private void logout() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

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
            for (Student student : student_objects) {
                pw.println(student.id + "," + student.firstname + "," + student.lastname + "," + student.status);
            }
            JOptionPane.showMessageDialog(frame, "Attendance records saved successfully.");
            resetCheckboxes();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to save attendance records.");
            e.printStackTrace();
        }
    }

    private void resetCheckboxes() {
        for (int i = 0; i < student_objects.size(); i++) {
            table.setValueAt(false, i, 3);
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
