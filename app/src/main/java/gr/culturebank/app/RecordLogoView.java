package gr.culturebank.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;

public class RecordLogoView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); private final Paint s=new Paint(Paint.ANTI_ALIAS_FLAG);
    public RecordLogoView(Context c){super(c);s.setStyle(Paint.Style.STROKE);}
    @Override protected void onDraw(Canvas c){super.onDraw(c);float w=getWidth(),h=getHeight(),cx=w/2f;float r=Math.min(w,h)*.22f;float cy=h*.27f;
        p.setColor(Color.rgb(4,60,120));c.drawCircle(cx,cy,r,p);s.setColor(Color.rgb(237,177,43));s.setStrokeWidth(Math.max(2,w*.005f));c.drawCircle(cx,cy,r,s);
        for(int i=-2;i<=2;i++){float y=cy+i*r*.28f;c.drawLine(cx-r*.92f,y,cx+r*.92f,y,s);}c.drawOval(new RectF(cx-r*.55f,cy-r,cx+r*.55f,cy+r),s);
        p.setColor(Color.rgb(239,181,48));Path land=new Path();land.moveTo(cx-r*.65f,cy-r*.4f);land.lineTo(cx-r*.15f,cy-r*.55f);land.lineTo(cx+r*.02f,cy-r*.15f);land.lineTo(cx-r*.20f,cy+r*.05f);land.lineTo(cx-r*.55f,cy);land.close();c.drawPath(land,p);land=new Path();land.moveTo(cx+r*.08f,cy-r*.45f);land.lineTo(cx+r*.65f,cy-r*.20f);land.lineTo(cx+r*.45f,cy+r*.45f);land.lineTo(cx+r*.05f,cy+r*.25f);land.close();c.drawPath(land,p);
        s.setStrokeWidth(Math.max(8,w*.025f));s.setColor(Color.rgb(28,92,182));Path ribbon=new Path();ribbon.moveTo(w*.08f,h*.43f);ribbon.cubicTo(w*.30f,h*.58f,w*.70f,h*.50f,w*.92f,h*.42f);c.drawPath(ribbon,s);s.setStrokeWidth(Math.max(4,w*.012f));s.setColor(Color.rgb(235,171,42));c.drawPath(ribbon,s);
        p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.SERIF,Typeface.BOLD));p.setColor(Color.rgb(237,179,50));p.setTextSize(w*.085f);c.drawText("ONE WORLD,",cx,h*.72f,p);p.setTextSize(w*.092f);c.drawText("ONE CANVAS",cx,h*.84f,p);p.setTypeface(Typeface.create(Typeface.SERIF,Typeface.NORMAL));p.setTextSize(w*.032f);p.setColor(Color.WHITE);c.drawText("WORLD HERITAGE GRAFFITI SCROLL · CULTUREBANK",cx,h*.94f,p);}
}
