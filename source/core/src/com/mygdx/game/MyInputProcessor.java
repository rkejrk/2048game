package com.mygdx.game;

import static com.mygdx.game.MyGdxGame.consts;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class MyInputProcessor implements InputProcessor {
    MyGdxGame game;

    MyInputProcessor(MyGdxGame game){
        this.game = game;
    }

    @Override
    public boolean keyDown (int keycode) {
//		ゲーム中でなければゲームスタート
        if(game.game_state != consts.GAME_PLAYING){
            game.game_state = consts.GAME_PLAYING;
            return true;
        }

        game.block_generator_flag = 1;
        if(keycode == Input.Keys.UP){
            game.upBlocks();
        } else if(keycode == Input.Keys.DOWN){
            game.downBlocks();
        } else if(keycode == Input.Keys.RIGHT){
            game.rightBlocks();
        } else if(keycode == Input.Keys.LEFT){
            game.leftBlocks();
        } else {
//            上下左右以外は新規作成しない
            game.block_generator_flag = 0;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

}
