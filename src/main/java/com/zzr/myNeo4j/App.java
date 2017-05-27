package com.zzr.myNeo4j;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class App {
	private Connection conn = null;
	private Statement stmt = null; 
	
	/*ResultSet rs = stmt.executeQuery("create (n0:Person { name: 'S' }),(n1:Person { name: 'A1' }),(n2:Person { name: 'A2' }),(n3:Person { name: 'A3' }),(n4:Person { name: 'A4' }),(n5:Person { name: 'A5' }),(m1:Person { name: 'B1' }),(m2:Person { name: 'B2' }),(m3:Person { name: 'B3' }),(m0:Person { name: 'D' }),(n0)-[:KNOWS]->(n1),(n1)-[:KNOWS]->(n2),(n2)-[:KNOWS]->(n3),(n3)-[:KNOWS]->(n4),(n4)-[:KNOWS]->(n5),(n5)-[:KNOWS]->(m0),(n0)-[:KNOWS]->(m1),(m1)-[:KNOWS]->(m2),(m2)-[:KNOWS]->(m3),(m3)-[:KNOWS]->(m0)");
	while (rs.next()) {
		System.out.println(rs.getString("n"));
	}*/
	
	@Before
	public void before() {
		conn = JdbcUtil.getConnection();
    	try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	System.out.println(conn);
	}
	
	@After
	public void after() {
    	JdbcUtil.close(conn, stmt);
	}
	
	@Test
	public void test() throws Exception {
		// 1.创建一个xml解析器对象
	    SAXReader reader = new SAXReader();
	    
	    // 2.读取xml文档，返回Document对象
	    Document doc = reader.read(new File("./src/main/java/com/zzr/myNeo4j/data.xml"));
	    
	    // 3.得到根标签
	    Element rootElem = doc.getRootElement();
	    
	    getChildNodes(rootElem);
	}
	
	/**
	 *  获取传入的标签下的所有子节点
	 */
	@SuppressWarnings("unchecked")
	private void getChildNodes(Element elem) {
	    System.out.println(elem.getName());
	    // 得到节点下的所有直接子节点对象(不仅仅是标签对象)
	    Iterator<Node> it = elem.nodeIterator();
	    while (it.hasNext()) {
	        Node node = it.next();
	        // 判断是否是标签节点
	        if (node instanceof Element) {
	            Element el = (Element)node;
	            getChildNodes(el);
	        }       
	    }
	}
}
