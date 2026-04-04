package knight.nameless;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Main extends ApplicationAdapter {
    private final int SCREEN_WIDTH = 1280;
    private final int SCREEN_HEIGHT = 720;
    public OrthographicCamera camera;
    public ExtendViewport viewport;
    private Controller controller;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private Texture fontTexture;
    private BitmapFont font;
    private Rectangle rectangle;
    private Rectangle ball;
    private Vector2 ballVelocity;
    private Color[] colors;
    private int colorIndex;
    private int score;
    private final int playerSpeed = 600;
    private boolean shouldClearScreen = true;

    @Override
    public void create() {

        fontTexture = new Texture("fonts/test.png");
        fontTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("fonts/test.fnt"), new TextureRegion(fontTexture));
        font.getData().scale(2f);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        controller = Controllers.getCurrent();

        rectangle = new Rectangle(100, 100, 64, 64);
        ball = new Rectangle(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 48, 48);
        ballVelocity = new Vector2(400, 400);

        colors = new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.CORAL, Color.GOLD};

        camera = new OrthographicCamera();
        camera.position.set(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 0);
        viewport = new ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private void touchControllers() {

        Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        var mouseBounds = new Rectangle(worldCoordinates.x, worldCoordinates.y, 2, 2);

        if (Gdx.input.isTouched())
            rectangle.setPosition(mouseBounds.x, mouseBounds.y);
    }
    private void update(float deltaTime) {

        touchControllers();

        keyboardControllers(deltaTime);

        if (controller != null)
            joystickControllers(deltaTime);

        if (ball.x < 0 || ball.x > SCREEN_WIDTH - ball.width)
        {
            ballVelocity.x *= -1;
            colorIndex = MathUtils.random(0, colors.length - 1);
        }

        else if (ball.y < 0 || ball.y > SCREEN_HEIGHT - ball.height)
        {
            ballVelocity.y  *= -1;
            colorIndex = MathUtils.random(0, colors.length - 1);
        }

        // Check collision between two rectangles.
        if (rectangle.overlaps(ball))
        {
            ballVelocity.scl(-1);
            score++;
            colorIndex = MathUtils.random(0, colors.length - 1);

//            PlaySound(hitSound);
        }

        ball.x += (int) ballVelocity.x * deltaTime;
        ball.y += (int) ballVelocity.y * deltaTime;
    }

    private void keyboardControllers(float deltaTime) {

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.W) && rectangle.y < SCREEN_HEIGHT - rectangle.height)
            rectangle.y += playerSpeed * deltaTime;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.S) && rectangle.y > 0)
            rectangle.y -= playerSpeed * deltaTime;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.D) && rectangle.x < SCREEN_WIDTH - rectangle.width)
            rectangle.x += playerSpeed * deltaTime;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.A) && rectangle.x > 0)
            rectangle.x -= playerSpeed * deltaTime;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.Q))
            rectangle.setPosition(0, 0);
    }

    private void joystickControllers(float deltaTime) {

        if (controller.getButton(controller.getMapping().buttonDpadUp) && rectangle.y < SCREEN_HEIGHT - rectangle.height)
            rectangle.y += playerSpeed * deltaTime;

        if (controller.getButton(controller.getMapping().buttonDpadDown) && rectangle.y > 0)
            rectangle.y -= playerSpeed * deltaTime;

        if (controller.getButton(controller.getMapping().buttonDpadRight) && rectangle.x < SCREEN_WIDTH - rectangle.width)
            rectangle.x += playerSpeed * deltaTime;

        if (controller.getButton(controller.getMapping().buttonDpadLeft) && rectangle.x > 0)
            rectangle.x -= playerSpeed * deltaTime;

        if (controller.getButton(controller.getMapping().buttonBack))
            rectangle.setPosition(0, 0);

        if (controller.getButton(controller.getMapping().buttonLeftStick))
            shouldClearScreen = !shouldClearScreen;
    }

    @Override
    public void render() {

        float deltaTime = Gdx.graphics.getDeltaTime();

        update(deltaTime);

        if (shouldClearScreen)
            ScreenUtils.clear(Color.BLACK);

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

        shapeRenderer.setColor(colors[colorIndex]);
        shapeRenderer.rect(ball.x, ball.y, ball.width, ball.height);

        shapeRenderer.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        font.draw(batch,"(" + (int)rectangle.x + ", " + (int)rectangle.y + ")" ,450,SCREEN_HEIGHT - 50);

        batch.end();
    }

    @Override
    public void dispose() {
        font.dispose();
        fontTexture.dispose();
        shapeRenderer.dispose();
        batch.dispose();
    }
}
