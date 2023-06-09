package project7;

import java.io.File;
import java.util.Iterator;
import java.util.Scanner;

/**
 * A really simple iterator/iterable that returns lines from a file.
 * 
 * We want this to avoid having to read the entire file into memory.
 * There really should be a built-in way to do this, but the Java standard
 * libraries leave much to be desired.
 */
class FileStream implements Iterator<String>, Iterable<String> {
    Scanner sc;
    String fileName;

    FileStream(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean hasNext() {
        return sc.hasNext();
    }

    @Override
    public String next() {
        return sc.next();
    }

    @Override
    public Iterator<String> iterator() {
        try {
            sc = new Scanner(new File(fileName));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return this;
    }
}