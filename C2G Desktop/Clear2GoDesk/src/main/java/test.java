import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.google.firebase.*;
public class test extends JFrame {
    private JButton button1;
    private JPanel panel1;
    private JLabel pana;


    public test() {
        super("Simple Form");
        panel1  = new JPanel();
        button1 = new JButton();
        pana = new JLabel();

        panel1.add(button1);
        panel1.add(pana);
        setContentPane(panel1);
        // ... (rest of the code)
        pana.setVisible(true);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Your button click action here (e.g., display a message)
                pana.setText("Button clicked!");
            }
        });
    }
    public static void main(String[] args) {
        JFrame frame = new test();
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}

