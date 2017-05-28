package com.zzr.myNeo4j;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class App {
	private Connection conn = null;
	private Statement stmt = null;
	private String curDisease = null;   // 当前处理的疾病
	private int number = 0;
	private List<RelationShips> reList = new LinkedList<RelationShips>();
	
	@Before
	public void before() throws Exception {
		conn = JdbcUtil.getConnection();
		stmt = conn.createStatement();
	}
	
	@After
	public void after() throws Exception {
    	JdbcUtil.close(conn, stmt);
	}
	
	@Test
	public void parseXmlToNeo4j() throws Exception {
		// 1.创建一个xml解析器对象
	    SAXReader reader = new SAXReader();
	    
	    // 2.读取xml文档，返回Document对象
	    Document doc = reader.read(new File("./src/main/java/com/zzr/myNeo4j/data.xml"));
	    
	    // 3.得到根标签，diseases
	    Element rootElem = doc.getRootElement();
	    
	    // 4.迭代diseases下的每一个disease
	    @SuppressWarnings("unchecked")
		Iterator<Element> it = rootElem.elementIterator("disease");
	    
	    StringBuffer sb = null;
	    while (it.hasNext()) {
	    	reList.clear();
	    	sb = new StringBuffer("");
	    	
	    	sb.append("CREATE (d" + number + ":disease { ");
	    	firstFloor(it.next(), sb);
	    	sb.append(" })");
	    	
	    	// System.out.println(sb.toString());
	    	stmt.executeQuery(sb.toString());
	    	
	    	for (RelationShips rs : reList) {
	    		if (rs.getAttr() != null) {
	    			stmt.executeQuery("START a=node(*), b=node(*) WHERE a.name='" + rs.getFrom() + 
	    				"' AND b.name='" + rs.getTo() + "' CREATE (a)-[:" + rs.getRe() + "{content:'null'}]->(b)");
	    		} else {
	    			stmt.executeQuery("START a=node(*), b=node(*) WHERE a.name='" + rs.getFrom() + 
		    				"' AND b.name='" + rs.getTo() + "' CREATE (a)-[:" + rs.getRe() + "{content:'" + rs.getAttr() + "'}]->(b)");
	    		}
	    	}
	    	
	    	number++;
	    }
	}
	
	/**
	 * 处理name、properties和只有一层的标签
	 */
	@SuppressWarnings("unchecked")
	private void firstFloor(Element elem, StringBuffer sb) throws Exception {
		List<Element> list = elem.elements();
		
	    for (Element el : list) {
	    	if ("name".equals(el.getName())) {
	    		curDisease = el.getText();
	    		sb.append("name:'" + el.getText() + "'");
	    		
	    	} else if ("properties".equals(el.getName())) {
	    		List<Element> propList = el.elements();
	    		for (Element propEl : propList) {
	    			sb.append(", " + propEl.getName() + ":'" + propEl.getText() + "'");
	    		}
	    		
	    	} else {
	    		// 如果标签只有一层，则当做属性处理；否则抽成实体对象
	    		List<Element> childList = el.elements();
	    		
	    		if (childList.size() == 0) {
	    			sb.append(", " + el.getName() + ":'" + el.getText().trim() + "'");
	    			
	    		} else {
	    			otherFloor(el, sb);
	    		}
	    	}
	    }
	}
	
	/**
	 * @param disease下的直接子节点，如【病因】
	 * 将多层的抽成实体节点
	 */
	@SuppressWarnings("unchecked")
	private void otherFloor(Element elem, StringBuffer sb) throws Exception {
		List<Element> list = elem.elements();
		
		for (Element el : list) {
			List<Element> oneList = el.elements();
			
			// 先判断节点是否已经存在
			if (!isExisted(el.getName()))
				// System.out.println("CREATE ({ name: '" + el.getName() + "' })");
				stmt.executeQuery("CREATE ({ name: '" + el.getName() + "' })");
			
			if (oneList.size() > 0) {
				// 处理边1
				add(curDisease, el.getName(), elem.getName(), null);
				
				for (Element oneEl : oneList) {
					List<Element> twoList = oneEl.elements();
					
					// 先判断节点是否已经存在
					if (!isExisted(oneEl.getName()))
						// System.out.println("CREATE ({ name: '" + oneEl.getName() + "' })");
						stmt.executeQuery("CREATE ({ name: '" + oneEl.getName() + "' })");
					
					if (twoList.size() > 0) {
						// 处理边2
						add(el.getName(), oneEl.getName(), "include", null);
						
						for (Element twoEl : twoList) {
							
							// 先判断节点是否已经存在
							if (!isExisted(twoEl.getName()))
								// System.out.println("CREATE ({ name: '" + twoEl.getName() + "' })");
								stmt.executeQuery("CREATE ({ name: '" + twoEl.getName() + "' })");
							
							// 处理边3
							add(oneEl.getName(), twoEl.getName(), "include", twoEl.getText().trim());
						}
					} else {
						// 处理边2
						add(el.getName(), oneEl.getName(), "include", oneEl.getText().trim());
						
					}
				}
				
			} else {
				// 处理边1
				add(curDisease, el.getName(), elem.getName(), el.getText().trim());
			}
		}
	}
	
	/**
	 * 判断节点是否已经存在
	 */
	private boolean isExisted(String name) throws Exception {
		ResultSet rs = stmt.executeQuery("START n=node(*) WHERE n.name='" + name + "' RETURN n");
		return rs.next();
	}
	
	/**
	 * 添加边到集合中
	 */
	private void add(String from, String to, String re, String attr) throws Exception {
		RelationShips rs = new RelationShips(from, to, re, attr);
		reList.add(rs);
	}
}
