package com.mygdx.game;

public class BlockModel {
    /**
     * x座標
     */
    private float x;
    /**
     * y座標
     */
    private float y;
    /**
     * 現在のブロック情報
     */
    private int block_value;

    /**
     * ブロックのインスタンスを作成する
     */
    BlockModel( float x, float y){
        this.x = x;
        this.y = y;
    }

    public void setValue(int block_value) {
        this.block_value = block_value;
    }

    public void removeValue() {
        this.block_value = 0;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getBlockValue() {
        return block_value;
    }
}
