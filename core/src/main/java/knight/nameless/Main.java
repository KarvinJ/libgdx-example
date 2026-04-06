package knight.nameless;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
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
    private Player player;
    private BitmapFont font;
    private Rectangle ball;
    private Vector2 ballVelocity;
    private Color[] colors;
    private int colorIndex;
    private int score;
    private final int playerSpeed = 600;
    private boolean shouldClearScreen = true;
    private Sound sound;
    private int gameState;
    private boolean isGamePaused;

    @Override
    public void create() {

        fontTexture = new Texture("fonts/test.png");
        fontTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("fonts/test.fnt"), new TextureRegion(fontTexture));
        font.getData().scale(2f);

        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/magic.wav"));

        player = new Player(100, 100, "img/redbird.png");

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        controller = Controllers.getCurrent();

        ball = new Rectangle(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 32, 32);
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
            player.bounds.setPosition(mouseBounds.x, mouseBounds.y);
    }
    private void update(float deltaTime) {

        player.update(deltaTime);

        touchControllers();

        keyboardControllers(deltaTime);

        if (controller != null && controller.isConnected())
            joystickControllers(deltaTime);
        else
            controller = Controllers.getCurrent();

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

        // Check collision between two player.boundss.
        if (player.bounds.overlaps(ball))
        {
            ballVelocity.scl(-1);
            score++;
            sound.play();
        }

        if (gameState >= 2) {

            ball.x += ballVelocity.x * deltaTime;
            ball.y += ballVelocity.y * deltaTime;
        }
    }

    private void keyboardControllers(float deltaTime) {

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.F2) && gameState < 7)
            gameState++;

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.F1) && gameState > -1)
            gameState--;

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.Q))
            player.bounds.setPosition(0, 0);

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.SPACE))
            shouldClearScreen = !shouldClearScreen;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.W) && player.bounds.y < SCREEN_HEIGHT - player.bounds.height)
            player.bounds.y += playerSpeed * deltaTime;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.S) && player.bounds.y > 0)
            player.bounds.y -= playerSpeed * deltaTime;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.D) && player.bounds.x < SCREEN_WIDTH - player.bounds.width)
            player.bounds.x += playerSpeed * deltaTime;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.A) && player.bounds.x > 0)
            player.bounds.x -= playerSpeed * deltaTime;
    }

    private void joystickControllers(float deltaTime) {

        if (controller.getButton(controller.getMapping().buttonDpadUp) && player.bounds.y < SCREEN_HEIGHT - player.bounds.height)
            player.bounds.y += playerSpeed * deltaTime;

        if (controller.getButton(controller.getMapping().buttonDpadDown) && player.bounds.y > 0)
            player.bounds.y -= playerSpeed * deltaTime;

        if (controller.getButton(controller.getMapping().buttonDpadRight) && player.bounds.x < SCREEN_WIDTH - player.bounds.width)
            player.bounds.x += playerSpeed * deltaTime;

        if (controller.getButton(controller.getMapping().buttonDpadLeft) && player.bounds.x > 0)
            player.bounds.x -= playerSpeed * deltaTime;

        if (controller.getButton(controller.getMapping().buttonBack))
            player.bounds.setPosition(0, 0);

        if (controller.getButton(controller.getMapping().buttonLeftStick))
            shouldClearScreen = !shouldClearScreen;
    }

    @Override
    public void render() {

        float deltaTime = Gdx.graphics.getDeltaTime();

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.ENTER))
            isGamePaused = !isGamePaused;

        if (!isGamePaused)
            update(deltaTime);

        if (shouldClearScreen)
            ScreenUtils.clear(Color.BLACK);

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (gameState <= 2) {

            shapeRenderer.setColor(Color.WHITE);
            player.draw(shapeRenderer);
        }

        if (gameState >= 2) {

            shapeRenderer.setColor(colors[colorIndex]);
            shapeRenderer.rect(ball.x, ball.y, ball.width, ball.height);
        }

        shapeRenderer.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        if (gameState > 2)
            player.draw(batch);

        if (gameState == 1)
            font.draw(batch,"(" + (int)player.bounds.x + ", " + (int)player.bounds.y + ")" ,450,SCREEN_HEIGHT - 50);

        if (gameState >= 2)
            font.draw(batch, String.valueOf(score),200,SCREEN_HEIGHT - 50);

        if (isGamePaused)
            font.draw(batch, "Game Paused",350,SCREEN_HEIGHT / 2f);

        batch.end();
    }

    @Override
    public void dispose() {
        font.dispose();
        sound.dispose();
        fontTexture.dispose();
        shapeRenderer.dispose();
        batch.dispose();
    }
}
