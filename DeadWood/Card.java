import java.util.*;
import javax.swing.*;

public class Card {
    private String name;
    private int budget;
    private Role[] roles;
    private Boolean flipped = false;
    //ArrayList<Player> players represents players actively on the card (note: not players in the room)
    private ArrayList<Player> players = new ArrayList<Player>(); 
    private ImageIcon cIcon;
    private JLabel cardLabel;
    /* Method called when a player enters into a room for the first time
     */
    public void initalize(String n, int b, String cardIconUrl){
        name = n;
        budget = b;
        cIcon = new ImageIcon(cardIconUrl);
        cardLabel = new JLabel();
        flipped = true;
    }

    public String getName(){
        return name;
    }

    public int getBudget(){
        return budget;
    }

    public ArrayList<Player> getPlayers(){
        return players;
    }

    public Role[] getRoles(){
        return roles;
    }

    public boolean getFlipped(){
        return flipped;
    }

    public void setFlipped(Boolean status){
        flipped = status;
    }

    /* All three iterations of setRoles used when the specific card is created
     * What method is called depends on how many roles are on each card (3, 2, 1)
     * Note: roles only for the card, NOT for the room
     */
    public void setRoles(Role a, Role b, Role c){
        roles = new Role[] {a, b, c};
    }

    public void setRoles(Role a, Role b){
        roles = new Role[] {a, b};
    }

    public void setRoles(Role a){
        roles = new Role[] {a};
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

    public ImageIcon getImage(){
        return cIcon;
    }

    public JLabel getJLabel(){
        return cardLabel;
    }
}
