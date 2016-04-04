package com.ev112.codeblack.simpleclient.test;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/*
 *
 * 
 * 
 * 
 * 
 */
public class TestJTable extends JFrame {

    private static final int MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    private JTable table;
    private JScrollPane pane; 

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                TestJTable test = new TestJTable();
                test.setVisible(true);
            }
        });
    }

    TestJTable() {
        super();
        initUI();
        addKeyBindings();
    }

    /*
     * 
     * 
     * 
     */
    private void initUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        String[] headers = new String[]{"apples", "bananas"};
        String[][] data = new String[][]{{"1", "2"}, {"4", "6"}};
        
        table = new JTable(data, headers);
        table.setCellSelectionEnabled(true);
    
        pane = new JScrollPane(table);
        
        this.add(pane);
        this.pack();
        this.setSize(new Dimension(300, 400));
    
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        
        resize();
    }

    private void resize() {
        for (int column = 0; column < table.getColumnCount(); column++)
        {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();
         
            for (int row = 0; row < table.getRowCount(); row++)
            {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component c = table.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);
         
                //  We've exceeded the maximum width, no need to check other rows
                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }
         
            tableColumn.setPreferredWidth( preferredWidth );
        }    
    	
    }
    
    
    
    /*
     * 
     * 
     * 
     */
    private void addKeyBindings() {

    	//root maps
//      InputMap im = this.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//      ActionMap am = this.getRootPane().getActionMap();
        InputMap im = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = table.getActionMap();
        
        //add custom action
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, MASK), "inc");
        am.put("inc", biggerFont());
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, MASK), "dec");
        am.put("dec", smallerFont());
    }

    /*
     * 
     * 
     * 
     */
    private AbstractAction biggerFont() {
        AbstractAction save = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // JOptionPane.showMessageDialog(TestJTable.this.table, "Action Triggered.");
                
                Font f = table.getFont();
                int size = f.getSize();
                table.setFont(new Font(f.getFontName(), Font.PLAIN, size+3));
                table.setRowHeight(size+3);
                resize();
                table.editingCanceled(null);
            }
        };
        return save;
    }

    private AbstractAction smallerFont() {
        AbstractAction save = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // JOptionPane.showMessageDialog(TestJTable.this.table, "Action Triggered.");
                
                Font f = table.getFont();
                int size = f.getSize();
                table.setFont(new Font(f.getFontName(), Font.PLAIN, size-3));
                table.setRowHeight(size-3);
                resize();
                table.editingCanceled(null);
            }
        };
        return save;
    }
}
