package org.vaadin.haijian;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.vaadin.haijian.filegenerator.FileBuilder;

import com.vaadin.data.Container;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;

public abstract class Exporter extends Button implements StreamSource {

	private static final long serialVersionUID = 5803570823414976951L;

	private List<BeforeSaveListener> beforeSaveListeners = new ArrayList<>();
	protected FileBuilder fileBuilder;
	private FileDownloader fileDownloader;
	private Locale locale;
	private String dateFormatString;
	protected String downloadFileName;
	private Long cacheTime;

	public Exporter() {
		fileDownloader = new FileDownloader(new StreamResource(this, getDownloadFileName()));
		fileDownloader.extend(this);
	}

	public Exporter(Table table) {
		this();
		setTableToBeExported(table);
	}

	public Exporter(Container container,
					Object[] visibleColumns) {
		this();
		setCaption("Exporter");
		setContainerToBeExported(container);
		setVisibleColumns(visibleColumns);
	}

	public Exporter(Container container) {
		this(container, null);
	}

	public void setTableToBeExported(Table table) {
		fileBuilder = createFileBuilder(table);
		configureFileBuilderLocale();

		setVisibleColumns(table.getVisibleColumns());
		setHeader(table.getCaption());
		for (Object column : table.getVisibleColumns()) {
			String header = table.getColumnHeader(column);
			if (header != null) {
				setColumnHeader(column, header);
			}
		}
	}

	public void setContainerToBeExported(Container container) {
		fileBuilder = createFileBuilder(container);
		configureFileBuilderLocale();
	}

	protected void configureFileBuilderLocale() {
		if (locale != null) {
			fileBuilder.setLocale(locale);
		}
		if (dateFormatString != null) {
			fileBuilder.setDateFormat(dateFormatString);
		}
	}

	public void setVisibleColumns(Object[] visibleColumns) {
		fileBuilder.setVisibleColumns(visibleColumns);
	}

	public void setColumnHeader(Object propertyId, String header) {
		fileBuilder.setColumnHeader(propertyId, header);
	}

	public void setHeader(String header) {
		fileBuilder.setHeader(header);
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormatString = dateFormat;
	}

	protected abstract FileBuilder createFileBuilder(Container container);

	protected abstract FileBuilder createFileBuilder(Table table);

	protected abstract String getDownloadFileName();

	public void setDownloadFileName(String fileName) {
		downloadFileName = fileName;
		StreamResource streamResource = (StreamResource) fileDownloader.getFileDownloadResource();
		streamResource.setFilename(getDownloadFileName());
		if (cacheTime != null)
			streamResource.setCacheTime(cacheTime);
	}

	@Override
	public InputStream getStream() {
		for (BeforeSaveListener beforeSaveListener : beforeSaveListeners) {
			beforeSaveListener.beforeSave();
		}
		try {
			return new FileInputStream(fileBuilder.getFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @since 0.4
	 */
	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}

	/**
	 * @since 0.4
	 */
	public void addBeforeSaveListener(BeforeSaveListener beforeSaveListener) {
		this.beforeSaveListeners.add(beforeSaveListener);
	}

	/**
	 * @since 0.4
	 */
	public boolean removeBeforeSaveListener(BeforeSaveListener beforeSaveListener) {
		return this.beforeSaveListeners.remove(beforeSaveListener);
	}

	/**
	 * @since 0.4
	 */
	@FunctionalInterface
	public interface BeforeSaveListener {

		void beforeSave();
	}
}
