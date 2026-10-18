import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.nimbus.State;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Locale;

public class MainForm
{
    private JPanel pnlMain;
    private JPanel pnlEmployee;
    private JPanel pnEmployeeList;
    private JPanel pnlListTitle;
    private JScrollPane scpEmployeeList;
    private JPanel pnlButtons;
    private JPanel pnlEmployeeInfo;
    private JPanel pnlInfoTitle;
    private JLabel lblLIstTitle;
    private JLabel lblInfoTitle;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JPanel pnlField;
    private JPanel pnlInput;
    private JLabel lblEmployeeID;
    private JTextField txtEmployeeID;
    private JTextField txtEmployeeFullName;
    private JTextField txtEmployeePhone;
    private JComboBox cmbPositionName;
    private JComboBox cmbDegreeName;
    private JSpinner spnEmployeeYOB;
    private JTable tblEmployeeList;
    private DefaultTableModel tableModel;
    private JLabel lbEmployeeGender;
    private JLabel lblEmployeeFullName;
    private JLabel lblEmployeeYOB;
    private JLabel lblEmployeePhone;
    private JLabel lblPositionName;
    private JLabel lblDegreeName;
    private JRadioButton radMale;
    private JRadioButton radFemale;
    private JButton btnRead;
    private JButton btnCancel;
    private ButtonGroup genderGroup;
    
