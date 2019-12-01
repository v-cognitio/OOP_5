package DAO;

public abstract class Table implements IQueryable, AutoCloseable {

    protected String name;

    public Table(String name) {
        this.name = name;
    }

    public String getName() { return this.name; }

}
