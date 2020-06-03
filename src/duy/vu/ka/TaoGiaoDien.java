package duy.vu.ka;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.awt.event.ActionEvent;

public class TaoGiaoDien extends JFrame {

	public Timer vaogame, demnguoccauhoi;
	JOptionPane jOpNhapTen, jOpXoaDiem;
	JLabel lblGiaoDienChinh, lblGiaoDienChoiGame, lblDemNguocBatDau, lblPhepTinh, lblDemDiem, lblHienThiDiem, lblNguoi5,
			lblNguoi4, lblNguoi3, lblNguoi2, lblNguoi1;
	JPanel panelVaoGame, panelTopPlayers;
	JButton btnSai, btnDung, btnPlayAgain, btnBack, btnClear, btnBackFromTop;
	JProgressBar progTienTrinh;
	public int demNguocBatDau = 5, demnguocChoi = 100, a, b, c, d, dokho = 1, dokho1 = 0;
	public static int diem = 0;
	private JPanel contentPane;
	private JPanel panelKetThuc;
	private JLabel lblAnhKetThuc;

	public static String DRIVER = "org.sqlite.JDBC";
	public static String URL = "jdbc:sqlite:diemcao.db";
	public static Statement state;
	public static Connection conn;
	public static PreparedStatement pre;
	// tạo bảng
	public  void CreateTable() throws ClassNotFoundException, SQLException {
		Class.forName(DRIVER);
		conn = DriverManager.getConnection(URL);
		state = conn.createStatement();
		
		DatabaseMetaData dbm = conn.getMetaData();
		ResultSet table = dbm.getTables(null, null, "DiemCao", null);
		if (table.next()) {
			System.out.println("Bảng đã tồn tại");
		}
		else {
			System.out.println("Bảng chưa tồn tại, tiến hành tạo bảng ");
			
			ResultSet result = state.executeQuery("CREATE TABLE IF NOT EXISTS DiemCao"
					+ " (id integer PRIMARY KEY AUTOINCREMENT, "
					+ "HoTen varchar(30)," + " score integer);");
		}
	}


