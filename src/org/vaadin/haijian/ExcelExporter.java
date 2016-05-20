package org.vaadin.haijian;

import java.util.HashMap;
import java.util.Map;

import org.vaadin.haijian.filegenerator.ExcelFileBuilder;
import org.vaadin.haijian.filegenerator.FileBuilder;

import com.vaadin.data.Container;
import com.vaadin.ui.Table;

public class ExcelExporter extends Exporter {

	private static final long serialVersionUID = -1981433789055280899L;

	private Map<Object, IColumnValueConverter> converters = new HashMap<Object, IColumnValueConverter>();
	private Map<Object, String> formatter = new HashMap<Object, String>();

    public ExcelExporter() {
        super();
    }

    public ExcelExporter(Table table) {
        super(table);
    }

    public ExcelExporter(Container container, Object[] visibleColumns) {
        super(container, visibleColumns);
    }

    public ExcelExporter(Container container) {
        super(container);
    }

    @Override
    protected FileBuilder createFileBuilder(Container container) {
        return new ExcelFileBuilder(container, converters, formatter);
    }
    
    @Override
    protected FileBuilder createFileBuilder(Table table) {
        return new ExcelFileBuilder(table, converters, formatter);
    }

    @Override
    protected String getDownloadFileName() {
    	if(downloadFileName == null){
    		return "exported-excel.xls";
        }
    	if(downloadFileName.endsWith(".xls")){
    		return downloadFileName;
    	}else{
    		return downloadFileName + ".xls";
    	}
    }

	public void setColumnValueConverter(Object propertyId, IColumnValueConverter converter) {
		converters.put(propertyId, converter);
	}

	public void setCellFormatter(Object propertyId, String format) {
		formatter.put(propertyId, format);
	}
}
