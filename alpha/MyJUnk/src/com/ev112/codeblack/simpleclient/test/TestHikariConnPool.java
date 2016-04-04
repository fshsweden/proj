package com.ev112.codeblack.simpleclient.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class TestHikariConnPool {

	private final int RUNS = 2000;
	private int successes = 0;
	private int failures = 0;
	private HikariDataSource ds;

	private synchronized Connection getPooledConnection() throws SQLException {
		return ds.getConnection();
	}

	/**
	 * 
	 * @author peterandersson
	 *
	 */
	private class Job implements Runnable {

		private final int number;
		private volatile boolean done = false;
		private Connection conn;

		public Job(final Connection conn, final int number) {
			this.number = number;
			this.conn = conn;
		}

		@Override
		public void run() {
			try {
				PreparedStatement stmt = null;
				ResultSet rs = null;
				try {
					long startTime = System.nanoTime();

					stmt = conn.prepareStatement("SELECT DISTINCT DT FROM zdat_trades ORDER BY DT");
					rs = stmt.executeQuery();
					while (rs.next()) {
						String dt = rs.getString("dt");
					}

					long endTime = System.nanoTime();
					long duration = (endTime - startTime) / 1000000; // milliseconds.

					System.out.println("Job #" + number + " executed OK in "	+ duration + " ms ");

					successes++;
					done = true;
					
					conn.close(); // CLOSE CONNECTION!!!

				} catch (SQLException e) {
					System.out.println(e.getLocalizedMessage());
					failures++;
				} finally {
					if (rs != null) {
						rs.close();
					}
					if (stmt != null) {
						stmt.close();
					}
				}
			} catch (Exception ex) {
				System.out.println("Exception:" + ex.getLocalizedMessage());
			}
		}

		public boolean isDone() {
			return done;
		}

		public Integer getNumber() {
			return number;
		}
	}

	public TestHikariConnPool() {

		HikariConfig config = new HikariConfig();
		config.setMaximumPoolSize(100);

		config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		config.addDataSourceProperty("serverName", "192.168.0.199");
		config.addDataSourceProperty("port", "3306");
		config.addDataSourceProperty("databaseName", "alpha");
		config.addDataSourceProperty("user", "alpha");
		config.addDataSourceProperty("password", "alpha");

		config.setConnectionTimeout(30000);
		HikariDataSource pooledDataSource = new HikariDataSource(config);

		// ******************************************
		for (int i = 0; i < 800; i++) {
			try {
				System.out.println("Trying to get connection #" + i);
				long startTime = System.nanoTime();
				Connection c = pooledDataSource.getConnection();
				long endTime = System.nanoTime();
				long duration = (endTime - startTime) / 1000000; // milliseconds.
				System.out.println("Successfully got connection #" + i + " it took " + duration + " ms");
				Job j = new Job(c, i);
				Thread t = new Thread(j);
				t.start();
			} catch (Exception ex) {
				System.out.println(">>>>>>>>>>>>>>>>>>>> Exception:" + ex.getLocalizedMessage());
				System.out.println("                 RE-TRYING!");
				i--;
			}
		}

		// ******************************************
		try {
			Thread.sleep(10000);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		// ******************************************

		System.out.println(successes + "/" + failures);
		
		try {
			Thread.sleep(600000);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		pooledDataSource.close();
	}

	public static void main(String[] args) {
		new TestHikariConnPool();
	}
}
