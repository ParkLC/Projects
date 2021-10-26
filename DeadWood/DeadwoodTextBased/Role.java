public class Role {
    private String name;
    private int rank;
    private Player currentPlayer = null;
    private boolean roleAvailable = true;

    public Role(String n, int r){
        name = n;
        rank = r;
        currentPlayer = null;
        roleAvailable = true;
    }
    
    public String getName(){
        return name;
    }

    public int getRank(){
        return rank;
    }

    public Player getPlayer(){
        return currentPlayer;
    }

    public void setPlayer(Player player){
        currentPlayer = player;
    }

    /* role only available if
     * room is unwrapped
     * player not already on role
     * role rank is less than or equal to player rank
     */
    public void setRoleAvailable(boolean avail){
        roleAvailable = avail;
    }

    public boolean isRoleAvailable(){
        return roleAvailable;
    }
}

