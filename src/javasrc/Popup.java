package javasrc;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class Popup
{
  public static void displayMessage(int width, int height, String message, String title, int messageType, boolean exit)
  {

    JTextArea textArea = new JTextArea(message);
    textArea.setFont(new Font("SansSerif", Font.BOLD, 15));
    textArea.setSize( new Dimension(width, height));


    UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("ARIAL", Font.PLAIN, 35)));
    JOptionPane.showMessageDialog(null, textArea, title, messageType);
    if(exit) System.exit(0);
  }
}
