public class Arrow {
    public static double posX;
    public static double posY;
    public static double startTime;
    private static final double PERIOD_OF_ARROW = 1500;

    Arrow() {}
    // getY gives the top of arrow position, calculation with percentage
    public static double getY() {
        double percentage = (Environment.currTime-startTime) / PERIOD_OF_ARROW;
        posY = -Environment.scaleY/2 + Environment.scaleY * percentage;
        if (posY > Environment.scaleY/2)
            Environment.isArrowExist = false;
        return posY;
    }
}