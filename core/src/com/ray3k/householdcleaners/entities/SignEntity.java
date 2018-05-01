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
import com.esotericsoftware.spine.AnimationState;
import com.ray3k.householdcleaners.Core;
import com.ray3k.householdcleaners.Entity;
import com.ray3k.householdcleaners.SpineTwoColorEntity;
import com.ray3k.householdcleaners.states.GameState;

public class SignEntity extends SpineTwoColorEntity implements Healthy {
    private int row;
    private Mode mode;
    private float health;
    private static final float MAX_HEALTH = 100.0f;
    private ZombieEntity target;
    private static final float DAMAGE_DELAY = .5f;
    private float damageTimer;
    private static final float DAMAGE = 5.0f;

    public SignEntity() {
        super(Core.DATA_PATH + "/spine/sign.json", "stand", GameState.twoColorPolygonBatch);
        setCheckingCollisions(true);
        setMode(Mode.STATIONARY);
        
        getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (entry.getAnimation().getName().equals("hurt")) {
                    setMode(Mode.STATIONARY);
                } else if (entry.getAnimation().getName().equals("die")) {
                    SignEntity.this.dispose();
                }
            }
        });
        
        health = MAX_HEALTH;
    }

    @Override
    public void actSub(float delta) {
        damageTimer -= delta;
        if (damageTimer <= 0) {
            damageTimer = DAMAGE_DELAY;
            
            if (target != null && !target.isDestroyed() && target.getMode() != Mode.DYING) {
                target.hurt(DAMAGE);
                GameState.inst().playSound("punch");
                hurt(2.0f);
            }
        }
        target = null;
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
        if (mode != Mode.DYING) {
            if (other instanceof ZombieEntity) {
                ZombieEntity zombie = (ZombieEntity) other;
                if (zombie.getRow() == getRow()) {
                    target = zombie;
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

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(Mode mode) {
        if (this.mode != Mode.DYING) {
            this.mode = mode;

            if (mode == Mode.HURT) {
                getAnimationState().setAnimation(0, "hurt", false);
            } else if (mode == Mode.STATIONARY) {
                getAnimationState().addAnimation(0, "stand", true, 0.0f);
            } else if (mode == Mode.DYING) {
                getAnimationState().setAnimation(0, "die", false);
            }
        }
    }

    @Override
    public void hurt(float damage) {
        if (mode != Mode.DYING) {
            health -= damage;

            if (health <= 0) {
                setMode(Mode.DYING);
            }
        }
    }

    @Override
    public void kill() {
        hurt(getHealth());
    }

    @Override
    public void heal(float health) {
        this.health += health;
    }

    @Override
    public void heal() {
        heal(MAX_HEALTH);
    }

    @Override
    public float getHealth() {
        return health;
    }
}
