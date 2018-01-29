package com.rogue.game.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.rogue.game.Assets;
import com.rogue.game.Bullet;
import com.rogue.game.BulletEmitter;
import com.rogue.game.Controller;
import com.rogue.game.Hero;
import com.rogue.game.Map;
import com.rogue.game.Monster;
import com.rogue.game.MonsterEmitter;
import com.rogue.game.PowerUp;
import com.rogue.game.PowerUpsEmitter;
import com.rogue.game.TrashEmitter;

import org.w3c.dom.css.Rect;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// Делаем большую карту+
// Не даем камере смотреть за пределы карты+
// Засунуть задний фон в атлас
// Управление для смартфона в виде кнопок
// Подкорректировать GUI, чтобы он не был привязан к камере+
// Коллизи пуль с монстрами+
// МонстрЕмиттер+
// Магические числа, избавляемся+

public class GameScreen implements Screen {
    private transient SpriteBatch batch;
    private Map map;
    private Hero hero;
    private transient BitmapFont font;
    private PowerUpsEmitter powerUpsEmitter;
    private BulletEmitter bulletEmitter;
    private transient Sound soundTakeMoney;
    private transient Music mainTheme;
    private transient ShapeRenderer shapeRenderer;
    private MonsterEmitter monsterEmitter;
    private final static boolean DEBUG_MODE = true;
    private transient Camera camera;
    private transient Camera screenCamera;
    private TrashEmitter trashEmitter;
    private transient Texture texture;
    private boolean paused;
    private Rectangle activeRect;
    private Controller controller;
    private Window pauseWindow;
    private Stage pauseStage;

    private boolean isAndroidVersion = true;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    public Map getMap() {
        return map;
    }

    public Hero getHero() {
        return hero;
    }

    public BulletEmitter getBulletEmitter() {
        return bulletEmitter;
    }

    public Controller getController() {
        return controller;
    }

    @Override
    public void show() {
        TextureAtlas atlas = Assets.getInstance().getAtlas();
        Gdx.input.setInputProcessor(null);

        map = new Map(64);
        map.generateMap();
        hero = new Hero(this, map, 300, 300);
        monsterEmitter = new MonsterEmitter(this, 15, 0.5f);
        for (int i = 0; i < 15; i++) {
            monsterEmitter.createMonster(MathUtils.random(0, map.getEndOfWorldX()), 500);
        }
        trashEmitter = new TrashEmitter(this, atlas.findRegion("asteroid64"), 20);
        powerUpsEmitter = new PowerUpsEmitter(atlas.findRegion("money"));
        bulletEmitter = new BulletEmitter(atlas.findRegion("bullet48"), 0);
        afterLoad();
    }

