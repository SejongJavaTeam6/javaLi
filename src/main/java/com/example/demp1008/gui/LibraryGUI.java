package com.example.demp1008.gui;

import com.example.demp1008.Demp1008Application;
import com.example.demp1008.entity.Book;
import com.example.demp1008.entity.Loan;
import com.example.demp1008.entity.Member;
import com.example.demp1008.service.BookService;
import com.example.demp1008.service.LoanService;
import com.example.demp1008.service.MemberService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class LibraryGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // 서비스 객체들
    private final MemberService memberService;
    private final BookService bookService;
    private final LoanService loanService;
    private JTable bookTable;
    private JTable loansTable;
    private JDialog loansDialog;


    // 현재 로그인한 회원 정보
    private Member currentMember;

    // "도서 추가" 버튼을 클래스 필드로 선언
    private JButton addBookButtonMain;

    public LibraryGUI(MemberService memberService, BookService bookService, LoanService loanService) {
        this.memberService = memberService;
        this.bookService = bookService;
        this.loanService = loanService;

        setTitle("도서관 관리 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        initLoginPanel();           // 로그인 패널
        initRegisterPanel();        // 회원가입 패널
        initMainPanel();            // 메인 패널 (로그인 후)

        add(mainPanel);
    }

    /**
     * 도서 대출 버튼 렌더러
     */
    public class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            return this;
        }
    }

    /**
     * 도서 대출 버튼 에디터
     */
    public class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private final LibraryGUI gui;

        public ButtonEditor(JCheckBox checkBox, LibraryGUI gui) {
            super(checkBox);
            this.gui = gui;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value != null ? value.toString() : "");
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                if (bookTable != null) {
                    int row = bookTable.getSelectedRow();

                    if (row >= 0) {
                        // Book Number 컬럼 (인덱스 1)에서 Book의 번호 가져오기
                        String bookNumber = (String) bookTable.getValueAt(row, 1);
                        boolean isAvailable = "available".equals(bookTable.getValueAt(row, 4));

                        if (isAvailable && gui.currentMember != null) {
                            try {
                                // LoanService의 borrowBook 메서드 사용
                                loanService.borrowBook(gui.currentMember.getEmail(), bookNumber);
                                JOptionPane.showMessageDialog(gui, "대출 성공!");
                                gui.refreshBookTable();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(gui, "대출 실패: " + ex.getMessage());
                            }
                        } else if (gui.currentMember == null) {
                            JOptionPane.showMessageDialog(gui, "로그인이 필요합니다.");
                        } else {
                            JOptionPane.showMessageDialog(gui, "해당 도서는 대출 불가능합니다.");
                        }
                    }
                }
            }
            isPushed = false;
            return label;
        }
    }

    /**
     * 로그인 패널 초기화
     */
    private void initLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 로그인 컴포넌트
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("로그인");
        JButton registerButton = new JButton("회원가입");
        // JButton addBookButton = new JButton("도서 추가");  // 책 등록 버튼 제거

        // 컴포넌트 배치
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(new JLabel("이메일:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(new JLabel("비밀번호:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        // buttonPanel.add(addBookButton);  // 버튼 패널에서 제거

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginPanel.add(buttonPanel, gbc);

        // 이벤트 처리
        loginButton.addActionListener(e -> {
            try {
                currentMember = memberService.login(emailField.getText(),
                        new String(passwordField.getPassword()));
                cardLayout.show(mainPanel, "main");
                refreshBookTable();
                JOptionPane.showMessageDialog(this, "로그인 성공!");

                // "도서 추가" 버튼의 가시성 설정
                if (currentMember.getEmail().equals("1")) {
                    addBookButtonMain.setVisible(true);
                } else {
                    addBookButtonMain.setVisible(false);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "로그인 실패: " + ex.getMessage());
            }
        });

        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "register"));

        // addBookButton.addActionListener(e -> showAddBookDialog());  // 이벤트 처리 제거

        mainPanel.add(loginPanel, "login");
    }

    /**
     * 회원가입 패널 초기화
     */
    private void initRegisterPanel() {
        JPanel registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 회원가입 필드들
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField phoneField = new JTextField(20);
        JTextField ageField = new JTextField(20);

        // 컴포넌트 배치
        int gridy = 0;
        addFormField(registerPanel, "이름:", nameField, gbc, gridy++);
        addFormField(registerPanel, "이메일:", emailField, gbc, gridy++);
        addFormField(registerPanel, "비밀번호:", passwordField, gbc, gridy++);
        addFormField(registerPanel, "전화번호:", phoneField, gbc, gridy++);
        addFormField(registerPanel, "나이:", ageField, gbc, gridy++);

        JButton submitButton = new JButton("회원가입");
        JButton backButton = new JButton("뒤로가기");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.gridwidth = 2;
        registerPanel.add(buttonPanel, gbc);

        // 이벤트 처리
        submitButton.addActionListener(e -> {
            Member member = new Member();
            member.setName(nameField.getText());
            member.setEmail(emailField.getText());
            member.setPassword(new String(passwordField.getPassword()));
            member.setPhoneNumber(phoneField.getText());
            try {
                member.setAge(Integer.parseInt(ageField.getText()));
                memberService.createMember(member);
                JOptionPane.showMessageDialog(this, "회원가입 성공!");
                cardLayout.show(mainPanel, "login");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "유효하지 않은 나이입니다.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "회원가입 실패: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        mainPanel.add(registerPanel, "register");
    }

    /**
     * 메인 패널 초기화 (로그인 후)
     */
    private void initMainPanel() {
        JPanel mainMenuPanel = new JPanel(new BorderLayout());

        // 상단 메뉴 패널
        JPanel menuPanel = new JPanel(new FlowLayout());
        JButton searchButton = new JButton("도서 검색");
        addBookButtonMain = new JButton("도서 추가"); // 클래스 필드로 "도서 추가" 버튼 초기화
        JButton myLoansButton = new JButton("내 대출 목록");
        JButton logoutButton = new JButton("로그아웃");

        // 기본적으로 "도서 추가" 버튼을 숨김
        addBookButtonMain.setVisible(false);

        menuPanel.add(searchButton);
        menuPanel.add(addBookButtonMain);
        menuPanel.add(myLoansButton);
        menuPanel.add(logoutButton);

        // 중앙 테이블 패널
        String[] columns = {"ID", "도서번호", "제목", "출판사", "대출 가능 여부", "대출"};
        DefaultTableModel bookTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // 대출처리 버튼 컬럼만 편집 가능
            }
        };
        bookTable = new JTable(bookTableModel);

        // ID 컬럼 숨기기
        bookTable.getColumnModel().getColumn(0).setMinWidth(0);
        bookTable.getColumnModel().getColumn(0).setMaxWidth(0);
        bookTable.getColumnModel().getColumn(0).setWidth(0);

        // 버튼 컬럼 설정
        bookTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        bookTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane scrollPane = new JScrollPane(bookTable);
        mainMenuPanel.add(menuPanel, BorderLayout.NORTH);
        mainMenuPanel.add(scrollPane, BorderLayout.CENTER);

        // 초기 도서 목록 로드
        refreshBookTable();

        // 이벤트 처리
        searchButton.addActionListener(e -> {
            String keyword = JOptionPane.showInputDialog("제목으로 검색:");
            if (keyword != null && !keyword.trim().isEmpty()) {
                searchBooks(keyword, bookTableModel);
            }
        });

        addBookButtonMain.addActionListener(e -> showAddBookDialog());

        myLoansButton.addActionListener(e -> showLoansDialog());

        logoutButton.addActionListener(e -> {
            currentMember = null;
            cardLayout.show(mainPanel, "login");
            JOptionPane.showMessageDialog(this, "로그아웃 되었습니다.");
        });

        mainMenuPanel.setName("mainMenuPanel");
        mainPanel.add(mainMenuPanel, "main");
    }

    /**
     * 폼 필드 추가 유틸리티 메서드
     */
    private void addFormField(JPanel panel, String label, JComponent field,
                              GridBagConstraints gbc, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }

    /**
     * 도서 추가 다이얼로그 표시
     */
    private void showAddBookDialog() {
        JDialog dialog = new JDialog(this, "새 도서 추가", true);
        JPanel dialogPanel = new JPanel(new GridBagLayout());
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField titleField = new JTextField(20);
        JTextField publisherField = new JTextField(20);
        JTextField bookNumberField = new JTextField(20);
        JTextField pageCntField = new JTextField(20); // 페이지 수 필드 추가

        addFormField(dialogPanel, "제목:", titleField, gbc, 0);
        addFormField(dialogPanel, "출판사:", publisherField, gbc, 1);
        addFormField(dialogPanel, "도서번호:", bookNumberField, gbc, 2);
        addFormField(dialogPanel, "페이지 수:", pageCntField, gbc, 3); // 배치

        JButton submitButton = new JButton("추가");
        JButton cancelButton = new JButton("취소");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        dialogPanel.add(buttonPanel, gbc);

        // 이벤트 처리
        submitButton.addActionListener(e -> {
            Book book = new Book();
            book.setTitle(titleField.getText());
            book.setPublisher(publisherField.getText());
            book.setBookNumber(bookNumberField.getText());
            book.setAvailable(true);
            book.setLoanCnt(0);
            try {
                book.setPageCnt(Integer.parseInt(pageCntField.getText()));
                bookService.createBook(book);
                JOptionPane.showMessageDialog(dialog, "도서 추가 성공!");
                dialog.dispose();
                refreshBookTable();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dialog, "유효하지 않은 페이지 수입니다.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "도서 추가 실패: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(dialogPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * 대출 목록 다이얼로그 표시
     */
    private void showLoansDialog() {
        if (currentMember == null) {
            JOptionPane.showMessageDialog(this, "do login");
            return;
        }

        JDialog dialog = new JDialog(this, "my loan list", true);
        //dialog.setLayout(new BorderLayout());
        this.loansDialog = dialog;

        String[] columns = {"제목", "대출 날짜", "반납 예정일", "상태", "Loan ID", "반납"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // "반납" 버튼 컬럼만 편집 가능
            }
        };
        loansTable = new JTable(model); // 여기서 loansTable 필드에 직접 대입

        // Loan ID 컬럼 숨기기
        loansTable.getColumnModel().getColumn(4).setMinWidth(0);
        loansTable.getColumnModel().getColumn(4).setMaxWidth(0);
        loansTable.getColumnModel().getColumn(4).setWidth(0);

        loansTable.getColumnModel().getColumn(5).setCellRenderer(new ReturnButtonRenderer());
        loansTable.getColumnModel().getColumn(5).setCellEditor(new ReturnButtonEditor(new JCheckBox(), this));

        JScrollPane scrollPane = new JScrollPane(loansTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        try {
            List<Loan> loans = loanService.getMemberLoans(currentMember.getEmail());
            for (Loan loan : loans) {
                model.addRow(new Object[]{
                        loan.getBook().getTitle(),
                        loan.getLoanDate(),
                        loan.getScheduledReturnDate(),
                        loan.getOverdueDays() > 0 ? "연체 " + loan.getOverdueDays() + "일" : "정상",
                        loan.getId(),
                        "Return"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "대출 목록을 불러오는 데 실패했습니다: " + e.getMessage());
        }

        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // loansTable을 반환하는 메서드
    private JTable getLoansTable() {
        return loansTable;
    }


    /**
     * 도서 검색 기능 구현
     *
     * @param keyword 검색 키워드
     * @param model   테이블 모델
     */
    private void searchBooks(String keyword, DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<Book> books = bookService.findByTitleContaining(keyword);
            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(this, "해당 키워드를 포함하는 도서를 찾을 수 없습니다.");
                return;
            }
            for (Book book : books) {
                model.addRow(new Object[]{
                        book.getId(),                   // ID (숨김)
                        book.getBookNumber(),           // 도서번호
                        book.getTitle(),                // 제목
                        book.getPublisher(),            // 출판사
                        book.isAvailable() ? "available" : "not available",  // 대출가능 여부
                        book.isAvailable() ? "Borrow" : "-"    // 대출처리 버튼
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "도서 검색 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 도서 테이블 갱신
     */
    private void refreshBookTable() {
        if (bookTable != null) {
            DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
            model.setRowCount(0); // 기존 데이터 초기화
            try {
                List<Book> books = bookService.findAllBooks();
                for (Book book : books) {
                    model.addRow(new Object[]{
                            book.getId(),                   // ID (숨김)
                            book.getBookNumber(),           // 도서번호
                            book.getTitle(),                // 제목
                            book.getPublisher(),            // 출판사
                            book.isAvailable() ? "available" : "not available",  // 대출가능 여부
                            book.isAvailable() ? "Borrow" : "-"    // 대출처리 버튼
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "도서 목록을 불러오는 데 실패했습니다: " + e.getMessage());
            }
        }
    }

    /**
     * 도서 테이블 가져오기
     *
     * @return JTable 객체
     */
    private JTable getBookTable() {
        return bookTable;
    }

    /**
     * 도서 반납 버튼 렌더러
     */
    public class ReturnButtonRenderer extends JButton implements TableCellRenderer {
        public ReturnButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Return");
            return this;
        }
    }

    /**
     * 도서 반납 버튼 에디터
     */
    public class ReturnButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private boolean isPushed;
        private final LibraryGUI gui;

        public ReturnButtonEditor(JCheckBox checkBox, LibraryGUI gui) {
            super(checkBox);
            this.gui = gui;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            button.setText("Return");
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                JTable loansTable = gui.getLoansTable();
                int row = loansTable.getSelectedRow();

                if (row >= 0) {
                    Long loanId = (Long) loansTable.getValueAt(row, 4);
                    try {
                        gui.loanService.returnBook(loanId);
                        JOptionPane.showMessageDialog(gui, "반납 성공!");
                        gui.refreshBookTable();

                        // 기존 대출 목록 다이얼로그 닫기
                        if (gui.loansDialog != null) {
                            gui.loansDialog.dispose();
                        }

                        // 새로 대출 목록 다이얼로그 열기
                        gui.showLoansDialog();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(gui, "반납 실패: " + ex.getMessage());
                    }
                }
            }
            isPushed = false;
            return "Return";
        }

    }


    /**
     * 메인 메서드
     */
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Demp1008Application.class);
        SwingUtilities.invokeLater(() -> {
            MemberService memberService = context.getBean(MemberService.class);
            BookService bookService = context.getBean(BookService.class);
            LoanService loanService = context.getBean(LoanService.class);
            new LibraryGUI(memberService, bookService, loanService).setVisible(true);
        });
    }
}
