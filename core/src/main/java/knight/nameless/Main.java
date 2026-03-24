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
    private BitmapFont font;
    private Rectangle rectangle;
    private Rectangle ball;
    private Vector2 ballVelocity;
    private TextureRegion[] scoreNumbers;
    private Color[] colors;
    private int colorIndex;
    private int score;

    @Override
    public void create() {

        font = new BitmapFont();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        controller = Controllers.getCurrent();

        rectangle = new Rectangle(100, 100, 64, 64);
        ball = new Rectangle(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 32, 32);
        ballVelocity = new Vector2(300, 300);

        scoreNumbers = loadNumbersTextureRegion();

        colors = new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.CORAL, Color.BLACK};

        camera = new OrthographicCamera();
        camera.position.set(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 0);
        viewport = new ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private TextureRegion[] loadNumbersTextureRegion() {

        Texture textureToSplit = new Texture("numbers.png");

        return TextureRegion.split(
            textureToSplit, textureToSplit.getWidth() / 10,
            textureToSplit.getHeight()
        )[0];
    }

    private void drawNumbers(SpriteBatch batch, int number, float positionX) {

        final float width = 48;
        final float height = 64;
        final float positionY = SCREEN_HEIGHT - 70;
        var spaceBetweenNumbers = scoreNumbers[0].getRegionWidth() * 2 - 10;

        if (number < 0) {

            batch.draw(scoreNumbers[0], positionX, positionY, width, height);
            return;
        }

        if (number > 999) {

            int thousand = number / 1000;
            int thousandUnits = number % 1000;

            int hundred = number / 100;
            int hundredUnits = number % 100;

            batch.draw(scoreNumbers[thousand], positionX, positionY, width, height);

            if (thousandUnits < 10) {

                batch.draw(scoreNumbers[0], positionX + spaceBetweenNumbers, positionY, width, height);
                batch.draw(scoreNumbers[0], positionX + spaceBetweenNumbers * 2, positionY, width, height);
                batch.draw(scoreNumbers[thousandUnits], positionX + spaceBetweenNumbers * 3, positionY, width, height);
            }
            else {

                int hundredTens = thousandUnits / 10;
                int units = thousandUnits % 10;

                batch.draw(scoreNumbers[0], positionX + spaceBetweenNumbers, positionY, width, height);
                batch.draw(scoreNumbers[hundredTens], positionX + spaceBetweenNumbers * 2, positionY, width, height);
                batch.draw(scoreNumbers[units], positionX + spaceBetweenNumbers * 3, positionY, width, height);
            }


        } else if (number < 10) {

            batch.draw(scoreNumbers[number], positionX + spaceBetweenNumbers * 2, positionY, width, height);

        } else {

            int hundred = number / 100;
            int hundredUnits = number % 100;

            if (hundred > 0)
                batch.draw(scoreNumbers[hundred], positionX, positionY, width, height);

            if (hundredUnits < 10) {

                batch.draw(scoreNumbers[0], positionX + spaceBetweenNumbers, positionY, width, height);
                batch.draw(scoreNumbers[hundredUnits], positionX + spaceBetweenNumbers * 2, positionY, width, height);
            } else {

                int hundredTens = hundredUnits / 10;
                int units = hundredUnits % 10;

                batch.draw(scoreNumbers[hundredTens], positionX + spaceBetweenNumbers, positionY, width, height);
                batch.draw(scoreNumbers[units], positionX + spaceBetweenNumbers * 2, positionY, width, height);
            }
        }
    }

    private void update(float deltaTime) {

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

        ball.x += ballVelocity.x * deltaTime;
        ball.y += ballVelocity.y * deltaTime;
    }

    private void keyboardControllers(float deltaTime) {

        final int playerSpeed = 600;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.W) && rectangle.x < SCREEN_WIDTH - rectangle.width)
            rectangle.y += playerSpeed * deltaTime;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.S) && rectangle.x > 0)
            rectangle.y -= playerSpeed * deltaTime;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.D) && rectangle.y < SCREEN_HEIGHT - rectangle.height)
            rectangle.x += playerSpeed * deltaTime;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.A) && rectangle.y > 0)
            rectangle.x -= playerSpeed * deltaTime;

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.Q))
            rectangle.setPosition(0, 0);
    }

    private void joystickControllers(float deltaTime) {

        final int playerSpeed = 600;

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
    }

    @Override
    public void render() {

        float deltaTime = Gdx.graphics.getDeltaTime();

        update(deltaTime);

        ScreenUtils.clear(Color.LIGHT_GRAY);

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

        shapeRenderer.setColor(colors[colorIndex]);
        shapeRenderer.rect(ball.x, ball.y, ball.width, ball.height);

        shapeRenderer.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        drawNumbers(batch, (int) rectangle.x, SCREEN_WIDTH / 2f - 128);
        drawNumbers(batch, (int) rectangle.y, SCREEN_WIDTH / 2f);

        batch.end();
    }

    @Override
    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
        batch.dispose();
    }
}
