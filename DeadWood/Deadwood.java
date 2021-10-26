import java.util.*;
import java.io.*;
import javax.swing.*;

public class Deadwood{
    static Player currentPlayer;
    static int currentPlayerIndex;
    static int currentDay = 1;
    static int maxDays;
    static Player[] playerOrder;
    static int scenesRemaining;
    static int playerAmount;
    static Room[] rooms;
    static Card[] cards = new Card[40];
    static String[] colors = new String[]{"BLUE", "GREEN", "RED", "YELLOW", "CYAN", "ORANGE", "PINK", "VIOLET"};//used for identifying players
    @SuppressWarnings("unchecked")
    static Board<Room> board = Board.getInstance();
    static int cardsFlipped = -1;
    static Scanner sc;
    static Bank bank = new Bank();
    static BoardLayersListener gui = BoardLayersListener.getInstance();
    static volatile String actionMode = "";

    /* Singleton */
    public static final Deadwood instance = new Deadwood();

    private Deadwood() {
    }

    public static Deadwood getInstance() {
        return instance;
    }
    /* End Singleton */

    /* Method called before the game begins
     * special rules are put in place
     * board object is created
     * room objects and paths to rooms are created
     * each players room is set to trailers and each player is added to trailers
     */
    public static void initializeBoard(){
        playerAmount = gui.getPlayerAmount();
        playerOrder = new Player[playerAmount];
        int e = 0;
        while(e < playerAmount){
            playerOrder[e] = new Player(colors[e], "images/dice/" + (""+colors[e].charAt(0)).toLowerCase() + "1.png", new JLabel());
            e++;
        }
        currentPlayer = playerOrder[0];
       
        gui.initPlayerPosition(playerOrder);
        specialRules();
        createRooms();
        createPaths();
        
        //set each players room to the trailers
        for(int x = 0; x < playerAmount; x++){
            playerOrder[x].setCurrentRoom(rooms[0]);
            rooms[0].addPlayer(playerOrder[x]);
        }
        
        gui.initUpgradeButtons();
        gui.initBlankCards(rooms);
        gui.initShotCounters(rooms);
        gui.displayScores(playerOrder);
        gui.displayVisibleButtons(getCurrentPlayer());
        gui.setVisible(true);
    }

    /* The first thing that happens at the beginning of each day (and the beginning of the game)
     * Sets the Players in the Trailer
     * Resets shot counter for each Room
     * current day is incremented
     */
    public static void newDay(){
        createRooms();
        createPaths();
        for(int x = 0; x < playerAmount; x++){
            playerOrder[x].setCurrentRoom(rooms[0]);
            rooms[0].addPlayer(playerOrder[x]);
            playerOrder[x].setMoveFlag(false);
            gui.resetGUI();
        }
        currentDay++;

        gui.displayMessage("It's a new day! Day " + currentDay + ". All players are back at the trailers. " + currentPlayer.getName() + ", you're up!");
        gui.displayVisibleButtons(currentPlayer);
    }

    /* Method called when the game is over
     * Calculate scores and declares winner
     */
    public static void endGame(){
        String message = "The game is over! Here are the scores:\n";
        Player winner = playerOrder[0];
        int topScore = 0;
        for(int x = 0; x < playerOrder.length; x++){
            message += "\nPlayer: " + playerOrder[x].getName() + " Score: " + calculateScore(playerOrder[x]);
            if(calculateScore(playerOrder[x]) > topScore){
                winner = playerOrder[x];
                topScore = calculateScore(playerOrder[x]);
            }
        }
        message += "\nThe winner is player: " + winner.getName();
        message += "\nThank you for playing :)";
        gui.displayMessage(message);
        System.exit(1);
    }

