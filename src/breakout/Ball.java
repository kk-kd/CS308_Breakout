package breakout;

import breakout.directions.CollisionDirection;
import breakout.directions.MovingDirection;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class Ball extends Element {
    private double dx;
    private double dy;
    private double radius;

    private Circle instance;
    private CollisionDirection collisionDirection;

    public static final int ERROR = 1;
    public Ball(double x, double y, int alpha, int speed, double radius, Paint fill, MovingDirection direction) {
        super(x, y - radius, alpha, speed, fill, direction);

        this.radius = radius;
        makeShape();
        collisionDirection = CollisionDirection.NO_COLLISION;
    }

    public Ball(double x, double y, int speed, double radius, Paint fill) {
        this(x, y, 60, speed, radius, fill, MovingDirection.STAY);
    }

    @Override
    public void makeShape() {
        instance = new Circle(x, y, radius);
        instance.setFill(fill);
    }

    @Override
    public void updateMovingDirection() {
        dx = Math.cos(Math.toRadians(alpha));
        dy = Math.sin(Math.toRadians(alpha));

        switch (movingDirection) {
            case UPRIGHT:
                dy = -dy;
                break;
            case UPLEFT:
                dx = -dx;
                dy = -dy;
                break;
            case DOWNLEFT:
                dx = -dx;
            case DOWNRIGHT:
                // Do not have to change
                break;
            case STAY:
                dx = 0;
                dy = 0;
            default:
                break;
        }
    }

    @Override
    public Circle getInstance() throws NullPointerException {
        if (instance == null) {
            throw new NullPointerException("Instance has not been created");
        }
        return instance;
    }

    public void move(double elapsedTime) {
//        System.out.println("dx: " + dx);
//        System.out.println("dy: " + dy);
        x = instance.getCenterX() + dx * elapsedTime * speed;
        y = instance.getCenterY() + dy * elapsedTime * speed;
        instance.setCenterX(x);
        instance.setCenterY(y);
    }

    public void paddleCollision() {
        collisionDirection = CollisionDirection.DOWNTOUP;
        changeDirection();
    }

    public boolean hitBoundary() {
        return ((x - radius < 0) || (x + radius > Level.BG_WIDTH) || (y - radius < 0)) || (y + radius > Level.BG_HEIGHT); //TODO: delete the last condition
//        System.out.println("x + radius: " + (x + radius));
//        System.out.println("x - radius: " + (x - radius));
//        System.out.println("y + radius: " + (y + radius));
    }

    public void changeDirection() {
        switch (collisionDirection) {
            case LEFTTORIGHT:
                movingDirection = movingDirection == MovingDirection.UPLEFT ? MovingDirection.UPRIGHT : MovingDirection.DOWNRIGHT;
                break;
            case UPTODOWN:
                movingDirection = movingDirection == MovingDirection.UPRIGHT ? MovingDirection.DOWNRIGHT : MovingDirection.DOWNLEFT;
                break;
            case RIGHTTOLEFT:
                movingDirection = movingDirection == MovingDirection.UPRIGHT ? MovingDirection.UPLEFT : MovingDirection.DOWNLEFT;
                break;
            case DOWNTOUP:
                movingDirection = movingDirection == MovingDirection.DOWNLEFT ? MovingDirection.UPLEFT : MovingDirection.UPRIGHT;
            default:
                break;
        }
        updateMovingDirection();
        collisionDirection = CollisionDirection.NO_COLLISION;
    }

    public void boundaryCollision() {
        if (x - radius < 0) {
            collisionDirection = CollisionDirection.LEFTTORIGHT;
        } else if (x + radius > Level.BG_WIDTH) {
            collisionDirection = CollisionDirection.RIGHTTOLEFT;
        } else if (y - radius < 0) {
            collisionDirection = CollisionDirection.UPTODOWN;
        } else if (y + radius > Level.BG_HEIGHT) {  // TODO: delete this!
            collisionDirection = CollisionDirection.DOWNTOUP;
        } else {
            throw new IllegalStateException("There is no collision!");
        }
        changeDirection();
    }

    public void brickCollision(Brick brick) {
        if (y + radius <= brick.getY()) {
            collisionDirection = CollisionDirection.DOWNTOUP;
        } else if (y - radius >= brick.getY() + Brick.BRICK_HEIGHT) {
            collisionDirection = CollisionDirection.UPTODOWN;
        } else if (x < brick.getX()) {
            collisionDirection = CollisionDirection.RIGHTTOLEFT;
        } else if (x > brick.getX() + Brick.BRICK_WIDTH) {
            collisionDirection = CollisionDirection.LEFTTORIGHT;
        } else {
            collisionDirection = CollisionDirection.LEFTTORIGHT;
            System.out.println("Warning: Invalid Brick Collision!");
        }
        changeDirection();
    }
}
