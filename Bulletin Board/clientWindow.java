import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import components.set.Set;
import components.set.Set2;

public class clientWindow {

    private JFrame frame;
    private JTextArea textField;
    private JTextField textField_2;
    static JTextPane userList;
    static JTextPane mainWindow;

    static Socket chatClient = null;
    static DataInputStream in = null;
    static DataOutputStream out = null;
    static boolean running = false;
    static Thread recv = new Thread(new clientListener());
//    Thread tRecv=new Thread(new RecvThread());

    static String nickName = "";
    static String groupName = "";
    static int id;
    static int groupId;
    final static String CRLF = "\r\n";
    static Set<String> userListSet = new Set2<String>();
    static String userListStr = "";
    static String mainWin = "";

    static class clientListener implements Runnable {
        @Override
        public void run() {
            while (running) {
                try {
                    String headerLine = "";
                    String temp = "";
                    headerLine = in.readUTF();
                    if (headerLine.equals("System Message")) {
                        temp = in.readUTF();
                        mainWin += temp + "\r\n\r\n";
                        mainWindow.setText(mainWin);
                    } else if (headerLine.equals("User Message")) {
                        temp = in.readUTF();
                        mainWin += temp + "\r\n";
                        temp = in.readUTF();
                        mainWin += temp + "\r\n\r\n";
                        mainWindow.setText(mainWin);
                    } else if (headerLine.equals("List Update Add")) {
                        temp = in.readUTF();
                        userListSet.add(temp);
                        userListStr = "";
                        for (Iterator<String> it = userListSet.iterator(); it
                                .hasNext();) {
                            userListStr += it.next() + "\r\n";
                        }
                        userList.setText(userListStr);
                    } else if (headerLine.equals("List Update Delete")) {
                        temp = in.readUTF();
                        userListSet.remove(temp);
                        userListStr = "";
                        for (Iterator<String> it = userListSet.iterator(); it
                                .hasNext();) {
                            userListStr += it.next() + "\r\n";
                        }
                        userList.setText(userListStr);
                    } else if (headerLine.equals("Message Not Found")) {
                        JOptionPane.showMessageDialog(null, "Wrong Message ID");
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    try {
                        in.close();
                        out.close();
                        chatClient.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    running = false;
                    System.out.println("Disconnected");
                    System.exit(0);
                }

            }
        }
    }

    public static void send(String str) {
        try {
            System.out.println(str);
            out.writeUTF(str);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        String returnMessage = "";
        int portNumber = 8888;
        try {
            chatClient = new Socket("127.0.0.1", portNumber);
            System.out.println("Connection estabilished");
            in = new DataInputStream(chatClient.getInputStream());
            out = new DataOutputStream(chatClient.getOutputStream());
            running = true;
            returnMessage = in.readUTF();
            System.out.println(returnMessage);
            if (returnMessage.equals("Cennected! Your Client ID is assigned.")) {
                id = Integer.parseInt(in.readUTF());
                System.out.println("Client ID is: " + id);
            }
        } catch (UnknownHostException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    clientWindow window = new clientWindow();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public clientWindow() {
        this.initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        String returnMessage = "";

        // select group
        String s = "";
        boolean flag2 = true;
        Object[] possibilities = { "Group1", "Group2", "Group3", "Group4",
        "Group5" };
        while (flag2) {
            s = (String) JOptionPane.showInputDialog(this.frame,
                    "Please select a group", "Select A Group",
                    JOptionPane.PLAIN_MESSAGE, null, possibilities, "Group1");
            if (s != null) {
                flag2 = false;
            }
        }
        groupName = s;
        groupId = Integer.parseInt(groupName.substring(groupName.length() - 1)) - 1;
        send("Group Selection");
        send(groupName);
        System.out.println("The group ID is: " + groupId);

        // select nick name
        String r = "";
        boolean flag = true;
        while (flag) {
            r = (String) JOptionPane.showInputDialog(this.frame,
                    "Please type in your nick name", "Select A Nick Name",
                    JOptionPane.PLAIN_MESSAGE, null, null, null);
            if (r != null && r.length() != 0) {
                send("Nick Name Selection");
                send(r);
                try {
                    returnMessage = in.readUTF();
                    System.out.println(returnMessage);
                    if (returnMessage.equals("Success")) {
                        flag = false;
                    } else {
                        JOptionPane
                        .showMessageDialog(null,
                                "The name has been used. Please select a new one.");
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        nickName = r;

        this.frame = new JFrame();
        this.frame.setBounds(100, 100, 900, 600);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getContentPane().setLayout(null);

        this.mainWindow = new JTextPane();
        this.mainWindow.setEditable(false);
        this.mainWindow.setBounds(6, 41, 675, 398);
        this.frame.getContentPane().add(this.mainWindow);

        JLabel lblNewLabel = new JLabel("User List");
        lblNewLabel.setBounds(759, 17, 61, 16);
        this.frame.getContentPane().add(lblNewLabel);

        this.userList = new JTextPane();
        this.userList.setEditable(false);
        this.userList.setBounds(693, 41, 201, 398);
        this.frame.getContentPane().add(this.userList);

        this.textField = new JTextArea();
        this.textField.setBounds(6, 451, 675, 121);
        this.frame.getContentPane().add(this.textField);
        this.textField.setColumns(10);

        JButton bSend = new JButton("Send");
        bSend.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String l2 = clientWindow.this.textField.getText();
                if (l2 != null && l2.length() != 0) {
                    clientWindow.this.textField.setText("");
                    String l1 = "";
                    l1 += nickName
                            + new SimpleDateFormat(" @ yyyy-MM-dd HH:mm:ss")
                    .format(Calendar.getInstance().getTime());
                    send("User Message");
                    send(l1);
                    send(l2);
                }
            }
        });
        bSend.setBounds(684, 519, 210, 53);
        this.frame.getContentPane().add(bSend);

        this.textField_2 = new JTextField();
        this.textField_2.setColumns(10);
        this.textField_2.setBounds(693, 483, 52, 28);
        this.frame.getContentPane().add(this.textField_2);

        JButton bRetHis = new JButton("Retrieve History");
        bRetHis.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String input = clientWindow.this.textField_2.getText();
                if (input != null && input.length() != 0) {
                    clientWindow.this.textField_2.setText("");
                    send("Retrieve History");
                    send(input);
                }
            }
        });

        bRetHis.setBounds(746, 484, 148, 29);
        this.frame.getContentPane().add(bRetHis);

        JButton bExit = new JButton("Exit");
        bExit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    in.close();
                    out.close();
                    chatClient.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                System.exit(0);
            }
        });
        bExit.setBounds(684, 451, 210, 29);
        this.frame.getContentPane().add(bExit);

