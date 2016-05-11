package org.vaadin.haijian.filegenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.vaadin.haijian.IColumnValueConverter;

import com.vaadin.data.Container;

public class ExcelFileBuilder extends FileBuilder {

	private static final long serialVersionUID = -8005096215388515449L;

    private Workbook workbook;
    private Sheet sheet;
    private int rowNr;
    private int colNr;
    private Row row;
    private Cell cell;
    private CellStyle dateCellStyle;
    private CellStyle boldStyle;

	private Map<Object, IColumnValueConverter> converters;

	private Object[] visibleColumns;

	private Map<Object, String> formaters;

    public ExcelFileBuilder(Container container) {
        super(container);
    }

	public ExcelFileBuilder(Container container,
							Map<Object, IColumnValueConverter> converters) {
		super(container);

		if (visibleColumns == null)
			visibleColumns = container.getContainerPropertyIds().toArray();

		this.converters = converters;
	}

	public ExcelFileBuilder(Container container,
							Map<Object, IColumnValueConverter> converters,
							Map<Object, String> formaters) {
		this(container, converters);
		this.formaters = formaters;
	}

    public void setDateCellStyle(String style) {
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat(
                style));
    }

    @Override
    public String getFileExtension() {
        return ".xls";
    }

    @Override
    protected void writeToFile() {
        try {
            workbook.write(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewRow() {
        row = sheet.createRow(rowNr);
        rowNr++;
        colNr = 0;
    }

    @Override
    protected void onNewCell() {
        cell = row.createCell(colNr);

		String columnValueFormater = getCellFormaters().get(visibleColumns[colNr]);
		if (columnValueFormater != null) {
			DataFormat format = workbook.createDataFormat();
			CellStyle style = workbook.createCellStyle();
			short formatNumber = format.getFormat(columnValueFormater);
			System.out.println("Für format " + columnValueFormater + " wurde id " + formatNumber + " zurückgegeben");
			style.setDataFormat(formatNumber);
			cell.setCellStyle(style);
		}

        colNr++;
    }

    @Override
    protected void buildCell(Object value) {

		IColumnValueConverter columnValueConverter = getCellValueConverters().get(visibleColumns[colNr - 1]);
		if (columnValueConverter != null)
			value = columnValueConverter.convert(value);

        if (value == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
            cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
        } else if (value instanceof Date) {
            cell.setCellValue(formatDate((Date) value));
            cell.setCellType(Cell.CELL_TYPE_STRING);
        } else if (value instanceof Calendar) {
        	Calendar calendar = (Calendar) value;
        	cell.setCellValue(calendar.getTime());
            cell.setCellType(Cell.CELL_TYPE_STRING);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        } else {
            cell.setCellValue(value.toString());
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
    }

    @Override
    protected void buildColumnHeaderCell(String header) {
        buildCell(header);
        cell.setCellStyle(getBoldStyle());
    }

    public CellStyle getDateCellStyle() {
        if (dateCellStyle == null) {
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(createHelper.createDataFormat()
                    .getFormat(getDateFormatString()));
        }
        return dateCellStyle;
    }

    private CellStyle getBoldStyle() {
        if (boldStyle == null) {
            Font bold = workbook.createFont();
            bold.setBoldweight(Font.BOLDWEIGHT_BOLD);

            boldStyle = workbook.createCellStyle();
            boldStyle.setFont(bold);
        }
        return boldStyle;
    }

    @Override
    protected void buildHeader() {
        if (getHeader() == null) {
            return;
        }
        onNewRow();
        onNewCell();
        cell.setCellValue(getHeader());

        Font headerFont = workbook.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setFontHeightInPoints((short) 15);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cell.setCellStyle(headerStyle);

        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0,
                getNumberofColumns() - 1));
        onNewRow();
    }

    @Override
    protected void buildFooter() {
        for (int i = 0; i < getNumberofColumns(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    @Override
    protected void resetContent() {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet();
        colNr = 0;
        rowNr = 0;
        row = null;
        cell = null;
        dateCellStyle = null;
        boldStyle = null;
    }

	protected Map<Object, IColumnValueConverter> getCellValueConverters() {
		return converters;
	}

	protected Map<Object, String> getCellFormaters() {
		return formaters;
	}

	@Override
	public void setVisibleColumns(Object[] visibleColumns) {
		super.setVisibleColumns(visibleColumns);
		this.visibleColumns = visibleColumns;
	}
}
