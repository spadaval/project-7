package project7;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A bounded sorted array.
 * 
 * Evicts the largest item when full.
 */
class BoundedSortedArray<T extends Comparable<T>> implements Comparator<T>, Iterable<T> {

  ArrayList<T> buffer;
  int maxSize;

  public BoundedSortedArray(int size) {
    buffer = new ArrayList<T>(size);
    this.maxSize = size;
  }

  /**
   * Build a BoundedSortedArray from the hashed contents of a file.
   * @param maxSize
   * @param fileName
   * @return
   */
  static BoundedSortedArray<Integer> fromFile(int maxSize, String fileName) {
    BoundedSortedArray<Integer> sa = new BoundedSortedArray<Integer>(maxSize);

    for (String line : new FileStream(fileName)) {
      sa.add(line.hashCode());
    }
    return sa;
  }

  public HashSet<T> asSet() {
    return new HashSet<T>(buffer);
  }

  /**
   * Add a value to the list. Evicts the largest value if the buffer is full.
   * 
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

  private int getSortedIndex(T value) {
    return getSortedIndex(value, 0, buffer.size());
  }

  /**
   * Find the index to insert the value at.
   * 
   * Quick-select, esentially.
   * 
   * @param value
   * @param start
   * @param end
   * @return The index to insert the value at.
   */
  private int getSortedIndex(T value, int start, int end) {
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
}

public class Minhash {

  static final int signatureSize = 400;

  public double jaccard(String fA, String fB) {
    Set<Integer> sa = BoundedSortedArray.fromFile(signatureSize, fA).asSet();
    Set<Integer> sb = BoundedSortedArray.fromFile(signatureSize, fB).asSet();
    int intersection = Sets.intersection(sa, sb).size();
    int union = sa.size() + sb.size() - intersection;

    return intersection / (double) union;
  }

}
