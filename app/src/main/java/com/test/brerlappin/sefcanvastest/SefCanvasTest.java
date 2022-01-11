package com.test.brerlappin.sefcanvastest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class SefCanvasTest extends Activity implements SurfaceHolder.Callback, View.OnTouchListener{
    Bitmap mainBuffer;
    Canvas mainCanvas;
    SurfaceView surface;
    SurfaceHolder holder;
    boolean surfaceReady = false;
    private static final float INITIAL_RADIUS = 20.0f;
    private static final float RADIUS_INCREMENTS = 1.0f;
    float currentRadius = INITIAL_RADIUS;
    float pointerX=0.0f, pointerY=0.0f;
    int currentColor = 1;//1=CYAN, 2=MAGENTA, 3=YELLOW, 4=BLACK, 5=RED, 6=GREEN, 7=BLUE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sef_canvas_test);

        surface = (SurfaceView) findViewById(R.id.main_surface);
        holder = surface.getHolder();
        holder.addCallback(this);

        surface.setOnTouchListener(this);
    }

    @Override public void surfaceCreated(SurfaceHolder holder){
        surfaceReady = true;
    }
    @Override public void surfaceDestroyed(SurfaceHolder holder){
        destroyCanvas();
        surfaceReady = false;
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height){
        initCanvas(width, height);
    }

    private void initCanvas(int width, int height){
        mainBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mainCanvas = new Canvas(mainBuffer);

        mainCanvas.drawARGB(255, 255, 255, 255);
        drawBufferToSurface();
    }
    private void drawBufferToSurface(){
        Log.v("Draw Event", "Drawing buffer to surface!!!");
        if(!surfaceReady){
            return;
        }
        Canvas tmpCanvas = holder.lockCanvas();
        tmpCanvas.drawBitmap(mainBuffer, new Matrix(), null);
        holder.unlockCanvasAndPost(tmpCanvas);
    }
    private void destroyCanvas(){
        mainCanvas = null;
        mainBuffer = null;
    }

    void drawTouchCircle(float x, float y, float radius){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //paint.setColor(getColor());
        //paint.setAlpha(192);
        setPaintShaderRadial(paint);
        //paint.setShadowLayer(2,4,4,Color.BLACK);
        mainCanvas.drawCircle(x, y, radius, paint);

        //SHOULD NOT BE HERE
        drawBufferToSurface();
    }
    void drawTouchLine(float x, float y, float radius){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //paint.setColor(getColor());
        //paint.setAlpha(192);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(radius*2);
        paint.setStrokeCap(Paint.Cap.ROUND);
        setPaintShaderRadial(paint);
        //paint.setShadowLayer(2,4,4,Color.BLACK);
        mainCanvas.drawLine(pointerX,pointerY,x,y,paint);

        //SHOULD NOT BE HERE
        drawBufferToSurface();
    }
    void setPaintShader(Paint thePaint){
        int color1 = getColor();
        //cycleColors();
        //int color2 = getColor();
        int colors[] = {color1, Color.WHITE};
        LinearGradient shader = new LinearGradient(0,0,mainBuffer.getWidth(),mainBuffer.getHeight(),
                colors,null, Shader.TileMode.CLAMP);
        thePaint.setShader(shader);
    }
    void setPaintShaderRadial(Paint thePaint){
        RadialGradient shader = new RadialGradient(mainBuffer.getWidth()/2,mainBuffer.getHeight()/2,
                mainBuffer.getHeight(),getColor(),Color.BLACK, Shader.TileMode.CLAMP);
        thePaint.setShader(shader);
    }
    void cycleColors(){
        //1=CYAN, 2=MAGENTA, 3=YELLOW, 4=BLACK, 5=RED, 6=GREEN, 7=BLUE
        currentColor++;
        if(currentColor > 7)
            currentColor = 1;
    }
    int getColor(){
        switch (currentColor){
            case 1: return Color.CYAN;
            case 2: return Color.MAGENTA;
            case 3: return Color.YELLOW;
            case 4: return Color.BLACK;
            case 5: return Color.RED;
            case 6: return Color.GREEN;
            case 7: return Color.BLUE;
            default: return Color.CYAN;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        Log.v("Touch Event", "Action: "+event.getAction());
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                pointerX = event.getX();
                pointerY = event.getY();
                drawTouchCircle(pointerX, pointerY, currentRadius);
                break;
            case MotionEvent.ACTION_MOVE:
                if(pointerX == event.getX() && pointerY == event.getY()){
                    currentRadius+=RADIUS_INCREMENTS;
                    drawTouchCircle(pointerX, pointerY, currentRadius);
                }else
                    drawTouchLine(event.getX(), event.getY(), currentRadius);

                pointerX = event.getX();
                pointerY = event.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                cycleColors();
                break;
            case MotionEvent.ACTION_UP:
                currentRadius = INITIAL_RADIUS;
                break;
        }

        return true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        surface.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE |
                                      View.SYSTEM_UI_FLAG_FULLSCREEN |
//                                      View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                                      View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                      View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
}
