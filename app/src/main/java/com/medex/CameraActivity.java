package com.medex;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.glass.content.Intents;
import com.medex.globals.LocalDataStore;
import com.medex.globals.ParseUtil;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

public class CameraActivity extends Activity {
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private boolean previewOn;
	private final static int CAMERA_FPS = 5000;
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.camera_preview);

		// Set up the camera preview user interface
		getWindow().setFormat(PixelFormat.UNKNOWN);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceHolderCallback());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA: { // camera button (hardware)

			camera.stopPreview(); // stop the preview
			camera.release(); // release the camera
			previewOn = false;

			// Return false to allow the camera button to do its default action
			return false;
		}
		case KeyEvent.KEYCODE_DPAD_CENTER: // touchpad tap
		case KeyEvent.KEYCODE_ENTER: {

			camera.stopPreview();
			camera.release();
			previewOn = false; // Don't release the camera in surfaceDestroyed()

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // capture image
			startActivityForResult(intent, 0);

			return true;
		}
		default: {
			return super.onKeyDown(keyCode, event);
		}
		}
	}

	private static final int TAKE_PICTURE_REQUEST_NUM = 1;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
			if(resultCode == RESULT_OK) {

				String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);
				processPictureWhenReady(picturePath);
				Log.d(TAG, "onActivityResult called. PicturePath is: " + picturePath);

			}
		finish();
		}

	private void processPictureWhenReady(final String picturePath) {
		Log.d(TAG, "processPictureWhenReady() called.");
		ParseUtil util=new ParseUtil();
		final File pictureFile = new File(picturePath);

		if (pictureFile.exists()) {
			Log.d(TAG, "pictureFile exists. Ready to process.");
			Log.d(TAG, "onActivityResult called. PicturePath is: " + picturePath);
			// The picture is ready; process it - need to add to session images.
			LocalDataStore.getInstance().currentSession.addImage(picturePath);
			try {
				boolean flag=util.postPatientDetails(CameraActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			// The file does not exist yet. Before starting the file observer, you
			// can update your UI to let the user know that the application is
			// waiting for the picture (for example, by displaying the thumbnail
			// image and a progress indicator).
			Log.d(TAG, "pictureFile doens't exists yet.");
			final File parentDirectory = pictureFile.getParentFile();
			FileObserver observer = new FileObserver(parentDirectory.getPath(),
					FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
				// Protect against additional pending events after CLOSE_WRITE
				// or MOVED_TO is handled.
				private boolean isFileWritten;

				@Override
				public void onEvent(int event, String path) {
					if (!isFileWritten) {
						// For safety, make sure that the file that was created in
						// the directory is actually the one that we're expecting.
						File affectedFile = new File(parentDirectory, path);
						isFileWritten = affectedFile.equals(pictureFile);

						if (isFileWritten) {
							stopWatching();

							// Now that the file is ready, recursively call
							// processPictureWhenReady again (on the UI thread).
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									processPictureWhenReady(picturePath);
								}
							});
						}
					}
				}
			};
			observer.startWatching();
		}
	}

	// camera preview stuff
	class SurfaceHolderCallback implements SurfaceHolder.Callback {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			if (null != camera) {

				try {
					Camera.Parameters params = camera.getParameters(); // must change the camera parameters to fix a bug in XE1
					params.setPreviewFpsRange(CAMERA_FPS, CAMERA_FPS);
					camera.setParameters(params);

					camera.setPreviewDisplay(surfaceHolder);
					camera.startPreview();
					previewOn = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				releaseCameraAndPreview();
				camera = Camera.open();
			} catch (Exception e) {
				Log.e(getString(R.string.app_name), "failed to open Camera");
				e.printStackTrace();
			}

			//camera = Camera.open();
		}
		private void releaseCameraAndPreview() {
		//	camera.stopPreview();
			if (camera != null) {
				camera.release();
				camera = null;
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (previewOn) {
				camera.stopPreview(); //stop the preview
				camera.release();  //release the camera for using it later (or if another app want to use)
			}
		}
	}

}
