package edu.cmu.cmulib.API.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import edu.cmu.cmulib.Master_forUI;
import org.netlib.util.booleanW;

import edu.cmu.cmulib.API.data.DoubleColumnInterpolationStrategy;
import edu.cmu.cmulib.API.data.EmptyWrongDataStrategy;
import edu.cmu.cmulib.API.data.HotDeckStrategy;
import edu.cmu.cmulib.API.data.SampleVisualizationProcessor;
import edu.cmu.cmulib.API.data.WrongDataTypeStrategy;
import edu.cmu.cmulib.API.SVD.Calculate1svd;

public class UI extends JPanel {

	private static final long serialVersionUID = -3568891808859925700L;
	private static final String[] ALGOS_NAME = { "SVD", "Decision Tree" };
	private static final String[] VISUA_STRATEGY = { "2D", "3D", "paracoord" };
	private static final String[] FILE_TO_DUMP_NAME = { "Input", "Result" };
	private static final String TITLE = "CMULib";
	private static final int GRID_GAP = 10;
	private static final int BORDER_LEN = 10;
	private static final int FIELD_LEN = 30;
	private static final int INPUT_ITEM_NUM = 3;
	private static final int DUMP_ITEM_NUM = 2;
	private static final int TEXT_AREA_WIDTH = 50;
	private static final int TEXT_AREA_HEIGHT = 20;
	private static boolean advanceFlag = true;
	private final JFrame frame = new JFrame(TITLE);

	/** Input path, output folder, algorithm */
	private final JTextField inputPathField = new JTextField(FIELD_LEN);
	private final JTextField outputFolderField = new JTextField(FIELD_LEN);
	private final JButton browse1Btn = new JButton("Browse");
	private final JButton browse2Btn = new JButton("Browse");
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final JComboBox algoListBox = new JComboBox(ALGOS_NAME);
	private final JComboBox visuListBox = new JComboBox(VISUA_STRATEGY);
	private final JComboBox sampListBox = new JComboBox();
	private final JComboBox<WrongDataTypeStrategy> dataHandlingBox = new JComboBox<WrongDataTypeStrategy>();

	/** prepossing area */
	private final JButton advanceButton = new JButton("Advanced Options");

	private final JButton Visualize = new JButton("Visualize");
	private final JButton Sampling = new JButton("Sample");
	/** run button and progress area */
	private final JButton runBtn = new JButton("Run");
	private final JTextArea progressArea = new JTextArea(TEXT_AREA_HEIGHT,
			TEXT_AREA_WIDTH);

	/** file to download, download to, start downloading */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final JComboBox fileListBox = new JComboBox(FILE_TO_DUMP_NAME);
	private final JTextField dumpPathField = new JTextField(FIELD_LEN);
	private final JButton browse3Btn = new JButton("Browse");
	private final JButton startDumpBtn = new JButton("Start Downloading");


	/** various file path string */
	private final JFileChooser inputPathFC = new JFileChooser();
	private final JFileChooser outputFolderFC = new JFileChooser();
	private final JFileChooser downloadFolderFC = new JFileChooser();
	private JPanel prePanel = new JPanel();

	public File inputFile;
	public File outputFolder;
	private File downloadToFolder;
	private String algoName = "SVD";
	private String fileToDownloadOption;

	private final Master_forUI core;

	// private final Model model

