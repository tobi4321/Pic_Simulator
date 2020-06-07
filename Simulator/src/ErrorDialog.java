import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
/// class ErrorDialog
/**
*  This class is used to display error windows with variable title and text.
* **/
public class ErrorDialog extends JDialog {

	/// 
	private static final long serialVersionUID = 1L;

	/// The content panel of the error window.
	private static ErrorDialog dialog;
	private final JPanel contentPanel = new JPanel();
	/// The title of the error window.
	protected JLabel lbl_ErrorTitle;
	/// The text displayed in the error window.
	protected JLabel lbl_ErrorText;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			 dialog = new ErrorDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The Method to create and display the error window.
	 */
	public ErrorDialog() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 639, 301);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		{
			 lbl_ErrorTitle = new JLabel("\u00DCberschrift");
			contentPanel.add(lbl_ErrorTitle);
		}
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.CENTER);
			{
				lbl_ErrorText = new JLabel("Text");
				panel.add(lbl_ErrorText);
				lbl_ErrorText.setFont(new Font("Tahoma", Font.PLAIN, 16));
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