    //read from text file
    //this will be called the first time a player enters a room
    //avoids problem of creating 10 new objects at the start of every day, and instead creating them over the course of the game
    /* Method called when a player enters a room for the first time
     * used for initalizing card objects
     * card object data read in from a cards.txt
     * FORMAT: name,budget,numberOfRoles,roleName,roleRank,roleDescription (continues for numberOfRoles)
     */
    public static void flipCard(Room room){
        cardsFlipped++;
        try{
            if(cardsFlipped == 0){
                File cardFile = new File("cards.txt");
                sc = new Scanner(cardFile);
            }
            String cardLine = sc.nextLine();
            String[] cardLineArray = cardLine.split(",");
            String name = cardLineArray[0];
            int budget = Integer.parseInt(cardLineArray[1]);
            int numRoles = Integer.parseInt(cardLineArray[2]);
            String imageUrl = cardLineArray[cardLineArray.length - 1];
            cards[cardsFlipped] = new Card();
            room.setCard(cards[cardsFlipped]);
            room.getCard().initalize(name, budget, imageUrl);
            String roleName;
            int roleRank;
            Role[] roles = new Role[numRoles];
            int i = 0;
            int startingX = 0;
            int incr = 0;
            if(numRoles == 3){
                startingX = 20;
            }
            else if(numRoles == 2){
                startingX = 53;
            }
            else if(numRoles == 1){
                startingX = 83;
            }
            for(int x = 0; x < numRoles; x++){
                roleName = cardLineArray[3 + (x*2)];
                roleRank = Integer.parseInt(cardLineArray[4 + (x*2)]);
                Role role = new Role(roleName, roleRank, startingX + incr, 47);
                roles[i] = role;
                i++;
                incr+=62;
            }
        switch(roles.length){
            case 1: room.getCard().setRoles(roles[0]);
                    break;
            case 2: room.getCard().setRoles(roles[0], roles[1]);
                    break;
            case 3: room.getCard().setRoles(roles[0], roles[1], roles[2]);
                    break;
            default: System.out.println("\nERROR on card initialization\n");
                    System.exit(0);
        }
        
        }
        catch(FileNotFoundException e){
            System.out.println("File not found");
        }
        gui.revealCard(room, room.getCard());
    }

    /* Method called at start of each day
     * creates room object, sets name, shot counter, has empty card
     */
    public static void createRooms(){
         rooms = new Room[12];
         
         rooms[0] = new Room("Trailers", 0, null, 995, 275);

         rooms[1] = new Room("Casting Office", 0, null, 9, 459);

         rooms[2] = new Room("Main Street", 3, null, 2, 969, 28, 969, 28, new int[] {804, 23, 858, 23, 912, 23}, 969, 148);
         rooms[2].setRoles(new Role("Railroad worker", 1, 637, 22), new Role("Falls off Roof", 2, 720, 22), 
                           new Role("Woman in black Dress", 2, 637, 105), new Role("Mayor McGinty", 4, 720, 105));

         rooms[3] = new Room("Saloon", 2, null, 3, 632, 280, 632, 280, new int[] {679, 216, 626, 216}, 632, 400);
         rooms[3].setRoles(new Role("Reluctant Farmer", 1, 877, 352), new Role("Woman in Red Dress", 2, 877, 276));

         rooms[4] = new Room("Bank", 1, null, 4, 623, 475, 623, 476, new int[] {840, 549}, 623, 596);
         rooms[4].setRoles(new Role("Suspicious Gentleman", 2, 911, 554), new Role("Flustered Teller", 3, 911, 470));

         rooms[5] = new Room("Church", 2, null, 5, 623, 734, 624, 734, new int[] {682, 675, 623, 675}, 624, 854);
         rooms[5].setRoles(new Role("Dead Man", 1, 857, 730), new Role("Crying Woman", 2, 858, 730));

         rooms[6] = new Room("Hotel", 3, null, 6, 969, 740, 969, 741, new int[] {1111, 683, 1058, 683, 1005, 683}, 969, 861);
         rooms[6].setRoles(new Role("Sleeping Drunkard", 1, 1111, 469), new Role("Rare Player", 1, 1044, 509), 
                           new Role("Falls from Balcony", 2, 1111, 557), new Role("Australian Bartender", 3, 1046, 596));

         rooms[7] = new Room("Secret Hideout", 3, null, 7, 27, 732, 27, 732, new int[] {354, 764, 299, 764, 244, 764}, 27, 852);
         rooms[7].setRoles(new Role("Clumsy Pit Fighter", 1, 435, 719), new Role("Thug with Knife", 2, 521, 719), 
                           new Role("Dangerous Tom", 3, 435, 808), new Role("Penny, who is Lost", 4, 521, 808));
         
         rooms[8] = new Room("Ranch", 2, null, 8, 252, 478, 252, 478, new int[] {525, 473, 472, 473}, 252, 598);
         rooms[8].setRoles(new Role("Shot in Leg", 1, 412, 608), new Role("Saucy Fred", 2, 488, 608), 
                           new Role("Man Under Horse", 3, 488, 525));

         rooms[9] = new Room("General Store", 2, null, 9, 370, 282, 370, 282, new int[] {313, 330, 313, 277}, 370, 402);
         rooms[9].setRoles(new Role("Man in Overalls", 1, 236, 276), new Role("Mister Keach", 3, 236, 358));

         rooms[10] = new Room("Jail", 1, null, 10, 281, 27, 281, 27, new int[] {442, 156}, 281, 147);
         rooms[10].setRoles(new Role("Prisoner in Cell", 2, 519, 25), new Role("Feller in Irons", 3, 519, 105));

         rooms[11] = new Room("Train Station", 3, null, 11, 21, 69, 21, 69, new int[] {141, 11, 89, 11, 36, 11}, 21, 189);
         rooms[11].setRoles(new Role("Crusty Prospector", 1, 114, 227), new Role("Dragged by Train", 1, 51, 268), 
                            new Role("Preacher with Bag", 2, 114, 320), new Role("Cyrus the Gunfighter", 4, 49, 356));
    }

