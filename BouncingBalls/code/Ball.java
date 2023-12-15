import java.util.ArrayList;

public class Ball {
    private static final double minPossibleHeight = Player.height * 1.4;
    private static final double minPossibleRadius = Environment.scaleY * 0.0175;
    private final double Vx;
    private double Vy;
    private final double maxVy;
    private final double timeAir;

    public static ArrayList<Ball> BallList = new ArrayList<>();
    public String sideDir;
    public String vertDir;
    public int level;
    public double x;
    public double y;
    public double ballTime;
    public double currentRadius;
    public double currentHeight;
    public static Ball tempBallRight;
    public static Ball tempBallLeft;
    public static boolean addBall = false;

    // defining ball initially and its currents
    Ball(int level, double x, double y, double time, String sideDir, String vertDir) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.ballTime = time;
        this.sideDir = sideDir;
        this.vertDir = vertDir;
        double HEIGHT_MULTIPLIER = 1.75;
        double RADIUS_MULTIPLIER = 2;
        double GRAVITY = 0.000003 * Environment.scaleY * 1000000;
        double PERIOD_OF_BALL = 15000;
        currentHeight = minPossibleHeight * Math.pow(HEIGHT_MULTIPLIER, level);
        currentRadius = minPossibleRadius * Math.pow(RADIUS_MULTIPLIER, level);
        maxVy = Math.pow(2* GRAVITY *currentHeight,0.5) / 70;
        timeAir = Math.pow(currentHeight*2/ GRAVITY,0.5) * 3;
        Vx = Environment.scaleX / PERIOD_OF_BALL * 40;
        Vy = maxVy;
    }

    // ball move function
    public void move() {

        // ball moves to the right until its edges go out of the canvas
        if (sideDir.equals("r")) {
            if (x + Vx < Environment.scaleX - currentRadius)
                x += Vx;
            else {
                x -= Vx;
                sideDir = "l";
            }
        }

        // ball moves to the left until its edges go out of the canvas
        if (sideDir.equals("l")){
            if (x - Vx > currentRadius)
                x -= Vx;
            else {
                x += Vx;
                sideDir = "r";
            }
        }

        // ball's vertical movement is calculated by percentage of
        // (time elapsed) / (expected time in the air)

        double percentage = (Environment.time - ballTime) / timeAir;
        // when ball goes "up", if timeAir limit is exceeded, then direction will be "down".
        if (vertDir.equals("u")) {
            if (Environment.time - ballTime < timeAir) {
                Vy = maxVy * (1 - percentage);
                y += Vy;
            }
            else {
                vertDir = "d";
                ballTime = Environment.time;
                return;
            }
        }

        // when ball goes "down", if ball hits the ground
        // then direction will be "up"
        if (vertDir.equals("d")) {
            if (y - Vy > currentRadius) {
                Vy = maxVy * percentage;
                y -= Vy;
            }
            else {
                vertDir = "u";
                ballTime = Environment.time;
                y = currentRadius;
            }
        }

        // check whether ball's edges touch arrow
        if ((Math.abs(x - Arrow.posX) < currentRadius) && (Environment.isArrowExist)) {
            if ((y < Arrow.posY + 4.5) || (Math.pow(y - (Arrow.posY + 4.5), 2) + Math.pow(x - (Arrow.posX), 2) < Math.pow(currentRadius, 2))) {
                if (level > 0) {
                    // split the ball, tempBalls will be added to list later in Env. class
                    addBall = true;
                    tempBallRight = new Ball(level-1,x,y,Environment.time,"r","u");
                    tempBallLeft = new Ball(level-1,x,y,Environment.time,"l","u");level = -1;
                }
                level = -1;
                x = -99;
                y = -99;

                // prevent another ball touch arrow at the same time
                Environment.isArrowExist = false;
            }
        }

        // check whether player's edges touch ball, if yes, game = "lose"
        if (y - currentRadius < Player.height) {
            if ((x + currentRadius > Player.x-Player.width/2) && (x < Player.x)) {
                Environment.isLoop = false;
            }
            if ((x - currentRadius < Player.x + Player.width / 2) && (x > Player.x)) {
                Environment.isLoop = false;
            }

        }
    }
}