package knight.nameless;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Player {

    public final Rectangle bounds;
    private TextureRegion textureRegion;
    private TextureRegion birdsRegion;
    private TextureRegion reimuRegion;
    private Animation<TextureRegion> birdsAnimation;
    private Animation<TextureRegion> reimuAnimation;
    private float animationTimer;

    public Player(int positionX, int positionY, String path) {

        textureRegion = new TextureRegion(new Texture(path));
        bounds = new Rectangle(100, 100, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());

        reimuRegion = new TextureRegion(new Texture("img/reimu-spritesheet.png"));
        birdsRegion = new TextureRegion(new Texture("img/red-bird-sprites.png"));

        birdsAnimation = makeAnimationByTotalFrames(birdsRegion, 3);
        reimuAnimation = makeAnimationByTotalFrames(reimuRegion, 8);
    }

    private Animation<TextureRegion> makeAnimationByTotalFrames(TextureRegion region, int totalFrames) {

        int textureWidth = region.getRegionWidth() / totalFrames;

        Array<TextureRegion> animationFrames = new Array<>();

        for (int i = 0; i < totalFrames; i++)
            animationFrames.add(new TextureRegion(region, i * textureWidth, 0, textureWidth, region.getRegionHeight()));

        return new Animation<>(0.2f, animationFrames);
    }

    public void update(float deltaTime) {

        animationTimer += deltaTime;

        textureRegion = birdsAnimation.getKeyFrame(animationTimer, true);
//        playerRegion = reimuAnimation.getKeyFrame(animationTimer, true);
    }

    public void draw(Batch batch) {

        batch.draw(textureRegion, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void draw(ShapeRenderer shapeRenderer) {

        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void dispose() {

        textureRegion.getTexture().dispose();
        reimuRegion.getTexture().dispose();
        birdsRegion.getTexture().dispose();
    }
}
