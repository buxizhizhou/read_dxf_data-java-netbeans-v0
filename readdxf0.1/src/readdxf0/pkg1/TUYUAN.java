/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package readdxf0.pkg1;
import java.lang.Math;
//import org.apache.commons.math3.util.FastMath;
/**
 *
 * @author User
 */

abstract class tuys{
  String name;
}

class Point extends tuys{
  double x,y;
  //String name="Point";
  public Point(){
    x=0.0;
    y=0.0;
    name="Point";
  }
  public Point(Point a){
    x=a.x;
    y=a.y;
    name="Point";
  }
  public Point(double a, double b){
    x=a;
    y=b;
    name="Point";
  }
  public void shift(double sx,double sy,double xz,double cx,double cy){//x、y进行缩放，关于原点选择xz弧度,加上插入点（即原点变换后的位置）的横纵坐标
    x=x*sx;                                                            //注意xz是弧度
    y=y*sy;
    double r=Math.sqrt(x*x+y*y);//点（x,y）相对于原点的长度
    double yhd=0;//点相对于原点的原始弧度
    if(x!=0 || y!=0) yhd=Math.acos(x/r);//必须不是原点，即r不会0时，才能除。
    if(y<0) yhd=-1*yhd;//由于a度和-a度的余弦值相同，所以要通过y坐标判断一下。 sin和cos在360度内都不是一一对应的。
    //System.out.print("x/r:");System.out.println(x/r);
    //System.out.print("yhd:");System.out.println(Math.toDegrees(yhd));
    //System.out.print("原始弧度："); System.out.println(yhd);
    //System.out.print("x,y:"); System.out.println(x); System.out.println(y);
    x=r*Math.cos(xz+yhd)+cx;//这里应该用r乘，而不是x或y。。。
    y=r*Math.sin(xz+yhd)+cy;   
    //System.out.print("修改后x,y:"); System.out.println(x); System.out.println(y);
  }
}

class Line extends tuys{
  Point qd,zd;
  double zb[]=new double[4];
  //String name="Line";
  public Line(Point a, Point b){
    qd=new Point(a);
    zd=new Point(b);
    //name="Line";
    zb[0]=qd.x;zb[1]=qd.y;zb[2]=zd.x;zb[3]=zd.y;
  }
  public Line(double x1,double y1,double x2,double y2){
    qd=new Point();
    zd=new Point();
    qd.x=x1;
    qd.y=y1;
    zd.x=x2;
    zd.y=y2;
    //name="Line";
    zb[0]=qd.x;zb[1]=qd.y;zb[2]=zd.x;zb[3]=zd.y;
  }
  public Line(Line sl){
    qd=new Point(sl.qd);
    zd=new Point(sl.zd);
    zb[0]=qd.x;zb[1]=qd.y;zb[2]=zd.x;zb[3]=zd.y;
  }
  public void whzb(){//由起点和终点维护坐标对数组
    zb[0]=qd.x;zb[1]=qd.y;zb[2]=zd.x;zb[3]=zd.y;
  }
}

class LWpolyline extends tuys{
  Point num[];
  int plen;//点个数，即num数组的长度
  double zb[];//点的xy坐标对数组，方便存入Oracle数据库
  //int close;//是否闭合
  //String name="LWpolyline";
  public LWpolyline(int n,double xy[]){//点个数，坐标x、y数组
    //if(bh==1) n=n+1;//闭合的话，最后重复第一个点
    //close=bh;
    plen=n;
    num=new Point[n];
    zb=new double[n*2];
    for(int i=0;i<n*2;++i){
      zb[i]=xy[i];
      //System.out.println(i);
      num[i/2]=new Point();//num数组中的每个元素还需要new Point   —— thanks for 室友LXK
      num[i/2].x=xy[i];
      ++i;
      zb[i]=xy[i];
      num[i/2].y=xy[i];
   }
   name="LWpolyline";
  }
  public LWpolyline(LWpolyline slw){
    this(slw.plen,slw.zb);
  }
  public void whzb(){//根据Point数组和长度，维护坐标对数组
    for(int i=0;i<plen;++i){
     zb[i*2]=num[i].x;
     zb[i*2+1]=num[i].y;
    }
  }
  public boolean isRect(){//判断是否为矩形
   if(plen!=4) return false;
   if(num[0].x==num[1].x && num[1].y==num[2].y && num[2].x==num[3].x && num[3].y==num[0].y && num[0].y!=num[1].y && num[1].x!=num[2].x && num[2].y!=num[3].y && num[3].x!=num[0].x)
    return true;
   else if(num[0].y==num[1].y && num[1].x==num[2].x && num[2].y==num[3].y && num[3].x==num[0].x && num[0].x!=num[1].x && num[1].y!=num[2].y && num[2].x!=num[3].x && num[3].y!=num[0].y)
    return true;
   else return false;
  } 
}

