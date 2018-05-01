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
import com.esotericsoftware.spine.Event;
import com.ray3k.householdcleaners.Core;
import com.ray3k.householdcleaners.Entity;
import com.ray3k.householdcleaners.SpineTwoColorEntity;
import com.ray3k.householdcleaners.states.GameState;

public class ZombieEntity extends SpineTwoColorEntity implements Healthy {
    private float health;
    private static final float MAX_HEALTH = 100.0f;
    private static final float ATTACK_DAMAGE =  25.0f;
    
    private Mode mode;
    private int row;
    private Healthy target;

    public ZombieEntity() {
        super(Core.DATA_PATH + "/spine/zombie.json", "walk", GameState.twoColorPolygonBatch);
        getAnimationState().getCurrent(0).setLoop(true);
        setCheckingCollisions(true);
        mode = Mode.WALKING;
        health = MAX_HEALTH;
        
        getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (entry.getAnimation().getName().equals("die")) {
                    ZombieEntity.this.dispose();
                }
            }

            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
                if (event.getData().getName().equals("attack")) {
                    if (target != null) {
                        target.setMode(Mode.HURT);
                        target.hurt(ATTACK_DAMAGE);
                        if (target.getHealth() <= 0) {
                            setMode(Mode.WALKING);
                        }
                    }
                } else if (event.getData().getName().equals("chew")) {
                    GameState.inst().playSound("chew");
                } else if (event.getData().getName().equals("die")) {
                    GameState.inst().playSound("die");
                }
            }
        });
    }

    @Override
    public void actSub(float delta) {
        if (getX() < -100.0f) {
            GameState.entityManager.addEntity(new GameOverTimerEntity(2.0f));
            GameState.inst().playSound("no");
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
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(Mode mode) {
        if (this.mode != mode && this.mode != Mode.DYING) {
            this.mode = mode;
            if (mode == Mode.DYING) {
                getAnimationState().setAnimation(0, "die", false);
                setMotion(0.0f, 0.0f);
            } else if (mode == Mode.ATTACKING) {
                getAnimationState().setAnimation(0, "hit", true);
                setMotion(0.0f, 0.0f);
            } else if (mode == Mode.WALKING) {
                getAnimationState().setAnimation(0, "walk", true);
                setMotion(50.0f, 180.0f);
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
    public void hurt(float damage) {
        getAnimationState().setAnimation(1, "hurt", false);
        health -= damage;
        if (health <= 0 && mode != Mode.DYING) {
            setMode(Mode.DYING);
            GameState.inst().addScore(1);
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
        health = MAX_HEALTH;
    }

    @Override
    public float getHealth() {
        return health;
    }

    public Healthy getTarget() {
        return target;
    }

    public void setTarget(Healthy target) {
        this.target = target;
    }
}