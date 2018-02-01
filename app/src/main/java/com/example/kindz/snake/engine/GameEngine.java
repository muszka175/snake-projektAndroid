package com.example.kindz.snake.engine;

import android.graphics.Path;

import com.example.kindz.snake.classes.Coordinate;
import com.example.kindz.snake.enums.Direction;
import com.example.kindz.snake.enums.GameState;
import com.example.kindz.snake.enums.TileType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kindz on 12.12.2017.
 */

public class GameEngine {
    // wymiary gry, proporcje
    public static final int GameWidth = 28;
    public static final int GameHeight = 42;

    private List<Coordinate> walls = new ArrayList<>();
    private List<Coordinate> snake = new ArrayList<>();
    private List<Coordinate> apples = new ArrayList<>();

    private Random random = new Random(); // random do spawnu jabłek
    private boolean increaseTail = false; // czy ogon ma się powiększyć?

    private Direction currentDirection = Direction.East; // kierunek w którą stronę ma iśc snake na dzień dobry
    private Direction lastDirection = Direction.East;

    private GameState currentGameState = GameState.Running; // stan gry

    private long snakeSpeed = 500;




    // gdzie głowa snake'a
    private Coordinate getSnakeHead(){
        return snake.get(0);
    }
    //konstruktor
    public GameEngine(){

    }

    //inicjalizacja gry
    public void initGame(){

        AddSnake();
//        AddWalls();
        AddApples();
    }

    // update kierunku w którym idzie snake
    public void UpdateDirection(Direction newDirection){
        if(Math.abs(newDirection.ordinal() - currentDirection.ordinal()) % 2 == 1){
            currentDirection = newDirection;
        }

    }

    // chodzenie snake'a
    public void Update(){
        //Update the snake
        lastDirection = currentDirection;
        switch(currentDirection){
            case North:
                UpdateSnake(0,-1);
                break;
            case East: //right
                UpdateSnake(1,0);
                break;
            case South:
                UpdateSnake(0,1);
                break;
            case West:
                UpdateSnake(-1,0);
                break;
        }

        //Check wall collision
        for(Coordinate w: walls){
            if(snake.get(0).equals(w)){
                currentGameState = GameState.Lost;
            }
        }

        // Check self collision
        for (int i = 1 ; i < snake.size() ; i++){
            if(getSnakeHead().equals(snake.get(i))) {
                currentGameState = GameState.Lost;
                return;
            }
        }

        //Check apples
        Coordinate appleToRemove = null;
        for(Coordinate apple : apples){
            if(getSnakeHead().equals(apple)){
                appleToRemove = apple;
                increaseTail = true;
            }
        }
        if(appleToRemove != null){
            apples.remove(appleToRemove);
            AddApples();
        }


    }


    // tworzenie mapy, snake'a i jabłek
    public TileType[][] getMap(){
        TileType[][] map = new TileType[GameWidth][GameHeight];

        for(int x = 0; x < GameWidth; x++){
            for(int y = 0; y < GameHeight; y++){
                map[x][y] = TileType.Nothing;
            }
        }

        for (Coordinate wall: walls){
            map[wall.getX()][wall.getY()] = TileType.Wall;
        }

        for (Coordinate s: snake){
            map[s.getX()][s.getY()] = TileType.SnakeTail;
        }

        for(Coordinate a: apples){
            map[a.getX()][a.getY()] = TileType.Apple;
        }


        map[snake.get(0).getX()][snake.get(0).getY()] = TileType.SnakeHead;

        return map;
    }


    private void UpdateSnake(int x, int y){
        int newX = snake.get(snake.size() - 1).getX();
        int newY = snake.get(snake.size() -1).getY();

        for (int i = snake.size() -1; i > 0 ; i--) {
            snake.get(i).setX(snake.get(i-1).getX());
            snake.get(i).setY(snake.get(i-1).getY());
        }

        // wydłużanie ogonka
        if (increaseTail){
            snake.add(new Coordinate(newX,newY));
            increaseTail = false;
            snakeSpeed = (long) (getSnakeSpeed() * 0.9d); //po każdym zjedzeniu jabłka szybciej o 10%
        }

        int newHeadPositionX = snake.get(0).getX() + x;
        int newHeadPositionY = snake.get(0).getY() + y;

        if(newHeadPositionX<0) newHeadPositionX = GameWidth-1;
        if(newHeadPositionX>GameWidth-1) newHeadPositionX = 0;
        if(newHeadPositionY<0) newHeadPositionY = GameHeight-1;
        if(newHeadPositionY>GameHeight-1) newHeadPositionY = 0;


        snake.get(0).setX(newHeadPositionX);
        snake.get(0).setY(newHeadPositionY);



    }

    //tworzenie snake'a
    private void AddSnake() {
        snake.clear(); // by wyczyścić wszystkie elementy z listy snake

        snake.add(new Coordinate(7,7));
        snake.add(new Coordinate(6,7));
        snake.add(new Coordinate(5,7));
        snake.add(new Coordinate(4,7));
//        snake.add(new Coordinate(3,7));
//        snake.add(new Coordinate(2,7));

    }

    // tworzenie ścian gry
    private void AddWalls() {

        // Top and bottom walls
        for (int x = 0; x < GameWidth; x++) {
            walls.add(new Coordinate(x, 0)); //top
            walls.add(new Coordinate(x, GameHeight - 1)); //bottom
        }

        // Left and right walls
        // y = 1 bo mamy już w punkcie 0 kawałek ściany
        for (int y = 1; y < GameHeight; y++) {
            walls.add(new Coordinate(0, y));
            walls.add(new Coordinate(GameWidth - 1, y));
        }



    }
    private void AddApples(){
        Coordinate coordinate = null;

        boolean added = false; // czy dodano jablko?

        // dodawanie jabłek, by nie dodać w ścianę czy tam, gdzie znajduje się snake
        while ( !added ) {
            int x = 1 + random.nextInt(GameWidth - 2);
            int y = 1 + random.nextInt(GameHeight - 2);

            // kolizja
            coordinate = new Coordinate(x, y);
            boolean collision = false;
            for (Coordinate s : snake) {
                if (s.equals(coordinate)) {
                    collision = true;
                    //break;
                }
            }

          /*  if (collision = true) {
                continue;
            }*/

            for (Coordinate a : apples) {
                if (a.equals(coordinate)) {
                    collision = true;
                    //break;
                }
            }

            added = !collision;
        }

        apples.add(coordinate);
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public GameState getCurrentGameState(){
        return currentGameState;
    }


    public long getSnakeSpeed() {
        return snakeSpeed;
    }
}
