import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

import components.map.Map;
import components.map.Map.Pair;
import components.map.Map2;
import components.sequence.Sequence;
import components.sequence.Sequence1L;
import components.set.Set;
import components.set.Set2;

public class Server {
    ServerSocket chatServer = null;

    Map<Integer, Client> clientList = new Map2<Integer, Client>();

    Map<Integer, Client> group0 = new Map2<Integer, Client>();
    Map<Integer, String> group0History = new Map2<Integer, String>();
    Set<String> group0Users = new Set2<String>();

    Map<Integer, Client> group1 = new Map2<Integer, Client>();
    Map<Integer, String> group1History = new Map2<Integer, String>();
    Set<String> group1Users = new Set2<String>();

    Map<Integer, Client> group2 = new Map2<Integer, Client>();
    Map<Integer, String> group2History = new Map2<Integer, String>();
    Set<String> group2Users = new Set2<String>();

    Map<Integer, Client> group3 = new Map2<Integer, Client>();
    Map<Integer, String> group3History = new Map2<Integer, String>();
    Set<String> group3Users = new Set2<String>();

    Map<Integer, Client> group4 = new Map2<Integer, Client>();
    Map<Integer, String> group4History = new Map2<Integer, String>();
    Set<String> group4Users = new Set2<String>();

    Sequence<Map<Integer, Client>> groups = new Sequence1L<Map<Integer, Client>>();
    Sequence<Map<Integer, String>> history = new Sequence1L<Map<Integer, String>>();
    Sequence<Set<String>> users = new Sequence1L<Set<String>>();

    boolean running = false;
    int clientId = 0;

    public static void main(String[] args) {
        System.out.println("Chat Server is Running");
        new Server().start();
    }

