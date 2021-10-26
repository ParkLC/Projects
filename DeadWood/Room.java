import java.util.*;
import javax.swing.*;

public class Room {
    private Card card;
    private int shotCounter;
    private Role[] roles;
    private String name;
    //ArrayList<Player> players represents players actively in the room (note: not players on the card)
    private ArrayList<Player> players = new ArrayList<Player>(); 
    private boolean wrapped = false;
    private int ID;
    private int cardSlotX;
    private int cardSlotY;
    private ArrayList<Integer> shotCounterCoord = new ArrayList<Integer>();// Format: [X1][Y1][X2][Y2]...
    private int roomXCoord;
    private int roomYCoord;
    private JLabel[] shotLabels;
    private JLabel roomLabel = new JLabel();
    private int[] playerHolders = new int[16];// Format: [X1][Y1][X2][Y2]...

    public Room(String n, int shots, Card c, int id, int roomX, int roomY, int cardX, int cardY, int[] shotCCoord, int playerHolderX, int playerHolderY){
        name = n;
        shotCounter = shots;
        card = c;
        ID = id;
        cardSlotX = cardX;
        cardSlotY = cardY;
        roomXCoord = roomX;
        roomYCoord = roomY;
        for(int i : shotCCoord){
            shotCounterCoord.add(i);
        }
        wrapped = false;
        if(n == "Trailers" || n == "Casting Office"){
            wrapped = true;
        }
        shotLabels = new JLabel[shotCounter];

        int offset = 0;
        for(int j = 1; j <= 16; j+=2){
            if(j == 9){
                offset = 0;
                playerHolderY+=50;
            }
            playerHolders[j-1] = playerHolderX+offset;
            playerHolders[j] = playerHolderY;
            offset+=50;
        }
    }

    public Room(String n, int shots, Card c, int roomX, int roomY){
        name = n;
        shotCounter = shots;
        card = c;
        roomXCoord = roomX;
        roomYCoord = roomY;
        wrapped = false;
        if(n == "Trailers" || n == "Casting Office"){
            wrapped = true;
        }

        int offset = 0;
        for(int j = 1; j <= 16; j+=2){
            if(j == 9){
                offset = 0;
                roomY+=50;
            }
            playerHolders[j-1] = roomX+offset;
            playerHolders[j] = roomY;
            offset+=50;
        }
    }

    public String getName(){
        return name;
    }

    public Card getCard(){
        return card;
    }

    public int getID(){
        return ID;
    }

    public void setCard(Card c){
        card = c;
    }

    public int getShots(){
        return shotCounter;
    }

    public void removeShot(){
        shotCounter = shotCounter - 1;
    }

    public Role[] getRoles(){
        return roles;
    }

    /* All four iterations of setRoles used when the specific rooms are created
     * What method called depends on how many roles are in each room (4, 3, 2, 1)
     * Note: roles only for the room, NOT for the card
     */
    public void setRoles(Role a, Role b, Role c, Role d){
        roles = new Role[] {a, b, c, d};
    }
    
    public void setRoles(Role a, Role b, Role c){
        roles = new Role[] {a, b, c};
    }
    
    public void setRoles(Role a, Role b){
        roles = new Role[] {a, b};
    }

    public void setRoles(Role a){
        roles = new Role[] {a};
    }

    public String hasWrapped(){
        if(wrapped){
            return "wrapped";
        }
        else{
            return "unwrapped";
        }
    }

    public void updateWrapped(boolean update){
        wrapped = update;
    }

    public ArrayList<Player> getPlayers(){
        return players;
    }

    /* Player is added to room when they enter the room
     * Necessary to keep track for when the room wraps
     */
    public void addPlayer(Player player){
        players.add(player);
    }

    /* Player is removed from room when they leave room
     * Iterates through players to find the player specified and
     * removes them from players arrayList
     */
    public void removePlayer(Player player){
        for(int x = 0; x < players.size(); x++){
            if(players.get(x) == player){
                players.remove(x);
            }
        }
    }

    public int getCardX(){
        return cardSlotX;
    }

    public int getCardY(){
        return cardSlotY;
    }

    public int getRoomX(){
        return roomXCoord;
    }

    public int getRoomY(){
        return roomYCoord;
    }

    public ArrayList<Integer> getShotCounterCoords(){
        return shotCounterCoord;
    }

    public JLabel[] getShotLabels(){
        return shotLabels;
    }

    public JLabel getJLabel(){
        return roomLabel;
    }

    public int[] getPlayerHolderCoord(){
        return playerHolders;
    }
}

