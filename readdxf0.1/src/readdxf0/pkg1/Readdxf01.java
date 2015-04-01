/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 原始可正常显示图形的版本。未加入房间识别。——2014.10.1
 */
package readdxf0.pkg1;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.spatial.geometry.*;
import oracle.jdbc.driver.*;
import oracle.sql.STRUCT;
/**
 *
 * @author User
 */
public class Readdxf01 {

    /**
     * @param args the command line arguments
     */
    final public static String fileName="一层平面图_t3.dxf";
    final public static String tableName="cad";
    //final public static String fileName="标准层_t3.dxf";
    //final public static String fileName="from食堂建筑平面图.dxf";
    //final public static String fileName="from惠州酒店十层平面图.dxf";
    //final public static String fileName="ts方框.dxf";
    
    public static int cnt=0;
    final public static int zbsrid=32774;
    //final public static double PI=3.1415926;
    public static List<List> lst=new ArrayList();//存储每个块的图元组成部分。  注意static
    public static List<String> kname=new ArrayList();//lst相应的块名称
    public static List<String> filter=new ArrayList();//不去读取图元的图层名
    public static List<String> mentc=new ArrayList();//门所在图层
    public static String wintcnm="WINDOW";//门窗的图层名
    public static List allty=new ArrayList();//存储所有的图元
    public static List door=new ArrayList();//存由门变换过来的直线段
    
    public static void connect_database(JGeometry geo) throws InstantiationException, IllegalAccessException, SQLException{
           //建立数据库连接   
           String Driver="oracle.jdbc.driver.OracleDriver";    //连接数据库的方法    
           String URL="jdbc:oracle:thin:@127.0.0.1:1521:indoor";    //indoor为数据库的SID    
           String Username="indooradmin";    //用户名               
           String Password="indoor";    //密码    
           //String tableName="cad";
           try {
                   Class.forName(Driver).newInstance();    //加载数据库驱动
                   Connection con=DriverManager.getConnection(URL,Username,Password);  
                   if(!con.isClosed())
                       System.out.println("Succeeded connecting to the Database!");
                   //Statement stmt=con.createStatement();
                   
                   String sqlInsert="INSERT INTO "+tableName+"("+"idC"+","+" geom"+")"+" VALUES("+cnt+",?)";
                   cnt++;
                   System.out.println("Executing query:'"+sqlInsert+"'");
                   PreparedStatement stmt=con.prepareStatement(sqlInsert);
                   STRUCT dbObject=JGeometry.store(geo,con);
                   stmt.setObject(1, dbObject);
                   stmt.execute();
                   stmt.close();
                   
                   con.close();  
               } catch (ClassNotFoundException ex) {
                   Logger.getLogger(Readdxf01.class.getName()).log(Level.SEVERE, null, ex);
               }
    }
    
    public static double huDu(Point a,Point c){//点c为圆心，点a相对于c的弧度。
      double r=Math.sqrt((a.x-c.x)*(a.x-c.x)+(a.y-c.y)*(a.y-c.y));//半径长
      double hd=0;
      if(a.x!=c.x || a.y!=c.y) hd=Math.acos((a.x-c.x)/r);//必须不是圆心，即r不会0时，才能除。
      if(a.y<c.y) hd=-1*hd;//若点a在圆心c下方，则度数为负的。
      return hd;
    }
    
    public static Solid readSolid(BufferedReader bfr) throws IOException{
           double x1=0,x2=0,y1=0,y2=0,z1=0,z2=0,x3=0,y3=0,z3=0,x4=0,y4=0,z4=0;
           String s1=null,s2=null;
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals("  8")==false);  //图层
           String tc=new String(s2);
           //获取点坐标
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 10")==false);  //x坐标
           x1=Double.parseDouble(s2);
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 20")==false);
           y1=Double.valueOf(s2).doubleValue();
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 30")==false);
           z1=Double.valueOf(s2).doubleValue();
           
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 11")==false);  
           x2=Double.valueOf(s2).doubleValue();   
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 21")==false);
           y2=Double.valueOf(s2).doubleValue();
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 31")==false);
           z2=Double.valueOf(s2).doubleValue();
           
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 12")==false);  
           x3=Double.valueOf(s2).doubleValue();   
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 22")==false);
           y3=Double.valueOf(s2).doubleValue();
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 32")==false);
           z3=Double.valueOf(s2).doubleValue();
           
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 13")==false);
           x4=Double.valueOf(s2).doubleValue();   
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 23")==false);
           y4=Double.valueOf(s2).doubleValue();
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 33")==false);
           z4=Double.valueOf(s2).doubleValue();
           