    /* Method called to create paths to each room node
     * Paths could be changed to create unique boards
     */
    public static void createPaths(){
        board.addPath(rooms[0], rooms[2]);//trailer <-> main street
        board.addPath(rooms[0], rooms[3]);//trailer <-> saloon
        board.addPath(rooms[0], rooms[6]);//trailer <-> Hotel
        board.addPath(rooms[2], rooms[3]);//main street <-> saloon
        board.addPath(rooms[2], rooms[10]);//main street <-> jail
        board.addPath(rooms[3], rooms[9]);//saloon <-> general store
        board.addPath(rooms[3], rooms[4]);//saloon <-> bank
        board.addPath(rooms[4], rooms[8]);//bank <-> ranch
        board.addPath(rooms[4], rooms[5]);//bank <-> church
        board.addPath(rooms[4], rooms[6]);//bank <-> hotel
        board.addPath(rooms[5], rooms[6]);//church <-> hotel
        board.addPath(rooms[5], rooms[7]);//church <-> secret hideout
        board.addPath(rooms[7], rooms[8]);//secret hideout <-> ranch
        board.addPath(rooms[7], rooms[1]);//secret hideout <-> casting office
        board.addPath(rooms[8], rooms[1]);//ranch <-> casting office
        board.addPath(rooms[8], rooms[9]);//ranch <-> general store
        board.addPath(rooms[9], rooms[11]);//general store <-> train station
        board.addPath(rooms[9], rooms[10]);//general store <-> jail
        board.addPath(rooms[10], rooms[11]);//jail <-> train station
        board.addPath(rooms[11], rooms[1]);//train station <-> casting office
    }
    
    /* Method checks if a room is next to another room
     * Used to make sure a move is legal
     */
    public static boolean isNeighbor(Room source, Room destination){
        ArrayList<Room> neighbors = board.getNeighbors(source);
        boolean isNeighbor = false;

        for(int x = 0; x < neighbors.size(); x++){
            if(!isNeighbor && destination.getName() == (neighbors.get(x)).getName()){
                isNeighbor = true;
            }
        }
        return isNeighbor;
    }

    /* The final method that is called at the end of the game
     * Score is calculated by (dollars + credits + (rank*5))
     */
    public static int calculateScore(Player player){
        return player.getDollars() + player.getCredits() + (player.getRank() * 5);
    }

