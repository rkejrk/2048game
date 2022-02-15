package com.mygdx.game;


import static sun.jvm.hotspot.debugger.win32.coff.DebugVC50X86RegisterEnums.TAG;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sun.tools.javac.util.Log;

import java.util.logging.Level;
import java.util.logging.Logger;


public class MyGdxGame extends ApplicationAdapter {
	/**
	 * 定数の参照
	 */
	static Consts consts = new Consts();
	/**
	 *　設定情報を取得する
	 */
	private Preferences prefs;
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
	private FPSLogger logger = new FPSLogger();
	private com.badlogic.gdx.utils.Logger log_gdx = new com.badlogic.gdx.utils.Logger("ALRIGHT");
	/**
	 * ログ
	 */
	private Logger logger_java = Logger.getLogger("afs");
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
	private int new_width = consts.STATIC_WIDTH;
	/**
	 * 画面立幅
	 */
	private int new_height = consts.STATIC_HEIGHT;

	/**
	 * マスの情報
	 */
	private Array<Array<BlockModel>> blockArray;
	/**
	 * 4*4マスを配置するコンテンツの横幅
	 */
	private int content_size = consts.STATIC_WIDTH - 20;
	/**
	 * ブロックを追加するフラグ
	 * 0・・・追加しない
	 * 1・・・追加する
	 */
	public int block_generator_flag = 1;

	@Override
	public void resize (int width, int height) {
		new_height = height;
		new_width = width;
		viewport.update(width, height);
		content_size = consts.STATIC_WIDTH - 20;
	}

	@Override
	public void create () {
		//		設定を保存する
		prefs = Gdx.app.getPreferences(consts.PREFERENCE_ID);
		prefs.putInteger("count", 4);
		prefs.flush();

//		カメラで映す範囲を設定
		camera = new OrthographicCamera(consts.STATIC_WIDTH, consts.STATIC_HEIGHT);
		viewport = new FitViewport(consts.STATIC_WIDTH, consts.STATIC_HEIGHT, camera);

//		スプライト表示の準備
		batch = new SpriteBatch();

//		フォント表示の準備
		fontGen = new FreeTypeFontGenerator(Gdx.files.local(consts.DIGITAL_FONT_PATH));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 64;
		param.color = Color.WHITE;
		param.borderColor = Color.BROWN;
		bitmapFont = fontGen.generateFont(param);

//		図形描画の準備
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);

//		データの初期化
		blockArray = new Array<Array<BlockModel>>();
	}

	@Override
	public void render () {
//		全体の背景カラーを設定する
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		カメラが持つワールド座標をもとに図形を配置するように設定し、画面幅による位置のずれ対策を行う
		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);

		logger.log();

//		コンテンツの初期描画
		int count = prefs.getInteger("count", 4);
		generatorContentOutLine(count);
//		新規ブロックデータの追加処理
		if(block_generator_flag != consts.BLOCK_VALUE_NONE){
			setNewBlock(count);
		}

//		TODO:イベント処理
		
//		TODO:採点

