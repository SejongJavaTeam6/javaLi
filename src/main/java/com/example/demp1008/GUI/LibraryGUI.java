package com.example.demp1008.GUI;

import com.example.demp1008.Demp1008Application;
import com.example.demp1008.entity.Book;
import com.example.demp1008.entity.Loan;
import com.example.demp1008.entity.Member;
import com.example.demp1008.service.BookService;
import com.example.demp1008.service.MemberService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;


public class LibraryGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // 서비스 객체들
    private final MemberService memberService;
    private final BookService bookService;

    // 현재 로그인한 회원 정보
    private Member currentMember;

    public LibraryGUI(MemberService memberService, BookService bookService) {
        this.memberService = memberService;
        this.bookService = bookService;

        setTitle("도서관 관리 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        initLoginPanel();           // 로그인 패널
        initRegisterPanel();        // 회원가입 패널
        initMainPanel();           // 메인 패널 (로그인 후)

        add(mainPanel);
    }

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
                JTable table = gui.getBookTable();
                int row = table.getSelectedRow();

                // ID 컬럼(숨김)에서 Book의 ID 가져오기
                Long bookId = (Long) table.getValueAt(row, 0);
                boolean isAvailable = "가능".equals(table.getValueAt(row, 4));

                if (isAvailable && currentMember != null) {
                    try {
                        // 기존 bookService.borrowBook() 메서드 사용
                        bookService.borrowBook(currentMember.getId(), bookId);
                        JOptionPane.showMessageDialog(gui, "대출 성공!");
                        gui.refreshBookTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(gui, "대출 실패: " + ex.getMessage());
                    }
                } else if (currentMember == null) {
                    JOptionPane.showMessageDialog(gui, "로그인이 필요합니다.");
                }
            }
            isPushed = false;
            return label;
        }
    }

    private void initLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 로그인 컴포넌트
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("로그인");
        JButton registerButton = new JButton("회원가입");
        JButton addBookButton = new JButton("책 등록");  // 책 등록 버튼 추가

        // 컴포넌트 배치
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(new JLabel("이메일:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("비밀번호:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(addBookButton);  // 버튼 패널에 추가

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginPanel.add(buttonPanel, gbc);

        // 이벤트 처리
        loginButton.addActionListener(e -> {
            try {
                currentMember = memberService.login(emailField.getText(),
                        new String(passwordField.getPassword()));
                cardLayout.show(mainPanel, "main");
                refreshBookTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "로그인 실패: " + ex.getMessage());
            }
        });

        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "register"));

        addBookButton.addActionListener(e -> showAddBookDialog());  // 책 등록 다이얼로그 호출

        mainPanel.add(loginPanel, "login");
    }

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

        JButton submitButton = new JButton("가입하기");
        JButton backButton = new JButton("뒤로가기");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = gridy;
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
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "회원가입 실패: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        mainPanel.add(registerPanel, "register");
    }

    private void initMainPanel() {
        JPanel mainMenuPanel = new JPanel(new BorderLayout());

        // 상단 메뉴 패널
        JPanel menuPanel = new JPanel(new FlowLayout());
        JButton searchButton = new JButton("책 검색");
        JButton addBookButton = new JButton("책 등록");
        JButton myLoansButton = new JButton("내 대출 목록");
        JButton logoutButton = new JButton("로그아웃");

        menuPanel.add(searchButton);
        menuPanel.add(addBookButton);
        menuPanel.add(myLoansButton);
        menuPanel.add(logoutButton);

        // 중앙 테이블 패널
        String[] columns = {"ID", "제목", "출판사", "도서번호", "대출가능", "대출처리"};
        DefaultTableModel bookTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // 대출처리 버튼 컬럼만 편집 가능
            }
        };
        JTable bookTable = new JTable(bookTableModel);

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

        // 이벤트 처리
        searchButton.addActionListener(e -> {
            String keyword = JOptionPane.showInputDialog("검색어를 입력하세요:");
            if (keyword != null && !keyword.trim().isEmpty()) {
                searchBooks(keyword, bookTableModel);
            }
        });

        addBookButton.addActionListener(e -> showAddBookDialog());

        myLoansButton.addActionListener(e -> showLoansDialog());

        logoutButton.addActionListener(e -> {
            currentMember = null;
            cardLayout.show(mainPanel, "login");
        });

        mainPanel.add(mainMenuPanel, "main");
    }

    private void addFormField(JPanel panel, String label, JComponent field,
                              GridBagConstraints gbc, int y) {
        gbc.gridx = 0; gbc.gridy = y;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void showAddBookDialog() {
        JDialog dialog = new JDialog(this, "새 책 등록", true);
        JPanel dialogPanel = new JPanel(new GridBagLayout());
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField titleField = new JTextField(20);
        JTextField publisherField = new JTextField(20);
        JTextField bookNumberField = new JTextField(20);

        addFormField(dialogPanel, "제목:", titleField, gbc, 0);
        addFormField(dialogPanel, "출판사:", publisherField, gbc, 1);
        addFormField(dialogPanel, "도서번호:", bookNumberField, gbc, 2);

        JButton submitButton = new JButton("등록");
        submitButton.addActionListener(e -> {
            Book book = new Book();
            book.setTitle(titleField.getText());
            book.setPublisher(publisherField.getText());
            book.setBookNumber(bookNumberField.getText());
            book.setAvailable(true);
            book.setLoanCnt(0);
            book.setPageCnt(0);

            try {
                bookService.createBook(book);
                JOptionPane.showMessageDialog(dialog, "책 등록 성공!");
                dialog.dispose();
                refreshBookTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "책 등록 실패: " + ex.getMessage());
            }
        });

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(submitButton, gbc);

        dialog.add(dialogPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showLoansDialog() {
        if (currentMember == null) return;

        JDialog dialog = new JDialog(this, "내 대출 목록", true);
        dialog.setLayout(new BorderLayout());

        String[] columns = {"책 제목", "대출일", "반납예정일", "상태"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        List<Loan> loans = memberService.getMemberLoans(currentMember.getEmail());
        for (Loan loan : loans) {
            model.addRow(new Object[]{
                    loan.getBook().getTitle(),
                    loan.getLoanDate(),
                    loan.getReturnDate(),
                    loan.getOverdueDays() > 0 ? "연체 " + loan.getOverdueDays() + "일" : "정상"
            });
        }

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void searchBooks(String keyword, DefaultTableModel model) {
        model.setRowCount(0);
        List<Book> books = bookService.searchBooks(keyword);
        for (Book book : books) {
            model.addRow(new Object[]{
                    book.getId(),           // ID (숨김)
                    book.getBookNumber(),   // 도서번호
                    book.getTitle(),        // 제목
                    book.getPublisher(),    // 출판사
                    book.isAvailable() ? "가능" : "불가능",  // 대출가능 여부
                    book.isAvailable() ? "대출하기" : "-"    // 대출처리 버튼
            });
        }
    }

    private void refreshBookTable() {
        // 현재 메인 패널의 테이블 갱신
        JTable table = getBookTable();
        if (table != null) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            searchBooks("", model);
        }
    }

    private JTable getBookTable() {
        Component comp = ((JPanel)mainPanel.getComponent(2)).getComponent(1);
        if (comp instanceof JScrollPane) {
            return (JTable)((JScrollPane)comp).getViewport().getView();
        }
        return null;
    }

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Demp1008Application.class);
        SwingUtilities.invokeLater(() -> {
            MemberService memberService = context.getBean(MemberService.class);
            BookService bookService = context.getBean(BookService.class);
            new LibraryGUI(memberService, bookService).setVisible(true);
        });
    }
}
