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

    /* Method rewards player on their success in acting based on if they are in a
     * starring role or acting as an extra
     * An on card success means that the Player receives 2 credits, and 1 shot counter is removed
     * An off card success means that the Player receives 1 credit, 1 dollar, and 1 shot counter is removed
     * If there are 0 shot counters remaining after an actor has acted, then the scene must wrap
     */
    public static void actingSuccess(Player player, String rollType){
        Room currentRoom = player.getCurrentRoom();
        
        if(rollType == "onCard"){
            player.addCredits(2);
            currentRoom.removeShot();
            System.out.println("Congrats player " + player.getName() + ", you've received 2 credits!");
            System.out.println(currentRoom.getName() + " now has " + currentRoom.getShots() + " remaining!");
        }
        else if(rollType == "offCard"){
            player.addCredits(1);
            player.addDollars(1);
            currentRoom.removeShot();
            System.out.println("Congrats player " + player.getName() + ", you've received 1 dollar and 1 credit!");
            System.out.println(currentRoom.getName() + " now has " + currentRoom.getShots() + " shots remaining!");
        }

        if(currentRoom.getShots() == 0){
            Card currentCard = currentRoom.getCard();
            ArrayList<Player> players = currentCard.getPlayers();

            currentRoom.updateWrapped(true);
            System.out.println("Looks like the scene is wrapped!");

            if(players.size() != 0){
                sceneWrapBonus(players, currentRoom, currentCard);
            }

            //players should not be tied to roles
            Deadwood.clearPlayerRoles(currentRoom);

        }
    }

    public void displayPrices(){
        System.out.println("Here are the ranks and their prices:\n"
        + "Rank | Dollars | Credits\n"
        + "  2  |    " + rankTwoCostDollars + "    |    " + rankTwoCostCredits + "\n"
        + "  3  |    " + rankThreeCostDollars + "   |    " + rankThreeCostCredits + "\n"
        + "  4  |    " + rankFourCostDollars + "   |    " + rankFourCostCredits + "\n"
        + "  5  |    " + rankFiveCostDollars + "   |    " + rankFiveCostCredits + "\n"
        + "  6  |    " + rankSixCostDollars + "   |    " + rankSixCostCredits + "\n");

        System.out.println("Syntax for buying a rank:\n"
                        + "Buying with Dollars: upgrade-$-(rank number)\n"
                        + "Buying with Credits: upgrade-c-(rank number)\n");
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
        System.out.print("There was at least 1 actor acting on a card in this room! So ");
        for(int x = 0; x < players.size(); x++){
            System.out.print(players.get(x).getName() + " ");
        }
        System.out.println("all get bonuses!");

        int budget = card.getBudget();
        int[] dieRolls = Deadwood.rollDie(budget);
        Role[] roles = room.getCard().getRoles();
        int loopVar = roles.length - 1;

        for(int i = roles.length; i >= 0; i--){
            if(loopVar >= 0){
                if(roles[loopVar].getPlayer() != null){
                    roles[loopVar].getPlayer().addDollars(dieRolls[i]);
                    System.out.println(roles[loopVar].getPlayer().getName() + " just got: " + dieRolls[i] + " Dollars");
                }
                loopVar--;
            } else{
                loopVar = roles.length - 1;
                if(roles[loopVar].getPlayer() != null){
                    roles[loopVar].getPlayer().addDollars(dieRolls[i]);
                    System.out.println(roles[loopVar].getPlayer().getName() + " just got: " + dieRolls[i] + " Dollars");
                }
                loopVar--;
            }
        }

        ArrayList<Player> offCardPlayers = room.getPlayers();

        if(offCardPlayers.size() > 0){
            System.out.print("Don't worry ");
            for(int x = 0; x < offCardPlayers.size(); x++){
                System.out.print(offCardPlayers.get(x).getName() + " ");
            }
            System.out.println("you all get a bonus too!");
        }

        for(int x = 0; x < offCardPlayers.size(); x++){
            Role playerRole = offCardPlayers.get(x).getCurrentRole();
            int bonus = playerRole.getRank();
            offCardPlayers.get(x).addDollars(bonus);
            System.out.println(offCardPlayers.get(x).getName() + " just got " + bonus + " dollars!");
        }

        Deadwood.clearPracticeChips(offCardPlayers, card.getPlayers());

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
                                }else{
                                    return false;
                                }
                            }
                            break;

                    case 3: if(player.getDollars() >= rankThreeCostDollars){
                                if(player.subtractDollars(rankThreeCostDollars)){
                                    player.setRank(3);
                                }else{
                                    return false;
                                }
                            }
                            break;

                    case 4: if(player.getDollars() >= rankFourCostDollars){
                                if(player.subtractDollars(rankFourCostDollars)){
                                    player.setRank(4);
                                }else{
                                    return false;
                                }
                            }
                            break;

                    case 5: if(player.getDollars() >= rankFiveCostDollars){
                                if(player.subtractDollars(rankFiveCostDollars)){
                                    player.setRank(5);
                                }else{
                                    return false;
                                }
                            }
                            break;

                    case 6: if(player.getDollars() >= rankSixCostDollars){
                                if(player.subtractDollars(rankSixCostDollars)){
                                    player.setRank(6);
                                }else{
                                    return false;
                                }
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
                                }else{
                                    return false;
                                }
                            }
                            break;

                    case 3: if(player.getCredits() >= rankThreeCostCredits){
                                if(player.subtractCredits(rankThreeCostCredits)){
                                    player.setRank(3);
                                }else{
                                    return false;
                                }
                            }
                            break;

                    case 4: if(player.getCredits() >= rankFourCostCredits){
                                if(player.subtractCredits(rankFourCostCredits)){
                                    player.setRank(4);
                                }else{
                                    return false;
                                }
                            }
                            break;

                    case 5: if(player.getCredits() >= rankFiveCostCredits){
                                if(player.subtractCredits(rankFiveCostCredits)){
                                    player.setRank(5);
                                }else{
                                    return false;
                                }
                            }
                            break;

                    case 6: if(player.getCredits() >= rankSixCostCredits){
                                if(player.subtractCredits(rankSixCostCredits)){
                                    player.setRank(6);
                                }else{
                                    return false;
                                }
                            }
                            break;

                    default : System.out.println("Entered an invalid rank!\n"
                                               + "Ranks range from 1 to 6, try again.\n");
                              return false;
                }
            }

            return true;
        } else{
            System.out.println("Your rank is: " + player.getRank()
                             + "\nTry to purchase a higher rank.\n");
            return false;
        }
    }

}
