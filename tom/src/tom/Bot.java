package tom;

import java.util.Random;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jibble.pircbot.*;

public class Bot extends PircBot {

    //random number object
    Random rand = new Random();
    
    //the channel the current message was received from
    public static String channel;
    //sender of current message
    public static String sender;
    public static String hostname;
    public static boolean waitForName = true;
    //bot name
    public static String botName = "tom";
    public static String server = "";
    //array of words in message
    String[] msgArray;
    
    //number of messages on channel sense bot last spoke
    public static int inactivity = 0;
    //delay in messages before bot looks for opportunity to talk
    public static int actionDelay = 30;
    

    ArrayList<String> messages = new ArrayList<String>();
    ArrayList<String> announcements = new ArrayList<String>();
    
    

    //constructor
    public Bot() {
        this.setName(botName);
    }
   
    //New message on channel
        public void onMessage(String channel, String sender, String login, String hostname, String message){
        Bot.inactivity++;
        Bot.sender = sender;
        Bot.channel = channel;
        Bot.hostname = hostname;
        
        //split msg into words and place in an array
        msgArray = message.split("\\s+");
        
        addLogEntry(message);
        
        //if message came from game server, remove <name> from message and edit sender to match
        formatInGameMsg();
        
        //user messaging check
        checkForMsg(sender);
        tell();
        
        //Requested Actions
        if(inactivity > 0)
        {
             if(checkForName())
             {
                 pageAdmin();
                 leave();
                 changeName();
                 pingURL();
                 mojang();
                 lastSeen();
                 //help();
                 changeNick();
                 rude(); 
                 announce();
                 news();
                 poll();
             }
        }
        
        //Look for conversational replies
        if(inactivity > 0)
         {
              if(checkForName())
              {
                  Replies newReply = new Replies();
                  String reply = newReply.newMessage(msgArray);
                  if(!reply.equals(" "))
                  {
                      Reply(reply);
                  }
              }
         }
    
        //random actions/replies
        if(inactivity > actionDelay)
        {
            lmgtfy();
            caps();
            wave();
            bug();
            aBot();
            greg();
            goodBye();
            deadMsg();
            bored();
            
        }
        
        convert();
        
        if(inactivity > actionDelay)
            vip();
        
        //add time stamp for the sender
        timeStampUser(Bot.sender);
  
    }//end onMessage
    
    //check for use of bot name in message
        public boolean checkForName() {
        	
        	if(waitForName)
        	{
        		String[] names = {this.getName(),
                        this.getName()+"?",
                        this.getName()+":",
                        this.getName()+",",
                        "\""+this.getName()+"\"",
                        this.getName()+"!",
                        this.getName()+"."};
			    if(matchWords(names))
			    {
			        return true;
			    } else {
			        return false;
			    }
        	} else {
        		return true;
        	}
    }
    

    
    //reply methods
         public void randReply(String[] response) {
            int randomNum = rand.nextInt(response.length);
            String reply = response[randomNum];
            
            try {
                Thread.sleep(replyDelay(reply));
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            sendMessage(channel, reply);
            inactivity = 0;
            
     }//end reply
     
         public void Reply(String response) {
         try {
             Thread.sleep(replyDelay(response));
         } catch(InterruptedException ex) {
             Thread.currentThread().interrupt();
         }
         
            sendMessage(channel, response);
            inactivity = 0;
     }//end reply
         
     public void quickReply(String reply)
     {
    	 sendMessage(channel, reply);
         inactivity = 0;
     }
     
     public long replyDelay(String reply)
     {
         long delay;
         int length = reply.length();
         
         if(length < 6)
             delay = 1000;
         else if(length < 12)
             delay = 2000;
         else if(length < 22)
             delay = 3000;
         else
             delay = 4000;
         
         return delay;
     }
     
     
     
     //--------IRC Events---------------------
     
     
        public void onJoin(String channel, String sender, String login, String hostname) 
        {
             Bot.sender = sender;
             checkForMsg(sender);
             timeStampUser(sender);
             
            if(inactivity > actionDelay+10)
            {
                if(randBoolean())
                {
                    String[] replies = {"o/",
                            "Hello "+sender,
                            "OMG it's "+sender,
                            "shut up guys, he signed in",
                            "hey",
                            "WELCOME!!"};
                    randReply(replies);
                }
            }
        }//end on join
        
        public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) 
        {
            
            System.out.println(reason);
            
            //if a Server left the channel
            String[] servers = {"DONUT",
                                "BAGEL"};
            
            for(int x=0;x<servers.length;x++)
            {
                if(sourceNick.equals(servers[x]))
                {
                    if(!reason.equals("Quit: Leaving."))
                    {
                        String ops = getOps();
                        String[] responses = {"Server down! Server down!" + ops + " HELP!",
                                                "Who caused that? Better get" + ops + " here.",
                                                "That's all she wrote folks. Maybe an op can help?" + ops};
                        randReply(responses);
                    }
                }  
            }
        }//end on quit
        
         protected void onAction(String sender, String login, String hostname, String target, String action)
        {
            if(inactivity > actionDelay+10)
                if(randBoolean())
                    sendAction(channel, action+", also");
        }
        
