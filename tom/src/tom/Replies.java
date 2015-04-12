package tom;


import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import com.mysql.jdbc.Connection;

public class Replies {
  //random number object
    Random rand = new Random();
    
    String[] msgArray;
    String reply;
    
    public String newMessage(String[] msgArray)
    {
       this.reply = " ";
       this.msgArray = msgArray;
       formatText();
       searchDirectReplies();
       return reply;
    }
    
    public void randReply(String[] response)
    {
        int randomNum = rand.nextInt(response.length);
        String reply = response[randomNum];
       this.reply = reply;
    }
    
    public void Reply(String reply) {
        this.reply = reply;
    }
    
    public void searchDirectReplies()
    {
      
            hello();
            whatsUp();
            who();
            changeDelay();
            thanks();
            why();
            shutUp();
            fact();
            tellHelp();
            congrats();
            what();
            sayHi();
            love();
            bot();
    }
    
        public void hello() {
           String words[] = {"hello",
                               "hi",
                               "welcome",
                               "hey",
                               "o/"};
           String response[] = {"hello",
                               "hi",
                               "hey",
                               "hey, whats up",
                               "o/"};
           
           if(matchWords(words))
           {
               randReply(response);
           }
       }//end hello
       
        public void whatsUp() {
           String words[] = {"sup"};
           String pairs[][] = {{"whats", "up"},
                               {"what", "up"},
                               {"what's", "up"},
                               {"how", "are"}};
           String response[] = {"I'm fine",
                               "I wish I had a brain...",
                               "punching trees",
                               "hanging out in Jared's RAM"};
           
           if(matchWords(words) || matchPairs(pairs))
           {
               randReply(response);
           }
       }//end whats up
 
        public void congrats() {
        String words[] = {"congrats"};
        String pairs[][] = {{"thats", "awesome"},
                            {"that's", "awesome"},
                            {"thats", "cool"},
                            {"that's", "cool"}};
        String response[] = {"I know",
                            "Thanks",
                            "you're cool"};
        if(matchWords(words) || matchPairs(pairs))
        {
            randReply(response);
        }
    }//end
    
        public void what() {
        String words[] = {"whats",
                          "what's"};
        String pairs[][] = {{"what", "is"},
                            {"what", "are"}};
        String response[] = {"I dont know",
                            "what?",
                            "who knows",
                            "42"};
        if(matchWords(words) || matchPairs(pairs))
        {
            randReply(response);
        }
    }//end
    
        public void who() {
           String words[] = {"who",
                               "whose",
                               "whos",
                               "who's"};
           String pairs[][] = {{"who", "is"},
                               {"who", "are"}};
           String response[] = {"I'm "+Bot.botName,
                               "I am the brain interactive construct.",
                               "who are you?",
                               "Java Exception Thrown"};
           
           if(matchWords(words) || matchPairs(pairs))
           {
               randReply(response);
           }
       }//end who
    
       public void thanks() {
           String words[] = {"thank",
                               "thanks"};
           String response[] = {"np",
                               "anytime",
                               "you're welcome"};
           if(matchWords(words))
           {
               randReply(response);
           }
       }//end thanks
       
       public void why() {
           String words[] = {"why"};
           String response[] = {"because",
                               "that's how it is"};
           if(matchWords(words))
           {
               randReply(response);
           }
       }//end why
       
       public void changeDelay() {
           String words[] = {"actionDelay"};
           
           if(matchWords(words))
           {
               int newDelay=0;
               for(int x=0;x<msgArray.length;x++)
               {
                   if(validate(msgArray[x]))
                   {
                       newDelay = Integer.parseInt(msgArray[x]);
                   }
               }
               
               if(!(newDelay == 0))
               {
                   Bot.actionDelay = newDelay;
               }
               
               try {
                   Thread.sleep(1000);
               } catch(InterruptedException ex) {
                   Thread.currentThread().interrupt();
               }
               
               Reply("Setting changed to " + Bot.actionDelay + " messages.");
           }
       }//end change delay
       
       public void shutUp() {
           String words[] = {"shutup"};
           String pairs[][] = {{"shut", "up"}};
           String response[] = {"you shut up",
                               "screw you",
                               "you're mean",
                               "Do you talk to other bots like that?"};
           if(matchWords(words) || matchPairs(pairs))
           {
               randReply(response);
           }
       }//end shut up
       
       public void tellHelp() {
           if(Bot.inactivity > 0)
           {
               String words[] = {"tell"};
               String pairs[][] = {{"send", "message"},
                                   {"send", "a"}};
               if(matchWords(words) || matchPairs(pairs))
               {
                   Reply("To leave someone a message type: " + Bot.botName + " tell <user> <message>");
               }
           }
       }
       
       public void bot() {
           if(Bot.inactivity > 0)
           {
               String words[] = {"bot"};
               if(matchWords(words))
               {
                   String[] response = {"Why would you think I was a bot?",
                                       "beep",
                                       "Are you a bot?"};
                   
                   
                   randReply(response);
               }
           }
       }
       
       public void fact() {

           String[] words = {"fact"};

           if(matchWords(words))
           {
               randomFact();
           }
       }//end fact
       
