package ui;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import basis.DirectedGraph;

public class BlackBoxTest {
    private Interface iface;

    @Before
    public void setUp() throws Exception {
        iface = new Interface();

        // 使用反射访问和初始化graph字段
        Field graphField = Interface.class.getDeclaredField("graph");
        graphField.setAccessible(true);  // 允许访问private字段

        // 创建并设置新的DirectedGraph对象
        DirectedGraph graph = new DirectedGraph();
        graphField.set(iface, graph);

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
    }

    @Test
    public void testSingleBridgeWord() {
        String result = iface.queryBridgeWords("to", "seek");
        assertEquals("No bridge words from \"to\" to \"seek\"!", result);
    }

    @Test
    public void testMultipleBridgeWords() {
        String result = iface.queryBridgeWords("to", "new");
        assertEquals("No bridge words from \"to\" to \"new\"!", result);
    }

    @Test
    public void testNoBridgeWords() {
        String result = iface.queryBridgeWords("life", "worlds");
        assertEquals("No bridge words from \"life\" to \"worlds\"!", result);
    }

    @Test
    public void testWord1NotInGraph() {
        String result = iface.queryBridgeWords("hello", "to");
        assertEquals("No \"hello\" in the graph!", result);
    }

    @Test
    public void testWord2NotInGraph() {
        String result = iface.queryBridgeWords("to", "hello");
        assertEquals("No \"hello\" in the graph!", result);
    }

    @Test
    public void testBothWordsNotInGraph() {
        String result = iface.queryBridgeWords("hello", "world");
        assertEquals("No \"hello\" and \"world\" in the graph!", result);
    }

    @Test
    public void testWord1IsNull() {
        String result = iface.queryBridgeWords("", "world");
        assertEquals("No \"\" and \"world\" in the graph!", result);
    }
}
