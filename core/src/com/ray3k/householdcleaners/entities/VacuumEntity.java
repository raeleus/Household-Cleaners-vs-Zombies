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

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ray3k.householdcleaners.Core;
import com.ray3k.householdcleaners.Entity;
import com.ray3k.householdcleaners.SpineTwoColorEntity;
import com.ray3k.householdcleaners.states.GameState;

public class VacuumEntity extends SpineTwoColorEntity {
    private Mode mode;
    private int row;
    
    public static enum Mode {
        STATIONARY, ATTACKING
    }

    public VacuumEntity() {
        super(Core.DATA_PATH + "/spine/vacuum.json", "stand", GameState.twoColorPolygonBatch);
        mode = Mode.STATIONARY;
        setCheckingCollisions(true);
    }

    @Override
    public void actSub(float delta) {
        if (getX() > GameState.GAME_WIDTH + 100.0f) {
            dispose();
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
        if (other instanceof ZombieEntity) {
            ZombieEntity zombie = (ZombieEntity) other;
            if (zombie.getRow() == getRow()) {
                if (mode == Mode.STATIONARY) {
                    mode = Mode.ATTACKING;
                    getAnimationState().setAnimation(0, "attack", false);
                    setMotion(300.0f, 0.0f);
                    GameState.inst().playSound("vacuum");
                } else if (mode == Mode.ATTACKING) {
                    if (zombie.getMode() != ZombieEntity.Mode.DYING) {
                        zombie.kill();
                    }
                }
            }
        }
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

}
