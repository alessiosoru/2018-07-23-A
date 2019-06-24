package it.polito.tdp.newufosightings.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.newufosightings.model.Confine;
import it.polito.tdp.newufosightings.model.Sighting;
import it.polito.tdp.newufosightings.model.State;

public class NewUfoSightingsDAO {

	public List<Sighting> loadAllSightings() {
		String sql = "SELECT * FROM sighting ";
		List<Sighting> list = new ArrayList<>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);	
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(new Sighting(res.getInt("id"), res.getTimestamp("datetime").toLocalDateTime(),
						res.getString("city"), res.getString("state"), res.getString("country"), res.getString("shape"),
						res.getInt("duration"), res.getString("duration_hm"), res.getString("comments"),
						res.getDate("date_posted").toLocalDate(), res.getDouble("latitude"),
						res.getDouble("longitude")));
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return list;
	}

	public List<State> loadAllStates() {
		String sql = "SELECT * FROM state ";
		List<State> result = new ArrayList<State>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				State state = new State(rs.getString("id"), rs.getString("Name"), rs.getString("Capital"),
						rs.getDouble("Lat"), rs.getDouble("Lng"), rs.getInt("Area"), rs.getInt("Population"),
						rs.getString("Neighbors"));
				result.add(state);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<State> loadAllStates(Map<String, State> stateIdMap) {
		String sql = "SELECT * FROM state ";
		List<State> result = new ArrayList<State>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				State state = new State(rs.getString("id"), rs.getString("Name"), rs.getString("Capital"),
						rs.getDouble("Lat"), rs.getDouble("Lng"), rs.getInt("Area"), rs.getInt("Population"),
						rs.getString("Neighbors"));
				result.add(state);
				stateIdMap.put(state.getId(), state);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<String> getShapesByYear(Integer anno) {
		String sql = "SELECT DISTINCT shape " + 
				"FROM sighting " + 
				"WHERE YEAR(DATETIME) = ? ";
		List<String> result = new ArrayList<String>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("shape"));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	/*
	 * NON POSSO FARE UNA QUERY UNICA
	 * SELECT neighbor.state1, neighbor.state2, COUNT(*) AS cnt\n" + 
				"FROM neighbor , sighting \n" + 
				"WHERE neighbor.state1 = sighting.state OR \n" + 
				"	neighbor.state2 = sighting.state AND \n" + 
				"	state1<state2 AND \n" + 
				"	year(sighting.DATETIME)= 2012 AND \n" + 
				"	shape = 'circle'\n" + 
				"GROUP BY state1, state2
				PERCHE' ELIMINEREI GLI STATI IN CUI NON SI HANNO
				AVVISTAMENTI DI QUEL TIPO IN QUELL'ANNO
				
				NEL GRAFO PRIMA COLLEGO TUTTI I CONFINI,
				POI CERCO I PESI CHE POSSONO ESSERE ANCHE ZERO
	 */

	public List<Confine> getConfini() {
		String sql = "SELECT * " + 
				"FROM neighbor " + 
				"WHERE state1<state2 ";
		List<Confine> result = new ArrayList<Confine>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add( new Confine(rs.getString("state1"), rs.getString("state2")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public Integer getPesoConfine(String state1, String state2, Integer anno, String forma) {
		String sql = "SELECT COUNT(*) AS cnt " + 
				"FROM sighting  " + 
				"WHERE sighting.state = ? OR  " + 
				"	sighting.state = ? AND " + 
				"	year(sighting.DATETIME)= ? AND " + 
				"	shape = ? ";
		Integer peso=0;

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, state1);
			st.setString(2, state2);
			st.setInt(3, anno);
			st.setString(4, forma);
			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				peso=rs.getInt("cnt");
			}

			conn.close();
			return peso;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Sighting> getAvvistammentiByYearShape(Integer anno, String forma) {
		String sql = "SELECT * FROM sighting "+ 
				"WHERE year(sighting.DATETIME)= ? AND " + 
				"	shape = ? ";
		List<Sighting> list = new ArrayList<>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);	
			st.setInt(1, anno);
			st.setString(2, forma);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(new Sighting(res.getInt("id"), res.getTimestamp("datetime").toLocalDateTime(),
						res.getString("city"), res.getString("state"), res.getString("country"), res.getString("shape"),
						res.getInt("duration"), res.getString("duration_hm"), res.getString("comments"),
						res.getDate("date_posted").toLocalDate(), res.getDouble("latitude"),
						res.getDouble("longitude")));
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return list;
	}

}