    public MainForm()
    {
        genderGroup = new ButtonGroup();
        genderGroup.add(radFemale);
        genderGroup.add(radMale);
        
        
        String[] tableTitle = {"Mã nhân viên", "Họ tên", "Năm sinh", "Giới tính", "Số điện thoại", "Chức vụ", "Bằng cấp"};
        
        tableModel = new DefaultTableModel(tableTitle, 0);
        tblEmployeeList.setModel(tableModel);
        tblEmployeeList.setRowHeight(50);
        loadData();
        loadComboBox();
        setMode(null);
        
        btnAdd.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (btnCancel.isEnabled())
                {
                    setMode("input");
                }
                else
                {
                    setMode("create");
                }
            }
        });
        btnUpdate.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setMode("update");
            }
        });
        btnDelete.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setMode("delete");
            }
        });
        btnRefresh.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setMode("refresh");
            }
        });
        btnRead.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setMode("read");
            }
        });
        btnCancel.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setMode("refresh");
            }
        });
    }
    
    private void enableEditingFields(boolean booleanValue)
    {
        txtEmployeeFullName.setEnabled(booleanValue);
        spnEmployeeYOB.setEnabled(booleanValue);
        radMale.setEnabled(booleanValue);
        radFemale.setEnabled(booleanValue);
        txtEmployeePhone.setEnabled(booleanValue);
        cmbPositionName.setEnabled(booleanValue);
        cmbDegreeName.setEnabled(booleanValue);
    }
    
    private void clearEditingFields()
    {
        txtEmployeeID.setText("");
        txtEmployeeFullName.setText("");
        spnEmployeeYOB.setValue(0);
        genderGroup.clearSelection();
        txtEmployeePhone.setText("");
    }
    
    private void buttonState(String mode)
    {
        boolean state = true;
        switch (mode)
        {
            case "create":
                btnAdd.setEnabled(state);
                btnCancel.setEnabled(state);
                
                btnRead.setEnabled(!state);
                btnUpdate.setEnabled(!state);
                btnDelete.setEnabled(!state);
                break;
            case "update":
                btnUpdate.setEnabled(state);
                btnCancel.setEnabled(state);
                
                btnAdd.setEnabled(!state);
                btnRead.setEnabled(!state);
                btnDelete.setEnabled(!state);
                break;
            case "delete":
                btnDelete.setEnabled(state);
                btnCancel.setEnabled(state);
                
                btnAdd.setEnabled(!state);
                btnRead.setEnabled(!state);
                btnUpdate.setEnabled(!state);
                break;
            case "read":
            case "refresh":
            case null,
                 default:
                btnAdd.setEnabled(state);
                btnRead.setEnabled(state);
                
                btnUpdate.setEnabled(!state);
                btnDelete.setEnabled(!state);
                btnCancel.setEnabled(!state);
                break;
        }
    }
    
    private void setMode(String mode)
    {
        boolean state = true;
        switch (mode)
        {
            case "create":
                enableEditingFields(state);
                buttonState(mode);
                generateEmployeeID();
                break;
            case "input":
                if (validateInput())
                {
                    createData();
                }
                break;
            case "read":
                enableEditingFields(!state);
                clearEditingFields();
                buttonState(mode);
                loadData();
                break;
            case "update":
                enableEditingFields(state);
                buttonState(mode);
                break;
            case "delete":
                enableEditingFields(state);
                buttonState(mode);
                break;
            case "refresh":
            case null,
                 default:
                enableEditingFields(!state);
                clearEditingFields();
                buttonState(mode);
                loadData();
                break;
        }
    }
    
    private void loadData()
    {
        tableModel.setRowCount(0);
        String selectData =
                """
                            SELECT E.EmployeeID, E.EmployeeFullName, E.EmployeeYOB, E.EmployeeGender, E.EmployeePhone, SC.PositionName, D.DegreeName
                            FROM Employee E
                            JOIN SalaryCoefficient SC ON E.PositionID = SC.PositionID
                            JOIN Degree D ON E.EmployeeDegree = D.DegreeID
                        """;
        try
                (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectData);)
        {
            while (rs.next())
            {
                String EmployeeID = rs.getString("EmployeeID");
                String EmployeeFullName = rs.getString("EmployeeFullName");
                int EmployeeYOB = rs.getInt("EmployeeYOB");
                String EmployeeGender = rs.getString("EmployeeGender");
                String EmployeePhone = rs.getString("EmployeePhone");
                String EmployeePosition = rs.getString("PositionName");
                String EmployeeDegree = rs.getString("DegreeName");
                Object[] EmployeeInfo = new Object[]{EmployeeID, EmployeeFullName, EmployeeYOB, EmployeeGender, EmployeePhone, EmployeePosition, EmployeeDegree};
                tableModel.addRow(EmployeeInfo);
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog
                    (
                            null,
                            "Lỗi" + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
        }
    }
    
    private void loadComboBox()
    {
        String selectPosition =
                """
                        SELECT SC.PositionName
                        FROM SalaryCoefficient SC
                        """;
        try
                (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectPosition))
        {
            while (rs.next())
            {
                cmbPositionName.addItem(rs.getString("PositionName"));
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog
                    (
                            null,
                            "Không tìm thấy dữ liệu!",
                            "Lỗi!",
                            JOptionPane.ERROR_MESSAGE
                    );
        }
        String selectDegree =
                """
                        SELECT D.DegreeName
                        FROM Degree D
                        """;
        try
                (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectDegree))
        {
            while (rs.next())
            {
                cmbDegreeName.addItem(rs.getString("DegreeName"));
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog
                    (
                            null,
                            "Không tìm thấy dữ liệu!",
                            "Lỗi!",
                            JOptionPane.ERROR_MESSAGE
                    );
        }
    }
    
    private void generateEmployeeID()
    {
        String selectLatestEmployeeID =
                """
                        SELECT E.EmployeeID
                        FROM Employee E
                        ORDER BY E.EmployeeID DESC
                        LIMIT 1
                        """;
        try
                (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectLatestEmployeeID))
        {
            String latestEmployeeID = rs.getString("EmployeeID");
            String prefix = latestEmployeeID.replaceAll("\\d", "");
            String numbers = latestEmployeeID.replaceAll("\\D", "");
            int nextNumber = Integer.parseInt(numbers) + 1;
            txtEmployeeID.setText(prefix + String.format("%0" + numbers.length() + "d", nextNumber));
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog
                    (
                            null,
                            "Lỗi: " + e.getMessage(),
                            "Lỗi!",
                            JOptionPane.ERROR_MESSAGE
                    );
        }
    }
    
    private boolean validateInput()
    {
        if (txtEmployeeFullName.getText().trim().isEmpty())
        {
            JOptionPane.showMessageDialog
                    (
                            null,
                            "Họ tên không được để trống!",
                            "Cảnh báo!",
                            JOptionPane.WARNING_MESSAGE
                    );
            txtEmployeeID.requestFocus();
            return false;
        }
        if ((txtEmployeeFullName.getText().trim()).matches("\\d+"))
        {
            JOptionPane.showMessageDialog
                    (
                            null,
                            "Họ tên không được chứa số!",
                            "Cảnh báo!",
                            JOptionPane.WARNING_MESSAGE
                    );
            txtEmployeeFullName.setText("");
            txtEmployeeFullName.requestFocus();
            return false;
        }
        if ((int) spnEmployeeYOB.getValue() <= 0)
        {
            JOptionPane.showMessageDialog
                    (
                            null,
                            "Năm sinh phải lớn hơn 0!",
                            "Cảnh báo!",
                            JOptionPane.WARNING_MESSAGE
                    );
            spnEmployeeYOB.setValue(0);
            spnEmployeeYOB.requestFocus();
            return false;
        }
        if (!radMale.isSelected() && !radFemale.isSelected())
        {
            JOptionPane.showMessageDialog
                    (
                            null,
                            "Vui lòng chọn giới tính!",
                            "Cảnh báo!",
                            JOptionPane.WARNING_MESSAGE
                    );
            radMale.requestFocus();
            return false;
        }
        if (txtEmployeePhone.getText().replaceAll("\\s+", "").isEmpty())
        {
            JOptionPane.showMessageDialog
                    (
                            null,
                            "Số điện thoại không được để trống!",
                            "Cảnh báo!",
                            JOptionPane.WARNING_MESSAGE
                    );
            txtEmployeePhone.requestFocus();
            return false;
        }
        if (!(txtEmployeePhone.getText().replaceAll("\\s+", "").matches("\\d+")))
        {
            JOptionPane.showMessageDialog
                    (
                            null,
                            "Điện thoại không được chứa chữ!",
                            "Cảnh báo!",
                            JOptionPane.WARNING_MESSAGE
                    );
            txtEmployeePhone.setText("");
            txtEmployeePhone.requestFocus();
            return false;
        }
        return true;
    }
    
    private void createData()
    {
        String employeeID = txtEmployeeID.getText();
        String employeeFullName = txtEmployeeFullName.getText();
        int employeeYOB = (int) spnEmployeeYOB.getValue();
        String employeeGender = (radMale.isSelected()) ? "Nam" : "Nữ";
        String employeePhone = txtEmployeePhone.getText().replaceAll("\\s+", "");
        String employeePosition = (String) cmbPositionName.getSelectedItem();
        String selectPosition =
                """
                        SELECT SC.PositionID
                        FROM SalaryCoefficient SC
                        WHERE SC.PositionName = ?
                        """;
        String positionID = "";
        try
                (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(selectPosition);
                )
        {
            pstmt.setString(1, employeePosition);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                positionID = rs.getString("PositionID");
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog
                    (
                            null,
                            "Lỗi: " + e.getMessage(),
                            "Lỗi!",
                            JOptionPane.ERROR_MESSAGE
                    );
            return;
        }
        
        String employeeDegree = (String) cmbDegreeName.getSelectedItem();
        String selectDegreeID =
                """
                        SELECT D.DegreeID
                        FROM Degree D
                        WHERE D.DegreeName = ?
                        """;
        String degreeID = "";
        try
                (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(selectDegreeID))
        {
            pstmt.setString(1, employeeDegree);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                degreeID = rs.getString("DegreeID");
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog
                    (
                            null,
                            "Lỗi: degree " + e.getMessage(),
                            "Lỗi!",
                            JOptionPane.ERROR_MESSAGE
                    );
            return;
        }
        String insertData =
                """
                        INSERT INTO Employee VALUES
                        (?, ?, ?, ?, ?, ?, ?, ?)
                        """;
        try
                (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(insertData))
        {
            pstmt.setString(1, employeeID);
            pstmt.setString(2, employeeFullName);
            pstmt.setInt(3, employeeYOB);
            pstmt.setString(4, employeeGender);
            pstmt.setString(5, employeePhone);
            pstmt.setString(6, positionID);
            pstmt.setString(7, degreeID);
            pstmt.setString(8, null);
            pstmt.execute();
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog
                    (
                            null,
                            "Lỗi: " + e.getMessage(),
                            "Lỗi!",
                            JOptionPane.ERROR_MESSAGE
                    );
        }
        JOptionPane.showMessageDialog
                (
                        null,
                        "Thêm nhân viên thành công!",
                        "Thông báo!",
                        JOptionPane.INFORMATION_MESSAGE
                );
        setMode("refresh");
    }
    
    public static void main(String[] args)
    {
        JFrame frm = new JFrame("Phầm mềm quản lý nhân sự");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setContentPane(new MainForm().pnlMain);
        frm.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
    }
    
    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }
    
    /**
     Method generated by IntelliJ IDEA GUI Designer
     >>> IMPORTANT!! <<<
     DO NOT edit this method OR call it in your code!
     @noinspection ALL
     */
    private void $$$setupUI$$$()
    {
        pnlMain = new JPanel();
        pnlMain.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(10, 10, 10, 10), -1, -1));
        pnlMain.setBackground(new Color(-1));
        pnlEmployee = new JPanel();
        pnlEmployee.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        pnlMain.add(pnlEmployee, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlInfoTitle = new JPanel();
        pnlInfoTitle.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        pnlEmployee.add(pnlInfoTitle, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lblInfoTitle = new JLabel();
        Font lblInfoTitleFont = this.$$$getFont$$$("Consolas", Font.BOLD, 36, lblInfoTitle.getFont());
        if (lblInfoTitleFont != null)
        {
            lblInfoTitle.setFont(lblInfoTitleFont);
        }
        lblInfoTitle.setText("Thông tin nhân viên");
        pnlInfoTitle.add(lblInfoTitle, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 50), null, 0, false));
        pnlEmployeeInfo = new JPanel();
        pnlEmployeeInfo.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        pnlEmployee.add(pnlEmployeeInfo, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlField = new JPanel();
        pnlField.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        pnlEmployeeInfo.add(pnlField, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lblEmployeeID = new JLabel();
        Font lblEmployeeIDFont = this.$$$getFont$$$("Consolas", Font.BOLD, 28, lblEmployeeID.getFont());
        if (lblEmployeeIDFont != null)
        {
            lblEmployeeID.setFont(lblEmployeeIDFont);
        }
        lblEmployeeID.setText("Mã nhân viên:");
        pnlField.add(lblEmployeeID, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblEmployeeFullName = new JLabel();
        Font lblEmployeeFullNameFont = this.$$$getFont$$$("Consolas", Font.BOLD, 28, lblEmployeeFullName.getFont());
        if (lblEmployeeFullNameFont != null)
        {
            lblEmployeeFullName.setFont(lblEmployeeFullNameFont);
        }
        lblEmployeeFullName.setText("Họ tên:");
        pnlField.add(lblEmployeeFullName, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblEmployeeYOB = new JLabel();
        Font lblEmployeeYOBFont = this.$$$getFont$$$("Consolas", Font.BOLD, 28, lblEmployeeYOB.getFont());
        if (lblEmployeeYOBFont != null)
        {
            lblEmployeeYOB.setFont(lblEmployeeYOBFont);
        }
        lblEmployeeYOB.setText("Năm sinh:");
        pnlField.add(lblEmployeeYOB, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblEmployeePhone = new JLabel();
        Font lblEmployeePhoneFont = this.$$$getFont$$$("Consolas", Font.BOLD, 28, lblEmployeePhone.getFont());
        if (lblEmployeePhoneFont != null)
        {
            lblEmployeePhone.setFont(lblEmployeePhoneFont);
        }
        lblEmployeePhone.setText("Số điện thoại:");
        pnlField.add(lblEmployeePhone, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblPositionName = new JLabel();
        Font lblPositionNameFont = this.$$$getFont$$$("Consolas", Font.BOLD, 28, lblPositionName.getFont());
        if (lblPositionNameFont != null)
        {
            lblPositionName.setFont(lblPositionNameFont);
        }
        lblPositionName.setText("Chức vụ:");
        pnlField.add(lblPositionName, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblDegreeName = new JLabel();
        Font lblDegreeNameFont = this.$$$getFont$$$("Consolas", Font.BOLD, 28, lblDegreeName.getFont());
        if (lblDegreeNameFont != null)
        {
            lblDegreeName.setFont(lblDegreeNameFont);
        }
        lblDegreeName.setText("Bằng cấp:");
        pnlField.add(lblDegreeName, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lbEmployeeGender = new JLabel();
        Font lbEmployeeGenderFont = this.$$$getFont$$$("Consolas", Font.BOLD, 28, lbEmployeeGender.getFont());
        if (lbEmployeeGenderFont != null)
        {
            lbEmployeeGender.setFont(lbEmployeeGenderFont);
        }
        lbEmployeeGender.setText("Giới tính:");
        pnlField.add(lbEmployeeGender, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlInput = new JPanel();
        pnlInput.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 2, new Insets(0, 0, 0, 0), -1, -1));
        pnlEmployeeInfo.add(pnlInput, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        txtEmployeeID = new JTextField();
        txtEmployeeID.setEditable(true);
        txtEmployeeID.setEnabled(false);
        Font txtEmployeeIDFont = this.$$$getFont$$$("Consolas", Font.PLAIN, 28, txtEmployeeID.getFont());
        if (txtEmployeeIDFont != null)
        {
            txtEmployeeID.setFont(txtEmployeeIDFont);
        }
        pnlInput.add(txtEmployeeID, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtEmployeeFullName = new JTextField();
        Font txtEmployeeFullNameFont = this.$$$getFont$$$("Consolas", Font.PLAIN, 28, txtEmployeeFullName.getFont());
        if (txtEmployeeFullNameFont != null)
        {
            txtEmployeeFullName.setFont(txtEmployeeFullNameFont);
        }
        pnlInput.add(txtEmployeeFullName, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtEmployeePhone = new JTextField();
        Font txtEmployeePhoneFont = this.$$$getFont$$$("Consolas", Font.PLAIN, 28, txtEmployeePhone.getFont());
        if (txtEmployeePhoneFont != null)
        {
            txtEmployeePhone.setFont(txtEmployeePhoneFont);
        }
        pnlInput.add(txtEmployeePhone, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        cmbPositionName = new JComboBox();
        Font cmbPositionNameFont = this.$$$getFont$$$("Consolas", Font.PLAIN, 28, cmbPositionName.getFont());
        if (cmbPositionNameFont != null)
        {
            cmbPositionName.setFont(cmbPositionNameFont);
        }
        pnlInput.add(cmbPositionName, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbDegreeName = new JComboBox();
        Font cmbDegreeNameFont = this.$$$getFont$$$("Consolas", Font.PLAIN, 28, cmbDegreeName.getFont());
        if (cmbDegreeNameFont != null)
        {
            cmbDegreeName.setFont(cmbDegreeNameFont);
        }
        pnlInput.add(cmbDegreeName, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnEmployeeYOB = new JSpinner();
        Font spnEmployeeYOBFont = this.$$$getFont$$$("Consolas", Font.PLAIN, 28, spnEmployeeYOB.getFont());
        if (spnEmployeeYOBFont != null)
        {
            spnEmployeeYOB.setFont(spnEmployeeYOBFont);
        }
        pnlInput.add(spnEmployeeYOB, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        radMale = new JRadioButton();
        Font radMaleFont = this.$$$getFont$$$("Consolas", Font.PLAIN, 28, radMale.getFont());
        if (radMaleFont != null)
        {
            radMale.setFont(radMaleFont);
        }
        radMale.setText("Nam");
        pnlInput.add(radMale, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        radFemale = new JRadioButton();
        Font radFemaleFont = this.$$$getFont$$$("Consolas", Font.PLAIN, 28, radFemale.getFont());
        if (radFemaleFont != null)
        {
            radFemale.setFont(radFemaleFont);
        }
        radFemale.setText("Nữ");
        pnlInput.add(radFemale, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlButtons = new JPanel();
        pnlButtons.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        pnlEmployee.add(pnlButtons, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnUpdate = new JButton();
        Font btnUpdateFont = this.$$$getFont$$$("Consolas", Font.BOLD, 26, btnUpdate.getFont());
        if (btnUpdateFont != null)
        {
            btnUpdate.setFont(btnUpdateFont);
        }
        btnUpdate.setText("Sửa");
        pnlButtons.add(btnUpdate, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnDelete = new JButton();
        Font btnDeleteFont = this.$$$getFont$$$("Consolas", Font.BOLD, 26, btnDelete.getFont());
        if (btnDeleteFont != null)
        {
            btnDelete.setFont(btnDeleteFont);
        }
        btnDelete.setText("Xoá");
        pnlButtons.add(btnDelete, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnRead = new JButton();
        Font btnReadFont = this.$$$getFont$$$("Consolas", Font.BOLD, 28, btnRead.getFont());
        if (btnReadFont != null)
        {
            btnRead.setFont(btnReadFont);
        }
        btnRead.setText("Xem");
        pnlButtons.add(btnRead, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnAdd = new JButton();
        Font btnAddFont = this.$$$getFont$$$("Consolas", Font.BOLD, 28, btnAdd.getFont());
        if (btnAddFont != null)
        {
            btnAdd.setFont(btnAddFont);
        }
        btnAdd.setText("Thêm");
        pnlButtons.add(btnAdd, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnRefresh = new JButton();
        Font btnRefreshFont = this.$$$getFont$$$("Consolas", Font.BOLD, 26, btnRefresh.getFont());
        if (btnRefreshFont != null)
        {
            btnRefresh.setFont(btnRefreshFont);
        }
        btnRefresh.setText("Làm mới");
        pnlButtons.add(btnRefresh, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnCancel = new JButton();
        Font btnCancelFont = this.$$$getFont$$$("Consolas", Font.BOLD, 28, btnCancel.getFont());
        if (btnCancelFont != null)
        {
            btnCancel.setFont(btnCancelFont);
        }
        btnCancel.setText("Huỷ");
        pnlButtons.add(btnCancel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnEmployeeList = new JPanel();
        pnEmployeeList.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        pnlMain.add(pnEmployeeList, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlListTitle = new JPanel();
        pnlListTitle.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        pnEmployeeList.add(pnlListTitle, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lblLIstTitle = new JLabel();
        Font lblLIstTitleFont = this.$$$getFont$$$("Consolas", Font.BOLD, 36, lblLIstTitle.getFont());
        if (lblLIstTitleFont != null)
        {
            lblLIstTitle.setFont(lblLIstTitleFont);
        }
        lblLIstTitle.setText("Danh sách nhân viên");
        pnlListTitle.add(lblLIstTitle, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 50), null, 0, false));
        scpEmployeeList = new JScrollPane();
        pnEmployeeList.add(scpEmployeeList, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(750, -1), null, 0, false));
        tblEmployeeList = new JTable();
        Font tblEmployeeListFont = this.$$$getFont$$$("Consolas", Font.PLAIN, 24, tblEmployeeList.getFont());
        if (tblEmployeeListFont != null)
        {
            tblEmployeeList.setFont(tblEmployeeListFont);
        }
        scpEmployeeList.setViewportView(tblEmployeeList);
    }
    
    /**
     @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont)
    {
        if (currentFont == null)
        {
            return null;
        }
        String resultName;
        if (fontName == null)
        {
            resultName = currentFont.getName();
        }
        else
        {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1'))
            {
                resultName = fontName;
            }
            else
            {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }
    
    /**
     @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    {
        return pnlMain;
    }
    
}