class Arc extends tuys{
  Point center;
  double radius,qhd,zhd;//半径、起点弧度，终点弧  
  Point qd,td,zd;//起点、第三点、终点
  double zb[]=new double[6];//点的xy坐标，顺时针存储，CAD中的起点到终点一般为逆时针方向，则应先存终点  //注意可能需要通过构造函数来维护这个数组，如果利用它的话。
  /*public Arc(Point a,Point b,Point c){//起点、第三点、终点顺序初始化
    qd=new Point(a);
    td=new Point(b);
    zd=new Point(c);
    zb=new double[6];
    zb[0]=zd.x;zb[1]=zd.y;zb[2]=td.x;zb[3]=td.y;zb[4]=qd.x;zb[5]=qd.y; //必须以顺时针存   这里顺时针逆时针可能没有关系，因为三个点确定了一个弧，不会有方向差异。
    //zb[0]=qd.x;zb[1]=qd.y;zb[2]=td.x;zb[3]=td.y;zb[4]=zd.x;zb[5]=zd.y;
    name="Arc";
  }*/
  public Arc(Arc sac){
    this(sac.center,sac.radius,sac.qhd,sac.zhd);//构造函数可以相互调用，但得用this而不是函数名——感谢崔大治。。。
  }
  
  public Arc(Point cent,double r,double qds,double zds){
    center=new Point(cent);
    radius=r;
    qhd=qds;
    zhd=zds;
    computertp(center,r,qds,zds);//构造三点
    whzb();
  }
  
  public void computertp(Point cent,double rad,double qds,double zds){//构造三点
    if(zds<qds) zds=zds+Math.PI*2;//如果终点度数小于起点度数，则加上一个2PI，否则对计算第三点时有影响。
    double qx=cent.x+rad*Math.cos(qds);
    double qy=cent.y+rad*Math.sin(qds);
    double zx=cent.x+rad*Math.cos(zds);
    double zy=cent.y+rad*Math.sin(zds);
    //构造第三个点
    double tx=0.0,ty=0.0;
    double tds=(qds+zds)/2;
    double r=Math.sqrt((qx-cent.x)*(qx-cent.x)+(qy-cent.y)*(qy-cent.y));
    tx=r*Math.cos(tds)+cent.x;
    ty=r*Math.sin(tds)+cent.y;
    qd=new Point(qx,qy);
    td=new Point(tx,ty);
    zd=new Point(zx,zy);
  }
  
  public void whzb(){//由三个点维护坐标数组
   zb[0]=zd.x;zb[1]=zd.y;zb[2]=td.x;zb[3]=td.y;zb[4]=qd.x;zb[5]=qd.y;
   //zb[0]=qd.x;zb[1]=qd.y;zb[2]=td.x;zb[3]=td.y;zb[4]=zd.x;zb[5]=zd.y;
  }
}


class Circle extends tuys{
  Point center;
  double radius;
  //String name="Circle";
  public Circle(Point c,double r){
   center=new Point(c);
   radius=r;
   //name="Circle";
  }
  public Circle(double cx,double cy,double r){
   this(new Point(cx,cy),r);
  }
  public Circle(Circle sccl){
   this(sccl.center,sccl.radius);
  }
}

 class Solid extends tuys{
   Point yi,er,san,si;//第一、二、三、四角点(左下，右下，左上，右上)
   double zb[];
   public Solid(Point a,Point b,Point c,Point d){
    yi=new Point(a);
    er=new Point(b);
    san=new Point(c);
    si=new Point(d);
    whzb();
   }
   
   public Solid(Solid sld){
    yi=new Point(sld.yi);
    er=new Point(sld.er);
    san=new Point(sld.san);
    si=new Point(sld.si);
    whzb();
   } 
   
   public void whzb(){//最终方法，不能被重写
    if(san.x==si.x && san.y==si.y){//判断第三点和第四点是否是同一点
     zb=new double[6+2];
     zb[0]=yi.x; zb[1]=yi.y;
     zb[2]=er.x; zb[3]=er.y;
     zb[4]=san.x; zb[5]=san.y;
     zb[6]=yi.x; zb[7]=yi.y;
    }
    else{
     zb=new double[8+2];
     zb[0]=yi.x; zb[1]=yi.y;
     zb[2]=er.x; zb[3]=er.y;
     zb[4]=si.x; zb[5]=si.y;//注意顺序
     zb[6]=san.x; zb[7]=san.y;
     zb[8]=yi.x; zb[9]=yi.y;
    }
   }
 }

public class TUYUAN {
 //final public static double PI=3.1415926;
}