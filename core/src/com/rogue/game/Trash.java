package com.rogue.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Trash {
    private Vector2 position;
    private Vector2 velocity;
    private TextureRegion texture;
    private Rectangle hitArea;
    private float scale;
    private float angle;

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getHitArea() {
        return hitArea;
    }

    public Trash(TextureRegion texture) {
        this.texture = texture;
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.hitArea = new Rectangle(0, 0, 0, 0);
    }

    public void prepare(int heroPositionX) {
        position.set(MathUtils.random(heroPositionX - 1280, heroPositionX + 1280), MathUtils.random(1500, 5000));
        velocity.set(0, -500.0f);
        hitArea.setPosition(position);
        scale = MathUtils.random(0.6f, 1.5f);
        hitArea.width = 28 * scale;
        hitArea.height = 28 * scale;
        angle = MathUtils.random(0, 360);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, scale, scale, angle);
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);
        hitArea.setPosition(position);
        hitArea.x += 2 * scale;
        hitArea.y += 2 * scale;
    }
}
