package project7;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

/**
 * A really simple iterator/iterable that returns lines from a file.
 * 
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
    }
    return this;
  }
}
/**
 * A bounded sorted array.
 * 
 * Evicts the largest item when full. 
 */
class SortedArray<T extends Comparable<T>> implements Comparator<T>, Iterable<T> {

  ArrayList<T> buffer;
  int maxSize;

  public SortedArray(int size) {
    buffer = new ArrayList<T>(size);
    this.maxSize = size;
  }

  static SortedArray<Integer> fromFile(int maxSize, String fileName) {
    SortedArray<Integer> sa = new SortedArray<Integer>(maxSize);

    for (String line : new FileStream(fileName)) {
      sa.add(line.hashCode());
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

  /**
   * Find the index to insert the value at.
   * 
   * Quick-select, esentially.
   * @param value
   * @param start
   * @param end
   * @return The index to insert the value at. 
   */
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

  /**
   * Add a value to the list. Evicts the largest value if the buffer is full.
   * @param value
   */
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

class Sets {
  static <T> HashSet<T> intersection(HashSet<T> a, HashSet<T> b) {
    HashSet<T> intersection = new HashSet<T>(a);
    intersection.retainAll(b);
    return intersection;
  }
}



public class Minhash {

  static final int signatureSize = 400;

  public double jaccard(String fA, String fB) {
    HashSet<Integer> sa = SortedArray.fromFile(signatureSize, fA).asSet();
    HashSet<Integer> sb = SortedArray.fromFile(signatureSize, fB).asSet();
    int intersection = Sets.intersection(sa, sb).size();
    int union = sa.size() + sb.size() - intersection;

    return intersection / (double) union;
  }

}
