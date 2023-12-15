public class Environment {
   public static double scaleY = 9.0;
   public static double scaleX = 16.0;
   public static double TOTAL_GAME_DURATION = 40000;
   public static int PAUSE_DURATION = 15;
   public static int canvasWidth = 800;
   public static int canvasHeight = 500;
   public static long startTime;
   public static long currTime;
   public static double time;
   public static double tempStartTime;
   public static double loopTime;
   public static boolean isArrowExist = false;
   public static boolean isLoop = true;
   public static String game = "lose";

   Environment() {}

   public void run(boolean tempBool){
      Environment.isLoop = tempBool;

      // adjusting time variables
      Environment.currTime = System.currentTimeMillis();
      Environment.time = (System.currentTimeMillis()-Environment.startTime)/1000.0;
      Environment.loopTime = (System.currentTimeMillis()-Environment.tempStartTime)/1000.0;

      StdDraw.picture(Environment.scaleX/2,Environment.scaleY/2, "background.png",Environment.scaleX,Environment.scaleY);

      // drawing arrow
      if ((StdDraw.isKeyPressed(32)) && (!Environment.isArrowExist)) {
         Environment.isArrowExist = true;
         Arrow.posX = Player.x;
         Arrow.startTime = currTime;
      }
      if (Environment.isArrowExist) {
         double x = Arrow.posX;
         double y = Arrow.getY();
         StdDraw.picture(x,y,"arrow.png",0.15,Environment.scaleY);
      } else Arrow.posY = -Environment.scaleY/2;

      // drawing player
      if (StdDraw.isKeyPressed(37)) {
         Player.x = Player.getXLeft();
      }
      if (StdDraw.isKeyPressed(39)) {
         Player.x = Player.getXRight();
      }
      StdDraw.picture(Player.x,Player.y, "player_back.png", Player.width, Player.height);

      // drawing bar, if time is greater than total duration, end the loop, game = "lose"
      StdDraw.picture(8, -0.5, "bar.png", 16, 1);
      if (Environment.currTime - Environment.startTime < Environment.TOTAL_GAME_DURATION) {
         double x = Bar.getBarX();
         int green = Bar.getBarGreen();
         StdDraw.setPenColor(225,green,0);
         StdDraw.filledRectangle(0, -0.5, x, 0.25);
      }
      else
         Environment.isLoop = false;

      // move every ball in BallList
      for (Ball ball : Ball.BallList)
         if (ball != null)
            ball.move();

      // if a ball splits into half add to BallList
      if (Ball.addBall) {
         Ball.BallList.add(Ball.tempBallLeft);
         Ball.BallList.add(Ball.tempBallRight);
         Ball.addBall = false;
      }

      // check whether BallList is empty or not
      // if empty, end the loop, game = "win"
      int ballCounter = 0;
      for (Ball ball : Ball.BallList)
         if ((ball != null) && (ball.level > -1))
            ballCounter += 1;
      if (ballCounter < 1) {
         Environment.game = "win";
         Environment.isLoop = false;
      }

      // if loop is ended, show the gameScreen and ask "yes" or "no"
      if (!Environment.isLoop) {
         StdDraw.picture(Environment.scaleX / 2, Environment.scaleY / 2.18, "game_screen.png",
                 Environment.scaleX / 3.18, Environment.scaleY / 4);
         Font font = new Font("Helvetica", Font.BOLD, 30);
         StdDraw.setFont(font);
         StdDraw.setPenColor(StdDraw.BLACK);
         // win or lose
         if (Environment.game.equals("lose")) {
            StdDraw.text(Environment.scaleX / 2, Environment.scaleY / 2, "Game Over!");
         }
         else
            StdDraw.text(Environment.scaleX / 2, Environment.scaleY / 2, "You Won!");
         // replay or quit
         font = new Font("Helvetica", Font.ITALIC, 15);
         StdDraw.setFont(font);
         StdDraw.text(Environment.scaleX / 2, Environment.scaleY / 2.3, "To Replay Click \"Y\"");
         StdDraw.text(Environment.scaleX / 2, Environment.scaleY / 2.6, "To Quit Click \"N\"");
         StdDraw.show();
         StdDraw.enableDoubleBuffering();

         // waiting user to press "y" or "n"
         while (!Environment.isLoop) {
            if (StdDraw.isKeyPressed(89)) {
               // if game is going to continue, making adjustments for game to start
               // ballList, game=lose, arrow does not exist, player.x, loop=true.
               Environment.startTime = System.currentTimeMillis();
               Player.x = Environment.scaleX/2;
               Ball.BallList.clear();
               Ball.BallList.add(new Ball(2,Environment.scaleX/4,0.5,0,"r","u"));
               Ball.BallList.add(new Ball(1,Environment.scaleX/3,0.5,0,"l","u"));
               Ball.BallList.add(new Ball(0,Environment.scaleX/4,0.5,0,"r","u"));
               Environment.isLoop = true;
               Environment.isArrowExist = false;
               Environment.game = "lose";
            } else if (StdDraw.isKeyPressed(78))
               System.exit(0);
         }
      }

      // if game is continuing "then" draw the balls to the canvas
      for (Ball ball : Ball.BallList)
         if (ball != null)
            if (ball.level > -1)
               StdDraw.picture(ball.x, ball.y, "ball.png", ball.currentRadius * 2, ball.currentRadius * 2);
      StdDraw.show();
      Environment.tempStartTime = System.currentTimeMillis();
      StdDraw.pause(Environment.PAUSE_DURATION);
      StdDraw.enableDoubleBuffering();
      StdDraw.clear();
   }
}
