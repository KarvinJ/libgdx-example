package knight.nameless;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Brick {
    public final Rectangle bounds;
    public final int points;
    public final Color color;
    public boolean isDestroyed;

    public Brick(Rectangle bounds, int points, Color color) {
        this.bounds = bounds;
        this.points = points;
        this.color = color;
    }

    public void draw(ShapeRenderer shapeRenderer) {

        if (isDestroyed)
            return;

        shapeRenderer.setColor(color);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
