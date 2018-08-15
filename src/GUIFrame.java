import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

class GUIFrame
{
  private static JPanel panel;
  private static JButton goButton;
  private static JFrame frame;

  private static JTextField emailTextField;
  private static JPasswordField passwordTextField;
  private static JTextField destinationTextField;

  private static Font font;

  static void createView()
  {
    frame = new JFrame();
    createPanel();
    createEmailFields();
    createPasswordFields();
    createDestinationFields();
    createButtons();
    setFrameSettings();
  }

  private static void createPanel()
  {
    panel = new JPanel();
    panel.setBackground(Color.BLACK);
    frame.getContentPane().add(panel);
  }

  private static void createEmailFields()
  {
    font = new Font("SansSerif", Font.BOLD, 25);
    JLabel emailLabel = new JLabel();
    emailLabel.setText("Email Address");
    emailLabel.setForeground(Color.WHITE);
    emailLabel.setFont(font);
    panel.add(emailLabel);

    emailTextField = new JTextField();
    emailTextField.setPreferredSize(new Dimension(630, 60));
    emailTextField.setFont(font);
    panel.add(emailTextField);
  }

  private static void createPasswordFields()
  {
    JLabel passwordLabel = new JLabel();
    passwordLabel.setText("Password        ");
    passwordLabel.setForeground(Color.WHITE);
    passwordLabel.setFont(font);
    panel.add(passwordLabel);

    passwordTextField = new JPasswordField();
    passwordTextField.setPreferredSize(new Dimension(630, 60));
    passwordTextField.setFont(font);
    panel.add(passwordTextField);
  }

  private static void createDestinationFields()
  {
    JLabel destinationLabel = new JLabel();
    destinationLabel.setText("Destination CSV  ");
    destinationLabel.setForeground(Color.WHITE);
    destinationLabel.setFont(font);
    panel.add(destinationLabel);

    destinationTextField = new JTextField();
    destinationTextField.setPreferredSize(new Dimension(600, 60));
    destinationTextField.setFont(font);
    panel.add(destinationTextField);
  }

  private static void createButtons()
  {
    goButton = new JButton("GO");
    goButton.setPreferredSize(new Dimension(80, 60));
    goButton.setFont(font);
    goButton.addActionListener(e -> {
      EmailClient.doTheClick();
      frame.dispose();
    });
    panel.add(goButton);
  }

  private static void setFrameSettings()
  {
    frame.setTitle("CSV Email Client");
    frame.setPreferredSize(new Dimension(830, 300));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setVisible(true);
    frame.getRootPane().setDefaultButton(goButton);
  }

  static JTextField getEmailTextField()
  {
    return emailTextField;
  }

  static JPasswordField getPasswordTextField()
  {
    return passwordTextField;
  }

  static JTextField getDestinationTextField()
  {
    return destinationTextField;
  }
}
