package core.extensions;

import java.util.HashMap;
import java.util.Map;

public class MapQueue<T> {

    private Map<Integer, GenericList<T>> map;

    public MapQueue() {
        map = new HashMap<>();
    }

    public synchronized void addToQueue(int id, T e) {
        GenericList<T> temp;
        if (map.get(id) == null) {
            temp = new GenericList<>();
        } else {
            temp = map.get(id);
        }
        temp.insert(e);
        map.put(id, temp);
        notifyAll();
    }

    public synchronized T getFromQueue(int id) {
        if (map.get(id) == null || map.get(id).isEmpty()) {
            notifyAll();
            return null;
        }

        GenericList<T> temp = map.get(id);
        T obj = temp.get(0);
        map.put(id, temp);
        return obj;
    }

    public synchronized void removeFromQueue(int id) {
        GenericList<T> temp = map.get(id);
        temp.remove(0);
        map.put(id, temp);
    }
}
