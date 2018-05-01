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
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.householdcleaners.Core;
import com.ray3k.householdcleaners.State;

public class MenuState extends State {
    private Stage stage;
    private Skin skin;
    private Table root;

    public MenuState(Core core) {
        super(core);
    }
    
    @Override
    public void start() {
        skin = Core.assetManager.get(Core.DATA_PATH + "/ui/household-cleaners-ui.json", Skin.class);
        stage = new Stage(new ScreenViewport());
        
        Gdx.input.setInputProcessor(stage);
        
        createMenu();
    }
    
    private void createMenu() {
        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        Image image = new Image(skin, "logo");
        image.setScaling(Scaling.fit);
        root.add(image);
        
        root.row();
        Table table = new Table();
        table.setBackground(skin.getDrawable("grave"));
        root.add(table).growY().width(475.0f);
        
        table.defaults().space(10.0f);
        TextButton textButton = new TextButton("Play", skin);
        table.add(textButton);
        
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Core.assetManager.get(Core.DATA_PATH + "/sfx/pop.wav", Sound.class).play(1.0f);
                showDialog();
            }
        });
        
        table.row();
        textButton = new TextButton("Quit", skin);
        table.add(textButton);
        
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }
    
    private void showDialog() {
        Dialog dialog = new Dialog("", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    Core.assetManager.get(Core.DATA_PATH + "/sfx/pop.wav", Sound.class).play(1.0f);
                    Core.stateManager.loadState("game");
                }
            }
        };
        
        Label label = new Label("Difficulty", skin);
        dialog.getContentTable().add(label);
        
        dialog.getContentTable().row();
        Slider slider = new Slider(0.0f, 100.0f, 1.0f, false, skin);
        slider.setValue(GameState.difficulty);
        dialog.getContentTable().add(slider).growX();
        
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                GameState.difficulty = ((Slider) actor).getValue();
            }
        });
        
        dialog.getContentTable().row();
        label = new Label("Ramping", skin);
        dialog.getContentTable().add(label);
        
        dialog.getContentTable().row();
        slider = new Slider(10f, 100.0f, 1.0f, false, skin);
        slider.setValue(GameState.ramping);
        dialog.getContentTable().add(slider).growX();
        
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                GameState.ramping = ((Slider) actor).getValue();
            }
        });
        
        dialog.button("OK", true).button("Cancel", false);
        
        dialog.key(Keys.ESCAPE, false).key(Keys.ENTER, true);
        
        dialog.show(stage);
    }
    
    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        Gdx.gl.glClearColor(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void act(float delta) {
        stage.act(delta);
    }

    @Override
    public void dispose() {
        
    }

    @Override
    public void stop() {
        stage.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}