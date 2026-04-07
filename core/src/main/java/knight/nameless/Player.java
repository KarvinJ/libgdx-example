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
    public final Rectangle reimuBounds;
    private TextureRegion textureRegion;
    private final TextureRegion birdsRegion;
    private final TextureRegion reimuRegion;
    private final Animation<TextureRegion> birdsAnimation;
    private final Animation<TextureRegion> reimuAnimation;
    private float animationTimer;

    public Player(int positionX, int positionY, String path) {

        textureRegion = new TextureRegion(new Texture(path));
        bounds = new Rectangle(
            positionX,
            positionY,
            textureRegion.getRegionWidth() * 3,
            textureRegion.getRegionHeight() * 3
        );

        birdsRegion = new TextureRegion(new Texture("img/red-bird-sprites.png"));
        birdsAnimation = makeAnimationByRegion(birdsRegion, 3, 0.15f);

        reimuRegion = new TextureRegion(new Texture("img/reimu-spritesheet.png"));
        reimuBounds = new Rectangle(0, 0, reimuRegion.getRegionWidth() / 14f, reimuRegion.getRegionHeight());
        reimuAnimation = makeAnimationByRegion(reimuRegion, 14, 0.04f);
    }

    private Animation<TextureRegion> makeAnimationByRegion(TextureRegion region, int totalFrames, float frameDuration) {

        int frameWidth = region.getRegionWidth() / totalFrames;

        Array<TextureRegion> animationFrames = new Array<>();

        for (int i = 0; i < totalFrames; i++)
            animationFrames.add(new TextureRegion(region, i * frameWidth, 0, frameWidth, region.getRegionHeight()));

        return new Animation<>(frameDuration, animationFrames);
    }

    public void update(float deltaTime) {

        animationTimer += deltaTime;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void draw(Batch batch) {

        batch.draw(textureRegion, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void drawBirdAnimation(Batch batch) {

        textureRegion = birdsAnimation.getKeyFrame(animationTimer, true);
        batch.draw(textureRegion, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void drawReimuAnimation(Batch batch) {

        textureRegion = reimuAnimation.getKeyFrame(animationTimer, true);

        var reimuWidth = reimuBounds.width * 2;
        var reimuHeight = reimuBounds.height * 2;

        batch.draw(
            textureRegion,
            bounds.x - reimuWidth / 4,
            bounds.y - reimuHeight / 4,
            reimuBounds.width * 2,
            reimuBounds.height * 2
        );
    }

    public void dispose() {

        textureRegion.getTexture().dispose();
        reimuRegion.getTexture().dispose();
        birdsRegion.getTexture().dispose();
    }
}