    public void afterLoad() {
        camera = new OrthographicCamera(ScreenManager.VIEW_WIDTH, ScreenManager.VIEW_HEIGHT);
        screenCamera = new OrthographicCamera(ScreenManager.VIEW_WIDTH, ScreenManager.VIEW_HEIGHT);
        screenCamera.position.set(ScreenManager.VIEW_WIDTH / 2, ScreenManager.VIEW_HEIGHT / 2, 0);
        screenCamera.update();
        font = Assets.getInstance().getAssetManager().get("zorque24.ttf", BitmapFont.class);
        activeRect = new Rectangle(0, 0, 1280, 720);
        texture = new Texture("bg.jpg");
        mainTheme = Gdx.audio.newMusic(Gdx.files.internal("Jumping bat.wav"));
        mainTheme.setLooping(true);
//        mainTheme.play();
//        soundTakeMoney = Gdx.audio.newSound(Gdx.files.internal("takeMoney.wav"));
        if (DEBUG_MODE) {
            shapeRenderer = new ShapeRenderer();
            shapeRenderer.setAutoShapeType(true);
        }
        Assets.getInstance().loadAssets(ScreenManager.ScreenType.GAME);
        createPauseWindow();
        controller = new Controller(batch);
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            isAndroidVersion = true;
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.setProjectionMatrix(screenCamera.combined);
        batch.draw(texture, 0, 0);
        batch.setProjectionMatrix(camera.combined);
        map.render(batch);
        hero.render(batch);
        monsterEmitter.render(batch);
        trashEmitter.render(batch);
        powerUpsEmitter.render(batch);
        bulletEmitter.render(batch);
        batch.setProjectionMatrix(screenCamera.combined);
        hero.renderGUI(batch, font);
        batch.end();
        if (isAndroidVersion) {
            controller.draw();
        }
        if (paused) {
            pauseStage.draw();
        }
        if (DEBUG_MODE) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin();
            shapeRenderer.rect(hero.getHitArea().x, hero.getHitArea().y, hero.getHitArea().width, hero.getHitArea().height);
            shapeRenderer.end();
        }
    }

    public void loadGame() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(Gdx.files.local("mydata.sav").file()));
            hero = (Hero) ois.readObject();
            map = (Map) ois.readObject();
            monsterEmitter = (MonsterEmitter) ois.readObject();
            hero.afterLoad(this);
            map.afterLoad();
            monsterEmitter.afterLoad(this);
            if (pauseWindow == null)    createPauseWindow();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveGame() {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(Gdx.files.local("mydata.sav").file()));
            oos.writeObject(hero);
            oos.writeObject(map);
            oos.writeObject(monsterEmitter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(float dt) {
        if (controller.isPauseJustPressed()) {
            paused = !paused;
            if (paused) {
                Gdx.input.setInputProcessor(pauseWindow.getStage());
            } else controller.setToInputProcessor();
        }
        if (controller.isSaveJustPressed()) {
            saveGame();
        }
        if (controller.isLoadJustPressed()) {
            loadGame();
        }
        if (!paused) {
            map.update(dt);
            hero.setBlock();
            monsterEmitter.setBlocks();
            hero.update(dt);
            updateHeroCamera();
            monsterEmitter.update(activeRect, dt);
            bulletEmitter.update(dt);
            powerUpsEmitter.update(dt);
            trashEmitter.update(dt);
            checkCollisions();
            bulletEmitter.checkPool();
        }
    }

    private void createPauseWindow() {
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        skin.add("font32", font);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = skin.getFont("font32");
        textButtonStyle.fontColor = Color.YELLOW;
        textButtonStyle.up = skin.getDrawable("menuBtn");
        skin.add("textButtonStyle", textButtonStyle);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = skin.getFont("font32");
        skin.add("windowStyle", windowStyle);

        pauseWindow = new Window("[ PAUSE MODE ]", skin, "windowStyle");
        pauseWindow.getTitleLabel().setAlignment(1);
        pauseWindow.padTop(50);
        pauseWindow.setResizable(false);
        pauseWindow.setMovable(false);
        pauseWindow.setSize(ScreenManager.VIEW_WIDTH / 4, ScreenManager.VIEW_HEIGHT / 2);
        pauseWindow.setPosition(ScreenManager.VIEW_WIDTH / 3, ScreenManager.VIEW_HEIGHT / 3);
        pauseWindow.setModal(true);

        final TextButton continueBtn = new TextButton("CONTINUE", skin, "textButtonStyle");
        continueBtn.getLabelCell().padBottom(30);
        continueBtn.padTop(30);
        continueBtn.center();
        continueBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                paused = false;
                controller.setToInputProcessor();
                return true;
            }
        });

        TextButton exitBtn = new TextButton("EXIT GAME", skin, "textButtonStyle");
        exitBtn.getLabelCell().padBottom(30);
        exitBtn.padTop(30);
        exitBtn.center();
        exitBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                return true;
            }
        });

        VerticalGroup verticalGroup = new VerticalGroup();
        verticalGroup.addActor(continueBtn);
        verticalGroup.addActor(exitBtn);
        verticalGroup.expand();
        pauseWindow.add(verticalGroup);
        pauseWindow.pack();
        pauseStage = new Stage(ScreenManager.getInstance().getViewport());
        pauseStage.addActor(pauseWindow);
    }

    public void checkCollisions() {
        for (int i = 0; i < trashEmitter.getTrash().length; i++) {
            if (hero.getHitArea().overlaps(trashEmitter.getTrash()[i].getHitArea())) {
                trashEmitter.recreateTrash(i);
                hero.takeDamage(5);
            }
        }
        for (int i = 0; i < powerUpsEmitter.getPowerUps().length; i++) {
            PowerUp p = powerUpsEmitter.getPowerUps()[i];
            if (p.isActive() && hero.getHitArea().contains(p.getPosition())) {
                p.use(hero);
                p.deactivate();
//                soundTakeMoney.play();
            }
        }
        for (int i = 0; i < bulletEmitter.getActiveList().size(); i++) {
            Bullet b = bulletEmitter.getActiveList().get(i);
            if (!map.checkSpaceIsEmpty(b.getPosition().x, b.getPosition().y)) {
                b.deactivate();
                continue;
            }
            if (b.isPlayersBullet()) {
                for (int j = 0; j < monsterEmitter.getMonsters().length; j++) {
                    Monster m = monsterEmitter.getMonsters()[j];
                    if (m.isActive()) {
                        if (m.getHitArea().contains(b.getPosition())) {
                            b.deactivate();
                            if (m.takeDamage(25)) {
                                powerUpsEmitter.tryToCreatePowerUp(m.getCenterX(), m.getCenterY(), 0.5f);
                                hero.addScore(100);
                            }
                            break;
                        }
                    }
                }
            }
            if (!b.isPlayersBullet()) {
                if (hero.getHitArea().contains(b.getPosition())) {
                    b.deactivate();
                    hero.takeDamage(10);
                    break;
                }
            }
        }

    }

    public void updateHeroCamera() {
        camera.position.set(hero.getCenterX(), hero.getCenterY(), 0);
        if (camera.position.y < ScreenManager.VIEW_HEIGHT / 2) {
            camera.position.y = ScreenManager.VIEW_HEIGHT / 2;
        }
        if (camera.position.x < ScreenManager.VIEW_WIDTH / 2) {
            camera.position.x = ScreenManager.VIEW_WIDTH / 2;
        }
        if (camera.position.x > map.getEndOfWorldX() - ScreenManager.VIEW_WIDTH / 2) {
            camera.position.x = map.getEndOfWorldX() - ScreenManager.VIEW_WIDTH / 2;
        }
        camera.update();
        activeRect.setPosition(camera.position.x - 640, camera.position.y - 360);
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.getInstance().onResize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        mainTheme.dispose();
        if (DEBUG_MODE) {
            shapeRenderer.dispose();
        }
    }
}