    /* Method called from Bank.java
     * Used to clear players roles and set to null
     * Also sets the rooms wrapped status to true
     */
    public static void clearPlayerRoles(Room room){
        ArrayList<Player> offCardPlayers = room.getPlayers();
        ArrayList<Player> onCardPlayers = room.getCard().getPlayers();

        Role[] offCardRoles = room.getRoles();
        Role[] onCardRoles = room.getCard().getRoles();

        //Remove roles from players
        if(offCardPlayers != null){
            for(int x = 0; x < offCardPlayers.size(); x++){
                offCardPlayers.get(x).setCurrentRole(null);
            }
        }
        if(onCardPlayers != null){
            for(int x = 0; x < onCardPlayers.size(); x++){
                onCardPlayers.get(x).setCurrentRole(null);
            }
        }

        //Remove players from roles
        for(int x = 0; x < offCardRoles.length; x++){
            offCardRoles[x].setPlayer(null);
        }
        for(int x = 0; x < onCardRoles.length; x++){
            onCardRoles[x].setPlayer(null);
        }

        room.updateWrapped(true);
    }

    /* Method called when a scene wraps
     * Resets practice chips for each player to 0
     */
    public static void clearPracticeChips(ArrayList<Player> offCardPlayers, ArrayList<Player> onCardPlayers){
        for(int x = 0; x < offCardPlayers.size(); x++){
            offCardPlayers.get(x).resetPracticeChips();
        }
        for(int x = 0; x < onCardPlayers.size(); x++){
            onCardPlayers.get(x).resetPracticeChips();
        }
    }

    /* Gets current player, usually called from BoardLayersListener
     */
    public static Player getCurrentPlayer(){
        return currentPlayer;
    }

    /* Gets array of all players, usually called from BoardLayersListener
     */
    public static Player[] getPlayerOrder(){
        return playerOrder;
    }

    /* Gets array of all rooms, usually called from BoardLayersListener
     */
    public static Room[] getRooms(){
        return rooms;
    }

    /* Returns how many roles a player can take at 1 time
     * Takes into account player and role rank,
     * if role is already taken,
     * if room is wrapped
     * Usually called from BoardLayersListener
     */
    public static int getAvailableRolesCount(){
        Room room = currentPlayer.getCurrentRoom();
        if(room.getName() == "Trailers" || room.getName() == "Casting Office"){
            return 0;
        }
        Role[] roomRoles = room.getRoles();
        Card card = room.getCard();
        int roleCount = 0;
        Role[] cardRoles = card.getRoles();

        for(int i = 0; i < roomRoles.length; i++){
            if(roomRoles[i].getRank() <= currentPlayer.getRank() && roomRoles[i].getPlayer() == null){
                roleCount++;
            }
        }

        for(int i = 0; i < cardRoles.length; i++){
            if(cardRoles[i].getRank() <= currentPlayer.getRank() && cardRoles[i].getPlayer() == null){
                roleCount++;
            }
        }
        return roleCount;
    }

    /* Assigns a Role to a Player
     * If player takes on card role, they are removed from the room for room wrap bonus payouts
     * Returns true if Role is available and succesfully taken
     * Returns false otherwise
     */
    public static boolean takeARole(String roleName){
        Room currentRoom = currentPlayer.getCurrentRoom();
        Card currentCard = currentRoom.getCard();
        Role[] roomRoles = currentRoom.getRoles();
        Role[] cardRoles = currentCard.getRoles();
        Boolean roleTaken = false;
        for(int x = 0; x < roomRoles.length; x++){
            if(roomRoles[x].getName().equals(roleName) && roomRoles[x].isRoleAvailable() && currentPlayer.getRank() >= roomRoles[x].getRank() && currentRoom.hasWrapped() == "unwrapped"){
                currentPlayer.setCurrentRole(roomRoles[x]);
                currentPlayer.setRoleType("offCard");
                roomRoles[x].setPlayer(currentPlayer);
                roomRoles[x].setRoleAvailable(false);
                roleTaken = true;
            }
        }

        for(int x = 0; x < cardRoles.length; x++){
            if(cardRoles[x].getName().equals(roleName) && cardRoles[x].isRoleAvailable() && currentPlayer.getRank() >= cardRoles[x].getRank()  && currentRoom.hasWrapped() == "unwrapped"){
                currentPlayer.setCurrentRole(cardRoles[x]);
                currentPlayer.setRoleType("onCard");
                cardRoles[x].setPlayer(currentPlayer);
                cardRoles[x].setRoleAvailable(false);
                currentCard.addPlayer(currentPlayer);
                currentRoom.removePlayer(currentPlayer);
                roleTaken = true;
            }
        }

        if(roleTaken){
            Role role = currentPlayer.getCurrentRole();
            if(currentPlayer.getRoleType() == "offCard"){
                gui.movePlayer(currentPlayer, role.getXCoord(), role.getYCoord());
            }
            else if(currentPlayer.getRoleType() == "onCard"){
                gui.movePlayer(currentPlayer, currentRoom.getCardX() + role.getXCoord(), currentRoom.getCardY() + role.getYCoord());
            }
        }
        return roleTaken;
    }

