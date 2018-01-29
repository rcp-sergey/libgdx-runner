package com.rogue.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.rogue.game.screens.GameScreen;

import java.io.Serializable;

public class Hero extends BaseUnit implements Serializable {
    private int lifes;
    private int coins;
    private int score;
    private StringBuilder guiStringHelper;

    public void addCoins(int amount) {
        coins += amount;
    }

    public void addScore(int amount) {
        score += amount;
    }

    public Hero(GameScreen gameScreen, Map map, float x, float y) {
        super(gameScreen, map, 100, 360.0f, 0.4f, x, y, 100, 100);
        this.coins = 0;
        this.lifes = 5;
        this.score = 0;
        this.guiStringHelper = new StringBuilder(150);
        this.type = Type.Knight;
        this.afterLoad(gameScreen);
    }

    @Override
    public void destroy() {
        lifes--;
        hp = maxHp;
    }

    @Override
    public void update(float dt) {
        if (gameScreen.getController().isRightPressed()) {
            moveRight();
        }
        if (gameScreen.getController().isLeftPressed()) {
            moveLeft();
        }
        if (gameScreen.getController().isJumpPressed()) {
            jump();
        }
        if (gameScreen.getController().isFirePressed()) {
            fire(dt, true);
        }
        super.update(dt);
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        guiStringHelper.setLength(0);
        guiStringHelper.append("SCORE: ").append(score).append("\nHP: ").append(hp).append(" / ").append(maxHp).append(" x").append(lifes).append("\nCoins: ").append(coins);
        font.draw(batch, guiStringHelper, 20, 700);
    }
}
