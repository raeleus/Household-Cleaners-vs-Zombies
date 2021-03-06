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
import com.esotericsoftware.spine.attachments.PointAttachment;
import com.ray3k.householdcleaners.Core;
import com.ray3k.householdcleaners.Entity;
import com.ray3k.householdcleaners.SpineTwoColorEntity;
import com.ray3k.householdcleaners.states.GameState;

public class SprayEntity extends SpineTwoColorEntity implements Healthy {
    private int row;
    private Mode mode;
    private float health;
    private static final float MAX_HEALTH = 100.0f;
    private static final float DAMAGE = 12.0f;
    private static final float FIRING_RATE = .65f;
    private float firingTimer;

    public SprayEntity() {
        super(Core.DATA_PATH + "/spine/spray.json", "stand", GameState.twoColorPolygonBatch);
        setCheckingCollisions(true);
        setMode(Mode.STATIONARY);
        
        getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (entry.getAnimation().getName().equals("hurt")) {
                    setMode(Mode.STATIONARY);
                } else if (entry.getAnimation().getName().equals("die")) {
                    SprayEntity.this.dispose();
                } else if (entry.getAnimation().getName().equals("attack")) {
                    setMode(Mode.STATIONARY);
                }
            }
        });
        
        health = MAX_HEALTH;
        firingTimer = FIRING_RATE;
    }

    @Override
    public void actSub(float delta) {
        firingTimer -= delta;
        if (firingTimer < 0) {
            firingTimer = FIRING_RATE;
            
            boolean fire = false;
            
            for (Entity entity : GameState.entityManager.getEntities()) {
                if (entity instanceof ZombieEntity) {
                    ZombieEntity zombieEntity = (ZombieEntity) entity;
                    if (zombieEntity.getRow() == row && zombieEntity.getX() < 700) {
                        fire = true;
                        break;
                    }
                }
            }
            
            if (fire) {
                GameState.inst().playSound("pop");
                PointAttachment point = (PointAttachment) getSkeleton().findSlot("point").getAttachment();

                BulletEntity bullet = new BulletEntity();
                bullet.setDamage(DAMAGE);
                bullet.setPosition(getX() + point.getX(), getY() + point.getY());
                bullet.setMotion(250.0f, 0.0f);
                bullet.setRow(row);
                GameState.entityManager.addEntity(bullet);

                setMode(Mode.ATTACKING);
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
        if (mode != Mode.DYING) {
            if (other instanceof ZombieEntity) {
                ZombieEntity zombie = (ZombieEntity) other;
                if (zombie.getRow() == getRow()) {
                    zombie.setMode(ZombieEntity.Mode.ATTACKING);
                    zombie.setTarget(this);
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
                getAnimationState().setAnimation(0, "die", false);
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
