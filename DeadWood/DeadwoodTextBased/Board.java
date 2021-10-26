import java.util.*;

public class Board<Room> {
    Bank bank;

    //The board will be represented by a map
    private Map<Room, List<Room> > map = new HashMap<>();

    /* Method adds a path, from source to destination
       utilizes addRoom so that you can simultaneously develop
       pathways between new and existing rooms
     */
    public void addPath(Room source, Room destination){
        if(!map.containsKey(source)){
            addRoom(source);
        }

        if(!map.containsKey(destination)){
            addRoom(destination);
        }

        map.get(source).add(destination);
        map.get(destination).add(source);
    }

    //Method develops the nodes within the map, a helper function for addPath
    public void addRoom(Room s){
        map.put(s, new LinkedList<Room>());
    }

    //This functions returns an arraylist consisting of all neighbors to a room
    public ArrayList<Room> getNeighbors(Room node){
        ArrayList<Room> neighbors = new ArrayList<Room>();

        for(Room v : map.keySet()){
            if(map.get(v).contains(node))
                neighbors.add(v);
        }
    
        return neighbors;
    }
}

