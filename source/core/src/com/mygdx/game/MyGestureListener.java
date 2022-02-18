package com.mygdx.game;

import static com.mygdx.game.MyGdxGame.consts;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

public class MyGestureListener implements GestureDetector.GestureListener {
    MyGdxGame game;

    MyGestureListener(MyGdxGame game){
        this.game = game;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
//		ゲーム中でなければゲームスタート
        if(game.game_state != consts.GAME_PLAYING){
            game.game_state = consts.GAME_PLAYING;
            return true;
        }
        return true;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        game.block_generator_flag = 1;
        if(velocityY < -2000){
            game.upBlocks();
        } else if(velocityY > 2000){
            game.downBlocks();
        } else if(velocityX > 2000){
            game.rightBlocks();
        } else if(velocityX < -2000){
            game.leftBlocks();
        } else {
//            上下左右以外は新規作成しない
            game.block_generator_flag = 0;
        }
//		方向キーの場合新しいブロックを作成する
        return true;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom (float originalDistance, float currentDistance){

        return false;
    }

    @Override
    public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer){
        return false;
    }

    @Override
    public void pinchStop() {
    }
}
