package com.mikereese.coffeerun;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;
import java.util.concurrent.Executors;

public class CoffeeRun extends ApplicationAdapter {
	ImageButton button;


	private OrthographicCamera gameCam;
	private Viewport gamePort;
	Sound bounce;
	Sound spill;
	boolean playSound = false;
	SpriteBatch batch;
	Texture img;
	int gameOverScreen =0;
	Texture background;
	Texture backgrounds[];
	Texture logo1;
	Texture logo2;
	Texture tapIcon;
	Texture highScoreImg;
	boolean playing;
	Texture cups[];
	Music music;
	int flapState = 0;
	float cupY = 0;
	float velocity = 0;

	int gameState = 0;
	float gravity = 2;
	int bgState = 1;
	int score = 0;
	Texture topTube;
	Texture bottomTube;
	float gap = 1500;
	int scoringTube = 0;

	float maxTubeOffset;
	Random randomGenerator;

	Circle cupRect;
	//ShapeRenderer shapeRenderer;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	Texture gameOver;

	BitmapFont font;
	float tubeVelocity = 4;

	int numberOfNeedles = 4;
	float[] tubeX = new float[numberOfNeedles];
	float[] tubeOffset = new float[numberOfNeedles];
	float distanceBetweenTubes;

	@Override
	public void resize(int width, int height) {
		gamePort.update(width,height,true);
	}

	@Override
	public void create () {
		Texture mute = new Texture("coffee.gif");
		Drawable muteB = new TextureRegionDrawable(new TextureRegion());
		button = new ImageButton(muteB);

		gameCam = new OrthographicCamera();
		gamePort = new StretchViewport(1080,1776,gameCam);
		cupRect = new Circle();
		music = Gdx.audio.newMusic(Gdx.files.internal("hello.mp3"));
		music.setLooping(true);
		music.setVolume(0.1f);
		//music.play();

		bounce = Gdx.audio.newSound(Gdx.files.internal("bounce.ogg"));
		spill = Gdx.audio.newSound(Gdx.files.internal("splash.mp3"));

		//shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		batch = new SpriteBatch();
		background = new Texture("rainy-day.jpeg");
		cups = new Texture[3];
		backgrounds = new Texture[5];
		cups[0] = new Texture("coffee1.png");
		cups[1] = new Texture("coffee2.png");
		cups[2] = new Texture("coffee3.png");

		logo1 = new Texture("emeraldcity.png");
		logo2 = new Texture("logo2.png");
		tapIcon = new Texture("tapIcon.gif");
		gameOver = new Texture("gameover.png");
		highScoreImg = new Texture("highScore.png");

		playing = true;



		cupY = gamePort.getWorldHeight()/2 - cups[flapState].getHeight()/2;

		topTube = new Texture("needleTop.png");
		bottomTube = new Texture("needleBottom.png");

		bottomTubeRectangles = new Rectangle[numberOfNeedles];
		topTubeRectangles = new Rectangle[numberOfNeedles];

		initializeGame();




	}

	public void initializeGame(){

		maxTubeOffset = gamePort.getWorldHeight()/2 - gap /2 -100;
		randomGenerator = new Random();
		distanceBetweenTubes = gamePort.getWorldWidth() * 3 / 4;

		//tubeX = gamePort.getWorldWidth()/2-topTube.getWidth()/2;
		for(int i = 0; i< numberOfNeedles; i++){
			tubeOffset[i] = (randomGenerator.nextFloat() - .5f) * (gamePort.getWorldHeight() - gap - 700);
			tubeX[i] = gamePort.getWorldWidth()/2-topTube.getWidth()/2 + gamePort.getWorldWidth() + i * distanceBetweenTubes;//adds tubes half a screen a way++

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.setProjectionMatrix(gameCam.combined);
		
		batch.begin();
		batch.draw(background, 0, 0, gamePort.getWorldWidth(), gamePort.getWorldHeight());

		if(gameState == 0){
			batch.draw(logo1,50,1200,logo1.getWidth()*2,logo1.getHeight()*2);
			batch.draw(logo2,115,1050,logo2.getWidth(),logo2.getHeight());
			batch.draw(tapIcon,355,335,tapIcon.getWidth()*2,tapIcon.getHeight()*2);

		}


		if(Gdx.input.justTouched()){
			gameState = 1;
		}

		if(gameState != 0 && playing) {


			if(tubeX[scoringTube] < gamePort.getWorldWidth()/2){
				score++;
				Gdx.app.log("Score",String.valueOf(score));
				if(scoringTube< numberOfNeedles -1)
					scoringTube++;
				else
					scoringTube = 0;
			}

			for(int i = 0; i< numberOfNeedles; i++){

				if(tubeX[i]<-topTube.getWidth()){
					tubeX[i] += numberOfNeedles *distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - .5f) * (gamePort.getWorldHeight() - gap - 700);
				}
				else {
					tubeX[i] = tubeX[i] - 4;



				}

				batch.draw(topTube,tubeX[i],gamePort.getWorldHeight()/2 + gap /2-500+tubeOffset[i]);
				batch.draw(bottomTube,tubeX[i],gamePort.getWorldHeight()/2-gap/2-bottomTube.getWidth()-100+tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i]+50,gamePort.getWorldHeight()/2 + gap /2-500+tubeOffset[i]+100,topTube.getWidth()-100,topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i]+50,gamePort.getWorldHeight()/2-gap/2-bottomTube.getWidth()-100+tubeOffset[i]-100,topTube.getWidth()-100,topTube.getHeight());
			}