//		ブロックの描画
		generatorBlock(count);
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
	private void generatorContentOutLine(int count){
//		背景描画
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(0.835f, 0.737f, 0.68f, 1);
		shapeRenderer.setColor(0.501f, 0.368f, 0.368f, 0.5f);
		shapeRenderer.box(
				-content_size / 2f,
				-content_size /2f,
				0,
				content_size,
				content_size,
				0
		);
		shapeRenderer.end();

//		マス情報更新前の情報をバックアップ
		Array<Array<BlockModel>> backUpArray = blockArray;
//		現在の画面に合わせた新規マス情報を用意
		blockArray = new Array<Array<BlockModel>>();
		float blank_block_size = ((float) content_size / count) - (consts.BLOCK_MARGIN * 2) ;

		for(int c_y=0; c_y < count; c_y++){
			Array<BlockModel> columns = new Array<BlockModel>();
			for(int c_x=0; c_x < count; c_x++){
//				c_x,c_yとマスの表示領域の掛け算
				float x = ((blank_block_size + (consts.BLOCK_MARGIN * 2)) * c_x)
						- (content_size / 2f) + consts.BLOCK_MARGIN;
				float y = ((blank_block_size + (consts.BLOCK_MARGIN * 2)) * c_y)
						- (content_size / 2f) + consts.BLOCK_MARGIN;

//				各マスを描写
				shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//				shapeRenderer.setColor(0.94f, 0.835f, 0.694f, 1);
				shapeRenderer.setColor(0.8f, 0.592f, 0.592f, 0.8f);
				shapeRenderer.box(x, y, 0, blank_block_size, blank_block_size, 0);
				shapeRenderer.end();

//				新規マス情報を作成し配列に追加していく
				BlockModel block = new BlockModel(x, y);
//				すでにブロックが存在する場合はブロックの情報を上書き
				if(!backUpArray.isEmpty() && backUpArray.size > c_y && backUpArray.get(c_y).size > c_x) {
					block.setValue(backUpArray.get(c_y).get(c_x).getBlockValue());
				}
				columns.add(block);
			}

			blockArray.add(columns);
		}
	}

	/**
	 * 新しいブロックを追加する
	 * @param count 列および行の数
	 */
	private void setNewBlock(int count){
//		作成が完了するまで繰り返す
		while (block_generator_flag != consts.BLOCK_VALUE_NONE){
//			ランダムにブロックを生成する位置を設定
			int row_random_index = MathUtils.random(0, count - 1);
			int col_random_index = MathUtils.random(0, count - 1);
//			位置情報
			int row_index = 0;
			int col_index = 0;
			for(Array<BlockModel> row: blockArray){
				for(BlockModel col: row){
//					ランダムな座標のマスで値が0である場合、2のブロックを追加し終了する
					if(
						row_random_index == row_index &&
						col_random_index == col_index &&
						col.getBlockValue() == consts.BLOCK_VALUE_NONE
					){
						col.setValue(2);
						block_generator_flag = consts.BLOCK_VALUE_NONE;
					}

					col_index++;
				}
				row_index++;
			}
		}
	}

	/**
	 * ブロックを表示する
	 * @param count 行および列の数
	 */
	private void generatorBlock(int count) {
		for (Array<BlockModel> row : blockArray) {
			for (BlockModel col : row) {
//				値が0以外のマスはブロックを生成する
				if (col.getBlockValue() != consts.BLOCK_VALUE_NONE) {
					float blank_block_size =
							((float) content_size / count) - (consts.BLOCK_MARGIN * 2);

					//blockの描写
					shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
					blockColorChange(col.getBlockValue());
					shapeRenderer.box(col.getX(), col.getY(), 0, blank_block_size, blank_block_size, 0);
					shapeRenderer.end();

					//文字
					batch.begin();
//					bitmapFont.draw(batch, String.valueOf(col.getBlockValue()), col.getX() + new_width /1.7f ,  col.getY() + new_height/1.7f);
//					TODO: xyの基準が0じゃない？
					bitmapFont.draw(batch, String.valueOf(col.getBlockValue()), col.getX() + new_width ,  col.getY() + new_width);
					batch.end();
				}
			}
		}
	}

	/**
	 * ブロックの値によって色の設定を変更する
	 */
	private void blockColorChange(int value){
		switch (value) {
			case 2:
				shapeRenderer.setColor(0.98f, 0.556f, 1, 1);
				break;
			case 4: shapeRenderer.setColor(0.99f, 0.741f, 1, 1);
				break;
			case 8: shapeRenderer.setColor(0.792f, 0.592f, 0.8f, 0.8f);
				break;
			case 16: shapeRenderer.setColor(0.494f, 0.368f, 0.501f, 0.5f);
				break;
			case 32: shapeRenderer.setColor(0.494f, 0.313f, 0.501f, 0.5f);
				break;
			case 64: shapeRenderer.setColor(0.882f, 0.741f, 1, 1);
				break;
			case 128: shapeRenderer.setColor(0.729f, 0.407f, 1, 1);
				break;
			case 256: shapeRenderer.setColor(0.415f, 0.313f, 0.501f, 0.5f);
				break;
			case 512: shapeRenderer.setColor(0.705f, 0.592f, 0.8f, 0.8f);
				break;
			case 1024: shapeRenderer.setColor(0.443f, 0.368f, 0.501f, 0.5f);
				break;
			case 2048: shapeRenderer.setColor(0.341f, 0.145f, 0.333f, 0.34f);
				break;
			default: shapeRenderer.setColor(1, 0.2f, 0.972f, 1);
				break;
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
