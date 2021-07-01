package ControlPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A class which represents the main window of the control panel.
 * Extends JFrame to be the window, and implements ActionListener to process
 * actions and Runnable to work.
 */
public class MainForm extends JFrame implements ActionListener, Runnable{
    ControlPanelRequests cpr;

    private JPanel outerPanel;
    private JPanel Login;
    private JPanel Main;
    private JPanel mainWindow;
    private JLabel loginLabel;
    private JPanel interactionPanel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameTextField;
    private JTextField passwordTextField;
    private JButton loginButton;
    private JTabbedPane tabbedPane1;
    private JPanel createBillboardTab;
    private JPanel listBillboardsTab;
    private JPanel scheduleBillboardsTab;
    private JPanel editUsersTab;
    private JPanel logoutTab;
    private JButton logoutButton;
    private JLabel logoutLabel;
    private JButton createBillboardButton;
    private JTextField createBillboardNameTextField;
    private JTextField createBillboardBackgroundColourTextField;
    private JTextField createBillboardMessageTextField;
    private JTextField createBillboardMessageColourTextField;
    private JTextField createBillboardPictureTextField;
    private JTextField createBillboardInformationTextField;
    private JTextField createBillboardInformationColourTextField;
    private JPanel editBillboardWindow;
    private JTextField editBillboardNameField;
    private JTextField editBillboardBgColourField;
    private JTextField editBillboardMessageField;
    private JTextField editBillboardMessageColourField;
    private JTextField editBillboardPictureField;
    private JTextField editBillboardInfoField;
    private JTextField editBillboardInfoColourField;
    private JPanel formPanel;
    private JButton editBillboardButton;
    private JPanel weekViewTab;
    private JPanel dayViewTab;
    private JPanel daysPanel;
    private JPanel editScheduleWindow;
    private JTextField editScheduleTimeField;
    private JTextField editScheduleDurationField;
    private JTextField editScheduleReccurencePeriodField;
    private JComboBox editScheduleRecurrenceOptions;
    private JPanel editScheduleForm;
    private JButton confirmEditScheduleButton;
    private JTextField editScheduleNameField;
    private JButton deleteBillboardButton;
    private JPanel createUserWindow;
    private JPanel editUserForm;
    private JTextField editUserUsernameField;
    private JPasswordField editUserPasswordField;
    private JCheckBox editUserCreateBillboardCheckBox;
    private JCheckBox editUserEditBillboardsCheckBox;
    private JCheckBox editUserScheduleBillboardsCheckBox;
    private JCheckBox editUserEditUserCheckBox;
    private JButton editUserSubmitButton;
    private JButton editBillboardPreviewButton;
    private JButton previewButton;

    CardLayout card;
    Container c;

    // The username of the currently signed in user
    private String username;