    /* Player must be assigned to a role to be able to act
     * "Die is rolled" and die output + Players practice chips are compared to the scene budget
     * Returns true for succesfully acting
     * Returns false otherwise
     */
    public static boolean attemptToAct(){
        Role playerRole = currentPlayer.getCurrentRole();
        Room playerRoom = currentPlayer.getCurrentRoom();
        Card currentCard = playerRoom.getCard();
        if(currentCard == null){
            return false;
        }
        int budget = currentCard.getBudget();
        String roleType = currentPlayer.getRoleType();

        Boolean acted = false;

        if(playerRole != null && playerRoom.hasWrapped() != "wrapped"){
            int[] dieRoll = rollDie(1);
            //Commented out but not deleted in case TA or Prof wants to see what is being roled
            //System.out.println("Die roll: " + dieRoll[0] + " practice chips: " + currentPlayer.getPracticeChips());
            //System.out.println("Budget: " + currentPlayer.getCurrentRoom().getCard().getBudget());
            if(dieRoll[0] + currentPlayer.getPracticeChips() >= budget){//Success
                Bank.actingSuccess(currentPlayer, roleType);
                acted = true;
                gui.removeShotCounter(playerRoom);
            }else if(roleType == "offCard"){//Fail
                Bank.actingFail(currentPlayer);
            }
        }else{
            System.out.println("You have yet to take a role!");
        }
        gui.displayScores(playerOrder);
        return acted;
    }

    /* Method called when a player has a role but doesn't want to act
     * Returns true if they can rehearse
     * Returns false otherwise
     */
    public static boolean rehearse(){
        Role playerRole = currentPlayer.getCurrentRole();
        Room playerRoom = currentPlayer.getCurrentRoom();
        String message = "";

        if(playerRole != null && playerRoom.hasWrapped() != "wrapped"){
            int budget = ((currentPlayer.getCurrentRoom()).getCard()).getBudget();
            if((currentPlayer.getPracticeChips()) >= budget){
                message += "The budget of the room is " + budget + " and you have " + currentPlayer.getPracticeChips() + " practice chips so you are guarenteed success if you act! So no more rehearsing!!";
                gui.displayMessage(message);
                return true;
            }else{
                currentPlayer.addPracticeChip();
                message += currentPlayer.getName() + " has received a practice chip\n";
                message += "They now have " + currentPlayer.getPracticeChips();
                gui.displayScores(playerOrder);
                gui.displayMessage(message);
                return true;
            }
        }else{
            message += "You have yet to take a role!";
            gui.displayMessage(message);
            return false;
        }
       
    }

    /* Ends a players turn
     * Method called when player clicks end button in BoardLayersListener
     */
    public static void endTurn(){
        currentPlayer.setMoveFlag(false);
        currentPlayerIndex++;
        if(currentPlayerIndex == playerAmount){
            currentPlayerIndex = 0;
        }
        currentPlayer = playerOrder[currentPlayerIndex];
        gui.displayCurrentPlayer(currentPlayer);
        gui.displayVisibleButtons(currentPlayer);
        
        if(unwrappedRooms().size() <= 1){
            clearFinalRoom(unwrappedRooms().get(0));
            newDay();
        }
        if(isGameOver()){
            endGame();
        }
        
    }

