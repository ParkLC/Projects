import java.util.*;

public class Bank{

    //there is probably a better way to do this
    static private int rankTwoCostDollars = 4;
    static private int rankTwoCostCredits = 5;
    static private int rankThreeCostDollars = 10;
    static private int rankThreeCostCredits = 10;
    static private int rankFourCostDollars = 18;
    static private int rankFourCostCredits = 15;
    static private int rankFiveCostDollars = 28;
    static private int rankFiveCostCredits = 20;
    static private int rankSixCostDollars = 40;
    static private int rankSixCostCredits = 25;
    static Deadwood controller = Deadwood.getInstance();
    static BoardLayersListener gui = BoardLayersListener.getInstance();

    /* Method rewards player on their success in acting based on if they are in a
     * starring role or acting as an extra
     * An on card success means that the Player receives 2 credits, and 1 shot counter is removed
     * An off card success means that the Player receives 1 credit, 1 dollar, and 1 shot counter is removed
     * If there are 0 shot counters remaining after an actor has acted, then the scene must wrap
     */
    public static void actingSuccess(Player player, String rollType){
        Room currentRoom = player.getCurrentRoom();
        String message = "Success!\n";
        
        if(rollType == "onCard"){
            player.addCredits(2);
            currentRoom.removeShot();
            message += player.getName() + " got 2 credits!\n";
        }
        else if(rollType == "offCard"){
            player.addCredits(1);
            player.addDollars(1);
            currentRoom.removeShot();
            message += player.getName() + " got 1 dollar and 1 credit\n";
        }

        gui.displayMessage(message);

        if(currentRoom.getShots() == 0){
            Card currentCard = currentRoom.getCard();
            ArrayList<Player> players = currentCard.getPlayers();

            currentRoom.updateWrapped(true);

            if(players.size() != 0){
                sceneWrapBonus(players, currentRoom, currentCard);
            }
            controller.clearPlayerRoles(currentRoom);
            controller.clearPracticeChips(player.getCurrentRoom().getPlayers(), player.getCurrentRoom().getCard().getPlayers());
            gui.resetPositions(currentRoom);
            gui.clearCard(currentRoom.getCard());
        }

        
    }

    /* Method only called when an off card actor fails to act
     * The Player recieves 1 dollar, no shot counters are removed
     */
    public static void actingFail(Player player){
        player.addDollars(1);
    }

    /* Method called when there are 0 shot counters remaining in a room AND there is at least 1 Player acting on the card in the room
     * Method must have access to all Players in the room and the budget
     * Active player "rolls die", the amount pf die is determined by the budget of the room
     * The die amounts corelate to the players based on the roles they are on.
     * The top role gets the highest die amount, the second role gets the second die amount, and so on
     * Off Card roles receive a dollar bonus based on the rank of the role they are working on
     */
    public static void sceneWrapBonus(ArrayList<Player> players, Room room, Card card){
        int budget = card.getBudget();
        int[] dieRolls = controller.rollDie(budget);
        Role[] roles = room.getCard().getRoles();
        int loopVar = roles.length - 1;
        String message = "Scene has wrapped!\n\n";

        for(int i = roles.length; i >= 0; i--){
            if(loopVar >= 0){
                if(roles[loopVar].getPlayer() != null){
                    roles[loopVar].getPlayer().addDollars(dieRolls[i - 1]);
                    message += roles[loopVar].getPlayer().getName() + " just got: " + dieRolls[i - 1] + " Dollars!\n";
                }
                loopVar--;
            } else{
                loopVar = roles.length - 1;
                if(roles[loopVar].getPlayer() != null){
                    roles[loopVar].getPlayer().addDollars(dieRolls[i]);
                    message += roles[loopVar].getPlayer().getName() + " just got: " + dieRolls[i] + " Dollars!\n";
                }
                loopVar--;
            }
        }

        ArrayList<Player> offCardPlayers = room.getPlayers();

        try{
            for(int x = 0; x < offCardPlayers.size(); x++){
                Role playerRole = offCardPlayers.get(x).getCurrentRole();
                int bonus = playerRole.getRank();
                offCardPlayers.get(x).addDollars(bonus);
                message += offCardPlayers.get(x).getName() + " just got " + bonus + " dollars!\n";
            }
        } catch(NullPointerException NPE){
            System.out.println("Null Pointer Exception, unable to give bonus to off card roles");
        }

        gui.displayMessage( message);
    }

    /* Method called when a Player rehearses, rather than acts
     * The Players practice chip attribute increases by 1
     */
    public void gainPracticeChip(Player player){
        player.addPracticeChip();
    }

