package com.zzr.myNeo4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	
	public static void main(String[] args) throws SQLException {
		Connection con = DriverManager.getConnection(
				"jdbc:neo4j:http://localhost:7474/", "neo4j", "z123456");

		// Querying
		Statement stmt = con.createStatement();
		
		ResultSet rs = stmt.executeQuery("START n=node(*) RETURN n");
		while (rs.next()) {
			System.out.println(rs.getString("n"));
		}
		
//		ResultSet rs = stmt.executeQuery("create (n0:Person { name: 'S' }),(n1:Person { name: 'A1' }),(n2:Person { name: 'A2' }),(n3:Person { name: 'A3' }),(n4:Person { name: 'A4' }),(n5:Person { name: 'A5' }),(m1:Person { name: 'B1' }),(m2:Person { name: 'B2' }),(m3:Person { name: 'B3' }),(m0:Person { name: 'D' }),(n0)-[:KNOWS]->(n1),(n1)-[:KNOWS]->(n2),(n2)-[:KNOWS]->(n3),(n3)-[:KNOWS]->(n4),(n4)-[:KNOWS]->(n5),(n5)-[:KNOWS]->(m0),(n0)-[:KNOWS]->(m1),(m1)-[:KNOWS]->(m2),(m2)-[:KNOWS]->(m3),(m3)-[:KNOWS]->(m0)");
//		while (rs.next()) {
//			System.out.println(rs.getString("n"));
//		}
	}
	
}
