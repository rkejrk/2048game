package com.mygdx.game;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MyGdxGame extends ApplicationAdapter {
	/**
	 * 定数の参照
	 */
	static Consts consts = new Consts();
	/**
	 * 画面の解像度に依存しないよう表示範囲を固定する
	 * https://zarudama.github.io/libgdx_basic/libgdx02/
	 */
	private FitViewport viewport;
	/**
	 * 画面上の情報を取得
	 */
	private OrthographicCamera camera;
	/**
	 * ログを残す
	 */
	private FPSLogger logger;
	/**
	 * スプライトを画面上に表示する
	 */
	private SpriteBatch batch;
	/**
	 * 図形を画面上に表示する
	 */
	private ShapeRenderer shapeRenderer;
	/**
	 * フォントを追加する
	 */
	private FreeTypeFontGenerator fontGen;
	/**
	 * 文字を画面上に表示する
	 */
	private BitmapFont bitmapFont;
	/**
	 * 画面横幅
	 */
	int new_width = consts.static_width;
	/**
	 * 画面立幅
	 */
	int new_height = consts.static_height;

	@Override
	public void resize (int width, int height) {
		new_height = height;
		new_width = width;
		viewport.update(width, height);
	}

	@Override
	public void create () {
//		ログ作成の準備
		logger = new FPSLogger();
//		カメラで映す範囲を設定
		camera = new OrthographicCamera(consts.static_width, consts.static_height);
		viewport = new FitViewport(consts.static_width, consts.static_height, camera);

//		スプライト表示の準備
		batch = new SpriteBatch();

//		フォント表示の準備
		fontGen = new FreeTypeFontGenerator(Gdx.files.local("font/Let_s_go_Digital_Regular.ttf"));
//		fontGen = new FreeTypeFontGenerator(Gdx.files.local("font/Molot.otf"));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 32;
		param.color = Color.BLUE;
		bitmapFont = fontGen.generateFont(param);

//		図形描画の準備
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
	}

	@Override
	public void render () {
//		全体の背景カラーを設定する
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		カメラが持つワールド座標をもとに図形を配置するように設定し、画面幅による位置のずれ対策を行う
		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);

		this.generateContentOutLine(4);
		//		ブロック描写

//		文字
		batch.begin();
		bitmapFont.draw(
				batch,
				"width: " + String.valueOf(new_width) + "\nheight: "+ String.valueOf(new_height),
				new_width - 12*35, new_height - 66
		);
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		shapeRenderer.dispose();
		fontGen.dispose();
		bitmapFont.dispose();
	}

	/**
	 * 指定行列数のマスを描画する
	 * @param count マスの行数および列数
	 */
	private void generateContentOutLine(int count){
//		背景描画
		int content_size = consts.static_width - 20;
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0.835f, 0.737f, 0.68f, 1);
		shapeRenderer.box(
				-content_size / 2,
				-content_size /2,
				0,
				content_size,
				content_size,
				0
		);
		shapeRenderer.end();
//		マス描画
		float margin = 5;
		float blank_block_size = (content_size / count) - (margin * 2) ;
		for(int c_y=0; c_y < count; c_y++){
			for(int c_x=0; c_x < count; c_x++){
//				c_x,c_yとマスの表示領域の掛け算
				float x = ((blank_block_size + (margin * 2)) * c_x)
						- (content_size / 2) + margin;
				float y = ((blank_block_size + (margin * 2)) * c_y)
						- (content_size / 2) + margin;

				shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
				shapeRenderer.setColor(0.94f, 0.835f, 0.694f, 1);
				shapeRenderer.box(x, y, 0, blank_block_size, blank_block_size, 0);
				shapeRenderer.end();
			}
		}
	}

//	/**
//	 * [開発時補助]座標(0.0)の位置で垂直に交わる線を描画する
//	 */
//	private void generateGuideLine(){
//		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//		shapeRenderer.setColor(0, 0, 1, 1);
//		shapeRenderer.x(0, 0, 1000);
//		shapeRenderer.end();
//	}
}
