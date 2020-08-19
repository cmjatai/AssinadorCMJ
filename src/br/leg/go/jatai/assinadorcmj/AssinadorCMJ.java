package br.leg.go.jatai.assinadorcmj;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.pdfa.PdfADocument;

import imageUtil.ImageLoader;

public class AssinadorCMJ extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 532280064802860134L;

	int screenHeight = 650;
	int screenWidth = 1024;

	private JPanel jContentPane = null;
	private JPanel pData = null;

	private JButton btnSelectFile = null;
	private JButton btnGirarImagem = null;
	private JButton btnCreatePDF = null;

	private JLabel jLabelMessage = null;
	private JLabel labelMax1 = null;
	private JLabel labelMax2 = null;

	private JScrollPane panelListFiles = null;
	private JList<String> listFiles = null;
	private JCheckBox chkImageList = null;
	private JSpinner maxSizeFileOutput = null;
	private JPanel panelImage = null;

	private File files[] = null;
	private int rotates[] = null;
	private BufferedImage[] images = null;

	private int rotate[] = {0, 90, 180, -90};

	public static void showMessage(String info, String title, int tipo) {
		javax.swing.JDialog f = new javax.swing.JDialog();
		f.setSize(220, 150);
		javax.swing.JOptionPane.showMessageDialog(f, info, title, tipo);
	}

	/**
	 * @param args
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws URISyntaxException, IOException {
		
		/*
		 * CodeSource codeSource =
		 * AssinadorCMJ.class.getProtectionDomain().getCodeSource(); String path =
		 * codeSource.getLocation().getPath(); if(args.length==0 &&
		 * Runtime.getRuntime().maxMemory()/1024/1024<15360) { String command =
		 * "java -Xmx2048m -jar \""+path+"AssinadorCMJ.jar\" restart";
		 * System.out.println(command); Runtime.getRuntime().exec(command); return; }
		 */

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				AssinadorCMJ thisClass = new AssinadorCMJ();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
				AssinadorCMJ.leftTop(thisClass);
			}
		});
	}

	public static void center(Component componente) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle r = componente.getBounds();
		int widthSplash = r.width;
		int heightSplash = r.height;
		int posX = (screen.width / 2) - (widthSplash / 2);
		int posY = (screen.height / 2) - (heightSplash / 2);

		componente.setBounds(posX, posY, widthSplash, heightSplash);
	}

	public static void leftTop(Component componente) {
		// Centraliza a janela de abertura no centro do desktop.
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle r = componente.getBounds();
		int widthSplash = r.width;
		int heightSplash = r.height;

		int posX = 50;
		int posY = 50;

		componente.setBounds(posX, posY, widthSplash, heightSplash);
	}

	public AssinadorCMJ() {
		super();
		initialize();
	}

	private void initialize() {

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jContentPane = new JPanel();
		jContentPane.setLayout(null);
		jContentPane.setOpaque(false);
		jContentPane.add(getFrm(), null);

		this.setContentPane(jContentPane);

		this.setTitle("JFrame");

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent e) {
				onWindowOpened();
			}
		});

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				listenerResize(e);
			}

		});
		this.addWindowStateListener(new WindowStateListener() {

			@Override
			public void windowStateChanged(WindowEvent e) {
				listenerResize(e);
			}
		});
	}

	private void listenerResize(ComponentEvent e) {
		pData.setBounds(new Rectangle(2, 2, e.getComponent().getWidth(), e.getComponent().getHeight()));
		chkImageList.setLocation(5, pData.getHeight() - 70);
		panelListFiles.setSize(220, pData.getHeight() - 75);

		if (e instanceof WindowEvent) 
			listFiles.setSize(220, pData.getHeight() - 75);

		panelImage.setSize(pData.getWidth() - panelImage.getLocation().x - 10,
				pData.getHeight() - panelImage.getLocation().y - 40);
	}

	protected void onWindowOpened() {
		setTitle("Assinador CMJ - a parte de assinatura digital ainda está em construção.");
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();

		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setSize(new Dimension(screenWidth, screenHeight));

		pData.setPreferredSize(
				new Dimension((int) (getPreferredSize().width - 5), (int) (getPreferredSize().height - 5)));
		pData.setSize(new Dimension((int) (getPreferredSize().width - 5), (int) (getPreferredSize().height - 5)));
	}

	private JPanel getFrm() {
		if (pData == null) {
			pData = new JPanel();
			pData.setLayout(null);

			pData.add(getLabelMessage(), null);

			pData.add(getButtonCreatePDF(), null);
			pData.add(getJCheckBoxImageList(), null);
			pData.add(getMaxSizeFileOutput(), null);
			pData.add(getButtonSelectFile(), null);
			pData.add(getPanelImage(), null);

			pData.add(getButtonGirarImagemSelecionada(), null);
			pData.add(getJPanelListFiles(), null);

		}
		return pData;
	}

	private JPanel getPanelImage() {

		if (panelImage == null) {

			panelImage = new JPanel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					renderImageSelected();
				}
			};
			panelImage.setLayout(new BorderLayout());

			panelImage.setLocation(235, 55);
			panelImage.setSize(2000, 2000);

			panelImage.setBackground(Color.WHITE);
			panelImage.setBorder(BorderFactory.createEtchedBorder());

			panelImage.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					renderImageSelected();
				}
			});
		}
		return panelImage;
	}

	private JButton getButtonCreatePDF() {

		btnCreatePDF = new JButton();
		btnCreatePDF.setForeground(new Color(0, 0, 0));
		btnCreatePDF.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnCreatePDF.setText("Gerar PDF");

		btnCreatePDF.setLocation(234, 7);
		btnCreatePDF.setSize(120, 20);
		btnCreatePDF.setMargin(new Insets(0, 0, 0, 0));

		btnCreatePDF.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionBtnCreatePDF();
			}
		});

		return btnCreatePDF;
	}

	public static long midiaToEscala(String midia, float escala, float quality) throws IOException {
		imageUtil.Image image = ImageLoader.fromFile(midia);

		int width = (int) (image.getWidth() * escala);

		String newFile = midia + ".jpg";
		image.getResizedToWidth(width).soften(0.08f).writeToJPG(new File(newFile), quality);

		return 0;
	}

	protected void actionBtnCreatePDF() {

		JFileChooser d = new JFileChooser();
		d.setMultiSelectionEnabled(false);
		d.setDialogTitle("Exportar PDF");

		File f = new File(ClassLoader.getSystemClassLoader().getResource(".").getFile());
		d.setCurrentDirectory(f);
		// c.setCurrentDirectory(new File("/home/leandro/Público/edital"));
		int rVal = d.showSaveDialog(this);

		String nameFile = "/tmp/file_" + (new GregorianCalendar().getTimeInMillis()) + ".pdf";
		if (rVal == JFileChooser.APPROVE_OPTION) {
			nameFile = d.getSelectedFile().getAbsolutePath();
		} else if (rVal == JFileChooser.CANCEL_OPTION) {
			return;
		}

		final String selectedNameFile = nameFile;
		
		jLabelMessage.setText("Gerando Arquivo...");
		new Thread(new Runnable() {				
			
			@Override
			public void run() {
				try {
					createPDF(selectedNameFile);
				} catch (Exception e) {
					jLabelMessage.setText("Não foi possível gerar o arquivo!");
					e.printStackTrace();
				}
			}
		}).start();
	}


	private void mergePDF(String outputNameFile) throws Exception {
		PdfWriter writer = new PdfWriter(outputNameFile);
		PdfDocument pdf = new PdfDocument(writer);

		Document document = new Document(pdf);
		document.setMargins(0, 0, 0, 0);

		for (int i = 0; i < files.length; i++) {

			if (images[i] == null) {
				PdfReader reader = new PdfReader(files[i]);
				PdfDocument pdfLoad = new PdfDocument(reader);
				pdfLoad.copyPagesTo(1, pdfLoad.getNumberOfPages(), pdf);
				//pdfLoad.close();
			} else {
				BufferedImage buf = images[i];
				pdf.addNewPage(buf.getHeight() > buf.getWidth() ? PageSize.A4 : PageSize.A4.rotate());
				Image pdfImg = getITextImageByBuf(buf);
				document.add(pdfImg);
			}
		}
		document.close();
		pdf.close();

		jLabelMessage.setText("Arquivo Gerado com sucesso no formato PDF.");
	}

	private void mergePDFa(String outputNameFile) throws Exception {
		PdfWriter writer = new PdfWriter(outputNameFile);
		InputStream inputStream = this.getClass().getResourceAsStream("/sRGB_CS_profile.icm");
		PdfADocument pdf = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B,
				new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", inputStream));

		Document document = new Document(pdf);
		document.setMargins(0, 0, 0, 0);

		for (int i = 0; i < files.length; i++) {

			if (images[i] == null) {
				PdfReader reader = new PdfReader(files[i]);
				PdfDocument pdfLoad = new PdfDocument(reader);
				pdfLoad.copyPagesTo(1, pdfLoad.getNumberOfPages(), pdf);
				//pdfLoad.close();
			} else {
				BufferedImage buf = images[i];
				pdf.addNewPage(buf.getHeight() > buf.getWidth() ? PageSize.A4 : PageSize.A4.rotate());
				Image pdfImg = getITextImageByBuf(buf);
				document.add(pdfImg);
			}
		}
		document.close();
		pdf.close();

		jLabelMessage.setText("Arquivo Gerado com sucesso no formato PDF/A-1B.");
	}

	private void constructPDF(String outputNameFile, int escala) throws Exception {
		PdfWriter writer = null;
		/*
		 * ByteArrayOutputStream bOut = new ByteArrayOutputStream(); if (outputNameFile
		 * == null) writer = new PdfWriter(bOut);
		 * 
		 */		writer = new PdfWriter(outputNameFile);


		 InputStream inputStream = this.getClass().getResourceAsStream("/sRGB_CS_profile.icm");
		 PdfADocument pdf = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B,
				 new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", inputStream));

		 Document document = new Document(pdf);
		 document.setMargins(0, 0, 0, 0);

		 for (int i = 0; i < files.length; i++) {			 
			 BufferedImage buf = null;
			 if (escala == 0) {
				 buf = getBufferedImageByIndice(i);
			 }
			 else {
				 buf = getBufferedImageByPath(files[i].getAbsolutePath() + ".jpg");
				 buf = rotateBufferedImage(i, buf);
			 }
			 pdf.addNewPage(buf.getHeight() > buf.getWidth() ? PageSize.A4 : PageSize.A4.rotate());
			 Image pdfImg = getITextImageByBuf(buf);
			 document.add(pdfImg);		 

			 if (escala != 0) {
				 File fdel = new File(files[i].getAbsolutePath() + ".jpg");
				 if (fdel.exists())
					 fdel.delete();
			 }
		 }
		 document.close();
		 pdf.close();

		 jLabelMessage.setText("Arquivo Gerado com sucesso no formato PDF/A-1B.");

	}

	private void createPDF(String outputNameFile) throws Exception {
		if (files.length == 0)
			return;
		boolean executeMerge = false;
		for (int i = 0; i < files.length; i++) {
			if (getBufferedImageByIndice(i) == null) {
				executeMerge = true;
			}
		}	

		if (executeMerge) {
			try {
				mergePDFa(outputNameFile);
			} catch (Exception e) {
				mergePDF(outputNameFile);
			}
			return;
		}

		int amax_size = (int) maxSizeFileOutput.getValue() * 1024 * 1024;
		
		if (amax_size != 0) {
			jLabelMessage.setText("A redução das imagens pode demorar alguns minutos. Aguarde processamento!");
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				int max_size = (int) maxSizeFileOutput.getValue() * 1024 * 1024;
				if (max_size == 0) {
					try {
						constructPDF(outputNameFile, 0);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						jLabelMessage.setText("Não foi possível gerar o arquivo!");
					}
					return;
				}

				long size = 0;

				for (int i = 0; i < files.length; i++) {
					File ff = new File(files[i].getAbsolutePath());
					size += ff.length();
				}

				if (size < max_size) {
					try {
						constructPDF(outputNameFile, 0);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						jLabelMessage.setText("Não foi possível gerar o arquivo!");

					}
					return;
				}

				final java.util.List<File> ls = Collections.synchronizedList(new ArrayList<File>());

				//float quality = ((float)(max_size - size) / size + 1); // * 1.1f;		
				//float escala = 0.99f;

				float quality = 0.8f; // * 1.1f;		
				float escala = (1 + (float)(max_size - size) / size /2);

				float min_escala = 0.4f;
				float min_quality = 0.4f;
				float min_min_quality = 0.3f;
				while (true) {
					final float th_escala = escala;
					final float th_quality = quality;

					for (int i = 0; i < files.length; i++) {
						ls.add(files[i]);
					}
					final CountDownLatch latch = new CountDownLatch(9);

					Runnable run = new Runnable() {
						@Override
						public void run() {
							File fff;
							while (!ls.isEmpty()) {
								synchronized (ls) {
									if (!ls.isEmpty()) {
										fff = ls.remove(0);
									} else
										fff = null;
								}

								if (fff != null) {
									try {
										//System.out.println(fff.getName() + " - " + th_escala + " - " + th_quality);
										midiaToEscala(fff.getAbsolutePath(), th_escala, th_quality);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
							latch.countDown();
						}
					};

					for (int n = 0; n < 9; n++) {
						new Thread(run).start();
					}
					try {
						latch.await();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						jLabelMessage.setText("Não foi possível gerar o arquivo!");

						return;
					}

					size = 0;
					for (int i = 0; i < files.length; i++) {
						File ff = new File(files[i].getAbsolutePath() + ".jpg");
						size += ff.length();
					}
					System.out.println("=======size:" + String.valueOf(size) + " escala:" + String.valueOf(escala)
					+ "  quality:" + String.valueOf(quality));			
					if (size > max_size) {
						for (int i = 0; i < files.length; i++) {
							File fdel = new File(files[i].getAbsolutePath() + ".jpg");
							fdel.delete();
						}
						if (escala > min_escala) {
							escala *= 0.95;
							continue;
						} else {
							quality *= 0.95;
							continue;
						}
					}
					try {
						constructPDF(outputNameFile, 1);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						jLabelMessage.setText("Não foi possível gerar o arquivo!");

					}
					return;
				}

			}
		}).start();


	}

	private Image getITextImageByBuf(BufferedImage buf) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( buf, "jpg", baos );
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();

		ImageData imageData = ImageDataFactory.create(imageInByte);
		Image pdfImg = new Image(imageData);

		if (buf.getHeight() > buf.getWidth())
			pdfImg.scaleAbsolute(595, 842);	
		else 
			pdfImg.scaleAbsolute(842, 595);
		return pdfImg;
	}

	private void createPDFOld(String nameFile) {
		int escala = 2500;
		int min_escala = 1300;
		float quality = 0.8f;
		float min_quality = 0.4f;
		float min_min_quality = 0.3f;
		float c = 1f;
		float taxa = 0.07f;
		int max_size = 9000000;

		final java.util.List<File> ls = Collections.synchronizedList(new ArrayList<File>());


		long size = 0;

		for (int i = 0; i < files.length; i++) {
			File ff = new File(files[i].getAbsolutePath());
			size += ff.length();
		}
		escala = (int) (min_escala + escala * (max_size / (float) size));

		imageUtil.Image image = null;
		try {
			image = ImageLoader.fromFile(files[0].getAbsolutePath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (image != null && image.getWidth() < escala)
			escala = (int) (image.getWidth() * 0.95);

		long media_base = (long) ((max_size / (float) files.length) * 1.2);

		c = 1;

		if ((int) maxSizeFileOutput.getValue() != 0)
			while (true) {

				try {
					midiaToEscala(files[0].getAbsolutePath(), escala, quality);
				} catch (IOException e) {
					break;
				}

				File ff = new File(files[0].getAbsolutePath() + ".jpg");
				long media = ff.length();
				System.out.println("Cálculo da média em: " + files[0].getName() + " - Escala:" + escala
						+ " - Qualidade:" + quality + " - Média Base:" + media_base + " - Media Atual:" + media);

				if (media < media_base)
					break;

				if (quality < min_quality)
					break;

				if (escala > min_escala) {
					escala *= 0.99;
					continue;
				} else if (escala != 0) {
					c += 1f;
					quality -= taxa / c;
					continue;
				}

			}

		c = 1;
		while (true) {

			try {

				if ((int) maxSizeFileOutput.getValue() == 0) {
					escala = 0;
				} else {

					for (int i = 0; i < files.length; i++) {
						ls.add(files[i]);
					}

					final int th_escala = escala;
					final float th_quality = quality;

					final CountDownLatch latch = new CountDownLatch(3);

					Runnable run = new Runnable() {
						@Override
						public void run() {
							File fff;
							while (!ls.isEmpty()) {
								synchronized (ls) {
									if (!ls.isEmpty()) {
										fff = ls.remove(0);
									} else
										fff = null;
								}

								if (fff != null) {
									try {
										System.out.println(fff.getName() + " - " + th_escala + " - " + th_quality);
										midiaToEscala(fff.getAbsolutePath(), th_escala, th_quality);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
							latch.countDown();
						}
					};

					for (int n = 0; n < 3; n++) {
						Thread t = new Thread(run);
						t.start();
					}
					latch.await();

					size = 0;
					for (int i = 0; i < files.length; i++) {
						File ff = new File(files[i].getAbsolutePath() + ".jpg");
						size += ff.length();
					}
					System.out.println("=======size:" + String.valueOf(size) + " escala:" + String.valueOf(escala)
					+ "quality:" + String.valueOf(quality));
					if (size > max_size) {
						for (int i = 0; i < files.length; i++) {
							File fdel = new File(files[i].getAbsolutePath() + ".jpg");
							fdel.delete();
						}
						if (escala > min_escala) {
							escala *= 0.95;
							continue;
						} else if (escala != 0) {
							c += 1f;
							quality -= taxa / c;
							continue;
						}
					}
				}

				InputStream inputStream = this.getClass().getResourceAsStream("/sRGB_CS_profile.icm");
				PdfADocument pdf = new PdfADocument(new PdfWriter(nameFile), PdfAConformanceLevel.PDF_A_1B,
						new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", inputStream));

				PageSize pageSize = PageSize.A4;


				Document document = new Document(pdf, pageSize);
				document.setMargins(0, 0, 0, 0);

				for (int i = 0; i < files.length; i++) {

					if (i + 1 < files.length)
						pdf.addNewPage();

					// if (paisagem[i])
					// document.setPageSize(PageSize.A4.rotate());
					// else
					// document.setPageSize(PageSize.A4);

					String path = files[i].getAbsolutePath() + (escala == 0 ? "" : ".jpg");

					ImageData imageData = ImageDataFactory.create(path);
					com.itextpdf.layout.element.Image pdfImg = new com.itextpdf.layout.element.Image(imageData);

					pdfImg.scaleAbsolute(595, 842);
					document.add(pdfImg);

					// if (paisagem[i]) {
					// imPDF.setRotationDegrees(-90);
					// // imPDF.scaleAbsolute(840,595);
					// }
					// else
					// imPDF.scaleAbsolute(595, 842);

					// imPDF.scaleAbsolute(document.getPageSize().getHeight(),
					// document.getPageSize().getWidth());

					// document.add(imPDF);

					File fdel = new File(files[i].getAbsolutePath() + ".jpg");
					fdel.delete();

				}
				document.close();
				pdf.close();
				break;

			} catch (Exception e) {
				jLabelMessage.setText("Não foi possível gerar o arquivo!");
				return;
			}
		}
		jLabelMessage.setText("Arquivo Gerado com sucesso!");
	}

	private JCheckBox getJCheckBoxImageList() {

		chkImageList = new JCheckBox();
		chkImageList.setText("Mostrar Imagens na Lista");

		chkImageList.setLocation(5, 530);
		chkImageList.setSize(220, 22);

		chkImageList.setMargin(new Insets(0, 0, 0, 0));
		chkImageList.setVisible(false);

		chkImageList.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				updateListFiles();
			}
		});

		return chkImageList;

	}

	private JSpinner getMaxSizeFileOutput() {

		maxSizeFileOutput = new JSpinner();

		maxSizeFileOutput.setLocation(485, 30);
		maxSizeFileOutput.setSize(100, 22);
		//DefaultEditor edit = (DefaultEditor) maxSizeFileOutput.getEditor();
		//edit.getTextField().setEditable(false);
		//maxSizeFileOutput.setModel(new SpinnerNumberModel(0, 0, 0, 0));

		labelMax1 = new JLabel("TODO: Limitar tamanho do PDF em MB");
		labelMax1.setFont(new Font("Dialog", Font.BOLD, 11));
		labelMax1.setLocation(590, 23);		
		labelMax1.setSize(400, 22);
		pData.add(labelMax1, null);


		labelMax2 = new JLabel("(deixe zero para sem limite de tamanho)");
		labelMax2.setFont(new Font("Dialog", Font.ITALIC, 11));
		labelMax2.setLocation(590, 35);		
		labelMax2.setSize(400, 22);		
		pData.add(labelMax2, null);

		return maxSizeFileOutput;

	}

	private JScrollPane getJPanelListFiles() {

		panelListFiles = new JScrollPane(getJListFiles());

		panelListFiles.setLocation(7, 35);
		panelListFiles.setSize(220, pData.getHeight() - 75);

		panelListFiles.setBackground(new Color(255, 255, 255));

		return panelListFiles;

	}

	private JList getJListFiles() {

		listFiles = new JList();
		listFiles.setSize(220, pData.getHeight() - 75);
		listFiles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listFiles.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				renderImageSelected();
			}
		});

		return listFiles;
	}
	private void renderImageSelected() {
		int[] _idxFilesSelecteds = listFiles.getSelectedIndices();

		if (_idxFilesSelecteds.length == 1) {

			panelImage.removeAll();

			BufferedImage ii = getBufferedImageByIndice(_idxFilesSelecteds[0]);

			Graphics2D g = (Graphics2D) panelImage.getGraphics();
			g.setColor(new Color(200, 200, 200));
			g.fillRect(1, 1, panelImage.getWidth() - 3 , panelImage.getHeight() - 2);
			if (ii == null) {
				btnGirarImagem.setVisible(false);
				maxSizeFileOutput.setVisible(false);
				labelMax1.setVisible(false);
				labelMax2.setVisible(false);
				return;
			}

			btnGirarImagem.setVisible(true);
			maxSizeFileOutput.setVisible(true);
			labelMax1.setVisible(true);
			labelMax2.setVisible(true);

			double wi = ii.getWidth();
			double hi = ii.getHeight();

			int w = ii.getWidth();
			int h = ii.getHeight();

			double wp = 0; 
			double hp = 0; 

			wp = panelImage.getWidth();
			hp = panelImage.getHeight();	

			double space = 11;
			int drawLocationX = 0;
			int drawLocationY = 0;

			//image paixagem
			if (wi > hi) {
				//tela paixagem
				if (wp > hp) { // razão da tela é maior que da imagem
					if (hp/wp < hi/wi) {
						h = (int) (hp - space);
						w = (int)(h * ((double)(wi) / hi)) ;
						drawLocationX = (panelImage.getWidth() - w) / 2;
						drawLocationY = (int) (space / 2);
					} else {
						w = (int) (wp - space);
						h = (int)(w * ((double)(hi) / wi));
						drawLocationX = (int) (space / 2);
						drawLocationY = (panelImage.getHeight() - h) / 2;
					}
					//tela retrato
				} else {
					w = (int) (wp - space);
					h = (int)(w * ((double)(hi) / wi));
					drawLocationX = (int) (space / 2);
					drawLocationY = (panelImage.getHeight() - h) / 2;
				} 
				//imagem retrato
			} else {
				//tela paixagem
				if (wp > hp) {
					h = (int) (hp - space);
					w = (int)(h * ((double)(wi) / hi)) ;
					drawLocationX = (panelImage.getWidth() - w) / 2;
					drawLocationY = (int) (space / 2);
					//tela retrato
				} else {
					// razão da tela é maior que da imagem
					if (hp/wp > hi/wi) {
						w = (int) (wp - space);
						h = (int)(w * ((double)(hi) / wi)) ;
						drawLocationX = (int) (space / 2);
						drawLocationY = (panelImage.getHeight() - h) / 2;
					} else {
						h = (int) (hp - space);
						w = (int)(h * ((double)(wi) / hi)) ;

						drawLocationX = (panelImage.getWidth() - w) / 2;
						drawLocationY = (int) (space / 2);
					}
				} 
			}

			g.drawImage(ii, drawLocationX, drawLocationY, w, h ,null);
		}
	}

	private JButton getButtonSelectFile() {

		btnSelectFile = new JButton();
		btnSelectFile.setForeground(new Color(0, 0, 0));
		btnSelectFile.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnSelectFile.setText("Selecione JPGs ou PDFs");

		btnSelectFile.setLocation(7, 7);
		btnSelectFile.setSize(220, 20);
		btnSelectFile.setMargin(new Insets(0, 0, 0, 0));

		btnSelectFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jLabelMessage.setText("");
				actionBtnSelectFile();
			}
		});

		return btnSelectFile;
	}

	private JButton getButtonGirarImagemSelecionada() {

		btnGirarImagem = new JButton();
		btnGirarImagem.setForeground(new Color(0, 0, 0));
		btnGirarImagem.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnGirarImagem.setText("Girar Imagens Selecionadas");

		btnGirarImagem.setLocation(235, 30);
		btnGirarImagem.setSize(220, 20);
		btnGirarImagem.setMargin(new Insets(0, 0, 0, 0));

		btnGirarImagem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionBtnGiraIamgem();
			}
		});

		return btnGirarImagem;
	}

	protected BufferedImage getBufferedImageByIndice(int i) {
		BufferedImage src = images[i];
		if (src == null) {
			try {
				src = ImageIO.read(files[i]);
				images[i] = src;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return src;
	}

	protected BufferedImage getBufferedImageByPath(String path) {
		try {
			return ImageIO.read(new File(path));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private void actionBtnGiraIamgem() {

		int[] _idxFilesSelecteds = listFiles.getSelectedIndices();

		for (int i: _idxFilesSelecteds) {
			images[i] = null;
			BufferedImage src = getBufferedImageByIndice(i);

			if (src == null) {
				return;
			}

			rotates[i] = (rotates[i] + 1) % 4;

			BufferedImage dest = rotateBufferedImage(i, src);
			images[i] = dest;
		}
		renderImageSelected();

	}

	private BufferedImage rotateBufferedImage(int i, BufferedImage src) {
		int width = 0;
		int height = 0;

		BufferedImage dest = null;

		if (rotates[i] == 1 || rotates[i] == 3) {
			height = src.getWidth();
			width = src.getHeight();
		} else {
			width = src.getWidth();
			height = src.getHeight();
		}
		dest = new BufferedImage(width, height, src.getType());

		Graphics2D graphics2D = dest.createGraphics();
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		graphics2D.setRenderingHints(rh);

		if (rotates[i] == 1 || rotates[i]==3) {
			graphics2D.translate(rotates[i] == 1 ? width : 0, rotates[i] == 3 ? height : 0);
			width = 0;
			height = 0;
		}
		graphics2D.rotate(Math.toRadians(rotate[rotates[i]]), width / 2, height / 2);	


		graphics2D.drawRenderedImage(src, null);
		graphics2D.dispose();
		return dest;
	}

	protected void actionBtnSelectFile() {

		JFileChooser c = new JFileChooser();
		c.setMultiSelectionEnabled(true);
		c.setDialogTitle("Selecione as imagens a adicionar");

		File f = new File(ClassLoader.getSystemClassLoader().getResource(".").getFile());
		String ss = "";
		try {
			ss = f.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// c.setCurrentDirectory(new File("/home/leandro/Público/edital"));
		//jLabelMessage.setText(ss);

		int rVal = c.showOpenDialog(this);

		if (rVal == JFileChooser.APPROVE_OPTION) {

			files = c.getSelectedFiles();
			rotates = new int[files.length];

			images = new BufferedImage[files.length];		    

			btnGirarImagem.setVisible(true);
			maxSizeFileOutput.setVisible(true);
			labelMax1.setVisible(true);
			labelMax2.setVisible(true);
			for (int i = 0; i < files.length; i++) {
				images[i] = null;
				rotates[i] = 0;
				//images[i] = ImageIO.read(files[i]);
			}
			Runnable run = new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < files.length; i++) {
						if (getBufferedImageByIndice(i) == null) {
							btnGirarImagem.setVisible(false);
							maxSizeFileOutput.setVisible(false);
							labelMax1.setVisible(false);
							labelMax2.setVisible(false);
							break;
						}
					}
				}
			};

			Thread t = new Thread(run);
			t.start();

			updateListFiles();

		}
		if (rVal == JFileChooser.CANCEL_OPTION) {
			files = null;
		}
	}

	private void updateListFiles() {

		if (chkImageList.isSelected()) {

			pData.remove(panelListFiles);
			pData.add(getJPanelListFiles());

			/*
			 * listFiles.setListData(files); listFiles.setCellRenderer(new
			 * DefaultListCellRenderer() {
			 * 
			 * public Component getListCellRendererComponent(JList list, Object value, int
			 * index, boolean isSelected, boolean cellHasFocus) { // for default cell
			 * renderer behavior Component c = super.getListCellRendererComponent(list,
			 * value, index, isSelected, cellHasFocus); // set icon for cell image
			 * 
			 * BufferedImage bImg = (BufferedImage) value;
			 * 
			 * java.awt.Image img = new ImageIcon(bImg).getImage(); int w =
			 * img.getWidth(null); int h = img.getHeight(null); int ww = 0; int escala =
			 * 200;
			 * 
			 * if (h > w) { // Escala Referencia Vertical w = escala; h = (int) (escala *
			 * img.getHeight(null)) / img.getWidth(null); // ww = w; // w = (int)(ww *
			 * ((float)w / (float)h)); // h = ww; } else { // Escala Referencia paisagem w =
			 * escala; h = (int) ((escala * img.getHeight(null)) / img.getWidth(null)); // h
			 * = escala; // w = (int) (escala * img.getWidth(null)) / //
			 * img.getHeight(null); }
			 * 
			 * bImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			 * 
			 * Graphics2D g = bImg.createGraphics(); g.drawImage(img, 0, 0, w, h, null);
			 * 
			 * ((JLabel) c).setIcon(new ImageIcon(bImg));
			 * 
			 * // ((JLabel)c).setIcon(new ImageIcon((BufferedImage)value));
			 * 
			 * ((JLabel) c).setText(null); return c; }
			 * 
			 * });
			 */		} else {

				 pData.remove(panelListFiles);
				 pData.add(getJPanelListFiles());

				 listFiles.setModel(new ListModel<String>() {

					 @Override
					 public void removeListDataListener(ListDataListener arg0) {
						 // TODO Auto-generated method stub

					 }

					 @Override
					 public int getSize() {
						 // TODO Auto-generated method stub
						 if (files == null) {
							 return 0;
						 }
						 return files.length;
					 }

					 @Override
					 public String getElementAt(int arg0) {
						 return files[arg0].getName();
					 }

					 @Override
					 public void addListDataListener(ListDataListener arg0) {
						 // TODO Auto-generated method stub
					 }
				 });
			 }
	}

	private JLabel getLabelMessage() {
		jLabelMessage = new JLabel();
		jLabelMessage.setFont(new Font("Dialog", Font.BOLD, 14));
		jLabelMessage.setLocation(374, 0);
		jLabelMessage.setSize(1000, 30);
		return jLabelMessage;
	}
}
