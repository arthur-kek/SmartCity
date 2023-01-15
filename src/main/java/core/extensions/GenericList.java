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
        T obj = genericList.get(id);
        genericList.remove(obj);
    }

    public synchronized T get(int id) {
        return genericList.get(id);
    }

    public synchronized boolean isEmpty() {
        return genericList.isEmpty();
    }
}
