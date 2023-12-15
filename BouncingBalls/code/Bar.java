public class Bar {
    Bar(){}
    // getting bottom bar length every frame
    public static double getBarX() {
        return Environment.scaleX - (Environment.currTime - Environment.startTime) *
                Environment.scaleX / Environment.TOTAL_GAME_DURATION;
    }
    // getting bottom bar green color every frame
    public static int getBarGreen(){
        return 225 - (int) ((Environment.currTime-Environment.startTime) * 225 / Environment.TOTAL_GAME_DURATION);

    }
}
