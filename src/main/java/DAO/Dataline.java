package DAO;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Dataline implements Iterable< Pair<String, String> > {

    private List< Pair<String, String> > data = new ArrayList<>();

    public Dataline addField(String name, String value) {
        data.add(new Pair<>(name, value));
        return this;
    }

    public int getSize() {
        return data.size();
    }

    public String getValue(int index) {
        return data.get(index).getValue();
    }

    public String getValue(String name) {
        for (Pair p : data) {
            if (p.getKey().equals(name)) {
                return (String) p.getValue();
            }
        }
        return null;
    }

    @Override
    public Iterator< Pair<String, String> > iterator() {
        return data.iterator();
    }
}
