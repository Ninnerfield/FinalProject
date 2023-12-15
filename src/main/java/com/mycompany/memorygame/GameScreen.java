/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/*
    Need to Fix:
    Make sure all Classes Start with Uppercase
    All variables start with lowercase
    Upload to GitHub
    Make Sure all files have my name as an author
    DELETE THIS WHEN COMPLETE
*/
package com.mycompany.memorygame;
import com.google.gson.Gson;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
/**
 * @author Ninnerfield
 */
public class GameScreen extends javax.swing.JFrame implements ActionListener {
    public static int cardClicks = 0;
    private int seconds;
    private int moves = 13;
    private int previousButton;
    private int lastGuess;
    JButton[] buttonList = new JButton[12];
    String[] buttonIconUrl = new String[12];
    private Gson gson;
    private String firstGuess;
    int firstGuessCardNum;
    int secondGuessCardNum;
    int pairs = 0;
    private String secondGuess;
    private int setMade = 0;
    String cardString = "";
    private int setBackImageMade = 0;
    private String imageUrl;
    private boolean checkEquals;
    private int wait;
    private boolean run = true;
    private int score;
    
    public int getcardClicks(){
        return cardClicks;
    }
    
    public void setcardClicks(int i){
        cardClicks = i;
    }
    
    public void checkScore(){
        //Code based on https://www.guru99.com/buffered-reader-in-java.html
        score = pairs * moves * moves * 38 - seconds;
        if (moves == 0 && pairs < 6){
            JOptionPane.showMessageDialog(null, "You Lost", "You Lost", JOptionPane.ERROR_MESSAGE);
        }else{
            if (score > Integer.parseInt(StartScreen.returnHighScore().split(":")[1])){
                String name = JOptionPane.showInputDialog("Your Score Is:" + score + ". Congrats, you broke the high score, enter your username:");
                if (name == ""){
                    JOptionPane.showMessageDialog(null, "That is not a valid username", "Error", JOptionPane.ERROR_MESSAGE);
                    name = JOptionPane.showInputDialog("Your Score Is:" + score + ". Congrats, you broke the high score, enter your username:");
                }
                StartScreen.setHighScore(name + ":" + score);
                File scoreFile = new File("highscore.dat");
                if (!scoreFile.exists()){
                    try {
                        scoreFile.createNewFile();
                    } catch (IOException ex) {
                        Logger.getLogger(GameScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                FileWriter writeFile = null;
                BufferedWriter writer = null;
                try{
                    writeFile = new FileWriter(scoreFile);
                    writer = new BufferedWriter(writeFile);
                    writer.write(StartScreen.getHighScore());
                }catch(Exception e){

                }finally{
                    try{
                        if (writer != null)
                            writer.close();
                    }catch(Exception e){

                    }
                }
            }else{
                JOptionPane.showMessageDialog(null, "You did not beat the high score of " +Integer.parseInt(StartScreen.returnHighScore().split(":")[1]) + " with a score of: " + score + ". Better luck next time!", "ScoreScreen", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
        

    public void computeMoves(int cardNum){
        setcardClicks(cardClicks + 1);
        if (getcardClicks() == 2){ //These next lines of code will subtract 1 from the remaining moves and update it on the gui
            moves = moves - 1;
            cardClicks = 0;
            setMade = 0;
            movesLeft.setText(Integer.toString(moves));  
        }
        previousButton = cardNum; //Sets this button to the previous button pressed
    } 
    
    public int lastGuess(int cardValue){
        lastGuess = cardValue;
        return lastGuess;
    }

    public final void scaleImage(String url, int cardnum){ //Iterates though the buttons and sets all the images to the card as well as scales them.
       // code based on https://stackoverflow.com/questions/2856480/resizing-a-imageicon-in-a-jbutton
        if (setBackImageMade == 0){ 
            try{
                for (int i=0; i<12; i++){
                    ImageIcon icon = new ImageIcon(new URL("https://deckofcardsapi.com/static/img/back.png"));
                    Image img = icon.getImage();
                    Image imgScale = img.getScaledInstance(79, 100,Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(imgScale);        
                    buttonList[i].setIcon(scaledIcon);
                    setBackImageMade = 1;
                }
            }catch (MalformedURLException ex) {
                Logger.getLogger(GameScreen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else //scales the icon, and sets the button icon to a random button when clicked
            try {
                ImageIcon icon = new ImageIcon(new URL(url));
                Image img = icon.getImage();
                Image imgScale = img.getScaledInstance(79, 100,Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(imgScale);        
                buttonList[cardnum].setIcon(scaledIcon);
            }catch (MalformedURLException ex) {
                Logger.getLogger(GameScreen.class.getName()).log(Level.SEVERE, null, ex);
            }    
}
    public ImageIcon disabledImage(String url, int cardnum){ //Scales the image after the button has been disabled, 
                                                            //without this function the disabled button turns gray and I wasn't pleased with how it looked
        try {
         ImageIcon icon = new ImageIcon(new URL(url));
         Image img = icon.getImage();
         Image imgScale = img.getScaledInstance(79, 100,Image.SCALE_SMOOTH);
         ImageIcon scaledIcon = new ImageIcon(imgScale);        
         buttonList[cardnum].setIcon(scaledIcon);
         return scaledIcon;
        }catch (MalformedURLException ex) {
            Logger.getLogger(GameScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    //This function gets all the information from the api including the 
    //image link and cuts the string down to only the image link which then 
    // returns it for another function to use to call the png   
    public String setCards(){ 
        if (setMade == 1){
            CardRetrieve cr = gson.fromJson(cardString, CardRetrieve.class); 
            String temp = CardGetter.cardGetter("https://deckofcardsapi.com/api/deck/"+cr.deck_id+"/draw/?count=1");
            CardObjectGetter COG = gson.fromJson(temp, CardObjectGetter.class);
            String cardVals = new Gson().toJson(COG.cards);
            String[] arrOfStr = cardVals.split(",", 0);
            String imageVal = arrOfStr[1];
            String[] getImageLink = imageVal.split("h", 0);
            String finalImageLink = getImageLink[1];
            String[] getFinalImageLink = finalImageLink.split("g", 0);
            imageUrl = ("h"+getFinalImageLink[0]+"g"+getFinalImageLink[1]+"g"); 
        }
        if (setMade == 0){
            cardString = CardGetter.cardGetter("https://deckofcardsapi.com/api/deck/new/shuffle/?cards=AS,AS,KS,KS,2D,2D,AC,AC,KC,KC,2H,2H");
            setMade = 1;
        }
        return imageUrl;
    }  
    public void waitToFlipCard(){
        run = false;
        java.util.Timer time1 = new java.util.Timer();
        wait = 0;
        TimerTask t1 = new TimerTask(){
            public void run() {
                if (moves > 0){
                    wait++;
                    if (wait == 4){
                        scaleImage("https://deckofcardsapi.com/static/img/back.png", firstGuessCardNum);
                        scaleImage("https://deckofcardsapi.com/static/img/back.png", secondGuessCardNum);
                        run = true;
                    }       
                }
            }
        };
        time1.scheduleAtFixedRate(t1,1000, 1000);
    }
    public GameScreen() {
        initComponents(); 
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        gson = new Gson();
        card0.addActionListener(this);
        card1.addActionListener(this);
        card2.addActionListener(this);
        card3.addActionListener(this);
        card4.addActionListener(this);
        card5.addActionListener(this);
        card6.addActionListener(this);
        card7.addActionListener(this);
        card8.addActionListener(this);
        card9.addActionListener(this);
        card10.addActionListener(this);
        card11.addActionListener(this);
        buttonList[0] = card0; 
        buttonList[1] = card1;
        buttonList[2] = card2;
        buttonList[3] = card3;
        buttonList[4] = card4;
        buttonList[5] = card5;
        buttonList[6] = card6;
        buttonList[7] = card7;
        buttonList[8] = card8;
        buttonList[9] = card9;
        buttonList[10] = card10;
        buttonList[11] = card11;
          cardClicks = 0;
        scaleImage("https://deckofcardsapi.com/static/img/back.png", 1);
        setCards();
        for (int i = 0; i<12;i++){
            buttonList[i].setEnabled(true);
            String Url = setCards();
            buttonIconUrl[i] = (Url);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        card0 = new javax.swing.JButton();
        card2 = new javax.swing.JButton();
        card3 = new javax.swing.JButton();
        card6 = new javax.swing.JButton();
        card7 = new javax.swing.JButton();
        card5 = new javax.swing.JButton();
        card1 = new javax.swing.JButton();
        card4 = new javax.swing.JButton();
        card8 = new javax.swing.JButton();
        card9 = new javax.swing.JButton();
        card10 = new javax.swing.JButton();
        card11 = new javax.swing.JButton();
        CLB = new javax.swing.JLabel();
        CLB1 = new javax.swing.JLabel();
        timer = new javax.swing.JLabel();
        Leavebtn = new javax.swing.JButton();
        movesLeft = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        jLabel3.setText("jLabel3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 51, 51));

        jLabel1.setFont(new java.awt.Font("Ravie", 1, 36)); // NOI18N
        jLabel1.setText("Time:");

        jLabel2.setFont(new java.awt.Font("Ravie", 1, 36)); // NOI18N
        jLabel2.setText("Moves Left:");
        jLabel2.setToolTipText("");

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        CLB.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                CLBPropertyChange(evt);
            }
        });

        CLB1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                CLB1PropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(CLB, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(card0, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card8, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card4, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(card5, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card9, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(58, 58, 58)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(card6, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card10, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card2, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(card11, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card7, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(75, 75, 75))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(card3, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CLB1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(103, 103, 103)
                .addComponent(CLB1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 210, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(card3, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CLB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card0, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(card5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card7, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(card4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(card8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
        );

        timer.setFont(new java.awt.Font("Ravie", 1, 36)); // NOI18N
        timer.setForeground(new java.awt.Color(255, 255, 255));
        timer.setText("0");

        Leavebtn.setText("Main Menu");
        Leavebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LeavebtnActionPerformed(evt);
            }
        });

        movesLeft.setFont(new java.awt.Font("Ravie", 1, 36)); // NOI18N
        movesLeft.setForeground(new java.awt.Color(255, 255, 255));
        movesLeft.setText("10");

        jLabel4.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Match The Cards!");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(timer, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                                .addGap(36, 36, 36)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(movesLeft)
                                .addGap(22, 22, 22))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(109, 109, 109)
                                .addComponent(Leavebtn)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(movesLeft)
                    .addComponent(jLabel2)
                    .addComponent(timer, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Leavebtn)
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CLBPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_CLBPropertyChange
        // TODO add your handling code here:
        //Code based on https://youtu.be/spYqsH8cSWw?feature=shared
        java.util.Timer time = new java.util.Timer();
        seconds = -1;
        TimerTask t = new TimerTask(){
            public void run() {
               if (moves > 0 && pairs != 6){
                    seconds++;
                    int stopWatch = seconds / 2; //In testing the stopwatch was counting up by 2 
                    timer.setText(Integer.toString(stopWatch));
               }
            }
        };
        time.scheduleAtFixedRate(t, 1000, 1000);
        //The code above is the stopwatch that is counting up  
    }//GEN-LAST:event_CLBPropertyChange

    private void LeavebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeavebtnActionPerformed
        // TODO add your handling code here:
        StartScreen SS = new StartScreen();
        SS.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_LeavebtnActionPerformed

    private void CLB1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_CLB1PropertyChange
        // TODO add your handling code here:
       movesLeft.setText(Integer.toString(moves));
    }//GEN-LAST:event_CLB1PropertyChange

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameScreen().setVisible(true);
            }
        });     
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CLB;
    private javax.swing.JLabel CLB1;
    private javax.swing.JButton Leavebtn;
    private javax.swing.JButton card0;
    private javax.swing.JButton card1;
    private javax.swing.JButton card10;
    private javax.swing.JButton card11;
    private javax.swing.JButton card2;
    private javax.swing.JButton card3;
    private javax.swing.JButton card4;
    private javax.swing.JButton card5;
    private javax.swing.JButton card6;
    private javax.swing.JButton card7;
    private javax.swing.JButton card8;
    private javax.swing.JButton card9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel movesLeft;
    private javax.swing.JLabel timer;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if (run == true){
            for (int i=0; i<12; i++){
                if (e.getSource() == buttonList[i]){
                    if (cardClicks == 0){
                        firstGuess = buttonIconUrl[i];//Sets fist guess to the Url used to check if first and second guess are equal
                        firstGuessCardNum = i;
                    }
                    scaleImage(buttonIconUrl[i], i);
                    if (cardClicks == 1){ //If this is the second card to be flipped in the move it will check to make sure the user didn't click this card twice.
                        if (previousButton !=i){ //Checks if the previous button pressed was this button so it won't count clicking this button twice as a move
                            computeMoves(i);
                            secondGuess = buttonIconUrl[i]; //Sets fist guess to the Url used to check if first and second guess are equal
                            checkEquals = firstGuess.equals(secondGuess);
                            scaleImage(buttonIconUrl[i], i);
                            secondGuessCardNum = i;
                            if (checkEquals == false){
                                waitToFlipCard();
                            }else{
                                buttonList[firstGuessCardNum].setEnabled(false);
                                buttonList[secondGuessCardNum].setEnabled(false);

                                ImageIcon button1 = disabledImage(buttonIconUrl[firstGuessCardNum], firstGuessCardNum);
                                buttonList[firstGuessCardNum].setDisabledIcon(button1);
                                ImageIcon button2 = disabledImage(buttonIconUrl[secondGuessCardNum], secondGuessCardNum);
                                buttonList[secondGuessCardNum].setDisabledIcon(button2);
                                pairs++;
                                if (pairs == 6){
                                    computeMoves(i);
                                    checkScore();
                                }
                            }
                            if (moves == 0 && pairs < 6){
                                checkScore();
                            }
                        }
                    }else{
                        computeMoves(i);
                        }
                }
            }
        } 
   }
}

