package com.mygdx.game;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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
import java.util.logging.Logger;


public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
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
	final private FPSLogger logger = new FPSLogger();
	final private Logger logger_java = Logger.getLogger("afs");
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
	/**
	 * 表示する画面の状態
	 */
	public int game_state = consts.GAME_OVER;

	@Override
	public void resize (int width, int height) {
		new_height = height;
		new_width = width;
		viewport.update(width, height);
		content_size = consts.STATIC_WIDTH - 20;
	}

	@Override
	public void create () {
		//設定を保存する
		prefs = Gdx.app.getPreferences(consts.PREFERENCE_ID);
		prefs.putInteger("count", 4);
		prefs.flush();

		Gdx.input.setInputProcessor(this);

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
		blockArray = new Array<>();
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

		if(game_state == consts.GAME_PLAYING){

//			コンテンツの初期描画
			int count = prefs.getInteger("count", 4);

			generatorContentOutLine(count);
//			新規ブロックデータの追加処理
			if(block_generator_flag != consts.BLOCK_VALUE_NONE){
				setNewBlock(count);
			}

//			ブロックの描画
			generatorBlock(count);

//			採点
			game_state = gameEndCheck();
		} else {
//			ゲームクリアまたは、オーバーの場合ブロック情報を初期化する
			blockArray = new Array<>();

//			TODO: 広告を表示

//			文字を表示
			batch.begin();
			if(game_state == consts.GAME_OVER) {
				bitmapFont.draw(batch, "GAME OVER \n\nAny Key Press \nNext Game", 20, new_height / 2f);
			} else {
				bitmapFont.draw(batch, "GAME CLEAR \n\nAny Key Press \nNext Game", 10, new_height / 2f);
			}
			batch.end();
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		shapeRenderer.dispose();
		fontGen.dispose();
		bitmapFont.dispose();
	}

	@Override
	public boolean keyDown (int keycode) {
		if(game_state != consts.GAME_PLAYING){
			game_state = consts.GAME_PLAYING;
			return true;
		}
		if(keycode == Input.Keys.UP){
			upBlocks();
		} else if(keycode == Input.Keys.DOWN){
			downBlocks();
		} else if(keycode == Input.Keys.RIGHT){
			rightBlocks();
		} else if(keycode == Input.Keys.LEFT){
			leftBlocks();
		}
//		方向キーの場合新しいブロックを作成する
		if (
				keycode == Input.Keys.UP ||
				keycode == Input.Keys.DOWN ||
				keycode == Input.Keys.LEFT ||
				keycode == Input.Keys.RIGHT
		){
			block_generator_flag = 1;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}

	/**
	 * 指定行列数のマスを描画する
	 * @param count マスの行数および列数
	 */
	private void generatorContentOutLine(int count){
//		背景描画
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
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
		blockArray = new Array<>();
		float blank_block_size = ((float) content_size / count) - (consts.BLOCK_MARGIN * 2) ;

		int r_i = 0;
		for(int c_y = count - 1; c_y >= 0; c_y--){
			Array<BlockModel> columns = new Array<>();
			for(int c_x = 0; c_x < count; c_x++){
//				c_x,c_yとマスの表示領域の掛け算
				float x = ((blank_block_size + (consts.BLOCK_MARGIN * 2)) * c_x)
						- (content_size / 2f) + consts.BLOCK_MARGIN;
				float y = ((blank_block_size + (consts.BLOCK_MARGIN * 2)) * c_y)
						- (content_size / 2f) + consts.BLOCK_MARGIN;

//				各マスを描写
				shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
				shapeRenderer.setColor(0.8f, 0.592f, 0.592f, 0.8f);
				shapeRenderer.box(x, y, 0, blank_block_size, blank_block_size, 0);
				shapeRenderer.end();

//				新規マス情報を作成し配列に追加していく
				BlockModel block = new BlockModel(x, y);
//				すでにブロックが存在する場合はブロックの情報を上書き
				if(!backUpArray.isEmpty() && backUpArray.size > c_y && backUpArray.get(c_y).size > c_x) {
					block.setValue(backUpArray.get(r_i).get(c_x).getBlockValue());
				}
				columns.add(block);
			}
			r_i++;
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
			for(Array<BlockModel> row: blockArray){
				int col_index = 0;
				for(BlockModel col: row){
//					ランダムな座標のマスで値が0である場合、2のブロックを追加し終了する
					if(
						row_random_index == row_index &&
						col_random_index == col_index &&
						col.getBlockValue() == consts.BLOCK_VALUE_NONE
					){
						col.setValue(2);
						block_generator_flag = 0;
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
					bitmapFont.draw(batch, String.valueOf(col.getBlockValue()), col.getX() + new_width /1.5f ,  col.getY() + new_height/1.7f);
//					TODO: xyの基準がWindowに依存している？
//					logger_java.info(col.getX() + ", " + col.getY());
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

	/**
	 * ブロックを上に寄せる
	 */
	private void upBlocks(){

		for(int col_index = 0; col_index < blockArray.size; col_index++){
			for(int row_index = 0; row_index < blockArray.size; row_index++){

//				ブロックを詰める
				Array<Integer> zero_last_index = new Array<>();
				for (int i = row_index; i < blockArray.size; i++) {
					int value = blockArray.get(i).get(col_index).getBlockValue();
					if(value == consts.BLOCK_VALUE_NONE){
//						値が存在しない場合は移動用マスとしてインデックスを保存
						zero_last_index.add(i);
					} else if(zero_last_index.size != 0) {
//						値が存在する場合、上寄せする
						blockArray.get(((int) zero_last_index.get(0))).get(col_index).setValue(value);
						zero_last_index.removeIndex(0);
						blockArray.get(i).get(col_index).removeValue();
//						値が存在しない場合は移動用マスとしてインデックスを保存
						zero_last_index.add(i);
					}
				}

//				上下2個の値を取得
				int top_block_value = blockArray.get(row_index).get(col_index).getBlockValue();
//				下にブロックがないかチェック
				int bottom_block_value = 0;
				int bottom_index = row_index + 1;
//				値が0以外のマスに当たるまで繰り返す
				while (
						bottom_index < blockArray.size && bottom_block_value == consts.BLOCK_VALUE_NONE
				) {
					bottom_block_value =
							blockArray.get(bottom_index).get(col_index).getBlockValue();
					bottom_index++;
				}

//				2個の値場合は上のブロックの値を大きくする
				if(top_block_value == bottom_block_value){
					blockArray.get(row_index).get(col_index).setValue(top_block_value + bottom_block_value);
					blockArray.get(bottom_index - 1).get(col_index).removeValue();
				}
			}
		}
	}

	/**
	 * ブロックを下に寄せる
	 */
	private void downBlocks(){

		for(int col_index = 0; col_index < blockArray.size; col_index++){
			for(int row_index = blockArray.size - 1; row_index >= 0; row_index--){

//				ブロックを詰める
				Array<Integer> zero_last_index = new Array<>();
				for (int i = blockArray.size - 1; i >= 0; i--) {
					int value = blockArray.get(i).get(col_index).getBlockValue();
					if(value == consts.BLOCK_VALUE_NONE){
//						値が存在しない場合は移動用マスとしてインデックスを保存
						zero_last_index.add(i);
					} else if(zero_last_index.size != 0) {
//						値が存在する場合、上寄せする
						blockArray.get(((int) zero_last_index.get(0))).get(col_index).setValue(value);
						zero_last_index.removeIndex(0);
						blockArray.get(i).get(col_index).removeValue();
//						値が存在しない場合は移動用マスとしてインデックスを保存
						zero_last_index.add(i);
					}
				}

//				上下2個の値を取得
				int top_block_value = blockArray.get(row_index).get(col_index).getBlockValue();
//				上にブロックがないかチェック
				int bottom_block_value = 0;
				int bottom_index = row_index - 1;
//				値が0以外のマスに当たるまで繰り返す
				while (
						bottom_index >= 0 && bottom_block_value == consts.BLOCK_VALUE_NONE
				) {
					bottom_block_value =
							blockArray.get(bottom_index).get(col_index).getBlockValue();
					bottom_index--;
				}

//				2個の値場合は上のブロックの値を大きくする
				if(top_block_value == bottom_block_value){
					blockArray.get(row_index).get(col_index).setValue(top_block_value + bottom_block_value);
					blockArray.get(bottom_index + 1).get(col_index).removeValue();
				}
			}
		}
	}

	/**
	 * ブロックを左に寄せる
	 */
	private void leftBlocks(){

		for(int row_index = 0; row_index < blockArray.size; row_index++){
			for(int col_index = 0; col_index < blockArray.size; col_index++){

//				ブロックを詰める
				Array<Integer>zero_last_index = new Array<>();
				for (int i = col_index; i < blockArray.size; i++) {
					int value = blockArray.get(row_index).get(i).getBlockValue();
					if(value == consts.BLOCK_VALUE_NONE){
//						値が存在しない場合は移動用マスとしてインデックスを保存
						zero_last_index.add(i);
					} else if(zero_last_index.size != 0) {
//						値が存在する場合、上寄せする
						blockArray.get(row_index).get(((int) zero_last_index.get(0))).setValue(value);
						zero_last_index.removeIndex(0);
						blockArray.get(row_index).get(i).removeValue();
//						値が存在しない場合は移動用マスとしてインデックスを保存
						zero_last_index.add(i);
					}
				}

//				上下2個の値を取得
				int top_block_value = blockArray.get(row_index).get(col_index).getBlockValue();
//				下にブロックがないかチェック
				int bottom_block_value = 0;
				int bottom_index = col_index + 1;
//				値が0以外のマスに当たるまで繰り返す
				while (
						bottom_index < blockArray.size && bottom_block_value == consts.BLOCK_VALUE_NONE
				) {
					bottom_block_value =
							blockArray.get(row_index).get(bottom_index).getBlockValue();
					bottom_index++;
				}

//				2個の値場合は上のブロックの値を大きくする
				if(top_block_value == bottom_block_value){
					blockArray.get(row_index).get(col_index).setValue(top_block_value + bottom_block_value);
					blockArray.get(row_index).get(bottom_index - 1).removeValue();
				}
			}
		}
	}

	/**
	 * ブロックを右に寄せる
	 */
	private void rightBlocks(){

		for(int row_index = 0; row_index < blockArray.size; row_index++){
			for(int col_index = blockArray.size - 1; col_index >= 0; col_index--){
//				ブロックを詰める
				Array<Integer> zero_last_index = new Array<>();
				for (int i = blockArray.size - 1; i >= 0; i--) {
					int value = blockArray.get(row_index).get(i).getBlockValue();
					if(value == consts.BLOCK_VALUE_NONE){
//						値が存在しない場合は移動用マスとしてインデックスを保存
						zero_last_index.add(i);
					} else if(zero_last_index.size != 0) {
//						値が存在する場合、上寄せする
						blockArray.get(row_index).get(((int) zero_last_index.get(0))).setValue(value);
						zero_last_index.removeIndex(0);
						blockArray.get(row_index).get(i).removeValue();
//						値が存在しない場合は移動用マスとしてインデックスを保存
						zero_last_index.add(i);
					}
				}

//				上下2個の値を取得
				int top_block_value = blockArray.get(row_index).get(col_index).getBlockValue();
//				上にブロックがないかチェック
				int bottom_block_value = 0;
				int bottom_index = col_index - 1;
//				値が0以外のマスに当たるまで繰り返す
				while (
						bottom_index >= 0 && bottom_block_value == consts.BLOCK_VALUE_NONE
				) {
					bottom_block_value =
							blockArray.get(row_index).get(bottom_index).getBlockValue();
					bottom_index--;
				}

//				2個の値場合は上のブロックの値を大きくする
				if(top_block_value == bottom_block_value){
					blockArray.get(row_index).get(col_index).setValue(top_block_value + bottom_block_value);
					blockArray.get(row_index).get(bottom_index + 1).removeValue();
				}
			}
		}
	}

	/**
	 * ブロックの状態からゲーム状況を判断する
	 * @return 画面の状態
	 */
	private int gameEndCheck(){
		boolean blank_flg = false;
		boolean move_flag = false;
		boolean clear_flg = false;

		for(Array<BlockModel> row: blockArray){
			int c_last_value = 1;

			for(BlockModel col: row) {
				int value = col.getBlockValue();
//				2048ブロックがないかチェック
				if (value == 2048) clear_flg = true;
//				全マス0以外かチェック
				if (value == consts.BLOCK_VALUE_NONE) blank_flg = true;
//				横に重複ブロックがないかチェック
				if (value == c_last_value) move_flag = true;
				if (value != consts.BLOCK_VALUE_NONE) c_last_value = value;
			}
		}

//		縦に重複ブロックがないかチェック
		for(int c_i = 0; c_i < blockArray.size; c_i++){
			int r_last_value = 1;

			for(int r_i = 0; r_i < blockArray.size; r_i++){
				int value = blockArray.get(r_i).get(c_i).getBlockValue();
//				横に重複ブロックがないかチェック
				if (value == r_last_value) move_flag = true;
				if (value != consts.BLOCK_VALUE_NONE) r_last_value = value;
			}
		}

		if (!blank_flg && move_flag){
			return consts.GAME_OVER;
		}
		if (clear_flg){
			return consts.GAME_CLEAR;
		}
		return consts.GAME_PLAYING;
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
