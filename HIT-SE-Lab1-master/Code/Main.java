package Code;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Transform;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.*;
import javax.imageio.ImageIO;
import java.lang.Math;
import javafx.beans.binding.DoubleBinding;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import javafx.scene.control.TextArea;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;

/**
 * JavaFX主应用程序
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BaseWindow.fxml"));
        Parent root = loader.load();
        //获取主窗口上的控件对象
        MenuItem saveMI = (MenuItem)loader.getNamespace().get("saveMenuItem");
        Button textBT = (Button)loader.getNamespace().get("textButton");
        Button showBT = (Button)loader.getNamespace().get("showButton");
        Button queryBT = (Button)loader.getNamespace().get("queryButton");
        Button generateBT = (Button)loader.getNamespace().get("generateButton");
        Button pathBT = (Button)loader.getNamespace().get("pathButton");
        Button walkBT = (Button)loader.getNamespace().get("walkButton");
        //将暂时不可用的控件设置为不可用
        saveMI.setDisable(true);
        textBT.setDisable(true);
        showBT.setDisable(true);
        queryBT.setDisable(true);
        generateBT.setDisable(true);
        pathBT.setDisable(true);
        walkBT.setDisable(true);
        Scene scene = new Scene(root);
        stage.setTitle("软件工程实验一");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

/**
 * 有向图结点类
 */
class Vertex {
    public String name;
    public HashSet<Vertex> predecessors;
    public HashSet<Vertex> successors;
    public HashMap<Vertex, Integer> weights;

    public Vertex() {
        this.name = null;
        this.predecessors = new HashSet<>();
        this.successors = new HashSet<>();
        this.weights = new HashMap<>();
    }

    public Vertex(String name) {
        this.name = name;
        this.predecessors = new HashSet<>();
        this.successors = new HashSet<>();
        this.weights = new HashMap<>();
    }

