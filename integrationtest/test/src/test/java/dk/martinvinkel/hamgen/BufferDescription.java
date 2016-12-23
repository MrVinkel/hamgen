package dk.martinvinkel.hamgen;

import org.hamcrest.BaseDescription;

import java.util.ArrayList;
import java.util.List;

public class BufferDescription extends BaseDescription {

    private List<String> buffer;

    public BufferDescription() {
        this(new ArrayList<String>());
    }

    public BufferDescription(List<String> buffer) {
        this.buffer = buffer;
    }

    @Override
    protected void append(String str) {
        buffer.add(str);
    }

    @Override
    protected void append(char c) {
        this.append(String.valueOf(c));
    }

    public List<String> getBuffer() {
        return buffer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(String s : buffer) {
            sb.append(s);
        }
        return sb.toString();
    }
}