			if(Gdx.input.justTouched()){
				velocity = -30;
				bounce.play(0.5f);



			}

			//keep cup from falling off screen
			if(cupY > 0 || velocity<0){
				//graivity
				velocity += gravity;
				cupY -= velocity;//decrease position of cup by velocity
			}





		}
		else{

			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		}

		//create a new thread to control speed of animation, stalls all other actions otherwise
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				if (flapState == 0) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {}
					flapState = 1;
				} else if (flapState == 1) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {}
					flapState = 2;
				} else {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {}
					flapState = 0;
				}
			}
		});





		batch.draw(cups[flapState], gamePort.getWorldWidth() / 2 - cups[flapState].getWidth() / 2 + 100, cupY, 300, 300);//place sprite at center of screen


		cupRect.set(gamePort.getWorldWidth()/2-13, cupY +cups[0].getHeight()/2-95,cups[0].getWidth()/2-175);

		font.draw(batch,String.valueOf(score),100,200);


		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);

		//shapeRenderer.circle(cupRect.x,cupRect.y,cupRect.radius );

		//if(playSound)
			//spill.play();
		for(int i =0;i<4;i++){
			//shapeRenderer.rect(tubeX[i]+50,gamePort.getWorldHeight()/2 + gap /2-500+tubeOffset[i]+100,topTube.getWidth()-100,topTube.getHeight());
			//shapeRenderer.rect(tubeX[i]+50,gamePort.getWorldHeight()/2-gap/2-bottomTube.getWidth()-100+tubeOffset[i]-100,topTube.getWidth()-100,topTube.getHeight());

			//topTubeRectangles[i] = new Rectangle(tubeX[i],gamePort.getWorldHeight()/2 + gap /2-500+tubeOffset[i],topTube.getWidth(),topTube.getHeight());
			//bottomTubeRectangles[i] = new Rectangle(tubeX[i],gamePort.getWorldHeight()/2-gap/2-bottomTube.getWidth()-100+tubeOffset[i],topTube.getWidth(),topTube.getHeight());

			if(Intersector.overlaps(cupRect,topTubeRectangles[i]) || Intersector.overlaps(cupRect,bottomTubeRectangles[i])){
				playing = false;
				gameOverScreen=1;
				playSound = true;

				cupY = 0;

				if (gameOverScreen == 1) {


					batch.draw(gameOver,50,1200,gameOver.getWidth()*3,gameOver.getHeight()*3);


					Preferences prefs = Gdx.app.getPreferences("Scores");
					int highScore = prefs.getInteger("score");
					highScore = Math.max(highScore,score);
					prefs.putInteger("score",highScore);
					prefs.flush();

					batch.draw(highScoreImg,130,1000,highScoreImg.getWidth()*2,highScoreImg.getHeight()*2);
					font.draw(batch,String.valueOf(highScore),800,1100);
					batch.draw(tapIcon,355,600,tapIcon.getWidth()*2,tapIcon.getHeight()*2);
				}



				if(Gdx.input.justTouched()){
					gameState = 0;
					score = 0;
					scoringTube = 0;
					velocity = 0;
					gameOverScreen = 0;



					playing = true;
					initializeGame();

				}






			}
		}
		batch.end();

		//shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
