package gr.culturebank.webapp;

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
    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        float w=getWidth(),h=getHeight(),cx=w/2f,top=h*.06f,baseY=h*.68f;
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(4,58,112));
        Path roof=new Path();
        roof.moveTo(w*.18f,h*.26f); roof.lineTo(cx,top); roof.lineTo(w*.82f,h*.26f);
        roof.lineTo(w*.76f,h*.32f); roof.lineTo(cx,h*.14f); roof.lineTo(w*.24f,h*.32f); roof.close();
        c.drawPath(roof,p);
        stroke.setColor(Color.rgb(238,181,52)); stroke.setStrokeWidth(Math.max(3,w*.008f)); c.drawPath(roof,stroke);
        p.setColor(Color.rgb(238,181,52));
        for(float x: new float[]{w*.27f,w*.68f}) c.drawRoundRect(new RectF(x,h*.30f,x+w*.06f,baseY),w*.015f,w*.015f,p);
        p.setColor(Color.rgb(3,61,116)); c.drawRoundRect(new RectF(w*.25f,h*.31f,w*.75f,baseY),w*.02f,w*.02f,p);
        stroke.setColor(Color.rgb(37,133,214)); stroke.setStrokeWidth(Math.max(2,w*.006f)); c.drawRoundRect(new RectF(w*.25f,h*.31f,w*.75f,baseY),w*.02f,w*.02f,stroke);

        p.setColor(Color.rgb(4,45,90)); c.drawCircle(cx,h*.49f,w*.17f,p);
        stroke.setColor(Color.rgb(242,188,59)); stroke.setStrokeWidth(Math.max(3,w*.007f)); c.drawCircle(cx,h*.49f,w*.17f,stroke);
        stroke.setStrokeWidth(Math.max(1,w*.003f));
        c.drawOval(new RectF(cx-w*.085f,h*.32f,cx+w*.085f,h*.66f),stroke);
        c.drawLine(cx-w*.16f,h*.49f,cx+w*.16f,h*.49f,stroke);
        c.drawLine(cx-w*.14f,h*.43f,cx+w*.14f,h*.43f,stroke);
        c.drawLine(cx-w*.14f,h*.55f,cx+w*.14f,h*.55f,stroke);

        p.setColor(Color.rgb(242,188,59));
        Path land=new Path(); land.moveTo(cx-w*.12f,h*.40f);land.lineTo(cx-w*.04f,h*.36f);land.lineTo(cx+w*.015f,h*.40f);land.lineTo(cx-w*.02f,h*.48f);land.lineTo(cx-w*.09f,h*.46f);land.close();c.drawPath(land,p);
        land=new Path();land.moveTo(cx+w*.02f,h*.39f);land.lineTo(cx+w*.11f,h*.41f);land.lineTo(cx+w*.13f,h*.49f);land.lineTo(cx+w*.07f,h*.56f);land.lineTo(cx+w*.02f,h*.50f);land.close();c.drawPath(land,p);

        p.setColor(Color.rgb(2,52,102)); c.drawRect(w*.20f,baseY,w*.80f,baseY+h*.045f,p);
        p.setTypeface(Typeface.create(Typeface.SERIF,Typeface.BOLD));
        p.setTextAlign(Paint.Align.CENTER); p.setTextSize(w*.118f);
        p.setColor(Color.WHITE); c.drawText("CULTURE",cx-w*.06f,h*.91f,p);
        p.setColor(Color.rgb(228,165,42)); c.drawText("BANK",cx+w*.25f,h*.91f,p);
    }
}
