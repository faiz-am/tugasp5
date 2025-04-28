package pemkom2;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Server extends JFrame {
    private JButton startButton;
    private JTextArea textArea;
    private ServerSocket serverSocket;
    private Socket socket;

    public Server() {
        setTitle("Server - File Receiver");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        startButton = new JButton("Start Server");
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        add(startButton, "North");
        add(scrollPane, "Center");

        startButton.addActionListener(e -> startServer());
    }

    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5000);
                textArea.append("Server started. Waiting for client...\n");

                socket = serverSocket.accept();
                textArea.append("Client connected.\n");

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String fileName = dis.readUTF();
                long fileSize = dis.readLong();

                FileOutputStream fos = new FileOutputStream("received_" + fileName);
                byte[] buffer = new byte[4096];

                int read;
                long totalRead = 0;
                long remaining = fileSize;

                while ((read = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {
                    totalRead += read;
                    remaining -= read;
                    fos.write(buffer, 0, read);
                }

                textArea.append("File received: " + fileName + " (" + fileSize + " bytes)\n");

                fos.close();
                dis.close();
                socket.close();
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                textArea.append("Error: " + ex.getMessage() + "\n");
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Server().setVisible(true);
        });
    }
}