	// thêm thông tin
	public void ThemDiemCao(String tenDiemCao, int diemCao)  {
		final String insertSQL = "INSERT INTO DiemCao(HoTen, score) VALUES(?, ?)";
		try {
			pre = conn.prepareStatement(insertSQL);
			pre.setString(1, tenDiemCao);
			pre.setInt(2, diemCao);
			pre.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				pre.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		
		
	
	}
	
	//hiển thị thông tin top players
	public void HienThiThongTin() {
		ArrayList<String> ds = new ArrayList<String>();
		int count  = 0;
		try {
			state = conn.createStatement();
			ResultSet res = state.executeQuery("SELECT * FROM DiemCao ORDER BY  score DESC");
			while(res.next()) {
				String ten = res.getString("HoTen");
				int score = res.getInt("score");
				ds.add(ten + "  " + score);
				count++;
				if(count == 5) break;
			}
			if(count == 0) return;
			if(count >= 1) {
				lblNguoi1.setText(ds.get(0));
				if(count >= 2)
					lblNguoi2.setText(ds.get(1));
				if(count >= 3)
					lblNguoi3.setText(ds.get(2));
				if(count >= 4)
					lblNguoi4.setText(ds.get(3));
				if(count >= 5)
					lblNguoi5.setText(ds.get(4));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void XoaThongTin() throws SQLException {
		pre = conn.prepareStatement("DELETE FROM DiemCao");
		pre.executeUpdate();
		lblNguoi1.setText("");
		lblNguoi2.setText("");
		lblNguoi3.setText("");
		lblNguoi4.setText("");
		lblNguoi5.setText("");
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TaoGiaoDien frame = new TaoGiaoDien();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	// đếm ngược bắt đầu vào game
	public void DemNguocVaoGame() {
		demNguocBatDau = 5;
		lblDemNguocBatDau.setVisible(true);
		demNguocBatDau dngcBD = new demNguocBatDau();
		vaogame = new Timer(700, dngcBD);
		vaogame.start();
	}

	/**
	 * Create the frame.
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public TaoGiaoDien() throws ClassNotFoundException, SQLException {
		CreateTable();
		setResizable(false);
		ImageIcon igmIF = new ImageIcon(getClass().getClassLoader().getResource("LogoJFrame.png"));
		this.setIconImage(igmIF.getImage());

		this.setTitle("Math Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 436, 569);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Panel màn hình chính
		JPanel panelMain = new JPanel();
		panelMain.setBounds(0, 0, 430, 530);
		contentPane.add(panelMain);
		panelMain.setLayout(null);

		// nút start
		JButton btnStart = new JButton("");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Đếm ngược vào trò chơi
				panelMain.setVisible(false);
				lblPhepTinh.setVisible(false);
				btnSai.setVisible(false);
				btnDung.setVisible(false);
				progTienTrinh.setVisible(false);
				panelVaoGame.setVisible(true);
				DemNguocVaoGame();

			}
		});

		// button quit
		JButton btnQuit = new JButton("");
		// add event cho button quit
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int x = JOptionPane.showConfirmDialog(null, "Bạn sẽ thoát game? ", "Quit", JOptionPane.YES_NO_OPTION);
				if (x == JOptionPane.YES_NO_OPTION)
					System.exit(0);
			}
		});
		btnQuit.setIcon(new ImageIcon(getClass().getClassLoader().getResource("Quit Button.png")));
		btnQuit.setBounds(152, 478, 129, 41);
		panelMain.add(btnQuit);
		btnStart.setIcon(new ImageIcon(getClass().getClassLoader().getResource("Start.png")));
		btnStart.setBounds(152, 280, 129, 41);
		panelMain.add(btnStart);

		// nút tutorial
		JButton btnTutorial = new JButton("");
		btnTutorial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane jOpHuongDan = new JOptionPane();
				jOpHuongDan.showMessageDialog(null,
						"Màn hình sẽ xuất hiện một phép tính random"
								+ ",\n công việc của bạn là cho biết phép tính đó đúng hai sai"
								+ ".\nNếu bạn trả lời chính xác thì được cộng 1 điểm, trả lời"
								+ " \nkhông chính xác sẽ bị thua cuộc. Người chơi sẽ thua khi trả lời không"
								+ " \nchính xác hoặc trả lời vượt quá thời gian cho phép",
						"Tutorial", JOptionPane.QUESTION_MESSAGE);
			}
		});
		btnTutorial.setIcon(new ImageIcon(getClass().getClassLoader().getResource("Tutorial.png")));
		btnTutorial.setBounds(152, 332, 129, 41);
		panelMain.add(btnTutorial);

		// nút ghi lại những người điểm cao
		JButton btnTopPlayers = new JButton("");

		// add event cho top Players
		btnTopPlayers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelMain.setVisible(false);
				panelTopPlayers.setVisible(true);
				lblNguoi1.setVisible(true);
				lblNguoi2.setVisible(true);
				lblNguoi3.setVisible(true);
				lblNguoi4.setVisible(true);
				lblNguoi5.setVisible(true);
				HienThiThongTin();
			}
		});
		btnTopPlayers.setIcon(new ImageIcon(getClass().getClassLoader().getResource("topPlayer.png")));
		btnTopPlayers.setBounds(152, 380, 129, 41);
		panelMain.add(btnTopPlayers);

		// nút trợ giúp
		JButton btnHelp = new JButton("");
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane jOpHelp = new JOptionPane();
				jOpHelp.showMessageDialog(null,
						"Game được viết bởi Vũ lấy ý tưởng từ Freaking Math."
								+ "\n Nếu cần trợ giúp liên hệ vun7255@gmail.com",
						"Help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnHelp.setIcon(new ImageIcon(getClass().getClassLoader().getResource("Help.png")));
		btnHelp.setBounds(152, 429, 129, 41);
		panelMain.add(btnHelp);

		// Label ảnh chính trước khi vào game
		lblGiaoDienChinh = new JLabel("");
		ImageIcon imgGDChinh = new ImageIcon(getClass().getClassLoader().getResource("GiaoDienBanDau.png"));
		
		lblGiaoDienChinh.setIcon(imgGDChinh);
		lblGiaoDienChinh.setBounds(0, 0, 430, 530);
		panelMain.add(lblGiaoDienChinh);

		panelVaoGame = new JPanel();
		panelVaoGame.setLayout(null);
		panelVaoGame.setBounds(0, 0, 430, 530);
		panelVaoGame.setVisible(false);
		contentPane.add(panelVaoGame);

		// Label đếm ngược khi vào game
		lblDemNguocBatDau = new JLabel("");
		lblDemNguocBatDau.setIcon(null);
		lblDemNguocBatDau.setForeground(new Color(0, 0, 128));
		lblDemNguocBatDau.setHorizontalAlignment(SwingConstants.CENTER);
		lblDemNguocBatDau.setFont(new Font("Snap ITC", Font.PLAIN, 80));
		lblDemNguocBatDau.setBounds(36, 159, 365, 180);
		lblDemNguocBatDau.setVisible(true);
		panelVaoGame.add(lblDemNguocBatDau);

		// label đếm điểm
		lblDemDiem = new JLabel("");
		lblDemDiem.setBounds(41, 0, 252, 57);
		lblDemDiem.setForeground(Color.RED);
		lblDemDiem.setHorizontalAlignment(SwingConstants.CENTER);
		lblDemDiem.setFont(new Font("Snap ITC", Font.PLAIN, 31));
		panelVaoGame.add(lblDemDiem);

		// Label hiển thị phép tính
		lblPhepTinh = new JLabel("");
		lblPhepTinh.setIcon(null);
		lblPhepTinh.setForeground(new Color(0, 0, 128));
		lblPhepTinh.setHorizontalAlignment(SwingConstants.CENTER);
		lblPhepTinh.setFont(new Font("Snap ITC", Font.PLAIN, 30));
		lblPhepTinh.setBounds(36, 159, 365, 180);
		panelVaoGame.add(lblPhepTinh);

		// button Sai
		btnSai = new JButton("");

		// add event button sai
		btnSai.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (a + b != d) {
					diem++;
					dokho++;
					dokho1++;
					lblDemDiem.setText("Points " + diem);
					HienThiPhepTinh();
					demnguocChoi = 100;
					demnguoccauhoi.start();
				} else {
					lblPhepTinh.setText("Your Point " + diem);
					btnDung.setVisible(false);
					btnSai.setVisible(false);
					demnguoccauhoi.stop();
					panelVaoGame.setVisible(false);
					panelKetThuc.setVisible(true);
					lblHienThiDiem.setText("Your Points " + diem);
					jOpNhapTen = new JOptionPane();
					String layTenDiemCao = jOpNhapTen.showInputDialog("Game over \nPlease tell me your name ");
					if (layTenDiemCao == null || layTenDiemCao.length() == 0)
						layTenDiemCao = "noname";
					ThemDiemCao(layTenDiemCao, diem);
					btnBack.setVisible(true);
					btnBack.setVisible(true);
					btnPlayAgain.setVisible(true);
				}
			}
		});
		btnSai.setIcon(new ImageIcon(getClass().getClassLoader().getResource("X button.png")));
		btnSai.setBackground(new Color(255, 255, 0));
		btnSai.setBounds(10, 428, 80, 60);
		btnSai.setVisible(false);
		panelVaoGame.add(btnSai);

		// button Đúng
		btnDung = new JButton("");
		btnDung.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (a + b == d) {
					diem++;
					dokho++;
					dokho1++;
					lblDemDiem.setText("Points " + diem);
					HienThiPhepTinh();
					demnguocChoi = 100;
					demnguoccauhoi.start();
				} else {
					lblPhepTinh.setText("Your Point " + diem);
					btnDung.setVisible(false);
					btnSai.setVisible(false);
					demnguoccauhoi.stop();
					panelVaoGame.setVisible(false);
					panelKetThuc.setVisible(true);
					lblHienThiDiem.setText("Your Points " + diem);
					jOpNhapTen = new JOptionPane();
					String layTenDiemCao = jOpNhapTen.showInputDialog("Game over \nPlease tell me your name ");
					if (layTenDiemCao == null || layTenDiemCao.length() == 0)
						layTenDiemCao = "noname";
					ThemDiemCao(layTenDiemCao, diem);
					btnBack.setVisible(true);
					btnPlayAgain.setVisible(true);

				}
			}
		});
		btnDung.setIcon(new ImageIcon(getClass().getClassLoader().getResource("V button.png")));
		btnDung.setBackground(Color.YELLOW);
		btnDung.setBounds(340, 428, 80, 60);
		btnDung.setVisible(false);
		panelVaoGame.add(btnDung);

		// thanh tiến trình
		progTienTrinh = new JProgressBar();
		progTienTrinh.setFont(new Font("Stencil Std", Font.PLAIN, 15));
		progTienTrinh.setForeground(new Color(0, 255, 0));
		progTienTrinh.setStringPainted(true);
		progTienTrinh.setBackground(new Color(0, 255, 127));
		progTienTrinh.setBounds(0, 79, 430, 30);
		panelVaoGame.add(progTienTrinh);

		// ảnh khi vào game
		JLabel lblVaoGame = new JLabel("");
		lblVaoGame.setIcon(new ImageIcon(getClass().getClassLoader().getResource("back2.png")));
		lblVaoGame.setBounds(0, 0, 430, 530);
		panelVaoGame.add(lblVaoGame);

		// Tạo panel khi chơi thua
		panelKetThuc = new JPanel();
		panelKetThuc.setBounds(0, 0, 430, 530);
		contentPane.add(panelKetThuc);
		panelKetThuc.setLayout(null);
		panelKetThuc.setVisible(false);

		// Label hiển thị điểm ở panel kết thúc
		lblHienThiDiem = new JLabel();
		lblHienThiDiem.setIcon(null);
		lblHienThiDiem.setForeground(new Color(0, 0, 128));
		lblHienThiDiem.setHorizontalAlignment(SwingConstants.CENTER);
		lblHienThiDiem.setFont(new Font("Snap ITC", Font.PLAIN, 30));
		lblHienThiDiem.setBounds(36, 159, 365, 180);
		panelKetThuc.add(lblHienThiDiem);

		// button play again
		btnPlayAgain = new JButton("");
		btnPlayAgain.setBounds(122, 295, 111, 60);

		// add event cho button play again
		btnPlayAgain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				diem = 0;
				dokho = 1;
				dokho1 = 0;
				panelKetThuc.setVisible(false);
				lblPhepTinh.setVisible(false);
				btnSai.setVisible(false);
				btnDung.setVisible(false);
				progTienTrinh.setVisible(false);
				panelVaoGame.setVisible(true);
				lblDemDiem.setText("Points 0");
				demNguocBatDau = 5;

				DemNguocVaoGame();

			}
		});
		btnPlayAgain.setIcon(new ImageIcon(getClass().getClassLoader().getResource("PlayAgain.png")));
		btnPlayAgain.setBackground(new Color(135, 206, 250));
		btnPlayAgain.setForeground(new Color(0, 191, 255));
		btnPlayAgain.setVisible(false);
		panelKetThuc.add(btnPlayAgain);

		// button back menu
		btnBack = new JButton("");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblDemDiem.setText("Points 0");
				diem = 0;
				dokho = 1;
				panelKetThuc.setVisible(false);
				panelMain.setVisible(true);
				btnBack.setVisible(false);
				btnPlayAgain.setVisible(false);
			}
		});
		btnBack.setIcon(new ImageIcon(getClass().getClassLoader().getResource("Back.png")));
		btnBack.setBounds(172, 377, 111, 60);
		btnPlayAgain.setIcon(new ImageIcon(getClass().getClassLoader().getResource("PlayAgain.png")));
		btnPlayAgain.setBounds(172, 306, 111, 60);
		btnBack.setVisible(false);
		panelKetThuc.add(btnBack);

		// ảnh panel kết thúc
		lblAnhKetThuc = new JLabel("");
		lblAnhKetThuc.setIcon(new ImageIcon(getClass().getClassLoader().getResource("BackGameOver.png")));
		lblAnhKetThuc.setBounds(0, 0, 430, 530);
		panelKetThuc.add(lblAnhKetThuc);

		// Giao Diện Top Players
		panelTopPlayers = new JPanel();
		panelTopPlayers.setBounds(0, 0, 430, 541);
		contentPane.add(panelTopPlayers);
		panelTopPlayers.setVisible(false);
		panelTopPlayers.setLayout(null);

		// button xóa kỉ lục
		btnClear = new JButton("");

		// add event cho button xóa kỉ lục
		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int dialogresult = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn xóa tất cả điểm kỉ lục?",
						"Warning", JOptionPane.YES_NO_OPTION);
				if (dialogresult == JOptionPane.YES_NO_OPTION) {
					lblNguoi1.setVisible(false);
					lblNguoi2.setVisible(false);
					lblNguoi3.setVisible(false);
					lblNguoi4.setVisible(false);
					lblNguoi5.setVisible(false);
					try {
						XoaThongTin();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnClear.setBackground(new Color(255, 255, 0));
		btnClear.setIcon(new ImageIcon(getClass().getClassLoader().getResource("ClearTopP.png")));
		btnClear.setBounds(296, 71, 57, 51);
		panelTopPlayers.add(btnClear);

		// button quay lại menu
		btnBackFromTop = new JButton("");

		// add event cho button back from TOpPlayers
		btnBackFromTop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelTopPlayers.setVisible(false);
				panelMain.setVisible(true);
			}
		});
		btnBackFromTop.setIcon(new ImageIcon(getClass().getClassLoader().getResource("BackMenu.png")));
		btnBackFromTop.setBounds(363, 71, 57, 51);
		panelTopPlayers.add(btnBackFromTop);

		lblNguoi5 = new JLabel("");
		lblNguoi5.setHorizontalAlignment(SwingConstants.LEFT);
		lblNguoi5.setForeground(new Color(51, 0, 204));
		lblNguoi5.setFont(new Font("Snap ITC", Font.ITALIC, 31));
		lblNguoi5.setBounds(71, 475, 349, 43);
		panelTopPlayers.add(lblNguoi5);

		lblNguoi4 = new JLabel("");
		lblNguoi4.setHorizontalAlignment(SwingConstants.LEFT);
		lblNguoi4.setForeground(new Color(51, 0, 204));
		lblNguoi4.setFont(new Font("Snap ITC", Font.ITALIC, 31));
		lblNguoi4.setBounds(71, 392, 349, 43);
		panelTopPlayers.add(lblNguoi4);

		lblNguoi3 = new JLabel("");
		lblNguoi3.setHorizontalAlignment(SwingConstants.LEFT);
		lblNguoi3.setForeground(new Color(51, 0, 204));
		lblNguoi3.setFont(new Font("Snap ITC", Font.ITALIC, 31));
		lblNguoi3.setBounds(71, 302, 349, 43);
		panelTopPlayers.add(lblNguoi3);

		lblNguoi2 = new JLabel("");
		lblNguoi2.setHorizontalAlignment(SwingConstants.LEFT);
		lblNguoi2.setForeground(new Color(51, 0, 204));
		lblNguoi2.setFont(new Font("Snap ITC", Font.ITALIC, 31));
		lblNguoi2.setBounds(71, 217, 349, 43);
		panelTopPlayers.add(lblNguoi2);

		lblNguoi1 = new JLabel("");
		lblNguoi1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNguoi1.setForeground(new Color(51, 0, 204));
		lblNguoi1.setFont(new Font("Snap ITC", Font.ITALIC, 31));
		lblNguoi1.setBounds(71, 133, 349, 43);
		panelTopPlayers.add(lblNguoi1);

		JLabel lblAnhTopPlayers = new JLabel("");
		lblAnhTopPlayers.setIcon(new ImageIcon(getClass().getClassLoader().getResource("top players.png")));
		lblAnhTopPlayers.setBounds(0, 0, 430, 530);
		panelTopPlayers.add(lblAnhTopPlayers);

	}

	// tạo phương thức hiện thị những người điểm cao lên panel kỉ lục

	// đếm ngược bắt đầu trò chơi
	public class demNguocBatDau implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (demNguocBatDau > 0) {
				lblDemNguocBatDau.setText(String.valueOf(demNguocBatDau));
				demNguocBatDau--;
			}
			if (demNguocBatDau == 0) {
				lblDemNguocBatDau.setText("Start");
				demNguocBatDau--;
			}
			if (demNguocBatDau == -1) {
				lblDemNguocBatDau.setVisible(false);
				lblPhepTinh.setVisible(true);
				btnSai.setVisible(true);
				btnDung.setVisible(true);
				progTienTrinh.setVisible(true);
				lblDemDiem.setVisible(true);
				vaogame.stop();

				// bắt đầu game
				// Vào trò chơi
				HienThiPhepTinh();
				demNguocPhepTinh dngcPT = new demNguocPhepTinh();
				demnguoccauhoi = new Timer(17, dngcPT);
				demnguoccauhoi.start();

			}
		}

	}

	// hiển thị phép tính
	public void HienThiPhepTinh() {
		Random rd = new Random();
		this.a = rd.nextInt(dokho * 3) + 1;
		this.b = rd.nextInt(dokho * 3) + 1;
		this.c = rd.nextInt(10) + 1;
		if (c % 2 == 0) {
			this.d = a + b;
			lblPhepTinh.setText(a + " + " + b + " = " + d);
		} else {
			if (a > b)
				this.d = rd.nextInt(dokho * 3) + a;
			else
				this.d = rd.nextInt(dokho * 3) + b;

			lblPhepTinh.setText(a + " + " + b + " = " + d);
		}
	}

	// đếm ngược từng phép tính
	public class demNguocPhepTinh implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (demnguocChoi > 0) {
				demnguocChoi--;
				progTienTrinh.setValue(demnguocChoi);
				if (demnguocChoi <= 30)
					progTienTrinh.setBackground(new Color(255, 0, 0));
			}

			if (demnguocChoi == 0) {
				jOpNhapTen = new JOptionPane();
				String layTenDiemCao = jOpNhapTen.showInputDialog("Game over \nPlease tell me your name ");
				if (layTenDiemCao == null || layTenDiemCao.length() == 0)
					layTenDiemCao = "noname";
				ThemDiemCao(layTenDiemCao, diem);
				lblPhepTinh.setText("Your Point " + diem);
				btnDung.setVisible(false);
				btnSai.setVisible(false);
				demnguoccauhoi.stop();
				panelVaoGame.setVisible(false);
				panelKetThuc.setVisible(true);
				lblHienThiDiem.setText("Your Points " + diem);
				btnBack.setVisible(true);
				btnPlayAgain.setVisible(true);
				demnguocChoi = 100;
			}

		}
	}
}
