import java.awt.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.event.*;
import java.util.*;
import java.lang.*;

public class BoardLayersListener extends JFrame {

    // static Board<Room> board;
    static final long serialVersionUID = 0;
    static boolean roomsVisible = false;
    static boolean rolesVisible = false;
    static boolean moveSelections = false;
    static boolean upgradesVisible = false;
    static JButton[] roomButtonArr;
    static JButton[] roleButtonArr;
    static JButton[] upgradeButtonArrDollar = new JButton[5];
    static JButton[] upgradeButtonArrCredit = new JButton[5];
    static Role[] roleArr;
    @SuppressWarnings("unchecked")
    Board<Room> board = Board.getInstance();
    
    // JLabels
    static JLabel[] upgradeLabels = new JLabel[8];
    static ArrayList<JLabel> scoreLabels;
    JLabel boardlabel;
    JLabel cardlabel;
    ArrayList<JLabel> playerlabels;
    static JLabel[] blankCards = new JLabel[12];

    // JButtons
    JButton bAct;
    JButton bRehearse;
    JButton bMove;
    JButton bUpgrade;
    JButton bTakeRole;
    JButton bEnd;

    // JLayered Pane
    JLayeredPane bPane;

    ImageIcon icon;

    Deadwood controller = Deadwood.getInstance();

    // Singleton
    private static BoardLayersListener instance = new BoardLayersListener();

    public static BoardLayersListener getInstance(){
        return instance;
    }

    private BoardLayersListener() {
        // Create window and board
        super("Deadwood");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        bPane = getLayeredPane();
        boardlabel = new JLabel();
        icon = new ImageIcon("images/board.jpg");
        boardlabel.setIcon(icon);
        boardlabel.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
        bPane.add(boardlabel, Integer.valueOf(1));
        setSize(icon.getIconWidth() + 200, icon.getIconHeight());

        // Create action buttons
        bAct = new JButton("ACT");
        bAct.setName("act");
        bAct.setBackground(Color.white);
        bAct.setBounds(icon.getIconWidth() + 10, 60, 150, 100);
        bAct.addMouseListener(new boardMouseListener());

        bRehearse = new JButton("REHEARSE");
        bRehearse.setName("rehearse");
        bRehearse.setBackground(Color.white);
        bRehearse.setBounds(icon.getIconWidth() + 170, 60, 150, 100);
        bRehearse.addMouseListener(new boardMouseListener());

        bMove = new JButton("MOVE");
        bMove.setName("move");
        bMove.setBackground(Color.white);
        bMove.setBounds(icon.getIconWidth() + 330, 60, 150, 100);
        bMove.addMouseListener(new boardMouseListener());

        bUpgrade = new JButton("UPGRADE");
        bUpgrade.setName("upgrade");
        bUpgrade.setBackground(Color.white);
        bUpgrade.setBounds(icon.getIconWidth() + 10, 170, 150, 100);
        bUpgrade.addMouseListener(new boardMouseListener());

        bTakeRole = new JButton("TAKE ROLE");
        bTakeRole.setName("work");
        bTakeRole.setBackground(Color.white);
        bTakeRole.setBounds(icon.getIconWidth() + 170, 170, 150, 100);
        bTakeRole.addMouseListener(new boardMouseListener());

        bEnd = new JButton("END");
        bEnd.setName("end");
        bEnd.setBackground(Color.white);
        bEnd.setBounds(icon.getIconWidth() + 330, 170, 150, 100);
        bEnd.addMouseListener(new boardMouseListener());

        // Place the action buttons in the top layer
        bPane.add(bAct, Integer.valueOf(2));
        bPane.add(bRehearse, Integer.valueOf(2));
        bPane.add(bMove, Integer.valueOf(2));
        bPane.add(bUpgrade, Integer.valueOf(2));
        bPane.add(bTakeRole, Integer.valueOf(2));
        bPane.add(bEnd, Integer.valueOf(2));

        // Current Player Label
        JLabel currPlayerLabel = new JLabel("Current Player: ");
        currPlayerLabel.setBounds(icon.getIconWidth() + 10, 800, 200, 200);
        bPane.add(currPlayerLabel, Integer.valueOf(2));
    }

    /* Method displays a popup window to the user
     */
    public void displayMessage(String message){
        JFrame frame = new JFrame("message");
        JOptionPane.showMessageDialog(frame, message);
    }

