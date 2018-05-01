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
import com.ray3k.householdcleaners.Core;
import com.ray3k.householdcleaners.Entity;
import com.ray3k.householdcleaners.SpineTwoColorEntity;
import com.ray3k.householdcleaners.states.GameState;

public class MoneyEntity extends SpineTwoColorEntity {
    private float targetY;
    
    public MoneyEntity() {
        super(Core.DATA_PATH + "/spine/money.json", "animation", GameState.twoColorPolygonBatch);
        setDepth(-100);
    }
    
    @Override
    public void actSub(float delta) {
        if (getX() < 0.0f) {
            setX(0.0f);
        } else if (getX() > GameState.GAME_WIDTH) {
            setX(GameState.GAME_WIDTH);
        }
        
        if (getY() < targetY) {
            setY(targetY);
            setMotion(0.0f, 0.0f);
        }
        
        if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
            GameState.tempCoords.x = Gdx.input.getX();
            GameState.tempCoords.y = Gdx.input.getY();
            GameState.inst().getGameCamera().unproject(GameState.tempCoords);
            
            if (getSkeletonBounds().aabbContainsPoint(GameState.tempCoords.x, GameState.tempCoords.y)) {
                dispose();
                GameState.inst().addMoney(10);
                GameState.inst().playSound("money");
            }
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

    public void setTarget(float y) {
        targetY = y;
    }
}
