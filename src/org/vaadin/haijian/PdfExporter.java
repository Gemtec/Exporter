package org.vaadin.haijian;

import java.util.List;

import org.vaadin.haijian.filegenerator.FileBuilder;
import org.vaadin.haijian.filegenerator.PdfFileBuilder;

import com.itextpdf.text.Element;
import com.vaadin.data.Container;
import com.vaadin.ui.Table;

public class PdfExporter extends Exporter {

	private static final long serialVersionUID = 8187412021337366390L;
	private Iterable<List<Object>> datasouce;

	public PdfExporter() {
		super();
	}

	public PdfExporter(Iterable<List<Object>> datasouce) {
		super();
		this.datasouce = datasouce;
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
		return new PdfFileBuilder(container, datasouce);
	}

	@Override
	protected FileBuilder createFileBuilder(Table table) {
		return new PdfFileBuilder(table);
	}

	@Override
	protected String getDownloadFileName() {
		if (downloadFileName == null) {
			return "exported-pdf.pdf";
		}
		if (downloadFileName.endsWith(".pdf")) {
			return downloadFileName;
		} else {
			return downloadFileName + ".pdf";
		}
	}

	public void setWithBorder(boolean withBorder) {
		((PdfFileBuilder) fileBuilder).setWithBorder(withBorder);
	}

	public void setHorizonzalAlignments(HorizontalAlignment[] alignments) {
		int[] aligns = new int[alignments.length];
		for (int i = 0; i < alignments.length; i++) {
			aligns[i] = alignments[i].getAlignment();
		}
		((PdfFileBuilder) fileBuilder).setHorizonzalAlignments(aligns);
	}

	public void setRelativeWidths(float[] widths) {
		((PdfFileBuilder) fileBuilder).setRelativeWidths(widths);
	}

	public void setDataSource(Iterable<List<Object>> datasouce) {
		((PdfFileBuilder) fileBuilder).setDataSorce(datasouce);
	}

	/**
	 * @since 0.2
	 */
	public void setOrientationPortrait() {
		((PdfFileBuilder) fileBuilder).setPortrait();
	}

	/**
	 * @since 0.2
	 */
	public void setOrientationLandscape() {
		((PdfFileBuilder) fileBuilder).setLandscape();
	}

	/**
	 * Specifies if the column headers should be displayed on every page.
	 * 
	 * @param columHeadersOnEveryPage
	 *            If {@code true}, the headers of each column will be displayed on every page.
	 * @since 0.2
	 */
	public void setColumnHeadersOnEveryPage(boolean columHeadersOnEveryPage) {
		((PdfFileBuilder) fileBuilder).setColumnHeadersOnEveryPage(columHeadersOnEveryPage);
	}

	public enum HorizontalAlignment {

		LEFT(Element.ALIGN_LEFT),
		CENTER(Element.ALIGN_CENTER),
		RIGHT(Element.ALIGN_RIGHT);

		HorizontalAlignment(int alignment) {
			this.alignment = alignment;
		}

		private int alignment;

		public int getAlignment() {
			return alignment;
		}

	}
}
