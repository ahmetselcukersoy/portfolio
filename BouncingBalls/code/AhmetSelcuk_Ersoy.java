public class AhmetSelcuk_Ersoy {
    public static void main(String[] args){

        // adjusting initial ballList and creating canvas
        StdDraw.setCanvasSize(Environment.canvasWidth,Environment.canvasHeight);
        StdDraw.setXscale(0,Environment.scaleX);
        StdDraw.setYscale(-1,Environment.scaleY);
        Environment.startTime = System.currentTimeMillis();
        Ball.BallList.add(new Ball(2,Environment.scaleX/4,0.5,0,"r","u"));
        Ball.BallList.add(new Ball(1,Environment.scaleX/3,0.5,0,"l","u"));
        Ball.BallList.add(new Ball(0,Environment.scaleX/4,0.5,0,"r","u"));

        // creating new Environment which is going to run the game inside of it
        Environment env = new Environment();
        while (Environment.isLoop) {
            env.run(true);
        }
    }
}

