package project7;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

class SortedArray<T extends Comparable<T>> implements Comparator<T>, Iterable<T> {

  ArrayList<T> buffer;
  int maxSize;

  public SortedArray(int size) {
    buffer = new ArrayList<T>(size);
    this.maxSize = size;
  }

  static SortedArray<String> fromFile(int maxSize, String fileName) {
    SortedArray<String> sa = new SortedArray<String>(maxSize);
    Iterator<String> lines = Utils.lines(fileName);
    while (lines.hasNext()) {
      sa.add(lines.next());
    }
    return sa;
  }

  HashSet<T> asSet() {
    HashSet<T> set = new HashSet<T>();
    for (T t : buffer) {
      set.add(t);
    }
    return set;
  }

  int getSortedIndex(T value) {
    return getSortedIndex(value, 0, buffer.size());
  }

  int getSortedIndex(T value, int start, int end) {
    // switch to simple comparisons when scanning through a small enough region of
    // the list
    if (end - start < 10) {
      for (int i = start; i < end; i++) {
        if (compare(value, buffer.get(i)) <= 0) {
          return i;
        }
      }
      return end;
    }
    int mid = (start + end) / 2;
    if (compare(value, buffer.get(mid)) <= 0) {
      return getSortedIndex(value, start, mid);
    } else {
      return getSortedIndex(value, mid, end);
    }
  }

  public void add(T value) {
    // if the buffer is full, the last value is too large and we need to remove it
    if (buffer.size() == maxSize) {
      // If the list is full and the value is larger than all values in the list, we
      // can safely skip it
      if (value.compareTo(buffer.get(buffer.size() - 1)) > 0) {
        return;
      }
      buffer.remove(buffer.size() - 1);
    }

    buffer.add(getSortedIndex(value), value);
  }

  public int compare(T a, T b) {
    return a.compareTo(b);
  }

  public Iterator<T> iterator() {
    return buffer.iterator();
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append("[");
    for (T t : buffer) {
      buf.append(t.toString());
      buf.append(", ");
    }
    buf.append("]");
    buf.append(String.format("(max=%d)", maxSize));
    return buf.toString();
  }
}

class Utils {
  static Iterator<String> lines(String fileName) {
    Scanner scanner = null;
    try {
      scanner = new Scanner(new File(fileName));
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (scanner == null)
      return null;
    return new FileStream(scanner);
  }

  static <T> HashSet<T> intersection(HashSet<T> a, HashSet<T> b) {
    HashSet<T> intersection = new HashSet<T>(a);
    intersection.retainAll(b);
    return intersection;
  }
}

class FileStream implements Iterator<String> {

  Scanner sc;

  FileStream(Scanner sc) {
    this.sc = sc;
  }

  @Override
  public boolean hasNext() {
    return sc.hasNext();
  }

  @Override
  public String next() {
    return sc.next();
  }

}

public class Minhash {

  static final int signatureSize = 500;

  public double jaccard(String fA, String fB) {

    /**
     * fA: Name of first file
     * fB: Name of second file
     */

    HashSet<String> sa = SortedArray.fromFile(signatureSize, fA).asSet();
    HashSet<String> sb = SortedArray.fromFile(signatureSize, fB).asSet();
    int intersection = Utils.intersection(sa, sb).size();
    int union = sa.size() + sb.size() - intersection;

    return intersection / (double) union;
  }

}
