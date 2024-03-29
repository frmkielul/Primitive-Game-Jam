package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import utilities.Constants;

public class Player {
	private Sprite sprite;
	public Body body;
	private boolean frozen = false;
	private static boolean invertedJump = false;
	private static boolean canJump = false;
	
	public Player(float startX, float startY) {
		/* Sprite Initialization */
		sprite = new Sprite(new Texture(Gdx.files.internal("player.png")));
		sprite.setPosition(startX, startY);
		System.out.println("Player constructor called");
	}
	public void physicsSetup() {
		BodyDef bodyDef = new BodyDef();
		// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
		bodyDef.type = BodyType.DynamicBody;
		// Set our body's starting position in the world
		bodyDef.position.set(this.sprite.getX(), this.sprite.getY());
		bodyDef.fixedRotation = true;
		// Create our body in the world using our body definition
		body = Constants.world.createBody(bodyDef);
		
		// Create a circle shape and set its radius to 6
		PolygonShape rect = new PolygonShape();
		rect.setAsBox(this.sprite.getWidth() / 2, this.sprite.getHeight() / 2);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = rect;
		fixtureDef.density = 1f; 
		fixtureDef.friction = 1f;
		fixtureDef.restitution = 0.2f; // Make it bounce a little bit
		body.setGravityScale(5);	// higher # = increases gravity for this body only
		// Create our fixture and attach it to the body
		@SuppressWarnings("unused")
		Fixture fixture = body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		rect.dispose();
	}
	public void draw(Batch batch) {
		batch.draw(sprite, sprite.getX(), sprite.getY());
	}
	public void update() {
		if (!frozen) {
			this.sprite.setPosition(body.getPosition().x - this.sprite.getWidth() / 2, body.getPosition().y - this.sprite.getHeight() / 2);
			Vector2 pos = this.body.getPosition();

			// apply left impulse, but only if max velocity is not reached yet
			if (Gdx.input.isKeyPressed(Keys.A)) {          
			     this.body.applyLinearImpulse(-50f, 0, pos.x, pos.y, true);
			}

			// apply right impulse, but only if max velocity is not reached yet
			if (Gdx.input.isKeyPressed(Keys.D)) {
			     this.body.applyLinearImpulse(50f, 0, pos.x, pos.y, true);
			}
			if (Gdx.input.isKeyJustPressed(Keys.SPACE) && invertedJump && canJump) {
				this.body.applyLinearImpulse(0, -800f, pos.x, pos.y, true);
				disableJump();
			}
			if (Gdx.input.isKeyJustPressed(Keys.SPACE) && !invertedJump && canJump) {
				this.body.applyLinearImpulse(0, 800f, pos.x, pos.y, true);
				disableJump();
			}
		}
		// System.out.println(Constants.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)));	// screen to world	
		// System.out.println("InvertedJump: " + invertedJump);
		System.out.println(body.getLinearVelocity().x + ", " + body.getLinearVelocity().y);
		
	}
	public Vector2 getPosition() {
		return this.body.getPosition();
	}
	public void toggleFrozen() {
		this.frozen = !this.frozen;
	}
	public Vector2 getSpritePosition() {
		return new Vector2(this.sprite.getX(), this.sprite.getY());
	}
	public static void upsideDownJump() {
		invertedJump = true;
	}
	public static void regularJump() {
		invertedJump = false;
	}
	public static void disableJump() {
		canJump = false;
	}
	public static void enableJump() {
		canJump = true;
	}
}
