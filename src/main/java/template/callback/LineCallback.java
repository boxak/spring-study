package template.callback;

public interface LineCallback<T> {
    T workWithLine(String line, T value);
}
