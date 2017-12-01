package org.vaadin.haijian.filegenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.data.Container;
import com.vaadin.ui.Table;

public class PdfFileBuilder extends FileBuilder {

	private static final long serialVersionUID = -4638530112076578469L;

	private Document document;
	private PdfPTable table;
	private static Font catFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
	private static Font subFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
	private static Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);

	private Rectangle orientation = PageSize.A4;

	/** Stores relative width for columns as percentage */
	private float[] relativeWidths;
	/** Stores custom column alignments */
	private int[] columnAlignemnts;

	private boolean withBorder;
	private int colNr;

	private Iterable<List<Object>> dataSupplier;

	public PdfFileBuilder(Container container) {
		super(container);
	}

	public PdfFileBuilder(Table table) {
		super(table);
	}

	public PdfFileBuilder(	Container container,
							Iterable<List<Object>> dataSupplier) {
		this(container);
		this.dataSupplier = dataSupplier;
	}

	@Override
	protected void buildHeader() {
		if (getHeader() != null) {
			Paragraph title = new Paragraph(getHeader(), catFont);
			title.add(new Paragraph(" "));
			title.setAlignment(Element.ALIGN_CENTER);
			try {
				document.add(title);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void buildColumnHeaderCell(String header) {
		PdfPCell cell = new PdfPCell(new Phrase(header, subFont));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		if (!withBorder) {
			cell.setBorder(Rectangle.NO_BORDER);
		}
		table.addCell(cell);
	}

	@Override
	protected void buildCell(Object modelValue, Object presentationValue) {
		int horizontalAlignment = 0;
		/*
		 * checks if an alignments for a specified column exists, defaults to left(0) if none was
		 * found
		 */
		if (columnAlignemnts != null) {
			horizontalAlignment = columnAlignemnts.length <= colNr - 1 ? 0 : columnAlignemnts[colNr - 1];
		}

		PdfPCell cell;
		if (modelValue == null) {
			cell = new PdfPCell(new Phrase(""));
		} else if (modelValue instanceof Calendar) {
			Calendar calendar = (Calendar) modelValue;
			cell = new PdfPCell(new Phrase(formatDate(calendar.getTime()), cellFont));
		} else if (modelValue instanceof Date) {
			cell = new PdfPCell(new Phrase(formatDate((Date) modelValue), cellFont));
		} else {
			cell = new PdfPCell(new Phrase(presentationValue.toString(), cellFont));
		}
		cell.setHorizontalAlignment(horizontalAlignment);

		if (!withBorder) {
			cell.setBorder(Rectangle.NO_BORDER);
		}
		table.addCell(cell);
	}

	@Override
	protected String getFileExtension() {
		return ".pdf";
	}

	@Override
	protected void writeToFile() {
		if (dataSupplier != null) {
			createRowsFromDataSupplier(dataSupplier);
			table.setComplete(true);
		} else {
			try {
				document.add(table);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		document.close();
	}

	@Override
	protected void onNewRow() {
		colNr = 0;
	}

	@Override
	protected void onNewCell() {
		colNr++;
	}

	public boolean isWithBorder() {
		return withBorder;
	}

	public void setWithBorder(boolean withBorder) {
		this.withBorder = withBorder;
	}

	@Override
	protected void resetContent() {
		document = new Document();
		document.setPageSize(orientation);
		table = new PdfPTable(getNumberofColumns());
		if (dataSupplier != null) {
			container.removeAllItems();
			table.setComplete(false);
		}
		table.setWidthPercentage(100);
		if (relativeWidths != null) {
			try {
				table.setWidths(relativeWidths);
			} catch (DocumentException e1) {
				throw new RuntimeException(e1.getMessage());
			}
		}
		try {
			PdfWriter.getInstance(document, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		colNr = 0;
		document.open();
	}

	/**
	 * @since 0.3
	 */
	public void setLandscape() {
		orientation = PageSize.A4.rotate();
	}

	/**
	 * @since 0.3
	 */
	public void setPortrait() {
		orientation = PageSize.A4;
	}

	public void setHorizonzalAlignments(int[] alignments) {
		this.columnAlignemnts = alignments;
	}

	public void setRelativeWidths(float[] widths) {
		this.relativeWidths = widths;
	}

	@Override
	public void setVisibleColumns(Object[] visibleColumns) {
		super.setVisibleColumns(visibleColumns);
	}

	@Override
	protected void buildFooter() {
		if (dataSupplier != null)
			try {
				document.add(table);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
	}

	private void createRowsFromDataSupplier(Iterable<List<Object>> dataSupplier) {
		for (List<Object> rowModels : dataSupplier) {
			addRows(rowModels);
		}
	}

	/**
	 * @param modelObjects
	 *            must have the same type as the container objects
	 */
	private void addRows(List<Object> modelObjects) {
		for (Object itemId : modelObjects) {
			addRowItem(itemId);
		}

		try {
			document.add(table);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	private void addRowItem(Object itemId) {
		container.removeAllItems();
		container.addItem(itemId);
		onNewRow();
		buildRow(itemId);
	}

	public void setDataSorce(Iterable<List<Object>> datasouce) {
		dataSupplier = datasouce;
	}

}
