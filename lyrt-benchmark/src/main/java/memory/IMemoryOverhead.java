package memory;

public interface IMemoryOverhead<T> {
    void setup();
    T proceed(int numCore, int numRole);
}
