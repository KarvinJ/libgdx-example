package knight.nameless;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
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

public class Main extends ApplicationAdapter implements ControllerListener {

    private final int SCREEN_WIDTH = 1280;
    private final int SCREEN_HEIGHT = 720;
    public OrthographicCamera camera;
    public ExtendViewport viewport;
    private Controller controller;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private Texture fontTexture;
    private Player player;
    private Rectangle player2;
    private BitmapFont font;
    private Rectangle ball;
    private Vector2 ballVelocity;
    private Color[] colors;
    private int colorIndex;
    private int score;
    private int score2;
    private final int playerSpeed = 600;
    private boolean shouldClearScreen = true;
    private Sound sound;
    private int gameState;
    private boolean isGamePaused;

    @Override
    public void create() {

        camera = new OrthographicCamera();
        camera.position.set(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 0);
        viewport = new ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        controller = Controllers.getCurrent();

        if (controller != null)
            controller.addListener(this);

        fontTexture = new Texture("fonts/test.png");
        fontTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("fonts/test.fnt"), new TextureRegion(fontTexture));
        font.getData().scale(2f);

        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/magic.wav"));

        player = new Player((int) (SCREEN_WIDTH / 2f), (int) (SCREEN_HEIGHT / 2f), "img/redbird.png");
        player2 = new Rectangle(SCREEN_WIDTH - 32, SCREEN_HEIGHT / 2f, 16, 96);

        ball = new Rectangle(SCREEN_WIDTH / 2f - 32 / 2f, SCREEN_HEIGHT / 2f, 32, 32);
        ballVelocity = new Vector2(400, 400);

        colors = new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.CORAL, Color.GOLD};
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

        if (ball.x < 0 || ball.x > SCREEN_WIDTH - ball.width) {

            ballVelocity.x *= -1;
            colorIndex = MathUtils.random(0, colors.length - 1);
        }
        else if (ball.y < 0 || ball.y > SCREEN_HEIGHT - ball.height) {

            ballVelocity.y *= -1;
            colorIndex = MathUtils.random(0, colors.length - 1);
        }

        if (player.bounds.overlaps(ball)) {

            ballVelocity.scl(-1);

            if (gameState >= 3)
                sound.play();

            if (gameState >= 4)
                score++;
        }

        if (gameState >= 2 || gameState < -2) {

            ball.x += ballVelocity.x * deltaTime;
            ball.y += ballVelocity.y * deltaTime;
        }
    }

    private void keyboardControllers(float deltaTime) {

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.F2) && gameState < 7)
            gameState++;

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.F1) && gameState >= 0)
            gameState--;

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.Q))
            resetValues();

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

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.UP))
            player.bounds.height++;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.DOWN))
            player.bounds.height--;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.LEFT))
            player.bounds.width--;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.RIGHT))
            player.bounds.width++;
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

        if (controller.getButton(controller.getMapping().buttonY))
            player.bounds.height++;

        if (controller.getButton(controller.getMapping().buttonA))
            player.bounds.height--;

        if (controller.getButton(controller.getMapping().buttonX))
            player.bounds.width--;

        if (controller.getButton(controller.getMapping().buttonB))
            player.bounds.width++;
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

        if (gameState == 1)
            drawCoordinateSystem(shapeRenderer);

        if (gameState <= 4) {

            shapeRenderer.setColor(Color.WHITE);
            player.draw(shapeRenderer);
        }

        if (gameState < 0)
            shapeRenderer.rect(player2.x, player2.y, player2.width, player2.height);

        if (gameState < -4)
            shapeRenderer.rectLine(SCREEN_WIDTH / 2f, SCREEN_HEIGHT, SCREEN_WIDTH / 2f, 0, 2);

        if (gameState >= 2) {

            shapeRenderer.setColor(colors[colorIndex]);
            shapeRenderer.rect(ball.x, ball.y, ball.width, ball.height);
        }

        if (gameState < -1) {

            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(ball.x, ball.y, ball.width, ball.height);
        }

        shapeRenderer.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        if (gameState == 5)
            player.draw(batch);

        if (gameState == 6)
            player.drawBirdAnimation(batch);

        else if (gameState == 7)
            player.drawReimuAnimation(batch);

        if (gameState == 1)
            font.draw(batch, "(" + (int) player.bounds.x + ", " + (int) player.bounds.y + ")", 450, SCREEN_HEIGHT - 50);

        if (gameState >= 4 || gameState < -3)
            font.draw(batch, String.valueOf(score), SCREEN_WIDTH / 2f - 150, SCREEN_HEIGHT - 50);

        if (gameState < -3)
            font.draw(batch, String.valueOf(score2), SCREEN_WIDTH / 2f + 110, SCREEN_HEIGHT - 50);

        if (isGamePaused)
            font.draw(batch, "Game Paused", 350, SCREEN_HEIGHT / 2f);

        font.draw(batch, String.valueOf(gameState), SCREEN_WIDTH - 150, SCREEN_HEIGHT - 50);

        batch.end();
    }

    private void drawCoordinateSystem(ShapeRenderer shapeRenderer) {

        int newPosition = 40;
        int lineLength = 20;

        for (int i = 0; i < 18; i++) {

            shapeRenderer.rectLine(0, newPosition, lineLength, newPosition, 4);
            newPosition += 40;
        }

        newPosition = 40;

        for (int i = 0; i < 35; i++) {

            shapeRenderer.rectLine(newPosition, 0, newPosition, lineLength, 4);
            newPosition += 40;
        }
    }

    private void resetValues() {

        gameState = 1;
        player.bounds.set(0, 0, 34 , 24);
        ball.setPosition(SCREEN_WIDTH / 2f - ball.width / 2f, SCREEN_HEIGHT / 2f);
        score = 0;
    }

    @Override
    public void dispose() {

        player.dispose();
        font.dispose();
        sound.dispose();
        fontTexture.dispose();
        shapeRenderer.dispose();
        batch.dispose();
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {

        if (buttonCode == controller.getMapping().buttonR1 && gameState < 7)
            gameState++;

        if (buttonCode == controller.getMapping().buttonL1 && gameState > -10)
            gameState--;

        if (buttonCode == controller.getMapping().buttonStart)
            isGamePaused = !isGamePaused;

        if (buttonCode == controller.getMapping().buttonBack)
            resetValues();

        if (buttonCode == controller.getMapping().buttonLeftStick)
            shouldClearScreen = !shouldClearScreen;

        if (buttonCode == controller.getMapping().buttonRightStick)
            ball.setPosition(100, 100);

        return false;
    }

    @Override
    public void connected(Controller controller) {}

    @Override
    public void disconnected(Controller controller) {}

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }
}
