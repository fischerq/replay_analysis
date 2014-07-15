package utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.*;

class SaveListener implements ActionListener{
	private ChartWindow window;
	
	public SaveListener(ChartWindow w){
		window = w;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		window.save();
	}
	
}

public class ChartWindow extends JFrame{
	private static int width = 640;
	private static int height = 480;
	
	private JFreeChart chart;
	private String title;
	
	public ChartWindow(String title, JFreeChart chart) {
        super(title);
        this.title = title;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        this.chart = chart;
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(width, height));
        this.add(chartPanel, BorderLayout.CENTER);
 
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        buttonPanel.add(saveButton);
        saveButton.addActionListener(new SaveListener(this));
        this.add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
	
	public void save(){
		try {
			ChartUtilities.saveChartAsPNG(new File(title+".png"), chart, width, height);
		} catch (IOException e) {}
	}
}
