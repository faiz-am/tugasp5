/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pemkom2;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client extends JFrame {
    private JButton connectButton;
    private JButton sendButton;
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private Socket socket;
    private DataOutputStream dos;
    private File selectedFile;

    public Client() {
        setTitle("Client - File Sender");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectButton = new JButton("Connect to Server");
        sendButton = new JButton("Choose File & Send");
        sendButton.setEnabled(false);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel topPanel = new JPanel();
        topPanel.add(connectButton);
        topPanel.add(sendButton);

        add(topPanel, "North");
        add(scrollPane, "Center");

        fileChooser = new JFileChooser();

        connectButton.addActionListener(e -> connectToServer());
        sendButton.addActionListener(e -> chooseAndSendFile());
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5000); // Ganti IP kalau beda device
            dos = new DataOutputStream(socket.getOutputStream());
            textArea.append("Connected to server.\n");
            sendButton.setEnabled(true);
            connectButton.setEnabled(false);
        } catch (IOException ex) {
            ex.printStackTrace();
            textArea.append("Connection failed: " + ex.getMessage() + "\n");
        }
    }

    private void chooseAndSendFile() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            new Thread(() -> sendFile()).start();
        }
    }

    private void sendFile() {
        try {
            String fileName = selectedFile.getName();
            long fileSize = selectedFile.length();

            dos.writeUTF(fileName);
            dos.writeLong(fileSize);

            FileInputStream fis = new FileInputStream(selectedFile);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, read);
            }
            textArea.append("File sent: " + fileName + " (" + fileSize + " bytes)\n");

            fis.close();
            dos.close();
            socket.close();
            sendButton.setEnabled(false);
        } catch (IOException ex) {
            ex.printStackTrace();
            textArea.append("Error sending file: " + ex.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Client().setVisible(true);
        });
    }
}
