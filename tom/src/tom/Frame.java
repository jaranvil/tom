package tom;


import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import com.mysql.jdbc.Connection;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class Frame extends JFrame {

    private JPanel contentPane;
    private JTextField txtName;
    private JTextField textServer;
    private JTextField textChannel;
    private JLabel lblDisconnected;
    private JLabel lblInactivity;
    private JButton btnUp;
    private JButton btnDown;
    private JLabel lblDelay;
    private JComboBox comboBox;
    
    Bot bot = new Bot();

    //launch
    public static void main(String[] args) throws Exception {
       
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Frame frame = new Frame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Frame() {
        setTitle("IRC Bot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 470, 255);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new CardLayout(0, 0));
        
        JPanel panel = new JPanel();
        contentPane.add(panel, "name_176673813986879");
        panel.setLayout(null);
        
        txtMessage = new JTextField();
        txtMessage.setBounds(258, 127, 176, 28);
        panel.add(txtMessage);
        txtMessage.setColumns(10);
        
        JLabel lblBotName = new JLabel("Bot Name:");
        lblBotName.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        lblBotName.setBounds(10, 11, 74, 16);
        panel.add(lblBotName);
        
        txtName = new JTextField();
        txtName.setText("tom");
        txtName.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        txtName.setBounds(80, 9, 135, 20);
        panel.add(txtName);
        txtName.setColumns(10);
        
        textServer = new JTextField();
        textServer.setText("pinebox.ddns.net");
        textServer.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        textServer.setColumns(10);
        textServer.setBounds(80, 40, 135, 20);
        panel.add(textServer);
        
        JLabel lblServer = new JLabel("Server:");
        lblServer.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        lblServer.setBounds(10, 42, 74, 16);
        panel.add(lblServer);
        
        textChannel = new JTextField();
        textChannel.setText("#breakfast");
        textChannel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        textChannel.setColumns(10);
        textChannel.setBounds(80, 71, 135, 20);
        panel.add(textChannel);
        
        JLabel lblChannel = new JLabel("Channel:");
        lblChannel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        lblChannel.setBounds(10, 73, 74, 16);
        panel.add(lblChannel);
        
        JButton btnConnect = new JButton("Connect");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    connect();
                } catch (NickAlreadyInUseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IrcException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        btnConnect.setBounds(20, 163, 89, 23);
        panel.add(btnConnect);
        
        JButton btnDisconnect = new JButton("Disconnet");
        btnDisconnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                disconnect();
            }
        });
        btnDisconnect.setBounds(119, 163, 89, 23);
        panel.add(btnDisconnect);
        
        lblInactivity = new JLabel("Inactivity: 0");
        lblInactivity.setBounds(286, 42, 92, 14);
        panel.add(lblInactivity);
        
        lblDisconnected = new JLabel("Disconnected");
        lblDisconnected.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblDisconnected.setForeground(Color.RED);
        lblDisconnected.setBounds(286, 11, 94, 14);
        panel.add(lblDisconnected);
        
        lblDelay = new JLabel("Action Delay: 0");
        lblDelay.setBounds(286, 80, 92, 14);
        panel.add(lblDelay);
        
        btnUp = new JButton("^");
        btnUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Bot.actionDelay++;
            }
        });
        btnUp.setBounds(387, 64, 41, 23);
        panel.add(btnUp);
        
        btnDown = new JButton("v");
        btnDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Bot.actionDelay--;
            }
        });
        btnDown.setBounds(387, 93, 41, 23);
        panel.add(btnDown);
        
        JButton btnSend = new JButton("Send");
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bot.Reply(txtMessage.getText());
                txtMessage.setText("");
            }
        });
        btnSend.setBounds(298, 166, 89, 23);
        panel.add(btnSend);
        
        comboBox = new JComboBox();
        comboBox.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		if(comboBox.getSelectedIndex()==0)
        		{
        			Bot.waitForName = true;
        		} else {
        			Bot.waitForName = false;
        		}
        	}
        });
        comboBox.setModel(new DefaultComboBoxModel(new String[] {"Wait for name", "Don't wait for name"}));
        comboBox.setBounds(81, 102, 134, 20);
        panel.add(comboBox);
        
        //trying to redirect console output
        
//        JTextArea textConsole2 = new JTextArea();
//        textConsole2.setBounds(225, 7, 768, 317);
//        panel.add(textConsole2);
//        
//        panel.add( new JScrollPane(textConsole2) );
//        MessageConsole mc = new MessageConsole(textConsole2);
//        mc.redirectOut();
//        mc.redirectErr(Color.RED, null);
//        mc.setMessageLines(100);
        
        
        
        Timer timer = new Timer(500 ,taskPerformer);
        timer.start();
        
    }//end constructor
    
    
    public void connect() throws NickAlreadyInUseException, IOException, IrcException
    {
        String server = textServer.getText();
        String channel = textChannel.getText();
        String name = txtName.getText();
        
        bot.setName(name);
        Bot.botName = name;

        // Enable debugging output.
        bot.setVerbose(true);
        
        // Connect to the IRC server.
        bot.connect(server);
        Bot.server = server;

        // Join the #pircbot channel.
        bot.joinChannel(channel);

  }
    
    public void disconnect()
    {
        bot.disconnect();
    }
    

    
  
    ActionListener taskPerformer = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            if(bot.isConnected())
            {
                lblDisconnected.setText("Connected");
                lblDisconnected.setForeground(Color.GREEN);
            } else {
                lblDisconnected.setText("Disconnected");
                lblDisconnected.setForeground(Color.red);
            }
            lblDelay.setText("Action Delay: "+Bot.actionDelay);
            lblInactivity.setText("Inactivity: "+Bot.inactivity);
        }
    };
    private JTextField txtMessage;
}