    /* Method gets the arrayList of unwrapped rooms
     * Used to check if the day should end
     */
    public static ArrayList<Room> unwrappedRooms(){
        ArrayList<Room> unwrappedRooms = new ArrayList<Room>();
        int count = 0;
        for(int i = 2; i < rooms.length; i++){
            if(rooms[i].hasWrapped() == "unwrapped"){
                count++;
                unwrappedRooms.add(rooms[i]);
            }
        }
        if(count <= 1){
            return unwrappedRooms;
        }
        return unwrappedRooms;
    }

    /* Method called when the day is over but there is one room that needs its card clared
     * Called from endTurn()
     */
    public static void clearFinalRoom(Room room){
        ArrayList<Player> offCardPlayers = room.getPlayers();
        Role[] offCardRoles = room.getRoles();

        if(offCardPlayers != null){
            for(int x = 0; x < offCardPlayers.size(); x++){
                offCardPlayers.get(x).setCurrentRole(null);
            }
        }

        for(int x = 0; x < offCardRoles.length; x++){
            offCardRoles[x].setPlayer(null);
        }

        if(room.getCard() != null){
            ArrayList<Player> onCardPlayers = room.getCard().getPlayers();
            Role[] onCardRoles = room.getCard().getRoles();

            if(onCardPlayers != null){
                for(int x = 0; x < onCardPlayers.size(); x++){
                    onCardPlayers.get(x).setCurrentRole(null);
                }
            }

            for(int x = 0; x < onCardRoles.length; x++){
                onCardRoles[x].setPlayer(null);
            }

            gui.bPane.remove(room.getCard().getJLabel());
        }
        room.updateWrapped(true);
    }

    /* Player wants to move from their current room to a new room
     * Room must be adjacent to players current room
     * Returns true if succesfully moved
     * Returns false otherwise
     */
    public static void movePlayer(Player player, Room newRoom){
        Room currentRoom = player.getCurrentRoom();

        boolean isNeighbor = isNeighbor(currentRoom, newRoom);
        if(isNeighbor && player.getCurrentRole() == null){
            player.getCurrentRoom().removePlayer(player);
            player.setCurrentRoom(newRoom);
            newRoom.addPlayer(player);
            for(int i = 0; i < playerOrder.length; i++){
                if(currentPlayer.equals(playerOrder[i])){
                    gui.movePlayer(currentPlayer, newRoom.getPlayerHolderCoord()[i*2], newRoom.getPlayerHolderCoord()[i*2 + 1]);
                }
            }
        }else if(!isNeighbor){
            System.out.println("Sorry! " + currentRoom.getName() + " is not next to " + newRoom.getName());
        }else if(!(player.getCurrentRole() == null)){
            System.out.println("Sorry! You can't leave a room until it has wrapped! There are still " + currentRoom.getShots() + " shot(s) remaining!");
        }
    }

    /* Die is rolled when a player attempts to act (dieAmount = 1) OR
     * Die is rolled when a scene wraps (dieAmount = scene budget)
     * Returns an array of ints with each index representing a die roll
     */
    public static int[] rollDie(int dieCount){
        int[] dieArray = new int[dieCount];
        for(int d = 0; d < dieCount; d++){
            Random rr = new Random();
            int roll = rr.nextInt(6) + 1;
            dieArray[d] = roll;
        }

        //sorted from lowest (index 0), to highest (index dieCount - 1)
        Arrays.sort(dieArray);

        return dieArray;
    }

    //method goes room by room summing up all shots remaing
    //used to check if the day should end
    public static int totalShotsRemaning(){
        int shotsRemaining = 0;
        for(int x = 0; x < 10; x++){
            shotsRemaining += rooms[x].getShots();
        }
        
        return shotsRemaining;
    }

    public static boolean isGameOver(){
        return currentDay > maxDays;
    }

