package gr.culturebank.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import java.util.Random;

public class StarFieldView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random r = new Random(2026);
    private float[] xs, ys, sizes, phase;
    public StarFieldView(Context c){ super(c); p.setColor(Color.WHITE); }
    @Override protected void onSizeChanged(int w,int h,int ow,int oh){ int n=120; xs=new float[n];ys=new float[n];sizes=new float[n];phase=new float[n]; for(int i=0;i<n;i++){xs[i]=r.nextFloat()*w;ys[i]=r.nextFloat()*h;sizes[i]=0.8f+r.nextFloat()*2.4f;phase[i]=r.nextFloat()*6.28f;} }
    @Override protected void onDraw(Canvas c){ super.onDraw(c); if(xs==null)return; double t=System.currentTimeMillis()/700.0; for(int i=0;i<xs.length;i++){ float a=(float)(0.25+0.75*Math.abs(Math.sin(t+phase[i]))); p.setAlpha((int)(255*a)); c.drawCircle(xs[i],ys[i],sizes[i],p);} postInvalidateDelayed(70); }
}
