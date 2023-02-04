package core.extensions;

import java.util.HashMap;
import java.util.Map;

public class MapQueue<T> {

    private Map<Integer, GenericList<T>> map;

    public MapQueue() {
        map = new HashMap<>();
    }

    public synchronized void addToQueue(int key, T e) {
        GenericList<T> temp;
        if (map.get(key) == null) {
            temp = new GenericList<>();
        } else {
            temp = map.get(key);
        }
        temp.insert(e);
        map.put(key, temp);
    }

    public synchronized T getFromQueue(int key) {
        if (map.get(key) == null || map.get(key).isEmpty()) {
            return null;
        }

        GenericList<T> temp = map.get(key);
        T obj = temp.get(0);
        map.put(key, temp);
        return obj;
    }

    public synchronized T getAndRemoveFromQueue(int key) {
        if (map.get(key) == null || map.get(key).isEmpty()) {
            return null;
        }

        GenericList<T> temp = map.get(key);
        T obj = temp.getAndRemove(0);
        map.put(key, temp);
        return obj;
    }

    public synchronized void removeFromQueue(int key) {
        GenericList<T> temp = map.get(key);
        temp.remove(0);
        map.put(key, temp);
    }

    public synchronized void insertAsFirst(int key, T e) {
        GenericList<T> temp = map.get(key);
        temp.insertAsFirst(e);
        map.put(key, temp);
    }

    public synchronized void insertAtCertainPosition(int key, int position, T e) {
        GenericList<T> temp = map.get(key);
        temp.insertAtCertainPosition(position, e);
        map.put(key, temp);
    }

    public synchronized GenericList<T> getEntireQueue(int key) {
        return map.get(key);
    }

}
