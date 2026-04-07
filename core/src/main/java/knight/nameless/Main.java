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
import com.badlogic.gdx.utils.Array;
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
    private Array<Brick> bricks;
    private Color[] colors;
    private int colorIndex;
    private int score;
    private int score2;
    private final int playerSpeed = 600;
    private boolean shouldClearScreen = true;
    private Sound sound;
    private int gameState;
    private boolean isGamePaused;
    private boolean isAutoPlayMode;

    @Override
    public void create() {

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

        player = new Player(SCREEN_WIDTH / 2f,  SCREEN_HEIGHT / 2f, "img/redbird.png");
        player2 = new Rectangle(SCREEN_WIDTH - 32, SCREEN_HEIGHT / 2f, 16, 96);

        ball = new Rectangle(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 32, 32);
        ballVelocity = new Vector2(400, 400);

        colors = new Color[]{
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.CORAL,
            Color.GOLD,
            Color.CYAN,
            Color.FOREST,
            Color.PURPLE,
            Color.LIGHT_GRAY,
            Color.VIOLET,
        };

        bricks = createBricks();

        camera = new OrthographicCamera();
        camera.position.set(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 0);
        viewport = new ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private Array<Brick> createBricks() {

        Array<Brick> bricks = new Array<>();

        int brickPoints = 1;
        int positionX;
        int positionY = 400;

        for (int row = 0; row < 9; row++)
        {
            positionX = 6;

            for (int column = 0; column < 12; column++)
            {
                Brick actualBrick = new Brick(
                    new Rectangle(positionX, positionY, 102, 20),
                    brickPoints,
                    colors[row]
                );

                bricks.add(actualBrick);
                positionX += 106;
            }

            brickPoints++;
            positionY += 22;
        }

        return bricks;
    }

    private void touchControllers() {

        Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        var mouseBounds = new Rectangle(worldCoordinates.x, worldCoordinates.y, 2, 2);

        if (Gdx.input.isTouched())
            player.bounds.setPosition(mouseBounds.x, mouseBounds.y);
    }

    private void update() {

        float deltaTime = Gdx.graphics.getDeltaTime();

        player.update(deltaTime);

        touchControllers();

        keyboardControllers(deltaTime);

        if (controller != null && controller.isConnected())
            joystickControllers(deltaTime);
        else
            controller = Controllers.getCurrent();

        if ((gameState > 0 || gameState < -7) && (ball.x < 0 || ball.x > SCREEN_WIDTH - ball.width)) {

            ballVelocity.x *= -1;
            colorIndex = MathUtils.random(0, colors.length - 1);
        }
        else if (ball.y < 0 || ball.y > SCREEN_HEIGHT - ball.height) {

            ballVelocity.y *= -1;
            colorIndex = MathUtils.random(0, colors.length - 1);
        }

        if (player.bounds.overlaps(ball) || ((gameState < 0 && gameState > -7) && player2.overlaps(ball))) {

            if (gameState > 0)
                ballVelocity.scl(-1);
            else if (gameState < 0 && gameState > -7)
                ballVelocity.x *= -1;
            else if (gameState == -8)
                ballVelocity.y *= -1;

            if (gameState < 0 || gameState >= 3)
                sound.play();

            if (gameState >= 4)
                score++;
        }

        if (ball.x > SCREEN_WIDTH) {

            ball.x = SCREEN_WIDTH / 2f;
            ball.y = SCREEN_HEIGHT / 2f;
            ballVelocity.scl(-1);
            score++;
        }

        else if (ball.x < - ball.width) {

            ball.x = SCREEN_WIDTH / 2f;
            ball.y = SCREEN_HEIGHT / 2f;
            ballVelocity.scl(-1);
            score2++;
        }

        if (gameState == -8) {

            for (Brick brick : bricks) {

                if (brick.isDestroyed)
                    continue;

                if (brick.bounds.overlaps(ball)) {

                    sound.play();
                    ballVelocity.y *= -1;
                    score += brick.points;
                    brick.isDestroyed = true;
                    break;
                }
            }
        }

        if (gameState == -7) {

            score = 0;
            ball.setPosition(SCREEN_WIDTH / 2f, SCREEN_HEIGHT /2f);
        }

        if (gameState >= 2 || gameState < -2 && gameState != -7) {

            ball.x += (int)(ballVelocity.x * deltaTime);
            ball.y += (int)(ballVelocity.y * deltaTime);
        }

        if (isAutoPlayMode && ball.y < SCREEN_HEIGHT - player2.height)
            player2.y = ball.y;
    }

    private void keyboardControllers(float deltaTime) {

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.F2) && gameState < 7)
            gameState++;

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.F1) && gameState > -8)
            gameState--;

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.Q))
            resetValues();

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.E)) {

            isAutoPlayMode = !isAutoPlayMode;
            ball.setPosition(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f);
        }

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

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.ENTER))
            isGamePaused = !isGamePaused;

        if (!isGamePaused)
            update();

        if (shouldClearScreen) {

            if (gameState != -6)
                ScreenUtils.clear(Color.BLACK);
            else
                ScreenUtils.clear(0.08f,0.63f,0.52f,1);
        }

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (gameState == -8) {

            for (Brick brick : bricks)
                brick.draw(shapeRenderer);
        }

        if (gameState == 1)
            drawCoordinateSystem(shapeRenderer);

        if (gameState <= 4) {

            shapeRenderer.setColor(Color.WHITE);
            player.draw(shapeRenderer);
        }

        if (gameState < 0 && gameState > -7)
            shapeRenderer.rect(player2.x, player2.y, player2.width, player2.height);

        if (gameState == -6) {

            shapeRenderer.setColor(new Color(0.5f, 0.8f, 0.72f, 1));
            shapeRenderer.circle(SCREEN_WIDTH / 2f,  SCREEN_HEIGHT / 2f, 150);
        }

        shapeRenderer.setColor(Color.WHITE);

        if (gameState < -4 && gameState > -7)
            shapeRenderer.rectLine(SCREEN_WIDTH / 2f, SCREEN_HEIGHT, SCREEN_WIDTH / 2f, 0, 2);

        if (gameState < -1) {

            if (gameState == -6)
                shapeRenderer.setColor(Color.YELLOW);

            shapeRenderer.rect(ball.x, ball.y, ball.width, ball.height);
        }

        if (gameState >= 2) {

            shapeRenderer.setColor(colors[colorIndex]);
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
            font.draw(batch, "(" + (int) player.bounds.x + ", " + (int) player.bounds.y + ")", 450, SCREEN_HEIGHT - 25);

        if (gameState >= 4 || gameState < -3)
            font.draw(batch, String.valueOf(score), SCREEN_WIDTH / 2f - 150, SCREEN_HEIGHT - 25);

        if (gameState < -3 && gameState > -7)
            font.draw(batch, String.valueOf(score2), SCREEN_WIDTH / 2f + 110, SCREEN_HEIGHT - 25);

        if (isGamePaused)
            font.draw(batch, "Game Paused", 350, SCREEN_HEIGHT / 2f);

        font.draw(batch, String.valueOf(gameState), SCREEN_WIDTH - 150, SCREEN_HEIGHT - 25);

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
        player.bounds.set(0, 0, 34 * 3, 24 * 3);
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

        if (buttonCode == controller.getMapping().buttonL1 && gameState > -8)
            gameState--;

        if (buttonCode == controller.getMapping().buttonStart)
            isGamePaused = !isGamePaused;

        if (buttonCode == controller.getMapping().buttonBack)
            resetValues();

        if (buttonCode == controller.getMapping().buttonLeftStick)
            shouldClearScreen = !shouldClearScreen;

        if (buttonCode == controller.getMapping().buttonRightStick) {

            isAutoPlayMode = !isAutoPlayMode;
            ball.setPosition(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f);
        }

        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    @Override
    public void connected(Controller controller) {}

    @Override
    public void disconnected(Controller controller) {}
}