    /* Method called before the game begins, and adds special rules depending on playerCount
     * 2-3 Players: 3 days
     * 4 Players:   Normal rules
     * 5 Players:   Players start with 2 credits
     * 6 Players:   Players start with 4 credits
     * 7-8 Players: Players start at rank 2
     */
    public static void specialRules(){
        if(playerAmount <= 3){
            maxDays = 3;
        }else if(playerAmount == 4){
            maxDays = 4;
        }else if(playerAmount == 5){
            //players start with 2 credits
            for(int i = 0; i < playerAmount; i++){
                playerOrder[i].addCredits(2);
            }
            maxDays = 4;
        }else if(playerAmount == 6){
            //players start with 4 credits
            for(int i = 0; i < playerAmount; i++){
                playerOrder[i].addCredits(4);
            }
            maxDays = 4;
        }else{
            //players start with rank 2
            for(int i = 0; i < playerAmount; i++){
                playerOrder[i].setRank(2);
                gui.setNewRank(playerOrder[i], 2);
            }
            maxDays = 4;
        }
    }
    
    /* Deadwood.java should be called as such: java Deadwood*/
    public static void main(String args[]){
        initializeBoard();
        gui.displayCurrentPlayer(currentPlayer);

        //the game begins
        while(currentDay <= maxDays){
        
            Scanner in = new Scanner(System.in);
            String playerInput = actionMode;

            while(!(playerInput.equals("end"))){
                while(playerInput.equals("")){
                    playerInput = actionMode;
                }
                if(playerInput.contains("work")){
                    try{
                        String[] inputArray = playerInput.split("-");
                        String roleName = inputArray[1];
                        if(takeARole(roleName)){
                            actionMode = "";
                            endTurn();
                            break;
                        }
                        else{
                            System.out.println("Sorry! This role is either spelt wrong, not in this room, already has someone acting on it, the room is wrapped, or you aren't the right rank!\n");
                            actionMode = "";
                        }
                    } catch(ArrayIndexOutOfBoundsException ex){
                        System.out.println("Whoops, looks like your syntax is wrong. If you need to see what roles there are, type 'role options'\n");
                        actionMode = "";
                    }
                }else if(playerInput.equals("Act")){
                    if(attemptToAct()){
                        actionMode = "";
                        endTurn();
                        break;
                    }
                    else{
                        actionMode = "";
                        endTurn();
                        break;
                    }
                }else if(playerInput.equals("Rehearse")){
                    if(rehearse()){
                        actionMode = "";
                        endTurn();
                        break;
                    }
                }else if(playerInput.contains("move")){
                    String[] moveLocation = playerInput.split("-");
                    String location = moveLocation[1];
                    for(int x = 0; x < rooms.length; x++){
                        if(rooms[x].getName().equals(location) && !currentPlayer.getMoveFlag()){
                            movePlayer(currentPlayer, rooms[x]);
                            currentPlayer.setMoveFlag(true);
                            if(rooms[x].getCard() == null){
                                //not equal to trailers or casting office
                                if(!(rooms[x].equals(rooms[0])) && !(rooms[x].equals(rooms[1]))){
                                    flipCard(rooms[x]);
                                }
                            }
                            gui.displayVisibleButtons(currentPlayer);
                        }
                    }
                    actionMode = "";
                }else if(playerInput.contains("upgrade")){
                    int rankChoice = 0;
                    String[] upgradePlayer = playerInput.split("-");
                    if(upgradePlayer.length == 1 && currentPlayer.getCurrentRoom().getName().equals("Casting Office")){
                        // bank.displayPrices();
                    } else if(upgradePlayer[1].toLowerCase().equals("c") || upgradePlayer[1].equals("$") && upgradePlayer.length == 3 && currentPlayer.getCurrentRoom().getName().equals("Casting Office")){
                        try{
                            rankChoice = Integer.parseInt(upgradePlayer[2]);
                            actionMode = "";
                        }catch(Exception z){
                            System.out.println("WRONG SYNTAX");
                            actionMode = "";
                            break;
                        }
                        if(bank.upgrade(currentPlayer, rankChoice, upgradePlayer[1].toLowerCase().charAt(0))){
                            break;
                        }
                    }
                }
                playerInput = "";
            }//end of while that checks for player input
        currentPlayer.setMoveFlag(false);
        } //end of while check for days
    }
}