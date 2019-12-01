package DAO;

import javafx.util.Pair;

import java.util.List;

public interface IQueryable {

    void insert(Dataline selector);

    void update(Dataline selector);

    List<Dataline> select(Dataline selector);

    List<Dataline> getFullTable();

    static boolean checkSelect(Dataline query, Dataline selector) {
        for (Pair<String, String> el : selector) {
            if (!query.getValue(el.getKey()).equals(el.getValue())) {
                return false;
            }
        }

        return true;
    }
}
