import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Hugo on 2017-05-18.
 */
public class ConvertToXMLGUI extends JFrame implements ActionListener{
    private JTextField fileNameField;
    private JButton openButton;
    private JTextField xmlNameField;
    private JButton convertButton;
    private JPanel contentPanel;

    File file;

    public ConvertToXMLGUI() {
        super("Convert to xml");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new FlowLayout());
        add(contentPanel);
        pack();

        openButton.addActionListener(this);
        convertButton.addActionListener(this);

        setVisible(true);
    }

    public static void main(String[] args) {
        new ConvertToXMLGUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == openButton) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();

                fileNameField.setText(file.getName());

                xmlNameField.setEditable(true);

                convertButton.setEnabled(true);
            }

        }

        if(e.getSource() == convertButton) {
            new ConvertToXML().convertFileToXML(file, xmlNameField.getText());
        }
    }
}
