public class Player {
    private static final double PERIOD_OF_PLAYER = 6000;
    private static final double PLAYER_HEIGHT_SCALEY_RATE = 1.0/8.0;
    private static final double PLAYER_HEIGHT_WIDTH_RATE = 40.0/27.0;
    private static final double velocity = Environment.scaleX / PERIOD_OF_PLAYER * 2.6;
    public static double height = PLAYER_HEIGHT_SCALEY_RATE * Environment.scaleY;
    public static double width = height * (1/PLAYER_HEIGHT_WIDTH_RATE);
    public static double x = Environment.scaleX/2;
    public static double y = height/2;

    Player(){}
    // Player moves
    // If player edges go out of the canvas, player.x is set to canvas edges
    public static double getXLeft() {
        return Math.max(x - velocity * Environment.loopTime * 1000, width / 2);
    }
    public static double getXRight() {
        return Math.min(x + velocity * Environment.loopTime * 1000, Environment.scaleX - width / 2);
    }

}
