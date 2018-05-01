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
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ray3k.householdcleaners.Core;
import com.ray3k.householdcleaners.EntityManager;
import com.ray3k.householdcleaners.State;

public class GameOverState extends State {
    private Stage stage;
    private Skin skin;
    private int score;
    private float time;
    private float bestTime;
    private int highScore;
    private final float MAX_TIME = 9999.0f;
    public static EntityManager entityManager;
    private OrthographicCamera gameCamera;
    private Viewport gameViewport;

    public GameOverState(Core core) {
        super(core);
        highScore = 0;
    }

    @Override
    public void start() {
        gameCamera = new OrthographicCamera();
        gameViewport = new ScreenViewport(gameCamera);
        gameViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        gameViewport.apply();
        
        gameCamera.position.set(gameCamera.viewportWidth / 2, gameCamera.viewportHeight / 2, 0);
        
        skin = Core.assetManager.get(Core.DATA_PATH + "/ui/household-cleaners-ui.json", Skin.class);
        
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        entityManager = new EntityManager();
        
        refreshTable();
    }

    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        Gdx.gl.glClearColor(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        gameCamera.update();
        
        stage.draw();
    }

    @Override
    public void act(float delta) {
        entityManager.act(delta);
        
        stage.act(delta);
        
        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            Core.stateManager.loadState("menu");
        }
    }

    @Override
    public void stop() {
        stage.dispose();
    }

    @Override
    public void dispose() {
        
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
        if (score > highScore) {
            highScore = score;
        }
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
        if (time < bestTime) {
            bestTime = time;
        }
    }
    
    private void refreshTable() {
        stage.clear();
        
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        Label label = new Label("Game Over", skin);
        root.add(label).colspan(2);
        
        root.row();
        label = new Label("Score\n\n" + score, skin, "title");
        label.setAlignment(Align.center);
        root.add(label).pad(20.0f);
        
        if (!MathUtils.isEqual(bestTime, MAX_TIME)) {
            root.row();
            label = new Label("High Score\n\n" + highScore, skin, "title");
            label.setAlignment(Align.center);
            root.add(label).pad(20.0f);
        }
        
        root.row();
        label = new Label("Press space\nto return to menu!", skin, "title");
        label.setAlignment(Align.center);
        root.add(label);
    }
}
