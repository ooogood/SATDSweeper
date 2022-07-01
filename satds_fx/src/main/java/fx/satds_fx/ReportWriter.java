package fx.satds_fx;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class ReportWriter {
	/** page setting */
	private final PDRectangle pageSize = PDRectangle.A4;
	private final float pageH = pageSize.getHeight();
	private final float pageW = pageSize.getWidth();
	private final float margin = 50;
	/** text size and format */
	private final PDType1Font font = PDType1Font.TIMES_ROMAN;
	private final int titleFontSize = 24;
	private final int titleDateFontSize = 10;
	private final int headerFontSize = 18;
	private final int contentFontSize = 12;
	private final float lineSpacing = 6;
	// multiply by sizeFactor: turn page width value into Font width value
	private final float contentSizeFactor = (float)(1000.0 / contentFontSize);
	private final float titleSizeFactor = (float)(1000.0 / titleFontSize);
	private final float headerSizeFactor = (float)(1000.0 / headerFontSize);
	/** table setting */
	private final float colCommentW = 220;
	private final float colLocationW = 100;
	private final float colDateW = 80;
	private final float colPrioW = 30;
	private final float colEstTimeW = 50;
	private final int approxMaxCharComment;
	private final int approxMaxCharLocation;
	private final int approxMaxCharDate;
	private final int approxMaxCharEstTime;
	private final float colSpacing = 6;
	private final String exceedRep = "...";
	/** writing cursor */
	private float curX;
	private float curY;
	PDDocument doc;
	PDPageContentStream curPageStream;
	private int curSize;

	public ReportWriter() {
		// calculate maximum maximum char count with whitespace 
		// (assuming whitespace is the smallest char in all font)
		float contentSpaceWidth = font.getSpaceWidth() / contentSizeFactor;
		approxMaxCharComment = (int)( colCommentW / contentSpaceWidth );
		approxMaxCharLocation = (int)( colLocationW / contentSpaceWidth );
		approxMaxCharDate = (int)( colDateW / contentSpaceWidth );
		approxMaxCharEstTime = (int)( colEstTimeW / contentSpaceWidth );
	}

	public void write( String path ) throws IOException {
		doc = new PDDocument();
		newPage();

		curX = margin;
		curY = pageH - margin;

		// todo: maybe wrap adding title into a function
		// add title
		String title = "Self-Admitted Technical Debt Report";
		changeFont(titleFontSize);
		float titleWidth = font.getStringWidth(title) / titleSizeFactor;
		curX = (float)(( pageW - titleWidth ) * 0.5);
		writeSingleLine(title);
		// add title date
		changeFont(titleDateFontSize);
		SimpleDateFormat formatter= new SimpleDateFormat( "yyyy-MM-dd" );
		String titleDate = formatter.format( new Date( System.currentTimeMillis() ) );
		curX = (float)(( pageW + titleWidth ) * 0.5 + 10.0);
		writeSingleLine(titleDate);

		// spacing between title and content
		advanceHeight( 2 * lineSpacing );

		// start writing content
		// add header
		writeHeader( "Selected by keyword" );

		// TODO: read from model
		// *** test data *** //
		Comment cmt = new Comment( "// this is a comment hahahahahahahahahahahahahahahahahahahahahhaaha", "Main.java:L11", "01/01/1999" );
		cmt.getPriority().setValue('W');
		cmt.getEstimate().setText("2");
		// test data end //
		for( int i = 0; i < 50; ++i ) {
			writeTableSingleRow( cmt );
		}
		curPageStream.stroke();
		curPageStream.close();

		doc.save( path );
		doc.close();
	}

	protected void writeTableSingleRow( Comment cmt ) throws IOException {
		changeFont(contentFontSize);
		// new line for content
		curX = margin;
		advanceHeight( curSize + lineSpacing );
		// write cotent
		String trimedContent = fitStringIntoWidth(cmt.getContent(), colCommentW, approxMaxCharComment, font, contentSizeFactor );
		writeSingleLine(trimedContent);
		curX += ( colCommentW + colSpacing );
		// write location
		String trimedLocation = fitStringIntoWidth(cmt.getLocation(), colLocationW, approxMaxCharLocation, font, contentSizeFactor );
		writeSingleLine(trimedLocation);
		curX += ( colLocationW + colSpacing );
		// write date
		String trimedDate = fitStringIntoWidth(cmt.getDate(), colDateW, approxMaxCharDate, font, contentSizeFactor );
		writeSingleLine(trimedDate);
		curX += ( colDateW + colSpacing );
		// write priority (assume will not exceed width, it has only one char)
		writeSingleLine(String.valueOf( cmt.getPriority().getValue() ));
		curX += ( colPrioW + colSpacing );
		// write est. time
		String trimedEstTime = fitStringIntoWidth(cmt.getEstimate().getText() + " day(s)", colEstTimeW, approxMaxCharEstTime, font, contentSizeFactor );
		writeSingleLine(trimedEstTime);
	}

	protected String fitStringIntoWidth( String src, float w, int approxMax, PDType1Font fnt, float sizeFactor ) throws IOException {
		// turn width into StringWidth
		w *= sizeFactor;
		if( fnt.getStringWidth( src ) <= w ) return src;
		w -= fnt.getStringWidth( exceedRep );
		// assert: w should not be negative
		// find how much width has src[0:approxMax) exceed
		if( approxMax > src.length() ) approxMax = src.length();
		float exceedW = fnt.getStringWidth( src.substring( 0, approxMax ) ) - w;
		int i = approxMax - 1;
		StringBuilder sb = new StringBuilder();
		sb.append( src.charAt( i-- ) );
		while( fnt.getStringWidth( sb.toString() ) < exceedW ) {
			sb.append( src.charAt( i-- ) );
		}
		return src.substring( 0, i + 1 ) + exceedRep;
	}

	protected void advanceHeight( float by ) throws IOException {
		if( curY - by < margin ) {
			newPage();
			curY = pageH - margin;
		}
		else curY -= by;
	}
	
	protected void newPage() throws IOException {
		if( curPageStream != null ) {
			curPageStream.stroke();
			curPageStream.close();
		}
		PDPage curPage = new PDPage( pageSize );
		doc.addPage(curPage);
		curPageStream = new PDPageContentStream(doc, curPage);
		curPageStream.setStrokingColor(Color.DARK_GRAY);
		curPageStream.setLineWidth(1);
		changeFont( curSize );
	}

	protected void changeFont( int size ) throws IOException {
		// assuming all font in the report is the same
		curSize = size;
		curPageStream.setFont( font, curSize );
	}

	protected void writeSingleLine( String text )
	throws IOException {
		curPageStream.beginText();
		curPageStream.newLineAtOffset( curX, curY );
		curPageStream.showText( text );
		curPageStream.endText();
	}

	protected void writeHeader( String text ) throws IOException {
		changeFont(headerFontSize);
		// newline for header
		curX = margin;
		advanceHeight( curSize + lineSpacing );
		writeSingleLine( text );
	}

}
