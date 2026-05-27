package kill.bill.backend.Utilities;

public class Position {
    private int position_x = 0;
    private int position_y = 0;


    public Position(int x,int y){
        this.position_x = x;
        this.position_y = y;
    }

    public int getPosition_x() {
        return position_x;
    }

    public int getPosition_y() {
        return position_y;
    }

    public void increment_x() {
        this.position_x += 1;
    }

    public void increment_y() {
        this.position_y += 1;
    }

    public void decrement_x() {
        this.position_x -= 1;
    }

    public void decrement_y() {
        this.position_y -= 1;
    }

}