    /**
     * The constructor for MainForm.
     * @param title The title of the main window.
     * @throws IOException Throws if connection to the server goes wrong somehow.
     */
    public MainForm(String title) throws IOException{
        super(title);
        // Forces logout if window is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cpr.logout(username);
                super.windowClosing(e);
            }
        });
        cpr = new ControlPanelRequests(System.getProperty("user.dir") + "/BillboardViewer/");
        c = getContentPane();
        card = (CardLayout)outerPanel.getLayout();
        c.setLayout(card);
        loginButton.addActionListener(this);
        logoutButton.addActionListener(this);
        createBillboardButton.addActionListener(this);
        // Refreshes tabs whenever each tab is clicked on
        tabbedPane1.addChangeListener(e -> {
            System.out.println("Tab: " + tabbedPane1.getSelectedIndex());
            if (tabbedPane1.getSelectedIndex() == 1){
                listBillboardsTab.removeAll();
                System.out.println("Generating billboard list...");
                generateBillboardList();
            } else if (tabbedPane1.getSelectedIndex() == 3){
                editUsersTab.removeAll();
                System.out.println("Generating user list...");
                generateUsers();
            } else if (tabbedPane1.getSelectedIndex() == 2){
                daysPanel.removeAll();
                System.out.println("Generating schedule...");
                generateSchedule();
            }
        });
        setDefaultLookAndFeelDecorated(true);
        setSize(1000, 750);
        setContentPane(outerPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Method that catches actions performed by components
     * @param e The action details.
     */
    @Override
    public void actionPerformed(ActionEvent e){
        if (e.getActionCommand().contains("Login")) {
            username = usernameTextField.getText();
            String password = passwordTextField.getText();
            if (cpr.login(username, password)) {
                card.show(getContentPane(), "Main");
                tabbedPane1.setSelectedIndex(0);
                usernameTextField.setText("");
                passwordTextField.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Username or password is incorrect.");
            }
        } else if (e.getActionCommand().contains("Logout")) {
            cpr.logout(username);
            username = "";
            // Clear all components
            for (var component : createBillboardTab.getComponents()){
                try {
                    JTextField field = (JTextField)component;
                    field.setText("");
                } catch (Exception ignored){}
            }
            listBillboardsTab.removeAll();
            editUsersTab.removeAll();
            // Go back to login screen
            card.previous(getContentPane());
        } else if (e.getActionCommand().contains("Create Billboard")) {
            Billboard tempBillboard = new Billboard();
            String tempName = createBillboardNameTextField.getText();
            String tempBGColour = createBillboardBackgroundColourTextField.getText();
            String tempMessage = createBillboardMessageTextField.getText();
            String tempMessageColour = createBillboardMessageColourTextField.getText();
            String tempPicture = createBillboardPictureTextField.getText();
            String tempURL;
            String tempData;
            // Check if picture is URL or Base64 data
            if (tempPicture.matches("http[s]://.*")) {
                tempURL = tempPicture;
                tempData = "";
            } else {
                tempURL = "";
                tempData = tempPicture;
            }
            String tempInfo = createBillboardInformationTextField.getText();
            String tempInfoColor = createBillboardInformationColourTextField.getText();
            if (tempBillboard.fromStrings(tempName, tempBGColour, tempMessage, tempMessageColour, tempURL, tempData, tempInfo, tempInfoColor, username)) {
                if (cpr.createBillboard(tempBillboard)) {
                    Alert("Billboard created");
                } else {
                    Alert("Something went wrong!");
                }
            } else {
                Alert("All required fields have not been filled in.");
            }
        }
        // Changes permissions for a user
        else if (e.getActionCommand().contains("Perms -")) {
            String editedUsername = e.getActionCommand().substring(8);

            ArrayList<String> editedUserPerms = new ArrayList<>();
            int editedUserIndex = -1;

            for (var user : clientSideUsers){
                JTextField usernameField = (JTextField)user.get(0);
                if (usernameField.getText().toLowerCase().equals(editedUsername.toLowerCase())){
                    editedUserIndex = clientSideUsers.indexOf(user);
                }
            }
            // Makes sure the user exists
            if (editedUserIndex >= 0) {
                ArrayList<Object> user = clientSideUsers.get(editedUserIndex);
                for (int i = 1; i < user.size(); i++){
                    JComboBox<?> tempPermComboBox = (JComboBox<?>)user.get(i);
                    if (tempPermComboBox.getSelectedItem() == "X") {
                        switch (i) {
                            case 1:
                                editedUserPerms.add("Create Billboards");
                                break;
                            case 2:
                                editedUserPerms.add("Edit All Billboards");
                                break;
                            case 3:
                                editedUserPerms.add("Schedule Billboards");
                                break;
                            case 4:
                                editedUserPerms.add("Edit Users");
                                break;
                        }
                    }
                }
                if (cpr.setPermissions(editedUsername, editedUserPerms)){
                    Alert("Permissions successfully changed!");
                } else {
                    Alert("Something went wrong!");
                }
                refreshUsers();
            }
        }
        // Changes user's password
        else if (e.getActionCommand().contains("Pass -")){
            String username = e.getActionCommand().split(" - ")[1];
            Object newPass = JOptionPane.showInputDialog(
                    editUsersTab,
                    "Input a new password: ",
                    "Edit password",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null);
            if (newPass != null){
                if (cpr.setPassword(username, String.valueOf(newPass.hashCode()))){
                    Alert("Password successfully changed!");
                } else {
                    Alert("Something went wrong!");
                }
            }
        }
        // Edits billboard
        else if (e.getActionCommand().contains("Edit - ")){
            String billboardName = e.getActionCommand().split(" - ")[1];
            try {
                editBillboard(billboardName);
            } catch (Exception error){
                System.out.println(error);
                if (e.toString().contains("Invalid billboard name")){
                    Alert("That billboard apparently doesn't exist!");
                } else if (e.toString().contains("Invalid")){
                    Alert("Invalid billboard!");
                }
            }
        }
        // Deletes a user
        else if (e.getActionCommand().contains("Delete -")){
            String userToDelete = e.getActionCommand().split(" - ")[1];
            if (cpr.deleteUser(userToDelete)){
                Alert("User deleted successfully!");
            } else {
                Alert("Something went wrong!");
            }
            refreshUsers();
        } else if (e.getActionCommand().equals("Create User Window")){
            createUser();
        }else if (e.getActionCommand().equals("Create User")){
            String username = editUserUsernameField.getText();
            String password = String.valueOf(new String(editUserPasswordField.getPassword()).hashCode());
            ArrayList<String> perms = new ArrayList<>();
            if (editUserCreateBillboardCheckBox.isSelected()){
                perms.add("Create Billboards");
            }
            if (editUserEditBillboardsCheckBox.isSelected()){
                perms.add("Edit All Billboards");
            }
            if (editUserScheduleBillboardsCheckBox.isSelected()){
                perms.add("Schedule Billboards");
            }
            if (editUserEditUserCheckBox.isSelected()){
                perms.add("Edit Users");
            }
            if (cpr.createUser(username, perms, password)){
                Alert("User successfully created!");
            } else {
                Alert("User not created!");
            }
            refreshUsers();
            // Clears edit user window
            editUserUsernameField.setText("");
            editUserPasswordField.setText("");
            editUserCreateBillboardCheckBox.setSelected(false);
            editUserEditBillboardsCheckBox.setSelected(false);
            editUserScheduleBillboardsCheckBox.setSelected(false);
            editUserEditUserCheckBox.setSelected(false);
        }
    }

    /**
     * Refreshes the edit users tab
     */
    private void refreshUsers(){
        editUsersTab.removeAll();
        generateUsers();
        setVisible(false);
        setVisible(true);
    }

    /**
     * Creates a window with a form to edit a billboard
     * @param name The name of the billboard to edit.
     * @throws Exception Throws an error if it is not successful.
     */
    private void editBillboard(String name) throws Exception{
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame newWindow = new JFrame("Edit Billboard");
        newWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Billboard billboard = cpr.getBillboard(name);

        // Is the picture in Base64 or not?
        boolean isData = false;

        if (billboard.name != null){
            // Fills in the known values into the form
            editBillboardNameField.setText(billboard.name);
            editBillboardNameField.setEditable(false);
            editBillboardBgColourField.setText(billboard.backgroundColour);
            editBillboardMessageField.setText(billboard.message);
            editBillboardMessageColourField.setText(billboard.messageColour);
            if (billboard.pictureData != null) {
                editBillboardPictureField.setText(billboard.pictureData);
                isData = true;
            } else {
                editBillboardPictureField.setText(billboard.pictureURL);
            }
            editBillboardInfoField.setText(billboard.infoMessage);
            editBillboardInfoColourField.setText(billboard.infoColour);

            Billboard finalBillboard = billboard;
            boolean finalIsData = isData;
            // Adds action listener to send the
            // edited billboard to the ControlPanelRequests.
            editBillboardButton.addActionListener(e -> {
                if (finalIsData){
                finalBillboard.fromStrings(
                        editBillboardNameField.getText(),
                        editBillboardBgColourField.getText(),
                        editBillboardMessageField.getText(),
                        editBillboardMessageColourField.getText(),
                        "",
                        editBillboardPictureField.getText(),
                        editBillboardInfoField.getText(),
                        editBillboardInfoColourField.getText(),
                        username
                );} else {
                    finalBillboard.fromStrings(
                            editBillboardNameField.getText(),
                            editBillboardBgColourField.getText(),
                            editBillboardMessageField.getText(),
                            editBillboardMessageColourField.getText(),
                            editBillboardPictureField.getText(),
                            "",
                            editBillboardInfoField.getText(),
                            editBillboardInfoColourField.getText(),
                            username
                    );
                }
                if (cpr.editBillboard(finalBillboard)){
                    Alert("Successfully edited!");
                } else {
                    Alert("Something went wrong!");
                }
                for (ActionListener a : editBillboardButton.getActionListeners()){
                    editBillboardButton.removeActionListener(a);
                }
                newWindow.dispose();
            });

            // Adds action listener to delete a billboard
            deleteBillboardButton.addActionListener(e -> {
                if (cpr.deleteBillboard(editBillboardNameField.getText())){
                    Alert("Successfully deleted!");
                    for (ActionListener a: deleteBillboardButton.getActionListeners()){
                        deleteBillboardButton.removeActionListener(a);
                    }
                    newWindow.dispose();
                    listBillboardsTab.removeAll();
                    generateBillboardList();
                    // This refreshes the window, immediately reflecting the changes
                    setVisible(false);
                    setVisible(true);
                } else {
                    Alert("Something went wrong!");
                }
            });

            // Previews a billboard
            editBillboardPreviewButton.addActionListener(e -> {
                ViewerPreview.previewBillboard(
                        editBillboardBgColourField.getText(),
                        editBillboardMessageField.getText(),
                        editBillboardMessageColourField.getText(),
                        editBillboardPictureField.getText(),
                        editBillboardInfoField.getText(),
                        editBillboardInfoColourField.getText()
                );
            });

            newWindow.getContentPane().add(formPanel, BorderLayout.CENTER);

            newWindow.setSize(600, 400);

            newWindow.setVisible(true);
        } else {
            throw new Exception("Invalid billboard name!");
        }
    }

    /**
     * Creates a new window with the message.
     * @param message The message to display.
     */
    public static void Alert(String message){
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Generates the list of billboards as buttons
     */
    private void generateBillboardList(){
        ArrayList<String> perms = cpr.getPermissions(username);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx=0;
        c.gridy=0;
        ArrayList<Billboard> billboards = cpr.getBillboards();
        // This loops through the billboards
        // it starts at 1 so the % works to make it in two columns
        for (int i = 1; i<= billboards.size(); i++){
            JButton tempButton = new JButton(billboards.get(i - 1).name);
            if (perms.contains("Edit All Billboards")) {
                tempButton.setActionCommand("Edit - " + billboards.get(i - 1).name);
                tempButton.addActionListener(this);
            } else if (perms.contains("Create Billboards")){
                if (billboards.get(i - 1).creator.equals(username)){
                    if (cpr.getSchedule().entries != null){
                        ArrayList<String> names = new ArrayList<>();
                        for (var entry : cpr.getSchedule().entries){
                            Billboard tempBillboard = (Billboard)entry.get(0);
                            names.add(tempBillboard.name);
                        }
                        if (!names.contains(billboards.get(i-1).name)) {
                            tempButton.setActionCommand("Edit - " + billboards.get(i-1).name);
                            tempButton.addActionListener(this);
                        }
                    }
                }
            }
            listBillboardsTab.add(tempButton, c);
            if (i%2==0 && i!=0){
                c.gridy++;
            }
            c.gridx=i%2;
        }
    }

    /**
     * List of users and permissions that the window currently has
     */
    ArrayList<ArrayList<Object>> clientSideUsers = new ArrayList<ArrayList<Object>>();

    /**
     * Generates the list of users and their permissions
     */
    private void generateUsers() {
        ArrayList<String> userPerms = cpr.getPermissions(username);
        ArrayList<String> users;
        if (!userPerms.isEmpty()){
            if (userPerms.contains("Edit Users")){
                users = cpr.getUsers();
                JPanel tableContainer = new JPanel();
                tableContainer.setLayout(new GridBagLayout());
                editUsersTab.add(tableContainer, BorderLayout.NORTH);
                GridBagConstraints c = new GridBagConstraints();
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridy=0;

                c.insets =  new Insets(0, 0, 0, 10);
                c.gridx=0;
                tableContainer.add(new JLabel("Username"), c);

                c.gridx=1;
                tableContainer.add(new JLabel("Create Billboards"), c);

                c.gridx=2;
                tableContainer.add(new JLabel("Edit All Billboards"), c);

                c.gridx=3;
                tableContainer.add(new JLabel("Schedule Billboards"), c);

                c.gridx=4;
                tableContainer.add(new JLabel("Edit Users"), c);

                String[] listItems = {" ", "X"};

                // Creates an array with usernames and their permissions
                ArrayList<ArrayList<String>> usersWithPerms = new ArrayList<>();
                for (String user : users){
                    ArrayList<String> tempUser = new ArrayList<>();
                    tempUser.add(user);
                    tempUser.addAll(cpr.getPermissions(user));
                    usersWithPerms.add(tempUser);
                }

                c.gridx=0;
                c.gridy=1;
                for (ArrayList<String> line: usersWithPerms){
                    ArrayList<Object> tempUserArray = new ArrayList<Object>();

                    JTextField userLabel = new JTextField(line.get(0));
                    userLabel.setEditable(false);
                    userLabel.setMinimumSize(new Dimension(80, 30));
                    tableContainer.add(userLabel, c);
                    tempUserArray.add(userLabel);

                    c.gridx=1;
                    for (int i=1; i<5; i++){
                        if (i == 1 && line.subList(1, line.size()).toString().toLowerCase().contains("create billboards")){
                            JComboBox list = new JComboBox(listItems);
                            list.setSelectedIndex(1);
                            tableContainer.add(list, c);
                            tempUserArray.add(list);
                        } else if (i == 2 && line.subList(1, line.size()).toString().toLowerCase().contains("edit all billboards")){
                            JComboBox list = new JComboBox(listItems);
                            list.setSelectedIndex(1);
                            tableContainer.add(list, c);
                            tempUserArray.add(list);
                        } else if (i == 3 && line.subList(1, line.size()).toString().toLowerCase().contains("schedule billboards")){
                            JComboBox list = new JComboBox(listItems);
                            list.setSelectedIndex(1);
                            tableContainer.add(list, c);
                            tempUserArray.add(list);
                        }
                        // User is not allowed to edit their own edit users permission
                        else if (i == 4 && line.subList(1, line.size()).toString().toLowerCase().contains("edit users")){
                            if (line.subList(0, 1).toString().toLowerCase().contains(username.toLowerCase())){
                                JComboBox list = new JComboBox(listItems);
                                list.setSelectedIndex(1);
                                list.setEnabled(false);
                                tableContainer.add(list, c);
                                tempUserArray.add(list);
                            } else {
                                JComboBox list = new JComboBox(listItems);
                                list.setSelectedIndex(1);
                                tableContainer.add(list, c);
                                tempUserArray.add(list);
                            }
                        } else {
                            JComboBox list = new JComboBox(listItems);
                            list.setSelectedIndex(0);
                            tableContainer.add(list, c);
                            tempUserArray.add(list);
                        }
                        c.gridx+=1;
                    }

                    c.gridx=5;
                    JButton changePassButton = new JButton("Edit password");
                    changePassButton.setActionCommand("Pass - " + line.get(0));
                    changePassButton.addActionListener(this);
                    tableContainer.add(changePassButton, c);

                    c.gridx=6;
                    JButton changePermsButton = new JButton("Edit user permissions");
                    changePermsButton.setActionCommand("Perms - " + line.get(0));
                    changePermsButton.addActionListener(this);
                    tableContainer.add(changePermsButton, c);

                    if (!username.toLowerCase().equals(line.get(0).toLowerCase())) {
                        c.gridx = 7;
                        JButton deleteUserButton = new JButton("Delete user");
                        deleteUserButton.setActionCommand("Delete - " + line.get(0));
                        deleteUserButton.addActionListener(this);
                        tableContainer.add(deleteUserButton, c);
                    }

                    clientSideUsers.add(tempUserArray);
                    c.gridx=0;
                    c.gridy+=1;
                }

                JButton createUserButton = new JButton("Create user");
                createUserButton.setActionCommand("Create User Window");
                createUserButton.addActionListener(this);
                tableContainer.add(createUserButton, c);
            }
            // If user doesn't have edit users permission
            // they can only change their password
            else {
                JButton changePassButton = new JButton("Edit password");
                changePassButton.setActionCommand("Pass - " + username);
                changePassButton.addActionListener(this);
                editUsersTab.add(changePassButton);
            }
        }
    }

    /**
     * Creates a window with a form to create a user
     */
    public void createUser(){
        JFrame newWindow = new JFrame("Create User");
        newWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        editUserSubmitButton.setActionCommand("Create User");
        editUserSubmitButton.addActionListener(this);
        // Adds an action listener to clean up the window and action listeners
        editUserSubmitButton.addActionListener(e -> {
            for (ActionListener a : editUserSubmitButton.getActionListeners()){
                editUserSubmitButton.removeActionListener(a);
            }
            newWindow.dispose();
        });

        newWindow.add(editUserForm);
        newWindow.setSize(600, 400);
        newWindow.setVisible(true);
    }

    /**
     * Generates the next seven days and makes them
     * redirect to each day's schedule
     */
    public void generateSchedule(){
        Schedule schedule = cpr.getSchedule();
        LocalDate today = LocalDate.now();
        // Generates the next week of dates with buttons for each
        for (int i=0; i<7; i++){
            JButton dayButton = new JButton(today.getDayOfWeek().name());
            LocalDate finalToday = today;
            // Adds action listener to display relevant day's schedule
            dayButton.addActionListener(e -> {
                CardLayout card = (CardLayout)scheduleBillboardsTab.getLayout();
                card.next(scheduleBillboardsTab);
                generateDaySchedule(finalToday, schedule);
            });
            daysPanel.add(dayButton);
            today = today.plusDays(1);
        }
    }

    /**
     * Recursively removes every component in a parent
     * @param comp The parent component.
     */
    public void removeChildren(JComponent comp){
        Component[] comps = comp.getComponents();
        if (comps.length > 0){
            for (Component c : comps){
                JComponent child = (JComponent)c;
                removeChildren(child);
            }
        }
        comp.removeAll();
    }

    /**
     * Generates that day's schedule.
     * @param day The day to generate the schedule for.
     * @param schedule The schedule of all billboards.
     */
    public void generateDaySchedule(LocalDate day, Schedule schedule){
        JPanel calendarPanel = new JPanel();
        calendarPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx=0;
        c.gridy=0;
        JButton backButton = new JButton("Back");
        // Adds action listener to go back to the week view
        backButton.addActionListener(e -> {
            CardLayout card = (CardLayout)scheduleBillboardsTab.getLayout();
            removeChildren(dayViewTab);
            dayViewTab.removeAll();
            daysPanel.removeAll();
            generateSchedule();
            card.next(scheduleBillboardsTab);
        });
        calendarPanel.add(backButton, c);

        c.gridx=0;
        c.gridy=1;
        c.insets = new Insets(10, 0, 0, 10);
        // Creates a grid where each column is each hour of the day
        // and each row is each minute
        for (int col=0; col<24; col++){
            for (int row=1; row<=60; row++){
                c.gridx=col;
                c.gridy=row+1;
                JPanel tempPanel = new JPanel();
                tempPanel.setLayout(new BorderLayout());
                tempPanel.setPreferredSize(new Dimension(100, 50));
                JButton tempButton = new JButton("Unscheduled");
                int finalCol = col;
                int finalRow = row;
                // Adds action listener to create new window to create billboard at
                // the relevant time slot
                tempButton.addActionListener(e -> {
                    try {
                        editSchedule(null, LocalDateTime.of(
                                day.getYear(),
                                day.getMonth(),
                                day.getDayOfMonth(),
                                finalCol,
                                finalRow -1
                        ));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                tempButton.setBackground(new Color(67, 71, 68));
                tempButton.addActionListener(e -> {

                });
                JLabel tempLabel;
                if (String.valueOf(row-1).length() == 1){
                    tempLabel = new JLabel(col + ":0" + (row-1));
                } else {
                    tempLabel = new JLabel(col + ":" + (row-1));
                }
                tempLabel.setHorizontalAlignment(JLabel.CENTER);
                tempPanel.add(tempLabel, BorderLayout.NORTH);
                tempPanel.add(tempButton, BorderLayout.CENTER);
                calendarPanel.add(tempPanel, c);
            }
        }

        // Changes unscheduled time slots to reflect
        // scheduled billboards
        for (var entry : schedule.entries){
            LocalDateTime dateTime = (LocalDateTime) entry.get(1);
            LocalDate date = dateTime.toLocalDate();
            int hour = dateTime.getHour();
            int minute = dateTime.getMinute();
            String recurrence = (String) entry.get(3);
            if (recurrence.equals("daily")) {
                placeBillboardDuration(entry, hour, minute, calendarPanel);
            } else if (recurrence.equals("hourly")){
                if (date.equals(day)) {
                    for (int h = hour; h < 24; h++) {
                        placeBillboardDuration(entry, h, minute, calendarPanel);
                    }
                } else if (date.isBefore(day)) {
                    for (int h = 0; h < 24; h++) {
                        placeBillboardDuration(entry, h, minute, calendarPanel);
                    }
                }
            } else {
                int period = Integer.parseInt(recurrence);
                int newMin = minute;
                placeBillboardDuration(entry, hour, minute, calendarPanel);
                if (date.equals(day)) {
                    for (int h = hour; h < 24; h++) {
                        do {
                            newMin += period;
                            placeBillboardDuration(entry, h, newMin%60, calendarPanel);
                        }
                        while (newMin%60+period < 60);
                    }
                } else if (date.isBefore(day)) {
                    placeBillboardDuration(entry, 0, newMin%60, calendarPanel);
                    for (int h = 0; h < 24; h++) {
                        do {
                            newMin += period;
                            placeBillboardDuration(entry, h, newMin%60, calendarPanel);
                        }
                        while (newMin%60+period < 60);
                    }
                }
            }
        }

        JScrollPane scrollableTab = new JScrollPane(calendarPanel);
        scrollableTab.getVerticalScrollBar().setUnitIncrement(16);
        scrollableTab.getHorizontalScrollBar().setUnitIncrement(20);
        dayViewTab.add(scrollableTab);
    }

    /**
     * Inserts a billboard at a given time in the grid.
     * @param entry An ArrayList entry from the schedule containing (Billboard, time, duration, recurrence).
     * @param hour The hour the billboard is scheduled for.
     * @param minute The minute the billboard is scheduled for.
     * @param calendarPanel The parent component.
     */
    void placeBillboardDuration(ArrayList<Object> entry, int hour, int minute, JPanel calendarPanel){
        LocalDateTime dateTime = (LocalDateTime) entry.get(1);
        LocalDate date = dateTime.toLocalDate();

        // Loops through each component and calculates overlapping hours
        // and inserts them accordingly into the grid
        for (Component comp : calendarPanel.getComponents()){
            if (((JComponent)comp).getComponents().length > 0) {
                JLabel tempLabel = (JLabel) ((JComponent) comp).getComponent(0);
                JButton tempButton = (JButton) ((JComponent) comp).getComponent(1);
                int labelHour = Integer.parseInt(tempLabel.getText().split(":")[0]);
                int labelMin = Integer.parseInt(tempLabel.getText().split(":")[1]);
                // If hour and minute are the same, just insert
                if ((hour == labelHour && minute == labelMin)) {
                    placeButton(tempButton, (Billboard)entry.get(0), LocalDateTime.of(date, LocalTime.of(labelHour, labelMin)));
                } else if (minute+(int)entry.get(2) >= 60){ // If it overlaps over two different hours
                    // If it is within the hour but past the starting minute
                    if ((labelHour == hour) && ((labelMin > minute) && (labelMin <= minute+(int)entry.get(2)))){
                        placeButton(tempButton, (Billboard)entry.get(0), LocalDateTime.of(date, LocalTime.of(labelHour, labelMin)));
                    } else if ((labelHour > hour) && labelHour == hour+(minute+(int)entry.get(2))/60){ // If it is the greatest hour it can be
                        // If the label minute is less than the minute overlap
                        if (labelMin < (minute+(int)entry.get(2))%60) {
                            placeButton(tempButton, (Billboard)entry.get(0), LocalDateTime.of(date, LocalTime.of(labelHour, labelMin)));
                        }
                    } else if ((labelHour != 0) && (labelHour > hour) && (labelHour < hour+(minute+(int)entry.get(2))/60)) { // If it is a greater hour
                        placeButton(tempButton, (Billboard)entry.get(0), LocalDateTime.of(date, LocalTime.of(labelHour, labelMin)));
                    }
                } else if ((hour == labelHour) && (labelMin > minute) && (labelMin < (minute+(int)entry.get(2)))){ // If it is within the hour but past the starting minute and doesn't overlap
                    placeButton(tempButton, (Billboard)entry.get(0), LocalDateTime.of(date, LocalTime.of(labelHour, labelMin)));
                }
            }
        }
    }

    /**
     * Changes the unscheduled button to contain the details for the scheduled billboard.
     * @param button The button to modify.
     * @param billboard The billboard to put on the button.
     * @param dateTime The time to edit the billboard at.
     */
    void placeButton(JButton button, Billboard billboard, LocalDateTime dateTime){
        button.setText(billboard.name);
        button.setBackground(new Color(209, 209, 209));
        for (ActionListener a : button.getActionListeners()){
            button.removeActionListener(a);
        }
        // Adds an action listener to edit the billboard
        button.addActionListener(e -> {
            try {
                editSchedule(billboard, dateTime);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * Creates a new window to edit the schedule for billboard at specific time.
     * @param billboard The billboard to edit.
     * @param dateTime The time at which to edit the billboard.
     */
    private void editSchedule(Billboard billboard, LocalDateTime dateTime){
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame newWindow = new JFrame("Edit Billboard");
        newWindow.setLayout(new CardLayout());
        newWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // If billboard exists at that time, create window
        // where user can either delete it or schedule new billboard
        if (billboard != null) {
            JButton deleteButton = new JButton("Delete " + billboard.name + " from the schedule?");
            // Adds action listener to delete the billboard from the schedule
            deleteButton.addActionListener(e -> {
                ArrayList<Object> billboardToUnschedule = new ArrayList<>();

                LocalDateTime newDateTime = null;

                // Gets the starting time for the billboard on the schedule.
                for (var entry : cpr.getSchedule().entries) {
                    if (((Billboard) entry.get(0)).name.equals(billboard.name)) {
                        newDateTime = LocalDateTime.of(
                                dateTime.getYear(),
                                dateTime.getMonth(),
                                dateTime.getDayOfMonth(),
                                ((LocalDateTime) entry.get(1)).getHour(),
                                ((LocalDateTime) entry.get(1)).getMinute()
                        );
                    }
                }

                billboardToUnschedule.add(billboard.name);
                billboardToUnschedule.add(newDateTime.toString());
                if (cpr.unscheduleBillboard(billboardToUnschedule)) {
                    Alert("Billboard removed!");
                    // Cleans up the edit window
                    for (ActionListener actionListener : deleteButton.getActionListeners()) {
                        deleteButton.removeActionListener(actionListener);
                    }
                    dayViewTab.removeAll();
                    generateDaySchedule(newDateTime.toLocalDate(), cpr.getSchedule());
                    newWindow.dispose();
                } else {
                    Alert("Something went wrong!");
                }
            });

            JButton editScheduleButton = new JButton("Schedule new billboard?");
            // Adds action listener to create window to edit the billboard
            editScheduleButton.addActionListener(e -> {
                generateScheduleForm(newWindow, dateTime);
                newWindow.getContentPane().add(editScheduleWindow);
                CardLayout c = (CardLayout) newWindow.getContentPane().getLayout();
                c.next(newWindow.getContentPane());
            });

            JPanel innerPanel = new JPanel();
            innerPanel.setLayout(new GridBagLayout());

            GridBagConstraints c = new GridBagConstraints();
            c.gridx=0;
            c.gridy=0;
            innerPanel.add(deleteButton, c);

            c.gridx=1;
            innerPanel.add(editScheduleButton, c);

            newWindow.getContentPane().add(innerPanel);
        }
        // If no billboard is scheduled for that time,
        // just allows for creating of billboard at that time
        else {
            generateScheduleForm(newWindow, dateTime);
            newWindow.getContentPane().add(editScheduleWindow);
        }

        newWindow.setSize(600, 400);

        newWindow.setVisible(true);
    }

    /**
     * Generates the scheduling billboard window.
     * @param parent The parent component.
     * @param dateTime The dateTime to schedule the billboard at.
     */
    public void generateScheduleForm(JFrame parent, LocalDateTime dateTime){
        editScheduleTimeField.setText(dateTime.toString());
        // Adds action listener to check if all fields are filled in
        confirmEditScheduleButton.addActionListener(ev -> {
            if (editScheduleTimeField.getText().length() == 0 ||
                    editScheduleNameField.getText().length() == 0 ||
                    editScheduleDurationField.getText().length() == 0) {
                Alert("All fields need to be filled in!");
            }
            ArrayList<Object> tempBillboardSchedule = new ArrayList<>();
            for (Billboard b : cpr.getBillboards()){
                if (b.name.equals(editScheduleNameField.getText())){
                    tempBillboardSchedule.add(b.name);
                }
            }
            try {
                if (tempBillboardSchedule.get(0) != null) {
                    tempBillboardSchedule.add(editScheduleDurationField.getText());
                    tempBillboardSchedule.add(editScheduleTimeField.getText());
                    if (Objects.equals(editScheduleRecurrenceOptions.getSelectedItem(), "Daily")){
                        tempBillboardSchedule.add(editScheduleRecurrenceOptions.getSelectedItem().toString().toLowerCase());
                    } else if (Objects.equals(editScheduleRecurrenceOptions.getSelectedItem(), "Hourly")){
                        tempBillboardSchedule.add(editScheduleRecurrenceOptions.getSelectedItem().toString().toLowerCase());
                    } else if (editScheduleRecurrenceOptions.getSelectedItem().equals("Every x mins")){
                        if (editScheduleReccurencePeriodField.getText().length() > 0){
                            tempBillboardSchedule.add(editScheduleReccurencePeriodField.getText());
                        } else {
                            Alert("All fields need to be filled in!");
                        }
                    }
                    if (cpr.scheduleBillboard(tempBillboardSchedule)){
                        Alert("Billboard successfully scheduled!");
                    } else {
                        Alert("Something went wrong!");
                    }
                    dayViewTab.removeAll();
                    generateDaySchedule(LocalDateTime.parse(editScheduleTimeField.getText()).toLocalDate(), cpr.getSchedule());
                    for (ActionListener a : confirmEditScheduleButton.getActionListeners()){
                        confirmEditScheduleButton.removeActionListener(a);
                    }
                    parent.dispose();
                    editScheduleNameField.setText("");
                    editScheduleTimeField.setText("");
                    editScheduleDurationField.setText("");
                    editScheduleReccurencePeriodField.setText("");
                }
            } catch (Exception err){
                System.out.println(err.toString());
            }
        });
    }

    /**
     * Necessary to work.
     */
    @Override
    public void run() {

    }

    /**
     * Main to run the main form and check if connection is good.
     * @param args
     */
    public static void main(String[] args){
        try {
            SwingUtilities.invokeLater(new MainForm("Control Panel"));
        } catch (IOException error){
            System.out.println(error.toString());
            Alert("Couldn't connect to the server!");
        }
    }
}
