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
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.ray3k.householdcleaners.Entity;
import com.ray3k.householdcleaners.states.GameState;

public class BackgroundEntity extends Entity {
    private TiledDrawable tiledDrawable;

    @Override
    public void create() {
        tiledDrawable = new TiledDrawable(GameState.spineAtlas.findRegion("tile"));
    }

    @Override
    public void act(float delta) {
    }

    @Override
    public void actEnd(float delta) {
    }

    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        if (spriteBatch.isDrawing()) {
            tiledDrawable.draw(spriteBatch, 0.0f, 0.0f, 102.0f * GameState.COLUMNS, 102.0f * GameState.ROWS);
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void collision(Entity other) {
    }

}
