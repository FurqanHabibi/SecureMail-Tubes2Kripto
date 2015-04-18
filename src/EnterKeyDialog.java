import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;


public class EnterKeyDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 
	public static void main(String[] args) {
		try {
			EnterKeyDialog dialog = new EnterKeyDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
	
	private String key;
	private Callback okayCallback;
	
	/**
	 * Create the dialog.
	 */
	public EnterKeyDialog(Callback _okayCallback) {
		okayCallback = _okayCallback;
		setBounds(100, 100, 450, 256);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JTextArea textArea = new JTextArea();
			textArea.setBounds(10, 123, 414, 45);
			JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane.setBounds(10, 32, 414, 81);
			contentPanel.add(scrollPane);
		}
		
		JLabel lblEnterKey = new JLabel("Enter key:");
		lblEnterKey.setBounds(10, 11, 64, 14);
		contentPanel.add(lblEnterKey);
		
		JButton btnChooseFile = new JButton("Choose file...");
		btnChooseFile.setBounds(10, 145, 119, 23);
		contentPanel.add(btnChooseFile);
		
		JLabel lblNewLabel = new JLabel("Or browse file:");
		lblNewLabel.setBounds(10, 124, 84, 23);
		contentPanel.add(lblNewLabel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						okayCallback.doAction(key);
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
