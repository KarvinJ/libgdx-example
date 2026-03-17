package knight.nameless;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.controllers.Controller;import com.badlogic.gdx.controllers.Controllers;import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    private final int SCREEN_WIDTH = 640;
    private final int SCREEN_HEIGHT = 480;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private Rectangle rectangle;
    private Controller controller;

    @Override
    public void create() {

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        controller = Controllers.getCurrent();

        rectangle = new Rectangle(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 64, 64);
    }

    private void update() {

        if (controller != null)
            joystickControllers();
    }

    private void joystickControllers(){

        if (controller.getButton(controller.getMapping().buttonDpadRight))
            rectangle.x += 5;

        else if (controller.getButton(controller.getMapping().buttonDpadLeft))
            rectangle.x -= 5;

        else if (controller.getButton(controller.getMapping().buttonDpadUp))
            rectangle.y += 5;

        else if (controller.getButton(controller.getMapping().buttonDpadDown))
            rectangle.y -= 5;

        if (controller.getButton(controller.getMapping().buttonBack))
            rectangle.setPosition(0, 0);
    }

    @Override
    public void render() {

        update();

        ScreenUtils.clear(Color.BLACK);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

        shapeRenderer.end();
//        batch.begin();
//        batch.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
    }
}
