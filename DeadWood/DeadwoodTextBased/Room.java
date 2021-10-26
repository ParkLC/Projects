import java.util.*;

public class Room {
    private Card card;
    private int shotCounter;
    private Role[] roles;
    private String name;
    //ArrayList<Player> players represents players actively in the room (note: not players on the card)
    private ArrayList<Player> players = new ArrayList<Player>(); 
    private boolean wrapped = false;

    public Room(String n, int shots, Card c){
        name = n;
        shotCounter = shots;
        card = c;
        wrapped = false;
        if(n == "Trailers" || n == "Casting Office"){
            wrapped = true;
        }
    }

    public String getName(){
        return name;
    }

    public Card getCard(){
        return card;
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
}