        JLabel header = new JLabel("Hi " + nickName
                + ", Welcome to join the discusstion of " + groupName);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
        header.setBounds(6, 6, 675, 27);
        this.frame.getContentPane().add(header);

        //set users list and last two history message
        try {
            //discard unnecessary system messages
            returnMessage = in.readUTF();
            returnMessage = in.readUTF();
            returnMessage = in.readUTF();
            returnMessage = in.readUTF();

            returnMessage = in.readUTF();
            System.out.println(returnMessage);
            if (returnMessage.equals("User List")) {
                returnMessage = in.readUTF();
                System.out.println(returnMessage);
                while (!returnMessage.equals("END OF USER LIST")) {
                    userListSet.add(returnMessage);
                    returnMessage = in.readUTF();
                    System.out.println(returnMessage);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (Iterator<String> it = userListSet.iterator(); it.hasNext();) {
            userListStr += it.next() + "\r\n";
        }
        this.userList.setText(userListStr);
        try {
            // set last two history
            String temp = in.readUTF(); //get history size
            int hisSize = Integer.parseInt(temp);
            System.out.println("This group has " + hisSize + " record(s)");
            int counter = 0;
            while (hisSize > 0 && counter < 2) {
                temp = in.readUTF(); //skip header line
                temp = in.readUTF();
                mainWin += temp + "\r\n";
                temp = in.readUTF();
                mainWin += temp + "\r\n\r\n";
                mainWindow.setText(mainWin);
                hisSize--;
                counter++;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        recv.start();
    }
}
