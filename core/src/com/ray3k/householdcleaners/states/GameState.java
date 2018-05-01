/*
 * The MIT License
 *
 * Copyright 2018 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ray3k.householdcleaners.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.ray3k.householdcleaners.AnimationDrawable;
import com.ray3k.householdcleaners.Core;
import com.ray3k.householdcleaners.Entity;
import com.ray3k.householdcleaners.EntityManager;
import com.ray3k.householdcleaners.InputManager;
import com.ray3k.householdcleaners.SpineTwoColorEntity;
import com.ray3k.householdcleaners.State;
import com.ray3k.householdcleaners.entities.BackgroundEntity;
import com.ray3k.householdcleaners.entities.MoneyEntity;
import com.ray3k.householdcleaners.entities.PreviewEntity;
import com.ray3k.householdcleaners.entities.VacuumEntity;
import com.ray3k.householdcleaners.entities.ZombieEntity;

public class GameState extends State {
    private static GameState instance;
    private int score;
    private static int highscore = 0;
    private OrthographicCamera gameCamera;
    private Viewport gameViewport;
    private InputManager inputManager;
    private Skin skin;
    private Stage stage;
    public static EntityManager entityManager;
    public static TextureAtlas spineAtlas;
    public static TwoColorPolygonBatch twoColorPolygonBatch;
    public static final float GAME_WIDTH = 800;
    public static final float GAME_HEIGHT = 600;
    private BackgroundEntity bg;
    private float zombieTimer;
    private Array<Entity> others;
    public static final int ROWS = 4;
    public static final int COLUMNS = 8;
    public static Entity[][] grid;
    public final static Vector3 tempCoords = new Vector3();
    private int money;
    private static final float MONEY_DELAY = 10.0f;
    private float moneyTimer;
    private static final float BUTTON_DELAY = 5.0f;
    private float buttonTimer;
    private Array<PreviewEntity.Type> timeline;
    public static float climaxTime;
    public static float difficulty;
    public static float ramping;
    private float zombieDelay;
    private static final float ZOMBIE_DELAY_MINIMUM = .3f;
    private static final float ZOMBIE_DELAY_INITIAL = 15.0f;
    private boolean playedHungry;
    
    public static GameState inst() {
        return instance;
    }
    
    public GameState(Core core) {
        super(core);
    }
    
    @Override
    public void start() {
        instance = this;
        
        spineAtlas = Core.assetManager.get(Core.DATA_PATH + "/spine/household-cleaners.atlas", TextureAtlas.class);
        
        score = 0;
        
        inputManager = new InputManager();
        
        gameCamera = new OrthographicCamera();
        gameCamera.position.set(0.0f, 0.0f, 0.0f);
        gameViewport = new StretchViewport(GAME_WIDTH, GAME_HEIGHT, gameCamera);
        gameViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        gameViewport.apply();
        
        skin = Core.assetManager.get(Core.DATA_PATH + "/ui/household-cleaners-ui.json", Skin.class);
        stage = new Stage(new ScreenViewport());
        
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputManager);
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
        
        entityManager = new EntityManager();
        
        twoColorPolygonBatch = new TwoColorPolygonBatch(3100);
        
        timeline = new Array<PreviewEntity.Type>();
        String text = Gdx.files.local(Core.DATA_PATH + "/data/timeline.data").readString();
        String[] split = text.split("\\r\\n");
        for (String string : split) {
            String[] typeStrings = string.split("\\|");
            timeline.add(PreviewEntity.Type.valueOf(typeStrings[MathUtils.random(typeStrings.length - 1)]));
        }
        
        createStageElements();
        
        bg = new BackgroundEntity();
        entityManager.addEntity(bg);
        
        for (int i = 0; i < ROWS; i++) {
            VacuumEntity vacuum = new VacuumEntity();
            vacuum.setRow(i);
            vacuum.setPosition(0.0f, i * 102.0f + 51.0f);
            vacuum.setDepth(10 * i);
            entityManager.addEntity(vacuum);
        }
        
        zombieDelay = ZOMBIE_DELAY_INITIAL;
        zombieTimer = zombieDelay;
        others = new Array<Entity>();
        
        grid = new Entity[COLUMNS][ROWS];
        
        money = 20;
        
        moneyTimer = MONEY_DELAY;
        buttonTimer = BUTTON_DELAY;
        playedHungry = false;
    }
    
    private void createStageElements() {
        skin.get("loading", AnimationDrawable.class).setSkin(skin);
        
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        Table table = new Table();
        table.setName("cards");
        table.pad(5.0f);
        table.defaults().space(2.0f);
        table.add();
        table.add();
        table.add();
        table.add();
        root.add(table).expandX().left();
        
        createButton();
        
        root.row();
        table = new Table();
        root.add(table).growX().expandY().bottom();
        
        Label label = new Label("$20", skin);
        label.setName("money");
        label.setColor(Color.GREEN);
        table.add(label).expand();
        
        label = new Label("Score: 0", skin);
        label.setName("score");
        table.add(label).expand();
    }
    
    private void createButton() {
        final Table table = stage.getRoot().findActor("cards");
        
        for (Cell cell : table.getCells()) {
            if (cell.getActor() != null) {
                ((Button) cell.getActor()).setDisabled(false);
            } else {
                final PreviewEntity.Type type = nextType();

                final Button button = new Button(skin, type.getJsonName());

                button.setDisabled(true);
                button.setUserObject(type);
                cell.setActor(button);

                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event,
                            Actor actor) {
                        if (getMoney() >= type.getPrice()) {
                            PreviewEntity preview = new PreviewEntity(type);
                            entityManager.addEntity(preview);
                            addMoney(-type.getPrice());
                            table.removeActor(button);
                            resetCards();
                        }
                    }
                });
                break;
            }
        }
    }
    
    private PreviewEntity.Type nextType() {
        PreviewEntity.Type returnValue;
        
        if (timeline.size > 0) {
            returnValue = timeline.removeIndex(0);
        } else {
            returnValue = (new Array<PreviewEntity.Type>(PreviewEntity.Type.values())).random();
        }
        
        return returnValue;
    }
    
    private void resetCards() {
        final Table table = stage.getRoot().findActor("cards");
        
        Array<Actor> actors = new Array<Actor>();
        
        for (Cell cell : table.getCells()) {
            if (cell.getActor() != null) {
                actors.add(cell.getActor());
                cell.setActor(null);
            }
        }
        
        for (Cell cell : table.getCells()) {
            if (actors.size > 0) {
                cell.setActor(actors.first());
                actors.removeIndex(0);
            } else {
                break;
            }
        }
    }
    
    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        Gdx.gl.glClearColor(71 / 255.0f, 71 / 255.0f, 71 / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        gameCamera.update();
        
        spriteBatch.setProjectionMatrix(gameCamera.combined);
        spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatch.begin();
        bg.draw(spriteBatch, delta);
        spriteBatch.end();
        
        twoColorPolygonBatch.setProjectionMatrix(gameCamera.combined);
        twoColorPolygonBatch.begin();
        twoColorPolygonBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        entityManager.draw(spriteBatch, delta);
        twoColorPolygonBatch.end();
        
        stage.draw();
    }

    @Override
    public void act(float delta) {
        tempCoords.x = Gdx.input.getX();
        tempCoords.y = Gdx.input.getY();
        GameState.inst().getGameCamera().unproject(tempCoords);
        
        int column = MathUtils.floor(tempCoords.x / 102.0f);
        int row = MathUtils.floor(tempCoords.y / 102.0f);
        
        if (column >= 0 && column < GameState.COLUMNS && row < GameState.ROWS && GameState.grid[column][row] != null && !GameState.grid[column][row].isDestroyed()) {
            if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
                GameState.grid[column][row].dispose();
                playSound("shovel");
            }
        }
        
        zombieTimer -= delta;
        if (zombieTimer < 0.0f) {
            zombieDelay -= zombieDelay * ramping * .1f / 100;
            System.out.println(zombieDelay);
            if (zombieDelay < 1.0f) {
                if (!playedHungry) {
                    playedHungry = true;
                    playSound("zombies");
                }
            }
            
            if (zombieDelay < 0.0f) {
                zombieDelay = 0.0f;
            }
            
            zombieTimer = zombieDelay * (100 - difficulty) / 100 + ZOMBIE_DELAY_MINIMUM;
            ZombieEntity zombie = new ZombieEntity();
            zombie.setRow(MathUtils.random(3));
            zombie.setPosition(GameState.GAME_WIDTH + 100.0f, zombie.getRow() * 102.0f + 51.0f);
            zombie.setMotion(50.0f, 180.0f);
            zombie.setDepth(10 * zombie.getRow());
            entityManager.addEntity(zombie);
        }
        
        moneyTimer -= delta;
        if (moneyTimer < 0.0f) {
            moneyTimer = MONEY_DELAY;
            
            MoneyEntity money = new MoneyEntity();
            money.setPosition(MathUtils.random(GAME_WIDTH), GAME_HEIGHT);
            money.setMotion(50.0f, 270.0f);
            money.setTarget(102.0f * MathUtils.random(ROWS - 1) + 51.0f);
            entityManager.addEntity(money);
        }
        
        buttonTimer -= delta;
        if (buttonTimer < 0.0f) {
            buttonTimer = BUTTON_DELAY;
            
            createButton();
        }
        
        entityManager.act(delta);
        
        System.out.println(entityManager.getEntities().size);
        
        others.clear();
        others.addAll(entityManager.getEntities());
        for (Entity entity : entityManager.getEntities()) {
            if (entity.isCheckingCollisions() && entity instanceof SpineTwoColorEntity) {
                for (Entity other : others) {
                    if (other.isCheckingCollisions() && other instanceof SpineTwoColorEntity) {
                        if (((SpineTwoColorEntity) entity).getSkeletonBounds().aabbIntersectsSkeleton(((SpineTwoColorEntity) other).getSkeletonBounds())) {
                            entity.collision(other);
                        }
                    }
                }
            }
        }
        
        stage.act(delta);
        
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Core.stateManager.loadState("menu");
        }
    }

    @Override
    public void dispose() {
        if (twoColorPolygonBatch != null) {
            twoColorPolygonBatch.dispose();
        }
    }

    @Override
    public void stop() {
        stage.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        if (score > highscore) {
            highscore = score;
        }
        
        Label label = stage.getRoot().findActor("score");
        label.setText("Score: " + Integer.toString(this.score));
    }
    
    public void addScore(int score) {
        this.score += score;
        if (this.score > highscore) {
            highscore = this.score;
        }
        
        Label label = stage.getRoot().findActor("score");
        label.setText("Score: " + Integer.toString(this.score));
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public void setGameCamera(OrthographicCamera gameCamera) {
        this.gameCamera = gameCamera;
    }

    public Skin getSkin() {
        return skin;
    }

    public Stage getStage() {
        return stage;
    }
    
    public void playSound(String name) {
        playSound(name, 1.0f, 1.0f);
    }
    
    public void playSound (String name, float volume) {
        playSound(name, volume, 1.0f);
    }
    
    /**
     * 
     * @param name
     * @param volume
     * @param pitch .5 to 2. 1 is default
     */
    public void playSound(String name, float volume, float pitch) {
        Core.assetManager.get(Core.DATA_PATH + "/sfx/" + name + ".wav", Sound.class).play(volume, pitch, 0.0f);
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
        
        Label label = stage.getRoot().findActor("money");
        label.setText("$" + Integer.toString(this.money));
    }
    
    public void addMoney(int money) {
        setMoney(getMoney() + money);
    }
}