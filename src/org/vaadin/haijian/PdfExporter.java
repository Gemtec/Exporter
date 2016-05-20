package org.vaadin.haijian;

import org.vaadin.haijian.filegenerator.FileBuilder;
import org.vaadin.haijian.filegenerator.PdfFileBuilder;

import com.itextpdf.text.Element;
import com.vaadin.data.Container;
import com.vaadin.ui.Table;

public class PdfExporter extends Exporter {

	private static final long serialVersionUID = 8187412021337366390L;

	public static final int ALIGNMENT_LEFT = Element.ALIGN_LEFT;
	public static final int ALIGNMENT_CENTER = Element.ALIGN_CENTER;
	public static final int ALIGNMENT_RIGHT = Element.ALIGN_RIGHT;

    public PdfExporter() {
        super();
    }

    public PdfExporter(Table table) {
        super(table);
    }

    public PdfExporter(Container container, Object[] visibleColumns) {
        super(container, visibleColumns);
    }

    public PdfExporter(Container container) {
        super(container);
    }

    @Override
    protected FileBuilder createFileBuilder(Container container) {
        return new PdfFileBuilder(container);
    }
    
    @Override
    protected FileBuilder createFileBuilder(Table table) {
        return new PdfFileBuilder(table);
    }

    @Override
    protected String getDownloadFileName() {
    	if(downloadFileName == null){
    		return "exported-pdf.pdf";
        }
    	if(downloadFileName.endsWith(".pdf")){
    		return downloadFileName;
    	}else{
    		return downloadFileName + ".pdf";
    	}
    }

    public void setWithBorder(boolean withBorder) {
        ((PdfFileBuilder) fileBuilder).setWithBorder(withBorder);
    }
    
	public void setHorizonzalAlignments(int[] alignments) {
		((PdfFileBuilder) fileBuilder).setHorizonzalAlignments(alignments);
	}

	public void setRelativeWidths(float[] widths) {
		((PdfFileBuilder) fileBuilder).setRelativeWidths(widths);
	}
}
