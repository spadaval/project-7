package project7;

import java.util.HashSet;
import java.util.Set;

class Sets {
    static <T> Set<T> intersection(Set<T> a, Set<T> b) {
        HashSet<T> intersection = new HashSet<T>(a);
        intersection.retainAll(b);
        return intersection;
    }
}
