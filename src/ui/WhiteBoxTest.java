package ui;


import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import basis.DirectedGraph;
import basis.Vertex;
import ui.Interface;

import java.lang.reflect.Field;

public class WhiteBoxTest {
    Interface anInterface1;
    Interface anInterface2;

    @Before
    public void setUp() throws Exception {
        anInterface1 = new Interface();

        // 使用反射访问和初始化graph字段
        Field graphField = Interface.class.getDeclaredField("graph");
        graphField.setAccessible(true);  // 允许访问private字段

        // 创建并设置新的DirectedGraph对象
        DirectedGraph graph = new DirectedGraph();
        graphField.set(anInterface1, graph);

        // 添加顶点和边
        graph.addVertex("to");
        graph.addVertex("explore");
        graph.addVertex("strange");
        graph.addVertex("new");
        graph.addVertex("worlds");
        graph.addVertex("seek");
        graph.addVertex("out");
        graph.addVertex("life");
        graph.addVertex("and");
        graph.addVertex("civilizations");

        graph.addEdge("to", "explore");
        graph.addEdge("explore", "strange");
        graph.addEdge("strange", "new");
        graph.addEdge("new", "worlds");
        graph.addEdge("to", "seek");
        graph.addEdge("seek", "out");
        graph.addEdge("out", "new");
        graph.addEdge("new", "life");
        graph.addEdge("life", "and");
        graph.addEdge("and", "new");
        graph.addEdge("new", "civilizations");
        graph.addEdge("worlds", "to");

        anInterface2 = new Interface();
        // 使用反射访问和初始化graph字段
        Field graphField2 = Interface.class.getDeclaredField("graph");
        graphField2.setAccessible(true);  // 允许访问private字段

        // 创建并设置新的DirectedGraph对象
        DirectedGraph graph2 = new DirectedGraph();
        graphField.set(anInterface2, graph2);
    }

    /**
     * 1. startName 和 endName 均不存在
     * 预期输出：return "No " + startName + " or " + endName + " in the graph!"
     */
    @Test
    public void testNameBothNotExist() {
        String startName = "A";
        String endName = "B";
        String result = anInterface1.calcShortestPath(startName, endName);
        assertEquals("No " + startName + " or " + endName + " in the graph!", result);
    }

    /**
     * 2. startName 不存在，但 endName 存在
     * 预期输出：return "No " + startName + " or " + endName + " in the graph!"
     */
    @Test
    public void testNoStartName() {
        String startName = "A";
        String endName = "to";
        String result = anInterface1.calcShortestPath(startName, endName);

        assertEquals("No " + startName + " or " + endName + " in the graph!", result);
    }

    /**
     * 3. startName 存在，但 endName 不存在
     * 预期输出：return "No " + startName + " or " + endName + " in the graph!"
     */
    @Test
    public void testNoEndName() {
        String startName = "to";
        String endName = "B";
        String result = anInterface1.calcShortestPath(startName, endName);

        assertEquals("No " + startName + " or " + endName + " in the graph!", result);
    }

    /**
     * 4. startName 和 endName 均存在，且存在最短路径
     * 预期输出："The length of the shortest path is " + distance[i]
     */
    @Test
    public void testNameExistPathExist() {
        String startName = "to";
        String endName = "new";

        String result = anInterface1.calcShortestPath(startName, endName);

        assertEquals("The length of the shortest path is 3" , result);
    }


    /**
     * 5. 顶点集 vertice为空
     * 预期输出：return "No " + startName + " or " + endName + " in the graph!"
     */
    @Test
    public void testEmptyVertice() {

        String startName = "A";
        String endName = "B";
        String result = anInterface2.calcShortestPath(startName, endName);
        assertEquals("No " + startName + " or " + endName + " in the graph!", result);
    }

    /**
     * 6. startName 和 endName 均存在，但不存在最短路径
     * 预期输出："No path from " + startName + " to " + endName + "!"
     */
    @Test
    public void testNameExistPathNotExist() {
        String startName = "civilizations";
        String endName = "new";
        String result = anInterface1.calcShortestPath(startName, endName);

        assertEquals("No path from " + startName + " to " + endName + "!", result);
    }




}
