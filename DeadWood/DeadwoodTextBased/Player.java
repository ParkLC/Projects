public class Player{
    private String name;
    private int dollars = 0;
    private int credits = 0;
    private int rank = 1;
    private Role currentRole;
    private Room currentRoom;
    private int practiceChips;
    private String roleType;
    private boolean hasMoved = false;

    public Player(String n){
        name = n;
    }

    public String getName(){
        return name;
    }

    public int getDollars(){
        return dollars;
    }

    public int getCredits(){
        return credits;
    }

    public int getPracticeChips(){
        return practiceChips;
    }

    public int getRank(){
        return rank;
    }

    public Role getCurrentRole(){
        return currentRole;
    }

    public Room getCurrentRoom(){
        return currentRoom;
    }

    public void addDollars(int amount){
        dollars += amount;
    }

    public boolean subtractDollars(int amount){
        if(amount <= dollars){
            System.out.println("previous dolars: " + dollars);
            dollars -= amount;
            System.out.println("new dollars: " + dollars);
            return true;
        } else{
            System.out.println("Insufficient Dollars");
            return false;
        }
    }

    public void addCredits(int amount){
        credits += amount;
    }

    public boolean subtractCredits(int amount){
        if(amount <= credits){
            System.out.println("previous credits: " + credits);
            credits -= amount;
            System.out.println("new credits: " + credits);
            return true;
        } else{
            System.out.println("Insufficient Credits");
            return false;
        }
    }

    public void addPracticeChip(){
        practiceChips++;
    }

    public void resetPracticeChips(){
        practiceChips = 0;
    }

    public void setRank(int r){
        rank = r;
    }

    public void setCurrentRole(Role role){
        currentRole = role;
    }

    public void setCurrentRoom(Room room){
        currentRoom = room;
    }

    public void setRoleType(String rt){
        roleType = rt;
    }

    public String getRoleType(){
        return roleType;
    }

    public boolean getMoveFlag(){
        return hasMoved;
    }
    public void setMoveFlag(boolean f){
        hasMoved = f;
    }
}