         protected  void onPrivateMessage(String sender, String login, String hostname, String message) 
     {
         String[] messageArray = message.split("\\s+");
         Replies newReply = new Replies();
         String reply = newReply.newMessage(messageArray);
         if(!reply.equals(" "))
         {
             try {
                 Thread.sleep(replyDelay(reply));
             } catch(InterruptedException ex) {
                 Thread.currentThread().interrupt();
             }
             sendMessage(sender, reply);
         }
     }
       
         @Override
         public void onDisconnect() {
             while(!isConnected()) {
                 try {
                     //reconnect();
                     joinChannel(channel);
                 } catch (Exception e) {
                     e.getStackTrace();
                 }
             }
         }
         
         @Override
         public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) 
         {
        	 if(recipientNick.equalsIgnoreCase(Bot.botName))
        	 {
        		 try {
     				connect(server);
     			} catch (IOException | IrcException e) {
     				e.printStackTrace();
     			}
        	 }

             joinChannel(channel);
             
         }
         
         @Override
         public void onPart(String channel, String sender, String login, String hostname) 
         {
        	 if(sender.equalsIgnoreCase(Bot.botName))
        	 {
        		 joinChannel(channel);
        	 }
         }
        
        
        //--------Requested actions-------------------
        
        
        public void pageAdmin() {
            if(inactivity > actionDelay+50){
                String pairs[][] = {{"page", "a"},
                        {"page", "an"},
                        {"page", "admin"},
                        {"get", "help"}};
                String ops = getOps();
                String response[] = {"Paging: " + ops};
                if(matchPairs(pairs))
                    {
                    randReply(response);
                    }
            }
        }//end page admin
        
        public void changeName() {
            String pairs[][] = {{"change", "name"}};
            String temp = "I was pretty fond of " + Bot.botName + "...";
            String response[] = {temp,
                                "sure thing eh"};
            if(matchPairs(pairs))
            {
                randReply(response);
                
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                
                String newName = msgArray[msgArray.length - 1];//last word in message
                
                changeNick(newName);
                this.setName(newName);
                Bot.botName = newName;
            }
        }//end name change
        
        public void leave() {
            
            String words[] = {"exit",
                            "leave",
                            "bye",
                            "quit"};
            String pairs[][] = {{"go", "away"}};
            
            if(matchWords(words) || matchPairs(pairs))
            {
                if(Bot.sender.equalsIgnoreCase("Jared") || Bot.sender.equalsIgnoreCase("Jared_"))
                {
                        disconnect();
                } else {
                    String response[] = {"You can't get rid of me!",
                                        "Jared says I shouldn't listen to you.",
                                        "I'll never leave!",
                                        "haha, no"};
                    if(matchWords(words) || matchPairs(pairs))
                    {
                        randReply(response);
                    }
                }
            }
    
            
        }//end leave

        public void lastSeen()
        {
           String[][] pairs = {{"last", "seen"},
                               {"last", "saw"},
                               {"last", "see"}};
            if(matchPairs(pairs))
            {
                String user = msgArray[msgArray.length-1];
                user = cleanName(user);
                
                ArrayList<String> nicks = new ArrayList<String>();
                nicks = getNicks(user);
                
                for(int x=0;x<nicks.size();x++)
                {
                    String timeStamp = getLastSeen(nicks.get(x));
                    
//                  int timezone = -1;
//                  
//                  for(int x=0;x<msgArray.length;x++)
//                  {
//                      if(validate(msgArray[x]))
//                      {
//                          timezone = Integer.parseInt(msgArray[x]);
//                      }
//                  }
//                  
//                  if(timezone < -12 || timezone > 12)
//                  {
//                      timezone = -1;
//                  }
                  

                  String[] timeArray = timeStamp.split(":");
                  String[] secondArray = timeArray[0].split("\\s+");
                  int hour = Integer.parseInt(secondArray[1]);     
                  
                  if(hour==1)
                      hour = 12;
                  else
                      hour = hour -1;
                  
//                  if(timezone<0)
//                  {
//                      for(int x=0;x<timezone;x--)
//                      {
//                          if(hour == 1)
//                              hour = 12;
//                          else
//                              hour = hour -1;
//                      }
//                  }
//                  
//                  if(timezone>=0)
//                  {
//                      for(int x=0;x<timezone;x++)
//                      {
//                          if(hour == 12)
//                              hour = 1;
//                          else
//                              hour = hour +1;
//                      }
//                  }
                  
                  timeStamp = secondArray[0] + " " + hour + ":" + timeArray[1] + ":" + timeArray[2];

                  
                  if(timeStamp.equals("never"))
                  {
                      Reply("I've never seen someone calls "+nicks.get(x));
                  } else {
                      Reply("Last I saw "+nicks.get(x)+" was "+timeStamp+" EST");
                  }
                  
                  
              }
                }//end for nicks
                
               
        }//end lastSeen
        
        public void mojang()
        {
               String[] words = {"mojang"};
               String[][] pairs = {{"server", "status"},
                                   {"minecraft", "server"},
                                   {"minecraft", "servers"}};
               if(matchWords(words) || matchPairs(pairs))
               {
                   String website = "OFFLINE";
                   String login = "UNKNOWN"; //need to ping on a port other then 80
                   String session = "OFFLINE";
                   String account = "OFFLINE";
                   String auth = "UNKNOWN"; //need to ping on a port other then 80
                   
                   if(ping("minecraft.net"))
                       website = "ONLINE";
                   if(ping("login.minecraft.net"))
                           login = "ONLINE";
                   if(ping("session.minecraft.net"))
                       session = "ONLINE";
                   if(ping("account.mojang.com"))
                       account = "ONLINE";
                   if(ping("auth.mojang.com"))
                       auth = "ONLINE";
                   
                   Reply("Website: "+website+", Login: "+login+", Session: "+session+", Account: "+account+", Auth: "+auth);
               }
                   
        }
     
        public void pingURL()
        {
               String[] words = {"ping"};
               if(matchWords(words))
               {
                   Reply("PING " + msgArray[msgArray.length-1] + " ...");
                   if(ping(msgArray[msgArray.length-1]))
                   {
                       Reply("Pong");
                   } else {
                       Reply("Offline");
                   }
               }
                   
        }
        
        public void help()
        {
            String[][] words = {{Bot.botName, "help"}};
            if(matchPairs(words))
            {
                Reply("I'll pm you my help menu");
                
                sendMessage(sender, "tell <user> <message>");
                sendMessage(sender, "last seen <user>");
                sendMessage(sender, "page admin");
                sendMessage(sender, "fact					   --gives random facts");
                sendMessage(sender, "mojang status             --sortof works");
                sendMessage(sender, "ping <host>               --\"ping\" - try to open socket on port 80");   
                sendMessage(sender, "change name <new name>    --change my name");
                sendMessage(sender, "Suggestions?");
            }
        }
        
        public void changeNick()
        {
            String[][] pairs = {{"go", "by"},
                                {"call", "me"}};
            if(matchPairs(pairs))
            {
                String lastWord = msgArray[msgArray.length-1];
                
                setNick(sender, lastWord);

                String[] response = {"Cool, "+sender+" is also called "+lastWord,
                                    "Thanks for letting me know, "+lastWord,
                                    "I'll remember that "+sender+". Or should I say "+lastWord};
                randReply(response);
                
            }
        }
        
        
        //----------Random actions/replies----------------
        

         public void aBot() {
        
            if(randBoolean())
            {
                String words[] = {"bot"};
                String response[] = {"I ain't no bot",
                                    "better then a human",
                                    "beep beep, boop boop"};
                if(matchWords(words))
                {
                    randReply(response);
                }
            }
            

        }//end aBot
        
        public void bug() {
            if(randBoolean())
            {
                String words[] = {"bug"};
                if(matchWords(words))
                {
                    Reply("it's not a bug, it's a feature");
                }
            }
            

        }//end bug
        
        public void lmgtfy() {
             
             int randomNum = rand.nextInt(10);

             if(randomNum == 1)
             {
                 if(msgArray.length > 10)
                 {
                     if(msgArray[msgArray.length-1].endsWith("?"))
                     {
                        String URL = "http://lmgtfy.com/?q="; 
    
                        for(int x=0;x<msgArray.length;x++) {
                            if(x == 0) {
                                URL = URL + msgArray[x];
                            } else {
                                URL = URL + "+" + msgArray[x];
                            }
                        }//end for
                     Reply(URL);
                    }//end ? if
                 }//end msgArray length
             }//end rand num if
            }//end lmgtfy
         
        public void greg() {
            if(randBoolean())
            {
                String words[] = {"gregtech",
                "greg"};
                String pairs[][] = {{"greg", "tech"}};
                String response[] = {"Don't get me started on greg, "+Bot.sender,
                                "Greg is why bots can't play minecraft"};
                
                if(matchWords(words) || matchPairs(pairs))
                {
                randReply(response);
                }
            }
         }
        
        public void wave()
         {
                String[] words = {"o/"};
                if(matchWords(words))
                    Reply("o/");
         }//end wave
         
        public void caps()
        {
            if(allCaps())
            {
                String[] responses = {"Turn it down a notch "+sender,
                                        "Chill out "+sender,
                                        "SPAM!",
                                        "CAPS!"};
                
                randReply(responses);
                
            }
        }
         
        public void goodBye()
        {
            String words[] = {"goodnight",
                                "gnight",
                                "bye",
                                "seeya",
                                "g2g",
                                "cya"};
            String pairs[][] = {{"good", "bye"}};
            String response[] = {"bye o/",
                                 "see ya later",
                                 "good bye",
                                 "come back soon"};

            if(matchWords(words) || matchPairs(pairs))
            {
                randReply(response);
            }
            
        }
        
        public void deadMsg()
        {
            if(rand.nextInt(4)==1)
            {
                String fours[][] = {{"from", "a", "high", "place"}};
                String[] response = {"do a flip"};
                if(matchFour(fours))
                {
                    randReply(response);
                }
            }
           
        }
        
        public void bored()
        {
            if(inactivity > actionDelay+150)
            {
            	
            	String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\";
                
                File f = new File(path);
                String[] polls = f.list();
                
                String openPolls = "";
                
                for(int x=0;x<polls.length;x++)
                {
                	if(x==0)
                		openPolls = polls[x];
                	else
                		openPolls = openPolls + ", " + polls[x];
                }
                
                if(openPolls.equals(""))
                {
                	 String[] responses = {"I haven't said anything in awhile.",
                             "beep",
                             "Do bots think?",
                             "test message, please ignore"};
                	 randReply(responses);
                } else {
                	Reply("There are open polls! Type \""+Bot.botName+" poll help\" for info.");
                }
               
            }
            
            
            
        }
        
        public void convert()
        {
            if(inactivity>0)
            {
                for(int x=0;x<msgArray.length;x++)
                {
                    if(msgArray[x].charAt(msgArray[x].length()-1) == 'c' || msgArray[x].charAt(msgArray[x].length()-1) == 'C')
                        {
                            String strTemp = msgArray[x];
                            strTemp.replace("c", "");
                            strTemp.replace("C", "");
                            String str="";
                            
                            
                            boolean valid = true;
                            int i = 0;
                            
           
                            while (i < strTemp.length()-1) 
                            {
                                if (!Character.isDigit(strTemp.charAt(i)))
                                {
                                    valid = false;
                                    break;
                                } else {
                                    str = str + strTemp.charAt(i);
                                }
                                i++;
                            }               
                            
                            if(valid)
                            {
                                int intTemp = Integer.parseInt(str);
                                double d = (intTemp*(9/5))+32;
                                int newTemp = (int) d;
                                Reply(intTemp+"\u00b0C = "+newTemp+"\u00b0F");
                            }
                        }
                    if(msgArray[x].charAt(msgArray[x].length()-1) == 'f' || msgArray[x].charAt(msgArray[x].length()-1) == 'F')
                        {
                            String strTemp = msgArray[x];
                            strTemp.replace("f", "");
                            strTemp.replace("F", "");
                            String str="";
                            
                            
                            boolean valid = true;
                            int i = 0;
                            
           
                            while (i < strTemp.length()-1) 
                            {
                                if (!Character.isDigit(strTemp.charAt(i)))
                                {
                                    valid = false;
                                    break;
                                } else {
                                    str = str + strTemp.charAt(i);
                                }
                                i++;
                            }               
                            
                            if(valid)
                            {
                                int intTemp = Integer.parseInt(str);
                                double d = (intTemp-31)/1.8;
                                int newTemp = (int) d;
                                Reply(intTemp+"\u00b0F = "+newTemp+"\u00b0C");
                            }
                           
                        }
                }
            }
        }
        
        
        public void rude() {
            String pairs[][] = {{"fuck", "off"},
                                {"fuck", "you"},
                                {"screw", "you"}};
            String response[] = {"I was just trying to help...",
                                "I'm sorry "+Bot.sender+" :(",
                                "That's rude. Maybe now I won't deliver any of your messages "+Bot.sender,
                                "Bots have feelings too, you know."};
            
            int choice = rand.nextInt(3);
            
            if(matchPairs(pairs))
            {
            	 if(choice==1)
                 {
                     
                     
                     
                     
                         String response2[] = {"fine, I'll leave.",
                                 ":(",
                                 "I don't need to take this abuse!"};
         
         
                         try {
                             Thread.sleep(2000);
                         } catch(InterruptedException ex) {
                             Thread.currentThread().interrupt();
                         }
                         
                         
                         sendMessage(channel, response2[rand.nextInt(4)-1]);
                         inactivity = 0;
                        
                         
                         try {
                             Thread.sleep(2000);
                         } catch(InterruptedException ex) {
                             Thread.currentThread().interrupt();
                         }
                         
                         partChannel(channel);
                         
                         try {
                             Thread.sleep(10000);
                         } catch(InterruptedException ex) {
                             Thread.currentThread().interrupt();
                         }
                         
                         joinChannel(channel);
                         
                     
                         String[] responses = {"jk",
                         					"Oh, I can't leave.",
                         					"SURPRISE",
                         					"I may have overreacted"};
                         				
                        
                         randReply(responses);
                     
                 
                     
                 } else if (choice==2) {
                     String[] responses3 = {"is sad",
                     						"fucks off",
                     						"ignores "+sender};
                     int reply = rand.nextInt(responses3.length);
                     sendAction(channel, responses3[reply]);
                 } else {
                 	if(matchPairs(pairs))
                     {
                         randReply(response);
                     }
                 }
            }
            
           
            
            
        }
        
        public void announce()
        {
            if(msgArray[0].equalsIgnoreCase(this.getName()) && msgArray[1].equalsIgnoreCase("announce"))
            {
                if(msgArray.length > 2)
                {
                    String announcement = "";
                    for(int x=2;x<msgArray.length;x++)
                    {
                        announcement = announcement + " " + msgArray[x];
                    }
                    
                    String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\announcements.txt";
                    
                    File f = new File(path);
                    
                    try {
                        if (!f.exists()) {
                            f.createNewFile();
                        }
                        FileWriter fw = new FileWriter(f.getAbsoluteFile(), true);
                        
                   
                       fw.write(announcement+"\r\n");
                       fw.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Reply("I'll let people know!");
                } else {
                    Reply("Something went wrong");
                }
            }
        }
        
        public void news()
        {
            String[] words = {"news",
                                "new"};
            if(matchWords(words))
            {
                boolean announcement = false;
                String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\announcements.txt";
                
                
                    File f = new File(path);

                    if (f.exists()) {
                        try {
                            
                            BufferedReader br = new BufferedReader(new FileReader(path));
                            
                            String line;
                            while ((line = br.readLine()) != null) {
                              announcements.add(line);
                            }
                            br.close();
                            f.delete();
                            announcement = true;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        
                    }
                       
                    
                    if(announcement)
                    {
                        for(int x = 0;x<announcements.size();x++)
                        {
                            Reply(announcements.get(x));
                        }
                    }
            }
        }
        
        public void vip()
        {

        }
        
        
 //------------User messaging--------------------
        
        public void tell()
        {
            if(msgArray[0].equalsIgnoreCase(this.getName()) && msgArray[1].equalsIgnoreCase("tell"))
            {
                if(msgArray.length > 3)
                {
                    String message = "";
                    for(int x=3;x<msgArray.length;x++)
                    {
                        message = message + " " + msgArray[x];
                    }
                    
                    String user = msgArray[2].replace("|", "");
                    
                    
                    String[] finalMessage = {"Hey " + user + ", " + sender + " says:" + message,
                    					user + ": " + message + ". - From " +sender};
         	
                    
                    
                    message = finalMessage[rand.nextInt(finalMessage.length)];
                    
                    
                    //msgList.add(new Outbox(sender, msgArray[2], message));
                    
                    setMessage(msgArray[2], message);
                    
                    
                    
                    String[] response = {"will do.",
                    					"I'll try.",
                    					msgArray[2] + " you say..  Sure.",
                    					"got it",
                    					"sure",
                    					"maybe"};
                    
               
                    	randReply(response);
                    
                    
                    int num = rand.nextInt(6);
                    if(num==1)
                    {
                    	try {
                            Thread.sleep(6000);
                        } catch(InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    	
                    	String[] resposnes = {"Anyone see " + msgArray[2] + " around?",
                    							msgArray[2].toUpperCase() + "!!"};
                    	randReply(resposnes);
                    	
                    }
                    
                    
                } else {
                    Reply("Something went wrong");
                }
            }
        }//end tell
        
        public void checkForMsg(String sender)
        {
            if(getMessage(sender))
            {
                for(int x=0;x<messages.size();x++)
                {
                    Reply(messages.get(x)); 
                }
                messages.clear();
            }
          
            
//            for(int x=0;x<msgList.size();x++)
//            {
//                if(msgList.get(x).getTo().equalsIgnoreCase(Bot.sender))
//                {
//                    Reply("Hey " + msgList.get(x).getTo() + ", " + msgList.get(x).getFrom() + " says:" + msgList.get(x).getMessage());
//                    msgList.get(x).setTo("sent");
//                }
//            }
        }
        
        
        
//-------------Polls---------------------
        
        public void poll()
        {
        	if(msgArray.length>2)
        	{
        		if(msgArray[1].equalsIgnoreCase("poll") && msgArray[2].equals("help"))
            		pollHelp();
            	if(msgArray[1].equalsIgnoreCase("polls"))
            		listPolls();
            	if(msgArray[1].equalsIgnoreCase("poll") && !msgArray[2].equals("help"))
            		pollInfo();
            	if(msgArray[1].equalsIgnoreCase("vote"))
            		vote();
            	if(msgArray[1].equalsIgnoreCase("newPoll"))
            		newPoll();
            	if(msgArray[1].equalsIgnoreCase("addOption"))
            		addOption();
            	if(msgArray[1].equalsIgnoreCase("results"))	
            		pollResults();
            	if(msgArray[1].equalsIgnoreCase("endPoll"))
            		endPoll();
        	}
        }
        
        public void pollHelp()
        {
        	Reply("I'll pm you info about polls");
            
            sendMessage(sender, Bot.botName+" polls   			 					--list open polls");
            sendMessage(sender, Bot.botName+" poll [poll_name]						--see a poll");
            sendMessage(sender, Bot.botName+" vote [poll_name] [option #]			--vote in a poll");
            sendMessage(sender, Bot.botName+" newPoll [poll_name] [question]		--create a poll");
            sendMessage(sender, Bot.botName+" addOption [poll_name] [option]		--add options to your poll");
            sendMessage(sender, Bot.botName+" results [poll_name]					--see the results of a poll");
            sendMessage(sender, Bot.botName+" endPoll [poll_name]					--end a poll (deletes results!)");
        }
        
        public void listPolls()
        {
        	String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\";
            
            File f = new File(path);
            String[] polls = f.list();
            
            String openPolls = "";
            
            for(int x=0;x<polls.length;x++)
            {
            	if(x==0)
            		openPolls = polls[x];
            	else
            		openPolls = openPolls + ", " + polls[x];
            }
            
            if(openPolls.equals(""))
            	quickReply("No polls currently open. Type \""+Bot.botName+" poll help\" to start your own!");
            else
            	quickReply("Open Polls: " + openPolls);
        }
        
        public void pollInfo()
        {
        	String poll = msgArray[2];
        	String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\"+poll;
    		File f = new File(path);
    		
    		if(f.exists())
    		{
    			String pathOptions= "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\"+poll+"\\options.txt";
    			 try {
                     
                     BufferedReader br = new BufferedReader(new FileReader(pathOptions));
                     
                     ArrayList<String> options = new ArrayList<String>();
                     String line;
                     int counter =1;
                     while ((line = br.readLine()) != null) 
                     {
                       options.add(counter+" - "+line);
                       counter++;
                     }
                     br.close();
                     
                    String pathQuestion = "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\"+poll+"\\question.txt";
             		BufferedReader questionReader = new BufferedReader(new FileReader(pathQuestion));
             		String question = questionReader.readLine();
             		questionReader.close();
             		
             		
             		quickReply(question);
             		for(int x=0;x<options.size();x++)
             		{
             			quickReply(options.get(x));
             		}
             		quickReply("To vote type: "+Bot.botName+" vote "+poll+ " #");
                 } catch (FileNotFoundException e) {
                     e.printStackTrace();
                     quickReply("This poll has no options.");
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
    			
    		} else {
    			quickReply("Poll does not exist");
    		}
        	
        }
        
        public void vote()
        {
        	String poll = msgArray[2];
    		
    		String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\"+poll;
    		File f = new File(path);
    		if(f.exists())
    		{
    			if((!Bot.hostname.contains("kiwiirc.com")))
    			{
    				if(validateInt(msgArray[3]))
        			{
        				String pathVote = "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\"+poll+"\\"+hostname+".txt";
                		File voteFile = new File(pathVote);
                		
                		if(!voteFile.exists())
                		{
                			try {
                				voteFile.createNewFile();
            					FileWriter writer = new FileWriter(voteFile.getAbsoluteFile(), true);
            					writer.write(msgArray[3]);
            					writer.close();
            					Reply("Vote recorded.");
            				} catch (IOException e) {
            					e.printStackTrace();
            				}
                		} else {
                			quickReply("You already voted for that poll. Cheater.");
                		}
        			} else {
        				quickReply("syntax error");
        			}
    			} else {
    				quickReply("Votes cannot be cast from kiwiirc.com");
    			}
    		} else {
    			quickReply("Poll does not exist.");
    		}
        }
        
        public void newPoll()
        {
        		String name = msgArray[2];
        		
        		String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\"+name;
        		File f = new File(path);
        		try {
                    if (!f.exists()) 
                    {
                        f.mkdir();
                       
                        String pathQuestion= "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\"+name+"\\question.txt";
                		File question = new File(pathQuestion);
                		question.createNewFile();
                        
                        FileWriter fw = new FileWriter(question.getAbsoluteFile(), true);
                        
                        String questionString = "";
                        for(int x=3;x<msgArray.length;x++)
                        {
                        	questionString = questionString + msgArray[x]+" ";
                        }
                        
                        fw.write(questionString);
                        fw.close();
                        
                        String pathAuthor= "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\"+name+"\\author.txt";
                        File authorFile = new File(pathAuthor);
                        authorFile.createNewFile();
                        FileWriter authorWriter = new FileWriter(authorFile.getAbsoluteFile(), true);
                        authorWriter.write(sender);
                        authorWriter.close();
                        
                        
                        quickReply("Poll " + name + " created. Add choices by typing: "+Bot.botName+" addOption [poll_name] [option]");
                    } else {
                    	quickReply("Poll already exists");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }         
        }
        
        public void addOption()
        {
        	String poll = msgArray[2];
        	
        	if(checkAuthor(poll))
        	{
        		String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\"+poll+"\\options.txt";
        		File f = new File(path);
        		try {
                    if (!f.exists()) 
                        f.createNewFile();
                    String option = "";
                    for(int x=3;x<msgArray.length;x++)
                    {
                    	option = option + msgArray[x]+" ";
                    }
                    FileWriter fw = new FileWriter(f.getAbsoluteFile(), true);

                    fw.write(option+"\r\n");
                    fw.close();
                        
                    quickReply("Option added.");
                    
                } catch (IOException e) {
                    e.printStackTrace();
                    quickReply("Something didn't work");
                }  
            
        	} else {
        		quickReply("Only the creator of a poll can add options.");
        	}
        	

        }
        
        public void pollResults()
        {
        	String poll = msgArray[2];
        	String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\"+poll+"\\options.txt";
    		
    		ArrayList<String> options = new ArrayList<String>();
    		BufferedReader br;
    		
			try {
				br = new BufferedReader(new FileReader(path));
				String line;
	            while ((line = br.readLine()) != null) 
	            {
	              options.add(line);
	            }
	            br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
            
            int[] results = new int[options.size()];
            int totalVotes=0;
            
            String pollDir = "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\"+poll+"\\";
            File dir = new File(pollDir);
            String[] votes = dir.list();
            
            for(int x=0;x<votes.length;x++)
            {
            	if(!(votes[x].equalsIgnoreCase("options.txt")) && !(votes[x].equalsIgnoreCase("author.txt")) && !(votes[x].equalsIgnoreCase("question.txt")))
    			{
            		try {
						BufferedReader reader = new BufferedReader(new FileReader(pollDir+"\\"+votes[x]));
						
							
							String vote = reader.readLine();
							System.out.println(vote);
							if(validateInt(vote))
							{
								int voteNum = Integer.parseInt(vote);
								results[voteNum-1]++;
								totalVotes++;
							}
						
						reader.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
    			}
            }
            
            
            for(int x=0;x<options.size();x++)
            {
            	double percentDouble = ((results[x] *100)/ totalVotes);
            	String percent = percentDouble + "%";
            	String optionResult = percent + " (" + results[x] + " votes) - " + options.get(x);
            	quickReply(optionResult);
            }
    		
    		
        }
        
        public void endPoll()
        {
        	String poll = msgArray[2];
        	if(checkAuthor(poll))
        	{
        		
        		String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\"+poll;
        		File f = new File(path);
        		f.delete();
        		quickReply(poll + " poll has ended.");
        	} else {
        		quickReply("Only the poll creator can end a poll");
        	}
        }
        
        public boolean checkAuthor(String poll)
        {
        	String pathAuthor = "C:\\Users\\Jared\\Documents\\BreakfastBot\\polls\\"+poll+"\\author.txt";
        	try {
				BufferedReader reader = new BufferedReader(new FileReader(pathAuthor));
				String author = reader.readLine();
				reader.close();
				
				if(author.equalsIgnoreCase(sender))
					return true;
				else
					return false;
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
        	
        }

 //-------------logic stuff----------------
        
            
        public boolean validateInt(String input)
        {
            Pattern p = Pattern.compile("^-?\\d+$");
            Matcher m = p.matcher(input);
            if(m.find())
            {
                return true;
            }
            
            return false;
        }
        
         public boolean allCaps()
             {
               //check to see if message is in all caps
               //as a lazy workaround, I'm just going to check the second and third word
                 if(msgArray.length>2)
                 {
                     char[] secondWord = msgArray[1].toCharArray();
                     char[] thirdWord = msgArray[2].toCharArray();
                    
                     for(int x=0;x<secondWord.length;x++)
                     {
                         if(!Character.isUpperCase(secondWord[x]))
                            {
                                 return false;
                            }
                     }
                     for(int x=0;x<thirdWord.length;x++)
                     {
                         if(!Character.isUpperCase(thirdWord[x]))
                            {
                                 return false;
                            }
                     }
                     return true;
                 }
                 return false;
             }
             
         public String getOps()
         {
             String ops = "";
             User[] users = getUsers(Bot.channel);
             for(int y=0;y<users.length;y++)
             {
                 if(users[y].isOp())
                 {
                     if(!(users[y].equals("ChanServ") || users[y].equals("OperServ")))
                     {
                         String newOp = users[y].getNick();
                         newOp = newOp.replace("@", "");
                         ops = ops + " " + newOp;
                     }
                 }
             }
             return ops;
         }//end get ops
         
         public void formatInGameMsg()
         {
             //if the incoming message came from a server
             //first word is <sender>
             if(msgArray[0].charAt(0) == '<') 
             {
                 //set sender
                 String sender = msgArray[0];
                 sender = sender.replace("<", "");
                 sender = sender.replace(">", "");
                 Bot.sender = sender;
                 
                 
                 //remove name from array
                 String newArray[];
                 newArray = new String[msgArray.length-1];
                 for(int x=0;x<msgArray.length-1;x++)
                 {
                     newArray[x] = msgArray[x+1];
                 }
                 msgArray = newArray;
                 checkForMsg(sender);
             }
         }
         
         //match single words in message from words[] array
         public boolean matchWords(String[] words)
         {
             for(int x=0;x<msgArray.length;x++) {
                 for(int y=0;y<words.length;y++){
                     if(msgArray[x].equalsIgnoreCase(words[y]))
                     {
                         return true;
                     }//end if
                 }//end nested for
             }//end for
             return false;
         }//end matchWords
        
         //match pairs of words in message from pairs[] array
         public boolean matchPairs(String[][] pairs)
         {
             for(int x=0;x<(msgArray.length);x++) {
                 for(int y=0;y<pairs.length;y++){
                     if(msgArray[x].equalsIgnoreCase(pairs[y][0]) && msgArray[x+1].equalsIgnoreCase(pairs[y][1]))
                     {
                         return true;
                     }
                 }//end nested for
             }//end for
             return false;
         }//end match pairs
         
         //match strings of four words in message
         public boolean matchFour(String[][] four)
         {
             for(int x=0;x<(msgArray.length);x++) {
                 for(int y=0;y<four.length;y++){
                     if(msgArray[x].equalsIgnoreCase(four[y][0]) && msgArray[x+1].equalsIgnoreCase(four[y][1]) && msgArray[x+2].equalsIgnoreCase(four[y][2]) && msgArray[x+3].equalsIgnoreCase(four[y][3]))
                     {
                         return true;
                     }
                 }//end nested for
             }//end for
             return false;
         }
         
         public static boolean validate(String s){
             boolean valid = true;
             int i = 0;
             
             //not valid if length = 0
             if (s.length() == 0)  
                 valid = false;    
             
             //not valid if it contains a letter or symbol
             while (i < s.length()) 
             {
                 if (!Character.isDigit(s.charAt(i)))
                 {
                     valid = false;
                     break;
                 }
                 i++;
             }               
             //return result 
             return valid;           
     }//end method validate
         
         public boolean ping(String host)
         {
             Socket socket = null;
            
             try {
                 socket = new Socket(host, 80);
                 return true;
             } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {            
                 if (socket != null) try { socket.close(); } catch(IOException e) {}
             }
             return false;
         }
         
         public void timeStampUser(String user)
         {
             user = cleanName(user);
             String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\lastSeenData\\"+user+".txt";
             
             File f = new File(path);
             
             //set date and time
             DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
             Date date = new Date();
             String timestamp = dateFormat.format(date); 

             try {
                 if (!f.exists()) {
                     f.createNewFile();
                 }
      
                 FileWriter fw = new FileWriter(f.getAbsoluteFile());
                 fw.write(timestamp);
                 fw.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }//end time stamp

         public String getLastSeen(String user)
         {
             String lastSeen = "never";
             String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\lastSeenData\\"+user+".txt";
             
             try {
                // FileReader fr = new FileReader(path);
                 
                BufferedReader br = new BufferedReader(new FileReader(path));
                lastSeen = br.readLine();
                br.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

             return lastSeen;
         }
         
         public void setMessage(String to, String message)
         {
             String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\messages\\"+to+".txt";
             
             File f = new File(path);
             
             try {
                 if (!f.exists()) {
                     f.createNewFile();
                 }
                 FileWriter fw = new FileWriter(f.getAbsoluteFile(), true);
                 
            
                fw.write(message+"\r\n");
                fw.close();

             } catch (IOException e) {
                 e.printStackTrace();
             }
         }//end set message
         
         public boolean getMessage(String to)
         {
             String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\messages\\"+to+".txt";
             
             
                 File f = new File(path);

                 if (f.exists()) {
                     try {
                         
                         BufferedReader br = new BufferedReader(new FileReader(path));
                         
                         String line;
                         while ((line = br.readLine()) != null) {
                           messages.add(line);
                         }
                         br.close();
                         f.delete();
                         return true;
                     } catch (FileNotFoundException e) {
                         e.printStackTrace();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                     
                 }
                     return false;
               
         }
         
         public void addLogEntry(String message)
         {
             String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\log.txt";
             
             File f = new File(path);

             try {
                 if (!f.exists()) {
                     f.createNewFile();
                 }
      
                 FileWriter fw = new FileWriter(f.getAbsoluteFile());
                 fw.append(message);
                 fw.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }//end time stamp
       
         public boolean randBoolean() 
         {
             int randomNum = rand.nextInt(4);
             if(randomNum == 1)
                 return true;
             else
                 return false;
         }
         
         public void setNick(String user, String nick)
         {
             
             ArrayList<String> knownNicks = new ArrayList<String>();
             String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\nicks\\"+user+".txt";
             
             File f = new File(path);
             
             try {
                 if (!f.exists()) 
                 {
                     f.createNewFile();
                     
                 }
                     
                 
                 FileWriter fw = new FileWriter(f.getAbsoluteFile(), true);
                 
               
                fw.write(nick+"\r\n");
                fw.close();
                
                
                BufferedReader br = new BufferedReader(new FileReader(path));

                String line;
                while ((line = br.readLine()) != null) {
                  knownNicks.add(line);
                }
                br.close();

             } catch (IOException e) {
                 e.printStackTrace();
             }

             
             for(int x=0;x<knownNicks.size();x++)
             {
                 String nickPath = "C:\\Users\\Jared\\Documents\\BreakfastBot\\nicks\\"+knownNicks.get(x)+".txt";
                 File file = new File(nickPath);
                 
                
                    try {
                        if (file.exists())
                            file.delete();
                        
                        
                        file.createNewFile();
                        
                        FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
                        
                        fw.write(user+"\r\n");
                        for(int y=0;y<knownNicks.size();y++)
                        {
                            fw.write(knownNicks.get(y)+"\r\n");
                        }

                        
                        fw.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                 
                
             }
         
             
             
             
         }
         
         public ArrayList<String> getNicks(String user)
         {
             ArrayList<String> knownNicks = new ArrayList<String>();
             String path = "C:\\Users\\Jared\\Documents\\BreakfastBot\\nicks\\"+user+".txt";
             knownNicks.add(user);
            try {
                BufferedReader br = new BufferedReader(new FileReader(path));
                
                String line;
                while ((line = br.readLine()) != null) {
                  knownNicks.add(line);
                }
                br.close();
                
                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
             
             return knownNicks;
             
             
             
         }
         
         public String cleanName(String name)
         {
             name.toLowerCase();
             name.replace("|", "");
             name.replace("*", "");
             name.replace("&", "");
             name.replace("~", "");
             name.replace("#", "");
             name.replace("@", "");
             name.replace("^", "");
             
             return name;
         }
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
}//edn class
