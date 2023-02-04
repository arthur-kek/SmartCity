package core.extensions;

import java.util.ArrayList;
import java.util.List;

public class GenericList<T> {

    private List<T> genericList;

    public GenericList() {
        genericList = new ArrayList<>();
    }

    public synchronized void insert(T e) {
        genericList.add(e);
    }

    public synchronized void remove(int id) {
        genericList.remove(id);
    }

    public synchronized void remove(T obj) {
        genericList.remove(obj);
    }

    public synchronized T getAndRemove(int id) {
        T temp = get(id);
        if (temp != null) {
            genericList.remove(temp);
            return temp;
        }
        return null;
    }

    public synchronized void insertAsFirst(T obj) {
        genericList.add(0, obj);
    }

    public synchronized void insertAtCertainPosition(int position, T obj) {
        genericList.add(position, obj);
    }

    public synchronized T get(int id) {
        return genericList.get(id);
    }

    public synchronized boolean isEmpty() {
        return genericList.isEmpty();
    }

    public synchronized List<T> getList() {
        return genericList;
    }
}
