package fantastic4_1;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

//국경아 DB DAO 소스코드
public class PersonDAO {
	private Connection conn;
	private Statement stmt;
	private PreparedStatement pstmt;
	private PreparedStatement pstmt2;
	private PreparedStatement pstmt3;
	private ResultSet rs;

	String url = "jdbc:oracle:thin:@127.0.0.1:1521:XE";
	// String id = "ga";
	// String pw = "1234";
	String id = "fantastic4";
	String pw = "123456";


	// 사원 추가
	public int insertPerson(String name, String birthDate, String address, String phoneNum, String division,
			String position, String license, String accountNum, String joinDate) {

		String IdNum = setIdSequence(division)+".NEXTVAL,";//자동 시퀀스로 사번 생성하게 함****등록안됨
		
		System.out.println(IdNum);
		int num = -1;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// 해당 드라이버를 메모리에 업로드
			conn = DriverManager.getConnection(url, id, pw);
			String sql1 = "insert into PInfo values("+IdNum+",?,?,?,?,?,?,?,?,?)";
			System.out.println(division);

			pstmt = conn.prepareStatement(sql1);
			//pstmt.setString(1, IdNum); // 물음표 위치 번호, 1번부터 시작
			pstmt.setString(1, name);
			System.out.println(name);
			pstmt.setString(2, division);
			pstmt.setString(3, position);
			pstmt.setString(4, joinDate);
			pstmt.setString(5, birthDate);
			pstmt.setString(6, address);
			pstmt.setString(7, phoneNum);
			pstmt.setString(8, license);
			pstmt.setString(9, accountNum);
			
			num = pstmt.executeUpdate();
			
			
			PaymentDAO pdao = new PaymentDAO();
			//int basepay = position();
			String sql2 = "insert into Payment values(?,?)";
			pstmt2 = conn.prepareStatement(sql2);
			pstmt2.setString(1, IdNum+".curval");
			//pstmt2.setString(2, basepay);
			num = pstmt2.executeUpdate();
			

			String sql3 = "insert into monthrecord values(?)";
			pstmt3 = conn.prepareStatement(sql3);
			pstmt3.setString(1, IdNum+".curval");
			num = pstmt3.executeUpdate();
			

			//테이블들의 기본키인 사번이 입력된 열을 자동으로 생성되게 함

		} catch (ClassNotFoundException e) {// try에서 에러가 발생할 경우 catch실행
			e.printStackTrace(); // 오류를 콘솔창에 자세히 출력해줌
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// finally는 try 에러와 상관없이 실행
		finally { // 반드시 close 해야함, 닫을때는 나중에 열었던것부터 닫기
			try {
				if (pstmt3 != null)
					pstmt3.close();
				if (pstmt2 != null)
					pstmt2.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return num;
	}

	private String setIdSequence(String division) {
		String seq = "";
		switch(division){
		case "회계팀":
			seq = "seq_Account";
			break;
		case "영업1팀":
			seq = "seq_Sales";
			break;
		case "부설연구소":
			seq = "seq_Lap";
			break;
		case "사업제안팀":
			seq = "seq_Proposal";
			break;
		}
		return seq;
	}

	
	// 사원 삭제(바로 삭제 되게 설정함)
 	public int deletePerson(int IdNum) {
		int num = -1;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// 해당 드라이버를 메모리에 업로드
			conn = DriverManager.getConnection(url, id, pw);
			String sql1 = "delete PInfo where IdNum = ?";
			pstmt = conn.prepareStatement(sql1);
			pstmt.setInt(1, IdNum); // 해당 IdNum을 가진 사람 삭제

			num = pstmt.executeUpdate();

		} catch (ClassNotFoundException e) {// try에서 에러가 발생할 경우 catch실행
			e.printStackTrace(); // 오류를 콘솔창에 자세히 출력해줌
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return num;
	}

	// 사원 수정
	public int updatePerson(int IdNum, String address, String division, String position, String license,
			String accountNum, String phoneNum) {
		int num = -1;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// 해당 드라이버를 메모리에 업로드
			conn = DriverManager.getConnection(url, id, pw);
			String sql1 = "update PInfo set address = ?, phoneNum = ?, "
					+ "division = ?, postion = ?, license = ?, accountNum = ?" + "where IdNum = ?";
			pstmt = conn.prepareStatement(sql1);
			pstmt.setString(1, address);
			pstmt.setString(2, phoneNum);
			pstmt.setString(3, division);
			pstmt.setString(4, position);
			pstmt.setString(5, license);
			pstmt.setString(6, accountNum);
			pstmt.setInt(7, IdNum); // 해당 IdNum을 가진 사람 수정

			num = pstmt.executeUpdate();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return num;

	}

	// 검색기능
	// 경아가 제작한 검색기능을 입력값에 따라 다르게 실행되도록 오버로드함 , int IdNum, String name
	public ArrayList<PersonVO> searchPerson(int IdNum) {
		ArrayList<PersonVO> temp = new ArrayList<>();
		PersonVO vo = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, id, pw);
			String sql2 = "select * from PInfo where IdNum = ?";
			pstmt = conn.prepareStatement(sql2);

			pstmt.setInt(1, IdNum);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				String tempIdNum = rs.getString(1); // 2번째 있는 column을 꺼냄
				// select * 일 경우 getString(N) N번째 column을 꺼냄
				// select name, ppp 일경우 getString(1번은 name, 2번은 ppp)
				// select name// column이 하나일 경우는 getString(1)이면 됨
				if (IdNum == Integer.parseInt(tempIdNum)) {
					vo = new PersonVO(rs.getString(1));
					System.out.println(tempIdNum);
					temp.add(vo);
				}
			}

		} catch (ClassNotFoundException e) {
			// try에서 에러가 발생할 경우 catch실행
			e.printStackTrace(); // 오류를 콘솔창에 자세히 출력해줌
		} catch (SQLException e) { // DriverManager.getConnection(url,id,pw)에서
									// add.catch
			e.printStackTrace();
		} // finally는 try 에러와 상관없이 실행
		finally { // 반드시 close 해야함, 닫을때는 나중에 열었던것부터 닫기
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return temp;
	}

