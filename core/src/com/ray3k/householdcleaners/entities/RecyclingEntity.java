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
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.attachments.PointAttachment;
import com.ray3k.householdcleaners.Core;
import com.ray3k.householdcleaners.Entity;
import com.ray3k.householdcleaners.SpineTwoColorEntity;
import com.ray3k.householdcleaners.states.GameState;

public class RecyclingEntity extends SpineTwoColorEntity implements Healthy {
    private int row;
    private Mode mode;
    private float health;
    private static final float MAX_HEALTH = 100.0f;
    private static final float MONEY_DELAY = 10.0f;
    private float moneyTimer;

    public RecyclingEntity() {
        super(Core.DATA_PATH + "/spine/recycling.json", "stand", GameState.twoColorPolygonBatch);
        setCheckingCollisions(true);
        setMode(Mode.STATIONARY);
        
        getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (entry.getAnimation().getName().equals("hurt")) {
                    setMode(Mode.STATIONARY);
                } else if (entry.getAnimation().getName().equals("die")) {
                    RecyclingEntity.this.dispose();
                }
            }
        });
        
        health = MAX_HEALTH;
        moneyTimer = MONEY_DELAY;
    }

    @Override
    public void actSub(float delta) {
        moneyTimer -= delta;
        if (moneyTimer < 0) {
            moneyTimer = MONEY_DELAY;
            
            MoneyEntity money = new MoneyEntity();
            PointAttachment point = (PointAttachment) getSkeleton().findSlot("point").getAttachment();
            money.setPosition(getX() + point.getX(), getY() + point.getY());
            if (MathUtils.randomBoolean()) {
                money.setMotion(150.0f, 75.0f);
            } else {
                money.setMotion(150.0f, 105.0f);
            }
            money.setGravity(200.0f, 270.0f);
            money.setTarget(getY());
            GameState.entityManager.addEntity(money);
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
