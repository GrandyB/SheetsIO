/**
 * HttpTemplater.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2021.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package application.services.http;

import application.models.CellWrapper;

/**
 * Builder mechanism for the Html templating of various file types.
 *
 * @author Mark "Grandy" Bishop
 */
public class HtmlResponseBuilder {

	static String CORE_TEMPLATE = "<html>" //
			+ "	<head>" //
			+ "		<script type=\"text/javascript\" src=\"http://livejs.com/live.js\"></script>" //
			+ "		<style>body, iframe { margin: 0; padding: 0; background-color: rgba(0, 0, 0, 0); overflow: hidden; } [scale] </style>" //
			+ "	</head>" //
			+ "	<body>" //
			+ "		<content></content>" //
			+ " </body>" //
			+ "</html>";
	static String SCALE_CSS = "#content { width: 100%; height: 100%; }";
	static String IFRAME_TEMPLATE = "<iframe src=\"%s\" id=\"content\" frameborder=\"0\"></iframe>";
	static String IMG_TEMPLATE = "<img src=\"%s\" id=\"content\" />";
	static String DIV_TEMPLATE = "<div id=\"content\"><span id=\"text\">%s</span></div>";
	static String VIDEO_TEMPLATE = "<video width=\"100%\" height=\"100%\" autoplay [loop]><source src=\"[src]\" type=\"[type]\"></video>";

	static String EMPTY_IMG_SRC = "data:image/gif;base64,R0lGODlhAQABAIAAAP///wAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";

	private String innerContent;
	private boolean scale = true;
	private boolean loop = false;

	public String build() {
		return CORE_TEMPLATE.replace("<content></content>", innerContent) //
				// Global parameters
				.replace("[scale]", scale ? SCALE_CSS : "") //
				.replace("[loop]", loop ? "loop" : "");
	}

	/**
	 * @param url
	 *            e.g. 'https://obs.ninja'
	 * @return a html doc with an <iframe> tag for a given src url
	 */
	public HtmlResponseBuilder buildIframeTemplate(String url) {
		this.innerContent = String.format(IFRAME_TEMPLATE, url);
		return this;
	}

	/**
	 * Build in an img tag with a src. If this image is a local file url, we instead
	 * just use the filename as the src; this way, the browser will send a file GET
	 * request, which is handled by {@link HttpService#handleFileGetRequest} and
	 * serves it from disk.
	 * 
	 * @param cell
	 *            The {@link CellWrapper} that this request is for
	 * @param url
	 *            e.g. 'https://i.imgur.com/asd123.png'
	 * @return a html doc with an <img> tag for a given src url
	 */
	public HtmlResponseBuilder buildImgTemplate(CellWrapper cell, String url) {
		String src = getSrc(cell, url, EMPTY_IMG_SRC);
		this.innerContent = String.format(IMG_TEMPLATE, src);
		return this;
	}

	/**
	 * @param text
	 *            e.g. 'hello there'
	 * @return a html doc with a <div> tag for a given src url
	 */
	public HtmlResponseBuilder buildDivTemplate(String text) {
		this.innerContent = String.format(DIV_TEMPLATE, text);
		return this;
	}

	/**
	 * @param url
	 *            e.g. '/path/to/dir/files/AoE2/thing.webm'
	 * @param type
	 *            e.g. 'video/webm'
	 * @return a html doc with a <video> tag for a given src url and content-type
	 */
	public HtmlResponseBuilder buildVideoTemplate(CellWrapper cell, String url) {
		String src = getSrc(cell, url, "");
		this.innerContent = VIDEO_TEMPLATE //
				.replace("[src]", src) //
				.replace("[type]", cell.getFileExtension().getContentType());
		return this;
	}

	/** @return an empty page, no value. */
	public HtmlResponseBuilder empty() {
		this.innerContent = "";
		return this;
	}

	/**
	 * @return a builder that either scales it content to 100% w/h, or doesn't try.
	 */
	public HtmlResponseBuilder scale(boolean scale) {
		this.scale = scale;
		return this;
	}

	/**
	 * @return a builder that either loops its content or not (for video).
	 */
	public HtmlResponseBuilder loop(boolean loop) {
		this.loop = loop;
		return this;
	}

	private static String getSrc(CellWrapper cell, String url, String emptyDefault) {
		String src = emptyDefault;
		boolean isForLocalFile = url.contains("file://");
		if (isForLocalFile) {
			// Use the file name as the src, resulting in a file GET request when served
			src = cell.getName() + "." + cell.getFileExtension().getExtension();
		} else if (url != null && !url.trim().isEmpty()) {
			src = url;
		}
		return src;
	}
}
