/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pemkom2;
import javax.swing.*;
import java.io.*;
import java.net.*;
//aktifkan server dulu
public class Client extends JFrame {
    private JTextArea textArea;
    private JTextField messageField;
    private JButton sendButton;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public Client() {
        setTitle("Client Chat");
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

        // Menjalankan client chat
        startClient();
    }

    private void startClient() {
        new Thread(() -> {
            try {
                String serverAddress = "127.0.0.1"; // Ganti dengan IP server
                int port = 12345;
                socket = new Socket(serverAddress, port);
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());

                // Menerima pesan dari server
                String message;
                while ((message = dis.readUTF()) != null) {
                    textArea.append("Server: " + message + "\n");
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
                dos.writeUTF(message); // Mengirimkan pesan ke server
                textArea.append("Client: " + message + "\n");
                textArea.setCaretPosition(textArea.getDocument().getLength()); // Scroll otomatis ke bawah
                messageField.setText(""); // Menghapus field input
            } catch (IOException e) {
                textArea.append("Error: " + e.getMessage() + "\n");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Client().setVisible(true);
        });
    }
}
