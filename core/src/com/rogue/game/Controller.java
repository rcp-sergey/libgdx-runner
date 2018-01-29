package com.rogue.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.rogue.game.screens.ScreenManager;


/**
 * Created by SERGEY on 14.01.2018.
 */

public class Controller {
    private static final int LEFT_KEY = Input.Keys.A;
    private static final int RIGHT_KEY = Input.Keys.D;
    private static final int JUMP_KEY = Input.Keys.SPACE;
    private static final int FIRE_KEY = Input.Keys.L;
    private static final int PAUSE_KEY = Input.Keys.P;
    private static final int SAVE_KEY = Input.Keys.F8;
    private static final int LOAD_KEY = Input.Keys.F10;
    private FitViewport viewport;
    private Stage stage;
    private OrthographicCamera camera;
    private transient Skin skin;
    private transient BitmapFont font;
    private boolean leftPressed, rightPressed, jumpPressed, firePressed;
    private Button btnLeftMove, btnRightMove, btnJump, btnFire, btnMenu;

    public Controller(SpriteBatch batch) {
        camera = new OrthographicCamera();
        viewport = new FitViewport(ScreenManager.VIEW_WIDTH, ScreenManager.VIEW_HEIGHT, camera);
        stage = new Stage();
        stage = new Stage(viewport, batch);
        font = Assets.getInstance().getAssetManager().get("zorque24.ttf", BitmapFont.class);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        skin.add("font", font);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("btnHeal");
        textButtonStyle.font = font;
        skin.add("simpleBtn", textButtonStyle);

        btnLeftMove = new TextButton("<", skin, "simpleBtn");
        btnRightMove = new TextButton(">", skin, "simpleBtn");
        btnJump = new TextButton("^", skin, "simpleBtn");
        btnFire = new TextButton("F", skin, "simpleBtn");
        btnMenu = new TextButton("=", skin, "simpleBtn");

        stage.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case LEFT_KEY:
                        leftPressed = true;
                        break;
                    case RIGHT_KEY:
                        rightPressed = true;
                        break;
                    case JUMP_KEY:
                        jumpPressed = true;
                        break;
                    case FIRE_KEY:
                        firePressed = true;
                        break;
                }
                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch (keycode) {
                    case LEFT_KEY:
                        leftPressed = false;
                        break;
                    case RIGHT_KEY:
                        rightPressed = false;
                        break;
                    case JUMP_KEY:
                        jumpPressed = false;
                        break;
                    case FIRE_KEY:
                        firePressed = false;
                        break;
                }
                return true;
            }
        });

        setToInputProcessor();

        btnLeftMove.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });

        btnRightMove.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });


        btnJump.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                jumpPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                jumpPressed = false;
            }
        });

        btnFire.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                firePressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                firePressed = false;
            }
        });

        btnMenu.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        setBtnPositions();

        stage.addActor(btnLeftMove);
        stage.addActor(btnRightMove);
        stage.addActor(btnJump);
        stage.addActor(btnFire);
        stage.addActor(btnMenu);
    }

    private void setBtnPositions() {
        btnLeftMove.setPosition(50, 50);
        btnJump.setPosition(ScreenManager.VIEW_WIDTH - 250, 50);
        btnRightMove.setPosition(150, 50);
        btnFire.setPosition(ScreenManager.VIEW_WIDTH - 150, 50);
        btnMenu.setPosition(ScreenManager.VIEW_WIDTH - 100, ScreenManager.VIEW_HEIGHT - 100);
    }

    public boolean isSaveJustPressed() {
        if (Gdx.input.isKeyJustPressed(SAVE_KEY)) {
            return true;
        }
        return false;
    }

    public boolean isLoadJustPressed() {
        if (Gdx.input.isKeyJustPressed(LOAD_KEY)) {
            return true;
        }
        return false;
    }

    public boolean isPauseJustPressed() {
        if (Gdx.input.isKeyJustPressed(PAUSE_KEY) || btnMenu.getClickListener().isPressed()) {
            btnMenu.getClickListener().cancel();
            return true;
        }
        return false;
    }

    public void draw() {
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        setBtnPositions();
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isJumpPressed() {
        return jumpPressed;
    }

    public boolean isFirePressed() {
        return firePressed;
    }

    public void setToInputProcessor() {
        Gdx.input.setInputProcessor(stage);
    }
}
