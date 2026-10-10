package gr.culturebank.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;

public class CultureBankLogoView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    public CultureBankLogoView(Context c){ super(c); stroke.setStyle(Paint.Style.STROKE); }
    @Override protected void onDraw(Canvas c){ super.onDraw(c); float w=getWidth(), h=getHeight(); float cx=w/2f; float top=h*0.05f; float baseY=h*0.69f;
        p.setColor(Color.rgb(3,55,105)); p.setStyle(Paint.Style.FILL);
        Path roof=new Path(); roof.moveTo(w*.19f,h*.25f); roof.lineTo(cx,top); roof.lineTo(w*.81f,h*.25f); roof.lineTo(w*.76f,h*.31f); roof.lineTo(cx,h*.13f); roof.lineTo(w*.24f,h*.31f); roof.close(); c.drawPath(roof,p);
        stroke.setColor(Color.rgb(242,185,57)); stroke.setStrokeWidth(Math.max(3,w*.008f)); c.drawPath(roof,stroke);
        p.setColor(Color.rgb(242,185,57)); for(float x: new float[]{w*.28f,w*.70f}){ c.drawRoundRect(new RectF(x, h*.29f, x+w*.055f, baseY),w*.015f,w*.015f,p); }
        p.setColor(Color.rgb(2,64,122)); c.drawRoundRect(new RectF(w*.25f,h*.31f,w*.75f,baseY),w*.02f,w*.02f,p);
        stroke.setColor(Color.rgb(25,129,215)); stroke.setStrokeWidth(Math.max(2,w*.006f)); c.drawRoundRect(new RectF(w*.25f,h*.31f,w*.75f,baseY),w*.02f,w*.02f,stroke);
        p.setColor(Color.rgb(4,48,92)); c.drawCircle(cx,h*.49f,w*.17f,p);
        stroke.setStrokeWidth(Math.max(3,w*.007f)); stroke.setColor(Color.rgb(245,190,61)); c.drawCircle(cx,h*.49f,w*.17f,stroke);
        stroke.setStrokeWidth(Math.max(1,w*.003f)); for(int i=-2;i<=2;i++){float dx=i*w*.045f; c.drawOval(new RectF(cx-w*.17f+Math.abs(dx),h*.32f,cx+w*.17f-Math.abs(dx),h*.66f),stroke);} for(int i=-2;i<=2;i++){float y=h*.49f+i*h*.045f;c.drawLine(cx-w*.16f,y,cx+w*.16f,y,stroke);}
        p.setColor(Color.rgb(245,190,61)); Path land=new Path(); land.moveTo(cx-w*.11f,h*.40f);land.lineTo(cx-w*.04f,h*.36f);land.lineTo(cx+w*.015f,h*.40f);land.lineTo(cx-w*.02f,h*.47f);land.lineTo(cx-w*.08f,h*.46f);land.close();c.drawPath(land,p); land=new Path(); land.moveTo(cx+w*.02f,h*.39f);land.lineTo(cx+w*.11f,h*.40f);land.lineTo(cx+w*.13f,h*.48f);land.lineTo(cx+w*.07f,h*.56f);land.lineTo(cx+w*.02f,h*.50f);land.close();c.drawPath(land,p);
        p.setColor(Color.rgb(2,54,104)); c.drawRect(w*.20f,baseY,w*.80f,baseY+h*.045f,p); stroke.setColor(Color.rgb(35,128,205));c.drawRect(w*.20f,baseY,w*.80f,baseY+h*.045f,stroke); p.setColor(Color.rgb(1,34,69)); c.drawRect(w*.16f,baseY+h*.05f,w*.84f,baseY+h*.085f,p);
        p.setTypeface(Typeface.create(Typeface.SERIF,Typeface.BOLD)); p.setTextAlign(Paint.Align.CENTER); p.setTextSize(w*.12f); p.setColor(Color.WHITE); c.drawText("CULTURE",cx,h*.91f,p); float cultureW=p.measureText("CULTURE"); p.setColor(Color.rgb(229,165,42)); c.drawText("BANK",cx+cultureW*.54f,h*.91f,p); }
}