       public void randomFact()
       {    
           try {
            Class.forName("com.mysql.jdbc.Driver");
            
           } catch (ClassNotFoundException e) {
            e.printStackTrace();
           }
           
          Connection con;
          
          try {
            con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/facts","root","lockview");
            PreparedStatement statement = con.prepareStatement("select * from fact");
            ResultSet result = statement.executeQuery();
            
            //this is an awful way to do this
            //need the prepared statement to return one random fact
            int randomNum = rand.nextInt(1450);
            for(int x=0;x<randomNum;x++)
                result.next();
            
            String test = result.getString(1);
            Reply(test);
            
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
           
        
        //----------- Old Facts---------------
        
           
//               String[] quotes = {"Dartboards are made out of horse hairs.",
//                                  "Cats sleep 16 to 18 hours per day.",
//                                  "Odontophobia is the fear of teeth.",
//                                  "Karoke means 'empty orchestra' in Japanese.",
//                                  "The most money ever paid for a cow in an auction was $1.3 million.",
//                                  "1 in 5,000 north Atlantic lobsters are born bright blue.",
//                                  "Elephants are the only mammals that can't jump.",
//                                  "Charlie Brown's father was a barber.",
//                                  "The plastic things on the end of shoelaces are called aglets.",
//                                  "Despite a population of over a billion, China has only about 200 family names.",
//                                  "The human heart creates enough pressure when it pumps out to the body to squirt blood 30 feet.",
//                                  "Beethoven dipped his head in cold water before he composed.",
//                                  "Banging your head against a wall uses 150 calories an hour.",
//                                  "Starfish have no brains.",
//                                  "During it’s lifetime an oyster changes its sex from male to female and back several times.",
//                                  "Venus is the only planet that rotates clockwise.",
//                                  "The first bomb the Allies dropped on Berlin in WWII killed the only elephant in the Berlin Zoo.",
//                                  "The elephant is the only animal with 4 knees.",
//                                  "According to suicide statistics, Monday is the favored day for self-destruction.",
//                                  "If you have 3 quarters, 4 dimes, and 4 pennies, you have $1.19. You also have the largest amount of money in coins without being able to make change for a dollar.",
//                                  "In the average lifetime, a person will walk the equivalent of 5 times around the equator.",
//                                  "Animals, including pigs, horses and even insects, could be tried and convicted of crimes for several centuries across many parts of Europe.",
//                                  "Heroin derives its name from \"heroic\" as it was originally marketed as a non-addictive wonder cure to morphine addiction.",
//                                  "Performing the Nazi salute in Germany is a criminal offence punishable by up to 3 years of prison.",
//                                  "Before publishing \"Don Quixote,\" Cervantes was captured and kept as slave for 5 years in Algiers. A ransom was paid by his parents.",
//                                  "It takes the average American taxpayer 13 hours a year to comply with the tax code by reading the rules, gathering receipts and filling out forms.",
//                                  "Diana Ross, who sang \"Ain't No Mountain High Enough,\" lost her ex-husband to a mountain climbing accident years later.",
//                                  "Before becoming an artist, Van Gogh was a teacher, a bookshop clerk, and a preacher.",
//                                  "New Zealand's Ninety-Mile Beach is only 55 miles long.",
//                                  "Recycling one ton of paper saves 682.5 gallons of oil, 7,000 gallons of water and 3.3 cubic yards of landfill space.",
//                                  "Perfume or cologne appears to confuse ants. It makes it harder for them to tell friend from foe.",
//                                  "Sweden's Volvo made the three-point seatbelt design patent open and available to other car manufacturers for free, in the interest of safety. It saves one life every 6 minutes.",
//                                  "The world's steepest street is Baldwin St., with a 38° gradient, in Dunedin, New Zealand",
//                                  "A man is 35% more likely to be diagnosed with prostate cancer than a woman is to be diagnosed with breast cancer.",
//                                  "In 1999, an ex-prostitute in New Zealand, Georgina Beyer, became the world's first openly transsexual Member of Parliament",
//                                  "De-extinction is scientifically possible. Several viruses have already been brought back, including the 1918 flu pandemic virus.",
//                                  "There is no scientific evidence that shaving or waxing will make your hair come back thicker.",
//                                  "29 days after the Titanic sank, a movie about it was released. It featured an actress who was actually on the Titanic and survived"};
//               
//               boolean[] booleans = new boolean[quotes.length];
//               
//               boolean replied = false;
//               
//               while(!replied)
//               {
//                   int fact = rand.nextInt(quotes.length);
//                   if(!booleans[fact])
//                   {
//                       Reply(quotes[fact]);
//                       booleans[fact] = true;
//                       replied = true;
//                   }
//                   
//                   if(!checkFacts(booleans))
//                   {
//                       Reply("I don't have anymore facts :(");
//                       replied = true;
//                   }
//                   
//               }
//              

       }//end facts
       
       public boolean checkFacts(boolean[] array)
       {
           for(int x=0;x<array.length;x++)
           {
               if(array[x]==false);
                   return true;
           }
           return false;
       }
       
       public void sayHi()
       {
          
           String pairs[][] = {{"say", "hi"},
                               {"say", "hello"}};
           if(matchPairs(pairs))
           {
               String lastWord = msgArray[msgArray.length-1];
               
               String response[] = {"Hello "+lastWord,
                                   "Hey "+lastWord};
               randReply(response);
           }
       }
       
       public void love()
       {
           String[] words = {"love"};

           if(matchWords(words))
           {
               String[] response = {"What is love?",
                                     "**something witty about love**"};
                                     
               randReply(response);                      
               
           }
       }
       
       
       
       //search logic
       
        public void formatText()
       {
           for(int x=0;x<msgArray.length;x++){
               msgArray[x] = msgArray[x].replace("?", "");
               msgArray[x] = msgArray[x].replace("!", "");
               msgArray[x] = msgArray[x].replace(".", "");
               msgArray[x] = msgArray[x].replace(",", "");
               msgArray[x] = msgArray[x].replace(":", "");
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
       
       
       
     
}//end class