           if(filter.contains(tc)) return null;
           Solid Sld=new Solid(new Point(x1,y1),new Point(x2,y2),new Point(x3,y3),new Point(x4,y4));
           return Sld;
    }
    
    public static Arc readArc(BufferedReader bfr) throws IOException, InstantiationException, IllegalAccessException, SQLException{
           double cx=0.0,cy=0.0,rad=0.0,qd=0.0,zd=0.0;
           String s1=null,s2=null;
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals("  8")==false);  //图层
           String tc=new String(s2);
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 10")==false); 
           cx=Double.parseDouble(s2);
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 20")==false); 
           cy=Double.parseDouble(s2);
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 40")==false); //半径
           rad=Double.parseDouble(s2);
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 50")==false); //起点弧度
           qd=Double.parseDouble(s2)/180*Math.PI;
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 51")==false); //终点弧度
           zd=Double.parseDouble(s2)/180*Math.PI;
           
           /*
           if(zd<qd) zd=zd+Math.PI*2;//如果终点度数小于起点度数，则加上一个2PI，否则对计算第三点时有影响。
           double qx=cx+rad*Math.cos(qd);
           double qy=cy+rad*Math.sin(qd);
           double zx=cx+rad*Math.cos(zd);
           double zy=cy+rad*Math.sin(zd);
           //构造第三个点
           double tx=0.0,ty=0.0;
           if(qx!=zx){
             tx=(qx+zx)/2;
             ty=Math.sqrt(rad*rad-(cx-tx)*(cx-tx))+cy; //!!!别忘了加cy  //这个有问题，ty只有正没有负
           }
           else{
             ty=(qy+zy)/2;
             tx=cx;
           }
           //double qsita=huDu(new Point(qx,qy),new Point(cx,cy));
           //double zsita=huDu(new Point(zx,zy),new Point(cx,cy));
           double td=(qd+zd)/2;
           double r=Math.sqrt((qx-cx)*(qx-cx)+(qy-cy)*(qy-cy));
           tx=r*Math.cos(td)+cx;
           ty=r*Math.sin(td)+cy;*/
           
           if(filter.contains(tc)) return null;
           Arc arc=new Arc(new Point(cx,cy),rad,qd,zd);
           return arc;
    }
    
    public static Circle readCircle(BufferedReader bfr) throws IOException, InstantiationException, IllegalAccessException, SQLException{
           //圆是填充的，可以因此产生问题。可能可以用弧来代替圆，不知道弧的三点能不能有重合
           String s1=null,s2=null;
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals("  8")==false);  //图层
           String tc=new String(s2);
           double center_x=0.0,center_y=0.0,radius=0.0;
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 10")==false); //圆心
           center_x=Double.parseDouble(s2);
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 20")==false); 
           center_y=Double.parseDouble(s2);
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 40")==false); //抛弃z坐标的0值，读半径
           radius=Double.parseDouble(s2);
           
           if(filter.contains(tc)) return null;
           Circle ccle=new Circle(center_x,center_y,radius);
           return ccle;
           /*JGeometry geo=JGeometry.createCircle(center_x,center_y,radius,zbsrid);
           connect_database(geo);*/
    }
    
    public static Line readLine(BufferedReader bfr) throws InstantiationException, IllegalAccessException, SQLException, IOException{
           //这里假设坐标是按照xyz的顺序给出的，若要与顺序无关，可以把下面程序改成在while循环里一直读，然后循环里做判断。
           //System.out.println("heihei");
           double x1=0,x2=0,y1=0,y2=0,z1=0,z2=0;
           String s1=null,s2=null;
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals("  8")==false);  //图层
           String tc=new String(s2);
           //获取点坐标
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 10")==false);  //x坐标
           //System.out.println("s1:"+s1+"    s2:"+s2);
           x1=Double.parseDouble(s2);
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 20")==false);
           y1=Double.valueOf(s2).doubleValue();
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 30")==false);
           z1=Double.valueOf(s2).doubleValue();
           
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 11")==false);  //x坐标
           x2=Double.valueOf(s2).doubleValue();   
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 21")==false);
           y2=Double.valueOf(s2).doubleValue();
           while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 31")==false);
           z2=Double.valueOf(s2).doubleValue();
           
           if(filter.contains(tc)) return null;
           Line l1=new Line(x1,y1,x2,y2);
           return l1;
           //System.out.println(x1+"  "+y1+"  "+x2+"  "+y2);
           //int elemInfo[]={1,2,1};
           /*
           double ordinates[]={x1,y1,x2,y2};
           JGeometry geo=JGeometry.createLinearLineString(ordinates,2,32774);
           connect_database(geo);*/
    }
    
    public static LWpolyline readLWpolyline(BufferedReader bfr) throws IOException, InstantiationException, IllegalAccessException, SQLException{
             double x=0,y=0;
             String s1=null,s2=null;
             while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals("  8")==false);  //图层
             String tc=new String(s2);
             while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 90")==false);  //顶点数
             int num=Integer.parseInt(s2.trim());
             while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 70")==false);  //闭合性
             int cls=Integer.parseInt(s2.trim());   //吐槽：尼玛坑爹啊，自己通过画图对比才知道这个标志多段线是否闭合啊（帮助文档里竟然写关闭，不是闭合。。。）
             if(cls==1) num=num+1;//闭合的话，最后重复第一个点
             double ordinates[] = new double[num*2];
             for(int i=0;i<(cls==1?num-1:num);++i){//中间的表达式，是让i小于原始的num
              while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 10")==false);  //坐标
              x=Double.parseDouble(s2);
              while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 20")==false);  //坐标
              y=Double.parseDouble(s2);
              ordinates[i*2]=x;
              ordinates[i*2+1]=y;
             }
             if(cls==1) { ordinates[2*num-2]=ordinates[0]; ordinates[2*num-1]=ordinates[1];}//重复第一点
             if(filter.contains(tc)) return null;
             LWpolyline lwln=new LWpolyline(num,ordinates);
             return lwln;
             /*
             for(int i=0;i<2*num;++i){
              System.out.println(ordinates[i]+" ");
             }
             
             JGeometry geo=JGeometry.createLinearLineString(ordinates,2,32774);
             connect_database(geo);
             */
    }
    
    public static int find(String name){
      int i=0;
      int flag=0;//是否找到
      for(;i<kname.size();++i){
       if(kname.get(i).equals(name)) { flag=1; break;}
      }
      if(flag==1) return i;
      else return -1;
    }
    
    /**/public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {  
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
    ObjectOutputStream out = new ObjectOutputStream(byteOut);  
    out.writeObject(src);  
    ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());  
    ObjectInputStream in = new ObjectInputStream(byteIn);  
    @SuppressWarnings("unchecked")  
    List<T> dest = (List<T>) in.readObject();  
    return dest;  
    }   
    
    public static void readInsert(BufferedReader bfr) throws IOException, InstantiationException, IllegalAccessException, SQLException, ClassNotFoundException{
      String s1=null,s2=null;
      while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals("  8")==false);  //图层
      String tc=new String(s2);
      while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals("  2")==false);  //块名称
      String name=s2;
      while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 10")==false);  //插入点x值
      double cx=Double.parseDouble(s2);
      while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 20")==false);  //插入点y值
      double cy=Double.parseDouble(s2);
      while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null && s1.equals(" 30")==false);  //插入点z值
      double cz=Double.parseDouble(s2);
      //System.out.println("hehehe");
      double sx=1,sy=1,sz=1,xz=0;//xyz的缩放比例、旋转角度（弧度）
      bfr.mark(100);//这里也需要mark下，不然组码30后直接结束跟着是下一图元的组码0，这样就回退到上一次这个函数里while循环mark的地方了。。    ——调了好长时间，看着几万行的txt啊。。才知道什么是bug。。
      while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null){
        if(s1.equals(" 41"))  sx =Double.parseDouble(s2);
        else if(s1.equals(" 42")) sy=Double.parseDouble(s2);
        else if(s1.equals(" 43")) sz=Double.parseDouble(s2);
        else if(s1.equals(" 50")) xz=Math.toRadians(Double.parseDouble(s2)); //xz=Double.parseDouble(s2)/180*Math.PI;//shift函数利用的是cos函数，为弧度，所以这里转换为弧度
        else if(s1.equals("  0")){//这里回退的判断条件只是下一个图元开始或块结尾，即组码为0
          bfr.reset();//回退到上一次mark的位置，即回退两行。回退的原因是可能这些可选属性均没有，然后读到了下一个图元的开头或者块的结尾。
          break;
        }
        else break;//这里没有处理70、71及拉伸方向等的情况。   
        bfr.mark(100);//标记输入流当前位置。参数为最大读入数据，足够大即可，这里随便选的100
      }
      
      if(filter.contains(tc)) return ;//该图层在要过滤的图层名中，则直接结束，不再存储。
      System.out.print("kname:"); System.out.println(name);
      int indx=find(name); System.out.print("indx:"); System.out.println(indx);
      if(indx<0) return ;
      //if(name.compareTo("$DorLib2D$00000001")!=0) return;//暂时先处理门1这一种情况。
      List tlst=new ArrayList(lst.get(indx));//List tlst=deepCopy(lst.get(indx));
      //List JGlst=new ArrayList();//把这一参照块的图元集合起来，暂时还没用到，可能方便后面修改识别门的地方
      List<Arc> AClst=new ArrayList();//存块定义中的弧
      
      for(int i=0;i<tlst.size();++i){//遍历lst的indx位置元素
   /**/if(tlst.get(i).getClass()==Line.class){//直线
         Line templ=new Line((Line)tlst.get(i));//先是对List的元素进行强制类型转换，然后再创建一个备份
         templ.qd.shift(sx, sy, xz,cx,cy);
         templ.zd.shift(sx, sy, xz,cx,cy);
         templ.whzb();                                                           //吐槽：我要死了，为什么下面的几个都知道维护坐标，这里忘了。。。
         //存入Oracle
         JGeometry geo=JGeometry.createLinearLineString(templ.zb,2,32774);
         connect_database(geo);
       }
       else if(tlst.get(i).getClass()==LWpolyline.class){//多段线
         LWpolyline templw=new LWpolyline((LWpolyline)tlst.get(i));
         for(int j=0;j<templw.plen;++j){
          templw.num[j].shift(sx, sy, xz,cx,cy);
         }
         templw.whzb();
         //存入Oracle
         if(templw.isRect()){
           
         }
          JGeometry geo=null;//if(templw.close==1) geo=JGeometry.createLinearPolygon(templw.zb, 2, zbsrid);//不能用这个函数创建多边形，因为多边形内部是填充的
          geo=JGeometry.createLinearLineString(templw.zb,2,zbsrid);
          connect_database(geo);
       }
       else if(tlst.get(i).getClass()==Arc.class){//弧
         Arc tempac=new Arc((Arc)tlst.get(i));
         tempac.qd.shift(sx, sy, xz,cx,cy);
         tempac.td.shift(sx, sy, xz,cx,cy);
         tempac.zd.shift(sx, sy, xz,cx,cy);
         tempac.center.shift(sx, sy, xz, cx, cy);
         tempac.whzb();
         //存入Oracle
          JGeometry geo=JGeometry.createArc2d(tempac.zb, 2, zbsrid);
          //System.out.print("插入点："); System.out.println(cx);System.out.println(cy);
          //System.out.print("Arc:"); System.out.println(tempac.qd.x);System.out.println(tempac.qd.y);System.out.println(tempac.td.x);System.out.println(tempac.td.y);System.out.println(tempac.zd.x);System.out.println(tempac.zd.y);
          connect_database(geo);
          AClst.add(tempac);
       }/**//**/
       else if(tlst.get(i).getClass()==Circle.class){//圆  //这里关于半径扩大的倍数可能有问题，也许用圆上三点来变换为会好些。
         Circle tempccl=new Circle((Circle)tlst.get(i));
         tempccl.center.shift(sx, sy, xz,cx,cy);
         tempccl.radius=tempccl.radius*sx;//横纵坐标都扩大sx倍，半径扩大sx倍。   半径扩大的倍数既不是不变，也不是横纵坐标扩大的倍数之积
         //存入Oracle
          JGeometry geo=JGeometry.createCircle(tempccl.center.x,tempccl.center.y,tempccl.radius,zbsrid);
          //System.out.println("Circle:"); System.out.println(tempccl.center.x);  System.out.println(tempccl.center.y); System.out.println(tempccl.radius);
          connect_database(geo);
       }
       else if(tlst.get(i).getClass()==Solid.class){
         Solid tempsld=new Solid((Solid)tlst.get(i));
         tempsld.yi.shift(sx, sy, xz, cx, cy);
         tempsld.er.shift(sx, sy, xz, cx, cy);
         tempsld.san.shift(sx, sy, xz, cx, cy);
         tempsld.si.shift(sx, sy, xz, cx, cy);
         tempsld.whzb();
         JGeometry geo=JGeometry.createLinearLineString(tempsld.zb,2,zbsrid);
         connect_database(geo);
       }
      }//end of 遍历for
      if(tc.compareTo(wintcnm)==0){//在门窗图层
        if(AClst.size()==1){
          Arc tarc=AClst.get(0);
          //if(tarc.)
        }
      }
      //return JGlst;
    }
    
    public static void readEntities(BufferedReader bfr) throws IOException, InstantiationException, IllegalAccessException, SQLException, ClassNotFoundException{
      String s1=null,s2=null;
      int flag=0;//指示所读是否为ENTITIES段，1为是，0为否。
       while((s1 = bfr.readLine())!=null && (s2 = bfr.readLine())!=null){ //&& s2.equals("ENDSEC")==false){
         if(s1.equals("  0") && s2.equals("SECTION")){
            s1=bfr.readLine();
            s2=bfr.readLine();
            if(s1.equals("  2") && s2.equals("ENTITIES")) { flag=1; System.out.println("begin in Entities");continue; }   //开始读ENTITIES段
         }
         if(flag==0) continue;    //所读内容不是ENTITIES段
         if(flag==1 && s1.equals("  0") && s2.equals("ENDSEC")) { flag=0; System.out.println("end of Entities"); break; }  //读完ENTITIES段
         if(s1.equals("  0")){//不判断s1直接判断s2是不对的。
          if(s2.equals("LINE")){//直线
            Line ln=readLine(bfr);
            if(ln!=null){
            JGeometry geo=JGeometry.createLinearLineString(ln.zb,2,zbsrid);
            connect_database(geo);
            allty.add(geo);
            }
          }
          else if(s2.equals("LWPOLYLINE")){//多段线
            LWpolyline lwln=readLWpolyline(bfr);
            if(lwln!=null){
            JGeometry geo=JGeometry.createLinearLineString(lwln.zb,2,zbsrid);
            connect_database(geo);
            allty.add(geo);
            }
          }
          else if(s2.equals("CIRCLE")){//圆
            Circle ccle=readCircle(bfr);
            if(ccle!=null){
            JGeometry geo=JGeometry.createCircle(ccle.center.x,ccle.center.y,ccle.radius,zbsrid);
            connect_database(geo);
            allty.add(geo);
            }
          }
          else if(s2.equals("ARC")){//弧
            Arc arc=readArc(bfr);
            if(arc!=null){
            JGeometry geo=JGeometry.createArc2d(arc.zb, 2, zbsrid);  //new JGeometry(2,zbsrid,elemI,ordinates);
            connect_database(geo);
            allty.add(geo);
            }
          }/**/
          else if(s2.equals("INSERT")){//参照块图元
            readInsert(bfr);
             /*List<JGeometry> Jlst=readInsert(bfr);
            for(int i=0;i<Jlst.size();++i){
             connect_database(Jlst.get(i));
            }*/
          }
          else if(s2.equals("SOLID")){
            Solid sld=readSolid(bfr);
            if(sld!=null){
            JGeometry geo=JGeometry.createLinearLineString(sld.zb,2,zbsrid);
            connect_database(geo);
            }
          }
         }//if-s1
       }
    }
     
    public static void readBlocks(BufferedReader bfr) throws IOException, InstantiationException, IllegalAccessException, SQLException{
      String s1=null,s2=null;
      int flag=0;//指示所读是否为BLOCKS段，1为是，0为否。
      while((s1 = bfr.readLine())!=null && (s2 = bfr.readLine())!=null){
        if(s1.equals("  0") && s2.equals("SECTION")){
            s1=bfr.readLine();
            s2=bfr.readLine();
            //System.out.println("s1:"+s1+"    s2:"+s2);
            if(s1.equals("  2") && s2.equals("BLOCKS")) { flag=1; System.out.println("begin in Blocks");continue; }   //开始读BLOCKS段
         }
        if(flag==0) continue;    //所读内容不是BLOCKS段
        if(flag==1 && s1.equals("  0") && s2.equals("ENDSEC")) { flag=0; System.out.println("end of Blocks"); break; }  //读完BLOCKS段
        if(s1.equals("  0") && s2.equals("BLOCK")){//每个块条目
           int kflg=0;//块条目是否读完
           List tl=new ArrayList();
           while(s1.compareTo("  2")!=0) { s1=bfr.readLine(); s2=bfr.readLine(); }
           kname.add(s2);//加入块名称
           while(kflg==0 && (s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null){
             if(s1.equals("100") && s2.equals("AcDbBlockEnd")){//该块条目读完
               kflg=1;
               lst.add(tl);
             }
             if(s1.equals("  0")){
               if(s2.equals("LINE")){
                 Line ln=readLine(bfr);
                 if(ln!=null) tl.add(ln);
               }
               else if(s2.equals("LWPOLYLINE")){
                 LWpolyline lwln=readLWpolyline(bfr);
                 if(lwln!=null) tl.add(lwln);
               }
               else if(s2.equals("ARC")){
                 Arc arc=readArc(bfr);
                 if(arc!=null) tl.add(arc);
               }
               else if(s2.equals("CIRCLE")){
                 Circle ccle=readCircle(bfr);
                 if(ccle!=null) tl.add(ccle);
               }
               else if(s2.equals("INSERT")){//嵌套块定义
                 
               }
               else if(s2.equals("SOLID")){
                 Solid sld=readSolid(bfr);
                 if(bfr!=null) tl.add(sld);
               }
             }
           }
        }
      }
    }
    
    public static void createindex() throws InstantiationException, IllegalAccessException, SQLException{
           //建立数据库连接   
           String Driver="oracle.jdbc.driver.OracleDriver";    //连接数据库的方法    
           String URL="jdbc:oracle:thin:@127.0.0.1:1521:indoor";    //indoor为数据库的SID    
           String Username="indooradmin";    //用户名               
           String Password="indoor";    //密码    
           //String tableName="cad";
           String colName="geom";
           try {
                   Class.forName(Driver).newInstance();    //加载数据库驱动
                   Connection con=DriverManager.getConnection(URL,Username,Password);  
                   if(!con.isClosed())
                       System.out.println("Succeeded connecting to the Database!");
                   Statement stmt=con.createStatement();
                   
                   String sql="delete from user_sdo_geom_metadata";
                   System.out.println("Executing query:'"+sql+"'");
                   //PreparedStatement stmt=con.prepareStatement(sqlInsert);
                   stmt.executeUpdate(sql);
                   
                   sql="insert into user_sdo_geom_metadata values('"+tableName+"','"+colName+"',sdo_dim_array(sdo_dim_element('x',0,10000,0.5),sdo_dim_element('y',0,10000,0.5)),32774)";
                   System.out.println("Executing query:'"+sql+"'");
                   stmt.executeUpdate(sql);
                   
                   sql="delete from user_sdo_index_metadata";
                   System.out.println("Executing query:'"+sql+"'");
                   stmt.executeUpdate(sql);
                   
                   sql="drop index cad_index";
                   System.out.println("Executing query:'"+sql+"'");
                   stmt.executeUpdate(sql);
                   
                   sql="create index cad_index on cad(geom) indextype is mdsys.spatial_index";
                   System.out.println("Executing query:'"+sql+"'");
                   stmt.executeUpdate(sql);
                   
                   stmt.close();
                   con.close();  
               } catch (ClassNotFoundException ex) {
                   Logger.getLogger(Readdxf01.class.getName()).log(Level.SEVERE, null, ex);
               }
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        // TODO code application logic here
       File file=new File(fileName);
       FileReader fr=new FileReader(file);
       BufferedReader bfr= new BufferedReader(fr);
       System.out.println("here!");  
       String bz=new String("AXIS");//标注线（包括轴网标注的圆以及引伸它的直线）
       String zx=new String("DOTE");//红色轴线
       String bk=new String("加粗线");//最外边的边框矩形
       String tk=new String("图框");//右下角的图信息及作者信息边框
       Scanner reader=new Scanner(System.in);
       //StringBuffer temps=new StringBuffer();
       String temps=null;
       //String test="hello"; test=temps;
       System.out.print("输入标注线图层：");
       if((temps=reader.nextLine()).trim().compareTo("")!=0) bz=new String(temps);
       filter.add(bz);
       System.out.print("输入轴线图层：");
       if((temps=reader.nextLine()).trim().compareTo("")!=0) zx=new String(temps);
       filter.add(zx);
       System.out.print("输入边框线图层：");
       if((temps=reader.nextLine()).trim().compareTo("")!=0) bk=new String(temps);
       filter.add(bk);
       System.out.print("输入右下角信息边框图层：");
       if((temps=reader.nextLine()).trim().compareTo("")!=0) tk=new String(temps);
       filter.add(tk);
       while(true){
         System.out.print("输入其他图层或结束(q)：");
         temps=reader.nextLine();
         if(temps.compareTo("q")==0) break;
         if(temps.trim().compareTo("")!=0) filter.add(temps);
       }
       //输入提取门所需图层名
       while(true){
         System.out.print("输入门所在图层或结束(q)：");
         temps=reader.nextLine();
         if(temps.compareTo("q")==0) break;
         if(temps.trim().compareTo("")!=0) mentc.add(temps);
       }
       
       readBlocks(bfr);
       readEntities(bfr);
       //createindex(); //创建索引。  感觉还是可以放在SQL文件里，因为创建数据库表还是要执行SQL文件的。在这里执行，如果索引不存在，drop index句就会异常。
       System.out.println("There!");
       bfr.close();
       fr.close();
    }
}



    /*public static void test_rdBlocks() throws IOException{
      File file=new File("test_rdBlocks.txt");
      FileWriter fr=new FileWriter(file);
      BufferedWriter bfw= new BufferedWriter(fr);
      for(int i=0;i<kname.size();++i){
        bfw.write("kuai-name:");//bfw.newLine();
        bfw.write(kname.get(i));
        bfw.newLine();
        for(int j=0;j<lst.get(i).size();++j){
          if(lst.get(i).getClass()==Line.class){
           Line templn=(Line)lst.get(i).get(j);
           bfw.write("10");
           bfw.newLine();
           bfw.write(""+templn.qd.x);
           bfw.newLine();
          }
        }
      }
    }*/