package DAO;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvTable extends Table implements IQueryable {

    private File file;
    private boolean temporary = false;

    public CsvTable(String path) {
        super(path);
        file = new File(path);
    }

    public CsvTable(String path, boolean temporary) {
        this(path);
        this.temporary = temporary;
    }

    @Override
    public void insert(Dataline selector) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            //TODO:: add field names check
            StringBuffer line = new StringBuffer();
            for (Pair<String, String> pair : selector) {
                line.append(pair.getValue()).append(",");
            }
            line.delete(line.length() - 1, line.length());
            writer.newLine();
            writer.write(line.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Dataline selector) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            List<String> keys  = Arrays.asList(reader.readLine().split(","));
            List<String> names = Arrays.asList(reader.readLine().split(","));

            Dataline keyLine = new Dataline();
            for (String key : keys) {
                keyLine.addField(key, selector.getValue(key));
            }

            List<String> data = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                Dataline tempLine = new Dataline();
                int nameIndex = 0;
                for (String value : Arrays.asList(line.split(","))) {
                    tempLine.addField(names.get(nameIndex++), value);
                }
                if (IQueryable.checkSelect(tempLine, keyLine)) {
                    StringBuffer updatedLine = new StringBuffer();
                    for (Pair<String, String> pair : selector) {
                        updatedLine.append(pair.getValue()).append(",");
                    }
                    updatedLine.delete(updatedLine.length() - 1, updatedLine.length());
                    data.add(updatedLine.toString());
                }
                else {
                    data.add(line);
                }
            }

            writer.write(String.join(",",keys));
            writer.newLine();
            writer.write(String.join(",",names));
            for (String str : data) {
                writer.newLine();
                writer.write(str);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Dataline> select(Dataline selector) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            List<String> names = Arrays.asList(reader.readLine().split(","));

            List<Dataline> res = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                Dataline tempLine = new Dataline();
                int nameIndex = 0;
                for (String value : Arrays.asList(line.split(","))) {
                    tempLine.addField(names.get(nameIndex++), value);
                }
                if (IQueryable.checkSelect(tempLine, selector)) {
                    res.add(tempLine);
                }
            }

            return res;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Dataline> getFullTable() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            List<String> names = Arrays.asList(reader.readLine().split(","));

            List<Dataline> res = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                Dataline tempLine = new Dataline();
                int nameIndex = 0;
                for (String value : Arrays.asList(line.split(","))) {
                    tempLine.addField(names.get(nameIndex++), value);
                }
                res.add(tempLine);
            }

            return res;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() throws Exception {
        if (temporary) {
            file.delete();
        }
    }
}