    public boolean equals(Vertex v) {
        return this.name.equals(v.name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}

class Arrow extends Group {
    private Line line;  //箭头的直线部分

    private static final double arrow_size = 8;    //箭头大小

    public Arrow(Line line, int weight) {
        this(line, new Polygon(), new Text(String.valueOf(weight)));
    }

    /**
     *
     * @param line 箭头的直线部分
     * @param triangle 箭头的三角形部分
     * @param text 箭头的标签部分（权值）
     */
    private Arrow(Line line, Polygon triangle, Text text) {
        super(line, triangle, text);    //超类Group的构造
        this.line = line;

        double sxInit = getStartX();    //获得直线的起点x坐标
        double syInit = getStartY();    //获得直线的起点y坐标
        double exInit = getEndX();      //获得直线的终点x坐标
        double eyInit = getEndY();      //获得直线的终点y坐标

        double dxInit = exInit - sxInit;    //横坐标差
        double dyInit = eyInit - syInit;    //纵坐标差
        double angleInit = Math.atan2(dyInit, dxInit);  //直线角度

        Transform transInit = Transform.translate(exInit, eyInit);  //初始化变换tansInit,将其x坐标偏移设置为直线的终点x坐标，y坐标偏移设置为直线的终点y坐标
        transInit = transInit.createConcatenation(Transform.rotate(Math.toDegrees(angleInit), 0, 0));   //设置绕点(0,0)旋转一定的角度（直线的角度）
        triangle.getPoints().clear();   //清空三角形中原有的点
        triangle.getPoints().addAll(    //将三角形初始位置设置在点(0,0)处
                0.0, 0.0,
                - arrow_size, arrow_size / 2,
                - arrow_size, - arrow_size / 2);
        triangle.getTransforms().clear();   //清空三角形已有的变换
        triangle.getTransforms().add(transInit);    //将为三角形添加变换transInit
        triangle.setFill(Color.PURPLE);     //设置三角形颜色为紫色

        text.setWrappingWidth(40);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setTextOrigin(VPos.CENTER);
        text.setFill(Color.PURPLE);
        text.setLayoutX((sxInit + exInit) / 2 - text.getWrappingWidth() / 2);   //设置文本x坐标为直线的中点x坐标
        text.setLayoutY((syInit + eyInit) / 2 - text.getScaleY());  //设置文本y坐标为直线的中点y坐标

        //初始化属性监听器updater，用于在直线的位置属性变化时更新三角形与文本的位置属性，以便保持箭头形状
        InvalidationListener updater = o -> {
            double sx = getStartX();
            double sy = getStartY();
            double ex = getEndX();
            double ey = getEndY();

            double dx = ex - sx;
            double dy = ey - sy;
            double angle = Math.atan2(dy, dx);

            //更新过程与初始化计算方法相同
            Transform transform = Transform.translate(ex, ey);
            transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
            triangle.getPoints().clear();
            triangle.getPoints().addAll(
                    0.0, 0.0,
                    - arrow_size, arrow_size / 2,
                    - arrow_size, - arrow_size / 2);
            triangle.getTransforms().clear();
            triangle.getTransforms().add(transform);
            triangle.setFill(Color.PURPLE);

            text.setLayoutX((sx + ex) / 2 - text.getWrappingWidth() / 2);
            text.setLayoutY((sy + ey) / 2 - text.getScaleY());
        };

        //将监听器updater添加到直线的各位置属性
        startXProperty().addListener(updater);
        startYProperty().addListener(updater);
        endXProperty().addListener(updater);
        endYProperty().addListener(updater);
    }

    /**
     *
     * @param value 箭头的颜色
     */
    public void setStroke(Paint value) {
        this.line.setStroke(value);
    }

    /**
     *
     * @param value 箭头的直线宽度
     */
    public void setStrokeWidth(double value) {
        this.line.setStrokeWidth(value);
    }

    /**
     *
     * @param value 为直线的起点x坐标设置的值
     */
    public final void setStartX(double value) {
        line.setStartX(value);
    }

    /**
     *
     * @return 直线的起点x坐标
     */
    public final double getStartX() {
        return line.getStartX();
    }

    /**
     *
     * @return 直线的startX属性
     */
    public final DoubleProperty startXProperty() {
        return line.startXProperty();
    }

    /**
     *
     * @param value 为直线的起点y坐标设置的值
     */
    public final void setStartY(double value) {
        line.setStartY(value);
    }

    /**
     *
     * @return 直线的起点y坐标
     */
    public final double getStartY() {
        return line.getStartY();
    }

    /**
     *
     * @return 直线的startY属性
     */
    public final DoubleProperty startYProperty() {
        return line.startYProperty();
    }

    /**
     *
     * @param value 为直线的终点x坐标设置的值
     */
    public final void setEndX(double value) {
        line.setEndX(value);
    }

    /**
     *
     * @return 直线的终点x坐标
     */
    public final double getEndX() {
        return line.getEndX();
    }

    /**
     *
     * @return 直线的endX属性
     */
    public final DoubleProperty endXProperty() {
        return line.endXProperty();
    }

    /**
     *
     * @param value 为直线的终点y坐标设置的值
     */
    public final void setEndY(double value) {
        line.setEndY(value);
    }

    /**
     *
     * @return 直线的终点y坐标
     */
    public final double getEndY() {
        return line.getEndY();
    }

    /**
     *
     * @return 直线的endY属性
     */
    public final DoubleProperty endYProperty() {
        return line.endYProperty();
    }
}

/**
 * 有向图类
 */
class DirectedGraph {
    private ArrayList<Vertex> vertices;		//顶点表(动态数组)

    public DirectedGraph() {
        this.vertices = new ArrayList<>();
    }

    public ArrayList<Vertex> getVertices() {
        return this.vertices;
    }

    public int getVertexNumber() {
        return this.vertices.size();
    }

    public int getEdgeNumber() {
        int number = 0;
        for (Vertex v : vertices) {
            number += v.successors.size();
        }
        return number;
    }

    /**
     *
     * @param name 向有向图中添加的节点名称
     */
    public void addVertex(String name) {
        for (Vertex v : vertices) {
            if (v.name.equals(name)) {
                return;
            }
        }
        vertices.add(new Vertex(name));
    }

    /**
     *
     * @param head 向有向图中添加的边的起点名称
     * @param tail 向有向图中添加的边的终点名称
     */
    public void addEdge(String head, String tail) {
        Vertex a = new Vertex();
        Vertex b = new Vertex();
        for (Vertex v : vertices) {
            if (v.name.equals(head)) {
                a = v;
            }
            if (v.name.equals(tail)) {
                b = v;
            }
        }
        if (a.successors.contains(b)) {
            int weight = a.weights.get(b);
            a.weights.replace(b, weight + 1);
        } else {
            a.successors.add(b);
            b.predecessors.add(a);
            a.weights.put(b, 1);
        }
    }

    @Override
    public String toString() {
        String result = "";
        for (Vertex v : vertices) {
            result += v.toString() + ": " + v.successors.toString() + "\n";
        }
        return result;
    }

    @Override
    public int hashCode() {
        int code = 0;
        for (Vertex v : vertices) {
            code += v.hashCode();
        }
        return code;
    }
}

/**
 * 有向图的处理工具类
 */
class GraphProcessor {
    /**
     * 根据文件路径（名称）生成有向图
     * @param fileName 文件路径（名称）
     * @return 读取文件内容生成的有向图
     */
    public static DirectedGraph generateGraph(String fileName) {
        Scanner in;
        String pre, post;
        DirectedGraph graph = new DirectedGraph();
        try {
            in = new Scanner(new FileInputStream(fileName));
            do {
                pre = parseText(in.next());
            } while (pre == null && in.hasNext());
            if (pre != null) {
                graph.addVertex(pre);
            }
            while (in.hasNext()) {
                post = parseText(in.next());
                if (post != null ) {
                    graph.addVertex(post);
                    graph.addEdge(pre, post);
                    pre = post;
                }
            }
        } catch (FileNotFoundException e) {
            System.exit(0);
        }
        return graph;
    }

    /**
     * 处理字符串中的非法字符，返回满足格式要求的字符串
     * @param str 待处理的字符串
     * @return 处理后的满足格式要求的字符串
     */
    public static String parseText(String str) {
        StringBuffer sb = new StringBuffer();
        if (str != null) {
            for (int i = 0; i < str.length(); ++i) {
                char c = str.charAt(i);
                if (Character.isLetter(c)) {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }
        return (sb.toString().equals("")) ? null : sb.toString();
    }

    /**
     * 从有向图结点集合中随机选择一个结点
     * @param set 待选择的集合
     * @return 选择出的结点
     */
    public static Vertex randomSelect(Collection<Vertex> set) {
        if (set == null || set.size() == 0) {
            return null;
        }
        int item = new Random().nextInt(set.size());
        int i = 0;
        for (Vertex v : set) {
            if (i == item) {
                return v;
            }
            ++i;
        }
        return null;
    }
}

/**
 * 随机游走的线程控制类
 */
class RandomWalker implements Runnable {
    private TextArea area;				//随机游走控制面板的结果显示区域
    private ArrayList<Vertex> vertices;	//有向图的顶点列表
    private Thread thread;				//用于控制的线程
    private boolean suspended;			//用于标注线程是否暂停

    public RandomWalker(TextArea a, ArrayList<Vertex> l) {
        this.area = a;
        this.vertices = l;
    }

    /**
     * 线程的运行方法
     */
    @Override
    public void run() {
        HashMap<Vertex, HashSet<Vertex>> walkedVertices = new HashMap<>();	//用于记录已被随机游走过的有向边
        for (Vertex v : vertices) {
            walkedVertices.put(v, new HashSet<>());
        }
        Vertex pre = vertices.get(new Random().nextInt(vertices.size()));	//用于记录有向边起点
        Vertex next;	//用于记录有向边终点
        StringBuffer sb = new StringBuffer();	//随机游走结果字符串构造器
        sb.append(pre.name);
        area.setText(sb.toString());
        while (true) {
            try {
                Thread.sleep(1000);		//线程休眠一秒后运行
                //线程同步，若线程暂停则进入等待状态
                synchronized (this) {
                    while (suspended) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            next = GraphProcessor.randomSelect(pre.successors);	//随机选择pre结点的一个后继结点
            //若没有后继结点，则随机游走停止
            if (next == null) {
                break;
            }
            //更新显示随机游走结果
            sb.append(" " + next.name);
            area.setText(sb.toString());
            //若遇到已经游走过的有向边，则停止随机游走
            if (walkedVertices.get(pre).contains(next)) {
                break;
            }
            walkedVertices.get(pre).add(next);	//记录已经游走过的有向边
            pre = next;	//沿路径向前移动
        }
    }

    /**
     * 线程开始运行
     */
    public void start() {
        if(thread == null){
            thread = new Thread(this, "RandomWalkThread");
            thread.start();
        }
    }

    /**
     * 线程暂停
     */
    public void suspend() {
        suspended = true;
    }

    /**
     * 线程继续
     */
    public synchronized void resume() {
        suspended = false;
        notify();
    }
}

/**
 * 绘图面板上的顶点显示类，继承StackPane类目的是使顶点中的文本自动居中
 */
class PointBox extends StackPane {
    private DoubleProperty centerX = new SimpleDoubleProperty();	//顶点中心的X属性
    private DoubleProperty centerY = new SimpleDoubleProperty();	//顶点中心的y属性

    public PointBox() {
        super();
        //顶点中心x属性=左上角x位置属性+顶点宽度/2，顶点中心y属性=左上角y位置属性+顶点高度/2
        centerX.bind(this.layoutXProperty().add(this.prefWidthProperty().divide(2)));
        centerY.bind(this.layoutYProperty().add(this.prefHeightProperty().divide(2)));
    }

    public final double getCenterX() {
        return this.centerX.get();
    }

    public final void setCenterX(double value) {
        this.centerX.set(value);
    }

    public final double getCenterY() {
        return this.centerY.get();
    }

    public final void setCenterY(double value) {
        this.centerY.set(value);
    }

    public DoubleProperty centerXProperty() {
        return centerX;
    }

    public DoubleProperty centerYProperty() {
        return centerY;
    }

    /**
     * 获取绘图顶点对象内部的Circle形状对象
     * @return
     */
    public Circle getCircle() {
        for (Node node : this.getChildren()) {
            if (node instanceof Circle) {
                return (Circle)node;
            }
        }
        return null;
    }

    /**
     * 获取绘图顶点对象内部的Text文本对象
     */
    public Text getText() {
        for (Node node : this.getChildren()) {
            if (node instanceof Text) {
                return (Text)node;
            }
        }
        return null;
    }
}



/**
 * 主窗口控制类
 */
class BaseWindowController {
    @FXML private MenuBar menuBar;				//菜单栏
    @FXML private MenuItem saveMenuItem;		//“另存为”菜单项

    @FXML private Button textButton;			//“查看源文本”按钮
    @FXML private Button showButton;			//“展示有向图”按钮
    @FXML private Button queryButton;			//“查询桥接词”按钮
    @FXML private Button generateButton;		//“生成新文本”按钮
    @FXML private Button pathButton;			//“求最短路径”按钮
    @FXML private Button walkButton;			//“随机游走”按钮

    @FXML private ScrollPane canvasContainer;	//画布面板的容器面板
    @FXML private AnchorPane canvasPane;		//画布面板，用于画有向图
    @FXML private TextArea console;				//控制台，用于显示各种信息
    @FXML private StackPane stackPane;			//控制按钮面板的容器

    private DirectedGraph graph;				//有向图
    private File dataFile;						//源文本文件对象

    private static HashMap<String, PointBox> points = new HashMap<>();				//<顶点名称,绘图面板中对应的绘图顶点>
    private static HashMap<Arrow, Pair<String, String>> edges = new HashMap<>();	//<绘图有向边,<起点名称,终点名称>>
    private static final double radius = 25;	//绘图顶点的半径

    private static int[] distance;				//记录各结点到某个结点的最短路径长度
    private static final int infinity = Integer.MAX_VALUE / 2;	//路径长的无穷大值

    /**
     * “打开”菜单项被点击时的事件处理方法
     */
    @FXML
    protected void handleOpenMenuItemClicked(ActionEvent e) {
        Stage stage = (Stage)menuBar.getScene().getWindow();//文件选择窗口
        FileChooser fileChooser = new FileChooser();		//文件选择器

        fileChooser.setTitle("打开文件");	//设置窗口标题
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));		//设置初始路径
        //设置文件格式过滤器
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("文本文档", "*.txt"),
                new FileChooser.ExtensionFilter("所有文件", "*.*"));
        File file = fileChooser.showOpenDialog(stage);	//以“打开文件”模式打开窗口，并使用file记录选择打开的文件对象
        dataFile = file;	//记录源文本文件对象
        if (file != null) {
            try (Scanner scan = new Scanner(file)) {
                String content = scan.useDelimiter("\\Z").next();	//使用文件结束标志作为分隔符以便读取文件全部内容
                console.setText(content);	//将文本内容显示到界面控制台
                graph = GraphProcessor.generateGraph(file.getAbsolutePath());	//生成有向图
            } catch (FileNotFoundException err) {
                err.printStackTrace();
            }
        }
        //成功生成有向图后，各功能控制按钮可用
        if (graph != null) {
            textButton.setDisable(false);
            showButton.setDisable(false);
            queryButton.setDisable(false);
            generateButton.setDisable(false);
            pathButton.setDisable(false);
            walkButton.setDisable(false);
        }
    }

    /**
     * “另存为”菜单项被点击时的事件处理方法
     */
    @FXML
    protected void handleSaveMenuItemClicked(ActionEvent e) {
        Stage stage = (Stage)menuBar.getScene().getWindow();//文件另存为窗口
        FileChooser fileChooser = new FileChooser();		//文件另存为器

        fileChooser.setTitle("保存图片");	//设置窗口标题
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));		//设置初始路径
        //设置文件格式过滤器
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG图像", "*.png"),
                new FileChooser.ExtensionFilter("JPG图像", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF图像", "*.gif"));
        File file = fileChooser.showSaveDialog(stage);	//以“另存为模式”打开窗口，并使用file记录选择保存的文件对象
        if (file != null) {
            String name = file.getName();	//获取文件保存路径
            String extendName = name.substring(name.lastIndexOf('.') + 1);	//获取文件拓展名
            WritableImage image = new WritableImage((int)canvasPane.getWidth(), (int)canvasPane.getHeight());	//初始化可写入图片
            try {
                //根据文件拓展名选择不同的图片保存格式
                switch(extendName) {
                    case "png":
                        SnapshotParameters spPNG = new SnapshotParameters();
                        spPNG.setFill(Color.WHITE);	//设置填充色
                        //以png格式将图片写入文件
                        ImageIO.write(SwingFXUtils.fromFXImage(canvasPane.snapshot(spPNG, image), null), "png", file);
                        break;
                    case "jpg":
                        SnapshotParameters spJPG = new SnapshotParameters();
                        spJPG.setFill(Color.WHITE);
                        ImageIO.write(SwingFXUtils.fromFXImage(canvasPane.snapshot(spJPG, image), null), "jpg", file);
                        break;
                    case "gif":
                        SnapshotParameters spGIF = new SnapshotParameters();
                        spGIF.setFill(Color.WHITE);
                        ImageIO.write(SwingFXUtils.fromFXImage(canvasPane.snapshot(spGIF, image), null), "gif", file);
                        break;
                    default:
                        break;
                }
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
    }

    /**
     * “关闭”菜单项被点击时的事件处理方法
     */
    @FXML
    protected void handleCloseMenuItemClicked(ActionEvent e) {
        Stage stage = (Stage)menuBar.getScene().getWindow();	//获取主窗口
        stage.close();	//关闭主窗口
    }

    /**
     * “查看源文件”按钮被点击时的事件处理方法
     */
    @FXML
    protected void handleTextButtonClicked(MouseEvent e) {
        if (dataFile != null) {
            try (Scanner scan = new Scanner(dataFile)) {
                String content = scan.useDelimiter("\\Z").next();	//使用源文件对象获取源文件内容
                console.setText(content);	//显示到控制台
            } catch (FileNotFoundException err) {
                err.printStackTrace();
            }
        }
    }

    /**
     * “展示有向图”按钮被点击时的事件处理方法
     */
    @FXML
    protected void handleShowButtonClicked(MouseEvent e) {
        points.clear();			//清空记录的绘图顶点
        canvasPane.getChildren().clear();	//清空绘图面板上的所有形状
        showDirectedGraph();	//展示有向图
        saveMenuItem.setDisable(false);		//有向图被画出后，“另存为”菜单项可用
    }

    /**
     * “查询桥接词”按钮被点击后的事件处理方法
     */
    @FXML
    protected void handleQueryButtonClicked(MouseEvent e) throws Exception {
        GridPane prePane = (GridPane)stackPane.getChildren().get(0);	//获取控制按钮面板
        //初始化查询桥接词面板
        FXMLLoader loader = new FXMLLoader(getClass().getResource("QueryPane.fxml"));
        GridPane pane = loader.load();
        TextField word1TF = (TextField)loader.getNamespace().get("word1TextField");
        TextField word2TF = (TextField)loader.getNamespace().get("word2TextField");
        Button returnBT = (Button)loader.getNamespace().get("returnButton");
        Button queryBT = (Button)loader.getNamespace().get("queryButton");
        //“返回”按钮被点击时，重新显示控制按钮面板
        returnBT.setOnMouseClicked(event -> {
            stackPane.getChildren().remove(pane);	//从容器面板中移除“查询桥接词”面板
            stackPane.getChildren().add(prePane);	//将控制按钮面板重新添加到容器面板
        });
        //“查询”按钮被点击时，查询桥接词并显示
        queryBT.setOnMouseClicked(event -> {
            String word1 = word1TF.getText().trim();	//第一个单词
            String word2 = word2TF.getText().trim();	//第二个单词
            //判断输入的合法性
            if (word1.equals("") && !word2.equals("")) {
                console.setText("请输入单词1！");
                return;
            } else if (!word1.equals("") && word2.equals("")) {
                console.setText("请输入单词2！");
                return;
            } else if (word1.equals("") && word2.equals("")) {
                console.setText("请输入单词1和单词2！");
                return;
            } else {
                String output = queryBridgeWords(word1, word2);
                console.setText(output);
            }
        });
        //在控制按钮区域显示查询桥接词面板
        stackPane.getChildren().remove(prePane);
        stackPane.getChildren().add(pane);
    }

    /**
     * “生成新文本”按钮被点击时的事件处理方法，面板显示方法同上
     */
    @FXML
    protected void handleGenerateButtonClicked(MouseEvent e) throws Exception {
        GridPane prePane = (GridPane)stackPane.getChildren().get(0);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GeneratePane.fxml"));
        GridPane pane = loader.load();
        TextField inputTF = (TextField)loader.getNamespace().get("inputTextField");
        Button returnBT = (Button)loader.getNamespace().get("returnButton");
        Button generateBT = (Button)loader.getNamespace().get("generateButton");
        //“返回”按钮被点击时，重新显示控制按钮面板
        returnBT.setOnMouseClicked(event -> {
            stackPane.getChildren().remove(pane);
            stackPane.getChildren().add(prePane);
        });
        //“生成”按钮被点击时，生成新文本并显示
        generateBT.setOnMouseClicked(event -> {
            String inputText = inputTF.getText().trim();
            if (inputText.equals("")) {
                console.setText("请输入新文本！");
                return;
            }
            String newText = generateNewText(inputText);
            console.setText(newText);
        });
        stackPane.getChildren().remove(prePane);
        stackPane.getChildren().add(pane);
    }

    /**
     * “求最短路径”按钮被点击时的事件处理方法，面板显示方法同上
     */
    @FXML
    protected void handlePathButtonClicked(MouseEvent e) throws Exception {
        GridPane prePane = (GridPane)stackPane.getChildren().get(0);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PathPane.fxml"));
        GridPane pane = loader.load();
        TextField word1TF = (TextField)loader.getNamespace().get("word1TextField");
        TextField word2TF = (TextField)loader.getNamespace().get("word2TextField");
        Button returnBT = (Button)loader.getNamespace().get("returnButton");
        Button yesBT = (Button)loader.getNamespace().get("yesButton");
        //“返回”按钮被点击时，重新显示控制按钮面板
        returnBT.setOnMouseClicked(event -> {
            stackPane.getChildren().remove(pane);
            stackPane.getChildren().add(prePane);
            for (Arrow edge : edges.keySet()) {
                edge.setStroke(Color.GREEN);
                edge.setStrokeWidth(1);
            }
        });
        //“确定”按钮被点击时，求最短路径并显示
        yesBT.setOnMouseClicked(event -> {
            for (Arrow edge : edges.keySet()) {
                edge.setStroke(Color.GREEN);
                edge.setStrokeWidth(1);
            }
            String word1 = word1TF.getText().trim();
            String word2 = word2TF.getText().trim();
            String result;
            if (!word1.equals("") && word2.equals("")) {
                Vertex end = GraphProcessor.randomSelect(graph.getVertices());
                result = calcShortestPath(word1, end.name);
            } else if (word1.equals("") && !word2.equals("")) {
                Vertex start = GraphProcessor.randomSelect(graph.getVertices());
                result = calcShortestPath(start.name, word2);
            } else if (word1.equals("") && word2.equals("")) {
                result = "The two words can't be both empty!";
            } else {
                result = calcShortestPath(word1, word2);
            }
            console.setText(result);
        });
        stackPane.getChildren().remove(prePane);
        stackPane.getChildren().add(pane);
    }

    /**
     * “随机游走”按钮被点击时的事件处理方法，面板显示方法同上
     */
    @FXML
    protected void handleWalkButtonClicked(MouseEvent e) throws Exception {
        GridPane prePane = (GridPane) stackPane.getChildren().get(0);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WalkPane.fxml"));
        GridPane pane = loader.load();
        TextArea resultTA = (TextArea) loader.getNamespace().get("resultTextArea");
        Button startBT = (Button) loader.getNamespace().get("startButton");
        Button suspendBT = (Button) loader.getNamespace().get("suspendButton");
        Button resumeBT = (Button) loader.getNamespace().get("resumeButton");
        Button returnBT = (Button) loader.getNamespace().get("returnButton");
        Button saveBT = (Button) loader.getNamespace().get("saveButton");
        RandomWalker walker = new RandomWalker(resultTA, graph.getVertices());
        //“开始”按钮被点击时，启动随机游走线程
        startBT.setOnMouseClicked(event -> {
            walker.start();
        });
        //“暂停”按钮被点击时，暂停随机游走线程
        suspendBT.setOnMouseClicked(event -> {
            walker.suspend();
        });
        //“继续”按钮被点击时，继续随机游走线程
        resumeBT.setOnMouseClicked(event -> {
            walker.resume();
        });
        //“返回”按钮被点击时，重新显示控制按钮面板
        returnBT.setOnMouseClicked(event -> {
            stackPane.getChildren().remove(pane);
            stackPane.getChildren().add(prePane);
        });
        //“保存”按钮被点击时，打开文本保存窗口保存文件
        saveBT.setOnMouseClicked((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("保存文本");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("文本文档", "*.txt"));
            File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());
            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(resultTA.getText());
                    writer.close();
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        });
        stackPane.getChildren().remove(prePane);
        stackPane.getChildren().add(pane);
    }

    /**
     * 绘图面板被双击时的事件处理方法
     */
    @FXML
    protected void handleCanvasPaneClicked(MouseEvent e) throws Exception {
        if (e.getClickCount() == 2) {	//判断点击次数是否为2
            //初始化图片显示面板
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ImageWindow.fxml"));
            StackPane root = loader.load();
            root.getChildren().clear();
            root.getChildren().add(canvasPane);	//将绘图面板添加到图片显示面板
            //初始化图片显示窗口
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("查看大图");
            stage.show();
            //当图片显示窗口被关闭时，将绘图面板重新添加到其在主窗口的容器中，以便显示
            stage.setOnCloseRequest(event -> {
                canvasContainer.setContent(null);
                canvasContainer.setContent(canvasPane);
            });
        }
    }

    /**
     * 展示有向图
     */
    private void showDirectedGraph() {
        points.clear();
        edges.clear();
        canvasPane.getChildren().clear();

        ArrayList<Vertex> vertices = this.graph.getVertices();	//获取有向图顶点列表
        String startName, endName;

        //该循环通过遍历有向图中的各有向边进行绘图
        for (Vertex v : vertices) {	//对每个顶点v
            PointBox start;		//用于记录绘图起点
            if (!points.keySet().contains(v.name)) {	//若顶点v尚未存在于绘图顶点中（即画出的有向图中没有该顶点）
                PointBox box = addPoint(v.name);	//添加该绘图顶点
                start = box;	//记录绘图起点
            } else {	//若顶点v已经存在于绘图顶点中（即画出的有向图中有该顶点）
                start = points.get(v.name);		//只记录绘图起点，不进行添加操作
            }
            startName = v.name;		//起点名称
            for (Vertex e : v.successors) {		//对顶点v的每个后继顶点e
                PointBox end;	//用于记录绘图终点
                if (!points.keySet().contains(e.name)) {	//若顶点e尚未存在于绘图顶点中（即画出的有向图中没有该顶点）
                    PointBox box = addPoint(e.name);	//添加该绘图顶点
                    end = box;	//记录绘图终点
                } else {	//若顶点e已经存在于绘图顶点中（即画出的有向图中有该顶点）
                    end = points.get(e.name);	//只记录绘图终点，不进行添加操作
                }
                endName = e.name;	//终点名称

                double dx = end.getCenterX() - start.getCenterX();	//绘图起点与终点的横坐标差
                double dy= end.getCenterY() - start.getCenterY();	//绘图起点与终点的纵坐标差
                double ds = Math.sqrt(dx * dx + dy * dy);			//绘图起点与终点的距离（即顶点圆心的距离）
                Line line = new Line();		//用于构造有向边的直线
                line.setStroke(Color.GREEN);	//设置直线颜色
                //设置直线的位置，使其位于绘图起点中心与绘图终点中心的连线上，且直线起点终点均位于圆上
                line.setStartX(start.getCenterX() + radius * dx / ds);
                line.setStartY(start.getCenterY() + radius * dy / ds);
                line.setEndX(end.getCenterX() - radius * dx / ds);
                line.setEndY(end.getCenterY() - radius * dy / ds);
                line.setMouseTransparent(true);
                //将直线的各位置属性与绘图起点、绘图终点的位置属性绑定，以便在拖动顶点时直线随之移动
                line.startXProperty().bind(
                        new DoubleBinding() {
                            {
                                super.bind(start.centerXProperty(), start.centerYProperty(), end.centerXProperty(), end.centerYProperty());
                            }
                            @Override
                            protected double computeValue() {
                                DoubleBinding x = end.centerXProperty().subtract(start.centerXProperty());
                                DoubleBinding y = end.centerYProperty().subtract(start.centerYProperty());
                                DoubleBinding s = new DoubleBinding() {
                                    {
                                        super.bind(x, y);
                                    }
                                    @Override
                                    protected double computeValue() {
                                        return Math.sqrt(Math.pow(x.get(), 2) + Math.pow(y.get(), 2));
                                    }
                                };
                                return start.centerXProperty().add(x.divide(s).multiply(radius)).get();
                            }
                        }
                );
                line.startYProperty().bind(
                        new DoubleBinding() {
                            {
                                super.bind(start.centerXProperty(), start.centerYProperty(), end.centerXProperty(), end.centerYProperty());
                            }
                            @Override
                            protected double computeValue() {
                                DoubleBinding x = end.centerXProperty().subtract(start.centerXProperty());
                                DoubleBinding y = end.centerYProperty().subtract(start.centerYProperty());
                                DoubleBinding s = new DoubleBinding() {
                                    {
                                        super.bind(x, y);
                                    }
                                    @Override
                                    protected double computeValue() {
                                        return Math.sqrt(Math.pow(x.get(), 2) + Math.pow(y.get(), 2));
                                    }
                                };
                                return start.centerYProperty().add(y.divide(s).multiply(radius)).get();
                            }
                        }
                );
                line.endXProperty().bind(
                        new DoubleBinding() {
                            {
                                super.bind(start.centerXProperty(), start.centerYProperty(), end.centerXProperty(), end.centerYProperty());
                            }
                            @Override
                            protected double computeValue() {
                                DoubleBinding x = end.centerXProperty().subtract(start.centerXProperty());
                                DoubleBinding y = end.centerYProperty().subtract(start.centerYProperty());
                                DoubleBinding s = new DoubleBinding() {
                                    {
                                        super.bind(x, y);
                                    }
                                    @Override
                                    protected double computeValue() {
                                        return Math.sqrt(Math.pow(x.get(), 2) + Math.pow(y.get(), 2));
                                    }
                                };
                                return end.centerXProperty().subtract(x.divide(s).multiply(radius)).get();
                            }
                        }
                );
                line.endYProperty().bind(
                        new DoubleBinding() {
                            {
                                super.bind(start.centerXProperty(), start.centerYProperty(), end.centerXProperty(), end.centerYProperty());
                            }
                            @Override
                            protected double computeValue() {
                                DoubleBinding x = end.centerXProperty().subtract(start.centerXProperty());
                                DoubleBinding y = end.centerYProperty().subtract(start.centerYProperty());
                                DoubleBinding s = new DoubleBinding() {
                                    {
                                        super.bind(x, y);
                                    }
                                    @Override
                                    protected double computeValue() {
                                        return Math.sqrt(Math.pow(x.get(), 2) + Math.pow(y.get(), 2));
                                    }
                                };
                                return end.centerYProperty().subtract(y.divide(s).multiply(radius)).get();
                            }
                        }
                );
                Arrow edge = new Arrow(line, v.weights.get(e));
                edges.put(edge, new Pair<>(startName, endName));
            }
        }
        //将绘图顶点、有向边的形状对象添加到绘图面板上以便显示
        canvasPane.getChildren().addAll(points.values());
        canvasPane.getChildren().addAll(edges.keySet());
    }

    /**
     * 创建新的绘图顶点
     * @param name 待添加绘图顶点名称
     * @return	绘图顶点图形对象
     */
    private PointBox addPoint(String name) {
        Random random = new Random();
        int canvasWidth = (int)canvasPane.getWidth();
        int canvasHeight = (int)canvasPane.getHeight();
        double x, y;

        //随机产生圆心坐标并判断该坐标是否合法，若不合法则重新产生直至合法
        do {
            x = (double)random.nextInt(canvasWidth);
            y = (double)random.nextInt(canvasHeight);
        } while(!isGoodCircleCenter(x, y));

        //初始化绘图顶点
        PointBox box = new PointBox();
        box.setLayoutX(x - radius);
        box.setLayoutY(y - radius);
        box.setPrefSize(radius * 2, radius * 2);
        box.setStyle("-fx-background-color: transparent");	//设置背景色为透明

        //初始化圆
        Circle circle = new Circle();
        circle.setRadius(radius);
        circle.setFill(Color.PINK);
        circle.setMouseTransparent(true);	//设置为对鼠标不可见

        //初始化文本（顶点名称）
        Text text = new Text(name);
        text.setWrappingWidth(radius * 2);
        text.setFill(Color.BLUE);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setTextOrigin(VPos.CENTER);
        text.setMouseTransparent(true);		//设置为对鼠标不可见

        box.getChildren().addAll(circle, text);
        //鼠标进入绘图顶点区域时，将顶点颜色变为黄色
        box.setOnMouseEntered(event -> box.getCircle().setFill(Color.YELLOW));
        //鼠标离开绘图顶点区域时，将顶点颜色恢复为粉色
        box.setOnMouseExited(event -> box.getCircle().setFill(Color.PINK));
        //鼠标拖动绘图顶点时，重新设置绘图顶点的位置属性使其跟随鼠标移动
        box.setOnMouseDragged(event -> {
            double eventX = event.getX() + box.getLayoutX();
            double eventY = event.getY() + box.getLayoutY();
            if (eventX - radius < 0) {
                box.setLayoutX(0);
            } else if (eventX + radius > canvasPane.getWidth()) {
                box.setLayoutX(canvasPane.getWidth() - radius * 2);
            } else {
                box.setLayoutX(eventX - radius);
            }
            if (eventY - radius < 0) {
                box.setLayoutY(0);
            } else if (eventY + radius > canvasPane.getHeight()) {
                box.setLayoutY(canvasPane.getHeight() - radius * 2);
            } else {
                box.setLayoutY(eventY - radius);
            }
        });
        //鼠标在绘图顶点区域按下时，将顶点颜色变为红色
        box.setOnMousePressed(event -> box.getCircle().setFill(Color.RED));
        //鼠标在绘图顶点区域释放时，将顶点颜色变为黄色
        box.setOnMouseReleased(event -> box.getCircle().setFill(Color.YELLOW));
        //记录新创建的绘图顶点
        points.put(name, box);
        return box;
    }

    /**
     * 判断一个绘图顶点的圆心坐标是否合法
     * @param x 待判断圆心坐标的x值
     * @param y 待判断圆心坐标的y值
     * @return 判断结果
     */
    private boolean isGoodCircleCenter(double x, double y) {
        //判断圆是否会与绘图面板边框相交
        if (x <= radius*2 || x >= canvasPane.getWidth() - radius*2 || y <= radius*2 || y >= canvasPane.getHeight() - radius*2) {
            return false;
        }
        //判断圆之间是否相交
        else {
            for (String point : points.keySet()) {
                double a = points.get(point).getCenterX();
                double b = points.get(point).getCenterY();
                if (Math.pow(a - x, 2) + Math.pow(b - y, 2) <= Math.pow(radius * 2, 2)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 查询word1与word2的桥接词
     * @param word1 第一个单词
     * @param word2 第二个单词
     * @return 桥接词查询结果
     */
    private String queryBridgeWords(String word1, String word2) {
        ArrayList<Vertex> vertices = this.graph.getVertices();	//获取有向图顶点列表
        Vertex a = null;
        Vertex b = null;
        //查找名称为word1和word2的顶点对象
        for (Vertex v : vertices) {
            if (v.name.equals(word1)) {
                a = v;
            }
            if (v.name.equals(word2)) {
                b = v;
            }
        }
        //未查找到顶点
        if (a == null && b != null) {
            return "No \"" + word1 + "\" in the graph!";
        } else if (a != null && b == null) {
            return "No \"" + word2 + "\" in the graph!";
        } else if (a == null && b == null) {
            return "No \"" + word1 + "\" and \"" + word2 + "\" in the graph!";
        }
        HashSet<Vertex> successors = a.successors;		//获取第一个单词的后继顶点集合
        HashSet<Vertex> predecessors = b.predecessors;	//获取第二个单词的前驱顶点集合
        HashSet<Vertex> intersection = new HashSet<>();	//初始化交集集合
        intersection.addAll(successors);
        intersection.retainAll(predecessors);			//取前驱集合与后继集合的交集
        //若交集集合中没有元素，则无桥接词
        if (intersection.size() == 0) {
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        }
        if (intersection.size() == 1) {
            String wordName = intersection.toString();
            return "The bridge word from \"" + word1 + "\" to \"" + word2 + "\" is: " + wordName.substring(1, wordName.length() - 1) + ".";
        }
        StringBuffer sb = new StringBuffer();
        int count = 0;
        for (Vertex v : intersection) {
            if (count < intersection.size() - 1) {
                sb.append(v.name + ", ");
            } else {
                sb.append("and " + v.name + ".");
            }
            ++count;
        }
        //返回查询结果
        return "The bridge words from \"" + word1  + "\" to \"" + word2 + "\" are: " + sb.toString();
    }

    /**
     * 插入桥接词生成新文本
     * @param inputText 源文本
     * @return	生成的新文本
     */
    private String generateNewText(String inputText) {
        String pre, post;
        Scanner scan = new Scanner(inputText);
        StringBuffer sb = new StringBuffer(inputText);
        HashSet<Vertex> bridgeWords;
        int fromIndex = 0;	//用于记录查找插入位置时的起始位置
        do {
            pre = GraphProcessor.parseText(scan.next());
        } while (pre == null && scan.hasNext());
        while (scan.hasNext()) {
            post = GraphProcessor.parseText(scan.next());
            if (post != null) {
                bridgeWords = getBridgeWords(pre, post);	//获取桥接词集合
                Vertex insertVertex = GraphProcessor.randomSelect(bridgeWords);	//随机选择插入的桥接词（结点）
                int insertIndex = sb.indexOf(post, fromIndex);	//获取插入位置
                if (insertVertex != null) {
                    sb.insert(insertIndex, insertVertex.name + " ");		//插入桥接词
                    fromIndex = insertIndex + insertVertex.name.length() + 1;	//更新起始位置
                }
                pre = post;
            }
        }
        scan.close();
        return sb.toString();
    }

    /**
     * 获取word1与word2的桥接词集合
     * @param word1 第一个单词
     * @param word2 第二个单词
     * @return 查找到的桥接词顶点集合
     */
    private HashSet<Vertex> getBridgeWords(String word1, String word2) {
        ArrayList<Vertex> vertices = this.graph.getVertices();
        Vertex a = null;
        Vertex b = null;
        for (Vertex v : vertices) {
            if (v.name.equals(word1)) {
                a = v;
            }
            if (v.name.equals(word2)) {
                b = v;
            }
        }
        if (a == null || b == null) {
            return null;
        }
        HashSet<Vertex> successors = a.successors;
        HashSet<Vertex> predecessors = b.predecessors;
        HashSet<Vertex> intersection = new HashSet<>();
        intersection.addAll(successors);
        intersection.retainAll(predecessors);
        return (intersection.size() == 0)? null : intersection;
    }

    /**
     * 求解并在绘图面板上标注最短路径，返回最短路径的长度信息
     * @param startName 路径起点名称
     * @param endName 路径终点名称
     * @return 最短路径的长度信息
     */
    private String calcShortestPath(String startName, String endName) {
        ArrayList<Vertex> vertices = this.graph.getVertices();
        Vertex startVertex = null;
        Vertex endVertex = null;
        for (Vertex v : vertices) {
            if (v.name.equals(startName) ) {
                startVertex = v;
            }
            if (v.name.equals(endName)) {
                endVertex = v;
            }
        }
        if (startVertex == null || endVertex == null) {
            return "No " + startName + " or " + endName + " in the graph!";
        }
        HashMap<Vertex, HashSet<Vertex>> path = dijkstra(endVertex);	//用于记录最短路径（实际上是一个有向图的子图，因为有向图可能有多条）
        int i = vertices.indexOf(startVertex);
        //若起点到终点的最短路径长度为无穷大，则没有最短路径
        if (distance[i] == infinity) {
            return "No path from " + startName + " to " + endName + "!";
        }
        //在绘图面板上展示最短路径
        showPath(startVertex, endVertex, path);
        //返回最短路径长度
        return "The length of the shortest path is " + distance[i];
    }

    /**
     * Dijkstra算法求最短路径
     * @param end 路径的终点
     * @return 最短路径子图
     */
    private HashMap<Vertex, HashSet<Vertex>> dijkstra(Vertex end) {
        boolean[] known = new boolean[graph.getVertexNumber()];	//记录结点是否已知
        distance = new int[graph.getVertexNumber()];			//记录各结点与终点的最短路径长度
        HashMap<Vertex, HashSet<Vertex>> path = new HashMap<>();//最短路径子图
        //初始化
        Arrays.fill(known, false);
        Arrays.fill(distance, infinity);
        ArrayList<Vertex> vertices = this.graph.getVertices();
        for (Vertex n : vertices) {
            path.put(n, new HashSet<>());
        }

        distance[vertices.indexOf(end)] = 0;
        Vertex b = end;
        ArrayList<Vertex> set = new ArrayList<>();	//保存下一步可能访问的未知结点，缩小结点搜索空间，优化Dijkstra算法性能
        while (true) {
            if (b == null) {
                break;
            }
            known[vertices.indexOf(b)] = true;
            set.remove(b);
            for (Vertex a : b.predecessors) {
                //若找到更近的路径，则更新原来的最短路径
                if (!known[vertices.indexOf(a)] && distance[vertices.indexOf(b)] + a.weights.get(b) < distance[vertices.indexOf(a)]) {
                    distance[vertices.indexOf(a)] = distance[vertices.indexOf(b)] + a.weights.get(b);
                    path.get(a).clear();
                    path.get(a).add(b);
                    set.add(a);
                }
                //若找到同样近的路径，则将该路径添加到最短路径子图中
                else if (!known[vertices.indexOf(a)] && distance[vertices.indexOf(b)] + a.weights.get(b) == distance[vertices.indexOf(a)]) {
                    path.get(a).add(b);
                    set.add(a);
                }
            }
            //搜索下次要访问的节点
            if (set.isEmpty()) {
                b = null;
            } else {
                Vertex min = set.get(0);
                for (Vertex n : set) {
                    if (distance[vertices.indexOf(n)] < distance[vertices.indexOf(min)]) {
                        min = n;
                    }
                }
                b = min;
            }
        }
        return path;
    }

    /**
     * 在绘图面板上通过DFS最短路径子图展示最短路径
     * @param start 路径起点
     * @param end 路径终点
     * @param path 最短路径子图
     */
    private void showPath(Vertex start, Vertex end, HashMap<Vertex, HashSet<Vertex>> path) {
        HashMap<Vertex, Boolean> visited = new HashMap<>();
        for (Vertex n : path.keySet()) {
            visited.put(n, false);
        }
        visited.replace(start, true);
        Stack<Vertex> stack = new Stack<>();
        Stack<Vertex> branch = new Stack<>();
        Vertex a = start;
        Vertex b;
        if (path.get(a).size() > 1) {
            for (int i = 0; i < path.get(a).size() - 1; ++i) {
                branch.push(a);
            }
        }
        for (Vertex v : path.get(a)) {
            stack.push(v);
        }
        Random rand = new Random();
        Color color = Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        while (!stack.empty()) {
            b = stack.pop();
            if (b == end || visited.get(b)) {
                visited.replace(b, true);
                if (!branch.empty()) {
                    a = branch.pop();
                }
                continue;
            }
            showEdge(a, b, color);
            visited.replace(b, true);
            if (path.get(b).size() > 1) {
                for (int i = 0; i < path.get(b).size() - 1; ++i) {
                    branch.push(b);
                }
            }
            for (Vertex v : path.get(b)) {
                if (v == end || visited.get(v)) {
                    showEdge(b, v, color);
                    color = Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
                }
                stack.push(v);
            }
            a = b;
        }
    }

    /**
     * 在绘图面板中用特定颜色标注一条有向边
     * @param start 有向边的起点
     * @param end 有向边的终点
     * @param color 用于标注的颜色
     */
    private void showEdge(Vertex start, Vertex end, Color color) {
        for (Arrow edge : edges.keySet()) {
            if (edges.get(edge).getKey().equals(start.name) && edges.get(edge).getValue().equals(end.name)) {
                edge.setStroke(color);
                edge.setStrokeWidth(3);
                return;
            }
        }
    }
}

