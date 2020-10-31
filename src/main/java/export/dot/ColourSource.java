package export.dot;

public class ColourSource {
    private String[] colours = { "red", "orange", "yellow", "green", "blue", "purple", "lightblue", "lightgreen" };
    private int next = 0;

    public String nextColour() {
        String ret = colours[next];
        next = next + 1 % colours.length;
        return ret;
    }
}