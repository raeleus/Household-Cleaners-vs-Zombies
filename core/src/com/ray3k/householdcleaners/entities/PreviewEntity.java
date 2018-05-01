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

package com.ray3k.householdcleaners.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.ray3k.householdcleaners.Core;
import com.ray3k.householdcleaners.Entity;
import com.ray3k.householdcleaners.SpineTwoColorEntity;
import com.ray3k.householdcleaners.states.GameState;
import static com.ray3k.householdcleaners.states.GameState.entityManager;

public class PreviewEntity extends SpineTwoColorEntity {
    
    public static enum Type {
        SPRAY("spray", 50), SIGN("sign", 20), RECYCLING("recycling", 10), MOP("mop", 30), GLOVE("glove", 10), GLASS_CLEANER("glass-cleaner", 20), BROOM("broom", 50), BLEACH("bleach", 10);
        
        private final String jsonName;
        private final int price;
        
        Type(String jsonName, int price) {
            this.jsonName = jsonName;
            this.price = price;
        }

        public String getJsonName() {
            return jsonName;
        }

        public int getPrice() {
            return price;
        }
    }
    
    private Type type;
    
    public PreviewEntity(Type type) {
        super(Core.DATA_PATH + "/spine/" + type.jsonName + ".json", "select", GameState.twoColorPolygonBatch);
        this.type = type;
    }
    
    @Override
    public void actSub(float delta) {
        GameState.tempCoords.x = Gdx.input.getX();
        GameState.tempCoords.y = Gdx.input.getY();
        GameState.inst().getGameCamera().unproject(GameState.tempCoords);
        
        int column = MathUtils.floor(GameState.tempCoords.x / 102.0f);
        float x = column * 102.0f;
        x = MathUtils.clamp(x, 0.0f, 102.0f * (GameState.COLUMNS - 1)) + 51.0f;
        
        int row = MathUtils.floor(GameState.tempCoords.y / 102.0f);
        float y = row * 102.0f;
        y = MathUtils.clamp(y, 0.0f, 102.0f * (GameState.ROWS - 1)) + 51.0f;
        setPosition(x, y);
        
        if (column < GameState.COLUMNS && row < GameState.ROWS && (GameState.grid[column][row] == null || GameState.grid[column][row].isDestroyed())) {
            if (!getAnimationState().getCurrent(0).getAnimation().getName().equals("select")) {
                getAnimationState().setAnimation(0, "select", false);
            }
            
            if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
                dispose();
                
                GameState.inst().playSound("plant");

                switch (type) {
                    case BLEACH:
                        BleachEntity bleachEntity = new BleachEntity();
                        bleachEntity.setRow(row);
                        bleachEntity.setPosition(x, y);
                        bleachEntity.setDepth(10 * row);
                        entityManager.addEntity(bleachEntity);
                        GameState.grid[column][row] = bleachEntity;
                        break;
                    case BROOM:
                        BroomEntity broomEntity = new BroomEntity();
                        broomEntity.setRow(row);
                        broomEntity.setPosition(x, y);
                        broomEntity.setDepth(10 * row);
                        entityManager.addEntity(broomEntity);
                        GameState.grid[column][row] = broomEntity;
                        break;
                    case GLASS_CLEANER:
                        GlassCleanerEntity glassCleanerEntity = new GlassCleanerEntity();
                        glassCleanerEntity.setRow(row);
                        glassCleanerEntity.setPosition(x, y);
                        glassCleanerEntity.setDepth(10 * row);
                        entityManager.addEntity(glassCleanerEntity);
                        GameState.grid[column][row] = glassCleanerEntity;
                        break;
                    case GLOVE:
                        GloveEntity gloveEntity = new GloveEntity();
                        gloveEntity.setRow(row);
                        gloveEntity.setPosition(x, y);
                        gloveEntity.setDepth(10 * row);
                        entityManager.addEntity(gloveEntity);
                        GameState.grid[column][row] = gloveEntity;
                        break;
                    case MOP:
                        MopEntity mopEntity = new MopEntity();
                        mopEntity.setRow(row);
                        mopEntity.setPosition(x, y);
                        mopEntity.setDepth(10 * row);
                        entityManager.addEntity(mopEntity);
                        GameState.grid[column][row] = mopEntity;
                        break;
                    case RECYCLING:
                        RecyclingEntity recyclingEntity = new RecyclingEntity();
                        recyclingEntity.setRow(row);
                        recyclingEntity.setPosition(x, y);
                        recyclingEntity.setDepth(10 * row);
                        entityManager.addEntity(recyclingEntity);
                        GameState.grid[column][row] = recyclingEntity;
                        break;
                    case SIGN:
                        SignEntity signEntity = new SignEntity();
                        signEntity.setRow(row);
                        signEntity.setPosition(x, y);
                        signEntity.setDepth(10 * row);
                        entityManager.addEntity(signEntity);
                        GameState.grid[column][row] = signEntity;
                        break;
                    case SPRAY:
                        SprayEntity sprayEntity = new SprayEntity();
                        sprayEntity.setRow(row);
                        sprayEntity.setPosition(x, y);
                        sprayEntity.setDepth(10 * row);
                        entityManager.addEntity(sprayEntity);
                        GameState.grid[column][row] = sprayEntity;
                        break;
                }
            }
        } else if (!getAnimationState().getCurrent(0).getAnimation().getName().equals("cancel")) {
            getAnimationState().setAnimation(0, "cancel", false);
        }
    }

    @Override
    public void drawSub(SpriteBatch spriteBatch, float delta) {
    }

    @Override
    public void create() {
    }

    @Override
    public void actEnd(float delta) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void collision(Entity other) {
    }
}
