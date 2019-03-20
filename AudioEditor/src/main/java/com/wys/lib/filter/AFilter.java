package com.wys.lib.filter;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.SparseArray;

import com.wys.lib.utils.MatrixUtils;
import com.wys.lib.utils.OpenGLUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Created by yas on 2019/3/19
 * Describe:完成加载shader、绘制图像、清除数据等工作
 */
public abstract class AFilter {
    private static final String TAG="AFilter";

    public static boolean DEBUG=true;

    /**
     * 单位矩阵
     */
    public static final float[] OM = MatrixUtils.getOriginalMatrix();
    /**程序句柄*/
    protected int mProgram;
    /**定点坐标句柄*/
    protected int mHPosition;
    /**纹理坐标句柄*/
    protected int mHCoord;
    /**总变换矩阵句柄*/
    protected int mHMatrix;
    /**默认纹理贴图句柄*/
    protected int mHTexture;

    protected Resources mRes;
    /**定点坐标buffer*/
    protected FloatBuffer mVerBuffer;
    /**纹理坐标buffer*/
    protected FloatBuffer mTextBuffer;

    protected int mFlag=0;

    private float[] matrix= Arrays.copyOf(OM,16);

    /**默认使用Texture2D0*/
    private int textureType=0;
    private int textureId=0;
    /**顶点坐标(openGL)*/
    private float pos[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f,-1.0f,
    };
    /**w纹理坐标*/
    private float[] coord={
          0.0f, 0.0f,
          0.0f, 1.0f,
          1.0f, 0.0f,
          1.0f, 1.0f,
    };

    private SparseArray<boolean[]> mBools;
    private SparseArray<int[]> mInts;
    private SparseArray<float[]> mFloats;

    public AFilter(Resources mRes){
        this.mRes=mRes;
        initBuffer();
    }

    protected  void initBuffer(){
        ByteBuffer a = ByteBuffer.allocateDirect(32);
        a.order(ByteOrder.nativeOrder());
        mVerBuffer = a.asFloatBuffer();
        mVerBuffer.put(pos);
        mVerBuffer.position(0);
        ByteBuffer b = ByteBuffer.allocateDirect(32);
        b.order(ByteOrder.nativeOrder());
        mTextBuffer=b.asFloatBuffer();
        mTextBuffer.put(coord);
        mTextBuffer.position(0);
    }

    public final void create(){
        onCreate();
    }

    public final void setSize(int width,int height){
        onSizeChanged(width,height);
    }


    public void draw(){
        onClear();
        onUseProgram();
        onSetExpandData();
        onBindTexture();
        onDraw();
    }

    protected void onDraw() {
        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition,2, GLES20.GL_FLOAT, false, 0,mVerBuffer);
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTextBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
    }

    /**
     * 绑定默认纹理
     */
    protected void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE+textureType);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,getTextureId());
        GLES20.glUniform1i(mHTexture,textureType);
    }


    /**
     * 设置其它扩展数据
     */
    protected void onSetExpandData() {
        GLES20.glUniformMatrix4fv(mHMatrix,1,false,matrix,0);
    }

    protected void onUseProgram() {
        GLES20.glUseProgram(mProgram);
    }

    /**
     * 清除画布
     */
    protected void onClear(){
        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
    /**实现次方法，完成程序的创建，可直接调用createProgram实现*/
    protected abstract void onCreate();

    protected abstract void onSizeChanged(int width, int height);



    /**
     * 创建openGL程序
     * @param vertex   顶点脚本
     * @param fragment  片元脚本
     */
    protected final void createProgram(String vertex,String fragment){
        mProgram = OpenGLUtil.uCreateGlProgram(vertex,fragment);
        mHPosition = GLES20.glGetAttribLocation(mProgram,"vPosition");
        mHCoord = GLES20.glGetAttribLocation(mProgram,"vCoord");
        mHMatrix = GLES20.glGetUniformLocation(mProgram,"vMatrix");
        mHTexture = GLES20.glGetUniformLocation(mProgram,"vTexture");
    }
    protected final void createProgramByAssetsFile(String vertex, String fragment){
        createProgram(OpenGLUtil.uRes(vertex),OpenGLUtil.uRes(fragment));
    }

    public final void setMatrix(float[] matrix){
        this.matrix=matrix;
    }

    public float[] getMatrix(){
        return matrix;
    }

    public final void setTextureType(int type){
        this.textureType=type;
    }

    public final int getTextureType(){
        return textureType;
    }

    public final int getTextureId(){
        return textureId;
    }

    public final void setTextureId(int textureId){
        this.textureId=textureId;
    }

    public void setFlag(int flag){
        this.mFlag=flag;
    }

    public int getFlag(){
        return mFlag;
    }

    public void setFloat(int type,float ... params){
        if(mFloats==null){
            mFloats=new SparseArray<>();
        }
        mFloats.put(type,params);
    }
    public void setInt(int type,int ... params){
        if(mInts==null){
            mInts=new SparseArray<>();
        }
        mInts.put(type,params);
    }
    public void setBool(int type,boolean ... params){
        if(mBools==null){
            mBools=new SparseArray<>();
        }
        mBools.put(type,params);
    }

    public boolean getBool(int type,int index) {
        if (mBools == null) return false;
        boolean[] b = mBools.get(type);
        return !(b == null || b.length <= index) && b[index];
    }

    public int getInt(int type,int index){
        if (mInts == null) return 0;
        int[] b = mInts.get(type);
        if(b == null || b.length <= index){
            return 0;
        }
        return b[index];
    }

    public float getFloat(int type,int index){
        if (mFloats == null) return 0;
        float[] b = mFloats.get(type);
        if(b == null || b.length <= index){
            return 0;
        }
        return b[index];
    }

    public int getOutputTexture(){
        return -1;
    }
}
