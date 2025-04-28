package pemkom2;
//aktifkan server dulu
import javax.swing.*;
import java.io.*;
import java.net.*;

public class Server extends JFrame {
    private JTextArea textArea;
    private JTextField messageField;
    private JButton sendButton;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public Server() {
        setTitle("Server Chat");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Membuat komponen GUI
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); // Menjaga scrollbar

        messageField = new JTextField(30);
        sendButton = new JButton("Send");

        // Menambahkan event handler untuk tombol kirim
        sendButton.addActionListener(e -> sendMessage());

        // Mengatur layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(scrollPane);  // Menambahkan area chat
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(messageField);
        bottomPanel.add(sendButton);
        panel.add(bottomPanel);

        add(panel);

        // Menjalankan server chat
        startServer();
    }

    private void startServer() {
        new Thread(() -> {
            try {
                int port = 12345;
                serverSocket = new ServerSocket(port);
                textArea.append("Server listening on port " + port + "...\n");

                // Menunggu koneksi dari client
                clientSocket = serverSocket.accept();
                textArea.append("Client connected: " + clientSocket.getInetAddress() + "\n");

                dis = new DataInputStream(clientSocket.getInputStream());
                dos = new DataOutputStream(clientSocket.getOutputStream());

                // Menerima pesan dari client
                String message;
                while ((message = dis.readUTF()) != null) {
                    textArea.append("Client: " + message + "\n");
                    textArea.setCaretPosition(textArea.getDocument().getLength()); // Scroll otomatis ke bawah
                }
            } catch (IOException e) {
                textArea.append("Error: " + e.getMessage() + "\n");
            }
        }).start();
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            try {
                dos.writeUTF(message); // Mengirimkan pesan ke client
                textArea.append("Server: " + message + "\n");
                textArea.setCaretPosition(textArea.getDocument().getLength()); // Scroll otomatis ke bawah
                messageField.setText(""); // Menghapus field input
            } catch (IOException e) {
                textArea.append("Error: " + e.getMessage() + "\n");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Server().setVisible(true);
        });
    }
}