	public UI(Master_forUI inputcore) {
		// model = inputModel;
		core = inputcore;

		JPanel labelsPanel = new JPanel();
		labelsPanel.setLayout(new GridLayout(INPUT_ITEM_NUM, 1, GRID_GAP,
				GRID_GAP));
		labelsPanel.add(new JLabel("Input Path: "));
		labelsPanel.add(new JLabel("Output Folder: "));
		labelsPanel.add(new JLabel("Algorithm: "));
		// labelsPanel.add(new JLabel("Missing Data Handling: "));
		labelsPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_LEN,
				BORDER_LEN, BORDER_LEN, BORDER_LEN));

		JPanel fieldsPanel = new JPanel();
		fieldsPanel.setLayout(new GridLayout(INPUT_ITEM_NUM, 1, GRID_GAP,
				GRID_GAP));
		fieldsPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_LEN,
				BORDER_LEN, BORDER_LEN, BORDER_LEN));

		JPanel grid1 = new JPanel();
		grid1.add(inputPathField);
		grid1.add(browse1Btn);
		fieldsPanel.add(grid1);

		JPanel grid2 = new JPanel();
		grid2.add(outputFolderField);
		grid2.add(browse2Btn);
		fieldsPanel.add(grid2);

		JPanel grid3 = new JPanel();
		grid3.add(algoListBox);
		fieldsPanel.add(grid3);

		// JPanel grid4 = new JPanel();
		// List<WrongDataTypeStrategy> stras =
		// this.core.getWrongDataStrategies();
		// for (WrongDataTypeStrategy stra : stras) {
		// dataHandlingBox.addItem(stra);
		// }
		// grid4.add(dataHandlingBox);
		// fieldsPanel.add(grid4);

		/** input panel */
		// advanceButton.setPreferredSize(new Dimension(500, 50));

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());
		inputPanel.add(labelsPanel, BorderLayout.WEST);
		inputPanel.add(fieldsPanel, BorderLayout.EAST);
		inputPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_LEN,
				BORDER_LEN, BORDER_LEN, BORDER_LEN));

		/** prepossing panel #Shen */
		// JPanel prePanel = new JPanel();
		// prePanel.add(advanceButton);

		prePanel.add(new JLabel("Sampling Strategy:   "));
		prePanel.add(sampListBox);
		prePanel.add(Sampling);

		prePanel.add(new JLabel("Visualization Strategy:   "));
		prePanel.add(visuListBox);
		prePanel.add(Visualize);

		prePanel.add(new JLabel("Missing Data Handling: "));

		List<WrongDataTypeStrategy> stras = this.core.getWrongDataStrategies();
		for (WrongDataTypeStrategy stra : stras) {
			dataHandlingBox.addItem(stra);
		}
		prePanel.add(dataHandlingBox);

		prePanel.setLayout(new GridLayout(3, 1, 3, GRID_GAP));
		prePanel.setBorder(BorderFactory.createEmptyBorder(BORDER_LEN,
				5 * BORDER_LEN, 5 * BORDER_LEN, 5 * BORDER_LEN));

		/** run panel */
		JPanel runPanel = new JPanel();
		runPanel.add(runBtn);
		runBtn.setPreferredSize(new Dimension(500, 50));
		runPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_LEN,
				5 * BORDER_LEN, 5 * BORDER_LEN, 5 * BORDER_LEN));

        /** advance panel */
        JPanel advancePanel = new JPanel();
        advancePanel.add(advanceButton);
        advanceButton.setPreferredSize(new Dimension(500, 50));
        advancePanel.setBorder(BorderFactory.createEmptyBorder(BORDER_LEN,
                5 * BORDER_LEN, 5 * BORDER_LEN, 5 * BORDER_LEN));

		/** progress panel */
		JPanel progressPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(progressArea);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		progressPanel.add(scrollPane);
		progressPanel.setBorder(new TitledBorder(new EtchedBorder(),
				"Progress & Result", TitledBorder.CENTER,
				TitledBorder.DEFAULT_POSITION));

		/** dump Panel */
		JPanel dumpPanel = new JPanel();

		JPanel pathNamePanel = new JPanel();
		pathNamePanel.setLayout(new GridLayout(DUMP_ITEM_NUM, 1, GRID_GAP,
				GRID_GAP));

		pathNamePanel.add(new JLabel("File to Download: "));
		pathNamePanel.add(new JLabel("Download to: "));

		JPanel pathFieldPanel = new JPanel();
		pathFieldPanel.setLayout(new GridLayout(DUMP_ITEM_NUM, 1, GRID_GAP,
				GRID_GAP));
		pathFieldPanel.add(fileListBox);
		JPanel downtoBrowse = new JPanel();
		downtoBrowse.add(dumpPathField);
		downtoBrowse.add(browse3Btn);
		pathFieldPanel.add(downtoBrowse);

		dumpPanel.setLayout(new BorderLayout());
		dumpPanel.add(pathNamePanel, BorderLayout.WEST);
		dumpPanel.add(pathFieldPanel, BorderLayout.EAST);
		dumpPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_LEN,
				BORDER_LEN, BORDER_LEN, BORDER_LEN));

		/** start downloading button panel */
		JPanel startDownPanel = new JPanel();
		startDownPanel.add(startDumpBtn);
		startDumpBtn.setPreferredSize(new Dimension(500, 50));
		startDownPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_LEN,
				BORDER_LEN, 5 * BORDER_LEN, BORDER_LEN));

		/** put together */
        /*
		prePanel.setVisible(false);
		frame.getContentPane().setLayout(
				new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.getContentPane().add(inputPanel);
		frame.getContentPane().add(advanceButton);
		frame.getContentPane().add(prePanel);
		frame.getContentPane().add(runPanel);
		frame.getContentPane().add(progressPanel);
		frame.getContentPane().add(dumpPanel);
		frame.getContentPane().add(startDownPanel);

		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		*/

        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        JButton dataInjectionBtn = new JButton();
        leftPanel.add(inputPanel);
        // // advanceButton.setPreferredSize(new Dimension(10, 10));
        // // inputPanel.add(advanceButton);
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.X_AXIS));
        leftPanel.add(advancePanel);
        leftPanel.add(prePanel);
        leftPanel.add(runPanel);
        rightPanel.add(progressPanel);
        rightPanel.add(dumpPanel);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        frame.getContentPane().setLayout(
                new GridLayout(1, 1, GRID_GAP, GRID_GAP));

        frame.getContentPane().add(leftPanel);
        frame.getContentPane().add(rightPanel);

        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /** SET ALL ACTION LISTENER HERE */
        setAllActionListener();
	}

	private void setAllActionListener() {

		browse1Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int returnVal = inputPathFC.showOpenDialog(UI.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					inputFile = inputPathFC.getSelectedFile();
					try {
						inputPathField.setText(inputFile.getCanonicalPath());
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		});

		browse2Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				outputFolderFC.setCurrentDirectory(new java.io.File("."));
				outputFolderFC
						.setDialogTitle("Choose Folder to put output file");
				outputFolderFC
						.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				outputFolderFC.setAcceptAllFileFilterUsed(false);
				int returnVal = outputFolderFC.showOpenDialog(UI.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					outputFolder = outputFolderFC.getCurrentDirectory();
					try {
						outputFolderField.setText(outputFolder
								.getCanonicalPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		browse3Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				downloadFolderFC.setCurrentDirectory(new java.io.File("."));
				downloadFolderFC
						.setDialogTitle("Choose Folder to put output file");
				downloadFolderFC
						.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				downloadFolderFC.setAcceptAllFileFilterUsed(false);
				int returnVal = downloadFolderFC.showOpenDialog(UI.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					downloadToFolder = downloadFolderFC.getCurrentDirectory();
					try {
						dumpPathField.setText(downloadToFolder
								.getCanonicalPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		runBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				algoName = (String) algoListBox.getSelectedItem();
				if (algoName.equals("SVD")) {
                    try {
                        updateprogressArea("***************** Start Job ***************************\n");
                        core.getProcessor().setWrongDataTypeStrategy((WrongDataTypeStrategy) dataHandlingBox.getSelectedItem());
                        //core.svdMaster(inputFile.getCanonicalPath(),
                        //       outputFolder.getCanonicalPath());
                        updateprogressArea("input file set as: " + inputFile.getCanonicalPath() + "\n");
                        updateprogressArea("output file path set as: " + outputFolder.getCanonicalPath() + "\n");
                        Thread svdRunner = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    core.startRun(inputFile.getCanonicalPath(),
                                            outputFolder.getCanonicalPath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        svdRunner.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        onShowingAlert("Error", "An exception is thrown during process");
                    }
				}

			}
		});

		advanceButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent event) {

				prePanel.setVisible(advanceFlag);

				advanceFlag = !advanceFlag;

				frame.revalidate();
				frame.repaint();
				frame.pack();

			}
		});

		startDumpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				fileToDownloadOption = (String) fileListBox.getSelectedItem();

				if (fileToDownloadOption.equals("Input")) {

				} else if (fileToDownloadOption.equals("Result")) {

				}
			}
		});

		Visualize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
                System.out.println("visualized listener");

				SampleVisualizationProcessor sampleVisualizationProcessor = new SampleVisualizationProcessor();
				sampleVisualizationProcessor.visualize((String) visuListBox
						.getSelectedItem());


			}
		});
	}

	public void updateprogressArea(String str) {
		progressArea.append(str);
	}

	/** show the frame */
	public void show() {
		frame.setVisible(true);
	}

	/** create GUI and show it */
	public static void createAndShowGUI() {
		Master_forUI core = new Master_forUI();
		core.registerDataStrategy(new EmptyWrongDataStrategy());
		core.registerDataStrategy(new DoubleColumnInterpolationStrategy(0.0));
		core.registerDataStrategy(new HotDeckStrategy(0.0));
        try {
            core.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UI gui = new UI(core);
		core.setUI(gui);
		gui.show();
	}

	public static void main(String[] args) {
        /*
		try {

			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			//
			// .setLookAndFeel("com.sun.java.swing.plaf.nimbus.SynthLookAndFeel");

		} catch (Exception e) {
			e.printStackTrace();
		}
        */
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public void onShowingAlert(String title, String message) {
		JFrame frame = (JFrame) SwingUtilities.getRoot(this);
		showDialog(frame, title, message);
	}

	/**
	 * show dialog
	 * 
	 * @param component
	 *            component
	 * @param title
	 *            title of the dialog
	 * @param message
	 *            message of the dialog
	 */
	private static void showDialog(Component component, String title,
			String message) {
		JOptionPane.showMessageDialog(component, message, title,
				JOptionPane.INFORMATION_MESSAGE);
	}

}
