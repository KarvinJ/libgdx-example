package knight.nameless;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    public final Rectangle bounds;
    private final TextureRegion textureRegion;

    public Player(Rectangle bounds, String path) {
        this.bounds = bounds;
        textureRegion = new TextureRegion(new Texture(path));
    }

    public void draw(Batch batch) {

        batch.draw(textureRegion, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void draw(ShapeRenderer shapeRenderer) {

        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void dispose() {

        textureRegion.getTexture().dispose();
    }
}
