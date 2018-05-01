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
import com.ray3k.householdcleaners.Maths;
import com.ray3k.householdcleaners.SpineTwoColorEntity;
import com.ray3k.householdcleaners.states.GameState;

public class MopEntity extends SpineTwoColorEntity implements Healthy {
    private int row;
    private Mode mode;
    private float health;
    private static final float MAX_HEALTH = 200.0f;
    private ZombieEntity target;
    private static final float DAMAGE = 100.0f;
    private float startX;
    private int uses;

    public MopEntity() {
        super(Core.DATA_PATH + "/spine/mop.json", "stand", GameState.twoColorPolygonBatch);
        setCheckingCollisions(true);
        setMode(Mode.STATIONARY);
        
        getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (entry.getAnimation().getName().equals("hurt")) {
                    setMode(Mode.STATIONARY);
                } else if (entry.getAnimation().getName().equals("die")) {
                    MopEntity.this.dispose();
                } else if (entry.getAnimation().getName().equals("attack")) {
                    if (--uses <= 0) {
                        setMode(Mode.DYING);
                    } else {
                        setMode(Mode.STATIONARY);
                    }
                } 
            }
            
            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
                if (event.getData().getName().equals("attack")) {
                    boolean makeSound = false;
                    for (Entity entity : GameState.entityManager.getEntities()) {
                        if (entity instanceof ZombieEntity) {
                            ZombieEntity zombie = (ZombieEntity) entity;
                            if (zombie.getMode() != Mode.DYING && zombie.getRow() == row) {
                                if (zombie.getX() - getX() < 25.0f && zombie.getX() - getX() > -25.0f) {
                                    zombie.hurt(DAMAGE);
                                    makeSound = true;
                                }
                            }
                        }
                    }
                    
                    if (makeSound) {
                        GameState.inst().playSound("squish");
                    }
                }
            }
        });
        
        health = MAX_HEALTH;
        uses = 3;
    }

    @Override
    public void actSub(float delta) {
        if (mode == Mode.STATIONARY) {
            setX(Maths.approach(getX(), startX, 100.0f * delta));
            
            Mode newMode = Mode.STATIONARY;
            for (Entity entity : GameState.entityManager.getEntities()) {
                if (entity instanceof ZombieEntity) {
                    ZombieEntity zombie = (ZombieEntity) entity;
                    if (zombie.getMode() != Mode.DYING && zombie.getRow() == row) {
                        if (zombie.getX() - getX() < 150.0f) {
                            newMode = Mode.ATTACKING;
                            target = zombie;
                            startX = getX();
                            break;
                        }
                    }
                }
            }

            if (newMode != mode) {
                setMode(newMode);
            }
        } else if (mode == Mode.ATTACKING) {
            setX(Maths.approach(getX(), target.getX(), 100.0f * delta));
        }
    }

    @Override
    public void drawSub(SpriteBatch spriteBatch, float delta) {
    }

    @Override
    public void create() {
        startX = getX();
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
//                    zombie.setMode(ZombieEntity.Mode.ATTACKING);
//                    zombie.setTarget(this);
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
            } else if (mode == Mode.ATTACKING) {
                getAnimationState().setAnimation(0, "attack", false);
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