	public ArrayList<PersonVO> searchPerson(String name) {
		ArrayList<PersonVO> temp = new ArrayList<>();
		PersonVO vo = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, id, pw);
			String sql2 = "select * from PInfo where name = ?";
			pstmt = conn.prepareStatement(sql2);
			pstmt.setString(1, name);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				String tempName = rs.getString(2); // 2번째 있는 column을 꺼냄
				// select * 일 경우 getString(N) N번째 column을 꺼냄
				// select name, ppp 일경우 getString(1번은 name, 2번은 ppp)
				// select name// column이 하나일 경우는 getString(1)이면 됨
				if (name.equals(tempName)) {
					vo = new PersonVO(tempName);
					temp.add(vo);
				}
			}

		} catch (ClassNotFoundException e) {
			// try에서 에러가 발생할 경우 catch실행
			e.printStackTrace(); // 오류를 콘솔창에 자세히 출력해줌
		} catch (SQLException e) { // DriverManager.getConnection(url,id,pw)에서
									// add.catch
			e.printStackTrace();
		} // finally는 try 에러와 상관없이 실행
		finally { // 반드시 close 해야함, 닫을때는 나중에 열었던것부터 닫기
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return temp;
	}

	// 정렬 메소드
	public ArrayList<PersonVO> sortName() {// 이름 정렬
		PersonVO vo = null;
		ArrayList<PersonVO> aLName = new ArrayList<>();
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, id, pw);
			String sql1 = "select name from PInfo order by name";
			pstmt = conn.prepareStatement(sql1);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				String name = rs.getString(1);
				vo = new PersonVO(name);
				aLName.add(vo);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return aLName;

	}

	public ArrayList<PersonVO> sortDivision() {// 부서정렬
		PersonVO vo = null;
		ArrayList<PersonVO> aLDivision = new ArrayList<>();
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, id, pw);
			String sql1 = "select name from PInfo order by division";
			pstmt = conn.prepareStatement(sql1);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				String name = rs.getString(1); //
				vo = new PersonVO(name);
				aLDivision.add(vo);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace(); // 오류를 콘솔창에 자세히 출력해줌
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return aLDivision;

	}

	public ArrayList<PersonVO> sortPosition() {// 직책정렬
		PersonVO vo = null;
		ArrayList<PersonVO> aLPosition = new ArrayList<>();
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, id, pw);
			String sql1 = "select name from PInfo order by position";
			pstmt = conn.prepareStatement(sql1);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				String name = rs.getString(1); //
				vo = new PersonVO(name);
				aLPosition.add(vo);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace(); // 오류를 콘솔창에 자세히 출력해줌
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return aLPosition;

	}

	public ArrayList<PersonVO> sortJoinDate() {// 입사일순 정렬
		PersonVO vo = null;
		ArrayList<PersonVO> aLJoinDate = new ArrayList<>();
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, id, pw);
			String sql1 = "select name from PInfo order by joinDate";
			pstmt = conn.prepareStatement(sql1);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				String name = rs.getString(1); //
				vo = new PersonVO(name);
				aLJoinDate.add(vo);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace(); // 오류를 콘솔창에 자세히 출력해줌
		} catch (SQLException e) {
			e.printStackTrace();
		} // finally는 try 에러와 상관없이 실행
		finally { // 반드시 close 해야함, 닫을때는 나중에 열었던것부터 닫기
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return aLJoinDate;

	}

	// 이름 입력시 해당 IdNum가져오는 메소드
	// public int getPersonIdNum(String name) {
	// int num = -1;
	// int IdNum=0;
	// try {
	// Class.forName("oracle.jdbc.driver.OracleDriver");
	// // 해당 드라이버를 메모리에 업로드
	// conn = DriverManager.getConnection(url, id, pw);
	// String sql1 = "select IdNum from PInfo where name = ?";
	// pstmt = conn.prepareStatement(sql1);
	// pstmt.setString(1, name);
	//
	// num = pstmt.executeUpdate();
	// rs = pstmt.executeQuery();
	//
	// if (rs.next()) {
	// IdNum = rs.getInt(1);
	// }
	//
	// } catch (ClassNotFoundException e) {
	// e.printStackTrace();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// if (pstmt != null)
	// pstmt.close();
	// if (conn != null)
	// conn.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// return IdNum;
	// }

	// 이름 입력시 해당 IdNum가져오는 메소드
	public int getPersonIdNum(String name) {
		int num = -1;
		int IdNum = 0;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// 해당 드라이버를 메모리에 업로드
			conn = DriverManager.getConnection(url, id, pw);
			String sql1 = "select IdNum from PInfo where name = ?";
			pstmt = conn.prepareStatement(sql1);
			pstmt.setString(1, name);

			num = pstmt.executeUpdate();
			rs = pstmt.executeQuery();

			if (rs.next()) {
				IdNum = rs.getInt(1);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return IdNum;
	}

	// 사람정보가져오기 메소드 만든이 : 장찬경
	public ArrayList<PersonVO> getpersoninfo(String inputName) {
		PersonVO vo = null;
		ArrayList<PersonVO> getpersoninfo = new ArrayList<>();
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, id, pw);
			String sql1 = "select * from PINFO where NAME = ?"; //여기부분 수정함 : 국경아
			System.out.println(sql1);

			pstmt = conn.prepareStatement(sql1);// ****이거 오류생김
			pstmt.setString(1, inputName); //여기부분 수정함 : 국경아

			rs = pstmt.executeQuery();

			while (rs.next()) {
				int idnum = rs.getInt(1);
				String name = rs.getString(2);
				String div = rs.getString(3);
				String pos = rs.getString(4);
				Date join = rs.getDate(5);
				Date birth = rs.getDate(6);
				String add = rs.getString(7);
				String phone = rs.getString(8);
				String lic = rs.getString(9);
				String acc = rs.getString(10);

				System.out.println(acc);

				vo = new PersonVO(idnum, name, div, pos, join, birth, add, phone, lic, acc);

				getpersoninfo.add(vo);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace(); // 오류를 콘솔창에 자세히 출력해줌
		} catch (SQLException e) {
			e.printStackTrace();
		} // finally는 try 에러와 상관없이 실행
		finally { // 반드시 close 해야함, 닫을때는 나중에 열었던것부터 닫기
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return getpersoninfo;

	}

}