    /* Method resets cards, players and shot counters
     * Called on a new day
     */
    public void resetGUI(){
        initBlankCards(controller.getRooms());
        initShotCounters(controller.getRooms());
        int offsetX = 0;
        int offsetY = 0;
        Player[] players = controller.getPlayerOrder();
        JLabel pLabel;
        ImageIcon pIcon;
        for(int i = 0; i < players.length; i++){
            movePlayer(players[i], 995 + offsetX, 275 + offsetY);
            offsetX += 50;
            if(i == 3){
                offsetX = 0;
                offsetY += 50;
            }
        }
    }

    /* Prompts the user for the amount of players in the game
     * Called at the start of the game
     */
    public int getPlayerAmount(){
        String count = "";
        JFrame playerPrompt = new JFrame();
        String[] choices = {"2", "3", "4", "5", "6", "7", "8"};
        count = (String) JOptionPane.showInputDialog(null, "How Many Players?", "Player Selection", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        int result = Integer.parseInt(count);
        return result;
    }

    /* Method displays the current player's dice in the bottom right corner of the screen
     */
    public void displayCurrentPlayer(Player currentPlayer){
        JLabel pLabel = new JLabel();
        pLabel.setIcon(currentPlayer.getIcon());
        pLabel.setBounds(icon.getIconWidth() + 130, 877, currentPlayer.getIcon().getIconHeight(), currentPlayer.getIcon().getIconHeight());
        bPane.add(pLabel, Integer.valueOf(2));
        bPane.moveToFront(pLabel);
    }

    /* Method moves the players dice to a specifed x, y coordinate
     * Has to find correct JLabel to move based on player's name
     */
    public void movePlayer(Player player, int xCord, int yCord){
        for(int i = 0; i < playerlabels.size(); i++){
            if(player.getName() == playerlabels.get(i).getName()){
                playerlabels.get(i).setBounds(xCord, yCord, player.getIcon().getIconWidth(), player.getIcon().getIconHeight());
            }
        }
        bPane.repaint();
    }

    /* Method initalizes all players dice icons in the trailers at the start of every day
     */
    public void initPlayerPosition(Player[] players){
        JLabel pLabel;
        ImageIcon pIcon;
        int offsetX = 0;
        int offsetY = 0;
        playerlabels = new ArrayList<JLabel>();
        scoreLabels = new ArrayList<JLabel>();
        for(int i = 0; i < players.length; i++){
            pLabel = new JLabel();
            pIcon = players[i].getIcon();
            pLabel.setIcon(pIcon);
            pLabel.setBounds(995 + offsetX, 275 + offsetY, 46, 46);
            pLabel.setName(players[i].getName());
            playerlabels.add(pLabel);
            bPane.add(playerlabels.get(i), Integer.valueOf(10));
            pLabel.setVisible(true);

            offsetX += 50;

            if(i == 3){
                offsetX = 0;
                offsetY += 50;
            }
            scoreLabels.add(i, players[i].getPLabel());
            scoreLabels.get(i).setVisible(false);
            bPane.add(scoreLabels.get(i), Integer.valueOf(2));
        }
    }

    /* Method called when a room wraps
     * Players are put back in the blank areas of the rooms
     */
    public void resetPositions(Room room){
        ArrayList<Player> players = room.getPlayers();
        ArrayList<Player> p = room.getCard().getPlayers();
        players.addAll(p);

        int[] playerHolders = room.getPlayerHolderCoord();
        int x = 0;
        for(int i = 0; i < players.size() * 2; i+=2){
            movePlayer(players.get(x), playerHolders[i], playerHolders[i + 1]);
            x++;
        }
        
    }

    /* Method initalizes all the blank cards in each room at the start of every day
     */
    public void initBlankCards(Room[] rooms){
        ImageIcon cardImg;
        for(int i = 0; i < rooms.length; i++){
            if(!(rooms[i].getName().equals("Trailers")) && !(rooms[i].getName().equals("Casting Office"))){
                if(blankCards[i] == null){
                    blankCards[i] = new JLabel();
                    cardImg = new ImageIcon("images/cardback.jpg");
                    Image scaledImg = cardImg.getImage();
                    scaledImg = scaledImg.getScaledInstance(205, 115, java.awt.Image.SCALE_SMOOTH);
                    cardImg = new ImageIcon(scaledImg);
                    blankCards[i].setIcon(cardImg);
                    blankCards[i].setBounds(rooms[i].getCardX(), rooms[i].getCardY(), 205, 115);
                    bPane.add(blankCards[i], Integer.valueOf(2));
                }
                blankCards[i].setVisible(true);
            }
        }
    }

    /* Method initalizes all the shot counters in each room at the start of every day
     */
    public void initShotCounters(Room[] rooms){
        ImageIcon shot = new ImageIcon("images/shot.png");
        for(int i = 0; i < rooms.length; i++){
            if(!(rooms[i].getName().equals("Trailers")) && !(rooms[i].getName().equals("Casting Office"))){
                JLabel[] shotLabels = rooms[i].getShotLabels();
                ArrayList<Integer> shotCounterCoords = rooms[i].getShotCounterCoords();
                int x = 0;
                int y = 1;
                for(int j = 0; j < shotCounterCoords.size()/2; j++){
                    if(shotLabels[j] == null){
                        shotLabels[j] = new JLabel();
                        shotLabels[j].setIcon(shot);
                        shotLabels[j].setBounds(shotCounterCoords.get(x), shotCounterCoords.get(y), shot.getIconWidth(), shot.getIconHeight());
                        bPane.add(shotLabels[j], Integer.valueOf(2));
                        x+=2;
                        y+=2;
                    }
                    shotLabels[j].setVisible(true);
                }
            }
        }
    }

    /* Method inializes the upgrade buttons when a player clicks the upgrade menu button
     */
    public void initUpgradeButtons(){
        upgradeButtonArrDollar[0] = new JButton("4");
        upgradeButtonArrDollar[0].setName("$-2");
        upgradeButtonArrDollar[0].addMouseListener(new boardMouseListener());
        upgradeButtonArrDollar[0].setBackground(Color.white);
        upgradeButtonArrDollar[1] = new JButton("10");
        upgradeButtonArrDollar[1].setName("$-3");
        upgradeButtonArrDollar[1].addMouseListener(new boardMouseListener());
        upgradeButtonArrDollar[1].setBackground(Color.white);
        upgradeButtonArrDollar[2] = new JButton("18");
        upgradeButtonArrDollar[2].setName("$-4");
        upgradeButtonArrDollar[2].addMouseListener(new boardMouseListener());
        upgradeButtonArrDollar[2].setBackground(Color.white);
        upgradeButtonArrDollar[3] = new JButton("28");
        upgradeButtonArrDollar[3].setName("$-5");
        upgradeButtonArrDollar[3].addMouseListener(new boardMouseListener());
        upgradeButtonArrDollar[3].setBackground(Color.white);
        upgradeButtonArrDollar[4] = new JButton("40");
        upgradeButtonArrDollar[4].setName("$-6");
        upgradeButtonArrDollar[4].addMouseListener(new boardMouseListener());
        upgradeButtonArrDollar[4].setBackground(Color.white);

        upgradeButtonArrCredit[0] = new JButton("5");
        upgradeButtonArrCredit[0].setName("c-2");
        upgradeButtonArrCredit[0].addMouseListener(new boardMouseListener());
        upgradeButtonArrCredit[0].setBackground(Color.white);
        upgradeButtonArrCredit[1] = new JButton("10");
        upgradeButtonArrCredit[1].setName("c-3");
        upgradeButtonArrCredit[1].addMouseListener(new boardMouseListener());
        upgradeButtonArrCredit[1].setBackground(Color.white);
        upgradeButtonArrCredit[2] = new JButton("15");
        upgradeButtonArrCredit[2].setName("c-4");
        upgradeButtonArrCredit[2].addMouseListener(new boardMouseListener());
        upgradeButtonArrCredit[2].setBackground(Color.white);
        upgradeButtonArrCredit[3] = new JButton("20");
        upgradeButtonArrCredit[3].setName("c-5");
        upgradeButtonArrCredit[3].addMouseListener(new boardMouseListener());
        upgradeButtonArrCredit[3].setBackground(Color.white);
        upgradeButtonArrCredit[4] = new JButton("25");
        upgradeButtonArrCredit[4].setName("c-6");
        upgradeButtonArrCredit[4].addMouseListener(new boardMouseListener());
        upgradeButtonArrCredit[4].setBackground(Color.white);

        ImageIcon board = new ImageIcon("images/board.jpg");
        int Yoffset = 0;
        for(int i = 0; i < 5; i++){
            upgradeLabels[i] = new JLabel("rank " + (i+2));
            upgradeLabels[i].setName("" + (i+2));
            ImageIcon icon = new ImageIcon("images/dice/w" + (i+2) + ".png");
            upgradeLabels[i].setIcon(icon);
            upgradeLabels[i].setBounds(board.getIconWidth() + 10, (board.getIconHeight()/2) + Yoffset, icon.getIconWidth(), icon.getIconHeight());
            upgradeButtonArrDollar[i].setBounds(board.getIconWidth() + 70, (board.getIconHeight()/2) + Yoffset, 50, 30);
            upgradeButtonArrCredit[i].setBounds(board.getIconWidth() + 150, (board.getIconHeight()/2) + Yoffset, 50, 30);

            bPane.add(upgradeLabels[i]);
            bPane.add(upgradeButtonArrCredit[i]);
            bPane.add(upgradeButtonArrDollar[i]);
            Yoffset+=60;
        }

        upgradeLabels[5] = new JLabel("RANK");
        upgradeLabels[5].setBounds(board.getIconWidth() + 10, (board.getIconHeight()/2) - 30, 100, 20);

        upgradeLabels[6] = new JLabel("DOLLARS");
        upgradeLabels[6].setBounds(board.getIconWidth() + 70, (board.getIconHeight()/2) - 30, 100, 20);

        upgradeLabels[7] = new JLabel("CREDITS");
        upgradeLabels[7].setBounds(board.getIconWidth() + 150, (board.getIconHeight()/2) - 30, 100, 20);

        bPane.add(upgradeLabels[5]);
        bPane.add(upgradeLabels[6]);
        bPane.add(upgradeLabels[7]);
        disableUpgrades();
    }

    /* Method hides all upgrade buttons from screen
     */
    public void disableUpgrades(){
        for(int i = 0; i < 5; i++){
            upgradeLabels[i].setVisible(false);
            upgradeButtonArrCredit[i].setVisible(false);
            upgradeButtonArrDollar[i].setVisible(false);
        }
        upgradeLabels[5].setVisible(false);
        upgradeLabels[6].setVisible(false);
        upgradeLabels[7].setVisible(false);
    }

    /* Method reveals all upgrade buttons on screen
     */
    public void enableUpgrades(){
        for(int i = 0; i < 5; i++){
            upgradeLabels[i].setVisible(true);
            upgradeButtonArrCredit[i].setVisible(true);
            upgradeButtonArrDollar[i].setVisible(true);
        }
        upgradeLabels[5].setVisible(true);
        upgradeLabels[6].setVisible(true);
        upgradeLabels[7].setVisible(true);
    }

    /* Method places card on board
     * Called when player enters room for the first time
     */
    public void revealCard(Room room, Card card){
        JLabel cardLabel = card.getJLabel();
        ImageIcon cardImg = card.getImage();
        cardLabel.setIcon(cardImg);
        cardLabel.setBounds(room.getCardX(), room.getCardY(), cardImg.getIconWidth(), cardImg.getIconHeight());
        cardLabel.setVisible(true);
        bPane.add(cardLabel, Integer.valueOf(4));
        blankCards[room.getID()].setVisible(false);
    }

    /* Method clears the card from a room after the room wraps
     * Called from bank
     */
    public void clearCard(Card card){
        JLabel cardLabel = card.getJLabel();
        cardLabel.setVisible(false);
    }

    /* Method clears 1 shot counter from room when player successfully acts
     */
    public void removeShotCounter(Room room){
        int shotNum = room.getShots();
        JLabel[] shotLabels = room.getShotLabels();
        shotLabels[shotNum].setVisible(false);
    }

    /* Method updates the players dice icon to display the correct rank
     */
    public void setNewRank(Player player, int rank){
        ImageIcon icon = new ImageIcon("images/dice/" + (""+player.getName().charAt(0)).toLowerCase() + rank + ".png");
        player.setIcon(icon);
        for(int i = 0; i < playerlabels.size(); i++){
            if(player.getName() == playerlabels.get(i).getName()){
                playerlabels.get(i).setIcon(icon);
            }
        }
        displayScores(controller.getPlayerOrder());
        displayCurrentPlayer(player);
        bPane.repaint();
    }

    /* Method displays the score after every turn
     */
    public void displayScores(Player[] players){
        int offSet = 0;
        for(int i = 0; i < players.length; i++){
            scoreLabels.get(i).setText("<html> Dollars: " + players[i].getDollars() + 
                            "<br> Credits: " + players[i].getCredits() + 
                            "<br> Rank: " + players[i].getRank() + 
                            "<br> Score: " + players[i].getScore() + "</html>");
            ImageIcon pIcon = players[i].getIcon();
            scoreLabels.get(i).setIcon(pIcon);
            scoreLabels.get(i).setBounds(25 + offSet, 900, 190, 100);
            scoreLabels.get(i).setVisible(true);
            bPane.repaint();
            offSet += 125;
        }
    }

    /* Method displays only the buttons that are legal moves for the player
     * NOTE: Occasionly, bTakeRole is displayed when it shouldn't, if clicked, you will be stuck and will have to end the game :(
     */
    public void displayVisibleButtons(Player player){
        disableMenu();
        if(player.getCurrentRole() != null){
            bAct.setVisible(true);
            bRehearse.setVisible(true);
        }
        if(player.getCurrentRole() == null && !player.getMoveFlag()){
            bMove.setVisible(true);
        }
        if(player.getCurrentRole() == null && player.getCurrentRoom().hasWrapped() == "unwrapped" && controller.getAvailableRolesCount() != 0){
            bTakeRole.setVisible(true);
        }
        if(player.getCurrentRoom().getName() == "Casting Office"){
            bUpgrade.setVisible(true);
        }
        bEnd.setVisible(true);
    }

    /* Method hides all menu buttons
     */
    public void disableMenu(){
        bAct.setVisible(false);
        bEnd.setVisible(false);
        bMove.setVisible(false);
        bRehearse.setVisible(false);
        bTakeRole.setVisible(false);
        bUpgrade.setVisible(false);
    }

    /* Method shows all menu buttons
     */
    public void enableMenu(){
        bAct.setVisible(true);
        bEnd.setVisible(true);
        bMove.setVisible(true);
        bRehearse.setVisible(true);
        bTakeRole.setVisible(true);
        bUpgrade.setVisible(true);
    }

    /* boardMouseListener notifies Deadwood.java of any mouse clicks
     * Depending on the mouse click, Deadwood.java updates the models and GUI
     */
    class boardMouseListener implements MouseListener {
        String actionMode = "";
        public void mouseClicked(MouseEvent e){
            //Act
            if(e.getSource() == bAct && !moveSelections){
                controller.actionMode = "Act";
            }
            //Rehearse
            else if(e.getSource() == bRehearse && !moveSelections){
                controller.actionMode = "Rehearse";
            }
            //Move
            else if(e.getSource() == bMove || moveSelections){
                moveSelections = true;
                actionMode = "Move";
                Player player = controller.getCurrentPlayer();
                Room currentRoom = player.getCurrentRoom();
                int offset = 0;
                if(!roomsVisible){
                    ArrayList<Room> neighbors = board.getNeighbors(currentRoom);
                    roomButtonArr = new JButton[neighbors.size()];
                    for(int i = 0; i < neighbors.size(); i++){
                        roomButtonArr[i] = new JButton(neighbors.get(i).getName());
                        roomButtonArr[i].setName(neighbors.get(i).getName());
                        roomButtonArr[i].setBackground(Color.white);
                        roomButtonArr[i].setBounds(icon.getIconWidth() + 170, 300 + offset, 150, 100);
                        roomButtonArr[i].addMouseListener(new boardMouseListener());
                        bPane.add(roomButtonArr[i], Integer.valueOf(2));
                        offset += 150;
                        roomsVisible = true;
                    }
                }
                disableMenu();
                for(int j = 0; j < roomButtonArr.length; j++){
                    if(((JButton)e.getSource()).getName() == roomButtonArr[j].getName()){
                        moveSelections = false;
                        roomsVisible = false;
                        controller.actionMode = ("move-" + ((JButton)e.getSource()).getName());
                        for(int x = 0; x < roomButtonArr.length; x++){
                            bPane.remove(roomButtonArr[x]);
                        }
                        enableMenu();
                        bAct.setVisible(false);
                        bRehearse.setVisible(false);
                        bUpgrade.setVisible(false);
                        bMove.setVisible(false);
                        if(((JButton)e.getSource()).getName().equals("Trailers") || ((JButton)e.getSource()).getName().equals("Casting Office")){
                            bTakeRole.setVisible(false);
                        }
                        if(((JButton)e.getSource()).getName().equals("Casting Office")){
                            bUpgrade.setVisible(true);
                        }
                        break;
                    }
                }
            }
            //Upgrade
            else if(e.getSource() == bUpgrade && !moveSelections){
                actionMode = "Upgrade";
                enableUpgrades();
                disableMenu();
                bEnd.setVisible(true);
            }
            //Take a role
            else if(e.getSource() == bTakeRole || rolesVisible){
                actionMode = "Role";
                Player player = controller.getCurrentPlayer();
                Room currentRoom = player.getCurrentRoom();
                int invisibleCount = 0;
                int offset = 0;
                if(!rolesVisible){
                    bEnd.setVisible(true);
                    Role[] roomRoles = currentRoom.getRoles();
                    Role[] cardRoles = currentRoom.getCard().getRoles();
                    roleArr = new Role[roomRoles.length + cardRoles.length];
                    System.arraycopy(roomRoles, 0, roleArr, 0, roomRoles.length);
                    System.arraycopy(cardRoles, 0, roleArr, roomRoles.length, cardRoles.length);
                    roleButtonArr = new JButton[roleArr.length];                    
                    for(int i = 0; i < roleButtonArr.length; i++){
                        roleButtonArr[i] = new JButton(roleArr[i].getName());
                        roleButtonArr[i].setName(roleArr[i].getName());
                        roleButtonArr[i].setBackground(Color.white);
                        roleButtonArr[i].setBounds(icon.getIconWidth() + 170, 300 + offset, 200, 30);
                        roleButtonArr[i].addMouseListener(new boardMouseListener());
                        bPane.add(roleButtonArr[i], Integer.valueOf(2));
                        offset += 50;
                        rolesVisible = true;
                        if(roleArr[i].getRank() > player.getRank()|| roleArr[i].getCurrentPlayer() != null){
                            roleButtonArr[i].setVisible(false);
                            invisibleCount++;
                        }
                    } 
                }
                disableMenu();
                for(int j = 0; j < roleButtonArr.length; j++){
                    if(invisibleCount == roleButtonArr.length){
                        bEnd.setVisible(true);
                    }
                    if(((JButton)e.getSource()).getName() == roleButtonArr[j].getName()){
                        rolesVisible = false;
                        controller.actionMode = ("work-" + ((JButton)e.getSource()).getName());
                        for(int x = 0; x < roleButtonArr.length; x++){
                            bPane.remove(roleButtonArr[x]);
                        }
                        enableMenu();
                        bAct.setVisible(false);
                        bRehearse.setVisible(false);
                        bUpgrade.setVisible(false);
                        bMove.setVisible(false);
                        bTakeRole.setVisible(false);
                        break;
                    }
                }
            }
            //End turn
            else if(e.getSource() == bEnd){
                actionMode = "End";
                controller.endTurn();
                disableUpgrades();
                if(controller.isGameOver()){
                    controller.endGame();
                }
            }
            else{
                //upgrade button's listen here
                for(int x = 0; x < 5; x++){
                    if(e.getSource() == upgradeButtonArrDollar[x]){
                        actionMode = "upgrade-" + upgradeButtonArrDollar[x].getName();
                        Deadwood.actionMode = actionMode;
                        disableUpgrades();
                        bUpgrade.setVisible(true);
                        break;
                    }else if(e.getSource() == upgradeButtonArrCredit[x]){
                        actionMode = "upgrade-" + upgradeButtonArrCredit[x].getName();
                        Deadwood.actionMode = actionMode;
                        disableUpgrades();
                        bUpgrade.setVisible(true);
                        break;
                    }
                }
            }
            displayScores(controller.getPlayerOrder());
        }
        public void mousePressed(MouseEvent e) {
        }
        public void mouseReleased(MouseEvent e) {
        }
        public void mouseEntered(MouseEvent e) {
        }
        public void mouseExited(MouseEvent e) {
        }
    }
}