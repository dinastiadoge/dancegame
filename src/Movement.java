import java.util.Objects;

public class Movement {

    int value;

    public Movement(int value) {
        this.value = value;
    }

    public boolean hit(int value){
        return this.value == value;
    }

    public int getValue() {
        return value;
    }
}