    /* Method only available when a Player is in the Casting Office Room
     * Based on what rank the Player wants to have, it will cost a certain amount of dollars or credits
     * Method returns true for succesful upgrade
     * Method returns false otherwise
     */
    public boolean upgrade(Player player, int rankRequest, char moneyType){
        if(player.getRank() < rankRequest){
            if(moneyType == '$'){
                switch (rankRequest) {//Switch compares players current dollar amount with what is needed
                    case 2: if(player.getDollars() >= rankTwoCostDollars){
                                if(player.subtractDollars(rankTwoCostDollars)){
                                    player.setRank(2);
                                    gui.setNewRank(player, 2);
                                    gui.displayMessage(controller.getCurrentPlayer().getName() + " is now rank 2");
                                }else{
                                    return false;
                                }
                            }else{
                                gui.displayMessage("Not enough dollars");
                            }
                            break;

                    case 3: if(player.getDollars() >= rankThreeCostDollars){
                                if(player.subtractDollars(rankThreeCostDollars)){
                                    player.setRank(3);
                                    gui.setNewRank(player, 3);
                                    gui.displayMessage(controller.getCurrentPlayer().getName() + " is now rank 3");
                                }else{
                                    return false;
                                }
                            }else{
                                gui.displayMessage("Not enough dollars");
                            }
                            break;

                    case 4: if(player.getDollars() >= rankFourCostDollars){
                                if(player.subtractDollars(rankFourCostDollars)){
                                    player.setRank(4);
                                    gui.setNewRank(player, 4);
                                    gui.displayMessage(controller.getCurrentPlayer().getName() + " is now rank 4");
                                }else{
                                    return false;
                                }
                            }else{
                                gui.displayMessage("Not enough dollars");
                            }
                            break;

                    case 5: if(player.getDollars() >= rankFiveCostDollars){
                                if(player.subtractDollars(rankFiveCostDollars)){
                                    player.setRank(5);
                                    gui.setNewRank(player, 5);
                                    gui.displayMessage(controller.getCurrentPlayer().getName() + " is now rank 5");
                                }else{
                                    return false;
                                }
                            }else{
                                gui.displayMessage("Not enough dollars");
                            }
                            break;

                    case 6: if(player.getDollars() >= rankSixCostDollars){
                                if(player.subtractDollars(rankSixCostDollars)){
                                    player.setRank(6);
                                    gui.setNewRank(player, 6);
                                    gui.displayMessage(controller.getCurrentPlayer().getName() + " is now rank 6");
                                }else{
                                    return false;
                                }
                            }else{
                                gui.displayMessage("Not enough dollars");
                            }
                            break;

                    default : System.out.println("Entered an invalid rank!\n"
                                               + "Ranks range from 1 to 6, try again.\n");
                                return false;
                }
            } else if(moneyType == 'c'){//Switch compares player current credit count with needed amount.
                switch (rankRequest) {
                    case 2: if(player.getCredits() >= rankTwoCostCredits){
                                if(player.subtractCredits(rankTwoCostCredits)){
                                    player.setRank(2);
                                    gui.setNewRank(player, 2);

                                    gui.displayMessage(controller.getCurrentPlayer().getName() + " is now rank 2");

                                }else{
                                    return false;
                                }
                            }else{
                                gui.displayMessage("Not enough credits");
                            }
                            break;

                    case 3: if(player.getCredits() >= rankThreeCostCredits){
                                if(player.subtractCredits(rankThreeCostCredits)){
                                    player.setRank(3);
                                    gui.setNewRank(player, 3);
                                    gui.displayMessage(controller.getCurrentPlayer().getName() + " is now rank 3");

                                }else{
                                    return false;
                                }
                            }else{
                                gui.displayMessage("Not enough credits");
                            }
                            break;

                    case 4: if(player.getCredits() >= rankFourCostCredits){
                                if(player.subtractCredits(rankFourCostCredits)){
                                    player.setRank(4);
                                    gui.setNewRank(player, 4);
                                    gui.displayMessage(controller.getCurrentPlayer().getName() + " is now rank 4");
                                }else{
                                    return false;
                                }
                            }else{
                                gui.displayMessage("Not enough credits");
                            }
                            break;

                    case 5: if(player.getCredits() >= rankFiveCostCredits){
                                if(player.subtractCredits(rankFiveCostCredits)){
                                    player.setRank(5);
                                    gui.setNewRank(player, 5);
                                    gui.displayMessage(controller.getCurrentPlayer().getName() + " is now rank 5");
                                }else{
                                    return false;
                                }
                            }else{
                                gui.displayMessage("Not enough credits");
                            }
                            break;

                    case 6: if(player.getCredits() >= rankSixCostCredits){
                                if(player.subtractCredits(rankSixCostCredits)){
                                    player.setRank(6);
                                    gui.setNewRank(player, 6);
                                    gui.displayMessage(controller.getCurrentPlayer().getName() + " is now rank 6");
                                }else{
                                    return false;
                                }
                            }else{
                                gui.displayMessage("Not enough credits");
                            }
                            break;

                    default : System.out.println("Entered an invalid rank!\n"
                                               + "Ranks range from 1 to 6, try again.\n");
                              return false;
                }
            }

            return true;
        } else{
            gui.displayMessage("Your rank is: " + player.getRank()
                             + "\nTry to purchase a higher rank.");
            return false;
        }
    }

}