    public void start() {
        try {
            this.groups.add(0, this.group0);
            this.groups.add(1, this.group1);
            this.groups.add(2, this.group2);
            this.groups.add(3, this.group3);
            this.groups.add(4, this.group4);

            this.history.add(0, this.group0History);
            this.history.add(1, this.group1History);
            this.history.add(2, this.group2History);
            this.history.add(3, this.group3History);
            this.history.add(4, this.group4History);

            this.users.add(0, this.group0Users);
            this.users.add(1, this.group1Users);
            this.users.add(2, this.group2Users);
            this.users.add(3, this.group3Users);
            this.users.add(4, this.group4Users);

            int portNumber = 8888;
            this.chatServer = new ServerSocket(portNumber);
            this.running = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while (this.running) {
            try {
                Socket newConnection = this.chatServer.accept();
                Client newClient = new Client(newConnection, this.clientId);
                this.clientList.add(this.clientId, newClient);
                System.out
                        .println("Client " + this.clientId + " is connected.");
                this.clientId++;
                new Thread(newClient).start();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    class Client implements Runnable {
        Socket clientScoket;
        boolean running = false;
        DataInputStream in;
        DataOutputStream out;
        String nickName = "";
        int myID;
        int groupID;

        public Client(Socket newConnection, int clientID) {
            this.clientScoket = newConnection;
            try {
                this.in = new DataInputStream(
                        this.clientScoket.getInputStream());
                this.out = new DataOutputStream(
                        this.clientScoket.getOutputStream());
//                this.br = new BufferedReader(new InputStreamReader(this.in));
                this.running = true;
                this.myID = clientID;
                //send congratulation message
                this.send("Cennected! Your Client ID is assigned.");
                //assign client id
                this.send(this.myID + "");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (this.running) {
                try {
                    String headerLine = "";
                    headerLine = this.in.readUTF();
                    if (headerLine.equals("Nick Name Selection")) {
                        //set client's nick name
                        String temp = this.in.readUTF();
                        if (Server.this.users.entry(this.groupID)
                                .contains(temp)) {
                            this.send("Fail");
                        } else {
                            this.nickName = temp;
                            Server.this.users.entry(this.groupID).add(
                                    this.nickName);
                            this.send("Success");
                            // send system message and add the user to list
                            if (this.groupID == 0) {
                                Server.this.group0.add(this.myID, this);
                                this.sendSystemMessage(Server.this.group0, 0,
                                        this.nickName
                                                + " has joined the group.");
                                this.sendSystemMessage(Server.this.group0, 1,
                                        this.nickName);
                            } else if (this.groupID == 1) {
                                Server.this.group1.add(this.myID, this);
                                this.sendSystemMessage(Server.this.group1, 0,
                                        this.nickName
                                                + " has joined the group.");
                                this.sendSystemMessage(Server.this.group1, 1,
                                        this.nickName);
                            } else if (this.groupID == 2) {
                                Server.this.group2.add(this.myID, this);
                                this.sendSystemMessage(Server.this.group2, 0,
                                        this.nickName
                                                + " has joined the group.");
                                this.sendSystemMessage(Server.this.group2, 1,
                                        this.nickName);
                            } else if (this.groupID == 3) {
                                Server.this.group3.add(this.myID, this);
                                this.sendSystemMessage(Server.this.group3, 0,
                                        this.nickName
                                                + " has joined the group.");
                                this.sendSystemMessage(Server.this.group3, 1,
                                        this.nickName);
                            } else if (this.groupID == 4) {
                                Server.this.group4.add(this.myID, this);
                                this.sendSystemMessage(Server.this.group4, 0,
                                        this.nickName
                                                + " has joined the group.");
                                this.sendSystemMessage(Server.this.group4, 1,
                                        this.nickName);
                            }

                            //send active user list
                            this.send("User List");
                            for (Iterator<String> it = Server.this.users.entry(
                                    this.groupID).iterator(); it.hasNext();) {
                                this.send(it.next());
                            }
                            this.send("END OF USER LIST");

                            //send last two history message
                            int count = 0;
                            int hisSize = Server.this.history.entry(
                                    this.groupID).size();
                            this.send(hisSize + "");
                            for (Iterator<Pair<Integer, String>> it = Server.this.history
                                    .entry(this.groupID).iterator(); it
                                    .hasNext();) {
                                String history = it.next().value();
                                if (count == hisSize - 2
                                        || count == hisSize - 1) {
                                    this.send("User Message");
                                    String l1 = history.substring(0,
                                            history.indexOf("|*****|"));
                                    String l2 = history.substring(history
                                            .indexOf("|*****|") + 7);
                                    this.send(l1);
                                    this.send(l2);
                                }
                                count++;
                            }
                        }

                    } else if (headerLine.equals("Group Selection")) {
                        this.processGroupSelection();
                    } else if (headerLine.equals("User Message")) {
                        int messageID = Server.this.history.entry(this.groupID)
                                .size() + 1;
                        String l1 = "[" + messageID + "] " + this.in.readUTF();
                        String l2 = this.in.readUTF();
                        Server.this.history.entry(this.groupID).add(messageID,
                                l1 + "|*****|" + l2);
                        this.sendUserMessage(
                                Server.this.groups.entry(this.groupID), l1, l2);
                    } else if (headerLine.equals("Retrieve History")) {
                        int requestMessageID = 0;
                        try {
                            requestMessageID = Integer.parseInt(this.in
                                    .readUTF());
                        } catch (Exception e) {
                            requestMessageID = 0;
                        }
                        if (Server.this.history.entry(this.groupID).hasKey(
                                requestMessageID)) {
                            this.send("User Message");
                            String history = Server.this.history.entry(
                                    this.groupID).value(requestMessageID);
                            String l1 = history.substring(0,
                                    history.indexOf("|*****|"));
                            String l2 = history.substring(history
                                    .indexOf("|*****|") + 7);
                            this.send(l1);
                            this.send(l2);
                        } else {
                            this.send("Message Not Found");
                        }

                    }
                } catch (IOException e) {
                    //remove the client from clients and group lists
                    if (Server.this.clientList.hasKey(this.myID)) {
                        Server.this.clientList.remove(this.myID);
                        Server.this.groups.entry(this.groupID)
                                .remove(this.myID);
                        Server.this.users.entry(this.groupID).remove(
                                this.nickName);
                        //send goodbye message
                        this.sendSystemMessage(
                                Server.this.groups.entry(this.groupID), 0,
                                this.nickName + " has leaved the group.");
                        this.sendSystemMessage(Server.this.group0, 2,
                                this.nickName);

                        System.out.println("Client " + this.myID
                                + " is disconnected.");

                        //close the client
                        try {
                            this.in.close();
                            this.out.close();
                            this.clientScoket.close();
                            this.running = false;
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                    }
                }

            }
        }

        public void processGroupSelection() {
            //set client's group
            String group;
            try {
                group = this.in.readUTF();
                if (group.equals("Group1")) {
//                    Server.this.group0.add(this.myID, this);
                    this.groupID = 0;
//                    this.sendSystemMessage(Server.this.group0, 0, this.nickName
//                            + " has joined the group.");
//                    this.sendSystemMessage(Server.this.group0, 1, this.nickName);
                } else if (group.equals("Group2")) {
//                    Server.this.group1.add(this.myID, this);
                    this.groupID = 1;
//                    this.sendSystemMessage(Server.this.group1, 0, this.nickName
//                            + " has joined the group.");
//                    this.sendSystemMessage(Server.this.group1, 1, this.nickName);

                } else if (group.equals("Group3")) {
//                    Server.this.group2.add(this.myID, this);
                    this.groupID = 2;
//                    this.sendSystemMessage(Server.this.group2, 0, this.nickName
//                            + " has joined the group.");
//                    this.sendSystemMessage(Server.this.group2, 1, this.nickName);

                } else if (group.equals("Group4")) {
//                    Server.this.group3.add(this.myID, this);
                    this.groupID = 3;
//                    this.sendSystemMessage(Server.this.group3, 0, this.nickName
//                            + " has joined the group.");
//                    this.sendSystemMessage(Server.this.group3, 1, this.nickName);
                } else if (group.equals("Group5")) {
//                    Server.this.group4.add(this.myID, this);
                    this.groupID = 4;
//                    this.sendSystemMessage(Server.this.group4, 0, this.nickName
//                            + " has joined the group.");
//                    this.sendSystemMessage(Server.this.group4, 1, this.nickName);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void sendSystemMessage(Map<Integer, Client> targetGroup,
                int header, String message) {
            for (Iterator<Pair<Integer, Client>> it = targetGroup.iterator(); it
                    .hasNext();) {
                Client temp = it.next().value();
                if (header == 0) {
                    temp.send("System Message");
                    temp.send("[System] " + message);
                } else if (header == 1) {
                    temp.send("List Update Add");
                    temp.send(message);
                } else {
                    temp.send("List Update Delete");
                    temp.send(message);
                }
            }
        }

        public void sendUserMessage(Map<Integer, Client> targetGroup,
                String line1, String line2) {
            for (Iterator<Pair<Integer, Client>> it = targetGroup.iterator(); it
                    .hasNext();) {
                Client temp = it.next().value();
                temp.send("User Message");
                temp.send(line1);
                temp.send(line2);
            }
        }

        public void send(String str) {
            try {
                this.out.writeUTF(str);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}