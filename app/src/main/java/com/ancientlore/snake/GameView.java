package com.ancientlore.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


class GameView extends SurfaceView implements Runnable
{
	Thread thread = null;
	private volatile boolean running;
	private Context context;
	private int displayX, displayY;
	private SurfaceHolder surfaceHolder;
	private Canvas canvas;
	private Paint paint;
	private Paint paintInfo;
	private Paint paintTip;
	private Paint paintState;

	private int upperPanelY;

	ManagerGame _gm;
	ManagerInput _im;
	ManagerLevel _lm;
	ManagerSound _sm;

	public GameView(Context context)
	{
		super(context);
	}

	public GameView(Context context, int displayX, int displayY)
	{
		super(context);
		this.context = context;
		this.displayX = displayX;
		this.displayY = displayY;
		upperPanelY = displayY / 12;

		surfaceHolder = getHolder();
		paint = new Paint();
		paintInfo = new Paint();
		paintInfo.setTextSize(displayX / 20);
		paintInfo.setColor(Color.WHITE);
		paintInfo.setTextAlign(Paint.Align.LEFT);
		paintTip = new Paint();
		paintTip.setTextSize(displayX / 26);
		paintTip.setColor(Color.WHITE);
		paintTip.setTextAlign(Paint.Align.CENTER);
		paintState = new Paint();
		paintState.setTextSize(displayX / 9);
		paintState.setColor(Color.WHITE);
		paintState.setTextAlign(Paint.Align.CENTER);

		_gm = ManagerGame.getInstance();
		_gm.initialise(3);
		_lm = ManagerLevel.getInstance();
		_lm.initialize(context, displayX, displayY, upperPanelY);
		_im = ManagerInput.getInstance();
		_im.initialise(context, displayX, upperPanelY);
		_sm = ManagerSound.getInstance();
		_sm.initialise(context);

		_gm.setBestScore(ActivityMain.prefs.getInt("HiScore", 0));
		ActivityMain.editor.apply();
	}

	void draw()
	{
		if (surfaceHolder.getSurface().isValid())
		{
			canvas = surfaceHolder.lockCanvas();
			//-----------------
			canvas.drawColor(Color.BLACK);
			paint.setColor(Color.DKGRAY);
			canvas.drawRect(_lm.getGameField(), paint);

			if (_gm.isShowGrid())
			{
				paint.setColor(Color.argb(255, 115, 115, 115));
				for (int i = 0; i < _lm.getGridWidth() + 2; i++)
				{
					canvas.drawLine(i * _lm.getTileSize(), _lm.getGameField().top,
							i * _lm.getTileSize(), _lm.getGameField().bottom, paint);
				}
				for (int i = 0; i < _lm.getGridHeight() + 2; i++)
				{
					canvas.drawLine(0, i * _lm.getTileSize() + _lm.getGameField().top,
							_lm.getGameField().right, i * _lm.getTileSize() + _lm.getGameField().top, paint);
				}
			}
			if (_gm.isShowSectors())
			{
				paint.setColor(Color.argb(255, 115, 115, 115));
				canvas.drawLine(_lm.getGameField().left, _lm.getGameField().top,
						_lm.getGameField().right, _lm.getGameField().bottom, paint);
				canvas.drawLine(_lm.getGameField().right, _lm.getGameField().top,
						_lm.getGameField().left, _lm.getGameField().bottom, paint);
			}

			for (GOFood obj : _lm.getFood())
			{
				canvas.drawBitmap(_lm.getSprites()[obj.getIndex()].bitmap,
						obj.getRect().left, obj.getRect().top, paint);
			}

			for (GOSegment obj : _lm.getSnake())
			{
				canvas.drawBitmap(_lm.getSprites()[obj.getIndex()].bitmap,
						obj.getRect().left, obj.getRect().top, paint);
			}

			paint.setColor(Color.argb(255, 0, 0, 0));
			canvas.drawRect(0, 0, displayX, upperPanelY, paint);
			canvas.drawText(getResources().getString(R.string.ingame_score) + ":" + _gm.getScore() +
					"    " + getResources().getString(R.string.ingame_lives) + ": " + _gm.getLives(), 20, 80, paintInfo);

			canvas.drawBitmap(_im.getMenuButton().getBitmap(),
					_im.getMenuButton().getRect().left, _im.getMenuButton().getRect().top, paint);
			canvas.drawBitmap(_im.getPauseButton().getBitmap(),
					_im.getPauseButton().getRect().left, _im.getPauseButton().getRect().top, paint);

			if (!_gm.isPlaying())
			{
				canvas.drawColor(Color.argb(200, 0, 0, 0));
				canvas.drawText(getResources().getString(R.string.ingame_tut_pause), displayX / 2, displayY / 2 + upperPanelY, paintState);
				canvas.drawText(getResources().getString(R.string.ingame_tut_line_1),
						displayX / 2, displayY / 2 + upperPanelY + paintState.getTextSize() + 5, paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_tut_line_2),
						displayX / 2, displayY / 2 + upperPanelY + paintState.getTextSize() + 5 + paintTip.getTextSize(), paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_tut_line_3),
						displayX / 2, displayY / 2 + upperPanelY + paintState.getTextSize() + 10 + 2 * paintTip.getTextSize(), paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_tut_line_end),
						displayX / 2, displayY / 2 + upperPanelY + paintState.getTextSize() + 15 + 3 * paintTip.getTextSize(), paintTip);
			}
			//-----------------
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	@Override
	public void run()
	{
		while (running)
		{
			if (_gm.isPlaying())
			{
				_lm.update(_gm, _sm);
			}
			draw();
			try
			{
				Thread.sleep(51);//17 = 1000(milliseconds)/60(FPS)
			}
			catch (InterruptedException e)
			{
				Log.e("Error", "Can't sleep on run()");
			}
		}
	}

	protected void pause()
	{
		running = false;
		try
		{
			thread.join();
		}
		catch (InterruptedException e)
		{
			Log.e("Error", "Can't pause");
		}
	}

	protected void resume()
	{
		running = true;
		thread = new Thread(this);
		thread.start();
	}
}
